package geogebra.web.gui.layout.panels;

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

	public CASDockPanelW() {
		super(0, null, null, true, 0);
		initWidget(toplevel = new SimpleLayoutPanel());
		ancestor = new VerticalPanelSmart();
		toplevel.add(ancestor);
	}

	protected Widget loadComponent() {
		return toplevel;
	}

	

	public void onResize() {
		super.onResize();
		
    }

	public void attachApp(App app) {
	   this.application = app;
	   onResize();
	}

	public CASViewW getCAS() {
		return sview;
	}

	public void showCASView(boolean show) {

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
}
