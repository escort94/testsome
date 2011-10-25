package cn.com.jit.ida.ca.log;

import cn.com.jit.ida.IDAException;

public class LogException extends IDAException
{
  private static final String CA = "80";
  public static final String LOG_ACHIVE = "8086";
  public static final String LOG_ACHIVE_DES = "操作日志归档";
  public static final String CERT_ACHIVE = "8086";
  public static final String CERT_ACHIVE_DES = "证书归档";
  public static final String OPTLOG_SEARCH = "8088";
  public static final String OPTLOG_SEARCH_DES = "归档业务日志查询服务";
  public static final String CERT_SEARCH = "8089";
  public static final String CERT_SEARCH_DES = "归档证书查询服务";
  public static final String LICENCE_ERR = "01";
  public static final String LICENCE_ERR_DES = "Licence验证 ";
  public static final String VALIDATE_ERR = "02";
  public static final String VALIDATE_ERR_DES = "数据有效性检查 ";
  public static final String POLICY_ERR = "03";
  public static final String POLICY_ERR_DES = "操作员权限检查 ";
  public static final String DEAL_OPERATION_ERR = "04";
  public static final String DEAL_OPERATION_ERR_DES = "执行业务操作 ";
  public static final String REQUEST = "05";
  public static final String REQUEST_DES = "设置请求信息 ";
  public static final String REPONSE = "06";
  public static final String RESPONSE_DES = "设置响应信息 ";
  public static final String OTHERS_ERR = "07";
  public static final String OTHERS_ERR_DES = "其他错误 ";
  public static final String OPTOR_NULL = "0201";
  public static final String OPTOR_NULL_DES = "数据有效性检查 操作员为空";
  public static final String OPTOR_DN_NULL = "0202";
  public static final String OPTOR_DN_NULL_DES = "数据有效性检查 操作员DN为空";
  public static final String OPTLOG_ERR = "0203";
  public static final String OPTLOG_ERR_DES = "数据有效性检查 记录业务日志失败";
  public static final String REQ_FORMAT_ERR = "0204";
  public static final String REQ_FORMAT_ERR_DES = "数据有效性检查 请求信息格式不合法";
  public static final String REP_FORMAT_ERR = "0205";
  public static final String REP_FORMAT_ERR_DES = "数据有效性检查 应答信息格式不合法";
  public static final String SYS_ERR = "0701";
  public static final String SYS_ERR_DES = "其他错误 系统错误";
  public static final String DB_ERR = "0702";
  public static final String DB_ERR_DES = "其他错误 DB错误";
  public static final String NULL_OPTERATOR = "0200";
  public static final String NULL_OPTERATOR_DES = "数据有效性检查  操作员为空";
  public static final String NULL_FROM_INDEX = "0201";
  public static final String NULL_FROM_INDEX_DES = "数据有效性检查  查询起始位置为空";
  public static final String NULL_ROW_COUNT = "0202";
  public static final String NULL_ROW_COUNT_DES = "数据有效性检查  查询记录数为空";
  public static final String NULL_QUERY_OPTION = "0203";
  public static final String NULL_QUERY_OPTION_DES = "数据有效性检查  查询条件为空";
  public static final String NULL_SORT_BY = "0204";
  public static final String NULL_SORT_BY_DES = "数据有效性检查  统计证书的分类方式为空";

  public LogException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public LogException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public LogException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.LogException
 * JD-Core Version:    0.6.0
 */