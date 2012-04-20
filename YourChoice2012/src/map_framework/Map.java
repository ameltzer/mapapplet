package map_framework;

import java.awt.Color;

import javax.swing.JFrame;

import shp_framework.SHPMap;

/**
 * 
 * @author Aaron Meltzer
 *
 */
public class Map extends JFrame{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected DataModel data;
	protected EventRelayer event;
	public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.CYAN;
	
	protected SHPMap map;
	/**
	 * 
	 * @return DataModel
	 */
	public DataModel getDataModel(){
		return data;
	}
	/**
	 * 
	 * @return EventRelayer
	 */
	public EventRelayer getEvent(){
		return event;
	}
	/**
	 * 
	 * @return SHPMap
	 */
	public SHPMap getMap(){
		return map;
	}
	
	public void initWindow(){
		
	}
	public void initData(){
		
	}
	public void layoutGUI(){
		
	}
	public void initHandlers(){
		
	}
}
