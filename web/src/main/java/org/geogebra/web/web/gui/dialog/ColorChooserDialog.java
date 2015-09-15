package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ColorChooserW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class ColorChooserDialog extends DialogBoxW implements SetLabels {

	private ColorChooserW colorChooserW; 
	private FlowPanel mainWidget;
	private Button btnOk;
	private Button btnCancel;
	private Button btnReset;
	private GColor selectedColor;
	private AppW app;
	private ColorChangeHandler handler;
	
	public ColorChooserDialog(AppW app, final GColor originalColor, final ColorChangeHandler handler) {
		super(false, true, null, app.getPanel());
		this.app = app;
		this.handler = handler;
		final GDimensionW colorIconSizeW = new GDimensionW(20, 20);
		colorChooserW = new ColorChooserW(app, 400, 210, colorIconSizeW, 4);
		colorChooserW.enableOpacity(false);
		colorChooserW.enableBackgroundColorPanel(false);
		colorChooserW.setSelectedColor(originalColor);
		setSelectedColor(originalColor);
		mainWidget = new FlowPanel();
		mainWidget.add(colorChooserW);
		FlowPanel btnPanel = new FlowPanel();
		btnOk = new Button();
		btnCancel = new Button();
		btnCancel.addStyleName("cancelBtn");
		btnReset = new Button();
		btnReset.addStyleName("resetBtn");
		btnPanel.addStyleName("DialogButtonPanel");
		btnPanel.add(btnCancel);
		btnPanel.add(btnReset);
		btnPanel.add(btnOk);
		mainWidget.add(btnPanel);
		
		btnOk.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
					handler.onColorChange(getSelectedColor());
		            hide();	            
    
			}});
		btnCancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
            }});
		
		btnReset.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				setSelectedColor(originalColor);
				colorChooserW.setSelectedColor(originalColor);
				colorChooserW.update();
			}});
	
		setLabels();

		setWidget(mainWidget);
		
		colorChooserW.addChangeHandler(new ColorChangeHandler() {
			
			public void onForegroundSelected() {
				// TODO Auto-generated method stub
				
			}
			
			public void onColorChange(GColor color) {
				setSelectedColor(color);
			}
			
			public void onClearBackground() {
				// TODO Auto-generated method stub
				
			}
			
			public void onBackgroundSelected() {
				// TODO Auto-generated method stub
				
			}
			
			public void onAlphaChange() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setLabels() {
		this.getCaption().setText(localize("ChooseColor"));
		colorChooserW.setPaletteTitles(localize("RecentColor"), localize("Other"));
		colorChooserW.setPreviewTitle(localize("Preview"));
		btnOk.setText(localize("OK"));
		btnCancel.setText(localize("Cancel"));
		btnReset.setText(localize("Reset"));
    }

	private String localize(final String id) {
		Localization loc = app.getLocalization();
		String txt = loc.getPlain(id);
		if (txt.equals(id)) {
			txt = loc.getMenu(id);
		}
		return txt;
	}
	
	public GColor getSelectedColor() {
	    return selectedColor;
    }
	
	public void setSelectedColor(GColor selectedColor) {
	    this.selectedColor = selectedColor;
    }
	
	public ColorChangeHandler getHandler() {
	    return handler;
    }
	
	public void setHandler(ColorChangeHandler handler) {
	    this.handler = handler;
    }
}