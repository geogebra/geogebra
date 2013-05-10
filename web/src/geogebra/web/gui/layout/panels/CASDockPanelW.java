package geogebra.web.gui.layout.panels;

import geogebra.common.cas.view.CASView;
import geogebra.common.main.App;
import geogebra.web.cas.view.CASViewW;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class CASDockPanelW extends DockPanelW {

	App application = null;

	SimpleLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	CASViewW sview;

	public CASDockPanelW(App app) {
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
		
		application = app;
	}

	protected Widget loadComponent() {
		sview = (CASViewW) ((AppW)application).getGuiManager().getCasView();	
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
			
			if(width <0 || height < 0){
				return;
			}
			
			
			sview.getComponent().setWidth(width+"px");
			sview.getComponent().setHeight(height+"px");			
			//TODO: Focus panel
		}
		
    }

	public void attachApp(App app) {
	   super.attachApp(app);
	}

	public CASViewW getCAS() {
		return sview;
	}

	public void showView(boolean show) {

		if (application == null) return;

		// imperfect yet
		if (show && sview == null) {
			sview = (CASViewW) ((AppW)application).getGuiManager().getCasView();			
			//((MyTableW)sview.getConsoleTable()).setRepaintAll();
			ancestor.add(sview.getComponent());
			application.getGuiManager().attachCasView();
			//((MyTableW)sview.getConsoleTable()).repaint();
			onResize();
		} else if (!show && sview != null) {
			ancestor.remove(sview.getComponent());
			sview = null;
			onResize();
		}
	}
	
	public App getApp() {
	    return application;
    }

	private static String getDefaultToolbar() {
		return CASView.TOOLBAR_DEFINITION;		
	}
}
