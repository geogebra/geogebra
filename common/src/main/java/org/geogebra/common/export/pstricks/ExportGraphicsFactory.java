package org.geogebra.common.export.pstricks;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;

public interface ExportGraphicsFactory {
	GGraphics2D createGraphics(FunctionalNVar ef, Inequality inequality, GeoGebraExport export);
}
