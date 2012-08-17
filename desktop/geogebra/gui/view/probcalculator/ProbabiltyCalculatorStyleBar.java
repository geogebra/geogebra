package geogebra.gui.view.probcalculator;

import geogebra.common.main.App;
import geogebra.gui.util.MyToggleButton;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class ProbabiltyCalculatorStyleBar extends JToolBar implements
		ActionListener {

	private static final long serialVersionUID = 1L;

	private AppD app;

	/** probabililty calculator */
	ProbabilityCalculator probCalc;

	/** icon height in pixels */
	protected int iconHeight = 18;

	/** rounding button */
	JButton btnRounding;

	/** rounding popup menu */
	JPopupMenu roundingPopup;

	private MyToggleButton btnCumulative, btnLineGraph, btnGrid, btnStepGraph,
			btnBarGraph, btnExport, btnNormalOverlay;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator
	 */
	public ProbabiltyCalculatorStyleBar(AppD app, ProbabilityCalculator probCalc) {

		this.probCalc = probCalc;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();

	}

	private void createGUI() {
		this.removeAll();
		buildOptionsButton();

		btnCumulative = new MyToggleButton(
				app.getImageIcon("cumulative_distribution.png"), iconHeight);
		btnCumulative.setSelected(probCalc.isCumulative());
		btnCumulative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				probCalc.setCumulative(!probCalc.isCumulative());
			}
		});

		btnLineGraph = new MyToggleButton(app.getImageIcon("line_graph.png"),
				iconHeight);
		btnLineGraph.addActionListener(this);

		btnStepGraph = new MyToggleButton(app.getImageIcon("step_graph.png"),
				iconHeight);
		btnStepGraph.addActionListener(this);

		btnBarGraph = new MyToggleButton(app.getImageIcon("bar_graph.png"),
				iconHeight);
		btnBarGraph.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnBarGraph);
		gp.add(btnLineGraph);
		gp.add(btnStepGraph);

		btnGrid = new MyToggleButton(app.getImageIcon("grid.gif"), iconHeight);
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
		btnExport = new MyToggleButton(app.getImageIcon("export16.png"),
				iconHeight);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);
		
		// create normal overlay button
		btnNormalOverlay = new MyToggleButton(app.getImageIcon("normal-overlay.png"),
				iconHeight);
		btnNormalOverlay.setFocusable(false);
		btnNormalOverlay.addActionListener(this);

		// add(btnRounding);
		// addSeparator();
		// add(btnCumulative);
		// addSeparator();
		add(btnLineGraph);
		add(btnStepGraph);
		add(btnBarGraph)
		;
		addSeparator();
		add(btnNormalOverlay);
		
		addSeparator();
		add(btnExport);
		// add(btnGrid); (grid doesn't work well with discrete graphs and point
		// capturing)

	}

	/**
	 * Updates the GUI
	 */
	public void updateGUI() {

		btnLineGraph.setVisible(probCalc.getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnStepGraph.setVisible(probCalc.getProbManager().isDiscrete(
				probCalc.getSelectedDist()));
		btnBarGraph.setVisible(probCalc.getProbManager().isDiscrete(
				probCalc.getSelectedDist()));

		btnLineGraph.removeActionListener(this);
		btnStepGraph.removeActionListener(this);
		btnBarGraph.removeActionListener(this);
		btnNormalOverlay.removeActionListener(this);
		
		btnLineGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculator.GRAPH_LINE);
		btnStepGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculator.GRAPH_STEP);
		btnBarGraph
				.setSelected(probCalc.getGraphType() == ProbabilityCalculator.GRAPH_BAR);
		
		btnNormalOverlay.setSelected(probCalc.isShowNormalOverlay());

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
		
		btnLineGraph.setToolTipText(app.getMenu("LineGraph"));
		btnStepGraph.setToolTipText(app.getMenu("StepGraph"));
		btnBarGraph.setToolTipText(app.getMenu("BarChart"));
		
		btnExport.setToolTipText(app.getMenu("Export"));
		btnNormalOverlay.setToolTipText(app.getMenu("NormalOverlay"));
		//btnCumulative.setToolTipText(app.getMenu("Cumulative"));
		
	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildOptionsButton() {

		btnRounding = new JButton(app.getImageIcon("triangle-down.png"));
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
		roundingPopup = createRoundingPopup();

		btnRounding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// popup appears below the button
				roundingPopup.show(getParent(), btnRounding.getLocation().x,
						btnRounding.getLocation().y + btnRounding.getHeight());
			}
		});

		updateMenuDecimalPlaces(roundingPopup);

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
		String[] strDecimalSpaces = app.getRoundingMenu();

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
				mi.setFont(app.getFontCanDisplayAwt(text));
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
				probCalc.updatePrintFormat(decimals, -1);

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
				probCalc.updatePrintFormat(-1, figures);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

		else if (e.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculator.GRAPH_LINE);
		}

		else if (e.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculator.GRAPH_BAR);
		}

		else if (e.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected())
				probCalc.setGraphType(ProbabilityCalculator.GRAPH_STEP);
		}

		else if (e.getSource() == btnNormalOverlay) {
				probCalc.setShowNormalOverlay(btnNormalOverlay.isSelected());
				probCalc.updateAll();
		}
		
		else if (e.getSource() == btnExport) {
			JPopupMenu menu = probCalc.getPlotPanel().getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

	}

}
