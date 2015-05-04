package org.geogebra.web.web.gui.view.dataCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.CmdDataFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.gui.view.dataCollection.Settings.AccSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.GeoListBox;
import org.geogebra.web.web.gui.view.dataCollection.Settings.GeoListBox.DefaultEntries;
import org.geogebra.web.web.gui.view.dataCollection.Settings.LightSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.LoudnessSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.MagFieldSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.OrientationSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.ProxiSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.SensorSetting;
import org.geogebra.web.web.main.AppWapplication;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class DataCollectionView extends FlowPanel implements View, SetLabels,
		ChangeHandler {

	private AppW app;
	private TabLayoutPanel tabPanel;
	private FlowPanel dataCollectionTab;

	/** GeoElements that are available and not used from another sensor */
	ArrayList<GeoElement> availableObjects = new ArrayList<GeoElement>();
	/** list of all used GeoElements */
	private ArrayList<GeoElement> usedObjects = new ArrayList<GeoElement>();

	/** panel with the available sensors */
	private FlowPanel sensorSettings;
	private ArrayList<SensorSetting> sensors = new ArrayList<SensorSetting>();

	private MyToggleButton2 connectButton;
	private TextBox appIDTextBox;

	/** different settings for the available sensors */
	private AccSetting acc;
	private MagFieldSetting magField;
	private OrientationSetting orientation;
	private ProxiSetting proxi;
	private LightSetting light;
	private LoudnessSetting loudness;

	/** widgets which need translation */
	private Label connectionLabel;
	private Label appID;
	private Label connectedLabel;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public DataCollectionView(AppW app) {
		this.app = app;
		this.addStyleName("dataCollectionView");

		createGUI();
		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				handleClick();
			}
		}, ClickEvent.getType());
	}

	/**
	 * handles a click on this view. used to recognize if this view gains focus.
	 */
	void handleClick() {
		this.app.setActiveView(AppW.VIEW_DATA_COLLECTION);
	}

	private void createGUI() {
		this.tabPanel = new TabLayoutPanel(30, Unit.PX);

		addTabContent();
		this.add(this.tabPanel);
		addCloseButton();
	}

	/**
	 * adds a cross into the upper right corner to close this view
	 */
	private void addCloseButton() {
		FlowPanel closeButtonPanel = new FlowPanel();
		closeButtonPanel.setStyleName("closeButtonPanel");
		StandardButton closeButton = new StandardButton(
				GuiResources.INSTANCE.dockbar_close());
		closeButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.getGuiManager().setShowView(false,
						AppW.VIEW_DATA_COLLECTION);
			}
		});
		closeButtonPanel.add(closeButton);
		this.add(closeButtonPanel);
	}

	/**
	 * adds a new tab to the {@link #tabPanel}
	 */
	private void addTabContent() {
		this.dataCollectionTab = new FlowPanel();
		this.dataCollectionTab.addStyleName("dataCollectionTab");

		addConnection();
		addSettingsPanel();

		this.tabPanel.add(this.dataCollectionTab, "Data Collection");
	}

	private void addSettingsPanel() {
		this.sensorSettings = new FlowPanel();
		this.sensorSettings.addStyleName("sensorSettings");

		updateGeoList();
		addAccelerometer();
		addMagneticField();
		addOrientation();
		addProximity();
		addLight();
		addLoudness();
		this.dataCollectionTab.add(this.sensorSettings);

		setSensorSettingsEnabled(false);
	}

	/**
	 * sets the {@link #sensorSettings} to enabled/disabled depending on the
	 * webSocket connection
	 * 
	 * @param flag
	 *            {@code true} to set the {@link #sensorSettings} enabled
	 */
	void setSensorSettingsEnabled(boolean flag) {
		if (flag) {
			this.sensorSettings.removeStyleName("disabled");
		} else {
			this.sensorSettings.addStyleName("disabled");
		}
	}

	private void addConnection() {
		FlowPanel connection = new FlowPanel();
		connection.addStyleName("connection");

		FlowPanel connectionCaption = new FlowPanel();
		connectionCaption.addStyleName("panelTitle");
		this.connectionLabel = new Label("Connection");
		connectionCaption.add(connectionLabel);
		connection.add(connectionCaption);

		FlowPanel setting = new FlowPanel();
		setting.addStyleName("panelIndent");

		this.appID = new Label("App ID:");
		this.appIDTextBox = new TextBox();
		this.appIDTextBox.addKeyDownHandler(new KeyDownHandler() {
		
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					connectButton.setDown(true);
					appIDTextBox.setFocus(false);
					handleConnectionClicked();
				}
			}
		});

		this.connectedLabel = new Label("Connected...");
		this.connectedLabel.addStyleName("connectedLabel");
		this.connectedLabel.setVisible(false);

		Image imgON = new Image(GuiResources.INSTANCE.datacollection_on());
		Image imgOFF = new Image(GuiResources.INSTANCE.datacollection_off());
		this.connectButton = new MyToggleButton2(imgON, imgOFF);
		this.connectButton.addStyleName("connectButton");
		this.connectButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				handleConnectionClicked();
			}
		});

		setting.add(appID);
		setting.add(appIDTextBox);
		setting.add(connectButton);
		setting.add(connectedLabel);
		connection.add(setting);

		dataCollectionTab.add(connection);
	}

	/**
	 *  
	 */
	void handleConnectionClicked() {
		if (connectButton.isDown()) {
			((AppWapplication) app).getDataCollection().onConnect(
					appIDTextBox.getText());
			connectedLabel.setText("Connected...");
			connectedLabel.setVisible(true);
			setSensorSettingsEnabled(true);
			appIDTextBox.setEnabled(false);
		} else {
			((AppWapplication) app).getDataCollection().onDisconnect();
			connectedLabel.setVisible(false);
			setSensorSettingsEnabled(false);
			appIDTextBox.setEnabled(true);
		}
	}

	private void addAccelerometer() {
		acc = new AccSetting(app, this, "Accelerometer");
		this.sensors.add(acc);
		this.sensorSettings.add(acc);
	}

	private void addMagneticField() {
		this.magField = new MagFieldSetting(app, this, "Magnetic Field");
		this.sensors.add(this.magField);
		this.sensorSettings.add(this.magField);
	}

	private void addOrientation() {
		this.orientation = new OrientationSetting(app, this, "Orientation");
		this.sensors.add(this.orientation);
		this.sensorSettings.add(this.orientation);
	}

	private void addProximity() {
		this.proxi = new ProxiSetting(app, this, "Proximity");
		this.sensors.add(this.proxi);
		this.sensorSettings.add(this.proxi);
	}

	private void addLight() {
		this.light = new LightSetting(app, this, "Light");
		this.sensors.add(this.light);
		this.sensorSettings.add(this.light);
	}

	private void addLoudness() {
		this.loudness = new LoudnessSetting(app, this, "Loudness");
		this.sensors.add(this.loudness);
		this.sensorSettings.add(this.loudness);
	}


	/**
	 * show/hide specific sensor settings depending on the availability of the
	 * sensors
	 * 
	 * @param sensor
	 *            which sensor
	 * @param flag
	 *            {@code true} if sensor is available and the settings should be
	 *            shown
	 */
	public void setVisible(Types sensor, boolean flag) {
		switch (sensor) {
		case ACCELEROMETER_X:
			this.acc.setVisible(flag);
			break;
		case MAGNETIC_FIELD_X:
			this.magField.setVisible(flag);
			break;
		case ORIENTATION_X:
			this.orientation.setVisible(flag);
			break;
		case PROXIMITY:
			this.proxi.setVisible(flag);
			break;
		case LIGHT:
			this.light.setVisible(flag);
			break;
		case LOUDNESS:
			this.loudness.setVisible(flag);
			break;
		default:
			break;
		}
	}

	@Override
	public void setLabels() {

		// this.connectionLabel
		// this.connectedLabel
		// this.appID
		for (SensorSetting setting : this.sensors) {
			setting.setLabels();
		}

		// TODO Auto-generated method stub

	}

	/**
	 * if this view gets focus back, the list of GeoElements has to be updated
	 * because it's possible, that some new GeoElements were created or some
	 * were deleted
	 */
	public void updateGeoList() {
		TreeSet<GeoElement> newTreeSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		this.availableObjects.clear();
		// fill list of available objects
		for (GeoElement element : newTreeSet) {
			if ((element instanceof GeoNumeric || element instanceof GeoFunction)
					&& !this.usedObjects.contains(element)) {
				this.availableObjects.add(element);
			}
		}
		// check if some elements were deleted
		ArrayList<GeoElement> geosToRemove = new ArrayList<GeoElement>();
		for (GeoElement elem : usedObjects) {
			if (!newTreeSet.contains(elem)) {
				// used element was deleted, so remove it
				geosToRemove.add(elem);
				((AppWapplication) app).getDataCollection()
						.removeRegisteredGeo(elem);
			}
		}
		usedObjects.removeAll(geosToRemove);
		updateListBoxes();
	}

	/**
	 * the old selected geo is moved to {@link #availableObjects} and the new
	 * selected geo is moved to {@link #usedObjects}. the chosen geo is removed
	 * from the selectionList of the other listBoxes
	 */
	@Override
	public void onChange(ChangeEvent event) {
		GeoListBox listBox = (GeoListBox) event.getSource();
		// if a geo was selected give it free
		GeoElement oldSelection = listBox.getSelection();

		if (oldSelection != null) {
			setGeoUnused(oldSelection, listBox);
		}

		// get new selection
		GeoElement newSelection = listBox.getGeoElement(listBox
				.getSelectedItemText());

		if (newSelection == null) {

			if (listBox.getSelectedIndex() == DefaultEntries.EMPTY_SELECTION
					.getIndex()) {
				listBox.setSelection(newSelection); // set null, handled in
													// GeoListBox
			} else if (listBox.getSelectedIndex() == DefaultEntries.CREATE_NUMBER
					.getIndex()) {
				newSelection = new GeoNumeric(
						app.getKernel().getConstruction(), null, 0);
				listBox.addItem(newSelection);
				setGeoUsed(newSelection, listBox);
			} else {
				// create new data function
				newSelection = CmdDataFunction.getDataFunction(
						app.getKernel(), null)[0];
				listBox.addItem(newSelection);
				setGeoUsed(newSelection, listBox);
			}

		} else {
			setGeoUsed(newSelection, listBox);
		}
		updateOtherListBoxes(listBox);
	}

	/**
	 * removes the given {@link GeoElement} from the list of
	 * {@link #availableObjects} and adds it to the list of {@link #usedObjects}
	 * . It sets the correct selection of the given {@link GeoListBox} and
	 * starts logging data if the depending sensor is set to ON.
	 * 
	 * @param geo
	 *            {@link GeoElement}
	 * @param listBox
	 *            {@link GeoListBox}
	 */
	private void setGeoUsed(GeoElement geo, GeoListBox listBox) {
		listBox.setSelection(geo);
		this.availableObjects.remove(geo);
		this.usedObjects.add(geo);
		if (listBox.getSensorSetting().isOn()) {
			((AppWapplication) app).getDataCollection().registerGeo(
					listBox.getType().toString(), geo);
		}
	}

	/**
	 * removes the given {@link GeoElement} from the list of
	 * {@link #usedObjects}, adds it to the list of {@link #availableObjects}
	 * and stops logging data.
	 * 
	 * @param geo
	 * @param listBox
	 */
	private void setGeoUnused(GeoElement geo, GeoListBox listBox) {
		this.availableObjects.add(geo);
		this.usedObjects.remove(geo);
		((AppWapplication) app).getDataCollection().removeRegisteredGeo(
				listBox.getType());
	}

	/**
	 * updates every ListBox except the given one.
	 * 
	 * @param box
	 */
	private void updateOtherListBoxes(GeoListBox listbox) {
		for (SensorSetting setting : this.sensors) {
			setting.updateOtherBoxes(listbox, availableObjects, usedObjects);
		}
	}

	/**
	 * updates the entries of every box in the list of {@link #listBoxes boxes}
	 */
	private void updateListBoxes() {
		for (SensorSetting setting : this.sensors) {
			setting.updateAllBoxes(availableObjects, usedObjects);
		}
	}

	/**
	 * @return a hashMap with the specific sensor-string and the depending
	 *         GeoElement
	 */
	public HashMap<Types, GeoElement> getActivedSensors() {
		HashMap<Types, GeoElement> activeSensors = new HashMap<Types, GeoElement>();

		for (SensorSetting setting : this.sensors) {
			if (setting.isOn()) {
				for (GeoListBox listBox : setting.getListBoxes()) {
					if (listBox.getSelectedIndex() != 0) {
						activeSensors.put(listBox.getType(),
								listBox.getSelection());
					}
				}
			}
		}
		return activeSensors;
	}

	/**
	 * update GUI if entered ID was wrong
	 */
	public void onWrongID() {
		setSensorSettingsEnabled(false);
		((AppWapplication) app).getDataCollection().onDisconnect();
		this.connectedLabel.setText("Connection failed");
		this.connectButton.setDown(false);
		this.appIDTextBox.setEnabled(true);
		this.appIDTextBox.setSelectionRange(0, this.appIDTextBox.getText()
				.length());
		this.appIDTextBox.setFocus(true);
	}

	@Override
	public void add(GeoElement geo) {
		// not used
	}

	@Override
	public void remove(GeoElement geo) {
		// not used
	}

	@Override
	public void rename(GeoElement geo) {
		// not used
	}

	@Override
	public void update(GeoElement geo) {
		// not used
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// not used
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not used
	}

	@Override
	public void repaintView() {
		// not used
	}

	@Override
	public boolean suggestRepaint() {
		// not used
		return false;
	}

	@Override
	public void reset() {
		// not used
	}

	@Override
	public void clearView() {
		// not used
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// not used
	}

	@Override
	public int getViewID() {
		// not used
		return 0;
	}

	@Override
	public boolean hasFocus() {
		// not used
		return false;
	}

	@Override
	public boolean isShowing() {
		// not used
		return false;
	}

	@Override
	public void startBatchUpdate() {
		// not used
	}

	@Override
	public void endBatchUpdate() {
		// not used
	}
}

