package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.TimerTask;

class LoadTimerTask extends TimerTask
{
  private static boolean isFirst = true;

  public void run()
  {
    SysLogger localSysLogger = LogManager.getSysLogger();
    if (isFirst)
      isFirst = false;
    else
      try
      {
        CTMLManager.initializeInstance();
        SelfExtensionManager.initializeInstance();
        Privilege localPrivilege = Privilege.getInstance();
        TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
        localPrivilege.refreshAdminInfo();
        localTemplateAdmin.refreshAdminInfo();
        localSysLogger.info("同步数据成功");
      }
      catch (Exception localException)
      {
        localSysLogger.info(localException.getMessage());
        localSysLogger.info("同步数据异常中止");
      }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.LoadTimerTask
 * JD-Core Version:    0.6.0
 */