package org.geogebra.common.util;

import java.util.HashMap;

import org.geogebra.common.kernel.Kernel;

/**
 * @author Christoph
 * 
 */
public abstract class Assignment {

	/**
	 * The Result of the Assignment
	 */
	public enum Result {
		/**
		 * The assignment is CORRECT
		 */
		CORRECT,
		/**
		 * The assignment is WRONG and we can't tell why
		 */
		WRONG,
		/**
		 * There are not enough input geos, so we cannot check
		 */
		NOT_ENOUGH_INPUTS,
		/**
		 * We have enough input geos, but one or more are of the wrong type
		 */
		WRONG_INPUT_TYPES,
		/**
		 * There is no output geo matching our macro
		 */
		WRONG_OUTPUT_TYPE,
		/**
		 * The assignment was correct in the first place but wrong after
		 * randomization
		 */
		WRONG_AFTER_RANDOMIZE,
		/**
		 * The assignment could not be checked
		 */
		UNKNOWN,
	}

	/**
	 * The fractions for the Results. Each Result may have any fraction between
	 * -100 and 100 (i.e. -1 and 1)
	 */
	protected HashMap<Result, Double> fractionForResult;
	/**
	 * The hints for the Results. There may or may not be a hint set for a
	 * particular result.
	 */
	protected HashMap<Result, String> hintForResult;
	/**
	 * The current state of the Assignment (should only get updated when
	 * checkAssignment is called)
	 */
	protected Result res;
	/**
	 * Kernel
	 */
	protected Kernel kernel;

	/**
	 * 
	 * 
	 * @param kernel
	 *            Kernel
	 */
	public Assignment(Kernel kernel) {
		fractionForResult = new HashMap<>();
		hintForResult = new HashMap<>();
		res = Result.UNKNOWN;
		this.kernel = kernel;
	}

	/**
	 * Exhaustive Testing of the Assignment
	 * 
	 * @return {@link Result} of the check
	 */
	public abstract Result checkAssignment();

	/**
	 * Get the fraction for the current state of the assignment. Don't forget to
	 * call checkAssignment() or checkExercise() prior to getFraction() if you
	 * want to update the Result.
	 * 
	 * @return the fraction for the current state of the assignment <br />
	 *         if the user specified a fraction it will be returned otherwise 1
	 *         for Result.CORRECT 0 else
	 */
	public double getFraction() {
		double fraction = 0;
		if (fractionForResult.containsKey(res)) {
			fraction = fractionForResult.get(res);
		} else if (res == Result.CORRECT) {
			fraction = 1.0f;
		}
		return fraction;
	}

	/**
	 * Gives the hint for the actual state for this {@link GeoAssignment}
	 * 
	 * @return the hint for current {@link Result}
	 */
	public String getHint() {
		return hintForResult.get(res);
	}

	/**
	 * @return the actual state for this {@link GeoAssignment} as {@link Result}
	 */
	public Result getResult() {
		return res;
	}

	/**
	 * @return true if user specified hints for any result in the assignment
	 */
	public boolean hasHint() {
		return !hintForResult.isEmpty();
	}

	/**
	 * @return true if user specified fractions for any result result in the
	 *         assignment
	 */
	public boolean hasFraction() {
		return !fractionForResult.isEmpty();
	}

	/**
	 * @param result
	 *            the result for which the fraction should be set
	 * @param f
	 *            the fraction in the interval [-1,1] which should be used for
	 *            the result (will do nothing if fraction is not in [-1,1])
	 */
	public void setFractionForResult(Result result, double f) {
		if (-1 <= f && f <= 1) {
			fractionForResult.put(result, f);
		}
	}

	/**
	 * @param result
	 *            the result for which the fraction should be returned
	 * @return the fraction corresponding to the result<br />
	 *         if the user specified a fraction it will be returned otherwise 1
	 *         for Result.CORRECT 0 else
	 */
	public double getFractionForResult(Result result) {
		double frac = 0;
		if (fractionForResult.containsKey(result)) {
			frac = fractionForResult.get(result);
		} else if (result == Result.CORRECT) {
			frac = 1.0f;
		}
		return frac;
	}

	/**
	 * Sets the Hint for a particular Result.
	 * 
	 * @param res
	 *            the {@link Result}
	 * @param hint
	 *            the hint which should be shown to the student in case of the
	 *            {@link Result} res
	 */
	public void setHintForResult(Result res, String hint) {
		this.hintForResult.put(res, hint);
	}

	/**
	 * @param result
	 *            the Result for which the hint should be returned
	 * @return hint corresponding to result
	 */
	public String getHintForResult(Result result) {
		String hint = "";
		if (hintForResult.containsKey(result)) {
			hint = hintForResult.get(result);
		}
		return hint;
	}

	/**
	 * Not all {@link Result}s might be suitable for a specific type of
	 * Assignment
	 * 
	 * @return the the Results which are meaningful for the type of Assignment
	 */
	public abstract Result[] possibleResults();

	/**
	 * @return XML describing the Exercise. Will be empty if no changes to the
	 *         Exercise were made (i.e. if isStandardExercise).<br />
	 *         Only Elements and Properties which are set or not standard will
	 *         be included.
	 * 
	 *         <pre>
	 * {@code
	 *  <assignment toolName="Tool2">
	 *    <result name="CORRECT" hint="Great, that&apos;s correct!" />
	 *    <result name="WRONG" hint="Try again!" />
	 *    <result name="NOT_ENOUGH_INPUTS" hint="You should at least have
	 *            &#123;inputs&#125; in your construction!" />
	 *    <result name="WRONG_INPUT_TYPES" hint="We were not able to find
	 *            &#123;inputs&#125;, although it seems you have drawn a triangle!" />
	 *    <result name="WRONG_OUTPUT_TYPE" hint=
	 *        "We couldn&apos;t find a triangle in the construction!" />
	 *    <result name="WRONG_AFTER_RANDOMIZE" hint="Should never happen
	 *        in this construction! Contact your teacher!" fraction="0.5" />
	 *    <result name="UNKNOWN" hint=
	 *        "Something went wrong - ask your teacher!" />
	 *  </assignment>
	 * }
	 *         </pre>
	 */
	public abstract String getAssignmentXML();

	/**
	 * @param sb
	 *            the StringBuilder to which to append the XML common to all
	 *            Assignments
	 * 
	 * @return XML including the mapping of possible Results to hints and
	 *         fractions
	 */
	protected StringBuilder getAssignmentXML(StringBuilder sb) {

		if (hasHint() || hasFraction()) {
			for (Result res1 : Result.values()) {
				String hint = hintForResult.get(res1);
				Double fraction = fractionForResult.get(res1);
				if (hint != null && !hint.isEmpty() || fraction != null) {
					sb.append("\t\t<result name=\"");
					StringUtil.encodeXML(sb, res1.toString());
					sb.append("\" ");
					if (hint != null && !hint.isEmpty()) {
						sb.append("hint=\"");
						StringUtil.encodeXML(sb, hint);
						sb.append("\" ");
					}
					if (fraction != null) {
						sb.append("fraction=\"");
						sb.append(fraction.floatValue());
						sb.append("\" ");
					}
					sb.append("/>\n");
				}
			}
		}
		sb.append("\t</assignment>\n");
		return sb;
	}

	/**
	 * @return Filename or String indicating which icon should be used by GUI
	 *         for this Assignment
	 */
	public abstract String getIconFileName();

	/**
	 * @return A String describing the Assignment (eg. Boolean d for a
	 *         BoolAssignment or the ToolName for a GeoAssignment)
	 */
	public abstract String getDisplayName();

	/**
	 * If construction changes the assignment may become invalid
	 * 
	 * @return true if the assignment is valid
	 */
	public abstract boolean isValid();
}