package geogebra.gui.layout;

import geogebra.common.gui.SetLabels;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.MySmallJButton;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.HelpAction;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements ActionListener, SetLabels {

	private static final long serialVersionUID = 1L;

	Application app;
	private Layout layout;

	private MyPopup popup;

	private JPanel fullPanel, minimumPanel;
	private ViewButtonBar viewButtonBar;

	private boolean enableHiding = true;

	/**
	 * flag to determine if dockbar is in a minimized state
	 */
	protected boolean isMinimized = true;

	private DockButton btnFileOpen;

	private DockButton btnFileSave;

	private DockButton btnPrint;

	/**
	 * Constructs a DockBar
	 * 
	 * @param app
	 */
	public DockBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		setBorder(BorderFactory.createEmptyBorder());
		initGUI();

	}

	private void initGUI() {

		setLayout(new BorderLayout());
		MouseAdapter ml = new MyMouseListener();

		viewButtonBar = new ViewButtonBar(app);
		viewButtonBar.setOrientation(JToolBar.VERTICAL);
		viewButtonBar.addMouseListener(ml);

		// wrap viewButtonBar to be vertically centered
		JPanel gluePanel = new JPanel();
		gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.Y_AXIS));
		gluePanel.add(Box.createVerticalGlue());
		gluePanel.add(viewButtonBar);
		gluePanel.add(Box.createVerticalGlue());
		gluePanel.setBackground(SystemColor.control);

		fullPanel = new JPanel(new BorderLayout());
		fullPanel.add(Box.createVerticalStrut(50), BorderLayout.NORTH);

		fullPanel.add(gluePanel, BorderLayout.CENTER);
		fullPanel.add(getGridButtonPanel(), BorderLayout.SOUTH);
		fullPanel.setBackground(SystemColor.control);
		fullPanel.setOpaque(true);

		getMinimumPanel();

		popup = new MyPopup();
		popup.addMouseListener(ml);

		addListenerToAllComponents(this, ml);

		setLabels();
		updateLayout();
		toggleMinimumFullPanel();
	}

	private JPanel getGridButtonPanel() {

		// properties button
		MySmallJButton btnProperties = new MySmallJButton(
				app.getImageIcon("view-properties24.png"), 7);
		btnProperties.setFocusPainted(false);
		btnProperties.setBorderPainted(false);
		btnProperties.setContentAreaFilled(false);
		btnProperties.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int viewId = AbstractApplication.VIEW_PROPERTIES;
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(viewId), viewId, false);
			}

		});
		btnProperties.setToolTipText(app.getMenuTooltip("Properties"));

		// file open button
		btnFileOpen = new DockButton(app.getImageIcon("document-open22.png"));
		btnFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.getGuiManager().openFile();
			}
		});

		// print button
		btnFileSave = new DockButton(app.getImageIcon("document-save22.png"));
		btnFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.getGuiManager().save();
			}
		});

		// print button
		btnPrint = new DockButton(app.getImageIcon("document-print22.png"));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GeoGebraMenuBar.showPrintPreview(app);
			}
		});

		// help button
		DockButton btnHelp = new DockButton(app.getImageIcon("help22.png"));
		btnHelp.setFocusPainted(false);
		btnHelp.setBorderPainted(false);
		btnHelp.setContentAreaFilled(false);
		btnHelp.setToolTipText(app.getMenuTooltip("Help"));

		// TODO: better help action ?
		btnHelp.addActionListener(new HelpAction(app, app
				.getImageIcon("help.png"), app.getMenu("Help"),
				AbstractApplication.WIKI_MANUAL));

		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		//buttonPanel.add(Box.createVerticalStrut(20));
		buttonPanel.add(OptionsUtil.flowPanelCenter(0, 0, 0, SystemColor.control, btnHelp));
		//buttonPanel.add(Box.createVerticalStrut(2));
		//buttonPanel.add(OptionsUtil.flowPanelCenter(0, 0, 0,
		//		SystemColor.control, btnFileOpen));
		//buttonPanel.add(Box.createVerticalStrut(2));
		//buttonPanel.add(OptionsUtil.flowPanelCenter(0, 0, 0,
		//		SystemColor.control, btnFileSave));
		//buttonPanel.add(Box.createVerticalStrut(2));
		//buttonPanel.add(OptionsUtil.flowPanelCenter(0, 0, 0,
			//	SystemColor.control, btnPrint));

		buttonPanel.add(Box.createVerticalStrut(20));

		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(SystemColor.control);

		JPanel p = new JPanel(new BorderLayout());
		p.add(buttonPanel, BorderLayout.CENTER);
		
		
		return p;
	}

	/**
	 * Updates the layout. If minimized, minimumPanel is shown Otherwise, either
	 * the popup or the full panel is shown
	 */
	protected void updateLayout() {

		if (enableHiding) {
			popup.removeAll();
			popup.add(fullPanel);
			fullPanel.setBorder(BorderFactory.createEmptyBorder());
		} else {
			Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 1,
					SystemColor.controlShadow);
			Border insideBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
					SystemColor.controlLtHighlight);
			fullPanel.setBorder(BorderFactory.createCompoundBorder(
					outsideBorder, insideBorder));
		}
	}

	private void toggleMinimumFullPanel() {

		removeAll();

		if (isMinimized) {
			add(minimumPanel, BorderLayout.CENTER);
		} else {
			add(fullPanel, BorderLayout.CENTER);
		}
		this.revalidate();
		this.repaint();
	}

	private void showPopup() {
		if (!popup.isVisible()) {
			popup.setPopupSize(popup.getPreferredSize().width,
					getMinimumPanel().getHeight() - 4);
			popup.show(this, -popup.getPreferredSize().width
					+ getMinimumPanel().getWidth(), 2);
		}
	}

	private void hidePopup() {
		if (popup.isVisible()) {
			popup.setVisible(false);
		}
	}

	public void update() {
		// app.updateToolBar();

		// btnSettings.setSelected(!app.isMainPanelShowing());

		// btnView.setVisible(app.isMainPanelShowing());
		// btnOptions.setVisible(app.isMainPanelShowing());

	}

	public void openDockBar() {
		isMinimized = false;
		updateLayout();
	}

	public void updateViewButtons() {
		viewButtonBar.updateViewButtons();
	}

	/**
	 * Mouse listener to handle showing the popup.
	 */
	class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!(e.getSource() instanceof AbstractButton)) {
				hidePopup();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			showPopup();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Application.printStacktrace("exit"
			// +e.getSource().getClass().getName());

			Component source = (Component) e.getSource();
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, source);
			Rectangle r = new Rectangle(popup.getLocationOnScreen());
			r.width = popup.getWidth();
			r.height = popup.getHeight();

			if (!r.contains(p) && e.getPoint().x < app.getFrame().getWidth()) {
				hidePopup();
				// System.out.println(mainPanel.getBounds().contains(e.getPoint()));
			}

			// lblIcon.setIcon(app.getImageIcon("dockbar-triangle.png"));
			// minimumPanel.setBackground(null);
			// minimumPanel.setBorder(normalBorder);
		}
	}

	public static void addListenerToAllComponents(JComponent c, MouseAdapter l) {

		c.addMouseListener(l);

		for (Component cc : c.getComponents())
			if (cc instanceof JComponent) {
				addListenerToAllComponents((JComponent) cc, l);
			}
	}

	public void actionPerformed(ActionEvent e) {
		e.getSource();
		update();
	}

	/**
	 * Returns minimumPanel, a slim vertical bar displayed when the dockBar is
	 * minimized. When clicked it restores the dockBar to full size.
	 * 
	 */
	private JPanel getMinimumPanel() {

		final Border normalBorder = BorderFactory.createMatteBorder(1, 1, 0, 1,
				SystemColor.controlShadow);

		BorderFactory.createBevelBorder(BevelBorder.RAISED);

		final JLabel lblIcon;

		if (minimumPanel == null) {
			minimumPanel = new JPanel(new BorderLayout(0, 0));
			
			lblIcon = new JLabel(); //app.getImageIcon("dockbar-triangle.png"));
			lblIcon.setFont(app.getFont(false, Font.PLAIN, 10));
			lblIcon.setText("\u25C3");
			lblIcon.setPreferredSize(new Dimension(10, 0));
			minimumPanel.add(lblIcon, BorderLayout.CENTER);

			minimumPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 0) {
						if (!enableHiding) {
							isMinimized = false;
							toggleMinimumFullPanel();
							//lblIcon.setIcon(app
								//	.getImageIcon("dockbar-triangle.png"));
						}
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {

					if (enableHiding) {
						showPopup();
					}

					//lblIcon.setIcon(app
						//	.getImageIcon("dockbar-triangle-rollover.png"));
					// minimumPanel.setBackground(Color.LIGHT_GRAY);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					//lblIcon.setIcon(app.getImageIcon("dockbar-triangle.png"));
					minimumPanel.setBackground(null);
					minimumPanel.setBorder(normalBorder);
				}
			});
		}
		minimumPanel.setBackground(null);
		minimumPanel.setBorder(normalBorder);
		return minimumPanel;
	}
	
	
	public void setLabels() {

		btnPrint.setToolTipText(app.getMenu("Print"));
		btnFileSave.setToolTipText(app.getMenu("Save"));
		btnFileOpen.setToolTipText(app.getMenu("Load"));

		updateViewButtons();
	}
	
	

	class MyPopup extends JPopupMenu {

		public MyPopup() {
			super();
			setOpaque(true);
			setBackground(SystemColor.control);
			setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 8));

		}

		@Override
		public void setVisible(boolean isVisible) {

			super.setVisible(isVisible);
		}

	}

	class DockButton extends JButton {

		private JToolTip tip;

		public DockButton(Icon ic) {
			super(ic);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setMargin(new Insets(1, 1, 1, 1));
			setOpaque(true);
			setBackground(SystemColor.control);

			addMouseListener(new ToolTipMouseAdapter());
		}

		@Override
		public void setIcon(Icon ic) {

			int s = app.getImageIcon("check.png").getIconWidth();
			super.setIcon(GeoGebraIcon.joinIcons(
					GeoGebraIcon.createEmptyIcon(s, s), (ImageIcon) ic));

			Dimension dim = new Dimension(getIcon().getIconWidth() + 5,
					getIcon().getIconHeight() + 10);
			setPreferredSize(dim);
			setMaximumSize(dim);
			setMinimumSize(dim);

		}

		@Override
		public JToolTip createToolTip() {
			tip = super.createToolTip();
			// add margin
			tip.setBorder(BorderFactory.createCompoundBorder(tip.getBorder(),
					BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			return tip;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event) {
			// position the tip to the right of the button, vertically centered
			Point p = new Point();
			p.y = 0;
			if (tip != null) {
				p.y = this.getHeight() / 2 - tip.getPreferredSize().height / 2;
			}
			p.x = this.getWidth() + 5;
			return p;
		}

		/**
		 * Listeners that give the tool tip a custom initial delay = 0
		 */
		public class ToolTipMouseAdapter extends MouseAdapter {
			private int defaultInitialDelay;
			private boolean preventToolTipDelay = true;

			@Override
			public void mouseEntered(MouseEvent e) {
				defaultInitialDelay = ToolTipManager.sharedInstance()
						.getInitialDelay();
				if (preventToolTipDelay) {
					ToolTipManager.sharedInstance().setInitialDelay(0);
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				ToolTipManager.sharedInstance().setInitialDelay(
						defaultInitialDelay);

			}

		}

	}

	

	// ============================================
	// Full screen button
	// (experimental code)
	// ===========================================
	private boolean fullScreen = false;

	private void toggleFullScreen() {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		// GraphicsDevice[] gs = ge.getScreenDevices();
		// Determine if full-screen mode is supported directly
		if (gs.isFullScreenSupported()) {
			AbstractApplication.info("full screen mode supported");
		} else {
			AbstractApplication.info("full screen mode not supported");
		}

		fullScreen = !fullScreen;
		JFrame f = app.getFrame();
		try {
			if (fullScreen) { // Enter full-screen mode

				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension dim = toolkit.getScreenSize();
				f.setResizable(true);

				f.removeNotify();
				f.setUndecorated(true);
				f.addNotify();

				gs.setFullScreenWindow(f);

				f.setLocation(0, 0);
				f.setSize(dim);
				f.validate();

			} else { // Return to normal windowed mode

				gs.setFullScreenWindow(null);

				f.removeNotify();
				f.setUndecorated(false);
				f.addNotify();
				f.validate();

			}
		}

		catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

		finally {
			// Exit full-screen mode
			// AbstractApplication.info("finally");
			// gs.setFullScreenWindow(null);
		}

	}

}
