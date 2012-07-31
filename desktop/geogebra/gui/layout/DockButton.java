package geogebra.gui.layout;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

/***********************************************
 * DockButton class
 */
class DockButton extends JToggleButton {

	private AppD app;
	private JToolTip tip;

	public DockButton(AppD app, Icon ic) {
		this(app);
		setIcon(ic);
	}

	public DockButton(AppD app) {
		super();
		this.app = app;
		
		setFocusable(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setMargin(new Insets(0,0,5,0));
		
		addMouseListener(new ToolTipMouseAdapter());
	}
	
	
	@Override
	public void setIcon(Icon ic) {

		super.setSelectedIcon(GeoGebraIcon.joinIcons(
				app.getImageIcon("check.png"), (ImageIcon) ic));
		int s = app.getImageIcon("check.png").getIconWidth();
		super.setIcon(GeoGebraIcon.joinIcons(
				GeoGebraIcon.createEmptyIcon(s, s), (ImageIcon) ic));
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
		if (tip == null) {
			return super.getToolTipLocation(event);
		}

		// position the tip to the left of the button, vertically centered
		Point p = new Point();
		p.y = this.getHeight() / 2 - tip.getPreferredSize().height / 2;
		p.x = -tip.getPreferredSize().width - 2;

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