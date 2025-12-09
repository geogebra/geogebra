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

package org.geogebra.common.kernel.commands;

/**
 * ID's of tables for commands
 */
public interface CommandsConstants {

	/**
	 * Index for commands
	 */
	int ALL_COMMANDS_INDEX = -1;

	/**
	 * Index for math commands
	 */
	int MATH_FUNC_INDEX = -2;

	/** Geometry commands */
	int TABLE_GEOMETRY = 0;
	/** Algebra commands */
	int TABLE_ALGEBRA = 1;
	/** Text commands */
	int TABLE_TEXT = 2;
	/** Logical commands */
	int TABLE_LOGICAL = 3;
	/** Functions and calculus commands */
	int TABLE_FUNCTION = 4;
	/** Conic commands */
	int TABLE_CONIC = 5;
	/** List commands */
	int TABLE_LIST = 6;
	/** Matrix and vector commands */
	int TABLE_VECTOR = 7;
	/** Transformation commands */
	int TABLE_TRANSFORMATION = 8;
	/** Charts */
	int TABLE_CHARTS = 9;
	/** Statistics commands */
	int TABLE_STATISTICS = 10;
	/** Probability commands */
	int TABLE_PROBABILITY = 11;
	/** Spreadsheet commands */
	int TABLE_SPREADSHEET = 12;
	/** Scripting commands */
	int TABLE_SCRIPTING = 13;
	/** Discrete math commands */
	int TABLE_DISCRETE = 14;
	/** GeoGebra commands */
	int TABLE_GEOGEBRA = 15;
	/** Optimization commands */
	int TABLE_OPTIMIZATION = 16;
	/** 3D commands */
	int TABLE_3D = 17;
	/** CAS commands -- do not display this table by default */
	int TABLE_CAS = 18;
	/** Financial commands */
	int TABLE_FINANCIAL = 19;

	/**
	 * Compatibility commands in case some English commands were renamed. This
	 * table should never be displayed in input help.
	 */
	int TABLE_ENGLISH = 20;

}
