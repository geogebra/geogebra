package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.DownloadColladaDaeAction;
import org.geogebra.web.full.gui.menubar.action.DownloadColladaHtmlAction;
import org.geogebra.web.full.gui.menubar.action.DownloadDefaultFormatAction;
import org.geogebra.web.full.gui.menubar.action.DownloadPdfAction;
import org.geogebra.web.full.gui.menubar.action.DownloadPngAction;
import org.geogebra.web.full.gui.menubar.action.DownloadStlAction;
import org.geogebra.web.full.gui.menubar.action.DownloadSvgAction;
import org.geogebra.web.full.gui.menubar.action.ReportProblemAction;
import org.geogebra.web.full.gui.menubar.action.SaveAction;
import org.geogebra.web.full.gui.menubar.action.ShareAction;
import org.geogebra.web.full.gui.menubar.action.ShowForumAction;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.full.gui.menubar.action.ShowPrintPreviewAction;
import org.geogebra.web.full.gui.menubar.action.ShowSettingsAction;
import org.geogebra.web.full.gui.menubar.action.ShowTutorialsAction;
import org.geogebra.web.full.gui.menubar.action.SignInAction;
import org.geogebra.web.full.gui.menubar.action.SignOutAction;
import org.geogebra.web.full.gui.menubar.action.StartAppAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances.
 */
public class BaseMenuActionHandlerFactory implements MenuActionHandlerFactory {

    private AppWFull app;

    /**
     * @param app app
     */
    public BaseMenuActionHandlerFactory(AppWFull app) {
        this.app = app;
    }

    @Override
    public MenuActionHandler create() {
        DefaultMenuActionHandler actionProvider = new DefaultMenuActionHandler(app);
        actionProvider.setMenuAction(Action.CLEAR_CONSTRUCTION, new ClearAllAction(true));
        actionProvider.setMenuAction(Action.EXIT_EXAM_MODE, new ExitExamAction());
        actionProvider.setMenuAction(Action.SHOW_LICENSE, new ShowLicenseAction());
        actionProvider.setMenuAction(Action.SHOW_SETTINGS, new ShowSettingsAction());
        actionProvider.setMenuAction(Action.SAVE_FILE, new SaveAction());
        actionProvider.setMenuAction(Action.SHARE_FILE, new ShareAction());
        actionProvider.setMenuAction(Action.SHOW_TUTORIALS, new ShowTutorialsAction());
        actionProvider.setMenuAction(Action.SHOW_FORUM, new ShowForumAction());
        actionProvider.setMenuAction(Action.REPORT_PROBLEM, new ReportProblemAction());
        actionProvider.setMenuAction(Action.SIGN_IN, new SignInAction());
        actionProvider.setMenuAction(Action.SIGN_OUT, new SignOutAction());
        actionProvider.setMenuAction(Action.DOWNLOAD_GGB, new DownloadDefaultFormatAction());
        actionProvider.setMenuAction(Action.DOWNLOAD_PNG, new DownloadPngAction(app));
        actionProvider.setMenuAction(Action.DOWNLOAD_SVG, new DownloadSvgAction(app));
        actionProvider.setMenuAction(Action.DOWNLOAD_PDF, new DownloadPdfAction(app));
        actionProvider.setMenuAction(Action.DOWNLOAD_STL, new DownloadStlAction());
        actionProvider.setMenuAction(Action.DOWNLOAD_COLLADA_DAE, new DownloadColladaDaeAction());
        actionProvider.setMenuAction(Action.DOWNLOAD_COLLADA_HTML, new DownloadColladaHtmlAction());
        actionProvider.setMenuAction(Action.PREVIEW_PRINT, new ShowPrintPreviewAction());
        actionProvider.setMenuAction(Action.START_GRAPHING, StartAppAction.create(app, "graphing"));
        actionProvider.setMenuAction(Action.START_GEOMETRY, StartAppAction.create(app, "geometry"));
        actionProvider.setMenuAction(Action.START_GRAPHING_3D, StartAppAction.create(app, "3d"));
        actionProvider.setMenuAction(Action.START_CAS_CALCULATOR, StartAppAction.create(app, "calculator"));
        actionProvider.setMenuAction(Action.START_CLASSIC, StartAppAction.create(app, "classic"));
        actionProvider.setMenuAction(Action.START_CAS_CALCULATOR, StartAppAction.create(app, "cas"));
        return actionProvider;
    }
}
