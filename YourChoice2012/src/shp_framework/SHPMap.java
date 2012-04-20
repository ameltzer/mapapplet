package shp_framework;

import java.util.Iterator;

import shp_framework.geometry.SHPShape;
/**
 * SHPMap - This class represents the shapefile itself. It has access to 
 * the geographic data, knows if it is visible or not, and knows its own
 * location name. If ever we were to render multiple shapefiles, like as 
 * pieces in a map, we may choose to manage them by name with some visible 
 * and some not.
 * 
 * @author Richard McKenna
 */
public class SHPMap
{
	// NOTE THAT RIGHT NOW, WE ARE ONLY USING ONE SHAPE FILE AT A TIME,
	// BUT WE COULD CHANGE THIS TO POTENTIALLY STORE MULTIPLE SHAPEFILES
	// SIMULTANEOUSLY
	private String location;
	private SHPData shapefileData;
	private boolean visible;

	/**
	 * This constructor initializes the map data, assuming it's
	 * already been loaded.
	 * @param initLocation:String
	 * @param initShapefileData:SHPData
	 */
	public SHPMap(	String initLocation,
					SHPData initShapefileData)
	{
		location = initLocation;
		shapefileData = initShapefileData;
		visible = true;
	}

	// ACCESSOR METHODS
	/**
	 * @return String
	 */
	public String	getLocation()		{ return location;		}
	/**
	 * @return SHPData
	 */
	public SHPData 	getShapefileData() 	{ return shapefileData; }
	/**
	 * @return boolean
	 */
	public boolean 	isVisible()			{ return visible;		}

	// ITERATOR - WE'RE DOING THIS BY PROXY
	/**
	 * @return Iterator<SHPShape
	 */
	public Iterator<SHPShape> shapesIterator()
	{
		return shapefileData.shapesIterator();
	}

	// MUTATOR METHOD
	/**
	 * @param initVisible:boolean
	 */
	public void setVisible(boolean initVisible)
	{
		visible = initVisible;
	}
}