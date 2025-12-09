/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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