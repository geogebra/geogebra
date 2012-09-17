package geogebra.gui.menubar;

import geogebra.common.main.OptionType;
import geogebra.common.main.settings.KeyboardSettings;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.AppD;
import geogebra.plugin.kinect.KinectTest;
import geogebra.plugin.kinect.KinectTestApplication;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * The "View" menu.
 */
public class ViewMenu extends BaseMenu {
	private static final long serialVersionUID = 1L;
	private final LayoutD layout;

	private AbstractAction showKeyboardAction, showAlgebraInputAction,
			showKinectAction, refreshAction, recomputeAllViews;

	private JCheckBoxMenuItem cbShowKeyboard, cbShowKinect, cbShowInputBar;

	private ShowViewAction[] showViews;
	private JCheckBoxMenuItem[] cbViews;

	private AbstractAction showLayoutOptionsAction;

	/**
	 * @param app
	 *            app
	 * @param layout
	 *            layout
	 */
	public ViewMenu(AppD app, LayoutD layout) {
		super(app, app.getMenu("View"));

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
			return;
		}
		initViewItems(this);
		
		addSeparator();

		JMenuItem mi;

		// show/hide keyboard
		if (!app.isApplet()) {
			cbShowKeyboard = new JCheckBoxMenuItem(showKeyboardAction);
			cbShowKeyboard.setIcon(app.getImageIcon("keyboard.png"));
			KeyboardSettings kbs = app.getSettings().getKeyboard();
			if (kbs.isShowKeyboardOnStart()) {
				cbShowKeyboard.setSelected(true);
				VirtualKeyboard vk = ((GuiManagerD)app.getGuiManager()).getVirtualKeyboard();
				vk.setVisible(true);
			}
			add(cbShowKeyboard);
		}
		
		cbShowInputBar = new JCheckBoxMenuItem(showAlgebraInputAction);
		add(cbShowInputBar);
		
		// show Kinect option in Eclipse & 5.0 Webstart only
		// ie not 4.2
		if (!AppD.isWebstart() || app.is3D()) {
			// TEST: show/hide Kinect window
			cbShowKinect = new JCheckBoxMenuItem(showKinectAction);
			app.setEmptyIcon(cbShowKinect);
			add(cbShowKinect);
		}

		// cbShowHandwriting = new JCheckBoxMenuItem(showHandwritingAction);
		// app.setEmptyIcon(cbShowHandwriting);
		// add(cbShowHandwriting);
		//
		// menuHandwriting = new JMenu(app.getMenu("Handwriting"));
		// menuHandwriting.setIcon(app.getEmptyIcon());
		// cbShowHandwritingAutoAdd = new
		// JCheckBoxMenuItem(showHandwritingAutoAddAction);
		// app.setEmptyIcon(cbShowHandwritingAutoAdd);
		// menuHandwriting.add(cbShowHandwritingAutoAdd);
		// cbShowHandwritingTimedAdd = new
		// JCheckBoxMenuItem(showHandwritingTimedAddAction);
		// app.setEmptyIcon(cbShowHandwritingTimedAdd);
		// menuHandwriting.add(cbShowHandwritingTimedAdd);
		// cbShowHandwritingTimedRecognise = new
		// JCheckBoxMenuItem(showHandwritingTimedRecogniseAction);
		// app.setEmptyIcon(cbShowHandwritingTimedRecognise);
		// menuHandwriting.add(cbShowHandwritingTimedRecognise);

		// add(menuHandwriting);

		addSeparator();

		mi = add(showLayoutOptionsAction);

		addSeparator();

		mi = add(refreshAction);
		setMenuShortCutAccelerator(mi, 'F');

		mi = add(recomputeAllViews);
		// F9 and Ctrl-R both work, but F9 doesn't on MacOS, so we must display
		// Ctrl-R
		setMenuShortCutAccelerator(mi, 'R');
		
		// support for right-to-left languages
		app.setComponentOrientation(this);


	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		initViewActions();

		// display the layout options dialog
		showLayoutOptionsAction = new AbstractAction(app.getMenu("Layout")
				+ " ...", app.getImageIcon("view-properties16.png")) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.LAYOUT,
						null);
			}
		};

		showKeyboardAction = new AbstractAction(app.getPlain("Keyboard")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				if (AppD.isVirtualKeyboardActive()
						&& !((GuiManagerD)app.getGuiManager()).showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD)app.getGuiManager()).toggleKeyboard(true);
					update();

				} else {

					AppD.setVirtualKeyboardActive(!AppD
							.isVirtualKeyboardActive());
					((GuiManagerD)app.getGuiManager()).toggleKeyboard(
							AppD.isVirtualKeyboardActive());
					update();
				}

			}
		};
		
		showAlgebraInputAction = new AbstractAction(app.getMenu("InputField")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraInput(!app.showAlgebraInput(), true);
				app.updateContentPane();
			}
		};


		showKinectAction = new AbstractAction(app.getMenu("KinectWindow")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				JFrame f = new JFrame(app.getMenu("KinectWindow"));
				final KinectTestApplication app2 = new KinectTestApplication(f);

				app2.viewer = new KinectTest(app.getKernel());
				f.add("Center", app2.viewer);
				f.pack();
				f.setVisible(true);
				Thread runner = new Thread() {

					@Override
					public void run() {
						try {
							Thread.sleep(7000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						app2.run();
					}
				};
				runner.start();

			}
		};

		/*
		 * showHandwritingAction = new
		 * AbstractAction(app.getPlain("ShowHandwriting")) { private static
		 * final long serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) {
		 * 
		 * if (Application.isHandwritingRecognitionActive() &&
		 * !((GuiManagerD)app.getGuiManager()).showHandwritingRecognition()) {
		 * 
		 * // if handwriting is active but hidden, just show it
		 * ((GuiManagerD)app.getGuiManager()).toggleHandwriting(true); update();
		 * 
		 * } else {
		 * 
		 * Application.setHandwritingRecognitionActive(!Application.
		 * isHandwritingRecognitionActive());
		 * ((GuiManagerD)app.getGuiManager()).toggleHandwriting
		 * (Application.isHandwritingRecognitionActive()); update(); }
		 * 
		 * } };
		 * 
		 * showHandwritingAutoAddAction = new
		 * AbstractAction(app.getPlain("AutoAdd")) { private static final long
		 * serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) {
		 * Application.setHandwritingRecognitionAutoAdd
		 * (!Application.isHandwritingRecognitionAutoAdd()); if
		 * (Application.isHandwritingRecognitionAutoAdd() &&
		 * Application.isHandwritingRecognitionTimedAdd()) {
		 * Application.setHandwritingRecognitionTimedAdd
		 * (!Application.isHandwritingRecognitionTimedAdd());
		 * ((GuiManagerD)app.getGuiManager()).updateMenubar(); } if
		 * (((GuiManagerD)app.getGuiManager()).showHandwritingRecognition()) {
		 * ((GuiManagerD)app.getGuiManager()).getHandwriting().repaint(); } } };
		 * 
		 * showHandwritingTimedAddAction = new
		 * AbstractAction(app.getPlain("TimedAdd")) { private static final long
		 * serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) {
		 * Application.setHandwritingRecognitionTimedAdd
		 * (!Application.isHandwritingRecognitionTimedAdd()); if
		 * (Application.isHandwritingRecognitionTimedAdd() &&
		 * Application.isHandwritingRecognitionAutoAdd()) {
		 * Application.setHandwritingRecognitionAutoAdd
		 * (!Application.isHandwritingRecognitionAutoAdd());
		 * ((GuiManagerD)app.getGuiManager()).updateMenubar(); } if
		 * (((GuiManagerD)app.getGuiManager()).showHandwritingRecognition()) {
		 * ((GuiManagerD)app.getGuiManager()).getHandwriting().repaint(); } } };
		 * 
		 * showHandwritingTimedRecogniseAction = new
		 * AbstractAction(app.getPlain("TimedRecognise")) { private static final
		 * long serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) {
		 * Application.setHandwritingRecognitionTimedRecognise
		 * (!Application.isHandwritingRecognitionTimedRecognise()); if
		 * (((GuiManagerD)app.getGuiManager()).showHandwritingRecognition()) {
		 * ((GuiManagerD)app.getGuiManager()).getHandwriting().repaint(); } } };
		 */

		refreshAction = new AbstractAction(app.getMenu("Refresh"),
				new ImageIcon(app.getRefreshViewImage())) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.refreshViews();
			}
		};

		recomputeAllViews = new AbstractAction(
				app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getKernel().updateConstruction();
			}
		};

		recomputeAllViews = new AbstractAction(
				app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getKernel().updateConstruction();
			}
		};
	}

	@Override
	public void update() {
		if (!initialized) {
			return;
		}

		updateViews();

		cbShowInputBar.setSelected(app.showAlgebraInput());

		if (cbShowKeyboard != null) {
			cbShowKeyboard.setSelected(AppD.isVirtualKeyboardActive());
		}

		// cbShowHandwriting.setSelected(Application.isHandwritingRecognitionActive());
		// cbShowHandwritingAutoAdd.setSelected(Application.isHandwritingRecognitionAutoAdd());
		// cbShowHandwritingTimedAdd.setSelected(Application.isHandwritingRecognitionTimedAdd());
		// cbShowHandwritingTimedRecognise.setSelected(Application.isHandwritingRecognitionTimedRecognise());

		// enable menus if necessary
		// menuInput.setEnabled(app.showAlgebraInput());
		// menuToolBar.setEnabled(app.showToolBar());

	}

	private void initViewActions() {
		if (!initialized) {
			return;
		}
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for (DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if (panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with menu items
		showViews = new ShowViewAction[viewsInMenu];
		{
			int i = 0;
			ShowViewAction action;

			for (DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}


				action = new ShowViewAction(panel);

				showViews[i] = action;
				++i;
			}
		}
	}

	private void initViewItems(JMenu menu) {
		if (!initialized) {
			return;
		}
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for (DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if (panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with menu items
		cbViews = new JCheckBoxMenuItem[viewsInMenu];
		{
			int i = 0;
			JCheckBoxMenuItem cb;

			for (DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				cb = new JCheckBoxMenuItem(showViews[i]);
				showViews[i].setCheckBox(cb);
				cb.setIcon(panel.getIcon());

				if (panel.hasMenuShortcut()) {
					setMenuShortCutShiftAccelerator(cb, panel.getMenuShortcut());
				}

				menu.add(cb);
				cbViews[i] = cb;
				++i;
			}
		}
	}

	private void updateViews() {

		if (!initialized) {
			return;
		}

		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());

		// update views
		{
			int i = 0;

			for (DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				cbViews[i].setSelected(((GuiManagerD)app.getGuiManager()).showView(
						panel.getViewId()));
				++i;
			}
		}
	}
	
	
	private class ShowViewAction extends AbstractAction{

		private DockPanel panel;
		private int viewId;
		private JCheckBoxMenuItem cb;
		
		public ShowViewAction(DockPanel panel){
			super(app.getPlain(panel.getViewTitle()));
			this.panel=panel;
			viewId = panel.getViewId();
		}
		
		public void setCheckBox(JCheckBoxMenuItem cb){
			this.cb=cb;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			((GuiManagerD)app.getGuiManager()).setShowView(
					!((GuiManagerD)app.getGuiManager()).showView(viewId), viewId);
			
			//ensure check box is correctly selected/unselected for case where hide aborted
			cb.setSelected(panel.isVisible());
			
		}
		
	}

}
