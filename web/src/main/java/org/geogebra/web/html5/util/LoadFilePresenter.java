package org.geogebra.web.html5.util;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

/**
 * File loader for Web
 */
public class LoadFilePresenter {
	// NB this needs to be adjusted in app-release if we change it here
	private static final int MIN_SIZE_FOR_PICKER = 650;

	/**
	 * Create new file loader
	 */
	public LoadFilePresenter() {

	}

	private ViewW mview;

	/**
	 * @return Article element wrapper
	 */
	public ViewW getView() {
		return mview;
	}

	/**
	 * @param view
	 *            article element wrapper
	 */
	public void setView(ViewW view) {
		this.mview = view;
	}

	/**
	 * Run applet for current view
	 */
	public void onPageLoad() {

		ViewW view = getView();
		String base64String;
		String filename;
		view.adjustScale();
		final AppW app = view.getApplication();
		boolean fileOpened = true;
		boolean specialPerspective = false;
		app.setAllowSymbolTables(view.getDataParamAllowSymbolTable());
		app.setErrorDialogsActive(view.getDataParamErrorDialogsActive());
		if (isReloadDataInStorage()) {
			// do nothing here - everything done in isReloadDataInStorage()
			// function
		} else if (!"".equals((filename = view.getDataParamJSON()))) {
			processJSON(filename);
		} else if (!"".equals((base64String = view.getDataParamBase64String()))) {
			process(base64String);
		} else if (!"".equals((filename = view.getDataParamFileName()))) {
			fetch(filename);
		} else if (!"".equals((filename = view.getDataParamTubeID()))) {
			app.openMaterial(view.getDataParamTubeID(), new Runnable() {

				@Override
				public void run() {
					openEmptyApp(app);

				}
			});
		} else {
			fileOpened = false;
			specialPerspective = openEmptyApp(app);
		}

		// app.setChooserPopupsEnabled(enableChooserPopups);
		// app.setErrorDialogsActive(errorDialogsActive);
		// if (customToolBar != null && customToolBar.length() > 0 &&
		// showToolBar)
		// app.getGuiManager().setToolBarDefinition(customToolBar);
		// app.setMaxIconSize(maxIconSize);
		boolean fullApp = !app.isApplet();
		boolean showToolBar = view.getDataParamShowToolBar(fullApp);
		boolean showMenuBar = view.getDataParamShowMenuBar(fullApp);
		boolean showAlgebraInput = view.getDataParamShowAlgebraInput(fullApp);

		app.setShowMenuBar(showMenuBar);
		app.setShowAlgebraInput(showAlgebraInput, false);
		app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp());
		app.getKernel().setShowAnimationButton(
		        view.getDataParamShowAnimationButton());
		app.setCapturingThreshold(view.getDataParamCapturingThreshold());

		boolean undoActive = (showToolBar || showMenuBar
		        || view.getDataParamApp() || app.getScriptManager()
						.getStoreUndoListeners().size() > 0)
				&& view.getDataParamEnableUndoRedo();

		app.setUndoActive(undoActive);

		String language = view.getDataParamLanguage();
		if (language == null || "".equals(language)) {
			language = app.getLanguageFromCookie();
		}

		if (language != null) {
			String country = view.getDataParamCountry();
			if (country == null || "".equals(country)) {
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
		if (!specialPerspective) {
			app.updateToolBar();
		}
		if (!fileOpened) {
			GeoGebraProfiler.getInstance().profileEnd();
			app.getScriptManager().ggbOnInit();
		}
	}

	/**
	 * Open app without file / base64
	 * 
	 * @param app
	 *            application
	 * @return whether special perspective (search / customize) was used
	 */
	boolean openEmptyApp(final AppW app) {
		// we dont have content, it is an app
		Log.debug("no base64content, possibly App loaded?");

		// code moved here from AppWapplication.afterCoreObjectsInited - start
		String perspective = mview.getDataParamPerspective();
		if (perspective.length() == 0) {
			perspective = Location.getParameter("GeoGebraPerspective");
		}
		if (perspective == null) {
			perspective = "";
		}
		if (app.getGuiManager() != null) {
			if (perspective.startsWith("search:")) {
				app.setCloseBrowserCallback(new Runnable() {

					@Override
					public void run() {
						finishEmptyLoading(app, null);

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
				finishEmptyLoading(app, PerspectiveDecoder.decode(perspective,
				        app.getKernel().getParser(),
						ToolBar.getAllToolsNoMacros(true, app.isExam(), app)));
				return false;

			}
		}

		finishEmptyLoading(app, null);
		return false;

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
		app.setPreferredSize(
				new GDimensionW((int) app.getWidth(), (int) app.getHeight()));
		app.loadPreferences(p);
		// default layout doesn't have a Graphics View 2
		app.getEuclidianViewpanel().deferredOnResize();

		app.appSplashCanNowHide();
		// TODO this should probably go to default XML
		app.getSettings().getAlgebra().setTreeMode(SortMode.ORDER);

		app.updateToolBar();
		app.focusLost(null, null);
		app.setUndoActive(true);
		if (p != null) {
			if (!app.getArticleElement().getDataParamShowAppsPicker()) {
				app.showStartTooltip(p.getDefaultID());
			}
			app.setActivePerspective(p.getDefaultID() - 1);
		}

		// no Feature.ADJUST_VIEWS: returns false.
		app.adjustViews(false);
		boolean smallScreen = Window.getClientWidth() < MIN_SIZE_FOR_PICKER
				|| Window.getClientHeight() < MIN_SIZE_FOR_PICKER;
		if (app.getArticleElement().getDataParamShowAppsPicker()
				&& app.getExam() == null && !smallScreen) {
			app.showPerspectivesPopup();
			}

		app.updateRounding();

	}



	private boolean isReloadDataInStorage() {
		if (!Browser.supportsSessionStorage()) {
			return false;
		}
		Storage stockStore = Storage.getLocalStorageIfSupported();

		if (stockStore == null) {
			return false;
		}
		String base64String = stockStore.getItem("reloadBase64String");
		if ((base64String == null) || (base64String.length() == 0)) {
			return false;
		}
		process(base64String);
		stockStore.removeItem("reloadBase64String");
		return true;
	}

	/**
	 * @param dataParamBase64String
	 *            a base64 string
	 */
	public void process(String dataParamBase64String) {
		getView().processBase64String(dataParamBase64String);
	}

	/**
	 * @param json
	 *            JSON encoded ZIP file (zip.js)
	 */
	public void processJSON(final String json) {
		Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				getView().processJSON(json);
			}
		};

		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	private void fetch(String fileName) {
		getView().processFileName(fileName);
	}

}
