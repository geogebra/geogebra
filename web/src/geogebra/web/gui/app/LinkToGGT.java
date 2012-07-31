package geogebra.web.gui.app;

import geogebra.web.gui.images.AppResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

public class LinkToGGT extends Composite {

	private static LinkToGGTUiBinder uiBinder = GWT
	        .create(LinkToGGTUiBinder.class);

	interface LinkToGGTUiBinder extends UiBinder<HTMLPanel, LinkToGGT> {
	}
	
	@UiField
	HTMLPanel linktoggbtube;

	public LinkToGGT() {
		initWidget(uiBinder.createAndBindUi(this));
		Image img = new Image(AppResources.INSTANCE.GeoGebraTube().getSafeUri());
		img.setPixelSize(50, 50);
		img.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				Window.open("http://geogebratube.org", "", "");
			}
		});
		linktoggbtube.add(img);
	}

	
}
