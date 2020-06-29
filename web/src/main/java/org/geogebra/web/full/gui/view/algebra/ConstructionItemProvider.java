package org.geogebra.web.full.gui.view.algebra;

import java.util.Iterator;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.ToStringConverter;

/**
 * Serializes construction items for keyboard input.
 */
public final class ConstructionItemProvider implements HasLastItem {

	private final Construction cons;
	private final AlgebraViewW algebraView;
	private final ToStringConverter<GeoElement> converter;
	private boolean isLastItemSimpleNumber;
	private boolean isLastItemText;

	/**
	 * @param cons construction
	 * @param algebraView Algebra view
	 */
	public ConstructionItemProvider(Construction cons, AlgebraViewW algebraView,
									ToStringConverter<GeoElement> converter) {
		this.cons = cons;
		this.algebraView = algebraView;
		this.converter = converter;
	}

	@Override
	public String getPreviousItemFrom(GeoElement element) {
		return convertToString(getPreviousElementFrom(element));
	}

	private String convertToString(GeoElement element) {
		if (element != null) {
			return converter.convert(element);
		}
		return "";
	}

	@Nullable
	private GeoElement getPreviousElementFrom(GeoElement element) {
		TreeSet<GeoElement> elements = cons.getGeoSetWithCasCellsConstructionOrder();
		Iterator<GeoElement> iterator = elements.descendingIterator();
		if (element == null) {
			return getPreviousElementFrom(iterator);
		} else {
			Iterator<GeoElement> iteratorFromElement = findElement(element, iterator);
			return getPreviousElementFrom(iteratorFromElement);
		}
	}

	private GeoElement getPreviousElementFrom(Iterator<GeoElement> iterator) {
		if (!iterator.hasNext()) {
			setLastItemFlagsWith(null);
			return null;
		}
		GeoElement iterated = iterator.next();
		while (algebraView.getNode(iterated) == null && iterator.hasNext()) {
			iterated = iterator.next();
		}
		if (algebraView.getNode(iterated) != null) {
			setLastItemFlagsWith(iterated);
			return iterated;
		} else {
			setLastItemFlagsWith(null);
			return null;
		}
	}

	private Iterator<GeoElement> findElement(GeoElement element, Iterator<GeoElement> iterator) {
		GeoElement iterated = null;
		while (iterated != element && iterator.hasNext()) {
			iterated = iterator.next();
		}
		return iterator;
	}

	private void setLastItemFlagsWith(GeoElement lastItem) {
		String lastItemString = convertToString(lastItem);
		isLastItemSimpleNumber = StringUtil.isSimpleNumber(lastItemString);
		isLastItemText = lastItem != null && lastItem.isGeoText();
	}

	@Override
	public boolean isLastItemSimpleNumber() {
		return isLastItemSimpleNumber;
	}

	@Override
	public boolean isLastItemText() {
		return isLastItemText;
	}
}