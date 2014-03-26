package geogebra.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.dialog.handler.ColorChangeHandler;
import geogebra.common.gui.dialog.options.OptionsEuclidian;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.App;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.gui.util.LineStylePopup;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.NumberListBox;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW,
	IEuclidianOptionsListener {

	private AppW app;
	private TabPanel tabPanel;
	private EuclidianView view;
	private EuclidianOptionsModel model;
	private BasicTab basicTab;
	private AxisTab xAxisTab;
	private AxisTab yAxisTab;
	private GridTab gridTab;
	private ListBox lbTooltips;
	private abstract class EuclidianTab extends FlowPanel implements SetLabels 
	{};
	
	private class BasicTab extends EuclidianTab {
		private Label dimTitle;
		private Label[] dimLabel;
		private AutoCompleteTextFieldW tfMinX;
		private AutoCompleteTextFieldW tfMaxX;
		private AutoCompleteTextFieldW tfMinY;
		private AutoCompleteTextFieldW tfMaxY;
		private AutoCompleteTextFieldW tfAxesRatioX;
		private AutoCompleteTextFieldW tfAxesRatioY;
		
		private Label axesRatioLabel;
		private FlowPanel dimPanel;
		private ToggleButton tbLockRatio;
		private CheckBox cbShowAxes;
		private CheckBox cbBoldAxes;
		private Label colorLabel;
		private Button btAxesColor;
		private Label lineStyle;
		private FlowPanel axesOptionsPanel;
		private AutoCompleteTextFieldW axesOptionTitle;
		private Label axesOptionsTitle;
		private PopupMenuButton axesStylePopup;
		private Label backgroundColorLabel;
		private Button btBackgroundColor;
		private CheckBox cbShowMouseCoords;
		private Label tooltips;
		private Label miscTitle;
		private Label consProtocolTitle;
		private FlowPanel consProtocolPanel;
		private CheckBox cbShowNavbar;
		private CheckBox cbNavPlay;
		private CheckBox cbOpenConsProtocol;
		private CheckBox cbShowGrid;
		private CheckBox cbBoldGrid;

		public BasicTab() {
			addDimensionPanel();
			addAxesOptionsPanel();
			addConsProtocolPanel();
			addMiscPanel();
			setStyleName("propertiesTab");
		}

		private void addMinMaxHandler(final AutoCompleteTextFieldW tf, final MinMaxType type) {

			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyMinMax(tf.getText(), type);
					}
				}});

			tf.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.applyMinMax(tf.getText(), type);
				}	
			});

		}
		protected double parseDouble(String text) {
			if (text == null || text.equals(""))
				return Double.NaN;
			return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
		}
		
		private void applyAxesRatio() {
	
			model.applyAxesRatio(parseDouble(tfAxesRatioX.getText()),
					parseDouble(tfAxesRatioY.getText()));
		}
		private void addAxesRatioHandler(final AutoCompleteTextFieldW tf) {
	
			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						applyAxesRatio();
					}
				}});

			tf.addFocusListener(new FocusListener(this){
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
			
			tbLockRatio = new ToggleButton(new Image(AppResources.INSTANCE.lock()));
			tbLockRatio.setValue(view.isLockedAxesRatio());
			tbLockRatio.setEnabled(view.isZoomable());
		
			axesRatioLabel = new Label("");

			dimPanel = new FlowPanel();
			add(dimTitle);
			dimPanel.add(LayoutUtil.panelRow(dimLabel[0], tfMinX, dimLabel[1],
					tfMaxX));
			
		
			dimPanel.add(LayoutUtil.panelRow(dimLabel[2], tfMinY, dimLabel[3],
					tfMaxY));
			
			dimPanel.add(LayoutUtil.panelRow(axesRatioLabel));
			dimPanel.add(LayoutUtil.panelRow(tfAxesRatioX, new Label(" : "),
					tfAxesRatioY, tbLockRatio));
			
			add(dimPanel);
			
			addMinMaxHandler(tfMinX, MinMaxType.minX);
			addMinMaxHandler(tfMaxX, MinMaxType.maxX);

			addMinMaxHandler(tfMinY, MinMaxType.minY);
			addMinMaxHandler(tfMaxY, MinMaxType.maxY);
			
			addAxesRatioHandler(tfAxesRatioX);
			addAxesRatioHandler(tfAxesRatioY);
			
		}
		
		private void addAxesOptionsPanel() {

			axesOptionsTitle = new Label();
			axesOptionsTitle.setStyleName("panelTitle");
			// show axes checkbox
			cbShowAxes = new CheckBox(app.getPlain("ShowAxes"));

			// show bold checkbox
			cbBoldAxes = new CheckBox(app.getPlain("Bold"));

			// axes color
			colorLabel = new Label(app.getPlain("Color") + ":");

			btAxesColor = new Button("\u2588");
			
			btAxesColor.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	              app.getDialogManager().showColorChooserDialog(model.getAxesColor(),
	            		  new ColorChangeHandler() {
					
					public void onForegroundSelected() {
						// TODO Auto-generated method stub
						
					}
					
					public void onColorChange(GColor color) {
						model.applyAxesColor(color);
						
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
				});
                }});
			
			// axes style
			lineStyle = new Label(app.getPlain("LineStyle") + ":");
			final ImageOrText[] iconArray = new ImageOrText[EuclidianOptionsModel.getAxesStyleLength()];
			GDimensionW iconSize = new GDimensionW(80, 30);
			for (int i = 0; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createAxesStyleIcon(i, iconSize);
			}
			
			axesStylePopup = new PopupMenuButton(app, iconArray, -1, 1, iconSize,
					geogebra.common.gui.util.SelectionTable.MODE_ICON){
				@Override
				public void handlePopupActionEvent(){
					super.handlePopupActionEvent();
					int idx = getSelectedIndex();
					model.appyAxesStyle(idx);

				}
			};
			axesStylePopup.setKeepVisible(false);		

			// axes options panel
			axesOptionsPanel = new FlowPanel();
			axesOptionsPanel.add(axesOptionsTitle);
			axesOptionsPanel.add(LayoutUtil.panelRow(cbShowAxes,
					 cbBoldAxes));
			axesOptionsPanel.add(LayoutUtil.panelRow(colorLabel, btAxesColor,
					 lineStyle, axesStylePopup));
			cbShowAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.showAxes(cbShowAxes.getValue());

                }});
			
			cbBoldAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyBoldAxes(cbBoldAxes.getValue(), cbShowAxes.getValue());

                }});
			
			tbLockRatio.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					if (tbLockRatio.getValue()) {
						model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
								/ parseDouble(tfAxesRatioY.getText()));
					} else {
						model.applyLockRatio(null);
					}

                }});
			
			add(axesOptionsPanel);
		}

		private void toggleConsProtButton() {
			ConstructionProtocolNavigationW cpn = (ConstructionProtocolNavigationW) app
					.getGuiManager().getConstructionProtocolNavigation();
			cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
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
			consProtocolPanel.add(cbNavPlay);

			cbOpenConsProtocol = new CheckBox();
			consProtocolPanel.add(cbOpenConsProtocol);

			cbShowNavbar.setStyleName("checkBoxPanel");
			cbNavPlay.setStyleName("checkBoxPanel");
			cbOpenConsProtocol.setStyleName("checkBoxPanel");

			add(consProtocolTitle);
			add(consProtocolPanel);
			
			cbShowNavbar.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					app.toggleShowConstructionProtocolNavigation();
                }});
			
			cbNavPlay.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					toggleConsProtButton();
				}});
			
			cbOpenConsProtocol.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					toggleConsProtButton();
				}});
			
						
			
        }

		private void addMiscPanel() {
			miscTitle = new Label();
			miscTitle.setStyleName("panelTitle");
			// background color panel
			backgroundColorLabel = new Label(app.getPlain("BackgroundColor") + ":");
	
			btBackgroundColor = new Button("\u2588");

			// show mouse coords
			cbShowMouseCoords = new CheckBox();

			// show tooltips
			tooltips = new Label(app.getPlain("Tooltips") + ":");
			lbTooltips = new ListBox();
			model.fillTooltipCombo();
			
			FlowPanel miscPanel = new FlowPanel();
			miscPanel.add(miscTitle);
			miscPanel.add(LayoutUtil.panelRow(backgroundColorLabel, btBackgroundColor));
			miscPanel.add(LayoutUtil.panelRow(tooltips, lbTooltips));
			miscPanel.add(LayoutUtil.panelRow(cbShowMouseCoords));
			add(miscPanel);
    
			btBackgroundColor.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
		              app.getDialogManager().showColorChooserDialog(model.getBackgroundColor(),
		           		  new ColorChangeHandler() {
							
							public void onForegroundSelected() {
								// TODO Auto-generated method stub
								
							}
							
							public void onColorChange(GColor color) {
								
								model.applyBackgroundColor();
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

		public void setLabels() {
			dimTitle.setText(app.getPlain("Dimensions"));
			
			dimLabel[0].setText(app.getPlain("xmin") + ":");
			dimLabel[1].setText(app.getPlain("xmax") + ":");
			dimLabel[2].setText(app.getPlain("ymin") + ":");
			dimLabel[3].setText(app.getPlain("ymax") + ":");
			axesRatioLabel.setText(app.getPlain("xAxis") + " : "
					+ app.getPlain("yAxis"));
			
			axesOptionsTitle.setText(app.getPlain("Axes"));
			cbShowAxes.setText(app.getPlain("ShowAxes"));
			cbBoldAxes.setText(app.getPlain("Bold"));
			colorLabel.setText(app.getPlain("Color") + ":");
			lineStyle.setText(app.getPlain("LineStyle") + ":");

			miscTitle.setText(app.getPlain("Miscellaneous"));
			lbTooltips.clear();
			backgroundColorLabel.setText(app.getPlain("BackgroundColor") + ":");
			int index = lbTooltips.getSelectedIndex();
			model.fillTooltipCombo();
			lbTooltips.setSelectedIndex(index);
			cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));
			
			consProtocolTitle.setText(app
				.getPlain("ConstructionProtocolNavigation"));
			
			cbShowNavbar.setText(app.getPlain("Show"));
			cbNavPlay.setText(app.getPlain("PlayButton"));
			cbOpenConsProtocol.setText(app.getPlain("ConstructionProtocolButton"));

			
		}

		public void enableAxesRatio(boolean value) {
			tfAxesRatioX.getTextBox().setEnabled(value);
			tfAxesRatioY.getTextBox().setEnabled(value);
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
		
		}

		public void updateGrid(GColor color, boolean isShown, boolean isBold, int gridType) {
		//	btGridColor.setForeground(geogebra.awt.GColorD.getAwtColor(view
		//			.getGridColor()));

			cbShowGrid.setValue(view.getShowGrid());
			cbBoldGrid.setValue(view.getGridIsBold());
	    }
		
		public void updateConsProtocolPanel(boolean isVisible) {
			// cons protocol panel
			cbShowNavbar.setValue(isVisible);
			cbNavPlay.setEnabled(((GuiManagerW) app.getGuiManager()).isConsProtNavigationPlayButtonVisible());
			cbOpenConsProtocol.setValue(((GuiManagerW) app.getGuiManager())
					.isConsProtNavigationProtButtonVisible());

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
	        tbLockRatio.setValue(value);
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
				tfAxesRatioY.setText("" + xscale / yscale);
			} else {
				tfAxesRatioX.setText("" + yscale / xscale);
				tfAxesRatioY.setText("1");
			}

        }

	}
	
	private class AxisTab extends EuclidianTab {
		private AxisPanel axisPanel;
		
		public AxisTab(int axis) {
			axisPanel = new AxisPanel(app, view, axis);
			setStyleName("propertiesTab");
			add(axisPanel);
		}
		
		public void setLabels() {
	        axisPanel.setLabels();
        }
		
	}
		
	private class GridTab extends EuclidianTab {
		private static final int iconHeight = 24;
		private CheckBox cbShowGrid;
		private ListBox lbGridType;
		private CheckBox cbGridManualTick;
		private NumberListBox ncbGridTickX;
		private NumberListBox ncbGridTickY;
		private ListBox lbGridTickAngle;
		private Label gridLabel1;
		private Label gridLabel2;
		private Label gridLabel3;
		LineStylePopup btnGridStyle;
		private Label lblColor;
		private CheckBox cbBoldGrid;
		private Button btGridColor;
		public GridTab() {
			setStyleName("propertiesTab");
			cbShowGrid = new CheckBox();
			cbShowGrid.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	                model.showGrid(cbShowGrid.getValue());
                }});
			add(cbShowGrid);
			initGridTypePanel();
			initGridStylePanel();
		}
		
		private void initGridTypePanel() {

			// grid type combo box
			lbGridType = new ListBox();
			
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
			
			ncbGridTickX = new NumberListBox(app);
			ncbGridTickY = new NumberListBox(app);
			ncbGridTickX.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyGridTicks(ncbGridTickX.getValue(), 0);
					updateView();
				}});
			
			ncbGridTickY.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyGridTicks(ncbGridTickY.getValue(), 1);
					updateView();
					}});

			// checkbox for grid labels
			lbGridTickAngle = new ListBox();
		
			
			// grid labels
			gridLabel1 = new Label("x:");
			gridLabel2 = new Label("y:");
			gridLabel3 = new Label("\u03B8" + ":"); // Theta
		
			FlowPanel tickPanel = LayoutUtil.panelRow(cbGridManualTick, gridLabel1,
					ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
					lbGridTickAngle);
			
			add(tickPanel);
			
			FlowPanel typePanel = new FlowPanel();
			typePanel.add(lbGridType);
			typePanel.add(cbGridManualTick);
			typePanel.add(LayoutUtil.panelRow(
					gridLabel1, ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
					lbGridTickAngle));

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
			add(typePanel);
		}

		protected void updateView() {
			view.updateBackground();
			updateGUI();
        }

		private void initGridStylePanel() {

			// line style
			btnGridStyle = LineStylePopup.create(app, iconHeight, -1, false);
			//			slider.setSnapToTicks(true);
			btnGridStyle.addPopupHandler(new PopupMenuHandler() {

				public void fireActionPerformed(Object actionButton) {
					model.appyGridStyle(btnGridStyle.getSelectedIndex());

				}});
			btnGridStyle.setKeepVisible(false);

			// color
			lblColor = new Label();
			btGridColor = new Button("\u2588");
			btGridColor.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
	              app.getDialogManager().showColorChooserDialog(model.getGridColor(),
	            		new ColorChangeHandler() {
						
						public void onForegroundSelected() {
							// TODO Auto-generated method stub
							
						}
						
						public void onColorChange(GColor color) {
							model.applyGridColor(color);
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

			stylePanel.add(LayoutUtil.panelRow(btnGridStyle));
			stylePanel.add(LayoutUtil.panelRow(lblColor, btGridColor, cbBoldGrid));
			
			add(stylePanel);
		}


		public void setLabels() {
	        cbShowGrid.setText(app.getPlain("ShowGrid"));
	        int idx = lbGridType.getSelectedIndex();
	        lbGridType.clear();
	        model.fillGridTypeCombo();
	        lbGridType.setSelectedIndex(idx);
	        
	        idx = lbGridTickAngle.getSelectedIndex();
	        model.fillAngleOptions();
	        lbGridTickAngle.setSelectedIndex(idx);
			cbGridManualTick.setText(app.getPlain("TickDistance") + ":");
			lblColor.setText(app.getPlain("Color") + ":");
			cbBoldGrid.setText(app.getMenu("Bold"));
		}

		public void addGridTypeItem(String item) {
	        lbGridType.addItem(item);
        }

		public void addAngleOptionItem(String item) {
	       lbGridTickAngle.addItem(item);
        }
		

		public void update(GColor color, boolean isShown, boolean isBold,
				int gridType) {
		//	btGridColor.setForeground(geogebra.awt.GColorD.getAwtColor(color));

			cbShowGrid.setValue(isShown);
			cbBoldGrid.setValue(isBold);
			lbGridType.setSelectedIndex(gridType);
			
		}
		
		public void updateTicks(boolean isAutoGrid, double[] gridTicks,
				int gridType) {
	
			if (gridType != EuclidianView.GRID_POLAR) {

				ncbGridTickY.setVisible(true);
				gridLabel2.setVisible(true);
				lbGridTickAngle.setVisible(false);
				gridLabel3.setVisible(false);

				ncbGridTickX.setValue(gridTicks[0]);
				ncbGridTickY.setValue(gridTicks[1]);
				gridLabel1.setText("x:");

			} else {
				ncbGridTickY.setVisible(false);
				gridLabel2.setVisible(false);
				lbGridTickAngle.setVisible(true);
				gridLabel3.setVisible(true);

				ncbGridTickX.setValue(gridTicks[0]);
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

	}
	
	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		this.app = app;
		this.view = (EuclidianView) activeEuclidianView;
		model = new EuclidianOptionsModel(app, view, this);
		initGUI();
    }

	private void initGUI() {
		tabPanel = new TabPanel();
		addBasicTab();
		addXAxisTab();
		addYAxisTab();
		addGridTab();
		updateGUI();
	    tabPanel.selectTab(0);
		app.setDefaultCursor();
    }

	private void addBasicTab() {
		basicTab = new BasicTab();
		tabPanel.add(basicTab, "basic");
	}
	
	private void addXAxisTab() {
		xAxisTab = new AxisTab(EuclidianOptionsModel.X_AXIS);
		tabPanel.add(xAxisTab, "x");
	}
	
	private void addYAxisTab() {
		yAxisTab = new AxisTab(EuclidianOptionsModel.Y_AXIS);
		tabPanel.add(yAxisTab, "y");
	}
	
	private void addGridTab() {
		gridTab = new GridTab();
		tabPanel.add(gridTab, "grid");
	}
	
	public void setLabels() {
	    TabBar tabBar = tabPanel.getTabBar();
	    tabBar.setTabText(0, app.getMenu("Properties.Basic"));
	    tabBar.setTabText(1, app.getPlain("xAxis"));
	    tabBar.setTabText(2, app.getPlain("yAxis"));
	    tabBar.setTabText(3, app.getPlain("Grid"));
	    
	    basicTab.setLabels();
	    xAxisTab.setLabels();
	    yAxisTab.setLabels();
	    gridTab.setLabels();
	    
    }

	public void setView(EuclidianViewWeb euclidianView1) {
	    // TODO Auto-generated method stub
	    App.debug("setView");
    }

	public void showCbView(boolean b) {
	    App.debug("showCbView");
	        // TODO Auto-generated method stub
	    
    }

	public void updateGUI() {
	    model.updateProperties();	    
	    setLabels();
    }

	@Override
    public void updateBounds() {
	    basicTab.updateBounds();
    }

	public Widget getWrappedPanel() {
	    return tabPanel;
    }
	
	private AutoCompleteTextFieldW getTextField() {
		InputPanelW input = new InputPanelW(null, (AppW) app, 1, -1, true);
		AutoCompleteTextFieldW tf = (AutoCompleteTextFieldW)input.getTextComponent();
		tf.setStyleName("numberInput");
		return tf;
	}
	
	public GColor getEuclidianBackground(int viewNumber) {
		return null;
//		return new GColorW(
//				((GuiManagerW) (app.getGuiManager()))
//						.showColorChooser(app.getSettings()
//								.getEuclidian(viewNumber).getBackground()));
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
	    // TODO Auto-generated method stub
	    
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
}

