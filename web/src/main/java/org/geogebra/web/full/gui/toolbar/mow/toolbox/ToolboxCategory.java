package org.geogebra.web.full.gui.toolbar.mow.toolbox;

public enum ToolboxCategory {
	SELECT("select"),
	PEN("pen"),
	SHAPES("shapes"),
	TEXT("text"),
	UPLOAD("upload"),
	LINK("link"),
	MORE("more"),
	SPOTLIGHT("spotlight"),
	RULER("ruler");

	private final String category;

	ToolboxCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return category;
	}
}

