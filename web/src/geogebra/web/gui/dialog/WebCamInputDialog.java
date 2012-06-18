package geogebra.web.gui.dialog;

import geogebra.web.gui.view.algebra.InputPanel.DialogType;
import geogebra.web.main.Application;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WebCamInputDialog extends PopupPanel implements ClickHandler{

	protected Application app;

	protected Widget inputWidget;
	protected Button btCancel, btOK;
	protected Element video;

	public WebCamInputDialog(boolean modal) {
	    super(false, modal);
    }

	protected void createGUI(String title, String message,
			boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {

		inputWidget = new Widget();
		video = populate(inputWidget.getElement());

		// create buttons
		btOK = new Button("OK");
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button("Cancel");
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		// create button panel
		HorizontalPanel btPanel = new HorizontalPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);

		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputWidget);
		centerPanel.add(btPanel);

		setWidget(centerPanel);
	}

	public native Element populate(Element el) /*-{

		el.innerHTML = "<video width='640' height='480' autoplay>This video could not be played. Please check out in the GeoGebra Wiki: why.</video>\n";
		var video = el.firstChild;

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
		try {
			$wnd.navigator.getUserMedia({video: true}, function(bs) {
				if ($wnd.URL && $wnd.URL.createObjectURL) {
					video.src = $wnd.URL.createObjectURL(bs);
				} else {
					video.src = bs;
				}
			});
		} catch (e) {
			$wnd.navigator.getUserMedia("video", function(bs) {
				if ($wnd.URL && $wnd.URL.createObjectURL) {
					video.src = $wnd.URL.createObjectURL(bs);
				} else {
					video.src = bs;
				}
			});
		}
		return video;
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
	    // do nothing - overriden method
	    if (event.getSource() == btOK) {
	    	app.urlDropHappened(shotcapture(video),0,0);
	    	hide();
	    } else if (event.getSource() == btCancel) {
	    	hide();
	    }
    }

}
