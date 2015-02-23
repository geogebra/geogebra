package geogebra.gui.view.probcalculator;

import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabiltyCalculatorStyleBar;
import geogebra.common.main.App;
import geogebra.gui.util.MyToggleButton;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;

/**
 * StyleBar for the ProbabilityCalculator view
 * 
 * @author G. Sturr
 * 
 */
public class ProbabiltyCalculatorStyleBarD extends ProbabiltyCalculatorStyleBar implements
		ActionListener {

	private static final long serialVersionUID = 1L;

	

	/** rounding button */
	JButton btnRounding;

	/** rounding popup menu */
	JPopupMenu roundingPopup;
	
	JToolBar wrappedToolbar;

	private MyToggleButton btnCumulative, btnLineGraph, btnGrid, btnStepGraph,
			btnBarGraph, btnExport, btnNormalOverlay;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator
	 */
	public ProbabiltyCalculatorStyleBarD(AppD app, ProbabilityCalculatorViewD probCalc) {
		
		this.wrappedToolbar = new JToolBar();
		this.probCalc = probCalc;
		this.app = app;
		wrappedToolbar.setFloatable(false);
		createGUI();
		updateLayout();
		updateGUI();
		setLabels();

	}

	public void updateIcons() {
		if (btnCumulative == null) {
			return;
		}
		iconHeight = ((AppD) app).getScaledIconSize();
		btnCumulative.setIcon(((AppD) app)
				.getScaledIcon("cumulative_distribution.png"));
		btnLineGraph.setIcon(((AppD) app).getScaledIcon("line_graph.png"));
		btnStepGraph.setIcon(((AppD) app).getScaledIcon("step_graph.png"));
		btnBarGraph.setIcon(((AppD) app).getScaledIcon("bar_graph.png"));
		btnGrid.setIcon(((AppD) app).getScaledIcon("grid.gif"));
		btnExport.setIcon(((AppD) app).getScaledIcon("export16.png"));
		btnNormalOverlay.setIcon(((AppD) app)
				.getScaledIcon("normal-overlay.png"));

	}

	private void createGUI() {
		iconHeight = ((AppD) app).getScaledIconSize();

		wrappedToolbar.removeAll();
		buildOptionsButton();

		btnCumulative = new MyToggleButton(
				((AppD) app).getScaledIcon("cumulative_distribution.png"),
				iconHeight);
		btnCumulative.setSelected(probCalc.isCumulative());
		btnCumulative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((ProbabilityCalculatorViewD) probCalc).setCumulative(!probCalc.isCumulative());
			}
		});

		btnLineGraph = new MyToggleButton(
				((AppD) app).getScaledIcon("line_graph.png"),
				iconHeight);
		btnLineGraph.addActionListener(this);

		btnStepGraph = new MyToggleButton(
				((AppD) app).getScaledIcon("step_graph.png"),
				iconHeight);
		btnStepGraph.addActionListener(this);

		btnBarGraph = new MyToggleButton(
				((AppD) app).getScaledIcon("bar_graph.png"),
				iconHeight);
		btnBarGraph.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnBarGraph);
		gp.add(btnLineGraph);
		gp.add(btnStepGraph);

		btnGrid = new MyToggleButton(((AppD) app).getScaledIcon("grid.gif"),
				iconHeight);
		btnGrid.setSelected(probCalc.getPlotSettings().showGrid);
		btnGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlotSettings ps = probCalc.getPlotSettings();
				ps.showGrid = !ps.showGrid;
				probCalc.setPlotSettings(ps);
				probCalc.updatePlotSettings();
			}
		});

		// create export button
		btnExport = new MyToggleButton(
				((AppD) app).getScaledIcon("export16.png"),
				iconHeight);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		// create normal overlay button
		btnNormalOverlay = new MyToggleButton(
				((AppD) app).getScaledIcon("normal-overlay.png"), iconHeight);
		btnNormalOverlay.setFocusable(false);
		btnNormalOverlay.addActionListener(this);

	}

	/**
	 * Updates the button layout to fit the selected ProbabilityCalculator tab
	 */
	public void updateLayout() {

		wrappedToolbar.removeAll();

		if (((ProbabilityCalculatorViewD) probCalc).isDistributionTabOpen()) {
			// add(btnRounding);
			// addSeparator();
			// add(btnCumulative);
			// addSeparator();
			wrappedToolbar.add(btnLineGraph);
			wrappedToolbar.add(btnStepGraph);
			wrappedToolbar.add(btnBarGraph);
			wrappedToolbar.addSeparator();
			wrappedToolbar.add(btnNormalOverlay);

			wrappedToolbar.addSeparator();
			wrappedToolbar.add(btnExport);
			// add(btnGrid); (grid doesn't work well with discrete graphs and
			// point
			// capturing)
		}else{
			// keep bar height uniform 
			wrappedToolbar.add(Box.createVerticalStrut(20));
		}
		wrappedToolbar.revalidate();
		wrappedToolbar.repaint();

	}

	/**
	 * Updates the GUI
	 */
	public void updateGUI() {

		iconHeight = ((AppD) app).getScaledIconSize();
		btnLineGraph.setVisible(((ProbabilityCalculatorViewD) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnStepGraph.setVisible(((ProbabilityCalculatorViewD) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnBarGraph.setVisible(((ProbabilityCalculatorViewD) probCalc).getProbManager().isDiscrete(
				probCalc.getSelectedDist()));

		btnLineGraph.removeActionListener(this);
		btnStepGraph.removeActionListener(this);
		btnBarGraph.removeActionListener(this);
		btnNormalOverlay.removeActionListener(this);

		btnLineGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewD.GRAPH_LINE);
		btnStepGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewD.GRAPH_STEP);
		btnBarGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculatorViewD.GRAPH_BAR);

		btnNormalOverlay.setSelected(probCalc.isShowNormalOverlay());
		btnNormalOverlay.setVisible(probCalc.isOverlayDefined());
		btnLineGraph.addActionListener(this);
		btnStepGraph.addActionListener(this);
		btnBarGraph.addActionListener(this);
		btnNormalOverlay.addActionListener(this);
	}

	/**
	 * Updates localized labels
	 */
	public void setLabels() {
		btnRounding.setText(app.getMenu("Rounding"));
		btnExport.setToolTipText(app.getMenu("Export"));
		btnLineGraph.setToolTipText(app.getMenu("LineGraph"));

		btnStepGraph.setToolTipText(app.getMenu("StepGraph"));
		btnBarGraph.setToolTipText(app.getMenu("BarChart"));
		btnNormalOverlay.setToolTipText(app.getMenu("OverlayNormalCurve"));

		// btnCumulative.setToolTipText(app.getMenu("Cumulative"));

	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildOptionsButton() {

		btnRounding = new JButton(
				((AppD) app).getScaledIcon("triangle-down.png"));
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
		roundingPopup = createRoundingPopup();

		btnRounding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// popup appears below the button
				roundingPopup.show(wrappedToolbar.getParent(), btnRounding.getLocation().x,
						btnRounding.getLocation().y + btnRounding.getHeight());
			}
		});

		updateMenuDecimalPlaces(roundingPopup);

		((AppD) app).setComponentOrientation(roundingPopup);

	}

	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(JPopupMenu menu) {
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
			MenuElement[] m = menu.getSubElements();
			((JRadioButtonMenuItem) m[pos]).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private JPopupMenu createRoundingPopup() {
		JPopupMenu menu = new JPopupMenu();
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();

		addRadioButtonMenuItems(menu, this, strDecimalSpaces,
				App.strDecimalSpacesAC, 0);

		return menu;
	}

	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	private void addRadioButtonMenuItems(JPopupMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			} else {
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(((AppD) app).getFontCanDisplayAwt(text));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);
				((ProbabilityCalculatorViewD) probCalc).updatePrintFormat(decimals, -1);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				// Application.debug("figures " + figures);
				((ProbabilityCalculatorViewD) probCalc).updatePrintFormat(-1, figures);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

		else if (e.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewD.GRAPH_LINE);
		}

		else if (e.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewD.GRAPH_BAR);
		}

		else if (e.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculatorViewD.GRAPH_STEP);
		}

		else if (e.getSource() == btnNormalOverlay) {
			probCalc.setShowNormalOverlay(btnNormalOverlay.isSelected());
			probCalc.updateAll();
		}

		
		else if (e.getSource() == btnExport) {
			JPopupMenu menu = ((ProbabilityCalculatorViewD) probCalc).getPlotPanel().getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

	}
	
	/**
	 * @return the wrapped toolbar
	 */
	public JToolBar getWrappedToolbar() {
		return wrappedToolbar;
	}

}
