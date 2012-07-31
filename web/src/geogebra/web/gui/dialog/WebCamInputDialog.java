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

public class WebCamInputDialog extends PopupPanel implements ClickHandler{

	protected AppW app;

	protected SimplePanel inputWidget;
	protected Button btCancel, btOK;
	protected Element video;

	public WebCamInputDialog(boolean modal, AppW app) {
	    super(false, modal);
	    this.app = app;
	    createGUI();
	    center();
    }

	protected void createGUI() {

		inputWidget = new SimplePanel();
		video = populate(inputWidget.getElement());

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

	public native Element populate(Element el) /*-{

		el.style.position = "relative";
		var ihtml = "<span style='position:absolute;width:640px;height:480px;text-align:center;'><br><br>Please click 'Allow' in the pop-up bar.</span>\n";
		ihtml += "<video width='640' height='480' autoplay><br><br>This video could not be played. 'video tag' is not supported. For more information, please check out the GeoGebra Wiki.</video>";
		el.innerHTML = ihtml;
		var video = el.lastChild;

		$wnd.navigator.getUserMedia =
			$wnd.navigator.getUserMedia ||
			$wnd.navigator.webkitGetUserMedia ||
			$wnd.navigator.msGetUserMedia ||
			$wnd.navigator.mozGetUserMedia ||
			$wnd.navigator.oGetUserMedia ||
			null;

		$wnd.URL =
			$wnd.URL ||
			$wnd.webkitURL ||
			$wnd.msURL ||
			$wnd.mozURL ||
			$wnd.oURL ||
			null;

		if ($wnd.navigator.getUserMedia) {
			try {
				$wnd.navigator.getUserMedia({video: true}, function(bs) {
					if ($wnd.URL && $wnd.URL.createObjectURL) {
						video.src = $wnd.URL.createObjectURL(bs);
						el.firstChild.style.display = "none";
					} else {
						video.src = bs;
						el.firstChild.style.display = "none";
					}
				});
				return video;
			} catch (e) {
				try {
					$wnd.navigator.getUserMedia("video", function(bs) {
						if ($wnd.URL && $wnd.URL.createObjectURL) {
							video.src = $wnd.URL.createObjectURL(bs);
							el.firstChild.style.display = "none";
						} else {
							video.src = bs;
							el.firstChild.style.display = "none";
						}
					});
					return video;
				} catch (exc) {
					el.firstChild.innerHTML = "<br><br>This video could not be played. 'getUserMedia' is not supported. For more information, please check out the GeoGebra Wiki.";
					return null;
				}
			}
		} else {
			el.firstChild.innerHTML = "<br><br>This video could not be played. 'getUserMedia' is not supported. For more information, please check out the GeoGebra Wiki.";
		}
		return null;
	}-*/;

	public native String shotcapture(Element video) /*-{

		var canvas = $doc.createElement("canvas");
		canvas.width = 640;
		canvas.height = 480;
		var ctx = canvas.getContext('2d');
		ctx.drawImage(video, 0, 0);
		return canvas.toDataURL('image/png');

	}-*/;

	public void onClick(ClickEvent event) {
	    if (event.getSource() == btOK) {
	    	if (video != null)
	    		app.urlDropHappened(shotcapture(video),0,0);
	    	hide();
	    } else if (event.getSource() == btCancel) {
	    	hide();
	    }
    }

}
