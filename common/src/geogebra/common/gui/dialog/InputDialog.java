package geogebra.common.gui.dialog;

import java.util.ArrayList;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.geos.GeoElement;

public abstract class InputDialog {

	protected String initString;
	protected InputHandler inputHandler;
	protected String inputText = null;
	protected ArrayList<GeoElement> tempArrayList = new ArrayList<GeoElement>();
	protected boolean processInputHandler() {
		return inputHandler.processInput(inputText);
	}

}
