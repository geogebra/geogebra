/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c prog.cc -Wall  -DHAVE_CONFIG_H -DIN_GIAC " -*- */
#include "giacPCH.h"

/*
 *  Copyright (C) 2001,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef HAVE_NO_PWD_H
#ifndef BESTA_OS
#include <pwd.h>
#endif
#endif
#include <stdexcept>
#include <cmath>
#include <cstdlib>
#include <algorithm>
#include "prog.h"
#include "identificateur.h"
#include "symbolic.h"
#include "identificateur.h"
#include "usual.h"
#include "sym2poly.h"
#include "subst.h"
#include "plot.h"
#include "tex.h"
#include "input_parser.h"
#include "input_lexer.h"
#include "rpn.h"
#include "help.h"
#include "ti89.h" // for _unarchive_ti
#include "permu.h"
#include "modpoly.h"
#include "unary.h"
#include "input_lexer.h"
#include "maple.h"
#include "derive.h"
#include "giacintl.h"
#include "misc.h"
#include "lin.h"
#include "pari.h"
#include "intg.h"
#include "giacintl.h"
// #include "input_parser.h"
#ifdef HAVE_LIBDL
#include <dlfcn.h>
#endif // HAVE_LIBDL
#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#undef HAVE_LIBPARI
#endif

#ifdef BESTA_OS
unsigned int AspenGetNow(); 
#endif

#ifdef RTOS_THREADX 
u32 AspenGetNow();
extern "C" uint32_t mainThreadStack[];
#else
#undef clock
#undef clock_t
#include <time.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#if 1 // defined(GIAC_HAS_STO_38) && defined(VISUALC)
  const int rand_max2=2147483647;
#else
  const int rand_max2=RAND_MAX;
#endif

#ifdef HAVE_LIBDL
  modules_tab giac_modules_tab;
#endif

  static int prog_eval_level(GIAC_CONTEXT){
    if (int i=prog_eval_level_val(contextptr))
      return i;
    return std::max(1,eval_level(contextptr));
  }

  gen check_secure(){
    if (secure_run)
      return gensizeerr(gettext("Running in secure mode"));
    return 0;
  }

  string indent(GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return "\n:"+string(debug_ptr(contextptr)->indent_spaces,' ');
    else
      return " \n"+string(debug_ptr(contextptr)->indent_spaces,' ');
  }

  static string indent2(GIAC_CONTEXT){
    return string(debug_ptr(contextptr)->indent_spaces,' ');
  }

  // static gen substsametoequal(const gen & g){  return symbolic(at_subst,apply(g,sametoequal)); }

  // static gen subssametoequal(const gen & g){  return symbolic(at_subs,apply(g,sametoequal));  }

  // static gen maplesubssametoequal(const gen & g){  return symbolic(at_maple_subs,apply(g,sametoequal)); }

  gen equaltosame(const gen & a){
    // full replacement of = by == has been commented to avoid
    // problems with tests like: if (limit(...,x=0,..))
    /*
    unary_function_ptr equaltosametab1[]={at_equal,at_subst,at_subs,at_maple_subs};
    vector<unary_function_ptr> substin(equaltosametab1,equaltosametab1+sizeof(equaltosametab1)/sizeof(unary_function_ptr));
    gen_op equaltosametab2[]={symb_same,substsametoequal,subssametoequal,maplesubssametoequal};
    vector<gen_op> substout(equaltosametab2,equaltosametab2+sizeof(equaltosametab2)/sizeof(gen_op));
    gen tmp=subst(a,substin,substout,true);
    return tmp;
    */
    if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_equal) )
      return symb_same(a._SYMBptr->feuille._VECTptr->front(),a._SYMBptr->feuille._VECTptr->back());
    else
      return a;
  }

  gen sametoequal(const gen & a){
    if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_same) )
      return symb_equal(a._SYMBptr->feuille._VECTptr->front(),a._SYMBptr->feuille._VECTptr->back());
    else
      return a;
  }

  static void increment_instruction(const const_iterateur & it0,const const_iterateur & itend,GIAC_CONTEXT){
    const_iterateur it=it0;
    for (;it!=itend;++it)
      increment_instruction(*it,contextptr);
  }

  void increment_instruction(const vecteur & v,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      increment_instruction(*it,contextptr);
  }

  void increment_instruction(const gen & arg,GIAC_CONTEXT){
    // cerr << debug_ptr(contextptr)->current_instruction << " " << arg <<endl;
    ++debug_ptr(contextptr)->current_instruction;
    if (arg.type!=_SYMB)
      return;
    unary_function_ptr u=arg._SYMBptr->sommet;
    gen f=arg._SYMBptr->feuille;
    const unary_function_eval * uptr=dynamic_cast<const unary_function_eval *>(u.ptr());
    if (uptr && uptr->op==_ifte){
      --debug_ptr(contextptr)->current_instruction;
      increment_instruction(*f._VECTptr,contextptr);
      return;
    }
    if ( (u==at_local) || (uptr && uptr->op==_for) ){
      f=f._VECTptr->back();
      if (f.type!=_VECT){
	if (f.is_symb_of_sommet(at_bloc) && f._SYMBptr->feuille.type==_VECT)
	  increment_instruction(*f._SYMBptr->feuille._VECTptr,contextptr);
	else
	  increment_instruction(f,contextptr);
      }
      else 
	increment_instruction(*f._VECTptr,contextptr);
      return;
    }
    if (u==at_bloc){
      if (f.type!=_VECT)
	increment_instruction(f,contextptr);
      else
	increment_instruction(*f._VECTptr,contextptr);
      return;
    }
    if (u==at_try_catch){
      increment_instruction(f._VECTptr->front(),contextptr);
      increment_instruction(f._VECTptr->back(),contextptr);
    }
  }

  static string concatenate(const vector<string> & v){
    vector<string>::const_iterator it=v.begin(),itend=v.end();
    string res;
    for (;it!=itend;++it){
      res=res + "  "+*it;
    }
    return res;
  }

  void debug_print(const vecteur & arg,vector<string> & v,GIAC_CONTEXT){
    const_iterateur it=arg.begin(),itend=arg.end();
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_program)){
	vector<string> tmp;
	debug_print(*it,tmp,contextptr);
	v.push_back(concatenate(tmp));
      }
      debug_print(*it,v,contextptr);
    }
  }

  void debug_print(const gen & e,vector<string>  & v,GIAC_CONTEXT){
    if (e.type!=_SYMB){
      v.push_back(indent2(contextptr)+e.print(contextptr));
      return ;
    }
    bool is38=abs_calc_mode(contextptr)==38;
    unary_function_ptr u=e._SYMBptr->sommet;
    gen f=e._SYMBptr->feuille;
    const unary_function_eval * uptr=dynamic_cast<const unary_function_eval *>(u.ptr());
    if (uptr && uptr->op==_ifte){
      string s=indent2(contextptr);
      s += is38?"IF ":"if(";
      vecteur w=*f._VECTptr;
      s += w.front().print(contextptr);
      s += is38?" THEN/ELSE":")";
      v.push_back(s);
      debug_ptr(contextptr)->indent_spaces += 1;
      debug_print(w[1],v,contextptr);
      debug_ptr(contextptr)->indent_spaces += 1;
      debug_print(w[2],v,contextptr);
      debug_ptr(contextptr)->indent_spaces -=2;
      return ;
    }
    if (u==at_local){
      string s(indent2(contextptr));
      s += is38?"LOCAL ":"local ";
      s += f._VECTptr->front().print(contextptr);
      v.push_back(s);
      debug_ptr(contextptr)->indent_spaces += 2;
      f=f._VECTptr->back();
      if (f.type!=_VECT)
	debug_print(f,v,contextptr);
      else
	debug_print(*f._VECTptr,v,contextptr);
      debug_ptr(contextptr)->indent_spaces -= 2;
      return;
    }
    if (uptr && uptr->op==_for){
      string s(indent2(contextptr));
      s += is38?"FOR(":"for(";
      vecteur w=*f._VECTptr;
      s += w[0].print(contextptr)+";"+w[1].print(contextptr)+";"+w[2].print(contextptr)+")";
      v.push_back(s);
      debug_ptr(contextptr)->indent_spaces += 2;
      f=f._VECTptr->back();
      if ((f.type==_SYMB) && (f._SYMBptr->sommet==at_bloc))
	f=f._SYMBptr->feuille;
      if (f.type!=_VECT)
	debug_print(f,v,contextptr);
      else
	debug_print(*f._VECTptr,v,contextptr);
      debug_ptr(contextptr)->indent_spaces -= 2;
      return;
    }
    if (u==at_bloc){
      v.push_back(string(indent2(contextptr)+"bloc"));
      debug_ptr(contextptr)->indent_spaces += 2;
      if (f.type!=_VECT)
	debug_print(f,v,contextptr);
      else
	debug_print(*f._VECTptr,v,contextptr);
      debug_ptr(contextptr)->indent_spaces -= 2;
      return;
    }
    if (u==at_try_catch){
      // cerr << f << endl;
      v.push_back(string(indent2(contextptr)+"try"));
      debug_ptr(contextptr)->indent_spaces += 1;
      debug_print(f._VECTptr->front(),v,contextptr);
      debug_ptr(contextptr)->indent_spaces += 1;
      debug_print(f._VECTptr->back(),v,contextptr);
      debug_ptr(contextptr)->indent_spaces -=2;
      return;
    }
    v.push_back(indent2(contextptr)+e.print(contextptr));
  }

  static vecteur rm_checktype(const vecteur & v){
    vecteur addvars(v);
    iterateur it=addvars.begin(),itend=addvars.end();
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_check_type))
	*it=it->_SYMBptr->feuille._VECTptr->back();
      if (it->is_symb_of_sommet(at_sto))
	*it=it->_SYMBptr->feuille._VECTptr->back();	
    }
    return addvars;
  }
  // res1= list of assignation with =, res2= list of non declared global vars, res3= list of declared global vars, res4=list of functions
  void check_local_assign(const gen & g,const vecteur & prog_args,vecteur & res1,vecteur & res2,vecteur & res3,vecteur & res4,bool testequal,GIAC_CONTEXT){
    if (g.is_symb_of_sommet(at_double_deux_points))
      return;
    if (g.is_symb_of_sommet(at_local)){
      gen &f=g._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2)
	return;
      gen declaredvars=f._VECTptr->front();
      if (declaredvars.type==_VECT && declaredvars._VECTptr->size()==2){
	vecteur f1(gen2vecteur(declaredvars._VECTptr->front()));
	vecteur f2(gen2vecteur(declaredvars._VECTptr->back()));
	res3=mergevecteur(res3,f2);
	declaredvars=mergevecteur(f1,f2);
      }
      vecteur addvars(rm_checktype(gen2vecteur(declaredvars)));
      vecteur newvars(mergevecteur(prog_args,addvars));
      check_local_assign(f._VECTptr->back(),newvars,res1,res2,res3,res4,testequal,contextptr);
      return; 
    }
    if (g.is_symb_of_sommet(at_sto)){
      gen &f=g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().is_symb_of_sommet(at_program)){
	res4.push_back(f._VECTptr->back());
	gen & ff = f._VECTptr->front()._SYMBptr->feuille;
	if (ff.type==_VECT && ff._VECTptr->size()==3){
	  vecteur alt_prog_args(gen2vecteur(ff._VECTptr->front()));
	  check_local_assign(ff._VECTptr->back(),alt_prog_args,res1,res2,res3,res4,testequal,contextptr);
	}
	return;
      }
    }
    if (g.is_symb_of_sommet(at_ifte)){
      vecteur v=lop(g,at_array_sto);
      if (!v.empty() && logptr(contextptr))
	*logptr(contextptr) << gettext("Warning, =< is in-place assign, check ") << v << endl;
    }
    if (g.is_symb_of_sommet(at_bloc) || 
	g.is_symb_of_sommet(at_for) ||
	g.is_symb_of_sommet(at_ifte)){
      check_local_assign(g._SYMBptr->feuille,prog_args,res1,res2,res3,res4,testequal,contextptr);
      return;
    }
    if (testequal && g.is_symb_of_sommet(at_equal)){
      if (g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2 && g._SYMBptr->feuille._VECTptr->front().type!=_INT_ )
	res1.push_back(g);
      return;
    }
    if (g.is_symb_of_sommet(at_of)){
      gen & f=g._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2)
	return;
      if (eval(f._VECTptr->front(),1,contextptr)!=f._VECTptr->front())
	check_local_assign(f._VECTptr->back(),prog_args,res1,res2,res3,res4,false,contextptr);
      else
	check_local_assign(f,prog_args,res1,res2,res3,res4,false,contextptr);
      return;
    }
    if (g.type==_SYMB){
      check_local_assign(g._SYMBptr->feuille,prog_args,res1,res2,res3,res4,false,contextptr);
      return;
    }
    if (g.type!=_VECT){
      vecteur l(*_lname(g,contextptr)._VECTptr);
      const_iterateur it=l.begin(),itend=l.end();
      for (;it!=itend;++it){
	if (!equalposcomp(res2,*it) && !equalposcomp(prog_args,*it))
	  res2.push_back(*it);
      }
      return;
    }
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      check_local_assign(*it,prog_args,res1,res2,res3,res4,testequal,contextptr);
    }
  }
  bool is_constant_idnt(const gen & g){
    return g==cst_pi || g==cst_euler_gamma || is_inf(g) || is_undef(g) || (g.type==_IDNT && strcmp(g._IDNTptr->id_name,"i")==0);
  }
  // Return the names of variables that are not local in g
  // and the equality that are not used (warning = instead of := )
  string check_local_assign(const gen & g,GIAC_CONTEXT){
    string res;
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	res += check_local_assign(*it,contextptr);
      return res;
    }
    if (g.is_symb_of_sommet(at_nodisp))
      return check_local_assign(g._SYMBptr->feuille,contextptr);
    if (g.is_symb_of_sommet(at_sto)){
      gen & f =g._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2)
	return res;
      res=check_local_assign(f._VECTptr->front(),contextptr);
      return res.substr(0,res.size()-1)+gettext(" compiling ")+f._VECTptr->back().print(contextptr)+'\n';
    }
    if (!g.is_symb_of_sommet(at_program))
      return res;
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=3)
      return "// Invalid program";
    vecteur & v =*f._VECTptr;
    vecteur vars=rm_checktype(gen2vecteur(v[0])),res1,res2(1,undef),res3,res4;
    gen prog=v.back();
    check_local_assign(prog,vars,res1,res2,res3,res4,true,contextptr);
    int rs=res2.size();
    for (int i=0;i<rs;i++){
      if (is_constant_idnt(res2[i])){
	res2.erase(res2.begin()+i);
	--i; --rs;
      }
    }
    if (!res1.empty()){
      res=gettext("// Warning, assignation is :=, check these lines: ");
      const_iterateur it=res1.begin(),itend=res1.end();
      for (;it!=itend;++it){
	res += it->print(contextptr);
      }
      res +="\n";
    }
    if (res2.size()>=1){
      res+=gettext("// Warning: ");
      const_iterateur it=res2.begin(),itend=res2.end();
      for (;it!=itend;++it){
	// pi already checked if (*it!=cst_pi)
	res += it->print(contextptr)+",";
      }
      res +=gettext(" declared as global variable(s)\n");
    }
    if (res.empty())
      return giac::first_error_line(contextptr)?gettext("// Error(s)\n"):gettext("// Success\n");
    else
      return res;
  }

  static string printascheck_type(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    return print_the_type(feuille._VECTptr->front().val,contextptr)+' '+feuille._VECTptr->back().print(contextptr);
  }
  
  gen symb_check_type(const gen & args,GIAC_CONTEXT){
    return symbolic(at_check_type,args);
  }
  gen _check_type(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_check_type(args,contextptr);
    if (args._VECTptr->size()!=2)
      return gensizeerr(gettext("check_type must have 2 args"));
    gen res=args._VECTptr->back();
    gen req=args._VECTptr->front();
    if (req.type!=_INT_) // FIXME check for matrix(...) or vector(...)
      return res;
    int type;
    switch (res.type){
    case _INT_:
      type=_ZINT;
      break;
    case _REAL:
      type=_DOUBLE_;
      break;
    default:
      type=res.type;
      break;
    }   
    if (req.val==_MAPLE_LIST){
      if (type==_VECT)
	return res;
      return gensizeerr(contextptr);
    }
    if (type==req.val)
      return res;
    if (type==_ZINT && type==(req.val &0xff) ){
      if (req.val==_POSINT && is_strictly_positive(res,contextptr)) 
	return res;
      if (req.val==_NEGINT && is_strictly_positive(-res,contextptr))
	return res;
      if (req.val==_NONPOSINT && is_positive(-res,contextptr))
	return res;
      if (req.val==_NONNEGINT && is_positive(res,contextptr))
	return res;
    }
    return gentypeerr(gettext("Argument should be of type ")+print_the_type(args._VECTptr->front().val,contextptr));
  }
  static const char _check_type_s []="check_type";
  static define_unary_function_eval2_index (118,__check_type,&symb_check_type,_check_type_s,&printascheck_type);
  define_unary_function_ptr( at_check_type ,alias_at_check_type ,&__check_type);

  // static gen symb_type(const gen & args){  return symbolic(at_type,args); }
  gen _type(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    int type;
    switch (args.type){
    case _INT_:
      type=_ZINT;
      break;
    case _REAL:
      type=_DOUBLE_;
      break;
    default:
      if (args.is_symb_of_sommet(at_program))
	type=_FUNC;
      else
	type=args.type;
    }   
    gen tmp(type);
    tmp.subtype=1;
    return tmp;
  }
  static const char _type_s []="type";
  static define_unary_function_eval (__type,&_type,_type_s);
  define_unary_function_ptr5( at_type ,alias_at_type,&__type,0,true);

  gen _nop(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG &&  a.subtype==-1) return  a;
    if (a.type==_VECT && a.subtype==_SEQ__VECT){
      // Workaround so that sequences inside spreadsheet are saved as []
      gen tmp=a;
      tmp.subtype=0;
      return tmp;
    }
    return a;
  }
  static const char _nop_s []="nop";
  static define_unary_function_eval (__nop,&_nop,_nop_s);
  define_unary_function_ptr5( at_nop ,alias_at_nop,&__nop,0,true);

  string printasinnerbloc(const gen & feuille,GIAC_CONTEXT){
    if ( (feuille.type==_SYMB) && feuille._SYMBptr->sommet==at_bloc)
      return printasinnerbloc(feuille._SYMBptr->feuille,contextptr);
    if (feuille.type!=_VECT)
      return indent(contextptr)+feuille.print(contextptr);
    const_iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    string res;
    if (it==itend)
      return res;
    for (;;){
      res += indent(contextptr)+it->print(contextptr);
      ++it;
      if (it==itend)
	return res;
      if (xcas_mode(contextptr)!=3)
	res += ";";
    }
  }

  static void local_init(const gen & e,vecteur & non_init_vars,vecteur & initialisation_seq){
    vecteur v;
    if (e.type!=_VECT)
      v=vecteur(1,e);
    else
      v=*e._VECTptr;
    if (v.size()==2 && v.front().type==_VECT)
      v=*v.front()._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_IDNT){
	non_init_vars.push_back(*it);
	continue;
      }
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_sto)){
	non_init_vars.push_back(it->_SYMBptr->feuille._VECTptr->back());
	initialisation_seq.push_back(*it);
      }
    }
  }

  static gen add_global(const gen & i,GIAC_CONTEXT){
#ifndef NO_STDEXCEPT
    if (i.type==_IDNT)
#endif
      return identificateur("global_"+i.print(contextptr));
    return gensizeerr(gettext("Proc Parameters"));
  }

  static string printasprogram(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=3) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    string res;
    bool calc38=abs_calc_mode(contextptr)==38;
    if (!calc38){
      if (xcas_mode(contextptr)==3)
	res="\n:lastprog";
      else
	res=" "; // was res=indent(contextptr);
    }
    gen & feuille0=feuille._VECTptr->front();
    if (feuille0.type==_VECT && feuille0.subtype==_SEQ__VECT && feuille0._VECTptr->size()==1)
      res +="("+feuille0._VECTptr->front().print(contextptr)+")";
    else
      res +="("+feuille0.print(contextptr)+")";
    if (xcas_mode(contextptr)==3)
      res +="\n";
    else
      res += "->";
    bool test;
    string locals,inits;
    gen proc_args=feuille._VECTptr->front();
    vecteur vect,non_init_vars,initialisation_seq;
    if ((xcas_mode(contextptr)>0) && (feuille._VECTptr->back().type==_SYMB) && (feuille._VECTptr->back()._SYMBptr->sommet==at_local)){
        test=false;
        gen tmp=feuille._VECTptr->back()._SYMBptr->feuille;
	local_init(tmp._VECTptr->front(),non_init_vars,initialisation_seq);
	// For Maple add proc parameters to local vars
	if (xcas_mode(contextptr) ==1+_DECALAGE){
	  if (proc_args.type==_VECT){
	    vecteur v=*proc_args._VECTptr;
	    non_init_vars=mergevecteur(non_init_vars,v);
	    iterateur it=v.begin(),itend=v.end();
	    for (;it!=itend;++it){
	      gen tmp=add_global(*it,contextptr);
	      initialisation_seq.push_back(symb_sto(tmp,*it));
	      *it=tmp;
	    }
	    proc_args=gen(v,_SEQ__VECT);
	  }
	  else {
	    non_init_vars.push_back(proc_args);
	    gen tmp=add_global(proc_args,contextptr);
	    initialisation_seq.push_back(symb_sto(tmp,proc_args));
	    proc_args=tmp;
	  }
	}
	if (!non_init_vars.empty()){
	  if (xcas_mode(contextptr)==3)
	    locals=indent(contextptr)+"Local "+printinner_VECT(non_init_vars,_SEQ__VECT,contextptr);
	  else
	    locals=indent(contextptr)+"  local "+printinner_VECT(non_init_vars,_SEQ__VECT,contextptr)+";";
	}
	inits=printasinnerbloc(gen(initialisation_seq,_SEQ__VECT),contextptr);
	if (tmp._VECTptr->back().type==_VECT)
	  vect=*tmp._VECTptr->back()._VECTptr;
	else
	  vect=makevecteur(tmp._VECTptr->back());
    }
    else {
        test=(feuille._VECTptr->back().type!=_VECT ||feuille._VECTptr->back().subtype );
        if (!test)
            vect=*feuille._VECTptr->back()._VECTptr;
    }
    if (test){
      if (xcas_mode(contextptr)==3)
	return res+":Func "+feuille._VECTptr->back().print(contextptr)+"\n:EndFunc\n";
      return res+feuille._VECTptr->back().print(contextptr);
    }
    if (xcas_mode(contextptr)>0){
      if (xcas_mode(contextptr)==3)
	res+=":Func"+locals;
      else {
	res="proc("+proc_args.print(contextptr)+")"+locals;
	if (xcas_mode(contextptr)==2)
	  res +=indent(contextptr)+"begin ";
	if (inits.size()) 
	  res += indent(contextptr)+inits+";";
      }
    }
    else
      res += calc38?"BEGIN " :"{";
    const_iterateur it=vect.begin(),itend=vect.end();
    debug_ptr(contextptr)->indent_spaces +=2;
    for (;;){
      if (xcas_mode(contextptr)==3)
	res += indent(contextptr)+it->print(contextptr);
      else
	res += indent(contextptr)+it->print(contextptr);
      ++it;
      if (it==itend){
	debug_ptr(contextptr)->indent_spaces -=2;
	if (xcas_mode(contextptr)!=3)
	  res += "; "+indent(contextptr);
	switch (xcas_mode(contextptr)){
	case 0:
	  res += calc38?"END;":"}";
	  break;
	case 1: case 1+_DECALAGE:
	  res+=indent(contextptr)+"end;";
	  break;
	case 2:
	  return res+=indent(contextptr)+"end_proc;";
	  break;
	case 3:
	  return res+=indent(contextptr)+"EndFunc\n";
	}
	break;
      }
      else {
	if (xcas_mode(contextptr)!=3)
	  res +="; ";
      }
    }
    return res;
  }

  static string texprintasprogram(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (latex_format(contextptr)==1){
      return printasprogram(feuille,sommetstr,contextptr);
    }
    string s("\\parbox{12cm}{\\tt ");
    s += translate_underscore(printasprogram(feuille,sommetstr,contextptr));
    s+=" }";
    return s;
  }

  static void replace_keywords(const gen & a,const gen & b,gen & newa,gen & newb,GIAC_CONTEXT){
    // Check that variables in a are really variables, otherwise print
    // the var and make new variables
    vecteur newv(gen2vecteur(a));
    if (newv.size()==2 && newv.front().type==_VECT && newv.back().type==_VECT){
      gen tmpa,tmpb;
      replace_keywords(*newv.front()._VECTptr,b,tmpa,tmpb,contextptr);
      replace_keywords(*newv.back()._VECTptr,tmpb,newa,newb,contextptr);
      newa=gen(makevecteur(tmpa,newa),a.subtype);
      return;
    }
    vecteur v1,v2;
    iterateur it=newv.begin(),itend=newv.end();
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_sto) || it->is_symb_of_sommet(at_check_type)) // FIXME check 1st arg too
	continue;
      if (it->type!=_IDNT && it->type!=_CPLX){
	v1.push_back(*it);
	string s=gen2string(*it);
	int ss=s.size();
	if (ss>2 && s[0]=='\'' && s[ss-1]=='\'')
	  s=s.substr(1,ss-2);
	sym_tab::const_iterator i = syms().find(s);
	if (i == syms().end()) {
	  *it = *(new identificateur(s));
	  syms()[s] = *it;
	} else {
	  // std::cerr << "lexer" << s << endl;
	  *it = i->second;
	}
	v2.push_back(*it);
      }
    }
    newa=gen(newv,_SEQ__VECT);
    if (v1.empty())
      newb=b;
    else
      newb=quotesubst(b,v1,v2,contextptr);
  }

  // a=arguments, b=values, c=program bloc, d=program name
  symbolic symb_program_sto(const gen & a,const gen & b,const gen & c,const gen & d,bool embedd,GIAC_CONTEXT){
    bool warn=false;
#ifndef GIAC_HAS_STO_38
    if (logptr(contextptr))
      warn=true;
#endif
    if (warn){
      *logptr(contextptr) << gettext("// Parsing ") << d << endl;
      if (c.is_symb_of_sommet(at_derive))
	*logptr(contextptr) << gettext("Warning, defining a derivative function should be done with function_diff or unapply: ") << c << endl;
       if (c.type==_SYMB && c._SYMBptr->sommet!=at_local && c._SYMBptr->sommet!=at_bloc && c._SYMBptr->sommet!=at_when && c._SYMBptr->sommet!=at_for && c._SYMBptr->sommet!=at_ifte){
	 vecteur lofc=lop(c,at_of);
	 vecteur lofc_no_d;
	 vecteur av=gen2vecteur(a);
	 for (unsigned i=0;i<lofc.size();++i){
	   if (lofc[i][1]!=d && !equalposcomp(av,lofc[i][1]) )
	     lofc_no_d.push_back(lofc[i]);
	 }
	 if (!lofc_no_d.empty()){
	   *logptr(contextptr) << gettext("Warning: algebraic function defined in term of others functions may lead to evaluation errors") << endl;
	   cerr << c.print(contextptr) << endl;
	   *logptr(contextptr) << gettext("Perhaps you meant ") << d.print(contextptr) << ":=unapply(" << c.print(contextptr) << ",";
	   if (a.type==_VECT && a.subtype==_SEQ__VECT && a._VECTptr->size()==1)
	     *logptr(contextptr) << a._VECTptr->front().print(contextptr) << ")" << endl;
	   else
	     *logptr(contextptr) << a.print(contextptr) << ")" << endl;
	 }
       }
    }
    gen newa,newc;
    replace_keywords(a,((embedd&&c.type==_VECT)?makevecteur(c):c),newa,newc,contextptr);
    symbolic g=symbolic(at_program,gen(makevecteur(newa,b,newc),_SEQ__VECT));
    g=symbolic(at_sto,gen(makevecteur(g,d),_SEQ__VECT));
    if (warn)
      *logptr(contextptr) << check_local_assign(g,contextptr) ;
    if (warn && newc.is_symb_of_sommet(at_local)){
      // check that a local variable name does not shadow a parameter name
      gen & newcf=newc._SYMBptr->feuille;
      if (newcf.type==_VECT && newcf._VECTptr->size()==2){
	gen & vars = newcf._VECTptr->front();
	gen inters=_intersect(gen(makevecteur(vars,newa),_SEQ__VECT),contextptr);
	if (inters.type==_VECT && !inters._VECTptr->empty()){
	  inters.subtype=_SEQ__VECT;
	  *logptr(contextptr) << gettext("Warning: Local variables shadow function arguments ") << inters << endl;
	}
      }
    }
    return g;
  }
  symbolic symb_program(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen newa,newc;
    replace_keywords(a,c,newa,newc,contextptr);
    symbolic g=symbolic(at_program,gen(makevecteur(newa,b,newc),_SEQ__VECT));
#ifndef GIAC_HAS_STO_38
    if (logptr(contextptr))
      *logptr(contextptr) << check_local_assign(g,contextptr) ;
#endif
    return g;
  }
  symbolic symb_program(const gen & args){
    return symbolic(at_program,args);
  }
  static void lidnt_prog(const gen & g,vecteur & res);
  static void lidnt_prog(const vecteur & v,vecteur & res){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      lidnt_prog(*it,res);
  }
  static void lidnt_prog(const gen & g,vecteur & res){
    switch (g.type){
    case _VECT:
      lidnt_prog(*g._VECTptr,res);
      break;
    case _IDNT:
      if (!equalposcomp(res,g))
	res.push_back(g);
      break;
    case _SYMB:
      /* if (g._SYMBptr->sommet==at_at || g._SYMBptr->sommet==at_of )
	lidnt_prog(g._SYMBptr->feuille._VECTptr->back(),res);
	else */
	lidnt_prog(g._SYMBptr->feuille,res);
      break;
    }
  }

  static void local_vars(const gen & g,vecteur & v,GIAC_CONTEXT){
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	local_vars(*it,v,contextptr);
      }
      return ;
    }
    if (g.type!=_SYMB)
      return;
    if (g._SYMBptr->sommet==at_local && g._SYMBptr->feuille.type==_VECT){
      vecteur & w = *g._SYMBptr->feuille._VECTptr;
      if (w[0].type==_VECT && w[0]._VECTptr->size()==2 && w[0]._VECTptr->front().type==_VECT)
	v=mergevecteur(v,*w[0]._VECTptr->front()._VECTptr);
      else
	v=mergevecteur(v,gen2vecteur(w[0]));
      local_vars(w[1],v,contextptr);
    }
    else
      local_vars(g._SYMBptr->feuille,v,contextptr);
  }

  gen quote_program(const gen & args,GIAC_CONTEXT){
    // return symb_program(args);
    // g:=unapply(p ->translation(w,p),w);g(1)
    // Necessite d'evaluer les arguments des programmes a l'interieur d'un programme.
    // Mais il ne faut pas evaluer les variables declarees comme locales!!
    bool in_prog=debug_ptr(contextptr)->sst_at_stack.size()!=0;
    // ?? Subst all variables except arguments
    if (!in_prog || args.type!=_VECT || args._VECTptr->size()!=3)
      return symb_program(args);
    vecteur & v = *args._VECTptr;
    vecteur vars(gen2vecteur(v[0]));
    int s=vars.size(); // s vars not subst-ed
    lidnt_prog(v[2],vars);
    vars=vecteur(vars.begin()+s,vars.end());
    // Remove local variables from the list
    vecteur tmpvar,resvar;
    local_vars(v[2],tmpvar,contextptr); 
    const_iterateur it=vars.begin(),itend=vars.end();
    for (;it!=itend;++it){
      if (!equalposcomp(tmpvar,*it))
	resvar.push_back(*it);
    }
    gen tmp=gen(resvar).eval(1,contextptr);
    vecteur varsub(*tmp._VECTptr);
    return symbolic(at_program,quotesubst(args,resvar,varsub,contextptr));
  }
  static bool is_return(const gen & g,gen & newres){
    if ((g.type==_SYMB) && (g._SYMBptr->sommet==at_return) ){
      gen tmp = g._SYMBptr->feuille;
      is_return(tmp,newres);
      return true;
    }
    if (g.type==_STRNG && g.subtype==-1){
      newres=g;
      return true;
    }
    if ( (g.type==_VECT && g.subtype ==_SEQ__VECT && g._VECTptr->size()==1) )
      return is_return(g._VECTptr->front(),newres);
    newres=g;
    return false;
  }
  void adjust_sst_at(const gen & name,GIAC_CONTEXT){
    debug_ptr(contextptr)->sst_at.clear();
    const_iterateur it=debug_ptr(contextptr)->debug_breakpoint.begin(),itend=debug_ptr(contextptr)->debug_breakpoint.end();
    for (;it!=itend;++it){
      if (it->_VECTptr->front()==name)
	debug_ptr(contextptr)->sst_at.push_back(it->_VECTptr->back().val);
    }
  }

  void program_leave(const gen & save_debug_info,bool save_sst_mode,debug_struct * dbgptr){
    dbgptr->args_stack.pop_back();
    // *logptr(contextptr) << "Leaving " << args << endl;
    if (!dbgptr->sst_at_stack.empty()){
      dbgptr->sst_at=dbgptr->sst_at_stack.back();
      dbgptr->sst_at_stack.pop_back();
    }
    if (!dbgptr->current_instruction_stack.empty()){
      dbgptr->current_instruction=dbgptr->current_instruction_stack.back();
      dbgptr->current_instruction_stack.pop_back();
    }
    dbgptr->sst_mode=save_sst_mode;
    if (dbgptr->current_instruction_stack.empty())
      dbgptr->debug_mode=false;
    (*dbgptr->debug_info_ptr)=save_debug_info;
    (*dbgptr->fast_debug_info_ptr)=save_debug_info;
  }

  gen _program(const gen & args,const gen & name,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args.eval(prog_eval_level(contextptr),contextptr);
    // set breakpoints
    debug_struct * dbgptr=debug_ptr(contextptr);
    context * newcontextptr=(context *)contextptr;
    const_iterateur it,itend;
    gen res,newres,label,vars,values,prog,save_debug_info;
    // *logptr(contextptr) << & res << endl;
#ifdef RTOS_THREADX
    if ((void *)&res<= (void *)&mainThreadStack[1024]) { 
      gensizeerr(gettext("Too many recursions"),res);
      return res;
    }
#else
#if !defined(WIN32) && defined HAVE_PTHREAD_H
    if (contextptr){
      // cerr << &slevel << " " << thread_param_ptr(contextptr)->stackaddr << endl;
      if ( ((unsigned long) &res) < ((unsigned long) thread_param_ptr(contextptr)->stackaddr)+8192){
	gensizeerr(gettext("Too many recursion levels"),res); 
	return res;
      }
    }
    else {
      if ( int(dbgptr->sst_at_stack.size()) >= MAX_RECURSION_LEVEL+1){
	gensizeerr(gettext("Too many recursions"),res);
	return res;
      }
    }
#else
    if ( int(dbgptr->sst_at_stack.size()) >= MAX_RECURSION_LEVEL+1){
      gensizeerr(gettext("Too many recursions"),res);
      return res;
    }
#endif
#endif
    dbgptr->sst_at_stack.push_back(dbgptr->sst_at);
    dbgptr->sst_at.clear();
    if (name.type==_IDNT)
      adjust_sst_at(name,contextptr);
    dbgptr->current_instruction_stack.push_back(dbgptr->current_instruction);
    dbgptr->current_instruction=0;
    bool save_sst_mode = dbgptr->sst_mode,findlabel,calc_save ;
    int protect=0;
    // *logptr(contextptr) << "Entering prog " << args << " " << dbgptr->sst_in_mode << endl;
    if (dbgptr->sst_in_mode){
      dbgptr->sst_in_mode=false;
      dbgptr->sst_mode=true;
    }
    else
      dbgptr->sst_mode=false;
    // Bind local var
    if (ctrl_c || args._VECTptr->size()!=3){
      gensizeerr(res,contextptr);
      return res;
    }
    calc_save=calc_mode(contextptr)==38;
    if (calc_save) 
      calc_mode(-38,contextptr);
    vars=args._VECTptr->front();
    values=(*(args._VECTptr))[1];
    prog=args._VECTptr->back();
    save_debug_info=(*dbgptr->debug_info_ptr);
    if (vars.type!=_VECT)
      vars=gen(makevecteur(vars));
    if (values.type!=_VECT || values.subtype!=_SEQ__VECT || (vars._VECTptr->size()==1 && values._VECTptr->size()!=1))
      values=gen(makevecteur(values));
    // *logptr(contextptr) << vars << " " << values << endl;
    dbgptr->args_stack.push_back(gen(mergevecteur(vecteur(1,name),*values._VECTptr)));
    // removed sst test so that when a breakpoint is evaled
    // the correct info is displayed
    (*dbgptr->debug_info_ptr)=prog;
    (*dbgptr->fast_debug_info_ptr)=prog;
    if (!vars._VECTptr->empty())
      protect=bind(*values._VECTptr,*vars._VECTptr,newcontextptr);
    if (protect==-RAND_MAX){
      program_leave(save_debug_info,save_sst_mode,dbgptr);
      if (calc_save) 
	calc_mode(38,contextptr);
      gensizeerr(res,contextptr);
      return res;
    }
#ifndef NO_STDEXCEPT
    try {
#endif
      if (prog.type!=_VECT || prog.subtype){
	++debug_ptr(newcontextptr)->current_instruction;
	if (debug_ptr(newcontextptr)->debug_mode){
	  debug_loop(res,newcontextptr);
	  if (!is_undef(res)){
	    if (!prog.in_eval(prog_eval_level(newcontextptr),res,newcontextptr))
	      res=prog;
	  }
	}
	else {
	  if (!prog.in_eval(prog_eval_level(newcontextptr),res,newcontextptr))
	    res=prog;
	}
	if (is_return(res,newres))
	  res=newres;
      }
      else {
	it=prog._VECTptr->begin();
	itend=prog._VECTptr->end();
	findlabel=false;
	for (;!ctrl_c && it!=itend;++it){
	  ++debug_ptr(newcontextptr)->current_instruction;
	  if (debug_ptr(newcontextptr)->debug_mode){
	    debug_loop(res,newcontextptr);
	    if (is_undef(res)) break;
	  }
	  if (!findlabel){
	    if (it->is_symb_of_sommet(at_return)){
	      if (!it->_SYMBptr->feuille.in_eval(prog_eval_level(newcontextptr),newres,newcontextptr))
		newres=it->_SYMBptr->feuille;
	      is_return(newres,res);
	      break;
	    }
	    if (!it->in_eval(prog_eval_level(newcontextptr),res,newcontextptr))
	      res=*it;
	  }
	  else
	    res=*it;
	  if (res.type==_STRNG && res.subtype==-1)
	    break;
	  if (res.type==_SYMB){
	    if (findlabel && res.is_symb_of_sommet(at_label) && label==res._SYMBptr->feuille)
	      findlabel=false;
	    if (!findlabel && res.is_symb_of_sommet(at_goto)){
	      findlabel=true;
	      label=res._SYMBptr->feuille;
	    }
	  }
	  if (findlabel && it+1==itend)
	    it=prog._VECTptr->begin()-1;
	  if (!findlabel && is_return(res,newres)){
	    res=newres;
	    break;
	  }
	}
      }
#ifndef NO_STDEXCEPT
    } // end try
    catch (std::runtime_error & e){
      if (!vars._VECTptr->empty())
	leave(protect,*vars._VECTptr,newcontextptr);
      if (calc_save) 
	calc_mode(38,contextptr);
      throw(std::runtime_error(e.what()));
    }
#endif
    if (!vars._VECTptr->empty())
      leave(protect,*vars._VECTptr,newcontextptr);
    program_leave(save_debug_info,save_sst_mode,dbgptr);
    if (calc_save) 
      calc_mode(38,contextptr);
    return res;
  }
  static const char _program_s []="program";
  static define_unary_function_eval4_index (147,__program,&quote_program,_program_s,&printasprogram,&texprintasprogram);
  define_unary_function_ptr5( at_program ,alias_at_program,&__program,_QUOTE_ARGUMENTS,0);

  static string printasbloc(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) )
      return "{"+feuille.print(contextptr)+";}";
    const_iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    string res("{");
    if (xcas_mode(contextptr)>0){
      if (xcas_mode(contextptr)==3)
	res="";
      else
	res=indent(contextptr)+"begin";
    }
    debug_ptr(contextptr)->indent_spaces +=2;
    for (;;){
      if (xcas_mode(contextptr)==3)
	res += indent(contextptr)+it->print(contextptr);
      else
	res += indent(contextptr)+it->print(contextptr);
      ++it;
      if (it==itend){
	debug_ptr(contextptr)->indent_spaces -=2;
	if (xcas_mode(contextptr)==3)
	  break;
	res += "; "+indent(contextptr);
	if (xcas_mode(contextptr)>0)
	  res += indent(contextptr)+"end";
	else
	  res += "}";
	break;
      }
      else {
	if (xcas_mode(contextptr)!=3)
	  res +="; ";
      }
    }
    return res;
  }
  gen symb_bloc(const gen & args){
    if (args.type!=_VECT)
      return args;
    if (args.type==_VECT && args._VECTptr->size()==1)
      return args._VECTptr->front();
    gen a=args; a.subtype=_SEQ__VECT;
    return symbolic(at_bloc,a);
  }
  gen _bloc(const gen & prog,GIAC_CONTEXT){
    if ( prog.type==_STRNG &&  prog.subtype==-1) return  prog;
    gen res,label;
    bool findlabel=false;
    debug_struct * dbgptr=debug_ptr(contextptr);
    if (prog.type!=_VECT){
      ++dbgptr->current_instruction;
      if (dbgptr->debug_mode){
	debug_loop(res,contextptr);
	if (is_undef(res)) return res;
      }
      return prog.eval(eval_level(contextptr),contextptr);
    }
    else {
      const_iterateur it=prog._VECTptr->begin(),itend=prog._VECTptr->end();
      for (;!ctrl_c && it!=itend;++it){
	++dbgptr->current_instruction;
	if (dbgptr->debug_mode){
	  debug_loop(res,contextptr);
	  if (is_undef(res)) return res;
	}
	if (!findlabel){
	  if (it->type==_SYMB && it->_SYMBptr->sommet==at_return){
	    // res=it->_SYMBptr->feuille.eval(prog_eval_level(contextptr),contextptr);
	    if (!it->_SYMBptr->feuille.in_eval(prog_eval_level(contextptr),res,contextptr))
	      res=it->_SYMBptr->feuille;
	    increment_instruction(it+1,itend,contextptr);
	    return symbolic(at_return,res);
	  }
	  else {
	    // res=it->eval(eval_level(contextptr),contextptr);
	    if (!it->in_eval(eval_level(contextptr),res,contextptr))
	      res=*it;
	  }
	}
	else 
	  res=*it;
	if (res.type==_STRNG && res.subtype==-1)
	  return res;
	if (res.type==_SYMB){
	  unary_function_ptr & u=res._SYMBptr->sommet;
	  if (!findlabel && (u==at_return || u==at_break)) {
	    increment_instruction(it+1,itend,contextptr);
	    return res; // it->eval(eval_level(contextptr),contextptr);
	  }
	  if (!findlabel && u==at_goto){
	    findlabel=true;
	    label=res._SYMBptr->feuille;
	  }
	  if ( u==at_label && label==res._SYMBptr->feuille )
	    findlabel=false;
	}
	// restart the bloc if needed
	if (findlabel && it+1==itend)
	  it=prog._VECTptr->begin()-1;
      }
    }
    return res;
  }
  static const char _bloc_s []="bloc";
  static define_unary_function_eval2_index (145,__bloc,&_bloc,_bloc_s,&printasbloc);
  define_unary_function_ptr5( at_bloc ,alias_at_bloc,&__bloc,_QUOTE_ARGUMENTS,0);

  // test
  string printasifte(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=3) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    bool calc38=abs_calc_mode(contextptr)==38;
    const_iterateur it=feuille._VECTptr->begin();//,itend=feuille._VECTptr->end();
    string res(calc38?"IF ":"if ");
    if (xcas_mode(contextptr)==3)
      res="If ";
    if (calc38 || xcas_mode(contextptr)>0)
      res += sametoequal(*it).print(contextptr);
    else
      res += "("+it->print(contextptr);
    ++it;
    if (calc38 || xcas_mode(contextptr)>0){
      if (xcas_mode(contextptr)==3){
	if (is_undef(*(it+1)) && (it->type!=_SYMB || it->_SYMBptr->sommet!=at_bloc) )
	  return res + indent(contextptr)+"  "+it->print(contextptr);
	res += " Then "; // +indent(contextptr);
      }
      else
	res += calc38?" THEN ":" then ";
    }
    else
      res += ") ";
    debug_ptr(contextptr)->indent_spaces +=2;
    if ((calc38 || xcas_mode(contextptr)>0) && (it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
      res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
    else
      res += it->print(contextptr) +";";
    debug_ptr(contextptr)->indent_spaces -=2;
    ++it;
    while ( (calc38 || xcas_mode(contextptr)>0) && (it->type==_SYMB) && (it->_SYMBptr->sommet==at_ifte) ){
      if (xcas_mode(contextptr)==3)
	res += indent(contextptr)+"Elseif ";
      else
	res += indent(contextptr)+ (calc38? "ELIF ":"elif ");
      it=it->_SYMBptr->feuille._VECTptr->begin();
      res += it->print(contextptr);
      if (xcas_mode(contextptr)==3)
	res += " Then "; // +indent(contextptr);
      else
	res += calc38? " THEN ":" then ";
      ++it;
      debug_ptr(contextptr)->indent_spaces +=2;
      if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
      else
	res += it->print(contextptr) +";" ;
      debug_ptr(contextptr)->indent_spaces -=2;
      ++it;
    }
    if (!is_zero(*it)){
      if (!calc38 && xcas_mode(contextptr)<=0 && (res[res.size()-1]=='}'))
	res += indent(contextptr)+" else ";
      else {
	if (calc38)
	  res += " ELSE ";
	else {
	  if (xcas_mode(contextptr)<=0)
	    res +=";"; 
	  if (xcas_mode(contextptr)==3)
	    res += indent(contextptr)+"Else ";
	  else
	    res+= " else ";
	}
      }
      debug_ptr(contextptr)->indent_spaces +=2;
      if ((calc38 || xcas_mode(contextptr)>0) && (it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
      else {
	res += it->print(contextptr) +";";
	if (!xcas_mode(contextptr))
	  res += ";";
      }
      debug_ptr(contextptr)->indent_spaces -=2;
    }
    // FIXME? NO ; AT END OF IF
    if ((xcas_mode(contextptr)<=0) && (res[res.size()-1]!='}'))
      res +=" ";
    if (calc38)
      res += " END ";
    else {
      if ( (xcas_mode(contextptr) ==1) || (xcas_mode(contextptr) == 1+_DECALAGE) )
	res += indent(contextptr)+ "fi ";
      if (xcas_mode(contextptr)==2)
	res += indent(contextptr)+ "end_if ";
      if (xcas_mode(contextptr)==3)
	res += indent(contextptr)+"EndIf ";
    }
    return res;
  }
  symbolic symb_ifte(const gen & test,const gen & oui, const gen & non){
    return symbolic(at_ifte,gen(makevecteur(test,oui,non),_SEQ__VECT));
  }

  gen symb_return(const gen & arg){
    return gen(symbolic(at_return,arg));
  }
  gen symb_when(const gen & arg){
    return gen(symbolic(at_when,arg));
  }

  gen ifte(const gen & args,bool isifte,const context * contextptr){
    gen test,res;
    if (args.type!=_VECT || args._VECTptr->size()!=3){
      gensizeerr(gettext("Ifte must have 3 args"),res);
      return res;
    }
    test=args._VECTptr->front();
    test=equaltosame(test.eval(eval_level(contextptr),contextptr)).eval(eval_level(contextptr),contextptr);
    if (!is_integer(test)){
      test=test.evalf_double(eval_level(contextptr),contextptr);
      if ( (test.type!=_DOUBLE_) && (test.type!=_CPLX) ){
	if (isifte){
	  gensizeerr(gettext("Ifte: Unable to check test"),res); 
	  return res;
	}
	else
	  return symb_when(args);
      }
    }
    bool rt;
    gen clause_vraie=(*(args._VECTptr))[1];
    gen clause_fausse=args._VECTptr->back();
    // *logptr(contextptr) << "Ifte " << debug_ptr(contextptr)->current_instruction << endl ;
    if (is_zero(test)){ // test false, do the else part
      if (isifte){
	increment_instruction(clause_vraie,contextptr);
	// *logptr(contextptr) << "Else " << debug_ptr(contextptr)->current_instruction << endl ;
	++debug_ptr(contextptr)->current_instruction;
	if (debug_ptr(contextptr)->debug_mode){
	  debug_loop(test,contextptr);
	  if (is_undef(test)) return test;
	}
      }
      rt=clause_fausse.is_symb_of_sommet(at_return);
      if (rt)
	clause_fausse=clause_fausse._SYMBptr->feuille;
      // res=clause_fausse.eval(eval_level(contextptr),contextptr);
      if (!clause_fausse.in_eval(eval_level(contextptr),res,contextptr))
	res=clause_fausse;
      if (rt && (res.type!=_SYMB || res._SYMBptr->sommet!=at_return))
	res=symb_return(res);
      // *logptr(contextptr) << "Else " << debug_ptr(contextptr)->current_instruction << endl ;
    }
    else { // test true, do the then part
      if (isifte){
	++debug_ptr(contextptr)->current_instruction;
	if (debug_ptr(contextptr)->debug_mode){
	  debug_loop(test,contextptr);
	  if (is_undef(test)) return test;
	}
      }
      rt=clause_vraie.is_symb_of_sommet(at_return);
      if (rt)
	clause_vraie=clause_vraie._SYMBptr->feuille;
      // res=clause_vraie.eval(eval_level(contextptr),contextptr);
      if (!clause_vraie.in_eval(eval_level(contextptr),res,contextptr))
	res=clause_vraie;
      if (rt && (res.type!=_SYMB || res._SYMBptr->sommet!=at_return) )
	res=symb_return(res);
      // *logptr(contextptr) << "Then " << debug_ptr(contextptr)->current_instruction << endl ;
      if (isifte)
	increment_instruction(clause_fausse,contextptr);
      // *logptr(contextptr) << "Then " << debug_ptr(contextptr)->current_instruction << endl ;
    }
    return res;
  }
  gen _ifte(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return ifte(args,true,contextptr);
  }
  static const char _ifte_s []="ifte";
  static define_unary_function_eval2_index (141,__ifte,&_ifte,_ifte_s,&printasifte);
  define_unary_function_ptr5( at_ifte ,alias_at_ifte,&__ifte,_QUOTE_ARGUMENTS,0);

  gen _evalb(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen test=equaltosame(args);
    test=normal(test,contextptr);
    test=test.eval(eval_level(contextptr),contextptr);
    test=test.evalf_double(1,contextptr);
    if ( (test.type!=_DOUBLE_) && (test.type!=_CPLX) )
      return symbolic(at_evalb,args);
    if (is_zero(test))
      return zero;
    return plus_one;
  }
  static const char _evalb_s []="evalb";
  static define_unary_function_eval_quoted (__evalb,&_evalb,_evalb_s);
  define_unary_function_ptr5( at_evalb ,alias_at_evalb,&__evalb,_QUOTE_ARGUMENTS,true);

  static const char _maple_if_s []="if";
  static define_unary_function_eval2_quoted (__maple_if,&_ifte,_maple_if_s,&printasifte);
  define_unary_function_ptr5( at_maple_if ,alias_at_maple_if,&__maple_if,_QUOTE_ARGUMENTS,0);

  static string printaswhen(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    bool b=abs_calc_mode(contextptr)==38;
    if (b || xcas_mode(contextptr)||feuille.type!=_VECT || feuille._VECTptr->size()!=3)
      return (b?"IFTE":sommetstr)+("("+feuille.print(contextptr)+")");
    vecteur & v=*feuille._VECTptr;
    if (calc_mode(contextptr)==1){
      string s="If["+v[0].print(contextptr)+","+v[1].print(contextptr);
      if (!is_undef(v[2]))
	s +=","+v[2].print(contextptr);
      return s+"]";
    }
    return "(("+v[0].print(contextptr)+")? "+v[1].print(contextptr)+" : "+v[2].print(contextptr)+")";
  }
  gen symb_when(const gen & t,const gen & a,const gen & b){
    return symbolic(at_when,gen(makevecteur(t,a,b),_SEQ__VECT));
  }
  gen _when(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(gettext("3 or 4 arguments expected"));
    vecteur & v=*args._VECTptr;
    if (v.size()==3){
      gen res=ifte(args,false,contextptr);
      return res;
    }
    if (v.size()!=4)
      return gentypeerr(contextptr);
    gen res=ifte(vecteur(v.begin(),v.begin()+3),false,contextptr);
    if (res.type==_SYMB && res._SYMBptr->sommet==at_when)
      return v[3];
    return res;
  }
  static const char _when_s []="when";
  static define_unary_function_eval2_quoted (__when,&_when,_when_s,&printaswhen);
  define_unary_function_ptr5( at_when ,alias_at_when,&__when,_QUOTE_ARGUMENTS,true);

  // convert back increment and decrement to sto
  static gen from_increment(const gen & g){
    int type=0;
    if (g.is_symb_of_sommet(at_increment))
      type=1;
    if (g.is_symb_of_sommet(at_decrement))
      type=-1;
    if (type){
      gen & f =g._SYMBptr->feuille;
      if (f.type!=_VECT)
	return symbolic(at_sto,gen(makevecteur(symbolic(at_plus,makevecteur(f,type)),f),_SEQ__VECT));
      vecteur & v = *f._VECTptr;
      if (v.size()!=2)
	return gensizeerr(gettext("from_increment"));
      return symbolic(at_sto,gen(makevecteur(symbolic(at_plus,gen(makevecteur(v[0],type*v[1]),_SEQ__VECT)),v[0]),_SEQ__VECT));
    }
    return g;
  }

  // loop
  string printasfor(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT) 
      return sommetstr+('('+feuille.print(contextptr)+')');
    if (feuille._VECTptr->size()==2)
      return feuille._VECTptr->front().print(contextptr)+ " in "+feuille._VECTptr->back().print(contextptr);
    if (feuille._VECTptr->size()!=4)
      return sommetstr+('('+feuille.print(contextptr)+')');
    int maplemode=xcas_mode(contextptr) & 0x07;
    if (abs_calc_mode(contextptr)==38)
      maplemode=4;
    const_iterateur it=feuille._VECTptr->begin();//,itend=feuille._VECTptr->end();
    string res;
    gen inc(from_increment(*(it+2)));
    if (is_integer(*it) && is_integer(*(it+2))){
      ++it;
      if (is_one(*it) && (it+2)->is_symb_of_sommet(at_bloc)){
	const gen & loopf=(it+2)->_SYMBptr->feuille;
	if (loopf.type==_VECT && loopf._VECTptr->back().is_symb_of_sommet(at_ifte)){
	  const vecteur & condv=*loopf._VECTptr->back()._SYMBptr->feuille._VECTptr;
	  if (condv.size()==3 && is_zero(condv[2]) && condv[1].is_symb_of_sommet(at_break)){ 
	    // repeat until condv[0] loop, loopf except the end is the loop
	    vecteur corps=vecteur(loopf._VECTptr->begin(),loopf._VECTptr->end()-1);
	    res = maplemode==4?"REPEAT ":"repeat ";
	    res += printasinnerbloc(corps,contextptr);
	    res += indent(contextptr);
	    res += maplemode==4?"UNTIL ":"until ";
	    res += condv[0].print();
	    return res;
	  }
	}
      }
      if (it->is_symb_of_sommet(at_for) && it->_SYMBptr->feuille.type==_VECT && it->_SYMBptr->feuille._VECTptr->size()==2){
	res = "for "+it->_SYMBptr->feuille._VECTptr->front().print(contextptr)+" in "+ it->_SYMBptr->feuille._VECTptr->back().print(contextptr)+ " do ";
      }
      else {
	if (maplemode>0){
	  if (maplemode==3 && is_one(*it) ){
	    it += 2;
	    res="Loop";
	    if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	      res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
	    else
	      res += it->print(contextptr) +";";
	    return res+indent(contextptr)+"EndLoop";
	  }
	  if (maplemode==3)
	    res = "While "+ sametoequal(*it).print(contextptr) +indent(contextptr);
	  else
	    res = (maplemode==4?"WHILE ":"while ") + sametoequal(*it).print(contextptr) + (maplemode==4?" DO":" do ");
	}
	else
	  res = "while("+it->print(contextptr)+")";
      }
      ++it;
      ++it;
      debug_ptr(contextptr)->indent_spaces += 2;
      if ((maplemode>0) && (it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	res += printasinnerbloc(it->_SYMBptr->feuille,contextptr)+";";
      else
	res += it->print(contextptr) +";";
      debug_ptr(contextptr)->indent_spaces -= 2;
      if (maplemode==1)
	return res+indent(contextptr)+" od;";
      if (maplemode==2)
	return res+indent(contextptr)+" end_while;";
      if (maplemode==3)
	return res+indent(contextptr)+" EndWhile";
      if (maplemode==4)
	return res+indent(contextptr)+" END;";
    }
    else {  
      if (maplemode>0){// pb for generic loops for Maple translation
	gen inc=from_increment(*(it+2));
	if ( (it->type!=_SYMB) || (it->_SYMBptr->sommet!=at_sto) || (inc.type!=_SYMB) || inc._SYMBptr->sommet!=at_sto || (it->_SYMBptr->feuille._VECTptr->back()!=inc._SYMBptr->feuille._VECTptr->back()) )
	  return "Maple/Mupad/TI For: unable to convert";
	gen var_name=it->_SYMBptr->feuille._VECTptr->back();
	gen step=normal(inc._SYMBptr->feuille._VECTptr->front()-var_name,contextptr);
	gen condition=*(it+1),limite=plus_inf;
	if (is_positive(-step,contextptr)) 
	  limite=minus_inf;
	bool simple_loop=false,strict=true,ascending=true;
	if (condition.type==_SYMB){
	  unary_function_ptr op=condition._SYMBptr->sommet;
	  if (condition._SYMBptr->feuille.type==_VECT){
	    if (op==at_inferieur_strict)
	      simple_loop=true;
	    if (op==at_inferieur_egal){
	      strict=false;
	      simple_loop=true;
	    }
	    if (op==at_superieur_strict){
	      simple_loop=(maplemode>=2);
	      ascending=false;
	    }
	    if (op==at_superieur_egal){
	      simple_loop=(maplemode>=2);
	      ascending=false;
	      strict=false;
	    }
	  }
	  if (simple_loop){
	    simple_loop=(condition._SYMBptr->feuille._VECTptr->front()==var_name);
	    limite=condition._SYMBptr->feuille._VECTptr->back();
	  }
	}
	if (maplemode==3)
	  res="For ";
	else
	  res = (maplemode==4?"FOR ":"for ");
	res += var_name.print(contextptr);
	if (maplemode==3)
	  res += ",";
	else
	  res += (maplemode==4?" FROM ":" from ");
	res += it->_SYMBptr->feuille._VECTptr->front().print(contextptr);
	if (maplemode==3){
	  res += ","+limite.print(contextptr);
	  if (!is_one(step))
	    res += ","+step.print(contextptr);
	  if (!simple_loop)
	    res += indent(contextptr)+"If not("+(it+1)->print(contextptr)+")"+indent(contextptr)+"Exit";
	  res += indent(contextptr);
	}
	else {
	  gen absstep=step;
	  if (simple_loop){
	    absstep = abs(step,contextptr); 
	    if (ascending)
	      res += maplemode==4?" TO ":" to ";
	    else
	      res += maplemode==4?" DOWNTO ":" downto ";
	    res += limite.print(contextptr);
#ifndef BCD
	    if (!strict && !is_integer(step)){
	      if (ascending)
		res +="+";
	      else
		res += "-";
	      res += absstep.print(contextptr);
	      res += "/2";
	    }
#endif
	  }
	  if (!is_one(absstep)){
	    if (maplemode==2)
	      res += " step ";
	    else
	      res += maplemode==4?" STEP ":" by ";
	    res += step.print(contextptr);
	  }
	  if (!simple_loop){
	    res += (maplemode==4)?" WHILE ":" while ";
	    res += (it+1)->print(contextptr);
	  }
	  res += maplemode==4?" DO ":" do ";
	}
	it += 3;
	if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	  res += printasinnerbloc(it->_SYMBptr->feuille,contextptr)+";";
	else
	  res += it->print(contextptr)+";" ;
	if (maplemode==1)
	  return res + indent(contextptr)+" od;";
	if (maplemode==2)
	  return res + indent(contextptr)+" end_for;";
	if (maplemode==3)
	  return res + indent(contextptr)+"EndFor";
	if (maplemode==4)
	  return res + indent(contextptr)+"END;";
      }
      res="for (";
      res += it->print(contextptr) + ';';
      ++it;
      res += it->print(contextptr) + ';';
      ++it;
      res += it->print(contextptr) + ") ";
      ++it;
      debug_ptr(contextptr)->indent_spaces += 2;
      res += it->print(contextptr) ;
      debug_ptr(contextptr)->indent_spaces -= 2;
    }
    if (res[res.size()-1]!='}')
      res += "; ";
    return res;
  }
  symbolic symb_for(const gen & e){
    return symbolic(at_for,e);
  }
  symbolic symb_for(const gen & a,const gen & b,const gen & c,const gen & d){
    return symbolic(at_for,gen(makevecteur(a,b,c,d),_SEQ__VECT));
  }
  
  static gen to_increment(const gen & g){
    if (!g.is_symb_of_sommet(at_sto))
      return g;
    gen & f =g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=2)
      return g;
    gen & a = f._VECTptr->front();
    gen & b = f._VECTptr->back();
    if (b.type!=_IDNT || a.type!=_SYMB)
      return g;
    gen & af=a._SYMBptr->feuille;
    if (af.type!=_VECT || af._VECTptr->empty())
      return g;
    vecteur & av= *af._VECTptr;
    int s=av.size();
    int type=0;
    if (a.is_symb_of_sommet(at_plus))
      type=1;
    // there was a wrong test with at_minus for -= (type=-1)
    if (type && av.front()==b){
      if (s==2){
	if (is_one(av.back()))
	  return symbolic(type==1?at_increment:at_decrement,b);
	if (is_minus_one(av.back()))
	  return symbolic(type==1?at_decrement:at_increment,b);
	return symbolic(type==1?at_increment:at_decrement,gen(makevecteur(b,av.back()),_SEQ__VECT));
      }
      if (type)
	return symbolic(at_increment,gen(makevecteur(b,symbolic(at_plus,vecteur(av.begin()+1,av.end()))),_SEQ__VECT));
    }
    return g;
  }
  static bool ck_is_one(const gen & g){
    if (is_one(g))
      return true;
    if (g.type>_POLY){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Unable to eval test in loop : ")+g.print());
#endif
      return false; // this will stop the loop in caller
    }
    return false;
  }
  static bool set_for_in(int counter,int for_in,const vecteur & v,const string & s,const gen & name,GIAC_CONTEXT){
    if (counter<0 || counter>=int(for_in==1?v.size():s.size()))
      return false;
    return !is_undef(sto(for_in==1?v[counter]:string2gen(string(1,s[counter]),false),name,contextptr));
  }
  gen _for(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    // for elem in list: for(elem,list), inert form
    if (args.type!=_VECT || args._VECTptr->size()==2)
      return symb_for(args);
    if (args._VECTptr->size()!=4)
      return gensizeerr(gettext("For must have 4 args"));
    // Initialization
    gen initialisation=args._VECTptr->front();
    // add assigned variables to be local
    bool bound=false;
    vecteur loop_var;
    int protect=0;
    context * newcontextptr=(context * ) contextptr;
    if ( (initialisation.type==_SYMB) && (initialisation._SYMBptr->sommet==at_sto)){
      gen variable=initialisation._SYMBptr->feuille._VECTptr->back();
      if (variable.type==_IDNT){
	if (contextptr==context0 && (xcas_mode(contextptr)!=1 && (!variable._IDNTptr->localvalue || variable._IDNTptr->localvalue->empty() || (*variable._IDNTptr->localvalue)[variable._IDNTptr->localvalue->size()-2].val<protection_level-1) ) ){
	  bound=true;
	  loop_var=makevecteur(variable);
	  protect=bind(makevecteur(zero),loop_var,newcontextptr);
	}
      }
      else {
#ifndef NO_STDEXCEPT
	throw(std::runtime_error(gettext("Invalid loop index (hint: i=sqrt(-1)!)")));
#endif
	return undeferr(gettext("Invalid loop index (hint: i=sqrt(-1)!)"));
      }
    }
    gen test=(*(args._VECTptr))[1];
    if ((test.type==_SYMB) && (test._SYMBptr->sommet==at_equal))
      test = symb_same(test._SYMBptr->feuille._VECTptr->front(),test._SYMBptr->feuille._VECTptr->back());
    // FIXME: eval local variables in test that are not in increment and prog
    gen increment=to_increment((*(args._VECTptr))[2]);
    gen prog=(*(args._VECTptr))[3];
    if ( (prog.type==_SYMB) && (prog._SYMBptr->sommet==at_bloc))
      prog=prog._SYMBptr->feuille;
    vecteur forprog=prog.type==_VECT?*prog._VECTptr:vecteur(1,prog);
    iterateur it,itbeg=forprog.begin(),itend=forprog.end();
    for (it=itbeg;it!=itend;++it){
      *it=to_increment(*it);
    }
    gen res,oldres;
    // loop
    int save_current_instruction=debug_ptr(newcontextptr)->current_instruction;
    int eval_lev=eval_level(newcontextptr);
    debug_struct * dbgptr=debug_ptr(newcontextptr);
#ifndef NO_STDEXCEPT
    try {
#endif
      bool findlabel=false;
      gen label,newres;
      int counter=0;
      int for_in=0; // 1 in list, 2 in string
      vecteur for_in_v;
      string for_in_s;
      gen index_name;
      if ((test.is_symb_of_sommet(at_for) || test.is_symb_of_sommet(at_pour))&& test._SYMBptr->feuille.type==_VECT && test._SYMBptr->feuille._VECTptr->size()==2){
	gen tmp=eval(test._SYMBptr->feuille._VECTptr->back(),eval_lev,newcontextptr);
	if (tmp.type==_VECT){
	  for_in_v=*tmp._VECTptr;
	  for_in=1;
	}
	else {
	  if (tmp.type==_STRNG){
	    for_in_s=*tmp._STRNGptr;
	    for_in=2;
	  }
	  else
	    return gensizeerr(contextptr);
	}
	index_name=test._SYMBptr->feuille._VECTptr->front();
      }
      for (initialisation.eval(eval_lev,newcontextptr);
	   for_in?set_for_in(counter,for_in,for_in_v,for_in_s,index_name,newcontextptr):ck_is_one(test.eval(eval_lev,newcontextptr).evalf(1,newcontextptr));
	   ++counter,(test.val?increment.eval(eval_lev,newcontextptr):0)){
	if (interrupted)
	  break;
	dbgptr->current_instruction=save_current_instruction;
	findlabel=false;
	// add a test for boucle of type program/composite
	// if that's the case call eval with test for break and continue
	for (it=itbeg;!interrupted && it!=itend;++it){
	  if (ctrl_c || (res.type==_STRNG && res.subtype==-1)){
	    interrupted = true; ctrl_c=false;
	    *logptr(contextptr) << "Stopped in loop" << endl;
	    gensizeerr(gettext("Stopped by user interruption."),res);
	    break;
	  }
	  oldres=res;
	  ++dbgptr->current_instruction;
	  if (dbgptr->debug_mode){
	    debug_loop(res,newcontextptr);
	    if (is_undef(res)){
	      increment_instruction(it+1,itend,newcontextptr);
	      if (bound)
		leave(protect,loop_var,newcontextptr);
	      return res;
	    }
	  }
	  if (!findlabel)
	    res=it->eval(eval_lev,newcontextptr);
	  else
	    res=*it;
	  if (is_return(res,newres)) {
	    increment_instruction(it+1,itend,newcontextptr);
	    if (bound)
	      leave(protect,loop_var,newcontextptr);
	    return res;
	  }
	  if (res.type==_SYMB){
	    unary_function_ptr & u=res._SYMBptr->sommet;
	    if (!findlabel){ 
	      if (u==at_break){
		increment_instruction(it+1,itend,newcontextptr);
		test=zero;
		res=u; // res=oldres;
		break;
	      }
	      if (u==at_continue){
		increment_instruction(it+1,itend,newcontextptr);
		res=oldres;
		break;
	      }
	    }
	    else {
	      if (u==at_label && label==res._SYMBptr->feuille)
		findlabel=false;
	    }
	    if (!findlabel && u==at_goto){
	      findlabel=true;
	      label=res._SYMBptr->feuille;
	    }
	  }
	  if (findlabel && it+1==itend)
	    it=itbeg-1;
	} // end of loop of FOR bloc instructions
      } // end of user FOR loop
      dbgptr->current_instruction=save_current_instruction;
      increment_instruction(itbeg,itend,newcontextptr);
#ifndef NO_STDEXCEPT
    } // end try
    catch (std::runtime_error & e){
      if (bound)
	leave(protect,loop_var,newcontextptr);
      gen res(string2gen(e.what(),false));
      res.subtype=-1;
      return res;
    }
#endif
    if (bound)
      leave(protect,loop_var,newcontextptr);
    return res==at_break?string2gen("breaked",false):res;
  }

  static const char _for_s []="for";
  static define_unary_function_eval2_index (143,__for,&_for,_for_s,&printasfor);
  define_unary_function_ptr5( at_for ,alias_at_for,&__for,_QUOTE_ARGUMENTS,0);

  // returns level or -1 on error
  int bind(const vecteur & vals,const vecteur & vars,context * & contextptr){
    if (vals.size()!=vars.size()){
#ifdef DEBUG_SUPPORT
      setsizeerr(gen(vals).print(contextptr)+ " size() != " + gen(vars).print(contextptr));
#endif
      return -RAND_MAX;
    }
    if (debug_ptr(contextptr)->debug_localvars)
      *debug_ptr(contextptr)->debug_localvars=vars;
    const_iterateur it=vals.begin(),itend=vals.end();
    const_iterateur jt=vars.begin();
    gen tmp;
    if (contextptr){
      context * newcontextptr = new context(* contextptr);
      newcontextptr->tabptr = new sym_tab;
      if (contextptr->globalcontextptr)
	newcontextptr->globalcontextptr = contextptr->globalcontextptr;
      else 
	newcontextptr->globalcontextptr = contextptr;
      newcontextptr->previous=contextptr;
      contextptr=newcontextptr;
      if (debug_ptr(contextptr))
	debug_ptr(contextptr)->debug_contextptr=contextptr;
    }
    for (;it!=itend;++it,++jt){
      if (jt->type==_SYMB){
	if (jt->_SYMBptr->sommet==at_check_type){
	  tmp=jt->_SYMBptr->feuille._VECTptr->back();
	  if (is_undef(_check_type(makevecteur(jt->_SYMBptr->feuille._VECTptr->front(),*it),contextptr)))
	    return -RAND_MAX;
	}
	else {
	  if (jt->_SYMBptr->sommet==at_double_deux_points ){
	    tmp=jt->_SYMBptr->feuille._VECTptr->front();
	    if (is_undef(_check_type(makevecteur(jt->_SYMBptr->feuille._VECTptr->back(),*it),contextptr)))
	      return -RAND_MAX;
	  }
	  else { 
	    if (jt->_SYMBptr->sommet==at_of){
	      tmp=jt->_SYMBptr->feuille._VECTptr->front();
	      *logptr(contextptr) << gettext("Invalid variable ")+jt->print(contextptr)+gettext(" using ")+tmp.print(contextptr)+gettext(" instead.");
	    }
	    else
	      tmp=*jt;
	  }
	}
      }
      else
	tmp=*jt;
      if (tmp.type==_IDNT){
	if (contextptr)
	  (*contextptr->tabptr)[tmp._IDNTptr->id_name]=*it;
	else
	  tmp._IDNTptr->push(protection_level,*it);
      }
      else {
	if (tmp.type==_FUNC){
#ifndef NO_STDEXCEPT
	  setsizeerr(gettext("Reserved word:")+tmp.print(contextptr));
#else
	  *logptr(contextptr) << gettext("Reserved word:")+tmp.print(contextptr) << endl;
#endif
	  return -RAND_MAX;
	}
	else {
#ifndef NO_STDEXCEPT
	  setsizeerr(gettext("Not bindable")+tmp.print(contextptr));
#else
	  *logptr(contextptr) << gettext("Not bindable")+tmp.print(contextptr) << endl;
#endif
	  return -RAND_MAX;
	}
      }
    }
    if (!contextptr)
      ++protection_level;
    return protection_level-1;
  }

  bool leave(int protect,vecteur & vars,context * & contextptr){
    iterateur it=vars.begin(),itend=vars.end(),jt,jtend;
    gen tmp;
    if (contextptr){
      if (contextptr->previous){
	context * tmpptr=contextptr;
	contextptr=contextptr->previous;
	if (debug_ptr(contextptr))
	  debug_ptr(contextptr)->debug_contextptr=contextptr;
	if (tmpptr->tabptr){
	  delete tmpptr->tabptr;
	  delete tmpptr;
	  return true;
	}
      }
      return false;
    }
    for (;it!=itend;++it){
      if (it->type==_SYMB && it->_SYMBptr->sommet==at_check_type)
	tmp=it->_SYMBptr->feuille._VECTptr->back();
      else {
	if (it->type==_SYMB && it->_SYMBptr->sommet==at_double_deux_points)
	  tmp=it->_SYMBptr->feuille._VECTptr->front();
	else
	  tmp=*it;
      }
#ifdef DEBUG_SUPPORT
      if (tmp.type!=_IDNT) setsizeerr(gettext("prog.cc/leave"));
#endif    
      if (tmp._IDNTptr->localvalue){
	jt=tmp._IDNTptr->localvalue->begin(),jtend=tmp._IDNTptr->localvalue->end();
	for (;;){
	  if (jt==jtend)
	    break;
	  --jtend;
	  --jtend;
	  if (protect>jtend->val){
	    ++jtend;
	    ++jtend;
	    break;
	  }
	}
	tmp._IDNTptr->localvalue->erase(jtend,tmp._IDNTptr->localvalue->end());
      }
    }
    protection_level=protect;
    return true;
  }

  static string printaslocal(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    const_iterateur it=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    string res;
    gen local_global=*it,locals=gen2vecteur(*it),globals=vecteur(0);
    if (local_global.type==_VECT && local_global._VECTptr->size()==2){ 
      gen f=local_global._VECTptr->front(),b=local_global._VECTptr->back();
      if (f.type!=_IDNT){
	locals=gen2vecteur(f);
	globals=gen2vecteur(b);
      }
    }
    if (!locals._VECTptr->empty()){
      res += indent(contextptr);
      if (abs_calc_mode(contextptr)==38)
	res += "LOCAL ";
      else {
	if (xcas_mode(contextptr)>0){
	  if (xcas_mode(contextptr)==3)
	    res += "Local ";
	  else
	    res += "local ";
	}
	else
	  res += "{ local ";
      }
      if (locals._VECTptr->size()==1)
	res += locals._VECTptr->front().print(contextptr);
      else {
	locals.subtype=_SEQ__VECT;
	res += locals.print(contextptr);
      }
      if (xcas_mode(contextptr)!=3)
	res +=';';
    }
    if (!globals._VECTptr->empty()){
      res += indent(contextptr);
      if (abs_calc_mode(contextptr)==38)
	res += "GLOBAL ";
      else {
	if (xcas_mode(contextptr)>0){
	  if (xcas_mode(contextptr)==3)
	    res += "Global ";
	  else
	    res += "global ";
	}
	else
	  res += " global ";
      }
      if (globals._VECTptr->size()==1)
	res += globals._VECTptr->front().print(contextptr);
      else {
	globals.subtype=_SEQ__VECT;
	res += globals.print(contextptr);
      }
      if (xcas_mode(contextptr)!=3)
	res +=';';      
    }
    if (abs_calc_mode(contextptr)==38)
      res += indent(contextptr)+"BEGIN ";
    else {
      if (xcas_mode(contextptr)>0 && xcas_mode(contextptr)!=3)
	res += indent(contextptr)+"begin ";
    }
    debug_ptr(contextptr)->indent_spaces +=2;
    ++it;
    for ( ;;){
      if (it->type!=_VECT)
	res += indent(contextptr)+it->print(contextptr);
      else {
	const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	for (;jt!=jtend;++jt){
	  res += indent(contextptr)+jt->print(contextptr);
	  if (xcas_mode(contextptr)!=3)
	    res += "; " ;
	}
      }
      ++it;
      if (it==itend){
	debug_ptr(contextptr)->indent_spaces -= 2;
	if (abs_calc_mode(contextptr)==38)
	  res += indent(contextptr)+"END;";
	else {
	  switch (xcas_mode(contextptr)){
	  case 0:
	    res += indent(contextptr)+"}";
	    break;
	  case 1: case 1+_DECALAGE:
	    res+=indent(contextptr)+"end;";
	    break;
	  case 2:
	    return res+=indent(contextptr)+"end_proc;";
	    break;
	  }
	}
	return res;
      }
      else
	if (xcas_mode(contextptr)!=3)
	  res +="; ";
    }
  }
  gen symb_local(const gen & a,const gen & b,GIAC_CONTEXT){
    gen newa,newb;
    replace_keywords(a,b,newa,newb,contextptr);
    return symbolic(at_local,gen(makevecteur(newa,newb),_SEQ__VECT));
  }
  gen symb_local(const gen & args,GIAC_CONTEXT){
    if (args.type==_VECT && args._VECTptr->size()==2)
      return symb_local(args._VECTptr->front(),args._VECTptr->back(),contextptr);
    return symbolic(at_local,args);
  }

  gen _local(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_local(args,contextptr);
    int s=args._VECTptr->size();
    if (s!=2)
      return gensizeerr(gettext("Local must have 2 args"));
    // Initialization
    gen vars=args._VECTptr->front();
    if (vars.type==_VECT && vars._VECTptr->size()==2 && vars._VECTptr->front().type!=_IDNT)
      vars = vars._VECTptr->front();
    if (vars.type!=_VECT)
      vars=makevecteur(vars);
    vecteur names,values;
    iterateur it=vars._VECTptr->begin(),itend=vars._VECTptr->end();
    names.reserve(itend-it);
    values.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type==_IDNT){
	names.push_back(*it);
#if 1
	gen err=string2gen(gettext("Unitialized local variable ")+it->print(contextptr),false);
	err.subtype=-1;
	values.push_back(err);
#else
	values.push_back(0);
#endif
	continue;
      }
      if ( (it->type!=_SYMB) || (it->_SYMBptr->sommet!=at_sto))
	return gentypeerr(contextptr);
      gen nom=it->_SYMBptr->feuille._VECTptr->back();
      gen val=it->_SYMBptr->feuille._VECTptr->front().eval(eval_level(contextptr),contextptr);
      if (nom.type!=_IDNT)
	return gentypeerr(contextptr);
      names.push_back(nom);
      values.push_back(val);
    }
    context * newcontextptr = (context *) contextptr;
    int protect=bind(values,names,newcontextptr);
    gen prog=args._VECTptr->back(),res,newres;
    if (protect!=-RAND_MAX){
      if (prog.type!=_VECT){
	++debug_ptr(newcontextptr)->current_instruction;
	if (debug_ptr(newcontextptr)->debug_mode){
	  debug_loop(res,newcontextptr);
	  if (!is_undef(res)){
	    if (!prog.in_eval(eval_level(newcontextptr),res,newcontextptr))
	      res=prog;
	  }
	}
	else {
	  if (!prog.in_eval(eval_level(newcontextptr),res,newcontextptr))
	    res=prog;
	}
      }
      else {
	it=prog._VECTptr->begin(),itend=prog._VECTptr->end();
	bool findlabel=false;
	gen label;
	for (;!ctrl_c && it!=itend;++it){
	  ++debug_ptr(newcontextptr)->current_instruction;
	  // cout << *it << endl;
	  if (debug_ptr(newcontextptr)->debug_mode){
	    debug_loop(res,newcontextptr);
	    if (!is_undef(res)){
	      if (!findlabel){
		if (!it->in_eval(eval_level(newcontextptr),res,newcontextptr))
		  res=*it;
	      }
	      else
		res=*it;
	    }
	  }
	  else {
	    if (!findlabel){
	      if (!it->in_eval(eval_level(newcontextptr),res,newcontextptr))
		res=*it;
	    }
	    else
	      res=*it;
	  }
	  if (res.type==_SYMB){
	    unary_function_ptr & u=res._SYMBptr->sommet;
	    if (findlabel && u==at_label && label==res._SYMBptr->feuille)
	      findlabel=false;
	    if (!findlabel && u==at_goto){
	      findlabel=true;
	      label=res._SYMBptr->feuille;
	    }
	  }
	  if (findlabel && it+1==itend)
	    it=prog._VECTptr->begin()-1;
	  if (!findlabel && is_return(res,newres) ){
	    // res=newres;
	    break;
	  }
	}
      }
      leave(protect,names,newcontextptr);
    }
    else
      return gensizeerr(contextptr);
    return res;
  }

  static const char _local_s []="local";
  static define_unary_function_eval2_index (85,__local,&_local,_local_s,&printaslocal);
  define_unary_function_ptr5( at_local ,alias_at_local,&__local,_QUOTE_ARGUMENTS,0);

  static string printasreturn(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( abs_calc_mode(contextptr)==38 || (xcas_mode(contextptr)==1) || (xcas_mode(contextptr)==1+_DECALAGE) )
      return "RETURN("+feuille.print(contextptr)+")";
    if (xcas_mode(contextptr)==3)
      return "Return "+feuille.print(contextptr);
    return sommetstr+("("+feuille.print(contextptr)+")");
  }
  static gen symb_return(const gen & args,GIAC_CONTEXT){
    return symbolic(at_return,args);
  }
  static const char _return_s []="return";
  static define_unary_function_eval2_index (86,__return,&symb_return,_return_s,&printasreturn);
  define_unary_function_ptr( at_return ,alias_at_return ,&__return);

  static string printastry_catch(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()<3) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    const_iterateur it=feuille._VECTptr->begin();//,itend=feuille._VECTptr->end();
    string res;
    if (feuille._VECTptr->size()==4){
      res = "IFERR ";
      res += printasinnerbloc(*it,contextptr);
      ++it;
      ++it;
      res += " THEN ";
      res += printasinnerbloc(*it,contextptr);
      ++it;
      res += " ELSE ";
      res += printasinnerbloc(*it,contextptr);
      res += " END";
      return res;
    }
    if (xcas_mode(contextptr)==3)
      res += "Try";
    else
      res += "try ";
    res += it->print(contextptr);
    ++it;
    if (xcas_mode(contextptr)==3){
      res += indent(contextptr)+"Else";
      ++it;
      if (!is_undef(*it))
	res += printasinnerbloc(*it,contextptr);
      res += indent(contextptr)+"EndTry";
    }
    else {
      if (res[res.size()-1]!='}')
	res += "; ";
      res += "catch(" + it->print(contextptr) + ")";
      ++it;
      res += it->print(contextptr);
      if (res[res.size()-1]!='}')
	res += "; ";
    }
    return res;
  }
  
  gen symb_try_catch(const gen & args){
    return symbolic(at_try_catch,args);
  }
  gen _try_catch(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_try_catch(args);
    int args_size=args._VECTptr->size();
    if (args_size!=3 && args_size!=4)
      return gensizeerr(gettext("Try_catch must have 3 or 4 args"));
    gen res;
    int saveprotect=protection_level;
    vector< vector<int> > save_sst_at_stack(debug_ptr(contextptr)->sst_at_stack);
    vecteur save_args_stack(debug_ptr(contextptr)->args_stack);
    vector<int> save_current_instruction_stack=debug_ptr(contextptr)->current_instruction_stack;
    int save_current_instruction=debug_ptr(contextptr)->current_instruction;
    bool do_else_try=args_size==4;
#ifndef NO_STDEXCEPT
    try {
      ++debug_ptr(contextptr)->current_instruction;
      if (debug_ptr(contextptr)->debug_mode)
	debug_loop(res,contextptr);
      res=args._VECTptr->front().eval(eval_level(contextptr),contextptr);
    }
    catch (std::runtime_error & error ){
      ++debug_ptr(contextptr)->current_instruction;
      if (debug_ptr(contextptr)->debug_mode)
	debug_loop(res,contextptr);
      // ??? res=args._VECTptr->front().eval(eval_level(contextptr),contextptr);
      do_else_try=false;
      if (!contextptr)
	protection_level=saveprotect;
      debug_ptr(contextptr)->sst_at_stack=save_sst_at_stack;
      debug_ptr(contextptr)->args_stack=save_args_stack;
      debug_ptr(contextptr)->current_instruction_stack=save_current_instruction_stack;
      gen id=(*(args._VECTptr))[1];
      string er(error.what());
      er = '"'+er+'"';
      gen tmpsto;
      if (id.type==_IDNT)
	tmpsto=sto(gen(er,contextptr),id,contextptr);
      if (is_undef(tmpsto)) return tmpsto;
      debug_ptr(contextptr)->current_instruction=save_current_instruction;
      increment_instruction(args._VECTptr->front(),contextptr);
      ++debug_ptr(contextptr)->current_instruction;
      if (debug_ptr(contextptr)->debug_mode)
	debug_loop(res,contextptr);
      res=(*args._VECTptr)[2].eval(eval_level(contextptr),contextptr);
    }
#else
    ++debug_ptr(contextptr)->current_instruction;
    if (debug_ptr(contextptr)->debug_mode)
      debug_loop(res,contextptr);
    if (is_undef(res)) return res;
    res=args._VECTptr->front().eval(eval_level(contextptr),contextptr);
    if (is_undef(res)){
      do_else_try=false;
      if (!contextptr)
	protection_level=saveprotect;
      debug_ptr(contextptr)->sst_at_stack=save_sst_at_stack;
      debug_ptr(contextptr)->args_stack=save_args_stack;
      debug_ptr(contextptr)->current_instruction_stack=save_current_instruction_stack;
      gen id=(*(args._VECTptr))[1];
      string er(gen2string(res));
      er = '"'+er+'"';
      gen tmpsto;
      if (id.type==_IDNT)
	tmpsto=sto(gen(er,contextptr),id,contextptr);
      if (is_undef(tmpsto)) return tmpsto;
      debug_ptr(contextptr)->current_instruction=save_current_instruction;
      increment_instruction(args._VECTptr->front(),contextptr);
      ++debug_ptr(contextptr)->current_instruction;
      if (debug_ptr(contextptr)->debug_mode){
	debug_loop(res,contextptr);
	if (is_undef(res)) return res;
      }
      res=(*args._VECTptr)[2].eval(eval_level(contextptr),contextptr);
    }
#endif
    if (do_else_try){
      res=args._VECTptr->back().eval(eval_level(contextptr),contextptr);
    }
    debug_ptr(contextptr)->current_instruction=save_current_instruction;
    increment_instruction(args._VECTptr->front(),contextptr);
    increment_instruction(args._VECTptr->back(),contextptr);
    return res;
  }
  static const char _try_catch_s []="try_catch";
  static define_unary_function_eval2_quoted (__try_catch,&_try_catch,_try_catch_s,&printastry_catch);
  define_unary_function_ptr5( at_try_catch ,alias_at_try_catch,&__try_catch,_QUOTE_ARGUMENTS,0);

  static gen feuille_(const gen & g,const gen & interval,GIAC_CONTEXT){
    vecteur v;
    if (g.type==_SYMB){
      gen & f=g._SYMBptr->feuille;
      if (f.type==_VECT)
	v=*f._VECTptr;
      else
	v=vecteur(1,f);
    }
    else {
      if (g.type==_VECT)
	v=*g._VECTptr;
      else
	v=vecteur(1,g);
    }
    int s=v.size();
    if (interval.type==_INT_){
      int i=interval.val-(xcas_mode(contextptr)!=0);
      if (i==-1 && g.type==_SYMB)
	return g._SYMBptr->sommet;
      if (i<0 || i>=s)
	return gendimerr(contextptr);
      return v[i];
    }
    if (interval.is_symb_of_sommet(at_interval)&& interval._SYMBptr->feuille.type==_VECT){
      vecteur & w=*interval._SYMBptr->feuille._VECTptr;
      if (w.size()!=2 || w.front().type!=_INT_ || w.back().type!=_INT_)
	return gentypeerr(contextptr);
      int i=w.front().val,j=w.back().val;
      if (i>j)
	return gen(vecteur(0),_SEQ__VECT);
      if (xcas_mode(contextptr)){
	--i;
	--j;
      }
      if (i<0 || i>=s || j<0 || j>=s)
	return gendimerr(contextptr);
      return gen(vecteur(v.begin()+i,v.begin()+j+1),_SEQ__VECT);
    }
    return gensizeerr(contextptr);
  }
  gen _feuille(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT){
      if (args.subtype==_SEQ__VECT && args._VECTptr->size()==2)
	return feuille_(args._VECTptr->front(),args._VECTptr->back(),contextptr);
      return gen(*args._VECTptr,_SEQ__VECT);
    }
    if (args.type!=_SYMB)
      return args;
    gen tmp=args._SYMBptr->feuille;
    if (tmp.type==_VECT)
      tmp.subtype=_SEQ__VECT;
    return tmp;
  }
  static const char _feuille_s []="op";
  static define_unary_function_eval2 (__feuille,&_feuille,_feuille_s,&printassubs);
  define_unary_function_ptr5( at_feuille ,alias_at_feuille,&__feuille,0,true);
  
  gen _maple_op(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT){
      vecteur & v=*args._VECTptr;
      if (args.subtype==_SEQ__VECT && v.size()>1)
	return feuille_(v.back(),v.front(),contextptr);
      return gen(v,_SEQ__VECT);
    }
    if (args.type!=_SYMB)
      return args; // was symbolic(at_maple_op,args);
    return args._SYMBptr->feuille;
  }
  static const char _maple_op_s []="op";
  static define_unary_function_eval2 (__maple_op,&_maple_op,_maple_op_s,&printasmaple_subs);
  define_unary_function_ptr( at_maple_op ,alias_at_maple_op ,&__maple_op);
  
  gen _sommet(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_SYMB)
      return at_id;
    int nargs;
    if (args._SYMBptr->feuille.type==_VECT)
        nargs=args._SYMBptr->feuille._VECTptr->size();
    else
        nargs=1;
    return gen(args._SYMBptr->sommet,nargs);
  }
  static const char _sommet_s []="sommet";
  static define_unary_function_eval (__sommet,&_sommet,_sommet_s);
  define_unary_function_ptr5( at_sommet ,alias_at_sommet,&__sommet,0,true);

  // replace in g using equalities in v
  gen subsop(const vecteur & g,const vecteur & v,const gen & sommet,GIAC_CONTEXT){
    gen newsommet=sommet;
    vecteur res(g);
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if ( (!it->is_symb_of_sommet(at_equal) && !it->is_symb_of_sommet(at_same)) || it->_SYMBptr->feuille.type!=_VECT || it->_SYMBptr->feuille._VECTptr->size()!=2){
	*logptr(contextptr) << gettext("Unknown subsop rule ") << *it << endl;
	continue;
      }
      vecteur w=*it->_SYMBptr->feuille._VECTptr;
      if (w.front().type==_VECT){
	vecteur rec=*w.front()._VECTptr;
	if (rec.size()<1)
	  return gendimerr(contextptr);
	if (rec.size()==1)
	  w.front()=rec.front();
	else {
	  int i=rec.front().val;
	  if (i>=0 && xcas_mode(contextptr))
	    --i;
	  if (i<0)
	    i += res.size();
	  if (rec.front().type!=_INT_ || i<0 || i>=signed(res.size()))
	    return gendimerr(contextptr);
	  if (is_undef( (res[i]=subsop(res[i],vecteur(1,symbolic(at_equal,gen(makevecteur(vecteur(rec.begin()+1,rec.end()),w.back()),_SEQ__VECT))),contextptr)) ) )
	    return res[i];
	  continue;
	}
      }
      if (w.front().type!=_INT_)
	continue;
      int i=w.front().val;
      if (i>=0 && xcas_mode(contextptr))
	--i;
      /*
      if (i==-1){
	newsommet=w.back();
	continue;
      }
      */
      if (i<0)
	i += res.size();
      if (i<0 || i>=signed(res.size()))
	return gendimerr(contextptr);
      res[i]=w.back();
    }
    it=res.begin();
    itend=res.end();
    vecteur res1;
    res1.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type!=_VECT || it->subtype!=_SEQ__VECT || !it->_VECTptr->empty() )
	res1.push_back(*it);
    }
    if (newsommet.type!=_FUNC)
      return res1;
    else
      return symbolic(*newsommet._FUNCptr,res1);
  }
  gen subsop(const gen & g,const vecteur & v,GIAC_CONTEXT){
    if (g.type==_VECT)
      return subsop(*g._VECTptr,v,0,contextptr);
    if (g.type!=_SYMB)
      return g;
    vecteur w(gen2vecteur(g._SYMBptr->feuille));
    return subsop(w,v,g._SYMBptr->sommet,contextptr);
  }
  gen _maple_subsop(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gendimerr(contextptr);
    return subsop(v.back(),vecteur(v.begin(),v.end()-1),contextptr);
  }
  static const char _maple_subsop_s []="subsop";
  static define_unary_function_eval2 (__maple_subsop,&_maple_subsop,_maple_subsop_s,&printasmaple_subs);
  define_unary_function_ptr( at_maple_subsop ,alias_at_maple_subsop ,&__maple_subsop);
  
  gen _subsop(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gendimerr(contextptr);
    return subsop(v.front(),vecteur(v.begin()+1,v.end()),contextptr);
  }
  static const char _subsop_s []="subsop";
  static define_unary_function_eval2 (__subsop,&_subsop,_subsop_s,&printassubs);
  define_unary_function_ptr( at_subsop ,alias_at_subsop ,&__subsop);
  
  // static gen symb_append(const gen & args){  return symbolic(at_append,args);  }
  gen _append(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( args.type!=_VECT || !args._VECTptr->size() )
      return gensizeerr(contextptr);
    const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (itend-it==2 && it->type==_STRNG && (it+1)->type==_STRNG)
      return string2gen(*it->_STRNGptr+*(it+1)->_STRNGptr,false);
    if (it->type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v(*it->_VECTptr);
    int subtype=it->subtype;
    ++it;
    for (;it!=itend;++it)
      v.push_back(*it);
    return gen(v,subtype);
  }
  static const char _append_s []="append";
  static define_unary_function_eval (__append,&_append,_append_s);
  define_unary_function_ptr5( at_append ,alias_at_append,&__append,0,true);

  // static gen symb_prepend(const gen & args){  return symbolic(at_prepend,args); }
  gen _prepend(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (!args._VECTptr->size()) || (args._VECTptr->front().type!=_VECT) )
      return gensizeerr(contextptr);
    gen debut=args._VECTptr->front();
    return gen(mergevecteur(cdr_VECT(*args._VECTptr),*debut._VECTptr),debut.subtype);
  }
  static const char _prepend_s []="prepend";
  static define_unary_function_eval (__prepend,&_prepend,_prepend_s);
  define_unary_function_ptr5( at_prepend ,alias_at_prepend,&__prepend,0,true);

  gen _contains(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->front().type!=_VECT) )
      return gensizeerr(contextptr);
    return equalposcomp(*args._VECTptr->front()._VECTptr,args._VECTptr->back());
  }
  static const char _contains_s []="contains";
  static define_unary_function_eval (__contains,&_contains,_contains_s);
  define_unary_function_ptr5( at_contains ,alias_at_contains,&__contains,0,true);

  // check if a set A is included in a set B
  gen _is_included(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->front().type!=_VECT) || (args._VECTptr->back().type!=_VECT) )
      return gensizeerr(contextptr);
    vecteur a(*args._VECTptr->front()._VECTptr);
    vecteur b(*args._VECTptr->back()._VECTptr);
    sort(b.begin(),b.end(),islesscomplexthanf);
    for (unsigned i=0;i<a.size();++i){
      if (!binary_search(b.begin(),b.end(),a[i],islesscomplexthanf))
	return 0;
    }
    return 1;
  }
  static const char _is_included_s []="is_included";
  static define_unary_function_eval (__is_included,&_is_included,_is_included_s);
  define_unary_function_ptr5( at_is_included ,alias_at_is_included,&__is_included,0,true);

  static gen symb_select(const gen & args){
    return symbolic(at_select,args);
  }
  static gen symb_remove(const gen & args){
    return symbolic(at_remove,args);
  }
  static gen select_remove(const gen & args,bool selecting,const context * contextptr){
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2)){
      if (selecting)
	return symb_select(args);
      else
	return symb_remove(args);
    }
    gen v((*(args._VECTptr))[1]);
    int subtype;
    unary_function_ptr * fn=0;
    if (v.type==_SYMB){
      if (v._SYMBptr->feuille.type==_VECT)
	v=v._SYMBptr->feuille;
      else
	v=makevecteur(v._SYMBptr->feuille);
      subtype=-1;
      fn=&v._SYMBptr->sommet;
    }
    else
      subtype=v.subtype;
    if ( (v.type!=_VECT) && (v.type!=_SYMB)){
      if (selecting)
	return symb_select(args);
      else
	return symb_remove(args);
    }
    gen f(args._VECTptr->front());
    bool prog=f.is_symb_of_sommet(at_program);
    vecteur otherargs(args._VECTptr->begin()+1,args._VECTptr->end());
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    if (otherargs.size()==1){
      for (;it!=itend;++it){
	if (prog){
	  if (is_zero(f(*it,contextptr))!=selecting)
	    res.push_back(*it);
	}
	else {
	  if ((*it==f)==selecting )
	    res.push_back(*it);
	}
      }
    }
    else {
      for (;it!=itend;++it){
	otherargs.front()=*it;
	if (is_zero(f(otherargs,contextptr))!=selecting)
	  res.push_back(*it);
      }
    }
    if (subtype<0)
      return symbolic(*fn,res);
    else
      return gen(res,subtype);
  }
  gen _select(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return select_remove(args,1,contextptr);
  }
  static const char _select_s []="select";
  static define_unary_function_eval (__select,&_select,_select_s);
  define_unary_function_ptr5( at_select ,alias_at_select,&__select,0,true);

  gen _remove(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return select_remove(args,0,contextptr);
  }
  static const char _remove_s []="remove";
  static define_unary_function_eval (__remove,&_remove,_remove_s);
  define_unary_function_ptr5( at_remove ,alias_at_remove,&__remove,0,true);

  static string printnostring(const gen & g,GIAC_CONTEXT){
    if (g.type==_STRNG)
      return *g._STRNGptr;
    else
      return g.print(contextptr);
  }
  static gen symb_concat(const gen & args){
    return symbolic(at_concat,args);
  }
  gen concat(const gen & g,bool glue_lines,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return symb_concat(g);
    vecteur & v=*g._VECTptr;
    if (v.size()!=2){
      if (g.subtype==_SEQ__VECT)
	return g;
      return symb_concat(g);
    }
    gen v0=v[0],v1=v[1];
    if (v0.type==_VECT && v1.type==_VECT){
      if (!glue_lines && v1.subtype!=_SEQ__VECT && ckmatrix(v0) && ckmatrix(v1) && v0._VECTptr->size()==v1._VECTptr->size() )
	return gen(mtran(mergevecteur(mtran(*v0._VECTptr),mtran(*v1._VECTptr))));
      else
	return gen(mergevecteur(*v0._VECTptr,*v1._VECTptr),v0.subtype);
    }
    if (v0.type==_VECT)
      return gen(mergevecteur(*v0._VECTptr,vecteur(1,v1)),v0.subtype);
    if (v1.type==_VECT)
      return gen(mergevecteur(vecteur(1,v0),*v1._VECTptr),v1.subtype);
    if ( (v0.type==_STRNG) || (v1.type==_STRNG) )
      return string2gen(printnostring(v0,contextptr) + printnostring(v1,contextptr),false);
    return 0;
  }
  gen _concat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return concat(args,false,contextptr);
  }
  static const char _concat_s []="concat";
  static define_unary_function_eval (__concat,&_concat,_concat_s);
  define_unary_function_ptr5( at_concat ,alias_at_concat,&__concat,0,true);

  static gen symb_option(const gen & args){
    return symbolic(at_option,args);
  }
  gen _option(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symb_option(args);
  }
  static const char _option_s []="option";
  static define_unary_function_eval (__option,&_option,_option_s);
  define_unary_function_ptr( at_option ,alias_at_option ,&__option);

  static string printascase(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) || (feuille._VECTptr->back().type!=_VECT))
      return sommetstr+('('+feuille.print(contextptr)+')');
    string res("switch (");
    res += feuille._VECTptr->front().print(contextptr);
    res += "){";
    debug_ptr(contextptr)->indent_spaces +=2;
    const_iterateur it=feuille._VECTptr->back()._VECTptr->begin(),itend=feuille._VECTptr->back()._VECTptr->end();
    for (;it!=itend;++it){
      ++it;
      if (it==itend){
	res += indent(contextptr)+"default:";
	--it;
	debug_ptr(contextptr)->indent_spaces += 2;
	res += indent(contextptr)+it->print(contextptr);
	debug_ptr(contextptr)->indent_spaces -= 2;
	break;
      }
      res += indent(contextptr)+"case "+(it-1)->print(contextptr)+":";
      debug_ptr(contextptr)->indent_spaces += 2;
      res += indent(contextptr)+it->print(contextptr);
      debug_ptr(contextptr)->indent_spaces -=2;
    }
    debug_ptr(contextptr)->indent_spaces -=2;
    res+=indent(contextptr)+"}";
    return res;
  }
  gen symb_case(const gen & args){
    return symbolic(at_case,args);
  }
  gen symb_case(const gen & a,const gen & b){
    return symbolic(at_case,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _case(const gen & args,GIAC_CONTEXT){ // FIXME DEBUGGER
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->back().type!=_VECT) )
      return symb_case(args);
    gen expr=args._VECTptr->front().eval(eval_level(contextptr),contextptr),res=undef,oldres,newres;
    const_iterateur it=args._VECTptr->back()._VECTptr->begin(),itend=args._VECTptr->back()._VECTptr->end();
    for (;it!=itend;){
      if (it+1==itend){
	res=it->eval(eval_level(contextptr),contextptr);
	break;
      }
      if (expr==it->eval(eval_level(contextptr),contextptr)){
	++it;
	oldres=res;
	res=it->eval(eval_level(contextptr),contextptr);
	if (res==symbolic(at_break,zero)){
	  res=oldres;
	  break;
	}
	if (res.is_symb_of_sommet(at_return))
	  break;
      }
      else
	++it;
      if (it!=itend)
	++it;
    }
    return res;
  }
  static const char _case_s []="case";
  static define_unary_function_eval2_index (123,__case,&_case,_case_s,&printascase);
  define_unary_function_ptr5( at_case ,alias_at_case,&__case,_QUOTE_ARGUMENTS,0);

  static gen symb_rand(const gen & args){
    return symbolic(at_rand,args);
  }
  static gen rand_integer_interval(const gen & x1,const gen & x2,GIAC_CONTEXT){
    static gen rand_max_plus_one=gen(rand_max2)+1;
    if (is_strictly_positive(x1-x2,contextptr))
      return rand_integer_interval(x2,x1,contextptr);
    int n=(x2-x1).bindigits()/gen(rand_max2).bindigits()+1;
    // Make n random numbers
    gen res=zero;
    for (int i=0;i<n;++i)
      res=rand_max_plus_one*res+giac_rand(contextptr);
    // Now res is in [0,(RAND_MAX+1)^n-1]
    // Rescale in x1..x2
    return x1+_iquo(makevecteur(res*(x2-x1),pow(rand_max_plus_one,n)),contextptr);
  }
  gen rand_interval(const vecteur & v,bool entier,GIAC_CONTEXT){
    static gen rand_max_plus_one=gen(rand_max2)+1;
    gen x1=v.front(),x2=v.back();
    if (x1==x2)
      return x1;
    if ((entier || xcas_mode(contextptr)==1) && is_integer(x1) && is_integer(x2) )
      return rand_integer_interval(x1,x2,contextptr);
#ifdef HAVE_LIBMPFR
    if (x1.type==_REAL && x2.type==_REAL){
      int n=mpfr_get_prec(x1._REALptr->inf);
      int nr=int(n*std::log(2.0)/std::log(rand_max2+1.0));
      gen xr=0;
      for (int i=0;i<=nr;++i){
	xr=xr*rand_max_plus_one+giac_rand(contextptr);
      }
      return x1+((x2-x1)*xr)/pow(rand_max_plus_one,nr+1);
    }
#endif
    gen x=evalf_double(x1,1,contextptr),y=evalf_double(x2,1,contextptr);
    if ( (x.type==_DOUBLE_) && (y.type==_DOUBLE_) ){
      double xd=x._DOUBLE_val,yd=y._DOUBLE_val;
      double xr= (giac_rand(contextptr)/evalf_double(rand_max_plus_one,1,contextptr)._DOUBLE_val)*(yd-xd)+xd;
      return xr;
    }
    return symb_rand(gen(v,_SEQ__VECT));
  }

  static gen rand_n_in_list(int n,const vecteur & v,GIAC_CONTEXT){
    n=absint(n);
    if (signed(v.size())<n)
      return gendimerr(contextptr);
    // would be faster with randperm
    vecteur w(v);
    vecteur res;
    for (int i=0;i<n;++i){
      int tmp=int((double(giac_rand(contextptr))*w.size())/rand_max2);
      res.push_back(w[tmp]);
      w.erase(w.begin()+tmp);
    }
    return res;
  }
  gen _rand(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_INT_){
      if (args.val<0)
	return -(xcas_mode(contextptr)==3)+int(args.val*(giac_rand(contextptr)/(rand_max2+1.0)));
      else
	return (xcas_mode(contextptr)==3 || abs_calc_mode(contextptr)==38)+int(args.val*(giac_rand(contextptr)/(rand_max2+1.0)));
    }
    if (args.type==_ZINT)
      return rand_integer_interval(zero,args,contextptr);
    if (args.type==_USER)
      return args._USERptr->rand(contextptr);
    if (args.type==_VECT){ 
      if (args._VECTptr->empty())
	return giac_rand(contextptr);
      vecteur & v=*args._VECTptr;
      int s=v.size();
      if (s==2){ 
	if (v.front().type==_INT_ && v.back().type==_VECT){ // rand(n,list) choose n in list
	  return rand_n_in_list(v.front().val,*v.back()._VECTptr,contextptr);
	}
	if ( (v.back().type==_SYMB) && (v.back()._SYMBptr->sommet==at_interval) ){
	  // arg1=loi, arg2=intervalle
	}
	return rand_interval(v,args.subtype==0,contextptr);
      }
      if (s==3 && v[0].type==_INT_ && v[1].type==_INT_ && v[2].type==_INT_){ 
	// 3 integers expected, rand(n,min,max) choose n in min..max
	int n=v[0].val;
	int m=v[1].val;
	int M=v[2].val;
	if (m>M){ int tmp=m; m=M; M=tmp; }
	vecteur v;
	for (int i=m;i<=M;++i) v.push_back(i);
	return rand_n_in_list(n,v,contextptr);
      }
    }
    if ( (args.type==_SYMB) && (args._SYMBptr->sommet==at_interval) ){
      vecteur & v=*args._SYMBptr->feuille._VECTptr;
      return symb_program(vecteur(0),vecteur(0),symb_rand(gen(v,_SEQ__VECT)),contextptr);
      // return rand_interval(v);
    }
    return symb_rand(args);
  }
  static const char _rand_s []="rand";
  static define_unary_function_eval (__rand,&_rand,_rand_s);
  define_unary_function_ptr5( at_rand ,alias_at_rand,&__rand,0,true);

  gen _srand(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_INT_){
      int n=args.val;
#ifdef GIAC_HAS_STO_38
      n = (1000000000*ulonglong(n))% 2147483647;
#endif
      srand(n);
      rand_seed(n,contextptr);
      return args;
    }
    else {
#if defined RTOS_THREADX || defined BESTA_OS
      int t=AspenGetNow();
#else
      int t=int(time(NULL));
#endif
      t = (1000000000*ulonglong(t))% 2147483647;
#ifdef VISUALC
      // srand48(t);
#endif
      rand_seed(t,contextptr);
      srand(t);
      return t;
    }
  }
  static const char _srand_s []="srand";
  static define_unary_function_eval (__srand,&_srand,_srand_s);
  define_unary_function_ptr5( at_srand ,alias_at_srand ,&__srand,0,T_RETURN);

  static gen symb_char(const gen & args){
    return symbolic(at_char,args);
  }
  gen _char(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    string s;
    if (args.type==_INT_){
      s += args.val ;
    }
    else {
      if (args.type==_VECT){
	const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
	for (;it!=itend;++it){
	  s += it->val;
	}
      }
      else return symb_char(args);
    }
    gen tmp=string2gen(s,false);
    return tmp;
  }
  static const char _char_s []="char";
  static define_unary_function_eval (__char,&_char,_char_s);
  define_unary_function_ptr5( at_char ,alias_at_char,&__char,0,true);

  static gen symb_asc(const gen & args){
    return symbolic(at_asc,args);
  }
  gen _asc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_STRNG){
      int l=args._STRNGptr->size();
      vecteur v(l);
      for (int i=0;i<l;++i)
	v[i]=int( (unsigned char) ((*args._STRNGptr)[i]));
      return v;
    }
    if (args.type==_VECT){
      if ( (args._VECTptr->size()!=2) ||(args._VECTptr->front().type!=_STRNG) || (args._VECTptr->back().type!=_INT_) )
	return gensizeerr(gettext("asc"));
      return int( (unsigned char) (*args._VECTptr->front()._STRNGptr)[args._VECTptr->back().val]);
    }
    else return symb_asc(args);
  }

  static const char _asc_s []="asc";
  static define_unary_function_eval (__asc,&_asc,_asc_s);
  define_unary_function_ptr5( at_asc ,alias_at_asc,&__asc,0,true);

  static gen symb_map(const gen & args){
    return symbolic(at_map,args);
  }
  gen _map(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_map(args);
    vecteur v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentoofewargs("");
    gen objet=v.front();
    gen to_map=v[1];
    // FIXME: should have maple_map and mupad_map functions
    if (xcas_mode(contextptr)==1){
      objet=v[1];
      to_map=v.front();
    }
    bool matrix = ckmatrix(objet) && s>2;
    if (matrix){
      matrix=false;
      for (int i=2;i<s;++i){
	if (v[i]==at_matrix){
	  v.erase(v.begin()+i);
	  --s;
	  matrix=true;
	  break;
	}
      }
    }
    if (to_map.type==_VECT)
      return gensizeerr(contextptr);
    if (v.size()==2){
      if (objet.type==_SYMB){
	gen & f=objet._SYMBptr->feuille;
	gen tmp=_map(makevecteur(f,to_map),contextptr);
	if (f.type==_VECT && tmp.type==_VECT)
	  tmp.subtype=f.subtype;
	if (objet._SYMBptr->sommet==at_equal || objet._SYMBptr->sommet==at_same)
	  return symbolic(at_equal,tmp);
	return objet._SYMBptr->sommet(tmp,contextptr);
      }
      // if (to_map.type==_FUNC) return apply(objet,*to_map._FUNCptr);
      if (objet.type==_POLY){
	int dim=objet._POLYptr->dim;
	polynome res(dim);
	vector< monomial<gen> >::const_iterator it=objet._POLYptr->coord.begin(),itend=objet._POLYptr->coord.end();
	res.coord.reserve(itend-it);
	vecteur argv(dim+1);
	for (;it!=itend;++it){
	  argv[0]=it->value;
	  index_t::const_iterator i=it->index.begin();
	  for (int j=0;j<dim;++j,++i)
	    argv[j+1]=*i;
	  gen g=to_map(gen(argv,_SEQ__VECT),contextptr);
	  if (!is_zero(g))
	    res.coord.push_back(monomial<gen>(g,it->index));
	}
	return res;
      }
      if (objet.type!=_VECT)
	return to_map(objet,contextptr);
      const_iterateur it=objet._VECTptr->begin(),itend=objet._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it){
	if (matrix && it->type==_VECT){
	  const vecteur & tmp = *it->_VECTptr;
	  const_iterateur jt=tmp.begin(),jtend=tmp.end();
	  vecteur tmpres;
	  tmpres.reserve(jtend-jt);
	  for (;jt!=jtend;++jt){
	    tmpres.push_back(to_map(*jt,contextptr));
	  }
	  res.push_back(tmpres);
	}
	else
	  res.push_back(to_map(*it,contextptr));
      }
      return res;
    }
    if (objet.type==_POLY){
      int dim=objet._POLYptr->dim;
      vecteur opt(v.begin()+2,v.end());
      opt=mergevecteur(vecteur(dim+1),opt);
      polynome res(dim);
      vector< monomial<gen> >::const_iterator it=objet._POLYptr->coord.begin(),itend=objet._POLYptr->coord.end();
      res.coord.reserve(itend-it);
      for (;it!=itend;++it){
	opt[0]=it->value;
	index_t::const_iterator i=it->index.begin();
	for (int j=0;j<dim;++j,++i)
	  opt[j+1]=*i;
	gen g=to_map(gen(opt,_SEQ__VECT),contextptr);
	if (!is_zero(g))
	  res.coord.push_back(monomial<gen>(g,it->index));
      }
      return res;
    }
    vecteur opt(v.begin()+1,v.end());
    opt[0]=objet;
    if (objet.type!=_VECT)
      return to_map(opt,contextptr);
    const_iterateur it=objet._VECTptr->begin(),itend=objet._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      if (matrix && it->type==_VECT){
	const vecteur & tmp = *it->_VECTptr;
	const_iterateur jt=tmp.begin(),jtend=tmp.end();
	vecteur tmpres;
	tmpres.reserve(jtend-jt);
	for (;jt!=jtend;++jt){
	  opt[0]=*jt;
	  tmpres.push_back(to_map(gen(opt,_SEQ__VECT),contextptr));
	}
	res.push_back(tmpres);
      }
      else {
	opt[0]=*it;
	res.push_back(to_map(gen(opt,_SEQ__VECT),contextptr));
      }
    }
    return res;
  }
  static const char _map_s []="map";
  static define_unary_function_eval (__map,&_map,_map_s);
  define_unary_function_ptr5( at_map ,alias_at_map,&__map,0,true);
  
  static gen symb_apply(const gen & args){
    return symbolic(at_apply,args);
  }
  gen _apply(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_apply(args);
    if (args._VECTptr->empty())
      return gensizeerr(gettext("apply"));
    vecteur v=*args._VECTptr;
    gen to_apply=v.front();
    int n=to_apply.subtype;
    int n2=v.size();
    for (int i=2;i<n2;++i){
      if (v[i]==at_matrix){
	swapgen(v[0],v[1]);
	return _map(gen(v,args.subtype),contextptr);
      }
    }
    if (to_apply.type!=_FUNC)
      n=n2-1;
    if (n && (n2==n+1) ){
      vecteur res;
      for (int i=0;;++i){
	vecteur tmp;
	bool finished=true;
	for (int j=1;j<=n;++j){
	  gen & g=v[j];
	  if (g.type!=_VECT)
	    tmp.push_back(g);
	  else {
	    if (signed(g._VECTptr->size())>i){
	      finished=false;
	      tmp.push_back((*g._VECTptr)[i]);
	    }
	    else
	      tmp.push_back(zero);
	  }
	}
	if (finished)
	  break;
	if (n==1)
	  res.push_back(to_apply(tmp.front(),contextptr));
	else
	  res.push_back(to_apply(tmp,contextptr));
      }
      return res;
    }
    else
      return gensizeerr(contextptr);
    return 0;
  }
  static const char _apply_s []="apply";
  static define_unary_function_eval (__apply,&_apply,_apply_s);
  define_unary_function_ptr5( at_apply ,alias_at_apply,&__apply,0,true);
  
  // static gen symb_makelist(const gen & args){  return symbolic(at_makelist,args);  }
  gen _makelist(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr();
    vecteur v(*args._VECTptr);
    int s=v.size();
    if (s<2)
      return gensizeerr(contextptr);
    gen f(v[0]),debut,fin,step(1);
    if (v[1].is_symb_of_sommet(at_interval)){
      debut=v[1]._SYMBptr->feuille._VECTptr->front();
      fin=v[1]._SYMBptr->feuille._VECTptr->back();
      if (s>2)
	step=v[2];
    }
    else {
      if (s<3)
	return gensizeerr(contextptr);
      debut=v[1];
      fin=v[2];
      if (s>3)
	step=v[3];
    }
    if (is_zero(step))
      return gensizeerr(gettext("Invalid null step"));
    vecteur w;
    if (is_greater(fin,debut,contextptr)){
      step=abs(step,contextptr);
      for (gen i=debut;is_greater(fin,i,contextptr);i=i+step)
	w.push_back(f(i,contextptr));
    }
    else {
      step=-abs(step,contextptr);
      for (gen i=debut;is_greater(i,fin,contextptr);i=i+step)
	w.push_back(f(i,contextptr));
    }
    return w;
  }
  static const char _makelist_s []="makelist";
  static define_unary_function_eval (__makelist,&_makelist,_makelist_s);
  define_unary_function_ptr5( at_makelist ,alias_at_makelist,&__makelist,0,true);

  static gen symb_interval(const gen & args){
    return symbolic(at_interval,args);
  }
  gen symb_interval(const gen & a,const gen & b){
    return symbolic(at_interval,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _interval(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symb_interval(args);
  }
  static const char _interval_s []=" .. ";
  static define_unary_function_eval4_index (56,__interval,&_interval,_interval_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_interval ,alias_at_interval ,&__interval);
  
  static string printascomment(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_STRNG)
      return sommetstr+('('+feuille.print(contextptr)+')');
    string chaine=*feuille._STRNGptr;
    int s=chaine.size();
    if ( (xcas_mode(contextptr)==1) || (xcas_mode(contextptr)==1+_DECALAGE)){
        string res("# ");
        for (int i=0;i<s;++i){
            if ((i==s-1)||(chaine[i]!='\n'))
                res +=chaine[i];
            else
                res += indent(contextptr)+"# ";
        }
        return res;
    }
    int l=chaine.find_first_of('\n');
    if ((l<0)|| (l>=s))
        return "//"+chaine + indent(contextptr);
    return "/*"+chaine+"*/";
  }
  static gen symb_comment(const gen & args){
    return symbolic(at_comment,args);
  }
  gen _comment(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symb_comment(args);
  }
  static const char _comment_s []="comment";
  static define_unary_function_eval2 (__comment,&_comment,_comment_s,&printascomment);
  define_unary_function_ptr5( at_comment ,alias_at_comment ,&__comment,0,true);

  // static gen symb_throw(const gen & args){  return symbolic(at_throw,args);  }
  gen _throw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return gensizeerr(args.print(contextptr));
  }
  static const char _throw_s []="throw";
  static define_unary_function_eval (__throw,&_throw,_throw_s);
  define_unary_function_ptr5( at_throw ,alias_at_throw ,&__throw,0,T_RETURN);

  gen symb_union(const gen & args){
    return symbolic(at_union,args);
  }
  gen _union(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v =*args._VECTptr;
    if (v.empty())
      return args;
    if (v.size()==1 && v.front().type==_VECT)
      return gen(v,_SET__VECT).eval(1,contextptr);
    if (v.size()!=2)
      return gensizeerr(contextptr);
    gen a=v.front(),b=v.back();
    if ( (a.type!=_VECT) || (b.type!=_VECT))
      return gensizeerr(gettext("Union"));
    return gen(mergevecteur(*a._VECTptr,*b._VECTptr),_SET__VECT).eval(1,contextptr);
  }
  static const char _union_s []=" union ";
  static define_unary_function_eval4_index (58,__union,&_union,_union_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_union ,alias_at_union ,&__union);

  gen symb_intersect(const gen & args){
    return symbolic(at_intersect,args);
  }
  gen _intersect(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ((args.type!=_VECT) || (args._VECTptr->size()!=2))
      return gensizeerr();
    gen a=args._VECTptr->front(),b=args._VECTptr->back();
    if ( a.type==_VECT && b.type==_VECT){
      vecteur v;
      const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
      for (;it!=itend;++it){
	if (equalposcomp(*b._VECTptr,*it))
	  v.push_back(*it);
      }
      return gen(v,_SET__VECT);
    }
    return gensizeerr(contextptr);
  }
  static const char _intersect_s []=" intersect ";
  static define_unary_function_eval4_index (62,__intersect,&_intersect,_intersect_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_intersect ,alias_at_intersect ,&__intersect);

  gen symb_minus(const gen & args){
    return symbolic(at_minus,args);
  }
  gen _minus(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ((args.type!=_VECT) || (args._VECTptr->size()!=2))
      return symb_minus(args);
    gen a=args._VECTptr->front(),b=args._VECTptr->back();
    if ( (a.type!=_VECT) || (b.type!=_VECT))
      return gensizeerr(gettext("Minus"));
    vecteur v;
    const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
    for (;it!=itend;++it){
      if (!equalposcomp(*b._VECTptr,*it))
	v.push_back(*it);
    }
    return gen(v,_SET__VECT);
  }
  static const char _minus_s []=" minus ";
  static define_unary_function_eval4_index (60,__minus,&_minus,_minus_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_minus ,alias_at_minus ,&__minus);

  static string printasdollar(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return sommetstr+feuille.print(contextptr);
    vecteur & v=*feuille._VECTptr;
    int s=v.size();
    if (s==2)
      return printsommetasoperator(feuille,sommetstr,contextptr);
    if (s==3)
      return v[0].print(contextptr)+sommetstr+v[1].print(contextptr)+" in "+v[2].print(contextptr);
    return "error";
  }
  gen symb_dollar(const gen & args){
    return symbolic(at_dollar,args);
  }
  gen _dollar(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    vecteur vargs;
    if (args.type!=_VECT){
      identificateur tmp(" _t");
      vargs=makevecteur(tmp,symbolic(at_equal,gen(makevecteur(tmp,args),_SEQ__VECT)));
    }
    else
      vargs=*args._VECTptr;
    int s=vargs.size();
    if (s<2)
      return symb_dollar(args);
    gen a=vargs.front(),b=vargs[1],b1=eval(b,eval_level(contextptr),contextptr);
    if (b1.type==_INT_ && b1.val>=0)
      return gen(vecteur(b1.val,eval(a,eval_level(contextptr),contextptr)),_SEQ__VECT);
    gen var,intervalle,step=1;
    if ( (b.type==_SYMB) && (b._SYMBptr->sommet==at_equal || b._SYMBptr->sommet==at_same ) ){
      var=b._SYMBptr->feuille._VECTptr->front();
      if (var.type!=_IDNT)
	return gensizeerr(contextptr);
      /* Commented example seq(irem(g&^((p-1)/(Div[i])),p),i=(1 .. 2))
	 int status=*var._IDNTptr->quoted;
	 *var._IDNTptr->quoted=1;
	 a=eval(a,contextptr);
	 *var._IDNTptr->quoted=status;      
      */
      intervalle=eval(b._SYMBptr->feuille._VECTptr->back(),eval_level(contextptr),contextptr);
      if (s>=3)
	step=vargs[2];
    }
    else {
      if (s>=3){
	var=vargs[1];
	intervalle=eval(vargs[2],eval_level(contextptr),contextptr);
      }
      if (s>=4)
	step=vargs[3];
    }
    if (intervalle.type==_VECT){
      const_iterateur it=intervalle._VECTptr->begin(),itend=intervalle._VECTptr->end();
      vecteur res;
      for (;it!=itend;++it)
	res.push_back(eval(quotesubst(a,var,*it,contextptr),eval_level(contextptr),contextptr));
      return gen(res,_SEQ__VECT);
      // return gen(res,intervalle.subtype);
    }
    if ( (intervalle.type==_SYMB) && (intervalle._SYMBptr->sommet==at_interval)){
      gen c=intervalle._SYMBptr->feuille._VECTptr->front(),d=intervalle._SYMBptr->feuille._VECTptr->back();
      gen debut=c,fin=d;
      bool reverse=ck_is_greater(debut,fin,contextptr);
      step=abs(step,contextptr);
      step=eval(reverse?-step:step,eval_level(contextptr),contextptr);
      vecteur res;
      for (;;debut+=step){
	if (ck_is_strictly_greater(reverse?fin:debut,reverse?debut:fin,contextptr))
	  break;
	res.push_back(eval(quotesubst(a,var,debut,contextptr),eval_level(contextptr),contextptr));
	if (debut==fin)
	  break;
      }
      return gen(res,_SEQ__VECT);
    }
    return symb_dollar(args);    
  }
  static const char _dollar_s []="$";
  string texprintasdollar(const gen & g,const char * s,GIAC_CONTEXT){
    if ( (g.type==_VECT) && (g._VECTptr->size()==2))
      return gen2tex(g._VECTptr->front(),contextptr)+"\\$"+gen2tex(g._VECTptr->back(),contextptr);
    return "\\$ "+g.print(contextptr);
  }
  static define_unary_function_eval4_index (125,__dollar,&_dollar,_dollar_s,&printasdollar,&texprintasdollar);
  define_unary_function_ptr5( at_dollar ,alias_at_dollar,&__dollar,_QUOTE_ARGUMENTS,0);

  static gen symb_makemat(const gen & args){
    return symbolic(at_makemat,args);
  }
  gen _makemat(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_makemat(args);
    int s=args._VECTptr->size();
    if ( (s!=3) && (s!=2) )
      return symb_makemat(args);
    gen fonction,intervalle1,intervalle2;
    if (s==3){
      fonction=args._VECTptr->front();
      intervalle1=(*(args._VECTptr))[1];
      intervalle2=args._VECTptr->back();
    }
    else {
      intervalle1=args._VECTptr->front();
      intervalle2=args._VECTptr->back();
    }
    if (intervalle1.type==_INT_)
      intervalle1=symb_interval(makevecteur(zero,intervalle1-1));
    if (intervalle2.type==_INT_)
      intervalle2=symb_interval(makevecteur(zero,intervalle2-1));
    if ( (intervalle1.type!=_SYMB) || (intervalle1._SYMBptr->sommet!=at_interval) ||(intervalle2.type!=_SYMB) || (intervalle2._SYMBptr->sommet!=at_interval))
      return gensizeerr(gettext("makemat"));
    intervalle1=intervalle1._SYMBptr->feuille;
    intervalle2=intervalle2._SYMBptr->feuille;
    if ((intervalle1.type!=_VECT) || (intervalle1._VECTptr->size()!=2) || (intervalle2.type!=_VECT) || (intervalle2._VECTptr->size()!=2))
      return gensizeerr(gettext("interval"));
    gen debut_i=intervalle1._VECTptr->front(),fin_i=intervalle1._VECTptr->back();
    gen debut_j=intervalle2._VECTptr->front(),fin_j=intervalle2._VECTptr->back();
    if ( (debut_i.type!=_INT_) || (fin_i.type!=_INT_) || (debut_j.type!=_INT_) || (fin_j.type!=_INT_) )
      return gensizeerr(gettext("Boundaries not integer"));
    int di=debut_i.val,fi=fin_i.val,dj=debut_j.val,fj=fin_j.val;
    int stepi=1,stepj=1;
    if (di>fi)
      stepi=-1;
    if (dj>fj)
      stepj=-1;
    if ((fonction.type!=_SYMB) || (fonction._SYMBptr->sommet!=at_program)){
      int s=(fj-dj+1)*stepj;
      vecteur w(s,fonction);
      int t=(fi-di+1)*stepi;
      vecteur res(t);
      for (int i=0;i<t;++i)
	res[i]=w; // each element of res will be a free line, so that =< works
      return res;
    }
    vecteur v,w,a(2);
    v.reserve((fi-di)*stepi);
    w.reserve((fj-dj)*stepj);
    for (;;di+=stepi){
      a[0]=di;
      w.clear();
      for (int djj=dj;;djj+=stepj){
	a[1]=djj;
	w.push_back(fonction(gen(a,_SEQ__VECT),contextptr));
	if (djj==fj)
	  break;
      }
      v.push_back(w);
      if (di==fi)
	break;
    }
    return v;
  }
  static const char _makemat_s []="makemat";
  static define_unary_function_eval (__makemat,&_makemat,_makemat_s);
  define_unary_function_ptr5( at_makemat ,alias_at_makemat,&__makemat,0,true);

  gen symb_compose(const gen & args){
    return symbolic(at_compose,args);
  }
  gen _compose(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symb_compose(args);
  }
  static const char _compose_s []="@";
  static define_unary_function_eval4 (__compose,&_compose,_compose_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_compose ,alias_at_compose ,&__compose);

  gen _composepow(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_composepow,args);
  }
  static const char _composepow_s []="@@";
  static define_unary_function_eval4 (__composepow,&_composepow,_composepow_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_composepow ,alias_at_composepow ,&__composepow);

  gen symb_args(const gen & args){
    return symbolic(at_args,args);
  }
  gen _args(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen e;
    if (debug_ptr(contextptr)->args_stack.empty())
      e=vecteur(0);
    else
      e=debug_ptr(contextptr)->args_stack.back();
    if ( (args.type==_VECT) && (args._VECTptr->empty()))
      return e;
    else
      return e(args,contextptr);
  }
  static const char _args_s []="args";
  static define_unary_function_eval (__args,&_args,_args_s);
  define_unary_function_ptr( at_args ,alias_at_args ,&__args);
  
  // static gen symb_lname(const gen & args){  return symbolic(at_lname,args);  }
  static void lidnt(const vecteur & v,vecteur & res){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      lidnt(*it,res);
  }

  extern const unary_function_ptr * const  at_int;

  void lidnt(const gen & args,vecteur & res){
    switch (args.type){
    case _IDNT:
      if (!equalposcomp(res,args))
	res.push_back(args);
      break;
    case _SYMB:
      if (args._SYMBptr->sommet==at_program && args._SYMBptr->feuille.type==_VECT && args._SYMBptr->feuille._VECTptr->size()==3){
	lidnt(args._SYMBptr->feuille._VECTptr->front(),res);
	lidnt(args._SYMBptr->feuille._VECTptr->back(),res);
	return;
      }
      if (args._SYMBptr->sommet==at_pnt && args._SYMBptr->feuille.type==_VECT && args._SYMBptr->feuille._VECTptr->size()==3){
	lidnt(args._SYMBptr->feuille._VECTptr->front(),res);
	lidnt((*args._SYMBptr->feuille._VECTptr)[1],res);
	return;
      }
      if ( (args._SYMBptr->sommet==at_integrate || args._SYMBptr->sommet==at_int || args._SYMBptr->sommet==at_sum) && args._SYMBptr->feuille.type==_VECT && args._SYMBptr->feuille._VECTptr->size()==4){
	vecteur & v =*args._SYMBptr->feuille._VECTptr;
	vecteur w(1,v[1]);
	lidnt(v[0],w);
	const_iterateur it=w.begin(),itend=w.end();
	for (++it;it!=itend;++it)
	  lidnt(*it,res);
	lidnt(v[2],res);
	lidnt(v.back(),res);
	return;
      }      
      lidnt(args._SYMBptr->feuille,res);
      break;
    case _VECT:
      lidnt(*args._VECTptr,res);
      break;
    }       
  }
  vecteur lidnt(const gen & args){
    vecteur res;
    lidnt(args,res);
    return res;
  }
  gen _lname(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return lidnt(args);
  }
  static const char _lname_s []="lname";
  static define_unary_function_eval (__lname,&_lname,_lname_s);
  define_unary_function_ptr5( at_lname ,alias_at_lname,&__lname,0,true);

  static gen symb_has(const gen & args){
    return symbolic(at_has,args);
  }
  gen _has(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
      if ( (args.type!=_VECT) || (args._VECTptr->size()!=2))
          return symb_has(args);
      return equalposcomp(*_lname(args._VECTptr->front(),contextptr)._VECTptr,args._VECTptr->back());
  }
  static const char _has_s []="has";
  static define_unary_function_eval (__has,&_has,_has_s);
  define_unary_function_ptr5( at_has ,alias_at_has,&__has,0,true);

  gen _kill(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->empty()){
      if (!contextptr)
	protection_level=0;
      debug_ptr(contextptr)->debug_mode=false;
      debug_ptr(contextptr)->current_instruction_stack.clear();
      debug_ptr(contextptr)->sst_at_stack.clear();
      debug_ptr(contextptr)->args_stack.clear();
      return gensizeerr(gettext("Program killed"));
    }
#ifdef HAVE_LIBPTHREAD
    if (args.type==_VECT)
      return apply(args,_kill,contextptr);
    if (args.type==_POINTER_ && args.subtype==_THREAD_POINTER){
      context * cptr=(context *) args._POINTER_val;
      thread_param * tptr =thread_param_ptr(cptr);
      if (cptr 
#ifndef __MINGW_H
	  && tptr->eval_thread
#endif
	  ){
	gen g=tptr->v[0];
	if (g.type==_VECT && g._VECTptr->size()==2 && g._VECTptr->front().is_symb_of_sommet(at_quote)){
	  pthread_mutex_lock(cptr->globalptr->_mutex_eval_status_ptr);
	  gen tmpsto=sto(undef,g._VECTptr->front()._SYMBptr->feuille,cptr);
	  if (is_undef(tmpsto)) return tmpsto;
	  pthread_mutex_unlock(cptr->globalptr->_mutex_eval_status_ptr);
	}
      }
      kill_thread(true,cptr);
      return 1;
    }
#endif
    return gentypeerr(contextptr);
  }
  static const char _kill_s []="kill";
  static define_unary_function_eval (__kill,&_kill,_kill_s);
  define_unary_function_ptr5( at_kill ,alias_at_kill,&__kill,0,true);

  gen _halt(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (debug_ptr(contextptr)->debug_allowed){
      debug_ptr(contextptr)->debug_mode=true;
      debug_ptr(contextptr)->sst_mode=true;
      return plus_one;
    }
    return zero;
  }
  static const char _halt_s []="halt";
  static define_unary_function_eval_quoted (__halt,&_halt,_halt_s);
  define_unary_function_ptr5( at_halt ,alias_at_halt,&__halt,_QUOTE_ARGUMENTS,true);

  gen _debug(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id && thread_eval_status(contextptr)!=1)
      return args;
    if (debug_ptr(contextptr)->debug_allowed){
      debug_ptr(contextptr)->debug_mode=true;
      debug_ptr(contextptr)->sst_in_mode=true;
      debug_ptr(contextptr)->debug_prog_name=0;
    }
    return args.eval(eval_level(contextptr),contextptr);
  }
  static const char _debug_s []="debug";
  static define_unary_function_eval_quoted (__debug,&_debug,_debug_s);
  define_unary_function_ptr5( at_debug ,alias_at_debug,&__debug,_QUOTE_ARGUMENTS,true);

  gen _sst_in(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id)
      return zero;
    if (debug_ptr(contextptr)->debug_allowed){
      debug_ptr(contextptr)->debug_mode=true;
      debug_ptr(contextptr)->sst_in_mode=true;
      return plus_one;
    }
    return zero;
  }
  static const char _sst_in_s []="sst_in";
  static define_unary_function_eval_quoted (__sst_in,&_sst_in,_sst_in_s);
  define_unary_function_ptr5( at_sst_in ,alias_at_sst_in,&__sst_in,_QUOTE_ARGUMENTS,true);

  gen _sst(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id)
      return args;
    if (debug_ptr(contextptr)->debug_allowed){
      debug_ptr(contextptr)->debug_mode=true;
      debug_ptr(contextptr)->sst_mode=true;
      return plus_one;
    }
    return zero;
  }
  static const char _sst_s []="sst";
  static define_unary_function_eval_quoted (__sst,&_sst,_sst_s);
  define_unary_function_ptr5( at_sst ,alias_at_sst,&__sst,_QUOTE_ARGUMENTS,true);

  gen _cont(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id)
      return args;
    if (debug_ptr(contextptr)->debug_allowed){
      debug_ptr(contextptr)->sst_mode=false;
      return plus_one;
    }
    return zero;
  }
  static const char _cont_s []="cont";
  static define_unary_function_eval_quoted (__cont,&_cont,_cont_s);
  define_unary_function_ptr5( at_cont ,alias_at_cont,&__cont,_QUOTE_ARGUMENTS,true);

  static gen watch(const gen & args,GIAC_CONTEXT){
    if (!equalposcomp(debug_ptr(contextptr)->debug_watch,args))
      debug_ptr(contextptr)->debug_watch.push_back(args);
    return args;
  }
  gen _watch(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id && thread_eval_status(contextptr)!=1 )
      return args;
    if (args.type==_VECT && args._VECTptr->empty() && debug_ptr(contextptr)->debug_localvars)
      apply( *debug_ptr(contextptr)->debug_localvars,contextptr,watch);
    else
      apply(args,contextptr,watch);
    return debug_ptr(contextptr)->debug_watch;
  }
  static const char _watch_s []="watch";
  static define_unary_function_eval_quoted (__watch,&_watch,_watch_s);
  define_unary_function_ptr5( at_watch ,alias_at_watch,&__watch,_QUOTE_ARGUMENTS,true);

  static gen rmwatch(const gen & args,GIAC_CONTEXT){
    int pos;
    if (args.type==_INT_){
      pos=args.val+1;
      if (pos>signed(debug_ptr(contextptr)->debug_watch.size()))
	return debug_ptr(contextptr)->debug_watch;
    }
    else 
      pos=equalposcomp(debug_ptr(contextptr)->debug_watch,args);
    if (pos){
      debug_ptr(contextptr)->debug_watch.erase(debug_ptr(contextptr)->debug_watch.begin()+pos-1,debug_ptr(contextptr)->debug_watch.begin()+pos);
      return debug_ptr(contextptr)->debug_watch;
    }
    return zero;
  }

  gen _rmwatch(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->empty() && debug_ptr(contextptr)->debug_localvars)
      return apply( *debug_ptr(contextptr)->debug_localvars,contextptr,rmwatch);
    else
      return apply(args,contextptr,rmwatch);
  }
  static const char _rmwatch_s []="rmwatch";
  static define_unary_function_eval_quoted (__rmwatch,&_rmwatch,_rmwatch_s);
  define_unary_function_ptr5( at_rmwatch ,alias_at_rmwatch,&__rmwatch,_QUOTE_ARGUMENTS,true);

  gen _breakpoint(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id && thread_eval_status(contextptr)!=1)
      return args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->front().type!=_IDNT) || (args._VECTptr->back().type!=_INT_) )
      return zero;
    if (!equalposcomp(debug_ptr(contextptr)->debug_breakpoint,args)){
      debug_ptr(contextptr)->debug_breakpoint.push_back(args);
      // FIXME should also modify debug_ptr(contextptr)->sst_at_stack if the breakpoint applies
      // to a program != current program
      if (!debug_ptr(contextptr)->args_stack.empty() && debug_ptr(contextptr)->args_stack.back().type==_VECT && debug_ptr(contextptr)->args_stack.back()._VECTptr->front()==args._VECTptr->front())
	debug_ptr(contextptr)->sst_at.push_back(args._VECTptr->back().val);
    }
    return debug_ptr(contextptr)->debug_breakpoint;
  }
  static const char _breakpoint_s []="breakpoint";
  static define_unary_function_eval_quoted (__breakpoint,&_breakpoint,_breakpoint_s);
  define_unary_function_ptr5( at_breakpoint ,alias_at_breakpoint,&__breakpoint,_QUOTE_ARGUMENTS,true);

  gen _rmbreakpoint(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (child_id&& thread_eval_status(contextptr)!=1)
      return args;
    int pos;
    if (args.type==_INT_){
      pos=args.val;
      if (pos<1 || pos>signed(debug_ptr(contextptr)->debug_breakpoint.size())){
	adjust_sst_at(*debug_ptr(contextptr)->debug_prog_name,contextptr);
	return debug_ptr(contextptr)->debug_breakpoint;
      }
    }
    else 
      pos=equalposcomp(debug_ptr(contextptr)->debug_breakpoint,args);
    if (pos){
      debug_ptr(contextptr)->debug_breakpoint.erase(debug_ptr(contextptr)->debug_breakpoint.begin()+pos-1,debug_ptr(contextptr)->debug_breakpoint.begin()+pos);
      adjust_sst_at(*debug_ptr(contextptr)->debug_prog_name,contextptr);
      return debug_ptr(contextptr)->debug_breakpoint;
    }
    return zero;
  }
  static const char _rmbreakpoint_s []="rmbreakpoint";
  static define_unary_function_eval_quoted (__rmbreakpoint,&_rmbreakpoint,_rmbreakpoint_s);
  define_unary_function_ptr5( at_rmbreakpoint ,alias_at_rmbreakpoint,&__rmbreakpoint,_QUOTE_ARGUMENTS,true);

#ifdef GIAC_HAS_STO_38
  void aspen_debug_loop(gen & res,GIAC_CONTEXT);

  void debug_loop(gen &res,GIAC_CONTEXT){
    aspen_debug_loop(res,contextptr);
  }

#else // GIAC_HAS_STO_38

  void debug_loop(gen &res,GIAC_CONTEXT){
    if (!debug_ptr(contextptr)->debug_allowed || (!debug_ptr(contextptr)->sst_mode && !equalposcomp(debug_ptr(contextptr)->sst_at,debug_ptr(contextptr)->current_instruction)) )
      return;
    // Detect thread debugging
    int thread_debug=thread_eval_status(contextptr);
    if (thread_debug>1)
      return;
    if (thread_debug==1){
      // Fill dbgptr->debug_info_ptr and fast_debug_info_ptr 
      // with debugging infos to be displayed
      debug_struct * dbgptr=debug_ptr(contextptr);
      vecteur w; 
      // w[0]=function, args,
      // w[1]=breakpoints
      // w[2] = instruction to eval or program if debugging a prog
      // w[3]= evaluation result
      // w[4]= current instruction number 
      // w[5] = watch vector, w[6] = watch values
      if (!debug_ptr(contextptr)->args_stack.empty()){
	w.push_back(debug_ptr(contextptr)->args_stack.back());
	w.push_back(vector_int_2_vecteur(debug_ptr(contextptr)->sst_at,contextptr));
      }
      else {
	w.push_back(undef);
	w.push_back(undef);
      }
      w.push_back((*debug_ptr(contextptr)->fast_debug_info_ptr));
      w.push_back(res);
      w.push_back(debug_ptr(contextptr)->current_instruction);
      vecteur dw=debug_ptr(contextptr)->debug_watch;
      if (contextptr && dw.empty()){
	// put the last 2 environments
	const context * cur=contextptr;
	sym_tab::const_iterator it=cur->tabptr->begin(),itend=cur->tabptr->end();
	for (;it!=itend;++it){
	  dw.push_back(identificateur(it->first));
	}
	if (cur->previous && cur->previous!=cur->globalcontextptr){
	  cur=cur->previous;
	  sym_tab::const_iterator it=cur->tabptr->begin(),itend=cur->tabptr->end();
	  for (;it!=itend;++it){
	    dw.push_back(identificateur(it->first));
	  }
	}
      }
      w.push_back(dw);
      // evaluate watch with debug_ptr(contextptr)->debug_allowed=false
      debug_ptr(contextptr)->debug_allowed=false;
      iterateur it=dw.begin(),itend=dw.end();
      for (;it!=itend;++it)
	*it=protecteval(*it,1,contextptr);
      w.push_back(dw);
      debug_ptr(contextptr)->debug_allowed=true;
      *dbgptr->debug_info_ptr=w;
      dbgptr->debug_refresh=false;
      // Switch to level 2, waiting for main
      thread_eval_status(2,contextptr);
      for (;;){
	// Wait until status is put back by main to level 1
	usleep(10000);
	if (thread_eval_status(contextptr)==1){
	  // the wait function of the main thread should put in debug_info_ptr
	  // the next instruction, here we check for sst/sst_in/cont/kill
	  if (dbgptr->fast_debug_info_ptr){
	    gen test=*dbgptr->fast_debug_info_ptr;
	    if (test.type==_SYMB)
	      test=test._SYMBptr->sommet;
	    if (test.type==_FUNC){
	      if (test==at_sst){
		dbgptr->sst_in_mode=false;
		dbgptr->sst_mode=true;
		return;
	      }
	      if (test==at_sst_in){
		dbgptr->sst_in_mode=true;
		dbgptr->sst_mode=true;
		return;
	      }
	      if (test==at_cont){
		dbgptr->sst_in_mode=false;
		dbgptr->sst_mode=false;
		return;
	      }
	      if (test==at_kill){
		_kill(0,contextptr);
		return;
	      }
	    } // end type _FUNC
	    // eval
	    w[2] = *dbgptr->fast_debug_info_ptr;
	    w[3] = *dbgptr->fast_debug_info_ptr = protecteval(w[2],1,contextptr);
	    *dbgptr->debug_info_ptr=w;
	    dbgptr->debug_refresh=true;
	  } // end if (*dbgptr->debug_info_ptr)
	  thread_eval_status(2,contextptr); // Back to level 2
	} // end if (thread_eval_status()==1)
      } // end endless for loop
    } // end thread debugging
#if (defined WIN32) || (!defined HAVE_SIGNAL_H_OLD)
    *logptr(contextptr) << gettext("Sorry! Debugging requires a true operating system") << endl;
    *logptr(contextptr) << gettext("Please try xcas on Linux or an Unix") << endl;
    return;
#else // WIN32
    if (child_id)
      return;
    vecteur w; 
    // w[0]=[function + args, breakpoints]
    // w[2]= res of last evaluation, 
    // w[3] = next instruction, w[4]=debug_ptr(contextptr)->current_instruction
    // w[5] = watch vector, w[6] = watch values
    // evaluate watch with debug_ptr(contextptr)->debug_allowed=false
    debug_ptr(contextptr)->debug_allowed=false;
    debug_ptr(contextptr)->debug_allowed=true;
    if (!debug_ptr(contextptr)->args_stack.empty()){
      w.push_back(makevecteur(debug_ptr(contextptr)->args_stack.back(),vector_int_2_vecteur(debug_ptr(contextptr)->sst_at,contextptr)));
    }
    else
      w.push_back(undef);
    w.push_back(undef);
    w.push_back(res);
    w.push_back((*debug_ptr(contextptr)->fast_debug_info_ptr));
    (*debug_ptr(contextptr)->fast_debug_info_ptr)=undef;
    w.push_back(debug_ptr(contextptr)->current_instruction);
    w.push_back(debug_ptr(contextptr)->debug_watch);
    w.push_back(undef);
    bool in_debug_loop=true;
    for (;in_debug_loop;){
#ifndef NO_STDEXCEPT
      try {
#endif
	vecteur tmp=gen2vecteur(debug_ptr(contextptr)->debug_watch);
	iterateur it=tmp.begin(),itend=tmp.end();
	for (;it!=itend;++it)
	  *it=it->eval(1,contextptr);
	w[6]=tmp;
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & error){
	w[6]=string2gen(error.what(),false);
      }
#endif
      ofstream child_out(cas_sortie_name().c_str());
      gen e(symbolic(at_debug,w));
      *logptr(contextptr) << gettext("Archiving ") << e << endl;
      archive(child_out,e,contextptr);
      archive(child_out,zero,contextptr);
      child_out << "Debugging\n" << '¤' ;
      child_out.close();
      kill_and_wait_sigusr2();
      ifstream child_in(cas_entree_name().c_str());
      w[1]= unarchive(child_in,contextptr);
      child_in.close();
      *logptr(contextptr) << gettext("Click reads ") << w[1] << endl;
      if (w[1].type==_SYMB){
	if (w[1]._SYMBptr->sommet==at_sst){
	  debug_ptr(contextptr)->sst_in_mode=false;
	  debug_ptr(contextptr)->sst_mode=true;
	  return;
	}
	if (w[1]._SYMBptr->sommet==at_sst_in){
	  debug_ptr(contextptr)->sst_in_mode=true;
	  debug_ptr(contextptr)->sst_mode=true;
	  return;
	}
	if (w[1]._SYMBptr->sommet==at_cont){
	  debug_ptr(contextptr)->sst_in_mode=false;
	  debug_ptr(contextptr)->sst_mode=false;
	  return;
	}
	if (w[1]._SYMBptr->sommet==at_kill){
	  _kill(0,contextptr);
	}
      }
#ifndef NO_STDEXCEPT
      try {
#endif
	w[2] =w[1].eval(1,contextptr);
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & error ){
	w[2]=string2gen(error.what(),false);
      }
#endif
    }
#endif // WIN32
  }
#endif // GIAC_HAS_STO_38

  static string printasbackquote(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return "`"+feuille.print(contextptr)+"`";
  }
  gen _backquote(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return args;
  }
  static const char _backquote_s []="backquote";
  static define_unary_function_eval2 (__backquote,&_backquote,_backquote_s,&printasbackquote);
  define_unary_function_ptr( at_backquote ,alias_at_backquote ,&__backquote);

  static string printasdouble_deux_points(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    gen a,b;
    check_binary(feuille,a,b);
    string s=b.print(contextptr);
    if (b.type==_SYMB && b._SYMBptr->sommet!=at_of && b._SYMBptr->sommet.ptr()->printsommet)
      s = '('+s+')';
    if (b.type==_FUNC && s.size()>2 && s[0]=='\'' && s[s.size()-1]=='\'')
      s=s.substr(1,s.size()-2);
    return a.print(contextptr)+"::"+s+ " ";
  }
  gen symb_double_deux_points(const gen & args){
    return symbolic(at_double_deux_points,args);
  }
  gen _double_deux_points(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen a,b,c;
    if (!check_binary(args,a,b))
      return a;
    if (sto_38 && abs_calc_mode(contextptr)==38 && a.type==_IDNT){
      gen value;
      if (rcl_38(value,a._IDNTptr->id_name,b.type==_IDNT?b._IDNTptr->id_name:b.print().c_str(),undef,false,contextptr)){
	return value;
      }
    }
#ifndef RTOS_THREADX
#ifndef BESTA_OS
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_lock(&context_list_mutex);
#endif
    if (a.type==_INT_ && a.subtype==0 && a.val>=0 && a.val<(int)context_list().size()){
      context * ptr = context_list()[a.val];
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&context_list_mutex);
#endif
      return eval(b,1,ptr);
    }
    if (context_names){
      map<string,context *>::iterator nt=context_names->find(a.print(contextptr)),ntend=context_names->end();
      if (nt!=ntend){
	context * ptr = nt->second;
#ifdef HAVE_LIBPTHREAD
	pthread_mutex_unlock(&context_list_mutex);
#endif
	return eval(b,1,ptr);
      }
    }
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&context_list_mutex);
#endif
#endif // RTOS
#endif
    c=b;
    if (b.is_symb_of_sommet(at_of))
      c=b._SYMBptr->feuille[0];
    string cs=c.print(contextptr);
    /* // following code not used since qualified names after export 
       // make b a symbolic not just the function name
    int l=cs.size(),j=0;
    for (;j<l-1;++j){
      if (cs[j]==':' && cs[j+1]==':')
	break;
    }
    if (j==l-1)
    */      
    cs=a.print(contextptr)+"::"+cs;
    std::pair<charptr_gen *,charptr_gen *> p= equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(cs.c_str(),0),tri);
    if (p.first!=p.second && p.first!=builtin_lexer_functions_end()){
      c=p.first->second;
      if (b.is_symb_of_sommet(at_of))
	return c(b._SYMBptr->feuille[1],contextptr);
      else
	return c;
    }
    map_charptr_gen::const_iterator it=lexer_functions().find(cs.c_str());
    if (it!=lexer_functions().end()){
      c=it->second;
      if (b.is_symb_of_sommet(at_of))
	return c(b._SYMBptr->feuille[1],contextptr);
      else
	return c;
    }
    if (b.type==_FUNC) // ? should be != _IDNT 
      return b;
    if (b.type==_SYMB)
      return b.eval(eval_level(contextptr),contextptr);
    gen aa=a.eval(1,contextptr);
    if (aa.type==_VECT)
      return find_in_folder(*aa._VECTptr,b);
    return symb_double_deux_points(args);
  }
  static const char _double_deux_points_s []="double_deux_points";
  static define_unary_function_eval2_index(91,__double_deux_points,&_double_deux_points,_double_deux_points_s,&printasdouble_deux_points);
  define_unary_function_ptr5( at_double_deux_points ,alias_at_double_deux_points,&__double_deux_points,_QUOTE_ARGUMENTS,0);

  bool is_binary(const gen & args){
    return (args.type==_VECT) && (args._VECTptr->size()==2) ;
  }

  bool check_binary(const gen & args,gen & a,gen & b){
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) ){
      a=gensizeerr(gettext("check_binary"));
      b=a;
      return false;
    }
    a=args._VECTptr->front();
    b=args._VECTptr->back();
    return true;
  }

  static bool maple2mupad(const gen & args,int in_maple_mode,int out_maple_mode,GIAC_CONTEXT){
    if (is_undef(check_secure()))
      return false;
    gen a,b;
    if (!check_binary(args,a,b))
      return false;
    string as,bs;
    if (a.type==_IDNT)
      as=a._IDNTptr->name();
    if (a.type==_STRNG)
      as=*a._STRNGptr;
    if (b.type==_IDNT)
      bs=b._IDNTptr->name();
    if (b.type==_STRNG)
      bs=*b._STRNGptr;
    int save_maple_mode=xcas_mode(contextptr);
    xcas_mode(contextptr)=in_maple_mode;
    ifstream infile(as.c_str());
    vecteur v;
#ifndef NO_STDEXCEPT
    try {
#endif
      readargs_from_stream(infile,v,contextptr);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error &  ){
      xcas_mode(contextptr)=save_maple_mode;
      return false;
    }
#endif
    xcas_mode(contextptr)=out_maple_mode;
    ofstream outfile(bs.c_str());
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      outfile << *it << endl;
    xcas_mode(contextptr)=save_maple_mode;
    return true;
  }

  gen _maple2mupad(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return maple2mupad(args,1,2,contextptr);
  }
  static const char _maple2mupad_s []="maple2mupad";
  static define_unary_function_eval_quoted (__maple2mupad,&_maple2mupad,_maple2mupad_s);
  define_unary_function_ptr5( at_maple2mupad ,alias_at_maple2mupad,&__maple2mupad,_QUOTE_ARGUMENTS,true);

  gen _maple2xcas(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return maple2mupad(args,1,0,contextptr);
  }
  static const char _maple2xcas_s []="maple2xcas";
  static define_unary_function_eval_quoted (__maple2xcas,&_maple2xcas,_maple2xcas_s);
  define_unary_function_ptr5( at_maple2xcas ,alias_at_maple2xcas,&__maple2xcas,_QUOTE_ARGUMENTS,true);

  gen _mupad2maple(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return maple2mupad(args,2,1,contextptr);
  }
  static const char _mupad2maple_s []="mupad2maple";
  static define_unary_function_eval_quoted (__mupad2maple,&_mupad2maple,_mupad2maple_s);
  define_unary_function_ptr5( at_mupad2maple ,alias_at_mupad2maple,&__mupad2maple,_QUOTE_ARGUMENTS,true);

  gen _mupad2xcas(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return maple2mupad(args,2,0,contextptr);
  }
  static const char _mupad2xcas_s []="mupad2xcas";
  static define_unary_function_eval_quoted (__mupad2xcas,&_mupad2xcas,_mupad2xcas_s);
  define_unary_function_ptr5( at_mupad2xcas ,alias_at_mupad2xcas,&__mupad2xcas,_QUOTE_ARGUMENTS,true);

  static string printasvirgule(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    return feuille._VECTptr->front().print(contextptr)+','+feuille._VECTptr->back().print(contextptr);
  }
  gen _virgule(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args;
    const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (itend-it<2)
      return args;
    gen res=makesuite(*it,*(it+1));
    ++it;
    ++it;
    for (;it!=itend;++it)
      res=makesuite(res,*it);
    return res;
  }
  static const char _virgule_s []="virgule";
  static define_unary_function_eval2 (__virgule,&_virgule,_virgule_s,&printasvirgule);
  define_unary_function_ptr( at_virgule ,alias_at_virgule ,&__virgule);

  gen _pwd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
#ifndef HAVE_NO_CWD
    char * buffer=getcwd(0,0);
    if (buffer){
      string s(buffer);
#ifndef HAVE_LIBGC
      free(buffer);
#endif
      return string2gen(s,false);
    }
#endif
    return gensizeerr(contextptr);
  }
  static const char _pwd_s []="pwd";
  static define_unary_function_eval (__pwd,&_pwd,_pwd_s);
  define_unary_function_ptr5( at_pwd ,alias_at_pwd,&__pwd,0,true);

  gen _cd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (args.type!=_STRNG)
      return gentypeerr(contextptr);
    int res;
    string s(*args._STRNGptr);
    string ss(*_pwd(zero,contextptr)._STRNGptr+'/'),current;
    int l=s.size();
    for (int i=0;i<=l;i++){
      if ( (i==l) || (s[i]=='/') ){
	if (i){
	  if (current==".."){
	    int t=ss.size()-2;
	    for (;t>0;--t){
	      if (ss[t]=='/')
		break;
	    }
	    if (t)
	      ss=ss.substr(0,t+1);
	    else
	      ss="/";
	  } 
	  else { // not ..
	    if (current[0]=='~'){
	      if (current.size()==1){ // uid user directory
		ss = home_directory();
	      }
	      else { // other user directory
		current=current.substr(1,current.size()-1);
#ifndef HAVE_NO_PWD_H
		passwd * p=getpwnam(current.c_str());
		if (!p)
		  return gensizeerr(gettext("No such user ")+current);
		ss = p->pw_dir ;
		ss +='/';
#else
		ss = "/";
#endif
	      }
	    }
	    else
	      ss+=current+"/";
	  } // end .. detection
	}
	else // i==0 / means absolute path
	  ss="/";
	current="";
      } // end / detection
      else {
	if (s[i]>' ')
	  current += s[i];
      }
    } // end for
#ifndef HAVE_NO_CWD
    res=chdir(ss.c_str());
#else
    res=-1;
#endif
    if (res)
      return gensizeerr(contextptr);
    gen g=symbolic(at_cd,_pwd(zero,contextptr));
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id)
      _signal(symb_quote(g),contextptr);
#endif
    // *logptr(contextptr) << g << endl;
    return g;
  }
  static const char _cd_s []="cd";
  static define_unary_function_eval (__cd,&_cd,_cd_s);
  define_unary_function_ptr5( at_cd ,alias_at_cd,&__cd,0,true);

  gen _scientific_format(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return scientific_format(contextptr);
    scientific_format(args.val,contextptr);
    return args;
  }
  static const char _scientific_format_s []="scientific_format";
  static define_unary_function_eval2 (__scientific_format,&_scientific_format,_scientific_format_s,&printasDigits);
  define_unary_function_ptr( at_scientific_format ,alias_at_scientific_format ,&__scientific_format);

  gen _integer_format(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return integer_format(contextptr);
    integer_format(args.val,contextptr);
    return args;
  }
  static const char _integer_format_s []="integer_format";
  static define_unary_function_eval2 (__integer_format,&_integer_format,_integer_format_s,&printasDigits);
  define_unary_function_ptr5( at_integer_format ,alias_at_integer_format,&__integer_format,0,true);

  // 0: xcas, 1: maple, 2: mupad, 3: ti
  gen _xcas_mode(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return xcas_mode(contextptr);
    xcas_mode(contextptr)=args.val;
    return string2gen("Warning: some commands like subs might change arguments order",false);
  }
  static const char _xcas_mode_s []="xcas_mode";
  static define_unary_function_eval (__xcas_mode,&_xcas_mode,_xcas_mode_s);
  define_unary_function_ptr5( at_xcas_mode ,alias_at_xcas_mode,&__xcas_mode,0,true);
  static const char _maple_mode_s []="maple_mode";
  static define_unary_function_eval (__maple_mode,&_xcas_mode,_maple_mode_s);
  define_unary_function_ptr5( at_maple_mode ,alias_at_maple_mode,&__maple_mode,0,true);

  gen giac_eval_level(const gen & g,GIAC_CONTEXT){
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return eval_level(contextptr);
    eval_level(contextptr)=args.val;
    DEFAULT_EVAL_LEVEL=args.val;
    return args;
  }
  static const char _eval_level_s []="eval_level";
  static define_unary_function_eval2 (__eval_level,&giac_eval_level,_eval_level_s,&printasDigits);
  define_unary_function_ptr5( at_eval_level ,alias_at_eval_level,&__eval_level,0,true);

  gen _prog_eval_level(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return prog_eval_level(contextptr);
    prog_eval_level_val(contextptr)=args.val;
    return args;
  }
  static const char _prog_eval_level_s []="prog_eval_level";
  static define_unary_function_eval2 (__prog_eval_level,&_prog_eval_level,_prog_eval_level_s,&printasDigits);
  define_unary_function_ptr5( at_prog_eval_level ,alias_at_prog_eval_level,&__prog_eval_level,0,true);

  gen _with_sqrt(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return withsqrt(contextptr);
    withsqrt(contextptr)=(args.val)!=0;
    return args;
  }
  static const char _with_sqrt_s []="with_sqrt";
  static define_unary_function_eval2 (__with_sqrt,&_with_sqrt,_with_sqrt_s,&printasDigits);
  define_unary_function_ptr5( at_with_sqrt ,alias_at_with_sqrt,&__with_sqrt,0,true);

  gen _all_trig_solutions(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return all_trig_sol(contextptr);
    all_trig_sol((args.val)!=0,contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _all_trig_solutions_s []="all_trig_solutions";
  static define_unary_function_eval2 (__all_trig_solutions,&_all_trig_solutions,_all_trig_solutions_s,&printasDigits);
  define_unary_function_ptr( at_all_trig_solutions ,alias_at_all_trig_solutions ,&__all_trig_solutions);

  gen _ntl_on(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return ntl_on(contextptr);
    ntl_on((args.val)!=0,contextptr);
    ntl_on((args.val)!=0,context0); // Current factorization routines do not have access to the context
    return args;
  }
  static const char _ntl_on_s []="ntl_on";
  static define_unary_function_eval2 (__ntl_on,&_ntl_on,_ntl_on_s,&printasDigits);
  define_unary_function_ptr( at_ntl_on ,alias_at_ntl_on ,&__ntl_on);

  gen _complex_mode(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return complex_mode(contextptr);
    complex_mode((args.val)!=0,contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _complex_mode_s []="complex_mode";
  static define_unary_function_eval2 (__complex_mode,&_complex_mode,_complex_mode_s,&printasDigits);
  define_unary_function_ptr( at_complex_mode ,alias_at_complex_mode ,&__complex_mode);

  gen _angle_radian(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return angle_radian(contextptr);
    angle_radian((args.val)!=0,contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _angle_radian_s []="angle_radian";
  static define_unary_function_eval2 (__angle_radian,&_angle_radian,_angle_radian_s,&printasDigits);
  define_unary_function_ptr( at_angle_radian ,alias_at_angle_radian ,&__angle_radian);

  gen _epsilon(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG &&  arg.subtype==-1) return  arg;
    gen args=evalf_double(arg,0,contextptr);
    if (args.type!=_DOUBLE_)
      return epsilon(contextptr);
    epsilon(fabs(args._DOUBLE_val),contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _epsilon_s []="epsilon";
  static define_unary_function_eval2 (__epsilon,&_epsilon,_epsilon_s,&printasDigits);
  define_unary_function_ptr( at_epsilon ,alias_at_epsilon ,&__epsilon);

  gen _proba_epsilon(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG &&  arg.subtype==-1) return  arg;
    gen args=evalf_double(arg,0,contextptr);
    if (args.type!=_DOUBLE_)
      return proba_epsilon(contextptr);
    proba_epsilon(contextptr)=fabs(args._DOUBLE_val);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _proba_epsilon_s []="proba_epsilon";
  static define_unary_function_eval2 (__proba_epsilon,&_proba_epsilon,_proba_epsilon_s,&printasDigits);
  define_unary_function_ptr( at_proba_epsilon ,alias_at_proba_epsilon ,&__proba_epsilon);

  gen _complex_variables(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return complex_variables(contextptr);
    complex_variables((args.val)!=0,contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _complex_variables_s []="complex_variables";
  static define_unary_function_eval2 (__complex_variables,&_complex_variables,_complex_variables_s,&printasDigits);
  define_unary_function_ptr( at_complex_variables ,alias_at_complex_variables ,&__complex_variables);

  gen _approx_mode(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);
    if (args.type!=_INT_)
      return approx_mode(contextptr);
    approx_mode((args.val)!=0,contextptr);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _approx_mode_s []="approx_mode";
  static define_unary_function_eval2 (__approx_mode,&_approx_mode,_approx_mode_s,&printasDigits);
  define_unary_function_ptr( at_approx_mode ,alias_at_approx_mode ,&__approx_mode);

  gen _threads(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return threads;
    threads=giacmax(absint(args.val),1);
    parent_cas_setup(contextptr);
    return args;
  }
  static const char _threads_s []="threads";
  static define_unary_function_eval2 (__threads,&_threads,_threads_s,&printasDigits);
  define_unary_function_ptr( at_threads ,alias_at_threads ,&__threads);

  int digits2bits(int n){
#ifdef OLDGNUWINCE
    return (n*33)/10;
#else
    return int(floor(std::log(10.0)/std::log(2.0)*n))+1;
#endif
  }

  int bits2digits(int n){
#ifdef OLDGNUWINCE
    return (n*3)/10;
#else
    return int(floor(std::log(2.0)/std::log(10.0)*n))+1;
#endif
  }

  void set_decimal_digits(int n,GIAC_CONTEXT){
#ifdef GNUWINCE
    return undef;
#else
#ifdef HAVE_LIBMPFR
    decimal_digits(contextptr)=giacmax(absint(n),1);
#else
    decimal_digits(contextptr)=giacmin(giacmax(absint(n),1),13);
#endif
    // deg2rad_g=evalf(cst_pi,1,0)/180;
    // rad2deg_g=inv(deg2rad_g);
#endif
  }

  bool cas_setup(const vecteur & v_orig,GIAC_CONTEXT){
    vecteur v(v_orig);
    if (v.size()<7)
      return false;
    if (*logptr(contextptr) && debug_infolevel) 
      *logptr(contextptr) << gettext("Cas_setup ") << v << char(10) << char(13) ;
    if (v[0].type==_INT_)
      approx_mode((v[0].val)!=0,contextptr);
    else {
      v[0]=evalf_double(v[0],1,contextptr);
      if (v[0].type==_DOUBLE_)
	approx_mode(v[0]._DOUBLE_val!=0,contextptr);
    }
    if (v[1].type==_INT_)
      complex_variables((v[1].val)!=0,contextptr);
    else {
      v[1]=evalf_double(v[1],1,contextptr);
      if (v[1].type==_DOUBLE_)
	complex_variables(v[1]._DOUBLE_val!=0,contextptr);
    }
    if (v[2].type==_INT_)
      complex_mode((v[2].val)!=0,contextptr);
    else {
      v[2]=evalf_double(v[2],1,contextptr);
      if (v[2].type==_DOUBLE_)
	complex_mode(v[2]._DOUBLE_val!=0,contextptr);
    }
    if (v[3].type==_INT_)
      angle_radian(v[3].val!=0,contextptr);
    else {
      v[3]=evalf_double(v[3],1,contextptr);
      if (v[3].type==_DOUBLE_)
	angle_radian(v[3]._DOUBLE_val!=0,contextptr);
    }
    v[4]=evalf_double(v[4],1,contextptr);
    if (v[4].type==_DOUBLE_){
      int format=int(v[4]._DOUBLE_val);
      scientific_format(format % 16,contextptr);
      integer_format(format/16,contextptr);
    }
    v[5]=evalf_double(v[5],1,contextptr);
    if (v[5].type==_DOUBLE_)
      epsilon(fabs(v[5]._DOUBLE_val),contextptr);
    if (v[5].type==_VECT && v[5]._VECTptr->size()==2 && v[5]._VECTptr->front().type==_DOUBLE_ && v[5]._VECTptr->back().type==_DOUBLE_){
      epsilon(fabs(v[5]._VECTptr->front()._DOUBLE_val),contextptr);
      proba_epsilon(contextptr)=fabs(v[5]._VECTptr->back()._DOUBLE_val); 
    }
    if (v[6].type==_INT_)
      set_decimal_digits(v[6].val,contextptr);
    else {
      v[6]=evalf_double(v[6],1,contextptr);
      if (v[6].type==_DOUBLE_)
	set_decimal_digits(int(v[6]._DOUBLE_val),contextptr);
    }
    if (v.size()>=8){
      if (v[7].type==_VECT){
	vecteur & vv =*v[7]._VECTptr;
	if (vv.size()>=4){
	  threads=std::max(1,int(evalf_double(vv[0],1,contextptr)._DOUBLE_val));
	  MAX_RECURSION_LEVEL=std::max(int(evalf_double(vv[1],1,contextptr)._DOUBLE_val),1);
	  debug_infolevel=std::max(0,int(evalf_double(vv[2],1,contextptr)._DOUBLE_val));
	  DEFAULT_EVAL_LEVEL=std::max(1,int(evalf_double(vv[3],1,contextptr)._DOUBLE_val));
	}
      }
    }
    if (v.size()>=9){ 
      if (v[8].type==_INT_)
	increasing_power(v[8].val!=0,contextptr);
      else {
	v[8]=evalf_double(v[8],1,contextptr);
	if (v[8].type==_DOUBLE_)
	  increasing_power(v[8]._DOUBLE_val!=0,contextptr);
      }
    }
    if (v.size()>=10){ 
      if (v[9].type==_INT_)
	withsqrt(v[9].val!=0,contextptr);
      else {
	v[9]=evalf_double(v[9],1,contextptr);
	if (v[9].type==_DOUBLE_)
	  withsqrt(v[9]._DOUBLE_val!=0,contextptr);
      }
    }
    if (v.size()>=11){ 
      if (v[10].type==_INT_)
	all_trig_sol(v[10].val!=0,contextptr);
      else {
	v[10]=evalf_double(v[10],1,contextptr);
	if (v[10].type==_DOUBLE_)
	  all_trig_sol(v[10]._DOUBLE_val!=0,contextptr);
      }
    }
    return true;
  }
  vecteur cas_setup(GIAC_CONTEXT){
    vecteur v;
    v.push_back(approx_mode(contextptr));
    v.push_back(complex_variables(contextptr));
    v.push_back(complex_mode(contextptr));
    v.push_back(angle_radian(contextptr));
    v.push_back(scientific_format(contextptr)+16*integer_format(contextptr));
    v.push_back(makevecteur(epsilon(contextptr),proba_epsilon(contextptr)));
    v.push_back(decimal_digits(contextptr));
    v.push_back(makevecteur(threads,MAX_RECURSION_LEVEL,debug_infolevel,DEFAULT_EVAL_LEVEL));
    v.push_back(increasing_power(contextptr));
    v.push_back(withsqrt(contextptr));
    v.push_back(all_trig_sol(contextptr));    
    return v;
  }
  gen _cas_setup(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & w=*args._VECTptr;
    if (w.empty())
      return cas_setup(contextptr);
    if (!cas_setup(w,contextptr))
      return gendimerr(contextptr);
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id){
      _signal(symbolic(at_quote,symbolic(at_cas_setup,w)),contextptr);
    }
#endif
    return args;
  }
  static const char _cas_setup_s []="cas_setup";
  static define_unary_function_eval (__cas_setup,&giac::_cas_setup,_cas_setup_s);
  define_unary_function_ptr5( at_cas_setup ,alias_at_cas_setup,&__cas_setup,0,true);

  void parent_cas_setup(GIAC_CONTEXT){
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id){
      _signal(symbolic(at_quote,symbolic(at_cas_setup,cas_setup(contextptr))),contextptr);
    }
#endif
  }

  string printasDigits(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type==_VECT && feuille._VECTptr->empty())
      return sommetstr;
    return sommetstr+(" := "+feuille.print(contextptr));
  }
  gen _Digits(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    if (g.type==_DOUBLE_)
      args=int(g._DOUBLE_val);    
    if (args.type!=_INT_)
      return decimal_digits(contextptr);
    set_decimal_digits(args.val,contextptr);
    _cas_setup(cas_setup(contextptr),contextptr);
    return decimal_digits(contextptr);
  }
  static const char _Digits_s []="Digits";
  static define_unary_function_eval2 (__Digits,&giac::_Digits,_Digits_s,&printasDigits);
  define_unary_function_ptr( at_Digits ,alias_at_Digits ,&__Digits);

  gen _xport(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    string libname(gen2string(args));
    std::map<std::string,std::vector<std::string> >::iterator it=library_functions().find(libname);
    if (it==library_functions().end())
      return zero;
    sort(it->second.begin(),it->second.end());
    // Add library function names to the translator
    std::vector<std::string>::iterator jt=it->second.begin(),jtend=it->second.end(),kt,ktend;
    for (;jt!=jtend;++jt){
      string tname=libname+"::"+*jt;
      // Find if the name exists in the translator base
      it=lexer_translator().find(*jt);
      if (it==lexer_translator().end())
	lexer_translator()[*jt]=vector<string>(1,tname);
      else { // Name exists, check if tname is in the vector, else push it
	kt=it->second.begin(); ktend=it->second.end();
	for (;kt!=ktend;++kt){
	  if (*kt==tname)
	    break;
	}
	if (kt!=ktend)
	  it->second.erase(kt);
	it->second.push_back(tname);
      }
    }
    return plus_one;
  }
  gen _insmod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_STRNG)
      return _xport(args,contextptr);
#ifdef HAVE_LIBDL
    string libname=*args._STRNGptr;
    if (libname.empty())
      return 0;
    // a way to add the current path to the search
    if (libname[0]!='/'){
      if (libname.size()<3 || libname.substr(0,3)!="lib")
	libname = "lib"+libname;
      gen pwd=_pwd(0,contextptr);
      if (pwd.type==_STRNG){
	string libname1 = *pwd._STRNGptr+'/'+libname;
	if (libname1.size()<3 || libname1.substr(libname1.size()-3,3)!=".so")
	  libname1 += ".so";
	if (is_file_available(libname1.c_str()))
	  libname=libname1;
      }
    }
#ifndef WIN32
    if (libname.size()<3 || libname.substr(libname.size()-3,3)!=".so")
      libname += ".so";
#endif
    modules_tab::const_iterator i = giac_modules_tab.find(libname);
    if (i!=giac_modules_tab.end())
      return plus_two; // still registered
    registered_lexer_functions().clear();
    doing_insmod=true;
    void * handle = dlopen (libname.c_str(), RTLD_LAZY);
    if (!handle) {
      setsizeerr (string(dlerror()));
    }
    // if (debug_infolevel)
    //  *logptr(contextptr) << registered_lexer_functions << endl;
    giac_modules_tab[libname]=module_info(registered_lexer_functions(),handle);
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id)
      _signal(symb_quote(symbolic(at_insmod,args)),contextptr);
    else
#endif
      *logptr(contextptr) << gettext("Parent insmod") <<endl;
    return _xport(args,contextptr);
#else // HAVE_LIBDL
    return zero;
#endif // HAVE_LIBDL
  }
  static const char _insmod_s []="insmod";
  static define_unary_function_eval (__insmod,&_insmod,_insmod_s);
  define_unary_function_ptr5( at_insmod ,alias_at_insmod,&__insmod,0,true);

  gen _rmmod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_STRNG)
      return gentypeerr(contextptr);
#ifdef HAVE_LIBDL
    string libname=*args._STRNGptr;
    modules_tab::const_iterator i = giac_modules_tab.find(libname);
    if (i==giac_modules_tab.end())
      return plus_two; // not registered
    dlclose(i->second.handle);
    bool res= lexer_function_remove(i->second.registered_names);
    giac_modules_tab.erase(libname);
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id)
      _signal(symb_quote(symbolic(at_rmmod,args)),contextptr);
#endif
    return(res);
#else // HAVE_LIBDL
    return zero;
#endif // HAVE_LIBDL
  }

  /*
  gen _rmmod(const gen & args){
  if ( args){
    if (args.type==_VECT)
      apply(args.type==_STRNG &&  args.subtype==-1{
    if (args.type==_VECT)
      apply(args)) return  args){
    if (args.type==_VECT)
      apply(args;
    if (args.type==_VECT)
      apply(args,giac::rmmod);
    rmmod(args);    
  }
  */
  static const char _rmmod_s []="rmmod";
  static define_unary_function_eval (__rmmod,&_rmmod,_rmmod_s);
  define_unary_function_ptr5( at_rmmod ,alias_at_rmmod,&__rmmod,0,true);

  gen _lsmod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    vecteur v;
#ifdef HAVE_LIBDL
    modules_tab::const_iterator i = giac_modules_tab.begin(),iend=giac_modules_tab.end();
    for (;i!=iend;++i)
      v.push_back(string2gen(i->first,false));
#endif
    return v;
  }
  static const char _lsmod_s []="lsmod";
  static define_unary_function_eval (__lsmod,&_lsmod,_lsmod_s);
  define_unary_function_ptr5( at_lsmod ,alias_at_lsmod,&__lsmod,0,true);

  class gen_sort {
    gen sorting_function;
    const context * contextptr;
  public:
    bool operator () (const gen & a,const gen & b){
      gen c=sorting_function(gen(makevecteur(a,b),_SEQ__VECT),contextptr);
      if (c.type!=_INT_){
#ifndef NO_STDEXCEPT
	setsizeerr(gettext("Unable to sort ")+c.print(contextptr));
#else
	*logptr(contextptr) << gettext("Unable to sort ") << c << endl;
#endif
	return true;
      }
      return !is_zero(c);
    }
    gen_sort(const gen & f,const context * ptr): sorting_function(f),contextptr(ptr) {};
  };

  /*
  gen sorting_function;
  bool sort_sort(const gen & a,const gen & b){
    gen c=sorting_function(gen(makevecteur(a,b),_SEQ__VECT),0);
    if (c.type!=_INT_)
      setsizeerr(gettext("Unable to sort ")+c.print(contextptr));
    return !is_zero(c);
  }
  */

  gen simplifier(const gen & g,GIAC_CONTEXT){
    return liste2symbolique(symbolique2liste(g,contextptr));
  }
  gen _simplifier(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return simplifier(g,contextptr);
    return apply(g,_simplifier,contextptr);
  }

  gen _sort(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_SYMB)
      return simplifier(args,contextptr);
    if (args.type!=_VECT)
      return args; // FIXME sort in additions, symbolic(at_sort,args);
    vecteur v=*args._VECTptr;
    int subtype;
    gen f;
    if ( v.size()==2 && v[0].type==_VECT ){
      f=v[1];
      subtype=v[0].subtype;
      v=*v[0]._VECTptr;
    }
    else {
      f=at_inferieur_strict;
      subtype=args.subtype;
    }
    sort(v.begin(),v.end(),gen_sort(f,contextptr));
    return gen(v,subtype);
  }
  static const char _sort_s []="sort";
  static define_unary_function_eval (__sort,&_sort,_sort_s);
  define_unary_function_ptr5( at_sort ,alias_at_sort,&__sort,0,true);

  static gen remove_nodisp(const gen & g){
    if (g.is_symb_of_sommet(at_nodisp))
      return g._SYMBptr->feuille;
    return g;
  }
  gen _ans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    int s=history_out(contextptr).size();
    if (!s)
      return undef;
    int i;
    if (args.type!=_INT_)
      i=-1;
    else {
      i=args.val;
      if (xcas_mode(contextptr)==3)
	i=-i;
    }
    if (i>=0){
      if (i>=s)
	return gentoofewargs(print_INT_(i));
      return remove_nodisp(history_out(contextptr)[i]);
    }
    if (s+i<0)
      return gentoofewargs(print_INT_(-i));
    return remove_nodisp(history_out(contextptr)[s+i]);
  }
  static const char _ans_s []="ans";
  static define_unary_function_eval (__ans,&_ans,_ans_s);
  define_unary_function_ptr5( at_ans ,alias_at_ans,&__ans,0,true);

  gen _quest(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (rpn_mode(contextptr))
      return gensizeerr(contextptr);
    int s=history_in(contextptr).size();
    if (!s)
      return undef;
    int i;
    if (args.type!=_INT_)
      i=-2;
    else
      i=args.val;
    if (i>=0){
      if (i>=s)
	return gentoofewargs(print_INT_(i));
      return remove_nodisp(history_in(contextptr)[i]);
    }
    if (s+i<0)
      return gentoofewargs(print_INT_(-i));
    return remove_nodisp(history_in(contextptr)[s+i]);
  }
  static const char _quest_s []="quest";
  static define_unary_function_eval (__quest,&_quest,_quest_s);
  define_unary_function_ptr5( at_quest ,alias_at_quest,&__quest,0,true);

  vector<int> float2continued_frac(double d_orig,double eps){
    if (eps<1e-11)
      eps=1e-11;
    double d=fabs(d_orig);
    vector<int> v;
    if (d>rand_max2){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Float too large"));
#endif
      v.push_back(rand_max2);
      return v;
    }
    double i;
    for (;;){
      i=floor(d);
      v.push_back(int(i));
      d=d-i;
      if (d<eps)
	return v;
      d=1/d;
      eps=eps*d*d;
    }
  }

  gen continued_frac2gen(vector<int> v,double d_orig,double eps,GIAC_CONTEXT){
    gen res(v.back());
    for (;;){
      v.pop_back();
      if (v.empty()){
	if (
	    !my_isnan(d_orig) &&
	    fabs(evalf_double(res-d_orig,1,contextptr)._DOUBLE_val)>eps)
	  return d_orig;
	return res;
      }
      res=inv(res,contextptr);
      res=res+v.back();
    }
    return res;
  }

  gen chk_not_unit(const gen & g){
    if (g.is_symb_of_sommet(at_unit))
      return gensizeerr(gettext("Incompatible units"));
    return g;
  }

  gen _convert(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT){
      if (args.type==_POLY)
	return _convert(vecteur(1,args),contextptr);
      return gensizeerr(contextptr);
    }
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s>=1 && v.front().type==_POLY){
      int dim=v.front()._POLYptr->dim;
      vecteur idx(dim);
      vector< monomial<gen> >::const_iterator it=v.front()._POLYptr->coord.begin(),itend=v.front()._POLYptr->coord.end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it){
	index_t::const_iterator j=it->index.begin();
	for (int k=0;k<dim;k++,++j)
	  idx[k]=*j;
	res.push_back(makevecteur(it->value,idx));
      }
      return res;
    }
    if (s<2)
      return gensizeerr(contextptr);
    gen & f=v[1];
    gen g=v.front();
    if (f.is_symb_of_sommet(at_unit)){
      return chk_not_unit(mksa_reduce(evalf(g/f,1,contextptr),contextptr))*f;
    }
    if (s==3 && f.type==_INT_ ){
      if (f.val==_BASE && is_integer(v.back()) ){
	if (is_greater(1,v.back(),contextptr))
	  return gensizeerr(gettext("Bad conversion basis"));
	if (is_integer(g)){
	  if (is_zero(g))
	    return makevecteur(g);
	  // convert(integer,base,integer)
	  bool positif=is_positive(g,contextptr);
	  g=abs(g,contextptr);
	  vecteur res;
	  gen q;
	  for (;!is_zero(g);){
	    res.push_back(irem(g,v.back(),q));
	    g=q;
	  }
	  // reverse(res.begin(),res.end());
	  if (positif)
	    return res;
	  return -res;
	}
	if (g.type==_VECT){
	  vecteur w(*g._VECTptr);
	  reverse(w.begin(),w.end());
	  return horner(w,v.back());
	}
      }
      if (f.val==_CONFRAC && v.back().type==_IDNT){
	g=evalf_double(g,1,contextptr);
	if (g.type==_DOUBLE_)
	  return sto(vector_int_2_vecteur(float2continued_frac(g._DOUBLE_val,epsilon(contextptr))),v.back(),contextptr);
      }
    }
    if (s>2)
      g=gen(mergevecteur(vecteur(1,g),vecteur(v.begin()+2,v.begin()+s)),args.subtype);
#ifndef CAS38_DISABLED
    if (v[1].type==_FUNC){
      if (f==at_sincos)
	return sincos(g,contextptr);
      if (f==at_sin || f==at_SIN)
	return trigsin(g,contextptr);
      if (f==at_cos || f==at_COS)
	return trigcos(g,contextptr);
      if (f==at_tan || f==at_TAN)
	return halftan(g,contextptr);
      if (f==at_plus)
	return partfrac(tcollect(g,contextptr),true,contextptr);
      if (f==at_prod)
	return _factor(_texpand(g,contextptr),contextptr);
      if (f==at_division)
	return _simplify(g,contextptr);
      if (f==at_exp || f==at_ln || f==at_EXP)
	return trig2exp(g,contextptr);
      if (f==at_string)
	return string2gen(g.print(contextptr),false);
      if (f==at_matrix || f==at_vector || f==at_array){
	g.subtype=_MATRIX__VECT;
	return g;
      }
      return f(g,contextptr);
      // setsizeerr();
    }
#endif
    if (f.type==_INT_ && f.val>=0) {
      int i=f.val;
      if (f.val==_FRAC && f.subtype==_INT_TYPE)
	return exact(g,contextptr);
      if (f.val==_POLY1__VECT && f.subtype==_INT_MAPLECONVERSION){ // remove order_size
	if (g.type==_VECT && !g._VECTptr->empty()){
	  // check if g is a list of [coeff,[index]]
	  vecteur & w=*g._VECTptr;
	  if (w.front().type==_VECT && w.front()._VECTptr->size()==2 && w.front()._VECTptr->back().type==_VECT){
	    unsigned dim=w.front()._VECTptr->back()._VECTptr->size();
	    iterateur it=w.begin(),itend=w.end();
	    polynome res(dim);
	    vector< monomial<gen> > & coord =res.coord;
	    coord.reserve(itend-it);
	    index_t i(dim);
	    for (;it!=itend;++it){
	      if (it->type!=_VECT || it->_VECTptr->size()!=2 || it->_VECTptr->back().type!=_VECT)
		break;
	      vecteur & idx = *it->_VECTptr->back()._VECTptr;
	      if (idx.size()!=dim)
		break;
	      const_iterateur jt=idx.begin(),jtend=idx.end();
	      for (int k=0;jt!=jtend;++jt,++k){
		if (jt->type!=_INT_)
		  break;
		i[k]=jt->val;
	      }
	      if (jt!=jtend)
		break;
	      coord.push_back(monomial<gen>(it->_VECTptr->front(),i));
	    }
	    if (it==itend)
	      return res;
	  }
	}
	vecteur l(lop(g,at_order_size));
	vecteur lp(l.size(),zero);
	g=subst(g,l,lp,false,contextptr);
	return g;
      }
#ifndef CAS38_DISABLED
      if (f.subtype==_INT_MAPLECONVERSION){
	switch (i){
	case _TRIG:
	  return sincos(g,contextptr);
	case _EXPLN:
	  return trig2exp(g,contextptr);
	case _PARFRAC: case _FULLPARFRAC:
	  return _partfrac(g,contextptr);
	case _MAPLE_LIST:
	  if (g.subtype==0 && ckmatrix(g)){
	    vecteur v;
	    aplatir(*g._VECTptr,v);
	    return v;
	  }
	  g.subtype=0;
	  return g;
	default:
	  return gensizeerr(contextptr);
	}
      }
#endif
      g.subtype=v.back().val;
      return g;
    }
    return gensizeerr(contextptr);
  }
  static const char _convert_s []="convert";
  static define_unary_function_eval (__convert,&_convert,_convert_s);
  define_unary_function_ptr5( at_convert ,alias_at_convert,&__convert,0,true);

  static const char _convertir_s []="convertir";
  static define_unary_function_eval (__convertir,&_convert,_convertir_s);
  define_unary_function_ptr5( at_convertir ,alias_at_convertir,&__convertir,0,true);

  gen _deuxpoints(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_deuxpoints,args);
  }
  static const char _deuxpoints_s []=":";
  static define_unary_function_eval4 (__deuxpoints,&_deuxpoints,_deuxpoints_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_deuxpoints ,alias_at_deuxpoints ,&__deuxpoints);

  // FIXME SECURITY
  gen quote_read(const gen & args,GIAC_CONTEXT){
    if (args.type!=_STRNG)
      return symbolic(at_read,args);
    string fichier=*args._STRNGptr;
    ifstream inf(fichier.c_str());
    if (!inf)
      return undef;
#if defined( VISUALC ) || defined( BESTA_OS )
    char * thebuf = ( char * )alloca( BUFFER_SIZE );
#else
    char thebuf[BUFFER_SIZE];
#endif
    inf.getline(thebuf,BUFFER_SIZE,'\n');
    string lu(thebuf),thet;
    if (lu.size()>9 && lu.substr(0,9)=="{VERSION "){ // Maple Worksheet
      ofstream of("__.map");
      mws_translate(inf,of);
      of.close();
      xcas_mode(contextptr)=1;
      *logptr(contextptr) << gettext("Running maple text translation __.map") << endl;
      fichier="__.map";
    }
    if (lu.size()>6 && lu.substr(0,6)=="**TI92"){ // TI archive
      inf.close();
      xcas_mode(contextptr)=3;
      eval(_unarchive_ti(args,contextptr),1,contextptr);
      return symbolic(at_xcas_mode,3);
    }
    if (lu=="\\START92\\\r"){ // TI text
      ofstream of("__.ti");
      ti_translate(inf,of);
      of.close();
      xcas_mode(contextptr)=3;
      *logptr(contextptr) << gettext("Running TI89 text translation __.ti") << endl;
      fichier="__.ti";
    } // end file of type TI
    inf.close();
    ifstream inf2(fichier.c_str());
    vecteur v;
    readargs_from_stream(inf2,v,contextptr);
    return v.size()==1?v.front():gen(v,_SEQ__VECT);
  }
  gen _read(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT && !args._VECTptr->empty() && args._VECTptr->front().type==_STRNG){
      FILE * f=fopen(args._VECTptr->front()._STRNGptr->c_str(),"r");
      if (!f)
	return undef;
      string s;
      while (!feof(f))
	s += fgetc(f);
      return string2gen(s,false);
    }
    if (args.type!=_STRNG)
      return symbolic(at_read,args);
    return eval(quote_read(args,contextptr),eval_level(contextptr),contextptr);
  }
  static const char _read_s []="read";
  static define_unary_function_eval (__read,&_read,_read_s);
  define_unary_function_ptr5( at_read ,alias_at_read ,&__read,0,T_RETURN);

  gen _write(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (args.type==_VECT){
      vecteur v=*args._VECTptr;
      v.front()=eval(v.front(),eval_level(contextptr),contextptr);
      if (v.size()<2 || v.front().type!=_STRNG)
	return gensizeerr(contextptr);
      ofstream inf(v[0]._STRNGptr->c_str());
      const_iterateur it=v.begin()+1,itend=v.end();
      for (;it!=itend;++it){
	if (it->type==_IDNT){
	  gen tmp=eval(*it,1,contextptr);
	  gen tmp2=*it;
	  inf << symb_sto(tmp,tmp2) << ";" << endl;
	}
      }
      return plus_one;
    }
    if (args.type!=_STRNG)
      return symbolic(at_write,args);
    ofstream inf(args._STRNGptr->c_str());
    const_iterateur it=history_in(contextptr).begin(),itend=history_in(contextptr).end();
    if (it==itend)
      return zero;
    for (;it!=itend;++it){
      inf << *it << ";" << endl;
    }
    return plus_one;
  }
  static const char _write_s []="write";
  static define_unary_function_eval_quoted (__write,&_write,_write_s);
  define_unary_function_ptr5( at_write ,alias_at_write,&__write,_QUOTE_ARGUMENTS,true);

  gen _save_history(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (args.type!=_STRNG)
      return symbolic(at_save_history,args);
    ofstream of(args._STRNGptr->c_str());
    vecteur v(history_in(contextptr));
    if (!v.empty() && v.back().is_symb_of_sommet(at_save_history))
      v.pop_back();
    of << gen(history_in(contextptr),_SEQ__VECT) << endl;
    return plus_one;
  }
  static const char _save_history_s []="save_history";
  static define_unary_function_eval (__save_history,&_save_history,_save_history_s);
  define_unary_function_ptr5( at_save_history ,alias_at_save_history,&__save_history,0,true);
  /*
  gen _matrix(const gen & args){
  if ( args){
    if (!ckmatrix(args))
      return symbolic(at_matrix.type==_STRNG &&  args.subtype==-1{
    if (!ckmatrix(args))
      return symbolic(at_matrix)) return  args){
    if (!ckmatrix(args))
      return symbolic(at_matrix;
    if (!ckmatrix(args))
      return symbolic(at_matrix,args);
    gen res=args;
    res.subtype=_MATRIX__VECT;
    return res;
  }
  static const char _matrix_s []="matrix";
  static define_unary_function_eval (__matrix,&_matrix,_matrix_s);
  define_unary_function_ptr5( at_matrix ,alias_at_matrix,&__matrix);
  */

  gen symb_findhelp(const gen & args){
    return symbolic(at_findhelp,args);
  }
  gen _findhelp(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen args(g);
    int lang=language(contextptr);
    int helpitems = 0;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2 && g._VECTptr->back().type==_INT_){
      args=g._VECTptr->front();
      lang=absint(g._VECTptr->back().val);
    }
    if (args.type==_FUNC && string(args._FUNCptr->ptr()->s)=="pari")
      return string2gen(pari_help(0),false);
    if (args.type==_SYMB && string(args._SYMBptr->sommet.ptr()->s)=="pari")
      return string2gen(pari_help(args._SYMBptr->feuille),false);
    string argss=args.print(contextptr);
    // remove space at the end if required
    while (!argss.empty() && argss[argss.size()-1]==' ')
      argss=argss.substr(0,argss.size()-1);
    if (argss.size()>5 && argss.substr(0,5)=="pari_")
      return string2gen(pari_help(string2gen(argss.substr(5,argss.size()-5),false)),false);      
    const char * howto, * syntax, * related, *examples;
    if (has_static_help(argss.c_str(),lang,howto,syntax,related,examples)){
      return string2gen(string(howto)+'\n'+string(syntax)+'\n'+string(related)+'\n'+string(examples),false);
    }
    if (!vector_aide_ptr() || vector_aide_ptr()->empty()){
      if (!vector_aide_ptr())
	vector_aide_ptr() = new vector<aide>;
      * vector_aide_ptr()=readhelp("aide_cas",helpitems,false);
      if (!helpitems){
	* vector_aide_ptr()=readhelp(default_helpfile,helpitems);
      }
      if (!helpitems){
	* vector_aide_ptr()=readhelp((giac_aide_dir()+"aide_cas").c_str(),helpitems);
      }
    }
    if (vector_aide_ptr()){
      string s=argss; // args.print(contextptr);
      int l=s.size();
      if ( (l>2) && (s[0]=='\'') && (s[l-1]=='\'') )
	s=s.substr(1,l-2);
      l=s.size();
      if (l && s[l-1]==')'){
	int i=l-1;
	for (;i;--i){
	  if (s[i]=='(')
	    break;
	}
	if (i)
	  s=s.substr(0,i);
      }
      s=writehelp(helpon(s,*vector_aide_ptr(),lang,vector_aide_ptr()->size()),lang);
      return string2gen(s,false);
    }
    else
      return gensizeerr(gettext("No help file found"));
    return 0;
  }
  static const char _findhelp_s []="findhelp";
  static define_unary_function_eval_quoted (__findhelp,&_findhelp,_findhelp_s);
  define_unary_function_ptr5( at_findhelp ,alias_at_findhelp,&__findhelp,_QUOTE_ARGUMENTS,true);

  gen _member(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen g=args;
    vecteur v;
    if (args.type!=_VECT){
      g=args.eval(eval_level(contextptr),contextptr);
      if (g.type!=_VECT)
	return symbolic(at_member,args);
      v=*g._VECTptr;
    }
    else {
      v=*args._VECTptr;
      if (v.size()>1){
	v[0]=eval(v[0],eval_level(contextptr),contextptr);
	v[1]=eval(v[1],eval_level(contextptr),contextptr);
      }
    }
    int s=v.size();
    if (s<2)
      return gentoofewargs("");
    if (v[1].type!=_VECT)
      return gensizeerr(contextptr);
    int i=equalposcomp(*v[1]._VECTptr,v[0]);
    if (s==3){
      gen tmpsto;
      if (xcas_mode(contextptr))
	tmpsto=sto(i,v[2],contextptr);
      else
	tmpsto=sto(i-1,v[2],contextptr);
      if (is_undef(tmpsto)) return tmpsto;
    }
    return i;
  }
  static const char _member_s []="member";
  static define_unary_function_eval_quoted (__member,&_member,_member_s);
  define_unary_function_ptr5( at_member ,alias_at_member,&__member,_QUOTE_ARGUMENTS,true);

  // tablefunc(expression,[var,min,step])
  gen _tablefunc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen f,x=vx_var,xstart=gnuplot_xmin,step=(gnuplot_xmax-gnuplot_xmin)/10;
    gen xmax=gnuplot_xmax;
    if (args.type==_VECT){
      vecteur & v=*args._VECTptr;
      int s=v.size();
      if (!s)
	return gentoofewargs("");
      f=v[0];
      if (s>1)
	x=v[1];
      if (s>2)
	xstart=v[2];
      if (s>3)
	step=v[3];
      if (s>4)
	xmax=v[4];
    }
    else
      f=args;
    vecteur l0(makevecteur(x,f));
    gen graphe=symbolic(at_plotfunc,
			gen(makevecteur(_cell(makevecteur(vecteur(1,minus_one),vecteur(1,zero)),contextptr),
					symb_equal(_cell(makevecteur(vecteur(1,minus_one),vecteur(1,minus_one)),contextptr),symb_interval(xstart,xmax))
				    ),_SEQ__VECT));
    graphe.subtype=_SPREAD__SYMB;
    vecteur l1(makevecteur(step,graphe));
    gen l31(_cell(makevecteur(vecteur(1,minus_one),vecteur(1,zero)),contextptr)+_cell(makevecteur(plus_one,vecteur(1,zero)),contextptr));
    l31.subtype=_SPREAD__SYMB;
    gen l32(symb_evalf(symbolic(at_subst,gen(makevecteur(_cell(makevecteur(zero,vecteur(1,zero)),contextptr),_cell(makevecteur(zero,vecteur(1,minus_one)),contextptr),_cell(makevecteur(vecteur(1,zero),vecteur(1,minus_one)),contextptr)),_SEQ__VECT))));
    l32.subtype=_SPREAD__SYMB;
    vecteur l2(makevecteur(xstart,l32));
    vecteur l3(makevecteur(l31,l32));
    return makevecteur(l0,l1,l2,l3);
  }
  static const char _tablefunc_s []="tablefunc";
  static define_unary_function_eval (__tablefunc,&_tablefunc,_tablefunc_s);
  define_unary_function_ptr5( at_tablefunc ,alias_at_tablefunc,&__tablefunc,0,true);

  // tableseq(expression,[var,value])
  // var is a vector of dim the number of terms in the recurrence
  gen _tableseq(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    gen f,x=vx_var,uzero=zero;
    int dim=1;
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax;
    if (args.type==_VECT){
      vecteur & v=*args._VECTptr;
      int s=v.size();
      if (!s)
	return gentoofewargs("");
      f=v[0];
      if (s>1)
	x=v[1];
      if (s>2)
	uzero=evalf_double(v[2],1,contextptr);
      if (x.type==_VECT){
	dim=x._VECTptr->size();
	if (uzero.type!=_VECT)
	  return gentypeerr(contextptr);
	if (uzero._VECTptr->front().type==_VECT)
	  uzero=uzero._VECTptr->front();
	if ( (uzero.type!=_VECT) || (signed(uzero._VECTptr->size())!=dim) )
	  return gendimerr(contextptr);
      }
      else {
	if (uzero.type==_VECT && uzero._VECTptr->size()==3){
	  vecteur & uv=*uzero._VECTptr;
	  if (uv[1].type!=_DOUBLE_ || uv[2].type!=_DOUBLE_)
	    return gensizeerr(contextptr);
	  xmin=uv[1]._DOUBLE_val;
	  xmax=uv[2]._DOUBLE_val;
	  uzero=uv[0];
	}
      }
    }
    else
      f=args;
    vecteur res;
    res.push_back(f);
    if (x.type!=_VECT){
      res.push_back(x);
      if (dim!=1)
	res.push_back(dim);
      else {
	gen l31(symbolic(at_plotseq,
		       gen(makevecteur(
				       _cell(makevecteur(zero,vecteur(1,zero)),contextptr),
				       symb_equal(_cell(makevecteur(plus_one,vecteur(1,zero)),contextptr),makevecteur(_cell(makevecteur(vecteur(1,plus_one),vecteur(1,zero)),contextptr),xmin,xmax)),
				       9),_SEQ__VECT
			   )
		       )
	      );
	l31.subtype=_SPREAD__SYMB;
	res.push_back(l31);
      }
      res.push_back(uzero);
      gen l51(symb_evalf(symbolic(at_subst,gen(makevecteur(_cell(makevecteur(zero,vecteur(1,zero)),contextptr),_cell(makevecteur(plus_one,vecteur(1,zero)),contextptr),_cell(makevecteur(vecteur(1,minus_one),vecteur(1,zero)),contextptr)),_SEQ__VECT))));
      l51.subtype=_SPREAD__SYMB;
      res.push_back(l51);
    }
    else {
      for (int i=0;i<dim;++i)
	res.push_back(x[i]);
      vecteur tmp1,tmp2;
      for (int i=0;i<dim;++i){
	res.push_back(uzero[i]);
	tmp1.push_back(_cell(makevecteur(i+1,vecteur(1,zero)),contextptr));
	tmp2.push_back(_cell(makevecteur(vecteur(1,i-dim),vecteur(1,zero)),contextptr));
      }
      gen l41(symb_eval(symbolic(at_subst,gen(makevecteur(_cell(makevecteur(zero,vecteur(1,zero)),contextptr),tmp1,tmp2),_SEQ__VECT))));
      l41.subtype=_SPREAD__SYMB;
      res.push_back(l41);
    }
    return mtran(vecteur(1,res));
  }
  static const char _tableseq_s []="tableseq";
  static define_unary_function_eval_quoted (__tableseq,&_tableseq,_tableseq_s);
  define_unary_function_ptr5( at_tableseq ,alias_at_tableseq,&__tableseq,_QUOTE_ARGUMENTS,true);

  gen protecteval(const gen & g,int level, GIAC_CONTEXT){
    gen res;
#ifdef HAVE_LIBGSL //
    gsl_set_error_handler_off();
#endif //
    ctrl_c = false; interrupted=false;
    // save cas_setup in case of an exception
    vecteur cas_setup_save = cas_setup(contextptr);
    if (cas_setup_save.size()>5 && cas_setup_save[5].type==_VECT && cas_setup_save[5]._VECTptr->size()==2){
      vecteur & v = *cas_setup_save[5]._VECTptr;
      if (is_strictly_greater(v[0],1e-6,contextptr)){
	*logptr(contextptr) << gettext("Restoring epsilon to 1e-6 from ") << v[0] << endl;
	epsilon(1e-6,contextptr);
      }
      if (is_strictly_greater(v[1],1e-6,contextptr)){
	*logptr(contextptr) << gettext("Restoring proba epsilon to 1e-6 from ") << v[0] << endl;
	proba_epsilon(contextptr)=1e-6;
      }
      cas_setup_save=cas_setup(contextptr);
    }
    debug_struct dbg;
    dbg=*debug_ptr(contextptr);
#ifndef NO_STDEXCEPT
    try {
#endif
      res=approx_mode(contextptr)?g.evalf(level,contextptr):g.eval(level,contextptr);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & e){
      *debug_ptr(contextptr)=dbg;
      res=string2gen(e.what(),false);
      ctrl_c=false; interrupted=false;
      // something went wrong, so restore the old cas_setup
      cas_setup(cas_setup_save, contextptr);
    }
#endif
    return res;
  }

  static string printasnodisp(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    int maplemode=xcas_mode(contextptr) & 0x07;
    if (maplemode==1 || maplemode==2){
      string res=feuille.print(contextptr);
      int l=res.size(),j;
      for (j=l-1;j>=0 && res[j]==' ';--j)
	;
      if (res[j]==';')
	res[j]=':';
      else
	res += ':';
      return res;
    }
    return sommetstr+("("+feuille.print(contextptr)+")");
  }
  gen _nodisp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return string2gen("Done",false);
  }
  static const char _nodisp_s []="nodisp";
  static define_unary_function_eval2 (__nodisp,(const gen_op_context)_nodisp,_nodisp_s,&printasnodisp);
  define_unary_function_ptr5( at_nodisp ,alias_at_nodisp,&__nodisp,0,true);

  gen _unapply(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || args._VECTptr->empty() )
      return gentypeerr(contextptr);
    vecteur v=*args._VECTptr,w;
    int s=v.size();
    if (s<2)
      w=vecteur(1,vx_var);
    else {
      if (s==2 && v[1].type==_VECT)
	w=*v[1]._VECTptr;
      else
	w=vecteur(v.begin()+1,v.end());
    }
    gen g=subst(v[0].eval(eval_level(contextptr),contextptr),w,w,false,contextptr);
    if (g.type==_VECT && !g.subtype)
      g=makevecteur(g);
    return symbolic(at_program,gen(makevecteur(gen(w,_SEQ__VECT),w*zero,g),_SEQ__VECT));
  }
  static const char _unapply_s []="unapply";
  static define_unary_function_eval_quoted (__unapply,&_unapply,_unapply_s);
  define_unary_function_ptr5( at_unapply ,alias_at_unapply,&__unapply,_QUOTE_ARGUMENTS,true);

  gen _makevector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return vecteur(1,args);
    vecteur & v=*args._VECTptr;
    if (ckmatrix(args))
      return gen(v,_MATRIX__VECT);
    return v;
  }
  static const char _makevector_s []="makevector";
  static define_unary_function_eval (__makevector,&_makevector,_makevector_s);
  define_unary_function_ptr5( at_makevector ,alias_at_makevector,&__makevector,0,true);


  gen _makesuite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return vecteur(1,args);
    vecteur & v=*args._VECTptr;
    return gen(v,_SEQ__VECT);
  }
  static const char _makesuite_s []="makesuite";
  static define_unary_function_eval (__makesuite,&_makesuite,_makesuite_s);
  define_unary_function_ptr5( at_makesuite ,alias_at_makesuite,&__makesuite,0,true);

  gen _matrix(const gen & g,const context * contextptr){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur v=*g._VECTptr;
    if (ckmatrix(v))
      return gen(v,_MATRIX__VECT);
    int vs=v.size();
    if (vs<2)
      return gentypeerr(contextptr);
    if (vs==2 && v[0].type==_INT_ && v[1].type==_VECT){
      int l=giacmax(v[0].val,0);
      vecteur res(l);
      vecteur w(*v[1]._VECTptr);
      if (ckmatrix(w))
	aplatir(*v[1]._VECTptr,w);
      int s=giacmin(l,w.size());
      for (int i=0;i<s;++i)
	res[i]=w[i];
      return res;
    }
    if (vs==2){
      v.push_back(zero);
      ++vs;
    }
    if ( (v[0].type!=_INT_) || (v[1].type!=_INT_) )
      return gensizeerr(contextptr);
    int l(giacmax(v[0].val,0)),c(giacmax(v[1].val,0));
    bool transpose=(vs>3);
    if (transpose){ // try to merge arguments there
      // v[2]..v[vs-1] represents flattened submatrices 
      vecteur v2;
      for (int i=2;i<vs;++i){
	if (v[i].type!=_VECT)
	  return gentypeerr(contextptr);
	vecteur & w = *v[i]._VECTptr;
	int vis=w.size();
	if (vis % l)
	  return gendimerr(contextptr);
	int nc=vis/l;
	for (int J=0;J<nc;++J){
	  for (int I=J;I<vis;I+=nc)
	    v2.push_back(w[I]);
	}
      }
      v[2]=v2;
      swapint(l,c);
    }
    if (v[2].type==_VECT){
      vecteur w=*v[2]._VECTptr;
      int s=w.size();
      if (ckmatrix(w)){
	int ss=0;
	if (s)
	  ss=w[0]._VECTptr->size();
	int ll=giacmin(l,s);
	for (int i=0;i<ll;++i){
	  if (ss<c)
	    w[i]=mergevecteur(*w[i]._VECTptr,vecteur(c-ss));
	  else
	    w[i]=vecteur(w[i]._VECTptr->begin(),w[i]._VECTptr->begin()+c);
	}
	if (s<l)
	  w=mergevecteur(w,vecteur(l-s,vecteur(c)));
	else
	  w=vecteur(w.begin(),w.begin()+l);
	return gen(makefreematrice(w),_MATRIX__VECT);
      }
      else {
	vecteur res;
	if (s<l*c)
	  w=mergevecteur(w,vecteur(l*c-s));
	for (int i=0;i<l;++i)
	  res.push_back(vecteur(w.begin()+i*c,w.begin()+(i+1)*c));
	if (transpose)
	  res=mtran(res);
	return gen(makefreematrice(res),_MATRIX__VECT);
      }
    }
    // v[2] as a function, should take 2 args
    gen f=v[2];
    if (!f.is_symb_of_sommet(at_program))
      return gen(vecteur(l,vecteur(c,f)),_MATRIX__VECT);
    vecteur res(l);
    int decal=(xcas_mode(contextptr)!=0);
    for (int i=decal;i<l+decal;++i){
      vecteur tmp(c);
      for (int j=decal;j<c+decal;++j)
	tmp[j-decal]=f(gen(makevecteur(i,j),_SEQ__VECT),contextptr);
      res[i-decal]=tmp;
    }
    return gen(res,_MATRIX__VECT);
  }
  static const char _matrix_s []="matrix";
  static define_unary_function_eval (__matrix,&_matrix,_matrix_s);
  define_unary_function_ptr5( at_matrix ,alias_at_matrix,&__matrix,0,true);

  static string printasbreak(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38)
      return "BREAK ";
    if (xcas_mode(contextptr)==3)
      return "Exit ";
    else
      return sommetstr;
  }
  gen _break(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_break,0);
  }
  static const char _break_s []="break";
  static define_unary_function_eval2_index (104,__break,&_break,_break_s,&printasbreak);
  define_unary_function_ptr5( at_break ,alias_at_break ,&__break,0,T_BREAK);

  static string printascontinue(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38)
      return "CONTINUE ";
    if (xcas_mode(contextptr)==3)
      return "Cycle ";
    else
      return sommetstr;
  }
  gen _continue(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_continue,0);
  }
  static const char _continue_s []="continue";
  static define_unary_function_eval2_index (104,__continue,&_continue,_continue_s,&printascontinue);
  define_unary_function_ptr( at_continue ,alias_at_continue ,&__continue);

  static string printaslabel(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return "Lbl "+feuille.print(contextptr);
    else
      return "label "+feuille.print(contextptr);
  }
  gen _label(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_label,args);
  }
  static const char _label_s []="label";
  static define_unary_function_eval2 (__label,&_label,_label_s,&printaslabel);
  define_unary_function_ptr5( at_label ,alias_at_label ,&__label,0,T_RETURN);

  static string printasgoto(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return "Goto "+feuille.print(contextptr);
    else
      return "goto "+feuille.print(contextptr);
  }
  gen _goto(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return symbolic(at_goto,args);
  }
  static const char _goto_s []="goto";
  static define_unary_function_eval2 (__goto,&_goto,_goto_s,&printasgoto);
  define_unary_function_ptr5( at_goto ,alias_at_goto ,&__goto,0,T_RETURN);

  /*
  static vecteur local_vars(const vecteur & v,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res;
    for (;it!=itend;++it){
      if (it->type==_IDNT && 
	  (contextptr?contextptr->tabptr->find(*it->_IDNTptr->name)==contextptr->tabptr->end():(!it->_IDNTptr->localvalue || it->_IDNTptr->localvalue->empty()))
	  )
	res.push_back(*it);
    }
    return res;
  }
  */
  static string printastilocal(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return "invalid |";
    return '('+feuille._VECTptr->front().print(contextptr)+"|"+feuille._VECTptr->back().print(contextptr)+')';
  }
  gen _tilocal(const gen & args,const context * contextptr){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return symbolic(at_tilocal,args);
    vecteur & v=*args._VECTptr;
    // find local variables
    vecteur cond(gen2vecteur(v[1]));
    vecteur docond,vars;
    const_iterateur it=cond.begin(),itend=cond.end();
    for (;it!=itend;++it){
      if (it->type!=_SYMB)
	continue;
      unary_function_ptr & u=it->_SYMBptr->sommet;
      gen & g=it->_SYMBptr->feuille;
      if ( (g.type!=_VECT) || (g._VECTptr->empty()) )
	return gensizeerr(contextptr);
      if (u==at_equal){
	gen tmp=g._VECTptr->front();
	if (tmp.type==_IDNT){
	  gen tmp1(eval(tmp,eval_level(contextptr),contextptr));
	  if (tmp1.type==_IDNT)
	    tmp=tmp1;
	  tmp.subtype=0; // otherwise if inside a folder sto will affect tmp!
	  vars.push_back(tmp);
	}
	docond.push_back(symbolic(at_sto,gen(makevecteur(g._VECTptr->back(),tmp),_SEQ__VECT)));
	continue;
      }
      if (u==at_sto){
	if (g._VECTptr->back().type==_IDNT)
	  vars.push_back(g._VECTptr->back());
	docond.push_back(*it);
	continue;
      }
      if (g._VECTptr->front().type==_IDNT)
	vars.push_back(g._VECTptr->front());
      if (it->type==_SYMB && it->_SYMBptr->sommet!=at_superieur_strict && it->_SYMBptr->sommet!=at_superieur_egal && it->_SYMBptr->sommet!=at_inferieur_strict && it->_SYMBptr->sommet!=at_inferieur_egal &&it->_SYMBptr->sommet!=at_and)
	return gensizeerr(gettext("Invalid |"));
      docond.push_back(symbolic(at_assume,*it));
    }
    vecteur v0(vars.size(),zero);
    gen gv(v[0]);
    // Replace v[0] by its value if it is a global identifier
    if (gv.type==_IDNT){
      if (contextptr){
	sym_tab::const_iterator it=contextptr->tabptr->find(gv._IDNTptr->id_name),itend=contextptr->tabptr->end();
	if (it!=itend)
	  gv=it->second;
      }
      else {
	if (gv._IDNTptr->value)
	  gv=*gv._IDNTptr->value;
      }
    }
    /*
    // Replace local variables by their value in gv
    vecteur vname(*_lname(gv,contextptr)._VECTptr),docondvar(*_lname(docond,contextptr)._VECTptr);
    vecteur vval(vname);
    iterateur jt=vval.begin(),jtend=vval.end();
    for (;jt!=jtend;++jt){
      if (jt->type!=_IDNT || equalposcomp(docondvar,*jt))
	continue;
      if (contextptr){
	sym_tab::const_iterator kt=contextptr->tabptr->find(*jt->_IDNTptr->name);
	if (kt!=contextptr->tabptr->end())
	  *jt=kt->second;
      }
      else {
	if (jt->_IDNTptr->localvalue && !jt->_IDNTptr->localvalue->empty())
	  *jt=jt->_IDNTptr->localvalue->back();
      }
    }
    gv=quotesubst(gv,vname,vval,contextptr);
    */
    // Replace vars global IDNT by local IDNT
    vecteur vname=vars;
    iterateur jt=vname.begin(),jtend=vname.end();
    for (;jt!=jtend;++jt)
      jt->subtype=_GLOBAL__EVAL;
    vecteur vval=vars;
    jt=vval.begin(),jtend=vval.end();
    for (;jt!=jtend;++jt)
      jt->subtype=0;
    gv=quotesubst(gv,vname,vval,contextptr);
    docond=*quotesubst(docond,vname,vval,contextptr)._VECTptr;
    gen prg=symb_program(gen(vname,_SEQ__VECT),gen(v0,_SEQ__VECT),symb_bloc(makevecteur(docond,gv)),contextptr);
    return prg(gen(v0,_SEQ__VECT),contextptr);
  }
  static const char _tilocal_s []="|";
  static define_unary_function_eval4_index (103,__tilocal,&_tilocal,_tilocal_s,&printastilocal,&texprintsommetasoperator);
  define_unary_function_ptr5( at_tilocal ,alias_at_tilocal,&__tilocal,_QUOTE_ARGUMENTS,0);

  static string printasdialog(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return "Dialog "+symbolic(at_bloc,feuille).print(contextptr)+indent(contextptr)+"EndDialog";
  }  
  string printasinputform(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return printasdialog(feuille,sommetstr,contextptr);
    return sommetstr+("("+feuille.print(contextptr)+")");
  }  

  // Eval everything except IDNT and symbolics with
  vecteur inputform_pre_analysis(const gen & g,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    int s=v.size();
    for (int i=0;i<s;++i){
      if (v[i].type==_IDNT || v[i].type!=_SYMB)
	continue;
      unary_function_ptr & u =v[i]._SYMBptr->sommet;
      if ( (u==at_output) || (u==at_Text) || (u==at_Title) || (u==at_click) || (u==at_Request) || (u==at_choosebox) || (u==at_DropDown) || (u==at_Popup) || u==at_of || u==at_at)
	continue;
      v[i]=protecteval(v[i],eval_level(contextptr),contextptr);
    }
    return v;
  }
  gen inputform_post_analysis(const vecteur & v,const gen & res,GIAC_CONTEXT){
    return res.eval(eval_level(contextptr),contextptr);
  }
  // user input sent back to the parent process
  gen _inputform(const gen & args,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[1])
      return interactive_op_tab[1](args,contextptr);
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    string cs(gettext("inputform may be used in a window environment only"));
#if defined WIN32 || (!defined HAVE_SIGNAL_H_OLD)
    *logptr(contextptr) << cs << endl;
    return string2gen(cs,false);
#else
    if (child_id){ 
      *logptr(contextptr) << cs << endl;
      return string2gen(cs,false);
    }
    // pre-analysis
    vecteur v(gen2vecteur(args));
    // int vs=signed(v.size());
    gen res;
    // form
    ofstream child_out(cas_sortie_name().c_str());
    gen e(symbolic(at_inputform,args));
    *logptr(contextptr) << gettext("Archiving ") << e << endl;
    archive(child_out,e,contextptr);
    archive(child_out,e,contextptr);
    if ( (args.type==_VECT) && (args._VECTptr->empty()) )
      child_out << "User input requested\n" << '¤' ;
    else
      child_out << args << '¤' ;
    child_out.close();
    kill_and_wait_sigusr2();
    ifstream child_in(cas_entree_name().c_str());
    res= unarchive(child_in,contextptr);
    child_in.close();
    *logptr(contextptr) << gettext("Inputform reads ") << res << endl;
    // post analysis
    return inputform_post_analysis(v,res,contextptr);
#endif
  }
  static const char _inputform_s []="inputform";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__inputform,&_inputform,_inputform_s);
#else
  unary_function_eval __inputform(1,&giac::_inputform,_inputform_s,&printasinputform);
#endif
  define_unary_function_ptr5( at_inputform ,alias_at_inputform,&__inputform,_QUOTE_ARGUMENTS,true);

  gen _choosebox(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_choosebox,args),contextptr);
  }
  static const char _choosebox_s []="choosebox";
  static define_unary_function_eval_quoted (__choosebox,&giac::_choosebox,_choosebox_s);
  define_unary_function_ptr5( at_choosebox ,alias_at_choosebox,&__choosebox,_QUOTE_ARGUMENTS,true);

  gen _output(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_output,args),contextptr);
  }
  static const char _output_s []="output";
  static define_unary_function_eval_quoted (__output,&giac::_output,_output_s);
  define_unary_function_ptr5( at_output ,alias_at_output,&__output,_QUOTE_ARGUMENTS,true);

  gen _input(const gen & args,bool textinput,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    const_iterateur it=v.begin(),itend=v.end();
    if (it==itend)
      return __click.op(args,contextptr);
    gen res;
    for (;it!=itend;++it){
      if (it->type==_IDNT || it->is_symb_of_sommet(at_at) || it->is_symb_of_sommet(at_of)){
	if (textinput)
	  res=__click.op(makevecteur(string2gen(it->print(contextptr)),0,*it,1),contextptr);
	else
	  res=__click.op(makevecteur(string2gen(it->print(contextptr),false),0,*it),contextptr);
      }
      if (it+1==itend)
	break;
      if (it->type==_STRNG && ( (it+1)->type==_IDNT || (it+1)->is_symb_of_sommet(at_at) || (it+1)->is_symb_of_sommet(at_of))){
	if (textinput)
	  res=__click.op(makevecteur(*it,0,*(it+1),1),contextptr);
	else
	  res=__click.op(makevecteur(*it,0,*(it+1)),contextptr);
	++it;
      }
    }
    if (is_zero(res))
      return gensizeerr(contextptr);
    return res;
  }

  string printastifunction(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type==_VECT && feuille.subtype==_SEQ__VECT && feuille._VECTptr->empty())
      return string(sommetstr)+" ";
    return sommetstr+(" "+feuille.print(contextptr));
  }
  gen _Text(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_Text,args),contextptr);
  }
  static const char _Text_s []="Text";
  static define_unary_function_eval2_quoted (__Text,&giac::_Text,_Text_s,&printastifunction);
  define_unary_function_ptr5( at_Text ,alias_at_Text,&__Text,_QUOTE_ARGUMENTS,0);

  gen _Title(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_Title,args),contextptr);
  }
  static const char _Title_s []="Title";
  static define_unary_function_eval2_quoted (__Title,&giac::_Title,_Title_s,&printastifunction);
  define_unary_function_ptr5( at_Title ,alias_at_Title,&__Title,_QUOTE_ARGUMENTS,0);

  gen _Request(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_Request,args),contextptr);
  }
  static const char _Request_s []="Request";
  static define_unary_function_eval2_quoted (__Request,&giac::_Request,_Request_s,&printastifunction);
  define_unary_function_ptr5( at_Request ,alias_at_Request,&__Request,_QUOTE_ARGUMENTS,0);

  gen _DropDown(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_DropDown,args),contextptr);
  }
  static const char _DropDown_s []="DropDown";
  static define_unary_function_eval2_quoted (__DropDown,&giac::_DropDown,_DropDown_s,&printastifunction);
  define_unary_function_ptr5( at_DropDown ,alias_at_DropDown,&__DropDown,_QUOTE_ARGUMENTS,0);

  gen _Popup(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(symbolic(at_Popup,args),contextptr);
  }
  static const char _Popup_s []="Popup";
  static define_unary_function_eval2_quoted (__Popup,&giac::_Popup,_Popup_s,&printastifunction);
  define_unary_function_ptr5( at_Popup ,alias_at_Popup,&__Popup,_QUOTE_ARGUMENTS,0);

  gen _Dialog(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    return __inputform.op(args,contextptr);
  }
  static const char _Dialog_s []="Dialog";
  static define_unary_function_eval2_index (89,__Dialog,&giac::_Dialog,_Dialog_s,&printasdialog);
  define_unary_function_ptr5( at_Dialog ,alias_at_Dialog,&__Dialog,_QUOTE_ARGUMENTS,0);

  gen _expr(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2 && args._VECTptr->front().type==_STRNG && args._VECTptr->back().type==_INT_){
      int mode=args._VECTptr->back().val;
      bool rpnmode=mode<0;
      mode=absint(mode) % 256;
      if (mode>3)
	return gensizeerr(contextptr);
      int save_mode=xcas_mode(contextptr);
      bool save_rpnmode=rpn_mode(contextptr);
      xcas_mode(contextptr)=mode;
      rpn_mode(contextptr)=rpnmode;
      gen res=eval(gen(*args._VECTptr->front()._STRNGptr,contextptr),eval_level(contextptr),contextptr);
      xcas_mode(contextptr)=save_mode;
      rpn_mode(contextptr)=save_rpnmode;
      return res;
    }
    if (args.type!=_STRNG)
      return symbolic(at_expr,args);
    return eval(gen(*args._STRNGptr,contextptr),eval_level(contextptr),contextptr);
  }
  static const char _expr_s []="expr";
  static define_unary_function_eval (__expr,&giac::_expr,_expr_s);
  define_unary_function_ptr5( at_expr ,alias_at_expr,&__expr,0,true);

  static const char _execute_s []="execute";
  static define_unary_function_eval (__execute,&giac::_expr,_execute_s);
  define_unary_function_ptr5( at_execute ,alias_at_execute,&__execute,0,true);

  gen _string(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    string res;
    if (args.type==_VECT){
      const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
      for (;it!=itend;){
	if (it->type!=_STRNG){
	  res += it->print(contextptr);
	  ++it;
	  if (it!=itend)
	    res += ','; 
	  continue;
	}
	res += *it->_STRNGptr;
	++it;
	if (it==itend)
	  return string2gen(res,false);
	if (it->type==_STRNG)
	  res += '\n';
      }
    }
    else
      res=args.print(contextptr);
    return string2gen(res,false);
  }
  static const char _string_s []="string";
  static define_unary_function_eval (__string,&giac::_string,_string_s);
  define_unary_function_ptr5( at_string ,alias_at_string,&__string,0,true);

  gen _part(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type==_VECT) && args._VECTptr->size()==2 ){
      gen & i=args._VECTptr->back();
      gen & g=args._VECTptr->front();
      if (i.type!=_INT_ || i.val<=0){
	if (g.type!=_SYMB)
	  return string2gen(g.print(contextptr),false);
	else
	  return string2gen(g._SYMBptr->sommet.ptr()->s,false);
      }
      else {
	if (g.type!=_SYMB){
	  if (i.val!=1)
	    return gensizeerr(contextptr);
	  return g;
	}
	else {
	  vecteur v(gen2vecteur(g._SYMBptr->feuille));
	  if (signed(v.size())<i.val)
	    return gensizeerr(contextptr);
	  return v[i.val-1];
	}
      }
    }
    if (args.type==_SYMB)
      return int(gen2vecteur(args._SYMBptr->feuille).size());
    return 0;
  }
  static const char _part_s []="part";
  static define_unary_function_eval (__part,&giac::_part,_part_s);
  define_unary_function_ptr5( at_part ,alias_at_part,&__part,0,true);

  string tiasc_translate(const string & s){
    int l=s.size();
    string t("");
    for (int i=0;i<l;++i){
      char c=s[i];
      if (c=='\r')
	continue;
      if (c=='@'){
	t += "//";
	continue;
      }
      if (c=='\\'){
	++i;
	string ti_escape("");
	for (;i<l;++i){
	  char c=s[i];
	  if (c=='\\' || c==' '){
	    break;
	  }
	  ti_escape += c;
	}
	if (i==l || c==' ')
	  return t+"::"+ti_escape;
	if (ti_escape=="->"){
	  t += "=>";
	  continue;
	}
	if (ti_escape=="(C)"){ // comment
	  t += "//";
	  continue;
	}
	if (ti_escape=="(-)"){
	  t += '-';
	  continue;
	}
	if (ti_escape=="e"){
	  t += "exp(1)";
	  continue;
	}
	if (ti_escape=="i"){
	  t += '\xa1';
	  continue;
	}
	t += ti_escape;
      }
      else
	t += c;
    }
    if (t==string(t.size(),' '))
      return "";
    return t;
  }

  gen _Pause(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen g1=g;
    if (is_integer(g1) || g1.type==_REAL)
      g1=evalf_double(g,1,contextptr);
    if (g1.type==_DOUBLE_)
      usleep(int(g1._DOUBLE_val*1000000));
    else
      __interactive.op(symbolic(at_Pause,g),contextptr);
    return 0;
  }
  static const char _Pause_s []="Pause";
  static define_unary_function_eval2 (__Pause,&_Pause,_Pause_s,&printastifunction);
  define_unary_function_ptr5( at_Pause ,alias_at_Pause ,&__Pause,0,T_RETURN);

  static const char _DelVar_s []="DelVar";
  static define_unary_function_eval2_quoted (__DelVar,&_purge,_DelVar_s,&printastifunction);
  define_unary_function_ptr5( at_DelVar ,alias_at_DelVar,&__DelVar,_QUOTE_ARGUMENTS,T_RETURN);

  gen _Row(const gen & g,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[6])
      return interactive_op_tab[6](g,contextptr);
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    return spread_Row(contextptr);
  }
  static const char _Row_s []="Row";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__Row,&_Row,_Row_s);
#else
  unary_function_eval __Row(0,&_Row,_Row_s,&printastifunction);
#endif
  define_unary_function_ptr( at_Row ,alias_at_Row ,&__Row);

  gen _Col(const gen & g,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[7])
      return interactive_op_tab[7](g,contextptr);
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    return spread_Col(contextptr);
  }
  static const char _Col_s []="Col";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__Col,&_Col,_Col_s);
#else
  unary_function_eval __Col(0,&_Col,_Col_s,&printastifunction);
#endif
  define_unary_function_ptr( at_Col ,alias_at_Col ,&__Col);

  gen matrix_apply(const gen & a,const gen & b,gen (* f) (const gen &, const gen &) ){
    if (a.type!=_VECT || b.type!=_VECT || a._VECTptr->size()!=b._VECTptr->size())
      return apply(a,b,f);
    const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end(),jt=b._VECTptr->begin();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it,++jt){
      res.push_back(apply(*it,*jt,f));
    }
    return gen(res,a.subtype);
  }
  gen matrix_apply(const gen & a,const gen & b,GIAC_CONTEXT,gen (* f) (const gen &, const gen &,GIAC_CONTEXT) ){
    if (a.type!=_VECT || b.type!=_VECT || a._VECTptr->size()!=b._VECTptr->size())
      return apply(a,b,contextptr,f);
    const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end(),jt=b._VECTptr->begin();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it,++jt){
      res.push_back(apply(*it,*jt,contextptr,f));
    }
    return gen(res,a.subtype);
  }
  gen prod(const gen & a,const gen &b){
    return a*b;
  }
  gen somme(const gen & a,const gen &b){
    return a+b;
  }
  gen _pointprod(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen a,b;
    if (!check_binary(g,a,b))
      return a;
    return matrix_apply(a,b,contextptr,operator_times);
  }
  static const char _pointprod_s []=".*";
  static define_unary_function_eval4_index (92,__pointprod,&_pointprod,_pointprod_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_pointprod ,alias_at_pointprod ,&__pointprod);

  gen _pointdivision(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen a,b;
    if (!check_binary(g,a,b))
      return a;
    return matrix_apply(a,b,contextptr,rdiv);
  }
  static const char _pointdivision_s []="./";
  static define_unary_function_eval4_index (94,__pointdivision,&_pointdivision,_pointdivision_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_pointdivision ,alias_at_pointdivision ,&__pointdivision);

  gen giac_pow(const gen &,const gen &,GIAC_CONTEXT);
  gen pointpow(const gen & a,const gen &b,GIAC_CONTEXT){
    if (b.type!=_VECT && a.type==_VECT){
      return apply(a,b,contextptr,pointpow);
    }
    return matrix_apply(a,b,contextptr,giac_pow);
  }
  gen _pointpow(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    gen a,b;
    if (!check_binary(g,a,b))
      return a;
    return pointpow(a,b,contextptr);
  }
  static const char _pointpow_s []=".^";
  static define_unary_function_eval4_index (96,__pointpow,&_pointpow,_pointpow_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_pointpow ,alias_at_pointpow ,&__pointpow);

  string printassuffix(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return feuille.print(contextptr)+sommetstr;
  }  
  gen _pourcent(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    return rdiv(g,100,contextptr);
  }
  static const char _pourcent_s []="%";
  static define_unary_function_eval2_index (100,__pourcent,&_pourcent,_pourcent_s,&printassuffix);
  define_unary_function_ptr( at_pourcent ,alias_at_pourcent ,&__pourcent);

  gen _hash(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (g.type!=_STRNG)
      return g;
    return gen(*g._STRNGptr,contextptr);
  }
  static const char _hash_s []="#";
  static define_unary_function_eval_index (98,__hash,&_hash,_hash_s);
  define_unary_function_ptr( at_hash ,alias_at_hash ,&__hash);

  bool user_screen=false;
  int user_screen_io_x=0,user_screen_io_y=0;
  int user_screen_fontsize=14;
  gen _interactive(const gen & args,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[2])
      return interactive_op_tab[2](args,contextptr);
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
#if defined(WIN32) || !defined(HAVE_SIGNAL_H_OLD)
    return 0;
#else
    if (child_id)
      return 0;
    ofstream child_out(cas_sortie_name().c_str());
    gen e(symbolic(at_interactive,args));
    // *logptr(contextptr) << e << endl;
    archive(child_out,e,contextptr);
    archive(child_out,e,contextptr);
    child_out.close();
    kill_and_wait_sigusr2();
    ifstream child_in(cas_entree_name().c_str());
    gen res= unarchive(child_in,contextptr);
    child_in.close();
    return res;
#endif
  }
  static const char _interactive_s []="interactive";
#ifdef RTOS_THREADX
  define_unary_function_eval_index(1,__interactive,&_interactive,_interactive_s);
  // const unary_function_eval __interactive(1,&_interactive,_interactive_s);
#else
  unary_function_eval __interactive(1,&_interactive,_interactive_s);
#endif
  define_unary_function_ptr5( at_interactive ,alias_at_interactive,&__interactive,_QUOTE_ARGUMENTS,true);

  // v=[ [idnt,value] ... ]
  // search g in v if found return value
  // else return g unevaluated
  gen find_in_folder(vecteur & v,const gen & g){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT || it->_VECTptr->size()!=2)
	continue;
      vecteur & w=*it->_VECTptr;
      if (w[0]==g)
	return w[1];
    }
    return g;
  }

  gen _ti_semi(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return symbolic(at_ti_semi,args);
    vecteur & v=*args._VECTptr;
    matrice m1,m2;
    if (!ckmatrix(v[0])){
      if (v[0].type==_VECT)
	m1=vecteur(1,*v[0]._VECTptr);
      else
	m1=vecteur(1,vecteur(1,v[0]));
    }
    else
      m1=*v[0]._VECTptr;
    if (!ckmatrix(v[1])){
      if (v[1].type==_VECT)
	m2=vecteur(1,*v[1]._VECTptr);
      else
	m2=vecteur(1,vecteur(1,v[1]));
    }
    else
      m2=*v[1]._VECTptr;
    // *logptr(contextptr) << m1 << " " << m2 << endl;
    return mergevecteur(m1,m2); 
  }
  static const char _ti_semi_s []=";";
  static define_unary_function_eval4 (__ti_semi,&_ti_semi,_ti_semi_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_ti_semi ,alias_at_ti_semi ,&__ti_semi);

  gen widget_size(const gen & g,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[9])
      return interactive_op_tab[9](g,contextptr);
    return zero;
  }
  static const char _widget_size_s []="widget_size";
  // const string _widget_size_s ="widget_size";
#ifdef RTOS_THREADX
  define_unary_function_eval(__widget_size,&widget_size,_widget_size_s);
  // const unary_function_eval __widget_size(0,&widget_size,_widget_size_s);
#else
  unary_function_eval __widget_size(0,&widget_size,_widget_size_s);
#endif
  define_unary_function_ptr5( at_widget_size ,alias_at_widget_size,&__widget_size,0,true);

  gen keyboard(const gen & g,GIAC_CONTEXT){
    return zero;
  }
  static const char _keyboard_s []="keyboard";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__keyboard,&keyboard,_keyboard_s);
  // const unary_function_eval __keyboard(0,&keyboard,_keyboard_s);
#else
  unary_function_eval __keyboard(0,&keyboard,_keyboard_s);
#endif
  define_unary_function_ptr5( at_keyboard ,alias_at_keyboard,&__keyboard,0,true);

  gen current_sheet(const gen & g,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[5])
      return interactive_op_tab[5](g,contextptr);
    return zero;
  }
  static const char _current_sheet_s []="current_sheet";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__current_sheet,&current_sheet,_current_sheet_s);
#else
  unary_function_eval __current_sheet(1,&current_sheet,_current_sheet_s);
#endif
  define_unary_function_ptr5( at_current_sheet ,alias_at_current_sheet,&__current_sheet,_QUOTE_ARGUMENTS,true);
  
  static string printasmaple_lib(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT || feuille._VECTptr->size()!=2)
      return "Error printasmaple_lib";
    vecteur & v=*feuille._VECTptr;
    return v[0].print(contextptr)+"["+v[1].print(contextptr)+"]";
  }
  gen maple_lib(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    return v[1];
  }
  static const char _maple_lib_s[]="maple_lib";
#ifdef RTOS_THREADX
  static define_unary_function_eval2_index(110,__maple_lib,&maple_lib,_maple_lib_s,&printasmaple_lib);
#else
  const unary_function_eval __maple_lib(110,&maple_lib,_maple_lib_s,&printasmaple_lib);
#endif
  define_unary_function_ptr( at_maple_lib ,alias_at_maple_lib ,&__maple_lib);

  gen window_switch(const gen & g,GIAC_CONTEXT){
    return zero; // defined by GUI handler
  }
  static const char _window_switch_s []="window_switch";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__window_switch,&window_switch,_window_switch_s);
#else
  const unary_function_eval __window_switch(0,&window_switch,_window_switch_s);
#endif
  define_unary_function_ptr( at_window_switch ,alias_at_window_switch ,&__window_switch);


  // Funcion has been changed -> simplify
  static const char _simplifier_s []="simplifier";
  static define_unary_function_eval (__simplifier,&_simplify,_simplifier_s);
  define_unary_function_ptr5( at_simplifier ,alias_at_simplifier,&__simplifier,0,true);

  static const char _regrouper_s []="regroup";
  static define_unary_function_eval (__regrouper,&_simplifier,_regrouper_s);
  define_unary_function_ptr5( at_regrouper ,alias_at_regrouper,&__regrouper,0,true);

  gen find_or_make_symbol(const string & s,bool check38,GIAC_CONTEXT){
    gen tmp;
    find_or_make_symbol(s,tmp,0,check38,contextptr);
    return tmp;
  }

  // To each unit we associate a number and a vector of powers of kg, m, s
  /*
  map_charptr_gen unit_conversion_map;
  gen mksa_register(const char * s,const gen & equiv){
    unit_conversion_map[s+1]=equiv;
    return find_or_make_symbol(s,false,context0);
  }
  */
  std::map<const char *, const mksa_unit *,ltstr> & unit_conversion_map(){
    static std::map<const char *, const mksa_unit *,ltstr> * unit_conversion_mapptr=0;
    if (!unit_conversion_mapptr)
      unit_conversion_mapptr=new std::map<const char *, const mksa_unit *,ltstr>;
    return *unit_conversion_mapptr;
  }
  gen mksa_register(const char * s,const mksa_unit * equiv){
    std::map<const char *, const mksa_unit *,ltstr>::const_iterator it=unit_conversion_map().find(s+1),itend=unit_conversion_map().end();
    if (it!=itend)
      return syms()[s];
    unit_conversion_map()[s+1]=equiv;
    return (syms()[s] = new ref_identificateur(s));
  }
  gen mksa_register_unit(const char * s,const mksa_unit * equiv){
    return symbolic(at_unit,makevecteur(1,mksa_register(s,equiv)));
  }
  // fundemental metric units
  const mksa_unit __m_unit={1,1,0,0,0,0,0,0,0};
  const mksa_unit __kg_unit={1,0,1,0,0,0,0,0,0};
  const mksa_unit __s_unit={1,0,0,1,0,0,0,0,0};
  const mksa_unit __A_unit={1,0,0,0,1,0,0,0,0};
  const mksa_unit __K_unit={1,0,0,0,0,1,0,0,0};
  const mksa_unit __mol_unit={1,0,0,0,0,0,1,0,0};
  const mksa_unit __cd_unit={1,0,0,0,0,0,0,1,0};
  const mksa_unit __E_unit={1,0,0,0,0,0,0,0,1};
  const mksa_unit __Bq_unit={1,0,0,-1,0,0,0,0,0};
  const mksa_unit __C_unit={1,0,0,1,1,0,0,0,0};
  const mksa_unit __F_unit={1,-2,-1,4,2,0,0,0,0};
  const mksa_unit __Gy_unit={1,2,0,-2,0,0,0,0,0};
  const mksa_unit __H_unit={1,2,1,-2,-2,0,0,0,0};
  const mksa_unit __Hz_unit={1,0,0,-1,0,0,0,0,0};
  const mksa_unit __J_unit={1,2,1,-2,0,0,0,0,0};
  const mksa_unit __mho_unit={1,-2,-1,3,2,0,0,0,0};
  const mksa_unit __N_unit={1,1,1,-2,0,0,0,0,0};
  const mksa_unit __Ohm_unit={1,2,1,-3,-2,0,0,0,0};
  const mksa_unit __Pa_unit={1,-1,1,-2,0,0,0,0,0};
  const mksa_unit __rad_unit={1,0,0,0,0,0,0,0,0};
  const mksa_unit __S_unit={1,-2,-1,3,2,0,0,0,0};
  const mksa_unit __Sv_unit={1,2,0,-2,0,0,0,0,0};
  const mksa_unit __T_unit={1,0,1,-2,-1,0,0,0,0};
  const mksa_unit __V_unit={1,2,1,-3,-1,0,0,0,0};
  const mksa_unit __W_unit={1,2,1,-3,0,0,0,0,0};
  const mksa_unit __Wb_unit={1,2,1,-2,-1,0,0,0,0};
  vecteur & usual_units(){
    static vecteur * usual_units_ptr=0;
    if (!usual_units_ptr){
      usual_units_ptr=new vecteur;
      *usual_units_ptr=mergevecteur(
				   mergevecteur(makevecteur(_C_unit,_F_unit,_Gy_unit,_H_unit,_Hz_unit,_J_unit,_mho_unit),
						makevecteur(_N_unit,_Ohm_unit,_Pa_unit,_rad_unit,_S_unit,_Sv_unit,_T_unit)),
				   makevecteur(_V_unit,_W_unit,_Wb_unit)
				   );
    }
    return *usual_units_ptr;
  }
  const mksa_unit __Angstrom_unit={1e-10,1,0,0,0,0,0,0,0};
  const mksa_unit __Btu_unit={1055.05585262,2,1,-2,0,0,0,0,0};
  const mksa_unit __Curie_unit={3.7e10,0,0,-1,0,0,0,0,0};
  const mksa_unit __FF_unit={.152449017237,0,0,0,0,0,0,0,1};
  const mksa_unit __Fdy_unit={96487,0,0,1,1,0,0,0,0};
  const mksa_unit __Gal={0.01,1,0,-2,0,0,0,0,0};
  const mksa_unit __HFCC_unit={1400,1,0,0,0,0,0,0,0};
  const mksa_unit __L_unit={0.001,3,0,0,0,0,0,0,0};
  const mksa_unit __P_unit={.1,-1,1,-1,0,0,0,0,0};
  const mksa_unit __R_unit={0.000258,0,-1,1,1,0,0,0,0};
  const mksa_unit __Rankine_unit={5./9,0,0,0,0,1,0,0,0};
  const mksa_unit __St_unit={0.0001,2,0,-1,0,0,0,0,0};
  const mksa_unit __Wh_unit={3600,2,1,-2,0,0,0,0,0};
  const mksa_unit __a_unit={100,2,0,0,0,0,0,0,0};
  const mksa_unit __acre_unit={4046.87260987,2,0,0,0,0,0,0,0};
  const mksa_unit __arcmin_unit={2.90888208666,0,0,0,0,0,0,0,0};
  const mksa_unit __arcs_unit={4.8481368111,0,0,0,0,0,0,0,0};
  const mksa_unit __atm_unit={101325.0,-1,1,-2,0,0,0,0,0};
  const mksa_unit __au_unit={1.495979e11,1,0,0,0,0,0,0,0};
  const mksa_unit __b_unit={1e-28,2,0,0,0,0,0,0,0};
  const mksa_unit __bar_unit={1e5,-1,1,-2,0,0,0,0,0};
  const mksa_unit __bbl_unit={.158987294928,3,0,0,0,0,0,0,0};
  const mksa_unit __bblep_unit={.158987294928*0.857*41.76e9,2,1,-2,0,0,0,0,0};
  const mksa_unit __boe_unit={.158987294928*0.857*41.76e9,2,1,-2,0,0,0,0,0};
  const mksa_unit __bu={0.036368736,3,0,0,0,0,0,0,0};
  const mksa_unit __buUS={0.03523907,3,0,0,0,0,0,0,0};
  const mksa_unit __cal_unit={4.1868,2,1,-2,0,0,0,0,0};
  const mksa_unit __cf_unit={1.08e6,2,1,-2,0,0,0,0,0};
  const mksa_unit __chain_unit={20.1168402337,1,0,0,0,0,0,0,0};
  const mksa_unit __ct_unit={0.0002,0,1,0,0,0,0,0,0};
  const mksa_unit __dB_unit={1,0,0,0,0,0,0,0,0};
  const mksa_unit __d_unit={86400,0,0,1,0,0,0,0,0};
  const mksa_unit __deg_unit={1.74532925199e-2,0,0,0,0,0,0,0,0};
  // const mksa_unit __degreeF_unit={5./9,0,0,0,0,1,0,0,0};
  const mksa_unit __dyn_unit={1e-5,1,1,-2,0,0,0,0,0};
  const mksa_unit __eV_unit={1.60217733e-19,2,1,-2,0,0,0,0,0};
  const mksa_unit __erg_unit={1e-7,2,1,-2,0,0,0,0,0};
  const mksa_unit __fath_unit={1.82880365761,1,0,0,0,0,0,0,0};
  const mksa_unit __fbm_unit={0.002359737216,3,0,0,0,0,0,0,0};
  const mksa_unit __fc_unit={10.7639104167,1,0,0,0,0,0,0,0};
  const mksa_unit __fermi_unit={1e-15,1,0,0,0,0,0,0,0};
  const mksa_unit __flam_unit={3.42625909964,-2,0,0,0,0,0,1,0};
  const mksa_unit __fm_unit={1.82880365761,1,0,0,0,0,0,0,0};
  const mksa_unit __ft_unit={0.3048,1,0,0,0,0,0,0,0};
  const mksa_unit __ftUS_unit={0.304800609601,1,0,0,0,0,0,0,0};
  const mksa_unit __g_unit={1e-3,0,1,0,0,0,0,0,0};
  const mksa_unit __galC_unit={0.00454609,3,0,0,0,0,0,0,0};
  const mksa_unit __galUK_unit={0.004546092,3,0,0,0,0,0,0,0};
  const mksa_unit __galUS_unit={0.003785411784,3,0,0,0,0,0,0,0};
  const mksa_unit __cu_unit={0.000236588236373,3,0,0,0,0,0,0,0};
  const mksa_unit __gf_unit={0.00980665,1,1,-2,0,0,0,0,0};
  const mksa_unit __gmol_unit={1,0,0,0,0,0,1,0,0};
  const mksa_unit __gon_unit={1.57079632679e-2,0,0,0,0,0,0,0};
  const mksa_unit __grad_unit={1.57079632679e-2,0,0,0,0,0,0,0,0};
  const mksa_unit __grain_unit={0.00006479891,0,1,0,0,0,0,0,0};
  const mksa_unit __h_unit={3600,0,0,1,0,0,0,0,0};
  const mksa_unit __ha_unit={10000,2,0,0,0,0,0,0,0};
  const mksa_unit __hp_unit={745.699871582,2,1,-3,0,0,0,0,0};
  const mksa_unit __in_unit={0.0254,1,0,0,0,0,0,0,0};
  const mksa_unit __inH2O_unit={248.84,-1,1,-2,0,0,0,0,0};
  const mksa_unit __inHg_unit={3386.38815789,-1,1,-2,0,0,0,0,0};
  const mksa_unit __j_unit={86400,0,0,1,0,0,0,0,0};
  const mksa_unit __kip_unit={4448.22161526,1,1,-2,0,0,0,0,0};
  const mksa_unit __knot_unit={0.51444444444,1,0,-1,0,0,0,0,0};
  const mksa_unit __kph_unit={0.2777777777777,1,0,-1,0,0,0,0,0};
  const mksa_unit __l_unit={0.001,3,0,0,0,0,0,0,0};
  const mksa_unit __lam_unit={3183.09886184,-2,0,0,0,0,0,1,0};
  const mksa_unit __lb_unit={0.45359237,0,1,0,0,0,0,0,0};
  const mksa_unit __lbf_unit={4.44922161526,1,1,-2,0,0,0,0,0};
  const mksa_unit __lbmol_unit={453.59237,0,0,0,0,0,1,0,0};
  const mksa_unit __lbt_unit={0.3732417216,0,1,0,0,0,0,0,0};
  const mksa_unit __lep_unit={0.857*41.76e6,2,1,-2,0,0,0,0,0};
  const mksa_unit __liqpt_unit={0.000473176473,3,0,0,0,0,0,0,0};
  const mksa_unit __lyr_unit={9.46052840488e15,1,0,0,0,0,0,0,0};
  const mksa_unit __mi_unit={1609.344,1,0,0,0,0,0,0,0};
  const mksa_unit __miUS_unit={1609.34721869,1,0,0,0,0,0,0,0};
  const mksa_unit __mil_unit={0.0000254,1,0,0,0,0,0,0,0};
  const mksa_unit __mile_unit={1609.344,1,0,0,0,0,0,0,0};
  const mksa_unit __mille_unit={1852,1,0,0,0,0,0,0,0};
  const mksa_unit __mn_unit={60,0,0,1,0,0,0,0,0};
  const mksa_unit __mmHg_unit={133.322368421,-1,1,-2,0,0,0,0,0};
  const mksa_unit __molK_unit={1,0,0,0,0,1,1,0,0};
  const mksa_unit __mph_unit={0.44704,1,0,-1,0,0,0,0,0};
  const mksa_unit __nmi_unit={1852,1,0,0,0,0,0,0,0};
  const mksa_unit __oz_unit={0.028349523125,0,1,0,0,0,0,0,0};
  const mksa_unit __ozUK_unit={2.8413075e-5,3,0,0,0,0,0,0,0};
  const mksa_unit __ozfl_unit={2.95735295625e-5,3,0,0,0,0,0,0,0};
  const mksa_unit __ozt_unit={0.0311034768,0,1,0,0,0,0,0,0};
  const mksa_unit __pc_unit={3.08567818585e16,1,0,0,0,0,0,0,0};
  const mksa_unit __pdl_unit={0.138254954376,1,1,-2,0,0,0,0,0};
  const mksa_unit __pk_unit={0.0088097675,3,0,0,0,0,0,0,0};
  const mksa_unit __psi_unit={6894.75729317,-1,1,-2,0,0,0,0,0};
  const mksa_unit __pt_unit={0.000473176473,3,0,0,0,0,0,0,0};
  const mksa_unit __ptUK_unit={0.0005682615,3,0,0,0,0,0,0,0};
  const mksa_unit __qt_unit={0.000946359246,3,0,0,0,0,0,0,0};
  const mksa_unit __rd_unit={0.01,2,0,-2,0,0,0,0,0};
  const mksa_unit __rem_unit={0.01,2,0,-2,0,0,0,0,0};
  const mksa_unit __rod_unit={5.02921005842,1,0,0,0,0,0,0,0};
  const mksa_unit __rpm_unit={0.0166666666667,0,0,-1,0,0,0,0,0};
  const mksa_unit __sb_unit={10000,-2,0,0,0,0,0,1,0};
  const mksa_unit __slug_unit={14.5939029372,0,1,0,0,0,0,0,0};
  const mksa_unit __st_unit={1,3,0,0,0,0,0,0,0};
  const mksa_unit __t_unit={1000,0,1,0,0,0,0,0,0};
  const mksa_unit __tbsp_unit={1.47867647813e-5,3,0,0,0,0,0,0,0};
  const mksa_unit __tec_unit={41.76e9/1.5,2,1,-2,0,0,0,0,0};
  const mksa_unit __tep_unit={41.76e9,2,1,-2,0,0,0,0,0};
  const mksa_unit __tepC_unit={830,1,0,0,0,0,0,0,0};
  const mksa_unit __tepcC_unit={1000,1,0,0,0,0,0,0,0};
  const mksa_unit __tepgC_unit={650,1,0,0,0,0,0,0,0};
  const mksa_unit __tex={1e-6,-1,1,0,0,0,0,0,0};
  const mksa_unit __therm_unit={105506000,2,1,-2,0,0,0,0,0};
  const mksa_unit __toe_unit={41.76e9,2,1,-2,0,0,0,0,0};
  const mksa_unit __ton_unit={907.18474,0,1,0,0,0,0,0,0};
  const mksa_unit __tonUK_unit={1016.0469088,0,1,0,0,0,0,0,0};
  const mksa_unit __torr_unit={133.322368421,-1,1,-2,0,0,0,0,0};
  const mksa_unit __tr_unit={2*M_PI,0,0,0,0,0,0,0,0};
  const mksa_unit __tsp_unit={4.928921614571597e-6,3,0,0,0,0,0,0,0};
  const mksa_unit __u_unit={1.6605402e-27,0,1,0,0,0,0,0,0};
  const mksa_unit __yd_unit={0.9144,1,0,0,0,0,0,0,0};
  const mksa_unit __yr_unit={31556925.9747,0,0,1,0,0,0,0,0};
  const mksa_unit __micron_unit={1e-6,1,0,0,0,0,0,0,0};

  const mksa_unit __hbar_unit={1.05457266e-34,2,1,-1,0,0,0,0};        
  const mksa_unit __c_unit={299792458,1,0,-1,0,0,0,0};        
  const mksa_unit __g__unit={9.80665,1,0,-2,0,0,0,0};       
  const mksa_unit __IO_unit={1e-12,0,1,-3,0,0,0,0}; 
  const mksa_unit __epsilonox_unit={3.9,0,0,0,0,0,0,0}; 
  const mksa_unit __epsilonsi_unit={11.9,0,0,0,0,0,0,0,0}; 
  const mksa_unit __qepsilon0_unit={1.4185979e-30,-3,-1,5,3,0,0,0}; 
  const mksa_unit __epsilon0q_unit={55263469.6,-3,-1,3,1,0,0,0}; 
  const mksa_unit __kq_unit={8.617386e-5,2,1,-3,-1,-1,0,0}; 
  const mksa_unit __c3_unit={.002897756,1,0,0,0,1,0,0}; 
  const mksa_unit __lambdac_unit={ 0.00242631058e-9,1,0,0,0,0,0,0,0}; 
  const mksa_unit __f0_unit={2.4179883e14,0,0,-1,0,0,0,0}; 
  const mksa_unit __lambda0_unit={1239.8425e-9,1,0,0,0,0,0,0}; 
  const mksa_unit __muN_unit={5.0507866e-27,2,0,0,1,0,0,0}; 
  const mksa_unit __muB_unit={ 9.2740154e-24,2,0,0,1,0,0,0}; 
  const mksa_unit __a0_unit={.0529177249e-9,1,0,0,0,0,0,0}; 
  const mksa_unit __Rinfinity_unit={10973731.534,-1,0,0,0,0,0,0}; 
  const mksa_unit __Faraday_unit={96485.309,0,0,1,1,0,-1,0}; 
  const mksa_unit __phi_unit={2.06783461e-15,2,1,-2,-1,0,0,0};
  const mksa_unit __alpha_unit={7.29735308e-3,0,0,0,0,0,0,0}; 
  const mksa_unit __mpme_unit={1836.152701,0,0,0,0,0,0,0}; 
  const mksa_unit __mp_unit={1.6726231e-27,0,1,0,0,0,0,0}; 
  const mksa_unit __qme_unit={1.75881962e11,0,-1,1,1,0,0,0};
  const mksa_unit __me_unit={9.1093897e-31,0,1,0,0,0,0,0}; 
  const mksa_unit __qe_unit={1.60217733e-19,0,0,1,1,0,0,0};
  const mksa_unit __h__unit={6.6260755e-34,2,1,-1,0,0,0,0}; 
  const mksa_unit __G_unit={6.67259e-11,3,-1,-2,0,0,0,0}; 
  const mksa_unit __mu0_unit={1.25663706144e-6,1,1,-2,-2,0,0,0}; 
  const mksa_unit __epsilon0_unit={8.85418781761e-12,-3,-1,4,2,0,0,0}; 
  const mksa_unit __sigma_unit={ 5.67051e-8,0,1,-3,0,-4,0,0}; 
  const mksa_unit __StdP_unit={101325.0,-1,1,-2,0,0,0,0}; 
  const mksa_unit __StdT_unit={273.15,0,0,0,0,1,0,0}; 
  const mksa_unit __R__unit={8.31451,2,1,-2,0,-1,-1,0}; 
  const mksa_unit __Vm_unit={22.4141e-3,3,0,0,0,0,-1,0}; 
  const mksa_unit __k_unit={1.380658e-23,2,1,-2,0,-1,0,0}; 
  const mksa_unit __NA_unit={6.0221367e23,0,0,0,0,0,-1,0}; 
  const mksa_unit __mSun_unit={1.989e30,0,1,0,0,0,0,0}; 
  const mksa_unit __RSun_unit={6.955e8,1,0,0,0,0,0,0}; 
  const mksa_unit __PSun_unit={3.846e26,2,1,-3,0,0,0,0}; 
  const mksa_unit __mEarth_unit={5.9736e24,0,1,0,0,0,0,0}; 
  const mksa_unit __REarth_unit={6.371e6,1,0,0,0,0,0,0}; 
  const mksa_unit __sd_unit={8.61640905e4,0,0,1,0,0,0,0}; 
  const mksa_unit __syr_unit={3.15581498e7,0,0,1,0,0,0,0}; 

  // table of alpha-sorted units
  const mksa_unit * const unitptr_tab[]={
    &__A_unit,
    &__Angstrom_unit,
    &__Bq_unit,
    &__Btu_unit,
    &__C_unit,
    &__Curie_unit,
    &__E_unit,
    &__F_unit,
    &__FF_unit,
    &__Faraday_unit,
    &__Fdy_unit,
    &__G_unit,
    &__Gal,
    &__Gy_unit,
    &__H_unit,
    &__HFCC_unit,
    &__Hz_unit,
    &__IO_unit,
    &__J_unit,
    &__K_unit,
    &__L_unit,
    &__N_unit,
    &__NA_unit,
    &__Ohm_unit,
    &__P_unit,
    &__PSun_unit,
    &__Pa_unit,
    &__R_unit,
    &__REarth_unit,
    &__RSun_unit,
    &__R__unit,
    &__Rankine_unit,
    &__Rinfinity_unit,
    &__S_unit,
    &__St_unit,
    &__StdP_unit,
    &__StdT_unit,
    &__Sv_unit,
    &__T_unit,
    &__V_unit,
    &__Vm_unit,
    &__W_unit,
    &__Wb_unit,
    &__Wh_unit,
    &__a_unit,
    &__a0_unit,
    &__acre_unit,
    &__alpha_unit,
    &__arcmin_unit,
    &__arcs_unit,
    &__atm_unit,
    &__au_unit,
    &__b_unit,
    &__bar_unit,
    &__bbl_unit,
    &__bblep_unit,
    &__boe_unit,
    &__bu,
    &__buUS,
    &__c3_unit,
    &__c_unit,
    &__cal_unit,
    &__cd_unit,
    &__cf_unit,
    &__chain_unit,
    &__ct_unit,
    &__cu_unit,
    &__d_unit,
    &__dB_unit,
    &__deg_unit,
    // &__degreeF_unit,
    &__dyn_unit,
    &__eV_unit,
    &__epsilon0_unit,
    &__epsilon0q_unit,
    &__epsilonox_unit,
    &__epsilonsi_unit,
    &__erg_unit,
    &__f0_unit,
    &__fath_unit,
    &__fbm_unit,
    &__fc_unit,
    &__fermi_unit,
    &__flam_unit,
    &__fm_unit,
    &__ft_unit,
    &__ftUS_unit,
    &__g_unit,
    &__g__unit,
    &__galC_unit,
    &__galUK_unit,
    &__galUS_unit,
    &__gf_unit,
    &__gmol_unit,
    &__gon_unit,
    &__grad_unit,
    &__grain_unit,
    &__h_unit,
    &__h__unit,
    &__ha_unit,
    &__hbar_unit,
    &__hp_unit,
    &__inH2O_unit,
    &__inHg_unit,
    &__in_unit,
    &__j_unit,
    &__k_unit,
    &__kg_unit,
    &__kip_unit,
    &__knot_unit,
    &__kph_unit,
    &__kq_unit,
    &__l_unit,
    &__lam_unit,
    &__lambda0_unit,
    &__lambdac_unit,
    &__lb_unit,
    &__lbf_unit,
    &__lbmol_unit,
    &__lbt_unit,
    &__lep_unit,
    &__liqpt_unit,
    &__lyr_unit,
    &__m_unit,
    &__mEarth_unit,
    &__mSun_unit,
    &__me_unit,
    &__mho_unit,
    &__mi_unit,
    &__miUS_unit,
    &__mil_unit,
    &__mile_unit,
    &__mille_unit,
    &__mn_unit,
    &__mmHg_unit,
    &__mn_unit,
    &__mol_unit,
    &__molK_unit,
    &__mp_unit,
    &__mph_unit,
    &__mpme_unit,
    &__mu0_unit,
    &__muB_unit,
    &__muN_unit,
    &__nmi_unit,
    &__oz_unit,
    &__ozUK_unit,
    &__ozfl_unit,
    &__ozt_unit,
    &__pc_unit,
    &__pdl_unit,
    &__phi_unit,
    &__pk_unit,
    &__psi_unit,
    &__pt_unit,
    &__ptUK_unit,
    &__qe_unit,
    &__qepsilon0_unit,
    &__qme_unit,
    &__qt_unit,
    &__rad_unit,
    &__rd_unit,
    &__rem_unit,
    &__rod_unit,
    &__rpm_unit,
    &__s_unit,
    &__sb_unit,
    &__sd_unit,
    &__sigma_unit,
    &__slug_unit,
    &__st_unit,
    &__syr_unit,
    &__t_unit,
    &__tbsp_unit,
    &__tec_unit,
    &__tep_unit,
    &__tepC_unit,
    &__tepcC_unit,
    &__tepgC_unit,
    &__tex,
    &__therm_unit,
    &__toe_unit,
    &__tonUK_unit,
    &__ton_unit,
    &__torr_unit,
    &__tr_unit,
    &__tsp_unit,
    &__u_unit,
    &__yd_unit,
    &__yr_unit,
    &__micron_unit
  };
  const unsigned unitptr_tab_length=sizeof(unitptr_tab)/sizeof(mksa_unit *);
  const char * const unitname_tab[]={
    "_A",
    "_Angstrom",
    "_Bq",
    "_Btu",
    "_C",
    "_Curie",
    "_E",
    "_F",
    "_FF",
    "_Faraday_",
    "_Fdy",
    "_G_",
    "_Gal",
    "_Gy",
    "_H",
    "_HFCC",
    "_Hz",
    "_IO_",
    "_J",
    "_K",
    "_L",
    "_N",
    "_NA_",
    "_Ohm",
    "_P",
    "_PSun_",
    "_Pa",
    "_R",
    "_REarth_",
    "_RSun_",
    "_R_",
    "_Rankine",
    "_Rinfinity_",
    "_S",
    "_St",
    "_StdP_",
    "_StdT_",
    "_Sv",
    "_T",
    "_V",
    "_Vm_",
    "_W",
    "_Wb",
    "_Wh",
    "_a",
    "_a0_",
    "_acre",
    "_alpha_",
    "_arcmin",
    "_arcs",
    "_atm",
    "_au",
    "_b",
    "_bar",
    "_bbl",
    "_bblep",
    "_boe",
    "_bu",
    "_buUS",
    "_c3_",
    "_c_",
    "_cal",
    "_cd",
    "_cf",
    "_chain",
    "_ct",
    "_cu",
    "_d",
    "_dB",
    "_deg",
    // "_degreeF",
    "_dyn",
    "_eV",
    "_epsilon0_",
    "_epsilon0q_",
    "_epsilonox_",
    "_epsilonsi_",
    "_erg",
    "_f0_",
    "_fath",
    "_fbm",
    "_fc",
    "_fermi",
    "_flam",
    "_fm",
    "_ft",
    "_ftUS",
    "_g",
    "_g_",
    "_galC",
    "_galUK",
    "_galUS",
    "_gf",
    "_gmol",
    "_gon",
    "_grad",
    "_grain",
    "_h",
    "_h_",
    "_ha",
    "_hbar_",
    "_hp",
    "_inH2O",
    "_inHg",
    "_inch"       ,
    "_j",
    "_k_",
    "_kg",
    "_kip",
    "_knot",
    "_kph",
    "_kq_",
    "_l",
    "_lam",
    "_lambda0_",
    "_lambdac_",
    "_lb",
    "_lbf",
    "_lbmol",
    "_lbt",
    "_lep",
    "_liqpt",
    "_lyr",
    "_m",
    "_mEarth_",
    "_mSun_",
    "_me_",
    "_mho",
    "_mi",
    "_miUS",
    "_mil",
    "_mile",
    "_mille",
    "_min",
    "_mmHg",
    "_mn",
    "_mol",
    "_molK",
    "_mp_",
    "_mph",
    "_mpme_",
    "_mu0_",
    "_muB_",
    "_muN_",
    "_nmi",
    "_oz",
    "_ozUK",
    "_ozfl",
    "_ozt",
    "_pc",
    "_pdl",
    "_phi_",
    "_pk",
    "_psi",
    "_pt",
    "_ptUK",
    "_qe_",
    "_qepsilon0_",
    "_qme_",
    "_qt",
    "_rad",
    "_rd",
    "_rem",
    "_rod",
    "_rpm",
    "_s",
    "_sb",
    "_sd_",
    "_sigma_",
    "_slug",
    "_st",
    "_syr_",
    "_t",
    "_tbsp",
    "_tec",
    "_tep",
    "_tepC",
    "_tepcC",
    "_tepgC",
    "_tex",
    "_therm",
    "_toe",
    "_ton",
    "_tonUK",
    "_torr",
    "_tr",
    "_tsp",
    "_u",
    "_yd",
    "_yr",
    "_µ"
  };

  const char * const * const unitname_tab_end=unitname_tab+unitptr_tab_length;
#ifndef NO_PHYSICAL_CONSTANTS
  gen _m_unit(mksa_register("_m",&__m_unit));
  gen _kg_unit(mksa_register("_kg",&__kg_unit));
  gen _s_unit(mksa_register("_s",&__s_unit));
  gen _A_unit(mksa_register("_A",&__A_unit));
  gen _K_unit(mksa_register("_K",&__K_unit)); // Kelvin
  gen _mol_unit(mksa_register("_mol",&__mol_unit)); // mol
  gen _cd_unit(mksa_register("_cd",&__cd_unit)); // candela
  gen _E_unit(mksa_register("_E",&__E_unit)); // euro
  gen _Bq_unit(mksa_register("_Bq",&__Bq_unit));
  gen _C_unit(mksa_register("_C",&__C_unit));
  gen _F_unit(mksa_register("_F",&__F_unit));
  gen _Gy_unit(mksa_register("_Gy",&__Gy_unit));
  gen _H_unit(mksa_register("_H",&__H_unit));
  gen _Hz_unit(mksa_register("_Hz",&__Hz_unit));
  gen _J_unit(mksa_register("_J",&__J_unit));
  gen _mho_unit(mksa_register("_mho",&__mho_unit));
  gen _N_unit(mksa_register("_N",&__N_unit));
  gen _Ohm_unit(mksa_register("_Ohm",&__Ohm_unit));
  gen _Pa_unit(mksa_register("_Pa",&__Pa_unit));
  gen _rad_unit(mksa_register("_rad",&__rad_unit)); // radian
  gen _S_unit(mksa_register("_S",&__S_unit));
  gen _Sv_unit(mksa_register("_Sv",&__Sv_unit));
  gen _T_unit(mksa_register("_T",&__T_unit));
  gen _V_unit(mksa_register("_V",&__V_unit));
  gen _W_unit(mksa_register("_W",&__W_unit));
  gen _Wb_unit(mksa_register("_Wb",&__Wb_unit));
  gen _l_unit(mksa_register("_l",&__l_unit));
  gen _molK_unit(mksa_register("_molK",&__molK_unit));
  gen _L_unit(mksa_register("_L",&__L_unit));
  // other metric units in m,kg,s,A
  gen _st_unit(mksa_register("_st",&__st_unit));
  // useful non metric units
  gen _a_unit(mksa_register("_a",&__a_unit));
  gen _acre_unit(mksa_register("_acre",&__acre_unit));
  gen _arcmin_unit(mksa_register("_arcmin",&__arcmin_unit));
  gen _arcs_unit(mksa_register("_arcs",&__arcs_unit));
  gen _atm_unit(mksa_register("_atm",&__atm_unit));
  gen _au_unit(mksa_register("_au",&__au_unit));
  gen _Angstrom_unit(mksa_register("_Angstrom",&__Angstrom_unit));
  gen _micron_unit(mksa_register("_µ",&__micron_unit));
  gen _b_unit(mksa_register("_b",&__b_unit));
  gen _bar_unit(mksa_register("_bar",&__bar_unit));
  gen _bbl_unit(mksa_register("_bbl",&__bbl_unit));
  gen _buUS(mksa_register("_buUS",&__buUS));
  gen _bu(mksa_register("_bu",&__bu));
  gen _Btu_unit(mksa_register("_Btu",&__Btu_unit));
  gen _cal_unit(mksa_register("_cal",&__cal_unit));
  gen _chain_unit(mksa_register("_chain",&__chain_unit));
  gen _Curie_unit(mksa_register("_Curie",&__Curie_unit));
  gen _ct_unit(mksa_register("_ct",&__ct_unit));
  gen _deg_unit(mksa_register("_deg",&__deg_unit));
  gen _d_unit(mksa_register("_d",&__d_unit));
  gen _dB_unit(mksa_register("_dB",&__dB_unit));
  gen _dyn_unit(mksa_register("_dyn",&__dyn_unit));
  gen _erg_unit(mksa_register("_erg",&__erg_unit));
  gen _eV_unit(mksa_register("_eV",&__eV_unit));
  // gen _degreeF_unit(mksa_register("_degreeF",&__degreeF_unit));
  gen _Rankine_unit(mksa_register("_Rankine",&__Rankine_unit));
  gen _fath_unit(mksa_register("_fath",&__fath_unit));
  gen _fm_unit(mksa_register("_fm",&__fm_unit));
  gen _fbm_unit(mksa_register("_fbm",&__fbm_unit));
  // gen _fc_unit(mksa_register("_fc",&__fc_unit));
  gen _Fdy_unit(mksa_register("_Fdy",&__Fdy_unit));
  gen _fermi_unit(mksa_register("_fermi",&__fermi_unit));
  gen _flam_unit(mksa_register("_flam",&__flam_unit));
  gen _ft_unit(mksa_register("_ft",&__ft_unit));
  gen _ftUS_unit(mksa_register("_ftUS",&__ftUS_unit));
  gen _Gal(mksa_register("_Gal",&__Gal));
  gen _g_unit(mksa_register("_g",&__g_unit));
  gen _galUS_unit(mksa_register("_galUS",&__galUS_unit));
  gen _galC_unit(mksa_register("_galC",&__galC_unit));
  gen _galUK_unit(mksa_register("_galUK",&__galUK_unit));
  gen _gf_unit(mksa_register("_gf",&__gf_unit));
  gen _gmol_unit(mksa_register("_gmol",&__gmol_unit));
  gen _grad_unit(mksa_register("_grad",&__grad_unit));
  gen _gon_unit(mksa_register("_gon",&__gon_unit));
  gen _grain_unit(mksa_register("_grain",&__grain_unit));
  gen _ha_unit(mksa_register("_ha",&__ha_unit));
  gen _h_unit(mksa_register("_h",&__h_unit));
  gen _hp_unit(mksa_register("_hp",&__hp_unit));
  gen _in_unit(mksa_register("_inch",&__in_unit));
  gen _inHg_unit(mksa_register("_inHg",&__inHg_unit));
  gen _inH2O_unit(mksa_register("_inH2O",&__inH2O_unit));
  gen _j_unit(mksa_register("_j",&__j_unit));
  gen _FF_unit(mksa_register("_FF",&__FF_unit));
  gen _kip_unit(mksa_register("_kip",&__kip_unit));
  gen _knot_unit(mksa_register("_knot",&__knot_unit));
  gen _kph_unit(mksa_register("_kph",&__kph_unit));
  gen _lam_unit(mksa_register("_lam",&__lam_unit));
  gen _lb_unit(mksa_register("_lb",&__lb_unit));
  gen _lbf_unit(mksa_register("_lbf",&__lbf_unit));
  gen _lbmol_unit(mksa_register("_lbmol",&__lbmol_unit));
  gen _lbt_unit(mksa_register("_lbt",&__lbt_unit));
  gen _lyr_unit(mksa_register("_lyr",&__lyr_unit));
  gen _mi_unit(mksa_register("_mi",&__mi_unit));
  gen _mil_unit(mksa_register("_mil",&__mil_unit));
  gen _mile_unit(mksa_register("_mile",&__mile_unit));
  gen _mille_unit(mksa_register("_mille",&__mille_unit));
  gen _mn_unit(mksa_register("_mn",&__mn_unit));
  gen _miUS_unit(mksa_register("_miUS",&__miUS_unit));
  gen _mmHg_unit(mksa_register("_mmHg",&__mmHg_unit));
  gen _mph_unit(mksa_register("_mph",&__mph_unit));
  gen _nmi_unit(mksa_register("_nmi",&__nmi_unit));
  gen _oz_unit(mksa_register("_oz",&__oz_unit));
  gen _ozfl_unit(mksa_register("_ozfl",&__ozfl_unit));
  gen _ozt_unit(mksa_register("_ozt",&__ozt_unit));
  gen _ozUK_unit(mksa_register("_ozUK",&__ozUK_unit));
  gen _P_unit(mksa_register("_P",&__P_unit));
  gen _pc_unit(mksa_register("_pc",&__pc_unit));
  gen _pdl_unit(mksa_register("_pdl",&__pdl_unit));
  gen _pk_unit(mksa_register("_pk",&__pk_unit));
  gen _psi_unit(mksa_register("_psi",&__psi_unit));
  gen _pt_unit(mksa_register("_pt",&__pt_unit));
  gen _ptUK_unit(mksa_register("_ptUK",&__ptUK_unit));
  gen _liqpt_unit(mksa_register("_liqpt",&__liqpt_unit));
  gen _qt_unit(mksa_register("_qt",&__qt_unit));
  gen _R_unit(mksa_register("_R",&__R_unit));
  gen _rd_unit(mksa_register("_rd",&__rd_unit));
  gen _rod_unit(mksa_register("_rod",&__rod_unit));
  gen _rem_unit(mksa_register("_rem",&__rem_unit));
  gen _rpm_unit(mksa_register("_rpm",&__rpm_unit));
  gen _sb_unit(mksa_register("_sb",&__sb_unit));
  gen _slug_unit(mksa_register("_slug",&__slug_unit));
  gen _St_unit(mksa_register("_St",&__St_unit));
  gen _t_unit(mksa_register("_t",&__t_unit));
  gen _tbsp_unit(mksa_register("_tbsp",&__tbsp_unit));
  gen _tex(mksa_register("_tex",&__tex));
  gen _therm_unit(mksa_register("_therm",&__therm_unit));
  gen _ton_unit(mksa_register("_ton",&__ton_unit));
  gen _tonUK_unit(mksa_register("_tonUK",&__tonUK_unit));
  gen _torr_unit(mksa_register("_torr",&__torr_unit));
  gen _tr_unit(mksa_register("_tr",&__tr_unit)); // radian
  gen _u_unit(mksa_register("_u",&__u_unit));
  gen _yd_unit(mksa_register("_yd",&__yd_unit));
  gen _yr_unit(mksa_register("_yr",&__yr_unit));

  // Some hydrocarbur energy equivalent
  // tep=tonne equivalent petrole, lep litre equivalent petrole
  // toe=(metric) ton of oil equivalent
  // bblep = baril equivalent petrole, boe=baril of oil equivalent
  gen _tep_unit(mksa_register("_tep",&__tep_unit));
  gen _toe_unit(mksa_register("_toe",&__toe_unit));
  gen _cf_unit(mksa_register("_cf",&__cf_unit));
  gen _tec_unit(mksa_register("_tec",&__tec_unit));
  gen _lep_unit(mksa_register("_lep",&__lep_unit));
  gen _bblep_unit(mksa_register("_bblep",&__bblep_unit));
  gen _boe_unit(mksa_register("_boe",&__boe_unit));
  gen _Wh_unit(mksa_register("_Wh",&__Wh_unit));
  // Equivalent Carbon for 1 tep, oil, gas, coal
  gen _tepC_unit(mksa_register("_tepC",&__tepC_unit));
  gen _tepgC_unit(mksa_register("_tepgC",&__tepgC_unit));
  gen _tepcC_unit(mksa_register("_tepcC",&__tepcC_unit));
  // mean PRG for HFC in kg C unit
  gen _HFCC_unit(mksa_register("_HFCC",&__HFCC_unit));
#endif

  static vecteur mksa_unit2vecteur(const mksa_unit * tmp){
    vecteur v;
    if (tmp->K==0 && tmp->mol==0 && tmp->cd==0){
      if (tmp->m==0 && tmp->kg==0 && tmp->s==0 && tmp->A==0 && tmp->E==0){
	v.push_back(tmp->coeff);
      }
      else {
	v.reserve(5);
	v.push_back(tmp->coeff);
	v.push_back(tmp->m);
	v.push_back(tmp->kg);
	v.push_back(tmp->s);
	v.push_back(tmp->A);
      }
    }
    else {
      v.reserve(9);
      v.push_back(tmp->coeff);
      v.push_back(tmp->m);
      v.push_back(tmp->kg);
      v.push_back(tmp->s);
      v.push_back(tmp->A);
      v.push_back(tmp->K);
      v.push_back(tmp->mol);
      v.push_back(tmp->cd);
      v.push_back(tmp->E);
    }
    return v;
  }

  static bool tri3(const char * a,const char * b){
    return strcmp(a,b)<0;
  }

  // return a vector of powers in MKSA system
  vecteur mksa_convert(const identificateur & g,GIAC_CONTEXT){
    string s=g.print(contextptr);
    // Find prefix in unit
    int exposant=0;
    int l=s.size();
    std::pair<const char * const * const,const char * const * const> pp=equal_range(unitname_tab,unitname_tab_end,s.c_str(),tri3);
    if (pp.first!=pp.second && pp.second!=unitname_tab_end)
      mksa_register_unit(*pp.first,unitptr_tab[pp.first-unitname_tab]);
    if (l>1 && s[0]=='_'){
      --l;
      s=s.substr(1,l);
    }
    else
      return makevecteur(g);
    gen res=plus_one;
    std::map<const char *, const mksa_unit *,ltstr>::const_iterator it=unit_conversion_map().find(s.c_str()),itend=unit_conversion_map().end();
    if (it==itend && l>1){
      switch (s[0]){
      case 'Y':
	exposant=24;
	break;
      case 'Z':
	exposant=21;
	break;
      case 'E':
	exposant=18;
	break;
      case 'P':
	exposant=15;
	break;
      case 'T':
	exposant=12;
	break;
      case 'G':
	exposant=9;
	break;
      case 'M':
	exposant=6;
	break;
      case 'K': case 'k':
	exposant=3;
	break;
      case 'H': case 'h':
	exposant=2;
	break;
      case 'D':
	exposant=1;
	break;
      case 'd':
	exposant=-1;
	break;
      case 'c':
	exposant=-2;
	break;
      case 'm':
	exposant=-3;
	break;
      case 'µ':
	exposant=-6;
	break;
      case 'n':
	exposant=-9;
	break;
      case 'p':
	exposant=-12;
	break;
      case 'f':
	exposant=-15;
	break;
      case 'a':
	exposant=-18;
	break;
      case 'z':
	exposant=-21;
	break;
      case 'y':
	exposant=-24;
	break;
      }
    }
    if (exposant!=0){
      s=s.substr(1,l-1);
      res=std::pow(10.0,double(exposant));
      std::pair<const char * const * const,const char * const * const> pp=equal_range(unitname_tab,unitname_tab_end,("_"+s).c_str(),tri3);
      if (pp.first!=pp.second && pp.second!=unitname_tab_end)
	mksa_register_unit(*pp.first,unitptr_tab[pp.first-unitname_tab]);
      it=unit_conversion_map().find(s.c_str());
    }
    if (it==itend)
      return makevecteur(res*find_or_make_symbol("_"+s,false,contextptr));
    vecteur v=mksa_unit2vecteur(it->second);
    v[0]=res*v[0];
    return v;
  }

  vecteur mksa_convert(const gen & g,GIAC_CONTEXT){
    if (g.type==_IDNT)
      return mksa_convert(*g._IDNTptr,contextptr);
    if (g.type!=_SYMB)
      return makevecteur(g);
    if (g.is_symb_of_sommet(at_unit)){
      vecteur & v=*g._SYMBptr->feuille._VECTptr;
      vecteur res0=mksa_convert(v[1],contextptr);
      vecteur res1=mksa_convert(v[0],contextptr);
      vecteur res=addvecteur(res0,res1);
      res.front()=res0.front()*res1.front();
      return res;
    }
    if (g._SYMBptr->sommet==at_inv){
      vecteur res(mksa_convert(g._SYMBptr->feuille,contextptr));
      res[0]=inv(res[0],contextptr);
      int s=res.size();
      for (int i=1;i<s;++i)
	res[i]=-res[i];
      return res;
    }
    if (g._SYMBptr->sommet==at_pow){
      gen & f=g._SYMBptr->feuille;
      if (f.type!=_VECT||f._VECTptr->size()!=2)
	return vecteur(1,gensizeerr(contextptr));
      vecteur res(mksa_convert(f._VECTptr->front(),contextptr));
      gen e=f._VECTptr->back();
      res[0]=pow(res[0],e,contextptr);
      int s=res.size();
      for (int i=1;i<s;++i)
	res[i]=e*res[i];
      return res;
    }
    if (g._SYMBptr->sommet==at_prod){
      gen & f=g._SYMBptr->feuille;
      if (f.type!=_VECT)
	return mksa_convert(f,contextptr);
      vecteur & v=*f._VECTptr;
      vecteur res(makevecteur(plus_one));
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	vecteur tmp(mksa_convert(*it,contextptr));
	res[0]=res[0]*tmp[0];
	iterateur it=res.begin()+1,itend=res.end(),jt=tmp.begin()+1,jtend=tmp.end();
	for (;it!=itend && jt!=jtend;++it,++jt)
	  *it=*it+*jt;
	for (;jt!=jtend;++jt)
	  res.push_back(*jt);
      }
      return res;
    }
    return makevecteur(g);
  }

  gen unitpow(const gen & g,const gen & exponent){
    if (is_zero(exponent))
      return plus_one;
    if (is_one(exponent))
      return g;
    if (evalf_double(exponent,1,context0).type!=_DOUBLE_)
      return gensizeerr(gettext("Invalid unit exponent")+exponent.print());
    return symbolic(at_pow,gen(makevecteur(g,exponent),_SEQ__VECT));
  }
  gen mksa_reduce(const gen & g,GIAC_CONTEXT){
    vecteur v(mksa_convert(g,contextptr));
    if (is_undef(v)) return v;
    gen res1=v[0];
    gen res=plus_one;
    int s=v.size();
    if (s>2)
      res = res *unitpow(_kg_unit,v[2]);
    if (s>1)
      res = res *unitpow(_m_unit,v[1]);
    if (s>3)
      res = res *unitpow(_s_unit,v[3]);
    if (s>4)
      res = res * unitpow(_A_unit,v[4]);
    if (s>5)
      res = res * unitpow(_K_unit,v[5]);
    if (s>6)
      res = res * unitpow(_mol_unit,v[6]);
    if (s>7)
      res = res * unitpow(_cd_unit,v[7]);
    if (s>8)
      res = res * unitpow(_E_unit,v[8]);
    if (is_one(res))
      return res1;
    else
      return symbolic(at_unit,makevecteur(res1,res));
  }
  static const char _mksa_s []="mksa";
  static define_unary_function_eval (__mksa,&mksa_reduce,_mksa_s);
  define_unary_function_ptr5( at_mksa ,alias_at_mksa,&__mksa,0,true);
  
  gen _ufactor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      vecteur & v=*g._VECTptr;
      return v.back()*mksa_reduce(v.front()/v.back(),contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _ufactor_s []="ufactor";
  static define_unary_function_eval (__ufactor,&_ufactor,_ufactor_s);
  define_unary_function_ptr5( at_ufactor ,alias_at_ufactor,&__ufactor,0,true);
  
  gen _usimplify(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_usimplify,contextptr);
    if (!g.is_symb_of_sommet(at_unit))
      return g;
    vecteur v=mksa_convert(g,contextptr);
    if (is_undef(v)) return v;
    gen res1=v[0];
    int s=v.size();
    if (s>5)
      return g;
    for (int i=s;i<5;++i)
      v.push_back(zero);
    // look first if it's a mksa
    int pos=0;
    for (int i=1;i<5;++i){
      if (v[i]==zero)
	continue;
      if (pos){
	pos=0;
	break;
      }
      pos=i;
    }
    if (pos)
      return mksa_reduce(g,contextptr);
    v[0]=plus_one;
    const_iterateur it=usual_units().begin(),itend=usual_units().end();
    for (;it!=itend;++it){
      string s=it->print(contextptr);
      gen tmp=mksa_unit2vecteur(unit_conversion_map()[s.substr(1,s.size()-1).c_str()]);
      if (tmp==v)
	return _ufactor(gen(makevecteur(g,symbolic(at_unit,makevecteur(1,*it))),_SEQ__VECT),contextptr);
    }
    // count non-zero in v, if ==2 return mksa
    int count=0;
    for (it=v.begin()+1,itend=v.end();it!=itend;++it){
      if (!is_zero(*it))
	++count;
    }
    if (count<=2) 
      return mksa_reduce(g,contextptr);
    it=usual_units().begin(); itend=usual_units().end();
    for (;it!=itend;++it){
      string s=it->print(contextptr);
      gen tmp=mksa_unit2vecteur(unit_conversion_map()[s.substr(1,s.size()-1).c_str()]);
      vecteur w(*tmp._VECTptr);
      for (int j=0;j<2;j++){
	vecteur vw;
	if (j)
	  vw=addvecteur(v,w);
	else
	  vw=subvecteur(v,w);
	for (int i=1;i<5;++i){
	  if (vw[i]==zero)
	    continue;
	  if (pos){
	    pos=0;
	    break;
	  }
	  pos=i;
	}
	if (pos){
	  if (j)
	    return _ufactor(gen(makevecteur(g,symbolic(at_unit,makevecteur(1,unitpow(*it,-1)))),_SEQ__VECT),contextptr);
	  else
	    return _ufactor(gen(makevecteur(g,symbolic(at_unit,makevecteur(1,*it))),_SEQ__VECT),contextptr);
	}
      }
    }
    return g;
  }
  static const char _usimplify_s []="usimplify";
  static define_unary_function_eval (__usimplify,&_usimplify,_usimplify_s);
  define_unary_function_ptr5( at_usimplify ,alias_at_usimplify,&__usimplify,0,true);
  
  gen symb_unit(const gen & a,const gen & b,GIAC_CONTEXT){
    // Add a _ to all identifiers in b
    if (!lop(b,at_of).empty())
      return gensizeerr(contextptr);
    vecteur v(lidnt(b)); // was lvar(b), changed because 1_(km/s) did not work
    for (unsigned i=0;i<v.size();++i){
      if (v[i].type!=_IDNT)
	return gensizeerr(contextptr); // bad unit
    }
    vecteur w(v);
    iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      find_or_make_symbol("_"+it->print(contextptr),*it,0,false,contextptr);
    }
    return symbolic(at_unit,makevecteur(a,subst(b,v,w,true,contextptr)));
  }
  static string printasunit(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT || feuille._VECTptr->size()!=2)
      return "printasunit error";
    vecteur & v=*feuille._VECTptr;
    vecteur v1(lidnt(v[1]));
    vecteur w(v1);
    iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      string s;
      s=it->print(contextptr);
      if (!s.empty() && s[0]=='_')
	s=s.substr(1,s.size()-1);
      *it=identificateur(s);//find_or_make_symbol(s,*it,0,false,contextptr);
    }
    string tmp(subst(v[1],v1,w,true,contextptr).print(contextptr));
    if (tmp[0]=='c' || (v[1].type==_SYMB 
			// && !v[1].is_symb_of_sommet(at_pow)
			) )
      tmp="_("+tmp+")";
    else
      tmp="_"+tmp;
    if (v[0].type<_POLY || v[0].type==_FLOAT_)
      return v[0].print(contextptr)+tmp;
    else
      return "("+v[0].print(contextptr)+")"+tmp;
  }
  static gen unit(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return symbolic(at_unit,g);
  }
  static const char _unit_s []="_";
  static define_unary_function_eval2_index (112,__unit,&unit,_unit_s,&printasunit);
  define_unary_function_ptr( at_unit ,alias_at_unit ,&__unit);

  const unary_function_ptr * binary_op_tab(){
  static const unary_function_ptr binary_op_tab_ptr []={*at_plus,*at_prod,*at_pow,*at_and,*at_ou,*at_xor,*at_different,*at_same,*at_equal,*at_unit,*at_compose,*at_composepow,*at_deuxpoints,*at_tilocal,*at_pointprod,*at_pointdivision,*at_pointpow,*at_division,*at_normalmod,*at_minus,*at_intersect,*at_union,*at_interval,*at_inferieur_egal,*at_inferieur_strict,*at_superieur_egal,*at_superieur_strict,0};
    return binary_op_tab_ptr;
  }
  // unary_function_ptr binary_op_tab[]={at_and,at_ou,at_different,at_same,0};

  // Physical constants -> in input_lexer.ll
#ifndef NO_PHYSICAL_CONSTANTS
  identificateur _cst_hbar("_hbar_",symbolic(at_unit,makevecteur(1.05457266e-34,_J_unit*_s_unit)));
  gen cst_hbar(_cst_hbar);
  identificateur _cst_clightspeed("_c_",symbolic(at_unit,makevecteur(299792458,_m_unit/_s_unit)));
  gen cst_clightspeed(_cst_clightspeed);
  identificateur _cst_ga("_g_",symbolic(at_unit,makevecteur(9.80665,_m_unit*unitpow(_s_unit,-2))));
  gen cst_ga(_cst_ga);
  identificateur _cst_IO("_IO_",symbolic(at_unit,makevecteur(1e-12,_W_unit*unitpow(_m_unit,-2))));
  gen cst_IO(_cst_IO);
  // gen cst_IO("_io",context0); //  IO 1e-12W/m^2
  identificateur _cst_epsilonox("_epsilonox_",3.9);
  gen cst_epsilonox(_cst_epsilonox); // 3.9
  identificateur _cst_epsilonsi("_epsilonsi_",11.9);
  gen cst_epsilonsi(_cst_epsilonsi); // 11.9
  identificateur _cst_qepsilon0("_qepsilon0_",symbolic(at_unit,makevecteur(1.4185979e-30,_F_unit*_C_unit/_m_unit)));
  gen cst_qepsilon0(_cst_qepsilon0); // qeps0 1.4185979e-30 F*C/m
  identificateur _cst_epsilon0q("_epsilon0q_",symbolic(at_unit,makevecteur(55263469.6,_F_unit/(_m_unit*_C_unit))));
  gen cst_epsilon0q(_cst_epsilon0q); // eps0q 55263469.6 F/(m*C)
  identificateur _cst_kq("_kq_",symbolic(at_unit,makevecteur(8.617386e-5,_J_unit/(_K_unit*_C_unit))));
  gen cst_kq(_cst_kq); // kq 8.617386e-5 J/(K*C)
  identificateur _cst_c3("_c3_",symbolic(at_unit,makevecteur(.002897756,_m_unit*_K_unit)));
  gen cst_c3(_cst_c3); // c3 .002897756m*K
  identificateur _cst_lambdac("_lambdac_",symbolic(at_unit,makevecteur( 0.00242631058e-9,_m_unit)));
  gen cst_lambdac(_cst_lambdac); // lambdac 0.00242631058 nm
  identificateur _cst_f0("_f0_",symbolic(at_unit,makevecteur(2.4179883e14,_Hz_unit)));
  gen cst_f0(_cst_f0); //  f0 2.4179883e14Hz
  identificateur _cst_lambda0("_lambda0_",symbolic(at_unit,makevecteur(1239.8425e-9,_m_unit)));
  gen cst_lambda0(_cst_lambda0); // lambda0 1239.8425_nm
  identificateur _cst_muN("_muN_",symbolic(at_unit,makevecteur(5.0507866e-27,_J_unit/_T_unit)));
  gen cst_muN(_cst_muN); // muN 5.0507866e-27_J/T
  identificateur _cst_muB("_muB_",symbolic(at_unit,makevecteur( 9.2740154e-24,_J_unit/_T_unit)));
  gen cst_muB(_cst_muB); // muB 9.2740154e-24 J/T
  identificateur _cst_a0("_a0_",symbolic(at_unit,makevecteur(.0529177249e-9,_m_unit)));
  gen cst_a0(_cst_a0); // a0 .0529177249_nm
  identificateur _cst_Rinfinity("_Rinfinity_",symbolic(at_unit,makevecteur(10973731.534,unitpow(_m_unit,-1))));
  gen cst_Rinfinity(_cst_Rinfinity); // Rinf 10973731.534 m^-1
  identificateur _cst_Faraday("_Faraday_",symbolic(at_unit,makevecteur(96485.309,_C_unit/_mol_unit)));
  gen cst_Faraday(_cst_Faraday); // F 96485.309 C/gmol
  identificateur _cst_phi("_phi_",symbolic(at_unit,makevecteur(2.06783461e-15,_Wb_unit)));
  gen cst_phi(_cst_phi); // phi 2.06783461e-15 Wb
  identificateur _cst_alpha("_alpha_",7.29735308e-3);
  gen cst_alpha(_cst_alpha); // alpha 7.29735308e-3
  identificateur _cst_mpme("_mpme_",1836.152701);
  gen cst_mpme(_cst_mpme); // mpme 1836.152701
  identificateur _cst_mp("_mp_",symbolic(at_unit,makevecteur(1.6726231e-27,_kg_unit)));
  gen cst_mp(_cst_mp); // mp 1.6726231e-27 kg
  identificateur _cst_qme("_qme_",symbolic(at_unit,makevecteur(1.75881962e11,_C_unit/_kg_unit)));
  gen cst_qme(_cst_qme); // qme 175881962000 C/kg
  identificateur _cst_me("_me_",symbolic(at_unit,makevecteur(9.1093897e-31,_kg_unit)));
  gen cst_me(_cst_me); // me 9.1093897e-31 kg
  identificateur _cst_qe("_qe_",symbolic(at_unit,makevecteur(1.60217733e-19,_C_unit)));
  gen cst_qe(_cst_qe); // q 1.60217733e-19 C
  identificateur _cst_hPlanck("_h_",symbolic(at_unit,makevecteur(6.6260755e-34,_J_unit*_s_unit)));
  gen cst_hPlanck(_cst_hPlanck); //  h 6.6260755e-34 Js
  identificateur _cst_G("_G_",symbolic(at_unit,makevecteur(6.67259e-11,unitpow(_m_unit,3)*unitpow(_s_unit,-2)*unitpow(_kg_unit,-1))));
  gen cst_G(_cst_G); // G 6.67259e-11m^3/s^2kg
  identificateur _cst_mu0("_mu0_",symbolic(at_unit,makevecteur(1.25663706144e-6,_H_unit/_m_unit)));
  gen cst_mu0(_cst_mu0); // mu0 1.25663706144e-6 H/m
  identificateur _cst_epsilon0("_epsilon0_",symbolic(at_unit,makevecteur(8.85418781761e-12,_F_unit/_m_unit)));
  gen cst_epsilon0(_cst_epsilon0); // eps0 8.85418781761e-12 F/m
  identificateur _cst_sigma("_sigma_",symbolic(at_unit,makevecteur( 5.67051e-8,_W_unit*unitpow(_m_unit,-2)*unitpow(_K_unit,-4))));
  gen cst_sigma(_cst_sigma); // sigma 5.67051e-8 W/m^2*K^4
  identificateur _cst_StdP("_StdP_",symbolic(at_unit,makevecteur(101325.0,_Pa_unit)));
  gen cst_StdP(_cst_StdP); // StdP 101.325_kPa
  identificateur _cst_StdT("_StdT_",symbolic(at_unit,makevecteur(273.15,_K_unit)));
  gen cst_StdT(_cst_StdT); // StdT 273.15_K
  identificateur _cst_Rydberg("_R_",symbolic(at_unit,makevecteur(8.31451,_J_unit/_molK_unit)));
  gen cst_Rydberg(_cst_Rydberg); // Rydberg 8.31451_J/(gmol*K)
  identificateur _cst_Vm("_Vm_",symbolic(at_unit,makevecteur(22.4141,_l_unit/_mol_unit)));
  gen cst_Vm(_cst_Vm); // Vm 22.4141_l/gmol
  identificateur _cst_kBoltzmann("_k_",symbolic(at_unit,makevecteur(1.380658e-23,_J_unit/_K_unit)));
  gen cst_kBoltzmann(_cst_kBoltzmann); // k 1.380658e-23 J/K
  identificateur _cst_NA("_NA_",symbolic(at_unit,makevecteur(6.0221367e23,unitpow(_mol_unit,-1))));
  gen cst_NA(_cst_NA); // NA 6.0221367e23 1/gmol
#endif // NO_PHYSICAL_CONSTANTS

  gen maple_root(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return symbolic(at_maple_root,g);
    vecteur & v=*g._VECTptr;
    return pow(v[1],inv(v[0],contextptr),contextptr);
  }
  static const char _maple_root_s []="root";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__maple_root,&maple_root,_maple_root_s);
#else
  static const unary_function_eval __maple_root(0,&maple_root,_maple_root_s);
#endif
  define_unary_function_ptr( at_maple_root ,alias_at_maple_root ,&__maple_root);

  gen symb_interrogation(const gen & e1,const gen & e3){
    if (e3.is_symb_of_sommet(at_deuxpoints)){
      gen & f =e3._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2)
	return symb_when(e1,f._VECTptr->front(),f._VECTptr->back());
    }
    return symb_when(e1,e3,undef);
  }

  bool first_ascend_sort(const gen & a,const gen & b){
    gen g=inferieur_strict(a[0],b[0],context0); 
    if (g.type!=_INT_)
      return a[0].islesscomplexthan(b[0]);
    return g.val==1;
  }
  bool first_descend_sort(const gen & a,const gen & b){
    gen g=superieur_strict(a[0],b[0],context0); 
    if (g.type!=_INT_)
      return !a[0].islesscomplexthan(b[0]);
    return g.val==1;
  }

#ifdef NO_UNARY_FUNCTION_COMPOSE
  gen user_operator(const gen & g,GIAC_CONTEXT){
    return gensizeerr(gettext("User operator not available on this architecture"));
  }

#else
  // Create an operator with a given syntax
  vector<unary_function_ptr> user_operator_list;   // GLOBAL VAR
  gen user_operator(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()<3)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    // int s=signed(v.size());
    if (v[0].type!=_STRNG)
      return string2gen("Operator name must be of type string",false);
    string & ss=*v[0]._STRNGptr;
    vector<unary_function_ptr>::iterator it=user_operator_list.begin(),itend=user_operator_list.end();
    for (;it!=itend;++it){
      if (it->ptr()->s==ss){
	break;
      }
    }
    if (it!=itend){
      const unary_function_abstract * ptr0=it->ptr();
      const unary_function_user * ptr=dynamic_cast<const unary_function_user *>(ptr0);
      if (!ptr)
	return zero;
      if (ptr->f==v[1])
	return plus_one;
      return zero;
    }
    if (v[2].type==_INT_){ 
      int token_value=v[2].val;
      unary_function_user * uf;
      if (v[2].subtype==_INT_MUPADOPERATOR){
	switch (v[2].val){
	case _POSTFIX_OPERATOR:
	  uf= new unary_function_user (0,v[1],ss,0,0,0);
	  token_value=T_FACTORIAL; // like factorial
	  break;
	case _PREFIX_OPERATOR:
	  uf=new unary_function_user(0,v[1],ss,0,0,0);
	  token_value=T_NOT; // like not
	  break;
	case _BINARY_OPERATOR:
	  uf = new unary_function_user (0,v[1],ss);
	  token_value=T_FOIS; // like *
	  break;
	default:
	  return zero;
	}
      }
      else 
	// non mupad syntax, v[2] is input_parser.yy token value
	uf = new unary_function_user(0,v[1],ss);
      unary_function_ptr u(uf);
      // cout << symbolic(u,makevecteur(1,2)) << endl;
      user_operator_list.push_back(u);
      bool res=lexer_functions_register(u,ss.c_str(),token_value);
      if (res){
#ifdef HAVE_SIGNAL_H_OLD
	if (!child_id)
	  _signal(symb_quote(symbolic(at_user_operator,g)),contextptr);
#endif
	return plus_one;
      }
      user_operator_list.pop_back();
      delete uf;
    }
    return zero;
  }
#endif // NO_UNARY_FUNCTION_COMPOSE
  static const char _user_operator_s []="user_operator";
  static define_unary_function_eval (__user_operator,&user_operator,_user_operator_s);
  define_unary_function_ptr( at_user_operator ,alias_at_user_operator ,&__user_operator);

  gen current_folder_name;

  gen getfold(const gen & g){
    if (is_zero(g))
      return string2gen("main",false);
    return g;
  }

  gen _SetFold(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (!is_zero(g) && g.type!=_IDNT)
      return gensizeerr(contextptr);
    bool ok=is_zero(g);
    if (g.type==_IDNT && g._IDNTptr->value && g._IDNTptr->value->type==_VECT && g._IDNTptr->value->subtype==_FOLDER__VECT)
      ok=true;
    if ( ok || (g.type==_IDNT && g._IDNTptr->id_name && (strcmp(g._IDNTptr->id_name,"main")==0|| strcmp(g._IDNTptr->id_name,"home")) ) ){
      gen res=current_folder_name;
      current_folder_name=g;
#ifdef HAVE_SIGNAL_H_OLD
      if (!child_id)
	_signal(symb_quote(symbolic(at_SetFold,g)),contextptr);
#endif
      return getfold(res);
    }
    return gensizeerr(gettext("Non existent Folder"));
  }
  static const char _SetFold_s []="SetFold";
  static define_unary_function_eval2_quoted (__SetFold,&_SetFold,_SetFold_s,&printastifunction);
  define_unary_function_ptr5( at_SetFold ,alias_at_SetFold,&__SetFold,_QUOTE_ARGUMENTS,T_RETURN); 

  
  static string printaspiecewise(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( feuille.type!=_VECT || feuille._VECTptr->empty() || abs_calc_mode(contextptr)!=38)
      return sommetstr+('('+feuille.print(contextptr)+')');
    vecteur & v = *feuille._VECTptr;
    string res("CASE");
    int s=v.size();
    for (int i=0;i<s/2;i++){
      res += " IF ";
      res += v[2*i].print(contextptr);
      res += " THEN ";
      res += printasinnerbloc(v[2*i+1],contextptr);
      res += " END";
    }
    if (s%2){
      res += " DEFAULT ";
      res += printasinnerbloc(v[s-1],contextptr);
    }
    return res+" END";
  }
  gen _piecewise(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    // evaluate couples of condition/expression, like in a case
    if (g.type!=_VECT)
      return g;
    vecteur & v =*g._VECTptr;
    int s=v.size();
    gen test;
    for (int i=0;i<s/2;++i){
      test=v[2*i];
      test=equaltosame(test.eval(eval_level(contextptr),contextptr)).eval(eval_level(contextptr),contextptr);
      test=test.evalf_double(eval_level(contextptr),contextptr);
      if ( (test.type!=_DOUBLE_) && (test.type!=_CPLX) )
	return symbolic(at_piecewise,g.eval(eval_level(contextptr),contextptr));
      if (is_zero(test))
	continue;
      return v[2*i+1].eval(eval_level(contextptr),contextptr);
    }
    if (s%2)
      return v[s-1].eval(eval_level(contextptr),contextptr);
    return undeferr(gettext("No case applies"));
  }
  static const char _piecewise_s []="piecewise";
  static define_unary_function_eval2_quoted (__piecewise,&_piecewise,_piecewise_s,&printaspiecewise);
  define_unary_function_ptr5( at_piecewise ,alias_at_piecewise,&__piecewise,_QUOTE_ARGUMENTS,true);

  gen _geo2d(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    return g;
  }
  static const char _geo2d_s []="geo2d";
  static define_unary_function_eval (__geo2d,&_geo2d,_geo2d_s);
  define_unary_function_ptr5( at_geo2d ,alias_at_geo2d,&__geo2d,0,true);

  static const char _geo3d_s []="geo3d";
  static define_unary_function_eval (__geo3d,&_geo2d,_geo3d_s);
  define_unary_function_ptr5( at_geo3d ,alias_at_geo3d,&__geo3d,0,true);

  static const char _spreadsheet_s []="spreadsheet";
  static define_unary_function_eval (__spreadsheet,&_geo2d,_spreadsheet_s);
  define_unary_function_ptr5( at_spreadsheet ,alias_at_spreadsheet,&__spreadsheet,0,true);

  std::string print_program_syntax(int maple_mode){
    string logs;
    switch (maple_mode){
    case 0:
      logs="xcas";
      break;
    case 1:
      logs="maple";
      break;
    case 2:
      logs="mupad";
      break;
    case 3:
      logs="ti";
      break;
    default:
      logs=print_INT_(maple_mode);
    }
    return logs;
  }

  gen _threads_allowed(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (is_zero(g))
      threads_allowed=false;
    else
      threads_allowed=true;
    return threads_allowed;
  }
  static const char _threads_allowed_s []="threads_allowed";
  static define_unary_function_eval (__threads_allowed,&_threads_allowed,_threads_allowed_s);
  define_unary_function_ptr5( at_threads_allowed ,alias_at_threads_allowed,&__threads_allowed,0,true);

  gen _mpzclass_allowed(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG &&  g.subtype==-1) return  g;
    if (is_zero(g))
      mpzclass_allowed=false;
    else
      mpzclass_allowed=true;
    return mpzclass_allowed;
  }
  static const char _mpzclass_allowed_s []="mpzclass_allowed";
  static define_unary_function_eval (__mpzclass_allowed,&_mpzclass_allowed,_mpzclass_allowed_s);
  define_unary_function_ptr5( at_mpzclass_allowed ,alias_at_mpzclass_allowed,&__mpzclass_allowed,0,true);

  gen whentopiecewise(const gen & g,GIAC_CONTEXT){
    return symbolic(at_piecewise,g);
  }
  const alias_type when_tab_alias[]={(alias_type)&__when,0};
  const unary_function_ptr * const when_tab=(const unary_function_ptr * const)when_tab_alias;
  const gen_op_context when2piecewise_tab[]={whentopiecewise,0};
  gen when2piecewise(const gen & g,GIAC_CONTEXT){
    return subst(g,when_tab,when2piecewise_tab,false,contextptr);
    /*
    vector< gen_op_context > when2piecewise_v(1,whentopiecewise);
    vector< const unary_function_ptr *> when_v(1,at_when);
    return subst(g,when_v,when2piecewise_v,false,contextptr);
    */
  }

  gen piecewisetowhen(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return g;
    vecteur v = *g._VECTptr;
    int s=v.size();
    if (s==1)
      return gensizeerr(contextptr);
    if (s==2){
      v.push_back(undef);
      s++;
    }
    if (s==3)
      return symbolic(at_when,g);
    gen tmp=piecewisetowhen(vecteur(v.begin()+2,v.end()),contextptr);
    return symbolic(at_when,gen(makevecteur(v[0],v[1],tmp),_SEQ__VECT));
  }
  const alias_type piecewise_tab_alias[]={alias_at_piecewise,0};
  const unary_function_ptr * const piecewise_tab=(const unary_function_ptr * const)piecewise_tab_alias;
  const gen_op_context piecewise2when_tab[]={piecewisetowhen,0};
  gen piecewise2when(const gen & g,GIAC_CONTEXT){
    return subst(g,piecewise_tab,piecewise2when_tab,false,contextptr);
    /*
    vector< const unary_function_ptr *> piecewise_v(1,at_piecewise);
    vector< gen_op_context > piecewise2when_v(1,piecewisetowhen);
    return subst(g,piecewise_v,piecewise2when_v,false,contextptr);
    */
  }

  gen whentosign(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gensizeerr(contextptr);
    vecteur v = *g._VECTptr;
    if (v[0].is_symb_of_sommet(at_equal) || v[0].is_symb_of_sommet(at_same)){
      *logptr(contextptr) << gettext("Assuming false condition ") << v[0].print(contextptr) << endl;
      return v[2];
    }
    if (v[0].is_symb_of_sommet(at_different)){
      *logptr(contextptr) << gettext("Assuming true condition ") << v[0].print(contextptr) << endl;
      return v[1];
    }
    bool ok=false;
    if (v[0].is_symb_of_sommet(at_superieur_strict) || v[0].is_symb_of_sommet(at_superieur_egal)){
      v[0]=v[0]._SYMBptr->feuille[0]-v[0]._SYMBptr->feuille[1];
      ok=true;
    }
    if (!ok && (v[0].is_symb_of_sommet(at_inferieur_strict) || v[0].is_symb_of_sommet(at_inferieur_egal)) ){
      v[0]=v[0]._SYMBptr->feuille[1]-v[0]._SYMBptr->feuille[0];
      ok=true;
    }
    if (!ok)
      return gensizeerr(gettext("Unable to handle when condition ")+v[0].print(contextptr));
    return symbolic(at_sign,v[0])*(v[1]-v[2])/2+(v[1]+v[2])/2;
  }
  const gen_op_context when2sign_tab[]={whentosign,0};
  gen when2sign(const gen & g,GIAC_CONTEXT){
    return subst(g,when_tab,when2sign_tab,false,contextptr);
    /*
    vector< gen_op_context > when2sign_v(1,whentosign);
    vector< const unary_function_ptr *> when_v(1,at_when);
    return subst(g,when_v,when2sign_v,false,contextptr);
    */
  }

  // test if m(i) is an array index: that will not be the case if
  // i is an _IDNT or a list of _IDNT
  // AND m is not already defined as an array
  bool is_array_index(const gen & m,const gen & i,GIAC_CONTEXT){
    if (i.type==_VECT){
      for (unsigned j=0;j<i._VECTptr->size();++j){
	if ((*i._VECTptr)[j].type!=_IDNT)
	  return true;
      }
    }
    else {
      if (i.type!=_IDNT)
	return true;
    }
    gen mv=eval(m,1,contextptr);
    return mv.type==_VECT;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
