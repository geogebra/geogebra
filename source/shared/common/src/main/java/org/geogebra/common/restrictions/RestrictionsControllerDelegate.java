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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;

public interface RestrictionsControllerDelegate {

	/**
	 * @return The current sub-app, or null in case the app switcher is currently shown.
	 */
	@CheckForNull SuiteSubApp getCurrentSubApp();

	/**
	 * Activate the given sub-app.
	 *
	 * @param subApp The sub-app to switch to.
	 *
	 * @apiNote This method is expected to call
	 * {@link RestrictionsController#setActiveContext(Restrictions.ContextDependencies)}
	 * after switching to the new subapp.
	 */
	void switchSubApp(@Nonnull SuiteSubApp subApp);
}
