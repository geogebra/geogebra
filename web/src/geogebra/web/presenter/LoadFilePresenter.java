package geogebra.web.presenter;

import geogebra.web.helper.FileLoadCallback;
import geogebra.web.helper.UrlFetcher;
import geogebra.web.jso.JsUint8Array;

public class LoadFilePresenter extends BasePresenter {
	
	private final UrlFetcher urlFetcher;
	

	public LoadFilePresenter(UrlFetcher urlFetcher) {
		this.urlFetcher = urlFetcher;
	}
	
	public void onPageLoad() {
		if (getView().getDataParamFileName() != "") {
			fetch(getView().getDataParamFileName());
		} else if (urlFetcher.isGgbFileParameterSpecified()) {
			fetch(urlFetcher.getAbsoluteGgbFileUrlFromParameter());
		} else {
			getView().promptUserForGgbFile();
		}
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
