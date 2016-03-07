package com.himamis.retex.renderer.android;

import com.himamis.retex.renderer.share.cyrillic.CyrillicRegistration;
import com.himamis.retex.renderer.share.greek.GreekRegistration;

public class BaseObjectHelper {
	public static String getPath(Object base, String name) {
		String ret = null;
		if (base == CyrillicRegistration.class) {
			ret = "cyrillic/" + name;
		} else if (base == GreekRegistration.class) {
			ret = "greek/" + name;
		} else {
			ret = name;
		}
		return ret;
	}
}
