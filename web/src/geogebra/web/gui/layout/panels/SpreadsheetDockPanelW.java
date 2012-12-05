package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.app.SpreadsheetStyleBarPanel;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetView;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class SpreadsheetDockPanelW extends ResizeComposite /*DockPanelW*/ {

	App application = null;

	SimplePanel toplevel;

	VerticalPanelSmart ancestor;
	SpreadsheetStyleBarPanel sstylebar;
	SpreadsheetView sview;

	public SpreadsheetDockPanelW() {
		super();
		initWidget(toplevel = new SimplePanel());
		ancestor = new VerticalPanelSmart();
		ancestor.add(sstylebar = new SpreadsheetStyleBarPanel());
		toplevel.add(ancestor);
	}

	public void onResize() {
		//App.debug("Resized");
		if (application != null) {

			if (sview != null) {
				// If this is resized, we may know its width and height
				int width = this.getOffsetWidth();//this is 400, OK
				int height = this.getOffsetHeight() -
					(((SpreadsheetView)application.getGuiManager().getSpreadsheetView()).
				getSpreadsheetStyleBar()).getOffsetHeight();

				sview.getScrollPanel().setWidth(width+"px");
				sview.getScrollPanel().setHeight(height+"px");

				sview.getFocusPanel().setWidth(width+"px");
				sview.getFocusPanel().setHeight(height+"px");
			}
		}
    }

	public void attachApp(App app) {
	   this.application = app;
	   sstylebar.attachApp(app);
	   onResize();
	}

	public SpreadsheetView getSpreadsheet() {
		return sview;
	}

	public void showSpreadsheetView(boolean show) {

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
