package map_framework;



import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.TreeMap;

import shp_framework.SHPData;
import shp_framework.SHPMap;
import shp_framework.geometry.SHPPolygon;
import shp_framework.geometry.SHPShape;
import dbf_framework.DBFTable;
/**
 * 
 * @author Aaron Meltzer
 *
 */
public class DataModel {
	public static final String MAP_TITLE = " 2008 Presidential Election Results";
	public static final String MINI_FLAG_EXT = ".gif";
	//the DBFTable of a DBF File
	protected Renderer renderer;
	protected Map theMap;
	protected DBFTable sections;
	protected SHPMap usaSHP;
	protected SHPMap currentSHP;
	protected String currentMapName;
	protected String currentMapAbbr;
	protected String currentOverallAbbr;
	protected SHPPolygon highlightedPolygon;
	protected Polygon testPoly;
	protected TreeMap<String,Image> flags;
	protected TreeMap<String,Image> miniFlags;
	protected boolean mapRendered;
	
	public DataModel()
	{
		// AND INITIALIZE OUR DATA STRUCTURES
		testPoly = new Polygon();
		flags = new TreeMap<String, Image>();
		miniFlags = new TreeMap<String, Image>();
		
		// THE MAP HAS NOT YET BEEN RENDERED
		mapRendered = false;
		sections = new DBFTable();
	}
	/**
	 * @return String
	 */
	public String 		getCurrentMapName() 		{ return currentMapName; 	}
	/**
	 * 
	 * @return String
	 */
	public String 		getCurrentMapAbbr() 		{ return currentMapAbbr; 	}
	/**
	 * 
	 * @return SHPPolygon
	 */
	public SHPPolygon	getHighlightedPolygon()		{ return highlightedPolygon;}
	/**
	 * 
	 * @return boolean
	 */
	public boolean 	isMapLoaded() 			{ return currentMapName != null; 		}
	/**
	 * 
	 * @return boolean
	 */
	public boolean	isMapRendered()			{ return mapRendered;					}
	/**
	 * 
	 * @return boolean
	 */
	public boolean 	isRegionHighlighted()	{ return highlightedPolygon != null; 	}
	/**
	 * 
	 * @return TreeMap<String,Image>
	 */
	public TreeMap<String, Image> getFlags()  { return flags;							}
	/**
	 * 
	 * @return TreeMap<String,Image>
	 */
	public TreeMap<String, Image> getMiniFlags() { return miniFlags;				}
	/**
	 * 
	 * @return DBFTable
	 */
	public DBFTable getTable()				 { return sections;					}
	/**
	 * @return SHPMap
	 */
	public SHPMap getCurrentSHP()
	{
		// WE ONLY HAVE ONE FOR NOW, YOU MIGHT WANT TO CHANGE
		// HOW THIS WORKS SINCE YOU'LL HAVE OTHERS
		return currentSHP;
	}
	/**
	 * 
	 * @param map:SHPMap
	 */
	public void setCurrentSHP(SHPMap map){
		currentSHP=map;
	}
	/**
	 * 
	 * @param abbr:String
	 */
	public void setCurrentMapAbbr(String abbr){
		this.currentMapAbbr = abbr;
	}
	/**
	 * For accessing the large flag of the map currently being rendered.
	 **/
	/**
	 * @return Image
	 */
	public Image getCurrentFlag()
	{
		return flags.get(currentMapAbbr);
	}	
	/**
	 * 
	 * @return String
	 */
	public String		getOverallMapAbbr()			{ return this.currentOverallAbbr; }
	/**
	 * For accessing the SHPMap that corresponds to the the map that
	 * is currently being rendered.
	 **/
	/**
	 * 
	 * @param abbr:String
	 */
	public void setOverallMapAbbr(String abbr){
		this.currentOverallAbbr = abbr;
	}
	/**
	 * @param poly:SHPPolygon
	 * 
	 * Called whenever a different territory is highlighted, this updates
	 * our data model so it knows what is highlighted for rendering
	 * purposes.
	 */
	public void setHighlightedRegion(SHPPolygon poly)
	{
		if (highlightedPolygon != null)
			highlightedPolygon.setLineColor(Map.DEFAULT_BORDER_COLOR);
		highlightedPolygon = poly;
		poly.setLineColor(Map.DEFAULT_HIGHLIGHT_COLOR);		
	}
	
	/**
	 * @param initMapRendered:boolean
	 * Called to keep track of whether the map has been rendered
	 * at least once or not. The reason for this is so we don't
	 * keep doing our zoom to map function.
	 */
	public void setMapRendered(boolean initMapRendered)
	{
		mapRendered = initMapRendered;
	}
	
	// SERVICE METHODS - THESE METHODS PROVIDE ADDITIONAL DATA PROCESSING
	// SERVICES, IN PARTICULAR FOR THE EVENT HANDLERS.
	
	/**
	 * @param name:String
	 * @param flagAbbr:String
	 * @param flag:Image
	 * This method adds the flag argument to the proper data structure
	 * for storage. Note that this method properly filters the images
	 * into their proper container, with .png files going to flags, and
	 * .gif files going to miniFlags.
	 **/
	public void addFlag(String name, String flagAbbr, Image flag)
	{
		if (name.endsWith(MINI_FLAG_EXT))
			miniFlags.put(flagAbbr, flag);
		else
			flags.put(flagAbbr, flag);
	}

	/**
	 * @param x:int
	 * @param y:int
	 * Called in reponse to mouse motion, this method tests to see
	 * if the current mouse's x,y position overlaps any of the current
	 * map's polygons, and if it does, makes that the highlighted
	 * map region. Note that this method forces a renderer repaint.
	 **/
	public void highlightMapRegion(int x, int y)
	{
		boolean polySelected = selectPolygonAt(x, y);
		if (!polySelected)
		{
			// THE MOUSE ISN'T CURRENTLY OVER ANY SHAPES, SO
			// DON'T HIGHLIGHT ANY OF THEM
			resetHighlightedRegion();
		}
		
		// UPDATE THE VIEW
		renderer.repaint();	
	}
	public void recallPaint(){
		renderer.repaint();
	}
	/**
	 * Undoes all map highlighting, this method should be called
	 * when the mouse is determined to not be overlapping any map
	 * regions.
	 **/
	public void resetHighlightedRegion()
	{
		highlightedPolygon = null;
	}	
	
	/**
	 * @param x:int
	 * @param y:int
	 * @return boolean
	 * Used for testing to see if the x,y location is within the
	 * bounds of a map region's polygon. If it is, that region is
	 * highlighted and true is returned. If no map region is found
	 * to contain the point, all regions are unhighlighted and 
	 * false is returned. 
	 **/
	public boolean selectPolygonAt(int x, int y)
	{
		SHPMap currentMap = getCurrentSHP();
		SHPData mapData = currentMap.getShapefileData();	
		Iterator<SHPShape> polyIt = (mapData.getShapes().iterator());
		SHPPolygon poly;
		boolean polyFound = false;
		boolean stopSearching = false;
		
		// GO THROUGH ALL THE POLYGONS IN THE MAP
		while (polyIt.hasNext())
		{
			// TEST EACH ONE
			poly = (SHPPolygon)polyIt.next();

			if (!stopSearching)
			{
				// ONCE ONE IS FOUND TO BE TRUE, NONE OTHERS CAN
				polyFound = pointIsInPoly(renderer, poly, x, y);
			}

			if (polyFound)
			{
				// MARK THIS ONE FOR HIGHLIGHTING
				setHighlightedRegion(poly);
				renderer.setPolyNumber(poly.getRecordNumber()-1);
				return true;
			}				
			else
			{
				// ALL OTHERS WILL KEEP THE STANDARD BLACK
				poly.setLineColor(Color.black);
			}
		}
		renderer.setPolyNumber(-1);
		return false;
	}	
	/**
	 * @param renderer:ElectionMapRenderer
	 * @param poly:SHPPolygon
	 * @param x:int
	 * @param y:int
	 * @return boolean
	 * This method tests to see if the (x,y) point is inside one of the parts (polygons)
	 * of the poly argument. If it is, true is returned, else false.
	 **/
	public boolean pointIsInPoly(Renderer renderer, SHPPolygon poly, int x, int y)
	{
		// GO THROUGH ALL THE PARTS (POLYGONS) OF THIS SHPPolygon. REMEMBER, IN
		// AN SHP FILE, A POLYGON IS MAKE UP OF OTHER PARTS, WHICH ARE EACH THEIR
		// OWN POLYGONS
		for (int i = 0; i < poly.getNumParts(); i++)
		{
			// CLEAR OUR RECYCLED POLYGON OBJECT
			testPoly.reset();
		
			// DETERMINE WHERE IN THE ARRAY WE'LL GET OUR POINTS FROM
			int partStart = poly.getParts()[i];
			int partEnd = poly.getNumPoints()-1;
			if (i < poly.getNumParts()-1)
			{
				partEnd = poly.getParts()[i+1]-1;
			}
		
			// NOW FILL OUR testPoly WITH PARTS
			for (int j = partStart; j <= partEnd; j++)
			{
				double lat = poly.getXPointsData()[j];
				double lon = poly.getYPointsData()[j];
				int pX = renderer.xCoordinateToPixel(lat);
				int pY = renderer.yCoordinateToPixel(lon);
				testPoly.addPoint(pX,pY);
			}
			// AND LET OUR testPoly DO THE TEST
			if (testPoly.contains(x, y))
				return true;
		}
		return false;
	}
	/**
	 * @param theMap:Map
	 */
	public void setTheMap(Map theMap) {
		this.theMap = theMap;
	}
	/**
	 * @return Map
	 */
	public Map getTheMap() {
		return theMap;
	}
	/**
	 * @param usaSHP:SHPMap
	 */
	public void setUsaSHP(SHPMap usaSHP) {
		this.usaSHP = usaSHP;
	}
	/**
	 * @return SHPMap
	 */
	public SHPMap getUsaSHP() {
		return usaSHP;
	}
}
