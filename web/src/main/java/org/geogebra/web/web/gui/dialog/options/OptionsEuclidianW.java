package org.geogebra.web.web.gui.dialog.options;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.OptionsEuclidian;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.LineStylePopup;
import org.geogebra.web.web.gui.util.MyCJButton;
import org.geogebra.web.web.gui.util.NumberListBox;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW,
	IEuclidianOptionsListener {

	protected AppW app;
	protected MultiRowsTabPanel tabPanel;
	protected EuclidianView view;
	public EuclidianOptionsModel model;
	protected BasicTab basicTab;
	AxisTab xAxisTab;
	AxisTab yAxisTab;
	private GridTab gridTab;
	ListBox lbTooltips;
	private boolean isIniting;
	protected Localization loc;
	
	protected abstract class EuclidianTab extends FlowPanel implements SetLabels {
		
		protected EuclidianTab() {
			setStyleName("propertiesTab");
		}
		
		public void onResize(int height, int width) {
			this.setHeight(height + "px");
			this.setWidth(width + "px");
		}
	}
	
	protected class BasicTab extends EuclidianTab {
		
		private Label dimTitle;
		private Label[] dimLabel;
		private AutoCompleteTextFieldW tfMinX;
		private AutoCompleteTextFieldW tfMaxX;
		private AutoCompleteTextFieldW tfMinY;
		private AutoCompleteTextFieldW tfMaxY;
		AutoCompleteTextFieldW tfAxesRatioX;
		AutoCompleteTextFieldW tfAxesRatioY;
		
		private Label axesRatioLabel;
		private FlowPanel dimPanel;
		ToggleButton tbLockRatio;
		private Image imgLock;
		private Image imgUnlock;
		
		
		protected CheckBox cbShowAxes;
		CheckBox cbBoldAxes;
		private Label colorLabel;
		private MyCJButton btAxesColor;
		private Label lineStyle;
		protected FlowPanel axesOptionsPanel;
		private AutoCompleteTextFieldW axesOptionTitle;
		private Label axesOptionsTitle;
		private PopupMenuButton axesStylePopup;
		protected Label backgroundColorLabel;
		protected MyCJButton btBackgroundColor;
		CheckBox cbShowMouseCoords;
		private Label tooltips;
		protected Label miscTitle;
		private Label consProtocolTitle;
		private FlowPanel consProtocolPanel;
		CheckBox cbShowNavbar;
		CheckBox cbNavPlay;
		CheckBox cbOpenConsProtocol;
		private CheckBox cbShowGrid;
		private CheckBox cbBoldGrid;
		protected Label lblAxisLabelStyle;
		protected CheckBox cbAxisLabelSerif;
		protected CheckBox cbAxisLabelBold;
		protected CheckBox cbAxisLabelItalic;
		public BasicTab() {
			super();
			addDimensionPanel();
			addAxesOptionsPanel();
			addConsProtocolPanel();
			addMiscPanel();
		}

		private void addMinMaxHandler(final AutoCompleteTextFieldW tf, final MinMaxType type) {

			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyMinMax(tf.getText(), type);
						updateView();
					}
				}});

			tf.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					model.applyMinMax(tf.getText(), type);
					updateView();
				}	
			});

		}
		protected double parseDouble(String text) {
			if (text == null || text.equals(""))
				return Double.NaN;
			return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
		}
		
		void applyAxesRatio() {
	
			model.applyAxesRatio(parseDouble(tfAxesRatioX.getText()),
					parseDouble(tfAxesRatioY.getText()));
			updateView();
		}
		
		private void addAxesRatioHandler(final AutoCompleteTextFieldW tf) {
	
			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						applyAxesRatio();
					}
				}});

			tf.addFocusListener(new FocusListenerW(this){
				@Override
				protected void wrapFocusLost(){
					applyAxesRatio();
					
				}	
			});

		}
		
		private void addDimensionPanel() {
			dimTitle = new Label("");
			dimTitle.setStyleName("panelTitle");
			dimLabel = new Label[4]; // "Xmin", "Xmax" etc.
			for (int i = 0; i < 4; i++) {
				dimLabel[i] = new Label("");
			}

			
			tfMinX = getTextField();
			tfMaxX = getTextField();
			
			tfMinY = getTextField();
			tfMaxY = getTextField();
			
			tfAxesRatioX = getTextField();
			tfAxesRatioY = getTextField();
			
			enableAxesRatio(view.isZoomable() && !view.isLockedAxesRatio());
			
			imgLock = new Image(AppResources.INSTANCE.lock());
			imgUnlock = new Image(AppResources.INSTANCE.unlock());

			tbLockRatio = new ToggleButton(imgLock);
			tbLockRatio.setValue(view.isLockedAxesRatio());
			tbLockRatio.setEnabled(view.isZoomable());
		
			axesRatioLabel = new Label("");

			dimPanel = new FlowPanel();
			addToDimPanel(dimTitle);
			
			FlowPanel xMinPanel = new FlowPanel();
			FlowPanel xMaxPanel = new FlowPanel();
			FlowPanel yMinPanel = new FlowPanel();
			FlowPanel yMaxPanel = new FlowPanel();
			xMinPanel.setStyleName("panelRowCell");
			xMaxPanel.setStyleName("panelRowCell");
			yMinPanel.setStyleName("panelRowCell");
			yMaxPanel.setStyleName("panelRowCell");
			xMinPanel.add(dimLabel[0]);
			xMinPanel.add(tfMinX);
			xMaxPanel.add(dimLabel[1]);
			xMaxPanel.add(tfMaxX);
			yMinPanel.add(dimLabel[2]);
			yMinPanel.add(tfMinY);
			yMaxPanel.add(dimLabel[3]);
			yMaxPanel.add(tfMaxY);
			
			dimPanel.add(LayoutUtilW.panelRow(xMinPanel, xMaxPanel));
			dimPanel.add(LayoutUtilW.panelRow(yMinPanel, yMaxPanel));
			
			dimPanel.add(LayoutUtilW.panelRow(axesRatioLabel));
			dimPanel.add(LayoutUtilW.panelRow(tfAxesRatioX, new Label(" : "),
					tfAxesRatioY, tbLockRatio));
			
			indentDimPanel();
			
			addMinMaxHandler(tfMinX, MinMaxType.minX);
			addMinMaxHandler(tfMaxX, MinMaxType.maxX);

			addMinMaxHandler(tfMinY, MinMaxType.minY);
			addMinMaxHandler(tfMaxY, MinMaxType.maxY);
			
			addAxesRatioHandler(tfAxesRatioX);
			addAxesRatioHandler(tfAxesRatioY);
			
			tbLockRatio.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					if (tbLockRatio.getValue()) {
						model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
								/ parseDouble(tfAxesRatioY.getText()));
					} else {
						model.applyLockRatio(-1);
					}


				}
			});
		}
		
		protected void indentDimPanel(){
			indent(dimPanel);
		}
		
		protected void addToDimPanel(Widget w){
			add(w);
		}
		
		protected void indent(FlowPanel panel) {
			FlowPanel indent = new FlowPanel();
			indent.setStyleName("panelIndent");
			indent.add(panel);
			add(indent);
	
        }

		protected void addAxesOptionsPanel() {

			axesOptionsTitle = new Label();
			axesOptionsTitle.setStyleName("panelTitle");
			// show axes checkbox
			cbShowAxes = new CheckBox(loc.getMenu("ShowAxes"));

			// show bold checkbox
			cbBoldAxes = new CheckBox(loc.getMenu("Bold"));

			// axes color
			colorLabel = new Label(loc.getMenu("Color") + ":");

			lblAxisLabelStyle = new Label(loc.getMenu("LabelStyle"));
			// show axis label bold checkbox
			cbAxisLabelBold = new CheckBox(loc.getMenu("Bold"));

			cbAxisLabelSerif = new CheckBox(loc.getMenu("Serif"));

			// show axis label italic checkbox
			cbAxisLabelItalic = new CheckBox(loc.getMenu("Italic"));

			btAxesColor = new MyCJButton();
			
			btAxesColor.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	              app.getDialogManager().showColorChooserDialog(model.getAxesColor(),
	            		  new ColorChangeHandler() {
					
					public void onForegroundSelected() {
						// TODO Auto-generated method stub
						
					}
					
					public void onColorChange(GColor color) {
						model.applyAxesColor(color);
						updateAxesColorButton(color);
					}
					
					public void onClearBackground() {
						// TODO Auto-generated method stub
						
					}
					
					public void onBackgroundSelected() {
						// TODO Auto-generated method stub
						
					}
					
					public void onAlphaChange() {
						// TODO Auto-generated method stub
						
					}

						public void onBarSelected() {
							// TODO Auto-generated method stub

						}
				});
                }});
			
			// axes style
			lineStyle = new Label(loc.getMenu("LineStyle") + ":");
			final ImageOrText[] iconArray = new ImageOrText[EuclidianOptionsModel.getAxesStyleLength()];
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIconW
						.createAxesStyleIcon(EuclidianStyleConstants
								.getLineStyleOptions(i));
			}
			
			axesStylePopup = new PopupMenuButton(app, iconArray, -1, 1,
			        org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
				@Override
				public void handlePopupActionEvent(){
					int idx = getSelectedIndex();
					
					model.appyAxesStyle(
							EuclidianStyleConstants.getLineStyleOptions(idx)
					// make sure bold checkbox doesn't change
							+ (cbBoldAxes.getValue() ? EuclidianStyleConstants.AXES_BOLD
									: 0));
					updateView();
					super.handlePopupActionEvent();
					
				}
			};
			axesStylePopup.setKeepVisible(false);		

			// axes options panel
			axesOptionsPanel = new FlowPanel();
			add(axesOptionsTitle);
			fillAxesOptionsPanel();
			cbShowAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					setShowAxes(cbShowAxes.getValue());
					updateView();
                }});
			
			cbBoldAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyBoldAxes(cbBoldAxes.getValue(), cbShowAxes.getValue());
					updateView();
                }});
			
			cbAxisLabelSerif.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					model.setAxesLabelsSerif(cbAxisLabelSerif.getValue());
				}
			});

			cbAxisLabelBold.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					model.setAxisFontBold(cbAxisLabelBold.getValue());
				}
			});

			cbAxisLabelItalic.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					model.setAxisFontItalic(cbAxisLabelItalic.getValue());
				}
			});

			indent(axesOptionsPanel);
		}
		
		protected void setShowAxes(Boolean value) {
			model.showAxes(value);
			xAxisTab.setShowAxis(value);
			yAxisTab.setShowAxis(value);

		}

		protected void fillAxesOptionsPanel(){
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbShowAxes,
					 cbBoldAxes));
			axesOptionsPanel.add(LayoutUtilW.panelRow(colorLabel, btAxesColor,
					 lineStyle, axesStylePopup));
			axesOptionsPanel.add(LayoutUtilW.panelRow(lblAxisLabelStyle,
					cbAxisLabelSerif, cbAxisLabelBold, cbAxisLabelItalic));

		}


		void togglePlayButton() {

			Collection<ConstructionProtocolNavigation> cpns = app
					.getGuiManager().getAllConstructionProtocolNavigations();
			for (ConstructionProtocolNavigation cpn : cpns) {
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
			}
			app.setUnsaved();
			updateGUI();

		}

		void toggleConsProtButton() {
			Collection<ConstructionProtocolNavigation> cpns = app
					.getGuiManager().getAllConstructionProtocolNavigations();
			for (ConstructionProtocolNavigation cpn : cpns) {
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
			}
			app.setUnsaved();
			updateGUI();
		}
		
		private void addConsProtocolPanel() {
			consProtocolTitle = new Label();
			consProtocolTitle.setStyleName("panelTitle");
			consProtocolPanel = new FlowPanel();
		
			cbShowNavbar = new CheckBox();
			
			consProtocolPanel.add(cbShowNavbar);

			cbNavPlay = new CheckBox();
			
			cbOpenConsProtocol = new CheckBox();
			
			cbShowNavbar.setStyleName("checkBoxPanel");
			
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName("panelIndent");
			cbNavPlay.setStyleName("checkBoxPanel");
			cbOpenConsProtocol.setStyleName("checkBoxPanel");
			buttons.add(cbNavPlay);
			buttons.add(cbOpenConsProtocol);
			consProtocolPanel.add(buttons);
			
			add(consProtocolTitle);
			indent(consProtocolPanel);
			
			cbShowNavbar.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					app.toggleShowConstructionProtocolNavigation(view
							.getViewID());
					cbNavPlay.setEnabled(cbShowNavbar.getValue());
					cbOpenConsProtocol.setEnabled(cbShowNavbar.getValue());
                }});
			
			cbNavPlay.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					togglePlayButton();
				}});
			
			cbOpenConsProtocol.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					toggleConsProtButton();
				}});
			
						
			
        }
		
		protected FlowPanel miscPanel;
		
		protected void applyBackgroundColor(GColor color){
			int viewIdx = 0;
			if (view == app.getEuclidianView1()) {
				viewIdx = 1; 
			} else if (app.hasEuclidianView2EitherShowingOrNot(1) && view == app.getEuclidianView2(1)) {
				viewIdx = 2;
			} else if (app.hasEuclidianView3D() && view == app.getEuclidianView3D()) {
				viewIdx = 3;
			}	
			model.applyBackgroundColor(viewIdx, color);
		}


		protected void addMiscPanel() {
			miscTitle = new Label();
			miscTitle.setStyleName("panelTitle");
			// background color panel
			backgroundColorLabel = new Label(
					loc.getMenu("BackgroundColor") + ":");
	
			btBackgroundColor = new MyCJButton();

			// show mouse coords
			cbShowMouseCoords = new CheckBox();

			// show tooltips
			tooltips = new Label(loc.getMenu("Tooltips") + ":");
			lbTooltips = new ListBox();
			model.fillTooltipCombo();
			
			miscPanel = new FlowPanel();
			add(miscTitle);
			
			fillMiscPanel();

			indent(miscPanel);
    
			btBackgroundColor.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
		              app.getDialogManager().showColorChooserDialog(model.getBackgroundColor(),
		           		  new ColorChangeHandler() {
							
							public void onForegroundSelected() {
								// TODO Auto-generated method stub
								
							}
							
							public void onColorChange(GColor color) {
								applyBackgroundColor(color);
								updateBackgroundColorButton(color);
							}
							
							public void onClearBackground() {
								// TODO Auto-generated method stub
								
							}
							
							public void onBackgroundSelected() {
								// TODO Auto-generated method stub
								
							}
							
							public void onAlphaChange() {
								// TODO Auto-generated method stub
								
							}

						public void onBarSelected() {
							// TODO Auto-generated method stub

						}
						});
		                
//	                model.applyBackgroundColor();
                }});

			cbShowMouseCoords.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	                model.applyMouseCoords(cbShowMouseCoords.getValue());
                }});
			
			lbTooltips.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	                model.applyTooltipMode(lbTooltips.getSelectedIndex());
                }});
			
		}
		
		
		
		protected void fillMiscPanel(){
			miscPanel.add(LayoutUtilW.panelRow(backgroundColorLabel, btBackgroundColor));
			miscPanel.add(LayoutUtilW.panelRow(tooltips, lbTooltips));
			miscPanel.add(LayoutUtilW.panelRow(cbShowMouseCoords));
		}


		public void setLabels() {
			dimTitle.setText(loc.getMenu("Dimensions"));
			
			dimLabel[0].setText(loc.getMenu("xmin") + ":");
			dimLabel[1].setText(loc.getMenu("xmax") + ":");
			dimLabel[2].setText(loc.getMenu("ymin") + ":");
			dimLabel[3].setText(loc.getMenu("ymax") + ":");
			axesRatioLabel.setText(
					loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"));
			
			axesOptionsTitle.setText(loc.getMenu("Axes"));
			cbShowAxes.setText(loc.getMenu("ShowAxes"));
			cbBoldAxes.setText(loc.getMenu("Bold"));
			colorLabel.setText(loc.getMenu("Color") + ":");
			lineStyle.setText(loc.getMenu("LineStyle") + ":");

			miscTitle.setText(loc.getMenu("Miscellaneous"));
			backgroundColorLabel.setText(loc.getMenu("BackgroundColor") + ":");
			int index = lbTooltips.getSelectedIndex();
			lbTooltips.clear();
			model.fillTooltipCombo();
			lbTooltips.setSelectedIndex(index);
			cbShowMouseCoords.setText(loc.getMenu("ShowMouseCoordinates"));
			
			consProtocolTitle
					.setText(loc.getMenu("ConstructionProtocolNavigation"));
			
			cbShowNavbar.setText(loc.getMenu("Show"));
			cbNavPlay.setText(loc.getMenu("PlayButton"));
			cbOpenConsProtocol
					.setText(loc.getMenu("ConstructionProtocolButton"));
			
			lblAxisLabelStyle.setText(loc.getMenu("LabelStyle"));
			cbAxisLabelSerif.setText(loc.getMenu("Serif"));
			cbAxisLabelBold.setText(loc.getMenu("Bold"));
			cbAxisLabelItalic.setText(loc.getMenu("Italic"));

		}

		public void enableAxesRatio(boolean value) {
			tfAxesRatioX.getTextBox().setEnabled(value);
			tfAxesRatioY.getTextBox().setEnabled(value);
			// tbLockRatio.getDownFace().setImage(value ? imgUnlock : imgLock);
			if (tbLockRatio != null) {
				tbLockRatio.getUpFace().setImage(value ? imgUnlock : imgLock);
			}
		}

		public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
			tfMinX.setText(minX);
			tfMaxX.setText(maxX);
			tfMinY.setText(minY);
			tfMaxY.setText(maxY);

		}
		public void updateAxes(GColor color, boolean isShown, boolean isBold) {

//			btAxesColor.setForeground(new GColorW(view.getAxesColor()));
			cbShowAxes.setValue(view.getShowXaxis() && view.getShowYaxis());
			cbBoldAxes.setValue(view.areAxesBold());
			updateAxesColorButton(color);
		}
		
		public void updateAxesColorButton(GColor color) {
			ImageOrText content = new ImageOrText();
			content.setBgColor(color);
			btAxesColor.setIcon(content);
		}

		public void updateBackgroundColorButton(GColor color) {
			ImageOrText content = new ImageOrText();
			content.setBgColor(color);
			btBackgroundColor.setIcon(content);
		}
		
		public void updateConsProtocolPanel(boolean isVisible) {
			// cons protocol panel
			cbShowNavbar.setValue(isVisible);
			ConstructionProtocolNavigationW cpn = (ConstructionProtocolNavigationW) app
			        .getGuiManager()
			        .getConstructionProtocolNavigationIfExists();
			cbNavPlay.setValue(cpn == null || cpn.isPlayButtonVisible());
			cbOpenConsProtocol.setValue(cpn == null
			        || cpn.isConsProtButtonVisible());

			cbNavPlay.setEnabled(isVisible);
			cbOpenConsProtocol.setEnabled(isVisible);

		}

		public void showMouseCoords(boolean value) {
	        cbShowMouseCoords.setValue(value);
        }

		public void selectAxesStyle(int index) {
	        axesStylePopup.setSelectedIndex(index);
        }

		public void enabeLock(boolean value) {
	        tbLockRatio.setEnabled(value);
        }

		final protected void updateMinMax() {
			view.updateBoundObjects();
			
			setMinMaxText(view.getXminObject().getLabel(
					StringTemplate.editTemplate),
					view.getXmaxObject().getLabel(
					StringTemplate.editTemplate),
					view.getYminObject().getLabel(
					StringTemplate.editTemplate),
					view.getYmaxObject().getLabel(
					StringTemplate.editTemplate));

		}

		public void updateBounds() {

			updateMinMax();

			double xscale = view.getXscale();
			double yscale = view.getYscale();
			if (xscale >= yscale) {
				tfAxesRatioX.setText("1");
				tfAxesRatioY.setText(app.getKernel().format(xscale / yscale,
						StringTemplate.editTemplate));
			} else {
				tfAxesRatioX.setText(app.getKernel().format(yscale / xscale,
						StringTemplate.editTemplate));
				tfAxesRatioY.setText("1");
			}

        }

	}
	
	protected class AxisTab extends EuclidianTab {
		private AxisPanel axisPanel;
			
		public AxisTab(int axis, boolean view3D) {
			super();
			axisPanel = new AxisPanel(app, view, axis, view3D);
			add(axisPanel);
		}
		
		public void updateView(EuclidianView view) {
			axisPanel.updateView(view);
		}

		public void setShowAxis(boolean value) {
			axisPanel.setShowAxis(value);
		}

		public void setLabels() {
	        axisPanel.setLabels();
        }
		
	}
		
	protected class GridTab extends EuclidianTab {
		private static final int iconHeight = 24;
		CheckBox cbShowGrid;
		ListBox lbGridType;
		CheckBox cbGridManualTick;
		NumberListBox ncbGridTickX;
		NumberListBox ncbGridTickY;
		ListBox lbGridTickAngle;
		private Label gridLabel1;
		private Label gridLabel2;
		private Label gridLabel3;
		protected Label lblGridType;
		private Label lblGridStyle;
		LineStylePopup btnGridStyle;
		private Label lblColor;
		CheckBox cbBoldGrid;
		private MyCJButton btGridColor;
		private FlowPanel mainPanel;
		public GridTab() {
			super();
			cbShowGrid = new CheckBox();
			cbShowGrid.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					enableGrid(cbShowGrid.getValue());
				}
			});
			mainPanel = new FlowPanel();

			add(cbShowGrid);
			add(mainPanel);
			initGridTypePanel();
			initGridStylePanel();
		}
		
		void enableGrid(boolean value) {
			model.showGrid(value);
			if (value) {
				mainPanel.removeStyleName("disabled");
			} else {
				mainPanel.setStyleName("disabled");
			}
			lbGridType.setEnabled(value);
			cbGridManualTick.setEnabled(value);
			btnGridStyle.setEnabled(value);
			cbBoldGrid.setEnabled(value);
			btGridColor.setEnabled(value);

		}
		private void initGridTypePanel() {

			// grid type combo box
			lblGridType = new Label();
			lbGridType = new ListBox();
			mainPanel.add(lblGridType);
			lblGridType.setStyleName("panelTitle");
			
			lbGridType.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	                model.appyGridType(lbGridType.getSelectedIndex());
					updateView();
                }});
			// tick intervals

			cbGridManualTick = new CheckBox();
			cbGridManualTick.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	                model.appyGridManualTick(cbGridManualTick.getValue());
					updateView();
	        	}});
			cbGridManualTick.setStyleName("checkBoxPanel");
			ncbGridTickX = new NumberListBox(app){

				@Override
                protected void onValueChange(String value) {
					model.applyGridTicks(ncbGridTickX.getValue(), 0);
					updateView();
	                
                }};
	
            ncbGridTickY = new NumberListBox(app){

				@Override
                protected void onValueChange(String value) {
					model.applyGridTicks(ncbGridTickY.getValue(), 1);
					updateView();	                
                }};
                
                
			// checkbox for grid labels
			lbGridTickAngle = new ListBox();
			
			FlowPanel gridTickAnglePanel = new FlowPanel();
			gridTickAnglePanel.setStyleName("panelRow");
			addGridType(gridTickAnglePanel);
		
			
			// grid labels
			gridLabel1 = new Label("x:");
			gridLabel2 = new Label("y:");
			gridLabel3 = new Label("\u03B8" + ":"); // Theta
			
			FlowPanel ncbGridTickXPanel = new FlowPanel();
			FlowPanel ncbGridTickYPanel = new FlowPanel();
			FlowPanel ncbGridTickAnglePanel = new FlowPanel();
			ncbGridTickXPanel.setStyleName("panelRowCell");
			ncbGridTickYPanel.setStyleName("panelRowCell");
			ncbGridTickAnglePanel.setStyleName("panelRowCell");
			ncbGridTickXPanel.add(gridLabel1);
			ncbGridTickXPanel.add(ncbGridTickX);
			ncbGridTickYPanel.add(gridLabel2);
			ncbGridTickYPanel.add(ncbGridTickY);
			ncbGridTickAnglePanel.add(gridLabel3);
			ncbGridTickAnglePanel.add(lbGridTickAngle);
		
			FlowPanel tickPanel = LayoutUtilW.panelRow(cbGridManualTick, ncbGridTickXPanel, 
					ncbGridTickYPanel, ncbGridTickAnglePanel);
			mainPanel.add(tickPanel);
			
			FlowPanel typePanel = new FlowPanel();
			typePanel.add(gridTickAnglePanel);
			typePanel.add(cbGridManualTick);
			typePanel.add(LayoutUtilW.panelRowIndent(
					ncbGridTickXPanel, ncbGridTickYPanel, ncbGridTickAnglePanel));

			lbGridTickAngle.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event) {
					model.applyGridTickAngle(lbGridTickAngle.getSelectedIndex());
					updateView();
				}
			});
			
			lbGridType.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event) {
					model.appyGridType(lbGridType.getSelectedIndex());
					updateView();					
				}
			});
			typePanel.setStyleName("panelIndent");
			mainPanel.add(typePanel);
		}
		
		protected void addGridType(FlowPanel gridTickAnglePanel){
			gridTickAnglePanel.add(lbGridType);
		}


		private void initGridStylePanel() {

			// line style
			lblGridStyle = new Label();
			addOnlyFor2D(lblGridStyle);
			lblGridStyle.setStyleName("panelTitle");
			btnGridStyle = LineStylePopup.create(app, iconHeight, -1, false);
			//			slider.setSnapToTicks(true);
			btnGridStyle.addPopupHandler(new PopupMenuHandler() {

				public void fireActionPerformed(PopupMenuButton actionButton) {
					model.appyGridStyle(EuclidianView
							.getLineType(btnGridStyle.getSelectedIndex()));

				}});
			btnGridStyle.setKeepVisible(false);

			// color
			lblColor = new Label();
			btGridColor = new MyCJButton();
			btGridColor.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					if (cbShowGrid.getValue() == false) {
						return;
					}
	              app.getDialogManager().showColorChooserDialog(model.getGridColor(),
	            		new ColorChangeHandler() {
						
						public void onForegroundSelected() {
							// TODO Auto-generated method stub
							
						}
						
						public void onColorChange(GColor color) {
							model.applyGridColor(color);
							updateGridColorButton(color);
						}
						
						public void onClearBackground() {
							// TODO Auto-generated method stub
							
						}
						
						public void onBackgroundSelected() {
							// TODO Auto-generated method stub
							
						}
						
						public void onAlphaChange() {
							// TODO Auto-generated method stub
							
						}

						public void onBarSelected() {
							// TODO Auto-generated method stub

						}
					});
				// Just for dummy.
//				
				}
			});
			// bold
			cbBoldGrid = new CheckBox();
			cbBoldGrid.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					model.applyBoldGrid(cbBoldGrid.getValue());
					updateView();
				}
			});

			// style panel
			FlowPanel stylePanel = new FlowPanel();

			stylePanel.add(LayoutUtilW.panelRowIndent(btnGridStyle));
			stylePanel.add(LayoutUtilW.panelRowIndent(lblColor, btGridColor, cbBoldGrid));
			
			addOnlyFor2D(stylePanel);
		}
		
		
		protected void addOnlyFor2D(Widget w){
			mainPanel.add(w);
		}


		public void setLabels() {
			cbShowGrid.setText(loc.getMenu("ShowGrid"));
	        int idx = lbGridType.getSelectedIndex();
	        setGridTypeLabel();
	        lbGridType.clear();
	        model.fillGridTypeCombo();
	        lbGridType.setSelectedIndex(idx);
	        
	        idx = lbGridTickAngle.getSelectedIndex();
	        lbGridTickAngle.clear();
	        model.fillAngleOptions();
	        lbGridTickAngle.setSelectedIndex(idx);
			cbGridManualTick.setText(loc.getMenu("TickDistance") + ":");
			lblGridStyle.setText(loc.getMenu("LineStyle"));
			lblColor.setText(loc.getMenu("Color") + ":");
			cbBoldGrid.setText(loc.getMenu("Bold"));
		}
		
		protected void setGridTypeLabel(){
			lblGridType.setText(loc.getMenu("GridType"));
		}

		public void addGridTypeItem(String item) {
	        lbGridType.addItem(item);
        }

		public void addAngleOptionItem(String item) {
	       lbGridTickAngle.addItem(item);
        }
		

		public void update(GColor color, boolean isShown, boolean isBold,
				int gridType) {

			enableGrid(isShown);
			cbShowGrid.setValue(isShown);
			cbBoldGrid.setValue(isBold);
			lbGridType.setSelectedIndex(gridType);
			btGridColor.getElement().getStyle().setColor(StringUtil.toHtmlColor(color));
			updateGridColorButton(color);
		}
	
		public void updateTicks(boolean isAutoGrid, double[] gridTicks,
				int gridType) {
	
			if (gridType != EuclidianView.GRID_POLAR) {

				ncbGridTickY.setVisible(true);
				gridLabel2.setVisible(true);
				lbGridTickAngle.setVisible(false);
				gridLabel3.setVisible(false);

				ncbGridTickX.setDoubleValue(gridTicks[0]);
				ncbGridTickY.setDoubleValue(gridTicks[1]);
				gridLabel1.setText("x:");

			} else {
				ncbGridTickY.setVisible(false);
				gridLabel2.setVisible(false);
				lbGridTickAngle.setVisible(true);
				gridLabel3.setVisible(true);

				ncbGridTickX.setDoubleValue(gridTicks[0]);
				int val = (int) (view.getGridDistances(2) * 12 / Math.PI) - 1;
				if (val == 5)
					val = 4; // handle Pi/2 problem
				lbGridTickAngle.setSelectedIndex(val);
				gridLabel1.setText("r:");
			}

			ncbGridTickX.setEnabled(!isAutoGrid);
			ncbGridTickY.setEnabled(!isAutoGrid);
			lbGridTickAngle.setEnabled(!isAutoGrid);
		}

		public void selectGridStyle(int style) {
	        btnGridStyle.selectLineType(style);
        }
		
		public void updateGridColorButton(GColor color) {
			ImageOrText content = new ImageOrText();
			content.setBgColor(color);
			btGridColor.setIcon(content);
		}
	
	}
	
	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.view = (EuclidianView) activeEuclidianView;
		model = new EuclidianOptionsModel(app, view, this);
		initGUI();
		isIniting = false;
    }

	/**
	 * update the view (also for model)
	 * 
	 * @param view
	 *            view
	 */
	public void updateView(EuclidianView view) {
		setView(view);
		view.setOptionPanel(this);
		model.setView(view);
		xAxisTab.updateView(view);
		yAxisTab.updateView(view);
	}

	private void initGUI() {
		tabPanel = new MultiRowsTabPanel();
		addTabs();
		updateGUI();
	    tabPanel.selectTab(0);
		app.setDefaultCursor();
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			public void onSelection(SelectionEvent<Integer> event) {
				updateGUI();
			}
		});
    }
	
	/**
	 * add tabs
	 */
	protected void addTabs(){
		addBasicTab();
		addAxesTabs();
		addGridTab();
	}
	
	/**
	 * add tabs for axes
	 */
	protected void addAxesTabs(){
		addXAxisTab();
		addYAxisTab();
	}

	private void addBasicTab() {
		basicTab = newBasicTab();
		tabPanel.add(basicTab, "basic");
	}
	
	protected BasicTab newBasicTab(){
		return new BasicTab();
	}
	
	private void addXAxisTab() {
		xAxisTab = newAxisTab(EuclidianOptionsModel.X_AXIS);
		tabPanel.add(xAxisTab, "x");
	}
	
	private void addYAxisTab() {
		yAxisTab = newAxisTab(EuclidianOptionsModel.Y_AXIS);
		tabPanel.add(yAxisTab, "y");
	}
	
	/**
	 * 
	 * @param axis axis id
	 * @return axis tab
	 */
	protected AxisTab newAxisTab(int axis){
		return new AxisTab(axis, false);
	}
	
	private void addGridTab() {
		gridTab = newGridTab();
		tabPanel.add(gridTab, "grid");
	}
	
	/**
	 * 
	 * @return new grid tab
	 */
	protected GridTab newGridTab(){
		return new GridTab();
	}

	/**
	 * set labels
	 * @param tabBar tab bar
	 * @param gridIndex index for grid tab
	 */
	protected void setLabels(Widget tabBar2, int gridIndex) {

		MultiRowsTabBar tabBar = (MultiRowsTabBar) tabBar2;

		tabBar.setTabText(0, loc.getMenu("Properties.Basic"));
		tabBar.setTabText(1, loc.getMenu("xAxis"));
		tabBar.setTabText(2, loc.getMenu("yAxis"));
		tabBar.setTabText(gridIndex, loc.getMenu("Grid"));



		basicTab.setLabels();
		xAxisTab.setLabels();
		yAxisTab.setLabels();
		gridTab.setLabels();
	}

	/**
	 * set labels
	 */
	public void setLabels() {
			setLabels(((MultiRowsTabPanel) tabPanel).getTabBar(), 3);

	}

	public void setView(EuclidianView euclidianView1) {
		this.view = euclidianView1;
		if (!isIniting) {
			updateGUI();
		}
    }

	public void showCbView(boolean b) {
		Log.warn("showCbView");
	        // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateGUI() {
	    model.updateProperties();
	    setLabels();
    }

	@Override
    public void updateBounds() {
	    basicTab.updateBounds();
    }

	public Widget getWrappedPanel() {
		return (Widget) tabPanel;
    }
	
	protected AutoCompleteTextFieldW getTextField() {
		InputPanelW input = new InputPanelW(null, app, 1, -1, true);
		AutoCompleteTextFieldW tf = input.getTextComponent();
		tf.setStyleName("numberInput");
		return tf;
	}
	
	public GColor getEuclidianBackground(int viewNumber) {
		return app.getSettings().getEuclidian(viewNumber).getBackground();
	}

	public void enableAxesRatio(boolean value) {
		basicTab.enableAxesRatio(value);
	}		

	public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
		basicTab.setMinMaxText(minX, maxX, minY, maxY);
	}

	public void addTooltipItem(String item) {
	    lbTooltips.addItem(item);
    }

	public void updateAxes(GColor color, boolean isShown, boolean isBold) {
		basicTab.updateAxes(color, isShown, isBold);
	}

	public void updateBackgroundColor(GColor color) {
		basicTab.updateBackgroundColorButton(color);
	}
	
	public void selectTooltipType(int index) {
		lbTooltips.setSelectedIndex(index);
	}

	public void updateConsProtocolPanel(boolean isVisible) {
	    basicTab.updateConsProtocolPanel(isVisible);
    }

	public void updateGrid(GColor color, boolean isShown, boolean isBold,
            int gridType) {
		gridTab.update(color, isShown, isBold, gridType);
    }

	public void showMouseCoords(boolean value) {
		basicTab.showMouseCoords(value);
    }

	public void selectAxesStyle(int index) {
	    basicTab.selectAxesStyle(index);
    }

	public void updateGridTicks(boolean isAutoGrid, double[] gridTicks,
            int gridType) {
		gridTab.updateTicks(isAutoGrid, gridTicks, gridType);
    }

	public void enableLock(boolean value) {
		basicTab.enabeLock(value);
	}

	public void selectGridStyle(int style) {
		if (gridTab == null) {
			return;
		}
		gridTab.selectGridStyle(style);
    }

	public void addGridTypeItem(String item) {
		if (gridTab == null) {
			return;
		}
		
		gridTab.addGridTypeItem(item);
    }

	public void addAngleOptionItem(String item) {
		if (gridTab == null) {
			return;
		}
	    gridTab.addAngleOptionItem(item);

	}
	protected void updateView() {
		view.updateBackground();
		updateGUI();
    }

	@Override
    public void onResize(int height, int width) {
		for(int i = 0; i < tabPanel.getWidgetCount(); i++) {
			EuclidianTab tab = (EuclidianTab) tabPanel.getWidget(i);
			if (tab != null) {
				tab.onResize(height, width);
			}
		}
    }

	/**
	 * select the correct tab
	 * 
	 * @param index
	 *            index
	 */
	public void setSelectedTab(int index) {
		// tabbedPane.setSelectedIndex(index);
		Log.warn("======== OptionsEuclidianW.setSelectedTab() : TODO");
	}

	public void updateAxisFontStyle(boolean serif, boolean isBold,
			boolean isItalic) {
		basicTab.cbAxisLabelSerif.setValue(serif);
		basicTab.cbAxisLabelBold.setValue(isBold);
		basicTab.cbAxisLabelItalic.setValue(isItalic);

	}

	public MultiRowsTabPanel getTabPanel() {
		return tabPanel;
	}
}

