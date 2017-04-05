package org.geogebra.keyboard.web;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.KeyboardLocale;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedKeyboard extends FlowPanel {

	private KeyboardLocale locale;

	public TabbedKeyboard() {

	}

	public void buildGUI(ButtonHandler kb, KeyboardLocale loc) {
		KeyboardFactory kbf = new KeyboardFactory();
		FlowPanel tabs = new FlowPanel();
		HorizontalPanel switcher = new HorizontalPanel();
		this.locale = loc;
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

		keyboard = buildPanel(kbf.createLettersKeyboard("QWERTYUIOP",
				"ASDFGHJKL''", "ZXCVBNM"), kb);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "ABC"));

		add(switcher);
		add(tabs);
	}

	private Widget makeSwitcherButton(final KeyPanelBase keyboard,
			String string) {
		Button ret = new Button(string);
		ClickStartHandler.init(ret, new ClickStartHandler(true, true) {

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
					KeyBoardButtonBase button = makeButton(wb, b);
					if (offset > 0) {
						button.getElement().getStyle()
								.setMarginLeft(offset * 50, Unit.PX);
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

	private KeyBoardButtonBase makeButton(WeightedButton wb, ButtonHandler b) {
		switch (wb.getActionType()) {


		case INPUT_TRANSLATE_MENU:
			return new KeyBoardButtonBase(locale.getMenu(wb.getActionName()),
					wb.getActionName().replace("Function.", ""), b);
		case INPUT_TRANSLATE_COMMAND:
			return new KeyBoardButtonBase(locale.getCommand(wb.getActionName()),
					wb.getActionName(), b);
		case CUSTOM:
			return new KeyBoardButtonBase(wb.getActionName(),
					wb.getActionName(), b);
		case INPUT:
		default:
			return new KeyBoardButtonBase(wb.getActionName(),
					wb.getActionName(), b);
		}

	}
}
