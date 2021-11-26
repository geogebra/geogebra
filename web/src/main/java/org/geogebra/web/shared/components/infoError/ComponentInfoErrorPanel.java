package org.geogebra.web.shared.components.infoError;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentInfoErrorPanel extends FlowPanel {
	private Localization loc;

	public ComponentInfoErrorPanel(Localization loc, InfoErrorData data, SVGResource img,
			 Runnable buttonAction) {
		this.loc = loc;
		addStyleName("infoErrorPanel");
		buildGUI(data, img, buttonAction);
	}

	private void buildGUI(InfoErrorData data, SVGResource img, Runnable buttonAction) {
		NoDragImage infoImage = new NoDragImage(img.getSafeUri().asString());
		infoImage.setWidth(112);
		infoImage.setHeight(112);
		add(infoImage);

		if (data.getTitle() != null) {
			Label titleLabel = new Label(loc.getMenu(data.getTitle()));
			titleLabel.setStyleName("title");
			add(titleLabel);
		}

		if (data.getSubtext() != null) {
			Label subtextLabel = new Label(loc.getMenu(data.getSubtext()));
			subtextLabel.setStyleName("subtext");
			add(subtextLabel);
		}

		if (data.getActionButtonText() != null) {
			StandardButton actionButton =
					new StandardButton(loc.getMenu(data.getActionButtonText()));
			actionButton.addStyleName("dialogContainedButton");
			actionButton.addFastClickHandler(source -> buttonAction.run());
			add(actionButton);
		}
	}
}
