 /* -*- mode: C++; compile-command: "flex input_lexer.ll && g++ -g -I.. -c input_lexer.cc" -*- */
/** @file input_lexer.h
 *
 *  Lexical analyzer definition for reading expressions.
 *  input_lexer.ll must be processed with flex. */

/*
 *  Original version by GiNaC 
 *  Copyright (C) 1999-2000 Johannes Gutenberg University Mainz, Germany
 *  Modified for Giac (c) 2001, Bernard Parisse, Institut Fourier
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

#ifndef __GIAC_INPUT_LEXER_H__
#define __GIAC_INPUT_LEXER_H__
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"

extern "C" {
#include <stdio.h>
}

#include "global.h"
#include <string>
#include <map>
#include "help.h"
// yacc stack type
#ifndef YYSTYPE
#define YYSTYPE giac::gen
#endif
#define YY_EXTRA_TYPE  const giac::context *
typedef struct yy_buffer_state *YY_BUFFER_STATE;

// lex functions/variables
extern int giac_yyerror(void *scanner,const char *s);
extern int giac_yylex(YYSTYPE * yylval_param ,void * yyscanner);

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  std::vector<int> & lexer_localization_vector();
  std::map<std::string,std::string> & lexer_localization_map();
  std::multimap<std::string,localized_string> & back_lexer_localization_map();
  // lexer_localization_map translates keywords from the locale to giac 
  // lexer_localization_vector is the list of languages currently translated
  void update_lexer_localization(const std::vector<int> & v,std::map<std::string,std::string> &lexer_map,std::multimap<std::string,localized_string> &back_lexer_map);
  

  std::map<std::string,std::vector<std::string> > & lexer_translator();
  std::map<std::string,std::vector<std::string> > & library_functions();
  map_charptr_gen & lexer_functions();


  inline bool tri (const std::pair<const char *,gen> & a ,const std::pair<const char *,gen> & b){
    return strcmp(a.first, b.first) < 0;
  }
 
  typedef std::pair<const char *,gen> charptr_gen;
  charptr_gen * builtin_lexer_functions_begin();
  charptr_gen * builtin_lexer_functions_end();
#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS
  extern const unsigned long builtin_lexer_functions_[];
#else
  extern const unsigned long * const builtin_lexer_functions_;
#endif

  // return true/false to tell if s is recognized. return the appropriate gen if true
  bool CasIsBuildInFunction(char const *s, gen &g);

  sym_tab & syms();
  // The lexer recognize first static declared symbols as declared in
  // input_lexer.ll, then it does 2 steps:
  // step 1: look in lexer_translator if the name is a recognized
  // function name, if so translate to the real giac function name
  // step 2: look in syms for an identifier, if no exists with this
  // name make one
  // lexer_functions is the table of all used symbols with real giac names. 
  // The subtype of the gen is used
  // to keep the parser token returned by the lexer

  // Used to keep track of functions inserted during an insmod
  extern bool doing_insmod ;
  std::vector<user_function> & registered_lexer_functions();
  
  struct unary_function_ptr;
  // Return true if s is associated to a function with non prefix syntax
  bool has_special_syntax(const char * s);
  bool lexer_functions_register(const unary_function_ptr & u,const char * s,int parser_token);
  inline bool lexer_functions_register(const unary_function_ptr * u,const char * s,int parser_token){ return lexer_functions_register(*u,s,parser_token); }
  bool lexer_function_remove(const std::vector<user_function> & v);

  // return the token associated to the string, T_SYMBOL if not found
  int find_or_make_symbol(const std::string & s,gen & res,void * scanner,bool check38,GIAC_CONTEXT);
  
  /** Add to the list of predefined symbols for the lexer. */
  void set_lexer_symbols(const vecteur & l,GIAC_CONTEXT);
  
  /** Set the input string to be parsed by giac_yyparse() (used internally). */
  YY_BUFFER_STATE set_lexer_string(const std::string &s,void * & scanner,const giac::context * contextptr);
  int delete_lexer_string(YY_BUFFER_STATE &state,void * & scanner);
  
  /** Get error message from the parser. */
  std::string get_parser_error(void);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // ndef __GIAC_INPUT_LEXER_H__
