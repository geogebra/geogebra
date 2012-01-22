package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SplashDialog {
	
	DivElement loadingWrapper = null;

	public SplashDialog() {
		loadingWrapper = Document.get().createDivElement();
		loadingWrapper.setClassName("loadinganim");
		RootPanel.getBodyElement().appendChild(loadingWrapper);		
		double top = (Window.getClientHeight() / 2) - 66;
		double left = (Window.getClientWidth() /2) - 220;
		loadingWrapper.getStyle().setTop(top, Unit.PX);
		loadingWrapper.getStyle().setLeft(left, Unit.PX);
	}
	
	public void hide() {
		loadingWrapper.removeFromParent();
	}
	
}
