package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

abstract public class Suggestion {
	private String[] labels;

	public Suggestion(String... labels) {
		this.labels = labels;
	}

	public String getLabels(GeoElementND geo) {
		if (labels[labels.length - 1] == null) {
			labels[labels.length - 1] = geo.getLabelSimple();
		}
		return labels.length == 1 ? labels[0]
				: "{" + StringUtil.join(", ", labels) + "}";
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
