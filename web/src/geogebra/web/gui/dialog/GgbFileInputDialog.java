package geogebra.web.gui.dialog;

import geogebra.common.kernel.geos.GeoPoint;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;

public class GgbFileInputDialog extends FileInputDialog{

	public GgbFileInputDialog(AppW app, GeoPoint location) {
	    super(app, location);
	    createGUI();
    }
	
	protected void createGUI(){
		super.createGUI();
//		inputWidget.getElement().setPropertyString("accept", "application/zip");
//		inputWidget.getElement().setPropertyString("accept", "application/octet-stream");
		addGgbChangeHandler(inputWidget.getElement(), app);
	}
	
	public native void addGgbChangeHandler(Element el, AppW appl) /*-{
		var dialog = this;
		el.onchange = function(event) {
			var files = this.files;
			if (files.length) {
				var fileToHandle = files[0];
				var ggbRegEx = /\.(ggb|ggt)$/i;
				if (fileToHandle.name.toLowerCase().match(ggbRegEx)) {
					var reader = new FileReader();
					reader.onloadend = function(ev) {
						if (reader.readyState === reader.DONE) {
							var fileStr = reader.result;
							appl.@geogebra.web.main.AppW::loadGgbFileAgain(Ljava/lang/String;)(fileStr);
							dialog.@geogebra.web.gui.dialog.FileInputDialog::hideAndFocus()();
						}
					};
					reader.readAsDataURL(fileToHandle);
				} else {
					//TODO: not ggb/ggt selected
				}
			}

		};
	}-*/;

	
}
