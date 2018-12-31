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
			JOptionPane.showMessageDialog(NewLinkDialog.this, "�˿���������ȷ������", "����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		operate_Connect(edt_url.getText(), port, edt_username.getText(),String.valueOf(edt_password.getPassword()));
	}
	
	/**
	 * ���Ӳ���
	 */
	private void operate_ConnectTest(String url, int port, String username, String password)
	{
//		System.out.println("����");
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
				JOptionPane.showMessageDialog(null, "[�ɹ�]���Ӳ������");
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
			
			JOptionPane.showMessageDialog(NewLinkDialog.this, "������Ϣ��\n"+error, "����ʧ��",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * ��ʼ����
	 */
	private void operate_Connect(String url, int port, String username, String password)
	{
//		System.out.println("��ʽ����");
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
			JOptionPane.showMessageDialog(NewLinkDialog.this, "������Ϣ��\n"+result, "����ʧ��",JOptionPane.ERROR_MESSAGE);
			isConnected = false;
		}
		dispose();
	}
	
	/**
	 * ��ʼ������
	 */
	private void InitContent()
	{
		Font font = new Font("΢���ź�", Font.BOLD, 16);
		
		//��ӱ���
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//���ԭ��
		JLabel hint = new JLabel("����ȷ�������ӵ���MySQL��");
		hint.setBounds(125,10,250,30);
		hint.setFont(font);
		background.add(hint);
		
		edt_url = new JTextField();
		edt_url.setBounds(200,58,250,30);
		edt_url.setFont(font);
		edt_url.setText("localhost");
		background.add(edt_url);
		{
			JLabel label = new JLabel("������ | IP��ַ ��");
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
			JLabel label = new JLabel("�˿� ��");
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
			JLabel label = new JLabel("�û��� ��");
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
			JLabel label = new JLabel("���� ��");
			label.setFont(font);
			label.setBounds(30,210,150,25);
			background.add(label);
		}
		
		JButton btn_test = new JButton("����");
		btn_test.setBounds(30,290,200,35);
		background.add(btn_test);
		
		JButton btn_connect = new JButton("����");
		btn_connect.setBounds(250,290,200,35);
		background.add(btn_connect);
		
		//��Ӱ�ť������
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
					JOptionPane.showMessageDialog(NewLinkDialog.this, "�˿���������ȷ������", "����",JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(NewLinkDialog.this, "�˿���������ȷ������", "����",JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				operate_Connect(edt_url.getText(), port, edt_username.getText(),String.valueOf(edt_password.getPassword()));
			}
		});
	}

	/**
	 * ��ʼ������
	 */
	private void InitDialog()
	{
		setModal(true);
		setTitle("�½�����");
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
	 * ����SQL������
	 * ���Ϊnull��SQL��������Ч
	 * ����Ϊ��Ч��SQL������
	 * @return <b>MySQLManager</b> sqlManager // <b>SQL������</b>
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
