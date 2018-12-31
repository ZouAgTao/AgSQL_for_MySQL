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
		//����ģʽ��ȡ��־�����
		logSave = LogSave.getLog();
		
		Font font = new Font("΢���ź�", Font.PLAIN, 18);
		
		//��ӱ���
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//��ʼ��Ԫ��
		JTextArea text = new JTextArea();
		text.setFont(font);
		JScrollPane jScrollPane = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(1,1,792,563);
		background.add(jScrollPane);
		
		//��ȡ����
		text.setText(logSave.toString());
	}

	private void InitDialog()
	{
		setModal(true);
		setTitle("AgSQL ��־");
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
