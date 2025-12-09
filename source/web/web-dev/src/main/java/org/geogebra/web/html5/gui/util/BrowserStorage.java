/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.debug.Log;

import elemental2.dom.DomGlobal;
import elemental2.webstorage.Storage;
import elemental2.webstorage.WebStorageWindow;

public enum BrowserStorage {
	LOCAL, SESSION;

	public static final String COPY_SLIDE = "copyslide";
	public static final String COPY_SLIDE_OBJECTS = "copyslideObjects";
	public static final String KEYBOARD_WANTED = "keyboardwanted";
	public static final String LAST_USED_SUB_APP = "lastUsedSubApp";

	private Storage storage;

	private void init() {
		WebStorageWindow storageWindow = WebStorageWindow.of(DomGlobal.window);
		try {
			storage = this == LOCAL ? storageWindow.localStorage : storageWindow.sessionStorage;
		} catch (Exception e) {
			// running in test environment or in a buggy browser
		}
	}

	/**
	 * @param key key to be removed
	 */
	public void removeItem(String key) {
		init();
		if (storage != null) {
			storage.removeItem(key);
		}
	}

	/**
	 * @param key storage key
	 * @return stored value
	 */
	public String getItem(String key) {
		init();
		if (storage != null) {
			return storage.getItem(key);
		}
		return null;
	}

	/**
	 * @param key storage key
	 * @param json stored value
	 */
	public void setItem(String key, String json) {
		init();
		try {
			if (storage != null) {
				storage.setItem(key, json);
			}
		} catch (Exception e) {
			Log.warn("Quota exceeded");
		}
	}

	/**
	 * @return number of keys
	 */
	public int getLength() {
		return storage == null ? 0 : storage.getLength();
	}

	/**
	 * @param index number from 0 to {@link #getLength()} - 1
	 * @return key with given index
	 */
	public String key(int index) {
		return storage == null ? "" : storage.key(index);
	}
}
