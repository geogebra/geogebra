package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.dom.client.Style;

public class MyToggleButton extends ToggleButton {

	private static final long serialVersionUID = 1L;

	public MyToggleButton(Image icon, int iconHeight) {
		super(icon);
		changeStyle();
		//Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		//setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	public MyToggleButton(ImageResource icon, int iconHeight) {
		super(new Image(icon));
		changeStyle();
		//Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		//setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	public void changeStyle() {
		getElement().getStyle().setPaddingTop(3, Style.Unit.PX);
		getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
		getElement().getStyle().setPaddingRight(3, Style.Unit.PX);
		getElement().getStyle().setPaddingBottom(1, Style.Unit.PX);
		getElement().getStyle().setMargin(5, Style.Unit.PX);
	}

	public void update(Object[] geos) {
	}

	private void toggle() {
		this.setValue(!this.getValue());
	}

	public void setSelected(boolean selected) {
	    // TODO Auto-generated method stub
	    
    }
}
