package win;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import data.Info;
import sql.MySQLManager;

public class MainWin extends JFrame
{
	private MySQLManager mySQLManager = null;
	private JTree jt_dbtree = null;
	private TablePanel[] tablePanels = new TablePanel[2];
	private String item = null;
	private JMenuItem mit_closelink = null;
	
	public MainWin()
	{
		winInit();
		menuInit();
		contentInit();
		
		setVisible(true);
	}
	
	private void newDB()
	{
		String name= JOptionPane.showInputDialog("���������ݿ������");
		if(name==null || name=="")
		{
			return;
		}
		if(!Pattern.matches("[A-Za-z0-9_]+", name))
		{
			JOptionPane.showMessageDialog(this, "������Ϸ������ݿ���","��ʽ����",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			mySQLManager.newDB(name);
			inner_showTree();
		}
	}
	
	private void renameDB(String old_name)
	{
		JOptionPane.showMessageDialog(this, "����MySQL�汾��֧�����ݿ��������Ŷ","��ʾ",JOptionPane.INFORMATION_MESSAGE);
//		String name= JOptionPane.showInputDialog("�������µ�����");
//		if(!Pattern.matches("[A-Za-z0-9_]+", name))
//		{
//			JOptionPane.showMessageDialog(this, "������Ϸ������ݿ���","��ʽ����",JOptionPane.ERROR_MESSAGE);
//		}
//		else
//		{
//			mySQLManager.renameDB(old_name, name);
//			inner_showTree();
//		}
	}
	
	private void removeDB(String name)
	{
		int value = JOptionPane.showConfirmDialog(this, "ȷ���Ƿ�ɾ�����ݿ�["+name+"]?", "��ʾ",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(value==0)
		{
			mySQLManager.removeDB(name);
			inner_showTree();
			((DataTabelPanel)tablePanels[1]).setTableName(null,null);
		}
	}
	
	private void inner_hideAllTable()
	{
		for(int i=0;i<tablePanels.length;i++)
		{
			tablePanels[i].hide();
		}
	}
	
	private void inner_showTable(int index)
	{
		for(int i=0;i<tablePanels.length;i++)
		{
			tablePanels[i].hide();
		}
		
		tablePanels[index].show();
	}
	
	private void operate_openBackupWin()
	{
		if(mySQLManager!=null)
		{
			new BackupWin(this, mySQLManager);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "���ȡ����ӡ���MySQLŶ��(�����)��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void operate_ImportWin()
	{
		if(mySQLManager!=null)
		{
			JOptionPane.showMessageDialog(this, "������ʼ�������ݿ�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
			if(mySQLManager.importDB())
			{
				JOptionPane.showMessageDialog(this, "����ɹ�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
				inner_showTree();
				inner_hideAllTable();
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this, "���ȡ����ӡ���MySQLŶ��(�����)��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private String inner_getDBName()
	{
		if(jt_dbtree!=null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt_dbtree.getLastSelectedPathComponent();
			if(node!=null)
			{
				return node.toString();
			}
			else
			{
				return null;
			}
			
		}
		else
		{
			return null;
		}
	}
	
	private void operate_openTableWin()
	{
		if(mySQLManager!=null)
		{
			((DataTabelPanel)tablePanels[1]).setSQLManager(mySQLManager);
//			((DataTabelPanel)tablePanels[1]).setTableName(null,null);
			inner_showTable(1);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "���ȡ����ӡ���MySQLŶ��(�����)��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void operate_openUserWin()
	{
		if(mySQLManager!=null)
		{
			((UserTablePanel)tablePanels[0]).setSQLManager(mySQLManager);
			inner_showTable(0);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "���ȡ����ӡ���MySQLŶ��(�����)��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void inner_showTree()
	{
		if(mySQLManager!=null)
		{
			ResultSet databases_list = mySQLManager.getDatabases();
			if(databases_list!=null)
			{
				try
				{
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("MySQL");
					DefaultTreeModel model = new DefaultTreeModel(root);
					
					int index = 0;
					
					while(databases_list.next())
					{
						DefaultMutableTreeNode sub = new DefaultMutableTreeNode(databases_list.getString(1));
						model.insertNodeInto(sub, root, index);
						index++;
					}
					
					jt_dbtree.setModel(model);
				}
				catch (SQLException e)
				{
					JOptionPane.showMessageDialog(this, "������Ϣ��\n���ݿ��б��ȡ����", "����",JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "������Ϣ��\n��ȡ���ݿ��б�ʧ�ܻ�Ϊ���ݿ�Ϊ��", "����",JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			jt_dbtree.setModel(null);
		}
	}
	
	private void operate_clsoeLink()
	{
		mySQLManager = null;
		inner_showTree();
		inner_hideAllTable();
		mit_closelink.setEnabled(false);
		
		System.gc();
	}
	
	private void operate_newLink()
	{
		mySQLManager = null;
		inner_showTree();
		mit_closelink.setEnabled(false);
		
		NewLinkDialog newLinkDialog = new NewLinkDialog(this);
		mySQLManager = newLinkDialog.getSQLManager();
		
		if(mySQLManager!=null)
		{
			mit_closelink.setEnabled(true);
		}
		
		inner_showTree();
		inner_hideAllTable();
		((DataTabelPanel)tablePanels[1]).setTableName(null,null);
	}
	
	private void operate_openShellWin()
	{
		if(mySQLManager!=null)
		{
			new ShellWin(this,mySQLManager);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "���ȡ����ӡ���MySQLŶ��(�����)��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void operate_openLogWin()
	{
		new LogDialog(this);
	}
	
	private void operate_openOptionWin()
	{
		JOptionPane.showMessageDialog(this, "���԰治����������ܣ�\n������ϵ���߹����������֤����ע�����������Űɣ�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void operate_openHelpWin()
	{
		Runtime runtime = Runtime.getRuntime();
		try
		{
			runtime.exec("notepad res/help.txt");
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, "�����ļ���","��ʾ",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void operate_openDonateWin()
	{
		new DonateDialog(this);
	}
	
	private void operate_openAboutWin()
	{
		new AboutDialog(this);
	}
	
	/**
	 * ��ʼ��������
	 * �������߶�Ϊ707(ʵ��)
	 */
	private void contentInit()
	{
		Font font = new Font("΢���ź�", Font.BOLD, 18);
		
		//��������
		JPanel p_background = new JPanel();
		p_background.setBackground(Color.white);
		p_background.setLayout(null);
		setContentPane(p_background);
		
		/*
		 * ���ù�����
		 * ���1024
		 * �߶�70
		 */
		JPanel p_tools = new JPanel();
		p_tools.setBackground(Color.white);
		p_tools.setBounds(0,0,1018,100);
		p_tools.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		p_tools.setLayout(null);
		p_background.add(p_tools);
		
		//����[����]��ť
		JButton btn_newlink = new JButton();
		btn_newlink.setIcon(new ImageIcon("res/icno_newlink.png"));
		btn_newlink.setBounds(20,10,64,64);
		btn_newlink.setFocusPainted(false);
		p_tools.add(btn_newlink);
		{
			JLabel label = new JLabel("����");
			label.setFont(font);
			label.setBounds(20,74,64,25);
			label.setHorizontalAlignment(JTextField.CENTER);
			p_tools.add(label);
		}
		
		//����[�û�]��ť
		JButton btn_user = new JButton();
		btn_user.setIcon(new ImageIcon("res/icon_user.png"));
		btn_user.setBounds(110,10,64,64);
		btn_user.setFocusPainted(false);
		p_tools.add(btn_user);
		{
			JLabel label = new JLabel("�û�");
			label.setFont(font);
			label.setBounds(110,74,64,25);
			label.setHorizontalAlignment(JTextField.CENTER);
			p_tools.add(label);
		}
		
		//�ָ���
		{
			JPanel line = new JPanel();
			line.setBackground(Color.gray);
			line.setBounds(200,5,1,90);
			p_tools.add(line);
		}
		
		//����[��]��ť
		JButton btn_table = new JButton();
		btn_table.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_table.png"), 64, 64));
		btn_table.setBounds(230,10,64,64);
		btn_table.setFocusPainted(false);
		p_tools.add(btn_table);
		{
			JLabel label = new JLabel("��");
			label.setFont(font);
			label.setBounds(230,74,64,25);
			label.setHorizontalAlignment(JTextField.CENTER);
			p_tools.add(label);
		}
		
		//����[����]��ť
		JButton btn_import = new JButton();
		btn_import.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_view.png"), 64, 64));
		btn_import.setBounds(320,10,64,64);
		btn_import.setFocusPainted(false);
		p_tools.add(btn_import);
		{
			JLabel label = new JLabel("����");
			label.setFont(font);
			label.setBounds(320,74,64,25);
			label.setHorizontalAlignment(JTextField.CENTER);
			p_tools.add(label);
		}
		
		//����[����]��ť
		JButton btn_backup = new JButton();
		ImageIcon icon_backup = new ImageIcon("res/icon_backup.png");
		icon_backup.setImage(icon_backup.getImage().getScaledInstance(64, 64,Image.SCALE_DEFAULT));
		btn_backup.setIcon(icon_backup);
		btn_backup.setBounds(410,10,64,64);
		btn_backup.setFocusPainted(false);
		p_tools.add(btn_backup);
		{
			JLabel label = new JLabel("����");
			label.setFont(font);
			label.setBounds(410,74,64,25);
			label.setHorizontalAlignment(JTextField.CENTER);
			p_tools.add(label);
		}
		
		/*
		 * ����Ŀ¼��
		 * ���250
		 * �߶�637
		 */
		JPanel p_dir = new JPanel();
		p_dir.setBackground(Color.white);
		p_dir.setBounds(0,99,250,608);
		p_dir.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		p_dir.setLayout(null);
		p_background.add(p_dir);
		
		//��ʼ����Ŀ¼��
		jt_dbtree = new JTree();
		JScrollPane jScrollPane = new JScrollPane(jt_dbtree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setBounds(2,2,247,605);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		
		ImageIcon icon_root = new ImageIcon("res/icon_root.png");
		icon_root.setImage(icon_root.getImage().getScaledInstance(20, 20,Image.SCALE_DEFAULT));
		ImageIcon icon_leaf = new ImageIcon("res/icon_leaf.png");
		icon_leaf.setImage(icon_leaf.getImage().getScaledInstance(20, 20,Image.SCALE_DEFAULT));
		renderer.setClosedIcon(icon_root);
		renderer.setOpenIcon(icon_root);
		renderer.setLeafIcon(icon_leaf);
		jt_dbtree.setCellRenderer(renderer);
//		jt_dbtree.setEditable(true);
		jt_dbtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		jt_dbtree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				if(e.getButton()==MouseEvent.BUTTON1)	//������
				{
					if(e.getClickCount()==2)
					{
						item = inner_getDBName();
						if(item!=null)
						{
							if(!item.equals("MySQL"))
							{
								//��ѡ���Ĵ���
								operator_SelectTableWin(item);
							}
						}
					}
				}
				else if(e.getButton()==MouseEvent.BUTTON3)	//����Ҽ�
				{
					TreePath pathForLocation = jt_dbtree.getPathForLocation(e.getX(), e.getY());
					jt_dbtree.setSelectionPath(pathForLocation);
					
					//��ʾ�Ҽ��˵�
					item = inner_getDBName();
					if(item!=null)
					{
						if(item.equals("MySQL"))
						{
							JPopupMenu menu = new JPopupMenu();
							JMenuItem it_newdb = new JMenuItem("�½����ݿ�");
							it_newdb.addActionListener(new ActionListener()
							{
								@Override
								public void actionPerformed(ActionEvent e)
								{
									newDB();
								}
							});
							menu.add(it_newdb);
							menu.show(jt_dbtree, e.getX(), e.getY());
						}
						else
						{
							JPopupMenu menu = new JPopupMenu();
							JMenuItem it_rename = new JMenuItem("���������ݿ�");
							JMenuItem it_remove = new JMenuItem("ɾ�����ݿ�");
							
							it_rename.addActionListener(new ActionListener()
							{
								@Override
								public void actionPerformed(ActionEvent e)
								{
									renameDB(item);
								}
							});
							
							it_remove.addActionListener(new ActionListener()
							{
								@Override
								public void actionPerformed(ActionEvent e)
								{
									removeDB(item);
								}
							});
							
							menu.add(it_rename);
							menu.addSeparator();
							menu.add(it_remove);
							menu.show(jt_dbtree, e.getX(), e.getY());
						}
					}
				}
			}
			
		});
		
		p_dir.add(jScrollPane);
		inner_showTree();
		
		/*
		 * ����������
		 * ���768
		 * �߶�637
		 */
		JPanel p_content = new JPanel();
		p_content.setBackground(Color.white);
		p_content.setBounds(249,99,769,608);
		p_content.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		p_content.setLayout(null);
		p_background.add(p_content);
		
		//��ʼ����������
		
		//��ʼ����[�û���]
		tablePanels[0] = new UserTablePanel(this);
		p_content.add(tablePanels[0]);
		
		//��ʼ����[���ݱ�]
		tablePanels[1] = new DataTabelPanel(this);
		p_content.add(tablePanels[1]);
		
		//�������еı���ͼ
		inner_hideAllTable();
		
		//��ʼ����ť������
		btn_newlink.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_newLink();
			}
		});
		
		btn_user.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openUserWin();
			}
		});
		
		btn_table.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openTableWin();
			}
		});
		
		btn_import.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_ImportWin();
			}
		});
		
		btn_backup.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openBackupWin();
			}
		});
	}
	
	private void operator_SelectTableWin(String dbname)
	{
		if(mySQLManager!=null)
		{
			((DataTabelPanel)tablePanels[1]).setSQLManager(mySQLManager);
		}
		SelectTableDialog selectTableDialog = new SelectTableDialog(this,dbname,mySQLManager);
		((DataTabelPanel)tablePanels[1]).setTableName(dbname,selectTableDialog.getTableName());
		inner_showTable(1);
	}
	
	/**
	 * ��ʼ���˵���
	 */
	private void menuInit()
	{	
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//���ò˵�
		JMenu menu_file = new JMenu("  �ļ�  ");
		JMenu menu_tools = new JMenu("  ����  ");
		JMenu menu_help = new JMenu("  ����  ");
		menuBar.add(menu_file);
		menuBar.add(menu_tools);
		menuBar.add(menu_help);
		
		//����[�ļ�]�˵�
		JMenuItem mit_newlink = new JMenuItem("�½�����");
		mit_closelink = new JMenuItem("�ر�����");
		mit_closelink.setEnabled(false);
		JMenuItem mit_exit = new JMenuItem("�˳�");
		menu_file.add(mit_newlink);
		menu_file.add(mit_closelink);
		menu_file.addSeparator();
		menu_file.add(mit_exit);
		
		//����[����]�˵�
		JMenuItem mit_shell = new JMenuItem(" �����н��� ");
		ImageIcon icon_shell = new ImageIcon("res/icon_shell.png");
		icon_shell.setImage(icon_shell.getImage().getScaledInstance(20, 20,Image.SCALE_DEFAULT));
		mit_shell.setIcon(icon_shell);
		JMenuItem mit_log = new JMenuItem(" ��ʷ��־ ");
		JMenuItem mit_options = new JMenuItem(" ѡ��... ");
		menu_tools.add(mit_shell);
		menu_tools.addSeparator();
		menu_tools.add(mit_log);
		menu_tools.addSeparator();
		menu_tools.add(mit_options);
		
		//����[����]�˵�
		ImageIcon icon_help = new ImageIcon("res/icon_help.png");
		icon_help.setImage(icon_help.getImage().getScaledInstance(20, 20,Image.SCALE_DEFAULT));
		JMenuItem mit_help = new JMenuItem("AgSQL for MySQL ����");
		mit_help.setIcon(icon_help);
		JMenuItem mit_donate = new JMenuItem("֧�ֿ�����");
		JMenuItem mit_about = new JMenuItem("����...");
		ImageIcon icon_about = new ImageIcon("res/icon.png");
		icon_about.setImage(icon_about.getImage().getScaledInstance(20, 20,Image.SCALE_DEFAULT));
		mit_about.setIcon(icon_about);
		menu_help.add(mit_help);
		menu_help.addSeparator();
		menu_help.add(mit_donate);
		menu_help.addSeparator();
		menu_help.add(mit_about);
		
		//���õ���¼�
		mit_newlink.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_newLink();
			}
		});
		
		mit_closelink.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_clsoeLink();
			}
		});
		
		mit_exit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		mit_shell.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openShellWin();
			}
		});
		
		mit_log.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openLogWin();
			}
		});
		
		mit_options.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openOptionWin();
			}
		});
		
		mit_help.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openHelpWin();
			}
		});
		
		mit_donate.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openDonateWin();
			}
		});
		
		mit_about.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				operate_openAboutWin();
			}
		});
	}
	
	/**
	 * ��ʼ�����ڵĻ�������
	 * x:48 y:52
	 */
	private void winInit()
	{
		setTitle("AgSQL for MySQL");
		setSize(1072, 820);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage((new ImageIcon("res/appico.png")).getImage());
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
