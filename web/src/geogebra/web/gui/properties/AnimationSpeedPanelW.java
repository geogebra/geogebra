package geogebra.web.gui.properties;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import geogebra.common.gui.dialog.options.model.AnimationSpeedModel.IAnimationSpeedListener;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AnimationSpeedPanelW extends ListBoxPanel implements IAnimationSpeedListener {
	private AutoCompleteTextFieldW tfAnimSpeed;
	private Label modeLabel;
	private AnimationSpeedModel model;
	private AppW app;
	
	public AnimationSpeedPanelW(AppW app) {
        super(app.getLocalization(), app.getPlain("AnimationSpeed") + ": ");
        this.app = app;
    	model = new AnimationSpeedModel(app, this) ;
    	setModel(model);
    	modeLabel = new Label();

		InputPanelW inputPanel = new InputPanelW(null, app, -1, false);
		tfAnimSpeed = inputPanel.getTextComponent();
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(getLabel());
        mainPanel.add(tfAnimSpeed);
        mainPanel.add(modeLabel);
        mainPanel.add(getListBox());
        setWidget(mainPanel);

        tfAnimSpeed.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doActionPerformed();	    
				}
			}

		});

        tfAnimSpeed.addFocusListener(new FocusListener(this){

			@Override
			protected void wrapFocusGained(){
			}
			
			@Override
			protected void wrapFocusLost(){
					doActionPerformed();
			}	
		});

	}
	
	private void doActionPerformed() {
		NumberValue animSpeed = 
			app.getKernel().getAlgebraProcessor().evaluateToNumeric(tfAnimSpeed.getText(), false);
		if (animSpeed != null) {
			model.applySpeedChanges(animSpeed);
		}
		
	}

	@Override
	public void setLabels() {
		super.setLabels();
		modeLabel.setText(app.getPlain("Repeat") + ": ");
	}

	public void setText(String text) {
		tfAnimSpeed.setText(text);
    }

	public void setPartOfSliderPanel() {
	    model.setShowSliders(true);
	    
    }
	
}
