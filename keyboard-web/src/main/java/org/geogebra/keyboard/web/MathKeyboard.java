package org.geogebra.keyboard.web;


import org.geogebra.common.euclidian.event.PointerEventType;
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
		HasKeyboard hk = null;
		KBBase kb = new KBBase(true, hk) {

			@Override
			public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setKeyboardMode(KeyboardMode mode) {
				// TODO Auto-generated method stub

			}
		};
		for (Row row : layout.getModel().getRows()) {
			HorizontalPanel hp = new HorizontalPanel();
			for (WeightedButton wb : row.getButtons()) {
				KeyBoardButtonBase button = new KeyBoardButtonBase(kb);
				hp.add(button);
			}
			RootPanel.get().add(hp);
		}
	}
}
