package geogebra.web.gui.layout.panels;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetStyleBarW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.AppW;

import com.google.gwt.resources.client.ImageResource;
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

	@Override
	protected Widget loadComponent() {
		setViewImage(GuiResources.INSTANCE.styleBar_spreadsheetView());
		if (wrapview == null) {
			wrapview = new AbsolutePanel();
			wrapview.addStyleName("SpreadsheetWrapView");
			sview = app.getGuiManager().getSpreadsheetView();
			wrapview.add(sview.getFocusPanel());
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

	@Override
	public void onResize() {
		super.onResize();

		if (app != null) {

			if (sview != null) {

				int width = getComponentInteriorWidth();
				int height = getComponentInteriorHeight();

				if (width < 0 || height < 0) {
					return;
				}

				wrapview.setPixelSize(width, height);

				sview.onResize();

			}


		}
	}

	public SpreadsheetViewW getSpreadsheet() {
		return sview;
	}

	@Override
	public void showView(boolean show) {
		App.debug("unimplemented method");
	}

	public App getApp() {
		return app;
	}

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_MOVE);
		if (GeoGebraConstants.IS_PRE_RELEASE) {				
			sb.append(" || ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
			sb.append(" , ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
			sb.append(" , ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);
			sb.append(" , ");
		}

		sb.append(EuclidianConstants.MODE_PROBABILITY_CALCULATOR);

		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE);

		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_SUM);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_AVERAGE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_COUNT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MAX);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MIN);


		return sb.toString();
	}

	@Override 
	public boolean isStyleBarVisible() {
		if (app.isApplet()) {
			return false;
		}
		return super.isStyleBarVisible();
	}

	@Override
	public boolean hasStyleBar() {
		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();

		if (settings == null)
			return super.hasStyleBar();

		return super.hasStyleBar() && settings.showRowHeader() && settings.showColumnHeader();
	}

	@Override
	public ImageResource getIcon() {
		return AppResources.INSTANCE.view_spreadsheet24();
	}
}
