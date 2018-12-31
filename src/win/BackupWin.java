package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import data.Info;
import sql.MySQLManager;

public class BackupWin extends JDialog
{
	private JButton btn_backup = null;
	private JList<String> table_list = null;
	
	private MySQLManager sqlManager = null;
	
	private String db_name = null;
	
	public BackupWin(JFrame parent, MySQLManager mySQLManager)
	{
		super(parent);
		
		this.sqlManager = mySQLManager;
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}
	
	private void InitTable()
	{
		ResultSet resultSet = sqlManager.getDatabases();
		if(resultSet!=null)
		{
			try
			{
				resultSet.last();
				String[] list = new String[resultSet.getRow()];
				
				for(int i=1;i<=list.length;i++)
				{
					resultSet.absolute(i);
					list[i-1]=resultSet.getString(1);
				}
				table_list.setListData(list);
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
				
				JOptionPane.showMessageDialog(null, "获取数据库发生错误：\n"+error,"错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			table_list.setListData(new String[] {});
		}
	}
	
	private void InitContent()
	{
		//添加背景
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//初始化按钮
		btn_backup = new JButton("备份");
		btn_backup.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_backup.png"), 30, 30));
		btn_backup.setBounds(10, 10, 120, 35);
		btn_backup.setEnabled(false);
		background.add(btn_backup);
		
		//初始化表格
		table_list = new JList<>(new String[]{"  测试数据","  测试数据","  测试数据","  测试数据","  测试数据"});
		table_list.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		JScrollPane jScrollPane = new JScrollPane(table_list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(1, 50, 792, 513);
		background.add(jScrollPane);
		
		//初始化表格监听器
		table_list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				int index = table_list.getSelectedIndex();
				if(index !=-1)
				{
					btn_backup.setEnabled(true);
					
					db_name = table_list.getSelectedValue();
				}
			}
			
		});
		
		//初始化表格
		InitTable();
		
		//初始化按钮监听器
		btn_backup.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				startBackup();
				
				InitTable();
				
				btn_backup.setEnabled(false);
				table_list.clearSelection();
			}
		});
		
	}
	
	private void startBackup()
	{
		JOptionPane.showMessageDialog(this, "即将开始备份数据库["+db_name+"]","提示",JOptionPane.INFORMATION_MESSAGE);
		if(sqlManager.backUPDB(db_name))
		{
			JOptionPane.showMessageDialog(this, "备份成功","提示",JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "备份失败","提示",JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void InitDialog()
	{
		setModal(true);
		setTitle("选择需要备份的数据库");
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
