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
 
package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Test;

public class AlgoListElementTest extends BaseUnitTest {

	private GeoList list;
	private GeoElement[] output;

	@Test
	public void testExistingValueInFlatList() {
		withList("{1,2,3,4}").at(1).shouldHaveValue("1");
	}

	@Test
	public void testTypeInNonExistingIndexFlatList() {
		withList("{(1,1)}").at(2).shouldHaveType(GeoClass.POINT);
	}

	@Test
	public void testEmptyFlatListShouldHaveElementTypeNumeric() {
		withList("{}").at(2).shouldHaveType(GeoClass.NUMERIC);
	}

	private void shouldHaveType(GeoClass geoClass) {
		assertThat(output[0], hasProperty("type",
				GeoElement::getGeoClassType, geoClass));
	}

	private AlgoListElementTest withList(String list) {
		this.list = add(list);
		return this;
	}

	private AlgoListElementTest at(int x) {
		GeoNumeric index = new GeoNumeric(getConstruction(), x);
		AlgoListElement algo = new AlgoListElement(getConstruction(), list, index);
		output = algo.getOutput();
		return this;
	}

	private void shouldHaveValue(String value) {
		assertThat(output[0], hasValue(value));

	}

	private AlgoListElementTest at(int... indexes) {
		GeoNumeric[] nums = Arrays.stream(indexes)
				.mapToObj(idx ->  new GeoNumeric(getConstruction(), idx))
				.toArray(GeoNumeric[]::new);

		AlgoListElement algo = new AlgoListElement(getConstruction(), list, nums,
				false);
		output = algo.getOutput();
		return this;
	}

	@Test
	public void testExistingValueInMatrix() {
		withList("{{1,2},{3,4}}").at(1, 1).shouldHaveValue("1");
		withList("{{1,2},{3,4}}").at(1, 2).shouldHaveValue("2");
		withList("{{1,2},{3,4}}").at(2, 1).shouldHaveValue("3");
		withList("{{1,2},{3,4}}").at(2, 2).shouldHaveValue("4");
	}

	@Test
	public void testNonExistingIndexInMatrix() {
		withList("{{1,2},{3,4}}").at(3, 1).shouldHaveType(GeoClass.NUMERIC);
		withList("{{1,2},{3,4}}").at(1, 3).shouldHaveType(GeoClass.NUMERIC);
	}

	@Test
	public void testNonExistingIndexInPointMatrix() {
		withList("{{(1,1),(0,0)},{(0,0),(4,4)}}").at(2, 2)
				.shouldHaveType(GeoClass.POINT);
	}

	@Test
	public void testEmptyMatrixShouldHaveElementTypeNumeric() {
		withList("{{},{}}").at(3, 1).shouldHaveType(GeoClass.NUMERIC);
		withList("{{},{}}").at(3, 16).shouldHaveType(GeoClass.NUMERIC);
	}
}