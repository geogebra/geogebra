package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASViewW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.app.VerticalPanelSmart;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Top level GUI for the CAS view
 *
 */
public class CASDockPanelW extends DockPanelW {

	SimpleLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	CASViewW sview;

	public CASDockPanelW(App appl) {
		super(
				App.VIEW_CAS, 	// view id
				"CAS", 					// view title phrase 
				getDefaultToolbar(),	// toolbar string
				true,					// style bar?
				4,						// menu order
				'K' // ctrl-shift-K
			);
		
		
		//initWidget(toplevel = new SimpleLayoutPanel());
		//ancestor = new VerticalPanelSmart();
		//toplevel.add(ancestor);
		
		app = (AppW)appl;
	}

	protected Widget loadComponent() {
		setViewImage(getResources().styleBar_CASView());
		sview = (CASViewW) app.getGuiManager().getCasView();	
		return sview.getComponent();
	}

	

	public void onResize() {
		super.onResize();
		if (sview != null) {
			/*
			// If this is resized, we may know its width and height
			int width = this.getOffsetWidth();//this is 400, OK
			int height = this.getOffsetHeight();

			if (application.getGuiManager().hasSpreadsheetView())
				height -= (((CASViewW)application.getGuiManager().
					getSpreadsheetView()).getCASStyleBar()).getOffsetHeight();
			 */

			int width = this.getComponentInteriorWidth();
			int height = this.getComponentInteriorHeight();

			// <= is needed because otherwise the width/height would
			// be set to 0 (as getComponentInteriorWidth not being ready)
			// so the style bar would be made invisible
			if (width <= 0 || height <= 0) {
				return;
			}

			sview.getComponent().setWidth(width+"px");
			sview.getComponent().setHeight(height+"px");			
			//TODO: Focus panel
		}
    }

	public CASViewW getCAS() {
		return sview;
	}

	public void showView(boolean show) {

		if (app == null) return;

		// imperfect yet
		if (show && sview == null) {
			sview = (CASViewW) app.getGuiManager().getCasView();			
			//((MyTableW)sview.getConsoleTable()).setRepaintAll();
			ancestor.add(sview.getComponent());
			app.getGuiManager().attachCasView();
			//((MyTableW)sview.getConsoleTable()).repaint();
			onResize();
		} else if (!show && sview != null) {
			ancestor.remove(sview.getComponent());
			sview = null;
			onResize();
		}
	}
	
	public App getApp() {
	    return app;
    }

	private static String getDefaultToolbar() {
		return CASView.TOOLBAR_DEFINITION;		
	}
	
	@Override
	protected Widget loadStyleBar() {
		return ((CASViewW)((GuiManagerW)app.getGuiManager()).getCasView()).getCASStyleBar();
	}
	
	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_cas();
	}
}
