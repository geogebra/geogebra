// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c rpn.cc  -DIN_GIAC -DHAVE_CONFIG_H" -*-
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
#include "rpn.h"
#include "symbolic.h"
#include "unary.h"
#include <algorithm>
#include "prog.h"
#include "usual.h"
#include "identificateur.h"
#include <stdio.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#include <dirent.h>
#ifndef __MINGW_H
#include <pwd.h>
#endif
#endif
#include "input_lexer.h"
#include "plot.h"
#include "tex.h"
#include "ti89.h"
#include "maple.h"
#include "misc.h"
#include "permu.h"
#include "intg.h"
#include "derive.h"
#include "sym2poly.h"
#include "input_parser.h"
#include "solve.h"
#include "subst.h"
#include "csturm.h"
#include "giacintl.h"

#ifdef GIAC_HAS_STO_38
#include "aspen.h"
void EditMat(int);
#endif


#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#if defined(GIAC_HAS_STO_38) && defined(VISUALC)
  static const int rand_max3=2147483647;
#else
  static const int rand_max3=RAND_MAX;
#endif

  string enmajuscule(const string & s){
    string res;
    string::const_iterator it=s.begin(),itend=s.end();
    for (;it!=itend;++it){
      if ((*it>='a') && (*it<='z'))
        res += *it-32;
      else
        res += *it;
    }
    return res;
  }

  string printasconstant(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return sommetstr;
  }
#ifdef RTOS_THREADX
  gen return_undef(const gen &,GIAC_CONTEXT){
    return undef;
  }
  static const char _rpn_s []="rpn";
  static define_unary_function_eval2 (__rpn,&return_undef,_rpn_s,&printasconstant);
  define_unary_function_ptr5( at_rpn ,alias_at_rpn ,(unary_function_eval*)&__rpn,0,true);

  static const char _alg_s []="alg";
  static define_unary_function_eval2 (__alg,&return_undef,_alg_s,&printasconstant);
  define_unary_function_ptr5( at_alg ,alias_at_alg ,(unary_function_eval *)&__alg,0,true);

  static const char _ROLL_s []="ROLL";
  static define_unary_function_eval (__ROLL,&return_undef,_ROLL_s);
  define_unary_function_ptr( at_ROLL ,alias_at_ROLL ,&__ROLL);

  static const char _ROLLD_s []="ROLLD";
  static define_unary_function_eval (__ROLLD,&return_undef,_ROLLD_s);
  define_unary_function_ptr( at_ROLLD ,alias_at_ROLLD ,&__ROLLD);

  static const char _SWAP_s []="SWAP";
  static define_unary_function_eval (__SWAP,&return_undef,_SWAP_s);
  define_unary_function_ptr5( at_SWAP ,alias_at_SWAP ,&__SWAP,0,T_RPN_OP);

  static const char _DUP_s []="DUP";
  static define_unary_function_eval (__DUP,&return_undef,_DUP_s);
  define_unary_function_ptr5( at_DUP ,alias_at_DUP ,&__DUP,0,T_RPN_OP);

  static const char _OVER_s []="OVER";
  static define_unary_function_eval (__OVER,&return_undef,_OVER_s);
  define_unary_function_ptr5( at_OVER ,alias_at_OVER ,&__OVER,0,T_RPN_OP);

  static const char _PICK_s []="PICK";
  static define_unary_function_eval (__PICK,&return_undef,_PICK_s);
  define_unary_function_ptr5( at_PICK ,alias_at_PICK ,&__PICK,0,T_RPN_OP);

  static const char _DROP_s []="DROP";
  static define_unary_function_eval (__DROP,&return_undef,_DROP_s);
  define_unary_function_ptr5( at_DROP ,alias_at_DROP ,&__DROP,0,T_RPN_OP);

  static const char _NOP_s []="NOP";
  static define_unary_function_eval2_index (136,__NOP,&return_undef,_NOP_s,&printasconstant);
  define_unary_function_ptr5( at_NOP ,alias_at_NOP ,&__NOP,0,T_RPN_OP);

  static const char _IFTE_s []="IFTE";
  static define_unary_function_eval2_index (126,__IFTE,&return_undef,_IFTE_s,&printasconstant);
  define_unary_function_ptr5( at_IFTE ,alias_at_IFTE ,&__IFTE,0,T_RPN_OP);

  static const char _RPN_LOCAL_s []="RPN_LOCAL";
  static define_unary_function_eval2_index (130,__RPN_LOCAL,&return_undef,_RPN_LOCAL_s,&printasconstant);
  define_unary_function_ptr5( at_RPN_LOCAL ,alias_at_RPN_LOCAL ,&__RPN_LOCAL,0,T_RPN_OP);

  static const char _RPN_FOR_s []="RPN_FOR";
  static define_unary_function_eval2_index (132,__RPN_FOR,&return_undef,_RPN_FOR_s,&printasconstant);
  define_unary_function_ptr5( at_RPN_FOR ,alias_at_RPN_FOR ,&__RPN_FOR,0,T_RPN_OP);

  static const char _RPN_WHILE_s []="RPN_WHILE";
  static define_unary_function_eval2_index (134,__RPN_WHILE,&return_undef,_RPN_WHILE_s,&printasconstant);
  define_unary_function_ptr5( at_RPN_WHILE ,alias_at_RPN_WHILE ,&__RPN_WHILE,0,T_RPN_OP);

  static const char _RPN_CASE_s []="RPN_CASE";
  static define_unary_function_eval2_index (128,__RPN_CASE,&return_undef,_RPN_CASE_s,&printasconstant);
  define_unary_function_ptr5( at_RPN_CASE ,alias_at_RPN_CASE ,&__RPN_CASE,0,T_RPN_OP);

  static const char _RPN_UNTIL_s []="RPN_UNTIL";
  static define_unary_function_eval2 (__RPN_UNTIL,&return_undef,_RPN_UNTIL_s,&printasconstant);
  define_unary_function_ptr( at_RPN_UNTIL ,alias_at_RPN_UNTIL ,&__RPN_UNTIL);

  static const char _rpn_prog_s []="rpn_prog";
  static define_unary_function_eval2_index (83,__rpn_prog,&return_undef,_rpn_prog_s,&printasconstant);
  define_unary_function_ptr5( at_rpn_prog ,alias_at_rpn_prog,&__rpn_prog,_QUOTE_ARGUMENTS,0);
#else // RTOS_THREADX
  static gen symb_rpn(const gen & args){
    return symbolic(at_rpn,args);
  }
  gen _rpn(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    rpn_mode(contextptr)=true;
    return symb_rpn(args);
  }
  static const char _rpn_s []="rpn";
  static define_unary_function_eval2 (__rpn,&_rpn,_rpn_s,&printasconstant);
  define_unary_function_ptr5( at_rpn ,alias_at_rpn ,(unary_function_eval*)&__rpn,0,true);

  static gen symb_alg(const gen & args){
    return symbolic(at_alg,args);
  }
  gen _alg(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    rpn_mode(contextptr)=false;
    return symb_alg(args);
  }
  static const char _alg_s []="alg";
  static define_unary_function_eval2 (__alg,&_alg,_alg_s,&printasconstant);
  define_unary_function_ptr5( at_alg ,alias_at_alg ,(unary_function_eval *)&__alg,0,true);

  void roll(int i,vecteur & v){
    if (i<2)
      return;
    iterateur it=v.begin(),itend=v.end();
    if (itend-it<i)
      return;
    it=itend-i;
    gen save=*it;
    for (;;){
      ++it;
      if (it==itend)
	break;
      *(it-1)=*it;
    }
    *(it-1)=save;
  }

  void ROLL(int i,GIAC_CONTEXT){
    roll(i,history_in(contextptr));
    roll(i,history_out(contextptr));
  }

  gen _ROLL(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args._VECTptr->empty())
      return args;
    gen e=args._VECTptr->back();
    args._VECTptr->pop_back();
    if (e.type==_INT_)
      roll(e.val,*args._VECTptr);
    if (e.type==_DOUBLE_)
      roll(int(e._DOUBLE_val),*args._VECTptr);
    return args;
  }
  static const char _ROLL_s []="ROLL";
  static define_unary_function_eval (__ROLL,&_ROLL,_ROLL_s);
  define_unary_function_ptr( at_ROLL ,alias_at_ROLL ,&__ROLL);

  void rolld(int i,vecteur & v){
    if (i<2)
      return;
    iterateur it=v.begin(),itend=v.end();
    if (itend-it<i)
      return;
    it=itend-i;
    --itend;
    gen save=*itend;
    for (;it!=itend;){
      --itend;
      *(itend+1)=*itend;
    }
    *it=save;
  }

  void ROLLD(int i,GIAC_CONTEXT){
    rolld(i,history_in(contextptr));
    rolld(i,history_out(contextptr));
  }
  gen _ROLLD(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args._VECTptr->empty())
      return args;
    gen e=args._VECTptr->back();
    args._VECTptr->pop_back();
    if (e.type==_INT_)
      rolld(e.val,*args._VECTptr);
    if (e.type==_DOUBLE_)
      rolld(int(e._DOUBLE_val),*args._VECTptr);
    return args;
  }
  static const char _ROLLD_s []="ROLLD";
  static define_unary_function_eval (__ROLLD,&_ROLLD,_ROLLD_s);
  define_unary_function_ptr( at_ROLLD ,alias_at_ROLLD ,&__ROLLD);

  void stack_swap(vecteur & v){
    iterateur it=v.begin(),itend=v.end();
    int s=itend-it;
    if (s<2)
      return;
    --itend;
    gen tmp=*itend;
    *itend=*(itend-1);
    *(itend-1)=tmp;
  }

  void SWAP(GIAC_CONTEXT){
    stack_swap(history_in(contextptr));
    stack_swap(history_out(contextptr));
  }

  gen _SWAP(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    stack_swap(*args._VECTptr);
    return args;
  }
  static const char _SWAP_s []="SWAP";
  static define_unary_function_eval (__SWAP,&_SWAP,_SWAP_s);
  define_unary_function_ptr5( at_SWAP ,alias_at_SWAP ,&__SWAP,0,T_RPN_OP);

  void dup(vecteur & v){
    if (!v.empty())
      v.push_back(v.back());
  }
  gen _DUP(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    dup(*args._VECTptr);
    return args;
  }
  static const char _DUP_s []="DUP";
  static define_unary_function_eval (__DUP,&_DUP,_DUP_s);
  define_unary_function_ptr5( at_DUP ,alias_at_DUP ,&__DUP,0,T_RPN_OP);

  void over(vecteur & v){
    int s=v.size();
    if (s>=2)
      v.push_back(v[s-2]);
  }
  gen _OVER(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    over(*args._VECTptr);
    return args;
  }
  static const char _OVER_s []="OVER";
  static define_unary_function_eval (__OVER,&_OVER,_OVER_s);
  define_unary_function_ptr5( at_OVER ,alias_at_OVER ,&__OVER,0,T_RPN_OP);

  void pick(int i,vecteur & v){
    int s=v.size();
    if ((i>=1) && (s>=i))
      v.push_back(v[s-i]);
  }
  gen _PICK(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args._VECTptr->empty())
      return args;
    gen e=args._VECTptr->back();
    args._VECTptr->pop_back();
    if (e.type==_INT_)
      pick(e.val,*args._VECTptr);
    if (e.type==_DOUBLE_)
      pick(int(e._DOUBLE_val),*args._VECTptr);
    return args;
  }
  static const char _PICK_s []="PICK";
  static define_unary_function_eval (__PICK,&_PICK,_PICK_s);
  define_unary_function_ptr5( at_PICK ,alias_at_PICK ,&__PICK,0,T_RPN_OP);

  void drop(vecteur & v){
    if (v.empty())
      return;
    v.pop_back();
  }
  gen _DROP(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    drop(*args._VECTptr);
    return args;
  }
  static const char _DROP_s []="DROP";
  static define_unary_function_eval (__DROP,&_DROP,_DROP_s);
  define_unary_function_ptr5( at_DROP ,alias_at_DROP ,&__DROP,0,T_RPN_OP);

  static string printasNOP(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return sommetstr;
  }
  gen symb_NOP(const gen & args){
    return vecteur(1,symbolic(at_NOP,args));
  }
  gen _NOP(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return args;
  }
  static const char _NOP_s []="NOP";
  static define_unary_function_eval2_index (136,__NOP,&_NOP,_NOP_s,&printasNOP);
  define_unary_function_ptr5( at_NOP ,alias_at_NOP ,&__NOP,0,T_RPN_OP);

  static string printasIFTE(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    const_iterateur it=feuille._VECTptr->begin();
    string res("IF " + printinner_VECT(*it->_VECTptr,_RPN_FUNC__VECT,contextptr));
    res += " THEN ";
    ++it;
    res += printinner_VECT(*it->_VECTptr,_RPN_FUNC__VECT,contextptr) + " ELSE ";
    ++it;
    return res + printinner_VECT(*it->_VECTptr,_RPN_FUNC__VECT,contextptr)+ " END";
  }
  gen symb_IFTE(const gen & args){
    return symbolic(at_IFTE,args);
  }
  gen _IFTE(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<3)
      return gensizeerr(contextptr);
    gen no=args._VECTptr->back();
    args._VECTptr->pop_back();
    gen yes=args._VECTptr->back();
    args._VECTptr->pop_back();
    gen e=args._VECTptr->back();
    args._VECTptr->pop_back();
    if (e.type==_VECT){
      rpn_eval(e,*args._VECTptr,contextptr);
      if (args._VECTptr->empty())
	return args;
      e=args._VECTptr->back();
      args._VECTptr->pop_back();
    }
    if (is_zero(e))
      return rpn_eval(no,*args._VECTptr,contextptr);
    else
      return rpn_eval(yes,*args._VECTptr,contextptr);
  }
  static const char _IFTE_s []="IFTE";
  static define_unary_function_eval2_index (126,__IFTE,&_IFTE,_IFTE_s,&printasIFTE);
  define_unary_function_ptr5( at_IFTE ,alias_at_IFTE ,&__IFTE,0,T_RPN_OP);

  static string printasRPN_LOCAL(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    string s("-> ");
    s += printinner_VECT(*feuille._VECTptr->front()._VECTptr,_RPN_FUNC__VECT,contextptr);
    gen e= feuille._VECTptr->back();
    if ( (e.type==_VECT) && (e.subtype==_RPN_FUNC__VECT))
      return s + " " +e.print(contextptr);
    else { 
      if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_quote))
	return s + " '"+e._SYMBptr->feuille.print(contextptr)+"'";
      else
	return s + " '"+e.print(contextptr)+"'";
    }
  }
  gen symb_RPN_LOCAL(const gen & a,const gen & b){
    return symbolic(at_RPN_LOCAL,makesequence(a,b));
  }
  gen _RPN_LOCAL(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // stack level 2=symbolic names, level 1=program
    if (args.type!=_VECT)
      return symbolic(at_RPN_LOCAL,args);
    int s=args._VECTptr->size();
    if (s<3)
      return gentoofewargs("RPN_LOCAL must have at least 3 args");
    gen prog=args._VECTptr->back();
    args._VECTptr->pop_back();
    vecteur names=*(args._VECTptr->back()._VECTptr); // must be a vector
    args._VECTptr->pop_back();
    // get values from stack
    int nvars=names.size();
    if (s-2<nvars)
      return gentoofewargs("RPN_LOCAL");
    vecteur values(names);
    for (int j=nvars-1;j>=0;--j){
      values[j]=args._VECTptr->back();
      args._VECTptr->pop_back();
    }
    // Initialization
    context * newcontextptr = (context *) contextptr;
    int protect=bind(values,names,newcontextptr);
    vecteur res;
    if ((prog.type==_SYMB) && (prog._SYMBptr->sommet==at_quote)){
      args._VECTptr->push_back(prog._SYMBptr->feuille.eval(eval_level(contextptr),newcontextptr));
      res=*args._VECTptr;
    }
    else
      res=rpn_eval(prog,*args._VECTptr,newcontextptr);
    leave(protect,names,newcontextptr);
    return gen(res,_RPN_STACK__VECT);
  }
  static const char _RPN_LOCAL_s []="RPN_LOCAL";
  static define_unary_function_eval2_index (130,__RPN_LOCAL,&_RPN_LOCAL,_RPN_LOCAL_s,&printasRPN_LOCAL);
  define_unary_function_ptr5( at_RPN_LOCAL ,alias_at_RPN_LOCAL ,&__RPN_LOCAL,0,T_RPN_OP);

  static string printasRPN_FOR(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) && (feuille._VECTptr->size()!=2))
      return "Invalid_RPN_FOR";
    string s;
    gen controle=feuille._VECTptr->front();
    gen prog=feuille._VECTptr->back();
    bool is_start= (controle[0].print(contextptr)==" j");
    if (is_start)
      s="START ";
    else
      s="FOR "+controle[0].print(contextptr)+" ";
    s += printinner_VECT(*prog._VECTptr,_RPN_FUNC__VECT,contextptr);
    if (is_one(controle[1]))
      s+=" NEXT";
    else
      s+= " "+controle[1].print(contextptr)+" STEP";
    return s;
  }
  gen symb_RPN_FOR(const gen & a,const gen & b){
    return symbolic(at_RPN_FOR,makesequence(a,b));
  }
  gen _RPN_FOR(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // stack level 4=init
    // stack level 3=end
    // stack level 2=[init_var,step]
    // level 1=program to execute, 
    if (args.type!=_VECT)
      return symbolic(at_RPN_FOR,args);
    int s=args._VECTptr->size();
    if (s<4)
      return gentoofewargs("RPN_FOR must have at least 4 args");
    gen prog=args._VECTptr->back();
    args._VECTptr->pop_back();
    vecteur controle=*(args._VECTptr->back()._VECTptr); // it must be a vector
    args._VECTptr->pop_back();
    gen fin=args._VECTptr->back();
    args._VECTptr->pop_back();
    gen debut=args._VECTptr->back();
    args._VECTptr->pop_back();
    // Initialization
    vecteur names(1,controle[0]);
    gen test=inferieur_egal(controle[0],fin,contextptr);
    context * newcontextptr = (context *) contextptr;
    int protect=bind(vecteur(1,debut),names,newcontextptr);
    vecteur res;
    for (;!is_zero(test.eval(eval_level(newcontextptr),newcontextptr).evalf(eval_level(contextptr),newcontextptr));sto(eval(controle[0]+controle[1],eval_level(newcontextptr),newcontextptr),controle[0],newcontextptr)){
      res=rpn_eval(prog,*args._VECTptr,newcontextptr);
    }
    leave(protect,names,newcontextptr);
    return gen(res,_RPN_STACK__VECT);
  }
  static const char _RPN_FOR_s []="RPN_FOR";
  static define_unary_function_eval2_index (132,__RPN_FOR,&_RPN_FOR,_RPN_FOR_s,&printasRPN_FOR);
  define_unary_function_ptr5( at_RPN_FOR ,alias_at_RPN_FOR ,&__RPN_FOR,0,T_RPN_OP);

  static string printasRPN_WHILE(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) && (feuille._VECTptr->size()!=2))
      return "Invalid_RPN_WHILE";
    return "WHILE "+ printinner_VECT(*feuille._VECTptr->front()._VECTptr,_RPN_FUNC__VECT,contextptr) + " REPEAT "+printinner_VECT(*feuille._VECTptr->back()._VECTptr,_RPN_FUNC__VECT,contextptr)+ " END ";
  }
  gen symb_RPN_WHILE(const gen & a,const gen & b){
    return symbolic(at_RPN_WHILE,makesequence(a,b));
  }
  gen _RPN_WHILE(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // stack level 2=condition
    // level 1=program to execute 
    if (args.type!=_VECT)
      return symbolic(at_RPN_WHILE,args);
    int s=args._VECTptr->size();
    if (s<2)
      return gentoofewargs("RPN_WHILE must have at least 2 args");
    gen prog=args._VECTptr->back();
    args._VECTptr->pop_back();
    gen controle=args._VECTptr->back();
    args._VECTptr->pop_back();
    vecteur res;
    for (;;){
      res=rpn_eval(controle,*args._VECTptr,contextptr);
      if (args._VECTptr->empty())
	return gentoofewargs("WHILE");
      gen tmp=args._VECTptr->back();
      args._VECTptr->pop_back();
      if (is_zero(tmp.eval(1,contextptr).evalf(1,contextptr)))
	break;
      res=rpn_eval(prog,*args._VECTptr,contextptr);
    }
    return gen(res,_RPN_STACK__VECT);
  }
  static const char _RPN_WHILE_s []="RPN_WHILE";
  static define_unary_function_eval2_index (134,__RPN_WHILE,&_RPN_WHILE,_RPN_WHILE_s,&printasRPN_WHILE);
  define_unary_function_ptr5( at_RPN_WHILE ,alias_at_RPN_WHILE ,&__RPN_WHILE,0,T_RPN_OP);

  static string printasRPN_CASE(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return "Invalid_RPN_CASE";
    vecteur v=*feuille._VECTptr;
    if ((v.size()!=1) ||(v.front().type!=_VECT))
      return "Invalid_RPN_CASE";
    string res("CASE ");
    const_iterateur it=v.front()._VECTptr->begin(),itend=v.front()._VECTptr->end();
    for (;it!=itend;){
      res += printinner_VECT(*it->_VECTptr,_RPN_FUNC__VECT,contextptr);
      ++it;
      if (it==itend)
	break;
      res += " THEN " + printinner_VECT(*it->_VECTptr,_RPN_FUNC__VECT,contextptr) + " END ";
      ++it;
    }
    return res+" END ";
  }
  gen symb_RPN_CASE(const gen & a){
    return symbolic(at_RPN_CASE,vecteur(1,a));
  }
  gen _RPN_CASE(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // level 1=[case1, prg1, case2,prg2,..., [default]]
    if (args.type!=_VECT)
      return symbolic(at_RPN_CASE,args);
    int s=args._VECTptr->size();
    if (s<1)
      return gentoofewargs("RPN_CASE must have at least 1 arg");
    vecteur controle=*args._VECTptr->back()._VECTptr;
    args._VECTptr->pop_back();
    const_iterateur it=controle.begin(),itend=controle.end();
    vecteur res;
    for (;it!=itend;){
      res=rpn_eval(*it,*args._VECTptr,contextptr);
      if (args._VECTptr->empty())
	return gentoofewargs("CASE");
      ++it;
      if (it==itend) // default of the case struct
	break;
      gen test=args._VECTptr->back();
      args._VECTptr->pop_back();
      if (!(is_zero(test.eval(1,contextptr).evalf(1,contextptr)))){
	res=rpn_eval(*it,*args._VECTptr,contextptr);
	break;
      }
      ++it;
    }
    return gen(*args._VECTptr,_RPN_STACK__VECT);
  }
  static const char _RPN_CASE_s []="RPN_CASE";
  static define_unary_function_eval2_index (128,__RPN_CASE,&_RPN_CASE,_RPN_CASE_s,&printasRPN_CASE);
  define_unary_function_ptr5( at_RPN_CASE ,alias_at_RPN_CASE ,&__RPN_CASE,0,T_RPN_OP);

  static string printasRPN_UNTIL(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) && (feuille._VECTptr->size()!=2))
      return "Invalid_RPN_UNTIL";
    return "DO "+ printinner_VECT(*feuille._VECTptr->front()._VECTptr,_RPN_FUNC__VECT,contextptr) + " UNTIL "+printinner_VECT(*feuille._VECTptr->back()._VECTptr,_RPN_FUNC__VECT,contextptr)+ " END ";
  }
  gen symb_RPN_UNTIL(const gen & a,const gen & b){
    return symbolic(at_RPN_UNTIL,makesequence(a,b));
  }
  gen _RPN_UNTIL(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // stack level 2=program
    // level 1=condition 
    if (args.type!=_VECT)
      return symbolic(at_RPN_UNTIL,args);
    int s=args._VECTptr->size();
    if (s<2)
      return gentoofewargs("RPN_UNTIL must have at least 2 args");
    gen controle=args._VECTptr->back();
    args._VECTptr->pop_back();
    gen prog=args._VECTptr->back();
    args._VECTptr->pop_back();
    vecteur res;
    for (;;){
      res=rpn_eval(prog,*args._VECTptr,contextptr);
      res=rpn_eval(controle,*args._VECTptr,contextptr);
      if (args._VECTptr->empty())
	return gentoofewargs("UNTIL");
      gen tmp=args._VECTptr->back();
      args._VECTptr->pop_back();
      if (!is_zero(tmp.eval(eval_level(contextptr),contextptr).evalf(eval_level(contextptr),contextptr)))
	break;
    }
    return gen(res,_RPN_STACK__VECT);
  }
  static const char _RPN_UNTIL_s []="RPN_UNTIL";
  static define_unary_function_eval2 (__RPN_UNTIL,&_RPN_UNTIL,_RPN_UNTIL_s,&printasRPN_UNTIL);
  define_unary_function_ptr( at_RPN_UNTIL ,alias_at_RPN_UNTIL ,&__RPN_UNTIL);

  // RPN evaluation loop (_VECTEVAL), no return stack currently
  vecteur rpn_eval(const vecteur & prog,vecteur & pile,const context * contextptr){
    const_iterateur it=prog.begin(),itend=prog.end();
    for (;it!=itend;++it){
      if (it->type==_FUNC){
	// test nargs with subtype
	int nargs=it->subtype;
	if (nargs>signed(pile.size()))
	  return vecteur(1,gentoofewargs(it->print(contextptr)+": stack "+gen(pile).print(contextptr)));
	if (nargs==1){
	  pile.back()=(*it->_FUNCptr)(pile.back(),contextptr);
	}
	else {
	  if (nargs){
	    vecteur v(nargs);
	    for (int k=nargs-1;k>=0;--k){
	      v[k]=pile.back();
	      pile.pop_back();
	    }
	    pile.push_back((*it->_FUNCptr)(v,contextptr));
	  }
	  else {
	    gen res;
	    if (*it->_FUNCptr==at_eval){ // eval stack level 1
	      if (pile.empty())
		return vecteur(1,gentoofewargs("EVAL"));
	      res=pile.back();
	      pile.pop_back();
	      if ( (res.type==_SYMB) && (res._SYMBptr->sommet==at_rpn_prog))
		res=res._SYMBptr->feuille;
	      res=rpn_eval(res,pile,contextptr);
	    }
	    else
	      res=(*it->_FUNCptr)(pile,contextptr);
	    if ( (res.type==_VECT) && (res.subtype==_RPN_STACK__VECT) )
	      pile= *res._VECTptr;
	    else
	      pile= vecteur(1,res);
	  }
	}
      }
      else {
	// test for special symbolic (control struct)
	const unary_function_ptr control_op[]={*at_RPN_LOCAL,*at_RPN_FOR,*at_IFTE,*at_RPN_CASE,*at_RPN_WHILE,*at_RPN_UNTIL,0};
	if ( (it->type==_SYMB) && equalposcomp(control_op,it->_SYMBptr->sommet)){
	  // push args of it to the stack and call sommet on the stack
	  if (it->_SYMBptr->feuille.type!=_VECT) // should not happen!
	    pile.push_back(it->_SYMBptr->feuille);
	  else
	    pile=mergevecteur(pile,*it->_SYMBptr->feuille._VECTptr);
	  gen res=it->_SYMBptr->sommet(pile,contextptr);
	  if ( (res.type==_VECT) && (res.subtype==_RPN_STACK__VECT) )
	    pile= *res._VECTptr;
	  else
	    pile= vecteur(1,res);	  
	}
	else {
	  if ( (it->type!=_VECT) 
	       // || (it->subtype==_RPN_FUNC__VECT) 
	       ){
	    gen res=it->eval(1,contextptr);
	    if ( (res.type==_VECT) && (res.subtype==_RPN_STACK__VECT))
	      pile=*res._VECTptr;
	    else
	      pile.push_back(res);
	  }
	  else
	    pile.push_back(*it);
	}
      }
    }
    return pile;
  }

  vecteur rpn_eval(const gen & prog,vecteur & pile,const context * contextptr){
    if (prog.type!=_VECT)
      return rpn_eval(vecteur(1,prog),pile,contextptr);
    else
      return rpn_eval(*prog._VECTptr,pile,contextptr);
  }

  static string printasrpn_prog(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return "<< "+feuille.print(contextptr)+" >>";
    return "<< "+printinner_VECT(*feuille._VECTptr,_RPN_FUNC__VECT,contextptr)+" >>";
  }
  gen symb_rpn_prog(const gen & args){
    return symbolic(at_rpn_prog,args);
  }
  gen _rpn_prog(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!rpn_mode(contextptr) || (args.type!=_VECT))
      return symbolic(at_rpn_prog,args);
    vecteur pile(history_out(contextptr));
    *logptr(contextptr) << pile << " " << args << endl;
    return gen(rpn_eval(*args._VECTptr,pile,contextptr),_RPN_STACK__VECT);
  }
  static const char _rpn_prog_s []="rpn_prog";
  static define_unary_function_eval2_index (83,__rpn_prog,&_rpn_prog,_rpn_prog_s,&printasrpn_prog);
  define_unary_function_ptr5( at_rpn_prog ,alias_at_rpn_prog,&__rpn_prog,_QUOTE_ARGUMENTS,0);

#endif // RTOS_THREADX

  gen _RCL(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // stack level 2=condition
    // level 1=program to execute 
    if (args.type!=_IDNT)
      return symbolic(at_RCL,args);
    return args._IDNTptr->eval(1,args,contextptr);
  }
  static const char _RCL_s []="RCL";
  static define_unary_function_eval (__RCL,&_RCL,_RCL_s);
  define_unary_function_ptr5( at_RCL ,alias_at_RCL ,&__RCL,0,T_RPN_OP);

#if defined(__APPLE__) // || defined(__FreeBSD__)
  static int int_one (struct dirent *unused){
    return 1;
  }
#else
  static int int_one (const struct dirent *unused){
    return 1;
  }
#endif

  gen _VARS(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur res;
    if (contextptr){
      if (contextptr->globalcontextptr && contextptr->globalcontextptr->tabptr){
	sym_tab::const_iterator it=contextptr->globalcontextptr->tabptr->begin(),itend=contextptr->globalcontextptr->tabptr->end();
	for (;it!=itend;++it)
	  res.push_back(identificateur(it->first));
      }
      return res;
    }
    if (!variables_are_files(contextptr)){
      sym_tab::const_iterator it=syms().begin(),itend=syms().end();
      for (;it!=itend;++it){
	gen id=it->second;
	if (id.type==_IDNT && id._IDNTptr->value){
	  res.push_back(id);
	}
      }
      if (is_one(args) && current_folder_name.type==_IDNT && current_folder_name._IDNTptr->value){ // add variables of the current folder
	gen & tmp=*current_folder_name._IDNTptr->value;
	if (tmp.type==_VECT){
	  vecteur v=*current_folder_name._IDNTptr->value->_VECTptr;
	  iterateur it=v.begin(),itend=v.end();
	  for (;it!=itend;++it){
	    if (it->type!=_VECT || it->_VECTptr->size()!=2)
	      continue;
	    vecteur & w=*it->_VECTptr;
	    res.push_back(w[0]);
	  }
	}
      }
      return res;
    }
#ifndef VARIABLE_ARE_FILES
    return undef;
#else
    struct dirent **eps;
    int n;
    n = scandir ("./", &eps, int_one, alphasort);
    if (n >= 0){
      string s;
      int cnt;
      for (cnt = 0; cnt < n; ++cnt){
	s=string(eps[cnt]->d_name);
	unsigned k=0;
	for (;k<s.size();++k){
	  if (!isalphan(s[k]))
	    break;
	}
	if ( (k==s.size()-4) && (s[k]=='.') && (s.substr(k+1,3)=="cas") )
	  res.push_back(identificateur(s.substr(0,k)));
      }
      if ( rpn_mode(contextptr) && (args.type==_VECT) && (args.subtype==_RPN_STACK__VECT) ){
	args._VECTptr->push_back(res);
	return args;
      }
      return res;
    }
    else
      settypeerr ("Couldn't open the directory");
    return 0;
#endif
  }

  static const char _VARS_s []="VARS";
  static define_unary_function_eval (__VARS,&_VARS,_VARS_s);
  define_unary_function_ptr( at_VARS ,alias_at_VARS ,&__VARS);

  gen _purge(const gen & args,const context * contextptr) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (rpn_mode(contextptr) && (args.type==_VECT)){
      if (!args._VECTptr->size())
	return gentoofewargs("purge");
      gen apurger=args._VECTptr->back();
      _purge(apurger,contextptr);
      args._VECTptr->pop_back();
      return gen(*args._VECTptr,_RPN_STACK__VECT);
    }
    if (args.type==_VECT)
      return apply(args,contextptr,giac::_purge);
    if (args.is_symb_of_sommet(at_at)){
      gen & f = args._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2){
	gen m = eval(f._VECTptr->front(),eval_level(contextptr),contextptr);
	gen indice=eval(f._VECTptr->back(),eval_level(contextptr),contextptr);
	if (m.type==_MAP){
	  gen_map::iterator it=m._MAPptr->find(indice),itend=m._MAPptr->end();
	  if (it==itend)
	    return gensizeerr(gettext("Bad index")+indice.print(contextptr));
	  m._MAPptr->erase(it);
	  return 1;
	}
      }
    }
    if (args.type!=_IDNT)
      return symbolic(at_purge,args);
    // REMOVED! args.eval(eval_level(contextptr),contextptr); 
    if (contextptr){
      if (contextptr->globalcontextptr!=contextptr){ 
	// purge a local variable = set it to assume(DOM_SYMBOLIC)
	gen a2(_SYMB);
	a2.subtype=1;
	return sto(gen(makevecteur(a2),_ASSUME__VECT),args,contextptr);
      }
      // purge a global variable
      sym_tab::iterator it=contextptr->tabptr->find(args._IDNTptr->id_name),itend=contextptr->tabptr->end();
      if (it==itend)
	return string2gen("No such variable "+args.print(contextptr),false);
      gen res=it->second;
      if (it->second.type==_POINTER_ && it->second.subtype==_THREAD_POINTER)
	return gentypeerr(args.print(contextptr)+" is locked by thread "+it->second.print(contextptr));
      contextptr->tabptr->erase(it);
      return res;
    }
    if (current_folder_name.type==_IDNT && current_folder_name._IDNTptr->value && current_folder_name._IDNTptr->value->type==_VECT){
      vecteur v=*current_folder_name._IDNTptr->value->_VECTptr;
      iterateur it=v.begin(),itend=v.end();
      gen val;
      for (;it!=itend;++it){
	if (it->type!=_VECT || it->_VECTptr->size()!=2)
	  continue;
	vecteur & w=*it->_VECTptr;
	if (w[0]==args){
	  val=w[1];
	  break;
	}
      }
      if (it!=itend){
	v.erase(it);
	gen res=gen(v,_FOLDER__VECT);
	*current_folder_name._IDNTptr->value=res;
#ifdef HAVE_SIGNAL_H_OLD
	if (!child_id && signal_store)
	  _signal(symb_quote(symbolic(at_sto,makesequence(res,current_folder_name))),contextptr);
#endif
	return val;
      }
    }
    if (args._IDNTptr->value){
#ifndef RTOS_THREADX
#ifndef BESTA_OS
      if (variables_are_files(contextptr))
	unlink((args._IDNTptr->name()+string(cas_suffixe)).c_str());
#endif
#endif
      gen res=*args._IDNTptr->value;
      if (res.type==_VECT && res.subtype==_FOLDER__VECT){
	if (res._VECTptr->size()!=1)
	  return gensizeerr(gettext("Non-empty folder"));
      }
      delete args._IDNTptr->value;
      args._IDNTptr->value=0;
#ifdef HAVE_SIGNAL_H_OLD
      if (!child_id && signal_store)
	_signal(symb_quote(symb_purge(args)),contextptr);
#endif
      return res;
    }
    else
      return string2gen(args.print(contextptr)+" not assigned",false);
  }
  static const char _purge_s []="purge";
  static define_unary_function_eval_quoted (__purge,&_purge,_purge_s);
  define_unary_function_ptr5( at_purge ,alias_at_purge,&__purge,_QUOTE_ARGUMENTS,0);

  static string printasdivision(const gen & feuille,const char * s,GIAC_CONTEXT){
    if (feuille.type!=_VECT || feuille._VECTptr->size()!=2)
      return printsommetasoperator(feuille,s,contextptr);
    gen n=feuille._VECTptr->front();
    bool need=need_parenthesis(n);
    string res;
    if (need) res+='(';
    res += n.print(contextptr);
    if (need) res += ')';
    res += '/';
    gen f=feuille._VECTptr->back();
    if ( (f.type==_SYMB && ( f._SYMBptr->sommet==at_plus || f._SYMBptr->sommet==at_prod || f._SYMBptr->sommet==at_inv  || need_parenthesis(f._SYMBptr->sommet) )) || (f.type==_CPLX) || (f.type==_MOD))
      res += '('+f.print(contextptr)+')';
    else
      res += f.print(contextptr);
    return res;
  }

  static string texprintasdivision(const gen & feuille,const char * s,GIAC_CONTEXT){
    if (feuille.type!=_VECT || feuille._VECTptr->size()!=2)
      return "invalid /";
    return "\\frac{"+gen2tex(feuille._VECTptr->front(),contextptr)+"}{"+gen2tex(feuille._VECTptr->back(),contextptr)+"}";
  }
  static gen symb_division(const gen & args){
    return symbolic(at_division,args);
  }
  gen _division(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) )
      return symb_division(args);
    gen a=args._VECTptr->front(),b=args._VECTptr->back();
    if (a.is_approx()){
      gen b1;
      if (has_evalf(b,b1,1,contextptr) && b.type!=b1.type)
	return rdiv(a,b1,contextptr);
    }
    if (b.is_approx()){
      gen a1;
      if (has_evalf(a,a1,1,contextptr) && a.type!=a1.type)
	return rdiv(a1,b,contextptr);
    }
    return rdiv(a,b,contextptr);
  }
  static const char _division_s []="/";
  static define_unary_function_eval4_index (10,__division,&_division,_division_s,&printasdivision,&texprintasdivision);
  define_unary_function_ptr( at_division ,alias_at_division ,&__division);

  static gen symb_binary_minus(const gen & args){
    return symbolic(at_binary_minus,args);
  }
  gen _binary_minus(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) )
      return symb_binary_minus(args);
    return args._VECTptr->front()-args._VECTptr->back();
  }
  static const char _binary_minus_s[]="-";
  static define_unary_function_eval4_index (6,__binary_minus,&_binary_minus,_binary_minus_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_binary_minus ,alias_at_binary_minus ,&__binary_minus);

  vecteur tab2vecteur(gen tab[]){
    vecteur res;
    for (;!is_zero(*tab);++tab)
      res.push_back(*tab);
    return res;
  }

  // hp38 compatibility
  // model
  gen _hp38(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) )
      return gensizeerr(contextptr);
    return undef;
  }
  static const char _hp38_s[]="hp38";
  static define_unary_function_eval (__hp38,&_hp38,_hp38_s);
  define_unary_function_ptr5( at_hp38 ,alias_at_hp38,&__hp38,0,true);

  // real/complex
  gen _ABS(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return abs(args,contextptr);
    if (args.subtype)
      return apply(args,giac::_abs,contextptr);
    vecteur v;
    aplatir(*args._VECTptr,v);
    return _l2norm(v,contextptr);
  }
  static const char _ABS_s[]="ABS";
  static define_unary_function_eval (__ABS,&_ABS,_ABS_s);
  define_unary_function_ptr5( at_ABS ,alias_at_ABS,&__ABS,0,T_UNARY_OP_38);

  gen _ARG(const gen & g,GIAC_CONTEXT){
    if (angle_radian(contextptr)==0)
      return arg(evalf2bcd(g,1,contextptr),contextptr);
    return arg(g,contextptr);
  }
  static const char _ARG_s[]="ARG";
  static define_unary_function_eval (__ARG,&_ARG,_ARG_s);
  define_unary_function_ptr5( at_ARG ,alias_at_ARG,&__ARG,0,T_UNARY_OP_38);

  static const char _CONJ_s[]="CONJ";
  static define_unary_function_eval (__CONJ,(const gen_op_context)giac::conj,_CONJ_s);
  define_unary_function_ptr5( at_CONJ ,alias_at_CONJ,&__CONJ,0,T_UNARY_OP_38);

  static const char _RE_s[]="RE";
  static define_unary_function_eval (__RE,(const gen_op_context)giac::re,_RE_s);
  define_unary_function_ptr5( at_RE ,alias_at_RE,&__RE,0,T_UNARY_OP_38);

  static const char _IM_s[]="IM";
  static define_unary_function_eval (__IM,(const gen_op_context)giac::im,_IM_s);
  define_unary_function_ptr5( at_IM ,alias_at_IM,&__IM,0,T_UNARY_OP_38);

  static const char _FLOOR_s[]="FLOOR";
  static define_unary_function_eval (__FLOOR,(const gen_op_context)giac::_floor,_FLOOR_s);
  define_unary_function_ptr5( at_FLOOR ,alias_at_FLOOR,&__FLOOR,0,T_UNARY_OP_38);

  static const char _CEILING_s[]="CEILING";
  static define_unary_function_eval (__CEILING,(const gen_op_context)giac::_ceil,_CEILING_s);
  define_unary_function_ptr5( at_CEILING ,alias_at_CEILING,&__CEILING,0,T_UNARY_OP_38);

  gen fPart(const gen & g,GIAC_CONTEXT);
  static const char _FRAC_s[]="FRAC";
  static define_unary_function_eval (__FRAC,&giac::fPart,_FRAC_s);
  define_unary_function_ptr5( at_FRAC ,alias_at_FRAC,&__FRAC,0,T_UNARY_OP_38);

  static const char _MAX_s[]="MAX";
  static define_unary_function_eval (__MAX,&giac::_max,_MAX_s);
  define_unary_function_ptr5( at_MAX ,alias_at_MAX,&__MAX,0,T_UNARY_OP_38);

  static const char _MIN_s[]="MIN";
  static define_unary_function_eval (__MIN,&giac::_min,_MIN_s);
  define_unary_function_ptr5( at_MIN ,alias_at_MIN,&__MIN,0,T_UNARY_OP_38);

  gen _MODULO(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2){
      gen a=args._VECTptr->front(),b=args._VECTptr->back();
      if (is_zero(b))
	return a;
      if (a.type==_FLOAT_){
	if (b.type==_FLOAT_)
	  return fmod(a._FLOAT_val,b._FLOAT_val);
	if (b.type==_INT_)
	  return fmod(a._FLOAT_val,giac_float(b.val));
      }
      return a-b*_floor(a/b,contextptr);
    }
    return gentypeerr(contextptr);
  }
  static const char _MOD_s[]="MOD";
  static define_unary_function_eval4 (__MOD,&giac::_MODULO,_MOD_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr5( at_MOD ,alias_at_MOD,&__MOD,0,T_DIV);

  static const char _ROUND_s[]="ROUND";
  static define_unary_function_eval (__ROUND,&giac::_round,_ROUND_s); 
  define_unary_function_ptr5( at_ROUND ,alias_at_ROUND,&__ROUND,0,T_UNARY_OP_38);

  gen _trunc(const gen & args,GIAC_CONTEXT);

  static const char _TRUNCATE_s[]="TRUNCATE";
  static define_unary_function_eval (__TRUNCATE,&giac::_trunc,_TRUNCATE_s); 
  define_unary_function_ptr5( at_TRUNCATE ,alias_at_TRUNCATE,&__TRUNCATE,0,T_UNARY_OP_38);

  static const char _QUOTE_s[]="QUOTE";
  static define_unary_function_eval_quoted (__QUOTE,&giac::quote,_QUOTE_s); 
  define_unary_function_ptr5( at_QUOTE ,alias_at_QUOTE,&__QUOTE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  // Note that 0 is necessary otherwise conflict on freeBSD
  static const char _SIGN_s[]="SIGN";
  static define_unary_function_eval (__SIGN0,&giac::sign,_SIGN_s);
  define_unary_function_ptr5( at_SIGN ,alias_at_SIGN,&__SIGN0,0,T_UNARY_OP_38);
  
  // proba
  static const char _COMB_s[]="COMB";
  static define_unary_function_eval (__COMB,&giac::_comb,_COMB_s); 
  define_unary_function_ptr5( at_COMB ,alias_at_COMB,&__COMB,0,T_UNARY_OP_38);  

  static const char _PERM_s[]="PERM";
  static define_unary_function_eval (__PERM,&giac::_perm,_PERM_s); 
  define_unary_function_ptr5( at_PERM ,alias_at_PERM,&__PERM,0,T_UNARY_OP_38);  

  static string printasRANDOM(const gen & feuille,const char * s,GIAC_CONTEXT){
    if (feuille.type==_VECT && feuille._VECTptr->empty())
      return "RANDOM";
    return "(RANDOM "+feuille.print()+")";
  }
  gen _RANDOM(const gen & g0,GIAC_CONTEXT){
    gen g(g0);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    is_integral(g);
    if (g.type==_CPLX)
      return _rand(gen(makevecteur(*g._CPLXptr,*(g._CPLXptr+1)),_SEQ__VECT),contextptr);
    if (g.type!=_VECT || g.subtype!=_SEQ__VECT || !g._VECTptr->empty())
      return _rand(g,contextptr);
    return double(giac_rand(contextptr))/rand_max3;
  }
  static const char _RANDOM_s[]="RANDOM";
  static define_unary_function_eval2 (__RANDOM,&giac::_RANDOM,_RANDOM_s,&printasRANDOM); 
  define_unary_function_ptr5( at_RANDOM ,alias_at_RANDOM,&__RANDOM,0,T_RETURN);  

  static const char _RANDSEED_s[]="RANDSEED";
  static define_unary_function_eval (__RANDSEED,&giac::_srand,_RANDSEED_s); 
  define_unary_function_ptr5( at_RANDSEED ,alias_at_RANDSEED,&__RANDSEED,0,T_RETURN);  

  gen _MAXREAL(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#if 0 // def BCD
    return_bcd_maxreal;
#else
    return 1.79769313486e+308;
#endif
  }
  static const char _MAXREAL_s[]="MAXREAL";
  static define_unary_function_eval (__MAXREAL,&giac::_MAXREAL,_MAXREAL_s); 
  define_unary_function_ptr5( at_MAXREAL ,alias_at_MAXREAL,&__MAXREAL,0,T_RETURN);  

  gen _MINREAL(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#if 0 // def BCD
    return_bcd_minreal;
    // return_bcd_minreal;
#else
    return 1.26480805335e-321;
#endif
  }
  static const char _MINREAL_s[]="MINREAL";
  static define_unary_function_eval (__MINREAL,&giac::_MINREAL,_MINREAL_s); 
  define_unary_function_ptr5( at_MINREAL ,alias_at_MINREAL,&__MINREAL,0,T_RETURN);  

  // transcendent
  static const char _EXP_s[]="EXP";
  static define_unary_function_eval (__EXP,&giac::exp,_EXP_s);
  define_unary_function_ptr5( at_EXP ,alias_at_EXP,&__EXP,0,T_UNARY_OP_38);

#if 0
  define_partial_derivative_onearg_genop( D_at_expm1,"D_at_expm1",&giac::exp);
#endif
  gen _EXPM1(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return exp(g,contextptr)-1;
  }
  static const char _EXPM1_s[]="EXPM1";
  static define_unary_function_eval (__EXPM1,&giac::_EXPM1,_EXPM1_s);
  define_unary_function_ptr5( at_EXPM1 ,alias_at_EXPM1,&__EXPM1,0,T_UNARY_OP_38);

#if 0
  static gen d_lnp1(const gen & args,GIAC_CONTEXT){
    return inv(args+1,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_lnp1,"D_at_lnp1",&d_lnp1);
#endif
  gen _LNP1(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return ln(g+1,contextptr);
  }
  static const char _LNP1_s[]="LNP1";
  static define_unary_function_eval (__LNP1,&giac::_LNP1,_LNP1_s);
  define_unary_function_ptr5( at_LNP1 ,alias_at_LNP1,&__LNP1,0,T_UNARY_OP_38);

  static const char _LN_s[]="LN";
  static define_unary_function_eval (__LN,&giac::ln,_LN_s);
  define_unary_function_ptr5( at_LN ,alias_at_LN,&__LN,0,T_UNARY_OP_38);

  gen _LOG(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2)
      return _logb(g,contextptr);
    return giac::log10(g,contextptr);
  }
  static const char _LOG_s[]="LOG";
  static define_unary_function_eval (__LOG,&_LOG,_LOG_s);
  define_unary_function_ptr5( at_LOG ,alias_at_LOG,&__LOG,0,T_UNARY_OP_38);

  static const char _ALOG_s[]="ALOG";
  static define_unary_function_eval (__ALOG,&giac::alog10,_ALOG_s);
  define_unary_function_ptr5( at_ALOG ,alias_at_ALOG,&__ALOG,0,T_UNARY_OP_38);

  static const char _SIN_s[]="SIN";
  static define_unary_function_eval (__SIN,&giac::sin,_SIN_s);
  define_unary_function_ptr5( at_SIN ,alias_at_SIN,&__SIN,0,T_UNARY_OP_38);

  static const char _COS_s[]="COS";
  static define_unary_function_eval (__COS,&giac::cos,_COS_s);
  define_unary_function_ptr5( at_COS ,alias_at_COS,&__COS,0,T_UNARY_OP_38);

  static const char _TAN_s[]="TAN";
  static define_unary_function_eval (__TAN,&giac::tan,_TAN_s);
  define_unary_function_ptr5( at_TAN ,alias_at_TAN,&__TAN,0,T_UNARY_OP_38);

  static const char _ASIN_s[]="ASIN";
  static define_unary_function_eval (__ASIN,&giac::asin,_ASIN_s);
  define_unary_function_ptr5( at_ASIN ,alias_at_ASIN,&__ASIN,0,T_UNARY_OP_38);

  static const char _ACOS_s[]="ACOS";
  static define_unary_function_eval (__ACOS,&giac::acos,_ACOS_s);
  define_unary_function_ptr5( at_ACOS ,alias_at_ACOS,&__ACOS,0,T_UNARY_OP_38);

  static const char _ATAN_s[]="ATAN";
  static define_unary_function_eval (__ATAN,&giac::atan,_ATAN_s);
  define_unary_function_ptr5( at_ATAN ,alias_at_ATAN,&__ATAN,0,T_UNARY_OP_38);

  static const char _SINH_s[]="SINH";
  static define_unary_function_eval (__SINH,&giac::sinh,_SINH_s);
  define_unary_function_ptr5( at_SINH ,alias_at_SINH,&__SINH,0,T_UNARY_OP_38);

  static const char _COSH_s[]="COSH";
  static define_unary_function_eval (__COSH,&giac::cosh,_COSH_s);
  define_unary_function_ptr5( at_COSH ,alias_at_COSH,&__COSH,0,T_UNARY_OP_38);

  static const char _TANH_s[]="TANH";
  static define_unary_function_eval (__TANH,&giac::tanh,_TANH_s);
  define_unary_function_ptr5( at_TANH ,alias_at_TANH,&__TANH,0,T_UNARY_OP_38);

  static const char _ASINH_s[]="ASINH";
  static define_unary_function_eval (__ASINH,&giac::asinh,_ASINH_s);
  define_unary_function_ptr5( at_ASINH ,alias_at_ASINH,&__ASINH,0,T_UNARY_OP_38);

  static const char _ACOSH_s[]="ACOSH";
  static define_unary_function_eval (__ACOSH,&giac::acosh,_ACOSH_s);
  define_unary_function_ptr5( at_ACOSH ,alias_at_ACOSH,&__ACOSH,0,T_UNARY_OP_38);

  static const char _ATANH_s[]="ATANH";
  static define_unary_function_eval (__ATANH,&giac::atanh,_ATANH_s);
  define_unary_function_ptr5( at_ATANH ,alias_at_ATANH,&__ATANH,0,T_UNARY_OP_38);

  /* matrices */
  gen _ADDROW(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    if (!v.empty() && v[0].type==_IDNT){
      gen v0=v[0];
      gen g=eval(args,eval_level(contextptr),contextptr);
      if (ckmatrix(g[0])){
	gen tmp=_ADDROW(g,contextptr);
	return is_undef(tmp)?tmp:sto(tmp,v0,contextptr);
      }
    }
    v[2]=_floor(v[2],contextptr);
    if (v.size()!=3 || !ckmatrix(v[0]) || v[1].type!=_VECT || v[2].type!=_INT_)
      return gentypeerr(contextptr);
    vecteur & w = *v[0]._VECTptr;
    if (w.front()._VECTptr->size()!=v[1]._VECTptr->size())
      return gendimerr(contextptr);
    int s=w.size();
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int l2=v[2].val-shift;
    if (l2<0 || l2>s)
      return gendimerr(contextptr);
    matrice res(w.begin(),w.begin()+l2);
    res.push_back(v[1]);
    for (int i=l2;i<s;++i)
      res.push_back(w[i]);
    return res;
  }
  static const char _ADDROW_s[]="ADDROW";
  static define_unary_function_eval_quoted (__ADDROW,&giac::_ADDROW,_ADDROW_s);
  define_unary_function_ptr5( at_ADDROW ,alias_at_ADDROW,&__ADDROW,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _ADDCOL(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    if (!v.empty() && v[0].type==_IDNT){
      gen v0=v[0];
      gen g=eval(args,eval_level(contextptr),contextptr);
      if (ckmatrix(g[0])){
	gen tmp=_ADDCOL(g,contextptr);
	return is_undef(tmp)?tmp:sto(tmp,v0,contextptr);
      }
    }
    if (v.size()!=3 || !ckmatrix(v[0]))
      return gensizeerr(contextptr);
    matrice m;
    mtran(*v[0]._VECTptr,m);
    gen res=_ADDROW(makesequence(m,v[1],v[2]),contextptr);
    if (res.type==_VECT){
      mtran(*res._VECTptr,m);
      res=m;
    }
    return res;
  }
  static const char _ADDCOL_s[]="ADDCOL";
  static define_unary_function_eval_quoted (__ADDCOL,&giac::_ADDCOL,_ADDCOL_s);
  define_unary_function_ptr5( at_ADDCOL ,alias_at_ADDCOL,&__ADDCOL,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _SCALE(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gensizeerr(contextptr);
    vecteur v =*g._VECTptr;
    swapgen(v[0],v[1]);
    return _mRow(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _SCALE_s[]="SCALE";
  static define_unary_function_eval_quoted (__SCALE,&giac::_SCALE,_SCALE_s);
  define_unary_function_ptr5( at_SCALE ,alias_at_SCALE,&__SCALE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _SCALEADD(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=4)
      return gensizeerr(contextptr);
    vecteur v =*g._VECTptr;
    swapgen(v[0],v[1]);
    return _mRowAdd(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _SCALEADD_s[]="SCALEADD";
  static define_unary_function_eval_quoted (__SCALEADD,&giac::_SCALEADD,_SCALEADD_s);
  define_unary_function_ptr5( at_SCALEADD ,alias_at_SCALEADD,&__SCALEADD,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _SWAPCOL(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    if (!v.empty() && v[0].type==_IDNT){
      gen v0=v[0];
      gen g=eval(args,eval_level(contextptr),contextptr);
      if (ckmatrix(g[0]))
	return sto(_SWAPCOL(g,contextptr),v0,contextptr);
    }
    if (v.size()!=3 || !ckmatrix(v[0]))
      return gensizeerr(contextptr);
    matrice m;
    mtran(*v[0]._VECTptr,m);
    gen res=_rowSwap(makesequence(m,v[1],v[2]),contextptr);
    if (res.type==_VECT){
      mtran(*res._VECTptr,m);
      res=m;
    }
    return res;
  }
  static const char _SWAPCOL_s[]="SWAPCOL";
  static define_unary_function_eval_quoted (__SWAPCOL,&giac::_SWAPCOL,_SWAPCOL_s);
  define_unary_function_ptr5( at_SWAPCOL ,alias_at_SWAPCOL,&__SWAPCOL,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _DELCOL_s[]="DELCOL";
  static define_unary_function_eval_quoted (__DELCOL,&giac::_delcols,_DELCOL_s);
  define_unary_function_ptr5( at_DELCOL ,alias_at_DELCOL,&__DELCOL,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _DELROW_s[]="DELROW";
  static define_unary_function_eval_quoted (__DELROW,&giac::_delrows,_DELROW_s);
  define_unary_function_ptr5( at_DELROW ,alias_at_DELROW,&__DELROW,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _SWAPROW_s[]="SWAPROW";
  static define_unary_function_eval_quoted (__SWAPROW,&giac::_rowSwap,_SWAPROW_s);
  define_unary_function_ptr5( at_SWAPROW ,alias_at_SWAPROW,&__SWAPROW,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _SUB(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    gen v0=v[0];
    v=*eval(v,eval_level(contextptr),contextptr)._VECTptr;
    if (v.size()!=4 || v0.type!=_IDNT)
      return _mid(v,contextptr);
    v[2]=_floor(v[2],contextptr);
    v[3]=_floor(v[3],contextptr);
    if (ckmatrix(v[1]))
      return sto(_subMat(gen(makevecteur(v[1],v[2],v[3]),_SEQ__VECT),contextptr),v0,contextptr);
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    if (v[1].type==_VECT && v[2].type==_INT_ && v[3].type==_INT_){
      vecteur & w =*v[1]._VECTptr;
      int v2=v[2].val-shift, v3=v[3].val-shift;
      if (v2<=v3 && v2>=0 && v3<int(w.size()))
	return sto(gen(vecteur(w.begin()+v2,w.begin()+v3+1),v[1].subtype),v0,contextptr);
    }
    return undef;
  }
  static const char _SUB_s[]="SUB";
  static define_unary_function_eval_quoted (__SUB,&giac::_SUB,_SUB_s);
  define_unary_function_ptr5( at_SUB ,alias_at_SUB,&__SUB,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _RANDMAT(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    int s=v.size();
    gen name=v[0];
    if (s!=2 || (name.type!=_IDNT && !name.is_symb_of_sommet(at_double_deux_points)))
      return gensizeerr(contextptr);
    v=*eval(v,eval_level(contextptr),contextptr)._VECTptr;
    v[1]=_floor(v[1],contextptr);
    if (v[1].type!=_INT_)
      return gentypeerr(contextptr);
    if (s==2)
      return sto(vranm(v[1].val,0,contextptr),name,contextptr);
    v[2]=_floor(v[2],contextptr);
    if (v[2].type!=_INT_)
      return gentypeerr(contextptr);
    return sto(mranm(v[1].val,v[2].val,0,contextptr),name,contextptr);    
  }
  static const char _RANDMAT_s[]="RANDMAT";
  static define_unary_function_eval_quoted (__RANDMAT,&giac::_RANDMAT,_RANDMAT_s);
  define_unary_function_ptr5( at_RANDMAT ,alias_at_RANDMAT,&__RANDMAT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _REDIM(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    int s=v.size();
    if (s==3)
      return _REDIM(gen(makevecteur(v[0],makevecteur(v[1],v[2])),_SEQ__VECT),contextptr);
    gen name=v[0];
    if (s!=2 || (name.type!=_IDNT && !name.is_symb_of_sommet(at_double_deux_points)))
      return gensizeerr(contextptr);
    v=*eval(v,eval_level(contextptr),contextptr)._VECTptr;
    if (v[0].type!=_VECT)
      return gentypeerr(contextptr);
    vecteur w=*v[0]._VECTptr,argv;
    argv=gen2vecteur(v[1]);
    for (unsigned i=0;i<argv.size();++i){
      if (!is_integral(argv[i]))
	return gentypeerr(contextptr);
    }
    if (ckmatrix(v[0])){
      if (argv.size()==2){
	w.clear();
	int newl=argv[0].val,newc=argv[1].val;
	if (newl<=0 || newc<=0 || newl*newc>LIST_SIZE_LIMIT)
	  return gendimerr(contextptr);
	// create w
	vecteur & v0=*v[0]._VECTptr;
	newl=giacmin(newl,v0.size());
	int nc=giacmin(newc,v0.front()._VECTptr->size()),j;
	for (int i=0;i<newl;++i){
	  vecteur & cur = *v0[i]._VECTptr;
	  for (j=0;j<nc;++j){
	    w.push_back(cur[j]);
	  }
	  for (;j<newc;++j)
	    w.push_back(0);
	}
      }
      else
	aplatir(*v[0]._VECTptr,w);
    }
    argv.push_back(w);
    return sto(_matrix(gen(argv,_SEQ__VECT),contextptr),name,contextptr);
  }
  static const char _REDIM_s[]="REDIM";
  static define_unary_function_eval_quoted (__REDIM,&giac::_REDIM,_REDIM_s);
  define_unary_function_ptr5( at_REDIM ,alias_at_REDIM,&__REDIM,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _REPLACE(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    int s=v.size();
    gen name=v[0];
    if (s!=3 || (name.type!=_IDNT && !name.is_symb_of_sommet(at_double_deux_points)))
      return gensizeerr(contextptr);
    v=*eval(v,eval_level(contextptr),contextptr)._VECTptr;
    if (v[0].type!=_VECT || v[2].type!=_VECT)
      return gentypeerr(contextptr);
    v[1]=_floor(v[1],contextptr);
    int pos,l,c=0,shift=abs_calc_mode(contextptr)==38; //  && v[1].subtype==_LIST__VECT;
    vecteur w0=*v[0]._VECTptr,w2=*v[2]._VECTptr,argv;
    if (ckmatrix(v[0])){
      mdims(w0,l,c);
      if (ckmatrix(v[2]) && v[1].type==_VECT){
	vecteur & v1=*v[1]._VECTptr;
	if (v1.size()!=2 || v1.front().type!=_INT_ || v1.back().type!=_INT_)
	  return gentypeerr(contextptr);
	int ls,cs,pos1=v1.front().val-shift,pos2=v1.back().val-shift;
	if (pos1<0 || pos1>=l || pos2<0 || pos2>=c)
	  return gendimerr(contextptr);
	mdims(w2,ls,cs);
	std_matrix<gen> target,source;
	matrice2std_matrix_gen(w2,source);
	matrice2std_matrix_gen(w0,target);
	for (int i=0;i<ls;++i){
	  if (i+pos1>=l)
	    break;
	  vecteur & ws =source[i];
	  vecteur & wt =target[i+pos1];
	  for (int j=0;j<cs;++j){
	    if (j+pos2>=c)
	      break;
	    wt[j+pos2]=ws[j];
	  }
	}
	std_matrix_gen2matrice(target,w0);
	return sto(gen(w0,_MATRIX__VECT),name,contextptr);
      }
      aplatir(*v[0]._VECTptr,w0);
    }
    else
      l=w0.size();
    if (v[1].type==_VECT){
      vecteur & v1=*v[1]._VECTptr;
      if (v1.size()!=2 || v1.front().type!=_INT_ || v1.back().type!=_INT_)
	return gentypeerr(contextptr);
      pos=(v1.front().val-shift)*c+(v1.back().val-shift);
    }
    else {
      if (v[1].type!=_INT_)
	return gentypeerr(contextptr);
      pos=v[1].val;
      if (abs_calc_mode(contextptr)==38)
	--pos;
    }
    if (ckmatrix(v[2]))
      aplatir(*v[2]._VECTptr,w2);
    int w0s=w0.size(),w2s=w2.size(),i=giacmax(pos,0),j=0;
    for (;i<w0s && j<w2s;++j,++i){
      w0[i]=w2[j];
    }
    if (!c) return sto(gen(w0,v[0].subtype),name,contextptr);
    return sto(_matrix(gen(makevecteur(l,c,w0),_SEQ__VECT),contextptr),name,contextptr);
  }
  static const char _REPLACE_s[]="REPLACE";
  static define_unary_function_eval_quoted (__REPLACE,&giac::_REPLACE,_REPLACE_s);
  define_unary_function_ptr5( at_REPLACE ,alias_at_REPLACE,&__REPLACE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _COLNORM_s[]="COLNORM";
  static define_unary_function_eval (__COLNORM,&giac::_colNorm,_COLNORM_s);
  define_unary_function_ptr5( at_COLNORM ,alias_at_COLNORM,&__COLNORM,0,T_UNARY_OP_38);

  static const char _ROWNORM_s[]="ROWNORM";
  static define_unary_function_eval (__ROWNORM,&giac::_rowNorm,_ROWNORM_s);
  define_unary_function_ptr5( at_ROWNORM ,alias_at_ROWNORM,&__ROWNORM,0,T_UNARY_OP_38);

  static const char _CROSS_s[]="CROSS";
  static define_unary_function_eval (__CROSS,&giac::_cross,_CROSS_s);
  define_unary_function_ptr5( at_CROSS ,alias_at_CROSS,&__CROSS,0,T_UNARY_OP_38);

  static const char _DET_s[]="DET";
  static define_unary_function_eval (__DET,&giac::_det,_DET_s);
  define_unary_function_ptr5( at_DET ,alias_at_DET,&__DET,0,T_UNARY_OP_38);

  static const char _DOT_s[]="DOT";
  static define_unary_function_eval (__DOT,&giac::_dotprod,_DOT_s);
  define_unary_function_ptr5( at_DOT ,alias_at_DOT,&__DOT,0,T_UNARY_OP_38);

  gen _EIGENVAL(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!is_squarematrix(args))
      return gendimerr(contextptr);
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_egvl(evalf(args,1,contextptr),contextptr);
    res=_diag(res,contextptr);
    complex_mode(b,contextptr);
    return res;
  }
  static const char _EIGENVAL_s[]="EIGENVAL";
  static define_unary_function_eval (__EIGENVAL,&giac::_EIGENVAL,_EIGENVAL_s);
  define_unary_function_ptr5( at_EIGENVAL ,alias_at_EIGENVAL,&__EIGENVAL,0,T_UNARY_OP_38);

  gen _EIGENVV(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!is_squarematrix(args))
      return gendimerr(contextptr);
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_jordan(evalf(args,1,contextptr),contextptr);
    complex_mode(b,contextptr);
    if (res.type==_VECT) 
      res.subtype=_LIST__VECT;
    return res;
  }
  static const char _EIGENVV_s[]="EIGENVV";
  static define_unary_function_eval (__EIGENVV,&giac::_EIGENVV,_EIGENVV_s);
  define_unary_function_ptr5( at_EIGENVV ,alias_at_EIGENVV,&__EIGENVV,0,T_UNARY_OP_38);

  static const char _IDENMAT_s[]="IDENMAT";
  static define_unary_function_eval (__IDENMAT,&giac::_idn,_IDENMAT_s);
  define_unary_function_ptr5( at_IDENMAT ,alias_at_IDENMAT,&__IDENMAT,0,T_UNARY_OP_38);

  static const char _INVERSE_s[]="INVERSE";
  static define_unary_function_eval (__INVERSE,&giac::inv,_INVERSE_s);
  define_unary_function_ptr5( at_INVERSE ,alias_at_INVERSE,&__INVERSE,0,T_UNARY_OP_38);

  gen _TRACE(const gen & g,GIAC_CONTEXT){
    if (!is_squarematrix(g))
      return gensizeerr(contextptr);
    return mtrace(*g._VECTptr);
  }
  static const char _TRACE_s[]="TRACE";
  static define_unary_function_eval (__TRACE,&giac::_TRACE,_TRACE_s);
  define_unary_function_ptr5( at_TRACE ,alias_at_TRACE,&__TRACE,0,T_UNARY_OP_38);

  static const char _TRN_s[]="TRN";
  static define_unary_function_eval (__TRN,&giac::_trn,_TRN_s);
  define_unary_function_ptr5( at_TRN ,alias_at_TRN,&__TRN,0,T_UNARY_OP_38);

  static const char _RANK_s[]="RANK";
  static define_unary_function_eval (__RANK,&giac::_rank,_RANK_s);
  define_unary_function_ptr5( at_RANK ,alias_at_RANK,&__RANK,0,T_UNARY_OP_38);

  gen _SIZE(const gen& args,GIAC_CONTEXT){
    if (ckmatrix(args))
      return _dim(args,contextptr);
    else
      return _size(args,contextptr);
  }
  static const char _SIZE_s[]="SIZE";
  static define_unary_function_eval (__SIZE,&giac::_SIZE,_SIZE_s);
  define_unary_function_ptr5( at_SIZE ,alias_at_SIZE,&__SIZE,0,T_UNARY_OP_38);

  static const char _SORT_s[]="SORT";
  static define_unary_function_eval (__SORT,&giac::_sort,_SORT_s);
  define_unary_function_ptr5( at_SORT ,alias_at_SORT,&__SORT,0,T_UNARY_OP_38);

  static const char _DELTALIST_s[]="ΔLIST";
  static define_unary_function_eval (__DELTALIST,&giac::_deltalist,_DELTALIST_s);
  define_unary_function_ptr5( at_DELTALIST ,alias_at_DELTALIST,&__DELTALIST,0,T_UNARY_OP_38);

  static const char _CONCAT_s[]="CONCAT";
  static define_unary_function_eval (__CCONCAT,&giac::_concat,_CONCAT_s);
  define_unary_function_ptr5( at_CONCAT ,alias_at_CONCAT,&__CCONCAT,0,T_UNARY_OP_38);

  static const char _PILIST_s[]="ΠLIST";
  static define_unary_function_eval (__PILIST,&giac::_product,_PILIST_s);
  define_unary_function_ptr5( at_PILIST ,alias_at_PILIST,&__PILIST,0,T_UNARY_OP_38);

  static const char _SIGMALIST_s[]="ΣLIST";
  static define_unary_function_eval (__SIGMALIST,&giac::_sum,_SIGMALIST_s);
  define_unary_function_ptr5( at_SIGMALIST ,alias_at_SIGMALIST,&__SIGMALIST,0,T_UNARY_OP_38);

  static const char _REVERSE_s[]="REVERSE";
  static define_unary_function_eval (__REVERSE,&giac::_revlist,_REVERSE_s);
  define_unary_function_ptr5( at_REVERSE ,alias_at_REVERSE,&__REVERSE,0,T_UNARY_OP_38);

  gen _POS(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->front().type!=_VECT) )
      return gensizeerr(contextptr);
    return equalposcomp(*args._VECTptr->front()._VECTptr,evalf2bcd(args._VECTptr->back(),1,contextptr));    
  }
  static const char _POS_s[]="POS";
  static define_unary_function_eval (__POS,&giac::_POS,_POS_s);
  define_unary_function_ptr5( at_POS ,alias_at_POS,&__POS,0,T_UNARY_OP_38);

  gen _MAKELIST(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || (args._VECTptr->size()!=4 && args._VECTptr->size()!=5))
      return gensizeerr(contextptr);
    vecteur & v = *args._VECTptr;
    if (v.size()==5 && is_positive(-v[4]*(v[3]-v[2]),contextptr))
      return gensizeerr(gettext("Invalid step value"));
    gen res=giac::_seq(args,contextptr);
    if (res.type==_VECT)
      res.subtype=_LIST__VECT;
    return res;
  }
  static const char _MAKELIST_s[]="MAKELIST";
  static define_unary_function_eval (__MAKELIST,&giac::_MAKELIST,_MAKELIST_s);
  define_unary_function_ptr5( at_MAKELIST ,alias_at_MAKELIST,&__MAKELIST,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _INT(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_positive(g,contextptr))
      return _floor(g,contextptr);
    else
      return _ceil(g,contextptr);
  }
  static const char _INT_s[]="INT";
  static define_unary_function_eval (__INT,&giac::_INT,_INT_s);
  define_unary_function_ptr5( at_INT ,alias_at_INT,&__INT,0,T_UNARY_OP_38);

  static int taylorxn=0;
  static void hp38_eval(vecteur & v,gen & x,gen& newx,GIAC_CONTEXT){
    x=v[1];
    if (x.is_symb_of_sommet(at_equal))
      x=x._SYMBptr->feuille[0];
    identificateur idx("taylorx"+print_INT_(taylorxn));
    ++taylorxn;
    newx=idx;
    v[0]=eval(subst(v[0],x,newx,false,contextptr),eval_level(contextptr),contextptr);
    v[1]=newx;
    int s=v.size();
    for (int i=2;i<s;++i)
      v[i]=eval(v[i],eval_level(contextptr),contextptr);
  }

  gen _HPDIFF(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen tmp;
    if (args.type==_VECT && args.subtype==_SEQ__VECT && args._VECTptr->size()>=2){
      gen value=(*args._VECTptr)[1],var=value;
      if (value.is_symb_of_sommet(at_equal)){
	var=value._SYMBptr->feuille[0];
	value=value._SYMBptr->feuille[1];
      }
      if (var.type!=_IDNT && abs_calc_mode(contextptr)==38)
	return gensizeerr(gettext("∂(expression,variable=value)"));
      int ndiff=1;
      if (args._VECTptr->size()>=3 && (*args._VECTptr)[2].type==_INT_ ){
	ndiff=(*args._VECTptr)[2].val;
	if (ndiff<0)
	  return gensizeerr(gettext("Order of derivation must be positive"));
      }
      if (args._VECTptr->size()>=4 && abs_calc_mode(contextptr)!=38)
	value=(*args._VECTptr)[3];
      identificateur idx("hpdiffx");
      gen newx(idx);
      gen arg0=eval(subst(args._VECTptr->front(),var,newx,false,contextptr),eval_level(contextptr),contextptr);
      if (ndiff==0){
	value=eval(value,1,contextptr);
	tmp=gen(makevecteur(arg0,newx,value),_SEQ__VECT);
	return _limit(tmp,contextptr);
      }
      else {
	tmp=gen(makevecteur(arg0,newx,ndiff),_SEQ__VECT);
	tmp=_derive(tmp,contextptr);
	tmp=subst(tmp,newx,value,false,contextptr);
	return eval(tmp,1,contextptr);
      }
    }
    else {
      gen a(args);
      if (guess_program(a,contextptr))
	return _derive(a,contextptr);
      return _HPDIFF(gen(makevecteur(args,vx_var),_SEQ__VECT),contextptr);
    }
  }
  static const char _HPDIFF_s[]="∂";
  static define_unary_function_eval_quoted (__HPDIFF,&giac::_HPDIFF,_HPDIFF_s);
  define_unary_function_ptr5( at_HPDIFF ,alias_at_HPDIFF,&__HPDIFF,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _HPINT(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype==_SEQ__VECT){
      vecteur v = *args._VECTptr;
      if (v.size()==3){
	if (v.front().is_symb_of_sommet(at_equal)){
	  gen var=v.front()._SYMBptr->feuille[0];
	  gen lower=v.front()._SYMBptr->feuille[1];
	  v=makevecteur(v[2],var,lower,v[1]);
	}
	if (v[1].is_symb_of_sommet(at_equal)){
	  gen var=v[1]._SYMBptr->feuille[0];
	  gen lower=v[1]._SYMBptr->feuille[1];
	  v=makevecteur(v.front(),var,lower,v[2]);
	}
	if (v.size()==3)
	  return _integrate(args,contextptr);
      }
      if (v.size()>=4){
	if (v[1].type!=_IDNT){
	  return gensizeerr(gettext("∫(expression,var,lower,upper)"));
	  swapgen(v[0],v[2]);
	  swapgen(v[1],v[3]);
	}
	gen x,newx;
	hp38_eval(v,x,newx,contextptr);
	gen tmp=_integrate(gen(v,_SEQ__VECT),contextptr);
	return subst(tmp,newx,x,false,contextptr);
      }
    }
#ifndef CAS38_DISABLED
    return _integrate(args,contextptr);
#else
    return gensizeerr(contextptr);
#endif
  }
  static const char _HPINT_s[]="∫";
  static define_unary_function_eval_quoted (__HPINT,&giac::_HPINT,_HPINT_s);
  define_unary_function_ptr5( at_HPINT ,alias_at_HPINT,&__HPINT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _HPSUM(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype==_SEQ__VECT && args._VECTptr->size()<=3){
      if (args._VECTptr->size()!=3)
	return gensizeerr(contextptr);
      const vecteur & v = *args._VECTptr;
      if (v[0].is_symb_of_sommet(at_equal)){
	gen var=v[0]._SYMBptr->feuille[0];
	if (var.type!=_IDNT)
	  return gensizeerr(contextptr);
	gen inf=v[0]._SYMBptr->feuille[1];
	gen sup=v[1];
	gen expr=v[2];
	return _sum(gen(makevecteur(expr,var,inf,sup),_SEQ__VECT),contextptr);
      }
      if (v[1].is_symb_of_sommet(at_equal)){
	gen var=v[1]._SYMBptr->feuille[0];
	if (var.type!=_IDNT)
	  return gensizeerr(contextptr);
	gen inf=v[1]._SYMBptr->feuille[1];
	gen sup=v[2];
	gen expr=v[0];
	return _sum(gen(makevecteur(expr,var,inf,sup),_SEQ__VECT),contextptr);
      }
    }
    return _sum(args,contextptr);
  }
  static const char _HPSUM_s[]="Σ";
  static define_unary_function_eval_quoted (__HPSUM,&giac::_HPSUM,_HPSUM_s);
  define_unary_function_ptr5( at_HPSUM ,alias_at_HPSUM,&__HPSUM,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _TAYLOR(const gen & args,GIAC_CONTEXT){
#ifdef CAS38_DISABLED
    return gensizeerr(contextptr);
#endif
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    if (v.size()<2)
      v.push_back(x__IDNT_e);
    gen x,newx;
    hp38_eval(v,x,newx,contextptr);
    gen res=subst(_taylor(gen(v,_SEQ__VECT),contextptr),newx,x,false,contextptr);
    v=lop(res,at_order_size);
    res=subst(res,v,vecteur(v.size()),false,contextptr);
    return res;
  }
  static const char _TAYLOR_s[]="TAYLOR";
  static define_unary_function_eval_quoted (__TAYLOR,&_TAYLOR,_TAYLOR_s);
  define_unary_function_ptr5( at_TAYLOR ,alias_at_TAYLOR,&__TAYLOR,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _POLYCOEFF(const gen &args0,GIAC_CONTEXT){
    gen args=eval(args0,1,contextptr);
    if (args.type==_VECT && args.subtype!=_SEQ__VECT)
      return _pcoeff(args,contextptr);
    return _symb2poly(args0,contextptr);
  }
  static const char _POLYCOEF_s[]="POLYCOEF";
  static define_unary_function_eval (__POLYCOEF,&giac::_POLYCOEFF,_POLYCOEF_s);
  define_unary_function_ptr5( at_POLYCOEF ,alias_at_POLYCOEF,&__POLYCOEF,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _horner(const gen & args,GIAC_CONTEXT);
  gen _POLYEVAL(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    if (args.subtype!=_SEQ__VECT)
      return _POLYFORM(_horner(gen(makevecteur(args,vx_var),_SEQ__VECT),contextptr),contextptr);
    return _horner(args,contextptr);
  }
  static const char _POLYEVAL_s[]="POLYEVAL";
  static define_unary_function_eval (__POLYEVAL,&giac::_POLYEVAL,_POLYEVAL_s);
  define_unary_function_ptr5( at_POLYEVAL ,alias_at_POLYEVAL,&__POLYEVAL,0,T_UNARY_OP_38);

  gen evalfunc(const gen & args,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    vecteur v(lidnt(args));
    vecteur lf,lfval;
    for (unsigned i=0;i<v.size();++i){
      if (v[i].type!=_IDNT)
	continue;
      const char * ch=v[i]._IDNTptr->id_name;
      if (strlen(ch)==2 && (ch[0]=='F' || ch[0]=='X' || ch[0]=='Y' || ch[0]=='R')){
	lf.push_back(v[i]);
	lfval.push_back(eval(v[i],1,contextptr));
      }
    }
    if (lf.empty())
      return args;
    return subst(args,lf,lfval,false,contextptr);
#else
    return args;
#endif
  }
  static string printasPOLYFORM(const gen &feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type==_VECT && feuille._VECTptr->size()==2 && feuille._VECTptr->back().type==_FUNC){
      const char * s=feuille._VECTptr->back()._FUNCptr->ptr()->s;
      return feuille._VECTptr->front().print(contextptr)+"\xe2\x96\xba"+((strcmp(s,"eval")==0)?"\xe2\x96\xba":s);
    }
    return sommetstr+('('+feuille.print(contextptr)+')');
  }
  gen _POLYFORM(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return args;
    gen tmp;
    if (args.type==_VECT && !args._VECTptr->empty()){
      const vecteur & v = *args._VECTptr;
      tmp=v[0];
      tmp=eval(tmp,1,context0);
      tmp=evalfunc(tmp,contextptr);
      int vs=v.size();
#ifdef CAS38_DISABLED
      if (vs==2 && v.back()==at_prod && is_integral(tmp))
	return _ifactor(tmp,context0);
#else
      if (vs==2){
	if (v.back()==at_eval) return tmp;
	if (v.back()==at_prod){
	  if (is_integral(tmp))
	    return _ifactor(tmp,context0);
	  return _factor(tmp,context0);
	}
	if (v.back()==at_plus)
	  return _partfrac(tmp,context0); 
	if (v.back().type==_FUNC)
	  return _convert(gen(makevecteur(tmp,v.back()),_SEQ__VECT),context0);
      }
#endif
      if (v.back().type==_FUNC) // additional check for STO> +/-/etc. if CAS38_DISABLED is set
	return gensizeerr(contextptr);
      if (vs>1)
	return _reorder(makesequence(tmp,vecteur(v.begin()+1,v.end())),context0);
    }
    tmp=eval(args,1,context0);
    tmp=evalfunc(tmp,contextptr);
    return _recursive_normal(tmp,context0); // return symb_quote(_partfrac(args,contextptr)); // return symb_quote(recursive_normal(args,1,contextptr)); // symb_quote(simplify(eval(args,1,context0),context0));
  }
  static const char _POLYFORM_s[]="POLYFORM";
  static define_unary_function_eval2_quoted (__POLYFORM,&giac::_POLYFORM,_POLYFORM_s,&printasPOLYFORM);
  define_unary_function_ptr5( at_POLYFORM ,alias_at_POLYFORM,&__POLYFORM,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _POLYROOT(const gen & args0,GIAC_CONTEXT){
    gen args=eval(args0,1,contextptr);
    if (args.type==_VECT && args.subtype!=_SEQ__VECT)
      ;
    else
      args=args0;
    gen res;
    if (complex_mode(contextptr))
      res=_proot(args,contextptr);
    else {
#if 0 // set to 1 : POLYROOT returns only real roots without multiplicities, SLOW on aspen
      vecteur vas_res;
      if (vas(symb2poly_num(args,contextptr),0,0,1e-14,vas_res,false,contextptr))
	res=vas_res;
      else
	res=_proot(args,contextptr);
#else
      res=_proot(args,contextptr);
#endif
    }
#ifdef GIAC_HAS_STO_38
    if (res.type==_VECT)
      res.subtype=_LIST__VECT;
#endif
    return res;
  }
  static const char _POLYROOT_s[]="POLYROOT";
  static define_unary_function_eval (__POLYROOT,&giac::_POLYROOT,_POLYROOT_s);
  define_unary_function_ptr5( at_POLYROOT ,alias_at_POLYROOT,&__POLYROOT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _ISOLATE(const gen & args0,GIAC_CONTEXT){
#ifdef CAS38_DISABLED
    return gensizeerr(contextptr);
#endif
    return symb_quote(_solve(args0,contextptr));
  }
  static const char _ISOLATE_s[]="ISOLATE";
  static define_unary_function_eval_quoted (__ISOLATE,&giac::_ISOLATE,_ISOLATE_s);
  define_unary_function_ptr5( at_ISOLATE ,alias_at_ISOLATE,&__ISOLATE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _QUAD_s[]="QUAD";
  static define_unary_function_eval_quoted (__QUAD,&giac::_ISOLATE,_QUAD_s);
  define_unary_function_ptr5( at_QUAD ,alias_at_QUAD,&__QUAD,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _IS_LINEAR(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2){
      vecteur & v = *args._VECTptr;
      gen a,b;
      if (v[1].type==_IDNT && is_linear_wrt(v[0],v[1],a,b,contextptr))
	return makevecteur(eval(a,eval_level(contextptr),contextptr),eval(b, eval_level(contextptr),contextptr));
      else
	return 0;
    }
    return gentypeerr(contextptr);
    return 0;
  }
  static const char _IS_LINEAR_s[]="LINEAR?";
  static define_unary_function_eval_quoted (__IS_LINEAR,&giac::_IS_LINEAR,_IS_LINEAR_s);
  define_unary_function_ptr5( at_IS_LINEAR ,alias_at_IS_LINEAR,&__IS_LINEAR,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  static const char _FNROOT_s[]="FNROOT";
  static define_unary_function_eval (__FNROOT,&giac::_fsolve,_FNROOT_s);
  define_unary_function_ptr5( at_FNROOT ,alias_at_FNROOT,&__FNROOT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  gen _SVD(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    if (!is_squarematrix(args0))
      return gentypeerr(contextptr);
    gen args=evalf(args0,1,contextptr);
    gen res= _svd(gen(makevecteur(args,-1),_SEQ__VECT),contextptr);
    if (res.type==_VECT) res.subtype=_LIST__VECT;
    return res;    
  }
  static const char _SVD_s[]="SVD";
  static define_unary_function_eval (__SVD,&giac::_SVD,_SVD_s); // FIXME
  define_unary_function_ptr5( at_SVD ,alias_at_SVD,&__SVD,0,T_UNARY_OP_38);

  gen _SVL(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    if (!is_squarematrix(args0))
      return gentypeerr(contextptr);
    gen args=evalf(args0,1,contextptr);
    return _svd(gen(makevecteur(args,-2),_SEQ__VECT),contextptr);
  }
  static const char _SVL_s[]="SVL";
  static define_unary_function_eval (__SVL,&giac::_SVL,_SVL_s); // FIXME
  define_unary_function_ptr5( at_SVL ,alias_at_SVL,&__SVL,0,T_UNARY_OP_38);

  gen _SPECRAD(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    gen args=evalf(args0,1,contextptr);
    if (!is_squarematrix(args))
      return gentypeerr(contextptr);
    vecteur v=megvl(*args._VECTptr,contextptr);
    if (is_undef(v)) return v;
    gen res,tmp;
    int s=v.size();
    for (int i=0;i<s;++i){
      tmp=abs(v[i],contextptr);
      if (ck_is_strictly_greater(tmp,res,contextptr))
	res=tmp;
    }
    return res;
  }
  static const char _SPECRAD_s[]="SPECRAD";
  static define_unary_function_eval (__SPECRAD,&giac::_SPECRAD,_SPECRAD_s); // FIXME
  define_unary_function_ptr5( at_SPECRAD ,alias_at_SPECRAD,&__SPECRAD,0,T_UNARY_OP_38);

  gen _SPECNORM(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    gen args=evalf(args0,1,contextptr);
    if (!ckmatrix(args)){
      if (args.type==_VECT)
	return _l2norm(args,contextptr);
      return gentypeerr(contextptr);
    }
    return _max(_SVL(*args._VECTptr,contextptr),contextptr);
  }
  static const char _SPECNORM_s[]="SPECNORM";
  static define_unary_function_eval (__SPECNORM,&giac::_SPECNORM,_SPECNORM_s); // FIXME
  define_unary_function_ptr5( at_SPECNORM ,alias_at_SPECNORM,&__SPECNORM,0,T_UNARY_OP_38);

  gen _COND(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    // COND(matrix,2) L2norm condition number
    // otherwise COLNORM(args0)*COLNORM(inv(args0))
    if (args0.type==_VECT && args0._VECTptr->size()==2 && args0._VECTptr->back()==2){
      gen args=args0._VECTptr->front();
      if (!ckmatrix(args))
	return gentypeerr(contextptr);
      gen g=_SVL(args,contextptr);
      if (is_undef(g)) return g;
      if (g.type!=_VECT)
	return undef;
      vecteur & v =*g._VECTptr;
      int s=v.size();
      gen mina(plus_inf),maxa(0);
      for (int i=0;i<s;++i){
	gen tmp=abs(v[i],contextptr);
	if (ck_is_strictly_greater(mina,tmp,contextptr))
	  mina=tmp;
	if (ck_is_strictly_greater(tmp,maxa,contextptr))
	  maxa=tmp;
      }
      return maxa/mina;
    }
    else {
      gen args=evalf(args0,1,contextptr);
      if (!is_squarematrix(args))
	return gensizeerr(contextptr);
      gen invargs=inv(args,contextptr);
      return _colNorm(args,contextptr)*_colNorm(invargs,contextptr);
      // return _colNorm(args,contextptr)*_rowNorm(args,contextptr)/abs(_det(args,contextptr),contextptr);
    }
  }
  static const char _COND_s[]="COND";
  static define_unary_function_eval (__COND,&giac::_COND,_COND_s); // FIXME
  define_unary_function_ptr5( at_COND ,alias_at_COND,&__COND,0,T_UNARY_OP_38);

  gen _SCHUR(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen res;
    if (ckmatrix(args)){
      if (!is_squarematrix(args))
	return gendimerr(contextptr);
#ifdef HAVE_LIBMPFR
      res= _hessenberg(gen(makevecteur(args,epsilon(contextptr)),_SEQ__VECT),contextptr); 
#else
      res= _hessenberg(gen(makevecteur(args,1e-12),_SEQ__VECT),contextptr); 
#endif
    }
    else
      res= _hessenberg(args,contextptr);
    if (res.type==_VECT) res.subtype=_LIST__VECT;
    return res;
  }
  static const char _SCHUR_s[]="SCHUR";
  static define_unary_function_eval (__SCHUR,&giac::_SCHUR,_SCHUR_s); // FIXME
  define_unary_function_ptr5( at_SCHUR ,alias_at_SCHUR,&__SCHUR,0,T_UNARY_OP_38);

  gen _LQ(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    gen args=evalf(args0,1,contextptr);
    if (!ckmatrix(args))
      return gentypeerr(contextptr);
    gen res=qr(makevecteur(_trn(args,contextptr),-1),contextptr);
    if (is_undef(res) || res.type!=_VECT || res._VECTptr->size()<3)
      return gensizeerr(contextptr);
    vecteur v(*res._VECTptr);
    v[0]=_trn(v[0],contextptr);
    v[1]=_trn(v[1],contextptr);
    swapgen(v[0],v[1]);
    // v[2]=midn(v[0]._VECTptr->size());
    return gen(v,_LIST__VECT);
  }
  static const char _LQ_s[]="LQ";
  static define_unary_function_eval (__LQ,&giac::_LQ,_LQ_s); 
  define_unary_function_ptr5( at_LQ ,alias_at_LQ,&__LQ,0,T_UNARY_OP_38);

  static const char _RREF_s[]="RREF";
  static define_unary_function_eval (__RREF,&giac::_rref,_RREF_s); // FIXME
  define_unary_function_ptr5( at_RREF ,alias_at_RREF,&__RREF,0,T_UNARY_OP_38);

  gen _XPON(const gen & g0,GIAC_CONTEXT){
    if (g0.type==_STRNG && g0.subtype==-1) return g0;
    if (is_equal(g0))
      return apply_to_equal(g0,_XPON,contextptr);
    if (g0.type==_VECT)
      return apply(g0,_XPON,contextptr);
#if 0 // def BCD
    gen g=evalf2bcd(g0,1,contextptr);
#else
    gen g=evalf_double(g0,1,contextptr);
#endif
    if (is_zero(g))
      return undef;
    gen gf=_floor(log10(abs(g,contextptr),contextptr),contextptr); 
    if (gf.type!=_INT_ && gf.type!=_FLOAT_)
      return gensizeerr(contextptr);
    return gf;
  }
  static const char _XPON_s[]="XPON";
  static define_unary_function_eval (__XPON,&giac::_XPON,_XPON_s); // FIXME
  define_unary_function_ptr5( at_XPON ,alias_at_XPON,&__XPON,0,T_UNARY_OP_38);

  gen _MANT(const gen & g0,GIAC_CONTEXT){
    if (g0.type==_STRNG && g0.subtype==-1) return g0;
    if (is_equal(g0))
      return apply_to_equal(g0,_MANT,contextptr);
    if (g0.type==_VECT)
      return apply(g0,_MANT,contextptr);
#if 0 // def BCD
    gen g=evalf2bcd(g0,1,contextptr);
#else
    gen g=evalf_double(g0,1,contextptr);
#endif
    if (is_zero(g))
      return g;
    gen gabs=abs(g,contextptr);
    gen gf=_floor(log10(gabs,contextptr),contextptr); 
    if (abs_calc_mode(contextptr)!=38 && gf.type!=_INT_)
      return gensizeerr(contextptr);
    // FIXME number of digits
    return evalf(gabs*alog10(-gf,contextptr),1,contextptr);
  }
  static const char _MANT_s[]="MANT";
  static define_unary_function_eval (__MANT,&giac::_MANT,_MANT_s); 
  define_unary_function_ptr5( at_MANT ,alias_at_MANT,&__MANT,0,T_UNARY_OP_38);

  gen _HMSX(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    if (g0.type==_VECT)
      return apply(g0,_HMSX,contextptr);
    gen g(evalf(g0,1,contextptr));
    if (g.type==_DOUBLE_)
      g = g+1e-12;
    if (g.type==_FLOAT_)
      g = g+plus_one/20000;
    if (g.type!=_DOUBLE_ && g.type!=_FLOAT_)
      return gentypeerr(contextptr);
    gen h=_floor(g,contextptr);
    gen m=_floor(100*(g-h),contextptr);
    gen s=_floor(100*(100*(g-h)-m),contextptr);
    return h+m/giac_float(60.)+s/giac_float(3600.);
  }
  static const char _HMSX_s[]="HMSX";
  static define_unary_function_eval (__HMSX,&giac::_HMSX,_HMSX_s); 
  define_unary_function_ptr5( at_HMSX ,alias_at_HMSX,&__HMSX,0,T_UNARY_OP_38);

  gen _XHMS(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    if (g0.type==_VECT)
      return apply(g0,_XHMS,contextptr);
    gen g(evalf(g0,1,contextptr));
    if (g.type==_DOUBLE_)
      g = g+1e-12;
    if (g.type==_FLOAT_)
      g=g+plus_one/7200;
    if (g.type!=_DOUBLE_ && g.type!=_FLOAT_)
      return gentypeerr(contextptr);
    gen h=_floor(g,contextptr);
    gen m=_floor(60*(g-h),contextptr);
    gen s=_floor(60*(60*(g-h)-m),contextptr);
    return h+m/giac_float(100.)+s/giac_float(10000.);
  }
  static const char _XHMS_s[]="XHMS";
  static define_unary_function_eval (__XHMS,&giac::_XHMS,_XHMS_s); 
  define_unary_function_ptr5( at_XHMS ,alias_at_XHMS,&__XHMS,0,T_UNARY_OP_38);

  gen _DEGXRAD(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_DEGXRAD,contextptr);
    return deg2rad_d*g;
  }
  static const char _DEGXRAD_s[]="DEGXRAD";
  static define_unary_function_eval (__DEGXRAD,&giac::_DEGXRAD,_DEGXRAD_s); 
  define_unary_function_ptr5( at_DEGXRAD ,alias_at_DEGXRAD,&__DEGXRAD,0,T_UNARY_OP_38);

  gen _RADXDEG(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_RADXDEG,contextptr);
    return rad2deg_d*g;
  }
  static const char _RADXDEG_s[]="RADXDEG";
  static define_unary_function_eval (__RADXDEG,&giac::_RADXDEG,_RADXDEG_s); 
  define_unary_function_ptr5( at_RADXDEG ,alias_at_RADXDEG,&__RADXDEG,0,T_UNARY_OP_38);

  gen _PERCENT(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return g._VECTptr->front()*g._VECTptr->back()/giac_float(100.);
  }
  static const char _PERCENT_s[]="%"; // FIXE
  static define_unary_function_eval (__PERCENT,&giac::_PERCENT,_PERCENT_s); 
  define_unary_function_ptr5( at_PERCENT ,alias_at_PERCENT,&__PERCENT,0,T_UNARY_OP_38);

  gen _PERCENTCHANGE(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return giac_float(100.)*(g._VECTptr->back()-g._VECTptr->front())/g._VECTptr->front();
  }
  static const char _PERCENTCHANGE_s[]="%CHANGE"; 
  static define_unary_function_eval (__PERCENTCHANGE,&giac::_PERCENTCHANGE,_PERCENTCHANGE_s); 
  define_unary_function_ptr5( at_PERCENTCHANGE ,alias_at_PERCENTCHANGE,&__PERCENTCHANGE,0,T_UNARY_OP_38);

  gen _PERCENTTOTAL(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return giac_float(100.)*g._VECTptr->back()/g._VECTptr->front();
  }
  static const char _PERCENTTOTAL_s[]="%TOTAL";
  static define_unary_function_eval (__PERCENTTOTAL,&giac::_PERCENTTOTAL,_PERCENTTOTAL_s); 
  define_unary_function_ptr5( at_PERCENTTOTAL ,alias_at_PERCENTTOTAL,&__PERCENTTOTAL,0,T_UNARY_OP_38);

  gen _DISP(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen a=g._VECTptr->front();
    if (a.type!=_INT_ || a.val <0 || a.val>10)
      setdimerr(contextptr);
    gen b=g._VECTptr->back();
    return _legende(gen(makevecteur(makevecteur(0,a*gen(12)),b),_SEQ__VECT),contextptr);
  }
  static const char _DISP_s []="DISP";
  static define_unary_function_eval_quoted (__DISP,&giac::_DISP,_DISP_s);
  define_unary_function_ptr5( at_DISP ,alias_at_DISP,&__DISP,0,T_UNARY_OP_38);

  static const char _WAIT_s []="WAIT";
  static define_unary_function_eval (__WAIT,&giac::_Pause,_WAIT_s);
  define_unary_function_ptr5( at_WAIT ,alias_at_WAIT,&__WAIT,0,T_UNARY_OP_38);

#ifdef GIAC_HAS_STO_38
  gen aspen_input(const vecteur & v,GIAC_CONTEXT);
  gen aspen_msgbox(const vecteur & v,GIAC_CONTEXT);
#endif

  // INPUT(A) or INPUT(A,"title") or INPUT(A,"title","label") or INPUT(A,"title","label","help")
  // or INPUT(A,"title","label","help",default_value)
  gen _INPUT(const gen & args,GIAC_CONTEXT){
    vecteur v=gen2vecteur(args);
    int s=v.size();
    if (s==1){
      v.push_back(string2gen("Input"));
      ++s;
    }
    if (s==2){
      v.push_back(string2gen(v[0].print(contextptr),false));
      ++s;
    }
    if (s==3){
      v.push_back(string2gen("Enter value for "+v[0].print(contextptr),false));
      ++s;
    }
    // check types
    if (v[0].is_symb_of_sommet(at_double_deux_points) && v[0]._SYMBptr->feuille.type==_VECT && v[0]._SYMBptr->feuille._VECTptr->size()==2)
      v[0]=v[0]._SYMBptr->feuille._VECTptr->back();
    for (int i=1; i<=3; i++) { if (v[i].type!=_STRNG) v[i]= eval(v[i], 1, contextptr); if (v[i].type!=_STRNG) v[i]=  string2gen(v[i].print(contextptr)); }
    if (v[0].type!=_IDNT || s>5)
      return gentypeerr(contextptr);
    // set default value in v[0]
    if (s==5){
      gen tmpsto=sto(v[4],v[0],contextptr);
      if (is_undef(tmpsto)) return tmpsto;
      v.pop_back();
      s=4;
    }
#ifdef GIAC_HAS_STO_38
    return aspen_input(v,contextptr);
#else
    // now make a dialog
    v[1]=symbolic(at_Title,v[1]);
    v[2]=symbolic(at_Request,makesequence(v[2],v[0]));
    v[3]=symbolic(at_Text,v[3]);
    v.erase(v.begin());
    return _Dialog(gen(v,_SEQ__VECT),contextptr);
#endif
  }
  static const char _INPUT_s []="INPUT";
  static define_unary_function_eval_quoted (__INPUT,giac::_INPUT,_INPUT_s);
  define_unary_function_ptr5( at_INPUT ,alias_at_INPUT,&__INPUT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

#ifdef GIAC_HAS_STO_38
  // MSGBOX(txt) or MSGBOX(txt, true/false) to have OK/Cancel or just OK menus
  gen _MSGBOX(const gen & args,GIAC_CONTEXT){
    vecteur v=gen2vecteur(args);
    int s=v.size();
    if (s==1) { v.push_back(gen(0)); ++s; }
    return aspen_msgbox(v,contextptr);
  }
#else
  gen _MSGBOX(const gen & args,GIAC_CONTEXT){
    return _output(args,contextptr);
  }
#endif
  static const char _MSGBOX_s []="MSGBOX";
  static define_unary_function_eval (__MSGBOX,giac::_MSGBOX,_MSGBOX_s);
  define_unary_function_ptr5( at_MSGBOX ,alias_at_MSGBOX,&__MSGBOX,0,T_UNARY_OP_38);

  static const char _GETKEY_s[]="GETKEY";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval(__GETKEY,&at_GETKEYAspen,_GETKEY_s);
  // unary_function_eval __GETKEY(0,&at_GETKEYAspen,_GETKEY_s);
#else
  unary_function_eval __GETKEY(0,&_getKey,_GETKEY_s);
#endif
  define_unary_function_ptr5( at_GETKEY ,alias_at_GETKEY,&__GETKEY,0,T_UNARY_OP_38);

  gen _ITERATE(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=4)
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    gen v0=v[0];
    gen v1=v[1];
    v[2]=eval(v[2],eval_level(contextptr),contextptr);
    v[3]=eval(v[3],eval_level(contextptr),contextptr);
    if (v[3].type!=_INT_)
      return gentypeerr(contextptr);
    int n=v[3].val;
    bool all=n<0;
    if (all){
      n=-n;
      if (n>LIST_SIZE_LIMIT)
	return gendimerr(contextptr);
    }
    gen value=v[2];
    vecteur res;
    if (all)
      res=vecteur(n+1,value);
    for (int i=0;!ctrl_c && i<n;++i){
      value=evalf(subst(v0,v1,value,false,contextptr),eval_level(contextptr),contextptr);
      if (is_undef(value))
	return value;
      if (all)
	res[i+1]=value;
    }
    return all?res:value;
  }
  static const char _ITERATE_s[]="ITERATE";
  static define_unary_function_eval_quoted (__ITERATE,&_ITERATE,_ITERATE_s);
  define_unary_function_ptr5( at_ITERATE ,alias_at_ITERATE,&__ITERATE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

#ifndef GIAC_HAS_STO_38
  gen _RECURSE(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    gen v0=v[0];
    gen v1=v[1];
    int s=v.size();
    for (int i=2;i<s;++i)
      v[i]=eval(v[i],eval_level(contextptr),contextptr);
    if (v[0].type!=_IDNT)
      return _rsolve(gen(v,args.subtype),contextptr);
    // HP38 syntax, define recurrent sequence
    // idnt, expression, first term, second term
    // e.g. RECURSE(U,U(N-1)*N,1,2) STO> U1(N)
    return gentypeerr(contextptr);
    return undef;
  }  
  static const char _RECURSE_s[]="RECURSE";
  static define_unary_function_eval_quoted (__RECURSE,&_RECURSE,_RECURSE_s);
  define_unary_function_ptr5( at_RECURSE ,alias_at_RECURSE,&__RECURSE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);
#endif

  gen _MAKEMAT(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=3)
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    gen v0=v[0];
    gen v1=eval(v[1],eval_level(contextptr),contextptr);
    gen v2=eval(v[2],eval_level(contextptr),contextptr);
    is_integral(v1);
    is_integral(v2);
    if (v1.type!=_INT_ || v2.type!=_INT_ || v1.val<1 || v2.val<1)
      return gensizeerr(contextptr);
    int l=giacmax(v1.val,1),c=giacmax(v2.val,1);
    if (longlong(l)*c>LIST_SIZE_LIMIT)
      return gendimerr(contextptr);
    identificateur idI("I"),idJ("J");
    vecteur IJ=makevecteur(idI,idJ);
    vecteur IJval(2),res;
    for (int i=1;i<=l;++i){
      vecteur ligne(c);
      IJval[0]=i;
      for (int j=1;j<=c;++j){
	IJval[1]=j;
	ligne[j-1]=eval(subst(v0,IJ,IJval,false,contextptr),eval_level(contextptr),contextptr);
      }
      res.push_back(ligne);
    }
    return res;
  }
  static const char _MAKEMAT_s[]="MAKEMAT";
  static define_unary_function_eval_quoted (__MAKEMAT,&_MAKEMAT,_MAKEMAT_s);
  define_unary_function_ptr5( at_MAKEMAT ,alias_at_MAKEMAT,&__MAKEMAT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

  // For over-determined system, find the solution such that || A*X-B || (L2) is min
  // for under-determined system, find a solution X of A*X=B such that ||X|| is min
  gen _LSQ(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    vecteur & v = *args._VECTptr;
    gen v0=v[0]; // evalf(v[0],1,contextptr)
    gen v1=v[1];
    if (!ckmatrix(v0) || v1.type!=_VECT)
      return gentypeerr(contextptr);
    int neq=v0._VECTptr->size(); // neq equations
    v0=_trn(v0,contextptr);
    matrice A=*v0._VECTptr,B;
    if (ckmatrix(v1))
      B=gen2vecteur(_trn(v1,contextptr));
    else
      B=vecteur(1,v1);
    if (int(B[0]._VECTptr->size())!=neq)
      return gendimerr(contextptr);
    int as=A.size(),bs=B.size(); 
    // bs system to solve, each with neq equations and as variables
    if (as>neq){ // under-determined system, find the smallest solution
      // not optimal since we solve the system for each Bi
      A.push_back(0);
      matrice res;
      for (int i=0;i<bs;++i){
	gen Bi=B[i];
	A[as]=Bi;
	matrice At=gen2vecteur(_trn(A,contextptr));
	vecteur B=mker(At,contextptr);
	if (is_undef(B) || B.empty())
	  return undef;
	// The last element of B must have a non-zero last component
	vecteur Bend=*B.back()._VECTptr;
	gen last=-Bend.back();
	if (is_zero(last))
	  return vecteur(0);
	vecteur R=divvecteur(Bend,last);
	R.pop_back();
	B.pop_back();
	int Bs=B.size();
	for (int j=0;j<Bs;j++)
	  B[j]._VECTptr->pop_back();
	// The solution is R+Vect(B[0],..,B[Bs-1])
	// the smallest solution is the orthogonal projection of 0 on R+Vect(B)
	// i.e. R + projection of -R on Vect(B)
	matrice r,Bg=gramschmidt(B,r,false,contextptr);
	gen Rtmp(R);
	for (int j=0;j<Bs;++j){
	  Rtmp -= dotvecteur(Bg[j],R)/dotvecteur(Bg[j],Bg[j])*Bg[j];
	}
	res.push_back(Rtmp);
      }
      res=gen2vecteur(_trn(res,contextptr));
      return res;
    }
    // orthogonal projection of each vector of B on image of A
    matrice r,Ag=gramschmidt(A,r,false,contextptr);
    matrice res;
    for (int i=0;i<bs;++i){
      gen Bi=B[i];
      vecteur tmp(as);
      for (int j=0;j<as;++j){
	tmp[j] = (Ag[j]*Bi)/(Ag[j]*Ag[j]);
      }
      res.push_back(tmp);
    }
    res=gen2vecteur(_trn(res,contextptr));
    return mmult(*inv(r,contextptr)._VECTptr,res);
  }
  static const char _LSQ_s[]="LSQ";
  static define_unary_function_eval (__LSQ,&_LSQ,_LSQ_s);
  define_unary_function_ptr5( at_LSQ ,alias_at_LSQ,&__LSQ,0,T_UNARY_OP_38);
  
  static string printasNTHROOT(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    // return '('+(printsommetasoperator(feuille," NTHROOT ",contextptr))+')';
    return printsommetasoperator(feuille," NTHROOT ",contextptr);
  }
  static string texprintasNTHROOT(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"\\mbox{ NTHROOT }",contextptr);
  }
  gen _NTHROOT(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return _surd(gen(makevecteur(args._VECTptr->back(),args._VECTptr->front()),_SEQ__VECT),contextptr);
  }
  static const char _NTHROOT_s[]="NTHROOT";
  static define_unary_function_eval4 (__NTHROOT,&_NTHROOT,_NTHROOT_s,&printasNTHROOT,&texprintasNTHROOT);
  define_unary_function_ptr5( at_NTHROOT ,alias_at_NTHROOT,&__NTHROOT,0,T_POW);

#ifndef GIAC_HAS_STO_38
  gen * rpn_ans(){
    return 0;
  }
#endif
  gen _Ans(const gen & args,GIAC_CONTEXT){
    if (rpn_ans())
      return *rpn_ans();
    return _ans(0,contextptr);
  }
  static const char _Ans_s[]="Ans";
  static string printasAns(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return _Ans_s;
  }
  static define_unary_function_eval2 (__Ans,&_Ans,_Ans_s,&printasAns);
  define_unary_function_ptr5( at_Ans ,alias_at_Ans,&__Ans,0,T_LITERAL);
  
  gen _EXPORT_(const gen & args,GIAC_CONTEXT){
    // eval sto only
    vecteur v =gen2vecteur(args);
    int nsto=0,s=v.size();
    for (int i=0;i<s;++i){
      if (v[i].is_symb_of_sommet(at_sto)){
	++nsto;
	eval(v[i],1,contextptr);
      }
    }
    return nsto;
  }
  static const char _EXPORT__s[]="EXPORT";
  static define_unary_function_eval (__EXPORT_,&_EXPORT_,_EXPORT__s);
  define_unary_function_ptr5( at_EXPORT ,alias_at_EXPORT_,&__EXPORT_,_QUOTE_ARGUMENTS,T_RETURN);
  
  gen _VIEWS(const gen & args,GIAC_CONTEXT){
    return _EXPORT_(args,contextptr);
  }
  static const char _VIEWS_s[]="VIEWS";
  static define_unary_function_eval (__VIEWS,&_VIEWS,_VIEWS_s);
  define_unary_function_ptr5( at_VIEWS ,alias_at_VIEWS,&__VIEWS,_QUOTE_ARGUMENTS,T_RETURN);
  
  gen _pointer(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    if (args._VECTptr->front().type!=_INT_ || args._VECTptr->back().type!=_INT_)
      return gentypeerr(contextptr);
    return gen((void *)(unsigned)args._VECTptr->front().val, args._VECTptr->back().val);
  }
  static const char _pointer_s[]="pointer";
  static define_unary_function_eval (__pointer,&_pointer,_pointer_s);
  define_unary_function_ptr5( at_pointer ,alias_at_pointer,&__pointer,0,T_UNARY_OP_38);
  
  int is_known_name_home_38(const char * idname){
    int s=strlen(idname);
    if (s==1 && idname[0]>='A' && idname[0]<='Z')
      return 3;
    if (s==2 && !strcmp(idname,"θ"))
      return 3;
    if (s==2 && (idname[0]=='Z' || idname[0]=='L' || idname[0]=='M') && idname[1]>='0' && idname[1]<='9')
      return 3;
    return is_known_name_38?is_known_name_38(0,idname):0;
  }

  // 1 and 2 app or program variable, 3 home variable
  int is_known_name_home_38(const char * name_space,const char * idname){
    if (name_space)
      return is_known_name_38?is_known_name_38(name_space,idname):0;
    return is_known_name_home_38(idname);
  }

  static gen qualifysubst(const gen & g,const vecteur & vin,const vecteur & vout,GIAC_CONTEXT){
    if (vin.empty()) return g;
    if (g.type!=_VECT){
      if (g.is_symb_of_sommet(at_sto)){
	gen & f = g._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2){
	  return symbolic(at_sto,gen(makevecteur(subst(f._VECTptr->front(),vin,vout,true,contextptr),f._VECTptr->back()),_SEQ__VECT));
	}
      }
      return subst(g,vin,vout,true,contextptr);
    }
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(qualifysubst(*it,vin,vout,contextptr));
    return gen(res,g.subtype);
  }

  void qualify(gen & g,const vecteur & v,const gen & prog,GIAC_CONTEXT){
    if (v.empty()) return;
    vecteur w(v);
    unsigned s=v.size();
    for (unsigned i=0;i<s;++i){
      w[i]=symbolic(at_double_deux_points,gen(makevecteur(prog,w[i]),_SEQ__VECT));
    }   
    g=subst(g,v,w,true,contextptr);
    //g=qualifysubst(g,v,w,contextptr);
  }

  // 0: ok, -1: invalid views, -2: invalid VIEWS/EXPORT declaration in a program
  static int parse_program_decl(vecteur & assignation_by_equal,vecteur & undeclared_global_vars,vecteur & declared_global_vars,vecteur & declared_functions,vecteur & exported_function_names,vecteur & exported_variable_names,vecteur & unknown_exported,vecteur & unexported,vecteur & unexported_declared_global_vars,vecteur & views,vecteur & errors,const gen & prog,gen & parsed,GIAC_CONTEXT){
    check_local_assign(parsed,vecteur(0),assignation_by_equal,undeclared_global_vars,declared_global_vars,declared_functions,true,contextptr);
    vecteur vprog=lop(parsed,at_program);
    if (!lop(vprog,at_VIEWS).empty() || !lop(vprog,at_EXPORT).empty())
      return -2;
    // views
    vecteur vviews=lop(parsed,at_VIEWS);
    iterateur vt=vviews.begin(),vtend=vviews.end();
    for (;vt!=vtend;++vt){
      gen & f = vt->_SYMBptr->feuille;
      if (f.type==_VECT){
	vecteur v=*f._VECTptr;
	if (v.size()==2){
	  if (v.front().type==_STRNG)
	    reverse(v.begin(),v.end());
	  if (v.front().is_symb_of_sommet(at_sto))
	    v.front()=v.front()._SYMBptr->feuille[1];
	  views=mergevecteur(views,v);
	}
	else return -1;
      }
    }
    // exported/unexported: declaration like export f,"help for f";
    vecteur exported=lop(parsed,at_EXPORT),exported_names;
    if (exported.empty()){
      unexported=declared_functions;
    }
    else {
      iterateur it=exported.begin(),itend=exported.end();
      for (;it!=itend;++it){
	gen & f = it->_SYMBptr->feuille;
	if (f.type==_VECT){
	  vecteur v=*f._VECTptr;
	  if (!v.empty()){
	    if (v.front().type==_STRNG)
	      reverse(v.begin(),v.end());
	    if (v.front().is_symb_of_sommet(at_sto))
	      v.front()=v.front()._SYMBptr->feuille[1];
	    exported_names=mergevecteur(exported_names,v);
	  }
	}
	else {
	  exported_names.push_back(f.is_symb_of_sommet(at_sto)?f._SYMBptr->feuille[1]:f);
	}
      }
      // check if exported functions are defined or exported variables declared
      // check also that we do not redefine regular home variables
      itend=exported_names.end();
      // first replace function definition by function name
      for (it=exported_names.begin();it!=itend;++it){
	if (it->is_symb_of_sommet(at_sto)){
	  *it=it->_SYMBptr->feuille;
	  if (it->type==_VECT && it->_VECTptr->size()==2)
	    *it=it->_VECTptr->back();
	}
      }
      for (it=exported_names.begin();it!=itend;++it){
	if (it->type!=_STRNG){
	  if (equalposcomp(declared_functions,*it) && !equalposcomp(declared_global_vars,*it) && !equalposcomp(undeclared_global_vars,*it))
	    unknown_exported.push_back(*it);
	  if (it->type==_IDNT && is_known_name_home_38(it->_IDNTptr->id_name))
	    errors.push_back(*it);
	}
      }
      it=declared_functions.begin(),itend=declared_functions.end();
      for (;it!=itend;++it){
	if (!equalposcomp(exported_names,*it))
	  unexported.push_back(*it);
      }
    }
    // remove exported functions/views and variables from undeclared global vars
    // check other: either home variables or put in errors
    vecteur tmp;
    tmp=undeclared_global_vars;
    undeclared_global_vars.clear();
    const_iterateur jt=tmp.begin(),jtend=tmp.end();
    for (;jt!=jtend;++jt){
      if (jt->type!=_IDNT)
	continue;
      if (!equalposcomp(declared_functions,*jt) && !equalposcomp(exported_names,*jt)){
	if (is_known_name_home_38(0,jt->_IDNTptr->id_name))
	  undeclared_global_vars.push_back(*jt);
	else
	  errors.push_back(*jt);
      }
    }
    // keep only home and app variables in declared global vars
    tmp=declared_global_vars;
    declared_global_vars.clear();
    jt=tmp.begin(); jtend=tmp.end();
    for (;jt!=jtend;++jt){
      if (jt->type==_IDNT && !equalposcomp(exported_names,*jt)){
	if (is_known_name_home_38(0,jt->_IDNTptr->id_name))
	  declared_global_vars.push_back(*jt);
	else
	  unexported_declared_global_vars.push_back(*jt);
      }
    }
    // split exported_names into function/variables
    exported_function_names.clear();
    exported_variable_names.clear();
    if (!exported_names.empty()){
      vecteur sto_var=lop(parsed,at_sto);
      int s=exported_names.size();
      int is_var=-1;
      for (int i=0;i<s;++i){
	gen & var=exported_names[i];
	if (var.type==_IDNT){
	  const_iterateur it=sto_var.begin(),itend=sto_var.end();
	  for (;it!=itend;++it){
	    if (it->type!=_SYMB) continue;
	    gen & f =it->_SYMBptr->feuille;
	    if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->back()==var){
	      if (f._VECTptr->front().is_symb_of_sommet(at_program))
		is_var=0;
	      else
		is_var=1;
	      break;
	    }
	  }
	  if (it==itend)
	    is_var=1;
	}
	if (is_var>=0){
	  if (is_var)
	    exported_variable_names.push_back(var);
	  else
	    exported_function_names.push_back(var);
	}
      }
    }
    // qualify unexported functions and declared unexported variables
    qualify(parsed,unexported,prog,contextptr);
    qualify(parsed,unexported_declared_global_vars,prog,contextptr);
    qualify(parsed,exported_variable_names,prog,contextptr);
#ifndef RTOS_THREADX
    ofstream of("c:\\log"); of << "=" << assignation_by_equal << endl << "undecl vars:" << undeclared_global_vars << endl << "decl vars:" << declared_global_vars << endl << "decl func.:" << declared_functions << endl << "exported func:" << exported_function_names << endl << "exported vars:"<<exported_variable_names << endl << "unknown exported:"<<unknown_exported << endl << "unexported:" << unexported << endl << "unexported declared global vars:"<<unexported_declared_global_vars << endl << "views:" << views << endl << "errors:" << errors << endl << "prog:"<<prog << endl << "parsed:" << parsed; of.close();   
#endif
    return errors.size();
  }

  // 0: ok, -1: invalid view, >0: #errors
  int parse_program(const wchar_t * source,const wchar_t * progname,vecteur & assignation_by_equal,vecteur & undeclared_global_vars,vecteur & declared_global_vars,vecteur & exported_function_names,vecteur & exported_variable_names,vecteur & unknown_exported,vecteur & unexported,vecteur & unexported_declared_global_vars,vecteur & views,vecteur & errors,gen & parsed,GIAC_CONTEXT){
    unsigned utfs = giacmax(utf8length(source),utf8length(progname)); 
    char * utf8source = new char[utfs+1];
    unicode2utf8(source,utf8source,wstrlen(source));
    string s(utf8source);
    unicode2utf8(progname,utf8source,wstrlen(progname));
    string utf8progname(utf8source);
    gen prog=identificateur(utf8progname);
    delete [] utf8source;
    int c=calc_mode(contextptr);
    calc_mode(contextptr)=-38;
    parsed=gen(s,contextptr);
    calc_mode(contextptr)=c;
    vecteur declared_functions;
    int err=parse_program_decl(assignation_by_equal,undeclared_global_vars,declared_global_vars,declared_functions,exported_function_names,exported_variable_names,unknown_exported,unexported,unexported_declared_global_vars,views,errors,prog,parsed,contextptr);
    return err;
  }

  gen _testfunc(const gen & g0,GIAC_CONTEXT){
    if (g0.type==_STRNG){
      int c=calc_mode(contextptr);
      calc_mode(contextptr)=-38;
      gen parsed(*g0._STRNGptr,contextptr);
      calc_mode(contextptr)=c;
      gen prog("abc",contextptr);
      vecteur assignation_by_equal,undeclared_global_vars,declared_global_vars,declared_functions,exported_function_names,exported_variable_names,unknown_exported,unexported,unexported_declared_global_vars,views,errors;
      parse_program_decl(assignation_by_equal,undeclared_global_vars,declared_global_vars,declared_functions,exported_function_names,exported_variable_names,unknown_exported,unexported,unexported_declared_global_vars,views,errors,prog,parsed,contextptr);
      // eval 
      gen res=eval(parsed,1,contextptr);
      vecteur resv=makevecteur(assignation_by_equal,undeclared_global_vars,declared_global_vars,exported_function_names,exported_variable_names,unknown_exported,unexported,unexported_declared_global_vars,errors);
      resv.push_back(res);
      return resv;
    }
    return undef;
  }
  static const char _testfunc_s[]="testfunc";
  static define_unary_function_eval(__testfunc,&_testfunc,_testfunc_s);
  define_unary_function_ptr5( at_testfunc ,alias_at_testfunc,&__testfunc,0,true);


#ifdef DOUBLEVAL
  const identificateur nsymbolic__IDNT("nsymbolic");
  const gen nsymbolic(nsymbolic__IDNT);

  const identificateur u0_nm1__IDNT("u0_nm1");
  const gen u0_nm1(u0_nm1__IDNT);
  const identificateur v0_nm2__IDNT("v0_nm2");
  const gen v0_nm2(v0_nm2__IDNT);
  const identificateur U0__IDNT("U0");
  const gen U0_idnt(U0__IDNT);

  const identificateur u1_nm1__IDNT("u1_nm1");
  const gen u1_nm1(u1_nm1__IDNT);
  const identificateur v1_nm2__IDNT("v1_nm2");
  const gen v1_nm2(v1_nm2__IDNT);
  const identificateur U1__IDNT("U1");
  const gen U1_idnt(U1__IDNT);

  const identificateur u2_nm1__IDNT("u2_nm1");
  const gen u2_nm1(u2_nm1__IDNT);
  const identificateur v2_nm2__IDNT("v2_nm2");
  const gen v2_nm2(v2_nm2__IDNT);
  const identificateur U2__IDNT("U2");
  const gen U2_idnt(U2__IDNT);

  const identificateur u3_nm1__IDNT("u3_nm1");
  const gen u3_nm1(u3_nm1__IDNT);
  const identificateur v3_nm2__IDNT("v3_nm2");
  const gen v3_nm2(v3_nm2__IDNT);
  const identificateur U3__IDNT("U3");
  const gen U3_idnt(U3__IDNT);

  const identificateur u4_nm1__IDNT("u4_nm1");
  const gen u4_nm1(u4_nm1__IDNT);
  const identificateur v4_nm2__IDNT("v4_nm2");
  const gen v4_nm2(v4_nm2__IDNT);
  const identificateur U4__IDNT("U4");
  const gen U4_idnt(U4__IDNT);

  const identificateur u5_nm1__IDNT("u5_nm1");
  const gen u5_nm1(u5_nm1__IDNT);
  const identificateur v5_nm2__IDNT("v5_nm2");
  const gen v5_nm2(v5_nm2__IDNT);
  const identificateur U5__IDNT("U5");
  const gen U5_idnt(U5__IDNT);

  const identificateur u6_nm1__IDNT("u6_nm1");
  const gen u6_nm1(u6_nm1__IDNT);
  const identificateur v6_nm2__IDNT("v6_nm2");
  const gen v6_nm2(v6_nm2__IDNT);
  const identificateur U6__IDNT("U6");
  const gen U6_idnt(U6__IDNT);

  const identificateur u7_nm1__IDNT("u7_nm1");
  const gen u7_nm1(u7_nm1__IDNT);
  const identificateur v7_nm2__IDNT("v7_nm2");
  const gen v7_nm2(v7_nm2__IDNT);
  const identificateur U7__IDNT("U7");
  const gen U7_idnt(U7__IDNT);

  const identificateur u8_nm1__IDNT("u8_nm1");
  const gen u8_nm1(u8_nm1__IDNT);
  const identificateur v8_nm2__IDNT("v8_nm2");
  const gen v8_nm2(v8_nm2__IDNT);
  const identificateur U8__IDNT("U8");
  const gen U8_idnt(U8__IDNT);

  const identificateur u9_nm1__IDNT("u9_nm1");
  const gen u9_nm1(u9_nm1__IDNT);
  const identificateur v9_nm2__IDNT("v9_nm2");
  const gen v9_nm2(v9_nm2__IDNT);
  const identificateur U9__IDNT("U9");
  const gen U9_idnt(U9__IDNT);

#else // GIAC_HAS_STO_38
  const alias_ref_identificateur ref_nsymbolic={-1,0,0,"nsymbolic",0,0};
  const define_alias_gen(alias_nsymbolic,_IDNT,0,&ref_nsymbolic);
  const gen & nsymbolic = * (gen *) & alias_nsymbolic;

  const alias_ref_identificateur ref_u0_nm1={-1,0,0,"u0_nm1",0,0};
  const define_alias_gen(alias_u0_nm1,_IDNT,0,&ref_u0_nm1);
  const gen & u0_nm1 = * (gen *) & alias_u0_nm1;
  const alias_ref_identificateur ref_v0_nm2={-1,0,0,"v0_nm2",0,0};
  const define_alias_gen(alias_v0_nm2,_IDNT,0,&ref_v0_nm2);
  const gen & v0_nm2 = * (gen *) & alias_v0_nm2;
  const alias_ref_identificateur ref_U0_idnt={-1,0,0,"U0",0,0};
  const define_alias_gen(alias_U0_idnt,_IDNT,0,&ref_U0_idnt);
  const gen & U0_idnt = * (gen *) & alias_U0_idnt;

  const alias_ref_identificateur ref_u1_nm1={-1,0,0,"u1_nm1",0,0};
  const define_alias_gen(alias_u1_nm1,_IDNT,0,&ref_u1_nm1);
  const gen & u1_nm1 = * (gen *) & alias_u1_nm1;
  const alias_ref_identificateur ref_v1_nm2={-1,0,0,"v1_nm2",0,0};
  const define_alias_gen(alias_v1_nm2,_IDNT,0,&ref_v1_nm2);
  const gen & v1_nm2 = * (gen *) & alias_v1_nm2;
  const alias_ref_identificateur ref_U1_idnt={-1,0,0,"U1",0,0};
  const define_alias_gen(alias_U1_idnt,_IDNT,0,&ref_U1_idnt);
  const gen & U1_idnt = * (gen *) & alias_U1_idnt;

  const alias_ref_identificateur ref_u2_nm1={-1,0,0,"u2_nm1",0,0};
  const define_alias_gen(alias_u2_nm1,_IDNT,0,&ref_u2_nm1);
  const gen & u2_nm1 = * (gen *) & alias_u2_nm1;
  const alias_ref_identificateur ref_v2_nm2={-1,0,0,"v2_nm2",0,0};
  const define_alias_gen(alias_v2_nm2,_IDNT,0,&ref_v2_nm2);
  const gen & v2_nm2 = * (gen *) & alias_v2_nm2;
  const alias_ref_identificateur ref_U2_idnt={-1,0,0,"U2",0,0};
  const define_alias_gen(alias_U2_idnt,_IDNT,0,&ref_U2_idnt);
  const gen & U2_idnt = * (gen *) & alias_U2_idnt;

  const alias_ref_identificateur ref_u3_nm1={-1,0,0,"u3_nm1",0,0};
  const define_alias_gen(alias_u3_nm1,_IDNT,0,&ref_u3_nm1);
  const gen & u3_nm1 = * (gen *) & alias_u3_nm1;
  const alias_ref_identificateur ref_v3_nm2={-1,0,0,"v3_nm2",0,0};
  const define_alias_gen(alias_v3_nm2,_IDNT,0,&ref_v3_nm2);
  const gen & v3_nm2 = * (gen *) & alias_v3_nm2;
  const alias_ref_identificateur ref_U3_idnt={-1,0,0,"U3",0,0};
  const define_alias_gen(alias_U3_idnt,_IDNT,0,&ref_U3_idnt);
  const gen & U3_idnt = * (gen *) & alias_U3_idnt;

  const alias_ref_identificateur ref_u4_nm1={-1,0,0,"u4_nm1",0,0};
  const define_alias_gen(alias_u4_nm1,_IDNT,0,&ref_u4_nm1);
  const gen & u4_nm1 = * (gen *) & alias_u4_nm1;
  const alias_ref_identificateur ref_v4_nm2={-1,0,0,"v4_nm2",0,0};
  const define_alias_gen(alias_v4_nm2,_IDNT,0,&ref_v4_nm2);
  const gen & v4_nm2 = * (gen *) & alias_v4_nm2;
  const alias_ref_identificateur ref_U4_idnt={-1,0,0,"U4",0,0};
  const define_alias_gen(alias_U4_idnt,_IDNT,0,&ref_U4_idnt);
  const gen & U4_idnt = * (gen *) & alias_U4_idnt;

  const alias_ref_identificateur ref_u5_nm1={-1,0,0,"u5_nm1",0,0};
  const define_alias_gen(alias_u5_nm1,_IDNT,0,&ref_u5_nm1);
  const gen & u5_nm1 = * (gen *) & alias_u5_nm1;
  const alias_ref_identificateur ref_v5_nm2={-1,0,0,"v5_nm2",0,0};
  const define_alias_gen(alias_v5_nm2,_IDNT,0,&ref_v5_nm2);
  const gen & v5_nm2 = * (gen *) & alias_v5_nm2;
  const alias_ref_identificateur ref_U5_idnt={-1,0,0,"U5",0,0};
  const define_alias_gen(alias_U5_idnt,_IDNT,0,&ref_U5_idnt);
  const gen & U5_idnt = * (gen *) & alias_U5_idnt;

  const alias_ref_identificateur ref_u6_nm1={-1,0,0,"u6_nm1",0,0};
  const define_alias_gen(alias_u6_nm1,_IDNT,0,&ref_u6_nm1);
  const gen & u6_nm1 = * (gen *) & alias_u6_nm1;
  const alias_ref_identificateur ref_v6_nm2={-1,0,0,"v6_nm2",0,0};
  const define_alias_gen(alias_v6_nm2,_IDNT,0,&ref_v6_nm2);
  const gen & v6_nm2 = * (gen *) & alias_v6_nm2;
  const alias_ref_identificateur ref_U6_idnt={-1,0,0,"U6",0,0};
  const define_alias_gen(alias_U6_idnt,_IDNT,0,&ref_U6_idnt);
  const gen & U6_idnt = * (gen *) & alias_U6_idnt;

  const alias_ref_identificateur ref_u7_nm1={-1,0,0,"u7_nm1",0,0};
  const define_alias_gen(alias_u7_nm1,_IDNT,0,&ref_u7_nm1);
  const gen & u7_nm1 = * (gen *) & alias_u7_nm1;
  const alias_ref_identificateur ref_v7_nm2={-1,0,0,"v7_nm2",0,0};
  const define_alias_gen(alias_v7_nm2,_IDNT,0,&ref_v7_nm2);
  const gen & v7_nm2 = * (gen *) & alias_v7_nm2;
  const alias_ref_identificateur ref_U7_idnt={-1,0,0,"U7",0,0};
  const define_alias_gen(alias_U7_idnt,_IDNT,0,&ref_U7_idnt);
  const gen & U7_idnt = * (gen *) & alias_U7_idnt;

  const alias_ref_identificateur ref_u8_nm1={-1,0,0,"u8_nm1",0,0};
  const define_alias_gen(alias_u8_nm1,_IDNT,0,&ref_u8_nm1);
  const gen & u8_nm1 = * (gen *) & alias_u8_nm1;
  const alias_ref_identificateur ref_v8_nm2={-1,0,0,"v8_nm2",0,0};
  const define_alias_gen(alias_v8_nm2,_IDNT,0,&ref_v8_nm2);
  const gen & v8_nm2 = * (gen *) & alias_v8_nm2;
  const alias_ref_identificateur ref_U8_idnt={-1,0,0,"U8",0,0};
  const define_alias_gen(alias_U8_idnt,_IDNT,0,&ref_U8_idnt);
  const gen & U8_idnt = * (gen *) & alias_U8_idnt;

  const alias_ref_identificateur ref_u9_nm1={-1,0,0,"u9_nm1",0,0};
  const define_alias_gen(alias_u9_nm1,_IDNT,0,&ref_u9_nm1);
  const gen & u9_nm1 = * (gen *) & alias_u9_nm1;
  const alias_ref_identificateur ref_v9_nm2={-1,0,0,"v9_nm2",0,0};
  const define_alias_gen(alias_v9_nm2,_IDNT,0,&ref_v9_nm2);
  const gen & v9_nm2 = * (gen *) & alias_v9_nm2;
  const alias_ref_identificateur ref_U9_idnt={-1,0,0,"U9",0,0};
  const define_alias_gen(alias_U9_idnt,_IDNT,0,&ref_U9_idnt);
  const gen & U9_idnt = * (gen *) & alias_U9_idnt;

#endif

  int has_Un(const gen & g){
    vecteur l(lvar(g));
    for (unsigned i=0;i<l.size();++i){
      if (l[i].type==_IDNT){
	const char * s=l[i]._IDNTptr->id_name;
	if (strlen(s)==2 && s[0]=='U' && s[1]>='0' && s[1]<='9')
	  return s[1]-'0';
      }
    }
    return -1;
  }
  
  static void seqapp_lop_of(const gen & g,vecteur & res){
    vecteur vof(lop(g,at_of));
    const_iterateur it=vof.begin(),itend=vof.end();
    for (;it!=itend;++it){
      gen & f=it->_SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2 || f._VECTptr->front().type!=_IDNT)
	continue;
      if (f._VECTptr->back().type==_SYMB)
	seqapp_lop_of(f._VECTptr->back(),res); 
      res.push_back(*it);
    }
  }

  int is_n_minus_one_or_two(const gen & arg){
    if (arg.type!=_SYMB)
      return 0;
    if (arg._SYMBptr->feuille.type==_VECT && arg._SYMBptr->feuille._VECTptr->size()==2){
      gen a1=arg._SYMBptr->feuille._VECTptr->front();
      gen a2=arg._SYMBptr->feuille._VECTptr->back();
      if (a1.type==_IDNT && !strcmp(a1._IDNTptr->id_name,"N")){
	if (arg._SYMBptr->sommet==at_plus){
	  if (a2==-1) return 1;
	  if (a2==-2) return 2;
	}
	if (arg._SYMBptr->sommet==at_binary_minus){
	  if (a2==1) return 1;
	  if (a2==2) return 2;
	}	  
      }
    }
    return 0;
  }
  
  // Prepares app sequence for computing a recurrence relation
  // Valid if 1 sequence is checked and does not depend on other sequences
  // Given expr_un, the expression of UK(N) in terms of UK(N-1) and UK(N-2)
  // write the recurrence relation as UK(N)=subst(expr,vars,[N,UK(N-1),UK(N-2)])
  // Return 0 or -10-val if expr_un is invalid or depends on Uval, 
  // 1 if it does not depend on UK(N-2)
  // 3 if not recurrent
  // 2 otherwise
  int seqapp_prepare(const gen & expr_un,gen & expr,vecteur & vars,GIAC_CONTEXT,int seqno){
    if (has_Un(expr_un)!=-1)
      return 0;
    vecteur vof; seqapp_lop_of(expr_un,vof); // (lop(expr_un,at_of));
    // check functions with names in U0-U9
    int s=vof.size(),retval=1;
    gen uk(vx_var);
    bool notrecurrent=true;
    for (int i=0;i<s;++i){
      gen & f=vof[i]._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2 || f._VECTptr->front().type!=_IDNT)
	continue;
      gen & id=f._VECTptr->front();
      const char * idname=id._IDNTptr->id_name;
      if (strlen(idname)!=2 || idname[0]!='U' || idname[1]<'0' || idname[1]>'9')
	continue;
      notrecurrent=false;
      uk=id;
      if (seqno==-1){
	seqno=idname[1]-'0';
      }
      else {
	if (seqno!=idname[1]-'0')
	  return -10-(idname[1]-'0');
      }
      gen & arg=f._VECTptr->back();
#if 1
      int test=is_n_minus_one_or_two(arg);
      if (!test)
	return 0;
      if (test==2)
	retval=2;
#else
      if (arg!=n__IDNT_e-1 && arg!=n__IDNT_e-2)
	return 0;
      if (arg==n__IDNT_e-2)
	retval=2;
#endif
    }
    identificateur uk_nm1_("uk_nm1"),uk_nm2_("uk_nm2");
    gen uk_nm1(uk_nm1_),uk_nm2(uk_nm2_);
    vecteur vars0(makevecteur(n__IDNT_e,symb_of(uk,n__IDNT_e-1),symb_of(uk,n__IDNT_e-2)));
    vars=makevecteur(nsymbolic,uk_nm1,uk_nm2);
    expr=subst(expr_un,vars0,vars,true,contextptr);
    /* additional checking disabled
    vecteur lv(makevecteur(uk_nm1,uk_nm2));
    lidnt(expr,lv);
    s=lv.size();
    for (int i=2;i<s;++i){
      gen & g =lv[i];
      if (g.type!=_IDNT)
	return 0;
      if (strlen(g._IDNTptr->id_name)>1)
	return 0;
      if (g._IDNTptr->id_name[0]<'A' || g._IDNTptr->id_name[0]>'Z')
	return 0;
    }
    */
    return notrecurrent?3:retval;
  }

  // Compute UK(N) for K=k to m, where UK(k) and UK(k+1) are given
  // If the recurrence relation does not depend on UK(N-2), set UK(k+1) to undef
  vecteur seqapp_compute(const gen & expr0,const vecteur & vars,const gen & UK_k,const gen &UK_kp1,int k,int m,GIAC_CONTEXT){
    gen f1f9(lidnt_function38(expr0));
    gen f1f9value=eval(f1f9,1,contextptr);
    gen expr(expr0);
    if (f1f9.type==_VECT && !f1f9._VECTptr->empty())
      expr=subst(expr0,f1f9,f1f9value,true,contextptr);
    gen UK_nm2(UK_k),UK_nm1(UK_kp1),tmp;
    if (is_undef(UK_k))// also compute UK_n
      UK_nm2=subst(expr,vars,makevecteur(k,UK_k,undef),false,contextptr);
    if (is_undef(UK_kp1))// compute UK_np1
      UK_nm1=subst(expr,vars,makevecteur(k+1,UK_k,undef),false,contextptr);
    UK_nm1=evalf(UK_nm1,1,contextptr);
    if (UK_nm1.type>_REAL && UK_nm1.type!=_FLOAT_)
      UK_nm1=undef;
    UK_nm2=evalf(UK_nm2,1,contextptr);
    if (UK_nm2.type>_REAL && UK_nm2.type!=_FLOAT_)
      UK_nm2=undef;    
    vecteur res(makevecteur(UK_nm2,UK_nm1));
    for (int n=k+2;n<=m;++n){
      tmp=subst(expr,vars,makevecteur(n,UK_nm1,UK_nm2),false,contextptr);
      tmp=evalf(tmp,1,contextptr);
      if (tmp.type>_REAL && tmp.type!=_FLOAT_)
	res.push_back(undef);
      else
	res.push_back(tmp);
      UK_nm2=UK_nm1;
      UK_nm1=tmp;
    }
    return res;
  }



  // Prepares app sequence for computing all recurrences relations
  // expr_un should contain the expression for U0(N) to UK(N) for K<=9
  // undef may be used if the sequence is not checked
  // Rewrite the recurrence relation as [U0(N),...,UK(N)]=subst(expr,vars,[N,U0(N-1),U0(N-2),...])
  // Return 0 if expr_un is invalid, -10-val if Uval should be checked
  // Return 1 if it does not depend on UK(N-2), 3 if not recurrent, 2 otherwise
  int seqapp_prepare(const vecteur & expr_un,vecteur & expr,vecteur & vars,GIAC_CONTEXT){
    int dim=expr_un.size();
    if (has_Un(expr_un)!=-1)
      return 0;
    vecteur vof; seqapp_lop_of(expr_un,vof); // (lop(expr_un,at_of));
    vector<int> defined_seqs;
    for (unsigned i=0;i<expr_un.size();++i){
      if (!is_undef(expr_un[i]))
	defined_seqs.push_back(i); // allowed sequences numbers (starts with U0, not U1)
    }
    // check functions with names in U0-U9
    int s=vof.size(),retval=3;
    gen uk;
    for (int i=0;i<s;++i){
      gen & f=vof[i]._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2 || f._VECTptr->front().type!=_IDNT)
	continue;
      gen & id=f._VECTptr->front();
      const char * idname=id._IDNTptr->id_name;
      if (strlen(idname)!=2 || idname[0]!='U' || idname[1]<'0' || idname[1]>'9')
	continue;
      int val=idname[1]-'0';
      if (!equalposcomp(defined_seqs,val))
	return -val-10;
      gen & arg=f._VECTptr->back();
#if 1
      int test=is_n_minus_one_or_two(arg);
      if (!test)
	return 0;
      if (test==2)
	retval=2;
      else {
	if (retval!=2)
	  retval=1;
      }
#else
      if (arg!=n__IDNT_e-1 && arg!=n__IDNT_e-2)
	return 0;
      if (arg==n__IDNT_e-2)
	retval=2;
      else {
	if (retval!=2)
	  retval=1;
      }
#endif
    }
    vecteur vars0(1,n__IDNT_e); vars=vecteur(1,nsymbolic);
    vecteur tab_uk(30);
    tab_uk[0]=u0_nm1; // (identificateur("u0_nm1"));
    tab_uk[1]=v0_nm2; // (identificateur("v0_nm2"));
    tab_uk[2]=U0_idnt; // (identificateur("U0"));
    tab_uk[3]=u1_nm1; // (identificateur("u1_nm1"));
    tab_uk[4]=v1_nm2; // (identificateur("v1_nm2"));
    tab_uk[5]=U1_idnt; // (identificateur("U1"));
    tab_uk[6]=u2_nm1; // (identificateur("u2_nm1"));
    tab_uk[7]=v2_nm2; // (identificateur("v2_nm2"));
    tab_uk[8]=U2_idnt; // (identificateur("U2"));
    tab_uk[9]=u3_nm1; // (identificateur("u3_nm1"));
    tab_uk[10]=v3_nm2; // (identificateur("v3_nm2"));
    tab_uk[11]=U3_idnt; // (identificateur("U3"));
    tab_uk[12]=u4_nm1; // (identificateur("u4_nm1"));
    tab_uk[13]=v4_nm2; // (identificateur("v4_nm2"));
    tab_uk[14]=U4_idnt; // (identificateur("U4"));
    tab_uk[15]=u5_nm1; // (identificateur("u5_nm1"));
    tab_uk[16]=v5_nm2; // (identificateur("v5_nm2"));
    tab_uk[17]=U5_idnt; // (identificateur("U5"));
    tab_uk[18]=u6_nm1; // (identificateur("u6_nm1"));
    tab_uk[19]=v6_nm2; // (identificateur("v6_nm2"));
    tab_uk[20]=U6_idnt; // (identificateur("U6"));
    tab_uk[21]=u7_nm1; // (identificateur("u7_nm1"));
    tab_uk[22]=v7_nm2; // (identificateur("v7_nm2"));
    tab_uk[23]=U7_idnt; // (identificateur("U7"));
    tab_uk[24]=u8_nm1; // (identificateur("u8_nm1"));
    tab_uk[25]=v8_nm2; // (identificateur("v8_nm2"));
    tab_uk[26]=U8_idnt; // (identificateur("U8"));
    tab_uk[27]=u9_nm1; // (identificateur("u9_nm1"));
    tab_uk[28]=v9_nm2; // (identificateur("v9_nm2"));
    tab_uk[29]=U9_idnt; // (identificateur("U9"));
    gen n_minus_1=n__IDNT_e-1;
    for (int i=0;i<dim;++i){
      gen uk=tab_uk[3*i+2];
      vars0.push_back(new_ref_symbolic(symbolic(at_of,makenewvecteur(uk,n_minus_1))));
      vars.push_back(tab_uk[3*i]);
    }
    gen n_minus_2=n__IDNT_e-2;
    for (int i=0;i<dim;++i){
      gen uk=tab_uk[3*i+2];
      vars0.push_back(new_ref_symbolic(symbolic(at_of,makenewvecteur(uk,n_minus_2))));
      vars.push_back(tab_uk[3*i+1]);
    }
    expr=subst(expr_un,vars0,vars,true,contextptr);
    /* additional checking disabled
    vecteur lv(vars);
    lidnt(expr,lv);
    s=lv.size();
    for (int i=vars.size();i<s;++i){
      gen & g =lv[i];
      if (is_undef(g)) continue;
      if (g.type!=_IDNT)
	return 0;
      if (strlen(g._IDNTptr->id_name)>1)
	return 0;
      if (g._IDNTptr->id_name[0]<'A' || g._IDNTptr->id_name[0]>'Z')
	return 0;
    }
    */
    return retval;
  }


  // Compute UK(N) for K=k to m, where UK(k) and UK(k+1) are given
  // If the recurrence relation does not depend on UK(N-2), set UK_kp1 to vecteur(0)
  vecteur seqapp_compute(const vecteur & expr0,const vecteur & vars,const vecteur & UK_k,const vecteur &UK_kp1,int k,int m,GIAC_CONTEXT){
    gen f1f9(lidnt_function38(expr0));
    gen f1f9value=eval(f1f9,1,contextptr);
    vecteur expr(expr0);
    if (f1f9.type==_VECT && !f1f9._VECTptr->empty()){
      iterateur it=expr.begin(),itend=expr.end();
      for (;it!=itend;++it){
	*it = subst(*it,f1f9,f1f9value,true,contextptr);
      }
    }
    int dim=expr.size();
    vecteur UK_nm2(UK_k),UK_nm1(UK_kp1),tmp(dim);
    vecteur vals(2*dim+1);
    if (UK_k.empty()){// compute UK_n
      vals[0]=k;
      for (int i=0;i<dim;++i){
	vals[i+1]=undef;
      }      
      for (int i=0;i<dim;++i)
	vals[dim+i+1]=undef;
      UK_nm2=gen2vecteur(subst(expr,vars,vals,false,contextptr));
    }
    if (dim!=int(UK_nm2.size()))
      return vecteur(dim,gendimerr(contextptr));
    if (UK_kp1.empty()){// compute UK_np1
      vals[0]=k+1;
      for (int i=0;i<dim;++i){
	vals[i+1]=UK_nm2[i];
      }      
      for (int i=0;i<dim;++i)
	vals[dim+i+1]=undef;
      UK_nm1=subst(expr,vars,vals,false,contextptr);
    }
    gen tmpU=evalf(UK_nm2,1,contextptr);
    if (tmpU.type!=_VECT)
      return vecteur(dim,gendimerr(contextptr));
    UK_nm2=*tmpU._VECTptr;
    tmpU=evalf(UK_nm1,1,contextptr);
    if (tmpU.type!=_VECT)
      return vecteur(dim,gendimerr(contextptr));
    UK_nm1=*tmpU._VECTptr;
    vecteur res(makevecteur(UK_nm2,UK_nm1));
    res.reserve(m-k+1);
    for (int n=k+2;n<=m;++n){
      vals[0]=n;
      for (int i=0;i<dim;++i){
	vals[i+1]=UK_nm1[i];
      }
      for (int i=0;i<dim;++i){
	vals[dim+i+1]=UK_nm2[i];
      }
      vecteur::const_iterator it=expr.begin(),itend=expr.end();
      for (int i=0;it!=itend;++it,++i)
	tmp[i]=is_undef(*it)?*it:evalf(sortsubst(*it,vars,vals,false,contextptr),1,contextptr);
      res.push_back(tmp);
      UK_nm2=UK_nm1;
      UK_nm1=tmp;
    }
    return res;
  }

  gen _tests(const gen & g0,GIAC_CONTEXT){
    // return eval_before_diff(g0,vx_var,contextptr);
    if (g0.type==_VECT){
      vecteur expr,vars;
      if (g0.subtype==_SEQ__VECT && g0._VECTptr->front().type==_VECT && g0._VECTptr->size()==3){
	int i=seqapp_prepare(*g0._VECTptr->front()._VECTptr,expr,vars,contextptr);
	if (i<0)
	  return gensizeerr(contextptr);
	gen g01=(*g0._VECTptr)[1];
	gen g02=(*g0._VECTptr)[2];
	vecteur v2;
	if (i==2 && g02.type==_VECT)
	  v2=*g02._VECTptr;
	if (g01.type==_VECT)
	  return seqapp_compute(expr,vars,*g01._VECTptr,v2,0,20,contextptr);
      }
      int i=seqapp_prepare(*g0._VECTptr,expr,vars,contextptr);
      if (i<0)
	return gensizeerr(contextptr);
      return seqapp_compute(expr,vars,vecteur(expr.size(),1.0),i==1?vecteur(0):vecteur(expr.size(),2.0),0,20,contextptr);
    }
    gen expr; vecteur vars;
    int i=seqapp_prepare(g0,expr,vars,contextptr);
    if (!i)
      return undef;
    return seqapp_compute(expr,vars,1.0,i==1?undef:2.0,0,20,contextptr);
  }
  static const char _tests_s[]="tests";
  static define_unary_function_eval(__tests,&_tests,_tests_s);
  define_unary_function_ptr5( at_tests ,alias_at_tests,&__tests,0,T_UNARY_OP_38);

#ifdef GIAC_HAS_STO_38
  gen aspen_choose(const vecteur & v,GIAC_CONTEXT);
#endif

  gen _CHOOSE(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG &&  args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<3)
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr; 
    if (v[0].is_symb_of_sommet(at_double_deux_points) && v[0]._SYMBptr->feuille.type==_VECT && v[0]._SYMBptr->feuille._VECTptr->size()==2)
      v[0]=v[0]._SYMBptr->feuille._VECTptr->back();
    for (int i=1; i<=1; i++) { if (v[i].type!=_STRNG) v[i]= eval(v[i], 1, contextptr); if (v[i].type!=_STRNG) v[i]=  string2gen(v[i].print(contextptr)); }
    if (v.front().type!=_IDNT || v[1].type!=_STRNG)
      return gentypeerr(contextptr);
#ifdef GIAC_HAS_STO_38
    return aspen_choose(v,contextptr);
#else
    vecteur res(3);
    res[2]=v[0];
    res[0]=v[1];
    vecteur tmp;
    for (unsigned i=2;i<v.size();i++)
      tmp.push_back(eval(v[i],1,contextptr));
    res[1]=tmp;
    return __inputform.op(symbolic(at_choosebox,gen(res,_SEQ__VECT)),contextptr);
#endif
  }
  static const char _CHOOSE_s []="CHOOSE";
  static define_unary_function_eval_quoted (__CHOOSE,&giac::_CHOOSE,_CHOOSE_s);
  define_unary_function_ptr5( at_CHOOSE ,alias_at_CHOOSE,&__CHOOSE,_QUOTE_ARGUMENTS,T_UNARY_OP_38);



#ifdef GIAC_HAS_STO_38
  // encoding is up to 8 nibbles with various values...
  // 1 optional graphic name
  // 2 graphic name
  // 3 coordinate pair
  // 4 optional coordinate pair
  // 5 color
  // 6 optional color
  // 7 integer
  // 8 boolean
  // 9 graphic name, return the adresses'adress and does not allow G0!!!
  // a: angle
  // b: optional integer
  // c: w/h based on 0/0
  // 0x631 means : optional graphic name, coordinate pair, optional color

#define CyrilleFnc(n)   static const char _##n##_s []= #n;  static define_unary_function_eval_quoted (__##n,&giac::_##n,_##n##_s); define_unary_function_ptr5( at_##n ,alias_at_##n,&__##n,0,T_UNARY_OP_38);
  gen _FREEZE(const gen & args,GIAC_CONTEXT)
  {
    dofreeze();
    return 1;
  }
  CyrilleFnc(FREEZE);

  gen _GETPIX(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[2];
    if (!GraphicVerifInputs(args, &g, xy, 0x31, NULL, contextptr)) return gensizeerr(contextptr);
    return gen(dogetpix(g, xy[0], xy[1]));
  }
  CyrilleFnc(GETPIX);

  gen _LINE(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4], c= 0;
    if (!GraphicVerifInputs(args, &g, xy, 0x6331, &c, contextptr)) return gensizeerr(contextptr);
    doline(g, xy[0], xy[1], xy[2], xy[3], c);
    return 1;
  }
  CyrilleFnc(LINE);

  gen _RECT(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4]= {0, 0, 0x0fffffff,0x0fffffff}, c[2]= { 3, -1};
    if (!GraphicVerifInputs(args, &g, xy, 0x66441, c, contextptr)) return gensizeerr(contextptr);
    if (c[1]==-1) c[1]= c[0];
    dorect(g, xy[0], xy[1], xy[2]-xy[0]+1, xy[3]-xy[1]+1, c[0], c[1]);
    return 1;
  }
  CyrilleFnc(RECT);

  gen _INVERT(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4]= {0, 0, 0x0fffffff, 0x0fffffff };
    if (!GraphicVerifInputs(args, &g, xy, 0x441, NULL, contextptr)) return gensizeerr(contextptr);
    doinvert(g, xy[0], xy[1], xy[2]-xy[0]+1, xy[3]-xy[1]+1);
    return 1;
  }
  CyrilleFnc(INVERT);

  gen _BLIT(const gen & args,GIAC_CONTEXT)
  {
    void *g[2]; int xy[8]= { 0, 0, 0xfffffff, 0xfffffff, 0, 0, 0xfffffff, 0xfffffff }, c= -1;
    if (!GraphicVerifInputs(args, g, xy, 0x6442431, NULL, contextptr)) return gensizeerr(contextptr);
    xy[3]+= -xy[1]+1; if (xy[3]<0) xy[3]= -2-xy[3];
    xy[7]+= -xy[5]+1; if (xy[7]<0) xy[7]= -2-xy[7];
    doblit(g[0], xy[0], xy[1], xy[2]-xy[0]+1, xy[3], g[1], xy[4], xy[5], xy[6]-xy[4]+1, xy[7], c);
    return 1;
  }
  CyrilleFnc(BLIT);
  gen _TEXTOUT(const gen &args, GIAC_CONTEXT)
  {
    if (args.type!=_VECT) return gensizeerr(contextptr);
    int s= args.__VECTptr->v.size(); 
    if (s<3) return gensizeerr(contextptr);
    gen t= *args.__VECTptr->v.begin();
    gen v(*args._VECTptr); v._VECTptr->erase(v._VECTptr->begin());
    void *g; int xy[2]={0, 0}, c[4]= {0, 0, 1023, -1};
    //TEXTOUT("text", [G?], x, y, [font, [color, [width, [color]]]])
    if (!GraphicVerifInputs(v, &g, xy, 0x6b6b31, c, contextptr)) return gensizeerr(contextptr);
    return gen(dotextout(&t, g, xy, c, contextptr));
  }
  CyrilleFnc(TEXTOUT);

  gen _PIXON(const gen & args,GIAC_CONTEXT){
    void *g; int xy[2], c= 0;
    if (!GraphicVerifInputs(args, &g, xy, 0x631, &c, contextptr)) return gensizeerr(contextptr);
    dopixon(g, xy[0], xy[1], c);
    return 1;
  }

  gen _PIXOFF(const gen & args,GIAC_CONTEXT){
    void *g; int xy[2];
    if (!GraphicVerifInputs(args, &g, xy, 0x31, NULL, contextptr)) return gensizeerr(contextptr);
    dopixon(g, xy[0], xy[1], 3);
    return 1;
  }
  gen _DIMGROB(const gen &args,GIAC_CONTEXT)
  {
    void *g; int xy[2], c= 3;
    if (!GraphicVerifInputs(args, &g, xy, 0x6c9, &c, contextptr)) return gensizeerr(contextptr);
    dodimgrob((void**)g, xy[0], xy[1], c, gen());
    return 1;
  }
  CyrilleFnc(DIMGROB);
  gen _SUBGROB(const gen &args,GIAC_CONTEXT)
  {
    void *g[2]; int xy[4]= {0, 0, 0xfffffff, 0xfffffff};
    if (!GraphicVerifInputs(args, g, xy, 0x9441, NULL, contextptr)) return gensizeerr(contextptr);
    dosubgrob(g, xy);
    return 1;
  }
  CyrilleFnc(SUBGROB);
  gen _ARC(const gen & args,GIAC_CONTEXT)
  {
    ///< draws a circle, or an arc between a1 and a2. full circle is 4096... 
    // if OldColor is !=-1 and indiate the background color of the screen BEFORE the arc is drawn, the arc is filled...
    //int Arc(int x, int y, int r, int color, int a1=0, int a2=4096, int OldColor=-1); 
    // ARC(x, y, r, [c, [a1, a2]])
    void *g; int xy[2], c[4]= { 0, 0, 0, 4096};
    if (!GraphicVerifInputs(args, &g, xy, 0xaab731, c, contextptr)) return gensizeerr(contextptr);
    doarc(g, true, xy, c);
    return 1;
  }

  gen _GETPIX_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[2];
    if (!GraphicVerifInputs2(args, &g, xy, 0x31, NULL, true, contextptr)) return gensizeerr(contextptr);
    return gen(dogetpix(g, xy[0], xy[1]));
  }
  CyrilleFnc(GETPIX_P);

  gen _LINE_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4], c= 0;
    if (!GraphicVerifInputs2(args, &g, xy, 0x6331, &c, true, contextptr)) return gensizeerr(contextptr);
    doline(g, xy[0], xy[1], xy[2], xy[3], c);
    return 1;
  }
  CyrilleFnc(LINE_P);

  gen _RECT_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4]= {0, 0, 0x0fffffff,0x0fffffff}, c[2]= { 3, -1};
    if (!GraphicVerifInputs2(args, &g, xy, 0x66441, c, true, contextptr)) return gensizeerr(contextptr);
    if (c[1]==-1) c[1]= c[0];
    if (xy[0]>xy[2]) swap(xy[0], xy[2]);
    if (xy[1]>xy[3]) swap(xy[1], xy[3]);
    dorect(g, xy[0], xy[1], xy[2]-xy[0]+1, xy[3]-xy[1]+1, c[0], c[1]);
    return 1;
  }
  CyrilleFnc(RECT_P);

  gen _INVERT_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[4]= {0, 0, 0x0fffffff, 0x0fffffff };
    if (!GraphicVerifInputs2(args, &g, xy, 0x441, NULL, true, contextptr)) return gensizeerr(contextptr);
    doinvert(g, xy[0], xy[1], xy[2]-xy[0]+1, xy[3]-xy[1]+1);
    return 1;
  }
  CyrilleFnc(INVERT_P);

  gen _BLIT_P(const gen & args,GIAC_CONTEXT)
  {
    void *g[2]; int xy[8]= { 0, 0, 0xfffffff, 0xfffffff, 0, 0, 0xfffffff, 0xfffffff }, c= -1;
    if (!GraphicVerifInputs2(args, g, xy, 0x6442431, NULL, true, contextptr)) return gensizeerr(contextptr);
    xy[3]+= -xy[1]+1; if (xy[3]<0) xy[3]= -2-xy[3];
    xy[7]+= -xy[5]+1; if (xy[7]<0) xy[7]= -2-xy[7];
    doblit(g[0], xy[0], xy[1], xy[2]-xy[0]+1, xy[3], g[1], xy[4], xy[5], xy[6]-xy[4]+1, xy[7], c);
    return 1;
  }
  CyrilleFnc(BLIT_P);
  gen _TEXTOUT_P(const gen &args, GIAC_CONTEXT)
  {
    if (args.type!=_VECT) return gensizeerr(contextptr);
    int s= args.__VECTptr->v.size(); 
    if (s<3) return gensizeerr(contextptr);
    gen t= *args.__VECTptr->v.begin();
    gen v(*args._VECTptr); v._VECTptr->erase(v._VECTptr->begin());
    void *g; int xy[2]={0, 0}, c[4]= {0, 0, 1023, -1};
    //TEXTOUT("text", [G?], x, y, [font, [color, [width, [color]]]])
    if (!GraphicVerifInputs2(v, &g, xy, 0x6b6b31, c, true, contextptr)) return gensizeerr(contextptr);
    return gen(dotextout(&t, g, xy, c, contextptr));
  }
  CyrilleFnc(TEXTOUT_P);
  gen _PIXON_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[2], c= 0;
    if (!GraphicVerifInputs2(args, &g, xy, 0x631, &c, true, contextptr)) return gensizeerr(contextptr);
    dopixon(g, xy[0], xy[1], c);
    return 1;
  }
  CyrilleFnc(PIXON_P);
  gen _PIXOFF_P(const gen & args,GIAC_CONTEXT)
  {
    void *g; int xy[2];
    if (!GraphicVerifInputs2(args, &g, xy, 0x31, NULL, true, contextptr)) return gensizeerr(contextptr);
    dopixon(g, xy[0], xy[1], 3);
    return 1;
  }
  CyrilleFnc(PIXOFF_P);
  gen _DIMGROB_P(const gen &args,GIAC_CONTEXT)
  {
    void *g; int xy[2], c= 3;
    if (!GraphicVerifInputs2(args, &g, xy, 0x6c9, &c, false, contextptr)) return gensizeerr(contextptr);
    dodimgrob((void**)g, xy[0], xy[1], c, args.__VECTptr->v.end()[-1]);
    return 1;
  }
  CyrilleFnc(DIMGROB_P);
  gen _SUBGROB_P(const gen &args,GIAC_CONTEXT)
  {
    void *g[2]; int xy[4]= {0, 0, 0xfffffff, 0xfffffff};
    if (!GraphicVerifInputs2(args, g, xy, 0x9441, NULL, true, contextptr)) return gensizeerr(contextptr);
    dosubgrob(g, xy);
    return 1;
  }
  CyrilleFnc(SUBGROB_P);
  gen _ARC_P(const gen & args,GIAC_CONTEXT)
  {
    ///< draws a circle, or an arc between a1 and a2. full circle is 4096... 
    // if OldColor is !=-1 and indiate the background color of the screen BEFORE the arc is drawn, the arc is filled...
    //int Arc(int x, int y, int r, int color, int a1=0, int a2=4096, int OldColor=-1); 
    // ARC(x, y, r, [c, [a1, a2]])
    void *g; int xy[2], c[4]= { 0, 0, 0, 4096};
    if (!GraphicVerifInputs2(args, &g, xy, 0xaab731, c, true, contextptr)) return gensizeerr(contextptr);
    doarc(g, false, xy, c);
    return 1;
  }
  CyrilleFnc(ARC_P);
  gen _GROBW_P(const gen & args, GIAC_CONTEXT)
  {
    void *g; if (!GraphicVerifInputs(args, &g, NULL, 1, NULL, contextptr)) return gensizeerr(contextptr);
    return gen(dogrobw(g));
  }
  CyrilleFnc(GROBW_P);
  gen _GROBH_P(const gen & args, GIAC_CONTEXT)
  {
    void *g; if (!GraphicVerifInputs(args, &g, NULL, 1, NULL, contextptr)) return gensizeerr(contextptr);
    return gen(dogrobh(g, false));
  }
  CyrilleFnc(GROBH_P);
  gen _GROBW(const gen & args, GIAC_CONTEXT)
  {
    void *g; if (!GraphicVerifInputs(args, &g, NULL, 1, NULL, contextptr)) return gensizeerr(contextptr);
    int w= dogrobw(g);
    return w*getxrangeperpixel();
  }
  CyrilleFnc(GROBW);
  gen _GROBH(const gen & args, GIAC_CONTEXT)
  {
    void *g; if (!GraphicVerifInputs(args, &g, NULL, 1, NULL, contextptr)) return gensizeerr(contextptr);
    int h= dogrobh(g, true);
    return h*getyrangeperpixel();
  }
  CyrilleFnc(GROBH);

  CyrilleFnc(ISKEYDOWN);

#else
  gen _ARC(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=5)
      return gensizeerr(contextptr);
    vecteur & v =*args._VECTptr;
    return _cercle(gen(makevecteur(v[0]+cst_i*v[1],v[2],v[3],v[4]),_SEQ__VECT),contextptr);
  }
  gen _PIXON(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v =*args._VECTptr;
    return symb_pnt(v.front()+cst_i*v.back(),int(FL_BLACK),contextptr);
  }
  gen _PIXOFF(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v =*args._VECTptr;
    return symb_pnt(v.front()+cst_i*v.back(),int(FL_WHITE),contextptr);
  }
  gen _LINE(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=4)
      return _droite(args,contextptr);
    vecteur & v =*args._VECTptr;
    return _droite(gen(makevecteur(v[0]+cst_i*v[1],v[2]+cst_i*v[3]),_SEQ__VECT),contextptr);
  }
  static const char _LINE_s []="LINE";
  static define_unary_function_eval (__LINE,&giac::_LINE,_LINE_s);
  define_unary_function_ptr5( at_LINE ,alias_at_LINE,&__LINE,0,T_UNARY_OP_38);

  gen _RECT(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=4)
      return _droite(args,contextptr);
    vecteur & v =*args._VECTptr;
    gen a=v[0],b=v[1],c=v[2],d=v[3];
    if (is_greater(a,c,contextptr)) 
      swapgen(a,c);
    if (is_greater(b,d,contextptr)) 
      swapgen(b,d);
    gen e=a+b*cst_i,f=c+b*cst_i,g=a+d*cst_i,h=c+d*cst_i;
    gen res=pnt_attrib(gen(makevecteur(e,f,h,g,e),_GROUP__VECT),vecteur(1,int(FL_BLACK)),contextptr);
    return res;
  }
  static const char _RECT_s []="RECT";
  static define_unary_function_eval (__RECT,&giac::_RECT,_RECT_s);
  define_unary_function_ptr5( at_RECT ,alias_at_RECT,&__RECT,0,T_UNARY_OP_38);

#endif
  static const char _PIXON_s []="PIXON";
  static define_unary_function_eval (__PIXON,&giac::_PIXON,_PIXON_s);
  define_unary_function_ptr5( at_PIXON ,alias_at_PIXON,&__PIXON,0,T_UNARY_OP_38);
  static const char _PIXOFF_s []="PIXOFF";
  static define_unary_function_eval (__PIXOFF,&giac::_PIXOFF,_PIXOFF_s);
  define_unary_function_ptr5( at_PIXOFF ,alias_at_PIXOFF,&__PIXOFF,0,T_UNARY_OP_38);
  static const char _ARC_s []="ARC";
  static define_unary_function_eval (__ARC,&giac::_ARC,_ARC_s);
  define_unary_function_ptr5( at_ARC ,alias_at_ARC,&__ARC,0,T_UNARY_OP_38);

  static const char _PRINT_s []="PRINT";
  static define_unary_function_eval_quoted (__PRINT,&giac::_print,_PRINT_s);
  define_unary_function_ptr5( at_PRINT ,alias_at_PRINT,&__PRINT,0,T_UNARY_OP_38);

  static const char _SEC_s []="SEC";
  static define_unary_function_eval (__SEC,&giac::_sec,_SEC_s);
  define_unary_function_ptr5( at_SEC ,alias_at_SEC,&__SEC,0,T_UNARY_OP_38);

  static const char _CSC_s []="CSC";
  static define_unary_function_eval (__CSC,&giac::_csc,_CSC_s);
  define_unary_function_ptr5( at_CSC ,alias_at_CSC,&__CSC,0,T_UNARY_OP_38);

  static const char _COT_s []="COT";
  static define_unary_function_eval (__COT,&giac::_cot,_COT_s);
  define_unary_function_ptr5( at_COT ,alias_at_COT,&__COT,0,T_UNARY_OP_38);

  static const char _ASEC_s []="ASEC";
  static define_unary_function_eval (__ASEC,&giac::_asec,_ASEC_s);
  define_unary_function_ptr5( at_ASEC ,alias_at_ASEC,&__ASEC,0,T_UNARY_OP_38);

  static const char _ACSC_s []="ACSC";
  static define_unary_function_eval (__ACSC,&giac::_acsc,_ACSC_s);
  define_unary_function_ptr5( at_ACSC ,alias_at_ACSC,&__ACSC,0,T_UNARY_OP_38);

  static const char _ACOT_s []="ACOT";
  static define_unary_function_eval (__ACOT,&giac::_acot,_ACOT_s);
  define_unary_function_ptr5( at_ACOT ,alias_at_ACOT,&__ACOT,0,T_UNARY_OP_38);

  gen _Celsius2Fahrenheit(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return apply(g,_Celsius2Fahrenheit,contextptr);
    return (g*gen(9))/5+32;
  }
  static const char _Celsius2Fahrenheit_s []="Celsius2Fahrenheit";
  static define_unary_function_eval (__Celsius2Fahrenheit,&giac::_Celsius2Fahrenheit,_Celsius2Fahrenheit_s);
  define_unary_function_ptr5( at_Celsius2Fahrenheit ,alias_at_Celsius2Fahrenheit,&__Celsius2Fahrenheit,0,T_UNARY_OP_38);

  gen _Fahrenheit2Celsius(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return apply(g,_Fahrenheit2Celsius,contextptr);
    return (g-32)*gen(5)/9;
  }
  static const char _Fahrenheit2Celsius_s []="Fahrenheit2Celsius";
  static define_unary_function_eval (__Fahrenheit2Celsius,&giac::_Fahrenheit2Celsius,_Fahrenheit2Celsius_s);
  define_unary_function_ptr5( at_Fahrenheit2Celsius ,alias_at_Fahrenheit2Celsius,&__Fahrenheit2Celsius,0,T_UNARY_OP_38);

  // put here function names that are in lowercase in giac and should be printed uppercase
  // on HP
  static const char * const display_in_maj[] ={
    "ABS",
    "ACOS",
    "ACOSH",
    "ACOT",
    "ACSC",
    //    "ADDCOL",
    // "ADDROW",
    "ALOG",
    // "ARC",
    "ARG",
    "ASEC",
    "ASIN",
    "ASINH",
    "ATAN",
    "ATANH",
    "BREAK",
    "CEILING",
    "CHOOSE",
    "COLNORM",
    "COMB",
    // "CONCAT",
    "COND",
    "CONJ",
    "COS",
    "COSH",
    "COT",
    "CROSS",
    "CSC",
    // "DEGXRAD",
    "DELCOL",
    "DELROW",
    "DET",
    "DISP",
    "DOT",
    // "EIGENVAL",
    // "EIGENVV",
    "EXP",
    // "EXPM1",
    // "EXPORT",
    "FLOOR",
    "FNROOT",
    "FRAC",
    // "FREEZE",
    // "GETKEY",
    // "GF",
    //"HMSX",
    "IDENMAT",
    "IM",
    // "INPUT",
    // "INT",
    "INVERSE",
    // "ISOLATE",
    // "ITERATE",
    // "LINEAR?",
    "LN",
    // "LNP1",
    "LOG",
    // "LQ",
    // "LSQ",
    "LU",
    // "MAKELIST",
    // "MAKEMAT",
    // "MANT",
    "MAX",
    // "MAXREAL",
    "MIN",
    // "MINREAL",
    "MOD",
    // "MSGBOX",
    // "NTHROOT",
    "PERM",
    // "PIXOFF",
    // "PIXON",
    // "POLYCOEF",
    // "POLYEVAL",
    // "POLYFORM",
    // "POLYROOT",
    // "POS",
    "PRINT",
    "QR",
    // "QUAD",
    "QUOTE",
    // "RADXDEG",
    // "RANDMAT",
    // "RANDOM",
    "RANDSEED",
    "RANK",
    "RE",
    // "RECURSE",
    // "REDIM",
    // "REPLACE",
    "REVERSE",
    "ROUND",
    "ROWNORM",
    "RREF",
    // "SCALE",
    // "SCALEADD",
    // "SCHUR",
    "SEC",
    "SIGN",
    "SIN",
    "SINH",
    // "SIZE",
    "SORT",
    // "SPECNORM",
    // "SPECRAD",
    "SUB",
    // "SVD",
    // "SVL",
    // "SWAPCOL",
    "SWAPROW",
    "TAN",
    "TANH",
    "TAYLOR",
    "TRACE",
    "TRN",
    "TRUNCATE",
    "UTPC",
    "UTPF",
    "UTPN",
    "UTPT",
    // "VIEWS",
    "WAIT",
    // "XHMS",
    // "XPON",
  };

  static const int display_in_maj_size=sizeof(display_in_maj)/sizeof(const char *);

  // check if a lowercase commandname should be uppercased
  static char maj_converted[16];
  
  char * hp38_display_in_maj(const char * s){
    int l=strlen(s);
    if (l>15)
      return 0;
    maj_converted[l]=0;
    for (int i=0;i<l;++i){
      maj_converted[i]=toupper(s[i]);
    }
    int beg=0,end=display_in_maj_size,cur,test;
    // string index is always >= begin and < end
    for (;;){
      cur=(beg+end)/2;
      test=strcmp(maj_converted,display_in_maj[cur]);
      if (!test)
	return maj_converted;
      if (cur==beg){
	return 0;
      }
      if (test>0)
	beg=cur;
      else
	end=cur;
    }
    return 0;
  }

  gen _polar_complex(const gen & g,GIAC_CONTEXT){
    if (g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen angle=evalf(g._VECTptr->back(),1,contextptr);
    gen res= evalf(g._VECTptr->front(),1,contextptr);
#ifdef GIAC_HAS_STO_38
    if (angle.type==_FLOAT_ && res.type==_FLOAT_)
      {
	HP_Real a, r, s, c;
	fExpand(gen2HP(angle), &a); fExpand(gen2HP(res), &r);
	fisin(&a, &s, angle_radian(contextptr)?AMRad:AMDeg); ficos(&a, &c, angle_radian(contextptr)?AMRad:AMDeg); 
	fimul_L(&s, &r, &s); fimul_L(&c, &r, &c);
	HP_gen C= fUnExpand(&c), S= fUnExpand(&s);
	gen gC, gS; gC= HP2gen(C); gS= HP2gen(S);
	res= gC+gS*cst_i;
      } else {
      if (angle_radian(contextptr)==0) angle = angle * m_pi(contextptr)/180;
      res=res*exp(cst_i*angle,contextptr);
    }
#else
    res=res*(cos(angle,contextptr)+cst_i*sin(angle,contextptr));
#endif
    int * ptr = complex_display_ptr(res);
    if (ptr)
      *ptr=1;
    return res;
  }
#ifdef BCD
  static const char _polar_complex_s[]="\xe2\x88\xa1";
#else
  static const char _polar_complex_s[]=" polar_complex ";
#endif
  static define_unary_function_eval4 (__polar_complex,&giac::_polar_complex,_polar_complex_s,&printsommetasoperator,&texprintsommetasoperator); 
  define_unary_function_ptr5( at_polar_complex ,alias_at_polar_complex,&__polar_complex,0,T_MOD);  

#ifdef GIAC_HAS_STO_38
  gen aspen_ADigits(int i);
  gen aspen_AFormat(int i);
  gen aspen_AAngle(int i);
  gen aspen_AComplex(int i);
  gen aspen_ALanguage(int i);
#endif

  gen _HDigits(const gen & g0,GIAC_CONTEXT){
    gen g=g0;
    if (g.type==_VECT && g._VECTptr->empty()){
      g=-1;
    }
    else {
      if (g.type==_FLOAT_)
	g=get_int(g0._FLOAT_val);
      if (g.type==_DOUBLE_)
	g=_floor(g,contextptr);
      if (g.type!=_INT_)
	return gentypeerr(contextptr);
      if (g.val<0 || g.val>12)
	return gensizeerr(contextptr);
    }
#ifdef GIAC_HAS_STO_38
    return aspen_ADigits(g.val);
#else
    return _Digits(g,contextptr);
#endif
  }
  static const char _HDigits_s []="HDigits";
  static define_unary_function_eval2 (__HDigits,&_HDigits,_HDigits_s,&printasDigits);
  define_unary_function_ptr( at_HDigits ,alias_at_HDigits ,&__HDigits);

  gen _HFormat(const gen & g0,GIAC_CONTEXT){
    gen g=g0;
    if (g.type==_VECT && g._VECTptr->empty()){
      g=-1;
    }
    else {
      if (g.type==_FLOAT_)
	g=get_int(g0._FLOAT_val);
      if (g.type==_DOUBLE_)
	g=_floor(g,contextptr);
      if (g.type!=_INT_)
	return gentypeerr(contextptr);
      if (g.val<0 || g.val>4)
	return gensizeerr(contextptr);
    }
#ifdef GIAC_HAS_STO_38
    return aspen_AFormat(g.val);
#else
    return _scientific_format(g,contextptr);
#endif
  }
  static const char _HFormat_s []="HFormat";
  static define_unary_function_eval2 (__HFormat,&_HFormat,_HFormat_s,&printasDigits);
  define_unary_function_ptr( at_HFormat ,alias_at_HFormat ,&__HFormat);

  gen _HAngle(const gen & g0,GIAC_CONTEXT){
    gen g=g0;
    if (g.type==_VECT && g._VECTptr->empty()){
      g=-1;
    }
    else {
      if (g.type==_FLOAT_)
	g=get_int(g0._FLOAT_val);
      if (g.type==_DOUBLE_)
	g=_floor(g,contextptr);
      if (g.type!=_INT_)
	return gentypeerr(contextptr);
      if (g.val<1 || g.val>2)
	return gensizeerr(contextptr);
    }
#ifdef GIAC_HAS_STO_38
    return aspen_AAngle(g.val);
#else
    return _angle_radian(g-1,contextptr);
#endif
  }
  static const char _HAngle_s []="HAngle";
  static define_unary_function_eval2 (__HAngle,&_HAngle,_HAngle_s,&printasDigits);
  define_unary_function_ptr( at_HAngle ,alias_at_HAngle ,&__HAngle);

  gen _HComplex(const gen & g0,GIAC_CONTEXT){
    gen g=g0;
    if (g.type==_VECT && g._VECTptr->empty()){
      g=-1;
    }
    else {
      if (g.type==_FLOAT_)
	g=get_int(g0._FLOAT_val);
      if (g.type==_DOUBLE_)
	g=_floor(g,contextptr);
      if (g.type!=_INT_)
	return gentypeerr(contextptr);
      if (g.val<0 || g.val>1)
	return gensizeerr(contextptr);
    }
#ifdef GIAC_HAS_STO_38
    return aspen_AComplex(g.val);
#else
    return _complex_mode(g,contextptr);
#endif
  }
  static const char _HComplex_s []="HComplex";
  static define_unary_function_eval2 (__HComplex,&_HComplex,_HComplex_s,&printasDigits);
  define_unary_function_ptr( at_HComplex ,alias_at_HComplex ,&__HComplex);

  gen _HLanguage(const gen & g0,GIAC_CONTEXT){
    gen g=g0;
    if (g.type==_VECT && g._VECTptr->empty()){
      g=-1;
    }
    else {
      if (g.type==_FLOAT_)
	g=get_int(g0._FLOAT_val);
      if (g.type==_DOUBLE_)
	g=_floor(g,contextptr);
      if (g.type!=_INT_)
	return gentypeerr(contextptr);
      if (g.val<0 || g.val>2)
	return gensizeerr(contextptr);
    }
#ifdef GIAC_HAS_STO_38
    return aspen_ALanguage(g.val);
#else
    if (g==-1)
      return language(contextptr);
    language(g.val,contextptr);
    return g.val;
#endif
  }
  static const char _HLanguage_s []="HLanguage";
  static define_unary_function_eval2 (__HLanguage,&_HLanguage,_HLanguage_s,&printasDigits);
  define_unary_function_ptr( at_HLanguage ,alias_at_HLanguage ,&__HLanguage);

  gen _EDITMAT(const gen & args,GIAC_CONTEXT){
    if (args.type==_STRNG &&  args.subtype==-1) return  args;
#ifdef GIAC_HAS_STO_38
    if (args.type!=_IDNT)
      return gensizeerr(contextptr);
    const char * id = args._IDNTptr->id_name;
    if (id[0]!='M' || id[1]<'0' || id[1]>'9')
      return gensizeerr(contextptr);
    int i=id[1]-'0';
    EditMat(i?i-1:9);
#endif
    return eval(args,1,contextptr);
  }
  static const char _EDITMAT_s []="EDITMAT";
  static define_unary_function_eval_quoted (__EDITMAT,&giac::_EDITMAT,_EDITMAT_s);
  define_unary_function_ptr5( at_EDITMAT ,alias_at_EDITMAT,&__EDITMAT,_QUOTE_ARGUMENTS,T_UNARY_OP_38);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

