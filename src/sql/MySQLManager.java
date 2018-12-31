package sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import data.LogSave;

public class MySQLManager
{
	private String url = "localhost";
	private int port = 3306;
	private String username = "root";
	private String password = "";
	
	private Connection conn = null;
	private PreparedStatement prep = null;
	
	private LogSave logSave = null;
	
	public MySQLManager()
	{
		logSave = LogSave.getLog();
	}
	
	public MySQLManager(String url, int port, String username, String password)
	{
		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
		
		logSave = LogSave.getLog();
	}
	
	public boolean importDB()
	{
		logSave.log("��ʼ�������ݿ�");
		Runtime rt = Runtime.getRuntime();
		
		String filename = "";
		String path = "";
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "*.sql";
			}
			@Override
			public boolean accept(File f)
			{
				String name = f.getName();
				return f.isDirectory() || name.toLowerCase().endsWith(".sql");
			}
		});
		chooser.showDialog(new JLabel(), "����SQL�ļ�");
		
		File output = chooser.getSelectedFile();
		
		if(output==null)
		{
			return false;
		}
		
		filename = ((output.getName()).split("\\."))[0];
		path = output.getAbsolutePath();
		
		StringBuffer cmd = new StringBuffer();
		cmd.append("mysql -u");
		cmd.append(username);
		cmd.append(" -p");
		cmd.append(password);
		cmd.append(" -h");
		cmd.append(url);
		cmd.append(" "+filename+" ");
		cmd.append("< ");
		cmd.append(path);
		
		try
		{
			newDB(filename);
			
			rt.exec("cmd /c "+cmd.toString());
			
			logSave.log("����ɹ�");
			
			return true;
		}
		catch (Exception e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("����ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "����ʧ�ܣ�������Ϣ����\n"+error,"����",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public void addUser(String name)
	{
		logSave.log("��ʼ�½��û�");
		
		String passwd= JOptionPane.showInputDialog("����������");
		String temp= JOptionPane.showInputDialog("��������һ������");
		if(passwd==null || temp==null || passwd.equals("") || temp.equals(""))
		{
			JOptionPane.showMessageDialog(null, "�½��û�ȡ��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if(!passwd.equals(temp))
		{
			JOptionPane.showMessageDialog(null, "�����������벻һ��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try
		{
			prep = conn.prepareStatement("CREATE USER '"+name+"'@'"+url+"' IDENTIFIED BY '"+passwd+"';");
			prep.execute();
			JOptionPane.showMessageDialog(null, "�½��û�["+name+"]�ɹ�", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("�½��û��ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			temp = e.getMessage();
			logSave.log("�½��û�ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ����\n"+error,"����",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void removeUser(String name)
	{
		logSave.log("��ʼɾ���û�");
		
		if(name.equals("root"))
		{
			JOptionPane.showMessageDialog(null, "���ڰ�ȫ�Կ��ǣ���֧��ɾ��root�û�", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try
		{
			prep = conn.prepareStatement("DROP USER '"+name+"'@'"+url+"';");
			prep.execute();
			JOptionPane.showMessageDialog(null, "ɾ���û�["+name+"]�ɹ�", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("ɾ���û��ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("ɾ���û�ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ����\n"+error,"����",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public boolean backUPDB(String db_name)
	{
		logSave.log("��ʼ�������ݿ�");
		Runtime rt = Runtime.getRuntime();
		
		StringBuffer cmd = new StringBuffer();
		cmd.append("mysqldump -u");
		cmd.append(username);
		cmd.append(" -p");
		cmd.append(password);
		cmd.append(" -h");
		cmd.append(url);
		cmd.append(" --set-charset=utf8 ");
		cmd.append(db_name);
		
		try
		{
			Process child = rt.exec(cmd.toString());
			InputStream in = child.getInputStream();
			InputStreamReader ir = new InputStreamReader(in, "utf8");
			
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.showDialog(new JLabel(), "����SQL�ļ�");
			File output = chooser.getSelectedFile();
			
			if(output==null || !output.isDirectory())
			{
				return false;
			}
			
			FileOutputStream fo = new FileOutputStream(new File(output, db_name+".sql"));
			OutputStreamWriter os = new OutputStreamWriter(fo, "utf8");
			
			char[] temp = new char[1024000];
			int len = 0;
			while ((len = ir.read(temp)) > 0)
			{
				os.write(temp, 0, len);
				os.flush();
			}
			in.close();
			ir.close();
			os.close();
			fo.close();
			
			logSave.log("���ݳɹ�");
			
			return true;
		}
		catch (IOException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("����ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ����\n"+error,"����",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public String exce(String sql)
	{
		logSave.log("ִ��"+sql);
		try
		{
			prep = conn.prepareStatement(sql);
			if(prep.execute())
			{
				StringBuilder str = new StringBuilder("");
				
				ResultSet resultSet = prep.getResultSet();
				int columnCount = resultSet.getMetaData().getColumnCount();
				
				str.append("������\n\n");
				for (int i = 1; i <= columnCount; i++)
				{
					str.append("["+resultSet.getMetaData().getColumnName(i)+"]"+" ");
				}
				str.append("\n\n\n\n");
				
				str.append("�ֶΣ� \n\n");
				while(resultSet.next())
				{
					str.append("{\n\n");
					for (int i = 1; i <= columnCount; i++)
					{
						str.append("["+resultSet.getObject(i)+"]\n\n");
					}
					str.append("}\n\n");
				}
				
				logSave.log("ִ�гɹ����н������");
				return str.toString();
			}
			else
			{
				int update_count = prep.getUpdateCount();
				
				logSave.log("ִ�гɹ�����"+update_count+"����¼���ı�");
				return "SQL���ִ�гɹ�\n"+update_count+"�����ݱ��ı�";
			}
		}
		catch (SQLException e)
		{
			logSave.log("ִ��ʧ�ܣ�������Ϣ��\n"+e.getMessage());
			return e.getMessage();
		}
	}
	
	public void newDB(String name)
	{
		logSave.log("�����½����ݿ�["+name+"]");
		try
		{
			prep = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS `"+name+"`;");
			prep.execute();
			logSave.log("�����ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			logSave.log("����ʧ�ܣ�������Ϣ��\n"+e.getMessage());
			
			JOptionPane.showMessageDialog(null, "�������ݿⷢ������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean[] getType(String db_name, String table_name)
	{
		logSave.log("��ѯ����������["+db_name+"."+table_name+"]");
		ArrayList<Boolean> list = new ArrayList<>();
		try
		{
			prep = conn.prepareStatement("SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE table_name = '"+table_name+"' AND table_schema = '"+db_name+"';");
			ResultSet resultSet = prep.executeQuery();
			while(resultSet.next())
			{
				String t = resultSet.getString(1);
				if(t.equals("tinyint") || t.equals("smallint") || t.equals("mediumint") || t.equals("int") || t.equals("integer") || t.equals("bigint") || t.equals("float") || t.equals("double") || t.equals("decimal"))
				{
					list.add(true);
				}
				else
				{
					list.add(false);
				}
			}
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "��ȡ���ݿ����Ϣ��������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
		
		if(list.isEmpty())
		{
			return null;
		}
		else
		{
			boolean[] res = new boolean[list.size()];
			for(int i=0;i<list.size();i++)
			{
				res[i]=list.get(i);
			}
			return res;
		}
	}
	
	public void deleteData(String db_name, String table_name, String[] src_column_list, String[] src_var_list, boolean[] type)
	{
		logSave.log("ɾ����¼����["+db_name+"."+table_name+"]�е�"+src_column_list[0]+" = "+src_var_list[0]+"������");
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			String sql = "DELETE FROM `"+table_name+"` WHERE ";
			
			for(int i=0;i<src_column_list.length;i++)
			{
				if(type[i])
				{
					sql = sql + "`"+src_column_list[i]+"` = "+src_var_list[i]+" ";
				}
				else
				{
					sql = sql + "`"+src_column_list[i]+"` = '"+src_var_list[i]+"' ";
				}
				
				if(i!=src_column_list.length-1)
				{
					sql+="AND ";
				}
			}
			
			sql+=";";
			
//			System.out.println(sql);
			
			prep.execute(sql);
			
			logSave.log("ɾ���ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("ɾ��ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "ɾ�����ݿⷢ������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public void update(String db_name, String table_name, String[] src_column_list, String[] src_var_list, String dst_value, int index, boolean[] type)
	{
		logSave.log("���¼�¼����["+db_name+"."+table_name+"]�е�"+src_column_list[index]+" = "+src_var_list[index]+" �޸�Ϊ��"+dst_value);
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			String sql = null;
			if(type[index])
			{
				sql = "UPDATE `"+table_name+"` SET `"+src_column_list[index]+"` = "+dst_value+" WHERE ";
				
			}
			else
			{
				sql = "UPDATE `"+table_name+"` SET `"+src_column_list[index]+"` = '"+dst_value+"' WHERE ";
			}
			
			for(int i=0;i<src_column_list.length;i++)
			{
				if(i!=index)
				{
					if(type[i])
					{
						sql = sql + "`"+src_column_list[i]+"` = "+src_var_list[i]+" ";
					}
					else
					{
						sql = sql + "`"+src_column_list[i]+"` = '"+src_var_list[i]+"' ";
					}
					
					if(i!=src_column_list.length-1)
					{
						sql+="AND ";
					}
				}
			}
			
			sql+=";";
			
//			System.out.println(sql);
			
			prep.execute(sql);
			logSave.log("���³ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("����ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "���������ݿⷢ������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
	}
	
//	public void renameDB(String old_name, String new_name)
//	{
//		try
//		{
//			prep = conn.prepareStatement("RENAME DATABASE `"+old_name+"` TO `"+new_name+"`;");
//			prep.execute();
//		}
//		catch (SQLException e)
//		{
//			final int MAX_LENGTH=50;
//			String temp = e.getMessage();
//			String error = "";
//			while(temp.length()>MAX_LENGTH)
//			{
//				error = error + temp.substring(0, MAX_LENGTH) + "\n";
//				temp = temp.substring(MAX_LENGTH + 1, temp.length());
//			}
//			
//			error = error + temp;
//			
//			JOptionPane.showMessageDialog(null, "���������ݿⷢ������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
//		}
//	}
	
	public void removeTable(String db_name, String table_name)
	{
		logSave.log("ɾ�����["+db_name+"."+table_name+"]");
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			prep.execute("DROP TABLE `"+table_name+"`;");
			logSave.log("ɾ���ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("ɾ��ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "ɾ����������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void removeDB(String name)
	{
		logSave.log("ɾ�����ݿ�"+name);
		try
		{
			prep = conn.prepareStatement("DROP DATABASE `"+name+"`;");
			prep.execute();
			logSave.log("ɾ���ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("ɾ��ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "ɾ�����ݿⷢ������\n"+error,"����",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public ResultSet getDatabases()
	{
		logSave.log("��ȡ���ݿ��б�");
		try
		{
			prep = conn.prepareStatement("show databases;");
			return prep.executeQuery();
		}
		catch (SQLException e)
		{
			logSave.log("��ȡʧ��");
			return null;
		}
	}
	
	public String connect()
	{
		logSave.log("(��½MySQL)"+username+"@"+url+":"+port+"(PASSWORD:"+password+")");
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", username, password);
			if(!conn.isClosed())
			{
				logSave.log("��½�ɹ�");
				return null;
			}
			else
			{
				logSave.log("��½ʧ�ܣ�����δ֪����");
				return "����δ֪����";
			}
		}
		catch(Exception exception)
		{			
			final int MAX_LENGTH=50;
			String temp = exception.getMessage();
			logSave.log("��½ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			return error;
		}
	}
	
	public String getPasswd(String username)
	{
		logSave.log("��ȡ�û�["+username+"]������");
		String passwd = "";
		try
		{
			prep = conn.prepareStatement("SELECT authentication_string FROM mysql.user WHERE User = '"+username+"';");
			ResultSet resultSet = prep.executeQuery();
			
			while(resultSet.next())
			{
				passwd = resultSet.getString(1);
			}
			
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "��ȡ�û���Ϣʧ��",JOptionPane.ERROR_MESSAGE);
		}
		
		return passwd;
	}
	
	public boolean changePasswd(String username, String passwd)
	{
		logSave.log("�޸��û�["+username+"]������Ϊ: "+passwd);
		try
		{
			prep = conn.prepareStatement("SET PASSWORD FOR '"+username+"'@'localhost' = PASSWORD('"+passwd+"');");
			prep.execute();
			JOptionPane.showMessageDialog(null, "�޸ĳɹ�", "�޸Ľ��",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("�޸ĳɹ�");
			return true;
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("�޸�ʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "�����޸�ʧ��",JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
	}
	
	public String[] getColumns(String db_name, String table_name)
	{
		logSave.log("��ѯ��["+db_name+"."+table_name+"]����������Ϣ");
		ArrayList<String> list = new ArrayList<>();
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			ResultSet resultSet = prep.executeQuery("SHOW COLUMNS FROM `"+table_name+"`;");
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "��ȡ���ݿ��["+db_name+"."+table_name+"]������ʧ��",JOptionPane.ERROR_MESSAGE);
		}
		
		if(list.isEmpty())
		{
			return null;
		}
		else
		{
			return (String[])list.toArray(new String[list.size()]);
		}
	}
	
	public String[][] getRows(String db_name, String table_name)
	{
		logSave.log("��ѯ��["+db_name+"."+table_name+"]����������Ϣ");
		
		String[][] result = null;
		
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			ResultSet resultSet = prep.executeQuery("SELECT * FROM `"+table_name+"`;");
			
			int length_of_column = resultSet.getMetaData().getColumnCount();
			resultSet.last();
			int length_of_row = resultSet.getRow();
			
			result = new String[length_of_row][length_of_column];
			
			for(int i=0;i<length_of_row;i++)
			{
				for(int j=0;j<length_of_column;j++)
				{
					resultSet.absolute(i+1);
					result[i][j]=resultSet.getString(j+1);
				}
			}
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "��ȡ���ݿ��["+db_name+"."+table_name+"]�ļ�¼ʧ��",JOptionPane.ERROR_MESSAGE);
		}
		
		return result;
	}
	
	public String[] getTableList(String name)
	{
		logSave.log("��ѯ���ݿ�["+name+"]�����б�");
		
		ArrayList<String> list = new ArrayList<>();
		try
		{
//			prep = conn.prepareStatement("SELECT `TABLE_NAME` FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_SCHEMA` = `"+name+"` ;");
			prep = conn.prepareStatement("USE `"+name+"`;");
			prep.execute();
			ResultSet resultSet = prep.executeQuery("SHOW TABLES;");
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "��ȡ���ݿ�["+name+"]�ı�ʧ��",JOptionPane.ERROR_MESSAGE);
		}
		
		if(list.isEmpty())
		{
			return new String[] {};
		}
		else
		{
			return (String[])list.toArray(new String[list.size()]);
		}
	}
	
	public String[] getUserList()
	{
		logSave.log("��ѯ�û��б�");
		ArrayList<String> list = new ArrayList<>();
		try
		{
			prep = conn.prepareStatement("SELECT User FROM mysql.user;");
			ResultSet resultSet = prep.executeQuery();
			while(resultSet.next())
			{
				String username = resultSet.getString(1);
				if(!Pattern.matches("mysql\\..*?", username))
				{
					list.add(username);
				}
			}
			logSave.log("��ѯ�ɹ�");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("��ѯʧ�ܣ�������Ϣ��\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "������Ϣ��\n"+error, "��ȡ�û���Ϣʧ��",JOptionPane.ERROR_MESSAGE);
		}
		
		if(list.isEmpty())
		{
			return new String[] {};
		}
		else
		{
			return (String[])list.toArray(new String[list.size()]);
		}
	}
}
