package win;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import data.LogSave;

public class LogDialog extends JDialog
{
	private LogSave logSave = null;
	
	public LogDialog(JFrame parent)
	{
		super(parent);
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}

	private void InitContent()
	{
		//单例模式获取日志类对象
		logSave = LogSave.getLog();
		
		Font font = new Font("微软雅黑", Font.PLAIN, 18);
		
		//添加背景
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//初始化元素
		JTextArea text = new JTextArea();
		text.setFont(font);
		JScrollPane jScrollPane = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(1,1,792,563);
		background.add(jScrollPane);
		
		//获取内容
		text.setText(logSave.toString());
	}

	private void InitDialog()
	{
		setModal(true);
		setTitle("AgSQL 日志");
		setSize(848, 645);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_backup.png")).getImage());
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
