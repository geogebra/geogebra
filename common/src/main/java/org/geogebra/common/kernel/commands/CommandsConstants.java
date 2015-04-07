/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.commands;

/**
 * ID's of tables for commands
 */
public interface CommandsConstants {
	/** Geometry commands */
	public static final int TABLE_GEOMETRY = 0;
	/** Algebra commands */
	public static final int TABLE_ALGEBRA = 1;
	/** Text commands */
	public static final int TABLE_TEXT = 2;
	/** Logical commands */
	public static final int TABLE_LOGICAL = 3;
	/** Functions and calculus commands */
	public static final int TABLE_FUNCTION = 4;
	/** Conic commands */
	public static final int TABLE_CONIC = 5;
	/** List commands */
	public static final int TABLE_LIST = 6;
	/** Matrix and vector commands */
	public static final int TABLE_VECTOR = 7;
	/** Transformation commands */
	public static final int TABLE_TRANSFORMATION = 8;
	/** Charts */
	public static final int TABLE_CHARTS = 9;
	/** Statistics commands */
	public static final int TABLE_STATISTICS = 10;
	/** Probability commands */
	public static final int TABLE_PROBABILITY = 11;
	/** Spreadsheet commands */
	public static final int TABLE_SPREADSHEET = 12;
	/** Scripting commands */
	public static final int TABLE_SCRIPTING = 13;
	/** Discrete math commands */
	public static final int TABLE_DISCRETE = 14;
	/** GeoGebra commands */
	public static final int TABLE_GEOGEBRA = 15;
	/** Optimization commands */
	public static final int TABLE_OPTIMIZATION = 16;
	/** 3D commands */
	public static final int TABLE_3D = 17;
	/** CAS commands -- do not display this table by default */
	public static final int TABLE_CAS = 18;
	/** Financial commands */
	public static final int TABLE_FINANCIAL = 19;

	/**
	 * Compatibility commands in case some English commands were renamed. This
	 * table should never be displayed in input help.
	 */
	public static final int TABLE_ENGLISH = 20;

}
