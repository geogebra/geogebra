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

package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.editor.share.util.Unicode;

/**
 * Class that converts symbols to readable text for screen readers.
 *
 */
public class SymbolReader {
	private final Map<Character, String> map;
	private final Localization loc;

	/**
	 * Constructor
	 * @param loc {@link Localization}
	 */
	public SymbolReader(Localization loc) {
		map = new HashMap<>();
		this.loc = loc;
		addOperatorsAndBraces();
		addGreekLowerLetters();
		map.put('\u2218', ScreenReader.getDegrees(loc));

	}

	private void addOperatorsAndBraces() {
		map.put('+', ScreenReader.getPlus(loc));
		map.put('-', ScreenReader.getMinus(loc));
		map.put('\u00b7', ScreenReader.getTimes(loc));
		map.put('\u22C5', ScreenReader.getTimes(loc));
		map.put(',', ScreenReader.getComma(loc));
		map.put('(', ScreenReader.getOpenParenthesis(loc));
		map.put(')', ScreenReader.getCloseParenthesis(loc));
		map.put('{', ScreenReader.getOpenBrace(loc));
		map.put('}', ScreenReader.getCloseBrace(loc));
		map.put('[', ScreenReader.getOpenBracket(loc));
		map.put(']', ScreenReader.getCloseBracket(loc));
		map.put(';', ScreenReader.getSemicolon(loc));
	}

	private void addGreekLowerLetters() {
		map.put(Unicode.alpha, greek("alpha"));
		map.put(Unicode.beta, greek("beta"));
		map.put(Unicode.gamma, greek("gamma"));
		map.put(Unicode.delta, greek("delta"));
		map.put(Unicode.epsilon, greek("epsilon"));
		map.put(Unicode.zeta, greek("zeta"));
		map.put(Unicode.eta, greek("eta"));
		map.put(Unicode.theta, greek("theta"));
		map.put(Unicode.iota, greek("iota"));
		map.put(Unicode.kappa, greek("kappa"));
		map.put(Unicode.lambda, greek("lambda"));
		map.put(Unicode.mu, greek("mu"));
		map.put(Unicode.nu, greek("nu"));
		map.put(Unicode.xi, greek("xi"));
		map.put(Unicode.omicron, greek("omicron"));
		map.put(Unicode.pi, greek("pi"));
		map.put(Unicode.rho, greek("rho"));
		map.put(Unicode.sigma, greek("sigma"));
		map.put(Unicode.sigmaf, greek("sigmaFinalForm"));
		map.put(Unicode.tau, greek("tau"));
		map.put(Unicode.upsilon, greek("upsilon"));
		map.put(Unicode.Alpha, greek("alpha"));
		map.put(Unicode.Beta, greek("beta"));
		map.put(Unicode.Gamma, greek("gamma"));
		map.put(Unicode.Delta, greek("delta"));
		map.put(Unicode.Epsilon, greek("epsilon"));
		map.put(Unicode.Zeta, greek("zeta"));
		map.put(Unicode.Eta, greek("eta"));
		map.put(Unicode.Theta, greek("theta"));
		map.put(Unicode.Iota, greek("iota"));
		map.put(Unicode.Kappa, greek("kappa"));
		map.put(Unicode.Lambda, greek("lambda"));
		map.put(Unicode.Mu, greek("mu"));
		map.put(Unicode.Nu, greek("nu"));
		map.put(Unicode.Xi, greek("xi"));
		map.put(Unicode.Omicron, greek("omicron"));
		map.put(Unicode.Pi, greek("pi"));
		map.put(Unicode.Rho, greek("rho"));
		map.put(Unicode.Sigma, greek("sigma"));
		//  Note: no Unicode.Sigmaf;
		map.put(Unicode.Tau, greek("tau"));
		map.put(Unicode.Upsilon, greek("upsilon"));
	}

	private String greek(String symbolName) {
		return loc.getMenuDefault(symbolName, symbolName);
	}

	/**
	 * Query the symbol for readable text.
	 *
	 * @param symbol to read.
	 * @return the readable text of the symbol.
	 */
	public String get(Character symbol) {
		return map.getOrDefault(symbol, String.valueOf(symbol));
	}
}
