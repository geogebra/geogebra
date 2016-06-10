package org.geogebra.desktop.util;

public enum GuiResourcesD implements ImageResourceD {
	EMPTY("/gui/images/empty.gif"),

	GEOGEBRA64("/gui/images/geogebra64.png"),

	NAV_PLAY("/geogebra/main/nav_play.png"),

	NAV_PLAY_CIRCLE(
			"/org/geogebra/common/icons_play/p24/nav_play_circle.png"),

	NAV_PLAY_HOVER(
			"/org/geogebra/common/icons_play/p24/nav_play_circle_hover.png"),

	VIEW_REFRESH("/gui/images/menu-icons/40px/view-refresh.png"),

	NAV_PAUSE_CIRCLE(
			"/org/geogebra/common/icons_play/p24/nav_pause_circle.png"),

	NAV_PAUSE_CIRCLE_HOVER(
			"/org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png"),

	NAV_PAUSE("/geogebra/main/nav_pause.png"),

	MODE_SHOWHIDELABEL(
			"/org/geogebra/common/icons_toolbar/p64/mode_showhidelabel.png"), DIALOG_ERROR(
					"/org/geogebra/common/icons/png/web/dialog-error.png"), MENU_HELP(
							"/org/geogebra/common/menu_icons/p20/menu-help.png"),

	CURSOR_GRAB(
			"/gui/images/cursor_grab.gif"), CURSOR_ZOOMOUT(
					"/gui/images/cursor_zoomout.gif"),

	CURSOR_ZOOMIN("/gui/images/cursor_zoomin.gif"), CURSOR_GRABBING(
			"/gui/images/cursor_grabbing"), UNDO(
					"/gui/images/64px/menu-edit-undo.png"), REDO(
							"/gui/images/64px/menu-edit-redo.png"),

	MENU_OPTIONS("/gui/images/64px/menu-options.png"),

	TRIANGLE_DOWN("/gui/images/64px/triangle-down.png"),

	DELETE_SMALL("/gui/images/64px/delete_small.gif"),

	DOCKBAR_TRIANGLE_RIGHT("/gui/images/64px/dockbar-triangle-right.png"),

	DOCKBAR_TRIANGLE_LEFT(
			"/gui/images/64px/dockbar-triangle-left.png"),

	LIST_ADD("/gui/images/64px/list-add.png"),

	COLOR_CHOOSER_CHECK(
			"/gui/images/64px/color_chooser_check.png"),

	TOOL_MODE32("/org/geogebra/common/icons_toolbar/p32/mode_tool.png"),

	TOOL_MODE64(
			"/org/geogebra/common/icons_toolbar/p64/mode_tool.png"),

	GO_DOWN("/geogebra/gui/images/go-down.png"),

	GO_UP("/geogebra/gui/images/go-up.png"),

	GO_PREVIOUS("/geogebra/gui/images/go-previous.png"),

	GO_NEXT("/geogebra/gui/images/go-next.png"),

	NAV_FASTFORWARD(
			"/geogebra/gui/images/nav_fastforward.png"),

	NAV_REWIND("/geogebra/gui/images/nav_rewind.png"),

	NAV_SKIPBACK("/geogebra/gui/images/nav_skipback.png"),

	NAV_SKIPFORWARD("/geogebra/gui/images/nav_skipforward.png"),

	EXIT("/geogebra/gui/images/exit.png"),

	FONT("/gui/images/64px/font.png"),

	MODE_SHOWHIDELABEL_GIF("/gui/images/64px/mode_showhidelabel_16.gif"),

	DOCUMENT_PRINT("/gui/images/64px/document-print.png"),

	MENU_VIEW_GRAPHICS3D(
			"/gui/images/64px/menu_view_graphics3D.png"),

	MENU_VIEW_CAS("/gui/images/64px/menu_view_cas.png"),

	MENU_VIEW_SPREADSHEET("/gui/images/64px/menu_view_spreadsheet.png"),

	MENU_VIEW_GRAPHICS("/gui/images/64px/menu_view_graphics.png"),

	MENU_VIEW_ALGEBRA(
			"/gui/images/64px/menu_view_algebra.png"),

	DOCUMENT_NEW(
					"/gui/images/64px/document-new.png"),

	MENU_VIEW_CONSTRUCTION_PROTOCOL(
							"/gui/images/64px/menu_view_construction_protocol.png"),

	MENU_VIEW_GRAPHICS2(
			"/gui/images/64px/menu_view_graphics2.png"), MENU_VIEW_PROBABILITY(
					"/gui/images/64px/menu_view_probability.png"),

	VIEW_PROPERTIES_22("/gui/images/64px/view-properties22.png"),

	GEOGEBRA("/gui/images/64px/geogebra.png"),

	HELP("/gui/images/64px/help.png"),

	INFO("/gui/images/64px/info.gif"),

	KEYBOARD("/gui/images/64px/keyboard.png"),

	VIEW_PROPERTIES_16(
			"/gui/images/64px/view-properties16.png"),

	MENU_EDIT_COPY(
					"/gui/images/64px/menu-edit-copy.png");
	private String filename;

	GuiResourcesD(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
