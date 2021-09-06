package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;

import com.himamis.retex.editor.share.util.Unicode;

public class SymbolReaderMap {
	private final Map<Character, String> map;
	public SymbolReaderMap(Localization loc) {
		map = new HashMap<>();
		addOperatorsAndBraces();
		addGreekLowerLetters(loc);
		map.put('\u2218', ScreenReader.getDegrees(loc));

	}

	private void addOperatorsAndBraces() {
		map.put('+', " plus ");
		map.put('-', " minus ");
		map.put('=', " equals ");
		map.put(',', ScreenReader.getComma());
		map.put('(', ScreenReader.getOpenParenthesis());
		map.put(')', ScreenReader.getCloseParenthesis());
		map.put('{', " open brace ");
		map.put('}', " close brace ");
		map.put('[', " open bracket ");
		map.put(']', " close bracket ");
		map.put('\'', "");
	}

	private void addGreekLowerLetters(Localization loc) {
		map.put(Unicode.alpha, loc.getMenuDefault("alpha", "alpha"));
		map.put(Unicode.beta, loc.getMenuDefault("beta", "beta"));
		map.put(Unicode.gamma, loc.getMenuDefault("gamma", "gamma"));
		map.put(Unicode.delta, loc.getMenuDefault("delta", "delta"));
		map.put(Unicode.epsilon, loc.getMenuDefault("epsilon", "epsilon"));
		map.put(Unicode.zeta, loc.getMenuDefault("zeta", "zeta"));
		map.put(Unicode.eta, loc.getMenuDefault("eta", "eta"));
		map.put(Unicode.theta, loc.getMenuDefault("theta", "theta"));
		map.put(Unicode.iota, loc.getMenuDefault("iota", "iota"));
		map.put(Unicode.kappa, loc.getMenuDefault("kappa", "kappa"));
		map.put(Unicode.lambda, loc.getMenuDefault("lambda", "lambda"));
		map.put(Unicode.mu, loc.getMenuDefault("mu", "mu"));
		map.put(Unicode.nu, loc.getMenuDefault("nu", "nu"));
		map.put(Unicode.xi, loc.getMenuDefault("xi", "xi"));
		map.put(Unicode.omicron, loc.getMenuDefault("omicron", "omicron"));
		map.put(Unicode.pi, loc.getMenuDefault("pi", "pi"));
		map.put(Unicode.rho, loc.getMenuDefault("rho", "rho"));
		map.put(Unicode.sigma, loc.getMenuDefault("sigma", "sigma"));
		map.put(Unicode.sigmaf, loc.getMenuDefault("sigma final form", "sigma  final form"));
		map.put(Unicode.tau, loc.getMenuDefault("tau", "tau"));
		map.put(Unicode.upsilon, loc.getMenuDefault("upsilon", "upsilon"));
	}

	public String get(Character ch) {
		return map.getOrDefault(ch, ch + "");
	}
}