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
