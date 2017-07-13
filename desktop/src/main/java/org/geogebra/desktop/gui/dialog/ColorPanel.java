package org.geogebra.desktop.gui.dialog;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * panel color chooser and preview panel
 */
class ColorPanel extends JPanel
		implements ActionListener, UpdateablePropertiesPanel, ChangeListener,
		SetLabels, UpdateFonts, IColorObjectListener {

	/**
	 * 
	 */
	private final PropertiesPanelD propertiesPanelD;
	private static final long serialVersionUID = 1L;
	private ColorObjectModel model;
	private JLabel previewLabel, currentColorLabel;
	private PreviewPanel previewPanel;
	private JPanel opacityPanel, colorChooserContainer;
	private JRadioButton rbtnForegroundColor, rbtnBackgroundColor;
	private JButton btnClearBackground;

	private JSlider opacitySlider;
	private JPanel previewMetaPanel;

	private Color selectedColor;
	private JPanel southPanel;
	private JCheckBox automatic;

	// For handle single bar
	private JToggleButton[] selectionBarButtons;
	private int selectedBarButton;
	private JPanel barsPanel;
	private boolean isBarChart = false;

	public ColorPanel(PropertiesPanelD propertiesPanelD,
			GeoGebraColorChooser colChooser) {
		this.propertiesPanelD = propertiesPanelD;
		model = new ColorObjectModel(this.propertiesPanelD.app);
		model.setListener(this);
		previewPanel = new PreviewPanel();
		previewLabel = new JLabel();
		currentColorLabel = new JLabel();
		automatic = new JCheckBox();
		automatic.addActionListener(this);
		// prepare color chooser
		colChooser.setLocale(this.propertiesPanelD.app.getLocale());
		colChooser.getSelectionModel().addChangeListener(this);

		// get the color chooser panel
		AbstractColorChooserPanel colorChooserPanel = colChooser
				.getChooserPanels()[0];

		// create opacity slider
		opacitySlider = new JSlider(0, 100);
		opacitySlider.setMajorTickSpacing(25);
		opacitySlider.setMinorTickSpacing(5);
		opacitySlider.setPaintTicks(true);
		opacitySlider.setPaintLabels(true);
		opacitySlider.setSnapToTicks(true);

		updateSliderFonts();

		rbtnForegroundColor = new JRadioButton();
		rbtnBackgroundColor = new JRadioButton();
		ButtonGroup group = new ButtonGroup();
		group.add(rbtnForegroundColor);
		group.add(rbtnBackgroundColor);
		rbtnForegroundColor.setSelected(true);
		rbtnBackgroundColor.addActionListener(this);
		rbtnForegroundColor.addActionListener(this);

		btnClearBackground = new JButton(this.propertiesPanelD.app
				.getImageIcon(GuiResourcesD.DELETE_SMALL));
		btnClearBackground.setFocusPainted(false);
		btnClearBackground.addActionListener(this);

		// panel to hold color chooser
		colorChooserContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		colorChooserContainer.add(colorChooserPanel);

		// panel to hold opacity slider
		opacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		opacityPanel.add(opacitySlider);
		// panel to hold preview
		previewMetaPanel = new JPanel(new FlowLayout());
		previewMetaPanel.add(previewLabel);
		previewMetaPanel.add(previewPanel);
		previewMetaPanel.add(currentColorLabel);

		// vertical box panel that stacks the preview and opacity slider
		// together
		southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		southPanel.add(previewMetaPanel, c);
		southPanel.add(opacityPanel, c);
		c.gridwidth = 2;
		southPanel.add(rbtnForegroundColor, c);
		c.gridwidth = 1;
		southPanel.add(rbtnBackgroundColor, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 30, 0, 0);
		southPanel.add(btnClearBackground, c);

		// put the sub-panels together
		setLayout(new BorderLayout());
		southPanel.add(automatic);

		add(colorChooserContainer, BorderLayout.NORTH);
		add(southPanel, this.propertiesPanelD.loc.borderWest());
	}

	/**
	 * Extended JPanel that draws a preview rectangle filled with the color of
	 * the currently selected GeoElement(s). If the geo is fillable the panel
	 * paints a transparent rectangle using the geo's alpha value. An opaque 2
	 * pixel border is drawn around the transparent interior.
	 * 
	 */
	protected class PreviewPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private Color alphaFillColor;

		public PreviewPanel() {
			setPreferredSize(new Dimension(80,
					ColorPanel.this.propertiesPanelD.app.getGUIFontSize()
							+ 16));
			setMaximumSize(this.getPreferredSize());
			this.setBorder(BorderFactory.createEmptyBorder());
			this.setBackground(null);
			this.setOpaque(true);
		}

		/**
		 * Sets the preview colors.
		 * 
		 * @param color
		 * @param alpha
		 */
		public void setPreview(Color color, double alpha) {

			if (color == null) {
				alphaFillColor = getBackground();
				setForeground(getBackground());
			} else {
				float[] rgb = new float[3];
				color.getRGBColorComponents(rgb);
				alphaFillColor = new Color(rgb[0], rgb[1], rgb[2],
						(float) alpha);
				setForeground(new Color(rgb[0], rgb[1], rgb[2], 1f));
			}
			this.repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			Insets insets = getInsets();
			int w = this.getWidth() - insets.left - insets.right;
			int h = this.getHeight() - insets.top - insets.bottom;

			g2.setPaint(Color.WHITE);
			g.fillRect(insets.left, insets.top, w, h);

			g2.setPaint(alphaFillColor);
			g.fillRect(insets.left, insets.top, w, h);

			g2.setPaint(getForeground());
			g2.setStroke(new BasicStroke(3));
			g.drawRect(insets.left + 3, insets.top + 3, w - 7, h - 7);

			g2.setPaint(Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(1));
			g.drawRect(insets.left, insets.top, w - 1, h - 1);

			g2.setPaint(Color.WHITE);
			g2.setStroke(new BasicStroke(1));
			g2.drawRect(insets.left + 1, insets.top + 1, w - 3, h - 3);

		}
	}

	@Override
	public void setLabels() {
		Localization loc = this.propertiesPanelD.app.getLocalization();
		previewLabel
				.setText(loc.getMenu("Preview") + ": ");
		opacityPanel.setBorder(BorderFactory.createTitledBorder(
				loc.getMenu("Opacity")));
		this.propertiesPanelD.colChooser
				.setLocale(this.propertiesPanelD.app.getLocale());
		rbtnBackgroundColor.setText(loc.getMenu("BackgroundColor"));
		rbtnForegroundColor.setText(loc.getMenu("ForegroundColor"));
		btnClearBackground.setToolTipText(loc.getMenu("Remove"));
		this.automatic.setText(loc.getMenu("Automatic"));
		updateToolTipText();
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		addSelectionBar();
		return update();
	}

	public JPanel update() {

		if (!model.checkGeos()) {
			return null;
		}

		model.updateProperties();

		rbtnBackgroundColor.setVisible(model.hasBackground());
		rbtnForegroundColor.setVisible(model.hasBackground());
		btnClearBackground.setVisible(
				rbtnBackgroundColor.isSelected() && model.hasBackground());
		btnClearBackground.setEnabled(rbtnBackgroundColor.isSelected());
		// hide the color chooser and preview if we have an image
		colorChooserContainer
				.setVisible(!model.hasImageGeo() && !model.isSequentialColor());
		previewMetaPanel.setVisible(!model.hasImageGeo());
		automatic.setVisible(model.hasDefaultGeos());
		automatic.setSelected(model.isSequentialColor());
		return this;
	}

	// Methods that set value for single bar if single bar is selected
	// and bar has tag for value

	private void setPreview(GeoElement geo, double alpha0) {
		AlgoBarChart algo = (AlgoBarChart) geo.getParentAlgorithm();
		double alpha = alpha0;
		if (selectedBarButton != 0
				&& (algo.getBarAlpha(selectedBarButton) != -1)) {
			alpha = algo.getBarAlpha(selectedBarButton);
		}
		previewPanel.setPreview(selectedColor, alpha);
	}

	private void setOpacitySlider(GeoElement geo, double alpha) {
		/*
		 * AlgoBarChart algo=(AlgoBarChart) geo.getParentAlgorithm(); if
		 * (selectedBarButton != 0 && algo.getBarAlpha(selectedBarButton) != -1)
		 * { alpha = algo.getBarAlpha(selectedBarButton); }
		 * opacitySlider.setValue(Math.round(alpha * 100));
		 */
	}

	private void setChooser(GeoElement geo0) {
		if (geo0.getParentAlgorithm() instanceof AlgoBarChart) {
			AlgoBarChart algo = (AlgoBarChart) geo0.getParentAlgorithm();
			if (selectedBarButton != 0
					&& algo.getBarColor(selectedBarButton) != null) {
				GColor color = algo.getBarColor(selectedBarButton);
				selectedColor = new Color(color.getRed(), color.getGreen(),
						color.getBlue(), color.getAlpha());
			}
		}
		this.propertiesPanelD.colChooser.getSelectionModel()
				.setSelectedColor(selectedColor);
	}

	private void updateToolTipText() {
		// set the preview tool tip and color label text for the chosen
		// color
		if (selectedColor == null) {
			previewPanel.setToolTipText("");
		} else {
			previewPanel.setToolTipText(
					getToolTipText(propertiesPanelD.app, selectedColor));
		}
		currentColorLabel.setText(previewPanel.getToolTipText());
	}

	/**
	 * Sets the tooltip string for a given color
	 * 
	 * @param app
	 *            application
	 * 
	 * @param color
	 *            color
	 * @return tooltip
	 */
	public String getToolTipText(App app, Color color) {
		return ColorObjectModel.getColorAsString(app, GColorD.newColor(color));
	}

	// Add tag for color and alpha or remove if selected all bars
	private void updateBarsColorAndAlpha(GeoElement geo, Color col,
			double alpha,
			boolean updateAlphaOnly) {
		AlgoBarChart algo = (AlgoBarChart) geo.getParentAlgorithm();
		if (selectedBarButton == 0) {
			for (int i = 1; i < selectionBarButtons.length; i++) {
				algo.setBarColor(null, i);
				algo.setBarAlpha(-1, i);
			}
			geo.setAlphaValue(alpha);
			if (!updateAlphaOnly) {
				geo.setObjColor(GColorD.newColor(col));
			}
			algo.setBarAlpha(alpha, selectedBarButton);
			return;
		}
		if (!updateAlphaOnly) {
			algo.setBarColor(GColorD.newColor(col), selectedBarButton);
		}
		algo.setBarAlpha(alpha, selectedBarButton);
		// For barchart opacity color and
		// opacity image have same value if there is a tag
		this.propertiesPanelD.fillingPanel.opacitySlider
				.removeChangeListener(this.propertiesPanelD.fillingPanel);
		this.propertiesPanelD.fillingPanel.opacitySlider
				.setValue((int) Math.round(alpha * 100));
		this.propertiesPanelD.fillingPanel.opacitySlider
				.addChangeListener(this.propertiesPanelD.fillingPanel);
	}

	// Add panel for single bar if is a BarChart
	private void addSelectionBar() {
		if (barsPanel != null) {
			remove(barsPanel);
		}
		// AlgoElement algo = model.getGeoAt(0).getParentAlgorithm();
		if (model.isBarChart()) {
			int numBar = model.getBarChartIntervals();
			isBarChart = true;
			selectionBarButtons = new JToggleButton[numBar + 1];
			ButtonGroup group = new ButtonGroup();
			barsPanel = new JPanel(new GridLayout(0, 3, 5, 5));
			barsPanel.setBorder(new TitledBorder(
					this.propertiesPanelD.loc.getMenu("SelectedBar")));
			for (int i = 0; i < numBar + 1; i++) {
				selectionBarButtons[i] = new JToggleButton(
						this.propertiesPanelD.loc.getPlain("BarA", i + ""));
				selectionBarButtons[i].setSelected(false);
				selectionBarButtons[i].setActionCommand("" + i);
				selectionBarButtons[i].addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						selectedBarButton = Integer
								.parseInt(((JToggleButton) arg0.getSource())
										.getActionCommand());
						ColorPanel.this.update();
					}

				});
				barsPanel.add(selectionBarButtons[i]);
				group.add(selectionBarButtons[i]);
			}
			selectionBarButtons[0]
					.setText(this.propertiesPanelD.loc.getMenu("AllBars"));
			selectionBarButtons[selectedBarButton].setSelected(true);
			add(barsPanel, this.propertiesPanelD.loc.borderEast());
		}
	}

	/**
	 * Listens for color chooser state changes
	 */
	@Override
	public void stateChanged(ChangeEvent e) {

		float alpha = opacitySlider.getValue() / 100.0f;
		GColor color = GColorD
				.newColor(this.propertiesPanelD.colChooser.getColor());
		if (e.getSource() == opacitySlider) {
			model.applyChanges(color, alpha, true);
		} else {
			model.applyChanges(color, alpha, false);
		}

	}

	/**
	 * action listener implementation for label mode combobox
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == rbtnBackgroundColor || source == rbtnForegroundColor) {
			addSelectionBar();
			update();
		}

		if (source == btnClearBackground) {
			model.clearBackgroundColor();
			addSelectionBar();
			update();
		}
		if (source == automatic) {
			model.setSequential(automatic.isSelected());
			update();
		}
	}

	@Override
	public void updateFonts() {
		Font font = this.propertiesPanelD.app.getPlainFont();

		previewLabel.setFont(font);
		currentColorLabel.setFont(font);
		opacityPanel.setFont(font);
		// colChooser.setFont(font);
		rbtnBackgroundColor.setFont(font);
		rbtnForegroundColor.setFont(font);
		btnClearBackground.setFont(font);

		updateSliderFonts();

	}

	private void updateSliderFonts() {
		// set slider label font
		Dictionary<?, ?> labelTable = opacitySlider.getLabelTable();
		Enumeration<?> en = labelTable.elements();
		JLabel label;
		while (en.hasMoreElements()) {
			label = (JLabel) en.nextElement();
			label.setFont(this.propertiesPanelD.app.getSmallFont());
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		if (model.getGeos() == null) {
			return;
		}
		update();
	}

	@Override
	public void updateChooser(boolean equalObjColor,
			boolean equalObjColorBackground, boolean allFillable,
			boolean hasBackground, boolean hasOpacity) {
		// initialize selected color and opacity
		selectedColor = null;
		Color selectedBGColor = null;
		double alpha = 1;
		GeoElement geo0 = model.getGeoAt(0);
		if (equalObjColorBackground) {
			selectedBGColor = GColorD.getAwtColor(geo0.getBackgroundColor());
		}

		if (isBackgroundColorSelected()) {
			selectedColor = selectedBGColor;
		} else {
			// set selectedColor if all selected geos have the same color
			if (equalObjColor) {
				if (allFillable) {
					selectedColor = GColorD.getAwtColor(geo0.getFillColor());
					alpha = geo0.getAlphaValue();

					// can be -1 for lists
					if (alpha < 0) {
						alpha = 0;
					}
				} else {
					selectedColor = GColorD.getAwtColor(geo0.getObjectColor());
				}
			}
		}

		updateToolTipText();

		// set the chooser color
		this.propertiesPanelD.colChooser.getSelectionModel()
				.removeChangeListener(this);
		if (isBarChart) {
			setChooser(geo0);
		} else {
			this.propertiesPanelD.colChooser.getSelectionModel()
					.setSelectedColor(selectedColor);
		}
		this.propertiesPanelD.colChooser.getSelectionModel()
				.addChangeListener(this);

		// set the opacity
		opacitySlider.removeChangeListener(this);
		if (allFillable && hasOpacity) { // show opacity slider and set to
			// first geo's
			// alpha value
			opacityPanel.setVisible(true);

			alpha = geo0.getAlphaValue();
			// can be -1 for lists
			if (alpha < 0) {
				alpha = 0;
			}

			if (isBarChart) {
				setOpacitySlider(geo0, alpha);
			} else {
				opacitySlider.setValue((int) Math.round(alpha * 100));
			}
		} else { // hide opacity slider and set alpha = 1
			opacityPanel.setVisible(false);
			alpha = 1;
			opacitySlider.setValue((int) Math.round(alpha * 100));
		}
		opacitySlider.addChangeListener(this);

		// set the preview panel (do this after the alpha level is set
		// above)
		if (geo0.getParentAlgorithm() instanceof AlgoBarChart) {
			isBarChart = true;
			setPreview(geo0, alpha);
		} else {
			isBarChart = false;
			previewPanel.setPreview(selectedColor, alpha);
		}

	}

	@Override
	public void updatePreview(GColor col, double alpha) {
		// update preview panel
		Color color = GColorD.getAwtColor(col);
		previewPanel.setPreview(color, alpha);
		previewPanel
				.setToolTipText(getToolTipText(propertiesPanelD.app, color));
		currentColorLabel.setText(previewPanel.getToolTipText());

	}

	@Override
	public boolean isBackgroundColorSelected() {
		return rbtnBackgroundColor.isSelected();
	}

	@Override
	public void updateNoBackground(GeoElement geo, GColor col, double alpha,
			boolean updateAlphaOnly, boolean allFillable) {

		Color color = GColorD.getAwtColor(col);
		if (!updateAlphaOnly) {
			if (isBarChart) {
				updateBarsColorAndAlpha(geo, color, alpha, updateAlphaOnly);
			} else {
				geo.setObjColor(col);
			}
		}
		if (allFillable) {
			if (isBarChart) {
				updateBarsColorAndAlpha(geo, color, alpha, updateAlphaOnly);
			} else {
				geo.setAlphaValue(alpha);
			}
		}
		automatic.setSelected(geo.isAutoColor());

	}

} // ColorPanel