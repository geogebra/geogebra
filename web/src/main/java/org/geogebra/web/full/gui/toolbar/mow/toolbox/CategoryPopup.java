package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

public class CategoryPopup extends GPopupPanel {

	public CategoryPopup(AppW appW, List<Integer> tools) {
		super(appW.getAppletFrame(), appW);
		setAutoHideEnabled(false);

		buildGui(tools);
	}

	private void buildGui(List<Integer> tools) {
		for (Integer mode : tools) {
			addToolButton(mode);
		}
	}

	private void addToolButton(Integer mode) {
		//StandardButton toolBtn = new StandardButton()
	}

	public void show(int left, int top) {
		show();
		setPopupPosition(left ,top);
	}
}
