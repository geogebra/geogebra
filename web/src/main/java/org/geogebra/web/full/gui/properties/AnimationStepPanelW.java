package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.full.gui.AngleTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AnimationStepPanelW extends OptionPanel 
implements ITextFieldListener {
	private AnimationStepModel model;
	private Label label;
	private AngleTextFieldW tfAnimStep;
	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 */
	public AnimationStepPanelW(AppW app) {
		this(new AnimationStepModel(app), app);
	}

	/**
	 *
	 * @param m model
	 * @param app application
	 */
	public AnimationStepPanelW(AnimationStepModel m, final AppW app) {
		kernel = app.getKernel();
		model = m;
		model.setListener(this);
		setModel(model);
		// text field for animation step
		label = new Label();
		tfAnimStep = new AngleTextFieldW(6, app);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("optionsInput");
		mainPanel.add(label);
		mainPanel.add(tfAnimStep);
		setWidget(mainPanel);

		// update on every change, so no extra blur handler needed
		tfAnimStep.addChangeHandler(event -> doActionPerformed());

		tfAnimStep.addKeyDownHandler(event -> {
			if (event.getNativeKeyCode() == '\n') {
				doActionPerformed();
			}
		});

		tfAnimStep.addBlurHandler(event -> doActionPerformed());

		tfAnimStep.enableGGBKeyboard();
	}

	private void doActionPerformed() {
		model.applyChanges(tfAnimStep.getText());
		//update(model.getGeos());
	}

	@Override
	public void setText(String text) {
		tfAnimStep.setText(text);
	}

	@Override
	public void setLabels() {
		label.setText(kernel.getLocalization().getMenu("AnimationStep"));
	}

	public void setPartOfSliderPanel() {
		model.setPartOfSlider(true);
	}

}
