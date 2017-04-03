package org.geogebra.keyboard.web;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.Row;
import org.geogebra.keyboard.base.linear.WeightedButton;
import org.geogebra.keyboard.base.linear.impl.factory.KeyboardFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class MathKeyboard implements EntryPoint {
	public void onModuleLoad() {
		KeyboardFactory kbf = new KeyboardFactory();
		LinearKeyboard layout = kbf.createMathKeyboard();
		for (Row row : layout.getRows()) {
			HorizontalPanel hp = new HorizontalPanel();
			for (WeightedButton wb : row.getButtons()) {
				ButtonImpl button = new ButtonImpl(wb);
				hp.add(button);
			}
			RootPanel.get().add(hp);
		}
	}
}
