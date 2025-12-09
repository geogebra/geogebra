/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.layout;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.style.shared.BorderStyle;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchMoveHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * Glass pane is used to draw the drag-preview area on the panels if the user
 * enters the drag'n'drop mode.
 * 
 * @author Florian Sonner, adapted by G.Sturr
 */
public class DockGlassPaneW extends AbsolutePanel
		implements MouseMoveHandler, TouchMoveHandler {

	private boolean dragInProgress = false;

	private DockManagerW dockManager;
	private DockPanelW[] dockPanels;
	private Rectangle[] dockPanelsBounds;
	private DnDState dndState;

	private String color;
	private static final String COLOR_DEFAULT = "lightgray";
	private static final String COLOR_NOT_ENOUGH_SPACE = "lightgray"; // Color.red;
	private static final String COLOR_SAME_PLACE = "white";

	private static final int BORDER_WIDTH = 4;

	private SimplePanel previewPanel;

	private HandlerRegistration reg0;
	private HandlerRegistration reg1;
	private HandlerRegistration reg2;

	private GeoGebraElement ae;

	/**********************************************
	 * Constructs a DockGlassPane
	 */
	public DockGlassPaneW() {
		setVisible(false);
		setStyleName("DockGlassPane");

		previewPanel = new SimplePanel();
		previewPanel.getElement().getStyle()
			.setBorderWidth(BORDER_WIDTH, Unit.PX);
		previewPanel.getElement().getStyle()
				.setBorderStyle(BorderStyle.SOLID);
		previewPanel.getElement().getStyle().setBorderColor("gray");

		previewPanel.setVisible(false);
		add(previewPanel);

		this.getElement().getStyle().setZIndex(5000);
	}
	
	public void setGeoGebraElement(GeoGebraElement element) {
		this.ae = element;
	}

	/**
	 * @param dockManager1
	 *            dock manager
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void attach(DockManagerW dockManager1, int w, int h) {
		this.dockManager = dockManager1;
		if (h > 0 && w > 0) {
			this.getElement().getStyle().setPosition(Position.ABSOLUTE);
			this.setWidth(w + "px");
			this.setHeight(h + "px");
			this.getElement().getStyle().setTop(0, Unit.PX);
			this.getElement().getStyle().setLeft(0, Unit.PX);
		}
	}

	public boolean isDragInProgress() {
		return dragInProgress;
	}

	/**
	 * Start the dragging process by adding the mouse listeners.
	 * 
	 * @param state
	 *            drag state
	 */
	public void startDrag(DnDState state) {
		// we need capture for touch; otherwise touch move events go to
		// dragPanel
		Event.setCapture(this.getElement());
		setVisible(true);
		reg0 = addBitlessDomHandler(this, TouchMoveEvent.getType());
		reg1 = addDomHandler(this, MouseMoveEvent.getType());
		reg2 =
		ClickEndHandler.init(this, new ClickEndHandler() {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				onMouseUp();

			}
		});
		
		// this.getElement().getStyle().setZIndex(50);
		if (dragInProgress) {
			return;
		}

		dragInProgress = true;

		dndState = state;
		dndState.setTarget(state.getSource());

		// cache the absolute bounds of all DockPanels
		dockPanels = dockManager.getPanels();
		ArrayList<DockPanelW> dockPanelsList = new ArrayList<>();
		ArrayList<Rectangle> bounds = new ArrayList<>();

		for (DockPanelW dockPanel : dockPanels) {
			// we don't need to care about invisible or views in a different
			// window for the drag'n'drop
			if (dockPanel.isVisible()) {
				// tmpRect = dockPanels[i].getBounds();
				Rectangle tmpRect = new Rectangle(dockPanel.getAbsoluteLeft(),
						dockPanel.getAbsoluteTop(),
						dockPanel.getOffsetWidth(),
						dockPanel.getOffsetHeight());

				dockPanelsList.add(dockPanel);
				bounds.add(tmpRect);
			}
		}

		dockPanelsBounds = new Rectangle[bounds.size()];
		dockPanelsBounds = bounds.toArray(dockPanelsBounds);

		dockPanels = new DockPanelW[dockPanelsList.size()];
		dockPanels = dockPanelsList.toArray(dockPanels);
	}

	/**
	 * The mouse was released, quit the drag'n'drop mode.
	 */
	public void stopDrag() {
		Event.releaseCapture(this.getElement());
		if (!dragInProgress) {
			return;
		}
		reg0.removeHandler();
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
		if (target.getHeight() < DockComponent.MIN_SIZE * 2) {
			color = COLOR_NOT_ENOUGH_SPACE;
		}
	}

	private void setColorEnoughWidth(DockPanelW target) {
		if (target.getWidth() < DockComponent.MIN_SIZE * 2) {
			color = COLOR_NOT_ENOUGH_SPACE;
		}
	}

	/**
	 * Calculate where the panel would be placed if the mouse is released.
	 * 
	 * @param mouseX
	 *            mouse x-coord
	 * @param mouseY
	 *            mouse y-coord
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
				break;
			}
		}

		if (update) {
			DockPanelW target = dndState.getTarget();

			x2 = (int) (target.getAbsoluteLeft() / ae.getScaleX());
			y2 = (int) (target.getAbsoluteTop() / ae.getScaleY());
			w = target.getOffsetWidth();
			h = target.getOffsetHeight();

			int relativeLeft = mouseX - x2;
			int relativeTop = mouseY - y2;

			int orientation = ((DockSplitPaneW) target.getParent()).getOrientation();

			double leftPercent = relativeLeft * 1.0 / target.getOffsetWidth();
			double topPercent = relativeTop * 1.0 / target.getOffsetHeight();
			double maxDist = 0.35;

			color = COLOR_DEFAULT;

			// calculate the preview rectangle
			if (orientation == SwingConstants.VERTICAL_SPLIT) {

				if (leftPercent < maxDist) {
					if (leftPercent < maxDist / 2) {
						dndState.setRegion(DnDState.LEFT_OUT);
						setColorEnoughWidth(target);

						DockSplitPaneW splitPane = (DockSplitPaneW) target.getParent();
						x2 = (int) (splitPane.getAbsoluteLeft() / ae.getScaleX());
						y2 = (int) (splitPane.getAbsoluteTop() / ae.getScaleY());

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

						DockSplitPaneW splitPane = (DockSplitPaneW) target.getParent();
						x2 = (int) (splitPane.getAbsoluteLeft() / ae.getScaleX());
						y2 = (int) (splitPane.getAbsoluteTop() / ae.getScaleY());

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

						DockSplitPaneW splitPane = (DockSplitPaneW) target.getParent();
						x2 = (int) (splitPane.getAbsoluteLeft() / ae.getScaleX());
						y2 = (int) (splitPane.getAbsoluteTop() / ae.getScaleY());
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

						DockSplitPaneW splitPane = (DockSplitPaneW) target.getParent();
						x2 = (int) (splitPane.getAbsoluteLeft() / ae.getScaleX());
						y2 = (int) (splitPane.getAbsoluteTop() / ae.getScaleY());
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
				x2 = (int) (target.getAbsoluteLeft() / ae.getScaleX());
				y2 = (int) (target.getAbsoluteTop() / ae.getScaleY());
				w = target.getOffsetWidth();
				h = target.getOffsetHeight();

				color = COLOR_SAME_PLACE;
			}

			// x2 += (int) (Math.ceil(stroke.getLineWidth() / 2));
			// y2 += (int) (Math.ceil(stroke.getLineWidth() / 2));
			w -= 2 * BORDER_WIDTH;
			h -= 2 * BORDER_WIDTH;

			setWidgetPosition(previewPanel, x2
					- (int) (this.getAbsoluteLeft() / ae.getScaleX()), y2
					- (int) (this.getAbsoluteTop() / ae.getScaleY()));
			previewPanel.setPixelSize(w, h);
			previewPanel.getElement().getStyle().setBackgroundColor(color);
			previewPanel.getElement().getStyle().setOpacity(0.6f);
			previewPanel.setVisible(true);

		} else {
			dndState.setTarget(null);
			previewPanel.setVisible(false);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (dragInProgress) {
			// Use getClientX rather than getX, see #4049
			mouseDragged(event.getClientX() + NavigatorUtil.getWindowScrollLeft(),
					event.getClientY() + NavigatorUtil.getWindowScrollTop());
		}
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (dragInProgress) {
			if (event.getTouches().length() == 1) {
				event.preventDefault();
				mouseDragged(
						event.getTouches().get(0).getClientX()
								+ NavigatorUtil.getWindowScrollLeft(), event.getTouches()
								.get(0).getClientY()
								+ NavigatorUtil.getWindowScrollTop());
			}
		}
	}

	/**
	 * Handle mouse up.
	 */
	public void onMouseUp() {
		if (dragInProgress) {
			stopDrag();
		}
	}

	public GeoGebraElement getGeoGebraElement() {
		return ae;
	}

}
