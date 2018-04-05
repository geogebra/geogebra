%start stylesheet

%%

stylesheet
  : charset space_cdata_list import_list namespace_list general_list
  	%{
  		$$ = {};
  		if ( $1 )
  		  	$$["charset"]	= $1;
  		if ( $3 )
  			$$["imports"]	= $3;
  		if ( $4 )
  			$$["namespaces"]	= $4;
  		if ( $5 )
  			$$["rulelist"]	= $5;

  		return $$;
  	%}
  ;
charset
  : CHARSET_SYM wempty STRING wempty ';'	-> $3
  |											-> ""
  ;
import_list
  : import_item
  	%{
  		$$ = [];
  		if ( $1 !== null )
  			$$.push ( $1 );
  	%}
  | import_list import_item
  	%{
  		$$ = $1;
  		if ( $2 !== null )
  			$$.push ( $2 );
  	%}
  |							-> null
  ;
import_item
  : import					-> $1
  | space_cdata_list		-> null
  ;
import
  : IMPORT_SYM wempty string_or_uri media_query_list ';' wempty
  	%{
  		$$ = {
  			"import": $3
  		};

  		if ( $4 != null )
	  		$$[ "mediaqueries" ] = $4;
  	%}
  ;
namespace_list
  : namespace_item
  	%{
  		$$ = [];
  		if ( $1 !== null )
  			$$.push ( $1 );
  	%}
  | namespace_list namespace_item
  	%{
  		$$ = $1;
  		if ( $2 !== null )
  			$$.push ( $2 );
  	%}
  |							-> null
  ;
namespace_item
  : namespace				-> $1
  | space_cdata_list		-> null
  ;
namespace
  : NAMESPACE_SYM wempty namespace_prefix string_or_uri ';' wempty
  	%{
  		$$ = {
  			"namespace": $4
  		};
  		
  		if ( $3 )
	  		$$["prefix"] = $3;
  	%}
  ;
namespace_prefix
  : IDENT wempty			-> $1
  |	wempty					-> null
  ;
string_or_uri
  : STRING wempty			-> $1
  | URI wempty				-> $1
  ;
general_list
  : general_item
  	%{
  		$$ = [];
  		if ( $1 !== null )
  			$$.push ( $1 );
  	%}
  | general_list general_item
  	%{
  		$$ = $1;
  		$$.push( $2 );
  	%}
  |							-> null
  ;
general_item
  : ruleset					-> $1
  | media					-> $1
  | page					-> $1
  | font_face				-> $1
  | keyframes				-> $1
  | space_cdata_list		-> null
  ;
media
  : MEDIA_SYM wempty media_query_list '{' wempty general_list '}' wempty		-> { "type": "media", "mediaqueries" : $3, "children": $6 }
  ;
media_query_list
  : media_query													-> $1
  | media_query_list media_query								-> $1 + ' ' + $2
  | media_query_list media_combinator media_query				-> $1 + $2 + $3
  |																-> null
  ;
media_combinator
  : '('	wempty			-> ' ' + $1				/* cwdoh; for beatify */
  | ')'	wempty			-> $1
  | ':'	wempty			-> $1
  | ','	wempty			-> ", "
  | whitespace			-> ' '
  ;
media_query
  : expr	-> $1
  |			-> ""
  ;
page
  : PAGE_SYM wempty page_ident pseudo_page wempty '{' wempty declaration_list '}' wempty		->	{ "id": $3, "pseudo": $4, "declarations": $8 }
  ;
page_ident
  : IDENT			-> $1
  |					-> ""
  ;
pseudo_page
  : ':' IDENT		-> $1 + $2
  |					-> ""
  ;
font_face
  : FONT_FACE_SYM wempty '{' wempty declaration_list '}' wempty			-> { "type": "fontface", "declarations": $5 }
  ;
unary_operator
  : '-'							-> $1
  | '+'							-> $1
  ;
property
  : IDENT wempty				-> $1
  | '*' IDENT wempty			-> $1 + $2			/* cwdoh; */
  ;
ruleset
  : selector_list '{' wempty declaration_list '}' wempty		-> { "type": "style", "selector": $1, "declarations": $4 }
  ;
selector_list
  : selector									-> $1
  | selector_list ',' wempty selector			-> $1 + $2 + ' ' + $4
  ;
selector
  : simple_selector								-> $1
  | selector combinator simple_selector			-> $1 + $2 + $3
  ;
combinator
  : '+' wempty					-> $1
  | '>' wempty					-> $1
  | /* empty */					-> ""
  ;
simple_selector
  : simple_selector_atom_list whitespace					-> $1 + " "
  | element_name simple_selector_atom_list wempty		-> $1 + $2
  ;
simple_selector_atom_list
  : simple_selector_atom								-> $1
  | simple_selector_atom_list simple_selector_atom		-> $1 + $2
  |														-> ""
  ;
simple_selector_atom
  : HASH			-> $1
  | class			-> $1
  | attrib			-> $1
  | pseudo			-> $1
  ;
class
  : '.' IDENT		-> $1 + $2
  ;
element_name
  : IDENT			-> $1
  | '*'				-> $1
  ;
attrib
  : '[' wempty IDENT wempty ']'													-> $1 + $3 + $5
  | '[' wempty IDENT wempty attrib_operator wempty attrib_value wempty ']'		-> $1 + $3 + $5 + $6 + $7 + $9
  ;
attrib_operator
  : '='										-> $1
  | INCLUDES								-> $1
  | DASHMATCH								-> $1
  | PREFIXMATCH								-> $1
  | SUFFIXMATCH								-> $1
  | SUBSTRINGMATCH							-> $1
  ;
attrib_value
  : IDENT									-> $1
  | STRING									-> $1
  ;
pseudo
  : ':' IDENT										-> $1 + $2
  | ':' FUNCTION wempty IDENT wempty ')'			-> $1 + $2 + $4 + $6
  | ':' FUNCTION wempty attrib wempty ')'			-> $1 + $2 + $4 + $6		/* cwdoh; modern browsers allow attrib in pseudo function? */
  | ':' ':' IDENT									-> $1 + $2 + $3				/* cwdoh; is "::" moz extension? */
  ;

declaration_list
  : declaration_parts
  	%{
  		$$ = {};
  		if ( $1 !== null ) {
        if(!$$[ $1[0] ]){
          $$[ $1[0] ] = $1[1];
        } else if(Object.prototype.toString.call($$[ $1[0] ]) === '[object Array]') {
          $$[ $1[0] ].push($1[1]);
        } else {
          $$[ $1[0] ] = [ $$[ $1[0] ], $1[1] ];
        }
  		}
  	%}
  | declaration_list declaration_parts
  	%{
  		$$ = $1;
  		if ( $2 !== null ) {
        if(!$$[ $2[0] ]){
          $$[ $2[0] ] = $2[1];
        } else if(Object.prototype.toString.call($$[ $2[0] ]) === '[object Array]') {
          $$[ $2[0] ].push($2[1]);
        } else {
          $$[ $2[0] ] = [ $$[ $2[0] ], $2[1] ];
        }
	  	}
  	%}
  ;
declaration_parts
  : declaration 		-> $1
  | ';'					-> null
  | wempty				-> null
  ;
declaration
  : property ':' wempty expr wempty					-> [ $1, $4 ]
  | property ':' wempty expr IMPORTANT_SYM wempty	-> [ $1, $4 + " !important" ]
  | /* empty */										-> null
  ;
expr
  : term									-> $1
  | expr operator term						-> $1 + $2 + $3
  | expr term								-> $1 + ' ' + $2
  ;
term
  : computable_term							-> $1
  | unary_operator computable_term			-> $1 + $2
  | string_term								-> $1
  ;
computable_term
  : NUMBER wempty							-> $1
  | PERCENTAGE wempty						-> $1
  | LENGTH wempty							-> $1
  | EMS wempty								-> $1
  | EXS wempty								-> $1
  | ANGLE wempty							-> $1
  | TIME wempty								-> $1
  | FREQ wempty								-> $1
  | FUNCTION wempty expr ')' wempty			-> $1 + $3 + $4
  ;
string_term
  : STRING wempty							-> $1
  | IDENT wempty							-> $1
  | URI wempty								-> $1
  | UNICODERANGE wempty						-> $1
  | hexcolor	 							-> $1
  ;
operator
  : '/' wempty					-> $1
  | ',' wempty					-> $1
  | '=' wempty					-> $1
  |	/* empty */					-> ""
  ;
hexcolor
  : HASH wempty					-> $1
  ;
whitespace
  : S					-> ' '
  | whitespace S		-> ' '
  ;
wempty
  : whitespace		-> $1
  |					-> ""
  ;
space_cdata_list
  : space_cdata							-> null
  | space_cdata_list space_cdata		-> null
  |
  ;
space_cdata
  : S			-> null
  | CDO			-> null
  | CDC			-> null
  ;
keyframes
  : keyframe_symbol IDENT wempty '{' wempty keyframe_list '}' wempty		-> { "type": "keyframes", "id": $2,	"keyframes": $6, "prefix": $1 }
  ;
keyframe_list
  : keyframe					-> [ $1 ]
  | keyframe_list keyframe
  	%{
  		$$ = $1;
  		$$.push( $2 );
  	%}
  |								-> []
  ;
keyframe
  : keyframe_offset_list '{' wempty declaration_list '}' wempty			-> { "type": "keyframe", "offset": $1, "declarations": $4 }
  ;
keyframe_offset_list
  : keyframe_offset wempty								-> $1
  | keyframe_offset_list ',' keyframe_offset wempty		-> $1 + ", " + $2
  ;
keyframe_offset
  : IDENT						-> $1
  | STRING						-> $1
  | PERCENTAGE					-> $1
  ;

keyframe_symbol
  : KEYFRAMES wempty			-> $1.split( new RegExp("@([-a-zA-Z0-9]*)keyframes", "g") )[1]		/* only prefix */
  ;
