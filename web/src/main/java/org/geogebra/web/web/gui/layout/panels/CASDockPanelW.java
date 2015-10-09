package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASViewW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.app.VerticalPanelSmart;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Top level GUI for the CAS view
 *
 */
public class CASDockPanelW extends NavigableDockPanelW {

	SimpleLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	CASViewW sview;


	public CASDockPanelW(App appl) {
		super(App.VIEW_CAS, // view id
				"CAS", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				4, // menu order
				'K' // ctrl-shift-K
		);

		// initWidget(toplevel = new SimpleLayoutPanel());
		// ancestor = new VerticalPanelSmart();
		// toplevel.add(ancestor);

		app = (AppW) appl;
	}

	public CASViewW getCAS() {
		return sview;
	}

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

	public MathKeyboardListener getKeyboardListener() {
		return ((CASViewW) ((GuiManagerW) app.getGuiManager()).getCasView())
				.getEditor();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		// TODO Auto-generated method stub
		return getResources().styleBar_CASView();
	}

	@Override
	protected Panel getViewPanel() {
		sview = (CASViewW) app.getGuiManager().getCasView();
		if (!app.supportsView(App.VIEW_CAS)) {
			return new FlowPanel();
		}
		sview.maybeOpenKeyboard(true);
		return sview.getComponent();
	}
}
