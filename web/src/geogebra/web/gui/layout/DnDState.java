package geogebra.web.gui.layout;

/**
 * The state of the drag'n'drop procedure.
 * 
 * @author Florian Sonner
 */
public class DnDState {
	/**
	 * The panel will be placed in the top area of the target panel.
	 */
	public static final int TOP = 1;
	
	/**
	 * The panel will be placed in the right area of the target panel.
	 */
	public static final int RIGHT = 2;
	
	/**
	 * The panel will be placed at the bottom of the target panel.
	 */
	public static final int BOTTOM = 4;
	
	/**
	 * The panel will be placed in the left area of the target panel.
	 */
	public static final int LEFT = 8;
	
	/**
	 * The panel will be placed above the split pane (just for horizontal
	 * split panes).
	 */
	public static final int TOP_OUT = 16;
	
	/**
	 * The panel will be placed on the right side of the split pane (just
	 * for vertical split panes).
	 */
	public static final int RIGHT_OUT = 32;
	
	/**
	 * The panel will be placed at the bottom of the split pane (just for
	 * horizontal split panes).
	 */
	public static final int BOTTOM_OUT = 64;
	
	/**
	 * The panel will be placed on the left side of the split pane (just for
	 * vertical split panes). 
	 */
	public static final int LEFT_OUT = 128;
	
	
	/**
	 * The panel which was dragged by the user. Can't be changed from the
	 * outside as a new source should create a new DockState. 
	 */
	private DockPanelW source;
	
	/**
	 * The panel the mouse is above at the moment.
	 */
	private DockPanelW target;
	
	/**
	 * The exact region the mouse is above at the moment, see the constants
	 * of this class.
	 */
	private int region = TOP;
	
	public DnDState(DockPanelW source) {
		this.source = source;
	}
	
	public DockPanelW getSource() {
		return source;
	}
	
	public void setTarget(DockPanelW target) {
		this.target = target;
	}
	
	public DockPanelW getTarget() {
		return target;
	}
	
	public void setRegion(int region) {
		if(region < 0 || region > LEFT_OUT) 
			throw new IllegalArgumentException();
		
		this.region = region;
	}
	
	public int getRegion() {
		return region;
	}
	
	public boolean isRegionOut() {
		return region >= TOP_OUT;
	}
}
