package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;

/**
 * An exercise containing the assignments
 * 
 * @author Christoph
 *
 */
public class Exercise {

	private ArrayList<Assignment> assignments;
	private Kernel kernel;
	private Construction construction;
	private App app;

	private static Exercise INSTANCE;

	private Exercise(App app) {
		this.app = app;
		kernel = app.getKernel();
		construction = kernel.getConstruction();

		assignments = new ArrayList<Assignment>();
	}

	/**
	 * @param app
	 *            application
	 * @return the Instance of the Exercise
	 */
	public static Exercise getInstance(App app) {
		if (INSTANCE == null) {
			INSTANCE = new Exercise(app);
		}
		return INSTANCE;
	}

	/**
	 * Resets the Exercise to contain no user defined tools.
	 */
	public void reset() {
		assignments = new ArrayList<Assignment>();
	}

	/**
	 * Resets the Exercise and adds all user defined tools to the Exercise.
	 */
	public void initStandardExercise() {
		reset();
		if (app.getKernel().hasMacros()) {
			ArrayList<Macro> macros = app.getKernel().getAllMacros();
			for (Macro macro : macros) {
				addAssignment(macro);
			}
		}
	}

	/**
	 * Checks all Assignments in this exercise.
	 * 
	 * Use getResult() and getHints
	 */
	public void checkExercise() {
		if (assignments.isEmpty()) {
			initStandardExercise();
		}
		ArrayList<String> addListeners = app.getScriptManager()
				.getAddListeners();
		ArrayList<String> tmpListeners = new ArrayList<String>();
		for (String addListener : addListeners) {
			tmpListeners.add(addListener);
			app.getScriptManager().unregisterAddListener(addListener);
		}
		for (Assignment assignment : assignments) {
			assignment.checkAssignment(construction);
		}
		for (String addListener : tmpListeners) {
			app.getScriptManager().registerAddListener(addListener);
		}
	}

	/**
	 * The overall fraction of the Exercise
	 * 
	 * TODO is this really doing the right thing???? i.e. if more assignments
	 * with 100% are in the Exercise... I guess we should use weighted fractions
	 * or use optional tag...
	 * 
	 * @return the sum of fractions for each assignment
	 */
	public float getFraction() {
		float fraction = 0;
		for (Assignment assignment : assignments) {
			fraction += assignment.getFraction();
		}
		return fraction > 1 ? 1 : fraction;
	}

	/**
	 * Creates a new Assignment and adds it to the Exercise.
	 * 
	 * @param macro
	 *            the user defined Tool corresponding to the Assignment
	 * @return the newly created Assignment
	 */
	public Assignment addAssignment(Macro macro) {
		Assignment a = new Assignment(macro);
		assignments.add(a);
		return a;
	}

	/**
	 * @return all assignments contained in the exercise
	 */
	public ArrayList<Assignment> getParts() {
		return assignments;
	}

	public boolean usesMacro(Macro macro) {
		boolean uses = false;
		for (Assignment assignment : assignments) {
			uses = uses || assignment.getTool().equals(macro);
		}
		return uses;
	}

	public boolean usesMacro(int macroID) {
		return usesMacro(kernel.getMacro(macroID));
	}

	/**
	 * @return false if there are no Macros or any change to the standard
	 *         behavior has been made with the ExerciseBuilder <br />
	 *         true if there are Macros which can be used for checking
	 * 
	 */
	private boolean isStandardExercise() {
		boolean res = app.getKernel().hasMacros();
		for (int i = 0; i < assignments.size() && res; i++) {
			res = assignments.get(i).getTool()
					.equals(app.getKernel().getAllMacros().get(i))
					&& !(assignments.get(i).hasHint() || assignments.get(i)
							.hasFraction());
		}
		return res;
	}

	/**
	 * @return XML describing the Exercise. Will be empty if no changes to the
	 *         Exercise were made (i.e. if isStandardExercise).<br />
	 *         Only Elements and Properties which are set or not standard will
	 *         be included.
	 * 
	 *         <pre>
	 * {@code <exercise>
	 * 	<assignment toolName="Tool2">
	 * 		<result name="CORRECT" hint="Great, that&apos;s correct!" />
	 * 		<result name="WRONG" hint="Try again!" />
	 * 		<result name="NOT_ENOUGH_INPUTS" hint="You should at least have &#123;inputs&#125; in your construction!" />
	 * 		<result name="WRONG_INPUT_TYPES" hint="We were not able to find &#123;inputs&#125;, although it seems you have drawn a triangle!" />
	 * 		<result name="WRONG_OUTPUT_TYPE" hint="We couldn&apos;t find a triangle in the construction!" />
	 * 		<result name="WRONG_AFTER_RANDOMIZE" hint="Should never happen in this construction! Contact your teacher!" fraction="0.5" />
	 * 		<result name="UNKNOWN" hint="Something went wrong - ask your teacher!" />
	 * 	</assignment>
	 * </exercise>
	 * }
	 * </pre>
	 */
	public String getExerciseXML() {
		StringBuilder sb = new StringBuilder();
		if (!isStandardExercise()) {
			for (Assignment a : assignments) {
				sb.append(a.getAssignmentXML());
			}
		}
		return sb.toString();
	}

	public boolean isEmpty() {
		return assignments.isEmpty();
	}

	public void remove(Assignment assignment) {
		assignments.remove(assignment);
	}

}
