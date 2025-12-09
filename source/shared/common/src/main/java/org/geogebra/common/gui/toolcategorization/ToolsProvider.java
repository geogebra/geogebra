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

package org.geogebra.common.gui.toolcategorization;

import javax.annotation.Nonnull;

/**
 * An abstraction for retrieving and filtering available tools.
 */
public interface ToolsProvider {

	/**
	 * @return The currently available tools. Note that the set of available tools can change,
	 * e.g. during exams, or when a file with a custom tool collection is loaded.
	 */
	@Nonnull ToolCollection getAvailableTools();

	/**
	 * Add a (temporary or permanent) tools filter.
	 * @param filter A tools filter.
	 */
	void addToolsFilter(@Nonnull ToolCollectionFilter filter);

	/**
	 * Remove a previously added tools filter.
	 * @param filter A tools filter.
	 */
	void removeToolsFilter(@Nonnull ToolCollectionFilter filter);
}
