package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

abstract public class Suggestion {

	private static final int MODE_NONE = -1;

	/**
	 * @param geo
	 *            geo
	 * @param sug
	 *            suggestion
	 * @param algosFound
	 *            this array is updated when algo found; may be null if not
	 *            needed
	 * @return whether all algos related to a suggestion were found
	 */
	static boolean checkDependentAlgo(GeoElementND geo, Suggestion sug,
			boolean[] algosFound) {
		for (AlgoElement algo : geo.getAlgorithmList()) {
			if (algo != null
					&& algo.getOutputLength() > 0
					&& algo.getOutput(0).isLabelSet()
					&& sug.allAlgosExist(algo.getClassName(), algo.getInput(),
							algosFound)) {
				return true;
			}
			if (algo instanceof AlgoDependentList
					&& checkDependentAlgo(algo.getOutput(0), sug, algosFound)) {
				return true;
			}
		}
		return false;
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
	 * @param loc
	 *            localization
	 * @return label for suggestion button
	 */
	abstract public String getCommand(Localization loc);

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
	public final void execute(GeoElementND geo) {
		runCommands(geo);
		geo.getKernel().storeUndoInfo();
	}

	/**
	 * @return whether this is the slider suggestion
	 */
	public boolean isAutoSlider() {
		return false;
	}

	/**
	 * 
	 * @return tool mode to use.
	 */
	public int getMode() {
		return MODE_NONE;
	}
	
	/**
	 * 
	 * @return if the suggestion has associated with
	 */
	public boolean hasMode() {
		return getMode() != MODE_NONE;
	}
}
