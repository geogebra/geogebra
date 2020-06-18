package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;

public abstract class AbstractGeoElementDelegate implements GeoElementDelegate {

	protected final GeoElement element;

	/**
	 * Create a new AbstractGeoElementDelegate
	 * @param element element
	 * @throws NotApplicablePropertyException if not applicable
	 */
	public AbstractGeoElementDelegate(GeoElement element) throws NotApplicablePropertyException {
		this.element = element;
		checkIsApplicable();
	}

	@Override
	public GeoElement getElement() {
		return element;
	}

	@Override
	public void checkIsApplicable() throws NotApplicablePropertyException {
		if (!checkIsApplicable(element)) {
			throw new NotApplicablePropertyException(element, this);
		}
	}

	@Override
	public boolean isEnabled() {
		return element.isEuclidianVisible();
	}

	protected abstract boolean checkIsApplicable(GeoElement element);

	protected boolean isApplicableToGeoList(GeoList list) {
		for (int i = 0; i < list.size(); i++) {
			if (!(checkIsApplicable(list.get(i)))) {
				return false;
			}
		}
		return true;
	}

	protected boolean isTextOrInput(GeoElement element) {
		return element instanceof GeoText || element instanceof GeoInputBox;
	}
}
