// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c -Wall modfactor.cc" -*-
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
#include "sym2poly.h"
#include "modpoly.h"
#include "modfactor.h"
#include "giacintl.h"
#include <stdlib.h>
#include <cmath>
#include "pari.h"
#include "gen.h"

// uncomment to remove NTL factorization
// #undef HAVE_LIBNTL
// #undef HAVE_LIBPARI

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  static int debuglevel=0;
  // ******************************************************************
  // Modular factorization for univariate polynomial factorization in Z
  // assumes q is univariate, square-free in Z/modulo*Z
  // assumes modulo is prime !=2
  // ******************************************************************

  // find modular roots and linear factors
  bool roots(const modpoly & q,environment * env, vecteur & v,vector<modpoly> & w){
    modpoly ddfactor(q);
    modpoly x(xpower1());
    normalize_env(env);
    // try modulars from -modulo/2 to modulo/2
    int moduloover2=env->pn.val/2;
    if (env->complexe){
      for (int i=-moduloover2;i<=moduloover2;i++){
	for (int j=-moduloover2;j<=moduloover2;j++){
	  gen tmp(i,j);
	  if ( is_zero(horner(ddfactor,tmp,env)) ){
	    modpoly linearfact(x);
	    linearfact=linearfact-tmp*one();
	    v.push_back(tmp);
	    w.push_back(linearfact);
	    ddfactor=operator_div(ddfactor,linearfact,env);
	  }
	}
      }
      return true;
    }
    modpoly xtop(xpowerpn(env));
    if (is_undef(xtop))
      return false;
    if (ddfactor.size()-1){
      for (int i=0;i<env->pn.val;i++){
	int dddeg=ddfactor.size()-1;
	if (!dddeg)
	  break; // no more linear factor to extract
	if (dddeg==1){
	  w.push_back(ddfactor);
	  v.push_back(-ddfactor[1]*invenv(ddfactor[0],env));
	  break;
	}
	gen gi=env->coeff.makegen(i);
	gen evaluation=horner(ddfactor,gi,env);
	// cerr << gi << ":" << evaluation << endl;
	if ( is_zero(evaluation) ){
	  modpoly linearfact(x);
	  linearfact=linearfact-gi*one();
	  v.push_back(gi);
	  w.push_back(linearfact);
	  ddfactor=operator_div(ddfactor,linearfact,env);
	}
      }
    }
    return true;
  }

  // v[i]=x^(pn*i) mod q
  // matrix of the v[i] for i=0..jstart or i=0..degree(q) if jstart=0
  void qmatrix(const modpoly & q,environment * env,vector<modpoly> & v,int jstart){
    v.clear();
    int dim;
    if (jstart)
      dim=jstart;
    else
      dim=q.size()-1;
    normalize_env(env);
    v.reserve(dim);
    modpoly temp(one()),temp2,temp3;
    v.push_back(temp);
    if ( env->pn.type==_INT_ && env->pn.val<signed(q.size()) ){
      int p=env->pn.val;
      for (int i=1;i<dim;i++){
	temp2=temp;
	shiftmodpoly(temp2,p); // shift instead of *x^p
	DivRem(temp2,q,env,temp3,temp);
	v.push_back(temp);
      }
    }
    else {
      modpoly xtopn(powmod(xpower1(),env->pn,q,env));
      for (int i=1;i<dim;i++){
	operator_times(temp,xtopn,env,temp2); 
	DivRem(temp2,q,env,temp3,temp); // temp=(temp*xtop) % q;
	v.push_back(temp);
      }
    }
  }

  // compute s(x)=r(x^pn) mod q using the q-matrix
  static bool xtoxpowerpn(const modpoly & r, const vector<modpoly> & v, environment * env,int qsize,modpoly & s){
    s.clear();
    if (r.empty())
      return true;
    normalize_env(env);
    int rs=r.size();
    if ((rs-1)*env->pn.val<qsize){
      s=x_to_xp(r,env->pn.val);
      return !is_undef(s);
    }
    int d=v.size();
    if (rs-1>=d)
      return false; // setsizeerr();
    int maxdeg(1); // maximal degree+1
    // make an array of iterator pointing to the cst coeff of v[]
    // and find maximal degree of v
#if 1 // def VISUALC
    modpoly::const_iterator *itv = new modpoly::const_iterator[d],*itvptr;
#else
    modpoly::const_iterator itv[d],*itvptr;
#endif
    vector<modpoly>::const_iterator vitend=v.end(),vit=v.begin();
    for (itvptr=itv;vit!=vitend;++vit,++itvptr){
      * itvptr=vit->end();
      maxdeg=giacmax(maxdeg,int(vit->size()));
    }
#if 1 // def VISUALC
    gen * res = new gen[maxdeg];
#else
    gen res[maxdeg];
#endif
    gen * resptr=res;
    modpoly::const_iterator itend=r.end(),itbeg=r.begin();
    --itend;
    --itbeg;
    if ( env->moduloon && env->pn.type==_INT_ && env->pn.val<smallint ){
      for (int i=0;i<maxdeg;++i,++resptr){ // compute coeff of x^i
	int res=0; // init coeff
	itvptr=itv; // itvptr points to itv[0], after the coeff of x^i in v[0]
	vit=v.begin();
	for (modpoly::const_iterator it=itend;it!=itbeg;--it,++itvptr,++vit){
	  if ( * (int *) &(*itvptr)!=0){
	    if (*itvptr!=vit->begin()){
	      --(*itvptr); // decreases iterator to increase power of x to x^i
	      res += (it->val)*((*itvptr)->val);
	    }
	    else
	      * (int *) &(*itvptr)=0; // mark that v[] has no coeff of order >=i
	  }
	}
	*resptr=gen(res);
      }
    }
    else {
      for (int i=0;i<maxdeg;++i,++resptr){ // compute coeff of x^i
	*resptr=gen( 0); // init coeff
	itvptr=itv; // itvptr points to itv[0], after the coeff of x^i in v[0]
	vit=v.begin();
	// in the loop we add to *resptr the coeff of x^i in r[j]*v[j] 
	// for increasing j
	// therefore the r iterator it starts at itend (for r[0]) and decreases
	// itvptr starts at &itv[] and increases
	// vit starts at v.begin() and increases
	for (modpoly::const_iterator it=itend;it!=itbeg;--it,++itvptr,++vit){
	  if ( * (int *)&(*itvptr)!=0){
	    if (*itvptr!=vit->begin()){
	      --(*itvptr); // decreases iterator to increase power of x to x^i
	      *resptr=*resptr+(*it)*(*(*itvptr));
	    }
	    else
	      * (int *) &(*itvptr)=0; // mark that v[] has no coeff of order >=i
	  }
	}
      }
    }
    // trim the result into a modpoly
    --resptr;
    // here resptr points to the highest coeff of x^i in the array res[maxdeg]
    if (env->moduloon){
      for (;resptr!=res-1;--resptr){
	// cout << *resptr << " " << env->modulo << endl;
	if (!is_zero(smod(*resptr,env->modulo)))
	  break;   
      }
      for (;resptr!=res-1;--resptr){
	s.push_back(smod(*resptr,env->modulo));
      }
    }
    else {
      for (;resptr!=res-1;--resptr){
	if (!is_zero(*resptr))
	  break;
      }
      for (;resptr!=res-1;--resptr){
	s.push_back(*resptr);
      }      
    }
#if 1 // def VISUALC
    delete [] itv;
    delete [] res;
#endif
    return true;
  }

  static gen lastnonzero(const dense_POLY1 & q) {
    int d=q.size();
    gen n( 0),n0( 0);
    for (;d;--d){
      n=q[d-1];
      if (!is_zero(n))
	return n;
    }
    return 0;
  }

  // distinct degree factorization
  bool ddf(const modpoly & q,const vector<modpoly> & qmat,environment * env,vector< facteur<modpoly> >& v){
    modpoly xtop(powmod(xpower1(),env->pn,q,env));
    modpoly ddfactor;
    gcdmodpoly(operator_minus(xtop,xpower1(),env),q,env,ddfactor);
    if (is_undef(ddfactor))
      return false;
    modpoly qrem(operator_div(q,ddfactor,env));
    if (ddfactor.size()-1)
      v.push_back( facteur<modpoly>(ddfactor,1) );
    int k=1;
    modpoly xpi=xtop ;
    for (int i=2;;i++){
      int qremdeg=qrem.size()-1;
      if (!qremdeg)
	return true;
      if (qremdeg<2*i){
	v.push_back( facteur<modpoly>(qrem,qremdeg) );
	return true;
      }
      // compute x^(pn^i) mod qrem then gcd(x^pn^i-x,qrem)
      // since X^(pn^i)-X is divisible by 
      // any irreductible of degre dividing i
      // cout << modulo << " " << qmat << endl;
      if (qmat.empty())
	xpi=powmod(xpower1(),pow(env->pn,i),qrem,env);
      else {
	if (!xtoxpowerpn(operator_mod(xpi,qrem,env),qmat,env,0,xpi))
	  return false;
      }
      gcdmodpoly(operator_minus(xpi,xpower1(),env),qrem,env,ddfactor);
      if (is_undef(ddfactor))
	return false;
      k=ddfactor.size()-1;
      if (k)
	v.push_back(facteur<modpoly>(ddfactor,i));
      if (k==qremdeg)
	return true;
      if (k)
	qrem=operator_div(qrem,ddfactor,env);
    }
    return true;
  }

  bool cantor_zassenhaus(const modpoly & ddfactor,int i,const vector<modpoly> & qmat,environment * env,vector<modpoly> & v){
    if (debuglevel)
      cout << "Factoring [" << i << "] " << ddfactor << endl;
    int k=ddfactor.size()-1; 
    if (k==i){
      v.push_back(ddfactor);
      return true;
    }
    if (i==1){ // compute roots
      if (!env || is_strictly_greater(10000,env->pn,context0)){
	vecteur vtmp;
	if (!roots(ddfactor,env,vtmp,v))
	  return false;
	return true;
      }
    }
    // compute qmat modulo ddfactor
    vector<modpoly> thisqmat;
    if (qmat.empty())
      qmatrix(ddfactor,env,thisqmat,0);
    else {
      vector<modpoly>::const_iterator it=qmat.begin(),itend=qmat.end(); 
      for (int j=0;(it!=itend) && (j<k);++it,++j)
	thisqmat.push_back(operator_mod(*it,ddfactor,env));
    }
    int deg=0;
    modpoly fact1;
    for ( int count=2;(deg==0) || (deg==k);count++)  {
      // make random polynomial pp of degree <k
      // double d=k-1;
      // deg=1+(int) (d*rand()/(RAND_MAX+1.0));
      // deg = max(count % (2*i),2);
      deg = 2*i-1;
      modpoly pp(random(deg,env));
      if (debuglevel)
	cout << "Degree:" << deg << ":" << pp << endl ;
      // we have pp^(pn^i-1)=1 mod p,q
      // factoring we get (pp^(pn^i-1)/2-1)(pp^(pn^i+1)/2+1)=0 mod p,q
      // the exponent of the first factor may be rewritten
      // (1+pn+..+pn^(i-1))*(pn-1)/2 and that's the way it is computed
      // pp * pp^pn * ... * pp^(pn^(i-1)) and then to the power (pn-1)/2
      //
      // In characteristic 2, pp^(2^(i*m))+pp=0 mod 2,q
      // hence P(pp)*(P(pp)+1)=0 mod 2,q
      // where P(X)=X^(2^(i*(m-1)))+X^(2^(i*(m-2)))+...+X
      modpoly ppp(pp),temp,tmp;
      if (env->modulo.val==2 ){
	modpoly somme(pp);
	unsigned m=env->pn.val;
	m *= i-1;
	for (unsigned ii=1;ii<m;ii *=2){
	  if (!xtoxpowerpn(ppp,thisqmat,env,k,temp))
	    return false;
	  ppp=temp;
	  operator_times(pp,ppp,env,temp); 
	  DivRem(temp,ddfactor,env,tmp,pp); // pp=(pp*ppp)% ddfactor;
	  somme=operator_plus(somme,pp,env);
	}
	pp=somme;
      }
      else {
	for (int ii=1;ii<i;++ii){
	  if (!xtoxpowerpn(ppp,thisqmat,env,k,temp))
	    return false;
	  ppp=temp;
	  operator_times(pp,ppp,env,temp); 
	  DivRem(temp,ddfactor,env,tmp,pp); // pp=(pp*ppp)% ddfactor;
	}
	pp=powmod(pp,(env->pn-1)/2,ddfactor,env);
	pp=operator_minus(pp,one(),env);
      }
      gcdmodpoly(pp,ddfactor,env,fact1); 
      if (is_undef(fact1))
	return false;
      // hopefully it will split ddfactor
      deg=fact1.size()-1;
    }
    // cout << "cz:" << i << fact1 << ddfactor/fact1 << endl;
    // recursive calls
    if (!cantor_zassenhaus(fact1,i,thisqmat,env,v) || 
	!cantor_zassenhaus(operator_div(ddfactor,fact1,env),i,thisqmat,env,v))
      return false;
    return true;
  }

  bool cantor_zassenhaus(const vector< facteur<modpoly> > & v_in,const vector<modpoly> & qmat, environment * env,vector<modpoly> & v){
    // split elements of v_in
    // cout << v_in << endl;
    vector< facteur<modpoly> >::const_iterator it=v_in.begin(),itend=v_in.end();
    for (;it!=itend;++it){
      if (!cantor_zassenhaus(it->fact,it->mult,qmat,env,v))
	return false;
    }
    return true;
  }

  /*
   * From Z/pZ -> Z
   */

  // lift modular roots (quadratic lift)
  static void liftroots(const dense_POLY1 &q,environment * env,vecteur & v){
    // env->moduloon=false;
    dense_POLY1 qprime=derivative(q);
    // env->moduloon=true;
    gen lcoeff(q.front());
    gen tcoeff(lastnonzero(q));
    gen bound=gen( 2)*abs(lcoeff*tcoeff,context0);
    gen modulo_orig(env->modulo),moduloi(env->modulo),modulonext(env->modulo);
    // cout << modulo << endl;
    while (is_greater(bound,moduloi,context0)) { // (moduloi<=bound){
      modulonext=moduloi*moduloi;
      vecteur::iterator it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	env->modulo=modulonext;
	gen temp1(iquo(horner(q,*it,env),moduloi));
	env->modulo=moduloi;
	gen temp2(horner(qprime,*it,env));
	*it=smod(*it - moduloi* temp1 * invmod(temp2,env->modulo),modulonext) ;
      }
      moduloi=modulonext;
      env->modulo=moduloi;
    }
  }
  
  // number of factors and possible degrees
  int nfact(const vector< facteur<modpoly> > & v, vector<bool> & possible_degrees , int maxdeg){
    int k=v.size(); 
    possible_degrees[0]=true;
    for (int i=1;i<maxdeg;i++)
      possible_degrees[i]=false;
    int i=0;
    for (int j=0;j<k;j++){
      int deg=v[j].mult; // degree of this equal degree factor
      int mult=(v[j].fact.size()-1)/deg; // number of equal degree factors
      if (debuglevel)
	cout << "Distinct degree factor of " << mult << " factors of deg " << deg << endl;
      i = i + mult;
      for (int k=maxdeg-1;k>-1;k--){
	if (possible_degrees[k]){
	  for (int l=mult;l;l--)
	    possible_degrees[k+l*deg]=true;
	}
      }
    }
    return i;
  }

  // set tab to tab && othertab
  void intersect(vector<bool>::iterator tab, vector<bool>::iterator othertab,int size) {
    vector<bool>::iterator end = tab+size;
    for (;tab!=end;++tab,++othertab)
      *tab = (*tab) && (*othertab);
  }

  static void print_possible_degrees(const vector<bool> & possible_degrees,int qdeg){
    for (int tmp=0;tmp<=qdeg;tmp++)
      if (possible_degrees[tmp])
	cout << tmp << " ";
    cout << endl;
  }

  // number of true elements
  int sigma(const vector<bool> & deg){
    int res=0;
    vector<bool>::const_iterator it=deg.begin(),itend=deg.end();
    for (;it!=itend;++it)
      res += *it;
    return res;
  }

  // Landau-Mignotte bound
  gen mignotte_bound(const dense_POLY1 & p){
    int d=p.size()-1;
    gen n( d+1);
    if (d%2)
      n=n+n;
    n=(isqrt(n)+gen( 1));
    n=n*abs(norm(p,context0),context0).re(context0);
    n=n*pow(gen( 2),1+(d/2));
    return n;
  }

  gen mignotte_bound(const polynome & p){
    int d=p.lexsorted_degree();
    gen n( d+1);
    if (d%2)
      n=n+n;
    n=(isqrt(n)+gen( 1));
    n=n*abs(p.norm(),context0).re(context0);
    n=n*pow(gen( 2),1+(d/2));
    return n;
  }

  static bool extracttruefactors(dense_POLY1 & q, environment * env,vector<modpoly> & v_in, vector<modpoly> & v_orig, vectpoly & v_out, int & n,const gen & modulo_orig, const gen & moduloi, gen & bound, vector<modpoly> & u){
    int totaldegreefound=0;
    modpoly quo,rem;
    vector<modpoly>::const_iterator it=v_in.begin(),itend=v_in.end();
    vector<modpoly>::const_iterator orig_it=v_orig.begin();
    vector<modpoly> w_in,w_orig;
    for (;it!=itend;++it,++orig_it){
      // cout << "Trying " << q << "/" << *it << endl;
      if (DenseDivRem(q,*it,quo,rem,true) && (rem.empty())){
	// cout << "Found early factor " << *it << endl;
	q=quo;
	v_out.push_back(unmodularize(*it));
	totaldegreefound += it->size()-1;
	n--;
	if (n==1){
	  v_in.clear();
	  v_out.push_back(unmodularize(quo));
	  q=one();
	  n--;
	  return true;
	}
      }
      else {
	// cout << "No luck for this one!" << endl;
	w_in.push_back(*it);
	w_orig.push_back(*orig_it);
      }
    }
    if (totaldegreefound){
      v_in=w_in;
      v_orig=w_orig;
      env->modulo=modulo_orig;
      if (!egcd(v_orig,env,u))
	return false;
      env->modulo=moduloi;
      bound=iquo(bound,pow(gen(2),totaldegreefound/2));
    }
    return true;
  }

  // lift factorization from Z/pZ to Z/p^kZ for a sufficiently large k
  // using quadratic Hensel lift
  // modulo is modified to modulo^k
  // returns -1 on error
  static int liftq(environment * env,dense_POLY1 & q,gen &bound,vector<modpoly> & v_in,vectpoly & v_out,vector<bool> & possible_degrees){
    if (debuglevel)
      cout << "Big data -> using quadratic Hensel lift." << v_in.size() << endl;
    gen lcoeff(q.front());
    bool notunit=(!is_one(lcoeff));
    bool testfortruefs;
    gen modulo_orig(env->modulo),moduloi(env->modulo),modulonext(env->modulo*env->modulo);
    vector<modpoly> v_orig(v_in),u;
    if (!egcd(v_in,env,u))
      return -1;
    if (debuglevel){
      cout << "Lifting v_in:" << v_in << endl << "u:" << u << endl;
      cout << "Modulo:" << env->modulo << " Bound:" << bound << endl;
    }
    int n=v_in.size();
    int nfact_to_find = n;
    vector<bool> truefactor(n),hasbeentested(n);
    for (int i=0;i<n;i++){
      truefactor[i]=false;
      hasbeentested[i]=false;
    }
    modpoly newq(q);
    modpoly Q;
    env->modulo = modulonext;
    modpoly pi; // initialize pi with the product of v_in
    vector<modpoly>::iterator it=v_in.begin(),itend=v_in.end();
    mulmodpoly(it,itend,env,pi);
    for (int count=0;;count++){
      // update Q at modulonext, modulonext=moduloi*modulo_orig;
      env->modulo=modulonext;
      env->pn=env->modulo;
      env->moduloon=false;
      Q=q-lcoeff*pi;
      divmodpoly(Q,moduloi,Q);
      Q=modularize(Q,modulo_orig,env);
      if (is_undef(Q))
	return -1;
      env->moduloon=true;
      // _VECTute Q such that q=lcoeff*(pi+p^k*Q)
      // modulo=modulonext; // work in Z/p^(2), done by modularize below
      // cout << "Q:" << Q << endl;
      // _VECTute new v_in
      if (Q.empty())
	env->modulo=modulonext;
      else {
	if (notunit)
	  mulmodpoly(Q,invmod(lcoeff,modulo_orig),env,Q); // Q=Q*invmod(lcoeff);
	vector<modpoly>::iterator itv=v_in.begin(),itvend=v_in.end();
	vector<modpoly>::iterator itu=u.begin();
	vector<modpoly>::iterator itv0=v_orig.begin();
	for (int i=0;itv!=itvend;++itv,++itu,++itv0,++i){
	  if (!truefactor[i]){
	    env->modulo=modulo_orig;
	    modpoly vadd,tmp1,tmp2,truefact;
	    mulmodpoly(Q,*itu,env,tmp1);
	    DivRem(tmp1,*itv0,env,tmp2,vadd); 
	    // modpoly vadd((Q*(*itu)) % (*itv0));
	    env->modulo=modulonext; // back in Z/p^2k
	    if (vadd.empty()){
	      testfortruefs=true;
	      mulmodpoly(*itv,lcoeff,env,truefact);
	      ppz(truefact);
	    }
	    else {
	      hasbeentested[i]=false;
	      env->moduloon =false ;
	      mulmodpoly(vadd,moduloi,vadd);
	      env->moduloon = true ;
	      addmodpoly(*itv,vadd,env,*itv); // (*itv)=(*itv)+moduloi*vadd;
	      testfortruefs = false;
	    }
	    int debut_time=clock();
	    if (debuglevel)
	      cout << debut_time << "Searching true factor" << endl;
	    // test if *itv divides q in Z
	    if ( testfortruefs && !hasbeentested[i] && DenseDivRem(newq,truefact,tmp1,tmp2,true) && (tmp2.empty()) ){
	      int fin_time=clock();
	      if (debuglevel)
		cout << fin_time << "Found true factor " << *itv << endl << "New bound:"<< bound << endl;
	      // yes! _VECTute new q and bound
	      truefactor[i]=true;
	      nfact_to_find--;
	      v_out.push_back(unmodularize(truefact));
	      if (nfact_to_find==1){
		v_out.push_back(unmodularize(tmp1));
		q=one();
		return 0;
	      }
	      newq=tmp1;
	      bound=mignotte_bound(newq);
	      int truefactdeg=truefact.size()-1;
	      for (int j=0;j<signed(newq.size());j++)
		possible_degrees[j]=possible_degrees[j+truefactdeg];
	      if (debuglevel){
		cout << "New degree set ";
		print_possible_degrees(possible_degrees,newq.size()-1);
	      }
	    }
	    else {
	      int fin_time=clock();
	      if (debuglevel)
		cout << fin_time << "No luck!" << endl;
	      if (testfortruefs)
		hasbeentested[i]=true;
	    }
	  }
	}
      }
      if (debuglevel)
	cout << "Lifted to " << modulonext << endl;
      if ( is_strictly_greater(modulonext,bound,context0) ) // (modulonext>bound) )
	break;
      v_orig=v_in;
      if (is_strictly_greater(modulonext*moduloi,bound,context0)){ // modulonext*moduloi>bound)
	// need a last *linear* lift, modulo_orig and u unchanged,
	moduloi = modulonext ;
	modulonext=moduloi*modulo_orig;
	// update pi
	env->modulo = modulonext ;
	it=v_in.begin(); itend=v_in.end();
	mulmodpoly(it,itend,env,pi);      
      }
      else { 
	// compute new u at modulonext and new pi at modulonext^2
	// pidown[l]=Pi_{j>n-2-l} v_in[j], piup[l]=Pi_{j<l+1} v_in[j]
	vector<modpoly> piup,pidown;
	env->modulo=modulonext*modulonext; // modulonext^2 for pi at next iteration
	pidown.push_back(v_in[n-1]);
	modpoly tmp(v_in[n-1]); // initialization needed if n=2
	for (int k=1;k<=n-2;k++){
	  mulmodpoly(pidown[k-1],v_in[n-k-1],env,tmp);
	  pidown.push_back(tmp);
	}
	mulmodpoly(tmp,v_in[0],env,pi);
	env->modulo = modulonext ;
	piup.push_back(v_in[0]);
	for (int k=1;k<=n-2;k++){
	  mulmodpoly(piup[k-1],v_in[k],env,tmp);
	  piup.push_back(tmp);
	}
	// update u and v
	u[0] = operator_minus( operator_times(plus_two,u[0],env) , operator_mod( operator_times( operator_mod(pidown[n-2] , v_in[0],env),operator_times(u[0], u[0],env),env)  , v_in[0],env) , env);
	for (int k=1;k<n-1;k++){
	  mulmodpoly( operator_mod( pidown[n-2-k] ,v_in[k],env), operator_mod(piup[k-1] , v_in[k],env),env,tmp);
	  u[k] = operator_minus( operator_times(gen(2),u[k],env) , operator_mod(operator_times( tmp,operator_times(u[k] , u[k] ,env),env) , v_in[k] ,env) , env);
	}
	u[n-1] = operator_minus( operator_times(gen(2),u[n-1],env) , operator_mod( operator_times ( operator_mod(piup[n-2],v_in[n-1],env), operator_times(u[n-1],u[n-1],env),env ) , v_in[n-1] ,env) , env);
	/*
	  {
	  modpoly scalarprod(u[0]*pidown[n-2]);
	  for (int k=1;k<n-1;k++){
	  scalarprod = scalarprod + u[k]*pidown[n-2-k]*piup[k-1];
	  }
	  scalarprod=scalarprod+u[n-1]*piup[n-2]-one(); // this is 0 mod modulonext
	  cout << "Modulo" << modulo << ", verif v_in.u:" << scalarprod << endl;
	  }
	*/
	moduloi=modulonext;
	modulo_orig = moduloi;
	modulonext=moduloi*modulo_orig;
      } // end update
    } // end for loop
    // move true factors from v_in to v_out
    vector<modpoly> w;
    for (int i=0;i<n;i++){
      if (!truefactor[i])
	w.push_back(v_in[i]);
    }
    swap(v_in,w);
    swap(q,newq);
    for (int i=0;i<n;i++)
      if (!truefactor[i])
	if (!hasbeentested[i]){
	  // cout << "Testing 1 combinations" << endl;
	  return 1;
	}
    // cout << "Skipping 1 combinations" << endl;
    return 2; // all linear factors have still been tested
  }

  // lift factorization from Z/pZ to Z/p^kZ for a sufficiently large k
  // using linear Hensel lift
  // modulo is modified to modulo^k
  bool liftl(environment * env,dense_POLY1 & q,gen &bound,vector<modpoly> & v_in,vectpoly & v_out){
    gen lcoeff(q.front());
    bool notunit=(!is_one(lcoeff));
    gen modulo_orig(env->modulo),moduloi(env->modulo),modulonext(env->modulo);
    vector<modpoly> v_orig(v_in),u;
    if (!egcd(v_orig,env,u))
      return false;
    if (debuglevel){
      cout << "Lifting v_in:" << v_in << endl << "u:" << u << endl;
      cout << "Modulo:" << env->modulo << "Bound" << bound << endl;
    }
    int n=v_in.size();
    int d=q.size()-1;
    // at bound/2^d/2, we will look for factors of q1 in v_in, 
    // if true add factors to v_out and reduce the bound
    // modulo_orig^tryfactors=bound/2^d/2
    int tryfactors=(bound.bindigits()-d/2)/modulo_orig.bindigits();
    for (int count=1;is_greater(bound,moduloi,context0);count++){ // moduloi<=bound
      if ((count==tryfactors/4) || (count==tryfactors)){
	if (!extracttruefactors(q,env,v_in,v_orig,v_out,n,modulo_orig,moduloi,bound,u))
	  return false;
      }
      if (!n)
	return true;
      modulonext=moduloi*modulo_orig;
      env->moduloon=false;
      // product of v_in in Z/p^k Z
      vector<modpoly>::iterator it=v_in.begin(),itend=v_in.end();
      dense_POLY1 pi;
      mulmodpoly(it,itend,env,pi);
      // _VECTute Q such that q=lcoeff*(pi+p^k*Q)
      // modulo=modulonext; // work in Z/p^(k+1), done by modularize below
      modpoly Q((q-lcoeff*pi)/moduloi);
      Q=modularize(Q,modulo_orig,env);
      if (is_undef(Q))
	return false;
      if (notunit)
	mulmodpoly(Q,invmod(lcoeff,env->modulo),env,Q); // Q=Q*invmod(lcoeff);
      // cout << "Q:" << Q << endl;
      // _VECTute new v_in
      if (Q.empty()){
	env->modulo=modulonext;
	moduloi=modulonext;
      }
      else {
	it=v_in.begin();
	vector<modpoly>::const_iterator itu=u.begin(),itorig=v_orig.begin();
	for (;it!=itend;++it,++itu,++itorig){
	  env->modulo=modulo_orig; // work in Z/p or Z/p^k
	  modpoly vadd,tmp1,tmp2;
	  mulmodpoly(Q,*itu,env,tmp1);
	  DivRem(tmp1,*itorig,env,tmp2,vadd); 
	  // modpoly vadd(Q*(*itu) % (*itorig));
	  env->modulo=modulonext; // back in Z/p^(k+1) or Z/p^2k
	  (*it)=operator_plus(*it,operator_times(moduloi,vadd,env),env);
	}
	moduloi=modulonext;
      }
      if (debuglevel)
	cout << "Lifted to " << modulonext << endl;
    }
    return true;
  }

  // given a factorization v_in of q in Z/p^kZ find a factorization v_out 
  // over Z, k is the minimal # of factors of v_in to be combined
  void combine(const dense_POLY1 & q, const vector<modpoly> & v_in,environment * env,vectpoly & v_out,vector<bool> & possible_degrees, int k){
    if (v_in.empty())
      return; // nothing to do
    int n=v_in.size();
    if (debuglevel)
      cout << clock() << "Starting combining with " << n << " factors" << endl;
    gen lcoeff(smod(q.front(),env->modulo));
    bool notunit=(!is_one(lcoeff));
    if (n==1){
      polynome temp(unmodularize(lcoeff*v_in.front()));
      if (notunit)
	ppz(temp);
      v_out.push_back(temp);
      return;
    }
#if 1 // def VISUALC
    vector<modpoly>::const_iterator * it=new vector<modpoly>::const_iterator[n+1],itend=v_in.end(),itbegin=v_in.begin(), current;
#else
    vector<modpoly>::const_iterator it[n+1],itend=v_in.end(),itbegin=v_in.begin(), current;
#endif
    vector<int> degrees(n); // records degrees of v_in polynomials
    for (int j=0;j<n;j++)
      degrees[j]=v_in[j].size()-1;
    gen twoto32(pow(gen(2),32)); // requires int=32 bits
    vector<int> d1tab(n); // records coeff of degree d-1*2^32/modulo
    for (int j=0;j<n;j++)
      d1tab[j]=(iquo(v_in[j][1]*twoto32,env->modulo)).to_int();
    gen dminus1bound((norm(q,0)+1)*gen((int)q.size()-1)*lcoeff);
    int d1=(iquo(dminus1bound*twoto32,env->modulo)).to_int()+1; // maxvalue of d-1 coeff for a product
    // cout << dminus1bound << endl;
    for (;k<n;k++){
      if (debuglevel)
	cout << clock() << "Testing combination of " << k << " factors" << endl;
      // initialize all iterators, picstcoeff[] and totaldeg[]
      it[1]=v_in.begin();
      // product of cst coeff
      gen lastpi(1);
      vecteur picstcoeff;
      picstcoeff.push_back(1); 
      // sum of degrees
      int lastdeg=0;
      index_t totaldeg;
      totaldeg.push_back(0);
      // the coeff of degree d-1
      gen lastdminus1(0);
      vecteur dminus1;
      dminus1.push_back(0);
      for (int j=1;j<k;j++){
	it[j+1]=it[j]+1;
	lastpi = smod(lastpi*cstcoeff(*it[j]),env->modulo);
	picstcoeff.push_back(lastpi);
	lastdeg += it[j]->size()-1;
	totaldeg.push_back(lastdeg);
	lastdminus1 = lastdminus1 + (*it[j])[1];
	dminus1.push_back(lastdminus1);
      }
      // look if the next possible degree is > degree(q)/2 -> q is irreducible
      int tmp=lastdeg+it[k]->size()-1;
      for (;2*tmp<=signed(q.size());++tmp)
	if (possible_degrees[tmp])
	  break;
      if (2*tmp>signed(q.size())-1){
	v_out.push_back(unmodularize(q));
#if 1 // def VISUALC
	delete [] it;
#endif
	return ;
      }
      current=it[k];
      int _VECTteur=0;
      int * position=&degrees[current-itbegin];
      int lastd1=(iquo(lastdminus1*twoto32,env->modulo)).to_int();
      int * d1tabposition=&d1tab[current-itbegin];
      for (;;){
	// combination of k factors
	// first test that the degree is admissible, 
	// then do the d-1 test: coeff multiplied by lcoeff mod modulo < bound
	// and that the product of cst_coeff divides the polynomial
	if ( possible_degrees[lastdeg+(*position)] &&
	     // ( abs(smod((lastdminus1+(*current)[1])*lcoeff,env->modulo)) < dminus1bound ) &&
	     ( absint(lastd1+(*d1tabposition))< d1 ) ){
	  if (
	      is_zero(cstcoeff(q)%smod(lastpi*cstcoeff(*current),env->modulo))
	      ){
	    it[k]=current;
	    // modpoly pi(*it[1]);
	    // for (int j=2;j<=k;j++)
	    //  pi=pi*(*it[j]);
	    modpoly pi;
	    mulmodpoly(it,1,k,env,pi);
	    if (notunit)
	      mulmodpoly(pi,lcoeff,env,pi);
	    dense_POLY1 quo(1),rem(1);
	    // cout << pi << " " << combination << endl;
	    if (notunit)
	      ppz(pi);
	    if ( // (gen(2)*abs(pi[2])<dminus1bound*gen((int)q.size()-2)) &&
		DenseDivRem(q,pi,quo,rem,true) && 
		(rem.empty())
		 ){
	      // push factor found
	      polynome found(unmodularize(pi));
	      if (debuglevel){
		cout << "Found:" ;
		dbgprint(found);
	      }
	      v_out.push_back(found);
	      // remove factors found from the list 
	      vector<modpoly> v_new;
	      vector<modpoly>::const_iterator itnew=v_in.begin();
	      for (int j=1;j<=k;j++){
		for (;itnew!=it[j];++itnew)
		  v_new.push_back(*itnew);
		++itnew;
	      }
	      for (;itnew!=itend;++itnew)
		v_new.push_back(*itnew);
	      // if v_new.size is large it might be more efficient
	      // to _VECTute the factorization of newq
	      // else call combine with q <- quo
	      if (v_new.size()>24){
		int j=3;
		factorunivsqff(unmodularize(quo),env,v_out,j,debuglevel,v_new.size());
	      }
	      else {
		// re_VECTute possible_degrees
		int founddeg=found.lexsorted_degree();
		int newqdeg=q.size()-1-founddeg;
		vector<bool> new_possible_degrees(newqdeg+1);
		for (int j=0;j<=newqdeg;j++)
		  new_possible_degrees[j]=possible_degrees[j+founddeg];
		if (debuglevel)
		  print_possible_degrees(new_possible_degrees,newqdeg);
		combine(quo,v_new,env,v_out,new_possible_degrees,k);
	      }
#if 1 // def VISUALC
	      delete [] it;
#endif
	      return;
	    }
	  }
	}
	++position;
	++_VECTteur;
	++current;
	++d1tabposition;
	if (current==itend){ // increment the predecessor until not = max
	  int j=k-1;
	  for (;j>0;j--){
	    ++it[j];
	    if (it[j]!=itend+j-k)
	      break;
	  }
	  if (debuglevel && (j<k/2))
	    cout << clock() << " " << _VECTteur << " tries." << endl;
	  // if j!=0, recalculate successors, picstcoeff and totaldeg
	  if (j){
	    lastpi=picstcoeff[j-1];
	    lastdeg=totaldeg[j-1];
	    lastdminus1=dminus1[j-1];
	    for (;j<k;j++){
	      lastpi = smod(lastpi*cstcoeff(*it[j]),env->modulo);
	      picstcoeff[j]=lastpi;
	      lastdeg += it[j]->size()-1;
	      totaldeg[j]=lastdeg;
	      lastdminus1 = lastdminus1 + (*it[j])[1];
	      dminus1[j]=lastdminus1;
	      it[j+1]=it[j]+1;
	    }
	    current=it[k];
	    position=&degrees[current-itbegin];
	    lastd1=(iquo(lastdminus1*twoto32,env->modulo)).to_int();
	    d1tabposition=&d1tab[current-itbegin];
	    if (2*(lastdeg+current->size()-1)>q.size()-1)
	      break;
	  }
	  else
	    break;
	}
      }
    }
    // k==n
    v_out.push_back(unmodularize(q));
#if 1 // def VISUALC
    delete [] it;
#endif
  }

  const char idivis23[][6]={
    {0,0,0,0,0,0},
    {1,0,0,0,0,0},
    {1,2,0,0,0,0},
    {1,3,0,0,0,0},
    {1,2,4,0,0,0},
    {1,5,0,0,0,0},
    {1,2,3,6,0,0},
    {1,7,0,0,0,0},
    {1,2,4,8,0,0},
    {1,3,9,0,0,0},
    {1,2,5,10,0,0},
    {1,11,0,0,0,0},
    {1,2,3,4,6,12},
    {1,13,0,0,0,0},
    {1,2,7,14,0,0},
    {1,3,5,15,0,0},
    {1,2,4,8,16,0},
    {1,17,0,0,0,0},
    {1,2,3,6,9,18},
    {1,19,0,0,0,0},
    {1,2,4,5,10,20},
    {1,3,7,21,0,0},
    {1,2,11,22,0,0},
    {1,23,0,0,0,0},
  };

  // Find linear factors of q
  int do_linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,vecteur & croots,int & i){
    int pn;
    if (normalize_env(env))
      pn=env->pn.val;
    else
      pn=-1;
    if (q.coord.empty()){
      qrem=q;
      return 4;
    }
    if (q.dim!=1) 
      return 0; // setsizeerr(gettext("modfactor.cc/linearfind"));
    int qdeg=q.lexsorted_degree();
    if (!qdeg)
      return 4;
    if (qdeg==1){
      v.push_back(q);
      if (q.coord.size()==1)
	croots.push_back(0);
      else
	croots.push_back(-q.coord.back().value/q.coord.front().value);
      qrem=polynome(gen( 1),1);
      return 4;
    }
    if (!env->complexe && q.coord.size()>1 && q.coord.front().value.type==_INT_ && q.coord.back().value.type==_INT_){
      int qn=absint(q.coord.front().value.val),q0=absint(q.coord.back().value.val);
      if (qn<24 && q0<24){
	// check all rationals divisors of q0/divisor of qn
	env->moduloon=false;
	polynome Qtest(1),Qquo,Qrem;
	Qtest.coord.reserve(2);
	Qtest.coord.push_back(monomial<gen>(1,1,1));
	if (q.coord.back().index[0]>0){
	  croots.push_back(0);
	  v.push_back(Qtest);	  
	  qrem=q.shift(index_t(1,-1));
	}
	else
	  qrem=q;
	Qtest.coord.push_back(monomial<gen>(1,0,1));
	const char * qndiv=idivis23[qn];
	const char * q0div=idivis23[q0];
	for (int i=0;i<6;i++){
	  int num=q0div[i];
	  if (!num)
	    break;
	  for (int j=0;j<6;j++){
	    int den=qndiv[j];
	    if (!den)
	      break;
	    Qtest.coord.front().value=den;
	    if (gcd(num,den)==1){
	      Qtest.coord.back().value=-num;	      
	      if (qrem.TDivRem(Qtest,Qquo,Qrem,false) && Qrem.coord.empty()){
		croots.push_back(gen(num)/gen(den));
		v.push_back(Qtest);
		swap(qrem.coord,Qquo.coord);
	      }
	      Qtest.coord.back().value=num;	      
	      if (qrem.TDivRem(Qtest,Qquo,Qrem,false) && Qrem.coord.empty()){
		croots.push_back(gen(-num)/gen(den));
		v.push_back(Qtest);
		swap(qrem.coord,Qquo.coord);
	      }
	      if (qrem.lexsorted_degree()==0)
		return 4;
	      if (qrem.lexsorted_degree()<=1){
		v.push_back(qrem);
		if (qrem.coord.size()==1)
		  croots.push_back(0);
		else
		  croots.push_back(-qrem.coord.back().value/qrem.coord.front().value);
		qrem.coord.clear();
		return 4;
	      }
	    }
	  }
	}
	return 4;
      } // end qn<20 and q0<20
    }
    modpoly Q;
    // find a prime such that q remains sqff
    gen m;
    gen lcoeff=q.coord.front().value; // REMOVED .re(0)
    bool notunit=(!is_one(lcoeff));
    for (;i<100;i++){
      if (env->complexe && primes[i]%4!=3) // insure that -1 is not a square mod primes[i]
	continue;
      m=gen(primes[i]);
      if (!is_zero(smod(lcoeff,m))){ // insure that degree remains constant
	Q=modularize(q,m,env);
	if (is_undef(Q))
	  return 0;
	mulmodpoly(Q,invmod(lcoeff,env->modulo),env,Q); // Q=Q*invmod(lcoeff) Q is now unitary
	// cout << "Trying with " << m << Q << endl;
	if (is_one(gcd(Q,derivative(Q,env),env)))
	  break;
      }
    }
    if (i==100) return 0; // setsizeerr(gettext("modfactor.cc/linearfind")); 
    vecteur w,xpuipn(xpowerpn(env));
    vector<modpoly> wtmp;
    if (is_undef(xpuipn)) return 0;
    if (!roots((pn>0 && signed(Q.size())>pn)?gcd(Q,operator_minus(xpuipn,xpower1(),env),env):Q,env,w,wtmp))
      return 0;
    dense_POLY1 q1(modularize(q,gen(0),env));
    if (is_undef(q1)) return 0;
    liftroots(q1,env,w);
    // try each element of w
    vecteur::const_iterator it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      dense_POLY1 combination,quo,rem;
      if (notunit){
	// cout << *it << " " << lcoeff << " " << env->modulo << endl;
	gen s( smod((*it)*lcoeff,env->modulo) );
	gen g(gcd(s,lcoeff));
	combination.push_back(iquo(lcoeff,g));
	combination.push_back(iquo(-s,g));
	// cout << combination << endl;
      }
      else {
	combination.push_back(gen( 1));
	combination.push_back(smod(-(*it),env->modulo));
      }
      if (DenseDivRem(q1,combination,quo,rem,true) && (rem.empty())){
	// push factor found
	v.push_back(unmodularize(combination));
	croots.push_back(-combination.back()/combination.front());
	q1=quo;
      }
    }
    qrem=unmodularize(q1);
    env->moduloon=false;
    return 4;
  }

  static bool do_factorunivsqff(const polynome & q,environment * env,vectpoly & v,int & i,int debug,int modfactor_primes){
    debuglevel=debug;  // 1 or debug;
    if (q.coord.empty())
      return true;
    if (q.dim!=1) return false; // setsizeerr(gettext("modfactor.cc/factorunivsqff"));
    int qdeg=q.lexsorted_degree();
    if (!qdeg)
      return true;
    if (qdeg==1){
      v.push_back(q);
      return true;
    }
    // find a "best" prime such that Q is sqff and has not too much factors
    gen m;
    gen lcoeff=q.coord.front().value.re(context0);
    gen bound=mignotte_bound(q)*abs(lcoeff,context0) ;
    double logbound_d=bound.bindigits()*giac_log((double) 2);
    if (debuglevel>=2)
      cout << "Bound " << bound << " log:" << logbound_d << endl;
    modpoly Qtry(1);
    int bestprime,bestnumberoffactors;
    gen bestliftsteps;
    vector< facteur<modpoly> > wf;
    vector<modpoly> bestqmat;
    vector<bool> possible_degrees(qdeg+1);
    for (int essai=0;essai<modfactor_primes;i++){
      // for (int essai=0;essai<50;i++){
      if (i==100){
	if (bestqmat.empty()) return false; // setsizeerr(gettext("modfactor.cc/factorunivsqff"));
	break;
      }
      int currentprime=primes[i];
      m=gen(primes[i]);
      if (!is_zero(smod(lcoeff,m))){ // insure that degree remains constant
	Qtry=modularize(q,m,env);
	if (is_undef(Qtry))
	  return false;
	Qtry=operator_times(invmod(lcoeff,env->modulo),Qtry,env); // -> Q is now unitary
	// cout << "Trying with " << m << Q << endl;
	if (is_one(gcd(Qtry,derivative(Qtry,env),env))){
	  vector< facteur<modpoly> > wftry;
	  // distinct degree factorization of Qtry mod env->modulo
	  vector<modpoly> qmat;
	  qmatrix(Qtry,env,qmat,0);
	  if (!ddf(Qtry,qmat,env,wftry))
	    return false;
	  if (debuglevel)
	    cout << "Trying prime " << env->modulo << endl;
	  vector<bool> new_degrees(qdeg+1);
	  int numberoffactors;
	  if (essai){
	    numberoffactors=nfact(wftry,new_degrees,qdeg+1);
	    intersect(possible_degrees.begin(),new_degrees.begin(),qdeg+1);
	  }
	  else
	    numberoffactors=nfact(wftry,possible_degrees,qdeg+1);
	  if (debuglevel){
	    cout << "Possible degrees after intersection:";
	    print_possible_degrees(possible_degrees,qdeg);
	  }
	  if ( (numberoffactors==1) || (sigma(possible_degrees)<3) ){
	    v.push_back(q);
	    env->moduloon=false;
	    return true;
	  }
	  int ilifta=int(giac_ceil(giac_log(logbound_d/giac_log((double) primes[i]))/giac_log(2.0)));
	  int iliftb=giacmax(1,int(giac_ceil(giac_log(2./3.*logbound_d/giac_log((double) primes[i]))/giac_log(2.))));
	  if (debuglevel)
	    cout << "Would use min " << ilifta << "," << iliftb << " steps modulo " << currentprime << endl; 
	  gen qlifta(pow(currentprime,(long unsigned int) pow(gen(2),ilifta).to_int()));
	  gen qliftb(pow(currentprime,(long unsigned int) 3*pow(gen(2),iliftb-1).to_int()));
	  if (debuglevel>=2)
	    cout << "Would use min " << qlifta << "," << endl << qliftb << " modulo " << currentprime << endl; 
	  gen liftsteps;
	  if (is_strictly_greater(qliftb,qlifta,context0)) // (qlifta<qliftb)
	    liftsteps=qlifta;
	  else
	    liftsteps=qliftb;
	  // _VECTare with previous factorization:
	  // keep the factorization with less factors
	  // if equal number try to minimize the # of lifting steps
	  if ( (!essai) || is_strictly_greater(bestnumberoffactors,numberoffactors,context0) || ( (numberoffactors==bestnumberoffactors) && is_strictly_greater(bestliftsteps,liftsteps,context0) ) ){ // (numberoffactors<bestnumberoffactors) && (liftsteps<bestliftsteps)
	    bestprime=currentprime;
	    bestliftsteps = liftsteps;
	    bestnumberoffactors=numberoffactors;
	    bestqmat=qmat;
	    wf=wftry;
	  }
	  essai++;
	}
	else // non sqff
	  if (debuglevel)
	    cout << "Not square-free modulo " << currentprime <<endl;
      } 
      else // degree non cst
	if (debuglevel)
	  cout << "Non constant degree modulo " << currentprime << endl;
    }
    env->modulo=gen(bestprime);
    if (debuglevel){
      cout << "Using prime " << env->modulo << endl;
      if (debuglevel>=2){
	cout << " for " ;
	dbgprint(q);
      }
    }
    vector<modpoly> w;
    if (!cantor_zassenhaus(wf,bestqmat,env,w))
      return false;
    // cout << "Starting lift" << endl;
    // lift and combine
    dense_POLY1 q1(modularize(q,gen(0),env));
    if (is_undef(q1))
      return false;
#ifdef HAVE_LIBPARI
    // use PARI LLL+knapsack recombination
    if (w.size()>12){
      vector<dense_POLY1> res;
      if (pari_lift_combine(q1,w,env->modulo,res)){
	vector<dense_POLY1>::const_iterator it=res.begin(),itend=res.end();
	for (;it!=itend;++it)
	  v.push_back(unmodularize(*it));
	env->moduloon=false;
	return true;
      }
    }
#endif // HAVE_LIBPARI
    if (is_strictly_greater(bound,pow(env->modulo,(long unsigned int) HENSEL_QUADRATIC_POWER),context0)) { // bound>pow(...)
      int res=liftq(env,q1,bound,w,v,possible_degrees);
      if (res==-1)
	return false;
      if (res)
	combine(q1,w,env,v,possible_degrees,res);
    }
    else {
      if (!liftl(env,q1,bound,w,v))
	return false;
      combine(q1,w,env,v,possible_degrees);
    }
    env->moduloon=false;
    if (debuglevel)
      cout << clock() << "End combine" << endl;
    return true;
  }

#ifdef HAVE_LIBNTL
  typedef gen inttype; 


  int ntlfactor(inttype *p, int pdeg,inttype ** result,int * resultdeg,int debug=0){
    NTL::ZZX f(tab2ZZX(p,pdeg));
    // cout << "Factoring " << f << endl;
    NTL::vec_pair_ZZX_long factors;
    NTL::ZZ c;
    factor(c, factors, f,debug); // c will be 0 since q has content 1
    // convert factors to arrays, multiplicity is always 1
    // cout << factors << endl;
    int s=factors.length();
    for (int i=0;i<s;i++){
      ZZX2tab(factors[i].a,resultdeg[i],result[i]);
    }
    return s;
  }
  // ithprime will not be used here
  bool factorunivsqff(const polynome & q,environment * env,vectpoly & v,int & i,int debug=0,int modfactor_primes=MODFACTOR_PRIMES){
#ifdef HAVE_LIBPTHREAD
    int locked=pthread_mutex_trylock(&ntl_mutex);
#endif // HAVE_LIBPTHREAD
    if (locked){
      if (!do_factorunivsqff(q,env,v,i,debug,modfactor_primes))
	return false;
    }
    try {
      int n=q.lexsorted_degree();
      inttype tab[n+1]; // dense rep of the polynomial
      inttype * result[n]; // array of dense rep of the polynomial
      int resultdeg[n];
      if (!polynome2tab(q,n,tab))
	return false;
      // cerr << "NTL factor begins" << endl;
      int size=ntlfactor(tab,n,result,resultdeg,debug); 
      // cerr << "NTL factor end" << endl;
      // size is the number of poly in result
      for (long i = 0 ; i < size; i++){
	v.push_back(tab2polynome(result[i],resultdeg[i]));
	delete [] result[i];
      }
    } catch (std::runtime_error & e){
    }
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&ntl_mutex);
#endif
    return true;
  }

  // find linear factor, nothing is done here, all the work is performed above
  int linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,int & i){
#ifdef HAVE_LIBPTHREAD
    int locked=pthread_mutex_trylock(&ntl_mutex);
#endif // HAVE_LIBPTHREAD
    if (locked){
      vecteur cr;
      return do_linearfind(q,env,qrem,v,cr,i);
    }
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&ntl_mutex);
#endif
    qrem=q;
    return 2;
  }

#else // HAVE_LIBNTL

  // find linear factor only 
  int linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,int & i){
    vecteur cr;
    return do_linearfind(q,env,qrem,v,cr,i);
  }

  // this function trashes the current value of env->modulo
  bool factorunivsqff(const polynome & q,environment * env,vectpoly & v,int & i,int debug,int modfactor_primes){
    return do_factorunivsqff(q,env,v,i,debug,modfactor_primes);
  }


#endif // ndef HAVE_LIBNTL

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
