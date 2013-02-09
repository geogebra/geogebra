package geogebra.web.gui.dialog;

import geogebra.common.kernel.geos.GeoPoint;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;

public class ImageFileInputDialog extends FileInputDialog{

	public ImageFileInputDialog(AppW app, GeoPoint location) {
	    super(app, location);
	    createGUI();
    }

	protected void createGUI() {
		super.createGUI();
		addGgbChangeHandler(inputWidget.getElement(), app);
	}

	public native void addGgbChangeHandler(Element el, AppW appl) /*-{
		var dialog = this;
		el.setAttribute("accept", "image/*");
		el.onchange = function(event) {
			var files = this.files;
			if (files.length) {
				var fileTypes = /^image.*$/;
				for (var i = 0, j = files.length; i < j; ++i) {
					if (!files[i].type.match(fileTypes)) {
						continue;
					}
					var fileToHandle = files[i];
					appl.@geogebra.web.main.AppW::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, dialog.@geogebra.web.gui.dialog.FileInputDialog::getNativeHideAndFocus()());				
					break
				}
			}
		};
	}-*/;

	public void onClick(ClickEvent event) {
	    if (event.getSource() == btCancel) {
	    	hideAndFocus();
	    }
    }
}
