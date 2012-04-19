package election_map_applet;

import java.io.File;

import javax.swing.JApplet;

public class ElectionAppletStart extends JApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init()
	{
		try{
			// SETUP THE WINDOW
			ElectionMapViewer frame = new ElectionMapViewer();
			frame.setCode(getCodeBase());
			frame.getFileManager().setApplet(this);
			frame.resumeStart();
			// DISPLAY THE WINDOW
			frame.setVisible(true);
			frame.getDataModel().setViewWidth(frame.getWidth());
			frame.getDataModel().setViewHeight(frame.getHeight());
			frame.getRenderer().setSizeDependent(frame.getWidth(), frame.getHeight());	
			// WE'LL WAIT UNTIL WE KNOW THE SIZE OF THE RENDERING
			// PANEL BEFORE WE ZOOM IN ON THE MAP
			ElectionMapRenderer render = frame.getRenderer();
			while (render.getWidth() <= 0)
			{
				try { Thread.sleep(10); }
				catch(InterruptedException ie) { ie.printStackTrace(); }
			}
			render.repaint();
			render.setFile(new File(ElectionMapFileManager.USA_DBF));
			}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("init method error");
		}
	}
}
