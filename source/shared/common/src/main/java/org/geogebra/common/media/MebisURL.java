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

package org.geogebra.common.media;

/**
 * Result of packUrl with error code.
 * 
 * @author laszlo
 *
 */
public class MebisURL extends VideoURL {
	private MebisError error;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            to set.
	 * @param error
	 *            to set.
	 */
	public MebisURL(String url, MebisError error) {
		super(url, error == MebisError.NONE, MediaFormat.VIDEO_MEBIS);
		this.error = error;
	}

	/**
	 * 
	 * @return error if any.
	 */
	public MebisError getError() {
		return error;
	}
}