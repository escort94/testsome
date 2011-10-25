package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.Request;
import java.util.Properties;

public class GetConfigRequest extends Request
{
  Properties properties = new Properties();

  public GetConfigRequest()
  {
    super.setOperation("SYETEMVIEWCONFIG");
  }

  public Properties getProperties()
  {
    return this.properties;
  }

  public void setProperties(Properties paramProperties)
  {
    this.properties = paramProperties;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.GetConfigRequest
 * JD-Core Version:    0.6.0
 */