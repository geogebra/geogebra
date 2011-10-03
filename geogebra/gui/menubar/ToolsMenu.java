package geogebra.gui.menubar;

import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.gui.toolbar.Toolbar;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * The "Tools" menu.
 */
class ToolsMenu extends BaseMenu {
	private static final long serialVersionUID = -2012951866084095682L;

	private AbstractAction
		toolbarConfigAction,
		showCreateToolsAction,
		showManageToolsAction,
		modeChangeAction
	;
	
	public ToolsMenu(Application app) {
		super(app, app.getMenu("Tools"));
		
		initActions();
		update();
	}
	
	/**
	 * Initialize the menu items.
	 */
	@SuppressWarnings("unchecked")
	private void updateItems()
	{
		removeAll();

		add(toolbarConfigAction);
		addSeparator();
		add(showCreateToolsAction);
		add(showManageToolsAction);
		addSeparator();

		JMenu[] modeMenus = new JMenu[13];
		modeMenus[0] = new JMenu(app.getMenu("MovementTools"));
		modeMenus[1] = new JMenu(app.getMenu("PointTools"));
		modeMenus[2] = new JMenu(app.getMenu("BasicLineTools"));
		modeMenus[3] = new JMenu(app.getMenu("SpecialLineTools"));
		modeMenus[4] = new JMenu(app.getMenu("PolygonTools"));
		modeMenus[5] = new JMenu(app.getMenu("CircleArcTools"));
		modeMenus[6] = new JMenu(app.getMenu("ConicSectionTools"));
		modeMenus[7] = new JMenu(app.getMenu("MeasurementTools"));
		modeMenus[8] = new JMenu(app.getMenu("TransformationTools"));
		modeMenus[9] = new JMenu(app.getMenu("SpecialObjectTools"));
		modeMenus[10] = new JMenu(app.getMenu("ActionObjectTools"));
		modeMenus[11] = new JMenu(app.getMenu("GeneralTools"));
		modeMenus[12] = new JMenu(app.getMenu("CustomTools"));

		for (int i = 0; i < modeMenus.length; ++i) {
			modeMenus[i].setIcon(app.getEmptyIcon());
			add(modeMenus[i]);
		}

		Toolbar toolbar = new Toolbar(app);
		Vector<Object> modes = Toolbar.parseToolbarString(toolbar
				.getDefaultToolbarString());

		int menuIndex = 0;

		for (Iterator iter = modes.iterator(); iter.hasNext();) {
			Object next = iter.next();
			if (next instanceof Vector) {
				for (Iterator iter2 = ((Vector) next).iterator(); iter2
						.hasNext();) {
					Object next2 = iter2.next();

					if (next2 instanceof Integer) {
						int mode = ((Integer) next2).intValue();

						if (mode < 0)
							modeMenus[menuIndex].addSeparator();
						else {
							JMenuItem item = new JMenuItem(app
									.getToolName(mode));// ,
														// app.getModeIcon(mode));
							item.setActionCommand(Integer.toString(mode));
							item.addActionListener(modeChangeAction);
							
							app.setTooltipFlag();
							item.setToolTipText(app.getToolHelp(mode));
							app.clearTooltipFlag();
							
							modeMenus[menuIndex].add(item);
						}
					} else {
						Application
								.debug("Nested default toolbar not supported");
					}
				}

				++menuIndex;
			}
		}

		if (modeMenus[modeMenus.length - 1].getItemCount() == 0)
			modeMenus[modeMenus.length - 1].setEnabled(false);
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		toolbarConfigAction = new AbstractAction(app
				.getMenu("Toolbar.Customize")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showToolbarConfigDialog();
			}
		};

		// Florian Sonner 2008-08-13
		modeChangeAction = new AbstractAction() {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setMode(Integer.parseInt(e.getActionCommand()));
			}
		};

		showCreateToolsAction = new AbstractAction(app
				.getMenu("Tool.CreateNew")
				+ " ...", app.getImageIcon("tool.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolCreationDialog tcd = new ToolCreationDialog(app);
				tcd.setVisible(true);
			}
		};

		showManageToolsAction = new AbstractAction(app.getMenu("Tool.Manage")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolManagerDialog tmd = new ToolManagerDialog(app);
				tmd.setVisible(true);
			}
		};
	}

	@Override
	public void update() {		
		updateItems();
		
		// TODO update labels
	}
}
