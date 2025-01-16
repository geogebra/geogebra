package com.himamis.retex.editor.share.serializer;

import static com.himamis.retex.renderer.share.platform.FactoryProvider.debugS;

import java.util.HashMap;
import java.util.function.Function;

import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathPlaceholder;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;
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
import com.himamis.retex.renderer.share.UnderOverArrowAtom;
import com.himamis.retex.renderer.share.UnderscoreAtom;
import com.himamis.retex.renderer.share.Unit;
import com.himamis.retex.renderer.share.commands.CommandOpName;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Directly convert MathComponents into atoms
 * 
 * @author Zbynek, Agoston
 *
 */
public class TeXBuilder {

	public static final MathComponent SELECTION = new MathSequence() {
		@Override
		public String toString() {
			return "SELECTION";
		}
	};
	public static final double DEFAULT_PLACEHOLDER_Y_SCALE = 1.6;

	// With default and medium text size, "(" open bracket disappears
	// when followed by a placeholder for (,).
	private static final double CHAR_PLACEHOLDER_Y_SCALE = 1.5;

	private MathSequence currentField;
	private int currentOffset;
	private HashMap<Atom, MathComponent> atomToComponent;
	private final TeXParser parser;
	private final TeXSerializer teXSerializer;

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

	private Atom buildSequence(MathSequence mathFormula) {
		RowAtom ra = new RowAtom();

		if (mathFormula.size() == 0) {
			ra.add(getPlaceholderAtom(mathFormula));
			return ra;
		}

		for (int i = 0; i < mathFormula.size(); i++) {
			MathComponent argument1 = mathFormula.getArgument(i);
			if (argument1.hasTag(Tag.SUPERSCRIPT)) {
				Atom sup = build(((MathFunction) argument1).getArgument(0));
				Atom tmp = addToSup(ra.getLastAtom(), sup);
				atomToComponent.put(tmp, argument1);
				ra.add(tmp);
				continue;
			} else if (argument1.hasTag(Tag.SUBSCRIPT)) {
				Atom sub = build(((MathFunction) argument1).getArgument(0));
				Atom tmp = addToSub(ra.getLastAtom(), sub);
				atomToComponent.put(tmp, argument1);
				ra.add(tmp);
				continue;
			} else if (argument1 instanceof MathCharPlaceholder) {
				Atom box = getCharPlaceholder(argument1.getParentIndex());
				atomToComponent.put(box, argument1);
				ra.add(box);
				continue;
			}

			Atom argument = build(argument1);

			// same ugly hack for ln as we have in TeXSerializer
			if (argument instanceof CharAtom
					&& ((CharAtom) argument).getCharacter() == 'n'
					&& ra.last() instanceof CharAtom
					&& ((CharAtom) ra.last()).getCharacter() == 'l') {
				Atom last = ra.getLastAtom();
				ra.add(new RomanAtom(new RowAtom(last, argument)));
				continue;
			}

			ra.add(argument);
		}
		MathComponent last = mathFormula.getArgument(mathFormula.size() - 1);
		if (last instanceof MathCharacter && ((MathCharacter) last).isOperator()) {
			ra.add(new SpaceAtom(Unit.EM, 0));
		}

		return ra;
	}

	private Atom getCharPlaceholder(int index) {
		return index == currentOffset
				? getInvisiblePlaceholder()
				: getPlaceholderBox(CHAR_PLACEHOLDER_Y_SCALE);
	}

	private Atom getPlaceholderAtom(MathSequence mathFormula) {
		Atom a;
		if (mathFormula == currentField) {
			Atom placeholder = new CharAtom('1');
			atomToComponent.put(placeholder, SELECTION);
			a = new ScaleAtom(new PhantomAtom(placeholder), 0.1, 1);
		} else {
			a = getPlaceholder(mathFormula);
		}
		return a;
	}

	private Atom buildPlaceholder(MathPlaceholder placeholder) {
		return new ColorAtom(buildString(placeholder.getContent()), null,
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXSerializer.commandPlaceholderColor));
	}

	private Atom fancyPlaceholder(MathComponent placeholder) {
		return new InputAtom(build(placeholder),
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(TeXSerializer.placeholderBackground),
				FactoryProvider.getInstance().getGraphicsFactory()
						.createColor(0x6557D2));
	}

	private Atom getPlaceholder(MathSequence sequence) {
		MathContainer parent = sequence.getParent();
		if (parent == null
				|| (parent instanceof MathArray && parent.size() == 1)
				|| (parent instanceof MathFunction
					&& ((MathFunction) parent).getName().isRenderingOwnPlaceholders())
				|| !teXSerializer.isPlaceholderEnabled()) {
			return getInvisiblePlaceholder();
		}
		if (parent instanceof MathFunction) {
			Tag fn = ((MathFunction) parent).getName();
			if (fn == Tag.APPLY || fn == Tag.LOG) {
				return getInvisiblePlaceholder();
			}
		}

		return getPlaceholderBox();
	}

	private Atom getInvisiblePlaceholder() {
		return new PhantomAtom(new CharAtom('1'));
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
		if (lastAtom instanceof ScriptsAtom) {
			((ScriptsAtom) lastAtom).addToSub(sub);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, sub, new RowAtom());
	}

	private Atom addToSup(Atom lastAtom, Atom sup) {
		if (lastAtom instanceof ScriptsAtom) {
			((ScriptsAtom) lastAtom).addToSup(sup);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, null, sup);
	}

	private Atom build(MathComponent argument) {
		Atom ret;
		if (argument instanceof MathCharacter) {
			ret = newCharAtom((MathCharacter) argument);
		} else if (argument instanceof MathFunction) {
			ret = buildFunction((MathFunction) argument);
		} else if (argument instanceof MathArray) {
			ret = buildArray((MathArray) argument);
		} else if (argument instanceof MathCharPlaceholder) {
			ret = getCharPlaceholder(argument.getParentIndex());
		} else if (argument instanceof MathPlaceholder) {
			ret = buildPlaceholder((MathPlaceholder) argument);
		} else if (argument instanceof MathSequence) {
			ret = buildSequence((MathSequence) argument);
		} else {
			ret = new EmptyAtom();
		}

		if (!atomToComponent.containsKey(ret)) {
			atomToComponent.put(ret, argument);
		}
		return ret;
	}

	private Atom newCharAtom(MathCharacter character) {
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
		}

		String replacement = replacements.get(unicode);
		if (replacement != null) {
			return SymbolAtom.get(replacement).duplicate();
		}

		Atom ret = parser.getAtomFromUnicode(unicode, true);
		if (ret instanceof SymbolAtom) {
			ret = ((SymbolAtom) ret).duplicate();
		}

		// apply wrapping hack on symbols
		return new ResizeAtom(ret, null, null);
	}

	private Atom asScript(Atom content) {
		Atom script = new ScriptsAtom(EmptyAtom.get(), null,
				content);
		return new ResizeAtom(script, null, null);
	}

	private Atom buildArray(MathArray array) {
		if (array.isMatrix()) {
			final SymbolAtom op = SymbolAtom.get(lookupBracket('('));
			final SymbolAtom cl = SymbolAtom.get(lookupBracket(')'));

			ArrayOfAtoms atoms = new ArrayOfAtoms();
			for (int i = 0; i < array.rows(); i++) {
				for (int j = 0; j < array.columns(); j++) {
					if (j != 0) {
						atoms.add(EnvArray.ColSep.get());
					}
					atoms.add(build(array.getArgument(i, j)));
				}
				atoms.add(EnvArray.RowSep.get());
			}
			atoms.checkDimensions();

			final Atom mat = new SMatrixAtom(atoms, false);
			return new FencedAtom(mat, op, cl);
		} else if (array.getOpenKey() == '"') {
			Atom argument = new RowAtom(newCharAtom(Unicode.OPEN_DOUBLE_QUOTE),
					build(array.getArgument(0)), newCharAtom(Unicode.CLOSE_DOUBLE_QUOTE));

			return new RomanAtom(new TextStyleAtom(argument, TextStyle.MATHNORMAL));
		} else {
			return buildFenced(array.getOpenKey(), array.getCloseKey(), array, 0);
		}
	}

	private Atom buildString(String str) {
		RowAtom atom = new RowAtom();
		for (char c : str.toCharArray()) {
			atom.add(newCharAtom(c));
		}
		return atom;
	}

	private Atom buildFenced(char leftKey, char rightKey,
			MathContainer argument, int offset) {
		return buildFenced(leftKey, rightKey, argument, offset, ',', this::build);
	}

	private Atom buildFenced(char leftKey, char rightKey,
			MathContainer argument, int offset, char delimiter,
			Function<MathComponent, Atom> transform) {
		RowAtom row = new RowAtom((Atom) null);
		for (int i = offset; i < argument.size(); i++) {
			if (i > offset) {
				row.add(newCharAtom(delimiter));
			}
			row.add(transform.apply(argument.getArgument(i)));
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

	private Atom buildFunction(MathFunction argument) {
		switch (argument.getName()) {
		case SUPERSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					null,
					build(argument.getArgument(0))
			);
		case SUBSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					build(argument.getArgument(0)),
					null
			);
		case FRAC:
			return new FractionAtom(build(argument.getArgument(0)),
					build(argument.getArgument(1)));
		case SQRT:
			return new NthRoot(build(argument.getArgument(0)), new EmptyAtom());
		case CBRT:
			return new NthRoot(build(argument.getArgument(0)), newCharAtom('3'));
		case NROOT:
			return new NthRoot(build(argument.getArgument(1)),
					build(argument.getArgument(0)));
		case LOG:
			Atom log = new RomanAtom(buildString("log"));
			if (argument.getArgument(0).size() > 0
					|| currentField == argument.getArgument(0)) {
				log = new ScriptsAtom(log, build(argument.getArgument(0)), new RowAtom());
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
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case SUM_EQ:
			Atom sum = newCharAtom('\u2211');
			sum.type_limits = TeXConstants.SCRIPT_NORMAL;
			return new ScriptsAtom(
					sum,
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case PROD_EQ:
			Atom prod = newCharAtom('\u220F');
			prod.type_limits = TeXConstants.SCRIPT_NORMAL;
			return new ScriptsAtom(
					prod,
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case LIM_EQ:
			return new ScriptsAtom(
					CommandOpName.createOperation("lim", null, true),
					build(argument.getArgument(0)),
					null
			);
		case VEC:
			return new UnderOverArrowAtom(build(argument.getArgument(0)), false, true);
		case ATOMIC_POST:
			return new ScriptsAtom(
					build(argument.getArgument(0)),
					build(argument.getArgument(1)),
					build(argument.getArgument(2))
			);
		case ATOMIC_PRE:
			Atom arg1 = build(argument.getArgument(0));
			Atom arg2 = build(argument.getArgument(1));
			Atom arg3 = build(argument.getArgument(2));
			ScriptsAtom scriptsAtom = new ScriptsAtom(EmptyAtom.get(), arg1, arg2,
					TeXConstants.Align.RIGHT);
			return wrap(scriptsAtom, arg3);
		case POINT:
			return buildFenced('(', ')', argument, 0, ',', this::fancyPlaceholder);
		case POINT_AT:
			return buildFenced('(', ')', argument, 0, '|', this::fancyPlaceholder);
		case RECURRING_DECIMAL:
			Atom overline = new OverlinedAtom(build(argument.getArgument(0)));
			MathComponent next = argument.nextSibling();
			if (!(next instanceof MathCharacter) || !((MathCharacter) next).isWordBreak()) {
				return wrap(overline, new SpaceAtom());
			} else {
				return overline;
			}
		default:
			StringBuilder functionName = new StringBuilder();
			teXSerializer.serialize(argument.getArgument(0), functionName);
			Atom function = build(argument.getArgument(0));

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
	 * @param rootComponent
	 *            root
	 * @param currentField1
	 *            selected field
	 * @return atom representing the whole sequence
	 */
	public Atom build(MathComponent rootComponent, MathSequence currentField1,
			int currentOffset, boolean textMode) {
		this.currentField = currentField1;
		this.currentOffset = currentOffset;
		this.atomToComponent = new HashMap<>();
		Atom root = build(rootComponent);
		if (textMode) {
			return new RomanAtom(new TextStyleAtom(root, TextStyle.MATHNORMAL));
		}
		return root;
	}

	/**
	 * Access the internal mapping atom-&gt; component
	 * 
	 * @param atom
	 *            atom
	 * @return corresponding component
	 */
	public MathComponent getComponent(Atom atom) {
		return atomToComponent.get(atom);
	}

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		teXSerializer.setSyntaxAdapter(syntaxAdapter);
	}

	public void enablePlaceholder(boolean enable) {
		teXSerializer.setPlaceholderEnabled(enable);
	}
}
