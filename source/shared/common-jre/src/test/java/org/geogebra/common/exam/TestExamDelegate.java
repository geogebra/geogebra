package org.geogebra.common.exam;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.move.ggtapi.models.Material;

public class TestExamDelegate implements ExamControllerDelegate {

	private SuiteSubApp subApp = SuiteSubApp.GRAPHING;
	private Material material;

	@Override
	public void examClearApps() {

	}

	@Override
	public void examClearClipboard() {

	}

	@Override
	public void examSetActiveMaterial(@CheckForNull Material material) {
		this.material = material;
	}

	@Override
	public @CheckForNull Material examGetActiveMaterial() {
		return material;
	}

	@Override
	public @CheckForNull SuiteSubApp examGetCurrentSubApp() {
		return subApp;
	}

	@Override
	public void examSwitchSubApp(@Nonnull SuiteSubApp subApp) {
		this.subApp = subApp;
	}
}
