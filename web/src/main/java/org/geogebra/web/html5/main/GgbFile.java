package org.geogebra.web.html5.main;

import java.util.HashMap;

public class GgbFile extends HashMap<String, String> {

	public GgbFile duplicate() {
		GgbFile copy = new GgbFile();
		for (Entry<String, String> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

}
