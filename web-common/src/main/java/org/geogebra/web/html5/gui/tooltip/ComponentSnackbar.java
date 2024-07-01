package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSnackbar extends FlowPanel {

	public static final int TOOL_TOOLTIP_DURATION = 8000;
	public static final int DEFAULT_TOOLTIP_DURATION = 4000;
	private StandardButton actionBtn;
	private Runnable btnAction;
	private int showDuration;
	private Timer fadeIn = new Timer() {
		@Override
		public void run() {
			addStyleName("fadeIn");
			fadeOut.schedule(showDuration);
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
			removeFromParent();
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
		addStyleName("snackbarComponent");
		getElement().setId("snackbarID");
		buildGui(title, text, buttonText);
		app.getAppletFrame().add(this);
		fadeIn.schedule(100);
	}

	private void buildGui(String title, String text, String buttonText) {
		FlowPanel textContainer = new FlowPanel();
		textContainer.addStyleName("txtContainer");

		String[] textLines = title.split("\\n");
		for (String line : textLines) {
			Label textLbl = new Label(line);
			textLbl.addStyleName("title");
			textContainer.add(textLbl);
		}

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
					fadeOut.run();
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
	 * fade out snackbar without delay
	 */
	public void hide() {
		fadeOut.run();
		btnAction = null;
	}

	public void setShowDuration(int showDuration) {
		this.showDuration = showDuration;
	}
}
