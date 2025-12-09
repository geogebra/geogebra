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

package org.geogebra.common.main.undo;

import java.util.Collection;

/**
 * Data for saving undo history list
 */
public class UndoHistory {
	private final Collection<UndoCommand> commands;
	private final int iteratorIndex;

	/**
	 * Constructor
	 *  @param commands undo commands
	 * @param iteratorIndex current iterator.
	 */
	public UndoHistory(Collection<UndoCommand> commands,
			int iteratorIndex) {
		this.commands = commands;
		this.iteratorIndex = iteratorIndex;
	}

	/**
	 *
	 * @return the stored commands
	 */
	public Collection<UndoCommand> commands() {
		return commands;
	}

	/**
	 *
	 * @return the stored iterator
	 */
	public int iteratorIndex() {
		return iteratorIndex;
	}
}
