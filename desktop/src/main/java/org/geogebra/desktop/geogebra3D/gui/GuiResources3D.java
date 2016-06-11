package org.geogebra.desktop.geogebra3D.gui;

import org.geogebra.desktop.util.ImageResourceD;

public enum GuiResources3D implements ImageResourceD {
	PLANE("/gui/images/64px/plane.gif"), PROJECTION_ORTOGRAPHIC(
			"/gui/images/64px/stylingbar_graphics3D_view_orthographic.png"), PROJECTION_PERSPECTIVE(
			"/gui/images/64px/stylingbar_graphics3D_view_perspective.png"), PROJECTION_GLASSES(
			"/gui/images/64px/stylingbar_graphics3D_view_glasses.png"), PROJECTION_OBLIQUE(
			"/gui/images/64px/stylingbar_graphics3D_view_oblique.png");
	private String fn;

	GuiResources3D(String fn) {
		this.fn = fn;
	}

	public String getFilename() {
		return fn;
	}

}
