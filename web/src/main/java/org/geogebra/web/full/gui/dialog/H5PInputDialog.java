package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.ComponentOrDivider;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import elemental2.dom.File;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

public class H5PInputDialog extends ComponentDialog {

	MediaInputPanel mediaInputPanel;
	private FileUpload h5pChooser = getH5PChooser();
	private FileUpload getH5PChooser() {
		FileUpload h5pChooser = new FileUpload();
		h5pChooser.addChangeHandler(event -> {
			HTMLInputElement el = Js.uncheckedCast(h5pChooser.getElement());
			loadH5PElement(el.files.item(0));
		});
		h5pChooser.getElement().setAttribute("accept", ".h5p");
		return h5pChooser;
	}

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
		addDialogContent(h5pChooser);

		h5pChooser.addStyleName("hidden");
		setPosBtnDisabled(true);

	}

	private void addSelectFileButton() {
		FlowPanel container = new FlowPanel();
		container.addStyleName("btnContainer");

		StandardButton selectFileBtn = new StandardButton(app.getLocalization()
				.getMenu("H5PDialog.UploadFile"), app);
		selectFileBtn.setStyleName("materialOutlinedButton");
		selectFileBtn.addStyleName("uploadFileBtn");
		selectFileBtn.addFastClickHandler(source -> h5pChooser.click());

		container.add(selectFileBtn);
		addDialogContent(container);
	}

	/**
	 * loads the h5p element
	 *
	 * @param file
	 *            to load.
	 *
	 */
	void loadH5PElement(File file) {
		// TODO fill me
		hide();
	}

	@Override
	public void hide() {
		app.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}
}
