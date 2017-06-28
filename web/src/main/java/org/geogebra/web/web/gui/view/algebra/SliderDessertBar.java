package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Dessert bar for undefined variable to make a slider
 * 
 * @author Zbynek
 */
public class SliderDessertBar extends FlowPanel {
	private Label label;

	/**
	 * @param loc
	 *            localization
	 * @param parentItem
	 *            parent tree item
	 */
	public SliderDessertBar(final RadioTreeItem parentItem) {
		addStyleName("sliderDessertBar");
		label = new Label();
		label.addStyleName("sliderDessertButton");
		add(label);
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Log.debug("aaaaaaaaaaaaaaaaaaj");
			}
		});

	}
}
