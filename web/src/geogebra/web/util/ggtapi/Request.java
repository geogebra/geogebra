package geogebra.web.util.ggtapi;

/**
 * @author gabor
 *	All GGT Request class should implement it
 */
public interface Request {
	
	/**
	 * @return JSON.stringify of the given Request
	 */
	public String toJSONString();

}
