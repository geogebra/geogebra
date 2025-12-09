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

package org.geogebra.common.geogebra3D.main.settings;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Settings for view for plane
 * 
 * @author mathieu
 *
 */
public class EuclidianSettingsForPlane extends EuclidianSettings {
	private boolean mirror = false;
	private int rotate = 0;
	private boolean isFromLoadFile = false;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 */
	public EuclidianSettingsForPlane(App app) {
		super(app);
	}

	/**
	 * set transform for plane
	 * 
	 * @param mirror
	 *            mirrored
	 * @param rotate
	 *            rotated
	 */
	public void setTransformForPlane(boolean mirror, int rotate) {
		this.mirror = mirror;
		this.rotate = rotate;

	}

	/**
	 * 
	 * @return if mirrored
	 */
	public boolean getMirror() {
		return mirror;
	}

	/**
	 * 
	 * @return rotation angle
	 */
	public int getRotate() {
		return rotate;
	}

	@Override
	public boolean isViewForPlane() {
		return true;
	}

	/**
	 * set these settings created from loading file or not
	 * 
	 * @param flag
	 *            flag
	 */
	public void setFromLoadFile(boolean flag) {
		isFromLoadFile = flag;
	}

	/**
	 * 
	 * @return if these settings created from loading file or not
	 */
	public boolean isFromLoadFile() {
		return isFromLoadFile;
	}

}
