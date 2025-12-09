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

package org.geogebra.web.full.main.mask;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MaskWidgetList;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.PointerEventHandler;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

/**
 * Container class to hold and update the masks as widgets
 * on demand.
 *
 * @author laszlo
 */
public class MaskWidgetListW implements MaskWidgetList {

	private GeoGebraFrameFull appletFrame;
	private final EuclidianView view;
	private final EuclidianController controller;

	private List<MaskWidget> widgets = new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param app {@link AppWFull}
	 */
	public MaskWidgetListW(AppWFull app) {
		this.appletFrame = app.getAppletFrame();
		this.view = app.getActiveEuclidianView();
		this.controller = view.getEuclidianController();
	}

	@Override
	public void clearMasks() {
		for (MaskWidget w: widgets) {
			w.removeFromParent();
		}
		widgets.clear();
	}

	@Override
	public void masksToForeground() {
		clearMasks();
		for (GeoPolygon mask : getMasks()) {
			add(mask);
		}
	}

	private List<GeoPolygon> getMasks() {
		Construction cons = controller.getKernel().getConstruction();

		List<GeoPolygon> list = new ArrayList<>();
		for (GeoElement geo : cons.getGeoSetConstructionOrder()) {
			if (geo.isMask()) {
				list.add((GeoPolygon) geo);
			}
		}
		return list;
	}

	private void add(final GeoPolygon polygon) {
		final MaskWidget maskWidget = new MaskWidget(polygon, view);
		widgets.add(maskWidget);
		ClickStartHandler.init(maskWidget, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				int xOffset = maskWidget.getAbsoluteLeft()
						- view.getAbsoluteLeft();
				int yOffset = maskWidget.getAbsoluteTop()
						- view.getAbsoluteTop();
				controller.widgetsToBackground();
				view.getApplication().getSelectionManager().clearSelectedGeos();
				
				if (view instanceof EuclidianViewW) {
					PointerEventHandler.startCapture((EuclidianViewW) view);
					MouseTouchGestureControllerW mtg = ((EuclidianControllerW) controller)
							.getMouseTouchGestureController();
					AbstractEvent event = new PointerEvent(x + xOffset,
							y + yOffset, type, mtg);
					mtg.onPointerEventStart(event);
				}
			}
		});

		appletFrame.add(maskWidget);
	}
}
