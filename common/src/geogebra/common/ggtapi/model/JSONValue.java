package geogebra.common.ggtapi.model;

/**
 * @author gabor
 * 
 * represents the superclass of JSONObject and JSONArray
 *
 */
public abstract class JSONValue {
	
	/**
	 * @return the JSON representation of the object
	 */
	public abstract String jsonToString();

}
