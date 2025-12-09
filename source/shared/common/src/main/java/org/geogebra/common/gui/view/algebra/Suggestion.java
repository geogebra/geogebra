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

package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.cas.AlgoDependentSymbolic;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

abstract public class Suggestion {

	/**
	 * @param geo
	 *            geo
	 * @param sug
	 *            suggestion
	 * @param algosMissing
	 *            this array is updated when algo found; may be null if not
	 *            needed
	 * @return whether all algos related to a suggestion were found
	 */
	static boolean checkDependentAlgo(GeoElementND geo, Suggestion sug,
			boolean[] algosMissing) {
		for (AlgoElement algo : geo.getAlgorithmList()) {
			if (algo != null
					&& algo.getOutputLength() > 0
					&& algo.getOutput(0).isLabelSet()
					&& sug.allAlgosExist(extractClassName(algo), algo.getInput(),
							algosMissing)) {
				return true;
			}
			if (algo instanceof AlgoDependentList
					&& checkDependentAlgo(algo.getOutput(0), sug, algosMissing)) {
				return true;
			}
		}
		return false;
	}

	static private GetCommand extractClassName(AlgoElement algo) {
		if (algo instanceof AlgoDependentSymbolic) {
			ExpressionValue definition = algo.getOutput(0).getDefinition().unwrap();
			if (definition instanceof Command) {
				try {
					return Commands.valueOf(((Command) definition).getName());
				} catch (Exception e) {
					return null;
				}
			}
		}
		return algo.getClassName();
	}

	/**
	 * @param className
	 *            algo name
	 * @param input
	 *            algo input
	 * @param algosMissing
	 *            previously found algos
	 * @return whether all algos already exist
	 */
	protected abstract boolean allAlgosExist(GetCommand className,
			GeoElement[] input, boolean[] algosMissing);

	/**
	 * This method runs the necessary commands to execute this suggestion.
	 * Implement the main logic of suggestions here.
	 * Calling store undo info is not necessary inside this method.
	 *
	 * @param geo geo element to perform the suggestion on
	 */
	abstract protected void runCommands(GeoElementND geo);

	/**
	 * Execute this suggestion.
	 *
	 * @param geo the geo element to perform the suggestion on
	 */
	public void execute(GeoElementND geo) {
		runCommands(geo);
		geo.getKernel().storeUndoInfo();
	}
}
