package org.geogebra.web.html5.util;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window.Location;

public class LoadFilePresenter {

	public LoadFilePresenter() {

	}

	private View view;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void onPageLoad() {

		View view = getView();
		String base64String;
		String filename;
		view.adjustScale();
		final AppW app = view.getApplication();
		boolean fileOpened = true;
		boolean specialPerspective = false;
		app.setAllowSymbolTables(view.getDataParamAllowSymbolTable());
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

		// app.setUseBrowserForJavaScript(useBrowserForJavaScript);
		// app.setRightClickEnabled(enableRightClick);
		// app.setChooserPopupsEnabled(enableChooserPopups);
		// app.setErrorDialogsActive(errorDialogsActive);
		// if (customToolBar != null && customToolBar.length() > 0 &&
		// showToolBar)
		// app.getGuiManager().setToolBarDefinition(customToolBar);
		// app.setMaxIconSize(maxIconSize);

		boolean showToolBar = view.getDataParamShowToolBar(App.isFullAppGui());
		boolean showMenuBar = view.getDataParamShowMenuBar(App.isFullAppGui());
		boolean showAlgebraInput = view.getDataParamShowAlgebraInput(App
		        .isFullAppGui());

		app.setShowMenuBar(showMenuBar);
		app.setShowAlgebraInput(showAlgebraInput, false);
		app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp());
		app.getKernel().setShowAnimationButton(
		        view.getDataParamShowAnimationButton());
		app.setCapturingThreshold(view.getDataParamCapturingThreshold());

		boolean undoActive = (showToolBar || showMenuBar
		        || view.getDataParamApp() || app.getScriptManager()
		        .getStoreUndoListeners().size() > 0);

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

	private boolean openEmptyApp(final AppW app) {
		// we dont have content, it is an app
		Log.debug("no base64content, possibly App loaded?");

		// code moved here from AppWapplication.afterCoreObjectsInited - start
		String perspective = view.getDataParamPerspective();
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
				        ToolBar.getAllToolsNoMacros(true, app.isExam())));
				return false;

			}
		}

		finishEmptyLoading(app, null);
		return false;

	}

	private void finishEmptyLoading(AppW app, Perspective p) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout()
		        .setPerspectives(app.getTmpPerspectives(), p);
		}
		// default layout doesn't have a Graphics View 2
		app.getEuclidianViewpanel().deferredOnResize();

		// code moved here from AppWapplication.afterCoreObjectsInited - end

		app.appSplashCanNowHide();

		Storage stockStore = null;

		stockStore = Storage.getLocalStorageIfSupported();
		if (stockStore != null) {
			String xml = stockStore
			        .getItem(GeoGebraPreferences.XML_USER_PREFERENCES);
			if (xml != null)
				app.setXML(xml, false);
			String xmlDef = stockStore
			        .getItem(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES);
			// String xmlDef = ggbPrefs.get(XML_DEFAULT_OBJECT_PREFERENCES,
			// factoryDefaultXml);
			// if (!xmlDef.equals(factoryDefaultXml)) {
			boolean eda = app.getKernel().getElementDefaultAllowed();
			app.getKernel().setElementDefaultAllowed(true);
			if (xmlDef != null)
				app.setXML(xmlDef, false);
			app.getKernel().setElementDefaultAllowed(eda);
			// }
		}
		app.updateToolBar();
		app.focusLost(null, null);
		app.showStartTooltip();
	}

	private boolean isReloadDataInStorage() {
		if (!Browser.supportsSessionStorage()) {
			return false;
		}
		Storage stockStore = Storage.getLocalStorageIfSupported();

		if (stockStore == null)
			return false;
		String base64String = stockStore.getItem("reloadBase64String");
		if ((base64String == null) || (base64String.length() == 0))
			return false;
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

	public void processJSON(final String dataParamBase64String) {
		Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
			public void execute() {
				getView().processJSON(dataParamBase64String);
			}
		};

		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	public void onWorksheetConstructionFailed(String errorMessage) {
		getView().showError(errorMessage);
	}

	public void onWorksheetReady() {
		getView().hide();
	}

	private void fetch(String fileName) {
		getView().showLoadAnimation();
		getView().processFileName(fileName);
	}

	public AppW getApplication() {
		return getView().getApplication();
	}

}
