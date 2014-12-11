package geogebra.phone.gui.view.euclidian;

import geogebra.phone.gui.view.AbstractStyleBar;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

public class EuclidianStyleBar extends AbstractStyleBar {
	
	private geogebra.common.euclidian.EuclidianStyleBar euclidianStyleBar;
	
	public EuclidianStyleBar(geogebra.common.euclidian.EuclidianStyleBar euclidianStyleBar) {
		this.euclidianStyleBar = euclidianStyleBar;
	}

	@Override
	protected IsWidget createStyleBar() {
		return (IsWidget) euclidianStyleBar;
	}

	@Override
	protected ImageResource createStyleBarIcon() {
		return (ImageResource) resources.styleBar_graphicsView();
	}

}
