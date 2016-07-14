package org.geogebra.desktop.gui.layout;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.desktop.gui.dialog.LanguageDialog;
import org.geogebra.desktop.gui.util.GeoGebraIcon;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * JPopupMenu to offer Perspective choices
 * 
 * @author G.Sturr
 * 
 */
public class PerspectivePanel extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private AppD app;

	/* Layout manager */
	protected LayoutD layout;

	private DockBar dockBar;
	private JButton btnLanguage;

	private AbstractAction setLanguageAction, changePerspectiveAction,
			managePerspectivesAction, savePerspectiveAction;

	/****************************************************
	 * Constructs a PerspectivePanel
	 * 
	 * @param app
	 * @param dockBar
	 */
	public PerspectivePanel(AppD app, DockBar dockBar) {

		this.app = app;
		this.layout = (LayoutD) app.getGuiManager().getLayout();
		this.dockBar = dockBar;

		initActions();
		initItems();
		Border b = this.getBorder();
		Border empty = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		this.setBorder(BorderFactory.createCompoundBorder(b, empty));

	}

	@Override
	public void setVisible(boolean b) {
		// prevent call from javax.swing.JPopupMenu.menuSelectionChanged()
		if (!dockBar.sideBarHasMouse())
			superSetVisible(b);
	}

	/**
	 * call super.setVisible()
	 * 
	 * @param b
	 *            flag
	 */
	public void superSetVisible(boolean b) {
		super.setVisible(b);
		dockBar.setSidebarTriangle(b);
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems() {

		this.removeAll();

		JMenuItem title = new JMenuItem("<html><font color = black>"
				+ app.getMenu("Perspectives") + "</font></html>");
		title.setIcon(GeoGebraIcon.createEmptyIcon(32, 32));
		title.setFont(app.getBoldFont());
		title.setEnabled(false);

		add(Box.createVerticalStrut(5));
		add(title);
		add(Box.createVerticalStrut(5));

		addPerspective(0, GuiResourcesD.MENU_VIEW_ALGEBRA);
		addPerspective(3, GuiResourcesD.MENU_VIEW_CAS);
		addPerspective(1, GuiResourcesD.PERSPECTIVES_GEOMETRY);
		addPerspective(4, GuiResourcesD.PERSPECTIVES_GEOMETRY3D);
		addPerspective(2, GuiResourcesD.MENU_VIEW_SPREADSHEET);
		addPerspective(5, GuiResourcesD.MENU_VIEW_PROBABILITY);


		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		// don't allow user perspectives in 4.2 (maybe in 5.0)
		boolean showUserPerpectives = false;

		if (showUserPerpectives && perspectives.length != 0) {
			addSeparator();
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				tmpItem.setIcon(app.getEmptyIcon());

				Dimension d = tmpItem.getMaximumSize();
				d.height = tmpItem.getPreferredSize().height;
				tmpItem.setMaximumSize(d);

				tmpItem.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
				add(tmpItem);
			}
		}

		btnLanguage = new JButton(setLanguageAction);
		btnLanguage.setMargin(new Insets(2, 2, 2, 2));
		btnLanguage.setToolTipText(app.getMenu("Language"));

		add(Box.createVerticalStrut(20));
		// add(OptionsUtil.flowPanelRight(0, 0, 0, btnLanguage,
		// Box.createHorizontalStrut(20)));

	}

	private void addPerspective(int i, ImageResourceD icon) {
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
		if (defaultPerspectives[i] == null) {
			return;
		}
		JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
		tmpItem.setText(app.getMenu(defaultPerspectives[i].getId()));
		tmpItem.setActionCommand("d" + i);

		Icon ic;
		if (icon != null) {
			ic = app.getScaledIcon(icon);
			// GeoGebraIcon.ensureIconSize((ImageIcon) ic, new
			// Dimension(40,40));
		} else {
			ic = app.getEmptyIcon();
		}
		tmpItem.setIcon(ic);

		Dimension d = tmpItem.getMaximumSize();
		d.height = tmpItem.getPreferredSize().height;
		tmpItem.setMaximumSize(d);

		add(tmpItem);

	}

	/**
	 * Initialize the actions.
	 */
	private void initActions() {

		final String flagName = app.getFlagName();

		setLanguageAction = new AbstractAction(null,
				app.getScaledFlagIcon(flagName)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				LanguageDialog d = new LanguageDialog(app);
				d.setVisible(true);

			}
		};

		savePerspectiveAction = new AbstractAction(
				app.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		managePerspectivesAction = new AbstractAction(
				app.getMenu("ManagePerspectives"), app.getEmptyIcon()) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		changePerspectiveAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				boolean changed;
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand()
							.substring(1));
					changed = layout.applyPerspective(
							Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					changed = layout.applyPerspective(layout
							.getPerspective(index));
				}
				if (changed) {
					app.storeUndoInfo();
				}

			}
		};

	}

}
