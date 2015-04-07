package org.geogebra.web.web.presenter;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.View;
import org.geogebra.web.web.jso.JsUint8Array;

import com.google.gwt.dom.client.Element;

public abstract class BasePresenter {

	private View view;

	public View getView() {
		return view;
	}

	public void setView(View view) {
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
