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

	/**
	 * Create a new Exercise
	 * 
	 * @param app
	 *            application
	 */
	public Exercise(App app) {
		this.app = app;
		kernel = app.getKernel();
		construction = kernel.getConstruction();

		assignments = new ArrayList<Assignment>();
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
	 * If one Assignment has 100%, the overall fraction will be 1 minus the sum
	 * of the fractions of the Assignments having negative Fractions.<br>
	 * Otherwise the overall fraction will be the sum of all positive fractions
	 * capped at 1 minus all negative fractions and then capped at 0.
	 * 
	 * @return the sum of fractions for all assignments
	 */
	public float getFraction() {
		float fractionsumplus = 0;
		float fractionsumminus = 0;
		Assignment singleCorrect = null;
		double stdPrecision = Kernel.STANDARD_PRECISION;
		for (Assignment assignment : assignments) {
			float assignmenFraction = assignment.getFraction();

			if (assignmenFraction >= 0) {
				if (assignmenFraction >= 1 - stdPrecision) {
					singleCorrect = assignment;
				}
				fractionsumplus += assignmenFraction;
			} else {
				fractionsumminus += assignmenFraction;
			}
		}
		float fraction = 0;
		if (singleCorrect != null || fractionsumplus >= 1 - stdPrecision) {
			fraction = 1;
		} else {
			fraction = fractionsumplus;
		}
		fraction += fractionsumminus;
		return fraction < 0 + stdPrecision ? 0 : fraction;
	}

	/**
	 * Creates a new Assignment and adds it to the Exercise.
	 * 
	 * @param macro
	 *            the user defined Tool corresponding to the Assignment
	 * @return the newly created Assignment
	 */
	public Assignment addAssignment(Macro macro) {
		Assignment a = getAssignment(macro);
		if (a == null) {
			a = new Assignment(macro);
			addAssignment(a);
		}
		return a;
	}

	/**
	 * @return all assignments contained in the exercise
	 */
	public ArrayList<Assignment> getParts() {
		return assignments;
	}

	/**
	 * Check if a macro is already used by this exercise
	 * 
	 * @param macro
	 *            the user defined tool
	 * @return true if this exercise uses the macro
	 */
	public boolean usesMacro(Macro macro) {
		boolean uses = false;
		for (Assignment assignment : assignments) {
			uses = uses || assignment.getTool().equals(macro);
		}
		return uses;
	}

	/**
	 * Check if a macro is already used by this exercise
	 * 
	 * @param macroID
	 *            the id of the user defined tool
	 * @return {@link #usesMacro(Macro)}
	 */
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
	public String getExerciseXML() {
		StringBuilder sb = new StringBuilder();
		if (!isStandardExercise()) {
			for (Assignment a : assignments) {
				sb.append(a.getAssignmentXML());
			}
		}
		return sb.toString();
	}

	/**
	 * Check if the Exercise has assignments
	 * 
	 * @return true if there are no assignments in this Exercise
	 */
	public boolean isEmpty() {
		return assignments.isEmpty();
	}

	/**
	 * Remove an assignment from this Exercise
	 * 
	 * @param assignment
	 *            the assignment to be removed from the Exercise
	 */
	public void remove(Assignment assignment) {
		assignments.remove(assignment);
	}

	/**
	 * Should be used if Macro is deleted
	 * 
	 * @param macro
	 *            the macro being used by the assignment which should be removed
	 */
	public void removeAssignment(Macro macro) {
		Assignment assignmentToRemove = null;
		for (Assignment assignment : assignments) {
			if (assignment.getTool().equals(macro)) {
				assignmentToRemove = assignment;
			}
		}
		if (assignmentToRemove != null) {
			remove(assignmentToRemove);
		}
	}

	/**
	 * Removes all Assignments from the Exercise
	 */
	public void removeAllAssignments() {
		reset();
	}

	/**
	 * @param macro
	 *            the macro being used by the assignment which should be
	 *            retrieved
	 * @return the assignment being used by the macro or null if macro isn't
	 *         used by the Exercise
	 */
	public Assignment getAssignment(Macro macro) {
		Assignment assignmentToReturn = null;
		for (Assignment assignment : assignments) {
			if (assignment.getTool().equals(macro)) {
				assignmentToReturn = assignment;
			}
		}
		return assignmentToReturn;
	}

	/**
	 * Adds an assignment to the exercise
	 * 
	 * @param assignment
	 *            the assignment to add to the Exercise
	 */
	public void addAssignment(Assignment assignment) {
		addAssignment(assignments.size(), assignment);
	}

	private boolean isValid(Assignment assignment) {
		return !assignments.contains(assignment)
				&& kernel.getMacro(assignment.getTool().getCommandName()) != null;
	}

	/**
	 * Adds an assignment to the exercise at specified index
	 * 
	 * @param assignmentIndex
	 *            the index to be used for the assignment, shifts all Elments =>
	 *            assignmentIndex to the right
	 * @param assignment
	 *            the assignment to add to the Exercise
	 */
	public void addAssignment(int assignmentIndex, Assignment assignment) {
		if (isValid(assignment)) {
			assignments.add(assignmentIndex, assignment);
		}
	}
}
