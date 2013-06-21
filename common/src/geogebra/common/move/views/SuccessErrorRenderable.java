package geogebra.common.move.views;

import geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * @author gabor
 * 
 * renderable class for success - error operations
 *
 */
public interface SuccessErrorRenderable {
	
	/**
	 * @param response from GGT
	 */
	public void success(JSONObject response);
	
	/**
	 * @param resonse from GGT
	 */
	public void fail(JSONObject resonse);

}
