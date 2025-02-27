package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.SimplePanel;

public class BaseWidgetFactory {

	public static final BaseWidgetFactory INSTANCE = new BaseWidgetFactory();

	/**
	 * @return flow panel; to be mocked
	 */
	public FlowPanel newPanel() {
		return new FlowPanel();
	}

	/**
	 * @param styleName - style name to add to the panel
	 * @return flow panel
	 */
	public FlowPanel newPanel(String styleName) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(styleName);
		return panel;
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
		return newText(text, className, Shades.NEUTRAL_900);
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
		return newText(text, className, Shades.NEUTRAL_700);
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
		return newText(text, className, Shades.NEUTRAL_500);
	}

	/**
	 * disabled text {@link Label}
	 * @param text - label text
	 * @return label with disabled text color
	 */
	public Label newDisabledText(String text) {
		return newDisabledText(text, null);
	}

	/**
	 * @param text - text of label
	 * @param className - additional css class name
	 * @param foreground - shade
	 * @return label with defined shade
	 */
	private Label newText(String text, String className, Shades foreground) {
		Label label = new Label(text);
		if (className != null) {
			label.setStyleName(className);
		}
		label.addStyleName(foreground.getFgColName());
		return label;
	}

	/**
	 * @param isVertical whether the divider is vertical or horizontal
	 * @return divider component
	 */
	public SimplePanel newDivider(boolean isVertical) {
		SimplePanel widget = new SimplePanel();
		widget.addStyleName("divider");
		if (isVertical) {
			widget.addStyleName("vertical");
		}
		widget.addStyleName(Shades.NEUTRAL_300.getName());
		return widget;
	}
}
