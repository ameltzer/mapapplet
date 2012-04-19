package election_map_applet;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

import shp_framework.SHPDataLoader;
import shp_framework.SHPMap;

/**
 * This class provides all file loading for the application. Note that it
 * will use the services of the DBF and SHP frameworks for loading those
 * particular file formats.
 * 
 * @author Richard McKenna, Aaron Meltzer
 **/
public class ElectionMapFileManager 
{
	// THIS IS WHERE WE'LL STORE OUR MAPS
	public static final String SETUP_DIR = "./setup/";
	public static final String FLAGS_DIR = SETUP_DIR + "flags/";
	public static final String MAPS_DIR = SETUP_DIR + "maps/";
	public static final String USA_SHP = MAPS_DIR + "USA.shp";
	public static final String USA_DBF = MAPS_DIR + "USA.dbf";
	
	// THIS IS THE ICON FOR OUR APP
	public static final String APP_ICON = SETUP_DIR + "USPresidentialSeal.png";

	// AND HERE ARE THE CLASSES THAT CAN LOAD THE DATA
	private SHPDataLoader shpLoader;
	private JApplet theApplet;
	private URL code;

	// WE'LL NEED TO LOAD DATA INTO THE DATA MODEL
	private ElectionMapDataModel dataModel;
	public static final String YEAR="2012";
	public static final String USA="USA";
	public static final String extensionSHP=".shp";
	public static final String extensionDBF=".dbf";
	/**
	 * The initView will be used by all subsequent methods.
	 * @param initDataModel: ElectionMapDataModel
	 **/
	public ElectionMapFileManager(ElectionMapDataModel initDataModel)
	{
		dataModel = initDataModel;
		shpLoader = new SHPDataLoader();
		
	}
	public void setApplet(JApplet applet){
		this.theApplet=applet;
		code= theApplet.getCodeBase();
	}
	
	/**
	 * @param view:ElectionMapViewer
	 * Loads all images, both sizes, for all map regions, into the
	 * data model.
	 **/
	public void loadAllFlags(ElectionMapViewer view)
	{
		// THE FLAG IMAGES MUST BE IN THIS DIRECTORY
		//File flagsDir = new File(FLAGS_DIR);
		
		// THE TRACKER MAKES SURE IMAGES ARE COMPLETELY
		// LOADED BEFORE MOVING ON, SINCE IMAGE LOADING
		// IS DONE ASYNCHRONOUSLY
		MediaTracker tracker = new MediaTracker(view);
		try
		{
			URL url = new URL(this.code, FLAGS_DIR);
			File flagsDir = new File(url.getPath());
			File[] flags = flagsDir.listFiles();
			// GO THROUGH ALL THE CONTENTS OF THE flags DIRECTORY
			for (int i = 0; i < flags.length; i++)
			{	
				// GET AND LOAD THE FLAG
				File flagFile = flags[i];
				String name = flagFile.getName();
				int dotIndex = name.indexOf('.');
				String abbr = name.substring(0, dotIndex);
				Image flag = ImageIO.read(flagFile);
				dataModel.addFlag(name, abbr, flag);				
				tracker.addImage(flag, i);
			}
			// AND WAIT FOR THEM TO LOAD
			tracker.waitForAll();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.exit(0);
			JOptionPane.showMessageDialog(	view, 
											"Error loading flags from " + FLAGS_DIR, 
											"Error loading flags from " + FLAGS_DIR,
											JOptionPane.ERROR_MESSAGE);
		}
		catch(InterruptedException ie)
		{
			// THIS SHOULD NEVER HAPPEN, SO WE HAVE NO PROGRAMMED RESPONSE,
			// BUT YOU SHOULD ALWAYS AT LEAST HAVE A STACK TRACE, DON'T JUST
			// SQUELCH EXCEPTIONS, THAT CAN MAKE LOGICAL ERRORS HARDER TO FIND
			ie.printStackTrace();
		}
	}
	
	/**
	 * @param view:ElectionMapViewer
	 * Loads the program's icon into the frame.
	 **/
	public void loadAppIcon(ElectionMapViewer view)
	{
		try
		{
			// LOAD THE APP'S ICON AND SET IT IN THE FRAME
			URL app= new URL(code, APP_ICON);
			Image appIcon = this.theApplet.getImage(app);
			MediaTracker tracker = new MediaTracker(view);
			tracker.addImage(appIcon, 0);
			try { tracker.waitForID(0); }
			catch(InterruptedException ie) { ie.printStackTrace(); }
			view.setIconImage(appIcon);
		}
		catch(IOException ioe)
		{
			// THIS MEANS THE IMAGE IS NOT WHERE IT SHOULD BE
			JOptionPane.showMessageDialog(	view, 
					"Error loading flags from " + FLAGS_DIR, 
					"Error loading flags from " + FLAGS_DIR,
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * @param view:ElectionMapViewer
	 * Loads the USA map, which should happen only once, at startup.
	 **/
	public void loadAllUSAMaps(ElectionMapViewer view)
	{
		try
		{
			// LOAD THE USA MAP FILE
			File shpFile = new File(USA_SHP);
			//load the SHPMap
			SHPMap usaSHP = shpLoader.loadShapefile(shpFile);
			// INITIALIZE THE COLORS
			dataModel.colorSections(usaSHP, new File(USA_DBF));
			// AND UPDATE THE GUI
			dataModel.initUSAMap(usaSHP);
		}
		catch(IOException ioe)
		{
			// THIS WOULD HAPPEN DURING A LOADING ERROR
			JOptionPane.showMessageDialog(	view, 
											"Error loading USA map ", 
											"Error loading USA map", 
											JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * 
	 * @param view:ElectionMapViewer
	 */
	public void loadAllBios(ElectionMapViewer view){
		
	}
}
