package win;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import data.Info;
import sql.MySQLManager;

public class DataTabelPanel extends TablePanel
{
	private JFrame parent = null;
	private MySQLManager sqlManager = null;
	private String table_name = null;
	private String db_name = null;
	
	private JButton btn_del = null;
	private JButton btn_rename = null;
	private JButton btn_add = null;
	
	private int select_row = -1;
	private int select_column = -1;
	
	private JTable table = null;
	
	public DataTabelPanel(JFrame parent)
	{
		super();
		
		this.parent = parent;
		Init();
	}
	
	private String[] getTableColumn()
	{
		if(sqlManager!=null)
		{
			String[] row =  sqlManager.getColumns(db_name, table_name);
			
			if(row==null || row.length<1)
			{
				return null;
			}
			
			String[] complete = new String[row.length+1];
			complete[0]="序号";
			for(int i=0;i<row.length;i++)
			{
				complete[i+1]=row[i];
			}
			return complete;
		}
		else
		{
			return null;
		}
	}
	
	private String[][] getTableRows()
	{
		if(sqlManager!=null)
		{
			String[][] row =  sqlManager.getRows(db_name, table_name);
			
			if(row==null ||row.length<1)
			{
				return null;
			}
			
			String[][] complete = new String[row.length][row[0].length+1];

			for(int i=0;i<row.length;i++)
			{
				complete[i][0]=""+(i+1);
				for(int j=0;j<row[i].length;j++)
				{
					complete[i][j+1]=row[i][j];
				}
			}
			return complete;
		}
		else
		{
			return null;
		}
	}
	
	private void Init()
	{
		//初始化按钮
		btn_add = new JButton("增加记录");
		btn_add.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_newlink.png"), 30, 30));
		btn_add.setBounds(10, 10, 130, 35);
		btn_add.setEnabled(false);
		add(btn_add);
		
		btn_del = new JButton("删除记录");
		btn_del.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_remove.png"), 30, 30));
		btn_del.setBounds(160, 10, 130, 35);
		btn_del.setEnabled(false);
		add(btn_del);
		
		btn_rename = new JButton("重命名字段");
		btn_rename.setIcon(Info.adaaptImageIcon(new ImageIcon("res/icon_edit.png"), 30, 30));
		btn_rename.setBounds(310, 10, 150, 35);
		btn_rename.setEnabled(false);
		add(btn_rename);
		
		//初始化表格
		table = new JTable();
		JScrollPane jscrollpane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jscrollpane.setBounds(10,50,750,550);
		table.setRowHeight(30);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(jscrollpane);
		
		//设置按钮监听器
		btn_add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(parent, "测试版不具有这个功能，\n请联联系作者购买正版许可证进行注册就有啦（大概吧）\n使用【工具】-【命令行界面】可以手动编辑表格","提示",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		btn_del.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(select_row!=-1 && select_column!=-1 && select_column!=0)
				{
					int value = JOptionPane.showConfirmDialog(parent, "确认是否删除["+table.getValueAt(select_row, select_column)+"]所在的整行数据?", "提示",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(value!=0)
					{
						return;
					}
					
					boolean type[] = null;
					type=sqlManager.getType(db_name, table_name);
					String[] column_name =  sqlManager.getColumns(db_name, table_name);
					String[] row_value = new String[column_name.length];
					for(int i=1;i<=row_value.length;i++)
					{
						row_value[i-1]=(String)table.getValueAt(select_row, i);
					}
					
					sqlManager.deleteData(db_name, table_name, column_name, row_value, type);
					
					select_row = -1;
					select_column = -1;
					btn_rename.setEnabled(false);
					btn_del.setEnabled(false);
					table.clearSelection();
					
					showTable();
				}
			}
		});
		
		btn_rename.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(select_row!=-1 && select_column!=-1 && select_column!=0)
				{
					String name= JOptionPane.showInputDialog("请输入新的值");
					boolean type[] = null;
					
					if(name==null)
					{
						return;
					}
					else
					{
						//获取该表所有列类型到type数组中
						type=sqlManager.getType(db_name, table_name);
						
						String[] column_name =  sqlManager.getColumns(db_name, table_name);
						String[] row_value = new String[column_name.length];
						for(int i=1;i<=row_value.length;i++)
						{
							row_value[i-1]=(String)table.getValueAt(select_row, i);
						}
						
						sqlManager.update(db_name, table_name, column_name, row_value, name, select_column-1, type);
					}
					
					select_row = -1;
					select_column = -1;
					btn_rename.setEnabled(false);
					btn_del.setEnabled(false);
					table.clearSelection();
					
					showTable();
				}
			}
		});
		
		//设置表格监听器
		table.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				if(e.getButton()==MouseEvent.BUTTON1)
				{
					select_row = table.getSelectedRow();
					select_column = table.getSelectedColumn();
					btn_del.setEnabled(true);
					btn_rename.setEnabled(true);
				}
			}
			
		});
		
		//初始化表格
		showTable();
	}
	
	public void setTableName(String db_name, String table_name)
	{
		this.db_name = db_name;
		this.table_name = table_name;
		showTable();
	}
	
	private void FitTableColumns(JTable myTable)
	{
		JTableHeader header = myTable.getTableHeader();
		
		int rowCount = myTable.getRowCount();
		Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
		while(columns.hasMoreElements())
		{
			TableColumn column = (TableColumn)columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int)myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
		    
			for(int row = 0; row<rowCount; row++)
			{
				int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
		        width = Math.max(width, preferedWidth);
		    }
		    header.setResizingColumn(column);
		    column.setWidth(width+myTable.getIntercellSpacing().width+25);
		}
	}
	
	public void showTable()
	{
		if(db_name!=null && table_name!=null)
		{
			//显示对应的数据列表
			String[] data_column = getTableColumn();
			String[][] data_rows = getTableRows();
			
			if(data_column!=null && data_rows!=null)
			{
				btn_add.setEnabled(true);
				table.setModel(new DefaultTableModel(data_rows,data_column) {public boolean isCellEditable(int row, int column) {return false;}});
				FitTableColumns(table);
			}
			else if(data_column!=null)
			{
				btn_add.setEnabled(true);
				table.setModel(new DefaultTableModel(new String[][] {},data_column) {public boolean isCellEditable(int row, int column) {return false;}});
				FitTableColumns(table);
			}
			else
			{
				btn_add.setEnabled(false);
			}
		}
		else
		{
			//清空并空白化
			table.setModel(new DefaultTableModel());
			btn_add.setEnabled(false);
			btn_del.setEnabled(false);
			btn_rename.setEnabled(false);
		}
	}

	public void setSQLManager(MySQLManager sqlManager)
	{
		this.sqlManager = sqlManager;
	}
}
