package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.ChartStyleAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.util.SelectionTableD;
import org.geogebra.desktop.gui.view.spreadsheet.MyTableD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * panel to select the filling of a polygon or conic section
 * 
 * @author Markus Hohenwarter
 */
@SuppressWarnings("rawtypes")
class FillingPanelD extends JPanel
		implements ChangeListener, SetLabels, UpdateFonts,
		UpdateablePropertiesPanel, ActionListener, IFillingListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** filling model */
	FillingModel model;
	/** opacity */
	JSlider opacitySlider;
	private JSlider angleSlider;
	private JSlider distanceSlider;
	private JComboBox cbFillType;
	private JCheckBox cbFillInverse;

	private JPanel transparencyPanel, hatchFillPanel, imagePanel, anglePanel,
			distancePanel;
	private JLabel lblFillType;
	/** symbol used for filling */
	JLabel lblSelectedSymbol;
	private JLabel lblMsgSelected;
	private JButton btnOpenFile;

	private PopupMenuButtonD btnImage;
	// button for removing turtle's image
	private JButton btnClearImage;
	private JLabel lblFillInverse;
	private JLabel lblSymbols;
	private ArrayList<ImageResourceD> imgFileNameList;
	private PopupMenuButtonD btInsertUnicode;

	// For handle single bar
	private JPanel barsPanel;
	private JToggleButton[] selectionBarButtons;
	/** selected button */
	int selectedBarButton;
	/** application */
	AppD app;
	private Localization loc;

	/**
	 * New filling panel
	 * 
	 * @param app
	 *            application
	 */
	public FillingPanelD(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();
		// For filling whit unicode char
		model = new FillingModel(app);
		model.setListener(this);
		btInsertUnicode = new PopupMenuButtonD(app);
		buildInsertUnicodeButton();
		btInsertUnicode.addActionListener(this);
		btInsertUnicode.setVisible(false);
		lblMsgSelected = new JLabel(loc.getMenu("Filling.CurrentSymbol") + ":");
		lblMsgSelected.setVisible(false);
		lblSymbols = new JLabel(loc.getMenu("Filling.Symbol") + ":");
		lblSymbols.setVisible(false);
		lblSelectedSymbol = new JLabel();
		lblSelectedSymbol.setFont(new Font("SansSerif", Font.PLAIN, 24));

		// JLabel sizeLabel = new JLabel(loc.getMenu("Filling") + ":");
		opacitySlider = new JSlider(0, 100);
		opacitySlider.setMajorTickSpacing(25);
		opacitySlider.setMinorTickSpacing(5);
		opacitySlider.setPaintTicks(true);
		opacitySlider.setPaintLabels(true);
		opacitySlider.setSnapToTicks(true);

		angleSlider = new JSlider(0, 180);
		// angleSlider.setPreferredSize(new Dimension(150,50));
		angleSlider.setMajorTickSpacing(45);
		angleSlider.setMinorTickSpacing(5);
		angleSlider.setPaintTicks(true);
		angleSlider.setPaintLabels(true);
		angleSlider.setSnapToTicks(true);

		// Create the label table
		Hashtable<Integer, JLabel> labelHash = new Hashtable<>();
		labelHash.put(Integer.valueOf(0), new JLabel("0" + Unicode.DEGREE_STRING));
		labelHash.put(Integer.valueOf(45),
				new JLabel(Unicode.FORTY_FIVE_DEGREES_STRING));
		labelHash.put(Integer.valueOf(90), new JLabel("90" + Unicode.DEGREE_STRING));
		labelHash.put(Integer.valueOf(135), new JLabel("135" + Unicode.DEGREE_STRING));
		labelHash.put(Integer.valueOf(180), new JLabel("180" + Unicode.DEGREE_STRING));
		angleSlider.setLabelTable(labelHash);

		distanceSlider = new JSlider(5, 50);
		// distanceSlider.setPreferredSize(new Dimension(150,50));
		distanceSlider.setMajorTickSpacing(10);
		distanceSlider.setMinorTickSpacing(5);
		distanceSlider.setPaintTicks(true);
		distanceSlider.setPaintLabels(true);
		distanceSlider.setSnapToTicks(true);

		/*
		 * Dimension dim = slider.getPreferredSize(); dim.width =
		 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
		 * slider.setPreferredSize(dim);
		 */

		// set label font
		Dictionary<?, ?> labelTable = opacitySlider.getLabelTable();
		Enumeration<?> en = labelTable.elements();
		JLabel label;
		while (en.hasMoreElements()) {
			label = (JLabel) en.nextElement();
			label.setFont(app.getSmallFont());
		}

		labelTable = angleSlider.getLabelTable();
		en = labelTable.elements();
		while (en.hasMoreElements()) {
			label = (JLabel) en.nextElement();
			label.setFont(app.getSmallFont());
		}

		labelTable = distanceSlider.getLabelTable();
		en = labelTable.elements();
		while (en.hasMoreElements()) {
			label = (JLabel) en.nextElement();
			label.setFont(app.getSmallFont());
		}

		// ========================================
		// create sub panels

		// panel for the fill type combobox
		cbFillType = new JComboBox();
		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel syPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lblFillType = new JLabel(loc.getMenu("Filling") + ":");
		cbFillInverse = new JCheckBox();
		lblFillInverse = new JLabel(loc.getMenu("InverseFilling"));
		cbPanel.add(lblFillType);
		cbPanel.add(cbFillType);
		cbPanel.add(cbFillInverse);
		cbPanel.add(lblFillInverse);
		syPanel.add(lblSymbols);
		syPanel.add(btInsertUnicode);
		syPanel.add(lblMsgSelected);
		lblSelectedSymbol.setAlignmentX(CENTER_ALIGNMENT);
		lblSelectedSymbol.setAlignmentY(CENTER_ALIGNMENT);
		lblSelectedSymbol.setVisible(false);
		syPanel.add(lblSelectedSymbol);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(cbPanel);
		panel.add(syPanel);
		// panels to hold sliders
		transparencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		transparencyPanel.add(opacitySlider);

		anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		anglePanel.add(angleSlider);

		distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		distancePanel.add(distanceSlider);

		// hatchfill panel: only shown when hatch fill option is selected
		hatchFillPanel = new JPanel();
		hatchFillPanel
				.setLayout(new BoxLayout(hatchFillPanel, BoxLayout.X_AXIS));
		hatchFillPanel.add(anglePanel);
		hatchFillPanel.add(distancePanel);
		hatchFillPanel.setVisible(false);

		// image panel: only shown when image fill option is selected
		createImagePanel();
		imagePanel.setVisible(false);

		// ===========================================================
		// put all the sub panels together

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(panel);
		this.add(transparencyPanel);
		this.add(hatchFillPanel);
		this.add(imagePanel);
	}

	/**
	 * @param enabled
	 *            enabled flag
	 */
	public void setAllEnabled(boolean enabled) {
		Component[] c = this.getComponents();
		for (int i = 0; i < c.length; i++) {
			Component[] subc = ((JPanel) c[i]).getComponents();
			for (int j = 0; j < subc.length; j++) {
				subc[j].setEnabled(enabled);
			}
		}
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		return update(geos);
	}

	@Override
	public void setLabels() {

		// setBorder(BorderFactory.createTitledBorder(loc.getMenu("Filling")));

		transparencyPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Opacity")));
		anglePanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Angle")));
		distancePanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Spacing")));
		imagePanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Images")));

		btnOpenFile.setText(loc.getMenu("ChooseFromFile") + Unicode.ELLIPSIS);

		// fill type combobox
		lblFillType.setText(loc.getMenu("Filling") + ":");

		int selectedIndex = cbFillType.getSelectedIndex();
		cbFillType.removeActionListener(this);
		cbFillType.removeAllItems();

		model.fillModes(loc);

		cbFillType.setSelectedIndex(selectedIndex);
		cbFillType.addActionListener(this);

	}

	private JPanel createImagePanel() {

		// =============================================
		// create array of image files from toolbar icons
		// for testing only ...

		imgFileNameList = new ArrayList<>();

		imgFileNameList.add(null); // for delete
		imgFileNameList.add(GuiResourcesD.FILLING_PAUSE);
		imgFileNameList.add(GuiResourcesD.FILLING_PLAY);
		imgFileNameList.add(GuiResourcesD.FILLING_STOP);
		imgFileNameList.add(GuiResourcesD.FILLING_REPLAY);
		imgFileNameList.add(GuiResourcesD.FILLING_SKIP_NEXT);
		imgFileNameList.add(GuiResourcesD.FILLING_SKIP_PREVIOUS);
		imgFileNameList.add(GuiResourcesD.FILLING_LOOP);
		imgFileNameList.add(GuiResourcesD.FILLING_ZOOM_IN);
		imgFileNameList.add(GuiResourcesD.FILLING_ZOOM_OUT);
		imgFileNameList.add(GuiResourcesD.FILLING_CLOSE);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_UP);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_DOWN);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_BACK);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_FORWARD);
		imgFileNameList.add(GuiResourcesD.FILLING_FAST_FORWARD);
		imgFileNameList.add(GuiResourcesD.FILLING_FAST_REWIND);

		ImageIcon[] iconArray = new ImageIcon[imgFileNameList.size()];
		iconArray[0] = GeoGebraIconD.createNullSymbolIcon(24, 24);
		for (int i = 1; i < iconArray.length; i++) {
			iconArray[i] = GeoGebraIconD.createFileImageIcon(
					imgFileNameList.get(i));
		}
		// ============================================

		// panel for button to open external file

		btnImage = new PopupMenuButtonD(app, iconArray, -1, 4,
				new Dimension(32, 32), SelectionTable.MODE_ICON);
		btnImage.setSelectedIndex(1);
		btnImage.setStandardButton(true);
		btnImage.setKeepVisible(false);
		btnImage.addActionListener(this);

		btnClearImage = new JButton(
				app.getScaledIcon(GuiResourcesD.DELETE_SMALL));
		btnClearImage.addActionListener(this);

		btnOpenFile = new JButton();
		btnOpenFile.addActionListener(this);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPanel.add(btnImage);
		btnPanel.add(btnClearImage);
		btnPanel.add(btnOpenFile);

		// =====================================
		// put all sub panels together

		imagePanel = new JPanel(new BorderLayout());
		imagePanel.add(btnPanel, BorderLayout.CENTER);
		return imagePanel;
	}

	@Override
	public void setStandardFillType() {
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(false);
		imagePanel.setVisible(false);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);
	}

	@Override
	public void setHatchFillType() {

		distanceSlider.removeChangeListener(this);
		distanceSlider.setMinimum(5);
		distanceSlider.addChangeListener(this);
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		angleSlider.removeChangeListener(this);
		angleSlider.setMaximum(180);
		angleSlider.setMinorTickSpacing(5);
		angleSlider.addChangeListener(this);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);
	}

	@Override
	public void setCrossHatchedFillType() {
		distanceSlider.removeChangeListener(this);
		distanceSlider.setMinimum(5);
		distanceSlider.addChangeListener(this);
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		// Only at 0, 45 and 90 degrees texturepaint not have mismatches
		angleSlider.removeChangeListener(this);
		angleSlider.setMaximum(45);
		angleSlider.setMinorTickSpacing(45);
		angleSlider.addChangeListener(this);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);

	}

	@Override
	public void setBrickFillType() {
		distanceSlider.removeChangeListener(this);
		distanceSlider.setMinimum(5);
		distanceSlider.addChangeListener(this);
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		anglePanel.setVisible(true);
		angleSlider.removeChangeListener(this);
		angleSlider.setMaximum(180);
		angleSlider.setMinorTickSpacing(45);
		angleSlider.addChangeListener(this);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);
	}

	@Override
	public void setSymbolFillType() {
		distanceSlider.removeChangeListener(this);
		distanceSlider.setMinimum(10);
		distanceSlider.addChangeListener(this);
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		// for dotted angle is useless
		anglePanel.setVisible(false);
		lblSymbols.setVisible(true);
		lblSelectedSymbol.setVisible(true);
		btInsertUnicode.setVisible(true);
	}

	@Override
	public void setDottedFillType() {
		distanceSlider.removeChangeListener(this);
		distanceSlider.setMinimum(5);
		distanceSlider.addChangeListener(this);
		transparencyPanel.setVisible(false);
		hatchFillPanel.setVisible(true);
		imagePanel.setVisible(false);
		// for dotted angle is useless
		anglePanel.setVisible(false);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);
	}

	@Override
	public void setImageFillType() {
		transparencyPanel.setVisible(true);
		hatchFillPanel.setVisible(false);
		imagePanel.setVisible(true);
		lblSymbols.setVisible(false);
		lblSelectedSymbol.setVisible(false);
		btInsertUnicode.setVisible(false);
		this.btnImage.setVisible(model.hasGeoButton());
		this.btnClearImage.setVisible(false);

		// for GeoButtons only show the image file button
		if (model.hasGeoButton() || model.hasGeoTurtle()) {
			transparencyPanel.setVisible(false);
			lblFillType.setVisible(false);
			cbFillType.setVisible(false);
			if (model.hasGeoTurtle()) {
				this.btnClearImage.setVisible(true);
			}
		}
	}

	/**
	 * @param geos
	 *            selected geos
	 * @return this or null (if geos can't be edited via this panel)
	 */
	public JPanel update(Object[] geos) {
		// check geos
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		cbFillType.removeActionListener(this);
		cbFillInverse.removeActionListener(this);
		opacitySlider.removeChangeListener(this);
		angleSlider.removeChangeListener(this);
		distanceSlider.removeChangeListener(this);

		model.updateProperties();

		cbFillType.addActionListener(this);
		cbFillInverse.addActionListener(this);
		opacitySlider.addChangeListener(this);
		angleSlider.addChangeListener(this);
		distanceSlider.addChangeListener(this);

		if (model.hasGeoButton()) {

			int index = 0;
			String imageFileName = model.getGeoAt(0).getImageFileName();

			for (int i = imgFileNameList.size() - 1; i >= 0; i--) {
				if (imageFileName.equals(imgFileNameList.get(i))) {
					index = i;
					break;
				}
			}

			btnImage.setSelectedIndex(index);
		} else {
			btnImage.setSelectedIndex(0);
		}
		addSelectionBar();
		return this;
	}

	@Override
	public void setFillInverseSelected(boolean value) {
		cbFillInverse.setSelected(value);
	}

	@Override
	public void setFillInverseVisible(boolean isVisible) {
		cbFillInverse.setVisible(isVisible);
		lblFillInverse.setVisible(isVisible);
	}

	@Override
	public void setFillTypeVisible(boolean isVisible) {
		lblFillType.setVisible(isVisible);
		cbFillType.setVisible(isVisible);
	}

	/**
	 * change listener implementation for slider
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// For barchart opacity color and
		// opacity image have same value if there is a tag
		if (e.getSource() == opacitySlider) {
			model.applyOpacity(opacitySlider.getValue());
			app.getKernel().notifyRepaint();
			return;
		}
		if (!angleSlider.getValueIsAdjusting()
				&& !distanceSlider.getValueIsAdjusting()) {
			model.applyAngleAndDistance(angleSlider.getValue(),
					distanceSlider.getValue());
		}
	}

	/**
	 * action listener for fill type combobox
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// handle change in fill type
		if (source == cbFillType) {
			model.applyFillType(cbFillType.getSelectedIndex());

		} else if (source == cbFillInverse) {

			model.applyFillingInverse(cbFillInverse.isSelected());

		}
		// handle image button selection
		else if (source == this.btnImage) {
			String fileName = null;
			if (btnImage.getSelectedIndex() == 0) {
				fileName = "";
			} else {
				fileName = imgFileNameList
						.get(btnImage.getSelectedIndex()).getFilename();
			}
			model.applyImage(fileName);
		} else if (source == this.btnClearImage) {
			model.applyImage("");
		}

		// handle load image file
		else if (source == btnOpenFile) {
			String fileName = ((GuiManagerD) app.getGuiManager())
					.getImageFromFile();
			model.applyImage(fileName);

		} else if (source == btInsertUnicode) {
			model.applyUnicode(lblSelectedSymbol.getText());

		}
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		transparencyPanel.setFont(font);
		anglePanel.setFont(font);
		distancePanel.setFont(font);
		imagePanel.setFont(font);

		btnOpenFile.setFont(font);
		lblFillType.setFont(font);
		cbFillType.setFont(font);

		lblFillInverse.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	private void addSelectionBar() {
		if (barsPanel != null) {
			remove(barsPanel);
		}
		GeoElement geo0 = model.getGeoAt(0);
		AlgoElement algo = geo0.getParentAlgorithm();
		if (algo instanceof ChartStyleAlgo) {
			int numBar = ((ChartStyleAlgo) algo).getIntervals();
			boolean isPie = algo instanceof AlgoPieChart;
			selectionBarButtons = new JToggleButton[numBar + 1];
			ButtonGroup group = new ButtonGroup();
			barsPanel = new JPanel(new GridLayout(0, 5, 5, 5));
			barsPanel.setBorder(new TitledBorder(loc.getMenu(isPie ?
					"SelectedSlice" : "SelectedBar")));
			for (int i = 0; i < numBar + 1; i++) {
				selectionBarButtons[i] = new JToggleButton(
						loc.getPlain(isPie? "SliceA" : "BarA", i + ""));
				selectionBarButtons[i].setSelected(false);
				selectionBarButtons[i].setActionCommand("" + i);
				selectionBarButtons[i].addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						selectedBarButton = Integer
								.parseInt(((JToggleButton) arg0.getSource())
										.getActionCommand());
						FillingPanelD.this.update(model.getGeos());
					}

				});
				group.add(selectionBarButtons[i]);
				barsPanel.add(selectionBarButtons[i]);
			}
			selectionBarButtons[0].setText(loc.getMenu(isPie ? "AllSlices" : "AllBars"));
			selectionBarButtons[selectedBarButton].setSelected(true);
			add(barsPanel);
		}
	}

	private void buildInsertUnicodeButton() {
		btInsertUnicode.removeAllMenuItems();

		btInsertUnicode.setKeepVisible(false);
		btInsertUnicode.setStandardButton(true);
		btInsertUnicode.setFixedIcon(GeoGebraIconD.createDownTriangleIcon(10));

		JMenu menu = new JMenu(loc.getMenu("Properties.Basic"));

		// Suits and music
		String[] fancy = StringUtil.getSetOfSymbols(0x2660, 16);
		btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));

		// Chess
		fancy = StringUtil.getSetOfSymbols(0x2654, 12);
		btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));

		// Stars
		fancy = StringUtil.getSetOfSymbols(0x2725, 3);
		String[] fancy2 = StringUtil.getSetOfSymbols(0x2729, 23);
		String[] union = new String[26];
		System.arraycopy(fancy, 0, union, 0, 3);
		System.arraycopy(fancy2, 0, union, 3, 23);
		btInsertUnicode.addPopupMenuItem(createMenuItem(union, -1, 4));

		// Squares
		fancy = StringUtil.getSetOfSymbols(0x2b12, 8);
		btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));
		app.setComponentOrientation(menu);

	}

	private JMenu createMenuItem(String[] table, int rows, int columns) {

		StringBuilder sb = new StringBuilder(7);
		sb.append(table[0]);
		sb.append(' ');
		sb.append(table[1]);
		sb.append(' ');
		sb.append(table[2]);
		sb.append("  ");

		JMenu menu = new JMenu(sb.toString());
		menu.add(new LatexTableFill(app, this, btInsertUnicode, table, rows,
				columns));

		menu.setFont(app.getFontCanDisplayAwt(sb.toString()));

		return menu;
	}

	/**
	 * Latex table for filling symbols
	 *
	 */
	class LatexTableFill extends SelectionTableD implements MenuElement {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] latexArray;
		private PopupMenuButtonD popupButton;

		/**
		 * @param app
		 *            application
		 * @param panel
		 *            panel
		 * @param popupButton
		 *            popup
		 * @param data
		 *            icons
		 * @param rows
		 *            numer of rows
		 * @param columns
		 *            number of columns
		 */
		public LatexTableFill(AppD app, FillingPanelD panel,
				PopupMenuButtonD popupButton, Object[] data, int rows,
				int columns) {
			super(app, data, rows, columns, new Dimension(24, 24),
					SelectionTable.MODE_TEXT);
			this.latexArray = data;
			this.popupButton = popupButton;
			setHorizontalAlignment(SwingConstants.CENTER);
			super.setSelectedIndex(0);
			this.setShowGrid(true);
			this.setGridColor(GColorD
					.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
			this.setBorder(
					BorderFactory.createLineBorder(MyTableD.TABLE_GRID_COLOR));
			this.setShowSelection(false);
		}

		@Override
		public Component getComponent() {
			return this;
		}

		@Override
		public MenuElement[] getSubElements() {
			return new MenuElement[0];
		}

		@Override
		public void menuSelectionChanged(boolean arg0) {
			// do nothing
		}

		@Override
		public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1,
				MenuSelectionManager arg2) {
			// do nothing
		}

		@Override
		public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1,
				MenuSelectionManager arg2) {

			if (this.getSelectedIndex() >= latexArray.length) {
				return;
			}

			if (arg0.getID() == MouseEvent.MOUSE_RELEASED) {

				// get the selected string
				Log.debug(
						"processMouseEvent, index: " + this.getSelectedIndex());
				String s = (String) latexArray[this.getSelectedIndex()];
				// if LaTeX string, adjust the string to include selected
				// text within braces

				if (s != null) {

					Log.debug("processMouseEvent, S: " + s);
					lblSelectedSymbol.setText(s);
					lblSelectedSymbol.setFont(app.getFontCanDisplayAwt(s));
				}
				Log.debug("handlePopupActionEvent begin");
				popupButton.handlePopupActionEvent();
				Log.debug("handlePopupActionEvent end");
			}
		}
	}

	@Override
	public void setSelectedIndex(int index) {
		cbFillType.setSelectedIndex(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItem(String item) {
		cbFillType.addItem(item);

	}

	@Override
	public void setSymbolsVisible(boolean isVisible) {

		if (isVisible) {
			btInsertUnicode.setVisible(true);
			lblSymbols.setVisible(true);
			lblSelectedSymbol.setVisible(true);
			lblMsgSelected.setVisible(true);
		} else {
			lblSymbols.setVisible(false);
			btInsertUnicode.setVisible(false);
			lblMsgSelected.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			lblSelectedSymbol.setText("");
		}
	}

	@Override
	public void setFillingImage(String imageFileName) {
		if (imageFileName != null) {

			int idx = 0;

			for (int i = imgFileNameList.size() - 1; i >= 0; i--) {
				if (imageFileName.equals(imgFileNameList.get(i))) {
					idx = i;
					break;
				}
			}
			btnImage.setSelectedIndex(idx);
		} else {
			btnImage.setSelectedIndex(-1);
		}
	}

	@Override
	public void setFillValue(int value) {
		opacitySlider.setValue(value);
	}

	@Override
	public void setAngleValue(int value) {
		angleSlider.removeChangeListener(this);
		angleSlider.setValue(value);
		angleSlider.addChangeListener(this);
	}

	@Override
	public void setDistanceValue(int value) {
		distanceSlider.removeChangeListener(this);
		distanceSlider.setValue(value);
		distanceSlider.addChangeListener(this);
	}

	@Override
	public int getSelectedBarIndex() {
		return selectedBarButton;
	}

	@Override
	public void selectSymbol(String symbol) {
		lblSelectedSymbol.setText(symbol);
	}

	@Override
	public String getSelectedSymbolText() {
		return lblSelectedSymbol.getText();
	}

	@Override
	public double getFillingValue() {
		return opacitySlider.getValue();
	}

	@Override
	public FillType getSelectedFillType() {
		return model.getFillTypeAt(cbFillType.getSelectedIndex());
	}

	@Override
	public int getDistanceValue() {
		return distanceSlider.getValue();
	}

	@Override
	public int getAngleValue() {
		return angleSlider.getValue();
	}

	@Override
	public void clearItems() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBarChart(int cols) {
		// TODO Auto-generated method stub

	}

}