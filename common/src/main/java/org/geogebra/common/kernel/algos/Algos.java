package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Identifies algorithm used for creation of a geo
 */
public enum Algos implements GetCommand {
	/** For dependent elements defined using math. operations */
	Expression,
	/** For elements created by macro */
	AlgoMacro;
	private String command;

	private Algos() {
		this.command = "Expression";
	}

	private Algos(Commands command) {
		this.command = command.name();
	}

	@Override
	public String getCommand() {
		return command;
	}

}
