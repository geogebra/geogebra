// -*- mode:C++ ; compile-command: "g++ -I.. -g -c lin.cc -DHAVE_CONFIG_H -DIN_GIAC" -*-
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
#include <cstdlib>
#include "sym2poly.h"
#include "usual.h"
#include "lin.h"
#include "subst.h"
#include "modpoly.h"
#include "prog.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // Should be rewritten with a map container for better efficiency!

  bool contains(const gen & e,const unary_function_ptr & mys){
    if (e.type!=_SYMB)
      return false;
    if (e._SYMBptr->sommet==mys)
      return true;
    if (e._SYMBptr->feuille.type!=_VECT)
      return contains(e._SYMBptr->feuille,mys);
    vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
    for (;it!=itend;++it)
      if (contains(*it,mys))
	return true;
    return false;
  }

  void compress(vecteur & res,GIAC_CONTEXT){
    if (res.size()==2) return;
    vecteur v,w;
    const_iterateur it=res.begin(),itend=res.end();
    v.reserve(itend-it);
    w.reserve((itend-it)/2);
    int pos;
    for (;it!=itend;++it){
      pos=equalposcomp(w,*(it+1));
      if (pos){
	v[2*(pos-1)] = normal(v[2*(pos-1)] + *it,false,contextptr);
	++it;
      }
      else {
	v.push_back(*it);
	++it;
	w.push_back(*it);
	v.push_back(*it);
      }
    }
    swap(res,v);
  }

  // back conversion
  gen unlin(vecteur & v,GIAC_CONTEXT){
    vecteur w;
    gen coeff;
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      coeff = *it;
      ++it;
      if (!is_zero(coeff))
	w.push_back(coeff*exp(*it,contextptr));
    }
    if (w.empty())
      return 0;
    return _plus(w,contextptr);
  }

  void convolution(const gen & coeff, const gen & arg,const vecteur & w,vecteur & res,GIAC_CONTEXT){
    vecteur::const_iterator it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      res.push_back(coeff*(*it));
      ++it;
      res.push_back(normal(arg+(*it),false,contextptr));
    }
    compress(res,contextptr);
  }

  void convolution(const vecteur & v,const vecteur & w, vecteur & res,GIAC_CONTEXT){
    res.clear();
    res.reserve(res.size()+v.size()*w.size()/2);
    vecteur::const_iterator it=v.begin(),itend=v.end();
    gen coeff;
    for (;it!=itend;++it){
      coeff = *it;
      ++it;
      convolution(coeff,*it,w,res,contextptr);
    }
  }

  void convolutionpower(const vecteur & v,int k,vecteur & res,GIAC_CONTEXT){
    res.clear();
    // should be improved for efficiency!
    if (k==0){
      res.push_back(1);
      res.push_back(0);
      return;
    }
    if (k==1){
      res=v;
      return;
    }
    convolutionpower(v,k/2,res,contextptr);
    vecteur tmp=res;
    convolution(tmp,tmp,res,contextptr);
    if (k%2){
      tmp=res;
      convolution(tmp,v,res,contextptr);
    }
  }

  // coeff & argument of exponential
  void lin(const gen & e,vecteur & v,GIAC_CONTEXT){
    if (e.type!=_SYMB){
      v.push_back(e);
      v.push_back(0);
      return ; // e*exp(0)
    }
    // e is symbolic, look for exp, cosh, sinh, +, *, neg and inv, ^
    unary_function_ptr s=e._SYMBptr->sommet;
    if ((s==at_plus) && (e._SYMBptr->feuille.type==_VECT)){
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      for (;it!=itend;++it)
	lin(*it,v,contextptr);
      compress(v,contextptr);
      return;
    }
    if (s==at_neg){
      vecteur tmp;
      lin(e._SYMBptr->feuille,tmp,contextptr);
      const_iterateur it=tmp.begin(),itend=tmp.end();
      for (;it!=itend;++it){
	v.push_back(-*it);
	++it;
	v.push_back(*it);
      }
      return;
    }
    if (s==at_inv){
      vecteur w;
      lin(e._SYMBptr->feuille,w,contextptr);
      if (w.size()==2){
	v.push_back(inv(w[0],contextptr));
	v.push_back(-w[1]);
      }
      else {
	gen coeff(unlin(w,contextptr));
	v.push_back(inv(coeff,contextptr));
	v.push_back(0);
      }
      return ;
    }
    if (s==at_prod){
      if (e._SYMBptr->feuille.type!=_VECT){
	lin(e._SYMBptr->feuille,v,contextptr);
	return;
      }
      vecteur w;
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      lin(*it,w,contextptr);
      ++it;
      for (;it!=itend;++it){
	vecteur v0;
	lin(*it,v0,contextptr);
	vecteur res;
	convolution(w,v0,res,contextptr);
	w=res;
      }
      v=mergevecteur(v,w);
      return;
    }
    if (s==at_pow){
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin();
      vecteur w;
      lin(*it,w,contextptr);
      ++it;
      if (w.size()==2){
	if ( is_zero(w[1]) && (w[0].type==_INT_) ){
	  w[1]=ln(w[0],contextptr);
	  w[0]=plus_one;
	}
	v.push_back(pow(w[0],*it,contextptr));
	v.push_back(w[1]*(*it));
	return ;
      }
      if ((it->type==_INT_) && (it->val>=0)){
	vecteur z(w),tmp;
	convolutionpower(z,it->val,tmp,contextptr);
	v=mergevecteur(v,tmp);
	compress(v,contextptr);
	return ;
      }
      gen coeff=unlin(w,contextptr);
      v.push_back(pow(coeff,*it,contextptr));
      v.push_back(0);
      return ;
    }
    gen f=_lin(e._SYMBptr->feuille,contextptr);
    if (s==at_exp){
      v.push_back(1);
      v.push_back(f);
      return ; // 1*exp(arg)
    }
    if (s==at_cosh){
      v.push_back(rdiv(1,2,contextptr));
      v.push_back(f);
      v.push_back(rdiv(1,2,contextptr));
      v.push_back(-f);
      return ; // 1/2*exp(arg)+1/2*exp(-arg)
    }
    if (s==at_sinh){
      v.push_back(rdiv(1,2,contextptr));
      v.push_back(f);
      v.push_back(rdiv(-1,2,contextptr));
      v.push_back(-f);
      return ; // 1/2*exp(arg)-1/2*exp(-arg)
    }
    v.push_back(symbolic(s,f));
    v.push_back(0);
  }

  symbolic symb_lin(const gen & a){
    return symbolic(at_lin,a);
  }

  // "unary" version
  gen _lin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_lin(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_lin,contextptr);
    vecteur v;
    if (args.type!=_VECT){
      lin(args,v,contextptr);
      return unlin(v,contextptr);
    }
    return apply(args,_lin,contextptr);
  }
  static const char _lin_s []="lin";
  static define_unary_function_eval (__lin,&_lin,_lin_s);
  define_unary_function_ptr5( at_lin ,alias_at_lin,&__lin,0,true);

  static const char _lineariser_s []="lineariser";
  static define_unary_function_eval (__lineariser,&_lin,_lineariser_s);
  define_unary_function_ptr5( at_lineariser ,alias_at_lineariser,&__lineariser,0,true);

  // back conversion
  gen tunlin(vecteur & v,GIAC_CONTEXT){
    vecteur w;
    gen coeff;
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      coeff = *it;
      ++it;
      coeff=coeff*(*it);
      if (!is_zero(coeff))
	w.push_back(coeff);
    }
    if (w.empty())
      return 0;
    if (w.size()==1)
      return w.front();
    return symbolic(at_plus,w);
  }

  static void tadd(vecteur & res,const gen & coeff,const gen & angle,GIAC_CONTEXT){
    gen newangle=angle,newcoeff=coeff;
    if ( (newangle.type==_SYMB) && (newangle._SYMBptr->sommet==at_neg)){
      newcoeff=-coeff;
      newangle=-newangle;
    }
    if ( (newangle.type==_SYMB) && ( (newangle._SYMBptr->sommet==at_sin) || (newangle._SYMBptr->sommet==at_cos) ) ){
      res.push_back(newcoeff);
      res.push_back(newangle);
    }
    else {
      newcoeff=newcoeff*newangle;
      if (!is_zero(newcoeff)){
	res.push_back(newcoeff);
	res.push_back(plus_one);
      }
    }
  }

  void tconvolution(const gen & coeff, const gen & arg,const vecteur & w,vecteur & res,GIAC_CONTEXT){
    gen newcoeff,tmp;
    if ((arg.type==_SYMB) && (arg._SYMBptr->sommet==at_cos)){
      vecteur::const_iterator it=w.begin(),itend=w.end();
      for (;it!=itend;++it){
	newcoeff=coeff*(*it);
	++it;
	if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_cos) ){
	  newcoeff=normal(rdiv(newcoeff,plus_two,contextptr),false,contextptr);
	  tadd(res,newcoeff,cos(normal(arg._SYMBptr->feuille-it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  tadd(res,newcoeff,cos(normal(arg._SYMBptr->feuille+it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  continue;
	}
	if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_sin) ){
	  newcoeff=normal(rdiv(newcoeff,plus_two,contextptr),false,contextptr);
	  tadd(res,newcoeff,sin(normal(it->_SYMBptr->feuille+arg._SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  tadd(res,newcoeff,sin(normal(it->_SYMBptr->feuille-arg._SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  continue;
	}
	res.push_back(normal(newcoeff*(*it),false,contextptr));
	res.push_back(arg);
      }
      compress(res,contextptr);
      return;
    }
    if ((arg.type==_SYMB) && (arg._SYMBptr->sommet==at_sin)){
      vecteur::const_iterator it=w.begin(),itend=w.end();
      for (;it!=itend;++it){
	newcoeff=coeff*(*it);
	++it;
	if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_cos) ){
	  newcoeff=normal(rdiv(newcoeff,plus_two,contextptr),false,contextptr);
	  tadd(res,newcoeff,sin(normal(arg._SYMBptr->feuille+it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  tadd(res,newcoeff,sin(normal(arg._SYMBptr->feuille-it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  continue;
	}
	if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_sin)){
	  newcoeff=normal(rdiv(newcoeff,plus_two,contextptr),false,contextptr);
	  tadd(res,newcoeff,cos(normal(arg._SYMBptr->feuille-it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  tadd(res,-newcoeff,cos(normal(arg._SYMBptr->feuille+it->_SYMBptr->feuille,false,contextptr),contextptr),contextptr);
	  continue;
	}
	res.push_back(normal(newcoeff*(*it),false,contextptr));
	res.push_back(arg);
      }
      compress(res,contextptr);
      return;
    }
    const_iterateur it=w.begin(),itend=w.end();
    newcoeff=coeff*arg;
    for (;it!=itend;++it){
      res.push_back(normal(*it*newcoeff,false,contextptr));
      ++it;
      res.push_back(*it);
    }
  }

  void tconvolution(const vecteur & v,const vecteur & w, vecteur & res,GIAC_CONTEXT){
    res.clear();
    res.reserve(res.size()+v.size()*w.size()/2);
    vecteur::const_iterator it=v.begin(),itend=v.end();
    gen coeff;
    for (;it!=itend;++it){
      coeff = *it;
      ++it;
      tconvolution(coeff,*it,w,res,contextptr);
    }
  }

  void tconvolutionpower(const vecteur & v,int k,vecteur & res,GIAC_CONTEXT){
    res.clear();
    // should be improved for efficiency!
    if (k==0){
      res.push_back(1);
      res.push_back(1);
      return;
    }
    if (k==1){
      res=v;
      return;
    }
    tconvolutionpower(v,k/2,res,contextptr);
    vecteur tmp=res;
    tconvolution(tmp,tmp,res,contextptr);
    if (k%2){
      tmp=res;
      tconvolution(tmp,v,res,contextptr);
    }
  }

  // coeff & argument of sin/cos
  void tlin(const gen & e,vecteur & v,GIAC_CONTEXT){
    if (e.type!=_SYMB){
      v.push_back(e);
      v.push_back(1);
      return ; // e*1
    }
    // e is symbolic, look for cos, sin, +, *, neg and inv, ^
    unary_function_ptr s=e._SYMBptr->sommet;
    if ( (s==at_cos) || (s==at_sin)){
      v.push_back(1);
      v.push_back(e);
      return ; 
    }
    if ((s==at_plus) && (e._SYMBptr->feuille.type==_VECT)){
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      for (;it!=itend;++it)
	tlin(*it,v,contextptr);
      compress(v,contextptr);
      return;
    }
    if (s==at_neg){
      vecteur w;
      tlin(e._SYMBptr->feuille,w,contextptr);
      const_iterateur it=w.begin(),itend=w.end();
      for (;it!=itend;++it){
	v.push_back(-*it);
	++it;
	v.push_back(*it);
      }
      return;
    }
    if (s==at_prod){
      if (e._SYMBptr->feuille.type!=_VECT){
	tlin(e._SYMBptr->feuille,v,contextptr);
	return;
      }
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      vecteur w;
      tlin(*it,w,contextptr);
      ++it;
      for (;it!=itend;++it){
	vecteur v0;
	tlin(*it,v0,contextptr);
	vecteur res;
	tconvolution(w,v0,res,contextptr);
	w=res;
      }
      v=mergevecteur(v,w);
      return;
    }
    if (s==at_pow){
      vecteur::const_iterator it=e._SYMBptr->feuille._VECTptr->begin();
      /*
      if ( (v.size()==2) && ((it+1)->type==_INT_) && ((it+1)->val>=0) ){
	tlin(*it,v);
	++it;
	return tpow(v,it->val);
      }
      */
      if (((it+1)->type==_INT_) && ((it+1)->val>=0)){
	vecteur w;
	tlin(*it,w,contextptr);
	vecteur z(w);
	++it;
	tconvolutionpower(z,it->val,w,contextptr);
	v=mergevecteur(v,w);
	return ;
      }
    }
    v.push_back(e);
    v.push_back(1);
  }

  symbolic symb_tlin(const gen & a){
    return symbolic(at_tlin,a);
  }

  // "unary" version
  gen _tlin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_tlin(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_tlin,contextptr);
    vecteur v;
    if (args.type!=_VECT){
      tlin(args,v,contextptr);
      return tunlin(v,contextptr);
    }
    return apply(args,_tlin,contextptr);
  }
  static const char _tlin_s []="tlin";
  static define_unary_function_eval (__tlin,&_tlin,_tlin_s);
  define_unary_function_ptr5( at_tlin ,alias_at_tlin,&__tlin,0,true);

  static const char _lineariser_trigo_s []="lineariser_trigo";
  static define_unary_function_eval (__lineariser_trigo,&_tlin,_lineariser_trigo_s);
  define_unary_function_ptr5( at_lineariser_trigo ,alias_at_lineariser_trigo,&__lineariser_trigo,0,true);

  // Expand and texpand
  static void split(const gen & e, gen & coeff, gen & arg,GIAC_CONTEXT){
    if (e.type==_INT_){
      coeff=e;
      arg=plus_one;
    }
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_neg)){
      split(e._SYMBptr->feuille,coeff,arg,contextptr);
      coeff=-coeff;
      return;
    }
    if ( (e.type!=_SYMB) || (e._SYMBptr->sommet!=at_prod) ) {
      coeff=plus_one;
      arg=e;
      return;
    }
    coeff = plus_one;
    arg = plus_one;
    const_iterateur it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
    for (;it!=itend;++it){
      if (it->type==_INT_)
	coeff = coeff * (*it);
      else {
	if ( (it->type==_SYMB)  && (it->_SYMBptr->sommet==at_neg)){
	  coeff = -coeff;
	  arg = arg * (it->_SYMBptr->feuille);
	}
	else
	  arg= arg * (*it);
      }
    }
    if ( (coeff.type==_INT_) && (coeff.val<0) ){
      coeff=-coeff;
      arg=-arg;
    }
  }

  /*
  gen sigma(const vecteur & v){
    // an "intelligent" version of return symbolic(at_plus,v);
    // split each element of v as an integer coeff * an arg
    vecteur vcoeff,varg,res;
    int pos;
    gen coeff,arg;
    const_iterateur it=v.begin(),itend=v.end();
    vcoeff.reserve(itend-it);
    varg.reserve(itend-it);
    for (;it!=itend;++it){
      split(*it,coeff,arg);
      pos=equalposcomp(varg,arg);
      if (!pos){
	vcoeff.push_back(coeff);
	varg.push_back(arg);
      }
      else 
	vcoeff[pos-1]=vcoeff[pos-1]+coeff;
    }
    it=vcoeff.begin(),itend=vcoeff.end();
    res.reserve(itend-it);
    const_iterateur vargit=varg.begin();
    for (;it!=itend;++it,++vargit)
      if (!is_zero(*it))
	res.push_back((*it)*(*vargit));
    if (res.empty())
      return zero;
    if (res.size()>1)
      return symbolic(at_plus,res);
    return res.front();
  }
  */

  gen prod_expand(const gen & a,const gen & b,GIAC_CONTEXT){
    bool a_is_plus= (a.type==_SYMB) && (a._SYMBptr->sommet==at_plus);
    bool b_is_plus= (b.type==_SYMB) && (b._SYMBptr->sommet==at_plus);
    if ( (!a_is_plus) && (!b_is_plus) )
      return a*b;
    if (!a_is_plus) // distribute wrt b
      return symbolic(at_plus,a*(*b._SYMBptr->feuille._VECTptr));
    if (!b_is_plus)
      return symbolic(at_plus,b*(*a._SYMBptr->feuille._VECTptr));
    // distribute wrt a AND b
    const_iterateur ita=a._SYMBptr->feuille._VECTptr->begin(),itaend=a._SYMBptr->feuille._VECTptr->end();
    const_iterateur itb=b._SYMBptr->feuille._VECTptr->begin(),itbend=b._SYMBptr->feuille._VECTptr->end();
    vecteur v;
    v.reserve((itbend-itb)*(itaend-ita));
    for (;ita!=itaend;++ita){
      for (itb=b._SYMBptr->feuille._VECTptr->begin();itb!=itbend;++itb)
	v.push_back( (*ita) * (*itb) );
    }
    return symbolic(at_plus,gen(v,_SEQ__VECT));
  }

  static gen prod_expand(const const_iterateur it,const const_iterateur itend,GIAC_CONTEXT){
    int s=itend-it;
    if (s==0)
      return plus_one;
    if (s==1)
      return *it;
    return _simplifier(prod_expand(prod_expand(it,it+s/2,contextptr),prod_expand(it+s/2,itend,contextptr),contextptr),contextptr);
  }
  static gen prod_expand(const gen & e_orig,GIAC_CONTEXT){
    gen e=aplatir_fois_plus(expand(e_orig,contextptr));
    if (e.type!=_VECT)
      return e;
    // look for sommet=at_plus inside e
    return prod_expand(e._VECTptr->begin(),e._VECTptr->end(),contextptr);
  }

  static gen prod_expand_nosimp(const const_iterateur it,const const_iterateur itend,GIAC_CONTEXT){
    int s=itend-it;
    if (s==0)
      return plus_one;
    if (s==1)
      return *it;
    return prod_expand(prod_expand_nosimp(it,it+s/2,contextptr),prod_expand_nosimp(it+s/2,itend,contextptr),contextptr);
  }
  static gen prod_expand_nosimp(const gen & e_orig,GIAC_CONTEXT){
    gen e=aplatir_fois_plus(e_orig);
    if (e.type!=_VECT)
      return e;
    // look for sommet=at_plus inside e
    return prod_expand_nosimp(e._VECTptr->begin(),e._VECTptr->end(),contextptr);
  }

  static void pow_expand_add_res(vector<gen> & factn,int pos,int sumexpo,const gen & coeff,const vecteur & w,const gen & p, int k,int n,vecteur & res,GIAC_CONTEXT){
    if (sumexpo==k){
      // End recursion
      res.push_back(coeff*p);
      return;
    }
    if (pos==n-1){
      // End recursion
      res.push_back(coeff/factn[k-sumexpo]*p*expand(pow(w[pos],k-sumexpo),contextptr));
      return;
    }
    for (int i=k-sumexpo;i>=0;--i){
      pow_expand_add_res(factn,pos+1,sumexpo+i,coeff/factn[i],w,expand(p*pow(w[pos],i),contextptr),k,n,res,contextptr);
    }
  }

  static gen expand_pow_expand(const gen & e,GIAC_CONTEXT){
    if (e.type!=_VECT || e._VECTptr->size()!=2)
      return e;
    vecteur & v=*e._VECTptr;
    gen base=expand(v[0],contextptr);
    gen exponent=expand(v[1],contextptr);
    if (v[1].is_symb_of_sommet(at_plus) && v[1]._SYMBptr->feuille.type==_VECT){
      vecteur & w=*v[1]._SYMBptr->feuille._VECTptr;
      const_iterateur it=w.begin(),itend=w.end();
      vecteur prodarg;
      prodarg.reserve(itend-it);
      for (;it!=itend;++it){
	prodarg.push_back(pow(base,*it,contextptr));
      }
      return _prod(prodarg,contextptr);
    }
    if (v[1].type==_INT_ ){
      if (v[0].is_symb_of_sommet(at_prod)&& v[0]._SYMBptr->feuille.type==_VECT){
	vecteur w(*v[0]._SYMBptr->feuille._VECTptr);
	iterateur it=w.begin(),itend=w.end();
	for (;it!=itend;++it){
	  *it=pow(expand(*it,contextptr),v[1],contextptr);
	}
	return _prod(w,contextptr);
      }
    }
    if (v[0].is_symb_of_sommet(at_plus) && v[0]._SYMBptr->feuille.type==_VECT && v[1].type==_INT_ && v[1].val>=0){
      int k=v[1].val;
      if (!k)
	return plus_one;
      if (k==1)
	return base;
      vector<gen> factn(k+1);
      factn[0]=1;
      for (int i=1;i<=k;++i){
	factn[i]=i*factn[i-1];
      }
      // (x1+...+xn)^k -> sum_{j1+...+jn=k} k!/(j1!j2!...jn!) x^j1 *... *x^jk
      vecteur & w=*v[0]._SYMBptr->feuille._VECTptr;
      int n=w.size();
      if (!n)
	return gensizeerr(contextptr);
      vecteur res;
      gen p;
      for (int i=k;i>=0;--i){
	p=expand(pow(w[0],i),contextptr);
	pow_expand_add_res(factn,1,i,factn[k]/factn[i],w,p,k,n,res,contextptr);
      }
      return symbolic(at_plus,res);
    }
    return symb_pow(base,exponent);
  }

  static gen expand_neg_expand(const gen & g_orig,GIAC_CONTEXT){
    gen g=expand(g_orig,contextptr);
    return -g;
  }

  vecteur tchebycheff(int n,bool first_kind){
    vecteur v0,v1,vtmp;
    if (first_kind) {
      v0.push_back(1);
      v1.push_back(1);
      v1.push_back(0);
    }
    else
      v1.push_back(1);
    if (!n)
      return v0;
    if (n==1)
      return v1;
    for (--n;n;--n){
      multvecteur(2,v1,vtmp);
      vtmp.push_back(0);
      vtmp=vtmp-v0;
      v0=v1;
      v1=vtmp;
    }
    return v1; 
  }

  static gen exp_expand(const gen & e,GIAC_CONTEXT){
    if (e.type!=_SYMB)
      return exp(e,contextptr);
    if (e._SYMBptr->sommet==at_plus)
      return symbolic(at_prod,apply(e._SYMBptr->feuille,exp_expand,contextptr));
    gen coeff,arg;
    split(e,coeff,arg,contextptr);
    return pow(exp(arg,contextptr),coeff,contextptr);
  }

  static gen ln_expand0(const gen & e,GIAC_CONTEXT){
    if (e.type==_FRAC)
      return ln(e._FRACptr->num,contextptr)-ln(e._FRACptr->den,contextptr);
    if (e.type!=_SYMB)
      return ln(e,contextptr);
    if (e._SYMBptr->sommet==at_prod)
      return symbolic(at_plus,apply(e._SYMBptr->feuille,ln_expand0,contextptr));
    if (e._SYMBptr->sommet==at_inv)
      return -ln_expand0(e._SYMBptr->feuille,contextptr);
    if (e._SYMBptr->sommet==at_pow){
      gen & tmp=e._SYMBptr->feuille;
      if (tmp.type==_VECT && tmp._VECTptr->size()==2)
	return tmp._VECTptr->back()*ln_expand0(tmp._VECTptr->front(),contextptr);
    }
    return ln(e,contextptr);
  }

  gen ln_expand(const gen & e0,GIAC_CONTEXT){
    gen e(factor(e0,false,contextptr));
    return ln_expand0(e,contextptr);
  }

  gen symhorner(const vecteur & v, const gen & e){
    if (v.empty())
      return zero;
    if (is_zero(e))
      return v.back();
    gen res=zero;
    const_iterateur it=v.begin(),itend=v.end();
    int n=itend-it-1;
    for (;it!=itend;++it,--n)
      res = res + (*it)*pow(e,n);
    return res;
  }

  static gen cos_expand(const gen & e,GIAC_CONTEXT);
  static gen sin_expand(const gen & e,GIAC_CONTEXT){
    if (e.type!=_SYMB)
      return sin(e,contextptr);
    if (e._SYMBptr->sommet==at_plus){
      vecteur v=*e._SYMBptr->feuille._VECTptr;
      gen last=v.back(),first;
      v.pop_back();
      if (v.size()==1)
	first=v.front();
      else
	first=symbolic(at_plus,v);
      return sin_expand(first,contextptr)*cos_expand(last,contextptr)+cos_expand(first,contextptr)*sin_expand(last,contextptr);
    }
    if (e._SYMBptr->sommet==at_neg)
      return -sin_expand(e._SYMBptr->feuille,contextptr);
    gen coeff,arg;
    split(e,coeff,arg,contextptr);
    if (!is_one(coeff) && coeff.type==_INT_ && coeff.val<max_texpand_expansion_order)
      return symhorner(tchebycheff(coeff.val,false),cos(arg,contextptr))*sin(arg,contextptr);
    else
      return sin(e,contextptr);
  }

  static gen cos_expand(const gen & e,GIAC_CONTEXT){
    if (e.type!=_SYMB)
      return cos(e,contextptr);
    if (e._SYMBptr->sommet==at_plus){
      vecteur v=*e._SYMBptr->feuille._VECTptr;
      gen last=v.back(),first;
      v.pop_back();
      if (v.size()==1)
	first=v.front();
      else
	first=symbolic(at_plus,v);
      return cos_expand(first,contextptr)*cos_expand(last,contextptr)-sin_expand(first,contextptr)*sin_expand(last,contextptr);
    }
    if (e._SYMBptr->sommet==at_neg)
      return cos_expand(e._SYMBptr->feuille,contextptr);
    gen coeff,arg;
    split(e,coeff,arg,contextptr);
    if (!is_one(coeff) && coeff.type==_INT_ && coeff.val<max_texpand_expansion_order)
      return symhorner(tchebycheff(coeff.val,true),cos(arg,contextptr));
    else
      return cos(e,contextptr);
  }

  static gen sin2tancos(const gen & g,GIAC_CONTEXT){
    return symb_cos(g)*symb_tan(g);
  }

  static gen even_pow_cos2tan(const gen & g,GIAC_CONTEXT){
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
      return g;
    gen a(g._VECTptr->front()),b(g._VECTptr->back());
    if ( (b.type!=_INT_) || (b.val%2) || (a.type!=_SYMB) || (a._SYMBptr->sommet!=at_cos) )
      return symbolic(at_pow,g);
    int i=b.val/2;
    return pow(plus_one+pow(symb_tan(a._SYMBptr->feuille),plus_two,contextptr),-i,contextptr);
  }

  static gen tan_expand(const gen & e,GIAC_CONTEXT){
    if (e.type!=_SYMB)
      return tan(e,contextptr);
    if (e._SYMBptr->sommet==at_plus){
      vecteur v=*e._SYMBptr->feuille._VECTptr;
      gen last=v.back(),first;
      v.pop_back();
      if (v.size()==1)
	first=v.front();
      else
	first=symbolic(at_plus,v);
      gen ta=tan_expand(first,contextptr);
      gen tb=tan_expand(last,contextptr);
      return rdiv(ta+tb,1-ta*tb,contextptr);
    }
    if (e._SYMBptr->sommet==at_neg)
      return -tan_expand(e._SYMBptr->feuille,contextptr);
    gen coeff,arg;
    split(e,coeff,arg,contextptr);
    if (!is_one(coeff) && coeff.type==_INT_ && coeff.val<max_texpand_expansion_order){
      gen g=rdiv(symhorner(tchebycheff(coeff.val,false),cos(arg,contextptr))*sin(arg,contextptr),symhorner(tchebycheff(coeff.val,true),cos(arg,contextptr)),contextptr);
      vector<const unary_function_ptr *> v;
      vector< gen_op_context > w;
      v.push_back(at_sin);
      w.push_back(&sin2tancos);
      g=subst(g,v,w,false,contextptr);
      v[0]=at_pow;
      w[0]=(&even_pow_cos2tan);
      g=subst(normal(g,false,contextptr),v,w,false,contextptr);      
      return normal(g,false,contextptr);
    }
    else
      return tan(e,contextptr);
  }

  symbolic symb_texpand(const gen & a){
    return symbolic(at_texpand,a);
  }

  // "unary" version
  gen _texpand(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_texpand(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_texpand,contextptr);
    vector<const unary_function_ptr *> v;
    vector< gen_op_context > w;
    v.push_back(at_exp);
    w.push_back(&exp_expand);
    v.push_back(at_ln);
    w.push_back(&ln_expand);
    v.push_back(at_prod);
    w.push_back(&prod_expand);
    v.push_back(at_sin);
    w.push_back(&sin_expand);
    v.push_back(at_cos);
    w.push_back(&cos_expand);
    v.push_back(at_tan);
    w.push_back(&tan_expand);
    return subst(args,v,w,false,contextptr);
  }
  static const char _texpand_s []="texpand";
  static define_unary_function_eval (__texpand,&_texpand,_texpand_s);
  define_unary_function_ptr5( at_texpand ,alias_at_texpand,&__texpand,0,true);

  static const char _developper_transcendant_s []="developper_transcendant";
  static define_unary_function_eval (__developper_transcendant,&_texpand,_developper_transcendant_s);
  define_unary_function_ptr5( at_developper_transcendant ,alias_at_developper_transcendant,&__developper_transcendant,0,true);

  gen expand(const gen & e,GIAC_CONTEXT){
    if (is_equal(e))
      return apply_to_equal(e,expand,contextptr);
    gen var,res;
    if (e.type!=_VECT && is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,expand(res,contextptr)));
    if (e.type==_VECT && e.subtype==_SEQ__VECT && e._VECTptr->size()==2){
      gen last=e._VECTptr->back();
      if (last.type==_STRNG || last.type==_FUNC){
	vector<const unary_function_ptr *> v;
	vector< gen_op_context > w;
	if (contains(last,gen(at_prod)) || (last.type==_STRNG && !strcmp(last._STRNGptr->c_str(),"*"))){ // expand * with no further simplification
	  v.push_back(at_prod);
	  w.push_back(prod_expand_nosimp);
	}
	if (contains(last,gen(at_ln))){
	  v.push_back(at_ln);
	  w.push_back(&ln_expand);
	}
	if (contains(last,gen(at_exp))){
	  v.push_back(at_exp);
	  w.push_back(&exp_expand);
	}
	if (contains(last,gen(at_sin))){ 
	  v.push_back(at_sin);
	  w.push_back(&sin_expand);
	}
	if (contains(last,gen(at_cos))){
	  v.push_back(at_cos);
	  w.push_back(&cos_expand);
	}
	if (contains(last,gen(at_tan))){
	  v.push_back(at_tan);
	  w.push_back(&tan_expand);
	}
	return subst(e._VECTptr->front(),v,w,false,contextptr);	
      }
    }
    vector<const unary_function_ptr *> v;
    vector< gen_op_context > w;
    v.push_back(at_prod);
    v.push_back(at_pow);
    v.push_back(at_neg);
    w.push_back(&prod_expand);
    w.push_back(&expand_pow_expand);
    w.push_back(&expand_neg_expand);
    return _simplifier(subst(e,v,w,false,contextptr),contextptr);
  }
  static const char _expand_s []="expand";
  symbolic symb_expand(const gen & args){
    return symbolic(at_expand,args);
  }
  static define_unary_function_eval (__expand,&giac::expand,_expand_s);
  define_unary_function_ptr( at_expand ,alias_at_expand ,&__expand);

  gen expexpand(const gen & e,GIAC_CONTEXT){
    if (is_equal(e))
      return apply_to_equal(e,expexpand,contextptr);
    gen var,res;
    if (is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,expexpand(res,contextptr)));
    vector<const unary_function_ptr *> v(1,at_exp);
    vector< gen_op_context > w(1,&exp_expand);
    return subst(e,v,w,false,contextptr);
  }
  static const char _expexpand_s []="expexpand";
  static define_unary_function_eval (__expexpand,&giac::expexpand,_expexpand_s);
  define_unary_function_ptr5( at_expexpand ,alias_at_expexpand,&__expexpand,0,true);

  gen lnexpand(const gen & e,GIAC_CONTEXT){
    if (is_equal(e))
      return apply_to_equal(e,lnexpand,contextptr);
    gen var,res;
    if (is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,lnexpand(res,contextptr)));
    vector<const unary_function_ptr *> v(1,at_ln);
    vector< gen_op_context > w(1,&ln_expand);
    return subst(e,v,w,false,contextptr);
  }
  static const char _lnexpand_s []="lnexpand";
  static define_unary_function_eval (__lnexpand,&giac::lnexpand,_lnexpand_s);
  define_unary_function_ptr5( at_lnexpand ,alias_at_lnexpand,&__lnexpand,0,true);

  gen trigexpand(const gen & e,GIAC_CONTEXT){
    if (is_equal(e))
      return apply_to_equal(e,trigexpand,contextptr);
    gen var,res;
    if (is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,trigexpand(res,contextptr)));
    vector<const unary_function_ptr *> v;
    vector< gen_op_context > w;
    v.push_back(at_sin);
    w.push_back(&sin_expand);
    v.push_back(at_cos);
    w.push_back(&cos_expand);
    v.push_back(at_tan);
    w.push_back(&tan_expand);
    v.push_back(at_prod);
    w.push_back(&prod_expand);    
    return subst(e,v,w,false,contextptr);
  }
  static const char _trigexpand_s []="trigexpand";
  static define_unary_function_eval (__trigexpand,&giac::trigexpand,_trigexpand_s);
  define_unary_function_ptr5( at_trigexpand ,alias_at_trigexpand,&__trigexpand,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
