// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c -Wall modpoly.cc  -DHAVE_CONFIG_H -DIN_GIAC" -*-
// N.B.: compiling with g++-3.4 -O2 -D_I386_ does not work
#include "giacPCH.h"
/*  Univariate dense polynomials including modular arithmetic
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "sym2poly.h"
#include "modpoly.h"
#include "usual.h"
#include "prog.h"
#include "derive.h"
#include "ezgcd.h"
#include "giacintl.h"
#include <stdlib.h>
#include <cmath>
#include <stdexcept>
#include <string.h>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // random modular number
  gen nrandom(environment * env){
    if (env->moduloon){
      double d=env->modulo.to_int();
      int j=(int) (d*rand()/(RAND_MAX+1.0));
      return smod(gen(j),env->modulo);
    }
    else {
      double d=env->pn.to_int();
      int j=(int) (d*rand()/(RAND_MAX+1.0));
      return env->coeff.makegen(j);
    }
  }

  gen invenv(const gen & g,environment * env){
    if (g.type==_USER)
      return g._USERptr->inv();
    return invmod(g,env->modulo);
  }

  /*
  void inpowmod(const gen & a,int n,const gen & m,gen & res){
    if (!n){
      res=gen(1);
      return ;
    }
    if (n==1){
      res=a;
      return ;
    }
    inpowmod(a,n/2,m,res);
    res=smod((res*res),m);
    if (n%2)
      res=smod((res*a),m);
  }

  gen powmod(const gen & a,int n,const gen & m){
    if (!n)
      return 1;
    if (n==1)
      return a;
    assert(n>1);
    gen res;
    inpowmod(a,n,m,res);
    return res;
  }
  */

  modpoly derivative(const modpoly & p){
    if (p.empty())
      return p;
    modpoly new_coord;
    int d=p.size()-1;
    new_coord.reserve(d);
    modpoly::const_iterator it=p.begin(); // itend=p.end(),
    for (;d;++it,--d)
      new_coord.push_back((*it)*gen(d));
    return new_coord;
  }

  modpoly derivative(const modpoly & p,environment * env){
    if (p.empty())
      return p;
    modpoly new_coord;
    int d=p.size()-1;
    new_coord.reserve(d);
    modpoly::const_iterator it=p.begin(); // itend=p.end(),
    gen n0( 0);
    for (;d;++it,--d)
      if ( smod((*it)*gen(d),env->modulo)!=n0 )
	break;
    for (;d;++it,--d)
      new_coord.push_back( smod((*it)*gen(d),env->modulo) );
    return new_coord;
  }

  modpoly integrate(const modpoly & p,const gen & shift_coeff){
    if (p.empty())
      return p;
    modpoly new_coord;
    new_coord.reserve(p.size());
    modpoly::const_iterator itend=p.end(),it=p.begin();
    for (int d=0;it!=itend;++it,++d)
      new_coord.push_back(normal(rdiv((*it),gen(d)+shift_coeff,context0),context0));
    return new_coord;
  }
  

  static bool is_rational(double d,int & num,int & den,double eps){
    double dcopy(d);
    // continued fraction expansion
    vector<int> v;
    for (int n=1;n<11;++n){
      v.push_back(int(d));
      d=d-int(d);
      if (fabs(d)<eps*n)
	break;
      d=1/d;
    }
    // re_VECTose fraction
    num=0;
    den=1;
    reverse(v.begin(),v.end());
    for (vector<int>::const_iterator it=v.begin();it!=v.end();++it){
      num=num+den*(*it);
      swap(num,den);
    }
    swap(num,den);
    return fabs(dcopy-(num*1.0)/den)<eps;
  }


  // return n such that p=phi_n, p is assumed to be irreducible
  // return 0 if p is not cyclotomic
  int is_cyclotomic(const modpoly & p,double eps){
    modpoly q; gen e;
    modpoly::const_iterator itend=p.end(),it=p.begin();
    for (;it!=itend;++it){
      if (it->type==_POLY){
	if (it->_POLYptr->coord.empty())
	  e=zero;
	else {
	  if (Tis_constant<gen>(*it->_POLYptr))
	    e=it->_POLYptr->coord.front().value;
	  else
	    return 0;
	}
      }
      else
	e=*it;
      if (e.type!=_INT_)
	return 0;
      q.push_back(e);
    }
    // q has integer coeff, q(X) must be = X^n conj(q(1/conj(X)))
    // if it has all its root over the unit circle
    // since q has integer coeff, q=X^n*q(1/X) i.e. is symmetric
    modpoly qs(q);
    reverse(q.begin(),q.end());
    if (q!=qs)
      return 0;
    // find arg of a root and compare to 2*pi
    gen r=a_root(qs,0,eps);
    if (is_undef(r)) return 0;
    double arg_d=evalf_double(arg(r,context0),1,context0)._DOUBLE_val;
    double d=2*M_PI/ arg_d;
    // find rational approx of d
    int num,den;
    if (!is_rational(d,num,den,eps))
      return 0;
    if (p==cyclotomic(num))
      return num;
    else
      return 0;
  }
  int is_cyclotomic(const modpoly & p,GIAC_CONTEXT){
    return is_cyclotomic(p,epsilon(contextptr)); 
  }
  // use 0 for Z, n!=0 for Z/nZ
  modpoly modularize(const polynome & p,const gen & n,environment * env){
    bool ismod;
    if (env && env->coeff.type!=_USER && !is_zero(n)){
      env->modulo=n;
      env->pn=env->modulo;
      ismod=true;
      env->moduloon=true;
    }
    else
      ismod=false;
    gen n0(0);
    vecteur v;
    if (p.dim!=1) return vecteur(1,gensizeerr(gettext("modpoly.cc/modularize")));
    if (p.coord.empty())
      return v;
    int deg=p.lexsorted_degree();
    int curpow=deg;
    v.reserve(deg+1);
    vector< monomial<gen> >::const_iterator it=p.coord.begin();
    vector< monomial<gen> >::const_iterator itend=p.coord.end();
    for (;it!=itend;++it){
      int newpow=it->index.front();
      for (;curpow>newpow;--curpow)
	v.push_back(n0);
      if (ismod)
	v.push_back(smod(it->value,env->modulo));
      else
	v.push_back(it->value);
      --curpow;
    }
    for (;curpow>-1;--curpow)
      v.push_back(n0);      
    return v;
  }

  modpoly modularize(const dense_POLY1 & p,const gen & n,environment * env){
    env->modulo=n;
    env->pn=env->modulo;
    env->moduloon=true;
    if (p.empty())
      return p;
    modpoly v;
    gen n0( 0);
    dense_POLY1::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (smod(*it,n)!=n0)
	break;
    }
    for (;it!=itend;++it)
      v.push_back(smod(*it,n));
    return v;
  }

  polynome unmodularize(const modpoly & a){
    if (a.empty())
      return polynome(1);
    vector< monomial<gen> > v;
    index_t i;
    int deg=a.size()-1;
    i.push_back(deg);
    vecteur::const_iterator it=a.begin();
    vecteur::const_iterator itend=a.end();
    gen n0( 0);
    for (;it!=itend;++it,--i[0]){
      if (*it!=n0)
	v.push_back(monomial<gen>(*it,i));
    }
    return polynome(1,v);
  }

  // random polynomial of degree =i
  modpoly random(int i,environment * env){
    vecteur v;
    v.reserve(i+1);
    gen e;
    do
      e=nrandom(env);
    while
      (is_zero(e));
    v.push_back(e);
    for (int j=1;j<=i;j++)
      v.push_back(nrandom(env));
    return v;
  }

  bool is_one(const modpoly & p){
    if (p.size()!=1)
      return false;
    return (is_one(p.front()));
  }

  // 1
  modpoly one(){
    vecteur v;
    v.push_back(gen(1));
    return v;
  }

  // x=x^1
  modpoly xpower1(){
    vecteur v;
    v.push_back(gen( 1));
    v.push_back(gen( 0));
    return v;
  }

  bool normalize_env(environment * env){
    if (env->moduloon || is_zero(env->pn)){
      env->pn=env->modulo;
      if (env->complexe)
	env->pn = env->pn * env->pn ;
    }
    return (env->pn.type==_INT_);
  }

  // x^modulo
  modpoly xpowerpn(environment * env){
    if (!normalize_env(env))
      return vecteur(1,gendimerr(gettext("Field too large")));
    int deg=env->pn.val;
    vecteur v(deg+1);
    v[0]=1;
    return v;
  }

  // x -> x^p (non modular)
  vecteur x_to_xp(const vecteur & v, int p){
    if (p<=0) return vecteur(1,gensizeerr(gettext("modpoly.cc/x_to_xp")));
    if ( (p==1) || v.empty())
      return v;
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res;
    res.reserve(1+(itend-it-1)*p);
    res.push_back(*it);
    ++it;
    for (;it!=itend;++it){
      for (int i=1;i<p;++i)
	res.push_back(zero);
      res.push_back(*it);
    }
    return res;
  }

  // multiply by x^n
  void shiftmodpoly(modpoly & a,int n){
    a.reserve(a.size()+n);
    for (int i=0;i<n;i++)
      a.push_back(0);
  }

  // high = high*x^n + low, size of low must be < n
  void mergemodpoly(modpoly & high,const modpoly & low,int n){
    int l=low.size();
    for (int i=0;i<n-l;i++)
      high.push_back(0);
    modpoly::const_iterator it=low.begin(), itend=low.end();
    for (;it!=itend;++it)
      high.push_back(*it);
  }

  gen cstcoeff(const modpoly & q){
    modpoly::const_iterator it=q.end();
    --it;
    return *it;
  }

  // !! Do not call with modpoly slices if new_coord and th/other overlapp
  void Addmodpoly(modpoly::const_iterator th_it,modpoly::const_iterator th_itend,modpoly::const_iterator other_it,modpoly::const_iterator other_itend,environment * env, modpoly & new_coord){
    int n=(th_itend-th_it);
    int m=(other_itend-other_it);
    if (m>n){ // swap th and other in order to have n>=m
      modpoly::const_iterator tmp=th_it;
      th_it=other_it;
      other_it=tmp;
      tmp=th_itend;
      th_itend=other_itend;
      other_itend=tmp;
      int saven=n;
      n=m;
      m=saven;
    }
    if (m && other_it==new_coord.begin()){
      modpoly temp(new_coord);
      Addmodpoly(th_it,th_itend,temp.begin(),temp.end(),env,new_coord);
      return;
    }
    if (n && (th_it==new_coord.begin()) ){
      modpoly::iterator th=new_coord.begin()+n-m;
      bool trim=(n==m);
      // in-place addition
      if (env && env->moduloon)
	for (;m;++th,++other_it,--m)
	  *th=smod((*th)+(*other_it), env->modulo);
      else
	for (;m;++th,++other_it,--m)
	  *th += (*other_it);
      if (trim){ 
	for (th=new_coord.begin();th!=th_itend;++th){
	  if (!is_zero(*th))
	    break;
	}
	new_coord.erase(new_coord.begin(),th);
      }
      return;
    }
    new_coord.clear();
    if ( (n<0) || (m<0) )
      return ;
    new_coord.reserve(n);
    if (n>m){ // no trimming needed
      for (;n>m;++th_it,--n)
	new_coord.push_back(*th_it);
    }
    else { // n==m, first remove all 0 terms of the sum
      if (env && env->moduloon)
	for (;n && is_zero(smod((*th_it)+(*other_it), env->modulo));++th_it,++other_it,--n)
	  ;
      else
	for (;n && is_zero(*th_it+*other_it);++th_it,++other_it,--n)
	  ;
    }
    // finish addition
    if (env && env->moduloon)
      for (;n;++th_it,++other_it,--n)
	new_coord.push_back(smod((*th_it)+(*other_it), env->modulo));
    else
      for (;n;++th_it,++other_it,--n)
	new_coord.push_back( *th_it+(*other_it) );
  }

  void addmodpoly(const modpoly & th, const modpoly & other, environment * env,modpoly & new_coord){
    // assert( (&th!=&new_coord) && (&other!=&new_coord) );
    modpoly::const_iterator th_it=th.begin(),th_itend=th.end();
    modpoly::const_iterator other_it=other.begin(),other_itend=other.end();
    Addmodpoly(th_it,th_itend,other_it,other_itend,env,new_coord);
  }

  void addmodpoly(const modpoly & th, const modpoly & other, modpoly & new_coord){
    // assert( (&th!=&new_coord) && (&other!=&new_coord) );
    modpoly::const_iterator th_it=th.begin(),th_itend=th.end();
    modpoly::const_iterator other_it=other.begin(),other_itend=other.end();
    environment * env=new environment;
    Addmodpoly(th_it,th_itend,other_it,other_itend,env,new_coord);
    delete env;
  }
  
  // modular polynomial arithmetic: gcd, egcd, simplify
  modpoly operator_plus (const modpoly & th,const modpoly & other,environment * env) {
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor addition
    if (th.empty())
      return other;
    if (other.empty())
      return th;
    modpoly new_coord;
    addmodpoly(th,other,env,new_coord);
    return new_coord;
  } 

  modpoly operator + (const modpoly & th,const modpoly & other) {
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor addition
    if (th.empty())
      return other;
    if (other.empty())
      return th;
    modpoly new_coord;
    addmodpoly(th,other,new_coord);
    return new_coord;
  } 


  void Submodpoly(modpoly::const_iterator th_it,modpoly::const_iterator th_itend,modpoly::const_iterator other_it,modpoly::const_iterator other_itend,environment * env,modpoly & new_coord){
    int n=(th_itend-th_it);
    if (!n){
      new_coord=modpoly(other_it,other_itend);
      mulmodpoly(new_coord,-1,new_coord);
      return;
    }
    int m=(other_itend-other_it);
    if (th_it==new_coord.begin()){
      if (n<m){
	modpoly temp(new_coord);
	Submodpoly(temp.begin(),temp.end(),other_it,other_itend,env,new_coord);
	return;
      }
      else {
	modpoly::iterator th=new_coord.begin()+n-m;
	bool trim=(n==m);
	// in-place -
	if (env && env->moduloon)
	  for (;m;++th,++other_it,--m)
	    *th=smod((*th)-(*other_it), env->modulo);
	else
	  for (;m;++th,++other_it,--m)
	    *th -= (*other_it);
	if (trim){ 
	  for (th=new_coord.begin();th!=th_itend;++th){
	    if (!is_zero(*th))
	      break;
	  }
	  new_coord.erase(new_coord.begin(),th);
	}
      }
      return;
    }
    if (m && (other_it==new_coord.begin()) ){
      bool inplace=(m>n);
      if (n==m){ // look if highest coeff vanishes
	if (env && env->moduloon)
	  inplace=!is_zero(smod((*th_it)-(*other_it), env->modulo));
	else
	  inplace=!is_zero((*th_it)-(*other_it));
      }
      if (inplace){ // in-place substraction
	modpoly::iterator th=new_coord.begin();
	for (;m>n;++th,--m)
	  *th=-(*th);
	if (env && env->moduloon)
	  for (;m;++th_it,++th,--m)
	    *th=smod((*th_it)-(*th), env->modulo);
	else
	  for (;m;++th_it,++th,--m)
	    *th=(*th_it)-(*th);
	return;
      }
      else { // copy new_coord to a temporary and call again Addmodpoly
	modpoly temp(new_coord);
	Submodpoly(th_it,th_itend,temp.begin(),temp.end(),env,new_coord);
	return;
      }
    }
    if ( (n<0) || (m<0) )
      return ;
    new_coord.clear();
    new_coord.reserve(giacmax(n,m));
    bool trimming;
    if (m==n)
      trimming=true;
    else
      trimming=false;
    for (;m>n;++other_it,--m)
      new_coord.push_back(-*other_it);
    for (;n>m;++th_it,--n)
      new_coord.push_back(*th_it);
    if (env && env->moduloon)
      for (;n;++th_it,++other_it,--n){
	gen tmp=smod((*th_it)-(*other_it), env->modulo);
	if ( trimming){ 
	  if (!is_zero(tmp)){
	    trimming=false;
	    new_coord.push_back(tmp);
	  }
	}
	else 
	  new_coord.push_back(tmp);
      }
    else
      for (;n;++th_it,++other_it,--n){
	gen tmp=(*th_it)-(*other_it);
	if ( trimming){ 
	  if (!is_zero(tmp)){
	    trimming=false;
	    new_coord.push_back(tmp);
	  }	
	}
	else 
	  new_coord.push_back(tmp);
      }
  }

  void submodpoly(const modpoly & th, const modpoly & other, environment * env,modpoly & new_coord){
    // assert( (&th!=&new_coord) && (&other!=&new_coord) );
    modpoly::const_iterator th_it=th.begin(),th_itend=th.end();
    modpoly::const_iterator other_it=other.begin(),other_itend=other.end();
    Submodpoly(th_it,th_itend,other_it,other_itend,env,new_coord);
  }

  void submodpoly(const modpoly & th, const modpoly & other, modpoly & new_coord){
    // assert( (&th!=&new_coord) && (&other!=&new_coord) );
    modpoly::const_iterator th_it=th.begin(),th_itend=th.end();
    modpoly::const_iterator other_it=other.begin(),other_itend=other.end();
    environment * env=new environment;
    Submodpoly(th_it,th_itend,other_it,other_itend,env,new_coord);
    delete env;
  }

  modpoly operator_minus (const modpoly & th,const modpoly & other,environment * env) {  
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor sub
    if (th.empty())
      return -other;
    if (other.empty())
      return th;    
    modpoly new_coord;
    submodpoly(th,other,env,new_coord);
    return new_coord;
  }

  modpoly operator - (const modpoly & th,const modpoly & other) {  
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor sub
    if (th.empty())
      return -other;
    if (other.empty())
      return th;    
    modpoly new_coord;
    submodpoly(th,other,new_coord);
    return new_coord;
  } 

  void mulmodpoly(const modpoly & th, const gen & fact,environment * env, modpoly & new_coord){
    if (!env || !env->moduloon){
      mulmodpoly(th,fact,new_coord);
      return;
    }
    if (is_zero(fact)){
      new_coord.clear();
      return ;
    }
    if (&th==&new_coord){
      if (is_one(fact))
	return;
      modpoly::iterator it=new_coord.begin(),itend=new_coord.end();
      if (!env->complexe && (env->modulo.type==_INT_) && (fact.type==_INT_) && (env->modulo.val<smallint) && (fact.val<smallint)){
	for (;it!=itend;++it)
	  it->val=smod( (it->val)*fact.val,env->modulo.val ) ;
      }
      else {
	for (;it!=itend;++it)
	  *it=smod( (*it)*fact,env->modulo);
      }
    }
    else { // &th!=&new_coord
      new_coord.clear();
      new_coord.reserve(th.size());
      modpoly::const_iterator it=th.begin(),itend=th.end();
      if (!env->complexe && (env->modulo.type==_INT_) && (fact.type==_INT_) && (env->modulo.val<smallint) && (fact.val<smallint)){
	for (;it!=itend;++it)
	  new_coord.push_back(smod( (it->val)*fact.val,env->modulo.val) );
      }
      else {
	for (;it!=itend;++it)
	  new_coord.push_back(smod( (*it)*fact,env->modulo) );
      }
    }
  }

  void mulmodpoly(const modpoly & th, const gen & fact, modpoly & new_coord){
    if (is_zero(fact)){
      new_coord.clear();
      return ;
    }
    if (&th==&new_coord){
      if (is_one(fact))
	return;
      modpoly::iterator it=new_coord.begin(),itend=new_coord.end();
      for (;it!=itend;++it)
	*it= (*it)*fact;
    }
    else { // &th!=&new_coord
      new_coord.clear();
      new_coord.reserve(th.size());
      modpoly::const_iterator it=th.begin(),itend=th.end();
      for (;it!=itend;++it)
	new_coord.push_back((*it)*fact);
    }
  } 

  modpoly operator * (const modpoly & th, const gen & fact){
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor constant multiplication
    if (is_one(fact))
      return th;
    modpoly new_coord;
    mulmodpoly(th,fact,new_coord);
    return new_coord;
  }

  modpoly operator * (const gen & fact,const modpoly & th){
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    if (is_one(fact))
      return th;
    modpoly new_coord;
    mulmodpoly(th,fact,new_coord);
    return new_coord;
  }

  modpoly operator * (const modpoly & a, const modpoly & b) {
    environment * env=new environment;
    modpoly temp(operator_times(a,b,env));
    delete env;
    return temp;
  }
  

  modpoly operator_times (const modpoly & th, const gen & fact,environment * env){
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    // Tensor constant multiplication
    if (is_one(fact))
      return th;
    modpoly new_coord;
    mulmodpoly(th,fact,env,new_coord);
    return new_coord;
  }

  modpoly operator_times (const gen & fact,const modpoly & th,environment * env){
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      return modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
    }
    if (is_one(fact))
      return th;
    modpoly new_coord;
    mulmodpoly(th,fact,env,new_coord);
    return new_coord;
  }

  // *res = *res + a*b, *res must not be elsewhere referenced
  inline void add_mul(mpz_t * res,mpz_t & prod,const gen &a,const gen &b){
    switch ( (a.type<< _DECALAGE) | b.type) {
    case _INT___INT_:
      mpz_set_si(prod,a.val);
#ifdef mpz_mul_si
      mpz_mul_si(prod,prod,b.val);
#else
      if (b.val<0){
	mpz_mul_ui(prod,prod,-b.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,prod,b.val);
#endif
      break;
    case _ZINT__ZINT:
      mpz_mul(prod,*a._ZINTptr,*b._ZINTptr);
      break;
    case _INT___ZINT:
#ifdef mpz_mul_si
      mpz_mul_si(prod,*b._ZINTptr,a.val);
#else
      if (a.val<0){
	mpz_mul_ui(prod,*b._ZINTptr,-a.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,*b._ZINTptr,a.val);
#endif
      break;
    case _ZINT__INT_:
#ifdef mpz_mul_si
      mpz_mul_si(prod,*a._ZINTptr,b.val);
#else
      if (b.val<0){
	mpz_mul_ui(prod,*a._ZINTptr,-b.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,*a._ZINTptr,b.val);
#endif
      break;
    }
    mpz_add(*res,*res,prod);
  }

  // *res = *res - a*b, *res must not be referenced elsewhere
  inline void sub_mul(mpz_t * res,mpz_t & prod,const gen &a,const gen &b){
    switch ( (a.type<< _DECALAGE) | b.type) {
    case _INT___INT_:
      mpz_set_si(prod,a.val);
#ifdef mpz_mul_si
      mpz_mul_si(prod,prod,b.val);
#else
      if (b.val<0){
	mpz_mul_ui(prod,prod,-b.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,prod,b.val);
#endif
      break;
    case _ZINT__ZINT:
      mpz_mul(prod,*a._ZINTptr,*b._ZINTptr);
      break;
    case _INT___ZINT:
#ifdef mpz_mul_si
      mpz_mul_si(prod,*b._ZINTptr,a.val);
#else
      if (a.val<0){
	mpz_mul_ui(prod,*b._ZINTptr,-a.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,*b._ZINTptr,a.val);
#endif
      break;
    case _ZINT__INT_:
#ifdef mpz_mul_si
      mpz_mul_si(prod,*a._ZINTptr,b.val);
#else
      if (b.val<0){
	mpz_mul_ui(prod,*a._ZINTptr,-b.val);
	mpz_neg(prod,prod);
      }
      else
	mpz_mul_ui(prod,*a._ZINTptr,b.val);
#endif
      break;
    }
    mpz_sub(*res,*res,prod);
  }

  static void Muldense_POLY1(const modpoly::const_iterator & ita0,const modpoly::const_iterator & ita_end,const modpoly::const_iterator & itb0,const modpoly::const_iterator & itb_end,environment * env,modpoly & new_coord){
    mpz_t prod;
    mpz_init(prod);
    new_coord.clear();
    if (ita0==ita_end || itb0==itb_end) return;
    modpoly::const_iterator ita_begin=ita0,ita=ita0,itb=itb0;
    for ( ; ita!=ita_end; ++ita ){
      ref_mpz_t * res = new ref_mpz_t; 
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	add_mul(&res->z,prod,*ita_cur,*itb_cur); // res = res + (*ita_cur) * (*itb_cur);
	if (ita_cur==ita_begin)
	  break;
      }
      if (env && env->moduloon)
	new_coord.push_back(smod(gen(res),env->modulo));
      else
	new_coord.push_back(gen(res));	
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      ref_mpz_t * res = new ref_mpz_t;
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	add_mul(&res->z,prod,*ita_cur,*itb_cur); // res=res+((*ita_cur)) * ((*itb_cur));
	if (ita_cur==ita_begin)
	  break;
      }
      if (env && env->moduloon)
	new_coord.push_back(smod(gen(res),env->modulo));
      else
	new_coord.push_back(gen(res));	
    }
    mpz_clear(prod);
  }

  // new_coord memory must be reserved, Mulmodpoly clears new_coord
  static void Mulmodpolymod(modpoly::const_iterator ita,modpoly::const_iterator ita_end,modpoly::const_iterator itb,modpoly::const_iterator itb_end,environment * env,modpoly & new_coord){
    int a=(ita_end-ita);
    int b=(itb_end-itb);
    if (!b)
      return ;
    if ( ( a <= KARAMUL_SIZE) || ( b <= KARAMUL_SIZE) ){
      Muldense_POLY1(ita,ita_end,itb,itb_end,env,new_coord);
      return ;
    }
    if (a<b){
      Mulmodpolymod(itb,itb_end,ita,ita_end,env,new_coord);
      return;
    }
    int mid=(a+1)/2;
    modpoly::const_iterator ita_mid=ita_end-mid;
    if (mid>=b){ // cut A in a/b+1 parts
      int nslices=a/b; // number of submultiplications -1
      ita_mid=ita+b;
      Mulmodpolymod(itb,itb_end,ita,ita_mid,env,new_coord); // initialization
      modpoly low;
      low.reserve(b*b);
      for (int i=1;i<nslices;i++){
	ita=ita_mid;
	ita_mid=ita_mid+b;
	shiftmodpoly(new_coord,b);
	Mulmodpolymod(itb,itb_end,ita,ita_mid,env,low);
	addmodpoly(new_coord,low,env,new_coord);
      }
      // last multiplication
      mid=a%b;
      if (mid){
	shiftmodpoly(new_coord,mid);
	Mulmodpolymod(itb,itb_end,ita_mid,ita_end,env,low);
	addmodpoly(new_coord,low,env,new_coord);	
      }
      return ;
    }
    // cut A and B in two parts
    // A=A_low+x^mid*A_high, B=B_low+x^mid*B_high
    // A*B = A_low*B_low + x^[2*mid]* A_high*B_high
    //     + x^mid* [ (A_low+A_high)*(B_low+B_high)-A_low*B_low-A_high*B_high ]
    modpoly lowlow, Aplus, Bplus, lowhigh;
    modpoly::const_iterator itb_mid=itb_end-mid;
    lowlow.reserve(3*mid);
    Mulmodpolymod(ita_mid,ita_end,itb_mid,itb_end,env,lowlow);
    // cout << "lowlow" << lowlow << endl;
    // new_coord.reserve(2*mid);
    Mulmodpolymod(ita,ita_mid,itb,itb_mid,env,new_coord);
    // cout << "new_coord" << new_coord << endl;
    lowhigh.reserve(2*mid);
    Addmodpoly(ita,ita_mid,ita_mid,ita_end,env,Aplus);
    Addmodpoly(itb,itb_mid,itb_mid,itb_end,env,Bplus);
    modpoly::const_iterator itap=Aplus.begin(),itap_end=Aplus.end();
    modpoly::const_iterator itbp=Bplus.begin(),itbp_end=Bplus.end();
    Mulmodpolymod(itap,itap_end,itbp,itbp_end,env,lowhigh);
    // cout << "lowhigh" << lowhigh << endl;
    submodpoly(lowhigh,new_coord,env,lowhigh);
    mergemodpoly(new_coord,lowlow,2*mid);
    submodpoly(lowhigh,lowlow,env,lowlow);
    // cout << "lowh-hh-ll" << lowlow << endl;
    shiftmodpoly(lowlow,mid);
    addmodpoly(new_coord,lowlow,env,new_coord);
    // modpoly verif;
    // Muldense_POLY1(ita,ita_end,itb,itb_end,env,verif);
    // cout << "newcoord" << new_coord << "=?" << verif << endl;
  }


  inline void Muldensemodpolysmall(const modpoly::const_iterator & ita0,const modpoly::const_iterator & ita_end,const modpoly::const_iterator & itb0,const modpoly::const_iterator & itb_end,environment * env,modpoly & new_coord){  
    new_coord.clear();
    if (ita0==ita_end || itb0==itb_end) return;
    modpoly::const_iterator ita_begin=ita0,ita=ita0,itb=itb0;
    for ( ; ita!=ita_end; ++ita ){
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      int res=0;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	res += ita_cur->val * itb_cur->val ;
	if (ita_cur==ita_begin)
	  break;
      }
      if (env && env->moduloon)
	new_coord.push_back(smod(res,env->modulo.val));
      else
	new_coord.push_back(res);
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      int res= 0;
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      for (;;) {
	res += ita_cur->val * itb_cur->val ;
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      if (env && env->moduloon)
	new_coord.push_back(smod(res,env->modulo.val));
      else
	new_coord.push_back(res);
    }
  }

  static void Mulmodpolysmall(modpoly::const_iterator & ita,modpoly::const_iterator & ita_end,modpoly::const_iterator & itb,modpoly::const_iterator & itb_end,environment * env,modpoly & new_coord){
    int a=(ita_end-ita);
    int b=(itb_end-itb);
    if (!b)
      return ;
    if ( ( a <= INT_KARAMUL_SIZE) || ( b <= INT_KARAMUL_SIZE) ){
      Muldensemodpolysmall(ita,ita_end,itb,itb_end,env,new_coord);
      return ;
    }
    if (a<b){
      Mulmodpolysmall(itb,itb_end,ita,ita_end,env,new_coord);
      return;
    }
    int mid=(a+1)/2;
    modpoly::const_iterator ita_mid=ita_end-mid;
    if (mid>=b){ // cut A in a/b+1 parts
      int nslices=a/b; // number of submultiplications -1
      ita_mid=ita+b;
      Mulmodpolysmall(itb,itb_end,ita,ita_mid,env,new_coord); // initialization
      modpoly low;
      low.reserve(b*b);
      for (int i=1;i<nslices;i++){
	ita=ita_mid;
	ita_mid=ita_mid+b;
	shiftmodpoly(new_coord,b);
	Mulmodpolysmall(itb,itb_end,ita,ita_mid,env,low);
	addmodpoly(new_coord,low,env,new_coord);
      }
      // last multiplication
      mid=a%b;
      if (mid){
	shiftmodpoly(new_coord,mid);
	Mulmodpolysmall(itb,itb_end,ita_mid,ita_end,env,low);
	addmodpoly(new_coord,low,env,new_coord);	
      }
      return ;
    }
    // cut A and B in two parts
    // A=A_low+x^mid*A_high, B=B_low+x^mid*B_high
    // A*B = A_low*B_low + x^[2*mid]* A_high*B_high
    //     + x^mid* [ (A_low+A_high)*(B_low+B_high)-A_low*B_low-A_high*B_high ]
    modpoly lowlow, Aplus, Bplus, lowhigh;
    modpoly::const_iterator itb_mid=itb_end-mid;
    lowlow.reserve(3*mid);
    Mulmodpolysmall(ita_mid,ita_end,itb_mid,itb_end,env,lowlow);
    // cout << "lowlow" << lowlow << endl;
    // new_coord.reserve(2*mid);
    Mulmodpolysmall(ita,ita_mid,itb,itb_mid,env,new_coord);
    // cout << "new_coord" << new_coord << endl;
    lowhigh.reserve(2*mid);
    Addmodpoly(ita,ita_mid,ita_mid,ita_end,env,Aplus);
    Addmodpoly(itb,itb_mid,itb_mid,itb_end,env,Bplus);
    modpoly::const_iterator itap=Aplus.begin(),itap_end=Aplus.end();
    modpoly::const_iterator itbp=Bplus.begin(),itbp_end=Bplus.end();
    Mulmodpolysmall(itap,itap_end,itbp,itbp_end,env,lowhigh);
    // cout << "lowhigh" << lowhigh << endl;
    submodpoly(lowhigh,new_coord,env,lowhigh);
    mergemodpoly(new_coord,lowlow,2*mid);
    submodpoly(lowhigh,lowlow,env,lowlow);
    // cout << "lowh-hh-ll" << lowlow << endl;
    shiftmodpoly(lowlow,mid);
    addmodpoly(new_coord,lowlow,env,new_coord);
  }

  // Warning: mulmodpoly assumes that coeff are integers
  void mulmodpoly(const modpoly & a, const modpoly & b, environment * env,modpoly & new_coord){
    if (a.empty() || b.empty()){
      new_coord.clear();
      return;
    }
    int as=a.size()-1;
    int bs=b.size()-1;
    if (!as){
      mulmodpoly(b,a.front(),env,new_coord);
      return;
    }
    if (!bs){
      mulmodpoly(a,b.front(),env,new_coord);
      return;
    }
    int product_deg=as+bs;
    if (&a==&new_coord){
      vecteur tmp;
      mulmodpoly(a,b,env,tmp);
      swap(tmp,new_coord);
      return;
      // setsizeerr(gettext("modpoly.cc/mulmodpoly"));
    }
    new_coord.reserve(product_deg+1);
    modpoly::const_iterator ita=a.begin(),ita_end=a.end(),itb=b.begin(),itb_end=b.end(); // ,ita_begin=a.begin()
    if ( env && (env->moduloon) && !env->complexe && (env->modulo.type==_INT_) && (env->modulo.val < smallint) && (product_deg < 65536) )
      Mulmodpolysmall(ita,ita_end,itb,itb_end,env,new_coord);
    else {
      if ( (!env || !env->moduloon ) && as>FFTMUL_SIZE && bs>FFTMUL_SIZE){
	// Check that all coeff are integers
	for (;ita!=ita_end;++ita){
	  if (!ita->is_integer())
	    break;
	}
	for (;itb!=itb_end;++itb){
	  if (!itb->is_integer())
	    break;
	}
	if (ita==ita_end && itb==itb_end){
	  cerr << "// fftmult" << endl;
	  fftmult(a,b,new_coord);
	  return ;
	}
	ita=a.begin();
	itb=b.begin();
      }
      if ((as<=KARAMUL_SIZE) && (bs<=KARAMUL_SIZE))
	Muldense_POLY1(ita,ita_end,itb,itb_end,env,new_coord);
      else
	Mulmodpolymod(ita,ita_end,itb,itb_end,env,new_coord);
    }
  }
  

  modpoly operator_times (const modpoly & a, const modpoly & b,environment * env) {
    // Multiplication
    // cout << a <<"*" << b << "[" << modulo << "]" << endl;
    if (a.empty())
      return a;
    if (b.empty())
      return b;
    modpoly new_coord;
    operator_times(a,b,env,new_coord);
    // cout << new_coord << endl;
    return new_coord;
  }

  void operator_times (const modpoly & a, const modpoly & b,environment * env,modpoly & new_coord) {
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      new_coord=modpoly(1,gensizeerr(gettext("Stopped by user interruption."))); 
      return;
    }
    if (env && env->moduloon){
      mulmodpoly(a,b,env,new_coord);
      return ;
    }
    modpoly::const_iterator ita=a.begin(),ita_end=a.end(),itb=b.begin(),itb_end=b.end();
    // Check that all coeff of a b are integers
    for (;ita!=ita_end;++ita){
      if (!ita->is_integer())
	break;
    }
    for (;itb!=itb_end;++itb){
      if (!itb->is_integer())
	break;
    }
    if (ita==ita_end && itb==itb_end){
      mulmodpoly(a,b,env,new_coord);
      return;
    }
    new_coord.clear();
    ita=a.begin(); itb=b.begin();
    if (ita==ita_end || itb==itb_end)
      return;
    modpoly::const_iterator ita_begin=ita;
    for ( ; ita!=ita_end; ++ita ){
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      gen res;
      for (;;) {
	res += (*ita_cur)*(*itb_cur); // res = res + (*ita_cur) * (*itb_cur);
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      new_coord.push_back(res);	
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      gen res;
      for (;;) {
	res += (*ita_cur)*(*itb_cur);
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      new_coord.push_back(res);	
    }
  }

  // res=(*it) * ... (*(it_end-1))
  void mulmodpoly(vector<modpoly>::const_iterator it,vector<modpoly>::const_iterator it_end,environment * env,modpoly & new_coord){
    int n=it_end-it;
    if (n>3){
      vector<modpoly>::const_iterator it_mid=it+(it_end-it)/2;
      modpoly first,second;
      mulmodpoly(it,it_mid,env,first);
      mulmodpoly(it_mid,it_end,env,second);
      mulmodpoly(first,second,env,new_coord);
      return ;
    }
    switch (n){
    case 0:
      return;
    case 1:
      new_coord=*it;
      return;
    case 2:
      operator_times(*it,*(it+1),env,new_coord);
      return;
    case 3:
      operator_times(*it,*(it+1),env,new_coord);
      new_coord=operator_times(*(it+2),new_coord,env);
      return ;
    }
  }

  void mulmodpoly(vector<modpoly>::const_iterator * it,int debut,int fin,environment * env,modpoly & pi){
    // pi = *(it[debut]);
    // for (int j=debut+1;j<=fin;j++){
    //  modpoly tmp;
    //  mulmodpoly(pi,*it[j],env,tmp);
    //  pi=tmp;
    // }
    //return ;
    if (fin-debut>2){
      int milieu=(debut+fin)/2;
      modpoly first,second;
      mulmodpoly(it,debut,milieu,env,first);
      mulmodpoly(it,milieu+1,fin,env,second);
      mulmodpoly(first,second,env,pi);
      return ;
    }
    switch (fin-debut){
    case 0:
      pi=*(it[debut]);
      break;
    case 1:
      operator_times(*(it[debut]),*(it[debut+1]),env,pi);
      break;
    case 2:
      operator_times(*(it[debut]),*(it[debut+1]),env,pi);
      pi=operator_times(pi,(*it[debut+2]),env);
      break;
    }
  }

  void negmodpoly(const modpoly & th, modpoly & new_coord){
    if (&th==&new_coord){
      modpoly::iterator a = new_coord.begin();
      modpoly::const_iterator a_end = new_coord.end();
      for (;a!=a_end;++a)
	*a=-(*a);
    }
    else {
      new_coord.reserve(th.size());
      modpoly::const_iterator a = th.begin();
      modpoly::const_iterator a_end = th.end();
      for (;a!=a_end;++a)
	new_coord.push_back(-(*a));
    }
  }

  modpoly operator - (const modpoly & th) {  
    // Negate
    modpoly new_coord;
    negmodpoly(th,new_coord);
    return new_coord;
  }

  // right redimension poly to degree n
  void rrdm(modpoly & p, int n){
    int s=p.size();
    if (s==n+1)
      return;
    for (;s>n+1;--s){ // remove trainling coeff
      p.pop_back();
    }
    for (;s<n+1;++s){ // add zeros coeff
      p.push_back(0);
    }
  }

  modpoly trim(const modpoly & p,environment * env){
    if (p.empty())
      return p;
    modpoly::const_iterator it=p.begin(),itend=p.end();
    gen n0( 0);
    if (env && env->moduloon)
      while ( (it!=itend) && (is_zero(smod(*it,env->modulo))) )
	++it;
    else
      while ( (it!=itend) && (is_zero(*it)) )
	++it;
    modpoly new_coord ;
    if (env && env->moduloon)
      for (;it!=itend;++it)
	new_coord.push_back(smod(*it,env->modulo));
    else
      for (;it!=itend;++it)
	new_coord.push_back(*it);
    return new_coord;
  }

  void divmodpoly(const modpoly & th, const gen & fact, modpoly & new_coord){
    if (is_one(fact)){
      if (&th!=&new_coord)
	new_coord=th;
      return ;
    }
    else {
      if (&th==&new_coord){
	modpoly::iterator it=new_coord.begin(),itend=new_coord.end();
	for (;it!=itend;++it)
	  //  *it =iquo(*it,fact);
	  *it=rdiv(*it,fact,context0);
      }
      else {
	modpoly::const_iterator it=th.begin(),itend=th.end();
	for (;it!=itend;++it)
	  new_coord.push_back(rdiv(*it,fact,context0)); // was iquo
	  // new_coord.push_back(iquo(*it,fact));
      }
    }
  }

  void divmodpoly(const modpoly & th, const gen & fact, environment * env,modpoly & new_coord){
    if (is_one(fact)){
      if (&th!=&new_coord)
	new_coord=th;
      return ;
    }
    if (!env || !env->moduloon)
      divmodpoly(th,fact,new_coord);
    else {
      gen factinv(invmod(fact,env->modulo));
      mulmodpoly(th,factinv,env,new_coord);
    }
  }

  modpoly operator / (const modpoly & th,const gen & fact ) {  
    if (is_one(fact))
      return th;
    modpoly new_coord;
    divmodpoly(th,fact,new_coord);
    return new_coord;
  }

  modpoly operator_div (const modpoly & th,const gen & fact,environment * env ) {  
    if (is_one(fact))
      return th;
    modpoly new_coord;
    divmodpoly(th,fact,env,new_coord);
    return new_coord;
  }

  bool DivRem(const modpoly & th, const modpoly & other, environment * env,modpoly & quo, modpoly & rem,bool allowrational){
    // cout << "DivRem" << th << "," << other << endl;
    if (other.empty()){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("modpoly.cc/DivRem"));
#endif
      return false;
    } 
    if (th.empty()){
      quo=th;
      rem=th;
      return true ;
    }
    int a=th.size()-1;
    int b=other.size()-1;
    if (other.size()==1){
      divmodpoly(th,other.front(),env,quo);
      rem.clear();
      return true ;
    }
    quo.clear();
    if (a<b){
      rem=th;
      return true;
    }
    quo.reserve(a-b+1);
    // A=BQ+R -> A=(B*invcoeff)*Q+(R*invcoeff), 
    // make division of A*coeff by B*coeff and multiply R by coeff at the end
    modpoly B;
    gen coeff=other.front(),invcoeff;
    if (env && env->moduloon){
      invcoeff=invmod(coeff,env->modulo);
      mulmodpoly(th,invcoeff,env,rem); // rem=th*invcoeff;
      mulmodpoly(other,invcoeff,env,B); // B=other*invcoeff;
    }
    else {
      rem=th;
      B=other;
    }
    // copy rem to an array
    modpoly::const_iterator remit=rem.begin(); // ,remend=rem.end();
    gen * tmp=new gen[a+1]; // must use new/delete
    gen * tmpend=&tmp[a];
    gen * tmpptr=tmpend; // tmpend points to the highest degree coeff of A
    /*
    vecteur vtmp(a+1);
    iterateur tmp=vtmp.begin();
    iterateur tmpend=vtmp.end()-1;
    iterateur tmpptr=tmpend; // tmpend points to the highest degree coeff of A
    */
    for (;tmpptr!=tmp-1;--tmpptr,++remit)
      *tmpptr=*remit;
    modpoly::const_iterator B_beg=B.begin(),B_end=B.end();
    gen n0( 0),q;
    for (;a>=b;--a){
      if (env && env->moduloon)
	q=smod(*tmpend,env->modulo);
      else {
	q=rdiv(*tmpend,coeff,context0);
	if (!allowrational){
	  if (q.type==_FRAC){
	    delete [] tmp;
	    return false;
	  }
	}
      }
      quo.push_back(q);
      --tmpend;
      bool fast=(env && (env->complexe || !env->moduloon) )?false:(q.type==_INT_) || (q.type==_ZINT);
      if (!is_zero(q)) {
	// tmp <- tmp - q *B.shifted
	tmpptr=tmpend;
	modpoly::const_iterator itq=B_beg;
	++itq; // first elements cancel
	if (env && (env->moduloon && !env->complexe) && (env->modulo.type==_INT_) && (env->modulo.val<smallint)){
	  for (;itq!=B_end;--tmpptr,++itq){ // no mod here to save comput. time
	    tmpptr->val -= q.val*itq->val ;
	  }	  
	}
	else {
	  mpz_t prod;
	  mpz_init(prod);
	  for (;itq!=B_end;--tmpptr,++itq){ // no mod here to save comput. time
	    if (fast && (tmpptr->type==_ZINT) && 
#ifndef SMARTPTR64
		(tmpptr->__ZINTptr->ref_count==1) && 
#else
		((ref_mpz_t *) (* (longlong *) tmpptr >> 16))->ref_count==1 &&
#endif
		( (itq->type==_ZINT) || (itq->type==_INT_) ) )
	      sub_mul(tmpptr->_ZINTptr,prod,q,*itq);
	    else
	      *tmpptr = (*tmpptr)-q*(*itq) ;
	  }
	  mpz_clear(prod);
	}
      }
      /*
      if (env && !env->moduloon) {
	cerr << quo << endl;
	cerr << quo*other << endl;
	cerr << "[";
	for (int i=1;i<a;++i)
	  cerr << tmp[a-i] << "," ;
	cerr << tmp[0] << "]" << endl;
	cerr << endl;
      }
      */
    } // end for (;;)
    // trim rem and multiply by coeff, this will modularize rem as well
    rem.clear();
    // bool trimming=true;
    if (env && env->moduloon){
      for (;tmpend!=tmp-1;--tmpend){
	if (!is_zero(smod(*tmpend,env->modulo)))
	  break;   
      }
      for (;tmpend!=tmp-1;--tmpend){
	rem.push_back(smod(*tmpend*coeff,env->modulo));
      }      
    }
    else {
      for (;tmpend!=tmp-1;--tmpend){
	if (!is_zero(*tmpend))
	  break;
      }
      for (;tmpend!=tmp-1;--tmpend){
	rem.push_back(*tmpend);
      }      
    }
    // cout << "DivRem" << th << "-" << other << "*" << quo << "=" << rem << " " << th-other*quo << endl;
    delete [] tmp;
    return true;
  }

  bool DenseDivRem(const modpoly & th, const modpoly & other,modpoly & quo, modpoly & rem,bool fastfalsetest){
    int n=th.size(), m=other.size();
    gen t=th[n-1], o=other[m-1];
    if (fastfalsetest && n && m ){
      if (is_zero(o)){
	if (!is_zero(t))
	  return false;
      }
      else {
	if (!is_zero(t % o))
	  return false;
	// if ((n>1) && (m>1))
	//  cout << ( th[n-2]-other[m-2]*(t/o) ) % o << endl;
      }
    }
    environment * env=new environment;
    bool res=DivRem(th,other,env,quo,rem,false);
    delete env;
    return res;
  }

  modpoly operator / (const modpoly & th,const modpoly & other) {  
    modpoly rem,quo;
    environment * env=new environment;
    DivRem(th,other,env,quo,rem);
    delete env;
    return quo;
  }

  modpoly operator % (const modpoly & th,const modpoly & other) {  
    modpoly rem,quo;
    environment * env=new environment;
    DivRem(th,other,env,quo,rem);
    delete env;
    return rem;
  }

  modpoly operator_div (const modpoly & th,const modpoly & other,environment * env) {  
    modpoly rem,quo;
    DivRem(th,other,env,quo,rem);
    return quo;
  }

  modpoly operator_mod (const modpoly & th,const modpoly & other,environment * env) {  
    modpoly rem,quo;
    DivRem(th,other,env,quo,rem);
    return rem;
  }

  // Pseudo division a*th = other*quo + rem
  void PseudoDivRem(const dense_POLY1 & th, const dense_POLY1 & other, dense_POLY1 & quo, dense_POLY1 & rem, gen & a){
    int ts=th.size();
    int os=other.size();
    if (ts<os){
      quo.clear();
      rem=th;
      a=1;
    }
    else {
      gen l(other[0]);
      a=pow(l,ts-os+1);
      DenseDivRem(th*a,other,quo,rem);
    }
  }

  /*
  dense_POLY1 AscPowDivRemModifiable(dense_POLY1 & num, dense_POLY1 & den,int order){
    // reverse and adjust den degree to order
    reverse(den.begin(),den.end());
    rrdm(den,order);
    // reverse and adjust num degree to 2*order
    reverse(num.begin(),num.end());
    rrdm(num,2*order);
    dense_POLY1 quo,rem;
    DenseDivRem(num,den,quo,rem);
    reverse(quo.begin(),quo.end());
    return trim(quo,env);
  }

  dense_POLY1 AscPowDivRem(const dense_POLY1 & num, const dense_POLY1 & den,int order){
    dense_POLY1 numcopy(num),dencopy(den);
    return AscPowDivRemModifiable(numcopy,dencopy,order);
  }
  */

  // Multiply each element of v by k
  inline void mulmodpoly(vector<int> & v,int k,int m){
    if (k==1)
      return;
    vector<int>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      *it *= k;
      *it %= m;
    }
    if (v.front()<0)
      v.front() += m;
  }

  // Euclidean division modulo m
  void DivRem(const vector<int> & th, const vector<int> & other,int m,vector<int> & quo, vector<int> & rem){
    if (other.empty()){
      rem=th;
      quo.clear();
      return;
    }
    if (th.empty()){
      quo=th;
      rem=th;
      return;
    }
    int a=th.size()-1;
    int b=other.size()-1;
    int coeff=other.front(),invcoeff=invmod(coeff,m);
    if (!b){
      quo=th;
      mulmod(quo,invcoeff,m);
      rem.clear();
      return;
    }
    quo.clear();
    rem=th;
    if (a<b)
      return;
    quo.reserve(a-b+1);
    // A=BQ+R -> A=(B*invcoeff)*Q+(R*invcoeff), 
    // make division of A*coeff by B*coeff and multiply R by coeff at the end
    vector<int> B=other;
    mulmod(rem,invcoeff,m); // rem=th*invcoeff;
    mulmod(B,invcoeff,m); // B=other*invcoeff;
    // copy rem to an array
    vector<int>::const_iterator remit=rem.begin();//,remend=rem.end();
#if defined VISUALC || defined BESTA_OS
    int * tmp=new int[a+1];
#else
    int tmp[a+1];
#endif
    int * tmpend=&tmp[a];
    int * tmpptr=tmpend; // tmpend points to the highest degree coeff of A
    for (;tmpptr!=tmp-1;--tmpptr,++remit)
      *tmpptr=*remit;
    vector<int>::const_iterator B_beg=B.begin(),B_end=B.end();
    int q;//n0(0),
    for (;a>=b;--a){
      q= *tmpend % m;
      quo.push_back(q);
      --tmpend;
      // tmp <- tmp - q *B.shifted (if q!=0)
      if (q) {
	tmpptr=tmpend;
	vector<int>::const_iterator itq=B_beg;
	++itq; // first elements cancel
	for (;itq!=B_end;--tmpptr,++itq){ 
	  *tmpptr = (*tmpptr -(longlong(q) * (*itq)))%m;
	}
      }
    }
    // trim rem and multiply by coeff, this will modularize rem as well
    rem.clear();
    // bool trimming=true;
    for (;tmpend!=tmp-1;--tmpend){
      if (*tmpend % m)
	break;   
    }
    for (;tmpend!=tmp-1;--tmpend){
      rem.push_back( (*tmpend*longlong(coeff)) % m);
    } 
#if defined VISUALC || defined BESTA_OS
    delete [] tmp;
#endif
  }

  // Conversion from vector<gen> to vector<int> modulo m
  void modpoly2smallmodpoly(const modpoly & p,vector<int> & v,int m){
    v.clear();
    const_iterateur it=p.begin(),itend=p.end();
    v.reserve(itend-it);
    int g;
    bool trim=true;
    for (;it!=itend;++it){
      if (it->type==_INT_)
	g=it->val % m;
      else 
	g=smod(*it,m).val;
      if (g)
	trim=false;
      if (!trim)
	v.push_back(g);
    }
  }


  // Conversion from vector<int> to vector<gen> using smod
  void smallmodpoly2modpoly(const vector<int> & v,modpoly & p,int m){
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    p.clear();
    p.reserve(itend-it);
    for (;it!=itend;++it){
      p.push_back(smod(*it,m));
    }
  }

  // compute r mod b into r
  // r, b must be allocated arrays of int
  // compute quotient if quoend!=0
  // set exactquo to true if you know that b divides r and only want to compute the quotient
  // this will not compute low degree coeff of r during division and spare some time
  static void rem(int * & r,int *rend,int * b,int *bend,int m,int * & quo,int *quoend,bool exactquo=false){
    int * i,*j,*rstop,*qcur,k,q,q2,lcoeffinv=1;
    k=bend-b;
    if (!k){
      quo=quoend;
      return;
    }
    if (rend-r<k){
      quo=quoend;
      return;
    }
    quo=quoend-((rend-r)-(k-1));
    qcur=quo;
    // inv leading coeff of b 
    if (*b !=1)
      lcoeffinv=invmod(*b,m);
    if (k==1){
      if (quoend){
	i=quo;
	for (;r!=rend;++r,++i){
	  type_operator_times_reduce(*r,lcoeffinv,*i,m);
	  // *i=(*r*lcoeffinv)%m;
	}
      }
      else
	r=rend;
      return;
    }
    if (rend-r==bend-b+1){
      // frequent case: the degree decrease by 1
      // a(x) += b(x)*(q1*x+q2)
      // first compute q1 and q2
      q=-smod(*r*longlong(lcoeffinv),m);
      ++r;
      q2=-smod( ((*r+longlong(q)* *(b+1))%m)*longlong(lcoeffinv),m);
      if (quoend){
	*qcur=-q;
	++qcur;
	*qcur=-q2;
      }
      ++r;
      // now compute a
      j=r;
      i=b+1;
      if (i!=bend){
	if (m<46340){
	  for (;;){
	    *j += q2* (*i);
	    ++i;
	    if (i==bend){
	      *j %= m;
	      break;
	    }
	    *j += q* (*i);
	    *j %= m;
	    ++j;
	  }
	}
	else {
	  for (;;){
	    type_operator_plus_times_reduce_nock(q2,*i,*j,m);
	    ++i;
	    if (i==bend)
	      break;
	    type_operator_plus_times_reduce_nock(q,*i,*j,m);
	    ++j;
	  }
	}
      } // end if i!=bend
    }    
    else {
      ++b; 
      // while degree(r)>=degree(b) do r <- r - r[0]*lcoeffinv*b
      // rend is not used anymore, we make it point k ints before
      rstop = rend-(k-1) ; // if r==rend then deg(r)==deg(b)
      for (;rstop-r>0;){
	type_operator_times_reduce(*r,lcoeffinv,q,m);
	// q=((*r)*longlong(lcoeffinv))%m;
	if (quoend){
	  *qcur=q;
	  ++qcur;
	}
	++r;
	if (q){
	  q=-q;
	  j=r;
	  i=b;
	  for (;i!=bend;++j,++i){
	    type_operator_plus_times_reduce_nock(q,*i,*j,m);
	    // *j = (*j + q * *i)%m;
	  }
	}
	if (exactquo && rend-r<=2*(k-1))
	  --bend;
      }
    }
    // trim answer
    for (;r!=rend;++r){
      if (*r)
	break;
    }
  }

  /*
  void rem_tabint(int * & r,int *rend,int * b,int *bend,int m,int * & quo,int *quoend){
    int * i,*j,*rstop,*qcur,k,q,lcoeffinv=1;
    k=bend-b;
    if (!k){
      quo=quoend;
      return;
    }
    if (rend-r<k){
      quo=quoend;
      return;
    }
    quo=quoend-((rend-r)-(k-1));
    qcur=quo;
    // inv leading coeff of b 
    if (*b !=1)
      lcoeffinv=invmod(*b,m);
    if (k==1){
      if (quoend){
	i=quo;
	for (;r!=rend;++r,++i){
	  type_operator_times_reduce(*r,lcoeffinv,*i,m);
	  // *i=(*r*lcoeffinv)%m;
	}
      }
      else
	r=rend;
      return;
    }
    ++b; 
    // while degree(r)>=degree(b) do r <- r - r[0]*lcoeffinv*b
    // rend is not used anymore, we make it point k ints before
    rstop = rend-(k-1) ; // if r==rend then deg(r)==deg(b)
    for (;rstop-r>0;){
      type_operator_times_reduce(*r,lcoeffinv,q,m);
      // q=((*r)*longlong(lcoeffinv))%m;
      if (quoend){
	*qcur=q;
	++qcur;
      }
      ++r;
      if (q){
	q=-q;
	j=r;
	i=b;
	for (;i!=bend;++j,++i){
	  // type_operator_plus_times_reduce_nock(q,*i,*j,m);
	  *j = (*j + q * *i)%m;
	  // *j = (*j + longlong(q) * *i)%m;
	}
      }
    }
    // trim answer
    for (;r!=rend;++r){
      if (*r)
	break;
    }
  }
  */

  static void gcdconvert(const modpoly & p,int m,int * a){
    const_iterateur it=p.begin(),itend=p.end();
    for (;it!=itend;++it,++a){
      if (it->type==_INT_)
	*a=it->val % m;
      else 
	*a=smod(*it,m).val;
    }
  }

  static bool gcdconvert(const polynome & p,int m,int * a){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int deg;
    for (;it!=itend;){
      if (it->value.type==_INT_)
	*a=it->value.val % m;
      else {
	if (it->value.type==_ZINT)
	  *a=smod(it->value,m).val;
	else
	  return false;
      }
      deg=it->index.front();
      ++it;
      if (it==itend){
	for (++a;deg>0;++a,--deg){
	  *a=0;
	}
	return true;
      }
      deg -= it->index.front();
      for (++a,--deg;deg>0;++a,--deg){
	*a=0;
      }
    }
    return true;
  }

  // Efficient small modular gcd of p and q using vector<int>
  void gcdsmallmodpoly(const modpoly &p,const modpoly & q,int m,modpoly & d){

    int as=p.size(),bs=q.size();
#if defined VISUALC || defined BESTA_OS
    int *asave=new int[as], *a=asave,*aend=a+as;
    int *bsave=new int[bs], *b=bsave,*bend=b+bs,*qcur=0;
#else
#ifndef NO_STDEXCEPT
    if (as>1000000 || bs>1000000)
      setdimerr();
#endif
    int asave[as], *a=asave,*aend=a+as;
    int bsave[bs], *b=bsave,*bend=b+bs,*qcur=0;
#endif
    gcdconvert(p,m,a);
    int * t;
    gcdconvert(q,m,b);
    for (;b!=bend;){
      rem(a,aend,b,bend,m,qcur,0);
      t=a; a=b; b=t;
      t=aend; aend=bend; bend=t;      
    }
    d.clear();
    d.reserve(aend-a);
    int ainv=1;
    if (a!=aend)
      ainv=invmod(*a,m);
    for (;a!=aend;++a){
      d.push_back(smod((*a)*longlong(ainv),m));
    }
#if defined VISUALC || defined BESTA_OS
    delete [] asave;
    delete [] bsave;
#endif
  }

  bool gcdsmallmodpoly(const polynome &p,const polynome & q,int m,polynome & d,polynome & dp,polynome & dq,bool compute_cof){
    if (p.dim!=1 || q.dim!=1)
      return false;
    bool promote = m>=46340;
    int as=p.lexsorted_degree()+1,bs=q.lexsorted_degree()+1;
    if (as>1000000 || bs>1000000)
      return false;
#if defined VISUALC || defined BESTA_OS
    int *asave = new int[as], *a=asave,*aend=a+as,*qcur=0;
    int *Asave = new int[as], *A=Asave,*Aend=A+as;
    int *bsave = new int[bs], *b=bsave,*bend=b+bs;
    int *Bsave = new int[bs], *B=Bsave,*Bend=B+bs;
#else
    int asave[as], *a=asave,*aend=a+as,*qcur=0;
    int Asave[as], *A=Asave,*Aend=A+as;
    int bsave[bs], *b=bsave,*bend=b+bs;
    int Bsave[bs], *B=Bsave,*Bend=B+bs;
#endif
    int * t;
    if (gcdconvert(p,m,a) && gcdconvert(q,m,b) ){
      memcpy(Asave,asave,as*sizeof(int));
      memcpy(Bsave,bsave,bs*sizeof(int));
      for (;b!=bend;){
	rem(a,aend,b,bend,m,qcur,0);
	t=a; a=b; b=t;
	t=aend; aend=bend; bend=t;      
      }
      d.coord.clear();
      int ainv=1;
      int * aa=a;
      if (a!=aend)
	ainv=invmod(*a,m);
      if (promote){
	for (int deg=aend-a-1;a!=aend;++a,--deg){
	  if (*a){
	    *a=smod((*a)*longlong(ainv),m);
	    d.coord.push_back(monomial<gen>(*a,deg,1,1));
	  }
	}
      }
      else {
	for (int deg=aend-a-1;a!=aend;++a,--deg){
	  if (*a){
	    *a=smod((*a)*ainv,m);
	    d.coord.push_back(monomial<gen>(*a,deg,1,1));
	  }
	}
      }
      if (aa!=aend && compute_cof){
	if (debug_infolevel>20)
	  cerr << "gcdsmallmodpoly, compute cofactors " << clock() << endl;
#if defined VISUALC || defined BESTA_OS
	int * qsave=new int[std::max(as,bs)], *qcur=qsave,*qend=qsave+std::max(as,bs);
#else
	int qsave[std::max(as,bs)], *qcur=qsave,*qend=qsave+std::max(as,bs);
#endif
	// int * qsave=new int[as], *qcur=qsave,*qend=qsave+as;
	rem(A,Aend,aa,aend,m,qcur,qend);
	dp.coord.clear();
	for (int deg=qend-qcur-1;qcur!=qend;++qcur,--deg){
	  if (*qcur)
	    dp.coord.push_back(monomial<gen>(smod(*qcur,m),deg,1,1));
	}
	qcur=qsave;
	rem(B,Bend,aa,aend,m,qcur,qend);
	dq.coord.clear();
	for (int deg=qend-qcur-1;qcur!=qend;++qcur,--deg){
	  if (*qcur)
	    dq.coord.push_back(monomial<gen>(smod(*qcur,m),deg,1,1));
	}
	if (debug_infolevel>20)
	  cerr << "gcdsmallmodpoly, end compute cofactors " << clock() << endl;
#if defined VISUALC || defined BESTA_OS
	delete [] qsave; 
#endif
      }
#if defined VISUALC || defined BESTA_OS
      delete [] asave; delete [] Asave; delete [] bsave; delete [] Bsave;
#endif
      return true;
    }
    else {
#if defined VISUALC || defined BESTA_OS
      delete [] asave; delete [] Asave; delete [] bsave; delete [] Bsave;
#endif
      return false;
    }
  }

  // invert a1 mod m
  double invmod(double a1,double A){
    double a(A),a2,u=0,u1=1,u2,q;
    for (;a1;){
      q=std::floor(a/a1);
      a2=a-q*a1;
      u2=u-q*u1;
      a=a1;
      a1=a2;
      u=u1;
      u1=u2;
    }
    if (a==-1){ a=1; u=-u; }
    if (a!=1) return 0;
    if (u<0) u+=A;
    return u;
  }

  bool convertdouble(const modpoly & p,double M,vector<double> & v){
    v.clear(); v.reserve(p.size());
    int m=int(M);
    const_iterateur it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (it->type==_INT_)
	v.push_back(it->val % m);
      else {
	if (it->type==_ZINT)
	  v.push_back(smod(*it,m).val);
	else
	  return false;
      }
    }
    return true;    
  }

  bool convertfromdouble(const vector<double> & A,modpoly & a,double M){
    a.clear(); a.reserve(A.size());
    int m( (int)M);
    vector<double>::const_iterator it=A.begin(),itend=A.end();
    for (;it!=itend;++it){
      double d=*it;
      if (d!=int(d))
	return false;
      if (d>M/2)
	a.push_back(int(d)-m);
      else
	a.push_back(int(d));
    }
    return true;
  }

  void multdoublepoly(double x,vector<double> & v,double m){
    if (x==1)
      return;
    vector<double>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      double t=*it * x;
      double q=std::floor(t/m);
      *it = t-q*m;
    }
  }

  // A = BQ+R mod m with B leading coeff = 1
  void quoremdouble(const vector<double> & A,const vector<double> & B,vector<double> & Q,vector<double> & R,double m){
    Q.clear();
    R=A;
    int rs=R.size(),bs=B.size();
    if (rs<bs)
      return;
    if (rs==bs+1){ } // possible improvement
    vector<double>::iterator it=R.begin(),itend=it+(rs-bs+1);
    for (;it!=itend;){
      double q=*it;
      Q.push_back(q);
      *it=0;
      ++it;
      vector<double>::iterator kt=it;
      vector<double>::const_iterator jt=B.begin()+1,jtend=B.end();
      for (;jt!=jtend;++kt,++jt){
	double d= *kt- q*(*jt);
	*kt=d-std::floor(d/m)*m;
      }
      for (;it!=itend;++it){
	if (*it)
	  break;
      }
    }
    for (;it!=R.end();++it){
      if (*it)
	break;
    }
    R.erase(R.begin(),it);
  }

  bool gcddoublemodpoly(const modpoly &p,const modpoly & q,double m,modpoly &a){
    vector<double> A,B,Q,R;
    if (!convertdouble(p,m,A) || !convertdouble(q,m,B))
      return false;
    while (!B.empty()){
      multdoublepoly(invmod(B.front(),m),B,m);
      quoremdouble(A,B,Q,R,m);
      swap(A,B);
      swap(B,R);
    }
    if (!A.empty())
      multdoublepoly(invmod(A.front(),m),A,m);
    return convertfromdouble(A,a,m);
  }

  bool gcdmodpoly(const modpoly &p,const modpoly & q,environment * env,modpoly &a){
    if (!env){
#ifndef NO_STDEXCEPT
      setsizeerr();
#endif
      return false;
    }
#ifndef EMCC
    if (env->moduloon && !env->complexe && env->modulo.type==_INT_ && env->modulo.val < (1 << 15) ){
      gcdsmallmodpoly(p,q,env->modulo.val,a);
      return true;
    }
#endif
#if 0
    if (env->moduloon && !env->complexe && env->modulo.type==_INT_ && env->modulo.val < (1 << 26) ){
      if (gcddoublemodpoly(p,q,env->modulo.val,a))
	return true;
    }
#endif
    a=p;
    modpoly b(q);
    modpoly quo,rem;
    while (!b.empty()){
      gen s=b.front();
      mulmodpoly(b,invenv(s,env),env,b);
      DivRem(a,b,env,quo,rem);
      // cout << "a:" << a << "b:" << b << "q:" << quo << "r:" << rem << endl;
      swap(a,b); // newa=b,  
      swap(b,rem); // newb=rem
    }
    if (!a.empty())
      mulmodpoly(a,invenv(a.front(),env),env,a);
    return true;
  }

  // compute gcd of p and q mod m, result in d
  void gcdsmallmodpoly(const vector<int> &p,const vector<int> & q,int m,vector<int> & d){
    gcdsmallmodpoly(p,q,m,d,0,0);
    return;
    int as=p.size(),bs=q.size();
    if (!as){ d=q; return ; }
    if (!bs){ d=p; return ; }
#if defined VISUALC || defined BESTA_OS
    int *asave=new int[as], *a=asave,*aend=a+as,*qcur=0;
    int *bsave=new int[bs], *b=bsave,*bend=b+bs;
#else
    int asave[as], *a=asave,*aend=a+as,*qcur=0;
    int bsave[bs], *b=bsave,*bend=b+bs;
#endif
    memcpy(a,&*p.begin(),as*sizeof(int));
    memcpy(b,&*q.begin(),bs*sizeof(int));
    int * t;
    for (;b!=bend;){
      rem(a,aend,b,bend,m,qcur,0);
      t=a; a=b; b=t;
      t=aend; aend=bend; bend=t;      
    }
    d.clear();
    d.reserve(aend-a);
    int ainv=1;
    if (a!=aend)
      ainv=invmod(*a,m);
    if (m>=46340){
      for (;a!=aend;++a){
	d.push_back(smod((*a)*longlong(ainv),m));
      }
    }
    else {
      for (;a!=aend;++a){
	d.push_back(smod((*a)*ainv,m));
      }
    }
#if defined VISUALC || defined BESTA_OS
    delete [] asave;
    delete [] bsave;
#endif
  }

  void gcdsmallmodpoly(const vector<int> &p,const vector<int> & q,int m,vector<int> & d,vector<int> * pcof,vector<int> * qcof){
    int as=p.size(),bs=q.size();
    if (!as){ 
      // p==0, pcof is undefined
      if (pcof)
	pcof->clear();
      d=q; 
      if (qcof){
	qcof->clear();
	qcof->push_back(1);
      }
      return ; 
    }
    if (!bs){ 
      // q==0
      if (qcof)
	qcof->clear();
      d=p; 
      if (pcof){
	pcof->clear();
	pcof->push_back(1);
      }
      return ; 
    }
    int ms=std::max(as,bs);
#if defined VISUALC || defined BESTA_OS
    int *asave=new int[ms], *a=asave,*aend=a+as,*qcur=0,*qend;
    int *bsave=new int[ms], *b=bsave,*bend=b+bs;
#else
    int asave[ms], *a=asave,*aend=a+as,*qcur=0,*qend;
    int bsave[ms], *b=bsave,*bend=b+bs;
#endif
    bool swapab=false;
    memcpy(a,&*p.begin(),as*sizeof(int));
    memcpy(b,&*q.begin(),bs*sizeof(int));
    int * t;
    for (;b!=bend;swapab=!swapab){
      rem(a,aend,b,bend,m,qcur,0);
      t=a; a=b; b=t;
      t=aend; aend=bend; bend=t;      
    }
    if (a==aend){ // should not happen!
#if defined VISUALC || defined BESTA_OS
      delete [] asave;
      delete [] bsave;
#endif
      return;
    }
    // normalize gcd
    int ainv=1;
    ainv=invmod(*a,m);
    if (ainv!=1){
      for (int * acur=a;acur!=aend;++acur)
	*acur = smod((*acur)*longlong(ainv),m);
    }
#if defined VISUALC || defined BESTA_OS
    int * cof=new int[ms];
#else
    int cof[ms];
#endif
    // find p cofactor
    if (pcof){
      qcur=cof;
      qend=cof+ms;
      b=swapab?asave:bsave;
      bend=b+as;
      memcpy(b,&*p.begin(),as*sizeof(int));
      rem(b,bend,a,aend,m,qcur,qend,true);
      pcof->clear();
      pcof->reserve(qend-qcur);
      for (;qcur!=qend;++qcur)
	pcof->push_back(*qcur);
    }
    if (qcof){
      qcur=cof;
      qend=cof+ms;
      b=swapab?asave:bsave;
      bend=b+bs;
      memcpy(b,&*q.begin(),bs*sizeof(int));
      rem(b,bend,a,aend,m,qcur,qend,true);
      qcof->clear();
      qcof->reserve(qend-qcur);
      for (;qcur!=qend;++qcur)
	qcof->push_back(*qcur);
    }
    d.clear();
    d.reserve(aend-a);
    for (;a!=aend;++a)
      d.push_back(*a);
    // cerr << d << " " << pcof << " " << p << endl;
    // cerr << d << " " << qcof << " " << q << endl;
#if defined VISUALC || defined BESTA_OS
    delete [] asave;
    delete [] bsave;
    delete [] cof;
#endif
  }

  static void dbgp(const modpoly & a){
    cout << a << endl;
  }

  static bool content_mod(const polynome & p,vecteur & gcd,environment * env){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (gcd.size()==1 || it->value.type!=_VECT){
	gcd=vecteur(1,1);
	break;
      }
      gcdmodpoly(gcd,*it->value._VECTptr,env,gcd);
      if (is_undef(gcd))
	return false;
    }
    return true;
  }

  gen hornermod(const vecteur & v,const gen & alpha,const gen & modulo){
    gen res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      res = smod(res*alpha+*it,modulo);
    }
    return res;
  }

  int hornermod(const vecteur & v,int alpha,int modulo){
    int res=0;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      /*
	if (it->type!=_INT_){
	cerr << v << endl;
	setsizeerr(gen(v).print(context0));
	}
      */
      res = (res*alpha+it->val)%modulo;
    }
    return smod(res,modulo);
  }

  // eval p at xn=alpha modulo
  static polynome pevaln(const polynome & p,const gen & alpha,const gen & modulo,index_t * & degptr,bool estreel){
    int a=0,m=0,dim=p.dim;
    bool nonmod = is_zero(modulo);
    bool smallmod = estreel && alpha.type==_INT_ && modulo.type==_INT_ && (m=modulo.val)<46340 && (a=alpha.val)<46340 ;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    polynome res(dim);
    res.coord.reserve(itend-it);
    gen tmp;
    for (;it!=itend;++it){
      if (it->value.type==_VECT)
	tmp=nonmod?horner(*it->value._VECTptr,alpha):(smallmod?hornermod(*it->value._VECTptr,a,m):hornermod(*it->value._VECTptr,alpha,modulo));
      else
	tmp=it->value; // smod(it->value,modulo);
      if (!is_zero(tmp))
	res.coord.push_back(monomial<gen>(tmp,it->index));
      else {
	if (degptr){
	  // if one of the indices of it->index is the same as *degptr
	  // the lcoeff with respect to this variable may vanish
	  for (int i=0;i<dim;++i){
	    if ((*degptr)[i]==(it->index)[i]){
	      degptr=0;
	      break;
	    }
	  }
	}
      }
    }
    return res;
  }

  static bool divmod(polynome & p,const vecteur & v,environment * env){
    if (v.size()==1){
      if (!is_one(v.front())){
	if (!env || !env->moduloon)
	  return false; // setsizeerr();
	p=invmod(v.front(),env->modulo)*p;
      }
    }
    else {
      vector< monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
      vecteur q,r;
      for (;it!=itend;++it){
	if (it->value.type!=_VECT)
	  return false; // setsizeerr();
	DivRem(*it->value._VECTptr,v,env,q,r);
	it->value=gen(q,_POLY1__VECT);
      }
    }
    return true;
  }

  static bool pp_mod(polynome & p,vecteur & v,environment * env){
    content_mod(p,v,env);
    return divmod(p,v,env);
  }

  // extract xn dependency as a modpoly
  static void convert_xn(const polynome & p,polynome & res){
    int dim=p.dim;
    res.dim=dim-1;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t old(dim,-1);
    vecteur cur;
    for (;it!=itend;++it){
      const index_t & curi=it->index.iref();
      old[dim-1]=curi[dim-1];
      if (curi==old){
	cur[curi[dim-1]]=it->value;
      }
      else {
	if (!cur.empty()){
	  reverse(cur.begin(),cur.end());
	  res.coord.push_back(monomial<gen>(gen(cur,_POLY1__VECT),index_t(old.begin(),old.end()-1)));
	}
	old=curi;
	cur=vecteur(curi[dim-1]+1);
	cur[curi[dim-1]]=it->value;
      }
    }
    if (!cur.empty()){
      reverse(cur.begin(),cur.end());
      res.coord.push_back(monomial<gen>(gen(cur,_POLY1__VECT),index_t(old.begin(),old.end()-1)));
    }
  }

  // put back xn dependency as a modpoly
  static void convert_back_xn(const polynome & p,polynome & res){
    res.coord.clear();
    int dim=p.dim,deg;
    res.dim=dim+1;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      index_t i(it->index.iref());
      i.push_back(0);
      if (it->value.type!=_VECT)
	res.coord.push_back(monomial<gen>(it->value,i));
      else {
	const_iterateur jt=it->value._VECTptr->begin(),jtend=it->value._VECTptr->end();
	deg=jtend-jt-1;
	for (;jt!=jtend;++jt,--deg){
	  if (!is_zero(*jt)){
	    i[dim]=deg;
	    res.coord.push_back(monomial<gen>(*jt,i));
	  }
	}
      }
    }
  }

  /*
  void modgcd_bi(const polynome &pxn, const polynome & qxn, int modulo,int gcddeg, polynome & d,polynome & pcofactor,polynome & qcofactor){
    d=polynome(dim-1);
    // we are now interpolating G=gcd(p,q)*a poly/x1
    // such that the leading coeff of G is Delta
    int pdeg(pxn.lexsorted_degree()),qdeg(qxn.lexsorted_degree()); 
    int delta=min(pdeg,qdeg);
    int e=0; // number of evaluations
    int alpha=0,ps,qs;
    if (debug_infolevel>1)
      cerr << "gcdmod find alpha dim " << d.dim << " " << clock() << endl;
    vector<int> palpha,qalpha,pcofactalpha,qcofactalpha,g,g1;
    for (;;++alpha){
      if (alpha==modulo)
	setsizeerr(gettext("Modgcd: no suitable evaluation point"));
      if (debug_infolevel>1)
	cerr << "gcdmod eval " << alpha << " dim " << d.dim << " " << clock() << endl;
      palpha=pevaln(pxn,alpha,modulo);
      if (palpha.empty())
	continue;
      if ((ps=palpha.size())==1){ 
	if (pdeg) 
	  continue;
	// gcd may only depend on first var
	d=cont;
	return;
      }
      qalpha=pevaln(qxn,alpha,modulo);
      if (qalpha.empty()) 
	continue;
      if ((qs=qalpha.size())==1){ 
	if (qdeg) 
	  continue;
	d=cont;
	return;
      }
      if ( ps!=pdeg+1 || qs!=qdeg+1 )
	continue;
      // palpha and qalpha are p_prim and q_prim evaluated at xn=alpha
      if (debug_infolevel>1)
	cerr << "gcdmod gcd at " << alpha << " dim " << d.dim << " " << clock() << endl;
      gcdsmallmodpoly(palpha,qalpha,modulo,g,pcofactalpha,qcofactalpha);
      int gdeg(g.size()-1);
      int gcd_plus_delta_deg=gcddeg+Delta.size()-1;
      if (gdeg==delta){
	if (debug_infolevel>1)
	  cerr << "gcdmod interp dim " << d.dim << " " << clock() << endl;
	g1=g;
	mulmodpoly(g1,smod(hornermod(Delta,alpha,modulo)*invmod(g.front(),modulo),modulo),modulo);
	smallmodpoly2modpoly(g1-pevaln(d,alpha,modulo),modulo,g1);
	mulpoly(g1,smod(invmod(hornermod(interp,alpha,modulo),modulo)*gen(interp,_POLY1__VECT),modulo),g1);
	smod(d+g1,modulo,d);
	interp=operator_times(interp,makevecteur(1,-alpha),&env);
	++e;
	if (e>gcddeg
	    || is_zero(g1)
	    ){
	  if (debug_infolevel)
	    cerr << "gcdmod pp1mod dim " << d.dim << " " << clock() << endl;
	  polynome pD,QP(dim),QQ(dim),R(d);
	  vecteur vtmp;
	  pp_mod(R,vtmp,&env);
	  convert_back_xn(R,pD);
	  // This removes the polynomial in x1 that we multiplied by
	  // (it was necessary to know the lcoeff of the interpolated poly)
	  if (debug_infolevel)
	    cerr << "gcdmod check dim " << d.dim << " " << clock() << endl;
	  divremmod(p,pD,modulo,QP,R);
	  // Now, gcd divides pD for gcddeg+1 values of x1
	  // degree(pD)<=degree(gcd)
	  if (R.coord.empty()){
	    divremmod(q,pD,modulo,QQ,R);
	    // If pD divides both P and Q, then the degree wrt variables
	    // x1,...,xn-1 is the right one (because it is <= since pD 
	    // divides the gcd and >= since pD(xn=one of the try) was a gcd
	    // The degree in xn is the right one because of the condition
	    // on the lcoeff
	    // Note that the division test might be much longer than the
	    // interpolation itself (e.g. if the degree of the gcd is small)
	    // but it seems unavoidable, for example if 
	    // P=Y-X+X(X-1)(X-2)(X-3)
	    // Q=Y-X+X(X-1)(X-2)(X-4)
	    // then gcd(P,Q)=1, but if we take Y=0, Y=1 or Y=2
	    // we get gcddeg=1 (probably degree 1 for the gcd)
	    // interpolation at X=0 and X=1 will lead to Y-X as candidate gcd
	    // and even adding X=2 will not change it
	    // We might remove division if we compute the cofactors of P and Q
	    // if P=pD*cofactor is true for degree(P) values of x1
	    // and same for Q, and the degrees wrt xn of pD and cofactors
	    // have sum equal to degree of P or Q + lcoeff then pD is the gcd
	    if (R.coord.empty()){
	      pD=pD*cont;
	      d=smod(pD*invmod(pD.coord.front().value,modulo),modulo);
	      pcofactor=pcofactor*QP;
	      pcofactor=smod(p_orig.coord.front().value*invmod(pcofactor.coord.front().value,modulo)*pcofactor,modulo);
	      qcofactor=qcofactor*QQ;
	      qcofactor=smod(q_orig.coord.front().value*invmod(qcofactor.coord.front().value,modulo)*qcofactor,modulo);
	      if (debug_infolevel)
		cerr << "gcdmod found dim " << d.dim << " " << clock() << endl;
	      return;
	    }
	  }
	  if (debug_infolevel)
	    cerr << "Gcdmod bad guess " << endl;
	  continue;
	}
	else
	  continue;
      }
      if (gdeg[0]>delta[0]) 
	continue;
      if (delta[0]>=gdeg[0]){ // restart with g
	gcdv=vecteur(1,g);
	alphav=vecteur(1,alpha);
	delta=gdeg;
	d=(g*smod(hornermod(Delta,alpha,modulo),modulo))*invmod(g.coord.front().value,modulo);
	e=1;
	interp=makevecteur(1,-alpha);
	continue;
      }
  }
  */

  static int degree_xn(const polynome & p){
    int res=1;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->value.type==_VECT)
	res=giacmax(res,it->value._VECTptr->size());
    }
    return res-1;
  }

  inline gen lcoeff(const polynome & p){
    return p.coord.empty()?0:p.coord.front().value;
  }

  // Find non zeros coeffs of p
  static int find_nonzero(const modpoly & p,index_t & res){
    res.clear();
    const_iterateur it=p.begin(),itend=p.end();
    res.reserve(itend-it);
    if (it==itend)
      return 0;
    int nzeros=0;
    for (;it!=itend;++it){
      if (is_zero(*it)){
	res.push_back(0);
	++nzeros;
      }
      else 	
	res.push_back(1);
    }
    return nzeros;
  }

  static void make_modprimitive_xn(polynome & p,const gen & modulo,polynome & content){
    int dim=p.dim,pxns=1;
    vector< polynome > pxn(1,polynome(dim));
    polynome d(dim),pcof(dim),qcof(dim);
    // fill pxn (not sorted)
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->value.type!=_VECT)
	pxn[0].coord.push_back(*it);
      else {
	vecteur & v=*it->value._VECTptr;
	int j=v.size()-1;
	if (j>=0){
	  for (;j>=pxns;++pxns)
	    pxn.push_back(polynome(dim));
	  const_iterateur jt=v.begin(); // ,jtend=v.end();
	  for (;j>=0;--j,++jt){
	    if (!is_zero(*jt))
	      pxn[j].coord.push_back(monomial<gen>(*jt,it->index));
	  }
	}
      }
    }
    content.dim=dim;
    content.coord.clear();
    // now for each polynomial in pxn, sort and find gcd with content
    for (int j=pxns-1;j>=0;--j){
      pxn[j].tsort();
      modgcd(content,pxn[j],modulo,d,pcof,qcof,false);
      content=d;
      if (Tis_constant<gen>(content)){
	content.coord.front().value=1;
	return;
      }
    }
    polynome q,r;
    divremmod(p,content,modulo,q,r);
    p=q;
  }

  bool convert(const polynome &p_orig, const polynome & q_orig,index_t & d,std::vector<hashgcd_U> & vars,std::vector< T_unsigned<gen,hashgcd_U> > & p,std::vector< T_unsigned<gen,hashgcd_U> > & q){
    int dim=p_orig.dim;
    index_t pdeg(p_orig.degree()),qdeg(q_orig.degree()),pqdeg(pdeg+qdeg);
    // convert p_orig and q_orig to vector< T_unsigned<gen,hashgcd_U> >
    // using pqdeg (instead of max(pdeg,qdeg) because of gcd(lcoeff(p),lcoeff(q)))
    // additional factor 2 since computing cofactors require more
    unsigned long long ans=1;
    for (int i=0;i<dim;++i){
      d[i]=2*(pdeg[i]+qdeg[i]+1); 
      int j=1;
      // round to next power of 2
      for (;;j++){
	if (!(d[i] >>= 1))
	  break;
      }
      d[i] = 1 << j;
      ans = ans*unsigned(d[i]);
      if (ans/RAND_MAX>=1){
	return false;
      }
    }
    vars[dim-1]=1;
    for (int i=dim-2;i>=0;--i){
      vars[i]=d[i+1]*vars[i+1];
    }
    convert<gen,hashgcd_U>(p_orig,d,p);
    convert<gen,hashgcd_U>(q_orig,d,q);
    return true;
  }

  bool gcd_modular(const polynome &p_orig, const polynome & q_orig, polynome & pgcd,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors){
    if (debug_infolevel)
      cerr << "gcd modular algo begin " << clock() << endl;
    int dim=p_orig.dim;
    vector< T_unsigned<gen,hashgcd_U> > p,q,g,pcof,qcof;
    index_t d(dim);
    std::vector<hashgcd_U> vars(dim);
    if (dim==1 || p_orig.coord.empty() || is_one(q_orig) || q_orig.coord.empty() || is_one(p_orig) || !convert(p_orig,q_orig,d,vars,p,q) || !gcd(p,q,g,pcof,qcof,vars,compute_cofactors,threads)){
      if (&pcofactor!=&p_orig) pcofactor=p_orig; 
      if (&qcofactor!=&q_orig) qcofactor=q_orig;
      return gcd_modular_algo(pcofactor,qcofactor,pgcd,compute_cofactors);
    }
    convert_from<gen,hashgcd_U>(g,d,pgcd);
    pgcd.dim=qcofactor.dim=pcofactor.dim=dim;
    if (compute_cofactors){
      convert_from<gen,hashgcd_U>(pcof,d,pcofactor);
      convert_from<gen,hashgcd_U>(qcof,d,qcofactor);
    }
    return true;
  }

  bool convert(const polynome &p_orig, const polynome & q_orig,index_t & d,std::vector<hashgcd_U> & vars,std::vector< T_unsigned<int,hashgcd_U> > & p,std::vector< T_unsigned<int,hashgcd_U> > & q,int modulo){
    int dim=p_orig.dim;
    index_t pdeg(p_orig.degree()),qdeg(q_orig.degree());
    // convert p_orig and q_orig to vector< T_unsigned<int,hashgcd_U> >
    // using pqdeg (instead of max(pdeg,qdeg) because of gcd(lcoeff(p),lcoeff(q)))
    // additional factor 2 since computing cofactors require more
    unsigned long long ans=1;
    d.clear();
    d.reserve(dim);
    for (int i=0;i<dim;++i){
      d.push_back(2*(pdeg[i]+qdeg[i]+1)); 
      int j=1;
      // round to next power of 2
      for (;;j++){
	if (!(d[i] >>= 1))
	  break;
      }
      d[i] = 1 << j;
      ans = ans*unsigned(d[i]);
      if (ans/RAND_MAX>=1)
	return false;
    }
    vars=std::vector<hashgcd_U>(dim);
    vars[dim-1]=1;
    for (int i=dim-2;i>=0;--i){
      vars[i]=d[i+1]*vars[i+1];
    }
    if (!convert_int32(p_orig,d,p,modulo) || !convert_int32(q_orig,d,q,modulo) )
      return false;
    return true;
  }

  bool mod_gcd(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & pgcd,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors){
    if (debug_infolevel)
      cerr << "modgcd begin " << clock() << endl;
    int dim=p_orig.dim;
    if ( dim==1 || p_orig.coord.empty() || is_one(q_orig) || q_orig.coord.empty() || is_one(p_orig) || modulo.type!=_INT_ ){
      return mod_gcd_c(p_orig,q_orig,modulo,pgcd,pcofactor,qcofactor,compute_cofactors);
    }
    if (debug_infolevel)
      cerr << "modgcd begin 2 " << clock() << endl;
    std::vector<hashgcd_U> vars(dim);
    vector< T_unsigned<int,hashgcd_U> > p,q,g,pcof,qcof;
    index_t d(dim);
    if (!convert(p_orig,q_orig,d,vars,p,q,modulo.val) || !mod_gcd(p,q,modulo.val,g,pcof,qcof,vars,compute_cofactors,threads))
      return mod_gcd_c(p_orig,q_orig,modulo,pgcd,pcofactor,qcofactor,compute_cofactors);
    convert_from<int,hashgcd_U>(g,d,pgcd);
    pgcd.dim=qcofactor.dim=pcofactor.dim=dim;
    if (compute_cofactors){
      convert_from<int,hashgcd_U>(pcof,d,pcofactor);
      convert_from<int,hashgcd_U>(qcof,d,qcofactor);
    }
    return true;
  }

  bool modgcd(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & d,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors){
    return mod_gcd(p_orig,q_orig,modulo,d,pcofactor,qcofactor,compute_cofactors);
  }

  bool mod_gcd_c(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & d,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors){
    if (p_orig.coord.empty() || is_one(q_orig)){
      d=q_orig;
      if (compute_cofactors){
	pcofactor=p_orig;
	qcofactor=q_orig/d;
      }
      return true;
    }
    if (q_orig.coord.empty() || is_one(p_orig)){
      d=p_orig;
      if (compute_cofactors){
	qcofactor=q_orig;
	pcofactor=p_orig/d;
      }
      return true;
    }
    int dim=p_orig.dim;
    d.dim=dim;
    pcofactor.dim=dim;
    qcofactor.dim=dim;
    environment env;
    env.modulo=modulo;
    env.pn=modulo;
    env.moduloon=true;
    bool estreel;
    if (gcdmod_dim1(p_orig,q_orig,modulo,d,pcofactor,qcofactor,compute_cofactors,estreel))
      return true;
    env.complexe=!estreel;
    if (debug_infolevel)
      cerr << "xn_gcdmod content/x1..xn-1 dim " << dim << " " << clock() << endl;
    // Make p and q primitive with respect to x1,...,xn-1
    // i.e. the coeff of p and q wrt x1,...,xn-1 which are polynomials in xn
    // are relative prime 
    polynome pxn,qxn,ptmp,qtmp,pcofactorxn,qcofactorxn,dxn,cont;
    convert_xn(p_orig,pxn);
    convert_xn(q_orig,qxn);
    vecteur pcont1,qcont1,pqcont1;
    if (!pp_mod(pxn,pcont1,&env) ||
	!pp_mod(qxn,qcont1,&env))
      return false;
    gcdmodpoly(pcont1,qcont1,&env,pqcont1);
    if (is_undef(pqcont1))
      return false;
    // Make p and q primitive with respect to xn
    // p(x1,...,xn) q(x1,...,xn) viewed as p(xn) and q(xn) 
    // with coeff polynomial wrt x1..xn-1
    make_modprimitive_xn(pxn,modulo,pcofactorxn);
    make_modprimitive_xn(qxn,modulo,qcofactorxn);
    modgcd(pcofactorxn,qcofactorxn,modulo,dxn,ptmp,qtmp,false);
    mulpoly(dxn,pqcont1,dxn);
    convert_back_xn(dxn,cont);
    if (compute_cofactors){
      mulpoly(pcofactorxn,pcont1,pcofactorxn);
      mulpoly(qcofactorxn,qcont1,qcofactorxn);
      convert_back_xn(pcofactorxn,pcofactor);
      convert_back_xn(qcofactorxn,qcofactor);
    }
    // Find degree of gcd with respect to xn, more precisely gcddeg>=degree/xn
    // and compute data for the sparse modular algorithm
    index_t vzero; // coeff of vzero correspond to zero or non zero
    int nzero=1; // Number of zero coeffs
    vecteur alphav,gcdv; // Corresponding values of alpha and gcd at alpha
    int gcddeg=0;
    vecteur b(dim-1),bnext;
    int pxndeg=degree_xn(pxn),qxndeg=degree_xn(qxn);
    for (int essai=0;essai<2;){
      gen pb(peval(pxn,b,modulo));
      gen qb(peval(qxn,b,modulo));
      for (;;){
	bnext=vranm(dim-1,0,0); // find another random point
	if (bnext!=b){ b=bnext; break; }
      }
      if (pb.type==_POLY && !pb._POLYptr->coord.empty())
	pb=pb._POLYptr->coord.front().value;
      if (pb.type!=_VECT || int(pb._VECTptr->size())!=pxndeg+1)
	continue;
      if (qb.type==_POLY && !qb._POLYptr->coord.empty())
	qb=qb._POLYptr->coord.front().value;
      if (qb.type!=_VECT || int(qb._VECTptr->size())!=qxndeg+1)
	continue;
      modpoly db;
      gcdmodpoly(*pb._VECTptr,*qb._VECTptr,&env,db);
      if (is_undef(db))
	return false;
      int dbdeg=db.size()-1;
      if (!dbdeg){ 
	gcddeg=0; break; 
      }
      if (!essai){ // 1st gcd test
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	++essai;
	continue;
      }
      // 2nd try
      if (dbdeg<gcddeg){ // 1st try was unlucky, restart 1st try
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	continue;
      }
      if (dbdeg!=gcddeg) 
	continue;
      // Same gcd degree for 1st and 2nd try, keep this degree
      index_t tmp;
      nzero=find_nonzero(db,tmp);
      if (nzero){
	vzero = vzero | tmp;
	// Recompute nzero, it is the number of 0 coeff of vzero
	index_t::const_iterator it=vzero.begin(),itend=vzero.end();
	for (nzero=0;it!=itend;++it){
	  if (!*it) ++nzero;
	}
      }
      ++essai;
    }
    if (!gcddeg){
      d=cont;
      return true;
    }
    vecteur interp(1,1);
    // gcd of leading coefficients of p and q viewed as poly in X_1...X_n-1
    // with coeff in Z[X_n]
    if (debug_infolevel)
      cerr << "gcdmod lcoeffn dim " << dim-1 << " " << clock() << endl;
    gen lp(pxn.coord.front().value),lq(qxn.coord.front().value);
    vecteur Delta(1,1),lcoeffp(1,1),lcoeffq(1,1);
    if (lp.type==_VECT)
      lcoeffp=*lp._VECTptr;
    if (lq.type==_VECT)
      lcoeffq=*lq._VECTptr;
    if ((lp.type==_VECT) && (lq.type==_VECT) ){
      gcdmodpoly(lcoeffp,lcoeffq,&env,Delta);
      if (is_undef(Delta))
	return false;
    }
    // estimate time for full lift or division try
    // size=p_orig.size()+q_orig.size()
    // sumdeg=pxndeg+qxndeg
    // %age=gcddeg/min(pxndeg,qxndeg)
    // %age^dim*(1-%age)^dim*size^2 estimates the time for division try
    // gcddeg*size estimates the time for lifting to gcddeg
    // sumdeg*size estimates the time for full lifting
    // if sumdeg<(gcddeg+%age^dim*(1-%age)^dim*size) do full lifting
    int Deltadeg = Delta.size()-1,liftdeg=giacmax(pxndeg,qxndeg)+Deltadeg;
    int gcddeg_plus_delta=gcddeg+Deltadeg;
    int liftdeg0=giacmax(liftdeg-gcddeg,gcddeg_plus_delta);
    // once liftdeg0 is reached we can replace g/gp/gq computation
    // by a check that d*dp=dxn*lcoeff(d*dp)/Delta at alpha
    // and d*dq=dxn*lcoeff(d*dq)/lcoeff(qxn) at alpha
    int sumdeg = pxndeg+qxndeg;
    double percentage = double(gcddeg)/giacmin(pxndeg,qxndeg);
    int sumsize = p_orig.coord.size()+q_orig.coord.size();
    double gcdlift=gcddeg+std::pow(percentage,dim)*std::pow(1-percentage,dim)*sumsize;
    bool compute_cof = sumdeg<gcdlift/(1+dim);
    polynome p(dim),q(dim);
    if (!compute_cof){
      convert_back_xn(pxn,p);
      convert_back_xn(qxn,q);
    }
    if (debug_infolevel)
      cerr << "dim " << dim << ", full lift:" << sumdeg << " , gcdlift:" << gcdlift/(1+dim) << " compute cofactors=" << compute_cof << endl;
    d=polynome(dim-1);
    polynome dp(dim-1),dq(dim-1),g1(dim-1);
    // we are now interpolating G=gcd(p,q)*a poly/xn
    // such that the leading coeff of G is Delta
    index_t pdeg(pxn.degree()),qdeg(qxn.degree()); 
    int spdeg=0,sqdeg=0;
    for (int i=0;i<dim-1;++i){
      spdeg += pdeg[i];
      sqdeg += qdeg[i];
    }
    index_t delta=index_min(pdeg,qdeg);
    int e=0; // number of evaluations
    int alpha=0;
    if (debug_infolevel>1)
      cerr << "gcdmod find alpha dim " << d.dim << " " << clock() << endl;
    for (;;++alpha){
      if (alpha==modulo){
	cerr << "Modgcd: no suitable evaluation point" << endl;
	return false;
      }
      if (debug_infolevel>1)
	cerr << "gcdmod eval " << alpha << " dim " << d.dim << " " << clock() << endl;
      index_t * pdegptr=&pdeg;
      const polynome & palpha=pevaln(pxn,alpha,modulo,pdegptr,estreel);
      if (palpha.coord.empty())
	continue;
      if (Tis_constant<gen>(palpha)){ 
	if (spdeg) 
	  continue;
	// gcd may only depend on xn
	d=cont;
	return true;
      }
      if (!pdegptr) 
	continue;
      index_t * qdegptr=&qdeg;
      const polynome & qalpha=pevaln(qxn,alpha,modulo,qdegptr,estreel);
      if (qalpha.coord.empty()) 
	continue;
      if (Tis_constant<gen>(qalpha)){ 
	if (sqdeg) 
	  continue;
	d=cont;
	return true;
      }
      if (!qdegptr)
	continue;
      // palpha/qalpha should have the same degrees than pxn/qxn
      // but the test requires checking all monomials of palpha/qalpha
      // if (palpha.lexsorted_degree()!=pdeg[0] || qalpha.lexsorted_degree()!=qdeg[0] )
      // continue;
      // palpha and qalpha are p_prim and q_prim evaluated at xn=alpha
      if (debug_infolevel>1)
	cerr << "gcdmod gcd at " << alpha << " dim " << d.dim << " " << clock() << endl;
      polynome g(dim-1),gp(dim-1),gq(dim-1);
      index_t * tmpptr=0;
      if (compute_cof && e>liftdeg0 && e<=liftdeg){
	g=pevaln(d,alpha,modulo,tmpptr,estreel);
	gp=pevaln(dp,alpha,modulo,tmpptr,estreel);
	gq=pevaln(dq,alpha,modulo,tmpptr,estreel);
	// check that g*gp=palpha*lcoeff/lcoeff
	mulpoly(gp,smod(lcoeff(palpha)*invmod(lcoeff(g)*lcoeff(gp),modulo),modulo),gp);
	mulpoly(gq,smod(lcoeff(qalpha)*invmod(lcoeff(g)*lcoeff(gq),modulo),modulo),gq);
	gp=smod(g*gp-palpha,modulo);
	gq=smod(g*gq-qalpha,modulo);
	if (is_zero(gp) && is_zero(gq)){
	  ++e;
	  continue;
	}
      }
      if (!modgcd(palpha,qalpha,modulo,g,gp,gq,compute_cof))
	return false;
      index_t gdeg(g.degree());
      if (gdeg==delta){
	// Try spmod first
	if (!compute_cof && nzero){
	  // Add alpha,g 
	  alphav.push_back(alpha);
	  gcdv.push_back(g);
	  if (gcddeg-nzero==e){ 
	    // We have enough evaluations, let's try SPMOD
	    // Build the matrix, each line has coeffs / vzero
	    matrice m;
	    for (int j=0;j<=e;++j){
	      index_t::reverse_iterator it=vzero.rbegin(),itend=vzero.rend();
	      vecteur line;
	      for (gen p=alphav[j],pp=plus_one;it!=itend;++it,pp=smod(p*pp,modulo)){
		if (*it)
		  line.push_back( pp);
	      }
	      reverse(line.begin(),line.end());
	      line.push_back(gcdv[j]);
	      m.push_back(line);
	    }
	    // Reduce linear system modulo modulo
	    gen det; vecteur pivots; matrice mred;
	    // cerr << "SPMOD " << clock() << endl;
	    if (!modrref(m,mred,pivots,det,0,m.size(),0,m.front()._VECTptr->size()-1,true,false,modulo,false))
	      return false;
	    // cerr << "SPMODend " << clock() << endl;
	    if (!is_zero(det)){	      
	      // Last column is the solution, it should be polynomials
	      // that must be untrunced with index = to non-0 coeff of vzero
	      polynome trygcd(dim);
	      index_t::const_iterator it=vzero.begin(),itend=vzero.end();
	      int deg=itend-it-1;
	      for (int pos=0;it!=itend;++it,--deg){
		if (!*it)
		  continue;
		gen tmp=mred[pos][e+1]; // e+1=#of points -> last col
		if (tmp.type==_POLY){
		  //*tmp._POLYptr=
		  tmp._POLYptr->untruncn(deg);
		  polynome tmpxn;
		  convert_xn(*tmp._POLYptr,tmpxn);
		  trygcd=trygcd+tmpxn;
		}
		else {
		  if (!is_zero(tmp)){
		    vecteur tmpxn(deg+1);
		    tmpxn.front()=tmp;
		    trygcd=trygcd+monomial<gen>(tmpxn,dim-1);
		  }
		}
		++pos;
	      }
	      // Check if trygcd is the gcd!
	      vecteur tmpv;
	      if (!pp_mod(trygcd,tmpv,&env))
		return false;
	      polynome pD,QP(dim),QQ(dim),R(dim);
	      convert_back_xn(trygcd,pD);
	      divremmod(p,pD,modulo,QP,R);
	      if (R.coord.empty()){
		divremmod(q,pD,modulo,QQ,R);
		if (R.coord.empty()){
		  pD=pD*cont;
		  d=smod(pD*invmod(pD.coord.front().value,modulo),modulo);
		  if (compute_cofactors){
		    pcofactor=pcofactor*QP;
		    pcofactor=smod(p_orig.coord.front().value*invmod(pcofactor.coord.front().value,modulo)*pcofactor,modulo);
		    qcofactor=qcofactor*QQ;
		    qcofactor=smod(q_orig.coord.front().value*invmod(qcofactor.coord.front().value,modulo)*qcofactor,modulo);
		  }
		  return true;
		}
	      }
	    }
	    // SPMOD not successfull :-(
	    nzero=0;
	  } // end if gcddeg-nzero==e
	} // end if (nzero)
	if (debug_infolevel>1)
	  cerr << "gcdmod interp dim " << d.dim << " " << clock() << endl;
	if (compute_cof){
	  // interpolate p cofactor
	  mulpoly(gp,smod(hornermod(lcoeffp,alpha,modulo)*invmod(gp.coord.front().value,modulo),modulo),g1);
	  smod(g1-pevaln(dp,alpha,modulo,tmpptr,estreel),modulo,g1);
	  if (!is_zero(g1)){
	    mulpoly(g1,smod(invmod(hornermod(interp,alpha,modulo),modulo)*gen(interp,_POLY1__VECT),modulo),g1);
	    smod(dp+g1,modulo,dp);
	  }
	  // interpolate q cofactor
	  mulpoly(gq,smod(hornermod(lcoeffq,alpha,modulo)*invmod(gq.coord.front().value,modulo),modulo),g1);
	  smod(g1-pevaln(dq,alpha,modulo,tmpptr,estreel),modulo,g1);
	  if (!is_zero(g1)){
	    mulpoly(g1,smod(invmod(hornermod(interp,alpha,modulo),modulo)*gen(interp,_POLY1__VECT),modulo),g1);
	    smod(dq+g1,modulo,dq);
	  }
	}
	// interp GCD
	mulpoly(g,smod(hornermod(Delta,alpha,modulo)*invmod(g.coord.front().value,modulo),modulo),g1);
	smod(g1-pevaln(d,alpha,modulo,tmpptr,estreel),modulo,g1);
	if (!is_zero(g1)){
	  mulpoly(g1,smod(invmod(hornermod(interp,alpha,modulo),modulo)*gen(interp,_POLY1__VECT),modulo),g1);
	  smod(d+g1,modulo,d);
	}
	interp=operator_times(interp,makevecteur(1,-alpha),&env);
	++e;
	vecteur vtmp;
	if (compute_cof){
	  if (e>liftdeg){ 
	    // divide d,dp,dq by their content in xn
	    if (!pp_mod(d,vtmp,&env) ||
		!pp_mod(dp,vtmp,&env) ||
		!pp_mod(dq,vtmp,&env))
	      return false;
	    polynome pD(dim),PP(dim),QQ(dim);
	    // check xn degrees of d+dp=degree(pxn), d+dq=degree(qxn)
	    int dxndeg=degree_xn(d),dpxndeg=degree_xn(dp),dqxndeg=degree_xn(dq);
	    if ( dxndeg+dpxndeg==degree_xn(pxn) &&
		 dxndeg+dqxndeg==degree_xn(qxn) ){
	      convert_back_xn(d,pD);
	      d=pD*cont;
	      if (compute_cofactors){
		convert_back_xn(dp,PP);
		convert_back_xn(dq,QQ);
		pcofactor=PP*pcofactor;
		qcofactor=QQ*qcofactor;
		pcofactor=smod(p_orig.coord.front().value*invmod(pcofactor.coord.front().value,modulo)*pcofactor,modulo);
		qcofactor=smod(q_orig.coord.front().value*invmod(qcofactor.coord.front().value,modulo)*qcofactor,modulo);
	      }
	      if (debug_infolevel)
		cerr << "gcdmod end dim " << dim << " " << clock() << endl;
	      return true;
	    }
	    d.coord.clear(); dp.coord.clear(); dq.coord.clear();
	    gcdv.clear(); alphav.clear(); 
	    interp.clear(); interp.push_back(1);
	    e=0;	    
	  }
	}
	else {
	  if (e>gcddeg || is_zero(g1)){
	    if (debug_infolevel)
	      cerr << "gcdmod pp1mod dim " << dim << " " << clock() << endl;
	    polynome pD,QP(dim),QQ(dim),R(d);
	    if (!pp_mod(R,vtmp,&env))
	      return false;
	    convert_back_xn(R,pD);
	    // This removes the polynomial in xn that we multiplied by
	    // (it was necessary to know the lcoeff of the interpolated poly)
	    if (debug_infolevel)
	      cerr << "gcdmod check dim " << dim << " " << clock() << endl;
	    // Now, gcd divides pD for gcddeg+1 values of x1
	    // degree(pD)<=degree(gcd)
	    if (divremmod(p,pD,modulo,QP,R) && R.coord.empty()){
	      // If pD divides both P and Q, then the degree wrt variables
	      // x1,...,xn-1 is the right one (because it is <= since pD 
	      // divides the gcd and >= since pD(xn=one of the try) was a gcd
	      // The degree in xn is the right one because of the condition
	      // on the lcoeff
	      // Note that the division test might be much longer than the
	      // interpolation itself (e.g. if the degree of the gcd is small)
	      // but it seems unavoidable, for example if 
	      // P=Y-X+X(X-1)(X-2)(X-3)
	      // Q=Y-X+X(X-1)(X-2)(X-4)
	      // then gcd(P,Q)=1, but if we take Y=0, Y=1 or Y=2
	      // we get gcddeg=1 (probably degree 1 for the gcd)
	      // interpolation at X=0 and X=1 will lead to Y-X as candidate gcd
	      // and even adding X=2 will not change it
	      // We might remove division if we compute the cofactors of P and Q
	      // if P=pD*cofactor is true for degree(P) values of x1
	      // and same for Q, and the degrees wrt xn of pD and cofactors
	      // have sum equal to degree of P or Q + lcoeff then pD is the gcd
	      if (divremmod(q,pD,modulo,QQ,R) &&R.coord.empty()){
		pD=pD*cont;
		d=smod(pD*invmod(pD.coord.front().value,modulo),modulo);
		if (compute_cofactors){
		  pcofactor=pcofactor*QP;
		  pcofactor=smod(p_orig.coord.front().value*invmod(pcofactor.coord.front().value,modulo)*pcofactor,modulo);
		  qcofactor=qcofactor*QQ;
		  qcofactor=smod(q_orig.coord.front().value*invmod(qcofactor.coord.front().value,modulo)*qcofactor,modulo);
		}
		if (debug_infolevel)
		  cerr << "gcdmod found dim " << d.dim << " " << clock() << endl;
		return true;
	      }
	    }
	    if (debug_infolevel)
	      cerr << "Gcdmod bad guess " << endl;
	  } // end if (e>gcddeg)
	} // end else [if (compute_cof)]
	continue;
      } // end gdeg==delta
      // FIXME: the current implementation may break if we are unlucky
      // If the degrees of palpha and qalpha are the same than 
      // those of pxn and qxn, delta <- index_min(gdeg,delta)
      // restart with g only if gdeg[j]<=delta[j] for all indices
      // stay with d only if delta[j]<=gdeg[j]
      if (gdeg[0]>delta[0]) 
	continue;
      if (delta[0]>=gdeg[0]){ // restart with g
	gcdv=vecteur(1,g);
	alphav=vecteur(1,alpha);
	delta=gdeg;
	d=(g*smod(hornermod(Delta,alpha,modulo),modulo))*invmod(g.coord.front().value,modulo);
	if (compute_cof){
	  dp=(gp*smod(hornermod(lcoeffp,alpha,modulo),modulo))*invmod(gp.coord.front().value,modulo);
	  dq=(gq*smod(hornermod(lcoeffq,alpha,modulo),modulo))*invmod(gq.coord.front().value,modulo);
	}
	e=1;
	interp=makevecteur(1,-alpha);
	continue;
      }
    }
  }

  modpoly gcd(const modpoly & p,const modpoly &q,environment * env){
    if (!env || !env->moduloon){
      polynome r,s;
      int dim=giacmax(inner_POLYdim(p),inner_POLYdim(q));
      poly12polynome(p,1,r,dim);
      poly12polynome(q,1,s,dim);
      return polynome2poly1(gcd(r,s),1);
    }
    modpoly a;
    gcdmodpoly(p,q,env,a);
    return a;
    dbgp(a);
    return a;
  }

  modpoly lcm(const modpoly & p,const modpoly &q,environment * env){
    modpoly g(gcd(p,q,env));
    return operator_times(operator_div(p,g,env),q,env);
  }

  // p1*u+p2*v=d
  void egcd(const modpoly &p1, const modpoly & p2, environment * env,modpoly & u,modpoly & v,modpoly & d){
    if (!env || !env->moduloon){
      int dim=giacmax(inner_POLYdim(p1),inner_POLYdim(p2));
      polynome pp1(dim),pp2(dim),pu(dim),pv(dim),pd(dim);
      poly12polynome(p1,1,pp1,dim);
      poly12polynome(p2,1,pp2,dim);
      egcd(pp1,pp2,pu,pv,pd);
      polynome2poly1(pu,1,u);
      polynome2poly1(pv,1,v);
      polynome2poly1(pd,1,d);
      if (is_minus_one(d)){
	d=-d; u=-u; v=-v;
      }
      return;
    }
    if (p2.empty()){
      u=one();
      v.clear();
      d=p1;
      return ;
    }
    if (p1.empty()){
      v=one();
      u.clear();
      d=p2;
      return ;
    }
    modpoly a,b,q,r,tmp;
    bool swapped=false;
    if (p1.size()<p2.size())
      swapped=true;
    // initializes a and b to p1, p2
    if (swapped){
      a=p2;
      b=p1;
    }
    else {
      a=p1;
      b=p2;
    }
    // initializes ua to 1 and ub to 0, the coeff of u in ua*a+va*b=a
    modpoly ua(one()),ub,ur;
    // TDivRem: a = bq+r 
    // hence ur <- ua-q*ub verifies
    // ur*a+vr*b=r
    // a <- b, b <- r, ua <- ub and ub<- ur
    for (;;){
      int n=b.size();
      if (n==1){ // b is cst !=0 hence is the gcd, ub is valid
	break;
      }
      DivRem(a,b,env,q,r); // division works always
      // if r is 0 then b is the gcd and ub the coeff
      if (r.empty())
	break;
      operator_times(q,ub,env,tmp); submodpoly(ua,tmp,env,ur); // ur=ua-q*ub;
      swap(a,b); swap(b,r); // a=b; b=r;
      swap(ua,ub); swap(ub,ur); // ua=ub; ub=ur;
    }
    // ub is valid and b is the gcd, vb=(b-ub*p1)/p2 if not swapped
    gen s=invmod(b.front(),env->modulo);
    mulmodpoly(b,s,env,d); // d=b*s;
    if (swapped){
      mulmodpoly(ub,s,env,v);
      // cout << ub << "*" << s << "=" << v << endl;
      // cout << "swapped" << d << "-" << v << "*" << p2 << "/" << p1 << endl;
      u=operator_div(operator_minus(d,operator_times(v,p2,env),env),p1,env);
    }
    else {
      mulmodpoly(ub,s,env,u);
      // cout << d << "-" << u << "*" << p1 << "/" << p2 << endl;
      v=operator_div(operator_minus(d,operator_times(u,p1,env),env),p2,env);
    }
    // cout << "Verif " << p1 << "*" << u << "+" << p2 << "*" << v << "=" << p1*u+p2*v << " " << d << endl;
  }

  // Solve a=b*x modulo the polynomial n 
  // with degree(a)<l and degree(b)<=degree(n)-l 
  // Assume degree(x)<degree(n)
  bool egcd_pade(const modpoly & n,const modpoly & x,int l,modpoly & a,modpoly &b,environment * env,bool psron){
    l=absint(l);
    modpoly r1(n);
    modpoly r2(x);
    modpoly v1,v2(one()),q,r(x),v(1,1);
    gen g(1),h(1),r20,r2pow,hpow;
    for (;;){
      // During the loop, v1*x+not_computed*n=r1 and v2*x+not_computed*n=r2
      int deg2=r2.size()-1;
      if (deg2<l){ 
	break;
      }
      int deg1=r1.size()-1,ddeg=deg1-deg2;
      if (!env || !env->moduloon){
	r20=r2.front();
	r2pow=pow(r2.front(),ddeg+1);
	DivRem(r2pow*r1,r2,env,q,r);
      }
      else
	DivRem(r1,r2,env,q,r);
      v=operator_minus(r2pow*v1,operator_times(q,v2,env),env);
      if (!psron){
	gen tmp=gcd(lgcd(r),lgcd(v));
	r=operator_div(r,tmp,env);
	v=operator_div(v,tmp,env);
      }
      else {
	if (!env || !env->moduloon){
	  hpow=pow(h,ddeg);
	  r=operator_div(r,hpow*g,env);
	  v=operator_div(v,hpow*g,env);
	  if (ddeg==1)
	    h=r20;
	  else
	    h=(pow(r20,ddeg)*h)/hpow;
	  g=r20;
	}
      }
      r1=r2;
      r2=r;
      v1=v2;
      v2=v;
    }
    a=r;
    b=v;
    // If a and b are not prime together, we may have a failure
    q=gcd(a,b,env);
    if (q.size()>1)
      return false;
    return true;
  }

  // Given [v_0 ... v_(2n-1)] (begin of the recurrence sequence) 
  // return [b_n...b_0] such that b_n*v_{n+k}+...+b_0*v_k=0
  // Example [1,-1,3,3] -> [1,-3,-6]
  // -> the recurrence relation is v_{n+2}=3v_{n+1}+6v_n
  // Algo: B*V=A with deg(A)< n and deg(B)=n -> B*V_truncated=A mod x^(2n)
  // psron=true by default to use the PSR Euclidean algorithm
  vecteur reverse_rsolve(const vecteur & v_orig,bool psron){
    if (v_orig.size()%2)
      return vecteur(1,gensizeerr(gettext("Argument must be a vector of even size")+gen(v_orig).print(context0)));
    vecteur v(v_orig);
    reverse(v.begin(),v.end());
    int n=v.size()/2;
    vecteur x2n(2*n+1),A,B;
    x2n[0]=1;
    egcd_pade(x2n,v,n,A,B,0,psron);
    vecteur G=gcd(A,B,0);
    v=B/G;
    reverse(v.begin(),v.end());
    v=trim(v,0);
    return v;
  }

  //***************************************************************
  // Fonctions independent on the actual implementation of modpoly
  //***************************************************************

  // given a, find u such that 
  // a[0]*...a[n-1]*u[n]+a[0]*...*a[n-2]*a[n]*u[n-1]+...+a[1]*...*a[n-1]*u[0]=1
  bool egcd(const vector<modpoly> & a,environment * env,vector<modpoly> & u){
    int n=a.size();
    if (n==0) return false; // setsizeerr(gettext("modpoly.cc/egcd"));
    // first compute the sequence of products
    // pi[0]=a[n-1], pi[k]=pi[k-1]*a[n-k-1], ... pi[n-2]=pi[n-3]*a[1]
    u.clear();
    u.reserve(n);
    vector<modpoly> pi;
    pi.reserve(n);
    pi.push_back(a[n-1]);
    modpoly tmp;
    for (int k=1;k<=n-2;k++){
      operator_times(pi[k-1],a[n-k-1],env,tmp);
      pi.push_back(tmp);
    }
    // cout << "a:" << a << endl;
    // cout << "pi:" << pi << endl;
    modpoly c(1,plus_one),U(1),v(1),d(1),q,r;
    // compute u[0] using egcd(a[0],p[n-2])
    // since a[0]*()+p[n-2]*u[0]=c
    // then solve ()=v[0]
    for (int k=0;k<=n-2;k++){
      egcd(a[k],pi[n-k-2],env,v,U,d);
      if (d.size()==1 && !is_one(d.front())){
	divmodpoly(v,d.front(),v);
	divmodpoly(U,d.front(),U);
	d.front()=1;
      }
      if (!is_one(d)) return false; // setsizeerr(gettext("modpoly.cc/egcd"));
      // multiply by v and U by c, compute new c, push u[]
      operator_times(U,c,env,tmp); DivRem(tmp,a[k],env,q,r); // r= U*c % a[k]
      u.push_back(r);
      operator_times(v,c,env,tmp); DivRem(tmp,pi[n-k-2],env,q,c); // c=(v*c) % pi[n-k-2];
    }
    u.push_back(c);
    // cout << "u:" << u << endl;
    return true;
  }
  
  // same as above
  /*
  vector<modpoly> egcd(const vector<modpoly> & a,environment * env){
    vector<modpoly> u;
    egcd(a,env,u);
    return u;
  }
  */

  modpoly simplify(modpoly & a, modpoly & b,environment * env){
    modpoly g;
    gcdmodpoly(a,b,env,g);
    a=operator_div(a,g,env);
    b=operator_div(b,g,env);
    return g;
  }

  static void inpowmod(const modpoly & p,const gen & n,const modpoly & pmod,environment * env,modpoly & res){
    if (is_zero(n)){
      res=one();
      return ;
    }
    if (is_one(n)){
      res=p;
      return;
    }
#if 1
    modpoly p2k(p),tmp,tmpq;
    res=one();
    gen N(n),q,r;
    while (!is_zero(N)){
      r=irem(N,2,q);
      N=iquo(N,2); // not sure q can be used because of inplace operations
      if (is_one(r)){
	operator_times(res,p2k,env,tmp);
	if (env)
	  DivRem(tmp,pmod,env,tmpq,res);
	else
	  swap(res,tmp); // res=tmp
      }
      operator_times(p2k,p2k,env,tmp);
      if (env)
	DivRem(tmp,pmod,env,tmpq,p2k);
      else
	swap(p2k,tmp); // res=tmp      
    }
#else    
    inpowmod(p,iquo(n,2),pmod,env,res);
    modpoly tmp,q;
    operator_times(res,res,env,tmp); 
    if (env)
      DivRem(tmp,pmod,env,q,res);
    else
      res=tmp; // res=(res*res) % pmod ;
    if (!is_zero(smod(n,2))){
      operator_times(res,p,env,tmp); 
      if (env)
	DivRem(tmp,pmod,env,q,res); // res=(res*p)%pmod;
      else
	res=tmp;
    }
#endif
  }

  modpoly powmod(const modpoly & p,const gen & n,const modpoly & pmod,environment * env){
    if (!ck_is_positive(n,0)){
      return vecteur(1,gensizeerr(gettext("modpoly.cc/powmod")));
    }
    modpoly res;
    inpowmod( (env?operator_mod(p,pmod,env):p) ,n,pmod,env,res);
    return res;
  }

  void hornerfrac(const modpoly & p,const gen &num, const gen &den,gen & res,gen & d){
    d=1;
    if (p.empty())
      res=0;
    else {
      modpoly::const_iterator it=p.begin(),itend=p.end();
      res=*it;
      ++it;
      if (it==itend){
	return;
      }
      d=den;
      for (;;){
	res=res*num+(*it)*d;
	++it;
	if (it==itend)
	  break;
	d=d*den;   
      }
    }    
  }

  gen horner(const modpoly & p,const fraction & f){
    if (p.empty())
      return 0;
    gen num=f.num,den=f.den,d=den;
    modpoly::const_iterator it=p.begin(),itend=p.end();
    gen res(*it);
    ++it;
    if (it==itend)
      return res;
    for (;;){
      res=res*num+(*it)*d;
      ++it;
      if (it==itend)
	break;
      d=d*den;   
    }
    return rdiv(res,d,context0);
  }

  gen horner(const modpoly & p,const gen & x,environment * env){
    int s=p.size();
    if (s==0)
      return 0;
    if (s==1)
      return p.front();
    if (is_inf(x)){
      if (s%2)
	return plus_inf*p.front();
      return x*p.front();
    }
    if (s==2){
      if (env && env->moduloon)
	return smod(p.front()*x+p.back(),env->modulo);
      else
	return p.front()*x+p.back();
    }
    if ( (!env || !env->moduloon) && x.type==_FRAC)
      return horner(p,*x._FRACptr);
    modpoly::const_iterator it=p.begin(),itend=p.end();
    gen res(*it);
    ++it;
    if (env && env->moduloon){
      for (;it!=itend;++it)
	res=smod(res*x+(*it),env->modulo);
    }
    else {
      for (;it!=itend;++it)
	res=res*x+(*it);
    }
    return res;
  }

  gen horner(const modpoly & p,const gen & x){
    return horner(p,x,0);
  }
   
  gen horner(const gen & g,const gen & x){
    if (g.type!=_VECT)
      return g;
    return horner(*g._VECTptr,x);
  }
  gen _horner(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,q,x;
    if (args.type!=_VECT)
      return symbolic(at_horner,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gensizeerr(contextptr);
    p=v.front();
    q=v[1];
    if (p.type==_VECT)
      return horner(*p._VECTptr,q);
    if (s==2)
      x=vx_var;
    else 
      x=v.back();
    if (!is_zero(derive(q,x,contextptr))) 
      return gensizeerr(contextptr);
    vecteur lv(1,x);
    lvar(p,lv);
    lvar(q,lv);
    gen aa=e2r(p,lv,contextptr),aan,aad;
    fxnd(aa,aan,aad);
    if ( ( (aad.type==_POLY)&&(aad._POLYptr->lexsorted_degree()) )
	 )
      return gensizeerr(contextptr);
    if (aan.type!=_POLY)
      return p;
    lv=vecteur(lv.begin()+1,lv.end());
    gen ba=e2r(q,lv,contextptr);
    vecteur a(polynome2poly1(*aan._POLYptr,1));
    return r2e(horner(a,ba),lv,contextptr)/r2e(aad,lv,contextptr);
  }
  static const char _horner_s []="horner";
  static define_unary_function_eval (__horner,&giac::_horner,_horner_s);
  define_unary_function_ptr5( at_horner ,alias_at_horner,&__horner,0,true);

  gen symb_horner(const modpoly & p,const gen & x,int d){
  // better suited if x is symbolic
    if (p.empty())
      return 0;
    modpoly::const_iterator it=p.begin(),itend=p.end();
    gen res;
    int i=itend-it-1;
    if (!i)
      return *it;
    for (;i>=0;++it,--i){
      if (i==d+1)
	res=res+(*it)*x;
      else {
	if (i==d)
	  res=res+(*it);
	else
	  res=res+(*it)*symbolic(at_pow,gen(makevecteur(x,i-d),_SEQ__VECT));
      }
    }
    return res;
  }

  gen symb_horner(const modpoly & p,const gen & x){
    return symb_horner(p,x,0);
  }

  // p=(X-x)q+p(x)
  gen horner(const modpoly & p,const gen & x,environment * env,modpoly & q){
    modpoly::const_iterator it=p.begin(),itend=p.end();
    if (p.empty()){
      q.clear();
      return 0;
    }
    q.resize(itend-it-1); 
    gen res(*it);
    ++it;
    if (it==itend)
      return res;
    q[0]=res; 
    if (env && env->moduloon){
      for (int pos=1;;++pos){
	res=smod(res*x+(*it),env->modulo);
	++it;
	if (it==itend)
	  break;
	q[pos]=res;
      }
    }
    else {
      if (x==1){
	for (int pos=1;;++pos){
	  res += *it ;
	  ++it;
	  if (it==itend)
	    break;
	  q[pos]=res;
	}
      }
      else {
	for (int pos=1;;++pos){
	  res=res*x+(*it);
	  ++it;
	  if (it==itend)
	    break;
	  q[pos]=res;
	}
      }
    }
    return res;
  }

  static modpoly taylordiff(const modpoly & p,const gen & x){
    int d=p.size();
    modpoly res(p),P(p);
    for (int i=1;i<=d;++i){
      res[d-i]=horner(P,x);
      P=derivative(P)/gen(i);
    }
    return res;
  }
  
  void modpoly2mpzpoly(const modpoly & p,mpz_t * & res){
    const_iterateur it=p.begin(),itend=p.end();
    res=new mpz_t[itend-it];
    for (int i=0;it!=itend;++i,++it){
      if (it->type==_INT_)
	mpz_init_set_si(res[i],it->val);
      else
	mpz_init_set(res[i],*it->_ZINTptr);
    }
  }

  void taylorshift1(mpz_t * tab,int size){
    for (int i=1;i<size;++i){
      // tab[j]=tab[j-1]+tab[j] for j from 1 to size-i
      for (int j=1;j<=size-i;++j){
	mpz_add(tab[j],tab[j],tab[j-1]);
      }
    }
  }

  void mpzpoly2modpoly(mpz_t * p,modpoly & res){
    iterateur it=res.begin(),itend=res.end();
    for (int i=0;it!=itend;++i,++it){
      *it=*(p+i);
      mpz_clear(p[i]);
    }
    delete [] p;
  }

  bool isintpoly(const modpoly & p){
    const_iterateur it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (!is_integer(*it))
	return false;
    }
    return true;
  }

  // shift polynomial
  modpoly taylor(const modpoly & p,const gen & x,environment * env){
    if (p.empty())
      return p;
    if ( (!env || !env->moduloon) && x.type==_FRAC) // use derivatives of p
      return taylordiff(p,x);
    modpoly res,a,b;
    a=p;
    if (x==1 && a.size()>5 && isintpoly(a)){
      mpz_t * tab;
      modpoly2mpzpoly(a,tab);
      taylorshift1(tab,a.size());
      mpzpoly2modpoly(tab,a);
      return a;
    }
    int d=p.size();
    for (int i=0;i<d;++i){
      res.push_back(horner(a,x,env,b));
      a.swap(b); // a=b;
    }
    reverse(res.begin(),res.end());
    return res;
  }

  gen lgcd(const dense_POLY1 & p){
    if (p.empty())
      return 1;
    dense_POLY1::const_iterator it=p.begin(),itend=p.end();
    gen n(*it),n1(1);
    for (;it!=itend;++it){
      n=gcd(n,*it);
      if (n==n1)
        return 1;
    }
    return n;
  }

  gen ppz(dense_POLY1 & p){
    gen n(lgcd(p));
    p=p/n;
    return n;
  }

  gen norm(const dense_POLY1 & p,GIAC_CONTEXT){
    gen res,tmp;
    dense_POLY1::const_iterator it=p.begin(), itend=p.end();
    for (;it!=itend;++it){
      tmp=abs(*it,contextptr);
      if (is_strictly_greater(tmp,res,contextptr)) // (res<tmp)
	res=tmp;
    }
    return res;
  }

  // assuming pmod and qmod are prime together, find r such that
  // r = p mod pmod  and r = q mod qmod
  // hence r = p + A*pmod = q + B*qmod
  // or A*pmod -B*qmod = q - p
  // assuming u*pmod+v*pmod=d we get
  // A=u*(q-p)/d
  dense_POLY1 ichinrem(const dense_POLY1 &p,const dense_POLY1 & q,const gen & pmod,const gen & qmod){
    gen u,v,d,tmp,pqmod(pmod*qmod);
    egcd(pmod,qmod,u,v,d);
    // cout << u << "*" << pmod << "+" << v << "*" << qmod << "=" << d << " " << u*pmod+v*qmod << endl;
    dense_POLY1::const_iterator a = p.begin();
    dense_POLY1::const_iterator a_end = p.end();
    dense_POLY1::const_iterator b = q.begin();
    dense_POLY1::const_iterator b_end = q.end();
    int n=(a_end-a), m=(b_end-b);
    dense_POLY1 res;
    res.reserve(giacmax(n,m));
    for (;m>n;++b,--m)
      res.push_back(smod(iquo(u*(*b),d),pqmod));
    for (;n>m;++a,--n)
      res.push_back(smod(*a-iquo(u*(*a),d),pqmod));
    for (;a!=a_end;++a,++b){
      res.push_back(smod(*a+iquo(u*(*b-*a),d) *pmod,pqmod)) ;
      // cout << a->value << " " << b->value << "->" << tmp << " " << pqmod << endl;
    }
    return res;
  }

  // assuming pmod and qmod are prime together, find r such that
  // r = p mod pmod  and r = q mod qmod
  // hence r = p + A*pmod = q + B*qmod
  // or A*pmod -B*qmod = q - p
  // assuming u*pmod+v*qmod=d we get
  // A=u*(q-p)/d
  modpoly chinrem(const modpoly & p,const modpoly & q, const modpoly & pmod, const modpoly & qmod,environment * env){
    modpoly u,v,d,r;
    egcd(pmod,qmod,env,u,v,d);
    r=operator_plus(p,operator_times(operator_times(u,operator_div(operator_minus(q,p,env),d,env),env),pmod,env),env);
    if (r.size()>=pmod.size()+qmod.size()-1)
      r=operator_mod(r,operator_times(pmod,qmod,env),env);
    return r;
  }

  void divided_differences(const vecteur & x,const vecteur & y,vecteur & res,environment * env){
    res=y;
    int s=x.size();
    for (int k=1;k<s;++k){
      if (env && env->moduloon){
	for (int j=s-1;j>=k;--j){
	  res[j]=smod((res[j]-res[j-1])*invmod(x[j]-x[j-k],env->modulo),env->modulo);
	}
      }
      else {
	for (int j=s-1;j>=k;--j){
	  res[j]=(res[j]-res[j-1])/(x[j]-x[j-k]);
	}
      }
    }
  }

  void interpolate(const vecteur & x,const vecteur & y,modpoly & res,environment * env){
    vecteur alpha;
    divided_differences(x,y,alpha,env);
    unsigned s=x.size();
    res.clear();
    res.reserve(s);
    int j=s-1;
    res.push_back(alpha[j]);
    for (j--;j>=0;j--){
      res.push_back(alpha[j]);
      iterateur it=res.end()-2,itbeg=res.begin()-1;
      const gen & fact = x[j];
      for (;it!=itbeg;it-=2){
	gen & tmp = *it;
	++it;
	*it -= tmp*fact;
	if (env && env->moduloon)
	  *it=smod(*it,env->modulo);
      }
    }
  }

  // Multiplication of multivariate polynomials using Lagrange interpolation
  void mulpoly_interpolate(const polynome & p,const polynome & q,polynome & res,environment * env){
    int s=p.dim;
    gen modulo;
    if (env &&env->moduloon)
      modulo=env->modulo;
    if (s<2){
      mulpoly(p,q,res,modulo);
      return;
    }
    bool estreel=poly_is_real(p) && poly_is_real(q);
    polynome pxn,qxn;
    convert_xn(p,pxn);
    convert_xn(q,qxn);
    int pd=p.degree(s-1);
    int qd=q.degree(s-1);
    int sd=pd+qd;
    vecteur x(sd+1);
    vecteur y(sd+1);
    modpoly v;
    index_t * degptr=0;
    for (int i=0;i<=sd;++i){
      x[i]=i;
      y[i]=new ref_polynome(s);
      mulpoly_interpolate(pevaln(pxn,i,modulo,degptr,estreel),pevaln(qxn,i,modulo,degptr,estreel),*y[i]._POLYptr,env);
    }
    interpolate(x,y,v,env);
    poly12polynome(v,s,res,s);
  }

  // Fast Fourier Transform, f the poly sum_{j<n} f_j x^j, 
  // and w=[1,omega,...,omega^[m-1]] with m a multiple of n (m=step*n)
  // return [f(1),f(omega),...,f(omega^[n-1]) [it's indeed n, not m]
  // WARNING f is given in ascending power
  void fft(const modpoly & f,const modpoly & w ,modpoly & res,environment * env){
    unsigned long n=f.size();
    if (n==1){
      res = f;
      return ;
    }
    unsigned long m=w.size();
    unsigned long step=m/n;
    unsigned k=0;
    if (n%2){
      for (k=3;k*k<=n;k++){
	if (!(n%k))
	  break;
      }
    }
    else
      k=2;
    if (k*k>n){ 
      // prime size, slow discrete Fourier transform
      res.clear();
      res.reserve(n);
      gen tmp;
      unsigned pos;
      for (unsigned i=0;i<n;++i){
	tmp = 0;
	pos = 0;
	for (unsigned j=0;j<n;++j){
	  tmp = tmp + f[j]*w[pos];
	  pos = (pos+i*step)%m;
	  if (env && env->moduloon)
	    tmp=smod(tmp,env->modulo);
	}
	res.push_back(tmp);
      }
      return;
    }
    if (k!=2){
      // assumes n is divisible by k, nk=n/k
      // P(X)=P_k(X)*[X^nk]^(k-1)+...+P_1(X) degree(P_k)<nk
      // P(w^(kj+l))= Q_l ( (w^k)^j )
      // with Q_l=P_1^(w^l)+w^(nk)*P_2^(w^l)+...
      unsigned long n2=n/k;
      vector<modpoly> Q(k),Qfft(k);
      for (unsigned j=0;j<k;++j)
	Q[j]=vecteur(n2,0);
      gen tmp;
      for (unsigned j=0;j<k;j++){
	// find Q[j]
	for (unsigned i=0;i<n2;i++){
	  tmp=0;
	  for (unsigned J=0;J<k;J++){
	    tmp += f[J*n2+i]*w[(J*j*n2*step)%m];
	  }
	  tmp=tmp*w[j*step*i];
	  if (env && env->moduloon)
	    tmp=smod(tmp,env->modulo);
	  Q[j][i]=tmp;
	}
	fft(Q[j],w,Qfft[j],env);
      }
      // build fft
      res.clear();
      res.reserve(n);
      for (unsigned i=0;i<n2;++i){
	for (unsigned j=0;j<k;++j)
	  res.push_back(Qfft[j][i]);
      }
      return;
    }
    // Compute r0=sum_[j<n/2] (f_j+f_(j+n/2))*x^j
    // and r1=sum_[j<n/2] (f_j-f_(j+n/2))*omega^[step*j]*x^j
    unsigned long n2=n/2;
    modpoly r0,r1;
    r0.reserve(n2); r1.reserve(n2);
    const_iterateur it=f.begin(),itn=it+n2,itend=itn,itk=w.begin();
    gen tmp;
    for (;it!=itend;++itn,itk+=step,++it){
      tmp=(*it)+(*itn);
      if (env && env->moduloon)
	tmp=smod(tmp,env->modulo);
      r0.push_back(tmp);
      tmp=((*it)-(*itn))*(*itk);
      if (env && env->moduloon)
	tmp=smod(tmp,env->modulo);
      r1.push_back(tmp);
    }
    // Recursive call
    modpoly r0f(n2),r1f(n2);
    fft(r0,w,r0f,env);
    fft(r1,w,r1f,env);
    // Return a mix of r0/r1
    res.clear();
    res.reserve(n);
    it=r0f.begin(); itend=it+n2; itn=r1f.begin();
    for (;it!=itend;){
      res.push_back(*it);
      ++it;
      res.push_back(*itn);
      ++itn;
    }
  }

  // Convolution of p and q, omega a n-th root of unity, n=2^k
  // WARNING p0 and q0 are given in ascending power
  void fftconv(const modpoly & p,const modpoly & q,unsigned long k,unsigned long n,const gen & omega,modpoly & pq,environment * env){
    vecteur w;
    w.reserve(n);
    w.push_back(1);
    gen omegan(omega),tmp;
    for (unsigned long i=1;i<n;++i){
      w.push_back(omegan);
      omegan=omegan*omega;
      if (env && env->moduloon)
	omegan=smod(omegan,env->modulo);
    }
    modpoly alpha(n),beta(n),gamma(n);
    fft(p,w,alpha,env);
    fft(q,w,beta,env);
    for (unsigned long i=0;i<n;++i){
      tmp=alpha[i]*beta[i];
      if (env && env->moduloon)
	gamma[i]=smod(tmp,env->modulo);
      else
	gamma[i]=tmp;
    }
    vecteur winv(1,1);
    winv.reserve(n);
    for (unsigned long i=1;i<n;++i)
      winv.push_back(w[n-i]);
    fft(gamma,winv,pq,env);
    pq=pq/gen(int(n));
    /*
    modpoly check(n);
    fft(alpha,winv,check,env);
    check=check/gen(int(n));
   */
  }

  // Convolution of p and q, omega a n-th root of unity, n=2^k
  // p and q are given in descending power order
  void fftconv(const modpoly & p0,const modpoly & q0,unsigned long k,const gen & omega,modpoly & pq,environment * env){
    unsigned long n= 1u <<k;
    // Adjust sizes
    modpoly p(p0),q(q0);
    reverse(p.begin(),p.end());
    reverse(q.begin(),q.end());
    unsigned long ps=p.size(),qs=q.size();
    for (unsigned long i=ps;i<n;++i)
      p.push_back(0);
    for (unsigned long i=qs;i<n;++i)
      q.push_back(0);
    fftconv(p,q,k,n,omega,pq,env);
    reverse(pq.begin(),pq.end());
    pq=trim(pq,env);
  }

  // Product of polynomial with integer coeffs using FFT
  // Only useful for large degree (around 1000 for coeff of size 2^degree(p))
  // Following ntl src/ZZX1.c SSMul
  void fftmult(const modpoly & p,const modpoly & q,modpoly & pq){
    int ps=p.size(),qs=q.size();
    unsigned long l=gen(ps+qs-1).bindigits()-1; // m=2^l <= deg(p*q) < 2^{l+1}
    // long m2 = 1u << (l + 1); /* m2 = 2m = 2^{l+1} */
    gen P=norm(p,0), Q=norm(q,0); // coeff assumed to be integers -> no context
    P=gen(giacmin(ps,qs))*P*Q+1;
    unsigned long bound=P.bindigits()+1; // 2^bound=smod bound on coeff of p*q
    unsigned long r=(bound >> l)+1;
    unsigned long mr=r<<l; // 2^mr is also a smod bound on coeff op p*q
    // Now work modulo p=2^{m*r}+1, using 2^r as a 2m root of unity
    environment * env=new environment;
    env->modulo=pow(plus_two,mr)+1;
    env->pn=env->modulo;
    env->moduloon=true;
    fftconv(p,q,l+1,pow(plus_two,r),pq,env);
  }

  modpoly fftmult(const modpoly & p,const modpoly & q){
    modpoly pq;
    fftmult(p,q,pq);
    return pq;
  }

#ifndef NEWGCD1
  bool giac_gcd_modular_algo1(polynome &p,polynome &q,polynome &d){
    environment * env=new environment;
    dense_POLY1 pp(modularize(p,0,env)),qq(modularize(q,0,env));
    if (is_undef(pp) || is_undef(qq))
      return false;
    // cout << "modular gcd 1 " << pp << " " << qq << endl;
    gen gcdfirstcoeff(gcd(pp.front(),qq.front()));
    int gcddeg= giacmin(pp.size(),qq.size())-1;
    gen bound(pow(gen(2),gcddeg+1)* abs(gcdfirstcoeff,context0) * min(norm(pp,context0), norm(qq,context0),context0));
    env->moduloon = true;
    // env->modulo=nextprime(max(gcdfirstcoeff+1,gen(30011),context0)); 
    env->modulo=30011;
    env->pn=env->modulo;
    if (poly_is_real(p) && poly_is_real(q))
      env->complexe=false;
    else
      env->complexe=true;
    gen productmodulo(1);
    dense_POLY1 currentgcd(p.dim),p_simp(p.dim),q_simp(p.dim),rem(p.dim);
    // 30011 leaves 267 primes below the 2^15 bound 
    for (;;){
      if (env->complexe){
	while (smod(env->modulo,4)==1)
	  env->modulo=nextprime(env->modulo+2);
      }
      if (is_zero(smod(gcdfirstcoeff,env->modulo))){
	env->modulo=nextprime(env->modulo+1);
	continue;
      }
      modpoly gcdmod;
      gcdmodpoly(pp,qq,env,gcdmod);
      if (is_undef(gcdmod))
	return false;
      // cout << "Modulo:" << modulo << " " << gcdmod << endl;
      gen adjustcoeff=gcdfirstcoeff*invmod(gcdmod.front(),env->modulo);
      mulmodpoly(gcdmod,adjustcoeff,env,gcdmod);
      int m=gcdmod.size()-1;
      if (!m){
	d=polynome(gen(1),1);
	delete env;
	return true;
      }
      // combine step
      if (m<gcddeg){ // previous prime was bad
	gcddeg=m;
	currentgcd=gcdmod;
	productmodulo=env->modulo;
      }
      else {
	if (m==gcddeg){ // start combine
	  if (productmodulo==gen(1)){ // no need to combine primes
	    currentgcd=gcdmod;
	    productmodulo=env->modulo;
	  }
	  else {
	    // cout << "Old gcd:" << productmodulo << " " << currentgcd << endl ;
	    currentgcd=ichinrem(gcdmod,currentgcd,env->modulo,productmodulo);
	    // cout << "Combined to " << currentgcd << endl;
	    productmodulo=productmodulo*env->modulo;
	  }
	}
	// m>gcddeg this prime is bad, just ignore
      }
      //      if (productmodulo>bound){
      modpoly dmod(modularize(currentgcd,productmodulo,env));
      if (is_undef(dmod))
	return false;
      ppz(dmod);
      if ( (DenseDivRem(pp,dmod,p_simp,rem,true)) && (rem.empty()) && (DenseDivRem(qq,dmod,q_simp,rem,true)) && (rem.empty()) ){
	p=unmodularize(p_simp);
	q=unmodularize(q_simp);
	d=unmodularize(dmod);
	delete env;
	return true;
      }
      // }
      // increment modulo
      // env->modulo=nextprime(env->modulo+2);
      
      do 
	env->modulo=nextprime(env->modulo+2);
      while (is_zero(gcdfirstcoeff % env->modulo)); 
      
      // since env->modulo > gcdfirstcoeff the loop breaks immediately
    }
    delete env;
    return false;
  }

#else // OLDGCD1

  bool giac_gcd_modular_algo1(polynome &p,polynome &q,polynome &d){
    gen lcoeffp=p.coord.front().value,lcoeffq=q.coord.front().value;
    gen pmax,qmax;
    Tlistmax(p,pmax);
    Tlistmax(q,qmax);
    gen gcdlcoeff=gcd(lcoeffp,lcoeffq);
    gen absgcdlcoeff=abs(gcdlcoeff,context0);
    int gcddeg= p.lexsorted_degree()+1;
    gen modulo=30011;
    // leaves many primes below the 2^15 bound 
    bool real= poly_is_real(p) && poly_is_real(q);
    if (real)
      modulo=536871001;
    gen productmodulo(1);
    polynome currentpcof,currentqcof;
    for (;;modulo=nextprime(modulo+2)){
      // find next eligible prime
      for (;;){
	if (!real){
	  while (smod(modulo,4)==1)
	    modulo=nextprime(modulo+2);
	}
	if (is_zero(gcdlcoeff%modulo))
	  modulo=nextprime(modulo+2);
	else
	  break;
      }
      polynome gcdmod,pcof,qcof;
      bool real;
      gcdmod_dim1(p,q,modulo,gcdmod,pcof,qcof,true,real);
      int m=gcdmod.lexsorted_degree();
      if (!m){
	d=polynome(1,1);
	return true;
      }
      mulpoly(gcdmod,gcdlcoeff,gcdmod);
      gcdmod=smod(gcdmod,modulo);
      if (m<gcddeg){ 
	// previous primes were bad
	gcddeg=m;
	d=gcdmod;
	currentpcof=pcof;
	currentqcof=qcof;
	productmodulo=modulo;
      }
      else {
	if (m==gcddeg){ 
	  // combine step
	  d=ichinrem(gcdmod,d,modulo,productmodulo);
	  currentpcof=ichinrem(pcof,currentpcof,modulo,productmodulo);
	  currentqcof=ichinrem(qcof,currentqcof,modulo,productmodulo);
	  productmodulo=productmodulo*modulo;
	}
	// m>gcddeg this prime is bad, just ignore
      }
      // now we have d*currentpcof=p*gcdlcoeff mod productmodulo
      // If max(coeff(d))*max(coeff(currentpcof))*min(sizes)+max(coeff(p))*abs(gcdlcoeff) < productmodulo, then 
      // d*currentpcof=p*gcdlcoeff
      gen curgcdmax,curpcofmax,curqcofmax,coeff;
      gen dz=ppz(d,false),pcofz=ppz(currentpcof,false),qcofz=ppz(currentqcof,false);
      Tlistmax(d,curgcdmax);
      Tlistmax(currentpcof,curpcofmax);
      Tlistmax(currentqcof,curqcofmax);
      curgcdmax = curgcdmax/dz;
      curpcofmax = curpcofmax/pcofz;
      curqcofmax = curqcofmax/qcofz;
      if (is_strictly_greater(productmodulo,gen(gcddeg)*curpcofmax*curgcdmax+pmax*absgcdlcoeff,context0) &&
	  is_strictly_greater(productmodulo,gen(gcddeg)*curqcofmax*curgcdmax+qmax*absgcdlcoeff,context0) ){
	d=d/dz;
	p=(currentpcof*dz)/gcdlcoeff;
	q=(currentqcof*dz)/gcdlcoeff;
	return true;
      }
    }
    return false;
  }
#endif // OLDGCD1

#ifdef HAVE_LIBNTL
#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t ntl_mutex = PTHREAD_MUTEX_INITIALIZER;
#endif

  void ininttype2ZZ(const inttype & temp,const inttype & step,NTL::ZZ & z,const NTL::ZZ & zzstep){
    if (temp==0){
      long j=0;
      z=j;
      return;
    }
    inttype q;
    inttype rem(irem(temp,step,q));
#ifndef NO_STDEXCEPT
    if (rem.type!=_INT_) setsizeerr(gettext("modpoly.cc/ininttype2ZZ"));
#endif
    long longtemp=rem.val;
    ininttype2ZZ(q,step,z,zzstep);
    NTL::ZZ zztemp;
    zztemp=longtemp;
    z=z*zzstep+zztemp;
  }

  NTL::ZZ inttype2ZZ(const inttype & i){
    inttype step(65536); // 2^16 
    inttype temp(i),q;
    NTL::ZZ zzstep;
    zzstep=65536;
    NTL::ZZ z;
    ininttype2ZZ(temp,step,z,zzstep);
    // cout << "cl_I2ZZ" << i << " -> " << z << endl;
    return NTL::ZZ(z);
  }

  void inZZ2inttype(const NTL::ZZ & zztemp,const NTL::ZZ & zzstep,inttype & temp,const inttype & step){
    if (zztemp==0){
      temp=0;
      return ;
    }
    NTL::ZZ zzdiv=zztemp/zzstep;
    long longtemp;
    conv(longtemp,zztemp-zzstep*zzdiv);
    inZZ2inttype(zzdiv,zzstep,temp,step);
    longlong llongtemp=longtemp;
    temp=temp*step+inttype(llongtemp);
  }


  inttype ZZ2inttype(const NTL::ZZ & z){
    if (z<0)
      return -ZZ2inttype(-z);
    inttype step(65536); // 2^16 
    inttype temp(0);
    NTL::ZZ zzstep;
    zzstep=65536;
    NTL::ZZ zztemp(z);
    inZZ2inttype(zztemp,zzstep,temp,step);
    // cout << "zz2cl_I " << z << " -> " << temp << endl;
    return inttype(temp);
  }

  NTL::ZZX tab2ZZX(const inttype * tab,int degree){
    NTL::ZZX f;
    f.rep.SetMaxLength(degree+1);
    f.rep.SetLength(degree+1);
    for (int i=0;i<=degree;i++)
      SetCoeff(f,i,inttype2ZZ(tab[i]));
    return NTL::ZZX(f);
  }

  void ZZX2tab(const NTL::ZZX & f,int & degree,inttype * & tab){
    // cout << f << endl;
    degree=deg(f);
    tab = new inttype[degree+1] ;
    for (int i=degree;i>=0;i--){
      inttype c=ZZ2inttype(coeff(f,i));
      tab[i]=c;
    }
  }

  NTL::GF2X modpoly2GF2X(const modpoly & p){
    NTL::GF2X f;
    int degree=p.size()-1;
    for (int i=0;i<=degree;i++)
      SetCoeff(f,i,p[degree-i].val);
    cerr << f << endl;
    return f;
  }

  modpoly GF2X2modpoly(const NTL::GF2X & f){
    // cout << f << endl;
    int degree=deg(f);
    modpoly tab (degree+1) ;
    for (int i=degree;i>=0;i--){
      tab[i]=int(unsigned(rep(coeff(f,i))));
    }
    reverse(tab.begin(),tab.end());
    return tab;
  }

  // Don't forget to set the modulus with ZZ_p::init(p) before calling this 
  NTL::ZZ_pX modpoly2ZZ_pX(const modpoly & p){
    NTL::ZZ_pX f;
    int degree=p.size()-1;
    for (int i=0;i<=degree;i++){
      NTL::ZZ_p tmp;
      conv(tmp,inttype2ZZ(p[degree-i]));
      SetCoeff(f,i,tmp);
    }
    cerr << f << endl;
    return f;
  }

  modpoly ZZ_pX2modpoly(const NTL::ZZ_pX & f){
    // cout << f << endl;
    int degree=deg(f);
    modpoly tab (degree+1) ;
    for (int i=degree;i>=0;i--){
      tab[i]=ZZ2inttype(rep(coeff(f,i)));
    }
    reverse(tab.begin(),tab.end());
    return tab;
  }

  bool polynome2tab(const polynome & p,int deg,inttype * tab){
    inttype n0(0);
    if (p.dim!=1) return false; // setsizeerr(gettext("modpoly.cc/polynome2tab"));
    if (p.coord.empty())
      return true;
    if ( deg!=p.lexsorted_degree()) return false; // setsizeerr(gettext("modpoly.cc/polynome2tab"));
    int curpow=deg;
    vector< monomial<gen> >::const_iterator it=p.coord.begin();
    vector< monomial<gen> >::const_iterator itend=p.coord.end();
    for (;it!=itend;++it){
      int newpow=it->index.front();
      for (;curpow>newpow;--curpow)
	tab[curpow]=n0;
      tab[curpow]=it->value;
      --curpow;
    }
    for (;curpow>-1;--curpow)
      tab[curpow]=n0;
    return true;
  }

  polynome tab2polynome(const inttype * tab,int deg){
    vector< monomial<gen> > v;
    index_t i;
    i.push_back(deg);
    const inttype * tabend=tab+deg+1;
    gen n0(0);
    for (;tab!=tabend;--i[0]){
      --tabend;
      if (gen(*tabend)!=n0)
	v.push_back(monomial<gen>(gen(*tabend),i));
    }
    return polynome(1,v);
  }

  int ntlgcd(inttype *p, int pdeg,inttype * q,int qdeg, inttype * & res, int & resdeg,int debug=0){
    NTL::ZZX f(tab2ZZX(p,pdeg));
    NTL::ZZX g(tab2ZZX(q,qdeg));
    NTL::ZZX d(GCD(f,g));
    ZZX2tab(d,resdeg,res);
    return resdeg;
  }

  bool gcd_modular_algo1(polynome &p,polynome &q,polynome &d,bool compute_cof){
    if (!poly_is_real(p) || !poly_is_real(q))
      return giac_gcd_modular_algo1(p,q,d);
    int np=p.lexsorted_degree();
    int nq=q.lexsorted_degree();
    if (np<NTL_MODGCD || nq<NTL_MODGCD)
      return giac_gcd_modular_algo1(p,q,d);
#ifdef HAVE_LIBPTHREAD
    int locked=pthread_mutex_trylock(&ntl_mutex);
#endif // HAVE_LIBPTHREAD
    if (locked)
      return giac_gcd_modular_algo1(p,q,d);
    bool res=true;
    try {
      inttype tabp[np+1]; // dense rep of the polynomial
      if (!polynome2tab(p,np,tabp))
	return false;
      inttype tabq[nq+1]; // dense rep of the polynomial
      if (!polynome2tab(q,nq,tabq))
	return false;
      int nd;
      inttype * res;
      ntlgcd(tabp,np,tabq,nq,res,nd);
      d=tab2polynome(res,nd);
      // cout << "PGCD=" << d << endl;
      delete [] res;
      if (compute_cof){
	p = p/d;
	q = q/d;
      }
    } catch(std::runtime_error & e){
      res=false;
    }
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&ntl_mutex);
#endif
    return res;
  }

#else // HAVE_LIBNTL

  bool gcd_modular_algo1(polynome &p,polynome &q,polynome &d,bool compute_cof){
    return giac_gcd_modular_algo1(p,q,d);
  }
#endif // HAVE_LIBNTL


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
