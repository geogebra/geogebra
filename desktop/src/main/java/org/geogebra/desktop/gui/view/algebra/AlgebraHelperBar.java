package org.geogebra.desktop.gui.view.algebra;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.desktop.gui.util.PopupMenuButton;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Helper tool bar for the algebra view which displays some useful buttons to
 * change the functionality (e.g. show auxiliary objects).
 */
public class AlgebraHelperBar extends JToolBar implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The algebra view which uses this tool bar.
	 */
	protected AlgebraViewD algebraView;

	/**
	 * Instance of the application.
	 */
	protected AppD app;

	/**
	 * Button to show/hide auxiliary objects in the algebra view.
	 */
	private JButton toggleAuxiliary;

	/**
	 * Button to toggle between the two tree modes of the algebra view: -
	 * Categorize objects by free / independent / auxiliary - Categorize objects
	 * by their type
	 */
	protected PopupMenuButton toggleTypeTreeMode;
	protected PopupMenuButton toggleDescriptionMode;


	private JPopupMenu treeModeMenu, descriptionMenu;

	private LocalizationD loc;

	/**
	 * Button to toggle LaTeX rendering
	 */
	// private JButton toggleLaTeX;

	/**
	 * Helper bar.
	 * 
	 * @param algebraView
	 * @param app
	 */
	public AlgebraHelperBar(AlgebraViewD algebraView, AppD app) {
		this.algebraView = algebraView;
		this.app = app;
		this.loc = app.getLocalization();
		setFloatable(false);

		addButtons();

		updateStates();
		updateLabels();
	}

	/**
	 * add the buttons
	 */
	protected void addButtons() {

		toggleAuxiliary = new JButton(
				app.getScaledIcon("stylingbar_algebraview_auxiliary_objects.png"));
		toggleAuxiliary.setFocusPainted(false);
		toggleAuxiliary.addActionListener(this);
		add(toggleAuxiliary);

		addSeparator();

		toggleTypeTreeMode = new PopupMenuButton(app);
		buildTreeModeMenu();
		toggleTypeTreeMode.setPopupMenu(treeModeMenu);
		toggleTypeTreeMode.setKeepVisible(true);
		toggleTypeTreeMode.setStandardButton(true); // mouse clicks over total
													// button region
		toggleTypeTreeMode.setIcon(app
				.getScaledIcon("stylingbar_algebraview_sort_objects_by.png"));
		add(toggleTypeTreeMode);

		toggleDescriptionMode = new PopupMenuButton(app);
		buildDescriptionMenu();
		toggleDescriptionMode.setPopupMenu(descriptionMenu);
		toggleDescriptionMode.setKeepVisible(true);
		toggleDescriptionMode.setStandardButton(true); // mouse clicks over
														// total
														// button region
		toggleDescriptionMode.setIcon(app
				.getScaledIcon("stylingbar_algebraview_sort_objects_by.png"));
		add(toggleDescriptionMode);

	}

	public void update() {
		// toggleAuxiliary.setIcon(app.getScaledIcon("auxiliary.png"));
		// toggleTypeTreeMode.setIcon(app.getScaledIcon("tree.png"));
		removeAll();
		addButtons();
		updateLabels();
	}

	public void updateStates() {

		toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
		//toggleTypeTreeMode.setSelected(algebraView.getTreeMode().equals(SortMode.TYPE));
		// toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
	}

	/**
	 * Update the tool tip texts (used for language change).
	 */
	public void updateLabels() {
		toggleAuxiliary.setToolTipText(loc.getPlainTooltip("AuxiliaryObjects"));

		toggleTypeTreeMode.setToolTipText(loc.getPlainTooltip("SortObjectsBy"));
		buildTreeModeMenu();
		buildDescriptionMenu();

	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggleAuxiliary) {
			app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
			toggleAuxiliary.setSelected(app.showAuxiliaryObjects());

		}
		// else if(e.getSource() == toggleLaTeX) {
		// algebraView.setRenderLaTeX(!algebraView.isRenderLaTeX());
		// toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
		// }
	}

	void buildTreeModeMenu() {

		if (treeModeMenu == null) {
			treeModeMenu = new JPopupMenu();
		}
		treeModeMenu.removeAll();

		JLabel title = new JLabel(app.getPlain("SortBy") + ":");
		title.setFont(app.getBoldFont());
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		add(title);

		treeModeMenu.add(title);

		SortMode[] sortModes = new SortMode[] { SortMode.DEPENDENCY,
				SortMode.TYPE, SortMode.LAYER, SortMode.ORDER };

		for (int i = 0; i < sortModes.length; i++) {
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
			mi.setFont(app.getPlainFont());
			mi.setBackground(Color.white);
			final SortMode sort = sortModes[i];
			mi.setText(app.getPlain(sort.toString()));

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					algebraView.setTreeMode(sort);
					buildTreeModeMenu();
				}
			});
			mi.setSelected(algebraView.getTreeMode() == sortModes[i]);
			treeModeMenu.add(mi);
		}

		

		app.setComponentOrientation(treeModeMenu);


	}

	void buildDescriptionMenu() {

		if (descriptionMenu == null) {
			descriptionMenu = new JPopupMenu();
		}
		descriptionMenu.removeAll();

		JLabel title = new JLabel(app.getMenu("AlgebraDescriptions") + ":");
		title.setFont(app.getBoldFont());
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		add(title);

		descriptionMenu.add(title);

		String[] modes = new String[] { app.getPlain("Value"),
				app.getPlain("Definition"), app.getPlain("Command") };
		for (int i = 0; i < modes.length; i++) {
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
			mi.setFont(app.getPlainFont());
			mi.setBackground(Color.white);
			mi.setText(modes[i]);
			final int current = i;
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.getKernel().setAlgebraStyle(current);
					app.getKernel().updateConstruction();
					buildDescriptionMenu();
				}
			});
			mi.setSelected(algebraView.getTreeMode() == SortMode.DEPENDENCY);
			descriptionMenu.add(mi);
		}



		app.setComponentOrientation(treeModeMenu);

	}
}
