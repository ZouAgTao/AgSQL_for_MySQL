package data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//以下使用设计模式的单例模式构建日志类
public class LogSave
{
	private StringBuilder log_file = new StringBuilder("");
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	private Calendar calendar = Calendar.getInstance(Locale.CHINA);
	
	public void log(String text)
	{
		log_file.append("["+format.format(calendar.getTime())+"] "+text+"\n");
	}
	
	//重写toString
	@Override
	public String toString()
	{
		return log_file.toString();
	}
	
	public LogSave()
	{
		log("[启动AgSQL for MySQL 日志记录系统]");
	}
	
	/**
	 * 单例模式变量
	 */
	private static LogSave log = new LogSave();
	
	/**
	 * 单例模式获取类
	 */
	public static LogSave getLog()
	{
		return log;
	}
}
