package org.geogebra.web.html5.gui;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.html5.css.GuiResourcesSimple;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class SplashDialog extends SimplePanel {

	boolean appLoaded = false;
	boolean timerEllapsed = false;
	boolean isPreviewExists = false;
	private String articleId;

	private Timer t = new Timer() {
		@Override
		public void run() {
			if (appLoaded) {
				hide();
			}
			timerEllapsed = true;
		}
	};

	private final GeoGebraFrame geogebraFrame;

	public SplashDialog(boolean showLogo, String articleId, GeoGebraFrame frame) {
		this.articleId = articleId;
		this.geogebraFrame = frame;
		isPreviewExists = checkIfPreviewExists();

		if (!isPreviewExists) {
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
			HTML spinner = new HTML(GuiResourcesSimple.INSTANCE
			        .ggbSpinnerHtml().getText());
			panel.add(spinner);
			addNativeLoadHandler(panel.getElement());
			add(panel);
		} else {
			this.triggerImageLoaded();
		}

		t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
	}

	private native String grabPreviewHtml() /*-{
		var ggbPreView = $doc.querySelector('.ggb_preview');
		ggbPreView.style.display = 'block';
		ggbPreView.parentNode.removeChild(ggbPreView);
		return ggbPreView.outerHTML;
	}-*/;

	protected native void addNativeLoadHandler(Element el) /*-{
		var img = el.querySelector(".spinner"), t = this;
		img.addEventListener("load", function() {
			t.@org.geogebra.web.html5.gui.SplashDialog::triggerImageLoaded()();
		});
	}-*/;

	private void triggerImageLoaded() {
		geogebraFrame.runAsyncAfterSplash();
	}

	private native boolean checkIfPreviewExists() /*-{
		var thisArticle = $doc
				.getElementById(this.@org.geogebra.web.html5.gui.SplashDialog::articleId);
		if (thisArticle) {
			return (thisArticle.querySelector(".ggb_preview") !== null);
		}
		return false;
	}-*/;

	protected void hide() {
		this.removeFromParent();
		removePreviewImg();

	}

	private native void removePreviewImg() /*-{
		var thisArticle = $doc
				.getElementById(this.@org.geogebra.web.html5.gui.SplashDialog::articleId), img;
		if (thisArticle) {
			img = thisArticle.querySelector(".ggb_preview");
		}
		if (img) {
			img.parentNode.removeChild(img);
		}
	}-*/;

	public void canNowHide() {
		appLoaded = true;
		if (timerEllapsed) {
			hide();
		}
	}

	public boolean isPreviewExists() {
		return isPreviewExists;
	}

}
