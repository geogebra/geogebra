package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SplashDialog extends DialogBox {

	private ImageResource splashImg = GuiResources.INSTANCE.getGeoGebraWebSplash();
	private ImageResource spinner = GuiResources.INSTANCE.getGeoGebraWebSpinner();
	
	public SplashDialog() {
		super();
		Canvas cvs = Canvas.createIfSupported();
		final Context2d ctx = cvs.getContext2d();
		cvs.setWidth("427px");
		cvs.setCoordinateSpaceWidth(427);
		cvs.setHeight("136px");
		cvs.setCoordinateSpaceHeight(136);
		final Image spli = new Image(splashImg);
		spli.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				ctx.drawImage(ImageElement.as(spli.getElement()),0,0);
			}
		});
		final Image spi = new Image(spinner);
		spi.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				ctx.drawImage(ImageElement.as(spi.getElement()), 0, 120);
			}
		});
		add(cvs);
		setAnimationEnabled(false);
		setPixelSize(427, 136);
		center();
		
	}
	
}
