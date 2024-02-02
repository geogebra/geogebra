package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.*;

import java.util.Arrays;
import java.util.List;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel {
	private final AppW appW;
	private ToolboxDecorator decorator;

	public ToolboxMow(AppW appW) {
		this.appW = appW;
		decorator = new ToolboxDecorator(this);
		addStyleName("toolboxMow");
		buildGui();
	}

	private void buildGui() {
		decorator.positionLeft();

		addActionButton(ToolbarSvgResources.INSTANCE.mode_move_32(),
				() -> appW.setMoveMode());
		addDivider();
		addCategoryButton(ToolbarSvgResources.INSTANCE.mode_pen(), Arrays.asList(MODE_PEN,
				MODE_HIGHLIGHTER, MODE_ERASER));
	}


	private void addActionButton(SVGResource image, Runnable handler) {
		StandardButton actionBtn = new StandardButton(image, null, 24, 24);
		actionBtn.addFastClickHandler((event) -> handler.run());

		add(actionBtn);
	}

	private void addCategoryButton(SVGResource image, List<Integer> tools) {
		StandardButton categoryBtn = new StandardButton(image, null, 24, 24);
		categoryBtn.addFastClickHandler((event)
				-> {
			CategoryPopup popup = new CategoryPopup(appW, tools);
			popup.show(categoryBtn.getElement().getAbsoluteRight(),
					categoryBtn.getElement().getAbsoluteTop());
		});

		add(categoryBtn);
	}

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.setStyleName("divider");

		add(divider);
	}
}
