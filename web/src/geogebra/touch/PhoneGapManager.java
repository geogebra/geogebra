package geogebra.touch;

import com.google.gwt.core.client.GWT;
import com.googlecode.gwtphonegap.client.PhoneGap;

public class PhoneGapManager {
	
	private static PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);
	
	public static PhoneGap getPhoneGap() {
		return phoneGap;
	}

	public static void initializePhoneGap() {
	    phoneGap.initializePhoneGap();
    }
}
