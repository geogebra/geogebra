package org.geogebra.common.awt;

public abstract class GDimension {

	public abstract int getWidth();

	public abstract int getHeight();

	@Override
	public final boolean equals(Object e) {
		if (e instanceof GDimension) {
			return getWidth() == ((GDimension) e).getWidth()
					&& getHeight() == ((GDimension) e).getHeight();
		}
		return false;
	}

}
