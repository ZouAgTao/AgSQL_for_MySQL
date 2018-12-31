package data;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Info
{
	public static ImageIcon adaaptImageIcon(ImageIcon icon, int width, int height)
	{
		icon.setImage(icon.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
		return icon;
	}
}
