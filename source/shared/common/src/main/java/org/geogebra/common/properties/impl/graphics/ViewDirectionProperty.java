package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class ViewDirectionProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private static final PropertyResource[] icons = {
			PropertyResource.ICON_VIEW_DIRECTION_XY, PropertyResource.ICON_VIEW_DIRECTION_XZ,
			PropertyResource.ICON_VIEW_DIRECTION_YZ
	};
	private static final String[] rawLabels = {
		"stylebar.ViewXY", "stylebar.ViewXZ", "stylebar.ViewYZ"
	};
	private final EuclidianView3D euclidianView;
	private int viewDirection = -1;

	/**
	 * Creates a view direction property
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public ViewDirectionProperty(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "stylebar.ViewDirection");
		this.euclidianView = euclidianView;
		setValues(List.of(EuclidianStyleConstants.VIEW_DIRECTION_XY,
				EuclidianStyleConstants.VIEW_DIRECTION_XZ,
				EuclidianStyleConstants.VIEW_DIRECTION_YZ));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		if (value != viewDirection) {
			viewDirection = value;
		}
		switch (value) {
		case EuclidianStyleConstants.VIEW_DIRECTION_XY:
			euclidianView.setRotAnimation(-90, 90, true);
			break;
		case EuclidianStyleConstants.VIEW_DIRECTION_XZ:
			euclidianView.setRotAnimation(-90, 0, true);
			break;
		case EuclidianStyleConstants.VIEW_DIRECTION_YZ:
			euclidianView.setRotAnimation(0, 0, true);
			break;
		default:
			// do nothing
		}
	}

	@Override
	public Integer getValue() {
		return viewDirection;
	}

	@Override
	public @CheckForNull String[] getLabels() {
		return rawLabels;
	}
}
