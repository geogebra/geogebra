package geogebra.web.gui.properties;

import geogebra.common.gui.dialog.options.model.AnimationStepModel;
import geogebra.common.gui.dialog.options.model.ITextFieldListener;
import geogebra.common.kernel.Kernel;
import geogebra.html5.event.FocusListener;
import geogebra.web.gui.AngleTextFieldW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AnimationStepPanelW extends OptionPanel 
implements ITextFieldListener {
	private AnimationStepModel model;
	private Label label;	
	private AngleTextFieldW tfAnimStep;
	private AppW app;
	private Kernel kernel;

	public AnimationStepPanelW(AppW app) {
		this.app = app;
		kernel = app.getKernel();
		model = new AnimationStepModel(this, app);
		setModel(model);
		// text field for animation step
		label = new Label();
		tfAnimStep = new AngleTextFieldW(6, app);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(label);
		mainPanel.add(tfAnimStep);
		setWidget(mainPanel);

		tfAnimStep.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				doActionPerformed();
			}

		});

		FocusListener focusListener = new FocusListener(this){};
		tfAnimStep.addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == '\n') {
					doActionPerformed();
				}

			}});

	}

	private void doActionPerformed() {
		model.applyChanges(kernel.getAlgebraProcessor().evaluateToNumeric(
				tfAnimStep.getText(),true));
		//update(model.getGeos());
	}

	public void setText(String text) {
		tfAnimStep.setText(text);
	}

	@Override
	public void setLabels() {
		label.setText(kernel.getApplication().getPlain("AnimationStep") + ": ");
	}

}
