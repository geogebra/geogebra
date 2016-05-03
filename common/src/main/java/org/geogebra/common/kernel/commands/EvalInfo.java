package org.geogebra.common.kernel.commands;

import java.util.TreeMap;

/**
 * Flags and auxiliary information used for evaluation of an expression
 */
public class EvalInfo {

	private boolean labelOutput;
	private TreeMap<String, String> casMap;
	private boolean redefineIndependent = true;
	private boolean scripting = true;
	private boolean simplifyIntegers = true;

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

	public EvalInfo(boolean labelOutput, boolean redefineIndependent) {
		this.labelOutput = labelOutput;
		this.redefineIndependent = redefineIndependent;
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

	public boolean mayRedefineIndependent() {
		return redefineIndependent;
	}

	public EvalInfo withScripting(boolean scripting) {
		EvalInfo ret = copy();
		ret.scripting = scripting;
		return ret;

	}

	public boolean isScripting() {
		return scripting;
	}

	private EvalInfo copy() {
		EvalInfo ret = new EvalInfo(this.labelOutput, this.redefineIndependent);
		ret.scripting = this.scripting;
		ret.casMap = this.casMap;
		ret.simplifyIntegers = this.simplifyIntegers;
		return ret;
	}

	public boolean isSimplifyingIntegers() {
		return simplifyIntegers;
	}

	public EvalInfo withSimplifying(boolean simplify) {
		EvalInfo ret = copy();
		ret.simplifyIntegers = simplify;
		return ret;
	}

}
