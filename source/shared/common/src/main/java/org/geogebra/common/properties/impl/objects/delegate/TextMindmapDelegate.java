package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;

public class TextMindmapDelegate extends AbstractGeoElementDelegate {

	public TextMindmapDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof GeoMindMapNode || element instanceof GeoInlineText;
	}
}
