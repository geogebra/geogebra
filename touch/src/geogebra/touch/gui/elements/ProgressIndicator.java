package geogebra.touch.gui.elements;

import geogebra.touch.TouchEntryPoint;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class ProgressIndicator extends PopupPanel {

	private static Image progressIndicator = new Image(TouchEntryPoint
			.getLookAndFeel().getIcons().progressIndicator());

	@Override
	public void show() {
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
