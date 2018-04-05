/* lexical scanner from W3C spec. see http://www.w3.org/TR/css3-syntax/#lexical */

h			[0-9a-fA-F]
nonascii	[\200-\377]
unicode		\\{h}{1,6}[ \t\r\n\f]?
escape		{unicode}|\\[ -~\200-\377]
nmstart		[a-zA-Z]|{nonascii}|{escape}
nmchar		[a-zA-Z0-9-]|{nonascii}|{escape}
string1		\"([\t !#$%&(-~]|\\{nl}|\'|{nonascii}|{escape})*\"
string2		\'([\t !#$%&(-~]|\\{nl}|\"|{nonascii}|{escape})*\'

identchar	[_]|{nmchar}
ident		[-]?{nmstart}{identchar}*
name		{identchar}+
num			([0-9]+(\.[0-9]+)?)|(\.[0-9]+)
string		{string1}|{string2}
url			([!#$%&*-~]|{nonascii}|{escape})*
w			[ \t\r\n\f]*
nl			\n|\r\n|\r|\f
range		\?{1,6}|{h}(\?{0,5}|{h}(\?{0,4}|{h}(\?{0,3}|{h}(\?{0,2}|{h}(\??|{h})))))

vendor_prefix		"-webkit-"|"-o-"|"-moz-"|"-ms-"
keyframes			[@]{vendor_prefix}?"keyframes"

%%

[ \t\r\n\f]+						{return 'S';}
\/\*[^*]*\*+([^/][^*]*\*+)*\/		{}								/* ignore comment */

"<!--"								{return 'CDO';}					/* comment delimiter open */
"-->"								{return 'CDC';}					/* comment delimiter close */
"~="								{return 'INCLUDES';}			/* includes */
"|="								{return 'DASHMATCH';}			/* dash match */
"^="								{return 'PREFIXMATCH';}			/* prefix match */
"$="								{return 'SUFFIXMATCH';}			/* suffix match */
"*="								{return 'SUBSTRINGMATCH';}		/* substring match */

"!"{w}"important"					{return 'IMPORTANT_SYM';}

"url("{w}{string}{w}")"				{return 'URI';}
"url("{w}{url}{w}")"				{return 'URI';}
{ident}"("							{return "FUNCTION";}

{keyframes}							{return 'KEYFRAMES';}

{string}							{return 'STRING';}
{ident}								{return 'IDENT';}

"#"{name}							{return 'HASH';}

"@import"							{return 'IMPORT_SYM';}
"@page"								{return 'PAGE_SYM';}
"@media"							{return 'MEDIA_SYM';}
"@font-face"						{return 'FONT_FACE_SYM';}
"@charset"							{return 'CHARSET_SYM';}
"@namespace"						{return 'NAMESPACE_SYM';}

{num}"em"							{return 'EMS';}
{num}"ex"							{return 'EXS';}
{num}"px"							{return 'LENGTH';}
{num}"cm"							{return 'LENGTH';}
{num}"mm"							{return 'LENGTH';}
{num}"in"							{return 'LENGTH';}
{num}"pt"							{return 'LENGTH';}
{num}"pc"							{return 'LENGTH';}
{num}"deg"							{return 'ANGLE';}
{num}"rad"							{return 'ANGLE';}
{num}"grad"							{return 'ANGLE';}
{num}"ms"							{return 'TIME';}
{num}"s"							{return 'TIME';}
{num}"Hz"							{return 'FREQ';}
{num}"kHz"							{return 'FREQ';}
{num}{ident}						{return 'DIMEN';}
{num}"%"							{return 'PERCENTAGE';}
{num}								{return 'NUMBER';}

U\+{range}							{return 'UNICODERANGE';}
U\+{h}{1,6}-{h}{1,6}				{return 'UNICODERANGE';}

.									{return yytext;}
