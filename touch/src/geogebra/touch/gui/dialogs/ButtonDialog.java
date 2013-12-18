package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.elements.InputArea;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.ui.HorizontalPanel;

public class ButtonDialog extends InputDialog implements ButtonPanelListener {

	private InputArea script;
	private HorizontalPanel scriptPanel;
	private boolean redefine = false;
	private ButtonPanel buttonPanel;

	public ButtonDialog(TouchApp app, DialogType type, TouchModel touchModel) {
		super(app, type, touchModel);

		this.script = new InputArea();
		this.scriptPanel = new HorizontalPanel();
		this.scriptPanel.setStyleName("sliderPanel");
		this.scriptPanel.add(this.script);

		this.inputFieldPanel.add(this.scriptPanel);

		this.buttonPanel = new ButtonPanel(this);
		this.contentPanel.add(this.buttonPanel);
		this.buttonPanel.setOKText(this.app.getLocalization().getPlain("Apply"));
		this.buttonPanel.setCancelText(this.app.getLocalization().getPlain("Cancel"));
	}

	public String getScript() {
		return this.script.getText();
	}

	public void setScript(String s) {
		this.script.setText(s);
	}

	@Override
	public void show() {
		super.show();
		this.setInputText("");
		this.script.setText("");
	}

	public boolean isRedefine() {
		return this.redefine;
	}

	public void setRedefine(boolean redefine) {
		this.redefine = redefine;
	}

}
