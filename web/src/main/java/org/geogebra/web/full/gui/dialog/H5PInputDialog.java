package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.ComponentOrDivider;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class H5PInputDialog extends ComponentDialog {

	MediaInputPanel mediaInputPanel;

	/**
	 * h5p tool dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 */
	public H5PInputDialog(AppW app,
			DialogData dialogData, boolean autoHide,
			boolean hasScrim) {
		super(app, dialogData, autoHide, hasScrim);
		addStyleName("mediaDialog");
		addStyleName("h5pDialog");
		buildGUI();
	}

	private void buildGUI() {
		Label helpTxt = new Label(app.getLocalization().getMenu("H5PDialog.InsertHelpTxt"));
		helpTxt.setStyleName("helpTxt");
		addDialogContent(helpTxt);

		mediaInputPanel = new MediaInputPanel((AppW) app, this, "Link", true);
		mediaInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));
		addDialogContent(mediaInputPanel);

		addDialogContent(new ComponentOrDivider(app.getLocalization().getMenu("Symbol.Or")));
		addSelectFileButton();
	}

	private void addSelectFileButton() {
		FlowPanel container = new FlowPanel();
		container.addStyleName("btnContainer");

		StandardButton selectFileBtn = new StandardButton(app.getLocalization()
				.getMenu("H5PDialog.UploadFile"), app);
		selectFileBtn.setStyleName("materialOutlinedButton");
		selectFileBtn.addStyleName("uploadFileBtn");
		ClickStartHandler.init(selectFileBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {

			}
		});

		container.add(selectFileBtn);
		addDialogContent(container);
	}
}
