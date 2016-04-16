package org.geogebra.web.web.presenter;

import org.geogebra.common.main.App;
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
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public void onCreateApplicationAndAddTo(Element element) {
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public void onSyncCanvasSizeWithApplication() {
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public void onFileContentLoaded(JsUint8Array zippedContent) {
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public void onWorksheetConstructionFailed(String errorMessage) {
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public void onWorksheetReady() {
		App.debug("implementation needed"); // TODO Auto-generated

	}

}
