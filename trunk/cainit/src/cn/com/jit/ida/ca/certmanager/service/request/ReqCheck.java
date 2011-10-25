package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.util.pki.asn1.x509.X509Name;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class ReqCheck
{
  private static final String DNTITLE = "C,0,OU,T,CN,SN,L,ST,E,DC,UID";

  public static boolean checkDN(String paramString)
  {
    try
    {
      X509Name localX509Name = new X509Name(paramString);
    }
    catch (Exception localException)
    {
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str1 = localStringTokenizer.nextToken();
      if (str1.indexOf("=") == -1)
        return false;
      String str2 = str1.substring(0, str1.indexOf("=")).toUpperCase().trim();
      if ("C,0,OU,T,CN,SN,L,ST,E,DC,UID".indexOf(str2) == -1)
        return false;
      String str3 = str1.substring(str1.indexOf("=") + 1, str1.length());
      if (str3.trim().equals(""))
        return false;
      byte[] arrayOfByte = str3.getBytes();
      for (int i = 0; i < arrayOfByte.length; i++)
        if ((arrayOfByte[i] == 43) || (arrayOfByte[i] == 59) || (arrayOfByte[i] == 34) || (arrayOfByte[i] == 92) || (arrayOfByte[i] == 47) || (arrayOfByte[i] == 44) || (arrayOfByte[i] == 35))
          return false;
    }
    return true;
  }

  public static boolean checkNotBefore(String paramString)
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      return false;
    long l = 0L;
    try
    {
      l = Long.parseLong(paramString);
    }
    catch (Exception localException1)
    {
      return false;
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    try
    {
      localSimpleDateFormat.parse(paramString);
    }
    catch (Exception localException2)
    {
      return false;
    }
    return true;
  }

  public static boolean checkValidity(String paramString)
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      return false;
    try
    {
      int i = Integer.parseInt(paramString);
      if (i <= 0)
        return false;
    }
    catch (Exception localException)
    {
      return false;
    }
    return true;
  }

  public static String filterDN(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    Object localObject = null;
    String str = null;
    while (localStringTokenizer.hasMoreTokens())
    {
      str = localStringTokenizer.nextToken().trim();
      if (localObject == null)
      {
        localObject = str;
        continue;
      }
      localObject = (String)localObject + "," + str;
    }
    return (String)localObject;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.ReqCheck
 * JD-Core Version:    0.6.0
 */