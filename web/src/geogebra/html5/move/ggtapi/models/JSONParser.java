package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.web.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 *	JSON parser for web and touch, to return common JSONValue-s
 */
public class JSONParser {

	/**
	 * @param json javascript object got from JSON.parse
	 * @return  JSONObject a common json object
	 */
	public static JSONObject parseToJSONObject(JavaScriptObject json) {
	   JSONObject ret = new JSONObject();
	   String [] keys = JSONParser.getKeys(json);
	   for (int i = 0, k = keys.length; i < k; i++) {
		   ret.put(keys[i], new JSONString(JSON.get(json, keys[i])));
	   }	   
	   return ret;
    }

	private static native String[] getKeys(JavaScriptObject json) /*-{
		var keys = Object.keys(json);
		return keys;
    }-*/;

}
