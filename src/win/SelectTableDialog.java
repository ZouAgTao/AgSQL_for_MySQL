package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

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

public class SelectTableDialog extends JDialog
{
	private String table_name = null;
	private String final_table_name = null;
	private String db_name = null;
	private MySQLManager sqlManager = null;
	
	private JList<String> table_list = null;
	private JButton btn_view = null;
	private JButton btn_edit = null;
	private JButton btn_del = null;
	
	public String getTableName()
	{
		return final_table_name;
	}
	
	public SelectTableDialog(JFrame parent, String dbname, MySQLManager mySQLManager)
	{
		super(parent);
		this.db_name = dbname;
		this.sqlManager = mySQLManager;
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}



	private void InitContent()
	{
//		Font font = new Font("微软雅黑", Font.BOLD, 16);
		
		//添加背景
		JPanel background = new JPanel();
		background.setBackground(Color.white);
		background.setLayout(null);
		add(background);
		
		//添加原件
		btn_view = new JButton("查阅表");
		btn_view.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_table.png"), 30, 30));
		btn_view.setBounds(10, 10, 120, 35);
		btn_view.setEnabled(false);
		background.add(btn_view);
		
		btn_edit = new JButton("修改表");
		btn_edit.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_edit.png"), 30, 30));
		btn_edit.setBounds(160, 10, 120, 35);
		btn_edit.setEnabled(false);
		background.add(btn_edit);
		
		JButton btn_add = new JButton("增加表");
		btn_add.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_newlink.png"), 30, 30));
		btn_add.setBounds(310, 10, 120, 35);
		background.add(btn_add);
		
		btn_del = new JButton("删除表");
		btn_del.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_cancel.png"), 30, 30));
		btn_del.setBounds(450, 10, 120, 35);
		btn_del.setEnabled(false);
		background.add(btn_del);
		
		//显示table列表
		table_list = new JList<>(new String[]{"  测试数据","  测试数据","  测试数据","  测试数据","  测试数据"});
		table_list.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		JScrollPane jScrollPane = new JScrollPane(table_list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(1, 50, 590, 314);
		background.add(jScrollPane);
		table_list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				int index = table_list.getSelectedIndex();
				if(index !=-1)
				{
					btn_del.setEnabled(true);
					btn_view.setEnabled(true);
					btn_edit.setEnabled(true);
					
					table_name = table_list.getSelectedValue();
				}
			}
		});
		
		//初始化表格
		initTable();
		
		//设置按钮监听器
		btn_add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String name= JOptionPane.showInputDialog("请输入表的名字");
				if(name==null || name=="")
				{
					return;
				}
				
				if(!Pattern.matches("[A-Za-z0-9_]+", name))
				{
					JOptionPane.showMessageDialog(SelectTableDialog.this, "请输入合法的表名","格式错误",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(SelectTableDialog.this, "测试版不具有这个功能，\n请联联系作者购买正版许可证进行注册就有啦（大概吧）\n使用【工具】-【命令行界面】可以手动编辑表格","提示",JOptionPane.INFORMATION_MESSAGE);
//					sqlManager.newTable(db_name, name);
//					initTable();
				}
			}
		});
		
		btn_del.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int value = JOptionPane.showConfirmDialog(SelectTableDialog.this, "确认是否删除表["+table_name+"]?", "提示",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(value==0)
				{
					sqlManager.removeTable(db_name, table_name);
					initTable();
				}
			}
		});
		
		btn_edit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(SelectTableDialog.this, "测试版不具有这个功能，\n请联联系作者购买正版许可证进行注册就有啦（大概吧）\n使用【工具】-【命令行界面】可以手动编辑表格","提示",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		btn_view.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				final_table_name = table_name;
				closeDialog();
			}
		});
	}
	
	private void closeDialog()
	{
		dispose();
	}
	
	private void initTable()
	{
		table_list.setListData(sqlManager.getTableList(db_name));
	}

	private void InitDialog()
	{
		setModal(true);
		setTitle("["+db_name+"]表管理");
		setSize(648, 452);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_table.png")).getImage());
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
