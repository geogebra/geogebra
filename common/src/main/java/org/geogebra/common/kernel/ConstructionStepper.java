package org.geogebra.common.kernel;

public interface ConstructionStepper {

	void firstStep();

	void previousStep();

	void nextStep();

	void lastStep();

	int getCurrentStepNumber();

	int getLastStepNumber();

	void setConstructionStep(int i);

}
