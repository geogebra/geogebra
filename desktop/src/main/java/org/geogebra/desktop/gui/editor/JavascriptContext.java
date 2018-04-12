/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.Element;
import javax.swing.text.View;

import org.geogebra.desktop.main.AppD;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class JavascriptContext extends ViewContext {

	/**
	 * TOKENS : A Map which contains the names of keywords
	 */
	private static final Map<String, Integer> TOKENS = new HashMap<>(
			14);

	static {
		TOKENS.put("Default", LexerConstants.DEFAULT);
		TOKENS.put("Operator", JavascriptLexerConstants.OPERATOR);
		TOKENS.put("Constante", JavascriptLexerConstants.CONSTANTE);
		TOKENS.put("Number", JavascriptLexerConstants.NUMBER);
		TOKENS.put("OpenClose", JavascriptLexerConstants.OPENCLOSE);
		TOKENS.put("String", JavascriptLexerConstants.STRING);
		TOKENS.put("Built-in Object", JavascriptLexerConstants.BUILTINOBJECT);
		TOKENS.put("Keyword", JavascriptLexerConstants.KEYWORD);
		TOKENS.put("Identifier", JavascriptLexerConstants.IDENTIFIER);
		TOKENS.put("Field Definition", JavascriptLexerConstants.FIELDDEF);
		TOKENS.put("Field", JavascriptLexerConstants.FIELD);
		TOKENS.put("GeoGebra Special", JavascriptLexerConstants.GGBSPECIAL);
		TOKENS.put("Object Name", JavascriptLexerConstants.OBJECTNAME);
		TOKENS.put("Line Comments", JavascriptLexerConstants.LINECOMMENTS);
		TOKENS.put("Multi Line Comments",
				JavascriptLexerConstants.MULTILINECOMMENTS);
		TOKENS.put("Function", JavascriptLexerConstants.FUNCTION);
		TOKENS.put("White", LexerConstants.WHITE);
		TOKENS.put("Tabulation", LexerConstants.TAB);
	}

	private View view;
	private List<Integer> typeToDefault = new ArrayList<>();

	private static final Map<String, Color> colorMap = new HashMap<>();
	static {
		colorMap.put("Default", Color.decode("#000000"));
		colorMap.put("Operator", Color.decode("#000000"));
		colorMap.put("Constante", Color.decode("#aa38f2"));
		colorMap.put("Number", Color.decode("#781d00"));
		colorMap.put("OpenClose", Color.decode("#af38ae"));
		colorMap.put("String", Color.decode("#ae6c4d"));
		colorMap.put("Built-in Object", Color.decode("#cf7454"));
		colorMap.put("Keyword", Color.decode("#aa38f2"));
		colorMap.put("Identifier", Color.decode("#000000"));
		colorMap.put("Field Definition", Color.decode("#848484"));
		colorMap.put("Field", Color.decode("#848484"));
		colorMap.put("GeoGebra Special", Color.decode("#ffaa00"));
		colorMap.put("Object Name", Color.decode("#68baba"));
		colorMap.put("Line Comments", Color.decode("#00b700"));
		colorMap.put("Multi Line Comments", Color.decode("#00b700"));
		colorMap.put("Function", Color.decode("#0303ff"));
		colorMap.put("White", Color.decode("#dcdcdc"));
		colorMap.put("Tabulation", Color.decode("#dcdcdc"));
	}

	private static final Map<String, Integer> attribMap = new HashMap<>();
	static {
		attribMap.put("Default", 0);
		attribMap.put("Operator", 0);
		attribMap.put("Constante", 0);
		attribMap.put("Number", 0);
		attribMap.put("OpenClose", 0);
		attribMap.put("String", 0);
		attribMap.put("Built-in Object", 0);
		attribMap.put("Keyword", 0);
		attribMap.put("Identifier", 0);
		attribMap.put("Field Definition", 0);
		attribMap.put("Field", 0);
		attribMap.put("GeoGebra Special", 0);
		attribMap.put("Object Name", 0);
		attribMap.put("Function", 0);
		attribMap.put("Line Comments", 0);
		attribMap.put("Multi Line Comments", 0);
		attribMap.put("White", 0);
		attribMap.put("Tabulation", 0);
	}

	/**
	 * The constructor
	 * 
	 * @param app
	 *            the Application where this context is needed
	 */
	public JavascriptContext(AppD app) {
		super();
		tokenFont = app.getPlainFont();
		genColors();
		genAttributes();
	}

	/**
	 * Generate an attribute for a type of keyword
	 * 
	 * @param keyword
	 *            the name can be found in scinotesConfiguration.xml
	 * @param type
	 *            the type to use
	 */
	public void genAttribute(String keyword, int type) {
		tokenAttrib[TOKENS.get(keyword)] = type;
		if (TOKENS.get(keyword) == LexerConstants.DEFAULT) {
			for (Integer i : typeToDefault) {
				tokenAttrib[i] = tokenAttrib[0];
			}
		}
	}

	/**
	 * Generate attributes to use to render the document
	 */
	public void genAttributes() {
		tokenAttrib = new int[JavascriptLexerConstants.NUMBEROFTOKENS];
		Map<String, Integer> map = attribMap;
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			String tokenType = entry.getKey();
			tokenAttrib[TOKENS.get(tokenType)] = entry.getValue().intValue();
		}

		for (Integer i : typeToDefault) {
			tokenAttrib[i] = tokenAttrib[0];
		}
	}

	/**
	 * Generate the colors to use to render the document
	 */
	public void genColors() {
		tokenColors = new Color[JavascriptLexerConstants.NUMBEROFTOKENS];
		Map<String, Color> map = colorMap;
		Iterator<Entry<String, Color>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Color> entry = it.next();
			String tokenType = entry.getKey();
			tokenColors[TOKENS.get(tokenType)] = entry.getValue();
		}

		typeToDefault.clear();
		for (int i = 0; i < tokenColors.length; i++) {
			if (tokenColors[i] == null) {
				tokenColors[i] = tokenColors[0];
				typeToDefault.add(i);
			}
		}
	}

	/**
	 * Generate a color for a type of keyword
	 * 
	 * @param name
	 *            the name can be found in scinotesConfiguration.xml
	 * @param color
	 *            the color to use
	 */
	public void genColors(String name, Color color) {
		if (tokenColors == null) {
			genColors();
		}

		tokenColors[TOKENS.get(name)] = color;

		if (TOKENS.get(name) == LexerConstants.DEFAULT) {
			for (Integer i : typeToDefault) {
				tokenColors[i] = tokenColors[0];
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getCurrentView() {
		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View create(Element elem) {
		view = new GeoGebraView(elem, new JavascriptLexer(), this);
		return view;
	}
}
