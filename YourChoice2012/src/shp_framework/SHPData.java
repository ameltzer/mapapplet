package shp_framework;

import java.util.Iterator;
import java.util.Vector;
import shp_framework.geometry.SHPShape;
/**
 * SHPData - This class stores all of the geometric data for a given map. Note
 * that a map is made up of shapes, each of which may have many parts. Note that
 * a part is a single polygon. Note that all data stored inside this class is
 * in geographic coordinates, meaning latitude and longitude, where east is
 * positive and west is negative, north is positive and south is negative.
 * 
 * @author Richard McKenna
 */
public class SHPData 
{
	// HERE'S ALL THE GEOMETRY FOR THE MAP
	private Vector<SHPShape> shapes;

	// HERE'S SHAPEFILE DATA
	private int fileCode;
	private int[] unusedBytes;
	private int fileLength;
	private int version;
	private int shapeType;
	private double[] mbr;
	private double[] zBounds;
	private double[] mBounds;

	/**
	 * This constructor just sets up our shapes data structure. The geometric
	 * data will be loaded and unloaded as needed.
	 */
	public SHPData()
	{
		shapes = new Vector<SHPShape>();
	}

	// ACCESSOR METHODS
	/**
	 * @return int
	 */
	public int					getFileCode()		{ return fileCode;			}
	/**
	 * @return int
	 */
	public int					getFileLength()		{ return fileLength;		}
	/**
	 * @return double[]
	 */
	public double[]				getMBounds()		{ return mBounds;			}
	/**
	 * @return double[]
	 */
	public double[]				getMBR()			{ return mbr;				}
	/**
	 * @return SHPShape
	 */
	public SHPShape 			getShape(int index)	{ return shapes.get(index);	}
	/**
	 * @return int
	 */
	public int					getShapeType()		{ return shapeType;			}
	/**
	 * @return Vector<SHPShape>
	 */
	public Vector<SHPShape> 	getShapes()			{ return shapes; 			}
	/**
	 * @return int[]
	 */
	public int[]				getUnusedBytes()	{ return unusedBytes;		}
	/**
	 * @return int
	 */
	public int					getVersion()		{ return version;			}
	/**
	 * @return double[]
	 */
	public double[]				getZBounds()		{ return zBounds;			}

	// ITERATOR - FOR GOING THROUGH ALL THE SHAPES ONE AT A TIME	
	/**
	 * @return Iterator<SHPShape>
	 */
	public Iterator<SHPShape> shapesIterator()
	{
		return shapes.iterator();
	}

	// MUTATOR METHODS
	/**
	 * @param shapeToAdd:SHPShape
	 */
	public void addShape(SHPShape shapeToAdd)
	{
		shapes.add(shapeToAdd);
	}
	/**
	 * 
	 * @param initShapes:Vector<SHPShape>
	 */
	public void setShapes(Vector<SHPShape> initShapes)
	{
		shapes = initShapes;
	}
	/**
	 * 
	 * @param initFileCode:int
	 */
	public void setFileCode(int initFileCode)
	{
		fileCode = initFileCode;
	}
	/**
	 * 
	 * @param initUnusedBytes:int[]
	 */
	public void setUnusedBytes(int[] initUnusedBytes)
	{
		unusedBytes = initUnusedBytes;
	}
	/**
	 * 
	 * @param initFileLength:int
	 */
	public void setFileLength(int initFileLength)
	{
		fileLength = initFileLength;		
	}
	/**
	 * 
	 * @param initVersion:int
	 */
	public void setVersion(int initVersion)
	{
		version = initVersion;
	}
	/**
	 * 
	 * @param initShapeType:int
	 */
	public void setShapeType(int initShapeType)
	{
		shapeType = initShapeType;
	}
	/**
	 * 
	 * @param initMBR:double[]
	 */
	public void setMBR(double[] initMBR)
	{
		mbr = initMBR;
	}
	/**
	 * 
	 * @param initZBounds:double[]
	 */
	public void setZBounds(double[] initZBounds)
	{
		zBounds = initZBounds;
	}
	/**
	 * 
	 * @param initMBounds:double[]
	 */
	public void setMBounds(double[] initMBounds)
	{
		mBounds = initMBounds;
	}
}