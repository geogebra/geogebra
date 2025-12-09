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

package org.geogebra.editor.share.catalog;

/**
 * Template for arrays and matrices with configurable delimiters.
 */
public class ArrayTemplate extends Template {

	private final int dimension;
	private final ArrayDelimiter openDelimiter;
	private final ArrayDelimiter closeDelimiter;
	private final ArrayDelimiter fieldDelimiter;
	private final ArrayDelimiter rowDelimiter;

	ArrayTemplate(Tag name, int dimension, ArrayDelimiter openDelimiter,
			ArrayDelimiter closeDelimiter,
			ArrayDelimiter fieldDelimiter, ArrayDelimiter rowDelimiter) {
		super(name, name.toString());
		this.dimension = dimension;
		this.openDelimiter = openDelimiter;
		this.closeDelimiter = closeDelimiter;
		this.fieldDelimiter = fieldDelimiter;
		this.rowDelimiter = rowDelimiter;
	}

	/**
	 * @return opening delimiter
	 */
	public ArrayDelimiter getOpenDelimiter() {
		return openDelimiter;
	}

	/**
	 * @return closing delimiter
	 */
	public ArrayDelimiter getCloseDelimiter() {
		return closeDelimiter;
	}

	/**
	 * @return delimiter between fields
	 */
	public ArrayDelimiter getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 * @return delimiter between rows
	 */
	public ArrayDelimiter getRowDelimiter() {
		return rowDelimiter;
	}

	/**
	 * @return true if 1-dimensional
	 */
	public boolean isArray() {
		return dimension == 1;
	}

	/**
	 * @return true if 2-dimensional
	 */
	public boolean isMatrix() {
		return dimension == 2;
	}
}
