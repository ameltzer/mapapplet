package map_framework;

import javax.swing.JPanel;
/**
 * 
 * @author Aaron Meltzer
 *
 */
public class Renderer extends JPanel {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected double viewportCenterX;
	protected double viewportCenterY;
	protected int polyLocation;
	protected double scale;
	/**
	 * 
	 * @param polyNumber:int
	 */
	public void setPolyNumber(int polyNumber) { this.setPolyLocation(polyNumber);}
/*** CONVERSION FUNCTIONS ***/
	
	/**
	 * This calculates and returns the longitude value that
	 * corresponds to the xPixel argument.
	 * @param xPixel:int
	 * @return double
	 */
	public double pixelToXCoordinate(int xPixel)
	{
		double longWidth = 360/scale;
		double minLong = viewportCenterX - (longWidth/2);
	
		// SCALE THE PIXEL
		double percentLong = ((double)xPixel)/getWidth();
		double xLong = minLong + (percentLong * longWidth);
		return xLong;
	}

	/**
	 * This calculates and returns the latitude value that
	 * corresponds to the yPixel argument.
	 * @param yPixel:int
	 * @return double
	 */
	public double pixelToYCoordinate(int yPixel)
	{
		double latHeight = 180/scale;
		double minLat = viewportCenterY - (latHeight/2);
	
		// SCALE THE PIXEL
		double percentLat = ((double)yPixel)/getHeight();
		double yLat = minLat + (percentLat * latHeight);
		return yLat;
	}
	/**
	 * This calculates and returns the x pixel value that
	 * corresponds to the xCoord longitude argument.
	 * @param xCoord:double
	 * @return int
	 */
	public int xCoordinateToPixel(double xCoord)
	{
		double longWidth = 360/scale;
		double minLong = viewportCenterX - (longWidth/2);
	
		// SCALE THE COORDINATE
		double percentX = (xCoord - minLong)/longWidth;
		int panelWidth = getWidth();
		double coord = panelWidth * percentX;
		return (int)Math.round(coord);
	}

	/**
	 * This calculates and returns the y pixel value that
	 * corresponds to the yCoord latitude argument.
	 * @param yCoord:double
	 * @return int
	 */
	public int yCoordinateToPixel(double yCoord)
	{
		double latHeight = 180/scale;
		double minLat = viewportCenterY - (latHeight/2);
	
		// SCALE THE COORDINATE
		double percentY = (yCoord - minLat)/latHeight;
		yCoord = getHeight() * percentY;
		return getHeight() - ((int)yCoord);
	}
	/**
	 * @param polyLocation:int
	 */
	public void setPolyLocation(int polyLocation) {
		this.polyLocation = polyLocation;
	}
	/**
	 * @return int
	 */
	public int getPolyLocation() {
		return polyLocation;
	}
}
