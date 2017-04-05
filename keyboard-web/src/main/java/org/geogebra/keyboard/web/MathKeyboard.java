package org.geogebra.keyboard.web;


import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class MathKeyboard implements EntryPoint {
	public void onModuleLoad() {
		KeyboardFactory kbf = new KeyboardFactory();
		HasKeyboard hk = null;
		ButtonHandler kb = new ButtonHandler() {
			@Override
			public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
				// TODO Auto-generated method stub

			}
		};
		FlowPanel tabs = new FlowPanel();
		HorizontalPanel switcher = new HorizontalPanel();
		KeyPanelBase keyboard = buildPanel(kbf.createMathKeyboard(), kb);
		tabs.add(keyboard);
		switcher.add(makeSwitcherButton(keyboard, "DEF"));

		keyboard = buildPanel(kbf.createFunctionsKeyboard(), kb);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "Fn"));

		keyboard = buildPanel(kbf.createGreekKeyboard(), kb);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "GREEK"));

		keyboard = buildPanel(
				kbf.createLettersKeyboard("QWERTYUIOP", "ASDFGHJKL''",
						"ZXCVBNM"),
				kb);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "ABC"));





		RootPanel.get().add(switcher);
		RootPanel.get().add(tabs);
		RootPanel.get().addStyleName("GeoGebraFrame");
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
	}

	private Widget makeSwitcherButton(final KeyPanelBase keyboard,
			String string) {
		Button ret = new Button(string);
		ClickStartHandler.init(ret, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				for (int i = 0; i < ((FlowPanel) keyboard.getParent())
						.getWidgetCount(); i++) {
					((FlowPanel) keyboard.getParent()).getWidget(i)
							.setVisible(false);
				}
				keyboard.setVisible(true);

			}
		});
		return ret;
	}

	private KeyPanelBase buildPanel(Keyboard layout, ButtonHandler b) {
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
							wb.getActionName(), b);
					if (offset > 0) {
					button.getElement().getStyle().setMarginLeft(offset * 50,
							Unit.PX);
					}
					button.getElement().getStyle().setWidth(wb.getWeight() * 50,
							Unit.PX);
					keyboard.addToRow(index, button);
					offset = 0;
				}
			}
			index++;
		}
		return keyboard;
	}
}
