package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.html5.main.AppW;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Combobox for numeric input
 */
public abstract class NumberListBox extends ComboBoxW {
	private ListDataModel model;

	/**
	 * @param app
	 *            application
	 */
	public NumberListBox(App app) {
		super((AppW) app);
		model = getModel();
		model.add("1", "1"); //pi
		model.add(Unicode.PI_STRING, Unicode.PI_STRING); // pi
		model.add(Unicode.PI_STRING + "/2", Unicode.PI_STRING + "/2"); // pi/2
	}
	
	/**
	 * Change input value.
	 * 
	 * @param value
	 *            input value
	 */
	public void setDoubleValue(Double value) {
		String valStr = value.toString();
		for (int idx = 0; idx < getItemCount(); idx++) {
			if (getModel().get(idx).equals(valStr)) {
				setSelectedIndex(idx);
				break;
			}
		}
	}

}
