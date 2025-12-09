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

package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;

import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

final class ArrayFlowPanel extends FlowPanel {
	private ArrayList<Widget> contents = new ArrayList<>();

	@Override
	public void clear() {
		contents.clear();
	}

	@Override
	public void insert(Widget widget, int pos) {
		contents.add(pos, widget);
	}

	@Override
	public void insert(IsWidget widget, int pos) {
		contents.add(pos, widget.asWidget());
	}

	@Override
	public int getWidgetCount() {
		return contents.size();
	}

	@Override
	public Widget getWidget(int i) {
		return contents.get(i);
	}
}