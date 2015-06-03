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
import org.geogebra.common.gui.dialog.options.OptionsObject;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.AnimatingModel;
import org.geogebra.common.gui.dialog.options.model.AuxObjectModel;
import org.geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import org.geogebra.common.gui.dialog.options.model.ButtonSizeModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel.IColorFunctionListener;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.CoordsModel;
import org.geogebra.common.gui.dialog.options.model.DecoAngleModel;
import org.geogebra.common.gui.dialog.options.model.DecoSegmentModel;
import org.geogebra.common.gui.dialog.options.model.FillingModel;
import org.geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel;
import org.geogebra.common.gui.dialog.options.model.GraphicsViewLocationModel.IGraphicsViewLocationListener;
import org.geogebra.common.gui.dialog.options.model.GroupModel;
import org.geogebra.common.gui.dialog.options.model.ImageCornerModel;
import org.geogebra.common.gui.dialog.options.model.IneqStyleModel;
import org.geogebra.common.gui.dialog.options.model.InterpolateImageModel;
import org.geogebra.common.gui.dialog.options.model.LayerModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel;
import org.geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import org.geogebra.common.gui.dialog.options.model.LodModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import org.geogebra.common.gui.dialog.options.model.PointSizeModel;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
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
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.GeoTextEditor;
import org.geogebra.web.html5.gui.inputfield.ITextEditPanel;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.web.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.web.gui.dialog.TextPreviewPanelW;
import org.geogebra.web.web.gui.dialog.options.OptionsTab.LodPanel;
import org.geogebra.web.web.gui.dialog.options.model.ExtendedAVModel;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.properties.AnimationSpeedPanelW;
import org.geogebra.web.web.gui.properties.AnimationStepPanelW;
import org.geogebra.web.web.gui.properties.ComboBoxPanel;
import org.geogebra.web.web.gui.properties.GroupOptionsPanel;
import org.geogebra.web.web.gui.properties.ListBoxPanel;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.properties.PropertiesViewW;
import org.geogebra.web.web.gui.properties.SliderPanelW;
import org.geogebra.web.web.gui.util.ComboBoxW;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.gui.view.algebra.RadioButtonTreeItem;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("javadoc")
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
	private ExtendedAVPanel avPanel;

	// Style
	// FillingPanel fillingPanel;

	//Advanced
	private ShowConditionPanel showConditionPanel;
	final boolean isDefaults;
	private ColorFunctionPanel colorFunctionPanel;
	private LayerPanel layerPanel;
	private TooltipPanel tooltipPanel;
	private SelectionAllowedPanel selectionAllowedPanel;
	private GraphicsViewLocationPanel graphicsViewLocationPanel;

	//Decoration

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




	private class ShowObjectPanel extends CheckboxPanel implements IShowObjectListener {
		public ShowObjectPanel() {
			super("ShowObject", loc);
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
			super("ShowTrace", loc);
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

			labelMode = new ListBox();
			labelMode.setMultipleSelect(false);

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
			super("FixObject", loc);
			setModel(new FixObjectModel(this));
		}
	}

	private class AuxPanel extends CheckboxPanel {

		public AuxPanel() {
			super("AuxiliaryObject", loc);
			setModel(new AuxObjectModel(this));
		}

	}

	private class ShowConditionPanel extends OptionPanel implements	IShowConditionListener {



		private ShowConditionModel model;
		private Label title;
		private AutoCompleteTextFieldW tfCondition;

		boolean processed;

		public ShowConditionPanel() {
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




	private class NamePanel extends OptionPanel implements IObjectNameListener {



		ObjectNameModel model;
		AutoCompleteTextFieldW tfName, tfDefinition, tfCaption;

		private Label nameLabel, defLabel, captionLabel;
		private InputPanelW inputPanelName, inputPanelDef, inputPanelCap;

		private FlowPanel mainWidget;
		private FlowPanel nameStrPanel;
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
			nameStrPanel = new FlowPanel();
			nameLabel = new Label();
			//inputPanelName.insert(nameLabel, 0);

			nameStrPanel.add(nameLabel);
			nameStrPanel.add(inputPanelName);
			mainWidget.add(nameStrPanel);

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

			nameStrPanel.setStyleName("optionsInput");
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
			mainWidget.clear();

//			if (loc.isRightToLeftReadingOrder()) {
//				mainWidget.add(inputPanelName);
//				mainWidget.add(nameLabel);
//			} else {
//				mainWidget.add(nameLabel);
//				mainWidget.add(inputPanelName);
//			}
			mainWidget.add(nameStrPanel);

			if (showDefinition) {
				// if (loc.isRightToLeftReadingOrder()) {
//					mainWidget.add(inputPanelDef);
//					mainWidget.add(defLabel);
//				} else {
//					mainWidget.add(defLabel);
//					mainWidget.add(inputPanelDef);
//				}
				mainWidget.add(defPanel);
			}

			if (showCaption) {
				// if (loc.isRightToLeftReadingOrder()) {
//					mainWidget.add(inputPanelCap);
//					mainWidget.add(captionLabel);
//				} else {
//					mainWidget.add(captionLabel);
//					mainWidget.add(inputPanelCap);
//				}
				mainWidget.add(captionPanel);
			}

			//app.setComponentOrientation(this);


		}

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
			tfDefinition.setText(ObjectNameModel.getDefText(geo));

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
			super("BackgroundImage", loc);
			setModel(new BackgroundImageModel(this));
		}

	}

	class ListAsComboPanel extends CheckboxPanel implements IListAsComboListener {
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
			model = new ReflexAngleModel(app, isDefaults);
			model.setListener(this);
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




	class RightAnglePanel extends CheckboxPanel {
		public RightAnglePanel() {
			super("EmphasizeRightAngle", loc);
			setModel(new RightAngleModel(this));

		}
	}

	private class ShowTrimmedIntersectionLinesPanel extends CheckboxPanel {


		public ShowTrimmedIntersectionLinesPanel() {
			super("ShowTrimmed", loc);
			setModel(new TrimmedIntersectionLinesModel(this));
		}

	} // ShowTrimmedIntersectionLines

	private class AnimatingPanel extends CheckboxPanel {
		public AnimatingPanel() {
			super("Animating", loc);
			setModel(new AnimatingModel(app, this));
		}

	}

	private class AllowOutlyingIntersectionsPanel extends CheckboxPanel {

		public AllowOutlyingIntersectionsPanel() {
			super("allowOutlyingIntersections", loc);
			setModel(new OutlyingIntersectionsModel(this));
		}

	}

	private class FixCheckboxPanel extends CheckboxPanel {

		public FixCheckboxPanel() {
			super("FixCheckbox", loc);
			setModel(new FixCheckboxModel(this));
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

		boolean processed = false;
		public ColorFunctionPanel() {
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


		public SelectionAllowedPanel() {
			super("SelectionAllowed", loc);
			setModel(new SelectionAllowedModel(this));
		}

	}

	private class TooltipPanel extends ListBoxPanel {


		public TooltipPanel() {
			super(loc, "Tooltip");
			TooltipModel model = new TooltipModel();
			model.setListener(this);
			setModel(model);
		}
	}

	private class LayerPanel extends ListBoxPanel {


		public LayerPanel() {
			super(loc, "Layer");
			LayerModel model = new LayerModel();
			model.setListener(this);
			setModel(model);
		}
	}

	private class GraphicsViewLocationPanel extends OptionPanel implements IGraphicsViewLocationListener {
		GraphicsViewLocationModel model;

		private Label title;
		CheckBox cbGraphicsView;
		CheckBox cbGraphicsView2;
		CheckBox cbGraphicsView3D;
		CheckBox cbGraphicsViewForPlane;

		public GraphicsViewLocationPanel() {
			model = new GraphicsViewLocationModel(app, this);
			setModel(model);

			title = new Label();
			cbGraphicsView = new CheckBox();
			cbGraphicsView2 = new CheckBox();
			cbGraphicsView3D = new CheckBox();
			cbGraphicsViewForPlane = new CheckBox();

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

			cbGraphicsViewForPlane.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					model.applyToEuclidianViewForPlane(cbGraphicsViewForPlane
							.getValue());

				}
			});

			FlowPanel mainPanel = new FlowPanel();
			FlowPanel checkBoxPanel = new FlowPanel();
			checkBoxPanel.setStyleName("optionsPanelIndent");
			checkBoxPanel.add(cbGraphicsView);
			checkBoxPanel.add(cbGraphicsView2);
			checkBoxPanel.add(cbGraphicsView3D);
			checkBoxPanel.add(cbGraphicsViewForPlane);
			
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

			}
		}

		@Override
		public void setLabels() {
			title.setText(app.getMenu("Location"));
			cbGraphicsView.setText(localize("DrawingPad"));
			cbGraphicsView2.setText(localize("DrawingPad2"));
			cbGraphicsView3D.setText(localize("GraphicsView3D"));
			cbGraphicsViewForPlane.setText(localize("ExtraViews"));

		}

		public void setCheckBox3DVisible(boolean flag) {
			cbGraphicsView3D.setVisible(flag);
		}

		public void setCheckBoxForPlaneVisible(boolean flag) {
			cbGraphicsViewForPlane.setVisible(flag);
		}


	}

	private class CoordsPanel extends ListBoxPanel {

		public CoordsPanel() {
			super(loc, "Coordinates");
			CoordsModel model = new CoordsModel();
			model.setListener(this);
			setModel(model);
		}
	} // CoordsPanel

	private class LineEqnPanel extends ListBoxPanel {


		public LineEqnPanel() {
			super(loc, "Equation");
			LineEqnModel model = new LineEqnModel();
			model.setListener(this);
			setModel(model);
		}
	} // LineEqnPanel

	private class ConicEqnPanel extends ListBoxPanel {


		public ConicEqnPanel() {
			super(loc, "Equation");
			ConicEqnModel model = new ConicEqnModel(loc);
			model.setListener(this);
			setModel(model);
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


		public StartPointPanel() {
			super(app, "StartingPoint");
			StartPointModel model = new StartPointModel(app);
			model.setListener(this);
			setModel(model);
		}

		private StartPointModel getStartPointModel() {
			return (StartPointModel)getModel();
		}

		@Override
		public OptionPanel updatePanel(Object[] geos) {
			getModel().setGeos(geos);
			boolean geosOK = getModel().checkGeos();

			if (getWidget() != null) {
				getWidget().setVisible(false);
			}

			if (!geosOK || getWidget() == null) {
				return null;
			}

			ComboBoxW combo = getComboBox();
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != combo.getItemCount() - 1) {
				combo.getModel().clear();
				combo.addItem("");
				getStartPointModel().fillModes(loc);
				setFirstLabel();
			}

			getModel().updateProperties();
			setLabels();
			return this;
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
			super("AbsoluteScreenLocation", loc);
			setModel(new AbsoluteScreenLocationModel(app, this));
		}

	}

	private class ImageCornerPanel extends ComboBoxPanel {

		private ImageCornerModel model;
		public ImageCornerPanel(int cornerIdx) {
			super(app, "CornerModel");
			model = new ImageCornerModel(app);
			model.setListener(this);
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
			setModel(new OptionsModel() {

				@Override
				protected boolean isValidAt(int index) {
					return getGeoAt(index) instanceof Locateable;
				}

				@Override
				public void updateProperties() {
					// TODO Auto-generated method stub

				}

				@Override
				public PropertyListener getListener() {
					return CornerPointsPanel.this;
				};
			});
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
		public OptionPanel updatePanel(Object[] geos) {
			if (geos == null) {
				return null;
			}

			boolean result = corner1.updatePanel(geos) != null;
			result = corner2.updatePanel(geos) != null || result;
			result = corner4.updatePanel(geos) != null || result;
			return result ? this : null;
		}
	}


	
	private class TextOptionsPanel extends OptionPanel implements ITextOptionsListener,
	ITextEditPanel, GeoElementSelectionListener {
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
		public OptionPanel updatePanel(Object[] geos) {

			getModel().setGeos(geos);

			if (!getModel().checkGeos()) {
				model.cancelEditGeo();
				return null;
			}

			getModel().updateProperties();
			setLabels();
			advancedPanel.updateGeoList(); 
			if(getModel().hasPreview()){
				updatePreview();
				editor.updateFonts();
			}

			return this;

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

		
		



	private class ScriptEditPanel extends OptionPanel {


		private ScriptInputPanelW clickDialog, updateDialog, globalDialog;
		private TabPanel tabbedPane;
		private FlowPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;

		public ScriptEditPanel() {
			int row = 35;
			int column = 15;
			setModel(new OptionsModel() {

				@Override
				protected boolean isValidAt(int index) {
					return true;
				}

				@Override
				public void updateProperties() {
					// TODO Auto-generated method stub

				}

				@Override
				public PropertyListener getListener() {
					return ScriptEditPanel.this;
				}
			});
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
		public OptionPanel updatePanel(Object[] geos) {
			if (geos.length != 1){
				return null;
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
			return this;
		}

	}

	private class ExtendedAVPanel extends CheckboxPanel {
		public ExtendedAVPanel() {
			super("show extended algebra view", loc);
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
				tabs.get(event.getSelectedItem()).initGUI(app, isDefaults);
				((PropertiesViewW) app.getGuiManager().getPropertiesView())
							.updatePropertiesView();
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
		basicTab = makeOptionsTab("Properties.Basic");

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

		if (app.has(Feature.AV_EXTENSIONS)
				&& RadioButtonTreeItem.showSliderOrTextBox) {
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
		OptionsTab tab = makeOptionsTab("Text");
		textOptionsPanel = new TextOptionsPanel();
		tab.add(textOptionsPanel);
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
		PointSizeModel ptSize = new PointSizeModel();
		PointStyleModel ptStyle = new PointStyleModel();
		LineStyleModel lineStyle = new LineStyleModel();
		AngleArcSizeModel arcSize = new AngleArcSizeModel();
		SlopeTriangleSizeModel slopeSize = new SlopeTriangleSizeModel();
		IneqStyleModel ineqStyle = new IneqStyleModel();
		TextFieldSizeModel tfSize = new TextFieldSizeModel(app);
		ButtonSizeModel buttonSize = new ButtonSizeModel();
		FillingModel filling = new FillingModel(app);
		LodModel lod = new LodModel(app, isDefaults);
		InterpolateImageModel interpol = new InterpolateImageModel();
		DecoAngleModel decoAngle = new DecoAngleModel();
		DecoSegmentModel decoSegment = new DecoSegmentModel();
		

		tab.addModel(ptSize).addModel(ptStyle).addModel(lineStyle)
				.addModel(arcSize).addModel(slopeSize).addModel(ineqStyle)
				.addModel(tfSize).addModel(buttonSize).addModel(filling)
				.addModel(lod).addModel(interpol).addModel(decoAngle)
				.addModel(decoSegment);
		/*
		 * tab.addPanelList(Arrays.asList(pointSizePanel, pointStylePanel,
		 * lineStylePanel, angleArcSizePanel, slopeTriangleSizePanel,
		 * ineqStylePanel, buttonSizePanel, textFieldSizePanel, fillingPanel,
		 * lodPanel, interpolateImagePanel, decoAnglePanel, decoSegmentPanel ));
		 */
		return tab;
	}

	private OptionsTab addScriptTab() {
		OptionsTab tab = makeOptionsTab("Scripting");
		ScriptEditPanel scriptEditPanel = new ScriptEditPanel();
		tab.add(scriptEditPanel);
		return tab;
	}
	

	private OptionsTab addAdvancedTab() {
		OptionsTab tab = makeOptionsTab("Advanced");
		showConditionPanel = new ShowConditionPanel();
		colorFunctionPanel = new ColorFunctionPanel();
		layerPanel = new LayerPanel();
		tooltipPanel = new TooltipPanel();
		selectionAllowedPanel = new SelectionAllowedPanel();
		graphicsViewLocationPanel = new GraphicsViewLocationPanel();

		tab.add(showConditionPanel);
		tab.add(colorFunctionPanel);
		GroupModel group = new GroupModel();
		group.add(layerPanel.getModel());
		group.add(tooltipPanel.getModel());
		group.add(selectionAllowedPanel.getModel());
		GroupOptionsPanel misc = new GroupOptionsPanel("Miscellaneous", loc,
				group);
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

		tab = makeOptionsTab("Properties.Algebra");
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
		tab = makeOptionsTab("Properties.Position");
		tab.addPanelList(
				Arrays.asList(startPointPanel,
						new CornerPointsPanel(),
						new AbsoluteScreenLocationPanel()
						));
		return tab;

	}

	private OptionsTab makeOptionsTab(String id) {
		return new OptionsTab(this.loc, this.tabPanel, id);
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