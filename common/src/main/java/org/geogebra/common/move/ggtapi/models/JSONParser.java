package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * @author gabor
 * Common parser, must be set from Web/Touch/Desktop
 */ 
public interface JSONParser {

	/**
	 * @param response parser a response into JSONObject
	 * @return
	 */
	JSONObject parseStrict(String response);

}
