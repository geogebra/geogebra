package org.geogebra.web.full.gui.pagecontrolpanel;

import com.google.gwt.storage.client.Storage;

public final class SlideCopyUtil {
	public static final String KEY = "copyslide";
	private static Storage storage = Storage.getLocalStorageIfSupported();

	private SlideCopyUtil() {
		// utility class
	}

	protected static void clearContent() {
		if (storage != null) {
			storage.removeItem(SlideCopyUtil.KEY);
		}
	}

	protected static String getContent() {
		if (storage != null) {
			return storage.getItem("copyslide");
		}
		return null;
	}

	protected static void setContent(String json) {
		if (storage != null) {
			storage.setItem("copyslide", json);
		}
	}
}
