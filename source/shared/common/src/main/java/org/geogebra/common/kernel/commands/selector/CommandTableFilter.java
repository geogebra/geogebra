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
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Filters commands by their table name. Commands from
 * {@link org.geogebra.common.kernel.commands.CommandsConstants#TABLE_ENGLISH}
 * are first converted to internal commands.
 */
public final class CommandTableFilter implements CommandFilter {

	private Set<Integer> filteredTables;

	/**
	 * Creates a Command Table filter instance.
	 * @param filteredTables the tables that have to be filtered
	 */
	public CommandTableFilter(Integer... filteredTables) {
		this.filteredTables = new HashSet<>(Arrays.asList(filteredTables));
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		Commands internalCommand = Commands.englishToInternal(command);
		return !filteredTables.contains(internalCommand.getTable());
	}
}
