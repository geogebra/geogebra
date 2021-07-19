package org.geogebra.web.full.gui.view.probcalculator;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorStyleBar;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.GCheckBoxMenuItem;
import org.geogebra.web.full.gui.menubar.GRadioButtonMenuItem;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.helper.SafeHtmlFactory;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author gabor Probability Calculator Stylebar for web
 *
 */
public class ProbabilityCalculatorStyleBarW extends
		ProbabilityCalculatorStyleBar implements ValueChangeHandler<Boolean> {
	
	private AriaMenuBar wrappedToolbar;
	private AriaMenuItem btnRounding;
	private AriaMenuBar roundingPopup;
	private GCheckBoxMenuItem btnLineGraph;
	private GCheckBoxMenuItem btnStepGraph;
	private GCheckBoxMenuItem btnBarGraph;
	//private MenuItem btnExport;
	//private GCheckBoxMenuItem btnNormalOverlay;
	private HandlerRegistration btnLineGraphHandler;
	private HandlerRegistration btnStepGraphHandler;
	private HandlerRegistration btnBarGraphHandler;

	/**
	 * @param app Application
	 * @param probCalc ProbabilityCalculatorViewW
	 */
	public ProbabilityCalculatorStyleBarW(App app, ProbabilityCalculatorViewW probCalc) {
		super(app, probCalc);
		this.wrappedToolbar = new AriaMenuBar();
		this.wrappedToolbar.addStyleName("ProbabilityCalculatorStyleBarW");

		createGUI();
		updateLayout();
		updateGUI();
		setLabels();
	}
	
	/**
	 * Updates localized labels
	 */
	public void setLabels() {
		Localization loc = getApp().getLocalization();
		btnRounding.setContent(loc.getMenu("Rounding"), false);
		// btnExport.setTitle(loc.getMenu("Export"));
		btnLineGraph.setTitle(loc.getMenu("LineGraph"));

		btnStepGraph.setTitle(loc.getMenu("StepGraph"));
		btnBarGraph.setTitle(loc.getMenu("BarChart"));
		// btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));

		// btnCumulative.setToolTipText(loc.getMenu("Cumulative"));
	}

	/**
	 * Update UI
	 */
	void updateGUI() {
		btnLineGraph.setVisible(getProbCalc().getProbManager().isDiscrete(
				getProbCalc().getSelectedDist()));
		btnStepGraph.setVisible(getProbCalc().getProbManager().isDiscrete(
				getProbCalc().getSelectedDist()));
		btnBarGraph.setVisible(getProbCalc().getProbManager()
				.isDiscrete(getProbCalc().getSelectedDist()));

		btnLineGraphHandler.removeHandler();
		btnStepGraphHandler.removeHandler();
		btnBarGraphHandler.removeHandler();

		btnLineGraph
				.setSelected(getProbCalc().getGraphType() == ProbabilityCalculatorView.GRAPH_LINE);
		btnStepGraph
				.setSelected(getProbCalc().getGraphType() == ProbabilityCalculatorView.GRAPH_STEP);
		btnBarGraph
				.setSelected(getProbCalc().getGraphType() == ProbabilityCalculatorView.GRAPH_BAR);

		//btnNormalOverlay.setSelected(probCalc.isShowNormalOverlay());

		btnLineGraphHandler = btnLineGraph.addValueChangeHandler(this);
		btnStepGraphHandler = btnStepGraph.addValueChangeHandler(this);
		btnBarGraphHandler = btnBarGraph.addValueChangeHandler(this);
		//btnNormalOverlayHandler = btnNormalOverlay.addValueChangeHandler(this);
    }

	private void createGUI() {
		wrappedToolbar.clearItems();
		buildOptionsButton();
		
		MyToggleButtonW btnCumulative = new MyToggleButtonW(
				AppResources.INSTANCE.cumulative_distribution());
		btnCumulative.setSelected(getProbCalc().isCumulative());
		btnCumulative.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				((ProbabilityCalculatorViewW) getProbCalc())
						.setCumulative(!getProbCalc().isCumulative());
			}
		});
		
		btnLineGraph = new GCheckBoxMenuItem(SafeHtmlFactory
				.getImageHtml(AppResources.INSTANCE.line_graph()), false);
		btnLineGraphHandler = btnLineGraph.addValueChangeHandler(this);

		btnStepGraph = new GCheckBoxMenuItem(SafeHtmlFactory
				.getImageHtml(AppResources.INSTANCE.step_graph()), false);
		btnStepGraphHandler = btnStepGraph.addValueChangeHandler(this);

		btnBarGraph = new GCheckBoxMenuItem(
				SafeHtmlFactory.getImageHtml(AppResources.INSTANCE.bar_graph()),
				false);
		btnBarGraphHandler = btnBarGraph.addValueChangeHandler(this);

		MyToggleButtonW btnGrid = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.grid_black(), 24));
		btnGrid.setSelected(getProbCalc().getPlotSettings().showGrid);
		btnGrid.addClickHandler(event -> {
			PlotSettings ps = getProbCalc().getPlotSettings();
			ps.showGrid = !ps.showGrid;
			getProbCalc().setPlotSettings(ps);
			getProbCalc().updatePlotSettings();
		});
		
	}

	private void buildOptionsButton() {
	    roundingPopup = createRoundingPopup();
	    Image img = new Image(AppResources.INSTANCE.triangle_down());
		btnRounding = new AriaMenuItem(img.getElement().getInnerHTML(), true,
				roundingPopup);
	    
	    updateMenuDecimalPlaces(roundingPopup);
    }
	
	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(AriaMenuBar menu) {
		int printFigures = getProbCalc().getPrintFigures();
		int printDecimals = getProbCalc().getPrintDecimals();

		if (menu == null) {
			return;
		}
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0
					&& printFigures < getOptionsMenu().figuresLookupLength()) {
				pos = getOptionsMenu().figuresLookup(printFigures);
			}
		} else {
			if (printDecimals > 0
					&& printDecimals < getOptionsMenu().decimalsLookupLength()) {
				pos = getOptionsMenu().decimalsLookup(printDecimals);
			}
		}

		try {
			ArrayList<AriaMenuItem> m = menu.getItems();
			((GRadioButtonMenuItem) m.get(pos)).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AriaMenuBar createRoundingPopup() {
		AriaMenuBar menu = new AriaMenuBar();
		
		String[] strDecimalSpaces = getApp().getLocalization().getRoundingMenu();
		addRadioButtonMenuItems(menu, this, strDecimalSpaces,
				App.getStrDecimalSpacesAC(), 0);
		
		return menu;
	}

	private void addRadioButtonMenuItems(AriaMenuBar menu,
            ValueChangeHandler<Boolean> al,
            String[] items, String[] actionCommands, int selectedPos) {
		GRadioButtonMenuItem mi;
		
		for (int i = 0; i < items.length; i++) {
			if ("---".equals(items[i])) {
				//add separator with css
			} else {
				String text = getApp().getLocalization().getMenu(items[i]);
				mi = new GRadioButtonMenuItem(text, actionCommands[i], "probstylebarradio");
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.addValueChangeHandler(al);
				menu.addItem(mi);
			}
		}
    }

	/**
	 * Update layout
	 */
	public void updateLayout() {
		wrappedToolbar.clearItems();

		if (((ProbabilityCalculatorViewW) getProbCalc()).isDistributionTabOpen()) {
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
		}
	}

	@Override
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
						((ProbabilityCalculatorViewW) getProbCalc())
								.updatePrintFormat(decimals, -1);
	
		 			} catch (Exception ex) {
						getApp().showGenericError(ex);
		 			}
		 		}
	
		 		// significant figures
		 		else if (cmd.endsWith("figures")) {
		 			try {
		 				String decStr = cmd.substring(0, 2).trim();
		 				int figures = Integer.parseInt(decStr);
		 				// Application.debug("figures " + figures);
		 				((ProbabilityCalculatorViewW) getProbCalc()).updatePrintFormat(-1, figures);
	
		 			} catch (Exception ex) {
		 				getApp().showError(ex.toString());
		 			}
		 		}
		    }
		} else if (event.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_LINE);
			}
		}

		else if (event.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected()) {
				getProbCalc().setGraphType(ProbabilityCalculatorView.GRAPH_BAR);
			}
		}

		else if (event.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_STEP);
			}
		}

//		else if (event.getSource() == btnNormalOverlay) {
//			probCalc.setShowNormalOverlay(btnNormalOverlay.isSelected());
//			probCalc.updateAll();
//		}
	}

	/**
	 * @return the toolbar wrapped to this stylebar
	 */
	public AriaMenuBar getWrappedToolBar() {
		return wrappedToolbar;
	}

}
