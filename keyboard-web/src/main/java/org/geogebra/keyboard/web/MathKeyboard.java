package org.geogebra.keyboard.web;


import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;

public class MathKeyboard implements EntryPoint {
	public void onModuleLoad() {
		KeyboardFactory kbf = new KeyboardFactory();
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

		KeyPanelBase keyboard = buildPanel(kbf.createMathKeyboard(), kb);
		RootPanel.get().add(keyboard);

		keyboard = buildPanel(kbf.createFunctionsKeyboard(), kb);
		RootPanel.get().add(keyboard);

		keyboard = buildPanel(kbf.createGreekKeyboard(), kb);
		RootPanel.get().add(keyboard);

		keyboard = buildPanel(
				kbf.createLettersKeyboard("QWERTYUIOP", "ASDFGHJKL''",
						"ZXCVBNM"),
				kb);
		RootPanel.get().add(keyboard);

		RootPanel.get().addStyleName("GeoGebraFrame");
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
	}

	private KeyPanelBase buildPanel(Keyboard layout, KBBase kb) {
		KeyPanelBase keyboard = new KeyPanelBase();
		keyboard.addStyleName("KeyBoard");
		keyboard.addStyleName("normal");
		int index = 0;
		for (Row row : layout.getModel().getRows()) {
			double offset = 0;
			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getActionName())) {
					offset = wb.getWeight();
				} else {
					KeyBoardButtonBase button = new KeyBoardButtonBase(
							wb.getActionName(),
							wb.getActionName(), kb);
					if (offset > 0) {
					button.getElement().getStyle().setMarginLeft(offset * 50,
							Unit.PX);
					}
					keyboard.addToRow(index, button);
					offset = 0;
				}
			}
			index++;
		}
		return keyboard;
	}
}
