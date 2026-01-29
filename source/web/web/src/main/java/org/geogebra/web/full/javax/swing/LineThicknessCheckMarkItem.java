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

package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * item with checkmark and line thickness preview
 */
public class LineThicknessCheckMarkItem extends FlowPanel {
	private Element checkImg;
	private boolean selected;
	private Label text;
	private final int value;

	/**
	 * constructor
	 * @param thicknessStyle style class name of item
	 * @param value thickness
	 * @param checkMark icon
	 */
	public LineThicknessCheckMarkItem(String thicknessStyle, int value, IconSpec checkMark) {
		addStyleName("lineThicknessItem");
		this.value = value;
		buildGUI(thicknessStyle, checkMark);
	}

	/**
	 * constructor
	 * @param itemText text of menu item
	 * @param thicknessStyle style class name of item
	 * @param value thickness
	 * @param checkMark icon
	 */
	public LineThicknessCheckMarkItem(String itemText, String thicknessStyle, int value,
			IconSpec checkMark) {
		addStyleName("lineThicknessItem");
		addStyleName(thicknessStyle);
		this.value = value;

		styleAndAddCheckMark(checkMark);

		Label text = BaseWidgetFactory.INSTANCE.newPrimaryText(itemText);
		this.text = text;
		add(text);
	}

	private void buildGUI(String thicknessStyle, IconSpec checkMark) {
		styleAndAddCheckMark(checkMark);

		SimplePanel linePreview = new SimplePanel();
		linePreview.addStyleName("linePreview");
		linePreview.addStyleName(thicknessStyle);
		add(linePreview);
	}

	private void styleAndAddCheckMark(IconSpec checkMark) {
		checkImg = checkMark.toElement();
		checkImg.addClassName("checkImg");
		getElement().insertFirst(checkImg);
	}

	/**
	 * @return true if checkmark is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            true if checkmark visible
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateCheckMarkStyle();
	}

	/**
	 * @return thickness value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * show or hide checkmark depending on its status (selected or not selected)
	 */
	public void updateCheckMarkStyle() {
		if (isSelected()) {
			checkImg.addClassName("selected");
		} else {
			checkImg.removeClassName("selected");
		}
	}

	/**
	 * update translation of text check mark item
	 * @param translation - new translation
	 */
	public void setLabel(String translation) {
		if (text != null) {
			text.setText(translation);
		}
	}
}