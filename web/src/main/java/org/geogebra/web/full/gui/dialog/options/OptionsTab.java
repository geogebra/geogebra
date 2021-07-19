package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.CenterImageModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel.IDecoAngleListener;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.DrawArrowsModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import org.geogebra.common.gui.dialog.options.model.StartPointModel;
import org.geogebra.common.gui.dialog.options.model.SymbolicModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldAlignmentModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.properties.AnimationSpeedPanelW;
import org.geogebra.web.full.gui.properties.AnimationStepPanelW;
import org.geogebra.web.full.gui.properties.ComboBoxPanel;
import org.geogebra.web.full.gui.properties.IOptionPanel;
import org.geogebra.web.full.gui.properties.ListBoxPanel;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.util.ColorChooserW;
import org.geogebra.web.full.gui.util.ComboBoxW;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.InlineTextFormatter;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.full.gui.util.PointStylePopup;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.PopupMenuHandler;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * options tab
 */
public class OptionsTab extends FlowPanel {
	/**
	 * 
	 */
	// private final OptionsObjectW optionsObjectW;
	private AppW app;
	private String titleId;
	private int index;
	private List<OptionsModel> models;
	private MultiRowsTabPanel tabPanel;
	private Localization loc;
	private boolean inited = false;
	private boolean focused = false;
	private boolean updated = true;
	
	/**
	 * @param app
	 *            applications
	 * @param loc
	 *            localization to get translations
	 * @param tabPanel
	 *            tab
	 * @param title
	 *            title of tab
	 */
	public OptionsTab(AppW app, Localization loc, MultiRowsTabPanel tabPanel,
			final String title) {
		super();
		// this.optionsObjectW = optionsObjectW;
		this.app = app;
		this.titleId = title;
		this.loc = loc;
		this.tabPanel = tabPanel;
		models = new ArrayList<>();
		setStyleName("propertiesTab");
	}

	/**
	 * @param panel
	 *            ad panel to model
	 */
	public void add(IOptionPanel panel) {
		add(panel.getWidget());
		models.add(panel.getModel());
	}

	/**
	 * @param model
	 *            model for options
	 * @return tab
	 */
	public OptionsTab addModel(OptionsModel model) {
		models.add(model);
		return this;
	}

	/**
	 * @param list
	 *            list of panels
	 */
	public void addPanelList(List<OptionPanel> list) {
		for (OptionPanel panel: list) {
			add(panel);
		}
	}

	/**
	 * @param geos
	 *            list of selected geos
	 * @return whether this panel is needed
	 */
	public boolean update(Object[] geos) {
		boolean enabled = updateGUI(geos);
		MultiRowsTabBar tabBar = this.tabPanel.getTabBar();
		tabBar.setTabText(index, getTabText());
		tabBar.setTabEnabled(index, enabled);

		if (!enabled && tabBar.getSelectedTab() == index) {
			tabBar.selectTab(0);
		}
		return enabled;
	}

	/**
	 * @param geos
	 *            construction elements
	 * @return whether this panel is needed
	 */
	boolean updateGUI(Object[] geos) {
		boolean enabled = false;
		if (focused) {
			this.updated = true;
			for (OptionsModel panel : models) {
				enabled = panel.updateMPanel(geos) || enabled;
			}
		} else {
			this.updated = false;
			for (OptionsModel panel : models) {
				panel.setGeos(geos);
				if (panel.checkGeos()) {
					return true;
				}
			}
		}
		return enabled;
	}

	private String getTabText() {
		return loc.getMenu(titleId);
	}

	/**
	 * add tab to panel
	 */
	public void addToTabPanel() {
		this.tabPanel.add(this, getTabText());
		index = this.tabPanel.getWidgetIndex(this);
	}

	/**
	 * @param height
	 *            current height
	 * @param width
	 *            current width
	 */
	public void onResize(int height, int width) {
		this.setHeight(height + "px");
		this.setWidth(width + "px");
    }

	/**
	 * @param isDefaults
	 *            true if default
	 */
	public void initGUI(boolean isDefaults) {
		this.focused = true;
		if (inited) {
			if (models.size() > 0 && !updated) {
				this.updateGUI(models.get(0).getGeos());
			}
			return;
		}
		inited = true;
		for (OptionsModel m : models) {
			IOptionPanel panel = buildPanel(m, isDefaults);
			if (panel != null) {
				add(panel.getWidget());
				// geos might be null in fome models because update only checks
				// for the first one
				m.updateMPanel(models.get(0).getGeos());
			}
		}
	}

	private IOptionPanel buildPanel(OptionsModel m, boolean isDefaults) {
		if (m instanceof ColorObjectModel) {
			ColorPanel ret = new ColorPanel((ColorObjectModel) m, app,
					isDefaults);
			((GuiManagerW) app.getGuiManager()).setColorTab(ret);
			return ret;
		}

		if (m instanceof PointSizeModel) {
			return new PointSizePanel((PointSizeModel) m);
		}
		if (m instanceof PointStyleModel) {
			return new PointStylePanel((PointStyleModel) m, app);
		}
		if (m instanceof LineStyleModel) {
			return new LineStylePanel((LineStyleModel) m, app);
		}
		if (m instanceof AngleArcSizeModel) {
			return new AngleArcSizePanel((AngleArcSizeModel) m);
		}
		if (m instanceof SlopeTriangleSizeModel) {
			return new SlopeTriangleSizePanel((SlopeTriangleSizeModel) m);
		}
		if (m instanceof IneqStyleModel) {
			return new IneqPanel((IneqStyleModel) m, app);
		}
		if (m instanceof TextFieldSizeModel) {
			return new TextFieldSizePanel((TextFieldSizeModel) m, app);
		}
		if (m instanceof ButtonSizeModel) {
			return new ButtonSizePanel((ButtonSizeModel) m, app);
		}
		if (m instanceof FillingModel) {
			return new FillingPanel((FillingModel) m, app);
		}
		if (m instanceof LodModel) {
			return new LodPanel((LodModel) m);
		}
		if (m instanceof SymbolicModel) {
			CheckboxPanel ret = new CheckboxPanel("Symbolic",
					app.getLocalization(), (SymbolicModel) m);
			return ret;
		}
		if (m instanceof InterpolateImageModel) {
			return new CheckboxPanel("Interpolate", app.getLocalization(),
					(InterpolateImageModel) m);
		}
		if (m instanceof DecoAngleModel) {
			DecoAnglePanel dap = new DecoAnglePanel((DecoAngleModel) m, app);
			dap.getWidget().setStyleName("optionsPanel");
			return dap;
		}
		if (m instanceof DecoSegmentModel) {
			DecoSegmentPanel dsp = new DecoSegmentPanel((DecoSegmentModel) m,
					app);
			dsp.getWidget().setStyleName("optionsPanel");
			return dsp;
		}
		if (m instanceof TextOptionsModel) {
			return new TextOptionsPanelW((TextOptionsModel) m, app);
		}
		if (m instanceof ScriptEditorModel) {
			return new ScriptEditPanel((ScriptEditorModel) m, app);
		}
		if (m instanceof StartPointModel) {
			return new StartPointPanel((StartPointModel) m, app);
		}
		if (m instanceof CornerPointsModel) {
			return new CornerPointsPanel((CornerPointsModel) m, app);
		}
		if (m instanceof AbsoluteScreenLocationModel) {
			return new CheckboxPanel("AbsoluteScreenLocation",
					app.getLocalization(), (AbsoluteScreenLocationModel) m);
		}
		if (m instanceof CenterImageModel) {
			return new CenterImagePanel((CenterImageModel) m, app, this);
		}
		if (m instanceof CoordsModel) {
			return new CoordsPanel((CoordsModel) m, app);
		}
		if (m instanceof LineEqnModel) {
			return new LineEqnPanel((LineEqnModel) m, app);
		}
		if (m instanceof PlaneEqnModel) {
			return new PlaneEqnPanel((PlaneEqnModel) m, app);
		}
		if (m instanceof ConicEqnModel) {
			return new ConicEqnPanel((ConicEqnModel) m, app);
		}
		if (m instanceof AnimationSpeedModel) {
			return new AnimationSpeedPanelW((AnimationSpeedModel) m, app);
		}
		if (m instanceof AnimationStepModel) {
			return new AnimationStepPanelW((AnimationStepModel) m, app);
		}
		if (m instanceof TextFieldAlignmentModel) {
			return new TextFieldAlignmentPanel((TextFieldAlignmentModel) m, app);
		}
		if (m instanceof DrawArrowsModel) {
			return new CheckboxPanel("DrawArrows", loc, (DrawArrowsModel) m);
		}

		return null;
	}

	/**
	 * @param id
	 *            key of word
	 * @return translation
	 */
	String localize(final String id) {
		return loc.getMenu(id);
	}

	/**
	 * Panel for color settings
	 */
	public static class ColorPanel extends OptionPanel
			implements IColorObjectListener {
		private ColorObjectModel model;
		private FlowPanel mainPanel;
		private ColorChooserW colorChooserW;
		private GColor selectedColor;
		private CheckBox sequential;
		private InlineTextFormatter inlineTextFormatter;

		/**
		 * @param model0
		 *            model
		 * @param app
		 *            application true if default
		 * @param isDefaults
		 *            whether this is for defaults: not used in web
		 */
		public ColorPanel(ColorObjectModel model0, App app,
				boolean isDefaults) {
			this.model = model0;
			model.setListener(this);
			setModel(model);

			final Dimension colorIconSizeW = new Dimension(20, 20);

			colorChooserW = new ColorChooserW(app, 350, 210, colorIconSizeW, 4);
			colorChooserW.addChangeHandler(new ColorChangeHandler() {

				@Override
				public void onColorChange(GColor color) {
					applyChanges(false);
				}

				@Override
				public void onAlphaChange() {
					applyChanges(true);
				}

				@Override
				public void onClearBackground() {
					getModel().clearBackgroundColor();
				}

				@Override
				public void onBackgroundSelected() {
					updatePreview(getModel().getGeoAt(0).getBackgroundColor(),
							1.0);
				}

				@Override
				public void onForegroundSelected() {
					GeoElement geo0 = getModel().getGeoAt(0);
					double alpha = 1.0;
					GColor color = null;
					if (geo0.isFillable()) {
						color = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						color = geo0.getObjectColor();
					}
					updatePreview(color, alpha);
				}

				@Override
				public void onBarSelected() {
					updateChooserFromBarChart(getModel().getGeoAt(0));
				}
			});
			colorChooserW.setColorPreviewClickable();
			inlineTextFormatter = new InlineTextFormatter();
			mainPanel = new FlowPanel();
			mainPanel.add(colorChooserW);

			if (isDefaults) {
				sequential = new CheckBox("Sequential");
				mainPanel.add(sequential);
				sequential.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// TODO we may need to update the GUI here
						getModel().setSequential(getSequential().getValue());
					}
				});
			}
			setWidget(mainPanel);
		}

		/**
		 * @param alphaOnly
		 *            no color, only alpha
		 */
		public void applyChanges(boolean alphaOnly) {
			double alpha = colorChooserW.getAlphaValue();
			GColor color = colorChooserW.getSelectedColor();
			if (model.isBarChart()) {
				model.applyBar(colorChooserW.getSelectedBar(),
						alphaOnly ? null : color, alpha);
			} else {
				model.applyChanges(color, alpha, alphaOnly);
			}
			if (!alphaOnly) {
				inlineTextFormatter.formatInlineText(
						model.getGeosAsList(),
						"color",
						StringUtil.toHtmlColor(color));
			}
		}

		@Override
		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground, boolean hasOpacity) {

			GeoElement geo0 = model.getGeoAt(0);
			colorChooserW.setChartAlgo(model.getBarChartIntervals(),
					model.getGeos());

			if (updateChooserFromBarChart(geo0)) {
				return;
			}

			GColor selectedBGColor = null;
			double alpha = 1;
			if (geo0.isGeoImage()) {
				colorChooserW.enableColorPanel(false);
			} else {
				colorChooserW.enableColorPanel(true);
			}

			selectedColor = null;
			if (equalObjColorBackground) {
				selectedBGColor = geo0.getBackgroundColor();
			}

			if (isBackgroundColorSelected()) {
				selectedColor = selectedBGColor;
			} else {
				// set selectedColor if all selected geos have the same color
				if (equalObjColor) {
					if (allFillable) {
						selectedColor = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						selectedColor = geo0.getObjectColor();
					}
				}
			}

			if (allFillable && hasOpacity) { // show opacity slider and set to
				// first geo's
				// alpha value
				colorChooserW.enableOpacity(true);
				alpha = geo0.getAlphaValue();

				colorChooserW.setAlphaValue(Math.round(alpha * 100));
			} else { // hide opacity slider and set alpha = 1
				colorChooserW.enableOpacity(false);
				alpha = 1;
				colorChooserW.setAlphaValue(Math.round(alpha * 100));
			}

			colorChooserW.enableBackgroundColorPanel(hasBackground);
			updatePreview(selectedColor, alpha);
		}

		/**
		 * @param geo0
		 *            geoElement
		 * @return whether this is a barchart
		 */
		public boolean updateChooserFromBarChart(GeoElement geo0) {
			ChartStyle chartStyle = model.getChartStyle();

			if (chartStyle == null) {
				return false;
			}

			double alpha = geo0.getAlphaValue();

			int barIdx = colorChooserW.getSelectedBar();

			if (barIdx == ColorObjectModel.ALL_BARS
					|| chartStyle.getBarColor(barIdx) == null) {
				selectedColor = geo0.getObjectColor();

			} else {
				selectedColor = chartStyle.getBarColor(barIdx);
				alpha = chartStyle.getBarAlpha(barIdx);
				if (selectedColor == null) {
					selectedColor = geo0.getObjectColor();
				}

				if (alpha == -1) {
					alpha = geo0.getAlphaValue() * 100;
				}
			}

			colorChooserW.enableBackgroundColorPanel(false);

			updatePreview(selectedColor, alpha);

			return true;
		}

		@Override
		public void updatePreview(GColor color, double alpha) {
			colorChooserW.setSelectedColor(color);
			colorChooserW.setAlphaValue(alpha);
			colorChooserW.update();
		}

		@Override
		public boolean isBackgroundColorSelected() {
			return colorChooserW.isBackgroundColorSelected();
		}

		@Override
		public void updateNoBackground(GeoElement geo, GColor col, double alpha,
				boolean updateAlphaOnly, boolean allFillable) {
			if (!updateAlphaOnly) {
				geo.setObjColor(col);
			}
			if (allFillable) {
				geo.setAlphaValue(alpha);
			}
		}

		@Override
		public void setLabels() {
			colorChooserW.setLabels();
		}

		/**
		 * @param background
		 *            background is selected
		 */
		public void setBackground(boolean background) {
			colorChooserW.setBackground(background);
		}

		@Override
		public ColorObjectModel getModel() {
			return model;
		}

		/**
		 * @return sequential check box
		 */
		public CheckBox getSequential() {
			return sequential;
		}
	}

	private static class TextFieldAlignmentPanel extends ListBoxPanel {

		TextFieldAlignmentPanel(TextFieldAlignmentModel model, AppW app) {
			super(app.getLocalization(), "stylebar.Align");
			model.setListener(this);
			setModel(model);
		}
	}

	private static class DecoAnglePanel extends DecoOptionPanel
			implements IDecoAngleListener {

		DecoAngleModel model;

		public DecoAnglePanel(DecoAngleModel model0, AppW app) {
			super(app);
			model = model0;
			model.setListener(this);
			setModel(model);
			final ImageOrText[] iconArray = new ImageOrText[DecoAngleModel
					.getDecoTypeLength()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIconW.createDecorAngleIcon(i);
			}
			init(iconArray, model);
		}

		@Override
		public void setArcSizeMinValue() {
			// angleArcSizePanel.setMinValue(); //TODO update min arc size on
			// deco change
		}

	}

	private static class DecoSegmentPanel extends DecoOptionPanel {
		DecoSegmentModel model;

		public DecoSegmentPanel(DecoSegmentModel model0, AppW app) {
			super(app);
			model = model0;
			model.setListener(this);
			setModel(model);

			final ImageOrText[] iconArray = new ImageOrText[DecoSegmentModel
					.getDecoTypeLength()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIconW.createDecorSegmentIcon(i);
			}
			init(iconArray, model);
		}
	}

	private class PointSizePanel extends OptionPanel implements ISliderListener {
		PointSizeModel model;
		SliderPanel slider;
		private Label titleLabel;

		public PointSizePanel(PointSizeModel model0) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsPanel");
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			slider = new SliderPanel(1, 9);
			slider.setTickSpacing(1);
			// slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}
			});
		}

		@Override
		public void setLabels() {
			titleLabel.setText(localize("PointSize"));
		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);
		}

	}

	private class PointStylePanel extends OptionPanel implements IComboListener {
		private PointStyleModel model;
		private Label titleLabel;
		private PointStylePopup btnPointStyle;

		public PointStylePanel(PointStyleModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);
			model.setListener(this);
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsPanel");
			titleLabel = new Label("-");
			mainPanel.add(titleLabel);
			btnPointStyle = PointStylePopup.create(app, -1, false, model);
			if (btnPointStyle != null) {
				btnPointStyle.setKeepVisible(false);
				mainPanel.add(btnPointStyle);
			}
			setWidget(mainPanel);
		}

		@Override
		public void setLabels() {
			titleLabel.setText(localize("PointStyle"));
		}

		@Override
		public void setSelectedIndex(int index) {
			if (btnPointStyle != null) {
				btnPointStyle.setSelectedIndex(index);
			}
		}

		@Override
		public void addItem(String item) {
			// do nothing
		}

		@Override
		public void clearItems() {
			// do nothing
		}

	}

	private class LineStylePanel extends OptionPanel implements
			ILineStyleListener {

		LineStyleModel model;
		private Label thicknessSliderLabel;
		SliderPanel thicknessSlider;
		private Label opacitySliderLabel;
		SliderPanel opacitySlider;
		private Label popupLabel;
		private Label styleHiddenLabel;
		LineStylePopup btnLineStyle;
		private FlowPanel stylePanel;
		private FlowPanel styleHiddenPanel;
		ListBox styleHiddenList;

		public LineStylePanel(LineStyleModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			thicknessSliderLabel = new Label();

			FlowPanel lineThicknessPanel = new FlowPanel();
			lineThicknessPanel.setStyleName("optionsPanel");
			lineThicknessPanel.add(thicknessSliderLabel);
			mainPanel.add(lineThicknessPanel);

			thicknessSlider = new SliderPanel(1,
					app.isWhiteboardActive()
							? 2 * EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE
							: GeoElement.MAX_LINE_WIDTH);
			thicknessSlider.setTickSpacing(1);
			// slider.setSnapToTicks(true);
			lineThicknessPanel.add(thicknessSlider);

			thicknessSlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyThickness(thicknessSlider.getValue());
				}
			});
			opacitySliderLabel = new Label();

			FlowPanel lineOpacityPanel = new FlowPanel();
			lineOpacityPanel.setStyleName("optionsPanel");
			lineOpacityPanel.add(opacitySliderLabel);
			mainPanel.add(lineOpacityPanel);

			opacitySlider = new SliderPanel(0, 100);
			opacitySlider.setTickSpacing(5);
			// opacitySlider.setSnapToTicks(true);
			lineOpacityPanel.add(opacitySlider);

			opacitySlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int value = (int) ((opacitySlider.getValue() / 100.0) * 255);
					model.applyOpacity(value);
				}
			});

			stylePanel = new FlowPanel();
			stylePanel.setStyleName("optionsPanel");
			popupLabel = new Label();
			stylePanel.add(popupLabel);
			btnLineStyle = LineStylePopup.create(app, false);
			// slider.setSnapToTicks(true);
			btnLineStyle.addPopupHandler(new PopupMenuHandler() {

				@Override
				public void fireActionPerformed(PopupMenuButtonW actionButton) {
					model.applyLineTypeFromIndex(btnLineStyle
							.getSelectedIndex());

				}
			});
			btnLineStyle.setKeepVisible(false);
			mainPanel.add(btnLineStyle);

			stylePanel.add(btnLineStyle);
			mainPanel.add(stylePanel);

			styleHiddenPanel = new FlowPanel();
			styleHiddenPanel.setStyleName("optionsPanel");
			styleHiddenLabel = new Label();
			styleHiddenPanel.add(styleHiddenLabel);
			styleHiddenList = new ListBox();
			styleHiddenList.setMultipleSelect(false);
			styleHiddenList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyLineStyleHidden(styleHiddenList
							.getSelectedIndex());
				}

			});
			styleHiddenPanel.add(styleHiddenList);
			mainPanel.add(styleHiddenPanel);

			setWidget(mainPanel);
		}

		@Override
		public void setLabels() {
			thicknessSliderLabel.setText(localize("Thickness"));
			opacitySliderLabel.setText(localize("LineOpacity"));
			popupLabel.setText(localize("LineStyle") + ":");
			styleHiddenLabel.setText(localize("HiddenLineStyle") + ":");
			int selectedIndex = styleHiddenList.getSelectedIndex();
			styleHiddenList.clear();
			styleHiddenList.addItem(localize("Hidden.Invisible")); // index 0
			styleHiddenList.addItem(localize("Hidden.Dashed")); // index 1
			styleHiddenList.addItem(localize("Hidden.Unchanged")); // index 2
			styleHiddenList.setSelectedIndex(selectedIndex);
		}

		@Override
		public void setThicknessSliderValue(int value) {
			thicknessSlider.setValue(value);
		}

		@Override
		public void setThicknessSliderMinimum(int minimum) {
			thicknessSlider.setMinimum(minimum);
		}

		@Override
		public void selectCommonLineStyle(boolean equalStyle, int type) {
			btnLineStyle.selectLineType(type);
		}

		@Override
		public void selectCommonLineStyleHidden(boolean equalStyle, int type) {
			styleHiddenList.setSelectedIndex(type);
		}

		@Override
		public void setLineTypeVisible(boolean value) {
			stylePanel.setVisible(value);
		}

		@Override
		public void setLineStyleHiddenVisible(boolean value) {
			styleHiddenPanel.setVisible(value);
		}

		@Override
		public void setOpacitySliderValue(int value) {
			opacitySlider.setValue(value);
		}

		@Override
		public void setLineOpacityVisible(boolean value) {
			opacitySlider.setVisible(value);
		}
	}

	private class AngleArcSizePanel extends OptionPanel implements
			ISliderListener {
		AngleArcSizeModel model;
		SliderPanel slider;
		private Label titleLabel;

		public AngleArcSizePanel(AngleArcSizeModel model0) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			slider = new SliderPanel(10, 100);
			slider.setTickSpacing(5);
			// slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}
			});
		}

		@Override
		public void setLabels() {
			titleLabel.setText(localize("Size"));
		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);
		}
	}

	private class SlopeTriangleSizePanel extends OptionPanel implements
			ISliderListener {
		SlopeTriangleSizeModel model;
		SliderPanel slider;
		private Label titleLabel;

		public SlopeTriangleSizePanel(SlopeTriangleSizeModel model0) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			slider = new SliderPanel(1, 10);
			slider.setTickSpacing(1);
			// slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}
			});
		}

		@Override
		public void setLabels() {
			titleLabel.setText(localize("Size"));
		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);
		}

	}

	private static class IneqPanel extends CheckboxPanel
			implements IIneqStyleListener {

		public IneqPanel(IneqStyleModel model, AppW app) {
			super("ShowOnXAxis", app.getLocalization(), model);
		}

		@Override
		public void enableFilling(boolean value) {
			// fillingPanel.setAllEnabled(value);
		}

		// @Override
		// public void apply(boolean value) {
		// super.apply(value);
		// enableFilling(!value);
		// }

	}

	private class TextFieldSizePanel extends OptionPanel implements
			ITextFieldListener {

		TextFieldSizeModel model;
		private InputPanelW inputPanel;
		AutoCompleteTextFieldW tfSize;
		Label lbSize;

		public TextFieldSizePanel(TextFieldSizeModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			lbSize = new Label();
			inputPanel = new InputPanelW(null, app, 1, -1, false);
			tfSize = inputPanel.getTextComponent();
			tfSize.setAutoComplete(false);
			tfSize.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					model.applyChanges(tfSize.getText());

				}
			});
			tfSize.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyChanges(tfSize.getText());
					}
				}
			});
			mainPanel.add(LayoutUtilW.panelRow(lbSize, inputPanel));
			mainPanel.setStyleName("optionsPanel");
			setWidget(mainPanel);

		}

		@Override
		public void setText(String text) {
			tfSize.setText(text);
		}

		@Override
		public void setLabels() {
			lbSize.setText(localize("TextfieldLength"));
		}

	}

	/**
	 * settings for button size
	 */
	public class ButtonSizePanel extends OptionPanel implements
			IButtonSizeListener {
		private InputPanelW ipButtonWidth;
		private InputPanelW ipButtonHeight;
		private AutoCompleteTextFieldW tfButtonWidth;
		private AutoCompleteTextFieldW tfButtonHeight;
		private CheckBox cbUseFixedSize;
		private Label labelWidth;
		private Label labelHeight;
		private Label labelPixelW;
		private Label labelPixelH;
		private ButtonSizeModel model;

		/**
		 * @param model0
		 *            model
		 * @param app
		 *            application
		 */
		public ButtonSizePanel(ButtonSizeModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);
			labelWidth = new Label();
			labelHeight = new Label();
			labelPixelW = new Label();
			labelPixelH = new Label();
			cbUseFixedSize = new CheckBox();
			setLabels();

			ipButtonWidth = new InputPanelW(null, app, 1, -1, false);
			ipButtonHeight = new InputPanelW(null, app, 1, -1, false);

			tfButtonWidth = ipButtonWidth.getTextComponent();
			tfButtonWidth.setAutoComplete(false);

			tfButtonHeight = ipButtonHeight.getTextComponent();
			tfButtonHeight.setAutoComplete(false);

			BlurHandler focusListener = new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					getModel().setSizesFromString(getTfButtonWidth().getText(),
							getTfButtonHeight().getText(),
							getCbUseFixedSize().getValue());

				}
			};

			tfButtonWidth.addBlurHandler(focusListener);
			tfButtonHeight.addBlurHandler(focusListener);

			KeyHandler keyHandler = new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						getModel().setSizesFromString(
								getTfButtonWidth().getText(),
								getTfButtonHeight().getText(),
								getCbUseFixedSize().getValue());
					}
				}

			};

			tfButtonWidth.addKeyHandler(keyHandler);
			tfButtonHeight.addKeyHandler(keyHandler);

			cbUseFixedSize.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					getModel().applyChanges(getCbUseFixedSize().getValue());

				}
			});
			// tfButtonHeight.setInputVerifier(new SizeVerify());
			// tfButtonWidth.setInputVerifier(new SizeVerify());
			// tfButtonHeight.setEnabled(cbUseFixedSize.getValue());
			// tfButtonWidth..setEnabled(cbUseFixedSize.getValue());

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("textPropertiesTab");
			FlowPanel fixedPanel = new FlowPanel();
			FlowPanel widthPanel = new FlowPanel();
			FlowPanel heightPanel = new FlowPanel();
			fixedPanel.setStyleName("optionsPanel");
			widthPanel.setStyleName("optionsPanel");
			heightPanel.setStyleName("optionsPanel");
			fixedPanel.add(cbUseFixedSize);
			widthPanel.add(labelWidth);
			widthPanel.add(tfButtonWidth);
			widthPanel.add(labelPixelW);
			heightPanel.add(labelHeight);
			heightPanel.add(tfButtonHeight);
			heightPanel.add(labelPixelH);
			mainPanel.add(fixedPanel);
			mainPanel.add(widthPanel);
			mainPanel.add(heightPanel);
			setWidget(mainPanel);
		}

		@Override
		public void updateSizes(int width, int height, boolean isFixed) {
			cbUseFixedSize.setValue(isFixed);
			tfButtonHeight.setText("" + height);
			tfButtonWidth.setText("" + width);
			// tfButtonHeight.setEnabled(isFixed);
			// tfButtonWidth.setEnabled(isFixed);
		}

		@Override
		public void setLabels() {
			labelWidth.setText(getLoc().getMenu("Width"));
			labelHeight.setText(getLoc().getMenu("Height"));
			labelPixelW.setText(getLoc().getMenu("Pixels.short"));
			labelPixelH.setText(getLoc().getMenu("Pixels.short"));
			cbUseFixedSize.setText(getLoc().getMenu("fixed"));
		}

		/**
		 * @return text area btn for width
		 */
		public AutoCompleteTextFieldW getTfButtonWidth() {
			return tfButtonWidth;
		}

		/**
		 * @return text area btn for height
		 */
		public AutoCompleteTextFieldW getTfButtonHeight() {
			return tfButtonHeight;
		}

		/**
		 * @return check box to fix size
		 */
		public CheckBox getCbUseFixedSize() {
			return cbUseFixedSize;
		}

		@Override
		public ButtonSizeModel getModel() {
			return model;
		}
	}

	/**
	 * level of detail panel
	 */
	class LodPanel extends OptionPanel implements IComboListener {
		private LodModel model;
		private FlowPanel mainWidget;
		private Label label;
		private ListBox combo;

		/**
		 * @param model0
		 *            model
		 */
		public LodPanel(LodModel model0) {
			model = model0;
			model.setListener(this);
			setModel(model);

			mainWidget = new FlowPanel();
			label = new Label();
			mainWidget.add(label);
			combo = new ListBox();
			combo.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					getModel().applyChanges(getCombo().getSelectedIndex());
				}
			});
			mainWidget.add(combo);
			setWidget(mainWidget);
		}

		@Override
		public void setLabels() {
			label.setText(getLoc().getMenu("LevelOfDetail"));
			int idx = combo.getSelectedIndex();
			combo.clear();
			model.fillModes(getLoc());
			combo.setSelectedIndex(idx);
		}

		@Override
		public void addItem(String item) {
			combo.addItem(item);
		}

		@Override
		public void setSelectedIndex(int index) {
			combo.setSelectedIndex(index);
		}

		@Override
		public void clearItems() {
			// do nothing
		}

		/**
		 * @return combo list
		 */
		public ListBox getCombo() {
			return combo;
		}

		@Override
		public LodModel getModel() {
			return model;
		}
	}

	private static class ImageCorner extends ComboBoxPanel {

		protected ImageCornerModel model;
		protected Localization localization;

		public ImageCorner(int cornerIdx, AppW app) {
			super(app, "CornerModel");
			this.localization = app.getLocalization();
			model = new ImageCornerModel(app);
			model.setListener(this);
			model.setCornerIdx(cornerIdx);
			setModel(model);
		}

		public void setIcon(ImageResource res) {
			if (res == null) {
				return;
			}
			FormLabel label = getLabel();
			label.setStyleName("imageCorner");
			label.getElement().getStyle()
					.setProperty("backgroundImage", "url(" + res + ")");
		}

		@Override
		protected void onComboBoxChange() {
			final String item = getComboBox().getValue();
			model.applyChanges(item, this);
		}

		@Override
		public void setLabels() {
			super.setLabels();
			if (model.isCenter()) {
				getLabel().setText(localization.getMenu("Center") + ":");
			} else {
				String strLabelStart = localization.getMenu("CornerPoint");
				getLabel().setText(
						strLabelStart + " " + model.getCornerNumber() + ":");
			}
		}

		@Override
		public OptionPanel updatePanel(Object[] geos) {
			getComboBox().setValue("");
			return super.updatePanel(geos);
		}

	}
	
	private static class ImageCenter extends ImageCorner {

		public ImageCenter(AppW app) {
			super(GeoImage.CENTER_INDEX, app);
		}
		
		@Override
		public void setIcon(ImageResource res) {
			// No icon for center point.
		}

		@Override
		public void setLabels() {
			super.setLabels();
			if (model.isCenter()) {
				getLabel().setText(localization.getCommand("Center") + ":");
			}
		}
	}

	/**
	 * position of corner panel
	 */
	static class CornerPointsPanel extends OptionPanel {

		private ImageCorner corner1;
		private ImageCorner corner2;
		private ImageCorner corner4;
		private ImageCenter center;

		/**
		 * @param model
		 *            model
		 * @param app
		 *            application
		 */
		public CornerPointsPanel(CornerPointsModel model, AppW app) {
			model.setListener(this);
			setModel(model);
			corner1 = new ImageCorner(0, app);
			corner2 = new ImageCorner(1, app);
			corner4 = new ImageCorner(2, app);
			center = new ImageCenter(app);
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.add(corner1.getWidget());
			mainPanel.add(corner2.getWidget());
			mainPanel.add(corner4.getWidget());
			if (center != null) {
				mainPanel.add(center.getWidget());
			}
			setWidget(mainPanel);
			corner1.setIcon(AppResources.INSTANCE.corner1());
			corner2.setIcon(AppResources.INSTANCE.corner2());
			corner4.setIcon(AppResources.INSTANCE.corner4());
		}

		@Override
		public void setLabels() {
			corner1.setLabels();
			corner2.setLabels();
			corner4.setLabels();
			if (center != null) {
				center.setLabels();
			}
		}

		@Override
		public OptionPanel updatePanel(Object[] geos) {
			if (geos == null) {
				return null;
			}

			boolean result = corner1.updatePanel(geos) != null;
			result = corner2.updatePanel(geos) != null || result;
			result = corner4.updatePanel(geos) != null || result;
			if (center != null) {
				center.updatePanel(geos);
			}
			return result ? this : null;
		}
	}

	private class StartPointPanel extends ComboBoxPanel {

		private Kernel kernel;

		public StartPointPanel(StartPointModel model, AppW app) {
			super(app, "StartingPoint");
			this.kernel = app.getKernel();
			model.setListener(this);
			setModel(model);
		}

		private StartPointModel getStartPointModel() {
			return (StartPointModel) getModel();
		}

		@Override
		public OptionPanel updatePanel(Object[] geos) {
			getModel().setGeos(geos);
			boolean geosOK = getModel().checkGeos();

			if (getWidget() != null) {
				getWidget().setVisible(geosOK);
			}

			if (!geosOK || getWidget() == null) {
				return null;
			}

			ComboBoxW combo = getComboBox();
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != combo.getItemCount() - 1) {
				combo.getModel().clear();
				combo.addItem("");
				getStartPointModel().fillModes(getLoc());
				setFirstLabel();
			}

			getModel().updateProperties();
			setLabels();
			return this;
		}

		@Override
		protected void onComboBoxChange() {
			final String strLoc = getComboBox().getValue();
			getStartPointModel().applyChanges(strLoc, this);
		}

		@Override
		public void setSelectedIndex(int index) {
			ComboBoxW cb = getComboBox();
			if (index == 0) {
				setFirstLabel();
			} else {
				cb.setSelectedIndex(-1);
			}
		}

		private void setFirstLabel() {
			GeoElement p = (GeoElement) getStartPointModel().getLocateableAt(0)
					.getStartPoint();
			if (p != null) {
				String coords = p.getLabel(StringTemplate.editTemplate);
				getComboBox().setValue(coords);
			}
		}

		@Override
		public void setLabels() {
			getLabel().setText(getLoc().getMenu(getTitle()) + ":");
		}
	} // StartPointPanel

	private static class CoordsPanel extends ListBoxPanel {

		public CoordsPanel(CoordsModel model, AppW app) {
			super(app.getLocalization(), "Coordinates");
			model.setListener(this);
			setModel(model);
		}
	} // CoordsPanel

	private static class CenterImagePanel extends CheckboxPanel {

		private OptionsTab tab;
		CenterImageModel model;

		public CenterImagePanel(CenterImageModel model, AppW app, OptionsTab tab) {
			super("CenterImage", app.getLocalization(), model);
			this.tab = tab;
			this.model = model;
		}
		
		@Override
		public void onChecked() {
			tab.updateGUI(model.getGeos());
		}
		
	}

	private static class LineEqnPanel extends ListBoxPanel {

		public LineEqnPanel(LineEqnModel model, AppW app) {
			super(app.getLocalization(), "Equation");
			model.setListener(this);
			setModel(model);
		}
	}

	private static class PlaneEqnPanel extends ListBoxPanel {

		public PlaneEqnPanel(PlaneEqnModel model, AppW app) {
			super(app.getLocalization(), "Equation");
			model.setListener(this);
			setModel(model);
		}
	} // LineEqnPanel

	private class ConicEqnPanel extends ListBoxPanel {

		public ConicEqnPanel(ConicEqnModel model, AppW app) {
			super(app.getLocalization(), "Equation");
			model.setListener(this);
			setModel(model);
		}

		@Override
		public void setLabels() {
			setTitle(getLoc().getMenu(getTitle()));
			ListBox lb = getListBox();
			if (getModel().hasGeos() && getModel().checkGeos()) {
				int selectedIndex = lb.getSelectedIndex();
				lb.clear();
				getModel().updateProperties();
				lb.setSelectedIndex(selectedIndex);
			}
		}

	} // ConicEqnPanel

	/**
	 * @param b
	 *            true if should have focus
	 */
	public void setFocused(boolean b) {
		this.focused = b;
	}

	/**
	 * @return localization
	 */
	public Localization getLoc() {
		return loc;
	}
	
}