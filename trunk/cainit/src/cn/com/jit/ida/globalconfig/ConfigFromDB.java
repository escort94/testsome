package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.IDAException;
import java.util.Properties;
import java.util.Vector;

public class ConfigFromDB extends Config
{
  protected Properties properties = new Properties();
  Vector vector;
  DBInterface dbManager;

  public ConfigFromDB(DBInterface paramDBInterface, String paramString)
    throws IDAException
  {
    super(paramString);
    this.dbManager = paramDBInterface;
    this.vector = paramDBInterface.getConfig(paramString);
    for (int i = 0; i < this.vector.size(); i++)
    {
      Information localInformation = (Information)this.vector.get(i);
      String str;
      if (localInformation.getIsEncrypted().equalsIgnoreCase("Y"))
        try
        {
          str = ProtectConfig.getInstance().Decrypto(localInformation.getValue());
        }
        catch (ConfigException localConfigException)
        {
          throw localConfigException;
        }
      else
        str = localInformation.getValue();
      this.properties.put(localInformation.getName(), str);
    }
  }

  public String getString(String paramString)
  {
    String str = this.properties.getProperty(paramString);
    if (str == null)
      return "";
    return str;
  }

  public void setString(String paramString1, String paramString2)
    throws IDAException
  {
    try
    {
      int i = 0;
      for (int j = 0; j < this.vector.size(); j++)
      {
        Information localInformation1 = (Information)this.vector.elementAt(j);
        if (!localInformation1.getName().equalsIgnoreCase(paramString1))
          continue;
        if (localInformation1.isEncrypted.equalsIgnoreCase("Y"))
          paramString2 = ProtectConfig.getInstance().Encrypto(paramString2);
        localInformation1.setValue(paramString2);
        this.dbManager.setConfig(this.type, localInformation1);
        i = 1;
        break;
      }
      if (i == 0)
      {
        Information localInformation2 = new Information(paramString1, paramString2, "N");
        this.dbManager.setConfig(this.type, localInformation2);
        this.vector.add(localInformation2);
      }
    }
    catch (IDAException localIDAException)
    {
      throw localIDAException;
    }
    this.properties.setProperty(paramString1, paramString2);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.ConfigFromDB
 * JD-Core Version:    0.6.0
 */