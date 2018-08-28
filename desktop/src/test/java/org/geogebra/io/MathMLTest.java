package org.geogebra.io;

import org.geogebra.common.io.MathMLParser;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class MathMLTest {
	private static String[] mathmlTest = {
			// quadratic formula
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\"> <mstyle displaystyle=\"true\"> <mfrac> <mrow> <mo> - </mo> <mi> b </mi> <mo> &PlusMinus; </mo> <msqrt> <msup> <mrow> <mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup> <mo> - </mo> <mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow> <mn> 2 </mn> <mi> a </mi> </mrow> </mfrac> </mstyle> </math>",
			// quadratic formula with comment
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\"> <mstyle displaystyle=\"true\"> <mfrac> <mrow> <mo> - </mo> <mi> b </mi> <mo> &#x00B1;<!--plus-minus sign--> </mo> <msqrt> <msup> <mrow> <mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup> <mo> - </mo> <mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow> <mn> 2 </mn> <mi> a </mi> </mrow> </mfrac> </mstyle> </math>",

			// MathJax tests http://www.mathjax.org/demos/mathml-samples/
			// quadratic formula
			"<math display='block'><mrow><mi>x</mi><mo>=</mo><mfrac><mrow><mo>&#x2212;</mo><mi>b</mi><mo>&#x00B1;</mo><msqrt><mrow><msup><mi>b</mi><mn>2</mn></msup><mo>&#x2212;</mo><mn>4</mn><mi>a</mi><mi>c</mi></mrow></msqrt></mrow><mrow><mn>2</mn><mi>a</mi></mrow></mfrac></mrow></math>",
			// Cauchy's Integral Formula
			"<math display='block'> <mstyle> <mi>f</mi> <mrow> <mo>(</mo> <mi>a</mi> <mo>)</mo> </mrow> <mo>=</mo> <mfrac> <mn>1</mn> <mrow> <mn>2</mn> <mi>"
					+ Unicode.PI_STRING + "<!-- " + Unicode.PI_STRING
					+ " --></mi> <mi>i</mi> </mrow> </mfrac> <msub>" + " <mo>"
					+ Unicode.phi + "</mo> <mrow> <mi>" + Unicode.gamma
					+ "</mi> </mrow> </msub> <mfrac> <mrow> <mi>f</mi> <mo>(</mo> <mi>z</mi> <mo>)</mo>"
					+ " </mrow> <mrow> <mi>z</mi> <mo>" + Unicode.MINUS
					+ "</mo> <mi>a</mi> </mrow> </mfrac> <mi>d</mi> <mi>z</mi> </mstyle></math>",
			// Double angle formula for Cosines
			"<math display='block'><mrow><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>+</mo><mi>&#x03c6;</mi><mo>)</mo></mrow><mo>=</mo><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>)</mo></mrow><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03c6;</mi><mo>)</mo></mrow><mo>&#x2212;</mo><mi>sin</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>)</mo></mrow><mi>sin</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03c6;</mi><mo>)</mo></mrow></mrow></math>",
			// Gauss' Divergence Theorem
			"<math display='block'><mrow><mrow><msub><mo>&#x222b;</mo><mrow><mi>D</mi></mrow></msub><mrow><mo>(</mo><mo>&#x2207;&#x22c5;</mo><mi>F</mi><mo>)</mo></mrow><mi>d</mi><mrow><mi>V</mi></mrow></mrow><mo>=</mo><mrow><msub><mo>&#x222b;</mo><mrow><mo>&#x2202;</mo><mi>D</mi></mrow></msub><mrow><mtext>&#x2009;</mtext><mi>F</mi><mo>&#x22c5;</mo><mi>n</mi></mrow><mi>d</mi><mi>S</mi></mrow></mrow></math>",
			// Curl of a Vector Field
			"<math display='block'><mrow><mover accent='true'><mrow><mo>&#x2207;</mo></mrow><mrow><mo>&#x2192;</mo></mrow></mover><mo>&#x00d7;</mo><mover accent='true'><mrow><mi>F</mi></mrow><mrow><mo>&#x2192;</mo></mrow></mover><mo>=</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>z</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>y</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>z</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>i</mi></mrow></mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>x</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>z</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>z</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>j</mi></mrow></mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>x</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>y</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>k</mi></mrow></mstyle></mrow></math>",
			// Standard Deviation
			"<math display='block'><mrow><mi>&#x03c3;</mi><mo>=</mo><msqrt><mrow><mfrac><mrow><mn>1</mn></mrow><mrow><mi>N</mi></mrow></mfrac><mstyle displaystyle='true'><mrow><munderover><mrow><mo>&#x2211;</mo></mrow><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mrow><mi>N</mi></mrow></munderover><mrow><msup><mrow><mo stretchy='false'>(</mo><msub><mrow><mi>x</mi></mrow><mrow><mi>i</mi></mrow></msub><mo>&#x2212;</mo><mi>&#x03bc;</mi><mo stretchy='false'>)</mo></mrow><mrow><mn>2</mn></mrow></msup></mrow></mrow></mstyle></mrow></msqrt><mo>.</mo></mrow></math>",
			// Definition of Christoffel Symbols
			"<math display='block'><mrow><msup><mrow><mo>(</mo><msub><mrow><mo>&#x2207;</mo></mrow><mrow><mi>X</mi></mrow></msub><mi>Y</mi><mo>)</mo></mrow><mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow><mi>X</mi></mrow><mrow><mi>i</mi></mrow></msup><msup><mrow><mo stretchy='false'>(</mo><msub><mrow><mo>&#x2207;</mo></mrow><mrow><mi>i</mi></mrow></msub><mi>Y</mi><mo stretchy='false'>)</mo></mrow><mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow><mi>X</mi></mrow><mrow><mi>i</mi></mrow></msup><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msup><mrow><mi>Y</mi></mrow><mrow><mi>k</mi></mrow></msup></mrow><mrow><mo>&#x2202;</mo><msup><mrow><mi>x</mi></mrow><mrow><mi>i</mi></mrow></msup></mrow></mfrac><mo>+</mo><msubsup><mrow><mi>&#x0393;</mi></mrow><mrow><mi>i</mi><mi>m</mi></mrow><mrow><mi>k</mi></mrow></msubsup><msup><mrow><mi>Y</mi></mrow><mrow><mi>m</mi></mrow></msup><mo>)</mo></mrow></mrow></math>",

			// a few tests from
			// https://eyeasme.com/Joe/MathML/MathML_browser_test
			// Axiom of power set
			"<math display=\"block\"> <mrow> <mo rspace=\"0\">&forall;</mo> <mi>A</mi> "
					+ "<mo lspace=\"mediummathspace\" rspace=\"0\">&exist;</mo> <mi>P</mi>"
					+ " <mo lspace=\"mediummathspace\" rspace=\"0\">&forall;</mo> <mi>B</mi>"
					+ " <mspace width=\"thinmathspace\" /> <mfenced open=\"[\" close=\"]\"> "
					+ "<mrow> <mi>B</mi> <mo>&isin;</mo> <mi>P</mi> "
					+ "<mo lspace=\"veryverythickmathspace\" rspace=\"veryverythickmathspace\">&Longleftrightarrow;</mo> "
					+ "<mo rspace=\"0\">&forall;</mo> <mi>C</mi> <mspace width=\"thinmathspace\" /> <mfenced> <mrow> <mi>C</mi>"
					+ " <mo>&isin;</mo> <mi>B</mi> <mo>&Implies;</mo> <mi>C</mi> <mo>&isin;</mo> <mi>A</mi> </mrow> "
					+ "</mfenced> </mrow> </mfenced> </mrow> </math>",
			// quadratic formula
			"<math display=\"block\"> <mrow> <mi>x</mi> <mo>=</mo> <mfrac> <mrow> <mo form=\"prefix\">&minus;</mo> <mi>b</mi> <mo>&PlusMinus;</mo> <msqrt> <msup> <mi>b</mi> <mn>2</mn> </msup> <mo>&minus;</mo> <mn>4</mn> <mo>&InvisibleTimes;</mo> <mi>a</mi> <mo>&InvisibleTimes;</mo> <mi>c</mi> </msqrt> </mrow> <mrow> <mn>2</mn> <mo>&InvisibleTimes;</mo> <mi>a</mi> </mrow> </mfrac> </mrow> </math>",
			// Binomial coefficient
			"<math display=\"block\"> <mrow> <mi>C</mi> <mfenced> <mi>n</mi> <mi>k</mi> </mfenced> <mo>=</mo> <msubsup> <mi>C</mi> <mi>k</mi> <mi>n</mi> </msubsup> <mo>=</mo> <mmultiscripts> <mi>C</mi> <mi>k</mi> <none /> <mprescripts /> <mi>n</mi> <none /> </mmultiscripts> <mo>=</mo> <mfenced> <mfrac linethickness=\"0\"> <mi>n</mi> <mi>k</mi> </mfrac> </mfenced> <mo>=</mo> <mfrac> <mrow> <mi>n</mi> <mo lspace=\"0\">!</mo> </mrow> <mrow> <mi>k</mi> <mo lspace=\"0\">!</mo> <mo rspace=\"mediummathspace\">&InvisibleTimes;</mo> <mfenced> <mrow> <mi>n</mi> <mo>&minus;</mo> <mi>k</mi> </mrow> </mfenced> <mo lspace=\"0\">!</mo> </mrow> </mfrac> </mrow> </math>",
			// Sophomore's dream
			"<math display=\"block\"> <mrow> <msubsup> <mo>&Integral;</mo> <mn>0</mn> <mn>1</mn> </msubsup> <msup> <mi>x</mi> <mi>x</mi> </msup> <mo rspace=\"mediummathspace\">&InvisibleTimes;</mo> <mo rspace=\"0\">&DifferentialD;</mo> <mi>x</mi> <mo>=</mo> <munderover> <mo>&Sum;</mo> <mrow> <mi>n</mi> <mo>=</mo> <mn>1</mn> </mrow> <mn>&infin;</mn> </munderover> <msup> <mfenced> <mrow> <mo form=\"prefix\">&minus;</mo> <mn>1</mn> </mrow> </mfenced> <mrow> <mi>n</mi> <mo>+</mo> <mn>1</mn> </mrow> </msup> <mo>&InvisibleTimes;</mo> <msup> <mi>n</mi> <mrow> <mo form=\"prefix\">&minus;</mo> <mi>n</mi> </mrow> </msup> </mrow> </math>",
			// nested roots
			"<math style=\"font-size: 8pt\" display=\"block\"> <mrow> <mfrac> <msqrt> <mn>1</mn> <mo>+</mo> <mroot> <mrow> <mn>2</mn> <mo>+</mo> <mroot> <mrow> <mn>3</mn> <mo>+</mo> <mroot> <mrow> <mn>4</mn> <mo>+</mo> <mroot> <mrow> <mn>5</mn> <mo>+</mo> <mroot> <mrow> <mn>6</mn> <mo>+</mo> <mroot> <mrow> <mn>7</mn> <mo>+</mo> <mroot> <mi>A</mi> <mn>19</mn> </mroot> </mrow> <mn>17</mn> </mroot> </mrow> <mn>13</mn> </mroot> </mrow> <mn>11</mn> </mroot> </mrow> <mn>7</mn> </mroot> </mrow> <mn>5</mn> </mroot> </mrow> <mn>3</mn> </mroot> </msqrt> <msup> <mi>&exponentiale;</mi> <mi>&pi;</mi> </msup> </mfrac> <mo>=</mo> <msup> <mi>x</mi> <mo style=\"font-size: larger;\">&tprime;</mo> </msup> </mrow> </math>",

			// Some other tests - temporarily
			// factorial
			"<math style=\"font-size: 8pt\" display=\"block\"> <mrow><mi>n</mi><mo>!</mo></mrow> </math>",
			// greek letters
			"<math style=\"font-size: 8pt\" display=\"block\"> <mrow><mi>&alpha;</mi><mo>+</mo><mi>&beta;</mi><mo>+</mo><mi>&gamma;</mi></mrow> </math>",

			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mstyle displaystyle=\"true\"><mi> sin </mi>  <mfenced>  <mrow>        <mi> x </mi>      </mrow>    </mfenced>  </mstyle></math>",
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">  <mstyle displaystyle=\"true\">    <munderover>      <mrow>        <mo> &#x222B;<!--integral--> </mo>      </mrow>      <mrow>        <mn> 0 </mn>      </mrow>      <mrow>        <mn> 1 </mn>      </mrow>    </munderover>    <msup>      <mrow>        <mi> x </mi>      </mrow>      <mrow>        <mn> 2 </mn>      </mrow>    </msup>  </mstyle></math>",
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">  <mstyle displaystyle=\"true\">    <munderover>      <mrow>        <mo> &#x2211;<!--n-ary summation--> </mo>      </mrow>      <mrow>        <mi> k </mi>        <mo> = </mo>        <mn> 1 </mn>      </mrow>      <mrow>        <mn> 3 </mn>      </mrow>    </munderover>    <mi> k </mi>  </mstyle></math>",
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">   <mstyle displaystyle=\"true\">     <msubsup>       <mrow>         <mi> x </mi>       </mrow>       <mrow>         <mn> 1 </mn>       </mrow>       <mrow>         <mn> 2 </mn>       </mrow>     </msubsup>   </mstyle> </math>",
			"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">   <mstyle displaystyle=\"true\">     <msqrt>       <mfrac>         <mrow>           <mi> x </mi>         </mrow>         <mrow>           <mi> x </mi>           <mo> + </mo>           <mn> 2 </mn>         </mrow>       </mfrac>     </msqrt>   </mstyle> </math>" };

	/**
	 * just for running test-cases
	 * 
	 */
	@Test
	public void testGeoGebra() {

		MathMLParser mathmlParser = new MathMLParser(true);

		for (int i = 0; i < mathmlTest.length; i++) {
			String s = mathmlTest[i];

			String latex = mathmlParser.parse(s, false, false);

			System.out.println(latex);
		}
	}

	@Test
	public void testLaTeX() {

		MathMLParser mathmlParser = new MathMLParser(false);

		for (int i = 0; i < mathmlTest.length; i++) {
			String s = mathmlTest[i];

			String latex = mathmlParser.parse(s, false, false);

			System.out.println(latex);
		}
	}
}
