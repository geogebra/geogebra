package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

abstract public class Suggestion {

	public Suggestion() {
	}

	
	
	static boolean hasDependentAlgo(GeoElementND geo, Commands test1,
			Commands test2) {
		AlgorithmSet set = geo.getAlgoUpdateSet();
		for (AlgoElement algo : set) {
			if (algo != null && (algo.getClassName() == test1
					|| algo.getClassName() == test2)) {
				return true;
			}
		}
		return false;
	}

	abstract public String getCommand(Localization loc);
	
	abstract public void execute(GeoElementND geo);
	
}
