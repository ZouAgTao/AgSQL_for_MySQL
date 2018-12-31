package win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;

public class DonateDialog extends JDialog
{
	public DonateDialog(JFrame parent)
	{
		super(parent);
		
		InitDialog();
		InitContent();
		
		setVisible(true);
	}
	
	private void InitContent()
	{
		JLabel panel = new JLabel();
		panel.setIcon(new ImageIcon("res/donate.png"));
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
		setTitle("支持 AgSQL 的开发");
		setSize(448, 852);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage((new ImageIcon("res/icon_donate.png")).getImage());
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
