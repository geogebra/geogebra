package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetStyleBarW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class SpreadsheetDockPanelW extends DockPanelW {

	SpreadsheetStyleBarW sstylebar;
	SpreadsheetViewW sview;
	AbsolutePanel wrapview;

	public SpreadsheetDockPanelW(App appl) {
		super(
				App.VIEW_SPREADSHEET, 		// view id
				"Spreadsheet", 						// view title phrase
				getDefaultToolbar(),				// toolbar string
				true,								// style bar?
				3, 									// menu order
				'S'									// menu shortcut
			);
		
		app = (AppW)appl;
	}

	protected Widget loadComponent() {
		if (wrapview == null) {
			wrapview = new AbsolutePanel();
			sview = app.getGuiManager().getSpreadsheetView();
			wrapview.add(sview);
		}
		return wrapview;
	}

	@Override
    protected Widget loadStyleBar() {
		if (sstylebar == null) {
			sstylebar = sview.getSpreadsheetStyleBar();
		}
		return sstylebar;
	}

	public void onResize() {
		super.onResize();

		// TODO: onResize should not be deferred (as in the case of Graphics views)!

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {

				if (app != null) {

					if (sview != null) {

						int width2 = ((MyTableW) sview.getSpreadsheetTable())
						        .getOffsetWidth();
						int height2 = ((MyTableW) sview.getSpreadsheetTable())
						        .getOffsetHeight();

						sview.getFocusPanel().setWidth(width2 + "px");
						sview.getFocusPanel().setHeight(height2 + "px");


						int width = getComponentInteriorWidth();
						int height = getComponentInteriorHeight();

						if (width < 0 || height < 0) {
							return;
						}

						wrapview.setPixelSize(width, height);

						if (app.getSettings().getSpreadsheet().showHScrollBar())
							sview.getScrollPanel().setHeight(height + "px");
						else // scrollbar's height usually doesn't exceed 20px
							sview.getScrollPanel().setHeight((height + 20) + "px");

						if (app.getSettings().getSpreadsheet().showVScrollBar())
							sview.getScrollPanel().setWidth(width + "px");
						else // scrollbar's width usually doesn't exceed 20px
							sview.getScrollPanel().setWidth((width + 20) + "px");
					}
				}

			}
		});

	}

	public SpreadsheetViewW getSpreadsheet() {
		return sview;
	}

	public void showView(boolean show) {
		App.debug("unimplemented method");
	}

	public App getApp() {
	    return app;
    }

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_MOVE);
	//	sb.append(" ");		
	//	sb.append(EuclidianConstants.MODE_RECORD_TO_SPREADSHEET);
		
	//	sb.append(" || ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_PROBABILITY_CALCULATOR);
		
	//	sb.append(" || ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE);
		
	//	sb.append(" || ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_SUM);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_AVERAGE);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_COUNT);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_MAX);
	//	sb.append(" , ");
	//	sb.append(EuclidianConstants.MODE_SPREADSHEET_MIN);
		

		return sb.toString();
	}

	@Override 
	public boolean isStyleBarVisible() {

		if (!app.isApplet()) {
			return super.isStyleBarVisible();
		}

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet(); 

		// currently no GUI / XML for hiding the style-bar
		// hide in applets if the row/column headers are missing
		return super.isStyleBarVisible() && settings.showRowHeader() && settings.showColumnHeader();
	}

	@Override
	public boolean hasStyleBar() {
		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();

		if (settings == null)
			return super.hasStyleBar();

		return super.hasStyleBar() && settings.showRowHeader() && settings.showColumnHeader();
	}
}
