package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentSnackbar extends FlowPanel {
	private AppW app;
	private StandardButton actionBtn;
	private Runnable btnAction;
	private Timer fadeIn = new Timer() {
		@Override
		public void run() {
			addStyleName("fadeIn");
			//fadeOut.schedule(4000);
		}
	};
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
	 * @param title snackbar title
	 * @param text snackbar text
	 * @param buttonText snackbar button text
	 */
	public ComponentSnackbar(AppW app, String title, String text, String buttonText) {
		this.app = app;
		addStyleName("snackbarComponent");
		if (app.isWhiteboardActive()) {
			addStyleName("mowPosition");
		}
		buildGui(title, text, buttonText);
		app.getPanel().add(this);
		fadeIn.schedule(100);
	}

	private void buildGui(String title, String text, String buttonText) {
		FlowPanel textContainer = new FlowPanel();
		textContainer.addStyleName("txtContainer");

		Label titleLbl = new Label(title);
		titleLbl.addStyleName("title");
		textContainer.add(titleLbl);

		if (text != null) {
			Label textLbl = new Label(text);
			textLbl.addStyleName("text");
			textContainer.add(textLbl);
		}
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
		fadeIn.schedule(500);
	}

	/**
	 * fade out snackbar
	 */
	public void hide() {
		fadeOut.run();
	}

	private void removeSnackbar() {
		app.getPanel().remove(this);
	}
}
