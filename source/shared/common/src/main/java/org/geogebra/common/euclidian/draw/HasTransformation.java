package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GAffineTransform;

/**
 * Object whose position and orientation is given by an affine transform.
 */
public interface HasTransformation {
	GAffineTransform getTransform();
}
