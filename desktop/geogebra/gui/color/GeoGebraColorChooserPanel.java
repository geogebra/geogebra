package geogebra.gui.color;

import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A color swatch chooser panel for GeoGebra.
 * 
 * @author G. Sturr
 */
public class GeoGebraColorChooserPanel extends AbstractColorChooserPanel {

	private static final long serialVersionUID = 1L;

	protected AppD app;
	protected GeoGebraColorChooser enclosingChooser;
	protected GeoGebraColorChooserPanel myChooser;

	protected MainSwatchPanel mainSwatchPanel;
	protected RecentSwatchPanel recentSwatchPanel;
	protected PrimarySwatchPanel primarySwatchPanel;
	protected CustomSwatchPanel customSwatchPanel;

	protected SwatchListener swatchListener;
	protected ArrayList<SwatchPanel> swatchPanelList;

	private JButton btnCustomColor;
	private JLabel lblRecent, lblCustom;
	private JPanel recentPanel, customPanel;

	protected static final int largeSwatchSize = 16;
	protected static final int smallSwatchSize = 14;

	/**********************************************************
	 * Constructs a color chooser panel
	 * 
	 * @param app
	 */
	public GeoGebraColorChooserPanel(AppD app) {
		super();
		this.app = app;
		this.myChooser = this;
	}

	@Override
	public String getDisplayName() {
		return UIManager.getString("ColorChooser.swatchesNameText");
	}

	@Override
	public int getMnemonic() {
		// return getInt("ColorChooser.swatchesMnemonic", -1);
		return -1;
	}

	@Override
	public int getDisplayedMnemonicIndex() {
		// return getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
		return -1;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	@Override
	public void installChooserPanel(JColorChooser enclosingChooser) {
		super.installChooserPanel(enclosingChooser);
		this.enclosingChooser = (GeoGebraColorChooser) enclosingChooser;
	}

	@Override
	protected void buildChooser() {

		// create the swatch panels and other GUI elements
		createGUIElements();

		// create a GridBagLayout
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, 10);

		// create a panel to hold all of our GUI
		JPanel mainPanel = new JPanel(gb);

		// add the primary swatch panel on the far left
		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(primarySwatchPanel, gbc);

		// add the main swatch panel in the middle
		gbc.gridheight = 2;
		gbc.gridx = 1;
		gbc.gridy = 0;
		mainPanel.add(mainSwatchPanel, gbc);

		// stack the recent panel, custom panel and RGB button on the far right
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 0;
		gbc.gridheight = 1;
		gbc.gridx = 2;
		gbc.gridy = 0;
		mainPanel.add(recentPanel, gbc);

		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.weighty = 1;
		gbc.gridx = 2;
		gbc.gridy = 1;
		mainPanel.add(customPanel, gbc);

		add(mainPanel);

	}

	private void createGUIElements() {

		// create the swatch panels
		mainSwatchPanel = new MainSwatchPanel();
		recentSwatchPanel = new RecentSwatchPanel();
		primarySwatchPanel = new PrimarySwatchPanel();
		customSwatchPanel = new CustomSwatchPanel();

		swatchPanelList = new ArrayList<SwatchPanel>();
		swatchPanelList.add(mainSwatchPanel);
		swatchPanelList.add(primarySwatchPanel);
		swatchPanelList.add(recentSwatchPanel);
		swatchPanelList.add(customSwatchPanel);

		// create a mouse listener and register it with each swatch panel
		swatchListener = new SwatchListener();
		for (SwatchPanel sp : swatchPanelList) {
			sp.addMouseListener(swatchListener);
		}

		// add borders to the swatch panels
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.gray, 1),
				BorderFactory.createLineBorder(Color.white, 1));
		for (SwatchPanel sp : swatchPanelList) {
			sp.setBorder(border);
		}

		// create a panel with label to contain the recent swatch panel
		recentPanel = new JPanel(new BorderLayout());
		lblRecent = new JLabel();
		lblRecent.setLabelFor(recentSwatchPanel);
		recentPanel.add(lblRecent, BorderLayout.NORTH);
		recentPanel.add(recentSwatchPanel, BorderLayout.CENTER);
		recentPanel.setMaximumSize(getPreferredSize());

		// create a button to open a RGB color chooser for custom colors
		btnCustomColor = new JButton(app.getImageIcon("list-add.png"));
		btnCustomColor.addActionListener(new CustomButtonActionListener());
		btnCustomColor.setPreferredSize(new Dimension(24, 18));
		btnCustomColor.setFocusPainted(false);

		// create a panel with label to contain the custom swatch panel
		lblCustom = new JLabel();
		lblCustom.setLabelFor(customSwatchPanel);
		customPanel = new JPanel(new BorderLayout());
		customPanel.add(lblCustom, BorderLayout.NORTH);
		customPanel.add(customSwatchPanel, BorderLayout.CENTER);
		customPanel.add(LayoutUtil.flowPanel(0, 2, 0, btnCustomColor),
				BorderLayout.SOUTH);
		customPanel.setMaximumSize(getPreferredSize());

		// set the labels
		setLabels();

	}

	@Override
	public void uninstallChooserPanel(JColorChooser enclosingChooser) {
		super.uninstallChooserPanel(enclosingChooser);
		for (SwatchPanel sp : swatchPanelList) {
			sp.removeMouseListener(swatchListener);
			sp = null;
		}
		swatchListener = null;
		removeAll(); // strip out all the sub-components
	}

	@Override
	public void updateChooser() {
		setSwatchPanelSelection(getColorSelectionModel().getSelectedColor());

	}

	/**
	 * Sets the visual feedback for the swatch panels so that the appropriate
	 * panel shows the current selection.
	 * 
	 * @param color
	 * @return
	 */
	public boolean setSwatchPanelSelection(Color color) {

		// clear visual feedback for swatch selection in the swatch panels
		for (SwatchPanel panel : swatchPanelList) {
			panel.setSelectionFromLocation(-1, -1);
		}

		// exit if the chooser null selection flag is set
		// (JColorChooser doesn't handle null color ... see class
		// GeoGebraColorChooser for workaround)
		if (enclosingChooser != null && enclosingChooser.isNullSelection())
			return true;

		// set the selected swatch visual feedback in the appropriate panel
		if (primarySwatchPanel.setSelectionFromColor(color))
			return true;
		else if (mainSwatchPanel.setSelectionFromColor(color))
			return true;
		else
			return customSwatchPanel.setSelectionFromColor(color);

	}

	/**
	 * Set localized strings.
	 */
	public void setLabels() {
		btnCustomColor.setToolTipText(app.getMenu("AddCustomColor"));
		lblCustom.setText(app.getMenu("Other") + ":");
		lblRecent.setText(app.getMenu("RecentColor") + ":");
	}
	
	public void updateFonts(){
		Font font = app.getPlainFont();
		btnCustomColor.setFont(font);
		lblCustom.setFont(font);
		lblRecent.setFont(font);
	}

	/**
	 * MouseListener for the swatch panels.
	 * 
	 */
	class SwatchListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {

			if (e.getSource() instanceof SwatchPanel) {

				SwatchPanel mySwatchPanel = (SwatchPanel) e.getSource();

				// exit if the mouse is not over a color swatch
				if (!mySwatchPanel.isSwatchLocation(e.getX(), e.getY()))
					return;

				// set the color selection to the color of the cell the mouse is
				// above
				Color color = mySwatchPanel.getColorForLocation(e.getX(),
						e.getY());
				getColorSelectionModel().setSelectedColor(color);

				// update the the recent swatch panel
				if (mySwatchPanel != recentSwatchPanel)
					recentSwatchPanel.setMostRecentColor(color);
			}
		}
	}

	/**
	 * Action listener for the custom color button. Creates and shows a color
	 * chooser dialog with a RGB color chooser panel.
	 * 
	 */
	class CustomButtonActionListener implements ActionListener {

		JColorChooser chooser;

		public void actionPerformed(ActionEvent arg0) {

			chooser = new JColorChooser();
			chooser.setColor(myChooser.getColorFromModel());
			DefaultRGBChooserPanel rgb = new DefaultRGBChooserPanel(app);
			AbstractColorChooserPanel panels[] = { rgb };
			chooser.setChooserPanels(panels);
			chooser.setPreviewPanel(rgb.getPreview());

			// show the chooser dialog
			JDialog dialog = JColorChooser.createDialog(app.getMainComponent(),
					app.getPlain("ChooseColor"), true, chooser,
					okActionListener, null);
			dialog.setVisible(true);

		}

		ActionListener okActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				customSwatchPanel.addCustomColor(chooser.getColor());
			}
		};

	}

	/**********************************************************
	 * 
	 * Base class for all swatch panels.
	 * 
	 **********************************************************/
	class SwatchPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		protected geogebra.common.awt.GColor[] colors;

		protected Dimension swatchSize = new Dimension(largeSwatchSize,
				largeSwatchSize);
		protected Dimension gap = new Dimension(1, 1);
		protected Dimension numSwatches;
		protected Dimension selectedSwatch = new Dimension(-1, -1);
		protected Dimension hoverSwatch = new Dimension(-1, -1);
		protected Dimension prevHoverSwatch = new Dimension(-1, -1);

		protected int swatchCount = 0;

		public SwatchPanel() {

			initValues();
			initColors();
			initSwatchCount();
			setToolTipText(""); // register for events
			setOpaque(true);
			setBackground(Color.white);
			setRequestFocusEnabled(false);
			addMouseListener(new SwatchMouseListener());
			addMouseMotionListener(new SwatchMouseMotionListener());

			setMaximumSize(getPreferredSize());
		}

		@Deprecated
		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		protected void initValues() {
		}

		protected void initColors() {

		}

		protected void initSwatchCount() {
			swatchCount = colors.length;
		}

		class SwatchMouseMotionListener extends MouseMotionAdapter {
			@Override
			public void mouseMoved(MouseEvent e) {
				updateHoverSwatch(e.getX(), e.getY());
			}
		}

		class SwatchMouseListener extends MouseAdapter {
			@Override
			public void mouseExited(MouseEvent e) {
				updateHoverSwatch(-1, -1);
			}
		}

		protected void updateHoverSwatch(int x, int y) {

			prevHoverSwatch.width = hoverSwatch.width;
			prevHoverSwatch.height = hoverSwatch.height;
			setCellFromLocation(x, y, hoverSwatch);

			if (prevHoverSwatch.width != hoverSwatch.width
					|| prevHoverSwatch.height != hoverSwatch.height) {
				repaint();
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			// g2d.setStroke(new BasicStroke(2));

			Insets insets = getInsets();
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());

			for (int row = 0; row < numSwatches.height; row++) {
				for (int column = 0; column < numSwatches.width; column++) {

					g2d.setColor(getColorForCell(column, row));
					int x;
					if ((!this.getComponentOrientation().isLeftToRight())
							&& (this instanceof RecentSwatchPanel)) {
						x = (numSwatches.width - column - 1)
								* (swatchSize.width + gap.width) + insets.left;
					} else {
						x = column * (swatchSize.width + gap.width)
								+ insets.left;
					}
					int y = row * (swatchSize.height + gap.height) + insets.top;

					g2d.fill3DRect(x + 1, y + 1, swatchSize.width - 1,
							swatchSize.height - 1, true);

					if (isSwatchCell(column, row)) {

						if (selectedSwatch != null
								&& row == selectedSwatch.height
								&& column == selectedSwatch.width) {
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawRect(x, y, swatchSize.width,
									swatchSize.height);

							if (app != null)
								g2d.drawImage(
										app.getImageIcon(
												"color_chooser_check.png")
												.getImage(), x + 3, y + 3, null);
						}

						if (hoverSwatch != null && row == hoverSwatch.height
								&& column == hoverSwatch.width) {
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawRect(x, y, swatchSize.width,
									swatchSize.height);
						}
					}

				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Insets insets = getInsets();
			int x = numSwatches.width * (swatchSize.width + gap.width)
					+ insets.left + insets.right;
			int y = numSwatches.height * (swatchSize.height + gap.height)
					+ insets.top + insets.bottom;
			return new Dimension(x, y);
		}

		@Override
		public String getToolTipText(MouseEvent e) {

			if (!isSwatchLocation(e.getX(), e.getY()))
				return "";

			Color color = getColorForLocation(e.getX(), e.getY());
			String name = GeoGebraColorConstants.getGeogebraColorName(app,
					new geogebra.awt.GColorD(color));
			String rgbStr = color.getRed() + ", " + color.getGreen() + ", "
					+ color.getBlue();
			if (name != null) {
				return name + "  " + rgbStr;
			}
			return rgbStr;
		}

		public Color getColorForLocation(int x, int y) {
			int column;
			if ((!this.getComponentOrientation().isLeftToRight())
					&& (this instanceof RecentSwatchPanel)) {
				column = numSwatches.width - x / (swatchSize.width + gap.width)
						- 1;
			} else {
				column = x / (swatchSize.width + gap.width);
			}
			int row = y / (swatchSize.height + gap.height);

			return getColorForCell(column, row);
		}

		public void setCellFromLocation(int x, int y, Dimension p) {
			if (x == -1 || y == -1) {
				p.width = -1;
				p.height = -1;
				return;
			}

			int column;
			if ((!this.getComponentOrientation().isLeftToRight())
					&& (this instanceof RecentSwatchPanel)) {
				column = numSwatches.width - x / (swatchSize.width + gap.width)
						- 1;
			} else {
				column = x / (swatchSize.width + gap.width);
			}
			int row = y / (swatchSize.height + gap.height);
			p.width = column;
			p.height = row;
		}

		private Color getColorForCell(int column, int row) {
			if ((row * numSwatches.width) + column < colors.length) {
				return geogebra.awt.GColorD
						.getAwtColor(colors[(row * numSwatches.width) + column]);
			}
			return Color.WHITE;
		}

		private boolean getCellForColor(Color color, Dimension cell) {

			for (int i = 0; i < colors.length; i++) {
				if (color.getRed() == colors[i].getRed()
						&& color.getGreen() == colors[i].getGreen()
						&& color.getBlue() == colors[i].getBlue()) {
					cell.height = i / numSwatches.width;
					cell.width = i % numSwatches.width;
					return true;
				}

			}
			cell.height = -1;
			cell.width = -1;
			return false;
		}

		protected boolean setSelectionFromColor(Color color) {
			if (selectedSwatch == null)
				selectedSwatch = new Dimension();

			boolean success = false;

			if (color == null) {
				selectedSwatch.width = -1;
				selectedSwatch.height = -1;
				success = true;
			} else {
				success = getCellForColor(color, selectedSwatch);
			}

			repaint();
			return success;
		}

		protected void setSelectionFromLocation(int xLoc, int yLoc) {
			if (xLoc < 0 || yLoc < 0) {
				selectedSwatch.width = -1;
				selectedSwatch.height = -1;
			} else {
				setCellFromLocation(xLoc, yLoc, selectedSwatch);
			}
			this.repaint();
		}

		Dimension cell = new Dimension();

		protected boolean isSwatchLocation(int xLoc, int yLoc) {
			setCellFromLocation(xLoc, yLoc, cell);
			return isSwatchCell(cell.width, cell.height);
		}

		protected boolean isSwatchCell(int column, int row) {
			int count = row * numSwatches.width + column + 1;
			return count <= swatchCount;
		}

	}

	/*******************************************************
	 * Recent swatch panel. This holds recently selected colors.
	 * 
	 */
	class RecentSwatchPanel extends SwatchPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void initValues() {
			numSwatches = new Dimension(6, 4);
			swatchSize = new Dimension(smallSwatchSize, smallSwatchSize);
		}

		@Override
		protected void initColors() {
			Color defaultRecentColor = UIManager
					.getColor("ColorChooser.swatchesDefaultRecentColor");
			int numColors = numSwatches.width * numSwatches.height;

			colors = new geogebra.common.awt.GColor[numColors];
			for (int i = 0; i < numColors; i++) {
				colors[i] = new geogebra.awt.GColorD(defaultRecentColor);
			}
		}

		@Override
		protected void initSwatchCount() {
			swatchCount = 0;
		}

		public void setMostRecentColor(Color c) {

			System.arraycopy(colors, 0, colors, 1, colors.length - 1);
			colors[0] = new geogebra.awt.GColorD(c);
			if (swatchCount < swatchSize.width * swatchSize.height)
				swatchCount++;

			repaint();
		}

	}

	/*******************************************************
	 * Custom swatch panel. This holds user defined RGB colors.
	 * 
	 */
	class CustomSwatchPanel extends SwatchPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void initValues() {
			numSwatches = new Dimension(5, 2);
			// swatchSize = new Dimension(smallSwatchSize,smallSwatchSize);
		}

		@Override
		protected void initSwatchCount() {
			swatchCount = 0;
		}

		@Override
		protected void initColors() {
			Color defaultRecentColor = UIManager
					.getColor("ColorChooser.swatchesDefaultRecentColor");
			int numColors = numSwatches.width * numSwatches.height;

			colors = new geogebra.common.awt.GColor[numColors];
			for (int i = 0; i < numColors; i++) {
				colors[i] = new geogebra.awt.GColorD(defaultRecentColor);
			}
		}

		public void addCustomColor(Color color) {

			if (color == null) {
				selectedSwatch.width = -1;
				selectedSwatch.height = -1;
				repaint();
			} else {
				System.arraycopy(colors, 0, colors, 1, colors.length - 1);
				colors[0] = new geogebra.awt.GColorD(color);
				myChooser.setSwatchPanelSelection(color);
				repaint();

				if (swatchCount < swatchSize.width * swatchSize.height)
					swatchCount++;

				getColorSelectionModel().setSelectedColor(color);
				primarySwatchPanel.setSelectionFromLocation(-1, -1);
				mainSwatchPanel.setSelectionFromLocation(-1, -1);
				recentSwatchPanel.setMostRecentColor(color);

			}

		}

		@Override
		protected boolean setSelectionFromColor(Color color) {
			if (!super.setSelectionFromColor(color))
				addCustomColor(color);
			return true;
		}

	}

	/*******************************************************
	 * Primary swatch panel. This holds primary (and near primary) colors and
	 * grays.
	 * 
	 */
	class PrimarySwatchPanel extends SwatchPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void initValues() {
			numSwatches = new Dimension(2, 9);
		}

		@Override
		protected void initColors() {
			colors = GeoGebraColorConstants.getPrimarySwatchColors();
		}

	}

	/********************************************************
	 * Main swatch panel. This panel sits in the middle and allows a set of
	 * colors to be picked which will move to the recent swatch panel.
	 */
	class MainSwatchPanel extends SwatchPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void initValues() {
			numSwatches = new Dimension(8, 9);
		}

		@Override
		protected void initColors() {
			colors = GeoGebraColorConstants.mainColorSwatchColors;
		}

	}

}
