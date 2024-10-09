package org.geogebra.web.geogebra3D.web.gui.dialog.options;

import java.util.Arrays;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.gui.dialog.options.BasicTab;
import org.geogebra.web.full.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HasText;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Properties for 3D view (web)
 */
@SuppressWarnings({ "synthetic-access" })
public class OptionsEuclidian3DW extends OptionsEuclidianW {

	private AxisTab zAxisTab;
	private ProjectionTab projectionTab;

	/**
	 * basic tab for 3D
	 * 
	 * @author mathieu
	 *
	 */
	protected class BasicTab3D extends BasicTab {

		private ComponentCheckbox cbYAxisVertical;
		private ComponentCheckbox cbAxesColored;
		private ComponentCheckbox cbUseClipping;
		private ComponentCheckbox cbShowClipping;
		private Label clippingOptionsTitle;
		private Label boxSizeTitle;
		private RadioButtonPanel<Integer> clippingRadioBtnPanel;
		private ComponentCheckbox cbUseLight;

		/**
		 * constructor
		 * 
		 * @param o - euclidian options
		 */
		public BasicTab3D(OptionsEuclidianW o) {
			super(o);
			addClippingOptionsPanel();
		}

		@Override
		protected void fillMiscPanel() {
			miscPanel.add(LayoutUtilW.panelRow(backgroundColorLabel,
					btBackgroundColor));
			miscPanel.add(LayoutUtilW.panelRow(cbUseLight));
		}

		@Override
		protected void addMiscPanel() {
			cbUseLight = new ComponentCheckbox(loc, true, "UseLighting",
					selected -> {
						get3dview().getSettings().setUseLight(selected);
						repaintView();
					});

			super.addMiscPanel();
		}

		@Override
		protected void applyBackgroundColor(GColor color) {
			model.applyBackgroundColor(3, color);
		}

		@Override
		protected void addAxesOptionsPanel() {
			cbYAxisVertical = new ComponentCheckbox(loc, false, "YAxisVertical",
					selected -> {
				get3dview().setYAxisVertical(selected);
				repaintView();
			});

			cbAxesColored = new ComponentCheckbox(loc, true, "AxesColored",
				selected -> {
				get3dview().getSettings().setHasColoredAxes(selected);
				repaintView();
			});

			super.addAxesOptionsPanel();
		}

		@Override
		protected void fillAxesOptionsPanel() {
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbShowAxes));
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbYAxisVertical));
			super.addFontStyleRow();
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbAxesColored));
		}

		private void addClippingOptionsPanel() {
			// clipping options panel
			clippingOptionsTitle = new Label();
			clippingOptionsTitle.setStyleName("panelTitle");
			FlowPanel clippingOptionsPanel = new FlowPanel();
			cbUseClipping = new ComponentCheckbox(loc, false, "UseClipping",
					selected -> {
						get3dview().setUseClippingCube(selected);
						repaintView();
					});
			clippingOptionsPanel.add(cbUseClipping);
			cbShowClipping = new ComponentCheckbox(loc, false, "ShowClipping",
					selected -> {
						get3dview().setShowClippingCube(selected);
						repaintView();
					});
			clippingOptionsPanel.add(cbShowClipping);

			add(clippingOptionsTitle);
			indent(clippingOptionsPanel);

			boxSizeTitle = new Label();
			boxSizeTitle.setStyleName("panelTitle");
			FlowPanel boxSizePanel = new FlowPanel();

			clippingRadioBtnPanel = new RadioButtonPanel<>(loc,
					Arrays.asList(
							newClippingButtonData("BoxSize.small",
									GeoClippingCube3D.REDUCTION_SMALL),
							newClippingButtonData("BoxSize.medium",
									GeoClippingCube3D.REDUCTION_MEDIUM),
							newClippingButtonData("BoxSize.large",
									GeoClippingCube3D.REDUCTION_LARGE)),
					get3dview().getSettings().getClippingReduction(),
					this::setClippingAndRepaint);

			boxSizePanel.add(clippingRadioBtnPanel);

			add(boxSizeTitle);
			indent(boxSizePanel);
		}

		private RadioButtonData<Integer> newClippingButtonData(String label,
				int value) {
			return new RadioButtonData<>(label, value);
		}

		private void setClippingAndRepaint(int clippingType) {
			get3dview().getSettings().setClippingReduction(clippingType);
			repaintView();
		}

		/**
		 * update clipping properties (use and size)
		 */
		public void update3DProperties() {
			cbYAxisVertical.setSelected(get3dview().getYAxisVertical());
			cbAxesColored
					.setSelected(get3dview().getSettings().getHasColoredAxes());

			cbUseLight.setSelected(get3dview().getUseLight());

			cbUseClipping.setSelected(get3dview().useClippingCube());
			cbShowClipping.setSelected(get3dview().showClippingCube());

			int flag = get3dview().getClippingReduction();
			clippingRadioBtnPanel.setValue(flag);
		}

		@Override
		public void setLabels() {
			super.setLabels();

			cbYAxisVertical.setLabels();
			cbAxesColored.setLabels();
			cbUseLight.setLabels();
			setText(clippingOptionsTitle, "Clipping");
			cbUseClipping.setLabels();
			cbShowClipping.setLabels();
			setText(boxSizeTitle, "BoxSize");
			clippingRadioBtnPanel.setLabels();
			getDimLabel()[4].setText(getOptionsEuclidianW().loc.getMenu("zmin") + ":");
			getDimLabel()[5].setText(getOptionsEuclidianW().loc.getMenu("zmax") + ":");
		}

		@Override
		protected void updateMinMax() {
			EuclidianView3D view = (EuclidianView3D) getOptionsEuclidianW().getView();
			view.updateBoundObjects();
			setMinMaxText(
					view.getXminObject().getLabel(StringTemplate.editTemplate),
					view.getXmaxObject().getLabel(StringTemplate.editTemplate),
					view.getYminObject().getLabel(StringTemplate.editTemplate),
					view.getYmaxObject().getLabel(StringTemplate.editTemplate),
					view.getZminObject().getLabel(StringTemplate.editTemplate),
					view.getZmaxObject().getLabel(StringTemplate.editTemplate));
		}
	}

	@Override
	protected GridTab newGridTab() {
		return new GridTab3D();
	}

	/**
	 * @param cb
	 *            input
	 * @param string
	 *            text
	 */
	public void setText(HasText cb, String string) {
		cb.setText(loc.getMenu(string));
	}

	/**
	 * Grid settings for 3D
	 *
	 */
	class GridTab3D extends GridTab {

		@Override
		protected void addGridType(FlowPanel gridTickAnglePanel) {
			// TODO remove this when implemented
		}

		@Override
		protected void addOnlyFor2D(Widget w) {
			// TODO remove this when implemented
		}

		@Override
		protected void setGridTypeLabel() {
			lblGridType.setText(
					loc.getMenu("GridType") + " : " + loc.getMenu("Cartesian"));
		}
	}

	@Override
	protected void updateView() {
		super.updateView();
		get3dview().getPlaneDrawable().setWaitForUpdate();
	}

	private class ProjectionTab extends EuclidianTab {

		private final Label orthoTitle;
		private final Label perspTitle;
		private final Label obliqueTitle;
		private final Label glassesTitle;

		private final AutoCompleteTextFieldW tfPersp;
		private final AutoCompleteTextFieldW tfGlassesEyeSep;
		private final AutoCompleteTextFieldW tfObliqueAngle;
		private final AutoCompleteTextFieldW tfObliqueFactor;
		private final FormLabel tfPerspLabel;
		private final FormLabel tfGlassesLabel;
		private final FormLabel tfObliqueAngleLabel;
		private final FormLabel tfObliqueFactorLabel;
		private final ComponentCheckbox cbGlassesGray;
		private final ComponentCheckbox cbGlassesShutDownGreen;

		private class ProjectionButtons implements FastClickHandler {

			private final ToggleButton[] buttons;

			ProjectionButtons() {
				buttons = new ToggleButton[4];

				buttons[EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC] = new ToggleButton(
						MaterialDesignResources.INSTANCE.projection_orthographic());
				buttons[EuclidianView3DInterface.PROJECTION_PERSPECTIVE] = new ToggleButton(
						MaterialDesignResources.INSTANCE.projection_perspective());
				buttons[EuclidianView3DInterface.PROJECTION_GLASSES] = new ToggleButton(
						MaterialDesignResources.INSTANCE.projection_glasses());
				buttons[EuclidianView3DInterface.PROJECTION_OBLIQUE] = new ToggleButton(
						MaterialDesignResources.INSTANCE.projection_oblique());

				for (int i = 0; i < 4; i++) {
					buttons[i].addFastClickHandler(this);
				}

				int buttonSelected = get3dview().getProjection();
				buttons[buttonSelected].setSelected(true);
			}

			public ToggleButton getButton(int i) {
				return buttons[i];
			}

			@Override
			public void onClick(Widget target) {
				if (!(target instanceof ToggleButton)) {
					return;
				}
				ToggleButton source = (ToggleButton) target;

				if (source == buttons[get3dview().getProjection()]) {
					source.setSelected(true);
					return;
				}

				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].equals(source)) {
						get3dview().getSettings().setProjection(i);
						repaintView();
						buttons[i].setSelected(true);
					} else {
						buttons[i].setSelected(false);
					}
				}
			}
		}

		public ProjectionTab() {
			super();

			ProjectionButtons projectionButtons = new ProjectionButtons();

			// orthographic projection
			orthoTitle = new Label("");
			orthoTitle.setStyleName("panelTitle");
			FlowPanel orthoPanel = new FlowPanel();
			orthoPanel.add(projectionButtons
					.getButton(
							EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC));
			add(orthoTitle);
			indent(orthoPanel);

			// perspective projection
			perspTitle = new Label("");
			perspTitle.setStyleName("panelTitle");
			tfPersp = getTextField();
			tfPerspLabel = new FormLabel().setFor(tfPersp);
			tfPersp.addKeyHandler(e -> {
				if (e.isEnterKey()) {
					processPerspText();
				}
			});

			tfPersp.addBlurHandler(event -> processPerspText());
			FlowPanel tfPerspPanel = new FlowPanel();
			tfPerspPanel.setStyleName("panelRowCell");
			tfPerspPanel.add(tfPerspLabel);
			tfPerspPanel.add(tfPersp);
			FlowPanel perspPanel = new FlowPanel();
			perspPanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(
									EuclidianView3DInterface.PROJECTION_PERSPECTIVE),
					tfPerspPanel));
			add(perspTitle);
			indent(perspPanel);

			// glasses projection (two images)
			glassesTitle = new Label("");
			glassesTitle.setStyleName("panelTitle");

			tfGlassesEyeSep = getTextField();
			tfGlassesLabel = new FormLabel().setFor(tfGlassesEyeSep);
			tfGlassesEyeSep.addKeyHandler(e -> {
				if (e.isEnterKey()) {
					processGlassesEyeSepText();
				}
			});
			tfGlassesEyeSep.addBlurHandler(event -> processGlassesEyeSepText());
			cbGlassesGray = new ComponentCheckbox(loc, false, "GrayScale",
					selected -> {
						get3dview().setGlassesGrayScaled(selected);
						repaintView();
					});
			cbGlassesShutDownGreen = new ComponentCheckbox(loc, false, "ShutDownGreen",
					selected -> {
						get3dview().setGlassesShutDownGreen(selected);
						repaintView();
					});
			FlowPanel tfGlassesPanel = new FlowPanel();
			tfGlassesPanel.setStyleName("panelRowCell");
			tfGlassesPanel.add(tfGlassesLabel);
			tfGlassesPanel.add(tfGlassesEyeSep);
			tfGlassesPanel.add(cbGlassesGray);
			tfGlassesPanel.add(cbGlassesShutDownGreen);
			FlowPanel glassesPanel = new FlowPanel();
			glassesPanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(
									EuclidianView3DInterface.PROJECTION_GLASSES),
					tfGlassesPanel));
			add(glassesTitle);
			indent(glassesPanel);

			// oblique projection
			obliqueTitle = new Label("");
			obliqueTitle.setStyleName("panelTitle");

			tfObliqueAngle = getTextField();
			tfObliqueAngleLabel = new FormLabel().setFor(tfObliqueAngle);
			tfObliqueAngle.addKeyHandler(e -> {
				if (e.isEnterKey()) {
					processObliqueAngleText();
				}
			});

			tfObliqueAngle.addBlurHandler(event -> processObliqueAngleText());

			tfObliqueFactor = getTextField();
			tfObliqueFactorLabel = new FormLabel().setFor(tfObliqueFactor);
			tfObliqueFactor.addKeyHandler(e -> {
				if (e.isEnterKey()) {
					processObliqueFactorText();
				}
			});

			tfObliqueFactor.addBlurHandler(event -> processObliqueFactorText());
			FlowPanel tfObliquePanel = new FlowPanel();
			tfObliquePanel.setStyleName("panelRowCell");
			tfObliquePanel.add(tfObliqueAngleLabel);
			tfObliquePanel.add(tfObliqueAngle);
			tfObliquePanel.add(tfObliqueFactorLabel);
			tfObliquePanel.add(tfObliqueFactor);
			FlowPanel obliquePanel = new FlowPanel();
			obliquePanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(
									EuclidianView3DInterface.PROJECTION_OBLIQUE),
					tfObliquePanel));
			add(obliqueTitle);
			indent(obliquePanel);

		}

		protected void processPerspText() {
			try {
				int val = Integer.parseInt(tfPersp.getText());
				int min = 1;
				if (val < min) {
					val = min;
					tfPersp.setText("" + val);
				}
				get3dview().getSettings()
						.setProjectionPerspectiveEyeDistance(val);
				repaintView();
			} catch (NumberFormatException e) {
				tfPersp.setText("" + (int) get3dview()
						.getProjectionPerspectiveEyeDistance());
			}
		}

		protected void processGlassesEyeSepText() {
			try {
				int val = Integer.parseInt(tfGlassesEyeSep.getText());
				if (val < 0) {
					val = 0;
					tfGlassesEyeSep.setText("" + val);
				}
				get3dview().getSettings().setEyeSep(val);
				repaintView();
			} catch (NumberFormatException e) {
				tfGlassesEyeSep.setText("" + (int) get3dview().getEyeSep());
			}
		}

		protected void processObliqueAngleText() {
			try {
				double val = Double.parseDouble(tfObliqueAngle.getText());
				if (!Double.isNaN(val)) {

					get3dview().getSettings().setProjectionObliqueAngle(val);
					repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueAngle
						.setText("" + get3dview().getProjectionObliqueAngle());
			}
		}

		protected void processObliqueFactorText() {
			try {
				double val = Double.parseDouble(tfObliqueFactor.getText());
				if (!Double.isNaN(val)) {
					if (val < 0) {
						val = 0;
						tfObliqueFactor.setText("" + val);
					}
					get3dview().setProjectionObliqueFactor(val);
					repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueFactor
						.setText("" + get3dview().getProjectionObliqueFactor());
			}
		}

		protected void indent(FlowPanel panel) {
			FlowPanel indent = new FlowPanel();
			indent.setStyleName("panelIndent");
			indent.add(panel);
			add(indent);

		}

		@Override
		public void setLabels() {
			setText(orthoTitle, "Orthographic");
			setText(perspTitle, "Perspective");
			setTextColon(tfPerspLabel, "EyeDistance");
			setText(glassesTitle, "Glasses");
			setTextColon(tfGlassesLabel, "EyesSeparation");
			cbGlassesGray.setLabels();
			cbGlassesShutDownGreen.setLabels();
			setText(obliqueTitle, "Oblique");
			setTextColon(tfObliqueAngleLabel, "Angle");
			setTextColon(tfObliqueFactorLabel, "Dilate.Factor");
		}

		/**
		 * update text values
		 */
		public void updateGUI() {
			tfPersp.setText(""
					+ (int) get3dview().getProjectionPerspectiveEyeDistance());
			tfGlassesEyeSep.setText("" + (int) get3dview().getEyeSep());
			cbGlassesGray.setSelected(get3dview().isGlassesGrayScaled());
			cbGlassesShutDownGreen.setSelected(get3dview().isGlassesShutDownGreen());
			tfObliqueAngle
					.setText("" + get3dview().getProjectionObliqueAngle());
			tfObliqueFactor
					.setText("" + get3dview().getProjectionObliqueFactor());

		}
	}

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            3D view
	 */
	public OptionsEuclidian3DW(AppW app, EuclidianViewInterfaceCommon view) {
		super(app, view);

	}

	/**
	 * @return 3D view
	 */
	public EuclidianView3D get3dview() {
		return (EuclidianView3D) view;
	}

	@Override
	protected BasicTab newBasicTab() {
		return new BasicTab3D(this);
	}

	@Override
	public void updateGUI() {
		((BasicTab3D) basicTab).update3DProperties();
		projectionTab.updateGUI();
		super.updateGUI();
		zAxisTab.updateGUI();
	}

	@Override
	public void setLabels() {
		MultiRowsTabBar tabBar = tabPanel.getTabBar();
		super.setLabels(tabBar, 4);
		tabBar.setTabText(3, loc.getMenu("zAxis"));
		zAxisTab.setLabels();
		tabBar.setTabText(5, loc.getMenu("Projection"));
		projectionTab.setLabels();
	}

	@Override
	protected void addAxesTabs() {
		super.addAxesTabs();
		addZAxisTab();
	}

	@Override
	protected AxisTab newAxisTab(int axis) {
		return new AxisTab(axis, true);
	}

	private void addZAxisTab() {
		zAxisTab = newAxisTab(EuclidianOptionsModel.Z_AXIS);
		tabPanel.add(zAxisTab, "z");
	}

	@Override
	protected void addTabs() {
		super.addTabs();
		addProjectionTab();
	}

	private void addProjectionTab() {
		projectionTab = new ProjectionTab();
		tabPanel.add(projectionTab, "projection");
	}

	/**
	 * Repaint 3D (scheduled)
	 */
	protected void repaintView() {
		get3dview().repaintView();
	}

}
