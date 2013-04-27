 /* -*- mode: C++; compile-command: "flex input_lexer.ll && make input_lexer.o " -*- */
/** @file input_lexer.ll
 *
 *  Lexical analyzer definition for reading expressions.
 *  Note Maple input should be processed replacing # with // and { } for set
 *  This file must be processed with flex. */

/*
 *  Copyright (C) 2001,10 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *  The very first version was inspired by GiNaC lexer
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


/*
 * The lexer will first check for static patterns and strings (defined below)
 * If a match is not found, it calls find_or_make_symbol
 * This function looks first if the string should be translated
 * (e.g. add a prefix from the export table)
 * then look in lexer_functions for a match, then look in sym_tab
 * if not found in sym_tab, a new identificateur is created & added in sym_tab
 * Functions in lexer_functions are added during the construction
 * of the corresponding unary_functions using lexer_functions_register
 */


/*
 *  Definitions
 */

%{
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#include <iostream>
#include <stdexcept>

#include "gen.h"
#include "input_lexer.h"
#include "help.h"
#include "identificateur.h"
#include "usual.h"
#include "derive.h"
#include "series.h"
#include "intg.h"
#include "sym2poly.h"
#include "moyal.h"
#include "subst.h"
#include "vecteur.h"
#include "modpoly.h"
#include "lin.h"
#include "solve.h"
#include "ifactor.h"
#include "alg_ext.h"
#include "gauss.h"
#include "isom.h"
#include "plot.h"
#include "prog.h"
#include "rpn.h"
#include "ezgcd.h"
#include "tex.h"
#include "risch.h"
#include "input_parser.h"    

#if defined(RTOS_THREADX) || defined(__MINGW_H)
  int isatty (int ){ return 0; }
#endif

  using namespace std;
  using namespace giac;
  void giac_yyset_column (int  column_no , yyscan_t yyscanner);
  int giac_yyget_column (yyscan_t yyscanner);
#define YY_USER_ACTION giac_yyset_column(giac_yyget_column(yyscanner)+yyleng,yyscanner);
#define YY_USER_INIT giac_yyset_column(1,yyscanner);

#ifndef NO_NAMESPACE_GIAC
  namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

    const char invalid_name[]="Invalid name";
    void increment_lexer_line_number_setcol(yyscan_t yyscanner,GIAC_CONTEXT){
      giac_yyset_column(1,yyscanner);
      increment_lexer_line_number(contextptr);
    }
    bool doing_insmod = false;

    sym_tab & syms(){
      static sym_tab * ans=new sym_tab;
      return * ans;
    }

    bool builtin_lexer_functions_sorted = false;

    map_charptr_gen & lexer_functions(){
      static map_charptr_gen * ans=0;
      if (!ans){
	ans = new map_charptr_gen;
	doing_insmod=false;
	builtin_lexer_functions_sorted=false;
      }
      return * ans;
    }


#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS
    // gen alias for static initialization on 32 bits processor
    struct charptr_gen_unary {
      const char * s;
      unsigned char type;  // see dispatch.h
      signed char subtype;
      unsigned short reserved; 
      unsigned long _FUNC_; // unary_function_ptr *
    };

    const charptr_gen_unary builtin_lexer_functions[] ={
#if defined(GIAC_HAS_STO_38) && defined(CAS38_DISABLED)
#include "static_lexer_38.h"
#else
#include "static_lexer.h"
#endif
    };

    // return true/false to tell if s is recognized. return the appropriate gen if true
    bool CasIsBuildInFunction(char const *s, gen &g){ 
      // binary search in builtin_lexer_functions
      int i=0, j=builtin_lexer_functions_number-1;
      int cmp;
      cmp= strcmp(s,builtin_lexer_functions[i].s);
      if (cmp==0) goto found; if (cmp<0) return false;
      cmp= strcmp(s,builtin_lexer_functions[j].s);
      if (cmp==0) { i=j; goto found; } if (cmp>0) return false;
      while (1){
        if (i+1>=j) return false;
        int mid= (i+j)/2;
        cmp= strcmp(s,builtin_lexer_functions[mid].s);
        if (cmp==0) { i=mid; goto found; } 
        if (cmp>0) i= mid; else j=mid;
      }
    found:
      g= gen(int(builtin_lexer_functions_[i]+builtin_lexer_functions[i]._FUNC_));
      g= gen(*g._FUNCptr);
      return true;
    }

    // Array added because GH compiler stores builtin_lexer_functions in RAM
    const unsigned long builtin_lexer_functions_[]={
#if defined(GIAC_HAS_STO_38) && defined(CAS38_DISABLED)
#include "static_lexer_38_.h"
#else
#include "static_lexer_.h"
#endif
    };

    const unsigned builtin_lexer_functions_number=sizeof(builtin_lexer_functions)/sizeof(charptr_gen_unary);

    charptr_gen * builtin_lexer_functions_begin(){
      return (charptr_gen *) builtin_lexer_functions;
    }

    charptr_gen * builtin_lexer_functions_end(){
      return builtin_lexer_functions_begin()+builtin_lexer_functions_number;
    }

#else
    unsigned builtin_lexer_functions_number;
    charptr_gen * builtin_lexer_functions(){
      static charptr_gen * ans=0;
      if (!ans){
	ans = new charptr_gen[1300];
	builtin_lexer_functions_number=0;
      }
      return ans;
    }

    charptr_gen * builtin_lexer_functions_begin(){
      return builtin_lexer_functions();
    }

    charptr_gen * builtin_lexer_functions_end(){
      return builtin_lexer_functions()+builtin_lexer_functions_number;
    }

    const unsigned long * const builtin_lexer_functions_=0;
    
#endif

    std::vector<int> & lexer_localization_vector(){
      static std::vector<int> * ans=new  std::vector<int>;
      return *ans;
    }

    std::map<std::string,std::string> & lexer_localization_map(){
      static std::map<std::string,std::string> * ans = new std::map<std::string,std::string>;
      return * ans;
    }
    std::multimap<std::string,giac::localized_string> & back_lexer_localization_map(){
      static std::multimap<std::string,giac::localized_string> * ans= new std::multimap<std::string,giac::localized_string>;
      return * ans;
    }

    // lexer_localization_vector() is the list of languages currently translated
    // lexer_localization_map translates keywords from the locale to giac 
    // back_lexer_localization_map() lists for a giac keyword the translations

    std::map<std::string,std::vector<std::string> > & lexer_translator (){
      static std::map<std::string,std::vector<std::string> > * ans = new std::map<std::string,std::vector<std::string> >;
      return * ans;
    }
    // lexer_translator will be updated when export/with is called
    // To each string (w/o ::) in a given library, 
    // If it exists, we push_back the full string (with ::)
    // If not we create a vector with the full string
    // If a library is unexported we remove the corresponding entry in the 
    // vector and remove the entry if the vector is empty
    std::map<std::string,std::vector<std::string> > & library_functions (){
      static std::map<std::string,std::vector<std::string> > * ans=new std::map<std::string,std::vector<std::string> >;
      return *ans;
    }

    // First string is the library name, second is the vector of function names
    // User defined relations
    vector<user_function> & registered_lexer_functions(){
      static vector<user_function> * ans = 0;
      if (!ans){
	ans = new vector<user_function>;
	// ans->reserve(50);
      }
      return * ans;
    }

    /* integer values */
    struct lexer_tab_int_type {
      const char * keyword;
      unsigned char status;
      int value;
      signed char subtype;
      short int return_value;
    };

    bool tri1(const lexer_tab_int_type & a,const lexer_tab_int_type & b){
      int res= strcmp(a.keyword,b.keyword);
      return res<0;
    }

    bool tri2(const char * a,const char * b){
      return strcmp(a,b)<0;
    }

    const lexer_tab_int_type lexer_tab_int_values []={
#ifdef GIAC_HAS_STO_38
#include "lexer_tab38_int.h"
#else
#include "lexer_tab_int.h"
#endif
    };

    const lexer_tab_int_type * const lexer_tab_int_values_begin = lexer_tab_int_values;
    const unsigned lexer_tab_int_values_n=sizeof(lexer_tab_int_values)/sizeof(lexer_tab_int_type);
    const lexer_tab_int_type * const lexer_tab_int_values_end = lexer_tab_int_values+lexer_tab_int_values_n;
#ifndef NO_NAMESPACE_GIAC
  } // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

%}

%option reentrant bison-bridge
%option outfile="input_lexer.cc"
%option header-file="lexer.h"
%option noyywrap
%option prefix="giac_yy"

	/* Abbreviations */
D	[0-9]
E	[eE][eE]?[-+]?{D}+
A	[a-zA-Z~\200-\355\357-\376] 
AN	[0-9a-zA-Z_~ ?\200-\355\357-\376] 
        /* If changed, modify isalphan in help.cc FIXME is . allowed inside alphanumeric ? answer NO */
%x comment
%x comment_hash
%x str
%x backquote
/*
 *  Lexical rules
 */

%%

[ \t\\]+			/* skip whitespace */
\n                increment_lexer_line_number_setcol(yyscanner,yyextra); //cerr << "Scanning line " << lexer_line_number(yyextra) << endl;
  /* Strings */
  /* \"[^\"]*\"        yylval = string2gen( giac_yytext); return T_STRING; */
\"                BEGIN(str); comment_s("",yyextra);
<str>\"\"         increment_comment_s('"',yyextra);
<str>\"        {  index_status(yyextra)=1; BEGIN(INITIAL); 
                  (*yylval)=string2gen(comment_s(yyextra),false); 
                  return T_STRING; }
<str>\n        increment_comment_s('\n',yyextra); increment_lexer_line_number_setcol(yyscanner,yyextra);
<str>\\[0-7]{1,3} {
                   /* octal escape sequence */
                   int result;
                   (void) sscanf( yytext + 1, "%o", &result );
                   increment_comment_s(char(result & 0xff),yyextra);
                   }
<str>\\[0-9]+      {
                   /* generate error - bad escape sequence; something
                    * like '\48' or '\0777777'
                    */
                   }
<str>\\n  increment_comment_s('\n',yyextra);
<str>\\t  increment_comment_s('\t',yyextra);
<str>\\r  increment_comment_s('\r',yyextra);
<str>\\b  increment_comment_s('\b',yyextra);
<str>\\f  increment_comment_s('\f',yyextra);
<str>\\(.|\n)  increment_comment_s(yytext[1],yyextra);
<str>[^\\\n\"]+ increment_comment_s(yytext,yyextra);
`                  if (rpn_mode(yyextra)){ index_status(yyextra)=0; return T_ACCENTGRAVE; } else { BEGIN(backquote); comment_s("",yyextra); }
<backquote>\n      increment_comment_s('\n',yyextra); increment_lexer_line_number_setcol(yyscanner,yyextra);
<backquote>[^\n`]+       increment_comment_s(yytext,yyextra);
<backquote>`       {  index_status(yyextra)=1; BEGIN(INITIAL); 
  return find_or_make_symbol(comment_s(yyextra),(*yylval),yyscanner,true,yyextra); }

"://"[^\n]*\n      index_status(yyextra)=0; increment_lexer_line_number_setcol(yyscanner,yyextra);
"//"[^\n]*\n      index_status(yyextra)=0; increment_lexer_line_number_setcol(yyscanner,yyextra);/* (*yylval) = string2gen('"'+string(giac_yytext).substr(2,string(giac_yytext).size()-3)+'"');   return T_COMMENT; */
"/*"              BEGIN(comment); comment_s(yyextra)="";

<comment>[^*\n]*        comment_s(yyextra)+=yytext; /* eat anything that's not a '*' */
<comment>"*"+[^*/\n]*   comment_s(yyextra)+=yytext; /* eat up '*'s not followed by '/'s */
<comment>\n             comment_s(yyextra) += '\n'; increment_lexer_line_number_setcol(yyscanner,yyextra); cerr << "(Comment) scanning line " << lexer_line_number(yyextra) << endl;
<comment>"*"+"/"        BEGIN(INITIAL); index_status(yyextra)=0; /* (*yylval) = string2gen(comment_s(yyextra),false); return T_COMMENT; */
"#++"[^*]*"++#"         index_status(yyextra)=0; /* (*yylval) = string2gen('"'+string(yytext).substr(3,string(yytext).size()-6)+'"'); return T_COMMENT; */
"#--"[^*]*"--#"         index_status(yyextra)=0; /* (*yylval) = string2gen('"'+string(yytext).substr(3,string(yytext).size()-6)+'"'); return T_COMMENT; */

"?"                     if (index_status(yyextra)) return T_INTERROGATION; if (calc_mode(yyextra)==1){ *yylval=undef; return T_SYMBOL;}  return T_HELP;
"_"                     opened_quote(yyextra) |= 2; return T_UNIT;
"'"                     if (opened_quote(yyextra) & 1) { opened_quote(yyextra) &= 0x7ffffffe; return T_QUOTE; } if (index_status(yyextra) && !in_rpn(yyextra) && xcas_mode(yyextra)!= 1) return T_PRIME; opened_quote(yyextra) |= 1; return T_QUOTE;
";"			index_status(yyextra)=0; if (xcas_mode(yyextra)==3) return TI_SEMI; (*yylval)=0; return T_SEMI;
"§"                  index_status(yyextra)=0; if (xcas_mode(yyextra)==3) return T_SEMI; return TI_SEMI;
":"			if (spread_formula(yyextra)) return T_DEUXPOINTS; if ( xcas_mode(yyextra)==3 ) { index_status(yyextra)=0; return TI_DEUXPOINTS; }  index_status(yyextra)=0; if (xcas_mode(yyextra)>0) { (*yylval)=1; return T_SEMI; } else return T_DEUXPOINTS;
":;"                    (*yylval)=1; return T_SEMI;
"::"                    index_status(yyextra)=0;return T_DOUBLE_DEUX_POINTS;

			/* special values */

"θ"	index_status(yyextra)=1; (*yylval)=theta__IDNT_e; return T_SYMBOL;
"i"			index_status(yyextra)=1; if (xcas_mode(yyextra) > 0 || !i_sqrt_minus1(yyextra)) { (*yylval)=i__IDNT_e; return T_SYMBOL; } else { (*yylval) = cst_i; return T_LITERAL;};
"ί"                      index_status(yyextra)=1; (*yylval) = cst_i; return T_LITERAL;
\xa1                    index_status(yyextra)=1; (*yylval) = cst_i; return T_LITERAL;
  /* \xef\xbd\x89            index_status(yyextra)=1; (*yylval) = cst_i; return T_LITERAL; */
\xe2\x81\xb1            index_status(yyextra)=1; (*yylval) = cst_i; return T_LITERAL;
"I"                     index_status(yyextra)=1; if (xcas_mode(yyextra)==0 || xcas_mode(yyextra)==3 || rpn_mode(yyextra)) { return find_or_make_symbol(yytext,(*yylval),yyscanner,true,yyextra); } else { (*yylval) = cst_i; return T_LITERAL; };
"%i"			index_status(yyextra)=1; (*yylval) = cst_i; return T_LITERAL;
"%e"			index_status(yyextra)=1; (*yylval) = symbolic(at_exp,1); return T_LITERAL;
"%pi"			index_status(yyextra)=1; (*yylval) = cst_pi; return T_LITERAL;
"pi"			index_status(yyextra)=1; (*yylval) = cst_pi; return T_LITERAL;
"π"			index_status(yyextra)=1; (*yylval) = cst_pi; return T_LITERAL;
"Pi"			index_status(yyextra)=1; (*yylval) = cst_pi; return T_LITERAL;
"PI"			index_status(yyextra)=1; (*yylval) = cst_pi; return T_LITERAL;
"euler_gamma"		index_status(yyextra)=1; (*yylval) = cst_euler_gamma; return T_LITERAL;
"infinity"		index_status(yyextra)=1; (*yylval) = unsigned_inf; return T_LITERAL;
"∞"		index_status(yyextra)=1; (*yylval) = unsigned_inf; return T_LITERAL;
"inf"		index_status(yyextra)=1; (*yylval) = plus_inf; return T_LITERAL;
"unsigned_inf"		index_status(yyextra)=1; (*yylval) = unsigned_inf; return T_LITERAL;
"plus_inf"		index_status(yyextra)=1; (*yylval) = plus_inf; return T_LITERAL;
"minus_inf"		index_status(yyextra)=1; (*yylval) = minus_inf; return T_LITERAL;
"undef"		        index_status(yyextra)=1; (*yylval) = undef; return T_LITERAL;
"ÿ"                     return T_END_INPUT;

               /* integer values */
"operator"              if (xcas_mode(yyextra)==2){ (*yylval) = gen(at_user_operator,6); index_status(yyextra)=0; return T_UNARY_OP; }  index_status(yyextra)=0; (*yylval) = _FUNC; (*yylval).subtype=_INT_TYPE; return T_TYPE_ID;
"list"         if (xcas_mode(yyextra)==3) { index_status(yyextra)=1; return find_or_make_symbol(yytext,(*yylval),yyscanner,true,yyextra); } index_status(yyextra)=0; (*yylval) = _MAPLE_LIST ; (*yylval).subtype=_INT_MAPLECONVERSION ;return T_TYPE_ID;


    /* vector/polynom/matrice delimiters */
"seq["              (*yylval) = _SEQ__VECT; return T_VECT_DISPATCH;
"set["              (*yylval) = _SET__VECT; return T_VECT_DISPATCH;
"list[" index_status(yyextra)=0; (*yylval) = _LIST__VECT; return T_VECT_DISPATCH;
"list(" index_status(yyextra)=0; (*yylval) = _LIST__VECT; return T_BEGIN_PAR;
"rpn_func["         (*yylval) = _RPN_FUNC__VECT; return T_VECT_DISPATCH;
"group["            (*yylval) = _GROUP__VECT; return T_VECT_DISPATCH;
"line["             (*yylval) = _LINE__VECT; return T_VECT_DISPATCH;
"vector["           (*yylval) = _VECTOR__VECT; return T_VECT_DISPATCH;
"matrix["           (*yylval) = _MATRIX__VECT; return T_VECT_DISPATCH;
"pnt["              (*yylval) = _PNT__VECT; return T_VECT_DISPATCH;
"ggbpnt["           (*yylval) = _GGB__VECT; return T_VECT_DISPATCH;
"point["            (*yylval) = _POINT__VECT; return T_VECT_DISPATCH;
"curve["            (*yylval) = _CURVE__VECT; return T_VECT_DISPATCH;
"halfline["         (*yylval) = _HALFLINE__VECT; return T_VECT_DISPATCH;
"poly1["            (*yylval) = _POLY1__VECT; return T_VECT_DISPATCH;
"assume["           (*yylval) = _ASSUME__VECT; return T_VECT_DISPATCH;
"spreadsheet["      (*yylval) = _SPREAD__VECT; return T_VECT_DISPATCH;
"folder["      (*yylval) = _FOLDER__VECT; return T_VECT_DISPATCH;
"polyedre["      (*yylval) = _POLYEDRE__VECT; return T_VECT_DISPATCH;
"rgba["      (*yylval) = _RGBA__VECT; return T_VECT_DISPATCH;
"â¦" index_status(yyextra)=0; (*yylval) = _LIST__VECT; return T_VECT_DISPATCH;
"â¦" index_status(yyextra)=1; return T_VECT_END;
"<"                     index_status(yyextra)=0; (*yylval)=gen(at_inferieur_strict,2);  return T_TEST_EQUAL;
">"                     index_status(yyextra)=0; (*yylval)=gen(at_superieur_strict,2); return T_TEST_EQUAL;
","                     index_status(yyextra)=0; return T_VIRGULE;
",,"                     index_status(yyextra)=0; return T_VIRGULE;
"("                     index_status(yyextra)=0; *yylval = 0; return T_BEGIN_PAR;
")"                     index_status(yyextra)=1; return T_END_PAR;
\[			if (index_status(yyextra)) { index_status(yyextra)=0; return T_INDEX_BEGIN; } else { (*yylval) = 0; return T_VECT_DISPATCH; } ;
\]			index_status(yyextra)=1; return T_VECT_END;
"%["                    index_status(yyextra)=0; (*yylval) = _POLY1__VECT; return T_VECT_DISPATCH; 
"%]"                    index_status(yyextra)=1; return T_VECT_END;
"%%["                   index_status(yyextra)=0; (*yylval) = _MATRIX__VECT; return T_VECT_DISPATCH; 
"%%]"                   index_status(yyextra)=1; return T_VECT_END;
"%%%["                  index_status(yyextra)=0; (*yylval) = _ASSUME__VECT; return T_VECT_DISPATCH; 
"%%%]"                  index_status(yyextra)=1; return T_VECT_END;
    /* geometric delimiters */
"%_("                   index_status(yyextra)=0; (*yylval) = _GROUP__VECT; return T_VECT_DISPATCH;
"%_)"                    index_status(yyextra)=1; return T_VECT_END;
"%%("                   index_status(yyextra)=0; (*yylval) = _LINE__VECT; return T_VECT_DISPATCH; 
"%%)"                   index_status(yyextra)=1; return T_VECT_END;
"%%%("                  index_status(yyextra)=0; (*yylval) = _VECTOR__VECT; return T_VECT_DISPATCH; 
"%%%)"                  index_status(yyextra)=1; return T_VECT_END;
"%%%%("                 index_status(yyextra)=0; (*yylval) = _CURVE__VECT; return T_VECT_DISPATCH; 
"%%%%)"                 index_status(yyextra)=1; return T_VECT_END;
    /* gen delimiters */
"{"                     index_status(yyextra)=0; if (rpn_mode(yyextra) ||calc_mode(yyextra)==1) { (*yylval)=0; return T_VECT_DISPATCH; } if (xcas_mode(yyextra)==3 || abs_calc_mode(yyextra)==38){ (*yylval) = _LIST__VECT;  return T_VECT_DISPATCH; } if (xcas_mode(yyextra) > 0 ){ (*yylval)=_SET__VECT; return T_VECT_DISPATCH; } else return T_BLOC_BEGIN;
"}"                     index_status(yyextra)=1; if (rpn_mode(yyextra) || calc_mode(yyextra)==1) return T_VECT_END; if (xcas_mode(yyextra)==3 || abs_calc_mode(yyextra)==38) return T_VECT_END; if (xcas_mode(yyextra) > 0) return T_VECT_END; else return T_BLOC_END;
"%{"                    index_status(yyextra)=0;  (*yylval)=_SET__VECT; return T_VECT_DISPATCH;
"%}"                    index_status(yyextra)=1; return T_VECT_END;
"%%{"                   index_status(yyextra)=0; return T_ROOTOF_BEGIN;
"%%}"                   index_status(yyextra)=1; return T_ROOTOF_END;
"%%%{"                  index_status(yyextra)=0; return T_SPOLY1_BEGIN;
"%%%}"                  index_status(yyextra)=1; return T_SPOLY1_END;
"<<"                    index_status(yyextra)=0; ++in_rpn(yyextra); return T_RPN_BEGIN;
">>"                    index_status(yyextra)=0; --in_rpn(yyextra); return T_RPN_END;

    /* binary operators */
"->"                    index_status(yyextra)=0; return T_MAPSTO;
"-<"                    (*yylval) = gen(at_couleur,2); index_status(yyextra)=0; return T_INTERVAL;
"=="			index_status(yyextra)=0; (*yylval)=gen(at_same,2); return T_TEST_EQUAL;
"'=='"                  index_status(yyextra)=0; (*yylval)=gen(at_same,2); return T_QUOTED_BINARY;
"_equal"                  index_status(yyextra)=0; (*yylval)=gen(at_same,2); return T_QUOTED_BINARY;
"!="			index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_TEST_EQUAL;
"'!='"                  index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_QUOTED_BINARY;
"<>"			index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_TEST_EQUAL;
"'<>'"                  index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_QUOTED_BINARY;
"_unequal"                  index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_QUOTED_BINARY;
"'<='"                  index_status(yyextra)=0; (*yylval)=gen(at_inferieur_egal,2); return T_QUOTED_BINARY;
"_leequal"                  index_status(yyextra)=0; (*yylval)=gen(at_inferieur_egal,2); return T_QUOTED_BINARY;
"<="			index_status(yyextra)=0; (*yylval)=gen(at_inferieur_egal,2); return T_TEST_EQUAL;
"'<'"                  index_status(yyextra)=0; (*yylval)=gen(at_inferieur_strict,2); return T_QUOTED_BINARY;
"_less"                  index_status(yyextra)=0; (*yylval)=gen(at_inferieur_strict,2); return T_QUOTED_BINARY;
"'>'"                  index_status(yyextra)=0; (*yylval)=gen(at_superieur_strict,2); return T_QUOTED_BINARY;
">="			index_status(yyextra)=0; (*yylval)=gen(at_superieur_egal,2); return T_TEST_EQUAL;
"'>='"                  index_status(yyextra)=0; (*yylval)=gen(at_superieur_egal,2); return T_QUOTED_BINARY;
"="                     spread_formula(yyextra)=!index_status(yyextra); index_status(yyextra)=0; (*yylval)=gen(at_equal,2); return T_EQUAL;
"'='"                  index_status(yyextra)=0; (*yylval)=gen(at_equal,2); return T_QUOTED_BINARY;
"$"                     index_status(yyextra)=0; (*yylval)=gen(at_dollar,2); if (xcas_mode(yyextra)>0) return T_DOLLAR_MAPLE; else return T_DOLLAR;
"%$"                   index_status(yyextra)=0; (*yylval)=gen(at_dollar,2); return T_DOLLAR_MAPLE;
"'$'"                  index_status(yyextra)=0; (*yylval)=gen(at_dollar,2); return T_QUOTED_BINARY;
"_seqgen"                  index_status(yyextra)=0; (*yylval)=gen(at_dollar,2); return T_QUOTED_BINARY;
":="			index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return T_AFFECT;
"':='"                  index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return T_QUOTED_BINARY;
"_assign"                  index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return T_QUOTED_BINARY;
"►"                    index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return TI_STO;
"▶"                index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return TI_STO;
"=>"                    index_status(yyextra)=0; (*yylval)=gen(at_sto,2); return TI_STO;
"=<"                    index_status(yyextra)=0; (*yylval)=gen(at_array_sto,2); return T_AFFECT;
"@"{D}+                   index_status(yyextra)=1; yytext[0]='0'; (*yylval) = symb_double_deux_points(makevecteur(_IDNT_id_at,chartab2gen(yytext,yyextra))); return T_SYMBOL;
"@"                     if (xcas_mode(yyextra)!=3) {index_status(yyextra)=0; (*yylval)=gen(at_compose,2); return T_COMPOSE; } BEGIN(comment_hash);
"@@"                     index_status(yyextra)=0; (*yylval)=gen(at_composepow,2); return T_POW;
"'@@'"                     index_status(yyextra)=0; (*yylval)=gen(at_composepow,2); return T_QUOTED_BINARY;
"_fnest"                     index_status(yyextra)=0; (*yylval)=gen(at_composepow,2); return T_QUOTED_BINARY;
"'@'"                  index_status(yyextra)=0; (*yylval)=gen(at_compose,2); return T_QUOTED_BINARY;
"_fconcat"                  index_status(yyextra)=0; (*yylval)=gen(at_compose,2); return T_QUOTED_BINARY;
"&&"                    index_status(yyextra)=0; (*yylval)=gen(at_and,2); return T_AND_OP;
"AND"                   index_status(yyextra)=0; (*yylval)=gen(at_and,2); return T_AND_OP;
"'&&'"                  index_status(yyextra)=0; (*yylval)=gen(at_and,2); return T_QUOTED_BINARY;
"'and'"                 index_status(yyextra)=0; (*yylval)=gen(at_and,2); return T_QUOTED_BINARY;
"_and"                 index_status(yyextra)=0; (*yylval)=gen(at_and,2); return T_QUOTED_BINARY;
"|"                     index_status(yyextra)=0; (*yylval)=gen(at_tilocal,2); return T_PIPE;
"||"                    index_status(yyextra)=0; (*yylval)=gen(at_ou,2); return T_AND_OP;
"'||'"                  index_status(yyextra)=0; (*yylval)=gen(at_ou,2); return T_QUOTED_BINARY;
"'or'"                  index_status(yyextra)=0; (*yylval)=gen(at_ou,2); return T_QUOTED_BINARY;
"_or"                  index_status(yyextra)=0; (*yylval)=gen(at_ou,2); return T_QUOTED_BINARY;
"OR"                    index_status(yyextra)=0; (*yylval)=gen(at_ou,2); return T_AND_OP;
"xor"                    index_status(yyextra)=0; (*yylval)=gen(at_xor,2); return T_AND_OP;
"_xor"                  index_status(yyextra)=0; (*yylval)=gen(at_xor,2); return T_QUOTED_BINARY;
"'xor'"                  index_status(yyextra)=0; (*yylval)=gen(at_xor,2); return T_QUOTED_BINARY;
"XOR"                    index_status(yyextra)=0; (*yylval)=gen(at_xor,2); return T_AND_OP;
".."                    index_status(yyextra)=0; (*yylval)=gen(at_interval,2); return T_INTERVAL;
"..."                    index_status(yyextra)=0; (*yylval)=gen(at_interval,2); return T_INTERVAL;
"'..'"                  index_status(yyextra)=0; (*yylval)=gen(at_interval,2); return T_QUOTED_BINARY;
"_range"                  index_status(yyextra)=0; (*yylval)=gen(at_interval,2); return T_QUOTED_BINARY;
"!"                     if (xcas_mode(yyextra) || index_status(yyextra)) { (*yylval)=gen(at_factorial); return T_FACTORIAL; } else { index_status(yyextra)=0; (*yylval)=gen(at_not,1); return T_NOT; }

    /* standard functions */
"Ans"                   index_status(yyextra)=1; (*yylval)=symbolic(at_Ans,0); return T_LITERAL;
"+"                     index_status(yyextra)=0; (*yylval)=gen(at_plus,2); return T_PLUS;
"++"                    index_status(yyextra)=0; (*yylval)=gen(at_increment,1); return T_FACTORIAL;
"+="                    index_status(yyextra)=0; (*yylval)=gen(at_increment,1); return T_PLUS;
"--"                    index_status(yyextra)=0; (*yylval)=gen(at_decrement,1); return T_FACTORIAL;
"-="                    index_status(yyextra)=0; (*yylval)=gen(at_decrement,1); return T_PLUS;
".+"                    index_status(yyextra)=0; (*yylval)=gen(at_plus,2); return T_PLUS;
"&"                     index_status(yyextra)=0; (*yylval)=gen(at_plus,2); return T_PLUS;
"√"                     index_status(yyextra)=0; (*yylval)=gen(at_sqrt,2); return T_NOT;
"∡"                     index_status(yyextra)=0; (*yylval)=gen(at_polar_complex,2); return T_MOD;
"²"                     index_status(yyextra)=0; (*yylval)=2; return T_SQ;
"³"                     index_status(yyextra)=0; (*yylval)=3; return T_SQ;
"⁴"                     index_status(yyextra)=0; (*yylval)=4; return T_SQ;
"⁵"                     index_status(yyextra)=0; (*yylval)=5; return T_SQ;
"⁶"                     index_status(yyextra)=0; (*yylval)=6; return T_SQ;
"⁷"                     index_status(yyextra)=0; (*yylval)=7; return T_SQ;
"⁸"                     index_status(yyextra)=0; (*yylval)=8; return T_SQ;
"⁹"                     index_status(yyextra)=0; (*yylval)=9; return T_SQ;
""                    index_status(yyextra)=0; (*yylval)=-1; return T_SQ;
\342\201\262            index_status(yyextra)=0; (*yylval)=-1; return T_SQ;
  /* "','"                   index_status(yyextra)=0; (*yylval)=gen(at_makevector,2); return T_QUOTED_BINARY; commented because of f('a','b') */
"'+'"                   index_status(yyextra)=0; (*yylval)=gen(at_plus,2); return T_QUOTED_BINARY;
"_plus"                   index_status(yyextra)=0; (*yylval)=gen(at_plus,2); return T_QUOTED_BINARY;
"-"                     index_status(yyextra)=0; (*yylval)=gen(at_binary_minus,2); return (calc_mode(yyextra)==38)?T_MOINS38:T_MOINS;
"−"                     index_status(yyextra)=0; if (calc_mode(yyextra)==38){ (*yylval)=gen(at_neg,2); return T_NEG38; } else { cerr << 1 << endl; (*yylval)=gen(at_binary_minus,2); return T_MOINS;}
".-"                     index_status(yyextra)=0; (*yylval)=gen(at_binary_minus,2); return T_MOINS;
"'-'"                   index_status(yyextra)=0; (*yylval)=gen(at_binary_minus,2); return T_QUOTED_BINARY;
"_subtract"                   index_status(yyextra)=0; (*yylval)=gen(at_binary_minus,2); return T_QUOTED_BINARY;
"*"                     index_status(yyextra)=0; (*yylval)=gen(at_prod,2); return T_FOIS;
"⊗"                     index_status(yyextra)=0; (*yylval)=gen(at_cross,2); return T_FOIS;
"*="                    index_status(yyextra)=0; (*yylval)=gen(at_multcrement,1); return T_FOIS;
"."                     index_status(yyextra)=0; if (abs_calc_mode(yyextra)==38){return T_DOUBLE_DEUX_POINTS; } else {(*yylval)=gen(at_prod,2); return T_FOIS;}
"&*"                     index_status(yyextra)=0; (*yylval)=gen(at_ampersand_times,2); return T_FOIS;
"&^"                     index_status(yyextra)=0; (*yylval)=gen(at_quote_pow,2); return T_POW;
".*"                     index_status(yyextra)=0; (*yylval)=gen(at_pointprod,2); return T_FOIS;
"'*'"                   index_status(yyextra)=0; (*yylval)=gen(at_prod,2); return T_QUOTED_BINARY;
"_mult"                   index_status(yyextra)=0; (*yylval)=gen(at_prod,2); return T_QUOTED_BINARY;
"/"                     index_status(yyextra)=0; (*yylval)=gen(at_division,2); return T_DIV;
"/="                    index_status(yyextra)=0; (*yylval)=gen(at_divcrement,1); return T_DIV;
"./"                     index_status(yyextra)=0; (*yylval)=gen(at_pointdivision,2); return T_DIV;
"'/'"                   index_status(yyextra)=0; (*yylval)=gen(at_division,2); return T_QUOTED_BINARY;
"_divide"                   index_status(yyextra)=0; (*yylval)=gen(at_division,2); return T_QUOTED_BINARY;
"%"                     index_status(yyextra)=0; if (abs_calc_mode(yyextra)==38){ (*yylval)=gen(at_PERCENT); return T_UNARY_OP_38; } if (xcas_mode(yyextra)==3) { (*yylval)=gen(at_pourcent); return T_FACTORIAL; } if (xcas_mode(yyextra)==1) { (*yylval)=symbolic(at_ans,vecteur(0)); return T_NUMBER; }  if (xcas_mode(yyextra)) (*yylval)=gen(at_irem,2); else (*yylval)=0; return T_MOD;
"%%"                     index_status(yyextra)=0; if (xcas_mode(yyextra)==0){ (*yylval)=gen(at_iquorem,2); return T_MOD;} (*yylval)=symbolic(at_ans,-2); return T_NUMBER; 
  /* \xe2\x88\xa1             index_status(yyextra)=0; (*yylval)=gen(at_polar_complex,2); return T_MOD; */
"%%%"                     if (xcas_mode(yyextra)==0){ (*yylval)=gen(at_quorem,2); return T_MOD;} index_status(yyextra)=0; (*yylval)=symbolic(at_ans,-3); return T_NUMBER; 
"'%'"                  index_status(yyextra)=0; (*yylval)=gen(at_irem,2); return T_QUOTED_BINARY;
"mod"                   index_status(yyextra)=0; if (xcas_mode(yyextra)==3) { (*yylval)=gen(at_irem,2); return T_UNARY_OP; } else { if (xcas_mode(yyextra)) (*yylval)=gen(at_irem,2); else (*yylval)=0; return T_MOD; }
"'mod'"                  index_status(yyextra)=0; (*yylval)=gen(at_irem,2); return T_QUOTED_BINARY;
"_mod"                  index_status(yyextra)=0; (*yylval)=gen(at_irem,2); return T_QUOTED_BINARY;
  /* "MOD"                   index_status(yyextra)=0; return T_MOD; */
"^"                     index_status(yyextra)=0; (*yylval)=gen(at_pow,2); return T_POW;
"pow"		         (*yylval) = gen(at_pow,2); index_status(yyextra)=0; return T_UNARY_OP;
"**"                     index_status(yyextra)=0; (*yylval)=gen(at_pow,2); return T_POW;
".^"                     index_status(yyextra)=0; (*yylval)=gen(at_pointpow,2); return T_POW;
"'^'"                   index_status(yyextra)=0; (*yylval)=gen(at_pow,2); return T_QUOTED_BINARY;
"_power"                   index_status(yyextra)=0; (*yylval)=gen(at_pow,2); return T_QUOTED_BINARY;
"Digits"                  (*yylval) = gen(at_Digits,0); index_status(yyextra)=0; return T_DIGITS;
"HDigits"                  (*yylval) = gen(at_HDigits,0); index_status(yyextra)=0; return T_DIGITS;
"HAngle"                  (*yylval) = gen(at_HAngle,0); index_status(yyextra)=0; return T_DIGITS;
"HFormat"                  (*yylval) = gen(at_HFormat,0); index_status(yyextra)=0; return T_DIGITS;
"HComplex"                  (*yylval) = gen(at_HComplex,0); index_status(yyextra)=0; return T_DIGITS;
"HLanguage"                  (*yylval) = gen(at_HLanguage,0); index_status(yyextra)=0; return T_DIGITS;
"DIGITS"                  (*yylval) = gen(at_Digits,0); index_status(yyextra)=0; return T_DIGITS;
"threads"      (*yylval) = gen(at_threads,0) ; index_status(yyextra)=0; return T_DIGITS;
"scientific_format"		        (*yylval) = gen(at_scientific_format,0); index_status(yyextra)=0; return T_DIGITS;
"angle_radian"		(*yylval) = gen(at_angle_radian,0); index_status(yyextra)=0; return T_DIGITS;
"approx_mode"		(*yylval) = gen(at_approx_mode,0); index_status(yyextra)=0; return T_DIGITS;
"all_trig_solutions"		(*yylval) = gen(at_all_trig_solutions,1); index_status(yyextra)=0; return T_DIGITS;
"ntl_on"		(*yylval) = gen(at_ntl_on,1); index_status(yyextra)=0; return T_DIGITS;
"complex_mode"		(*yylval) = gen(at_complex_mode,1); index_status(yyextra)=0; return T_DIGITS;
"complex_variables"	(*yylval) = gen(at_complex_variables,0); index_status(yyextra)=0; return T_DIGITS;
"epsilon"               (*yylval) = gen(at_epsilon,0); index_status(yyextra)=0; return T_DIGITS;
"proba_epsilon"               (*yylval) = gen(at_proba_epsilon,0); index_status(yyextra)=0; return T_DIGITS;

"arccos"		(*yylval) = gen(at_acos,1); index_status(yyextra)=0; return T_UNARY_OP;
"randnorm"		(*yylval) = gen(at_randNorm,1); index_status(yyextra)=0; return T_UNARY_OP;
"arccosh"		(*yylval) = gen(at_acosh,1); index_status(yyextra)=0; return T_UNARY_OP;
"'args'"                index_status(yyextra)=0; (*yylval)=gen(at_args,0); return T_QUOTED_BINARY;
"arcsin"		(*yylval) = gen(at_asin,1); index_status(yyextra)=0; return T_UNARY_OP;
"arcsinh"		(*yylval) = gen(at_asinh,1); index_status(yyextra)=0; return T_UNARY_OP;
"at"			(*yylval) = gen(at_at,2); index_status(yyextra)=0; return T_UNARY_OP;
"arctan"		(*yylval) = gen(at_atan,1); index_status(yyextra)=0; return T_UNARY_OP;
"arctanh"		(*yylval) = gen(at_atanh,1); index_status(yyextra)=0; return T_UNARY_OP;
"backquote"		(*yylval) = gen(at_backquote,1); index_status(yyextra)=0; return T_UNARY_OP;
"bloc"  		(*yylval) = gen(at_bloc,1); index_status(yyextra)=0; return T_UNARY_OP;
"BREAK"  		index_status(yyextra)=0; (*yylval)=gen(at_break,0); return T_BREAK;
"CASE"                  index_status(yyextra)=0; if (abs_calc_mode(yyextra)==38) return T_CASE38; else return T_CASE;
"CONT"		        (*yylval) = gen(at_cont,1); index_status(yyextra)=0; return T_UNARY_OP;
"DEBUG"		        (*yylval) = gen(at_debug,1); index_status(yyextra)=0; return T_UNARY_OP;
"derive"		(*yylval) = gen(at_derive,2); index_status(yyextra)=0; return T_UNARY_OP;
"D"  		if (xcas_mode(yyextra)==1 || xcas_mode(yyextra)==2) { (*yylval) = gen(at_function_diff,1); index_status(yyextra)=1; return T_UNARY_OP;} else { index_status(yyextra)=1; return find_or_make_symbol(yytext,(*yylval),yyscanner,true,yyextra); }
"e"                     if (xcas_mode(yyextra)==1 || xcas_mode(yyextra)==2) { (*yylval)=e__IDNT_e; }else (*yylval)=symbolic(at_exp,1); return T_NUMBER;
"ℯ"                     (*yylval)=symbolic(at_exp,1); return T_NUMBER;
"equal"			(*yylval) = gen(at_equal,2); index_status(yyextra)=0; return T_UNARY_OP;
"error"		        index_status(yyextra)=0; (*yylval)=gen(at_throw,1); return T_RETURN;
"erase"                 (*yylval) = gen(at_erase,0); index_status(yyextra)=0; return T_UNARY_OP;
"ERROR"		        index_status(yyextra)=0; (*yylval)=gen(at_throw,1); return T_RETURN;
"expand"                if (xcas_mode(yyextra)==3) (*yylval)=gen(at_partfrac); else (*yylval) = gen(at_expand,1); index_status(yyextra)=0; return T_UNARY_OP;
"export"		(*yylval) = gen(at_insmod,1); index_status(yyextra)=0; return T_RETURN;
"fdistrib"		(*yylval) = gen(at_expand,1); index_status(yyextra)=0; return T_UNARY_OP;
"for"			index_status(yyextra)=0; (*yylval)=gen(at_for,4); return T_FOR;
"FOR"			index_status(yyextra)=0; (*yylval)=gen(at_for,4); return T_FOR;
"HALT"		        (*yylval) = gen(at_halt,1); index_status(yyextra)=0; return T_UNARY_OP;
"end if"                index_status(yyextra)=0; (*yylval)=4; return T_BLOC_END;
"end do"                index_status(yyextra)=0; (*yylval)=9; return T_BLOC_END;
"end proc"                index_status(yyextra)=0; (*yylval)=3; return T_BLOC_END;
"if"      		index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_IF;
"IF"      		index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); if (rpn_mode(yyextra)) return T_RPN_IF; return T_IF; 
"ifte"      		index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_IFTE;
"IFTE"      		index_status(yyextra)=0; (*yylval)=gen(at_when,3); return T_IFTE;
"'ifte'"                index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_QUOTED_BINARY;
"'if'"                index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_QUOTED_BINARY;
"_if"                index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_QUOTED_BINARY;
"ifactors"      	if (xcas_mode(yyextra)==1) (*yylval) = gen(at_maple_ifactors); else (*yylval) = gen(at_ifactors,1); index_status(yyextra)=0; return T_UNARY_OP;
"'intersect'"                  index_status(yyextra)=0; (*yylval)=gen(at_intersect,2); return T_QUOTED_BINARY;
"_intersect"                  index_status(yyextra)=0; (*yylval)=gen(at_intersect,2); return T_QUOTED_BINARY;
"KILL"		        (*yylval) = gen(at_kill,1); index_status(yyextra)=0; return T_UNARY_OP;
"log"			(*yylval) = gen(at_ln,1); index_status(yyextra)=1; return T_UNARY_OP; /* index_status(yyextra)=1 to accept log[] for a basis log */
"'minus'"                  index_status(yyextra)=0; (*yylval)=gen(at_minus,2); return T_QUOTED_BINARY;
"_minus"                  index_status(yyextra)=0; (*yylval)=gen(at_minus,2); return T_QUOTED_BINARY;
"not"                 (*yylval) = gen(at_not,1); if (xcas_mode(yyextra)) return T_NOT;  index_status(yyextra)=0; return T_UNARY_OP;
"NOT"                 (*yylval) = gen(at_not,1); return T_NOT;  
"neg"		(*yylval) = gen(at_neg,1); index_status(yyextra)=0; return T_UNARY_OP;
"'not'"                  index_status(yyextra)=0; (*yylval)=gen(at_not,1); return T_QUOTED_BINARY;
"_not"                  index_status(yyextra)=0; (*yylval)=gen(at_not,1); return T_QUOTED_BINARY;
"normalf"		(*yylval) = gen(at_greduce,1); index_status(yyextra)=0; return T_UNARY_OP;
"'of'"                  index_status(yyextra)=0; (*yylval)=gen(at_of,2); return T_QUOTED_BINARY;
"op"                    if (xcas_mode(yyextra)==1) (*yylval) = gen(at_maple_op,1); else (*yylval) = gen(at_feuille,1); index_status(yyextra)=0; return T_UNARY_OP;
"feuille"               (*yylval) = gen(at_feuille,1); index_status(yyextra)=0; return T_UNARY_OP;
"option"                (*yylval)=2; index_status(yyextra)=0; return T_LOCAL;
"pcoef"		        (*yylval) = gen(at_pcoeff,1); index_status(yyextra)=0; return T_UNARY_OP;
"plotfunc2d" 		(*yylval) = gen(at_funcplot,2); index_status(yyextra)=0; return T_UNARY_OP;
"user_operator" 		(*yylval) = gen(at_user_operator,6); index_status(yyextra)=0; return T_UNARY_OP;
"purge"                 if (rpn_mode(yyextra)) {(*yylval)=gen(at_purge,0); index_status(yyextra)=0; return T_RPN_OP;} else {(*yylval) = gen(at_purge,1); index_status(yyextra)=0; return T_UNARY_OP;};
"unassign"                 if (rpn_mode(yyextra)) {(*yylval)=gen(at_purge,0); index_status(yyextra)=0; return T_RPN_OP;} else {(*yylval) = gen(at_purge,1); index_status(yyextra)=0; return T_UNARY_OP;};
"PURGE"                 if (rpn_mode(yyextra)) {(*yylval)=gen(at_purge,0); index_status(yyextra)=0; return T_RPN_OP;} else {(*yylval) = gen(at_purge,1); index_status(yyextra)=0; return T_UNARY_OP;};
"randseed"			(*yylval) = gen(at_srand,1); index_status(yyextra)=0; return T_RETURN;
"repeat"                (*yylval) = gen(at_for,1) ; index_status(yyextra)=0; return T_REPEAT;
"repeter"                (*yylval) = gen(at_for,1) ; index_status(yyextra)=0; return T_REPEAT;
"REPEAT"                (*yylval) = gen(at_for,1) ;index_status(yyextra)=0; return T_REPEAT;
"return"		(*yylval) = gen(at_return,1) ; index_status(yyextra)=0; return T_RETURN;
"retourne"		(*yylval) = gen(at_return,1) ; index_status(yyextra)=0; return T_RETURN;
"RETURN"		(*yylval) = gen(at_return,1) ; index_status(yyextra)=0; return T_RETURN;
"'return'"              (*yylval) = gen(at_return,1) ; index_status(yyextra)=0; return T_QUOTED_BINARY;
"root"                (*yylval) = gen(at_maple_root,1); index_status(yyextra)=1; return T_UNARY_OP;
"same"                  (*yylval) = gen(at_same,1); index_status(yyextra)=0; return T_UNARY_OP;
"SST"		        (*yylval) = gen(at_sst,1); index_status(yyextra)=0; return T_UNARY_OP;
"SST_IN"		(*yylval) = gen(at_sst_in,1); index_status(yyextra)=0; return T_UNARY_OP;
"subs"			if (xcas_mode(yyextra)==1) (*yylval) = gen(at_maple_subs,2); else (*yylval) = gen(at_subs,2); index_status(yyextra)=0; return T_UNARY_OP;
"subsop"		if (xcas_mode(yyextra)==1) (*yylval) = gen(at_maple_subsop,2); else (*yylval) = gen(at_subsop,2); index_status(yyextra)=0; return T_UNARY_OP;
"'union'"                  index_status(yyextra)=0; (*yylval)=gen(at_union,2); return T_QUOTED_BINARY;
"_union"                  index_status(yyextra)=0; (*yylval)=gen(at_union,2); return T_QUOTED_BINARY;
"virgule"               (*yylval) = gen(at_virgule,2); index_status(yyextra)=0; return T_UNARY_OP;
"VARS"                  (*yylval) = gen(at_VARS,0); index_status(yyextra)=0; return T_UNARY_OP;
"while"                 index_status(yyextra)=0; (*yylval)=gen(at_for,4); if (xcas_mode(yyextra)==3) return TI_WHILE; if (xcas_mode(yyextra)!=0) return T_MUPMAP_WHILE; return T_WHILE;
"WHILE"                 index_status(yyextra)=0; (*yylval)=gen(at_for,4); return T_MUPMAP_WHILE; /* return T_RPN_WHILE; */
"DO"                 index_status(yyextra)=0; (*yylval)=gen(at_for,4); return T_DO; /* must be here for DO ... END loop */
"do"                 index_status(yyextra)=0; (*yylval)=gen(at_for,4); return T_DO; /* must be here for DO ... END loop */
"Text"                  (*yylval) = gen(at_Text,1); index_status(yyextra)=0; return T_RETURN;
"DropDown"                  (*yylval) = gen(at_DropDown,1); index_status(yyextra)=0; return T_RETURN;
"Popup"                  (*yylval) = gen(at_Popup,1); index_status(yyextra)=0; return T_RETURN;
"Request"                  (*yylval) = gen(at_Request,1); index_status(yyextra)=0; return T_RETURN;
"Title"                  (*yylval) = gen(at_Title,1); index_status(yyextra)=0; return T_RETURN;
":Prgm"                 (*yylval)=0; index_status(yyextra)=0; return TI_PRGM;
":Func"                 (*yylval)=0; index_status(yyextra)=0; return TI_PRGM;
":func"                 (*yylval)=0; index_status(yyextra)=0; return TI_PRGM;
"If"      	       index_status(yyextra)=0; (*yylval)=gen(at_ifte,3); return T_IF;
"Return"	       (*yylval) = gen(at_return,1) ; index_status(yyextra)=0; return T_RETURN;
"Exit"  	       index_status(yyextra)=0; (*yylval)=gen(at_breakpoint,0); return T_BREAK;
"Loop"                  index_status(yyextra)=0; (*yylval)=gen(at_for,0); return TI_LOOP;
"For"                   index_status(yyextra)=0; (*yylval)=gen(at_for,0); return TI_FOR;
"While"                   index_status(yyextra)=0; (*yylval)=gen(at_for,0); return TI_WHILE;
"Cycle"               index_status(yyextra)=0; (*yylval)=gen(at_for,0); return T_CONTINUE;
"Disp"	       (*yylval) = gen(at_print,1) ; index_status(yyextra)=0; return T_RETURN;
"Pause"	       (*yylval) = gen(at_Pause,1) ; index_status(yyextra)=0; return T_RETURN;
"Lbl"	       (*yylval) = gen(at_label,1) ; index_status(yyextra)=0; return T_RETURN;
"Goto"	       (*yylval) = gen(at_goto,1) ; index_status(yyextra)=0; return T_RETURN;
"Dialog"       (*yylval) = gen(at_Dialog,1) ; index_status(yyextra)=0; return TI_DIALOG; 
"Row"	       (*yylval) = gen(at_Row,0) ; index_status(yyextra)=0; return T_DIGITS;
"Col"	       (*yylval) = gen(at_Col,0) ; index_status(yyextra)=0; return T_DIGITS;

"DELTALIST" index_status(yyextra)=0; (*yylval)=gen(at_DELTALIST); return T_UNARY_OP_38; 
"PILIST" index_status(yyextra)=0; (*yylval)=gen(at_PILIST); return T_UNARY_OP_38; 
"SIGMA" index_status(yyextra)=0;(*yylval)=gen(at_HPSUM); return T_UNARY_OP_38; 
"SIGMALIST" index_status(yyextra)=0; (*yylval)=gen(at_SIGMALIST); return T_UNARY_OP_38;
"∂" index_status(yyextra)=0;(*yylval)=gen(at_HPDIFF); return T_UNARY_OP_38; 
"∫" index_status(yyextra)=0;(*yylval)=gen(at_HPINT); return T_UNARY_OP_38; 
"≤" index_status(yyextra)=0; (*yylval)=gen(at_inferieur_egal,2); return T_TEST_EQUAL;
"≠" index_status(yyextra)=0; (*yylval)=gen(at_different,2); return T_TEST_EQUAL;
"≥" 		index_status(yyextra)=0; (*yylval)=gen(at_superieur_egal,2); return T_TEST_EQUAL;
           /* old format for physical constants
"_hbar_"        (*yylval) = symbolic(at_unit,makevecteur(1.05457266e-34,_J_unit*_s_unit)); index_status(yyextra)=0; return T_SYMBOL;
"_c_"        (*yylval) = symbolic(at_unit,makevecteur(299792458,_m_unit/_s_unit)); index_status(yyextra)=0; return T_SYMBOL;
"_g_"        (*yylval) = symbolic(at_unit,makevecteur(9.80665,_m_unit*unitpow(_s_unit,-2))); index_status(yyextra)=0; return T_SYMBOL;
"_IO_" (*yylval) = symbolic(at_unit,makevecteur(1e-12,_W_unit*unitpow(_m_unit,-2))); index_status(yyextra)=0; return T_SYMBOL; 
"_epsilonox_" (*yylval) = 3.9; index_status(yyextra)=0; return T_SYMBOL; 
"_epsilonsi_" (*yylval) = 11.9; index_status(yyextra)=0; return T_SYMBOL; 
"_qepsilon0_" (*yylval) = symbolic(at_unit,makevecteur(1.4185979e-30,_F_unit*_C_unit/_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_epsilon0q_" (*yylval) = symbolic(at_unit,makevecteur(55263469.6,_F_unit/(_m_unit*_C_unit))); index_status(yyextra)=0; return T_SYMBOL; 
"_kq_" (*yylval) = symbolic(at_unit,makevecteur(8.617386e-5,_J_unit/(_K_unit*_C_unit))); index_status(yyextra)=0; return T_SYMBOL; 
"_c3_" (*yylval) = symbolic(at_unit,makevecteur(.002897756,_m_unit*_K_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_lambdac_" (*yylval) = symbolic(at_unit,makevecteur( 0.00242631058e-9,_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_f0_" (*yylval) = symbolic(at_unit,makevecteur(2.4179883e14,_Hz_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_lambda0_" (*yylval) = symbolic(at_unit,makevecteur(1239.8425e-9,_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_muN_" (*yylval) = symbolic(at_unit,makevecteur(5.0507866e-27,_J_unit/_T_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_muB_" (*yylval) = symbolic(at_unit,makevecteur( 9.2740154e-24,_J_unit/_T_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_a0_" (*yylval) = symbolic(at_unit,makevecteur(.0529177249e-9,_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_Rinfinity_" (*yylval) = symbolic(at_unit,makevecteur(10973731.534,unitpow(_m_unit,-1))); index_status(yyextra)=0; return T_SYMBOL; 
"_Faraday_" (*yylval) = symbolic(at_unit,makevecteur(96485.309,_C_unit/_mol_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_phi_" (*yylval) = symbolic(at_unit,makevecteur(2.06783461e-15,_Wb_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_alpha_" (*yylval) = 7.29735308e-3; index_status(yyextra)=0; return T_SYMBOL; 
"_mpme_" (*yylval) = 1836.152701; index_status(yyextra)=0; return T_SYMBOL; 
"_mp_" (*yylval) = symbolic(at_unit,makevecteur(1.6726231e-27,_kg_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_qme_" (*yylval) = symbolic(at_unit,makevecteur(1.75881962e11,_C_unit/_kg_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_me_" (*yylval) = symbolic(at_unit,makevecteur(9.1093897e-31,_kg_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_qe_" (*yylval) = symbolic(at_unit,makevecteur(1.60217733e-19,_C_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_h_" (*yylval) = symbolic(at_unit,makevecteur(6.6260755e-34,_J_unit*_s_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_G_" (*yylval) = symbolic(at_unit,makevecteur(6.67259e-11,unitpow(_m_unit,3)*unitpow(_s_unit,-2)*unitpow(_kg_unit,-1))); index_status(yyextra)=0; return T_SYMBOL; 
"_mu0_" (*yylval) = symbolic(at_unit,makevecteur(1.25663706144e-6,_H_unit/_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_epsilon0_" (*yylval) = symbolic(at_unit,makevecteur(8.85418781761e-12,_F_unit/_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_sigma_" (*yylval) = symbolic(at_unit,makevecteur( 5.67051e-8,_W_unit*unitpow(_m_unit,-2)*unitpow(_K_unit,-4))); index_status(yyextra)=0; return T_SYMBOL; 
"_StdP_" (*yylval) = symbolic(at_unit,makevecteur(101325.0,_Pa_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_StdT_" (*yylval) = symbolic(at_unit,makevecteur(273.15,_K_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_R_" (*yylval) = symbolic(at_unit,makevecteur(8.31451,_J_unit/_molK_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_Vm_" (*yylval) = symbolic(at_unit,makevecteur(22.4141,_l_unit/_mol_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_k_" (*yylval) = symbolic(at_unit,makevecteur(1.380658e-23,_J_unit/_K_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_NA_" (*yylval) = symbolic(at_unit,makevecteur(6.0221367e23,unitpow(_mol_unit,-1))); index_status(yyextra)=0; return T_SYMBOL; 
"_mSun_" (*yylval) = symbolic(at_unit,makevecteur(1.989e30,_kg_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_RSun_" (*yylval) = symbolic(at_unit,makevecteur(6.955e8,_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_PSun_" (*yylval) = symbolic(at_unit,makevecteur(3.846e26,_W_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_mEarth_" (*yylval) = symbolic(at_unit,makevecteur(5.9736e24,_kg_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_REarth_" (*yylval) = symbolic(at_unit,makevecteur(6.371e6,_m_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_sd_" (*yylval) = symbolic(at_unit,makevecteur(8.61640905e4,_s_unit)); index_status(yyextra)=0; return T_SYMBOL; 
"_syr_" (*yylval) = symbolic(at_unit,makevecteur(3.15581498e7,_s_unit)); index_status(yyextra)=0; return T_SYMBOL; 
                        */
			/* numbers, also accept DMS e.g 1°15′27″13 */
{D}+			|
{D}+"°" | 
{D}+\302\260{D}+ | 
{D}+"°"{D}+\342\200\262 | 
{D}+"°"{D}+\342\200\262{D}+ | 
{D}+"°"{D}+\342\200\262{D}+\342\200\263 | 
{D}+"°"{D}+\342\200\262{D}+\342\200\263{D}+ | 
{D}+"°"{D}+\342\200\262{D}+"."{D}+ | 
{D}+"°"{D}+\342\200\262{D}+"."{D}+\342\200\263 | 
"#"[0-7]+"o"		|
"#"[0-1]+"b"		|
"#"[0-9a-fA-F]+"h"	|
"#o"[0-7]+		|
"#b"[0-1]+		|
"#x"[0-9a-fA-F]+	|
"0o"[0-7]+		|
"0b"[0-1]+		|
"0x"[0-9a-fA-F]+	|
{D}+"."{D}*({E})?	|
{D}*"."{D}+({E})?	|
{D}+{E}			{ 
  index_status(yyextra)=1;
  int l=strlen(yytext);
  char ch,ch2;
  if (l>2 && yytext[1]!='x' && (yytext[l-1]=='o' || yytext[l-1]=='b' || yytext[l-1]=='h') ){
    char base=yytext[l-1];
    for (int i=l-1;i>1;--i){
      yytext[i]=yytext[i-1];
    }
    if (base=='h')
      base='x';
    yytext[1]=base;
  }
  else {
    for (l=0;(ch=*(yytext+l));++l){
      if (ch=='x')
	break;
      if (ch=='e' || ch=='E'){
	if ( (ch2=*(yytext+l+1)) && (ch2=='e' || ch2=='E')){
	  ++l;
	  for (;(ch=*(yytext+l));++l)
	    *(yytext+l-1)=ch;
	  *(yytext+l-1)=0;
	  --l;
	}
      }
#ifndef BCD
      if ( (ch==-30 && *(yytext+l+1)==-128) || (ch==-62 && *(yytext+l+1)==-80) ){
	*yylval=0; return T_NUMBER;
      }
#endif
      if (ch==-30 && *(yytext+l+1)==-120 &&  *(yytext+l+2)==-110){
	l += 3;
	for (;(ch=*(yytext+l));++l)
	  *(yytext+l-2)=ch;
	*(yytext+l-2)=0;
	l -= 3;
	*(yytext+l)='-';
      }
    }
  }
  (*yylval) = chartab2gen(yytext,yyextra); return T_NUMBER; 
}

			/* UNITS 
"_"{A}{AN}* {
  std::pair<const char * const * const,const char * const * const> pp=equal_range(unitname_tab,unitname_tab_end,yytext,tri2);
  if (pp.first!=pp.second && pp.second!=unitname_tab_end){
    gen tmp=mksa_register_unit(*pp.first,unitptr_tab[pp.first-unitname_tab]);
    (*yylval)=tmp;
    index_status(yyextra)=0;
    return T_SYMBOL;
  }
  int res=find_or_make_symbol(yytext+1,(*yylval),yyscanner,false,yyextra);
 (*yylval)=symb_unit(1,(*yylval),yyextra);
 return res;
}
			*/
			/* symbols */
{A}{AN}*        |
"%"{A}{AN}*     {
 index_status(yyextra)=1;
 int res=find_or_make_symbol(yytext,(*yylval),yyscanner,true,yyextra);
 if (res==T_NUMBER)
   *yylval=(*yylval)(string2gen(unlocalize(yytext),false),yyextra);
 return res;
} 
"#"                     if (!xcas_mode(yyextra) || xcas_mode(yyextra)==3) { 
  // cerr << "hash" << endl;
  (*yylval)=gen(at_hash,1); return TI_HASH; 
} else BEGIN(comment_hash);
<comment_hash>[^*\n]*\n BEGIN(INITIAL); index_status(yyextra)=0; increment_lexer_line_number_setcol(yyscanner,yyextra);  /* comment_s(yyextra)=string(yytext); (*yylval)=string2gen(comment_s(yyextra).substr(0,comment_s(yyextra).size()-1),false); return T_COMMENT; */
			/* everything else */
.			(*yylval)=string2gen(string(yytext),false); return T_STRING;

%%

/*
 *  Routines
 */
#ifndef NO_NAMESPACE_GIAC
  namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
    
    void update_lexer_localization(const std::vector<int> & v,std::map<std::string,std::string> &lexer_map,std::multimap<std::string,giac::localized_string> &back_lexer_map){
      lexer_map.clear();
      back_lexer_map.clear();
      int s=v.size();
      for (int i=0;i<s;++i){
	int lang=v[i];
	if (lang>=1 && lang<=4){
	  std::string doc=find_doc_prefix(lang);
	  std::string file=giac::giac_aide_dir()+doc+"keywords";
	  std::string giac_kw,local_kw;
	  size_t l;
	  char * line = (char *)malloc(1024);
	  ifstream f(file.c_str());
	  if (f){
	    cerr << "// Using keyword file " << file << endl;
	    for (;;){
	      f.getline(line,1023,'\n');
	      l=strlen(line);
	      if (f.eof()){
		f.close();
		break;
	      }
	      if (l>3 && line[0]!='#'){
		if (line[l-1]=='\n')
		  --l;
		// read giac keyword
		size_t j;
		giac_kw="";
		for (j=0;j<l;++j){
		  if (line[j]==' ')
		    break;
		  giac_kw += line[j];
		}
		// read corresponding local keywords
		local_kw="";
		for (++j;j<l;++j){
		  if (line[j]==' '){
		    if (!local_kw.empty()){
		      lexer_map[local_kw]=giac_kw;
		      back_lexer_map.insert(pair<string,localized_string>(giac_kw,localized_string(lang,local_kw)));
		    }
		    local_kw="";
		  }
		  else
		    local_kw += line[j];
		}
		if (!local_kw.empty()){
		  lexer_map[local_kw]=giac_kw;
		  back_lexer_map.insert(pair<string,localized_string>(giac_kw,localized_string(lang,local_kw)));
		}
	      }
	    }
	    free(line);
	  } // if (f)
	  else
	    cerr << "// Unable to find keyword file " << file << endl;
	}
      }
    }

    bool has_special_syntax(const char * s){
      std::pair<charptr_gen *,charptr_gen *> p=
	equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),
		    std::pair<const char *,gen>(s,0),
		    tri);
      if (p.first!=p.second && p.first!=builtin_lexer_functions_end())
	return (p.first->second.subtype!=T_UNARY_OP-256);
      map_charptr_gen::const_iterator i = lexer_functions().find(s);
      if (i==lexer_functions().end())
	return false;
      return (i->second.subtype!=T_UNARY_OP-256);
    }
    
    bool lexer_functions_register(const unary_function_ptr & u,const char * s,int parser_token){
      map_charptr_gen::const_iterator i = lexer_functions().find(s);
      if (i!=lexer_functions().end())
	return false;
      if (doing_insmod)
	registered_lexer_functions().push_back(user_function(s,parser_token));
      if (!builtin_lexer_functions_sorted){
#ifndef STATIC_BUILTIN_LEXER_FUNCTIONS
	builtin_lexer_functions_begin()[builtin_lexer_functions_number]=std::pair<const char *,gen>(s,gen(u));
	if (parser_token==1)
	  builtin_lexer_functions_begin()[builtin_lexer_functions_number].second.subtype=T_UNARY_OP-256;
	else
	  builtin_lexer_functions_begin()[builtin_lexer_functions_number].second.subtype=parser_token-256;
	builtin_lexer_functions_number++;
#endif
      }
      else {
	lexer_functions()[s] = gen(u);
	if (parser_token==1)
	  lexer_functions()[s].subtype=T_UNARY_OP-256;
	else
	  lexer_functions()[s].subtype=parser_token-256;
      }
      // If s is a library function name (with ::), update the library
      int ss=strlen(s),j=0;
      for (;j<ss-1;++j){
	if (s[j]==':' && s[j+1]==':')
	  break;
      }
      if (j<ss-1){
	string S(s);
	string libname=S.substr(0,j);
	string funcname=S.substr(j+2,ss-j-2);
	std::map<std::string,std::vector<string> >::iterator it=library_functions().find(libname);
	if (it!=library_functions().end())
	  it->second.push_back(funcname);
	else
	  library_functions()[libname]=vector<string>(1,funcname);
      }
      return true;
    }

    bool lexer_function_remove(const vector<user_function> & v){
      vector<user_function>::const_iterator it=v.begin(),itend=v.end();
      map_charptr_gen::const_iterator i,iend;
      bool ok=true;
      for (;it!=itend;++it){
	i = lexer_functions().find(it->s.c_str());
	iend=lexer_functions().end();
	if (i==iend)
	  ok=false;
	else
	  lexer_functions().erase(it->s.c_str());
      }
      return ok;
    }

    int find_or_make_symbol(const string & s,gen & res,void * scanner,bool check38,GIAC_CONTEXT){
      int tmpo=opened_quote(contextptr);
      if (tmpo & 2)
	check38=false;
      if (s.size()==1){
#ifdef GIAC_HAS_STO_38
	if (s[0]>='A' && s[0]<='Z'){
	  index_status(contextptr)=1; 
	  res=*tab_one_letter_idnt[s[0]-'A'];
	  return T_SYMBOL;
	}
	if (check38 && s[0]>='a' && s[0]<='z' && calc_mode(contextptr)==38)
	  giac_yyerror(scanner,invalid_name);
#else
	if (s[0]>='a' && s[0]<='z'){
	  if (check38 && calc_mode(contextptr)==38)
	    giac_yyerror(scanner,invalid_name);
	  index_status(contextptr)=1; 
	  res=*tab_one_letter_idnt[s[0]-'a'];
	  return T_SYMBOL;
	}
#endif
	switch (s[0]){
	case '+':
	  res=at_plus;
	  return T_UNARY_OP;
	case '-':
	  res=at_neg;
	  return T_UNARY_OP;
	case '*':
	  res=at_prod;
	  return T_UNARY_OP;
	case '/':
	  res=at_division;
	  return T_UNARY_OP;
	case '^':
	  res=at_pow;
	  return T_UNARY_OP;
	}
      }
      string ts(s);
      std::map<std::string,std::string>::const_iterator trans=lexer_localization_map().find(ts);
      if (trans!=lexer_localization_map().end())
	ts=trans->second;
      std::map<std::string,std::vector<string> >::const_iterator j=lexer_translator().find(ts);
      if (j!=lexer_translator().end() && !j->second.empty())
	ts=j->second.back();
      std::pair<charptr_gen *,charptr_gen *> p=equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(ts.c_str(),0),tri);
      if (p.first!=p.second && p.first!=builtin_lexer_functions_end()){
	if (p.first->second.subtype==T_TO-256)
	  res=plus_one;
	else
	  res = p.first->second;
	res.subtype=1;
	if (builtin_lexer_functions_){
	  res=gen(int(builtin_lexer_functions_[p.first-builtin_lexer_functions_begin()]+p.first->second.val));
	  res=gen(*res._FUNCptr);
	}
	index_status(contextptr)=(p.first->second.subtype==T_UNARY_OP-256);
	int token=p.first->second.subtype;
	token += (token<0)?512:256 ;	
	return token;
      }
      lexer_tab_int_type tst={ts.c_str(),0,0,0,0};
      std::pair<const lexer_tab_int_type *,const lexer_tab_int_type *> pp = equal_range(lexer_tab_int_values,lexer_tab_int_values_end,tst,tri1);
      if (pp.first!=pp.second && pp.first!=lexer_tab_int_values_end){
	index_status(contextptr)=pp.first->status;
	res=int(pp.first->value);
	res.subtype=pp.first->subtype;
	return pp.first->return_value;
      }
      map_charptr_gen::const_iterator i = lexer_functions().find(ts.c_str());
      if (i!=lexer_functions().end()){
	if (i->second.subtype==T_TO-256)
	  res=plus_one;
	else
	  res = i->second;
	res.subtype=1;
	index_status(contextptr)=(i->second.subtype==T_UNARY_OP-256);
	return i->second.subtype+256 ;
      }
      sym_tab::const_iterator i2 = syms().find(s);
      if (i2 == syms().end()) {
	const char * S = s.c_str();
	// std::cerr << "lexer new" << s << endl;
	if (check38 && calc_mode(contextptr)==38 && strcmp(S,string_pi) && strcmp(S,string_euler_gamma) && strcmp(S,string_infinity) && strcmp(S,string_undef) && S[0]!='G'&& (!is_known_name_38 || !is_known_name_38(0,S))){
	  // detect invalid names and implicit multiplication 
	  size_t ss=strlen(S);
	  vecteur args;
	  for (size_t i=0;i<ss;++i){
	    char ch=S[i];
	    if (ch=='C' || (ch>='E' && ch<='H') || ch=='L' || ch=='M' || ch=='R'
		/* || ch=='S' */
		|| ch=='U' || ch=='V' || (ch>='X' && ch<='Z') ){
	      string name;
	      name += ch;
	      char c=0;
	      if (i<ss-1)
		c=s[i+1];
	      if (c>='0' && c<='9'){
		name += c;
		++i;
	      }
	      res = identificateur(name);
	      syms()[name.c_str()] = res;
	      args.push_back(res);
	    }
	    else {
	      string coeff;
	      for (++i;i<ss;++i){
		// up to next alphabetic char
		if (s[i]>32 && isalpha(s[i])){
		  --i;
		  break;
		}
		if (scanner && (s[i]<0 || s[i]>'z')){
		  giac_yyerror(scanner,invalid_name);
		  res=undef;
		  return T_SYMBOL;
		}
		coeff += s[i];
	      }
	      if (coeff.empty())
		res=1;
	      else
		res=atof(coeff.c_str());
	      if (ch=='i')
		res=res*cst_i;
	      else {
		if (ch=='e')
		  res=std::exp(1.0)*res;
		else {
		  // Invalid ident name, report error
		  if ( (ch>'Z' || ch<0) && scanner){
		    giac_yyerror(scanner,invalid_name);
		    res=undef;
		    return T_SYMBOL;
		  }
		  coeff=string(1,ch);
		  gen tmp = identificateur(coeff);
		  // syms()[coeff.c_str()]=tmp;
		  res=res*tmp;
		}
	      }
	      args.push_back(res);
	    }
	  }
	  if (args.size()==1)
	    res=args.front();
	  else 
	    res=_prod(args,contextptr);
	  syms()[s]=res;
	  return T_SYMBOL;
	} // end 38 compatibility mode
	res = identificateur(s);
	syms()[s] = res;
	return T_SYMBOL;
      } // end if ==syms.end()
      res = i2->second;
      return T_SYMBOL;
    }

  // Add to the list of predefined symbols
  void set_lexer_symbols(const vecteur & l,GIAC_CONTEXT){
    if (initialisation_done(contextptr))
      return;
    initialisation_done(contextptr)=true;
    const_iterateur it=l.begin(),itend=l.end();
    for (; it!=itend; ++it) {
      if (it->type!=_IDNT)
	continue;
      sym_tab::const_iterator i = syms().find(it->_IDNTptr->id_name);
      if (i==syms().end())
	syms()[it->_IDNTptr->name()] = *it;
    }
  }
  
    std::string translate_at(const char * ch){
      if (!strcmp(ch,"ΔLIST"))
	return "DELTALIST";
      if (!strcmp(ch,"ΠLIST"))
	return "PILIST";
      if (!strcmp(ch,"ΣLIST"))
	return "SIGMALIST";
      if (!strcmp(ch,"∫"))
	return "HPINT";
      if (!strcmp(ch,"∂"))
	return "HPDIFF";
      if (!strcmp(ch,"Σ"))
	return "HPSUM";
      if (!strcmp(ch,"∑"))
	return "HPSUM";
      string res;
      for (;*ch;++ch){
        if (*ch=='%')
          res +="PERCENT";
        else
          res += *ch;
      }
      return res;
    }
    
    // Set the input string
    // export GIAC_DEBUG=-2 to renew static_lexer.h/static_extern.h
    YY_BUFFER_STATE set_lexer_string(const std::string &s_orig,yyscan_t & scanner,GIAC_CONTEXT){
      if (abs_calc_mode(contextptr)==38 && s_orig==string(s_orig.size(),' '))
	giac_yyerror(scanner,"Void string");
#ifndef RTOS_THREADX
      if (!builtin_lexer_functions_sorted){
#ifndef STATIC_BUILTIN_LEXER_FUNCTIONS
	sort(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),tri);
#endif
	builtin_lexer_functions_sorted=true;
	int nfunc=builtin_lexer_functions_number;
	if (debug_infolevel==-2 || debug_infolevel==-4){
	  cerr << "Writing " << nfunc << " in static_lexer.h and static_extern.h "<< endl;
	  /*
	  ofstream static_add_ll("static_add.ll");
	  for (int i=0;i<nfunc;i++){
	    static_add_ll << "\"" << builtin_lexer_functions_begin()[i].first << "\" " ;
	    static_add_ll << "*yylval=gen(at_" << translate_at(builtin_lexer_functions_begin()[i].first) << ",0); index_status(yyextra)=0; ";
	    static_add_ll << "return " << signed(builtin_lexer_functions_begin()[i].second.subtype)+256 << ";" ;
            static_add_ll << endl;
	  }
	  static_add_ll.close();
	  */
	  ofstream static_lexer("static_lexer.h");
	  for (int i=0;i<nfunc;i++){
	    static_lexer << "{\"" << builtin_lexer_functions_begin()[i].first << "\",13," << signed(builtin_lexer_functions_begin()[i].second.subtype) ;
	    if (debug_infolevel==-2)
	      static_lexer << ",0,*((unsigned longlong *) at_" << translate_at(builtin_lexer_functions_begin()[i].first) << ")";
	    else
	      static_lexer << ",0,0"; 
	    if (builtin_lexer_functions_begin()[i].second._FUNCptr->quoted())
	      static_lexer << "| 1";
	    static_lexer << "}" ;
	    if (i!=nfunc-1)
	      static_lexer << ",";
	    static_lexer << endl;
	  }
	  static_lexer.close();
	  if (debug_infolevel==-4){
	    ofstream static_lexer_("static_lexer_.h");
	    for (int i=0;i<nfunc;i++){
	      static_lexer_ << "*((unsigned long *) at_" << translate_at(builtin_lexer_functions_begin()[i].first) << ")";
	      if (i!=nfunc-1)
		static_lexer_ << ",";
	      static_lexer_ << endl;
	    }
	    static_lexer_.close();
	  }
	  ofstream static_extern("static_extern.h");
	  static_extern << "#ifndef STATIC_EXTERN" << endl;
	  static_extern << "#define STATIC_EXTERN" << endl;
	  static_extern << "struct unary_function_ptr;" << endl;
	  for (int i=0;i<nfunc;i++){
	    static_extern << "extern const unary_function_ptr * const  at_" << translate_at(builtin_lexer_functions_begin()[i].first) << ";" << endl;
	  }
	  static_extern << "#endif // STATIC_EXTERN" << endl;
	  static_extern.close();
	}
      }
#endif // RTOS_THREADX
      string s(s_orig),lexer_string;
      bool instring=false;
      // stupid match of bracket then parenthesis
      int l=s.size(),nb=0,np=0;
      int i=0;
      if (lexer_close_parenthesis(contextptr)){
	for (;i<l;++i){
	  if (s[i]==92){
	    i += 2;
	    if (i>=l)
	      break;
	  }
	  if (instring){
	    if (s[i]=='"')
	      instring=false;
	  }
	  else {
	    switch (s[i]){
	    case '"':
	      instring=true;
	      break;
	    case '(':
	      ++np;
	      break;
	    case ')':
	      --np;
	      break;
	    case '[':
	      ++nb;
	      break;
	    case ']':
	      --nb;
	      break;
	    }
	  }
	}
	while (np<0 && i>=0 && s[i-1]==')'){
	  --i;
	  ++np;
	}
	while (nb<0 && i>=0 && s[i-1]==']'){
	  --i;
	  ++nb;
	}
	s=s.substr(0,i);
	if (nb<0)
	  cerr << "Too many ]" << endl;
	if (np<0)
	  cerr << "Too many )" << endl;
	if (nb>0){
	  *logptr(contextptr) << "Warning adding " << nb << " ] at end of input" << endl;
	  s=s+string(nb,']');
	}
	if (np>0){
	  *logptr(contextptr) << "Warning adding " << np << " ) at end of input" << endl;
	  s=s+string(np,')');
	}
      }
      index_status(contextptr)=0;
      opened_quote(contextptr)=0;
      in_rpn(contextptr)=0;
      lexer_line_number(contextptr)=1;
      first_error_line(contextptr)=0;
      spread_formula(contextptr)=0;
      l=s.size();
      for (;l;l--){
	if (s[l-1]!=' ')
	  break;
      }
      // strings ending with :;
      while (l>=4 && s[l-1]==';' && s[l-2]==':'){
	// skip spaces before :;
	int m;
	for (m=l-3;m>0;--m){
	  if (s[m]!=' ')
	    break;
	}
	if (m<=1 || s[m]!=';')
	  break;
	if (s[m-1]==':')
	  l = m+1;
	else {
	  s[m]=':';
	  s[m+1]=';';
	  l=m+2;
	}
      }
      s=s.substr(0,l);
      /* if (l && ( (s[l-1]==';') || (s[l-1]==':')))
	 l--; */
      string ss;
      for (int i=0;i<l;++i){
	if (i && (unsigned char)s[i]==0xc2 && (unsigned char)s[i+1]!=0xb0)
	  ss += ' ';
	if ( (unsigned char)s[i]==0xe2 && i<l-3 ){
          if ((unsigned char)s[i+1]==0x88){ 
	    // mathop, add blank before and after except following an e/E 
	    if ((unsigned char) s[i+2]==0x91){ // sigma
	      ss += " SIGMA";
	      i +=2;
	      continue;
	    }
	    if ((unsigned char) s[i+2]==0x86){ // delta
	      ss += " DELTA";
	      i +=2;
	      continue;
	    }
	    if ((unsigned char) s[i+2]==0x8f){ // pi
	      ss += " PI";
	      i +=2;
	      continue;
	    }
	    if ( i>1 && (s[i-1]=='e' || s[i-1]=='E')){
	      ss +='-';
	      i +=2;
	      continue;
	    }
	    if (i>2  && (s[i-1]==' ' && (s[i-2]=='e' || s[i-2]=='E')) ){
	      ss[ss.size()-1] = '-';
	      i += 3;
	      continue;
	    }
	    ss += ' ';
	    ss += s[i];
	    ++i;
	    ss += s[i];
	    ++i;
	    ss += s[i];
	    ss += ' ';
	    continue;
	  } // 0xe2 0x88
          if ((unsigned char)s[i+1]==0x96 && ((unsigned char)s[i+2]==0xba || (unsigned char)s[i+2]==182 )){  
	    // sto 
	    ss += s[i];
	    ++i;
	    ss += s[i];
	    ++i;
	    ss += s[i];
	    ss += ' ';
	    continue;
	  } // 0xe2 0x96
	} //end if s[i]=0xe2
	if (s[i]=='.'){
	  if ( i && (i<l-1) && (s[i-1]!=' ') && (s[i+1]=='.') ){
	    ss+= " ..";
	    ++i;
	  }
	  else
	    ss+='.';
	}
	else {
	  if (xcas_mode(contextptr) > 0 && xcas_mode(contextptr) !=3){
	    if (s[i]=='#')
	      ss += "//";
	    else
	      ss += s[i];
	  }
	  else
	    ss+=s[i];
	}
      }
      // ofstream of("log"); of << s << endl << ss << endl; of.close();
      lexer_string = ss+" \n ÿ";
      yylex_init(&scanner);
      yyset_extra(contextptr, scanner);
      YY_BUFFER_STATE state=yy_scan_string(lexer_string.c_str(),scanner);
      return state;
    }

    int delete_lexer_string(YY_BUFFER_STATE & state,yyscan_t & scanner){
      yy_delete_buffer(state,scanner);
      yylex_destroy(scanner);
      return 1;
    }

#ifndef NO_NAMESPACE_GIAC
  } // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
  
