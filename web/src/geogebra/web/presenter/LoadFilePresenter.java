package geogebra.web.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dev.js.ast.JsArrayAccess;

import geogebra.web.helper.FileLoadCallback;
import geogebra.web.helper.UrlFetcher;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.util.DataUtil;

public class LoadFilePresenter extends BasePresenter {
	
	private final UrlFetcher urlFetcher;
	

	public LoadFilePresenter(UrlFetcher urlFetcher) {
		this.urlFetcher = urlFetcher;
	}
	
	public void onPageLoad() {
		if (!getView().getDataParamFileName().equals("")) {
			fetch(getView().getDataParamFileName());
		} else if (!getView().getDataParamBase64String().equals("")) {
			process(getView().getDataParamBase64String());
		} else if (urlFetcher.isGgbFileParameterSpecified()) {
			fetch(urlFetcher.getAbsoluteGgbFileUrlFromParameter());
		} else {
			getView().promptUserForGgbFile();
		}
	}

	private void process(String dataParamBase64String) {
		byte[] bytes = DataUtil.decode(dataParamBase64String);
		JsArrayInteger jsBytes = JsArrayInteger.createArray().cast();
		jsBytes.setLength(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			int x = bytes[i];
			if (x < 0) x += 256;
			
			jsBytes.set(i, x);
		}
	   getView().fileContentLoaded(jsBytes);
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
	
	public FileLoadCallback getFileLoadCallback() {
		return fileLoadCallback;
	}
		
	// Private Methods
	private void fetch(String absoluteUrl) {
		getView().showLoadAnimation(absoluteUrl);
		urlFetcher.fetchGgbFileFrom(absoluteUrl, fileLoadCallback);
	}
	
	private final FileLoadCallback fileLoadCallback = new FileLoadCallback() {
		public void onSuccess(JsUint8Array zippedContent) {
			getView().fileContentLoaded(zippedContent);
		}
		
		public void onError(String errorMessage) {
			getView().showError(errorMessage);
		}
	};
	
}
