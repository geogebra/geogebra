/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * @param min min
	 * @param max max
	 * @return slider with given range
	 */
	public SliderW newSlider(int min, int max) {
		return new SliderW(min, max);
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
