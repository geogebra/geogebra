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
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialogW extends DialogBoxW implements
		GeoElementSelectionListener, HasKeyboardPopup {

	private AutoCompleteTextFieldW tfCaption;
	private Button btOK;
	private Button btCancel;
	private FlowPanel optionPane;
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
	public CheckboxCreationDialogW(AppW app, GPoint loc2,
			GeoBoolean geoBoolean) {
		super(false, false, null, app.getPanel(), app);

		this.app = app;
		this.loc = app.getLocalization();
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
			
			@Override
			public void onChange(ChangeEvent event) {
				GeoElement geo = gbObjects.getSelectedGeo();
				if (geo != null) {
					gbList.add(geo);
				}
			}
		});
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		gbList.add(geo);
	}

	protected void createGUI(String title) {
		addStyleName("GeoGebraPopup");
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("Checkbox");
		}
		getCaption().setText(title);
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
		
		btnRemove.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GeoElement geo = gbList.getSelectedGeo();
				if (geo != null) {
					gbObjects.add(geo);
					gbList.remove(geo);
				}
			}
		});
		// buttons
		btOK = new Button(loc.getMenu("OK"));
		btOK.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				actionPerformed(btOK);
			}
		});
		
		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.addStyleName("cancelBtn");
		btCancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				actionPerformed(btCancel);
			}
		});
		
		FlowPanel btPanel = new FlowPanel();
		btPanel.setStyleName("DialogButtonPanel");

		btPanel.add(btOK);
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

	/**
	 * Handle input event
	 * 
	 * @param src
	 *            input element
	 */
	public void actionPerformed(Object src) {
		if (src == btCancel) {
			hide();
		} else if (src == btOK) {
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
				// app.setMoveMode();
				app.getSelectionManager().addSelectionListener(this);
			} else {
				app.getSelectionManager().removeSelectionListener(this);
				app.setSelectionListenerMode(null);
				app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			}
		}
		// if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
		// if (flag) {
		// app.registerPopup(this);
		// } else {
		// app.unregisterPopup(this);
		// }
		// }

		super.setVisible(flag);
	}

}
