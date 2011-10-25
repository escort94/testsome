package cn.com.jit.ida.ca.ctml.util;

import cn.com.jit.ida.IDAException;
import java.io.PrintStream;

public class CTMLException extends IDAException
{
  private static final String CTML_MODULE_ID = "83";
  private static final String[] errDesc = new String[100];
  private static int counter = 0;

  public CTMLException(int paramInt)
  {
    super(convert(paramInt), getErrorDesc(paramInt));
  }

  public CTMLException(int paramInt, String paramString)
  {
    super(convert(paramInt), paramString);
  }

  public CTMLException(int paramInt, Exception paramException)
  {
    super(convert(paramInt), getErrorDesc(paramInt), paramException);
  }

  public CTMLException(int paramInt, String paramString, Exception paramException)
  {
    super(convert(paramInt), paramString, paramException);
  }

  private static String getErrorDesc(int paramInt)
  {
    return errDesc[paramInt];
  }

  private static String convert(int paramInt)
  {
    return "83" + convert(paramInt, 2);
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
    new CTMLException(0);
    for (int i = 0; i < 100; i++)
    {
      if (errDesc[i] == null)
        continue;
      System.out.println(i + ":" + errDesc[i]);
    }
  }

  static
  {
    new CTML();
    new SELFEXT();
    new PROCESSOR();
  }

  public static class PROCESSOR
  {
    public static final int GENCERTFAIL = CTMLException.access$004();
    public static final int INVALID_SERIALNUMBER;
    public static final int INVALID_NOTBEFORE;
    public static final int INVALID_NOTAFTER;
    public static final int INVALID_CTMLSTATUS;
    public static final int PROCESSEXTENSIONFAIL;
    public static final int CHECKSUBJECTFAIL;

    static
    {
      CTMLException.errDesc[CTMLException.counter] = "产生证书失败";
      INVALID_SERIALNUMBER = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书序列号";
      INVALID_NOTBEFORE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书生效时间";
      INVALID_NOTAFTER = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书失效时间";
      INVALID_CTMLSTATUS = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书模板状态，这个状态的证书模板不能进行签发证书的操作";
      PROCESSEXTENSIONFAIL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "处理扩展域失败";
      CHECKSUBJECTFAIL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "如果使用主题备用名称扩展域，证书主题必须包含Common Name";
    }
  }

  public static class SELFEXT
  {
    public static final int SELFEXTLIST_ISNULL = CTMLException.access$004();
    public static final int CREATESELFEXT_BADPARAM;
    public static final int DELETESELFEXT_BADNAMEPARAM;
    public static final int DELETESELFEXT_BADOIDPARAM;
    public static final int GETSELFEXT_NOTFIND;
    public static final int GETSELFEXT_NOTFINDBYNAME;
    public static final int GETSELFEXT_NOTFINDBYOID;
    public static final int GETSELFEXT_BADNAMEPARAM;
    public static final int GETSELFEXT_BADOIDPARAM;
    public static final int MODIFYSELFEXT_BADPARAM;
    public static final int UPDATESELFEXTSTATUS_BADPARAM;
    public static final int SELFEXT_EXISTED;

    static
    {
      CTMLException.errDesc[CTMLException.counter] = "自定义扩展域管理器的容器为空";
      CREATESELFEXT_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "新建自定义扩展域参数为空";
      DELETESELFEXT_BADNAMEPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "删除自定义扩展域参数名称为空";
      DELETESELFEXT_BADOIDPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "删除自定义扩展域参数OID为空";
      GETSELFEXT_NOTFIND = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定的自定义扩展域";
      GETSELFEXT_NOTFINDBYNAME = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定名称的自定义扩展域";
      GETSELFEXT_NOTFINDBYOID = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定OID的自定义扩展域";
      GETSELFEXT_BADNAMEPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "获取自定义扩展域参数名称为空";
      GETSELFEXT_BADOIDPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "获取自定义扩展域参数OID为空";
      MODIFYSELFEXT_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "修改自定义扩展域参数为空";
      UPDATESELFEXTSTATUS_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "更新自定义扩展域状态名称或新状态为空";
      SELFEXT_EXISTED = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "自定义扩展域已经存在";
    }
  }

  public static class CTML
  {
    public static final int PARSECTMLPOLICY = CTMLException.access$004();
    public static final int INVALIDCTMLTYPE;
    public static final int XMLNODENOTFIND;
    public static final int CTMLNODENOTFIND;
    public static final int DEFAULTVALIDITYISNULL;
    public static final int INVALIDDEFAULTVALIDITY;
    public static final int MAXVALIDITYISNULL;
    public static final int INVALIDMAXVALIDITY;
    public static final int NOTAFTERISNULL;
    public static final int INVALIDNOTAFTER;
    public static final int KEYTYPEISNULL;
    public static final int INVALIDKEYTYPE;
    public static final int KEYLENGTHISNULL;
    public static final int INVALIDKEYLENGTH;
    public static final int KEYGENPLACEISNULL;
    public static final int INVALIDKEYGENPLACE;
    public static final int ISSUESTATUSISNULL;
    public static final int INVALIDISSUESTATUS;
    public static final int ISSUEMEDIUMISNULL;
    public static final int INVALIDISSUEMEDIUM;
    public static final int KEYSPECISNULL;
    public static final int INVALIDKEYSPEC;
    public static final int UPDATEREPLACEISNULL;
    public static final int INVALIDUPDATEREPLACE;
    public static final int UPDTRANSPERIODISNULL;
    public static final int INVALIDUPDTRANSPERIOD;
    public static final int INVALIDEXTENSIONTYPE;
    public static final int INVALID_EXTENSION_USERPROCESSPOLICY;
    public static final int INVALIDEXTKEYUSAGE;
    public static final int EXTKEYUSAGE_VALUEISNULL;
    public static final int EXTKEYUSAGE_NOTFINDANY;
    public static final int FOUND_INVALIDEXTKEYUSAGE;
    public static final int EXTKEYUSAGE_OIDISNULL;
    public static final int EXTKEYUSAGE_NAMEISNULL;
    public static final int INVALIDKEYUSAGE;
    public static final int KEYUSAGE_VALUEISNULL;
    public static final int KEYUSAGE_NOTFINDANY;
    public static final int FOUND_INVALIDKEYUSAGE;
    public static final int INVALIDCERTTYPE;
    public static final int CERTTYPE_VALUEISNULL;
    public static final int CERTTYPE_NOTFINDANY;
    public static final int FOUND_INVALIDCERTTYPE;
    public static final int CTMLLIST_ISNULL;
    public static final int GETCTML_BADPARAM;
    public static final int GETCTML_BADNAMEPARAM;
    public static final int GETCTML_BADIDPARAM;
    public static final int GETCTML_NOTFIND;
    public static final int GETCTML_NOTFINDBYNAME;
    public static final int GETCTML_NOTFINDBYID;
    public static final int CREATECTML_BADPARAM;
    public static final int CTML_EXISTED;
    public static final int MODIFYCTML_BADPARAM;
    public static final int UPDATECTMLSTATUS_BADPARAM;
    public static final int DELETECTML_BADNAMEPARAM;
    public static final int DELETECTML_BADIDPARAM;
    public static final int GEN_CTMLID;

    static
    {
      CTMLException.errDesc[CTMLException.counter] = "解析证书模板策略信息失败";
      INVALIDCTMLTYPE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书模板类型";
      XMLNODENOTFIND = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "指定的XML节点没有找到";
      CTMLNODENOTFIND = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "CTML节点没有找到";
      DEFAULTVALIDITYISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "默认有效期为空";
      INVALIDDEFAULTVALIDITY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "默认有效期无效";
      MAXVALIDITYISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "最大有效期为空";
      INVALIDMAXVALIDITY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "最大有效期无效";
      NOTAFTERISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "最晚失效时间为空";
      INVALIDNOTAFTER = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "最晚失效时间无效";
      KEYTYPEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥类型为空";
      INVALIDKEYTYPE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥类型无效";
      KEYLENGTHISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥长度为空";
      INVALIDKEYLENGTH = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥长度无效";
      KEYGENPLACEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥产生地点为空";
      INVALIDKEYGENPLACE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥产生地点无效";
      ISSUESTATUSISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书发布状态为空";
      INVALIDISSUESTATUS = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书发布状态无效";
      ISSUEMEDIUMISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书发布方式为空";
      INVALIDISSUEMEDIUM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书发布方式无效";
      KEYSPECISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥容器策略为空";
      INVALIDKEYSPEC = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥容器策略无效";
      UPDATEREPLACEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "更新时是否替换原有证书标记为空";
      INVALIDUPDATEREPLACE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "更新时是否替换原有证书标记无效";
      UPDTRANSPERIODISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥容器策略为空";
      INVALIDUPDTRANSPERIOD = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥容器策略无效";
      INVALIDEXTENSIONTYPE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的扩展域类型";
      INVALID_EXTENSION_USERPROCESSPOLICY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的扩展域用户处理策略";
      INVALIDEXTKEYUSAGE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的增强密钥用法";
      EXTKEYUSAGE_VALUEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "增强密钥用法的值为空";
      EXTKEYUSAGE_NOTFINDANY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有找到任何增强密钥用法";
      FOUND_INVALIDEXTKEYUSAGE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "发现一个无效的增强密钥用法";
      EXTKEYUSAGE_OIDISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "用户定义的增强密钥用法OID为空";
      EXTKEYUSAGE_NAMEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "用户定义的增强密钥用法名称为空";
      INVALIDKEYUSAGE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的密钥用法";
      KEYUSAGE_VALUEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "密钥用法的值为空";
      KEYUSAGE_NOTFINDANY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有找到任何密钥用法";
      FOUND_INVALIDKEYUSAGE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "发现一个无效的密钥用法";
      INVALIDCERTTYPE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "无效的证书类型";
      CERTTYPE_VALUEISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书类型的值为空";
      CERTTYPE_NOTFINDANY = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有找到任何证书类型";
      FOUND_INVALIDCERTTYPE = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "发现一个无效的证书类型";
      CTMLLIST_ISNULL = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书管理器的容器为空";
      GETCTML_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "获取证书模板参数为空";
      GETCTML_BADNAMEPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "获取证书模板参数名称为空";
      GETCTML_BADIDPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "获取证书模板参数ID为空";
      GETCTML_NOTFIND = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定的证书模板";
      GETCTML_NOTFINDBYNAME = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定名称的证书模板";
      GETCTML_NOTFINDBYID = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "没有发现指定ID的证书模板";
      CREATECTML_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "新建证书模参数为空";
      CTML_EXISTED = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "证书模板已经存在";
      MODIFYCTML_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "修改证书模板参数为空";
      UPDATECTMLSTATUS_BADPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "更新证书模板名称或新状态为空";
      DELETECTML_BADNAMEPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "删除证书模板参数名称为空";
      DELETECTML_BADIDPARAM = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "删除证书模板参数ID为空";
      GEN_CTMLID = CTMLException.access$004();
      CTMLException.errDesc[CTMLException.counter] = "产生证书模板ID失败";
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.util.CTMLException
 * JD-Core Version:    0.6.0
 */