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
		logSave.log("开始导入数据库");
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
		chooser.showDialog(new JLabel(), "导入SQL文件");
		
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
			
			logSave.log("导入成功");
			
			return true;
		}
		catch (Exception e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("导入失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "导入失败，错误信息：：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public void addUser(String name)
	{
		logSave.log("开始新建用户");
		
		String passwd= JOptionPane.showInputDialog("请输入密码");
		String temp= JOptionPane.showInputDialog("请再输入一次密码");
		if(passwd==null || temp==null || passwd.equals("") || temp.equals(""))
		{
			JOptionPane.showMessageDialog(null, "新建用户取消", "提示",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if(!passwd.equals(temp))
		{
			JOptionPane.showMessageDialog(null, "两次输入密码不一致", "提示",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try
		{
			prep = conn.prepareStatement("CREATE USER '"+name+"'@'"+url+"' IDENTIFIED BY '"+passwd+"';");
			prep.execute();
			JOptionPane.showMessageDialog(null, "新建用户["+name+"]成功", "提示",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("新建用户成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			temp = e.getMessage();
			logSave.log("新建用户失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void removeUser(String name)
	{
		logSave.log("开始删除用户");
		
		if(name.equals("root"))
		{
			JOptionPane.showMessageDialog(null, "处于安全性考虑，不支持删除root用户", "提示",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try
		{
			prep = conn.prepareStatement("DROP USER '"+name+"'@'"+url+"';");
			prep.execute();
			JOptionPane.showMessageDialog(null, "删除用户["+name+"]成功", "提示",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("删除用户成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("删除用户失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public boolean backUPDB(String db_name)
	{
		logSave.log("开始备份数据库");
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
			chooser.showDialog(new JLabel(), "导出SQL文件");
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
			
			logSave.log("备份成功");
			
			return true;
		}
		catch (IOException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("备份失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public String exce(String sql)
	{
		logSave.log("执行"+sql);
		try
		{
			prep = conn.prepareStatement(sql);
			if(prep.execute())
			{
				StringBuilder str = new StringBuilder("");
				
				ResultSet resultSet = prep.getResultSet();
				int columnCount = resultSet.getMetaData().getColumnCount();
				
				str.append("列名：\n\n");
				for (int i = 1; i <= columnCount; i++)
				{
					str.append("["+resultSet.getMetaData().getColumnName(i)+"]"+" ");
				}
				str.append("\n\n\n\n");
				
				str.append("字段： \n\n");
				while(resultSet.next())
				{
					str.append("{\n\n");
					for (int i = 1; i <= columnCount; i++)
					{
						str.append("["+resultSet.getObject(i)+"]\n\n");
					}
					str.append("}\n\n");
				}
				
				logSave.log("执行成功，有结果返回");
				return str.toString();
			}
			else
			{
				int update_count = prep.getUpdateCount();
				
				logSave.log("执行成功，有"+update_count+"条记录被改变");
				return "SQL语句执行成功\n"+update_count+"条内容被改变";
			}
		}
		catch (SQLException e)
		{
			logSave.log("执行失败，错误信息：\n"+e.getMessage());
			return e.getMessage();
		}
	}
	
	public void newDB(String name)
	{
		logSave.log("尝试新建数据库["+name+"]");
		try
		{
			prep = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS `"+name+"`;");
			prep.execute();
			logSave.log("创建成功");
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
			
			logSave.log("创建失败，错误信息：\n"+e.getMessage());
			
			JOptionPane.showMessageDialog(null, "创建数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean[] getType(String db_name, String table_name)
	{
		logSave.log("查询所有列类型["+db_name+"."+table_name+"]");
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
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "获取数据库表信息发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
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
		logSave.log("删除记录，在["+db_name+"."+table_name+"]中的"+src_column_list[0]+" = "+src_var_list[0]+"所在行");
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
			
			logSave.log("删除成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("删除失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "删除数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public void update(String db_name, String table_name, String[] src_column_list, String[] src_var_list, String dst_value, int index, boolean[] type)
	{
		logSave.log("更新记录，在["+db_name+"."+table_name+"]中的"+src_column_list[index]+" = "+src_var_list[index]+" 修改为："+dst_value);
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
			logSave.log("更新成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("更新失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "重命名数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
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
//			JOptionPane.showMessageDialog(null, "重命名数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
//		}
//	}
	
	public void removeTable(String db_name, String table_name)
	{
		logSave.log("删除表格["+db_name+"."+table_name+"]");
		try
		{
			prep = conn.prepareStatement("USE `"+db_name+"`;");
			prep.execute();
			prep.execute("DROP TABLE `"+table_name+"`;");
			logSave.log("删除成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("删除失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "删除表发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void removeDB(String name)
	{
		logSave.log("删除数据库"+name);
		try
		{
			prep = conn.prepareStatement("DROP DATABASE `"+name+"`;");
			prep.execute();
			logSave.log("删除成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("删除失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "删除数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public ResultSet getDatabases()
	{
		logSave.log("获取数据库列表");
		try
		{
			prep = conn.prepareStatement("show databases;");
			return prep.executeQuery();
		}
		catch (SQLException e)
		{
			logSave.log("获取失败");
			return null;
		}
	}
	
	public String connect()
	{
		logSave.log("(登陆MySQL)"+username+"@"+url+":"+port+"(PASSWORD:"+password+")");
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", username, password);
			if(!conn.isClosed())
			{
				logSave.log("登陆成功");
				return null;
			}
			else
			{
				logSave.log("登陆失败，发生未知错误");
				return "发生未知错误";
			}
		}
		catch(Exception exception)
		{			
			final int MAX_LENGTH=50;
			String temp = exception.getMessage();
			logSave.log("登陆失败，错误信息：\n"+temp);
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
		logSave.log("获取用户["+username+"]的密码");
		String passwd = "";
		try
		{
			prep = conn.prepareStatement("SELECT authentication_string FROM mysql.user WHERE User = '"+username+"';");
			ResultSet resultSet = prep.executeQuery();
			
			while(resultSet.next())
			{
				passwd = resultSet.getString(1);
			}
			
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "获取用户信息失败",JOptionPane.ERROR_MESSAGE);
		}
		
		return passwd;
	}
	
	public boolean changePasswd(String username, String passwd)
	{
		logSave.log("修改用户["+username+"]的密码为: "+passwd);
		try
		{
			prep = conn.prepareStatement("SET PASSWORD FOR '"+username+"'@'localhost' = PASSWORD('"+passwd+"');");
			prep.execute();
			JOptionPane.showMessageDialog(null, "修改成功", "修改结果",JOptionPane.INFORMATION_MESSAGE);
			logSave.log("修改成功");
			return true;
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("修改失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "密码修改失败",JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
	}
	
	public String[] getColumns(String db_name, String table_name)
	{
		logSave.log("查询表["+db_name+"."+table_name+"]的所有列信息");
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
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "获取数据库表["+db_name+"."+table_name+"]的列名失败",JOptionPane.ERROR_MESSAGE);
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
		logSave.log("查询表["+db_name+"."+table_name+"]的所有行信息");
		
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
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "获取数据库表["+db_name+"."+table_name+"]的记录失败",JOptionPane.ERROR_MESSAGE);
		}
		
		return result;
	}
	
	public String[] getTableList(String name)
	{
		logSave.log("查询数据库["+name+"]的所有表");
		
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
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "获取数据库["+name+"]的表失败",JOptionPane.ERROR_MESSAGE);
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
		logSave.log("查询用户列表");
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
			logSave.log("查询成功");
		}
		catch (SQLException e)
		{
			final int MAX_LENGTH=50;
			String temp = e.getMessage();
			logSave.log("查询失败，错误信息：\n"+temp);
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(null, "错误信息：\n"+error, "获取用户信息失败",JOptionPane.ERROR_MESSAGE);
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
