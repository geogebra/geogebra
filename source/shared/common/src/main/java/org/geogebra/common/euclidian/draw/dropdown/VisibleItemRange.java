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

package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.kernel.geos.GeoList;

class VisibleItemRange {
	private int start;
	private int end;
	private int selected;
	private final GeoList list;

	VisibleItemRange(GeoList list) {
		this.list = list;
	}

	int getStart() {
		return start;
	}

	int getEnd() {
		return end;
	}

	int getSelected() {
		return selected;
	}

	void setSelected(int idx) {
		selected = idx;
	}

	void adjustToSelected() {
		start = list.getSelectedIndex() + getVisibleItemCount() < list.size()
			? list.getSelectedIndex() : list.size() - getVisibleItemCount();
	}

	int getVisibleItemCount() {
		return Math.max(1, end - start);
	}

	void reset() {
		start = 0;
	}

	void selectStart() {
		selected = start;
	}

	void updateStart() {
		start = list.getSelectedIndex();
	}

	void setVisibleAll() {
		start = 0;
		end = list.size();
	}

	void setVisible(int count) {
		if (start + count < list.size()) {
			end = start + count + 1;
		} else {
			start = list.size() - count - 1;
			end = list.size();
		}
	}

	boolean shiftBy(int diff) {
		if (start + diff >= 0 && end + diff < list.size() + 1) {
			start += diff;
			end += diff;
			return true;
		}
		return false;
	}
}
