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

package org.geogebra.common.gui.util;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.main.App;

/**
 * Filters modes that are invalid based on app.
 *
 * Deprecated, use {@link App#getAvailableTools()} instead.
 */
@Deprecated
public class InvalidToolFilter implements ToolCollectionFilter {

	private App app;

	/**
	 * Constructs an InvalidToolFilter.
	 *
	 * @param app app
	 */
	public InvalidToolFilter(App app) {
		this.app = app;
	}

	@Override
	public boolean isIncluded(int tool) {
		return app.isModeValid(tool);
	}
}
