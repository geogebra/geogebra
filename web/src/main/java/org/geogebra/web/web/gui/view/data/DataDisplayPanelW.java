package org.geogebra.web.web.gui.view.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.IDataDisplayListener;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Validation;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayPanelW extends FlowPanel implements 
		StatPanelInterfaceW, RequiresResize,
		IDataDisplayListener {
	private static final int NUM_CLASSES_IDX = 0;
	private static final int MANUAL_CLASSES_IDX = 1;
	private static final int STEM_IDX = 2;
	private static final int EMPTY_IDX = 3;

	private static final int METAPLOT_IDX = 0;
	private static final int IMAGE_IDX = 1;

	private static final int PLOTPANEL_MARGIN = 10;

	private static final int PLOTPANEL_MIN_WIDTH = 400;
	private static final int PLOTPANEL_MIN_HEIGHT = 150;
	private static final int OPTIONSPANEL_WIDTH = 200;

	// ggb fields
	private AppW app;
	private final LocalizationW loc;
	// privateDataAnalysisViewD daView;
	private DataDisplayModel model;
	// data view mode
	// display panels
	private DeckPanel displayDeckPanel;
	private FlowPanel metaPlotPanel, plotPanelNorth, plotPanelSouth;
	private PlotPanelEuclidianViewW plotPanel;

	private Label imageContainer;
	private Canvas latexCanvas;
	private GeoNumeric sample;
	// control panel
	private FlowPanel controlPanel;
	private DeckPanel controlDecks;
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
	private MyToggleButton2 minus;
	private MyToggleButton2 none;
	private MyToggleButton2 plus;

	private FlowPanel imagePanel;

	private Label lblTitleX, lblTitleY;
	private AutoCompleteTextFieldW fldTitleX, fldTitleY;
	private FrequencyTablePanelW frequencyTable;
	private MenuBar btnExport;
	private AutoCompleteTextFieldW fldNumClasses;

	private DataAnalysisModel daModel;

	private ScheduledCommand exportToEVAction;


	private ScrollPanel spFrequencyTable;


	private FlowPanel mainPanel;
	private int oldWidth;
	private int oldHeight;
	private DataAnalysisViewW daView;

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
		this.daView = daView;
		this.sample = new GeoNumeric(app.getKernel().getConstruction());

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
		oldWidth = 0;
		oldHeight = 0;
		// create options button
		btnOptions = new MyToggleButton2(new NoDragImage(GuiResources.INSTANCE
				.menu_icon_options().getSafeUri().asString(), 18));

		btnOptions.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				actionPerformed(btnOptions);
			}
		});

		// create export button
		btnExport = new MenuBar();

		// create control panel
		if (hasControlPanel) {

			// create sub-control panels
			createDisplayTypeComboBox();
			createNumClassesPanel();
			createManualClassesPanel();
			createStemPlotAdjustmentPanel();
			FlowPanel emptyControl = new FlowPanel();
			emptyControl.add(new Label("  "));

			// put sub-control panels into a deck panel
			controlDecks = new DeckPanel();
			controlDecks.add(numClassesPanel);
			controlDecks.add(manualClassesPanel);
			controlDecks.add(stemAdjustPanel);
			controlDecks.add(emptyControl);

			FlowPanel buttonPanel = new FlowPanel();
			buttonPanel.setStyleName("daOptionButtons");
			buttonPanel.add(LayoutUtil.panelRow(btnOptions, btnExport));

			// control panel
			controlPanel = new FlowPanel();
			controlPanel.add(LayoutUtil.panelRow(lbDisplayType, controlDecks, buttonPanel));
		}

		createExportToEvAction();
		plotPanel = new PlotPanelEuclidianViewW(app.getKernel(), exportToEVAction);
	
		//		plotPanel.setPreferredSize(PLOTPANEL_WIDTH, PLOTPANEL_HEIGHT);
		//		plotPanel.updateSize();
		plotPanelNorth = new FlowPanel();
		plotPanelSouth = new FlowPanel();
		GColor bgColor = plotPanel.getBackgroundCommon();

		lblTitleX = new Label();
		lblTitleY = new Label();

		fldTitleX = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldTitleY = (new InputPanelW(null, app, -1, false)).getTextComponent();

		fldTitleX.setEditable(false);
		fldTitleY.setEditable(false);

		metaPlotPanel = new FlowPanel();
		metaPlotPanel.setStyleName("daDotPanel");
		metaPlotPanel.add(plotPanel.getComponent());

		createImagePanel();

		// put display panels into a deck panel

		displayDeckPanel = new DeckPanel();

		displayDeckPanel.add(metaPlotPanel);
		displayDeckPanel.add(new ScrollPanel(imagePanel));

		// create options panel
		optionsPanel = new OptionsPanelW(app, daModel, getModel());
		optionsPanel.setVisible(false);

		frequencyTable = new FrequencyTablePanelW(app);

		spFrequencyTable = new ScrollPanel();
		spFrequencyTable.add(frequencyTable);
		spFrequencyTable.setStyleName("spFrequencyTable");

		// =======================================
		// put all the panels together

		mainPanel = new FlowPanel();
		mainPanel.setStyleName("dataDisplayMain");
		if (hasControlPanel) {
			mainPanel.add(controlPanel);
		}
		mainPanel.add(LayoutUtil.panelRow(displayDeckPanel, optionsPanel));

		add(mainPanel);
		createExportMenu();
		resize();
	}

	/**
	 * Sets the labels to the current language
	 */
	public void setLabels() {

		createDisplayTypeComboBox();

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
	 * Creates the ListBox that selects display type
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

		} else {
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
		if (app.has(Feature.JLM_IN_WEB)) {
			latexCanvas = Canvas.createIfSupported();
			imagePanel.add(latexCanvas);
		} else {
			imageContainer = new Label();
			imagePanel.add(imageContainer);
		}
		imagePanel.setStyleName("daImagePanel");

	}

	/**
	 * Creates a control panel for adjusting the number of histogram classes
	 */
	private void createNumClassesPanel() {

		int numClasses = getModel().getSettings().getNumClasses();
		fldNumClasses = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldNumClasses.setEditable(false);
		fldNumClasses.setOpaque(true);
		fldNumClasses.setColumns(2);
		fldNumClasses.setVisible(false);

		sliderNumClasses = new Slider( 3, 20);
		sliderNumClasses.setValue(numClasses);

		sliderNumClasses.setMajorTickSpacing(1);

		sliderNumClasses.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				getModel().getSettings().setNumClasses(sliderNumClasses.getValue());
				fldNumClasses.setText(("" + getModel().getSettings()
						.getNumClasses()));
				getModel().updatePlot(true);

			}
		});

		numClassesPanel = new FlowPanel();
		numClassesPanel.add(sliderNumClasses);
		numClassesPanel.add(fldNumClasses);

	}

	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel() {

		lblAdjust = new Label();
		minus = new MyToggleButton2("-1");
		none = new MyToggleButton2("0");
		plus = new MyToggleButton2("+1");

		minus.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				actionPerformed(minus);
			}
		});

		none.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				actionPerformed(none);
			}
		});

		plus.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				actionPerformed(plus);
			}
		});

		none.setValue(true);

		stemAdjustPanel = new FlowPanel();
		stemAdjustPanel.add(LayoutUtil.panelRow(minus, none, plus));
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
		manualClassesPanel.add(LayoutUtil.panelRow(lblStart, fldStart,
				lblWidth, fldWidth));
		fldStart.addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {
				actionPerformed(fldStart);
			}
		});

		fldStart.addKeyHandler(new KeyHandler() {

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldStart);
				}
			}
		});

		fldWidth.addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {
				actionPerformed(fldWidth);
			}
		});

		fldWidth.addKeyHandler(new KeyHandler() {

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldWidth);
				}
			}
		});

	}

	private void createExportMenu() {
		MenuBar menu = new MenuBar(true);
		MenuItem miToGraphich = new MenuItem(app.getMenu("CopyToGraphics"),
		        new Command() {

			        public void execute() {
				        exportToEV();
			        }
		});
		
		menu.addItem(miToGraphich);

		if(app.getLAF().copyToClipboardSupported()){
			MenuItem miAsPicture = new MenuItem(app.getPlain("ExportAsPicture"),
			        new Command() {

				        public void execute() {
				        	exportAsPicture();
				        }
			});
			menu.addItem(miAsPicture);
		}
		String image = "<img src=\""
				+ GuiResources.INSTANCE.menu_icons_file_export().getSafeUri()
						.asString() + "\" >";
		btnExport.addItem(image, true, menu);
	
	}
	// ==============================================
	// DISPLAY UPDATE
	// ==============================================

	protected void exportAsPicture() {
		app.getSelectionManager().clearSelectedGeos(true,
				false);
		app.updateSelection(false);

		app.setWaitCursor();
		app.copyEVtoClipboard(plotPanel);
		app.setDefaultCursor();
	
    }

	protected void exportToEV() {
		Integer euclidianViewID = null ;
		// if null ID then use EV1 unless shift is down, then use EV2
		if (euclidianViewID == null) {
			euclidianViewID = GlobalKeyDispatcherW.getShiftDown() ? app
					.getEuclidianView2(1).getViewID() : app
					.getEuclidianView1().getViewID();
		}

		// do the export
		getModel().exportGeosToEV(euclidianViewID);
		
		daView.updateOtherDataDisplay(this);
    }

	public void showControlPanel() {
		controlDecks.showWidget(EMPTY_IDX);
	}

	public void setOptionsButtonVisible() {
		btnOptions.setVisible(true);
		btnExport.setVisible(true);
		plotPanel.updateSize();
	}

	public void showInvalidDataDisplay() {
		//		imageContainer.setIcon(null);
		displayDeckPanel.showWidget(IMAGE_IDX);

	}

	// ============================================================
	// Event Handlers
	// ============================================================
	//
	public void actionPerformed(Object source) {
		if (source instanceof AutoCompleteTextFieldW)
		{
			doTextFieldActionPerformed(source);
		}

		else if (source == minus || source == plus || source == none) {
			minus.setValue(source == minus);
			none.setValue(source == none);
			plus.setValue(source == plus);
			App.debug("[Data]  - 0 + has pressed");
			if (source == minus) {
				getModel().getSettings().setStemAdjust(-1);
			}
			if (source == none) {
				getModel().getSettings().setStemAdjust(0);
			}
			if (source == plus) {
				getModel().getSettings().setStemAdjust(1);
			}
			getModel().updatePlot(true);
		}

		else if (source == btnOptions) {
			optionsPanel.setPanel(getModel().getSelectedPlot());
			optionsPanel.setVisible(btnOptions.isSelected());
			resize();
			
		}

		else if (source == btnExport) {
		}

		else 
			if (source == lbDisplayType) {
				int idx = lbDisplayType.getSelectedIndex();
				if (idx != -1) {
					PlotType t = plotTypes.get(idx);
					getModel().setSelectedPlot(t); 
					getModel().updatePlot(true);
				}

				if (optionsPanel.isVisible()) {
					optionsPanel.setPanel(getModel().getSelectedPlot());
					resize();
				}

			}
	}

	private void doTextFieldActionPerformed(Object source) {

		if (source == fldStart) {
			getModel().getSettings().setClassStart(
					Validation.validateDouble(fldStart, getModel()
							.getSettings().getClassStart()));
		} else if (source == fldWidth) {
			getModel().getSettings().setClassWidth(
					Validation.validateDoublePositive(fldWidth, getModel()
							.getSettings().getClassWidth()));
		}
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

	public void addDisplayTypeItem(PlotType type) {
		lbDisplayType.addItem(app.getMenu(type.key));
		plotTypes.add(type);
	}

	public void updateScatterPlot() {
		if (!daModel.isRegressionMode()) {
			return;
		}
		metaPlotPanel.clear();
		plotPanelNorth.clear();
		plotPanelSouth.clear();
		
		plotPanelSouth.add(LayoutUtil.panelRow(lblTitleX, fldTitleX));
		plotPanelNorth.add(LayoutUtil.panelRow(lblTitleY, fldTitleY));
		
		metaPlotPanel.add(plotPanelNorth);
		metaPlotPanel.add(plotPanel.getComponent());
		metaPlotPanel.add(plotPanelSouth);
		
	}

	public void updateFrequencyTable() {
		plotPanelSouth.add(spFrequencyTable);
		metaPlotPanel.add(plotPanelSouth);
	}

	public void setSelectedType(PlotType type) {
		lbDisplayType.setSelectedIndex(plotTypes.indexOf(type));
	}

	public void setTableFromGeoFrequencyTable(
			AlgoFrequencyTable parentAlgorithm, boolean b) {
		App.debug("setTableFromGeoFrequencyTable");
		frequencyTable.setTableFromGeoFrequencyTable(parentAlgorithm, b);
		resize(false);
	}

	public void removeFrequencyTable() {
		App.debug("removeFrequencyTable");
		plotPanelSouth.remove(spFrequencyTable);
		plotPanel.updateSize();
		resize(false);
	}

	public void updatePlotPanelSettings() {
		plotPanel.commonFields.updateSettings(plotPanel, getModel()
				.getSettings());
	}

	public void showManualClassesPanel() {
		controlDecks.showWidget(MANUAL_CLASSES_IDX);
	}

	public void showNumClassesPanel() {
		controlDecks.showWidget(NUM_CLASSES_IDX);
	}

	public void showPlotPanel() {
		displayDeckPanel.showWidget(METAPLOT_IDX);
	}
	

	public void updateStemPlot(String latex) {
		btnOptions.setVisible(false);
		btnExport.setVisible(false);
		
		String latexStr = DrawEquationWeb.inputLatexCosmetics(latex);
		if (app.has(Feature.JLM_IN_WEB)) {
			DrawEquationWeb.paintOnCanvas(sample, latexStr, latexCanvas,
					app.getFontSize());


		} else {

			imageContainer.setText("");
			DrawEquationWeb.drawEquationAlgebraView(
					imageContainer.getElement(), "\\mathrm {" + latexStr + "}",
					true);
		}

		if (hasControlPanel) {
			controlDecks.showWidget(STEM_IDX);
		}

		displayDeckPanel.showWidget(IMAGE_IDX);

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

	public void resize(int offsetWidth, int offsetHeight, boolean update) {
		int w = offsetWidth;
		int h = offsetHeight;
		
		int width = optionsPanel.isVisible() ? w - optionsPanel.getOffsetWidth() - PLOTPANEL_MARGIN
				: w;
		int height = (frequencyTable.isVisible() ? h - spFrequencyTable.getOffsetHeight() 
				: h) - lbDisplayType.getOffsetHeight() -  PLOTPANEL_MARGIN;

		if (daModel.isRegressionMode()) {
			height -= 2*lblTitleX.getOffsetHeight();
			height -= lblTitleY.getOffsetHeight();
		}
		
		if (width < PLOTPANEL_MIN_WIDTH) {
			width =  PLOTPANEL_MIN_WIDTH;
		}

		if (height < PLOTPANEL_MIN_HEIGHT) {
			height =  PLOTPANEL_MIN_HEIGHT;
		}

		if (oldWidth == width && oldHeight == height) {
			return;
		}
		
		oldWidth = width;
		oldHeight = height;
		
		plotPanel.setPreferredSize(new GDimensionW(width, height));
		if (optionsPanel.isVisible()) {
			optionsPanel.resize(height);
		}
		plotPanel.updateSize();
		plotPanel.repaintView();
		plotPanel.getEuclidianController().calculateEnvironment();
		if (update) {
			getModel().updatePlot(false);
		}

		if (app.has(Feature.JLM_IN_WEB)) {
			imagePanel.setPixelSize(width, height);

		} else {
			imageContainer.setPixelSize(width, height);
		}
		
	}


	public void resize(boolean update) {
	    resize(getOffsetWidth(), getOffsetHeight(), update);
    }

	public void resize() {
	    resize(true);
    }

	public void onResize() {
		resize(true);  
    }

	public void update() {
		model.updatePlot(true);
    }

}
