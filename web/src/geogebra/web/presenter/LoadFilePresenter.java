package geogebra.web.presenter;

import geogebra.common.main.GeoGebraPreferences;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.html5.View;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class LoadFilePresenter extends BasePresenter {
	

	public LoadFilePresenter() {
		
	}
	
	public void onPageLoad() {
		
		View view = getView();
		String filename;
		String base64String;
		String fileId;
		
		AppW app = view.getApplication();
		
		if (!"".equals((base64String = view.getDataParamBase64String()))) {
			process(base64String);
		} else if (!"".equals((filename = view.getDataParamFileName()))) {
			fetch(filename);
		} else if (!"".equals((fileId = getGoogleFileId()))) {
			 MyGoogleApis.getFileFromGoogleDrive(fileId,this);
		} else {
			//we dont have content, it is an app
			AppW.console("no base64content, possibly App loaded?");
			app.appSplashCanNowHide();
			
			Storage stockStore = null;
			stockStore = Storage.getLocalStorageIfSupported();
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
		
		
		
		//app.setUseBrowserForJavaScript(useBrowserForJavaScript);
		//app.setRightClickEnabled(enableRightClick);
		//app.setChooserPopupsEnabled(enableChooserPopups);
		//app.setErrorDialogsActive(errorDialogsActive);
		//if (customToolBar != null && customToolBar.length() > 0 && showToolBar)
		//	app.getGuiManager().setToolBarDefinition(customToolBar);
		//app.setMaxIconSize(maxIconSize);

		boolean showToolBar = view.getDataParamShowToolBar();
		boolean showMenuBar = view.getDataParamShowMenuBar();
		app.setShowMenuBar(showMenuBar);
		app.setShowAlgebraInput(view.getDataParamShowAlgebraInput(), false);
		app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp());
		app.getKernel().setShowAnimationButton(view.getDataParamShowAnimationButton());
		
		
		boolean undoActive = (showToolBar || showMenuBar || Web.currentGUI.equals(GuiToLoad.APP));

		app.setUndoActive(undoActive);			

		String language = view.getDataParamLanguage();
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
		app.setShiftDragZoomEnabled(view.getDataParamShiftDragZoomEnabled());
		app.setShowResetIcon(view.getDataParamShowResetIcon());
		
	}

	private native String getGoogleFileId() /*-{
	    if ($wnd.GGW_appengine && $wnd.GGW_appengine.FILE_IDS[0] !== "") {
	    	return $wnd.GGW_appengine.FILE_IDS[0];
	    }
	    return "";
    }-*/;

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
	// Private Methods
	private void fetch(String fileName) {
		getView().showLoadAnimation();
		String url = fileName.startsWith("http") ? fileName : GWT.getModuleBaseURL()+"../"+fileName;
		getView().processFileName(url);
	}
	
	public AppW getApplication() {
		return getView().getApplication();
	}
	
}
