package org.geogebra.desktop.export.pstricks;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.export.pstricks.ExportGraphicsFactory;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;

public class ExportGraphicsFactoryD implements ExportGraphicsFactory {
	@Override
	public GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality, GeoGebraExport export) {

		return new ExportGraphicsD(ef, inequality, export);
	}
}
