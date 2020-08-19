package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.scientific.LabelController;

public class SuggestionStatistics extends Suggestion {

	private static Suggestion INSTANCE = new SuggestionStatistics();

	@Override
	public String getCommand(Localization loc) {
		return loc.getMenu("Statistics");
	}

	@Override
	public void runCommands(GeoElementND geo) {
		boolean[] neededAlgos = getNeededAlgos(geo);
		boolean isSymbolicMode = geo.getKernel().getSymbolicMode() == SymbolicMode.SYMBOLIC_AV;
		String cmd;

		new LabelController().ensureHasLabel(geo);
		checkDependentAlgo(geo, INSTANCE, neededAlgos);
		AlgebraProcessor algebraProcessor = geo.getKernel().getAlgebraProcessor();

		if (neededAlgos[0]) {
			cmd = "Min[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
		if (neededAlgos[1]) {
			cmd = "Q1[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
		if (neededAlgos[2]) {
			cmd = "Median[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
		if (neededAlgos[3]) {
			cmd = "Q3[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
		if (neededAlgos[4]) {
			cmd = "Max[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
	}

	protected void processCommand(AlgebraProcessor algebraProcessor, String cmd,
			boolean isSymbolicMode) {
		if (isSymbolicMode) {
			algebraProcessor.processAlgebraCommand(
					cmd, false, new LabelHiderCallback());
		} else {
			algebraProcessor.processAlgebraCommand(cmd, false);
		}
	}

	private static boolean[] getNeededAlgos(GeoElementND geo) {
		boolean[] neededAlgos = {true, true, true, true, true};

		if (geo instanceof GeoList && ((GeoList)geo).size() < 2) {
			neededAlgos[1] = false;
			neededAlgos[3] = false;
		}

		return neededAlgos;
	}

	/**
	 * @param geo construction element
	 * @return statistics suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (isListOfNumbers(geo) && !checkDependentAlgo(geo, INSTANCE, getNeededAlgos(geo))) {
			return INSTANCE;
		}
		return null;
	}

	private static boolean isListOfNumbers(GeoElement geoElement) {
		if (geoElement instanceof GeoList && ((GeoList) geoElement).size() > 0) {
			GeoList geoList = (GeoList) geoElement;
			for (GeoElement geoItem : geoList.elementsAsArray()) {
				if (!(geoItem instanceof GeoNumeric)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
			boolean[] algosMissing) {
		if (className == Commands.Min) {
			algosMissing[0] = false;
		}
		if (className == Commands.Q1) {
			algosMissing[1] = false;
		}
		if (className == Commands.Median) {
			algosMissing[2] = false;
		}
		if (className == Commands.Q3) {
			algosMissing[3] = false;
		}
		if (className == Commands.Max) {
			algosMissing[4] = false;
		}

		return !algosMissing[0] && !algosMissing[1] && !algosMissing[2]
				&& !algosMissing[3] && !algosMissing[4];
	}
}