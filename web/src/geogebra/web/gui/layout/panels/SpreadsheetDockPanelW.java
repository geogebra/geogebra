package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.app.SpreadsheetStyleBarPanel;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class SpreadsheetDockPanelW extends DockPanelW {

	App application = null;

	SimpleLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	SpreadsheetStyleBarPanel sstylebar;
	SpreadsheetViewW sview;

	public SpreadsheetDockPanelW() {
		super(0, null, null, true, 0);
		initWidget(toplevel = new SimpleLayoutPanel());
		ancestor = new VerticalPanelSmart();
		ancestor.add(sstylebar = new SpreadsheetStyleBarPanel());
		toplevel.add(ancestor);
	}

	protected Widget loadComponent() {
		return toplevel;
	}

	protected Widget loadStyleBar() {
		return sstylebar;
	}

	public void onResize() {
		super.onResize();
		//App.debug("Resized");
		if (application != null) {

			if (sview != null) {
				// If this is resized, we may know its width and height
				int width = this.getOffsetWidth();//this is 400, OK
				int height = this.getOffsetHeight();

				if (application.getGuiManager().hasSpreadsheetView())
					height -= (((SpreadsheetViewW)application.getGuiManager().
						getSpreadsheetView()).getSpreadsheetStyleBar()).getOffsetHeight();

				sview.getScrollPanel().setWidth(width+"px");
				sview.getScrollPanel().setHeight(height+"px");

				int width2 = ((MyTableW)sview.getSpreadsheetTable()).getOffsetWidth();
				int height2 = ((MyTableW)sview.getSpreadsheetTable()).getOffsetWidth();

				sview.getFocusPanel().setWidth(width2+"px");
				sview.getFocusPanel().setHeight(height2+"px");
			}
		}
    }

	public void attachApp(App app) {
	   this.application = app;
	   sstylebar.attachApp(app);
	   onResize();
	}

	public SpreadsheetViewW getSpreadsheet() {
		return sview;
	}

	public void showView(boolean show) {

		if (application == null) return;

		// imperfect yet
		if (show && sview == null) {
			sview = ((AppW)application).getGuiManager().getSpreadsheetView();
			((MyTableW)sview.getSpreadsheetTable()).setRepaintAll();
			ancestor.add(sview);
			((MyTableW)sview.getSpreadsheetTable()).repaint();
			onResize();
		} else if (!show && sview != null) {
			ancestor.remove(sview);
			sview = null;
			onResize();
		}
	}
}
