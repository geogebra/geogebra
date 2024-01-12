package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.export.pstricks.ExportGraphicsFactory;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.util.debug.Log;

public class ExportGraphicsFactoryW implements ExportGraphicsFactory {

	@Override
	public GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality, GeoGebraExport export) {

		try {
			return new ExportGraphicsW(ef, inequality, export);
		} catch (RuntimeException e) {
			Log.debug(e);
			return null;
		}
	}

}
