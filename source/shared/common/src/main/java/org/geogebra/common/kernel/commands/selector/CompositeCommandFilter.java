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

package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Makes a composition of CommandFilters. If any of the instances
 * filters a command, the composition will filter it as well.
 */
final class CompositeCommandFilter implements CommandFilter {

	private List<CommandFilter> filters;

	/**
	 * Create a CompositeCommandFilter.
	 *
	 * @param filters filters to combine
	 */
	CompositeCommandFilter(CommandFilter... filters) {
		this.filters = Arrays.asList(filters);
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		for (CommandFilter filter: filters) {
			if (!filter.isCommandAllowed(command)) {
				return false;
			}
		}
		return true;
	}
}
