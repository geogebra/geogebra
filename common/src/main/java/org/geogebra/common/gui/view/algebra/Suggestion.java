package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

abstract public class Suggestion {

	private static final int MODE_NONE = -1;

	public Suggestion() {
	}

	static boolean hasDependentAlgo(GeoElementND geo, Suggestion sug) {
		for (AlgoElement algo : geo.getAlgorithmList()) {
			if (algo != null
					&& sug.sameAlgoType(algo.getClassName(), algo.getInput())) {
				return true;
			}
			if (algo instanceof AlgoDependentList
					&& hasDependentAlgo(algo.getOutput(0), sug)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean sameAlgoType(GetCommand className,
			GeoElement[] input);

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
