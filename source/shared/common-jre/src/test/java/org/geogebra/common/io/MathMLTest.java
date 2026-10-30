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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.geogebra.editor.share.util.Unicode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class MathMLTest {
	private static final List<List<String>> testInputs = List.of(
			// quadratic formula
			conversion("((- b ± sqrt(((b)^(2))- 4 a c)) / (2 a))", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<mfrac> <mrow> <mo> - </mo> <mi> b </mi> <mo> &PlusMinus; </mo> <msqrt>
<msup> <mrow> <mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup>
<mo> - </mo> <mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow>
<mn> 2 </mn> <mi> a </mi> </mrow> </mfrac> </mstyle> </math>"""),
			// quadratic formula with comment
			conversion("((- b ± sqrt(((b)^(2))- 4 a c)) / (2 a))", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<mfrac> <mrow> <mo> - </mo> <mi> b </mi>
<mo> &#x00B1;<!--plus-minus sign--> </mo> <msqrt> <msup> <mrow>
<mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup> <mo> - </mo>
<mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow> <mn> 2 </mn>
<mi> a </mi> </mrow> </mfrac> </mstyle> </math>"""),

			// MathJax tests http://www.mathjax.org/demos/mathml-samples/
			// quadratic formula
			conversion("x = ((- b ± sqrt(((b)^(2))- 4 a c)) / (2 a))", """
<math display='block'><mrow><mi>x</mi><mo>=</mo><mfrac><mrow><mo>&#x2212;</mo>
<mi>b</mi><mo>&#x00B1;</mo><msqrt><mrow><msup><mi>b</mi><mn>2</mn></msup>
<mo>&#x2212;</mo><mn>4</mn><mi>a</mi><mi>c</mi></mrow></msqrt></mrow><mrow>
<mn>2</mn><mi>a</mi></mrow></mfrac></mrow></math>"""),
			// Cauchy's Integral Formula
			conversion(
					"f ( a ) = ((1) / (2 π i))φ_{γ}((f ( z )) / (z − a))d z",
					"<math display='block'> <mstyle> <mi>f</mi> \n"
							+ "<mrow> <mo>(</mo> <mi>a</mi> <mo>)</mo> </mrow> <mo>=</mo> <mfrac> \n"
							+ "<mn>1</mn> <mrow> <mn>2</mn> <mi>\n"
							+ Unicode.PI_STRING + "<!-- " + Unicode.PI_STRING
							+ " --></mi> <mi>i</mi> </mrow> </mfrac> <msub>" + " <mo>\n"
							+ Unicode.phi + "</mo> <mrow> <mi>" + Unicode.gamma
							+ "</mi> </mrow> </msub> <mfrac> <mrow> <mi>f</mi> \n"
							+ "<mo>(</mo> <mi>z</mi> <mo>)</mo>\n"
							+ " </mrow> <mrow> <mi>z</mi> <mo>" + Unicode.MINUS
							+ "</mo> <mi>a</mi> </mrow> </mfrac> <mi>d</mi> <mi>z</mi> </mstyle></math>"),
			// Double angle formula for Cosines
			conversion("cos  ( θ + φ ) = cos  ( θ ) cos  ( φ ) - sin  ( θ ) sin  ( φ )", """
<math display='block'><mrow><mi>cos</mi><mo>&#x2061;</mo><mrow>
<mo>(</mo><mi>&#x03b8;</mi><mo>+</mo><mi>&#x03c6;</mi><mo>)</mo></mrow>
<mo>=</mo><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi>
<mo>)</mo></mrow><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo>
<mi>&#x03c6;</mi><mo>)</mo></mrow><mo>&#x2212;</mo><mi>sin</mi>
<mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>)</mo></mrow>
<mi>sin</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03c6;</mi>
<mo>)</mo></mrow></mrow></math>"""),
			// Gauss' Divergence Theorem
			conversion("\\#_{D}( ∇⋅ F ) d V = \\#_{∂ D}  F ⋅ n d S", """
<math display='block'><mrow><mrow><msub><mo>&#x222b;</mo><mrow>
<mi>D</mi></mrow></msub><mrow><mo>(</mo><mo>&#x2207;&#x22c5;</mo>
<mi>F</mi><mo>)</mo></mrow><mi>d</mi><mrow><mi>V</mi></mrow></mrow>
<mo>=</mo><mrow><msub><mo>&#x222b;</mo><mrow><mo>&#x2202;</mo>
<mi>D</mi></mrow></msub><mrow><mtext>&#x2009;</mtext><mi>F</mi>
<mo>&#x22c5;</mo><mi>n</mi></mrow><mi>d</mi><mi>S</mi></mrow></mrow></math>"""),
			// Curl of a Vector Field
			conversion(
					"∇ \\# × F \\# = ( ((∂ F_{z}) / (∂ y))- ((∂ F_{y}) / (∂ z))) i + ( ((∂ F_{x}) / (∂ z))- ((∂ F_{z}) / (∂ x))) j + ( ((∂ F_{y}) / (∂ x))- ((∂ F_{x}) / (∂ y))) k",
					"""
<math display='block'><mrow><mover accent='true'><mrow><mo>&#x2207;</mo></mrow>
<mrow><mo>&#x2192;</mo></mrow></mover><mo>&#x00d7;</mo><mover accent='true'>
<mrow><mi>F</mi></mrow><mrow><mo>&#x2192;</mo></mrow></mover><mo>=</mo>
<mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow>
<mrow><mi>z</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo>
<mi>y</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo>
<msub><mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow>
<mo>&#x2202;</mo><mi>z</mi></mrow></mfrac><mo>)</mo></mrow>
<mstyle mathvariant='bold' mathsize='normal'><mrow>
<mi>i</mi></mrow></mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow>
<mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow>
<mi>x</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo>
<mi>z</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo>
<msub><mrow><mi>F</mi></mrow><mrow><mi>z</mi></mrow></msub></mrow><mrow>
<mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>)</mo></mrow>
<mstyle mathvariant='bold' mathsize='normal'><mrow><mi>j</mi></mrow>
</mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub>
<mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow>
<mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow>
<mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>x</mi></mrow>
</msub></mrow><mrow><mo>&#x2202;</mo><mi>y</mi></mrow></mfrac><mo>)</mo>
</mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>k</mi>
</mrow></mstyle></mrow></math>"""),
			// Standard Deviation
			conversion("σ = sqrt(((1) / (N))ggbMathmlSum(i = 1,N,((( x_{i}- μ ))^(2))))).", """
<math display='block'><mrow><mi>&#x03c3;</mi><mo>=</mo><msqrt><mrow><mfrac><mrow>
<mn>1</mn></mrow><mrow><mi>N</mi></mrow></mfrac>
<mstyle displaystyle='true'><mrow><munderover><mrow><mo>&#x2211;</mo>
</mrow><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mrow><mi>N</mi>
</mrow></munderover><mrow><msup><mrow><mo stretchy='false'>(</mo><msub>
<mrow><mi>x</mi></mrow><mrow><mi>i</mi></mrow></msub><mo>&#x2212;</mo>
<mi>&#x03bc;</mi><mo stretchy='false'>)</mo></mrow><mrow>
<mn>2</mn></mrow></msup></mrow></mrow></mstyle></mrow></msqrt>
<mo>.</mo></mrow></math>"""),
			// Definition of Christoffel Symbols
			conversion(
					"((( ∇_{X}Y ))^(k))= ((X)^(i))((( ∇_{i}Y ))^(k))= ((X)^(i))( ((∂ ((Y)^(k))) / (∂ ((x)^(i))))+ (Γ_{i m})^(k)((Y)^(m)))",
					"""
<math display='block'><mrow><msup><mrow><mo>(</mo><msub><mrow>
<mo>&#x2207;</mo></mrow><mrow><mi>X</mi></mrow></msub><mi>Y</mi>
<mo>)</mo></mrow><mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow>
<mi>X</mi></mrow><mrow><mi>i</mi></mrow></msup><msup><mrow>
<mo stretchy='false'>(</mo><msub><mrow><mo>&#x2207;</mo></mrow><mrow>
<mi>i</mi></mrow></msub><mi>Y</mi><mo stretchy='false'>)</mo></mrow>
<mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow><mi>X</mi></mrow>
<mrow><mi>i</mi></mrow></msup><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo>
<msup><mrow><mi>Y</mi></mrow><mrow><mi>k</mi></mrow></msup></mrow><mrow>
<mo>&#x2202;</mo><msup><mrow><mi>x</mi></mrow><mrow>
<mi>i</mi></mrow></msup></mrow></mfrac><mo>+</mo><msubsup><mrow>
<mi>&#x0393;</mi></mrow><mrow><mi>i</mi><mi>m</mi></mrow><mrow>
<mi>k</mi></mrow></msubsup><msup><mrow><mi>Y</mi></mrow><mrow>
<mi>m</mi></mrow></msup><mo>)</mo></mrow></mrow></math>"""),

			// a few tests from
			// https://eyeasme.com/Joe/MathML/MathML_browser_test
			// Axiom of power set
			conversion("\\# A \\# P \\# B B \\# P \\# \\# C (C \\# B \\# C \\# A)", """
<math display="block"> <mrow> <mo rspace="0">&forall;</mo> <mi>A</mi>
<mo lspace="mediummathspace" rspace="0">&exist;</mo> <mi>P</mi>
<mo lspace="mediummathspace" rspace="0">&forall;</mo> <mi>B</mi>
<mspace width="thinmathspace" /> <mfenced open="[" close="]">
<mrow> <mi>B</mi> <mo>&isin;</mo> <mi>P</mi>
<mo lspace="veryverythickmathspace" rspace="veryverythickmathspace">
&Longleftrightarrow;</mo>
<mo rspace="0">&forall;</mo> <mi>C</mi> <mspace width="thinmathspace" />
<mfenced> <mrow> <mi>C</mi>
<mo>&isin;</mo> <mi>B</mi> <mo>&Implies;</mo> <mi>C</mi> <mo>&isin;</mo>
<mi>A</mi> </mrow>
</mfenced> </mrow> </mfenced> </mrow> </math>"""),
			// quadratic formula
			conversion("x = ((- b ± sqrt(((b)^(2))- 4  a  c)) / (2  a))", """
<math display="block"> <mrow> <mi>x</mi> <mo>=</mo> <mfrac> <mrow>
<mo form="prefix">&minus;</mo> <mi>b</mi> <mo>&PlusMinus;</mo>
<msqrt> <msup> <mi>b</mi> <mn>2</mn> </msup> <mo>&minus;</mo> <mn>4</mn>
<mo>&InvisibleTimes;</mo> <mi>a</mi> <mo>&InvisibleTimes;</mo>
<mi>c</mi> </msqrt> </mrow> <mrow> <mn>2</mn> <mo>&InvisibleTimes;</mo>
<mi>a</mi> </mrow> </mfrac> </mrow> </math>"""),
			// Binomial coefficient
			conversion("C (n)= (C_{k})^(n)= C k n = (n k)= ((n !) / (k !  (n - k)!))", """
<math display="block"> <mrow> <mi>C</mi> <mfenced> <mi>n</mi> <mi>k</mi> </mfenced>
<mo>=</mo> <msubsup> <mi>C</mi> <mi>k</mi> <mi>n</mi> </msubsup> <mo>=</mo>
<mmultiscripts> <mi>C</mi> <mi>k</mi> <none /> <mprescripts /> <mi>n</mi>
<none /> </mmultiscripts> <mo>=</mo> <mfenced> <mfrac linethickness="0">
<mi>n</mi> <mi>k</mi> </mfrac> </mfenced> <mo>=</mo> <mfrac> <mrow>
<mi>n</mi> <mo lspace="0">!</mo> </mrow> <mrow> <mi>k</mi>
<mo lspace="0">!</mo> <mo rspace="mediummathspace">&InvisibleTimes;</mo>
<mfenced> <mrow> <mi>n</mi> <mo>&minus;</mo> <mi>k</mi> </mrow> </mfenced>
<mo lspace="0">!</mo> </mrow> </mfrac> </mrow> </math>"""),
			// Sophomore's dream
			conversion("(\\#_{0})^(1)((x)^(x)) ⅆ x = \\#(n = 1,∞,(((- 1))^(n + 1))) ((n)^(- n))", """
<math display="block"> <mrow> <msubsup> <mo>&Integral;</mo> <mn>0</mn>
<mn>1</mn> </msubsup> <msup> <mi>x</mi> <mi>x</mi> </msup>
<mo rspace="mediummathspace">&InvisibleTimes;</mo>
<mo rspace="0">&DifferentialD;</mo> <mi>x</mi> <mo>=</mo> <munderover>
<mo>&Sum;</mo> <mrow> <mi>n</mi> <mo>=</mo> <mn>1</mn> </mrow>
<mn>&infin;</mn> </munderover> <msup> <mfenced> <mrow>
<mo form="prefix">&minus;</mo> <mn>1</mn> </mrow> </mfenced> <mrow>
<mi>n</mi> <mo>+</mo> <mn>1</mn> </mrow> </msup> <mo>&InvisibleTimes;</mo>
<msup> <mi>n</mi> <mrow> <mo form="prefix">&minus;</mo>
<mi>n</mi> </mrow> </msup> </mrow> </math>"""),
			// nested roots
			conversion(
					"((sqrt(1 + nroot(2 + nroot(3 + nroot(4 + nroot(5 + nroot(6 + nroot(7 + nroot(A,19),17),13),11),7),5),3))) / (((ⅇ)^(π))))= ((x)^(‴))",
					"""
<math style="font-size: 8pt" display="block"> <mrow> <mfrac> <msqrt> <mn>1</mn>
<mo>+</mo> <mroot> <mrow> <mn>2</mn> <mo>+</mo> <mroot> <mrow> <mn>3</mn>
<mo>+</mo> <mroot> <mrow> <mn>4</mn> <mo>+</mo> <mroot> <mrow> <mn>5</mn>
<mo>+</mo> <mroot> <mrow> <mn>6</mn> <mo>+</mo> <mroot> <mrow> <mn>7</mn>
<mo>+</mo> <mroot> <mi>A</mi> <mn>19</mn> </mroot> </mrow>
<mn>17</mn> </mroot> </mrow> <mn>13</mn> </mroot> </mrow> <mn>11</mn>
</mroot> </mrow> <mn>7</mn> </mroot> </mrow> <mn>5</mn> </mroot> </mrow>
<mn>3</mn> </mroot> </msqrt> <msup> <mi>&exponentiale;</mi> <mi>&pi;</mi>
</msup> </mfrac> <mo>=</mo> <msup> <mi>x</mi>
<mo style="font-size: larger;">&tprime;</mo> </msup> </mrow> </math>"""),

			// Some other tests - temporarily factorial
			conversion(
					"n !",
					"<math style=\"font-size: 8pt\" display=\"block\"><mrow><mi>n</mi><mo>!</mo></mrow> </math>"),
			// greek letters
			conversion(
					"α + β + γ",
					"<math style=\"font-size: 8pt\" display=\"block\"> <mrow><mi>&alpha;</mi>\n"
							+ "<mo>+</mo><mi>&beta;</mi><mo>+</mo><mi>&gamma;</mi></mrow> </math>"),
			conversion("sin (x)", """
<math xmlns="http://www.w3.org/1998/Math/MathML"><mstyle displaystyle="true">
<mi> sin </mi> <mfenced> <mrow>
<mi> x </mi> </mrow> </mfenced> </mstyle></math>"""),
			conversion("ggbMathmlIntegral(0,1,((x)^(2)))", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<munderover> <mrow> <mo> &#x222B;<!--integral--> </mo> </mrow> <mrow>
<mn> 0 </mn> </mrow> <mrow> <mn> 1 </mn> </mrow> </munderover> <msup>
<mrow> <mi> x </mi> </mrow> <mrow>
<mn> 2 </mn> </mrow> </msup> </mstyle></math>"""),
			conversion("ggbMathmlSum(k = 1,3,k )", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<munderover> <mrow> <mo> &#x2211;<!--n-ary summation--> </mo> </mrow>
<mrow> <mi> k </mi> <mo> = </mo> <mn> 1 </mn> </mrow> <mrow>
<mn> 3 </mn> </mrow> </munderover> <mi> k </mi> </mstyle></math>"""),
			conversion("(x_{1})^(2)", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<msubsup> <mrow> <mi> x </mi> </mrow> <mrow> <mn> 1 </mn> </mrow> <mrow>
<mn> 2 </mn> </mrow> </msubsup> </mstyle> </math>"""),
			conversion("sqrt(((x) / (x + 2)))", """
<math xmlns="http://www.w3.org/1998/Math/MathML"> <mstyle displaystyle="true">
<msqrt> <mfrac> <mrow> <mi> x </mi> </mrow> <mrow> <mi> x </mi>
<mo> + </mo> <mn> 2 </mn> </mrow> </mfrac> </msqrt> </mstyle> </math>
"""));

	private static List<String> conversion(String s, String t) {
		return List.of(s, t);
	}

	/**
	 * just for running test-cases
	 *
	 */
	@ParameterizedTest
	@FieldSource("testInputs")
	void testGeoGebra(List<String> s) {
		MathMLParser mathmlParser = new MathMLParser(true);
		String latex = mathmlParser.parse(s.get(1), false, false).strip();
		assertEquals(s.get(0), latex);
	}

	@Test
	void testLaTeX() {

		MathMLParser mathmlParser = new MathMLParser(false);

		for (List<String> s : testInputs) {
			String latex = mathmlParser.parse(s.get(1), false, false);

			assertNotNull(latex);
		}
	}
}
