package com.himamis.retex.renderer.desktop.font;

import com.himamis.retex.renderer.desktop.geom.GeneralPathD;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.GlyphVector;
import com.himamis.retex.renderer.share.platform.geom.Shape;

public class GlyphVectorD extends GlyphVector {

	private java.awt.font.GlyphVector impl;

	public GlyphVectorD(java.awt.font.GlyphVector gv) {
		impl = gv;
	}

	@Override
	public Shape getGlyphOutline(int i) {
		java.awt.Shape ret = impl.getGlyphOutline(i);
		if (ret instanceof java.awt.geom.GeneralPath) {
			return new GeneralPathD((java.awt.geom.GeneralPath) ret);
		}

		FactoryProvider.getInstance()
				.debug("unhandled Shape " + ret.getClass());
		return null;
	}

}
