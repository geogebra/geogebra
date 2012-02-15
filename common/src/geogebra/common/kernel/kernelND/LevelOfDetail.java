package geogebra.common.kernel.kernelND;

/**
 * Class to set the level of detail of surface, curve, etc.
 * @author matthieu
 *
 */
public class LevelOfDetail {
	
	/**
	 * default level of detail
	 */
	public static int LEVEL_OF_DETAIL_DEFAULT = 5;
	
	private int value;
	
	/**
	 * constructor
	 */
	public LevelOfDetail(){
		setValue(LEVEL_OF_DETAIL_DEFAULT);
	}

	/**
	 * sets the value
	 * @param val value
	 */
	public void setValue(int val){
		value = val;
	}
	
	/**
	 * 
	 * @return the value
	 */
	public int getValue(){
		return value;
	}
	
	
}
