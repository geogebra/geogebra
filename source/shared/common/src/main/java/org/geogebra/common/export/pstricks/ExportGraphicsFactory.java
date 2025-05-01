package org.geogebra.common.export.pstricks;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;

/**
 * Factory for export graphics.
 */
public interface ExportGraphicsFactory {

	/**
	 * @param ef function
	 * @param inequality inequality
	 * @param export export
	 * @return export graphics
	 */
	GGraphics2D createGraphics(FunctionalNVar ef, Inequality inequality, GeoGebraExport export);
}
