package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.OptionsObject;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimatingModel;
import org.geogebra.common.gui.dialog.options.model.AuxObjectModel;
import org.geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel.IButtonSizeListener;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel.IDecoAngleListener;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel.IFillingListener;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel.IGraphicsViewLocationListener;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.ISliderListener;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LayerModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import org.geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
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
import org.geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.dialog.options.model.TooltipModel;
import org.geogebra.common.gui.dialog.options.model.TraceModel;
import org.geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.GeoTextEditor;
import org.geogebra.web.html5.gui.inputfield.ITextEditPanel;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.FileInputDialog;
import org.geogebra.web.web.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.web.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.web.gui.dialog.TextPreviewPanelW;
import org.geogebra.web.web.gui.dialog.options.model.ExtendedAVModel;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.properties.AnimationSpeedPanelW;
import org.geogebra.web.web.gui.properties.AnimationStepPanelW;
import org.geogebra.web.web.gui.properties.ComboBoxPanel;
import org.geogebra.web.web.gui.properties.GroupOptionsPanel;
import org.geogebra.web.web.gui.properties.IOptionPanel;
import org.geogebra.web.web.gui.properties.ListBoxPanel;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.properties.PropertiesViewW;
import org.geogebra.web.web.gui.properties.SliderPanelW;
import org.geogebra.web.web.gui.util.ColorChooserW;
import org.geogebra.web.web.gui.util.ComboBoxW;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.LineStylePopup;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PointStylePopup;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.gui.view.algebra.RadioButtonTreeItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OptionsObjectW extends OptionsObject implements OptionPanelW{
	Localization loc;

	TabPanel tabPanel;

	private FlowPanel wrappedPanel;
	private OptionsTab basicTab;

	//Basic
	private NamePanel namePanel;
	private ShowObjectPanel showObjectPanel;
	private LodPanel lodPanel;
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
	private ExtendedAVPanel avPanel;

	// Style
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel;
	private LineStylePanel lineStylePanel;
	AngleArcSizePanel angleArcSizePanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private IneqPanel ineqStylePanel;
	private TextFieldSizePanel textFieldSizePanel;
	FillingPanel fillingPanel;
	private InterpolateImagePanel interpolateImagePanel; 

	//Advanced
	private ShowConditionPanel showConditionPanel;
	boolean isDefaults;
	private ButtonSizePanel buttonSizePanel;
	private ColorFunctionPanel colorFunctionPanel;
	private LayerPanel layerPanel;
	private TooltipPanel tooltipPanel;
	private SelectionAllowedPanel selectionAllowedPanel;
	private GraphicsViewLocationPanel graphicsViewLocationPanel;

	//Decoration
	private OptionPanel decoAnglePanel;
	private DecoSegmentPanel decoSegmentPanel;

	//Algebra
	private CoordsPanel coordsPanel;
	private LineEqnPanel lineEqnPanel;
	private ConicEqnPanel conicEqnPanel;

	private List<OptionsTab> tabs;

	private TextOptionsPanel textOptionsPanel;






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
		private List<IOptionPanel> panels;
		private boolean hasAdded;
		
		public OptionsTab(final String title) {
			super();
			this.titleId = title;
			hasAdded = false;
			panels = new ArrayList<IOptionPanel>();
			setStyleName("propertiesTab");
		}

		public void add(IOptionPanel panel) {
			add(panel.getWidget());
			panels.add(panel);
		}

		public void addPanelList(List<OptionPanel> list) {
			for (OptionPanel panel: list) {
				add(panel);
			}
		}

		public boolean update(Object[] geos) {
			boolean enabled = false;
			for (IOptionPanel panel: panels) {
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

		public void onResize(int height, int width) {
	         this.setHeight(height + "px");
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
				@Override
				public void onClick(ClickEvent event) {
					((BooleanOptionModel)getModel()).applyChanges(getCheckbox().getValue());
				}
			});

		}

		@Override
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

		@Override
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
		final CheckBox showLabelCB;
		private final FlowPanel mainWidget;
		final ListBox labelMode;
		ShowLabelModel model;
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
				@Override
				public void onClick(ClickEvent event) {
					model.applyShowChanges(showLabelCB.getValue());
				}
			});

			labelMode.addChangeHandler(new ChangeHandler(){

				@Override
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


		@Override
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
		boolean processed;

		public ShowConditionPanel() {
			kernel = app.getKernel();
			//this.propPanel = propPanel;
			model = new ShowConditionModel(app, this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsInput");

			title = new Label();
			title.setStyleName("panelTitle");
			
			mainPanel.add(title);
			// non auto complete input panel
			InputPanelW inputPanel = new InputPanelW(null, getAppW(), -1, false);
			tfCondition = inputPanel.getTextComponent();

			tfCondition.addKeyHandler(new KeyHandler(){

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						doActionPerformed();	    
					}
				}

			});

			tfCondition.addFocusListener(new FocusListenerW(this){
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



		void doActionPerformed() {
			processed = true;
			model.applyChanges(tfCondition.getText());
		}

		@Override
		public void setText(String text) {
			tfCondition.setText(text);	
		}



		@Override
		public void setLabels() {
			title.setText(app.getMenu("Condition.ShowObject"));

		}

		@Override
		public void updateSelection(Object[] geos) {
			// TODO Auto-generated method stub

		}

	}

	private class ColorPanel extends OptionPanel implements IColorObjectListener {
		ColorObjectModel model;
		private FlowPanel mainPanel;
		private ColorChooserW colorChooserW; 
		private GColor selectedColor;

		public ColorPanel() {
			model = new ColorObjectModel(app, this);
			setModel(model);

			final GDimensionW colorIconSizeW = new GDimensionW(20, 20);

			colorChooserW = new ColorChooserW(app, 350, 210, colorIconSizeW, 4);
			colorChooserW.addChangeHandler(new ColorChangeHandler(){

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
				}});
			colorChooserW.setColorPreviewClickable();

			mainPanel = new FlowPanel();
			mainPanel.add(colorChooserW);
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
			if (!updateAlphaOnly){
				geo.setObjColor(col);
			}
			if (allFillable){
				geo.setAlphaValue(alpha);
			}

		}


		@Override
		public void setLabels() {
			colorChooserW.setPaletteTitles(localize("RecentColor"), localize("Other"));
			colorChooserW.setPreviewTitle(localize("Preview"));
			colorChooserW.setBgFgTitles(localize("BackgroundColor"), localize("ForegroundColor"));
			colorChooserW.setOpacityTitle(localize("Opacity"));
		}

	}


	private class NamePanel extends OptionPanel implements IObjectNameListener {

		private static final long serialVersionUID = 1L;

		ObjectNameModel model;
		AutoCompleteTextFieldW tfName, tfDefinition, tfCaption;

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
			tfName = inputPanelName.getTextComponent();
			tfName.setAutoComplete(false);
			tfName.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					model.applyNameChange(tfName.getText());
				}	
			});
			tfName.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyNameChange(tfName.getText());
					}
				}});

			// definition field: non auto complete input panel
			inputPanelDef = new InputPanelW(null, getAppW(), 1, -1, true);
			tfDefinition = inputPanelDef
					.getTextComponent();
			tfDefinition.setAutoComplete(false);
			tfDefinition.addFocusListener(new FocusListenerW(this){
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

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyDefinitionChange(tfDefinition.getText());
					}

				}});

			// caption field: non auto complete input panel
			inputPanelCap = new InputPanelW(null, getAppW(), 1, -1, true);
			tfCaption = inputPanelCap.getTextComponent();
			tfCaption.setAutoComplete(false);

			tfCaption.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					model.applyCaptionChange(tfCaption.getText());
				}	
			});
			tfCaption.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyCaptionChange(tfCaption.getText());
					}
				}});

			mainWidget = new FlowPanel();

			// name panel
			namePanel = new FlowPanel();
			nameLabel = new Label();
			//inputPanelName.insert(nameLabel, 0);

			namePanel.add(nameLabel);
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

		@Override
		public void setLabels() {
			nameLabel.setText(loc.getPlain("Name") + ":");
			defLabel.setText(loc.getPlain("Definition") + ":");
			captionLabel.setText(loc.getMenu("Button.Caption") + ":");
		}

		@Override
		public void updateGUI(boolean showDefinition, boolean showCaption) {
			int rows = 1;
			mainWidget.clear();

//			if (loc.isRightToLeftReadingOrder()) {
//				mainWidget.add(inputPanelName);
//				mainWidget.add(nameLabel);
//			} else {
//				mainWidget.add(nameLabel);
//				mainWidget.add(inputPanelName);
//			}
			mainWidget.add(namePanel);

			if (showDefinition) {
				rows++;
//				if (loc.isRightToLeftReadingOrder()) {
//					mainWidget.add(inputPanelDef);
//					mainWidget.add(defLabel);
//				} else {
//					mainWidget.add(defLabel);
//					mainWidget.add(inputPanelDef);
//				}
				mainWidget.add(defPanel);
			}

			if (showCaption) {
				rows++;
//				if (loc.isRightToLeftReadingOrder()) {
//					mainWidget.add(inputPanelCap);
//					mainWidget.add(captionLabel);
//				} else {
//					mainWidget.add(captionLabel);
//					mainWidget.add(inputPanelCap);
//				}
				mainWidget.add(captionPanel);
			}

			//app.setComponentOrientation(this);

			this.rows = rows;

		}

		private int rows;

		/**
		 * current geo on which focus lost shouls apply
		 * (may be different to current geo, due to threads)
		 */
		GeoElement currentGeoForFocusLost = null;

		String redefinitionForFocusLost = "";

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

		@Override
		public void setNameText(final String text) {
			tfName.setText(text);
			tfName.requestFocus();
		}

		@Override
		public void setDefinitionText(final String text) {
			tfDefinition.setText(text);
		}

		@Override
		public void setCaptionText(final String text) {
			tfCaption.setText(text);
			tfCaption.requestFocus();
		}

		@Override
		public void updateCaption() {
			tfCaption.setText(model.getCurrentGeo().getRawCaption());

		}

		@Override
		public void updateDefLabel() {
			updateDef(model.getCurrentGeo());

			if (model.getCurrentGeo().isIndependent()) {
				defLabel.setText(localize("Value") + ":");
			} else {
				defLabel.setText(localize("Definition") + ":");
			}
		}

		@Override
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
				} else if (view.intValue() == App.VIEW_EUCLIDIAN2 && app.hasEuclidianView2(1)) {
					app.getEuclidianView2(1).drawListAsComboBox(geo, value);
				}

			}
		}

	}

	class ReflexAnglePanel extends OptionPanel implements IReflexAngleListener {
		ReflexAngleModel model;
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
		public void setSelectedItem(String item) {
			// TODO Auto-generated method stub

		}


	}

	class LodPanel extends OptionPanel implements IComboListener {
		LodModel model;
		private FlowPanel mainWidget;
		private Label label;
		ListBox combo;

		public LodPanel() {
			model = new LodModel(this, app, isDefaults);
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
		PointSizeModel model;
		SliderPanel slider;
		private Label titleLabel;
		public PointSizePanel() {
			model = new PointSizeModel(this);
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
			//			slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
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
		public PointStylePanel() {
			model = new PointStyleModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName("optionsPanel");
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

	private class LineStylePanel extends OptionPanel implements ILineStyleListener {
		
		LineStyleModel model;
		private Label thicknessSliderLabel;
		SliderPanel thicknessSlider;
		private Label opacitySliderLabel;
		SliderPanel opacitySlider;
		private Label popupLabel;
		LineStylePopup btnLineStyle;
		private int iconHeight = 24;
		private FlowPanel stylePanel;
		
		public LineStylePanel() {
			model = new LineStyleModel(this);
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
			//			slider.setSnapToTicks(true);
			lineThicknessPanel.add(thicknessSlider);

			thicknessSlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (true){//!slider.getValueIsAdjusting()) {
						model.applyThickness(thicknessSlider.getValue());
					}
				}});
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
			//opacitySlider.setSnapToTicks(true);
			lineOpacityPanel.add(opacitySlider);

			opacitySlider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (true){//!slider.getValueIsAdjusting()) {
						int value = (int) ((opacitySlider.getValue() / 100.0f) * 255);
						model.applyOpacity(value);
					}
				}});
			
			

			stylePanel = new FlowPanel();
			stylePanel.setStyleName("optionsPanel");
			popupLabel = new Label();
			stylePanel.add(popupLabel);
			btnLineStyle = LineStylePopup.create(getAppW(), iconHeight, -1, false);
			//			slider.setSnapToTicks(true);
			btnLineStyle.addPopupHandler(new PopupMenuHandler() {

				@Override
				public void fireActionPerformed(PopupMenuButton actionButton) {
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
			//			else {
			//				btnLineStyle.setSelectedIndex(-1);
			//			}
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



	private class AngleArcSizePanel extends OptionPanel implements ISliderListener {
		AngleArcSizeModel model;
		SliderPanel slider;
		private Label titleLabel;
		public AngleArcSizePanel() {
			model = new AngleArcSizeModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			slider = new SliderPanel(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}});
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

	private class SlopeTriangleSizePanel extends OptionPanel implements ISliderListener {
		SlopeTriangleSizeModel model;
		SliderPanel slider;
		private Label titleLabel;
		public SlopeTriangleSizePanel() {
			model = new SlopeTriangleSizeModel(this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			titleLabel = new Label();
			mainPanel.add(titleLabel);

			slider = new SliderPanel(1, 10);
			slider.setMajorTickSpacing(1);
			slider.setMinorTickSpacing(2);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//			slider.setSnapToTicks(true);
			mainPanel.add(slider);

			setWidget(mainPanel);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.applyChanges(slider.getValue());
				}});
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

		private static final long serialVersionUID = 1L;

		public IneqPanel() {
			super("ShowOnXAxis");
			setModel(new IneqStyleModel(this));
		}

		@Override
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

		TextFieldSizeModel model;
		private InputPanelW inputPanel;
		AutoCompleteTextFieldW tfSize;
		public TextFieldSizePanel() {
			model = new TextFieldSizeModel(getAppW(), this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();

			inputPanel = new InputPanelW(null, getAppW(), 1, -1, false);
			tfSize = inputPanel.getTextComponent();
			tfSize.setAutoComplete(false);
			tfSize.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					model.applyChanges(tfSize.getText());
				}	
			});
			tfSize.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyChanges(tfSize.getText());
					}
				}});
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
			//title.setText(localize("TextfieldLength"));
		}

	}


	public class ButtonSizePanel extends OptionPanel implements IButtonSizeListener {
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

			tfButtonWidth = ipButtonWidth.getTextComponent();
			tfButtonWidth.setAutoComplete(false);

			tfButtonHeight = ipButtonHeight.getTextComponent();
			tfButtonHeight.setAutoComplete(false);

			FocusListenerW focusListener = new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
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
								tfButtonHeight.getText(), cbUseFixedSize.getValue());
					}
				}

			};

			tfButtonWidth.addKeyHandler(keyHandler);
			tfButtonHeight.addKeyHandler(keyHandler);

			cbUseFixedSize.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					model.applyChanges(cbUseFixedSize.getValue());

				}});
			//tfButtonHeight.setInputVerifier(new SizeVerify());
			//tfButtonWidth.setInputVerifier(new SizeVerify());
			//tfButtonHeight.setEnabled(cbUseFixedSize.getValue());
			//tfButtonWidth..setEnabled(cbUseFixedSize.getValue());

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
		ColorFunctionModel model;
		private InputPanelW inputPanelA;
		private AutoCompleteTextFieldW tfRed, tfGreen, tfBlue, tfAlpha;
		private Label btRemove;
		private Label title;
		private Label nameLabelR, nameLabelG, nameLabelB, nameLabelA;

		ListBox cbColorSpace;
		int colorSpace = GeoElement.COLORSPACE_RGB;
		boolean allowSetComboBoxLabels = true;

		private String defaultR = "0", defaultG = "0", defaultB = "0",
				defaultA = "1";

		private Kernel kernel;
		boolean processed = false;
		public ColorFunctionPanel() {
			kernel = app.getKernel();
			model = new ColorFunctionModel(app, this);
			setModel(model);
			// non auto complete input panel
			InputPanelW inputPanelR = new InputPanelW(null, getAppW(), 1, -1, true);
			InputPanelW inputPanelG = new InputPanelW(null, getAppW(), 1, -1, true);
			InputPanelW inputPanelB = new InputPanelW(null, getAppW(), 1, -1, true);
			inputPanelA = new InputPanelW(null, getAppW(), 1, -1, true);
			tfRed = inputPanelR.getTextComponent();
			tfGreen = inputPanelG.getTextComponent();
			tfBlue = inputPanelB.getTextComponent();
			tfAlpha = inputPanelA.getTextComponent();

			

			nameLabelR = new Label();
			nameLabelG = new Label();
			nameLabelB = new Label();
			nameLabelA = new Label();

			FocusListenerW focusListener = new FocusListenerW(this){

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

				@Override
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

			btRemove = new Label();
			btRemove.addStyleName("textButton");
			btRemove.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.removeAll();
				}});

			cbColorSpace = new ListBox();
			cbColorSpace.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					colorSpace = cbColorSpace.getSelectedIndex();
					allowSetComboBoxLabels = false;
					setLabels();
					doActionPerformed();
					cbColorSpace.setSelectedIndex(colorSpace);
				}});
			
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
			//tfRed.setVisible(false);
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
			btRemove.setText(loc.getPlainTooltip("Remove"));
			//btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
		}

		void doActionPerformed() {
			processed = true;

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
			// TODO Auto-generated method stub

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

		@Override
		public void showAlpha(boolean value) {
			inputPanelA.setVisible(value);
			nameLabelA.setVisible(value);
		}

		@Override
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
		GraphicsViewLocationModel model;

		private Label title;
		CheckBox cbGraphicsView;
		CheckBox cbGraphicsView2;
		CheckBox cbGraphicsView3D;

		public GraphicsViewLocationPanel() {
			model = new GraphicsViewLocationModel(app, this);
			setModel(model);

			title = new Label();
			cbGraphicsView = new CheckBox();
			cbGraphicsView2 = new CheckBox();
			cbGraphicsView3D = new CheckBox();

			cbGraphicsView.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView1(cbGraphicsView.getValue());

				}});

			cbGraphicsView2.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView2(cbGraphicsView2.getValue());

				}});

			cbGraphicsView3D.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianView3D(cbGraphicsView3D.getValue());

				}
			});

			FlowPanel mainPanel = new FlowPanel();
			FlowPanel checkBoxPanel = new FlowPanel();
			checkBoxPanel.setStyleName("optionsPanelIndent");
			checkBoxPanel.add(cbGraphicsView);
			checkBoxPanel.add(cbGraphicsView2);
			checkBoxPanel.add(cbGraphicsView3D);
			
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

			}
		}

		@Override
		public void setLabels() {
			title.setText(app.getMenu("Location"));
			cbGraphicsView.setText(localize("DrawingPad"));
			cbGraphicsView2.setText(localize("DrawingPad2"));
			cbGraphicsView3D.setText(localize("GraphicsView3D"));

		}

		public void setCheckBox3DVisible(boolean flag) {
			cbGraphicsView3D.setVisible(flag);
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

	private class StartPointPanel extends ComboBoxPanel {
		private static final long serialVersionUID = 1L;

		public StartPointPanel() {
			super(app, "StartingPoint");
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
			ComboBoxW combo = getComboBox();
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != combo.getItemCount() - 1) {
				combo.getModel().clear();
				combo.addItem("");
				getStartPointModel().fillModes(loc);
				setFirstLabel();
			}
			return true;
		}

		@Override
		protected void onComboBoxChange(){
			final String strLoc = getComboBox().getValue();
			getStartPointModel().applyChanges(strLoc);

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

		@Override
		public void setSelectedItem(String item) {
			getComboBox().setValue(item);
		}
		
		private void setFirstLabel() {
			GeoElement p = (GeoElement)getStartPointModel().getLocateableAt(0).getStartPoint();
			if (p != null) {
				String coords = p.getLabel(StringTemplate.editTemplate); 
				getComboBox().setValue(coords);
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

	private class ImageCornerPanel extends ComboBoxPanel {
		private static final long serialVersionUID = 1L;
		private ImageCornerModel model;
		public ImageCornerPanel(int cornerIdx) {
			super(app, "CornerModel");
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
					res + ")");
		}

		@Override
		protected void onComboBoxChange() {
			final String item = getComboBox().getValue();
			model.applyChanges(item);

		}

		@Override
		public void setLabels() {
			super.setLabels();
			String strLabelStart = app.getPlain("CornerPoint");
			getLabel().setText(strLabelStart + model.getCornerNumber() + ":");
		}
		
		@Override
		public void setSelectedItem(String item) {
			getComboBox().setValue(item);
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

		@Override
		public void setLabels() {
			corner1.setLabels();
			corner2.setLabels();
			corner4.setLabels();
		}

		@Override
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

	private class InterpolateImagePanel extends CheckboxPanel {

		public InterpolateImagePanel() {
			super("Interpolate");
			setModel(new InterpolateImageModel(this));
		}

	}
	
	private class TextOptionsPanel extends OptionPanel implements ITextOptionsListener,
	ITextEditPanel, GeoElementSelectionListener {
		private static final boolean NO_DECIMALS = true;

		private static final int FontBOLD = 1;

		private static final int FontITALIC = 2;

		TextOptionsModel model;

		private Label decimalLabel;
		ListBox lbFont;
		ListBox lbSize;
		ListBox lbDecimalPlaces;
		MyToggleButton2 btnBold;
		MyToggleButton2 btnItalic;
		private ToggleButton  btnLatex;

		private FlowPanel secondLine;

		private FlowPanel editorPanel;
		private FlowPanel btnPanel;
		private Button btnOk;
		private Button btnCancel;

		private boolean secondLineVisible = false;
		GeoTextEditor editor;
		private TextEditAdvancedPanel advancedPanel;
		private GeoText orig;

		// ugly hack preventing infinite loop of update()
		private boolean redrawFromPreview;
		private static final int iconHeight = 24;
		private TextPreviewPanelW previewer; 
		public TextOptionsPanel() {
			createGUI();
		}
		
		public void createGUI() {
			model = new TextOptionsModel(app, this);
			setModel(model);
			editor = null;
			editor = new GeoTextEditor(getAppW(), this);
			editor.setStyleName("objectPropertiesTextEditor");
			lbFont = new ListBox();
			for (String item: model.getFonts()) {
				lbFont.addItem(item);
			}

			lbFont.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.setEditGeoText(editor.getText());
					model.applyFont(lbFont.getSelectedIndex() == 1);
				}});
			lbSize = new ListBox();
			for (String item : model.getFonts()) {
				lbSize.addItem(item);
			}
			lbSize.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					model.setEditGeoText(editor.getText());
					boolean isCustom = (lbSize.getSelectedIndex() == 7);
					if (isCustom) {
						String currentSize = Math
								.round(model.getTextPropertiesAt(0)
										.getFontSizeMultiplier() * 100)
										+ "%";

						GOptionPaneW.INSTANCE.showInputDialog(app,
								loc.getPlain("EnterPercentage"), currentSize,
								null, new AsyncOperation() {

							@Override
							public void callback(Object obj) {
								String[] dialogResult = (String[])obj;
								model.applyFontSizeFromString(dialogResult[1]);
							}
						});

					} else {
						model.applyFontSizeFromIndex(lbSize.getSelectedIndex());
					}
					updatePreview();
				}
			});

			// font size
			// TODO require font phrases F.S.
			// toggle buttons for bold and italic
			btnBold = new MyToggleButton2(app.getMenu("Bold.Short"));
			btnBold.addStyleName("btnBold");
			
			btnItalic = new MyToggleButton2(app.getMenu("Italic.Short"));
			btnItalic.addStyleName("btnItalic");
			
			btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
			btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

			btnLatex = new MyToggleButton2("LaTeX");
	
			// hack
//			btnLatex.getElement().getStyle().setWidth(100, Unit.PX);
			
			ClickHandler styleClick = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.setEditGeoText(editor.getText());
					model.applyFontStyle(btnBold.getValue(), btnItalic.getValue());
					updatePreview();
				}};

				btnBold.addClickHandler(styleClick);
				btnItalic.addClickHandler(styleClick);

				btnLatex.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
						model.setLaTeX(isLatex(), true);
						updatePreview();
					}});
				btnLatex.addStyleName("btnLatex");

				// decimal places
				lbDecimalPlaces = new ListBox();
				for (String item : loc.getRoundingMenu()) {
					lbDecimalPlaces.addItem(item);
				}

				lbDecimalPlaces.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
						model.setEditGeoText(editor.getText());
						model.applyDecimalPlaces(lbDecimalPlaces.getSelectedIndex());
						updatePreview();
					}});

				// font, size
				FlowPanel mainPanel = new FlowPanel();
				mainPanel.setStyleName("textPropertiesTab");
				FlowPanel firstLine = new FlowPanel();
				firstLine.setStyleName("textOptionsToolBar");
				firstLine.add(lbFont);
				firstLine.add(lbSize);
				firstLine.add(btnBold);
				firstLine.add(btnItalic);
				firstLine.add(btnLatex);

				// bold, italic
				secondLine = new FlowPanel();
				secondLine.setStyleName("optionsPanel");
				decimalLabel = new Label();
				secondLine.add(decimalLabel);
				secondLine.add(lbDecimalPlaces);

				mainPanel.add(firstLine);
				mainPanel.add(secondLine);
				secondLineVisible = true;


				editorPanel = new FlowPanel();
				editorPanel.setStyleName("optionsInput");
				editorPanel.add(editor);
				advancedPanel = new TextEditAdvancedPanel(getAppW(), this);
				redrawFromPreview = false;
				editorPanel.add(advancedPanel);
				mainPanel.add(editorPanel);

				previewer = advancedPanel.getPreviewer();

				btnPanel = new FlowPanel();
				btnPanel.setStyleName("optionsPanel");
				btnOk = new Button();
				btnPanel.add(btnOk);
				btnOk.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
						model.applyEditedGeo(editor.getText(), isLatex());
					}}); 

				btnCancel = new Button();
				btnPanel.add(btnCancel);
				btnCancel.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
						model.cancelEditGeo();
					}}); 

				mainPanel.add(btnPanel);
				setWidget(mainPanel);
				orig = null;
		}

		/**
		 * The editor must be recreated each time the options panel is
		 * re-attached to the DOM
		 */
		void reinitEditor() {

			int index = editorPanel.getWidgetIndex(editor);
			editorPanel.remove(editor);

			editor = new GeoTextEditor(getAppW(), this);
			editor.setStyleName("objectPropertiesTextEditor");
			editorPanel.insert(editor, index);
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
			advancedPanel.updateGeoList(); 
			if(getModel().hasPreview()){
				updatePreview();
				editor.updateFonts();
			}

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
			
			btnBold.setText(app.getMenu("Bold.Short"));
			btnItalic.setText(app.getMenu("Italic.Short"));
			
			btnLatex.setText(loc.getPlain("LaTeXFormula"));
			btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
			btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

			
			if (advancedPanel != null) {
				advancedPanel.setLabels();
			}
			btnOk.setText(localize("OK"));
			btnCancel.setText(localize("Cancel"));
		}

		@Override
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

		@Override
		public void setFontSizeVisibleOnly() {
			lbSize.setVisible(true);
			lbFont.setVisible(false);
			btnBold.setVisible(false);
			btnItalic.setVisible(false);
			secondLine.setVisible(false);
		}

		@Override
		public void selectSize(int index) {
			lbSize.setSelectedIndex(index);

		}

		@Override
		public void selectFont(int index) {
			lbFont.setSelectedIndex(index);

		}

		@Override
		public void selectDecimalPlaces(int index) {
			lbDecimalPlaces.setSelectedIndex(index);
		}

		@Override
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

			editorPanel.setVisible(model.isTextEditable());
			lbFont.setVisible(model.isTextEditable());
			btnLatex.setVisible(model.isTextEditable());
			btnPanel.setVisible(model.isTextEditable());

		}

		@Override
		public void updatePreview() {
			updatePreviewPanel();
		}

		boolean isLatex() {
			return btnLatex.getValue();
		}


		@Override
		public void selectFontStyle(int style) {

			btnBold.setValue(style == GFont.BOLD
					|| style == (GFont.BOLD + GFont.ITALIC));
			btnItalic.setValue(style == GFont.ITALIC
					|| style == (GFont.BOLD + GFont.ITALIC));


		}


		@Override
		public void updatePreviewPanel() {
			if (previewer == null) {
				return;
			}
			previewer.updateFonts();
			previewer.updatePreviewText(model.getEditGeo(), model.getGeoGebraString(
					editor.getDynamicTextList(), isLatex()), isLatex());
		}



		@Override
		public void setEditorText(ArrayList<DynamicTextElement> list) {

			editor.setText(list);

		}

		@Override
		public void setEditorText(String text) {

			editor.setText(text);

		}


		@Override
		public void insertGeoElement(GeoElement geo) {
			editor.insertGeoElement(geo);
		}


		@Override
		public void insertTextString(String text, boolean isLatex) {
			editor.insertTextString(text, isLatex);

		}


		@Override
		public GeoText getEditGeo() {
			return model.getEditGeo();
		}


		@Override
		public void geoElementSelected(GeoElement geo, boolean addToSelection) {
			model.cancelEditGeo();

		}


	}

	class FillingPanel extends OptionPanel implements IFillingListener {
		FillingModel model;
		SliderPanel opacitySlider;
		SliderPanel angleSlider;
		SliderPanel distanceSlider;
		private Label fillingSliderTitle;
		private Label angleSliderTitle;
		private Label distanceSliderTitle;

		private FlowPanel opacityPanel, hatchFillPanel, imagePanel,
		anglePanel, distancePanel;
		private Label lblFillType;
		private Label lblSelectedSymbol;
		private Label lblMsgSelected;
		private Button btnOpenFile;

		private PopupMenuButton btnImage;
		// button for removing turtle's image
		private PushButton btnClearImage;
		private Label lblFillInverse;
		private Label lblSymbols;
		ArrayList<ImageResource> iconList;
		private ArrayList<String> iconNameList;
		//	private PopupMenuButton btInsertUnicode;

		ListBox lbFillType;
		CheckBox cbFillInverse;
		private FlowPanel mainWidget;
		private FlowPanel fillTypePanel;
		private Label fillTypeTitle;
		private Label fillingMin;
		private FlowPanel btnPanel;
		AutoCompleteTextFieldW tfInsertUnicode;
		private InputPanelW unicodePanel;

		private class MyImageFileInputDialog extends FileInputDialog{

			private MyImageFileInputDialog myDialog;
			public MyImageFileInputDialog(AppW app, GeoPoint location) {
				super(app, location);
				createGUI();
			}

			@Override
			protected void createGUI() {
				super.createGUI();
				addGgbChangeHandler(getInputWidget().getElement(), getAppW());
			}

			public native void addGgbChangeHandler(Element el, AppW appl) /*-{
				var dialog = this;
				appl = this;
				el.setAttribute("accept", "image/*");
				el.onchange = function(event) {
					var files = this.files;
					if (files.length) {
						var fileTypes = /^image.*$/;
						for (var i = 0, j = files.length; i < j; ++i) {
							if (!files[i].type.match(fileTypes)) {
								continue;
							}
							var fileToHandle = files[i];
							appl.@org.geogebra.web.web.gui.dialog.options.OptionsObjectW.FillingPanel.MyImageFileInputDialog::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle,
							 dialog.@org.geogebra.web.web.gui.dialog.FileInputDialog::getNativeHideAndFocus()());				
							break
						}
					}
				};
			}-*/;

			@Override
			public void onClick(ClickEvent event) {
				if (event.getSource() == btCancel) {
					hideAndFocus();
				}
			}
			public native boolean openFileAsImage(JavaScriptObject fileToHandle,
					JavaScriptObject callback) /*-{

				var imageRegEx = /\.(png|jpg|jpeg|gif|bmp|svg)$/i;
				if (!fileToHandle.name.toLowerCase().match(imageRegEx))
					return false;

				var appl = this;
				var reader = new FileReader();
				reader.onloadend = function(ev) {
					if (reader.readyState === reader.DONE) {
						var fileData = reader.result;
						var fileName = fileToHandle.name;
						appl.@org.geogebra.web.web.gui.dialog.options.OptionsObjectW.FillingPanel.MyImageFileInputDialog::applyImage(Ljava/lang/String;Ljava/lang/String;)(fileName, fileData);
						if (callback != null) {
							callback();
						}
					}
				};
				reader.readAsDataURL(fileToHandle);
				return true;
			}-*/;

			public void applyImage(String fileName, String fileData) {
				MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
				String zip_directory = md5e.encrypt(fileData);

				String fn = fileName;
				int index = fileName.lastIndexOf('/');
				if (index != -1) {
					fn = fn.substring(index + 1, fn.length()); // filename without
				}
				// path
				fn = org.geogebra.common.util.Util.processFilename(fn);

				// filename will be of form
				// "a04c62e6a065b47476607ac815d022cc\liar.gif"
				fileName = zip_directory + '/' + fn;

				Construction cons = getAppW().getKernel().getConstruction();
				getAppW().getImageManager().addExternalImage(fileName,
						fileData);
				GeoImage geoImage = new GeoImage(cons);
				getAppW().getImageManager().triggerSingleImageLoading(
						fileName, geoImage);
				model.applyImage(fileName);
				App.debug("Applying " + fileName + " from dialog");

			}

		}

		public FillingPanel() {
			model = new FillingModel(getAppW(), this);
			setModel(model);
			mainWidget = new FlowPanel();
			fillTypePanel = new FlowPanel();
			fillTypePanel.setStyleName("optionsPanel");
			fillTypeTitle = new Label();
			lbFillType = new ListBox();

			fillTypePanel.add(fillTypeTitle);
			fillTypePanel.add(lbFillType);

			cbFillInverse = new CheckBox();
			fillTypePanel.add(cbFillInverse);
			lbFillType.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					model.applyFillType(lbFillType.getSelectedIndex());
				}});

			cbFillInverse.addClickHandler(new ClickHandler(){
				private DecoAnglePanel decoAnglePanel;

				@Override
				public void onClick(ClickEvent event) {
					model.applyFillingInverse(cbFillInverse.getValue());
				}});

			FlowPanel panel = new FlowPanel();
			panel.add(fillTypePanel);

			unicodePanel = new InputPanelW(null, getAppW(), 1, -1, true);
			tfInsertUnicode = unicodePanel.getTextComponent();
			//buildInsertUnicodeButton();
			unicodePanel.setVisible(false);
			tfInsertUnicode.setStyleName("fillSymbol");
			lblMsgSelected = new Label(loc.getMenu("Filling.CurrentSymbol")
					+ ":");
			lblMsgSelected.setVisible(false);
			fillingPanel = this;
			lblSymbols = new Label(app.getMenu("Filling.Symbol") + ":");
			lblSymbols.setVisible(false);
			lblSelectedSymbol = new Label();

			opacitySlider = new SliderPanel(0, 100);
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);

			angleSlider = new SliderPanel(0, 180);
			angleSlider.setMajorTickSpacing(45);
			angleSlider.setMinorTickSpacing(5);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);

			distanceSlider = new SliderPanel(5, 50);
			// distanceSlider.setPreferredSize(new Dimension(150,50));
			distanceSlider.setMajorTickSpacing(10);
			distanceSlider.setMinorTickSpacing(5);
			distanceSlider.setPaintTicks(true);
			distanceSlider.setPaintLabels(true);
			
			FlowPanel symbol1Panel = new FlowPanel();
			symbol1Panel.setStyleName("optionsPanelCell");
			symbol1Panel.add(lblSymbols);
			symbol1Panel.add(tfInsertUnicode);
			FlowPanel symbol2Panel = new FlowPanel();
			symbol2Panel.setStyleName("optionsPanelCell");
			symbol2Panel.add(lblMsgSelected);
			symbol2Panel.add(lblSelectedSymbol);
			
			FlowPanel symbolPanel = new FlowPanel();
			symbolPanel.setStyleName("optionsPanelIndent");
			symbolPanel.add(symbol1Panel);
			symbolPanel.add(symbol2Panel);
			lblSelectedSymbol.setVisible(false);
			panel.add(symbolPanel);
			// panels to hold sliders
			opacityPanel = new FlowPanel();
			opacityPanel.setStyleName("optionsPanelIndent");
			fillingSliderTitle = new Label();
			opacityPanel.add(fillingSliderTitle);
			opacityPanel.add(opacitySlider);

			anglePanel = new FlowPanel();
			anglePanel.setStyleName("optionsPanelIndent");
			angleSliderTitle = new Label();
			anglePanel.add(angleSliderTitle);
			anglePanel.add(angleSlider);

			distanceSliderTitle = new Label();
			distancePanel = new FlowPanel();
			distancePanel.setStyleName("optionsPanelIndent");
			distancePanel.add(distanceSliderTitle);
			distancePanel.add(distanceSlider);

			// hatchfill panel: only shown when hatch fill option is selected
			hatchFillPanel = new FlowPanel();
			hatchFillPanel.add(anglePanel);
			hatchFillPanel.add(distancePanel);
			hatchFillPanel.setVisible(false);

			// image panel: only shown when image fill option is selected
			createImagePanel();
			imagePanel.setVisible(false);

			// ===========================================================
			// put all the sub panels together

			mainWidget.add(panel);
			mainWidget.add(opacityPanel);
			mainWidget.add(hatchFillPanel);
			mainWidget.add(imagePanel);

			mainWidget.add(symbolPanel);
			setWidget(mainWidget);

			opacitySlider.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					model.applyOpacity(opacitySlider.getValue());
				}});

			ChangeHandler angleAndDistanceHandler = new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					model.applyAngleAndDistance(angleSlider.getValue(),
							distanceSlider.getValue());

				}};;


				angleSlider.addChangeHandler(angleAndDistanceHandler);
				distanceSlider.addChangeHandler(angleAndDistanceHandler);

				tfInsertUnicode.addFocusListener(new FocusListenerW(this){
					@Override
					protected void wrapFocusLost(){
						String symbolText = tfInsertUnicode.getText();
						if (symbolText.isEmpty()) {
							return;
						}
						model.applyUnicode(symbolText);
					}	
				});

				tfInsertUnicode.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
						if (e.isEnterKey()) {
							String symbolText = tfInsertUnicode.getText();
							model.applyUnicode(symbolText);
						}
					}});


				setLabels();
		}

		protected String getImageFileName(String fileName, String fileData) {


			MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
			String zip_directory = md5e.encrypt(fileData);

			String fn = fileName;
			int index = fileName.lastIndexOf('/');
			if (index != -1) {
				fn = fn.substring(index + 1, fn.length()); // filename without
			}
			fn = org.geogebra.common.util.Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc\liar.gif"
			return zip_directory + '/' + fn;
		}

		public void applyImage(String fileName, String fileData) {

			fileName = getImageFileName(fileName, fileData);

			Construction cons = getAppW().getKernel().getConstruction();
			getAppW().getImageManager().addExternalImage(fileName,
					fileData);
			GeoImage geoImage = new GeoImage(cons);
			getAppW().getImageManager().triggerSingleImageLoading(
					fileName, geoImage);
			model.applyImage(fileName);

		}


		private void createImagePanel() {
			imagePanel = new FlowPanel();
			btnPanel = new FlowPanel();
			iconList = new ArrayList<ImageResource>();
			iconList.add(null); // for delete
			AppResources res = AppResources.INSTANCE;
			iconList.add(res.go_down());
			iconList.add(res.go_up());
			iconList.add(res.go_previous());
			iconList.add(res.go_next());
			iconList.add(res.nav_fastforward());
			iconList.add(res.nav_rewind());
			iconList.add(res.nav_skipback());
			iconList.add(res.nav_skipforward());
			iconList.add(res.nav_play());
			iconList.add(res.nav_pause());

			iconList.add(res.exit());

			iconNameList = new ArrayList<String>();
			for (ImageResource ir: iconList) {

				iconNameList.add(ir != null ? ir.getName() : "");
			}

			final ImageOrText[] iconArray = new ImageOrText[iconList.size()];
			iconArray[0] = GeoGebraIcon.createNullSymbolIcon(24, 24);
			for (int i = 1; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createResourceImageIcon(iconList
				        .get(i));
			}
			//			// ============================================
			//
			//			// panel for button to open external file
			//
			btnImage = new PopupMenuButton(getAppW(), iconArray, -1, 4,
			        org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent(){
					super.handlePopupActionEvent();
					ImageResource res = null;
					int idx = getSelectedIndex();
					res = iconList.get(idx);
					if (res != null) {
						applyImage(res.getName(), res.getSafeUri().asString());
						App.debug("Applying " + res.getName() + " at index " + idx);
					}
					else {
						model.applyImage("");
					}
				}

			};
			btnImage.setSelectedIndex(-1);
			btnImage.setKeepVisible(false);
			btnClearImage = new PushButton(new Image(AppResources.INSTANCE.delete_small()));
			btnClearImage.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					model.applyImage("");
                }
				
			});
			btnOpenFile = new Button();
			btnOpenFile.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					MyImageFileInputDialog dialog = new MyImageFileInputDialog(getAppW(), null);
				}
			});


			btnPanel.add(btnImage);
			btnPanel.add(btnClearImage);
			btnPanel.add(btnOpenFile);
			btnPanel.setStyleName("optionsPanelIndent");

			imagePanel.add(btnPanel);

		}





		@Override
		public void setStandardFillType() {
			fillTypePanel.setVisible(true);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(false);
			imagePanel.setVisible(false);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);
		}

		@Override
		public void setHatchFillType() {
			fillTypePanel.setVisible(true);
			distanceSlider.setMinimum(5);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			angleSlider.setMaximum(180);
			angleSlider.setMinorTickSpacing(5);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);
		}

		@Override
		public void setCrossHatchedFillType() {
			fillTypePanel.setVisible(true);
			distanceSlider.setMinimum(5);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			// Only at 0, 45 and 90 degrees texturepaint not have mismatches
			angleSlider.setMaximum(45);
			angleSlider.setMinorTickSpacing(45);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);

		}

		@Override
		public void setBrickFillType() {
			fillTypePanel.setVisible(true);
			distanceSlider.setMinimum(5);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			anglePanel.setVisible(true);
			angleSlider.setMaximum(180);
			angleSlider.setMinorTickSpacing(45);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);
		}

		@Override
		public void setSymbolFillType() {
			fillTypePanel.setVisible(true);
			distanceSlider.setMinimum(10);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			// for dotted angle is useless
			anglePanel.setVisible(false);
			lblSymbols.setVisible(true);
			lblSelectedSymbol.setVisible(true);
			unicodePanel.setVisible(true);
			tfInsertUnicode.showPopupSymbolButton(true);
		}

		@Override
		public void setDottedFillType() {
			distanceSlider.setMinimum(5);
			opacityPanel.setVisible(false);
			hatchFillPanel.setVisible(true);
			imagePanel.setVisible(false);
			// for dotted angle is useless
			anglePanel.setVisible(false);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);
		}

		@Override
		public void setImageFillType() {
			fillTypePanel.setVisible(true);
			opacityPanel.setVisible(true);
			hatchFillPanel.setVisible(false);
			imagePanel.setVisible(true);
			lblSymbols.setVisible(false);
			lblSelectedSymbol.setVisible(false);
			unicodePanel.setVisible(false);
			this.btnImage.setVisible(true);
			this.btnClearImage.setVisible(true);

			// for GeoButtons only show the image file button
			if (model.hasGeoButton() || model.hasGeoTurtle()) {
				fillTypePanel.setVisible(false);
				opacityPanel.setVisible(false);
				if (lblFillType != null) {
					lblFillType.setVisible(false);
					lbFillType.setVisible(false);
				}
				if(model.hasGeoTurtle()){
					this.btnImage.setVisible(false);
					this.btnClearImage.setVisible(true);
				}
			}

			addSelectionBar();
		}


		private void addSelectionBar() {
			// TODO Auto-generated method stub

		}

//		@Override
//		public boolean update(Object[] geos) {
//			getModel().setGeos(geos);
//
//			if (!getModel().checkGeos()) {
//				return false;
//			}
//			model.updateProperties();
//
//			return true;
//		}

		@Override
		public void setSelectedIndex(int index) {
			lbFillType.setSelectedIndex(index);
		}

		@Override
		public void addItem(String item) {
			lbFillType.addItem(item);
		}

		public void updateFillTypePanel(FillType fillType) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setFillInverseVisible(boolean isVisible) {
			cbFillInverse.setVisible(isVisible);
		}

		@Override
		public void setFillTypeVisible(boolean isVisible) {
			lbFillType.setVisible(isVisible);  
		}

		@Override
		public void setLabels() {
			fillTypeTitle.setText(localize("Filling") + ":");
			cbFillInverse.setText(localize("InverseFilling"));
			int idx = lbFillType.getSelectedIndex();
			lbFillType.clear();
			model.fillModes(loc);
			lbFillType.setSelectedIndex(idx);
			fillingSliderTitle.setText(localize("Opacity"));
			angleSliderTitle.setText(localize("Angle"));
			distanceSliderTitle.setText(localize("Spacing"));
			btnOpenFile.setText(localize("ChooseFromFile") + "...");

		}


		@Override
		public void setSelectedItem(String item) {
			int idx = 0;
			lbFillType.setSelectedIndex(idx);
		}

		@Override
		public void setSymbolsVisible(boolean isVisible) {

			if (isVisible) {
				unicodePanel.setVisible(true);
				lblSymbols.setVisible(true);
				lblSelectedSymbol.setVisible(true);
				lblMsgSelected.setVisible(true);
			} else {
				lblSymbols.setVisible(false);
				unicodePanel.setVisible(false);
				lblMsgSelected.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				lblSelectedSymbol.setText("");
			}
		}

		@Override
		public void setFillingImage(String imageFileName) {

			int itemIndex = -1;
			if (imageFileName != null) {
				String fileName = imageFileName.substring(imageFileName.indexOf('/') + 1);
				App.debug("Filling with " + fileName);

				int idx = iconNameList.lastIndexOf(fileName);
				itemIndex = idx > 0 ? idx : 0;
			}

			btnImage.setSelectedIndex(itemIndex);

		}

		@Override
		public void setFillValue(int value) {
			opacitySlider.setValue(value);
		}

		@Override
		public void setAngleValue(int value) {
			angleSlider.setValue(value);
		}

		@Override
		public void setDistanceValue(int value) {
			distanceSlider.setValue(value);
		}

		@Override
		public int getSelectedBarIndex() {
			return 0;
		}

		@Override
		public void selectSymbol(String symbol) {
			lblSelectedSymbol.setText(symbol);
		}

		@Override
		public String getSelectedSymbolText() {
			return lblSelectedSymbol.getText();
		}

		@Override
		public float getFillingValue() {
			return opacitySlider.getValue();
		}

		@Override
		public FillType getSelectedFillType() {
			return model.getFillTypeAt(lbFillType.getSelectedIndex());
		}

		@Override
		public int getDistanceValue() {
			return distanceSlider.getValue();
		}

		@Override
		public int getAngleValue() {
			return angleSlider.getValue();
		}

		@Override
		public void setFillInverseSelected(boolean value) {
			cbFillInverse.setValue(value);
		}
	}

	private class DecoAnglePanel extends OptionPanel implements IDecoAngleListener {
		private Label decoLabel;
		private PopupMenuButton decoPopup;
		DecoAngleModel model;
		public DecoAnglePanel() {
			model = new DecoAngleModel(this);
			setModel(model);
			FlowPanel mainWidget = new FlowPanel();
			decoLabel = new Label();
			mainWidget.add(decoLabel);
			final ImageOrText[] iconArray = new ImageOrText[DecoAngleModel.getDecoTypeLength()];
			GDimensionW iconSize = new GDimensionW(80, 30);
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createDecorAngleIcon(i);
			}
			decoPopup = new PopupMenuButton(getAppW(), iconArray, -1, 1,
			        org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent(){
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
			angleArcSizePanel.setMinValue();
		}

	}

	private class DecoSegmentPanel extends OptionPanel implements IComboListener {
		private Label decoLabel;
		private PopupMenuButton decoPopup;
		DecoSegmentModel model;
		public DecoSegmentPanel() {
			model = new DecoSegmentModel(this);
			setModel(model);
			FlowPanel mainWidget = new FlowPanel();
			decoLabel = new Label();
			mainWidget.add(decoLabel);
			final ImageOrText[] iconArray = new ImageOrText[DecoSegmentModel.getDecoTypeLength()];
			GDimensionW iconSize = new GDimensionW(130,	app.getGUIFontSize() + 6);
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createDecorSegmentIcon(i);
			}
			decoPopup = new PopupMenuButton(getAppW(), iconArray, -1, 1,
			        org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent(){
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

	private class ScriptEditPanel extends OptionPanel {

		private static final long serialVersionUID = 1L;
		private ScriptInputPanelW clickDialog, updateDialog, globalDialog;
		private TabPanel tabbedPane;
		private FlowPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;

		public ScriptEditPanel() {
			int row = 35;
			int column = 15;

			tabbedPane = new TabPanel();
			tabbedPane.setStyleName("scriptTabPanel");

			clickDialog = new ScriptInputPanelW(getAppW(), app.getPlain("Script"),
					null, row, column, false, false);
			updateDialog = new ScriptInputPanelW(getAppW(),
					app.getPlain("JavaScript"), null, row, column, true, false);
			globalDialog = new ScriptInputPanelW(getAppW(),
					app.getPlain("GlobalJavaScript"), null, row, column, false,
					true);
			// add(td.getInputPanel(), BorderLayout.NORTH);
			// add(td2.getInputPanel(), BorderLayout.CENTER);
			clickScriptPanel = new FlowPanel();
			clickScriptPanel.add(clickDialog.getInputPanel(row, column, true));
			clickScriptPanel
			.add(clickDialog.getButtonPanel());

			updateScriptPanel = new FlowPanel();
			updateScriptPanel.add(
					updateDialog.getInputPanel(row, column, true));
			updateScriptPanel.add(updateDialog.getButtonPanel());

			globalScriptPanel = new FlowPanel();
			globalScriptPanel.add(globalDialog.getInputPanel(row, column, true));
			globalScriptPanel.add(globalDialog.getButtonPanel());
			setWidget(tabbedPane);

			tabPanel.addSelectionHandler(new SelectionHandler<Integer>() 
					{			
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					applyModifications();

				}
			});
			
			setLabels();
		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			clickDialog.applyModifications();
			updateDialog.applyModifications();
			globalDialog.applyModifications();
		}

		@Override
		public void setLabels() {
			// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
			String ok = localize("OK");
			String cancel = localize("Cancel");
			
			clickDialog.setLabels(ok, cancel);
			updateDialog.setLabels(ok, cancel);
			globalDialog.setLabels(ok, cancel);
		}

		@Override
		public boolean update(Object[] geos) {
			if (geos.length != 1){
				return false;
			}

			// remember selected tab
			int idx = tabbedPane.getTabBar().getSelectedTab();

			GeoElement geo = (GeoElement) geos[0];
			clickDialog.setGeo(geo);
			updateDialog.setGeo(geo);
			globalDialog.setGlobal();
			tabbedPane.clear();
			if (geo.canHaveClickScript())
				tabbedPane.add(clickScriptPanel, localize("OnClick"));
			if (geo.canHaveUpdateScript())
				tabbedPane.add(updateScriptPanel, localize("OnUpdate"));
			tabbedPane.add(globalScriptPanel, localize("GlobalJavaScript"));

			// select tab as before
			tabbedPane.selectTab(Math.max(0,	idx));
			return true;
		}

		private boolean checkGeos(Object[] geos) {
			// return geos.length == 1 && geos[0] instanceof
			// GeoJavaScriptButton;
			return geos.length == 1;
		}



		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	private class ExtendedAVPanel extends CheckboxPanel {
		public ExtendedAVPanel() {
			super("show extended algebra view");
			setModel(new ExtendedAVModel(this));
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

	AppW getAppW() {
		return (AppW) app;
	}

	private void initGUI() {
		wrappedPanel = new FlowPanel();
		wrappedPanel.setStyleName("propertiesPanel");
		tabPanel = new TabPanel();

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() 
				{			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
	//			updateGUI();
				((PropertiesViewW)app.getGuiManager().getPropertiesView()).updatePropertiesView();
			}
				});
		tabPanel.setStyleName("propertiesTabPanel");

		createBasicTab();

		if (!(app.isExam())) {
			tabs = Arrays.asList(
				basicTab,
				addTextTab(),
				addSliderTab(),
				addColorTab(),
				addStyleTab(),
				addPositionTab(),
				addAdvancedTab(),
				addAlgebraTab(),
				addScriptTab());
		} else {
			tabs = Arrays.asList(
					basicTab,
					addTextTab(),
					addSliderTab(),
					addColorTab(),
					addStyleTab(),
					addPositionTab(),
					addAdvancedTab(),
					addAlgebraTab());
		}

		for (OptionsTab tab: tabs) {
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
		basicTab = new OptionsTab("Properties.Basic");

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
			reflexAnglePanel.getWidget().setStyleName("optionsPanel");
		}

		listAsComboPanel = new ListAsComboPanel();
		rightAnglePanel = new RightAnglePanel();
		trimmedIntersectionLinesPanel = new ShowTrimmedIntersectionLinesPanel();

		//		tabList.add(comboBoxPanel);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		basicTab.add(allowOutlyingIntersectionsPanel.getWidget());

		fixCheckboxPanel = new FixCheckboxPanel();
		basicTab.add(fixCheckboxPanel.getWidget());

		if (app.isPrerelease() && RadioButtonTreeItem.showSliderOrTextBox) {
			if (!isDefaults) {
				avPanel = new ExtendedAVPanel();
				checkboxPanel.add(avPanel.getWidget());
			}

			basicTab.addPanelList(Arrays.asList(namePanel, showObjectPanel,
					tracePanel, labelPanel, fixPanel, auxPanel, animatingPanel,
					bgImagePanel, reflexAnglePanel, rightAnglePanel,
					listAsComboPanel, trimmedIntersectionLinesPanel,
					allowOutlyingIntersectionsPanel, fixCheckboxPanel, avPanel));
		} else {
			basicTab.addPanelList(Arrays.asList(namePanel, showObjectPanel,
					tracePanel, labelPanel, fixPanel, auxPanel, animatingPanel,
					bgImagePanel, reflexAnglePanel, rightAnglePanel,
					listAsComboPanel, trimmedIntersectionLinesPanel,
					allowOutlyingIntersectionsPanel, fixCheckboxPanel));
		}

	}

	private OptionsTab addTextTab() {
		OptionsTab tab = new OptionsTab("Text");
		textOptionsPanel = new TextOptionsPanel();
		tab.add(textOptionsPanel);
		return tab;
	}

	private OptionsTab addSliderTab() {
		OptionsTab tab = new OptionsTab("Slider");
		SliderPanelW sliderPanel = new SliderPanelW(getAppW(), false, true);
		tab.add(sliderPanel);
		return tab;
	}

	private OptionsTab addColorTab() {
		OptionsTab tab = new OptionsTab("Color");
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
		fillingPanel = new FillingPanel();
		lodPanel = new LodPanel();
		interpolateImagePanel = new InterpolateImagePanel();
		decoAnglePanel = new DecoAnglePanel();
		decoAnglePanel.getWidget().setStyleName("optionsPanel");
		decoSegmentPanel = new DecoSegmentPanel();
		decoSegmentPanel.getWidget().setStyleName("optionsPanel");
		
		tab.addPanelList(Arrays.asList(pointSizePanel,
				pointStylePanel,
				lineStylePanel,
				angleArcSizePanel,
				slopeTriangleSizePanel,
				ineqStylePanel,
				buttonSizePanel,
				textFieldSizePanel,
		        fillingPanel, lodPanel,
				interpolateImagePanel,
				decoAnglePanel,
				decoSegmentPanel
				));
		return tab;
	}

	private OptionsTab addScriptTab() {
		OptionsTab tab = new OptionsTab("Scripting");
		ScriptEditPanel scriptEditPanel = new ScriptEditPanel();
		tab.add(scriptEditPanel);
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

		tab.add(showConditionPanel);
		tab.add(colorFunctionPanel);
		GroupOptionsPanel misc = new GroupOptionsPanel("Miscellaneous", loc); 
		misc.add(layerPanel);
		misc.add(tooltipPanel);
		misc.add(selectionAllowedPanel);
		tab.add(misc);
		tab.add(graphicsViewLocationPanel);

		return tab;
	}

	private OptionsTab addAlgebraTab() {
		OptionsTab tab;
		coordsPanel = new CoordsPanel();
		lineEqnPanel = new LineEqnPanel();
		conicEqnPanel = new ConicEqnPanel();

		tab = new OptionsTab("Properties.Algebra");
		tab.addPanelList(
Arrays.asList(coordsPanel,
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

	public void reinit() {
		textOptionsPanel.reinitEditor();
		updateGUI();
	}

	@Override
	public void updateGUI() {
		App.debug("OPTION OBJECTS UPDATE_GUI");
		loc = app.getLocalization();
		Object[] geos = app.getSelectionManager().getSelectedGeos().toArray();

		if (geos != null && geos.length != 0) {
			wrappedPanel.setVisible(true);

			// app.setShowAuxiliaryObjects(true);

			for (OptionsTab tab: tabs) {
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
		tabPanel.selectTab(index < 0 ? 0: index);	    
	}

	public void openFileAsImage(String fileName) {
		App.debug(fileName);

	}

	public void updateIfInSelection(GeoElement geo) {

		if (selection != null && selection.size() == 1
				&& selection.contains(geo)) {
			updateGUI();
		}
	}

	@Override
    public void onResize(int height, int width) {
		for (OptionsTab tab: tabs) {
			tab.onResize(height, width);
		}
    }
	
}