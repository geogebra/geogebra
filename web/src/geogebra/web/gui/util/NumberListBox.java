package geogebra.web.gui.util;

import geogebra.common.main.App;

import com.google.gwt.user.client.ui.ListBox;

public class NumberListBox extends ListBox {
	private static final String PI_STRING = "\u03c0";
	private App app;
	public NumberListBox(App app) {		
		this.app = app;
		addItem("1"); //pi
		addItem(PI_STRING); //pi
		addItem(PI_STRING + "/2"); //pi/2
	}

	public double getValue() {
		final String text = getItemText(getSelectedIndex()).toString().trim();
		if (text.equals("")) return Double.NaN;
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);			
	}

}
