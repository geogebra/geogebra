package geogebra.geogebra3D.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import geogebra.geogebra3D.web.gui.images.StyleBar3DResources;
import geogebra.html5.event.FocusListenerW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.gui.util.MyToggleButton2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties for 3D view (web)
 * 
 * @author mathieu
 *
 */
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

		private CheckBox cbYAxisVertical;
		private CheckBox cbUseClipping, cbShowClipping;
		private FlowPanel clippingOptionsPanel, boxSizePanel;
		private Label clippingOptionsTitle, boxSizeTitle;
		private RadioButton radioClippingSmall, radioClippingMedium,
		        radioClippingLarge;
		private CheckBox cbUseLight;

		/**
		 * constructor
		 */
		public BasicTab3D() {
			super();

			addClippingOptionsPanel();

		}

		@Override
		protected void indentDimPanel() {
			// TODO remove this and implement stuff for 3D
		}

		@Override
		protected void addToDimPanel(Widget w) {
			// TODO remove this and implement stuff for 3D
		}

		@Override
		protected void fillMiscPanel() {
			miscPanel.add(LayoutUtil.panelRow(backgroundColorLabel,
			        btBackgroundColor));
			miscPanel.add(LayoutUtil.panelRow(cbUseLight));
		}
		
		
		@Override
        protected void addMiscPanel() {

			cbUseLight = new CheckBox();

			super.addMiscPanel();



			cbUseLight.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).getSettings().setUseLight(
					        cbUseLight.getValue());
					view.repaintView();
				}
			});

		}

		@Override
		protected void applyBackgroundColor(GColor color) {
			model.applyBackgroundColor(3, color);
		}

		@Override
		protected void addAxesOptionsPanel() {

			cbYAxisVertical = new CheckBox();

			cbYAxisVertical.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setYAxisVertical(cbYAxisVertical
					        .getValue());
					view.repaintView();
				}
			});

			super.addAxesOptionsPanel();
		}

		@Override
		protected void fillAxesOptionsPanel() {
			axesOptionsPanel.add(LayoutUtil.panelRow(cbShowAxes));
			axesOptionsPanel.add(LayoutUtil.panelRow(cbYAxisVertical));
		}

		private void addClippingOptionsPanel() {

			// clipping options panel
			clippingOptionsTitle = new Label();
			clippingOptionsTitle.setStyleName("panelTitle");
			clippingOptionsPanel = new FlowPanel();
			cbUseClipping = new CheckBox();
			cbUseClipping.setStyleName("checkBoxPanel");
			clippingOptionsPanel.add(cbUseClipping);
			// clippingOptionsPanel.add(Box.createRigidArea(new Dimension(10,
			// 0)));
			cbShowClipping = new CheckBox();
			cbShowClipping.setStyleName("checkBoxPanel");
			clippingOptionsPanel.add(cbShowClipping);

			add(clippingOptionsTitle);
			indent(clippingOptionsPanel);

			cbUseClipping.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setUseClippingCube(cbUseClipping
					        .getValue());
					view.repaintView();
				}
			});

			cbShowClipping.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setShowClippingCube(cbShowClipping
					        .getValue());
					view.repaintView();
				}
			});

			// clipping box size
			boxSizeTitle = new Label();
			boxSizeTitle.setStyleName("panelTitle");
			boxSizePanel = new FlowPanel();
			radioClippingSmall = new RadioButton("radioClipping");
			radioClippingMedium = new RadioButton("radioClipping");
			radioClippingLarge = new RadioButton("radioClipping");
			boxSizePanel.add(radioClippingSmall);
			boxSizePanel.add(radioClippingMedium);
			boxSizePanel.add(radioClippingLarge);

			add(boxSizeTitle);
			indent(boxSizePanel);

			radioClippingSmall.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).getSettings()
					        .setClippingReduction(
					                GeoClippingCube3D.REDUCTION_SMALL);
					view.repaintView();
				}
			});

			radioClippingMedium.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).getSettings()
					        .setClippingReduction(
					                GeoClippingCube3D.REDUCTION_MEDIUM);
					view.repaintView();
				}
			});

			radioClippingLarge.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).getSettings()
					        .setClippingReduction(
					                GeoClippingCube3D.REDUCTION_LARGE);
					view.repaintView();
				}
			});

		}

		/**
		 * update clipping properties (use and size)
		 */
		public void update3DProperties() {

			cbYAxisVertical.setValue(((EuclidianView3D) view)
			        .getYAxisVertical());

			cbUseLight.setValue(((EuclidianView3D) view).getUseLight());

			cbUseClipping.setValue(((EuclidianView3D) view).useClippingCube());
			cbShowClipping
			        .setValue(((EuclidianView3D) view).showClippingCube());

			int flag = ((EuclidianView3D) view).getClippingReduction();
			radioClippingSmall
			        .setValue(flag == GeoClippingCube3D.REDUCTION_SMALL);
			radioClippingMedium
			        .setValue(flag == GeoClippingCube3D.REDUCTION_MEDIUM);
			radioClippingLarge
			        .setValue(flag == GeoClippingCube3D.REDUCTION_LARGE);

		}

		@Override
		public void setLabels() {
			super.setLabels();

			cbYAxisVertical.setText(app.getPlain("YAxisVertical"));

			cbUseLight.setText(app.getMenu("UseLight"));

			clippingOptionsTitle.setText(app.getPlain("Clipping"));
			cbUseClipping.setText(app.getPlain("UseClipping"));
			cbShowClipping.setText(app.getPlain("ShowClipping"));

			boxSizeTitle.setText(app.getPlain("BoxSize"));
			radioClippingSmall.setText(app.getPlain("BoxSize.small"));
			radioClippingMedium.setText(app.getPlain("BoxSize.medium"));
			radioClippingLarge.setText(app.getPlain("BoxSize.large"));
		}

	}

	@Override
	protected GridTab newGridTab() {
		return new GridTab3D();
	}

	private class GridTab3D extends GridTab {

		public GridTab3D() {
			super();
		}

		@Override
		protected void addGridType(FlowPanel gridTickAnglePanel) {
			// TODO remove this when implemented
		}

		@Override
		protected void addOnlyFor2D(Widget w) {
			// TODO remove this when implemented
		}

		protected void setGridTypeLabel() {
			lblGridType.setText(app.getPlain("GridType") + " : "
			        + app.getMenu("Cartesian"));
		}
	}

	private class ProjectionTab extends EuclidianTab {

		private ProjectionButtons projectionButtons;

		private FlowPanel orthoPanel, perspPanel, obliquePanel, glassesPanel;
		private Label orthoTitle, perspTitle, obliqueTitle, glassesTitle;

		private AutoCompleteTextFieldW tfPersp, tfGlassesEyeSep,
		        tfObliqueAngle, tfObliqueFactor;
		private Label tfPerspLabel, tfGlassesLabel, tfObliqueAngleLabel,
		        tfObliqueFactorLabel;
		private CheckBox cbGlassesGray, cbGlassesShutDownGreen;

		private class ProjectionButtons implements ClickHandler {

			private MyToggleButton2[] buttons;
			private int buttonSelected;

			private ProjectionButtons() {

				buttons = new MyToggleButton2[4];

				buttons[EuclidianView3D.PROJECTION_ORTHOGRAPHIC] = new MyToggleButton2(
				        new Image(StyleBar3DResources.INSTANCE
				                .viewOrthographic()));
				buttons[EuclidianView3D.PROJECTION_PERSPECTIVE] = new MyToggleButton2(
				        new Image(
				                StyleBar3DResources.INSTANCE.viewPerspective()));
				buttons[EuclidianView3D.PROJECTION_GLASSES] = new MyToggleButton2(
				        new Image(StyleBar3DResources.INSTANCE.viewGlasses()));
				buttons[EuclidianView3D.PROJECTION_OBLIQUE] = new MyToggleButton2(
				        new Image(StyleBar3DResources.INSTANCE.viewOblique()));


				for (int i = 0; i < 4; i++) {
					buttons[i].addClickHandler(this);
				}

				buttonSelected = ((EuclidianView3D) view).getProjection();
				buttons[buttonSelected].setDown(true);
			}

			public ToggleButton getButton(int i) {
				return buttons[i];
			}

			@Override
			public void onClick(ClickEvent event) {
				MyToggleButton2 source = (MyToggleButton2) event.getSource();

				if (source == buttons[((EuclidianView3D) view).getProjection()]) {
					source.setDown(true);
					return;
				}

				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].equals(source)) {
						((EuclidianView3D) view).getSettings().setProjection(i);
						view.repaintView();
						buttons[i].setDown(true);
					} else {
						buttons[i].setDown(false);
					}
				}
			}
		}

		public ProjectionTab() {
			super();

			projectionButtons = new ProjectionButtons();

			// orthographic projection
			orthoTitle = new Label("");
			orthoTitle.setStyleName("panelTitle");
			orthoPanel = new FlowPanel();
			orthoPanel.add(projectionButtons
			        .getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC));
			add(orthoTitle);
			indent(orthoPanel);

			// perspective projection
			perspTitle = new Label("");
			perspTitle.setStyleName("panelTitle");
			perspPanel = new FlowPanel();
			tfPerspLabel = new Label("");
			tfPersp = getTextField();
			tfPersp.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processPerspText();
					}
				}
			});

			tfPersp.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processPerspText();
				}
			});
			FlowPanel tfPerspPanel = new FlowPanel();
			tfPerspPanel.setStyleName("panelRowCell");
			tfPerspPanel.add(tfPerspLabel);
			tfPerspPanel.add(tfPersp);
			perspPanel.add(LayoutUtil.panelRow(projectionButtons
			        .getButton(EuclidianView3D.PROJECTION_PERSPECTIVE),
			        tfPerspPanel));
			add(perspTitle);
			indent(perspPanel);

			// glasses projection (two images)
			glassesTitle = new Label("");
			glassesTitle.setStyleName("panelTitle");
			glassesPanel = new FlowPanel();
			tfGlassesLabel = new Label("");
			tfGlassesEyeSep = getTextField();
			tfGlassesEyeSep.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processGlassesEyeSepText();
					}
				}
			});
			tfGlassesEyeSep.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processGlassesEyeSepText();
				}
			});
			cbGlassesGray = new CheckBox(app.getPlain("GrayScale"));
			cbGlassesGray.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view).setGlassesGrayScaled(cbGlassesGray
					        .getValue());
					view.repaintView();
				}
			});
			cbGlassesShutDownGreen = new CheckBox(app.getPlain("ShutDownGreen"));
			cbGlassesShutDownGreen.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((EuclidianView3D) view)
					        .setGlassesShutDownGreen(cbGlassesShutDownGreen
					                .getValue());
					view.repaintView();
				}
			});
			FlowPanel tfGlassesPanel = new FlowPanel();
			tfGlassesPanel.setStyleName("panelRowCell");
			tfGlassesPanel.add(tfGlassesLabel);
			tfGlassesPanel.add(tfGlassesEyeSep);
			tfGlassesPanel.add(cbGlassesGray);
			tfGlassesPanel.add(cbGlassesShutDownGreen);
			glassesPanel.add(LayoutUtil.panelRow(projectionButtons
			        .getButton(EuclidianView3D.PROJECTION_GLASSES),
			        tfGlassesPanel));
			add(glassesTitle);
			indent(glassesPanel);

			// oblique projection
			obliqueTitle = new Label("");
			obliqueTitle.setStyleName("panelTitle");
			obliquePanel = new FlowPanel();
			tfObliqueAngleLabel = new Label("");
			tfObliqueAngle = getTextField();
			tfObliqueAngle.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processObliqueAngleText();
					}
				}
			});

			tfObliqueAngle.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processObliqueAngleText();
				}
			});
			tfObliqueFactorLabel = new Label("");
			tfObliqueFactor = getTextField();
			tfObliqueFactor.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processObliqueFactorText();
					}
				}
			});

			tfObliqueFactor.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processObliqueFactorText();
				}
			});
			FlowPanel tfObliquePanel = new FlowPanel();
			tfObliquePanel.setStyleName("panelRowCell");
			tfObliquePanel.add(tfObliqueAngleLabel);
			tfObliquePanel.add(tfObliqueAngle);
			tfObliquePanel.add(tfObliqueFactorLabel);
			tfObliquePanel.add(tfObliqueFactor);
			obliquePanel.add(LayoutUtil.panelRow(projectionButtons
			        .getButton(EuclidianView3D.PROJECTION_OBLIQUE),
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
				((EuclidianView3D) view).getSettings()
				        .setProjectionPerspectiveEyeDistance(val);
				view.repaintView();
			} catch (NumberFormatException e) {
				tfPersp.setText(""
				        + (int) ((EuclidianView3D) view)
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
				((EuclidianView3D) view).getSettings().setEyeSep(val);
				view.repaintView();
			} catch (NumberFormatException e) {
				tfGlassesEyeSep.setText(""
				        + (int) ((EuclidianView3D) view).getEyeSep());
			}
		}

		protected void processObliqueAngleText() {
			try {
				double val = Double.parseDouble(tfObliqueAngle.getText());
				if (!Double.isNaN(val)) {

					((EuclidianView3D) view).getSettings()
					        .setProjectionObliqueAngle(val);
					view.repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueAngle.setText(""
				        + ((EuclidianView3D) view).getProjectionObliqueAngle());
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
					((EuclidianView3D) view).setProjectionObliqueFactor(val);
					view.repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueFactor
				        .setText(""
				                + ((EuclidianView3D) view)
				                        .getProjectionObliqueFactor());
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
			orthoTitle.setText(app.getPlain("Orthographic"));

			perspTitle.setText(app.getPlain("Perspective"));
			tfPerspLabel
			        .setText(app.getPlain(app.getPlain("EyeDistance") + ":"));

			glassesTitle.setText(app.getPlain("Glasses"));
			tfGlassesLabel.setText(app.getPlain("EyesSeparation") + ":");
			cbGlassesGray.setText(app.getPlain("GrayScale"));
			cbGlassesShutDownGreen.setText(app.getPlain("ShutDownGreen"));

			obliqueTitle.setText(app.getPlain("Oblique"));
			tfObliqueAngleLabel.setText(app.getPlain("Angle") + ":");
			tfObliqueFactorLabel.setText(app.getMenu("Dilate.Factor") + ":");

		}

		/**
		 * update text values
		 */
		public void updateGUI() {
			tfPersp.setText(""
			        + (int) ((EuclidianView3D) view)
			                .getProjectionPerspectiveEyeDistance());
			tfGlassesEyeSep.setText(""
			        + (int) ((EuclidianView3D) view).getEyeSep());
			cbGlassesGray.setValue(((EuclidianView3D) view)
			        .isGlassesGrayScaled());
			cbGlassesShutDownGreen.setValue(((EuclidianView3D) view)
			        .isGlassesShutDownGreen());
			tfObliqueAngle.setText(""
			        + ((EuclidianView3D) view).getProjectionObliqueAngle());
			tfObliqueFactor.setText(""
			        + ((EuclidianView3D) view).getProjectionObliqueFactor());

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

	@Override
	protected BasicTab newBasicTab() {
		return new BasicTab3D();
	}

	@Override
	public void updateGUI() {
		((BasicTab3D) basicTab).update3DProperties();
		projectionTab.updateGUI();
		super.updateGUI();
	}

	@Override
	public void setLabels() {

		TabBar tabBar = tabPanel.getTabBar();

		super.setLabels(tabBar, 4);

		tabBar.setTabText(3, app.getPlain("zAxis"));
		zAxisTab.setLabels();

		tabBar.setTabText(5, app.getPlain("Projection"));
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

}
