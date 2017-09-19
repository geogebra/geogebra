package org.geogebra.common.kernel.cas;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;

public interface HasSteps {

	void getSteps(StepGuiBuilder builder);

	boolean canShowSteps();

}
