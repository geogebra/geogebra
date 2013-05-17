package geogebra.web.gui.layout;

import geogebra.common.gui.layout.DockComponent;
import geogebra.html5.awt.GRectangleW;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Glass pane is used to draw the drag-preview area on the panels if the user
 * enters the drag'n'drop mode.
 * 
 * @author Florian Sonner, adapted by G.Sturr
 */
public class DockGlassPaneW extends AbsolutePanel implements MouseUpHandler,
        MouseMoveHandler {
	private static final long serialVersionUID = 1L;

	private boolean dragInProgress = false;

	private DockManagerW dockManager;
	private DockPanelW[] dockPanels;
	private GRectangleW[] dockPanelsBounds;
	private DnDState dndState;

	private String color;
	private static final String COLOR_DEFAULT = "lightgray";
	private static final String COLOR_NOT_ENOUGH_SPACE = "lightgray";// Color.red;
	private static final String COLOR_SAME_PLACE = "white";

	private static final int BORDER_WIDTH = 4;

	private SimplePanel previewPanel;
	private Label previewLabel;

	private com.google.gwt.event.shared.HandlerRegistration reg1;

	private com.google.gwt.event.shared.HandlerRegistration reg2;

	/**********************************************
	 * Constructs a DockGlassPane
	 */
	public DockGlassPaneW() {

		setVisible(false);
		setStyleName("DockGlassPane");

		previewPanel = new SimplePanel();
		previewPanel.getElement().getStyle()
		        .setBorderWidth(BORDER_WIDTH, Style.Unit.PX);
		previewPanel.getElement().getStyle()
		        .setBorderStyle(Style.BorderStyle.SOLID);
		previewPanel.getElement().getStyle().setBorderColor("gray");

		// use previewLabel for debugging
		// previewLabel = new Label(" ");
		// previewPanel.add(previewLabel);

		previewPanel.setVisible(false);
		add(previewPanel);

		this.getElement().getStyle().setZIndex(5000);

	}

	public void attach(DockManagerW dockManager) {
		this.dockManager = dockManager;
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

		setVisible(true);
		reg1 = addDomHandler(this, MouseMoveEvent.getType());
		reg2 = addDomHandler(this, MouseUpEvent.getType());

		// this.getElement().getStyle().setZIndex(50);

		if (dragInProgress)
			return;

		dragInProgress = true;

		dndState = state;
		dndState.setTarget(state.getSource());

		// cache the absolute bounds of all DockPanels
		dockPanels = dockManager.getPanels();
		ArrayList<DockPanelW> dockPanelsList = new ArrayList<DockPanelW>();
		ArrayList<GRectangleW> bounds = new ArrayList<GRectangleW>();
		GRectangleW tmpRect;

		for (int i = 0; i < dockPanels.length; ++i) {
			// we don't need to care about invisible or views in a different
			// window for the drag'n'drop
			if (!dockPanels[i].isVisible() || dockPanels[i].isOpenInFrame())
				continue;

			// tmpRect = dockPanels[i].getBounds();
			tmpRect = new GRectangleW(dockPanels[i].getAbsoluteLeft(),
			        dockPanels[i].getAbsoluteTop(),
			        dockPanels[i].getOffsetWidth(),
			        dockPanels[i].getOffsetHeight());

			dockPanelsList.add(dockPanels[i]);
			bounds.add(tmpRect);
			// App.debug("left: " + tmpRect.getX() + "  top: " + tmpRect.getY()
			// + "  width: " + tmpRect.getWidth() + "  height: "
			// + tmpRect.getHeight());
		}

		dockPanelsBounds = new GRectangleW[bounds.size()];
		dockPanelsBounds = (bounds.toArray(dockPanelsBounds));

		dockPanels = new DockPanelW[dockPanelsList.size()];
		dockPanels = (dockPanelsList.toArray(dockPanels));
	}

	/**
	 * The mouse was released, quit the drag'n'drop mode.
	 */
	public void stopDrag() {

		if (!dragInProgress)
			return;

		reg1.removeHandler();
		reg2.removeHandler();

		setVisible(false);
		previewPanel.setVisible(false);
		dragInProgress = false;

		// this.getElement().getStyle().setZIndex(-5000);

		dockManager.drop(dndState);
		dndState = null;
	}

	private void setColorEnoughHeight(DockPanelW target) {
		if (target.getHeight() < DockComponent.MIN_SIZE * 2)
			color = COLOR_NOT_ENOUGH_SPACE;
	}

	private void setColorEnoughWidth(DockPanelW target) {
		if (target.getWidth() < DockComponent.MIN_SIZE * 2)
			color = COLOR_NOT_ENOUGH_SPACE;
	}

	/**
	 * Calculate where the panel would be placed if the mouse is released.
	 * 
	 * @param event
	 */
	public void mouseDragged(int mouseX, int mouseY) {

		int x2, y2, w, h;
		boolean update = false;

		// Check if the mouse intersects with any DockPanel
		for (int i = 0; i < dockPanelsBounds.length; ++i) {
			if (mouseX >= dockPanelsBounds[i].getX()
			        && mouseX <= dockPanelsBounds[i].getX()
			                + dockPanelsBounds[i].getWidth()
			        && mouseY >= dockPanelsBounds[i].getY()
			        && mouseY <= dockPanelsBounds[i].getY()
			                + dockPanelsBounds[i].getHeight()) {
				update = true;
				dndState.setTarget(dockPanels[i]);
				// App.debug("intersects with panel :" + i);
				break;
			}
		}

		if (update) {
			DockPanelW target = dndState.getTarget();

			x2 = target.getAbsoluteLeft();
			y2 = target.getAbsoluteTop();
			w = target.getOffsetWidth();
			h = target.getOffsetHeight();

			int relativeLeft = mouseX - x2;
			int relativeTop = mouseY - y2;

			int orientation = ((DockSplitPaneW) target.getParent())
			        .getOrientation();

			float leftPercent = relativeLeft * 1.0f / target.getOffsetWidth();
			float topPercent = relativeTop * 1.0f / target.getOffsetHeight();
			float maxDist = 0.35f;

			color = COLOR_DEFAULT;

			// calculate the preview rectangle
			if (orientation == DockSplitPaneW.VERTICAL_SPLIT) {

				if (leftPercent < maxDist) {
					if (leftPercent < maxDist / 2) {
						dndState.setRegion(DnDState.LEFT_OUT);
						setColorEnoughWidth(target);

						DockSplitPaneW splitPane = (DockSplitPaneW) target
						        .getParent();
						x2 = splitPane.getAbsoluteLeft();
						y2 = splitPane.getAbsoluteTop();

						w *= maxDist / 2;
						h = splitPane.getOffsetHeight();
					} else {
						dndState.setRegion(DnDState.LEFT);
						setColorEnoughWidth(target);
						w *= maxDist;
					}

				} else if (leftPercent > 1 - maxDist) {
					if (leftPercent > 1 - maxDist / 2) {
						dndState.setRegion(DnDState.RIGHT_OUT);
						setColorEnoughWidth(target);

						DockSplitPaneW splitPane = (DockSplitPaneW) target
						        .getParent();
						x2 = splitPane.getAbsoluteLeft();
						y2 = splitPane.getAbsoluteTop();

						x2 += w * (1 - maxDist / 2);
						w *= maxDist / 2;
						h = splitPane.getOffsetHeight();
					} else {
						dndState.setRegion(DnDState.RIGHT);
						setColorEnoughWidth(target);

						x2 += w * (1 - maxDist);
						w *= maxDist;
					}

				} else {
					if (topPercent < 0.5) {
						dndState.setRegion(DnDState.TOP);
						setColorEnoughHeight(target);

						h *= 0.5f;
					} else {
						dndState.setRegion(DnDState.BOTTOM);
						setColorEnoughHeight(target);

						y2 += h * 0.5f;
						h *= 0.5f;
					}
				}

			} else {
				if (topPercent < maxDist) {
					if (topPercent < maxDist / 2) {
						dndState.setRegion(DnDState.TOP_OUT);
						setColorEnoughHeight(target);

						DockSplitPaneW splitPane = (DockSplitPaneW) target
						        .getParent();
						x2 = splitPane.getAbsoluteLeft();
						y2 = splitPane.getAbsoluteTop();
						h *= maxDist / 2;
						w = splitPane.getOffsetWidth();

					} else {
						dndState.setRegion(DnDState.TOP);
						setColorEnoughHeight(target);

						h *= maxDist;
					}
				} else if (topPercent > 1 - maxDist) {
					if (topPercent > 1 - maxDist / 2) {
						dndState.setRegion(DnDState.BOTTOM_OUT);
						setColorEnoughHeight(target);

						DockSplitPaneW splitPane = (DockSplitPaneW) target
						        .getParent();
						x2 = splitPane.getAbsoluteLeft();
						y2 = splitPane.getAbsoluteTop();
						y2 += h * (1 - maxDist / 2);
						h *= maxDist / 2;
						w = splitPane.getOffsetWidth();
					} else {
						dndState.setRegion(DnDState.BOTTOM);
						setColorEnoughHeight(target);

						y2 += h * (1 - maxDist);
						h *= maxDist;
					}
				} else {
					if (leftPercent < 0.5) {
						dndState.setRegion(DnDState.LEFT);
						setColorEnoughWidth(target);

						w *= 0.5f;
					} else {
						dndState.setRegion(DnDState.RIGHT);
						setColorEnoughWidth(target);

						x2 += w * 0.5f;
						w *= 0.5f;
					}
				}

			}

			// nothing changed
			if (target == dndState.getSource() && !dndState.isRegionOut()) {
				x2 = target.getAbsoluteLeft();
				y2 = target.getAbsoluteTop();
				w = target.getOffsetWidth();
				h = target.getOffsetHeight();

				color = COLOR_SAME_PLACE;
			}

			// x2 += (int) (Math.ceil(stroke.getLineWidth() / 2));
			// y2 += (int) (Math.ceil(stroke.getLineWidth() / 2));
			w -= 2 * BORDER_WIDTH;
			h -= 2 * BORDER_WIDTH;

			setWidgetPosition(previewPanel, x2, y2);
			previewPanel.setPixelSize(w, h);
			previewPanel.getElement().getStyle().setBackgroundColor(color);
			previewPanel.getElement().getStyle().setOpacity(0.6f);
			previewPanel.setVisible(true);

		} else {
			dndState.setTarget(null);
			previewPanel.setVisible(false);
		}

	}

	public void onMouseMove(MouseMoveEvent event) {
		if (dragInProgress) {
			mouseDragged(event.getX(), event.getY());
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if (dragInProgress) {
			stopDrag();
		}
	}

}
