package org.geogebra.desktop.geogebra3D.gui.dialogs.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.geogebra3D.gui.GuiResources3D;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.dialog.options.AxisPanel;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;

/**
 * options for 3D view
 * 
 * @author mathieu
 * 
 */
public class OptionsEuclidian3DD extends OptionsEuclidianD<EuclidianView3D> {

	private AxisPanel3D zAxisPanel;

	private JCheckBox cbUseClipping, cbShowClipping;

	private JCheckBox cbYAxisVertical;
	private JCheckBox cbAxesColored;

	private JRadioButton radioClippingSmall, radioClippingMedium,
			radioClippingLarge;

	private JPanel clippingOptionsPanel, boxSizePanel;

	private JCheckBox cbUseLight;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            3D view
	 */
	public OptionsEuclidian3DD(AppD app, EuclidianView3D view) {
		super(app, view);

		enableStuff(false);

		updateGUI();
	}

	@Override
	protected void initAxesOptionsPanel() {
		// y axis is vertical
		cbYAxisVertical = new JCheckBox(loc.getMenu("YAxisVertical"));
		cbAxesColored = new JCheckBox(loc.getMenu("AxesColored"));

		super.initAxesOptionsPanel();

	}

	@Override
	protected void fillAxesOptionsPanel() {
		axesOptionsPanel.add(LayoutUtil.flowPanel(cbShowAxes));
		axesOptionsPanel.add(LayoutUtil.flowPanel(cbYAxisVertical));
		axesOptionsPanel.add(LayoutUtil.flowPanel(lblAxisLabelStyle,
				cbAxisLabelSerif, cbAxisLabelBold, cbAxisLabelItalic));
		axesOptionsPanel.add(LayoutUtil.flowPanel(cbAxesColored));
	}

	@Override
	protected void fillGridPanel(JPanel showGridPanel, JPanel gridPanel) {
		gridPanel.add(showGridPanel);
		gridPanel.add(typePanel);
	}

	@Override
	protected void setTypePanelLabel() {
		typePanel.setBorder(LayoutUtil.titleBorder(
				loc.getMenu("GridType") + " : " + loc.getMenu("Cartesian")));
	}

	@Override
	protected void addComboGridType() {
		// TODO remove this when implemented
	}

	@Override
	protected JPanel buildBasicPanel() {

		// -------------------------------------
		// clipping options panel
		clippingOptionsPanel = new JPanel();
		clippingOptionsPanel.setLayout(
				new BoxLayout(clippingOptionsPanel, BoxLayout.Y_AXIS));

		// clipping
		cbUseClipping = new JCheckBox(loc.getMenu("UseClipping"));
		clippingOptionsPanel.add(cbUseClipping);
		clippingOptionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		cbShowClipping = new JCheckBox(loc.getMenu("ShowClipping"));
		clippingOptionsPanel.add(cbShowClipping);

		boxSizePanel = new JPanel();
		boxSizePanel.setLayout(new BoxLayout(boxSizePanel, BoxLayout.Y_AXIS));
		radioClippingSmall = new JRadioButton(loc.getMenu("small"));
		radioClippingMedium = new JRadioButton(loc.getMenu("medium"));
		radioClippingLarge = new JRadioButton(loc.getMenu("large"));
		boxSizePanel.add(radioClippingSmall);
		boxSizePanel.add(radioClippingMedium);
		boxSizePanel.add(radioClippingLarge);
		radioClippingSmall.addActionListener(this);
		radioClippingMedium.addActionListener(this);
		radioClippingLarge.addActionListener(this);
		ButtonGroup boxSizeGroup = new ButtonGroup();
		boxSizeGroup.add(radioClippingSmall);
		boxSizeGroup.add(radioClippingMedium);
		boxSizeGroup.add(radioClippingLarge);

		// -------------------------------------

		JPanel basicPanel = super.buildBasicPanel();
		basicPanel.add(clippingOptionsPanel);
		basicPanel.add(boxSizePanel);

		return basicPanel;
	}

	@Override
	protected void fillMiscPanel() {
		// TODO remove this override
		miscPanel.add(LayoutUtil.flowPanel(backgroundColor, btBackgroundColor));
		miscPanel.add(LayoutUtil.flowPanel(cbUseLight));
	}

	@Override
	protected void initMiscPanel() {

		// use light
		cbUseLight = new JCheckBox();
		cbUseLight.addActionListener(this);

		super.initMiscPanel();
	}

	private void enableStuff(boolean flag) {
		// TODO remove when implemented

		// tfMinX.setEnabled(flag);
		// tfMaxX.setEnabled(flag);
		// tfMinY.setEnabled(flag);
		// tfMaxY.setEnabled(flag);
		//
		// btAxesColor.setEnabled(flag);
		// cbAxesStyle.setEnabled(flag);
		// cbShowMouseCoords.setEnabled(flag);
		// cbTooltips.setEnabled(flag);
		//
		// // ((AxisPanel3D) xAxisPanel).enableStuff(flag);
		// // ((AxisPanel3D) yAxisPanel).enableStuff(flag);
		// // zAxisPanel.enableStuff(flag);
		//
		// cbGridManualTick.setEnabled(flag);
		// ncbGridTickX.setEnabled(flag);
		// ncbGridTickY.setEnabled(flag);
		// cbGridTickAngle.setEnabled(flag);
		// cbGridStyle.setEnabled(flag);
		// cbGridType.setEnabled(flag);
		// cbBoldGrid.setEnabled(flag);
		// btGridColor.setEnabled(flag);

	}

	@Override
	protected void addDimPanel(JPanel basicPanel) {
		// TODO remove this and implement stuff for 3D
	}

	@Override
	public void updateGUI() {
		super.updateGUI();

		// y axis is vertical
		cbYAxisVertical.removeActionListener(this);
		cbYAxisVertical
				.setSelected(view.getYAxisVertical());
		cbYAxisVertical.addActionListener(this);

		// misc
		cbAxesColored.removeActionListener(this);
		cbAxesColored.setSelected(
				view.getSettings().getHasColoredAxes());
		cbAxesColored.addActionListener(this);
		cbUseLight.removeActionListener(this);
		cbUseLight.setSelected(view.getUseLight());
		cbUseLight.addActionListener(this);

		// clipping panel
		cbUseClipping.removeActionListener(this);
		cbUseClipping.setSelected(view.useClippingCube());
		cbUseClipping.addActionListener(this);

		cbShowClipping.removeActionListener(this);
		cbShowClipping.setSelected(view.showClippingCube());
		cbShowClipping.addActionListener(this);

		/*
		 * radioClippingSmall.removeActionListener(this);
		 * radioClippingMedium.removeActionListener(this);
		 * radioClippingLarge.removeActionListener(this);
		 */
		int flag = view.getClippingReduction();
		radioClippingSmall
				.setSelected(flag == GeoClippingCube3D.REDUCTION_SMALL);
		radioClippingMedium
				.setSelected(flag == GeoClippingCube3D.REDUCTION_MEDIUM);
		radioClippingLarge
				.setSelected(flag == GeoClippingCube3D.REDUCTION_LARGE);
		/*
		 * radioClippingSmall.addActionListener(this);
		 * radioClippingMedium.addActionListener(this);
		 * radioClippingLarge.addActionListener(this);
		 */

		// z axis panel
		zAxisPanel.updatePanel();

		// projection
		// tfPersp.removeActionListener(this);
		tfPersp.setText("" + (int) view
				.getProjectionPerspectiveEyeDistance());
		// tfPersp.addActionListener(this);

		tfGlassesEyeSep
				.setText("" + (int) view.getEyeSep());
		cbGlassesGray
				.setSelected(view.isGlassesGrayScaled());
		cbGlassesShutDownGreen
				.setSelected(view.isGlassesShutDownGreen());

		tfObliqueAngle.setText(
				"" + view.getProjectionObliqueAngle());
		tfObliqueFactor.setText(
				"" + view.getProjectionObliqueFactor());
	}

	@Override
	protected void initAxisPanels() {

		xAxisPanel = new AxisPanel3D(app, view, 0);
		yAxisPanel = new AxisPanel3D(app, view, 1);
		zAxisPanel = new AxisPanel3D(app, view, 2);
	}

	@Override
	protected void addTabs() {
		super.addTabs();
		tabbedPane.addTab("", buildProjectionPanel());
	}

	@Override
	protected void addAxisTabs() {
		super.addAxisTabs();
		tabbedPane.addTab("", new JScrollPane(zAxisPanel));

	}

	private JLabel[] projectionLabel;
	private JTextField tfPersp, tfGlassesEyeSep, tfObliqueAngle,
			tfObliqueFactor;
	private JLabel tfPerspLabel, tfGlassesLabel, tfObliqueAngleLabel,
			tfObliqueFactorLabel;
	private ProjectionButtons projectionButtons;
	private JCheckBox cbGlassesGray;
	private JLabel cbGlassesGrayLabel;
	private JCheckBox cbGlassesShutDownGreen;
	private JLabel cbGlassesShutDownGreenLabel;

	private class ProjectionButtons {

		private JButton[] buttons;

		private int buttonSelected;

		private EuclidianView3D view;

		private ProjectionButtons(OptionsEuclidian3DD options) {

			view = options.view;

			buttons = new JButton[4];

			buttons[EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC] = new JButton(
					app.getScaledIcon(GuiResources3D.PROJECTION_ORTOGRAPHIC));
			buttons[EuclidianView3DInterface.PROJECTION_PERSPECTIVE] = new JButton(
					app.getScaledIcon(GuiResources3D.PROJECTION_PERSPECTIVE));
			buttons[EuclidianView3DInterface.PROJECTION_GLASSES] = new JButton(
					app.getScaledIcon(GuiResources3D.PROJECTION_GLASSES));
			buttons[EuclidianView3DInterface.PROJECTION_OBLIQUE] = new JButton(
					app.getScaledIcon(GuiResources3D.PROJECTION_OBLIQUE));

			for (int i = 0; i < 4; i++) {
				buttons[i].addActionListener(options);
			}

			buttonSelected = view.getProjection();
			buttons[buttonSelected].setSelected(true);
		}

		JButton getButton(int i) {
			return buttons[i];
		}

		void setSelected(int i) {
			buttons[buttonSelected].setSelected(false);
			buttonSelected = i;
			buttons[buttonSelected].setSelected(true);

		}

		public void updateIcons() {
			if (buttons == null) {
				return;
			}
			buttons[EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC].setIcon(
					app.getScaledIcon(GuiResources3D.PROJECTION_ORTOGRAPHIC));
			buttons[EuclidianView3DInterface.PROJECTION_PERSPECTIVE].setIcon(
					app.getScaledIcon(GuiResources3D.PROJECTION_PERSPECTIVE));
			buttons[EuclidianView3DInterface.PROJECTION_GLASSES].setIcon(
					app.getScaledIcon(GuiResources3D.PROJECTION_GLASSES));
			buttons[EuclidianView3DInterface.PROJECTION_OBLIQUE].setIcon(
					app.getScaledIcon(GuiResources3D.PROJECTION_OBLIQUE));

		}
	}

	private JPanel buildProjectionPanel() {

		// JLabel label;

		projectionLabel = new JLabel[4]; // "orthographic", "perspective",
											// "glasses" etc.
		for (int i = 0; i < 4; i++) {
			projectionLabel[i] = new JLabel("");
		}

		projectionButtons = new ProjectionButtons(this);

		JPanel orthoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		orthoPanel.add(projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC));
		orthoPanel.add(projectionLabel[0]);

		JPanel perspPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		perspPanel.add(projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_PERSPECTIVE));
		perspPanel.add(projectionLabel[1]);
		tfPerspLabel = new JLabel("");
		perspPanel.add(tfPerspLabel);
		tfPersp = new MyTextFieldD(app, 5);
		tfPersp.addActionListener(this);
		tfPersp.addFocusListener(this);
		perspPanel.add(tfPersp);

		JPanel glassesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		glassesPanel.add(projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_GLASSES));
		glassesPanel.add(projectionLabel[2]);
		tfGlassesLabel = new JLabel("");
		glassesPanel.add(tfGlassesLabel);
		tfGlassesEyeSep = new MyTextFieldD(app, 3);
		tfGlassesEyeSep.addActionListener(this);
		tfGlassesEyeSep.addFocusListener(this);
		glassesPanel.add(tfGlassesEyeSep);
		cbGlassesGray = new JCheckBox();
		cbGlassesGray.addActionListener(this);
		cbGlassesGrayLabel = new JLabel("");
		glassesPanel.add(cbGlassesGray);
		glassesPanel.add(cbGlassesGrayLabel);
		cbGlassesShutDownGreen = new JCheckBox();
		cbGlassesShutDownGreen.addActionListener(this);
		cbGlassesShutDownGreenLabel = new JLabel("");
		glassesPanel.add(cbGlassesShutDownGreen);
		glassesPanel.add(cbGlassesShutDownGreenLabel);

		JPanel cavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		cavPanel.add(projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_OBLIQUE));
		cavPanel.add(
				projectionLabel[EuclidianView3DInterface.PROJECTION_OBLIQUE]);
		tfObliqueAngleLabel = new JLabel("");
		cavPanel.add(tfObliqueAngleLabel);
		tfObliqueAngle = new MyTextFieldD(app, 4);
		tfObliqueAngle.addActionListener(this);
		tfObliqueAngle.addFocusListener(this);
		cavPanel.add(tfObliqueAngle);
		tfObliqueFactorLabel = new JLabel("");
		cavPanel.add(tfObliqueFactorLabel);
		tfObliqueFactor = new MyTextFieldD(app, 4);
		tfObliqueFactor.addActionListener(this);
		tfObliqueFactor.addFocusListener(this);
		cavPanel.add(tfObliqueFactor);

		// ==========================================
		// create basic panel and add all sub panels

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5));

		/*
		 * northPanel.add(dimPanel); northPanel.add(Box.createRigidArea(new
		 * Dimension(0,16))); northPanel.add(axesOptionsPanel);
		 * northPanel.add(Box.createRigidArea(new Dimension(0,16)));
		 * northPanel.add(bgPanel);
		 */
		northPanel.add(orthoPanel);
		northPanel.add(perspPanel);
		northPanel.add(glassesPanel);
		northPanel.add(cavPanel);

		// use a BorderLayout to keep sub panels together
		JPanel ret = new JPanel(new BorderLayout());
		ret.add(northPanel, BorderLayout.NORTH);

		return ret;

	}

	@Override
	protected void setTabLabels() {
		tabbedPane.setTitleAt(0, loc.getMenu("Properties.Basic"));
		tabbedPane.setTitleAt(1, loc.getMenu("xAxis"));
		tabbedPane.setTitleAt(2, loc.getMenu("yAxis"));
		tabbedPane.setTitleAt(3, loc.getMenu("zAxis"));
		tabbedPane.setTitleAt(4, loc.getMenu("Grid"));
		tabbedPane.setTitleAt(5, loc.getMenu("Projection"));
	}

	@Override
	public void setLabels() {
		super.setLabels();

		zAxisPanel.setLabels();

		// y axis is vertical
		cbYAxisVertical.setText(loc.getMenu("YAxisVertical"));

		// misc
		cbAxesColored.setText(loc.getMenu("AxesColored"));
		cbUseLight.setText(loc.getMenu("UseLighting"));

		// clipping tab
		clippingOptionsPanel
				.setBorder(LayoutUtil.titleBorder(loc.getMenu("Clipping")));
		cbUseClipping.setText(loc.getMenu("UseClipping"));
		cbShowClipping.setText(loc.getMenu("ShowClipping"));

		boxSizePanel.setBorder(LayoutUtil.titleBorder(loc.getMenu("BoxSize")));
		radioClippingSmall.setText(loc.getMenu("BoxSize.small"));
		radioClippingMedium.setText(loc.getMenu("BoxSize.medium"));
		radioClippingLarge.setText(loc.getMenu("BoxSize.large"));

		// perspective tab
		projectionLabel[0].setText(loc.getMenu("Orthographic"));

		projectionLabel[1].setText(loc.getMenu("Perspective") + ":");
		tfPerspLabel.setText(loc.getMenu("EyeDistance") + ":");

		projectionLabel[2].setText(loc.getMenu("Glasses") + ":");
		tfGlassesLabel.setText(loc.getMenu("EyesSeparation") + ":");
		cbGlassesGrayLabel.setText(loc.getMenu("GrayScale"));
		cbGlassesShutDownGreenLabel.setText(loc.getMenu("ShutDownGreen"));

		projectionLabel[3].setText(loc.getMenu("Oblique") + ":");
		tfObliqueAngleLabel.setText(loc.getMenu("Angle") + ":");
		tfObliqueFactorLabel.setText(loc.getMenu("Dilate.Factor") + ":");

		projectionButtons.updateIcons();
		projectionButtons.setSelected(view.getProjection());
	}

	@Override
	protected void doActionPerformed(Object source) {

		if (source == cbYAxisVertical) {
			view
					.setYAxisVertical(cbYAxisVertical.isSelected());
		} else if (source == cbAxesColored) {
			view.getSettings()
					.setHasColoredAxes(cbAxesColored.isSelected());
		} else if (source == cbUseLight) {
			view.getSettings()
					.setUseLight(cbUseLight.isSelected());
		} else if (source == cbUseClipping) {
			view
					.setUseClippingCube(cbUseClipping.isSelected());
		} else if (source == cbShowClipping) {
			view
					.setShowClippingCube(cbShowClipping.isSelected());
		} else if (source == radioClippingSmall) {
			view.getSettings()
					.setClippingReduction(GeoClippingCube3D.REDUCTION_SMALL);
		} else if (source == radioClippingMedium) {
			view.getSettings()
					.setClippingReduction(GeoClippingCube3D.REDUCTION_MEDIUM);
		} else if (source == radioClippingLarge) {
			view.getSettings()
					.setClippingReduction(GeoClippingCube3D.REDUCTION_LARGE);
		} else if (source == tfPersp) {
			try {
				int val = Integer.parseInt(tfPersp.getText());
				int min = 1;
				if (val < min) {
					val = min;
					tfPersp.setText("" + val);
				}
				view.getSettings()
						.setProjectionPerspectiveEyeDistance(val);
			} catch (NumberFormatException e) {
				tfPersp.setText("" + (int) view
						.getProjectionPerspectiveEyeDistance());
			}
		} else if (source == tfGlassesEyeSep) {
			try {
				int val = Integer.parseInt(tfGlassesEyeSep.getText());
				if (val < 0) {
					val = 0;
					tfGlassesEyeSep.setText("" + val);
				}
				view.getSettings().setEyeSep(val);
			} catch (NumberFormatException e) {
				tfGlassesEyeSep.setText(
						"" + (int) view.getEyeSep());
			}
		} else if (source == tfObliqueAngle) {
			try {
				double val = Double.parseDouble(tfObliqueAngle.getText());
				if (!Double.isNaN(val)) {

					view.getSettings()
							.setProjectionObliqueAngle(val);
				}
			} catch (NumberFormatException e) {
				tfObliqueAngle.setText(""
						+ view.getProjectionObliqueAngle());
			}
		} else if (source == tfObliqueFactor) {
			try {
				double val = Double.parseDouble(tfObliqueFactor.getText());
				if (!Double.isNaN(val)) {
					if (val < 0) {
						val = 0;
						tfObliqueFactor.setText("" + val);
					}
					view.setProjectionObliqueFactor(val);
				}
			} catch (NumberFormatException e) {
				tfObliqueFactor.setText("" + view
						.getProjectionObliqueFactor());
			}
		} else if (source == projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC)) {
			view.getSettings()
					.setProjection(
							EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC);
			projectionButtons
					.setSelected(
							EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC);
		} else if (source == projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_PERSPECTIVE)) {
			view.getSettings()
					.setProjection(
							EuclidianView3DInterface.PROJECTION_PERSPECTIVE);
			projectionButtons
					.setSelected(
							EuclidianView3DInterface.PROJECTION_PERSPECTIVE);
		} else if (source == projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_GLASSES)) {
			view.getSettings()
					.setProjection(EuclidianView3DInterface.PROJECTION_GLASSES);
			projectionButtons
					.setSelected(EuclidianView3DInterface.PROJECTION_GLASSES);
		} else if (source == projectionButtons
				.getButton(EuclidianView3DInterface.PROJECTION_OBLIQUE)) {
			view.getSettings()
					.setProjection(EuclidianView3DInterface.PROJECTION_OBLIQUE);
			projectionButtons
					.setSelected(EuclidianView3DInterface.PROJECTION_OBLIQUE);
		} else if (source == cbGlassesGray) {
			view
					.setGlassesGrayScaled(cbGlassesGray.isSelected());
		} else if (source == cbGlassesShutDownGreen) {
			view.setGlassesShutDownGreen(
					cbGlassesShutDownGreen.isSelected());

		} else {
			super.doActionPerformed(source);
		}
	}

	@Override
	protected void updateFont(Font font) {

		super.updateFont(font);

		zAxisPanel.updateFont();
	}

	private static class AxisPanel3D extends AxisPanel {
		private static final long serialVersionUID = 1L;
		final static protected int AXIS_Z = 2;

		public AxisPanel3D(AppD app, EuclidianView view, int axis) {
			super(app, view, axis);
		}

		@Override
		protected String getString() {
			if (getModel().getAxis() == AXIS_Z) {
				return "zAxis";
			}
			return super.getString();
		}

		@Override
		protected void addCrossPanel(JPanel crossPanel) {
			// TODO implement this
		}
	}

	@Override
	protected void actionBtBackgroundColor() {
		EuclidianSettings settings = view.getSettings();
		GColor old = settings == null ? view.getBackground()
				: settings.getBackground();
		GColor color = GColorD.newColor(
				((GuiManagerD) (app.getGuiManager())).showColorChooser(old));

		if (settings == null) {
			view.setBackground(color);
		} else {
			settings.setBackground(color);
		}
	}

}
