package org.geogebra.web.touch;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.GWT;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableEvent;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableHandler;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * PhoneGap connector
 */
public class PhoneGapManager {

	private static PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);

	/**
	 * @return singleton PhoneGap instance
	 */
	public static PhoneGap getPhoneGap() {
		return phoneGap;
	}

	/**
	 * @param handler
	 *            back button handler
	 */
	public static void initializePhoneGap(final BackButtonPressedHandler handler) {
		if (handler != null) {
			phoneGap.addHandler(new PhoneGapAvailableHandler() {

				@Override
				public void onPhoneGapAvailable(PhoneGapAvailableEvent event) {
					addBackHandler(handler);
				}
			});
		}
		phoneGap.initializePhoneGap();
	}

	/**
	 * @param handler
	 *            back button handler
	 */
	protected static void addBackHandler(BackButtonPressedHandler handler) {
		try {
			phoneGap.getEvent().getBackButton()
					.addBackButtonPressedHandler(handler);
		} catch (Throwable t) {
			Log.error("No back button event.");
		}

	}
}
