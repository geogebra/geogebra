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
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.ListBox;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialogW extends ComponentDialog implements
		GeoElementSelectionListener, HasKeyboardPopup {
	private ComponentInputField tfCaption;
	private final List<GeoElement> availableObjects = new ArrayList<>();
	private final List<String> availableObjectNames = new ArrayList<>();
	private CompDropDown gbObjects;
	private GeoAttachedListBox gbList;

	private final GPoint location;
	private GeoBoolean geoBoolean;

	private class GeoAttachedListBox extends ListBox {
		private static final int MAX_VISIBLE_ROWS = 6;

		private final List<GeoElement> geos;

		public GeoAttachedListBox() {
			super();
			setMultipleSelect(false);
			geos = new ArrayList<>();
			setVisibleItemCount(MAX_VISIBLE_ROWS);
		}

		public void add(GeoElement geo) {
			if (contains(geo)) {
				return;
			}
			
			if (geo.isEuclidianVisible()) {
				addItem(getDescription(geo));
				geos.add(geo);
				availableObjects.remove(geo);
				rebuildNames();
			}
		}

		public GeoElement getGeoAt(int index) {
			return geos.get(index);
		}

		public GeoElement getSelectedGeo() {
			return getGeoAt(getSelectedIndex());
		}

		protected boolean contains(GeoElement geo) {
			return geos.contains(geo);
		}

		public void remove(GeoElement geo) {
			int idx = geos.lastIndexOf(geo);
			if (idx >= 0) {
				removeItem(idx);
				geos.remove(idx);
			}
		}
	}

	protected static String getDescription(GeoElement geo) {
		String text = geo.getLongDescription();
		if (text.length() < 100) {
			return text;
		}
		else {
			return geo.getNameDescription();
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
		this.location = loc2;
		this.geoBoolean = geoBoolean;
		initLists();
		buildContent();
		setOnPositiveAction(this::apply);
		addAttachHandler(evt -> {
			if (evt.isAttached()) {
				app.getSelectionManager().addSelectionListener(this);
			} else {
				app.getSelectionManager().removeSelectionListener(this);
			}
		});
	}

	private void initLists() {
		// fill combo box with all geos

		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();

		// lists for combo boxes to select input and output objects
		// fill combobox models
		for (GeoElement geo : sortedSet) {
			if (geo.isEuclidianShowable()) {
				availableObjects.add(geo);
			}
		}
		availableObjectNames.add("");
		gbObjects = new CompDropDown((AppW) app, "Tool.SelectObjects", availableObjectNames, -1);
		rebuildNames();
	
		// fill list with all selected geos
		gbList = new GeoAttachedListBox();

		// add all selected geos to list
		ArrayList<GeoElement> selectedGeos = app.getSelectionManager().getSelectedGeos();
		for (GeoElement geo : selectedGeos) {
			gbList.add(geo);
		}
		
		gbObjects.addChangeHandler(() -> {
			GeoElement geo = availableObjects.get(gbObjects.getSelectedIndex());
			if (geo != null) {
				gbList.add(geo);
			}
		});
	}

	private void rebuildNames() {
		availableObjectNames.clear();
		availableObjects.stream()
				.map(CheckboxCreationDialogW::getDescription).forEach(availableObjectNames::add);
		gbObjects.setSelectedIndex(-1);
		gbObjects.setLabels();
		gbObjects.setDisabled(availableObjects.isEmpty());
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (addToSelection) {
			gbList.add(geo);
		}
	}

	protected void buildContent() {
		// create caption panel
		String initString = geoBoolean == null ? ""
				: geoBoolean.getCaption(StringTemplate.defaultTemplate);

		tfCaption = new ComponentInputField((AppW) app, null, "Button.Caption",
				null, initString, -1);
		tfCaption.getTextField().getTextComponent().setAutoComplete(false);

		FlowPanel listPanel = new FlowPanel();
		listPanel.add(gbObjects);
		gbList.getElement().addClassName("cbCreationList");
		StandardButton btnRemove = new StandardButton(MaterialDesignResources
				.INSTANCE.delete_black(), 20);
		listPanel.add(LayoutUtilW.panelRow(gbList, btnRemove));
		
		btnRemove.addFastClickHandler(event -> {
			GeoElement geo = gbList.getSelectedGeo();
			if (geo != null) {
				availableObjects.add(geo);
				gbList.remove(geo);
				rebuildNames();
			}
		});

		// Create the JOptionPane.
		FlowPanel contentPanel = new FlowPanel();

		// create object list
		contentPanel.add(tfCaption);
		contentPanel.add(listPanel);

		// Make this dialog display it.
		addDialogContent(contentPanel);
	}

	private void apply() {
		// create new GeoBoolean
		if (geoBoolean == null) {
			geoBoolean = new GeoBoolean(app.getKernel().getConstruction());
			geoBoolean.setAbsoluteScreenLoc(location.x, location.y);
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
