package cn.com.jit.ida.ca.issue.opt;

import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.ca.issue.entity.CRLEntity;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class FileStore
  implements Store
{
  private String DEFAULT_CERT_ADDRESS = "./cert/";
  private String DEFAULT_CRL_ADDRESS = "./crl/";
  private String certPubAddress = null;
  private String crlPubAddress = null;

  public String getCertPubAddress()
  {
    return this.certPubAddress;
  }

  public void setCertPubAddress(String paramString)
  {
    if ((paramString != null) && (!paramString.trim().equals("")))
      this.certPubAddress = paramString;
  }

  public String getCrlPubAddress()
  {
    return this.crlPubAddress;
  }

  public void setCrlPubAddress(String paramString)
  {
    this.crlPubAddress = paramString;
  }

  public FileStore()
  {
    try
    {
      CAConfig localCAConfig = CAConfig.getInstance();
      String[][] arrayOfString = localCAConfig.getCertPubAddress();
      if (arrayOfString != null)
        for (int i = 0; i < arrayOfString.length; i++)
        {
          localObject1 = arrayOfString[i][0];
          if (!((String)localObject1).equals("URI"))
            continue;
          String str = arrayOfString[i][1];
          this.certPubAddress = str;
          break;
        }
      CRLConfig localCRLConfig = CRLConfig.getInstance();
      Object localObject1 = localCRLConfig.getCRLPubAddressForService();
      if (localObject1 != null)
        for (int j = 0; j < localObject1.length; j++)
        {
          Object localObject2 = localObject1[j][0];
          if (!localObject2.equals("URI"))
            continue;
          Object localObject3 = localObject1[j][1];
          this.crlPubAddress = localObject3;
          break;
        }
      if ((this.certPubAddress != null) && (!this.certPubAddress.endsWith(File.separator)))
        this.certPubAddress += File.separator;
      if ((this.crlPubAddress != null) && (!this.crlPubAddress.endsWith(File.separator)))
        this.crlPubAddress += File.separator;
    }
    catch (Exception localException)
    {
    }
    finally
    {
      if (this.certPubAddress == null)
        this.certPubAddress = this.DEFAULT_CERT_ADDRESS;
      if (this.crlPubAddress == null)
        this.crlPubAddress = this.DEFAULT_CRL_ADDRESS;
    }
  }

  public void add(BaseEntity paramBaseEntity)
    throws ISSException
  {
    String str1 = paramBaseEntity.getEntityType();
    try
    {
      Object localObject1;
      Object localObject2;
      String str2;
      Object localObject3;
      Object localObject4;
      if (str1.equals("CACert"))
      {
        localObject1 = (CAEntity)paramBaseEntity;
        localObject2 = new File(this.certPubAddress);
        if (!((File)localObject2).exists())
          ((File)localObject2).mkdirs();
        str2 = getCNName(((CAEntity)localObject1).getCASubject()) + "_" + ((CAEntity)localObject1).getCACertSN();
        localObject3 = new File(this.certPubAddress + File.separator + str2 + ".cer");
        localObject4 = new FileOutputStream((File)localObject3);
        ((FileOutputStream)localObject4).write(((CAEntity)localObject1).getCACert());
        ((FileOutputStream)localObject4).close();
      }
      else
      {
        File localFile;
        FileOutputStream localFileOutputStream;
        if (str1.equals("Cert"))
        {
          localObject1 = (CertEntity)paramBaseEntity;
          localObject2 = ((CertEntity)localObject1).getCertType();
          if (localObject2 == null)
            throw new ISSException("8407", "证书类型不能为空");
          str2 = this.certPubAddress + (String)localObject2;
          localObject3 = new File(str2);
          if (!((File)localObject3).exists())
            ((File)localObject3).mkdirs();
          localObject4 = getCNName(((CertEntity)localObject1).getCertDN()) + "_" + ((CertEntity)localObject1).getCertSN();
          localFile = new File(str2 + File.separator + (String)localObject4 + ".cer");
          localFileOutputStream = new FileOutputStream(localFile);
          localFileOutputStream.write(((CertEntity)localObject1).getCertContent());
          localFileOutputStream.close();
        }
        else
        {
          localObject1 = (CRLEntity)paramBaseEntity;
          long l = ((CRLEntity)localObject1).getCdpid();
          localObject3 = this.crlPubAddress;
          localObject4 = new File((String)localObject3);
          if (!((File)localObject4).exists())
            ((File)localObject4).mkdirs();
          localFile = null;
          if (l != -1L)
            localFile = new File((String)localObject3 + File.separator + "crl" + l + ".crl");
          else
            localFile = new File((String)localObject3 + File.separator + "crl.crl");
          if (localFile.exists())
            localFile.delete();
          localFileOutputStream = new FileOutputStream(localFile);
          localFileOutputStream.write(((CRLEntity)localObject1).getCrlContent());
          localFileOutputStream.close();
        }
      }
    }
    catch (Exception localException)
    {
      throw new ISSException("8415", "文件发布失败:" + localException.toString(), localException);
    }
  }

  public void delete(BaseEntity paramBaseEntity)
    throws ISSException
  {
    throw new UnsupportedOperationException("Method delete() not yet implemented.");
  }

  private String getCNName(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str1 = localStringTokenizer.nextToken();
      if (str1.indexOf("=") == -1)
        continue;
      String str2 = str1.substring(0, str1.indexOf("=")).toUpperCase().trim();
      if (str2.equals("CN"))
        return str1.substring(str1.indexOf("=") + 1, str1.length());
    }
    return paramString;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      CertEntity localCertEntity = new CertEntity("FILE");
      FileInputStream localFileInputStream = new FileInputStream("c:/cert.cer");
      byte[] arrayOfByte = new byte[localFileInputStream.available()];
      localFileInputStream.read(arrayOfByte);
      localFileInputStream.close();
      localCertEntity.setCertSN("00C80917199FFC70CB4BD4F28C3C80BE83");
      localCertEntity.setCertType("X509Ctml1");
      localCertEntity.setCertDN("C=CN, CN=lijian");
      localCertEntity.setCertStatus("Use");
      localCertEntity.setCertContent(arrayOfByte);
      FileStore localFileStore = new FileStore();
      localFileStore.add(localCertEntity);
      CRLEntity localCRLEntity = new CRLEntity("FILE");
      localCRLEntity.setCdpid(1L);
      localCRLEntity.setCrlContent(arrayOfByte);
      localFileStore.add(localCRLEntity);
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.opt.FileStore
 * JD-Core Version:    0.6.0
 */