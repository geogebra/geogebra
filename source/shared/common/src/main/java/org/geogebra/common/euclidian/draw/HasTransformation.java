package org.geogebra.common.euclidian.draw;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GAffineTransform;

/**
 * Object whose position and orientation is given by an affine transform.
 */
public interface HasTransformation {
	@MissingDoc
	GAffineTransform getTransform();
}
