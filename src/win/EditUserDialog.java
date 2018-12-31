package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import data.Info;
import sql.MySQLManager;

public class EditUserDialog extends JDialog
{
	private JPasswordField edt_passwd = null;
	private JCheckBox jcb_show_passwd = null;
	
	private MySQLManager sqlManager = null;
	private String username = null;
	
	public EditUserDialog(JFrame parent, MySQLManager sqlManager, String username)
	{
		super(parent);
		this.sqlManager = sqlManager;
		this.username = username;
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}
	
	private void InitContent()
	{
		Font font_b = new Font("΢���ź�", Font.BOLD, 16);
		Font font_p = new Font("΢���ź�", Font.PLAIN, 16);
		
		//��ӱ���
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//���ԭ��
		{
			JLabel label = new JLabel("�û��� ��");
			label.setBounds(100,50,250,30);
			label.setFont(font_b);
			background.add(label);
		}
		
		{
			JLabel label = new JLabel(username);
			label.setBounds(200,50,250,30);
			label.setFont(font_p);
			background.add(label);
		}
		
		{
			JLabel label = new JLabel("���� ��");
			label.setBounds(100,100,250,30);
			label.setFont(font_b);
			background.add(label);
		}
		
		edt_passwd = new JPasswordField();
		edt_passwd.setBounds(200,100,200,30);
		edt_passwd.setFont(font_p);
		background.add(edt_passwd);
		
		{
			jcb_show_passwd = new JCheckBox("��ʾ����");
			jcb_show_passwd.setBounds(100, 150, 200, 30);
			jcb_show_passwd.setFont(font_p);
			jcb_show_passwd.setBackground(Color.white);
			background.add(jcb_show_passwd);
			
			jcb_show_passwd.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(jcb_show_passwd.isSelected())
					{
						edt_passwd.setEchoChar((char) 0);
					}
					else
					{
						edt_passwd.setEchoChar('��');
					}
				}
			});
		}
		
		JButton btn_update = new JButton("�޸�");
		btn_update.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_edit.png"), 30, 30));
		btn_update.setBounds(100, 220, 120, 35);
		background.add(btn_update);
		
		JButton btn_cancel = new JButton("ȡ��");
		btn_cancel.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_cancel.png"), 30, 30));
		btn_cancel.setBounds(280, 220, 120, 35);
		background.add(btn_cancel);
		
		//���ð�ť������
		btn_update.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(sqlManager.changePasswd(username, String.valueOf(edt_passwd.getPassword())))
				{
					closeDialog();
				}
			}
		});
		
		btn_cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				closeDialog();
			}
		});
		
		//��ʼ������
		initPasswd();
	}

	private void closeDialog()
	{
		dispose();
	}
	
	private void initPasswd()
	{
		edt_passwd.setText(sqlManager.getPasswd(username));
	}
	
	private void InitDialog()
	{
		setModal(true);
		setTitle("�༭�û�");
		setSize(548, 395);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_edit.png")).getImage());
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
}
