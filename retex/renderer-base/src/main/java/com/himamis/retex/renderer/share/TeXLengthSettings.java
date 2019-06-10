package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

public class TeXLengthSettings {

	private static final Map<String, TeXLength> defaultMap;
	private static final Map<String, Double> defaultFactorMap;

	static {
		defaultMap= new HashMap<>();
		defaultFactorMap = new HashMap<>();

		// make \fcolorbox margin compatible with jlm v1
		defaultMap.put("fboxsep", new TeXLength(Unit.EM, 0.65));
		// thicker border of eg \ovalbox
		defaultMap.put("fboxrule", new TeXLength(Unit.PT, 2));
		defaultMap.put("scriptspace", new TeXLength(Unit.PT, 0.5));
		defaultMap.put("nulldelimiterspace", new TeXLength(Unit.PT, 1.2));
		defaultMap.put("delimitershortfall", new TeXLength(Unit.PT, 5.));
		defaultMap.put("dashlength", new TeXLength(Unit.PT, 6.));
		defaultMap.put("dashdash", new TeXLength(Unit.PT, 3.));
		defaultMap.put("shadowsize", new TeXLength(Unit.PT, 4.));
		defaultMap.put("tabcolsep", new TeXLength(Unit.PT, 0));
		defaultMap.put("baselineskip", new TeXLength(Unit.EX, 1.));
		defaultMap.put("textwidth", new TeXLength(Unit.NONE, Double.POSITIVE_INFINITY));

		defaultFactorMap.put("delimiterfactor", 901.);
		defaultFactorMap.put("cornersize", 0.5);
		defaultFactorMap.put("arraystretch", 1.);
	}

	private final Map<String, TeXLength> map;
	private final Map<String, Double> factorMap;

	public TeXLengthSettings() {
		map = new HashMap<>(defaultMap);
		factorMap = new HashMap<>(defaultFactorMap);;
	}

	public static boolean isLengthName(final String name) {
		return defaultMap.containsKey(name);
	}

	public static boolean isFactorName(final String name) {
		return defaultFactorMap.containsKey(name);
	}

	public void setLength(final String name, final TeXLength l) {
		map.put(name, l);
	}

	public TeXLength getLength(final String name, final double factor) {
		final TeXLength l = map.get(name);
		if (l != null) {
			return l.scale(factor);
		}
		return null;
	}

	public static TeXLength getDefaultLength(final String name, final double factor) {
		final TeXLength l = defaultMap.get(name);
		if (l != null) {
			return l.scale(factor);
		}
		return null;
	}

	public double getLength(final String name,
			final TeXEnvironment env) {
		final TeXLength l = map.get(name);
		if (l != null) {
			return l.getL() * l.getUnit().getFactor(env);
		}
		return 0.;
	}

	public Atom getLength(final String name) {
		return map.get(name).toAtom();
	}

	public double getTextwidth(TeXEnvironment env) {
		return getLength("textwidth", env);
	}

	public void setFactor(final String name, final double f) {
		factorMap.put(name, f);
	}

	public double getFactor(final String name) {
		return factorMap.get(name);
	}
}
