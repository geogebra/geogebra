package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.user.client.Window;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens the bug report page.
 */
public class ReportProblemAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		String url =
				GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB
						+ "&lang="
						+ app.getLocalization().getLanguage();
		Window.open(url, "_blank", "");
	}
}
