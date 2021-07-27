package org.geogebra.common.move.ggtapi.models;

public class Chapter {
	private int[] materials;
	private String title;

	/**
	 * @param title
	 *            title
	 * @param mats
	 *            material IDs
	 */
	public Chapter(String title, int[] mats) {
		this.title = title;
		this.materials = mats;
	}

	/**
	 * @return material IDs
	 */
	public int[] getMaterials() {
		return materials;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

}
