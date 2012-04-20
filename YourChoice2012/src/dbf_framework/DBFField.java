package dbf_framework;
/**
 * This type of object would store information about a particular
 * column, like what type of data will be stored there.
 * 
 * @author Richard McKenna, Aaron Meltzer
 */
public class DBFField 
{
	// THIS IS ALL THE DATA FOR A GIVEN COLUMN HEADER
	private String name;
	private DBFFieldType type;
	private int displacement;
	private int length;
	private int numberOfDecimalPlaces;
	private byte flags;
	private int next;
	private int step;
	private long reservedData;

	// ACCESSOR METHODS
	/**
	 * @return int
	 */
	public int				getDisplacement()			{ return displacement; 					}
	/**
	 * 
	 * @return byte
	 */
	public byte				getFlags()					{ return flags;							}
	/**
	 * 
	 * @return int
	 */
	public int				getLength()					{ return length;						}
	/**
	 * @return String
	 */
	public String 			getName() 					{ return name; 							}
	/**
	 * @return int
	 */
	public int				getNext()					{ return next;							}
	/**
	 * @return int
	 */
	public int				getNumberOfDecimalPlaces()	{ return numberOfDecimalPlaces;			}
	/**
	 * @return long
	 */
	public long				getReservedData()			{ return reservedData;					}
	/**
	 * @return int
	 */
	public int				getStep()					{ return step;							}
	/**
	 * @return DBFFieldType
	 */
	public DBFFieldType		getType()					{ return type; 							}

	// MUTATOR METHODS
	/**
	 * @param initDisplacement:int
	 */
	public void setDisplacement(int initDisplacement)	{ displacement = initDisplacement; 	}
	/**
	 * 
	 * @param initFlags initFlags:byte
	 */
	public void setFlags(byte initFlags)				{ flags = initFlags; 				}
	/**
	 * 
	 * @param initLength:int
	 */
	public void setLength(int initLength)				{ length = initLength;				}
	/**
	 * 
	 * @param initName:String
	 */
	public void setName(String initName) 				{ name = initName; 					}
	/**
	 * 
	 * @param initNext:int
	 */
	public void setNext(int initNext)					{ next = initNext;					}
	/**
	 * 
	 * @param initNODP:int
	 */
	public void setNumberOfDecimalPlaces(int initNODP)	{ numberOfDecimalPlaces = initNODP;	}
	/**
	 * 
	 * @param initRD:long
	 */
	public void setReservedData(long initRD)			{ reservedData = initRD;			}
	/**
	 * 
	 * @param initStep:int
	 */
	public void setStep(int initStep)					{ step = initStep;					}
	/**
	 * 
	 * @param initType:DBFFieldType
	 */
	public void setType(DBFFieldType initType)			{ type = initType; 					}
}