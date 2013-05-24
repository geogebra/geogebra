package geogebra.common.ggtapi.model;

import java.util.HashMap;

public class JSONObject extends JSONValue {
	
	
	private HashMap<String, String> json;
	
	public JSONObject() {
		this.json = new HashMap<String, String>();
	}

	@Override
	public String jsonToString() {
		StringBuffer sb = new StringBuffer();
	    sb.append("{");
	    boolean first = true;
	    String[] keys = computeKeys();
	    for (String key : keys) {
	      if (first) {
	        first = false;
	      } else {
	        sb.append(", ");
	      }
	      sb.append(JsonUtils.escapeValue(key));
	      sb.append(":");
	      sb.append(get(key));
	    }
	    sb.append("}");
	    return sb.toString();
	}

	/**
	 * @param key key to return
	 * @return	value referenced by the key
	 */
	public String get(String key) {
		if (json.containsKey(key)) {
			return json.get(key);
		}
		return null;
	}

	private String[] computeKeys() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param key key to index the value
	 * @param value value indexed by the key (null deletes the key)
	 */
	public void put(String key, String value) {
		if (json.containsKey(key)) {
			if (value == null) {
				json.remove(key);
				return;
			}
		}
		json.put(key, value);
	}
 
}
