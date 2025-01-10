package org.geogebra.common.move.ggtapi.models;

/**
 * Interface for all geogebra.org JSON API requests
 * @author gabor
 */
public interface Request {

	/**
	 * @param client
	 *            client
	 * @return JSON.stringify of the given Request
	 */
	public String toJSONString(ClientInfo client);

}
