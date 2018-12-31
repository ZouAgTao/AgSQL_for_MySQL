package main;

import javax.swing.UIManager;

import win.MainWin;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		new MainWin();
	}
}
