package org.geogebra.web.web.gui.view.dataCollection.Settings;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.web.gui.view.dataCollection.GeoListBox;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;

public abstract class SensorSetting extends FlowPanel implements SetLabels {
	private final String REAL_FREQUENCY = "ActualFrequency";

	/** caption */
	protected String captionString;
	/** Panel with an image to show if sensor is on or off */
	private SimplePanel sensorOnOff;
	/** A label with the caption */
	protected Label captionLabel;
	/** button to collapse/expand settings for this sensor */
	private ToggleButton collapse;

	private Image sensorON;
	private Image sensorOFF;
	private boolean sensorIsOn = false;

	/**
	 * panel with the name of the sensor data and the listbox with the depending
	 * geoElement
	 */
	FlowPanel dataValues;
	/** the listBoxes */
	private ArrayList<GeoListBox> listBoxes = new ArrayList<GeoListBox>();

	/**
	 * the label and string of the caption for translations after changing the
	 * language
	 */
	protected HashMap<Label, String> rowCaptions = new HashMap<Label, String>();

	protected AppW app;
	private DataCollectionView view;
	private String unit;

	private Label realFreqLabel;
	private int realFreq;
	private FlowPanel realFreqContainer;

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param dataView
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            the String to look up for translations
	 * @param unit
	 *            unit of the sensor values
	 */
	public SensorSetting(AppW app, DataCollectionView dataView,
			String captionString, String unit) {
		this.captionString = captionString;
		this.app = app;
		this.view = dataView;
		this.unit = unit;
		createGUI();
	}

	private void createGUI() {
		this.dataValues = new FlowPanel();
		this.dataValues.addStyleName("panelIndent");

		addCaption();
		addFrequencyPanel();
		addContent();

		this.add(dataValues);
	}

	/**
	 * adds a panel to show the "real" frequency
	 */
	protected void addFrequencyPanel() {
		this.realFreqContainer = new FlowPanel();
		this.realFreqContainer.addStyleName("rowContainer");
		this.realFreqLabel = new Label(app.getMenu(REAL_FREQUENCY) + ": "
				+ this.realFreq);
		this.realFreqContainer.add(this.realFreqLabel);
		this.dataValues.add(this.realFreqContainer);
		setRealFreqVisible(false);
	}

	protected abstract void addContent();

	/**
	 * panel with a {@link #sensorOnOff "sensor-on-off" button}, a
	 * {@link #captionLabel label} with the name of the sensor and a
	 * {@link #collapse button} to expand/collapse the settings for this sensor.
	 * if sensor is turned off, it stops logging the data values of this sensor.
	 */
	private void addCaption() {
		FlowPanel caption = new FlowPanel();
		caption.addStyleName("panelTitle");
		this.captionLabel = new Label();
		updateCaptionLabel();

		collapse = new ToggleButton(
				new Image(GuiResources.INSTANCE.collapse()), new Image(
						GuiResources.INSTANCE.expand()));
		collapse.addStyleName("collapse");
		collapse.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dataValues.setVisible(!collapse.isDown());
			}
		});

		sensorOnOff = new SimplePanel();
		sensorON = new Image(AppResources.INSTANCE.shown());
		sensorOFF = new Image(AppResources.INSTANCE.hidden());
		sensorOnOff.add(sensorOFF);
		sensorOnOff.addStyleName("sensorOnOffButton");

		caption.add(sensorOnOff);
		caption.add(this.captionLabel);
		caption.add(collapse);
		this.add(caption);
	}

	/**
	 * A row is used for one "sensor value" (e.g. Ax, Ay or Az). It contains the
	 * name of the sensor value and a {@link GeoListBox} to choose an element to
	 * which the received values should be logged.
	 * 
	 * @param rowCaption
	 *            caption
	 * @param type
	 *            {@link Types}
	 */
	protected void addRow(String rowCaption, Types type) {
		FlowPanel container = new FlowPanel();
		container.addStyleName("rowContainer");
		Label rowCaptionLabel = new Label();
		container.add(rowCaptionLabel);
		rowCaptions.put(rowCaptionLabel, rowCaption);

		GeoListBox listBox = new GeoListBox(type, this, app,
				this.view.getDataSettings());
		listBox.addChangeHandler(this.view);

		this.listBoxes.add(listBox);

		container.add(listBox);
		dataValues.add(container);
		updateContent();
	}

	/**
	 * updates the entries for all {@link GeoListBox listBoxes}
	 * 
	 * @param availableObjects
	 *            {@link ArrayList}
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	public void updateAllBoxes(ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		for (GeoListBox box : this.listBoxes) {
			updateBox(box, availableObjects, usedObjects);
		}
	}

	/**
	 * updates all {@link GeoListBox listBoxes} except the given one.
	 * 
	 * @param listBox
	 *            {@link GeoListBox}
	 * @param availableObjects
	 *            {@link ArrayList}
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	public void updateOtherBoxes(GeoListBox listBox,
			ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		for (GeoListBox box : this.listBoxes) {
			if (box != listBox) {
				updateBox(box, availableObjects, usedObjects);
			}
		}
	}

	/**
	 * updates the entries of the given {@link GeoListBox}
	 * 
	 * @param box
	 * @param availableObjects
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	private void updateBox(GeoListBox box,
			ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		GeoElement selectedElem = box.getSelection();
		box.clear();
		if (selectedElem != null && usedObjects.contains(selectedElem)) {
			box.addItem(selectedElem);
			box.setSelection(selectedElem);
			box.setSelectedIndex(box.getFirstFreeGeoListBoxIndex());
		} else {
			box.setSelection(null);
		}
		for (GeoElement elem : availableObjects) {
			box.addItem(elem);
		}
	}

	/**
	 *
	 * @return {@code true} if sensor is turned on
	 */
	public boolean isOn() {
		return this.sensorIsOn;
	}

	/**
	 * @return list of all {@link GeoListBox geoListBoxes} of this sensor
	 */
	public ArrayList<GeoListBox> getListBoxes() {
		return this.listBoxes;
	}

	@Override
	public void setLabels() {
		updateCaptionLabel();
		// is null for TimeSetting
		if (this.realFreqLabel != null) {
			this.realFreqLabel.setText(app.getMenu(REAL_FREQUENCY) + ": "
					+ this.realFreq);
		}
		updateContent();
	}

	/**
	 * update text of the content
	 */
	private void updateContent() {
		for (Label label : this.rowCaptions.keySet()) {
			label.setText(app.getMenu(this.rowCaptions.get(label)));
		}
	}

	/**
	 * sets the text of the {@link #captionLabel}
	 */
	protected void updateCaptionLabel() {
		this.captionLabel.setText(app.getMenu(captionString) + " ("
				+ app.getMenu(this.unit)
				+ ")");
	}

	/**
	 * updates the label of the "real" frequency
	 * 
	 * @param freq
	 *            int
	 */
	public void setRealFrequency(int freq) {
		this.realFreq = freq;
		this.realFreqLabel.setText(app.getMenu(REAL_FREQUENCY) + ": "
				+ this.realFreq);
	}

	/**
	 * shows/hides the label with the "real" frequency
	 * 
	 * @param visible
	 */
	private void setRealFreqVisible(boolean visible) {
		if (this.realFreqContainer != null) {
			this.realFreqContainer.setVisible(visible);
		}
	}

	/**
	 * if sensor is set to ON the SensorLogger starts logging data for this
	 * sensor.
	 * 
	 * @param flag
	 *            {@code true} to turn sensor ON
	 */
	public void setOn(boolean flag) {
		this.sensorIsOn = flag;
		sensorOnOff.clear();
		setRealFreqVisible(flag);
		if (flag) {
			sensorOnOff.add(sensorON);
			for (GeoListBox listbox : listBoxes) {
				if (listbox.getSelection() != null) {
					((AppWFull) app).getDataCollection().registerGeo(
							listbox.getType().toString(),
							listbox.getSelection());
				}
			}
		} else {
			sensorOnOff.add(sensorOFF);
			for (GeoListBox listbox : listBoxes) {
				((AppWFull) app).getDataCollection()
						.removeRegisteredGeo(listbox.getType());
			}
		}
	}
}