package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.kernel.View;
import org.geogebra.common.util.NumberFormatAdapter;
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

	final static private double MM_TO_CM = 0.1;

	private Runnable onExportButtonPressed;

	private ComponentInputField widthValue;
	private ComponentInputField lengthValue;
	private ComponentInputField heightValue;
	private ComponentInputField scaleUnitValue;
	private ComponentInputField scaleCmValue;
	private ComponentInputField lineThicknessValue;

	final private NumberFormatAdapter dimensionNF;
	final private NumberFormatAdapter scaleNF;

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
		dimensionNF = FormatFactory.getPrototype().getNumberFormat("#.#", 1);
		scaleNF = FormatFactory.getPrototype().getNumberFormat("#.##", 2);
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
		addStyleName("export3dDialog");
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
		dimensionsPanel.setStyleName("panelRow");
		widthValue = addTextField("Width", "cm", dimensionsPanel);
		lengthValue = addTextField("Length", "cm", dimensionsPanel);
		heightValue = addTextField("Height", "cm", dimensionsPanel);
		root.add(dimensionsPanel);
	}

	private void buildScalePanel(FlowPanel root) {
		FlowPanel scalePanel = new FlowPanel();
		scalePanel.setStyleName("panelRow");
		scaleUnitValue = addTextField("Scale", "units", scalePanel);
		Label equalLabel = new Label();
		equalLabel.setText("=");
		equalLabel.setStyleName("label");
		scalePanel.add(equalLabel);
		scaleCmValue = addTextField(null, "cm", scalePanel);
		root.add(scalePanel);
	}

	private void buildLineThicknessPanel(FlowPanel root) {
		lineThicknessValue = addTextField("Thickness", "mm", root);
	}

	private ComponentInputField addTextField(String labelText, String suffixText, FlowPanel root) {
		final ComponentInputField field = new ComponentInputField((AppW) app,
				null, labelText, null, "", 2, suffixText);
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
				.setText(app.getLocalization().getMenu("DownloadAsStl"));
		updateButtonLabels("Download");
	}

	private void initValues(double width, double length, double height,
			double scale, double thickness) {
		setValue(widthValue, width * MM_TO_CM, dimensionNF);
		setValue(lengthValue, length * MM_TO_CM, dimensionNF);
		setValue(heightValue, height * MM_TO_CM, dimensionNF);
		double s = scale * MM_TO_CM;
		if (s > 1) {
			setValue(scaleUnitValue, 1, scaleNF);
			setValue(scaleCmValue, s, scaleNF);
		} else {
			setValue(scaleUnitValue, 1 / s, scaleNF);
			setValue(scaleCmValue, 1, scaleNF);
		}
		setValue(lineThicknessValue, thickness * 2, dimensionNF);
	}

	static private void setValue(ComponentInputField input, double v,
			NumberFormatAdapter nf) {
		input.setInputText(nf.format(v));
	}

	@Override
	public void show(double width, double length, double height, double scale,
			double thickness, Runnable exportAction) {
		initValues(width, length, height, scale, thickness);
		this.onExportButtonPressed = exportAction;
		((AppW) app).registerPopup(this);
		super.show();
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
	}

}
