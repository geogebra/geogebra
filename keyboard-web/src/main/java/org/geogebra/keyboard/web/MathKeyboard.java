package org.geogebra.keyboard.web;


import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class MathKeyboard implements EntryPoint {
	public void onModuleLoad() {
		KeyboardFactory kbf = new KeyboardFactory();
		Keyboard layout = kbf.createMathKeyboard();
		for (Row row : layout.getModel().getRows()) {
			HorizontalPanel hp = new HorizontalPanel();
			for (WeightedButton wb : row.getButtons()) {
				ButtonImpl button = new ButtonImpl(wb);
				hp.add(button);
			}
			RootPanel.get().add(hp);
		}
	}
}
