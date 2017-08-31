package org.geogebra.common.kernel.stepbystep.solution;

import java.util.List;
import java.util.Stack;

import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.Localization;

public class SolutionBuilder {
	private Localization loc;

	private Stack<SolutionStep> previousSteps;
	private SolutionStep currentStep;
	private SolutionStep lastStep;

	public SolutionBuilder(Localization loc) {
		this.loc = loc;

		previousSteps = new Stack<SolutionStep>();
		add(SolutionStepType.WRAPPER);
	}

	public SolutionStep getSteps() {
		while (!previousSteps.isEmpty()) {
			currentStep = previousSteps.pop();
		}

		return currentStep;
	}

	public Localization getLocalization() {
		return loc;
	}

	public void add(SolutionStepType type, int color) {
		add(new SolutionStep(loc, type, color));
	}

	public void add(SolutionStepType type, StepNode... arguments) {
		add(new SolutionStep(loc, type, arguments));
	}

	public void add(SolutionStep newStep) {
		if (currentStep == null) {
			currentStep = newStep;
		} else {
			currentStep.addSubStep(newStep);
		}

		lastStep = newStep;
	}

	public void addAll(SolutionStep s) {
		List<SolutionStep> ss = s.getSubsteps();
		if (ss != null) {
			for (int i = 0; i < ss.size(); i++) {
				currentStep.addSubStep(ss.get(i));
			}
		}
	}

	public void levelDown() {
		previousSteps.push(currentStep);
		currentStep = lastStep;
	}

	public void levelUp() {
		lastStep = currentStep = previousSteps.pop();
	}

	public void reset() {
		previousSteps = new Stack<SolutionStep>();
		currentStep = lastStep = null;
		add(SolutionStepType.WRAPPER);
	}
}
