/* -*- mode:C++ ; compile-command: "g++-3.4 -DHAVE_CONFIG_H -I. -I..  -DIN_GIAC -g -c derive.cc" -*- */
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include <stdexcept>
#include <cmath>
#include "derive.h"
#include "usual.h"
#include "symbolic.h"
#include "unary.h"
#include "poly.h"
#include "sym2poly.h" // for equalposcomp
#include "tex.h"
#include "prog.h"
#include "intg.h"
#include "subst.h"
#include "plot.h"
#include "modpoly.h"
#include "moyal.h"
#include "alg_ext.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

   gen eval_before_diff(const gen & expr,const gen & variable,GIAC_CONTEXT){
    identificateur tmp_x_id(" eval_before_diff_x");
    gen tmp_x(tmp_x_id);
    gen res=subst(expr,variable,tmp_x,false,contextptr); // replace variable by a non affected identifier
    gen save_vx_var=vx_var;
    if (variable==vx_var) vx_var=tmp_x;
    int m=calc_mode(contextptr);
    calc_mode(0,contextptr);
    res=eval(res,1,contextptr); // eval res (all identifiers except X will be replaced by their values)
    res=eval(res,1,contextptr); // eval res (all identifiers except X will be replaced by their values)
    calc_mode(m,contextptr);
    vx_var=save_vx_var;
    res=subst(res,tmp_x,variable,false,contextptr);
    return res;
  }

  bool depend(const gen & g,const identificateur & i){
    if (g.type==_IDNT)
      return *g._IDNTptr==i;
    if (g.type==_SYMB)
      return depend(g._SYMBptr->feuille,i);
    if (g.type!=_VECT)
      return false;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      if (depend(*it,i))
	return true;
    }
    return false;
  }

  static gen derive_SYMB(const gen &g_orig,const identificateur & i,GIAC_CONTEXT){
    const symbolic & s = *g_orig._SYMBptr;
    // if s does not depend on i return 0
    if (!depend(g_orig,i))
      return zero;
    // rational operators are treated first for efficiency
    if (s.sommet==at_plus){
      if (s.feuille.type!=_VECT)
	return derive(s.feuille,i,contextptr);
      vecteur::const_iterator iti=s.feuille._VECTptr->begin(),itend=s.feuille._VECTptr->end();
      int taille=itend-iti;
      if (taille==2)
	return derive(*iti,i,contextptr)+derive(*(iti+1),i,contextptr);
      vecteur v;
      v.reserve(taille);
      gen e;
      for (;iti!=itend;++iti){
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e))
	  v.push_back(e);
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      return _plus(gen(v,_SEQ__VECT),contextptr); // symbolic(at_plus,v);
    }
    if (s.sommet==at_prod){
      if (s.feuille.type!=_VECT)
	return derive(s.feuille,i,contextptr);
      vecteur::const_iterator itbegin=s.feuille._VECTptr->begin(),itj,iti,itend=s.feuille._VECTptr->end();
      int taille=itend-itbegin;
      // does not work because of is_linear_wrt e.g. for cos(3*pi/4)
      // if (taille==2) return derive(*itbegin,i,contextptr)*(*(itbegin+1))+(*itbegin)*derive(*(itbegin+1),i,contextptr);
      vecteur v,w;
      v.reserve(taille);
      w.reserve(taille);
      gen e;
      for (iti=itbegin;iti!=itend;++iti){
	w.clear();
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e)){
	  for (itj=itbegin;itj!=iti;++itj)
	    w.push_back(*itj);
	  w.push_back(e);
	  ++itj;
	  for (;itj!=itend;++itj)
	    w.push_back(*itj);
	  v.push_back(_prod(w,contextptr));
	}
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      return symbolic(at_plus,gen(v,_SEQ__VECT));
    }
    if (s.sommet==at_neg)
      return -derive(s.feuille,i,contextptr);
    if (s.sommet==at_pow){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen base = s.feuille._VECTptr->front(),exponent=s.feuille._VECTptr->back();
      gen dbase=derive(base,i,contextptr),dexponent=derive(exponent,i,contextptr);
      // diff(base^exponent)=diff(exp(exponent*ln(base)))
      // =base^exponent*diff(exponent)*ln(base)+base^(exponent-1)*exponent*diff(base)
      if (is_zero(dexponent))
	return exponent*dbase*pow(base,exponent-1,contextptr);
      return dexponent*ln(base,contextptr)*s+exponent*dbase*pow(base,exponent-1,contextptr);
    }
    if (s.sommet==at_inv){
      if (s.feuille.is_symb_of_sommet(at_pow)){
	gen & f = s.feuille._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2)
	  return derive(symb_pow(f._VECTptr->front(),-f._VECTptr->back()),i,contextptr);
      }
      return rdiv(-derive(s.feuille,i,contextptr),pow(s.feuille,2),contextptr);
    }
    if (s.sommet==at_rootof)
      return gensizeerr(gettext("Derivative of rootof currently not handled"));
    if (s.sommet==at_UTPT){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_student(s.feuille,contextptr);
    }
    if (s.sommet==at_UTPC){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_chisquare(s.feuille,contextptr);
    }
    if (s.sommet==at_UTPF){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=3)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_snedecor(s.feuille,contextptr);
    }
    if (s.sommet==at_program){
      return gensizeerr(gettext("Expecting an expression, not a function"));
    }
    if (s.sommet==at_ln){ 
      if (s.feuille.is_symb_of_sommet(at_abs) )
	return rdiv(derive(s.feuille._SYMBptr->feuille,i,contextptr),s.feuille._SYMBptr->feuille,contextptr);
      if (s.feuille.is_symb_of_sommet(at_inv))
	return -derive(symbolic(at_ln,s.feuille._SYMBptr->feuille),i,contextptr);
      if (s.feuille.is_symb_of_sommet(at_prod)){
	gen res;
	const gen &f=s.feuille._SYMBptr->feuille;
	if (f.type==_VECT){
	  const_iterateur it=f._VECTptr->begin(),itend=f._VECTptr->end();
	  for (;it!=itend;++it)
	    res=res+derive(symbolic(at_ln,*it),i,contextptr);
	  return res;
	}
      }
    }
    if (s.feuille.type==_VECT){
      vecteur v=*s.feuille._VECTptr;
      int vs=v.size();
      if (vs>=3 && (s.sommet==at_ifte || s.sommet==at_when) ){
	for (int j=1;j<vs;++j){
	  gen & tmp=v[j];
	  tmp=derive(tmp,i,contextptr); // v[j]=derive(v[j],i,contextptr);
	  if (is_undef(tmp))
	    return tmp;
	}
	return symbolic(s.sommet,gen(v,s.feuille.subtype));
      }
      if (s.sommet==at_piecewise){
	for (int j=0;j<vs/2;++j){
	  gen & tmp=v[2*j+1];
	  tmp=derive(tmp,i,contextptr); // v[2*j+1]=derive(v[2*j+1],i,contextptr);
	}
	if (vs%2){
	  gen & tmp=v[vs-1];
	  tmp=derive(tmp,i,contextptr); // v[vs-1]=derive(v[vs-1],i,contextptr);
	}
	return symbolic(s.sommet,gen(v,s.feuille.subtype));
      }
      if (vs==2 && s.sommet==at_NTHROOT){
	gen base = v[1],exponent=inv(v[0],contextptr);
	gen dbase=derive(base,i,contextptr),dexponent=derive(exponent,i,contextptr);
	// diff(base^exponent)=diff(exp(exponent*ln(base)))
	// =base^exponent*diff(exponent)*ln(base)+base^(exponent-1)*exponent*diff(base)
	if (is_zero(dexponent))
	  return exponent*dbase*s/v[1];
	return dexponent*ln(base,contextptr)*s+exponent*dbase*s/v[1];
      }
      if (vs==3 && s.sommet==at_Beta){
	gen v0=v[0],v1=v[1],v2=v[2]; 
	if (!is_zero(derive(v0,i,contextptr)) || !is_zero(derive(v1,i,contextptr)) )
	  return gensizeerr("diff of incomplete beta with respect to non constant 1st or 2nd arg not implemented");
	// diff/v2(int_0^v2 t^(v0-1)*(1-t)^(v1-1) dt)
	gen tmp=pow(v2,v0-1,contextptr)*pow(1-v2,v1-1,contextptr)*derive(v2,i,contextptr);
	return tmp;
      }
      if ( (vs==2 || vs==3 && is_zero(v[2])) && (s.sommet==at_upper_incomplete_gamma || s.sommet==at_lower_incomplete_gamma || s.sommet==at_Gamma)){
	gen v0=v[0],v1=v[1]; 
	if (!is_zero(derive(v0,i,contextptr)))
	  return gensizeerr("diff of incomplete gamma with respect to non constant 1st arg not implemented");
	// diff(int_v1^inf exp(-t)*t^(v0-1) dt)
	gen tmp1=exp(-v1,contextptr)*pow(v1,v0-1,contextptr)*derive(v1,i,contextptr);
	return (s.sommet==at_lower_incomplete_gamma)?tmp1:-tmp1;
      }
      if (vs==3 && (s.sommet==at_lower_incomplete_gamma || s.sommet==at_lower_incomplete_gamma || s.sommet==at_Gamma)){
	return derive(symbolic(s.sommet,makesequence(v[0],v[1]))/symbolic(at_Gamma,v[0]),i,contextptr);
      }
    }
    // now look at other operators, first onearg operator
    if (s.sommet.ptr()->D){
      if (s.feuille.type!=_VECT)
	return (*s.sommet.ptr()->D)(1)(s.feuille,contextptr)*derive(s.feuille,i,contextptr);
      // multiargs operators
      int taille=s.feuille._VECTptr->size();
      vecteur v;
      v.reserve(taille);
      vecteur::const_iterator iti=s.feuille._VECTptr->begin(),itend=s.feuille._VECTptr->end();
      gen e;
      for (int j=1;iti!=itend;++iti,++j){
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e))
	  v.push_back(e*(*s.sommet.ptr()->D)(j)(s.feuille,contextptr));
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      return symbolic(at_plus,gen(v,_SEQ__VECT));
    }
    // integrate
    if (s.sommet==at_integrate){
      if (s.feuille.type!=_VECT)
	return s.feuille;
      vecteur v=*s.feuille._VECTptr;
      int nargs=v.size();
      if (nargs<=1)
	return s.feuille;
      if (nargs==2 && v[1].is_symb_of_sommet(at_equal)){
	gen v1f=v[1]._SYMBptr->feuille;
	if (v1f.type==_VECT && v1f._VECTptr->size()==2){
	  gen v1f1=v1f._VECTptr->front();
	  gen v1f2=v1f._VECTptr->back();
	  v[1]=v1f1;
	  if (v1f2.is_symb_of_sommet(at_interval)){
	    v.push_back(v1f2._SYMBptr->feuille._VECTptr->front());
	    v.push_back(v1f2._SYMBptr->feuille._VECTptr->back());
	    nargs=4;
	  }
	}
      }
      gen res,newint;
      if (v[1]==i)
	res=v[0];
      else {
	res=subst(v[0],v[1],i,false,contextptr);
	newint=derive(v[0],i,contextptr);	 
	if (nargs<4)
	  newint=integrate_gen(newint,v[1],contextptr);
      }
      if (nargs==2)
	return res+newint;
      if (nargs==3)
	return derive(v[2],i,contextptr)*subst(res,i,v[2],false,contextptr);
      if (nargs==4){
	gen a3=derive(v[3],i,contextptr);
	gen b3=is_zero(a3)?zero:limit(res,i,v[3],-1,contextptr);
	gen a2=derive(v[2],i,contextptr);
	gen b2=is_zero(a2)?zero:limit(res,i,v[2],1,contextptr);
	return a3*b3-a2*b2+_integrate(gen(makevecteur(newint,v[1],v[2],v[3]),_SEQ__VECT),contextptr);
      }
      return gensizeerr(contextptr);
    }
    // multi derivative and multi-indice derivatives
    if (s.sommet==at_derive){
      if (s.feuille.type!=_VECT)
	return symbolic(at_derive,gen(makevecteur(s.feuille,vx_var,2),_SEQ__VECT));
      if (s.feuille._VECTptr->size()==2){ // derive(f,x)
	gen othervar=(*s.feuille._VECTptr)[1];
	if (othervar.type!=_IDNT) return gensizeerr(gettext("derive.cc/derive_SYMB"));
	if (*othervar._IDNTptr==i){ // _FUNCnd derivative
	  vecteur res(*s.feuille._VECTptr);
	  symbolic sprime(s);
	  res.push_back(2);
	  return symbolic(at_derive,gen(res,_SEQ__VECT));
	}
	else {
	  vecteur var;
	  var.push_back(othervar);
	  var.push_back(i);
	  vecteur nderiv;
	  nderiv.push_back(1);
	  nderiv.push_back(1);
	  return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],var,nderiv),_SEQ__VECT));
	}
      }
      else { // derive(f,x,n)
	if (s.feuille._VECTptr->size()!=3)  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	gen othervar=(*s.feuille._VECTptr)[1];
	if (othervar.type==_IDNT){
	  if (*othervar._IDNTptr==i){ // n+1 derivative
	    vecteur vprime=(*s.feuille._VECTptr);
	    vprime[2] += 1;
	    return symbolic(s.sommet,gen(vprime,_SEQ__VECT));
	  }
	  else {
	    vecteur var;
	    var.push_back(othervar);
	    var.push_back(i);
	    vecteur nderiv;
	    nderiv.push_back((*s.feuille._VECTptr)[2]);
	    nderiv.push_back(1);
	    return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],var,nderiv),_SEQ__VECT));
	  }
	} // end if othervar.type==_IDNT
	else { // othervar.type must be _VECT
	  if (othervar.type!=_VECT)  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	  gen nder((*s.feuille._VECTptr)[2]);
	  if (nder.type!=_VECT ||
	      nder._VECTptr->size()!=othervar._VECTptr->size())  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	  vecteur nderiv(*nder._VECTptr);
	  int pos=equalposcomp(*othervar._VECTptr,i);
	  if (pos){
	    nderiv[pos-1]=nderiv[pos-1]+1;
	  }
	  else {
	    othervar._VECTptr->push_back(i);
	    nderiv.push_back(1);
	  }
	  return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],othervar,nderiv),_SEQ__VECT));
	}
      }
    }
    // no info about derivative
    return symbolic(at_derive,gen(makevecteur(s,i),_SEQ__VECT));
    i.dbgprint();
    s.dbgprint();
  }

  static gen derive_VECT(const vecteur & v,const identificateur & i,GIAC_CONTEXT){
    vecteur w;
    w.reserve(v.size());
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen tmp=derive(*it,i,contextptr);
      if (is_undef(tmp))
	return tmp;
      w.push_back(tmp);
    }
    return w;
  }

  gen derive(const gen & e,const identificateur & i,GIAC_CONTEXT){
    switch (e.type){
    case _INT_: case _DOUBLE_: case _ZINT: case _CPLX: case _MOD: case _REAL: case _USER: case _FLOAT_:
      return 0;
    case _IDNT:
      if (is_undef(e))
	return e;
      if (*e._IDNTptr==i)
	return 1;
      else
	return 0;
    case _SYMB:
      return derive_SYMB(e,i,contextptr);
    case _VECT:
      return derive_VECT(*e._VECTptr,i,contextptr);
    case _FRAC:
      return fraction(derive(e._FRACptr->num,i,contextptr)*e._FRACptr->den-(e._FRACptr->num)*derive(e._FRACptr->den,i,contextptr),e._FRACptr->den);
    default:
      return gentypeerr(contextptr);
    }
    return 0;
  }

  static gen _VECTderive(const gen & e,const vecteur & v,GIAC_CONTEXT){
    vecteur w;
    w.reserve(v.size());
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen tmp=derive(e,*it,contextptr);
      if (is_undef(tmp))
	return tmp;
      w.push_back(tmp);
    }
    return w;    
  }

  static gen derivesymb(const gen& e,const gen & var,GIAC_CONTEXT){
    identificateur x(" x");
    gen xx(x);
    gen f=subst(e,var,xx,false,contextptr);
    f=derive(f,x,contextptr);
    f=subst(f,xx,var,false,contextptr);
    return f;
  }
  gen derive(const gen & e,const gen & vars,GIAC_CONTEXT){
          //  cout << e << " " << vars << endl;
    switch (vars.type){
    case _IDNT:
      return derive(e,*vars._IDNTptr,contextptr);
    case _VECT:
      return _VECTderive(e,*vars._VECTptr,contextptr);
    case _SYMB:
      return derivesymb(e,vars,contextptr);
    default:
      return gensizeerr(contextptr);
    }
    return 0;
  }

  gen derive(const gen & e,const gen & vars,const gen & nderiv,GIAC_CONTEXT){
    if (nderiv.type==_INT_){
      int n=nderiv.val;
      gen ecopie(e),eprime(e);
      int j=1;
      for (;j<=n;++j){
	eprime=derive(ecopie,vars,contextptr);
	if (is_undef(eprime))
	  return eprime;
	if ( (eprime.type==_SYMB) && (eprime._SYMBptr->sommet==at_derive))
	  break;
	ecopie=eprime;
      }
      if (j==n+1)
	return eprime;
      return symbolic(at_derive,gen(makevecteur(ecopie,vars,n+1-j),_SEQ__VECT));
    }
    // multi-index derivation
    if (nderiv.type!=_VECT ||
	vars.type!=_VECT)  return gensizeerr(gettext("derive.cc/derive"));
    int s=nderiv._VECTptr->size();
    if (s!=signed(vars._VECTptr->size()))  return gensizeerr(gettext("derive.cc/derive"));
    int j=0;
    gen ecopie(e);
    for (;j<s;++j){
      ecopie=derive(ecopie,(*vars._VECTptr)[j],(*nderiv._VECTptr)[j],contextptr);
    }
    return ecopie;
  }  

  symbolic symb_derive(const gen & a,const gen & b){
    return symbolic(at_derive,gen(makevecteur(a,b),_SEQ__VECT));
  }

  gen symb_derive(const gen & a,const gen & b,const gen &c){
    if (is_zero(c))
      return a;
    if (is_one(c))
      return symb_derive(a,b);
    return symbolic(at_derive,gen(makevecteur(a,b,c),_SEQ__VECT));
  }

  // "unary" version
  gen _derive(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (calc_mode(contextptr)==1 && args.type!=_VECT)
      return _derive(makesequence(args,ggb_var(args)),contextptr);
    vecteur v;
    if (args.type==_VECT && args.subtype==_POLY1__VECT)
      return gen(derivative(*args._VECTptr),_POLY1__VECT);
    if (args.type==_VECT)
      v=plotpreprocess(gen(*args._VECTptr,_SEQ__VECT),contextptr);
    else
      v=plotpreprocess(args,contextptr);
    if (is_undef(v))
      return v;
    gen var,res;
    if (args.type!=_VECT && is_algebraic_program(v[0],var,res)){
      if (var.type==_VECT && var.subtype==_SEQ__VECT && var._VECTptr->size()==1)
	var=var._VECTptr->front();
      res=derive(res,var,contextptr);
      return symbolic(at_program,makesequence(var,0,res));
    }
    int s=v.size();
    if (s==2){
      if (v[1].type==_VECT && v[1].subtype==_SEQ__VECT){
	vecteur & w=*v[1]._VECTptr;
	int ss=w.size();
	gen res=v[0];
	for (int i=0;i<ss;++i)
	  res=ratnormal(derive(res,w[i],contextptr));
	return res;
      }
      if (args.type!=_VECT && v[0].type==_VECT && v[0].subtype==_POLY1__VECT)
	return gen(derivative(*v[0]._VECTptr),_POLY1__VECT);
      return derive(v[0],v[1],contextptr);
    }
    if (s==3 && (v[2].type==_INT_ || (v[2].type==_VECT && v[2].subtype!=_SEQ__VECT)) )
      return derive( v[0],v[1],v[2],contextptr);    
    if (s<3)
      return gensizeerr(contextptr);
    const_iterateur it=v.begin()+1,itend=v.end();
    res=v[0];
    for (;it!=itend;++it)
      res=ratnormal(_derive(gen(makevecteur(res,*it),_SEQ__VECT),contextptr));
    return res;
  }
  static const char _derive_s []="diff";
  static string printasderive(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT){
      if (need_parenthesis(feuille))
	return "("+feuille.print()+")'";
      return feuille.print()+"'";
    }
    return sommetstr+("("+feuille.print(contextptr)+")");
  }
  static string texprintasderive(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return gen2tex(feuille,contextptr)+"'";
    return "\\frac{\\partial \\left("+gen2tex(feuille._VECTptr->front(),contextptr)+"\\right)}{\\partial "+gen2tex(feuille._VECTptr->back(),contextptr)+"}";
  }
  static define_unary_function_eval4_quoted (__derive,&_derive,_derive_s,printasderive,texprintasderive);
  define_unary_function_ptr5( at_derive ,alias_at_derive,&__derive,_QUOTE_ARGUMENTS,true);

  gen _grad(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return _derive(args,contextptr);
  }
  static const char _grad_s []="grad";
  static define_unary_function_eval_quoted (__grad,&_grad,_grad_s);
  define_unary_function_ptr5( at_grad ,alias_at_grad,&__grad,_QUOTE_ARGUMENTS,true);

  // FIXME: This should not use any temporary identifier
  // Should define the identity operator and write again all rules here
  // NB: It requires all D operators for functions to be functions!
  gen _function_diff(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.is_symb_of_sommet(at_function_diff)){
      gen & f = g._SYMBptr->feuille;
      return symbolic(at_of,makesequence(gen(symbolic(at_composepow,makesequence(at_function_diff,2))),f));
    }
    if (g.is_symb_of_sommet(at_of)){
      gen & f = g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2){
	gen & f1=f._VECTptr->front();
	gen & f2=f._VECTptr->back();
	if (f1.is_symb_of_sommet(at_composepow)){
	  gen & f1f=f1._SYMBptr->feuille;
	  if (f1f.type==_VECT && f1f._VECTptr->size()==2 && f1f._VECTptr->front()==at_function_diff){
	    return symbolic(at_of,makesequence(gen(symbolic(at_composepow,makesequence(at_function_diff,f1f._VECTptr->back()+1))),f2));
	  }
	}
      }
    }
    identificateur _tmpi(" _x");
    gen _tmp(_tmpi);    
    gen dg(derive(g(_tmp,contextptr),_tmp,contextptr));
    if (lop(dg,at_derive).empty()){
      identificateur tmpi(" x");
      gen tmp(tmpi);
      gen res=symb_program(tmp,zero,quotesubst(dg,_tmp,tmp,contextptr),contextptr);
      return res;
    }
    return symbolic(at_function_diff,g);
  }
  static const char _function_diff_s []="function_diff";
  static define_unary_function_eval (__function_diff,&_function_diff,_function_diff_s);
  define_unary_function_ptr5( at_function_diff ,alias_at_function_diff,&__function_diff,0,true);

  static const char _fonction_derivee_s []="fonction_derivee";
  static define_unary_function_eval (__fonction_derivee,&_function_diff,_fonction_derivee_s);
  define_unary_function_ptr5( at_fonction_derivee ,alias_at_fonction_derivee,&__fonction_derivee,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
