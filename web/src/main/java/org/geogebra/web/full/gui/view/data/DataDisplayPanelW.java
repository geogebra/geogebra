package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;
import java.util.List;

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
import org.geogebra.common.util.Validation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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
		StatPanelInterfaceW, RequiresResize, IDataDisplayListener {
	private static final int NUM_CLASSES_IDX = 0;
	private static final int MANUAL_CLASSES_IDX = 1;
	private static final int STEM_IDX = 2;
	private static final int EMPTY_IDX = 3;

	private static final int METAPLOT_IDX = 0;
	private static final int IMAGE_IDX = 1;

	private static final int PLOTPANEL_MARGIN = 10;

	private static final int PLOTPANEL_MIN_WIDTH = 400;
	private static final int PLOTPANEL_MIN_HEIGHT = 150;
	// ggb fields
	private AppW app;
	private final LocalizationW loc;
	// privateDataAnalysisViewD daView;
	private DataDisplayModel model;
	// data view mode
	// display panels
	private DeckPanel displayDeckPanel;
	private FlowPanel metaPlotPanel;
	private FlowPanel plotPanelNorth;
	private FlowPanel plotPanelSouth;
	private PlotPanelEuclidianViewW plotPanel;

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
	private MyToggleButtonW btnOptions;

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
	private MyToggleButtonW minus;
	private MyToggleButtonW none;
	private MyToggleButtonW plus;

	private FlowPanel imagePanel;

	private Label lblTitleX;
	private Label lblTitleY;
	private AutoCompleteTextFieldW fldTitleX;
	private AutoCompleteTextFieldW fldTitleY;
	private FrequencyTablePanelW frequencyTable;
	private GPopupMenuW btnExport;
	private AutoCompleteTextFieldW fldNumClasses;

	private DataAnalysisModel daModel;

	private ScrollPanel spFrequencyTable;

	private int oldWidth;
	private int oldHeight;
	private DataAnalysisViewW daView;

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daView
	 *            daView
	 */
	public DataDisplayPanelW(DataAnalysisViewW daView, int id) {
		this.app = daView.getApp();
		this.loc = app.getLocalization();
		daModel = daView.getModel();
		setModel(new DataDisplayModel(daModel, this, id));
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
		btnOptions = new MyToggleButtonW(new NoDragImage(GuiResources.INSTANCE
				.menu_icon_options().getSafeUri().asString(), 18));
		ClickStartHandler.initDefaults(btnOptions, true, false);
		btnOptions.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				actionPerformed(btnOptions);
			}
		});

		// create export button
		btnExport = new GPopupMenuW(app, true) {
			@Override
			public int getPopupLeft() {
				return getPopupMenu().getAbsoluteLeft();
			}
		};
		btnExport.getPopupMenu().addStyleName("gwt-ToggleButton");
		btnExport.getPopupMenu().addStyleName("MyToggleButton");
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
			buttonPanel.add(
					LayoutUtilW.panelRow(btnOptions, btnExport.getPopupMenu()));

			// control panel
			controlPanel = new FlowPanel();
			controlPanel.add(LayoutUtilW.panelRow(lbDisplayType, controlDecks, buttonPanel));
		}

		plotPanel = new PlotPanelEuclidianViewW(app.getKernel());
	
		//		plotPanel.setPreferredSize(PLOTPANEL_WIDTH, PLOTPANEL_HEIGHT);
		//		plotPanel.updateSize();
		plotPanelNorth = new FlowPanel();
		plotPanelSouth = new FlowPanel();

		lblTitleX = new Label();
		lblTitleY = new Label();

		fldTitleX = (new InputPanelW(app, -1, false)).getTextComponent();
		fldTitleY = (new InputPanelW(app, -1, false)).getTextComponent();

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

		frequencyTable = new FrequencyTablePanelW();

		spFrequencyTable = new ScrollPanel();
		spFrequencyTable.add(frequencyTable);
		spFrequencyTable.setStyleName("spFrequencyTable");

		// =======================================
		// put all the panels together

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("dataDisplayMain");
		if (hasControlPanel) {
			mainPanel.add(controlPanel);
		}
		mainPanel.add(LayoutUtilW.panelRow(displayDeckPanel, optionsPanel));

		add(mainPanel);
		createExportMenu();
		resize();
	}

	/**
	 * Sets the labels to the current language
	 */
	@Override
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

				@Override
				public void onChange(ChangeEvent event) {
					actionPerformed(lbDisplayType);
				}
			});
			plotTypes = new ArrayList<>();

		} else {
			plotTypes.clear();
			lbDisplayType.clear();
		}

		getModel().fillDisplayTypes();
	}

	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel() {
		imagePanel = new FlowPanel();
		latexCanvas = Canvas.createIfSupported();
		imagePanel.add(latexCanvas);
		imagePanel.setStyleName("daImagePanel");
	}

	/**
	 * Creates a control panel for adjusting the number of histogram classes
	 */
	private void createNumClassesPanel() {

		int numClasses = getModel().getSettings().getNumClasses();
		fldNumClasses = (new InputPanelW(app, -1, false)).getTextComponent();
		fldNumClasses.setEditable(false);
		fldNumClasses.setWidthInEm(2);
		fldNumClasses.setVisible(false);

		sliderNumClasses = new Slider(3, 20);
		sliderNumClasses.setValue(numClasses);

		sliderNumClasses.setTickSpacing(1);

		sliderNumClasses.addChangeHandler(new ChangeHandler() {

			@Override
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
		minus = new MyToggleButtonW("-1");
		none = new MyToggleButtonW("0");
		plus = new MyToggleButtonW("+1");

		minus.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				actionPerformed(minus);
			}
		});

		none.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actionPerformed(none);
			}
		});

		plus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actionPerformed(plus);
			}
		});

		none.setValue(true);

		stemAdjustPanel = new FlowPanel();
		stemAdjustPanel.add(LayoutUtilW.panelRow(minus, none, plus));
	}

	/**
	 * Creates a control panel for manually setting classes
	 */
	private void createManualClassesPanel() {

		lblStart = new Label();
		lblWidth = new Label();

		fldStart = new AutoCompleteTextFieldW(4, app);
		fldStart.setText("" + (int) getModel().getSettings().getClassStart());
		addInsertHandler(fldStart);
		fldWidth = new AutoCompleteTextFieldW(4, app);
		fldStart.setWidthInEm(4);
		fldWidth.setWidthInEm(4);
		fldWidth.setText("" + (int) getModel().getSettings().getClassWidth());
		addInsertHandler(fldWidth);

		manualClassesPanel = new FlowPanel();
		manualClassesPanel.add(LayoutUtilW.panelRow(lblStart, fldStart,
				lblWidth, fldWidth));
		fldStart.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				actionPerformed(fldStart);
			}
		});

		fldStart.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldStart);
				}
			}
		});

		fldWidth.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				actionPerformed(fldWidth);
			}
		});

		fldWidth.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldWidth);
				}
			}
		});

	}

	private void addInsertHandler(final AutoCompleteTextFieldW field) {
		field.enableGGBKeyboard();
		field.addInsertHandler(new AutoCompleteTextFieldW.InsertHandler() {
			@Override
			public void onInsert(String text) {
				int cursorPos = field.removeDummyCursor();
				actionPerformed(field);
				if (Browser.isTabletBrowser()) {
					field.addDummyCursor(cursorPos);
				}
			}
		});
	}

	private void createExportMenu() {
		AriaMenuBar menu = new AriaMenuBar();
		AriaMenuItem miToGraphich = new AriaMenuItem(
				loc.getMenu("CopyToGraphics"), false,
		        new Command() {

			        @Override
					public void execute() {
				        exportToEV();
						btnExport.removeSubPopup();
			        }
		});

		menu.addItem(miToGraphich);

		if (app.getLAF().copyToClipboardSupported()) {
			AriaMenuItem miAsPicture = new AriaMenuItem(
					loc.getMenu("ExportAsPicture"), false,
			        new Command() {

				        @Override
						public void execute() {
				        	exportAsPicture();
							btnExport.removeSubPopup();
				        }
			});
			menu.addItem(miAsPicture);
		}
		String image = "<img src=\""
				+ GuiResources.INSTANCE.menu_icons_file_export().getSafeUri()
						.asString() + "\" >";
		btnExport.addItem(new AriaMenuItem(image, true, menu));
	
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
		// use EV1 unless shift is down, then use EV2
		int euclidianViewID = GlobalKeyDispatcherW.getShiftDown()
				? app.getEuclidianView2(1).getViewID()
				: app.getEuclidianView1().getViewID();

		// do the export
		getModel().exportGeosToEV(euclidianViewID);
		
		daView.updateOtherDataDisplay(this);
    }

	@Override
	public void showControlPanel() {
		controlDecks.showWidget(EMPTY_IDX);
	}

	@Override
	public void setOptionsButtonVisible() {
		btnOptions.setVisible(true);
		btnExport.setVisible(true);
		plotPanel.updateSize();
	}

	@Override
	public void showInvalidDataDisplay() {
		//		imageContainer.setIcon(null);
		displayDeckPanel.showWidget(IMAGE_IDX);
	}

	// ============================================================
	// Event Handlers
	// ============================================================
	//
	/**
	 * @param source
	 *            event source
	 */
	protected void actionPerformed(Object source) {
		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed(source);
		}

		else if (source == minus || source == plus || source == none) {
			minus.setValue(source == minus);
			none.setValue(source == none);
			plus.setValue(source == plus);
			Log.debug("[Data]  - 0 + has pressed");
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

		else if (source == lbDisplayType) {
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

	@Override
	public void updatePanel() {
		//
	}

	@Override
	public void addDisplayTypeItem(PlotType type) {
		lbDisplayType.addItem(loc.getMenu(type.getKey()));
		plotTypes.add(type);
	}

	@Override
	public void updateScatterPlot() {
		if (!daModel.isRegressionMode()) {
			return;
		}
		metaPlotPanel.clear();
		plotPanelNorth.clear();
		plotPanelSouth.clear();
		
		plotPanelSouth.add(LayoutUtilW.panelRow(lblTitleX, fldTitleX));
		plotPanelNorth.add(LayoutUtilW.panelRow(lblTitleY, fldTitleY));
		
		metaPlotPanel.add(plotPanelNorth);
		metaPlotPanel.add(plotPanel.getComponent());
		metaPlotPanel.add(plotPanelSouth);
	}

	@Override
	public void updateFrequencyTable() {
		plotPanelSouth.add(spFrequencyTable);
		metaPlotPanel.add(plotPanelSouth);
	}

	@Override
	public void setSelectedType(PlotType type) {
		lbDisplayType.setSelectedIndex(plotTypes.indexOf(type));
	}

	@Override
	public void setTableFromGeoFrequencyTable(
			AlgoFrequencyTable parentAlgorithm, boolean b) {
		Log.debug("setTableFromGeoFrequencyTable");
		frequencyTable.setTableFromGeoFrequencyTable(parentAlgorithm, b);
		resize(false);
	}

	@Override
	public void removeFrequencyTable() {
		Log.debug("removeFrequencyTable");
		plotPanelSouth.remove(spFrequencyTable);
		plotPanel.updateSize();
		resize(false);
	}

	@Override
	public void updatePlotPanelSettings() {
		plotPanel.commonFields.updateSettings(plotPanel, getModel()
				.getSettings());
	}

	@Override
	public void showManualClassesPanel() {
		controlDecks.showWidget(MANUAL_CLASSES_IDX);
	}

	@Override
	public void showNumClassesPanel() {
		controlDecks.showWidget(NUM_CLASSES_IDX);
	}

	@Override
	public void showPlotPanel() {
		displayDeckPanel.showWidget(METAPLOT_IDX);
	}
	
	@Override
	public void updateStemPlot(String latex) {
		btnOptions.setVisible(false);
		btnExport.setVisible(false);
		
		DrawEquationW.paintOnCanvas(sample, latex, latexCanvas,
				app.getFontSizeWeb());

		if (hasControlPanel) {
			controlDecks.showWidget(STEM_IDX);
		}

		displayDeckPanel.showWidget(IMAGE_IDX);
	}

	@Override
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

	@Override
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

	/**
	 * @param offsetWidth
	 *            width
	 * @param offsetHeight
	 *            height
	 * @param update
	 *            whether to update plot
	 */
	public void resize(int offsetWidth, int offsetHeight, boolean update) {
		int w = offsetWidth;
		int h = offsetHeight;
		
		int width = optionsPanel.isVisible() ? w - optionsPanel.getOffsetWidth() - PLOTPANEL_MARGIN
				: w;
		int height = (frequencyTable.isVisible() ? h - spFrequencyTable.getOffsetHeight() 
				: h) - lbDisplayType.getOffsetHeight() -  PLOTPANEL_MARGIN;

		if (daModel.isRegressionMode()) {
			height -= 2 * lblTitleX.getOffsetHeight();
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
		
		plotPanel.setPreferredSize(new Dimension(width, height));
		if (optionsPanel.isVisible()) {
			optionsPanel.resize(w - width - PLOTPANEL_MARGIN, height);
		}
		plotPanel.updateSize();
		plotPanel.repaintView();
		plotPanel.getEuclidianController().calculateEnvironment();
		if (update) {
			getModel().updatePlot(false);
		}

		imagePanel.setPixelSize(width, height);
	}

	public void resize(boolean update) {
	    resize(getOffsetWidth(), getOffsetHeight(), update);
    }

	@Override
	public void resize() {
	    resize(true);
    }

	@Override
	public void onResize() {
		resize(true);  
    }

	public void update() {
		model.updatePlot(true);
    }

}
