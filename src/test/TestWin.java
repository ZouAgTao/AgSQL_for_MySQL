package test;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;

import win.DataTabelPanel;

public class TestWin extends JFrame
{
	public TestWin()
	{
		setTitle("AgSQL for MySQL �������Դ���");
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
		//Ԫ��������
		int width = 1024;
		int height = 768;
		
		//�û���������
//		UserTablePanel userTablePanel = new UserTablePanel();
		DataTabelPanel dataTabelPanel;
		
		//���Դ���
		TestWin testWin = new TestWin();
		dataTabelPanel = new DataTabelPanel(testWin);
		testWin.adaptToPanel(dataTabelPanel, width, height, false);
		testWin.setVisible(true);
		
//		new SelectTableDialog(testWin, "���ݿ�����", null);
	}
}
