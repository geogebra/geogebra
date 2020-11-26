package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.web.full.cas.view.CASViewW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

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
	public CASDockPanelW(App appl) {
		super(App.VIEW_CAS, // view id
				"CAS", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				4, // menu order
				'K' // ctrl-shift-K
		);

		app = (AppW) appl;
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
		return ((CASViewW) ((GuiManagerW) app.getGuiManager()).getCasView())
				.getCASStyleBar();
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_cas();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return ((CASViewW) ((GuiManagerW) app.getGuiManager()).getCasView())
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
