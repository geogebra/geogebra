package org.geogebra.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.util.debug.Log;

/**
 * @author Christoph
 * 
 */
public class GeoAssignment extends Assignment {

	/**
	 * Possible values for CheckOperations
	 */
	public final static String[] CHECK_OPERATIONS = { "==", "AreEqual",
			"AreCongruent" };

	private String checkOp;

	private Inspecting geoInspector;

	private Macro macro;

	private GeoElement[] solutionObjects;

	private int callsToEqual, callsToCheckTypes;

	private Test[] inputTypes;
	/**
	 * The possible InputTypes for this Assignment
	 */
	HashSet<Test> uniqueInputTypes;
	private TreeSet<GeoElement> randomizeablePredecessors;

	private Construction cons;

	/**
	 * @param macro
	 *            the macro (user defined tool) corresponding to the assignment
	 */
	public GeoAssignment(Macro macro) {
		super(macro.getKernel());
		cons = kernel.getConstruction();
		this.macro = macro;
		inputTypes = macro.getInputTypes();

		uniqueInputTypes = new HashSet<Test>(Arrays.asList(inputTypes));
		randomizeablePredecessors = new TreeSet<GeoElement>();

		checkOp = "AreEqual";

		geoInspector = new Inspecting() {

			public boolean check(ExpressionValue v) {
				return ((GeoElement) v).isLabelSet()
						&& uniqueInputTypes.contains(Test.getSpecificTest(v));
			}

		};

	}

	@Override
	public Result checkAssignment() {
		res = Result.UNKNOWN;
		if (isValid()) {
			callsToEqual = 0;
			callsToCheckTypes = 0;
			boolean oldSilentMode = cons.getKernel().isSilentMode();
			cons.getKernel().setSilentMode(true);

			TreeSet<GeoElement> possibleOutputGeos = new TreeSet<GeoElement>(
					Collections.reverseOrder());

			// find all possible inputgeos and all outputgeos that match the
			// type of
			// the macro
			TreeSet<GeoElement> sortedSet = cons
					.getGeoSetNameDescriptionOrder();

			Iterator<GeoElement> it = sortedSet.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				TreeSet<GeoElement> allPredecessors = geo.getAllPredecessors();
				if (!allPredecessors.isEmpty()) {
					for (GeoElement macroOut : macro.getMacroOutput()) {
						if (macroOut.getClass().equals(geo.getClass())) {
							possibleOutputGeos.add(geo);
						}
					}
				}
			}
			if (macro.getMacroOutput().length > possibleOutputGeos.size()) {
				res = Result.WRONG_OUTPUT_TYPE;
			} else {
				checkCorrectness(possibleOutputGeos);
			}
			Log.debug("Checking on " + macro.getToolName()
					+ " completed. Comparisons of Objects: " + callsToEqual);
			Log.debug("Checking on " + macro.getToolName()
					+ " completed. Checked types of Objects: "
					+ callsToCheckTypes);
			cons.getKernel().setSilentMode(oldSilentMode);
		}
		return res;
	}

	private void checkCorrectness(TreeSet<GeoElement> possibleOutputGeos) {

		PermutationOfGeOElementsUtil outputPermutationUtil = new PermutationOfGeOElementsUtil(
				possibleOutputGeos.toArray(new GeoElement[0]),
				macro.getMacroOutput().length);
		GeoElement[] possibleOutputPermutation = outputPermutationUtil.next();

		TreeSet<Result> partRes = new TreeSet<Result>();
		while (possibleOutputPermutation != null && res != Result.CORRECT) {
			TreeSet<GeoElement> possibleInputGeos = getAllPredecessors(
					possibleOutputPermutation, geoInspector);
			if (possibleInputGeos.size() < macro.getInputTypes().length) {
				res = Result.NOT_ENOUGH_INPUTS;
			} else {
				checkPermutationsOfInputs(possibleOutputPermutation, partRes,
						possibleInputGeos);
			}
			possibleOutputPermutation = outputPermutationUtil.next();
		}
	}

	private void checkPermutationsOfInputs(
			GeoElement[] possibleOutputPermutation, TreeSet<Result> partRes,
			TreeSet<GeoElement> possibleInputGeos) {
		boolean isTypeCheckNeeded = uniqueInputTypes.size() > 1;
		GeoElement[] input;
		PermutationOfGeOElementsUtil inputPermutationUtil = new PermutationOfGeOElementsUtil(
				possibleInputGeos.toArray(new GeoElement[0]),
				macro.getInputTypes().length);

		input = inputPermutationUtil.next();
		boolean solutionFound = false;
		while (input != null && !solutionFound) {
			partRes.clear();
			if (!isTypeCheckNeeded || areTypesOK(input)) {
				AlgoMacro algoMacro = new AlgoMacro(cons, null, macro, input);
				GeoElement[] macroOutput = algoMacro.getOutput();
				for (int i = 0; i < possibleOutputPermutation.length
						&& (!partRes.contains(Result.WRONG)); i++) {
					checkEqualityOfGeos(input, macroOutput[i],
							possibleOutputPermutation, i, partRes);
				}
				algoMacro.remove();
				solutionFound = !partRes.contains(Result.WRONG)
						&& !partRes.contains(Result.WRONG_AFTER_RANDOMIZE)
						&& partRes.contains(Result.CORRECT);
			} else if (res != Result.WRONG_AFTER_RANDOMIZE
					&& res != Result.WRONG) {
				res = Result.WRONG_INPUT_TYPES;
			}
			if (partRes.contains(Result.WRONG)
					&& res != Result.WRONG_AFTER_RANDOMIZE) {
				res = Result.WRONG;
			} else if (partRes.contains(Result.WRONG_AFTER_RANDOMIZE)) {
				res = Result.WRONG_AFTER_RANDOMIZE;
				Log.debug("Objects wrong after Randomize: "
						+ toString(possibleOutputPermutation));
				Log.debug("Objects used as inputs: " + toString(input));
			} else if (partRes.contains(Result.CORRECT)) {
				res = Result.CORRECT;
				solutionObjects = possibleOutputPermutation;
				Log.debug("Objects found to be the Solution: "
						+ toString(solutionObjects));
				Log.debug("Objects used as inputs: " + toString(input));
			}
			input = inputPermutationUtil.next();

		}
	}

	private void checkEqualityOfGeos(GeoElement[] input,
			GeoElement macroOutput, GeoElement possibleOutput[], int i,
			TreeSet<Result> partRes) {
		// TODO Check if we really need to call adjustMoveableOutputs with all
		// possibleOutputs ie.the array
		boolean mayAdjustMoveableOutputs = adjustMoveableOutputs(macroOutput,
				possibleOutput);
		if (checkOp.equals("AreEqual")) {
			// GeoElement root = new AlgoAreEqual(cons, macroOutput,
			// possibleOutput[i]).getOutput()[0];
			//
			// AlgoProve algoProve = new AlgoProve(cons, null, root);
			partRes.add(macroOutput.isEqual(possibleOutput[i]) ? Result.CORRECT
					: Result.WRONG);
			// partRes.add(algoProve.getGeoBoolean().getBoolean() ?
			// Result.CORRECT
			// : Result.WRONG);
		} else if (checkOp.equals("==")) {
			partRes.add(ExpressionNodeEvaluator.evalEquals(macro.getKernel(),
					macroOutput, possibleOutput[i]).getBoolean() ? Result.CORRECT
					: Result.WRONG);
		} else if (checkOp.equals("AreCongruent")) {
			partRes.add(boolVal(macroOutput.isCongruent(possibleOutput[i])) ? Result.CORRECT
					: Result.WRONG);
		}
		callsToEqual++;
		int j = 0;
		if (partRes.contains(Result.CORRECT)) {
			while (j < input.length
					&& !partRes.contains(Result.WRONG_AFTER_RANDOMIZE)) {
				if (input[j].isRandomizable()) {
					mayAdjustMoveableOutputs = doProbabilisticChecking(
							input[j], macroOutput, possibleOutput, i, partRes,
							mayAdjustMoveableOutputs);
				} else {
					input[j].addRandomizablePredecessorsToSet(randomizeablePredecessors);
					for (int k = 0; k < randomizeablePredecessors.size()
							&& !partRes.contains(Result.WRONG_AFTER_RANDOMIZE); k++) {
						mayAdjustMoveableOutputs = doProbabilisticChecking(
								randomizeablePredecessors.pollFirst(),
								macroOutput, possibleOutput, i, partRes,
								mayAdjustMoveableOutputs);
					}
				}
				j++;
			}
		}
	}

	private static boolean boolVal(Boolean congruent) {
		return congruent != null && congruent;
	}

	private boolean doProbabilisticChecking(GeoElement geoToRandomize,
			GeoElement macroOutput, GeoElement[] possibleOutput, int i,
			TreeSet<Result> partRes, boolean mayAdjustMoveableOutputs) {
		boolean mayAdjustMoveableOutputsL = mayAdjustMoveableOutputs;
		GeoElement saveInput;
		saveInput = geoToRandomize.copy();
		geoToRandomize.randomizeForProbabilisticChecking();
		geoToRandomize.updateCascade();
		if (mayAdjustMoveableOutputs) {
			mayAdjustMoveableOutputsL = adjustMoveableOutputs(macroOutput,
					possibleOutput);
		}

		if (checkOp.equals("AreEqual")) {
			partRes.add(macroOutput.isEqual(possibleOutput[i]) ? Result.CORRECT
					: Result.WRONG_AFTER_RANDOMIZE);
		} else if (checkOp.equals("==")) {
			partRes.add(ExpressionNodeEvaluator.evalEquals(macro.getKernel(),
					macroOutput, possibleOutput[i]).getBoolean() ? Result.CORRECT
					: Result.WRONG_AFTER_RANDOMIZE);
		} else if (checkOp.equals("AreCongruent")) {
			partRes.add(boolVal(macroOutput.isCongruent(possibleOutput[i]))
					? Result.CORRECT
					: Result.WRONG_AFTER_RANDOMIZE);
		}
		callsToEqual++;
		geoToRandomize.set(saveInput);
		geoToRandomize.updateCascade();
		return mayAdjustMoveableOutputsL;
	}

	/**
	 * If some macro outputs are moveable (eg. point on path), push them close
	 * to the corresponding possible outputs (within given path/region
	 * constraint)
	 * 
	 * @param macroOutput
	 *            sample macro output
	 * @param possibleOutput
	 *            possible outputs
	 * @return whether an output was changeable
	 */
	private static boolean adjustMoveableOutputs(GeoElement macroOutput,
			GeoElement[] possibleOutput) {
		boolean ret = false;
		AlgoMacro algo = (AlgoMacro) macroOutput.getParentAlgorithm();
		int size = algo.getOutputLength();
		for (int i = 0; i < size; i++) {
			if (algo.isChangeable(algo.getOutput(i))
					&& possibleOutput[i] instanceof GeoPoint) {
				GeoPoint pt = (GeoPoint) possibleOutput[i];
				algo.setCoords((GeoPoint) algo.getOutput(i), pt.getX(),
						pt.getY(), pt.getZ());
				ret = true;
			}
		}
		return ret;
	}

	private static TreeSet<GeoElement> getAllPredecessors(
			GeoElement[] possibleOutputPermutation, Inspecting geoInspector) {

		TreeSet<GeoElement> possibleInputGeos = new TreeSet<GeoElement>();
		for (int i = 0; i < possibleOutputPermutation.length; i++) {
			possibleOutputPermutation[i].addPredecessorsToSet(
					possibleInputGeos, geoInspector);
		}
		for (int i = 0; i < possibleOutputPermutation.length; i++) {
			possibleInputGeos.remove(possibleOutputPermutation[i]);
		}

		return possibleInputGeos;
	}

	private boolean areTypesOK(GeoElement[] input) {
		boolean typesOK = true; // we assume that types are OK

		int k = 0;
		while (k < input.length && typesOK) {
			callsToCheckTypes++;
			if (inputTypes[k].check(input[k])) {
				typesOK = true;
			} else {
				typesOK = false;
			}
			k++;
		}
		return typesOK;
	}

	private static String toString(GeoElement[] elements) {
		String solObj = "";
		for (GeoElement g : elements) {
			if (!solObj.isEmpty()) {
				solObj += ", ";
			}
			solObj += g.toString(StringTemplate.defaultTemplate);

		}
		return solObj;
	}

	/**
	 * @return the icon file name of the user defined tool corresponding to this
	 *         assignment
	 */
	@Override
	public String getIconFileName() {
		return macro.getIconFileName();
	}

	/**
	 * @return the Name of the Tool corresponding to this Assignment
	 */
	public String getToolName() {
		return macro.getToolName();
	}

	/**
	 * @return the user defined tool corresponding to the assignment
	 */
	public Macro getTool() {
		return macro;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geogebra.common.util.Assignment#getAssignmentXML()
	 */
	@Override
	public String getAssignmentXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t<assignment toolName=\"");
		StringUtil.encodeXML(sb, macro.getToolName());
		sb.append("\" commandName=\"");
		StringUtil.encodeXML(sb, macro.getCommandName());
		sb.append("\" checkOperation=\"");
		StringUtil.encodeXML(sb, getCheckOperation());
		sb.append("\">\n");

		getAssignmentXML(sb);

		return sb.toString();
	}

	/**
	 * @param newTool
	 *            the Macro which should be used to checking
	 */
	public void setMacro(Macro newTool) {
		macro = newTool;
	}

	/**
	 * @return a String representing the operator/method used by this assignment
	 *         to check correctness. One of "==", "AreEqual", "AreCongruent"
	 */
	public String getCheckOperation() {
		return checkOp;
	}

	/**
	 * @param checkOp
	 *            The operator/method which should be used for checking this
	 *            Assignment. One of { "==", "AreEqual", "AreCongruent" }
	 */
	public void setCheckOperation(String checkOp) {
		this.checkOp = checkOp;
	}

	@Override
	public Result[] possibleResults() {
		return Result.values();
	}

	@Override
	public String getDisplayName() {
		return getToolName();
	}

	@Override
	public boolean isValid() {
		return kernel.getMacro(getTool().getCommandName()) != null;
	}
}

// Eyal Schneider
// http://stackoverflow.com/a/2799190
/**
 * Utility Class to permute the array of GeoElements
 * 
 * @author Eyal Schneider, http://stackoverflow.com/a/2799190
 * @author Adaption: Christoph Stadlbauer
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