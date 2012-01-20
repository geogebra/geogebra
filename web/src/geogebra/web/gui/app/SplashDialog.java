package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SplashDialog extends DialogBox {

	private Image splashImg = new Image(GuiResources.INSTANCE.getGeoGebraWebSplash());
	private Image spinner = new Image(GuiResources.INSTANCE.getGeoGebraWebSpinner());
	
	public SplashDialog() {
		super();
		VerticalPanel p = new VerticalPanel();
		p.add(splashImg);
		p.add(spinner);
		add(p);
		setModal(true);
		center();
		
	}
	
}
