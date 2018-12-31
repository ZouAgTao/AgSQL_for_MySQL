package data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//����ʹ�����ģʽ�ĵ���ģʽ������־��
public class LogSave
{
	private StringBuilder log_file = new StringBuilder("");
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	private Calendar calendar = Calendar.getInstance(Locale.CHINA);
	
	public void log(String text)
	{
		log_file.append("["+format.format(calendar.getTime())+"] "+text+"\n");
	}
	
	//��дtoString
	@Override
	public String toString()
	{
		return log_file.toString();
	}
	
	public LogSave()
	{
		log("[����AgSQL for MySQL ��־��¼ϵͳ]");
	}
	
	/**
	 * ����ģʽ����
	 */
	private static LogSave log = new LogSave();
	
	/**
	 * ����ģʽ��ȡ��
	 */
	public static LogSave getLog()
	{
		return log;
	}
}
