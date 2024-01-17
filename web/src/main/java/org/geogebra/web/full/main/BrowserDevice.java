package org.geogebra.web.full.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;

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
	public static class FileOpenButton extends FlowPanel {
		private Element input;
		private Element div;

		/**
		 * New button
		 */
		public FileOpenButton() {
			super();
			this.setStyleName("button");
			final Image icon = new Image(BrowseResources.INSTANCE.location_local());
			final Element span = DOM.createElement("span");
			span.setAttribute("style",
					"position: absolute; top: 0px; left: 0px; "
							+ "width: 50px; height: 50px; padding: 10px;  overflow: hidden;");
			span.appendChild(icon.getElement());
			input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute("style",
					"width: 500px; height: 60px; font-size: 56px;"
							+ "opacity: 0; position: absolute;"
							+ "right: 0px; top: 0px; cursor: pointer;");
			span.appendChild(input);

			DOM.insertChild(getElement(), span, 0);
		}

		/**
		 * New Button
		 * 
		 * @param style
		 *            style class of the button
		 */
		public FileOpenButton(String style) {
			super();
			this.setStyleName(style);
			div = DOM.createElement("div");
		}

		/**
		 * @param of
		 *            open file view
		 */
		public void setOpenFileView(BrowseViewI of) {
			HTMLInputElement fileInput = Js.uncheckedCast(input);

			fileInput.addEventListener("change", (event) -> {
				if (fileInput.files.length > 0) {
					File fileToHandle = fileInput.files.getAt(0);
					of.openFile(fileToHandle);
					fileInput.value = "";
				}
			});
		}

		/**
		 * @param imgUrl
		 *            icon url
		 * @param text
		 *            button text
		 */
		public void setImageAndText(String imgUrl, String text) {
			Element form = DOM.createElement("form");
			input = DOM.createElement("input");
			input.setAttribute("type", "file");
			form.appendChild(input);

			div.setInnerHTML(" <img src=\"" + imgUrl
					+ "\"  class=\"gwt-Image\" draggable=\"false\" role=\"button\"> "
					+ "<div class=\"gwt-Label\"/>" + text + "</div>");
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
