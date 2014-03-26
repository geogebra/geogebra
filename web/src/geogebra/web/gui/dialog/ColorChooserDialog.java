package geogebra.web.gui.dialog;

import geogebra.common.gui.SetLabels;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.util.ColorChangeHandler;
import geogebra.html5.gui.util.ColorChooserW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class ColorChooserDialog extends PopupPanel implements SetLabels {

	private ColorChooserW colorChooserW; 
	private FlowPanel mainWidget;
	private Button btnOk;
	private Button btnCancel;
	private Button btnReset;
	private AppW app;
	
	public ColorChooserDialog(AppW app, boolean autoHide, boolean modal) {
		super(autoHide, modal);
		this.app = app;
		final GDimensionW colorIconSizeW = new GDimensionW(20, 20);
		colorChooserW = new ColorChooserW(app, 400, 210, colorIconSizeW, 4);
		colorChooserW.enableOpacity(false);
		colorChooserW.enableBackgroundColorPanel(false);
		
		mainWidget = new FlowPanel();
		mainWidget.add(colorChooserW);
		FlowPanel btnPanel = new FlowPanel();
		btnOk = new Button();
		btnCancel = new Button();
		btnReset = new Button();
		btnPanel.addStyleName("DialogButtonPanel");
		btnPanel.add(btnOk);
		btnPanel.add(btnCancel);
		btnPanel.add(btnReset);
		mainWidget.add(btnPanel);
		
		btnOk.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
//				if (listener != null) {
//					listener.onCustomColor(getColor());
//				}
		            hide();	            
    
			}});
		btnCancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
            }});
		
		btnReset.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
	        }});
	
		setLabels();

		setWidget(mainWidget);
		
		colorChooserW.addChangeHandler(new ColorChangeHandler(){

			public void onColorChange() {
	            // TODO Auto-generated method stub
	            
            }

			public void onAlphaChange() {
	            // TODO Auto-generated method stub
	            
            }

			public void onClearBackground() {
	            // TODO Auto-generated method stub
	            
            }

			public void onForegroundSelected() {
	            // TODO Auto-generated method stub
	            
            }

			public void onBackgroundSelected() {
	            // TODO Auto-generated method stub
	            
            }

		});
	}
	public ColorChooserDialog(boolean autoHide) {
		super(autoHide);
		// TODO Auto-generated constructor stub
	}

	public ColorChooserDialog(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		// TODO Auto-generated constructor stub
	}
	
	public void setLabels() {
		setTitle(localize("ChooseColor"));
		//titleLabel.setText(localize("ChooseColor"));
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
}