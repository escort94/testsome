package cn.com.jit.ida.ca.issue;

import cn.com.jit.ida.IDAException;

public class ISSException extends IDAException
{
  private static final String ISSUE_MODULE = "84";
  public static final String ILLEGAL_ARGUMENT_SERVER_ADD = "8400";
  public static final String ILLEGAL_ARGUMENT_SERVER_ADD_DES = "LDAP服务器地址不能为空";
  public static final String LDAP_CONNECT_ERR = "8401";
  public static final String LDAP_CONNECT_ERR_DES = "LDAP连接失败";
  public static final String LDAP_CONNECT_NULL_ERR = "8402";
  public static final String LDAP_CONNECT_NULL_ERR_DES = "LDAP连接为空";
  public static final String LDAP_CLOSE_ERR = "8403";
  public static final String LDAP_CLOSE_ERR_DES = "LDAP关闭异常";
  public static final String ILLEGAL_ARGUMENT_ENTITY = "8404";
  public static final String ILLEGAL_ARGUMENT_ENTITY_DES = "待颁发实体不能为空";
  public static final String ILLEGAL_ARGUMENT_SN = "8405";
  public static final String ILLEGAL_ARGUMENT_SN_DES = "证书序列号不能为空";
  public static final String ILLEGAL_ARGUMENT_DN = "8406";
  public static final String ILLEGAL_ARGUMENT_DN_DES = "证书主题不能为空";
  public static final String ILLEGAL_ARGUMENT_CERT_TYPE = "8407";
  public static final String ILLEGAL_ARGUMENT_CERT_TYPE_DES = "证书类型不能为空";
  public static final String ILLEGAL_ARGUMENT_CERT = "8408";
  public static final String ILLEGAL_ARGUMENT_CERT_DES = "证书实体不能为空";
  public static final String ILLEGAL_ARGUMENT_ATTRI = "8409";
  public static final String ILLEGAL_ARGUMENT_ATTRI_DES = "发布属性不能为空";
  public static final String ISSUE_ENTITY_ERR = "8410";
  public static final String ISSUE_ENTITY_ERR_DES = "发布实体失败";
  public static final String REBIND_ERR = "8411";
  public static final String REBIND_ERR_DES = "更新实体失败";
  public static final String DELETE_ENTITY_ERR = "8412";
  public static final String DELETE_ENTITY_ERR_DES = "删除实体失败";
  public static final String SEARCH_ENTITY_ERR = "8413";
  public static final String SEARCH_ENTITY_ERR_DES = "查询证书实体失败";
  public static final String ENTRY_NOT_FOUND = "8414";
  public static final String ENTRY_NOT_FOUND_DES = "实体不存在";
  public static final String FILE_ISSUE_ERR = "8415";
  public static final String FILE_ISSUE_ERR_DES = "文件发布失败";
  public static final String GET_SCHEMA_ERR = "8416";
  public static final String GET_SCHEMA_ERR_DES = "获得LDAP语法失败";
  public static final String ADD_ATTRIBUTES_ERR = "8417";
  public static final String ADD_ATTRIBUTES_ERR_DES = "添加属性失败";
  public static final String DEL_ATTRIBUTES_ERR = "8418";
  public static final String DEL_ATTRIBUTES_ERR_DES = "删除属性失败";
  public static final String ADD_OBJECTCLASS_ERR = "8419";
  public static final String ADD_OBJECTCLASS_ERR_DES = "添加对象类失败";
  public static final String DEL_OBJECTCLASS_ERR = "8420";
  public static final String DEL_OBJECTCLASS_ERR_DES = "删除对象类失败";
  public static final String ROOT_CERT_NOTFOUND = "8421";
  public static final String ROOT_CERT_NOTFOUND_DES = "根证书没有发布";
  public static final String LDAP_STORE_CLASS_ERR = "8422";
  public static final String LDAP_STORE_CLASS_ERR_DES = "构造LDAP发布器失败";
  protected String errCode = null;
  protected String errDesc = null;
  protected Exception history = null;
  protected String errDescEx = null;

  public ISSException(String paramString)
  {
    super(paramString);
  }

  public ISSException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public ISSException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public ISSException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.ISSException
 * JD-Core Version:    0.6.0
 */