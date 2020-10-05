package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetStyleBarW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetViewW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class SpreadsheetDockPanelW extends NavigableDockPanelW {

	private SpreadsheetStyleBarW sstylebar;
	private SpreadsheetViewW sview;
	private AbsolutePanel wrapview;

	/**
	 * @param appl
	 *            application
	 */
	public SpreadsheetDockPanelW(App appl) {
		super(
				App.VIEW_SPREADSHEET, 		// view id
				"Spreadsheet", 						// view title phrase
				getDefaultToolbar(),				// toolbar string
				true,								// style bar?
				3, 									// menu order
				'S'									// menu shortcut
				);

		app = (AppW) appl;
	}

	@Override
	protected Panel getViewPanel() {
		if (wrapview == null) {
			wrapview = new AbsolutePanel();
			wrapview.addStyleName("SpreadsheetWrapView");
			sview = (SpreadsheetViewW) app.getGuiManager().getSpreadsheetView();
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

		if (app != null && sview != null) {
			int width = getComponentInteriorWidth();
			int height = getComponentInteriorHeight();

			if (width <= 0 || height <= 0) {
				return;
			}

			wrapview.setPixelSize(width, height);
			sview.onResize(width, height);
		}
	}

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_MOVE);
		
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);

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
		if (app.isApplet() && !app.showMenuBar() && !app.isStyleBarAllowed()) {
			return false;
		}
		return super.isStyleBarVisible();
	}

	@Override
	public boolean hasStyleBar() {
		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();

		if (settings == null) {
			return super.hasStyleBar();
		}

		return super.hasStyleBar() && settings.showRowHeader() && settings.showColumnHeader();
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_spreadsheet();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_spreadsheetView();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		MathKeyboardListener ml = this.sview.getSpreadsheetTable()
				.getEditor()
				.getTextfield();
		return ml;
	}

}
