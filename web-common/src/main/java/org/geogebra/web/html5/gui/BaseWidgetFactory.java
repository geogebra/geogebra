package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

public class BaseWidgetFactory {

	public static final BaseWidgetFactory INSTANCE = new BaseWidgetFactory();

	/**
	 * @return flow panel; to be mocked
	 */
	public FlowPanel newPanel() {
		return new FlowPanel();
	}

	/**
	 * @return button, to be mocked
	 */
	public Button newButton() {
		Button btn = new Button();
		btn.getElement().setAttribute("type", "button");
		return btn;
	}

	/**
	 * @return button, to be mocked
	 */
	public Label newLabel() {
		return new Label();
	}

	/**
	 * @return list box, to be mocked
	 */
	public ListBox newListBox() {
		return new ListBox();
	}

	public SliderW newSlider(int i, int j) {
		return new SliderW(i, j);
	}

	public Label newPrimaryText(String text, String className) {
		Label primaryText = new Label(text);
		if (className != null) {
			primaryText.setStyleName(className);
		}
		primaryText.addStyleName(Shades.NEUTRAL_900.getName());
		return primaryText;
	}

	public Label newPrimaryText(String text) {
		return newPrimaryText(text, null);
	}

	public Label newSecondaryText(String text, String className) {
		Label secondaryText = new Label(text);
		if (className != null) {
			secondaryText.setStyleName(className);
		}
		secondaryText.addStyleName(Shades.NEUTRAL_700.getName());
		return secondaryText;
	}
}
