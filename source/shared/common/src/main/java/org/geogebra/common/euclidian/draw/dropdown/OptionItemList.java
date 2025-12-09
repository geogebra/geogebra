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

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.kernel.geos.GeoList;

class OptionItemList {
	static final int VERTICAL_PADDING = 10;
	static final int HORIZONTAL_PADDING = 16;
	private final List<OptionItem> items;
	private final GeoList geoList;
	private GDimension maxDimension;

	OptionItemList(GeoList geoList) {
		this.geoList = geoList;
		this.items = new ArrayList<>();
	}

	int size() {
		return items.size();
	}

	OptionItem get(int idx) {
		return items.get(idx);
	}

	void fill(DrawDropDownList drawable) {
		items.clear();
		for (int i = 0; i < geoList.size(); i++) {
			OptionItem item = new OptionItem(geoList, drawable, i);
			items.add(item);
		}
	}

	void update(DropDownModel settings) {
		settings.updateFont();
		items.forEach(item -> item.update(settings.getFont()));
		calculateItemDimensions();
	}

	private void calculateItemDimensions() {
		int width = maxOfItems(OptionItem::getWidth);
		int height = maxOfItems(OptionItem::getHeight);
		maxDimension = AwtFactory.getPrototype().newDimension(width + 2 * HORIZONTAL_PADDING,
				height + 2 * VERTICAL_PADDING);
	}

	private int maxOfItems(ToIntFunction<OptionItem> extractor) {
		return items.stream().mapToInt(extractor).max().orElse(0);
	}

	OptionItem at(int x, int y) {
		return items.stream().filter(item -> item.isHit(x, y)).findFirst()
				.orElse(null);
	}

	int indexOf(OptionItem item) {
		return items.indexOf(item);
	}

	int getMaxWidth() {
		return maxDimension.getWidth();
	}

	int getMaxHeight() {
		return maxDimension.getHeight();
	}

	void updateMaxDimension(int boxWidth) {
		maxDimension = AwtFactory.getPrototype()
				.newDimension(
						Math.max(boxWidth, maxDimension.getWidth()),
						maxDimension.getHeight());
	}

	GDimension getMaxDimension() {
		return maxDimension;
	}

	boolean noDimension() {
		return maxDimension == null;
	}
}
