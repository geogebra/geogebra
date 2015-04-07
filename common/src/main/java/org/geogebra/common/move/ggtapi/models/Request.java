package org.geogebra.common.move.ggtapi.models;


/**
 * @author gabor
 *	All GGT Request class should implement it
 */
public interface Request {
	
	/**
	 * @return JSON.stringify of the given Request
	 */
	public String toJSONString(ClientInfo client);

}
