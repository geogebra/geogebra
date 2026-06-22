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

package org.geogebra.web.full.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.DomGlobal;
import elemental2.dom.File;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

/**
 * Device class for case we are running in the browser (eg Chrome app)
 */
public class BrowserDevice implements GDevice {

	/**
	 * Button for opening local files
	 *
	 */
	public static class FileOpenButton extends StandardButton {
		private final AppW app;
		private Element input;
		private final Element div;

		/**
		 * New Button
		 * @param app application
		 */
		public FileOpenButton(AppW app) {
			super();
			this.app = app;
			div = DOM.createDiv();
		}

		/**
		 * @param openFileView
		 *            open file view
		 */
		public void setOpenFileView(BrowseViewI openFileView) {
			HTMLInputElement fileInput = Js.uncheckedCast(input);

			fileInput.addEventListener("change", (event) -> {
				if (fileInput.files.length > 0) {
					File fileToHandle = fileInput.files.getAt(0);
					openFileView.close();
					app.getSaveController().showDialogIfNeeded((ignore) -> {
						app.openFile(fileToHandle);
					}, false);
					fileInput.value = "";
				}
			});
		}

		/**
		 * @param svgResource svg
		 * @param text button text
		 */
		public void setImageAndText(SVGResource svgResource, String text) {
			Element form = DOM.createElement("form");
			input = DOM.createElement("input");
			input.setAttribute("type", "file");
			form.appendChild(input);

			FlowPanel holder = new FlowPanel();
			holder.addStyleName("materialTonalButton");
			holder.add(new NoDragImage(svgResource.withFill(
					GeoGebraColorConstants.PURPLE_700.toString()), 24, 24));
			holder.add(new Label(text));
			div.appendChild(holder.getElement());
			div.appendChild(form);
			DOM.insertChild(getElement(), div, 0);
		}

		/**
		 * @param typeHint file extension including the dot or "image/*"
		 */
		public void setAcceptedFileType(String typeHint) {
			input.setAttribute("accept", typeHint);
		}
	}

	@Override
	public FileManager createFileManager(AppW app) {
		return new FileManagerW(app);
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView(AppW app) {
		return new ConstructionProtocolViewW(app);
	}

	@Override
	public void resizeView(int width0, int height0) {
		if (width0 > Browser.getScreenWidth()
				|| height0 > Browser.getScreenHeight()) {
			int width = Browser.getScreenWidth();
			int height = Browser.getScreenHeight();
			DomGlobal.window.moveTo(0, 0);
			DomGlobal.window.resizeTo(width, height);
		} else {
			DomGlobal.window.resizeTo(width0, height0);
		}

	}
}
