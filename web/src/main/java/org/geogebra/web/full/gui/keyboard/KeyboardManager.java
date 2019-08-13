package org.geogebra.web.full.gui.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;

public class KeyboardManager {

	private App app;

	public KeyboardManager(App appWFull) {
		this.app = appWFull;
	}

	public List<Integer> getKeyboardViews() {
		ArrayList<Integer> keyboardViews = new ArrayList<>();
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView) {
			keyboardViews.add(App.VIEW_ALGEBRA);
		}
		keyboardViews.addAll(Arrays.asList(App.VIEW_CAS, App.VIEW_SPREADSHEET,
				App.VIEW_PROBABILITY_CALCULATOR));
		if (app.getKernel().getConstruction().hasInputBoxes()) {
			keyboardViews.add(App.VIEW_EUCLIDIAN);
			keyboardViews.add(App.VIEW_EUCLIDIAN2);
		}
		return keyboardViews;
	}

}
