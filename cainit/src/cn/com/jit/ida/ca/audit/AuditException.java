package cn.com.jit.ida.ca.audit;

import cn.com.jit.ida.IDAException;

public class AuditException extends IDAException
{
  private static final String CA = "80";
  public static final String AUDIT_OPTLOG = "8071";
  public static final String AUDIT_OPTLOG_DES = "业务日志审计服务";
  public static final String AUDIT_CERT = "8072";
  public static final String AUDIT_CERT_DES = "证书审计服务";
  public static final String SYS_ERR = "9999";
  public static final String SYS_ERR_DES = "系统错误";
  private static final String PROTOCAL_ERR = "00";
  private static final String PROTOCAL_ERR_DES = "通信格式错误";
  private static final String VALIDATE_ERR = "01";
  private static final String VALIDATE_ERR_DES = "数据检查错误";
  private static final String LOG_ERR = "02";
  private static final String LOG_ERR_DES = "日志错误";
  public static final String REQ_FORMAT_ERR = "0000";
  public static final String REQ_FORMAT_ERR_DES = "通信格式错误 请求信息格式不合法";
  public static final String REP_FORMAT_ERR = "0001";
  public static final String REP_FORMAT_ERR_DES = "通信格式错误 响应信息格式不合法";
  public static final String NULL_OPTERATOR = "0100";
  public static final String NULL_OPTERATOR_DES = "数据检查错误 操作员为空";
  public static final String NULL_FROM_INDEX = "0101";
  public static final String NULL_FROM_INDEX_DES = "数据检查错误 查询起始位置为空";
  public static final String NULL_ROW_COUNT = "0102";
  public static final String NULL_ROW_COUNT_DES = "数据检查错误 查询记录数为空";
  public static final String NULL_QUERY_OPTION = "0103";
  public static final String NULL_QUERY_OPTION_DES = "数据检查错误 查询条件为空";
  public static final String NULL_SORT_BY = "0104";
  public static final String NULL_SORT_BY_DES = "数据检查错误 统计证书的分类方式为空";
  public static final String OPTLOG_ERR = "0200";
  public static final String OPTLOG_ERR_DES = "日志错误 记录操作日志错误";

  public AuditException(String paramString)
  {
    super(paramString);
  }

  public AuditException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public AuditException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public AuditException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.AuditException
 * JD-Core Version:    0.6.0
 */