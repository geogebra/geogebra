package geogebra.web.gui.view.data;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataDisplayModel;
import geogebra.common.gui.view.data.DataDisplayModel.IDataDisplayListener;
import geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoFrequencyTable;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.CardPanel;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.gui.util.Slider;
import geogebra.html5.main.GlobalKeyDispatcherW;
import geogebra.html5.main.LocalizationW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayPanelW extends FlowPanel implements /*ActionListener,
		FocusListener,*/ StatPanelInterfaceW, IDataDisplayListener {
	private static final long serialVersionUID = 1L;


	// ggb fields
	private AppW app;
	private final LocalizationW loc;
	// privateDataAnalysisViewD daView;
	private DataDisplayModel model;
	// data view mode
	// display panels
	private FlowPanel displayCardPanel;
	private FlowPanel metaPlotPanel, plotPanelNorth, plotPanelSouth;
	private PlotPanelEuclidianViewW plotPanel;

	private Label imageContainer;

	// control panel
	private FlowPanel controlPanel;
	private FlowPanel controlCards;
	private boolean hasControlPanel = true;
	private ListBox lbDisplayType;
	private List<PlotType> plotTypes;
	// options button and sidebar panel
	private OptionsPanelW optionsPanel;
	private MyToggleButton2 btnOptions;

	// numClasses panel
	// private int numClasses = 6;
	private FlowPanel numClassesPanel;
	private Slider sliderNumClasses;

	// manual classes panel
	private FlowPanel manualClassesPanel;
	private Label lblStart;
	private Label lblWidth;
	private AutoCompleteTextFieldW fldStart;
	private AutoCompleteTextFieldW fldWidth;

	// stemplot adjustment panel
	private FlowPanel stemAdjustPanel;
	private Label lblAdjust;
	private Button minus;
	private Button none;
	private Button plus;

	private FlowPanel imagePanel;

	private Label lblTitleX, lblTitleY;
	private AutoCompleteTextFieldW fldTitleX, fldTitleY;
	private FrequencyTablePanelW frequencyTable;
	private MyToggleButton2 btnExport;
	private AutoCompleteTextFieldW fldNumClasses;

	private DataAnalysisModel daModel;

	private ScheduledCommand exportToEVAction;

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daView
	 *            daView
	 */
	public DataDisplayPanelW(DataAnalysisViewW daView) {

		this.app = daView.getApp();
		this.loc = (LocalizationW) app.getLocalization();
		daModel = daView.getModel();
		setModel(new DataDisplayModel(daModel, this));
		// create the GUI
		createGUI();

	}

	/**
	 * Sets the plot to be displayed and the GUI corresponding to the given data
	 * analysis mode
	 * 
	 * @param plotIndex
	 *            the plot to be displayed
	 * @param mode
	 *            the data analysis mode
	 */
	public void setPanel(PlotType plotIndex, int mode) {
		getModel().updatePlot(plotIndex, mode);
		setLabels();
		getModel().updatePlot(true);
		optionsPanel.setVisible(false);
		btnOptions.setValue(false);

	}

	// ==============================================
	// GUI
	// ==============================================

	private void createGUI() {

		// create options button
		btnOptions = new MyToggleButton2(new Image(AppResources.INSTANCE.inputhelp_left_18x18().getSafeUri().asString()));
		// optionsButton.setIcon(app.getImageIcon("view-properties16.png"));
//		btnOptions.setIcon();
//		btnOptions.setSelectedIcon(app
//				.getImageIcon("inputhelp_right_18x18.png"));

		// optionsButton.setPreferredSize(new
		// Dimension(optionsButton.getIcon().getIconWidth(),18));
//		btnOptions.setMargin(new Insets(0, 0, 0, 0));
//		btnOptions.addActionListener(this);

		// create export button
		btnExport = new MyToggleButton2(new Image(AppResources.INSTANCE.export().getSafeUri().asString()));
		// btnExport.setPreferredSize(new
		// Dimension(btnExport.getIcon().getIconWidth(),18));

		// create control panel
		if (hasControlPanel) {

			// create sub-control panels
			createDisplayTypeComboBox();
			createNumClassesPanel();
			createManualClassesPanel();
			createStemPlotAdjustmentPanel();
			FlowPanel emptyControl = new FlowPanel();
			emptyControl.add(new Label("  "));

			// put sub-control panels into a card layout
			controlCards = new CardPanel();
			controlCards.add(numClassesPanel);
//			controlCards.add(manualClassesPanel);
			controlCards.add(stemAdjustPanel);
			controlCards.add(emptyControl);

			// control panel
			controlPanel = new FlowPanel();
			controlPanel.add(lbDisplayType);
			controlPanel.add(controlCards);
			controlPanel.add(LayoutUtil.panelRow(btnOptions, btnExport));
		}

		createExportToEvAction();
		plotPanel = new PlotPanelEuclidianViewW(app.getKernel(), exportToEVAction);

		plotPanelNorth = new FlowPanel();
		plotPanelSouth = new FlowPanel();
		GColor bgColor = plotPanel.getBackgroundCommon();
//		plotPanelNorth.setBackground(bgColor);
//		plotPanelSouth.setBackground(bgColor);
		lblTitleX = new Label();
		lblTitleY = new Label();
		
		fldTitleX = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldTitleY = (new InputPanelW(null, app, -1, false)).getTextComponent();
//		fldTitleY = new MyTextField(app, 20);
		fldTitleX.setEditable(false);
		fldTitleY.setEditable(false);
//		fldTitleX.setBackground(Color.white);
//		fldTitleY.setBackground(Color.white);

		metaPlotPanel = new FlowPanel();
		metaPlotPanel.add(plotPanel.getComponent());

		createImagePanel();

		// put display panels into a card layout

		displayCardPanel = new FlowPanel();
		//displayCardPanel.setBackground(bgColor);

		displayCardPanel.add(metaPlotPanel);
		displayCardPanel.add(new ScrollPanel(imagePanel));

		// create options panel
		optionsPanel = new OptionsPanelW(app, daModel, getModel().getSettings());
//		optionsPanel.addPropertyChangeListener("settings",
//				new PropertyChangeListener() {
//					public void propertyChange(PropertyChangeEvent evt) {
//						getModel().updatePlot(true);
//					}
//				});
		optionsPanel.setVisible(false);

		frequencyTable = new FrequencyTablePanelW(app);

		// =======================================
		// put all the panels together

		FlowPanel mainPanel = new FlowPanel();

		if (hasControlPanel) {
			mainPanel.add(controlPanel);
		}
		mainPanel.add(displayCardPanel);
		mainPanel.add(optionsPanel);

		add(mainPanel);

	}

	/**
	 * Sets the labels to the current language
	 */
	public void setLabels() {

		createDisplayTypeComboBox();
//		sliderNumClasses.setToolTipText(loc.getMenu("Classes"));
//		fldNumClasses.setToolTipText(loc.getMenu("Classes"));
		lblStart.setText(loc.getMenu("Start") + " ");
		lblWidth.setText(loc.getMenu("Width") + " ");
		if (daModel.isRegressionMode()) {
			lblTitleX.setText(loc.getMenu("Column.X") + ": ");
			lblTitleY.setText(loc.getMenu("Column.Y") + ": ");
		}
		lblAdjust.setText(loc.getMenu("Adjustment") + ": ");

		optionsPanel.setLabels();
		btnOptions.setToolTipText(loc.getMenu("Options"));

	}

	/**
	 * Creates the JComboBox that selects display type
	 */
	private void createDisplayTypeComboBox() {

		if (lbDisplayType == null) {
			lbDisplayType = new ListBox();
			lbDisplayType.addChangeHandler(new ChangeHandler() {
				
				public void onChange(ChangeEvent event) {
					actionPerformed(lbDisplayType);
				}
			});
			plotTypes = new ArrayList<PlotType>();
			
//			lbDisplayType.setRenderer(new MyRenderer(app));

		} else {
//			lbDisplayType.removeActionListener(this);
			lbDisplayType.clear();
		}

		getModel().fillDisplayTypes();

	}

	/**
	 * Updates the plot panel. Adds/removes additional panels as needed for the
	 * current selected plot.
	 */
	private void updatePlotPanelLayout() {

		metaPlotPanel.clear();
		plotPanelSouth.clear();
		plotPanelNorth.clear();
		metaPlotPanel.add(plotPanel.getComponent());

		getModel().updatePlotPanelLayout();
	}

	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel() {

		imagePanel = new FlowPanel();
	//	imagePanel.setBackground(Color.WHITE);
		imageContainer = new Label();
		imagePanel.add(imageContainer);

	}

	/**
	 * Creates a control panel for adjusting the number of histogram classes
	 */
	private void createNumClassesPanel() {

//		int numClasses = getModel().getSettings().getNumClasses();
//		fldNumClasses = new JTextField("" + numClasses);
//		fldNumClasses.setEditable(false);
//		fldNumClasses.setOpaque(true);
//		fldNumClasses.setColumns(2);
//		fldNumClasses.setHorizontalAlignment(SwingConstants.CENTER);
//		fldNumClasses.setBackground(null);
//		fldNumClasses.setBorder(BorderFactory.createEmptyBorder());
//		fldNumClasses.setVisible(false);
//
//		sliderNumClasses = new JSlider(SwingConstants.HORIZONTAL, 3, 20,
//				numClasses);
//		Dimension d = sliderNumClasses.getPreferredSize();
//		d.width = 80;
//		sliderNumClasses.setPreferredSize(d);
//		sliderNumClasses.setMinimumSize(new Dimension(50, d.height));
//
//		sliderNumClasses.setMajorTickSpacing(1);
//		sliderNumClasses.setSnapToTicks(true);
//		sliderNumClasses.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent evt) {
//				JSlider slider = (JSlider) evt.getSource();
//				getModel().getSettings().setNumClasses(slider.getValue());
//				fldNumClasses.setText(("" + getModel().getSettings()
//						.getNumClasses()));
//				getModel().updatePlot(true);
//			}
//		});
//
//		sliderNumClasses.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//				fldNumClasses.setVisible(true);
//				fldNumClasses.revalidate();
//			}
//
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//				fldNumClasses.setVisible(false);
//				fldNumClasses.revalidate();
//			}
//		});
//
		numClassesPanel = new FlowPanel();//new FlowLayout(FlowLayout.LEFT, 0, 0));
//		numClassesPanel.add(sliderNumClasses);
//		// numClassesPanel.add(lblNumClasses);
//		numClassesPanel.add(fldNumClasses);

	}

	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel() {

		lblAdjust = new Label();
		minus = new Button("-1");
		none = new Button("0");
		plus = new Button("+1");
//		minus.addActionListener(this);
//		none.addActionListener(this);
//		plus.addActionListener(this);
////		none.setSelected(true);
		stemAdjustPanel = new FlowPanel();
		stemAdjustPanel.add(minus);
		stemAdjustPanel.add(none);
		stemAdjustPanel.add(plus);

	}

	/**
	 * Creates a control panel for manually setting classes
	 */
	private void createManualClassesPanel() {

		lblStart = new Label();
		lblWidth = new Label();

		fldStart = new AutoCompleteTextFieldW(4, app);
		fldStart.setText("" + (int) getModel().getSettings().getClassStart());
	
		fldWidth = new AutoCompleteTextFieldW(4, app);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.setText("" + (int) getModel().getSettings().getClassWidth());

		manualClassesPanel = new FlowPanel();
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);
	}

	
	// ==============================================
	// DISPLAY UPDATE
	// ==============================================

	public void showControlPanel() {
//		((CardLayout) controlCards.getLayout())
//				.show(controlCards, "blankPanel");
	}

	public void setOptionsButtonVisible() {
		btnOptions.setVisible(true);
	}

	public void showInvalidDataDisplay() {
//		imageContainer.setIcon(null);
//		((CardLayout) displayCardPanel.getLayout()).show(displayCardPanel,
//				"imagePanel");
	}

	// ============================================================
	// Event Handlers
	// ============================================================
//
	public void actionPerformed(Object source) {
		
//
//		Object source = e.getSource();
//
//		if (source instanceof JTextField)
//			doTextFieldActionPerformed(source);
//
//		else if (source == minus || source == plus || source == none) {
//			minus.setSelected(source == minus);
//			none.setSelected(source == none);
//			plus.setSelected(source == plus);
//			if (source == minus) {
//				getModel().getSettings().setStemAdjust(-1);
//			}
//			if (source == none) {
//				getModel().getSettings().setStemAdjust(0);
//			}
//			if (source == plus) {
//				getModel().getSettings().setStemAdjust(1);
//			}
//			getModel().updatePlot(true);
//		}
//
//		else if (source == btnOptions) {
//			optionsPanel.setPanel(getModel().getSelectedPlot());
//			optionsPanel.setVisible(btnOptions.isSelected());
//		}
//
//		else if (source == btnExport) {
//			JPopupMenu menu = plotPanel.getContextMenu();
//			menu.show(btnExport,
//					-menu.getPreferredSize().width + btnExport.getWidth(),
//					btnExport.getHeight());
//		}
//
//		else 
		if (source == lbDisplayType) {
			int idx = lbDisplayType.getSelectedIndex();
			if (idx != -1) {
				PlotType t = plotTypes.get(idx);
				getModel().setSelectedPlot(t); 
				getModel().updatePlot(true);
			}
			
			if (optionsPanel.isVisible()) {
				optionsPanel.setPanel(getModel().getSelectedPlot());

			}

		}
//
	}

	private void doTextFieldActionPerformed(Object source) {
//
//		if (source == fldStart) {
//			getModel().getSettings().setClassStart(
//					Validation.validateDouble(fldStart, getModel()
//							.getSettings().getClassStart()));
//		} else if (source == fldWidth) {
//			getModel().getSettings().setClassWidth(
//					Validation.validateDouble(fldWidth, getModel()
//							.getSettings().getClassWidth()));
//		}
		getModel().updatePlot(true);
	}


	public void detachView() {
		// plotPanel.detachView();
	}

	public void attachView() {
		plotPanel.attachView();

	}


	public void updatePanel() {
		//
	}

	// **********************************************
	// Export
	// **********************************************

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 * 
	 * This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.
	 */
//	AbstractAction exportToEVAction = new AbstractAction() {
//		private static final long serialVersionUID = 1L;
//
//		public void actionPerformed(ActionEvent event) {
//			Integer euclidianViewID = (Integer) this
//					.getValue("euclidianViewID");
//
//			// if null ID then use EV1 unless shift is down, then use EV2
//			if (euclidianViewID == null) {
//				euclidianViewID = AppW.getShiftDown() ? app
//						.getEuclidianView2(1).getViewID() : app
//						.getEuclidianView1().getViewID();
//			}
//
//			// do the export
//			getModel().exportGeosToEV(euclidianViewID);
//
//			// null out the ID property
//			this.putValue("euclidianViewID", null);
//		}
//	};

	public void addDisplayTypeItem(PlotType type) {
		lbDisplayType.addItem(type.key);
		plotTypes.add(type);
	}

	public void updateScatterPlot() {
	plotPanelSouth.add(lblTitleX);
		plotPanelSouth.add(fldTitleX);
		plotPanelNorth.add(lblTitleY);
		plotPanelNorth.add(fldTitleY);

		metaPlotPanel.add(plotPanelNorth);
		metaPlotPanel.add(plotPanelSouth);
	}

	public void updateFrequencyTable() {
		plotPanelSouth.add(frequencyTable);
		metaPlotPanel.add(plotPanelSouth);
	}

	public void setSelectedType(PlotType type) {
		lbDisplayType.setSelectedIndex(0);//((int)lbDisplayType));
	}

	public void setTableFromGeoFrequencyTable(
			AlgoFrequencyTable parentAlgorithm, boolean b) {
		frequencyTable.setTableFromGeoFrequencyTable(parentAlgorithm, b);

	}

	public void updatePlotPanelSettings() {
		plotPanel.commonFields.updateSettings(plotPanel, getModel()
				.getSettings());
	}

	public void showManualClassesPanel() {
//		showCardPanel(controlCards, "manualClassesPanel");
	}

	public void showNumClassesPanel() {
//		showCardPanel(controlCards, "numClassesPanel");
	}

	public void showPlotPanel() {
//		showCardPanel(displayCardPanel, "plotPanel");
	}

	public void updateStemPlot(String latex) {
//		imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, latex,
//				app.getPlainFont(), true, Color.BLACK, null));
//		btnOptions.setVisible(false);
//		if (hasControlPanel) {
//			showCardPanel(controlCards, "stemAdjustPanel");
//		}
//
//		((CardLayout) displayCardPanel.getLayout()).show(displayCardPanel,
//				"imagePanel");
//
	}

	public void updateXYTitles(boolean isPointList, boolean isLeftToRight) {

		if (isPointList) {
			fldTitleX.setText(daModel.getDataTitles()[0]);
			fldTitleY.setText(daModel.getDataTitles()[0]);
		} else {
			if (isLeftToRight) {
				fldTitleX.setText(daModel.getDataTitles()[0]);
				fldTitleY.setText(daModel.getDataTitles()[1]);
			} else {
				fldTitleX.setText(daModel.getDataTitles()[1]);
				fldTitleY.setText(daModel.getDataTitles()[0]);
			}
		}
	}

	public void geoToPlotPanel(GeoElement listGeo) {
		listGeo.addView(plotPanel.getViewID());
		plotPanel.add(listGeo);
		listGeo.removeView(App.VIEW_EUCLIDIAN);
		app.getEuclidianView1().remove(listGeo);
	}

	public DataDisplayModel getModel() {
		return model;
	}

	public void setModel(DataDisplayModel model) {
		this.model = model;
	}

	private void createExportToEvAction() {
		/**
		 * Action to export all GeoElements that are currently displayed in this
		 * panel to a EuclidianView. The viewID for the target EuclidianView is
		 * stored as a property with key "euclidianViewID".
		 * 
		 * This action is passed as a parameter to plotPanel where it is used in the
		 * plotPanel context menu and the EuclidianView transfer handler when the
		 * plot panel is dragged into an EV.
		 */
		exportToEVAction = new ScheduledCommand() {
			
			private HashMap<String, Object> value = new HashMap<String, Object>();
			
			public Object getValue(String key) {
				return value.get(key);
			}
			
			public void putValue(String key, Object value) {
				this.value.put(key, value);
			}
			
			public void execute() {
				Integer euclidianViewID = (Integer) this
						.getValue("euclidianViewID");

			
				// if null ID then use EV1 unless shift is down, then use EV2
				if (euclidianViewID == null) {
					euclidianViewID = GlobalKeyDispatcherW.getShiftDown() ? app.getEuclidianView2(1)
							.getViewID() : app.getEuclidianView1().getViewID();
				}

				// do the export
				//exportGeosToEV(euclidianViewID);

				// null out the ID property
				this.putValue("euclidianViewID", null);
			}
		};

	}

}
