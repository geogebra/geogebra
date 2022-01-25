package org.geogebra.web.shared.components.infoError;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentInfoErrorPanel extends FlowPanel {
	private Localization loc;

	/**
	 * info/error panel constructor
	 * @param loc - localization
	 * @param data - data of the panel including title, subtext and button text
	 * @param img - image
	 * @param buttonAction - handler for the button
	 */
	public ComponentInfoErrorPanel(Localization loc, InfoErrorData data, SVGResource img,
			Runnable buttonAction) {
		this.loc = loc;
		addStyleName("infoErrorPanel");
		buildGUI(data, img, buttonAction);
	}

	/**
	 * default info/error panel constructor without button with light bulb img
	 * @param loc - localization
	 * @param data - data of the panel including title, subtext and button text
	 */
	public ComponentInfoErrorPanel(Localization loc, InfoErrorData data) {
		this(loc, data,  MaterialDesignResources.INSTANCE.mow_lightbulb(), null);
	}

	private void buildGUI(InfoErrorData data, SVGResource img, Runnable buttonAction) {
		NoDragImage infoImage = new NoDragImage(img, 56, 56);
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