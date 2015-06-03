package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel.IDecoAngleListener;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.properties.IOptionPanel;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.util.ColorChooserW;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.LineStylePopup;
import org.geogebra.web.web.gui.util.PointStylePopup;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;

class OptionsTab extends FlowPanel {
	/**
	 * 
	 */
	// private final OptionsObjectW optionsObjectW;
	private String titleId;
	private int index;
	private List<OptionsModel> models;
	private TabPanel tabPanel;
	private Localization loc;
	private boolean inited = false;
	
	public OptionsTab(Localization loc, TabPanel tabPanel,
			final String title) {
		super();
		// this.optionsObjectW = optionsObjectW;
		this.titleId = title;
		this.loc = loc;
		this.tabPanel = tabPanel;
		models = new ArrayList<OptionsModel>();
		setStyleName("propertiesTab");
	}

	public void add(IOptionPanel panel) {
		add(panel.getWidget());
		models.add(panel.getModel());
	}

	public OptionsTab addModel(OptionsModel model) {
		models.add(model);
		return this;
	}

	public void addPanelList(List<OptionPanel> list) {
		for (OptionPanel panel: list) {
			add(panel);
		}
	}

	public boolean update(Object[] geos) {
		boolean enabled = false;
		for (OptionsModel panel : models) {
			enabled = panel.updateMPanel(geos) || enabled;
		}

		TabBar tabBar = this.tabPanel.getTabBar();
		tabBar.setTabText(index, getTabText());
		tabBar.setTabEnabled(index, enabled);	
		if (!enabled && tabBar.getSelectedTab() == index) {
			tabBar.selectTab(0);
		}
		return enabled;
	}

	private String getTabText() {
		return loc.getMenu(titleId);
	}

	public void addToTabPanel() {
		this.tabPanel.add(this, getTabText());
		index = this.tabPanel.getWidgetIndex(this);
	}

	public void onResize(int height, int width) {
         this.setHeight(height + "px");
    }

	public void initGUI(App app) {
		if (inited) {
			return;
		}
		inited = true;
		for (OptionsModel m : models) {
			IOptionPanel panel = buildPanel(m, (AppW) app);
			if (panel != null) {
				add(panel.getWidget());
			}
		}

	}

	private IOptionPanel buildPanel(OptionsModel m, AppW app) {
		if (m instanceof ColorObjectModel) {
			return new ColorPanel((ColorObjectModel) m, app);
		}

		if (m instanceof PointSizeModel) {
			return new PointSizePanel((PointSizeModel)m);
		}
		if (m instanceof PointStyleModel) {
			return new PointStylePanel((PointStyleModel)m, app);
		}
		if (m instanceof LineStyleModel) {
			return new LineStylePanel((LineStyleModel)m, app);
		}
		if (m instanceof AngleArcSizeModel) {
			return new AngleArcSizePanel((AngleArcSizeModel)m);
		}
		if (m instanceof SlopeTriangleSizeModel) {
			return new SlopeTriangleSizePanel((SlopeTriangleSizeModel)m);
		}
		if(m instanceof IneqStyleModel){
			return new IneqPanel((IneqStyleModel) m, app);
		}
		if(m instanceof TextFieldSizeModel){
			return new TextFieldSizePanel((TextFieldSizeModel) m, app);
		}
		if(m instanceof ButtonSizeModel){
			return new ButtonSizePanel((ButtonSizeModel)m, app);
		}
		if(m instanceof FillingModel){
			return new FillingPanel((FillingModel) m, app);
		}
		if(m instanceof LodModel){
			return new LodPanel((LodModel) m, app);
		}
		if(m instanceof InterpolateImageModel){
			return new InterpolateImagePanel((InterpolateImageModel) m, app);
		}
		if(m instanceof DecoAngleModel){
			DecoAnglePanel dap =  new DecoAnglePanel((DecoAngleModel)m, app);
			dap.getWidget().setStyleName("optionsPanel");
			return dap;
		}
		if(m instanceof DecoSegmentModel){
			DecoSegmentPanel dsp = new DecoSegmentPanel((DecoSegmentModel)m,app);
			dsp.getWidget().setStyleName("optionsPanel");
			return dsp;
		}
		return null;
	}

	String localize(final String id) {
		// TODO Auto-generated method stub
		String txt = loc.getPlain(id);
		if (txt.equals(id)) {
			txt = loc.getMenu(id);
		}
		return txt;
	}
	public class ColorPanel extends OptionPanel implements IColorObjectListener {
		ColorObjectModel model;
		private FlowPanel mainPanel;
		private ColorChooserW colorChooserW;
		private GColor selectedColor;
		CheckBox sequential;

		public ColorPanel(ColorObjectModel model0, App app) {
			this.model = model0;
			model.setListener(this);
			setModel(model);

			final GDimensionW colorIconSizeW = new GDimensionW(20, 20);

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
					model.clearBackgroundColor();
				}

				@Override
				public void onBackgroundSelected() {
					updatePreview(model.getGeoAt(0).getBackgroundColor(), 1.0f);
				}

				@Override
				public void onForegroundSelected() {
					GeoElement geo0 = model.getGeoAt(0);
					float alpha = 1.0f;
					GColor color = null;
					if (geo0.isFillable()) {
						color = geo0.getFillColor();
						alpha = geo0.getAlphaValue();
					} else {
						color = geo0.getObjectColor();
					}

					updatePreview(color, alpha);
				}
			});
			colorChooserW.setColorPreviewClickable();

			sequential = new CheckBox("Sequential");
			mainPanel = new FlowPanel();
			mainPanel.add(colorChooserW);
			mainPanel.add(sequential);
			sequential.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					// TODO we may need to update the GUI here
					model.setSequential(sequential.getValue());

				}
			});
			setWidget(mainPanel);

		}

		public void applyChanges(boolean alphaOnly) {
			float alpha = colorChooserW.getAlphaValue();
			GColor color = colorChooserW.getSelectedColor();
			model.applyChanges(color, alpha, alphaOnly);
		}

		@Override
		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground, boolean hasOpacity) {
			GColor selectedBGColor = null;
			float alpha = 1;
			GeoElement geo0 = model.getGeoAt(0);
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

		@Override
		public void updatePreview(GColor color, float alpha) {
			colorChooserW.setSelectedColor(color);
			colorChooserW.setAlphaValue(alpha);
			colorChooserW.update();
		}

		@Override
		public boolean isBackgroundColorSelected() {
			return colorChooserW.isBackgroundColorSelected();
		}

		@Override
		public void updateNoBackground(GeoElement geo, GColor col, float alpha,
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
			colorChooserW.setPaletteTitles(localize("RecentColor"),
					localize("Other"));
			colorChooserW.setPreviewTitle(localize("Preview"));
			colorChooserW.setBgFgTitles(localize("BackgroundColor"),
					localize("ForegroundColor"));
			colorChooserW.setOpacityTitle(localize("Opacity"));
		}

	}

	private class DecoAnglePanel extends OptionPanel implements
			IDecoAngleListener {
		private Label decoLabel;
		private PopupMenuButton decoPopup;
		DecoAngleModel model;
		private AppW app;

		public DecoAnglePanel(DecoAngleModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			this.app = app;
			setModel(model);
			FlowPanel mainWidget = new FlowPanel();
			decoLabel = new Label();
			mainWidget.add(decoLabel);
			final ImageOrText[] iconArray = new ImageOrText[DecoAngleModel
					.getDecoTypeLength()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createDecorAngleIcon(i);
			}
			decoPopup = new PopupMenuButton(app, iconArray, -1, 1,
					org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent() {
					super.handlePopupActionEvent();
					int idx = getSelectedIndex();
					model.applyChanges(idx);

				}
			};
			decoPopup.setKeepVisible(false);
			mainWidget.add(decoPopup);
			setWidget(mainWidget);

		}

		@Override
		public void setSelectedIndex(int index) {
			decoPopup.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");

		}

		@Override
		public void setArcSizeMinValue() {
			// angleArcSizePanel.setMinValue(); //TODO update min arc size on
			// deco change
		}

	}

	private class DecoSegmentPanel extends OptionPanel implements
			IComboListener {
		private Label decoLabel;
		private PopupMenuButton decoPopup;
		DecoSegmentModel model;
		private AppW app;

		public DecoSegmentPanel(DecoSegmentModel model0, AppW app) {
			this.app = app;
			model = model0;
			model.setListener(this);
			setModel(model);
			FlowPanel mainWidget = new FlowPanel();
			decoLabel = new Label();
			mainWidget.add(decoLabel);
			final ImageOrText[] iconArray = new ImageOrText[DecoSegmentModel
					.getDecoTypeLength()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createDecorSegmentIcon(i);
			}
			decoPopup = new PopupMenuButton(app, iconArray, -1, 1,
					org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent() {
					super.handlePopupActionEvent();
					int idx = getSelectedIndex();
					model.applyChanges(idx);

				}
			};
			decoPopup.setKeepVisible(false);
			mainWidget.add(decoPopup);
			setWidget(mainWidget);

		}

		@Override
		public void setSelectedIndex(int index) {
			decoPopup.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");

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
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			// slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (true) {// !slider.getValueIsAdjusting()) {
						model.applyChanges(slider.getValue());
					}
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
		private int iconHeight = 24;

		public PointStylePanel(PointStyleModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);
			model.setListener(this);
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsPanel");
			titleLabel = new Label("-");
			mainPanel.add(titleLabel);
			btnPointStyle = PointStylePopup.create(app, iconHeight, -1, false,
					model);
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
			if (btnPointStyle != null)
				btnPointStyle.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

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
		LineStylePopup btnLineStyle;
		private int iconHeight = 24;
		private FlowPanel stylePanel;

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

			thicknessSlider = new SliderPanel(1, GeoElement.MAX_LINE_WIDTH);
			thicknessSlider.setMajorTickSpacing(2);
			thicknessSlider.setMinorTickSpacing(1);
			thicknessSlider.setPaintTicks(true);
			thicknessSlider.setPaintLabels(true);
			// slider.setSnapToTicks(true);
			lineThicknessPanel.add(thicknessSlider);

			thicknessSlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (true) {// !slider.getValueIsAdjusting()) {
						model.applyThickness(thicknessSlider.getValue());
					}
				}
			});
			opacitySliderLabel = new Label();

			FlowPanel lineOpacityPanel = new FlowPanel();
			lineOpacityPanel.setStyleName("optionsPanel");
			lineOpacityPanel.add(opacitySliderLabel);
			mainPanel.add(lineOpacityPanel);

			opacitySlider = new SliderPanel(0, 100);
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);
			// opacitySlider.setSnapToTicks(true);
			lineOpacityPanel.add(opacitySlider);

			opacitySlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (true) {// !slider.getValueIsAdjusting()) {
						int value = (int) ((opacitySlider.getValue() / 100.0f) * 255);
						model.applyOpacity(value);
					}
				}
			});

			stylePanel = new FlowPanel();
			stylePanel.setStyleName("optionsPanel");
			popupLabel = new Label();
			stylePanel.add(popupLabel);
			btnLineStyle = LineStylePopup.create(app, iconHeight, -1, false);
			// slider.setSnapToTicks(true);
			btnLineStyle.addPopupHandler(new PopupMenuHandler() {

				@Override
				public void fireActionPerformed(PopupMenuButton actionButton) {
					model.applyLineTypeFromIndex(btnLineStyle
							.getSelectedIndex());

				}
			});
			btnLineStyle.setKeepVisible(false);
			mainPanel.add(btnLineStyle);

			stylePanel.add(btnLineStyle);
			mainPanel.add(stylePanel);

			setWidget(mainPanel);
		}

		@Override
		public void setLabels() {
			thicknessSliderLabel.setText(localize("Thickness"));
			opacitySliderLabel.setText(localize("LineOpacity"));
			popupLabel.setText(localize("LineStyle") + ":");

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
			if (true) {
				btnLineStyle.selectLineType(type);

			}
			// else {
			// btnLineStyle.setSelectedIndex(-1);
			// }
		}

		@Override
		public void setLineTypeVisible(boolean value) {
			stylePanel.setVisible(value);
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
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
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

		public void setMinValue() {
			slider.setValue(AngleArcSizeModel.MIN_VALUE);
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
			slider.setMajorTickSpacing(1);
			slider.setMinorTickSpacing(2);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
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

	private class IneqPanel extends CheckboxPanel implements IIneqStyleListener {

		public IneqPanel(IneqStyleModel model, AppW app) {
			super("ShowOnXAxis", app.getLocalization());
			model.setListener(this);
			setModel(model);
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

	} // IneqPanel

	private class TextFieldSizePanel extends OptionPanel implements
			ITextFieldListener {

		TextFieldSizeModel model;
		private InputPanelW inputPanel;
		AutoCompleteTextFieldW tfSize;

		public TextFieldSizePanel(TextFieldSizeModel model0, AppW app) {
			model = model0;
			model.setListener(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();

			inputPanel = new InputPanelW(null, app, 1, -1, false);
			tfSize = inputPanel.getTextComponent();
			tfSize.setAutoComplete(false);
			tfSize.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
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
			mainPanel.add(inputPanel);
			mainPanel.setStyleName("optionsPanel");
			setWidget(mainPanel);

		}

		@Override
		public void setText(String text) {
			tfSize.setText(text);
		}

		@Override
		public void setLabels() {
			// title.setText(localize("TextfieldLength"));
		}

	}

	public class ButtonSizePanel extends OptionPanel implements
			IButtonSizeListener {
		private InputPanelW ipButtonWidth;
		private InputPanelW ipButtonHeight;
		AutoCompleteTextFieldW tfButtonWidth;
		AutoCompleteTextFieldW tfButtonHeight;
		CheckBox cbUseFixedSize;

		private Label labelWidth;
		private Label labelHeight;
		private Label labelPixelW;
		private Label labelPixelH;
		ButtonSizeModel model;
		private AppW app;

		public ButtonSizePanel(ButtonSizeModel model0, AppW app) {
			model = model0;
			this.app = app;
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

			FocusListenerW focusListener = new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					model.setSizesFromString(tfButtonWidth.getText(),
							tfButtonHeight.getText(), cbUseFixedSize.getValue());

				}
			};

			tfButtonWidth.addFocusListener(focusListener);
			tfButtonHeight.addFocusListener(focusListener);

			KeyHandler keyHandler = new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.setSizesFromString(tfButtonWidth.getText(),
								tfButtonHeight.getText(),
								cbUseFixedSize.getValue());
					}
				}

			};

			tfButtonWidth.addKeyHandler(keyHandler);
			tfButtonHeight.addKeyHandler(keyHandler);

			cbUseFixedSize.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyChanges(cbUseFixedSize.getValue());

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
			labelWidth.setText(loc.getPlain("Width"));
			labelHeight.setText(loc.getPlain("Height"));
			labelPixelW.setText(loc.getMenu("Pixels.short"));
			labelPixelH.setText(loc.getMenu("Pixels.short"));
			cbUseFixedSize.setText(loc.getPlain("fixed"));

		}

	}

	private class InterpolateImagePanel extends CheckboxPanel {

		public InterpolateImagePanel(InterpolateImageModel model, AppW app) {
			super("Interpolate", app.getLocalization());
			model.setListener(this);
			setModel(model);
		}

	}

	class LodPanel extends OptionPanel implements IComboListener {
		LodModel model;
		private FlowPanel mainWidget;
		private Label label;
		ListBox combo;
		private App app;

		public LodPanel(LodModel model0, App app) {
			model = model0;
			this.app = app;
			model.setListener(this);
			setModel(model);

			mainWidget = new FlowPanel();

			label = new Label();
			mainWidget.add(label);

			combo = new ListBox();

			combo.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(combo.getSelectedIndex());
				}
			});

			mainWidget.add(combo);

			setWidget(mainWidget);
		}

		@Override
		public void setLabels() {
			label.setText(app.getPlain("LevelOfDetail"));

			int idx = combo.getSelectedIndex();
			combo.clear();
			model.fillModes(loc);
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
		public void setSelectedItem(String item) {
			// nothing to do here

		}

	}

}