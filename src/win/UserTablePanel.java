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
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import data.Info;
import sql.MySQLManager;

public class UserTablePanel extends TablePanel
{
	private MySQLManager sqlManager = null;
	private JList<String> user_list = null;
	private JButton btn_edit = null;
	private String selectUserName = null;
	private JFrame parent = null;
	
	public UserTablePanel(JFrame parent)
	{
		super();
		this.parent = parent;
		
		Init();
	}
	
	private void operate_editUser()
	{
		new EditUserDialog(parent, sqlManager, selectUserName);
		initTable();
	}
	
	private void operate_addUser()
	{
		String name= JOptionPane.showInputDialog("请输入用户的名字");
		
		if(name==null || name=="")
		{
			return;
		}
		if(!Pattern.matches("[A-Za-z0-9_]+", name))
		{
			JOptionPane.showMessageDialog(this, "请输入合法的用户名","格式错误",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			if(sqlManager!=null)
			{
				sqlManager.addUser(name);
			}
		}
		
		initTable();
	}
	
	private void operate_removeUser()
	{
		int value = JOptionPane.showConfirmDialog(this, "确认是否删除用户["+selectUserName+"]?", "提示",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(value==0)
		{
			sqlManager.removeUser(selectUserName);
			initTable();
		}
	}
	
	private void initTable()
	{
		user_list.setListData(sqlManager.getUserList());
	}
	
	private void Init()
	{
		//编辑按钮
		btn_edit = new JButton("编辑用户");
		btn_edit.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_edit.png"), 30, 30));
		btn_edit.setBounds(20,10,130,35);
		btn_edit.setEnabled(false);
		add(btn_edit);
		
		//新建按钮
		JButton btn_add = new JButton("新建用户");
		btn_add.setBounds(180,10,130,35);
		btn_add.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_adduser.png"), 30, 30));
		add(btn_add);
		
		//删除按钮
		JButton btn_remove = new JButton("删除用户");
		btn_remove.setBounds(330,10,130,35);
		btn_remove.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_remove.png"), 30, 30));
		add(btn_remove);
		
		//设置按钮监听器
		btn_edit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_editUser();
			}
		});
		
		btn_add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_addUser();
			}
		});
		
		btn_remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_removeUser();
			}
		});
		
		//分隔符号
		{
			JPanel line = new JPanel();
			line.setBackground(Color.gray);
			line.setBounds(0,55,768,1);
			add(line);
		}
		
		//用户表格
		user_list = new JList<>(new String[]{"  测试数据","  测试数据","  测试数据","  测试数据","  测试数据"});
		user_list.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		JScrollPane jScrollPane = new JScrollPane(user_list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(1, 59, 766, 546);
		add(jScrollPane);
		
		//用户表格事件
		user_list.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e) 
            {
                int i = user_list.getSelectedIndex();
                
                if(i!=-1)
                {
                	btn_edit.setEnabled(true);
                	selectUserName = user_list.getSelectedValue();
                }
                else
                {
                	btn_edit.setEnabled(false);
                	selectUserName = null;
                }
            }
		});
	}
	
	public void setSQLManager(MySQLManager mySQLManager)
	{
		sqlManager = mySQLManager;
		if(sqlManager!=null)
		{
			initTable();
		}
		else
		{
			user_list.setListData(new String[] {});
		}
	}
}
