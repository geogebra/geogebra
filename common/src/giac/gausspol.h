/* -*- mode:C++ ; compile-command: "g++ -I.. -g -c gausspol.cc" -*- */
/*
 *  Copyright (C) 2000 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef _GIAC_GAUSSPOL_H_
#define _GIAC_GAUSSPOL_H_
#include "first.h"
#include "poly.h"
#include "gen.h"
#ifdef HAVE_PTHREAD_H
#include <pthread.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#endif

  extern const int primes[100] ;

  class nfactor {
  public:
    gen fact;
    int mult;
    nfactor():fact(1),mult(0) {}
    nfactor(const nfactor &n) : fact(n.fact),mult(n.mult) {}
    nfactor(const gen & n, int m) : fact(n),mult(m) {}
  };
  std::vector<nfactor> trivial_n_factor(gen &n);
  vecteur cyclotomic(int n);

  // gen pow(const gen & n,int k);
  typedef std::vector< monomial<gen> > monomial_v;
  typedef tensor<gen> polynome;

  // function on polynomials
  polynome gen2polynome(const gen & e,int dim);
  // check type of coefficients
  int coefftype(const polynome & p,gen & coefft);
  // remove MODulo coefficients
  void unmodularize(const polynome p,polynome & res);
  polynome unmodularize(const polynome & p);
  void modularize(polynome & d,const gen & m);
  // arithmetic
  bool is_one(const polynome & p);
  bool operator < (const polynome & f,const polynome & g);
  bool operator < (const facteur<polynome> & f,const facteur<polynome> & g);
  polynome firstcoeff(const polynome & p);
  void Add_gen ( std::vector< monomial<gen> >::const_iterator & a,
		 std::vector< monomial<gen> >::const_iterator & a_end,
		 std::vector< monomial<gen> >::const_iterator & b,
		 std::vector< monomial<gen> >::const_iterator & b_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_m &, const index_m &)) ;
  polynome operator + (const polynome & th,const polynome & other);
  void Sub_gen ( std::vector< monomial<gen> >::const_iterator & a,
		 std::vector< monomial<gen> >::const_iterator & a_end,
		 std::vector< monomial<gen> >::const_iterator & b,
		 std::vector< monomial<gen> >::const_iterator & b_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_m &, const index_m &)) ;
  polynome operator - (const polynome & th,const polynome & other);
  // Fast multiplication using hash maps, might also use an int for reduction
  // but there is no garantee that res is smod-ed modulo reduce
  // Use reduce=0 for non modular
  void mulpoly (const polynome & th, const polynome & other,polynome & res,const gen & reduce);
  polynome operator * (const polynome & th, const polynome & other) ;
  polynome & operator *= (polynome & th, const polynome & other) ;
  void Mul_gen ( std::vector< monomial<gen> >::const_iterator & ita,
		 std::vector< monomial<gen> >::const_iterator & ita_end,
		 std::vector< monomial<gen> >::const_iterator & itb,
		 std::vector< monomial<gen> >::const_iterator & itb_end,
		 std::vector< monomial<gen> > & new_coord,
		 bool (* is_strictly_greater)( const index_t &, const index_t &),
		 const std::pointer_to_binary_function < const monomial<gen> &, const monomial<gen> &, bool> m_is_greater
		 ) ;
  void mulpoly(const polynome & th,const gen & fact,polynome & res);
  polynome operator * (const polynome & th, const gen & fact) ;
  inline polynome operator * (const gen & fact, const polynome & th){ return th*fact; }
  // a*b+c*d
  gen foisplus(const polynome & a,const polynome & b,const polynome & c,const polynome & d);
  polynome operator - (const polynome & th) ;
  polynome operator / (const polynome & th,const polynome & other);
  polynome operator / (const polynome & th,const gen & fact );
  polynome operator % (const polynome & th,const polynome & other);
  polynome operator % (const polynome & th, const gen & modulo);
  polynome re(const polynome & th);
  polynome im(const polynome & th);
  polynome conj(const polynome & th);
  polynome poly1_2_polynome(const vecteur & v, int dimension);
  void polynome2poly1(const polynome & p,int var,vecteur & v);
  vecteur polynome12poly1(const polynome & p);
  int inner_POLYdim(const vecteur & v);
  vecteur polynome2poly1(const polynome & p,int var);
  vecteur polynome2poly1(const polynome & p); // for algebraic ext.
  gen polynome2poly1(const gen & e,int var);
  void poly12polynome(const vecteur & v, int var,polynome & p,int dimension=0);
  polynome poly12polynome(const vecteur & v,int var,int dimension=0);
  polynome poly12polynome(const vecteur & v);
  gen untrunc(const gen & e,int degree,int dimension);
  gen vecteur2polynome(const vecteur & v,int dimension);
  bool divrem1(const polynome & a,const polynome & b,polynome & quo,polynome & r,int exactquo=0,bool allowrational=false) ;
  bool divrem (const polynome & th, const polynome & other, polynome & quo, polynome & rem, bool allowrational = false );
  bool divremmod (const polynome & th,const polynome & other, const gen & modulo,polynome & quo, polynome & rem);
  bool exactquotient(const polynome & a,const polynome & b,polynome & quo,bool allowrational=true);
  bool powpoly (const polynome & th, int u,polynome & res);
  polynome pow(const polynome & th,int n);
  bool is_positive(const polynome & p);
  polynome pow(const polynome & p,const gen & n);
  polynome powmod(const polynome &p,int n,const gen & modulo);
  polynome gcd(const polynome & p,const polynome & q);
  void gcd(const polynome & p,const polynome & q,polynome & d);
  void lcmdeno(const polynome & p, gen & res);
  gen lcoeffn(const polynome & p);
  gen lcoeff1(const polynome & p);
  polynome ichinrem(const polynome &p,const polynome & q,const gen & pmod,const gen & qmod);
  // set i to i+(j-i)*B mod A, inplace operation
  void ichrem_smod_inplace(mpz_t * Az,mpz_t * Bz,mpz_t * iz,mpz_t * tmpz,gen & i,const gen & j);
  polynome resultant(const polynome & p,const polynome & q);
  polynome lgcd(const polynome & p);
  gen ppz(polynome & p,bool divide=true);
  void lgcdmod(const polynome & p,const gen & modulo,polynome & pgcd);
  polynome gcdmod(const polynome &p,const polynome & q,const gen & modulo);
  polynome content1mod(const polynome & p,const gen & modulo,bool setdim=true);
  void contentgcdmod(const polynome &p, const polynome & q, const gen & modulo, polynome & cont,polynome & prim);
  polynome pp1mod(const polynome & p,const gen & modulo);
  // modular gcd via PSR, used when not enough eval points available
  // a and b must be primitive and will be scratched
  void psrgcdmod(polynome & a,polynome & b,const gen & modulo,polynome & prim);
  // Find non zeros coeffs of p, res contains the positions of non-0 coeffs
  int find_nonzero(const polynome & p,index_t & res);
  polynome pzadic(const polynome &p,const gen & n);
  bool gcd_modular_algo(polynome &p,polynome &q, polynome &d,bool compute_cof);
  bool listmax(const polynome &p,gen & n );
  bool gcdheu(const polynome &p,const polynome &q, polynome & p_simp, gen & np_simp, polynome & q_simp, gen & nq_simp, polynome & d, gen & d_content ,bool skip_test=false,bool compute_cofactors=true);
  polynome gcdpsr(const polynome &p,const polynome &q,int gcddeg=0);
  void simplify(polynome & p,polynome & q,polynome & p_gcd);
  polynome simplify(polynome &p,polynome &q);
  void egcdlgcd(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d);
  void egcdpsr(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d);
  void egcd(const polynome &p1, const polynome & p2, polynome & u,polynome & v,polynome & d);
  // Input a,b,c,u,v,d such that a*u+b*v=d, 
  // Output u,v,C such that a*u+b*v=c*C
  void egcdtoabcuv(const tensor<gen> & a,const tensor<gen> &b, const tensor<gen> &c, tensor<gen> &u,tensor<gen> &v, tensor<gen> & d, tensor<gen> & C);

  bool findabcdelta(const polynome & p,polynome & a,polynome &b,polynome & c,polynome & delta);
  bool findde(const polynome & p,polynome & d,polynome &e);
  factorization sqff(const polynome &p );
  // factorize a square-free univariate polynomial
  bool sqfffactor(const polynome &p, vectpoly & v,bool with_sqrt,bool test_composite,bool complexmode);
  bool sqff_evident(const polynome & p,factorization & f,bool withsqrt,bool complexmode);
  // factorization over Z[i]
  bool cfactor(const polynome & p, gen & an,factorization & f,bool withsqrt,gen & extra_div);
  // factorization over an algebraic extension
  // the main variable of G is the algebraic extension variable
  // the minimal polynomial of this variable is p_mini
  // G is assumed to be square-free
  // See algorithm 3.6.4 in Henri Cohen book starting at step 3
  bool algfactor(const polynome & G,const polynome & p_mini,int & k,factorization & f,bool complexmode,gen & extra_div);
  // sqff factorization over a finite field
  factorization squarefree_fp(const polynome & p,unsigned n,unsigned exposant);
  // univariate factorization over a finite field, once sqff
  bool sqff_ffield_factor(const factorization & sqff_f,int n,environment * env,factorization & f);
  // p is primitive wrt the main var
  bool mod_factor(const polynome & p_orig,polynome & p_content,int n,factorization & f);

  // factorization over Z[e] where e is an algebraic extension
  bool ext_factor(const polynome &p,const gen & e,gen & an,polynome & p_content,factorization & f,bool complexmode,gen &extra_div);
  // factorization over Z[coeff_of_p]
  bool factor(const polynome &p,polynome & p_content,factorization & f,bool isprimitive,bool withsqrt,bool complexmode,const gen & divide_by_an,gen & extra_div);
  void unitarize(const polynome &pcur, polynome &unitaryp, polynome & an);
  polynome ununitarize(const polynome & unitaryp, const polynome & an);
  void partfrac(const polynome & num, const polynome & den, const std::vector< facteur< polynome > > & v , std::vector < pf <gen> > & pfde_VECT, polynome & ipnum, polynome & ipden, bool rational=true );
  pf<gen> intreduce_pf(const pf<gen> & p_cst, std::vector< pf<gen> > & intde_VECT ,bool residue=false);
  // Sturm sequences
  vecteur vector_of_polynome2vecteur(const vectpoly & v);
  vecteur sturm_seq(const polynome & p,polynome & cont);

  // prototype of factorization of univariate sqff unitary polynomial
  // provided e.g. by smodular
  bool factorunivsqff(const polynome & q,environment * env,vectpoly & v,int & ithprime,int debug,int modfactor_primes);
  // find linear factor only 
  int linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,int & ithprime);
  // prototype of modular 1-d gcd algorithm
  bool gcd_modular_algo1(polynome &p,polynome &q,polynome &d,bool compute_cof);
  polynome smod(const polynome & th, const gen & modulo);
  void smod(const polynome & th, const gen & modulo,polynome & res);
  bool gcdmod_dim1(const polynome &p,const polynome & q,const gen & modulo,polynome & d,polynome & pcof,polynome & qcof,bool compute_cof,bool & real);

  // evaluate p at v by replacing the last variables of p with values of v
  gen peval(const polynome & p,const vecteur & v,const gen &m,bool simplify_at_end=false,std::vector<int_unsigned> * pptr=0);
  int total_degree(const polynome & p);

  // build a multivariate poly
  // with normal coeff from a multivariate poly with multivariate poly coeffs
  polynome unsplitmultivarpoly(const polynome & p,int inner_dim);
  polynome splitmultivarpoly(const polynome & p,int inner_dim);
  polynome split(const polynome & p,int inner_dim);


  template <class U>
  bool convert_myint(const polynome & p,const index_t & deg,std::vector< T_unsigned<my_mpz,U> >  & v){
    typename std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    U u;
    my_mpz tmp;
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    T_unsigned<my_mpz,U> gu;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      gu.u=u;
      if (it->value.type==_ZINT)
	mpz_set(gu.g.ptr,*it->value._ZINTptr);
      else {
	if (it->value.type!=_INT_)
	  return false;
	mpz_set_si(gu.g.ptr,it->value.val);
      }
      v.push_back(gu);
    }
    return true;
  }


#ifdef HAVE_GMPXX_H
  template <class U>
  bool convert_myint(const polynome & p,const index_t & deg,std::vector< T_unsigned<mpz_class,U> >  & v){
    typename std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    U u;
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      T_unsigned<mpz_class,U> gu;
      gu.u=u;
      if (it->value.type==_ZINT){
	mpz_set(gu.g.get_mpz_t(),*it->value._ZINTptr);
      }
      else {
	if (it->value.type!=_INT_)
	  return false;
	gu.g=it->value.val;
      }
      v.push_back(gu);
    }
    return true;
  }
#endif

  template<class U> int coeff_type(const std::vector< T_unsigned<gen,U> > & p,unsigned & maxint){
    maxint=0;
    typename std::vector< T_unsigned<gen,U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return -1;
    int t=it->g.type,tt;
    register int tmp;
    for (++it;it!=itend;++it){
      tt=it->g.type;
      if (tt!=t)
	return -1;
      if (!tt){
	if (it->g.val>0)
	  tmp=it->g.val;
	else
	  tmp=-it->g.val;
	if (maxint<tmp)
	  maxint=tmp;
      }
    }
    return t;
  }

  template <class U>
  bool convert_double(const polynome & p,const index_t & deg,std::vector< T_unsigned<double,U> >  & v){
    typename std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    T_unsigned<double,U> gu;
    U u;
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      gu.u=u;
      if (it->value.type!=_DOUBLE_)
	return false;
      gu.g=it->value._DOUBLE_val;
      v.push_back(gu);
    }
    return true;
  }

  template <class U>
  bool convert_int32(const polynome & p,const index_t & deg,std::vector< T_unsigned<int,U> > & v,int modulo=0){
    typename std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    U u;
    index_t::const_iterator itit,oldit,ititend,ditbeg=deg.begin(),ditend=deg.end(),dit;
    for (;it!=itend;++it){
      u=0;
      oldit=itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      if (it->value.type==_INT_){
	if (modulo)
	  v.push_back(T_unsigned<int,U>(it->value.val % modulo,u));
	else
	  v.push_back(T_unsigned<int,U>(it->value.val,u));
      }
      else {
	if (modulo && it->value.type==_ZINT)
	  v.push_back(T_unsigned<int,U>(smod(it->value,modulo).val,u));
	else
	  return false;
      }
      int nterms=*(itit-1);
      if (nterms<=1 || nterms>=itend-it)
	continue;
      itit = (it+nterms)->index.begin();
      ititend = itit + p.dim-1;
      if (*(ititend))
	continue;
      for (;itit!=ititend;++oldit,++itit){
	if (*itit!=*oldit)
	  break;
      }
      if (itit!=ititend)
	continue;
      // for dense poly, make all terms with the same x1..xn-1 powers
      for (;nterms;--nterms){
	++it;
	--u;
	if (it->value.type==_INT_){
	  if (modulo)
	    v.push_back(T_unsigned<int,U>(it->value.val % modulo,u));
	  else
	    v.push_back(T_unsigned<int,U>(it->value.val,u));
	}
	else {
	  if (modulo && it->value.type==_ZINT)
	    v.push_back(T_unsigned<int,U>(smod(it->value,modulo).val,u));
	  else
	    return false;
	}
      }
    }
    return true;
  }

  template <class U>
  bool convert_int(const polynome & p,const index_t & deg,std::vector< T_unsigned<longlong,U> >  & v,longlong & maxp){
    typename std::vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    T_unsigned<longlong,U> gu;
    U u;
    maxp=0;
    longlong tmp;
    mpz_t tmpz;
    mpz_init(tmpz);
    index_t::const_iterator itit,ditbeg=deg.begin(),ditend=deg.end(),dit;
    for (;it!=itend;++it){
      u=0;
      itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      gu.u=u;
      if (it->value.type==_INT_)
	gu.g=it->value.val;
      else {
	if (it->value.type!=_ZINT || mpz_sizeinbase(*it->value._ZINTptr,2)>62){
	  mpz_clear(tmpz);
	  return false;
	}
	mpz2longlong(it->value._ZINTptr,&tmpz,gu.g);
      }
      tmp=gu.g>0?gu.g:-gu.g;
      if (tmp>maxp)
	maxp=tmp;
      v.push_back(gu);
    }
    mpz_clear(tmpz);
    return true;
  }

  template<class U> void convert_longlong(const std::vector< T_unsigned<gen,U> > & p,std::vector< T_unsigned<longlong,U> > & pd){
    typename std::vector< T_unsigned<gen,U> >::const_iterator it=p.begin(),itend=p.end();
    pd.reserve(itend-it);
    for (;it!=itend;++it)
      pd.push_back(T_unsigned<longlong,U>(it->g.val,it->u));
  }

  template<class T,class U> void convert_from(const std::vector< T_unsigned<T,U> > & p,std::vector< T_unsigned<gen,U> > & pd){
    typename std::vector< T_unsigned<T,U> >::const_iterator it=p.begin(),itend=p.end();
    pd.reserve(itend-it);
    for (;it!=itend;++it)
      pd.push_back(T_unsigned<gen,U>(gen(it->g),it->u));
  }

  // mode=0: fill both, =1 fill the gen part, =2 fill the index_m part
  template<class T,class U>
  void convert_from(typename std::vector< T_unsigned<T,U> >::const_iterator it,typename std::vector< T_unsigned<T,U> >::const_iterator itend,const index_t & deg,typename std::vector< monomial<gen> >::iterator jt,int mode=0){
    if (mode==1){
      for (;it!=itend;++jt,++it){
	jt->value=gen(it->g);
      }
      return;
    }
    index_t::const_reverse_iterator ditbeg=deg.rbegin(),ditend=deg.rend(),dit;
    int pdim=deg.size();
    U u,prevu=0;
    int k;
    int count=0;
#if defined(GIAC_NO_OPTIMIZATIONS) || ((defined(VISUALC) || defined(__APPLE__)) && !defined(GIAC_VECTOR))
    if (0){ count=0; }
#else
    if (pdim<=POLY_VARS){
      deg_t i[POLY_VARS+1];
      i[0]=2*pdim+1;
      deg_t * iitbeg=i+1,*iit,*iitback=i+pdim,*iitbackm1=iitback-1;
      for (iit=iitbeg;iit!=iitback;++iit)
	*iit=0;
      *iitback=0;
      for (--prevu;it!=itend;++it,++jt){
	u=it->u;
	if (prevu<=u+*iitback){
	  *iitback -= deg_t(prevu-u);
	  prevu=u;
	}
	else {
	  if (pdim>1 && (*iitbackm1)>0 && prevu<=u+*ditbeg+*iitback){
	    --(*iitbackm1);
	    *iitback += deg_t((u+(*ditbeg))-prevu);
	    prevu=u;
	  }
	  else 
	  {
	    prevu=u;
	    for (k=pdim,dit=ditbeg;dit!=ditend;++dit,--k){
	      // qr=div(u,*dit);
	      i[k]=u % (*dit); // qr.rem;
	      u= u / (*dit); // qr.quot;
	      count += pdim;
	    }
	  }
	}
	jt->index=i;
	if (mode)
	  continue;
	jt->value=gen(it->g);
	// p.coord.push_back(monomial<gen>(gen(it->g),i));
      }
    }
#endif
    else {
      index_t i(pdim);
      index_t::iterator iitbeg=i.begin(),iitback=i.end()-1,iitbackm1=iitback-1;
      for (--prevu;it!=itend;++it,++jt){
	u=it->u;
	if (prevu<=u+*iitback){
	  *iitback -= short(prevu-u);
	  prevu=u;
	}
	else {
	  if (pdim>1 && (*iitbackm1)>0 && prevu<=u+*ditbeg+*iitback){
	    --(*iitbackm1);
	    *iitback += short((u+(*ditbeg))-prevu);
	    prevu=u;
	    // cerr << "/" << u << ":" << i << endl;
	  }
	  else 
          {
	    prevu=u;
	    for (k=pdim-1,dit=ditbeg;dit!=ditend;++dit,--k){
	      // qr=div(u,*dit);
	      i[k]=u % (*dit); // qr.rem;
	      u= u / (*dit); // qr.quot;
	      count += pdim;
	      // i[k]=u % unsigned(*dit);
	      // u = u/unsigned(*dit);
	    }
	  }
	}
	jt->index=i;
	if (mode)
	  continue;
	jt->value=gen(it->g);
	// p.coord.push_back(monomial<gen>(gen(it->g),i));
      }
    }
    if (debug_infolevel>5)
      std::cerr << "Divisions: " << count << std::endl;
  }

  
  template<class T,class U>
  struct convert_t {
    typename std::vector< T_unsigned<T,U> >::const_iterator it,itend;
    const index_t * degptr;
    typename std::vector< monomial<gen> >::iterator jt;
    int mode;
  };

  template<class T,class U> 
  void * do_convert_from(void * ptr){
    convert_t<T,U> * argptr = (convert_t<T,U> *) ptr;
    convert_from<T,U>(argptr->it,argptr->itend,*argptr->degptr,argptr->jt,argptr->mode);
    return 0;
  }

  extern int threads;

  template<class T,class U>
  void convert_from(const std::vector< T_unsigned<T,U> > & v,const index_t & deg,polynome & p,bool threaded=false){
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v.begin(),itend=v.end();
    p.dim=deg.size();
    // p.coord.clear(); p.coord.reserve(itend-it);
    p.coord=std::vector< monomial<gen> >(itend-it);
    std::vector< monomial<gen> >::iterator jt=p.coord.begin();
    int nthreads=threads;
    if (nthreads==1 || !threaded ||p.dim>POLY_VARS){
      convert_from<T,U>(it,itend,deg,jt,0); 
      return;
    }
#ifdef HAVE_PTHREAD_H
    unsigned taille=itend-it;
    if (nthreads>1 
	&& int(taille)>nthreads*1000
	){
      pthread_t tab[nthreads];
      convert_t<T,U> arg[nthreads];
      for (int i=0;i<nthreads;i++){
	convert_t<T,U> tmp={it+i*(taille/nthreads),it+(i+1)*taille/nthreads,&deg,jt+i*(taille/nthreads),0};
	if (i==nthreads-1){
	  tmp.itend=itend;
	  convert_from<T,U>(tmp.it,tmp.itend,deg,tmp.jt,tmp.mode);
	}
	else {
	  arg[i]=tmp;
	  int res=pthread_create(&tab[i],(pthread_attr_t *) NULL,do_convert_from<T,U>,(void *) &arg[i]);
	  if (res)
	    convert_from<T,U>(tmp.it,tmp.itend,deg,tmp.jt,tmp.mode);
	}
      }
      for (int i=0;i<nthreads-1;++i){
	void * ptr;
	pthread_join(tab[i],&ptr);
      }
      return;
    } // end if (nthreads>1)
#endif
    convert_from<T,U>(it,itend,deg,jt,0); 
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_GAUSSPOL_H_
