package org.geogebra.web.web.gui.properties;

import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.AngleTextFieldW;

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

		FocusListenerW focusListener = new FocusListenerW(this){};
		tfAnimStep.addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == '\n') {
					doActionPerformed();
				}

			}});

	}

	private void doActionPerformed() {
		model.applyChanges(tfAnimStep.getText());
		//update(model.getGeos());
	}

	public void setText(String text) {
		tfAnimStep.setText(text);
	}

	@Override
	public void setLabels() {
		label.setText(kernel.getApplication().getPlain("AnimationStep") + ": ");
	}

	public void setPartOfSliderPanel() {
		model.setPartOfSlider(true);
    }

}
