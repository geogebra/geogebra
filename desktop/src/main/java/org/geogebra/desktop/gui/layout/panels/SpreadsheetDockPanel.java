package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the spreadsheet view.
 */
public class SpreadsheetDockPanel extends NavigableDockPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 */
	public SpreadsheetDockPanel(AppD app) {
		super(App.VIEW_SPREADSHEET, // view id
				"Spreadsheet", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				2, // menu order
				'S' // menu shortcut
		);

		setApp(app);
	}

	private GuiManagerD getGuiManager() {
		return (GuiManagerD) app.getGuiManager();
	}

	@Override
	protected JComponent loadStyleBar() {
		return getGuiManager().getSpreadsheetView().getSpreadsheetStyleBar();
	}

	@Override
	protected JComponent getViewPanel() {
		return getGuiManager().getSpreadsheetView().getViewContainer();
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
		sb.append(" , ");
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
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_SPREADSHEET);
	}

	@Override
	public boolean isStyleBarVisible() {

		if (!app.isApplet()) {
			return true;
		}

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();

		// currently no GUI / XML for hiding the style-bar
		// hide in applets if the row/column headers are missing
		return settings.showRowHeader() && settings.showColumnHeader();

	}

}
