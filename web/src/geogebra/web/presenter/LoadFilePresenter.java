package geogebra.web.presenter;

import geogebra.web.Web;
import geogebra.web.css.GuiResources;
import geogebra.web.helper.FileLoadCallback;
import geogebra.web.helper.JavaScriptInjector;
import geogebra.web.helper.UrlFetcher;
import geogebra.web.html5.View;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;

public class LoadFilePresenter extends BasePresenter {
	
	private final UrlFetcher urlFetcher;
	

	public LoadFilePresenter(UrlFetcher urlFetcher) {
		this.urlFetcher = urlFetcher;
	}
	
	public void onPageLoad() {
		
		View view = getView();
		String filename;
		String base64String;
		
		if (!"".equals((base64String = view.getDataParamBase64String()))) {
			process(base64String);
		} else if (!"".equals((filename = view.getDataParamFileName()))) {
			fetch(filename);
		} else {
			view.promptUserForGgbFile();
		}
		
		Application app = view.getApplication();
		
		//app.setUseBrowserForJavaScript(useBrowserForJavaScript);
		//app.setRightClickEnabled(enableRightClick);
		//app.setChooserPopupsEnabled(enableChooserPopups);
		//app.setErrorDialogsActive(errorDialogsActive);
		//if (customToolBar != null && customToolBar.length() > 0 && showToolBar)
		//	app.getGuiManager().setToolBarDefinition(customToolBar);
		//app.setMaxIconSize(maxIconSize);

		boolean showToolBar = view.getDataParamShowToolBar();
		boolean showMenuBar = view.getDataParamShowMenuBar();
		//app.setShowMenuBar(showMenuBar);
		//app.setShowAlgebraInput(view.getDataParamShowAlgebraInput(), true);
		//app.setShowToolBar(showToolBar, view.getDataParamShowToolBarHelp());	
		
		
		// TODO boolean undoActive = (showToolBar || showMenuBar);
		boolean undoActive = true;

		app.setUndoActive(undoActive);			

		String language = view.getDataParamLanguage();
		if (language != null) {
			String country = view.getDataParamCountry();
			if (country == null) {
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

	public boolean isGgbFileParameterSpecified() {
		return urlFetcher.isGgbFileParameterSpecified();
	}

	private void process(String dataParamBase64String) {
			getView().processBase64String(dataParamBase64String);
	}
	

	public void onWorksheetConstructionFailed(String errorMessage) {
		getView().showError(errorMessage);
	}
	
	public void onWorksheetReady() {
		getView().hide();
	}
	
	//Reverse MVP	
	public void fetchGgbFileFromUserInput(String userUrl) {
		fetch(urlFetcher.getAbsoluteGgbFileUrl(userUrl));
	}
		
	// Private Methods
	private void fetch(String fileName) {
		getView().showLoadAnimation();
		String url = fileName.startsWith("http") ? fileName : GWT.getModuleBaseURL()+"../"+fileName;
		getView().processFileName(url);
	}
	
}
