package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabiltyCalculatorStyleBar;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GCheckBoxMenuItem;
import geogebra.web.gui.menubar.GRadioButtonMenuItem;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.helper.SafeHtmlFactory;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor Probability Calculator Stylebar for web
 *
 */
public class ProbabilityCalculatorStyleBarW extends
        ProbabiltyCalculatorStyleBar implements ValueChangeHandler<Boolean>, ClickHandler, ScheduledCommand {
	
	private MenuBar wrappedToolbar;
	private MenuItem btnRounding;
	private MyMenuBar roundingPopup;
	private MyToggleButton2 btnCumulative;
	private GCheckBoxMenuItem btnLineGraph;
	private GCheckBoxMenuItem btnStepGraph;
	private GCheckBoxMenuItem btnBarGraph;
	private MyToggleButton2 btnGrid;
	private MenuItem btnExport;
	private GCheckBoxMenuItem btnNormalOverlay;

	public ProbabilityCalculatorStyleBarW(App app, ProbabilityCalculatorViewW probCalc) {
		this.wrappedToolbar = new MenuBar();
		this.probCalc = probCalc;
		this.app = app;
		
		createGUI();
		updateLayout();
		updateGUI();
	}

	private void updateGUI() {
		btnLineGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnStepGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnBarGraph.setVisible(((ProbabilityCalculatorViewW) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));

		//btnLineGraph.removeActionListener(this);
		//btnStepGraph.removeActionListener(this);
		//btnBarGraph.removeActionListener(this);
		//btnNormalOverlay.removeActionListener(this);

		btnLineGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_LINE);
		btnStepGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_STEP);
		btnBarGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewW.GRAPH_BAR);

		btnNormalOverlay.setSelected(probCalc.isShowNormalOverlay());

		//btnLineGraph.addActionListener(this);
		//btnStepGraph.addActionListener(this);
		//btnBarGraph.addActionListener(this);
		//btnNormalOverlay.addActionListener(this);
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
		
		btnLineGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.line_graph()));
		btnLineGraph.setScheduledCommand(this);
		
		btnStepGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.step_graph()));
		btnStepGraph.setScheduledCommand(this);
		
		btnBarGraph = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.bar_graph()));
		btnBarGraph.setScheduledCommand(this);
		
		btnGrid = new MyToggleButton2(AppResources.INSTANCE.grid());
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
		
		btnExport = new MenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.export16()));
		btnExport.setScheduledCommand(this);
		
		btnNormalOverlay = new GCheckBoxMenuItem(SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.normal_overlay()));
		btnNormalOverlay.setScheduledCommand(this);
	    
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
			wrappedToolbar.addItem(btnNormalOverlay);

			wrappedToolbar.addSeparator();
			wrappedToolbar.addItem(btnExport);
			// add(btnGrid); (grid doesn't work well with discrete graphs and
			// point
			// capturing)
		}else{
			// keep bar height uniform 
			///wrappedToolbar.add(Box.createVerticalStrut(20));
		}
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    // TODO Auto-generated method stub
	    
    }
	
	private class MyMenuBar extends MenuBar {
		
		public MyMenuBar() {
			super();
		}
		
		public Object[] getMenuItems() {
			return super.getItems().toArray();
		}
		
	}

	@Override
    public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void execute() {
	    // TODO Auto-generated method stub
	    
    }

}
