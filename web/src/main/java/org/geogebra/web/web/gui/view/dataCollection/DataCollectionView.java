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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.DataCollectionSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.gui.view.dataCollection.GeoListBox.DefaultEntries;
import org.geogebra.web.web.gui.view.dataCollection.Settings.AccSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.LightSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.LoudnessSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.MagFieldSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.OrientationSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.ProxiSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.SensorSetting;
import org.geogebra.web.web.gui.view.dataCollection.Settings.TimeSetting;
import org.geogebra.web.web.main.AppWFull;

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
		ChangeHandler, SettingListener {

	private final String DATA_CONNECTION = "DataConnection";
	private final String DATA_SHARING_CODE = "DataSharingCode";
	private final String CONNECTION_FAILD = "DataConnectionFailed";
	private final String CONNECTING = "Connecting";
	private final String FREQUENCY = "FrequencyHz";

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

	/** panels which are shown or hidden depending on connection-status */
	private FlowPanel connectionStatusPanel;
	private FlowPanel freqPanel;

	private MyToggleButton2 connectButton;
	private TextBox appIDTextBox;

	/** different settings for the available sensors */
	private AccSetting acc;
	private MagFieldSetting magField;
	private TimeSetting time;
	private OrientationSetting orientation;
	private ProxiSetting proxi;
	private LightSetting light;
	private LoudnessSetting loudness;

	/** widgets which need translation */
	private Label connectionLabel;
	private Label appID;
	private Label connectionFailed;
	private Label connecting;
	private Label frequency;

	/** holds sensor settings (e.g. Accelerometer x logs to function f) */
	private DataCollectionSettings settings;

	/** current used frequency */
	private int freqHz;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public DataCollectionView(AppW app) {
		this.app = app;
		this.addStyleName("dataCollectionView");
		this.settings = app.getSettings().getDataCollection();
		this.settings.addListener(this);


		createGUI();
		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				handleClick();
			}
		}, ClickEvent.getType());

		settingsChanged(this.settings);
	}

	/**
	 * attaches this view to kernel
	 */
	public void attachView() {
		app.getKernel().attach(this);
	}

	/**
	 * handles a click on this view. used to recognize if this view gains focus.
	 */
	void handleClick() {
		this.app.setActiveView(App.VIEW_DATA_COLLECTION);
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
				app.getGuiManager()
						.setShowView(false, App.VIEW_DATA_COLLECTION);
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
		addOrientation();
		addMagneticField();
		addProximity();
		addLight();
		addTime();
		// addLoudness(); not in use now
		this.dataCollectionTab.add(this.sensorSettings);
	}


	private void addConnection() {
		FlowPanel connection = new FlowPanel();
		connection.addStyleName("connection");

		// Caption
		FlowPanel connectionCaption = new FlowPanel();
		connectionCaption.addStyleName("panelTitle");
		this.connectionLabel = new Label("Connection with GeoGebra Data App");
		connectionCaption.add(this.connectionLabel);
		connection.add(connectionCaption);

		// Content
		FlowPanel setting = new FlowPanel();
		setting.addStyleName("panelIndent");
		addSharingCode(setting);
		addConnectionStatus(setting);
		addFrequency(setting);
		connection.add(setting);

		this.dataCollectionTab.add(connection);
	}

	private void addConnectionStatus(FlowPanel setting) {
		this.connectionStatusPanel = new FlowPanel();
		this.connectionStatusPanel.setVisible(false);
		this.connectionStatusPanel.addStyleName("rowContainer");
		this.connectionFailed = new Label(this.app.getMenu(CONNECTION_FAILD));
		this.connectionFailed.setVisible(false);
		this.connecting = new Label(this.app.getMenu(CONNECTING));
		this.connecting.setVisible(false);
		this.connectionStatusPanel.add(this.connectionFailed);
		this.connectionStatusPanel.add(this.connecting);
		setting.add(this.connectionStatusPanel);
	}

	/**
	 * adds SharingCode to the given panel
	 * 
	 * @param setting
	 */
	private void addSharingCode(FlowPanel setting) {
		FlowPanel appIDpanel = new FlowPanel();
		appIDpanel.addStyleName("rowContainer");
		appIDpanel.addStyleName("sharingCodePanel");
		this.appID = new Label("GeoGebra Data Sharing Code:");
		this.appIDTextBox = new TextBox();
		this.appIDTextBox.addStyleName("appIdTextBox");
		this.appIDTextBox.addKeyDownHandler(new KeyDownHandler() {
		
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (appIDTextBox.getText().length() > 0) {
						connectButton.setDown(true);
						appIDTextBox.setFocus(false);
						handleConnectionClicked();
					}
				}
			}
		});

		Image imgON = new Image(GuiResources.INSTANCE.datacollection_on());
		Image imgOFF = new Image(GuiResources.INSTANCE.datacollection_off());
		this.connectButton = new MyToggleButton2(imgOFF, imgON);
		this.connectButton.addStyleName("connectButton");
		this.connectButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (appIDTextBox.getText().length() > 0) {
					handleConnectionClicked();
				} else {
					connectButton.setDown(false);
				}
			}
		});

		appIDpanel.add(this.appID);
		appIDpanel.add(this.appIDTextBox);
		appIDpanel.add(this.connectButton);
		setting.add(appIDpanel);
	}

	/**
	 * adds frequency to the given panel
	 * 
	 * @param setting
	 */
	private void addFrequency(FlowPanel setting) {
		this.freqPanel = new FlowPanel();
		this.freqPanel.setVisible(false);
		this.freqPanel.addStyleName("rowContainer");

		this.frequency = new Label();
		this.freqPanel.add(this.frequency);

		setting.add(this.freqPanel);
	}

	/**
	 *  
	 */
	void handleConnectionClicked() {
		if (this.connectButton.isDown()) {
			this.connectionStatusPanel.setVisible(true);
			this.connecting.setVisible(true);
			this.connectionFailed.setVisible(false);
			((AppWFull) this.app).getDataCollection().onConnect(
					this.appIDTextBox.getText().toUpperCase());
		} else {
			this.appIDTextBox.removeStyleName("disabled");
			this.connectionStatusPanel.setVisible(false);
			this.freqPanel.setVisible(false);
			((AppWFull) this.app).getDataCollection().onDisconnect();
			for (SensorSetting setting : this.sensors) {
				setting.setOn(false);
			}
		}
	}

	private void addAccelerometer() {
		this.acc = new AccSetting(this.app, this, "Accelerometer", "m/s\u00B2");
		this.sensors.add(this.acc);
		this.sensorSettings.add(this.acc);
	}

	private void addMagneticField() {
		this.magField = new MagFieldSetting(this.app, this, "MagneticField",
				"\u00B5T");
		this.sensors.add(this.magField);
		this.sensorSettings.add(this.magField);
	}

	private void addTime() {
		this.time = new TimeSetting(this.app, this, "ReceivedData", "ms");
		this.sensors.add(this.time);
		this.sensorSettings.add(this.time);
	}

	private void addOrientation() {
		this.orientation = new OrientationSetting(this.app, this,
				"Orientation", app.getLocalization().getMenu("DegreeUnit"));
		this.sensors.add(this.orientation);
		this.sensorSettings.add(this.orientation);
	}

	private void addProximity() {
		this.proxi = new ProxiSetting(this.app, this, "Proximity", "cm");
		this.sensors.add(this.proxi);
		this.sensorSettings.add(this.proxi);
	}

	private void addLight() {
		this.light = new LightSetting(this.app, this, "Light", "lx");
		this.sensors.add(this.light);
		this.sensorSettings.add(this.light);
	}

	private void addLoudness() {
		this.loudness = new LoudnessSetting(this.app, this, "Loudness", "");
		this.sensors.add(this.loudness);
		this.sensorSettings.add(this.loudness);
	}


	/**
	 * @param sensor
	 *            which sensor
	 * @param flag
	 *            {@code true} if sensor is turned on
	 */
	public void setSensorOn(Types sensor, boolean flag) {
		switch (sensor) {
		case ACCELEROMETER_X:
			this.acc.setOn(flag);
			break;
		case MAGNETIC_FIELD_X:
			this.magField.setOn(flag);
			break;
		case ORIENTATION_X:
			this.orientation.setOn(flag);
			break;
		case PROXIMITY:
			this.proxi.setOn(flag);
			break;
		case LIGHT:
			this.light.setOn(flag);
			break;
		case TIMESTAMP:
			this.time.setOn(flag);
			break;
		// case LOUDNESS:
		// this.loudness.setOn(flag);
		// break;
		default:
			break;
		}
	}

	@Override
	public void setLabels() {
		this.appID.setText(this.app.getMenu(DATA_SHARING_CODE));
		this.connectionLabel.setText(this.app.getMenu(DATA_CONNECTION));
		this.connecting.setText(this.app.getMenu(CONNECTING));
		this.connectionFailed.setText(this.app.getMenu(CONNECTION_FAILD));
		this.frequency.setText(app.getMenu(FREQUENCY) + ": " + this.freqHz);
		for (SensorSetting setting : this.sensors) {
			setting.setLabels();
		}
		updateListBoxes();
	}

	/**
	 * if this view gets focus back, the list of GeoElements has to be updated
	 * because it's possible, that some new GeoElements were created or some
	 * were deleted
	 */
	public void updateGeoList() {
		TreeSet<GeoElement> newTreeSet = this.app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		this.availableObjects.clear();
		// fill list of available objects
		for (GeoElement element : newTreeSet) {
			if ((element instanceof GeoNumeric || (element instanceof GeoFunction
					&& ((GeoFunction) element).getFunctionExpression()
							.getOperation() == Operation.DATA || element instanceof GeoList))
					&& !this.usedObjects.contains(element)) {
				this.availableObjects.add(element);
			}
		}

		this.usedObjects.clear();
		for (SensorSetting setting : this.sensors) {
			for (GeoListBox listBox : setting.getListBoxes()) {
				GeoElement selection = this.settings.getGeoMappedToSensor(
						listBox.getType(), this.app.getKernel()
								.getConstruction());
				if (selection != null && newTreeSet.contains(selection)) {
					this.usedObjects.add(selection);
					this.availableObjects.remove(selection);
				} else {
					//
					this.settings.removeMappedGeo(listBox.getType());
					((AppWFull) this.app).getDataCollection()
							.removeRegisteredGeo(listBox.getType());
				}
			}
		}
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
		int selectedIndex = listBox.getSelectedIndex();

		if (oldSelection != null) {
			setGeoUnused(oldSelection, listBox);
		}

		// get new selection
		GeoElement newSelection = listBox.getGeoElement(listBox
				.getSelectedItemText());

		if (newSelection == null) {

			if (listBox.getValue(selectedIndex) == DefaultEntries.EMPTY_SELECTION
					.getText()) {
				listBox.setSelection(newSelection); // set null, handled in
													// GeoListBox
			} else if (listBox.getValue(selectedIndex) == DefaultEntries.CREATE_NUMBER
					.getText()) {
				newSelection = new GeoNumeric(this.app.getKernel()
						.getConstruction(), null, 0, false);
				((GeoNumeric) newSelection).setDrawable(false);
				listBox.addItem(newSelection);
				setGeoUsed(newSelection, listBox);
			} else if (listBox.getValue(selectedIndex) == DefaultEntries.CREATE_DATA_FUNCTION
					.getText()) {
				// create new data function
				newSelection = CmdDataFunction.emptyFunction(app.getKernel(),
						null)[0];
				listBox.addItem(newSelection);
				setGeoUsed(newSelection, listBox);
			} else if (listBox.getValue(selectedIndex) == DefaultEntries.CREATE_LIST
					.getText()) {
				newSelection = new GeoList(this.app.getKernel()
						.getConstruction());
				listBox.addItem(newSelection);
				setGeoUsed(newSelection, listBox);
			}

		} else {
			setGeoUsed(newSelection, listBox);
		}
		// updateOtherListBoxes(listBox);somehow this sown {} list instead of
		// "time list" in the listbox it was created
		updateListBoxes();
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
			((AppWFull) this.app).getDataCollection().registerGeo(
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
		((AppWFull) this.app).getDataCollection().removeRegisteredGeo(
				listBox.getType());
	}

	/**
	 * updates every ListBox except the given one.
	 * 
	 * @param box
	 */
	private void updateOtherListBoxes(GeoListBox listbox) {
		for (SensorSetting setting : this.sensors) {
			setting.updateOtherBoxes(listbox, this.availableObjects,
					this.usedObjects);
		}
	}

	/**
	 * updates the entries of every box in the list of {@link #listBoxes boxes}
	 */
	private void updateListBoxes() {
		for (SensorSetting setting : this.sensors) {
			setting.updateAllBoxes(this.availableObjects, this.usedObjects);
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
		this.connectionFailed.setVisible(true);
		this.connecting.setVisible(false);
		((AppWFull) this.app).getDataCollection().onDisconnect();
		this.connectButton.setDown(false);
		this.appIDTextBox.setEnabled(true);
		this.appIDTextBox.removeStyleName("disabled");
		this.appIDTextBox.setSelectionRange(0, this.appIDTextBox.getText()
				.length());
		this.appIDTextBox.setFocus(true);

		this.freqPanel.setVisible(false);
	}

	/**
	 * update GUI if entered ID was correct
	 */
	public void onCorrectID() {
		this.connectionStatusPanel.setVisible(false);
		this.appIDTextBox.addStyleName("disabled");
		this.freqPanel.setVisible(true);
		((AppWFull) this.app).getDataCollection()
				.triggerAvailableSensors();
		((AppWFull) this.app).getDataCollection().triggerFrequency();
	}

	/**
	 * updates the label for the used frequency from GeoGebra Data App
	 * 
	 * @param freq
	 *            frequency in Hz
	 */
	public void setFrequency(int freq) {
		this.freqHz = freq;
		this.frequency.setText(app.getMenu(FREQUENCY) + ": " + this.freqHz);
	}

	@Override
	public void add(GeoElement geo) {
		this.updateGeoList();
	}

	@Override
	public void remove(GeoElement geo) {
		this.updateGeoList();
	}

	@Override
	public void rename(GeoElement geo) {
		this.updateGeoList();
	}

	@Override
	public void update(GeoElement geo) {
		this.updateGeoList();
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
		return App.VIEW_DATA_COLLECTION;
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

	/**
	 * returns settings in XML format
	 * 
	 * @param sb
	 *            to store XML
	 * @param asPreference
	 *            asPreference
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
		settings.getXML(sb, asPreference, app.getKernel().getConstruction());
	}

	public DataCollectionSettings getDataSettings() {
		return this.settings;
	}

	/**
	 * only after opening a file
	 */
	public void settingsChanged(AbstractSettings settings) {
		TreeSet<GeoElement> newTreeSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		this.availableObjects.clear();
		// fill list of available objects
		for (GeoElement element : newTreeSet) {
			if ((element instanceof GeoNumeric || (element instanceof GeoFunction
					&& ((GeoFunction) element).getFunctionExpression()
							.getOperation() == Operation.DATA || element instanceof GeoList))) {
				this.availableObjects.add(element);
			}
		}
		this.usedObjects.clear();
		for (SensorSetting setting : this.sensors) {
			for (GeoListBox listBox : setting.getListBoxes()) {
				GeoElement selection = ((DataCollectionSettings) settings)
						.getGeoMappedToSensor(listBox.getType(), app
								.getKernel().getConstruction());
				if (selection != null) {
					this.usedObjects.add(selection);
					this.availableObjects.remove(selection);
				}
			}
		}
		updateListBoxes();
	}

	/**
	 * update the label for the real frequency for the given type of sensor
	 * 
	 * @param sensor
	 *            {@link Types}
	 * @param freq
	 *            int
	 */
	public void setRealFrequency(Types sensor, int freq) {
		switch (sensor) {
		case ACCELEROMETER_X:
			this.acc.setRealFrequency(freq);
			break;
		case MAGNETIC_FIELD_X:
			this.magField.setRealFrequency(freq);
			break;
		case ORIENTATION_X:
			this.orientation.setRealFrequency(freq);
			break;
		case LIGHT:
			this.light.setRealFrequency(freq);
			break;
		case PROXIMITY:
			this.proxi.setRealFrequency(freq);
			break;
		case LOUDNESS:
			this.loudness.setRealFrequency(freq);
			break;
		}
	}
}