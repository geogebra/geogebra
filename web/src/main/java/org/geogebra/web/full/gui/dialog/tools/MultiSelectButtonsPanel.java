package org.geogebra.web.full.gui.dialog.tools;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

class MultiSelectButtonsPanel extends FlowPanel {
	public interface ButtonsListener {

		void moveSelection(boolean b);

		void deleteSelection();
	}

	public MultiSelectButtonsPanel(ButtonsListener widgets) {
		addStyleName("toolListButtons");

		addIconButton(MaterialDesignResources.INSTANCE.arrow_drop_up(),
				w -> widgets.moveSelection(true));
		addIconButton(MaterialDesignResources.INSTANCE.arrow_drop_down(),
				w -> widgets.moveSelection(false));
		addIconButton(MaterialDesignResources.INSTANCE.delete_black(),
				w -> widgets.deleteSelection());
	}

	private void addIconButton(SVGResource img,
			FastClickHandler clickHandler) {
		StandardButton btn = new StandardButton(img, null, 24);
		btn.addFastClickHandler(clickHandler);
		btn.addStyleName("IconButton");
		add(btn);
	}
}
