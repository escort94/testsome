package cn.com.jit.ida.log;

import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.LOGConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

public class LogManager
{
  public static final String SYSLOG = "IDA_SYS_LOG";
  public static final String SYSLOG_APPENDER = "IDA_SYS_LOG_APPENDER";
  public static final String DEFAULT_LOG_PATH = "./log/";
  public static final String SYSLOG_FILE = "syslog.log";
  public static final String SYSLOG_DATE = "yyyy-MM-dd";
  public static final String SYSLOG_LAYOUT = "%-4d [%t] - %m%n";
  public static final String CONSOLE_LAYOUT = "%-4d - %m%n";
  public static final String DEBUGLOG1_APPENDER = "IDA_DEBUG1_LOG_APPENDER";
  public static final String DEBUGLOG1_FILE = "debuglog.log";
  public static final String DEBUGLOG2_APPENDER = "IDA_DEBUG2_LOG_APPENDER";
  public static final String DEBUGLOG2_FILE = "debuglog_lev2.log";
  private static String sysLogSize = null;
  static String debugLogSize = null;
  static int maxBackupIndex = 0;
  static String logPath = null;
  private static final String DEFAULT_SYSLOG_SIZE = "5MB";
  private static final String DEFAULT_DEBUGLOG_SIZE = "10MB";
  private static int DEFAULT_MAX_BACKUP_INDEX = 65535;
  static final String DEBUGLOG_LAYOUT = "%-4d [%t]%-8p%c%x - %m%n";
  private static boolean debug1 = false;
  private static boolean debug2 = false;
  private static boolean isInited = false;

  public static void init()
  {
    if (!isInited)
    {
      try
      {
        InternalConfig localInternalConfig = InternalConfig.getInstance();
        int i = localInternalConfig.getLogSize();
        if (i > 0)
        {
          sysLogSize = Integer.toString(i) + "MB";
          debugLogSize = Integer.toString(i) + "MB";
        }
        String localObject1 = LOGConfig.getInstance().getLOG_Path();
        if ((localObject1 != null) && (!((String)localObject1).endsWith(File.separator)))
          logPath = (String)localObject1 + File.separator;
        else
          logPath = (String)localObject1;
      }
      catch (Exception localException)
      {
      }
      finally
      {
        if (sysLogSize == null)
          sysLogSize = "5MB";
        if (debugLogSize == null)
          debugLogSize = "10MB";
        if (logPath == null)
          logPath = "./log/";
        File localFile = new File(logPath);
        if (!localFile.exists())
        {
          boolean bool = localFile.mkdirs();
          if (!bool)
          {
            System.out.println("所配置的系统日志目录[" + localFile + "]为无效目录，系统将使用默认路径[" + "./log/" + "]保存系统日志");
            System.out.println("请尽快将配置文件中的日志目录修改为有效目录.");
            logPath = "./log/";
            localFile = new File(logPath);
            if (!localFile.exists())
              localFile.mkdirs();
          }
        }
        maxBackupIndex = DEFAULT_MAX_BACKUP_INDEX;
      }
      Logger.getRootLogger().removeAllAppenders();
      Logger.getRootLogger().setLevel(Level.WARN);
      Logger localLogger = Logger.getLogger("IDA_SYS_LOG");
      RollingFileAppender localRollingFileAppender = new RollingFileAppender();
      localRollingFileAppender.setName("IDA_SYS_LOG_APPENDER");
      localRollingFileAppender.setFile(logPath + "syslog.log");
      localRollingFileAppender.setAppend(true);
      localRollingFileAppender.setMaxFileSize(sysLogSize);
      localRollingFileAppender.setMaxBackupIndex(maxBackupIndex);
      localRollingFileAppender.setThreshold(Priority.WARN);
      Object localObject1 = new PatternLayout("%-4d [%t] - %m%n");
      localRollingFileAppender.setLayout((Layout)localObject1);
      localRollingFileAppender.activateOptions();
      localLogger.addAppender(localRollingFileAppender);
      ConsoleAppender localConsoleAppender = new ConsoleAppender();
      localConsoleAppender.setTarget("System.out");
      localConsoleAppender.setThreshold(Priority.WARN);
      PatternLayout localPatternLayout = new PatternLayout("%-4d - %m%n");
      localConsoleAppender.setLayout(localPatternLayout);
      localConsoleAppender.activateOptions();
      localLogger.addAppender(localConsoleAppender);
      isInited = true;
    }
  }

  public static SysLogger getSysLogger()
  {
    return new SysLogger("IDA_SYS_LOG");
  }

  public static OptLogger getOPtLogger()
  {
    return new OptLogger();
  }

  public static DebugLogger getDebugLogger(String paramString)
  {
    return new DebugLogger(paramString);
  }

  public static synchronized void startDebugMode_L1()
  {
    if (!debug1)
    {
      RollingFileAppender localRollingFileAppender = new RollingFileAppender();
      localRollingFileAppender.setName("IDA_DEBUG1_LOG_APPENDER");
      localRollingFileAppender.setFile(logPath + "debuglog.log");
      localRollingFileAppender.setAppend(true);
      localRollingFileAppender.setMaxFileSize(debugLogSize);
      localRollingFileAppender.setMaxBackupIndex(maxBackupIndex);
      localRollingFileAppender.setThreshold(Priority.INFO);
      PatternLayout localPatternLayout = new PatternLayout("%-4d [%t]%-8p%c%x - %m%n");
      localRollingFileAppender.setLayout(localPatternLayout);
      localRollingFileAppender.activateOptions();
      Logger.getRoot().addAppender(localRollingFileAppender);
      Logger.getRootLogger().setLevel(Level.INFO);
      debug1 = true;
    }
  }

  public static synchronized void startDebugMode_L2()
  {
    if (!debug1)
      startDebugMode_L1();
    if (!debug2)
    {
      RollingFileAppender localRollingFileAppender = new RollingFileAppender();
      localRollingFileAppender.setName("IDA_DEBUG2_LOG_APPENDER");
      localRollingFileAppender.setFile(logPath + "debuglog_lev2.log");
      localRollingFileAppender.setAppend(true);
      localRollingFileAppender.setMaxFileSize(debugLogSize);
      localRollingFileAppender.setMaxBackupIndex(maxBackupIndex);
      localRollingFileAppender.setThreshold(Priority.DEBUG);
      PatternLayout localPatternLayout = new PatternLayout("%-4d [%t]%-8p%c%x - %m%n");
      localRollingFileAppender.setLayout(localPatternLayout);
      localRollingFileAppender.activateOptions();
      Logger.getRoot().addAppender(localRollingFileAppender);
      Logger.getRootLogger().setLevel(Level.DEBUG);
      debug2 = true;
    }
  }

  static boolean isL1()
  {
    return debug1;
  }

  static boolean isL2()
  {
    return debug2;
  }

  public static synchronized void endDebugMode_L1()
  {
    if (debug1)
    {
      if (debug2)
        endDebugMode_L2();
      Logger.getRoot().removeAppender("IDA_DEBUG1_LOG_APPENDER");
      Logger.getRoot().setLevel(Level.WARN);
      debug1 = false;
    }
  }

  public static synchronized void endDebugMode_L2()
  {
    if (debug2)
    {
      Logger.getRoot().removeAppender("IDA_DEBUG2_LOG_APPENDER");
      Logger.getRoot().setLevel(Level.INFO);
      debug2 = false;
    }
  }

  public static boolean isDebug()
  {
    return debug2;
  }

  public static boolean isDebug2()
  {
    return debug2;
  }

  public static boolean isInitialized()
  {
    return isInited;
  }

  public static boolean isDebug1()
  {
    return debug1;
  }

  public static void main(String[] paramArrayOfString)
  {
    init();
    SysLogger localSysLogger = getSysLogger();
    localSysLogger.info("系统启动成功");
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String str = null;
    try
    {
      System.out.println("command>");
      for (str = localBufferedReader.readLine(); !str.toUpperCase().equals("EXIT"); str = localBufferedReader.readLine())
      {
        localSysLogger.info(str);
        if (str.toUpperCase().equals("DEBUG1"))
        {
          startDebugMode_L1();
          DebugLogger localDebugLogger = getDebugLogger("debug");
          localDebugLogger.appendMsg_L1(str);
          localDebugLogger.doLog();
        }
        System.out.println("command>");
      }
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }

  public static void setIsInited(boolean paramBoolean)
  {
    isInited = paramBoolean;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.log.LogManager
 * JD-Core Version:    0.6.0
 */