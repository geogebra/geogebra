/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialogW extends ComponentDialog implements
		GeoElementSelectionListener, HasKeyboardPopup {
	private AutoCompleteTextFieldW tfCaption;
	private GeoListBox gbObjects;
	private GeoAttachedListBox gbList;

	private GPoint location;
	private GeoBoolean geoBoolean;

	private LocalizationW loc;

	private class GeoListBox extends ListBox {
		private List<GeoElement> geos;
		
		public GeoListBox(boolean isMultipleSelect) {
			super();
			setMultipleSelect(isMultipleSelect);
			geos = new ArrayList<>();
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
			int idx = geos.lastIndexOf(geo);
			if (idx > 0) {
				remove(geos.lastIndexOf(geo));
			}
		}
		
		public void remove(int idx) {
			removeItem(idx);
			geos.remove(idx);
		}
	}
	
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

	/**
	 * Input Dialog for a GeoBoolean object Make this *not* modal to allow
	 * adding geos by clicking in EV
	 */
	public CheckboxCreationDialogW(AppW app, DialogData data,
			GPoint loc2, GeoBoolean geoBoolean) {
		super(app, data, false, false);
		addStyleName("Checkbox");
		this.loc = app.getLocalization();
		this.location = loc2;
		this.geoBoolean = geoBoolean;
		initLists();
		buildContent();
		setOnPositiveAction(this::apply);
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
		
		gbObjects.addChangeHandler(event -> {
			GeoElement geo = gbObjects.getSelectedGeo();
			if (geo != null) {
				gbList.add(geo);
			}
		});
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		gbList.add(geo);
	}

	protected void buildContent() {
		Label lblSelectObjects = new Label(loc.getMenu("Tool.SelectObjects"));
		lblSelectObjects.setStyleName("panelTitle");
		// create caption panel
		Label captionLabel = new Label(
				app.isUnbundledOrWhiteboard() ? loc.getMenu("Button.Caption")
						: loc.getMenu("Button.Caption") + ":");
		if (app.isUnbundledOrWhiteboard()) {
			captionLabel.addStyleName("coloredLabel");
		}
		String initString = geoBoolean == null ? ""
				: geoBoolean.getCaption(StringTemplate.defaultTemplate);
		InputPanelW ip = new InputPanelW(initString, app, 1, 15, true);
		tfCaption = ip.getTextComponent();
		tfCaption.setAutoComplete(false);
		FlowPanel captionPanel = new FlowPanel();
		if (app.isUnbundledOrWhiteboard()) {
			captionPanel.add(captionLabel);
			captionPanel.add(ip);
		} else {
			captionPanel.add(LayoutUtilW.panelRow(captionLabel, ip));
		}

		FlowPanel listPanel = new FlowPanel();
		listPanel.add(gbObjects);
		gbList.getElement().addClassName("cbCreationList");
		GPushButton btnRemove = new GPushButton(
				new Image(AppResources.INSTANCE.delete_small()));
		listPanel.add(lblSelectObjects);
		listPanel.add(LayoutUtilW.panelRow(gbList, btnRemove));
		
		btnRemove.addClickHandler(event -> {
			GeoElement geo = gbList.getSelectedGeo();
			if (geo != null) {
				gbObjects.add(geo);
				gbList.remove(geo);
			}
		});

		// Create the JOptionPane.
		FlowPanel contentPanel = new FlowPanel();

		// create object list
		contentPanel.add(captionPanel);
		contentPanel.add(listPanel);

		// Make this dialog display it.
		addDialogContent(contentPanel);
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
			app.showError(Errors.CircularDefinition);
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

	@Override
	public void setVisible(boolean flag) {
		if (!isModal()) {
			if (flag) {
				app.getSelectionManager().addSelectionListener(this);
			} else {
				app.getSelectionManager().removeSelectionListener(this);
				app.setSelectionListenerMode(null);
				app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			}
		}
		super.setVisible(flag);
	}
}
