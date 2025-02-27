package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenPositionModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.CenterImageModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.DrawArrowsModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.IconOptionsModel;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.SegmentStyleModel;
import org.geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import org.geogebra.common.gui.dialog.options.model.StartPointModel;
import org.geogebra.common.gui.dialog.options.model.SymbolicModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldAlignmentModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextPropertyModel;
import org.geogebra.common.gui.dialog.options.model.VectorHeadStyleModel;
import org.geogebra.common.gui.dialog.options.model.VerticalIncrementModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.InlineTextFormatter;
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
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.properties.AnimationSpeedPanelW;
import org.geogebra.web.full.gui.properties.ComboBoxPanel;
import org.geogebra.web.full.gui.properties.IOptionPanel;
import org.geogebra.web.full.gui.properties.ListBoxPanel;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.util.ColorChooserW;
import org.geogebra.web.full.gui.util.ComboBoxW;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.full.gui.util.PointStylePopup;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

/**
 * options tab
 */
public class OptionsTab extends FlowPanel {
	/**
	 * 
	 */
	// private final OptionsObjectW optionsObjectW;
	private final AppW app;
	private final String titleId;
	private int index;
	private final List<OptionsModel> models;
	private final MultiRowsTabPanel tabPanel;
	private final Localization loc;
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
	 * init GUI
	 */
	public void initGUI() {
		this.focused = true;
		if (inited) {
			if (models.size() > 0 && !updated) {
				this.updateGUI(models.get(0).getGeos());
			}
			return;
		}
		inited = true;
		for (OptionsModel m : models) {
			IOptionPanel panel = buildPanel(m);
			if (panel != null) {
				add(panel.getWidget());
				// geos might be null in fome models because update only checks
				// for the first one
				m.updateMPanel(models.get(0).getGeos());
			}
		}
	}

	private IOptionPanel buildPanel(OptionsModel m) {
		if (m instanceof ColorObjectModel) {
			ColorPanel ret = new ColorPanel((ColorObjectModel) m, app);
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
		if (m instanceof IneqStyleModel
			|| m instanceof SymbolicModel
			|| m instanceof InterpolateImageModel
			|| m instanceof AbsoluteScreenLocationModel) {
			return new CheckboxPanel(app.getLocalization(), (BooleanOptionModel) m);
		}
		if (m instanceof TextFieldSizeModel) {
			return new TextPropertyPanel((TextPropertyModel) m, app, true);
		}
		if (m instanceof AnimationStepModel
				|| m instanceof VerticalIncrementModel) {
			return new TextPropertyPanel((TextPropertyModel) m, app, false);
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
		if (m instanceof DecoAngleModel) {
			return new IconDropdownPanelW((IconOptionsModel) m,
					app, GeoGebraIconW.getAngleDecoIcons());
		}
		if (m instanceof DecoSegmentModel) {
			return new IconDropdownPanelW((IconOptionsModel) m,
					app, GeoGebraIconW.getSegmentDecoIcons());
		}
		if (m instanceof SegmentStyleModel) {
			IOptionPanel ssp;
			if (((SegmentStyleModel) m).isStartStyle()) {
				ssp = new IconDropdownPanelW((SegmentStyleModel) m,
						app, GeoGebraIconW.createSegmentStartStyleIcons());
			} else {
				ssp = new IconDropdownPanelW((SegmentStyleModel) m, app,
						GeoGebraIconW.createSegmentEndStyleIcons());

			}
			ssp.getWidget().addStyleName("inlineOption");
			return ssp;
		}
		if (m instanceof VectorHeadStyleModel) {
			return new IconDropdownPanelW((IconOptionsModel) m,
					app, GeoGebraIconW.createVectorHeadStyleIcons());
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
		if (m instanceof AbsoluteScreenPositionModel) {
			IOptionPanel ret = new TextPropertyPanel((AbsoluteScreenPositionModel) m, app, true);
			ret.getWidget().setStyleName("optionsPanel");
			ret.getWidget().addStyleName("inlineOption");
			return ret;
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
		if (m instanceof TextFieldAlignmentModel) {
			return new TextFieldAlignmentPanel((TextFieldAlignmentModel) m, app);
		}
		if (m instanceof DrawArrowsModel) {
			return new CheckboxPanel(loc, (DrawArrowsModel) m);
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
		private final ColorObjectModel model;
		private final FlowPanel mainPanel;
		private final ColorChooserW colorChooserW;
		private GColor selectedColor;
		private final InlineTextFormatter inlineTextFormatter;

		/**
		 * @param model0
		 *            model
		 * @param app
		 *            application true if default
		 */
		public ColorPanel(ColorObjectModel model0, App app) {
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
			colorChooserW.enableColorPanel(!geo0.isGeoImage());

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
	}

	private static class TextFieldAlignmentPanel extends ListBoxPanel {

		TextFieldAlignmentPanel(TextFieldAlignmentModel model, AppW app) {
			super(app.getLocalization(), "stylebar.Align");
			model.setListener(this);
			setModel(model);
		}
	}

	private static class IconDropdownPanelW extends OptionPanel
			implements IComboListener {
		Label styleLbl;
		PopupMenuButtonW stylePopup;
		AppW app;
		IconOptionsModel model;

		public IconDropdownPanelW(IconOptionsModel model, AppW app, ImageOrText[] images) {
			this.app = app;
			this.model = model;
			model.setListener(this);
			setModel(model);

			FlowPanel mainWidget = new FlowPanel();
			styleLbl = new Label();
			mainWidget.add(styleLbl);

			stylePopup = new PopupMenuButtonW(app, images,
					-1, 1, SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent() {
					super.handlePopupActionEvent();
					int idx = getSelectedIndex();
					model.applyChanges(idx);
				}
			};
			stylePopup.setKeepVisible(false);
			mainWidget.add(stylePopup);
			setWidget(mainWidget);
			setLabels();
			getWidget().setStyleName("optionsPanel");
		}

		@Override
		public void setLabels() {
			styleLbl.setText(app.getLocalization().getMenu(model.getTitle()) + ":");
		}

		@Override
		public void setSelectedIndex(int index) {
			stylePopup.setSelectedIndex(index);
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

	private class PointSizePanel extends OptionPanel implements ISliderListener {
		PointSizeModel model;
		SliderPanel slider;
		private final Label titleLabel;

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
			slider.addInputHandler(event -> model.applyChangesNoUndo(slider.getValue()));
			slider.addValueChangeHandler(event -> model.storeUndoInfo());
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
		private final PointStyleModel model;
		private final Label titleLabel;
		private final PointStylePopup btnPointStyle;

		public PointStylePanel(PointStyleModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);
			model.setListener(this);
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsPanel");
			titleLabel = new Label("-");
			mainPanel.add(titleLabel);
			btnPointStyle = PointStylePopup.create(app, -1, false);
			btnPointStyle.addPopupHandler(model::applyChanges);
			btnPointStyle.setKeepVisible(false);
			mainPanel.add(btnPointStyle);

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
		private final Label thicknessSliderLabel;
		SliderPanel thicknessSlider;
		private final Label opacitySliderLabel;
		SliderPanel opacitySlider;
		private final Label popupLabel;
		private final Label styleHiddenLabel;
		LineStylePopup btnLineStyle;
		private final FlowPanel stylePanel;
		private final FlowPanel styleHiddenPanel;
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

			thicknessSlider.addInputHandler(
					event -> model.applyThickness(thicknessSlider.getValue()));
			thicknessSlider.addValueChangeHandler(val -> model.storeUndoInfo());
			opacitySliderLabel = new Label();

			FlowPanel lineOpacityPanel = new FlowPanel();
			lineOpacityPanel.setStyleName("optionsPanel");
			lineOpacityPanel.add(opacitySliderLabel);
			mainPanel.add(lineOpacityPanel);

			opacitySlider = new SliderPanel(0, 100);
			opacitySlider.setTickSpacing(5);
			// opacitySlider.setSnapToTicks(true);
			lineOpacityPanel.add(opacitySlider);

			opacitySlider.addInputHandler(model::applyOpacityPercentage);
			opacitySlider.addValueChangeHandler(val -> model.storeUndoInfo());

			stylePanel = new FlowPanel();
			stylePanel.setStyleName("optionsPanel");
			popupLabel = new Label();
			stylePanel.add(popupLabel);
			btnLineStyle = LineStylePopup.create(app);
			// slider.setSnapToTicks(true);
			btnLineStyle.addPopupHandler(model::applyLineTypeFromIndex);
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
			styleHiddenList.addChangeHandler(event -> model.applyLineStyleHidden(styleHiddenList
					.getSelectedIndex()));
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
		private final Label titleLabel;

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
			slider.addInputHandler(model::applyChanges);
			slider.addValueChangeHandler(event -> model.storeUndoInfo());
		}

		@Override
		public void setLabels() {
			titleLabel.setText(localize("Size"));
		}

		@Override
		public void setValue(int value) {
			slider.setValue(value);
		}

		@Override
		public void setSliderMin(int min) {
			slider.setMinimum(min);
		}
	}

	private class SlopeTriangleSizePanel extends OptionPanel implements
			ISliderListener {
		SlopeTriangleSizeModel model;
		SliderPanel slider;
		private final Label titleLabel;

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
			slider.addInputHandler(event -> model.applyChangesNoUndo(slider.getValue()));
			slider.addValueChangeHandler(val -> model.storeUndoInfo());
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

	public static class TextPropertyPanel extends OptionPanel implements
			ITextFieldListener {

		private final LocalizationW loc;
		private final boolean inline;
		TextPropertyModel model;
		private final InputPanelW inputPanel;
		AutoCompleteTextFieldW textField;
		Label label;

		/**
		 * @param model0 model
		 * @param app application
		 * @param inline whether label and input are on single line
		 */
		public TextPropertyPanel(TextPropertyModel model0, AppW app, boolean inline) {
			model = model0;
			model.setListener(this);
			this.inline = inline;
			this.loc = app.getLocalization();
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			label = new Label();
			inputPanel = new InputPanelW(null, app, false);
			textField = inputPanel.getTextComponent();
			textField.setAutoComplete(false);
			textField.addBlurHandler(event -> model.applyChanges(textField.getText()));
			textField.addKeyHandler(e -> {
				if (e.isEnterKey()) {
					model.applyChanges(textField.getText());
				}
			});
			if (inline) {
				mainPanel.add(LayoutUtilW.panelRow(label, inputPanel));
			} else {
				mainPanel.add(label);
				mainPanel.add(inputPanel);
			}
			mainPanel.setStyleName(inline ? "optionsPanel" : "optionsInput");
			setWidget(mainPanel);
		}

		@Override
		public void setText(String text) {
			textField.setText(text);
		}

		@Override
		public void setLabels() {
			label.setText(loc.getMenu(model.getTitle()) + (inline ? ":" : ""));
		}

	}

	/**
	 * settings for button size
	 */
	public class ButtonSizePanel extends OptionPanel implements
			IButtonSizeListener {
		private final InputPanelW ipButtonWidth;
		private final InputPanelW ipButtonHeight;
		private final AutoCompleteTextFieldW tfButtonWidth;
		private final AutoCompleteTextFieldW tfButtonHeight;
		private final ComponentCheckbox cbUseFixedSize;
		private final Label labelWidth;
		private final Label labelHeight;
		private final Label labelPixelW;
		private final Label labelPixelH;
		private final ButtonSizeModel model;

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
			cbUseFixedSize = new ComponentCheckbox(loc, false, "fixed",
					getModel()::applyChanges);
			setLabels();

			ipButtonWidth = new InputPanelW(null, app, false);
			ipButtonHeight = new InputPanelW(null, app, false);

			tfButtonWidth = ipButtonWidth.getTextComponent();
			tfButtonWidth.setAutoComplete(false);

			tfButtonHeight = ipButtonHeight.getTextComponent();
			tfButtonHeight.setAutoComplete(false);

			BlurHandler focusListener = event -> getModel().setSizesFromString(
					getTfButtonWidth().getText(),
					getTfButtonHeight().getText(),
					getCbUseFixedSize().isSelected());

			tfButtonWidth.addBlurHandler(focusListener);
			tfButtonHeight.addBlurHandler(focusListener);

			KeyHandler keyHandler = e -> {
				if (e.isEnterKey()) {
					getModel().setSizesFromString(
							getTfButtonWidth().getText(),
							getTfButtonHeight().getText(),
							getCbUseFixedSize().isSelected());
				}
			};

			tfButtonWidth.addKeyHandler(keyHandler);
			tfButtonHeight.addKeyHandler(keyHandler);

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
			cbUseFixedSize.setSelected(isFixed);
			tfButtonHeight.setText("" + height);
			tfButtonWidth.setText("" + width);
		}

		@Override
		public void setLabels() {
			labelWidth.setText(getLoc().getMenu("Width"));
			labelHeight.setText(getLoc().getMenu("Height"));
			labelPixelW.setText(getLoc().getMenu("Pixels.short"));
			labelPixelH.setText(getLoc().getMenu("Pixels.short"));
			cbUseFixedSize.setLabels();
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
		public ComponentCheckbox getCbUseFixedSize() {
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
		private final LodModel model;
		private final FlowPanel mainWidget;
		private final Label label;
		private final ListBox combo;

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
			combo.addChangeHandler(event -> getModel().applyChanges(getCombo().getSelectedIndex()));
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

		private final ImageCorner corner1;
		private final ImageCorner corner2;
		private final ImageCorner corner4;
		private final ImageCenter center;

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

		private final Kernel kernel;

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

		private final OptionsTab tab;
		CenterImageModel model;

		public CenterImagePanel(CenterImageModel model, AppW app, OptionsTab tab) {
			super(app.getLocalization(), model);
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