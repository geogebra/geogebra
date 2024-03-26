package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FileUpload;

import elemental2.dom.File;
import elemental2.dom.FileList;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

/**
 * Factory for image upload buttons
 */
public final class UploadImagePanel {

	/**
	 * @param callback
	 *            Upload callback
	 * @return upload button
	 */
	public static FileUpload getUploadButton(AppW app, UploadImageCallback callback) {
		FileUpload upload = new FileUpload();
		Element el = upload.getElement();
		el.setAttribute("accept", "image/*");
		HTMLInputElement input = Js.uncheckedCast(el);
		app.getAppletFrame().add(upload); // needs to be in DOM to work for iPad Chrome
		input.addEventListener("change", event -> {
			if (upload.getParent() == app.getAppletFrame()) {
				upload.removeFromParent();
			}
			LoadSpinner spinner = new LoadSpinner();
			app.getAppletFrame().add(spinner);
			File fileToHandle = null;
			FileList files = input.files;
			if (files.length > 0) {
				for (int i = 0, j = files.length; i < j; ++i) {
					if (files.item(i).type.startsWith("image")) {
						fileToHandle = files.item(i);
						break;
					}
				}
			}
			if (fileToHandle != null) {
				FileReader reader = new FileReader();
				String fileName = fileToHandle.name;
				reader.addEventListener("loadend", (ev) -> {
					Log.debug("Image " + fileName + " loaded");
					if (reader.readyState == FileReader.DONE) {
						String fileStr = reader.result.asString();
						callback.insertImage(fileName, fileStr);
						spinner.removeFromParent();
					}
				});
				reader.addEventListener("error", evt -> spinner.removeFromParent());
				reader.readAsDataURL(fileToHandle);
			}
		});
		return upload;
	}

}