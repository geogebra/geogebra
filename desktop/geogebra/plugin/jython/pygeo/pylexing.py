import lexing
import keyword, __builtin__

class PyLexer(lexing.Lexer):
    separators = [
        'space', 'punctuation', 'assignment', 'grouper', 'operator',
        'shortstring', 'geo'
    ]
    order = [
        'float', 'integer', 'shortstring',
        'operator', 'assignment',
        'grouper',
        'keyword', 'builtin', 'identifier', 
        'space', 'punctuation'
        'comment',
    ]

    space = r'\s'
    decorator = r'^\s*@'
    punctuation = r"[.,;:]"
    assignment = r"=|\+=|-=|\*\*=|\*=|/=|//=|<<=|>>=|\^=|\|=|&="
    grouper = r"[(){}\[\]]"
    operator = r"\+|-|\*\*|\*|//|/|\^|&|\||~|<=|>=|==|!="
    keyword = r"(%s)(?!\w)" % "|".join(keyword.kwlist)
    builtin = r"(%s)(?!\w)" % "|".join(dir(__builtin__))
    integer = r"[0-9]+"
    identifier = r"[a-zA-Z_]\w*"
    float = r"[0-9]+\.[0-9]*([eE][+-]?[0-9]+)?"
    shortstring = r"[uU]?[rR]?'([^'\n\\]|\\[^\n])*'" \
                  r'|[uU]?[rR]?"([^"\n\\]|\\[^\n])*"'
    comment = r'#.*'
    geo = r"\$" + identifier
    decorator = "@" + identifier

def pythonify(source):
    pos = 0
    true_python_bits = []
    while True:
        try:
            for tok in PyLexer.scan(source, pos):
                if tok.toktype == PyLexer.geo:
                    bit = "geo." + tok.strval[1:]
                else:
                    bit = tok.strval
                true_python_bits.append(bit)
                pos += len(tok.strval)
            else:
                break
        except lexing.Error:
            true_python_bits.append(source[pos])
            pos += 1
    return "".join(true_python_bits)
