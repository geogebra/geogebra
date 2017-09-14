/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c gausspol.cc -D_I386_ -DHAVE_CONFIG_H -DIN_GIAC" -*- */
#include "giacPCH.h"
/*
 *  This file implements several functions that work on univariate and
 *  multivariate polynomials and rational functions.
 *  These functions include polynomial quotient and remainder, GCD and LCM
 *  computation, factorization and rational function normalization. */

/*
 *  Copyright (C) 2000,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

using namespace std;
#include "gausspol.h"
#include "modpoly.h"
#include "modfactor.h"
#include "solve.h" // for has_num_coeff
#include "alg_ext.h"
#include "sym2poly.h"
#include "prog.h"
#include "plot.h"
#include "modpoly.h"
#include "threaded.h"
#include "usual.h"
#include "ezgcd.h"
#include "giacintl.h"
#include <math.h>
#include <stdexcept>

#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#endif

#ifdef HAVE_GMPXX_H
#define myint mpz_class
#else
#define myint my_mpz
#endif

// #undef HAVE_LIBNTL

#ifdef USTL
namespace ustl {
  inline bool operator > (const giac::index_t & a,const giac::index_t & b){ 
    if (a.size()!=b.size()) 
      return a.size()>b.size();
    return !giac::all_inf_equal(a,b);
  }
  inline bool operator < (const giac::index_t & a,const giac::index_t & b){ 
    if (a.size()!=b.size()) 
      return a.size()<b.size();
    return !giac::all_sup_equal(a,b);
  }
}
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  const int primes[]={2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541};

  // trivial division factorization algorithm
  vector<nfactor> trivial_n_factor(gen &n){
    vector<nfactor> v;
    if (is_zero(n))
      return v;
    for (int i=0;i<sizeof(primes)/sizeof(int);i++){
      gen p(primes[i]);
      if (is_zero(n % p) ){
	int j=1;
	n=iquo(n,p);
	while (is_zero(n % p)){
	  n=iquo(n,p);
	  j++;
	}
	v.push_back(nfactor(p,j));
      }
      if (is_strictly_greater(p*p,n,context0))
	break;
    }
    if (n!=gen(1))
      v.push_back(nfactor(n,1));
    return v;
  }

#if 0
  struct is_strictly_smaller_t {
    is_strictly_smaller_t(){}
    bool operator ()(const gen & a,const gen & b){
      return is_strictly_greater(b,a,context0); // return (a<b)
    }
  };

  vecteur divisor(const gen & n){
    gen ntemp;
    if (!is_positive(n,context0)) // ok
      ntemp=-n;
    else
      ntemp=n;
    vector<nfactor> nv(trivial_n_factor(ntemp));
    int k=nv.size();
    vecteur v;
    v.push_back(gen(1));
    for (int j=0;j<k;j++){
      gen current(1);
      int mult=nv[j].mult;
      gen multiplie(nv[j].fact);
      v.reserve(v.size()*(mult+1));
      vecteur::const_iterator itbeg=v.begin();
      vecteur::const_iterator itend=v.end();
      for (int i=0;i<mult;i++){
	current=current*multiplie;
	vecteur::const_iterator it=itbeg;
	// COUT << "for " << *it << endl;
	for (;it!=itend;++it){
	  gen temp((*it)*current);
	  // COUT << *it << " " << current << " " << temp << endl;
	  v.push_back( temp );
	}
      }
    }
    sort(v.begin(),v.end(),is_strictly_smaller_t());
    return v;
  }
#endif

  vecteur cyclotomic(int n){
    // Algorithm base sur les relations suivantes
    // soit p premier, si p ne divise pas n alors 
    // cyclo_{n}(X^p)=cyclo_{np}(X)*cyclo_{n}(X)
    // si p divise n alors
    // cyclo_{n}(X^p)=cyclo_{np}(X)
    //
    // poser f(x)=x-1
    // parcourir la liste des facteurs premiers de n ordonnee par ordre 
    // croissant et pour chaque facteur premier p effecter f(x)=f(x^p)/f(x).
    // Le polynome cyclotomique P_n est alors f(x^[n/pi]) ou pi est le produit
    // des facteurs premiers.
    // Par exemple
    // n=6 donne la liste { 2 3 }
    // p=2: f(x)=(x^2-1)/(x-1)=x+1
    // p=3: f(x)=(x^3+1)/(x+1)=x^2-x+1
    // n/pi=1 c'est fini
    //
    // n=100 donne { 2 5 }
    // p=2: f(x)=x+1
    // p=5: f(x)=(x^5+1)/(x+1)=x^4-x^3+x^2-x+1
    // n/pi=10 donc f(x)=x^40-x^30+x^20-x^10+1
    gen ncopy(n);
    vector<nfactor> v(trivial_n_factor(ncopy));
    vecteur res;
    res.push_back(1);
    res.push_back(-1); // res=x-1
    int pi=1;
    vector<nfactor>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->fact.type!=_INT_) 
	return vecteur(1,gensizeerr(gettext("gausspol.cc/cyclotomic")));
      int p=it->fact.val;
      pi *= p;
      vecteur res_x_to_xp(x_to_xp(res,p));
      res=res_x_to_xp/res;
    }
    return x_to_xp(res,n/pi);
  }
  
  //**********************************
  // functions relative to polynomials
  //**********************************
  polynome gen2polynome(const gen & e,int dim){
    if (e.type==_POLY)
      return *e._POLYptr;
    return polynome(e,dim);
  }

  // instantiation of dbgprint for poly
  void dbg(const polynome & p){
    p.dbgprint();
  }

  bool is_one(const polynome & p){
    return Tis_one<gen>(p);
  }

  polynome firstcoeff(const polynome & p){
    return Tfirstcoeff<gen>(p);
  }

  void Add_gen ( std::vector< monomial<gen> >::const_iterator & a,
		 std::vector< monomial<gen> >::const_iterator & a_end,
		 std::vector< monomial<gen> >::const_iterator & b,
		 std::vector< monomial<gen> >::const_iterator & b_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_m &, const index_m &)) {
    if ( (a!=a_end && new_coord.begin()==a) || (b!=b_end && new_coord.begin()==b)){
      std::vector< monomial<gen> > tmp;
      Add_gen(a,a_end,b,b_end,tmp,is_strictly_greater);
      std::swap(new_coord,tmp);
      return;
    }
    new_coord.clear();
    new_coord.reserve( (a_end - a) + (b_end - b));
    gen sum;
    for (;;) {
      if (a == a_end) {
	while (b != b_end) {
	  new_coord.push_back(*b);
	  ++b;
	}
	break;
      } 
      const index_m & pow_a = a->index;
      // If b is empty, fill up with elements from a and stop
      if (b == b_end) {
	while (a != a_end) {
	  new_coord.push_back(*a);
	  ++a;
	}
	break;
      } 
      const index_m & pow_b = b->index;
      // a and b are non-empty, compare powers
      if (pow_a!=pow_b){
	if (is_strictly_greater(pow_a, pow_b)) {
	  // a has lesser power, get coefficient from a
	  new_coord.push_back(*a);
	  ++a;
	} 
	else  {
	  // b has lesser power, get coefficient from b
	  new_coord.push_back(*b);
	  ++b;
	} 
      }
      else {
	sum = (*a).value + (*b).value;
	if (!is_zero(sum))
	  new_coord.push_back(monomial<gen>(sum,pow_a));
	++a;
	++b;
      }
    }
  }

  polynome operator + (const polynome & th,const polynome & other) {
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      return monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),th.dim);
    }
    // Tensor addition
    vector< monomial<gen> >::const_iterator a=th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end=th.coord.end();
    if (a == a_end) {
      return other;
    }
    vector< monomial<gen> >::const_iterator b=other.coord.begin();
    vector< monomial<gen> >::const_iterator b_end=other.coord.end();
    if (b==b_end){
      return th;
    }
    polynome res(th.dim,th);
    Add_gen(a,a_end,b,b_end,res.coord,th.is_strictly_greater);
    return res;
  }

  void Sub_gen ( std::vector< monomial<gen> >::const_iterator & a,
		 std::vector< monomial<gen> >::const_iterator & a_end,
		 std::vector< monomial<gen> >::const_iterator & b,
		 std::vector< monomial<gen> >::const_iterator & b_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_m &, const index_m &)) {
    if ( (a!=a_end && new_coord.begin()==a) || (b!=b_end && new_coord.begin()==b)){
      std::vector< monomial<gen> > tmp;
      Sub_gen(a,a_end,b,b_end,tmp,is_strictly_greater);
      std::swap(new_coord,tmp);
      return;
    }
    new_coord.clear();
    new_coord.reserve( (a_end - a) + (b_end - b));
    gen diff;
    for (;;) {
      if (a == a_end) {
	while (b != b_end) {
	  new_coord.push_back(-(*b));
	  ++b;
	}
	break;
      } 
      const index_m & pow_a = a->index;
      // If b is empty, fill up with elements from a and stop
      if (b == b_end) {
	while (a != a_end) {
	  new_coord.push_back(*a);
	  ++a;
	}
	break;
      } 
      const index_m & pow_b = b->index;
      // a and b are non-empty, compare powers
      if (pow_a!=pow_b){
	if (is_strictly_greater(pow_a, pow_b)) {
	  // a has lesser power, get coefficient from a
	  new_coord.push_back(*a);
	  ++a;
	} 
	else  {
	  // b has lesser power, get coefficient from b
	  new_coord.push_back(-(*b));
	  ++b;
	} 
      }
      else {
	diff = (*a).value - (*b).value;
	if (!is_zero(diff))
	  new_coord.push_back(monomial<gen>(diff,pow_a));
	++a;
	++b;
      }
    }
  }

  polynome operator - (const polynome & th,const polynome & other) {  
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      return monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),th.dim);
    }
    // Tensor addition
    vector< monomial<gen> >::const_iterator a=th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end=th.coord.end();
    vector< monomial<gen> >::const_iterator b=other.coord.begin();
    vector< monomial<gen> >::const_iterator b_end=other.coord.end();
    if (b == b_end) {
      return th;
    }
    polynome res(th.dim,th);
    Sub<gen>(a,a_end,b,b_end,res.coord,th.is_strictly_greater);
    return res;
  }

  void mulpoly(const polynome & th,const gen & fact0,polynome & res){
    if (&th!=&res)
      res.coord.clear();
    gen fact=fact0;
    if (fact.type!=_MOD && fact.type!=_USER && !th.coord.empty() && th.coord.front().value.type==_MOD){
      fact = makemod(fact,*(th.coord.front().value._MODptr+1));
    }
    if (!is_zero(fact)){
      vector< monomial<gen> >::const_iterator a = th.coord.begin();
      vector< monomial<gen> >::const_iterator a_end = th.coord.end();
      Mul<gen>(a,a_end,fact,res.coord);
    }
  }

  polynome operator * (const polynome & th, const gen & fact){
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      return monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),th.dim);
    }
    // Tensor constant multiplication
    if (fact.type!=_MOD && fact==gen(1))
      return th;
    polynome res(th.dim,th);
    mulpoly(th,fact,res);
    return res;
  }

#ifdef NSPIRE
  template<class T> nio::ios_base<T> & operator << (nio::ios_base<T> & os,const int_unsigned & i){
    return os << i.g << ":" << i.u ;
  }
#else
  ostream & operator << (ostream & os,const int_unsigned & i){
    return os << i.g << ":" << i.u ;
  }
#endif

  inline bool operator < (const int_unsigned & gu1,const int_unsigned & gu2){
    return gu1.u > gu2.u;
  }

  template <class U>
  static bool convert(const polynome & p,const index_t & deg,std::vector< T_unsigned<int,U> >  & v,int reduce){
    std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    T_unsigned<int,U> gu;
    U u;
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    gen tmp;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*U(*dit)+U(*itit);
      gu.u=u;
      tmp=smod(it->value,reduce);
      if (tmp.type!=_INT_)
	return false;
      gu.g=tmp.val;
      v.push_back(gu);
    }
    return true;
  }

  template<class U>
  static void convert(const std::vector< T_unsigned<int,U> > & v,const index_t & deg,polynome & p){
    typename std::vector< T_unsigned<int,U> >::const_iterator it=v.begin(),itend=v.end();
    index_t::const_reverse_iterator ditbeg=deg.rbegin(),ditend=deg.rend(),dit;
    p.dim=ditend-ditbeg;
    p.coord.clear();
    p.coord.reserve(itend-it);
    U u;
    index_t i(p.dim);
    int k;
    for (;it!=itend;++it){
      u=it->u;
      for (k=p.dim-1,dit=ditbeg;dit!=ditend;++dit,--k){
	i[k]=u % unsigned(*dit);
	u = u/unsigned(*dit);
      }
      p.coord.push_back(monomial<gen>(it->g,i));
    }
  }


  template <class T,class U>
  static void convert(const vector< T_unsigned<T,U> > & source,vector< T_unsigned<gen,U> > & target){
    target.clear();
    typename vector< T_unsigned<T,U> >::const_iterator it=source.begin(),itend=source.end();
    target.reserve(itend-it);
    for (;it!=itend;++it)
      target.push_back(T_unsigned<gen,U>(it->g,it->u));
  }

  static gen ichrem_smod(mpz_t * Az,mpz_t * Bz,mpz_t * iz,mpz_t * tmpz,const gen & i,const gen & j){
    if (i.type==_ZINT)
      mpz_set(*iz,*i._ZINTptr);
    else
      mpz_set_si(*iz,i.val);
    // i-j
    if (j.type==_INT_){
      if (j.val>0)
	mpz_sub_ui(*tmpz,*iz,j.val);
      else
	mpz_add_ui(*tmpz,*iz,-j.val);
    }
    else
      mpz_sub(*tmpz,*iz,*j._ZINTptr);
    // times B +i
    mpz_addmul(*iz,*tmpz,*Bz);
    // mod A
    mpz_mod(*tmpz,*iz,*Az);
    // compare with *tmpz-Az
    mpz_sub(*iz,*tmpz,*Az);
    mpz_neg(*iz,*iz);
    ref_mpz_t *res = new ref_mpz_t(GIAC_MPZ_INIT_SIZE);
    if (mpz_cmp(*iz,*tmpz)>=0) // use *tmpz
      mpz_set(res->z,*tmpz);
    else {
      mpz_set(res->z,*iz);
      mpz_neg(res->z,res->z);
    }
    return res;
  }

  static gen ichrem_smod(mpz_t * Az,mpz_t * Bz,mpz_t * iz,mpz_t * tmpz,longlong i,longlong j){
    if (i==j)
      return i;
    longlong2mpz(i,iz);
    // longlong2mpz(i-j,tmpz); does not work since i-j might overflow
    longlong2mpz(j,tmpz);
    mpz_sub(*tmpz,*iz,*tmpz);
    // i+=B*(i-j)
    mpz_addmul(*iz,*tmpz,*Bz);
    // mod A
    mpz_mod(*tmpz,*iz,*Az);
    // compare with *tmpz-Az
    mpz_sub(*iz,*tmpz,*Az);
    mpz_neg(*iz,*iz);
    ref_mpz_t *res =  new ref_mpz_t(GIAC_MPZ_INIT_SIZE);
    int test=mpz_cmp(*iz,*tmpz);
    if (test>=0) // use *tmpz
      mpz_set(res->z,*tmpz);
    else {
      mpz_set(res->z,*iz);
      mpz_neg(res->z,res->z);
    }
    return res;
  }

#if 0
  // set i to i+((((j-i)* mod addprime)*u) mod addprime)*targetprime, inplace operation
  // (where u*targetprime+unknow_integer*addprime=1)
  static void ichrem_smod_inplace(int addprime,int u,const gen &targetprime,gen & i,const gen & j){
    longlong tmp=longlong(j.val)-((i.type==_ZINT)?modulo(*i._ZINTptr,addprime):i.val);
    tmp=(tmp*u)%addprime;
    if (targetprime.type==_ZINT && i.type==_ZINT){
      if (tmp>0)
	mpz_addmul_ui(*i._ZINTptr,*targetprime._ZINTptr,int(tmp));
      else
	mpz_submul_ui(*i._ZINTptr,*targetprime._ZINTptr,-int(tmp));
    }
    else
      i += int(tmp)*targetprime;
  }

  static gen ichrem_smod(int addprime,int u,const gen &targetprime,longlong i,int j,mpz_t * tmpz){
    longlong tmp=longlong(j)-i%addprime;
    tmp=(tmp*u)%addprime;
    gen I(i);
    I.uncoerce();
    // now return I+tmp*targetprime
    if (targetprime.type==_INT_){
      tmp *= targetprime.val; // no overflow since tmp<2^31 and targetprime.val also
      longlong2mpz(tmp,tmpz);
      mpz_add(*I._ZINTptr,*I._ZINTptr,*tmpz);
    }
    else {
      if (tmp>=0)
	mpz_addmul_ui(*I._ZINTptr,*targetprime._ZINTptr,tmp);
      else
	mpz_submul_ui(*I._ZINTptr,*targetprime._ZINTptr,-tmp);
    }
    return I;
  }
#endif

  // set i to i+(i-j)*B mod A, inplace operation
  void ichrem_smod_inplace(mpz_t * Az,mpz_t * Bz,mpz_t * iz,mpz_t * tmpz,gen & i,const gen & j){
    if (i==j)
      return;
    if (i.type==_ZINT)
      mpz_set(*iz,*i._ZINTptr);
    else
      mpz_set_si(*iz,i.val);
    // i-j
    if (j.type==_INT_){
      if (j.val>0)
	mpz_sub_ui(*tmpz,*iz,j.val);
      else
	mpz_add_ui(*tmpz,*iz,-j.val);
    }
    else
      mpz_sub(*tmpz,*iz,*j._ZINTptr);
    // times B +i
    mpz_addmul(*iz,*tmpz,*Bz);
    // mod A
    mpz_mod(*tmpz,*iz,*Az);
    // compare with *tmpz-Az
    mpz_sub(*iz,*tmpz,*Az);
    mpz_neg(*iz,*iz);
    if (i.type==_ZINT){
      if (mpz_cmp(*iz,*tmpz)>=0) // use *tmpz
	mpz_set(*i._ZINTptr,*tmpz);
      else {
	mpz_set(*i._ZINTptr,*iz);
	mpz_neg(*i._ZINTptr,*i._ZINTptr);
      }
    }
    else {
      ref_mpz_t *res = new ref_mpz_t(GIAC_MPZ_INIT_SIZE);
      if (mpz_cmp(*iz,*tmpz)>=0) // use *tmpz
	mpz_set(res->z,*tmpz);
      else {
	mpz_set(res->z,*iz);
	mpz_neg(res->z,res->z);
      }
      i=res;
    }
  }

#if 0
  // smod(B*(i-j)+i,A);  
  static gen ichrem_smod(const gen & A,const gen & B,const gen & i,const gen & j){
    if (i==j)
      return i;
    if (A.type!=_ZINT || B.type!=_ZINT)
      return smod(B*(i-j)+i,A);  
    mpz_t * Az=A._ZINTptr,*Bz=B._ZINTptr,iz,tmpz;
    ref_mpz_t *res = new ref_mpz_t(GIAC_MPZ_INIT_SIZE);
    mpz_init(tmpz);
    if (i.type==_ZINT)
      mpz_init_set(iz,*i._ZINTptr);
    else
      mpz_init_set_si(iz,i.val);
    // i-j
    if (j.type==_INT_){
      if (j.val>0)
	mpz_sub_ui(tmpz,iz,j.val);
      else
	mpz_add_ui(tmpz,iz,-j.val);
    }
    else
      mpz_sub(tmpz,iz,*j._ZINTptr);
    // times B +i
    mpz_addmul(iz,tmpz,*Bz);
    // mod A
    mpz_mod(tmpz,iz,*Az);
    // compare with tmpz-Az
    mpz_sub(iz,tmpz,*Az);
    mpz_neg(iz,iz);
    if (mpz_cmp(iz,tmpz)>0) // use tmpz
      mpz_set(res->z,tmpz);
    else {
      mpz_set(res->z,iz);
      mpz_neg(res->z,res->z);
    }
    mpz_clear(iz);
    mpz_clear(tmpz);
    return res;
  }

  static gen ichrem_smod(const gen & A,const gen & B,longlong i,longlong j){
    // return smod((i-j)*B+i,A);  
    if (i==j)
      return i;
    return ichrem_smod(A,B,gen(i),gen(j));
  }
#endif

  template<class T,class U>
  static void ichrem(const vector< T_unsigned<T,U> > & add,int addprime,const vector< T_unsigned<longlong,U> > & init,vector< T_unsigned<gen,U> > & target,gen & targetprime){
    gen A,B,d;
    egcd(addprime,targetprime,A,B,d);
#ifndef NO_STDEXCEPT
    if (!is_one(d)) // should not happen
      setsizeerr();
#endif
    // addprime*A+targetprime*B=1
    // find c such that c=it->g mod targetprime and c=jt->g mod addprime
    // it->g + v*targetprime = jt->g + u*addprime
    // it->g - jt->g = u*addprime - v*targetprime
    // v=(jt->g-it->g)*B
    // hence c=it->g+(jt->g-it->g)*B*targetprime mod addprime*targetprime
    // IMPROVE c=it->g+(((jt->g-it->g) mod addprime)*B mod addprime)*targetprime
    // int b=B.type==_ZINT?mpz_get_si(*B._ZINTptr):B.val;
    A=addprime*targetprime;
    B=-targetprime*B;
    mpz_t z1,z2;
    mpz_init(z1);
    mpz_init(z2);
    if (A.type!=_ZINT)
      A.uncoerce();
    if (B.type!=_ZINT)
      B.uncoerce();
    typename vector< T_unsigned<T,U> >::const_iterator jt=add.begin(),jtend=add.end();
    typename vector< T_unsigned<longlong,U> >::const_iterator kt=init.begin(),ktend=init.end();
    typename vector< T_unsigned<gen,U> >::iterator it=target.begin(),itend=target.end();
    if (it==itend){
      target.reserve(ktend-kt);
      for (;kt!=ktend && jt!=jtend;){
	if (kt->u==jt->u){
	  // it->g=smod(B*(it->g-jt->g)+it->g,A);
	  // target.push_back(T_unsigned<gen,U>(ichrem_smod(addprime,b,targetprime,kt->g,jt->g,&z1),jt->u));
	  target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,kt->g,jt->g),jt->u));
	  ++kt; ++jt;
	}
	else {
	  if (kt->u>jt->u){
	    target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,kt->g,0),kt->u));
	    ++kt;
	  }
	  else {
	    target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,0,jt->g),jt->u));
	    ++jt;
	  }
	}
      }
      for (;jt!=jtend;++jt)
	target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,0,jt->g),jt->u));
      for (;kt!=ktend;++jt)
	target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,kt->g,0),kt->u));
    }
    else {
      for (;it!=itend && jt!=jtend;){
	if (it->u==jt->u){
	  // it->g=smod(B*(it->g-jt->g)+it->g,A);
	  ichrem_smod_inplace(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,jt->g);
	  // ichrem_smod_inplace(addprime,b,targetprime,it->g,jt->g);
	  // it->g=ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,jt->g);
	  ++it; ++jt;
	}
	else {
	  if (it->u>jt->u){
	    ichrem_smod_inplace(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,0);
	    // ichrem_smod_inplace(addprime,b,targetprime,it->g,0);
	    // it->g=ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,0);
	    ++it;
	  }
	  else {
	    vector< T_unsigned<gen,U> > copie(it,itend);
	    target.erase(it,itend);
	    it=copie.begin(); itend=copie.end();
	    for (;it!=itend;){
	      if (jt==jtend || it->u>jt->u){
		it->g=ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,0);
		target.push_back(*it);
		++it;
	      }
	      else {
		++jt;
		if (it->u==jt->u){
		  target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,jt->g),it->u));
		  ++it; 
		}
		else 
		  target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,0,jt->g),jt->u));
	      }
	    }
	    break;
	  }
	}
      }
      for (;jt!=jtend;++jt)
	target.push_back(T_unsigned<gen,U>(ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,0,jt->g),jt->u));
      for (;it!=itend;++it){
	// it->g=ichrem_smod(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,0);
	ichrem_smod_inplace(A._ZINTptr,B._ZINTptr,&z1,&z2,it->g,0);
	// ichrem_smod_inplace(addprime,b,targetprime,it->g,0);
      }
    } // end target empty at beginning
    targetprime = addprime*targetprime;
    mpz_clear(z1);
    mpz_clear(z2);
  }

#ifdef INT128
  template<class U>
  static void smod(vector< T_unsigned<int128_t,U> > & target,int prime){
    typename vector< T_unsigned<int128_t,U> >::iterator it=target.begin(),itend=target.end();
    for (;it!=itend;++it){
      it->g %= prime;
    }
  }
#endif

  template<class U>
  static void smod(const vector< T_unsigned<longlong,U> > & source,vector< T_unsigned<longlong,U> > & target,int prime){
    if (&target==&source){
      typename vector< T_unsigned<longlong,U> >::iterator it=target.begin(),itend=target.end();
      for (;it!=itend;++it){
        it->g %= prime;
        if (!it->g){
          vector< T_unsigned<longlong,U> > copie(target);

#ifndef BESTA_OS
          smod(copie,target,prime);
#else

          // &copie != &target (by definition) so the following is
          // substituted from below as the Kiel ARM compiler does
          // not support this sort of recursive template expansion.

	  copie.clear();
	  typename vector< T_unsigned<longlong,U> >::const_iterator it=source.begin(),itend=source.end();
          copie.reserve(itend-it);
          longlong res;
          for (;it!=itend;++it){
            res=it->g % prime;
            if (res)
               copie.push_back(T_unsigned<longlong,U>(res,it->u));
          }
	  target=copie;
#endif

          break;
        }
      }
      return;
    }
    target.clear();
    typename vector< T_unsigned<longlong,U> >::const_iterator it=source.begin(),itend=source.end();
    target.reserve(itend-it);
    longlong res;
    for (;it!=itend;++it){
      res=it->g % prime;
      if (res)
	target.push_back(T_unsigned<longlong,U>(res,it->u));
    }
  }

#ifdef INT128
  template <class U>
  static void convert_int128(const vector< T_unsigned<longlong,U> > & p1d,vector< T_unsigned<int128_t,U> > & p1D){
    typename vector< T_unsigned<longlong,U> >::const_iterator it=p1d.begin(),itend=p1d.end();
    p1D.clear();
    p1D.reserve(itend-it);
    for (;it!=itend;++it)
      p1D.push_back(T_unsigned<int128_t,U>(it->g,it->u));
  }
#endif

  void addsamepower_gen(std::vector< monomial<gen> >::const_iterator & it,
			std::vector< monomial<gen> >::const_iterator & itend,
			std::vector< monomial<gen> > & new_coord){
    gen res;
    while (it!=itend){
      res=(*it).value;
      index_m pow=(*it).index;
      ++it;
      while ( (it!=itend) && ((*it).index==pow)){
	res=res+(*it).value;
	++it;
      }
      if (!is_zero(res))
	new_coord.push_back(monomial<gen>(res, pow));
    }
  }

  void Mul_gen ( std::vector< monomial<gen> >::const_iterator & ita,
		 std::vector< monomial<gen> >::const_iterator & ita_end,
		 std::vector< monomial<gen> >::const_iterator & itb,
		 std::vector< monomial<gen> >::const_iterator & itb_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_m &, const index_m &),
		 const std::pointer_to_binary_function < const monomial<gen> &, const monomial<gen> &, bool> m_is_strictly_greater
	     ) {
    if (ita==ita_end || itb==itb_end){
      new_coord.clear();
      return;
    }
    int asize=int(ita_end-ita),bsize=int(itb_end-itb);
    int d=int(ita->index.size());
    std::vector< monomial<gen> > multcoord;
    multcoord.reserve(asize*bsize); // correct for sparse polynomial
    std::vector< monomial<gen> >::const_iterator ita_begin = ita,itb_begin=itb ;
    index_m old_pow=(*ita).index+(*itb).index;
    gen res( 0);
    for ( ; ita!=ita_end; ++ita ){
      std::vector< monomial<gen> >::const_iterator ita_cur=ita;
      std::vector< monomial<gen> >::const_iterator itb_cur=itb;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	index_m cur_pow=(*ita_cur).index+(*itb_cur).index;
	if (cur_pow!=old_pow){
	  if (!is_zero(res))
	    multcoord.push_back( monomial<gen>(res ,old_pow ));
	  res=((*ita_cur).value) * ((*itb_cur).value);
	  old_pow=cur_pow;
	}
	else
	  res=res+((*ita_cur).value) * ((*itb_cur).value);      
	if (ita_cur==ita_begin)
	  break;
      }
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      std::vector< monomial<gen> >::const_iterator ita_cur=ita;
      std::vector< monomial<gen> >::const_iterator itb_cur=itb;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	index_m cur_pow=(*ita_cur).index+(*itb_cur).index;
	if (cur_pow!=old_pow){
	  if (!is_zero(res))
	    multcoord.push_back( monomial<gen>(res ,old_pow ));
	  res=((*ita_cur).value) * ((*itb_cur).value);
	  old_pow=cur_pow;
	}
	else
	  res=res+((*ita_cur).value) * ((*itb_cur).value);
    
	if (ita_cur==ita_begin)
	  break;
      }
    }
    // push last monomial
    if (!is_zero(res))
      multcoord.push_back( monomial<gen>(res ,old_pow ));
    // sort by asc. power
#if 1 // def NSPIRE
    sort( multcoord.begin(),multcoord.end(),sort_helper<gen>(m_is_strictly_greater));
#else
    sort( multcoord.begin(),multcoord.end(),m_is_strictly_greater);
#endif
    std::vector< monomial<gen> >::const_iterator it=multcoord.begin();
    std::vector< monomial<gen> >::const_iterator itend=multcoord.end();
    // adjust result size 
    // statistics about polynomial density
    // a dense poly of deg. aa and d variables has binomial(aa+d,d) monomials
    // we need to reserve at most asize*bsize
    // but less for dense polynomials since 
    // binomial(aa+d,d)*binomial(bb+d,d) > binomial(aa+bb+d,d)
    int aa=total_degree(ita_begin->index),bb=total_degree(itb_begin->index);
    double r;
    double factoriald=std::log(evalf_double(factorial(d+1),1,context0)._DOUBLE_val);
    // double factorialaa=std::lgamma(aa+1),factorialbb=std::lgamma(bb+1);
    // double factorialaad=std::lgamma(aa+d+1),factorialbbd=std::lgamma(bb+d+1);
    double factorialaabbd=std::log(evalf_double(factorial(aa+bb+d+1),1,context0)._DOUBLE_val),
      factorialaabb=std::log(evalf_double(factorial(aa+bb+1),1,context0)._DOUBLE_val);
    r=std::exp(factorialaabbd-(factorialaabb+factoriald));
    if (debug_infolevel>1)
      CERR << "// " << CLOCK() << " Mul degree " << aa << "+" << bb << " size " << asize << "*" << bsize << "=" << asize*bsize << " max " << r << endl;
    new_coord.clear();
    if (my_isinf(r) || my_isnan(r) || r>1e9)
      new_coord.reserve(itend-it);
    else
      new_coord.reserve(giacmin(int(r),int(itend-it)));
    // add terms with same power
    addsamepower_gen(it,itend,new_coord);
    if (debug_infolevel>1)
      CERR << "// Actual mul size " << new_coord.size() << endl;
  }

  bool is_integer_poly(const polynome & p,bool intonly){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->value.type==_INT_) continue;
      if (intonly) return false;
      if (it->value.type==_ZINT) continue;
      // if (it->type==_CPLX && is_exactly_zero(*(it->_CPLXptr+1))) continue;
      return false;
      // if (!is_integer(*it)) return false;
    }
    return true;
  }

  bool polynome2poly1(const polynome & p,const index_t & pdeg,const index_t &deg,vecteur & v){
    v.clear();
    int tot=0;
    for (size_t i=0;i<deg.size();++i){
      tot *= deg[i];
      tot += pdeg[i];
    }
    v.resize(tot+1);
    std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int u;
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    gen tmp;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*(*dit)+(*itit);
      if (!is_integer(it->value.type))
	return false;
      v[u]=it->value;
    }
    return true;
  }

  bool poly12polynome(const vecteur & v,const index_t & deg,polynome & p){
    const_iterateur it=v.begin(),itend=v.end();
    index_t::const_reverse_iterator ditbeg=deg.rbegin(),ditend=deg.rend(),dit;
    p.dim=ditend-ditbeg;
    p.coord.clear();
    p.coord.reserve(itend-it);
    int u,U=int(v.size());
    index_t i(p.dim);
    int k;
    for (--itend;itend>=it;--itend){
      gen g=*itend;
      if (is_zero(g))
	continue;
      u=int(itend-it);
      for (k=p.dim-1,dit=ditbeg;dit!=ditend;++dit,--k){
	i[k]=u % unsigned(*dit);
	u = u/unsigned(*dit);
      }
      p.coord.push_back(monomial<gen>(g,i));
    }
    return true;
  }


  // Fast multiplication using hash maps, might also use an int for reduction
  // but there is no garantee that res is smod-ed modulo reduce
  void mulpoly(const polynome & th, const polynome & other,polynome & res,const gen & reduce){
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      res=monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),th.dim); 
      return;
    }
    /*
    if (th.dim==12)
      CERR << "* begin " << CLOCK() << " " << th.coord.size() << "*" << other.coord.size() << endl;
    */
    // Multiplication
    vector< monomial<gen> >::const_iterator ita = th.coord.begin();
    vector< monomial<gen> >::const_iterator ita_end = th.coord.end();
    vector< monomial<gen> >::const_iterator itb = other.coord.begin();
    vector< monomial<gen> >::const_iterator itb_end = other.coord.end();
    //  COUT << coord.size() << " " << (int) ita_end - (int) ita << " " << sizeof(monomial<gen>) << endl ;
    // first some trivial cases
    if (ita==ita_end || is_one(other)){
      res=th;
      return;
    }
    if (itb==itb_end || is_one(th)){
      res=other;
      return ;
    }
    index_t d1=th.degree(),d2=other.degree(),d(th.dim);
    double d10,d20;
    if ( 0 && 
	 th.dim==1 
	 && (d10=d1[0])>=FFTMUL_SIZE && (d20=d2[0])>=FFTMUL_SIZE && (ita_end-ita)*double(itb_end-itb)>(d10+d20)*std::log(double(d10+d20))
	 && is_integer_poly(th,false) && is_integer_poly(other,false)
	 ){
      modpoly A=polynome2poly1(th,1);
      modpoly B=polynome2poly1(other,1);
      modpoly C;
      mulmodpoly(A,B,0,C);
      poly12polynome(C,1,res,1);
      return;
    }
    double lagrtime=1.,sumdeg=0.;
    for (int i=0;i<th.dim;++i){
      int tmp=(d1[i]+d2[i]+1);
      if (tmp>=(1<<15)){
	res=monomial<gen>(gensizeerr(gettext("Polynomial exponent overflow.")),th.dim); 
	return;
      }
      sumdeg += tmp;
      lagrtime *= tmp;
    }
    d10=lagrtime;
    lagrtime *= sumdeg;
    // Now look if length a=1 or length b=1, happens frequently
    // think of x^3*y^2*z translated to internal form
    int c1=int(th.coord.size());
    if (c1==1){
      res=other.shift(th.coord.front().index,th.coord.front().value);
      return ;
    }
    int c2=int(other.coord.size());
    if (c2==1){
      res=th.shift(other.coord.front().index,other.coord.front().value);
      return;
    }
    //int t1=th.coord.front().value.type,t2=other.coord.front().value.type;
    gen T1=th.coord.front().value,T2=other.coord.front().value;
    // gen T1=th.coord[c1/2].value,T2=other.coord[c2/2].value;
    int t1=T1.type,t2=T2.type;
#if 1 // does not work if _ext are embedded inside fractions (check done in unext)
    if (t1==_EXT || t2==_EXT){
      gen minp;
      if (t1==_EXT)
	minp=*(T1._EXTptr+1);
      else
	minp=*(T2._EXTptr+1);
      polynome p1m,p2m,pm(th.dim);
      if (minp.type==_VECT && unext(th,minp,p1m) && unext(other,minp,p2m)){
	mulpoly(p1m,p2m,pm,0);
	ext(pm,minp,res);
	//Mul<gen>(ita,ita_end,itb,itb_end,pm.coord,th.is_strictly_greater,th.m_is_strictly_greater);
	//if (!(pm-res).coord.empty()) 
	//CERR << "err" << th << endl << other << endl << pm-res << endl;
	return;
      }
    }
#endif
#ifdef NO_TEMPLATE_MULTGCD
    Mul<gen>(ita,ita_end,itb,itb_end,res.coord,th.is_strictly_greater,th.m_is_strictly_greater);
    // Mul_gen(ita,ita_end,itb,itb_end,res.coord,th.is_strictly_greater,th.m_is_strictly_greater);
    return;
#else
    if ( 
	// true // used for debugging with small poly
	c1>50 || c2 >50 || (c1>7 && c2>7) 
	){
      // Degree info, try to multiply the polys using integer for the exponents
      ulonglong ans=1,pid1=1,pid2=1;
      for (int i=0;i<th.dim;++i){
	pid1 = pid1*unsigned(d1[i]+1);
	pid2 = pid2*unsigned(d2[i]+1);
      }
      for (int i=0;i<th.dim;++i){
	d[i]=d1[i]+d2[i]+1;
	ans = ans*unsigned(d[i]);
	if (ans/RAND_MAX>RAND_MAX)
	  break;
      }
      // ans/d[th.dim-1] == degree 1 with respect to main var and 0 for other
      // guess size of result
      // compare product of d1[i] with c1 and product of d2[i] with c2
      // for sparness factor
      double d1sparness=double(c1)/pid1;
      double d2sparness=double(c2)/pid2;
      ulonglong c1c2= ulonglong(c1)*c2;
      if (ans<c1c2)
	c1c2=ans;
      c1c2 = unsigned(std::sqrt(d1sparness*d2sparness)*c1c2);
      if (c1c2> (1<<24) )
	c1c2 = 1 << 24;
      // Possible improvement for modular product mod p in an array
      // make one of the argument with negative coeffs, the other with positive
      // init array with p^2, then type_operator_plus_times_reduce
      // could do += p1[]*p2[] and if result <0 add p^2
      // ?encode reduce as -p^2, and check sign in do_threadmult in threaded.h
      // OR use int128
      if (ans<=RAND_MAX) {
	if (reduce.type==_INT_ && reduce.val<46340 && reduce.val>0){ 
	  // Modular multiplication, convert everything to integers
	  vector< int_unsigned > p1,p2,p;
	  if (convert(th,d,p1,reduce.val) && convert(other,d,p2,reduce.val)){
	    if (10*lagrtime<double(c1)*c2*std::log(double(giacmax(c1,c2))))
	      smallmulpoly_interpolate(p1,p2,p,d,reduce.val);
	    else
	      smallmult(p1,p2,p,reduce.val,int(c1c2));
	    convert(p,d,res);
	    return ;
	  }
	}
	if ( //false 
	     (t1==_INT_ || t1==_ZINT) && (t2==_INT_ || t2==_ZINT)
	    ){
	  if (//1||
	      0 && 
	      c1>=FFTMUL_SIZE && c2>=FFTMUL_SIZE && th.dim>1 && d10*std::log(d10)<c1*double(c2)){
	    CERR << CLOCK()*1e-6 << " ?fftmult " << c1 << "*" << c2 << " fft " << d10 << endl;
#if 1
	    vecteur thv,otherv,resv;
	    polynome2poly1(th,d1,d,thv); 
	    polynome2poly1(other,d2,d,otherv);
	    fftmult(thv,otherv,resv,0);
	    poly12polynome(resv,d,res);
#if 0	    // debug
	    polynome res1(res.dim);
	    Mul<gen>(ita,ita_end,itb,itb_end,res1.coord,th.is_strictly_greater,th.m_is_strictly_greater);
	    if (res1!=res)
	      CERR << "fftmult * error " << res-res1 << endl;
#endif
	    return;
#endif
	  }
	  longlong maxp1,maxp2;
	  // should be T_unsigned<long,unsigned>
	  // instead tmp_operator_times converts longlong args of * to long
	  vector< T_unsigned<longlong,unsigned> > p1d,p2d,pd;
	  if (convert_int(th,d,p1d,maxp1) && convert_int(other,d,p2d,maxp2) ) {
	    double maxp1p2=double(maxp1)*maxp2;
	    unsigned minc1c2=giacmin(c1,c2);
	    double un63=double(ulonglong (1) << 63);
	    double res_size=double(minc1c2)*maxp1p2;
	    // Check number of required primes: 
	    res_size /= un63;
	    double nprimes=std::ceil(std::log(res_size)/std::log(2147483647.));
	    if (debug_infolevel>5) CERR << CLOCK() << " primes required " << nprimes << endl;
#ifdef INT128
	    if (nprimes>0 && nprimes<10){
	      // using multiplications with int128
	      vector< T_unsigned<int128_t,unsigned> > p1D,p2D,pD;
	      convert_int128(p1d,p1D);
	      convert_int128(p2d,p2D);
	      if (th.dim==1 || !threadmult<int128_t,unsigned>(p1D,p2D,pD,ans/d[0],0,c1c2))
		smallmult<int128_t,unsigned>(p1D,p2D,pD,0,c1c2);
	      if (debug_infolevel>5) CERR << CLOCK() << " end int128 mult " << endl;
	      unsigned pds=pD.size();
	      res_size=double(minc1c2)*maxp1p2/std::pow(2.0,127);
	      if (res_size<1){
		if (debug_infolevel>5) CERR << CLOCK() << " begin result conversion" << endl;
		convert_from<int128_t,unsigned>(pD,d,res,false);
		if (debug_infolevel>5) CERR << CLOCK() << " end result conversion" << endl;
		return;
	      }
	      vector< T_unsigned<gen,unsigned> > target;
	      convert(pD,target);
	      double primed=3037000499.; // floor(2^31.5)
	      primed /= std::sqrt(double(giacmax(minc1c2,4)));
	      gen targetprime = pow(plus_two,128);
	      int prime2=prevprime(int(std::floor(primed))).val;
	      for (;res_size>=1;){
                if (debug_infolevel>5)
                  CERR << "prime used " << prime2 << endl;		
		// the product of the two poly mod prime2 can be computed
		// without mod computation
		vector< T_unsigned<longlong,unsigned> > p1add,p2add,add;
		smod(p1d,p1add,prime2);
		smod(p2d,p2add,prime2);
		if (th.dim==1 || !threadmult<longlong,unsigned>(p1add,p2add,add,ans/d[0],0,pds))
		  smallmult(p1add,p2add,add,0,pds);
		smod(add,add,prime2);
		if (debug_infolevel>5) CERR << CLOCK() << " ichrem longlong" << endl;	
		ichrem(add,prime2,pd,target,targetprime); // pd is not used at all here
		res_size /= prime2;
		prime2=prevprime(prime2-2).val;
	      }
	      if (debug_infolevel>5) CERR << CLOCK() << " begin result conversion" << endl;
	      convert_from<gen,unsigned>(target,d,res,true);
	      if (debug_infolevel>5) CERR << CLOCK() << " end result conversion" << endl;
	      return;
	    }
#endif // INT128
	    if (
#ifdef HAVE_GMPXX_H
		mpzclass_allowed?nprimes<3.5:nprimes<4.5
#else
		nprimes<4.5
#endif
		){
	      if(debug_infolevel>5) CERR << "Begin smallmult " << CLOCK() << endl;
	      if (th.dim==1 || !threadmult<longlong,unsigned>(p1d,p2d,pd,unsigned(ans/d[0]),0,size_t(c1c2)))
		smallmult<longlong,unsigned>(p1d,p2d,pd,0,size_t(c1c2));
	      if(debug_infolevel>5) CERR << "End smallmult " << CLOCK() << endl;
	      unsigned pds=unsigned(pd.size());
	      if ( res_size< 1 ){
		convert_from<longlong,unsigned>(pd,d,res,false);
		return;
	      }
	      // /*
	      else {
		if (debug_infolevel)
		  CERR << nprimes << " primes required" << endl;
		vector< T_unsigned<gen,unsigned> > target;
		// convert(pd,target);
		int prime1=2147483647;
		double primed=3037000499.; // floor(2^31.5)
		primed /= std::sqrt(double(giacmax(minc1c2,4)));
		// the product of the two poly mod prime2 can be computed
		// without mod computation
		int prime2=prevprime(int(std::floor(primed))).val;
		gen targetprime = pow(plus_two,64);
		for(;res_size>=1;--nprimes){
		  bool withsmod
		    =false;
		    //=true;
		    //=(res_size>std::pow(prime1-1000,nprimes-1)*prime2);
		  if (debug_infolevel>5) CERR << CLOCK() << " prime " << (withsmod?prime1:prime2) << endl;
		  if (withsmod){
		    vector< int_unsigned > p1,p2,padd;
		    if (!convert(th,d,p1,prime1) || !convert(other,d,p2,prime1)){
#ifndef NO_STDEXCEPT
		      setsizeerr(); // should not happen
#endif
		    }
		    if (th.dim==1 || !threadmult<int,unsigned>(p1,p2,padd,unsigned(ans/d[0]),prime1,pds))
		      smallmult(p1,p2,padd,prime1,pds);
		    if (debug_infolevel>5) CERR << CLOCK() << " ichrem int mod" << endl;	
		    ichrem(padd,prime1,pd,target,targetprime);
		    res_size /= prime1;
		    prime1=prevprime(prime1-2).val;
		  }
		  else {
		    vector< T_unsigned<longlong,unsigned> > p1add,p2add,add;
		    smod(p1d,p1add,prime2);
		    smod(p2d,p2add,prime2);
		    if (th.dim==1 || !threadmult<longlong,unsigned>(p1add,p2add,add,unsigned(ans/d[0]),0,pds))
		      smallmult(p1add,p2add,add,0,pds);
		    smod(add,add,prime2);
		    if (debug_infolevel>5) CERR << CLOCK() << " ichrem longlong" << endl;	
		    ichrem(add,prime2,pd,target,targetprime);
		    res_size /= prime2;
		    prime2=prevprime(prime2-2).val;
		  }
		  if (debug_infolevel>5) CERR << CLOCK() << " ichrem end" << endl;
		}
		if (debug_infolevel>5) CERR << CLOCK() << endl;
		convert_from<gen,unsigned>(target,d,res,true);
		if (debug_infolevel>5) CERR << CLOCK() << endl;
		return;
	      } // end else (some primes are required)
	      // */
	    } // end nprimes<something
	  } // end conversion to longlong possible
	} // end t1==_INT_
	if (t1==_MOD || t2==_MOD){
	  gen modulo;
	  if (t1==_MOD)
	    modulo=*(T1._MODptr+1);
	  else
	    modulo=*(T2._MODptr+1);
	  if (modulo.type==_INT_){
	    polynome p1m=unmodularize(th),p2m=unmodularize(other);
	    longlong maxp1,maxp2;
	    vector< T_unsigned<longlong,unsigned> > p1d,p2d,pd;
	    if (convert_int(p1m,d,p1d,maxp1) && convert_int(p2m,d,p2d,maxp2) ){
	      double maxp1p2=double(maxp1)*maxp2;
	      unsigned minc1c2=giacmin(c1,c2);
	      double un63=double(ulonglong (1) << 63);
	      double res_size=double(minc1c2)*maxp1p2;
	      // Check if mod may be done only at the end
	      res_size /= un63;
	      if (res_size<1){
		if (th.dim==1 || !threadmult<longlong,unsigned>(p1d,p2d,pd,unsigned(ans/d[0]),0,size_t(c1c2)))
		  smallmult(p1d,p2d,pd,0,size_t(c1c2));
		smod(pd,pd,modulo.val);
		convert_from<longlong,unsigned>(pd,d,res,false);
	      }
	      else {
#ifdef INT128
		if (res_size<un63){
		  vector< T_unsigned<int128_t,unsigned> > p1D,p2D,pD;
		  convert_int128(p1d,p1D);
		  convert_int128(p2d,p2D);
		  if (th.dim==1 || !threadmult<int128_t,unsigned>(p1D,p2D,pD,ans/d[0],0,c1c2))
		    smallmult<int128_t,unsigned>(p1D,p2D,pD,0,c1c2);
		  smod(pD,modulo.val);
		  convert_from<int128_t,unsigned>(pD,d,res,false);
		}
		else
#endif 
		  {
		    if (th.dim==1 || !threadmult<longlong,unsigned>(p1d,p2d,pd,unsigned(ans/d[0]),modulo.val,size_t(c1c2)))
		      smallmult(p1d,p2d,pd,modulo.val,size_t(c1c2));
		    convert_from<longlong,unsigned>(pd,d,res,false);
		  }
	      }
	      // modularize
	      gen g=makemod(res,modulo);
	      if (g.type==_POLY)
		res=*g._POLYptr;
	      else
		res.coord.clear();
	      return;
	    }
	  } // end modulo.type==_INT_
	} // end _MOD types
	if (t1==_DOUBLE_ && t2==_DOUBLE_){
	  vector< T_unsigned<double,unsigned> > p1d,p2d,pd;
	  if (convert_double(th,d,p1d) && convert_double(other,d,p2d) ){
	    if (th.dim==1 || !threadmult<double,unsigned>(p1d,p2d,pd,unsigned(ans/d[0]),0,size_t(c1c2)))
	      smallmult<double,unsigned>(p1d,p2d,pd,0,size_t(c1c2));
	    convert_from<double,unsigned>(pd,d,res,true);
	    return;
	  }
	}
	// FIXME : the comparison 10*lagr_time is not good at all for e.g int/double args
	if (is_zero(reduce) && 100*lagrtime<double(c1)*c2*std::log(double(giacmax(c1,c2)))){
	  vector< T_unsigned<gen,unsigned> > p1,p2,p;
	  convert<gen,unsigned>(th,d,p1); 
	  convert<gen,unsigned>(other,d,p2);
	  smallmulpoly_interpolate<gen,unsigned>(p1,p2,p,d);
	  convert<gen,unsigned>(p,d,res);
	  return;
	}
#ifdef HAVE_GMPXX_H
	if (t1<=_ZINT && t2<=_ZINT && mpzclass_allowed){
	  if (debug_infolevel>1)
	    CERR << "mpz mult convert begin " << CLOCK() << endl;
	  vector< T_unsigned<myint,unsigned> > p1d,p2d,pd;
	  if (convert_myint(th,d,p1d) && convert_myint(other,d,p2d) ){
	    if (debug_infolevel>1)
	      CERR << "mpz mult begin " << CLOCK() << endl;
	    // threadmult is slow for heap allocated data because of malloc lock
	    // if (th.dim==1 || !threadmult<myint,unsigned>(p1d,p2d,pd,ans/d[0],0,c1c2))
	      smallmult<myint,unsigned>(p1d,p2d,pd,0,c1c2);
	    if (debug_infolevel>1)
	      CERR << "mpz mult end " << CLOCK() << endl;
	    convert_from<myint,unsigned>(pd,d,res,false);
	    return;
	  }
	}
#endif
	vector< T_unsigned<gen,unsigned> > p1,p2,p;
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult convert begin " << CLOCK() << endl;
	convert<gen,unsigned>(th,d,p1);
	convert<gen,unsigned>(other,d,p2);
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult begin " << CLOCK() << endl;
	// threadmult<gen,.> does not work on multi-CPU (malloc error with GMP data structures) and it would be slow anyway because of malloc locks
	// if (th.dim==1 || !threadmult<gen,unsigned>(p1,p2,p,ans/d[0],0,c1c2))
	smallmult<gen,unsigned>(p1,p2,p,0,size_t(c1c2));
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult end " << CLOCK() << endl;
	convert<gen,unsigned>(p,d,res);
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult convert end " << CLOCK() << endl;
	return ;
	// CERR << "Copy " << CLOCK() << " " << copy_number << endl;
	// if (th.dim==12)
	//  CERR << "sort *unsigned end " << CLOCK() << " " << res.coord.size() << endl;
	/*
	  polynome save(res);
	  sort(res.coord.begin(),res.coord.end(),th.m_is_strictly_greater); // still done
	  if (res!=save)
	  CERR << "unsorted" << endl;
	*/
	// if (res.coord.size()==1357366)
	//  CERR << "coucou" << endl;
	// if (th.dim==12){
	//  CERR << "*unsigned end " << CLOCK() << endl;
      }
      if (ans/RAND_MAX<RAND_MAX){
	if ( (t1==_INT_ || t1==_ZINT) && (t2==_INT_ || t2==_ZINT)
	    ){
	  longlong maxp1,maxp2;
	  // should be T_unsigned<long,unsigned>
	  // instead tmp_operator_times converts longlong args of * to long
	  vector< T_unsigned<longlong,ulonglong> > p1d,p2d,pd;
	  if (debug_infolevel>1)
	    CERR << CLOCK() << "longlong mult ulonglong convert begin " << CLOCK() << endl;
	  if (convert_int(th,d,p1d,maxp1) && convert_int(other,d,p2d,maxp2) ){
	    double maxp1p2=double(maxp1)*maxp2;
	    unsigned minc1c2=giacmin(c1,c2);
	    double un63=double(ulonglong (1) << 63);
	    double res_size=double(minc1c2)*maxp1p2;
	    // Check number of required primes: 
	    res_size /= un63;
	    double nprimes=std::ceil(std::log(res_size)/std::log(2147483647.));
	    if (debug_infolevel>5) CERR << CLOCK() << " primes required " << nprimes << endl;
#ifdef INT128
	    if (nprimes>0 && nprimes<10){
	      // using multiplications with int128
	      vector< T_unsigned<int128_t,ulonglong> > p1D,p2D,pD;
	      convert_int128(p1d,p1D);
	      convert_int128(p2d,p2D);
	      if (th.dim==1 || !threadmult<int128_t,ulonglong>(p1D,p2D,pD,ans/d[0],0,c1c2))
		smallmult<int128_t,ulonglong>(p1D,p2D,pD,0,c1c2);
	      if (debug_infolevel>5) CERR << CLOCK() << " end int128 mult " << endl;
	      unsigned pds=pD.size();
	      res_size=double(minc1c2)*maxp1p2/std::pow(2.0,127);
	      if (res_size<1){
		if (debug_infolevel>5) CERR << CLOCK() << " begin result conversion" << endl;
		convert_from<int128_t,ulonglong>(pD,d,res,false);
		if (debug_infolevel>5) CERR << CLOCK() << " end result conversion" << endl;
		return;
	      }
	      vector< T_unsigned<gen,ulonglong> > target;
	      convert(pD,target);
	      double primed=3037000499.; // floor(2^31.5)
	      primed /= std::sqrt(double(giacmax(minc1c2,4)));
	      gen targetprime = pow(plus_two,128);
	      int prime2=prevprime(int(std::floor(primed))).val;
	      for (;res_size>=1;){
                if (debug_infolevel>5)
                  CERR << "prime used " << prime2 << endl;		
		// the product of the two poly mod prime2 can be computed
		// without mod computation
		vector< T_unsigned<longlong,ulonglong> > p1add,p2add,add;
		smod(p1d,p1add,prime2);
		smod(p2d,p2add,prime2);
		if (th.dim==1 || !threadmult<longlong,ulonglong>(p1add,p2add,add,ans/d[0],0,pds))
		  smallmult(p1add,p2add,add,0,pds);
		smod(add,add,prime2);
		if (debug_infolevel>5) CERR << CLOCK() << " ichrem longlong" << endl;	
		ichrem(add,prime2,pd,target,targetprime); // pd is not used at all here
		res_size /= prime2;
		prime2=prevprime(prime2-2).val;
	      }
	      if (debug_infolevel>5) CERR << CLOCK() << " begin result conversion" << endl;
	      convert_from<gen,ulonglong>(target,d,res,false);
	      if (debug_infolevel>5) CERR << CLOCK() << " end result conversion" << endl;
	      return;
	    }
#endif
	    if ( res_size< 1 ){
	      if (debug_infolevel>1)
		CERR << CLOCK() << "longlong mult ulonglong begin " << CLOCK() << endl;
	      if (th.dim==1 || !threadmult<longlong,ulonglong>(p1d,p2d,pd,ans/d[0],0,size_t(c1c2)))
		smallmult<longlong,ulonglong>(p1d,p2d,pd,0,size_t(c1c2));
	      if (debug_infolevel>1)
		CERR << CLOCK() << "longlong mult ulonglong end " << CLOCK() << endl;
	      convert_from<longlong,ulonglong>(pd,d,res,false);
	      if (debug_infolevel>1)
		CERR << CLOCK() << "longlong mult ulonglong convert end " << CLOCK() << endl;
	      return;
	    }
	  }
	}
	if (t1==_DOUBLE_ && t2==_DOUBLE_){
	  vector< T_unsigned<double,ulonglong> > p1d,p2d,pd;
	  if (convert_double(th,d,p1d) && convert_double(other,d,p2d) ){
	    if (th.dim==1 || !threadmult<double,ulonglong>(p1d,p2d,pd,unsigned(ans/d[0]),0,size_t(c1c2)))
	      smallmult<double,ulonglong>(p1d,p2d,pd,0,size_t(c1c2));
	    convert_from<double,ulonglong>(pd,d,res,true);
	    return;
	  }
	}
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult ulonglong convert begin " << CLOCK() << endl;
	vector< T_unsigned<gen,ulonglong> > p1,p2,p;
	convert<gen,ulonglong>(th,d,p1);
	convert<gen,ulonglong>(other,d,p2);
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult ulonglong mult begin " << CLOCK() << endl;
	smallmult<gen,ulonglong>(p1,p2,p,0,size_t(c1c2));
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult ulonglong mult end " << CLOCK() << endl;
	convert<gen,ulonglong>(p,d,res);
	if (debug_infolevel>1)
	  CERR << CLOCK() << "gen mult ulonglong convert end " << CLOCK() << endl;
	// if (th.dim==12)
	//  CERR << "sort*longlong end " << CLOCK() << " " << res.coord.size() << endl;
	// sort(res.coord.begin(),res.coord.end(),th.m_is_strictly_greater); // still done
	// if (th.dim==12)
	//  CERR << "*longlong end " << CLOCK() << endl;
	return ;
      }
    } // end if c1>7 && c2>7
    if (debug_infolevel>1)
      CERR << CLOCK() << "Mul<gen> begin " << CLOCK() << endl;
    if (c1*c2<100)
      Mul_gen(ita,ita_end,itb,itb_end,res.coord,th.is_strictly_greater,th.m_is_strictly_greater);
    else
      Mul<gen>(ita,ita_end,itb,itb_end,res.coord,th.is_strictly_greater,th.m_is_strictly_greater);
    if (debug_infolevel>1)
      CERR << CLOCK() << "Mul<gen> end " << CLOCK() << endl;
    // if (th.dim==12)
    //  CERR << "* end " << CLOCK() << " " << res.coord.size() << endl;
    return ;
#endif // NO_TEMPLATE_MULTGCD besta_os
  }

  polynome operator * (const polynome & th, const polynome & other) {
    polynome res(th.dim,th); // reserve() is done by Mul<gen>
    mulpoly(th,other,res,0);
    return res;
  }

  polynome & operator *= (polynome & th, const polynome & other) {
#ifdef NSPIRE
    th=th*other;
#else
    mulpoly(th,other,th,0);
#endif
    return th;
  }

  /* 
    Note about Miller Pure Recurrence, see Knuth, TAOC v.2
    If   P(x) = sum_{i=0}^n p_i x^k
    Then   P(x)^m = sum_{k=0}^{m*n} a(m,k) x^k
    Where
      a(m,0) = p_0^m, 
      a(m,k) = 1/(k p_0) sum_{i=1}^min(n,k) p_i ((m+1)i-k) a(m,k-i),
    For k<=m we have a division free implementation, let
    a(m,k)=b(m,k) p_0^(m-k)
    b(m,0)=1, b(m,k)=1/k sum_{i=1}^min(n,k) p_i ((m+1)i-k) b(m,k-i) p_0^(i-1)
    But for k>m, the division by p0 must be done at each step 
    which might be too costly
    Example: P(x)=3x^2+2x+5, n=2
    m=2: P^2=9*x^4+12*x^3+34*x^2+20*x+25
    b(m,0)=1, a(m,0)=25
    b(m,1)= p_1*(3*1-1)*b(m,0)=4, a(m,1)=20
    b(m,2)=1/2*(p_1*(3*1-2)*b(m,1)+p_2*(3*2-2)*p_0*b(m,0))
          =1/2*(2*4+3*4*5)=34, a(m,2)=34
    a(m,3)=1/3/5*(p_1*(3*1-3)*a(m,2)+p_2*(3*2-3)*a(m,1))=12
    a(m,4)=1/4/5*(p_1*(3*1-4)*a(m,3)+p_2*(3*2-4)*a(m,2))=1/20*(-2*12+3*2*34)=9
    There is a case where no bad division occurs: if p_0 is a constant
    (no other variable occur) or if n==1 (binomial formula)
  */
  bool powpoly(const polynome & th, int u,polynome & res){
    if (u<0){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Negative polynome power"));
#endif
      return false; 
    }
    if (!u){
      res= tensor<gen>(gen(1),th.dim);
      return true;
    }
    if (u==1){
      res=th;
      return true;
    }
    if (u==2){
      res=th*th;
      return true;
    }
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      res.coord.clear();
      res.coord.push_back(monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),res.dim));
      return false;
    }
    if (th.dim==1 && u>10){
      modpoly a;
      polynome2poly1(th,1,a);
      gen b=pow(gen(a,_POLY1__VECT),u);
      if (b.type==_VECT){
	poly12polynome(*b._VECTptr,1,res,1);
	return true;
      }
    }
    vector< monomial<gen> >::const_iterator ita = th.coord.begin();
    vector< monomial<gen> >::const_iterator ita_end = th.coord.end();
    int c1=int(ita_end-ita);
    if (c1==0){
      res=th;
      return true;
    }
    if (c1==1){
      res=th;
      res.coord.front().value=pow(res.coord.front().value,u);
      res.coord.front().index = res.coord.front().index*u ;
      return true;
    }
    ulonglong ans=1,pid1=1;
    index_t d1=th.degree(),d(th.dim);
    for (int i=0;i<th.dim;i++){
      if (d1[i]==1){ // apply binomial formula
	vecteur v,w(u+1);
	polynome2poly1(th,i+1,v);
	gen a=v[0],b=v[1],bk=b;
	vecteur vbk=makevecteur(1,b);
	for (int j=2;j<=u;++j){
	  bk=bk*b;
	  vbk.push_back(bk);
	}
	// (ax+b)^u=sum_j=0^u comb(u,j)*a^j*b^(u-j)*x^j
	gen aj=1,cuj=1;
	for (int j=0;j<=u;++j){
	  w[u-j]=(aj*cuj)*vbk[u-j];
	  aj=aj*a;
	  cuj=(int(u-j)*cuj)/int(j+1);
	}
	poly12polynome(w,i+1,res,th.dim);
	return true;
      }
    }
#ifndef NO_TEMPLATE_MULTGCD
    for (int i=0;i<th.dim;++i){
      pid1 = pid1*unsigned(d1[i]+1);
    }
    for (int i=0;i<th.dim;++i){
      d[i]=d1[i]*u+1;
      ans = ans*unsigned(d[i]);
      if (ans/RAND_MAX>RAND_MAX)
	break;
    }
    if (ans<=RAND_MAX){
      // int t1=th.coord.front().value.type;
      /*
#ifdef HAVE_GMPXX_H
      if (t1<=_ZINT && mpzclass_allowed){
	vector< T_unsigned<myint,unsigned> > p1,p2,p;
	if (convert_myint(th,d,p1) ){
	  p2=p1;
	  for (int i=1;i<u;++i){
	    if (debug_infolevel>20)
	    CERR << "power mpz " << i << " " << CLOCK() << endl;
	    unsigned c1c2 = p1.size()*p2.size();
	    if (th.dim==1 || !threadmult<myint,unsigned>(p1,p2,p,ans/d[0],0,c1c2))
	      smallmult<myint,unsigned>(p1,p2,p,0,c1c2);
	    p1=p;
	  }
	  convert_from<myint,unsigned>(p,d,res,false);
	  return;
	} 
      }
#endif
      */
      vector< T_unsigned<gen,unsigned> > p1,p2,p;
      convert<gen,unsigned>(th,d,p1);
      p2=p1;
      for (int i=1;i<u;++i){
	if (debug_infolevel>20)
	  CERR << "power gen " << i << " " << CLOCK() << endl;
	unsigned c1c2 = unsigned(p1.size()*p2.size());
	// threadmult<gen,.> does not work on multi-CPU (malloc error with GMP data structures)
	// if (th.dim==1 || !threadmult<gen,unsigned>(p1,p2,p,ans/d[0],0,c1c2))
	  smallmult<gen,unsigned>(p1,p2,p,0,c1c2);
	p1=p;
      }
      convert<gen,unsigned>(p,d,res);
    }
    else // ans>RAND_MAX
#endif // NO_TEMPLATE_MULTGCD
      res=Tpow(th,u);
    return true;
  }

  polynome operator - (const polynome & th) {  
    // Tensor addition
    polynome res(th.dim,th);
    vector< monomial<gen> >::const_iterator a = th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end = th.coord.end();
    res.coord.reserve(a_end - a );  
    for (;a!=a_end;++a){
      res.coord.push_back(monomial<gen>(-(*a).value,(*a).index));
    }
    return res;
  }

  void submulpoly(const polynome & a,const polynome & b,const polynome & q,polynome & r){
#if 0
    r=a-b*q;
#else
    polynome tmp(a.dim);
    mulpoly(b,q,tmp,0);
    vector< monomial<gen> >::const_iterator a_beg=a.coord.begin();
    vector< monomial<gen> >::const_iterator a_end=a.coord.end();
    vector< monomial<gen> >::const_iterator b_beg=tmp.coord.begin();
    vector< monomial<gen> >::const_iterator b_end=tmp.coord.end();
    vector< monomial<gen> > & new_coord=r.coord;
    new_coord.clear();
    for (;;) {
      // If a is empty, fill up with elements from b and stop
      if (a_beg == a_end) {
	while (b_beg != b_end) {
	  new_coord.push_back(-(*b_beg));
	  ++b_beg;
	}
	break;
      } 
      const index_m & pow_a = a_beg->index;
      // If b is empty, fill up with elements from a and stop
      if (b_beg == b_end) {
	while (a_beg != a_end) {
	  new_coord.push_back(*a_beg);
	  ++a_beg;
	}
	break;
      } 
      const index_m & pow_b = b_beg->index;
      // a and b are non-empty, compare powers
      if (pow_a!=pow_b){
	if (a.is_strictly_greater(pow_a, pow_b)) {
	  // a has lesser power, get coefficient from a
	  new_coord.push_back(*a_beg);
	  ++a_beg;
	} 
	else  {
	  // b has lesser power, get coefficient from b
	  new_coord.push_back(-(*b_beg));
	  ++b_beg;
	} 
      }
      else {
	gen diff = (*a_beg).value - (*b_beg).value;
	if (!is_zero(diff))
	  new_coord.push_back(monomial<gen>(diff,pow_a));
	++a_beg;
	++b_beg;
      }
    }  
#endif
  }

  // exactquo==2 means we know that b divides a and we search the cofactor
  // exactquo==1 means we want to check that b divides a
  // exactquo==-1 means compute quotient first using heap div then r=a-b*quo
  // exactquo==-2 means compute quotient only using heap div
  bool divrem1(const polynome & a,const polynome & b,polynome & quo,polynome & r,int exactquo,bool allowrational) {
    quo.coord.clear();
    quo.dim=a.dim;
    r.dim=a.dim;
    r.coord.clear();
    int bs=int(b.coord.size());
    if ( b.dim<=1 || bs==1 || a.coord.empty() ){
      return a.TDivRem(b,quo,r,allowrational) && (exactquo>0?r.coord.empty():true) ;
    }
    int bdeg=b.coord.front().index.front(),rdeg=a.lexsorted_degree(),ddeg=rdeg-bdeg;
#ifndef NO_TEMPLATE_MULTGCD
    int hashdivremres=0;
    if (ddeg>3 && !allowrational){ 
      index_t d1=a.degree(),d2=b.degree(),d3=b.coord.front().index.iref(),d(a.dim);
      // i-th degrees of th / other in quotient and remainder
      // are <= i-th degree of th + ddeg*(i-th degree of other - i-th degree of lcoeff of other) 
      double ans=1;
      for (int i=0;i<a.dim;++i){
	if (exactquo==2)
	  d[i]=d1[i]+1;
	else
	  d[i]=d1[i]+(ddeg+1)*(d2[i]-d3[i])+1;
	int j=1;
	// round to newt power of 2
	for (;;j++){
	  if (!(d[i] >>= 1))
	    break;
	}
	d[i] = 1 << j;
	ans = ans*unsigned(d[i]);
	if (ans/RAND_MAX>RAND_MAX)
	  break;
      }
      bool doit=true;
      if (ans<RAND_MAX){
	std::vector<unsigned> vars(a.dim);
	vars[a.dim-1]=1;
	for (int i=a.dim-2;i>=0;--i){
	  vars[i]=d[i+1]*vars[i+1];
	}
	if (debug_infolevel>1)
	  CERR << "divrem1 convert " << CLOCK() << std::endl;
	{
	  std::vector< T_unsigned<longlong,unsigned> > p1,p2,quot,remain;
	  longlong maxp1,maxp2;
	  doit=convert_int(a,d,p1,maxp1) && convert_int(b,d,p2,maxp2) && maxp1/RAND_MAX < RAND_MAX;
	  if (doit){
	    if (maxp1<int(RAND_MAX) && maxp2<double(RAND_MAX)/p2.size()){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 int32 begin " << CLOCK() << " maxp1=" << maxp1 << " maxp2=" << maxp2 << " ddeg=" << ddeg << std::endl;
	      // try with int instead of longlong
	      std::vector< T_unsigned<int,unsigned> > p132,p232,quot32,remain32;
	      if (convert_int32(a,d,p132) && convert_int32(b,d,p232) && 
		  (hashdivremres=hashdivrem<int,unsigned>(p132,p232,quot32,remain32,vars,0,RAND_MAX/double(maxp2)/p2.size(),false,exactquo))>=1){
		if (debug_infolevel>1)
		  CERR << "hashdivrem1 int32 success " << CLOCK() << " maxp1=" << maxp1 << " maxp2=" << maxp2 << " ddeg=" << ddeg << std::endl;
		convert_from(quot32,d,quo,true);
		if (exactquo==-1 && hashdivremres==2) submulpoly(a,b,quo,r); else
		  convert_from(remain32,d,r,true);
		return true;
	      }
	      else {
		if (debug_infolevel>1)	      
		  CERR << "hashdivrem1 int32 failure " << CLOCK() << std::endl;
	      }
	    }
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1 longlong begin " << CLOCK() << " maxp1=" << maxp1 << " maxp2=" << maxp2 << " ddeg=" << ddeg << std::endl;
	    if ((hashdivremres=hashdivrem<longlong,unsigned>(p1,p2,quot,remain,vars,/* reduce*/0,RAND_MAX/double(maxp2)/p2.size()*RAND_MAX,false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 longlong end " << CLOCK() << std::endl;
	      convert_from(quot,d,quo,false);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from(remain,d,r,false);
	      return true;
	    }
	    else {
	      if (debug_infolevel>1)	      
		CERR << "hashdivrem1 longlong failure " << CLOCK() << std::endl;
	    }
	  }
#ifdef INT128
	  {
	    int128_t maxp1,maxp2;
	    vector< T_unsigned<int128_t,unsigned> > aD,bD,qD,rD;
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1 int128 int begin " << CLOCK()  << " ddeg=" << ddeg << std::endl;
	    if (convert_int(a,d,aD,maxp1) && convert_int(b,d,bD,maxp2) && (hashdivremres=hashdivrem<int128_t,unsigned>(aD,bD,qD,rD,vars,0,1.7e38/double(maxp2)/p2.size(),false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 int128 int success " << CLOCK() << " maxp1=" << double(maxp1) << " maxp2=" << double(maxp2) << " ddeg=" << ddeg << std::endl;
	      convert_from<int128_t,unsigned>(qD,d,quo,true);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from<int128_t,unsigned>(rD,d,r,true);
	      return true;
	    }
	  }
#endif
	  doit=false;
	}
#ifdef HAVE_GMPXX_H
	if (mpzclass_allowed)
	{
	  std::vector< T_unsigned<myint,unsigned> > p1,p2,quot,remain;
	  if (debug_infolevel>1)
	    CERR << "divrem1mpz int convert " << CLOCK() << std::endl;
	  doit=convert_myint(a,d,p1) && convert_myint(b,d,p2);
	  if (doit){
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1mpz int begin " << CLOCK() << " ddeg=" << ddeg << std::endl;
	    if ((hashdivremres=hashdivrem<myint,unsigned>(p1,p2,quot,remain,vars,/* reduce */ 0,/* no size check */0.0,false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1mpz int end " << CLOCK() << std::endl;
	      convert_from(quot,d,quo,false);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from(remain,d,r,false);
	      return true;
	    }
	    else {
	      if (debug_infolevel>1)	      
		CERR << "hashdivrem1mpz int failure " << CLOCK() << std::endl;
	    }
	  }
	}
#endif
      }
      if (doit && ans/RAND_MAX<RAND_MAX){
	//#ifdef __VISUALC__ // Visual C++?
	// typedef unsigned __int64 ulonglong ;
	// #else
	// typedef unsigned long long ulonglong;
	//#endif
	std::vector<ulonglong> vars(a.dim);
	vars[a.dim-1]=1;
	for (int i=a.dim-2;i>=0;--i){
	  vars[i]=d[i+1]*vars[i+1];
	}
	if (debug_infolevel>1)
	  CERR << "divrem1 convert " << CLOCK() << std::endl;
	{
	  std::vector< T_unsigned<longlong,ulonglong> > p1,p2,quot,remain;
	  longlong maxp1,maxp2;
	  doit=convert_int(a,d,p1,maxp1) && convert_int(b,d,p2,maxp2) && maxp1/RAND_MAX < RAND_MAX;
	  // doit=false;
	  if (doit){
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1 longlong ulonglong begin " << CLOCK() << " maxp1=" << maxp1 << " maxp2=" << maxp2 << " ddeg=" << ddeg << std::endl;
	    if ((hashdivremres=hashdivrem<longlong,ulonglong>(p1,p2,quot,remain,vars,/* reduce */0,RAND_MAX/double(maxp2)/p2.size()*RAND_MAX,false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 longlong ulonglong end " << CLOCK() << std::endl;
	      convert_from(quot,d,quo,false);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from(remain,d,r,false);
	      return true;
	    }
	    else {
	      if (debug_infolevel>1)	      
		CERR << "hashdivrem1 longlong ulonglong failure " << CLOCK() << std::endl;
	    }
	  }
#ifdef INT128
	  {
	    int128_t maxp1,maxp2;
	    vector< T_unsigned<int128_t,ulonglong> > aD,bD,qD,rD;
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1 int128 ulonglong begin " << CLOCK() <<  " ddeg=" << ddeg << std::endl;
	    if (convert_int(a,d,aD,maxp1) && convert_int(b,d,bD,maxp2) && (hashdivremres=hashdivrem<int128_t,ulonglong>(aD,bD,qD,rD,vars,0,1.7e38/double(maxp2)/p2.size(),false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 int128 ulonglong success " << CLOCK() << " maxp1=" << double(maxp1) << " maxp2=" << double(maxp2) << " ddeg=" << ddeg << std::endl;
	      convert_from<int128_t,ulonglong>(qD,d,quo,true);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from<int128_t,ulonglong>(rD,d,r,true);
	      return true;
	    }
	  }
#endif
	}
#ifdef HAVE_GMPXX_H
	if (mpzclass_allowed)
	{
	  std::vector< T_unsigned<myint,ulonglong> > p1,p2,quot,remain;
	  // longlong maxp1,maxp2;
	  if (debug_infolevel>1)
	    CERR << "divrem1mpz ulonglong convert " << CLOCK() << std::endl;
	  doit=convert_myint(a,d,p1) && convert_myint(b,d,p2);
	  if (doit){
	    if (debug_infolevel>1)
	      CERR << "hashdivrem1z ulonglong begin " << CLOCK() <<  " ddeg=" << ddeg << std::endl;
	    if ((hashdivremres=hashdivrem<myint,ulonglong>(p1,p2,quot,remain,vars,/* reduce */ 0,/* no size check */0.0,false,exactquo))>=1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem1 ulonglong end " << CLOCK() << std::endl;
	      convert_from(quot,d,quo,false);
	      if (hashdivremres==2 && exactquo==-1) submulpoly(a,b,quo,r); else
		convert_from(remain,d,r,false);
	      return true;
	    }
	    else {
	      if (debug_infolevel>1)	      
		CERR << "hashdivrem1 ulonglong failure " << CLOCK() << std::endl;
	    }
	  }
	}
#endif
      }
    } // end if (ddeg>3)
#endif // NO_TEMPLATE_MULTGCD
    return a.TDivRem1(b,quo,r,allowrational,exactquo>0);
  }

  polynome operator / (const polynome & th,const polynome & other) {  
    if (Tis_one(other)) return th;
    polynome rem(th.dim,th),quo(th.dim,th);
    // if ( !(th).TDivRem1(other,quo,rem) )
    if ( !divrem1(th,other,quo,rem) ){
#ifdef NO_STDEXCEPT
      quo.coord.clear();
      quo.coord.push_back(monomial<gen>(gensizeerr(gettext("Unable to divide, perhaps due to rounding error")+th.print()+" / "+other.print()),quo.dim));
#else
      setsizeerr(gettext("Unable to divide, perhaps due to rounding error")+th.print()+" / "+other.print());
#endif
    }
    return(quo);
  }

  polynome operator / (const polynome & th,const gen & fact ) {  
    if (fact==gen(1))
      return th;
    polynome res(th.dim,th);
    vector< monomial<gen> >::const_iterator a = th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end = th.coord.end();
    Div(a,a_end,fact,res.coord);
    return res;
  }

  polynome operator % (const polynome & th,const polynome & other) {  
    polynome rem(th.dim,th),quo(th.dim,th);
    if ( !(th).TDivRem1(other,quo,rem) ){
#ifdef NO_STDEXCEPT
      rem.coord.clear();
      rem.coord.push_back(monomial<gen>(gensizeerr(gettext("Unable to divide, perhaps due to rounding error")+th.print()+" / "+other.print()),quo.dim));
#else
      setsizeerr(gettext("Unable to divide, perhaps due to rounding error")+th.print()+" / "+other.print());
#endif
    }
    return(rem);
  }

  polynome operator % (const polynome & th, const gen & modulo) {  
    polynome res(th.dim,th);
    vector< monomial<gen> >::const_iterator a = th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end = th.coord.end();
    res.coord.reserve(a_end - a );  
    for (;a!=a_end;++a){
      gen tmp((*a).value % modulo);
      if (!is_zero(tmp))
	res.coord.push_back(monomial<gen>(tmp,a->index));
    }
    return res;
  }

  polynome re(const polynome & th){
    return Tapply(th,giac::no_context_re);
  }

  polynome im(const polynome & th){
    return Tapply(th,giac::no_context_im);
  }

  polynome conj(const polynome & th){
    return Tapply(th,giac::no_context_conj);
  }

  void smod(const polynome & th, const gen & modulo,polynome & res){
    vector< monomial<gen> >::const_iterator a = th.coord.begin();
    vector< monomial<gen> >::const_iterator a_end = th.coord.end();
    res.coord.clear();
    res.coord.reserve(a_end - a );  
    for (;a!=a_end;++a){
      const gen & tmp=smod(a->value, modulo);
      if (!is_zero(tmp))
	res.coord.push_back(monomial<gen>(tmp,a->index));
    }
  }

  polynome smod(const polynome & th, const gen & modulo) {  
    polynome res(th.dim,th);
    smod(th,modulo,res);
    return res;
  }

  // var is the variable number to extract, from 1 to p.dim
  void polynome2poly1(const polynome & pp,int var,vecteur & v){
    if (pp.dim==0){
      gensizeerr("polynome2poly1");
      v.clear();
      if (!pp.coord.empty())
	v.push_back(pp.coord.front().value);
    }
    if (var!=1){
      polynome p(pp);
      p.reorder(transposition(0,var-1,p.dim));
      polynome2poly1(p,1,v);
      return;
    }
    v.clear();
    int current_deg=pp.lexsorted_degree();
    v.reserve(current_deg+1);
    vector< monomial<gen> >::const_iterator it=pp.coord.begin(),itend=pp.coord.end();
    for (;it!=itend;--current_deg){
      if (it->index.front()==current_deg){
	if (pp.dim==1){
	  v.push_back(it->value);
	  ++it;
	}
	else
	  v.push_back(Tnextcoeff<gen>(it,itend));
      }
      else {
#if 0
	if (pp.dim==1)
	  v.push_back(0);
	else 
	  v.push_back(polynome(pp.dim-1));
#else
	  v.push_back(0);
#endif
      }
    }
    for (;current_deg>=0;--current_deg)
      v.push_back(zero);
  }

  vecteur polynome2poly1(const polynome & p,int var){
    vecteur v;
    polynome2poly1(p,var,v);
    return v;
  }

  // like polynome2poly1 for univariate p
  vecteur polynome12poly1(const polynome & p){
    if (p.dim>1)
      return polynome2poly1(p,1);
    int current_deg=p.lexsorted_degree();
    vecteur v;
    v.reserve(current_deg+1);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;--current_deg){
      if (it->index.front()==current_deg){
	v.push_back(it->value);
	++it;
      }
      else
	v.push_back(zero);
    }
    for (;current_deg>=0;--current_deg)
      v.push_back(zero);
    return v;
  }

  vecteur polynome2poly1(const polynome & p){
    if (p.dim>1)
      return polynome2poly1(p,1);
    vecteur v;
    int current_deg=p.lexsorted_degree();
    v.reserve(current_deg+1);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;--current_deg){
      if (it->index.front()==current_deg){
	v.push_back(it->value);
	++it;
      }
      else
	v.push_back(zero);
    }
    for (;current_deg>=0;--current_deg)
      v.push_back(zero);
    return v;
  }
  
  gen polynome2poly1(const gen & e,int var){
      if (e.type==_POLY)
          return polynome2poly1(*e._POLYptr,var);
      if (e.type!=_FRAC)
          return e;
      return fraction(polynome2poly1(e._FRACptr->num,var),polynome2poly1(e._FRACptr->den,var));
  }

  int inner_POLYdim(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    int dim=1;
    for (;it!=itend;++it){
      if (it->type==_POLY){
	dim=it->_POLYptr->dim+1;
	break;
      }
    }
    return dim;
  }

  gen untrunc(const gen & e,int degree,int dimension){
    if (e.type==_POLY)
      return e._POLYptr->untrunc(degree,dimension);
    if (e.type==_EXT)
      return algebraic_EXTension(untrunc(*e._EXTptr,degree,dimension),untrunc(*(e._EXTptr+1),degree,dimension));
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(untrunc(*it,degree,dimension));
      return res;
    }
    if (e.type==_FRAC)
      return fraction(untrunc(e._FRACptr->num,degree,dimension),untrunc(e._FRACptr->den,0,dimension));
    return tensor<gen>(monomial<gen>(e,degree,1,dimension));
  }

  gen vecteur2polynome(const vecteur & v,int dimension){
    const_iterateur it=v.begin(),itend=v.end();
    gen e;
    for (int d=int(itend-it)-1;it!=itend;++it,--d){
      if (!is_zero(*it))
	e = e+untrunc(*it,d,dimension);
    }
    return e;
  }

  polynome poly12polynome(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    polynome p(1);
    for (int d=int(itend-it)-1;it!=itend;++it,--d){
      if (!is_zero(*it))
	p.coord.push_back(monomial<gen>(*it,d,1,1));
    }
    return p;
  }

  // WARNING: var begins at 1 and ends at dimension
  void poly12polynome(const vecteur & v, int var,polynome & p,int dimension){
    if (dimension)
      p.dim=dimension;
    else 
      p.dim=inner_POLYdim(v);
    p.coord.clear();
    const_iterateur it=v.begin(),itend=v.end();
    for (int d=int(itend-it)-1;it!=itend;++it,--d){
      if (is_zero(*it))
	continue;
      if (it->type!=_POLY || (it->_POLYptr->dim+1)!=p.dim)
	p.coord.push_back(monomial<gen>(*it,d,1,p.dim));
      else {
	vector< monomial<gen> >::const_iterator p_it=it->_POLYptr->coord.begin(),p_itend=it->_POLYptr->coord.end();
	for (;p_it!=p_itend;++p_it)
	  p.coord.push_back(p_it->untrunc(d,p.dim));
      }
    }
    if (var!=1){
      p.reorder(transposition(0,var-1,p.dim));
    }
  }

  polynome poly1_2_polynome(const vecteur & v, int dimension){
    polynome p(dimension);
    const_iterateur it=v.begin(),itend=v.end();
    for (int d=int(itend-it)-1;it!=itend;++it,--d){
      if (is_zero(*it))
	continue;
      p.coord.push_back(monomial<gen>(*it,d,1,p.dim));
    }
    return p;
  }

  polynome poly12polynome(const vecteur & v,int var,int dimension){
    polynome p(0);
    poly12polynome(v,var,p,dimension);
    return p;
  }

  // assuming pmod and qmod are prime together, find r such that
  // r = p mod pmod  and r = q mod qmod
  // hence r = p + A*pmod = q + B*qmod
  // or A*pmod -B*qmod = q - p
  // assuming u*pmod+v*pmod=d we get
  // A=u*(q-p)/d
  polynome ichinrem(const polynome &p,const polynome & q,const gen & pmod,const gen & qmod){
    gen u,v,d,tmp,pqmod(pmod*qmod);
    egcd(pmod,qmod,u,v,d);
    // COUT << u << "*" << pmod << "+" << v << "*" << qmod << "=" << d << " " << u*pmod+v*qmod << endl;
    vector< monomial<gen> >::const_iterator a = p.coord.begin();
    vector< monomial<gen> >::const_iterator a_end = p.coord.end();
    vector< monomial<gen> >::const_iterator b = q.coord.begin();
    vector< monomial<gen> >::const_iterator b_end = q.coord.end();
    polynome res(p.dim);
    res.coord.reserve(a_end - a );
    for (;(a!=a_end)&&(b!=b_end);){
      if (a->index != b->index){
	if (a->index>=b->index){
	  tmp=a->value-rdiv(u*a->value,d,context0);
	  res.coord.push_back(monomial<gen>(smod(tmp,pqmod),a->index));
	  ++a;
	}
	else {
	  tmp=rdiv(u*b->value,d,context0);
	  res.coord.push_back(monomial<gen>(smod(tmp,pqmod),b->index));
	  ++b;
	}
      }
      else {
	tmp=a->value+rdiv(u*(b->value-a->value),d,context0) *pmod ;
	// COUT << a->value << " " << b->value << "->" << tmp << " " << pqmod << endl;
	res.coord.push_back(monomial<gen>(smod(tmp,pqmod),b->index));
	++b;
	++a;
      }
    }
    for (;a!=a_end;++a)
      res.coord.push_back(monomial<gen>(smod(a->value-rdiv(u*(a->value),d,context0),pqmod),a->index));
    for (;b!=b_end;++b)
      res.coord.push_back(monomial<gen>(smod(rdiv(u*b->value,d,context0),pqmod),b->index));
    return res;
  }

  bool divrem (const polynome & th, const polynome & other, polynome & quo, polynome & rem, bool allowrational ){
    return th.TDivRem(other,quo,rem,allowrational);
  }

  bool exactquotient(const polynome & a,const polynome & b,polynome & quo,bool allowrational){
    CLOCK_T beg=CLOCK(),delta;
    if (debug_infolevel>1)
      CERR << beg*1e-6 << " exactquo begin" << endl;
    bool res= a.Texactquotient(b,quo,allowrational);
    delta=CLOCK()-beg;
    if (delta && debug_infolevel>1) // a.dim>=inspectdim
      CERR << "exactquo end " << delta*1e-6 << " " << res << endl;
    return res;
  }

  static bool divremmod2 (const polynome & th,const polynome & other, const gen & modulo,polynome & quo, polynome & rem) {
    int asize=int(th.coord.size());
    if (!asize){
      quo=th;
      rem=th; 
      return true;
    }
    int bsize=int(other.coord.size());
    if (bsize==0){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("gausspol.cc/divremmod2"));
#endif
      return false;
    }
    index_m a_max = th.coord.front().index;
    index_m b_max = other.coord.front().index;
    quo.coord.clear();
    quo.dim=th.dim;
    rem.dim=th.dim;
    if ( (bsize==1) && (b_max==b_max*0) ){
      rem.coord.clear();
      gen b=other.coord.front().value;
      if (b==gen(1))
	quo = th ;
      else {
	b=invmod(b,modulo);
	vector< monomial<gen> >::const_iterator itend=th.coord.end();
	for (vector< monomial<gen> >::const_iterator it=th.coord.begin();it!=itend;++it)
	  quo.coord.push_back(monomial<gen>( smod(it->value*b,modulo),it->index));
      }
    return true;
    }
    rem=th;
    if ( ! (a_max>=b_max) ){
      // test that the first power of a_max is < to that of b_max
      return (a_max.front()<b_max.front());
    }
    // bool mult=is_cinteger(other.coord.front().value);
    gen b=invmod(other.coord.front().value,modulo);
    while (a_max >= b_max){
      gen q=smod(rem.coord.front().value*b, modulo);
      quo.coord.push_back(monomial<gen>(q,a_max-b_max));
      polynome temp=other.shift(a_max-b_max,q);
      rem = smod(rem-temp, modulo);
      if (rem.coord.size())
	a_max=rem.coord.front().index;
      else
	break;
    }
    return(true);
  }

  bool divremmod (const polynome & th,const polynome & other, const gen & modulo,polynome & quo, polynome & r) {
    quo.coord.clear();
    quo.dim=th.dim;
    r.dim=th.dim;
    if ( (th.dim<=1) || (th.coord.empty()) )
      return divremmod2(th,other,modulo,quo,r);
    int os=int(other.coord.size());
    if (!os){
      r=th;
      return true;
    }
    if (os==1){
      // Check for a division by 1
      if (is_one(other.coord.front().value) && other.coord.front().index.is_zero()){
	quo=th;
	r.coord.clear();
	return true;
      }
      // IMPROVE Invert other.coord.front() and shift/multiply
      
    }
    std::vector< monomial<gen> >::const_iterator it=other.coord.begin();
    int bdeg=it->index.front(),rdeg=th.lexsorted_degree(),ddeg=rdeg-bdeg;
#ifndef NO_TEMPLATE_MULTGCD
    // FIXME hashdivrem may fail if not divisible if false is commented below
    // search new quotient term in threaded.h if heap multiplication is used
    if (//false && 
	ddeg>2 && os>10 
	){
      index_t d1=th.degree(),d2=other.degree(),d3=other.coord.front().index.iref(),d(th.dim);
      // i-th degrees of th / other in quotient and remainder
      // are <= i-th degree of th + ddeg*(i-th degree of other - i-th degree of lcoeff of other) 
      double ans=1;
      for (int i=0;i<th.dim;++i){
	d[i]=d1[i]+(ddeg+1)*(d2[i]-d3[i])+1;
	int j=1;
	// round to 2^
	for (;;j++){
	  if (!(d[i] >>= 1))
	    break;
	}
	d[i] = 1 << j;
	ans = ans*unsigned(d[i]);
	if (ans/RAND_MAX>RAND_MAX)
	  break;
      }
      if (ans<RAND_MAX){
	if (modulo.type==_INT_ && modulo.val<46340 && modulo.val>0){ 
	  // convert everything to integers
	  vector< T_unsigned<int,unsigned> > p1,p2,quot,remain;
	  vector<unsigned> vars(th.dim);
	  vars[th.dim-1]=1;
	  for (int i=th.dim-2;i>=0;--i){
	    vars[i]=d[i+1]*vars[i+1];
	  }
	  if (debug_infolevel>1)
	    CERR << "divrem convert " << CLOCK() << endl;
	  if (convert(th,d,p1,modulo.val) && convert(other,d,p2,modulo.val)){
	    if (debug_infolevel>1)
	      CERR << "hashdivrem begin " << CLOCK() << endl;
	    if (hashdivrem<int,unsigned>(p1,p2,quot,remain,vars,modulo.val,0.0,false)==1){
	      if (debug_infolevel>1)
		CERR << "hashdivrem end " << CLOCK() << endl;
	      convert(quot,d,quo);
	      convert(remain,d,r);
	      return true;
	    }
	    else
	      return false;
	  }
	} // end modulo.type==_INT
      } // end ans < RAND_MAX
      if (ans/RAND_MAX<RAND_MAX){
	if (modulo.type==_INT_ && modulo.val<46340 && modulo.val>0){ 
	  // convert everything to integers
	  vector< T_unsigned<int,ulonglong> > p1,p2,quot,remain;
	  vector<ulonglong> vars(th.dim);
	  vars[th.dim-1]=1;
	  for (int i=th.dim-2;i>=0;--i){
	    vars[i]=d[i+1]*vars[i+1];
	  }
	  if (convert(th,d,p1,modulo.val) && convert(other,d,p2,modulo.val)){
	    if (hashdivrem<int,ulonglong>(p1,p2,quot,remain,vars,modulo.val,0.0,false)==1){
	      convert(quot,d,quo);
	      convert(remain,d,r);
	      return true;
	    }
	    else
	      return false;
	  }
	} // end modulo.type==_INT
      } // end ans/RAND_MAX < RAND_MAX
    } // end if ddeg>2 && os>10 
#endif //NO_TEMPLATE_MULTGCD
    tensor<gen> b0(Tnextcoeff<gen>(it,other.coord.end()));
    r=th;
    tensor<gen> q(b0.dim),q_other(th.dim);
    while ( (rdeg=r.lexsorted_degree()) >=bdeg){
      it=r.coord.begin();
      tensor<gen> a0(Tnextcoeff<gen>(it,r.coord.end())),tmp(a0.dim);
      if (!divremmod(a0,b0,modulo,q,tmp) || !tmp.coord.empty())
	return false;
      q=q.untrunc1(rdeg-bdeg);
      quo=quo+q;
      mulpoly(q,other,q_other,modulo);
      r=smod(r-q_other,modulo);
      if (r.coord.empty())
	return true;
    }
    return true;
  }


  polynome pow(const polynome & p,const gen & n){
    polynome res(p.dim);
    if (!n.is_integer()){
#ifdef NO_STDEXCEPT
      res.coord.push_back(monomial<gen>(gensizeerr(gettext("gausspol.cc/pow")),p.dim));
      return res;
#else
      setsizeerr(gettext("gausspol.cc/pow"));
#endif
    }
    int i=n.to_int();
    if (!powpoly(p,i,res)){
#ifdef NO_STDEXCEPT
      res.coord.clear();
      res.coord.push_back(monomial<gen>(gensizeerr(gettext("gausspol.cc/pow")),p.dim));
#else
      setsizeerr(gettext("gausspol.cc/pow"));
#endif
    }
    return res;
  }

  polynome pow(const polynome & p, int  n){
    polynome res(p.dim);
    powpoly(p,n,res);
    return res;
  }

  static polynome powmod1(const polynome &p,int n,const gen & modulo){
    switch (n) {
    case 0: 
      return polynome(gen(1),p.dim);
    case 1: 
      return p; 
    default: 
      polynome temp(powmod(p,n/2,modulo));
      if (n%2)
	return (temp * temp * p) % modulo;
      else
	return (temp*temp) % modulo;
    }
  }

  polynome powmod(const polynome &p,int n,const gen & modulo){
    if (p.dim<2)
      return powmod1(p,n,modulo);
    polynome res(gen(1),p.dim);
    for (int i=0;i<n;i++)
      res=(res*p) % modulo;
    return res;
  }

  void exact_inplace(polynome & P){
    vector< monomial<gen> >::iterator it=P.coord.begin(),itend=P.coord.end();
    for (;it!=itend;++it)
      it->value=exact(it->value,context0);
  }

  void evalf_inplace(polynome & P){
    vector< monomial<gen> >::iterator it=P.coord.begin(),itend=P.coord.end();
    for (;it!=itend;++it)
      it->value=evalf(it->value,1,context0);
  }

  // Ducos: optimizations of the subresultant algorithm
  
  // n=d-1-e, d=degree(Sd), e=degree(Sd1), Se=(lc(Sd1)^n*Sd1)/lc(Sd)^n
  void ducos_e(const polynome & Sd,const polynome & sd,const polynome & Sd1,polynome & Se){
    int n=Sd.lexsorted_degree()-Sd1.lexsorted_degree()-1;
    if (!n){
      Se=Sd1;
      return;
    }
    if (n==1){
      Se=(Tfirstcoeff(Sd1)*Sd1)/sd;
      return;
    }
    // n>=2
    polynome sd1(Tfirstcoeff(Sd1)),s((sd1*sd1)/sd);
    for (int j=2;j<n;++j){
      s=(s*sd1)/sd;
    }
    Se=(s*Sd1)/sd;
  }

  // compute S_{e-1}
  void ducos_e1(const polynome & A,const polynome & Sd1,const polynome & Se,const polynome & sd,polynome & res){
    int d=A.lexsorted_degree(),e=Sd1.lexsorted_degree(),dim=A.dim;
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << "cducos_e1 begin d=" << d << endl;
    polynome cd1(Tfirstcoeff(Sd1)),se(Tfirstcoeff(Se));
    index_t sh(dim);
#if 0
    vector<polynome> Hv;
    for (int j=0;j<e;++j){
      sh[0]=j;
      Hv.push_back(se.shift(sh));
    }
#else
    vector<polynome> Hv(e);
    Hv.reserve(d);
#endif
    sh[0]=e;
    Hv.push_back(se.shift(sh)-Se);
    for (int j=e+1;j<d;++j){
      polynome XHj1(Hv.back());
      sh[0]=1; XHj1=XHj1.shift(sh); // X*H_{j-1}
      polynome piXHj1(A.dim);
      if (XHj1.lexsorted_degree()-e>=0)
	piXHj1=XHj1.Tcoeffs() [XHj1.lexsorted_degree()-e].untrunc1();
      XHj1=XHj1-(piXHj1*Sd1)/cd1;
      Hv.push_back(XHj1);
    }
    polynome D(A.dim),DA(A.dim); // sum_{j<d} pi_j(A)*H_j/lc(A)
#if 1
    vector<polynome> Av(A.Tcoeffs()); 
#else
    vector<polynome> Av; A.Tcoeffs(Av);
#endif
    // split next loop in 2 parts, because Hv indexes lower than e are straightforward
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 D begin" << endl;
    for (int j=e-1;j>=0;--j){
      sh[0]=j;
#if 0
      D.append((Av[Av.size()-1-j]*se.trunc1()).untrunc1().shift(sh));
#else
      D.append(Av[Av.size()-1-j].untrunc1()*se.shift(sh));
#endif
    }
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 D j=e " << e << "<" << d << endl;
    for (int j=e;j<d;++j){
      D = D + Av[Av.size()-1-j].untrunc1()*Hv[j];
    }
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 D end, start division" << endl;
#if 1
    D = D/Av.front().untrunc1();
#else
    if (!is_one(Av.front())){
      polynome quo(dim),rem(dim);
      divrem1(D,Av.front().untrunc1(),quo,rem,3,false);
      D.coord.swap(quo.coord);
    }
#endif
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 D ready" << endl;
    polynome Hd1(Hv.back());
    sh[0]=1;
    Hd1=Hd1.shift(sh); // X*Hd1
#if 1
    res=(cd1*(Hd1+D)-(Hd1.coeff(e).untrunc1()*Sd1));
#else
    res=(cd1*(Hd1+D)-(Hd1.Tcoeffs()[Hd1.lexsorted_degree()-1-e]).untrunc1()*Sd1);
#endif
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 D final division" << endl;
#if 1
    res=res/sd;
#else
    if (!is_one(sd)){
      polynome quo(dim),rem(dim);
      divrem1(res,sd,quo,rem,3,false);
      res.coord.swap(quo.coord);
    }
#endif
    if (debug_infolevel>1)
      CERR << CLOCK()*1e-6 << " ducos_e1 end" << endl;
    if ( (d-e+1)%2)
      res=-res;
  }

  void subresultant(const polynome & P,const polynome & Q,polynome & C,bool ducos){
    int dim=P.dim;
    if (dim==1){
      gen c;
      vecteur p,q;
      polynome2poly1(P,1,p);
      polynome2poly1(Q,1,q);
      subresultant(p,q,c);
      C=polynome(monomial<gen>(c,1));
      return;
    }
    int a=P.partial_degree(2);
    int b=Q.partial_degree(2);
    int m=P.lexsorted_degree();
    int n=Q.lexsorted_degree();
    // first estimate n*(a-m)+m*b 
    int d1=n*(a-m)+m*b;
    //gen Pg=a*gen(m)*comb(m+dim-2,dim-2);
    //gen Qg=b*gen(n)*comb(n+dim-2,dim-2);
    if (//1 ||
	!ducos && giacmin(m,n)>2 && dim<4 && P.coord.size()>=m && Q.coord.size() >= n
	// && Pg.type==_INT_ && P.coord.size()>=Pg.val/2 && Qg.type==_INT_ && Q.coord.size()>=Qg.val/2
	){ 
      double iclock=CLOCK()*1e-6;
      // for dense inputs, interpolate
      polynome pp0(P);
      pp0.reorder(transposition(0,1,dim));
      polynome qp0(Q);
      qp0.reorder(transposition(0,1,dim));
      // second estimate 
      a=pp0.lexsorted_degree();
      b=qp0.lexsorted_degree();
      // a*n+b*m
      int d2=a*n+b*m;
      int d=giacmin(d1,d2);
      // interpolation
      vecteur vp,vq,vp0,vq0,X(d+1),Y(d+1);
      polynome2poly1(pp0,1,vp);
      pp0=firstcoeff(P).trunc1();
      polynome2poly1(pp0,1,vp0);
      polynome2poly1(qp0,1,vq);
      qp0=firstcoeff(Q).trunc1();
      polynome2poly1(qp0,1,vq0);
      int j=-d/2;
      for (int i=0;i<=d;++i,++j){
	if (!debug_infolevel){
	  double cclock=CLOCK()*1e-6;
	  if (cclock-iclock>15)
	    debug_infolevel=1;
	}
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " interp horner, loop index " << i << endl;
	for (;;++j){
	  // find evaluation preserving degree in x
	  if (0 && j==0)
	    CERR << "j" << endl;
	  gen hp=horner(vp0,j);
	  gen hq=horner(vq0,j);
	  if (!is_zero(hp) && !is_zero(hq))
	    break;
	}
	X[i]=j;
	gen gp=horner(vp,j);
	gen gq=horner(vq,j);
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " interp resultant evaled at " << j << ", " << 100*double(i)/(d+1) << "% done" << endl;
	if (gp.type==_POLY && gq.type==_POLY){
	  Y[i]=resultant(*gp._POLYptr,*gq._POLYptr);
	  continue;
	}
	if (gp.type==_POLY){
	  Y[i]=pow(gq,gp._POLYptr->lexsorted_degree(),context0);
	  continue;
	}
	if (gq.type==_POLY){
	  Y[i]=pow(gp,gq._POLYptr->lexsorted_degree(),context0);
	  continue;		
	}
	Y[i]=1;
      }
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " interp dd " << endl;
      vecteur R=divided_differences(X,Y);
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " interp build " << endl;
      modpoly resp(1,R[d]),tmp; // cst in y
      for (int i=d-1;i>=0;--i){
	operator_times(resp,makevecteur(1,-X[i]),0,tmp);
	if (tmp.empty())
	  tmp=vecteur(1,R[i]);
	else
	  tmp.back() += R[i];
	tmp.swap(resp);
      }
      poly12polynome(resp,2,C,dim);
      return;
    }
    int d=P.lexsorted_degree(),e=Q.lexsorted_degree();
    if (d<e){
      subresultant(Q,P,C,true);
      // adjust sign
      if ((d*e)%2) C=-C;
      return;
    }
    if (e==0){
      if (Q.coord.empty()){
	C=Q;
	return;
      }
      C=pow(Q,d);
      return;
    }
    polynome sd(pow(Tfirstcoeff(Q),d-e)), A(Q),B(P.dim),quo(P.dim),tmp(P.dim);
    P.TPseudoDivRem(-Q,quo,B,tmp);
    for (unsigned step=0;;++step){
      d=A.lexsorted_degree(),e=B.lexsorted_degree();
      if (B.coord.empty()){
	C=B;
	return ;
      }
      int delta=d-e;
      if (delta>1){
	polynome sd(Tfirstcoeff(A));
	if (step==0)
	  sd=pow(sd,P.lexsorted_degree()-Q.lexsorted_degree());
	ducos_e(A,sd,B,C);
      }
      else
	C=B;
      if (e==0){
	// adjust sign: already done by doing pseudodivrem(-Q,...)
	//if ((P.lexsorted_degree()*Q.lexsorted_degree())%2) C=-C;
	return;
      }
      ducos_e1(A,B,C,sd,B);
      A.coord.swap(C.coord); // A=C;
      sd=Tfirstcoeff(A);
    }
  }

  void subresultant(const polynome & P,const polynome & Q,gen & c,polynome & C,bool ducos){
    polynome p(P),q(Q);
    gen pz=ppz(p),qz=ppz(q);
    gen coefft,coeffqt;
    int pt=coefftype(p,coefft),qt=coefftype(q,coeffqt);
    polynome g;
    if (pt==0 && qt==0){
      // try gcd only if it is fast (integer coefficients for example)
      g=gcd(p,q);
      if (g.lexsorted_degree()){
	C.coord.clear();
	return;
      }
    }
    else {
      g=Tlgcd(p);
      Tlgcd(q,g);
    }
    if (!is_one(g)){
      p=p/g;
      q=q/g;
    }
    subresultant(p,q,C,ducos);
    c=pow(pz,q.lexsorted_degree())*pow(qz,p.lexsorted_degree());
    if (!is_one(g)){
      int expo=p.lexsorted_degree()+q.lexsorted_degree();
      for (int i=0;i<expo;++i)
	C=g*C;
    }
  }

  bool resultant_sylvester(const polynome &p,const polynome &q,vecteur &pv,vecteur &qv,matrice & S,gen & determinant){
    polynome2poly1(p,1,pv);
    polynome2poly1(q,1,qv);
    sylvester(pv,qv,S);
    vecteur pivots;
    matrice mres;
    int s=int(S.size());
    if (!mrref(S,mres,pivots,determinant,0,s,0,s,
	       /* fullreduction */0,0,false/* no conversion*/,1/* guess algorithm */,1/* determinant */,
	       context0))
      return false;
    return true;
  }

  bool resultant_sylvester(const polynome &p,const polynome &q,matrice & S,polynome & res){
    vecteur pv,qv;
    gen determinant;
    if (!resultant_sylvester(p,q,pv,qv,S,determinant))
      return false;
    if (determinant.type==_POLY)
      res=determinant._POLYptr->untrunc1();
    else
      res=polynome(monomial<gen>(determinant,p.dim));
    return true;
  }

  polynome resultant(const polynome & p,const polynome & q){
    // polynomial subresultant does not work if p and q have approx coeff
    if (p.coord.empty())
      return p;
    if (q.coord.empty())
      return q;
    bool approx=has_num_coeff(p) || has_num_coeff(q);
    if (p.dim==1){
      if (approx  
	  // || (p.lexsorted_degree()>=GIAC_PADIC/2 && q.lexsorted_degree()>=GIAC_PADIC/2)
	  ){
	matrice S; polynome res(p.dim);
	if (resultant_sylvester(p,q,S,res))
	  return res;
      }
    }
    if (approx){
      polynome P(p),Q(q);
      exact_inplace(P); exact_inplace(Q);
      polynome res=Tresultant<gen>(P,Q);
      evalf_inplace(res);
      return res;
    }
    double pq=double(p.coord.size())*q.coord.size();
    unsigned dim=p.dim;
#if 0 // def HAVE_LIBPARI // : PARI is faster but has problems with some large inputs
    // we must keep the same variable ordering than in PARI
    if (dim>=2 && dim<=4 && pq>256 && p.coord.size()>4 && q.coord.size()>4){
      gen coefft,coeffqt;
      int pt=coefftype(p,coefft),qt=coefftype(q,coeffqt);
      if (pt==0 && qt==0){
	// PARI call
	vecteur lv;
	if (dim==2) lv=makevecteur(x__IDNT_e,y__IDNT_e);
	if (dim==3) lv=makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e);
	if (dim==4) lv=makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e,t__IDNT_e);
	gen P=r2sym(p,lv,context0),Q=r2sym(q,lv,context0),res;
	if (pari_polresultant(P,Q,lv,res,context0)){
	  res=sym2r(res,lv,context0);
	  if (res.type==_POLY){
#if 0
	    polynome res1; subresultant(p,q,res1,true);
	    if (res!=res1){
	      cerr << res._POLYptr->coord.size() << endl;
	      return res1;
	    }
#endif
	    return *res._POLYptr;
	  }
	}
      }
    }
#endif // HAVE_LIBPARI
    polynome R(p.dim); gen r;
    subresultant(p,q,r,R,false);
    return r*R;
#if 0
    polynome R1(Tresultant<gen>(p,q));
    // COUT << R << "," << R1 << endl;
    if (R!=R1)
      COUT << "error " << endl;
    return R1;
#endif
  }

  polynome lgcd(const polynome & p){
    return Tlgcd<gen>(p);
  }

  gen ppz(polynome & p,bool divide){
#ifdef USE_GMP_REPLACEMENTS
    return Tppz(p,divide);
#else
    vector< monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend)
      return 1;
    gen res=(itend-1)->value;
    for (it=p.coord.begin();it!=itend-1;++it){
      res=gcd(res,it->value,context0);
      if (is_one(res))
	return 1;
    }
    if (!divide)
      return res;
    if (res.type==_INT_ && res.val>0){
      for (it=p.coord.begin();it!=itend;++it){
	if (it->value.type!=_ZINT || it->value.ref_count()>1)
	  it->value=it->value/res; 
	else
	  mpz_divexact_ui(*it->value._ZINTptr,*it->value._ZINTptr,res.val);
      }
      return res;
    }
    if (res.type==_ZINT){
      for (it=p.coord.begin();it!=itend;++it){
	if (it->value.type!=_ZINT || it->value.ref_count()>1)
	  it->value=it->value/res; 
	else
	  mpz_divexact(*it->value._ZINTptr,*it->value._ZINTptr,*res._ZINTptr);
      }
      return res;
    }
    for (it=p.coord.begin();it!=itend;++it){
      it->value=it->value/res; 
    }
    return res;
#endif
  }

  // Find the content of p with respect to the 1st variable
  // p=p(x1,...,xn)=poly in x1 with coeff depending on x2,..,xn
  // content wrt x1 depends on x2,...,xn
  void lgcdmod(const polynome & p,const gen & modulo,polynome & pgcd){
    if (!p.dim){
      pgcd=p;
      return ;
    } 
    pgcd=pgcd.trunc1();
    vector< monomial<gen> >::const_iterator it=p.coord.begin();
    vector< monomial<gen> >::const_iterator itend=p.coord.end();
    // vector< monomial<gen> >::const_iterator itbegin=it;
    for (;it!=itend;){
      if (is_one(pgcd))
	break;
      pgcd=gcdmod(pgcd,Tnextcoeff<gen>(it,itend),modulo);
    }
    if (pgcd.coord.empty()){
      index_m i;
      for (int j=0;j<p.dim;j++)
	i.push_back(0);
      pgcd.coord.push_back(monomial<gen>(gen(1),i));
    }
    else
      pgcd=pgcd.untrunc1();
  }

  // Split a multivariate poly X_1...X_n as multivar X_dim+1...X_n
  // with coeff multivar poly of X_1..X_dim
  polynome split(const polynome & p,int inner_dim){
    int outer_dim=p.dim-inner_dim;
    polynome cur_inner(inner_dim);
    polynome res(outer_dim);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (; it!=itend;++it){
      index_t outer_index(it->index.begin()+inner_dim,it->index.end());
      index_t inner_index(it->index.begin(),it->index.begin()+inner_dim);
      cur_inner=polynome(monomial<gen>(it->value,inner_index));
      res=res+polynome(monomial<gen>(cur_inner,outer_index));
    }
    return res;
  }

  /*
#ifdef HASH_MAP_NAMESPACE
  class hash_function_index_t {
  public:
    inline size_t operator () (const index_t & a) const { 
      size_t r=0;
      index_t::const_iterator ita=a.begin(),itaend=a.end();
      for (;ita!=itaend;++ita){
	r <<= 4;
	r += (*ita) & 0xf;
      }
      return r;
    }
    hash_function_index_t() {};
  };

  typedef HASH_MAP_NAMESPACE::hash_map<index_t,polynome,hash_function_index_t> map_index_t_polynome;
#else
  typedef std::map<index_t,polynome> map_index_t_polynome;
#endif
  */

  typedef std::map<index_t,polynome> map_index_t_polynome;

  // return true if content=1 is detected 
  static bool split(const polynome & p,int inner_dim,map_index_t_polynome & res){
    // int outer_dim=p.dim-inner_dim;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (; it!=itend;++it){
      index_t cur_index= it->index.iref();
      index_t outer_index(it->index.begin()+inner_dim,it->index.end());
      index_t inner_index(it->index.begin(),it->index.begin()+inner_dim);
      map_index_t_polynome::iterator jt=res.find(outer_index),jtend=res.end();
      if (jt==jtend){
	if (is_zero(inner_index))
	  return true;
	res[outer_index]=polynome(monomial<gen>(it->value,inner_index));
      }
      else
	jt->second.coord.push_back(monomial<gen>(it->value,inner_index));
    }
    return false;
  }

  gen lcoeffn(const polynome & p){
    int dim=p.dim;
    polynome res(dim);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend)
      return 0;
    index_t i= it->index.iref(); 
    for (;it!=itend;++it){
      const index_t & j= it->index.iref(); 
      i[dim-1]=j[dim-1];
      if (i!=j)
	break;
      res.coord.push_back(*it);
    }
    return res;
  }

  gen lcoeff1(const polynome & p){
    if (p.coord.empty())
      return zero;
    int inner_dim=1;
    // int outer_dim=p.dim-inner_dim;
    polynome cur_inner(inner_dim);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t::const_iterator jt0 = it->index.begin(),jtend=it->index.end(),jt,kt0,kt;
    for (; it!=itend;++it){
      kt0 = it->index.begin();
      for (jt=jt0+inner_dim,kt=kt0+inner_dim;jt!=jtend;++kt,++jt){
	if (*kt<*jt)
	  break;
	if (*kt>*jt){
	  jt0=kt0;
	  jtend=kt0+p.dim;
	  cur_inner.coord.clear();
	  jt=jtend;
	  break;
	}
      }
      if (jt==jtend)
	cur_inner.coord.push_back(monomial<gen>(it->value,index_t(kt0,kt0+inner_dim)));
    }
    return cur_inner;
  }

  polynome content1mod(const polynome & p,const gen & modulo,bool setdim){
    if (p.coord.empty()){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("content1mod"));
#endif
      return polynome(monomial<gen>(1,p.dim));
    }
    if (p.coord.size()==1){
      int n=p.coord.front().index.front();
      polynome c(monomial<gen>(p.coord.front().value,index_t(1,n)));
      if (setdim)
	change_dim(c,p.dim);
      return c;
    }
    // New code
    map_index_t_polynome m;
    polynome c(1);
    if (!split(p,1,m)){
      int c0=RAND_MAX,i0;
      map_index_t_polynome::iterator it=m.begin(),itend=m.end();
      if (m.size()==1)
	c=it->second;
      else {
	for (;c0 && it!=itend;++it){
	  if (!it->second.coord.empty() && (i0=it->second.coord.front().index.front())<c0){
	    c=it->second;
	    c0=i0;
	  }
	}
	it=m.begin();
	for (;it!=itend;++it){
	  if (!c.coord.empty() &&c.coord.front().index.front()==0 ){
	    c=polynome(plus_one,1);
	    break;
	  }
	  c=gcdmod(c,it->second,modulo);
	}
	/* Old code
	   polynome lp(split(p,1)),c(1);
	   vector< monomial<gen> >::const_iterator it=lp.coord.begin(),itend=lp.coord.end();
	   for (;it!=itend;++it){
	   if (it->value.type==_POLY)
	   c=gcdmod(c,*it->value._POLYptr,modulo);
	   else {
	   c=polynome(plus_one,1); // was c=polynome(plus_one,p.dim-1);
	   break;
	   }
	   }
	*/
      } // end if itend==it+1
    } // end if (!split()) : i.e. if the content is not trivially 1
    else
      c=polynome(plus_one,1);
    if (setdim)
      change_dim(c,p.dim);
    return c;
  }
  
  polynome pp1mod(const polynome & p,const gen & modulo){
    polynome q(p.dim),r(p.dim);
    polynome tmp(content1mod(p,modulo));
    // CERR << "pp1mod " << tmp << endl;
    divremmod(p,tmp,modulo,q,r);
    return q;
  }

  // Find non zeros coeffs of p
  int find_nonzero(const polynome & p,index_t & res){
    res.clear();
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend)
      return 0;
    int old_deg=it->index.front(),cur_deg=0;
    int nzeros=0;
    res.push_back(1);
    for (;it!=itend;++it){
      cur_deg=it->index.front();
      if (cur_deg!=old_deg){
	nzeros += old_deg - cur_deg -1 ;
	for (int i=old_deg-cur_deg;i>1;--i)
	  res.push_back(0);
	res.push_back(1);
	old_deg=cur_deg;
      }
    }
    if (cur_deg){
      nzeros += cur_deg;
      for (int i=cur_deg;i>0;--i)
	res.push_back(0);
    }
    return nzeros;
  }

  static bool degree2unsigned(index_t & deg,unsigned & u){
    u=1;
    index_t::iterator it=deg.begin(),itend=deg.end();
    for (;it!=itend;++it){
      ++(*it);
      u = u*unsigned(*it);
      if (u>RAND_MAX)
	return false;
    }
    return true;
  }

  // p_orig and q_orig are primitive with respect to the main variable
  // p(x1,...,xn) q(x1,...,xn) viewed as p(x1) and q(x1) 
  // d must be the same
  static void mod_gcdmod(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & d,int gcddeg=0){
    if (p_orig.coord.empty() || is_one(q_orig)){
      d=q_orig;
      return;
    }
    if (q_orig.coord.empty() || is_one(p_orig)){
      d=p_orig;
      return;
    }
    if (debug_infolevel)
      CERR << "gcdmod content dim " << d.dim << " " << CLOCK() << endl;
    polynome p(p_orig.dim),q(q_orig.dim),r;
    vector<int_unsigned> pint,qint;
    index_t pintd=p_orig.degree(),qintd=q_orig.degree();
    unsigned pu,qu;
    // Make p and q primitive with respect to x2,...,xn
    // i.e. the coeff of p and q which are polynomials in x1
    // are relative prime 
    // r is the gcd of the content in this sense
    bool docontent1mod=true;
    // bug there, if false is removed lwN and lwN1 do not work
    if (false &&
	modulo.type==_INT_ && degree2unsigned(pintd,pu) && degree2unsigned(qintd,qu)){
      convert(p_orig,pintd,pint,modulo.val); 
      convert(q_orig,qintd,qint,modulo.val);
      if (is_content_trivially_1(pint,pu/pintd.front()) && is_content_trivially_1(qint,qu/qintd.front()))
	docontent1mod=false;
    }
    if (docontent1mod) {
      polynome pc(content1mod(p_orig,modulo)),qc(content1mod(q_orig,modulo));
      divremmod(p_orig,pc,modulo,p,r);
      divremmod(q_orig,qc,modulo,q,r);
      // CERR << "content end " << CLOCK() << endl;
      change_dim(pc,1); change_dim(qc,1);
      r=gcdmod(pc,qc,modulo);
      change_dim(r,p.dim);
    }
    else {
      p=p_orig; 
      q=q_orig;
      r=polynome(plus_one,p.dim);
    }
    // Find degree of gcd with respect to x1, more precisely gcddeg>=degree/x1
    // and compute data for the sparse modular algorithm
    index_t vzero; // coeff of vzero correspond to zero or non zero
    int nzero=1; // Number of zero coeffs
    vecteur alphav,gcdv; // Corresponding values of alpha and gcd at alpha
    if (!gcddeg){
      vecteur b(p.dim-1);
      for (int essai=0;essai<2;++essai){
	if (essai)
	  b=vranm(p.dim-1,0,0); // find another random point
	polynome Fb(1),Gb(1);
	// Fb and Gb are p and q where x2,...,xn are evaluated at b
	if (!find_good_eval(p,q,Fb,Gb,b,(debug_infolevel>=2),modulo))
	  break;
	polynome Db(gcdmod(Fb,Gb,modulo)); // 1-d gcd wrt x1
	int Dbdeg=Db.lexsorted_degree();
	if (!Dbdeg){
	  gcddeg=0;
	  break;
	}
	if (!gcddeg){ // 1st gcd test
	  gcddeg=Dbdeg;
	  nzero=find_nonzero(Db,vzero);
	}
	else { // 2nd try
	  if (Dbdeg<gcddeg){ // 1st try unlucky, restart 1st try
	    gcddeg=Dbdeg;
	    nzero=find_nonzero(Db,vzero);
	    --essai;
	  }
	  else {
	    if (Dbdeg>gcddeg) // 2nd try unlucky, restart 2nd try
	      --essai;
	    else { // Same gcd degree for 1st and 2nd try, keep this degree
	      index_t tmp;
	      nzero=find_nonzero(Db,tmp);
	      if (nzero){
		vzero = vzero | tmp;
		// Recompute nzero, it is the number of 0 coeff of vzero
		index_t::const_iterator it=vzero.begin(),itend=vzero.end();
		for (nzero=0;it!=itend;++it){
		  if (!*it) ++nzero;
		}
	      }
	    }
	  }
	}
      }
    }
    else {
      gcddeg -= r.lexsorted_degree() ;
      nzero = 0; // No info available
    }
    if (!gcddeg){
      d=r;
      return;
    }
    d=polynome(p.dim);
    polynome interp(plus_one,p.dim);
    // gcd of leading coefficients of p and q viewed as poly in X_2...X_n
    // with coeff in Z[X_1]
    if (debug_infolevel)
      CERR << "gcdmod lcoeff1 dim " << d.dim << " " << CLOCK() << endl;
    gen lp(lcoeff1(p)),lq(lcoeff1(q));
    polynome Delta(plus_one,p.dim);
    if ((lp.type==_POLY) && (lq.type==_POLY) )
      Delta=gcdmod(*lp._POLYptr,*lq._POLYptr,modulo);
    // we are now interpolating G=gcd(p,q)*a poly/x1
    // such that the leading coeff of G is Delta
    index_t pdeg(p.degree()),qdeg(q.degree()); 
    int spdeg=0,sqdeg=0;
    for (int i=1;i<p.dim;++i){
      spdeg += pdeg[i];
      sqdeg += qdeg[i];
    }
    index_t delta=index_min(index_t(pdeg.begin()+1,pdeg.end()),
			    index_t(qdeg.begin()+1,qdeg.end()));
    int e=0; // number of evaluations
    int alpha=0;
    if (debug_infolevel>1)
      CERR << "gcdmod find alpha dim " << d.dim << " " << CLOCK() << endl;
    for (;;++alpha){
      vecteur valpha;
      polynome palpha(p.dim-1),qalpha(q.dim-1);
      for (;alpha<modulo.val;++alpha){
	valpha=vecteur(1,alpha);
	gen tmp(peval(p,valpha,modulo,false,&pint));
	if (is_zero(tmp))
	  continue;
	if (tmp.type!=_POLY){ 
	  if (spdeg) 
	    continue;
	  // gcd may only depend on first var
	  d=r;
	  return;
	}
	palpha=smod(*tmp._POLYptr,modulo);
	tmp=peval(q,valpha,modulo,false,&qint);
	if (is_zero(tmp)) 
	  continue;
	if (tmp.type!=_POLY){ 
	  if (sqdeg) 
	    continue;
	  d=r;
	  return;
	}
	qalpha=smod(*tmp._POLYptr,modulo);
	if ( palpha.lexsorted_degree()==pdeg[1] &&
	     qalpha.lexsorted_degree()==qdeg[1] )
	  break;
      }
      // palpha and qalpha are p and q evaluated at x1=alpha
      if (debug_infolevel>1)
	CERR << "gcdmod eval " << alpha << " dim " << d.dim << " " << CLOCK() << endl;
      if (alpha==modulo){
#ifndef NO_STDEXCEPT
	setsizeerr(gettext("Modgcd: no suitable evaluation point"));
#endif
	return ;
      }
      polynome g(gcdmod(palpha,qalpha,modulo));
      index_t gdeg(g.degree());
      // int gcd_plus_delta_deg=gcddeg+Delta.lexsorted_degree();
      if (gdeg==delta){
	// Try spmod first
	if (nzero){
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
	    // CERR << "SPMOD " << CLOCK() << endl;
	    modrref(m,mred,pivots,det,0,int(m.size()),0,int(m.front()._VECTptr->size())-1,true,false,modulo,false,0);
	    // CERR << "SPMODend " << CLOCK() << endl;
	    if (!is_zero(det)){	      
	      // Last column is the solution, it should be polynomials
	      // that must be untrunced with index = to non-0 coeff of vzero
	      polynome trygcd(p.dim);
	      index_t::const_iterator it=vzero.begin(),itend=vzero.end();
	      int deg=int(itend-it)-1;
	      for (int pos=0;it!=itend;++it,--deg){
		if (!*it)
		  continue;
		gen tmp=mred[pos][e+1]; // e+1=#of points -> last col
		if (tmp.type==_POLY)
		  trygcd=trygcd+tmp._POLYptr->untrunc1(deg);
		else
		  if (!is_zero(tmp))
		    trygcd=trygcd+polynome(monomial<gen>(tmp,deg,1,p.dim));
		++pos;
	      }
	      // Check if trygcd is the gcd!
	      polynome pD(pp1mod(trygcd,modulo)),Q(p.dim),R(d.dim);
	      divremmod(p,pD,modulo,Q,R);
	      if (R.coord.empty()){
		divremmod(q,pD,modulo,Q,R);
		if (R.coord.empty()){
		  pD=pD*r;
		  d=smod(pD*invmod(pD.coord.front().value,modulo),modulo);
		  return;
		}
	      }
	    }
	    // SPMOD not successfull :-(
	    nzero=0;
	  } // end if gcddeg-nzero==e
	} // end if (nzero)
	if (debug_infolevel>1)
	  CERR << "gcdmod interp dim " << d.dim << " " << CLOCK() << endl;
	polynome g1=(g*smod(peval(Delta,valpha,modulo),modulo))*invmod(g.coord.front().value,modulo);
	gen tmp(g1-peval(d,valpha,modulo));
	if (tmp.type==_POLY){
	  g1=smod(*tmp._POLYptr,modulo);
	  g1=g1.untrunc1();
	}
	else
	  g1=polynome(tmp,p.dim);
	d=d+g1*interp*invmod(peval(interp,valpha,modulo),modulo);
	d=smod(d,modulo);
	interp=interp*(polynome(monomial<gen>(plus_one,1,1,p.dim))-polynome(gen(alpha),p.dim));
	++e;
	if (e>gcddeg
	    || is_zero(tmp)
	    ){
	  if (debug_infolevel)
	    CERR << "gcdmod pp1mod dim " << d.dim << " " << CLOCK() << endl;
	  polynome pD(pp1mod(d,modulo)),Q(p.dim),R(d.dim);
	  // This removes the polynomial in x1 that we multiplied by
	  // (it was necessary to know the lcoeff of the interpolated poly)
	  if (debug_infolevel)
	    CERR << "gcdmod check dim " << d.dim << " " << CLOCK() << endl;
	  // Now, gcd divides pD for gcddeg+1 values of x1
	  // degree(pD)<=degree(gcd)
	  divremmod(p,pD,modulo,Q,R);
	  if (debug_infolevel){
	    CERR << "test * " << CLOCK() << endl;
	    polynome R2;
	    mulpoly(pD,Q,R2,modulo);
	    CERR << "test * end " << CLOCK() << endl;
	  }
	  if (R.coord.empty()){
	    divremmod(q,pD,modulo,Q,R);
	    // If pD divides both P and Q, then the degree wrt variables
	    // x2,...,xn is the right one (because it is <= since pD 
	    // divides the gcd and >= since pD(x1=one of the try) was a gcd
	    // The degree in x is the right one because of the condition
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
	    // and same for Q, and the degrees wrt x1 of pD and cofactors
	    // have sum equal to degree of P or Q then pD is the gcd
	    if (R.coord.empty()){
	      pD=pD*r;
	      d=smod(pD*invmod(pD.coord.front().value,modulo),modulo);
	      if (debug_infolevel)
		CERR << "gcdmod found dim " << d.dim << " " << CLOCK() << endl;
	      return;
	    }
	  }
	  if (debug_infolevel)
	    CERR << "Gcdmod bad guess " << endl;
	  continue;
	}
	else
	  continue;
      }
      if (gdeg[0]>delta[0]) // branch if all degree are >=
	continue;
      if (delta[0]>=gdeg[0]){ // restart with g
	gcdv=vecteur(1,g);
	alphav=vecteur(1,alpha);
	delta=gdeg;
	g=(g*smod(peval(Delta,valpha,modulo),modulo))*invmod(g.coord.front().value,modulo);
	d=g.untrunc1();
	e=1;
	interp=polynome(monomial<gen>(plus_one,1,1,p.dim))-polynome(gen(alpha),p.dim);
	continue;
      }
    }
  }

  void psrgcdmod(polynome & a,polynome & b,const gen & modulo,polynome & prim){
    // set auxiliary polynomials g and h to 1
    polynome g(gen(1),a.dim);
    polynome h(g),quo(g),r(g);
    while (!a.coord.empty()){
      int n=b.lexsorted_degree();
      int m=a.lexsorted_degree();
      if (!n) {// if b is constant (then b!=0), gcd=original lgcdmod
	prim=polynome(gen(1),a.dim);
	return ;
      }
      int ddeg=m-n;
      if (ddeg<0)
	swap(a,b); // exchange a<->b may occur only at the beginning
      else {
	polynome b0(firstcoeff(b));
	divremmod(a*pow(b0,ddeg+1),b,modulo,quo,r); // division works always
	if (r.coord.empty())
	  break;
	// remainder is non 0, loop continue: a <- b
	a=b;
	polynome temp(powmod(h,ddeg,modulo));
	// now divides r by g*h^(m-n), result is the new b
        divremmod(r,g*temp,modulo,b,quo); // quo is the remainder here, not used
	// new g=b0 and new h=b0^(m-n)*h/temp
	if (ddeg==1) // the normal case, remainder deg. decreases by 1 each time
	  h=b0;
	else // not sure if it's better to keep temp or divide by h^(m-n+1)
	  divremmod(pow(b0,ddeg)*h,temp,modulo,h,quo);
	g=b0;
      }
    }
    // COUT << "Prim" << b << endl;
    quo.coord.clear();
    lgcdmod(b,modulo,quo);
    divremmod(b,quo,modulo,prim,r);
    prim=smod(prim*invmod(prim.coord.front().value,modulo),modulo);
  }
  
  void contentgcdmod(const polynome &p, const polynome & q, const gen & modulo, polynome & cont,polynome & prim){
    if (p.coord.empty()){
      cont.coord.clear();
      lgcdmod(q,modulo,cont);
      polynome temp(cont.dim);
      divremmod(q,cont,modulo,prim,temp);
      return ;
    }
    if (q.coord.empty()){
      contentgcdmod(q,p,modulo,cont,prim);
      return;
    }
    if (p.dim!=q.dim){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("gausspol.cc/contentgcdmod"));
#endif
      return ;
    }
    // dp and dq are the "content" of p and q w.r.t. other variables
    polynome dp(p.dim), dq(p.dim);
    // CERR << p.dim << " " << CLOCK() << endl;
    lgcdmod(p,modulo,dp);
    lgcdmod(q,modulo,dq);
    // CERR << "End " << p.dim << " " << CLOCK() << endl;
    cont=gcdmod(dp.trunc1(),dq.trunc1(),modulo).untrunc1();
    if (!p.dim){
      prim=polynome(gen(1),0);
      return ;
    }
    // COUT << "Cont" << cont << endl; 
    polynome a(p.dim),b(p.dim),quo(p.dim),r(p.dim);
    // a and b are the primitive part of p and q
    divremmod(p,dp,modulo,a,r);
    divremmod(q,dq,modulo,b,r);
    if (modulo.val>=4*giacmin(p.lexsorted_degree(),q.lexsorted_degree())){
      mod_gcdmod(a,b,modulo,prim);
      return ;
    }
    psrgcdmod(a,b,modulo,prim);
  }

  bool gcdmod_dim1(const polynome &p,const polynome & q,const gen & modulo,polynome & d,polynome & pcof,polynome & qcof,bool compute_cof,bool & real){
    real= poly_is_real(p) && poly_is_real(q);
    if (p.dim!=1)
      return false;
    if (q.dim!=1)
      return false; 
    d.dim=pcof.dim=qcof.dim=1;
    if (real && modulo.type==_INT_ && gcdsmallmodpoly(p,q,modulo.val,d,pcof,qcof,compute_cof)){
      return true; 
    }
    modpoly P(polynome2poly1(p,1));
    modpoly Q(polynome2poly1(q,1));
    environment envi;
    environment * env=&envi;
    env->modulo=modulo;
    env->pn=env->modulo;
    env->moduloon=true;
    env->complexe=true;
    modpoly R,PQ,PR;
    gcdmodpoly(P,Q,env,R);
    if (is_undef(R))
      return false;
    d=poly12polynome(R);
    if (compute_cof){
      DivRem(P,R,env,PQ,PR);
      pcof=poly12polynome(PQ);
      DivRem(Q,R,env,PQ,PR);
      qcof=poly12polynome(PQ);
    }
    return true;
  }

  polynome gcdmod(const polynome &p,const polynome & q,const gen & modulo){
#ifndef NO_STDEXCEPT
    if (p.dim!=q.dim)
      setsizeerr(gettext("Bug!"));
#endif
    if (p==q)
      return p;
    if (p.coord.empty())
      return q;
    if (q.coord.empty())
      return p;
    if (p.dim==1){
      polynome d(1),pd(1),qd(1);
      bool estreel;
      gcdmod_dim1(p,q,modulo,d,pd,qd,false,estreel);
      return d;
    }
    // Check that there are enough points for interpolation
    // Otherwise PSR
    if (modulo.val>=4*giacmin(p.lexsorted_degree(),q.lexsorted_degree())){
      polynome d(p.dim),pcof(p.dim),qcof(p.dim);
      if (modgcd(p,q,modulo,d,pcof,qcof,false))
	return d;
#ifdef TIMEOUT
      control_c();
#endif
      if (ctrl_c || interrupted){
	ctrl_c=false; interrupted=true;
	d.coord.push_back(monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),d.dim));
	return d;
      }
    }
    polynome a(smod(p*invmod(p.coord.front().value,modulo),modulo));
    polynome b(smod(q*invmod(q.coord.front().value,modulo),modulo));
    // Use evaluation points if enough available or modular psrh
    polynome prim(p.dim),cont(p.dim);
    contentgcdmod(a,b,modulo,prim,cont);
    if (debug_infolevel>10)
      COUT << "Prim" << prim << "Cont" << cont << endl;
    return smod(prim*cont, modulo);
  }

  /*
    p and q are assumed to have integer content=1
    the leading coeff of d=gcd(p,q) divides the leading coeff of p and q
    we will therefore normalize modular gcds to have the gcd of the
    leading coeffs as leading coeff, and will try divisibility 
    of it's smodular representant after division by the content
   */
  bool gcd_modular_algo(polynome &p,polynome &q, polynome &d,bool compute_cof){
    if (p.dim==1)
      return gcd_modular_algo1(p,q,d,compute_cof);
    polynome plgcd(p.dim), qlgcd(q.dim), pp(p.dim), qq(p.dim),gcdlgcd(p.dim);
    plgcd=lgcd(p);
    qlgcd=lgcd(q);
    pp=p/plgcd; 
    qq=q/qlgcd;
    gcdlgcd=gcd(plgcd,qlgcd);
    gen gcdfirstcoeff(gcd(pp.coord.front().value, qq.coord.front().value,context0));
    int gcddeg= giacmin(pp.lexsorted_degree(),qq.lexsorted_degree());
    gen bound(pow(gen(2),gcddeg+1)* abs(gcdfirstcoeff,context0) * min(pp.norm(), qq.norm(),context0));
    gen modulo(nextprime(max(gcdfirstcoeff+1,gen(30000),context0))); 
    gen productmodulo(1);
    polynome currentgcd(p.dim),p_simp(p.dim),q_simp(p.dim),rem(p.dim);
    // 30000 leaves many primes below the 2^15 bound 
    for (;;modulo = nextprime(modulo+2)){
      // increment modulo to avoid modulo = 1 [4] so that it works in Z[i]
      while ( is_one(modulo % 4) || is_zero(gcdfirstcoeff % modulo))
	modulo=nextprime(modulo+2);
      polynome _gcdmod(gcdmod(smod(pp,modulo),smod(qq,modulo),modulo));
      gen adjustcoeff=gcdfirstcoeff*invmod(_gcdmod.coord.front().value,modulo);
      _gcdmod=smod((_gcdmod * adjustcoeff), modulo) ;
      int m=_gcdmod.lexsorted_degree();
      if (!m){
	p=pp*(plgcd/gcdlgcd);
	q=qq*(qlgcd/gcdlgcd);
	d=gcdlgcd;
	return true;
      }
      // combine step
      if (m<gcddeg){ // previous prime was bad
	gcddeg=m;
	currentgcd=_gcdmod;
	productmodulo=modulo;
      }
      else {
	if (m==gcddeg){ // start combine
	  if (productmodulo==gen(1)){ // no need to combine primes
	    currentgcd=_gcdmod;
	    productmodulo=modulo;
	  }
	  else {
	    //  COUT << "Modulo:" << modulo << " " << _gcdmod << endl;
	    // COUT << "Old gcd:" << productmodulo << " " << currentgcd << endl ;
	    currentgcd=ichinrem(_gcdmod,currentgcd,modulo,productmodulo);
	    // COUT << "Combined to " << currentgcd << endl;
	    productmodulo=productmodulo*modulo;
	  }
	}
	// m>gcddeg this prime is bad, just ignore
      }
      //      if (productmodulo>bound){
      d=smod(currentgcd,productmodulo);
      ppz(d);
      //if ( pp.TDivRem1(d,p_simp,rem) && rem.coord.empty() && qq.TDivRem1(d,q_simp,rem) && rem.coord.empty() ){
      if ( divrem1(pp,d,p_simp,rem) && rem.coord.empty() && divrem1(qq,d,q_simp,rem) && rem.coord.empty() ){
	p=p_simp*(plgcd/gcdlgcd);
	q=q_simp*(qlgcd/gcdlgcd);
	d=d*gcdlgcd;
	return true;
      }
      // }
    }
    return false;
  }

  polynome pzadic(const polynome &p,const gen & n){
    monomial_v v;
    index_t i;
    for (monomial_v::const_iterator it=p.coord.begin();it!=p.coord.end();++it){
      i.clear();
      i.push_back(0);
      for (index_t::const_iterator iti=it->index.begin();iti!=it->index.end();++iti)
	i.push_back(*iti);
      gen k=it->value;
      for (int j=0;!is_zero(k);j++){
	gen r=smod(k,n.re(0));
	if (!is_zero(r)){
	  i[0]=j;
	  v.push_back(monomial<gen>(r,i));
	}
	k=iquo( (k-r),n.re(context0));
      }
    }
    // sort v
    polynome res(p.dim+1,v);
    res.tsort();
    return res;
  }

  bool listmax(const polynome &p,gen & n ){
    return Tlistmax<gen>(p,n);
  }

  bool unext(const polynome & p,const gen & pmin,polynome & res){
    res.dim=p.dim; res.coord.clear();
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    res.coord.reserve(itend-it);
    for (;it!=itend;++it){
      gen g=it->value;
      if (g.type==_FRAC)
	return false;
      if (g.type==_EXT){
	if (*(g._EXTptr+1)!=pmin)
	  return false;
	g=*g._EXTptr;
	if (g.type==_VECT)
	  g.subtype=_POLY1__VECT;
	res.coord.push_back(monomial<gen>(g,it->index));
      }
      else
	res.coord.push_back(*it);
    }
    return true;
  }

  bool ext(polynome & res,const gen & pmin){
    vector< monomial<gen> >::iterator it=res.coord.begin(),itend=res.coord.end();
    for (;it!=itend;++it){
      gen g=ext_reduce(it->value,pmin);
      if (is_zero(g)) return false;
      it->value=g;
    }
    return true;
  }

  void ext(const polynome & p,const gen & pmin,polynome & res){
    res.dim=p.dim;
    res.coord.clear();
    res.coord.reserve(p.coord.size());
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      gen g=ext_reduce(it->value,pmin);
      if (is_zero(g)) 
	continue;
      res.coord.push_back(monomial<gen>(g,it->index));
    }
  }

  void unmodularize(const polynome & p,polynome & res){
    res.dim=p.dim;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    res.coord.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->value.type==_MOD)
	res.coord.push_back(monomial<gen>(*it->value._MODptr,it->index));
      else
	res.coord.push_back(monomial<gen>(it->value,it->index));
    }
  }

  polynome unmodularize(const polynome & p){
    polynome res(p.dim);
    unmodularize(p,res);
    return res;
  }

  void modularize(polynome & d,const gen & m){
    vector< monomial<gen> >::iterator it=d.coord.begin(),itend=d.coord.end();
    for (;it!=itend;++it){
      if (it->value.type!=_USER)
	it->value=makemod(it->value,m);
    }
  }

  // Find indexes of p such that p is constant, answer is in i
  static void has_constant_variables(const polynome & p,index_t & i){
    i=index_t(p.dim,0);
    for (int j=0;j<p.dim;++j){
      i[j]=j;
    }
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t::iterator iit,iitend;
    for (;it!=itend && !i.empty();++it){
      index_t::const_iterator j=it->index.begin();
      iit=i.begin(); iitend=i.end();
      for (;iit!=iitend;){
	if (*(j+*iit)){ // non-0 power in monomial
	  i.erase(iit);
	  iit=i.begin();
	  iitend=i.end();
	}
	else
	  ++iit;
      }
    }
  }

  // p assumed to be constant wrt variables in pi
  // vi is a vector of degree
  static int extract_monomials(const polynome &p,const index_t & pi,vectpoly & vp){
    index_t pdeg=p.degree();
    // find largest degree of p with respect to these variables
    int s=int(pi.size()),ans=1;
    index_t v(s+1);
    int i=0;
    for (;i<s;++i){
      if (ans>1000) // FIXME what's the right size??
	return i;
      v[i]=pdeg[pi[i]]+1;
      ans=ans*v[i];
    }
    if (ans>10000)
      return -1;
    vp=vectpoly(ans,polynome(p.dim-s));
    if (ans==1)
      vp[0].coord.reserve(p.coord.size());
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t::const_iterator piitbeg=pi.begin(),piit,piitend=pi.end(),vitbeg=v.begin(),vit,iti;
    index_t::iterator iit;
    int vp_pos;
    for (;it!=itend;++it){
      index_m i(p.dim-s);
      piit=piitbeg;
      vit=vitbeg;
      iit=i.begin();
      iti=it->index.begin();
      vp_pos=0;
      // construct new index without constant variables
      // and find value of index inside vp
      // iti index in current monomial of p, piit index in list of variables (p or q cst), vit index in v
      for (int j=0;j!=p.dim;++iti,++j){
	if (piit!=piitend && j==*piit){
	  ++piit;
	  vp_pos=vp_pos*(*vit)+(*iti);
	  ++vit;
	}
	else {
	  *iit=*iti;
	  ++iit;
	}
      }
      vp[vp_pos].coord.push_back(monomial<gen>(it->value,i));
    }
    return 0;
  }

  static bool has_constant_variables_gcd(const polynome & p,const polynome & q,polynome & d){
    if (q.coord.empty()){
      d=p;
      return true;
    }
    if (p.coord.empty()){
      d=q;
      return true;
    }
    index_t pi,qi;
    has_constant_variables(p,pi);
    has_constant_variables(q,qi);
    // merge pi and qi
    index_t::iterator qit=qi.begin(),qitend=qi.end();
    for (;qit!=qitend;++qit){
      if (!equalposcomp(pi,*qit))
	pi.push_back(*qit);
    }
    if (pi.empty())
      return false;
    int s=int(pi.size());
    if (s==p.dim){
      gen n=gcd(Tcontent<gen>(p),Tcontent<gen>(q),context0);
      d=polynome(monomial<gen>(n,p.dim));
      return true;
    }
    sort(pi.begin(),pi.end());
    // p or q is constant with respect to at least one variable
    // make a vector of polynomial from p and q
    vectpoly vp,vq;
    int i;
    if ( (i=extract_monomials(p,pi,vp)) ){
      if (i<0)
	return false;
      pi=index_t(pi.begin(),pi.begin()+i);
      i=extract_monomials(p,pi,vp);
      if (i<0)
	return false;
    }
    if ( (i=extract_monomials(q,pi,vq)) ){
      if (i<0)
	return false;
      pi=index_t(pi.begin(),pi.begin()+i);
      extract_monomials(p,pi,vp);
      i=extract_monomials(q,pi,vq);
      if (i<0)
	return false;
    }
    // find gcd of polys in vp and vq
    vectpoly::const_iterator it=vp.begin(),itend=vp.end(),jt=vq.begin(),jtend=vq.end();
    d=*jt;
    for (++jt;!is_one(d) && it!=itend;++it)
      d=gcd(d,*it);
    for (;!is_one(d) && jt!=jtend;++jt)
      d=gcd(d,*jt);
    // reconstruct gcd of p and q
    vector< monomial<gen> >::iterator dt=d.coord.begin(),dtend=d.coord.end();
    index_t::const_iterator piitbeg=pi.begin(),piit,piitend=pi.end(),dtit;
    int j;
    for (;dt!=dtend;++dt){
      index_m newi;
      newi.reserve(p.dim);
      piit=piitbeg;
      dtit=dt->index.begin();
      for (j=0;j<p.dim;++j){
	if (piit!=piitend && j==*piit){
	  newi.push_back(0);
	  ++piit;
	}
	else {
	  newi.push_back(*dtit);
	  ++dtit;
	}
      }
      dt->index=newi;
    }
    d.dim=p.dim;
    return true;
  }

  int coefftype(const polynome & p,gen & coefft){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t=0;
    for (;it!=itend;++it){
      const unsigned char tmp=it->value.type;
      if (tmp==_INT_ || tmp==_ZINT)
	continue;
      t=tmp;
      coefft=it->value;
      if (t==_USER)
	return t;
      if (t==_MOD)
	return t;
      if (t==_EXT)
	return t;
    }
    return t;
  }

  static bool gcdheu(const polynome &p_orig,const index_t & p_deg,const polynome &q_orig, const index_t & q_deg,polynome & p_simp, gen & np_simp, polynome & q_simp, gen & nq_simp, polynome & d, gen & d_content,bool skip_test,bool compute_cofactors){
    // COUT << "Entering gcdheu " << p.dim << endl;
    if (debug_infolevel>=123456-p_orig.dim)
      CERR << "Gcdheu begin " << p_orig.dim << " " << CLOCK() << " " << p_deg << " " << p_orig.coord.size() << " " << q_deg << " " << q_orig.coord.size() << endl;
    if (&p_orig!=&p_simp)
      p_simp=p_orig;
    if (&q_orig!=&q_simp)
      q_simp=q_orig;
    if (debug_infolevel>=123456-p_simp.dim)
      CERR << "Gcdheu end copy" << CLOCK() << endl;
    // check if one coeff is a _MOD or _USER
    gen coefft,coeffqt;
    int pt=coefftype(p_simp,coefft),qt=coefftype(q_simp,coeffqt);
    if (pt>=_EXT && qt>=_EXT && pt!=qt){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Incompatible coeff type"));
#endif
      return false;
    }
    if (pt<_EXT && qt>=_EXT){
      pt=qt;
      coefft=coeffqt;
    }
    // If p, q have modular coeff, use modular algo
    if (!pt){
      pt=qt;
      coefft=coeffqt;
    }
    d_content=1;
    if (pt==_MOD){
      gen m=*(coefft._MODptr+1);
      if (debug_infolevel)
	CERR << "gcdmod begin " << CLOCK() << endl;
      polynome pmod,qmod;
      unmodularize(p_simp,pmod);
      unmodularize(q_simp,qmod);
      d=gcdmod(pmod,qmod,m);
      if (debug_infolevel)
	CERR << "gcdmod end " << CLOCK() << endl;
      if (compute_cofactors){
	polynome pmodd,qmodd,tmp;
	divremmod(pmod,d,m,pmodd,tmp);
	divremmod(qmod,d,m,qmodd,tmp);
	// CERR << dmod << ":;\n" << pmodd << ":;\n" << qmodd << endl;
	p_simp=pmodd;
	modularize(p_simp,m);
	q_simp=qmodd;
	modularize(q_simp,m);
      }
      modularize(d,m);
      return true;
    }
    if (pt==_USER){
      coefft._USERptr->polygcd(p_simp,q_simp,d);
      if (compute_cofactors){
	p_simp=p_simp/d;
	q_simp=q_simp/d;
      }
      return true;
    }
    // does not work for collect(( -az^2-3*az*cos(kt)^2+az-cos(kt)^2)*sqrt(2*cos(kt)^2+az^2+2*az*cos(kt)^2-1)*ax*ksx*sin(kt)+(az^2*cos(kt)^2+az*cos(kt)^2+az+2*cos(kt)^2-1)*sqrt(2*cos(kt)^2+az^2+2*az*cos(kt)^2-1)*ax*ktx*sin(kt)+(az^2+3*az*cos(kt)^2-az+cos(kt)^2)*sqrt(2*cos(kt)^2+az^2+2*az*cos(kt)^2-1)*ay*ksy*sin(kt)+(az^2+3*az*cos(kt)^2-az+cos(kt)^2)*sqrt(2*cos(kt)^2+az^2+2*az*cos(kt)^2-1)*ay*kty*sin(kt))
    // np_simp=(pt!=_EXT)?ppz(p_simp):1;
    // nq_simp=(qt!=_EXT)?ppz(q_simp):1;
    np_simp=ppz(p_simp);
    nq_simp=ppz(q_simp);
    if (debug_infolevel>=123456-p_simp.dim)
      CERR << "Gcdheu end ppz" << CLOCK() << " " << np_simp << " " << nq_simp << endl;
    d_content=gcd(np_simp,nq_simp,context0);
    // type may have changed by ppz simplification, recheck
    if (!is_integer(np_simp))
      pt=coefftype(p_simp,coefft);
    if (!is_integer(nq_simp))
      qt=coefftype(q_simp,coeffqt);
    if (pt>=_EXT && qt>=_EXT && pt!=qt){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Incompatible coeff type"));
#endif
      return false;
    }
    if (pt<_EXT && qt>=_EXT){
      pt=qt;
      coefft=coeffqt;
    }
    if (!pt){
      pt=qt;
      coefft=coeffqt;
    }
    if (qt==_POLY && pt<_POLY){
      pt=qt;
      coefft=coeffqt;
    }
    if (pt==_POLY){
      if (coefft.type!=_POLY){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false;
      }
      int innerdim=coefft._POLYptr->dim;
      polynome pmulti=unsplitmultivarpoly(p_simp,innerdim);
      polynome qmulti=unsplitmultivarpoly(q_simp,innerdim);
      d=gcd(pmulti,qmulti);
      d=splitmultivarpoly(d,innerdim);
      if (compute_cofactors){
	p_simp=p_simp/d;
	q_simp=q_simp/d;
      }
      return true;
    }
    if (Tis_constant(p_simp) || Tis_constant(q_simp)){
      if (debug_infolevel>=2)
	CERR << "//Gcdheu p constant!" << endl;
      d=polynome(plus_one,p_simp.dim);
      return true;
    }
    if (p_simp.dim==1 && !pt){
      // gcd in Z[X]
      return gcd_modular(p_simp,q_simp,d,p_simp,q_simp,compute_cofactors);
    }
    bool allowrational = (pt>=_POLY || qt>=_POLY) && (pt!=_EXT && qt!=_EXT);
    if (
	!all_inf_equal(q_deg,p_deg)
	// !(p_deg>q_deg)
	){
      polynome quo(p_simp.dim);
      if (exactquotient(q_simp,p_simp,quo,allowrational)){
	d=p_simp;
	q_simp=quo;
	p_simp=polynome(monomial<gen>(plus_one,0,p_simp.dim));
	if (is_positive(-d.coord.front())){
	  d=-d; p_simp=-p_simp; q_simp=-q_simp;
	}
	if ( debug_infolevel>=123456-p_simp.dim )
	  CERR << "// End exact " << p_simp.dim << " " << CLOCK() << " " <<d.coord.size() << endl;
	return true;
      }
      if ( debug_infolevel>=123456-p_simp.dim )
	CERR << "//Gcdheu exact division failed! " << CLOCK() << endl;
      if (p_simp.coord.size()==1){
	index_t i=index_gcd(p_simp.coord.front().index.iref(),q_simp.gcddeg());
	d=polynome(monomial<gen>(plus_one,i));
	if (i!=index_t(i.size())){
	  i=-i;
	  p_simp=p_simp.shift(i);
	  q_simp=q_simp.shift(i);
	}
	return true;
      }
    }
    if (
	!all_inf_equal(p_deg,q_deg)
	//!(q_deg>p_deg) 
	) {
      polynome quo(p_simp.dim);
      if (exactquotient(p_simp,q_simp,quo,allowrational)){
	d=q_simp;
	p_simp=quo;
	q_simp=polynome(monomial<gen>(plus_one,0,p_simp.dim));
	if (is_positive(-d.coord.front())){
	  d=-d; p_simp=-p_simp; q_simp=-q_simp;
	}
	if ( debug_infolevel>=123456-p_simp.dim )
	  CERR << "//End exact " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
	return true;
      }
      if ( debug_infolevel>=123456-p_simp.dim )
	CERR << "//Gcdheu exact division failed! " << CLOCK() << endl;
      if (q_simp.coord.size()==1){
	index_t i=index_gcd(q_simp.coord.front().index.iref(),p_simp.gcddeg());
	d=polynome(monomial<gen>(plus_one,i));
	if (i!=index_t(i.size())){
	  i=-i;
	  p_simp=p_simp.shift(i);
	  q_simp=q_simp.shift(i);
	}
	return true;
      }
    } 
    if (p_simp.lexsorted_degree()==0){
      if (debug_infolevel >= 20-p_simp.dim)
	CERR << "Begin cst " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
      if (q_simp.lexsorted_degree()==0){
	d=gcd(p_simp.trunc1(),q_simp.trunc1()).untrunc1();
      }
      else {
	d=p_simp;
	Tlgcd<gen>(q_simp,d);
      }
      if (!is_one(d) && compute_cofactors){
	p_simp=p_simp/d;
	q_simp=q_simp/d;
      }
      if (debug_infolevel >= 20-p_simp.dim)
	CERR << "End cst " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
      return true;
    }
    if (q_simp.lexsorted_degree()==0){
      if (debug_infolevel >= 20-p_simp.dim)
	CERR << "Begin cst " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
      d=q_simp;
      Tlgcd<gen>(p_simp,d);
      if (!is_one(d) && compute_cofactors){
	q_simp=q_simp/d;
	p_simp=p_simp/d;
      }
      if (debug_infolevel >= 20-p_simp.dim)
	CERR << "End cst " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
      return true;
    }
    if (pt==_EXT){ 
      // FIXME then test for
      // m:=matrix(2,2,[1,1,i*(sqrt(a^2*b^2-4*a*b)+a*b)/(2*a),i*(-sqrt(a^2*b^2-4*a*b)+a*b)/(2*a)]); M:=simplify(trn(m)*m); egvl(M);
      int dim=p_simp.dim;
      vector< T_unsigned<gen,hashgcd_U> > p,q,g,pcof,qcof;
      index_t di(dim);
      std::vector<hashgcd_U> vars(dim);
      if (!convert(p_simp,q_simp,di,vars,p,q))
	return false;
      if (!gcd_ext(p,q,g,pcof,qcof,vars,compute_cofactors,threads))
	return false;
      if (debug_infolevel>1)
	CERR << CLOCK()*1e-6 << " success gcd_ext" << endl;
      convert_from<gen,hashgcd_U>(g,di,d);
      if (compute_cofactors){
	convert_from<gen,hashgcd_U>(pcof,di,p_simp);
	convert_from<gen,hashgcd_U>(qcof,di,q_simp);
      }
      // normalize gcd and cofactors
      gen firstd=evalf_double(d.coord.front().value,1,context0);
      if (firstd.type==_DOUBLE_ && is_positive(-firstd,context0)){
	d *= -1;
	if (compute_cofactors){
	  p_simp *= -1;
	  q_simp *= -1;
	}
      }
      if (firstd.type==_CPLX && firstd._CPLXptr->type==_DOUBLE_ && (firstd._CPLXptr+1)->type==_DOUBLE_){
	int arg=int(std::floor(std::atan2((firstd._CPLXptr+1)->_DOUBLE_val,firstd._CPLXptr->_DOUBLE_val)/(M_PI/2)));
	if (arg!=0){
	  gen mult=arg>0?(-cst_i):(arg==-1?cst_i:-1);
	  d *= mult;
	  if (compute_cofactors){
	    p_simp *= mult;
	    q_simp *= mult;
	  }
	}
      }
      return true;
    }
    int Dbdeg=giacmin(p_simp.lexsorted_degree(),q_simp.lexsorted_degree());
    bool est_reel=poly_is_real(p_simp) && poly_is_real(q_simp);    // FIXME: should check for extensions!
    if (debug_infolevel>=2)
      CERR << "//Gcdheu " << p_deg << " " << p_simp.coord.size() << " " << q_deg << " " << q_simp.coord.size() << endl;
   // first try evaluation for quick trivial gcd
    if (!skip_test ){
      if (p_simp.dim>1) {
	vecteur b(p_simp.dim-1);
	polynome Fb(1),Gb(1),Db(1);
	if (debug_infolevel >= 20-p_simp.dim)
	  CERR << "// GCD eval dimension " << p_simp.dim << " " << CLOCK() << " " << p_deg << " " << p_simp.coord.size() << " " << q_deg << q_simp.coord.size() << " " << endl;
	gen essaimod=30011; // mod 4 = 3
	for (int essai=0;essai<2;++essai){
	  if (essai)
	    b=vranm(p_simp.dim-1,0,0); // find another random point
	  // essaimod was est_reel?essaimod:0
	  for (;!find_good_eval(p_simp,q_simp,Fb,Gb,b,debug_infolevel >= 20-p_simp.dim,essaimod);){
	    for (;;){
	      essaimod=nextprime(essaimod+1);
	      if (!is_one(smod(essaimod,4)))
		break;
	    }
	  }
#ifndef NO_STDEXCEPT
	  try {
#endif
	    Db=gcdmod(Fb,Gb,essaimod);
#ifndef NO_STDEXCEPT
	  } catch (std::runtime_error & ){
	    Db=gcd(Fb,Gb);
	  }
#endif
	  Dbdeg=Db.lexsorted_degree();
	  if (debug_infolevel >= 20-p_simp.dim)
	    CERR << "// evaled GCD deg " << Dbdeg << endl;
	  if (!Dbdeg){
	    d.coord.clear();
	    Tcommonlgcd<gen>(p_simp,q_simp,d);
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "end eval " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
	    if (compute_cofactors){
	      p_simp=p_simp/d;
	      q_simp=q_simp/d;
	    }
	    return true;
	  }
	  if (Dbdeg==p_simp.lexsorted_degree()){ // try p_simp/lgcd as gcd
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "Trying p/lgcd(p) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	    polynome p_simp_lgcd(Tlgcd<gen>(p_simp));
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "lgcd(p) ok " << p_simp.dim << " " << CLOCK() << endl;
	    polynome p_simp_simp(p_simp.dim);
	    if (!exactquotient(p_simp,p_simp_lgcd,p_simp_simp)) {
#ifndef NO_STDEXCEPT
	      setsizeerr(gettext("gausspol.cc/gcdheu"));
#endif
	      return false;
	    }
	    polynome quo(q_simp.dim);
	    if (exactquotient(q_simp,p_simp_simp,quo)){
	      if ( debug_infolevel >= 20-p_simp.dim )
		CERR << "Success p/lgcd(p) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	      polynome quo_lgcd(p_simp_lgcd);
	      Tlgcd<gen>(quo,quo_lgcd);
	      d=p_simp_simp*quo_lgcd;
	      if (compute_cofactors){
		p_simp=p_simp_lgcd/quo_lgcd;
		q_simp=quo/quo_lgcd;
	      }
	      return true;
	    }
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "Failed p/lgcd(p) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	  }
	  if (Dbdeg==q_simp.lexsorted_degree()){ // try p_simp/lgcd as gcd
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "Trying q/lgcd(q) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	    polynome q_simp_lgcd(Tlgcd<gen>(q_simp));
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "lgcd(q) ok " << p_simp.dim << " " << CLOCK() << endl;
	    polynome q_simp_simp(q_simp.dim);
	    if (!exactquotient(q_simp,q_simp_lgcd,q_simp_simp)){
#ifndef NO_STDEXCEPT
	      setsizeerr(gettext("gausspol.cc/gcdheu"));
#endif
	      return false;
	    }
	    polynome quo(p_simp.dim);
	    if (exactquotient(p_simp,q_simp_simp,quo)){
	      if ( debug_infolevel >= 20-p_simp.dim )
		CERR << "Success q/lgcd(q) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	      polynome quo_lgcd(q_simp_lgcd);
	      Tlgcd<gen>(quo,quo_lgcd);
	      d=q_simp_simp*quo_lgcd;
	      if (compute_cofactors){
		q_simp=q_simp_lgcd/quo_lgcd;
		p_simp=quo/quo_lgcd;
	      }
	      return true;
	    }
	    if ( debug_infolevel >= 20-p_simp.dim )
	      CERR << "Failed q/lgcd(q) as gcd " << p_simp.dim << " " << CLOCK() << endl;
	  }
	}
      }
    }
    // now work on p_simp and q_simp
    if (!p_simp.dim){
      d=polynome(gen(1),0);
      return true;
    }
    gen np,nq,n;
    if (!listmax(p_simp,np)){
      return false;
    }
    if (!listmax(q_simp,nq)){
      return false;
    }
    if (p_simp.dim==1){ // integer modular try, was p_simp.dim==1 && est_reel
      environment * env= new environment;
      bool avoid_it=false;
      dense_POLY1 pp,qq;
#ifndef NO_STDEXCEPT
      try {
#endif
	pp=modularize(p_simp,0,env);
	qq=modularize(q_simp,0,env);
	if (is_undef(pp) || is_undef(qq))
	  avoid_it=true;
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
	avoid_it=true;
      }
#endif
      env->moduloon = true;
      env->modulo=1001;
      env->pn=env->modulo;
      env->complexe=!est_reel;
      for (int essai=0;essai<2 && !avoid_it;++essai){
	env->modulo=nextprime(env->modulo+2);
	while ( is_one(smod(env->modulo,4)) || !is_one(gcd(gcd(env->modulo,pp.front(),context0),qq.front(),context0)) )
	  env->modulo=nextprime(env->modulo+2);
	modpoly _gcdmod;
	gcdmodpoly(pp,qq,env,_gcdmod);
	if (is_undef(_gcdmod))
	  return false;
	Dbdeg=giacmin(Dbdeg,int(_gcdmod.size())-1);
	if (!Dbdeg)
	  break;
      }
      delete env;
      if (!Dbdeg){
	d=polynome(gen(1),p_simp.dim);
	return true;
      }	
    } 
    polynome p1(p_simp.dim),q1(p_simp.dim),r1(p_simp.dim),r2(p_simp.dim);
    gen n_2(2),n_73794(73794),n_27011(27011);
    if (is_greater(nq,np,context0))
      n=n_2*nq+n_2;
    else
      n=n_2*np+n_2;
    // PSR if gcd has a large degree, modular if low degree, else try heugcd
    // PSR complexity is proportionnal to 
    // #iteration*deg_var_n*(total_deg_other_vars*#iteration)^(2*#other_var)
    // MODGCD to product of all (part_deg_of_gcd+1+part_deg_of_gcd_lcoeff)
    int maxpqdeg0=giacmax(p_simp.lexsorted_degree(),q_simp.lexsorted_degree());
    int minpqdeg0=giacmin(p_simp.lexsorted_degree(),q_simp.lexsorted_degree());
    index_t maxpqdeg(p_simp.dim);
    double sparsenessp=double(p_simp.coord.size()),sparsenessq=double(q_simp.coord.size());
    for (int i=0;i<p_simp.dim;++i){
      maxpqdeg[i]=giacmax(p_deg[i],q_deg[i]);
      sparsenessp /= (p_deg[i]+i+1);
      sparsenessp *= (i+1);
      sparsenessq /= (q_deg[i]+i+1);
      sparsenessq *= (i+1);
    }
    double heugcddigits=1.0,maxmodop=1.0,minmodop=1.0;
    int total_deg_other_var=-maxpqdeg[0];
    for (int i=0;i<p_simp.dim;++i){
      heugcddigits *= maxpqdeg[i] ; 
      minmodop *= Dbdeg+1; // approximation!!! should be partial degree[gcd]
      maxmodop *= giacmin(p_deg[i],q_deg[i])+1+p_deg[i]+q_deg[i];
      total_deg_other_var += maxpqdeg[i];
    }
    double psrstep=minpqdeg0-Dbdeg;
    double psrgcdop=std::pow(psrstep,2*(p_simp.dim-1))*(p_simp.coord.size()*q_simp.coord.size())/minpqdeg0, modop=std::sqrt(minmodop*maxmodop)/10,heuop=heugcddigits*heugcddigits/10.0;
    double minop=psrgcdop; if (psrgcdop>heuop) minop=heuop; if (minop>modop) minop=modop;
    if (debug_infolevel)
      CERR << "Psr " << psrgcdop << ", Mod " << modop << ", Heu " << heuop << ", Min" << minop << endl;
    if (modop<minop) minop=modop; // was if (est_reel && modop<minop)
    if (debug_infolevel){
      if (p_simp.dim==1)
	CERR << "GCD dim 1, n=" << n << " maxpqdeg0 " << maxpqdeg0 << "(" << maxpqdeg << ")" << endl;
      else
	CERR << "GCD dim " << p_simp.dim << " degree " << Dbdeg << " psrgcdop " << psrgcdop << " heuop " << heuop << " modgcdop " << minmodop << "," << maxmodop << endl;
    }
    if (!skip_test){
      // int dd=p_simp.dim*p.lexsorted_degree();
      // first try ezgcd then modgcd
      if ( // false && // uncomment to cancel EZGCD 
	  (sparsenessp<0.3 || sparsenessq<0.3 ) && psrgcdop> modop &&
	  (p_simp.dim>3) // && (Dbdeg<=maxpqdeg0/4+1) 
	  && ezgcd(p_simp,q_simp,d,true,true,0,minop)){
	if (debug_infolevel)
	  COUT << "// Used EZ gcd " << endl;
	if (compute_cofactors){
	  q_simp=q_simp/d;
	  p_simp=p_simp/d;
	}
	return true;
      }
      if (//false && 
	  p_simp.dim>1 && psrgcdop< modop && psrgcdop < heuop ){
	d=gcdpsr(p_simp,q_simp,Dbdeg);
	if (compute_cofactors){
	  q_simp=q_simp/d;
	  p_simp=p_simp/d;
	}
	return true;
      }
#ifndef NSPIRE
      if (//true ||  
	  modop < heuop 
	  ){ // was  if ( modop < heuop && est_reel)
	if (debug_infolevel)
	  COUT << "// " << CLOCK() << " Using modular gcd " << endl;
	bool res=gcd_modular(p_simp,q_simp,d,p_simp,q_simp,compute_cofactors);
	if (debug_infolevel)
	  COUT << "// " << CLOCK() << " End modular gcd " << endl;
	return res;
      }
#endif
    }
    if (debug_infolevel)
      COUT << "// Using Heu gcd " << endl;
    int max_try=0;
    for (; max_try<GCDHEU_MAXTRY;max_try++){
      polynome pn(p_simp(n));
      // COUT << p_simp << " pn:" << pn << " n=" << n << endl;
      polynome qn(q_simp(n));
      // NEW CODE
      if (skip_test){
	polynome pntmp,qntmp;
	gen pnsimp,qnsimp,dtmp;
	if (!gcdheu(pn,qn,pntmp,pnsimp,qntmp,qnsimp,d,dtmp,true,compute_cofactors)){
	  return false;
	}
	d=pzadic(d*dtmp,n);
      }
      else {
      // OLD CODE
	d=gcd(pn,qn);
	d=pzadic(d,n);
      }
      // END MODIFICATIONS
      ppz(d);
      if (!d.coord.empty()){
	gen tmp=d.coord.front().value;
	if (is_zero(re(tmp,context0))){ // checked
	  d=cst_i*d;
	  tmp=cst_i*tmp;
	}
	if (is_positive(-tmp,context0)) // checked
	  d=-d;
      }
      //      COUT << "(ppz) d=" << d << endl;
      // COUT << "p_simp" << p_simp << endl << "q_simp" << q_simp << endl;
      //if ( divrem(p_simp,d,p1,r1,false) && (r1.coord.size()==0) && divrem(q_simp,d,q1,r2,false) && (r2.coord.size()==0) ){
      if ( p_simp.TDivRem1(d,p1,r1) && (r1.coord.size()==0) && q_simp.TDivRem1(d,q1,r2) && (r2.coord.size()==0) ){
	//	COUT << "p_simp/d" << p1 << "q_simp/d" << q1 << endl;
	p_simp=p1;
	q_simp=q1;
	// COUT << "gcdheu success " << max_try << endl;
	if (debug_infolevel >= 20-p_simp.dim)
	  CERR << "end gcdheu " << p_simp.dim << " " << CLOCK() << " " << d.coord.size() << endl;
	return true;
      }
      n=iquo(n*n_73794,n_27011);
    }
    // COUT << "gcdheu failure" << endl;
    return false; 
  }

  bool gcdheu(const polynome &p_orig,const polynome &q_orig, polynome & p_simp, gen & np_simp, polynome & q_simp, gen & nq_simp, polynome & d, gen & d_content,bool skip_test,bool compute_cofactors){
    index_t pdeg=p_orig.degree(),qdeg=q_orig.degree();
    return gcdheu(p_orig,pdeg,q_orig,qdeg,p_simp,np_simp,q_simp,nq_simp,d,d_content,skip_test,compute_cofactors);
  }

  polynome gcdpsr(const polynome &p,const polynome &q,int gcddeg){
    if (has_num_coeff(p) || has_num_coeff(q))
      return polynome( monomial<gen>(1,p.dim));
    if (debug_infolevel)
      COUT << "// Using PSR gcd " << endl;
    if (!gcddeg && p.dim>1){ // find probable degree
      vecteur b(p.dim-1);
      polynome Fb(1),Gb(1),Db(1);
      for (int essai=0;essai<2;++essai){
	if (essai)
	  b=vranm(p.dim-1,0,0); // find another random point
	find_good_eval(p,q,Fb,Gb,b,debug_infolevel >= 20-p.dim);
	Db=gcd(Fb,Gb);
	int Dbdeg=Db.lexsorted_degree();
	if (!Dbdeg)
	  return gcd(Tlgcd(p),Tlgcd(q));
	if (!gcddeg)
	  gcddeg=Dbdeg;
	else
	  gcddeg=giacmin(Dbdeg,gcddeg);
      }
    }
    return Tgcdpsr<gen>(p,q,gcddeg);
  }

  bool findabcdelta(const polynome & p,polynome & a,polynome &b,polynome & c,polynome & delta){
    if (p.lexsorted_degree()!=2)
      return false;
    monomial_v::const_iterator it=p.coord.begin(),itend=p.coord.end();
    a=Tnextcoeff<gen>(it,itend);
    if (it==itend){
      b=polynome(a.dim);
      c=polynome(a.dim);
      delta=polynome(a.dim);
      return true;
    }
    if (it->index.front()==1)
      b=Tnextcoeff<gen>(it,itend);
    else
      b=polynome(a.dim);
    if (it==itend)
      c=polynome(a.dim);
    else
      c=Tnextcoeff<gen>(it,itend);
    delta=b*b-a*c*gen(4);
    return (it==itend);
  }

  bool findde(const polynome & p,polynome & d,polynome &e){
    if (p.coord.empty()){
      d=p;
      e=p;
      return true;
    }
    int n=p.lexsorted_degree();
    if (n>1)
      return false;
    monomial_v::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (!n){
      e=Tnextcoeff<gen>(it,itend);
      d=polynome(e.dim);
      return(it==itend);
    }
    d=Tnextcoeff<gen>(it,itend);
    if (it==itend)
      e=polynome(d.dim);
    else
      e=Tnextcoeff<gen>(it,itend);  
    return (it==itend);
  }

  int total_degree(const polynome & p){
    int res=0,deg;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      deg=int(it->index.total_degree());
      if (deg>res)
	res=deg;
    }
    return res;
  }

  // evaluate all vars but the j-th to 0
  static gen peval0(const polynome & p,int j,int & total_deg){
    if (!j){
      vecteur v(p.dim-1);
      total_deg=total_degree(Tfirstcoeff<gen>(p).degree());
      return peval(p,v,0);
    }
    vecteur res;
    total_deg=0;
    int s=0,smax=0,n=p.dim,total;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int i,k=0;
    index_t::const_iterator itit;
    bool add;
    for (;it!=itend;++it){
      itit=it->index.begin();
      add=true;
      for (i=0,total=0;i<n;++i,++itit){
	total += *itit;
	if (i==j)
	  k=*itit;
	else {
	  if (*itit) 
	    add=false;
	}
      }
      if (add) {
	if (k<s)
	  res[k] = res[k]+it->value;
	else {
	  for (;s<k;++s)
	    res.push_back(0);
	  res.push_back(it->value);
	  ++s;
	}
      }
      if (k>smax){
	total_deg=total-k;
	smax=k;
      }
      if (k==smax)
	total_deg=giacmax(total_deg,total-k);
    }
    reverse(res.begin(),res.end());
    return trim(res,0);
  }

  static bool exchange_variables(polynome & p,const index_t & pdeg,polynome & q,const index_t & qdeg,std::vector<int> & permutation){
    if (p.dim<2)
      return false;
    int pd=pdeg.front(),qd=qdeg.front(),res=giacmin(pd,qd),pos=0,tmp; 
    // Find first lowest degree position
    vector<int> vpos(1,0);
    for (int j=1;j<p.dim;++j){
      if ( (tmp=giacmin(pdeg[j],qdeg[j])) <res){
	res=tmp;
	vpos=vector<int>(1,j);
      }
      if (tmp==res)
	vpos.push_back(j);
    }
    int s=int(vpos.size());
    // Same lowest degree, eval p at 0...0 and compare
    // (for ezgcd to find good eval: peval at zero must be non 0
    // and the lcoeff must be as small as possible)
    pos=vpos[0]; 
    if (s>1){
      int plcoeff,qlcoeff;
      gen p0=peval0(p,pos,plcoeff);
      gen q0=peval0(q,pos,qlcoeff);
      for (int j=1;j<s;++j){
	if (!is_zero(p0) && !is_zero(q0) && plcoeff==0)
	  break;
	int ptmplcoeff,qtmplcoeff; 
	gen p0tmp(peval0(p,vpos[j],ptmplcoeff)),q0tmp(peval0(q,vpos[j],qtmplcoeff));
	if ( is_zero(p0tmp) || is_zero(q0tmp) )
	  continue;
	if ( is_zero(p0) || is_zero(q0) || (ptmplcoeff<plcoeff) ){
	  p0=p0tmp;
	  q0=q0tmp;
	  plcoeff=ptmplcoeff;
	  pos=vpos[j];
	}
      }
    }
    if (!pos)
      return false;
    if (debug_infolevel >= 20-p.dim)
      CERR << "Exchange " << CLOCK() << " " << p.dim << " " << p.degree() << " " << p.coord.size() << " " << q.degree() << " " << q.coord.size() << endl;
    permutation=transposition(0,pos,p.dim);
    p.reorder(permutation);
    q.reorder(permutation);
    return true;
  }

  void lcmdeno(const polynome & p, gen & res){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->value.type!=_FRAC)
	continue;
      gen tmp=it->value,tmpden=1;
      while (tmp.type==_FRAC){
	tmpden=tmpden*tmp._FRACptr->den;
	tmp=tmp._FRACptr->num;
      }
      res=lcm(tmpden,res);
    }
  }

  void simplify(polynome & p,polynome & q,polynome & p_gcd){
    if (is_one(q)){
      p_gcd=q;
      return;
    }
    if (is_one(p)){
      p_gcd=p;
      return ;
    }
    if (q.coord.empty()){
      p_gcd=polynome(gen(1),p.dim);
      swap(p_gcd.coord,p.coord);
      return ;
    }
    if (p.coord.empty()){
      p_gcd=polynome(gen(1),p.dim);
      swap(p_gcd.coord,q.coord);
      return ;
    }
    if (!p.dim){
      gen p0=p.coord.front().value,q0=q.coord.front().value;
      gen tmp=simplify(p0,q0);
      p=polynome(p0,0);
      q=polynome(q0,0);
      p_gcd=polynome(tmp,0);
      return;
    }
    if (p==q){
      p_gcd=polynome(gen(1),p.dim);
      swap(p.coord,p_gcd.coord);
      q=p;
      return;
    }
    if (has_constant_variables_gcd(p,q,p_gcd)){
      polynome temp(p.dim);
      exactquotient(p,p_gcd,temp);
      swap(p.coord,temp.coord);
      exactquotient(q,p_gcd,temp);
      swap(q.coord,temp.coord);
      return ;
    }
    p_gcd.coord.clear();
    polynome p_orig(p);
    polynome q_orig(q);
    std::vector<int> permutation;
    index_t pdeg=p.degree(),qdeg=q.degree();
    bool exchanged=exchange_variables(p_orig,pdeg,q_orig,qdeg,permutation);
    gen d_content=1,np_simp=1,nq_simp=1;
    if (gcdheu(p_orig,pdeg,q_orig,qdeg,p,np_simp,q,nq_simp,p_gcd,d_content,false,true)){
      p=p*rdiv(np_simp,d_content,context0);
      q=q*rdiv(nq_simp,d_content,context0);
      if (exchanged){
	p.reorder(permutation);
	q.reorder(permutation);
	p_gcd.reorder(permutation);
	if (!p_gcd.coord.empty() && is_strictly_positive(-p_gcd.coord.front().value,context0)){
	  p_gcd=-p_gcd;
	  p=-p;
	  q=-q;
	}
      }
      p_gcd=p_gcd*d_content;
      return ;
    }
    p_gcd=gcdpsr(p_orig,q_orig);
    polynome tmprem(p_gcd.dim);
    p_orig.TDivRem1(p_gcd,p,tmprem,true);
    q_orig.TDivRem1(p_gcd,q,tmprem,true);
    // If alg. extensions are involved, p and q may now contain fractions
    gen tmpmult(plus_one);
    lcmdeno(p,tmpmult);
    lcmdeno(q,tmpmult);
    p=p*tmpmult;
    q=q*tmpmult;
    if (exchanged){
      p.reorder(permutation);
      q.reorder(permutation);
      p_gcd.reorder(permutation);
    }
    p_gcd=inv(tmpmult,context0)*p_gcd;
    return ;
  }

  polynome simplify(polynome &p,polynome &q){
    polynome p_gcd(p.dim);
    simplify(p,q,p_gcd);
    return p_gcd;
  }

  void gcd(const polynome & p,const polynome & q,polynome & d){
#ifdef TIMEOUT
    control_c();
#endif
    if (ctrl_c || interrupted) { 
      interrupted = true; ctrl_c=false;
      d=monomial<gen>(gensizeerr(gettext("Stopped by user interruption.")),p.dim); 
      return ;
    }
    if (p.coord.empty()){
      d=q;
      return;
    }
    if (q.coord.empty()){
      d=p;
      return ;
    }
    /* if (p==q)
       return p; */
    if (p.dim==0){
      index_t i;
      d=polynome( monomial<gen>(gcd(p.constant_term(),q.constant_term(),context0),i));
      return ;
    }
    d.dim=p.dim;
    d.coord.clear();
    index_t pback=p.coord.back().index.iref(),qback=q.coord.back().index.iref();
    if (!is_zero(pback))
      pback=p.gcddeg();
    if (!is_zero(qback))
      qback=q.gcddeg();
    if (!is_zero(pback) || !is_zero(qback)){
      index_t dback=index_gcd(pback,qback);
      polynome pshift=p.shift(-pback), qshift=q.shift(-qback);
      gcd(pshift,qshift,d);
      if (!is_zero(dback))
	d=d.shift(dback);
      return;
    }
    polynome p_simp(p.dim),q_simp(p.dim);
    index_t pdeg=p.degree(),qdeg=q.degree();
    gen d_content,np_simp,nq_simp;
    if (p.coord.front().value.type==_MOD && gcdheu(p,pdeg,q,qdeg,p_simp,np_simp,q_simp,nq_simp,d,d_content,false,false) ){
      d *= d_content;
      return ;      
    }
    if (has_constant_variables_gcd(p,q,d))
      return ;
    d.coord.clear();
    std::vector<int> permutation;
    polynome p_orig(p),q_orig(q);
    bool exchanged=exchange_variables(p_orig,pdeg,q_orig,qdeg,permutation);
    if (gcdheu(p_orig,pdeg,q_orig,qdeg,p_orig,np_simp,q_orig,nq_simp,d,d_content,false,false)){
      if (exchanged)
	d.reorder(permutation);
      d *= d_content;
      return ;
    }
    d=gcdpsr(p_orig,q_orig);
    if (exchanged)
      d.reorder(permutation);
    // if integers only, should add here gcd using modgcd
    d *= d_content;
    if (!d.coord.empty() && d.coord.front().value.type==_MOD)
      d *= inv(d.coord.front().value,context0);
    return ;
  }

  polynome gcd(const polynome & p,const polynome & q){
    polynome d(p.dim);
    gcd(p,q,d);
    return d;
  }

  void egcdlgcd(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d){
    TegcdTlgcd(p1,p2,u,v,d);
  }

  void egcd(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d){
    if (try_hensel_egcd(p1,p2,u,v,d))
      return;
    polynome g=gcd(p1,p2);
    if (g.lexsorted_degree()){
      egcd(p1/g,p2/g,u,v,d);
      d=g*d;
      return;
    }
    if (p1.dim!=1){
      egcdpsr(p1,p2,u,v,d);
      return;
    }
    gen p1g,p2g;
    int p1t=coefftype(p1,p1g);
    int p2t=coefftype(p2,p2g);
    if (p1t==0 && p2t==0 
	&& p1.lexsorted_degree()>=GIAC_PADIC/2 && p2.lexsorted_degree()>=GIAC_PADIC/2
	){
      if (debug_infolevel>2)
	CERR << CLOCK()*1e-6 << "starting extended gcd degrees " << p1.lexsorted_degree() << " " << p2.lexsorted_degree() << endl;
      vecteur G,p1v,p2v;
      polynome2poly1(g,1,G);
      polynome2poly1(p1,1,p1v);
      polynome2poly1(p2,1,p2v);
      // solve sylvester matrix * []=d
      matrice S=sylvester(p1v,p2v);
      S=mtran(S);
      int add=int(p1v.size()+p2v.size()-G.size()-2);
      vecteur V=mergevecteur(vecteur(add,0),G);
      vecteur U=linsolve(S,V,context0);
      gen D;
      lcmdeno(U,D,context0);
      if (is_positive(-D,context0)){
	D=-D;
	for (iterateur it=U.begin(),itend=U.end();it!=itend;++it)
	  *it=-*it;
      }
      G=multvecteur(D,G);
      V=vecteur(U.begin()+p2v.size()-1,U.end());
      U=vecteur(U.begin(),U.begin()+p2v.size()-1);
      poly12polynome(U,1,u);
      poly12polynome(V,1,v);
      poly12polynome(G,1,d);
      if (0){ // debug code
	polynome u1,v1,d1;
	egcdlgcd(p1,p2,u1,v1,d1);
	if (is_positive(-d1.coord.front().value,context0)){
	  d1=-d1; u1=-u1; v1=-v1;
	}
	if (u!=u1 || v!=v1 || d!=d1)
	  CERR << "err" << endl;
      }
      return;
    }
    if (p1t==_EXT && p2t==_EXT && p1g.type==_EXT && p2g.type==_EXT && *(p1g._EXTptr+1)==*(p2g._EXTptr+1) && (p1g._EXTptr+1)->type==_VECT){
      vecteur G;
      polynome2poly1(g,1,G);
      polynome pmini(2),P1,P2;
      algext_vmin2pmin(*(p1g._EXTptr+1)->_VECTptr,pmini);
      polynome P1n(1),P2n(1);
      if (algext_convert(p1,p1g,P1) && algext_convert(p2,p1g,P2)){
	if (algnorme(P1,pmini,P1n) && algnorme(P2,pmini,P2n)){
	  // first solve norme(p1)*un+norme(p2)*vn=d
	  // then norme(p1)/p1*un*p1+norme(p2)/p2*vn*p2=d
	  // hence u=norme(p1)/p1*un and v=norme(p2)/p2*vn
	  int p1t=coefftype(P1n,p1g);
	  int p2t=coefftype(P2n,p2g);
	  polynome P12g=gcd(P1n,P2n);
	  if (p1t==0 && p2t==0 && P12g.lexsorted_degree()==0){
	    //CERR << P1n % pp1 << endl;
	    //CERR << P2n % pp2 << endl;
	    P1=P1n/p1;
	    P2=P2n/p2;
	    // solve sylvester matrix * []=d
	    matrice S=sylvester(polynome2poly1(P1n,1),polynome2poly1(P2n,1));
	    S=mtran(S);
	    vecteur V(S.size());
	    V[S.size()-1]=G[0];
	    vecteur U(linsolve(S,V,context0));
	    gen D;
	    lcmdeno(U,D,context0);
	    G=multvecteur(D,G);
	    poly12polynome(G,1,d);
	    int p2s=P2n.lexsorted_degree();
	    V=vecteur(U.begin()+p2s,U.end());
	    poly12polynome(V,1,v);
	    v=(v*P2) % p1;
	    U=vecteur(U.begin(),U.begin()+p2s);
	    poly12polynome(U,1,u);
	    u=(u*P1) % p2;
	    //CERR << (operator_times(u,p1,0)+operator_times(v,p2,0))/D << endl;
	    return;
	  }
	}
      }
    }
    if (p1t==_EXT && p2t==0 && p1g.type==_EXT && (p1g._EXTptr+1)->type==_VECT){
      vecteur G,p2v;
      polynome2poly1(g,1,G);
      polynome2poly1(p2,1,p2v);
      polynome pmini(2),P1;
      algext_vmin2pmin(*(p1g._EXTptr+1)->_VECTptr,pmini);
      polynome P1n(1);
      if (algext_convert(p1,p1g,P1)){
	if (algnorme(P1,pmini,P1n)){
	  // first solve norme(p1)*un+p2*v=d
	  // then norme(p1)/p1*un*p1+v*p2=d
	  // hence u=norme(p1)/p1*un 
	  int p1t=coefftype(P1n,p1g);
	  if (p1t==0){
	    P1=P1n/p1;
	    // solve sylvester matrix * []=d
	    matrice S=sylvester(polynome2poly1(P1n,1),p2v);
	    S=mtran(S);
	    vecteur V(vecteur(S.size()));
	    V[S.size()-1]=G[0];
	    vecteur U(linsolve(S,V,context0));
	    gen D;
	    lcmdeno(U,D,context0);
	    G=multvecteur(D,G);
	    poly12polynome(G,1,d);
	    int p2s=int(p2v.size())-1;
	    V=vecteur(U.begin()+p2s,U.end());
	    poly12polynome(V,1,v);
	    U=vecteur(U.begin(),U.begin()+p2s);
	    poly12polynome(U,1,u);
	    u=u*P1;
	    //CERR << (operator_times(u,p1,0)+operator_times(v,p2,0))/D << endl;
	    return;
	  }
	}
      }
    }
    if (p2t==_EXT && p1t==0 && p2g.type==_EXT && (p2g._EXTptr+1)->type==_VECT){
      egcd(p2,p1,v,u,d);
      return;
    }
    egcdlgcd(p1,p2,u,v,d);
    if (is_positive(-d.coord.front().value,context0)){
      d=-d; u=-u; v=-v;
    }
    if (d.coord.front().value.type==_USER){
      gen dinv=inv(d.coord.front().value,context0);
      if (dinv.type==_USER){
	d=dinv*d;
	u=dinv*u;
	v=dinv*v;
      }
    }
  }

  /* Factorization */

  // build a multivariate poly
  // with normal coeff from a multivariate poly with multivariate poly coeffs
  polynome unsplitmultivarpoly(const polynome & p,int inner_dim){
    polynome res(p.dim+inner_dim);
    index_t inner_index,outer_index;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      outer_index=it->index.iref();
      if (it->value.type!=_POLY){
	for (int j=0;j<inner_dim;++j)
	  outer_index.push_back(0);
	res.coord.push_back(monomial<gen>(it->value,outer_index));
      }
      else {
	vector< monomial<gen> >::const_iterator jt=it->value._POLYptr->coord.begin(),jtend=it->value._POLYptr->coord.end();
	for (;jt!=jtend;++jt){
	  inner_index= jt->index.iref();
	  res.coord.push_back(monomial<gen>(jt->value,mergeindex(outer_index,inner_index)));
	}
      }
    }
    return res;
  }
  
  // build from a multivariate poly with normal coeff 
  // a multivariate poly with multivariate poly coeffs
  polynome splitmultivarpoly(const polynome & p,int inner_dim){
    int outer_dim=p.dim-inner_dim;
    index_t cur_outer;
    polynome cur_inner(inner_dim);
    polynome res(outer_dim);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (; it!=itend;++it){
      index_t outer_index(it->index.begin(),it->index.begin()+outer_dim);
      index_t inner_index(it->index.begin()+outer_dim,it->index.end());
      if (outer_index!=cur_outer){
	if (!is_zero(cur_inner))
	  res.coord.push_back(monomial<gen>(cur_inner,cur_outer));
	cur_inner.coord.clear();
	cur_outer=outer_index;
      }
      cur_inner.coord.push_back(monomial<gen>(it->value,inner_index));
    }
    if (!is_zero(cur_inner))
      res.coord.push_back(monomial<gen>(cur_inner,cur_outer));
    return res;
  }

  // if one coeff of p is a polynomial, we must build a multivariate poly
  // with normal coeff from a multivariate poly with multivariate poly coeffs
  static bool poly_factor(const polynome & p, int inner_dim,polynome & p_content,factorization & f,bool with_sqrt,bool complexmode,gen & extra_div){
    // convert p -> pp
    polynome pp(unsplitmultivarpoly(p,inner_dim)),pp_content(p.dim+inner_dim);
    // factorize pp
    // setting with_sqrt to false otherwise problems with mixed num/exact
    // e.g. EIGENVAL([[4,x],[r,p]])
    if (!factor(pp,pp_content,f,false,false,complexmode,1,extra_div)) 
      return false;
    // convert back pp_content -> p_content and each term of f
    p_content=splitmultivarpoly(pp_content,inner_dim);
    factorization::iterator f_it=f.begin(),f_itend=f.end();
    for (;f_it!=f_itend;++f_it)
      f_it->fact=splitmultivarpoly(f_it->fact,inner_dim);
    return true;
  }

  // Yun algorithm in finite field of characteristic n
  // Must be called recursively since it will not detect powers multiple of n
  static void partialsquarefree_fp(const polynome & p,unsigned n,polynome & c,factorization & v){
    v.clear();
    polynome y(p.derivative()),w(p);
    y=smod(y,gen(int(n)));
    c=simplify(w,y);
    // If p=p_1*p_2^2*...*p_n^n, 
    // then c=gcd(p,p')=Pi_{i s.t. i%n!=0} p_i^{i-1} Pi_{i s.t. i%n==0} p_i^i
    // w=p/c=Pi_{i%n>=1} p_i, 
    // y=p'/c=Sum_{i%n>=1} ip_i'*pi_{j!=i, j%n>=1} p_j
    y=y-w.derivative(); 
    y=smod(y,gen(int(n)));
    // y=Sum_{i%n>=2} (i-1)p_i'*pi_{j!=i,j%n!=0} p_j
    int k=1;
    while(!y.coord.empty()){
      // y=sum_{i%n >= k+1} (i-k) p_i' * pi_{j!=i, j>=k} p_j
      polynome g=simplify(w,y);
      if (!Tis_one(g))
	v.push_back(facteur< polynome >(g,k)); 
      // extract one time the factors of multiplicity k mod n
      c=c/w;
      // this push p_k, now w=pi_{i%n>=k+1} p_i and 
      // y=sum_{i%n>=k+1} (i-k) p_i' * pi_{j!=i, j%n>=k+1} p_j
      y=y-w.derivative();
      y=smod(y,gen(int(n)));
      // y=sum_{i%n>=k+1} (i-(k+1)) p_i' * pi_{j!=i, j%n>=k+1} p_j
      k++;
    }
    if (!Tis_one(w))
      v.push_back(facteur< polynome >(w,k));
    // at the end c contains Pi_{i} p_i^{i-(i%n)}
  }
  
  // Yun algorithm in finite field of characteristic n
  // Requires factorization_compress after
  static factorization uncompressed_squarefree_fp(const polynome & p,unsigned n,unsigned exposant){
    factorization res;
    if (Tis_one(p))
      return res;
    polynome c(p.dim);
    partialsquarefree_fp(p,n,c,res);
    if (Tis_one(c))
      return res;
    if (Tis_constant(c)){
      // res.push_back(facteur<polynome>(c,1));
      return res;
    }
    // Check that all first degrees are divisible by n
    // and search for a variable such that one degree is not divisible by n
    vector< monomial<gen> >::const_iterator It=c.coord.begin(),Itend=c.coord.end();
    for (;It!=Itend;++It){
      const index_t &i=It->index.iref();
      for (int j=0;j<c.dim;++j){
	if (i[j]%n){
#ifndef NO_STDEXCEPT
	  if (j==0)
	    setsizeerr(gettext("Square free factor mod bug")+c.print());
#endif
	  // call complete factorization after reordering
	  c.reorder(transposition(0,j,c.dim));
	  factorization cf(squarefree_fp(c,n,exposant));
	  factorization::iterator jt=cf.begin(),jtend=cf.end();
	  for (;jt!=jtend;++jt){
	    jt->fact.reorder(transposition(0,j,c.dim));
	    res.push_back(*jt);
	  }
	  return res;
	}
      }
    }
    polynome b(c.dividealldegrees(n));
    if (exposant!=1){
      // replace all coeffs of b by coeff^(p^(n-1))
      // since in F_{p^n} we have a=(a^(p^(n-1)))^p which is not a^p 
      std::vector< monomial<gen> > ::iterator it=b.coord.begin(),itend=b.coord.end();
      int ntoexposant=pow(n,exposant-1).val;
      for (;it!=itend;++it){
	it->value=pow(it->value,ntoexposant);
      }
    }
    // Note that this is not correct, we must compute gcd of res and resn
    // that have the same residue modulo n
    // Example factor( (x+1)^3*(x-1)^4 %3 )
    // puts x-1 in res and (x^2-1)^3 in c
    // Hence we must call factorization_compress at the end
    factorization resn(uncompressed_squarefree_fp(b,n,exposant));
    factorization::const_iterator it=resn.begin(),itend=resn.end();
    for (;it!=itend;++it){
      res.push_back(facteur<polynome>(it->fact,it->mult*n));
    }
    return res;
  }

  // Compress factorization, required for sqff on finite field
  static void factorization_compress(factorization & sqff_f){
    factorization sqfftmp(sqff_f);
    sqff_f.clear();
    vecteur vtmp;
    int pos;
    factorization::const_iterator it=sqfftmp.begin(),itend=sqfftmp.end();
    for (;it!=itend;++it){
      if ( (pos=equalposcomp(vtmp,it->fact)) ){
	sqff_f[pos-1].mult += it->mult;
      }
      else {
	vtmp.push_back(it->fact);
	sqff_f.push_back(*it);
      }
    }
  }

  bool sqff_ffield_factor(const factorization & sqff_f,int n,environment * env,factorization & f){
    // Now factorize each factor
    factorization::const_iterator it=sqff_f.begin(),itend=sqff_f.end();
    for (;it!=itend;++it){
      // const facteur<polynome> & fp=*it;
      const polynome & itfact = it->fact;
      if (itfact.lexsorted_degree()<=1){
	f.push_back(*it);
	continue;
      }
      if (itfact.dim>1){
	vecteur b(itfact.dim-1);
	polynome Fb,Gb;
	if (find_good_eval(itfact,itfact,Fb,Gb,b,(debug_infolevel>=2))){
	  if (is_zero(b)){
	    factorization sqff_F0(squarefree_fp(Fb,n,1)),v0;
	    if (!sqff_ffield_factor(sqff_F0,n,env,v0))
	      return false;
	    if (try_hensel_lift_factor(itfact,Fb,v0,it->mult,f))
	      continue;
	  }
	  int essaimax=10;
	  for (int essai=0;essai<essaimax;++essai){
	    // try to translate
	    int b0d=itfact.dim;
	    vecteur vb0(b0d),vb1(b0d),lv(b0d);
	    lv[0]=gen("x0",context0);
	    // int hasard=rand()/(RAND_MAX/env->modulo.val);
	    int hasard=0;
	    vb0[0]=sym2r(lv[0]+hasard,lv,context0);
	    vb1[0]=sym2r(lv[0]-hasard,lv,context0);
	    for (int i=1;i<b0d;i++){
	      int hasard1=0; // rand()/(RAND_MAX/env->modulo.val);
	      int hasard2=std_rand()/(RAND_MAX/env->modulo.val);
	      lv[i]=gen("x"+print_INT_(i),context0);
	      vb0[i]=sym2r(lv[i]+hasard1*lv[0]+hasard2,lv,context0);
	      vb1[i]=sym2r(lv[i]-hasard1*lv[0]-hasard2,lv,context0);
	    }
	    gen pb=peval(unmodularize(itfact),vb0,env->modulo,false),num,den;
	    fxnd(pb,num,den);
	    if (num.type!=_POLY){
#ifndef NO_STDEXCEPT
	      setsizeerr();
#endif
	      return false;
	    }
	    polynome ptrans=*num._POLYptr;
	    modularize(ptrans,env->modulo);
	    factorization ftrans,v0;
	    b=vecteur(ptrans.dim-1);
	    find_good_eval(ptrans,ptrans,Fb,Gb,b,(debug_infolevel>=2));
	    if (is_zero(b)){
	      gen extra_div=1;
	      factor(Fb,Gb,v0,false,false,false,1,extra_div);
	      if (is_one(v0.front().fact))
		v0.erase(v0.begin());
	      if (try_hensel_lift_factor(ptrans,Fb,v0,it->mult,ftrans)){
		factorization::const_iterator it=ftrans.begin(),itend=ftrans.end();
		for (;it!=itend;++it){
		  pb=peval(unmodularize(it->fact),vb1,env->modulo,false);
		  fxnd(pb,num,den);
		  if (num.type!=_POLY){
#ifndef NO_STDEXCEPT
		    setsizeerr();
#endif
		    return false;
		  }
		  polynome tmp(*num._POLYptr);
		  modularize(tmp,env->modulo);
		  f.push_back(facteur<polynome>(tmp,it->mult));
		}
		essaimax=0;
	      }
	    }
	  } // end for essai<essaimax
	  if (essaimax==0)
	    continue;
	}
#ifndef NO_STDEXCEPT
	setsizeerr(gettext("Multivariate finite field factorzation expects a unitary polynomial regular at 0. Try to translate with respect to one variable"));
#endif
	return false;
      }
      // convert to vector 
      modpoly Qtry(modularize(
			      unmodularize(itfact)
			      //env->moduloon?unmodularize(itfact):it->fact
			      ,n,env));
      if (is_undef(Qtry)){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false;
      }
      // and call sqff mod factor
      vector< facteur<modpoly> > wf;
      vector<modpoly> qmat;
      // qmatrix(Qtry,env,qmat,0);
      if (!ddf(Qtry,qmat,env,wf)){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false;
      }
      vector<modpoly> w;
      if (!cantor_zassenhaus(wf,qmat,env,w)){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false;
      }
      // put result in f
      vector<modpoly>::const_iterator jt=w.begin(),jtend=w.end();
      gen gtmp;
      for ( ;jt!=jtend;++jt){
	polynome tmp(unmodularize(*jt));
	gtmp=env->moduloon?makemod(tmp,n):tmp;
	if (gtmp.type==_POLY)
	  f.push_back(facteur<polynome>(*gtmp._POLYptr,it->mult));
      }
    }
    // cleanup, set first coeff to 1
    factorization::iterator jt=f.begin(),jtend=f.end();
    for (;jt!=jtend;++jt){
      gen coeff=jt->fact.coord.front().value;
      if (coeff.type==_MOD)
	coeff = inv(coeff,context0);
      jt->fact = coeff * jt->fact;
    }
    return true;
  }

  factorization squarefree_fp(const polynome & p,unsigned n,unsigned exposant){
    factorization res(uncompressed_squarefree_fp(p,n,exposant));
    factorization_compress(res);
    return res;
  }

  // p is primitive wrt the main var
  bool mod_factor(const polynome & p_orig,polynome & p_content,int n,factorization & f){
    if (!is_probab_prime_p(n))
      return false;
    environment env;
    env.moduloon = true;
    env.modulo=n;
    env.pn=n;
    // Check that all coeff are mod
    polynome p(p_orig);
    vector< monomial<gen> >::iterator pit=p.coord.begin(),pitend=p.coord.end();
    for (;pit!=pitend;++pit){
      if (pit->value.type!=_MOD)
	pit->value=makemod(pit->value,n);
      gen & tmp = *(pit->value._MODptr+1);
      if (tmp.type!=_INT_ || tmp.val!=n){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false;
      }
      gen & val = *(pit->value._MODptr);
      if (val.type==_CPLX)
	env.complexe=true;
    }
#ifdef HAVE_LIBNTL
#ifdef HAVE_LIBPTHREAD
    int locked=pthread_mutex_trylock(&ntl_mutex);
#endif // HAVE_LIBPTHREAD
    if (p.dim==1 && !locked){
      bool res=true;
#ifndef NO_STDEXCEPT
      try {
#endif
	vecteur v;
	if (p.dim!=1){
#ifndef NO_STDEXCEPT
	  setsizeerr(gettext("gausspol.cc/mod_factor"));
#endif
	  return false;
	}
	if (p.coord.empty()){
#ifndef NO_STDEXCEPT
	  setsizeerr();
#endif
	  return false;
	}
	int deg=p.lexsorted_degree();
	int curpow=deg;
	v.reserve(deg+1);
	vector< monomial<gen> >::const_iterator ppit=p.coord.begin();
	vector< monomial<gen> >::const_iterator ppitend=p.coord.end();
	for (;ppit!=ppitend;++ppit){
	  int newpow=ppit->index.front();
	  for (;curpow>newpow;--curpow)
	    v.push_back(0);
	  if (ppit->value.type==_INT_)
	    v.push_back(ppit->value);
	  if (ppit->value.type==_MOD)
	    v.push_back(*ppit->value._MODptr);
	  --curpow;
	}
	for (;curpow>-1;--curpow)
	  v.push_back(0);      
	// FIXME NTL works on monic polynomials only!!
	gen v0=v.front();
	if (!is_one(v0)){
	  p_content = p_content*v0;
	  v0=invmod(v0,gen(n));
	  v = operator_times(v,v0,&env);
	}
	if (n==2){
	  NTL::GF2X ntlf(modpoly2GF2X(v));
	  NTL::vec_pair_GF2X_long fres(NTL::CanZass(ntlf,0));
	  int s=fres.length();
	  for (int i=0;i<s;i++){
	    modpoly res( GF2X2modpoly(fres[i].a));
	    f.push_back(facteur<polynome>(*makemod(unmodularize(res),2)._POLYptr,fres[i].b));
	  }
	}
	else {
	  NTL::ZZ_p::init(inttype2ZZ(n));
	  NTL::ZZ_pX ntlf(modpoly2ZZ_pX(v));
	  NTL::vec_pair_ZZ_pX_long fres(NTL::CanZass(ntlf,0));
	  int s=fres.length();
	  for (int i=0;i<s;i++){
	    modpoly res( ZZ_pX2modpoly(fres[i].a));
	    f.push_back(facteur<polynome>(*makemod(unmodularize(res),n)._POLYptr,fres[i].b));
	  }
	}
#ifndef NO_STDEXCEPT
      } catch (std::runtime_error & e){
	res=false;
      }
#endif
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&ntl_mutex);
#endif
      return res;
    } // end !locked
#endif
    // sqff
    factorization sqff_f(squarefree_fp(p,n,1));
    if (!sqff_ffield_factor(sqff_f,n,&env,f))
      return false;
    factorization_compress(f);
    // cleanup cst coeff
    gen coeff(1);
    factorization::iterator it=f.begin(),itend=f.end();
    for (;it!=itend;++it){
      coeff=coeff*pow(it->fact.coord.front().value,it->mult,context0);
    }
    coeff=p.coord.front().value/coeff;
    p_content=coeff*p_content;
    return true;
  }

  // factorization over an algebraic extension
  // the main variable of G is the algebraic extension variable
  // the minimal polynomial of this variable is p_mini
  // G is assumed to be square-free
  // See algorithm 3.6.4 in Henri Cohen book starting at step 3
  // Gtry is non 0 if algfactor has detected a possible factor
  bool algfactor(const polynome & G,const polynome & p_mini,int & k,factorization & f,bool complexmode,gen & extra_div,polynome & Gtry){
    // search sqff norm
    polynome norme(G.dim),temp(G.dim);
    k=-1;
    for (;;) {
      ++k;
      // replace X by X-k*Y in G and _compute resultant
      if (k){
	vecteur v;
	polynome2poly1(G,2,v); // X is the second var
	polynome decal(G.dim-1);
	decal.coord.push_back(monomial<gen>(gen(-k),1,G.dim-1)); // -k*main_var
	v=taylor(v,decal);
	poly12polynome(v,2,temp,G.dim);
	// take remainder otherwise algnorme is too slow 
	temp = temp % p_mini;
	if (!algnorme(temp,p_mini,norme)) 
	  norme=resultant(temp,p_mini).trunc1();
      }
      else {
#if 0 // IMPROVE: find a good criterion to enable!
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " sylvester resultant begin" << endl;
	vecteur Gv(polynome2poly1(G,1)),p_miniv(polynome2poly1(p_mini,1));
	matrice S=sylvester(p_miniv,Gv);
	S=mtran(S);
	gen g=det_minor(S,vecteur(0),false,context0);
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " sylvester resultant end" << endl;
	if (g.type==_POLY)
	  norme=*g._POLYptr;
	else
#endif
	  norme=resultant(G,p_mini).trunc1();
      }
      // check that norme is squarefree, first find inner dimension
      polynome dnorme=norme.derivative();
      int innerdim=0;
      vector< monomial<gen> >::const_iterator ckalg_it=norme.coord.begin(),ckalg_itend=norme.coord.end();
      for (; ckalg_it!=ckalg_itend;++ckalg_it){
	if (ckalg_it->value.type==_POLY){
	  innerdim=ckalg_it->value._POLYptr->dim;
	  break;
	}
      }
      // convert to usual multivariate polynomials
      polynome N(unsplitmultivarpoly(norme,innerdim)),Np(unsplitmultivarpoly(norme.derivative(),innerdim));
      polynome GG=gcd(N,Np);
      if (!GG.lexsorted_degree()){
	break;
      }
      else {
	if (k==0 && innerdim==0 && !Gtry.coord.empty()){
	  factorization ftry=sqff(GG);
	  int extdeg=p_mini.lexsorted_degree();
	  GG=polynome(monomial<gen>(plus_one,0,Gtry.dim));
	  for (int i=0;i<ftry.size();++i){
	    GG=GG*pow(ftry[i].fact,ftry[i].mult / (extdeg-1));
	  }
	  Np=Gtry % GG;
	  if (Np.coord.empty()){
	    Gtry.coord.swap(GG.coord);
	    return true;
	  }
	}
      }
    }
    if (debug_infolevel)
      CERR << CLOCK()*1e-6 << " norme factor begin" << endl;
    bool test=factor(norme,temp,f,true,false,complexmode,1,extra_div);
    if (debug_infolevel)
      CERR << CLOCK()*1e-6 << " norme factor end" << endl;
    Gtry.coord.clear();
    return test;
  }

  void algext_vmin2pmin(const vecteur & v_mini,polynome & p_mini){
    const_iterateur tmp_it=v_mini.begin(),tmp_itend=v_mini.end();
    for (int d=int(tmp_itend-tmp_it)-1;tmp_it!=tmp_itend;++tmp_it,--d){
      if (is_zero(*tmp_it))
	continue;
      p_mini.coord.push_back(monomial<gen>(*tmp_it,d,1,p_mini.dim));
    }
  }

  // add a dimension in front of pcur for algebraic extension variable
  bool algext_convert(const polynome & pcur,const gen & e,polynome & p_y){
    p_y.dim=pcur.dim+1;
    vector< monomial<gen> >::const_iterator p_it=pcur.coord.begin(),p_itend=pcur.coord.end();
    for (;p_it!=p_itend;++p_it){
      if (p_it->value.type!=_EXT){
	p_y.coord.push_back(p_it->untrunc1());
	continue;
      }
      if (*(p_it->value._EXTptr+1)!=*(e._EXTptr+1)){
#ifndef NO_STDEXCEPT
	setsizeerr(gettext("Factor: Only one algebraic extension allowed"));
#endif
	return false;
      }
      // convert the polynomial of the algebraic extension generator
      index_t ii=p_it->index.iref();
      ii.insert(ii.begin(),0);
      p_y=p_y+poly1_2_polynome(*(p_it->value._EXTptr->_VECTptr),p_y.dim).shift(ii);
    }
    // p_y=p_y/Tcontent(p_y);
    return true;
  }

  static bool do_factor(const polynome &p,polynome & p_content,factorization & f,bool isprimitive,bool with_sqrt,bool complexmode,const gen & divide_an_by,gen & extra_div);

  bool has_embedded_poly(const polynome & p){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->value.type==_POLY)
	return true;
    }
    return false;
  }

  bool ext_factor(const polynome &p,const gen & e,gen & an,polynome & p_content,factorization & f,bool complexmode,gen & extra_div){
    if (e._EXTptr->type!=_VECT){
#ifndef NO_STDEXCEPT
      settypeerr(gettext("Modular factorization not yet accessible"));
#endif
      return false;
    }
    gen ip=im(p,context0);
    bool ip0=is_zero(ip);
    if (!ip0 || complexmode){
      gen anreal(an),extra_divreal(extra_div); factorization freal; polynome p_contentreal(p_content);
      if (ip0 && !ext_factor(p,e,anreal,p_contentreal,freal,false,extra_divreal))
	return false;
      // replace i by [1,0]:[1,0,1]
      gen bn=1,the_ext=algebraic_EXTension(makevecteur(1,0),makevecteur(1,0,1));
      gen newp=re(p,context0)+the_ext*ip;
      if (newp.type!=_POLY)
	return false;
      vector< monomial<gen> >::iterator it=newp._POLYptr->coord.begin(),itend=newp._POLYptr->coord.end();
      for (;it!=itend;++it){
	if (it->value.type==_EXT)
	  it->value=ext_reduce(it->value);
	if (it->value.type==_FRAC && it->value._FRACptr->num.type==_EXT)
	  it->value=ext_reduce(it->value._FRACptr->num)/it->value._FRACptr->den;
      }
      lcmdeno(*newp._POLYptr,bn);
      newp=bn*newp;
      for (it=newp._POLYptr->coord.begin(),itend=newp._POLYptr->coord.end();it!=itend;++it){
	if (it->value.type==_EXT){
	  if (the_ext.type==_EXT){
	    common_EXT(*(it->value._EXTptr+1),*(the_ext._EXTptr+1),0,context0);
	    the_ext=ext_reduce(the_ext);
	    if (the_ext.type==_FRAC)
	      the_ext=the_ext._FRACptr->num;
	  }
	  else
	    the_ext=it->value;
	}
      }
      if (e.type==_EXT){
	gen ee=*(e._EXTptr+1);
	common_EXT(ee,*(the_ext._EXTptr+1),0,context0);
	the_ext=ext_reduce(the_ext);
	if (the_ext.type==_FRAC)
	  the_ext=the_ext._FRACptr->num;
      }
      for (it=newp._POLYptr->coord.begin();it!=itend;++it){
	if (it->value.type==_EXT)
	  it->value=ext_reduce(it->value);
      }
      gen bn2=1;
      lcmdeno(*newp._POLYptr,bn2);
      newp=bn2*newp;
      if (the_ext.type!=_EXT)
	return false;
      bool res=ext_factor(*newp._POLYptr,the_ext,an,p_content,f,false,extra_div);
      if (f.size()==freal.size()){
	an=anreal;
	p_content=p_contentreal;
	f=freal;
	extra_div=extra_divreal;
	return true;
      }
      an=an/(bn*bn2);
      return res;
    }
    an=p.coord.front().value;
    factorization fsqff=sqff(p);
    // factorization of each factor of fsqff
    factorization fz;
    factorization::const_iterator it=fsqff.begin(),itend=fsqff.end();
    for (;it!=itend;++it){
      polynome pcur=it->fact;
      gen tmp1(1); lcmdeno(pcur,tmp1);
      pcur=tmp1*pcur;
      // normalize leading term
      if (pcur.coord.front().value.type==_EXT){
	gen pcur0=inv_EXT(pcur.coord.front().value),num,den;
	fxnd(pcur0,num,den);
	pcur=num*pcur;
      }
      int mult=it->mult;
      int d=pcur.lexsorted_degree();
      if (!d)
	continue;
      if (d==1){
	an=rdiv(an,pow(pcur.coord.front().value,gen(mult),context0),context0);
	f.push_back(facteur<polynome>(pcur,mult));
	continue;
      }
      // make a polynomial with 1 more variable: the extension
      vecteur v_mini;
      if ((e._EXTptr+1)->type==_VECT)
	v_mini=*((e._EXTptr+1)->_VECTptr);
      else {
#ifndef NO_STDEXCEPT
	settypeerr(gettext("To be implemented"));
#endif
	return false;
      }
      // const_iterateur v_it,v_itend=v_mini.end();
      polynome p_y(p.dim+1);
      // polynome p_mini(poly12polynome(v_mini));
      // p_mini=p_mini.untrunc(0,2);
      // p_mini.reorder(transposition(0,1,2));
      // polynome p_mini(poly12polynome(v_mini,1,p.dim+1));
      polynome p_mini(p.dim+1);
      algext_vmin2pmin(v_mini,p_mini);
      if (!algext_convert(pcur,e,p_y))
	return false;
#if defined HAVE_LIBPARI && !defined(WIN32) // otherwise factor(x^4-4,sqrt(2)) segfault on cygwin32
      gen coefft;
      if (p_y.dim==2 && p_y.degree(1)>=4 && !complexmode && coefftype(p_y,coefft)<_POLY && coefftype(p_mini,coefft)<_POLY){
	int dim=p_y.dim;
	vecteur lv=makevecteur(y__IDNT_e,x__IDNT_e);
	gen P=r2sym(p_y,lv,context0),Pmini=r2sym(p_mini,lv,context0),res;
	swapgen(lv[0],lv[1]);
	// call changed in pari.cc to nffactor() without nfinit()
	// y^16-2204*y^15+3708732*y^14-2224018932*y^13+7601236038322*y^12-16871353226971624*y^11+11785784895214530912*y^10+14512858706664248868684*y^9-28159800647990521512088725*y^8+22629180037206015783743082216*y^7-4503073664215964343024123764736*y^6-18033250417520024351996412301581172*y^5+36809629124123557233363574360979382082*y^4-26522074490260067527688235446457244110348*y^3+5261395505608051233271161218542638549351612*y^2+1568113413809748536593336025794328431775552560*y+284494252767223281126819740714222484913944245281
	// generated by normal(rootof([[468,-1072,3680,-5865,9664,-7886,2040,1515],[1,-2,7,-10,16,-10,-2,4,1]])+sqrt(rootof([[555066,-1338975,4229538,-7124970,10786458,-9150474,798516,2168328],[1,-2,7,-10,16,-10,-2,4,1]])))
	// or A:=[[1,0,0],[rootof([[1,0,0],[1,-1,1,-1,1]]),rootof([[-1,1,-1,1],[1,-1,1,-1,1]]),rootof([[1,0,0],[1,-1,1,-1,1]])],[rootof([[1,0,1,-1],[1,-1,1,-1,1]]),rootof([[1,0],[1,-1,1,-1,1]]),0]];jordan(A);
	// takes forever for pari nfinit0
	if (pari_nffactor(P,Pmini,lv,res,context0) && res.type==_VECT){
	  vecteur v=*res._VECTptr;
	  unsigned j=0;
	  lv=vecteur(1,vecteur(1,lv[0]));
	  for (;j<v.size();++j){
	    res=v[j];
	    if (res.type!=_VECT || res._VECTptr->size()!=2)
	      break;
	    int mult=res._VECTptr->back().val;
	    res=sym2r(res._VECTptr->front(),lv,context0);
	    if (res.type==_FRAC)
	      res=res._FRACptr->num;
	    if (res.type!=_POLY)
	      continue;
	    // ? unitarize res
	    *res._POLYptr=*res._POLYptr/res._POLYptr->coord.front().value;
	    f.push_back(facteur<polynome>(*res._POLYptr,mult));
	  }
	  if (j==v.size()){ //adjust an
	    factorization::const_iterator f_it=f.begin(),f_itend=f.end();
	    for (;f_it!=f_itend;++f_it){
	      an=rdiv(an,pow(f_it->fact.coord.front().value,gen(f_it->mult),context0),context0);
	    }
	    continue;// return true;
	  }
	}
      }
#endif
      int k;
      polynome Gtry; 
      // polynome Gtry(pcur); 
      // does not work if trying to factor a rational poly over an extension
      if (!algfactor(p_y,p_mini,k,fz,false,extra_div,Gtry))
	return false;
      if (!Gtry.coord.empty()){
	// pcur is square free, multiplicities in ftry and fz should be 1
	polynome ptmp(pcur/Gtry);
	gen antmp;
	if (!ext_factor(ptmp,e,antmp,p_content,fz,false,extra_div))
	  return false;
	factorization ftry;
	polynome Gcontent(pcur.dim);
	if (!do_factor(Gtry,Gcontent,ftry,true,false,false,1,extra_div))
	  return false;
	for (int i=0;i<ftry.size();++i)
	  fz.push_back(ftry[i]);
	factorization::const_iterator f_it=fz.begin(),f_itend=fz.end();
	for (;f_it!=f_itend;++f_it){
	  pcur=f_it->fact;
	  // unitarize pcur
	  pcur=pcur/pcur.coord.front().value;
	  f.push_back(facteur<polynome>(pcur,mult));
	}
	continue;
      }
      factorization::const_iterator f_it=fz.begin(),f_itend=fz.end();
      if (f_itend-f_it==1){ // irreducible (after sqff)
	an=rdiv(an,pow(pcur.coord.front().value,gen(mult),context0),context0);
	f.push_back(facteur<polynome>(pcur,mult));
      }
      else {
	gen bn(1);
	polynome pcopy(pcur);
	bool embedded_poly=has_embedded_poly(p_mini);
	for (;f_it!=f_itend;++f_it){
	  if (k){ // shift f_it->fact
	    //vecteur v=polynome2poly1(f_it->fact);
	    vecteur v; polynome2poly1(f_it->fact,1,v);
	    vecteur decalv(2,zero);
	    decalv[0]=k; 
	    gen decal=algebraic_EXTension(decalv,v_mini);
	    v=taylor(v,decal);
	    // pcur=poly12polynome(v); 
	    poly12polynome(v,1,pcur,f_it->fact.dim);
	    if (embedded_poly)
	      pcur=gcd(pcur,pcopy); 
	    else {
	      // fix it for normal(sqrt(a*pi)/(2*sqrt(a)*sqrt(pi)));
	      // dcur might have denominators inside
	      if (f_it+1==f_itend){
		pcur=pcopy;
	      }
	      else {
		polynome dcur=simplify(pcur,pcopy);
		dcur.coord.swap(pcur.coord);
		gen t;
		lcmdeno(pcopy,t);
	      }
	    }
	  }
	  else {
	    if (embedded_poly)
	      pcur=gcd(f_it->fact,p);
	    else {
	      if (f_it+1==f_itend){
		pcur=pcopy;
	      }
	      else {
		polynome fcopy(f_it->fact);
		polynome dcur=simplify(fcopy,pcopy);
		dcur.coord.swap(pcur.coord);
		gen t;
		lcmdeno(pcopy,t);
	      }
	    }
	  }
	  // unitarize pcur instead of computing bn
	  pcur=pcur/pcur.coord.front().value;
	  // bn=bn*pow(pcur.coord.front().value,gen(mult));
	  f.push_back(facteur<polynome>(pcur,mult));
	}
	an=rdiv(an,bn,context0);
      }
    } // end for (;it!=itend;)    
    return true;   
  }


  static void addtov(const polynome & tmp,vectpoly & v,bool with_sqrt,bool complexmode){
    if (!with_sqrt || tmp.lexsorted_degree()!=2 || tmp.dim>1)
      v.push_back(tmp);
    else {
      vecteur w=polynome2poly1(tmp,1);
      gen a=w.front(),b=w[1],c=w[2];
      gen delta=4*a*c-b*b,deltaf;
      if ( !complexmode && has_evalf(delta,deltaf,1,context0) && is_positive(deltaf,context0)){
	v.push_back(tmp);
	return;
      }
      gen b_over_2=rdiv(b,plus_two,context0);
      if (b_over_2.type!=_FRAC){
	delta=a*c-b_over_2*b_over_2;
	gen un=plus_one;
	if (is_positive(delta,context0)){
	  un=cst_i;
	  delta=-delta;
	}
	vecteur vv(makevecteur(plus_one,rdiv(algebraic_EXTension(makevecteur(un,b_over_2),makevecteur(plus_one,zero,delta)),a,context0)));
	v.push_back(poly12polynome(vv,1));
	vv=makevecteur(1,algebraic_EXTension(makevecteur(-un,b_over_2),makevecteur(plus_one,zero,delta))/a);
	v.push_back(a*poly12polynome(vv,1));
      }
      else {
	gen un=plus_one;
	if (is_positive(delta,context0)){
	  un=cst_i;
	  delta=-delta;
	}
	vecteur vv(makevecteur(plus_one,rdiv(algebraic_EXTension(makevecteur(un,b),makevecteur(plus_one,zero,delta)),2*a,context0)));
	v.push_back(poly12polynome(vv,1));
	vv=makevecteur(1,rdiv(algebraic_EXTension(makevecteur(-un,b),makevecteur(plus_one,zero,delta)),2*a,context0));
	v.push_back(a*poly12polynome(vv,1));
      }
    }
  }
  
  bool cfactor(const polynome & p, gen & an,factorization & f,bool with_sqrt,gen &extra_div){
    an=p.coord.front().value;
    if (has_num_coeff(p) && p.dim==1){
      vectpoly w;
      if (!sqfffactor(p,w,false,false,true))
	return false;
      vectpoly::const_iterator itw=w.begin(),itwend=w.end();
      for (;itw!=itwend;++itw)
	f.push_back(facteur<polynome>(*itw,1));
      return true;
    }
    factorization fsqff=sqff(p);
    // factorization of each factor of fsqff
    factorization fz;
    factorization::const_iterator it=fsqff.begin(),itend=fsqff.end();
    for (;it!=itend;++it){
      polynome pcur=it->fact;
      int mult=it->mult;
      int d=pcur.lexsorted_degree();
      if (!d)
	continue;
      if (d==1){
	an=rdiv(an,pow(pcur.coord.front().value,gen(mult),context0),context0);
	f.push_back(facteur<polynome>(pcur,mult));
	continue;
      }
      // make a polynomial with 1 more variable (i)
      polynome p_y(im(pcur).untrunc1(1)+re(pcur).untrunc1());
      polynome p_mini(p_y.dim);
      p_mini.coord.push_back(monomial<gen>(1,1,p_y.dim));
      p_mini=p_mini.multiplydegrees(2);
      p_mini.coord.push_back(monomial<gen>(1,0,p_y.dim));
      int k;
      polynome Gtry;
      if (!algfactor(p_y,p_mini,k,fz,false,extra_div,Gtry))
	return false;
      factorization::const_iterator f_it=fz.begin(),f_itend=fz.end();
      for (;f_it!=f_itend;++f_it){
	if (k){ // shift f_it->fact
	  vecteur v;
	  polynome2poly1(f_it->fact,1,v);
	  gen decal=polynome(gen(0,k),f_it->fact.dim-1);
	  v=taylor(v,decal);
	  poly12polynome(v,1,pcur,f_it->fact.dim);
	  pcur=gcd(pcur,p);
	}
	else
	  pcur=gcd(f_it->fact,p);
	an=rdiv(an,pow(pcur.coord.front().value,gen(mult),context0),context0);
	vectpoly tmpv;
	addtov(pcur,tmpv,with_sqrt,true);
	f.push_back(facteur<polynome>(tmpv[0],mult));
	if (tmpv.size()==2)
	  f.push_back(facteur<polynome>(tmpv[1],mult));
      }
    }
    return true;   
  }

  // factorize a square-free univariate polynomial
  bool sqfffactor(const polynome &p, vectpoly & v,bool with_sqrt,bool test_composite,bool complexmode){
    if (debug_infolevel>5)
      CERR << "Begin sqfffactor" << p << endl;
    // test if p has a numeric coeff
    if (has_num_coeff(p)){
      vecteur w=polynome2poly1(p,1);
      w=proot(w); 
      if (is_undef(w))
	return false;
      const_iterateur it=w.begin(),itend=w.end();
      polynome res(1),res2(1);
      res.coord.push_back(monomial<gen>(1,index_t(1,1)));
      res2.coord.push_back(monomial<gen>(1,index_t(1,2)));
      for (;it!=itend;++it){
	polynome copie(1);
	gen impart=im(*it,context0);
	if (!complexmode && !is_zero(impart) && (it+1)!=itend ){
	  copie = res2;
	  gen repart=re(*it,context0);
	  copie.coord.push_back(monomial<gen>(-2*repart,index_t(1,1)));
	  copie.coord.push_back(monomial<gen>(repart*repart+impart*impart,index_t(1,0)));
	  ++it;
	}
	else {
	  copie = res;
	  if (!is_zero(*it))
	    copie.coord.push_back(monomial<gen>(-*it,index_t(1,0)));
	}
	v.push_back(copie);
      }
      return true;
    }
    int d;
    // special speedup for x^n +/- 1
    if (p.coord.size()==2 && p.coord.front().value==1 && is_zero(p.coord.back().index.iref())){
      d=p.lexsorted_degree();
      if (p.coord.back().value==-1){
	// product of cyclotomic(n) where n divides d
	gen dd=idivis(d,context0);
	if (dd.type==_VECT){
	  const_iterateur it=dd._VECTptr->begin(),itend=dd._VECTptr->end();
	  for (;it!=itend;++it){
	    polynome tmp=poly12polynome(cyclotomic(it->val),1);
	    addtov(tmp,v,with_sqrt,complexmode);
	  }
	  return true;
	}
      }
      if (p.coord.back().value==1){
	// product of cyclotomic(n) where n divides 2d and does not divide d
	gen dd=_minus(makesequence(idivis(2*d,context0),idivis(d,context0)),context0);
	if (dd.type==_VECT){
	  const_iterateur it=dd._VECTptr->begin(),itend=dd._VECTptr->end();
	  for (;it!=itend;++it){
	    polynome tmp=poly12polynome(cyclotomic(it->val),1);
	    addtov(tmp,v,with_sqrt,complexmode);
	  }
	  return true;
	}
      }
    }
    // find the gcd of the degrees of *it
    if (test_composite)
      d=p.gcddeg(0);
    else
      d=1;
    if (debug_infolevel>5)
      CERR << "sqfffactor gcddeg " << d << endl;
    if (d<=1){
      // find linear factors now!
      environment * env=new environment;
      polynome temp(1);
      int ithprime=1;
      int bound=linearfind(p,env,temp,v,ithprime);
      if (bound==0){
#ifndef NO_STDEXCEPT
	setsizeerr();
#endif
	return false; 
      }
      // if degree of temp<=3, we are finished since not irred -> one fact
      // has degree 1 (hence found previously)
      int tempdeg=temp.lexsorted_degree();
      if (debug_infolevel>5)
	CERR << "sqfffactor after linearfind " << temp << endl;
      if (tempdeg<bound){
	if (tempdeg)
	  addtov(temp,v,with_sqrt,complexmode);
      }
      else {
	// find other factors
	vectpoly w;
	int signe=1;
	if (is_positive(-temp.coord.front()))
	  signe=-1;
	if (!factorunivsqff(temp,env,w,ithprime,(debug_infolevel>1?debug_infolevel:0),MODFACTOR_PRIMES)){
#ifndef NO_STDEXCEPT
	  setsizeerr();
#endif
	  return false;
	}
	vectpoly::const_iterator itw=w.begin(),itwend=w.end();
	for (;itw!=itwend;++itw){
	  addtov(*itw,v,with_sqrt,complexmode);
	}
	if (signe==-1)
	  v.back()=-v.back();
      }
      delete env;
    }
    else { // gcddeg!=1, take the largest divisor of d
      //if (p.coord.size()==2){
      gen dd(d);
      vector<nfactor> nv(trivial_n_factor(dd));
      if (dd==gen(1))
	d=nv[nv.size()-1].fact.to_int();
      else
	d=dd.to_int();
      //}
      // use x^d as new variable, divide every degree by d
      if (d==p.lexsorted_degree()) 
	return sqfffactor(p,v,with_sqrt,false,complexmode);
      polynome q(p.dividedegrees(d));
      vectpoly w;
      // IMPROVE: if we factor allowing 2nd order poly roots
      // we could factor bisquare poly
      // BUT that requires converting the roots to internal form
      if (!sqfffactor(q,w,false,true,complexmode))
	return false;
      vectpoly::const_iterator itw=w.begin(),itwend=w.end();
      for (;itw!=itwend;++itw){
	if (!sqfffactor(itw->multiplydegrees(d),v,with_sqrt,false,complexmode))
	  return false;
      }
    }
    return true;
  }

  factorization sqff(const polynome &p ){
    factorization f=Tsqff_char0<gen>(p);
    // take care of cst coefficients
    if (!p.coord.empty()){
      gen p0=p.coord.front().value;
      for (unsigned i=0;i<f.size();++i){
	p0=p0/pow(f[i].fact.coord.front().value,f[i].mult,context0);
      }
      if (!is_one(p0)){
	if (f.empty() || f[0].mult!=1)
	  f.insert(f.begin(),facteur<polynome>(polynome(p0,p.dim),1));
	else
	  f[0].fact = p0*f[0].fact;
      }
    }
    return f;
  }

  static bool sqff_evident_primitive(const polynome & pp,factorization & f,bool with_sqrt,bool complexmode){
    // first square-free factorization

#if 0 // Cette version ne marche pas it->fact plus bas renvoie un vecteur vide.. ou quelquechose comme ca..
   const factorization & sqff_f = has_num_coeff(pp)?factorization(1,facteur< polynome >(pp,1)):sqff(pp);
#else // celle la, plus ancienne, marche... 
    factorization sqff_f;
    if (has_num_coeff(pp))
      sqff_f.push_back(facteur< polynome >(pp,1));
    else
      sqff_f=sqff(pp);
#endif
    f.clear();
    if (pp.dim!=1){
      f=sqff_f;
      return true;
    }
    factorization::const_iterator it=sqff_f.begin();
    factorization::const_iterator itend=sqff_f.end();
    vectpoly v;
    for (;it!=itend;++it){
      v.clear();
      if (!sqfffactor(it->fact,v,with_sqrt,true,complexmode))
	return false;
      f.reserve(f.size()+v.size());
      vectpoly::const_iterator itv=v.begin(),itvend=v.end();
      for (;itv!=itvend;++itv)
	f.push_back(facteur<polynome>(*itv,it->mult));
    }
    return true;
  }

  bool sqff_evident(const polynome & p,factorization & f,bool with_sqrt,bool complexmode){
    // first make p primitive
    polynome pp=p/lgcd(p);
    return sqff_evident_primitive(pp,f,with_sqrt,complexmode);
  }

  /* Factorization of sqff unitary polynomial with variables in reverse order
     Return number of factors, -1 if not successfull
     Might be called if polynomial is not unitary, but there is no
     proof that unlimited tries succeed in this case */
  static int unitaryfactor(polynome & unitaryp, vectpoly & f,bool with_sqrt,bool complexmode){
    int dd=unitaryp.degree(unitaryp.dim-1);
    if (!dd)
      return 0; // unitaryp is cst w.r.t. x
    if (dd==1){
      f.push_back(unitaryp);
      unitaryp=unitaryp/unitaryp;
      return 1;
    }
    if (unitaryp.dim==1){
      factorization ff;
      if (!sqff_evident(unitaryp,ff,with_sqrt,complexmode))
	return -1;
      if (f.empty())
	f.reserve(ff.size());
      factorization::const_iterator ff_it=ff.begin(),ff_end=ff.end();
      for (;ff_it!=ff_end;++ff_it)
	f.push_back(ff_it->fact);
      return int(ff.size());
    }
    ppz(unitaryp); // remove content
    gen n_2(2),np,n_73794(73794),n_27011(27011);
    polynome quo(unitaryp.dim),rem(unitaryp.dim);
    if (!listmax(unitaryp,np))
      return 0;
    gen x0(n_2*np+n_2); 
    int ntry=0;
    while (unitaryp.lexsorted_degree()){
      ntry++;
      if (ntry>GCDHEU_MAXTRY)
	return 0;
      // find evaluation point such that evaluated poly is sqff 
      // pz is unitary w.r.t. last var hence has same degree and is primitive
      polynome pz(unitaryp(x0));
      while (gcd(pz.derivative(),pz).lexsorted_degree()){
	x0=x0+gen(1);
	pz=unitaryp(x0); 
      }
      // factorization of pz
      vectpoly fz;
      int nf=unitaryfactor(pz,fz,with_sqrt,complexmode);
      if (nf==-1)
	return nf;
      if (!nf)
	return int(f.size());
      if (nf==1) {
	f.push_back(unitaryp);
	unitaryp=polynome(monomial<gen>(gen(1),0,unitaryp.dim));
	return int(f.size());
      }
      // factorization fz into factorization f
      vectpoly::iterator f_it=fz.begin(),f_itend=fz.end();
      for (;f_it!=f_itend;++f_it){
	*f_it=pzadic(*f_it,x0);
	// try division, each factor found is necessarily irreducible
	if ( (unitaryp.TDivRem1(*f_it,quo,rem)) && (rem.coord.empty())){
	  unitaryp=quo;
	  f.push_back(*f_it);
	}
      }
      x0=iquo(x0*n_73794,n_27011); // for the next try, if necessary
    }
    // factorize the cst term
    vectpoly fz;
    polynome tmp(unitaryp.trunc1());
    int nf=unitaryfactor(tmp,fz,with_sqrt,complexmode);
    if (nf==-1)
      return nf;
    if (!nf)
      return int(f.size());
    if (nf==1){
      f.push_back(unitaryp);
      unitaryp=polynome(monomial<gen>(gen(1),0,unitaryp.dim));
      return int(f.size());
    }
    vectpoly::iterator f_it=fz.begin(),f_itend=fz.end();
    for (;f_it!=f_itend;++f_it)
      f.push_back(f_it->untrunc1());
    unitaryp=polynome(monomial<gen>(gen(1),0,unitaryp.dim));
    return int(f.size());
  }
  
  void unitarize(const polynome &pcur, polynome &unitaryp, polynome & an){
    an=firstcoeff(pcur).trunc1();
    if (is_one(an)){
      unitaryp=pcur;
      return;
    }
    monomial_v::const_iterator it=pcur.coord.begin();
    monomial_v::const_iterator itend=pcur.coord.end();
    polynome curanpow(pow(an,0));
    int savpow=it->index.front();
    unitaryp=pow(polynome(monomial<gen>(gen(1),1,pcur.dim)),savpow);
    savpow--;
    int newpow;
    Tnextcoeff<gen>(it,itend); // ++it;
    for (;it!=itend;){
      newpow=it->index.front();
      polynome an_1=Tnextcoeff<gen>(it,itend);
      curanpow=curanpow*pow(an,savpow-newpow);
      unitaryp=unitaryp+(an_1*curanpow).untrunc1(newpow);
      savpow=newpow;
    }
  }

  polynome ununitarize(const polynome & unitaryp, const polynome & an){
    if (is_one(an))
      return unitaryp;
    monomial_v::const_iterator it=unitaryp.coord.begin();
    monomial_v::const_iterator itend=unitaryp.coord.end();
    int curpow;
    polynome ppush(unitaryp.dim);
    for (;it!=itend;){
      curpow=it->index.front();
      polynome an_1=Tnextcoeff<gen>(it,itend);
      ppush=ppush+(an_1*pow(an,curpow)).untrunc1(curpow);
    }
    return ppush/lgcd(ppush);
  }

  static bool do_factor_hensel(const polynome &p,polynome& p_primit,polynome & p_content,factorization & f,bool isprimitive,bool with_sqrt,bool complexmode,const gen & divide_an_by,gen & extra_div,bool hensel_only){
    if (p.dim==1){
      // FIXME: if p_primit has num coeffs, we must check the leading coeff
      // and adjust p_content
      if (has_num_coeff(p_primit)){
	gen an=p_primit.coord.front().value;
	p_content=an*p_content;
	vector< monomial<gen> >::iterator it=p_primit.coord.begin(),itend=p_primit.coord.end();
	for (;it!=itend;++it)
	  it->value=evalf(it->value/an,1,context0);
      }
      return sqff_evident_primitive(p_primit,f,with_sqrt,complexmode);
    }
    // extract powers of indeterminates
    index_t mindeg=p_primit.coord.back().index.iref();
    vector< monomial<gen> >::const_iterator pt=p_primit.coord.begin(),ptend=p_primit.coord.end();
    for (;pt!=ptend;++pt){
      mindeg=index_min(mindeg,pt->index.iref());
      if (is_zero(mindeg))
	break;
    }
    // square-free factorization
    factorization fsqff;
    if (!is_zero(mindeg)){
      p_primit=p_primit.shift(-mindeg);
      fsqff=sqff(p_primit);
      for (int i=0;i<p.dim;++i){
	if (mindeg[i])
	  f.push_back(facteur<polynome>(monomial<gen>(1,i+1,p.dim),mindeg[i]));
      }
    }
    else
      fsqff=sqff(p_primit);
    // factorization of each factor of fsqff
    /*
      First of course square free factorization, then try a few (2) random values for all indeterminates except the first one, for a fast check of irreducibility. If not, lift the equality in one variable 
      P(x,0,...0)=product P_i(x,0,...,0)
      more precisely, one must take care of the leading coefficient, hence lift
      P*lcoeff(P)^(#nfactors-1)=product P_i
      where in P_i the leading coefficient is replaced by lcoeff(P).
      In order to avoid densification (if lcoeff(P) has many coefficients), I make a bivariate factorization, this way instead of using lcoeff(P) for every P_i, I'm using a divisor of lcoeff(P). It's a little different from what is describe in the thesis of Bernardin (I don't make polynomial rational reconstructions for example).
      There is also a try to do sparse factorization before.
      If Hensel lift does not work (for example P(x,0...0) loose degree or has non square-free factors), I'm using "heuristic factorization", i.e. evaluate P at x>=2 linfnorm(P)+2, factor this polynomial, reconstruct factors (using x as basis and symmetric remainder) and check division, if remainder is 0 an irreducible factor has been found, otherwise try with a larger value of x.
      The corresponding code slices are in ezgcd.cc try_sparse_factor and try_hensel_lift_factor, and do_factor in gausspol.cc.
    */
    factorization::const_iterator it=fsqff.begin(),itend=fsqff.end();
    for (;it!=itend;++it){
      polynome pcur=it->fact;
      int mult=it->mult;
      if (has_num_coeff(pcur)){
	f.push_back(facteur<polynome>(pcur,mult));
	continue;
      }
      // try first 2 good evaluations in case pcur is irreducible
      vecteur b(pcur.dim-1),b0;
      factorization v,v0;
      polynome Fb(1),Gb(1),F0;
      int essai,nfactbound=RAND_MAX;
      for (essai=0;essai<2;++essai){
	if (essai)
	  b=vranm(pcur.dim-1,0,0); // find another random point
	find_good_eval(pcur,pcur,Fb,Gb,b,(debug_infolevel>=2));
	factor(Fb,Gb,v,false,false,false,1,extra_div);
	if (!essai){
	  F0=Fb;
	  v0=v;
	  b0=b;
	}
	if ( (v.size()==1) && (v.front().mult==1) )
	  break;
	factorization::const_iterator it=v.begin(),itend=v.end();
	int nfact=0;
	for (;it!=itend;++it)
	  nfact += it->mult;
	if (0 && essai && nfactbound>nfact){
	  nfactbound=nfact;
	  F0=Fb;
	  v0=v;
	  b0=b;
	}
	nfactbound=giacmin(nfactbound,nfact);
      }
      if (essai<2){
	f.push_back(facteur<polynome>(pcur,mult));
	continue;
      }
      // check if pcur is a homogeneous polynomial
      if (sum_degree(pcur.coord.back().index)){
	int xdeg=sum_degree(pcur.coord.back().index);
	vector< monomial<gen> >::iterator it=pcur.coord.begin(),itend=pcur.coord.end();
	for (;it!=itend;++it){
	  if (sum_degree(it->index)!=xdeg){
	    break;
	  }
	}
	if (it==itend){
	  // set x[j]=x[0]*x[j] for all vars, divide by x[0]^xdeg, 
	  // remove old x[0] variable
	  polynome pcurh(pcur.trunc1());
	  pcurh.tsort();
	  // factor it
	  polynome pcur_cont;
	  factorization pcur_f,ppcur_f;
	  do_factor(pcurh,pcur_cont,pcur_f,false,with_sqrt,complexmode,1,extra_div);
	  // factorize (recursivly) pcur_cont
	  for (int innerdim=1;innerdim<pcur.dim && !pcur_cont.coord.empty() && sum_degree(pcur_cont.coord.front().index);++innerdim){
	    polynome pp=pcur_cont.trunc1();
	    do_factor(pp,pcur_cont,ppcur_f,false,with_sqrt,complexmode,1,extra_div);
	    for (unsigned i=0;i<ppcur_f.size();++i){
	      polynome tmp(ppcur_f[i].fact.untrunc1());
	      for (int j=1;j<innerdim;++j){
		tmp=tmp.untrunc1();
	      }
	      pcur_f.push_back(facteur<polynome>(tmp,ppcur_f[i].mult));
	    }
	  }
	  if (pcur_f.size()==1){
	    f.push_back(facteur<polynome>(pcur,mult));
	    continue;
	  }
	  // for each factor multiply by x[0]^degree in x[j] of factor, 
	  // and set x[j]=x[j]/x[0], and put in v
	  for (unsigned i=0;i<pcur_f.size();++i){
	    polynome & P=pcur_f[i].fact;
	    it=P.coord.begin(); itend=P.coord.end();
	    int jdeg=0;
	    for (;it!=itend;++it)
	      jdeg=giacmax(jdeg,sum_degree(it->index));
	    it=P.coord.begin();
	    for (;it!=itend;++it){
	      index_t idx=it->index.iref();
	      idx.insert(idx.begin(),jdeg-sum_degree(idx));
	      it->index=idx;
	    }
	    P.tsort();
	    P.dim=pcur.dim;
	    // adjust sign
	    if (is_strictly_positive(-P.coord.front().value,context0))
	      P=-P;
	    f.push_back(facteur<polynome>(P,mult));
	  }
	  continue;
	} // end homogeneous poly
      } // end if (sum_degrees(...)
      if (try_sparse_factor(pcur,v,mult,f))
	continue;
      if (try_sparse_factor_bi(pcur,mult,f))
	continue;
      /* Try Hensel lift factorization */
      bool hensel_factored=false;
      for (unsigned hensel_try=0;hensel_try<5;++hensel_try){
	gen lm;
	if (!listmax(p,lm))
	  lm=100;
	if (p.dim>2 && !is_zero(b0) && is_greater(lm,10,context0)){
	  int b0d=int(b0.size());
	  // search a smaller b
	  for (int essai=0;essai<3;++essai){
	    for (int i=0;i<b0d;++i){
	      //b[i]=1+iquo(rand(),RAND_MAX/3);
	      b[i]=1+iquo(giac_rand(context0),RAND_MAX/4);
	    }
	    if (find_good_eval(pcur,pcur,Fb,Gb,b,(debug_infolevel>=2))){
	      b0=b;
	      break;
	    }
	  }
	  // translate
	  vecteur vb0(b0d+1),vb1(b0d+1),lv(b0d+1);
	  lv[0]=gen("x0",context0);
	  vb0[0]=sym2r(lv[0],lv,context0);
	  vb1[0]=vb0[0];
	  for (int i=1;i<=b0d;i++){
	    lv[i]=gen("x"+print_INT_(i),context0);
	    vb0[i]=sym2r(lv[i]+b0[i-1],lv,context0);
	    vb1[i]=sym2r(lv[i]-b0[i-1],lv,context0);
	  }
	  gen pb=peval(pcur,vb0,0,false),num,den;
	  fxnd(pb,num,den);
	  if (num.type!=_POLY){
#ifndef NO_STDEXCEPT
	    setsizeerr();
#endif
	    return false;
	  }
	  polynome ptrans=*num._POLYptr;
	  factorization ftrans;
	  b=vecteur(b.size());
	  find_good_eval(ptrans,ptrans,Fb,Gb,b,(debug_infolevel>=2));
	  if (is_zero(b)){
	    factor(Fb,Gb,v0,false,false,false,1,extra_div);
	    if (int(v0.size())<2*nfactbound && try_hensel_lift_factor(ptrans,Fb,v0,mult,ftrans)){
	      factorization::const_iterator it=ftrans.begin(),itend=ftrans.end();
	      for (;it!=itend;++it){
		pb=peval(it->fact,vb1,0,false);
		fxnd(pb,num,den);
		if (num.type!=_POLY){
#ifndef NO_STDEXCEPT
		  setsizeerr();
#endif
		  return false;
		}
		f.push_back(facteur<polynome>(*num._POLYptr,it->mult));
	      }
	      hensel_factored=true;
	      break; // break loop on hensel_try
	    }
	  }
	}
      } // end loop on hensel_try
      if (hensel_factored)
	continue;
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " hensel lift factor begin" << endl;
      if (is_zero(b0) && int(v0.size())<2*nfactbound && try_hensel_lift_factor(pcur,F0,v0,mult,f)){
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " hensel lift factor success" << endl;
	continue;
      }
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " hensel lift factor failure" << endl;
      if (hensel_only)
	return false;
      /* Now try heuristic factorization then call unitaryfactor
	 on each found factor */
      vectpoly fz;
      pcur.reverse();
      unitaryfactor(pcur,fz,false,false);
      pcur.reverse();
      vectpoly::iterator f_it=fz.begin(),f_itend=fz.end();
      for (;f_it!=f_itend;++f_it){
	f_it->reverse();
	// if an!=1, P(Y)=P(a_n*X) and divide by content
	f.push_back(facteur<polynome>(*f_it,mult));
      }
      if (!is_one(pcur)){
	/* now make polynomial unitary with respect to last var
	   P(x)=a_n*x^n+...+a_0, x=X/a_n,
	   P(x)=Q(X)=1/a_n^(n-1) * [ X^n+ a_{n-1}*a_n X^(n-1)+...+ a_0*a_n^{n-1}]
	*/
	fz.clear();
	polynome unitaryp(p.dim),an(p.dim-1);
	unitarize(pcur,unitaryp,an);
	// rewrite variables in inverted order
	unitaryp.reverse();
	// and call unitaryfactor
	if (unitaryfactor(unitaryp,fz,false,false)==-1)
	  return false;
	// rewrite back variables in initial order for each polynomial
	// and push back factorization
	f_it=fz.begin(),f_itend=fz.end();
	for (;f_it!=f_itend;++f_it){
	  f_it->reverse();
	  // if an!=1, P(Y)=P(a_n*X) and divide by content
	  f.push_back(facteur<polynome>(ununitarize(*f_it,an),mult));
	}
      }
    }
    // adjust lcoeff
    if (!p_content.coord.empty()){
      gen lc(1);
      for (it=f.begin(),itend=f.end();it!=itend;++it){
	lc=lc*pow(it->fact.coord.front().value,it->mult,context0);
      }
      p_content = p.coord.front().value/(p_content.coord.front().value*lc)*p_content;
    }
    return true;
  }

  static bool do_factor(const polynome &p,polynome & p_content,factorization & f,bool isprimitive,bool with_sqrt,bool complexmode,const gen & divide_an_by,gen & extra_div){
    f.clear();
    if (p.coord.empty()){
      p_content=p;
      return true;
    }
    polynome p_primit(p.dim);
    if (!isprimitive){
      p_content=lgcd(p);
      if (is_strictly_positive(-p.coord.front().value,context0) && is_strictly_positive(p_content.coord.front().value,context0)) 
	p_content=-p_content;
      // p_primit=p/p_content;
      polynome unused;
      if (!divrem1(p,p_content,p_primit,unused,0,false)){
	divrem1(p,p_content,p_primit,unused,0,true);
	gen tmp(1);
	lcmdeno(p_primit,tmp);
	p_primit = tmp*p_primit;
	extra_div=extra_div*tmp;
      }
    }
    else
      p_primit=p;
    p_content /= divide_an_by;
    if (is_one(p_primit))
      return true;
    if (p_primit.lexsorted_degree()==1){
      f.push_back(facteur<polynome>(p_primit,1));
      return true;
    }
    if (!is_zero(im(divide_an_by,0))) // || !is_zero(im(p_primit,context0)))
      complexmode=true;    
    if (!p_content.coord.empty()){
      if (!complexmode && !is_zero(im(p_content.coord.front().value,0)))
	complexmode=true;
      // check if one coeff is an alg. extension (only one is allowed)
      if (p_content.coord.front().value.type==_EXT){
	gen an;
	if (!ext_factor(p_primit,p_content.coord.front().value,an,p_content,f,complexmode,extra_div))
	  return false;
	p_content=an*p_content;
	return true;
      }
    }
    if (divide_an_by.type==_EXT){
      gen an;
      if (!ext_factor(p_primit,divide_an_by,an,p_content,f,complexmode,extra_div))
	return false;
      p_content=an*p_content;
      return true;
    }
    vector< monomial<gen> >::const_iterator ckalg_it=p.coord.begin(),ckalg_itend=p.coord.end();
    for (; ckalg_it!=ckalg_itend;++ckalg_it){
      if (p.dim>1 && (ckalg_it->value.type==_DOUBLE_ || 
		      ckalg_it->value.type==_REAL ||
		      ckalg_it->value.type==_FLOAT_ ||
		      (ckalg_it->value.type==_CPLX && (ckalg_it->value._CPLXptr->type==_DOUBLE_ || (ckalg_it->value._CPLXptr+1)->type==_DOUBLE_))
		      ) ){
	// FIXME Prime terminal output
	// CERR << "Factorization of multivariate polynomial with approx. coeffs not implemented. Please try with exact coefficients" << endl;
#if 1 // otherwise integrate(cos(x/2)**2/(x+sin(x)),x); failure
	return false;
#endif
      }
      if (ckalg_it->value.type==_USER){
	ckalg_it->value._USERptr->polyfactor(p_primit,f);
	return true;
      }
      if (ckalg_it->value.type==_EXT){
	// Try Hensel lift for multivariate factorization if extension of degree>=3
	if (p_primit.dim>1 && (ckalg_it->value._EXTptr+1)->type==_VECT 
	    //&& (ckalg_it->value._EXTptr+1)->_VECTptr->size()>3
	    ){
	  if (do_factor_hensel(p,p_primit,p_content,f,isprimitive,with_sqrt,complexmode,divide_an_by,extra_div,true))
	    return true;
	}
	gen an;
	if (!ext_factor(p_primit,ckalg_it->value,an,p_content,f,complexmode,extra_div))
	  return false;
	if (with_sqrt){
	  factorization fz(f);
	  f.clear();
	  factorization::const_iterator f_it=fz.begin(),f_itend=fz.end();
	  for (;f_it!=f_itend;++f_it){
	    vectpoly tmpv;
	    addtov(f_it->fact,tmpv,with_sqrt,complexmode);
	    f.push_back(facteur<polynome>(tmpv[0],f_it->mult));
	    if (tmpv.size()==2)
	      f.push_back(facteur<polynome>(tmpv[1],f_it->mult));
	  }
	}
	p_content=an*p_content;
	return true;
      }
    }
    // check if polynomial coeff are embedded inside p
    for (ckalg_it=p.coord.begin(); ckalg_it!=ckalg_itend;++ckalg_it){
      if (ckalg_it->value.type==_POLY)
	return poly_factor(p,ckalg_it->value._POLYptr->dim,p_content,f,with_sqrt,complexmode,extra_div);
    }
    // check if p has modular coeff
    for (ckalg_it=p.coord.begin(); ckalg_it!=ckalg_itend;++ckalg_it){
      if (ckalg_it->value.type==_MOD){
	if ((ckalg_it->value._MODptr+1)->type!=_INT_)
	  return false;
	return mod_factor(p_primit,p_content,(ckalg_it->value._MODptr+1)->val,f);
      }
    }
    // check if one coefficient is complex
    if (complexmode || !is_zero(im(p))){
      gen an;
      bool res=cfactor(p_primit,an,f,with_sqrt,extra_div);
      if (!res)
	return false;
      p_content=an*p_content;
      return true;
    }
    return do_factor_hensel(p,p_primit,p_content,f,isprimitive,with_sqrt,complexmode,divide_an_by,extra_div,false);
  }

  bool polynome_less(const polynome & f,const polynome & g){
    unsigned fs=unsigned(f.coord.size()),gs=unsigned(g.coord.size());
    if (fs!=gs)
      return fs<gs;
    if (!gs)
      return false;
    vector< monomial<gen> > ::const_iterator it=f.coord.begin(),jt=g.coord.begin(),itend=f.coord.end();
    for (;it!=itend;++it,++jt){
      if (it->index!=jt->index)
	return !(jt->index <= it->index);
      if (it->value!=jt->value){
	gen a=evalf_double(it->value,1,context0),b=evalf_double(jt->value,1,context0);
	if (a.type==_DOUBLE_ && b.type==_DOUBLE_)
	  return a._DOUBLE_val<b._DOUBLE_val;
	return it->value.islesscomplexthan(jt->value);
      }
    }
    return false;
  }

  struct facteur_polynome_sort_t {
    facteur_polynome_sort_t(){}
    bool operator ()(const facteur<polynome> & f,const facteur<polynome> & g){
      return polynome_less(f.fact,g.fact);
    }
  };

  bool factor(const polynome &p,polynome & p_content,factorization & f,bool isprimitive,bool with_sqrt,bool complexmode,const gen & divide_an_by,gen & extra_div){
    bool res=do_factor(p,p_content,f,isprimitive,with_sqrt,complexmode,divide_an_by,extra_div);
#if 1 // ndef EMCC // does not work for emscripten, don't know why...
    // sort f
    sort(f.begin(),f.end(),facteur_polynome_sort_t());
#endif
    return res;
  }

  bool operator < (const polynome & f,const polynome & g){
    return polynome_less(f,g);
  }
  
  bool operator < (const facteur<polynome> & f,const facteur<polynome> & g){
    const polynome & fp=f.fact;
    const polynome & gp=g.fact;
    return fp<gp;
  }

  bool is_positive(const polynome & p){
    if (p.coord.empty())
      return true;
    return (is_positive(p.coord.front().value,context0));
  }

  void partfrac(const polynome & num_, const polynome & den_, const vector< facteur< polynome > > & v_ , vector < pf <gen> > & pfdecomp, polynome & ipnum, polynome & ipden,bool rational ){
    polynome num(num_),den(den_);
    vector< facteur< polynome > > v(v_);
    vector< facteur< polynome > >::iterator jt=v.begin(),jtend=v.end()
;
    for (;jt!=jtend;++jt){
      gen tmp(1);
      lcmdeno(jt->fact,tmp);
      if (!is_one(tmp)){
	jt->fact=tmp*jt->fact;
	tmp=pow(tmp,jt->mult,context0);
	num=tmp*num;
	den=tmp*den;
      }
    }
    // check that all mult == 1 and deg<=2
    // later will split in 2 parts, 1st having this property
    vector< facteur< polynome > >::const_iterator it=v.begin(),itend=v.end();
    pfdecomp.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->mult!=1 || it->fact.lexsorted_degree()>2)
	break;
    }
    if (!rational || it!=itend){
      Tpartfrac(num,den,v,pfdecomp,ipnum,ipden);
      return;
    }
    // conditions met
    // compute integral part
    int dim=num.dim;
    polynome rem(dim);
    num.TPseudoDivRem(den,ipnum,rem,ipden);
    // for degree==1 : N/(P*Q)= (N mod P)/(Q mod P) / P + ...
    // for P of degree==2, P=a*x^2+b*x+c, D=P*Q, N mod P = n1*x+n2
    // Q mod P = q1*x+q2, then N/(D*P)=v/P+...
    // where v=1/(q2*(a*q2-b*q1)+c*q1^2)*(n2*(a*q2-q1*b)+q1*n1*c+(-q1*n2+q2*n1)*a*x)
    it = v.begin();
    if (itend-it==1){
      polynome nums(rem), dens(den*ipden);
      TsimplifybyTlgcd(nums,dens);
      pfdecomp.push_back(pf<gen>(nums,dens,it->fact,it->mult));   
      return;
    }
    polynome nmodp(dim),nmodpden(dim),q(dim),qmodp(dim),qmodpden(dim),quo(dim),tmp;
    for (;it!=itend;++it){
      const polynome & P =it->fact;
      if (P.lexsorted_degree()==0) continue;
      rem.TPseudoDivRem(P,quo,nmodp,nmodpden); // nmodpden*num=P*quo+nmodp -> num mod P = nmodp/nmodpden
      nmodpden=nmodpden*ipden;
      den.TDivRem(P,q,tmp,false);
      q.TPseudoDivRem(P,quo,qmodp,qmodpden); // qmodpden*q=P*quo+qmodp -> q mod P = qmodp/qmodpden
      if (P.lexsorted_degree()==1){
	simplify(qmodpden,nmodpden);
	simplify(nmodp,qmodp);
	pfdecomp.push_back(pf<gen>(nmodp*qmodpden,qmodp*nmodpden*P,P,1));
	continue;
      }
      vecteur P1,N1,Q1,Vnum(2),Vden(1);
      polynome2poly1(P,1,P1);
      polynome2poly1(qmodp,1,Q1);
      polynome2poly1(nmodp,1,N1);
      gen a=P1.front(),b=P1[1],c=P1.back();
      gen q1,q2,n1,n2,aq2bq1;
      if (Q1.size()==2){
	q1=Q1.front(); q2=Q1.back();
      }
      else
	q2=Q1.front();
      aq2bq1=a*q2-b*q1;
      if (N1.size()==2){
	n1=N1.front(); n2=N1.back();
      }
      else
	n2=N1.front();
      Vnum[0]=(-q1*n2+q2*n1)*a;
      Vnum[1]=n2*aq2bq1+q1*n1*c;
      Vden[0]=q2*aq2bq1+c*q1*q1;
      polynome vnum(dim),vden(dim);
      poly12polynome(Vnum,1,vnum,dim);
      poly12polynome(Vden,1,vden,dim);
      simplify(qmodpden,nmodpden);
      simplify(vnum,vden);
      pfdecomp.push_back(pf<gen>(vnum*qmodpden,vden*nmodpden*P,P,1));
    }
  }

  // Input a,b,c,u,v,d such that a*u+b*v=d, 
  // Output u,v,C such that a*u+b*v=c*C
  void egcdtoabcuv(const tensor<gen> & a,const tensor<gen> &b, const tensor<gen> &c, tensor<gen> &u,tensor<gen> &v, tensor<gen> & d, tensor<gen> & C){
    if (Tis_constant(c)){
      C=d;
      u *= c.coord.front().value;
      v *= c.coord.front().value;
      return;
    }
    tensor<gen> d0(Tfirstcoeff(d));
    int m=c.lexsorted_degree();
    int n=d.lexsorted_degree();
    assert(m>=n); // degree of c must be greater than degree of d
    C=Tpow(d0,m-n+1);
    tensor<gen> coverd(a.dim),temp(a.dim);
    (c*C).TDivRem1(d,coverd,temp);
    assert(temp.coord.empty()); // division of c by d must be exact
    // now multiply a*u+b*v=d by coverd -> a*u*coverd+b*v*coverd=c*d0pow
    u *= coverd; // u=u*coverd;
    v *= coverd; // v=v*coverd;
    m=u.lexsorted_degree();
    n=b.lexsorted_degree();
    if (m<n)
      return;
    // then reduces the degree of u, a*u+b*v=c*C
    d0=Tpow(Tfirstcoeff(b),m-n+1); 
    C *= d0; // C=C*d0;
    // now a*u*d0+b*v*d0=c*C
    (u*d0).TDivRem1(b,temp,u); // replace u*d0 -> temp*b+u
    // a*b + b*(a*temp+v*d0) = c*C
    v=a*temp+v*d0;
    return ;
  }

  // Bézout identity
  // given p and q, find u and v s.t. u*p+v*q=d where d=gcd(p,q) using PSR algo
  // Iterative algorithm to find u and d, then q=(d-u*p)/v
  void egcdpsr(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d){
    assert(p1.dim==p2.dim);
    // set auxiliary polynomials g and h to 1
    tensor<gen> g(gen(1),p1.dim);
    tensor<gen> h(g);
    tensor<gen> a(p1.dim),b(p1.dim),q(p1.dim),r(p1.dim);
    const tensor<gen> cp1=Tlgcd(p1);
    const tensor<gen> cp2=Tlgcd(p2);
    bool genswapped=false;
    if (p1.lexsorted_degree()<p2.lexsorted_degree())
      genswapped=true;
    // initializes a and b to p1, p2
    const tensor<gen> pp1=Tis_one(cp1)?p1:p1/cp1;
    const tensor<gen> pp2=Tis_one(cp2)?p2:p2/cp2;
    if (genswapped){
      a=pp2;
      b=pp1;
    }
    else {
      a=pp1;
      b=pp2;
    }
    // initializes ua to 1 and ub to 0, the coeff of u in ua*a+va*b=a
    tensor<gen> ua(gen(1),p1.dim), ub(p1.dim),ur(p1.dim);
    tensor<gen> b0pow(p1.dim);
    // loop: ddeg <- deg(a)-deg(b), 
    // genDivRem: b0^(ddeg+1)*a = bq+r 
    // hence ur <- ua*b0^(ddeg+1)-q*ub verifies
    // ur*a+vr*b=r
    // a <- b, b <- r/(g*h^ddeg), ua <- ub and ub<- ur/(g*h^ddeg)
    // g <- b0, h <- b0^(m-n) * h / h^ddeg
    for (;;){
      int n=b.lexsorted_degree();
      int m=a.lexsorted_degree();
      if (!n){ // b is cst !=0 hence is the gcd, ub is valid
	break;
      }
      int ddeg=m-n;
      const tensor<gen> b0=Tfirstcoeff(b);
      // b0pow=genpow(b0,ddeg+1);
      // (a*b0pow).genDivRem1(b,q,r); // division works always
      a.TPseudoDivRem(b,q,r,b0pow);
      // if r is 0 then b is the gcd and ub the coeff
      if (r.coord.empty())
	break;
      // COUT << ua*b0pow << std::endl << q*ub << std::endl ;
      (ua*b0pow).TSub(q*ub,ur); // ur=ua*b0pow-q*ub;
      // COUT << ur << std::endl;
      swap(a,b); // a=b
      const tensor<gen> temp=Tpow(h,ddeg);
      // now divides r by g*h^(m-n), result is the new b
      r.TDivRem1(g*temp,b,q); // q is not used anymore
      swap(ua,ub); // ua=ub
      ur.TDivRem1(g*temp,ub,q);
      // COUT << (b-ub*p1) << "/" << p2 << std::endl;
      // new g=b0 and new h=b0^(m-n)*h/temp
      if (ddeg==1) // the normal case, remainder deg. decreases by 1 each time
	h=b0;
      else // not sure if it's better to keep temp or divide by h^(m-n+1)
	(Tpow(b0,ddeg)*h).TDivRem1(temp,h,q);
      g=b0;
    }
    // ub is valid and b is the gcd, vb=(b-ub*p1)/p2 if not Tswapped
    // vb is stored in ua
    // COUT << ub << std::endl;
    if (genswapped){
      (b-ub*pp2).TDivRem1(pp1,ua,r);
      ua *= cp2; // ua=ua*cp2;
      ub *= cp1; // ub=ub*cp1;
      b *= cp1; b *= cp2; // b=b*cp1*cp2;
    }
    else {
      (b-ub*pp1).TDivRem1(pp2,ua,r);
      ua *= cp1; // ua=ua*cp1;
      ub *= cp2; // ub=ub*cp2;
      b *= cp1; b *= cp2; // b=b*cp1*cp2;
    }
    // final simplifications
    q.coord.clear();
    Tlgcd(b,q); // q=Tlgcd(b);
    Tlgcd(ua,q);
    Tlgcd(ub,q);
    b.TDivRem1(q,d,r,true);  // d=b/Tlgcd
    if (genswapped){
      ub.TDivRem1(q,v,r,true); // v=ub/Tlgcd
      ua.TDivRem1(q,u,r,true); // u=ua/Tlgcd
    }
    else {
      ub.TDivRem1(q,u,r,true); // u=ub/Tlgcd
      ua.TDivRem1(q,v,r,true); // v=ua/Tlgcd
    }
  }

  pf<gen> intreduce_pf(const pf<gen> & p_cst, vector< pf<gen> > & intdecomp ,bool residue){
    assert(p_cst.mult>0);
    if (p_cst.mult==1)
      return p_cst;
    pf<gen> p(p_cst);
    tensor<gen> fprime=p.fact.derivative();
    tensor<gen> d(fprime.dim),u(fprime.dim),v(fprime.dim),C(fprime.dim);
    tensor<gen> resnum(fprime.dim);
    gen resden(1),dengcd(1);
    egcdpsr(p.fact,fprime,u,v,d); // f*u+f'*v=d
    tensor<gen> usave(u),vsave(v);
    int initial_mult=p.mult-1;
    gen currentden=p.den/pow(p.fact,p.mult); // p.den.coord.front().value/pow(p.fact.coord.front().value,p.mult,context0);
    p.den=tensor<gen>(monomial<gen>(1,p.fact.dim));
    while (p.mult>1){
      egcdtoabcuv(p.fact,fprime,p.num,u,v,d,C);
      p.mult--;
      if (currentden.type==_POLY)
	currentden=gen(p.mult)*C*(*currentden._POLYptr);
      else
	currentden=gen(p.mult)*C*currentden;
      p.num=u*gen(p.mult)+v.derivative();
      if (!residue){ // resnum/resden + (-v*p.den)/currentden -> resnum/resden
	dengcd=simplify3(resden,currentden);
	if (currentden.type==_POLY)
	  resnum=resnum*(*currentden._POLYptr);
	else
	  resnum=resnum*currentden;
	if (resden.type==_POLY)
	  resnum=resnum-(*resden._POLYptr)*v*p.den;
	else
	  resnum=resnum-resden*v*p.den;
	resden=dengcd*resden*currentden;
	currentden = dengcd*currentden; // restore currentden
	p.den=p.den*p.fact;
      }
      // simplify from time to time
      if (p.mult%5 ==1){
	gen gn=lgcd(p.num);
	gen gn1=simplify3(gn,currentden);
	if (gn1.type==_POLY)
	  p.num = p.num / *gn1._POLYptr;
	else
	  p.num/=gn1;
      }
      if (p.mult==1)
	break;
      u=usave;
      v=vsave;
    }
    if (!residue){
      p.den=resden.type==_POLY?(*resden._POLYptr)*p.den:resden*p.den;
      TsimplifybyTlgcd(resnum,p.den);
      intdecomp.push_back(pf<gen>(resnum,p.den,p.fact,initial_mult));
    }
    p.den=(currentden.type==_POLY)?(*currentden._POLYptr)*p.fact:currentden*p.fact;
    return pf<gen>(p);
  }

  vecteur vector_of_polynome2vecteur(const vectpoly & v){
    vecteur res;
    vectpoly::const_iterator it=v.begin(),itend=v.end();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(*it);
    return res;
  }

  vecteur sturm_seq(const polynome & p,polynome & cont){
    vectpoly v;
    Tsturm_seq<gen>(p,cont,v);
    return vector_of_polynome2vecteur(v);
  }

  /* FAST PEVAL */
  /*
  // accumulate partial evaluation in polynomial (it,itend)
  // cur_index and nvar indicate the number of first identical eval.variables
  // vsize is the total number of eval.variables
  polynome peval(vector< monomial<gen> >::const_iterator & it,const vector< monomial<gen> >::const_iterator & itend,const vector< vecteur > & power_of_xi,index_t & cur_index,int nvar,int vsize,int var0){
    polynome res(var0);
    for (;;){
      if (it==itend)
	return res;
      const index_t & it_t=it->index.iref();
      index_t::const_iterator it_tt=it_t.begin();
      index_t::const_iterator it_ttend=it_tt+nvar,cur_it=cur_index.begin();
      for (;it_tt!=it_ttend;++cur_it,++it_tt){
	if (*it_tt!=*cur_it)
	  return res;
      }
      // same index beginning
      if (nvar==vsize){
	res.coord.push_back(monomial<gen>(it->value,index_t(it_t.begin()+vsize,it_t.end())));
	++it;
	if (debug_infolevel)
	  CERR << "// " << itend-it << " monomials remain " << CLOCK() << endl;
      }
      else { // go one level deeper
	cur_index.push_back(*(it->index.begin()+nvar));
	const gen & g=power_of_xi[nvar][cur_index.back()];
	if (debug_infolevel)
	  CERR << "// Enter level " << nvar+1 << " " << CLOCK() << endl;
	if (g.type==_POLY)
	  res=res+(*g._POLYptr)*peval(it,itend,power_of_xi,cur_index,nvar+1,vsize,var0);
	else
	  res=res+g*peval(it,itend,power_of_xi,cur_index,nvar+1,vsize,var0);
	cur_index.pop_back();
	if (debug_infolevel)
	  CERR << "// Back to level " << nvar << " " << CLOCK() << endl;
      }
    }
  }

  gen peval(const polynome & p,const vecteur & v){
    int pdim=p.dim,vsize=v.size(),var0=pdim-vsize;
    if (var0<0)
      setsizeerr(gettext("Too much substitution variables"));
    polynome res(var0);
    if (p.coord.empty())
      return res;
    vecteur vnum,vden;
    gen vn,vd;
    vnum.reserve(vsize);
    vden.reserve(vsize);
    for (int i=0;i<vsize;++i){
      fxnd(v[i],vn,vd);
      vnum.push_back(vn);
      vden.push_back(vd);
    }
    vector< vecteur > power_of_xi;
    power_of_xi.reserve(pdim);
    index_t pdeg(p.degree());
    index_t deg(pdeg.begin(),pdeg.begin()+vsize);
    // compute thet table of powers
    gen global_deno(plus_one);
    for (int i=0;i<vsize;++i){
      if (debug_infolevel)
	CERR << "// Computing powers of " << i << "th var " << CLOCK() << endl;
      // compute powers of ith component num and deno
      vecteur va(1,plus_one),vb(1,plus_one);
      gen vn(vnum[i]),vd(vden[i]),vnpow(plus_one),vdpow(plus_one);
      int degi=deg[i];
      for (int j=1;j<=degi;++j){
	vnpow=vnpow*vn;
	va.push_back(vnpow);
	vdpow=vdpow*vd;
	vb.push_back(vdpow);
      }
      // multiply in reverse order for the denominators
      for (int j=0;j<=degi;++j)
	va[j]=va[j]*vb[degi-j];
      global_deno=global_deno*vb.back();
      power_of_xi.push_back(va);
    }
    // we are now ready to evaluate the polynomial
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t cur_index;
    return fraction(peval(it,itend,power_of_xi,cur_index,0,vsize,var0),global_deno);
  }
  */

  // a*b+c*d
  gen foisplus(const polynome & a,const polynome & b,const polynome & c,const polynome & d){
    if (debug_infolevel >= 20-a.dim)
      CERR << "foisplus begin " << CLOCK() << endl;
#ifndef NO_TEMPLATE_MULTGCD
    index_t da=a.degree(),db=b.degree(),dc=c.degree(),dd=d.degree(),de(a.dim);
    double ans=1;
    for (int i=0;i<a.dim;++i){
      de[i]=giacmax(da[i]+db[i]+1,dc[i]+dd[i]+1);
      ans = ans*unsigned(de[i]);
      if (ans/RAND_MAX>RAND_MAX)
	break;
    }
    if (ans<=RAND_MAX){
      ref_polynome * res = new ref_polynome(a.dim);
      vector< T_unsigned<gen,unsigned> > pa,pb,p,pc,pd;
      convert<gen,unsigned>(a,de,pa);
      convert<gen,unsigned>(b,de,pb);
      smallmult<gen,unsigned>(pa,pb,p,0,100);
      convert<gen,unsigned>(c,de,pc);
      convert<gen,unsigned>(d,de,pd);
      smallmult<gen,unsigned>(pc,pd,pa,0,100);
      smalladd<gen,unsigned>(p,pa,pb);
      convert<gen,unsigned>(pb,de,res->t);
      if (debug_infolevel >= 20-a.dim)
	CERR << "foisplus end " << CLOCK() << endl;
      // CERR << res->t-(a*b+c*d) << endl;
      return res;
    }
    if (ans/RAND_MAX<RAND_MAX){
      ref_polynome * res = new ref_polynome(a.dim);
      vector< T_unsigned<gen,ulonglong> > pa,pb,p,pc,pd;
      convert<gen,ulonglong>(a,de,pa);
      convert<gen,ulonglong>(b,de,pb);
      smallmult<gen,ulonglong>(pa,pb,p,0,100);
      convert<gen,ulonglong>(c,de,pc);
      convert<gen,ulonglong>(d,de,pd);
      smallmult<gen,ulonglong>(pc,pd,pa,0,100);
      smalladd<gen,ulonglong>(p,pa,pb);
      convert<gen,ulonglong>(pb,de,res->t);
      // CERR << res->t << endl << (a*b+c*d) << endl;
      return res;
    }
#endif
    return a*b+c*d;
  }

  gen foisplus(const gen & a,const gen & b,const gen & c,const gen & d){
    if (a.type==_POLY && b.type<_POLY  &&c.type==_POLY && d.type<_POLY){      
      polynome res(a._POLYptr->dim);
      if (b==1){
	if (d==1)
	  a._POLYptr->TAdd(*c._POLYptr,res);
	else {
	  if (0 && c.ref_count()==1){
	    *c._POLYptr *= d;
	    a._POLYptr->TAdd(*c._POLYptr,res);
	  } else {
	    polynome cd(*c._POLYptr);
	    cd *= d;
	    a._POLYptr->TAdd(cd,res);
	  }
	}
	return res;
      }
      if (0 && a.ref_count()==1){
	*a._POLYptr *= b;
	return foisplus(a,1,c,d);
      }
      polynome ab(*a._POLYptr);
      ab *= b;
      if (d==1)
	ab.TAdd(*c._POLYptr,res);
      else {
	polynome cd(*c._POLYptr);
	cd *= d;
	ab.TAdd(cd,res);
      }
      return res;
    }
    return a*b+c*d;
  }

  static gen pevaladd(const gen & aa,const gen & bb){
    if (debug_infolevel>40)
      CERR << "pevaladd begin " << CLOCK() << endl;
    gen res=aa+bb;
    if (debug_infolevel>40)
      CERR << "pevaladd end " << CLOCK() << endl;
    return res;
  }

  static gen pevalmul(const gen & aa,const gen & bb,const gen & m){
    if (debug_infolevel>40)
      CERR << "pevalmul begin " << CLOCK() << endl;
    gen res;
    if (!is_zero(m))
      res=smod(aa,m)*bb;
    else
      res=aa*bb;
    /*
    if ( (aa.type!=_FRAC) || (bb.type!=_FRAC) )
      return aa*bb;
    const Tfraction<gen> & a(*aa._FRACptr);
    const Tfraction<gen> & b(*bb._FRACptr);
    gen res(Tfraction<gen>(a.num*b.num,a.den*b.den));
    */
    if (debug_infolevel>40)
      CERR << "pevalmul end " << CLOCK() << endl;
    return res;
  }

  // Horner like evaluation
  // m != 0 for modular evaluation
  static gen peval(vector< monomial<gen> >::const_iterator & it,const vector< monomial<gen> >::const_iterator & itend,const vecteur & nums,const vecteur & dens,const index_t & deg,index_t & cur_index,int nvar,int vsize,int var0,const gen & m){
    if (it==itend)
      return zero;
    if (nvar==vsize){
      polynome res(var0);
      for (;;){
	if (it==itend)
	  return res;
	index_t::const_iterator it_tt=it->index.begin();
	index_t::const_iterator it_ttend=it_tt+nvar,cur_it=cur_index.begin();
	for (;it_tt!=it_ttend;++cur_it,++it_tt){
	  if (*it_tt!=*cur_it){
	    return res;
	  }
	}
	// same main variables powers, accumulate constants
	res.coord.push_back(monomial<gen>(it->value,index_t(it->index.begin()+vsize,it->index.end())));
	++it;
      if (debug_infolevel>40)
	CERR << "// " << itend-it << " monomials remain " << CLOCK() << endl;
      }
    } 
    // we are not at the deepest level
    gen res,tmp1,tmp2;
    int prev_power=0,cur_power=deg[nvar];
    const gen & gn=nums[nvar];
    const gen & gd=dens[nvar];
    gen cur_gd(plus_one);
    if (is_zero(gn)){ // if gn=0 we just discard monomials
      for (;;){
	if (it==itend)
	  return zero;
	index_t::const_iterator it_tt=it->index.begin();
	index_t::const_iterator it_ttend=it_tt+nvar,cur_it=cur_index.begin();
	for (;it_tt!=it_ttend;++cur_it,++it_tt){
	  if (*it_tt!=*cur_it)
	    return zero;
	}
	if (!*it_tt) // break at first monomial with power = 0 at this index
	  break;
	++it;
      }
      cur_index.push_back(0);
      gen res(pow(gd,prev_power));
      if (!is_zero(m))
	res=smod(res,m);
      res=res*peval(it,itend,nums,dens,deg,cur_index,nvar+1,vsize,var0,m);
      cur_index.pop_back();
      return res;
    } // end gn==0
    for (;;){
      prev_power=cur_power;
      if (it==itend){
	if (!prev_power)
	  return res;
	else
	  return pevalmul(pow(gn,prev_power),res,m);
	// return pow(gn,prev_power)*res;
      }
      cur_power=*(it->index.begin()+nvar);
      // same powers for the beginning indices? (always true the first time)
      index_t::const_iterator it_tt=it->index.begin();
      index_t::const_iterator it_ttend=it_tt+nvar,cur_it=cur_index.begin();
      for (;it_tt!=it_ttend;++cur_it,++it_tt){
	if (*it_tt!=*cur_it)
	  return pevalmul(pow(gn,prev_power),res,m);
	// return pow(gn,prev_power)*res;
      }
      // Yes: go one level deeper
      tmp1=pevalmul(pow(gn,prev_power-cur_power),res,m);
      res=zero;
      cur_index.push_back(cur_power);
      if (debug_infolevel>40)
	CERR << "// Enter level " << nvar+1 << " " << CLOCK() << " ^ " << prev_power-cur_power << endl;
      tmp2=peval(it,itend,nums,dens,deg,cur_index,nvar+1,vsize,var0,m);
      cur_index.pop_back();
      if (debug_infolevel>40)
	CERR << "// Back to level " << nvar << " " << CLOCK() << endl; 
      cur_gd=cur_gd*pow(gd,prev_power-cur_power);
      if (!is_zero(m))
	cur_gd=smod(cur_gd,m);
      // res=pevalmul(pow(gn,prev_power-cur_power),res)+cur_gd*peval(it,itend,nums,dens,deg,cur_index,nvar+1,vsize,var0);
      res=pevaladd(tmp1,cur_gd*tmp2);
      if (!is_zero(m))
	res=smod(res,m);
      tmp1=zero;
      tmp2=zero;
    }
  }
  
  static void smallmult(const std::vector< int_unsigned > & v1,const std::vector< int_unsigned > & v2,std::vector< int_unsigned > & v,int reduce,int possible_size=100){
#ifdef HASH_MAP_NAMESPACE
    typedef HASH_MAP_NAMESPACE::hash_map<unsigned,int> hash_prod ;
    hash_prod produit(possible_size);
    // COUT << "hash " << CLOCK() << endl;
#else
    typedef std::map<unsigned,int> hash_prod;
    hash_prod produit;
    // COUT << "small map" << endl;
#endif    
    hash_prod::iterator prod_it,prod_itend;
    std::vector< int_unsigned >::const_iterator it1=v1.begin(),it1end=v1.end(),it2beg=v2.begin(),it2,it2end=v2.end();
    // FIXME if reduce is small use int for g1,g instead of longlong
    longlong g1,g;
    unsigned u1,u;
    for (;it1!=it1end;++it1){
      g1=it1->g;
      u1=it1->u;
      for (it2=it2beg;it2!=it2end;++it2){
	u=u1+it2->u;
	g=g1*it2->g ; // moved % reduce so that 1 % is done instead of 2
	prod_it=produit.find(u);
	if (prod_it==produit.end())
	  produit[u]=g % reduce;
	else {
	  int & s=prod_it->second;
	  g += s;
	  s = g % reduce;
	}
      }
    }
    int_unsigned gu;
    prod_it=produit.begin(),prod_itend=produit.end();
    v.clear();
    v.reserve(produit.size());
    for (;prod_it!=prod_itend;++prod_it){
      if (!is_zero(gu.g=prod_it->second)){
	gu.u=prod_it->first;
	v.push_back(gu);
      }
    }    
    // COUT << "smallmult end " << CLOCK() << endl;
    sort(v.begin(),v.end());
  }

  static void smallmult(int x,std::vector<int_unsigned> & v,int m){
    if (!x){
      v.clear();
      return;
    }
    std::vector<int_unsigned>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      it->g *= x;
      it->g %= m;
    }
  }

  static void smalladd(const std::vector< int_unsigned > & v1,const std::vector< int_unsigned > & v2,int m,std::vector< int_unsigned > & v){
    std::vector< int_unsigned >::const_iterator it1=v1.begin(),it1end=v1.end(),it2=v2.begin(),it2end=v2.end();
    int g;
    v.clear();
    v.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=(it1->g+it2->g)%m;
	if (g)
	  v.push_back(int_unsigned(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (it1->u>it2->u){
	  v.push_back(*it1);
	  ++it1;
	}
	else {
	  v.push_back(*it2);
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.push_back(*it2);
  }

  // Poly evaluation of p at x modulo m, d is the degree in int_unsigned.u
  static void peval(const vector<int_unsigned> & p,int d,int x,int m,vector<int_unsigned> & res){
    res.clear();
    vector<int_unsigned> tmp1,tmp2;
    if (p.empty())
      return;
    // CERR << p << endl;
    vector<int_unsigned>::const_iterator it=p.begin(),itend=p.end();
    int deg=d*(it->u / d),ddeg;
    for (;deg>=0;deg -=d ){ // Horner like
      // CERR << res << endl;
      smallmult(x,res,m);
      // CERR << res << endl;
      tmp2.clear();
      // Find next coeff
      for (;it!=itend;++it){
	ddeg=it->u-deg;
	if (ddeg<0)
	  break;
	tmp2.push_back(int_unsigned(it->g,ddeg));
      }
      // CERR << tmp2 << endl;
      tmp1=res;
      smalladd(tmp1,tmp2,m,res);
      // CERR << res << endl;
    }
  }

  static bool peval(const polynome & p,const gen & x0,int m,polynome & g,vector<int_unsigned> * P){
    gen x1=smod(x0,m);
    if (x1.type!=_INT_)
      return false;
    int x=x1.val;
    index_t d=p.degree();
    unsigned ans;
    if (!degree2unsigned(d,ans))
      return false;
    vector<int_unsigned> Q;
    if (!P) 
      P=&Q;
    if (P->empty() && !convert(p,d,*P,m))
      return false;
    vector<int_unsigned> res;
    peval(*P,ans/d.front(),x,m,res);
    d.erase(d.begin());
    convert(res,d,g);
    return true;
  }

  gen peval(const polynome & p,const vecteur & v,const gen & m,bool simplify_at_end,vector<int_unsigned> * pptr){
    int pdim=int(p.dim),vsize=int(v.size()),var0=pdim-vsize;
    if (v==vecteur(vsize)){ // fast evaluation at 0
      index_t i(pdim);
      i[vsize-1]=1;
      // i=(0,0,...,0,1)
      // find the last position in p where a monomial with index i
      // could be inserted, the remaining of p truncated is the answer
      vector< monomial<gen> >::const_iterator it,itend=p.coord.end();
      it=upper_bound(p.coord.begin(),itend,monomial<gen>(plus_one,i),p.m_is_strictly_greater);
      if ( (it!=itend) && it->index.iref()==i)
	++it;
      polynome res(var0);
      res.coord.reserve(itend-it);
      for (;it!=itend;++it){
	res.coord.push_back(monomial<gen>(it->value,index_t(it->index.begin()+vsize,it->index.end())));
      }
      return res;
    }
    if (vsize==1 && m.type==_INT_ && m.val>0 && m.val<46340){
      polynome res;
      if (peval(p,v.front(),m.val,res,pptr))
	return res;
    }
    if (var0<0){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Too much substitution variables"));
#else
      return gensizeerr(gettext("Too much substitution variables"));
#endif
    }
    polynome res(var0);
    if (p.coord.empty())
      return res;
    vecteur vnum,vden;
    gen vn,vd;
    vnum.reserve(vsize);
    vden.reserve(vsize);
    if (simplify_at_end){
      for (int i=0;i<vsize;++i){
	fxnd(v[i],vn,vd);
	vnum.push_back(vn);
	vden.push_back(vd);
      }
    }
    else {
      vnum=v;
      vden=vecteur(vsize,plus_one);
    }
    // we are now ready to evaluate the polynomial
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    index_t cur_index;
    index_t pdeg(p.degree());
    index_t deg(pdeg.begin(),pdeg.begin()+vsize);
    gen numer(peval(it,itend,vnum,vden,deg,cur_index,0,vsize,var0,m));
    if (!is_zero(m))
      numer=smod(numer,m);
    if (debug_infolevel>40){
      CERR << "// Peval end " << CLOCK();
      if (numer.type==_POLY)
	CERR << " poly " << numer._POLYptr->coord.size();
      CERR << endl;
    }
    if ( is_zero(numer))
      return numer;
    // compute thet table of powers
    gen global_deno(plus_one);
    for (int i=0;i<vsize;++i){
      global_deno=global_deno*pow(vden[i],deg[i]);
    }
    simplify(numer,global_deno);
    return fraction(numer,global_deno);
  }

#if 0
  /* Not used anymore */
  static factorization vector2factorization(const vectpoly & v){
    vectpoly::const_iterator it=v.begin(),itend=v.end();
    factorization res;
    for (int i=1;it!=itend;++it){
      if (Tis_one<gen>(*it))
	res.push_back(facteur<polynome>(*it,i));
    }
    return res;
  }
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
