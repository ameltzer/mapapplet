package election_map_applet;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;

import map_framework.Map;




import election_map_applet.events.ElectionMapKeyHandler;
import election_map_applet.events.ElectionMapMouseOverShapeHandler;
import election_map_applet.events.ElectionMapWindowHandler;

/**
 * This application allows the user to view national and individual
 * state election results for the 2008 US Presidential Elections.
 * 
 * @author Richard McKenna, Aaron Meltzer
 */
public class ElectionMapViewer extends Map
{
	// THIS CAN LOAD OUR DATA
	private ElectionMapFileManager fileManager;
	private URL code;
	// HERE'S THE DATA
	private ElectionMapDataModel dataModel;
	
	// FOR RENDERING THE MAP
	private ElectionMapRenderer renderer;
	private JButton reset;
	private JSlider slider;
	public void setCode(URL code){
		this.code=code;
	}
	public URL getCode(){
		return this.code;
	}
	/**
	 * This constructor sets up the GUI, including loading the
	 * USA map. Note that the state maps are only loaded upon 
	 * user request.
	 */
	public ElectionMapViewer()
	{
		// AND LOAD THE DATA MANAGEMENT CLASS
		dataModel = new ElectionMapDataModel();		

		// SETUP THE CLASS THAT LOADS DBF, SHP, and IMAGE FILES
		fileManager = new ElectionMapFileManager(dataModel);
	}
	public void resumeStart(){
		// INIT OUR APPLICATION
		initWindow();
		initData();
		layoutGUI();
		initHandlers();
		// AND THEN SETUP THE DATA MODEL
		dataModel.init(renderer, fileManager);
	}
	
	// ACCESSOR METHOD
	/**
	 * @return ElectionMapFileManager
	 */
	public ElectionMapFileManager getFileManager()	{ return fileManager; 	}
	/**
	 * 
	 * @return ElectionMapRenderer
	 */
	public ElectionMapRenderer	getRenderer()		{ return renderer;		}
	public ElectionMapDataModel getDataModel()		{ return dataModel;		}
	/**
	 * Initializes our GUI's window.
	 */
	
	public void initWindow()
	{
		// GIVE THE WINDOW A TITLE FOR THE TITLE BAR
		setTitle("Election Map Viewer");
		
		// MAXIMIZE IT TO FIT THE SCREEN
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// WE'LL HANDLE WINDOW CLOSING OURSELF, SO MAKE
		// SURE NOTHING IS DONE BY DEFAULT
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Initializes the data manager and the file manager used by our app.
	 * Note that the data model is still not ready for use after this method,
	 * it will still need a constructed renderer.
	 */
	public void initData()
	{
		fileManager.loadAppIcon(this);
		fileManager.loadAllFlags(this);
		fileManager.loadAllUSAMaps(this);
	}

	/**
	 * We are only using a single panel to render everything.
	 */
	public void layoutGUI()
	{
		try {
			renderer = new ElectionMapRenderer(dataModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(renderer, BorderLayout.CENTER);
		renderer.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		reset = new JButton("Reset");
		c.gridx=0;
		c.gridy=0;
		c.anchor= GridBagConstraints.LAST_LINE_END;
		renderer.add(reset, c);
		renderer.add(Box.createRigidArea(new Dimension(100,700)));
		slider = new JSlider();
		Dimension d = slider.getPreferredSize();   
		slider.setPreferredSize(new Dimension(d.width+600,d.height));
		c.gridx=0;
		c.gridy=1;
		renderer.add(slider,c);
	}
	
	/**
	 * This initializes and registers all event handlers. Note that
	 * we are using a MouseListener to listen for mouse clicks, and
	 * a MouseMotionListener to listen for mouse motion.
	 */
	public void initHandlers()
	{
		// THIS WILL LISTEN FOR KEY PRESSES SO THE USER CAN 
		// EASILY MOVE THE VIEWPORT AROUND AND ZOOM IN AND OUT
		ElectionMapKeyHandler kh = new ElectionMapKeyHandler(dataModel);
		this.addKeyListener(kh);
		renderer.addKeyListener(kh);
		this.setFocusable(true);
		renderer.setFocusable(true);

		// THIS WILL LISTEN FOR MOUSE MOVEMENT TO IMPLEMENT POLYGON HIGHLIGHTING
		ElectionMapMouseOverShapeHandler mosh = new ElectionMapMouseOverShapeHandler(dataModel);
		renderer.addMouseMotionListener(mosh);
		//Listens for the click event
		renderer.addMouseListener(new toState(dataModel));
		// THIS WILL HANDLE MOUSE CLICKS ON THE WINDOW'S X
		ElectionMapWindowHandler emwh = new ElectionMapWindowHandler(this);
		addWindowListener(emwh);
	}
	
	/**
	 * This main method starts the application.
	 * @param args:String[]
	 */
	/*
	 * class for the zooming action
	 */
	/**
	 * @author Aaron Meltzer
	 */
	public class toState implements MouseListener
	{
		
		private ElectionMapDataModel dataModel;
		/**
		 * 
		 * @param dataModel:ElectionMapDataModel
		 */
		public toState(ElectionMapDataModel dataModel){
			this.dataModel=dataModel;
		}
		/**
		 * @param arg0:MouseEvent
		 */
		public void mouseClicked(MouseEvent arg0)
		{
			//as long as you are in the USA map you can click to zoom
			if(this.dataModel.getCurrentMapAbbr().equals("USA"))
			{
				dataModel.getRenderer().zoomHandler((String)dataModel.getTable().getTree().
						get(dataModel.getRenderer().getPolyLocation()).getData(1));
			}
		}
		//we don't want any of these
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}	
	}
}