package org.geogebra.common.kernel.commands;

import java.util.TreeMap;

public class EvalInfo {

	private boolean labelOutput;
	private TreeMap<String, String> casMap;

	public EvalInfo(boolean labelOut, TreeMap<String, String> casMap) {
		this.labelOutput = labelOut;
		this.casMap = casMap;
	}

	public EvalInfo(boolean b) {
		this.labelOutput = b;
	}

	public boolean isLabelOutput() {
		return this.labelOutput;
	}

	public TreeMap<String, String> getCASMap() {
		return casMap;
	}

}
