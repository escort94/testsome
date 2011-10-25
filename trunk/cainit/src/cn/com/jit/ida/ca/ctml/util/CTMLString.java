package cn.com.jit.ida.ca.ctml.util;

public class CTMLString
{
  public class SYSINFO
  {
    public static final String CREATE_CTMLMANAGER = "创建证书模板管理器...";
    public static final String INIT_CTMLMANAGER = "初始化证书模板管理器...";
    public static final String CREATE_SELFEXTMANAGER = "创建自定义扩展域管理器...";
    public static final String INIT_SELFEXTMANAGER = "初始化自定义扩展域模板管理器...";

    public SYSINFO()
    {
    }
  }

  public class EXTENSION_NAME
  {
    public static final String AUTHINFOACCESS = "签发机构信息访问";
    public static final String AUTHORITYKEYID = "签发机构密钥标志符";
    public static final String BASICCONSTRAINTS = "基本限制";
    public static final String CERTPOLICY = "证书策略";
    public static final String CERTTYPE = "Netscape证书类型";
    public static final String CRLDP = "CRL发布点";
    public static final String EXTENDKEYUSAGE = "增强型密钥用法";
    public static final String KEYUSAGE = "密钥用法";
    public static final String SUBJECTKEYID = "主题密钥标志符";
    public static final String SubjectAltNameExt = "主题备用名称";
    public static final String CertificateTemplate = "证书模板名称";
    public static final String USERDEFINE = "自定义扩展域";
    public static final String UNKNOW = "未知扩展域";
    public static final String IDENTIFYCODE = "身份标识码";
    public static final String INSURANCENUMBER = "个人社会保险号";
    public static final String ORGANIZATIONCODE = "企业组织机构代码";
    public static final String ICREGISTRATIONNUMBERLABEL = "企业工商注册号";
    public static final String TAXATION_NUMBER_LABEL = "企业税号";
    public static final String POLICY_CONSTRAINTS = "策略限制";
    public static final String POLICY_MAPPINGS = "策略映射";

    public EXTENSION_NAME()
    {
    }
  }

  public class SERVICE_ERRDESC
  {
    public static final String CTMLNAME_ISNULL = "证书模板名称为空";
    public static final String CTMLNAME_INVALID = "证书模板名称无效";
    public static final String CTMLTYPE_ISNULL = "证书模板类型为空";
    public static final String CTMLTYPE_INVALID = "证书模板类型无效";
    public static final String CTMLPOLICY_ISNULL = "证书模板策略描述信息为空";
    public static final String CTMLPOLICY_INVALID = "证书模板策略描述信息无效";
    public static final String EXTENSIONVALUE_ISNULL = "扩展域的值为空，但是需要这个值";
    public static final String EXTENSION_ISREVOKED = "扩展域已经被注销，不能再使用";
    public static final String EXTENSION_NOTSUPPORT = "系统不支持该扩展域";
    public static final String CTMLMODIFY_NODATA = "没有发现要修改的证书模板数据";
    public static final String EXTENSIONVALUE_INVALID = "扩展域的值无效";
    public static final String SELFEXTNAME_ISNULL = "自定义扩展域名字为空";
    public static final String SELFEXTOID_ISNULL = "自定义扩展域OID为空";
    public static final String SELFEXTNAMEANDOID_ISNULL = "自定义扩展域名称和OID为空";
    public static final String SELFEXTENCODING_ISNULL = "自定义扩展域ENCODING为空";
    public static final String SELFEXTENDESC_ISNULL = "自定义扩展域DESC为空";
    public static final String PARSE_REQUESTXMLDOC = "解析请求XML对象失败";
    public static final String PARSE_REQUESTXMLDATA = "解析请求XML数据失败";
    public static final String CTMLSTATUS_NONDELETE = "这个状态的证书模板不能删除";
    public static final String CTMLSTATUS_NONMODIFY = "这个状态的证书模板不能修改";
    public static final String CTMLSTATUS_NONREVOKE = "这个状态的证书模板不能注销";
    public static final String ADMINCTML_CANTMODIFY = "管理员证书模板不能修改";
    public static final String ADMINCTML_CANTDELETE = "管理员证书模板不能删除";
    public static final String ADMINCTML_CANTREVOKE = "管理员证书模板不能注销";
    public static final String COMMCTML_CANTMODIFY = "系统通信证书模板不能修改";
    public static final String COMMCTML_CANTDELETE = "系统通信证书模板不能删除";
    public static final String COMMCTML_CANTREVOKE = "系统通信证书模板不能注销";

    public SERVICE_ERRDESC()
    {
    }
  }

  public class ERRDESC
  {
    public static final String PARSECTMLPOLICY = "解析证书模板策略信息失败";
    public static final String INVALIDCTMLTYPE = "无效的证书模板类型";
    public static final String XMLNODENOTFIND = "指定的XML节点没有找到";
    public static final String CTMLNODENOTFIND = "CTML节点没有找到";
    public static final String DEFAULTVALIDITYISNULL = "默认有效期为空";
    public static final String INVALIDDEFAULTVALIDITY = "默认有效期无效";
    public static final String MAXVALIDITYISNULL = "最大有效期为空";
    public static final String INVALIDMAXVALIDITY = "最大有效期无效";
    public static final String NOTAFTERISNULL = "最晚失效时间为空";
    public static final String INVALIDNOTAFTER = "最晚失效时间无效";
    public static final String KEYTYPEISNULL = "密钥类型为空";
    public static final String INVALIDKEYTYPE = "密钥类型无效";
    public static final String KEYLENGTHISNULL = "密钥长度为空";
    public static final String INVALIDKEYLENGTH = "密钥长度无效";
    public static final String KEYGENPLACEISNULL = "密钥产生地点为空";
    public static final String INVALIDKEYGENPLACE = "密钥产生地点无效";
    public static final String ISSUESTATUSISNULL = "证书发布状态为空";
    public static final String INVALIDISSUESTATUS = "证书发布状态无效";
    public static final String KEYSPECISNULL = "密钥容器策略为空";
    public static final String INVALIDKEYSPEC = "密钥容器策略无效";
    public static final String UPDATEREPLACEISNULL = "更新时是否替换原有证书标记为空";
    public static final String INVALIDUPDATEREPLACE = "更新时是否替换原有证书标记无效";
    public static final String UPDTRANSPERIODISNULL = "密钥容器策略为空";
    public static final String INVALIDUPDTRANSPERIOD = "密钥容器策略无效";
    public static final String ISSUEMEDIUMISNULL = "证书发布方式为空";
    public static final String INVALIDISSUEMEDIUM = "证书发布方式无效";
    public static final String INVALIDEXTENSIONTYPE = "无效的扩展域类型";
    public static final String INVALID_EXTENSION_USERPROCESSPOLICY = "无效的扩展域用户处理策略";
    public static final String INVALIDEXTKEYUSAGE = "无效的增强密钥用法";
    public static final String EXTKEYUSAGE_VALUEISNULL = "增强密钥用法的值为空";
    public static final String EXTKEYUSAGE_NOTFINDANY = "没有找到任何增强密钥用法";
    public static final String FOUND_INVALIDEXTKEYUSAGE = "发现一个无效的增强密钥用法";
    public static final String EXTKEYUSAGE_OIDISNULL = "用户定义的增强密钥用法OID为空";
    public static final String EXTKEYUSAGE_NAMEISNULL = "用户定义的增强密钥用法名称为空";
    public static final String INVALIDKEYUSAGE = "无效的密钥用法";
    public static final String KEYUSAGE_VALUEISNULL = "密钥用法的值为空";
    public static final String KEYUSAGE_NOTFINDANY = "没有找到任何密钥用法";
    public static final String FOUND_INVALIDKEYUSAGE = "发现一个无效的密钥用法";
    public static final String INVALIDCERTTYPE = "无效的证书类型";
    public static final String CERTTYPE_VALUEISNULL = "证书类型的值为空";
    public static final String CERTTYPE_NOTFINDANY = "没有找到任何证书类型";
    public static final String FOUND_INVALIDCERTTYPE = "发现一个无效的证书类型";
    public static final String GENCERTFAIL = "产生证书失败";
    public static final String INVALID_SERIALNUMBER = "无效的证书序列号";
    public static final String INVALID_NOTBEFORE = "无效的证书生效时间";
    public static final String INVALID_NOTAFTER = "无效的证书失效时间";
    public static final String INVALID_CTMLSTATUS = "无效的证书模板状态，这个状态的证书模板不能进行签发证书的操作";
    public static final String PROCESSEXTENSIONFAIL = "处理扩展域失败";
    public static final String CHECKSUBJECTFAIL = "如果使用主题备用名称扩展域，证书主题必须包含Common Name";
    public static final String SELFEXTLIST_ISNULL = "自定义扩展域管理器的容器为空";
    public static final String CREATESELFEXT_BADPARAM = "新建自定义扩展域参数为空";
    public static final String DELETESELFEXT_BADNAMEPARAM = "删除自定义扩展域参数名称为空";
    public static final String DELETESELFEXT_BADOIDPARAM = "删除自定义扩展域参数OID为空";
    public static final String GETSELFEXT_NOTFIND = "没有发现指定的自定义扩展域";
    public static final String GETSELFEXT_NOTFINDBYNAME = "没有发现指定名称的自定义扩展域";
    public static final String GETSELFEXT_NOTFINDBYOID = "没有发现指定OID的自定义扩展域";
    public static final String GETSELFEXT_BADNAMEPARAM = "获取自定义扩展域参数名称为空";
    public static final String GETSELFEXT_BADOIDPARAM = "获取自定义扩展域参数OID为空";
    public static final String MODIFYSELFEXT_BADPARAM = "修改自定义扩展域参数为空";
    public static final String UPDATESELFEXTSTATUS_BADPARAM = "更新自定义扩展域状态名称或新状态为空";
    public static final String REVOKESELFEXT_BADSTATUS = "待注销的自定义扩展域状态不正确";
    public static final String DELETESELFEXT_BADSTATUS = "待删除自定义扩展域状态不正确";
    public static final String MODIFYSELFEXT_BADSTATUS = "待修改自定义扩展域状态不正确";
    public static final String SELFEXT_EXISTED = "自定义扩展域已经存在";
    public static final String CMLTLIST_ISNULL = "证书管理器的容器为空";
    public static final String GETCTML_BADPARAM = "获取证书模板参数为空";
    public static final String GETCTML_BADNAMEPARAM = "获取证书模板参数名称为空";
    public static final String GETCTML_BADIDPARAM = "获取证书模板参数ID为空";
    public static final String GETCTML_NOTFIND = "没有发现指定的证书模板";
    public static final String GETCTML_NOTFINDBYNAME = "没有发现指定名称的证书模板";
    public static final String GETCTML_NOTFINDBYID = "没有发现指定ID的证书模板";
    public static final String CREATECTML_BADPARAM = "新建证书模参数为空";
    public static final String CTML_EXISTED = "证书模板已经存在";
    public static final String MODIFYCTML_BADPARAM = "修改证书模板参数为空";
    public static final String UPDATECTMLSTATUS_BADPARAM = "更新证书模板名称或新状态为空";
    public static final String DELETECTML_BADNAMEPARAM = "删除证书模板参数名称为空";
    public static final String DELETECTML_BADIDPARAM = "删除证书模板参数ID为空";
    public static final String GEN_CTMLID = "产生证书模板ID失败";

    public ERRDESC()
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.util.CTMLString
 * JD-Core Version:    0.6.0
 */