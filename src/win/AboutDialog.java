package win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;

public class AboutDialog extends JDialog
{
	public AboutDialog(JFrame parent)
	{
		super(parent);
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}

	private void InitContent()
	{
		JLabel panel = new JLabel();
		panel.setIcon(new ImageIcon("res/logo.png"));
		add(panel);
		panel.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				closeDialog();
			}
			
		});
	}
	
	private void closeDialog()
	{
		dispose();
	}
	
	private void InitDialog()
	{
		setModal(true);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setTitle("¹ØÓÚ AgSQL");
		setSize(752, 556);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon.png")).getImage());
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
