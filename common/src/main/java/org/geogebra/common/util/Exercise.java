package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Assignment.Result;

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
		// results = new ArrayList<>();
		// TODO if there is a saved Exercise
		// else
		if (app.getKernel().hasMacros()) {
			ArrayList<Macro> macros = app.getKernel().getAllMacros();
			for (Macro macro : macros) {
				assignments.add(new Assignment(macro));
			}
		}
	}

	public void checkExercise() {
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

	public ArrayList<Result> getResults() {
		ArrayList<Result> results = new ArrayList<Result>();
		for (Assignment assignment : assignments) {
			results.add(assignment.getResult());
		}
		return results;
	}

	public ArrayList<String> getHints() {
		ArrayList<String> hints = new ArrayList<String>();
		for (Assignment assignment : assignments) {
			hints.add(assignment.getHint());
		}
		return hints;
	}

	public float getFraction() {
		float fraction = 0;
		for (Assignment assignment : assignments) {
			fraction += assignment.getFraction();
		}
		return fraction > 1 ? 1 : fraction;
	}

	public Assignment addAssignment(Macro macro) {
		Assignment a = new Assignment(macro);
		assignments.add(a);
		return a;
	}

	public ArrayList<Assignment> getParts() {
		return assignments;
	}

	public static Exercise getInstance(App app) {
		if (INSTANCE == null) {
			INSTANCE = new Exercise(app);
		}
		return INSTANCE;
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

	public String getExerciseXML() {
		StringBuilder sb = new StringBuilder();
		if (!isStandardExercise()) {
			sb.append("<exercise>");
			for (Assignment a : assignments) {
				sb.append(a.getAssignmentXML());
			}
			sb.append("</exercise>");
		}
		return sb.toString();
	}

}
