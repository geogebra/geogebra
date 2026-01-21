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

package org.geogebra.editor.share.serializer;

import static com.himamis.retex.renderer.share.platform.FactoryProvider.debugS;

import java.util.HashMap;
import java.util.function.Function;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.editor.SyntaxAdapter;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.Unicode;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.ArrayOfAtoms;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.EnvArray;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.InputAtom;
import com.himamis.retex.renderer.share.JavaFontRenderingAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.OverlinedAtom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.ResizeAtom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SMatrixAtom;
import com.himamis.retex.renderer.share.ScaleAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TextStyle;
import com.himamis.retex.renderer.share.TextStyleAtom;
import com.himamis.retex.renderer.share.TypedAtom;
import com.himamis.retex.renderer.share.UnderOverArrowAtom;
import com.himamis.retex.renderer.share.UnderscoreAtom;
import com.himamis.retex.renderer.share.Unit;
import com.himamis.retex.renderer.share.commands.CommandOpName;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Directly convert Nodes into atoms
 * @author Zbynek, Agoston
 */
public class TeXBuilder {

	public static final Node SELECTION = new SequenceNode() {
		@Override
		public String toString() {
			return "SELECTION";
		}
	};
	public static final int INPUT_BORDER = 0x6557D2;
	public static final double DEFAULT_PLACEHOLDER_Y_SCALE = 1.6;

	// With default and medium text size, "(" open bracket disappears
	// when followed by a placeholder for (,).
	private static final double CHAR_PLACEHOLDER_Y_SCALE = 1.5;

	private SequenceNode currentNode;
	private int currentOffset;
	private HashMap<Atom, Node> atomToNode;
	private final TeXParser parser;
	private final TeXSerializer teXSerializer;
	private boolean useSimplePlaceholders = false;

	private final static HashMap<Character, String> replacements = new HashMap<>();

	static {
		replacements.put('%', "textpercent");
		replacements.put('$', "textdollar");
		replacements.put('&', "textampersand");
		replacements.put('\u23B8', "vert");
	}

	/**
	 * New formula to atom converter
	 */
	public TeXBuilder() {
		parser = new TeXParser("");
		teXSerializer = new TeXSerializer();
	}

	/**
	 * @param useSimplePlaceholders Whether to use simple placeholders in matrix
	 */
	public void useSimpleMatrixPlaceholders(boolean useSimplePlaceholders) {
		this.useSimplePlaceholders = useSimplePlaceholders;
	}

	private Atom buildSequence(SequenceNode mathFormula) {
		RowAtom ra = new RowAtom();

		if (mathFormula.size() == 0) {
			ra.add(getPlaceholderAtom(mathFormula));
			return ra;
		}

		for (int i = 0; i < mathFormula.size(); i++) {
			Node argument1 = mathFormula.getChild(i);
			if (argument1.hasTag(Tag.SUPERSCRIPT)) {
				Atom sup = build(((FunctionNode) argument1).getChild(0));
				Atom tmp = addToSup(ra.getLastAtom(), sup);
				atomToNode.put(tmp, argument1);
				ra.add(tmp);
				continue;
			} else if (argument1.hasTag(Tag.SUBSCRIPT)) {
				Atom sub = build(((FunctionNode) argument1).getChild(0));
				Atom tmp = addToSub(ra.getLastAtom(), sub);
				atomToNode.put(tmp, argument1);
				ra.add(tmp);
				continue;
			} else if (argument1 instanceof CharPlaceholderNode) {
				Atom box = getCharPlaceholder(argument1.getParentIndex());
				atomToNode.put(box, argument1);
				ra.add(box);
				continue;
			}

			Atom argument = build(argument1);

			// same ugly hack for ln as we have in TeXSerializer
			if (argument instanceof CharAtom atom
					&& atom.getCharacter() == 'n'
					&& ra.last() instanceof CharAtom
					&& ((CharAtom) ra.last()).getCharacter() == 'l') {
				Atom last = ra.getLastAtom();
				ra.add(new RomanAtom(new RowAtom(last, argument)));
				continue;
			}

			ra.add(argument);
		}
		Node last = mathFormula.getChild(mathFormula.size() - 1);
		if (last instanceof CharacterNode node && node.isOperator()) {
			ra.add(new SpaceAtom(Unit.EM, 0));
		}

		return ra;
	}

	private Atom getCharPlaceholder(int index) {
		return index == currentOffset
				? getInvisiblePlaceholder()
				: getPlaceholderBox(CHAR_PLACEHOLDER_Y_SCALE);
	}

	private Atom getPlaceholderAtom(SequenceNode mathFormula) {
		Atom a;
		if (mathFormula == currentNode) {
			Atom placeholder = new CharAtom('1');
			atomToNode.put(placeholder, SELECTION);
			a = new ScaleAtom(new PhantomAtom(placeholder), 0.1, 1);
		} else {
			a = getPlaceholder(mathFormula);
		}
		return a;
	}

	private Atom buildPlaceholder(PlaceholderNode placeholder) {
		return new ColorAtom(buildString(placeholder.getContent()), null,
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXSerializer.commandPlaceholderColor));
	}

	private Atom fancyPlaceholder(Node placeholder) {
		Atom ret = new InputAtom(build(placeholder),
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXSerializer.placeholderBackground),
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXBuilder.INPUT_BORDER));
		atomToNode.put(ret, placeholder);
		return ret;
	}

	private Atom getPlaceholder(SequenceNode sequence) {
		InternalNode parent = sequence.getParent();
		if (parent != null && parent.isRenderingOwnPlaceholders() && !useSimplePlaceholders) {
			return zwsp();
		}
		if (parent == null
				|| (parent instanceof ArrayNode && parent.size() == 1)
				|| !teXSerializer.isPlaceholderEnabled()) {
			return getInvisiblePlaceholder();
		}
		if (parent instanceof FunctionNode node) {
			Tag fn = node.getName();
			if (fn == Tag.APPLY || fn == Tag.LOG) {
				return getInvisiblePlaceholder();
			}
		}

		return getPlaceholderBox();
	}

	private Atom getInvisiblePlaceholder() {
		return new PhantomAtom(new CharAtom('1'));
	}

	private Atom zwsp() {
		return new SpaceAtom(Unit.EM, 0, InputAtom.MIN_INPUT_HEIGHT, 0);
	}

	private Atom getPlaceholderBox() {
		return getPlaceholderBox(DEFAULT_PLACEHOLDER_Y_SCALE);
	}

	private Atom getPlaceholderBox(double yScale) {
		return new ColorAtom(
				new ScaleAtom(new PhantomAtom(new CharAtom('g')), 1, yScale),
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXSerializer.placeholderColor),
				null
		);
	}

	private Atom addToSub(Atom lastAtom, Atom sub) {
		if (lastAtom instanceof ScriptsAtom atom) {
			atom.addToSub(sub);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, sub, new RowAtom());
	}

	private Atom addToSup(Atom lastAtom, Atom sup) {
		if (lastAtom instanceof ScriptsAtom atom) {
			atom.addToSup(sup);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, null, sup);
	}

	private Atom build(Node argument) {
		Atom ret;
		if (argument instanceof CharacterNode node) {
			ret = newCharAtom(node);
		} else if (argument instanceof FunctionNode node) {
			ret = buildFunction(node);
		} else if (argument instanceof ArrayNode node) {
			ret = buildArray(node);
		} else if (argument instanceof CharPlaceholderNode) {
			ret = getCharPlaceholder(argument.getParentIndex());
		} else if (argument instanceof PlaceholderNode node) {
			ret = buildPlaceholder(node);
		} else if (argument instanceof SequenceNode node) {
			ret = buildSequence(node);
		} else {
			ret = new EmptyAtom();
		}

		if (!atomToNode.containsKey(ret)) {
			atomToNode.put(ret, argument);
		}
		return ret;
	}

	private Atom newCharAtom(CharacterNode character) {
		if ("\\cdot{}".equals(character.getTexName())) {
			return SymbolAtom.get("cdot").duplicate();
		}
		if (character.getUnicodeString().length() > 1) {
			return new JavaFontRenderingAtom(character.getUnicodeString());
		}
		return newCharAtom(character.getUnicode());
	}

	private Atom newCharAtom(char unicode) {
		switch (unicode) {
		case ' ':
			return new SpaceAtom();
		case '^':
			return new AccentedAtom(new SpaceAtom(), Symbols.HAT);
		case '_':
			return new UnderscoreAtom();
		case '\u2032':
			return asScript(parser.getAtomFromUnicode(unicode, false));
		case '\u2033':
			return asScript(new RowAtom(parser.getAtomFromUnicode('\u2032', false),
					parser.getAtomFromUnicode('\u2032', false)));
		case Unicode.DEGREE_CHAR:
			return asScript(parser.getAtomFromUnicode('\u2218', false));

		case ':':
			return new ResizeAtom(new TypedAtom(TeXConstants.TYPE_PUNCTUATION,
					parser.getAtomFromUnicode(unicode, true)), null, null);
		}

		String replacement = replacements.get(unicode);
		if (replacement != null) {
			return SymbolAtom.get(replacement).duplicate();
		}

		Atom ret = parser.getAtomFromUnicode(unicode, true);
		if (ret instanceof SymbolAtom atom) {
			ret = atom.duplicate();
		}

		// apply wrapping hack on symbols
		return new ResizeAtom(ret, null, null);
	}

	private Atom asScript(Atom content) {
		Atom script = new ScriptsAtom(EmptyAtom.get(), null,
				content);
		return new ResizeAtom(script, null, null);
	}

	private Atom buildArray(ArrayNode array) {
		if (array.isMatrix()) {
			ArrayOfAtoms atoms = new ArrayOfAtoms();
			SequenceNode argument;
			for (int i = 0; i < array.getRows(); i++) {
				for (int j = 0; j < array.getColumns(); j++) {
					if (j != 0) {
						atoms.add(EnvArray.ColSep.get());
					}
					argument = array.getChild(i, j);
					atoms.add(useSimplePlaceholders ? build(argument) : fancyPlaceholder(argument));
				}
				atoms.add(EnvArray.RowSep.get());
			}
			return getFencedMatrix(atoms);

		} else if (array.getOpenDelimiter().getCharacter() == '"') {
			Atom argument = new RowAtom(newCharAtom(Unicode.OPEN_DOUBLE_QUOTE),
					build(array.getChild(0)), newCharAtom(Unicode.CLOSE_DOUBLE_QUOTE));

			return new RomanAtom(new TextStyleAtom(argument, TextStyle.MATHNORMAL));
		} else {
			return buildFenced(array.getOpenDelimiter().getCharacter(),
					array.getCloseDelimiter().getCharacter(), array, 0);
		}
	}

	private Atom getFencedMatrix(ArrayOfAtoms atoms) {
		atoms.checkDimensions();
		final SymbolAtom op = SymbolAtom.get(lookupBracket('('));
		final SymbolAtom cl = SymbolAtom.get(lookupBracket(')'));
		final Atom mat = new SMatrixAtom(atoms, false);
		return new FencedAtom(mat, op, cl);
	}

	private Atom buildString(String str) {
		RowAtom atom = new RowAtom();
		for (char c : str.toCharArray()) {
			atom.add(newCharAtom(c));
		}
		return atom;
	}

	private Atom buildFenced(char leftKey, char rightKey,
			InternalNode argument, int offset) {
		return buildFenced(leftKey, rightKey, argument, offset, ',', this::build);
	}

	private Atom buildFenced(char leftKey, char rightKey,
			InternalNode argument, int offset, char delimiter,
			Function<Node, Atom> transform) {
		RowAtom row = new RowAtom((Atom) null);
		for (int i = offset; i < argument.size(); i++) {
			if (i > offset) {
				row.add(newCharAtom(delimiter));
			}
			row.add(transform.apply(argument.getChild(i)));
		}
		return new FencedAtom(row,
				new SymbolAtom(lookupBracket(leftKey), TeXConstants.TYPE_OPENING,
						leftKey),
				new SymbolAtom(lookupBracket(rightKey), TeXConstants.TYPE_CLOSING,
						rightKey));
	}

	private static String lookupBracket(char bracket) {
		switch (bracket) {
		case '(':
			return "lbrack";
		case ')':
			return "rbrack";
		case '{':
			return "lbrace";
		case '}':
			return "rbrace";
		case '[':
			return "lsqbrack";
		case ']':
			return "rsqbrack";
		case '\u3008':
			return "langle";
		case '\u3009':
			return "rangle";
		case '\u2308':
			return "lceil";
		case '\u2309':
			return "rceil";
		case '\u230A':
			return "lfloor";
		case '\u230B':
			return "rfloor";
		case '|':
			return "vert";
		default:
			debugS("missing case in lookupBracket()");
			return "";
		}
	}

	private Atom buildFunction(FunctionNode argument) {
		switch (argument.getName()) {
		case SUPERSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					null,
					build(argument.getChild(0))
			);
		case SUBSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					build(argument.getChild(0)),
					null
			);
		case FRAC:
			return new FractionAtom(build(argument.getChild(0)),
					build(argument.getChild(1)));
		case SQRT:
			return new NthRoot(build(argument.getChild(0)), new EmptyAtom());
		case CBRT:
			return new NthRoot(build(argument.getChild(0)), newCharAtom('3'));
		case NROOT:
			return new NthRoot(build(argument.getChild(1)),
					build(argument.getChild(0)));
		case LOG:
			Atom log = new RomanAtom(buildString("log"));
			if (argument.getChild(0).size() > 0
					|| currentNode == argument.getChild(0)) {
				log = new ScriptsAtom(log, build(argument.getChild(0)), new RowAtom());
			}

			return wrap(
					log,
					buildFenced('(', ')', argument, 1)
			);
		case ABS:
			return buildFenced('|', '|', argument, 0);
		case FLOOR:
			return buildFenced('\u230A', '\u230B', argument, 0);
		case CEIL:
			return buildFenced('\u2308', '\u2309', argument, 0);
		case DEF_INT:
			return new ScriptsAtom(
					Symbols.INT.duplicate(),
					build(argument.getChild(0)),
					build(argument.getChild(1))
			);
		case SUM_EQ:
			Atom sum = newCharAtom('\u2211');
			sum.type_limits = TeXConstants.SCRIPT_NORMAL;
			return new ScriptsAtom(
					sum,
					build(argument.getChild(0)),
					build(argument.getChild(1))
			);
		case PROD_EQ:
			Atom prod = newCharAtom('\u220F');
			prod.type_limits = TeXConstants.SCRIPT_NORMAL;
			return new ScriptsAtom(
					prod,
					build(argument.getChild(0)),
					build(argument.getChild(1))
			);
		case LIM_EQ:
			return new ScriptsAtom(
					CommandOpName.createOperation("lim", null, true),
					build(argument.getChild(0)),
					null
			);
		case VEC:
			return new UnderOverArrowAtom(build(argument.getChild(0)), false, true);
		case ATOMIC_POST:
			return new ScriptsAtom(
					build(argument.getChild(0)),
					build(argument.getChild(1)),
					build(argument.getChild(2))
			);
		case ATOMIC_PRE:
			Atom arg1 = build(argument.getChild(0));
			Atom arg2 = build(argument.getChild(1));
			Atom arg3 = build(argument.getChild(2));
			ScriptsAtom scriptsAtom = new ScriptsAtom(EmptyAtom.get(), arg1, arg2,
					TeXConstants.Align.RIGHT);
			return wrap(scriptsAtom, arg3);
		case POINT:
			return buildFenced('(', ')', argument, 0, ',', this::fancyPlaceholder);
		case POINT_AT:
			return buildFenced('(', ')', argument, 0, '|', this::fancyPlaceholder);
		case VECTOR:
			ArrayOfAtoms rows = new ArrayOfAtoms();
			for (int i = 0; i < argument.size(); i++) {
				SequenceNode coord = argument.getChild(i);
				if (coord != null) {
					rows.add(fancyPlaceholder(coord));
					rows.add(EnvArray.RowSep.get());
				}
			}
			return getFencedMatrix(rows);
		case RECURRING_DECIMAL:
			Atom overline = new OverlinedAtom(build(argument.getChild(0)));
			Node next = argument.nextSibling();
			if (!(next instanceof CharacterNode) || !((CharacterNode) next).isWordBreak()) {
				return wrap(overline, new SpaceAtom());
			} else {
				return overline;
			}
		default:
			StringBuilder functionName = new StringBuilder();
			teXSerializer.serialize(argument.getChild(0), functionName);
			Atom function = build(argument.getChild(0));

			if (teXSerializer.isFunction(functionName.toString())) {
				function = new RomanAtom(function);
			}

			return wrap(
					function,
					buildFenced(argument.getOpeningBracket(), argument.getClosingBracket(),
							argument, 1)
			);
		}
	}

	private Atom wrap(Atom... atoms) {
		// the resize atom is just a hack so that the RowAtom is not destroyed
		// when added to another row atom
		return new ResizeAtom(new RowAtom(atoms), null, null);
	}

	/**
	 * @param rootNode root
	 * @param currentNode selected field
	 * @return atom representing the whole sequence
	 */
	public Atom build(Node rootNode, SequenceNode currentNode,
			int currentOffset, boolean textMode) {
		this.currentNode = currentNode;
		this.currentOffset = currentOffset;
		this.atomToNode = new HashMap<>();
		Atom root = build(rootNode);
		if (textMode) {
			return new RomanAtom(new TextStyleAtom(root, TextStyle.MATHNORMAL));
		}
		return root;
	}

	/**
	 * Access the internal mapping atom-&gt; node
	 * @param atom atom
	 * @return corresponding node
	 */
	public Node getNode(Atom atom) {
		return atomToNode.get(atom);
	}

	/**
	 * @param syntaxAdapter syntax adapter
	 */
	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		teXSerializer.setSyntaxAdapter(syntaxAdapter);
	}

	/**
	 * Enable or disable (simple) placeholders in the serializer.
	 * @param enable whether to enable
	 */
	public void enablePlaceholder(boolean enable) {
		teXSerializer.setPlaceholderEnabled(enable);
	}
}
