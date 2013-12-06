package geogebra.web.presenter;

import geogebra.common.main.App;
import geogebra.common.main.GeoGebraPreferences;
import geogebra.common.util.debug.Log;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.View;
import geogebra.web.WebStatic;
import geogebra.web.WebStatic.GuiToLoad;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class LoadFilePresenter{
	

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
		String fileId;
		String filename;
		
		final AppWeb app = view.getApplication();
		if(WebStatic.urlToOpen != null ){
			getView().showLoadAnimation();
			getView().processFileName(WebStatic.urlToOpen);
			WebStatic.urlToOpen = null;
		}
		else if (isReloadDataInStorage()){
			//do nothing here - everything done in isReloadDataInStorage() function 
		} 
		else if (!"".equals((base64String = view.getDataParamBase64String()))) {
			process(base64String);
		} 
		else if (!"".equals((filename = view.getDataParamFileName()))) { 
			fetch(filename);
		}
		else {
			//we dont have content, it is an app
			Log.debug("no base64content, possibly App loaded?");

			// code moved here from AppWapplication.afterCoreObjectsInited - start

			app.getGuiManager().getLayout().setPerspectives(app.getTmpPerspectives());

			if (app instanceof AppW) {
				// default layout doesn't have a Graphics View 2
				((AppW)app).getEuclidianViewpanel().deferredOnResize();
			}

			// code moved here from AppWapplication.afterCoreObjectsInited - end

			app.appSplashCanNowHide();
			
			Storage stockStore = null;
			
			stockStore = Storage.getLocalStorageIfSupported();
			if(stockStore != null){
				String xml = stockStore.getItem(GeoGebraPreferences.XML_USER_PREFERENCES);
				if (xml != null) app.setXML(xml, false);
				String xmlDef = stockStore.getItem(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES);
				//String xmlDef = ggbPrefs.get(XML_DEFAULT_OBJECT_PREFERENCES, factoryDefaultXml);
				//if (!xmlDef.equals(factoryDefaultXml)) {
					boolean eda = app.getKernel().getElementDefaultAllowed();
					app.getKernel().setElementDefaultAllowed(true);
					if (xmlDef != null) app.setXML(xmlDef, false);
					app.getKernel().setElementDefaultAllowed(eda);
				//}
			}
		}
			
		//app.setUseBrowserForJavaScript(useBrowserForJavaScript);
		//app.setRightClickEnabled(enableRightClick);
		//app.setChooserPopupsEnabled(enableChooserPopups);
		//app.setErrorDialogsActive(errorDialogsActive);
		//if (customToolBar != null && customToolBar.length() > 0 && showToolBar)
		//	app.getGuiManager().setToolBarDefinition(customToolBar);
		//app.setMaxIconSize(maxIconSize);

		boolean showToolBar = view.getDataParamShowToolBar();
		boolean showMenuBar = view.getDataParamShowMenuBar() || App.isFullAppGui();
		app.setShowMenuBar(showMenuBar);
		app.setShowAlgebraInput(view.getDataParamShowAlgebraInput(), false);
		app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp());
		app.getKernel().setShowAnimationButton(view.getDataParamShowAnimationButton());
		app.setCapturingThreshold(view.getDataParamCapturingThreshold());
		
		
		boolean undoActive = (showToolBar || showMenuBar || WebStatic.currentGUI.equals(GuiToLoad.APP));

		app.setUndoActive(undoActive);			

		String language = view.getDataParamLanguage();
		if(language == null || "".equals(language)){
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
		app.setShiftDragZoomEnabled(view.getDataParamShiftDragZoomEnabled());
		app.setShowResetIcon(view.getDataParamShowResetIcon());
		app.setAllowSymbolTables(view.getDataParamAllowSymbolTable());		
	}
	
	private boolean isReloadDataInStorage(){
		Storage stockStore = Storage.getLocalStorageIfSupported();
		
		if (stockStore == null) return false;
		String base64String = stockStore.getItem("reloadBase64String");
		if ((base64String==null) || (base64String.length()==0)) return false;
		process(base64String);
		stockStore.removeItem("reloadBase64String");
		return true;
	}

	/**
	 * @param dataParamBase64String a base64 string
	 */
	public void process(String dataParamBase64String) {
			getView().processBase64String(dataParamBase64String);
	}
	

	public void onWorksheetConstructionFailed(String errorMessage) {
		getView().showError(errorMessage);
	}
	
	public void onWorksheetReady() {
		getView().hide();
	}
	private void fetch(String fileName) { 
		getView().showLoadAnimation(); 
		String url = fileName.startsWith("http") ? fileName : GWT.getModuleBaseURL()+"../"+fileName; 
		getView().processFileName(url); 
	} 
	
	public AppWeb getApplication() {
		return getView().getApplication();
	}
	
}
