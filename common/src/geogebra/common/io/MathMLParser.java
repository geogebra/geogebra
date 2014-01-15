/*
 * Created on 2003-11-02
 */
package geogebra.common.io;

import java.util.HashMap;

/**
 * Apache 2.0 Licence
 * 
 * Original from http://www.tilman.de/programme/mathparser/download_en.html
 * 
 * Alterations by GeoGebra
 * 
 * @author (c) Tilman Walther
 * @author (c) Martin Wilke
 */
public class MathMLParser {

	private static HashMap<String, String> latexMap = new HashMap<String, String>();

	/*
	 * ** Links stehen zu findende Ausdrücke, rechts (getrennt durch einen oder mehrere
	 ** Tabs) die entsprechende Ersetzung.
	 **
	 ** Zeilen, die mit '**' beginnen (wie diese Erklärung) werden ignoriert, Zeilen
	 ** ohne Tabulator oder mit Tabulatoren an verschiedenen Stellen im String ebenfalls.
	 **
	 ** Da sich die Reihenfolge der Blöcke im MathML-Code von dem im LaTeX-Code
	 ** unterscheidet, muss MathParser die korrekte Reihenfolge für die Blöcke mitgeteilt
	 ** werden. Hierfür wird das Schlüsselwort %BLOCK[Blocknummer]% verwendet.
	 ** Sollen sämtliche Blöcke (unabhängig von Reihenfolge und Anzahl) übernommen
	 ** werden, wird das Schlüsselwort %BLOCKS% verwendet
	 **
	 ** Wird %BLOCK in einem Ersetzungsbefehl gefunden, wird der Parser rekursiv auf dem
	 ** folgenden Block aufgerufen und das Ergebnis an Stelle des Platzhalters in die
	 ** Ausgabe geschrieben.
	 **
	 */
	static {

		// Tags:
		latexMap.put("<mfrac>", "\\frac{%BLOCK1%}{%BLOCK2%}");
		latexMap.put("<msup>", "%BLOCK1%^{%BLOCK2%}");
		latexMap.put("<msub>", "%BLOCK1%_{%BLOCK2%}");
		latexMap.put("<msqrt>", "\\sqrt{%BLOCK1%}");
		latexMap.put("<mroot>", "\\sqrt[%BLOCK2%]{%BLOCK1%}");
		latexMap.put("<mfenced>", "\\left(%BLOCK1%\right)");
		latexMap.put("<msubsup>", "%BLOCK1%_{%BLOCK2%}^{%BLOCK3%}");
		latexMap.put("<munderover>", "%BLOCK1%_{%BLOCK2%}^{%BLOCK3%}");
		latexMap.put("<munder>", "%BLOCK1%_{%BLOCK2%}");
		latexMap.put("<mtable>", "\\matrix{%BLOCKS%}");
		latexMap.put("<mtr>", "%BLOCKS%\\cr");
		latexMap.put("<mtd>", "%BLOCK1%&");


		// Entities
		latexMap.put("&dot;", "\\cdot ");
		latexMap.put("&sdot;", "\\cdot ");
		latexMap.put("&middot;", "\\cdot ");
		latexMap.put("&times;", "\\times ");
		latexMap.put("&equals;", "\\Relbar ");
		latexMap.put("&forall;", "\\forall ");
		latexMap.put("&exist;", "\\exists ");
		latexMap.put("&%x220d;", "\\ni ");
		latexMap.put("&lowast;", "* ");
		latexMap.put("&minus;", "- ");
		latexMap.put("&frasl;", "/ ");
		latexMap.put("&ratio;", ": ");
		latexMap.put("&lt;", "< ");
		latexMap.put("&gt;", "> ");
		latexMap.put("&cong;", "\\cong ");
		latexMap.put("&InvisibleTimes;", " ");


		// Pfeile
		latexMap.put("&harr;", "\\leftrightarrow ");
		latexMap.put("&larr;", "\\leftarrow ");
		latexMap.put("&rarr;", "\\rightarrow ");
		latexMap.put("&hArr;", "\\Leftrightarrow ");
		latexMap.put("&lArr;", "\\Leftarrow ");
		latexMap.put("&rArr;", "\\Rightarrow ");

		// dynamische Zeichen
		latexMap.put("&sum;", "\\sum ");
		latexMap.put("&prod;", "\\prod ");
		latexMap.put("&Integral;", "\\int ");
		latexMap.put("&dd;", "d ");


		// griechisches Alphabet
		latexMap.put("&alpha;", "\\alpha");
		latexMap.put("&beta;", "\\beta");
		latexMap.put("&gamma;", "\\gamma ");
		latexMap.put("&delta;", "\\delta ");
		latexMap.put("&epsi;", "\\epsilon ");
		latexMap.put("&eta;", "\\eta ");
		latexMap.put("&iota;", "\\iota ");
		latexMap.put("&kappa;", "\\kappa ");
		latexMap.put("&lambda;", "\\lambda ");
		latexMap.put("&mu;", "\\mu ");
		latexMap.put("&mgr;", "\\mu ");
		latexMap.put("&nu;", "\\nu ");
		latexMap.put("&omicron;", "o ");
		latexMap.put("&pi;", "\\pi ");
		latexMap.put("&theta;", "\\theta ");
		latexMap.put("&rho;", "\\rho ");
		latexMap.put("&rgr;", "\\rho ");
		latexMap.put("&sigma;", "\\sigma ");
		latexMap.put("&tau;", "\\tau ");
		latexMap.put("&upsilon;", "\\upsilon ");
		latexMap.put("&phiv;", "\\phi");
		latexMap.put("&phi;", "\\varphi");
		latexMap.put("&chi;", "\\chi ");
		latexMap.put("&piv;", "\\varpi ");
		latexMap.put("&pgr;", "\\pi ");
		latexMap.put("&ohgr;", "\\omega ");
		latexMap.put("&omega;", "\\omega ");
		latexMap.put("&xi;", "\\xi ");
		latexMap.put("&psi;", "\\psi ");
		latexMap.put("&zeta;", "\\zeta ");
		latexMap.put("&Delta;", "\\Delta ");
		latexMap.put("&Phi;", "\\Phi ");
		latexMap.put("&Gamma;", "\\Gamma ");
		latexMap.put("&Lambda;", "\\Lambda ");
		latexMap.put("&Pi;", "\\Pi ");
		latexMap.put("&tgr;", "\\tau ");
		latexMap.put("&Theta;", "\\Theta ");
		latexMap.put("&Sigma;", "\\Sigma ");
		latexMap.put("&Upsilon;", "\\Upsilon ");
		latexMap.put("&sigmaf;", "\\varsigma ");
		latexMap.put("&Omega;", "\\Omega ");
		latexMap.put("&Xi;", "\\Xi ");
		latexMap.put("&Psi;", "\\Psi ");
		latexMap.put("&epsiv;", "\\epsilon ");
		latexMap.put("&phgr;", "\\phi ");
		latexMap.put("&ggr;", "\\gamma ");
		latexMap.put("&eegr;", "\\eta ");
		latexMap.put("&igr;", "\\iota ");
		latexMap.put("&phgr;", "\\phi ");
		latexMap.put("&kgr;", "\\kappa ");
		latexMap.put("&lgr;", "\\lambda ");
		latexMap.put("&ngr;", "\\nu ");
		latexMap.put("&ogr;", "o ");
		latexMap.put("&thgr;", "\\theta ");
		latexMap.put("&sgr;", "\\sigma ");
		latexMap.put("&ugr;", "\\upsilon ");
		latexMap.put("&zgr;", "\\zeta ");
		latexMap.put("&Agr;", "A ");
		latexMap.put("&Bgr;", "B ");
		latexMap.put("&KHgr;", "X ");
		latexMap.put("&Egr;", "E ");
		latexMap.put("&PHgr;", "\\Phi ");
		latexMap.put("&Ggr;", "\\Gamma ");
		latexMap.put("&EEgr;", "H ");
		latexMap.put("&Igr;", "I ");
		latexMap.put("&THgr;", "\\Theta ");
		latexMap.put("&Kgr;", "K ");
		latexMap.put("&Lgr;", "\\Lambda ");
		latexMap.put("&Mgr;", "M ");
		latexMap.put("&Ngr;", "N ");
		latexMap.put("&Ogr;", "O ");
		latexMap.put("&Pgr;", "\\Pi ");
		latexMap.put("&Rgr;", "P ");
		latexMap.put("&Sgr;", "\\Sigma ");
		latexMap.put("&Tgr;", "T ");
		latexMap.put("&Ugr;", "\\Upsilon ");
		latexMap.put("&OHgr;", "\\Omega ");
		latexMap.put("&Zgr;", "Z ");


		// Pfeile und andere Operatoren
		latexMap.put("&#x2212;", "-");
		latexMap.put("&perp;", "\\bot ");
		latexMap.put("&sim;", "~ ");
		latexMap.put("&prime;", "\\prime ");
		latexMap.put("&le;", "\\le ");
		latexMap.put("&ge;", "\\ge ");
		latexMap.put("&infin;", "\\infty ");
		latexMap.put("&clubs;", "\\clubsuit ");
		latexMap.put("&diams;", "\\diamondsuit ");
		latexMap.put("&hearts;", "\\heartsuit ");
		latexMap.put("&spades;", "\\spadesuit ");
		latexMap.put("&PlusMinus;", "\\pm ");
		latexMap.put("&Prime;", "\\prime\\prime ");
		latexMap.put("&prop;", "\\propto ");
		latexMap.put("&part;", "\\partial ");
		latexMap.put("&bull;", "\\bullet ");
		latexMap.put("&ne;", "\\neq ");
		latexMap.put("&equiv;", "\\equiv ");
		latexMap.put("&asymp;", "\\approx ");
		latexMap.put("&hellip;", "... ");
		latexMap.put("&VerticalBar;", "\\mid ");
		latexMap.put("&crarr;", "\\P ");
		latexMap.put("&alefsym;", "\\aleph ");
		latexMap.put("&image;", "\\Im ");
		latexMap.put("&real;", "\\Re ");
		latexMap.put("&weierp;", "\\wp ");
		latexMap.put("&otimes;", "\\otimes ");
		latexMap.put("&oplus;", "\\oplus ");
		latexMap.put("&empty;", "\\emtyset ");
		latexMap.put("&cap;", "\\cap ");
		latexMap.put("&cup;", "\\cup ");
		latexMap.put("&sup;", "\\supset ");
		latexMap.put("&supe;", "\\seupseteq ");
		latexMap.put("&nsub;", "\\not\\subset ");
		latexMap.put("&sub;", "\\subset ");
		latexMap.put("&sube;", "\\subseteq ");
		latexMap.put("&isin;", "\\in ");
		latexMap.put("&notin;", "\\notin ");
		latexMap.put("&ang;", "\\angle ");
		latexMap.put("&nabla;", "\\nabla ");
		latexMap.put("&radic;", "\\surd ");
		latexMap.put("&and;", "\\wedge ");
		latexMap.put("&or;", "\\vee ");
		latexMap.put("&and;", "\\wedge ");
		latexMap.put("&ang;", "\\angle ");
		latexMap.put("&angle;", "\\angle ");
		latexMap.put("&ap;", "\\approx ");
		latexMap.put("&approx;", "\\approx ");
		latexMap.put("&bigoplus;", "\\oplus ");
		latexMap.put("&bigotimes;", "\\otimes ");
		latexMap.put("&bot;", "\\bot ");
		latexMap.put("&bottom;", "\\bot ");
		latexMap.put("&cap;", "\\cap ");
		latexMap.put("&CirclePlus;", "\\oplus ");
		latexMap.put("&CircleTimes;", "\\otimes ");
		latexMap.put("&cong;", "\\cong ");
		latexMap.put("&Congruent;", "\\equiv ");
		latexMap.put("&cup;", "\\cup ");
		latexMap.put("&darr;", "\\downarrow ");
		latexMap.put("&dArr;", "\\Downarrow ");
		latexMap.put("&Del;", "\\nabla ");
		latexMap.put("&DoubleDownArrow;", "\\Downarrow ");
		latexMap.put("&DoubleLeftArrow;", "\\Leftarrow ");
		latexMap.put("&DoubleLeftRightArrow;", "\\Leftrightarrow ");
		latexMap.put("&DoubleRightArrow;", "\\Rightarrow ");
		latexMap.put("&DoubleUpArrow;", "\\Uparrow ");
		latexMap.put("&downarrow;", "\\downarrow ");
		latexMap.put("&Downarrow;", "\\Downarrow ");
		latexMap.put("&DownArrow;", "\\Downarrow ");
		latexMap.put("&Element;", "\\in ");
		latexMap.put("&emptyv;", "\\oslash ");
		latexMap.put("&equiv;", "\\equiv ");
		latexMap.put("&exist;", "\\exists ");
		latexMap.put("&Exist;", "\\exists ");
		latexMap.put("&forall;", "\\forall ");
		latexMap.put("&ForAll;", "\\forall ");
		latexMap.put("&ge;", "\\geq ");
		latexMap.put("&geq;", "\\geq ");
		latexMap.put("&GreaterEqual;", "\\geq ");
		latexMap.put("&harr;", "\\leftrightarrow ");
		latexMap.put("&hArr;", "\\Leftrightarrow ");
		latexMap.put("&iff;", "\\Leftrightarrow ");
		latexMap.put("&Implies;", "\\Rightarrow ");
		latexMap.put("&in;", "\\in ");
		latexMap.put("&infin;", "\\infty ");
		latexMap.put("&int;", "\\int ");
		latexMap.put("&Integral;", "\\int ");
		latexMap.put("&isin;", "\\in ");
		latexMap.put("&isinv;", "\\in ");
		latexMap.put("&diam;", "\\diamond ");
		latexMap.put("&diamond;", "\\diamond ");
		latexMap.put("&lang;", "\\left\\langle ");
		latexMap.put("&langle;", "\\left\\langle ");
		latexMap.put("&larr;", "\\leftarrow ");
		latexMap.put("&lArr;", "\\Leftarrow ");
		latexMap.put("&le;", "\\leq ");
		latexMap.put("&LeftAngleBracket;", "\\left\\langle ");
		latexMap.put("&Leftarrow;", "\\Leftarrow ");
		latexMap.put("&LeftArrow;", "\\leftarrow ");
		latexMap.put("&leftrightarrow;", "\\leftrightarrow ");
		latexMap.put("&Leftrightarrow;", "\\Leftrightarrow ");
		latexMap.put("&LeftRightArrow;", "\\leftrightarrow ");
		latexMap.put("&leq;", "\\leq ");
		latexMap.put("&lowast;", "\\ast ");
		latexMap.put("&minus;", "- ");
		latexMap.put("&nabla;", "\\nabla ");
		latexMap.put("&ne;", "\\neq ");
		latexMap.put("&NotElement;", "\\notin ");
		latexMap.put("&NotEqual;", "\\notin ");
		latexMap.put("&notin;", "\\notin ");
		latexMap.put("&oplus;", "\\oplus ");
		latexMap.put("&or;", "\\vee ");
		latexMap.put("&otimes;", "\\otimes ");
		latexMap.put("&part;", "\\partial ");
		latexMap.put("&partialD;", "\\partial ");
		latexMap.put("&perp;", "\\bot ");
		latexMap.put("&prod;", "\\Pi ");
		latexMap.put("&Product;", "\\Pi ");
		latexMap.put("&rang;", "\\right\\rangle ");
		latexMap.put("&rangle;", "\\right\\rangle ");
		latexMap.put("&rarr;", "\\rightarrow ");
		latexMap.put("&rArr;", "\\Rightarrow ");
		latexMap.put("&RightAngleBracket;", "\\right\\rangle ");
		latexMap.put("&rightarrow;", "\\rightarrow ");
		latexMap.put("&Rightarrow;", "\\Rightarrow ");
		latexMap.put("&RightArrow;", "\\rightarrow ");
		latexMap.put("&sdot;", "\\cdot ");
		latexMap.put("&sim;", "\\sim ");
		latexMap.put("&prop;", "\\propto ");
		latexMap.put("&Proportional;", "\\propto ");
		latexMap.put("&propto;", "\\propto ");
		latexMap.put("&sub;", "\\subset ");
		latexMap.put("&sube;", "\\subseteq ");
		latexMap.put("&subE;", "\\subseteq ");
		latexMap.put("&subset;", "\\subset ");
		latexMap.put("&subseteq;", "\\subseteq ");
		latexMap.put("&subseteqq;", "\\subseteq ");
		latexMap.put("&SubsetEqual;", "\\subseteq ");
		latexMap.put("&sum;", "\\Sigma ");
		latexMap.put("&Sum;", "\\Sigma ");
		latexMap.put("&sup;", "\\supset ");
		latexMap.put("&supe;", "\\supseteq ");
		latexMap.put("&supE;", "\\supseteq ");
		latexMap.put("&Superset;", "\\supset");
		latexMap.put("&SupersetEqual;", "\\supseteq ");
		latexMap.put("&supset;", "\\supset ");
		latexMap.put("&supseteq;", "\\supseteq ");
		latexMap.put("&supseteqq;", "\\supseteq ");
		latexMap.put("&Tilde;", "\\sim ");
		latexMap.put("&TildeFullEqual;", "\\cong ");
		latexMap.put("&TildeTilde;", "\\approx ");
		latexMap.put("&uarr;", "\\uparrow ");
		latexMap.put("&uArr;", "\\Uparrow ");
		latexMap.put("&uparrow;", "\\uparrow ");
		latexMap.put("&Uparrow;", "\\Uparrow ");
		latexMap.put("&UpArrow;", "\\uparrow ");
		latexMap.put("&UpTee;", "\\bot ");
		latexMap.put("&varnothing;", "\\oslash ");
		latexMap.put("&varpropto;", "\\propto ");
		latexMap.put("&vee;", "\\vee ");
		latexMap.put("&vprop;", "\\propto ");
		latexMap.put("&wedge;", "\\wedge ");
		latexMap.put("&xoplus;", "\\oplus ");
		latexMap.put("&xotime;", "\\otimes ");
		latexMap.put("&Space;", " ");
		latexMap.put("&colon;", ":");
		latexMap.put("&ApplyFunction;", " ");
		latexMap.put("&squ;", " ");

	}

	/**
	 * The default path of the substitutions file
	 */
	//protected final String SUBSTITUTIONS_FILE = "substitutions.txt";

	/**
	 * The comment prefix for the substitutions file. All lines of the
	 * substitutions file beginning with the comment prefix are ignored.
	 */
	protected final String COMMENT_PREFIX = "**";

	/**
	 * The place holder for blocks in substitutions. If a substitution contains
	 * a block place holder it is replaced by the LaTeX representation of
	 * the followig block.<br>
	 * Syntax: PH_BLOCKSTART + blockNumber + PH_BLOCKEND, e.g. '#BLOCK1#'.
	 */
	protected final String PH_BLOCK_START = "%BLOCK";
	protected final char PH_BLOCK_END = '%';

	protected final char[] specialCharacters = {'%','#','_','$'};
	protected final char[] leftBraces  = {'(','{','['};
	protected final char[] rightBraces = {')','{',']'};

	protected HashMap<String, String> substitutions;
	protected StringBuilder result;
	protected String strBuf;
	protected int pos;
	protected boolean wrappedEntities;
	protected boolean skipUnknownEntities;

	// temporary variables (declared global for better performance)
	//protected String startTag, endTag;
	protected String nextTag;
	protected StringBuilder tagBuf = new StringBuilder(200);  // used by readNextTag() & getBlockEnd()
	protected StringBuilder entity = new StringBuilder(32); // used by replaceEntities()
	protected String entitySubst = ""; // used by replaceEntities()


	/**
	 * Generates the substitution table from the default file path in
	 * field SUBSTITUTIONS_FILE.
	 * 
	 */
	public MathMLParser() {
		substitutions = latexMap;
	}


	/**
	 * Generates the substitution table from the given file path.
	 * 
	 * @param substitutionsTable the substitution table.
	 */
	public MathMLParser(HashMap<String, String> substitutionsTable) {
		substitutions = substitutionsTable;
	}


	/** TODO überarbeiten (complete MathML blocks only?):
	 * Parses MathML code into LaTeX code using the substitution table genereated
	 * by the constructor.<br>
	 * Only presentation markup can be parsed properly, no use for parsing
	 * content markup.
	 * <p>
	 * For example the presentation markup code
	 * <pre>
	 * &lt;mrow&gt;
	 *   &lt;msup&gt;
	 *     &lt;mfenced&gt;
	 *       &lt;mrow&gt;
	 *         &lt;mi&gt;a&lt;/mi&gt;
	 *         &lt;mo&gt;+&lt;/mo&gt;
	 *         &lt;mi&gt;b&lt;/mi&gt;
	 *       &lt;/mrow&gt;
	 *     &lt;/mfenced&gt;
	 *     &lt;mn&gt;2&lt;/mn&gt;
	 *   &lt;/msup&gt;
	 * &lt;/mrow&gt;
	 * </pre>
	 * can be parsed by this method, while the equivalent content markup
	 * <pre>
	 * &lt;mrow&gt;
	 *   &lt;apply&gt;
	 *     &lt;power/&gt;
	 *     &lt;apply&gt;
	 *       &lt;plus/&gt;
	 *       &lt;ci&gt;a&lt;/ci&gt;
	 *       &lt;ci&gt;b&lt;/ci&gt;
	 *     &lt;/apply&gt;
	 *     &lt;cn&gt;2&lt;/cn&gt;
	 *   &lt;/apply&gt;
	 * &lt;/mrow&gt;
	 * </pre>
	 * can not be parsed.
	 * </p>
	 * Both notations of entities can be parsed: The plain MathML notation, starting with
	 * an ampersand sign (e.g. '&amp;equals;'), or the "HTML wrapped" notation startig with an
	 * entity for the ampersand sign (e.g. '&amp;amp;equals;'). 
	 * 
	 * @param strBuf a StringBuilder containig the MathML code to parse
	 * @param wrappedEntities indicates whether the entities in the MathML code are
	 * HTML wrapped (e.g. '&amp;amp;PlusMinus;'), or not (e.g. '&amp;PlusMinus;')
	 * @return a StringBuilder containig the LaTeX representation of the input
	 * @throws Exception if an error occurs while parsing
	 */
	public String parse(String strBuf, boolean wrappedEntities, boolean skipUnknownEntities) {
		if (strBuf != null) {
			this.strBuf = strBuf;
			this.wrappedEntities = wrappedEntities;
			this.skipUnknownEntities = skipUnknownEntities;

			// usually the MathML input should have more characters as the output
			result = new StringBuilder(strBuf.length());

			pos = 0;
			try {
				while (strBuf.indexOf("<", pos) != -1) {
					parseBlock(getNextTag());
					skipFollowingTag();
				}
				// TODO besser result stutzen? -> return new StringBuilder(result) o. result.toString()
				return result.toString();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null; // TODO statt exception, später löschen
		}
		return null;
	}


	/** TODO Pseudocode überarbeiten, Algorithmus noch einmal nachvollziehen
	 * Parses a MathML block in strBuf recursively into LaTeX code.
	 * <p>
	 * Pseudocode:
	 * <pre>
	 * while (pos &lt;= blockEnd) {
	 *   if (insideOfInnerstBlock) {
	 *     result.append(convertToLatexSyntax(area));
	 *   }
	 *   else {
	 *     tmpTag = getNextTag();   // pos = pos + tmpTag.length();
	 *     if (substitutionAvailable(tmpTag)) {
	 *       while (substitutionContainsBlock) { 
	 *         addSubstitutionUpToPlaceHolderOfBlockToOutput();
	 *         parseBlock(getAreaOfNextBlock());
	 *       }
	 *       addRestOfSubstitutionOutPut();
	 *     }
	 *     else {
	 *       parseBlock(pos, getBlockEndIndex(tmpTag));
	 *     }
	 *     skipClosingTag();
	 *   }
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param areaEnd the end of the area to parse
	 * @return a StringBuilder with the LaTeX representation of the input
	 * @throws Exception if an error occurs while parsing
	 */
	void parseBlock(String startTag) throws Exception {

		String endTag = generateEndTag(startTag);
		
		System.out.println(startTag+ " "+endTag);
		
		int blockEnd = getBlockEnd(startTag, endTag);

		String substBuf;
		String blockContent;
		boolean inside = true;

		int blockNumber = 0;
		int prevBlockNumber;

		while (pos <= blockEnd) {

			// scan for subblocks
			int i = pos;
			while ((i <= blockEnd) && (strBuf.charAt(i) != '<')) i++;

			if ((startTag != endTag) && (i > blockEnd)) {

				// if sure to be at the end of the block hierarchy (inside), append block content to result 
				if (inside) {
					blockContent = strBuf.substring(pos, blockEnd+1);
					result.append(parseBlockContent(blockContent));
					pos = pos + blockContent.length();
					blockContent = null;
				}
				else {
					// if all subblocks have been processed skip to the end
					pos = blockEnd+1;
				}
			}
			else {
				// this block has subblocks
				inside = false;

				// if there is a substitution for the next block, write it to 'result' 
				if ((substBuf = substitutions.get(startTag)) != null) {

					int phIndex;
					int substIndex = 0;

					// parse subblocks recursively 
					while ( ((phIndex = substBuf.indexOf(PH_BLOCK_START, substIndex)) > -1) && (pos < blockEnd)) { // blockEnd-1 ??

						// write substitution up to the block marker
						while (substIndex < phIndex) {
							result.append(substBuf.charAt(substIndex));
							substIndex++;
						}
						substIndex += PH_BLOCK_START.length();

						// get number of the block to parse
						int blockNumberIndex = substIndex;
						while (substBuf.charAt(substIndex) != PH_BLOCK_END) {
							substIndex++;
						}

						prevBlockNumber = blockNumber;

						String blockNumberStr = substBuf.substring(blockNumberIndex, substIndex);

						if (blockNumberStr.equals("S")) {
							// keyword is BLOCKS -> parse all inner blocks in order of appearance

							// skip PH_BLOCK_END
							substIndex++;

							// jump to the block to parse
							skipBlocks((1 - prevBlockNumber) - 1);

							// parse subblocks
							while ((strBuf.substring(pos, blockEnd+1)).indexOf('<') != -1) {
								nextTag = getNextTag();
								parseBlock(nextTag);
								skipFollowingTag();
							}
						}
						else {
							// keyword is BLOCK + block number, parse inner blocks in given order

							try {
								blockNumber = Integer.parseInt(blockNumberStr);
							}
							catch (NumberFormatException nfe) {
								throw new Exception("Parsing error at character "+pos+": Unparseable block number in substitution.");
							}

							// skip PH_BLOCK_END
							substIndex++;

							// jump to the block to parse
							skipBlocks((blockNumber - prevBlockNumber) - 1);

							// parse subblock
							nextTag = getNextTag();
							parseBlock(nextTag);

							skipFollowingTag();
						}
					}

					// write (end of) substitution
					while (substIndex < substBuf.length()) {
						result.append(substBuf.charAt(substIndex));
						substIndex++;
					}

					pos = blockEnd + endTag.length();
				}
				else {

					// parse subblocks of nextTag
					while ((strBuf.substring(pos, blockEnd+1)).indexOf('<') != -1) {
						nextTag = getNextTag();
						parseBlock(nextTag);
						skipFollowingTag();
					}
				}
			}
		}
		//System.out.print(pos);
		// TODO Warum braucht 'amayaOut.htm' diese Anweisung? -> 853, 853 (<mprescripts/>)
		pos = blockEnd;
		//System.out.println(", "+pos+" ("+startTag+")");
	}


	/**
	 * Jumps to the next tag, reads it into 'startTag' an generates the corresponding 'endTag'.
	 */
	String getNextTag() {

		while (strBuf.charAt(pos) != '<') {
			pos++;
		}

		tagBuf.setLength(0);

		while (strBuf.charAt(pos) != '>') {
			tagBuf.append(strBuf.charAt(pos));
			pos++;
		}
		pos++;
		tagBuf.append('>');

		return tagBuf.toString();
	}


	/**
	 * Generates an end tag corresponding to the given 'startTag'.
	 * 
	 * @param startTag the start tag to generate an end tag from
	 * @return the end tag for the given start tag
	 */
	String generateEndTag(String startTag) {

		if (startTag.charAt(tagBuf.length()-2) != '/') {

			if (startTag.indexOf(' ') > -1) {
				// delete parameters of startTag
				return new String("</"+startTag.substring(1, startTag.indexOf(' '))+">");
			}
			else {
				return new String("</"+startTag.substring(1, startTag.length()));
			}
		}
		else {
			// if the tag is self-closing (e.g. "<mprescripts/>"), the endTag is the startTag
			return startTag;
		}
	}


	/**
	 * Skips all characters up to the end of the next tag.
	 */
	void skipFollowingTag() {
		while (strBuf.charAt(pos) != '>') {
			pos++;
		}
		pos++;
	}


	/**
	 * Skips (back and forth) a given number of blocks from the actual position.
	 * 
	 * @param blocksToSkip the number of blocks to skip
	 */
	void skipBlocks(int blocksToSkip) {

		if (blocksToSkip > 0) {
			for (int i = 0; i < blocksToSkip; i++) {

				String startTag = getNextTag();
				String endTag = generateEndTag(startTag);

				pos = getBlockEnd(startTag, endTag);

				if (endTag != null) {
					pos = pos + endTag.length();
				}
				else {
					pos = pos + startTag.length();
				}
			}
		}
		else if (blocksToSkip < 0) {

			for (int i = 0; i > blocksToSkip; i--) {

				int subBlocks = 1;

				while (strBuf.charAt(pos) != '>') {
					pos--;
				}

				tagBuf.setLength(0);

				while (strBuf.charAt(pos) != '<') {
					tagBuf.append(strBuf.charAt(pos));
					pos--;				
				}
				tagBuf.append('<');

				tagBuf.reverse();
				String blockEndTag = new String(tagBuf);
				String blockStartTag = new String(tagBuf.deleteCharAt(1));

				do {
					while (strBuf.charAt(pos) != '>') {
						pos--;
					}

					tagBuf.setLength(0);

					while (strBuf.charAt(pos) != '<') {
						tagBuf.append(strBuf.charAt(pos));
						pos--;				
					}
					tagBuf.append('<');
					tagBuf.reverse();

					if (tagBuf.indexOf(" ") > -1) tagBuf.delete(tagBuf.indexOf(" "), tagBuf.length()-1);

					if (tagBuf.toString().equals(blockStartTag)) {
						subBlocks--;
					}
					else {
						if (tagBuf.toString().equals(blockEndTag)) {
							subBlocks++;
						}
					}
				} while ( (subBlocks > 0) || (!(tagBuf.toString().equals(blockStartTag))) );
			}
		}
	}


	/**
	 * Returns the end index of the block defined by the 'startTag' parameter skipping
	 * all subblocks. The end index is the position of the character before the closing
	 * tag of the block.
	 * 
	 * @param startTag the tag that opened the block
	 * @param endTag the end tag to seek
	 * @return the index of the closing tag
	 */
	int getBlockEnd(String startTag, String endTag) {

		if (startTag != endTag) {

			int pos2 = pos;
			int subBlocks = 1;

			// delete parameters of startTag
			if (startTag.indexOf(' ') > -1) {
				startTag = startTag.substring(0, startTag.indexOf(' ')) + '>';
			}

			do {
				while (strBuf.charAt(pos2) != '<') {
					pos2++;
				}

				tagBuf.setLength(0);

				while (strBuf.charAt(pos2) != '>') {
					tagBuf.append(strBuf.charAt(pos2));
					pos2++;				
				}
				tagBuf.append('>');

				if (tagBuf.toString().equals(endTag)) {
					subBlocks--;
				}
				else { 
					if (tagBuf.indexOf(" ") > -1) tagBuf.delete(tagBuf.indexOf(" "), tagBuf.length()-1);

					if (tagBuf.toString().equals(startTag)) {
						subBlocks++;
					}
				}
			} while ( (subBlocks > 0) || (!(tagBuf.toString().equals(endTag))) );

			return (pos2 - endTag.length());
		}

		return pos - startTag.length();
	}


	/**
	 * Parses a String into Latex syntax and returns it.
	 * 
	 * @param s the string to parse
	 * @return the Latex representation of the given string
	 * @throws Exception if HTML wrapped entities were expected but not found
	 */
	String parseBlockContent(String s) throws Exception {

		// TODO hier!
		//System.out.println("got '"+s+"'");

		int sbIndex = 0;
		StringBuilder sb = new StringBuilder(s);

		// replace backslashes
		while ((sbIndex = sb.indexOf("\\", sbIndex)) > -1) {
			sb.insert(sbIndex+1, "backslash");
			sbIndex = sbIndex + 10;
		}

		// replace braces
		for (int i = 0; i < leftBraces.length; i++) {
			sbIndex = 0;
			while ((sbIndex = sb.indexOf(String.valueOf(leftBraces[i]), sbIndex)) > -1) {
				sb.insert(sbIndex, "\\left");
				sbIndex = sbIndex + 6;
			}
		}

		for (int i = 0; i < rightBraces.length; i++) {
			sbIndex = 0;
			while ((sbIndex = sb.indexOf(String.valueOf(rightBraces[i]), sbIndex)) > -1) {
				sb.insert(sbIndex, "\\right");
				sbIndex = sbIndex + 7;
			}
		}


		// replace special characters
		for (int i = 0; i < specialCharacters.length; i++) {
			sbIndex = 0;
			while ((sbIndex = sb.indexOf(String.valueOf(specialCharacters[i]), sbIndex)) > -1) {
				sb.insert(sbIndex, '\\');
				sbIndex = sbIndex + 2;
			}
		}

		// replace Entities
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("&", sbIndex)) > -1) {

			entity.setLength(0);

			while (sb.charAt(sbIndex) != ';') {
				entity.append(sb.charAt(sbIndex));
				sbIndex++;
			}
			entity.append(';');
			sbIndex++;

			if (wrappedEntities && entity.toString().equals("&amp;")) {

				sb.delete(sbIndex - 4, sbIndex);
				sbIndex = sbIndex - 5;

				entity.setLength(0);

				try {
					while (sb.charAt(sbIndex) != ';') {
						entity.append(sb.charAt(sbIndex));
						sbIndex++;
					}
				}
				catch (StringIndexOutOfBoundsException sioobe) {
					throw new Exception("Parsing error at character "+pos+": MathML code is not HTML wrapped.");
				}

				entity.append(';');
				sbIndex++;
			}

			if ((entitySubst = substitutions.get(entity.toString())) != null) {
				sb.delete(sbIndex - entity.length(), sbIndex);
				sbIndex = sbIndex - entity.length();
				sb.insert(sbIndex, entitySubst);
				sbIndex = sbIndex + entitySubst.length();
				sb.insert(sbIndex, " ");
				sbIndex++;
			}
			else {
				if (skipUnknownEntities) {
					sb.delete(sbIndex - entity.length(), sbIndex);
					sbIndex = sbIndex - entity.length();
					sb.insert(sbIndex, " ");
					sbIndex++;
				}
				else {
					sb.insert(sbIndex - entity.length(), "NOTFOUND:'");
					sbIndex = sbIndex + 10;
					sb.insert(sbIndex, "' ");
					sbIndex = sbIndex + 2;
				}
			}
		}

		// replace '&'
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("&", sbIndex)) > -1) {
			sb.insert(sbIndex, '\\');
			sbIndex = sbIndex + 2;
		}

		// replace german "umlauts"
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("ä", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"a");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("Ä", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"A");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("ö", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"o");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("Ö", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"O");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("ü", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"u");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("Ü", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"U");
			sbIndex = sbIndex + 10;
		}

		sbIndex = 0;
		while ((sbIndex = sb.indexOf("ß", sbIndex)) > -1) {
			sb.replace(sbIndex, (sbIndex+1), "\\protect\"s");
			sbIndex = sbIndex + 10;
		}


		return sb.toString();
	}


	/**
	 * Parses the substitution table from the given file.
	 * 
	 * @param filePath the path of the substitutions file
	 * @return a hashtable containing all found substitutions 
	 *
	Hashtable getSubstitutionTable(String fileName)
	throws IOException {

		BufferedReader file;

		try {
			file = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		}
		catch (FileNotFoundException fnfe) {
			try {
				file = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
			}
			catch (NullPointerException npe) {
				throw new IOException("The substitutions file ("+fileName+") could not be read.");
			}
		}

		Hashtable substitutionTable = new Hashtable();
		int lineNo = 0;
		String line;

		String tmpStr;
		String[] subStrings;

		try
		{
			while ((line = file.readLine()) != null)
			{
				lineNo++;

				if ((!line.startsWith(COMMENT_PREFIX)) && (line.indexOf('\t') > -1)) {

					// reduce multiple tabs to one
					tmpStr = line.replaceAll("\t+", "\t");

					if (tmpStr.indexOf('\t') == tmpStr.lastIndexOf('\t')) {

						// split at tab
						subStrings = tmpStr.split("\t");

						// add to table
						try {
							substitutionTable.put(subStrings[0], new StringBuilder(subStrings[1]));
						}
						catch (ArrayIndexOutOfBoundsException aioobe) {
							aioobe.printStackTrace();
							throw new IOException("Error in substitutions file ("+fileName+") at line "+lineNo);
						}
					}
				}
			}
		}
		catch (IOException ioe)
		{
			throw ioe;
		}
		finally
		{
			try
			{
				file.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();				
			}
		}

		return substitutionTable;
	}*/

	private static String[] mathmlTest = {
		// quadratic formula
		"<math xmlns=\"http://www.w3.org/1998/Math/MathML\"> <mstyle displaystyle=\"true\"> <mfrac> <mrow> <mo> - </mo> <mi> b </mi> <mo> &PlusMinus; </mo> <msqrt> <msup> <mrow> <mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup> <mo> - </mo> <mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow> <mn> 2 </mn> <mi> a </mi> </mrow> </mfrac> </mstyle> </math>",
		// quadratic formula with comment
		"<math xmlns=\"http://www.w3.org/1998/Math/MathML\"> <mstyle displaystyle=\"true\"> <mfrac> <mrow> <mo> - </mo> <mi> b </mi> <mo> &#x00B1;<!--plus-minus sign--> </mo> <msqrt> <msup> <mrow> <mi> b </mi> </mrow> <mrow> <mn> 2 </mn> </mrow> </msup> <mo> - </mo> <mn> 4 </mn> <mi> a </mi> <mi> c </mi> </msqrt> </mrow> <mrow> <mn> 2 </mn> <mi> a </mi> </mrow> </mfrac> </mstyle>",
		
		// MathJax tests http://www.mathjax.org/demos/mathml-samples/
		// quadratic formula
		"<math display='block'><mrow><mi>x</mi><mo>=</mo><mfrac><mrow><mo>&#x2212;</mo><mi>b</mi><mo>&#x00B1;</mo><msqrt><mrow><msup><mi>b</mi><mn>2</mn></msup><mo>&#x2212;</mo><mn>4</mn><mi>a</mi><mi>c</mi></mrow></msqrt></mrow><mrow><mn>2</mn><mi>a</mi></mrow></mfrac></mrow></math>",
		//Cauchy's Integral Formula
		"<math display='block'> <mstyle> <mi>f</mi> <mrow> <mo>(</mo> <mi>a</mi> <mo>)</mo> </mrow> <mo>=</mo> <mfrac> <mn>1</mn> <mrow> <mn>2</mn> <mi>π<!-- π --></mi> <mi>i</mi> </mrow> </mfrac> <msub> <mo>∮</mo> <mrow> <mi>γ</mi> </mrow> </msub> <mfrac> <mrow> <mi>f</mi> <mo>(</mo> <mi>z</mi> <mo>)</mo> </mrow> <mrow> <mi>z</mi> <mo>−</mo> <mi>a</mi> </mrow> </mfrac> <mi>d</mi> <mi>z</mi> </mstyle></math>",
		// Double angle formula for Cosines
		"<math display='block'><mrow><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>+</mo><mi>&#x03c6;</mi><mo>)</mo></mrow><mo>=</mo><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>)</mo></mrow><mi>cos</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03c6;</mi><mo>)</mo></mrow><mo>&#x2212;</mo><mi>sin</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03b8;</mi><mo>)</mo></mrow><mi>sin</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mi>&#x03c6;</mi><mo>)</mo></mrow></mrow></math>",
		// Gauss' Divergence Theorem
		"<math display='block'><mrow><mrow><msub><mo>&#x222b;</mo><mrow><mi>D</mi></mrow></msub><mrow><mo>(</mo><mo>&#x2207;&#x22c5;</mo><mi>F</mi><mo>)</mo></mrow><mi>d</mi><mrow><mi>V</mi></mrow></mrow><mo>=</mo><mrow><msub><mo>&#x222b;</mo><mrow><mo>&#x2202;</mo><mi>D</mi></mrow></msub><mrow><mtext>&#x2009;</mtext><mi>F</mi><mo>&#x22c5;</mo><mi>n</mi></mrow><mi>d</mi><mi>S</mi></mrow></mrow></math>",
		// Curl of a Vector Field
		"<math display='block'><mrow><mover accent='true'><mrow><mo>&#x2207;</mo></mrow><mrow><mo>&#x2192;</mo></mrow></mover><mo>&#x00d7;</mo><mover accent='true'><mrow><mi>F</mi></mrow><mrow><mo>&#x2192;</mo></mrow></mover><mo>=</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>z</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>y</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>z</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>i</mi></mrow></mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>x</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>z</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>z</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>j</mi></mrow></mstyle><mo>+</mo><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>y</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac><mo>&#x2212;</mo><mfrac><mrow><mo>&#x2202;</mo><msub><mrow><mi>F</mi></mrow><mrow><mi>x</mi></mrow></msub></mrow><mrow><mo>&#x2202;</mo><mi>y</mi></mrow></mfrac><mo>)</mo></mrow><mstyle mathvariant='bold' mathsize='normal'><mrow><mi>k</mi></mrow></mstyle></mrow></math>",
		// Standard Deviation
		"<math display='block'><mrow><mi>&#x03c3;</mi><mo>=</mo><msqrt><mrow><mfrac><mrow><mn>1</mn></mrow><mrow><mi>N</mi></mrow></mfrac><mstyle displaystyle='true'><mrow><munderover><mrow><mo>&#x2211;</mo></mrow><mrow><mi>i</mi><mo>=</mo><mn>1</mn></mrow><mrow><mi>N</mi></mrow></munderover><mrow><msup><mrow><mo stretchy='false'>(</mo><msub><mrow><mi>x</mi></mrow><mrow><mi>i</mi></mrow></msub><mo>&#x2212;</mo><mi>&#x03bc;</mi><mo stretchy='false'>)</mo></mrow><mrow><mn>2</mn></mrow></msup></mrow></mrow></mstyle></mrow></msqrt><mo>.</mo></mrow></math>",
		// Definition of Christoffel Symbols
		"<math display='block'><mrow><msup><mrow><mo>(</mo><msub><mrow><mo>&#x2207;</mo></mrow><mrow><mi>X</mi></mrow></msub><mi>Y</mi><mo>)</mo></mrow><mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow><mi>X</mi></mrow><mrow><mi>i</mi></mrow></msup><msup><mrow><mo stretchy='false'>(</mo><msub><mrow><mo>&#x2207;</mo></mrow><mrow><mi>i</mi></mrow></msub><mi>Y</mi><mo stretchy='false'>)</mo></mrow><mrow><mi>k</mi></mrow></msup><mo>=</mo><msup><mrow><mi>X</mi></mrow><mrow><mi>i</mi></mrow></msup><mrow><mo>(</mo><mfrac><mrow><mo>&#x2202;</mo><msup><mrow><mi>Y</mi></mrow><mrow><mi>k</mi></mrow></msup></mrow><mrow><mo>&#x2202;</mo><msup><mrow><mi>x</mi></mrow><mrow><mi>i</mi></mrow></msup></mrow></mfrac><mo>+</mo><msubsup><mrow><mi>&#x0393;</mi></mrow><mrow><mi>i</mi><mi>m</mi></mrow><mrow><mi>k</mi></mrow></msubsup><msup><mrow><mi>Y</mi></mrow><mrow><mi>m</mi></mrow></msup><mo>)</mo></mrow></mrow></math>"
	};

	public static void main(String[] args){
		MathMLParser mathmlParser = new MathMLParser();

		for (int i = 0; i < mathmlTest.length ; i++) {
			String s = mathmlTest[i];

			String latex = mathmlParser.parse(s, false, false);

			System.out.println(latex);
		}
	}
}
