package geogebra.gui.layout;

import geogebra.common.io.layout.Perspective;
import geogebra.gui.menubar.LanguageActionListener;
import geogebra.gui.menubar.OptionsMenuD;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

public class PerspectivePanel extends JPopupMenu {

	private AppD app;
	private LayoutD layout;
	private DockBar dockBar;

	private JPanel btnPanel;

	private AbstractAction changePerspectiveAction, managePerspectivesAction,
			savePerspectiveAction;

	public PerspectivePanel(AppD app, DockBar dockBar) {

		this.app = app;
		this.layout = (LayoutD) app.getGuiManager().getLayout();
		this.dockBar = dockBar;
		setupFlagLabel();
		initActions();
		initItems();
		Border b = this.getBorder();
		Border empty = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		this.setBorder(BorderFactory.createCompoundBorder(b, empty));
		// registerListeners();
	}

	private void registerListeners() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				hidePopup();
			}
		});
	}

	protected void hidePopup() {
		superSetVisible(false);
	}

	boolean flag = true;
	private AbstractAction setLanguageAction;

	public void setVisible(boolean b) {

		// prevent call from javax.swing.JPopupMenu.menuSelectionChanged()
		if (!dockBar.sideBarHasMouse())
			superSetVisible(b);
		// super.setVisible(b || flag);
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

		//add(Box.createVerticalStrut(10));
		add(title);
		//add(Box.createVerticalStrut(10));
		// addSeparator();
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			tmpItem.setActionCommand("d" + i);

			Icon ic;
			if (defaultPerspectives[i].getIconString() != null) {
				ic = app.getImageIcon(defaultPerspectives[i].getIconString());
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

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
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

		// JMenu subMenu = new JMenu(app.getMenuTooltip("Language"));
		// subMenu.setIcon(app.getFlagIcon(flagName));
		// subMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		// OptionsMenuD.addLanguageMenuItems(app, subMenu,
		// new LanguageActionListener(app));

		// add(subMenu);

		// add(Box.createVerticalGlue());

	}

	String flagName;

	JLabel languageLabel;

	private void setupFlagLabel() {

		flagName = app.getFlagName(false);

		languageLabel = new JLabel(app.getFlagIcon(flagName));
		languageLabel
				.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		AbstractAction setLanguageAction;
		languageLabel.setToolTipText(app.getMenuTooltip("Language"));
		languageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu myPopup = new JPopupMenu();
				OptionsMenuD.addLanguageMenuItems(app, myPopup,
						new LanguageActionListener(app));
				myPopup.setVisible(true);
				myPopup.show(languageLabel, 0, languageLabel.getHeight());
			}
		});
	}

	/**
	 * Initialize the actions.
	 */
	private void initActions() {

		final String flagName = app.getFlagName(false);

		setLanguageAction = new AbstractAction(app.getMenuTooltip("Language"),
				app.getFlagIcon(flagName)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
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
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand()
							.substring(1));
					layout.applyPerspective(geogebra.common.gui.Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}

			}
		};
	}

}
