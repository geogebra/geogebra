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

package org.geogebra.desktop.geogebra3D.gui;

import org.geogebra.desktop.util.ImageResourceD;

public enum GuiResources3D implements ImageResourceD {
	PLANE("/gui/images/64px/plane.gif"),

	PROJECTION_ORTHOGRAPHIC(
			"/gui/images/64px/stylingbar_graphics3D_view_orthographic.png"),

	PROJECTION_PERSPECTIVE(
			"/gui/images/64px/stylingbar_graphics3D_view_perspective.png"),

	PROJECTION_GLASSES(
			"/gui/images/64px/stylingbar_graphics3D_view_glasses.png"),

	PROJECTION_OBLIQUE(
			"/gui/images/64px/stylingbar_graphics3D_view_oblique.png"),

	STYLINGBAR_GRAPHICS3D_PLANE(
			"/gui/images/64px/stylingbar_graphics3D_plane.png"),

	STYLINGBAR_GRAPHICS3D_ROTATEVIEW_PLAY(
			"/gui/images/64px/stylingbar_graphics3D_rotateview_play.png"),

	STYLINGBAR_GRAPHICS3D_CLIPPING_MEDIUM(
			"/gui/images/64px/stylingbar_graphics3D_clipping_medium.png"),

	STYLINGBAR_GRAPHICS3D_VIEW_XY(
			"/gui/images/64px/stylingbar_graphics3D_view_xy.png"),

	STYLINGBAR_GRAPHICS3D_VIEW_YZ(
			"/gui/images/64px/stylingbar_graphics3D_view_yz.png"),

	STYLINGBAR_GRAPHICS3D_VIEW_XZ(
			"/gui/images/64px/stylingbar_graphics3D_view_xz.png"),

	STYLINGBAR_GRAPHICS3D_STANDARDVIEW_ROTATE(
			"/gui/images/64px/stylingbar_graphics3D_standardview_rotate.png");
	private String fn;

	GuiResources3D(String fn) {
		this.fn = fn;
	}

	@Override
	public String getFilename() {
		return fn;
	}

}
