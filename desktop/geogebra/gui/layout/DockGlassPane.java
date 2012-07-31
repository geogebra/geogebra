package geogebra.gui.layout;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * Glass pane is used to draw the drag-preview area on the panels if the
 * user enters the drag'n'drop mode.
 * 
 * @author Florian Sonner
 */
public class DockGlassPane extends JPanel implements AWTEventListener {
	private static final long serialVersionUID = 1L;
	
	private boolean dragInProgress = false;
	private BasicStroke stroke;
	private DockManager dockManager;

	private DockPanel[] dockPanels;
	private Rectangle[] dockPanelsBounds;

	private DnDState dndState;
	private Rectangle previewRect = new Rectangle();
	

	private Color color;
	
	private static final Color COLOR_DEFAULT = Color.gray;
	private static final Color COLOR_NOT_ENOUGH_SPACE = COLOR_DEFAULT;//Color.red;
	private static final Color COLOR_SAME_PLACE = Color.white;
	

	public DockGlassPane(DockManager dockManager) {
		this.dockManager = dockManager;
		
		stroke = new BasicStroke(4);
		
		setOpaque(false);
	}

	public boolean isDragInProgress() {
		return dragInProgress;
	}
	
	/**
	 * Start the dragging process by adding the mouse listeners.
	 * 
	 * @param state
	 */
	public void startDrag(DnDState state) {
		if(dragInProgress)
			return;
		
		dragInProgress = true;
		
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
		setVisible(true);

		dndState = state;
		dndState.setTarget(state.getSource());

		// cache the absolute bounds of all DockPanels
		dockPanels = dockManager.getPanels();
		ArrayList<DockPanel> dockPanelsList = new ArrayList<DockPanel>();
		ArrayList<Rectangle> bounds = new ArrayList<Rectangle>();
		Rectangle tmpRect;
		
		for(int i = 0; i < dockPanels.length; ++i) {
			// we don't need to care about invisible or views in a different window for the drag'n'drop
			if(!dockPanels[i].isVisible() || dockPanels[i].isOpenInFrame())
				continue;
			
			tmpRect = dockPanels[i].getBounds();
			tmpRect.setLocation(SwingUtilities.convertPoint(
				dockPanels[i],
				dockPanels[i].getLocation(),
				this
			));
			tmpRect.x -= dockPanels[i].getX();
			tmpRect.y -= dockPanels[i].getY();
			
			dockPanelsList.add(dockPanels[i]);
			bounds.add(tmpRect);
		}
		
		dockPanelsBounds = new Rectangle[bounds.size()];
		dockPanelsBounds = (bounds.toArray(dockPanelsBounds));
		
		dockPanels = new DockPanel[dockPanelsList.size()];
		dockPanels = (dockPanelsList.toArray(dockPanels));
		
		previewRect = new Rectangle();
	}

	/**
	 * The mouse was released, quit the drag'n'drop mode.
	 */
	public void stopDrag() {
		if(!dragInProgress)
			return;
		
		dragInProgress = false;
		
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		setVisible(false);
		
		dockManager.drop(dndState);
		dndState = null;
	}

	/**
	 * Paint the outline of the region on the window where the selected panel
	 * would reside if the user drops the panel.
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (dndState == null || dndState.getTarget() == null)
			return;

		Graphics2D g2d = (Graphics2D) g;

		g2d.setStroke(stroke);
		g2d.setColor(color);
		
		// draw the preview rectangle
		g2d.drawRect(
			previewRect.x,
			previewRect.y,
			previewRect.width,
			previewRect.height
		);
	}
	
	
	private void setColorEnoughHeight(DockPanel target){
		if (target.getHeight()<DockComponent.MIN_SIZE*2)
			color = COLOR_NOT_ENOUGH_SPACE;
	}

	private void setColorEnoughWidth(DockPanel target){
		if(target.getWidth()<DockComponent.MIN_SIZE*2)
			color = COLOR_NOT_ENOUGH_SPACE;
	}

	/**
	 * Calculate where the panel would be placed if the mouse is released.
	 * @param event
	 */
	public void mouseDragged(MouseEvent event) {
		Point mousePosition = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), this);
		
		boolean update = false;
		
		// Check if the mouse intersects with any DockPanel
		for (int i = 0; i < dockPanelsBounds.length; ++i) {
			if (mousePosition.x >= dockPanelsBounds[i].x
				&& mousePosition.x <= dockPanelsBounds[i].x + dockPanelsBounds[i].getWidth()
				&& mousePosition.y >= dockPanelsBounds[i].y
				&& mousePosition.y <= dockPanelsBounds[i].y+ dockPanelsBounds[i].getHeight())
			{
				update = true;
				dndState.setTarget(dockPanels[i]);
				break;
			}
		}

		if (update) {
			DockPanel target = dndState.getTarget();

			Point targetAbsPosition = SwingUtilities.convertPoint(
				target.getParent(),
				target.getLocation(),
				this
			);
			
			Point mouseRelPosition = mousePosition;
			mouseRelPosition.translate(-targetAbsPosition.x, -targetAbsPosition.y);
			
			int orientation = ((DockSplitPane)target.getParent()).getOrientation();			
			float leftPercent = mouseRelPosition.x * 1.0f / target.getWidth();
			float topPercent = mouseRelPosition.y * 1.0f / target.getHeight();
			float maxDist = 0.35f;
			
			previewRect.setLocation(targetAbsPosition);
			previewRect.setSize(target.getSize());
			
			color = COLOR_DEFAULT;
			
			// calculate the preview rectangle
			if(orientation == JSplitPane.VERTICAL_SPLIT) {
				if(leftPercent < maxDist) {
					if(leftPercent < maxDist / 2) {
						dndState.setRegion(DnDState.LEFT_OUT);
						setColorEnoughWidth(target);
						
						DockSplitPane splitPane = (DockSplitPane)target.getParent();
						previewRect.setLocation(SwingUtilities.convertPoint(splitPane.getParent(), splitPane.getLocation(), this));
						previewRect.width *= maxDist / 2;
						previewRect.height = splitPane.getHeight();
					} else {
						dndState.setRegion(DnDState.LEFT);
						setColorEnoughWidth(target);
						
						previewRect.width *= maxDist;
					}
				} else if(leftPercent > 1 - maxDist) {
					if(leftPercent > 1 - maxDist / 2) {
						dndState.setRegion(DnDState.RIGHT_OUT);
						setColorEnoughWidth(target);
						
						DockSplitPane splitPane = (DockSplitPane)target.getParent();
						previewRect.setLocation(SwingUtilities.convertPoint(splitPane.getParent(), splitPane.getLocation(), this));
						previewRect.x += previewRect.width * (1 - maxDist / 2);
						previewRect.width *= maxDist / 2;
						previewRect.height = splitPane.getHeight();
					} else {
						dndState.setRegion(DnDState.RIGHT);
						setColorEnoughWidth(target);
						
						previewRect.x += previewRect.width * (1 - maxDist);
						previewRect.width *= maxDist;
					}
				} else {
					if(topPercent < 0.5) {
						dndState.setRegion(DnDState.TOP);
						setColorEnoughHeight(target);
						
						previewRect.height *= 0.5f;
					} else {
						dndState.setRegion(DnDState.BOTTOM);
						setColorEnoughHeight(target);
						
						previewRect.y += previewRect.height * 0.5f;
						previewRect.height *= 0.5f;
					}
				}
				
				
				
				
				
			} else {
				if(topPercent < maxDist) {
					if(topPercent < maxDist / 2) {
						dndState.setRegion(DnDState.TOP_OUT);
						setColorEnoughHeight(target);
						
						DockSplitPane splitPane = (DockSplitPane)target.getParent();
						previewRect.setLocation(SwingUtilities.convertPoint(splitPane.getParent(), splitPane.getLocation(), this));
						previewRect.height *= maxDist / 2;
						previewRect.width = splitPane.getWidth();
					} else {
						dndState.setRegion(DnDState.TOP);
						setColorEnoughHeight(target);
						
						previewRect.height *= maxDist;
					}
				} else if(topPercent > 1 - maxDist) {
					if(topPercent > 1 - maxDist / 2) {
						dndState.setRegion(DnDState.BOTTOM_OUT);
						setColorEnoughHeight(target);
						
						DockSplitPane splitPane = (DockSplitPane)target.getParent();
						previewRect.setLocation(SwingUtilities.convertPoint(splitPane.getParent(), splitPane.getLocation(), this));
						previewRect.y += previewRect.height * (1 - maxDist / 2);
						previewRect.height *= maxDist / 2;
						previewRect.width = splitPane.getWidth();
					} else {
						dndState.setRegion(DnDState.BOTTOM);
						setColorEnoughHeight(target);
						
						previewRect.y += previewRect.height * (1-maxDist);
						previewRect.height *= maxDist;
					}
				} else {
					if(leftPercent < 0.5) {
						dndState.setRegion(DnDState.LEFT);
						setColorEnoughWidth(target);
						
						previewRect.width *= 0.5f;
					} else {
						dndState.setRegion(DnDState.RIGHT);
						setColorEnoughWidth(target);
						
						previewRect.x += previewRect.width * 0.5f;
						previewRect.width *= 0.5f;
					}
				}
				
			}
			
			// nothing changed
			if(target == dndState.getSource() && !dndState.isRegionOut()) {
				previewRect.setSize(target.getSize());
				previewRect.setLocation(targetAbsPosition);
				
				color = COLOR_SAME_PLACE;
			}
			
			previewRect.x += (int)(Math.ceil(stroke.getLineWidth() / 2));
			previewRect.y += (int)(Math.ceil(stroke.getLineWidth() / 2));
			previewRect.width -= stroke.getLineWidth();
			previewRect.height -= stroke.getLineWidth();
		} else {
			dndState.setTarget(null);
		}
		
		repaint();
	}
	
	/**
	 * Redirect every mouse action.
	 */
	public void eventDispatched(AWTEvent event) {
		if(event instanceof MouseEvent) {
			if(event.getID() == MouseEvent.MOUSE_DRAGGED && ((MouseEvent)event).getModifiers() == InputEvent.BUTTON1_MASK)
				mouseDragged((MouseEvent)event);
			else if(event.getID() == MouseEvent.MOUSE_RELEASED)
				stopDrag();
		}
	}
}
