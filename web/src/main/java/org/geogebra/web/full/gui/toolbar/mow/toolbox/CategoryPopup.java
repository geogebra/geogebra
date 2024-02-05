package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import java.util.List;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;

public class CategoryPopup extends GPopupPanel {
	private final AppW appW;

	/**
	 * Category popup for MOW toolbox
	 * @param appW - application
	 * @param tools - list of tools
	 */
	public CategoryPopup(AppW appW, List<Integer> tools) {
		super(appW.getAppletFrame(), appW);
		this.appW = appW;
		setAutoHideEnabled(true);

		buildGui(tools);
	}

	private void buildGui(List<Integer> tools) {
		FlowPanel contentPanel = new FlowPanel();
		for (Integer mode : tools) {
			addToolButton(mode, contentPanel);
		}
		add(contentPanel);
	}

	private void addToolButton(Integer mode, FlowPanel parent) {
		ResourcePrototype image = GGWToolBar.getImageURLNotMacro(
				ToolbarSvgResources.INSTANCE, mode, appW);
		StandardButton toolBtn = new StandardButton(image, null, 24, 24);
		parent.add(toolBtn);
	}

	/**
	 * show popup at position
	 * @param left - left position
	 * @param top - top position
	 */
	public void show(int left, int top) {
		show();
		setPopupPosition(left, top);
	}
}
