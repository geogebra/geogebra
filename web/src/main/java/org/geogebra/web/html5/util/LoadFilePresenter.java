package org.geogebra.web.html5.util;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.settings.StyleSettings;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

/**
 * File loader for Web
 */
public class LoadFilePresenter {

	/**
	 * Run applet for current view
	 * 
	 * @param view
	 *            applet parameters
	 * @param app
	 *            app
	 * @param vv
	 *            zip loader
	 */
	public void onPageLoad(final AppletParameters view, final AppW app,
			ViewW vv) {

		String base64String;
		String filename;
		app.checkScaleContainer();
		boolean fileOpened = true;
		app.setAllowSymbolTables(view.getDataParamAllowSymbolTable());
		app.setErrorDialogsActive(view.getDataParamErrorDialogsActive());

		if (!"".equals(filename = view.getDataParamJSON())) {
			processJSON(filename, vv);
		} else if (!""
				.equals(base64String = view.getDataParamBase64String())) {
			process(base64String, vv);
		} else if (!"".equals(filename = view.getDataParamFileName())) {
			vv.processFileName(filename);
		} else if (!"".equals(view.getDataParamTubeID())) {
			app.openMaterial(view.getDataParamTubeID(),
					err -> {
						openEmptyApp(app, view);
						ToolTipManagerW.sharedInstance()
								.showBottomMessage(app.getLocalization()
										.getError(err), false, app);
					});
		} else {
			fileOpened = false;
		}

		boolean fullApp = !app.isApplet();
		boolean showToolBar = view.getDataParamShowToolBar(fullApp);
		boolean showMenuBar = view.getDataParamShowMenuBar(fullApp);
		boolean showAlgebraInput = view.getDataParamShowAlgebraInput(fullApp);
		boolean isApp = view.getDataParamApp();

		app.setShowMenuBar(showMenuBar);
		app.setShowAlgebraInput(showAlgebraInput, false);
		app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp(true));
		app.getKernel().setShowAnimationButton(
		        view.getDataParamShowAnimationButton());
		app.setCapturingThreshold(view.getDataParamCapturingThreshold());
		if (!isApp) {
			app.getAppletFrame().addStyleName("appletStyle");
		}

		boolean undoActive = (showToolBar || showMenuBar
		        || view.getDataParamApp() || app.getScriptManager()
						.getStoreUndoListeners().size() > 0)
				&& view.getDataParamEnableUndoRedo();

		String language = view.getDataParamLanguage();
		if (StringUtil.empty(language)) {
			language = app.getLanguageFromCookie();

			if (!StringUtil.empty(language) && app.getLAF() != null) {
				app.getLAF().storeLanguage(language);
			}
		}

		if (language != null) {
			String country = view.getDataParamCountry();
			if (StringUtil.empty(country)) {
				app.setLanguage(language);
			} else {
				app.setLanguage(language, country);
			}
		}
		app.setUseBrowserForJavaScript(view.getDataParamUseBrowserForJS());
		app.setLabelDragsEnabled(view.getDataParamEnableLabelDrags());
		app.setUndoRedoEnabled(view.getDataParamEnableUndoRedo());
		app.setRightClickEnabled(view.getDataParamEnableRightClick());
		app.setShiftDragZoomEnabled(view.getDataParamShiftDragZoomEnabled()
		        || view.getDataParamApp());
		app.setShowResetIcon(view.getDataParamShowResetIcon());
		app.setAllowStyleBar(view.getDataParamAllowStyleBar());

		StyleSettings styleSettings = app.getSettings().getStyle();
		styleSettings.setButtonShadows(view.getDataParamButtonShadows());
		styleSettings.setButtonRounding(view.getDataParamButtonRounding());
		styleSettings.setButtonBorderColor(view.getDataParamButtonBorderColor());

		if (!fileOpened) {
			if (!openEmptyApp(app, view)) {
				app.updateToolBar();
			}
			// only do this after app initialized
			app.setUndoActive(undoActive);

			app.getAsyncManager().scheduleCallback(new Runnable() {
				@Override
				public void run() {
					app.getScriptManager().ggbOnInit();
				}
			});
		} else {
			// only do this after app initialized
			app.setUndoActive(undoActive);
		}
	}

	/**
	 * Open app without file / base64
	 * 
	 * @param app
	 *            application
	 * @param ae
	 *            article element
	 * @return whether special perspective (search / customize) was used
	 */
	boolean openEmptyApp(final AppW app, AppletParameters ae) {
		// we dont have content, it is an app
		Log.debug("no base64content, App loaded");

		// code moved here from AppWapplication.afterCoreObjectsInited - start
		String perspective = ae.getDataParamPerspective();

		if (!perspective.startsWith("search:")) {
			perspective = defaultPerspective(app, perspective);
		}
		if (perspective.length() == 0) {
			// Location param may be null
			perspective = Location.getParameter("GeoGebraPerspective");
			if (perspective == null) {
				perspective = "";
			}
		}
		if (app.getGuiManager() != null) {
			if (perspective.startsWith("search:")) {
				app.setCloseBrowserCallback(new Runnable() {

					@Override
					public void run() {
						deferredOpenEmpty(app);
					}
				});
				app.openSearch(perspective.substring("search:".length()));
				return true;

			} else if (perspective.startsWith("customize:")) {
				app.setCloseBrowserCallback(new Runnable() {

					@Override
					public void run() {
						finishEmptyLoading(app, null);
					}
				});
				app.showCustomizeToolbarGUI();
				return true;
			} else {
				Perspective pd = getPerspective(app, perspective);
				finishEmptyLoading(app, pd);

				return false;
			}
		}

		finishEmptyLoading(app, null);
		return false;

	}

	/**
	 * Init app after open screen was closed
	 * 
	 * @param app
	 *            application
	 */
	protected void deferredOpenEmpty(AppW app) {
		String perspective = defaultPerspective(app, "");
		finishEmptyLoading(app, !StringUtil.empty(perspective)
				? getPerspective(app, perspective) : null);
	}

	private static String defaultPerspective(AppW app, String userPerspective) {
		if (app.getConfig().getForcedPerspective() != null) {
			return app.getConfig().getForcedPerspective();
		}
		return userPerspective;
	}

	private static Perspective getPerspective(AppW app, String perspective) {
		Perspective pd = PerspectiveDecoder.decode(perspective,
				app.getKernel().getParser(),
				ToolBar.getAllToolsNoMacros(true, app.isExam(), app));
		if ("1".equals(perspective) || "2".equals(perspective)
				|| "5".equals(perspective)) {

			if (app.isPortrait()) {
				int height = app.getAppletParameters().getDataParamHeight();
				if (app.getAppletParameters().getDataParamFitToScreen()) {
					height = Window.getClientHeight();
				}
				if (height > 0) {
					double ratio = PerspectiveDecoder.portraitRatio(height,
							app.isUnbundledGraphing() || app.isUnbundled3D());
					pd.getSplitPaneData()[0].setDivider(ratio);
				}

			} else {
				int width = app.getAppletParameters().getDataParamWidth();
				if (app.getAppletParameters().getDataParamFitToScreen()) {
					width = Window.getClientWidth();
				}
				if (width > 0) {
					double ratio = PerspectiveDecoder.landscapeRatio(app,
							width);
					pd.getSplitPaneData()[0].setDivider(ratio);
				}
			}

		}
		return pd;
	}

	/**
	 * FInish loading when no base64 / filename enetered
	 * 
	 * @param app
	 *            application
	 * @param p
	 *            perspective
	 */
	void finishEmptyLoading(AppW app, Perspective p) {
		if (p != null) {
			app.setActivePerspective(p.getDefaultID() - 1);
		}
		app.getAppletFrame().updateHeaderSize();
		app.setPreferredSize(
				new Dimension(app.getAppletWidth(), app.getAppletHeight()));
		app.ensureStandardView();
		app.loadPreferences(p);
		app.setFileVersion(GeoGebraConstants.VERSION_STRING, "auto");

		// default layout doesn't have a Graphics View 2
		app.getEuclidianViewpanel().deferredOnResize();

		app.appSplashCanNowHide();

		app.updateToolBar();
		app.setUndoActive(true);
		if (p != null) {
			app.setActivePerspective(p.getDefaultID() - 1);
		}

		// no Feature.ADJUST_VIEWS: returns false.
		if (!app.isUnbundled() && app.isPortrait()) {
			app.adjustViews(false, false);
		}

		app.showPerspectivesPopupIfNeeded();

		app.updateRounding();
		preloadParser(app);
	}

	/**
	 * Make sure the parser is initiated: it will be needed for the first object
	 * creation and may cause a major delay (
	 * 
	 * @param app
	 *            application
	 */
	private static void preloadParser(final AppW app) {
		app.invokeLater(new Runnable() {
			@Override
			public void run() {
				app.getParserFunctions();
			}
		});
	}

	private static void process(String dataParamBase64String, ViewW view) {
		view.processBase64String(dataParamBase64String);
	}

	/**
	 * @param json
	 *            JSON encoded ZIP file (zip.js)
	 * @param view
	 *            zip handler
	 */
	public void processJSON(final String json, final ViewW view) {
		view.processJSON(json);
	}

}
