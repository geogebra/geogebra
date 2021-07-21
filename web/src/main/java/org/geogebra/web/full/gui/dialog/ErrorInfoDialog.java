package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ErrorInfoDialog extends ComponentDialog {

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param content - dialog content
	 * @param isError - if true use error icon, info icon otherwise
	 */
	public ErrorInfoDialog(AppW app, DialogData dialogData,
			String content, boolean isError) {
		super(app, dialogData, false, true);
		addStyleName("errorDialog");
		buildGUI(content, isError);
	}

	private void buildGUI(String content, boolean isError) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("mainPanel");

		NoDragImage img = new NoDragImage(
				isError ? GuiResourcesSimple.INSTANCE.dialog_error()
				: GuiResourcesSimple.INSTANCE.dialog_info(), 32);
		mainPanel.add(img);

		FlowPanel messagePanel = new FlowPanel();
		String[] lines = content.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}
		mainPanel.add(messagePanel);

		setDialogContent(mainPanel);
	}
}
