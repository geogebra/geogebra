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

package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelValuePanel extends FlowPanel
		implements CloseHandler<GPopupPanel>, SetLabels {
	private final AppW appW;
	private final StringPropertyListFacade<?> nameProperty;
	private final List<GeoElement> geos;

	private ComponentInputField tfName;

	/**
	 * Constructor
	 * @param appW - application
	 * @param nameProperty - name property
	 */
	public LabelValuePanel(AppW appW, StringPropertyListFacade<?> nameProperty,
			List<GeoElement> geos) {
		super();
		this.appW = appW;
		this.nameProperty = nameProperty;
		this.geos = geos;

		createDialog();
	}

	private void createDialog() {
		tfName = new ComponentInputField(appW, null, nameProperty.getRawName(),
				null, nameProperty.getValue(), null, false);
		if (geos.size() == 1) {
			tfName.getTextField().getTextComponent().setAutoComplete(false);
			tfName.getTextField().getTextComponent().enableGGBKeyboard();

			tfName.getTextField().getTextComponent().addKeyHandler(e -> {
				if (e.isEnterKey()) {
					onEnter();
				}
			});
			add(tfName);
			init();
		}
		setLabels();
	}

	/**
	 * Submit the change
	 */
	protected void onEnter() {
		setNamePropertyValueOrThrowError();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		setNamePropertyValueOrThrowError();
	}

	@Override
	public void setLabels() {
		tfName.setLabels();
	}

	private void init() {
		tfName.setInputText(nameProperty.getValue());
		tfName.focusDeferred();
	}

	private void setNamePropertyValueOrThrowError() {
		if (tfName.getText().isBlank()) {
			appW.showError(MyError.Errors.InvalidInput);
		} else {
			nameProperty.setValue(tfName.getText());
		}
	}

}
