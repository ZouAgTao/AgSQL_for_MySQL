package win;

import java.awt.Color;

import javax.swing.JPanel;

public class TablePanel extends JPanel
{
	public TablePanel()
	{
		setBackground(Color.white);
		setBounds(1,1,768,607);
		setLayout(null);
	}
	
	public void show()
	{
		setSize(767,606);
	}
	
	public void hide()
	{
		setSize(0, 0);
	}
}
