
package geogebra.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
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
import geogebra.common.gui.dialog.options.model.FixCheckboxModel;
import geogebra.common.gui.dialog.options.model.FixObjectModel;
import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.ISliderListener;
import geogebra.common.gui.dialog.options.model.ITextFieldListener;
import geogebra.common.gui.dialog.options.model.IneqStyleModel;
import geogebra.common.gui.dialog.options.model.IneqStyleModel.IIneqStyleListener;
import geogebra.common.gui.dialog.options.model.LayerModel;
import geogebra.common.gui.dialog.options.model.LayerModel.ILayerOptionsListener;
import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import geogebra.common.gui.dialog.options.model.ListAsComboModel;
import geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import geogebra.common.gui.dialog.options.model.ObjectNameModel;
import geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import geogebra.common.gui.dialog.options.model.OptionsModel;
import geogebra.common.gui.dialog.options.model.OutlyingIntersectionsModel;
import geogebra.common.gui.dialog.options.model.PointSizeModel;
import geogebra.common.gui.dialog.options.model.PointSizeModel.IPointSizeListener;
import geogebra.common.gui.dialog.options.model.PointStyleModel;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import geogebra.common.gui.dialog.options.model.RightAngleModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import geogebra.common.gui.dialog.options.model.ShowLabelModel;
import geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import geogebra.common.gui.dialog.options.model.ShowObjectModel;
import geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import geogebra.common.gui.dialog.options.model.SlopeTriangleSizeModel;
import geogebra.common.gui.dialog.options.model.TextFieldSizeModel;
import geogebra.common.gui.dialog.options.model.TraceModel;
import geogebra.common.gui.dialog.options.model.TrimmedIntersectionLinesModel;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LineStylePopup;
import geogebra.html5.gui.util.PointStylePopup;
import geogebra.html5.gui.util.Slider;
import geogebra.html5.openjdk.awt.geom.Dimension;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.SelectionTable;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OptionsObjectW extends
geogebra.common.gui.dialog.options.OptionsObject implements OptionPanelW {
	private FlowPanel wrappedPanel;
	private VerticalPanel basicTab;
	private FlowPanel colorTab;
	private FlowPanel styleTab;
	private TabPanel tabPanel;
	private VerticalPanel advancedTab;

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
	private List<OptionPanel> basicPanels;
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
	private List<OptionPanel> stylePanels;

	//Advanced
	private ShowConditionPanel showConditionPanel;
	private boolean isDefaults;
	private ButtonSizePanel buttonSizePanel;
	private ColorFunctionPanel colorFunctionPanel;
	private List<OptionPanel> advancedPanels;
	private LayerPanel layerPanel;

	private abstract class OptionPanel {
		private OptionsModel model;
		private Widget widget;

		public void update(Object[] geos) {
			getModel().setGeos(geos);
			if (!(getModel().checkGeos())) {
				if (widget != null) {
					widget.setVisible(false);
				}
				return;
			}
			if (widget != null) {
				widget.setVisible(true);
			}

			getModel().updateProperties();
			setLabels();
		}

		public Widget getWidget() {
			return widget;
		}

		public void setWidget(Widget widget) {
			this.widget = widget;
		}

		public OptionsModel getModel() {
			return model;
		}

		public void setModel(OptionsModel model) {
			this.model = model;
		}

		public abstract void setLabels();
	}

	private class CheckboxPanel extends OptionPanel implements IBooleanOptionListener {
		private final CheckBox checkbox;
		private final String title;
		public CheckboxPanel(final String title) {
			checkbox = new CheckBox();
			setWidget(getCheckbox());
			this.title = title;

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
			getCheckbox().setText(app.getPlain(title));
		}

		public CheckBox getCheckbox() {
			return checkbox;
		}
	}

	private class ShowObjectPanel extends CheckboxPanel implements IShowObjectListener {
		public ShowObjectPanel() {
			super(app.getPlain("ShowObject"));
			setModel(new ShowObjectModel(this));
		}

		public void updateCheckbox(boolean value, boolean isEnabled) {
			getCheckbox().setValue(value);
			getCheckbox().setEnabled(isEnabled);
		}
	}

	private class TracePanel extends CheckboxPanel {
		public TracePanel() {
			super(app.getPlain("ShowTrace"));
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
			showLabelCB = new CheckBox(app.getPlain("ShowLabel") + ":"); 
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
				showLabelCB.setText(app.getPlain("ShowLabel"));
			} else {
				showLabelCB.setText(app.getPlain("ShowLabel") + ":");
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
			labelMode.addItem(app.getPlain("Name")); // index 0
			labelMode.addItem(app.getPlain("NameAndValue")); // index 1
			labelMode.addItem(app.getPlain("Value")); // index 2
			labelMode.addItem(app.getPlain("Caption")); // index 3 Michael
			labelMode.setSelectedIndex(selectedIndex);        
		}
	}

	private class FixPanel extends CheckboxPanel {

		public FixPanel() {
			super(app.getPlain("FixObject"));
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

		public ShowConditionPanel(AppW app/*, PropertiesPanelD propPanel*/) {
			kernel = app.getKernel();
			//this.propPanel = propPanel;
			model = new ShowConditionModel(app, this);
			setModel(model);
			
			FlowPanel mainPanel = new FlowPanel();
			
			title = new Label();
			mainPanel.add(title);
			// non auto complete input panel
			InputPanelW inputPanel = new InputPanelW(null, app, -1, false);
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
		private VerticalPanel mainPanel;
		private ColorPopupMenuButton colorChooser;
		private GColor selectedColor;
		private SelectionTable colorTable;

		public ColorPanel() {
			model = new ColorObjectModel(app, this);
			setModel(model);

			final GDimensionW colorIconSize = new GDimensionW(20, 20);
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
			mainPanel = new VerticalPanel();
			FlowPanel colorPanel = new FlowPanel();
			Label label = new Label("Choose color:");
			colorPanel.add(label);
			colorPanel.add(colorChooser);
			mainPanel.add(colorPanel);
			setWidget(mainPanel);

		}


		public void applyChanges() {
			float alpha = colorChooser.getSliderValue() / 100.0f;
			GColor color = colorChooser.getSelectedColor();
			model.applyChanges(color, alpha, false);

		}

		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground) {
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
			colorChooser.update(model.getGeos());
		}


		public void updatePreview(GColor col, float alpha) {
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub

		}

	}


	private class NamePanel extends OptionPanel implements IObjectNameListener {

		private static final long serialVersionUID = 1L;

		private ObjectNameModel model;
		private AutoCompleteTextFieldW tfName, tfDefinition, tfCaption;

		//	private Runnable doActionStopped = new Runnable() {
		//		public void run() {
		//			actionPerforming = false;
		//		}
		//	};
		private Label nameLabel, defLabel, captionLabel;
		private InputPanelW inputPanelName, inputPanelDef, inputPanelCap;

		private AppW app;
		private Localization loc;
		private VerticalPanel mainWidget;
		private FlowPanel namePanel;
		private FlowPanel defPanel;
		private FlowPanel captionPanel;

		public NamePanel(AppW app) {
			this.app = app;
			this.loc = app.getLocalization();
			model = new ObjectNameModel(app, this);
			setModel(model);
			// NAME PANEL

			// non auto complete input panel
			inputPanelName = new InputPanelW(null, app, 1, -1, true);
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
			inputPanelDef = new InputPanelW(null, app, 1, -1, true);
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
			inputPanelCap = new InputPanelW(null, app, 1, -1, true);
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

			mainWidget = new VerticalPanel();

			// name panel
			namePanel = new FlowPanel();
			nameLabel = new Label();
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

			setLabels();
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
				defLabel.setText(app.getPlain("Value") + ":");
			} else {
				defLabel.setText(app.getPlain("Definition") + ":");
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
			super(app.getPlain("DrawAsDropDownList"));
			setModel(new ListAsComboModel(app, this));
		}

		public void drawListAsComboBox(GeoList geo, boolean value) {
			//TODO: implement the following:
			((EuclidianViewWeb)(app.getActiveEuclidianView())).drawListAsComboBox(geo, value);
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
			intervalLabel.setText(app.getPlain("AngleBetween"));

			setComboLabels();
		}
		public void setComboLabels() {
			int idx = intervalLB.getSelectedIndex();
			intervalLB.clear();
			model.fillCombo();
			intervalLB.setSelectedIndex(idx);

		}

		private int getIndex() {
			if (model.hasOrientation()) {
				return intervalLB.getSelectedIndex();
			}

			// first interval disabled
			return intervalLB.getSelectedIndex() + 1;
		}
		public void addComboItem(String item) {
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


	}


	class RightAnglePanel extends CheckboxPanel {
		public RightAnglePanel() {
			super(app.getPlain("EmphasizeRightAngle"));
			setModel(new RightAngleModel(this));

		}
	}

	private class ShowTrimmedIntersectionLinesPanel extends CheckboxPanel {

		private static final long serialVersionUID = 1L;
		public ShowTrimmedIntersectionLinesPanel() {
			super(app.getPlain("ShowTrimmed"));
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

	private class PointSizePanel extends OptionPanel implements IPointSizeListener {
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
			titleLabel.setText(app.getPlain("PointSize"));

		}

		public void setSliderValue(int value) {
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
			btnPointStyle = PointStylePopup.create((AppW)app, iconHeight, -1, false,
					model);
			if (btnPointStyle != null) {
				btnPointStyle.setKeepVisible(false);
				mainPanel.add(btnPointStyle);
			}
			setWidget(mainPanel);
		}
		@Override
		public void setLabels() {
			titleLabel.setText(app.getPlain("PointStyle"));

		}

		public void setSelectedIndex(int index) {
			if (btnPointStyle != null)
				btnPointStyle.setSelectedIndex(index);
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
			btnLineStyle = LineStylePopup.create((AppW)app, iconHeight, -1, false);
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
			sliderLabel.setText(app.getPlain("Thickness"));
			popupLabel.setText(app.getPlain("LineStyle") + ":");

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
			titleLabel.setText(app.getPlain("Size"));

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
			titleLabel.setText(app.getPlain("Size"));

		}

		public void setValue(int value) {
			slider.setValue(value);

		}

	}


	private class IneqPanel extends CheckboxPanel implements IIneqStyleListener {

		private static final long serialVersionUID = 1L;
		
		public IneqPanel() {
			super(app.getPlain("ShowOnXAxis"));
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
		public TextFieldSizePanel(AppW app) {
			model = new TextFieldSizeModel(app, this);
			setModel(model);

			FlowPanel mainPanel = new FlowPanel();
			title = new Label("Hejj!");
			mainPanel.add(title);
			
			inputPanel = new InputPanelW(null, app, 1, -1, false);
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
			setLabels();

		}
		public void setText(String text) {
			tfSize.setText(text);
        }

		@Override
        public void setLabels() {
	        title.setText(app.getPlain("TextfieldLength"));
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
		private Localization loc;
		private ButtonSizeModel model;
		
	
		public ButtonSizePanel(AppW app, Localization loc) {
			this.loc = loc;
			model = new ButtonSizeModel(this);
			setModel(model);
			labelWidth = new Label();
			labelHeight = new Label();
			labelPixelW = new Label();
			labelPixelH = new Label();
			cbUseFixedSize = new CheckBox();
			setLabels();
		
			ipButtonWidth = new InputPanelW(null, app, 1, -1, false);
			ipButtonHeight = new InputPanelW(null, app, 1, -1, false);
			
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
			cbUseFixedSize.setText(loc.getPlain("Fixed"));
	        
        }
		
	}
	
	private class ColorFunctionPanel extends OptionPanel implements IColorFunctionListener {
		private ColorFunctionModel model;
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
		public ColorFunctionPanel(AppW app) {
			kernel = app.getKernel();
			model = new ColorFunctionModel(app, this);
			setModel(model);
			// non auto complete input panel
			InputPanelW inputPanelR = new InputPanelW(null, app, 1, -1, true);
			InputPanelW inputPanelG = new InputPanelW(null, app, 1, -1, true);
			InputPanelW inputPanelB = new InputPanelW(null, app, 1, -1, true);
			InputPanelW inputPanelA = new InputPanelW(null, app, 1, -1, true);
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
			Localization loc = kernel.getLocalization();

			title.setText(loc.getMenu("DynamicColors"));
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
			tfAlpha.setVisible(value);
			nameLabelA.setVisible(value);
		}

		public void updateSelection(Object[] geos) {
			//updateSelection(geos);
			
		}
		
	}

	private class LayerPanel extends OptionPanel implements ILayerOptionsListener {
		private LayerModel model;
		private Label layerLabel;
		private ListBox layerModeCB;
		
		public LayerPanel() {
			model = new LayerModel(this);
			setModel(model);
			FlowPanel mainPanel = new FlowPanel();
			layerLabel = new Label();
			mainPanel.add(layerLabel);
			
			layerModeCB = new ListBox();
			model.addLayers();
			mainPanel.add(layerModeCB);
			
			layerModeCB.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	                model.applyChanges(layerModeCB.getSelectedIndex());
                }});
			setWidget(mainPanel);
			
		}
			
		public void setSelectedIndex(int index) {
	        layerModeCB.setSelectedIndex(index);
	        
        }

		public void addItem(String item) {
	        layerModeCB.addItem(item);
        }

		@Override
        public void setLabels() {
	        layerLabel.setText(app.getPlain("Layer") + ":");
        }
		
	}
	//-----------------------------------------------
	public OptionsObjectW(AppW app, boolean isDefaults) {
		this.app = app;
		this.isDefaults = isDefaults;
		kernel = app.getKernel();

		// build GUI
		initGUI();
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

		addBasicTab();
		addColorTab();
		addStyleTab();
		addAdvancedTab();
		selectTab(0);
		wrappedPanel.add(tabPanel);
		wrappedPanel.addAttachHandler(new AttachEvent.Handler() {

			  public void onAttachOrDetach(AttachEvent event) {
			    app.setDefaultCursor();
			  }
			});
		updateGUI();
	}



	private void addBasicTab() {
		basicTab = new VerticalPanel();
		basicTab.setStyleName("objectPropertiesTab");

		namePanel = new NamePanel((AppW)app);   
		if (!isDefaults) {
			basicTab.add(namePanel.getWidget());
		}

		VerticalPanel checkboxPanel = new VerticalPanel();
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
			basicTab.add(reflexAnglePanel.getWidget());
		}

		listAsComboPanel = new ListAsComboPanel();
		basicTab.add(listAsComboPanel.getWidget());

		rightAnglePanel = new RightAnglePanel();
		basicTab.add(rightAnglePanel.getWidget());

		trimmedIntersectionLinesPanel = new ShowTrimmedIntersectionLinesPanel();
		basicTab.add(trimmedIntersectionLinesPanel.getWidget());

		//		basicTabList.add(comboBoxPanel);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		basicTab.add(allowOutlyingIntersectionsPanel.getWidget());

		fixCheckboxPanel = new FixCheckboxPanel();
		basicTab.add(fixCheckboxPanel.getWidget());

		tabPanel.add(basicTab, "Basic");

		basicPanels = Arrays.asList(namePanel,
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
				fixCheckboxPanel);
	};

	private void addColorTab() {

		colorTab = new FlowPanel();
		colorTab.setStyleName("objectPropertiesTab");
		colorPanel = new ColorPanel();
		colorTab.add(colorPanel.getWidget());
		tabPanel.add(colorTab, "Color");
	}

	private void addStyleTab() {
		styleTab = new FlowPanel();
		styleTab.setStyleName("objectPropertiesTab");

		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel();
		lineStylePanel = new LineStylePanel();
		angleArcSizePanel = new AngleArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		ineqStylePanel = new IneqPanel();
		textFieldSizePanel = new TextFieldSizePanel((AppW)app);
		buttonSizePanel = new ButtonSizePanel((AppW)app, app.getLocalization());
		
		stylePanels = Arrays.asList(pointSizePanel,
				pointStylePanel,
				lineStylePanel,
				angleArcSizePanel,
				slopeTriangleSizePanel,
				ineqStylePanel,
				buttonSizePanel,
				textFieldSizePanel);
		
		for (OptionPanel panel: stylePanels) {
			styleTab.add(panel.getWidget());
		}
	
		tabPanel.add(styleTab, "Style");
	}

	private void addAdvancedTab() {
		advancedTab = new VerticalPanel();
		advancedTab.setStyleName("objectPropertiesTab");
		showConditionPanel = new ShowConditionPanel((AppW) app);
		colorFunctionPanel = new ColorFunctionPanel((AppW) app);
		
		layerPanel = new LayerPanel();
		advancedPanels = Arrays.asList(showConditionPanel,
				colorFunctionPanel,
				layerPanel);
	
		for (OptionPanel panel: advancedPanels) {
			advancedTab.add(panel.getWidget());
		}
	
		tabPanel.add(advancedTab, "Advanced");
	}




	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	public void setMinimumSize(Dimension preferredSize) {

	}

	public void updateGUI() {

		Object[] geos = app.getSelectionManager().getSelectedGeos().toArray();
		if (geos != null && geos.length != 0) {
			tabPanel.setVisible(true);
			for (OptionPanel panel: basicPanels) {
				panel.update(geos);
			}

			for (OptionPanel panel: stylePanels) {
				panel.update(geos);
			}

			for (OptionPanel panel: advancedPanels) {
				panel.update(geos);
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
