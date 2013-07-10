package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.AuxiliaryHeaderPanel;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;

public class SaveBar extends AuxiliaryHeaderPanel {
	private TextBox title;
	private StandardImageButton saveButton;

	public SaveBar(final FileManagerM fm, final App app){
		super(app.getLocalization().getMenu("Save"),app.getLocalization());
		

		this.title = new TextBox();
		///title.setText(app.getKernel().getConstruction().getTitle());
		this.title.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					SaveBar.this.performSave(fm,app);
				}
			}
		});

		this.saveButton = new StandardImageButton(
				CommonResources.INSTANCE.angle());
		this.saveButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SaveBar.this.performSave(fm,app);
			}
		}, ClickEvent.getType());

	}

	protected void performSave(FileManagerM fm, App app) {
		fm.saveFile(this.title.getText(), app);
		TouchEntryPoint.showTabletGUI();
	}

	public void setTitleText(String materialTitle) {
		this.title.setText(materialTitle);
		
	}

}
