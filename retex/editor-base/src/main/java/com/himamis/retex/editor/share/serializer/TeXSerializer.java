package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.util.LaTeXUtil;

/**
 * Serializes internal format into TeX format.
 */
public class TeXSerializer extends SerializerAdapter {

	private static final String cursor = "\\jlmcursor{0}";
	private static final String cursorBig = "\\jlmcursor{0.9}";
	private static final String selection_start = "\\jlmselection{";
	private static final String selection_end = "}";


    private static final String characterMissing = "\\nbsp ";
    private MetaModel metaModel;

	/**
	 * @param metaModel
	 *            model
	 */
    public TeXSerializer(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public void serialize(MathCharacter mathCharacter, StringBuilder stringBuilder) {
		if (mathCharacter.getUnicode() == '\u200b') {
			return;
		}
        // jmathtex v0.7: incompatibility
		if (mathCharacter == currentSelStart) {
			stringBuilder.append(selection_start);
		}
		if ("=".equals(mathCharacter.getName())) {
			stringBuilder.append("\\,=\\,");
		} else if ("@".equals(mathCharacter.getName())) {
			stringBuilder.append("\\@ ");
		} else if (" ".equals(mathCharacter.getName())) {
			stringBuilder.append("\\nbsp ");
        } else {
            String texName = mathCharacter.getTexName();
            if (LaTeXUtil.isSymbolEscapeable(texName)) {
                // escape special symbols
                stringBuilder.append('\\');
                stringBuilder.append(texName);
            } else if (LaTeXUtil.isReplaceableSymbol(texName)) {
                stringBuilder.append(LaTeXUtil.replaceSymbol(texName));
            } else {
                stringBuilder.append(texName);
            }
        }
		if (mathCharacter == currentSelEnd) {
			stringBuilder.append(selection_end);
		}

        // safety space after operator / symbol
        if (mathCharacter.isOperator() || mathCharacter.isSymbol()) {
            stringBuilder.append(' ');
        }
    }

    @Override
    public void serialize(MathSequence sequence, StringBuilder stringBuilder) {
		if (sequence == null) {
			stringBuilder.append("?");
			return;
		}
		int lengthBefore = stringBuilder.length();
        boolean addBraces = (sequence.hasChildren() || // {a^b_c}
                sequence.size() > 1 || // {aa}
                (sequence.size() == 1 && letterLength(sequence, 0) > 1) || // {\pi}
                (sequence.size() == 0 && sequence != currentField) || // {\triangleright}
                (sequence.size() == 1 && sequence == currentField))
                && // {a|}
                (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != '{');
		if (sequence == currentSelStart) {
			stringBuilder.append(selection_start);
		}
        if (addBraces) {
            // when necessary add curly braces
            stringBuilder.append('{');
        }

        if (sequence.size() == 0) {
			if (sequence == currentField) {
				if (currentSelStart == null) {
					stringBuilder.append(cursorBig);
				}
            } else {
                if (sequence.getParent() == null
                        || /* symbol.getParent() instanceof MathOperator || */
                        (sequence.getParent() instanceof MathFunction && sequence
                                .getParentIndex() == sequence.getParent()
                                .getInsertIndex())) {
                    stringBuilder.append(characterMissing);
                } else {
                    stringBuilder.append(characterMissing);
                }
            }
        } else {
            if (sequence == currentField) {

				if (currentOffset > 0) {
					serialize(sequence, stringBuilder, 0, currentOffset);
				}
				if (currentSelStart == null) {
					
					stringBuilder.append(cursor);

				}
				if (currentOffset < sequence.size()) {
					serialize(sequence, stringBuilder, currentOffset,
							sequence.size());
				}
				boolean emptyFormula = stringBuilder
						.substring(lengthBefore, stringBuilder.length())
						.replace("\\nbsp", "").replace(cursor, "").trim()
						.isEmpty();
				if(emptyFormula){
					String cursorFix = stringBuilder.toString().replace(cursor,cursorBig);
					stringBuilder.setLength(0);
					stringBuilder.append(cursorFix);
				}
            } else {
                serialize(sequence, stringBuilder, 0, sequence.size());
            }
        }

        if (addBraces) {
            // when necessary add curly braces
            stringBuilder.append('}');
        }
		if (sequence == currentSelEnd) {
			stringBuilder.append(selection_end);
		}
    }

    @Override
    public void serialize(MathSequence sequence, StringBuilder stringBuilder, int from, int to) {
        for (int i = from; i < to; i++) {
            serialize(sequence.getArgument(i), stringBuilder);
        }
    }

    @Override
    public void serialize(MathFunction function, StringBuilder stringBuilder) {
		if (function == currentSelStart) {
			stringBuilder.append(selection_start);
		}
        if (metaModel.isGeneral(function.getName())) {

            if ("^".equals(function.getName())
                    || "_".equals(function.getName())) {
                MathSequence parent = function.getParent();
                int index = function.getParentIndex();
                if (index == 0
                        || (index > 0
                        && parent.getArgument(index - 1) instanceof MathCharacter && ((MathCharacter) parent
                        .getArgument(index - 1)).isOperator())) {
                    stringBuilder.append(characterMissing);
                }
                stringBuilder.append(function.getName() + '{');
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append('}');

            } else if ("frac".equals(function.getName())) {
                stringBuilder.append("{");
				stringBuilder.append(function.getTexName());
				stringBuilder.append("{");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("}{");
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append("}}");

            } else if ("sqrt".equals(function.getName())) {
                stringBuilder.append(function.getTexName());
                stringBuilder.append("{");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("}");

            } else if ("nroot".equals(function.getName())) {
                stringBuilder.append(function.getTexName());
                stringBuilder.append('[');
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("]{");
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append('}');

            } else if ("sum".equals(function.getName())
                    || "prod".equals(function.getName())) {
                stringBuilder.append(function.getTexName());
                stringBuilder.append("_{");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append('=');
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append("}^");
                serialize(function.getArgument(2), stringBuilder);
				boolean addBraces = function.getArgument(3).hasOperator();
				addWithBraces(stringBuilder, function.getArgument(3),
						addBraces);

            } else if ("nsum".equals(function.getName())
                    || "nprod".equals(function.getName())) {
                stringBuilder.append(function.getTexName());
                stringBuilder.append("_{");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append('=');
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append('}');
				boolean addBraces = function.getArgument(2).hasOperator();
				addWithBraces(stringBuilder, function.getArgument(2),
						addBraces);

            } else if ("int".equals(function.getName())) {
                stringBuilder.append(function.getTexName());
                stringBuilder.append('_');
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append('^');
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append('{');
                boolean addBraces = currentBraces;
                if (addBraces) {
					stringBuilder.append("\\left(");
                }
                serialize(function.getArgument(2), stringBuilder);
                // jmathtex v0.7: incompatibility
				stringBuilder.append(" " + ("\\nbsp") + " d");
                serialize(function.getArgument(3), stringBuilder);
                if (addBraces) {
					stringBuilder.append("\\right)");
                }
                stringBuilder.append('}');

            } else if ("lim".equals(function.getName())) {
                // lim not implemented in jmathtex
				stringBuilder.append("\\lim_{");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append(" \\rightarrow ");
                serialize(function.getArgument(1), stringBuilder);
                // jmathtex v0.7: incompatibility
				stringBuilder.append("} " + ("\\nbsp") + " {");
				boolean addBraces = (function.getArgument(2).hasOperator()
						&& function
                        .getParent().hasOperator());
				this.addWithBraces(stringBuilder, function.getArgument(2),
						addBraces);
                stringBuilder.append('}');

            } else if ("factorial".equals(function.getName())) {
				boolean addBraces = function.getArgument(0).hasOperator();
				addWithBraces(stringBuilder, function.getArgument(0),
						addBraces);
                stringBuilder.append(function.getTexName());
            } else if ("'".equals(function.getName())) {
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("'");
            } else if ("abs".equals(function.getName())) {
                stringBuilder.append("\\left|");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("\\right|");
			} else if ("floor".equals(function.getName())) {
				stringBuilder.append("\\left\\lfloor");
				serialize(function.getArgument(0), stringBuilder);
				stringBuilder.append("\\right\\rfloor");
			} else if ("ceil".equals(function.getName())) {
				stringBuilder.append("\\left\\lceil");
				serialize(function.getArgument(0), stringBuilder);
				stringBuilder.append("\\right\\rceil");
            } else if ("function".equals(function.getName())) {
                stringBuilder.append("\\mathrm{" + function.getTexName() + "} ");
                // jmathtex v0.7: incompatibility
				stringBuilder.append("\\nbsp ");
                serialize(function.getArgument(0), stringBuilder);
                stringBuilder.append("\\left(");
                serialize(function.getArgument(1), stringBuilder);
                stringBuilder.append("\\right)=");
                boolean addBraces = currentBraces
                        || (function.getArgument(2).hasOperator() && function
                        .getParent().hasOperator());
                if (addBraces) {
                    stringBuilder.append("\\left(");
                }
                serialize(function.getArgument(2), stringBuilder);
                if (addBraces) {
                    stringBuilder.append("\\right)");
                }
            }
        } else {
			stringBuilder.append("{\\mathrm{");
			stringBuilder.append(function.getTexName());
			stringBuilder.append("}");
            stringBuilder.append("\\left");
            stringBuilder.append(MathFunction.getOpeningBracket());
            for (int i = 0; i < function.size(); i++) {
                serialize(function.getArgument(i), stringBuilder);
                if (i + 1 < function.size()) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("\\right");
            stringBuilder.append(MathFunction.getClosingBracket());
            stringBuilder.append("}");
        }
		if (function == currentSelEnd) {
			stringBuilder.append(selection_end);
		}
    }

	private void addWithBraces(StringBuilder stringBuilder,
			MathSequence argument, boolean addBraces) {
		if (currentBraces || addBraces) {
			stringBuilder.append("\\left(");
		}
		serialize(argument, stringBuilder);
		if (currentBraces || addBraces) {
			stringBuilder.append("\\right)");
		}

	}

	@Override
    public void serialize(MathArray array, StringBuilder stringBuilder) {
		if (this.currentSelStart == array) {
			stringBuilder.append(TeXSerializer.selection_start);
		}
		stringBuilder.append(array.getOpen().getTexName());
		for (int i = 0; i < array.rows(); i++) {
			for (int j = 0; j < array.columns(); j++) {
				serialize(array.getArgument(i, j), stringBuilder);
				if (j + 1 < array.columns()) {
					stringBuilder.append(array.getField().getTexName());
				} else if (i + 1 < array.rows()) {
					stringBuilder.append(array.getRow().getTexName());
				}
			}
		}
		stringBuilder.append(array.getClose().getTexName());
		if (this.currentSelEnd == array) {
			stringBuilder.append(TeXSerializer.selection_end);
		}
    }

    private static int letterLength(MathSequence symbol, int i) {
        if (symbol.getArgument(i) instanceof MathCharacter) {
            return ((MathCharacter) symbol.getArgument(i)).getTexName()
                    .length();
        }
		return 2;
    }

	/**
	 * @param ms
	 *            sequence
	 * @param model
	 *            model
	 * @return TeX representation of the sequence
	 */
	public static String serialize(MathSequence ms, MetaModel model) {
		StringBuilder b = new StringBuilder();
		new TeXSerializer(model).serialize(ms, b);
		return b.toString();
	}
}
