package org.geogebra.common.move.ggtapi.models;

public enum ResourceAction {
	EDIT("Edit"),
	VIEW("ViewMaterial"),
	INSERT_ACTIVITY("insert_worksheet"), // old key, translated as Insert Activity
	COPY("makeACopy"),
	SHARE("Share"),
	DELETE("Delete"),
	RENAME("Rename");

	private final String translationKey;

	ResourceAction(String key) {
		this.translationKey = key;
	}
	
	public String getTranslationKey() {
		return this.translationKey;
	}
}
