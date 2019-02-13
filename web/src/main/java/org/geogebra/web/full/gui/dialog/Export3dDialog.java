package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.kernel.View;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Dialog for export 3D
 *
 */
public class Export3dDialog extends OptionDialog
		implements Export3dDialogInterface, SetLabels {

	private String extension;
	private Runnable onExportButtonPressed;

	private ComponentInputField widthValue;
	private ComponentInputField lengthValue;
	private ComponentInputField heightValue;
	private ComponentInputField scaleUnitValue;
	private ComponentInputField scaleCmValue;
	private ComponentInputField lineThicknessValue;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            app
	 * @param view
	 *            exported view
	 * @param extension
	 *            format extension
	 */
	public Export3dDialog(final AppW app, final View view,
			String extension) {
		super(app.getPanel(), app, false);
		this.extension = extension;
		buildGui();
		setPrimaryButtonEnabled(true);
		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.getKernel().detach(view);
				app.unregisterPopup(Export3dDialog.this);
				app.hideKeyboard();
			}
		});
		setGlassEnabled(true);
	}

	private void buildGui() {
		addStyleName("tableOfValuesDialog");
		FlowPanel contentPanel = new FlowPanel();
		buildDimensionsPanel(contentPanel);
		buildScalePanel(contentPanel);
		buildLineThicknessPanel(contentPanel);
		buildButtonPanel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void buildDimensionsPanel(FlowPanel root) {
		FlowPanel dimensionsPanel = new FlowPanel();
		dimensionsPanel.setStyleName("panelRowIndent");
		widthValue = addTextField("Width", dimensionsPanel);
		lengthValue = addTextField("Length", dimensionsPanel);
		heightValue = addTextField("Height", dimensionsPanel);
		root.add(dimensionsPanel);
	}

	private void buildScalePanel(FlowPanel root) {
		FlowPanel scalePanel = new FlowPanel();
		scalePanel.setStyleName("panelRowIndent");
		scaleUnitValue = addTextField("Scale", scalePanel);
		Label equalLabel = new Label();
		equalLabel.setText("=");
		equalLabel.setStyleName("label");
		scalePanel.add(equalLabel);
		scaleCmValue = addTextField(null, scalePanel);
		root.add(scalePanel);
	}

	private void buildLineThicknessPanel(FlowPanel root) {
		lineThicknessValue = addTextField("Thickness", root);
	}

	private ComponentInputField addTextField(String labelText, FlowPanel root) {
		final ComponentInputField field = new ComponentInputField((AppW) app,
				null, labelText, null, "", 4);
		root.add(field);
		return field;
	}

	private void buildButtonPanel(FlowPanel root) {
		root.add(getButtonPanel());
	}

	@Override
	protected void processInput() {
		hide();
		if (onExportButtonPressed != null) {
			onExportButtonPressed.run();
		}
	}

	@Override
	public void setLabels() {
		getCaption()
				.setText(app.getLocalization().getPlain("DownloadAsA",
						extension.toUpperCase()));
		updateButtonLabels("Download");
	}

	@Override
	public void show(Runnable exportAction) {
		this.onExportButtonPressed = exportAction;
		((AppW) app).registerPopup(this);
		super.show();
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
	}

}
