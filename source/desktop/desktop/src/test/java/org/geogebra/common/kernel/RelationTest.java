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
 
package org.geogebra.common.kernel;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Locale;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.factories.CASFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.junit.Before;
import org.junit.Test;

public class RelationTest extends BaseUnitTest {

	private GeoElement A;
	private GeoElement B;
	private GeoElement C;
	private GeoElement f;
	private GeoElement a;
	private GeoElement b;

	/**
	 * Initialize objects
	 */
	@Before
	public void setupObjects() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		getApp().setCASFactory(new CASFactoryD());
		A = add("A=(0,0)");
		B = add("B=(0,1)");
		f = add("f=Line(A,B)");
		C = add("C=Midpoint(A,B)");
		add("s=Semicircle(A,B)");
		add("D=Point(s)");
		add("SetValue(D,(-0.7,0.3))");
		a = add("a=Segment(A,D)");
		b = add("b=Segment(B,D)");
	}

	@Test
	public void moreButtonShouldShowNDGS() {
		Relation rel = new Relation(getApp(), A, B, C, null);
		assertThat(rel.getRows()[0].getInfo(),
				containsString("collinear"));
		assertThat(rel.getExpandedRow(0).getInfo(),
				allOf(containsString("under the condition"),
						containsString("are not equal")));
	}

	@Test
	public void moreButtonShouldShowNDGSPath() {
		Relation rel = new Relation(getApp(), f, C, null, null);
		assertThat(rel.getRows()[0].getInfo(),
				containsString("lies on"));
		assertThat(rel.getExpandedRow(0).getInfo(),
				allOf(containsString("under the condition"),
						containsString("are not equal")));
	}

	@Test
	public void relationOrdering() {
		Relation rel = new Relation(getApp(), a, b, null, null);
		RelationPane.RelationRow[] rows = rel.getRows();
		assertThat(rows[0].getInfo(),
				containsString("perpendicular"));
		assertThat(rows[1].getInfo(),
				containsString("length"));

		getApp().setLocale(new Locale("es"));
		rows = new Relation(getApp(), a, b, null, null).getRows();
		assertThat(rows[0].getInfo(),
				containsString("perpendicular"));
		assertThat(rows[1].getInfo(),
				containsString("longitud"));
	}
}
