package org.geogebra.desktop.gui.view.probcalculator;

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

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorStyleBar;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * StyleBar for the ProbabilityCalculator view
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculatorStyleBarD extends ProbabilityCalculatorStyleBar
		implements ActionListener {

	/** icon height in pixels */
	private int iconHeight = 18;

	/** rounding button */
	JButton btnRounding;

	/** rounding popup menu */
	JPopupMenu roundingPopup;

	JToolBar wrappedToolbar;

	private MyToggleButtonD btnCumulative, btnLineGraph, btnGrid, btnStepGraph,
			btnBarGraph, btnExport, btnNormalOverlay;

	private final LocalizationD loc;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator
	 */
	public ProbabilityCalculatorStyleBarD(AppD app,
			ProbabilityCalculatorViewD probCalc) {
		super(app, probCalc);
		this.wrappedToolbar = new JToolBar();

		this.loc = app.getLocalization();
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
		iconHeight = ((AppD) getApp()).getScaledIconSize();
		btnCumulative.setIcon(((AppD) getApp())
				.getScaledIcon(GuiResourcesD.CUMULATIVE_DISTRIBUTION));
		btnLineGraph
				.setIcon(((AppD) getApp()).getScaledIcon(GuiResourcesD.LINE_GRAPH));
		btnStepGraph
				.setIcon(((AppD) getApp()).getScaledIcon(GuiResourcesD.STEP_GRAPH));
		btnBarGraph
				.setIcon(((AppD) getApp()).getScaledIcon(GuiResourcesD.BAR_GRAPH));
		btnGrid.setIcon(((AppD) getApp()).getScaledIcon(GuiResourcesD.GRID));
		btnExport.setIcon(((AppD) getApp()).getScaledIcon(GuiResourcesD.EXPORT16));
		btnNormalOverlay.setIcon(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.NORMAL_OVERLAY));

	}

	private void createGUI() {
		iconHeight = ((AppD) getApp()).getScaledIconSize();

		wrappedToolbar.removeAll();
		buildOptionsButton();

		btnCumulative = new MyToggleButtonD(((AppD) getApp()).getScaledIcon(
				GuiResourcesD.CUMULATIVE_DISTRIBUTION), iconHeight);
		btnCumulative.setSelected(getProbCalc().isCumulative());
		btnCumulative.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((ProbabilityCalculatorViewD) getProbCalc())
						.setCumulative(!getProbCalc().isCumulative());
			}
		});

		btnLineGraph = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.LINE_GRAPH),
				iconHeight);
		btnLineGraph.addActionListener(this);

		btnStepGraph = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.STEP_GRAPH),
				iconHeight);
		btnStepGraph.addActionListener(this);

		btnBarGraph = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.BAR_GRAPH),
				iconHeight);
		btnBarGraph.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnBarGraph);
		gp.add(btnLineGraph);
		gp.add(btnStepGraph);

		btnGrid = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.GRID), iconHeight);
		btnGrid.setSelected(getProbCalc().getPlotSettings().showGrid);
		btnGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlotSettings ps = getProbCalc().getPlotSettings();
				ps.showGrid = !ps.showGrid;
				getProbCalc().setPlotSettings(ps);
				getProbCalc().updatePlotSettings();
			}
		});

		// create export button
		btnExport = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.EXPORT16), iconHeight);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		// create normal overlay button
		btnNormalOverlay = new MyToggleButtonD(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.NORMAL_OVERLAY),
				iconHeight);
		btnNormalOverlay.setFocusable(false);
		btnNormalOverlay.addActionListener(this);

	}

	/**
	 * Updates the button layout to fit the selected ProbabilityCalculator tab
	 */
	public void updateLayout() {

		wrappedToolbar.removeAll();

		if (((ProbabilityCalculatorViewD) getProbCalc()).isDistributionTabOpen()) {
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
		} else {
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

		iconHeight = ((AppD) getApp()).getScaledIconSize();
		btnLineGraph.setVisible(((ProbabilityCalculatorViewD) getProbCalc())
				.getProbManager().isDiscrete(getProbCalc().getSelectedDist()));
		btnStepGraph.setVisible(((ProbabilityCalculatorViewD) getProbCalc())
				.getProbManager().isDiscrete(getProbCalc().getSelectedDist()));
		btnBarGraph.setVisible(((ProbabilityCalculatorViewD) getProbCalc())
				.getProbManager().isDiscrete(getProbCalc().getSelectedDist()));

		btnLineGraph.removeActionListener(this);
		btnStepGraph.removeActionListener(this);
		btnBarGraph.removeActionListener(this);
		btnNormalOverlay.removeActionListener(this);

		btnLineGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_LINE);
		btnStepGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_STEP);
		btnBarGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_BAR);

		btnNormalOverlay.setSelected(getProbCalc().isShowNormalOverlay());
		btnNormalOverlay.setVisible(getProbCalc().isOverlayDefined());
		btnLineGraph.addActionListener(this);
		btnStepGraph.addActionListener(this);
		btnBarGraph.addActionListener(this);
		btnNormalOverlay.addActionListener(this);
	}

	/**
	 * Updates localized labels
	 */
	public void setLabels() {
		btnRounding.setText(loc.getMenu("Rounding"));
		btnExport.setToolTipText(loc.getMenu("Export"));
		btnLineGraph.setToolTipText(loc.getMenu("LineGraph"));

		btnStepGraph.setToolTipText(loc.getMenu("StepGraph"));
		btnBarGraph.setToolTipText(loc.getMenu("BarChart"));
		btnNormalOverlay.setToolTipText(loc.getMenu("OverlayNormalCurve"));

		// btnCumulative.setToolTipText(loc.getMenu("Cumulative"));

	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildOptionsButton() {

		btnRounding = new JButton(
				((AppD) getApp()).getScaledIcon(GuiResourcesD.TRIANGLE_DOWN));
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
		roundingPopup = createRoundingPopup();

		btnRounding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// popup appears below the button
				roundingPopup.show(wrappedToolbar.getParent(),
						btnRounding.getLocation().x,
						btnRounding.getLocation().y + btnRounding.getHeight());
			}
		});

		updateMenuDecimalPlaces(roundingPopup);

		((AppD) getApp()).setComponentOrientation(roundingPopup);

	}

	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(JPopupMenu menu) {
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
			MenuElement[] m = menu.getSubElements();
			((JRadioButtonMenuItem) m[pos]).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private JPopupMenu createRoundingPopup() {
		JPopupMenu menu = new JPopupMenu();
		String[] strDecimalSpaces = getApp().getLocalization().getRoundingMenu();

		addRadioButtonMenuItems(menu, this, strDecimalSpaces,
				App.getStrDecimalSpacesAC(), 0);

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
			if ("---".equals(items[i])) {
				menu.addSeparator();
			} else {
				String text = loc.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(((AppD) getApp()).getFontCanDisplayAwt(text));
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);
				((ProbabilityCalculatorViewD) getProbCalc())
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
				((ProbabilityCalculatorViewD) getProbCalc()).updatePrintFormat(-1,
						figures);

			} catch (Exception ex) {
				getApp().showError(e.toString());
			}
		}

		else if (e.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_LINE);
			}
		}

		else if (e.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected()) {
				getProbCalc().setGraphType(ProbabilityCalculatorView.GRAPH_BAR);
			}
		}

		else if (e.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_STEP);
			}
		}

		else if (e.getSource() == btnNormalOverlay) {
			getProbCalc().setShowNormalOverlay(btnNormalOverlay.isSelected());
			getProbCalc().updateAll();
		}

		else if (e.getSource() == btnExport) {
			JPopupMenu menu = ((ProbabilityCalculatorViewD) getProbCalc())
					.getPlotPanel().getContextMenu();
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
