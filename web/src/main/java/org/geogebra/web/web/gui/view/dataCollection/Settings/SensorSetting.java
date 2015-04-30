package org.geogebra.web.web.gui.view.dataCollection.Settings;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.web.main.AppWapplication;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;

public abstract class SensorSetting extends FlowPanel implements SetLabels {

	public class GeoListBox extends ListBox {
		private final int EMPTY_SELECTION_INDEX = 0;
		private final int FIRST_GEOELEMENT_INDEX = 2;
		private Types type;
		private GeoElement selection;
		private ArrayList<GeoElement> items = new ArrayList<GeoElement>();
		private SensorSetting sensor;

		/**
		 * @param type
		 *            of sensor which is associated with this listbox
		 * @param sensor
		 *            {@link SensorSetting} to which this GeoListBox belongs
		 */
		public GeoListBox(Types type, SensorSetting sensor) {
			this.sensor = sensor;
			this.type = type;
		}

		/**
		 * @return the associated {@link Types sensor-type}
		 */
		public Types getType() {
			return this.type;
		}

		/**
		 * sets the {@link #selection selected GeoElement}. if {@code elem} is
		 * {@code null}, the selected index of this listbox is set to
		 * {@link #EMPTY_SELECTION_INDEX}.
		 * 
		 * @param elem
		 *            the selected {@link GeoElement}
		 */
		public void setSelection(GeoElement elem) {
			if (elem == null) {
				this.setSelectedIndex(EMPTY_SELECTION_INDEX);
			} else {
				this.setSelectedIndex(this.items.indexOf(elem)
						+ FIRST_GEOELEMENT_INDEX);
			}
			this.selection = elem;
		}

		/**
		 * @return the selected {@link GeoElement}
		 */
		public GeoElement getSelection() {
			return this.selection;
		}

		/**
		 * 
		 * @return the SensorSetting to which this GeoListBox belongs to
		 */
		public SensorSetting getSensorSetting() {
			return this.sensor;
		}

		/**
		 * @param elem
		 *            {@link GeoElement}
		 */
		public void addItem(GeoElement elem) {
			this.items.add(elem);
			super.addItem(elem.getNameDescription());
		}

		/**
		 * looks up in the list of elements displayed for this listbox for the
		 * geoElement with the given name
		 * 
		 * @param name
		 *            text of the selected item
		 * @return the {@link GeoElement} with the given name if it wasn't
		 *         found.
		 */
		public GeoElement getGeoElement(String name) {
			for (GeoElement geo : this.items) {
				if (geo.getNameDescription().equals(name)) {
					return geo;
				}
			}
			return null;
		}

		@Override
		public void clear() {
			this.items = new ArrayList<GeoElement>();
			this.selection = null;
			super.clear();
		}
	}

	private final String EMPTY_SELECTION = "- - -";

	private String captionString;
	/** button to set sensor on and off */
	ToggleButton sensorOnOff;
	private Label captionLabel;
	/** button to collapse/expand settings for this sensor */
	private ToggleButton collapse;

	/**
	 * panel with the name of the sensor data and the the listbox with the
	 * depending geoElement
	 */
	FlowPanel dataValues;
	/** the listBoxes */
	private ArrayList<GeoListBox> listBoxes = new ArrayList<GeoListBox>();

	private AppW app;
	private DataCollectionView view;

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param dataView
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            String
	 */
	public SensorSetting(AppW app, DataCollectionView dataView,
			String captionString) {
		this.captionString = captionString;
		this.app = app;
		this.view = dataView;
		createGUI();
	}

	private void createGUI() {
		this.dataValues = new FlowPanel();
		this.dataValues.addStyleName("panelIndent");

		addCaption();
		addContent();

		this.add(dataValues);
		this.setVisible(false);
	}

	protected abstract void addContent();

	/**
	 * panel with a "sensor-on-off" button and a label with the name of the
	 * sensor. if sensor is turned off, the settings for this sensor disappear
	 * and the connections between sensor values and geoElements are
	 * restored/set back.
	 */
	private void addCaption() {
		FlowPanel caption = new FlowPanel();
		caption.addStyleName("panelTitle");

		this.captionLabel = new Label(captionString);

		collapse = new ToggleButton(
				new Image(GuiResources.INSTANCE.collapse()), new Image(
						GuiResources.INSTANCE.expand()));
		collapse.addStyleName("collapse");
		collapse.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dataValues.setVisible(!collapse.isDown());
			}
		});

		sensorOnOff = new ToggleButton(
				new Image(AppResources.INSTANCE.hidden()), new Image(
						AppResources.INSTANCE.shown()));
		sensorOnOff.addStyleName("sensorOnOffButton");
		sensorOnOff.setDown(true);
		sensorOnOff.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (GeoListBox listbox : listBoxes) {
					if (listbox.getSelection() != null) {
						if (sensorOnOff.isDown()) {
							((AppWapplication) app).getDataCollection()
									.registerGeo(listbox.getType().toString(),
											listbox.getSelection());
						} else {
							((AppWapplication) app).getDataCollection()
									.removeRegisteredGeo(listbox.getType());
						}
					}
				}
			}
		});

		caption.add(sensorOnOff);
		caption.add(this.captionLabel);
		caption.add(collapse);
		this.add(caption);
	}

	/**
	 * @param rowCaption
	 *            caption
	 * @param type
	 *            {@link Types}
	 */
	protected void addRow(String rowCaption, Types type) {
		FlowPanel container = new FlowPanel();
		container.addStyleName("rowContainer");
		container.add(new Label(rowCaption));
		container.add(new Label(app.getPlain("LinkedObject")));

		GeoListBox listBox = new GeoListBox(type, this);
		listBox.addChangeHandler(this.view);

		listBox.addItem(EMPTY_SELECTION);
		this.listBoxes.add(listBox);

		container.add(listBox);
		dataValues.add(container);
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
		box.addItem(EMPTY_SELECTION);
		box.addItem("Create DataFunction");
		if (selectedElem != null && usedObjects.contains(selectedElem)) {
			box.addItem(selectedElem);
			box.setSelection(selectedElem);
			box.setSelectedIndex(box.FIRST_GEOELEMENT_INDEX);
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
		return this.sensorOnOff.isDown();
	}

	/**
	 * @return list of all {@link GeoListBox geoListBoxes} of this sensor
	 */
	public ArrayList<GeoListBox> getListBoxes() {
		return this.listBoxes;
	}

	@Override
	public void setLabels() {

	}
}

