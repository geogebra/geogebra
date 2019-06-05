package com.himamis.retex.renderer.desktop.geom;

import java.awt.geom.Path2D;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;

public class GeneralPathD implements ShapeD {

	private final java.awt.geom.GeneralPath impl;

	public GeneralPathD(java.awt.geom.GeneralPath g) {
		impl = g;
	}

	public GeneralPathD() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		impl = new java.awt.geom.GeneralPath(Path2D.WIND_EVEN_ODD);
	}

	public GeneralPathD(int rule) {
		impl = new java.awt.geom.GeneralPath(rule);
	}

	public static java.awt.geom.GeneralPath getAwtGeneralPath(Shape gp) {
		if (!(gp instanceof GeneralPathD)) {
			if (gp != null) {
				FactoryProvider.debugS("other type");
			}
			return null;
		}
		return ((GeneralPathD) gp).impl;
	}

	@Override
	public Rectangle2D getBounds2DX() {
		return new Rectangle2DD(impl.getBounds2D());
	}

}
