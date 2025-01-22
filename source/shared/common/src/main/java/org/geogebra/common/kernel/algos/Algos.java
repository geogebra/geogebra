package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

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

	@Override
	public String getCommand() {
		return command;
	}

	/**
	 * @param geo
	 *            construction element
	 * @param cmdOrExpression
	 *            command, macro or expression
	 * @return whether geo is using given command
	 */
	public static boolean isUsedFor(GetCommand cmdOrExpression,
			GeoElementND geo) {
		return geo.getParentAlgorithm() != null
				&& cmdOrExpression.equals(geo.getParentAlgorithm().getClassName());
	}

}
