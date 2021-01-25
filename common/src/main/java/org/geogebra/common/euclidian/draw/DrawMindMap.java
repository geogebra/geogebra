package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoInline;

public class DrawMindMap extends DrawInlineText {

	public DrawMindMap(EuclidianView view, GeoInline text) {
		super(view, text);
	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw the connections
		super.draw(g2);
	}
}
