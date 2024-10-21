package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.util.SliderUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * panel to select the filling of a polygon or conic section
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
	private final JSlider angleSlider;
	private final JSlider distanceSlider;
	private final JComboBox cbFillType;
	private final JCheckBox cbFillInverse;

	private final JPanel transparencyPanel;
	private final JPanel hatchFillPanel;
	private JPanel imagePanel;
	private final JPanel anglePanel;
	private final JPanel distancePanel;
	private final JLabel lblFillType;
	/** symbol used for filling */
	JLabel lblSelectedSymbol;
	private final JLabel lblMsgSelected;
	private JButton btnOpenFile;

	private PopupMenuButtonD btnImage;
	// button for removing turtle's image
	private JButton btnClearImage;
	private final JLabel lblFillInverse;
	private final JLabel lblSymbols;
	private ArrayList<ImageResourceD> imgFileNameList;
	private final PopupMenuButtonD btInsertUnicode;

	// For handle single bar
	private JPanel barsPanel;
	/** selected button */
	int selectedBarButton;
	/** application */
	AppD app;
	private final Localization loc;
	List<String> fancy;

	/**
	 * New filling panel
	 * @param app application
	 */
	public FillingPanelD(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();
		// For filling whit unicode char
		model = new FillingModel(app);
		model.setListener(this);
		btInsertUnicode = buildInsertUnicodeButton(app);
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
		labelHash.put(0, new JLabel("0" + Unicode.DEGREE_STRING));
		labelHash.put(45,
				new JLabel(Unicode.FORTY_FIVE_DEGREES_STRING));
		labelHash.put(90, new JLabel("90" + Unicode.DEGREE_STRING));
		labelHash.put(135, new JLabel("135" + Unicode.DEGREE_STRING));
		labelHash.put(180, new JLabel("180" + Unicode.DEGREE_STRING));
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
		lblFillType = new JLabel(loc.getMenu("Filling") + ":");
		cbFillInverse = new JCheckBox();
		lblFillInverse = new JLabel(loc.getMenu("InverseFilling"));
		cbPanel.add(lblFillType);
		cbPanel.add(cbFillType);
		cbPanel.add(cbFillInverse);
		cbPanel.add(lblFillInverse);
		JPanel syPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

		int cbFillTypeSelectedIndex = cbFillType.getSelectedIndex();

		cbFillType.removeActionListener(this);
		cbFillType.removeAllItems();
		model.fillModes(loc);

		cbFillType.setSelectedIndex(cbFillTypeSelectedIndex);
		cbFillType.addActionListener(this);
	}

	private JPanel createImagePanel() {

		// =============================================
		// create array of image files from toolbar icons
		// for testing only ...

		imgFileNameList = new ArrayList<>();

		imgFileNameList.add(null); // for delete
		imgFileNameList.add(GuiResourcesD.FILLING_PLAY);
		imgFileNameList.add(GuiResourcesD.FILLING_PAUSE);
		imgFileNameList.add(GuiResourcesD.FILLING_STOP);
		imgFileNameList.add(GuiResourcesD.FILLING_FAST_REWIND);
		imgFileNameList.add(GuiResourcesD.FILLING_FAST_FORWARD);
		imgFileNameList.add(GuiResourcesD.FILLING_SKIP_PREVIOUS);
		imgFileNameList.add(GuiResourcesD.FILLING_SKIP_NEXT);
		imgFileNameList.add(GuiResourcesD.FILLING_LOOP);
		imgFileNameList.add(GuiResourcesD.FILLING_REPLAY);
		imgFileNameList.add(GuiResourcesD.UNDO);
		imgFileNameList.add(GuiResourcesD.REDO);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_UP);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_DOWN);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_BACK);
		imgFileNameList.add(GuiResourcesD.FILLING_ARROW_FORWARD);
		imgFileNameList.add(GuiResourcesD.REMOVE);
		imgFileNameList.add(GuiResourcesD.ADD);
		imgFileNameList.add(GuiResourcesD.CHECK_MARK);
		imgFileNameList.add(GuiResourcesD.FILLING_CLOSE);
		imgFileNameList.add(GuiResourcesD.FILLING_ZOOM_OUT);
		imgFileNameList.add(GuiResourcesD.FILLING_ZOOM_IN);
		imgFileNameList.add(GuiResourcesD.FILLING_ZOOM_TO_FIT);
		imgFileNameList.add(GuiResourcesD.FILLING_CENTER_VIEW);
		imgFileNameList.add(GuiResourcesD.FILLING_HELP);
		imgFileNameList.add(GuiResourcesD.FILLING_SETTINGS);

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
	 * @param geos selected geos
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
		SliderUtil.addValueChangeListener(opacitySlider, val -> model.storeUndoInfo());
		angleSlider.addChangeListener(this);
		SliderUtil.addValueChangeListener(angleSlider, val -> model.storeUndoInfo());
		distanceSlider.addChangeListener(this);
		SliderUtil.addValueChangeListener(distanceSlider, val -> model.storeUndoInfo());

		if (model.hasGeoButton()) {
			int index = 0;
			String imageFileName = model.getGeoAt(0).getImageFileName();

			for (int i = imgFileNameList.size() - 1; i >= 0; i--) {
				if (imgFileNameList.get(i) != null
						&& imageFileName.equals(imgFileNameList.get(i).getFilename())) {
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
		model.applyAngleAndDistance(angleSlider.getValue(),
				distanceSlider.getValue());
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
			String fileName;
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
			model.applyUnicode((String) btInsertUnicode.getSelectedValue());
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

	private void addSelectionBar() {
		if (barsPanel != null) {
			remove(barsPanel);
		}
		GeoElement geo0 = model.getGeoAt(0);
		if (geo0 instanceof ChartStyleGeo) {
			int numBar = ((ChartStyleGeo) geo0).getIntervals();
			boolean isPie = geo0 instanceof GeoPieChart;
			JToggleButton[] selectionBarButtons = new JToggleButton[numBar + 1];
			ButtonGroup group = new ButtonGroup();
			barsPanel = new JPanel(new GridLayout(0, 5, 5, 5));
			barsPanel.setBorder(new TitledBorder(loc.getMenu(
					isPie ? "SelectedSlice" : "SelectedBar")));
			for (int i = 0; i < numBar + 1; i++) {
				selectionBarButtons[i] = new JToggleButton(
						loc.getPlain(isPie ? "SliceA" : "BarA", i + ""));
				selectionBarButtons[i].setSelected(false);
				selectionBarButtons[i].setActionCommand("" + i);
				selectionBarButtons[i].addActionListener(arg0 -> {
					selectedBarButton = Integer
							.parseInt(((JToggleButton) arg0.getSource())
									.getActionCommand());
					this.update(model.getGeos());
				});
				group.add(selectionBarButtons[i]);
				barsPanel.add(selectionBarButtons[i]);
			}
			selectionBarButtons[0].setText(loc.getMenu(isPie ? "AllSlices" : "AllBars"));
			selectionBarButtons[selectedBarButton].setSelected(true);
			add(barsPanel);
		}
	}

	private PopupMenuButtonD buildInsertUnicodeButton(AppD app) {
		// Suits and music
		fancy = StringUtil.getSetOfSymbols(0x2660, 16);
		// Chess
		fancy.addAll(StringUtil.getSetOfSymbols(0x2654, 12));
		// Stars
		fancy.addAll(StringUtil.getSetOfSymbols(0x2725, 3));
		fancy.addAll(StringUtil.getSetOfSymbols(0x2729, 23));
		fancy.add("$");
		fancy.add("#");
		// Squares
		fancy.addAll(StringUtil.getSetOfSymbols(0x2b12, 8));

		PopupMenuButtonD popupButton = new PopupMenuButtonD(app, fancy.toArray(new String[0]),
				-1, 8, new Dimension(32, 32), SelectionTable.MODE_TEXT);
		popupButton.setKeepVisible(false);
		popupButton.setStandardButton(true);
		return popupButton;
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
				if (imgFileNameList.get(i) != null
						&& imageFileName.equals(imgFileNameList.get(i).getFilename())) {
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
		btInsertUnicode.setSelectedIndex(fancy.indexOf(symbol));
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