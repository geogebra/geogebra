package org.geogebra.web.shared.components;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentSnackbar extends FlowPanel {
	private Localization loc;

	public ComponentSnackbar(AppW app, Localization loc,
			String title, String text, String buttonText) {
		this.loc = loc;
		addStyleName("snackbarComponent");
		buildGui(title, text, buttonText);
		app.getPanel().add(this);
	}

	private void buildGui(String title, String text, String buttonText) {
		FlowPanel textContainer = new FlowPanel();
		textContainer.addStyleName("txtContainer");

		if (title != null) {
			Label titleLbl = new Label(loc.getMenu(title));
			titleLbl.addStyleName("title");
			textContainer.add(titleLbl);
		}

		Label textLbl = new Label(loc.getMenu(text));
		textLbl.addStyleName("text");
		textContainer.add(textLbl);
		add(textContainer);

		if (buttonText != null) {
			StandardButton button = new StandardButton(buttonText);
			button.addStyleName("materialTextButton");
			add(button);
		}
	}

	public void show() {
		addStyleName("fadeIn");
	}
}
