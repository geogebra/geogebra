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

package org.geogebra.keyboard.web;

import java.util.ArrayList;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 *
 */
public class KeyPanelBase extends FlowPanel {

	private final TabbedKeyboard parent;
	private final ArrayList<FlowPanel> rows;
	private final ArrayList<BaseKeyboardButton> buttons;
	private Keyboard layout;

	/**
	 * @param keyboardLayout
	 *            {@link Keyboard}
	 */
	public KeyPanelBase(Keyboard keyboardLayout, TabbedKeyboard parent) {
		rows = new ArrayList<>();
		buttons = new ArrayList<>();
		this.parent = parent;
		this.layout = keyboardLayout;
	}

	/**
	 * adds the given button to the row with given index.
	 * 
	 * @param index
	 *            int
	 * @param button
	 *            {@link BaseKeyboardButton}
	 */
	public void addToRow(int index, BaseKeyboardButton button) {
		if (rows.size() <= index) {
			FlowPanel newRow = new FlowPanel();
			newRow.addStyleName("KeyPanelRow");
			rows.add(newRow);
			add(newRow);
		}
		rows.get(index).add(button);
		buttons.add(button);
	}

	private void reset() {
		clear();
		rows.clear();
		buttons.clear();
	}

	/**
	 * Make sure buttons are visible
	 */
	public void updatePanel() {
		if (buttons.isEmpty()) {
			updatePanel(layout);
		}
	}

	protected void updatePanel(Keyboard layout) {
		this.layout = layout;
		reset();
		int index = 0;
		for (Row row : layout.getModel().getRows()) {
			for (WeightedButton wb : row.getButtons()) {
				if (!Action.NONE.name().equals(wb.getPrimaryActionName())) {
					BaseKeyboardButton button = parent.makeButton(wb);
					addSecondary(button, wb);
					addToRow(index, button);
				}
			}
			index++;
		}
		updatePanelSize();
	}

	private static void addSecondary(BaseKeyboardButton btn,
			WeightedButton wb) {
		if (wb.getActionsSize() > 1) {
			btn.setSecondaryAction(wb.getActionName(1));
		}
	}

	/**
	 * This is much faster than updatePanel as it doesn't clear the model. It
	 * assumes the model and button layout are in sync.
	 */
	protected void updatePanelSize() {
		int buttonIndex = 0;
		int margins = 4;
		if (layout == null || buttons.isEmpty()) {
			return;
		}
		BaseKeyboardButton button = null;
		double weightSum = 6; // initial guess
		for (Row row : layout.getModel().getRows()) {
			weightSum = Math.max(row.getRowWeightSum(), weightSum);
		}
		int baseSize = parent.getBaseSize(weightSum);
		for (Row row : layout.getModel().getRows()) {
			double offset = 0;
			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getPrimaryActionName())) {
					offset = wb.getWeight();
				} else {
					button = buttons.get(buttonIndex);
					if (offset > 0) {
						button.getElement().getStyle().setMarginLeft(
								offset * baseSize + margins / 2d, Unit.PX);
					}
					button.getElement().getStyle().setWidth(
							wb.getWeight() * baseSize - margins, Unit.PX);
					offset = 0;
					buttonIndex++;
				}
			}
			if (Action.NONE.name().equals(row.getButtons()
					.get(row.getButtons().size() - 1).getPrimaryActionName())) {
				button.getElement().getStyle().setMarginRight(
						offset * baseSize + margins / 2d, Unit.PX);
			}
		}
	}
}
