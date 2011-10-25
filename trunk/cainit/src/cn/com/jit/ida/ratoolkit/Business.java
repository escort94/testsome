package cn.com.jit.ida.ratoolkit;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.service.request.CertReqDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertReqDownResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class Business
{
  private ConnConfig config;
  private Socket socket;
  private DataInputStream dis;
  private DataOutputStream dos;
  private static final String IBMPROVIDER = "IBMPKCS11Impl";
  private static final String PKCS11 = "PKCS11";
  private static final String PKCS11IBM = "PKCS11IMPLKS";
  private static final String IBMPKCS11 = "com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl";
  private static final String SUNPKCS11 = "sun.security.pkcs11.SunPKCS11";
  private static SSLSocketFactory sf = null;
  private static final boolean isDdebug = false;
  private static SSLContext sslContext;
  private String sunProvider = "SunPKCS11-SJYPROVIDER";

  public Business(ConnConfig paramConnConfig)
  {
    this.config = paramConnConfig;
    this.socket = null;
    if (paramConnConfig.getSjyName() != null)
      this.sunProvider = paramConnConfig.getSjyName();
  }

  public Response doBusiness(Request paramRequest)
    throws Exception
  {
    byte[] arrayOfByte1 = paramRequest.getData();
    connect();
    byte[] arrayOfByte2 = request(arrayOfByte1);
    disConnect();
    Response localResponse = new Response();
    localResponse.setData(arrayOfByte2);
    return localResponse;
  }

  private void connect()
    throws Exception
  {
    if (this.config == null)
      throw new Exception("ConnConfig must not be null.");
    if (sf == null)
    {
      localObject1 = this.config.getDeviceID();
      if ((localObject1 == null) || (((String)localObject1).equals("")))
        throw new Exception("DeviceID must not be null!");
      String str1;
      if (((String)localObject1).equals("JSOFT_LIB"))
      {
        str1 = System.getProperty("java.vm.version").substring(0, 3);
        float f = Float.parseFloat(str1);
        if (f <= 1.3D)
        {
          System.setProperty("javax.net.ssl.trustStore", this.config.getUserKeyPath());
          System.setProperty("javax.net.ssl.keyStore", this.config.getUserKeyPath());
          System.setProperty("javax.net.ssl.keyStorePassword", new String(this.config.getUserKeyPassword()));
          sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        else
        {
          sf = getSSLSocketFactory();
        }
        debug("--------------- Connection Properties -------------");
        debug("server ip=" + this.config.getServerIP());
        debug("server port=" + this.config.getServerPort());
        debug("trust store=" + this.config.getUserKeyPath());
        debug("user key store=" + this.config.getUserKeyPath());
        debug("user key password=" + new String(this.config.getUserKeyPassword()));
        debug("---------------------------------------------------\n");
        debug("to prepare connect to server ......");
      }
      else if (((String)localObject1).equals("JSJY05B_LIB"))
      {
        str1 = System.getProperty("java.vm.vendor");
        String str2 = this.config.getCfgName();
        Object localObject2;
        Object localObject3;
        Class localClass1;
        Object localObject4;
        Provider localProvider;
        KeyStore localKeyStore;
        if (str1.toUpperCase().indexOf("SUN") != -1)
        {
          System.setProperty("javax.net.ssl.keyStoreType", "PKCS11");
          System.setProperty("javax.net.ssl.trustStoreType", "PKCS11");
          System.setProperty("javax.net.ssl.keyStore", "NONE");
          System.setProperty("javax.net.ssl.trustStore", "NONE");
          System.setProperty("javax.net.ssl.keyStoreProvider", this.sunProvider);
          System.setProperty("javax.net.ssl.trustStoreProvider", this.sunProvider);
          System.setProperty("javax.net.ssl.keyStorePassword", new String(this.config.getUserKeyPassword()));
          debug("--------------- Connection Properties -------------");
          debug("keyStoreType=PKCS11");
          debug("trustStoreType=PKCS11");
          debug("keyStore=NONE");
          debug("trustStore=NONE");
          debug("keyStoreProvider=" + this.sunProvider);
          debug("user key password=" + new String(this.config.getUserKeyPassword()));
          debug("---------------------------------------------------\n");
          debug("to prepare connect to server ......");
          localObject2 = new Class[] { String.class };
          localObject3 = new Object[] { str2 };
          try
          {
            localClass1 = Class.forName("sun.security.pkcs11.SunPKCS11");
            localObject4 = localClass1.getConstructor(localObject2);
            localProvider = (Provider)createObject((Constructor)localObject4, localObject3);
            Security.addProvider(localProvider);
            char[] arrayOfChar1 = this.config.getUserKeyPassword();
            localKeyStore = KeyStore.getInstance("PKCS11", localProvider);
            localKeyStore.load(null, arrayOfChar1);
            sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
          }
          catch (ClassNotFoundException localClassNotFoundException2)
          {
            throw new Exception("sun.security.pkcs11.SunPKCS11 was not found .");
          }
          catch (NoSuchMethodException localNoSuchMethodException1)
          {
            throw new Exception("Method was not found .");
          }
        }
        else if (str1.toUpperCase().indexOf("IBM") != -1)
        {
          if (sslContext == null)
            try
            {
              debug("--------------- Connection Properties -------------");
              debug("keyStoreType=PKCS11IMPLKS");
              debug("trustStoreType=JKS");
              debug("keyStore=IBMPKCS11Impl");
              debug("trustStore=NONE");
              debug("keyStoreProvider=IBMPKCS11Impl");
              debug("user key password=" + new String(this.config.getUserKeyPassword()));
              debug("---------------------------------------------------\n");
              debug("to prepare connect to server ......");
              localClass1 = null;
              if (Security.getProvider("IBMPKCS11Impl") == null)
              {
                localClass1 = Class.forName("com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl");
                localObject2 = Preferences.userNodeForPackage(localClass1);
                ((Preferences)localObject2).put("IBMPKCSImpl DLL", this.config.getHardDriver());
                ((Preferences)localObject2).put("IBMPKCSImpl password", new String(this.config.getUserKeyPassword()));
                localObject3 = (Provider)localClass1.newInstance();
                Security.addProvider((Provider)localObject3);
                ((Preferences)localObject2).remove("IBMPKCSImpl DLL");
                ((Preferences)localObject2).remove("IBMPKCSImpl password");
              }
              if (Security.getProvider("IBMJSSE2") == null)
              {
                localObject2 = Class.forName("com.ibm.jsse2.IBMJSSEProvider2");
                localObject3 = (Provider)((Class)localObject2).newInstance();
                Security.addProvider((Provider)localObject3);
              }
              localObject2 = KeyStore.getInstance("PKCS11IMPLKS");
              ((KeyStore)localObject2).load(null, this.config.getUserKeyPassword());
              localObject3 = KeyManagerFactory.getInstance("IBMX509", "IBMJSSE2");
              ((KeyManagerFactory)localObject3).init((KeyStore)localObject2, this.config.getUserKeyPassword());
              localObject4 = TrustManagerFactory.getInstance("IbmX509", "IBMJSSE2");
              ((TrustManagerFactory)localObject4).init((KeyStore)localObject2);
              sslContext = SSLContext.getInstance("SSL", "IBMJSSE2");
              sslContext.init(((KeyManagerFactory)localObject3).getKeyManagers(), ((TrustManagerFactory)localObject4).getTrustManagers(), null);
            }
            catch (ClassNotFoundException localClassNotFoundException1)
            {
              throw new Exception("com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl was not found .");
            }
          sf = sslContext.getSocketFactory();
        }
        else if (str1.toUpperCase().indexOf("HEWLETT") != -1)
        {
          System.setProperty("javax.net.ssl.keyStoreType", "PKCS11");
          System.setProperty("javax.net.ssl.trustStoreType", "PKCS11");
          System.setProperty("javax.net.ssl.keyStore", "NONE");
          System.setProperty("javax.net.ssl.trustStore", "NONE");
          System.setProperty("javax.net.ssl.keyStoreProvider", this.sunProvider);
          System.setProperty("javax.net.ssl.trustStoreProvider", this.sunProvider);
          System.setProperty("javax.net.ssl.keyStorePassword", new String(this.config.getUserKeyPassword()));
          debug("--------------- Connection Properties -------------");
          debug("keyStoreType=PKCS11");
          debug("trustStoreType=PKCS11");
          debug("keyStore=NONE");
          debug("trustStore=NONE");
          debug("keyStoreProvider=" + this.sunProvider);
          debug("user key password=" + new String(this.config.getUserKeyPassword()));
          debug("---------------------------------------------------\n");
          debug("to prepare connect to server ......");
          localObject2 = new Class[] { String.class };
          localObject3 = new Object[] { str2 };
          try
          {
            Class localClass2 = Class.forName("sun.security.pkcs11.SunPKCS11");
            localObject4 = localClass2.getConstructor(localObject2);
            localProvider = (Provider)createObject((Constructor)localObject4, localObject3);
            Security.addProvider(localProvider);
            char[] arrayOfChar2 = this.config.getUserKeyPassword();
            localKeyStore = KeyStore.getInstance("PKCS11", localProvider);
            localKeyStore.load(null, arrayOfChar2);
            sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
          }
          catch (ClassNotFoundException localClassNotFoundException3)
          {
            throw new Exception("sun.security.pkcs11.SunPKCS11 was not found .");
          }
          catch (NoSuchMethodException localNoSuchMethodException2)
          {
            throw new Exception("Method was not found .");
          }
        }
      }
      else
      {
        throw new Exception("Wrong DeviceID .");
      }
    }
    Object localObject1 = (SSLSocket)sf.createSocket(this.config.getServerIP(), this.config.getServerPort());
    ((SSLSocket)localObject1).setUseClientMode(true);
    this.socket = ((Socket)localObject1);
    debug("get SSL socket connection OK.");
  }

  private void disConnect()
    throws Exception
  {
    debug("to prepare disconnect to server ......");
    if (this.socket == null)
    {
      debug("disconnect to server OK.");
      return;
    }
    this.socket.close();
    this.dos.close();
    this.dis.close();
    debug("disconnect to server OK.");
  }

  private byte[] request(byte[] paramArrayOfByte)
    throws Exception
  {
    debug("to pepare send request data ......");
    debug("request data length=" + paramArrayOfByte.length);
    this.dos = new DataOutputStream(this.socket.getOutputStream());
    this.dis = new DataInputStream(this.socket.getInputStream());
    debug("get net IOStream OK.");
    this.dos.writeInt(paramArrayOfByte.length);
    this.dos.write(paramArrayOfByte);
    this.dos.flush();
    debug("\n------------------ Request Data -------------------");
    debug(new String(paramArrayOfByte));
    debug("---------------------------------------------------\n");
    debug("send data OK.");
    int i = this.dis.readInt();
    debug("response data length=" + i);
    if (i <= 0)
      throw new Exception("data length: " + i + " is inviable.");
    byte[] arrayOfByte1 = new byte[i];
    int j = this.dis.read(arrayOfByte1);
    if (j == -1)
      throw new Exception("read data content error.");
    while (j < i)
    {
      byte[] arrayOfByte2 = new byte[i - j];
      int k = this.dis.read(arrayOfByte2);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, j, k);
      j += k;
    }
    debug("receive data OK.");
    debug("\n------------------ Response Data -------------------");
    debug(new String(arrayOfByte1));
    debug("---------------------------------------------------\n");
    return arrayOfByte1;
  }

  private void debug(String paramString)
  {
  }

  public static Object createObject(Constructor paramConstructor, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject = null;
    try
    {
      localObject = paramConstructor.newInstance(paramArrayOfObject);
      return localObject;
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new Exception("construct security provider of PKCS11 error");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Exception("construct security provider of PKCS11 error");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new Exception("construct security provider of PKCS11 error");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
    }
    throw new Exception("construct security provider of PKCS11 error");
  }

  public static void setSslContext(SSLContext paramSSLContext)
  {
    sslContext = paramSSLContext;
  }

  public static SSLContext getSslContext()
  {
    return sslContext;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      ConnConfig localConnConfig = new ConnConfig();
      localConnConfig.setServer("192.168.9.146", 40623);
      localConnConfig.setUserKeyFile_JKS(null, "11111111".toCharArray());
      localConnConfig.setDeviceID("JSJY05B_LIB");
      localConnConfig.setHardDriver("C:\\WINDOWS\\system32\\dapkcs11.dll:1");
      Business localBusiness = new Business(localConnConfig);
      CertReqDownRequest localCertReqDownRequest = new CertReqDownRequest();
      localCertReqDownRequest.setCertDN("cn=test1,ou=lj,c=cn");
      localCertReqDownRequest.setCtmlName("IDA管理员证书模板");
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      localCertReqDownRequest.setNotBefore(localSimpleDateFormat.format(new Date()));
      localCertReqDownRequest.setValidity("360");
      localCertReqDownRequest.setEmail("jian_li@jit.com.cn");
      localCertReqDownRequest.setRemark("remark");
      localCertReqDownRequest.setP10("MIIBXDCBxgIBADAfMQswCQYDVQQGEwJjbjEQMA4GA1UEAxMHTUQyX1JTQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAkx/w03Le3vFFvv+Jf2rafOq/IAHrwwRG4JueAtEL3xAK7K9sosDlEt3274R9B0FeTenq6zMLqsHJ7Uxk930AfIP5vNKxe0c+TIvAo292IngNyeRa3ycvBnva6iBW2EsMovpU74iTEMBqGksB5NXpJYXvAKimsMwVlIjeNmk8xD0CAwEAATANBgkqhkiG9w0BAQIFAAOBgQBfVwu6jITbBgY5A/Bg44wAkErUKlvYi0LpHQ3IlB8GTh0lwjj7Ih3yn57jkX8TUmJv63qIhfQeFfZMa7e1ehI5obbEKhxf0y8OiEq9+TptvT0W1m+AnPYqWkOMjIEwknalanEAIIHUVg9F8pqEjBV/cXAuIeEvUNen0hE3sFzZmA==");
      Response localResponse = localBusiness.doBusiness(localCertReqDownRequest);
      CertReqDownResponse localCertReqDownResponse = new CertReqDownResponse(localResponse);
      System.out.println("errorCode=" + localCertReqDownResponse.getErr());
      System.out.println("message=" + localCertReqDownResponse.getMsg());
      System.out.println("p7b=" + localCertReqDownResponse.getP7b());
      int i = 0;
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }

  protected KeyManager[] getKeyManagers()
    throws IOException, GeneralSecurityException
  {
    String str = KeyManagerFactory.getDefaultAlgorithm();
    KeyManagerFactory localKeyManagerFactory = KeyManagerFactory.getInstance(str);
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(this.config.getUserKeyPath());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
    }
    catch (Exception localException)
    {
    }
    KeyStore localKeyStore = KeyStore.getInstance("jks");
    localKeyStore.load(localFileInputStream, this.config.getUserKeyPassword());
    localFileInputStream.close();
    localKeyManagerFactory.init(localKeyStore, this.config.getUserKeyPassword());
    KeyManager[] arrayOfKeyManager = localKeyManagerFactory.getKeyManagers();
    return arrayOfKeyManager;
  }

  private TrustManager[] getTrustManagers()
    throws IOException, GeneralSecurityException
  {
    String str = TrustManagerFactory.getDefaultAlgorithm();
    TrustManagerFactory localTrustManagerFactory = TrustManagerFactory.getInstance(str);
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(this.config.getUserKeyPath());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new FileNotFoundException(localFileNotFoundException.getMessage());
    }
    catch (Exception localException)
    {
    }
    KeyStore localKeyStore = KeyStore.getInstance("jks");
    localKeyStore.load(localFileInputStream, this.config.getUserKeyPassword());
    localFileInputStream.close();
    localTrustManagerFactory.init(localKeyStore);
    TrustManager[] arrayOfTrustManager = localTrustManagerFactory.getTrustManagers();
    return arrayOfTrustManager;
  }

  private SSLSocketFactory getSSLSocketFactory()
    throws IOException, GeneralSecurityException
  {
    TrustManager[] arrayOfTrustManager = getTrustManagers();
    KeyManager[] arrayOfKeyManager = getKeyManagers();
    SSLContext localSSLContext = SSLContext.getInstance("SSL");
    localSSLContext.init(arrayOfKeyManager, arrayOfTrustManager, null);
    SSLSocketFactory localSSLSocketFactory = localSSLContext.getSocketFactory();
    return localSSLSocketFactory;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ratoolkit.Business
 * JD-Core Version:    0.6.0
 */