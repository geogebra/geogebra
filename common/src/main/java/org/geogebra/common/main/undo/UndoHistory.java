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
