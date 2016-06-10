package org.geogebra.desktop.util;

public enum GuiResourcesD implements ImageResourceD {
	EMPTY("/gui/images/empty.gif"),

	GEOGEBRA64("/gui/images/geogebra64.png"),

	NAV_PLAY("/main/nav_play.png"),

	NAV_PLAY_CIRCLE(
			"/org/geogebra/common/icons_play/p24/nav_play_circle.png"),

	NAV_PLAY_HOVER(
			"/org/geogebra/common/icons_play/p24/nav_play_circle_hover.png"),

	VIEW_REFRESH("/gui/images/menu-icons/40px/view-refresh.png"),

	NAV_PAUSE_CIRCLE(
			"/org/geogebra/common/icons_play/p24/nav_pause_circle.png"),

	NAV_PAUSE_CIRCLE_HOVER(
			"/org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png"),

	NAV_PAUSE("/main/nav_pause.png");
	private String filename;

	GuiResourcesD(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
