package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sql.MySQLManager;

public class NewLinkDialog extends JDialog
{
	private boolean isConnected = false;
	
	private MySQLManager sqlManager = null;
	
	private JTextField edt_url = null;
	private JTextField edt_port = null;
	private JTextField edt_username = null;
	private JPasswordField edt_password = null;
	
	public NewLinkDialog(JFrame parent)
	{
		super(parent);
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}
	
	private void press_enter()
	{
		int port = 3306;
		try
		{
			port = Integer.parseInt(edt_port.getText());
			if(port<1 || port >65535)
			{
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException exception)
		{
			JOptionPane.showMessageDialog(NewLinkDialog.this, "端口请输入正确的数字", "警告",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		operate_Connect(edt_url.getText(), port, edt_username.getText(),String.valueOf(edt_password.getPassword()));
	}
	
	/**
	 * 连接测试
	 */
	private void operate_ConnectTest(String url, int port, String username, String password)
	{
//		System.out.println("测试");
//		System.out.println(url);
//		System.out.println(port);
//		System.out.println(username);
//		System.out.println(password);
		
		Connection conn = null;
		
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", username, password);
			if(!conn.isClosed())
			{
				JOptionPane.showMessageDialog(null, "[成功]连接测试完毕");
				conn.close();
			}
		}
		catch(Exception exception)
		{			
			final int MAX_LENGTH=50;
			String temp = exception.getMessage();
			String error = "";
			while(temp.length()>MAX_LENGTH)
			{
				error = error + temp.substring(0, MAX_LENGTH) + "\n";
				temp = temp.substring(MAX_LENGTH + 1, temp.length());
			}
			
			error = error + temp;
			
			JOptionPane.showMessageDialog(NewLinkDialog.this, "错误信息：\n"+error, "连接失败",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 开始连接
	 */
	private void operate_Connect(String url, int port, String username, String password)
	{
//		System.out.println("正式连接");
//		System.out.println(url);
//		System.out.println(port);
//		System.out.println(username);
//		System.out.println(password);
		
		sqlManager = new MySQLManager(url, port, username, password);
		String result = sqlManager.connect();
		
		if(result==null)
		{
			isConnected = true;
		}
		else
		{
			JOptionPane.showMessageDialog(NewLinkDialog.this, "错误信息：\n"+result, "连接失败",JOptionPane.ERROR_MESSAGE);
			isConnected = false;
		}
		dispose();
	}
	
	/**
	 * 初始化内容
	 */
	private void InitContent()
	{
		Font font = new Font("微软雅黑", Font.BOLD, 16);
		
		//添加背景
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//添加原件
		JLabel hint = new JLabel("【请确保你连接的是MySQL】");
		hint.setBounds(125,10,250,30);
		hint.setFont(font);
		background.add(hint);
		
		edt_url = new JTextField();
		edt_url.setBounds(200,58,250,30);
		edt_url.setFont(font);
		edt_url.setText("localhost");
		background.add(edt_url);
		{
			JLabel label = new JLabel("主机名 | IP地址 ：");
			label.setFont(font);
			label.setBounds(30,60,150,25);
			background.add(label);
		}
		
		edt_port = new JTextField();
		edt_port.setBounds(200,108,250,30);
		edt_port.setFont(font);
		edt_port.setText("3306");
		background.add(edt_port);
		{
			JLabel label = new JLabel("端口 ：");
			label.setFont(font);
			label.setBounds(30,110,150,25);
			background.add(label);
		}
		
		edt_username = new JTextField();
		edt_username.setBounds(200,158,250,30);
		edt_username.setFont(font);
		edt_username.setText("root");
		background.add(edt_username);
		{
			JLabel label = new JLabel("用户名 ：");
			label.setFont(font);
			label.setBounds(30,160,150,25);
			background.add(label);
		}
		
		edt_password = new JPasswordField();
		edt_password.setBounds(200,208,250,30);
		edt_password.setFont(font);
		edt_password.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				super.keyPressed(e);
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					press_enter();
				}
			}
		});
		background.add(edt_password);
		{
			JLabel label = new JLabel("密码 ：");
			label.setFont(font);
			label.setBounds(30,210,150,25);
			background.add(label);
		}
		
		JButton btn_test = new JButton("测试");
		btn_test.setBounds(30,290,200,35);
		background.add(btn_test);
		
		JButton btn_connect = new JButton("连接");
		btn_connect.setBounds(250,290,200,35);
		background.add(btn_connect);
		
		//添加按钮监听器
		btn_test.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int port = 3306;
				try
				{
					port = Integer.parseInt(edt_port.getText());
					if(port<1 || port >65535)
					{
						throw new NumberFormatException();
					}
				}
				catch (NumberFormatException exception)
				{
					JOptionPane.showMessageDialog(NewLinkDialog.this, "端口请输入正确的数字", "警告",JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				operate_ConnectTest(edt_url.getText(), port, edt_username.getText(),String.valueOf(edt_password.getPassword()));
			}
		});
		
		btn_connect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int port = 3306;
				try
				{
					port = Integer.parseInt(edt_port.getText());
					if(port<1 || port >65535)
					{
						throw new NumberFormatException();
					}
				}
				catch (NumberFormatException exception)
				{
					JOptionPane.showMessageDialog(NewLinkDialog.this, "端口请输入正确的数字", "警告",JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				operate_Connect(edt_url.getText(), port, edt_username.getText(),String.valueOf(edt_password.getPassword()));
			}
		});
	}

	/**
	 * 初始化窗体
	 */
	private void InitDialog()
	{
		setModal(true);
		setTitle("新建连接");
		setSize(548, 452);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_newlink.png")).getImage());
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//		try
//		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 返回SQL管理器
	 * 如果为null则SQL管理器无效
	 * 否则为有效的SQL管理器
	 * @return <b>MySQLManager</b> sqlManager // <b>SQL管理器</b>
	 */
	public MySQLManager getSQLManager()
	{
		if(isConnected)
		{
			return sqlManager;
		}
		else
		{
			return null;
		}
	}
}
