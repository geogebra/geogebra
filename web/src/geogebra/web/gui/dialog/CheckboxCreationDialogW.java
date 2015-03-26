/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.web.gui.dialog;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;
import geogebra.html5.main.LocalizationW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.view.algebra.InputPanelW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;


/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialogW extends DialogBoxW implements
		GeoElementSelectionListener {

	private class GeoListBox extends ListBox {
		private List<GeoElement> geos;
		
		public GeoListBox(boolean isMultipleSelect) {
			super(isMultipleSelect);
			geos = new ArrayList<GeoElement>(); 
		}

		public GeoListBox() {
			this(false); 
		}

		public GeoElement getGeoAt(int index) {
			return geos.get(index);
		}
		
		public GeoElement getSelectedGeo() {
			return getGeoAt(getSelectedIndex());
		}
		
		protected void add(GeoElement geo) {
			if (geo != null) {
				addTextOfGeo(geo);
			} else {
				addItem("");
			}
			
			geos.add(geo);
		}
			
		protected void addTextOfGeo(GeoElement geo) {
			String text = geo.getLongDescription();
			if (text.length() < 100) {
				addItem(text);
			}
			else {
				addItem(geo.getNameDescription());
			}
		}
		
		protected boolean contains(GeoElement geo) {
			return geos.contains(geo);
		}

		public void remove(GeoElement geo) {
			remove(geos.lastIndexOf(geo));
        }
		
		public void remove(int idx) {
			removeItem(idx);
			geos.remove(idx);
	        
        }
	};
	
	private class GeoAttachedListBox extends GeoListBox {
		private static final int MAX_VISIBLE_ROWS = 6;
		private GeoListBox combo;

		public GeoAttachedListBox(GeoListBox combo) {
			super();
			setVisibleItemCount(MAX_VISIBLE_ROWS);
			this.combo = combo;
		}

		@Override
		public void add(GeoElement geo) {
			if (contains(geo)) {
				return;
			}
			
			if (geo.isEuclidianVisible()) {
				super.add(geo);
				combo.remove(geo);
			}
		}

	}
	private AutoCompleteTextFieldW tfCaption;
	private Button btApply, btCancel;
	private PushButton btnRemove;
	private FlowPanel optionPane, btPanel;
	private GeoListBox gbObjects;
	private GeoAttachedListBox gbList;
	
	private GPoint location;
	private AppW app;
	private GeoBoolean geoBoolean;

	private LocalizationW loc;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CheckboxCreationDialogW(AppW app, GPoint loc2,
			GeoBoolean geoBoolean) {
		this.app = app;
		this.loc = (LocalizationW) app.getLocalization();
		this.location = loc2;
		this.geoBoolean = geoBoolean;
		initLists();
		createGUI(loc.getMenu("CheckBoxTitle"));
		center();
	}

	private void initLists() {
		// fill combo box with all geos
		gbObjects = new GeoListBox();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();

		// lists for combo boxes to select input and output objects
		// fill combobox models
		Iterator<GeoElement> it = sortedSet.iterator();
		gbObjects.add(null);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isEuclidianShowable()) {
				gbObjects.add(geo);
			}
		}
	
		// fill list with all selected geos
		
		gbList = new GeoAttachedListBox(gbObjects);

		// add all selected geos to list
		for (int i = 0; i < app.getSelectionManager().getSelectedGeos().size(); i++) {
			GeoElement geo = app.getSelectionManager().getSelectedGeos().get(i);
			gbList.add(geo);
		}
		
		gbObjects.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				GeoElement geo = gbObjects.getSelectedGeo();
				if (geo != null) {
					gbList.add(geo);
				}
			}
		});
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		gbList.add(geo);
	}

	protected void createGUI(String title) {
		addStyleName("GeoGebraPopup");
		getCaption().setText(title);
		Label lblSelectObjects = new Label(loc.getMenu("Tool.SelectObjects"));
		lblSelectObjects.setStyleName("panelTitle");
		// create caption panel
		Label captionLabel = new Label(loc.getMenu("Button.Caption") + ":");
		String initString = geoBoolean == null ? "" : geoBoolean.getCaption(StringTemplate.defaultTemplate);
		InputPanelW ip = new InputPanelW(initString, app, 1, 15, true);
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW atf = (AutoCompleteTextFieldW) tfCaption;
			atf.setAutoComplete(false);
		}
		tfCaption.showPopupSymbolButton(true);
		FlowPanel captionPanel = new FlowPanel();
		captionPanel.add(LayoutUtil.panelRow(captionLabel, ip));

		FlowPanel listPanel = new FlowPanel();
		listPanel.add(gbObjects);
		gbList.getElement().setId("cbCreationList");
		btnRemove = new PushButton(new Image(AppResources.INSTANCE.delete_small()));
		listPanel.add(lblSelectObjects);
		listPanel.add(LayoutUtil.panelRow(gbList, btnRemove));
		
		btnRemove.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				GeoElement geo = gbList.getSelectedGeo();
				if (geo != null) {
					gbObjects.add(geo);
					gbList.remove(geo);
				}
			}
		});
		// buttons
		btApply = new Button(loc.getPlain("Apply"));
		btApply.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				actionPerformed(btApply);
			}
		});
		
		btCancel = new Button(loc.getPlain("Cancel"));
		btCancel.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				actionPerformed(btCancel);
			}
		});
		
		btPanel = new FlowPanel();
		btPanel.setStyleName("DialogButtonPanel");

		btPanel.add(btApply);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new FlowPanel();

		// create object list
		optionPane.add(captionPanel);
		optionPane.add(listPanel);
		optionPane.add(btPanel);

		// Make this dialog display it.
		setWidget(optionPane);
		
	}

	public void actionPerformed(Object src) {
		if (src == btCancel) {
			hide();
		} else if (src == btApply) {
			apply();
			hide();
		}
	}

	private void apply() {
		// create new GeoBoolean
		if (geoBoolean == null) {
			geoBoolean = new GeoBoolean(app.getKernel().getConstruction());
			geoBoolean.setAbsoluteScreenLoc(location.x, location.y, true);
			geoBoolean.setLabel(null);
		}

		// set visibility condition for all GeoElements in list
		try {
			for (int i = 0; i < gbList.getItemCount(); i++) {
				GeoElement geo = gbList.getGeoAt(i);
				geo.setShowObjectCondition(geoBoolean);
			}
		} catch (CircularDefinitionException e) {
			app.showError("CircularDefinition");
		}

		// set caption text
		String strCaption = tfCaption.getText().trim();
		if (strCaption.length() > 0) {
			geoBoolean.setCaption(strCaption);
		}

		// update boolean (updates visibility of geos from list too)
		geoBoolean.setValue(true);
		geoBoolean.setEuclidianVisible(true);
		geoBoolean.setLabelVisible(true);
		geoBoolean.updateRepaint();

		app.storeUndoInfo();
	}

	public void windowGainedFocus() {
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER
				|| app.getCurrentSelectionListener() != this) {
			app.setSelectionListenerMode(this);
		}
	}

	@Override
	public void setVisible(boolean flag) {
		if (!isModal()) {
			if (flag) { // set old mode again
			} else {
				app.setSelectionListenerMode(null);
				app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			}
		}
		super.setVisible(flag);
	}


}
