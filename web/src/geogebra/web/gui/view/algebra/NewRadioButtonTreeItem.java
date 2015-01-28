package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.Kernel;
import geogebra.html5.gui.inputfield.AutoCompleteW;

import java.util.List;

/**
 * NewRadioButtonTreeItem for creating new formulas in the algebra view
 * 
 * File created by Arpad Fekete
 */

public class NewRadioButtonTreeItem extends RadioButtonTreeItem implements
        AutoCompleteW {
	public NewRadioButtonTreeItem(Kernel kern) {
		super(kern);
	}

	public boolean getAutoComplete() {
		return true;
	}

	public List<String> resetCompletions() {
		// TODO!
		return null;
	}

	public List<String> getCompletions() {
		// TODO!
		return null;
	}
}
