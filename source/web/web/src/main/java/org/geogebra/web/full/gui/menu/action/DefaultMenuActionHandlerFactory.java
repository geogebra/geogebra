/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.DownloadColladaDaeAction;
import org.geogebra.web.full.gui.menubar.action.DownloadColladaHtmlAction;
import org.geogebra.web.full.gui.menubar.action.DownloadDefaultFormatAction;
import org.geogebra.web.full.gui.menubar.action.DownloadPdfAction;
import org.geogebra.web.full.gui.menubar.action.DownloadPngAction;
import org.geogebra.web.full.gui.menubar.action.DownloadStlAction;
import org.geogebra.web.full.gui.menubar.action.DownloadSvgAction;
import org.geogebra.web.full.gui.menubar.action.ExportImage;
import org.geogebra.web.full.gui.menubar.action.OpenProfilePage;
import org.geogebra.web.full.gui.menubar.action.ReportProblemAction;
import org.geogebra.web.full.gui.menubar.action.SaveAction;
import org.geogebra.web.full.gui.menubar.action.SaveLocalAction;
import org.geogebra.web.full.gui.menubar.action.ShareAction;
import org.geogebra.web.full.gui.menubar.action.ShowForumAction;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.full.gui.menubar.action.ShowPrintPreviewAction;
import org.geogebra.web.full.gui.menubar.action.ShowPrivacyPolicyAction;
import org.geogebra.web.full.gui.menubar.action.ShowSearchView;
import org.geogebra.web.full.gui.menubar.action.ShowSettingsAction;
import org.geogebra.web.full.gui.menubar.action.ShowTutorialsAction;
import org.geogebra.web.full.gui.menubar.action.SignInAction;
import org.geogebra.web.full.gui.menubar.action.SignOutAction;
import org.geogebra.web.full.gui.menubar.action.StartExamAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances.
 */
public class DefaultMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private AppWFull app;

	/**
	 * @param app app
	 */
	public DefaultMenuActionHandlerFactory(AppWFull app) {
		this.app = app;
	}

	@Override
	public DefaultMenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = new DefaultMenuActionHandler(app);
		actionHandler.setMenuAction(Action.CLEAR_CONSTRUCTION,
				new ClearAllAction(app.enableFileFeatures()));
		actionHandler.setMenuAction(Action.SHOW_SEARCH_VIEW, new ShowSearchView());
		actionHandler.setMenuAction(Action.SHOW_LICENSE, new ShowLicenseAction());
		actionHandler.setMenuAction(Action.SHOW_PRIVACY_POLICY, new ShowPrivacyPolicyAction());
		actionHandler.setMenuAction(Action.SHOW_SETTINGS, new ShowSettingsAction());
		actionHandler.setMenuAction(Action.SAVE_FILE, new SaveAction());
		actionHandler.setMenuAction(Action.SAVE_FILE_LOCAL, new SaveLocalAction());
		actionHandler.setMenuAction(Action.SHARE_FILE, new ShareAction());
		actionHandler.setMenuAction(Action.SHOW_TUTORIALS, new ShowTutorialsAction());
		actionHandler.setMenuAction(Action.SHOW_FORUM, new ShowForumAction());
		actionHandler.setMenuAction(Action.REPORT_PROBLEM, new ReportProblemAction());
		actionHandler.setMenuAction(Action.SIGN_IN, new SignInAction());
		actionHandler.setMenuAction(Action.SIGN_OUT, new SignOutAction());
		actionHandler.setMenuAction(Action.OPEN_PROFILE_PAGE, new OpenProfilePage());
		actionHandler.setMenuAction(Action.EXPORT_IMAGE, new ExportImage());
		actionHandler.setMenuAction(Action.DOWNLOAD_GGB, new DownloadDefaultFormatAction());
		actionHandler.setMenuAction(Action.DOWNLOAD_GGS, new DownloadDefaultFormatAction());
		actionHandler.setMenuAction(Action.DOWNLOAD_PNG, new DownloadPngAction(app));
		actionHandler.setMenuAction(Action.DOWNLOAD_SVG, new DownloadSvgAction(app));
		actionHandler.setMenuAction(Action.DOWNLOAD_PDF, new DownloadPdfAction(app));
		actionHandler.setMenuAction(Action.DOWNLOAD_STL, new DownloadStlAction());
		actionHandler.setMenuAction(Action.DOWNLOAD_COLLADA_DAE, new DownloadColladaDaeAction());
		actionHandler.setMenuAction(Action.DOWNLOAD_COLLADA_HTML, new DownloadColladaHtmlAction());
		actionHandler.setMenuAction(Action.PREVIEW_PRINT, new ShowPrintPreviewAction());
		actionHandler.setMenuAction(Action.START_EXAM_MODE, new StartExamAction());
		return actionHandler;
	}
}
