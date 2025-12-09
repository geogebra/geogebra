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
 
package org.geogebra.common.plugin.evaluator;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.EditorTyper;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Tests for the EvaluatorAPI
 */
public class EvaluatorAPITest extends BaseUnitTest {

	private EditorTyper typer;
	private EvaluatorAPI api;

	@Before
	public void setupTest() {
		MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), null);
		api = new EvaluatorAPI(getKernel(), mathField.getInternal());
		typer = new EditorTyper(mathField);
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	public void testGetEvaluatorValue() {
		typer.type("1/2");
		Map<String, Object> value = api.getEvaluatorValue();

		assertEquals("{\\frac{1}{2}}", value.get("latex").toString());
		assertEquals("((1)/(2))", value.get("content").toString());
		assertEquals("0.5", value.get("eval").toString());
		assertEquals("start fraction 1 over 2 end fraction",
				value.get("altText").toString().trim());
	}

	@Test
	public void testGetEvaluatorValueNonNumeric() {
		typer.type("GeoGebra");
		Map<String, Object> value = api.getEvaluatorValue();

		assertEquals("GeoGebra", value.get("latex").toString());
		assertEquals("GeoGebra", value.get("content").toString());
		assertEquals("NaN", value.get("eval").toString());
	}

	@Test
	public void testEmptyInput() {
		Map<String, Object> value = api.getEvaluatorValue();

		assertEquals("\\nbsp{}", value.get("latex").toString());
		assertEquals("", value.get("content").toString());
		assertEquals("NaN", value.get("eval").toString());
	}

	@Test
	public void testInvalidInput() {
		typer.type("1/");
		Map<String, Object> value = api.getEvaluatorValue();

		assertEquals("{\\frac{1}{" + TeXSerializer.PLACEHOLDER + "}}",
				value.get("latex").toString());
		assertEquals("((1)/())", value.get("content").toString());
		assertEquals("NaN", value.get("eval").toString());
	}

	@Test
	public void testSetEditorState() {
		api.setEditorState("{content:\"1+1/5\"}");
		Map<String, Object> value = api.getEvaluatorValue();

		assertEquals("1+{\\frac{1}{5}}", value.get("latex").toString());
		assertEquals("1+((1)/(5))", value.get("content").toString());
		assertEquals("1.2", value.get("eval").toString());
	}

	@Test
	public void testSetEditorStateInvalidCaret() {
		api.setEditorState("{content:\"1+1/5\", caret: \"/\"}");
		Map<String, Object> value = api.getEvaluatorValue();
		assertEquals("1+((1)/(5))", value.get("content").toString());
	}

	@Test
	public void testSetEditorStateInvalidContent() {
		api.setEditorState("{caret: [1,2,3]}");
		Map<String, Object> value = api.getEvaluatorValue();
		assertEquals("", value.get("content").toString());
	}

}
