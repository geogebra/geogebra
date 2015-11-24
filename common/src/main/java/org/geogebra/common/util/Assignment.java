package org.geogebra.common.util;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.util.GeoAssignment.Result;

public interface Assignment {

	/**
	 * Possible values for fractions (sorted ascending!)
	 */
	public final static float[] FRACTIONS = { -1f, -0.9f, -0.875f, -(5f / 6),
			-0.8f, -0.75f, -0.7f, -(2f / 3), -0.625f, -0.6f, -0.5f, -0.4f,
			-0.375f, -(1f / 3), -0.3f, -0.25f, -0.2f, -(1f / 6), -0.125f,
			-0.1f, 0f, 0.1f, 0.125f, (1f / 6), 0.2f, 0.25f, 0.3f, (1f / 3),
			0.375f, 0.4f, 0.5f, 0.6f, 0.625f, (2f / 3), 0.7f, 0.75f, 0.8f,
			(5f / 6), 0.875f, 0.9f, 1f };

	/**
	 * Exhaustive Testing of the Assignment
	 * 
	 * @param construction
	 *            the construction object of the kernel
	 * 
	 * @return {@link Result} of the check
	 */
	public Result checkAssignment(Construction construction);

	/**
	 * Get the fraction for the current state of the assignment. Don't forget to
	 * call checkAssignment() or checkExercise() prior to getFraction() if you
	 * want to update the Result.
	 * 
	 * @return the fraction for the current state of the assignment <br />
	 *         if the user specified a fraction it will be returned otherwise 1
	 *         for Result.CORRECT 0 else
	 */
	public float getFraction();

	/**
	 * @param result
	 *            the result for which the fraction should be returned
	 * @return the fraction corresponding to the result<br />
	 *         if the user specified a fraction it will be returned otherwise 1
	 *         for Result.CORRECT 0 else
	 */
	public float getFractionForResult(Result result);

	/**
	 * Gives the hint for the actual state for this {@link GeoAssignment}
	 * 
	 * @return the hint for current {@link Result}
	 */
	public String getHint();

	/**
	 * @return the actual state for this {@link GeoAssignment} as {@link Result}
	 */
	public Result getResult();

	/**
	 * @return true if user specified hints for any result in the assignment
	 */
	public boolean hasHint();

	/**
	 * @return true if user specified fractions for any result result in the
	 *         assignment
	 */
	public boolean hasFraction();

	/**
	 * @return XML describing the Exercise. Will be empty if no changes to the
	 *         Exercise were made (i.e. if isStandardExercise).<br />
	 *         Only Elements and Properties which are set or not standard will
	 *         be included.
	 * 
	 *         <pre>
	 * {@code
	 * 	<assignment toolName="Tool2">
	 * 		<result name="CORRECT" hint="Great, that&apos;s correct!" />
	 * 		<result name="WRONG" hint="Try again!" />
	 * 		<result name="NOT_ENOUGH_INPUTS" hint="You should at least have &#123;inputs&#125; in your construction!" />
	 * 		<result name="WRONG_INPUT_TYPES" hint="We were not able to find &#123;inputs&#125;, although it seems you have drawn a triangle!" />
	 * 		<result name="WRONG_OUTPUT_TYPE" hint="We couldn&apos;t find a triangle in the construction!" />
	 * 		<result name="WRONG_AFTER_RANDOMIZE" hint="Should never happen in this construction! Contact your teacher!" fraction="0.5" />
	 * 		<result name="UNKNOWN" hint="Something went wrong - ask your teacher!" />
	 * 	</assignment>
	 * }
	 * </pre>
	 */
	public String getAssignmentXML();

}