package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WebCamInputPanel extends VerticalPanel {
	
	private SimplePanel inputWidget;
	private Element video;
	private JavaScriptObject stream;
	private int canvasWidth = 640, canvasHeight = 480;// overwritten by real
														// dimensions
	
	private AppW app;
	private static final int MAX_CANVAS_WIDTH = 640;

	public WebCamInputPanel(AppW app) {
	    this.app = app;
	    initGUI();
    }

	private void initGUI() {		
		inputWidget = new SimplePanel();
		video = populate(inputWidget.getElement(), app.getMenu("Webcam.Chrome"), app.getMenu("Webcam.Firefox"), app.getMenu("Webcam.Problem"));

		add(inputWidget);
	}

	private native Element populate(Element el, String messageChrome, String messageFirefox, String errorMessage) /*-{

		el.style.position = "relative";
		var message = ($wnd.navigator.mozGetUserMedia) ? messageFirefox
				: messageChrome;
		var ihtml = "<span style='position:absolute;width:213px;height:160px;text-align:center;'><br><br>"
				+ message + "</span>\n";
		ihtml += "<video width='213' height='160' autoplay><br><br>"
				+ errorMessage + "</video>";
		el.innerHTML = ihtml;
		var video = el.lastChild;

		$wnd.navigator.getMedia = ($wnd.navigator.getUserMedia
				|| $wnd.navigator.webkitGetUserMedia
				|| $wnd.navigator.mozGetUserMedia || $wnd.navigator.msGetUserMedia);

		$wnd.URL = $wnd.URL || $wnd.webkitURL || $wnd.msURL || $wnd.mozURL
				|| $wnd.oURL || null;
		var that = this;
		if ($wnd.navigator.getMedia) {
			try {
				$wnd.navigator
						.getMedia(
								{
									video : true
								},
								function(bs) {
									if ($wnd.URL && $wnd.URL.createObjectURL) {
										video.src = $wnd.URL
												.createObjectURL(bs);
										el.firstChild.style.display = "none";
									} else {
										video.src = bs;
										el.firstChild.style.display = "none";
									}
									that.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stream = bs;
								},

								function(err) {
									@org.geogebra.common.main.App::debug(Ljava/lang/String;)("Error from WebCam: "+err);
								});

				return video;
			} catch (e) {
				el.firstChild.innerHTML = "<br><br>" + errorMessage;
				return null;

			}
		} else {
			el.firstChild.innerHTML = "<br><br>" + errorMessage;
		}
		return null;
	}-*/;

	private native String shotcapture(Element video1) /*-{
		var canvas = $doc.createElement("canvas");
		canvas.width = Math
				.max(video1.videoWidth || 0,
						@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH);
		canvas.height = video1.videoHeight ? Math.round(canvas.width
				* video1.videoHeight / video1.videoWidth)
				: 0.75 * @org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH;
		var ctx = canvas.getContext('2d');
		ctx.drawImage(video1, 0, 0);
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stopVideo()();
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::canvasWidth = canvas.width;
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::canvasHeight = canvas.height;
		return canvas.toDataURL('image/png');
	}-*/;

	public native void stopVideo() /*-{
		var stream = this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stream;
		if (stream == null) {
			return;
		}
		stream.stop();
		stream = null;
	}-*/;

	public String getImageDataURL() {
		if (video == null) {
			return null;
		}
		return shotcapture(video);
	}

	public void startVideo() {
		stopVideo();
		inputWidget.getElement().removeAllChildren();
		video = populate(inputWidget.getElement(), app.getMenu("Webcam.Chrome"), app.getMenu("Webcam.Firefox"), app.getMenu("Webcam.Problem"));

	    
    }

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}
}
