package org.geogebra.web.full.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.full.gui.dialog.image.ImageInputDialog;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.full.gui.dialog.image.UploadImageWithoutDialog;
import org.geogebra.web.full.gui.dialog.image.WebcamInputDialog;
import org.geogebra.web.full.gui.openfileview.OpenFileView;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

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
			final Image icon = new Image(
					BrowseResources.INSTANCE.location_local());
			final Element span = DOM.createElement("span");
			span.setAttribute("style",
					"position: absolute; top: 0px; left: 0px; "
							+ "width: 50px; height: 50px; padding: 10px;  overflow: hidden;");
			span.appendChild(icon.getElement());
			Element form = DOM.createElement("form");
			input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute("style",
					"width: 500px; height: 60px; font-size: 56px;"
							+ "opacity: 0; position: absolute;"
							+ "right: 0px; top: 0px; cursor: pointer;");
			form.appendChild(input);
			span.appendChild(form);

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
			addGgbChangeHandler(input, of);
		}

		private native void addGgbChangeHandler(Element el,
				BrowseViewI bg) /*-{
			var dialog = this;

			el.onchange = function(event) {
				var files = this.files;
				if (files.length) {
					var fileToHandle = files[0];
					bg.@org.geogebra.web.html5.gui.view.browser.BrowseViewI::openFile(Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle);
				}
				el.parentElement.reset();
			};
		}-*/;

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
		 * @return input element
		 */
		public Element getInput() {
			return input;
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
	public UploadImageDialog getImageInputDialog(AppW app) {

		return new ImageInputDialog(app);
	}

	/**
	 * @param app
	 *            application
	 * @return WebcamInputDialog webcam input dialog
	 */
	public WebcamInputDialog getWebcamInputDialog(AppW app) {
		return new WebcamInputDialog(app);
	}

	/**
	 * @param app
	 *            application
	 * @return image panel controller
	 */
	public UploadImageWithoutDialog getUploadImageWithoutDialog(AppW app) {
		return new UploadImageWithoutDialog(app);
	}

	@Override
	public BrowseViewI createBrowseView(AppW app) {
		if (app.isMebis()) {
			FileOpenButton mb = new FileOpenButton("containedButton");
			OpenFileView of = new OpenFileView(app, mb);
			mb.setOpenFileView(of);
			return of;
		}
		FileOpenButton mb = new FileOpenButton();
		BrowseGUI bg = new BrowseGUI(app, mb);
		mb.setOpenFileView(bg);
		return bg;
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
			Window.moveTo(0, 0);
			Window.resizeTo(width, height);
		} else {
			Window.resizeTo(width0, height0);
		}

	}
}
