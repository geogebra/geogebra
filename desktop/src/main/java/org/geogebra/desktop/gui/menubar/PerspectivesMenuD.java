package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;

/**
 * The "Perspectives" menu.
 */
class PerspectivesMenuD extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	LayoutD layout;

	private AbstractAction changePerspectiveAction;

	/**
	 * Creates new perspectives menu
	 * 
	 * @param app
	 * @param layout
	 */
	public PerspectivesMenuD(AppD app, LayoutD layout) {
		super(app, "Perspectives");

		this.layout = layout;

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);
	}

	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems() {

		if (!initialized) {
			// menus not created yet, so nothing to do
			return;
		}

		for (int i = 0; i < Layout.getDefaultPerspectivesLength(); ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(loc.getMenu(
					"Perspective." + Layout.getDefaultPerspectives(i).getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d" + i);
			add(tmpItem);
		}

		addSeparator();

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				add(tmpItem);
			}
			addSeparator();
		}

	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {

		changePerspectiveAction = new AbstractAction() {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer
							.parseInt(e.getActionCommand().substring(1));
					layout.applyPerspective(
							Layout.getDefaultPerspectives(index));
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}
			}
		};
	}

	@Override
	public void update() {
		removeAll();
		initItems();
	}

}
