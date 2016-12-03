package org.geogebra.web.web.gui.properties;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel.IAnimationSpeedListener;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.FieldHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AnimationSpeedPanelW extends ListBoxPanel implements IAnimationSpeedListener {
	private AutoCompleteTextFieldW tfAnimSpeed;
	private Label modeLabel;
	private AnimationSpeedModel model;
	private AppW app;
	
	public AnimationSpeedPanelW(AppW app) {
		this(new AnimationSpeedModel(app), app);
	}

	public AnimationSpeedPanelW(AnimationSpeedModel m, final AppW app) {
		super(app.getLocalization(), app.getLocalization().getMenu(
				"AnimationSpeed"));
        this.app = app;
		model = m;
		model.setListener(this);
    	setModel(model);
    	modeLabel = new Label();

		InputPanelW inputPanel = new InputPanelW(null, app, -1, false);
		tfAnimSpeed = inputPanel.getTextComponent();
        FlowPanel mainPanel = new FlowPanel();
        FlowPanel speedPanel = new FlowPanel();
        FlowPanel repeatPanel = new FlowPanel();
		speedPanel.setStyleName("optionsPanel rows");
		repeatPanel.setStyleName("optionsPanel rows");
        speedPanel.add(getLabel());
        speedPanel.add(tfAnimSpeed);
        repeatPanel.add(modeLabel);
        repeatPanel.add(getListBox());
		// mainPanel.add(LayoutUtil.panelRow(speedPanel, repeatPanel));
		mainPanel.add(LayoutUtilW.panelRowVertical(getLabel(), tfAnimSpeed));
		mainPanel.add(LayoutUtilW.panelRowVertical(modeLabel, getListBox()));
        setWidget(mainPanel);

        tfAnimSpeed.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doActionPerformed();	    
				}
			}

		});

        tfAnimSpeed.addFocusListener(new FocusListenerW(this){

			@Override
			protected void wrapFocusGained(){
				FieldHandler.focusGained(tfAnimSpeed, app);
			}
			
			@Override
			protected void wrapFocusLost(){
				doActionPerformed();
				FieldHandler.focusLost(tfAnimSpeed, app);
			}	
		});
        tfAnimSpeed.requestToShowSymbolButton();

	}
	
	private void doActionPerformed() {
		GeoNumberValue animSpeed = 
			app.getKernel().getAlgebraProcessor().evaluateToNumeric(tfAnimSpeed.getText(), false);
		if (animSpeed != null) {
			model.applySpeedChanges(animSpeed);
		}
		
	}

	@Override
	public void setLabels() {
		super.setLabels();
		Localization loc = app.getLocalization();
		getLabel().setText(loc.getPlain("AnimationSpeed") + ":");
		modeLabel.setText(loc.getPlain("Repeat") + ": ");
	}

	public void setText(String text) {
		tfAnimSpeed.setText(text);
    }

	public void setPartOfSliderPanel() {
	    model.setShowSliders(true);
	    
    }
	
}
