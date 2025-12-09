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

package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material;

public abstract class MaterialsManager implements MaterialsManagerI {

	/** prefix for autosave items in storage */
	public static final String AUTO_SAVE_KEY = "autosave";
	/** prefix for files in storage */
	public static final String FILE_PREFIX = "file_";
	/** characters not allowed in filename */
	public static final String reservedCharacters = "*/:<>?\\|+,.;=[]";

	/**
	 * @param matID
	 *            local ID of material
	 * @param title
	 *            of material
	 * @return creates a key (String) for the stockStore
	 */
	public static String createKeyString(int matID, String title) {
		StringBuilder sb = new StringBuilder(title.length() + 12);
		sb.append(FILE_PREFIX);
		sb.append(matID);
		sb.append('_');
		appendTitleWithoutReservedCharacters(title, sb);
		return sb.toString();
	}

	/**
	 * Remove all reserved characters from a ggb file title
	 * 
	 * @param title
	 *            title for ggb file
	 * @return title without reserved characters
	 */
	public static String getTitleWithoutReservedCharacters(String title) {
		StringBuilder sb = new StringBuilder(title.length());
		appendTitleWithoutReservedCharacters(title, sb);
		return sb.toString();
	}

	private static void appendTitleWithoutReservedCharacters(String title,
			StringBuilder sb) {
		for (int i = 0; i < title.length(); i++) {
			if (reservedCharacters.indexOf(title.charAt(i)) == -1) {
				sb.append(title.charAt(i));
			}
		}
	}

	/**
	 * @param mat
	 *            material
	 * @return storage key based on id and title
	 */
	public static String getFileKey(Material mat) {
		return createKeyString(mat.getLocalID(), mat.getTitle());
	}

	/**
	 * returns the ID from the given key. (key is of form "file_ID_fileName")
	 * 
	 * @param key
	 *            String
	 * @return int ID
	 */
	public static int getIDFromKey(String key) {
		return Integer.parseInt(key.substring(FILE_PREFIX.length(),
				key.indexOf("_", FILE_PREFIX.length())));
	}

	/**
	 * key is of form "file_ID_title"
	 * 
	 * @param key
	 *            file key
	 * @return the title
	 */
	public static String getTitleFromKey(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	/**
	 * Update local copy
	 * 
	 * @param title
	 *            new title
	 * @param modified
	 *            timestamp
	 * @param material
	 *            material
	 */
	protected abstract void updateFile(String title, long modified,
			Material material);

	protected abstract void showTooltip(Material mat);

	protected abstract App getApp();

	protected abstract void refreshMaterial(Material newMat);

	protected abstract void setTubeID(String localKey, Material newMat);

	@Override
	public boolean saveCurrentLocalIfPossible(App app, Runnable callback) {
		return false;
	}

	@Override
	public boolean isOnlineSavingPreferred() {
		return true;
	}
}
