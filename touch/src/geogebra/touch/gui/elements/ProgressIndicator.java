package geogebra.touch.gui.elements;

import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class ProgressIndicator extends PopupPanel {

	private static Image progressIndicator = new Image(DefaultResources.INSTANCE.progressIndicator());

	static
	{
		progressIndicator.setPixelSize(50, 50);
	}
	
	@Override
	public void show() {
		this.setPixelSize(50, 50);
		this.setWidget(ProgressIndicator.progressIndicator);
		super.show();
		this.center();

	}

	@Override
	public void hide() {
		super.hide();

		this.remove(ProgressIndicator.progressIndicator);
	}
}
