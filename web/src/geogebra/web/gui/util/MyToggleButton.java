package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Image;

public class MyToggleButton extends ToggleButton {

	private static final long serialVersionUID = 1L;

	public MyToggleButton(Image icon, int iconHeight) {
		super(icon);
		//Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		//setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	public void update(Object[] geos) {
	}

	private void toggle() {
		this.setValue(!this.getValue());
	}
}
