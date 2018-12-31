package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import sql.MySQLManager;

public class ShellWin extends JDialog
{
	private MySQLManager mySQLManager = null;
	
	private JTextField shell_cmd = null;
	private JTextArea shell_content = null;
	
	
	public ShellWin(JFrame parent, MySQLManager sqlManager)
	{
		super(parent);
		mySQLManager = sqlManager;
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}
	
	private void exce()
	{
		String cmd = shell_cmd.getText();
		shell_cmd.setText("");
		
		shell_content.setText(mySQLManager.exce(cmd));
	}
	
	private void InitContent()
	{
		Font font = new Font("微软雅黑", Font.BOLD, 16);
		
		//添加背景
		JPanel background = new JPanel();
		background.setBackground(Color.black);
		background.setLayout(null);
		add(background);
		
		//显示按钮
		JButton btn_enter = new JButton("执行");
		btn_enter.setBounds(642,1,150,35);
		background.add(btn_enter);
		btn_enter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				exce();
			}
		});
		
		//显示控制台
		shell_cmd = new JTextField();
		shell_cmd.setBounds(1,1,641,35);
		shell_cmd.setFont(font);
		shell_cmd.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					exce();
					return;
				}
				super.keyPressed(e);
			}
		});
		background.add(shell_cmd);
		
		shell_content = new JTextArea();
		shell_content.setFont(font);
		shell_content.setLineWrap(true);
		shell_content.setWrapStyleWord(true);
		JScrollPane jScrollPane = new JScrollPane(shell_content, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setBounds(1,36,792,527);
		background.add(jScrollPane);
		
		//控制台初始化
		shell_cmd.setText("这里输入SQL语句");
		shell_content.setText("[AgSQL for MySQL Shell]");
	}


	private void InitDialog()
	{
		setModal(true);
		setTitle("AgSQL - MySQL 控制台");
		setSize(848, 645);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_shell.png")).getImage());
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
