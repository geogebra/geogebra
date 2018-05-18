package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.IDataDisplayListener;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Validation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayPanelD extends JPanel implements ActionListener,
		FocusListener, StatPanelInterface, IDataDisplayListener {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppD app;
	private final LocalizationD loc;
	// privateDataAnalysisViewD daView;
	private DataDisplayModel model;
	// data view mode
	// display panels
	private JPanel displayCardPanel;
	private JPanel metaPlotPanel, plotPanelNorth, plotPanelSouth;
	private PlotPanelEuclidianViewD plotPanel;

	private JLabel imageContainer;

	// control panel
	private JPanel controlPanel;
	private JPanel controlCards;
	private boolean hasControlPanel = true;
	private JComboBox cbDisplayType;

	// options button and sidebar panel
	private OptionsPanelD optionsPanel;
	private JToggleButton btnOptions;

	// numClasses panel
	// private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses;

	// manual classes panel
	private JToolBar manualClassesPanel;
	private JLabel lblStart;
	private JLabel lblWidth;
	private AutoCompleteTextFieldD fldStart;
	private AutoCompleteTextFieldD fldWidth;

	// stemplot adjustment panel
	private JToolBar stemAdjustPanel;
	private JLabel lblAdjust;
	private JButton minus;
	private JButton none;
	private JButton plus;

	private JPanel imagePanel;

	private JLabel lblTitleX, lblTitleY;
	private MyTextFieldD fldTitleX, fldTitleY;
	private FrequencyTablePanel frequencyTable;
	private JToggleButton btnExport;
	private JTextField fldNumClasses;

	private DataAnalysisModel daModel;

	private DataAnalysisViewD daView;

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daView
	 *            daView
	 */
	public DataDisplayPanelD(DataAnalysisViewD daView, int id) {

		this.app = daView.getApp();
		this.loc = app.getLocalization();
		daModel = daView.getModel();
		setModel(new DataDisplayModel(daModel, this, id));
		// create the GUI
		this.daView = daView;
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
		btnOptions.setSelected(false);
		btnOptions.setSelectedIcon(
				app.getScaledIcon(GuiResourcesD.INPUTHELP_RIGHT_18x18));

	}

	public void updateIcons() {
		btnOptions
				.setIcon(app.getScaledIcon(GuiResourcesD.INPUTHELP_LEFT_18x18));
		btnOptions.setSelectedIcon(
				app.getScaledIcon(GuiResourcesD.INPUTHELP_RIGHT_18x18));

		btnExport.setIcon(app.getScaledIcon(GuiResourcesD.EXPORT16));

	}
	// ==============================================
	// GUI
	// ==============================================

	private void createGUI() {

		// create options button
		btnOptions = new JToggleButton();
		btnOptions.setBorderPainted(false);
		btnOptions.setFocusPainted(false);
		btnOptions.setContentAreaFilled(false);
		// optionsButton.setPreferredSize(new
		// Dimension(optionsButton.getIcon().getIconWidth(),18));
		btnOptions.setMargin(new Insets(0, 0, 0, 0));
		btnOptions.addActionListener(this);

		// create export button
		btnExport = new JToggleButton();
		btnExport.setBorderPainted(false);
		btnExport.setFocusPainted(false);
		btnExport.setContentAreaFilled(false);
		// btnExport.setPreferredSize(new
		// Dimension(btnExport.getIcon().getIconWidth(),18));
		btnExport.setMargin(new Insets(0, 0, 0, 0));
		btnExport.addActionListener(this);
		updateIcons();
		// create control panel
		if (hasControlPanel) {

			// create sub-control panels
			createDisplayTypeComboBox();
			createNumClassesPanel();
			createManualClassesPanel();
			createStemPlotAdjustmentPanel();
			JPanel emptyControl = new JPanel(new BorderLayout());
			emptyControl.add(new JLabel("  "));

			// put sub-control panels into a card layout
			controlCards = new JPanel(new CardLayout());
			controlCards.add("numClassesPanel", numClassesPanel);
			controlCards.add("manualClassesPanel", manualClassesPanel);
			controlCards.add("stemAdjustPanel", stemAdjustPanel);
			controlCards.add("blankPanel", emptyControl);

			// control panel
			controlPanel = new JPanel(new BorderLayout(0, 0));
			controlPanel.add(flowPanel(cbDisplayType), loc.borderWest());
			controlPanel.add(controlCards, BorderLayout.CENTER);
			controlPanel.add(flowPanelRight(btnOptions, btnExport),
					loc.borderEast());
		}

		plotPanel = new PlotPanelEuclidianViewD(app.getKernel(),
				exportToEVAction);

		plotPanelNorth = new JPanel();
		plotPanelSouth = new JPanel();
		Color bgColor = org.geogebra.desktop.awt.GColorD
				.getAwtColor(plotPanel.getBackgroundCommon());
		plotPanelNorth.setBackground(bgColor);
		plotPanelSouth.setBackground(bgColor);
		lblTitleX = new JLabel();
		lblTitleY = new JLabel();
		fldTitleX = new MyTextFieldD(app, 20);
		fldTitleY = new MyTextFieldD(app, 20);
		fldTitleX.setEditable(false);
		fldTitleX.setBorder(BorderFactory.createEmptyBorder());
		fldTitleY.setEditable(false);
		fldTitleY.setBorder(BorderFactory.createEmptyBorder());
		fldTitleX.setBackground(Color.white);
		fldTitleY.setBackground(Color.white);

		metaPlotPanel = new JPanel(new BorderLayout());
		metaPlotPanel.add(plotPanel.getJPanel(), BorderLayout.CENTER);

		createImagePanel();

		// put display panels into a card layout

		displayCardPanel = new JPanel(new CardLayout());
		displayCardPanel.setBackground(bgColor);

		displayCardPanel.add("plotPanel", metaPlotPanel);
		displayCardPanel.add("imagePanel", new JScrollPane(imagePanel));

		// create options panel
		optionsPanel = new OptionsPanelD(app, daModel,
				getModel().getSettings());
		optionsPanel.addPropertyChangeListener("settings",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						getModel().updatePlot(true);
					}
				});
		optionsPanel.setVisible(false);

		frequencyTable = new FrequencyTablePanel(app);

		// =======================================
		// put all the panels together

		JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

		if (hasControlPanel) {
			mainPanel.add(controlPanel, BorderLayout.NORTH);
		}
		mainPanel.add(displayCardPanel, BorderLayout.CENTER);
		mainPanel.add(optionsPanel, loc.borderEast());

		this.setLayout(new BorderLayout(0, 0));
		this.add(mainPanel, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		controlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				SystemColor.controlShadow));

	}

	/**
	 * Sets the labels to the current language
	 */
	@Override
	public void setLabels() {

		createDisplayTypeComboBox();
		sliderNumClasses.setToolTipText(loc.getMenu("Classes"));
		fldNumClasses.setToolTipText(loc.getMenu("Classes"));
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

		if (cbDisplayType == null) {
			cbDisplayType = new JComboBox();
			cbDisplayType.setRenderer(new MyRenderer(app));

		} else {
			cbDisplayType.removeActionListener(this);
			cbDisplayType.removeAllItems();
		}

		getModel().fillDisplayTypes();

		cbDisplayType.setFocusable(false);
		cbDisplayType.addActionListener(this);
		cbDisplayType.setMaximumRowCount(cbDisplayType.getItemCount());

	}

	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel() {

		imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(BorderFactory.createEmptyBorder());
		imagePanel.setBackground(Color.WHITE);
		imageContainer = new JLabel();
		imagePanel.setAlignmentX(SwingConstants.CENTER);
		imagePanel.setAlignmentY(SwingConstants.CENTER);
		imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
		imagePanel.add(imageContainer, BorderLayout.CENTER);

	}

	/**
	 * commonFields Creates a control panel for adjusting the number of
	 * histogram classes
	 */
	private void createNumClassesPanel() {

		int numClasses = getModel().getSettings().getNumClasses();
		fldNumClasses = new JTextField("" + numClasses);
		fldNumClasses.setEditable(false);
		fldNumClasses.setOpaque(true);
		fldNumClasses.setColumns(2);
		fldNumClasses.setHorizontalAlignment(SwingConstants.CENTER);
		fldNumClasses.setBackground(null);
		fldNumClasses.setBorder(BorderFactory.createEmptyBorder());
		fldNumClasses.setVisible(false);

		sliderNumClasses = new JSlider(SwingConstants.HORIZONTAL, 3, 20,
				numClasses);
		Dimension d = sliderNumClasses.getPreferredSize();
		d.width = 80;
		sliderNumClasses.setPreferredSize(d);
		sliderNumClasses.setMinimumSize(new Dimension(50, d.height));

		sliderNumClasses.setMajorTickSpacing(1);
		sliderNumClasses.setSnapToTicks(true);
		sliderNumClasses.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				getModel().getSettings().setNumClasses(slider.getValue());
				fldNumClasses.setText(
						("" + getModel().getSettings().getNumClasses()));
				getModel().updatePlot(true);
			}
		});

		sliderNumClasses.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				fldNumClasses.setVisible(true);
				fldNumClasses.revalidate();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				fldNumClasses.setVisible(false);
				fldNumClasses.revalidate();
			}
		});

		numClassesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		numClassesPanel.add(sliderNumClasses);
		// numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(fldNumClasses);

	}

	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel() {

		lblAdjust = new JLabel();
		minus = new JButton("-1");
		none = new JButton("0");
		plus = new JButton("+1");
		minus.addActionListener(this);
		none.addActionListener(this);
		plus.addActionListener(this);
		none.setSelected(true);
		stemAdjustPanel = new JToolBar();
		stemAdjustPanel.setFloatable(false);
		stemAdjustPanel.add(minus);
		stemAdjustPanel.add(none);
		stemAdjustPanel.add(plus);

	}

	/**
	 * Creates a control panel for manually setting classes
	 */
	private void createManualClassesPanel() {

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new AutoCompleteTextFieldD(4, app);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + (int) getModel().getSettings().getClassStart());
		fldStart.addFocusListener(this);

		fldWidth = new AutoCompleteTextFieldD(4, app);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + (int) getModel().getSettings().getClassWidth());
		fldWidth.addFocusListener(this);

		manualClassesPanel = new JToolBar();
		manualClassesPanel.setFloatable(false);
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(Box.createHorizontalStrut(4));
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);
	}

	public JPopupMenu getExportMenu() {
		return plotPanel.getContextMenu();
	}

	// ==============================================
	// DISPLAY UPDATE
	// ==============================================

	@Override
	public void showControlPanel() {
		((CardLayout) controlCards.getLayout()).show(controlCards,
				"blankPanel");
	}

	@Override
	public void setOptionsButtonVisible() {
		btnOptions.setVisible(true);
	}

	@Override
	public void showInvalidDataDisplay() {
		imageContainer.setIcon(null);
		((CardLayout) displayCardPanel.getLayout()).show(displayCardPanel,
				"imagePanel");
	}

	// ============================================================
	// Event Handlers
	// ============================================================

	@Override
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed(source);
		} else if (source == minus || source == plus || source == none) {
			minus.setSelected(source == minus);
			none.setSelected(source == none);
			plus.setSelected(source == plus);
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
		}

		else if (source == btnExport) {
			JPopupMenu menu = plotPanel.getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

		else if (source == cbDisplayType) {
			if (cbDisplayType.getSelectedItem().equals(MyRenderer.SEPARATOR)) {
				cbDisplayType.setSelectedItem(getModel().getSelectedPlot());
			} else {
				getModel().setSelectedPlot(
						(PlotType) cbDisplayType.getSelectedItem());
				getModel().updatePlot(true);
			}

			if (optionsPanel.isVisible()) {
				optionsPanel.setPanel(getModel().getSelectedPlot());

			}

		}

	}

	private void doTextFieldActionPerformed(Object source) {

		if (source == fldStart) {
			getModel().getSettings().setClassStart(Validation.validateDouble(
					fldStart, getModel().getSettings().getClassStart()));
		} else if (source == fldWidth) {
			getModel().getSettings()
					.setClassWidth(Validation.validateDoublePositive(fldWidth,
							getModel().getSettings().getClassWidth()));
		}
		getModel().updatePlot(true);
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			this.doTextFieldActionPerformed(source);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		//
	}

	public void detachView() {
		// plotPanel.detachView();
	}

	public void attachView() {
		plotPanel.attachView();

	}

	// ============================================================
	// Utilities
	// ============================================================

	private static JPanel flowPanel(JComponent... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

	private static JPanel flowPanelRight(JComponent... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

	@Override
	public void updateFonts(Font font) {
		plotPanel.updateFonts();
		updateIcons();
		optionsPanel.updateFonts(font);
		setLabels();
	}

	@Override
	public void updatePanel() {
		//
	}

	// ============================================================
	// ComboBox Renderer with SEPARATOR
	// ============================================================

	private static class MyRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public static final String SEPARATOR = "SEPARATOR";
		JSeparator separator;

		private AppD app;

		public MyRenderer(AppD app) {
			this.app = app;
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = "";
			if (value instanceof PlotType) {
				str = app.getLocalization()
						.getMenu(((PlotType) value).getKey());
			} else {
				Log.error("wrong class" + value);
			}
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
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
	AbstractAction exportToEVAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			Integer euclidianViewID = (Integer) this
					.getValue("euclidianViewID");

			// if null ID then use EV1 unless shift is down, then use EV2
			if (euclidianViewID == null) {
				euclidianViewID = app.getShiftDown()
						? app.getEuclidianView2(1).getViewID()
						: app.getEuclidianView1().getViewID();
			}

			// do the export
			getModel().exportGeosToEV(euclidianViewID);
			updateOtherDataDisplay();
			// null out the ID property
			this.putValue("euclidianViewID", null);
		}
	};

	@Override
	public void addDisplayTypeItem(PlotType type) {
		cbDisplayType.addItem(type);
	}

	protected void updateOtherDataDisplay() {
		daView.updateOtherDataDisplay(this);
	}

	@Override
	public void updateScatterPlot() {
		plotPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		plotPanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		plotPanelSouth.add(lblTitleX);
		plotPanelSouth.add(fldTitleX);
		plotPanelNorth.add(lblTitleY);
		plotPanelNorth.add(fldTitleY);

		metaPlotPanel.add(plotPanelNorth, BorderLayout.NORTH);
		metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
	}

	@Override
	public void updateFrequencyTable() {
		plotPanelSouth.setLayout(new BorderLayout());
		plotPanelSouth.add(frequencyTable, BorderLayout.CENTER);
		metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
	}

	@Override
	public void removeFrequencyTable() {
		metaPlotPanel.remove(plotPanelSouth);
	}

	@Override
	public void setSelectedType(PlotType type) {
		cbDisplayType.setSelectedItem(type);
	}

	@Override
	public void setTableFromGeoFrequencyTable(
			AlgoFrequencyTable parentAlgorithm, boolean b) {
		frequencyTable.setTableFromGeoFrequencyTable(parentAlgorithm, b);

	}

	@Override
	public void updatePlotPanelSettings() {
		plotPanel.commonFields.updateSettings(plotPanel,
				getModel().getSettings());
	}

	private static void showCardPanel(JPanel panel, String id) {
		((CardLayout) panel.getLayout()).show(panel, id);
	}

	@Override
	public void showManualClassesPanel() {
		showCardPanel(controlCards, "manualClassesPanel");
	}

	@Override
	public void showNumClassesPanel() {
		showCardPanel(controlCards, "numClassesPanel");
	}

	@Override
	public void showPlotPanel() {
		showCardPanel(displayCardPanel, "plotPanel");
	}

	@Override
	public void updateStemPlot(String latex) {
		imageContainer.setIcon(GeoGebraIconD.createLatexIcon(app, latex,
				app.getPlainFont(), Color.BLACK, null));
		btnOptions.setVisible(false);
		if (hasControlPanel) {
			showCardPanel(controlCards, "stemAdjustPanel");
		}

		((CardLayout) displayCardPanel.getLayout()).show(displayCardPanel,
				"imagePanel");

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

	@Override
	public void resize() {
		// TODO Auto-generated method stub

	}

	public void update() {
		model.updatePlot(true);
	}

}
