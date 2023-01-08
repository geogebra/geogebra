package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;

public interface VectorShape {
	DrawVectorModel properties();
	GLine2D body();
	GShape head();

	GLine2D clippedBody();
}
