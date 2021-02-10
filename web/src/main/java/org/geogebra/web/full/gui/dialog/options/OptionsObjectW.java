package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.options.OptionsObject;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimatingModel;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.AuxObjectModel;
import org.geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.CenterImageModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.DrawArrowsModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.GroupModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LayerModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import org.geogebra.common.gui.dialog.options.model.PlaneEqnModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import org.geogebra.common.gui.dialog.options.model.RightAngleModel;
import org.geogebra.common.gui.dialog.options.model.SelectionAllowedModel;
import org.geogebra.common.gui.dialog.options.model.ShowConditionModel;
import org.geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import org.geogebra.common.gui.dialog.options.model.ShowObjectModel;
import org.geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import org.geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import org.geogebra.common.gui.dialog.options.model.StartPointModel;
import org.geogebra.common.gui.dialog.options.model.SymbolicModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldAlignmentModel;
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TraceModel;
import org.geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.ViewLocationModel.IGraphicsViewLocationListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.properties.GroupOptionsPanel;
import org.geogebra.web.full.gui.properties.ListBoxPanel;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.properties.SliderPanelW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("javadoc")
public class OptionsObjectW extends OptionsObject implements OptionPanelW {
	Localization loc;

	MultiRowsTabPanel tabPanel;

	private FlowPanel wrappedPanel;
	private OptionsTab basicTab;

	// Basic
	LabelPanel labelPanel;
	AppW app;

	// Color picker

	// Style
	// FillingPanel fillingPanel;

	// Advanced
	final boolean isDefaults;

	List<OptionsTab> tabs;
	private TextOptionsModel textModel;

	String localize(final String id) {
		return loc.getMenu(id);
	}

	private class ShowObjectPanel extends CheckboxPanel
			implements IShowObjectListener {
		public ShowObjectPanel() {
			super("ShowObject", loc);
			setModel(new ShowObjectModel(this, app));
		}

		@Override
		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setValue(value);
			getCheckbox().setEnabled(isEnabled);
		}
	}

	private class LabelPanel extends OptionPanel implements IShowLabelListener {
		final CheckBox showLabelCB;
		private final FlowPanel mainWidget;
		final ListBox labelMode;
		ShowLabelModel model;

		public LabelPanel() {
			mainWidget = new FlowPanel();
			showLabelCB = new CheckBox(localize("ShowLabel") + ":");
			mainWidget.add(showLabelCB);
			mainWidget.setStyleName("checkBoxPanel");
			setWidget(mainWidget);

			model = new ShowLabelModel(app, this);
			setModel(model);

			updateShowLabel();

			labelMode = new ListBox();
			labelMode.setMultipleSelect(false);

			showLabelCB.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					model.applyShowChanges(showLabelCB.getValue());
				}
			});

			labelMode.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyModeChanges(
							model.fromDropdown(labelMode.getSelectedIndex()),
							true);
				}

			});
			mainWidget.add(labelMode);
		}

		private void updateShowLabel() {
			if (!model.isNameValueShown()) {
				showLabelCB.setText(localize("ShowLabel"));
			} else {
				showLabelCB.setText(localize("ShowLabel") + ":");
			}
		}

		@Override
		public void update(boolean isEqualVal, boolean isEqualMode, int mode) {
			// change "Show Label:" to "Show Label" if there's no menu
			updateShowLabel();

			GeoElement geo0 = model.getGeoAt(0);
			// set label visible checkbox
			if (isEqualVal) {
				showLabelCB.setValue(geo0.isLabelVisible());
			} else {
				showLabelCB.setValue(false);
			}

			// set label visible checkbox
			if (isEqualMode) {
				labelMode.setSelectedIndex(
						ShowLabelModel.getDropdownIndex(geo0));
			} else {
				labelMode.setSelectedIndex(-1);
			}

			// locus in selection
			labelMode.setVisible(model.isNameValueShown());
		}

		@Override
		public void setLabels() {
			updateShowLabel();
			int selectedIndex = labelMode.getSelectedIndex();
			labelMode.clear();
			labelMode.addItem(localize("Name")); // index 0
			labelMode.addItem(localize("NameAndValue")); // index 1
			labelMode.addItem(localize("Value")); // index 2
			labelMode.addItem(localize("Caption")); // index 3
			labelMode.addItem(localize("CaptionAndValue")); // index 4
			labelMode.setSelectedIndex(selectedIndex);
		}
	}

	private class ShowConditionPanel extends OptionPanel
			implements IShowConditionListener, ErrorHandler, Command {
		private ShowConditionModel model;
		private FormLabel title;
		private AutoCompleteTextFieldW tfCondition;

		private FlowPanel errorPanel;

		public ShowConditionPanel() {
			// this.propPanel = propPanel;
			model = new ShowConditionModel(app, this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsInput");
			// non auto complete input panel
			InputPanelW inputPanel = new InputPanelW(null, getAppW(), 1, -1,
					true);
			tfCondition = inputPanel.getTextComponent();

			title = new FormLabel().setFor(tfCondition);
			title.setStyleName("panelTitle");
			mainPanel.add(title);
			SingleActionProcessor processor = new SingleActionProcessor(this);
			processor.handleEvents(tfCondition);
			// put it all together
			mainPanel.add(inputPanel);
			errorPanel = new FlowPanel();
			errorPanel.addStyleName("Dialog-errorPanel");
			mainPanel.add(errorPanel);
			setWidget(mainPanel);
		}

		@Override
		public void resetError() {
			showError(null);
		}

		@Override
		public void showError(String msg) {
			if (msg == null) {
				return;
			}
			errorPanel.clear();
			String[] lines = msg.split("\n");
			for (String item : lines) {
				errorPanel.add(new Label(item));
			}
		}

		@Override
		public void showCommandError(String command, String message) {
			app.getDefaultErrorHandler().showCommandError(command, message);
		}

		@Override
		public String getCurrentCommand() {
			return tfCondition.getCommand();
		}

		@Override
		public void execute() {
			errorPanel.clear();
			model.applyChanges(tfCondition.getText(), this);
		}

		@Override
		public void setText(String text) {
			tfCondition.setText(text);
		}

		@Override
		public void setLabels() {
			title.setText(loc.getMenu("Condition.ShowObject"));
		}

		@Override
		public void updateSelection(Object[] geos) {
			// do nothing
		}

		@Override
		public boolean onUndefinedVariables(String string,
				AsyncOperation<String[]> callback) {
			return app.getGuiManager().checkAutoCreateSliders(string, callback);
		}
	}

	class ListAsComboPanel extends CheckboxPanel
			implements IListAsComboListener {
		public ListAsComboPanel() {
			super("DrawAsDropDownList", loc);
			setModel(new ListAsComboModel(app, this));
		}

		@Override
		public void drawListAsComboBox(GeoList geo, boolean value) {
			if (geo.getViewSet() == null) {
				app.getEuclidianView1().drawListAsComboBox(geo, value);
				return;
			}

			Iterator<Integer> it = geo.getViewSet().iterator();

			// #3929
			while (it.hasNext()) {
				Integer view = it.next();
				if (view.intValue() == App.VIEW_EUCLIDIAN) {
					app.getEuclidianView1().drawListAsComboBox(geo, value);
				} else if (view.intValue() == App.VIEW_EUCLIDIAN2
						&& app.hasEuclidianView2(1)) {
					app.getEuclidianView2(1).drawListAsComboBox(geo, value);
				}
			}
		}
	}

	class ReflexAnglePanel extends OptionPanel implements IReflexAngleListener {
		ReflexAngleModel model;
		private FlowPanel mainWidget;
		private FormLabel intervalLabel;
		private ListBox intervalLB;

		public ReflexAnglePanel() {
			model = new ReflexAngleModel(app, isDefaults);
			model.setListener(this);
			setModel(model);

			mainWidget = new FlowPanel();
			intervalLB = new ListBox();
			intervalLabel = new FormLabel("").setFor(intervalLB);
			mainWidget.add(intervalLabel);

			intervalLB.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(getIndex());
				}
			});

			mainWidget.add(intervalLB);
			setWidget(mainWidget);
		}

		@Override
		public void setLabels() {
			intervalLabel.setText(localize("AngleBetween"));
			setComboLabels();
		}

		@Override
		public void setComboLabels() {
			int idx = intervalLB.getSelectedIndex();
			intervalLB.clear();
			model.fillModes(loc);
			intervalLB.setSelectedIndex(idx);
		}

		int getIndex() {
			if (model.hasOrientation()) {
				return intervalLB.getSelectedIndex();
			}
			// first interval disabled
			return intervalLB.getSelectedIndex() + 1;
		}

		@Override
		public void addItem(String item) {
			intervalLB.addItem(item);
		}

		@Override
		public void setSelectedIndex(int index) {
			if (model.hasOrientation()) {

				if (index >= intervalLB.getItemCount()) {
					intervalLB.setSelectedIndex(0);
				} else {
					intervalLB.setSelectedIndex(index);
				}
			} else {
				// first interval disabled
				intervalLB.setSelectedIndex(index - 1);
			}
		}

		@Override
		public void clearItems() {
			// do nothing
		}
	}

	private class ColorFunctionPanel extends OptionPanel
			implements IColorFunctionListener, Command {

		ColorFunctionModel model;
		private InputPanelW inputPanelA;
		private AutoCompleteTextFieldW tfRed;
		private AutoCompleteTextFieldW tfGreen;
		private AutoCompleteTextFieldW tfBlue;
		private AutoCompleteTextFieldW tfAlpha;
		private Label btRemove;
		private Label title;
		private FormLabel nameLabelR;
		private FormLabel nameLabelG;
		private FormLabel nameLabelB;
		private FormLabel nameLabelA;

		ListBox cbColorSpace;
		int colorSpace = GeoElement.COLORSPACE_RGB;
		boolean allowSetComboBoxLabels = true;

		private String defaultR = "0";
		private String defaultG = "0";
		private String defaultB = "0";
		private String defaultA = "1";

		public ColorFunctionPanel() {
			model = new ColorFunctionModel(app, this);
			setModel(model);
			// non auto complete input panel
			InputPanelW inputPanelR = new InputPanelW(null, getAppW(), 1, -1,
					true);
			InputPanelW inputPanelG = new InputPanelW(null, getAppW(), 1, -1,
					true);
			InputPanelW inputPanelB = new InputPanelW(null, getAppW(), 1, -1,
					true);
			inputPanelA = new InputPanelW(null, getAppW(), 1, -1, true);
			tfRed = inputPanelR.getTextComponent();
			tfGreen = inputPanelG.getTextComponent();
			tfBlue = inputPanelB.getTextComponent();
			tfAlpha = inputPanelA.getTextComponent();

			nameLabelR = new FormLabel().setFor(tfRed);
			nameLabelG = new FormLabel().setFor(tfGreen);
			nameLabelB = new FormLabel().setFor(tfBlue);
			nameLabelA = new FormLabel().setFor(tfAlpha);
			SingleActionProcessor processor = new SingleActionProcessor(this);
			processor.handleEvents(tfRed);
			processor.handleEvents(tfGreen);
			processor.handleEvents(tfBlue);
			processor.handleEvents(tfAlpha);

			btRemove = new Label();
			btRemove.addStyleName("textButton");
			btRemove.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.removeAll();
				}
			});

			cbColorSpace = new ListBox();
			cbColorSpace.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					colorSpace = cbColorSpace.getSelectedIndex();
					allowSetComboBoxLabels = false;
					setLabels();
					execute();
					cbColorSpace.setSelectedIndex(colorSpace);
				}
			});

			FlowPanel redColorPanel = new FlowPanel();
			FlowPanel greenColorPanel = new FlowPanel();
			FlowPanel blueColorPanel = new FlowPanel();
			FlowPanel alphaColorPanel = new FlowPanel();
			redColorPanel.setStyleName("optionsPanelCell");
			greenColorPanel.setStyleName("optionsPanelCell");
			blueColorPanel.setStyleName("optionsPanelCell");
			alphaColorPanel.setStyleName("optionsPanelCell");

			redColorPanel.add(nameLabelR);
			redColorPanel.add(inputPanelR);
			greenColorPanel.add(nameLabelG);
			greenColorPanel.add(inputPanelG);
			blueColorPanel.add(nameLabelB);
			blueColorPanel.add(inputPanelB);
			alphaColorPanel.add(nameLabelA);
			alphaColorPanel.add(inputPanelA);

			FlowPanel colorsPanel = new FlowPanel();
			colorsPanel.setStyleName("optionsPanelIndent");
			colorsPanel.add(redColorPanel);
			colorsPanel.add(greenColorPanel);
			colorsPanel.add(blueColorPanel);
			colorsPanel.add(alphaColorPanel);

			FlowPanel mainWidget = new FlowPanel();
			title = new Label();
			title.setStyleName("panelTitle");

			mainWidget.add(title);

			mainWidget.add(colorsPanel);

			FlowPanel buttonsPanel = new FlowPanel();
			buttonsPanel.setStyleName("optionsPanelIndent");

			FlowPanel leftPanel = new FlowPanel();
			leftPanel.add(cbColorSpace);
			FlowPanel rightPanel = new FlowPanel();
			rightPanel.add(btRemove);
			buttonsPanel.add(leftPanel);
			buttonsPanel.add(rightPanel);

			mainWidget.add(buttonsPanel);

			setWidget(mainWidget);
		}

		@Override
		public void setLabels() {
			title.setText(loc.getMenu("DynamicColors"));
			// tfRed.setVisible(false);
			if (allowSetComboBoxLabels) {
				cbColorSpace.clear();
				cbColorSpace.addItem(loc.getMenu("RGB"));
				cbColorSpace.addItem(loc.getMenu("HSV"));
				cbColorSpace.addItem(loc.getMenu("HSL"));
			}
			allowSetComboBoxLabels = true;

			switch (colorSpace) {
			default:
			case GeoElement.COLORSPACE_RGB:
				nameLabelR.setText(
						StringUtil.capitalize(loc.getColor("red") + ":"));
				nameLabelG.setText(
						StringUtil.capitalize(loc.getColor("green") + ":"));
				nameLabelB.setText(
						StringUtil.capitalize(loc.getColor("blue") + ":"));
				break;
			case GeoElement.COLORSPACE_HSB:
				nameLabelR.setText(loc.getMenu("Hue") + ":");
				nameLabelG.setText(loc.getMenu("Saturation") + ":");
				nameLabelB.setText(loc.getMenu("Value") + ":");
				break;
			case GeoElement.COLORSPACE_HSL:
				nameLabelR.setText(loc.getMenu("Hue") + ":");
				nameLabelG.setText(loc.getMenu("Saturation") + ":");
				nameLabelB.setText(loc.getMenu("Lightness") + ":");
				break;
			}

			nameLabelA.setText(loc.getMenu("Opacity") + ":");
			btRemove.setText(loc.getPlainTooltip("Remove"));
			// btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
		}

		@Override
		public void execute() {
			String strRed = tfRed.getText();
			String strGreen = tfGreen.getText();
			String strBlue = tfBlue.getText();
			String strAlpha = tfAlpha.getText();

			model.applyChanges(strRed, strGreen, strBlue, strAlpha, colorSpace,
					defaultR, defaultG, defaultB, defaultA);
		}

		@Override
		public void setRedText(final String text) {
			tfRed.setText(text);
		}

		@Override
		public void setGreenText(final String text) {
			tfGreen.setText(text);
		}

		@Override
		public void setBlueText(final String text) {
			tfBlue.setText(text);
		}

		@Override
		public void setAlphaText(final String text) {
			tfAlpha.setText(text);
		}

		@Override
		public void setDefaultValues(GeoElement geo) {
			Kernel kernel = geo.getKernel();
			GColor col = geo.getObjectColor();
			defaultR = kernel.format(col.getRed() / 255.0,
					StringTemplate.editTemplate);
			defaultG = kernel.format(col.getGreen() / 255.0,
					StringTemplate.editTemplate);
			defaultB = kernel.format(col.getBlue() / 255.0,
					StringTemplate.editTemplate);
			defaultA = kernel.format(geo.getFillColor().getAlpha() / 255.0,
					StringTemplate.editTemplate);

			// set the selected color space and labels to match the first geo's
			// color space
			colorSpace = geo.getColorSpace();
			cbColorSpace.setSelectedIndex(colorSpace);
			allowSetComboBoxLabels = false;
			setLabels();
		}

		@Override
		public void showAlpha(boolean value) {
			inputPanelA.setVisible(value);
			nameLabelA.setVisible(value);
		}

		@Override
		public void updateSelection(Object[] geos) {
			// updateSelection(geos);
		}
	}

	private class LayerPanel extends ListBoxPanel {

		public LayerPanel() {
			super(loc, "Layer");
			LayerModel model = new LayerModel(app);
			model.setListener(this);
			setModel(model);
		}
	}

	private class ViewLocationPanel extends OptionPanel
			implements IGraphicsViewLocationListener {
		ViewLocationModel model;

		private Label title;
		CheckBox cbGraphicsView;
		CheckBox cbGraphicsView2;
		CheckBox cbGraphicsView3D;
		CheckBox cbGraphicsViewForPlane;
		CheckBox cbAlgebraView;

		public ViewLocationPanel() {
			model = new ViewLocationModel(app, this);
			setModel(model);

			title = new Label();
			cbGraphicsView = new CheckBox();
			cbGraphicsView2 = new CheckBox();
			cbGraphicsView3D = new CheckBox();
			cbGraphicsViewForPlane = new CheckBox();
			cbGraphicsView.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView1(cbGraphicsView.getValue());
				}
			});

			cbGraphicsView2.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView2(cbGraphicsView2.getValue());
				}
			});

			cbGraphicsView3D.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView3D(cbGraphicsView3D.getValue());
				}
			});

			cbGraphicsViewForPlane.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianViewForPlane(
							cbGraphicsViewForPlane.getValue());
				}
			});

			cbAlgebraView = new CheckBox();
			cbAlgebraView.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToAlgebraView(cbAlgebraView.getValue());
				}
			});

			FlowPanel mainPanel = new FlowPanel();
			FlowPanel checkBoxPanel = new FlowPanel();
			checkBoxPanel.setStyleName("optionsPanelIndent");
			checkBoxPanel.add(cbGraphicsView);
			checkBoxPanel.add(cbGraphicsView2);
			checkBoxPanel.add(cbGraphicsView3D);
			checkBoxPanel.add(cbGraphicsViewForPlane);
			checkBoxPanel.add(cbAlgebraView);

			mainPanel.add(title);
			title.setStyleName("panelTitle");
			mainPanel.add(checkBoxPanel);
			setWidget(mainPanel);
		}

		@Override
		public void selectView(int index, boolean isSelected) {
			switch (index) {
			case 0:
				cbGraphicsView.setValue(isSelected);
				break;
			case 1:
				cbGraphicsView2.setValue(isSelected);
				break;
			case 2:
				cbGraphicsView3D.setValue(isSelected);
				break;
			case 3:
				cbGraphicsViewForPlane.setValue(isSelected);
				break;
			case 4:
				cbAlgebraView.setValue(isSelected);
				break;
			default:
				// do nothing
				break;
			}
		}

		@Override
		public void setLabels() {
			title.setText(loc.getMenu("Location"));
			cbGraphicsView.setText(localize("DrawingPad"));
			cbGraphicsView2.setText(localize("DrawingPad2"));
			cbGraphicsView3D.setText(localize("GraphicsView3D"));
			cbGraphicsViewForPlane.setText(localize("ExtraViews"));
			cbAlgebraView.setText(localize("Algebra"));
		}

		@Override
		public void setCheckBox3DVisible(boolean flag) {
			cbGraphicsView3D.setVisible(flag);
		}

		@Override
		public void setCheckBoxForPlaneVisible(boolean flag) {
			cbGraphicsViewForPlane.setVisible(flag);
		}
	}

	/**
	 * 
	 * @param app
	 *            app
	 * @param isDefaults
	 *            whether it's for defaults
	 * @param onTabSelection
	 *            tab selection callback
	 */
	public OptionsObjectW(AppW app, boolean isDefaults,
			Runnable onTabSelection) {
		this.app = app;
		this.isDefaults = isDefaults;
		loc = app.getLocalization();
		// build GUI
		initPermissions();
		initGUI(onTabSelection);
	}

	private void initPermissions() {
		if (app.getLoginOperation() != null
				&& app.getAppletParameters().getDataParamApp()) {
			updateJsEnabled();
			app.getLoginOperation().getView().add(event -> {
				updateJsEnabled();
				updateGUI();
			});
		}
	}

	private void updateJsEnabled() {
		app.getScriptManager().setJsEnabled(!app.isMebis()
				|| app.getLoginOperation().isTeacherLoggedIn());

	}

	AppW getAppW() {
		return app;
	}

	private void initGUI(final Runnable onTabSelection) {
		wrappedPanel = new FlowPanel();
		wrappedPanel.setStyleName("propertiesPanel");
		tabPanel = new MultiRowsTabPanel();

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				// updateGUI();
				for (OptionsTab tab : tabs) {
					tab.setFocused(false);
				}
				tabs.get(event.getSelectedItem()).initGUI(isDefaults);
				onTabSelection.run();
			}
		});
		((Widget) tabPanel).setStyleName("propertiesPanel");
		createBasicTab();
		if (!(app.isExam())) {
			tabs = Arrays.asList(basicTab, addTextTab(), addSliderTab(),
					addColorTab(), addStyleTab(), addPositionTab(),
					addAdvancedTab(), addAlgebraTab(), addScriptTab());
		} else {
			// skip scripting in exam
			tabs = Arrays.asList(basicTab, addTextTab(), addSliderTab(),
					addColorTab(), addStyleTab(), addPositionTab(),
					addAdvancedTab(), addAlgebraTab());
		}

		for (OptionsTab tab : tabs) {
			tab.addToTabPanel();
		}

		wrappedPanel.add(tabPanel);
		wrappedPanel.addAttachHandler(new AttachEvent.Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				app.setDefaultCursor();
				reinit(); // re-attach the text editor
			}
		});
		wrappedPanel.setVisible(false);
		selectTab(0);
	}

	private void createBasicTab() {
		basicTab = makeOptionsTab("Properties.Basic");
		CheckboxPanel animatingPanel = null;
		CheckboxPanel bgImagePanel = null;
		ReflexAnglePanel reflexAnglePanel = null;
		labelPanel = new LabelPanel();
		NamePanel namePanel = new NamePanel(getAppW(), labelPanel.model);
		if (!isDefaults) {
			basicTab.add(namePanel);
		}

		FlowPanel checkboxPanel = new FlowPanel();
		basicTab.add(checkboxPanel);

		CheckboxPanel showObjectPanel = new ShowObjectPanel();
		checkboxPanel.add(showObjectPanel.getWidget());

		if (!isDefaults) {
			checkboxPanel.add(labelPanel.getWidget());
		}

		CheckboxPanel tracePanel = new CheckboxPanel("ShowTrace", loc,
				new TraceModel(null, app));
		checkboxPanel.add(tracePanel.getWidget());
		basicTab.add(checkboxPanel);

		if (!isDefaults) {
			animatingPanel = new CheckboxPanel("Animating", loc,
					new AnimatingModel(app, null));
			checkboxPanel.add(animatingPanel.getWidget());
		}

		CheckboxPanel fixPanel = new CheckboxPanel("FixObject", loc,
				new FixObjectModel(null, app));
		checkboxPanel.add(fixPanel.getWidget());

		CheckboxPanel auxPanel = new CheckboxPanel("AuxiliaryObject", loc,
				new AuxObjectModel(null, app));
		checkboxPanel.add(auxPanel.getWidget());

		if (!isDefaults) {
			bgImagePanel = new CheckboxPanel("BackgroundImage", loc,
					new BackgroundImageModel(null, app));
			checkboxPanel.add(bgImagePanel.getWidget());
		}
		basicTab.add(checkboxPanel);

		if (!isDefaults) {
			reflexAnglePanel = new ReflexAnglePanel();
			reflexAnglePanel.getWidget().setStyleName("optionsPanel");
		}

		CheckboxPanel listAsComboPanel = new ListAsComboPanel();
		CheckboxPanel rightAnglePanel = new CheckboxPanel("EmphasizeRightAngle",
				loc,
				new RightAngleModel(null, app));
		CheckboxPanel trimmedIntersectionLinesPanel = new CheckboxPanel(
				"ShowTrimmed", loc,
				new TrimmedIntersectionLinesModel(null, app));

		// tabList.add(comboBoxPanel);
		CheckboxPanel allowOutlyingIntersectionsPanel = new CheckboxPanel(
				"allowOutlyingIntersections", loc,
				new OutlyingIntersectionsModel(null, app));
		basicTab.add(allowOutlyingIntersectionsPanel.getWidget());

		CheckboxPanel fixCheckboxPanel = new CheckboxPanel("FixCheckbox", loc,
				new FixCheckboxModel(null, app));
		basicTab.add(fixCheckboxPanel.getWidget());

		basicTab.addPanelList(Arrays.asList(namePanel, showObjectPanel,
				tracePanel, labelPanel, fixPanel, auxPanel, animatingPanel,
				bgImagePanel, reflexAnglePanel, rightAnglePanel,
				listAsComboPanel, trimmedIntersectionLinesPanel,
				allowOutlyingIntersectionsPanel, fixCheckboxPanel));
	}

	private OptionsTab addTextTab() {
		OptionsTab tab = makeOptionsTab("Text");
		this.textModel = new TextOptionsModel(app);
		tab.addModel(textModel);
		return tab;
	}

	private OptionsTab addSliderTab() {
		OptionsTab tab = makeOptionsTab("Slider");
		SliderPanelW sliderPanel = new SliderPanelW(getAppW(), false, true);
		tab.add(sliderPanel);
		return tab;
	}

	private OptionsTab addColorTab() {
		OptionsTab tab = makeOptionsTab("Color");
		tab.addModel(new ColorObjectModel(app));
		return tab;
	}

	private OptionsTab addStyleTab() {
		OptionsTab tab = makeOptionsTab("Properties.Style");
		PointSizeModel ptSize = new PointSizeModel(app);
		PointStyleModel ptStyle = new PointStyleModel(app);
		LineStyleModel lineStyle = new LineStyleModel(app);
		DrawArrowsModel drawArrows = new DrawArrowsModel(null, app);
		AngleArcSizeModel arcSize = new AngleArcSizeModel(app);
		SlopeTriangleSizeModel slopeSize = new SlopeTriangleSizeModel(app);
		IneqStyleModel ineqStyle = new IneqStyleModel(app);
		TextFieldSizeModel tfSize = new TextFieldSizeModel(app);
		TextFieldAlignmentModel alignModel = new TextFieldAlignmentModel(app);
		ButtonSizeModel buttonSize = new ButtonSizeModel(app);
		FillingModel filling = new FillingModel(app);
		LodModel lod = new LodModel(app, isDefaults);
		InterpolateImageModel interpol = new InterpolateImageModel(app);
		DecoAngleModel decoAngle = new DecoAngleModel(app);
		DecoSegmentModel decoSegment = new DecoSegmentModel(app);

		tab.addModel(ptSize).addModel(ptStyle).addModel(lod).addModel(lineStyle)
				.addModel(drawArrows).addModel(arcSize).addModel(slopeSize).addModel(ineqStyle)
				.addModel(tfSize).addModel(alignModel).addModel(buttonSize)
				.addModel(filling).addModel(interpol).addModel(decoAngle)
				.addModel(decoSegment);
		return tab;
	}

	private OptionsTab addScriptTab() {
		OptionsTab tab = makeOptionsTab("Scripting");
		final ScriptEditorModel model = new ScriptEditorModel(app);
		tab.addModel(model);
		return tab;
	}

	private OptionsTab addAdvancedTab() {
		OptionsTab tab = makeOptionsTab("Advanced");
		ShowConditionPanel showConditionPanel = new ShowConditionPanel();
		ColorFunctionPanel colorFunctionPanel = new ColorFunctionPanel();
		LayerPanel layerPanel = new LayerPanel();
		CheckboxPanel selectionAllowedPanel = new CheckboxPanel(
				"SelectionAllowed", loc, new SelectionAllowedModel(null, app));

		tab.add(showConditionPanel);
		tab.add(colorFunctionPanel);
		GroupModel group = new GroupModel(app);
		group.add(layerPanel.getModel());
		group.add(selectionAllowedPanel.getModel());
		GroupOptionsPanel misc = new GroupOptionsPanel("Miscellaneous", loc,
				group);
		misc.add(layerPanel);
		misc.add(selectionAllowedPanel);
		tab.add(misc);
		ViewLocationPanel graphicsViewLocationPanel = new ViewLocationPanel();
		tab.add(graphicsViewLocationPanel);
		return tab;
	}

	private OptionsTab addAlgebraTab() {
		OptionsTab tab;
		tab = makeOptionsTab("Properties.Algebra");
		if (app.getConfig().isCoordinatesObjectSettingEnabled()) {
			tab.addModel(new CoordsModel(app));
		}
		tab.addModel(new LineEqnModel(app));
		tab.addModel(new PlaneEqnModel(app));
		tab.addModel(new SymbolicModel(app));
		tab.addModel(new ConicEqnModel(app));
		tab.addModel(new AnimationSpeedModel(getAppW()));
		tab.addModel(new AnimationStepModel(getAppW()));
		return tab;
	}

	private OptionsTab addPositionTab() {
		OptionsTab tab;
		tab = makeOptionsTab("Properties.Position");
		tab.addModel(new StartPointModel(app))
				.addModel(new CornerPointsModel(app))
				.addModel(new AbsoluteScreenLocationModel(app))
				.addModel(new CenterImageModel(app));
		return tab;
	}

	private OptionsTab makeOptionsTab(String id) {
		return new OptionsTab(app, this.loc, this.tabPanel, id);
	}

	protected void reinit() {
		textModel.reinitEditor();
		updateGUI();
	}

	@Override
	public void updateGUI() {
		loc = app.getLocalization();
		update(app.getSelectionManager().getSelectedGeos());
	}

	private void update(ArrayList<GeoElement> list) {
		Object[] geos = list.toArray();

		if (geos != null && geos.length != 0) {
			wrappedPanel.setVisible(true);
			for (OptionsTab tab : tabs) {
				tab.update(geos);
			}
		} else {
			wrappedPanel.setVisible(false);
		}
	}

	@Override
	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	public void selectTab(int index) {
		tabPanel.selectTab(index < 0 ? 0 : index);
	}

	public void openFileAsImage(String fileName) {
		Log.debug(fileName);
	}

	/**
	 * Update if given element is selected.
	 * 
	 * @param geo
	 *            element
	 */
	public void updateIfInSelection(GeoElement geo) {
		if (getSelection() != null && getSelection().size() == 1
				&& getSelection().contains(geo)) {
			updateGUI();
		}
	}

	@Override
	public void onResize(int height, int width) {
		for (OptionsTab tab : tabs) {
			tab.onResize(height, width);
		}
	}

	/**
	 * Update selection.
	 * 
	 * @param geos
	 *            selected geos
	 */
	public void updateSelection(ArrayList<GeoElement> geos) {
		setSelection(geos);
		update(geos);
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return tabPanel;
	}
}