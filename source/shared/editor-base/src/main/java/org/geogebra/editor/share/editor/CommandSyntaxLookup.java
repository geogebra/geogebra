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

package org.geogebra.editor.share.editor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.util.IntegralHelper.IntegralForm;

/** Looks up internal command and syntax information for editor commands. */
public interface CommandSyntaxLookup {
	/**
	 * @param commandName localized or English command name
	 * @return internal command name if available
	 */
	@CheckForNull String getInternalCommand(@Nonnull String commandName);

	/**
	 * @param tag integral command tag
	 * @param syntax localized syntax selected from autocomplete
	 * @return integral form matching the syntax, or null if not available
	 */
	@CheckForNull IntegralForm getIntegralForm(@Nonnull Tag tag, @Nonnull String syntax);
}
