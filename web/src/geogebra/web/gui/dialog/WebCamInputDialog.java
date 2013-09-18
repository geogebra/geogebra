package geogebra.web.gui.dialog;

import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author arpad
 *
 */
public class WebCamInputDialog extends PopupPanel implements ClickHandler{

	private AppW app;

	private SimplePanel inputWidget;
	private Button btCancel, btOK;
	private Element video;

	/**
	 * @param modal modal
	 * @param app app
	 */
	public WebCamInputDialog(boolean modal, AppW app) {
	    super(false, modal);
	    this.app = app;
		addStyleName("GeoGebraPopup");
	    createGUI();
	    center();
    }

	private void createGUI() {

		inputWidget = new SimplePanel();
		video = populate(inputWidget.getElement(), app.getMenu("Webcam.Chrome"), app.getMenu("Webcam.Firefox"), app.getMenu("Webcam.Problem"));

		// create buttons
		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		// create button panel
		HorizontalPanel btPanel = new HorizontalPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputWidget);
		centerPanel.add(btPanel);

		setWidget(centerPanel);
	}

	private native Element populate(Element el, String messageChrome, String messageFirefox, String errorMessage) /*-{

		el.style.position = "relative";
		var message = ($wnd.navigator.mozGetUserMedia) ? messageFirefox : messageChrome;
		var ihtml = "<span style='position:absolute;width:640px;height:480px;text-align:center;'><br><br>" + message + "</span>\n";
		ihtml += "<video width='640' height='480' autoplay><br><br>" + errorMessage + "</video>";
		el.innerHTML = ihtml;
		var video = el.lastChild;

		$wnd.navigator.getMedia = ( $wnd.navigator.getUserMedia || 
                         $wnd.navigator.webkitGetUserMedia ||
                         $wnd.navigator.mozGetUserMedia ||
                         $wnd.navigator.msGetUserMedia);

		$wnd.URL =
			$wnd.URL ||
			$wnd.webkitURL ||
			$wnd.msURL ||
			$wnd.mozURL ||
			$wnd.oURL ||
			null;

		if ($wnd.navigator.getMedia) {
			try {
				$wnd.navigator.getMedia({video: true}, function(bs) {
					if ($wnd.URL && $wnd.URL.createObjectURL) {
						video.src = $wnd.URL.createObjectURL(bs);
						el.firstChild.style.display = "none";
					} else {
						video.src = bs;
						el.firstChild.style.display = "none";
					}
					video.play();
				},
				
			    function(err) {
			      @geogebra.web.main.AppW::debug(Ljava/lang/String;)("Error from WebCam: "+err);
			    } );
			    
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
		canvas.width = 640;
		canvas.height = 480;
		var ctx = canvas.getContext('2d');
		ctx.drawImage(video1, 0, 0);
		return canvas.toDataURL('image/png');

	}-*/;

	public void onClick(ClickEvent event) {
	    if (event.getSource() == btOK) {
	    	if (video != null)
	    		app.urlDropHappened(shotcapture(video),0,0);
	    	hide();
			app.getActiveEuclidianView().requestFocusInWindow();
	    } else if (event.getSource() == btCancel) {
	    	hide();
			app.getActiveEuclidianView().requestFocusInWindow();
	    }
    }

}
