package test;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;

import win.DataTabelPanel;

public class TestWin extends JFrame
{
	public TestWin()
	{
		setTitle("AgSQL for MySQL 开发测试窗口");
		setResizable(false);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.black);
	}
	
	public void adaptToPanel(JComponent panel, int width, int height, boolean pack)
	{
		panel.setLocation(50, 50);
		getContentPane().setLayout(null);
		getContentPane().add(panel);
		setSize(width, height);
		setLocationRelativeTo(null);
		if(pack)
		{
			pack();
		}
	}
	
	public static void main(String[] args)
	{
		//元测试数据
		int width = 1024;
		int height = 768;
		
		//用户测试数据
//		UserTablePanel userTablePanel = new UserTablePanel();
		DataTabelPanel dataTabelPanel;
		
		//测试窗口
		TestWin testWin = new TestWin();
		dataTabelPanel = new DataTabelPanel(testWin);
		testWin.adaptToPanel(dataTabelPanel, width, height, false);
		testWin.setVisible(true);
		
//		new SelectTableDialog(testWin, "数据库名字", null);
	}
}
