package org.geogebra.web.full.gui.util;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.html5.main.AppW;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Combobox for numeric input
 */
public abstract class NumberListBox extends ComboBoxW {
	private static final String PI_HALF_STRING = Unicode.PI_STRING + "/2";

	/**
	 * @param app
	 *            application
	 */
	public NumberListBox(App app) {
		super((AppW) app);
		addItem("1");
		addItem(Unicode.PI_STRING);
		addItem(PI_HALF_STRING);
	}
	
	/**
	 * Change input value.
	 * 
	 * @param value
	 *            input value
	 */
	public void setDoubleValue(Double value) {
		if (DoubleUtil.isEqual(value, Math.PI)) {
			setSelected(Unicode.PI_STRING);
		}  else if (DoubleUtil.isEqual(value, Kernel.PI_HALF)) {
			setSelected(PI_HALF_STRING);
		} else {
			setSelected(getApp().getKernel().format(value, StringTemplate.defaultTemplate));
		}
	}

}
