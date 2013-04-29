// -*- mode:C++ ; compile-command: "g++-3.4 -I. -I.. -I../include -g -c  symbolic.cc -Wall -DIN_GIAC -DHAVE_CONFIG_H" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2000, 2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
using namespace std;
#include "symbolic.h"
#include "identificateur.h"
#include "usual.h"
#include "prog.h"
#include "rpn.h"
#include "plot.h"
#include "giacintl.h"
#include "global.h"

// NB: the operator in the symbolic (sommet) can not be replace by a pointer
// to a unary_function_ptr in the current code. Indeed, symbolic are created
// in the parser from temporary gen objects of type _FUNC which contains
// copies of unary_function_ptr, these copies are deleted once the parser
// gen object is deleted -> segfault.

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // unary_function_ptr quoted_op[]={at_of,at_for,at_bloc,at_local,at_program,at_rpn_prog,at_ifte,at_try_catch,at_print,at_signal,at_as_function_of,at_lieu,at_legende,at_debug,at_sst,at_sst_in,at_cont,at_kill,at_halt,at_watch,at_rmwatch,at_breakpoint,at_maple2mupad,at_mupad2maple,at_maple2xcas,at_mupad2xcas,at_purge,0};
#if defined(GIAC_GENERIC_CONSTANTS) || defined(VISUALC) || defined(__x86_64__)
  const unary_function_ptr * archive_function_tab(){
    static const unary_function_ptr archive_function_tab_ptr[]={*at_plus,*at_neg,*at_binary_minus,*at_prod,*at_division,*at_inv,*at_pow,*at_exp,*at_ln,*at_abs,*at_arg,*at_pnt,*at_point,*at_segment,*at_sto,*at_sin,
								*at_cos,*at_tan,*at_asin,*at_acos,*at_atan,*at_sinh,*at_cosh,*at_tanh,*at_asinh,*at_acos,*at_atanh,*at_interval,*at_union,*at_minus,*at_intersect,*at_not,
								*at_and,*at_ou,*at_inferieur_strict,*at_inferieur_egal,*at_superieur_strict,*at_superieur_egal,*at_different,*at_equal,*at_rpn_prog,*at_local,*at_return,*at_Dialog,*at_double_deux_points,*at_pointprod,*at_pointdivision,*at_pointpow,*at_hash,*at_pourcent,*at_tilocal,*at_break,*at_continue,*at_ampersand_times,*at_maple_lib,*at_unit,*at_plot_style,*at_xor,*at_check_type,*at_quote_pow,*at_case,*at_dollar,*at_IFTE,*at_RPN_CASE,*at_RPN_LOCAL,*at_RPN_FOR,*at_RPN_WHILE,*at_NOP,*at_unit,*at_ifte,*at_for,*at_bloc,*at_program,*at_same,*at_increment,*at_decrement,*at_multcrement,*at_divcrement,*at_sq,*at_display,*at_of,*at_at,*at_normalmod,0};
    archive_function_tab_length=sizeof(archive_function_tab_ptr)/sizeof(const unary_function_ptr *);
    return archive_function_tab_ptr;
  }
  int archive_function_tab_length=0;

#else
  static const unsigned long archive_function_tab_alias[]={
    // index = 2
    alias_at_plus,alias_at_neg,alias_at_binary_minus,alias_at_prod,alias_at_division,
    // 12
    alias_at_inv,alias_at_pow,alias_at_exp,alias_at_ln,alias_at_abs,
    // 22
    alias_at_arg,alias_at_pnt,alias_at_point,alias_at_segment,alias_at_sto,
    // 32
    alias_at_sin,alias_at_cos,alias_at_tan,alias_at_asin,alias_at_acos,
    // 42
    alias_at_atan,alias_at_sinh,alias_at_cosh,alias_at_tanh,alias_at_asinh,
    // 52
    alias_at_acosh,alias_at_atanh,alias_at_interval,alias_at_union,alias_at_minus,
    // 62
    alias_at_intersect,alias_at_not,alias_at_and,alias_at_ou,alias_at_inferieur_strict,
    // 72
    alias_at_inferieur_egal,alias_at_superieur_strict,alias_at_superieur_egal,alias_at_different,alias_at_equal,
    // 82
    alias_at_rpn_prog,alias_at_local,alias_at_return,alias_at_Dialog,alias_at_double_deux_points,
    // 92
    alias_at_pointprod,alias_at_pointdivision,alias_at_pointpow,alias_at_hash,alias_at_pourcent,
    // 102
    alias_at_tilocal,alias_at_break,alias_at_continue,alias_at_ampersand_times,alias_at_maple_lib,
    // 112
    alias_at_unit,alias_at_plot_style,alias_at_xor,alias_at_check_type,alias_at_quote_pow,
    // 122
    alias_at_case,alias_at_dollar,alias_at_IFTE,alias_at_RPN_CASE,alias_at_RPN_LOCAL,
    // 132
    alias_at_RPN_FOR,alias_at_RPN_WHILE,alias_at_NOP,alias_at_unit,alias_at_ifte,
    // 142
    alias_at_for,alias_at_bloc,alias_at_program,alias_at_same,alias_at_increment,
    // 152
    alias_at_decrement,alias_at_multcrement,alias_at_divcrement,alias_at_sq,alias_at_display,
    // 162
    alias_at_of,alias_at_at,alias_at_normalmod,
    0
  };
  const unary_function_ptr * _archive_function_tab = (const unary_function_ptr *) &archive_function_tab_alias;
  const unary_function_ptr * archive_function_tab(){
    return _archive_function_tab;
  }
  int archive_function_tab_length=sizeof(archive_function_tab_alias)/sizeof(unsigned long);

#endif

  int archive_function_index(const unary_function_ptr & f){
    return f.ptr()?(f.ptr()->index_quoted_function >> 1):0;
    return equalposcomp(archive_function_tab(),f);
  }

  int equalposcomp(const unary_function_ptr tab[],const unary_function_ptr & f){
    for (int i=1;*((unsigned long *)tab)!=0;++tab,++i){
      if (*tab==f)
	return i;
    }
    return 0;
  }

  int equalposcomp(const vector<const unary_function_ptr * > & tab,const unary_function_ptr * f){
    vector<const unary_function_ptr * >::const_iterator it=tab.begin(),itend=tab.end();
    for (int i=1;it!=itend;++it,++i){
      if (**it==*f)
	return i;
    }
    return 0;
  }

  int equalposcomp(const const_unary_function_ptr_ptr tab[],const unary_function_ptr & f){
    for (int i=1;*((unsigned long *)tab)!=0;++tab,++i){
      if (**tab==f)
	return i;
    }
    return 0;
  }

  symbolic::symbolic(const symbolic & mys,const gen & e): sommet(mys.sommet){
    vecteur tmp;
    if (mys.feuille.type==_VECT){
      tmp = *mys.feuille._VECTptr;
      tmp.push_back(e);
    }
    else {
      tmp.push_back(mys.feuille);
      tmp.push_back(e);
    }
    feuille = gen(tmp,_SEQ__VECT);
  };
  
  symbolic::symbolic(const gen & a,const unary_function_ptr & o,const gen & b):sommet(o) {
    if (b.type==_VECT)
      feuille=gen(mergevecteur(vecteur(1,a),*b._VECTptr),b.subtype);
    else
      feuille=gen(makevecteur(a,b),_SEQ__VECT);
  };
  
  symbolic::symbolic(const gen & a,const unary_function_ptr * o,const gen & b):sommet(*o) {
    if (b.type==_VECT)
      feuille=gen(mergevecteur(vecteur(1,a),*b._VECTptr),b.subtype);
    else
      feuille=gen(makevecteur(a,b),_SEQ__VECT);
  };
  
  int symbolic::size() const {
    if (feuille.type==_SYMB)
      return 1+feuille._SYMBptr->size();
    if (feuille.type!=_VECT)
      return 2;
    int s=1;
    iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    for (;it!=itend;++it){
      if (it->type==_SYMB)
	s += it->_SYMBptr->size();
      else
	++s;
    }
    return s;
  }

  bool print_rewrite_prod_inv=false;

  static string & add_print_plus(string & s,const symbolic & g,GIAC_CONTEXT){
    if (g.feuille.type!=_VECT){
      s += '+';
      return add_print(s,g.feuille,contextptr);
    }
    vecteur & v = *g.feuille._VECTptr;
    if (v.empty())
      return s;
    int l=v.size();
    if (l==1)
      s += '+';
    add_print(s, v.front(),contextptr);
    for (int i=1;i<l;++i){
      s += '+';
      add_print(s,v[i],contextptr);
    }
    return s;
  }

  static string & add_print_neg(string & s,const gen & feuille,GIAC_CONTEXT){
    int l = s.size();
    // +- and -- -> - and +
    if (l && s[l-1]=='+')
      s[l-1]='-';
    else {
      if (l && s[l-1]=='-')
	s[l-1]='+';
      else
	s += calc_mode(contextptr)==38?"−":"-";
    }
    if (feuille.type!=_CPLX){ 
      if(feuille.type!=_SYMB || (feuille._SYMBptr->sommet==at_inv || feuille._SYMBptr->sommet==at_prod) || !need_parenthesis(feuille)){
	return add_print(s,feuille,contextptr);
      }
    }
    s += "(";
    add_print(s,feuille,contextptr); 
    s += ")";
    return s;
  }

  static string & add_print_inv(string & s,const gen & feuille,GIAC_CONTEXT){
    gen f = feuille;
    bool isneg=false;
    bool calc38=calc_mode(contextptr)==38;
    if (f.is_symb_of_sommet(at_neg) && f._SYMBptr->feuille.type!=_VECT){
      f = f._SYMBptr->feuille;
      isneg=true;
    }
    if (f.type<_CPLX && is_positive(-f,contextptr)){
      f=-f;
      isneg=!isneg;
    }
    int l=s.size();
    if (isneg && l && s[l-1]=='-'){
      if (l==1) 
	s.clear();
      else
	s[l-1]='+';
      isneg=false;
    }
    if (isneg && l && s[l-1]=='+'){
      s[l-1]='-';
      isneg=false;
    }
    if ( !(f.type==_SYMB && ((f._SYMBptr->sommet==at_plus) || (f._SYMBptr->sommet==at_prod) || need_parenthesis(f._SYMBptr->sommet) || f._SYMBptr->sommet==at_inv )) && (f.type!=_CPLX) && (f.type!=_MOD)){
      s += (isneg?(calc38?"−1/":"-1/"):"1/");
      return add_print(s,f,contextptr);
    }
    else {
      s += (isneg?(calc38?"−1/(":"-1"):"1/(");
      add_print(s,f,contextptr);
      return s += ")";
    }
  }

  static string & add_print_prod(string & s,const symbolic & g,GIAC_CONTEXT){
    gen n0,d0;
    if (print_rewrite_prod_inv &&
	rewrite_prod_inv(g.feuille,n0,d0)
	){
      if (n0.type<_CPLX || n0.type==_IDNT){
	add_print(s,n0,contextptr);
	s += "/";
      }
      else {
	s +="(";
	add_print(s,n0,contextptr);
	s += ")/";
      }
      if (d0.type<_CPLX || d0.type==_IDNT)
	add_print(s,d0,contextptr); // s += d0.print(contextptr);
      else {
	s += "("; 
	add_print(s,d0,contextptr); s += ")"; // s += "("+d0.print(contextptr)+")";
      }
      return s;
    }
    if (g.feuille.type!=_VECT)
      return add_print(s,g.feuille,contextptr);
    vecteur & v = *g.feuille._VECTptr;
    int l=v.size();
    for (int i=0;i<l;++i){
      gen e(v[i]);
      if (e.type!=_SYMB){
	if (i)
	  s += '*';
	if ( (e.type==_CPLX) || (e.type==_MOD) )
	  s += "("+e.print(contextptr)+")";
	else 
	  add_print(s,e,contextptr); // s +=e.print(contextptr);
      }
      else {
	if (e._SYMBptr->sommet==at_inv){
	  gen f(e._SYMBptr->feuille);
	  if (i){
	    if ( (f.type==_CPLX) || (f.type==_MOD) ||
		 ((f.type==_SYMB) && 
		  ( (f._SYMBptr->sommet==at_plus) || (f._SYMBptr->sommet==at_prod) || need_parenthesis(f._SYMBptr->sommet) || f._SYMBptr->sommet==at_inv ))
		 ){
	      s += "/(";
	      add_print(s,f,contextptr);
	      s += ")"; // s += ("/("+f.print(contextptr) + ")");
	    }
	    else {
	      s += "/";
	      add_print(s,f,contextptr);
	      // s += ("/"+f.print(contextptr));
	    }
	  }
	  else
	    add_print(s,e,contextptr); // s += e.print(contextptr);
	} // end if e._SYMBptr->sommet==at_inv
	else {
	  if (i)
	    s += '*';
	  if ( (e._SYMBptr->sommet==at_plus) || (e._SYMBptr->sommet==at_neg) ){
	    s += "(";
	    add_print(s,e,contextptr);
	    s += ")";
	    // s += ("("+e.print(contextptr)+")");
	  }
	  else
	    add_print(s,e,contextptr); // s += e.print(contextptr);
	}
      }
    } // end_for
    return s;
  }

  static string & add_print_pow(string & s,const symbolic & g,GIAC_CONTEXT){
    if (g.feuille.type!=_VECT || g.feuille._VECTptr->size()!=2)
      return add_print(s,g.feuille,contextptr);
    gen pui=g.feuille._VECTptr->back();
    gen arg=g.feuille._VECTptr->front();
#ifndef GIAC_HAS_STO_38
    if (__pow.printsommet==&cprintaspow){
      s+="pow(";
      add_print(s,arg,contextptr);
      s+=",";
      add_print(s,pui,contextptr);
      return s+=')';
    }
#endif
    if (abs_calc_mode(contextptr)==38){
      bool need=need_parenthesis(arg);
      if (pui==plus_one_half){
	s += (need?"√(":"√");
	add_print(s,arg,contextptr);
	return s += (need?")":"");
      }
      if ( pui==minus_one_half  || pui==fraction(minus_one,plus_two) ){
	s += (need?"1/√(":"1/√");
	add_print(s,arg,contextptr);
	return s += (need?")":"");
      }
      if (pui==minus_one){
	s += (need?"(":"");
	add_print(s,arg,contextptr);
	return s += (need?")":"");
	return s += (need?")\xe2\x81\xb2":"\xe2\x81\xb2");
      }
      if (pui==plus_two){
	s += (need?"(":"");
	add_print(s,arg,contextptr);
	return s += (need?")²":"²");
      }
    }
    if (pui==plus_one_half){
      s += "sqrt(";
      add_print(s,arg,contextptr);
      return s += ')'; 
    }
    if ( pui==minus_one_half  || pui==fraction(minus_one,plus_two) ){
      s += "1/sqrt(";
      add_print(s,arg,contextptr);
      return s += ')';
    }
    if (arg.type==_IDNT || (arg.type==_SYMB && arg._SYMBptr->sommet!=at_neg && !arg._SYMBptr->sommet.ptr()->printsommet)){
      if (pui.type==_SYMB || pui.type==_FRAC){
	add_print(s,arg,contextptr);
#ifdef GIAC_HAS_STO_38
	s += '^';
#else
	s += __pow.s;
#endif
	s += '(';
	add_print(s,pui,contextptr);
	return s += ')';
      }
      else {
	add_print(s,arg,contextptr);
#ifdef GIAC_HAS_STO_38
	s += '^';
#else
	s += __pow.s;
#endif
	return add_print(s,pui,contextptr);
      }
    }
    bool argpar = ( (arg.type>_CPLX && arg.type!=_FLOAT_) || !is_positive(arg,contextptr)) && arg.type!=_IDNT ;
    if (argpar)
      s += '(';
    add_print(s,arg,contextptr);
    if (argpar)
      s += ')';
#ifdef GIAC_HAS_STO_38
    s += '^';
#else
    s += __pow.s;
#endif
    bool puipar = pui.type==_SYMB || pui.type==_FRAC || pui.type==_CPLX || (pui.type==_VECT && pui.subtype==_SEQ__VECT);
    if (puipar)
      s += '(';
    add_print(s,pui,contextptr);
    if (puipar)
      s += ')';
    return s ;      
  }

  static string & add_print_int(string & s,int i,GIAC_CONTEXT){
    char ch[32];
    int l=s.size();
    if (i<0){
      if (l && s[l-1]=='+')
	s[l-1]='-';
      else {
	if (s[l-1]=='-'){
	  if (l==1) 
	    s.clear();
	  else
	    s[l-1]='+';
	}
	else
	  s +=  (calc_mode(contextptr)==38)?"−":"-"; // add a minus
      }
      i=-i;
    }
    switch (integer_format(contextptr)){
    case 16:
      sprintf(ch,"0x%X",i);
    case 8:
      sprintf(ch,"0o%o",i);
    default:
      sprintf(ch,"%d",i);
    }
    s += ch;
    return s;
  }

  static string & add_print_symbolic(string & s,const symbolic & g,GIAC_CONTEXT){
    if (!g.sommet.ptr()){
      s+="NULL(";
      s+=g.feuille.print();
      s+=")";
      return s;
    }
    if ( g.sommet.ptr()->printsommet && g.sommet!=at_plus && g.sommet!=at_prod && g.sommet!=at_pow)
       return s += g.sommet.ptr()->printsommet(g.feuille,g.sommet.ptr()->s,contextptr);
    if ( g.feuille.type==_VECT && g.feuille._VECTptr->empty() ){
      s += g.sommet.ptr()->print(contextptr);
      s += "(NULL)";
      return s;
    }
    if (g.sommet==at_prod)
      return add_print_prod(s,g,contextptr);
    if (g.sommet==at_plus)
      return add_print_plus(s,g,contextptr);
    if (g.sommet==at_pow)
      return add_print_pow(s,g,contextptr);
    if (g.sommet==at_neg)
      return add_print_neg(s,g.feuille,contextptr);
    if (g.sommet==at_inv)
      return add_print_inv(s,g.feuille,contextptr);
    if ( g.feuille.type!=_VECT || ( g.sommet!=at_prod && g.feuille._VECTptr->front().type==_VECT ) ){
      s += g.sommet.ptr()->print(contextptr);
      s += '(';
      add_print(s,g.feuille,contextptr);
      s += ')';
      return s;
    }
    int l=g.feuille._VECTptr->size();
    s += g.sommet.ptr()->print(contextptr);
    s += '(';
    if (g.feuille.subtype!=_SEQ__VECT)
      s += begin_VECT_string(g.feuille.subtype,false,contextptr);
    for (int i=0;;++i){
      add_print(s,(*(g.feuille._VECTptr))[i],contextptr); // s += (*(feuille._VECTptr))[i].print(contextptr);
      if (i==l-1){
	break;
      }
      s += ',';
    }
    if (g.feuille.subtype!=_SEQ__VECT)
      s += end_VECT_string(g.feuille.subtype,false,contextptr);
    return s += ')';
  }

  string & add_print(string & s,const gen & g,GIAC_CONTEXT){
    if (g.type==_IDNT)
      return (s += g._IDNTptr->print(contextptr));
    int l=s.size();
    if (g.type==_INT_ && g.subtype==0)
      return add_print_int(s,g.val,contextptr);
    if (g.type==_VECT && g.subtype==0){
      s += calc_mode(contextptr)==1?'{':'[';
      add_printinner_VECT(s,*g._VECTptr,0,contextptr);
      return s += calc_mode(contextptr)==1?'}':']';
    }
    if (g.type==_FRAC && g._FRACptr->num.type==_INT_ && g._FRACptr->den.type==_INT_){
      add_print(s,g._FRACptr->num,contextptr);
      s += "/";
      add_print(s,g._FRACptr->den,contextptr);
      return s;
    }
    if (g.type==_SYMB)
      return add_print_symbolic(s,*g._SYMBptr,contextptr);
    const string & tmp=g.print(contextptr);
    // check +- -> - and -- -> +
    if (l && s[l-1]=='+' ){
      if (!tmp.empty() && tmp[0]=='-'){
	s = s.substr(0,l-1);
	return s += tmp;
      }
      if (tmp.size()>3 && (unsigned char)tmp[0]==226 && (unsigned char)tmp[1]==136 && (unsigned char) tmp[2]==146 ) { // -30, -120, -110
	s[l-1]='-';
	return s += tmp.substr(3,tmp.size()-3);
      }
    }
    if (l && s[l-1]=='-' ){
      if (!tmp.empty() && tmp[0]=='-'){
	s[l-1]='+';
	return s += tmp.substr(1,tmp.size()-1);
      }
      if (tmp.size()>3 && (unsigned char)tmp[0]==226 && (unsigned char)tmp[1]==136 && (unsigned char) tmp[2]==146 ) { // -30, -120, -110
	s[l-1]='+';
	return s += tmp.substr(3,tmp.size()-3);
      }
    }
    return s += tmp;
  }

  string symbolic::print(GIAC_CONTEXT) const{
    string s;
    add_print_symbolic(s,*this,contextptr);
    return s;
  }

  /* EVAL without system stack */

  static void eval_sto_pnt_symb(const gen & feuille,gen & e,GIAC_CONTEXT){
    if (e.type==_SYMB && e.ref_count()==1 && e._SYMBptr->feuille.type==_VECT && e._SYMBptr->feuille.ref_count()==1){
      vecteur & v=*e._SYMBptr->feuille._VECTptr;
      // legende not converted to string to avoid malloc ->faster
      v.push_back(feuille._VECTptr->back());
      // v=makevecteur(v.front(),v.back(),string2gen(feuille._VECTptr->back().print(contextptr),false));
      e._SYMBptr->feuille.subtype=_PNT__VECT;
    }
    else
      e=new_ref_symbolic(symbolic(at_pnt,gen(makevecteur(e._SYMBptr->feuille._VECTptr->front(),e._SYMBptr->feuille._VECTptr->back(),string2gen(feuille._VECTptr->back().print(contextptr),false)),_PNT__VECT)));
    e.subtype=gnuplot_show_pnt(*e._SYMBptr,contextptr);
  }

  static void eval_sto_pnt_vect(const gen & feuilleback,gen & e,GIAC_CONTEXT){
    vecteur v=*e._VECTptr;
    iterateur it=v.begin(),itend=v.end();
    gen legende;
    for (int pos=0;it!=itend;++pos,++it){
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_pnt) && (it->_SYMBptr->feuille._VECTptr->size()==2)){
	if (feuilleback.type==_VECT && pos<int(feuilleback._VECTptr->size()))
	  legende=(*feuilleback._VECTptr)[pos];
	else
	  legende=feuilleback;
	if (legende.type==_IDNT && abs_calc_mode(contextptr)==38){
	  // HP: if legende is an IDNT, keep it as legende
	  // this save alloc (2 alloc instead of 3 alloc + 1 print
	  *it=new_ref_symbolic(symbolic(at_pnt,gen(makevecteur(it->_SYMBptr->feuille._VECTptr->front(),it->_SYMBptr->feuille._VECTptr->back(),legende),_PNT__VECT)));
	}
	else {
	  *it=new_ref_symbolic(symbolic(at_pnt,gen(makevecteur(it->_SYMBptr->feuille._VECTptr->front(),it->_SYMBptr->feuille._VECTptr->back(),string2gen(legende.print(contextptr),false)),_PNT__VECT)));
	}
	it->subtype=gnuplot_show_pnt(*it->_SYMBptr,contextptr);
      }
    }
    e=gen(v,e.subtype);
  }

  static gen eval_sto(const gen & feuille,std::vector<const char *> & last,int level,GIAC_CONTEXT){ // autoname function
    gen ans;
    gen & feuilleback=feuille._VECTptr->back();
    vecteur & lastarg=last_evaled_arg(contextptr);
    if ( feuilleback.type==_SYMB && (feuilleback._SYMBptr->sommet==at_unquote || feuilleback._SYMBptr->sommet==at_hash ) ){
      ans=_sto(feuille.eval(level,contextptr),contextptr);
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
    bool b=show_point(contextptr),quotearg=false;
    if (b)
      show_point(false,contextptr);
    gen e=feuille._VECTptr->front();
#ifdef GIAC_HAS_STO_38 // quote STO> E, STO> F, STO>R, STO> X, STO>Y
    if (feuilleback.type==_IDNT){
      const char * ch = feuilleback._IDNTptr->id_name;
      if (strlen(ch)==2 && ch[1]>='0' && ch[1]<='9' && (ch[0]=='E' || ch[0]=='F' || ch[0]=='X' || ch[0]=='Y'))
	quotearg=true;
    }
#endif
    if (!quotearg)
      e=e.eval(level,contextptr);
    if (b)
      show_point(b,contextptr);
    if (e.type==_SYMB && e._SYMBptr->sommet==at_pnt && e._SYMBptr->feuille.type==_VECT && e._SYMBptr->feuille._VECTptr->size()==2 && (contextptr?!contextptr->previous:!protection_level) )
      eval_sto_pnt_symb(feuille,e,contextptr);
    if ( e.type==_VECT && !e._VECTptr->empty() && e._VECTptr->back().type==_SYMB && e._VECTptr->back()._SYMBptr->sommet==at_pnt && (contextptr?!contextptr->previous:!protection_level))
      eval_sto_pnt_vect(feuilleback,e,contextptr);
    ans=sto(e,feuilleback,contextptr);
    if (!last.empty())
      last.pop_back();
    if (!lastarg.empty())
      lastarg.pop_back();
    return ans;
  } // end sommet==at_sto

  // http://mitpress.mit.edu/sicp/full-text/book/book-Z-H-34.html#%_sec_5.4
  enum {
    nr_eval_dispatch=1,
    nr_eval_vect=2,
    nr_eval_of=18, 
    nr_eval_op=19, // below this increment destination
    nr_eval_prog=21, // above this do not increment destination
    nr_eval_sto=22, 
    nr_eval_local=23,
    nr_eval_bloc=24,
    nr_eval_if_cond=25,
    nr_eval_if_true=26,
    nr_eval_if_false=27,
    nr_eval_for_init=28,
    nr_eval_for_cond=29,
    nr_eval_for_loop=30,
    nr_eval_for_incr=31,
    nr_eval_for_in=32,
    nr_eval_catch=34,
  };

#define NR_INIT_STACK_SIZE 200

  struct nr_pointers_t{
    gen * itbeg;
    unsigned curpos,endpos;
    unsigned arglpos;
    gen old;
    vecteur argl;
    signed char state;
    nr_pointers_t(unsigned _arglpos,gen * _itbeg,gen * _it,gen * _itend,gen _old,int argls,signed char _state):itbeg(_itbeg),curpos(_it-_itbeg),endpos(_itend-_itbeg),arglpos(_arglpos),old(_old),argl(argls),state(_state) {};
    nr_pointers_t():itbeg(0),curpos(0),endpos(0),arglpos(0),old(0),argl(0),state(0){}
  };

  inline void fromto_restore(unsigned & arglpos,gen * & itbeg,gen * & it,gen * & itend,gen & old,vecteur & argl,signed char & state,vector<nr_pointers_t> & fromto_stack){
    vector<nr_pointers_t>::iterator ptr=fromto_stack.end()-1;
    arglpos=ptr->arglpos;
    itbeg=ptr->itbeg;
    it=ptr->itbeg+ptr->curpos;
    itend=ptr->itbeg+ptr->endpos;
    old=ptr->old;
    argl.swap(ptr->argl);
    state=ptr->state;
    fromto_stack.pop_back();
  }

  struct nr_prog {
    context * contextptr;
    gen save_debug_info,vars;
    bool save_sst_mode;
    int protect;
    nr_prog(context * _contextptr,const gen & _save_debug_info,const gen & _vars,bool _save_sst_mode,int _protect):contextptr(_contextptr),save_debug_info(_save_debug_info),vars(_vars),save_sst_mode(_save_sst_mode),protect(_protect) {};
    nr_prog() : contextptr(0),save_debug_info(0),vars(0),save_sst_mode(false),protect(0) {}
  };

  // name changed: BESTA compiler see this as repeated code with same sig as passing the itor itself 
  // - compiler whinges like a wounded pig --
  static void increment_instruction_ptr(const gen * it0,const gen * itend,GIAC_CONTEXT){
    const gen * it=it0;
    for (;it!=itend;++it)
      increment_instruction(*it,contextptr);
  }

  // nr_eval is slower than usual eval because it saves what is usually saved
  // on stack on the heap. It does not use stack for recursive calls
  // Should be used if we are near the bottom of the stack
  gen nr_eval(const gen & g,int level,const context * ct){
    context * contextptr=(context *) ct;
    gen *itbeg, * it, *itend; // source, begin, current and end of evaluation
    gen * destination; // destination of evaluation result
    gen res; // the final answer
    gen tmp;
    unsigned arglpos;
    vector<nr_pointers_t> fromto_stack; // save/restore destination, source, source end pointers here
    fromto_stack.reserve(NR_INIT_STACK_SIZE);
    signed char state=nr_eval_dispatch;
    gen old=0; // destination
    gen progname=undef,label=undef,forlast,lasterr;
    vecteur argl(1); // current evaled vecteur
    vector<nr_prog> nr_prog_stack;
    vector<int> nr_eval_for_stack;
    nr_prog_stack.reserve(NR_INIT_STACK_SIZE);
    gen prog;
    itbeg=it=(gen *) &g; itend=it+1; destination=(gen *)&argl.front();
    while (1){
#ifndef NO_STDEXCEPT
      try {
#endif
	if (lasterr.type==_STRNG || (destination->type==_STRNG && destination->subtype==-1)){
	  // error!
	  debug_ptr(contextptr)->current_instruction += itend-it;
	  it=itend;
	  if (lasterr.type!=_STRNG)
	    lasterr=*destination;
	}
	if (it!=itend){
	  if (state>=nr_eval_prog){
	    if (destination->type==_SYMB && (destination->_SYMBptr->sommet==at_return || destination->_SYMBptr->sommet==at_break || (state!=nr_eval_for_incr && destination->_SYMBptr->sommet==at_continue) ) ){
	      ++it; // do not eval anymore until end of function or loop
	      ++debug_ptr(contextptr)->current_instruction;
	      continue;
	    }
	    // goto
	    if (!is_undef(label)){
	      if (it->is_symb_of_sommet(at_label) && label==it->_SYMBptr->feuille){
		*destination=*it;
		label=undef;
	      }
	      ++it;
	      if (it==itend){
		debug_ptr(contextptr)->current_instruction -= (itend-itbeg);
		for (it=itbeg;it!=itend;++it){
		  if (it->is_symb_of_sommet(at_label) && label==it->_SYMBptr->feuille){
		    *destination=*it;
		    label=undef;
		    break;
		  }
		  ++debug_ptr(contextptr)->current_instruction;
		}
	      }
	      else
		++debug_ptr(contextptr)->current_instruction;
	      continue;
	    }
	    if (state!=nr_eval_if_cond && state!=nr_eval_for_init && state!=nr_eval_for_cond && state!=nr_eval_for_incr && state!=nr_eval_sto){
	      if (state==nr_eval_for_loop && it==itbeg+3 && it->is_symb_of_sommet(at_bloc))
		;
	      else {
		++debug_ptr(contextptr)->current_instruction;
		if (debug_ptr(contextptr)->debug_mode){
		  debug_loop(res,contextptr);
		  if (is_undef(res)) return res;
		}
	      }
	    }
	  }
	  switch (it->type){
	  case _VECT:
	    if (it->subtype==_SPREAD__VECT){
	      makespreadsheetmatrice(*it->_VECTptr,contextptr);
	      spread_eval(*it->_VECTptr,contextptr);
	      *destination=*it;
	      ++it; 
	      if (it!=itend && state<=nr_eval_op)
		++destination;
	      continue;
	    }
	    if (it->subtype==_FOLDER__VECT || it->subtype==_RGBA__VECT){
	      ++it; 
	      if (it!=itend && state<=nr_eval_op)
		++destination;
	      continue;
	    }
	    fromto_stack.push_back(nr_pointers_t(destination-(gen *) &argl.front(),itbeg,it,itend,old,1,state));
	    argl.swap(fromto_stack.back().argl);
	    // vecteur beginning by comment?
	    tmp=*it;
	    if (it->subtype==_SEQ__VECT && !it->_VECTptr->empty() && it->_VECTptr->front().type==_SYMB 
		&& (it->_VECTptr->front()._SYMBptr->sommet==at_comment)
		&& (it->_VECTptr->back().is_symb_of_sommet(at_return))
		){
	      itend=(gen *) &it->_VECTptr->back()+1;
	      it=(gen *) &it->_VECTptr->front();
	      for (;it!=itend;++it){
		if ( (it->type!=_SYMB) || (it->_SYMBptr->sommet!=at_comment) )
		  break;
	      }
	      if (it+1==itend){
		*destination=*it;
		fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
		destination=(gen *) &argl.front()+arglpos;
		++it; 
		if (it!=itend && state<=nr_eval_op)
		  ++destination;
		continue;
	      }
	      if (it==itend){
		*destination=zero;
		fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
		destination=(gen *) &argl.front()+arglpos;
		++it; 
		if (it!=itend && state<=nr_eval_op)
		  ++destination;
		continue; 
	      }
	    }
	    else {
	      itend=(gen *)&it->_VECTptr->back()+1;
	      it=(gen *)&it->_VECTptr->front();
	    }
	    itbeg=it;
	    old=tmp;
	    // normal vector evaluation
	    state=nr_eval_vect;
	    argl.resize(itend-it);
	    // vecteur_stack.push_back(argl);
	    // argl=vecteur(itend-it);
	    destination=(gen *)&argl.front();
	    continue;
	  case _SYMB: {
	    // special cases
	    unary_function_ptr & u =it->_SYMBptr->sommet;
	    gen f=it->_SYMBptr->feuille;
	    if (u==at_quote){
	      *destination=quote(f,contextptr);
	      ++it; 
	      if (it!=itend && state<=nr_eval_op)
		++destination;
	      continue;
	    }
	    if (u==at_program) { 
	      *destination=quote_program(it->_SYMBptr->feuille,contextptr);
	      ++it; 
	      if (it!=itend && state<=nr_eval_op)
		++destination;
	      continue;
	    }
	    int specop=0;
	    if (u==at_goto)
	      label=f;
	    if (u==at_ifte || u==at_when)
	      specop=nr_eval_if_cond;
	    if (u==at_for){ 
	      if (state==nr_eval_for_cond){ // for in for_cond is for the syntax for x in l
		*destination=*it;
		++it;
		continue;
	      }
	      nr_eval_for_stack.push_back(0); // index for for var in list/string
	      nr_eval_for_stack.push_back(debug_ptr(contextptr)->current_instruction);
	      specop=nr_eval_for_init;
	    }
	    if (u==at_bloc)
	      specop=nr_eval_bloc;
	    if (u==at_local)
	      specop=nr_eval_local;
	    if (u==at_sto)
	      specop=nr_eval_sto;
	    if (u==at_of)
	      specop=nr_eval_of;
	    if (u==at_try_catch)
	      specop=nr_eval_catch;
	    if (!specop && u.quoted() && !f.is_symb_of_sommet(at_unquote)){ 
	      // check for hash
	      if (f.is_symb_of_sommet(at_hash) && f._SYMBptr->feuille.type==_STRNG)
		f=gen(*f._SYMBptr->feuille._STRNGptr,contextptr);
	      *destination=u(f,contextptr);
	      ++it; 
	      if (it!=itend && state<=nr_eval_op)
		++destination;
	      continue;
	    }
	    // save pointers and state, eval args
	    fromto_stack.push_back(nr_pointers_t(destination-(gen *)&argl.front(),itbeg,it,itend,old,1,state));
	    argl.swap(fromto_stack.back().argl);
	    state=specop?specop:nr_eval_op;
	    old=*it;
	    destination=(gen *)&argl.front();
	    if ( f.type==_VECT && 
		 (
		  (specop==nr_eval_sto && f._VECTptr->size()==2) ||
		  (specop==nr_eval_if_cond && f._VECTptr->size()>=3) ||
		  (specop==nr_eval_for_init && f._VECTptr->size()>=3) ||
		  (specop==nr_eval_catch && f._VECTptr->size()>=3)
		  ) ){ 
	      // sto: eval first arg, but not the second arg
	      // if: eval first arg, but not the second third arg
	      // for: eval first arg [init]
	      // try ... catch: eval first arg
	      itbeg=it=&f._VECTptr->front();
	      itend=it+1;
	      continue;
	    }
	    if (specop==nr_eval_local){
	      int protect=0;
	      gen vars,values;
	      context * save_contextptr=contextptr;
	      // Bind local var
	      prog=f;
	      vars=prog._VECTptr->front();
	      if (vars.type==_VECT && vars._VECTptr->size()==2 && vars._VECTptr->front().type!=_IDNT)
		vars = vars._VECTptr->front();
	      if (vars.type!=_VECT)
		vars=makevecteur(vars);
	      values=gen(vecteur(vars._VECTptr->size()));
	      for (unsigned i=0;i<vars._VECTptr->size();++i){
		tmp=(*vars._VECTptr)[i];
		if (tmp.is_symb_of_sommet(at_sto) || tmp.is_symb_of_sommet(at_equal)){
		  (*values._VECTptr)[i]=tmp._SYMBptr->feuille._VECTptr->back();
		  (*vars._VECTptr)[i]=tmp._SYMBptr->feuille._VECTptr->front().eval(1,contextptr);
		}
	      }
	      prog=prog._VECTptr->back();
	      protect=bind(*values._VECTptr,*vars._VECTptr,contextptr);
	      if (protect==-RAND_MAX){
		gensizeerr(res,contextptr);
		return res;
	      }
	      // save previous state
	      nr_prog_stack.push_back(nr_prog(save_contextptr,0,vars,false,protect));
	      if (prog.type==_VECT && prog.subtype==0){
		itbeg=it=(gen *)&prog._VECTptr->front();
		itend=(gen *)&prog._VECTptr->back()+1;
	      }
	      else {
		itbeg=it=&prog;
		itend=it+1;
	      }
	      continue;
	    }
	    // if (specop==nr_eval_of || specof==nr_eval_bloc){
	    //  state=specop; // program name is in old, eval args normally
	    // }
	    // normal symbolic
	    if (f.type==_VECT && (f.subtype==_SEQ__VECT || (specop==nr_eval_of || specop==nr_eval_bloc))){
	      itend=(gen *)&f._VECTptr->back()+1;
	      itbeg=it=(gen *)&f._VECTptr->front();
	      argl.resize(itend-it);
	      destination=(gen *)&argl.front();
	    }
	    else {
	      itbeg=it=&it->_SYMBptr->feuille; // do not use &f here!
	      itend=it+1;
	    }
	    continue;
	  }
	  case _IDNT:
	    tmp=eval(*it,level,contextptr);
	    if (tmp.type!=_VECT || tmp.subtype!=_SEQ__VECT || (state!=nr_eval_vect && !(state==nr_eval_op && old.type==_SYMB && old._SYMBptr->feuille.type==_VECT)))
	      *destination=tmp;
	    else { 
	      // tmp is a sequence inside a vector, 
	      // enlarge argl and copy tmp._VECTptr in argl
	      int pos=destination-(gen *)&argl.front();
	      for (unsigned i=1;i<tmp._VECTptr->size();++i)
		argl.push_back(0);
	      destination=(gen *)&argl.front()+pos;
	      for (unsigned i=0;;){
		*destination=(*tmp._VECTptr)[i];
		++i;
		if (i==tmp._VECTptr->size())
		  break;
		++destination;
	      }
	    }
	    ++it; 
	    if (it!=itend && state<=nr_eval_op)
	      ++destination;
	    continue;
	  default:
	    *destination=*it;
	    ++it; 
	    if (it!=itend && state<=nr_eval_op)
	      ++destination;
	    continue;
	  } // end switch on it->type
	}
	// it==itend
	// end eval of current vector
	signed char oldsubtype=old.type==_VECT?old.subtype:0;
	if (fromto_stack.empty()){
	  return argl.front(); // end evaluation
	} 
	// dispatch depending on current mode
	switch (state){
	case nr_eval_dispatch:
	  continue;
	case nr_eval_vect:
	  // end eval of vecteur, store value in res
	  if (oldsubtype==_SET__VECT && !argl.empty()){
	    // remove multiple occurences
	    sort(argl.begin(),argl.end(),islesscomplexthanf);
	    vecteur tmp;
	    tmp.reserve(argl.size());
	    tmp.push_back(argl.front());
	    for (iterateur jt=argl.begin()+1;jt!=argl.end();++jt){
	      if (*jt!=tmp.back())
		tmp.push_back(*jt);
	    }
	    tmp.swap(argl);
	    res=gen(argl,oldsubtype);
	  }
	  else {
	    // FIXME: make a faster == check (may returns false for large == vecteurs)
	    if (old.type==_VECT && old._VECTptr->size()<17 && *old._VECTptr==argl)
	      res=old;
	    else
	      res=gen(argl,oldsubtype);
	  }
	  // restore previous state, old, pointers and argl
	  fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
	  destination=(gen *)&argl.front()+arglpos;
	  *destination=res;
	  ++it;
	  if (it!=itend && state<=nr_eval_op)
	    ++destination;
	  continue; // end state==nr_eval_vect
	case nr_eval_for_init:
	  forlast=undef;
	  ++itend;
	  state=nr_eval_for_cond;
	  continue;
	case nr_eval_for_cond:
	  res=*destination;
	  if ( nr_eval_for_stack.size()>=2 && (res.is_symb_of_sommet(at_for) || res.is_symb_of_sommet(at_pour)) && res._SYMBptr->feuille.type==_VECT && res._SYMBptr->feuille._VECTptr->size()==2){ 
	    // for var in list/string
	    res=eval(res._SYMBptr->feuille._VECTptr->back(),1,contextptr);
	    if (res.type==_VECT){
	      if (int(res._VECTptr->size())>nr_eval_for_stack[nr_eval_for_stack.size()-2]){
		res=res[nr_eval_for_stack[nr_eval_for_stack.size()-2]];
		sto(res,destination->_SYMBptr->feuille._VECTptr->front(),contextptr);
		res=1;
	      }
	      else
		res=0;
	    }
	    else {
	      if (res.type==_STRNG){
		if (int(res._STRNGptr->size())>nr_eval_for_stack[nr_eval_for_stack.size()-2]){
		  res=string2gen(string(1,(*res._STRNGptr)[nr_eval_for_stack[nr_eval_for_stack.size()-2]]),false);
		  sto(res,destination->_SYMBptr->feuille._VECTptr->front(),contextptr);
		  res=1;
		}
		else
		  res=0;
	      }
	      else
		res=0;
	    }
	    ++nr_eval_for_stack[nr_eval_for_stack.size()-2];
	  }
	  res=equaltosame(res).eval(eval_level(contextptr),contextptr);
	  if (!is_integer(res)){
	    res=res.evalf_double(eval_level(contextptr),contextptr);
	    if ( res.type!=_DOUBLE_ && res.type!=_CPLX ){
	      if (old._SYMBptr->sommet==at_for){
		if (!nr_eval_for_stack.empty()) // pop back instruction and for in index
		  nr_eval_for_stack.pop_back();
		if (!nr_eval_for_stack.empty())
		  nr_eval_for_stack.pop_back();
		fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
		destination=(gen *)&argl.front()+arglpos;
		gensizeerr(gettext("For: Unable to check test"),*destination); 
		res=*destination;
		it=itend;
		continue;
	      }
	      else
		res=old;
	    }
	  }
	  if (!is_zero(res)){ // TRUE
	    it++; itend+=2;
	    state=nr_eval_for_loop;
	  }
	  else {
	    increment_instruction_ptr(it+1,it+3,contextptr);
	    if (!nr_eval_for_stack.empty()) // pop back instruction and for in index
	      nr_eval_for_stack.pop_back();
	    if (!nr_eval_for_stack.empty())
	      nr_eval_for_stack.pop_back();
	    // restore state and pointers
	    fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
	    destination=(gen *)&argl.front()+arglpos;
	    *destination=forlast; 
	    ++it;
	    if (it!=itend && state<=nr_eval_op)
	      ++destination;
	  }
	  continue;
	case nr_eval_for_loop:
	  if (destination->type==_SYMB && (destination->_SYMBptr->sommet==at_break || destination->_SYMBptr->sommet==at_return)){
	    if (destination->_SYMBptr->sommet==at_break)
	      *destination=undef;
	    if (!nr_eval_for_stack.empty()) // pop back instruction and for in index
	      nr_eval_for_stack.pop_back();
	    if (!nr_eval_for_stack.empty())
	      nr_eval_for_stack.pop_back();
	    state=nr_eval_bloc;
	  }
	  else {
	    it -= 2; --itend;
	    state=nr_eval_for_incr;
	    forlast=*destination;
	  }
	  continue;
	case nr_eval_for_incr:
	  if (!nr_eval_for_stack.empty())
	    debug_ptr(contextptr)->current_instruction=nr_eval_for_stack.back();
	  if (debug_ptr(contextptr)->debug_mode){
	    debug_loop(res,contextptr);
	    if (is_undef(res)) return res;
	  }
	  state=nr_eval_for_cond;
	  it -= 2;
	  itend -= 1;
	  continue;
	case nr_eval_catch:
	  // catch lasterr, FIXME for debug
	  state=nr_eval_if_false;
	  if (lasterr.type==_STRNG){
	    lasterr.subtype=0;
	    sto(lasterr,*it,contextptr);
	    ++it;
	    itend += 2;
	    lasterr=*destination=res=0;
	    continue;
	  }
	  increment_instruction(*(it+1),contextptr);
	  if (old._SYMBptr->feuille._VECTptr->size()==4){
	    it += 2;
	    itend += 3;
	  }
	  continue;
	case nr_eval_if_cond:
	  res=*destination;
	  res=equaltosame(res).eval(eval_level(contextptr),contextptr);
	  if (!is_integer(res)){
	    res=res.evalf_double(eval_level(contextptr),contextptr);
	    if ( res.type!=_DOUBLE_ && res.type!=_CPLX ){
	      if (old._SYMBptr->sommet==at_ifte){
		fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
		destination=(gen *)&argl.front()+arglpos;
		gensizeerr(gettext("Ifte: Unable to check test"),*destination); 
		res=*destination;
		it=itend;
		continue;
	      }
	      else
		res=old;
	    }
	  }
	  if (!is_zero(res)){ // TRUE
	    state=nr_eval_if_true;
	    ++itend;
	  }
	  else { //FALSE
	    if (old._SYMBptr->sommet==at_ifte){
	      increment_instruction(*it,contextptr);
	    }
	    state=nr_eval_if_false;
	    ++it; itend+=2;
	  }
	  continue;
	case nr_eval_of:
	  if (argl.size()==2 && argl.front().is_symb_of_sommet(at_program)){
	    debug_struct * dbgptr=debug_ptr(contextptr);
	    int protect=0;
	    bool save_sst_mode=dbgptr->sst_mode;
	    gen vars,values;
	    context * save_contextptr=contextptr;
	    dbgptr->sst_at_stack.push_back(dbgptr->sst_at);
	    dbgptr->sst_at.clear();
	    progname=old._SYMBptr->feuille._VECTptr->front();
	    if (progname.is_symb_of_sommet(at_program))
	      progname=undef;
	    if (progname.type==_IDNT)
	      adjust_sst_at(progname,contextptr);
	    dbgptr->current_instruction_stack.push_back(dbgptr->current_instruction);
	    dbgptr->current_instruction=0;
	    if (dbgptr->sst_in_mode){
	      dbgptr->sst_in_mode=false;
	      dbgptr->sst_mode=true;
	    }
	    else
	      dbgptr->sst_mode=false;
	    // Bind local var
	    prog=argl.front()._SYMBptr->feuille;
	    vars=prog._VECTptr->front();
	    values=argl[1];
	    prog=prog._VECTptr->back();
	    if (vars.type!=_VECT)
	      vars=gen(makevecteur(vars));
	    if (values.type!=_VECT || values.subtype!=_SEQ__VECT || (vars._VECTptr->size()==1 && values._VECTptr->size()!=1))
	      values=gen(makevecteur(values));
	    // *logptr(contextptr) << vars << " " << values << endl;
	    // removed sst test so that when a breakpoint is evaled
	    // the correct info is displayed
	    (*dbgptr->fast_debug_info_ptr)=prog;
	    (*dbgptr->debug_info_ptr)=prog;
	    if (!vars._VECTptr->empty())
	      protect=bind(*values._VECTptr,*vars._VECTptr,contextptr);
	    if (protect==-RAND_MAX){
	      program_leave(*dbgptr->debug_info_ptr,save_sst_mode,dbgptr);
	      gensizeerr(res,contextptr);
	      return res;
	    }
	    // save previous state
	    nr_prog_stack.push_back(nr_prog(save_contextptr,*dbgptr->debug_info_ptr,vars,save_sst_mode,protect));
	    dbgptr->args_stack.push_back(gen(mergevecteur(vecteur(1,progname),*values._VECTptr)));
	    if (prog.type==_VECT && prog.subtype==0){
	      itbeg=it=(gen *)&prog._VECTptr->front();
	      itend=(gen *)&prog._VECTptr->back()+1;
	    }
	    else {
	      itbeg=it=&prog;
	      itend=it+1;
	    }
	    state=nr_eval_prog;
	  }
	  else
	    state=nr_eval_op; // like a goto nr_eval_op: below
	  continue;
	case nr_eval_if_true: 
	  if (old._SYMBptr->sommet==at_ifte)
	    increment_instruction(*it,contextptr);  // no break here
	case nr_eval_if_false:
	  res=*destination; // no break here
	case nr_eval_op: case nr_eval_sto: 
	  // eval operator
	  if (state==nr_eval_sto)
	    res=sto(*destination,old._SYMBptr->feuille._VECTptr->back(),contextptr);
	  else {
	    if (state==nr_eval_op){
	      if (old._SYMBptr->feuille.type==_VECT && old._SYMBptr->feuille.subtype==_SEQ__VECT)
		res=gen(argl,_SEQ__VECT);
	      else
		res=*destination;
	      res=(*old._SYMBptr->sommet.ptr())(res,contextptr);
	    }
	  }
	  // no break here -> restore
	case nr_eval_bloc:
	  // restore state and pointers
	  if (state==nr_eval_bloc) 
	    res=*destination;
	  fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
	  destination=(gen *)&argl.front()+arglpos;
	  *destination=res;
	  ++it;
	  if (it!=itend && state<=nr_eval_op)
	    ++destination;
	  continue;
	case nr_eval_prog: case nr_eval_local:
	  // end of program reached, restore context
	  res=*destination;
	  if (state==nr_eval_prog && res.is_symb_of_sommet(at_return))
	    res=res._SYMBptr->feuille;
	  if (!nr_prog_stack.back().vars._VECTptr->empty())
	    leave(nr_prog_stack.back().protect,*nr_prog_stack.back().vars._VECTptr,contextptr);
	  if (state==nr_eval_prog)
	    program_leave(nr_prog_stack.back().save_debug_info,nr_prog_stack.back().save_sst_mode,debug_ptr(contextptr));
	  contextptr=nr_prog_stack.back().contextptr;
	  nr_prog_stack.pop_back();
	  // restore state and pointers
	  fromto_restore(arglpos,itbeg,it,itend,old,argl,state,fromto_stack);
	  destination=(gen *)&argl.front()+arglpos;
	  *destination=res;
	  ++it;
	  if (it!=itend && state<=nr_eval_op)
	    ++destination;
	  continue;
	default:
	  gensizeerr(gettext("Bad state"),res);
	  return res;
	} // end switch
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & e) {
	res=string2gen(e.what(),false);
	res.subtype=-1;
	*destination=res;
      }
#endif
    } // end while(1)
    return argl.front();
  }

  gen symbolic::eval(int level,const context * contextptr) const {
    if (level==0 || !sommet.ptr())
      return *this;
    gen ans;
    // FIXME test should be removed later, it's here for tests. See global.cc DEFAULT_EVAL_LEVEL
    if (eval_level(contextptr)==26)
      return nr_eval(*this,level,contextptr);
    std::vector<const char *> & last =last_evaled_function_name(contextptr);
    last.push_back(sommet.ptr()->s);
    vecteur & lastarg=last_evaled_arg(contextptr);
    lastarg.push_back(feuille);
    if (sommet==at_sto && feuille.type==_VECT)
      return eval_sto(feuille,last,level,contextptr);
    if (sommet.quoted()){ 
#ifndef RTOS_THREADX
      if (feuille.type==_SYMB){ 
	unary_function_ptr & u=feuille._SYMBptr->sommet;
	if (u==at_unquote){
	  ans=sommet(feuille.eval(level,contextptr),contextptr);
	  if (!last.empty())
	    last.pop_back();
	  if (!lastarg.empty())
	    lastarg.pop_back();
	  return ans;
	}
	if (u==at_hash){
	  ans=sommet(gen(*feuille._SYMBptr->feuille._STRNGptr,contextptr),contextptr);
	  if (!last.empty())
	    last.pop_back();
	  if (!lastarg.empty())
	    lastarg.pop_back();
	  return ans;
	}
      }
#endif
      int & elevel=eval_level(contextptr);
      int save_level=elevel;
      elevel=level;
#ifdef NO_STDEXCEPT
      ans=sommet(feuille,contextptr);
#else
      try {
	ans=sommet(feuille,contextptr);
      }
      catch (std::runtime_error & err){
	elevel=save_level;
	throw(err);
      }
#endif
      elevel=save_level;
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    } // if (sommet.quoted())
    else {
      if ((sommet==at_neg) && (feuille.type==_IDNT) && !strcmp(feuille._IDNTptr->id_name,string_infinity)){
	if (!last.empty())
	  last.pop_back();
	if (!lastarg.empty())
	  lastarg.pop_back();
	return minus_inf;
      }
      if (sommet==at_quote){
	if (!last.empty())
	  last.pop_back();
	if (!lastarg.empty())
	  lastarg.pop_back();
	return quote(feuille,contextptr);
      }
      ans=(*sommet.ptr())(feuille.in_eval(level,ans,contextptr)?ans:feuille,contextptr);
      /*
	if (feuille.in_eval(level,ans,contextptr))
	ans=(*sommet.ptr())(ans,contextptr);
	else
	ans=(*sommet.ptr())(feuille,contextptr);
      */
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
  }

  bool rewrite_prod_inv(const gen & arg,gen & n,gen & d){
    n=1; d=1;
    if (arg.type==_VECT && !arg._VECTptr->empty() && arg._VECTptr->back().is_symb_of_sommet(at_inv)) {
      vecteur & uv=*arg._VECTptr;
      int tmps=uv.size(),invbegin;
      vecteur den(1,uv.back()._SYMBptr->feuille);
      // group all inv from the end to the beginning for the denominator
      for (invbegin=tmps-2;invbegin>=0;--invbegin){
	if (!uv[invbegin].is_symb_of_sommet(at_inv))
	  break;
	den.push_back(uv[invbegin]._SYMBptr->feuille);
      }
      vecteur num;
      for (int i=0;i<=invbegin;++i){
	if (uv[i].is_symb_of_sommet(at_inv) && uv[i]._SYMBptr->feuille.type<_POLY)
	  d=d*uv[i]._SYMBptr->feuille;
	else
	  num.push_back(uv[i]);
      }
      if (!is_one(d))
	den.insert(den.begin(),d);
      if (den.size()>1)
	d=new_ref_symbolic(symbolic(at_prod,den));
      else
	d=den.front();
      if (!num.empty()){
	if (num.size()==1)
	  n=num.front();
	else 
	  n=new_ref_symbolic(symbolic(at_prod,num));
      }
      return true;
    }
    // Group scalar denominators (warning, do not use for matrices!)
    vecteur num,den;
    prod2frac(new_ref_symbolic(symbolic(at_prod,arg)),num,den);
    if (!den.empty()){
      if (num.empty())
	n=plus_one;
      else {
	if (num.size()==1)
	  n=num.front();
	else
	  n=new_ref_symbolic(symbolic(at_prod,num));
      }
      /* code that does not work with matrices
	 if (den.size()==1)
	 d=den.front();
	 else
	 d=ref_symbolic(symbolic(at_prod,den);) 
      */
      if (den.size()==1 && den.front().type<_IDNT){
	d=den.front();
	return true;
      }
    }
    return false;
  }
    
  gen symbolic::evalf(int level,const context * contextptr) const {
    if (level==0)
      return *this;
    std::vector<const char *> & last =last_evaled_function_name(contextptr);
    last.push_back(sommet.ptr()->s);
    vecteur & lastarg=last_evaled_arg(contextptr);
    lastarg.push_back(feuille);
    if (sommet==at_sto){ // autoname function
      gen e=feuille._VECTptr->front().evalf(level,contextptr);
      if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_pnt) && (e._SYMBptr->feuille.type==_VECT) && (e._SYMBptr->feuille._VECTptr->size()==2))
	e=new_ref_symbolic(symbolic(at_pnt,gen(makevecteur(e._SYMBptr->feuille._VECTptr->front(),e._SYMBptr->feuille._VECTptr->back(),string2gen(feuille._VECTptr->back().print(contextptr),false)),_PNT__VECT)));
      if ( (e.type==_VECT) && (e._VECTptr->size()) && (e._VECTptr->back().type==_SYMB) && (e._VECTptr->back()._SYMBptr->sommet==at_pnt)){
	vecteur v=*e._VECTptr;
	iterateur it=v.begin(),itend=v.end();
	for (;it!=itend;++it){
	  if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_pnt) && (it->_SYMBptr->feuille._VECTptr->size()==2))
	    *it=new_ref_symbolic(symbolic(at_pnt,gen(makevecteur(it->_SYMBptr->feuille._VECTptr->front(),it->_SYMBptr->feuille._VECTptr->back(),string2gen(feuille._VECTptr->back().print(contextptr),false)),_PNT__VECT)));
	}
	e=v;
      }
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return sto(e,feuille._VECTptr->back(),contextptr);
    }
    gen ans;
    if (sommet==at_plus){
      if (feuille.type!=_VECT){
	if (feuille.type==_IDNT && !strcmp(feuille._IDNTptr->id_name,string_infinity))
	  ans=plus_inf;
	else
	  ans=feuille.evalf(level,contextptr);
      }
      else {
	const_iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
	for (;it!=itend;++it){
	  ans=ans+it->evalf(level,contextptr);
	}
      }
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
    if (sommet==at_prod){
      if (feuille.type!=_VECT)
	ans=feuille.evalf(level,contextptr);
      else {
	ans=1;
	const_iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
	for (;it!=itend;++it){
	  ans=ans*it->evalf(level,contextptr);
	}
      }
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
    if (sommet.quoted() && sommet!=at_and && !equalposcomp(plot_sommets,sommet) ){ 
      ans=sommet(feuille,contextptr);
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
    else {
      if ((sommet==at_neg) && (feuille.type==_IDNT) && !strcmp(feuille._IDNTptr->id_name,string_infinity)){
	if (!last.empty())
	  last.pop_back();
	if (!lastarg.empty())
	  lastarg.pop_back();
	return minus_inf;
      }
      if (sommet==at_quote){
	if (!last.empty())
	  last.pop_back();
	if (!lastarg.empty())
	  lastarg.pop_back();
	return quote(feuille,contextptr);
      }
      if (sommet==at_and || (sommet!=at_cercle && equalposcomp(plot_sommets,sommet))){
	// bool save_is_inevalf=is_inevalf;
	// is_inevalf=true;
	ans=new_ref_symbolic(symbolic(sommet,feuille.evalf(1,contextptr)));
	// is_inevalf=save_is_inevalf;
	if (!last.empty())
	  last.pop_back();
	if (!lastarg.empty())
	  lastarg.pop_back();
	return ans;
      }
      ans=(*sommet.ptr())(feuille.evalf(level,contextptr),contextptr);
      if (!last.empty())
	last.pop_back();
      if (!lastarg.empty())
	lastarg.pop_back();
      return ans;
    }
  }


  unsigned taille(const gen & g,unsigned max){
    if (g.type<=_IDNT)
      return 1;
    if (g.type==_FRAC)
      return 1+taille(g._FRACptr->num,max)+taille(g._FRACptr->den,max);
    if (g.type==_SYMB){
      if (g.is_symb_of_sommet(at_curve))
	return 10;
      return 1+taille(g._SYMBptr->feuille,max);
    }
    if (g.type==_VECT){
      unsigned res=0;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	res += taille(*it,max);
	if (max && res>max)
	  return res;
      }
      return res;
    }
    return 2;
  }

    
  ostream & operator << (ostream & os,const symbolic & s) { return os << s.print(context0); }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
