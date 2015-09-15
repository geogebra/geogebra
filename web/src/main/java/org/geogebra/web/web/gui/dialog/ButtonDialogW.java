package org.geogebra.web.web.gui.dialog;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ScriptArea;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonDialogW extends DialogBoxW implements ClickHandler{

	private AutoCompleteTextFieldW tfCaption; 
	private FlowPanel btPanel;
	private ButtonDialogModel model;
	private Button btApply, btCancel;
	private FlowPanel optionPane;
	private AppW app;
	private GeoButton button = null;
	private ScriptArea tfScript;
	
	public ButtonDialogW(AppW app, int x, int y, boolean textField) {
		super(false, true, null, app.getPanel());
		
		this.app = app;		
		model = new ButtonDialogModel(app, x, y, textField);
		addStyleName("GeoGebraPopup");
		createGUI();	
		this.setGlassEnabled(true);
		this.setVisible(true);
		center();
	}

	private void createGUI() {
		if (model.isTextField()) {
			this.getCaption().setText(app.getMenu("TextFieldAction"));
		}
		else {
			this.getCaption().setText(app.getMenu("ButtonAction"));
		}
		
		// create caption panel
		Label captionLabel = new Label(app.getMenu("Button.Caption")+":");
		
		String initString = model.getInitString();
		InputPanelW ip = new InputPanelW(initString, app, 1, 25, true);				
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW atf = (AutoCompleteTextFieldW) tfCaption;
			atf.setAutoComplete(false);
		}
		
		VerticalPanel captionPanel = new VerticalPanel();
		captionPanel.add(captionLabel);
		captionPanel.add(ip);
		captionPanel.addStyleName("captionPanel");
		//captionLabel.getElement().getParentElement().addClassName("tdForCaptionLabel");
		//captionLabel.getElement().getParentElement().setAttribute("style","vertical-align: middle");
				
		// combo box to link GeoElement to TextField
//		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction().
									getGeoSetNameDescriptionOrder();			
		
		final ListBox cbAdd = new ListBox();
		cbAdd.addItem("");
		
		if (model.isTextField()) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			Iterator<GeoElement> it = sortedSet.iterator();

			while (it.hasNext()) {
				GeoElement geo = it.next();				
				if (!geo.isGeoImage() && !(geo.isGeoButton()) && !(geo.isGeoBoolean())) {
//					comboModel.addElement(geo);
					String str = geo.toString(StringTemplate.defaultTemplate);
					cbAdd.addItem(str);
//					if (width < fm.stringWidth(str))
//						width = fm.stringWidth(str);
				}
			}	

			if (cbAdd.getItemCount() > 1) {
				cbAdd.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
						String text = cbAdd.getItemText(cbAdd.getSelectedIndex());
						if("".equals(text.trim())){
							model.setLinkedGeo(null);
						}
						GeoElement geo = getGeo(text);
						if (geo==null) return;
						model.setLinkedGeo(geo);
                    }
					
					public GeoElement getGeo(String text) {
						TreeSet<GeoElement> sortedSet = app.getKernel()
						        .getConstruction()
						        .getGeoSetNameDescriptionOrder();
						Iterator<GeoElement> it = sortedSet.iterator();
						while (it.hasNext()) {
							GeoElement geo = it.next();
							if (text.equals(geo
							        .toString(StringTemplate.defaultTemplate)))
								return geo;

						} 
						return null;
					}
					
				});
			}
		}

		Label scriptLabel = new Label(app.getPlain("Script") + ":");

		tfScript = new ScriptArea();
		
		FlowPanel scriptPanel = new FlowPanel();
		scriptPanel.add(scriptLabel);
		scriptPanel.add(tfScript);

		VerticalPanel linkedPanel = new VerticalPanel();
		Label linkedLabel = new Label(app.getPlain("LinkedObject")+":");
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);
		
		// buttons
		btApply = new Button(app.getPlain("Apply"));
		btApply.getElement().setAttribute("action","Apply");
		btApply.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().setAttribute("action","Cancel");
		btCancel.addClickHandler(this);
		btCancel.addStyleName("cancelBtn");
		btPanel = new FlowPanel();
		btPanel.add(btCancel);
		btPanel.add(btApply);
		btPanel.addStyleName("DialogButtonPanel");
			
		optionPane = new FlowPanel();
		
		// create object list
		optionPane.add(captionPanel);

		if (model.isTextField()) {
			optionPane.add(linkedPanel);
		}
		else {
			optionPane.add(scriptPanel);
		}
		
		optionPane.add(btPanel);	
		//Make this dialog display it.	
		setWidget(optionPane);
		this.addStyleName("buttonDialog");
		//this.getElement().getElementsByTagName("table").getItem(0).setAttribute("cellpadding", "5px");
    }

	public void onClick(ClickEvent event) {
//	    AbstractApplication.debug(((Widget) event.getSource()).getElement().getAttribute("action"));
	
		Object source = event.getSource();				
		if (source == btApply) {	
			model.apply(tfCaption.getText(), tfScript.getText());
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		else if (source == btCancel) {
			model.cancel();
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		
		
    }

}
