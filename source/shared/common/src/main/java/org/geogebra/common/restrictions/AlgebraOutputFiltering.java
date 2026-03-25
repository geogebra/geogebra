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

package org.geogebra.common.restrictions;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.main.AppConfig;

public interface AlgebraOutputFiltering {

	/**
	 * Create the App's base/default algebra output filter, as defined by its {@link AppConfig}.
	 * @return the base algebra output filter.
	 */
	@Nonnull AlgebraOutputFilter createBaseAlgebraOutputFilter();

	/**
	 * Get the algebra output filter.
	 * @return the current algebra output filter.
	 * @apiNote DO NOT CACHE THE RETURN VALUE, the filter may change at runtime.
	 */
	@Nonnull AlgebraOutputFilter getAlgebraOutputFilter();

	/**
	 * Set the algebra output filter.
	 * @param filter The new algebra output filter.
	 */
	void setAlgebraOutputFilter(@Nonnull AlgebraOutputFilter filter);
}
