// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -DHAVE_CONFIG_H -DIN_GIAC -g -c subst.cc" -*-
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
#include "subst.h"
#include "symbolic.h"
#include "sym2poly.h"
#include "unary.h"
#include "usual.h"
#include "derive.h"
#include "intg.h"
#include "prog.h"
#include "lin.h"
#include "solve.h"
#include "plot.h"
#include "modpoly.h"
#include "maple.h"
#include "ti89.h"
#include "alg_ext.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen checkanglemode(GIAC_CONTEXT){
    if (!angle_radian(contextptr)) 
      return gensizeerr(gettext("This function works only in radian mode"));
    return 0;
  }

  // bool quote_subst=false;
  /*
  static vector <const unary_function_ptr *> merge(const vector <const unary_function_ptr *>& v,const vector <const unary_function_ptr *> & w){
    vector <const unary_function_ptr *> res(v);
    vector <const unary_function_ptr *>::const_iterator it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      res.push_back(*it);
    }
    return res;
  }
  
  static vector <gen_op> merge(const vector <gen_op>& v,const vector <gen_op> & w){
    vector <gen_op> res(v);
    vector <gen_op>::const_iterator it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      res.push_back(*it);
    }
    return res;
  }
  */

  // sin, cos, tan in terms of tan(x/2)
  gen sin2tan2(const gen & e,GIAC_CONTEXT){
    gen a=symb_tan(rdiv(e,plus_two,contextptr));
    return rdiv(plus_two*a,pow(a,2)+1,contextptr);
  }

  gen cos2tan2(const gen & e,GIAC_CONTEXT){
    gen a=symb_tan(rdiv(e,plus_two,contextptr));
    return rdiv(1-pow(a,2),pow(a,2)+1,contextptr);
  }

  gen tan2tan2(const gen & e,GIAC_CONTEXT){
    gen a=symb_tan(rdiv(e,plus_two,contextptr));
    return rdiv(plus_two*a,1-pow(a,2),contextptr);
  }

  // hyperbolic trig to exp
  gen sinh2exp(const gen & e,GIAC_CONTEXT){
    gen a=exp(e,contextptr);
    return rdiv(a-inv(a,contextptr),plus_two,contextptr);
  }

  gen cosh2exp(const gen & e,GIAC_CONTEXT){
    gen a=exp(e,contextptr);
    return rdiv(a+inv(a,contextptr),plus_two,contextptr);
  }

  gen tanh2exp(const gen & e,GIAC_CONTEXT){
    gen a=pow(exp(e,contextptr),2);
    return rdiv(a-plus_one,a+plus_one,contextptr);
  }

  // trig to exp
  gen degtorad(const gen & g,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return g;
    return g*deg2rad_e;
  }
  gen radtodeg(const gen & g,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return g;
    return g*gen(180)/cst_pi;
  }
  gen sin2exp(const gen & e,GIAC_CONTEXT){
    gen a=exp(cst_i*degtorad(e,contextptr),contextptr);
    return rdiv(a-inv(a,contextptr),plus_two*cst_i,contextptr);
  }
  gen cos2exp(const gen & e,GIAC_CONTEXT){
    gen a=exp(cst_i*degtorad(e,contextptr),contextptr);
    return rdiv(a+inv(a,contextptr),plus_two,contextptr);
  }
  gen tan2exp(const gen & e,GIAC_CONTEXT){
    gen a=pow(exp(cst_i*degtorad(e,contextptr),contextptr),2);
    return rdiv(a-plus_one,cst_i*(a+plus_one),contextptr);
  }

  gen exp2sincos(const gen & e,GIAC_CONTEXT){
    gen a=re(e,contextptr),b=im(e,contextptr); 
    return exp(a,contextptr)*(cos(radtodeg(b,contextptr),contextptr)+cst_i*sin(radtodeg(b,contextptr),contextptr));
  }

  gen tantosincos(const gen & e,GIAC_CONTEXT){
    return rdiv(symb_sin(e),symb_cos(e),contextptr);
  }

  gen tantosincos2(const gen & e,GIAC_CONTEXT){
    gen e2=ratnormal(2*e);
    return rdiv(symb_sin(e2),(1+symb_cos(e2)),contextptr);
  }

  gen tantocossin2(const gen & e,GIAC_CONTEXT){
    gen e2=ratnormal(2*e);
    return rdiv((1-symb_cos(e2)),symb_sin(e2),contextptr);
  }

  gen asintoacos(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return cst_pi_over_2-acos(e,contextptr);
    else
      return 90-acos(e,contextptr);
  }

  gen acostoasin(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return cst_pi_over_2-asin(e,contextptr);
    else
      return 90-asin(e,0);
  }

  gen asintoatan(const gen & e,GIAC_CONTEXT){
    return symb_atan(rdiv(e,sqrt(1-pow(e,plus_two,contextptr),contextptr),contextptr));
  }

  gen atantoasin(const gen & e,GIAC_CONTEXT){
    return symb_asin(rdiv(e,sqrt(1+pow(e,plus_two,contextptr),contextptr),contextptr));
  }

  gen acostoatan(const gen & e,GIAC_CONTEXT){
    return cst_pi_over_2-asintoatan(e,contextptr);
  }

  gen atantoacos(const gen & e,GIAC_CONTEXT){
    return _asin2acos(atantoasin(e,contextptr),contextptr);
  }

  gen asin2ln(const gen & g_orig,GIAC_CONTEXT){
    gen g=degtorad(g_orig,contextptr);
    return cst_i*ln(g+sqrt(pow(g,2)-1,contextptr),contextptr)+cst_pi_over_2;
  }

  gen acos2ln(const gen & g_orig,GIAC_CONTEXT){
    gen g=degtorad(g_orig,contextptr);
    return -cst_i*ln(g+sqrt(pow(g,2)-1,contextptr),contextptr);
  }

  gen atan2ln(const gen & g_orig,GIAC_CONTEXT){
    gen g=degtorad(g_orig,contextptr);
    return rdiv(cst_i*ln(rdiv(cst_i+g,cst_i-g),contextptr),plus_two,contextptr);
  }

  // g=[base, exponant]
  gen trigcospow(const gen & g0,GIAC_CONTEXT){
    gen g(g0);
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    g.subtype=_SEQ__VECT;
    vecteur & v=*g._VECTptr;
    gen & base=v.front();
    gen & expo=v.back();
    if ( (base.type!=_SYMB) || (expo.type!=_INT_) )
      return symbolic(at_pow,g);
    gen tmpcos=symb_cos(base._SYMBptr->feuille);
    int ediv=expo.val/2,emod=expo.val%2;
    if (base._SYMBptr->sommet==at_sin)
      return pow(1-pow(tmpcos,2),ediv)*pow(base,emod);
    if (base._SYMBptr->sommet==at_tan){
      gen tmp=pow(tmpcos,2);
      tmp=rdiv(plus_one,tmp,contextptr)-plus_one; // 1/ cos^2 -1
      return pow(tmp,ediv)*pow(rdiv(symb_sin(base._SYMBptr->feuille),tmpcos,contextptr),emod);
    }
    return symbolic(at_pow,g);
  }

  gen trigsinpow(const gen & g0,GIAC_CONTEXT){
    gen g(g0);
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    g.subtype=_SEQ__VECT;
    vecteur & v=*g._VECTptr;
    gen & base=v.front();
    gen & expo=v.back();
    if ( (base.type!=_SYMB) || (expo.type!=_INT_) )
      return symbolic(at_pow,g);
    gen tmpsin=symb_sin(base._SYMBptr->feuille);
    int ediv=expo.val/2,emod=expo.val%2;
    if (base._SYMBptr->sommet==at_cos)
      return pow(1-pow(tmpsin,2),ediv)*pow(base,emod);
    if (base._SYMBptr->sommet==at_tan){
      gen tmp=pow(tmpsin,2);
      tmp=rdiv(tmp,plus_one-tmp,contextptr); // sin^2/ (1-sin^2)
      return pow(tmp,ediv)*pow(rdiv(tmpsin,symb_cos(base._SYMBptr->feuille),contextptr),emod);
    }
    return symbolic(at_pow,g);
  }

  gen trigtanpow(const gen & g0,GIAC_CONTEXT){
    gen g(g0);
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    g.subtype=_SEQ__VECT;
    vecteur & v=*g._VECTptr;
    gen & base=v.front();
    gen & expo=v.back();
    if ( (base.type!=_SYMB) || (expo.type!=_INT_) )
      return symbolic(at_pow,g);
    gen tmptan=symb_tan(base._SYMBptr->feuille);
    int ediv=expo.val/2,emod=expo.val%2;
    if (base._SYMBptr->sommet==at_cos)
      return pow(1+pow(tmptan,2),-ediv)*pow(base,emod);
    if (base._SYMBptr->sommet==at_sin){
      gen tmp=pow(tmptan,2);
      tmp=rdiv(tmp,plus_one+tmp,contextptr); // tan^2/ (1+tan^2)
      return pow(tmp,ediv)*pow(tmptan*symb_cos(base._SYMBptr->feuille),emod);
    }
    return symbolic(at_pow,g);
  }

  gen powtopowexpand(const gen & g0,GIAC_CONTEXT){
    gen g(g0);
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    g.subtype=_SEQ__VECT;
    vecteur & v(*g._VECTptr);
    if (v.size()!=2)
      return gensizeerr(contextptr);
    if (v[1].type!=_SYMB)
      return symbolic(at_pow,g);
    unary_function_ptr &u=v[1]._SYMBptr->sommet;
    if (u==at_neg)
      return inv(powtopowexpand(makevecteur(v[0],v[1]._SYMBptr->feuille),contextptr),contextptr);
    if ( (v[1]._SYMBptr->feuille.type!=_VECT) || ((u!=at_plus) && (u!=at_prod)) )
      return symbolic(at_pow,g);
    vecteur & w=*v[1]._SYMBptr->feuille._VECTptr;
    const_iterateur it=w.begin(),itend=w.end();
    if (u==at_plus){
      gen res(plus_one);
      for (;it!=itend;++it)
	res=res*powtopowexpand(makevecteur(v[0],*it),contextptr);
      return res;
    }
    if (u==at_prod){
      if (w.size()!=2)
	return symbolic(at_pow,g);
      if (w[0].type==_INT_)
	return pow(powtopowexpand(makevecteur(v[0],w[1]),contextptr),w[0],contextptr);
      if (w[1].type==_INT_)
	return pow(powtopowexpand(makevecteur(v[0],w[0]),contextptr),w[1],contextptr);
    }
    return symbolic(at_pow,g);
  }

  gen exptopower(const gen & g,GIAC_CONTEXT){
    vecteur l(lop(g,at_ln));
    if (l.size()!=1)
      return symbolic(at_exp,g);
    identificateur tmp(" x");
    gen gg=subst(g,l,vecteur(1,tmp),false,contextptr),a,b;
    if (!is_linear_wrt(gg,tmp,a,b,contextptr)) 
      return symbolic(at_exp,g);
    return exp(b,contextptr)*pow(l[0]._SYMBptr->feuille,a,contextptr);
  }

  // One substitution of an identifier
  static void subst_vecteur(const vecteur & v,const gen & i,const gen & newi,vecteur & w,bool quotesubst,GIAC_CONTEXT){
    if (&v==&w){
      vecteur::iterator it=w.begin(),itend=w.end();
      for (;it!=itend;++it)
	*it=subst(*it,i,newi,quotesubst,contextptr);
    }
    else {
      w.reserve(v.size());
      vecteur::const_iterator it=v.begin(),itend=v.end();
      for (;it!=itend;++it)
	w.push_back(subst(*it,i,newi,quotesubst,contextptr));
    }
  }

  vecteur subst(const vecteur & v,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT){
    vecteur w;
    subst_vecteur(v,i,newi,w,quotesubst,contextptr);
    return w;
  }

  static bool has_subst(const gen & e,const gen & i,const gen & newi,gen & newe,bool quotesubst,GIAC_CONTEXT);

  static bool has_subst_vect(const gen & e,const gen & i,const gen & newi,gen & newe,bool quotesubst,GIAC_CONTEXT){
    const vecteur & v =*e._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (has_subst(*it,i,newi,newe,quotesubst,contextptr))
	break;
    }
    if (it==itend)
      return false;
    gen res(new_ref_vecteur(*e._VECTptr),e.subtype);
    vecteur & w =*res._VECTptr;
    iterateur jt=w.begin()+(it-v.begin());
    *jt=newe;
    newe=res;
    for (++jt,++it;it!=itend;++it,++jt){
      if (!has_subst(*it,i,newi,*jt,quotesubst,contextptr))
	*jt=*it;
    }
    return true;
  }

  static gen subst_integrate(const gen & e,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT){
    vecteur v=*e._SYMBptr->feuille._VECTptr;
    int s=v.size();
    if ( (s>1) && (v[1]==i) ){
      vecteur l(*_lname(newi,contextptr)._VECTptr);
      if (!l.empty() && l.front()==cst_pi)
	l.erase(l.begin());
      if (l.empty())
	return gensizeerr(contextptr);
      v[1]=l.front();
      v=subst(v,i,newi,quotesubst,contextptr);
      v[0]=v[0]*derive(newi,l.front(),contextptr); 
      if (is_undef(v[0]))
	return v[0];
      if (s>2){
	identificateur t(" t");
	vecteur w=solve(newi-t,*l.front()._IDNTptr,1,contextptr); 
	if (w.empty())
	  return gensizeerr(gettext("Unable to solve"));
	if (s>3){
	  bool ordonne=is_greater(v[3],v[2],contextptr);
	  v[2]=limit(w.front(),t,v[2],ordonne?1:-1,contextptr);
	  v[3]=limit(w.front(),t,v[3],ordonne?-1:1,contextptr);
	}
	else
	  v[2]=limit(w.front(),t,v[2],0,contextptr);	  
      }
      return symbolic(at_integrate,gen(v,_SEQ__VECT));
    }
    return symbolic(at_integrate,gen(subst(v,i,newi,quotesubst,contextptr),_SEQ__VECT));
  }

  static gen subst_derive(const gen & e,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT){
    vecteur & v=*e._SYMBptr->feuille._VECTptr;
    int s=v.size();
    if ( (s==2) && (v[1]==i) ){
      vecteur l(*_lname(newi,contextptr)._VECTptr);
      if (l.empty())
	return gensizeerr(contextptr);
      v[1]=l.front();
      v=subst(v,i,newi,quotesubst,contextptr);
      return rdiv(symbolic(at_derive,v),derive(newi,l.front(),contextptr),contextptr); 
    }
    // Warning: ? return e for desolve (is_linear_diffeq)
    return symbolic(at_derive,subst(v,i,newi,quotesubst,contextptr));
    // return e;
  }

  static bool has_subst(const gen & e,const gen & i,const gen & newi,gen & newe,bool quotesubst,GIAC_CONTEXT){
    switch (e.type){
    case _INT_: case _ZINT: case _CPLX: case _DOUBLE_: case _REAL: case _STRNG: case _MOD: case _SPOL1: case _USER:
      return false;
    case _IDNT: case _FUNC:
      if (e==i){
	newe=newi;
	return true;
      }
      else
	return false;
    case _SYMB:
      if (e==i){
	newe=newi;
	return true;
      }
      if ( e._SYMBptr->sommet==at_pow && i.type==_SYMB && i._SYMBptr->sommet==at_exp && (*(e._SYMBptr->feuille._VECTptr))[1]*ln((*(e._SYMBptr->feuille._VECTptr))[0],contextptr) == i._SYMBptr->feuille ) {
	cerr << e << "=" << i << endl;
	newe=newi;
	return true;
      }
      if ( (e._SYMBptr->sommet==at_integrate || !strcmp(e._SYMBptr->sommet.ptr()->s,"integration") || !strcmp(e._SYMBptr->sommet.ptr()->s,"int")) && (e._SYMBptr->feuille.type==_VECT) && (i.type==_IDNT) ){
	newe=subst_integrate(e,i,newi,quotesubst,contextptr);
	return true;
      }
      if ( (e._SYMBptr->sommet==at_derive) && (e._SYMBptr->feuille.type==_VECT) &&(i.type==_IDNT) ){
	newe=subst_derive(e,i,newi,quotesubst,contextptr);
	return true;
      }
      if (has_subst(e._SYMBptr->feuille,i,newi,newe,quotesubst,contextptr)){
	if (quotesubst || e._SYMBptr->sommet.quoted())
	  newe=symbolic(e._SYMBptr->sommet,newe);
	else
	  newe=e._SYMBptr->sommet(newe,contextptr); 
	return true;
      }
      else
	return false;
    case _VECT:
      return has_subst_vect(e,i,newi,newe,quotesubst,contextptr);
    case _FRAC:
      if (e==i){
	newe=newi;
	return true;
      }
      newe=fraction(subst(e._FRACptr->num,i,newi,quotesubst,contextptr),subst(e._FRACptr->den,i,newi,quotesubst,contextptr));
      return true;
    default:
#ifndef NO_STDEXCEPT
      settypeerr(contextptr);
#endif
      return false;
    }
  }

  gen subst(const gen & e,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT){
    if (i.type==_VECT){
      if (newi.type!=_VECT || i._VECTptr->size()!=newi._VECTptr->size()){
#ifndef NO_STDEXCEPT
	setdimerr(contextptr);
#endif
	return e;
      }
      return subst(e,*i._VECTptr,*newi._VECTptr,quotesubst,contextptr);
    }
    gen res;
    if (has_subst(e,i,newi,res,quotesubst,contextptr))
      return res;
    else
      return e;
  }

  gen quotesubst(const gen & e,const gen & i,const gen & newi,GIAC_CONTEXT){
    return subst(e,i,newi,true,contextptr);
  }

#ifdef GIAC_MULTISUBST
  static int multisubst(const gen & e,vecteur & res,const gen & x,const vecteur & xval,GIAC_CONTEXT);

  static int multisubst_frac(const gen & e,vecteur & res,const gen & x,const vecteur & xval,GIAC_CONTEXT){
    vecteur resn,resd;
    const gen & N=e._FRACptr->num;
    const gen & D=e._FRACptr->den;
    int mn=multisubst_frac(N,resn,x,xval,contextptr);
    int md=multisubst_frac(D,resd,x,xval,contextptr);
    if (!mn && !md)
      return 0;
    int n=xval.size();
    res=xval;
    if (mn){
      if (md){
	for (int i=0;i<n;++i)
	  res[i]=resn[i]/resd[i];
      }
      else {
	for (int i=0;i<n;++i)
	  res[i]=resn[i]/D;
      }
    }
    else {
      for (int i=0;i<n;++i)
	res[i]=N/resd[i];
    }
    return 1;
  }

  static int multisubst_vect(const gen & e,vecteur & res,const gen & x,const vecteur & xval,GIAC_CONTEXT){
    const vecteur & v = *e._VECTptr;
    int s=v.size();
    std::vector<vecteur> vi(s) ;
    std::vector<int> mi(s);
    int m=0;
    for (int i=0;i<s;++i){
      if (mi[i]=multisubst(v[i],vi[i],x,xval,contextptr))
	m=1;
    }
    if (!m)
      return 0;
    // build res
    int n=xval.size();
    res=vecteur(n);
    iterateur it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
#ifdef SMARTPTR64
      *it=vecteur(s);
#else
      it->type=_VECT;
      it->__VECTptr=new_ref_vecteur(s);
#endif
    }
    for (int i=0;i<s;++i){
      // compute the i-th col of res
      if (mi[i]){
	vecteur & vii=vi[i];
	for (int j=0;j<n;j++){
	  vecteur & resv=*res[j]._VECTptr;
	  resv[i]=vii[j];
	}
      }
      else {
	for (int j=0;j<n;j++){
	  vecteur & resv=*res[j]._VECTptr;
	  resv[i]=v[i];
	}
      }
    }
    return 1;
  }

  static int multisubst_symb(const gen & e,vecteur & res,const gen & x,const vecteur & xval,GIAC_CONTEXT){
    int m=multisubst(e._SYMBptr->feuille,res,x,xval,contextptr);
    // FIXME integrate and derive
    if (!m)
      return 0;
    iterateur it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
      *it=e._SYMBptr->sommet.quoted()?symbolic(e._SYMBptr->sommet,*it):e._SYMBptr->sommet(*it,contextptr);
    }
    return 1;
  }

  // returns 1 if e evals to several values  
  // or 0 if they are all same = x
  static int multisubst(const gen & e,vecteur & res,const gen & x,const vecteur & xval,GIAC_CONTEXT){
    switch (e.type){
    case _INT_: case _ZINT: case _CPLX: case _DOUBLE_: case _REAL: case _STRNG: case _MOD: case _SPOL1: case _USER:
      return 0;
    case _IDNT: case _FUNC:
      if (e==x){
	res=xval;
	return 1;
      }
      else
	return 0;
    case _SYMB:
      if (e==x){
	res=xval;
	return 1;
      }
      return multisubst_symb(e,res,x,xval,contextptr);
    case _VECT:
      return multisubst_vect(e,res,x,xval,contextptr);
    case _FRAC:
      if (e==x){
	res=xval;
	return 1;
      }
      return multisubst_frac(e,res,x,xval,contextptr);
    default:
      return gentypeerr(contextptr);
    }
    return 0;
  }

  static vecteur multisubst(const gen & g,const gen & x,const vecteur & xval,GIAC_CONTEXT){
    int s=xval.size();
    vecteur res;
    if (multisubst(g,res,x,xval,contextptr))
      return res;
    else
      return vecteur(s,g);
  }

  static gen _multisubst(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=3)
      return gendimerr(contextptr);
    const vecteur & v=*args._VECTptr;
    if (v[2].type!=_VECT)
      return gensizeerr(contextptr);
    if (v[2]._VECTptr->empty())
      return v[2];
    return multisubst(v[0],v[1],*v[2]._VECTptr,contextptr);
  }
  static const char _multisubst_s []="multisubst";
  static define_unary_function_eval (__multisubst,&giac::_multisubst,_multisubst_s);
  define_unary_function_ptr5( at_multisubst ,alias_at_multisubst,&__multisubst,0,true);
#endif // GIAC_MULTISUBST

  sparse_poly1 subst(const sparse_poly1 & v,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT){
    sparse_poly1 p;
    sparse_poly1::const_iterator it=v.begin(),itend=v.end();
    p.reserve(itend-it);
    gen e;
    for (;it!=itend;++it){
      e=recursive_normal(subst(it->coeff,i,newi,quotesubst,contextptr),contextptr); 
      if (!is_zero(e))
	p.push_back(monome(e,it->exponent));
    }
    return p;
  }

  vecteur sortsubst(const vecteur & v,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT){
    if (i.empty())
      return v;
    if (v==i)
      return newi;
    vecteur w;
    w.reserve(v.size());
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      w.push_back(sortsubst(*it,i,newi,quotesubst,contextptr));
    return w;
  }

  // used for sorting
  static bool first_sort(const gen & a,const gen & b){
    return islesscomplexthanf(a[0],b[0]); // FIXME GIAC_CONTEXT
  }

  // g known to be in interval
  static int findpos(const_iterateur it,const_iterateur itend,const gen & g){
    const gen & itg=*it;
    if (g==itg)
      return 1;
    int n=itend-it;
    if (n<2)
      return 0;
    n /= 2;
    const_iterateur itmid=it+n;
    const gen & itgmid = *itmid;
    if (islesscomplexthanf(g,itgmid))
      return findpos(it,itmid,g);
    else {
      int f=findpos(itmid,itend,g);
      return f?n+f:0;
    }
  }

  int findpos(const vecteur & v,const gen & g){
    const_iterateur it=v.begin(),itend=v.end();
    if (it==itend)
      return 0;
    if (g==*it)
      return 1;
    if (g==*(itend-1))
      return itend-it;
    if (islesscomplexthanf(g,*it) || islesscomplexthanf(*(itend-1),g))
      return 0;
    return findpos(it,itend,g);
  }

  // Multiple substitutions
  static void sort2(vecteur & i,vecteur & newi){
    int is=i.size();
    if (i.size()<2)
      return;
    // set same size, required for mrv substition in series.cc
    for (int j=newi.size();j<is;++j)
      newi.push_back(i[j]);
    matrice atrier=mtran(makevecteur(i,newi));
    sort(atrier.begin(),atrier.end(),first_sort);
    atrier=mtran(atrier);
    i=*atrier[0]._VECTptr;
    newi=*atrier[1]._VECTptr;
  }

  vecteur subst(const vecteur & v,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT){
    vecteur sorti(i),sortnewi(newi);
    sort2(sorti,sortnewi);
    return sortsubst(v,sorti,sortnewi,quotesubst,contextptr);
  }

  gen sortsubst(const gen & e,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT){
    if (i.empty())
      return e;
    int pos;
    switch (e.type){
    case _INT_: case _ZINT: case _DOUBLE_: case _CPLX: case _REAL:
      return e;
    case _IDNT:
      pos=findpos(i,e);
      if (pos)
	return newi[pos-1];
      else
	return e;
    case _SYMB:
      pos=findpos(i,e);
      if (pos)
	return newi[pos-1];
      if (!quotesubst && abs_calc_mode(contextptr)!=38 && e._SYMBptr->sommet==at_pow){ 
	pos=findpos(i,exp((*(e._SYMBptr->feuille._VECTptr))[1]*ln((*(e._SYMBptr->feuille._VECTptr))[0],contextptr),contextptr)  );
	if (pos)
	  return newi[pos-1];
      }
      if (e._SYMBptr->feuille.type==_VECT){
	gen ef(sortsubst(*e._SYMBptr->feuille._VECTptr,i,newi,quotesubst,contextptr));
	ef.subtype=e._SYMBptr->feuille.subtype;
	if (quotesubst || e._SYMBptr->sommet.quoted() || e._SYMBptr->sommet==at_pow)
	  return symbolic(e._SYMBptr->sommet,ef);
	else
	  return e._SYMBptr->sommet(ef,contextptr);
      }
      else {
	gen ef=sortsubst(e._SYMBptr->feuille,i,newi,quotesubst,contextptr);
	if (quotesubst || e._SYMBptr->sommet.quoted())
	  return symbolic(e._SYMBptr->sommet,ef);
	else
	  return e._SYMBptr->sommet(ef,contextptr);
      }
    case _VECT:
      return gen(sortsubst(*e._VECTptr,i,newi,quotesubst,contextptr),e.subtype);
    case _FRAC:
      pos=findpos(i,e);
      if (pos)
	return newi[pos-1];
      return fraction(sortsubst(e._FRACptr->num,i,newi,quotesubst,contextptr),sortsubst(e._FRACptr->den,i,newi,quotesubst,contextptr));
    default:
      pos=findpos(i,e);
      if (pos)
	return newi[pos-1];
      return e;
    }
  }

  gen subst(const gen & e,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT){
    if (i.empty()) return e;
    vecteur sorti(i),sortnewi(newi);
    sort2(sorti,sortnewi);
    return sortsubst(e,sorti,sortnewi,quotesubst,contextptr);
  }

  gen subst(const gen & e,const vector<const unary_function_ptr *> & v,const vector< gen_op_context > & w,bool quotesubst,GIAC_CONTEXT){
    if (v.empty())
      return e;
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(subst(*it,v,w,quotesubst,contextptr));
      return gen(res,e.subtype);
    }
    if (e.type!=_SYMB)
      return e;
    gen arg=subst(e._SYMBptr->feuille,v,w,quotesubst,contextptr);
    int n=equalposcomp(v,&e._SYMBptr->sommet);
    if (!n){
      if (quotesubst){
	gen res=symbolic(e._SYMBptr->sommet,arg);
	res.subtype=e.subtype;
	return res;
      }
      return e._SYMBptr->sommet(arg,contextptr); 
    }
    gen tmp=w[n-1](arg,contextptr);
    return tmp;
  }

  // Quick check if e contains some ptr of v
  bool has_op(const gen & e,const unary_function_ptr * v){
    if (e.type==_SYMB){
      if (equalposcomp(v,&e._SYMBptr->sommet))
	return true;
      return has_op(e._SYMBptr->feuille,v);
    }
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it){
	if (has_op(*it,v))
	  return true;
      }
    }
    return false;
  }

  // Quick check if e contains v
  bool has_op(const gen & e,const unary_function_ptr & u){
    if (e.type==_SYMB){
      if (u==e._SYMBptr->sommet)
	return true;
      return has_op(e._SYMBptr->feuille,u);
    }
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it){
	if (has_op(*it,u))
	  return true;
      }
    }
    return false;
  }

  gen subst(const gen & e,const unary_function_ptr * v,const gen_op_context * w,bool quotesubst,GIAC_CONTEXT){
    if (*v==0 || !has_op(e,v))
      return e;
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(subst(*it,v,w,quotesubst,contextptr));
      return gen(res,e.subtype);
    }
    if (e.type!=_SYMB)
      return e;
    gen arg=subst(e._SYMBptr->feuille,v,w,quotesubst,contextptr);
    int n=equalposcomp(v,e._SYMBptr->sommet);
    if (!n){
      if (quotesubst){
	gen res=symbolic(e._SYMBptr->sommet,arg);
	res.subtype=e.subtype;
	return res;
      }
      return e._SYMBptr->sommet(arg,contextptr); 
    }
    gen tmp=w[n-1](arg,contextptr);
    return tmp;
  }

  gen subst(const gen & e,const vector<const unary_function_ptr *> & v,const vector< gen_op > & w,bool quotesubst,GIAC_CONTEXT){
    if (v.empty())
      return e;
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(subst(*it,v,w,quotesubst,contextptr));
      return gen(res,e.subtype);
    }
    if (e.type!=_SYMB)
      return e;
    gen arg=subst(e._SYMBptr->feuille,v,w,quotesubst,contextptr);
    int n=equalposcomp(v,&e._SYMBptr->sommet);
    if (!n){
      if (quotesubst){
	gen res=symbolic(e._SYMBptr->sommet,arg);
	res.subtype=e.subtype;
	return res;
      }
      return e._SYMBptr->sommet(arg,contextptr); 
    }
    gen tmp=w[n-1](arg);
    return tmp;
  }

  gen halftan(const gen & e,GIAC_CONTEXT){
    return subst(e,sincostan_tab,halftan_tab,false,contextptr);
  }

  gen _halftan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_halftan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_halftan,contextptr);
    return halftan(args,contextptr);
  }
  static const char _halftan_s []="halftan";
  static define_unary_function_eval (__halftan,&giac::_halftan,_halftan_s);
  define_unary_function_ptr5( at_halftan ,alias_at_halftan,&__halftan,0,true);

  // sin(x)=-cos(x+pi/2)
  static gen shift_sin(const gen & x,GIAC_CONTEXT){
    return -symb_cos(ratnormal(x+cst_pi_over_2));
  }
  // cos(x)=sin(x+pi/2)
  static gen shift_cos(const gen & x,GIAC_CONTEXT){
    return symb_sin(ratnormal(x+cst_pi_over_2));
  }
  // tan(x+pi/2)=-1/tan(x)
  static gen shift_tan(const gen & x,GIAC_CONTEXT){
    return minus_one/symb_tan(ratnormal(x+cst_pi_over_2));
  }
  const gen_op_context shift_phase_v_tab[]={shift_sin,shift_cos,shift_tan};
  gen shift_phase(const gen & e,GIAC_CONTEXT){
    return subst(e,sincostan_tab,shift_phase_v_tab,false,contextptr);
  }

  gen _shift_phase(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_shift_phase,contextptr);
    return shift_phase(args,contextptr);
  }
  static const char _shift_phase_s []="shift_phase";
  static define_unary_function_eval (__shift_phase,&giac::_shift_phase,_shift_phase_s);
  define_unary_function_ptr5( at_shift_phase ,alias_at_shift_phase,&__shift_phase,0,true);

  gen inv_test_exp(const gen & e,GIAC_CONTEXT){
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_exp))
      return symbolic(at_exp,-e._SYMBptr->feuille);
    return inv(e,contextptr);
  }

  gen rewrite_hyper(const gen & e,GIAC_CONTEXT){
    /*
    vector<const unary_function_ptr *> vu;
    vu.push_back(at_sinh); 
    vu.push_back(at_cosh); 
    vu.push_back(at_tanh); 
    vu.push_back(at_inv);
    vector <gen_op_context> vv(hyp2exp_tab,hyp2exp_tab+3);
    vv.push_back(inv_test_exp);
    return subst(e,vu,vv,false,contextptr);
    */
    return subst(e,sinhcoshtanhinv_tab,hypinv2exp_tab,false,contextptr);
  }

  gen hyp2exp(const gen & e,GIAC_CONTEXT){
    return subst(e,sinhcoshtanh_tab,hyp2exp_tab,false,contextptr);
  }
  gen _hyp2exp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_hyp2exp(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_hyp2exp,contextptr);
    return hyp2exp(args,contextptr);
  }
  static const char _hyp2exp_s []="hyp2exp";
  static define_unary_function_eval (__hyp2exp,&giac::_hyp2exp,_hyp2exp_s);
  define_unary_function_ptr5( at_hyp2exp ,alias_at_hyp2exp,&__hyp2exp,0,true);

  gen sincos(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr)){
      gen tmp=subst(e,tan_tab,tan2sincos_tab,true,contextptr);
      tmp=_pow2exp(tmp,contextptr);
      tmp=subst(tmp,exp_tab,exp2sincos_tab,false,contextptr);
      tmp=_exp2pow(tmp,contextptr);
      return tmp;
    }
    else
      return e;
  }
  gen _sincos(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_sincos(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_sincos,contextptr);
    return sincos(args,contextptr);
  }
  static const char _sincos_s []="sincos";
  static define_unary_function_eval (__sincos,&giac::_sincos,_sincos_s);
  define_unary_function_ptr5( at_sincos ,alias_at_sincos,&__sincos,0,true);

  gen trig2exp(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return subst(e,sincostan_tab,trig2exp_tab,false,contextptr);
    else
      return e;
  }
  gen _trig2exp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_trig2exp(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_trig2exp,contextptr);
    return trig2exp(args,contextptr);
  }
  static const char _trig2exp_s []="trig2exp";
  static define_unary_function_eval (__trig2exp,&giac::_trig2exp,_trig2exp_s);
  define_unary_function_ptr5( at_trig2exp ,alias_at_trig2exp,&__trig2exp,0,true);

  gen halftan_hyp2exp(const gen & e,GIAC_CONTEXT){
    return subst(e,sincostansinhcoshtanh_tab,halftan_hyp2exp_tab,false,contextptr);
  }

  gen _halftan_hyp2exp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_halftan_hyp2exp(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_halftan_hyp2exp,contextptr);
    return halftan_hyp2exp(args,contextptr);
  }
  static const char _halftan_hyp2exp_s []="halftan_hyp2exp";
  static define_unary_function_eval (__halftan_hyp2exp,&giac::_halftan_hyp2exp,_halftan_hyp2exp_s);
  define_unary_function_ptr5( at_halftan_hyp2exp ,alias_at_halftan_hyp2exp,&__halftan_hyp2exp,0,true);

  gen asin2acos(const gen & e,GIAC_CONTEXT){
    return subst(e,asin_tab,asin2acos_tab,false,contextptr);
  }
  gen _asin2acos(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_asin2acos(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_asin2acos,contextptr);
    return asin2acos(args,contextptr);
  }
  static const char _asin2acos_s []="asin2acos";
  static define_unary_function_eval (__asin2acos,&giac::_asin2acos,_asin2acos_s);
  define_unary_function_ptr5( at_asin2acos ,alias_at_asin2acos,&__asin2acos,0,true);

  gen asin2atan(const gen & e,GIAC_CONTEXT){
    return subst(e,asin_tab,asin2atan_tab,false,contextptr);
  }
  gen _asin2atan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_asin2atan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_asin2atan,contextptr);
    return asin2atan(args,contextptr);
  }
  static const char _asin2atan_s []="asin2atan";
  static define_unary_function_eval (__asin2atan,&giac::_asin2atan,_asin2atan_s);
  define_unary_function_ptr5( at_asin2atan ,alias_at_asin2atan,&__asin2atan,0,true);

  gen acos2asin(const gen & e,GIAC_CONTEXT){
    return subst(e,acos_tab,acos2asin_tab,false,contextptr);
  }
  gen _acos2asin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_acos2asin(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,acos2asin,contextptr);
    return acos2asin(args,contextptr);
  }
  static const char _acos2asin_s []="acos2asin";
  static define_unary_function_eval (__acos2asin,&giac::_acos2asin,_acos2asin_s);
  define_unary_function_ptr5( at_acos2asin ,alias_at_acos2asin,&__acos2asin,0,true);

  gen acos2atan(const gen & e,GIAC_CONTEXT){
    return subst(e,acos_tab,acos2atan_tab,false,contextptr);
  }
  gen _acos2atan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_acos2atan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_acos2atan,contextptr);
    return acos2atan(args,contextptr);
  }
  static const char _acos2atan_s []="acos2atan";
  static define_unary_function_eval (__acos2atan,&giac::_acos2atan,_acos2atan_s);
  define_unary_function_ptr5( at_acos2atan ,alias_at_acos2atan,&__acos2atan,0,true);

  gen atan2asin(const gen & e,GIAC_CONTEXT){
    return subst(e,atan_tab,atan2asin_tab,false,contextptr);
  }
  gen _atan2asin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_atan2asin(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_atan2asin,contextptr);
    return atan2asin(args,contextptr);
  }
  static const char _atan2asin_s []="atan2asin";
  static define_unary_function_eval (__atan2asin,&giac::_atan2asin,_atan2asin_s);
  define_unary_function_ptr5( at_atan2asin ,alias_at_atan2asin,&__atan2asin,0,true);

  gen atan2acos(const gen & e,GIAC_CONTEXT){
    return subst(e,atan_tab,atan2acos_tab,false,contextptr);
  }
  gen _atan2acos(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_atan2acos(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_atan2acos,contextptr);
    return atan2acos(args,contextptr);
  }
  static const char _atan2acos_s []="atan2acos";
  static define_unary_function_eval (__atan2acos,&giac::_atan2acos,_atan2acos_s);
  define_unary_function_ptr5( at_atan2acos ,alias_at_atan2acos,&__atan2acos,0,true);

  gen atrig2ln(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return subst(e,asinacosatan_tab,atrig2ln_tab,false,contextptr);
    else
      return e;
  }
  gen _atrig2ln(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_atrig2ln(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_atrig2ln,contextptr);
    return atrig2ln(args,contextptr);
  }
  static const char _atrig2ln_s []="atrig2ln";
  static define_unary_function_eval (__atrig2ln,&giac::_atrig2ln,_atrig2ln_s);
  define_unary_function_ptr5( at_atrig2ln ,alias_at_atrig2ln,&__atrig2ln,0,true);

  bool is_rational(const gen & g){
    if (is_integer(g))
      return true;
    if (g.type!=_FRAC)
      return false;
    return is_integer(g._FRACptr->num) && is_integer(g._FRACptr->den);
  }
  // if g is a symbolic depending linearly and rationnaly on a ln, 
  // factors out this term before taking the exp
  static gen rewrite_strong_exp(const gen & g_orig,GIAC_CONTEXT){
    if (g_orig.type!=_SYMB)
      return exp(g_orig,contextptr);
    gen g(g_orig),res(plus_one);
    vecteur v(lop(lvar(g),at_ln));
    v.insert(v.begin(),cst_pi);
    int s=v.size();
    identificateur t(" t");
    for (int i=0;i<s;++i){
      gen gt=quotesubst(g,v[i],t,contextptr);
      gen dg=normal(subst(derive(gt,t,contextptr),t,zero,false,contextptr),contextptr); 
      if (is_undef(dg))
	return dg;
      gen gdg=g-dg*v[i];
      if (!i)
	dg=dg/cst_i;
      if (is_zero(dg))
	continue;
      if (!is_rational(dg) && !is_assumed_integer(dg,contextptr))
	continue;
      g=gdg;
      if (!i)
	res=res*exp(cst_i*cst_pi*dg,contextptr);
      else {
	if (dg.is_symb_of_sommet(at_neg))
	  res=res/pow(v[i]._SYMBptr->feuille,dg._SYMBptr->feuille,contextptr);
	else
	  res=res*pow(v[i]._SYMBptr->feuille,dg,contextptr);
      }
    }
    return res*exp(normal(g,contextptr),contextptr);
  }

  // After extracting the cst coeff of g
  // if g is a linear combination of the components of wrt, 
  // this will return the coeffs of the linear comb
  // otherwise it will add g at the end of wrt and return [0...0 1]
  // It assumes that g and the coeff of wrt are multivariate rat. fractions
  vecteur as_linear_combination(const gen & g,vecteur & wrt,GIAC_CONTEXT){
    vecteur v(wrt);
    v.push_back(g);
    gen d;
    lcmdeno(v,d,contextptr);
    gen cl=v.back();
    int n=wrt.size();
    vecteur res(n),mat;
    if (cl.type!=_POLY){ // search for a non poly in wrt
      gen gg;
      int i;
      for (i=0;i<n;++i){
	if (v[i].type!=_POLY){
	  gg=rdiv(g,wrt[i],contextptr);
	  if (gg.type==_INT_)
	    break;
	  if ( (gg.type==_FRAC) && (gg._FRACptr->num.type==_INT_) && (gg._FRACptr->den.type==_INT_) )
	    break;
	}
      }
      if (i==n){
	wrt.push_back(g);
	res.push_back(plus_one);
      }
      else
	res[i]=gg;
      return res;
    }
    // we are now back to express cl as linear comb with integer coeff
    // but here in multivariate polynomials
    // we build now a linear system and solve it
    polynome & p =*cl._POLYptr;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      const index_m & i=it->index;
      vecteur tmp(n+1);
      tmp.back()=it->value;
      for (int j=0,k;j<n;++j){
	gen & gg=v[j];
	if (gg.type==_POLY){ 
	  if ( (k=gg._POLYptr->position(i)) >=0 )
	    tmp[j]=gg._POLYptr->coord[k].value;
	}
	else {
	  if (i.is_zero())
	    tmp[j]=gg;
	}
      }
      mat.push_back(tmp);
    }
    // add monomials that are not inside the last polynomial
    polynome fait(p);
    for (int j=0,k;j<n;++j){
      gen & gg=v[j];
      if (gg.type!=_POLY)
	continue;
      polynome & pj =*gg._POLYptr;
      vector< monomial<gen> >::const_iterator it=pj.coord.begin(),itend=pj.coord.end();
      for (;it!=itend;++it){
	const index_m & i=it->index;
	k=fait.position(i);
	if (k>=0 ) // this monomial is already in the system
	  continue;
	fait=fait+monomial<gen>(plus_one,i);
	// make a new line in the system for this new monomial
	vecteur tmp(n+1);
	tmp.back()=zero;
	for (int J=0,K;J<n;++J){
	  gen & GG=v[J];
	  if (GG.type==_POLY){ 
	    if ( (K=GG._POLYptr->position(i)) >=0 )
	      tmp[J]=GG._POLYptr->coord[K].value;
	  }
	  else {
	    if (i.is_zero())
	      tmp[J]=GG;
	  }
	}
	mat.push_back(tmp);
      }
    }
    // take re and im since coeffs are reals
    // FIXME: should have a better algo for rational coeffs
    mat=mergevecteur(*re(mat,contextptr)._VECTptr,*im(mat,contextptr)._VECTptr);
    // find solutions in kernel of mat
    matrice noyau=mker(mat,contextptr);
    // try each row of noyau that has a non-zero last coeff
    // since the first row coeff is -1 all coeff must be rationals
    int ns=noyau.size();
    for (int i=0;i<ns;++i){
      vecteur & w=*noyau[i]._VECTptr;
      if (is_zero(w.back()))
	continue;
      gen gg;
      lcmdeno(w,gg,contextptr);
      // check that all coeff are integers
      const_iterateur jt=w.begin(),jtend=w.end();
      for (;jt!=jtend;++jt){
	if (jt->type!=_INT_)
	  break;
      }
      if (jt!=jtend)
	continue;
      // additional check: w.v=0
      gen check=dotvecteur(w,v);
      if (!is_zero(check))
	continue;
      gg=w.back();
      w.pop_back();
      res=divvecteur(w,-gg);
      return res;
    }
    // no solution found
    wrt.push_back(g);
    res.push_back(plus_one);
    return res;
  }

  bool is_unit(const gen & g){
    if (g.type==_INT_)
      return (g.val==1) || (g.val==-1);
    if (g.type==_ZINT)
      return (g==1) || (g==-1);
    if (g.type==_CPLX)
      return (g._CPLXptr->type==_INT_) && (g._CPLXptr->val==0) && ( (g._CPLXptr+1)->type==_INT_) && ( ( (g._CPLXptr+1)->val==1) || ( (g._CPLXptr+1)->val==-1) );
    if (g.type==_POLY)
      return (g._POLYptr->coord.size()==1) && (g._POLYptr->coord.front().index.is_zero()) && is_unit(g._POLYptr->coord.front().value);
    return false;
  }

  bool is_algebraic_extension(const gen & g){
    if (g.type==_EXT)
      return true;
    if (g.type==_POLY && g._POLYptr->dim==0 && !g._POLYptr->coord.empty())
      return is_algebraic_extension(g._POLYptr->coord.front().value);
    return false;
  }

  // a is an extension, search if can be expressed as a product of powers of ext
  static bool ext_relation(const gen & a0,const vecteur & ext,const vecteur & vars,vecteur & coeffs,GIAC_CONTEXT){
    if (ext.empty())
      return false;
    gen a=r2e(a0,vars,contextptr);
    vecteur w=*r2e(ext,vars,contextptr)._VECTptr;
    w.push_back(a);
    vecteur l(lidnt(w));
    if (!l.empty()){ // check for random values of the variables
      vecteur lval=vranm(l.size(),0,0); 
      // should be improved to take care of assumptions and avoid bad eval points
      w=subst(w,l,lval,false,contextptr);
    }
    int s=w.size();
    matrice test(s);
    gen precision=1<<30; // 2^30
    for (int i=0;i<s;i++){
      vecteur tmp(s+2);
      tmp[i]=1;
      tmp[s]=_floor(precision*evalf(re(ln(w[i],contextptr),contextptr),1,contextptr),contextptr);
      tmp[s+1]=_floor(precision*evalf(im(ln(w[i],contextptr),contextptr),1,contextptr),contextptr);
      test[i]=tmp;
    }
    matrice S,A,L,O;
    S=lll(test,L,O,A,contextptr);
    if (is_undef(S))
      return false;
    // if the first line of A has a small norm then it is the line of coeff
    coeffs=*A[0]._VECTptr;
    if (is_greater(linfnorm(coeffs,contextptr),20,contextptr))
      return false;
    // found a probable relation, check it
    if (int(coeffs.size())!=s+2)
      return false;
    coeffs.pop_back();
    coeffs.pop_back();
    gen num(1),den(1);
    for (int i=0;i<s;++i){
      if (is_positive(coeffs[i],contextptr))
	num=num*pow((i==s-1?a0:ext[i]),coeffs[i],contextptr);
      else
	den=den*pow((i==s-1?a0:ext[i]),-coeffs[i],contextptr);
    }
    return is_zero(num-den);
  }

  // Complete a list of prime together factors primeargs such that
  // a is a product of these factors
  static void decompose(const gen &a_orig,vecteur & primeargs,vecteur & extargs,int begin,const vecteur & vars,GIAC_CONTEXT){
    if (is_unit(a_orig))
      return;
    if (is_algebraic_extension(a_orig)){
      vecteur coeffs;
      if (!ext_relation(a_orig,extargs,vars,coeffs,contextptr))
	extargs.push_back(a_orig);
      return;
    }
    if (primeargs.empty()){
      primeargs.push_back(a_orig);
      return;
    }
    gen a(a_orig);
    for (int i=begin;i<signed(primeargs.size());){
      gen g=simplify3(a,primeargs[i]);
      if (is_unit(g)){ // prime together -> go to the next element of primeargs
	++i;
	continue;
      }
      if (is_unit(primeargs[i])){
	primeargs[i]=g;
	continue;
      }
      decompose(g,primeargs,extargs,i,vars,contextptr);
    }
    if (!is_unit(a))
      primeargs.push_back(a);
  }

  static gen branch_evalf(const gen & g,GIAC_CONTEXT){
    if (is_undef(g)) 
      return g;
    vecteur v(*_lname(evalf(g,1,contextptr),contextptr)._VECTptr);
    gen gg(g);
    int s=v.size();
    for (int i=0;i<s;++i){
      vecteur w;
      gen point=0;
      int direction=1;
      find_range(v[i],w,contextptr);
      if (w.size()==1 && w.front().type==_VECT){
	w=*w.front()._VECTptr;
	if (w.size()==2){
	  gen l=w.front(),m=w.back();
	  if (!is_inf(m)){
	    point=m;
	    direction=-1;
	  }
	  else {
	    if (!is_inf(l))
	      point=l;
	  }
	}
      }
      *logptr(contextptr) << gettext("Simplification assuming ") << v[i] << " near " << point << (direction==1?"+":"-") << endl;
#ifdef NO_STDEXCEPT
      gg=limit(gg,*v[i]._IDNTptr,point,direction,contextptr);
      if (is_undef(gg))
	gg=0;
#else
      try {
	gg=limit(gg,*v[i]._IDNTptr,point,direction,contextptr);
      } catch (std::runtime_error &){
	gg=0;
      }
#endif
    }
    return evalf(gg,1,contextptr);
  }
  static gen expanded_ln(const gen & a_orig,const vecteur & primeargs,const vecteur & extargs,const vecteur & lnprimeargs,const vecteur & lnextargs,const vecteur & vars,GIAC_CONTEXT){
    gen res,gg,a=a_orig;
    int k;
    if (is_algebraic_extension(a)){
      vecteur coeffs;
      if (ext_relation(a,extargs,vars,coeffs,contextptr)){
	int s=coeffs.size()-1;
	for (int i=0;i<s;++i){
	  res = res + coeffs[i]*lnextargs[i];
	}
	res=-res/coeffs[s];
      }
      else
	res=ln(r2e(a,vars,contextptr),contextptr);
      return res;
    }
    int p=primeargs.size();
    for (int j=0;j<p;++j){
      for (k=0;;++k){
	gen gp=primeargs[j];
	gg=simplify3(a,gp);
	if (is_unit(gg))
	  break;
      }
      if (k)
	res=res+gen(k)*lnprimeargs[j];
      res=res+ln(r2e(gg,vars,contextptr),contextptr);
    }
    res=res+ln(r2e(a,vars,contextptr),contextptr);
    gg=re(branch_evalf(rdiv(ln(r2e(a_orig,vars,contextptr),contextptr)-res,cst_pi_over_2*cst_i,contextptr),contextptr),contextptr); 
    if (gg.type==_DOUBLE_)
      res=res+cst_pi_over_2*cst_i*gen(int(floor(gg._DOUBLE_val+0.5)));
    return res;
  }

  polynome gen2poly(const gen & g,int s){
    if (g.type==_POLY)
      return *g._POLYptr;
    else
      return polynome(g,s);
  }
  // If g has exponential variables, factors out powers of
  // these exponential variables and return the ln of g simplified
  // plus the arg of the exponential vars
  static gen simplifylnarg(const gen & g,GIAC_CONTEXT){
    gen res;
    vecteur l(lvar(g));
    int s=l.size();
    fraction f(sym2r(g,l,contextptr));
    gen nf,df;
    fxnd(f,nf,df);
    polynome n(gen2poly(nf,s)),d(gen2poly(df,s));
    // for every exp variable inside l, look if n or d has a valuation
    // with respect to this degree
    const_iterateur it=l.begin(),itend=l.end();
    for (int i=0;it!=itend;++it,++i){
      if (!it->is_symb_of_sommet(at_exp))
	continue;
      index_t shift_index(s);
      int nv=n.valuation(i);
      shift_index[i]=-nv;
      n=n.shift(shift_index);
      int dv=d.valuation(i);
      shift_index[i]=-dv;
      d=d.shift(shift_index);
      int v=nv-dv;
      res=res+v*it->_SYMBptr->feuille;
    }
    res=res+ln(r2sym(n,l,contextptr)/r2sym(d,l,contextptr),contextptr);
    return res;
  }
  static gen simplifylnexp(const gen & g,GIAC_CONTEXT){
    vecteur l(lop(g,at_ln));
    int s=l.size();
    if (!s)
      return g;
    vecteur lsub;
    for (int i=0;i<s;++i)
      lsub.push_back(simplifylnarg(l[i]._SYMBptr->feuille,contextptr));
    return quotesubst(g,l,lsub,contextptr);
  }

  static void rlvar(const gen &e,vecteur & res,bool alg){
    vecteur v;
    if (alg){
      vecteur tmp=alg_lvar(e);
      // make one list from the matrix
      const_iterateur it=tmp.begin(),itend=tmp.end();
      for (;it!=itend;++it){
	if (it->type==_VECT){
	  const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	  for (;jt!=jtend;++jt){
	    if (!equalposcomp(v,*jt))
	      v.push_back(*jt);
	  }
	}
      }
    }
    else
      v=lvar(e);
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!equalposcomp(res,*it)){
	// recursive call
	res.push_back(*it);
	if (it->type==_SYMB) {
	  rlvar(it->_SYMBptr->feuille,res,alg);
	  if (it->_SYMBptr->sommet==at_pow) 
	    rlvar(symbolic(at_ln,(*(it->_SYMBptr->feuille._VECTptr))[0]),res,alg);
	}
      }
    }
  }

  // recursive (alg) list of var
  vecteur rlvar(const gen &e,bool alg){
    vecteur res;
    rlvar(e,res,alg);
    sort(res.begin(),res.end(),symb_size_less);
    return res;
  }


  gen _pow2exp(const gen & e,GIAC_CONTEXT){
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it)
	v.push_back(_pow2exp(*it,contextptr));
      return gen(v,e.subtype);
    }
    if (e.type!=_SYMB)
      return e;
    gen var,res;
    if (is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,_pow2exp(res,contextptr)));
    if ( e._SYMBptr->sommet==at_pow && e._SYMBptr->feuille.type==_VECT && e._SYMBptr->feuille._VECTptr->size()==2){ 
      vecteur & v=*e._SYMBptr->feuille._VECTptr;
      if (v[1].type!=_INT_ && v[1].type!=_FRAC){
	gen tmp=-v[0];
	gen tmp1=_pow2exp(v[1],contextptr);
	if (is_strictly_positive(tmp,contextptr))
	  return exp(tmp1*_pow2exp(ln(tmp,contextptr),contextptr),contextptr)*symb_exp(v[1]*cst_i*cst_pi);
	else
	  return exp(tmp1*_pow2exp(ln(v[0],contextptr),contextptr),contextptr);
      }
    }
    return e._SYMBptr->sommet(_pow2exp(e._SYMBptr->feuille,contextptr),contextptr); 
  }
  static const char _pow2exp_s []="pow2exp";
  static define_unary_function_eval (__pow2exp,&_pow2exp,_pow2exp_s);
  define_unary_function_ptr5( at_pow2exp ,alias_at_pow2exp,&__pow2exp,0,true);

  gen pow2expln(const gen & e,const identificateur & x,GIAC_CONTEXT){
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it)
	v.push_back(pow2expln(*it,x,contextptr));
      return v;
    }
    if (e.type!=_SYMB)
      return e;
    if (e._SYMBptr->feuille.type==_VECT){
      vecteur & v=*e._SYMBptr->feuille._VECTptr;
      if ((e._SYMBptr->sommet==at_pow)  && ( contains(v[1],x) ||(v[1].type!=_INT_ && contains(v[0],x) ) ) )
	return symb_exp(pow2expln(v[1],x,contextptr)*symb_ln(pow2expln(v[0],x,contextptr)));
    }
    return e._SYMBptr->sommet(pow2expln(e._SYMBptr->feuille,x,contextptr),contextptr); 
  }

  gen pow2expln(const gen & e,GIAC_CONTEXT){
    if (e.type==_VECT)
      return apply(e,pow2expln,contextptr);
    if (e.type!=_SYMB)
      return e;
    if (e._SYMBptr->feuille.type==_VECT){
      vecteur & v=*e._SYMBptr->feuille._VECTptr;
      if (e._SYMBptr->sommet==at_pow  && v[1].type!=_INT_ && !(v[1].type==_FRAC && is_integer(v[0])))
	return symb_exp(pow2expln(v[1],contextptr)*symb_ln(pow2expln(v[0],contextptr)));
    }
    return e._SYMBptr->sommet(pow2expln(e._SYMBptr->feuille,contextptr),contextptr); 
  }

  gen simplifyfactorial(const gen & g,GIAC_CONTEXT){
    vecteur l(lop(g,at_factorial));
    int s=l.size();
    if (s<2)
      return g;
    // look if difference of arguments in l are integers
    vecteur newl(s);
    // newl will contain couples of arg of factorials and integers shift
    newl[0]=makevecteur(l[0]._SYMBptr->feuille,zero);
    for (int i=1;i<s;++i){
      // look in newl for an arg of factorial with integer difference
      gen current=l[i]._SYMBptr->feuille;
      newl[i]=makevecteur(current,zero);
      for (int j=0;j<i;++j){
	if (!is_zero(newl[j][1]))
	  continue;
	gen base=newl[j][0];
	gen difference=current-base;
	difference=simplify(difference,contextptr); // recursive call
	if (difference.type!=_INT_)
	  continue;
	int k=difference.val;
	if (k>=0){
	  newl[i]=makevecteur(base,difference);
	  break;
	}
	// current is the new factorial basis, change base in newl
	newl[i]=makevecteur(current,zero);
	for (k=0;k<i;++k){
	  vecteur & n=*newl[k]._VECTptr;
	  if (n[0]==base){
	    n[0]=current;
	    n[1]=n[1]-difference;
	  }
	}
	break;
      }
    }
    // now replace in newl the couple by a factorial*a product
    for (int i=0;i<s;++i){
      gen base=newl[i][0];
      int shift=newl[i][1].val;
      gen product=plus_one;
      for (int j=shift;j>0;--j){
	product=product*(base+j);
      }
      newl[i]=product*symbolic(at_factorial,base);
    }
    return subst(g,l,newl,false,contextptr);
  }

  gen simplifypsi(const gen & g,GIAC_CONTEXT){
    vecteur l(lop(g,at_Psi));
    int s=l.size();
    if (s<2)
      return g;
    // look if difference of arguments in l are integers
    vecteur newl(s);
    // newl will contain couples of arg of Psi and integers shift
    newl[0]=makevecteur(l[0]._SYMBptr->feuille,zero);
    for (int i=1;i<s;++i){
      // look in newl for an arg of Psi with integer difference
      gen current=l[i]._SYMBptr->feuille;
      newl[i]=makevecteur(current,zero);
      for (int j=0;j<i;++j){
	if (!is_zero(newl[j][1]))
	  continue;
	gen base=newl[j][0];
	gen difference=current-base;
	difference=simplify(difference,contextptr); // recursive call
	if (difference.type==_VECT && difference._VECTptr->size()==2 && difference._VECTptr->back()==0)
	  difference=difference._VECTptr->front();
	if (difference.type!=_INT_)
	  continue;
	int k=difference.val;
	if (k>=0){
	  newl[i]=makevecteur(base,difference);
	  break;
	}
	// current is the new Psi basis, change base in newl
	newl[i]=makevecteur(current,zero);
	for (k=0;k<i;++k){
	  vecteur & n=*newl[k]._VECTptr;
	  if (n[0]==base){
	    n[0]=current;
	    n[1]=n[1]-difference;
	  }
	}
	break;
      }
    }
    // now replace in newl the couple by a factorial*a product
    for (int i=0;i<s;++i){
      gen base=newl[i][0];
      gen expo=-1; gen base0=base;
      if (base.type==_VECT && base._VECTptr->size()==2){
	expo=-1-base._VECTptr->back();
	base0=base._VECTptr->front();
      }
      int shift=newl[i][1].val;
      gen somme=0;
      for (int j=shift-1;j>=0;--j){
	somme += pow(base0+j,expo,contextptr);
      }
      newl[i]=somme+symbolic(at_Psi,base);
    }
    return subst(g,l,newl,false,contextptr);
  }

  gen gammatofactorial(const gen & x,GIAC_CONTEXT){
    if (x.is_symb_of_sommet(at_plus) && x._SYMBptr->feuille.type==_VECT){
      vecteur v = *x._SYMBptr->feuille._VECTptr;
      if (v.size()==2 && v.back()==plus_one)
	return symbolic(at_factorial,v.front());
      if (v.size()>2 && v.back()==plus_one){
	v.pop_back();
	return symbolic(at_factorial,symbolic(at_plus,gen(v,_SEQ__VECT)));
      }
    }
    return symbolic(at_factorial,x-1);
  }

  gen gamma2factorial(const gen & g,GIAC_CONTEXT){
    return subst(g,gamma_tab,gamma2factorial_tab,false,contextptr);
  }

  gen factorialtogamma(const gen & g,GIAC_CONTEXT){
    return symbolic(at_Gamma,g+1);
  }

  gen factorial2gamma(const gen & g,GIAC_CONTEXT){
    return subst(g,factorial_tab,factorial2gamma_tab,false,contextptr);
  }

  gen tsimplify_common(const gen & e,GIAC_CONTEXT){
    gen g=pow2expln(e,contextptr);
    g=gamma2factorial(g,contextptr);
    g=simplifyfactorial(g,contextptr);
    g=simplifypsi(g,contextptr);
    // analyse of args of ln
    g=simplifylnexp(g,contextptr);
    vecteur l(lop(g,at_ln));
    int s=l.size();
    if (s>1){
      vecteur argln(s);
      for (int i=0;i<s;++i)
	argln[i]=tsimplify(l[i]._SYMBptr->feuille,contextptr);
      // check for common factors between args
      // now we make a vector of prime together values and rewrite
      // each arg as a product of these values + a multiple of 2*pi*i
      // arg <--> [ powers multiple_2*pi ]
      // Initialization
      vecteur vars(alg_lvar(argln)); 
      // FIXME alg_lvar for alg ext but requires decompose to work!
      for (int i=0;i<s;++i)
	argln[i]=e2r(argln[i],vars,contextptr);
      // extensions must be treated separately
      gen num,den;
      vecteur primeargs,extargs;
      for (int i=0;i<s;++i){
	// check for common factors between argln[i] and primeargs
	fxnd(argln[i],num,den);
	decompose(num,primeargs,extargs,0,vars,contextptr);
	decompose(den,primeargs,extargs,0,vars,contextptr);
      }
      // now rewrite ln[argln[i]] as a sum of ln[primeargs[]]
      // int p=primeargs.size();
      vecteur lnprimeargs(*apply(r2e(primeargs,vars,contextptr),at_ln,contextptr)._VECTptr);
      vecteur lnextargs(*apply(r2e(extargs,vars,contextptr),at_ln,contextptr)._VECTptr);
      vecteur newln(s);
      for (int i=0;i<s;++i){
	fxnd(argln[i],num,den);	
	newln[i]=expanded_ln(num,primeargs,extargs,lnprimeargs,lnextargs,vars,contextptr)-expanded_ln(den,primeargs,extargs,lnprimeargs,lnextargs,vars,contextptr);
	gen gg=evalf_double(re(branch_evalf(rdiv(l[i]-newln[i],cst_two_pi*cst_i,contextptr),contextptr),contextptr),0,contextptr); 
	if (gg.type==_DOUBLE_)
	  newln[i]=newln[i]+cst_two_pi*cst_i*gen(int(floor(gg._DOUBLE_val+0.5)));
       }
      g=subst(g,l,newln,false,contextptr);
    }
    // analyse of arguments of exp:
    l=lop(g,at_exp);
    s=l.size();
    if (!s)
      return recursive_normal(g,contextptr); 
    // recursively simplify inside exp
    vecteur newl(s); // vector of args of the exponential
    for (int i=0;i<s;++i)
      newl[i]=tsimplify(l[i]._SYMBptr->feuille,contextptr);
    // check for linear relations with rational coefficients between args
    // add i*pi and ln to the linear relations checking
    // First convert everything to multivariate fractions
    vecteur vars(1,cst_pi);
    lvar(newl,vars);
    vecteur ln_vars(lop(newl,at_ln));
    vecteur independant(*e2r(ln_vars,vars,contextptr)._VECTptr);
    int n_ln=independant.size();
    independant.push_back(e2r(cst_i*cst_pi,vars,contextptr));
    matrice m;
    for (int i=0;i<s;++i){
      m.push_back(as_linear_combination(e2r(newl[i],vars,contextptr),independant,contextptr));
    }
    // we have a matrix of rational numbers and a vector "independant"
    // so that newl[i]=i-th line of the matrix dot independant
    // For each column of the matrix we take the lcm of the denominators
    // and multiply the column by the lcm, dividing the corresponding
    // coeff of independant
    // then we use exp[r2e(independant,vars)]^[i-th line] for exp[newl[i]]
    // we do the substitution l by exp[newl] in g
    // and we return normal(g)
    // First make m a rectangular array
    int c=m.back()._VECTptr->size(),r=m.size();
    for (int i=0;i<r;++i){
      int ms=m[i]._VECTptr->size();
      if (ms<c)
	m[i]=mergevecteur(*m[i]._VECTptr,vecteur(c-ms,zero));
    }
    // lcm
    matrice mt=mtran(m);
    gen lcmg;
    for (int i=0;i<c;++i){
      lcmdeno(*mt[i]._VECTptr,lcmg,contextptr);
      // check for arg of the form ln()/deno
      if (i<n_ln)
	independant[i]=pow(ln_vars[i]._SYMBptr->feuille,inv(lcmg,contextptr),contextptr);
      else
	independant[i]=rewrite_strong_exp(r2e(rdiv(independant[i],lcmg,contextptr),vars,contextptr),contextptr);
    }
    m=mtran(mt);
    // compute exp[newl]
    for (int i=0;i<s;++i){
      vecteur & ligne=*m[i]._VECTptr;
      gen res(plus_one);
      for (int j=0;j<c;++j){
	res=res*pow(independant[j],ligne[j],contextptr);
      }
      newl[i]=res;
    }
    g=subst(g,l,newl,false,contextptr);
    g=normal(g,contextptr); 
    return g;// ratnormal(g);
  }
  gen tsimplify(const gen & e,GIAC_CONTEXT){
    // replace trig/inv trig expressions with exp/ln
    gen g=trig2exp(e,contextptr);
    g=atrig2ln(g,contextptr);
    return tsimplify_common(g,contextptr);
  }
  gen tsimplify_noexpln(const gen & e,int s1,int s2,GIAC_CONTEXT){
    gen g=e;
    if (s1>1)
      g=trig2exp(e,contextptr);
    if (s2>1)
      g=atrig2ln(g,contextptr);
    return tsimplify_common(g,contextptr);
  }
  gen _tsimplify(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tsimplify(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_tsimplify,contextptr);
    return tsimplify(args,contextptr);
  }
  static const char _tsimplify_s []="tsimplify";
  static define_unary_function_eval (__tsimplify,&giac::_tsimplify,_tsimplify_s);
  define_unary_function_ptr5( at_tsimplify ,alias_at_tsimplify,&__tsimplify,0,true);

  // find symbolic vars in g that have u has sommet
  vecteur lop(const gen & g,const unary_function_ptr & u){
    if (!has_op(g,u))
      return vecteur(0);
    if (g.type==_SYMB) {
      if (g._SYMBptr->sommet==u) 
	return vecteur(1,g);
      else
	return lop(g._SYMBptr->feuille,u);
    }
    if (g.type!=_VECT)
      return vecteur(0);
    vecteur res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it)
      res=mergeset(res,lop(*it,u));
    return res;
  }

  // find symbolic vars in g that have u has sommet
  vecteur lop(const gen & g,const unary_function_ptr * u){
    if (!has_op(g,*u))
      return vecteur(0);
    if (g.type==_SYMB) {
      if (g._SYMBptr->sommet==u) 
	return vecteur(1,g);
      else
	return lop(g._SYMBptr->feuille,u);
    }
    if (g.type!=_VECT)
      return vecteur(0);
    vecteur res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it)
      res=mergeset(res,lop(*it,u));
    return res;
  }

  // find symbolic vars in g that have u has sommet
  vecteur loptab(const gen & g,const unary_function_ptr * v){
    if (g.type==_SYMB) {
      if (equalposcomp(v,g._SYMBptr->sommet))
	return vecteur(1,g);
      else
	return loptab(g._SYMBptr->feuille,v);
    }
    if (g.type!=_VECT)
      return vecteur(0);
    vecteur res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it)
      res=mergeset(res,loptab(*it,v));
    return res;
  }

  // find symbolic vars in g that have u has sommet
  vecteur lop(const gen & g,const vector<const unary_function_ptr *> & v){
    if (g.type==_SYMB) {
      if (equalposcomp(v,&g._SYMBptr->sommet))
	return vecteur(1,g);
      else
	return lop(g._SYMBptr->feuille,v);
    }
    if (g.type!=_VECT)
      return vecteur(0);
    vecteur res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it)
      res=mergeset(res,lop(*it,v));
    return res;
  }

  gen expln2trig(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return apply(g,contextptr,expln2trig);
    if (g.type!=_SYMB)
      return g;
    if (g._SYMBptr->sommet==at_inv){
      // inv[*]=*[inv]
      // inv[exp]=exp[neg]
      const gen & f=g._SYMBptr->feuille;
      if (f.type==_SYMB){ 
	const unary_function_ptr & s=f._SYMBptr->sommet;
	const gen & ff=f._SYMBptr->feuille;
	if (s==at_exp)
	  return expln2trig(exp(-ff,contextptr),contextptr);
	if (s==at_prod)
	  return _prod(expln2trig(inv(ff,contextptr),contextptr),contextptr);
	if (s==at_pow)
	  return pow(expln2trig(inv(ff._VECTptr->front(),contextptr),contextptr),ff._VECTptr->back(),contextptr);
      }	
      // otherwise multiply by the conjugate
      gen tmp=expln2trig(g._SYMBptr->feuille,contextptr);
      gen retmp=re(tmp,contextptr); 
      gen imtmp=im(tmp,contextptr); 
      return (retmp-cst_i*imtmp)*inv(pow(retmp,2)+pow(imtmp,2),contextptr);
    }
    if (g._SYMBptr->sommet==at_exp)
      return sincos(g,contextptr);
    gen f=expln2trig(g._SYMBptr->feuille,contextptr);
    if (g._SYMBptr->sommet!=at_plus && g._SYMBptr->sommet!=at_prod && g._SYMBptr->sommet!=at_inv && g._SYMBptr->sommet!=at_pow && g._SYMBptr->sommet!=at_neg)
      f=normal(f,contextptr);
    if (g._SYMBptr->sommet==at_ln){
      gen gre=re(f,contextptr); 
      gen gim=im(f,contextptr); 
      if (is_zero(gre))
	return ln(pow(gim,2),contextptr)/2+sign(gim,contextptr)*cst_i*cst_pi_over_2;
      if (is_zero(gim)){
	if (complex_mode(contextptr))
	  return rdiv(ln(pow(gre,2),contextptr),plus_two,contextptr)+cst_i*(plus_one-sign(gre,contextptr))*cst_pi_over_2;
	else
	  return ln(f,contextptr);
      }
      return rdiv(ln(pow(gre,2)+pow(gim,2),contextptr),plus_two,contextptr)+cst_i*(atan(gim/gre,contextptr)+sign(gim,contextptr)*(plus_one-sign(gre,contextptr))*cst_pi_over_2);
    }
    return symbolic(g._SYMBptr->sommet,f);
  }

  gen gen_feuille(const gen & g){
    if (g.type==_SYMB)
      return g._SYMBptr->feuille;
    return g;
  }

  gen simplify(const gen & e_orig,GIAC_CONTEXT){
    if (e_orig.type<=_POLY || is_inf(e_orig))
      return e_orig;
    gen e=e_orig;
    if (e.type==_FRAC)
      return _evalc(e_orig,contextptr);
    if (!lop(e,at_ln).empty())
      e=lncollect(e_orig,contextptr);
    if (!lop(e,at_exp).empty())
      e=_exp2pow(e,contextptr);
    if (e.type==_SYMB && e._SYMBptr->feuille.type!=_VECT){
      if (e._SYMBptr->sommet!=at_inv && e._SYMBptr->sommet!=at_neg)
	return e._SYMBptr->sommet(simplify(e._SYMBptr->feuille,contextptr),contextptr); 
    }
    vecteur vabs(lop(e,at_abs));
    vecteur vabs2(vabs);
    iterateur it=vabs2.begin(),itend=vabs2.end();
    for (;it!=itend;++it){
      *it=symbolic(at_abs,factor(it->_SYMBptr->feuille,false,contextptr));
    }
    e=quotesubst(e,vabs,vabs2,contextptr);
    vabs=lop(lvar(e),at_pow);
    vabs2=vabs;
    it=vabs2.begin(); itend=vabs2.end();
    for (;it!=itend;++it){
      gen tmp=it->_SYMBptr->feuille;
      if (tmp.type==_VECT && tmp._VECTptr->size()==2)
	*it=normal(pow(simplify(tmp._VECTptr->front(),contextptr),tmp._VECTptr->back(),contextptr),contextptr);
    }
    // try to rewrite powers with less indep. vars
    if (vabs2.size()>1)
      vabs2=*tsimplify_common(vabs2,contextptr)._VECTptr;
    e=quotesubst(e,vabs,vabs2,contextptr);
    e=recursive_normal(e,contextptr); 
    // Don't touch fractional powers and absolute value anymore
    vabs2=lvar(e);
    vabs.clear();
    for (unsigned int i=0;i<vabs2.size();++i){
      if (vabs2[i].is_symb_of_sommet(at_abs) || vabs2[i].is_symb_of_sommet(at_pow))
	vabs.push_back(vabs2[i]);
    }
    if (!vabs.empty() && debug_infolevel)
      *logptr(contextptr) << gettext("simplify preserving ") << vabs << endl;
    int s=vabs.size();
    vabs2=vecteur(s);
    for (int i=0;i<s;++i){
      // vabs2[i]=identificateur(" "+vabs[i].print(contextptr));
      vabs2[i]=identificateur(" simplify_"+print_INT_(i));
    }
    gen esave=e;
    e=quotesubst(e,vabs,vabs2,contextptr);
    // check for the presence of trig/atrig functions
    vecteur v1(loptab(e,sincostan_tab));
    int s1=v1.size(),s2=loptab(e,asinacosatan_tab).size();
    gen g=tsimplify_noexpln(e,s1,s2,contextptr); 
    g=_exp2pow(g,contextptr);
    g=quotesubst(g,vabs2,vabs,contextptr);
    if (s1<=1 && s2<= 1)
      return ratnormal(g);
    int te=taille(e,RAND_MAX);
    int tg=taille(g,10*te);
    if (tg>=10*te)
      return esave;
    // convert back to trig and atrig functions
    g=expln2trig(g,contextptr); 
    if (!complex_mode(contextptr) && !has_i(g)){ 
      if (s1){
	if (v1.front().is_symb_of_sommet(at_sin))
	  return recursive_normal(trigsin(g,contextptr),contextptr); 
      }
      return recursive_normal(trigcos(g,contextptr),contextptr); 
    }
    gen reg=recursive_normal(re(g,contextptr),contextptr),
      img=recursive_normal(im(g,contextptr),contextptr);
    if (s1){
      if (v1.front().is_symb_of_sommet(at_sin))
	return normal(trigsin(reg,contextptr),contextptr)+cst_i*normal(trigsin(img,contextptr),contextptr);
    }
    g=normal(trigcos(reg,contextptr),contextptr)+cst_i*normal(trigcos(img,contextptr),contextptr);
    return g;
  }
  gen _simplify(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_simplify(res,contextptr)));
    if (args.type==_VECT){
      vecteur & v =*args._VECTptr;
      int vs=v.size();
      if ( (vs==2 || vs==3) && args.subtype==_SEQ__VECT && args[1].type==_VECT && !ckmatrix(args)){
	// simplify with side relations
	return _greduce(args,contextptr);
      }
      return apply(args,_simplify,contextptr);
    }
    if (is_equal(args))
      return apply_to_equal(args,_simplify,contextptr);
    int c=calc_mode(contextptr);
    calc_mode(0,contextptr);
    res=simplify(args,contextptr);
    calc_mode(c,contextptr);
    return res;
  }
  static const char _simplify_s []="simplify";
  static define_unary_function_eval (__simplify,&giac::_simplify,_simplify_s);
  define_unary_function_ptr5( at_simplify ,alias_at_simplify,&__simplify,0,true);

  gen trigcos(const gen & e,GIAC_CONTEXT){
    return subst(simplifier(e,contextptr),pow_tab,trigcos_tab,false,contextptr);
  }
  gen _trigcos(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_trigcos(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_trigcos,contextptr);
    return normal(trigcos(args,contextptr),contextptr);
  }
  static const char _trigcos_s []="trigcos";
  static define_unary_function_eval (__trigcos,&giac::_trigcos,_trigcos_s);
  define_unary_function_ptr5( at_trigcos ,alias_at_trigcos,&__trigcos,0,true);

  gen trigsin(const gen & e,GIAC_CONTEXT){
    return subst(simplifier(e,contextptr),pow_tab,trigsin_tab,false,contextptr);
  }
  gen _trigsin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_trigsin(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_trigsin,contextptr);
    return normal(trigsin(args,contextptr),contextptr);
  }
  static const char _trigsin_s []="trigsin";
  static define_unary_function_eval (__trigsin,&giac::_trigsin,_trigsin_s);
  define_unary_function_ptr5( at_trigsin ,alias_at_trigsin,&__trigsin,0,true);

  gen trigtan(const gen & e,GIAC_CONTEXT){
    return subst(simplifier(e,contextptr),pow_tab,trigtan_tab,false,contextptr);
  }
  gen _trigtan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_trigtan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_trigtan,contextptr);
    return normal(trigtan(args,contextptr),contextptr);
  }
  static const char _trigtan_s []="trigtan";
  static define_unary_function_eval (__trigtan,&giac::_trigtan,_trigtan_s);
  define_unary_function_ptr5( at_trigtan ,alias_at_trigtan,&__trigtan,0,true);

  gen tan2sincos(const gen & e,GIAC_CONTEXT){
    return subst(e,tan_tab,tan2sincos_tab,false,contextptr);
  }
  gen _tan2sincos(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
     gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tan2sincos(res,contextptr)));
   if (is_equal(args))
      return apply_to_equal(args,_tan2sincos,contextptr);
    return tan2sincos(args,contextptr);
  }
  static const char _tan2sincos_s []="tan2sincos";
  static define_unary_function_eval (__tan2sincos,&giac::_tan2sincos,_tan2sincos_s);
  define_unary_function_ptr5( at_tan2sincos ,alias_at_tan2sincos,&__tan2sincos,0,true);

  static gen sintocostan(const gen & e,GIAC_CONTEXT){
    return tan(e,contextptr)*cos(e,contextptr);
  }
  gen sin2_costan(const gen & e,GIAC_CONTEXT){
    vector< gen_op_context > sin2costan_v(1,sintocostan);
    vector<const unary_function_ptr *> sin_v(1,at_sin);
    return subst(e,sin_v,sin2costan_v,false,contextptr);
  }
  gen _sin2costan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_sin2costan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_sin2costan,contextptr);
    return sin2_costan(args,contextptr);
  }
  static const char _sin2costan_s []="sin2costan";
  static define_unary_function_eval (__sin2costan,&giac::_sin2costan,_sin2costan_s);
  define_unary_function_ptr5( at_sin2costan ,alias_at_sin2costan,&__sin2costan,0,true);

  static gen costosintan(const gen & e,GIAC_CONTEXT){
    return sin(e,contextptr)/tan(e,contextptr);
  }
  gen cos2sintan(const gen & e,GIAC_CONTEXT){
    vector< gen_op_context > cos2sintan_v(1,costosintan);
    vector<const unary_function_ptr *> cos_v(1,at_cos);
    return subst(e,cos_v,cos2sintan_v,false,contextptr);
  }
  gen _cos2sintan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_cos2sintan(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_cos2sintan,contextptr);
    return cos2sintan(args,contextptr);
  }
  static const char _cos2sintan_s []="cos2sintan";
  static define_unary_function_eval (__cos2sintan,&giac::_cos2sintan,_cos2sintan_s);
  define_unary_function_ptr5( at_cos2sintan ,alias_at_cos2sintan,&__cos2sintan,0,true);

  gen tan2sincos2(const gen & e,GIAC_CONTEXT){
    return subst(e,tan_tab,tan2sincos2_tab,false,contextptr);
  }
  gen _tan2sincos2(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tan2sincos2(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_tan2sincos2,contextptr);
    return tan2sincos2(args,contextptr);
  }
  static const char _tan2sincos2_s []="tan2sincos2";
  static define_unary_function_eval (__tan2sincos2,&giac::_tan2sincos2,_tan2sincos2_s);
  define_unary_function_ptr5( at_tan2sincos2 ,alias_at_tan2sincos2,&__tan2sincos2,0,true);

  gen tan2cossin2(const gen & e,GIAC_CONTEXT){
    return subst(e,tan_tab,tan2cossin2_tab,false,contextptr);
  }
  gen _tan2cossin2(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tan2cossin2(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_tan2cossin2,contextptr);
    return tan2cossin2(args,contextptr);
  }
  static const char _tan2cossin2_s []="tan2cossin2";
  static define_unary_function_eval (__tan2cossin2,&giac::_tan2cossin2,_tan2cossin2_s);
  define_unary_function_ptr5( at_tan2cossin2 ,alias_at_tan2cossin2,&__tan2cossin2,0,true);

  gen tcollect(const gen & args,GIAC_CONTEXT){
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur v;
    tlin(args,v,contextptr);
    // v= [coeff, sin/cos/1]
    int s=v.size();
    vector<int> deja;
    gen res,argu;
    for (int i=1;i<s;i+=2){
      if (equalposcomp(deja,i))
	continue;
      if (v[i].type!=_SYMB){
	res = res + v[i-1]*v[i];
	continue;
      }
      argu=v[i]._SYMBptr->feuille;
      int j=i+2;
      for (;j<s;j+=2){
	if ( (v[j].type==_SYMB) && (v[j]._SYMBptr->feuille==argu) )
	  break;
      }
      if (j>=s)
	res = res + v[i-1]*v[i];
      else { // found 2 trig terms with the same arg, combine
	deja.push_back(j);
	gen coscoeff,sincoeff;
	if (v[i]._SYMBptr->sommet==at_cos){
	  coscoeff=v[i-1];
	  sincoeff=v[j-1];
	}
	else {
	  coscoeff=v[j-1];
	  sincoeff=v[i-1];
	}
	gen newcoeff=normal(sqrt(pow(coscoeff,2)+pow(sincoeff,2),contextptr),contextptr);
	gen angle=normal(arg(coscoeff+cst_i*sincoeff,contextptr),contextptr);
	res=res+newcoeff*symbolic(at_cos,argu-angle);
      }
    }
    return res;
  }
  gen _tcollect(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tcollect(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_tcollect,contextptr);
    return apply(args,contextptr,tcollect);
  }
  static const char _tcollect_s []="tcollect";
  static define_unary_function_eval (__tcollect,&giac::_tcollect,_tcollect_s);
  define_unary_function_ptr5( at_tcollect ,alias_at_tcollect,&__tcollect,0,true);

  static const char _rassembler_trigo_s []="rassembler_trigo";
  static define_unary_function_eval (__rassembler_trigo,&giac::_tcollect,_rassembler_trigo_s);
  define_unary_function_ptr5( at_rassembler_trigo ,alias_at_rassembler_trigo,&__rassembler_trigo,0,true);

  static void postlncollect(vecteur & rescoeff,vecteur & resargln,vecteur & res,GIAC_CONTEXT){
    gen d;
    lcmdeno(rescoeff,d,contextptr);
    const_iterateur it=rescoeff.begin(),itend=rescoeff.end();
    const_iterateur jt=resargln.begin();
    gen lnint(plus_one);
    res.reserve(2*(itend-it));
    for (;it!=itend;++it,++jt){
      if (is_zero(*it))
	continue;
      if (it->type==_INT_)
	lnint=lnint*pow(*jt,it->val);
      else {
	res.push_back(*it/d);
	res.push_back(*jt);
      }
    }
    if (!is_one(lnint)){
      res.push_back(inv(d,contextptr));
      res.push_back(lnint);
    }
  }

  // -> [ coeffln argln ...]
  static vecteur inlncollect(const gen & args,GIAC_CONTEXT){
    if (args.type!=_SYMB)
      return makevecteur(args,symbolic(at_exp,1));
    const unary_function_ptr & u=args._SYMBptr->sommet;
    gen g=args._SYMBptr->feuille;
    if (u==at_ln)
      return makevecteur(1,g);
    if (u==at_plus){
      if (g.type!=_VECT)
	return inlncollect(g,contextptr);
      vecteur rescoeff,resargln;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end(),jt,jtend;
      for (;it!=itend;++it){
	vecteur tmp(inlncollect(*it,contextptr));
	jt=tmp.begin();
	jtend=tmp.end();
	for (;jt!=jtend;jt+=2){
	  int i;
	  if ( (i=equalposcomp(resargln,*(jt+1))) ){
	    rescoeff[i-1]=normal(rescoeff[i-1]+*jt,contextptr);
	  }
	  else {
	    rescoeff.push_back(*jt);
	    resargln.push_back(*(jt+1));
	  }
	}
      }
      vecteur res;
      postlncollect(rescoeff,resargln,res,contextptr);
      return res;
    }
    if (u==at_neg){
      vecteur tmp(inlncollect(g,contextptr));
      iterateur it=tmp.begin(),itend=tmp.end();
      for (;it!=itend;it+=2){
	*it=-*it;
      }
      return tmp;
    }
    if (u==at_prod){
      if (g.type!=_VECT)
	return inlncollect(g,contextptr);
      // is there a ln in g?
      const_iterateur it=g._VECTptr->begin(),it0=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_ln) )
	  break;
      }
      if (it!=itend){
	vecteur v(mergevecteur(vecteur(it0,it),vecteur(it+1,itend)));
	gen tmp=_prod(v,contextptr);
	vecteur vcoeff(1,tmp),varg(1,it->_SYMBptr->feuille),res;
	postlncollect(vcoeff,varg,res,contextptr);
	return res;
      }
    }
    return makevecteur((u)(_lncollect(g,contextptr),contextptr),
		       symbolic(at_exp,1)); 
  }
  gen lncollect(const gen & args,GIAC_CONTEXT){
    vecteur v(inlncollect(args,contextptr));
    gen res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;it+=2){
      res = res + (*it) * ln (*(it+1),contextptr);
    }
    return res;
  }
  gen _lncollect(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_lncollect(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_lncollect,contextptr);
    return apply(args,lncollect,contextptr);
  }
  static const char _lncollect_s []="lncollect";
  static define_unary_function_eval (__lncollect,&giac::_lncollect,_lncollect_s);
  define_unary_function_ptr5( at_lncollect ,alias_at_lncollect,&__lncollect,0,true);

  gen powexpand(const gen & e,GIAC_CONTEXT){
    return subst(e,pow_tab,powexpand_tab,false,contextptr);
  }
  gen _powexpand(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_powexpand(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_powexpand,contextptr);
    return apply(args,powexpand,contextptr);
  }
  static const char _powexpand_s []="powexpand";
  static define_unary_function_eval (__powexpand,&giac::_powexpand,_powexpand_s);
  define_unary_function_ptr5( at_powexpand ,alias_at_powexpand,&__powexpand,0,true);

  gen exp2pow(const gen & e,GIAC_CONTEXT){
    return subst(e,exp_tab,exp2power_tab,false,contextptr);
  }
  gen _exp2pow(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_exp2pow(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_exp2pow,contextptr);
    return apply(args,exp2pow,contextptr);
  }
  static const char _exp2pow_s []="exp2pow";
  static define_unary_function_eval (__exp2pow,&giac::_exp2pow,_exp2pow_s);
  define_unary_function_ptr5( at_exp2pow ,alias_at_exp2pow,&__exp2pow,0,true);
  
  gen factor_xn(const gen & args,const gen & x,GIAC_CONTEXT){
    vecteur l(1,x);
    lvar(args,l);
    gen temp=e2r(args,l,contextptr);
    gen n,d;
    fxnd(temp,n,d);
    l.erase(l.begin());
    vecteur nv(gen2vecteur(r2e(polynome2poly1(n,1),l,contextptr)));
    vecteur dv(gen2vecteur(r2e(polynome2poly1(d,1),l,contextptr)));
    int ns=nv.size(),ds=dv.size();
    int n1=ns-ds;
    return pow(x,n1)*symb_horner(nv,x,ns-1)/symb_horner(dv,x,ds-1);
  }
  gen factor_xn(const gen & args,GIAC_CONTEXT){
    return factor_xn(args,vx_var,contextptr);
  }
  gen _factor_xn(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_factor_xn(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_factor_xn,contextptr);
    return apply(args,factor_xn,contextptr);
  }
  static const char _factor_xn_s []="factor_xn";
  static define_unary_function_eval (__factor_xn,&giac::_factor_xn,_factor_xn_s);
  define_unary_function_ptr5( at_factor_xn ,alias_at_factor_xn,&__factor_xn,0,true);

  static const char _developper_s []="developper";
  static define_unary_function_eval (__developper,&giac::expand,_developper_s);
  define_unary_function_ptr5( at_developper ,alias_at_developper,&__developper,0,true);

  static const char _factoriser_s []="factoriser";
  static define_unary_function_eval (__factoriser,&giac::_factor,_factoriser_s);
  define_unary_function_ptr5( at_factoriser ,alias_at_factoriser,&__factoriser,0,true);

  static const char _factoriser_xn_s []="factoriser_xn";
  static define_unary_function_eval (__factoriser_xn,&giac::_factor_xn,_factoriser_xn_s);
  define_unary_function_ptr5( at_factoriser_xn ,alias_at_factoriser_xn,&__factoriser_xn,0,true);

  static const char _resoudre_s []="resoudre";
  static define_unary_function_eval_quoted (__resoudre,&giac::_solve,_resoudre_s);
  define_unary_function_ptr5( at_resoudre ,alias_at_resoudre,&__resoudre,_QUOTE_ARGUMENTS,true);

  static const char _substituer_s []="substituer";
  static define_unary_function_eval (__substituer,&giac::_subst,_substituer_s);
  define_unary_function_ptr5( at_substituer ,alias_at_substituer,&__substituer,0,true);

  static const char _deriver_s []="deriver";
  static define_unary_function_eval (__deriver,&giac::_derive,_deriver_s);
  define_unary_function_ptr5( at_deriver ,alias_at_deriver,&__deriver,0,true);

  static const char _integrer_s []="integrer";
  static define_unary_function_eval (__integrer,&giac::_integrate,_integrer_s);
  define_unary_function_ptr5( at_integrer ,alias_at_integrer,&__integrer,0,true);

  static const char _limite_s []="limite";
  static define_unary_function_eval (__limite,&giac::_limit,_limite_s);
  define_unary_function_ptr5( at_limite ,alias_at_limite,&__limite,0,true);

  static void find_conjugates(const gen & g,vecteur & v_in,vecteur & v_out){
    v_in.clear();
    v_out.clear();
    vecteur v1(lop(g,at_pow));
    int n=v1.size();
    for (int i=0;i<n;++i){
      gen & tmp =v1[i]._SYMBptr->feuille;
      if (tmp.type!=_VECT)
	continue;
      vecteur & v=*tmp._VECTptr;
      if (v.size()==2 && v.back().type==_FRAC && v.back()._FRACptr->den==plus_two){
	v_in.push_back(v1[i]);
	v_out.push_back(-v1[i]);
	break ; // change only 1 sqrt
      }
    }
  }

  gen _mult_conjugate(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g(g0);
    bool deno=true;
    if (g.type==_VECT && g._VECTptr->size()==2){
      if (g._VECTptr->back()==at_numer)
	deno=false;
      g=g._VECTptr->front();
    }
    gen n,d;
    vecteur v_in,v_out;
    prod2frac(g,v_in,v_out);
    n=_prod(v_in,contextptr);
    d=_prod(v_out,contextptr);
    find_conjugates(d,v_in,v_out);
    // search sqrt in d
    if (!deno || v_in.empty()){
      find_conjugates(n,v_in,v_out);
      gen mult=subst(n,v_in,v_out,false,contextptr);
      n=n*mult; // n=simplify(n*mult);
      d=d*mult;
    }
    else {
      gen mult=subst(d,v_in,v_out,false,contextptr);
      d=d*mult; // d=simplify(d*mult);
      n=n*mult;
    }
    return n/d;
  }
  static const char _mult_conjugate_s []="mult_conjugate";
  static define_unary_function_eval (__mult_conjugate,&_mult_conjugate,_mult_conjugate_s);
  define_unary_function_ptr5( at_mult_conjugate ,alias_at_mult_conjugate,&__mult_conjugate,0,true);

  gen _mult_c_conjugate(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g(g0);
    bool deno=true;
    if (g.type==_VECT && g._VECTptr->size()==2){
      if (g._VECTptr->back()==at_numer)
	deno=false;
      g=g._VECTptr->front();
    }
    gen n,d;
    vecteur v_in,v_out;
    prod2frac(g,v_in,v_out);
    n=_prod(v_in,contextptr);
    d=_prod(v_out,contextptr);
    if (deno && !is_zero(im(d,contextptr))){
      gen mult=conj(d,contextptr);
      n=symbolic(at_prod,gen(makevecteur(n,mult),_SEQ__VECT));
      d=symbolic(at_prod,gen(makevecteur(d,mult),_SEQ__VECT));
    }
    else {
      if (!is_zero(im(n,contextptr))){
	gen mult=conj(n,contextptr);
	n=symbolic(at_prod,gen(makevecteur(n,mult),_SEQ__VECT));
	d=symbolic(at_prod,gen(makevecteur(d,mult),_SEQ__VECT));
      }
    }
    return n/d;
  }
  static const char _mult_c_conjugate_s []="mult_c_conjugate";
  static define_unary_function_eval_quoted (__mult_c_conjugate,&_mult_c_conjugate,_mult_c_conjugate_s);
  define_unary_function_ptr5( at_mult_c_conjugate ,alias_at_mult_c_conjugate,&__mult_c_conjugate,_QUOTE_ARGUMENTS,true);

  static const char _multiplier_conjugue_s []="multiplier_conjugue";
  static define_unary_function_eval (__multiplier_conjugue,&giac::_mult_conjugate,_multiplier_conjugue_s);
  define_unary_function_ptr5( at_multiplier_conjugue ,alias_at_multiplier_conjugue,&__multiplier_conjugue,0,true);

  static const char _multiplier_conjugue_complexe_s []="multiplier_conjugue_complexe";
  static define_unary_function_eval (__multiplier_conjugue_complexe,&giac::_mult_c_conjugate,_multiplier_conjugue_complexe_s);
  define_unary_function_ptr5( at_multiplier_conjugue_complexe ,alias_at_multiplier_conjugue_complexe,&__multiplier_conjugue_complexe,0,true);

  gen _combine(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gensizeerr(contextptr);
    gen & f=v[s-1];
    gen g=v.front();
    if (f.type==_FUNC){
      if (f==at_sincos || f==at_sin || f==at_cos)
	return tcollect(g,contextptr);
      if (f==at_exp)
	return _lin(g,contextptr);
      if (f==at_ln)
	return lncollect(g,contextptr);
      return gensizeerr(contextptr);
    }
    if (f.type==_INT_ && f.val>=0) {
      int i=f.val;
      if (f.subtype==_INT_MAPLECONVERSION){
	switch (i){
	case _TRIG:
	  return tcollect(g,contextptr);
	case _EXPLN:
	  return _lin(lncollect(g,contextptr),contextptr);
	default:
	  return gensizeerr(contextptr);
	}
      }
    }
    return gensizeerr(contextptr);
  }
  static const char _combine_s []="combine";
  static define_unary_function_eval (__combine,&_combine,_combine_s);
  define_unary_function_ptr5( at_combine ,alias_at_combine,&__combine,0,true);

  static gen rectangular2polar(const gen & g,const context * contextptr){
    gen args=remove_at_pnt(g);
    gen module=abs(args,contextptr),argument=arg(args,contextptr);
    if (is_zero(argument))
      return module;
    return module*symbolic(at_exp,cst_i*argument);
  }

  gen _rectangular2polar(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,rectangular2polar,contextptr);
  }
  static const char _rectangular2polar_s []="rectangular2polar";
  static define_unary_function_eval (__rectangular2polar,&_rectangular2polar,_rectangular2polar_s);
  define_unary_function_ptr5( at_rectangular2polar ,alias_at_rectangular2polar,&__rectangular2polar,0,true);

  static gen polar2rectangular(const gen & g,const context * contextptr){
    gen args=remove_at_pnt(g);
    gen reel=re(args,contextptr), imag=im(args,contextptr);
    return reel+cst_i*imag;
  }

  gen _polar2rectangular(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,polar2rectangular,contextptr);
  }
  static const char _polar2rectangular_s []="polar2rectangular";
  static define_unary_function_eval (__polar2rectangular,&_polar2rectangular,_polar2rectangular_s);
  define_unary_function_ptr5( at_polar2rectangular ,alias_at_polar2rectangular,&__polar2rectangular,0,true);

  // x,y,z->rho,theta in [-pi/2..pi/2],psi
  // x=rho*cos(theta)*cos(phi), y=rho*cos(theta)*sin(phi), z=rho*sin(theta)
  // rho=sqrt(x^2+y^2+z^2)
  static gen rectangular2spherical(const gen & g,const context * contextptr){
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gensizeerr(contextptr);
    vecteur & v =*g._VECTptr;
    gen x =v[0],y=v[1],z=v[2];
    gen rho2=x*x+y*y+z*z;
    gen rho=sqrt(rho2,contextptr);
    gen theta=asin(z/rho,contextptr);
    gen phi=arg(x+cst_i*y,contextptr);
    return makevecteur(rho,theta,phi);
  }

  gen _rectangular2spherical(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==3 && args._VECTptr->front().type!=_VECT)
      return rectangular2spherical(args,contextptr);
    return apply(args,rectangular2spherical,contextptr);
  }
  static const char _rectangular2spherical_s []="rectangular2spherical";
  static define_unary_function_eval (__rectangular2spherical,&_rectangular2spherical,_rectangular2spherical_s);
  define_unary_function_ptr5( at_rectangular2spherical ,alias_at_rectangular2spherical,&__rectangular2spherical,0,true);

  static gen spherical2rectangular(const gen & g,const context * contextptr){
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gensizeerr(contextptr);
    vecteur & v =*g._VECTptr;
    gen rho=v[0],theta=v[1],phi=v[2];
    gen rhocos=rho*cos(theta,contextptr);
    return makevecteur(rhocos*cos(phi,contextptr),rhocos*sin(phi,contextptr),rho*sin(theta,contextptr));
  }

  gen _spherical2rectangular(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==3 && args._VECTptr->front().type!=_VECT)
      return spherical2rectangular(args,contextptr);
    return apply(args,spherical2rectangular,contextptr);
  }
  static const char _spherical2rectangular_s []="spherical2rectangular";
  static define_unary_function_eval (__spherical2rectangular,&_spherical2rectangular,_spherical2rectangular_s);
  define_unary_function_ptr5( at_spherical2rectangular ,alias_at_spherical2rectangular,&__spherical2rectangular,0,true);

  static gen heavisidetosign(const gen & args,GIAC_CONTEXT){
    return (1+sign(args,contextptr))/2;
  }
  const gen_op_context heaviside2sign_tab[]={heavisidetosign,0};
  
  gen Heavisidetosign(const gen & args,GIAC_CONTEXT){
    return subst(args,Heaviside_tab,heaviside2sign_tab,false,contextptr);
    // return subst(args,vector<const unary_function_ptr *>(1,at_Heaviside), vector< gen_op_context >(1,heavisidetosign),false,contextptr);
  }
  gen _Heavisidetosign(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,Heavisidetosign,contextptr);
    return apply(args,Heavisidetosign,contextptr);
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
