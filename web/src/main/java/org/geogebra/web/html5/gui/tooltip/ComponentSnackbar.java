package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentSnackbar extends FlowPanel {
	private AppW app;
	private Localization loc;
	private StandardButton actionBtn;
	private Runnable btnAction;
	private Timer fadeOut = new Timer() {
		@Override
		public void run() {
			removeStyleName("fadeIn");
			remove.schedule(2000);
		}
	};
	private Timer remove = new Timer() {
		@Override
		public void run() {
			removeSnackbar();
		}
	};

	/**
	 * constructor
	 * @param app see {@link AppW}
	 * @param loc localization
	 * @param title snackbar title
	 * @param text snackbar text
	 * @param buttonText snackbar button text
	 */
	public ComponentSnackbar(AppW app, Localization loc,
			String title, String text, String buttonText) {
		this.app = app;
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
			actionBtn = new StandardButton(buttonText);
			actionBtn.addStyleName("materialTextButton");
			add(actionBtn);
			actionBtn.addFastClickHandler(source -> {
				if (btnAction != null) {
					btnAction.run();
				}
			});
		}
	}

	/**
	 * set button action
	 * @param action - what should happen on positive button hit
	 */
	public void setButtonAction(Runnable action) {
		btnAction = action;
	}

	/**
	 * show snackbar and start fade out timer
	 */
	public void show() {
		addStyleName("fadeIn");
		fadeOut.schedule(4000);
	}

	/**
	 * fade out snackbar
	 */
	public void hide() {
		fadeOut.schedule(4000);
	}

	private void removeSnackbar() {
		app.getPanel().remove(this);
	}
}
