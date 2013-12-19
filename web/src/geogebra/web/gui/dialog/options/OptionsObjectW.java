
package geogebra.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import geogebra.common.gui.dialog.options.model.AnimatingModel;
import geogebra.common.gui.dialog.options.model.AuxObjectModel;
import geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import geogebra.common.gui.dialog.options.model.ColorObjectModel;
import geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import geogebra.common.gui.dialog.options.model.ConicEqnModel;
import geogebra.common.gui.dialog.options.model.CoordsModel;
import geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import geogebra.common.gui.dialog.options.model.FixObjectModel;
import geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel;
import geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel.IGraphicsViewLocationListener;
import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.ISliderListener;
import geogebra.common.gui.dialog.options.model.ITextFieldListener;
import geogebra.common.gui.dialog.options.model.ImageCornerModel;
import geogebra.common.gui.dialog.options.model.IneqStyleModel;
import geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import geogebra.common.gui.dialog.options.model.LayerModel;
import geogebra.common.gui.dialog.options.model.LineEqnModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import geogebra.common.gui.dialog.options.model.ListAsComboModel;
import geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import geogebra.common.gui.dialog.options.model.ObjectNameModel;
import geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import geogebra.common.gui.dialog.options.model.PointSizeModel;
import geogebra.common.gui.dialog.options.model.PointStyleModel;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import geogebra.common.gui.dialog.options.model.RightAngleModel;
import geogebra.common.gui.dialog.options.model.SelectionAllowedModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import geogebra.common.gui.dialog.options.model.ShowLabelModel;
import geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import geogebra.common.gui.dialog.options.model.ShowObjectModel;
import geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import geogebra.common.gui.dialog.options.model.StartPointModel;
import geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import geogebra.common.gui.dialog.options.model.TextOptionsModel;
import geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import geogebra.common.gui.dialog.options.model.TooltipModel;
import geogebra.common.gui.dialog.options.model.TraceModel;
import geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import geogebra.common.gui.inputfield.DynamicTextElement;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.inputfield.GeoTextEditor;
import geogebra.html5.gui.inputfield.ITextEditPanel;
import geogebra.html5.gui.inputfield.TextEditAdvancedPanel;
import geogebra.html5.gui.inputfield.TextPreviewPanelW;
import geogebra.html5.gui.util.ColorChooserW;
import geogebra.html5.gui.util.LineStylePopup;
import geogebra.html5.gui.util.PointStylePopup;
import geogebra.html5.gui.util.Slider;
import geogebra.html5.openjdk.awt.geom.Dimension;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.properties.AnimationSpeedPanelW;
import geogebra.web.gui.properties.AnimationStepPanelW;
import geogebra.web.gui.properties.ListBoxPanel;
import geogebra.web.gui.properties.OptionPanel;
import geogebra.web.gui.properties.SliderPanelW;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.SelectionTable;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.helper.AsyncOperation;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OptionsObjectW extends
geogebra.common.gui.dialog.options.OptionsObject implements OptionPanelW
{
	private Localization loc;

	TabPanel tabPanel;

	private FlowPanel wrappedPanel;
	private OptionsTab basicTab;
	
	//Basic
	private NamePanel namePanel;
	private ShowObjectPanel showObjectPanel;
	private TracePanel tracePanel;
	private LabelPanel labelPanel;
	private FixPanel fixPanel;
	private AuxPanel auxPanel;
	private AnimatingPanel animatingPanel;
	private BackgroundImagePanel bgImagePanel;
	private ReflexAnglePanel reflexAnglePanel;
	private RightAnglePanel rightAnglePanel;
	private ListAsComboPanel listAsComboPanel;
	private ShowTrimmedIntersectionLinesPanel trimmedIntersectionLinesPanel;
	private AllowOutlyingIntersectionsPanel allowOutlyingIntersectionsPanel;
	private FixCheckboxPanel fixCheckboxPanel;
	//Color picker
	private ColorPanel colorPanel;

	// Style
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel;
	private LineStylePanel lineStylePanel;
	private AngleArcSizePanel angleArcSizePanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private IneqPanel ineqStylePanel;
	private TextFieldSizePanel textFieldSizePanel;

	//Advanced
	private ShowConditionPanel showConditionPanel;
	private boolean isDefaults;
	private ButtonSizePanel buttonSizePanel;
	private ColorFunctionPanel colorFunctionPanel;
	private LayerPanel layerPanel;
	private TooltipPanel tooltipPanel;
	private SelectionAllowedPanel selectionAllowedPanel;
	private GraphicsViewLocationPanel graphicsViewLocationPanel;

	//Algebra
	private CoordsPanel coordsPanel;
	private LineEqnPanel lineEqnPanel;
	private ConicEqnPanel conicEqnPanel;

	private List<OptionsTab> tabs;


	String localize(final String id) {
		// TODO Auto-generated method stub
		String txt = loc.getPlain(id);
		if (txt.equals(id)) {
			txt = loc.getMenu(id);
		}
		return txt;
	}

	private class OptionsTab extends FlowPanel {
		private String titleId;
		private int index;
		private List<OptionPanel> panels;
		private boolean hasAdded;
		public OptionsTab(final String title) {
			super();
			this.titleId = title;
			hasAdded = false;
			panels = new ArrayList<OptionPanel>();
			setStyleName("ObjectPropertiesTab");
		}

		public void add(OptionPanel panel) {
			add(panel.getWidget());
			panels.add(panel);
		}

		public void addPanelList(List<OptionPanel> panels) {
			for (OptionPanel panel: panels) {
				add(panel);
			}
		}

		public boolean update(Object[] geos) {
			boolean enabled = false;
			for (OptionPanel panel: panels) {
				enabled = panel.update(geos) || enabled;
			}

			TabBar tabBar = tabPanel.getTabBar();
			tabBar.setTabText(index, getTabText());
			tabBar.setTabEnabled(index, enabled);	
			if (!enabled && tabBar.getSelectedTab() == index) {
				tabBar.selectTab(0);
			}
			return enabled;
		}

		private String getTabText() {
			return localize(titleId);
		}

		public void addToTabPanel() {
			tabPanel.add(this, getTabText());
			index = tabPanel.getWidgetIndex(this);
			hasAdded = true;
		}
	}

	private class CheckboxPanel extends OptionPanel implements IBooleanOptionListener {
		private final CheckBox checkbox;
		private final String titleId;
		public CheckboxPanel(final String title) {
			checkbox = new CheckBox();
			checkbox.setStyleName("checkBoxPanel");
			setWidget(getCheckbox());
			this.titleId = title;

			getCheckbox().addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					((BooleanOptionModel)getModel()).applyChanges(getCheckbox().getValue());
				}
			});

		}

		public void updateCheckbox(boolean value) {
			getCheckbox().setValue(value);
		}

		@Override
		public void setLabels() {
			getCheckbox().setText(localize(titleId));
		}

		public CheckBox getCheckbox() {
			return checkbox;
		}
	}


	private class ShowObjectPanel extends CheckboxPanel implements IShowObjectListener {
		public ShowObjectPanel() {
			super("ShowObject");
			setModel(new ShowObjectModel(this));
		}

		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setValue(value);
			getCheckbox().setEnabled(isEnabled);
		}
	}

	private class TracePanel extends CheckboxPanel {
		public TracePanel() {
			super("ShowTrace");
			setModel(new TraceModel(this));
		}

	}

	private class LabelPanel extends OptionPanel implements IShowLabelListener {
		private final CheckBox showLabelCB;
		private final FlowPanel mainWidget;
		private final ListBox labelMode;
		private ShowLabelModel model;
		public LabelPanel() {
			mainWidget = new FlowPanel();
			showLabelCB = new CheckBox(localize("ShowLabel") + ":"); 
			mainWidget.add(showLabelCB);
			setWidget(mainWidget);

			model = new ShowLabelModel(app, this);
			setModel(model);

			updateShowLabel();

			labelMode = new ListBox(false);

			// Borcherd

			labelMode.setEnabled(showLabelCB.getValue());

			showLabelCB.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					model.applyShowChanges(showLabelCB.getValue());
				}
			});

			labelMode.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyModeChanges(labelMode.getSelectedIndex());
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


		public void update(boolean isEqualVal, boolean isEqualMode) {
			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18

			updateShowLabel();

			GeoElement geo0 = model.getGeoAt(0);
			// set label visible checkbox
			if (isEqualVal) {
				showLabelCB.setValue(geo0.isLabelVisible());
				labelMode.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setValue(false);
				labelMode.setEnabled(false);
			}

			// set label visible checkbox
			if (isEqualMode) {
				labelMode.setSelectedIndex(geo0.getLabelMode());
			}
			else {
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
			labelMode.addItem(localize("Caption")); // index 3 Michael
			labelMode.setSelectedIndex(selectedIndex);        
		}
	}

	private class FixPanel extends CheckboxPanel {

		public FixPanel() {
			super("FixObject");
			setModel(new FixObjectModel(this));
		}
	}

	private class AuxPanel extends CheckboxPanel {

		public AuxPanel() {
			super("AuxiliaryObject");
			setModel(new AuxObjectModel(this));
		}

	}

	private class ShowConditionPanel extends OptionPanel implements	IShowConditionListener {

		private static final long serialVersionUID = 1L;

		private ShowConditionModel model;
		private Label title;
		private AutoCompleteTextFieldW tfCondition;

		private Kernel kernel;
		private boolean processed;

		public ShowConditionPanel() {
			kernel = app.getKernel();
			//this.propPanel = propPanel;
			model = new ShowConditionModel(app, this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();

			title = new Label();
			mainPanel.add(title);
			// non auto complete input panel
			InputPanelW inputPanel = new InputPanelW(null, getAppW(), -1, false);
			tfCondition = inputPanel.getTextComponent();

			tfCondition.addKeyHandler(new KeyHandler(){

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						doActionPerformed();	    
					}
				}

			});

			tfCondition.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusGained(){
					processed = false;
				}

				@Override
				protected void wrapFocusLost(){
					if (!processed) {
						doActionPerformed();
					}
				}	
			});
			// put it all together
			mainPanel.add(inputPanel);
			setWidget(mainPanel);
		}



		private void doActionPerformed() {
			processed = true;
			model.applyChanges(tfCondition.getText());
		}

		public void setText(String text) {
			tfCondition.setText(text);	
		}



		@Override
		public void setLabels() {
			title.setText(app.getMenu("Condition.ShowObject"));

		}

		public void updateSelection(Object[] geos) {
			// TODO Auto-generated method stub

		}

	}

	private class ColorPanel extends OptionPanel implements IColorObjectListener {
		private ColorObjectModel model;
		private FlowPanel mainPanel;
		private ColorChooserW colorChooserW; 
		private ColorPopupMenuButton colorChooser;
		private GColor selectedColor;
		private SelectionTable colorTable;
		private Label chooseLabel;
		private Label rgbLabel;

		public ColorPanel() {
			model = new ColorObjectModel(app, this);
			setModel(model);
			final GDimensionW colorIconSize = new GDimensionW(20, 20);
			final GDimensionW colorIconSizeW = new GDimensionW(15, 15);
			
			colorChooserW = new ColorChooserW(800, 300, colorIconSizeW, 2);
			
			colorChooser = new ColorPopupMenuButton((AppW) app, colorIconSize,
					ColorPopupMenuButton.COLORSET_DEFAULT, true) {

				@Override
				public void update(Object[] geos) {

					updateColorTable();
					GeoElement geo0 = model.getGeoAt(0);
					int index = this.getColorIndex(geo0.getObjectColor());
					setSelectedIndex(index);
					setDefaultColor(geo0.getAlphaValue(), geo0.getObjectColor());
				};

				@Override
				public void handlePopupActionEvent(){
					super.handlePopupActionEvent();
					applyChanges();
				}

				@Override
				public void setSliderValue(int value) {
					super.setSliderValue(value);
					if (!model.hasGeos()) {
						return;
					}
					float alpha = value / 100.0f;
					GColor color = model.getGeoAt(0).getObjectColor();
					model.applyChanges(color, alpha, true);
				}
			};

			colorChooser.setKeepVisible(false);
			mainPanel = new FlowPanel();
			FlowPanel colorPanel = new FlowPanel();
			chooseLabel = new Label();
			rgbLabel = new Label();
			colorPanel.add(chooseLabel);
			colorPanel.add(colorChooser);
			colorPanel.add(rgbLabel);
			mainPanel.add(colorPanel);
			mainPanel.add(colorChooserW);
			setWidget(mainPanel);

		}


		public void applyChanges() {
			float alpha = colorChooser.getSliderValue() / 100.0f;
			GColor color = colorChooser.getSelectedColor();
			model.applyChanges(color, alpha, false);

		}

		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground, boolean hasOpacity) {
			// TODO Auto-generated method stub
			selectedColor = null;
			GColor selectedBGColor = null;
			float alpha = 1;
			GeoElement geo0 = model.getGeoAt(0);
			if (equalObjColorBackground) {
				selectedBGColor = geo0.getBackgroundColor();
			}

			if (isBackgroundColorSelected()) {
				selectedColor = selectedBGColor;
			}				
			else {
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

			colorChooser.setSliderVisible(hasOpacity);
			colorChooser.update(model.getGeos());
			colorChooserW.update();
			updatePreview(selectedColor, 1);
		}


		public void updatePreview(GColor col, float alpha) {
			rgbLabel.setText(ColorObjectModel.getColorAsString(col));
		}

		public boolean isBackgroundColorSelected() {
			// TODO Auto-generated method stub
			return false;
		}


		public void updateNoBackground(GeoElement geo, GColor col, float alpha,
				boolean updateAlphaOnly, boolean allFillable) {
			if (!updateAlphaOnly){
				geo.setObjColor(col);
			}
			if (allFillable){
				geo.setAlphaValue(alpha);
			}

		}


		@Override
		public void setLabels() {
			chooseLabel.setText(localize("Color"));
		}

	}


	private class NamePanel extends OptionPanel implements IObjectNameListener {

		private static final long serialVersionUID = 1L;

		private ObjectNameModel model;
		private AutoCompleteTextFieldW tfName, tfDefinition, tfCaption;

		private Label nameLabel, defLabel, captionLabel;
		private InputPanelW inputPanelName, inputPanelDef, inputPanelCap;

		private FlowPanel mainWidget;
		private FlowPanel namePanel;
		private FlowPanel defPanel;
		private FlowPanel captionPanel;

		public NamePanel() {
			model = new ObjectNameModel(app, this);
			setModel(model);
			// NAME PANEL

			// non auto complete input panel
			inputPanelName = new InputPanelW(null, (AppW) app, 1, -1, true);
			tfName = (AutoCompleteTextFieldW) inputPanelName.getTextComponent();
			tfName.setAutoComplete(false);
			tfName.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.applyNameChange(tfName.getText());
				}	
			});
			tfName.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyNameChange(tfName.getText());
					}
				}});

			// definition field: non auto complete input panel
			inputPanelDef = new InputPanelW(null, getAppW(), 1, -1, true);
			tfDefinition = (AutoCompleteTextFieldW) inputPanelDef
					.getTextComponent();
			tfDefinition.setAutoComplete(false);
			tfDefinition.addFocusListener(new FocusListener(this){
				@Override
				public void wrapFocusGained() {
					//started to type something : store current geo if focus lost
					currentGeoForFocusLost = model.getCurrentGeo();
				}

				@Override
				protected void wrapFocusLost(){
					model.redefineCurrentGeo(currentGeoForFocusLost,  tfDefinition.getText(), 
							redefinitionForFocusLost); 
				}
			});

			tfDefinition.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyDefinitionChange(tfDefinition.getText());
					}

				}});

			// caption field: non auto complete input panel
			inputPanelCap = new InputPanelW(null, getAppW(), 1, -1, true);
			tfCaption = (AutoCompleteTextFieldW) inputPanelCap.getTextComponent();
			tfCaption.setAutoComplete(false);

			tfCaption.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.applyCaptionChange(tfCaption.getText());
				}	
			});
			tfCaption.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyCaptionChange(tfCaption.getText());
					}
				}});

			mainWidget = new FlowPanel();

			// name panel
			namePanel = new FlowPanel();
			nameLabel = new Label();
			inputPanelName.insert(nameLabel, 0);
			
			namePanel.add(inputPanelName);
			mainWidget.add(namePanel);

			// definition panel
			defPanel = new FlowPanel();
			defLabel = new Label();
			defPanel.add(defLabel);
			defPanel.add(inputPanelDef);
			mainWidget.add(defPanel);

			// caption panel
			captionPanel = new FlowPanel();
			captionLabel = new Label();
			captionPanel.add(captionLabel);
			captionPanel.add(inputPanelCap);
			mainWidget.add(captionPanel);

			namePanel.setStyleName("optionsInput");
			defPanel.setStyleName("optionsInput");
			captionPanel.setStyleName("optionsInput");
			setWidget(mainWidget);
			updateGUI(true, true);
		}

		public void setLabels() {
			nameLabel.setText(loc.getPlain("Name") + ":");
			defLabel.setText(loc.getPlain("Definition") + ":");
			captionLabel.setText(loc.getMenu("Button.Caption") + ":");
		}

		public void updateGUI(boolean showDefinition, boolean showCaption) {
			int rows = 1;
			mainWidget.clear();

			if (loc.isRightToLeftReadingOrder()) {
				mainWidget.add(inputPanelName);
				mainWidget.add(nameLabel);
			} else {
				mainWidget.add(nameLabel);
				mainWidget.add(inputPanelName);
			}

			if (showDefinition) {
				rows++;
				if (loc.isRightToLeftReadingOrder()) {
					mainWidget.add(inputPanelDef);
					mainWidget.add(defLabel);
				} else {
					mainWidget.add(defLabel);
					mainWidget.add(inputPanelDef);
				}
			}

			if (showCaption) {
				rows++;
				if (loc.isRightToLeftReadingOrder()) {
					mainWidget.add(inputPanelCap);
					mainWidget.add(captionLabel);
				} else {
					mainWidget.add(captionLabel);
					mainWidget.add(inputPanelCap);
				}
			}

			//app.setComponentOrientation(this);

			this.rows = rows;

		}

		private int rows;

		/**
		 * current geo on which focus lost shouls apply
		 * (may be different to current geo, due to threads)
		 */
		private GeoElement currentGeoForFocusLost = null;

		private String redefinitionForFocusLost = "";

		public void updateDef(GeoElement geo) {

			// do nothing if called by doActionPerformed
			//		if (actionPerforming)
			//			return;

			model.getDefInputHandler().setGeoElement(geo);
			tfDefinition.setText(model.getDefText(geo));

			// App.printStacktrace(""+geo);
		}

		public void updateName(GeoElement geo) {

			//		// do nothing if called by doActionPerformed
			//		if (actionPerforming)
			//			return;
			//
			model.getNameInputHandler().setGeoElement(geo);
			tfName.setText(geo.getLabel(StringTemplate.editTemplate));

			// App.printStacktrace(""+geo);
		}

		public void setNameText(final String text) {
			tfName.setText(text);
			tfName.requestFocus();
		}

		public void setDefinitionText(final String text) {
			tfDefinition.setText(text);
		}

		public void setCaptionText(final String text) {
			tfCaption.setText(text);
			tfCaption.requestFocus();
		}
		public void updateCaption() {
			tfCaption.setText(model.getCurrentGeo().getRawCaption());

		}

		public void updateDefLabel() {
			updateDef(model.getCurrentGeo());

			if (model.getCurrentGeo().isIndependent()) {
				defLabel.setText(localize("Value") + ":");
			} else {
				defLabel.setText(localize("Definition") + ":");
			}
		}

		public void updateName(String text) {
			tfName.setText(text);

			// if a focus lost is called in between, we keep the current definition text
			redefinitionForFocusLost = tfDefinition.getText();


		}
	}

	private class BackgroundImagePanel extends CheckboxPanel {

		public BackgroundImagePanel() {
			super("BackgroundImage");
			setModel(new BackgroundImageModel(this));
		}

	}

	class ListAsComboPanel extends CheckboxPanel implements IListAsComboListener {
		public ListAsComboPanel() {
			super("DrawAsDropDownList");
			setModel(new ListAsComboModel(app, this));
		}

		public void drawListAsComboBox(GeoList geo, boolean value) {
			Iterator<Integer> it = geo.getViewSet().iterator();

			// #3929
			while (it.hasNext()) {
				Integer view = it.next();
				if (view.intValue() == App.VIEW_EUCLIDIAN) {
					app.getEuclidianView1().drawListAsComboBox(geo, value);
				} else if (view.intValue() == App.VIEW_EUCLIDIAN2 && app.hasEuclidianView2()) {
					app.getEuclidianView2().drawListAsComboBox(geo, value);
				}

			}
		}

	}

	class ReflexAnglePanel extends OptionPanel implements IReflexAngleListener {
		private ReflexAngleModel model;
		private FlowPanel mainWidget;
		private Label intervalLabel;
		private ListBox intervalLB;

		public ReflexAnglePanel() {
			model = new ReflexAngleModel(this, app, isDefaults);
			setModel(model);

			mainWidget = new FlowPanel();

			intervalLabel = new Label();
			mainWidget.add(intervalLabel);

			intervalLB = new ListBox();

			intervalLB.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyChanges(getIndex());
				}   
			});

			mainWidget.add(intervalLB);

			setWidget(mainWidget);
		}

		public void setLabels() {
			intervalLabel.setText(localize("AngleBetween"));

			setComboLabels();
		}
		public void setComboLabels() {
			int idx = intervalLB.getSelectedIndex();
			intervalLB.clear();
			model.fillModes(loc);
			intervalLB.setSelectedIndex(idx);

		}

		private int getIndex() {
			if (model.hasOrientation()) {
				return intervalLB.getSelectedIndex();
			}

			// first interval disabled
			return intervalLB.getSelectedIndex() + 1;
		}
		public void addItem(String item) {
			intervalLB.addItem(item);
		}

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

		public void setSelectedItem(String item) {
	        // TODO Auto-generated method stub
	        
        }


	}


	class RightAnglePanel extends CheckboxPanel {
		public RightAnglePanel() {
			super("EmphasizeRightAngle");
			setModel(new RightAngleModel(this));

		}
	}

	private class ShowTrimmedIntersectionLinesPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;
		public ShowTrimmedIntersectionLinesPanel() {
			super("ShowTrimmed");
			setModel(new TrimmedIntersectionLinesModel(this));
		}

	} // ShowTrimmedIntersectionLines

	private class AnimatingPanel extends CheckboxPanel {
		public AnimatingPanel() {
			super("Animating");
			setModel(new AnimatingModel(app, this));
		}

	}

	private class AllowOutlyingIntersectionsPanel extends CheckboxPanel {

		public AllowOutlyingIntersectionsPanel() {
			super("allowOutlyingIntersections");
			setModel(new OutlyingIntersectionsModel(this));
		}

	}

	private class FixCheckboxPanel extends CheckboxPanel {

		public FixCheckboxPanel() {
			super("FixCheckbox");
			setModel(new FixCheckboxModel(this));
		}

	}

	private class PointSizePanel extends OptionPanel implements ISliderListener {
		private PointSizeModel model;
		private Slider slider;
		private Label titleLabel;
		private Label minLabel;
		private Label maxLabel;
		public PointSizePanel() {
			model = new PointSizeModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			FlowPanel flow = new FlowPanel();

			minLabel = new Label("1");
			flow.add(minLabel);
			flow.setStyleName("optionsSlider");
			slider = new Slider(1, 9);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			flow.add(slider);

			maxLabel = new Label("9");
			flow.add(maxLabel);
			mainPanel.add(flow);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					if (true){//!slider.getValueIsAdjusting()) {
						model.applyChanges(slider.getValue());
					}
				}});
		}
		@Override
		public void setLabels() {
			titleLabel.setText(localize("PointSize"));

		}

		public void setValue(int value) {
			slider.setValue(value);

		}

	}

	private class PointStylePanel extends OptionPanel implements IComboListener {
		private PointStyleModel model;
		private Label titleLabel;
		private PointStylePopup btnPointStyle;
		private int iconHeight = 24;
		public PointStylePanel() {
			model = new PointStyleModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label("-");
			mainPanel.add(titleLabel);
			btnPointStyle = PointStylePopup.create(getAppW(), iconHeight, -1, false,
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

		public void setSelectedIndex(int index) {
			if (btnPointStyle != null)
				btnPointStyle.setSelectedIndex(index);
		}
		public void addItem(String item) {
			// TODO Auto-generated method stub

		}
		public void setSelectedItem(String item) {
	        // TODO Auto-generated method stub
	        
        }


	} 

	private class LineStylePanel extends OptionPanel implements ILineStyleListener {
		LineStyleModel model;
		private Label sliderLabel;
		private Slider slider;
		private Label minLabel;
		private Label maxLabel;
		private Label popupLabel;
		LineStylePopup btnLineStyle;
		private int iconHeight = 24;
		public LineStylePanel() {
			model = new LineStyleModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			sliderLabel = new Label();
			mainPanel.add(sliderLabel);

			FlowPanel flow = new FlowPanel();
			flow.setStyleName("optionsSlider");
			minLabel = new Label("1");
			flow.add(minLabel);
			slider = new Slider(1, GeoElement.MAX_LINE_WIDTH);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			flow.add(slider);

			maxLabel = new Label("" + GeoElement.MAX_LINE_WIDTH);
			flow.add(maxLabel);
			mainPanel.add(flow);

			slider.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					if (true){//!slider.getValueIsAdjusting()) {
						model.applyThickness(slider.getValue());
					}
				}});

			FlowPanel stylePanel = new FlowPanel();
			popupLabel = new Label();
			stylePanel.add(popupLabel);
			btnLineStyle = LineStylePopup.create(getAppW(), iconHeight, -1, false);
			//			slider.setSnapToTicks(true);
			btnLineStyle.addPopupHandler(new PopupMenuHandler() {

				public void fireActionPerformed(Object actionButton) {
					model.applyLineTypeFromIndex(btnLineStyle.getSelectedIndex());

				}});
			btnLineStyle.setKeepVisible(false);
			mainPanel.add(btnLineStyle);

			stylePanel.add(btnLineStyle);
			mainPanel.add(stylePanel);

			setWidget(mainPanel);
		}
		@Override
		public void setLabels() {
			sliderLabel.setText(localize("Thickness"));
			popupLabel.setText(localize("LineStyle") + ":");

		}

		public void setValue(int value) {
			slider.setValue(value);

		}
		public void setMinimum(int minimum) {
			slider.setMinimum(minimum);

		}
		public void selectCommonLineStyle(boolean equalStyle, int type) {
			if (true) {
				btnLineStyle.selectLineType(type);

			}
			//			else {
			//				btnLineStyle.setSelectedIndex(-1);
			//			}
		}
	}



	private class AngleArcSizePanel extends OptionPanel implements ISliderListener {
		private AngleArcSizeModel model;
		private Slider slider;
		private Label titleLabel;
		private Label minLabel;
		private Label maxLabel;
		public AngleArcSizePanel() {
			model = new AngleArcSizeModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			FlowPanel flow = new FlowPanel();

			minLabel = new Label("10");
			flow.setStyleName("optionsSlider");
			flow.add(minLabel);

			slider = new Slider(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			flow.add(slider);

			maxLabel = new Label("100");
			flow.add(maxLabel);
			mainPanel.add(flow);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}});
		}
		@Override
		public void setLabels() {
			titleLabel.setText(localize("Size"));

		}

		public void setValue(int value) {
			slider.setValue(value);

		}

	}

	private class SlopeTriangleSizePanel extends OptionPanel implements ISliderListener {
		private SlopeTriangleSizeModel model;
		private Slider slider;
		private Label titleLabel;
		private Label minLabel;
		private Label maxLabel;
		public SlopeTriangleSizePanel() {
			model = new SlopeTriangleSizeModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			FlowPanel flow = new FlowPanel();

			minLabel = new Label("1");
			flow.add(minLabel);
			flow.setStyleName("optionsSlider");

			slider = new Slider(1, 10);
			slider.setMajorTickSpacing(1);
			slider.setMinorTickSpacing(2);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			flow.add(slider);

			maxLabel = new Label("10");
			flow.add(maxLabel);
			mainPanel.add(flow);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}});
		}
		@Override
		public void setLabels() {
			titleLabel.setText(localize("Size"));

		}

		public void setValue(int value) {
			slider.setValue(value);

		}

	}


	private class IneqPanel extends CheckboxPanel implements IIneqStyleListener {

		private static final long serialVersionUID = 1L;

		public IneqPanel() {
			super("ShowOnXAxis");
			setModel(new IneqStyleModel(this));
		}

		public void enableFilling(boolean value) {
			//	fillingPanel.setAllEnabled(value);
		}

		//		@Override
		//		public void apply(boolean value) {
		//			super.apply(value);
		//			enableFilling(!value);
		//		}

	} // IneqPanel

	private class TextFieldSizePanel extends OptionPanel implements ITextFieldListener {

		private TextFieldSizeModel model;
		private InputPanelW inputPanel;
		private Label title;
		private AutoCompleteTextFieldW tfSize;
		public TextFieldSizePanel() {
			model = new TextFieldSizeModel(getAppW(), this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();

			inputPanel = new InputPanelW(null, getAppW(), 1, -1, false);
			tfSize = (AutoCompleteTextFieldW) inputPanel.getTextComponent();
			tfSize.setAutoComplete(false);
			tfSize.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.applyChanges(tfSize.getText());
				}	
			});
			tfSize.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyChanges(tfSize.getText());
					}
				}});
			mainPanel.add(inputPanel);
			setWidget(mainPanel);

		}
		public void setText(String text) {
			tfSize.setText(text);
		}

		@Override
		public void setLabels() {
			title.setText(localize("TextfieldLength"));
		}

	}


	public class ButtonSizePanel extends OptionPanel implements IButtonSizeListener {
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


		public ButtonSizePanel() {
			model = new ButtonSizeModel(this);
			setModel(model);
			labelWidth = new Label();
			labelHeight = new Label();
			labelPixelW = new Label();
			labelPixelH = new Label();
			cbUseFixedSize = new CheckBox();
			setLabels();

			ipButtonWidth = new InputPanelW(null, getAppW(), 1, -1, false);
			ipButtonHeight = new InputPanelW(null, getAppW(), 1, -1, false);

			tfButtonWidth = (AutoCompleteTextFieldW) ipButtonWidth.getTextComponent();
			tfButtonWidth.setAutoComplete(false);

			tfButtonHeight = (AutoCompleteTextFieldW) ipButtonHeight.getTextComponent();
			tfButtonHeight.setAutoComplete(false);

			FocusListener focusListener = new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.setSizesFromString(tfButtonWidth.getText(),
							tfButtonHeight.getText(), cbUseFixedSize.getValue());

				}	
			};

			tfButtonWidth.addFocusListener(focusListener);			
			tfButtonHeight.addFocusListener(focusListener);

			KeyHandler keyHandler = new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.setSizesFromString(tfButtonWidth.getText(),
								tfButtonHeight.getText(), cbUseFixedSize.getValue());
					}
				}

			};

			tfButtonWidth.addKeyHandler(keyHandler);
			tfButtonHeight.addKeyHandler(keyHandler);

			cbUseFixedSize.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyChanges(cbUseFixedSize.getValue());

				}});
			//tfButtonHeight.setInputVerifier(new SizeVerify());
			//tfButtonWidth.setInputVerifier(new SizeVerify());
			//tfButtonHeight.setEnabled(cbUseFixedSize.getValue());
			//tfButtonWidth..setEnabled(cbUseFixedSize.getValue());

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.add(cbUseFixedSize);
			mainPanel.add(labelWidth);
			mainPanel.add(tfButtonWidth);
			mainPanel.add(labelPixelW);
			mainPanel.add(labelHeight);
			mainPanel.add(tfButtonHeight);
			mainPanel.add(labelPixelH);
			setWidget(mainPanel);
		}
		public void updateSizes(int width, int height, boolean isFixed) {
			cbUseFixedSize.setValue(isFixed);
			tfButtonHeight.setText("" + height);
			tfButtonWidth.setText("" + width);
			//			tfButtonHeight.setEnabled(isFixed);
			//			tfButtonWidth.setEnabled(isFixed);
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

	private class ColorFunctionPanel extends OptionPanel implements IColorFunctionListener {
		private ColorFunctionModel model;
		private InputPanelW inputPanelA;
		private AutoCompleteTextFieldW tfRed, tfGreen, tfBlue, tfAlpha;
		private Button btRemove;
		private Label title;
		private Label nameLabelR, nameLabelG, nameLabelB, nameLabelA;

		private ListBox cbColorSpace;
		private int colorSpace = GeoElement.COLORSPACE_RGB;
		private boolean allowSetComboBoxLabels = true;

		private String defaultR = "0", defaultG = "0", defaultB = "0",
				defaultA = "1";

		private Kernel kernel;
		private boolean processed = false;
		public ColorFunctionPanel() {
			kernel = app.getKernel();
			model = new ColorFunctionModel(app, this);
			setModel(model);
			// non auto complete input panel
			InputPanelW inputPanelR = new InputPanelW(null, getAppW(), 1, -1, true);
			InputPanelW inputPanelG = new InputPanelW(null, getAppW(), 1, -1, true);
			InputPanelW inputPanelB = new InputPanelW(null, getAppW(), 1, -1, true);
			inputPanelA = new InputPanelW(null, getAppW(), 1, -1, true);
			tfRed = (AutoCompleteTextFieldW) inputPanelR.getTextComponent();
			tfGreen = (AutoCompleteTextFieldW) inputPanelG.getTextComponent();
			tfBlue = (AutoCompleteTextFieldW) inputPanelB.getTextComponent();
			tfAlpha = (AutoCompleteTextFieldW) inputPanelA.getTextComponent();

			title = new Label();

			nameLabelR = new Label();
			nameLabelG = new Label();
			nameLabelB = new Label();
			nameLabelA = new Label();

			FocusListener focusListener = new FocusListener(this){

				@Override
				protected void wrapFocusGained(){
					processed = false;
				}

				@Override
				protected void wrapFocusLost(){
					if (!processed)
						doActionPerformed();
				}	
			};


			tfRed.addFocusListener(focusListener);						
			tfGreen.addFocusListener(focusListener);						
			tfBlue.addFocusListener(focusListener);						
			tfAlpha.addFocusListener(focusListener);						

			KeyHandler keyHandler = new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						if (!processed)
							doActionPerformed();
					}
				}

			};

			tfRed.addKeyHandler(keyHandler);
			tfGreen.addKeyHandler(keyHandler);
			tfBlue.addKeyHandler(keyHandler);
			tfAlpha.addKeyHandler(keyHandler);

			btRemove = new Button("\u2718");
			btRemove.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					model.removeAll();
				}});

			cbColorSpace = new ListBox();
			cbColorSpace.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					colorSpace = cbColorSpace.getSelectedIndex();
					allowSetComboBoxLabels = false;
					setLabels();
					doActionPerformed();
					cbColorSpace.setSelectedIndex(colorSpace);
				}});

			FlowPanel colorsPanel = new FlowPanel();
			colorsPanel.add(nameLabelR);
			colorsPanel.add(inputPanelR);
			colorsPanel.add(nameLabelG);
			colorsPanel.add(inputPanelG);
			colorsPanel.add(nameLabelB);
			colorsPanel.add(inputPanelB);
			colorsPanel.add(nameLabelA);
			colorsPanel.add(inputPanelA);

			FlowPanel mainWidget = new FlowPanel();

			mainWidget.add(title);

			mainWidget.add(colorsPanel);

			FlowPanel buttonsPanel = new FlowPanel();

			FlowPanel leftPanel = new FlowPanel();
			leftPanel.add(cbColorSpace);
			FlowPanel rightPanel = new FlowPanel();
			rightPanel.add(btRemove);
			buttonsPanel.add(leftPanel);
			buttonsPanel.add(rightPanel);

			mainWidget.add(buttonsPanel);

			setWidget(mainWidget);

		}

		public void setLabels() {
			title.setText(loc.getMenu("DynamicColors"));
			tfRed.setVisible(false);
			if (allowSetComboBoxLabels) {
				cbColorSpace.clear();
				cbColorSpace.addItem(loc.getMenu("RGB"));
				cbColorSpace.addItem(loc.getMenu("HSV"));
				cbColorSpace.addItem(loc.getMenu("HSL"));
			}
			allowSetComboBoxLabels = true;

			switch (colorSpace) {
			case GeoElement.COLORSPACE_RGB:
				nameLabelR.setText(loc.getMenu("Red") + ":");
				nameLabelG.setText(loc.getMenu("Green") + ":");
				nameLabelB.setText(loc.getMenu("Blue") + ":");
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

			//btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
		}

		private void doActionPerformed() {
			processed = true;

			String strRed = tfRed.getText();
			String strGreen = tfGreen.getText();
			String strBlue = tfBlue.getText();
			String strAlpha = tfAlpha.getText();

			model.applyChanges(strRed, strGreen, strBlue, strAlpha, colorSpace,
					defaultR, defaultG, defaultB, defaultA);

		}

		public void setRedText(final String text) {
			tfRed.setText(text);

		}

		public void setGreenText(final String text) {
			tfGreen.setText(text);
			// TODO Auto-generated method stub

		}

		public void setBlueText(final String text) {
			tfBlue.setText(text);

		}

		public void setAlphaText(final String text) {
			tfAlpha.setText(text);

		}

		public void setDefaultValues(GeoElement geo) {
			GColor col = geo.getObjectColor();
			defaultR = "" + col.getRed() / 255.0;
			defaultG = "" + col.getGreen() / 255.0;
			defaultB = "" + col.getBlue() / 255.0;
			defaultA = "" + geo.getFillColor().getAlpha() / 255.0;


			// set the selected color space and labels to match the first geo's
			// color space
			colorSpace = geo.getColorSpace();
			cbColorSpace.setSelectedIndex(colorSpace);
			allowSetComboBoxLabels = false;
			setLabels();

		}

		public void showAlpha(boolean value) {
			inputPanelA.setVisible(value);
			nameLabelA.setVisible(value);
		}

		public void updateSelection(Object[] geos) {
			//updateSelection(geos);

		}

	}
	private class SelectionAllowedPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;
		public SelectionAllowedPanel() {
			super("SelectionAllowed");
			setModel(new SelectionAllowedModel(this));
		}

	}

	private class TooltipPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;

		public TooltipPanel() {
			super(loc, "Tooltip");
			setModel(new TooltipModel(this));
		}
	}

	private class LayerPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;

		public LayerPanel() {
			super(loc, "Layer");
			setModel(new LayerModel(this));
		}
	} 

	private class GraphicsViewLocationPanel extends OptionPanel implements IGraphicsViewLocationListener {
		private GraphicsViewLocationModel model;

		private Label title;
		private CheckBox cbGraphicsView;
		private CheckBox cbGraphicsView2;

		public GraphicsViewLocationPanel() {
			model = new GraphicsViewLocationModel(app, this);
			setModel(model);

			title = new Label();
			cbGraphicsView = new CheckBox();
			cbGraphicsView2 = new CheckBox();

			cbGraphicsView.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyToEuclidianView1(cbGraphicsView.getValue());

				}});

			cbGraphicsView.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyToEuclidianView2(cbGraphicsView2.getValue());

				}});

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.add(title);
			mainPanel.add(cbGraphicsView);
			mainPanel.add(cbGraphicsView2);
			setWidget(mainPanel);
		}

		public void selectView(int index, boolean isSelected) {
			if (index == 0) {
				cbGraphicsView.setValue(isSelected);
			} else {
				cbGraphicsView2.setValue(isSelected);
			}    
		}

		@Override
		public void setLabels() {
			title.setText(app.getMenu("Location"));
			cbGraphicsView.setText(localize("DrawingPad"));
			cbGraphicsView2.setText(localize("DrawingPad2"));

		}

	}

	private class CoordsPanel extends ListBoxPanel {

		public CoordsPanel() {
			super(loc, "Coordinates");
			setModel(new CoordsModel(this));
		}
	} // CoordsPanel

	private class LineEqnPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;

		public LineEqnPanel() {
			super(loc, "Equation");
			setModel(new LineEqnModel(this));
		}
	} // LineEqnPanel

	private class ConicEqnPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;

		public ConicEqnPanel() {
			super(loc, "Equation");
			setModel(new ConicEqnModel(this, loc));
		}

		@Override
		public void setLabels() {
			setTitle(loc.getPlain(getTitle()));
			ListBox lb = getListBox();
			if (getModel().hasGeos() && getModel().checkGeos()) {
				int selectedIndex = lb.getSelectedIndex();
				lb.clear();
				getModel().updateProperties();
				lb.setSelectedIndex(selectedIndex);
			}
		}

	} // ConicEqnPanel

	private class StartPointPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;

		public StartPointPanel() {
			super(loc, "StartingPoint");
			setModel(new StartPointModel(app, this));
		}

		private StartPointModel getStartPointModel() {
			return (StartPointModel)getModel();
		}

		@Override 
		protected boolean setupPanel() {
			boolean result = super.setupPanel();
			if (!result) {
				return false;
			}
			ListBox lb = getListBox();
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != lb.getItemCount() - 1) {
				lb.clear();
				lb.addItem("");
				getStartPointModel().fillModes(loc);
				setFirstLabel();
			}
			return true;
		}

		@Override
		protected void onListBoxChange(){
			final String strLoc = getListBox().getValue(getListBox().getSelectedIndex());
			getStartPointModel().applyChanges(strLoc);

		}

		@Override
		public void setSelectedIndex(int index) {
			ListBox lb = getListBox();
			if (index == 0) {
				setFirstLabel();
			} else {
				lb.setSelectedIndex(-1);
			}
		}

		private void setFirstLabel() {
			GeoElement p = (GeoElement)getStartPointModel().getLocateableAt(0).getStartPoint();
			if (p != null) {
				String coords = p.getLabel(StringTemplate.editTemplate); 
				getListBox().setItemText(0, coords);
			}
		}

		@Override
		public void setLabels(){
			getLabel().setText(getTitle());
		}
	} // StartPointPanel

	private class AbsoluteScreenLocationPanel extends CheckboxPanel {
		public AbsoluteScreenLocationPanel(){
			super("AbsoluteScreenLocation");
			setModel(new AbsoluteScreenLocationModel(app, this));
		}

	}

	private class ImageCornerPanel extends ListBoxPanel {
		private static final long serialVersionUID = 1L;
		private ImageCornerModel model;
		public ImageCornerPanel(int cornerIdx) {
			super(loc, "CornerModel");
			model = new ImageCornerModel(app, this);
			model.setCornerIdx(cornerIdx);
			setModel(model);
		}

		public void setIcon(ImageResource res) {
			if (res == null) {
				return;
			}
			Label label = getLabel();
			label.setStyleName("imageCorner");
			label.getElement().getStyle().setProperty("backgroundImage", "url(" + 
					res.getSafeUri().asString() + ")");
		}

		@Override
		protected void onListBoxChange() {
			final String item = getListBox().getValue(getListBox().getSelectedIndex());
			model.applyChanges(item);

		}

		@Override
		public void setLabels() {
			super.setLabels();
			String strLabelStart = app.getPlain("CornerPoint");
			getLabel().setText(strLabelStart + model.getCornerNumber() + ":");
		}
	}

	private class CornerPointsPanel extends OptionPanel
	{

		private ImageCornerPanel corner1;
		private ImageCornerPanel corner2;
		private ImageCornerPanel corner4;

		public CornerPointsPanel() {
			corner1 = new ImageCornerPanel(0); 
			corner2 = new ImageCornerPanel(1); 
			corner4 = new ImageCornerPanel(2);
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.add(corner1.getWidget());
			mainPanel.add(corner2.getWidget());
			mainPanel.add(corner4.getWidget());
			setWidget(mainPanel);
			corner1.setIcon(AppResources.INSTANCE.corner1());
			corner2.setIcon(AppResources.INSTANCE.corner2());
			corner4.setIcon(AppResources.INSTANCE.corner4());
		}

		public void setLabels() {
			corner1.setLabels();
			corner2.setLabels();
			corner4.setLabels();
		}

		public boolean update(Object[] geos) {
			if (geos == null) {
				return false;
			}

			boolean result = corner1.update(geos);
			result = corner2.update(geos) || result;
			result = corner4.update(geos) || result;
			return result;
		}
	}

	private class TextOptionsPanel extends OptionPanel implements ITextOptionsListener,
	ITextEditPanel, GeoElementSelectionListener {
		private static final int FontBOLD = 1;

		private static final int FontITALIC = 2;

		private TextOptionsModel model;

		private Label decimalLabel;
		private ListBox lbFont;
		private ListBox lbSize; 
		private ListBox lbDecimalPlaces;
		private ToggleButton btnBold;
		private ToggleButton  btnItalic;
		private ToggleButton  btnLatex;

		private FlowPanel secondLine;

		private FlowPanel btnPanel;
		private Button btnOk;
		private Button btnCancel;
		
		private boolean secondLineVisible = false;
		private GeoTextEditor editor;
		private TextEditAdvancedPanel advancedPanel;

		private TextPreviewPanelW previewer;
		private GeoText orig;
		public TextOptionsPanel() {

			model = new TextOptionsModel(app, this);
			setModel(model);

			editor = new GeoTextEditor(getAppW(), this);

			lbFont = new ListBox();
			for (String item: model.getFonts()) {
				lbFont.addItem(item);
			}

			lbFont.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					model.applyFont(lbFont.getSelectedIndex() == 1);
				}});
			lbSize = new ListBox();
			for (String item : model.getFonts()) {
				lbSize.addItem(item);
			}
			lbSize.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					boolean isCustom = (lbSize.getSelectedIndex() == 7);
					if (isCustom) {
						GOptionPaneW.showInputDialog(getAppW(),
								loc.getPlain("EnterPercentage"), new AsyncOperation() {

							private String percentStr = Math.round(model.getTextPropertiesAt(0)
									.getFontSizeMultiplier() * 100) + "%";

							public void callback() {
								model.applyFontSizeFromString(percentStr);				
							}

							void setPercentStr(String str) {
								this.percentStr = str;
							}

							public Object getData() {
								return percentStr;
							}

							public void setData(Object data) {
								percentStr = (String) data;
							}
						});



					} else {
						model.applyFontSizeFromIndex(lbSize
								.getSelectedIndex());
					}
				}
			});

			// font size
			// TODO require font phrases F.S.
			// toggle buttons for bold and italic
			btnBold = new ToggleButton(new Image(AppResources.INSTANCE
					.format_text_bold().getSafeUri().asString()));
			btnItalic = new ToggleButton(new Image(AppResources.INSTANCE
					.format_text_italic().getSafeUri().asString()));
			btnBold.getElement().getStyle().setWidth(18, Unit.PX);
			btnBold.getElement().getStyle().setHeight(18, Unit.PX);
			btnItalic.getElement().getStyle().setWidth(18, Unit.PX);
			btnItalic.getElement().getStyle().setHeight(18, Unit.PX);
			btnLatex = new ToggleButton("LaTeX");

			ClickHandler styleClick = new ClickHandler() {

				public void onClick(ClickEvent event) {
					model.applyFontStyle(btnBold.getValue(), btnItalic.getValue());
				}};

				btnBold.addClickHandler(styleClick);
				btnItalic.addClickHandler(styleClick);

				btnLatex.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
						model.setLaTeX(isLatex(), true);
	                    updatePreview();
					}});
				
				// decimal places
				lbDecimalPlaces = new ListBox();
				for (String item : loc.getRoundingMenu()) {
					lbDecimalPlaces.addItem(item);
				}

				lbDecimalPlaces.addChangeHandler(new ChangeHandler(){

					public void onChange(ChangeEvent event) {
						model.applyDecimalPlaces(lbDecimalPlaces.getSelectedIndex());
						updatePreview();
					}});

				// font, size
				FlowPanel mainPanel = new FlowPanel();
				FlowPanel firstLine = new FlowPanel();
				firstLine.setStyleName("textOptionsToolBar");
				firstLine.add(lbFont);
				firstLine.add(lbSize);
				firstLine.add(btnBold);
				firstLine.add(btnItalic);
				firstLine.add(btnLatex);

				// bold, italic
				secondLine = new FlowPanel();
				decimalLabel = new Label();
				secondLine.add(decimalLabel);
				secondLine.add(lbDecimalPlaces);

				mainPanel.add(firstLine);
				mainPanel.add(secondLine);
				secondLineVisible = true;
				
				mainPanel.add(editor);
				
				advancedPanel = new TextEditAdvancedPanel(getAppW(), this);
				previewer = advancedPanel.getPreviewer();
				mainPanel.add(advancedPanel);
				//mainPanel.add(previewer.getPanel());
				
				btnPanel = new FlowPanel();
				btnOk = new Button();
				btnPanel.add(btnOk);
				btnOk.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
	                    model.applyEditedGeo(editor.getText(), isLatex());
                    }}); 
				
				btnCancel = new Button();
				btnPanel.add(btnCancel);
				btnCancel.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
	                    model.cancelEditGeo();
                    }}); 
				
				mainPanel.add(btnPanel);
				setWidget(mainPanel);
				orig = null;
		}


		@Override
		public boolean update(Object[] geos) {
			getModel().setGeos(geos);
			
			if (!getModel().checkGeos()) {
				model.cancelEditGeo();
				return false;
			}
			
			getModel().updateProperties();
			setLabels();
			updatePreview();
			
			return true;
			
		}
		
		@Override
		public void setLabels() {
			String[] fontSizes = app.getLocalization().getFontSizeStrings();

			int selectedIndex = lbSize.getSelectedIndex();
			lbSize.clear();

			for (int i = 0; i < fontSizes.length; ++i) {
				lbSize.addItem(fontSizes[i]);
			}

			lbSize.addItem(app.getMenu("Custom") + "...");

			lbSize.setSelectedIndex(selectedIndex);


			decimalLabel.setText(app.getMenu("Rounding") + ":");
			btnLatex.setText(loc.getPlain("LaTeXFormula"));
			if (advancedPanel != null) {
				advancedPanel.setLabels();
			}
			btnOk.setText(localize("Ok"));
			btnCancel.setText(localize("Cancel"));
			}
		
		public void setWidgetsVisible(boolean showFontDetails, boolean isButton) {
			// hide most options for Textfields
			lbFont.setVisible(showFontDetails);
			btnBold.setVisible(showFontDetails);
			btnItalic.setVisible(showFontDetails);
			secondLine.setVisible(showFontDetails);
			secondLineVisible = showFontDetails;

			if (isButton) {
				secondLine.setVisible(!showFontDetails);
				secondLineVisible = !showFontDetails;
			}        
		}

		public void selectSize(int index) {
			lbSize.setSelectedIndex(index);

		}

		public void selectFont(int index) {
			lbFont.setSelectedIndex(index);

		}

		public void selectDecimalPlaces(int index) {
			lbDecimalPlaces.setSelectedIndex(index);
		}

		public void setSecondLineVisible(boolean noDecimals) {
			if (noDecimals) {

				if (secondLineVisible) {
					secondLineVisible = false;
				}
			} else {
				if (!secondLineVisible) {
					secondLineVisible = true;
				}

				secondLine.setVisible(secondLineVisible);
			}

		}

		public void updatePreview() {
			updatePreviewPanel();
		}

		private boolean isLatex() {
	        return btnLatex.getValue();
        }


		public void selectFontStyle(int style) {

			btnBold.setValue(style == GFont.BOLD
					|| style == (GFont.BOLD + GFont.ITALIC));
			btnItalic.setValue(style == GFont.ITALIC
					|| style == (GFont.BOLD + GFont.ITALIC));


		}


		public void updatePreviewPanel() {
//			if (previewer == null || model.getEditGeo() == null ) {
//				return;
//			}
//
//			previewer.updatePreviewText(model.getEditGeo(), model.getGeoGebraString(
//			        editor.getDynamicTextList(), isLatex()), isLatex());
		}



		public void setEditorText(ArrayList<DynamicTextElement> list) {

			editor.setText(list);

		}


		public void insertGeoElement(GeoElement geo) {
			editor.insertGeoElement(geo);
		}


		public void insertTextString(String text, boolean isLatex) {
			editor.insertTextString(text, isLatex);
       
        }


		public GeoText getEditGeo() {
	        return model.getEditGeo();
        }


		public void geoElementSelected(GeoElement geo, boolean addToSelection) {
	         model.cancelEditGeo();
	        
        }


	}
	//-----------------------------------------------
	public OptionsObjectW(AppW app, boolean isDefaults) {
		this.app = app;
		this.isDefaults = isDefaults;
		kernel = app.getKernel();
		loc = app.getLocalization();
		// build GUI
		initGUI();
	}

	private AppW getAppW() {
		return (AppW) app;
	}

	private void initGUI() {
		wrappedPanel = new FlowPanel();
		wrappedPanel.setStyleName("objectProperties");
		tabPanel = new TabPanel();

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() 
				{			
			public void onSelection(SelectionEvent<Integer> event) {
				updateGUI();

			}
				});
		tabPanel.setStyleName("objectPropertiesTabPanel");

		createBasicTab();

		tabs = Arrays.asList(
				basicTab,
				addTextTab(),
				addSliderTab(),
				addColorTab(),
				addStyleTab(),
				addPositionTab(),
				addAdvancedTab(),
				addAlgebraTab());

		for (OptionsTab tab: tabs) {
			tab.addToTabPanel();
		}

		wrappedPanel.add(tabPanel);
		wrappedPanel.addAttachHandler(new AttachEvent.Handler() {

			public void onAttachOrDetach(AttachEvent event) {
				app.setDefaultCursor();
			}
		});
		updateGUI();
		selectTab(0);

	}

	private void createBasicTab() {
		basicTab = new OptionsTab("Properties.Basic");

		basicTab.setStyleName("objectPropertiesTab");

		namePanel = new NamePanel();   
		if (!isDefaults) {
			basicTab.add(namePanel);
		}

		FlowPanel checkboxPanel = new FlowPanel();
		basicTab.add(checkboxPanel);


		showObjectPanel = new ShowObjectPanel();   
		checkboxPanel.add(showObjectPanel.getWidget());



		labelPanel = new LabelPanel();
		if (!isDefaults) {
			checkboxPanel.add(labelPanel.getWidget());
		}

		tracePanel = new TracePanel(); 
		checkboxPanel.add(tracePanel.getWidget());
		basicTab.add(checkboxPanel);

		if (!isDefaults) {
			animatingPanel = new AnimatingPanel();
			checkboxPanel.add(animatingPanel.getWidget());
		}

		fixPanel = new FixPanel();
		checkboxPanel.add(fixPanel.getWidget());

		auxPanel = new AuxPanel();
		checkboxPanel.add(auxPanel.getWidget());

		if (!isDefaults) {
			bgImagePanel = new BackgroundImagePanel();
			checkboxPanel.add(bgImagePanel.getWidget());
		}
		basicTab.add(checkboxPanel);

		if (!isDefaults) {
			reflexAnglePanel = new ReflexAnglePanel();
		}

		listAsComboPanel = new ListAsComboPanel();
		rightAnglePanel = new RightAnglePanel();
		trimmedIntersectionLinesPanel = new ShowTrimmedIntersectionLinesPanel();

		//		tabList.add(comboBoxPanel);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		basicTab.add(allowOutlyingIntersectionsPanel.getWidget());

		fixCheckboxPanel = new FixCheckboxPanel();
		basicTab.add(fixCheckboxPanel.getWidget());


		basicTab.addPanelList(Arrays.asList(namePanel,
				showObjectPanel,
				tracePanel,
				labelPanel,	
				fixPanel,
				auxPanel,
				animatingPanel,
				bgImagePanel,
				reflexAnglePanel,
				rightAnglePanel,
				listAsComboPanel,
				trimmedIntersectionLinesPanel,
				allowOutlyingIntersectionsPanel,
				fixCheckboxPanel));

	}

	private OptionsTab addTextTab() {
		OptionsTab tab = new OptionsTab("Text");
		tab.setStyleName("objectPropertiesTab");
		TextOptionsPanel textOptionsPanel = new TextOptionsPanel();
		tab.add(textOptionsPanel);
		return tab;
	}

	private OptionsTab addSliderTab() {
		OptionsTab tab = new OptionsTab("Slider");
		tab.setStyleName("objectPropertiesTab");
		SliderPanelW sliderPanel = new SliderPanelW(getAppW(), false, true);
		tab.add(sliderPanel);
		return tab;
	}

	private OptionsTab addColorTab() {
		OptionsTab tab = new OptionsTab("Color");
		tab.setStyleName("objectPropertiesTab");
		colorPanel = new ColorPanel();
		tab.add(colorPanel);
		return tab;
	}

	private OptionsTab addStyleTab() {
		OptionsTab tab = new OptionsTab("Properties.Style");

		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel();
		lineStylePanel = new LineStylePanel();
		angleArcSizePanel = new AngleArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		ineqStylePanel = new IneqPanel();
		textFieldSizePanel = new TextFieldSizePanel();
		buttonSizePanel = new ButtonSizePanel();

		tab.addPanelList(Arrays.asList(pointSizePanel,
				pointStylePanel,
				lineStylePanel,
				angleArcSizePanel,
				slopeTriangleSizePanel,
				ineqStylePanel,
				buttonSizePanel,
				textFieldSizePanel));
		return tab;
	}

	private OptionsTab addAdvancedTab() {
		OptionsTab tab = new OptionsTab("Advanced"); 
		showConditionPanel = new ShowConditionPanel();
		colorFunctionPanel = new ColorFunctionPanel();
		layerPanel = new LayerPanel();
		tooltipPanel = new TooltipPanel();
		selectionAllowedPanel = new SelectionAllowedPanel();
		graphicsViewLocationPanel = new GraphicsViewLocationPanel();

		tab.addPanelList(Arrays.asList(showConditionPanel,
				colorFunctionPanel,
				layerPanel,
				tooltipPanel,
				selectionAllowedPanel,
				graphicsViewLocationPanel));

		return tab;
	}

	private OptionsTab addAlgebraTab() {
		OptionsTab tab;
		coordsPanel = new CoordsPanel();
		lineEqnPanel = new LineEqnPanel();
		conicEqnPanel = new ConicEqnPanel();

		tab = new OptionsTab("Properties.Algebra");
		tab.addPanelList(
				Arrays.asList((OptionPanel)coordsPanel,
						lineEqnPanel,
						conicEqnPanel,
						new AnimationStepPanelW(getAppW()),
						new AnimationSpeedPanelW(getAppW())
						));

		return tab;

	}

	private OptionsTab addPositionTab() {
		OptionsTab tab;
		OptionPanel startPointPanel = new StartPointPanel();
		tab = new OptionsTab("Properties.Position");
		tab.addPanelList(
				Arrays.asList(startPointPanel,
						new CornerPointsPanel(),
						new AbsoluteScreenLocationPanel()
						));
		return tab;

	}
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	public void setMinimumSize(Dimension preferredSize) {

	}

	public void updateGUI() {
		loc = app.getLocalization();
		Object[] geos = app.getSelectionManager().getSelectedGeos().toArray();
		if (geos != null && geos.length != 0) {
			tabPanel.setVisible(true);
			for (OptionsTab tab: tabs) {
				tab.update(geos);
			}


		} else {
			tabPanel.setVisible(false);
			App.debug("No geos");
		}


	}

	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	public void selectTab(int index) {
		tabPanel.selectTab(index);	    
	}

}