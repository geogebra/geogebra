package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.kernel.prover.AlgoAreEqual;

/**
 * @author Christoph
 * 
 */
public class Assignment {

	/**
	 * The Result of the Assignment
	 */
	public static enum Result {
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
		/**
		 * There are to many independent Inputs
		 */
		INPUT_AMBIGUOUS
	}

	/**
	 * Possible values for fractions (sorted ascending!)
	 */
	public final static float[] FRACTIONS = { 0f, 0.1f, 0.125f, 0.2f, 0.25f,
			0.3333f, 0.5f, 1f };

	private Macro macro;

	private HashMap<Result, Float> fractionForResult;
	/* The hints displayed to the Student */
	private HashMap<Result, String> hintForResult;

	private Result res;

	/**
	 * @param macro
	 */
	public Assignment(Macro macro) {
		this.macro = macro;

		fractionForResult = new HashMap<Result, Float>();

		hintForResult = new HashMap<Result, String>();

		res = Result.UNKNOWN;
	}

	/**
	 * Exhaustive Testing of the Assignment
	 * 
	 * TODO: There are some assumption on the construction which are not checked
	 * 
	 * @return {@link Result} of the check
	 */
	public Result checkAssignment(Construction cons) {
		res = Result.UNKNOWN;

		TreeSet<GeoElement> possibleInputGeos = new TreeSet<GeoElement>();
		TreeSet<GeoElement> possibleOutputGeos = new TreeSet<GeoElement>();

		// find all possible inputgeos and all outputgeos that match the type of
		// the macro
		TreeSet<GeoElement> sortedSet = cons.getGeoSetNameDescriptionOrder();
		Iterator<GeoElement> it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.hasChildren()) {
				for (GeoElement macroIn : macro.getMacroInput()) {
					if (geo.getClass().equals(macroIn.getClass())) {
						possibleInputGeos.add(geo);
					}
				}
			}
			if (!geo.isIndependent()) {
				for (GeoElement macroOut : macro.getMacroOutput()) {
					if (macroOut.getClass().equals(geo.getClass())) {
						possibleOutputGeos.add(geo);
					}
				}
			}
		}
		GeoElement[] inputGeos = new GeoElement[possibleInputGeos.size()];
		possibleInputGeos.toArray(inputGeos);

		boolean typesOK = true; // we assume that types are OK

		Test[] inputTypes = macro.getInputTypes();

		if (macro.getInputTypes().length > possibleInputGeos.size()) {
			res = Result.NOT_ENOUGH_INPUTS;
		} else if (possibleOutputGeos.isEmpty()) {
			res = Result.WRONG_OUTPUT_TYPE;
			// } else if (macro.getInputTypes().length <
			// possibleInputGeos.size()) {
			// res = Result.INPUT_AMBIGUOUS;
		} else { // if (inputTypes.length <= possibleInputGeos.size()) {
			GeoElement[] input = new GeoElement[inputTypes.length];

			PermutationOfGeOElementsUtil permutationUtil = new PermutationOfGeOElementsUtil(
					inputGeos, inputTypes.length);
			GeoElement[] inputNextPermutation = permutationUtil.next();
			while (inputNextPermutation != null
					&& (res == Result.WRONG || res == Result.UNKNOWN)) {
				// System.out.println(Arrays.toString(inputNextPermutation));
				int i = 0;
				// we assumed types are OK in the beginning
				typesOK = true;
				while (i < inputNextPermutation.length && typesOK) {
					if (inputTypes[i].check(inputNextPermutation[i])) {
						input[i] = inputNextPermutation[i];
						typesOK = true;
					} else {
						typesOK = false;
					}
					i++;
				}
				if (typesOK) {
					res = checkEqualityOfGeos(input, new ArrayList<GeoElement>(
							possibleOutputGeos), cons);
				}

				inputNextPermutation = permutationUtil.next();
			}

			// TODO: Prove!

			if (res == Result.UNKNOWN) {
				if (typesOK) {
					res = Result.WRONG;
				} else {
					res = Result.WRONG_INPUT_TYPES;
				}
			}
		}

		return res;
	}

	private Result checkEqualityOfGeos(GeoElement[] input,
			ArrayList<GeoElement> possibleOutputGeos, Construction cons) {

		// String[] label = { "" };
		AlgoMacro algoMacro = new AlgoMacro(cons, null, macro, input);
		GeoElement[] macroOutput = algoMacro.getOutput();

		GeoElement saveInput;

		// if (macroOutput[0] instanceof GeoPolygon) {
		int i = 0;
		while (i < possibleOutputGeos.size()
				&& (res == Result.UNKNOWN || res == Result.WRONG)) {
			AlgoAreEqual algoEqual = new AlgoAreEqual(cons, "", macroOutput[0],
					possibleOutputGeos.get(i));
			res = algoEqual.getResult().getBoolean() ? Result.CORRECT
					: Result.WRONG;
			int j = 0;
			while (j < input.length && res == Result.CORRECT) {
				if (input[j].isRandomizable()) {
					saveInput = input[j].copy();
					input[j].randomizeForProbabilisticChecking();
					input[j].updateCascade();
					res = algoEqual.getResult().getBoolean() ? Result.CORRECT
							: Result.WRONG_AFTER_RANDOMIZE;
					input[j].set(saveInput);
					input[j].updateCascade();
				}
				j++;
			}
			i++;
		}
		algoMacro.remove();
		return res;
	}

	/**
	 * @return the fraction for the Result if set, else 1.0 for correct Result
	 *         and 0 else.
	 */
	public float getFraction() {
		float fraction = 0f;
		if (fractionForResult.containsKey(res)) {
			fraction = fractionForResult.get(res);
		} else if (res == Result.CORRECT) {
			fraction = 1.0f;
		}
		return fraction;
	}

	public void setFractionForResult(Result res, float f) {
		fractionForResult.put(res, f);
	}

	public float getFractionForResult(Result res) {
		float frac = 0f;
		if (fractionForResult.containsKey(res)) {
			frac = fractionForResult.get(res);
		} else if (res == Result.CORRECT) {
			frac = 1.0f;
		}
		return frac;
	}

	public String getIconFileName() {
		return macro.getIconFileName();
	}

	public String getToolName() {
		return macro.getToolName();
	}

	/**
	 * Gives the hint for the actual state for this {@link Assignment}
	 * 
	 * @return the hint for current {@link Result}
	 */
	public String getHint() {
		return hintForResult.get(res);
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
	 * @return the actual state for this {@link Assignment} as {@link Result}
	 */
	public Result getResult() {
		return res;
	}

	public String getHintForResult(Result res) {
		String hint = "";
		if (hintForResult.containsKey(res)) {
			hint = hintForResult.get(res);
		}
		return hint;
	}
}

// Eyal Schneider
// http://stackoverflow.com/a/2799190
/**
 * Utility Class to permute the array of GeoElements
 * 
 * @author Eyal Schneider, http://stackoverflow.com/a/2799190
 * @author Adaption: Christoph Reinisch
 */
class PermutationOfGeOElementsUtil {
	private GeoElement[] arr;
	private int[] permSwappings;

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr) {
		this(arr, arr.length);
	}

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 * @param permSize
	 *            the Elements k < arr.length of the array you need to permute
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr, int permSize) {

		// this.arr = arr.clone();
		this.arr = new GeoElement[arr.length];
		System.arraycopy(arr, 0, this.arr, 0, arr.length);
		this.permSwappings = new int[permSize];
		for (int i = 0; i < permSwappings.length; i++) {
			permSwappings[i] = i;
		}
	}

	/**
	 * @return the next permutation of the array if exists, null otherwise
	 */
	public GeoElement[] next() {
		if (arr == null) {
			return null;
		}

		GeoElement[] res = new GeoElement[permSwappings.length];
		System.arraycopy(arr, 0, res, 0, permSwappings.length);
		// GeoElement[] res = Arrays.copyOf(arr, permSwappings.length);

		// Prepare next
		int i = permSwappings.length - 1;
		while (i >= 0 && permSwappings[i] == arr.length - 1) {
			swap(i, permSwappings[i]); // Undo the swap represented by
										// permSwappings[i]
			permSwappings[i] = i;
			i--;
		}

		if (i < 0) {
			arr = null;
		} else {
			int prev = permSwappings[i];
			swap(i, prev);
			int next = prev + 1;
			permSwappings[i] = next;
			swap(i, next);
		}

		return res;
	}

	private void swap(int i, int j) {
		GeoElement tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}