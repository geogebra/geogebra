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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListModelEvent;
import org.geogebra.web.full.gui.advanced.client.ui.widget.ComboBox;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.DropDownPosition;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.KeyCodes;

/**
 * Combo box
 */
public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	private static final int DEFAULT_VISIBLE_ROWS = 10;
	private static final String DEFAULT_WIDTH = "90px";

	/**
	 * @param app
	 *            application
	 */
	public ComboBoxW(AppW app) {
		super(app);

		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		setWidth(DEFAULT_WIDTH);
		
		this.prepareChoiceButton();
		this.setChoiceButtonVisible(true);
		
		addCloseHandler(event -> onValueChange(getValue()));

		addKeyDownHandler(event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				onValueChange(getValue());
			}
		});

		final AutoCompleteTextFieldW tf = getSelectedValue();
		tf.addStyleName("AutoCompleteTextFieldW");
		tf.enableGGBKeyboard();
		tf.addEnterPressHandler(() -> onValueChange(tf.getText()));

		tf.addInsertHandler(this::onValueChange);
	}

	@Override
	protected void select(ListModelEvent event) {
		if (isListPanelOpened()) {
			return;
		}
		super.select(event);
	}
	
	/**
	 * @param value
	 *            changed value
	 */
	protected abstract void onValueChange(String value);

	/**
	 * @param item
	 *            new item
	 */
	public void addItem(String item) {
		getModel().add(item, item);
	}

	/**
	 * @param id
	 *            item description
	 * @param item
	 *            item (used for color)
	 */
	public void addItem(String id, GeoElement item) {
		getModel().add(id, item);
	}
}