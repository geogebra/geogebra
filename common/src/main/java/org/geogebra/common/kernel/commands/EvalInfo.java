package org.geogebra.common.kernel.commands;

import java.util.TreeMap;

/**
 * Flags and auxiliary information used for evaluation of an expression
 */
public class EvalInfo {

	private boolean labelOutput;
	private TreeMap<String, String> casMap;

	/**
	 * @param labelOut
	 *            whether output should be labeled
	 * @param casMap
	 *            cas evaluation map
	 */
	public EvalInfo(boolean labelOut, TreeMap<String, String> casMap) {
		this.labelOutput = labelOut;
		this.casMap = casMap;
	}

	/**
	 * @param labelOut
	 *            whether to label output
	 */
	public EvalInfo(boolean labelOut) {
		this.labelOutput = labelOut;
	}

	/**
	 * @return whether outputs should be labeled
	 */
	public boolean isLabelOutput() {
		return this.labelOutput;
	}

	/**
	 * @return CAS cache
	 */
	public TreeMap<String, String> getCASMap() {
		return casMap;
	}

}
