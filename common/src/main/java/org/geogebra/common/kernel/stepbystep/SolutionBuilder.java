package org.geogebra.common.kernel.stepbystep;

import java.util.Stack;

public class SolutionBuilder {
	private Stack<SolutionStep> previousSteps;
	private SolutionStep currentStep;
	private SolutionStep lastStep;

	public SolutionBuilder() {
		previousSteps = new Stack<SolutionStep>();
	}

	public SolutionStep getSteps() {
		while (!previousSteps.isEmpty()) {
			currentStep = previousSteps.pop();
		}

		return currentStep;
	}

	public void add(String s, SolutionStepTypes type) {
		SolutionStep newStep = new SolutionStep(s, type);

		if (currentStep == null) {
			currentStep = newStep;
		} else {
			currentStep.addSubStep(newStep);
		}

		lastStep = newStep;
	}

	public void addAll(SolutionStep s) {
		currentStep.addSubStep(s);
	}

	public void levelDown() {
		previousSteps.push(currentStep);
		currentStep = lastStep;
	}

	public void levelUp() {
		lastStep = currentStep = previousSteps.pop();
	}
}
