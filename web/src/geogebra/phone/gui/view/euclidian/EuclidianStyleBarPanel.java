package geogebra.phone.gui.view.euclidian;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.phone.gui.view.StyleBarPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class EuclidianStyleBarPanel extends FlowPanel implements StyleBarPanel {

	public EuclidianStyleBarPanel(EuclidianStyleBar styleBar) {
		setStyleName("StyleBarPanel_");
		add((IsWidget) styleBar);
	}
}
