package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import java.io.PrintStream;

public class CTMLServiceException extends IDAException
{
  private static final String[] errDesc = new String[2000];
  private static int counter = 0;
  private static int module = 0;
  private static final int OFFSET = 100;

  public CTMLServiceException(int paramInt)
  {
    super(convert(paramInt), getErrorDesc(paramInt));
  }

  public CTMLServiceException(int paramInt, String paramString)
  {
    super(convert(paramInt), paramString);
  }

  public CTMLServiceException(int paramInt, Exception paramException)
  {
    super(convert(paramInt), getErrorDesc(paramInt), paramException);
  }

  public CTMLServiceException(int paramInt, String paramString, Exception paramException)
  {
    super(convert(paramInt), paramString, paramException);
  }

  private static String getErrorDesc(int paramInt)
  {
    return errDesc[paramInt];
  }

  private static String convert(int paramInt)
  {
    return convert(paramInt, 4);
  }

  private static String convert(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[paramInt2];
    for (int i = 1; i <= paramInt2; i++)
    {
      arrayOfByte[(paramInt2 - i)] = (byte)(paramInt1 % 10 + 48);
      paramInt1 /= 10;
    }
    return new String(arrayOfByte);
  }

  public static void main(String[] paramArrayOfString)
  {
    new CTMLServiceException(0);
    for (int i = 0; i < 500; i++)
    {
      if (errDesc[i] == null)
        continue;
      System.out.println(i + ":" + errDesc[i]);
    }
  }

  static
  {
    new CHECKPARAM();
    new CHECKSTATUS();
    new CHECKSYSLIMIT();
    new REQUEST();
  }

  public static class REQUEST
  {
    public static final int PARSE_REQUESTXMLDOC;
    public static final int PARSE_REQUESTXMLDATA;

    static
    {
      CTMLServiceException.access$002(CTMLServiceException.access$108() * 100);
      PARSE_REQUESTXMLDOC = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "解析请求XML对象失败";
      PARSE_REQUESTXMLDATA = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "解析请求XML数据失败";
    }
  }

  public static class CHECKSYSLIMIT
  {
    public static final int ADMINCTML_CANTDELETE;
    public static final int ADMINCTML_CANTREVOKE;
    public static final int ADMINCTML_CANTMODIFY;
    public static final int COMMCTML_CANTDELETE;
    public static final int COMMCTML_CANTREVOKE;
    public static final int COMMCTML_CANTMODIFY;

    static
    {
      CTMLServiceException.access$002(CTMLServiceException.access$108() * 100);
      ADMINCTML_CANTDELETE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "管理员证书模板不能删除";
      ADMINCTML_CANTREVOKE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "管理员证书模板不能注销";
      ADMINCTML_CANTMODIFY = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "管理员证书模板不能修改";
      COMMCTML_CANTDELETE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "系统通信证书模板不能删除";
      COMMCTML_CANTREVOKE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "系统通信证书模板不能注销";
      COMMCTML_CANTMODIFY = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "系统通信证书模板不能修改";
    }
  }

  public static class CHECKSTATUS
  {
    public static final int CTMLSTATUS_NONDELETE;
    public static final int CTMLSTATUS_NONMODIFY;
    public static final int CTMLSTATUS_NONREVOKE;
    public static final int REVOKESELFEXT_BADSTATUS;
    public static final int DELETESELFEXT_BADSTATUS;
    public static final int MODIFYSELFEXT_BADSTATUS;

    static
    {
      CTMLServiceException.access$002(CTMLServiceException.access$108() * 100);
      CTMLSTATUS_NONDELETE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "这个状态的证书模板不能删除";
      CTMLSTATUS_NONMODIFY = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "这个状态的证书模板不能修改";
      CTMLSTATUS_NONREVOKE = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "这个状态的证书模板不能注销";
      REVOKESELFEXT_BADSTATUS = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "待注销的自定义扩展域状态不正确";
      DELETESELFEXT_BADSTATUS = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "待删除自定义扩展域状态不正确";
      MODIFYSELFEXT_BADSTATUS = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "待修改自定义扩展域状态不正确";
    }
  }

  public static class CHECKPARAM
  {
    public static final int CTMLNAME_ISNULL;
    public static final int CTMLNAME_INVALID;
    public static final int CTMLTYPE_ISNULL;
    public static final int CTMLTYPE_INVALID;
    public static final int CTMLPOLICY_ISNULL;
    public static final int CTMLPOLICY_INVALID;
    public static final int EXTENSIONVALUE_ISNULL;
    public static final int EXTENSION_ISREVOKED;
    public static final int EXTENSION_NOTSUPPORT;
    public static final int CTMLMODIFY_NODATA;
    public static final int EXTENSIONVALUE_INVALID;
    public static final int SELFEXTNAME_ISNULL;
    public static final int SELFEXTOID_ISNULL;
    public static final int SELFEXTNAMEANDOID_ISNULL;
    public static final int SELFEXTENCODING_ISNULL;
    public static final int SELFEXTDESC_ISNULL;

    static
    {
      CTMLServiceException.access$002(CTMLServiceException.access$108() * 100);
      CTMLNAME_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板名称为空";
      CTMLNAME_INVALID = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板名称无效";
      CTMLTYPE_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板类型为空";
      CTMLTYPE_INVALID = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板类型无效";
      CTMLPOLICY_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板策略描述信息为空";
      CTMLPOLICY_INVALID = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "证书模板策略描述信息无效";
      EXTENSIONVALUE_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "扩展域的值为空，但是需要这个值";
      EXTENSION_ISREVOKED = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "扩展域已经被注销，不能再使用";
      EXTENSION_NOTSUPPORT = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "系统不支持该扩展域";
      CTMLMODIFY_NODATA = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "没有发现要修改的证书模板数据";
      EXTENSIONVALUE_INVALID = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "扩展域的值无效";
      SELFEXTNAME_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "自定义扩展域名字为空";
      SELFEXTOID_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "自定义扩展域OID为空";
      SELFEXTNAMEANDOID_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "自定义扩展域名称和OID为空";
      SELFEXTENCODING_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "自定义扩展域ENCODING为空";
      SELFEXTDESC_ISNULL = CTMLServiceException.access$004();
      CTMLServiceException.errDesc[CTMLServiceException.counter] = "自定义扩展域DESC为空";
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLServiceException
 * JD-Core Version:    0.6.0
 */