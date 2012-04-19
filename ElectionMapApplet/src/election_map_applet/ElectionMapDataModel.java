package election_map_applet;

import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import map_framework.DataModel;

import dbf_framework.DBFFileIO;
import dbf_framework.DBFRecord;
import dbf_framework.DBFTable;

import shp_framework.SHPMap;
import shp_framework.geometry.SHPShape;

/**
 * This class serves as the data manager for all our application's 
 * core data. In in addition to initializing data, it provides service
 * methods for event handlers to use in manipulating the maps.
 * 
 * @author Richard McKenna, Aaron Meltzer
 **/
public class ElectionMapDataModel extends DataModel
{
	// HERE'S THE MAP'S RENDERER, WHICH WE NEED TO NOTIFY WHENEVER
	// THERE ARE CHANGES TO DATA SO THAT IT REPAINTS ITSELF\
	private ElectionMapRenderer renderer;
	
	// HERE'S THE FILE MANAGER, WHICH WILL HELP WITH LOADING OF
	// STATE MAPS WHEN NEEDED
	private ElectionMapFileManager fileManager;

	//abr of a state
	private String stateAbbr;
	
	// THIS IS FOR HIGHLIGHTING A PART OF A POLYGON (LIKE A COUNTY OR STATE)

	// USED FOR TITLES AND THE USA MAP
	public static final String USA_MAP_NAME = "USA";
	public static final String USA_MAP_ABBR = "USA";
	
	// .gif IMAGES ARE THE MINI IMAGES
	
	// THIS HELPS US TO KNOW IF A MAP HAS BEEN
	// RENDERED AT LEAST ONCE OR NOT
	
	private String currentBio;
	private String[] allBios;
	private int currentYear;
	private Candidate[] candidates;
	private int viewWidth;
	private int viewHeight;
	private int demEv;
	private int repEv;
	private int totalEv;
	
	/**
	 * Note that the data model is not fully setup after this constructor. It
	 * still needs the renderer and the file manager, which should be loaded
	 * when ready via the init method.
	 **/
	public ElectionMapDataModel()
	{
		
		// AND INITIALIZE OUR DATA STRUCTURES
		testPoly = new Polygon();
		flags = new TreeMap<String, Image>();
		miniFlags = new TreeMap<String, Image>();
		stateAbbr = "USA";
		
		// THE MAP HAS NOT YET BEEN RENDERED
		mapRendered = false;
		sections = new DBFTable();
		demEv=0;
		repEv=0;
	}
	public int getDemEv(){ return this.demEv;}
	public int getRepEv(){ return this.repEv;}
	public void setViewWidth(int width){
		viewWidth=width;
	}
	public void setViewHeight(int height){
		viewHeight=height;
	}
	public int getViewWidth(){
		return this.viewWidth;
	}
	public int getViewHeight(){
		return this.viewHeight;
	}
	// SIMPLE ACCESSOR METHODS
	/**
	 * 
	 * @return ElectionMapRenderer
	 */
	public ElectionMapRenderer getRenderer() { return renderer;					}
	/**
	 * 
	 * @param abbr:String
	 */
	public void setCurrentStateAbbr(String abbr)		{ this.stateAbbr =abbr;}
	/**
	 * 
	 * @return String
	 */
	public String getStateAbbr()	{return this.stateAbbr;}
	// MORE COMPLEX ACCESSOR METHODS
	
	/**
	 * 
	 * @param candidates:Candidate[]
	 * @param file:File
	 * @return String[]
	 * @throws IOException
	 *  This function takes an array of candidates and a file, extracts information from the file and adds them to the
	 * correct candidate. It then constructs the string for a specific candidate and stores it in an array.
	 * Finally it returns the array of Strings for future use
	 *
	 */
	public String[]  buildStrings(Candidate[] candidates, File file) throws IOException{
		String[] votes = new String[candidates.length];
		DBFRecord theRecord;
		DBFTable currentTable = (new DBFFileIO().loadDBF(file));
		//if a state is selected
		if(renderer.getPolyLocation()!=-1)
			theRecord = (currentTable.getRecord(renderer.getPolyLocation()));
		else
			theRecord = currentTable.getRecord(0);//default record, only to satisfy the arguments
		//for each candidate create a string
		for(int i=0; i<candidates.length; i++){
			BigDecimal divisor = totalVotes(file, theRecord);
			BigDecimal numerator = candidates[i].getVotes();
			if(divisor.intValue()==0){
				divisor=BigDecimal.ONE;
				numerator=BigDecimal.ZERO;
			}
			votes[i] = candidates[i].getName() +": " + addCommas(candidates[i].getVotes()) + " Votes ("
			+ (numerator.divide(divisor,new MathContext(2))).multiply(new BigDecimal(100)).intValue()
					+"%)";
		}
		return votes;
	}
	/**
	 * 
	 * @param number:BigDecimal
	 * @return String
	 * this function takes in a number, makes it a string and adds in the commas
	 */
	public String addCommas(BigDecimal number){
		String numberString=number.toString();
		int[] commaPositions = new int[4];
		int inversePosition=0;
		int commaIterator=0;
		//find out where the commas should be placed
		for(int i= numberString.length()-1; i>=0; i--){
			if(inversePosition%3==0 && inversePosition!=0){
				commaPositions[commaIterator]=i;
				commaIterator++;
			}
			inversePosition++;
		}
		//since the above array will be done backward go to the last useful number int he array
		for(int i=0; i<commaPositions.length; i++){
			if(commaPositions[i]==0)
				break;
			commaIterator= i;
		}
		//if numberString.length()%3==1 then a comma needs to be added after the zero place, so move on to the next 0
		if(numberString.length() % 3==1){
			commaIterator++;
		}
		String temp = "";
		//add the commas
		for(int i=0; i<numberString.length(); i++){
			temp= temp.concat(Character.toString(numberString.charAt(i)));
			if(commaIterator>-1 && i == commaPositions[commaIterator]){
				temp=temp.concat(",");
				commaIterator--;
			}
		}
		return temp;
	}
	/**
	 * 
	 * @param candidate:int
	 * @param file:File
	 * @return BigDecimal
	 * @throws IOException
	 */
	public BigDecimal candidateVotes(int candidate, File file) throws IOException{
		//start at 0
		BigDecimal candidateVotes = BigDecimal.ZERO;
		Iterator<DBFRecord> iterator = (new DBFFileIO()).loadDBF(file).getTree().iterator();
		//add the candidates votes together. A candidate has a specific point in the array, which the calling
		//function will be responsible for telling this function.
		while(iterator.hasNext()){
			DBFRecord next = iterator.next();
			candidateVotes = candidateVotes.add(new BigDecimal((Long)next.getData(candidate)));
		}
		return candidateVotes;
	}
	/**
	 * 
	 * @param array:Candidate[]
	 * @return Candidate[]
	 * @throws IOException
	 *  sorts the candidate array based on who has the most votes
	 */
	public Candidate[] sortArray(Candidate[] array) throws IOException{
		for(int i=0; i<array.length; i++){
			Candidate temp = new Candidate(2, "temp", Color.black,0);
			temp.setDefaultVotes(new BigDecimal(-1));
			int k=0;
			for(int j=i; j<array.length; j++){
				if(temp.getVotes().compareTo(array[j].getVotes())<0){
					temp = array[j];
					k=j;
				}
			}
			array[k]=array[i];
			array[i]=temp;
		}
		return array;
	}
	/**
	 * 
	 * @param file:File
	 * @return String
	 * @throws IOException
	 *  take info from the file, finds the total number of votes and constructs the string
	 */
	public String totalVotesString(File file) throws IOException{
		String votes ="";
		//the table
		DBFTable currentTable = (new DBFFileIO()).loadDBF(file);
		DBFRecord theRecord=null;
		//if not selecting a county/state get a certain record
		if(renderer.getPolyLocation()!=-1)
			theRecord = (currentTable.getRecord(renderer.getPolyLocation()));
		else
			theRecord = currentTable.getRecord(0);//default record, only to satisfy the arguments
		BigDecimal totalVotes = totalVotes(file, theRecord);
		//construct string
		votes = "Total: " + this.addCommas(totalVotes) + "Votes (100%)";
		return votes;
	}
	/**
	 * 
	 * @param file:Fle
	 * @param theRecord:DBFRecord
	 * @return BigDecimal
	 * @throws IOException
	 * A helper method for the method above this calculates the total votes.
	 */
	public BigDecimal totalVotes(File file, DBFRecord theRecord) throws IOException{
		BigDecimal totalVotes = BigDecimal.ZERO;
		if(renderer.getPolyLocation()==-1){
			Iterator<DBFRecord> record= (new DBFFileIO()).loadDBF(file).getTree().iterator();
			while(record.hasNext()){
				DBFRecord next = record.next();
				for(int i=0; i<3; i++){
					totalVotes = totalVotes.add(new BigDecimal((Long)next.getData(next.getNumFields()-(3-i))));
				}
			}
		}
		else{
			for(int i=0; i<3; i++)
				totalVotes = totalVotes.add(new BigDecimal((Long)theRecord.getData(theRecord.getNumFields()-(3-i))));
		}
			
		return totalVotes;
	}
	// MUTATOR METHODS
	
	/**
	 * @param initRenderer:ElectionMapRenderer
	 * @param initFileManager:ElectionMapFileManager
	 * This method completes the setup of this data model. We'll need the 
	 * renderer and fileManager to properly process event responses.
	 **/
	public void init(	ElectionMapRenderer initRenderer,
						ElectionMapFileManager initFileManager)
	{
		// SAVE THESE GUYS FOR LATER
		renderer = initRenderer;
		fileManager = initFileManager;
	}
	/**
	 * 
	 * @param map:SHPMap
	 * @param file:File
	 */
	public void colorSections(SHPMap map, File file){
		try {
			//set sections to the relevant DBFTable
			sections = (new DBFFileIO()).loadDBF(file);
			initShapeColors(map);//color in sections
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param map:SHPMap
	 * For the provided map and table arguments, this method initializes
	 * all the shape regions in the map with the colors of the winner
	 * in the table's election results.
	 **/
	public void initShapeColors(SHPMap map)
	{
		// INITIALIZE THE COLORS
		Iterator<SHPShape> shapesIt = map.shapesIterator();
		for (int i=0; shapesIt.hasNext(); i++)
		{
			//get the next shape
			SHPShape shape = shapesIt.next();
			//find the number of fields
			int numFields = this.sections.getNumFields();
			DBFRecord record = this.sections.getTree().get(i);
			//if Obama has more votes, set it to blue. if McCain has more votes, set it to Red. Otherwise, set it to yellow
			if((Long)record.getData(numFields-3)>(Long)record.getData(numFields-2)){
				demEv+=(Long)record.getData(numFields-3);
				shape.setFillColor(Color.BLUE);
				
			}
			else if((Long)record.getData(numFields-3)<(Long)record.getData(numFields-2)){
				repEv+=(Long)record.getData(numFields-2);
				shape.setFillColor(Color.RED);
			}
			else{
				shape.setFillColor(Color.YELLOW);
			}
		}
	}	

	
	/**
	 * @param initUSAshp:SHPMap
	 * This method sets the USA map, including the shp and
	 * dbf data. Note that it does not force a repaint.
	 **/
	public void initUSAMap(SHPMap initUSAshp)
	{
		currentMapName = USA_MAP_NAME;
		currentMapAbbr = USA_MAP_ABBR;
		this.currentOverallAbbr = USA_MAP_ABBR;
		usaSHP = initUSAshp;
		currentSHP= usaSHP;
	}
	
	

	
	/**
	 * A helper method for switching the map in use to the original
	 * USA map.
	 **/
	private void switchToUSAMap()
	{
		currentMapName = USA_MAP_NAME;
		currentMapAbbr = USA_MAP_ABBR;
	}
	/**
	 * 
	 * @param name:String
	 * @param bio:String
	 * @return String
	 */
	public String addBios(String name, String bio){
		String result = name +"\n";
		StringTokenizer body = new StringTokenizer(bio);
		int i=0;
		while(body.hasMoreElements()){
			result+=body.nextToken();
			i++;
			if(i%5==0)
				result+="\n";
		}
		return result;
	}
}