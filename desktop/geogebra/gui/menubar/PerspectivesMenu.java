package geogebra.gui.menubar;

import geogebra.common.io.layout.Perspective;
import geogebra.gui.layout.LayoutD;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

/**
 * The "Perspectives" menu.
 */
class PerspectivesMenu extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	LayoutD layout;

	private AbstractAction
	changePerspectiveAction,
	managePerspectivesAction,
	savePerspectiveAction
	;
	/**
	 * Creates new perspectives menu
	 * @param app
	 * @param layout 
	 */
	public PerspectivesMenu(AppD app, LayoutD layout) {
		super(app, app.getMenu("Perspectives"));
		
		this.layout = layout;

		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
	}
	
	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems()
	{
		
		if (!initialized) {
			// menus not created yet, so nothing to do
			return;
		}

		
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
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
		
		// diable menu item to manage perspectives if there is no user perspective
		managePerspectivesAction.setEnabled(perspectives.length != 0);

		add(managePerspectivesAction);
		add(savePerspectiveAction);
	}
	
	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions()
	{
		savePerspectiveAction = new AbstractAction(app
				.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		managePerspectivesAction = new AbstractAction(app
				.getMenu("ManagePerspectives"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		changePerspectiveAction = new AbstractAction() {
			@SuppressWarnings("hiding")
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

	@Override
	public void update() {
		removeAll();
		initItems();
	}

}
