package org.geogebra.desktop.gui.view.algebra;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

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
	private final LocalizationD loc;
	private final List<AlgebraStyle> algebraStyles;

	/**
	 * Button to show/hide auxiliary objects in the algebra view.
	 */
	private JButton toggleAuxiliary;

	/**
	 * Button to toggle between the two tree modes of the algebra view: -
	 * Categorize objects by free / independent / auxiliary - Categorize objects
	 * by their type
	 */
	protected PopupMenuButtonD toggleTypeTreeMode;
	protected PopupMenuButtonD toggleDescriptionMode;

	private JPopupMenu treeModeMenu;
	private JPopupMenu descriptionMenu;

	/**
	 * Button to toggle LaTeX rendering
	 */
	// private JButton toggleLaTeX;

	/**
	 * Helper bar.
	 * 
	 * @param algebraView algebra view
	 * @param app application
	 */
	public AlgebraHelperBar(AlgebraViewD algebraView, AppD app) {
		this.algebraView = algebraView;
		this.app = app;
		this.loc = app.getLocalization();
		this.algebraStyles = AlgebraStyle.getAvailableValues(app);
		setFloatable(false);

		addButtons();

		updateStates();
		updateLabels();
	}

	/**
	 * add the buttons
	 */
	protected void addButtons() {

		toggleAuxiliary = new JButton(app.getScaledIcon(
				GuiResourcesD.STYLINGBAR_ALGEBRAVIEW_AUXILIARY_OBJECTS));
		toggleAuxiliary.setFocusPainted(false);
		toggleAuxiliary.addActionListener(this);
		add(toggleAuxiliary);

		addSeparator();

		toggleTypeTreeMode = new PopupMenuButtonD(app);
		buildTreeModeMenu();
		toggleTypeTreeMode.setPopupMenu(treeModeMenu);
		toggleTypeTreeMode.setKeepVisible(true);
		toggleTypeTreeMode.setStandardButton(true); // mouse clicks over total
													// button region
		toggleTypeTreeMode.setIcon(app.getScaledIcon(
				GuiResourcesD.STYLINGBAR_ALGEBRAVIEW_SORT_OBJECTS_BY));
		add(toggleTypeTreeMode);

		toggleDescriptionMode = new PopupMenuButtonD(app);
		buildDescriptionMenu();
		toggleDescriptionMode.setPopupMenu(descriptionMenu);
		toggleDescriptionMode.setKeepVisible(true);
		toggleDescriptionMode.setStandardButton(true); // mouse clicks over
														// total
														// button region
		toggleDescriptionMode
				.setIcon(app.getScaledIcon(GuiResourcesD.FORMULA_BAR));
		add(toggleDescriptionMode);

	}

	/**
	 * Update the UI
	 */
	public void update() {
		removeAll();
		addButtons();
		updateLabels();
	}

	private void updateStates() {
		toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
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
	@Override
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

		JLabel title = new JLabel(loc.getMenu("SortBy") + ":");
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
			mi.setText(loc.getMenu(sort.toString()));

			mi.addActionListener(e -> {
				app.getSettings().getAlgebra().setTreeMode(sort);
				buildTreeModeMenu();
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

		JLabel title = new JLabel(loc.getMenu("AlgebraDescriptions") + ":");
		title.setFont(app.getBoldFont());
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		add(title);
		descriptionMenu.add(title);

		algebraStyles.forEach(style -> {
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
			mi.setFont(app.getPlainFont());
			mi.setBackground(Color.white);
			mi.setText(style.getTranslationKey());
			mi.addActionListener(e -> {
				app.getSettings().getAlgebra().setStyle(style);
				app.getKernel().updateConstruction(false);
				buildDescriptionMenu();
			});
			mi.setSelected(algebraView.getTreeMode() == SortMode.DEPENDENCY);
			descriptionMenu.add(mi);
		});

		app.setComponentOrientation(treeModeMenu);
	}
}
