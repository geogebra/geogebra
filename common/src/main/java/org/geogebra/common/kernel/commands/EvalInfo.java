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
	private boolean useCAS = true;

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
	 * 
	 * @param labelOutput
	 *            whether label should be labeled
	 * @param redefineIndependent
	 *            whether independent geos may be redefined by processing the
	 *            expression
	 */
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

	/**
	 * @return whether independent geos may be redefined by processing the
	 *         expression
	 */
	public boolean mayRedefineIndependent() {
		return redefineIndependent;
	}

	/**
	 * @param scripts
	 *            whether to allow execution of scripting commands
	 * @return copy of this with adjusted scripting
	 */
	public EvalInfo withScripting(boolean scripts) {
		EvalInfo ret = copy();
		ret.scripting = scripts;
		return ret;

	}

	/**
	 * 
	 * @param cas
	 *            whether to allow using CAS for computations
	 * @return copy of this with adjusted CAS flag
	 */
	public EvalInfo withCAS(boolean cas) {
		EvalInfo ret = copy();
		ret.useCAS = cas;
		return ret;
	}

	/**
	 * @return whether scripting commands may be executed
	 */
	public boolean isScripting() {
		return scripting;
	}

	private EvalInfo copy() {
		EvalInfo ret = new EvalInfo(this.labelOutput, this.redefineIndependent);
		ret.scripting = this.scripting;
		ret.casMap = this.casMap;
		ret.simplifyIntegers = this.simplifyIntegers;
		ret.useCAS = this.useCAS;
		return ret;
	}

	/**
	 * @return whether subnodes such as 4/2 may be simplified
	 */
	public boolean isSimplifyingIntegers() {
		return simplifyIntegers;
	}

	/**
	 * @param simplify
	 *            whether subnodes such as 4/2 may be simplified
	 * @return copy of this with adjusted flag
	 */
	public EvalInfo withSimplifying(boolean simplify) {
		EvalInfo ret = copy();
		ret.simplifyIntegers = simplify;
		return ret;
	}

	/**
	 * 
	 * @param labeling
	 *            whether labels for output are allowed
	 * @return copy of this with adjusted labeling flag
	 */
	public EvalInfo withLabels(boolean labeling) {
		if (labeling == labelOutput) {
			return this;
		}
		EvalInfo ret = copy();
		ret.labelOutput = labeling;
		return ret;
	}

	/**
	 * @return whether CAS may be used
	 */
	public boolean isUsingCAS() {
		return useCAS;
	}

}
