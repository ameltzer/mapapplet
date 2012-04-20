package election_map_applet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;

import map_framework.Renderer;

import dbf_framework.DBFFileIO;
import dbf_framework.DBFTable;

import shp_framework.SHPData;
import shp_framework.SHPDataLoader;
import shp_framework.SHPMap;
import shp_framework.geometry.SHPPolygon;
import shp_framework.geometry.SHPShape;
import static election_map_applet.ElectionMapDataModel.*;
/**
 * This class does all the map rendering inside of a panel (itself). 
 * It knows how to render the shapes as well as the map legend and
 * title. It also manages the viewport, which means the window on the 
 * map that we are currently viewing, which means it manages scale
 * and the viewport location. Rendering settings like fonts and strokes
 * should also be managed here.
 * 
 * @author Richard McKenna, Aaron Meltzer
 **/
public class ElectionMapRenderer extends Renderer
{
	// THE APP, WE NEED IT TO SHARE INFO 
	private ElectionMapDataModel dataModel;
	private File selection;
	private File currentMap;
	private String miniFlagLocation;
	
	// VIEWPORT DATA IS USED FOR ZOOMING IN AND OUT
	// AND VIEWING ONLY A PORTION OF THE MAP
	
	// THIS HELPS US TO PROVIDE PADDING AROUND THE MAP WE ZOOM TO
	public static final double SCALE_MAP_DOWN_FACTOR = 0.8;
	
	// AND THE STYLES WE'LL USE FOR RENDERING THE MAP LINES
	public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
	public static final BasicStroke OUTLINED_POLYGON_STROKE = new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);

	// FONT SETTINGS FOR RENDERING
	public static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 36);
	
	// COLOR SETTINGS FOR RENDERING
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(100, 100, 255);
	public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.CYAN;
	public static final Color TITLE_COLOR = new Color(0, 0, 80);
	/**
	 * This constructor sets up all the rendering settings and gets
	 * ready to render the maps as provided by the data model.
	 * @param initDataModel:ElectionMapDataModel
	 * @throws IOException 
	 */
	public ElectionMapRenderer(ElectionMapDataModel initDataModel) throws IOException
	{
		// WE'LL NEED THIS DURING RENDERING TO GET WHAT TO RENDER
		dataModel = initDataModel;
		polyLocation=-1;
		URL urlCtx = dataModel.getCode();
		URL currentMapUrl = new URL(urlCtx, ElectionMapFileManager.USA_DBF);
		String path = currentMapUrl.getPath();
		selection = new File(path);
		currentMap = new File(path);
		// DEFAULT VIEWPORT STUFF, WHICH WILL BE CHANGED AS SOON
		// AS A MAP IS LOADED
		viewportCenterX = 0;
		viewportCenterY = 0;
		scale = 1;
		// SOME DEFAULT SETUP STUFF
		setBackground(DEFAULT_BACKGROUND_COLOR);
	}
	public void setSizeDependent(int width, int height){
		
	}
	/**
	 * 
	 * @param candidates:Candidate[]
	 * @return Candidate[]
	 * @throws IOException
	 * updates the candidates votes
	 */
	public Candidate[] updateCandidates(Candidate[] candidates) throws IOException{
		DBFTable currentTable = (new DBFFileIO()).loadDBF(selection);
		// not selecting a state/county do this. Otherwise send a different set of data
		if(polyLocation!=-1){
			for(int i=0; i<candidates.length; i++){
				candidates[i].setVotes(null,currentTable.getRecord(polyLocation),currentTable.getNumFields()-(3-i));
			}
		}
		else{
			for(int i=0; i<candidates.length; i++){
				candidates[i].setVotes(currentTable, null, currentTable.getNumFields()-(3-i));
			}
		}
		return candidates;
	}
	
	// ACCESSOR METHODS
	/**
	 * @return double
	 */
	public double getViewportCenterX() { return viewportCenterX; }
	/**
	 * 
	 * @return double
	 */
	public double getViewportCenterY() { return viewportCenterY; }
	/**
	 * 
	 * @return int
	 */
	public int getPolyLocation(){return this.polyLocation;}
	/**
	 * 
	 * @return File
	 */
	public File getCurrentMap(){ return this.currentMap;}
	/**
	 * 
	 * @return File
	 */
	public File getFile(){ return this.selection;}
	/**
	 * 
	 * @param file:File
	 */
	public void setFile(File file){ this.selection=file;		}
	/**
	 * 
	 * @param file:File
	 */
	public void setCurrentMap(File file){ this.currentMap =file;}
	/*** RENDERING METHODS ***/

	/**
	 * Called by the Swing and AWT libraries every time our panel is 
	 * displayed and then when we repaint it. All needed rendering
	 * on the panel must be done here each time.
	 * @param g:Graphics
	 */
	public void paintComponent(Graphics g)
	{
		int height = dataModel.getViewHeight();
		int width = dataModel.getViewWidth();
		// CLEARS THE PANEL TO THE BACKGROUND COLOR (BLUE FOR THE OCEAN)
		super.paintComponent(g);
		// ONLY RENDER IF THERE IS ACTUALLY A MAP TO DRAW
		if (dataModel.isMapLoaded())
		{
			// ONLY ZOOM TO A MAP ONCE
			if (!dataModel.isMapRendered())
			{
				// ZOOM
				zoomToMapBounds();
				
				// AND MAKE SURE WE DON'T ZOOM FOR THIS MAP AGAIN
				dataModel.setMapRendered(true);
			}

			// ONLY RENDER THE MAP IF THE USER HAS SELECTED IT
			renderMap(g);
		
			// NOW THE MAP TITLE
			renderTitle(g);
			try {
				renderLegend(g, height, width);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.buildBiography(g, height, width);
			g.setFont(new Font("Times New Roman", Font.BOLD, 12));
			g.setColor(Color.black);
			g.drawString(Integer.toString(dataModel.getCurrentYear()), width-700, height-70);
		}
	}	
	/**
	 * Renders the actual map, meaning polygon outlines and filling.
	 * @param g:Graphics
	 */
	public void renderMap(Graphics g)
	{
		// WE'LL USE Graphics2D METHODS
		Graphics2D g2 = (Graphics2D)g;
		SHPMap mapData = dataModel.getCurrentSHP();
		if (mapData == null)
			return;
		Iterator<SHPShape> shapesIt = mapData.shapesIterator();

		// RENDER ALL THE SHAPES
		while (shapesIt.hasNext())
		{
			// GET THE SHAPE
			SHPShape shape = shapesIt.next();
			// AND RENDER IT
			shape.render(g2, scale, viewportCenterX, viewportCenterY, getWidth(), getHeight());
			
			// ALWAYS SET THE COLOR BACK TO BLACK (THE DEFAULT)
			g2.setColor(Color.black);
		}

		SHPPolygon highlightedPolygon = dataModel.getHighlightedPolygon();
		// WE WILL OUTLINE THE PART THAT THE POLYGON IS OVER
		if (highlightedPolygon != null)
		{
			g2.setStroke(OUTLINED_POLYGON_STROKE);
			highlightedPolygon.render(g2, scale, viewportCenterX, viewportCenterY, getWidth(), getHeight());
		}
	}
	
	/** 
	 * Renders the title and flag of the current map according to
	 * the data model. These are rendered at the top of the map.
	 * @param g:Graphics
	 */
	public void renderTitle(Graphics g)
	{
		// RENDER THE USA OR STATE FLAG IN THE TOP LEFT
		Image flag = dataModel.getCurrentFlag();
		g.drawImage(flag, 10, 10, null);
		
		// GET THE TITLE OF THE MAP TO RENDER
		String title = dataModel.getCurrentMapName() + MAP_TITLE;
		
		// SWITCH RENDERING SETTINGS FOR THE TITLE
		g.setFont(TITLE_FONT);
		g.setColor(TITLE_COLOR);
		
		// AND MEASURE IT USING THE CURRENT RENDERING SETTINGS
		FontMetrics fm = g.getFontMetrics();
		int titleWidth = fm.stringWidth(title);
		int titleHeight = fm.getHeight();
		
		// NOW LET'S CENTER THE TITLE
		int canvasWidth = getWidth();
		int titleX = (canvasWidth/2) - (titleWidth/2);
		int titleY = titleHeight;
		
		// AND RENDER IT
		g.drawString(title, titleX, titleY);
	}
	/**
	 * 
	 * @param g:Graphics
	 * @throws IOException
	 * This function is responsible for rendering the legend. It sets certain info and calls functions
	 * which will get the data which this function then renders
	 */
	public void renderLegend(Graphics g, int height, int width) throws IOException{
		//always make sure to reinitialize the candidates so as to not have collision between data from past mouseovers
		Candidate[] candidates = new Candidate[3];
		candidates[0] = new Candidate(2, "Barack Obama", Color.BLUE, dataModel.getDemEv());
		candidates[1] = new Candidate(3, "John McCain", Color.RED, dataModel.getRepEv());
		candidates[2] = new Candidate(4, "Other", Color.GRAY, 0);
		//draw the rectangle
		g.drawRect(height+236,width-688, 290, 150);
		//set the color to fill
		g.setColor(new Color(248,248,255));
		//fill the rectangle
		g.fillRect(height+237, width-687, 289, 149);
		//set the font and color to draw the abbr
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g.setColor(Color.BLACK);
		g.drawString(this.dataModel.getStateAbbr(), width-260, height-149);
		if(dataModel.getCurrentMapAbbr().equals("USA"))
			this.miniFlagLocation=this.dataModel.getStateAbbr();
		//draw the miniflag
		g.drawImage(this.dataModel.getMiniFlags().get(this.miniFlagLocation), width-260, height-139, null);
		//update the candidates
		Candidate[] unsortedCan = new Candidate[candidates.length];
		for(int i=0; i<candidates.length; i++)
			unsortedCan[i]=candidates[i];
		candidates = this.updateCandidates(candidates);
		//sort the candidates
		candidates = dataModel.sortArray(candidates);
		//turn them into strings
		String[] votes = dataModel.buildStrings(candidates, selection);
		//set the font and loop through to render all the strings
		g.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		for(int i=0; i<votes.length; i++){
			g.setColor(candidates[i].getColor());
			g.drawString(votes[i], width-260, 675+((i+1)*13));
		}
		//draw the horizontal line
		//g.drawLine(1005, 715, 1200, 715);
		g.drawLine(width-260, height-59, width-80, height-59);
		//render the total votes in this given country or state
		g.setColor(Color.BLACK);
		g.drawString(dataModel.totalVotesString(selection), width-260, height-44);
		
		this.redrawBar(unsortedCan, g,height, width);
		int totalEv=0;
		for(int i=0; i<candidates.length; i++)
			totalEv+=candidates[i].getEv();
		for(int i=0; i<candidates.length-1; i++){
			if(candidates[i].getEv()>=(totalEv/2)){
				this.victory(candidates[i], g, height, width);
				break;
			}
		}
	}
	// VIEWPORT SETUP FUNCTIONS
	
	/**
	 * This method makes sure the viewport has not gone off the map. If the
	 * latitude or longitude is illegal, it gets corrected.
	 */
	public void correctViewport()
	{
		double longWidth = 360/scale;
		double minLong = viewportCenterX - (longWidth/2);
		double maxLong = viewportCenterX + (longWidth/2);

		double latHeight = 180/scale;
		double minLat = viewportCenterY - (latHeight/2);
		double maxLat = viewportCenterY + (latHeight/2);
	
		// CORRECT VIEWPORT IF IT WENT OFF THE WESTERN EDGE
		double diff = minLong - (-220);
		if (diff < 0)
		{
			viewportCenterX -= diff;
		}
		// OR OFF THE EASTERN EDGE
		else
		{
			diff = 180 - maxLong;
			if (diff < 0)
			{
				viewportCenterX += diff;
			}
		}

		// OR OFF THE SOUTHERN EDGE
		diff = minLat - (-90);
		if (diff < 0)
		{
			viewportCenterY -= diff;			
		}
		// OR THE NORTHERN EDGE
		else
		{
			diff = 90 - maxLat;
			if (diff < 0)
			{
				viewportCenterY += diff;
			}
		}
	}	

	/**
	 * This method moves the viewport to the arguments location,
	 * correcting if necessary.
	 * @param longCenter:double
	 * @param latCenter:double
	 */
	public void setCenter(double longCenter, double latCenter)
	{
		viewportCenterX = longCenter;
		viewportCenterY = latCenter;
		correctViewport();
	}

	/**
	 * This method moves the viewport to the pixel location. Note that this 
	 * method takes pixel locations x and y and must convert them 
	 * into corresponding lat/lon values.
	 * @param x:int
	 * @param y:int
	 */
	public void setCenter(int x, int y)
	{
		double longWidth = 360/scale;
		double latHeight = 180/scale;
		double longPerPixel = longWidth/getWidth();
		double latPerPixel = latHeight/getHeight();
		
		double longDist = (x - (getWidth()/2)) * longPerPixel;
		double latDist = ((getHeight()/2) - y) * latPerPixel;
		
		double xLong = longDist + viewportCenterX;
		double yLong = latDist + viewportCenterY;
		
		viewportCenterX = xLong;
		viewportCenterY = yLong;
		correctViewport();
	}

	// ZOOM FUNCTIONS
	
	/**
	 * This method will zoom our viewport in on the region
	 * represented by the x,y arguments. Note that these are
	 * pixel coordinates, so we must convert to lat,long. Also
	 * note that we have to determine the proper scale.
	 * @param x1:int
	 * @param y1:int
	 * @param x2:int
	 * @param y2:int
	 */
	public void zoom(int x1, int y1, int x2, int y2)
	{
		// ONLY ZOOM IF THIS IS A VALID ZOOM RECTANGLE
		if ((x2 > x1) && (y2 > y1))
		{
			double longWidth = 360/scale;
			double latHeight = 180/scale;
			double longPerPixel = longWidth/getWidth();
			double latPerPixel = latHeight/getHeight();
			
			double longDist1 = (x1 - (getWidth()/2)) * longPerPixel;
			double longDist2 = (x2 - (getWidth()/2)) * longPerPixel;
			double latDist1 = ((getHeight()/2) - y1) * latPerPixel;
			double latDist2 = ((getHeight()/2) - y2) * latPerPixel;
			
			double long1 = longDist1 + viewportCenterX;
			double long2 = longDist2 + viewportCenterX;
			double lat1 = latDist1 + viewportCenterY;
			double lat2 = latDist2 + viewportCenterY;
			//if not Alaska, do this, otherwise zoom to Alaska
			if(this.polyLocation!=0){
				viewportCenterX = ((long1 + long2)/2);
				viewportCenterY = ((lat1 + lat2)/2);
				// THE PROVIDED RECT WILL LIKELY NOT HAVE THE SAME ASPECT
				// RATIO OF THE VIEWPORT, SO LET'S FIGURE OUT 
				// THE PROPER SCALING FACTORS FOR X AND Y
				double scaleX = 360/(long2-long1);
				double scaleY = 180/(lat1-lat2);
				if (scaleX > scaleY)
					scale = scaleY;
				else
					scale = scaleX;
			}
			else{
				viewportCenterX = -155;
				this.viewportCenterY = 60;
				scale = 8;
			}
			// AND NOW LET'S ZOOM OUT SLIGHTLY TO
			// MAKE IT LOOK A LITTLE BETTER
			scale *= SCALE_MAP_DOWN_FACTOR;

			// CORRECT THE VIEWPORT IF NECESSARY, WE DON'T WANT TO SCALE OFF THE MAP
			correctViewport();
		}
	}

	/**
	 * This can be used to zoom to perfectly fit the bounding box of
	 * a shape, so the viewport moves right to a region.
	 */
	public void zoomToMapBounds()
	{
		SHPMap mapData = dataModel.getCurrentSHP();
		double[] mapBounds = mapData.getShapefileData().getMBR();
		int x1 = xCoordinateToPixel(mapBounds[0]);
		int y1 = yCoordinateToPixel(mapBounds[3]);
		int x2 = xCoordinateToPixel(mapBounds[2]);
		int y2 = yCoordinateToPixel(mapBounds[1]);
		zoom(x1, y1, x2, y2);
	}

	/**
	 * 
	 * @param abr:String
	 * This map handles zooming including calling the zoom function given to us
	 */
	public void zoomHandler(String abr){
		String location =ElectionMapFileManager.MAPS_DIR+abr;
		URL context = dataModel.getCode();
		URL mapURL=null;
		try {
			mapURL = new URL(context, location+".shp");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SHPMap map =null;
		try {		
			map = new SHPDataLoader().loadShapefile(new File(mapURL.getPath()));
			dataModel.setCurrentSHP(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SHPData data =map.getShapefileData();
		double[] array2 = data.getMBR();
		this.zoom((int)this.xCoordinateToPixel(array2[0]), (int)this.yCoordinateToPixel(array2[3]), 
				(int)this.xCoordinateToPixel(array2[2]), (int)this.yCoordinateToPixel(array2[1]));
		File currentFile = new File(location+".dbf");
		dataModel.colorSections(map, currentFile);
		dataModel.getRenderer().setFile(currentFile);
		dataModel.setCurrentMapAbbr(abr);
		if(abr!="USA")
			dataModel.setMapRendered(false);
		else
			dataModel.setMapRendered(true);
		dataModel.resetHighlightedRegion();
		this.repaint();
	}
	/**
	 * 
	 * @param info:String
	 */
	public void buildBiography(/*String name, String bio,*/ Graphics g, int height, int width){
		/**
		 * Only the white rectangle and outline are being drawn, 
		 * the functionality for biographies will be completed in HW6
		 */
		//String result = "\t"+name +"\n";
	//	StringTokenizer body = new StringTokenizer(bio);
	//	int i=0;
	//	while(body.hasMoreElements()){
	//		result+=body.nextToken();
	//		i++;
	//		if(i%5==0)
	//			result+="\n";
	//	}
		//draw the rectangle
		/*g.drawRect(height+236,width-688, 290, 150);
		//set the color to fill
		g.setColor(new Color(248,248,255));
		//fill the rectangle
		g.fillRect(height+237, width-687, 289, 149);*/
		g.setColor(Color.black);
		g.drawRect(height+236, width-842, 290, 150);
		//set the color to fill
		g.setColor(new Color(248,248,255));
		//fill the rectangle
		g.fillRect(height+237, width-841, 289, 149);
		//g.drawString(result ,1002, 401);
	}
	/**
	 * 
	 * @param year:int
	 */
	public void timelineHandler(int year){
		
	}
	/**
	 * 
	 * @param year:int
	 */
	public void redrawTimeline(int year, Graphics g){
	
	}
	/**
	 * 
	 * @param votes:int[]
	 */
	public void redrawBar(Candidate[] votes,  Graphics g, int height, int width){
		int x =width-1088;
		int y = height-720;
		double totalVotes= 0;
		for(int i=0; i<votes.length; i++)
			totalVotes+= votes[i].getEv();
		for(int i=0; i<votes.length; i++){
			double percentage = votes[i].getEv()/(totalVotes);
			double wideness=percentage * (width-400);
			//set the color to fill
			g.setColor(votes[i].getColor());
			g.drawRect(x, y, (new BigDecimal(wideness)).intValue(), 15);
			//fill the rectangle
			g.fillRect(x, y, (new BigDecimal(wideness)).intValue(), 15);
			x+=wideness;
		}
	}
	/**
	 * 
	 * @param name:String
	 */
	public void victory(Candidate winner, Graphics g, int height, int width){
		//String output = winner.getName() + " WON!";
		String output = winner.getName()+" WON!";
		StringTokenizer outputTokens = new StringTokenizer(output);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		int x = width-200;
		int displacement=0;
		for(int i=0; outputTokens.hasMoreTokens(); i++){
			String currentOutput= outputTokens.nextToken();
			if(outputTokens.hasMoreTokens()){
				g.setColor(winner.getColor());
				g.drawString(currentOutput, x+(displacement*11), height-114);
			}
			else{
				g.setColor(Color.green);
				g.drawString(currentOutput, x+(displacement*11), height-114);
			}
			displacement+=currentOutput.length()+1;
		}
	}
	/**
	 * 
	 * @param year:int
	 */
	public void changeYear(int year){
		
	}
}