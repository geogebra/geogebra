package org.geogebra.web.html5.gui;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import jsinterop.base.Js;

public class SplashDialog extends SimplePanel {

	boolean appLoaded = false;
	boolean timerEllapsed = false;
	boolean previewExists = false;
	private GeoGebraElement geoGebraElement;

	private Timer t = new Timer() {
		@Override
		public void run() {
			if (appLoaded) {
				hide();
			}
			timerEllapsed = true;
		}
	};

	private final GeoGebraFrameW geogebraFrame;

	/**
	 * @param showLogo
	 *            whether to show GeoGebra logo
	 * @param geoGebraElement
	 *            configuration element
	 * @param frame
	 *            frame
	 */
	public SplashDialog(boolean showLogo, GeoGebraElement geoGebraElement,
			AppletParameters parameters, GeoGebraFrameW frame) {
		this.geoGebraElement = geoGebraElement;
		this.geogebraFrame = frame;
		previewExists = checkIfPreviewExists(geoGebraElement)
				|| AppConfigDefault
						.isUnbundledOrNotes(parameters.getDataParamAppName());

		if (!previewExists) {
			FlowPanel panel = new FlowPanel();
			Style style = panel.getElement().getStyle();
			style.setPosition(Position.ABSOLUTE);
			style.setZIndex(1000000);
			style.setBackgroundColor("white");
			if (showLogo) {
				HTML logo = new HTML(GuiResourcesSimple.INSTANCE
				        .ggbSplashHtml().getText());
				panel.add(logo);
			}
			Image spinner = new NoDragImage(GuiResourcesSimple.INSTANCE
					.getGeoGebraWebSpinner().getSafeUri().asString());
			Style sstyle = spinner.getElement().getStyle();
			// position:absolute; margin-left:-8px; left:50%; bottom:5px;
			sstyle.setLeft(50, Unit.PCT);
			sstyle.setBottom(5, Unit.PX);
			sstyle.setPosition(Position.ABSOLUTE);
			sstyle.setMarginLeft(-8, Unit.PX);

			panel.add(spinner);
			Dom.addEventListener(spinner.getElement(), "load", evt -> triggerImageLoaded());
			add(panel);
		} else {
			Timer afterConstructor = new Timer() {

				@Override
				public void run() {
					triggerImageLoaded();
				}
			};
			afterConstructor.schedule(0);
		}

		t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
	}

	private void triggerImageLoaded() {
		geogebraFrame.runAsyncAfterSplash();
	}

	private boolean checkIfPreviewExists(Element thisArticle) {
		if (thisArticle != null && Dom.querySelectorForElement(thisArticle,
				".ggb_preview") != null) {
			return true;
		}
		return thisArticle != null
				&& thisArticle.getParentElement() != null
				&& Dom.querySelectorForElement(thisArticle.getParentElement(),
				".ggb_preview") != null;
	}

	/**
	 * Hide the splash popup.
	 */
	protected void hide() {
		this.removeFromParent();
		removePreviewImg(geoGebraElement);
	}

	private void removePreviewImg(Element thisArticle) {
		if (Js.isTruthy(thisArticle)) {
			Element img = Dom.querySelectorForElement(thisArticle, ".ggb_preview");
			if (Js.isTruthy(img)) {
				img.getParentNode().removeChild(img);
			}
		}
	}

	/**
	 * Notify about app load; hide popup if it was shown logng enough.
	 */
	public void canNowHide() {
		appLoaded = true;
		if (timerEllapsed) {
			hide();
		}
	}

	/**
	 * @return whether preview is present
	 */
	public boolean isPreviewExists() {
		return previewExists;
	}

}
