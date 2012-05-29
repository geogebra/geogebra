package geogebra.gui.layout;

import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private Layout layout;

	private MyPopup popup;
	private JDialog dialog;

	private JPanel mainPanel, minimumPanel;
	private ViewButtonBar viewButtonBar;


	/**
	 * flag to determine if the dockbar is in a minimized state
	 */
	protected boolean isMinimized = true;

	/**
	 * Constructs a DockBar
	 * 
	 * @param app
	 */
	public DockBar(Application app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();

		initGUI();
		setBorder(BorderFactory.createEmptyBorder());
		//updateLayout();
	}

	private void initGUI() {

		setLayout(new BorderLayout());

		viewButtonBar = new ViewButtonBar(app);
		viewButtonBar.setOrientation(JToolBar.VERTICAL);
		viewButtonBar.addMouseListener(new MyMouseListener());
		// viewButtonBar.setPreferredSize(new Dimension(32, 32));

		// wrap toolbar to be vertically centered
		JPanel gluePanel = new JPanel();
		gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.Y_AXIS));
		gluePanel.add(Box.createVerticalGlue());
		gluePanel.add(viewButtonBar);
		gluePanel.add(Box.createVerticalGlue());
		gluePanel.setBackground(SystemColor.control);
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(gluePanel, BorderLayout.WEST);
		

		// Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 1,
		// SystemColor.controlShadow);
		// Border insideBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
		// SystemColor.controlLtHighlight);
		// mainPanel.setBorder(BorderFactory.createCompoundBorder(outsideBorder,
		// insideBorder));

		mainPanel.setBackground(SystemColor.control);
		mainPanel.setBorder(BorderFactory.createEmptyBorder());
		
		MouseAdapter l = new MyMouseListener();
		this.addListenerToAllComponents(mainPanel, l);

		popup = new MyPopup();
		popup.removeAll();
		popup.add(mainPanel);
		popup.setOpaque(true);
		popup.setBackground(SystemColor.control);
		popup.setBorderPainted(false);

		popup.addMouseListener(l);
		getMinimizedPanel();
		this.add(getMinimizedPanel(), BorderLayout.CENTER);

		this.revalidate();

	}

	
	/**
	 * Updates the layout to either show the dockbar and its components or show
	 * the minimized panel.
	 */
	protected void updateLayout() {
		// this.removeAll();
		if (isMinimized && popup.isVisible()) {
			//Application.printStacktrace("");
			popup.setVisible(false);
			// this.add(getMinimizedPanel(), BorderLayout.CENTER);
		} else if(!popup.isVisible()) {
			popup.setPreferredSize(new Dimension(36, this.getMinimizedPanel()
					.getHeight() - 0));
			popup.show(this, 0, 0);
			// this.add(mainPanel, BorderLayout.CENTER);
		}
		this.revalidate();
		this.repaint();
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
	 * Mouse listener to handle minimizing the dock.
	 */
	class MyMouseListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
			if (!(e.getSource() instanceof AbstractButton)) {
				isMinimized = true;
				updateLayout();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			//System.out.println("mouse released from: " + e.getSource().getClass().getName());
		}
		
		
		
		@Override
		public void mouseEntered(MouseEvent e) {
			isMinimized = false;
			updateLayout();
			// lblIcon.setIcon(app.getImageIcon("dockbar-triangle-rollover.png"));
			// minimumPanel.setBackground(Color.LIGHT_GRAY);
			// restorePanel.setBorder(hoverBorder);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component source = (Component) e.getSource();
			//System.out.println(mainPanel.getBounds().contains(e.getPoint()));
			if (!mainPanel.getBounds().contains(e.getPoint())) {
				isMinimized = true;
				updateLayout();
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
			if (cc instanceof JComponent)
				addListenerToAllComponents((JComponent) cc, l);
	}


	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		update();
	}

	

	/**
	 * Returns minimumPanel, a slim vertical bar displayed when the dockBar is
	 * minimized. When clicked it restores the dockBar to full size.
	 * 
	 */
	private JPanel getMinimizedPanel() {

		final Border normalBorder = BorderFactory.createMatteBorder(1, 0, 0, 1,
				SystemColor.controlShadow);

		final Border hoverBorder = BorderFactory
				.createBevelBorder(BevelBorder.RAISED);

		final JLabel lblIcon;

		if (minimumPanel == null) {
			minimumPanel = new JPanel(new BorderLayout(0, 0));
			lblIcon = new JLabel(app.getImageIcon("dockbar-triangle.png"));
			lblIcon.setPreferredSize(new Dimension(10, 0));
			minimumPanel.add(lblIcon, BorderLayout.CENTER);

			minimumPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 0) {
						isMinimized = false;
						updateLayout();
						lblIcon.setIcon(app
								.getImageIcon("dockbar-triangle.png"));
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					isMinimized = false;
					updateLayout();
					lblIcon.setIcon(app
							.getImageIcon("dockbar-triangle-rollover.png"));
					minimumPanel.setBackground(Color.LIGHT_GRAY);
					// restorePanel.setBorder(hoverBorder);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// isMinimized = true;
					// updateLayout();
					lblIcon.setIcon(app.getImageIcon("dockbar-triangle.png"));
					minimumPanel.setBackground(null);
					minimumPanel.setBorder(normalBorder);
				}
			});
		}
		minimumPanel.setBackground(null);
		minimumPanel.setBorder(normalBorder);
		return minimumPanel;
	}

	
	class MyPopup extends JPopupMenu{
		
		public MyPopup(){
			super();
			MouseListener[] listeners = this.getMouseListeners();
			for(MouseListener l: listeners){
				this.removeMouseListener(l);
			}
		}
		
		
		@Override
		public void setVisible(boolean isVisible){
			
			//Application.printStacktrace("popup visible: " + isVisible);
			super.setVisible(isVisible);
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
