package org.geogebra.web.web.gui.view.probcalculator;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabiltyCalculatorStyleBar;
import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.GCheckBoxMenuItem;
import org.geogebra.web.web.gui.menubar.GRadioButtonMenuItem;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.helper.SafeHtmlFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author gabor Probability Calculator Stylebar for web
 *
 */
public class ProbabilityCalculatorStyleBarW extends
        ProbabiltyCalculatorStyleBar implements ValueChangeHandler<Boolean> {
	
	private MenuBar wrappedToolbar;
	private MenuItem btnRounding;
	private MyMenuBar roundingPopup;
	private MyToggleButton2 btnCumulative;
	private GCheckBoxMenuItem btnLineGraph;
	private GCheckBoxMenuItem btnStepGraph;
	private GCheckBoxMenuItem btnBarGraph;
	private MyToggleButton2 btnGrid;
	//private MenuItem btnExport;
	//private GCheckBoxMenuItem btnNormalOverlay;
	private HandlerRegistration btnLineGraphHandler;
	private HandlerRegistration btnStepGraphHandler;
	private HandlerRegistration btnBarGraphHandler;
	private HandlerRegistration btnNormalOverlayHandler;

	/**
	 * @param app Application
	 * @param probCalc ProbabilityCalculatorViewW
	 */
	public ProbabilityCalculatorStyleBarW(App app, ProbabilityCalculatorViewW probCalc) {
		this.wrappedToolbar = new MenuBar();
		this.wrappedToolbar.addStyleName("ProbabilityCalculatorStyleBarW");
		this.probCalc = probCalc;
		this.app = app;
		
		createGUI();
		updateLayout();
		updateGUI();
		setLabels();
	}
	
	/**
	 * Updates localized labels
	 */
	public void setLabels() {
		btnRounding.setText(app.getMenu("Rounding"));
		//btnExport.setTitle(app.getMenu("Export"));
		btnLineGraph.setTitle(app.getMenu("LineGraph"));

		btnStepGraph.setTitle(app.getMenu("StepGraph"));
		btnBarGraph.setTitle(app.getMenu("BarChart"));
		//btnNormalOverlay.setTitle(app.getMenu("OverlayNormalCurve"));

		// btnCumulative.setToolTipText(app.getMenu("Cumulative"));

	}

	void updateGUI() {
		btnLineGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnStepGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnBarGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));

		btnLineGraphHandler.removeHandler();
		btnStepGraphHandler.removeHandler();
		btnBarGraphHandler.removeHandler();
		if(btnNormalOverlayHandler != null){
			btnNormalOverlayHandler.removeHandler();
		}

		btnLineGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_LINE);
		btnStepGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_STEP);
		btnBarGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_BAR);

		//btnNormalOverlay.setSelected(probCalc.isShowNormalOverlay());

		btnLineGraphHandler = btnLineGraph.addValueChangeHandler(this);
		btnStepGraphHandler = btnStepGraph.addValueChangeHandler(this);
		btnBarGraphHandler = btnBarGraph.addValueChangeHandler(this);
		//btnNormalOverlayHandler = btnNormalOverlay.addValueChangeHandler(this);
    }

	private void createGUI() {
		wrappedToolbar.clearItems();
		buildOptionsButton();
		
		btnCumulative = new MyToggleButton2(AppResources.INSTANCE.cumulative_distribution());
		btnCumulative.setSelected(probCalc.isCumulative());
		btnCumulative.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				((ProbabilityCalculatorViewW) probCalc).setCumulative(!probCalc.isCumulative());
			}
		});
		
		btnLineGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.line_graph()), false);
		btnLineGraphHandler = btnLineGraph.addValueChangeHandler(this);
		
		btnStepGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.step_graph()), false);
		btnStepGraphHandler = btnStepGraph.addValueChangeHandler(this);
		
		btnBarGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.bar_graph()), false);
		btnBarGraphHandler = btnBarGraph.addValueChangeHandler(this);
		
		btnGrid = new MyToggleButton2(StyleBarResources.INSTANCE.grid());
		btnGrid.setSelected(probCalc.getPlotSettings().showGrid);
		btnGrid.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PlotSettings ps = probCalc.getPlotSettings();
				ps.showGrid = !ps.showGrid;
				probCalc.setPlotSettings(ps);
				probCalc.updatePlotSettings();
			}
		});
		
//		btnExport = new MenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.export16()));
//		btnExport.setScheduledCommand(new ScheduledCommand() {
//			
//			public void execute() {
//				
//				Window.open(((EuclidianViewW) ((ProbabilityCalculatorViewW)probCalc).plotPanel).getExportImageDataUrl(3, true),
//				        "_blank", null);
//				
//				
//			}
//		});
		
//		btnNormalOverlay = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.normal_overlay()), false);
//		btnNormalOverlayHandler = btnNormalOverlay.addValueChangeHandler(this);
	    
    }

	private void buildOptionsButton() {
	    roundingPopup = createRoundingPopup();
	    Image img = new Image(AppResources.INSTANCE.triangle_down());
	    btnRounding = new MenuItem(img.getElement().getInnerHTML(), true, roundingPopup);
	    
	    updateMenuDecimalPlaces(roundingPopup);
	    
	    
    }
	
	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(MyMenuBar menu) {
		int printFigures = probCalc.getPrintFigures();
		int printDecimals = probCalc.getPrintDecimals();

		if (menu == null)
			return;
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0 && printFigures < App.figuresLookup.length)
				pos = App.figuresLookup[printFigures];
		} else {
			if (printDecimals > 0 && printDecimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[printDecimals];
		}

		try {
			 Object[] m = menu.getMenuItems();
			((GRadioButtonMenuItem) m[pos]).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private MyMenuBar createRoundingPopup() {
		MyMenuBar menu = new MyMenuBar();
		
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();
		addRadioButtonMenuItems(menu, this, strDecimalSpaces, App.strDecimalSpacesAC, 0);
		
		return menu;
	}

	private void addRadioButtonMenuItems(MenuBar menu,
            ValueChangeHandler<Boolean> al,
            String[] items, String[] actionCommands, int selectedPos) {
		GRadioButtonMenuItem mi;
		
		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				//add separator with css
			} else {
				String text = app.getMenu(items[i]);
				mi = new GRadioButtonMenuItem(text, actionCommands[i], "probstylebarradio");
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.addValueChangeHandler(al);
				menu.addItem(mi);
				
				
			}
		}
	    
    }

	public void updateLayout() {
		wrappedToolbar.clearItems();

		if (((ProbabilityCalculatorViewW) probCalc).isDistributionTabOpen()) {
			// add(btnRounding);
			// addSeparator();
			// add(btnCumulative);
			// addSeparator();
			wrappedToolbar.addItem(btnLineGraph);
			wrappedToolbar.addItem(btnStepGraph);
			wrappedToolbar.addItem(btnBarGraph);
			wrappedToolbar.addSeparator();
			//wrappedToolbar.addItem(btnNormalOverlay);

			wrappedToolbar.addSeparator();
			//wrappedToolbar.addItem(btnExport);
			// add(btnGrid); (grid doesn't work well with discrete graphs and
			// point
			// capturing)
		}else{
			// keep bar height uniform 
			///wrappedToolbar.add(Box.createVerticalStrut(20));
		}
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Object source = event.getSource();
		if (source instanceof RadioButton) {
			String cmd = ((RadioButton) source).getElement().getAttribute("data-command");
		    
		    if (cmd != null) {
		 		if (cmd.endsWith("decimals")) {
		 			try {
		 				String decStr = cmd.substring(0, 2).trim();
		 				int decimals = Integer.parseInt(decStr);
		 				// Application.debug("decimals " + decimals);
		 				((ProbabilityCalculatorViewW) probCalc).updatePrintFormat(decimals, -1);
	
		 			} catch (Exception ex) {
		 				app.showError(ex.toString());
		 			}
		 		}
	
		 		// significant figures
		 		else if (cmd.endsWith("figures")) {
		 			try {
		 				String decStr = cmd.substring(0, 2).trim();
		 				int figures = Integer.parseInt(decStr);
		 				// Application.debug("figures " + figures);
		 				((ProbabilityCalculatorViewW) probCalc).updatePrintFormat(-1, figures);
	
		 			} catch (Exception ex) {
		 				app.showError(ex.toString());
		 			}
		 		}
		    }
		} else if (event.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewW.GRAPH_LINE);
		}

		else if (event.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewW.GRAPH_BAR);
		}

		else if (event.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewW.GRAPH_STEP);
		}

//		else if (event.getSource() == btnNormalOverlay) {
//			probCalc.setShowNormalOverlay(btnNormalOverlay.isSelected());
//			probCalc.updateAll();
//		}
	    
	    
    }
	
	private class MyMenuBar extends MenuBar {
		
		public MyMenuBar() {
			super();
		}
		
		public Object[] getMenuItems() {
			return super.getItems().toArray();
		}
		
	}
	
	/**
	 * @return the toolbar wrapped to this stylebar
	 */
	public MenuBar getWrappedToolBar() {
		return wrappedToolbar;
	}

}
