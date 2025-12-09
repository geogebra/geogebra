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

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.web.full.cas.view.CASViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Top level GUI for the CAS view
 *
 */
public class CASDockPanelW extends NavigableDockPanelW {

	private CASViewW casView;

	/**
	 * @param appl
	 *            application
	 */
	public CASDockPanelW(AppWFull appl) {
		super(App.VIEW_CAS, getDefaultToolbar(), true);
		app = appl;
	}

	/**
	 * @return CAS
	 */
	public CASViewW getCAS() {
		return casView;
	}

	/**
	 * @return application
	 */
	public App getApp() {
		return app;
	}

	private static String getDefaultToolbar() {
		return CASView.TOOLBAR_DEFINITION;
	}

	@Override
	protected Widget loadStyleBar() {
		return ((CASViewW) app.getGuiManager().getCasView())
				.getCASStyleBar();
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_cas();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return ((CASViewW) app.getGuiManager().getCasView())
				.getEditor();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_CASView();
	}

	@Override
	protected Panel getViewPanel() {
		casView = (CASViewW) app.getGuiManager().getCasView();
		if (!app.supportsView(App.VIEW_CAS)) {
			return new FlowPanel();
		}
		casView.maybeOpenKeyboard(true);
		return casView.getComponent();
	}

	@Override
	public void onResize() {
		if (casView == null) {
			return;
		}
		boolean oldFocus = ((CASTableCellEditor) casView.getEditor())
				.hasFocus();
		super.onResize();
		if (oldFocus) {
			casView.getEditor().setFocus(true);
		}

	}
}
