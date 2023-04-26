package org.geogebra.web.full.gui.util;

import org.geogebra.web.full.gui.view.algebra.Marble;

public class DataTagger {
	public static final String DATA_TAG = "data-test";
	private int marbleCount = 0;
	public void tagMarble(Marble marble) {
		marble.getElement().setAttribute(DATA_TAG, "marble" + marbleCount);
		marbleCount++;
	}
}
