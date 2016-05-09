package org.geogebra.web.web.presenter;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.web.jso.JsUint8Array;

import com.google.gwt.dom.client.Element;

public abstract class BasePresenter {

	private ViewW view;

	public ViewW getView() {
		return view;
	}

	public void setView(ViewW view) {
		this.view = view;
	}

	public void onPageLoad() {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

	public void onCreateApplicationAndAddTo(Element element) {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

	public void onSyncCanvasSizeWithApplication() {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

	public void onFileContentLoaded(JsUint8Array zippedContent) {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

	public void onWorksheetConstructionFailed(String errorMessage) {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

	public void onWorksheetReady() {
		Log.debug("implementation needed"); // TODO Auto-generated

	}

}
