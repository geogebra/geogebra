package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.app.SpreadsheetStyleBarPanel;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
	
	protected SpreadsheetDockPanelW dockPanel;

	public SpreadsheetDockPanelW(App app) {
		super(
				App.VIEW_SPREADSHEET, 		// view id
				"Spreadsheet", 						// view title phrase
				null,				// toolbar string
				true,								// style bar?
				3, 									// menu order
				'S'									// menu shortcut
			);
		
		//initWidget(toplevel = new SimpleLayoutPanel());
		//ancestor = new VerticalPanelSmart();
		//ancestor.add(sstylebar = new SpreadsheetStyleBarPanel());
		//toplevel.add(ancestor);
		
		application = app;
		this.dockPanel = this;
	}

	protected Widget loadComponent() {
		
		sview = ((AppW)application).getGuiManager().getSpreadsheetView();
					
		return sview;
	}

	protected Widget loadStyleBar() {
		if (sstylebar == null) {
			sstylebar = new SpreadsheetStyleBarPanel();
		}
		return sstylebar;
	}

	public void onResize() {
		super.onResize();
		// App.debug("Resized");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {

				if (application != null) {

					if (sview != null) {
						// If this is resized, we may know its width and height
						/*
						 * int width = this.getOffsetWidth();//this is 400, OK
						 * int height = this.getOffsetHeight();
						 * 
						 * if (application.getGuiManager().hasSpreadsheetView())
						 * height -=
						 * (((SpreadsheetViewW)application.getGuiManager().
						 * getSpreadsheetView
						 * ()).getSpreadsheetStyleBar()).getOffsetHeight();
						 */

						int width = dockPanel.getComponentInteriorWidth();
						int height = dockPanel.getComponentInteriorHeight();

						if (width < 0 || height < 0) {
							return;
						}

						sview.getScrollPanel().setWidth(width + "px");
						sview.getScrollPanel().setHeight(height + "px");

						int width2 = ((MyTableW) sview.getSpreadsheetTable())
						        .getOffsetWidth();
						int height2 = ((MyTableW) sview.getSpreadsheetTable())
						        .getOffsetWidth();

						sview.getFocusPanel().setWidth(width2 + "px");
						sview.getFocusPanel().setHeight(height2 + "px");

						// ((MyTableW)sview.getSpreadsheetTable()).setRepaintAll();
						// sview.repaint();
					}
				}

			}
		});

	}

	public void attachApp(App app) {
		super.attachApp(app);
		this.application = app;
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

	public App getApp() {
	    return application;
    }
}
