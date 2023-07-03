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

	/**
	 * primary text {@link Label} with additional class name
	 * @param text - label text
	 * @param className - css class name
	 * @return label with primary text color
	 */
	public Label newPrimaryText(String text, String className) {
		Label primaryText = new Label(text);
		if (className != null) {
			primaryText.setStyleName(className);
		}
		primaryText.addStyleName(Shades.NEUTRAL_900.getFgColName());
		return primaryText;
	}

	/**
	 * primary text {@link Label}
	 * @param text - label text
	 * @return label with primary text color
	 */
	public Label newPrimaryText(String text) {
		return newPrimaryText(text, null);
	}

	/**
	 * secondary text {@link Label} with additional class name
	 * @param text - label text
	 * @param className - css class name
	 * @return label with secondary text color
	 */
	public Label newSecondaryText(String text, String className) {
		Label secondaryText = new Label(text);
		if (className != null) {
			secondaryText.setStyleName(className);
		}
		secondaryText.addStyleName(Shades.NEUTRAL_700.getFgColName());
		return secondaryText;
	}

	/**
	 * secondary text {@link Label}
	 * @param text - label text
	 * @return label with secondary text color
	 */
	public Label newSecondaryText(String text) {
		return newSecondaryText(text, null);
	}

	/**
	 * disabled text {@link Label} with additional class name
	 * @param text - label text
	 * @param className - css class name
	 * @return label with disabled text color
	 */
	public Label newDisabledText(String text, String className) {
		Label disabledText = new Label(text);
		if (className != null) {
			disabledText.setStyleName(className);
		}
		disabledText.addStyleName(Shades.NEUTRAL_500.getFgColName());
		return disabledText;
	}

	/**
	 * disabled text {@link Label}
	 * @param text - label text
	 * @return label with disabled text color
	 */
	public Label newDisabledText(String text) {
		return newDisabledText(text, null);
	}
}
