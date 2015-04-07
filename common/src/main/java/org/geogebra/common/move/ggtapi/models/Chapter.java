package org.geogebra.common.move.ggtapi.models;

public class Chapter {
	private int[] materials;
	private String title;

	public Chapter(String title, int[] mats) {
		this.title = title;
		this.materials = mats;
	}

	public int[] getMaterials() {
		return materials;
	}

	public String getTitle() {
		return title;
	}

}
