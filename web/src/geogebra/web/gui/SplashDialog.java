package geogebra.web.gui;

import geogebra.common.GeoGebraConstants;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.applet.GeoGebraFrame;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
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
	
	private GeoGebraFrame geogebraFrame;
	
	

	public SplashDialog(boolean showLogo, String articleId) {
		this.articleId = articleId;
		isPreviewExists = checkIfPreviewExists();
		String html = "<div style=\"position: absolute; z-index: 1000000; background-color: white; \">";
		if (!isPreviewExists) {
			html += GuiResources.INSTANCE.ggbSplashHtml().getText();
		} else if (showLogo) {
			html += grabPreviewHtml();
		}
		html += GuiResources.INSTANCE.ggbSpinnerHtml().getText() + "</div>"; 
	    HTML splash = new HTML(html);	
	    addNativeLoadHandler(splash.getElement());
	    add(splash);
	    t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
    }
	
	private native String  grabPreviewHtml() /*-{
		var ggbPreView = $doc.querySelector('.ggb_preview');
	    ggbPreView.style.display = 'block';
	    ggbPreView.parentNode.removeChild(ggbPreView);
	    return ggbPreView.outerHTML;
    }-*/;

	protected native void addNativeLoadHandler(Element el) /*-{
		var img = el.querySelector(".spinner"),
			t = this;
		img.addEventListener("load",function() {
			t.@geogebra.web.gui.SplashDialog::triggerImageLoaded()();
		});
	}-*/;
	
	private void triggerImageLoaded() {
		geogebraFrame.runAsyncAfterSplash();
	}

	private native boolean checkIfPreviewExists() /*-{
		var thisArticle = $doc.getElementById(this.@geogebra.web.gui.SplashDialog::articleId);
		if (thisArticle) {
			return (thisArticle.querySelector(".ggb_preview") !== null);
		}
	    return false;
    }-*/;

	protected void hide() {
		this.removeFromParent();
    }

	private native void removePreviewImg() /*-{
	    var thisArticle = $doc.getElementById(this.@geogebra.web.gui.SplashDialog::articleId),
			img = thisArticle.querySelector(".ggb_preview");
		img.parentNode.removeChild(img);
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

	public void setGeoGebraFrame(GeoGebraFrame ggf) {
	    this.geogebraFrame = ggf;	    
    }

}
