package geogebra.gui.menubar;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * The "Windows" menu.
 */
class WindowMenu extends BaseMenu {
	private static final long serialVersionUID = -5087344097832121548L;
	
	private AbstractAction
		newWindowAction
	;

	public WindowMenu(AppD app) {
		super(app, app.getMenu("Window"));

		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
	}
	
	/**
	 * Initialize and update the items.
	 */
	private void updateItems()
	{
		if (!initialized) {
			return;
		}
		
		removeAll();
		JMenuItem mit = add(newWindowAction);
		setMenuShortCutAccelerator(mit, 'N');

		ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();
		int size = ggbInstances.size();
		if (size == 1)
			return;

		addSeparator();
		StringBuilder sb = new StringBuilder();
		ButtonGroup bg = new ButtonGroup();
		JRadioButtonMenuItem mi;
		
		int current = -1;
		
		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = ggbInstances.get(i);
			AppD application = ggb.getApplication();
			if (app == application) current = i;
		}
		
		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = ggbInstances.get(i);
			AppD application = ggb.getApplication();

			sb.setLength(0);
			sb.append(i + 1);
			if (application != null) // Michael Borcherds 2008-03-03 bugfix
				if (application.getCurrentFile() != null) {
					sb.append(" ");
					sb.append(application.getCurrentFile().getName());
				}

			mi = new JRadioButtonMenuItem(sb.toString());
			if (application == this.app)
				mi.setSelected(true);
			ActionListener al = new RequestFocusListener(ggb);
			mi.addActionListener(al);
			if (i == ((current+1)%size) ) setMenuShortCutShiftAccelerator(mi, 'N');
			else if (i == ((current-1+size)%size) ) setMenuShortCutShiftAltAccelerator(mi, 'N');
			bg.add(mi);
			add(mi);
		}
		
		// support for right-to-left languages
		app.setComponentOrientation(this);

	}
	
	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions()
	{
		newWindowAction = new AbstractAction(app.getMenu("NewWindow"), app
				.getImageIcon("document-new.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						GeoGebraFrame.createNewWindow(null);
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};
	}

	@Override
	public void update() {
		updateItems();
	}


	@Override
	protected void initItems() {
		//
	}
}
