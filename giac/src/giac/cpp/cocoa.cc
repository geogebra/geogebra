/* -*- mode:C++ ; compile-command: "g++ -I.. -I../include -I.. -g -c -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DHAVE_CONFIG_H -DIN_GIAC -Wall cocoa.cc" -*- */
#include "giacPCH.h"

#ifndef WIN32
#define COCOA9950
#endif

#ifdef HAVE_LIBPTHREAD
#endif

#if defined(USE_GMP_REPLACEMENTS) || defined(GIAC_VECTOR)
#undef HAVE_LIBCOCOA
#endif
#ifdef HAVE_LIBCOCOA
#ifdef COCOA9950
#include <CoCoA/RingZZ.H>
#include <CoCoA/RingQQ.H>
#else
#include <CoCoA/ZZ.H>
#include <CoCoA/RingQ.H>
#endif
#include <CoCoA/FractionField.H>
#include <CoCoA/GlobalManager.H>
#include <CoCoA/SparsePolyRing.H>
#include <CoCoA/TmpF5.H>
#include <CoCoA/io.H>
#include <CoCoA/symbol.H>
#include "TmpFGLM.H"
#endif
/*  
 *  Copyright (C) 2007,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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

#include <iostream>
#include <iomanip>
#include "cocoa.h"
#include "gausspol.h"
#include "identificateur.h"
#include "giacintl.h"
#include "index.h"
#include "modpoly.h"

#if defined(USE_GMP_REPLACEMENTS) || defined(GIAC_VECTOR)
#undef HAVE_LIBCOCOA
#endif

#if defined VISUALC && defined __x86_64__ 
#undef __x86_64__
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  //  vecteur trim(const vecteur & p,environment * env);

#ifdef HAVE_LIBCOCOA
  struct order_vectpoly {
    int order;
    vectpoly v;
  };

  struct Qx_I {
    CoCoA::SparsePolyRing * Qxptr;
    CoCoA::ideal * idealptr;
    CoCoA::PPOrdering * cocoa_order;
    Qx_I(): Qxptr(0), idealptr(0),cocoa_order(0){}
  };

  static bool operator < (const order_vectpoly & ov1,const order_vectpoly & ov2){
    if (ov1.order!=ov2.order)
      return ov1.order<ov2.order;
    unsigned ov1s=ov1.v.size(),ov2s=ov2.v.size();
    if (ov1s!=ov2s)
      return ov1s<ov2s;
    for (unsigned i=0;i<ov1s;++i){
      if (ov1.v[i].dim!=ov2.v[i].dim)
	return ov1.v[i].dim<ov2.v[i].dim;
      polynome p = ov1.v[i]-ov2.v[i];
      if (p.coord.empty())
	continue;
      return p.coord.front().value.islesscomplexthan(0);
    }
    return false;
  }
  
  static CoCoA::GlobalManager CoCoAFoundations;

  // cache here a list of already known ideals
  static std::map<order_vectpoly,Qx_I> cocoa_idealptr_map;

#ifdef COCOA9950
  static CoCoA::BigInt gen2ZZ(const gen & g){
    switch (g.type){
    case _INT_:
      return CoCoA::BigInt(g.val);
    case _ZINT:
#ifdef COCOA9950
      return CoCoA::BigInt(*g._ZINTptr);
#else
      return CoCoA::BigInt(CoCoA::CopyFromMPZ,*g._ZINTptr);
#endif
    default:
      setsizeerr(gettext("Invalid giac gen -> CoCoA ZZ conversion")+g.print());
      return CoCoA::BigInt(0);
    }
  }

  static gen ZZ2gen(const CoCoA::RingElem & z){
    CoCoA::BigInt n,d;
    if (CoCoA::IsInteger(n, z))
      return gen(CoCoA::mpzref(n));
    CoCoA::RingElem znum=CoCoA::num(z),zden=CoCoA::den(z);
    if (CoCoA::IsInteger(n, znum) && CoCoA::IsInteger(d, zden))
      return gen(CoCoA::mpzref(n))/gen(CoCoA::mpzref(d));
    setsizeerr(gettext("Unable to convert CoCoA data"));
    return undef;
  }
#else
  static CoCoA::ZZ gen2ZZ(const gen & g){
    switch (g.type){
    case _INT_:
      return CoCoA::ZZ(g.val);
    case _ZINT:
      return CoCoA::ZZ(CoCoA::CopyFromMPZ,*g._ZINTptr);
    default:
      setsizeerr(gettext("Invalid giac gen -> CoCoA ZZ conversion")+g.print());
      return CoCoA::ZZ(0);
    }
  }

  static gen ZZ2gen(const CoCoA::RingElem & z){
    CoCoA::ZZ n,d;
    if (CoCoA::IsInteger(n, z))
      return gen(CoCoA::mpzref(n));
    CoCoA::RingElem znum=CoCoA::num(z),zden=CoCoA::den(z);
    if (CoCoA::IsInteger(n, znum) && CoCoA::IsInteger(d, zden))
      return gen(CoCoA::mpzref(n))/gen(CoCoA::mpzref(d));
    setsizeerr(gettext("Unable to convert CoCoA data"));
    return undef;
  }
#endif

  static CoCoA::RingElem polynome2ringelem(const polynome & p,const std::vector<CoCoA::RingElem> & x){
    if (unsigned(p.dim)>x.size())
      setdimerr();
    CoCoA::RingElem res(x[0]-x[0]); // how do you construct 0 in CoCoA?
    vector<monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      CoCoA::RingElem tmp(gen2ZZ(it->value)*power(x[0],0));
      index_t::const_iterator jt=it->index.begin(),jtend=it->index.end();
      for (int i=0;jt!=jtend;++jt,++i)
	tmp *= power(x[i],*jt);
      res += tmp;
    }
    return res;
  }

  static polynome ringelem2polynome(const CoCoA::RingElem & f,const gen & order){
    CoCoA::SparsePolyIter it=CoCoA::BeginIter(f);
    unsigned dim=CoCoA::IsEnded(it)?0:CoCoA::NumIndets(CoCoA::owner(CoCoA::PP(it)));
    polynome res(dim);
    vector<long> expv;
    index_t index(dim);
    for (;!CoCoA::IsEnded(it);++it){
      const CoCoA::RingElem & z=CoCoA::coeff(it);
      gen coeff=ZZ2gen(z);
      const CoCoA::PPMonoidElem & pp=CoCoA::PP(it);
      CoCoA::exponents(expv,pp);
      for (unsigned i=0;i<dim;++i)
	index[i]=expv[i];
      res.coord.push_back(monomial<gen>(coeff,index));
    }
    change_monomial_order(res,order); // res.tsort();
    return res;
  }

  static void vector_polynome2vector_ringelem(const vectpoly & v,const CoCoA::SparsePolyRing & Qx,vector<CoCoA::RingElem> & g){
    const vector<CoCoA::RingElem> & x = CoCoA::indets(Qx);
    g.reserve(v.size());
    vectpoly::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      g.push_back(polynome2ringelem(*it,x));
    }
  }
#if 0
  static vector<CoCoA::RingElem> vector_polynome2vector_ringelem(const vectpoly & v,const CoCoA::SparsePolyRing & Qx){
    vector<CoCoA::RingElem> g;
    vector_polynome2vector_ringelem(v,Qx,g);
    return g;
  }
  static vectpoly vector_ringelem2vector_polynome(const vector<CoCoA::RingElem> & g,const gen & order){
    vectpoly res;
    vector_ringelem2vector_polynome(g,res,order);
    return res;
  }
#endif
  static void vector_ringelem2vector_polynome(const vector<CoCoA::RingElem> & g, vectpoly & res,const gen & order){
    vector<CoCoA::RingElem>::const_iterator it=g.begin(),itend=g.end();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(ringelem2polynome(*it,order));
    sort(res.begin(),res.end(),tensor_is_strictly_greater<gen>);
    reverse(res.begin(),res.end());    
  }

  static Qx_I get_or_make_idealptr(const vectpoly & v,const gen & order){
    if (order.type!=_INT_ || v.empty())
      settypeerr();
    order_vectpoly ov;
    ov.v=v;
    ov.order=order.val;
    std::map<order_vectpoly,Qx_I>::const_iterator it=cocoa_idealptr_map.find(ov),itend=cocoa_idealptr_map.end();
    if (it!=itend)
      return it->second;
    int d=v[0].dim;
    Qx_I qx_i;
    if (order.type==_INT_ && order.val!=0){
      switch (order.val){
      case _PLEX_ORDER:
	qx_i.cocoa_order = new CoCoA::PPOrdering(CoCoA::NewLexOrdering(d));
	break;
      case _TDEG_ORDER:
	qx_i.cocoa_order = new CoCoA::PPOrdering(CoCoA::NewStdDegLexOrdering(d));
	break;
      default:
	qx_i.cocoa_order = new CoCoA::PPOrdering(CoCoA::NewStdDegRevLexOrdering(d));
      }
      qx_i.Qxptr = new CoCoA::SparsePolyRing(CoCoA::NewPolyRing(
#ifdef COCOA9950
								CoCoA::RingQQ(), 
#else
								CoCoA::RingQ(), 
#endif
								CoCoA::SymbolRange("x",0,d-1),*qx_i.cocoa_order));
    }
    else
      qx_i.Qxptr = new CoCoA::SparsePolyRing(CoCoA::NewPolyRing(
#ifdef COCOA9950
								CoCoA::RingQQ(), 
#else
								CoCoA::RingQ(), 
#endif
								CoCoA::SymbolRange("x",0,d-1)));
    vector<CoCoA::RingElem> g;
    vector_polynome2vector_ringelem(v,*qx_i.Qxptr,g);
    qx_i.idealptr=new CoCoA::ideal(*qx_i.Qxptr,g);
    cocoa_idealptr_map[ov]=qx_i;
    // if (cocoa_order)
    //  delete cocoa_order;
    return qx_i;
  }

  // add a dimension so that p is homogeneous of degree d
  static void homogeneize(polynome & p,int deg){
    vector<monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    int n;
    for (;it!=itend;++it){
      index_t i=it->index.iref();
      n=total_degree(i);
      i.push_back(deg-n);
      it->index=i;
    }
    ++p.dim;
  }

  static void homogeneize(vectpoly & v){
    vectpoly::iterator it=v.begin(),itend=v.end();
    int d=0;
    for (;it!=itend;++it){
      d=giacmax(d,total_degree(*it));
    }
    for (it=v.begin();it!=itend;++it){
      homogeneize(*it,d);
    }
  }

  static void unhomogeneize(polynome & p){
    vector<monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      index_t i=it->index.iref();
      i.pop_back();
      it->index=i;
    }
    --p.dim;
  }

  static void unhomogeneize(vectpoly & v){
    vectpoly::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      unhomogeneize(*it);
    }
  }

  bool f5(vectpoly & v,const gen & order){
    homogeneize(v);
    CoCoA::SparsePolyRing Qx = CoCoA::NewPolyRing(
#ifdef COCOA9950
								CoCoA::RingQQ(), 
#else
								CoCoA::RingQ(), 
#endif
						  CoCoA::SymbolRange("x",0,v[0].dim-1));
    vector<CoCoA::RingElem> g;
    vector_polynome2vector_ringelem(v,Qx,g);
    CoCoA::ideal I(Qx,g);
    vector<CoCoA::RingElem> gb;
    CoCoA::F5(gb,I);
    CoCoA::operator<<(cout,gb);
    cout << endl;
    v.clear();
    vector_ringelem2vector_polynome(gb,v,order);
    unhomogeneize(v);
    return true;
  }

  // order may be 0 (use cocoa default and convert to lex for 0-dim ideals)
  // or _PLEX_ORDER (lexicographic) or _TDEG_ORDER (total degree) 
  // or _REVLEX_ORDER (total degree then reverse of lexicographic)
  bool cocoa_gbasis(vectpoly & v,const gen & order){
    Qx_I qx_i = get_or_make_idealptr(v,order);
    int d=v[0].dim;
    vector<CoCoA::RingElem> gb=TidyGens(*qx_i.idealptr);
    // CoCoA::operator<<(cout,gb);   
    // cout << endl;
    // 0-dim ideals convert to lexicographic order using CoCoA FGLM routine
    // otherwise leaves revlex order
    vector<CoCoA::RingElem> NewGBasis;
    if (order.type==_INT_ && order.val!=0)
      NewGBasis=gb;
    else {
      CoCoA::PPOrdering NewOrdering = CoCoA::NewLexOrdering(d);
      try {
	CoCoADortmund::FGLMBasisConversion(NewGBasis, gb, NewOrdering);
      } catch (...){
	v.clear();
	vector_ringelem2vector_polynome(gb,v,order);
	return false;
      }
      // CoCoA::operator<<(cout,NewGBasis);   
      // cout << endl;
    }
    v.clear();
    vector_ringelem2vector_polynome(NewGBasis,v,order);
    // reverse(v.begin(),v.end());
    // unhomogeneize(v);
    return true;
  }

  vecteur cocoa_in_ideal(const vectpoly & r,const vectpoly & v,const gen & order){
    Qx_I qx_i = get_or_make_idealptr(v,order);
    vector<CoCoA::RingElem> cocoa_r;
    vector_polynome2vector_ringelem(r,*qx_i.Qxptr,cocoa_r);
    int s=cocoa_r.size();
    gen tmp(-1);
    tmp.subtype=_INT_BOOLEAN;
    vecteur res(s,tmp);
    for (int i=0;i<s;++i)
      res[i].val=CoCoA::IsElem(cocoa_r[i],*qx_i.idealptr);
    return res;
  }

  bool cocoa_greduce(const vectpoly & r,const vectpoly & v,const gen & order,vectpoly & res){
    Qx_I qx_i = get_or_make_idealptr(v,order);
    vector<CoCoA::RingElem> cocoa_r;
    vector_polynome2vector_ringelem(r,*qx_i.Qxptr,cocoa_r);
    int s=cocoa_r.size();
    polynome tmp;
    for (int i=0;i<s;++i){
      CoCoA::RingElem tmpc=CoCoA::NF(cocoa_r[i],*qx_i.idealptr);
      tmp=ringelem2polynome(tmpc,order);
      res.push_back(tmp);
    }
    return true;
  }

#else // HAVE_LIBCOCOA

  bool f5(vectpoly & v,const gen & ordre){
    return false;
  }

  bool cocoa_gbasis(vectpoly & v,const gen & ordre){
    return false;
  }

  vecteur cocoa_in_ideal(const vectpoly & r,const vectpoly & v,const gen & ordre){
    return vecteur(r.size(),-1);
  }

  bool cocoa_greduce(const vectpoly & r,const vectpoly & v,const gen & order,vectpoly & res){
    return false;
  }

#endif  // HAVE_LIBCOCOA

#ifndef CAS38_DISABLED
#if GROEBNER_VARS!=15 // double revlex ordering is not compatible with indices swapping
#define GBASIS_SWAP 
#endif
  // minimal numbers of pair to reduce simultaneously with f4buchberger
  //#define GBASIS_F4BUCHBERGER 5
  #define GBASIS_F4BUCHBERGER 0
#define GBASIS_POSTF4BUCHBERGER 0 // 0 means final simplification at the end, 1 at each loop
  // if GIAC_SHORTSHIFTTYPE is defined, sparse matrix is using shift index
  // coded on 2 bytes -> FIXME segfault for cyclic9

#define GIAC_SHORTSHIFTTYPE 16


  void swap_indices(short * tab){
    swap(tab[1],tab[3]);
    swap(tab[4],tab[7]);
    swap(tab[5],tab[6]);
    swap(tab[8],tab[11]);
    swap(tab[9],tab[10]);
#if GROEBNER_VARS>11
    swap(tab[12],tab[15]);
    swap(tab[13],tab[14]);
#endif
  }

  // #define GIAC_CHARDEGTYPE should be in solve.h
#if defined BIGENDIAN && defined GIAC_CHARDEGTYPE
#undef GIAC_CHARDEGTYPE
#endif

#ifdef GIAC_CHARDEGTYPE
  typedef unsigned char degtype; // type for degree for large number of variables
  #define degratio 8
  #define degratiom1 7
#else
  typedef short degtype; // type for degree for large number of variables
  #define degratio 4
  #define degratiom1 3
#endif

  struct tdeg_t {
    void dbgprint() const;
    // data
#ifdef GIAC_64VARS
    union {
      short tab[GROEBNER_VARS+1];
      struct {
	short tdeg; // actually it's twice the total degree+1
	short tdeg2;
	order_t order_;
	longlong * ui;
      };
    };
    //int front() const { if (tdeg % 2) return (*(ui+1)) & 0xffff; else return order_.o==_PLEX_ORDER?tab[0]:tab[1];}
    tdeg_t(const tdeg_t & a){
      if (a.tab[0]%2){
	tdeg=a.tdeg;
	tdeg2=a.tdeg2;
	order_=a.order_;
	ui=a.ui;
	++(*ui);
      }
      else {
	longlong * ptr = (longlong *) tab;
	longlong * aptr = (longlong *) a.tab;
	ptr[0]=aptr[0];
	ptr[1]=aptr[1];
	ptr[2]=aptr[2];
	ptr[3]=aptr[3];
      }
    }
    void compute_degs(){
      if (tab[0]%2){
	longlong * ptr=ui+1;
	tdeg=0;
	int firstblock=order_.o;
	if (firstblock!=_3VAR_ORDER && firstblock<_7VAR_ORDER)
	  firstblock=order_.dim;
	longlong * ptrend=ui+1+(firstblock+degratiom1)/degratio;
	for (;ptr!=ptrend;++ptr){
	  longlong x=*ptr;
#ifdef GIAC_CHARDEGTYPE
	  tdeg += ((x+(x>>8)+(x>>16)+(x>>24)+(x>>32)+(x>>40)+(x>>48)+(x>>56))&0xff);
#else
	  tdeg += ((x+(x>>16)+(x>>32)+(x>>48))&0xffff);
#endif
	}
	tdeg=2*tdeg+1;
	tdeg2=0;
	ptrend=ui+1+(order_.dim+degratiom1)/degratio;
	for (;ptr!=ptrend;++ptr){
	  longlong x=*ptr;
#ifdef GIAC_CHARDEGTYPE
	  tdeg2 += ((x+(x>>8)+(x>>16)+(x>>24)+(x>>32)+(x>>40)+(x>>48)+(x>>56))&0xff);
#else
	  tdeg2 += ((x+(x>>16)+(x>>32)+(x>>48))&0xffff);
#endif
	}
      }
    }
    ~tdeg_t(){
      if (tab[0]%2){
	--(*ui);
	if (*ui==0)
	  free(ui);
      }
    }
    tdeg_t & operator = (const tdeg_t & a){
      if (tab[0] % 2){
	--(*ui);
	if (*ui==0)
	  free(ui);
	if (a.tab[0] % 2){
	  tdeg=a.tdeg;
	  tdeg2=a.tdeg2;
	  order_=a.order_;
	  ui=a.ui;
	  ++(*ui);
	  return *this;
	}
      }
      else {
	if (a.tab[0]%2){
	  ++(*a.ui);
	}
      }
      longlong * ptr = (longlong *) tab;
      longlong * aptr = (longlong *) a.tab;
      ptr[0]=aptr[0];
      ptr[1]=aptr[1];
      ptr[2]=aptr[2];
      ptr[3]=aptr[3];
      return *this;
    }
#else
    short tab[GROEBNER_VARS+1];
    int front(){ return tab[1];}
#endif
    // methods
    unsigned total_degree(order_t order) const {
#ifdef GIAC_64VARS
      if (tab[0]%2)
	return tdeg/2+tdeg2;
#endif
      // works only for revlex and tdeg
#if 0
      if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
	return tab[0];
      if (order==_3VAR_ORDER)
	return (tab[0] << 16)+tab[4];
      if (order==_7VAR_ORDER)
	return (tab[0] << 16) +tab[8];
      if (order==_11VAR_ORDER)
	return (tab[0] << 16) +tab[12];
#endif
      return tab[0];
    }
    // void set_total_degree(unsigned d) { tab[0]=d;}
    tdeg_t() { 
      longlong * ptr = (longlong *) tab;
      ptr[2]=ptr[1]=ptr[0]=0;
#if GROEBNER_VARS>11
      ptr[3]=0;
#endif
    }
    tdeg_t(int i){
      longlong * ptr = (longlong *) tab;
      ptr[2]=ptr[1]=ptr[0]=0;
#if GROEBNER_VARS>11
      ptr[3]=0;
#endif
    }
    void get_tab(short * ptr) const {
#ifdef GIAC_64VARS
      if (tab[0]%2){ // copy only 16 first
	degtype * ptr_=(degtype *)(ui+1);
	for (unsigned i=0;i<=GROEBNER_VARS;++i)
	  ptr[i]=ptr_[i];
	return;
      }
#endif
      for (unsigned i=0;i<=GROEBNER_VARS;++i)
	ptr[i]=tab[i];
#ifdef GIAC_64VARS
      ptr[0]/=2;
#endif
#ifdef GBASIS_SWAP
      swap_indices(ptr);
#endif
    }
    tdeg_t(const index_m & lm,order_t order){ 
#ifdef GIAC_64VARS
      if (lm.size()>GROEBNER_VARS){
	ui=(longlong *)malloc((1+(lm.size()+degratiom1)/degratio)*sizeof(longlong));
	longlong* ptr=ui;
	*ptr=1; ++ ptr;
#ifdef GIAC_CHARDEGTYPE
	for (int i=0;i<lm.size();){
	  unsigned char tableau[8]={0,0,0,0,0,0,0,0};
	  tableau[0]=lm[i];
	  ++i;
	  if (i<lm.size())
	    tableau[1] = lm[i];
	  ++i;
	  if (i<lm.size())
	    tableau[2]= lm[i];
	  ++i; 
	  if (i<lm.size())
	    tableau[3]= lm[i];
	  ++i; 
	  if (i<lm.size())
	    tableau[4]= lm[i];
	  ++i; 
	  if (i<lm.size())
	    tableau[5]= lm[i];
	  ++i; 
	  if (i<lm.size())
	    tableau[6]= lm[i];
	  ++i; 
	  if (i<lm.size())
	    tableau[7]= lm[i];
	  ++i; 
	  *ptr = * (longlong *)tableau ;
	  ++ptr;
	}
#else
	for (int i=0;i<lm.size();){
	  longlong x=lm[i];
	  ++i;
	  x <<= 16;
	  if (i<lm.size())
	    x += lm[i];
	  ++i;
	  x <<= 16;
	  if (i<lm.size())
	    x += lm[i];
	  ++i;
	  x <<= 16;
	  if (i<lm.size())
	    x += lm[i];
#ifndef BIGENDIAN
	  x = (x>>48) | (((x>>32)&0xffff)<<16) | (((x>>16)&0xffff)<<32) | ((x&0xffff)<<48);
#endif
	  *ptr = x;
	  ++ptr;
	  ++i;
	}
#endif // GIAC_CHARDEGTYPE
	if (order.o==_3VAR_ORDER || order.o>=_7VAR_ORDER){
	  tdeg=2*nvar_total_degree(lm,order.o)+1;
	  tdeg2=sum_degree_from(lm,order.o); 
	}
	else {
	  tdeg=2*lm.total_degree()+1;
	  tdeg2=0;
	}
	order_=order;
	return;
      }
#endif // GIAC_64VARS
      longlong * ptr_ = (longlong *) tab;
      ptr_[2]=ptr_[1]=ptr_[0]=0;
      short * ptr=tab;
#if GROEBNER_VARS>11
      ptr_[3]=0;
#endif
      // tab[GROEBNER_VARS]=order;
#if GROEBNER_VARS==15
      if (order.o==_3VAR_ORDER){
#ifdef GIAC_64VARS
	ptr[0]=2*(lm[0]+lm[1]+lm[2]);
#else
	ptr[0]=lm[0]+lm[1]+lm[2];
#endif
	ptr[1]=lm[2];
	ptr[2]=lm[1];
	ptr[3]=lm[0];
	ptr +=5;
	short t=0;
	vector<deg_t>::const_iterator it=lm.begin()+3,itend=lm.end();
	for (--itend,--it;it!=itend;++ptr,--itend){
	  t += *itend;
	  *ptr=*itend;
	}
	tab[4]=t;
	return;
      }
      if (order.o==_7VAR_ORDER){
#ifdef GIAC_64VARS
	ptr[0]=2*(lm[0]+lm[1]+lm[2]+lm[3]+lm[4]+lm[5]+lm[6]);
#else
	ptr[0]=lm[0]+lm[1]+lm[2]+lm[3]+lm[4]+lm[5]+lm[6];
#endif
	ptr[1]=lm[6];
	ptr[2]=lm[5];
	ptr[3]=lm[4];
	ptr[4]=lm[3];
	ptr[5]=lm[2];
	ptr[6]=lm[1];
	ptr[7]=lm[0];
	ptr +=9;
	short t=0;
	vector<deg_t>::const_iterator it=lm.begin()+7,itend=lm.end();
	for (--itend,--it;it!=itend;++ptr,--itend){
	  t += *itend;
	  *ptr=*itend;
	}
	tab[8]=t;
	return;
      }
      if (order.o==_11VAR_ORDER){
#ifdef GIAC_64VARS
	ptr[0]=2*(lm[0]+lm[1]+lm[2]+lm[3]+lm[4]+lm[5]+lm[6]+lm[7]+lm[8]+lm[9]+lm[10]);
#else
	ptr[0]=lm[0]+lm[1]+lm[2]+lm[3]+lm[4]+lm[5]+lm[6]+lm[7]+lm[8]+lm[9]+lm[10];
#endif
	ptr[1]=lm[10];
	ptr[2]=lm[9];
	ptr[3]=lm[8];
	ptr[4]=lm[7];
	ptr[5]=lm[6];
	ptr[6]=lm[5];
	ptr[7]=lm[4];
	ptr[8]=lm[3];
	ptr[9]=lm[2];
	ptr[10]=lm[1];
	ptr[11]=lm[0];
	ptr += 13;
	short t=0;
	vector<deg_t>::const_iterator it=lm.begin()+11,itend=lm.end();
	for (--itend,--it;it!=itend;++ptr,--itend){
	  t += *itend;
	  *ptr=*itend;
	}
	tab[12]=t;
	return;
      }
#endif
      vector<deg_t>::const_iterator it=lm.begin(),itend=lm.end();
      if (order.o==_REVLEX_ORDER || order.o==_TDEG_ORDER){
	*ptr=sum_degree(lm);
	++ptr;
      }
      if (order.o==_REVLEX_ORDER){
	for (--itend,--it;it!=itend;++ptr,--itend)
	  *ptr=*itend;
      }
      else {
	for (;it!=itend;++ptr,++it)
	  *ptr=*it;
      }
#ifdef GBASIS_SWAP
      swap_indices(tab);
#endif
#ifdef GIAC_64VARS
      *tab *=2;
#endif
    }
  };

  typedef map<tdeg_t,unsigned> annuaire;

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const tdeg_t & x){
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      os << "[";
      const longlong * ptr=x.ui+1,*ptrend=ptr+(x.order_.dim+degratiom1)/degratio;
      for (;ptr!=ptrend;++ptr){
	longlong x=*ptr;
#ifdef BIGENDIAN
	os << ((x>>48) &0xffff)<< "," << ((x>>32) & 0xffff) << "," << ((x>>16) & 0xffff) << "," << ((x) & 0xffff) << ",";
#else
	os << ((x) &0xffff)<< "," << ((x>>16) & 0xffff) << "," << ((x>>32) & 0xffff) << "," << ((x>>48) & 0xffff) << ",";
#endif
      }
      return os << "]";
    }
#endif    
    os << "[";
    for (unsigned i=0; i<=GROEBNER_VARS;++i){
      os << x.tab[i] << ",";
    }
    return os << "]";
  }
#else
  ostream & operator << (ostream & os,const tdeg_t & x){
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      os << "[";
      const longlong * ptr=x.ui+1,*ptrend=ptr+(x.order_.dim+degratiom1)/degratio;
      for (;ptr!=ptrend;++ptr){
	longlong x=*ptr;
#ifdef BIGENDIAN
	os << ((x>>48) &0xffff)<< "," << ((x>>32) & 0xffff) << "," << ((x>>16) & 0xffff) << "," << ((x) & 0xffff) << ",";
#else
	os << ((x) &0xffff)<< "," << ((x>>16) & 0xffff) << "," << ((x>>32) & 0xffff) << "," << ((x>>48) & 0xffff) << ",";
#endif
      }
      return os << "]";
    }
#endif    
    os << "[";
    for (unsigned i=0; i<=GROEBNER_VARS;++i){
      os << x.tab[i] << ",";
    }
    return os << "]";
  }
#endif
  void tdeg_t::dbgprint() const { COUT << * this << endl; }
  tdeg_t operator + (const tdeg_t & x,const tdeg_t & y);
  tdeg_t & operator += (tdeg_t & x,const tdeg_t & y){ 
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      if (!y.tab[0]%2){
	y.dbgprint();
	COUT << "erreur" << endl;
      }
      return x=x+y;
    }
#endif    
#if 1
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    xtab[0]+=ytab[0];
    xtab[1]+=ytab[1];
    xtab[2]+=ytab[2];
#if GROEBNER_VARS>11
    xtab[3]+=ytab[3];
#endif
#else
    for (unsigned i=0;i<=GROEBNER_VARS;++i)
      x.tab[i]+=y.tab[i];
#endif
    return x;  
  }
  tdeg_t operator + (const tdeg_t & x,const tdeg_t & y){
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      if (!y.tab[0]%2)
	COUT << "erreur" << endl;
      tdeg_t res;
      res.order_=x.order_;
      res.ui=(longlong *)malloc(1+(x.order_.dim+degratiom1)/degratio*sizeof(longlong));
      res.ui[0]=1; 
      const longlong * xptr=x.ui+1,*xend=xptr+(x.order_.dim+degratiom1)/degratio,*yptr=y.ui+1;
      longlong * resptr=res.ui+1;
      for (;xptr!=xend;++resptr,++yptr,++xptr)
	*resptr=*xptr+*yptr;
      res.tdeg=1;
      res.compute_degs();
      return res;
    }
#endif    
    tdeg_t res(x);
    return res += y;
#if 1
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y,*ztab=(ulonglong *)&res;
    ztab[0]=xtab[0]+ytab[0];
    ztab[1]=xtab[1]+ytab[1];
    ztab[2]=xtab[2]+ytab[2];
#if GROEBNER_VARS>11
    ztab[3]=xtab[3]+ytab[3];
#endif
#else
    for (unsigned i=0;i<=GROEBNER_VARS;++i)
      res.tab[i]=x.tab[i]+y.tab[i];
#endif
    return res;
  }
  void add(const tdeg_t & x,const tdeg_t & y,tdeg_t & res,int dim){
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      if (!y.tab[0]%2)
	COUT << "erreur" << endl;
      res=x+y;
      return;
    }
#endif    
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      res = x;
      res += y;
      return;
    }
#endif    
#if 1
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y,*ztab=(ulonglong *)&res;
    ztab[0]=xtab[0]+ytab[0];
    ztab[1]=xtab[1]+ytab[1];
    ztab[2]=xtab[2]+ytab[2];
#if GROEBNER_VARS>11
    ztab[3]=xtab[3]+ytab[3];
#endif
#else
    for (unsigned i=0;i<=dim;++i)
      res.tab[i]=x.tab[i]+y.tab[i];
#endif
  }
  tdeg_t operator - (const tdeg_t & x,const tdeg_t & y){ 
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      if (!y.tab[0]%2)
	COUT << "erreur" << endl;
      tdeg_t res;
      res.order_=x.order_;
      res.ui=(longlong *)malloc(1+(x.order_.dim+degratiom1)/degratio*sizeof(longlong));
      res.ui[0]=1; 
      const longlong * xptr=x.ui+1,*xend=xptr+(x.order_.dim+degratiom1)/degratio,*yptr=y.ui+1;
      longlong * resptr=res.ui+1;
      for (;xptr!=xend;++resptr,++yptr,++xptr)
	*resptr=*xptr-*yptr;
      res.tdeg=1;
      res.compute_degs();
      return res;
    }
#endif    
    tdeg_t res;
#if 1
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y,*ztab=(ulonglong *)&res;
    ztab[0]=xtab[0]-ytab[0];
    ztab[1]=xtab[1]-ytab[1];
    ztab[2]=xtab[2]-ytab[2];
#if GROEBNER_VARS>11
    ztab[3]=xtab[3]-ytab[3];
#endif
#else
    for (unsigned i=0;i<=GROEBNER_VARS;++i)
      res.tab[i]=x.tab[i]-y.tab[i];
#endif
    return res;
  }
  inline bool operator == (const tdeg_t & x,const tdeg_t & y){ 
    longlong X=((longlong *) x.tab)[0];
    if (X!= ((longlong *) y.tab)[0])
      return false;
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      //if (x.ui==y.ui) return true;
      const longlong * xptr=x.ui+1,*xend=xptr+(x.order_.dim+degratiom1)/degratio,*yptr=y.ui+1;
      //if (!x.tdeg){  xptr+=(x.order_.o+degratiom1)/degratio;  yptr+=(y.order_.o+degratiom1)/degratio; }
      for (;xptr!=xend;++yptr,++xptr){
	if (*xptr!=*yptr)
	  return false;
      }
      return true;
    }
#endif    
    return ((longlong *) x.tab)[1] == ((longlong *) y.tab)[1] &&
      ((longlong *) x.tab)[2] == ((longlong *) y.tab)[2] 
#if GROEBNER_VARS>11
      &&  ((longlong *) x.tab)[3] == ((longlong *) y.tab)[3]
#endif
    ;
  }
  inline bool operator != (const tdeg_t & x,const tdeg_t & y){ 
    return !(x==y);
  }

  static inline int tdeg_t_revlex_greater (const tdeg_t & x,const tdeg_t & y){
#ifdef GBASIS_SWAP
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    if (xtab[0]!=ytab[0]) // tdeg test already donne by caller
      return xtab[0]<=ytab[0]?1:0;
    if (xtab[1]!=ytab[1])
      return xtab[1]<=ytab[1]?1:0;
    if (xtab[2]!=ytab[2])
      return xtab[2]<=ytab[2]?1:0;
#if GROEBNER_VARS>11
    return xtab[3]<=ytab[3]?1:0;
#endif
    return 2;
#else // GBASIS_SWAP
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>=y.tab[0]?1:0;
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]<=y.tab[1]?1:0;
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]<=y.tab[2]?1:0;
      return x.tab[3]<=y.tab[3]?1:0;
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]<=y.tab[4]?1:0;
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]<=y.tab[5]?1:0;
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]<=y.tab[6]?1:0;
      return x.tab[7]<=y.tab[7]?1:0;
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]<=y.tab[8]?1:0;
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]<=y.tab[9]?1:0;
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]<=y.tab[10]?1:0;
      return x.tab[11]<=y.tab[11]?1:0;
    }
#if GROEBNER_VARS>11
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]<=y.tab[12]?1:0;
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]<=y.tab[13]?1:0;
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]<=y.tab[14]?1:0;
      return x.tab[15]<=y.tab[15]?1:0;
    }
#endif
    return 2;
#endif // GBASIS_SWAP
  }

  // inline bool operator <  (const tdeg_t & x,const tdeg_t & y){ return !tdeg_t_revlex_greater(x,y); }
  // inline bool operator >  (const tdeg_t & x,const tdeg_t & y){ return !tdeg_t_revlex_greater(y,x); }
  // inline bool operator <= (const tdeg_t & x,const tdeg_t & y){ return tdeg_t_revlex_greater(y,x); }
  // inline bool operator >=  (const tdeg_t & x,const tdeg_t & y){ return tdeg_t_revlex_greater(x,y); }

#if GROEBNER_VARS==15

  int tdeg_t_3var_greater (const tdeg_t & x,const tdeg_t & y){
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>=y.tab[0]?1:0;
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]<=y.tab[1]?1:0;
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]<=y.tab[2]?1:0;
      return x.tab[3]<=y.tab[3]?1:0;
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]>=y.tab[4]?1:0;
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]<=y.tab[5]?1:0;
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]<=y.tab[6]?1:0;
      return x.tab[7]<=y.tab[7]?1:0;
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]<=y.tab[8]?1:0;
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]<=y.tab[9]?1:0;
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]<=y.tab[10]?1:0;
      return x.tab[11]<=y.tab[11]?1:0;
    }
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]<=y.tab[12]?1:0;
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]<=y.tab[13]?1:0;
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]<=y.tab[14]?1:0;
      return x.tab[15]<=y.tab[15]?1:0;
    }
    return 2;
  }

  int tdeg_t_7var_greater (const tdeg_t & x,const tdeg_t & y){
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>=y.tab[0]?1:0;
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]<=y.tab[1]?1:0;
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]<=y.tab[2]?1:0;
      return x.tab[3]<=y.tab[3]?1:0;
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]<=y.tab[4]?1:0;
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]<=y.tab[5]?1:0;
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]<=y.tab[6]?1:0;
      return x.tab[7]<=y.tab[7]?1:0;
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]>=y.tab[8]?1:0;
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]<=y.tab[9]?1:0;
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]<=y.tab[10]?1:0;
      return x.tab[11]<=y.tab[11]?1:0;
    }
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]<=y.tab[12]?1:0;
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]<=y.tab[13]?1:0;
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]<=y.tab[14]?1:0;
      return x.tab[15]<=y.tab[15]?1:0;
    }
    return 2;
  }

  int tdeg_t_11var_greater (const tdeg_t & x,const tdeg_t & y){
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>=y.tab[0]?1:0;
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]<=y.tab[1]?1:0;
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]<=y.tab[2]?1:0;
      return x.tab[3]<=y.tab[3]?1:0;
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]<=y.tab[4]?1:0;
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]<=y.tab[5]?1:0;
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]<=y.tab[6]?1:0;
      return x.tab[7]<=y.tab[7]?1:0;
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]<=y.tab[8]?1:0;
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]<=y.tab[9]?1:0;
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]<=y.tab[10]?1:0;
      return x.tab[11]<=y.tab[11]?1:0;
    }
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]>=y.tab[12]?1:0;
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]<=y.tab[13]?1:0;
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]<=y.tab[14]?1:0;
      return x.tab[15]<=y.tab[15]?1:0;
    }
    return 2;
  }
#endif // GROEBNER_VARS==15

  int tdeg_t_lex_greater (const tdeg_t & x,const tdeg_t & y){
#ifdef GBASIS_SWAP
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    ulonglong X=*xtab, Y=*ytab;
    if (X!=Y){
      if ( (X & 0xffff) != (Y &0xffff))
	return (X&0xffff)>=(Y&0xffff)?1:0;
      return X>=Y?1:0;
    }
    if (xtab[1]!=ytab[1])
      return xtab[1]>=ytab[1]?1:0;
    if (xtab[2]!=ytab[2])
      return xtab[2]>=ytab[2]?1:0;
#if GROEBNER_VARS>11
    return xtab[3]>=ytab[3]?1:0;
#endif
    return 2;
#else
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>y.tab[0]?1:0;
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]>y.tab[1]?1:0;
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]>y.tab[2]?1:0;
      return x.tab[3]>y.tab[3]?1:0;
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]>y.tab[4]?1:0;
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]>y.tab[5]?1:0;
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]>y.tab[6]?1:0;
      return x.tab[7]>y.tab[7]?1:0;
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]>y.tab[8]?1:0;
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]>y.tab[9]?1:0;
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]>y.tab[10]?1:0;
      return x.tab[11]>=y.tab[11]?1:0;
    }
#if GROEBNER_VARS>11
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]>y.tab[12]?1:0;
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]>y.tab[13]?1:0;
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]>y.tab[14]?1:0;
      return x.tab[15]>=y.tab[15]?1:0;
    }
#endif
    return 2;
#endif
  }

  inline int tdeg_t_greater (const tdeg_t & x,const tdeg_t & y,order_t order){
    short X=x.tdeg;
    if (X!=y.tdeg) return X>y.tdeg?1:0; // since tdeg is tab[0] for plex
#ifdef GIAC_64VARS
    if (X%2){
      if (order.o>=_7VAR_ORDER || order.o==_3VAR_ORDER){
#ifdef GIAC_GBASISLEX 
	// if activated, check that poly8, polymod and zpolymod should be reordered
	// FIXME
#endif
	int n=(order.o+degratiom1)/degratio;
	const longlong * it1beg=x.ui,*it1=x.ui+n,*it2=y.ui+n;
	longlong a=0,b=0;
#ifdef BIGENDIAN
	for (;it1!=it1beg;--it2,--it1){
	  a=*it1; 
	  b=*it2;
	  if (a!=b)
	    break;
	}
	if (a!=b){
	  if ( ((a)&0xffff) != ((b)&0xffff) )
	    return ((a)&0xffff) <= ((b)&0xffff)?1:0;
	  if ( ((a>>16)&0xffff) != ((b>>16)&0xffff) )
	    return ((a>>16)&0xffff) <= ((b>>16)&0xffff)?1:0;
	  if ( ((a>>32)&0xffff) != ((b>>32)&0xffff) )
	    return ((a>>32)&0xffff) <= ((b>>32)&0xffff)?1:0;
	  return a <= b?1:0;
	}
#else
	for (;it1!=it1beg;--it2,--it1){
	  a=*it1; 
	  b=*it2;
	  if (a!=b)
	    return a<=b?1:0;
	}
#endif
	if (x.tdeg2!=y.tdeg2)
	  return x.tdeg2>=y.tdeg2;
	it1beg=x.ui+n;
	n=(x.order_.dim+degratiom1)/degratio;
	it1=x.ui+n;
	it2=y.ui+n;
#ifdef BIGENDIAN
	for (a=0,b=0;it1!=it1beg;--it2,--it1){
	  a=*it1; b=*it2;
	  if (a!=b)
	    break;
	}
	if (a!=b){
	  if ( ((a)&0xffff) != ((b)&0xffff) )
	    return ((a)&0xffff) <= ((b)&0xffff)?1:0;
	  if ( ((a>>16)&0xffff) != ((b>>16)&0xffff) )
	    return ((a>>16)&0xffff) <= ((b>>16)&0xffff) ?1:0;
	  if ( ((a>>32)&0xffff) != ((b>>32)&0xffff) )
	    return ((a>>32)&0xffff) <= ((b>>32)&0xffff) ?1:0;
	  return a <= b?1:0;
	}
#else
	for (;it1!=it1beg;--it2,--it1){
	  a=*it1; 
	  b=*it2;
	  if (a!=b)
	    return a<=b?1:0;
	}
#endif
	return 2;
      }
      if (order.o==_REVLEX_ORDER){
	if (x.tdeg!=y.tdeg) return x.tdeg>y.tdeg?1:0;
	const longlong * it1beg=x.ui,*it1=x.ui+(x.order_.dim+degratiom1)/degratio,*it2=y.ui+(y.order_.dim+degratiom1)/degratio;
	longlong a=0,b=0;
#ifdef BIGENDIAN
	for (;it1!=it1beg;--it2,--it1){
	  a=*it1; b=*it2;
	  if (a!=b)
	    break;
	}
	if (a!=b){
	  if ( ((a)&0xffff) != ((b)&0xffff) )
	    return ((a)&0xffff) <= ((b)&0xffff)?1:0;
	  if ( ((a>>16)&0xffff) != ((b>>16)&0xffff) )
	    return ((a>>16)&0xffff) <= ((b>>16)&0xffff)?1:0;
	  if ( ((a>>32)&0xffff) != ((b>>32)&0xffff) )
	    return ((a>>32)&0xffff) <= ((b>>32)&0xffff)?1:0;
	  return a <= b?1:0;
	}
#else
	for (;it1!=it1beg;--it2,--it1){
	  a=*it1; 
	  b=*it2;
	  if (a!=b)
	    return a<=b?1:0;
	}
#endif
	return 2;
      }
      if (order.o==_TDEG_ORDER && x.tdeg!=y.tdeg) 
	return x.tdeg>y.tdeg?1:0;
      // FIXME plex might be wrong
      const degtype * it1=(degtype *)(x.ui+1),*it1end=it1+x.order_.dim,*it2=(degtype *)(y.ui+1);
      for (;it1!=it1end;++it2,++it1){
	if (*it1!=*it2)
	  return *it1>=*it2?1:0;
      }
      return 2;
    }
#endif
    if (order.o==_REVLEX_ORDER)
      return tdeg_t_revlex_greater(x,y);
#if GROEBNER_VARS==15
    if (order.o==_3VAR_ORDER)
      return tdeg_t_3var_greater(x,y);
    if (order.o==_7VAR_ORDER)
      return tdeg_t_7var_greater(x,y);
    if (order.o==_11VAR_ORDER)
      return tdeg_t_11var_greater(x,y);
#endif
    return tdeg_t_lex_greater(x,y);
  }
  inline bool tdeg_t_strictly_greater (const tdeg_t & x,const tdeg_t & y,order_t order){
    return !tdeg_t_greater(y,x,order); // total order
  }

  bool tdeg_t_all_greater(const tdeg_t & x,const tdeg_t & y,order_t order){
#ifdef GIAC_64VARS
    if (x.tab[0]%2){
      if (!y.tab[0]%2)
	COUT << "erreur" << endl;
      if (x.tdeg<y.tdeg || x.tdeg2<y.tdeg2)
	return false;
#if 0
      const degtype * it1=(degtype *)(x.ui+1),*it1end=it1+x.order_.dim,*it2=(degtype *)(y.ui+1);
      for (;it1!=it1end;++it2,++it1){
	if (*it1<*it2)
	  return false;
      }
#else
      const longlong * it1=x.ui+1,*it1end=it1+(x.order_.dim+degratiom1)/degratio,*it2=y.ui+1;
      for (;it1!=it1end;++it2,++it1){
#ifdef GIAC_CHARDEGTYPE
	if ((*it1-*it2) & 0x8080808080808080ULL)
	  return false;
#else
	if ((*it1-*it2) & 0x8000800080008000ULL)
	  return false;
#endif
      }
#endif
      return true;
    }
#endif
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    if ((xtab[0]-ytab[0]) & 0x8000800080008000ULL)
      return false;
    if ((xtab[1]-ytab[1]) & 0x8000800080008000ULL)
      return false;
    if ((xtab[2]-ytab[2]) & 0x8000800080008000ULL)
      return false;
#if GROEBNER_VARS>11
    if ((xtab[3]-ytab[3]) & 0x8000800080008000ULL)
      return false;
#endif
    return true;
  }

  void index_lcm(const tdeg_t & x,const tdeg_t & y,tdeg_t & z,order_t order){
#ifdef GIAC_64VARS
    if (x.tdeg%2){
      if (!y.tab[0]%2)
	COUT << "erreur" << endl;
      z=tdeg_t();
      z.tdeg=1;
      z.order_=x.order_;
      z.ui=(longlong *)malloc((1+(x.order_.dim+degratiom1)/degratio)*sizeof(longlong));
      z.ui[0]=1;
      const degtype * xptr=(degtype *)(x.ui+1),*xend=xptr+degratio*((x.order_.dim+degratiom1)/degratio),*yptr=(degtype *)(y.ui+1);
      degtype * resptr=(degtype *)(z.ui+1);
      for (;xptr!=xend;++resptr,++yptr,++xptr)
	*resptr=*xptr>*yptr?*xptr:*yptr;
      z.tdeg=1;
      z.compute_degs();
      return ;
    }
#endif
    int t=0;
    const short * xtab=&x.tab[1],*ytab=&y.tab[1];
    short *ztab=&z.tab[1];
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 1
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 2
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 3
    ++xtab; ++ytab; ++ztab;
#if GROEBNER_VARS==15
    if (order.o==_3VAR_ORDER){
#ifdef GIAC_64VARS
      z.tab[0]=2*t;
#else
      z.tab[0]=t;
#endif
      t=0;
      ++xtab;++ytab;++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 5
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 6
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 7
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 8
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 9
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 10
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 11
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 12
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 13
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 14
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 15
      z.tab[4]=t; // 4
      return;
    }
#endif
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 4
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 5
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 6
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 7
    ++xtab; ++ytab; ++ztab;
#if GROEBNER_VARS==15
    if (order.o==_7VAR_ORDER){
#ifdef GIAC_64VARS
      z.tab[0]=2*t;
#else
      z.tab[0]=t;
#endif
      t=0;
      ++xtab;++ytab;++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 9
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 10
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 11
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 12
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 13
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 14
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 15
      z.tab[8]=t; // 8
      return;
    }
#endif
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 8
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 9
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 10
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 11
#if GROEBNER_VARS>11
    ++xtab; ++ytab; ++ztab;
#if GROEBNER_VARS==15
    if (order.o==_11VAR_ORDER){
#ifdef GIAC_64VARS
      z.tab[0]=2*t;
#else
      z.tab[0]=t;
#endif
      t=0;
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 13
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 14
      ++xtab; ++ytab; ++ztab;
      t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 15
      z.tab[12]=t; // 12
      return;
    }
#endif
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 12
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 13
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 14
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab); // 15
#endif
    if (order.o==_REVLEX_ORDER || order.o==_TDEG_ORDER){
#ifdef GIAC_64VARS
      z.tab[0]=2*t;
#else
      z.tab[0]=t;
#endif
    }
    else {
#ifdef GIAC_64VARS
      z.tab[0]=2*((x.tab[0]>y.tab[0])?x.tab[0]:y.tab[0]);
#else
      z.tab[0]=(x.tab[0]>y.tab[0])?x.tab[0]:y.tab[0];
#endif
    }
  }

  void get_index(const tdeg_t & x_,index_t & idx,order_t order,int dim){
#ifdef GIAC_64VARS
    if (x_.tab[0]%2){
      idx.resize(dim);
      const degtype * ptr=(degtype *)(x_.ui+1),*ptrend=ptr+x_.order_.dim;
      index_t::iterator target=idx.begin();
      for (;ptr!=ptrend;++target,++ptr)
	*target=*ptr;
      return;
    }
#endif
    idx.resize(dim);
#ifdef GBASIS_SWAP    
    tdeg_t x(x_);
    swap_indices(x.tab);
#else
    const tdeg_t & x= x_;
#endif
    const short * ptr=x.tab;
#if GROEBNER_VARS==15
    if (order.o==_3VAR_ORDER){
      ++ptr;
      for (int i=1;i<=3;++ptr,++i)
	idx[3-i]=*ptr;
      ++ptr;
      for (int i=1;i<=dim-3;++ptr,++i)
	idx[dim-i]=*ptr;
      return;
    }
    if (order.o==_7VAR_ORDER){
      ++ptr;
      for (int i=1;i<=7;++ptr,++i)
	idx[7-i]=*ptr;
      ++ptr;
      for (int i=1;i<=dim-7;++ptr,++i)
	idx[dim-i]=*ptr;
      return;
    }
    if (order.o==_11VAR_ORDER){
      ++ptr;
      for (int i=1;i<=11;++ptr,++i)
	idx[11-i]=*ptr;
      ++ptr;
      for (int i=1;i<=dim-11;++ptr,++i)
	idx[dim-i]=*ptr;
      return;
    }
#endif
    if (order.o==_REVLEX_ORDER || order.o==_TDEG_ORDER)
      ++ptr;
    if (order.o==_REVLEX_ORDER){
      for (int i=1;i<=dim;++ptr,++i)
	idx[dim-i]=*ptr;
    }
    else {
      for (int i=0;i<dim;++ptr,++i)
	idx[i]=*ptr;
#ifdef GIAC_64VARS
      idx[0]/=2;
#endif
    }
  }
  
  bool disjoint(const tdeg_t & a,const tdeg_t & b,order_t order,short dim){
#ifdef GIAC_64VARS
    if (a.tab[0]%2){
      if (!b.tab[0]%2)
	COUT << "erreur" << endl;
      const degtype * xptr=(degtype *)(a.ui+1),*xend=xptr+dim,*yptr=(degtype *)(b.ui+1);
      for (;xptr!=xend;++yptr,++xptr){
	if (*xptr && *yptr)
	  return false;
      }
      return true;
    }
#endif
#if GROEBNER_VARS==15
    if (order.o==_3VAR_ORDER){
      if ( (a.tab[1] && b.tab[1]) ||
	   (a.tab[2] && b.tab[2]) ||
	   (a.tab[3] && b.tab[3]) ||
	   (a.tab[5] && b.tab[5]) ||
	   (a.tab[6] && b.tab[6]) ||
	   (a.tab[7] && b.tab[7]) ||
	   (a.tab[8] && b.tab[8]) ||
	   (a.tab[9] && b.tab[9]) ||
	   (a.tab[10] && b.tab[10]) ||
	   (a.tab[11] && b.tab[11]) ||
	   (a.tab[12] && b.tab[12]) ||
	   (a.tab[13] && b.tab[13]) ||
	   (a.tab[14] && b.tab[14]) ||
	   (a.tab[15] && b.tab[15]) )
	return false;
      return true;
    }
    if (order.o==_7VAR_ORDER){
      if ( (a.tab[1] && b.tab[1]) ||
	   (a.tab[2] && b.tab[2]) ||
	   (a.tab[3] && b.tab[3]) ||
	   (a.tab[4] && b.tab[4]) ||
	   (a.tab[5] && b.tab[5]) ||
	   (a.tab[6] && b.tab[6]) ||
	   (a.tab[7] && b.tab[7]) ||
	   (a.tab[9] && b.tab[9]) ||
	   (a.tab[10] && b.tab[10]) ||
	   (a.tab[11] && b.tab[11]) ||
	   (a.tab[12] && b.tab[12]) ||
	   (a.tab[13] && b.tab[13]) ||
	   (a.tab[14] && b.tab[14]) ||
	   (a.tab[15] && b.tab[15]) )
	return false;
      return true;
    }
    if (order.o==_11VAR_ORDER){
      if ( (a.tab[1] && b.tab[1]) ||
	   (a.tab[2] && b.tab[2]) ||
	   (a.tab[3] && b.tab[3]) ||
	   (a.tab[4] && b.tab[4]) ||
	   (a.tab[5] && b.tab[5]) ||
	   (a.tab[6] && b.tab[6]) ||
	   (a.tab[7] && b.tab[7]) ||
	   (a.tab[8] && b.tab[8]) ||
	   (a.tab[9] && b.tab[9]) ||
	   (a.tab[10] && b.tab[10]) ||
	   (a.tab[11] && b.tab[11]) ||
	   (a.tab[13] && b.tab[13]) ||
	   (a.tab[14] && b.tab[14]) ||
	   (a.tab[15] && b.tab[15]))
	return false;
      return true;
    }
#endif
    const short * it=a.tab, * jt=b.tab;
#ifdef GBASIS_SWAP
    const short * itend=it+GROEBNER_VARS+1;
#endif
    if (order.o==_REVLEX_ORDER || order.o==_TDEG_ORDER){
      ++it; ++jt;
    }
#ifndef GBASIS_SWAP
    const short * itend=it+dim;
#endif
    for (;it<itend;++jt,++it){
      if (*it && *jt)
	return false;
    }
    return true;
  }

  // polynomial are vector< T_unsigned<gen,tdeg_t> >

  struct poly8 {
    std::vector< T_unsigned<gen,tdeg_t> > coord;
    // lex order is implemented using tdeg_t as a list of degrees
    // tdeg uses total degree 1st then partial degree in lex order, max 7 vars
    // revlex uses total degree 1st then opposite of partial degree in reverse ordre, max 7 vars
    order_t order; // _PLEX_ORDER, _REVLEX_ORDER or _TDEG_ORDER or _7VAR_ORDER or _11VAR_ORDER
    short int dim;
    unsigned sugar;
    void dbgprint() const;
    poly8():dim(0),sugar(0) {order.o=_PLEX_ORDER; order.lex=0; order.dim=0;}
    poly8(order_t o_,int dim_): order(o_),dim(dim_),sugar(0) {order.dim=dim_;}
    poly8(const polynome & p,order_t o_){
      order=o_;
      dim=p.dim;
      order.dim=p.dim;
      if (order.o%4!=3){
	if (p.is_strictly_greater==i_lex_is_strictly_greater)
	  order.o=_PLEX_ORDER;
	if (p.is_strictly_greater==i_total_revlex_is_strictly_greater)
	  order.o=_REVLEX_ORDER;
	if (p.is_strictly_greater==i_total_lex_is_strictly_greater)
	  order.o=_TDEG_ORDER;
      }
      if (
#ifdef GIAC_64VARS
	  0 &&
#endif
	  p.dim>GROEBNER_VARS)
	CERR << "Number of variables is too large to be handled by giac";
      else {
	coord.reserve(p.coord.size());
	for (unsigned i=0;i<p.coord.size();++i){
	  coord.push_back(T_unsigned<gen,tdeg_t>(p.coord[i].value,tdeg_t(p.coord[i].index,order)));
	}
      }
      if (coord.empty())
	sugar=0;
      else
	sugar=coord.front().u.total_degree(order);
    }
    void get_polynome(polynome & p) const {
      p.dim=dim;
      switch (order.o){
      case _REVLEX_ORDER:
	p.is_strictly_greater=i_total_revlex_is_strictly_greater;
	break;
      case _3VAR_ORDER:
	p.is_strictly_greater=i_3var_is_strictly_greater;
	break;
      case _7VAR_ORDER:
	p.is_strictly_greater=i_7var_is_strictly_greater;
	break;
      case _11VAR_ORDER:
	p.is_strictly_greater=i_11var_is_strictly_greater;
	break;
      case _TDEG_ORDER:
	p.is_strictly_greater=i_total_lex_is_strictly_greater;
	break;
      default:
      case _PLEX_ORDER:
	p.is_strictly_greater=i_lex_is_strictly_greater;
	break;
      }
      p.coord.clear();
      p.coord.reserve(coord.size());
      index_t idx(dim);
      for (unsigned i=0;i<coord.size();++i){
	get_index(coord[i].u,idx,order,dim);
	p.coord.push_back(monomial<gen>(coord[i].g,idx));
      }
      // if (order==_3VAR_ORDER || order==_7VAR_ORDER || order==_11VAR_ORDER) p.tsort();
    }
  };
  bool operator == (const poly8 & p,const poly8 &q){
    if (p.coord.size()!=q.coord.size())
      return false;
    for (unsigned i=0;i<p.coord.size();++i){
      if (p.coord[i].u!=q.coord[i].u || p.coord[i].g!=q.coord[i].g)
	return false;
    }
    return true;
  }

#ifdef NSPIRE
  template<class T> nio::ios_base<T> & operator << (nio::ios_base<T> & os, const poly8 & p)
#else
  ostream & operator << (ostream & os, const poly8 & p)
#endif
  {
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
#ifdef GIAC_64VARS
      if (it->u.tdeg%2){
	degtype * i=(degtype *)(it->u.ui+1);
	int s=it->u.order_.dim;
	for (int j=0;j<s;++j){
	  t2=i[j];
	  if (t2)
	    os << "*x"<< j << "^" << t2  ;
	}
	++it;
	if (it==itend)
	  break;
	os << " + ";
	continue;
      }
#endif
      short tab[GROEBNER_VARS+1];
      it->u.get_tab(tab);
      switch (p.order.o){
      case _PLEX_ORDER:
	for (int i=0;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2)
	    os << "*x"<< i << "^" << t2  ;
	}
	break;
      case _REVLEX_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;
#if GROEBNER_VARS==15
      case _3VAR_ORDER:
	for (int i=1;i<=3;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 3-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=5;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 4+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _7VAR_ORDER:
	for (int i=1;i<=7;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 7-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=9;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 8+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _11VAR_ORDER:
	for (int i=1;i<=11;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 11-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=13;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 12+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
#endif
      case _TDEG_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  if (t2)
	    os << "*x"<< i-1 << "^" << t2  ;
	}
	break;
      }
      ++it;
      if (it==itend)
	break;
      os << " + ";
    }
    return os;
  }


  void poly8::dbgprint() const { 
    CERR << *this << endl;
  }

  class vectpoly8:public vector<poly8>{
  public:
    void dbgprint() const { CERR << *this << endl; }
  };

  void vectpoly_2_vectpoly8(const vectpoly & v,order_t order,vectpoly8 & v8){
    v8.clear();
    v8.reserve(v.size());
    for (unsigned i=0;i<v.size();++i){
      v8.push_back(poly8(v[i],order));
    }
  }

  void vectpoly8_2_vectpoly(const vectpoly8 & v8,vectpoly & v){
    v.clear();
    v.reserve(v8.size());
    for (unsigned i=0;i<v8.size();++i){
      v.push_back(polynome(v8[i].dim));
      v8[i].get_polynome(v[i]);
    }
  }

  // Groebner basis code begins here

  gen inplace_ppz(poly8 & p,bool divide=true,bool quick=false){
    vector< T_unsigned<gen,tdeg_t> >::iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend)
      return 1;
    gen res=(itend-1)->g;
    for (;it!=itend;++it){
      if (it->g.type==_INT_){
	res=it->g;
	if (quick)
	  return 1;
	break;
      }
    }
    if (res.type==_ZINT)
      res=*res._ZINTptr; // realloc for inplace gcd
    for (it=p.coord.begin();it!=itend;++it){
      if (res.type==_ZINT && it->g.type==_ZINT){
	mpz_gcd(*res._ZINTptr,*res._ZINTptr,*it->g._ZINTptr);
      }
      else
	res=gcd(res,it->g);
      if (is_one(res))
	return 1;
    }
    if (!divide)
      return res;
#ifndef USE_GMP_REPLACEMENTS
    if (res.type==_INT_ && res.val>0){
      for (it=p.coord.begin();it!=itend;++it){
	if (it->g.type!=_ZINT || it->g.ref_count()>1)
	  it->g=it->g/res; 
	else
	  mpz_divexact_ui(*it->g._ZINTptr,*it->g._ZINTptr,res.val);
      }
      return res;
    }
    if (res.type==_ZINT){
      for (it=p.coord.begin();it!=itend;++it){
	if (it->g.type!=_ZINT || it->g.ref_count()>1)
	  it->g=it->g/res; 
	else
	  mpz_divexact(*it->g._ZINTptr,*it->g._ZINTptr,*res._ZINTptr);
      }
      return res;
    }
#endif
    for (it=p.coord.begin();it!=itend;++it){
      it->g=it->g/res; 
    }
    return res;
  }

  void inplace_mult(const gen & g,vector< T_unsigned<gen,tdeg_t> > & v){
    std::vector< T_unsigned<gen,tdeg_t> >::iterator it1=v.begin(),it1end=v.end();
    for (;it1!=it1end;++it1){
#if 0
      it1->g=g*(it1->g);
#else
      type_operator_times(g,it1->g,it1->g);
#endif
    }
  }
  
#define GBASIS_HEAP
#ifdef GBASIS_HEAP
  // heap: remove bitfields if that's not enough
  struct heap_t {
    unsigned i:16; // index in pairs of quotients/divisors
    unsigned qi:24;
    unsigned gj:24; // monomial index for quotient and divisor
    tdeg_t u; // product
  };

  // inline bool operator > (const heap_t & a,const heap_t & b){ return a.u>b.u; }

  // inline bool operator < (const heap_t & a,const heap_t & b){ return b>a; }
  struct heap_t_compare {
    order_t order;
    const heap_t * ptr;
    inline bool operator () (unsigned a,unsigned b){
      return !tdeg_t_greater((ptr+a)->u,(ptr+b)->u,order);
      // return (ptr+a)->u<(ptr+b)->u;
    }
    heap_t_compare(const vector<heap_t> & v,order_t o):order(o),ptr(v.empty()?0:&v.front()){};
  };

  struct compare_heap_t {
    order_t order;
    inline bool operator () (const heap_t & a,const heap_t & b){
      return !tdeg_t_greater(a.u,b.u,order);
      // return (ptr+a)->u<(ptr+b)->u;
    }
    compare_heap_t(order_t o):order(o) {}
  };

  struct heap_t_ptr {
    heap_t * ptr;
  };

  void heap_reduce(const poly8 & f,const vectpoly8 & g,const vector<unsigned> & G,unsigned excluded,vectpoly8 & q,poly8 & rem,poly8& R,gen & s,environment * env){
    // divides f by g[G[0]] to g[G[G.size()-1]] except maybe g[G[excluded]]
    // first implementation: use quotxsient heap for all quotient/divisor
    // do not use heap chain
    // ref Monaghan Pearce if g.size()==1
    // R is a temporary polynomial, should be different from f
    if (&rem==&f){
      R.dim=f.dim; R.order=f.order;
      heap_reduce(f,g,G,excluded,q,R,R,s,env);
      swap(rem.coord,R.coord);
      if (debug_infolevel>1000)
	g.dbgprint(); // instantiate dbgprint()
      return;
    }
    rem.coord.clear();
    if (f.coord.empty())
      return ;
    if (q.size()<G.size())
      q.resize(G.size());
    unsigned guess=0;
    for (unsigned i=0;i<G.size();++i){
      q[i].dim=f.dim;
      q[i].order=f.order;
      q[i].coord.clear();
      guess += unsigned(g[G[i]].coord.size());
    }
    vector<heap_t> H;
    compare_heap_t key(f.order);
    H.reserve(guess);
    vecteur invlcg(G.size());
    if (env && env->moduloon){
      for (unsigned i=0;i<G.size();++i){
	invlcg[i]=invmod(g[G[i]].coord.front().g,env->modulo);
      }
    }
    s=1;
    gen c,numer,denom;
    longlong C;
    unsigned k=0,i; // k=position in f
    tdeg_t m;
    bool finish=false;
    bool small0=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
    int p=env?env->modulo.val:0;
    while (!H.empty() || k<f.coord.size()){
      // is highest remaining degree in f or heap?
      if (k<f.coord.size() && (H.empty() || tdeg_t_greater(f.coord[k].u,H.front().u,f.order)) ){
	// it's in f or both
	m=f.coord[k].u;
	if (small0)
	  C=smod(f.coord[k].g,p).val;
	else {
	  if (s==1)
	    c=f.coord[k].g;
	  else
	    c=s*f.coord[k].g;
	}
	++k;
      }
      else {
	m=H.front().u;
	c=0;
	C=0;
      }
      // extract from heap all terms having m as monomials, substract from c
      while (!H.empty() && H.front().u==m){
	std::pop_heap(H.begin(),H.end(),key);
	heap_t & current=H.back(); // was root node of the heap
	const poly8 & gcurrent = g[G[current.i]];
	if (small0)
	  C -= longlong(q[current.i].coord[current.qi].g.val) * smod(gcurrent.coord[current.gj].g,p).val; 
	else {
	  if (env && env->moduloon){
	    c -= q[current.i].coord[current.qi].g * gcurrent.coord[current.gj].g;
	  }
	  else {
	    fxnd(q[current.i].coord[current.qi].g,numer,denom);
	    if (denom==s)
	      c -= numer*gcurrent.coord[current.gj].g;
	    else {
	      if (denom==1)
		c -= s*numer*gcurrent.coord[current.gj].g;
	      else
		c -= (s/denom)*numer*gcurrent.coord[current.gj].g;
	    }
	  }
	}
	if (current.gj<gcurrent.coord.size()-1){
	  ++current.gj;
	  current.u=q[current.i].coord[current.qi].u+gcurrent.coord[current.gj].u;
	  push_heap(H.begin(),H.end(),key);
	}
	else
	  H.pop_back();
      }
      if (small0){
	C %= p;
	if (C==0)
	  continue;
	c=C;
      }
      else {
	if (env && env->moduloon)
	  c=smod(c,env->modulo);
	if (c==0)
	  continue;
      }
      // divide (c,m) by one of the g if possible, otherwise push in remainder
      if (finish)
	i=unsigned(G.size());
      else {
	finish=true;
	for (i=0;i<G.size();++i){
	  if (i==excluded || g[G[i]].coord.empty())
	    continue;
	  if (tdeg_t_greater(m,g[G[i]].coord.front().u,f.order)){
	    finish=false;
	    if (tdeg_t_all_greater(m,g[G[i]].coord.front().u,f.order))
	      break;
	  }
	}
      }
      if (i==G.size()){
	if (s==1)
	  rem.coord.push_back(T_unsigned<gen,tdeg_t>(c,m)); // add c/s*m to remainder
	else {
	  //rem.coord.push_back(T_unsigned<gen,tdeg_t>(c/s,m)); // add c/s*m to remainder
	  rem.coord.push_back(T_unsigned<gen,tdeg_t>(Tfraction<gen>(c,s),m)); // add c/s*m to remainder
	}
	continue;
      }
      // add c/s*m/leading monomial of g[G[i]] to q[i]
      tdeg_t monom=m-g[G[i]].coord.front().u;
      if (env && env->moduloon){
	if (invlcg[i]!=1){
	  if (invlcg[i]==-1)
	    c=-c;
	  else
	    c=smod(c*invlcg[i],env->modulo);
	}
	q[i].coord.push_back(T_unsigned<gen,tdeg_t>(c,monom));
      }
      else {
	gen lcg=g[G[i]].coord.front().g;
	gen pgcd=simplify3(lcg,c);
	if (is_positive(-lcg,context0)){
	  lcg=-lcg;
	  c=-c;
	}
	s=s*lcg;
	if (s==1)
	  q[i].coord.push_back(T_unsigned<gen,tdeg_t>(c,monom));
	else
	  q[i].coord.push_back(T_unsigned<gen,tdeg_t>(Tfraction<gen>(c,s),monom));
      }
      // push in heap
      if (g[G[i]].coord.size()>1){
	heap_t current={i,int(q[i].coord.size())-1,1,g[G[i]].coord[1].u+monom};
	H.push_back(current);
	push_heap(H.begin(),H.end(),key);
      }
    } // end main heap pseudo-division loop
  }


  void heap_reduce(const poly8 & f,const vectpoly8 & g,const vector<unsigned> & G,unsigned excluded,vectpoly8 & q,poly8 & rem,poly8& TMP1,environment * env){
    gen s;
    if (debug_infolevel>2)
      CERR << f << " = " << endl;
    heap_reduce(f,g,G,excluded,q,rem,TMP1,s,env);
    // end up by multiplying rem by s (so that everything is integer)
    if (debug_infolevel>2){
      for (unsigned i=0;i<G.size();++i)
	CERR << "(" << g[G[i]]<< ")*(" << q[i] << ")+ ";
      CERR << rem << endl;
    }
    if (env && env->moduloon){
      if (!rem.coord.empty() && rem.coord.front().g!=1)
	smallmult(invmod(rem.coord.front().g,env->modulo),rem.coord,rem.coord,env->modulo.val);
      return;
    }
    if (s!=1)
      smallmult(s,rem.coord,rem.coord);
    gen tmp=inplace_ppz(rem);
    if (debug_infolevel>1)
      CERR << "ppz was " << tmp << endl;
  }

#endif // GBASIS_HEAP


  void smallmult(const gen & a,poly8 & p,gen & m){
    std::vector< T_unsigned<gen,tdeg_t> >::iterator pt=p.coord.begin(),ptend=p.coord.end();
    if (a.type==_INT_ && m.type==_INT_){
      for (;pt!=ptend;++pt){
	if (pt->g.type==_INT_)
	  pt->g=(longlong(pt->g.val)*a.val)%m.val;
	else
	  pt->g=smod(a*pt->g,m);
      }
    }
    else {
      for (;pt!=ptend;++pt){
	pt->g=smod(a*pt->g,m);
      }
    }
  }

  // p - a*q shifted mod m -> r
  void smallmultsub(const poly8 & p,unsigned pos,int a,const poly8 & q,const tdeg_t & shift,poly8 & r,int m){
    r.coord.clear();
    r.coord.reserve(p.coord.size()+q.coord.size());
    vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin()+pos,itend=p.coord.end(),jt=q.coord.begin(),jtend=q.coord.end();
    for (;jt!=jtend;++jt){
      tdeg_t v=jt->u+shift;
      for (;it!=itend && tdeg_t_strictly_greater(it->u,v,p.order);++it){
	r.coord.push_back(*it);
      }
      if (it!=itend && it->u==v){
	if (it->g.type==_INT_ && jt->g.type==_INT_){
	  int tmp=(it->g.val-longlong(a)*jt->g.val)%m;
	  if (tmp)
	    r.coord.push_back(T_unsigned<gen,tdeg_t>(tmp,v));
	}
	else
	  r.coord.push_back(T_unsigned<gen,tdeg_t>(smod(it->g-a*jt->g,m),v));
	++it;
      }
      else {
	if (jt->g.type==_INT_){
	  int tmp=(-longlong(a)*jt->g.val)%m;
	  r.coord.push_back(T_unsigned<gen,tdeg_t>(tmp,v));
	}
	else
	  r.coord.push_back(T_unsigned<gen,tdeg_t>(smod(-a*jt->g,m),v));
      }
    }
    for (;it!=itend;++it){
      r.coord.push_back(*it);
    }
  }

  // a and b are assumed to be _ZINT
  static void linear_combination(const gen & a,const poly8 &p,tdeg_t * ashift,const gen &b,const poly8 & q,tdeg_t * bshift,poly8 & r,environment * env){
    r.coord.clear();
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=q.coord.begin(),jtend=q.coord.end();
    r.coord.reserve((itend-it)+(jtend-jt));
    mpz_t tmpz;
    mpz_init(tmpz);
    if (jt!=jtend){
      tdeg_t v=jt->u;
      if (bshift)
	v=v+*bshift;
      for (;it!=itend;){
	tdeg_t u=it->u;
	if (ashift)
	  u=u+*ashift;
	if (u==v){
	  gen g;
#ifndef USE_GMP_REPLACEMENTS
	  if ( (it->g.type==_INT_ || it->g.type==_ZINT) &&
	       (jt->g.type==_INT_ || jt->g.type==_ZINT) ){
	    if (it->g.type==_INT_)
	      mpz_mul_si(tmpz,*a._ZINTptr,it->g.val);
	    else
	      mpz_mul(tmpz,*a._ZINTptr,*it->g._ZINTptr);
	    if (jt->g.type==_INT_){
	      if (jt->g.val>=0)
		mpz_addmul_ui(tmpz,*b._ZINTptr,jt->g.val);
	      else
		mpz_submul_ui(tmpz,*b._ZINTptr,-jt->g.val);
	    }
	    else
	      mpz_addmul(tmpz,*b._ZINTptr,*jt->g._ZINTptr);
	    if (mpz_sizeinbase(tmpz,2)<31)
	      g=int(mpz_get_si(tmpz));
	    else {
	      ref_mpz_t * ptr =new ref_mpz_t;
	      mpz_swap(ptr->z,tmpz);
	      g=ptr; // g=tmpz;
	    }
	  }
	  else
#endif
	    g=a*it->g+b*jt->g;
	  if (env && env->moduloon)
	    g=smod(g,env->modulo);
	  if (!is_zero(g))
	    r.coord.push_back(T_unsigned<gen,tdeg_t>(g,u));
	  ++it; ++jt;
	  if (jt==jtend)
	    break;
	  v=jt->u;
	  if (bshift)
	    v=v+*bshift;
	  continue;
	}
	if (tdeg_t_strictly_greater(u,v,p.order)){
	  gen g=a*it->g;
	  if (env && env->moduloon)
	    g=smod(g,env->modulo);
	  r.coord.push_back(T_unsigned<gen,tdeg_t>(g,u));
	  ++it;
	}
	else {
	  gen g=b*jt->g;
	  if (env && env->moduloon)
	    g=smod(g,env->modulo);
	  r.coord.push_back(T_unsigned<gen,tdeg_t>(g,v));
	  ++jt;
	  if (jt==jtend)
	    break;
	  v=jt->u;
	  if (bshift)
	    v=v+*bshift;
	}
      }
    }
    for (;it!=itend;++it){
      tdeg_t u=it->u;
      if (ashift)
	u=u+*ashift;
      gen g=a*it->g;
      if (env && env->moduloon)
	g=smod(g,env->modulo);
      r.coord.push_back(T_unsigned<gen,tdeg_t>(g,u));
    }
    for (;jt!=jtend;++jt){
      tdeg_t v=jt->u;
      if (bshift)
	v=v+*bshift;
      gen g=b*jt->g;
      if (env && env->moduloon)
	g=smod(g,env->modulo);
      r.coord.push_back(T_unsigned<gen,tdeg_t>(g,v));
    }
    mpz_clear(tmpz);
  }

  // check that &v1!=&v and &v2!=&v
  static void sub(const poly8 & v1,const poly8 & v2,poly8 & v,environment * env){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it1=v1.coord.begin(),it1end=v1.coord.end(),it2=v2.coord.begin(),it2end=v2.coord.end();
    gen g;
    v.coord.clear();
    v.coord.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=it1->g-it2->g;
	if (env && env->moduloon)
	  g=smod(g,env->modulo);
	if (!is_zero(g))
	  v.coord.push_back(T_unsigned<gen,tdeg_t>(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (tdeg_t_strictly_greater(it1->u,it2->u,v1.order)){
	  v.coord.push_back(*it1);
	  ++it1;
	}
	else {
	  v.coord.push_back(T_unsigned<gen,tdeg_t>(-it2->g,it2->u));
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.coord.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.coord.push_back(T_unsigned<gen,tdeg_t>(-it2->g,it2->u));
  }
  

  void reduce(const poly8 & p,const vectpoly8 & res,const vector<unsigned> & G,unsigned excluded,vectpoly8 & quo,poly8 & rem,poly8 & TMP1, poly8 & TMP2,environment * env){
#if 0
    // heap_reduce is not interesting here because there are too few terms
    // but could be used for a modular division in integer case
    // since it compute quotients
    if (env && env->moduloon){
      heap_reduce(p,res,G,excluded,quo,rem,TMP1,env);
      return;
    }
#endif
#if 0 // heap_reduce is not interesting here, arithmetic takes longer
    if (!env || !env->moduloon){
      heap_reduce(p,res,G,excluded,quo,rem,TMP1,env);
      return;
    }
#endif
    // last chance of improving = modular method for reduce or modular algo
    if (&p!=&rem)
      rem=p;
    if (p.coord.empty())
      return ;
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator pt,ptend;
    unsigned i,rempos=0;
    bool small0=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
    TMP1.order=p.order; TMP1.dim=p.dim; TMP2.order=p.order; TMP2.dim=p.dim; TMP1.coord.clear();
    for (unsigned count=0;;++count){
      ptend=rem.coord.end();
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      pt=rem.coord.begin()+rempos;
      if (pt>=ptend)
	break;
      for (i=0;i<G.size();++i){
	if (i==excluded || res[G[i]].coord.empty())
	  continue;
	if (tdeg_t_all_greater(pt->u,res[G[i]].coord.front().u,p.order))
	  break;
      }
      if (i==G.size()){ // no leading coeff of G is smaller than the current coeff of rem
	++rempos;
	// if (small0) TMP1.coord.push_back(*pt);
	continue;
      }
      gen a(pt->g),b(res[G[i]].coord.front().g);
      if (small0){
	smallmultsub(rem,0,smod(a*invmod(b,env->modulo),env->modulo).val,res[G[i]],pt->u-res[G[i]].coord.front().u,TMP2,env->modulo.val);
	// smallmultsub(rem,rempos,smod(a*invmod(b,env->modulo),env->modulo).val,res[G[i]],pt->u-res[G[i]].coord.front().u,TMP2,env->modulo.val);
	// rempos=0; // since we have removed the beginning of rem (copied in TMP1)
	swap(rem.coord,TMP2.coord);
	continue;
      }
      TMP1.coord.clear();
      TMP2.coord.clear();
      tdeg_t resshift=pt->u-res[G[i]].coord.front().u;
      if (env && env->moduloon){
	gen ab=a;
	if (b!=1)
	  ab=a*invmod(b,env->modulo);
	ab=smod(ab,env->modulo);
	smallshift(res[G[i]].coord,resshift,TMP1.coord);
	if (ab!=1)
	  smallmult(ab,TMP1,env->modulo); 
	sub(rem,TMP1,TMP2,env);
      }
      else {
	// -b*rem+a*shift(res[G[i]])
	simplify(a,b);
	if (b==-1){
	  b=-b;
	  a=-a;
	}
	gen c=-b;
	if (a.type==_ZINT && c.type==_ZINT && !is_one(a) && !is_one(b))
	  linear_combination(c,rem,0,a,res[G[i]],&resshift,TMP2,0);
	else {
	  smallshift(res[G[i]].coord,resshift,TMP1.coord);
	  if (!is_one(a))
	    inplace_mult(a,TMP1.coord);
	  if (!is_one(b))
	    inplace_mult(b,rem.coord);
	  sub(rem,TMP1,TMP2,0); 
	}
	//if (count % 6==5) inplace_ppz(TMP2,true,true); // quick gcd check
      }
      swap(rem.coord,TMP2.coord);
    }
    if (env && env->moduloon){
      // if (small0) swap(rem.coord,TMP1.coord);
      if (!rem.coord.empty() && rem.coord.front().g!=1)
	smallmult(invmod(rem.coord.front().g,env->modulo),rem.coord,rem.coord,env->modulo.val);
      return;
    }    
    gen g=inplace_ppz(rem);
    if (debug_infolevel>2){
      if (rem.coord.empty())
	CERR << "0 reduction" << endl;
      if (g.type==_ZINT && mpz_sizeinbase(*g._ZINTptr,2)>16)
	CERR << "ppz size was " << mpz_sizeinbase(*g._ZINTptr,2) << endl;
    }
  }

  void reduce1small(poly8 & p,const poly8 & q,poly8 & TMP1, poly8 & TMP2,environment * env){
    if (p.coord.empty())
      return ;
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator pt,ptend;
    unsigned rempos=0;
    TMP1.coord.clear();
    const tdeg_t & u = q.coord.front().u;
    const gen g=q.coord.front().g;
    for (unsigned count=0;;++count){
      ptend=p.coord.end();
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      pt=p.coord.begin()+rempos;
      if (pt>=ptend)
	break;
      if (!tdeg_t_all_greater(pt->u,u,p.order)){
	++rempos;
	// TMP1.coord.push_back(*pt);
	continue;
      }
      smallmultsub(p,0,smod(pt->g*invmod(g,env->modulo),env->modulo).val,q,pt->u-u,TMP2,env->modulo.val);
      // smallmultsub(p,rempos,smod(a*invmod(b,env->modulo),env->modulo).val,q,pt->u-u,TMP2,env->modulo.val);
      // rempos=0; // since we have removed the beginning of rem (copied in TMP1)
      swap(p.coord,TMP2.coord);
    }
    // if (small0) swap(p.coord,TMP1.coord);
    if (env && env->moduloon && !p.coord.empty() && p.coord.front().g!=1)
      smallmult(invmod(p.coord.front().g,env->modulo),p.coord,p.coord,env->modulo.val);
  }

  // reduce with respect to itself the elements of res with index in G
  void reduce(vectpoly8 & res,vector<unsigned> G,environment * env){
    if (res.empty() || G.empty())
      return;
    poly8 pred(res.front().order,res.front().dim),
      TMP1(res.front().order,res.front().dim),
      TMP2(res.front().order,res.front().dim);
    vectpoly8 q;
    // reduce res
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      poly8 & p=res[i];
      reduce(p,res,G,i,q,pred,TMP1,TMP2,env);
      swap(res[i].coord,pred.coord);
      pred.sugar=res[i].sugar;
    }
  }

  void spoly(const poly8 & p,const poly8 & q,poly8 & res,poly8 & TMP1, environment * env){
    if (p.coord.empty()){
      res=q;
      return ;
    }
    if (q.coord.empty()){
      res= p;
      return;
    }
    const tdeg_t & pi = p.coord.front().u;
    const tdeg_t & qi = q.coord.front().u;
    tdeg_t lcm;
    index_lcm(pi,qi,lcm,p.order);
    tdeg_t pshift=lcm-pi;
    unsigned sugarshift=pshift.total_degree(p.order);
    // adjust sugar for res
    res.sugar=p.sugar+sugarshift;
    // CERR << "spoly " << res.sugar << " " << pi << qi << endl;
    gen a=p.coord.front().g,b=q.coord.front().g;
    simplify3(a,b);
    if (debug_infolevel>2)
      CERR << "spoly " << a << " " << b << endl;
    if (a.type==_ZINT && b.type==_ZINT){
      tdeg_t u=lcm-pi,v=lcm-qi;
      linear_combination(b,p,&u,a,q,&v,res,env);
    }
    else {
      poly8 tmp1(p),tmp2(q);
      smallshift(tmp1.coord,lcm-pi,tmp1.coord);
      smallmult(b,tmp1.coord,tmp1.coord);
      smallshift(tmp2.coord,lcm-qi,tmp2.coord);
      smallmult(a,tmp2.coord,tmp2.coord);
      sub(tmp1,tmp2,res,env);
    }
    a=inplace_ppz(res);
    if (debug_infolevel>2)
      CERR << "spoly ppz " << a << endl;
  }

  static void gbasis_update(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpoly8 & res,unsigned pos,poly8 & TMP1,poly8 & TMP2,vectpoly8 & vtmp,environment * env){
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin gbasis update " << endl;
    const poly8 & h = res[pos];
    order_t order=h.order;
    vector<unsigned> C;
    C.reserve(G.size());
    const tdeg_t & h0=h.coord.front().u;
    tdeg_t tmp1,tmp2;
    // C is used to construct new pairs
    // create pairs with h and elements g of G, then remove
    // -> if g leading monomial is prime with h, remove the pair
    // -> if g leading monomial is not disjoint from h leading monomial
    //    keep it only if lcm of leading monomial is not divisible by another one
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[G[i]].coord.empty() || disjoint(h0,res[G[i]].coord.front().u,res.front().order,res.front().dim))
	continue;
      index_lcm(h0,res[G[i]].coord.front().u,tmp1,order); // h0 and G[i] leading monomial not prime together
      unsigned j;
      for (j=0;j<G.size();++j){
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return;
	if (i==j || res[G[j]].coord.empty())
	  continue;
	index_lcm(h0,res[G[j]].coord.front().u,tmp2,order);
	if (tdeg_t_all_greater(tmp1,tmp2,order)){
	  // found another pair, keep the smallest, or the first if equal
	  if (tmp1!=tmp2)
	    break; 
	  if (i>j)
	    break;
	}
      } // end for j
      if (j==G.size())
	C.push_back(G[i]);
    }
    vector< pair<unsigned,unsigned> > B1;
    B1.reserve(B.size()+C.size());
    for (unsigned i=0;i<B.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	continue;
      index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,tmp1,order);
      if (!tdeg_t_all_greater(tmp1,h0,order)){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].first].coord.front().u,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].second].coord.front().u,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
    }
    // B <- B union pairs(h,g) with g in C
    for (unsigned i=0;i<C.size();++i)
      B1.push_back(pair<unsigned,unsigned>(pos,C[i]));
    swap(B1,B);
    // Update G by removing elements with leading monomial >= leading monomial of h
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin Groebner interreduce " << endl;
    C.clear();
    C.reserve(G.size());
    vector<unsigned> hG(1,pos);
    bool small0=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (!res[G[i]].coord.empty() && !tdeg_t_all_greater(res[G[i]].coord.front().u,h0,order)){
	// reduce res[G[i]] with respect to h
	if (small0)
	  reduce1small(res[G[i]],h,TMP1,TMP2,env);
	else
	  reduce(res[G[i]],res,hG,-1,vtmp,res[G[i]],TMP1,TMP2,env);
	C.push_back(G[i]);
      }
      // NB: removing all pairs containing i in it does not work
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " end Groebner interreduce " << endl;
    C.push_back(pos);
    swap(C,G);
  }

  bool in_gbasis(vectpoly8 & res,vector<unsigned> & G,environment * env,bool sugar){
    poly8 TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vectpoly8 vtmp;
    vector< pair<unsigned,unsigned> > B;
    order_t order=res.front().order;
    //if (order==_PLEX_ORDER)
      sugar=false; // otherwise cyclic6 fails (bus error), don't know why
    for (unsigned l=0;l<res.size();++l){
      gbasis_update(G,B,res,l,TMP1,TMP2,vtmp,env);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      if (debug_infolevel>1)
	CERR << CLOCK() << " number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small0,cur;
      unsigned smallpos,smallsugar=0,cursugar=0;
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small0,order);
      if (sugar)
	smallsugar=res[B[smallpos].first].sugar+(small0-res[B[smallpos].first].coord.front().u).total_degree(order);
      for (unsigned i=smallpos+1;i<B.size();++i){
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	if (sugar)
	  cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree(order);
	bool doswap;
	if (order.o==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small0,cur,order);
	else {
	  if (cursugar!=smallsugar)
	    doswap = smallsugar > cursugar;
	  else
	    doswap=tdeg_t_strictly_greater(small0,cur,order);
	}
	if (doswap){
	  // CERR << "swap " << cursugar << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small0,cur); // small0=cur;
	  swap(smallsugar,cursugar);
	  smallpos=i;
	}
      }
      pair<unsigned,unsigned> bk=B[smallpos];
      if (debug_infolevel>1 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	CERR << CLOCK() << " reducing pair with 1 element not in basis " << bk << endl;
      B.erase(B.begin()+smallpos);
      poly8 h(res.front().order,res.front().dim);
      spoly(res[bk.first],res[bk.second],h,TMP1,env);
      if (debug_infolevel>1)
	CERR << CLOCK() << " reduce begin, pair " << bk << " remainder size " << h.coord.size() << endl;
      reduce(h,res,G,-1,vtmp,h,TMP1,TMP2,env);
      if (debug_infolevel>1){
	if (debug_infolevel>3){ CERR << h << endl; }
	CERR << CLOCK() << " reduce end, remainder size " << h.coord.size() << endl;
      }
      if (!h.coord.empty()){
	res.push_back(h);
	gbasis_update(G,B,res,unsigned(res.size()-1),TMP1,TMP2,vtmp,env);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " basis indexes " << G << " pairs indexes " << B << endl;
      }
    }
    return true;
  }

  longlong invmod(longlong a,longlong b){
    if (a==1 || a==-1 || a==1-b)
      return a;
    longlong aa(1),ab(0),ar(0);
#ifdef VISUALC
    longlong q,r;
    while (b){
      q=a/b;
      r=a-q*b;
      ar=aa-q*ab;
      a=b;
      b=r;
      aa=ab;
      ab=ar;
    }
#else
    lldiv_t qr;
    while (b){
      qr=lldiv(a,b);
      ar=aa-qr.quot*ab;
      a=b;
      b=qr.rem;
      aa=ab;
      ab=ar;
    }
#endif
    if (a==1)
      return aa;
    if (a!=-1){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Not invertible"));
#endif
      return 0;
    }
    return -aa;
  }

  longlong smod(longlong a,longlong b){
    longlong r=a%b;
    if (r>b/2)
      r -= b;
    else {
      if (r<=-b/2)
	r += b;
    }
    return r;
  }

#if 0 // def __x86_64__
  typedef longlong modint;
  typedef int128_t modint2;
  longlong smod(int128_t a,longlong b){
    longlong r=a%b;
    if (r>b/2)
      r -= b;
    else {
      if (r<=-b/2)
	r += b;
    }
  }

#else
  typedef int modint;
  typedef longlong modint2;
#endif

  struct polymod {
    std::vector< T_unsigned<modint,tdeg_t> > coord;
    // lex order is implemented using tdeg_t as a list of degrees
    // tdeg uses total degree 1st then partial degree in lex order, max 7 vars
    // revlex uses total degree 1st then opposite of partial degree in reverse ordre, max 7 vars
    order_t order; // _PLEX_ORDER, _REVLEX_ORDER or _TDEG_ORDER or _7VAR_ORDER or _11VAR_ORDER
    short int dim;
    unsigned sugar;
    void dbgprint() const;
    void swap(polymod & q){ 
      order_t tmp;
      tmp=order; order=q.order; q.order=tmp;
      int tmp2=dim; dim=q.dim; q.dim=tmp2;
      tmp2=sugar; sugar=q.sugar; q.sugar=tmp2;
      coord.swap(q.coord);
    }
    polymod():dim(0) {order_t tmp={_PLEX_ORDER,0}; order=tmp;}
    polymod(order_t o_,int dim_): dim(dim_) {order=o_; order.dim=dim_;}
    polymod(const polynome & p,order_t o_,modint m){
      order=o_; 
      dim=p.dim;
      order.dim=dim;
      if (order.o%4!=3){
	if (p.is_strictly_greater==i_lex_is_strictly_greater)
	  order.o=_PLEX_ORDER;
	if (p.is_strictly_greater==i_total_revlex_is_strictly_greater)
	  order.o=_REVLEX_ORDER;
	if (p.is_strictly_greater==i_total_lex_is_strictly_greater)
	  order.o=_TDEG_ORDER;
      }
      if (p.dim>GROEBNER_VARS-(order.o==_REVLEX_ORDER || order.o==_TDEG_ORDER)) 
	CERR << "Number of variables is too large to be handled by giac";
      else {
	if (!p.coord.empty()){
	  coord.reserve(p.coord.size());
	  for (unsigned i=0;i<p.coord.size();++i){
	    modint n;
	    if (p.coord[i].value.type==_ZINT)
	      n=modulo(*p.coord[i].value._ZINTptr,m);
	    else
	      n=p.coord[i].value.val % m;
	    coord.push_back(T_unsigned<modint,tdeg_t>(n,tdeg_t(p.coord[i].index,order)));
	  }
	  sugar=coord.front().u.total_degree(order);
	}
      }
    }
    void get_polynome(polynome & p) const {
      p.dim=dim;
      switch (order.o){
      case _PLEX_ORDER:
	p.is_strictly_greater=i_lex_is_strictly_greater;
	break;
      case _REVLEX_ORDER:
	p.is_strictly_greater=i_total_revlex_is_strictly_greater;
	break;
      case _3VAR_ORDER:
	p.is_strictly_greater=i_3var_is_strictly_greater;
	break;
      case _7VAR_ORDER:
	p.is_strictly_greater=i_7var_is_strictly_greater;
	break;
      case _11VAR_ORDER:
	p.is_strictly_greater=i_11var_is_strictly_greater;
	break;
      case _TDEG_ORDER:
	p.is_strictly_greater=i_total_lex_is_strictly_greater;
	break;
      }
      p.coord.clear();
      p.coord.reserve(coord.size());
      index_t idx(dim);
      for (unsigned i=0;i<coord.size();++i){
	get_index(coord[i].u,idx,order,dim);
	p.coord.push_back(monomial<gen>(coord[i].g,idx));
      }
      // if (order==_3VAR_ORDER || order==_7VAR_ORDER || order==_11VAR_ORDER) p.tsort();
    }
  }; // end polymod

  template<class T>
  void increase(vector<T> &v){
    if (v.size()!=v.capacity())
      return;
    vector<T> w;
    w.reserve(v.size()*2);
    for (unsigned i=0;i<v.size();++i){
      w.push_back(T(v[i].order,v[i].dim));
      w[i].coord.swap(v[i].coord);
    }
    v.swap(w);
  }
  
  struct polymod_sort_t {
    polymod_sort_t() {}
    bool operator () (const polymod & p,const polymod & q) const {
      if (q.coord.empty())
	return false;
      if (p.coord.empty())
	return true;
      if (p.coord.front().u==q.coord.front().u)
	return false;
      return tdeg_t_greater(q.coord.front().u,p.coord.front().u,p.order); // p.coord.front().u<q.coord.front().u; 
      // this should be enough to sort groebner basis
    }
  };

  void smallmultmod(modint a,polymod & p,modint m){
    if (a==1 || a==1-m)
      return;
    std::vector< T_unsigned<modint,tdeg_t> >::iterator pt=p.coord.begin(),ptend=p.coord.end();
    for (;pt!=ptend;++pt){
      pt->g=(longlong(pt->g)*a)%m;
    }
  }

  struct tdeg_t_sort_t {
    order_t order;
    tdeg_t_sort_t() {order_t tmp={_REVLEX_ORDER,0}; order=tmp;}
    tdeg_t_sort_t(order_t o):order(o) {}
    bool operator ()(const T_unsigned<modint,tdeg_t> & a,const T_unsigned<modint,tdeg_t> & b) const {return !tdeg_t_greater(b.u,a.u,order);}
    bool operator ()(const T_unsigned<gen,tdeg_t> & a,const T_unsigned<gen,tdeg_t> & b) const {return !tdeg_t_greater(b.u,a.u,order);}
  };
  void convert(const poly8 & p,polymod &q,modint env){
    q.coord.resize(p.coord.size());
    q.dim=p.dim;
    q.order=p.order;
    q.sugar=0;
    for (unsigned i=0;i<p.coord.size();++i){
      if (!env)
	q.coord[i].g=1;
      else {
	if (p.coord[i].g.type==_ZINT)
	  q.coord[i].g=modulo(*p.coord[i].g._ZINTptr,env);
	else
	  q.coord[i].g=(p.coord[i].g.val)%env;
      }
      q.coord[i].u=p.coord[i].u;
    }
    if (env && !q.coord.empty()){
      q.sugar=q.coord.front().u.total_degree(p.order);
      if (q.coord.front().g!=1)
	smallmultmod(invmod(q.coord.front().g,env),q,env);
      q.coord.front().g=1;
    }
    sort(q.coord.begin(),q.coord.end(),tdeg_t_sort_t(p.order));
  }
  void convert(const polymod & p,poly8 &q,modint env){
    q.coord.resize(p.coord.size());
    q.dim=p.dim;
    q.order=p.order;
    for (unsigned i=0;i<p.coord.size();++i){
      modint n=p.coord[i].g % env;
      if (n>env/2)
	n-=env;
      else {
	if (n<=-env/2)
	  n += env;
      }
      q.coord[i].g=n;
      q.coord[i].u=p.coord[i].u;
    }
    if (!q.coord.empty())
      q.sugar=q.coord.front().u.total_degree(p.order);
    else
      q.sugar=0;
  }

  bool operator == (const polymod & p,const polymod &q){
    if (p.coord.size()!=q.coord.size())
      return false;
    for (unsigned i=0;i<p.coord.size();++i){
      if (p.coord[i].u!=q.coord[i].u || p.coord[i].g!=q.coord[i].g)
	return false;
    }
    return true;
  }

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os, const polymod & p)
#else
  ostream & operator << (ostream & os, const polymod & p)
#endif
  {
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
#ifdef GIAC_64VARS
      if (it->u.tdeg%2){
	degtype * i=(degtype *)(it->u.ui+1);
	for (int j=0;j<it->u.order_.dim;++j){
	  t2=i[j];
	  if (t2)
	    os << "*x"<< j << "^" << t2  ;
	}
	++it;
	if (it==itend)
	  break;
	os << " + ";
	continue;
      }
#endif
      short tab[GROEBNER_VARS+1];
      it->u.get_tab(tab);
      switch (p.order.o){
      case _PLEX_ORDER:
	for (int i=0;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2)
	    os << "*x"<< i << "^" << t2  ;
	}
	break;
      case _TDEG_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  if (t2)
	    os << "*x"<< i-1 << "^" << t2  ;
	}
	break;
      case _REVLEX_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;
#if GROEBNER_VARS==15
      case _3VAR_ORDER:
	for (int i=1;i<=3;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 3-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=5;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 4+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _7VAR_ORDER:
	for (int i=1;i<=7;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 7-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=9;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 8+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _11VAR_ORDER:
	for (int i=1;i<=11;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 11-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=13;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 12+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
#endif
      }
      ++it;
      if (it==itend)
	break;
      os << " + ";
    }
    return os;
  }

  void polymod::dbgprint() const { 
    CERR << *this << endl;
  }

  class vectpolymod:public vector<polymod>{
  public:
    void dbgprint() const { CERR << *this << endl; }
  };

  void vectpoly_2_vectpolymod(const vectpoly & v,order_t order,vectpolymod & v8,modint m){
    v8.clear();
    v8.reserve(v.size());
    for (unsigned i=0;i<v.size();++i){
      v8.push_back(polymod(v[i],order,m));
      v8.back().order=order;
    }
  }

  void vectpolymod_2_vectpoly(const vectpoly8 & v8,vectpoly & v){
    v.clear();
    v.reserve(v8.size());
    for (unsigned i=0;i<v8.size();++i){
      v.push_back(polynome(v8[i].dim));
      v8[i].get_polynome(v[i]);
    }
  }

  void convert(const vectpoly8 & v,vectpolymod & w,modint env){
    if (w.size()<v.size())
      w.resize(v.size());
    for (unsigned i=0;i<v.size();++i){
      convert(v[i],w[i],env);
    }
  }

  void convert(const vectpolymod & v,vectpoly8 & w,modint env){
    w.resize(v.size());
    for (unsigned i=0;i<v.size();++i){
      convert(v[i],w[i],env);
    }
  }

  void convert(const vectpolymod & v,const vector<unsigned> & G,vectpoly8 & w,modint env){
    w.resize(v.size());
    for (unsigned i=0;i<G.size();++i){
      convert(v[G[i]],w[G[i]],env);
    }
  }

#ifdef GBASIS_HEAP
  void in_heap_reducemod(const polymod & f,const vectpolymod & g,const vector<unsigned> & G,unsigned excluded,vectpolymod & q,polymod & rem,polymod * R,modint env){
    // divides f by g[G[0]] to g[G[G.size()-1]] except maybe g[G[excluded]]
    // first implementation: use quotxsient heap for all quotient/divisor
    // do not use heap chain
    // ref Monaghan Pearce if g.size()==1
    // R is the list of all monomials
    if (R){
      R->dim=f.dim; R->order=f.order;
      R->coord.clear();
    }
    if (&rem==&f){
      polymod TMP;
      in_heap_reducemod(f,g,G,excluded,q,TMP,R,env);
      swap(rem.coord,TMP.coord);
      if (debug_infolevel>1000)
	g.dbgprint(); // instantiate dbgprint()
      return;
    }
    rem.coord.clear();
    if (f.coord.empty())
      return ;
    if (q.size()<G.size())
      q.resize(G.size());
    unsigned guess=0;
    for (unsigned i=0;i<G.size();++i){
      q[i].dim=f.dim;
      q[i].order=f.order;
      q[i].coord.clear();
      guess += unsigned(g[G[i]].coord.size());
    }
    vector<heap_t> H;
    compare_heap_t key(f.order);
    H.reserve(guess);
    vector<modint> invlcg(G.size());
    for (unsigned i=0;i<G.size();++i){
      invlcg[i]=invmod(g[G[i]].coord.front().g,env);
    }
    modint c=1;
#ifdef __x86_64__
    int128_t C=0; // int128_t to avoid %
#else
    modint2 C=0; // int128_t to avoid %
#endif
    unsigned k=0,i; // k=position in f
    tdeg_t m;
    bool finish=false;
    while (!H.empty() || k<f.coord.size()){
      // is highest remaining degree in f or heap?
      if (k<f.coord.size() && (H.empty() || tdeg_t_greater(f.coord[k].u,H.front().u,f.order)) ){
	// it's in f or both
	m=f.coord[k].u;
	C=f.coord[k].g;
	++k;
      }
      else {
	m=H[0].u;
	C=0;
      }
      if (R)
	R->coord.push_back(T_unsigned<modint,tdeg_t>(1,m));
      // extract from heap all terms having m as monomials, substract from c
      while (!H.empty() && H.front().u==m){
	std::pop_heap(H.begin(),H.end(),key);
	heap_t & current=H.back(); // was root node of the heap
	const polymod & gcurrent = g[G[current.i]];
	if (!R){
#ifdef __x86_64__
	  C -= modint2(q[current.i].coord[current.qi].g) * gcurrent.coord[current.gj].g;
#else
	  C = (C-modint2(q[current.i].coord[current.qi].g) * gcurrent.coord[current.gj].g) % env;
#endif
	}
	if (current.gj<gcurrent.coord.size()-1){
	  ++current.gj;
	  current.u=q[current.i].coord[current.qi].u+gcurrent.coord[current.gj].u;
	  push_heap(H.begin(),H.end(),key);
	}
	else
	  H.pop_back();
      }
      if (!R){
#ifdef __x86_64__
	c = C % env;
#else
	c=modint(C);
#endif
	if (c==0)
	  continue;
      }
      // divide (c,m) by one of the g if possible, otherwise push in remainder
      if (finish){
	rem.coord.push_back(T_unsigned<modint,tdeg_t>(c,m)); // add c*m to remainder
	continue;
      }
      finish=true;
#if 0
      for (i=G.size()-1;i!=-1;--i){
	if (i==excluded || g[G[i]].coord.empty())
	  continue;
	if (tdeg_t_greater(m,g[G[i]].coord.front().u,f.order)){
	  finish=false;
	  if (tdeg_t_all_greater(m,g[G[i]].coord.front().u,f.order))
	    break;
	}
      }
      if (i==-1){
	rem.coord.push_back(T_unsigned<modint,tdeg_t>(c,m)); // add c*m to remainder
	continue;
      }
#else
      for (i=0;i<G.size();++i){
	if (i==excluded || g[G[i]].coord.empty())
	  continue;
	if (tdeg_t_greater(m,g[G[i]].coord.front().u,f.order)){
	  finish=false;
	  if (tdeg_t_all_greater(m,g[G[i]].coord.front().u,f.order))
	    break;
	}
      }
      if (i==G.size()){
	rem.coord.push_back(T_unsigned<modint,tdeg_t>(c,m)); // add c*m to remainder
	continue;
      }
#endif
      // add c*m/leading monomial of g[G[i]] to q[i]
      tdeg_t monom=m-g[G[i]].coord.front().u;
      if (!R){
	if (invlcg[i]!=1){
	  if (invlcg[i]==-1)
	    c=-c;
	  else
	    c=(modint2(c)*invlcg[i]) % env;
	}
      }
      q[i].coord.push_back(T_unsigned<modint,tdeg_t>(c,monom));
      // push in heap
      if (g[G[i]].coord.size()>1){
	heap_t current={i,int(q[i].coord.size())-1,1,g[G[i]].coord[1].u+monom};
	H.push_back(current);
	push_heap(H.begin(),H.end(),key);
      }
    } // end main heap pseudo-division loop
  }

  void heap_reducemod(const polymod & f,const vectpolymod & g,const vector<unsigned> & G,unsigned excluded,vectpolymod & q,polymod & rem,modint env){
    in_heap_reducemod(f,g,G,excluded,q,rem,0,env);
    // end up by multiplying rem by s (so that everything is integer)
    if (debug_infolevel>2){
      for (unsigned i=0;i<G.size();++i)
	CERR << "(" << g[G[i]]<< ")*(" << q[i] << ")+ ";
      CERR << rem << endl;
    }
    if (!rem.coord.empty() && rem.coord.front().g!=1){
      smallmult(invmod(rem.coord.front().g,env),rem.coord,rem.coord,env);
      rem.coord.front().g=1;
    }
  }

#endif

  void symbolic_preprocess(const polymod & f,const vectpolymod & g,const vector<unsigned> & G,unsigned excluded,vectpolymod & q,polymod & rem,polymod * R){
    // divides f by g[G[0]] to g[G[G.size()-1]] except maybe g[G[excluded]]
    // first implementation: use quotient heap for all quotient/divisor
    // do not use heap chain
    // ref Monaghan Pearce if g.size()==1
    // R is the list of all monomials
    if (R){
      R->dim=f.dim; R->order=f.order;
      R->coord.clear();
    }
    rem.coord.clear();
    if (f.coord.empty())
      return ;
    if (q.size()<G.size())
      q.resize(G.size());
    unsigned guess=0;
    for (unsigned i=0;i<G.size();++i){
      q[i].dim=f.dim;
      q[i].order=f.order;
      q[i].coord.clear();
      guess += unsigned(g[G[i]].coord.size());
    }
    vector<heap_t> H_;
    vector<unsigned> H;
    H_.reserve(guess);
    H.reserve(guess);
    heap_t_compare keyheap(H_,f.order);
    unsigned k=0,i; // k=position in f
    tdeg_t m;
    bool finish=false;
    while (!H.empty() || k<f.coord.size()){
      // is highest remaining degree in f or heap?
      if (k<f.coord.size() && (H.empty() || tdeg_t_greater(f.coord[k].u,H_[H.front()].u,f.order)) ){
	// it's in f or both
	m=f.coord[k].u;
	++k;
      }
      else {
	m=H_[H.front()].u;
      }
      if (R)
	R->coord.push_back(T_unsigned<modint,tdeg_t>(1,m));
      // extract from heap all terms having m as monomials, substract from c
      while (!H.empty() && H_[H.front()].u==m){
	std::pop_heap(H.begin(),H.end(),keyheap);
	heap_t & current=H_[H.back()]; // was root node of the heap
	const polymod & gcurrent = g[G[current.i]];
	if (current.gj<gcurrent.coord.size()-1){
	  ++current.gj;
	  current.u=q[current.i].coord[current.qi].u+gcurrent.coord[current.gj].u;
	  std::push_heap(H.begin(),H.end(),keyheap);
	}
	else
	  H.pop_back();
      }
      // divide (c,m) by one of the g if possible, otherwise push in remainder
      if (finish){
	rem.coord.push_back(T_unsigned<modint,tdeg_t>(1,m)); // add to remainder
	continue;
      }
      finish=true;
      for (i=0;i<G.size();++i){
	const vector< T_unsigned<modint,tdeg_t> > & gGicoord=g[G[i]].coord;
	if (i==excluded || gGicoord.empty())
	  continue;
	if (tdeg_t_greater(m,gGicoord.front().u,f.order)){
	  finish=false;
	  if (tdeg_t_all_greater(m,gGicoord.front().u,f.order))
	    break;
	}
      }
      if (i==G.size()){
	rem.coord.push_back(T_unsigned<modint,tdeg_t>(1,m)); // add to remainder
	continue;
      }
      // add m/leading monomial of g[G[i]] to q[i]
      tdeg_t monom=m-g[G[i]].coord.front().u;
      q[i].coord.push_back(T_unsigned<modint,tdeg_t>(1,monom));
      // push in heap
      if (g[G[i]].coord.size()>1){
	heap_t current={i,int(q[i].coord.size())-1,1,g[G[i]].coord[1].u+monom};
	H.push_back(unsigned(H_.size()));
	H_.push_back(current);
	keyheap.ptr=&H_.front();
	std::push_heap(H.begin(),H.end(),keyheap);
      }
    } // end main heap pseudo-division loop
    // CERR << H_.size() << endl;
  }

  // p - a*q shifted mod m -> r
  void smallmultsubmod(const polymod & p,unsigned pos,modint a,const polymod & q,const tdeg_t & shift,polymod & r,modint m){
    r.coord.clear();
    r.coord.reserve(p.coord.size()+q.coord.size());
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin()+pos,itend=p.coord.end(),jt=q.coord.begin(),jtend=q.coord.end();
    tdeg_t v;
    int dim=p.dim;
    for (;jt!=jtend;++jt){
      add(jt->u,shift,v,dim);
      for (;it!=itend && tdeg_t_strictly_greater(it->u,v,p.order);++it){
	r.coord.push_back(*it);
      }
      if (it!=itend && it->u==v){
	modint tmp=(it->g-modint2(a)*jt->g)%m;
	if (tmp)
	  r.coord.push_back(T_unsigned<modint,tdeg_t>(tmp,v));
	++it;
      }
      else {
	modint tmp=(-modint2(a)*jt->g)%m;
	r.coord.push_back(T_unsigned<modint,tdeg_t>(tmp,v));
      }
    }
    for (;it!=itend;++it){
      r.coord.push_back(*it);
    }
  }

  // p - a*q mod m -> r
  void smallmultsubmod(const polymod & p,modint a,const polymod & q,polymod & r,modint m){
    r.coord.clear();
    r.coord.reserve(p.coord.size()+q.coord.size());
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=q.coord.begin(),jtend=q.coord.end();
    for (;jt!=jtend;++jt){
      const tdeg_t & v=jt->u;
      for (;it!=itend && tdeg_t_strictly_greater(it->u,v,p.order);++it){
	r.coord.push_back(*it);
      }
      if (it!=itend && it->u==v){
	modint tmp=(it->g-modint2(a)*jt->g)%m;
	if (tmp)
	  r.coord.push_back(T_unsigned<modint,tdeg_t>(tmp,v));
	++it;
      }
      else {
	int tmp=(-modint2(a)*jt->g)%m;
	r.coord.push_back(T_unsigned<modint,tdeg_t>(tmp,v));
      }
    }
    for (;it!=itend;++it){
      r.coord.push_back(*it);
    }
  }

  // p + q  -> r
  void smallmerge(polymod & p,polymod & q,polymod & r){
    if (p.coord.empty()){
      swap(q.coord,r.coord);
      return;
    }
    if (q.coord.empty()){
      swap(p.coord,r.coord);
      return;
    }
    r.coord.clear();
    r.coord.reserve(p.coord.size()+q.coord.size());
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=q.coord.begin(),jtend=q.coord.end();
    for (;jt!=jtend;++jt){
      const tdeg_t & v=jt->u;
      for (;it!=itend && tdeg_t_strictly_greater(it->u,v,p.order);++it){
	r.coord.push_back(*it);
      }
      r.coord.push_back(*jt);
    }
    for (;it!=itend;++it){
      r.coord.push_back(*it);
    }
  }

  void reducemod(const polymod & p,const vectpolymod & res,const vector<unsigned> & G,unsigned excluded,polymod & rem,modint env){
    if (&p!=&rem)
      rem=p;
    if (p.coord.empty())
      return ;
    polymod TMP2(p.order,p.dim);
    unsigned i,rempos=0;
    for (unsigned count=0;;++count){
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator pt=rem.coord.begin()+rempos;
      if (pt>=rem.coord.end())
	break;
      for (i=0;i<G.size();++i){
	if (i==excluded || res[G[i]].coord.empty())
	  continue;
	if (tdeg_t_all_greater(pt->u,res[G[i]].coord.front().u,p.order))
	  break;
      }
      if (i==G.size()){ // no leading coeff of G is smaller than the current coeff of rem
	++rempos;
	// if (small0) TMP1.coord.push_back(*pt);
	continue;
      }
      modint a(pt->g),b(res[G[i]].coord.front().g);
      if (pt->u==res[G[i]].coord.front().u){
	smallmultsubmod(rem,smod(modint2(a)*invmod(b,env),env),res[G[i]],TMP2,env);
	// Gpos=i; // assumes basis element in G are sorted wrt >
      }
      else
	smallmultsubmod(rem,0,smod(modint2(a)*invmod(b,env),env),res[G[i]],pt->u-res[G[i]].coord.front().u,TMP2,env);
      swap(rem.coord,TMP2.coord);
    }
    if (!rem.coord.empty() && rem.coord.front().g!=1){
      smallmultmod(invmod(rem.coord.front().g,env),rem,env);
      rem.coord.front().g=1;
    }
  }
 
#if 0
  // reduce with respect to itself the elements of res with index in G
  void reducemod(vectpolymod & res,vector<unsigned> G,modint env){
    if (res.empty() || G.empty())
      return;
    polymod pred(res.front().order,res.front().dim),
      TMP2(res.front().order,res.front().dim);
    vectpolymod q;
    // reduce res
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      polymod & p=res[i];
      reducemod(p,res,G,i,q,pred,TMP2,env);
      swap(res[i].coord,pred.coord);
      pred.sugar=res[i].sugar;
    }
  }
#endif

  void spolymod(const polymod & p,const polymod & q,polymod & res,polymod & TMP1,modint env){
    if (p.coord.empty()){
      res=q;
      return ;
    }
    if (q.coord.empty()){
      res= p;
      return;
    }
    const tdeg_t & pi = p.coord.front().u;
    const tdeg_t & qi = q.coord.front().u;
    tdeg_t lcm;
    index_lcm(pi,qi,lcm,p.order);
    //polymod TMP1(p);
    TMP1=p;
    // polymod TMP2(q);
    const polymod &TMP2=q;
    modint a=p.coord.front().g,b=q.coord.front().g;
    tdeg_t pshift=lcm-pi;
    unsigned sugarshift=pshift.total_degree(p.order);
    // adjust sugar for res
    res.sugar=p.sugar+sugarshift;
    // CERR << "spoly mod " << res.sugar << " " << pi << qi << endl;
    if (p.order.o==_PLEX_ORDER || sugarshift!=0)
      smallshift(TMP1.coord,pshift,TMP1.coord);
    // smallmultmod(b,TMP1,env);
    if (lcm==qi)
      smallmultsubmod(TMP1,smod(modint2(a)*invmod(b,env),env),TMP2,res,env);
    else
      smallmultsubmod(TMP1,0,smod(modint2(a)*invmod(b,env),env),TMP2,lcm-qi,res,env);
    if (!res.coord.empty() && res.coord.front().g!=1){
      smallmultmod(invmod(res.coord.front().g,env),res,env);
      res.coord.front().g=1;
    }
    if (debug_infolevel>2)
      CERR << "spolymod " << res << endl;
  }

  void reduce1smallmod(polymod & p,const polymod & q,polymod & TMP2,modint env){
    if (p.coord.empty())
      return ;
    unsigned rempos=0;
    const tdeg_t & u = q.coord.front().u;
    const modint invg=invmod(q.coord.front().g,env);
    for (unsigned count=0;;++count){
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator pt=p.coord.begin()+rempos;
      if (pt>=p.coord.end())
	break;
      if (pt->u==u){
	smallmultsubmod(p,0,smod(modint2(pt->g)*invg,env),q,pt->u-u,TMP2,env);
	swap(p.coord,TMP2.coord);
	break;
      }
      if (!tdeg_t_all_greater(pt->u,u,p.order)){
	++rempos;
	// TMP1.coord.push_back(*pt);
	continue;
      }
      smallmultsubmod(p,0,smod(modint2(pt->g)*invg,env),q,pt->u-u,TMP2,env);
      // smallmultsubmod(p,rempos,smod(modint2(pt->g)*invmod(g,env),env),q,pt->u-u,TMP2,env);
      rempos=0; 
      swap(p.coord,TMP2.coord);
    }
    // if (small0) swap(p.coord,TMP1.coord);
    if (!p.coord.empty() && p.coord.front().g!=1){
      smallmultmod(invmod(p.coord.front().g,env),p,env);
      p.coord.front().g=1;
    }
  }

  void reducesmallmod(polymod & rem,const vectpolymod & res,const vector<unsigned> & G,unsigned excluded,modint env,polymod & TMP1,bool normalize){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator pt,ptend;
    unsigned i,rempos=0;
    TMP1.coord.clear();
    for (unsigned count=0;;++count){
      ptend=rem.coord.end();
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      pt=rem.coord.begin()+rempos;
      if (pt>=ptend)
	break;
      for (i=0;i<G.size();++i){
	if (i==excluded || res[G[i]].coord.empty())
	  continue;
	if (tdeg_t_all_greater(pt->u,res[G[i]].coord.front().u,rem.order))
	  break;
      }
      if (i==G.size()){ // no leading coeff of G is smaller than the current coeff of rem
	++rempos;
	// if (small0) TMP1.coord.push_back(*pt);
	continue;
      }
      modint a(pt->g),b(res[G[i]].coord.front().g);
      smallmultsubmod(rem,0,smod(a*modint2(invmod(b,env)),env),res[G[i]],pt->u-res[G[i]].coord.front().u,TMP1,env);
      // smallmultsub(rem,rempos,smod(a*invmod(b,env->modulo),env->modulo).val,res[G[i]],pt->u-res[G[i]].coord.front().u,TMP2,env->modulo.val);
      // rempos=0; // since we have removed the beginning of rem (copied in TMP1)
      swap(rem.coord,TMP1.coord);
      continue;
    }
    if (normalize && !rem.coord.empty() && rem.coord.front().g!=1){
      smallmult(invmod(rem.coord.front().g,env),rem.coord,rem.coord,env);
      rem.coord.front().g=1;
    }
  }

  static void reducemod(vectpolymod &resmod,modint env){
    if (resmod.empty())
      return;
    // Initial interreduce step
    polymod TMP1(resmod.front().order,resmod.front().dim);
    vector<unsigned> G(resmod.size());
    for (unsigned j=0;j<G.size();++j)
      G[j]=j;
    for (unsigned j=0; j<resmod.size();++j){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      reducesmallmod(resmod[j],resmod,G,j,env,TMP1,true);
    }
  }

  static void gbasis_updatemod(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpolymod & res,unsigned pos,polymod & TMP2,modint env,bool reduce,const vector<unsigned> & oldG){
    if (debug_infolevel>2)
      CERR << CLOCK() << " mod begin gbasis update " << G.size() << endl;
    if (debug_infolevel>3)
      CERR << G << endl;
    const polymod & h = res[pos];
    if (h.coord.empty())
      return;
    order_t order=h.order;
    vector<unsigned> C;
    C.reserve(G.size()+1);
    const tdeg_t & h0=h.coord.front().u;
    // FIXME: should use oldG instead of G here
    for (unsigned i=0;i<oldG.size();++i){
      if (tdeg_t_all_greater(h0,res[oldG[i]].coord.front().u,order))
	return;
    }
    tdeg_t tmp1,tmp2;
    // C is used to construct new pairs
    // create pairs with h and elements g of G, then remove
    // -> if g leading monomial is prime with h, remove the pair
    // -> if g leading monomial is not disjoint from h leading monomial
    //    keep it only if lcm of leading monomial is not divisible by another one
#if 1
    size_t tmpsize=G.size();
    vector<tdeg_t> tmp(tmpsize);
    for (unsigned i=0;i<tmpsize;++i){
      if (res[G[i]].coord.empty()){
	tmp[i].tab[0]=-2;
      }
      else
	index_lcm(h0,res[G[i]].coord.front().u,tmp[i],order); 
    }
#else
    // this would be faster but it does not work for 
    // gbasis([25*y^2*x^6-10*y^2*x^5+59*y^2*x^4-20*y^2*x^3+43*y^2*x^2-10*y^2*x+9*y^2-80*y*x^6+136*y*x^5+56*y*x^4-240*y*x^3+104*y*x^2+64*x^6-192*x^5+192*x^4-64*x^3,25*y^2*6*x^5-10*y^2*5*x^4+59*y^2*4*x^3-20*y^2*3*x^2+43*y^2*2*x-10*y^2-80*y*6*x^5+136*y*5*x^4+56*y*4*x^3-240*y*3*x^2+104*y*2*x+64*6*x^5-192*5*x^4+192*4*x^3-64*3*x^2,25*2*y*x^6-10*2*y*x^5+59*2*y*x^4-20*2*y*x^3+43*2*y*x^2-10*2*y*x+9*2*y-80*x^6+136*x^5+56*x^4-240*x^3+104*x^2],[x,y],revlex);
    // pair <4,3> is not generated
    unsigned tmpsize=G.empty()?0:G.back()+1;
    vector<tdeg_t> tmp(tmpsize);
    for (unsigned i=0;i<tmpsize;++i){
      if (res[i].coord.empty()){
	tmp[i].tab[0]=-2;
      }
      else
	index_lcm(h0,res[i].coord.front().u,tmp[i],order); 
    }
#endif
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[G[i]].coord.empty() || disjoint(h0,res[G[i]].coord.front().u,res.front().order,res.front().dim))
	continue;
      // h0 and G[i] leading monomial not prime together
#if 1
      tdeg_t * tmp1=&tmp[i]; 
#else
      tdeg_t * tmp1=&tmp[G[i]];
#endif
      tdeg_t * tmp2=&tmp[0],*tmpend=tmp2+tmpsize;
      for (;tmp2!=tmp1;++tmp2){
	if (tmp2->tab[0]<0)
	  continue;
	if (tdeg_t_all_greater(*tmp1,*tmp2,order))
	  break; // found another pair, keep the smallest, or the first if equal
      }
      if (tmp2!=tmp1)
	continue;
      for (++tmp2;tmp2<tmpend;++tmp2){
	if (tmp2->tab[0]<0)
	  continue;
	if (tdeg_t_all_greater(*tmp1,*tmp2,order) && *tmp1!=*tmp2){
	  break; 
	}
      }
      if (tmp2==tmpend)
	C.push_back(G[i]);
    }
    vector< pair<unsigned,unsigned> > B1;
    B1.reserve(B.size()+C.size());
    for (unsigned i=0;i<B.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	continue;
      index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,tmp1,order);
      if (!tdeg_t_all_greater(tmp1,h0,order)){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].first].coord.front().u,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].second].coord.front().u,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
    }
    // B <- B union pairs(h,g) with g in C
    for (unsigned i=0;i<C.size();++i){
      B1.push_back(pair<unsigned,unsigned>(pos,C[i]));
    }
    swap(B1,B);
    // Update G by removing elements with leading monomial >= leading monomial of h
    if (debug_infolevel>2){
      CERR << CLOCK() << " end, pairs:"<< endl;
      if (debug_infolevel>3)
	CERR << B << endl;
      CERR << "mod begin Groebner interreduce " << endl;
    }
    C.clear();
    C.reserve(G.size()+1);
    // bool pos_pushed=false;
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (!res[G[i]].coord.empty() && !tdeg_t_all_greater(res[G[i]].coord.front().u,h0,order)){
	if (reduce){
	  // reduce res[G[i]] with respect to h
	  reduce1smallmod(res[G[i]],h,TMP2,env);
	}
	C.push_back(G[i]);
      }
      // NB: removing all pairs containing i in it does not work
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " mod end Groebner interreduce " << endl;
    C.push_back(pos);
    swap(C,G);
#if 0
    // clear in res polymod that are no more referenced
    vector<bool> used(res.size(),false);
    for (unsigned i=0;i<G.size();++i){
      used[G[i]]=true;
    }
    for (unsigned i=0;i<B.size();++i){
      used[B[i].first]=true;
      used[B[i].first]=false;
    }
    for (unsigned i=0;i<res.size();++i){
      if (!used[i] && !res[i].coord.capacity()){
	polymod clearer;
	swap(res[i].coord,clearer.coord);
      }
    }
#endif
  }

#if 0
  // update G, G is a list of index of the previous gbasis + new spolys
  // new spolys index are starting at debut
  static void gbasis_multiupdatemod(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpolymod & res,unsigned debut,polymod & TMP2,modint env){
    if (debug_infolevel>2)
      CERR << CLOCK() << " mod begin gbasis update " << G.size() << "+" << add.size() << endl;
    if (debug_infolevel>3)
      CERR << G << endl;
    vector<unsigned> C;
    // C is used to construct new pairs
    tdeg_t tmp1,tmp2;
    order_t order;
    for (unsigned pos=debut;pos<G.size();++pos){
      const polymod & h = res[pos];
      const tdeg_t & h0=h.coord.front().u;
      // create pairs with h and elements g of G, then remove
      // -> if g leading monomial is prime with h, remove the pair
      // -> if g leading monomial is not disjoint from h leading monomial
      //    keep it only if lcm of leading monomial is not divisible by another one
      for (unsigned i=0;i<pos;++i){
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return;
	if (res[G[i]].coord.empty() || disjoint(h0,res[G[i]].coord.front().u,res.front().order,res.front().dim))
	  continue;
	index_lcm(h0,res[G[i]].coord.front().u,tmp1,order); // h0 and G[i] leading monomial not prime together
	unsigned j;
	for (j=0;j<G.size();++j){
	  if (i==j || res[G[j]].coord.empty())
	    continue;
	  index_lcm(h0,res[G[j]].coord.front().u,tmp2,order);
	  if (tdeg_t_all_greater(tmp1,tmp2,order)){
	    // found another pair, keep the smallest, or the first if equal
	    if (tmp1!=tmp2)
	      break; 
	    if (i>j)
	      break;
	  }
	} // end for j
	if (j==G.size())
	  C.push_back(G[i]);
      }
    }
    vector< pair<unsigned,unsigned> > B1;
    B1.reserve(B.size()+C.size());
    for (unsigned i=0;i<B.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	continue;
      index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,tmp1,order);
      for (unsigned pos=debut;pos<G.size();++pos){
	const tdeg_t & h0=res[G[pos]].front().u;
	if (!tdeg_t_all_greater(tmp1,h0,order)){
	  B1.push_back(B[i]);
	  break;
	}
	index_lcm(res[B[i].first].coord.front().u,h0,tmp2,order);
	if (tmp2==tmp1){
	  B1.push_back(B[i]);
	  break;
	}
	index_lcm(res[B[i].second].coord.front().u,h0,tmp2,order);
	if (tmp2==tmp1){
	  B1.push_back(B[i]);
	  break;
	}
      }
    }
    // B <- B union B2
    for (unsigned i=0;i<B2.size();++i)
      B1.push_back(B2[i]);
    swap(B1,B);
    // Update G by removing elements with leading monomial >= leading monomial of h
    if (debug_infolevel>2){
      CERR << CLOCK() << " end, pairs:"<< endl;
      if (debug_infolevel>3)
	CERR << B << endl;
      CERR << "mod begin Groebner interreduce " << endl;
    }
    vector<unsigned> C;
    C.reserve(G.size());
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[G[i]].coord.empty())
	continue;
      const tdeg_t & Gi=res[G[i]].coord.front().u;
      unsigned j;
      for (j=i+1;j<G.size();++j){
	if (tdeg_t_all_greater(Gi,res[G[j]].coord.front().u,order))
	  break;
      }
      if (i==G.size()-1 || j==G.size())
	C.push_back(G[i]);
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " mod end Groebner interreduce " << endl;
    swap(C,G);
  }
#endif

  bool in_gbasismod(vectpoly8 & res8,vectpolymod &res,vector<unsigned> & G,modint env,bool sugar,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero){
    convert(res8,res,env);
    unsigned ressize=unsigned(res8.size());
    unsigned learned_position=0;
    bool learning=pairs_reducing_to_zero && pairs_reducing_to_zero->empty();
    if (debug_infolevel>1000)
      res.dbgprint(); // instantiate dbgprint()
    polymod TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vector< pair<unsigned,unsigned> > B;
    order_t order=res.front().order;
    if (order.o==_PLEX_ORDER)
      sugar=false;
    vector<unsigned> oldG(G);
    for (unsigned l=0;l<ressize;++l){
#ifdef GIAC_REDUCEMODULO
      reducesmallmod(res[l],res,G,-1,env,TMP2,env);
#endif      
      gbasis_updatemod(G,B,res,l,TMP2,env,true,oldG);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      oldG=G;
      if (debug_infolevel>1)
	CERR << CLOCK() << " mod number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small0,cur;
      unsigned smallpos,smallsugar=0,cursugar=0;
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small0,order);
      if (sugar)
	smallsugar=res[B[smallpos].first].sugar+(small0-res[B[smallpos].first].coord.front().u).total_degree(order);
      for (unsigned i=smallpos+1;i<B.size();++i){
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	bool doswap=false;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	if (sugar)
	  cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree(order);
	if (order.o==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small0,cur,order);
	else {
	  if (cursugar!=smallsugar)
	    doswap = smallsugar > cursugar;
	  else
	    doswap=tdeg_t_strictly_greater(small0,cur,order);
	}
	if (doswap){
	  // CERR << "swap mod " << cursugar << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small0,cur); // small0=cur;
	  swap(smallsugar,cursugar);
	  smallpos=i;
	}
      }
      pair<unsigned,unsigned> bk=B[smallpos];
      B.erase(B.begin()+smallpos);
      if (pairs_reducing_to_zero && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	++learned_position;
	continue;
      }
      if (debug_infolevel>1 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	CERR << CLOCK() << " mod reducing pair with 1 element not in basis " << bk << endl;
      // polymod h(res.front().order,res.front().dim);
      spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
      if (debug_infolevel>1){
	CERR << CLOCK() << " mod reduce begin, pair " << bk << " spoly size " << TMP1.coord.size() << " sugar deg " << TMP1.sugar << " degree " << TMP1.coord.front().u << endl;
      }
      reducemod(TMP1,res,G,-1,TMP1,env);
      if (debug_infolevel>1){
	if (debug_infolevel>2){ CERR << TMP1 << endl; }
	CERR << CLOCK() << " mod reduce end, remainder size " << TMP1.coord.size() << endl;
      }
      if (!TMP1.coord.empty()){
	if (ressize==res.size())
	  res.push_back(polymod(TMP1.order,TMP1.dim));
	swap(res[ressize],TMP1);
	++ressize;
	gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true,oldG);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " mod basis indexes " << G << " pairs indexes " << B << endl;
      }
      else {
	if (learning && pairs_reducing_to_zero)
	  pairs_reducing_to_zero->push_back(bk);
      }
    }
    if (ressize<res.size())
      res.resize(ressize);
    // sort(res.begin(),res.end(),tripolymod);
    convert(res,G,res8,env);
    return true;
  }

  // F4BUCHBERGER algorithm
  struct heap_tt {
    bool left;
    unsigned f4buchbergervpos:31;
    unsigned polymodpos;
    tdeg_t u;
    heap_tt(bool l,unsigned a,unsigned b,tdeg_t t):left(l),f4buchbergervpos(a),polymodpos(b),u(t){};
    heap_tt(unsigned a,unsigned b,tdeg_t t):left(true),f4buchbergervpos(a),polymodpos(b),u(t){};
    heap_tt():left(true),f4buchbergervpos(0),polymodpos(0),u(){};
  };

  struct heap_tt_compare {
    order_t order;
    const heap_tt * ptr;
    inline bool operator () (unsigned a,unsigned b){
      return !tdeg_t_greater((ptr+a)->u,(ptr+b)->u,order);
      // return (ptr+a)->u<(ptr+b)->u;
    }
    heap_tt_compare(const vector<heap_tt> & v,order_t o):order(o),ptr(v.empty()?0:&v.front()){};
  };


  struct compare_heap_tt {
    order_t order;
    inline bool operator () (const heap_tt & a,const heap_tt & b){
      return !tdeg_t_greater(a.u,b.u,order);
      // return (ptr+a)->u<(ptr+b)->u;
    }
    compare_heap_tt(order_t o):order(o) {}
  };


  // inline bool operator > (const heap_tt & a,const heap_tt & b){ return a.u>b.u; }

  // inline bool operator < (const heap_tt & a,const heap_tt & b){ return b>a;}

  struct heap_tt_ptr {
    heap_tt * ptr;
    heap_tt_ptr(heap_tt * ptr_):ptr(ptr_){};
    heap_tt_ptr():ptr(0){};
  };


  // inline bool operator > (const heap_tt_ptr & a,const heap_tt_ptr & b){ return a.ptr->u > b.ptr->u; }

  // inline bool operator < (const heap_tt_ptr & a,const heap_tt_ptr & b){ return b>a; }
  struct compare_heap_tt_ptr {
    order_t order;
    inline bool operator () (const heap_tt_ptr & a,const heap_tt_ptr & b){
      return !tdeg_t_greater(a.ptr->u,b.ptr->u,order);
      // return (ptr+a)->u<(ptr+b)->u;
    }
    compare_heap_tt_ptr(order_t o):order(o) {}
  };


  void collect(const vectpolymod & f4buchbergerv,polymod & allf4buchberger,int start=0){
    vectpolymod::const_iterator it=f4buchbergerv.begin(),itend=f4buchbergerv.end();
    vector<heap_tt> Ht;
    vector<heap_tt_ptr> H; 
    Ht.reserve(itend-it);
    H.reserve(itend-it);
    unsigned s=0;
    order_t keyorder={_REVLEX_ORDER,0};
    for (unsigned i=0;it!=itend;++i,++it){
      keyorder=it->order;
      if (int(it->coord.size())>start){
	s=giacmax(s,unsigned(it->coord.size()));
	Ht.push_back(heap_tt(i,start,it->coord[start].u));
	H.push_back(heap_tt_ptr(&Ht.back()));
      }
    }
    allf4buchberger.coord.reserve(s); // int(s*std::log(1+H.size())));
    compare_heap_tt_ptr key(keyorder);
    make_heap(H.begin(),H.end(),key);
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end(),key);
      // push root node of the heap in allf4buchberger
      heap_tt & current = *H.back().ptr;
      if (allf4buchberger.coord.empty() || allf4buchberger.coord.back().u!=current.u)
	allf4buchberger.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      if (current.polymodpos>=f4buchbergerv[current.f4buchbergervpos].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4buchbergerv[current.f4buchbergervpos].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end(),key);
    }
  }

  void collect(const vectpolymod & f4buchbergerv,const vector<unsigned> & G,polymod & allf4buchberger,unsigned start=0){
    unsigned Gsize=unsigned(G.size());
    if (!Gsize) return;
    vector<heap_tt> H;
    compare_heap_tt key(f4buchbergerv[G[0]].order);
    H.reserve(Gsize);
    for (unsigned i=0;i<Gsize;++i){
      if (f4buchbergerv[G[i]].coord.size()>start)
	H.push_back(heap_tt(i,start,f4buchbergerv[G[i]].coord[start].u));
    }
    make_heap(H.begin(),H.end(),key);
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end(),key);
      // push root node of the heap in allf4buchberger
      heap_tt & current =H.back();
      if (allf4buchberger.coord.empty() || allf4buchberger.coord.back().u!=current.u)
	allf4buchberger.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      if (current.polymodpos>=f4buchbergerv[G[current.f4buchbergervpos]].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4buchbergerv[G[current.f4buchbergervpos]].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end(),key);
    }
  }

  void leftright(const vectpolymod & res,vector< pair<unsigned,unsigned> > & B,vector<tdeg_t> & leftshift,vector<tdeg_t> & rightshift){
    for (unsigned i=0;i<B.size();++i){
      const polymod & p=res[B[i].first];
      const polymod & q=res[B[i].second];
      if (debug_infolevel>2)
	CERR << "leftright " << p << "," << q << endl;
      tdeg_t l(p.coord.front().u);
      index_lcm(p.coord.front().u,q.coord.front().u,l,p.order);
      leftshift[i]=l-p.coord.front().u;
      rightshift[i]=l-q.coord.front().u;
    }
  }

  // collect monomials from pairs of res (vector of polymods), shifted by lcm
  // does not collect leading monomial (since they cancel)
  void collect(const vectpolymod & res,vector< pair<unsigned,unsigned> > & B,polymod & allf4buchberger,vector<tdeg_t> & leftshift,vector<tdeg_t> & rightshift){
    int start=1;
    vector<heap_tt> Ht;
    vector<heap_tt_ptr> H; 
    Ht.reserve(2*B.size());
    H.reserve(2*B.size());
    unsigned s=0;
    order_t keyorder={_REVLEX_ORDER,0};
    for (unsigned i=0;i<B.size();++i){
      const polymod & p=res[B[i].first];
      const polymod & q=res[B[i].second];
      keyorder=p.order;
      if (int(p.coord.size())>start){
	s=giacmax(s,unsigned(p.coord.size()));
	Ht.push_back(heap_tt(true,i,start,p.coord[start].u+leftshift[i]));
	H.push_back(heap_tt_ptr(&Ht.back()));
      }
      if (int(q.coord.size())>start){
	s=giacmax(s,unsigned(q.coord.size()));
	Ht.push_back(heap_tt(false,i,start,q.coord[start].u+rightshift[i]));
	H.push_back(heap_tt_ptr(&Ht.back()));
      }
    }
    allf4buchberger.coord.reserve(s); // int(s*std::log(1+H.size())));
    compare_heap_tt_ptr key(keyorder);
    make_heap(H.begin(),H.end(),key);
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end(),key);
      // push root node of the heap in allf4buchberger
      heap_tt & current = *H.back().ptr;
      if (allf4buchberger.coord.empty() || allf4buchberger.coord.back().u!=current.u)
	allf4buchberger.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      unsigned vpos;
      if (current.left)
	vpos=B[current.f4buchbergervpos].first;
      else
	vpos=B[current.f4buchbergervpos].second;
      if (current.polymodpos>=res[vpos].coord.size()){
	H.pop_back();
	continue;
      }
      if (current.left)
	current.u=res[vpos].coord[current.polymodpos].u+leftshift[current.f4buchbergervpos];
      else 
	current.u=res[vpos].coord[current.polymodpos].u+rightshift[current.f4buchbergervpos];
      std::push_heap(H.begin(),H.end(),key);
    }
  }

  struct sparse_element {
    modint val;
    unsigned pos;
    sparse_element(modint v,size_t u):val(v),pos(unsigned(u)){};
    sparse_element():val(0),pos(-1){};
  };

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const sparse_element & s){
    return os << '{' << s.val<<',' << s.pos << '}' ;
  }
#else
  ostream & operator << (ostream & os,const sparse_element & s){
    return os << '{' << s.val<<',' << s.pos << '}' ;
  }
#endif

#ifdef GBASIS_F4BUCHBERGER
  bool reducef4buchbergerpos(vector<modint> &v,const vector< vector<modint> > & M,vector<int> pivotpos,modint env){
    unsigned pos=0;
    bool res=false;
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & m=M[i];
      pos=pivotpos[i];
      if (pos==-1)
	return res;
      modint c=v[pos];
      if (!c)
	continue;
      res=true;
      c=(modint2(invmod(m[pos],env))*c)%env;
      vector<modint>::const_iterator jt=m.begin()+pos+1;
      vector<modint>::iterator it=v.begin()+pos,itend=v.end();
      *it=0; ++it;
      for (;it!=itend;++jt,++it){
	if (*jt)
	  *it=(*it-modint2(c)*(*jt))%env;
      }
    }
    return res;
  }

#ifdef __x86_64__
  unsigned reducef4buchberger_64(vector<modint> &v,const vector< vector<sparse_element> > & M,modint env,vector<int128_t> & w){
    w.resize(v.size());
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    vector<int128_t>::iterator wt=w.begin();
    for (;vt!=vtend;++wt,++vt){
      *wt=*vt;
    }
    for (unsigned i=0;i<M.size();++i){
      const vector<sparse_element> & m=M[i];
      const sparse_element * it=&m.front(),*itend=it+m.size(),*it2;
      if (it==itend)
	continue;
      int128_t & ww=w[it->pos];
      if (ww==0)
	continue;
      modint c=(modint2(invmod(it->val,env))*ww)%env;
      // CERR << "multiplier ok line " << i << " value " << c << " " << w << endl;
      if (!c)
	continue;
      ww=0;
      ++it;
      it2=itend-8;
      for (;it<=it2;){
#if 0
	w[it[0].pos] -= modint2(c)*(it[0].val);
	w[it[1].pos] -= modint2(c)*(it[1].val);
	w[it[2].pos] -= modint2(c)*(it[2].val);
	w[it[3].pos] -= modint2(c)*(it[3].val);
	w[it[4].pos] -= modint2(c)*(it[4].val);
	w[it[5].pos] -= modint2(c)*(it[5].val);
	w[it[6].pos] -= modint2(c)*(it[6].val);
	w[it[7].pos] -= modint2(c)*(it[7].val);
	it+=8;
#else
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
#endif
      }
      for (;it!=itend;++it){
	w[it->pos] -= modint2(c)*(it->val);
      }
    }
    for (vt=v.begin(),wt=w.begin();vt!=vtend;++wt,++vt){
      if (*wt)
	*vt=*wt % env;
      else
	*vt=0;
    }
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return vt-v.begin();
    }
    return v.size();
  }

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const int128_t & i){
    return os << longlong(i) ;
    // return os << "(" << longlong(i>>64) <<","<< longlong(i) <<")" ;
  }
#else
  ostream & operator << (ostream & os,const int128_t & i){
    return os << longlong(i) ;
    // return os << "(" << longlong(i>>64) <<","<< longlong(i) <<")" ;
  }
#endif

#endif
  // sparse element if prime is < 2^24
  // if shift == 0 the position is absolute in the next sparse32 of the vector
  struct sparse32 {
    modint val:25;
    unsigned shift:7;
    sparse32(modint v,unsigned s):val(v),shift(s){};
    sparse32():val(0),shift(0){};
  };

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const sparse32 & s){
    return os << "(" << s.val << "," << s.shift << ")" ;
  }
#else
  ostream & operator << (ostream & os,const sparse32 & s){
    return os << "(" << s.val << "," << s.shift << ")" ;
  }
#endif

  unsigned reducef4buchberger_32(vector<modint> &v,const vector< vector<sparse32> > & M,modint env,vector<modint2> & w){
    w.resize(v.size());
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    vector<modint2>::iterator wt=w.begin();
    for (;vt!=vtend;++wt,++vt){
      *wt=*vt;
    }
    for (unsigned i=0;i<M.size();++i){
      const vector<sparse32> & m=M[i];
      vector<sparse32>::const_iterator it=m.begin(),itend=m.end(),it2=itend-16;
      if (it==itend)
	continue;
      unsigned p=0;
      modint val;
      if (it->shift){
	p += it->shift;
	val=it->val;
      }
      else {
	val=it->val;
	++it;
	p=*(unsigned *)&(*it);
      }
      modint2 & ww=w[p];
      if (ww==0)
	continue;
      modint c=(modint2(invmod(val,env))*ww)%env;
      if (!c)
	continue;
      ww=0;
      ++it;
      for (;it<=it2;){
	sparse32 se = *it;
	unsigned seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
	se = *it;
	seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
	++it;
      }
      for (;it!=itend;++it){
	const sparse32 & se = *it;
	unsigned seshift=se.shift;
	if (seshift){
	  p += seshift;
	  w[p] -= modint2(c)*se.val;
	}
	else {
	  ++it;
	  p = *(unsigned *) &*it;
	  w[p] -= modint2(c)*se.val;
	}
      }
    }
    for (vt=v.begin(),wt=w.begin();vt!=vtend;++wt,++vt){
      if (*wt)
	*vt = *wt % env;
      else
	*vt =0;
    }
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return unsigned(vt-v.begin());
    }
    return unsigned(v.size());
  }

#ifdef PSEUDO_MOD
  inline int pseudo_mod(longlong x,int p,unsigned invp,unsigned nbits){
    return int(x - (((x>>nbits)*invp)>>(nbits))*p);
  }
  // a <- (a+b*c) mod or smod p
  inline void pseudo_mod(int & a,int b,int c,int p,unsigned invp,unsigned nbits){
    a=pseudo_mod(a+((longlong)b)*c,p,invp,nbits);
  }
#endif

  unsigned reducef4buchberger(vector<modint> &v,const vector< vector<sparse_element> > & M,modint env){
#ifdef PSEUDO_MOD
    int nbits=sizeinbase2(env);
    unsigned invmodulo=((1ULL<<(2*nbits)))/env+1;
#endif
    for (unsigned i=0;i<M.size();++i){
      const vector<sparse_element> & m=M[i];
      vector<sparse_element>::const_iterator it=m.begin(),itend=m.end();
      if (it==itend)
	continue;
      modint c=(modint2(invmod(it->val,env))*v[it->pos])%env;
      v[it->pos]=0;
      if (!c)
	continue;
#ifdef PSEUDO_MOD
      if (env<(1<<29)){
	c=-c;
	for (++it;it!=itend;++it){
	  pseudo_mod(v[it->pos],c,it->val,env,invmodulo,nbits);
	}
	continue;
      }
#endif
      for (++it;it!=itend;++it){
	modint &x=v[it->pos];
	x=(x-modint2(c)*(it->val))%env;
      }
    }
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
#ifdef PSEUDO_MOD
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	*vt %= env;
    }
#endif
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return unsigned(vt-v.begin());
    }
    return unsigned(v.size());
  }


#if GIAC_SHORTSHIFTTYPE==8
  typedef unsigned char shifttype; 
  // assumes that all shifts are less than 2^(3*sizeof()), 
  // and almost all shifts are less than 2^sizeof()-1
  // for unsigned char here matrix density should be significantly above 0.004

  inline void next_index(unsigned & pos,const shifttype * & it){
    if (*it)
      pos+=(*it);
    else { // next 3 will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += (*it << 8);
      ++it;
      pos += *it;
    }
    ++it;
  }

  inline void next_index(vector<modint>::iterator & pos,const shifttype * & it){
    if (*it)
      pos+=(*it);
    else { // next 3 will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += (*it << 8);
      ++it;
      pos += *it;
    }
    ++it;
  }

  inline void next_index(vector<modint2>::iterator & pos,const shifttype * & it){
    if (*it)
      pos+=(*it);
    else { // next 3 will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += (*it << 8);
      ++it;
      pos += *it;
    }
    ++it;
  }

#ifdef __x86_64__
  inline void next_index(vector<int128_t>::iterator & pos,const shifttype * & it){
    if (*it)
      pos+=(*it);
    else { // next 3 will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += (*it << 8);
      ++it;
      pos += *it;
    }
    ++it;
  }
#endif

  unsigned first_index(const vector<shifttype> & v){
    if (v.front())
      return v.front();
    return (v[1]<<16)+(v[2]<<8)+v[3];
  }

  inline void pushsplit(vector<shifttype> & v,unsigned & pos,unsigned newpos){
    unsigned shift=newpos-pos;
    if (shift && (shift <(1<<8)))
      v.push_back(shift);
    else {
      v.push_back(0);
      v.push_back(shift >> 16 );
      v.push_back(shift >> 8);
      v.push_back(shift);
    }
    pos=newpos;
  }
#endif

#if GIAC_SHORTSHIFTTYPE==16
  typedef unsigned short shifttype; 

  inline void next_index(unsigned & pos,const shifttype * & it){
    if (*it)
      pos += (*it);
    else { // next will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += *it;
    }
    ++it;
  }

  inline void next_index(vector<modint>::iterator & pos,const shifttype * & it){
    if (*it)
      pos += (*it);
    else { // next will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += *it;
    }
    ++it;
  }

  inline void next_index(vector<modint2>::iterator & pos,const shifttype * & it){
    if (*it)
      pos += (*it);
    else { // next will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += *it;
    }
    ++it;
  }

#ifdef __x86_64__
  inline void next_index(vector<int128_t>::iterator & pos,const shifttype * & it){
    if (*it)
      pos += (*it);
    else { // next will make the shift
      ++it;
      pos += (*it << 16);
      ++it;
      pos += *it;
    }
    ++it;
  }
#endif

  unsigned first_index(const vector<shifttype> & v){
    if (v.front())
      return v.front();
    return (v[1]<<16)+v[2];
  }

  inline void pushsplit(vector<shifttype> & v,unsigned & pos,unsigned newpos){
    unsigned shift=newpos-pos;
    if ( shift && (shift < (1<<16)) )
      v.push_back(shift);
    else {
      v.push_back(0);
      v.push_back(shift >> 16 );
      v.push_back(shift);
    }
    pos=newpos;
  }
#endif

#ifndef GIAC_SHORTSHIFTTYPE
  typedef unsigned shifttype; 
  inline void next_index(unsigned & pos,const shifttype * & it){
    pos=(*it);
    ++it;
  }
  inline unsigned first_index(const vector<shifttype> & v){
    return v.front();
  }
  inline void pushsplit(vector<shifttype> & v,unsigned & pos,unsigned newpos){
    v.push_back(pos=newpos);
  }

#endif

  struct coeffindex_t {
    bool b;
    unsigned u:24;
    coeffindex_t(bool b_,unsigned u_):b(b_),u(u_) {};
    coeffindex_t():b(false),u(0) {};
  };

#ifdef __x86_64__
  unsigned reducef4buchbergersplit64(vector<modint> &v,const vector< vector<shifttype> > & M,const vector<unsigned> & firstpos,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<int128_t> & v128){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    v128.resize(v.size());
    vector<int128_t>::iterator wt=v128.begin(),wt0=wt;
    for (;vt!=vtend;++wt,++vt)
      *wt=*vt;
    vector<unsigned>::const_iterator fit=firstpos.begin(),fit0=fit,fitend=firstpos.end();
    for (;fit!=fitend;++fit){
      if (*(wt0+*fit)==0)
	continue;
      unsigned i=fit-fit0;
      const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
      bool shortshifts=coeffindex[i].b;
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end(),jt_=jtend-8;
      if (jt==jtend)
	continue;
      const vector<shifttype> & mindex=M[i];
      const shifttype * it=&mindex.front();
      unsigned pos=0;
      next_index(pos,it);
      wt=wt0+pos;
      // if (*wt==0) continue;
      // if (pos>v.size()) CERR << "error" <<endl;
      modint c=(invmod(*jt,env)*(*wt))%env;
      *wt=0;
      if (!c)
	continue;
      ++jt;
#ifdef GIAC_SHORTSHIFTTYPE
      if (shortshifts){
	if (jt<jt_){
	  while (ulonglong(it)%4){
	    wt += *it; ++it;;
	    *wt -=modint2(c)*(*jt);
	    ++jt;
	  }
	}
	for (;jt<jt_;){
#if 0
	  register unsigned pos = *(unsigned *)it;
	  wt += (pos & 0xffff);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += (pos >> 16);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  it += 2;
	  pos = *(unsigned *)it;
	  wt += (pos & 0xffff);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += (pos >> 16);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  it += 2;
#else
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;	
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  wt += *it; ++it;;
	  *wt -=modint2(c)*(*jt);
	  ++jt;	
#endif
	}
      }
      else {
	for (;jt<jt_;){
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;	
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;
	  next_index(wt,it);
	  *wt -=modint2(c)*(*jt);
	  ++jt;	
	}
      }
#else
      for (;jt<jt_;){
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
      }
#endif
      for (;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	next_index(wt,it);
	*wt -=modint2(c)*(*jt);
#else
	v128[*it]-=modint2(c)*(*jt);
	++it;
#endif
      }
    }
    for (vt=v.begin(),wt=v128.begin();vt!=vtend;++wt,++vt){
      if (*wt)
	*vt = *wt % env;
      else
	*vt=0;
    }
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return vt-v.begin();
    }
    return v.size();
  }

  unsigned reducef4buchbergersplit64u(vector<modint> &v,const vector< vector<unsigned> > & M,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<int128_t> & v128){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    v128.resize(v.size());
    vector<int128_t>::iterator wt=v128.begin();
    for (;vt!=vtend;++wt,++vt)
      *wt=*vt;
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end(),jt_=jtend-8;
      if (jt==jtend)
	continue;
      const vector<unsigned> & mindex=M[i];
      const unsigned * it=&mindex.front();
      unsigned pos=*it;
      // if (pos>v.size()) CERR << "error" <<endl;
      modint c=(invmod(*jt,env)*v128[pos])%env;
      v128[pos]=0;
      if (!c)
	continue;
      ++it;++jt;
      for (;jt<jt_;){
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
      }
      for (;jt!=jtend;++jt){
	v128[*it]-=modint2(c)*(*jt);
	++it;
      }
    }
    for (vt=v.begin(),wt=v128.begin();vt!=vtend;++wt,++vt){
      if (*wt)
	*vt = *wt % env;
      else
	*vt=0;
    }
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return vt-v.begin();
    }
    return v.size();
  }

  unsigned reducef4buchbergersplit64s(vector<modint> &v,const vector< vector<short unsigned> > & M,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<int128_t> & v128){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    v128.resize(v.size());
    vector<int128_t>::iterator wt=v128.begin();
    for (;vt!=vtend;++wt,++vt)
      *wt=*vt;
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end(),jt_=jtend-8;
      if (jt==jtend)
	continue;
      const vector<short unsigned> & mindex=M[i];
      const short unsigned * it=&mindex.front();
      unsigned pos=*it;
      // if (pos>v.size()) CERR << "error" <<endl;
      modint c=(invmod(*jt,env)*v128[pos])%env;
      v128[pos]=0;
      if (!c)
	continue;
      ++it;++jt;
      for (;jt<jt_;){
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
	v128[*it]-=modint2(c)*(*jt);
	++it; ++jt;
      }
      for (;jt!=jtend;++jt){
	v128[*it]-=modint2(c)*(*jt);
	++it;
      }
    }
    for (vt=v.begin(),wt=v128.begin();vt!=vtend;++wt,++vt){
      if (*wt)
	*vt = *wt % env;
      else
	*vt=0;
    }
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return vt-v.begin();
    }
    return v.size();
  }

#endif

  // reducef4buchberger matrix M has band structure, to spare memory
  // we split coeffs/index in :
  // - M each line is a list of shift index, 
  // - coeffindex, relative to coeffs, M[i][j] corresponds to coeffs[coeffinde[i]][j]
  // - coeffs is the list of coefficients
  unsigned reducef4buchbergersplit(vector<modint> &v,const vector< vector<shifttype> > & M,const vector<unsigned> & firstpos,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<modint2> & v64){
    vector<modint>::iterator vt=v.begin(),vt0=vt,vtend=v.end();
    vector<unsigned>::const_iterator fit=firstpos.begin(),fit0=fit,fitend=firstpos.end();
    if (env<(1<<24)){
      v64.resize(v.size());
      vector<modint2>::iterator wt=v64.begin(),wt0=wt,wtend=v64.end();
      for (;vt!=vtend;++wt,++vt)
	*wt=*vt;
      bool fastcheck = (fitend-fit0)<=0xffff;
      for (;fit!=fitend;++fit){
	if (fastcheck && *(wt0+*fit)==0)
	  continue;
	unsigned i=unsigned(fit-fit0);
	if (!fastcheck){ 
	  if ((i&0xffff)==0xffff){
	    // reduce the line mod env
	    for (wt=v64.begin();wt!=wtend;++wt){
	      if (*wt)
		*wt %= env;
	    }
	  }
	  if (v64[*fit]==0)
	    continue;
	}
	const vector<shifttype> & mindex=M[i];
	const shifttype * it=&mindex.front();
	unsigned pos=0;
	next_index(pos,it);
	wt=wt0+pos;
	// if (*wt==0) continue;
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	bool shortshifts=coeffindex[i].b;
	if (mcoeff.empty())
	  continue;
	const modint * jt=&mcoeff.front(),*jtend=jt+mcoeff.size(),*jt_=jtend-8;
	// if (pos>v.size()) CERR << "error" <<endl;
	// if (*jt!=1) CERR << "not normalized" << endl;
	modint c=(modint2(invmod(*jt,env))*(*wt))%env;
	*wt=0;
	if (!c)
	  continue;
	++jt;
#ifdef GIAC_SHORTSHIFTTYPE
	if (shortshifts){
	  for (;jt<jt_;){
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	    ++jt;
	  }
	  for (;jt!=jtend;++jt){
	    wt += *it; ++it;
	    *wt-=modint2(c)*(*jt);
	  }
	}
	else {
	  for (;jt!=jtend;++jt){
	    next_index(wt,it);
	    *wt-=modint2(c)*(*jt);
	  }
	}
#else // def GIAC_SHORTSHIFTTYPE
	for (;jt<jt_;){
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	}
	for (;jt!=jtend;++jt){
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	}
#endif // def GIAC_SHORTSHIFTTYPE
      }
      for (vt=v.begin(),wt=v64.begin();vt!=vtend;++wt,++vt){
	if (*wt)
	  *vt = *wt % env;
	else
	  *vt=0;
      }
    }
    else { // large modulo
#ifdef PSEUDO_MOD
      int nbits=sizeinbase2(env);
      unsigned invmodulo=((1ULL<<(2*nbits)))/env+1;
#endif
      for (;fit!=fitend;++fit){
	if (*(vt0+*fit)==0)
	  continue;
	unsigned i=unsigned(fit-fit0);
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	bool shortshifts=coeffindex[i].b;
	vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end(),jt_=jt-8;
	if (jt==jtend)
	  continue;
	const vector<shifttype> & mindex=M[i];
	const shifttype * it=&mindex.front();
	unsigned pos=0;
	next_index(pos,it);
	vt=v.begin()+pos;
	// if (pos>v.size()) CERR << "error" <<endl;
	modint c=(modint2(invmod(*jt,env))*(*vt))%env;
	*vt=0;
	if (!c)
	  continue;
	++jt;
#ifdef PSEUDO_MOD
	if (env<(1<<29)){
	  c=-c;
#ifdef GIAC_SHORTSHIFTTYPE
	  if (shortshifts){
	    for (;jt<jt_;){
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	      ++jt;
	    }
	    for (;jt!=jtend;++jt){
	      vt += *it; ++it;
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	    }
	  }
	  else {
	    for (;jt!=jtend;++jt){
	      next_index(vt,it);
	      // if (pos>v.size()) CERR << "error" <<endl;
	      pseudo_mod(*vt,c,*jt,env,invmodulo,nbits);
	    }
	  }
	  continue;
#else
	  for (;jt!=jtend;++jt){
	    // if (pos>v.size()) CERR << "error" <<endl;
	    pseudo_mod(v[*it],c,*jt,env,invmodulo,nbits);
	    ++it;
	  }
	  continue;
#endif // GIAC_SHORTSHIFTTYPES
	} // end if env<1<<29
#endif // PSEUDOMOD
	for (;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	  next_index(vt,it);
	  *vt = (*vt-modint2(c)*(*jt))%env;
#else
	  modint &x=v[*it];
	  ++it;
	  x=(x-modint2(c)*(*jt))%env;
#endif
	}
      }
      vector<modint>::iterator vt=v.begin(),vtend=v.end();
#ifdef PSEUDO_MOD
      for (vt=v.begin();vt!=vtend;++vt){
	if (*vt)
	  *vt %= env;
      }
#endif
    } // end else based on modulo size
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return unsigned(vt-v.begin());
    }
    return unsigned(v.size());
  }

  unsigned reducef4buchbergersplitu(vector<modint> &v,const vector< vector<unsigned> > & M,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<modint2> & v64){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    if (env<(1<<24)){
      v64.resize(v.size());
      vector<modint2>::iterator wt=v64.begin(),wtend=v64.end();
      for (;vt!=vtend;++wt,++vt)
	*wt=*vt;
      for (unsigned i=0;i<M.size();++i){
	if ((i&0xffff)==0xffff){
	  // reduce the line mod env
	  for (wt=v64.begin();wt!=wtend;++wt){
	    if (*wt)
	      *wt %= env;
	  }
	}
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	if (mcoeff.empty())
	  continue;
	const modint * jt=&mcoeff.front(),*jtend=jt+mcoeff.size(),*jt_=jtend-8;
	const vector<unsigned> & mindex=M[i];
	const unsigned * it=&mindex.front();
	unsigned pos=*it;
	// if (pos>v.size()) CERR << "error" <<endl;
	// if (*jt!=1) CERR << "not normalized" << endl;
	modint c=(modint2(invmod(*jt,env))*v64[pos])%env;
	v64[pos]=0;
	if (!c)
	  continue;
	++it; ++jt;
	for (;jt<jt_;){
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	}
	for (;jt!=jtend;++it,++jt){
	  v64[*it]-=modint2(c)*(*jt);
	}
      }
      for (vt=v.begin(),wt=v64.begin();vt!=vtend;++wt,++vt){
	if (*wt)
	  *vt = *wt % env;
	else
	  *vt=0;
      }
    }
    else { // large modulo
#ifdef PSEUDO_MOD
      int nbits=sizeinbase2(env);
      unsigned invmodulo=((1ULL<<(2*nbits)))/env+1;
#endif
      for (unsigned i=0;i<M.size();++i){
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end();
	if (jt==jtend)
	  continue;
	const vector<unsigned> & mindex=M[i];
	const unsigned * it=&mindex.front();
	unsigned pos=*it; 
	// if (pos>v.size()) CERR << "error" <<endl;
	modint c=(modint2(invmod(*jt,env))*v[pos])%env;
	v[pos]=0;
	if (!c)
	  continue;
	++it; ++jt;
#ifdef PSEUDO_MOD
	if (env<(1<<29)){
	  c=-c;
	  for (;jt!=jtend;++jt){
	    // if (pos>v.size()) CERR << "error" <<endl;
	    pseudo_mod(v[*it],c,*jt,env,invmodulo,nbits);
	    ++it;
	  }
	  continue;
	}
#endif
	for (;jt!=jtend;++jt){
	  modint &x=v[*it];
	  ++it;
	  x=(x-modint2(c)*(*jt))%env;
	}
      }
      vector<modint>::iterator vt=v.begin(),vtend=v.end();
#ifdef PSEUDO_MOD
      for (vt=v.begin();vt!=vtend;++vt){
	if (*vt)
	  *vt %= env;
      }
#endif
    } // end else based on modulo size
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return unsigned(vt-v.begin());
    }
    return unsigned(v.size());
  }

  unsigned reducef4buchbergersplits(vector<modint> &v,const vector< vector<unsigned short> > & M,vector< vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<modint2> & v64){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    if (env<(1<<24)){
      v64.resize(v.size());
      vector<modint2>::iterator wt=v64.begin(),wtend=v64.end();
      for (;vt!=vtend;++wt,++vt)
	*wt=*vt;
      for (unsigned i=0;i<M.size();++i){
	if ((i&0xffff)==0xffff){
	  // reduce the line mod env
	  for (wt=v64.begin();wt!=wtend;++wt){
	    if (*wt)
	      *wt %= env;
	  }
	}
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	if (mcoeff.empty())
	  continue;
	const modint * jt=&mcoeff.front(),*jtend=jt+mcoeff.size(),*jt_=jtend-8;
	const vector<unsigned short> & mindex=M[i];
	const unsigned short * it=&mindex.front();
	unsigned pos=*it;
	// if (pos>v.size()) CERR << "error" <<endl;
	// if (*jt!=1) CERR << "not normalized" << endl;
	modint c=(modint2(invmod(*jt,env))*v64[pos])%env;
	v64[pos]=0;
	if (!c)
	  continue;
	++it; ++jt;
	for (;jt<jt_;){
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	  v64[*it]-=modint2(c)*(*jt);
	  ++it; ++jt;
	}
	for (;jt!=jtend;++it,++jt){
	  v64[*it]-=modint2(c)*(*jt);
	}
      }
      for (vt=v.begin(),wt=v64.begin();vt!=vtend;++wt,++vt){
	if (*wt)
	  *vt = *wt % env;
	else
	  *vt=0;
      }
    }
    else { // large modulo
#ifdef PSEUDO_MOD
      int nbits=sizeinbase2(env);
      unsigned invmodulo=((1ULL<<(2*nbits)))/env+1;
#endif
      for (unsigned i=0;i<M.size();++i){
	const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
	vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end();
	if (jt==jtend)
	  continue;
	const vector<unsigned short> & mindex=M[i];
	const unsigned short * it=&mindex.front();
	unsigned pos=*it; 
	// if (pos>v.size()) CERR << "error" <<endl;
	modint c=(modint2(invmod(*jt,env))*v[pos])%env;
	v[pos]=0;
	if (!c)
	  continue;
	++it; ++jt;
#ifdef PSEUDO_MOD
	if (env<(1<<29)){
	  c=-c;
	  for (;jt!=jtend;++jt){
	    // if (pos>v.size()) CERR << "error" <<endl;
	    pseudo_mod(v[*it],c,*jt,env,invmodulo,nbits);
	    ++it;
	  }
	  continue;
	}
#endif
	for (;jt!=jtend;++jt){
	  modint &x=v[*it];
	  ++it;
	  x=(x-modint2(c)*(*jt))%env;
	}
      }
      vector<modint>::iterator vt=v.begin(),vtend=v.end();
#ifdef PSEUDO_MOD
      for (vt=v.begin();vt!=vtend;++vt){
	if (*vt)
	  *vt %= env;
      }
#endif
    } // end else based on modulo size
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return unsigned(vt-v.begin());
    }
    return unsigned(v.size());
  }

  bool tri(const vector<sparse_element> & v1,const vector<sparse_element> & v2){
    return v1.front().pos<v2.front().pos;
  }

  struct sparse_element_tri1 {
    sparse_element_tri1(){}
    bool operator() (const sparse_element & v1,const sparse_element & v2){
      return v1.val<v2.val;
    }
  };

  void sort_vector_sparse_element(vector<sparse_element>::iterator it,vector<sparse_element>::iterator itend){
    sort(it,itend,sparse_element_tri1());
  }
  
  void makeline(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<modint> & v,int start=0){
    v.resize(R.coord.size()); 
    v.assign(R.coord.size(),0);
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin()+start,itend=p.coord.end(),jt=R.coord.begin(),jtbeg=jt,jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void makelinesub(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<modint> & v,int start,modint env){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin()+start,itend=p.coord.end(),jt=R.coord.begin(),jtbeg=jt,jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    // v[jt-jtbeg] -= it->g;
	    modint & vv=v[jt-jtbeg];
	    vv = (vv-longlong(it->g))%env;
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    // v[jt-jtbeg]-=it->g;
	    modint & vv=v[jt-jtbeg];
	    vv = (vv-longlong(it->g))%env;
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  // put in v coeffs of polymod corresponding to R, and in rem those who do not match
  // returns false if v is null
  bool makelinerem(const polymod & p,polymod & rem,const polymod & R,vector<modint> & v){
    rem.coord.clear();
    v.clear();
    v.resize(R.coord.size()); 
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtend=R.coord.end();
    bool res=false;
    for (;it!=itend;++it){
      const tdeg_t & u=it->u;
      for (;jt!=jtend;++jt){
	if (tdeg_t_greater(u,jt->u,p.order) 
	    // u>=jt->u
	    ){
	  if (u==jt->u){
	    res=true;
	    v[jt-R.coord.begin()]=it->g;
	    ++jt;
	  }
	  else
	    rem.coord.push_back(*it);
	  break;
	}
      }
    }
    return res;
  }

  void makeline(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<sparse_element> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v.push_back(sparse_element(it->g,jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v.push_back(sparse_element(it->g,jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  typedef char used_t;
  // typedef bool used_t;
  
#if 1
  void convert(const vector<modint> & v,vector<sparse_element> & w,vector<used_t> & used){
    unsigned count=0;
    vector<modint>::const_iterator it=v.begin(),itend=v.end();
    vector<used_t>::iterator ut=used.begin();
    for (;it!=itend;++ut,++it){
      if (!*it)
	continue;
      *ut=1;
      ++count;
    }
    w.clear();
    w.reserve(count);
    for (count=0,it=v.begin();it!=itend;++count,++it){
      if (*it)
	w.push_back(sparse_element(*it,count));
    }
  }

#else
  void convert(const vector<modint> & v,vector<sparse_element> & w,vector<used_t> & used){
    unsigned count=0;
    vector<modint>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (*it){
#if 1
	used[it-v.begin()]=1;
#else
	++used[it-v.begin()];
	if (used[it-v.begin()]>100)
	  used[it-v.begin()]=100;
#endif
	++count;
      }
    }
    w.clear();
    w.reserve(count);
    for (it=v.begin();it!=itend;++it){
      if (*it)
	w.push_back(sparse_element(*it,it-v.begin()));
    }
  }
#endif

  // add to w non-zero coeffs of v, set bit i in bitmap to 1 if v[i]!=0
  // bitmap size is rounded to a multiple of 32
  void zconvert(const vector<modint> & v,vector<modint>::iterator & coeffit,unsigned * bitmap,vector<used_t> & used){
    vector<modint>::const_iterator it=v.begin(),itend=v.end();
    used_t * uit=&used.front();
    for (unsigned i=0;it!=itend;++i,++it){
      if (!*it)
	continue;
      *(uit+i)=1; // used[i]=1;
      bitmap[i>>5] |= (1<<(i&0x1f));
      *coeffit=*it;
      ++coeffit;
    }
  }

  void zconvert(const vector<modint> & v,vector<modint> & lescoeffs,unsigned * bitmap,vector<used_t> & used){
    vector<modint>::const_iterator it=v.begin(),itend=v.end();
    used_t * uit=&used.front();
    for (unsigned i=0;it!=itend;++i,++it){
      if (!*it)
	continue;
      *(uit+i)=1; // used[i]=1;
      bitmap[i>>5] |= (1<<(i&0x1f));
      lescoeffs.push_back(*it);
    }
  }

  // create matrix from list of coefficients and bitmap of non-zero positions
  // M must already have been created with the right number of rows
  void create_matrix(const vector<modint> & lescoeffs,const unsigned * bitmap,unsigned bitmapcols,const vector<used_t> & used,vector< vector<modint> > & M){
    unsigned nrows=unsigned(M.size());
    int ncols=0;
    vector<used_t>::const_iterator ut=used.begin(),utend=used.end();
    unsigned jend=unsigned(utend-ut);
    vector<modint>::const_iterator it=lescoeffs.begin();
    for (;ut!=utend;++ut){
      ncols += *ut;
    }
    // do all memory allocation at once, trying to speed up threaded execution
    for (unsigned i=0;i<nrows;++i)
      M[i].resize(ncols);
    for (unsigned i=0;i<nrows;++i){
      const unsigned * bitmapi = bitmap + i*bitmapcols;
      vector<modint>::iterator mi=M[i].begin();
      unsigned j=0;
      for (;j<jend;++j){
	if (!used[j])
	  continue;
	if (bitmapi[j>>5] & (1<<(j&0x1f))){
	  *mi=*it;
	  ++it;
	}
	++mi;
      }
    }
  }

  void create_matrix(const unsigned * bitmap,unsigned bitmapcols,const vector<used_t> & used,vector< vector<modint> > & M){
    unsigned nrows=unsigned(M.size());
    int ncols=0;
    vector<used_t>::const_iterator ut=used.begin(),utend=used.end();
    unsigned jend=unsigned(utend-ut);
    for (;ut!=utend;++ut){
      ncols += *ut;
    }
    vector<modint> tmp;
    for (unsigned i=0;i<nrows;++i){
      const unsigned * bitmapi = bitmap + i*bitmapcols;
      tmp.clear();
      tmp.resize(ncols);
      tmp.swap(M[i]);
      vector<modint>::iterator mi=M[i].begin(),it=tmp.begin();
      unsigned j=0;
      for (;j<jend;++j){
	if (!used[j])
	  continue;
	if (bitmapi[j>>5] & (1<<(j&0x1f))){
	  *mi=*it;
	  ++it;
	}
	++mi;
      }
    }
  }

  inline void push32(vector<sparse32> & v,modint val,unsigned & pos,unsigned newpos){
    unsigned shift=newpos-pos;
    if (newpos && (shift <(1<<7)))
      v.push_back(sparse32(val,shift));
    else {
      v.push_back(sparse32(val,0));
      v.push_back(sparse32());
      * (unsigned *) & v.back() =newpos;
    }
    pos=newpos;
  }

  void makeline32(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<sparse32> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtend=R.coord.end();
    unsigned pos=0;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    push32(v,it->g,pos,unsigned(jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    push32(v,it->g,pos,unsigned(jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void convert32(const vector<modint> & v,vector<sparse32> & w,vector<used_t> & used){
    unsigned count=0;
    vector<modint>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (*it){
#if 1
	used[it-v.begin()]=1;
#else
	++used[it-v.begin()];
	if (used[it-v.begin()]>100)
	  used[it-v.begin()]=100;
#endif
	++count;
      }
    }
    w.clear();
    w.reserve(1+int(count*1.1));
    unsigned pos=0;
    for (it=v.begin();it!=itend;++it){
      if (*it)
	push32(w,*it,pos,unsigned(it-v.begin()));
    }
  }

  void rref_f4buchbergermod_interreduce(vectpolymod & f4buchbergerv,const vector<unsigned> & f4buchbergervG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin build M" << endl;
    unsigned N=unsigned(R.coord.size()),i,j=0;
    unsigned c=N;
    double sknon0=0;
    vector<used_t> used(N,0);
    unsigned usedcount=0,zerolines=0,Msize=0;
    vector< vector<modint> > K(f4buchbergervG.size());
    for (i=0;i<G.size();++i){
      Msize += unsigned(quo[i].coord.size());
    }
    if ( env<(1<<24) && env*double(env)*Msize<9.223e18 ){
      vector< vector<sparse32> > M;
      M.reserve(N);
      vector<sparse_element> atrier;
      atrier.reserve(N);
      for (i=0;i<G.size();++i){
	std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
	for (;jt!=jtend;++j,++jt){
	  M.push_back(vector<sparse32>(0));
	  M[j].reserve(1+int(1.1*res[G[i]].coord.size()));
	  makeline32(res[G[i]],&jt->u,R,M[j]);
	  // CERR << M[j] << endl;
	  if (M[j].front().shift)
	    atrier.push_back(sparse_element(M[j].front().shift,j));
	  else
	    atrier.push_back(sparse_element(*(unsigned *) &M[j][1],j));
	}
      }
      if (debug_infolevel>1)
	CERR << CLOCK() << " end build M32" << endl;
      // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
      // CERR << "before sort " << M << endl;
      sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1);
      vector< vector<sparse32> > M1(atrier.size());
      double mem=0; // mem*4=number of bytes allocated for M1
      for (i=0;i<atrier.size();++i){
	swap(M1[i],M[atrier[i].pos]);
	mem += M1[i].size();
      }
      swap(M,M1);
      bool freemem=mem>4e7; // should depend on real memory available
      if (debug_infolevel>1)
	CERR << CLOCK() << " M32 sorted, rows " << M.size() << " columns " << N << " terms " << mem << " ratio " << (mem/M.size())/N <<endl;
      // CERR << "after sort " << M << endl;
      // step3 reduce
      vector<modint> v(N); vector<modint2> w(N);
      vector< vector<sparse32> > SK(f4buchbergerv.size());
      for (i=0;i<f4buchbergervG.size();++i){
	if (!f4buchbergerv[f4buchbergervG[i]].coord.empty()){
	  makeline(f4buchbergerv[f4buchbergervG[i]],0,R,v);
	  if (freemem){ 
	    polymod clearer; swap(f4buchbergerv[f4buchbergervG[i]].coord,clearer.coord); 
	  }
	  c=giacmin(c,reducef4buchberger_32(v,M,env,w));
	  // convert v to a sparse vector in SK and update used
	  convert32(v,SK[i],used);
	  //CERR << v << endl << SK[i] << endl;
	}
      }
      M.clear();
      if (debug_infolevel>1)
	CERR << CLOCK() << " f4buchbergerv reduced " << f4buchbergervG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
      for (i=0;i<N;++i)
	usedcount += (used[i]>0);
      if (debug_infolevel>1){
	CERR << CLOCK() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
	if (debug_infolevel>2)
	  CERR << " column32 used " << used << endl;
      }
      // create dense matrix K 
      for (i=0; i<K.size(); ++i){
	vector<modint> & v =K[i];
	if (SK[i].empty()){
	  ++zerolines;
	  continue;
	}
	v.resize(usedcount);
	vector<modint>::iterator vt=v.begin();
	vector<used_t>::const_iterator ut=used.begin(),ut0=ut;
	vector<sparse32>::const_iterator st=SK[i].begin(),stend=SK[i].end();
	unsigned p=0;
	for (j=0;st!=stend;++j,++ut){
	  if (!*ut) 
	    continue;
	  if (st->shift){
	    if (j==p + st->shift){
	      p += st->shift;
	      *vt=st->val;
	      ++st;
	      ++sknon0;
	    }
	  }
	  else {
	    if (j==* (unsigned *) &(*(st+1))){
	      *vt=st->val;
	      ++st;
	      p = * (unsigned *) &(*st);
	      ++st;
	      ++sknon0;
	    }
	  }
	  ++vt;
	}
#if 0
	vector<sparse32> clearer;
	swap(SK[i],clearer); // clear SK[i] memory
#endif
      }
    }
    else {
      vector< vector<sparse_element> > M;
      M.reserve(N);
      vector<sparse_element> atrier;
      atrier.reserve(N);
      for (i=0;i<G.size();++i){
	std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
	for (;jt!=jtend;++j,++jt){
	  M.push_back(vector<sparse_element>(0));
	  M[j].reserve(res[G[i]].coord.size());
	  makeline(res[G[i]],&jt->u,R,M[j]);
	  atrier.push_back(sparse_element(M[j].front().pos,j));
	}
      }
      if (debug_infolevel>1)
	CERR << CLOCK() << " end build M" << endl;
      // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
      // CERR << "before sort " << M << endl;
      sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
      vector< vector<sparse_element> > M1(atrier.size());
      double mem=0; // mem*8=number of bytes allocated for M1
      unsigned firstpart=0;
      for (i=0;i<atrier.size();++i){
	swap(M1[i],M[atrier[i].pos]);
	mem += M1[i].size();
	if (!M1[i].empty())
	  firstpart=giacmax(firstpart,M1[i].front().pos);
      }
      swap(M,M1);
      bool freemem=mem>4e7; // should depend on real memory available
      // sort(M.begin(),M.end(),tri);
      if (debug_infolevel>1)
	CERR << CLOCK() << " M sorted, rows " << M.size() << " columns " << N << "[" << firstpart << "] terms " << mem << " ratio " << (mem/N)/M.size() << endl;
      // CERR << "after sort " << M << endl;
      // step3 reduce
      vector<modint> v(N);
      vector< vector<sparse_element> > SK(f4buchbergerv.size());
#ifdef __x86_64__
      vector<int128_t> v128(N); 
      vector<modint> multiplier(M.size()); vector<unsigned> pos(M.size());
#endif
      for (i=0;i<f4buchbergervG.size();++i){
	if (!f4buchbergerv[f4buchbergervG[i]].coord.empty()){
	  makeline(f4buchbergerv[f4buchbergervG[i]],0,R,v);
	  if (freemem){ 
	    polymod clearer; swap(f4buchbergerv[f4buchbergervG[i]].coord,clearer.coord); 
	  }
#ifdef __x86_64__
	  /* vector<modint> w(v);
	  // CERR << "reduce " << v << endl << M << endl;
	  c=giacmin(c,reducef4buchbergerslice(w,M,env,v128,multiplier,pos));
	  c=giacmin(c,reducef4buchberger_64(v,M,env,v128));
	  if (w!=v) CERR << w << endl << v << endl; else CERR << "ok" << endl;
	  */
	  // c=giacmin(c,reducef4buchbergerslice(v,M,env,v128,multiplier,pos));
	  if (0 && env<(1<<29) && N>10000) // it's slower despite v128 not in cache
	    c=giacmin(c,reducef4buchberger(v,M,env));
	  else
	    c=giacmin(c,reducef4buchberger_64(v,M,env,v128));
#else // __x86_64__
	  c=giacmin(c,reducef4buchberger(v,M,env));
#endif // __x86_64__
	  // convert v to a sparse vector in SK and update used
	  convert(v,SK[i],used);
	  // CERR << v << endl << SK[i] << endl;
	}
      }
      M.clear();
      if (debug_infolevel>1)
	CERR << CLOCK() << " f4buchbergerv reduced " << f4buchbergervG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
      for (i=0;i<N;++i)
	usedcount += (used[i]>0);
      if (debug_infolevel>1){
	CERR << CLOCK() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
	// if (debug_infolevel>2) CERR << " column use " << used << endl;
      }
      // create dense matrix K 
      for (i=0; i<K.size(); ++i){
	vector<modint> & v =K[i];
	if (SK[i].empty()){
	  ++zerolines;
	  continue;
	}
	sknon0 += SK[i].size();
	v.resize(usedcount);
	vector<modint>::iterator vt=v.begin();
	vector<used_t>::const_iterator ut=used.begin(),ut0=ut;
	vector<sparse_element>::const_iterator st=SK[i].begin(),stend=SK[i].end();
	for (j=0;st!=stend;++j,++ut){
	  if (!*ut) 
	    continue;
	  if (j==st->pos){
	    *vt=st->val;
	    ++st;
	  }
	  ++vt;
	}
#if 1
	vector<sparse_element> clearer;
	swap(SK[i],clearer); // clear SK[i] memory
#endif
	// CERR << used << endl << SK[i] << endl << K[i] << endl;
      }
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> maxrankcols; longlong idet;
    // CERR << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,int(K.size()),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    //CERR << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv interreduced" << endl;
    for (i=0;i<f4buchbergervG.size();++i){
#if 0 // spare memory, keep exactly the right number of monomials in f4buchbergerv[]
      polymod tmpP(f4buchbergerv[f4buchbergervG[i]].order,f4buchbergerv[f4buchbergervG[i]].dim);
      vector<modint> & v =K[permu[i]];
      if (v.empty())
	continue;
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=tmpP.coord;
      Pcoord.reserve(vcount);
      vector<used_t>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),tmpP,env);	
	Pcoord.front().g=1;
      }
      swap(tmpP.coord,f4buchbergerv[f4buchbergervG[i]].coord);
#else
      // CERR << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4buchbergerv[f4buchbergervG[i]].coord;
      Pcoord.clear();
      vector<modint> & v =K[permu[i]];
      if (v.empty())
	continue;
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      Pcoord.reserve(vcount);
      vector<used_t>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4buchbergerv[f4buchbergervG[i]],env);	
	Pcoord.front().g=1;
      }
#endif
    }
  }


  void copycoeff(const polymod & p,vector<modint> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    for (;it!=itend;++it)
      v.push_back(it->g);
  }

  void copycoeff(const poly8 & p,vector<gen> & v){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    for (;it!=itend;++it)
      v.push_back(it->g);
  }

  // dichotomic seach for jt->u==u in [jt,jtend[
  bool dicho(std::vector< T_unsigned<modint,tdeg_t> >::const_iterator & jt,std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jtend,const tdeg_t & u,order_t order){
    if (jt->u==u) return true;
    for (;;){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator j=jt+(jtend-jt)/2;
      if (j==jt)
	return j->u==u;
      if (int res=tdeg_t_greater(j->u,u,order)){
	jt=j;
	if (res==2)
	  return true;
      }
      else
	jtend=j;
    }
  }

  void makelinesplit(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<shifttype> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtend=R.coord.end();
    unsigned pos=0;
    double nop1=R.coord.size(); 
    double nop2=4*p.coord.size()*std::log(nop1)/std::log(2.0);
    bool dodicho=nop2<nop1;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	/* new faster code */
	if (dodicho && dicho(jt,jtend,u,R.order)){
	  pushsplit(v,pos,unsigned(jt-R.coord.begin()));
	  ++jt;
	  continue;
	}
	/* end new faster code */
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    pushsplit(v,pos,unsigned(jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	/* new faster code */
	if (dodicho && dicho(jt,jtend,u,R.order)){
	  pushsplit(v,pos,unsigned(jt-R.coord.begin()));
	  ++jt;
	  continue;
	}
	/* end new faster code */
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    pushsplit(v,pos,unsigned(jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  // return true if all shifts are <=0xffff
  bool checkshortshifts(const vector<shifttype> & v){
    if (v.empty())
      return false;
    const shifttype * it=&v.front(),*itend=it+v.size();
    // ignore first, it's not a shift
    unsigned pos;
    next_index(pos,it);
    for (;it!=itend;++it){
      if (!*it)
	return false;
    }
    return true;
  }

  void makelinesplit(const poly8 & p,const tdeg_t * shiftptr,const polymod & R,vector<shifttype> & v){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=R.coord.begin(),jt0=jt,jtend=R.coord.end();
    unsigned pos=0;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    pushsplit(v,pos,unsigned(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    pushsplit(v,pos,unsigned(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void makelinesplitu(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<unsigned> & vu){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jt0=jt,jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    vu.push_back(hashgcd_U(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    vu.push_back(hashgcd_U(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void makelinesplits(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<unsigned short> & vu){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jt0=jt,jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    vu.push_back(hashgcd_U(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    vu.push_back(hashgcd_U(jt-jt0));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }


#define GIAC_Z

  // perhaps a good idea to lock when memory allocation occur?
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_t gbasismutex = PTHREAD_MUTEX_INITIALIZER;
#endif

  void rref_f4buchbergermodsplit_interreduce(vectpolymod & f4buchbergerv,const vector<unsigned> & f4buchbergervG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin build M" << endl;
    unsigned N=unsigned(R.coord.size()),i,j=0;
    if (N==0) return;
#if GIAC_SHORTSHIFTTYPE==16
    bool useshort=true;
#else
    bool useshort=N<=0xffff;
#endif
    unsigned nrows=0;
    for (i=0;i<G.size();++i){
      nrows += unsigned(quo[i].coord.size());
    }
    unsigned c=N;
    double sknon0=0;
    vector<used_t> used(N,0);
    unsigned usedcount=0,zerolines=0;
    vector< vector<modint> > K(f4buchbergervG.size());
    vector<vector<unsigned short> > Mindex;
    vector<vector<unsigned> > Muindex;
    vector< vector<modint> > Mcoeff(G.size());
    vector<coeffindex_t> coeffindex;
    if (useshort)
      Mindex.reserve(nrows);
    else
      Muindex.reserve(nrows);
    coeffindex.reserve(nrows);
    vector<sparse_element> atrier;
    atrier.reserve(nrows);
    for (i=0;i<G.size();++i){
      Mcoeff[i].reserve(res[G[i]].coord.size());
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      if (useshort){
	for (;jt!=jtend;++j,++jt){
	  Mindex.push_back(vector<unsigned short>(0));
#if GIAC_SHORTSHIFTTYPE==16
	  Mindex[j].reserve(int(1.1*res[G[i]].coord.size()));
#else
	  Mindex[j].reserve(res[G[i]].coord.size());
#endif
	}
      }
      else {
	for (;jt!=jtend;++j,++jt){
	  Muindex.push_back(vector<unsigned>(0));
	  Muindex[j].reserve(res[G[i]].coord.size());
	}
      }
    }
    for (i=0,j=0;i<G.size();++i){
      // copy coeffs of res[G[i]] in Mcoeff
      copycoeff(res[G[i]],Mcoeff[i]);
      // for each monomial of quo[i], find indexes and put in Mindex
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	coeffindex.push_back(coeffindex_t(N<=0xffff,i));
	if (useshort){
#if GIAC_SHORTSHIFTTYPE==16
	  makelinesplit(res[G[i]],&jt->u,R,Mindex[j]);
	  if (!coeffindex.back().b)
	    coeffindex.back().b=checkshortshifts(Mindex[j]);
	  atrier.push_back(sparse_element(first_index(Mindex[j]),j));
#else
	  makelinesplits(res[G[i]],&jt->u,R,Mindex[j]);
	  atrier.push_back(sparse_element(Mindex[j].front(),j));
#endif
	}
	else {
	  makelinesplitu(res[G[i]],&jt->u,R,Muindex[j]);
	  atrier.push_back(sparse_element(Muindex[j].front(),j));
	}
      }
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " end build Mindex/Mcoeff rref_f4buchbergermodsplit_interreduce" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // CERR << "before sort " << M << endl;
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector<coeffindex_t> coeffindex1(atrier.size());
    double mem=0; // mem*4=number of bytes allocated for M1
    if (useshort){
      vector< vector<unsigned short> > Mindex1(atrier.size());
      for (i=0;i<atrier.size();++i){
	swap(Mindex1[i],Mindex[atrier[i].pos]);
	mem += Mindex1[i].size();
	swap(coeffindex1[i],coeffindex[atrier[i].pos]);
      }
      swap(Mindex,Mindex1);
      nrows=unsigned(Mindex.size());
    }
    else {
      vector< vector<unsigned> > Muindex1(atrier.size());
      for (i=0;i<atrier.size();++i){
	swap(Muindex1[i],Muindex[atrier[i].pos]);
	mem += Muindex1[i].size();
	swap(coeffindex1[i],coeffindex[atrier[i].pos]);
      }
      swap(Muindex,Muindex1);
      nrows=unsigned(Muindex.size());
    }
    swap(coeffindex,coeffindex1);
    vector<unsigned> firstpos(atrier.size());
    for (i=0;i < atrier.size();++i){
      firstpos[i]=atrier[i].val;
    }
    bool freemem=true; // mem>4e7; // should depend on real memory available
    if (debug_infolevel>1)
      CERR << CLOCK() << " Mindex sorted, rows " << nrows << " columns " << N << " terms " << mem << " ratio " << (mem/nrows)/N <<endl;
    // CERR << "after sort " << M << endl;
    // step3 reduce
    vector<modint> v(N); 
    vector<modint2> v64(N);
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
#ifdef GIAC_Z
    if (N<nrows){
      CERR << "Error " << N << "," << nrows << endl;
      return;
    }
    unsigned Kcols=N-nrows;
    unsigned effectivef4buchbergervGsize=0;
    for (unsigned i=0;i<f4buchbergervG.size();++i){
      if (!f4buchbergerv[f4buchbergervG[i]].coord.empty())
	++effectivef4buchbergervGsize;
    }
    vector<modint> lescoeffs;
    lescoeffs.reserve(Kcols*effectivef4buchbergervGsize);
    // vector<modint> lescoeffs(Kcols*effectivef4buchbergervGsize);
    // vector<modint>::iterator coeffit=lescoeffs.begin();
    if (debug_infolevel>1)
      CERR << "Capacity for coeffs " << lescoeffs.size() << endl;
    vector<unsigned> lebitmap(((N>>5)+1)*effectivef4buchbergervGsize);
    unsigned * bitmap=&lebitmap.front();
#else
    vector< vector<sparse_element> > SK(f4buchbergerv.size());
#endif
    for (i=0;i<f4buchbergervG.size();++i){
      if (!f4buchbergerv[f4buchbergervG[i]].coord.empty()){
	makeline(f4buchbergerv[f4buchbergervG[i]],0,R,v);
	//CERR << v << endl;
#ifdef __x86_64__
	if (useshort){
	  if (env<(1<<24)){
#if GIAC_SHORTSHIFTTYPE==16
	    c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
#else
	    c=giacmin(c,reducef4buchbergersplits(v,Mindex,Mcoeff,coeffindex,env,v64));
#endif
	  }
	  else {
#if GIAC_SHORTSHIFTTYPE==16
	    c=giacmin(c,reducef4buchbergersplit64(v,Mindex,firstpos,Mcoeff,coeffindex,env,v128));
#else
	    c=giacmin(c,reducef4buchbergersplit64s(v,Mindex,Mcoeff,coeffindex,env,v128));
#endif
	  }
	}
	else {
	  if (env<(1<<24))
	    c=giacmin(c,reducef4buchbergersplitu(v,Muindex,Mcoeff,coeffindex,env,v64));
	  else
	    c=giacmin(c,reducef4buchbergersplit64u(v,Muindex,Mcoeff,coeffindex,env,v128));
	}
#else
	if (useshort){
#if GIAC_SHORTSHIFTTYPE==16
	  c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
#else
	  c=giacmin(c,reducef4buchbergersplits(v,Mindex,Mcoeff,coeffindex,env,v64));
#endif
	}
	else 
	  c=giacmin(c,reducef4buchbergersplitu(v,Muindex,Mcoeff,coeffindex,env,v64));
#endif
	// convert v to a sparse vector in SK and update used
	if (freemem){ 
	  polymod clearer; swap(f4buchbergerv[f4buchbergervG[i]].coord,clearer.coord); 
	}
#ifdef GIAC_Z
	// zconvert(v,coeffit,bitmap,used); bitmap += (N>>5)+1;
	zconvert(v,lescoeffs,bitmap,used); bitmap += (N>>5)+1;
#else
	convert(v,SK[i],used);
#endif
	//CERR << v << endl << SK[i] << endl;
      }
    }
#if 0 // def GIAC_Z
    if (debug_infolevel>1) CERR << "Total size for coeffs " << coeffit-lescoeffs.begin() << endl;
    if (freemem){ 
      for (i=0;i<f4buchbergervG.size();++i){
	polymod clearer; swap(f4buchbergerv[f4buchbergervG[i]].coord,clearer.coord); 
      }
    }
#endif
    Mindex.clear(); Muindex.clear();
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv split reduced " << f4buchbergervG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
    for (i=0;i<N;++i)
      usedcount += (used[i]>0);
    if (debug_infolevel>1){
      CERR << CLOCK() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
      if (debug_infolevel>3)
	CERR << " column split used " << used << endl;
    }
    // create dense matrix K 
#ifdef GIAC_Z
    bitmap=&lebitmap.front();
    create_matrix(lescoeffs,bitmap,(N>>5)+1,used,K);
    if (freemem){ 
      // clear memory required for lescoeffs
      vector<modint> tmp; lescoeffs.swap(tmp); 
      vector<unsigned> tmp1; lebitmap.swap(tmp1);
    }
#else
    for (i=0; i<K.size(); ++i){
      vector<modint> & v =K[i];
      if (SK[i].empty()){
	++zerolines;
	continue;
      }
      sknon0 += SK[i].size();
      v.resize(usedcount);
      vector<modint>::iterator vt=v.begin();
      vector<used_t>::const_iterator ut=used.begin(),ut0=ut;
      vector<sparse_element>::const_iterator st=SK[i].begin(),stend=SK[i].end();
      for (j=0;st!=stend;++j,++ut){
	if (!*ut) 
	  continue;
	if (j==st->pos){
	  *vt=st->val;
	  ++st;
	}
	++vt;
      }
#if 1
      vector<sparse_element> clearer;
      swap(SK[i],clearer); // clear SK[i] memory
#endif
      // CERR << used << endl << SK[i] << endl << K[i] << endl;
    } // end create dense matrix K
#endif // GIAC_Z
    if (debug_infolevel>1)
      CERR << CLOCK() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> maxrankcols; longlong idet;
    //CERR << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,int(K.size()),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    //CERR << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv interreduced" << endl;
    for (i=0;i<f4buchbergervG.size();++i){
      // CERR << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4buchbergerv[f4buchbergervG[i]].coord;
      Pcoord.clear();
      vector<modint> & v =K[permu[i]];
      if (v.empty())
	continue;
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      Pcoord.reserve(vcount);
      vector<used_t>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4buchbergerv[f4buchbergervG[i]],env);	
	Pcoord.front().g=1;
      }
    }
  }

  void rref_f4buchbergermod_nointerreduce(vectpolymod & f4buchbergerv,const vector<unsigned> & f4buchbergervG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    unsigned N=unsigned(R.coord.size()),i,j=0;
    for (i=0;i<G.size();++i){
      if (!quo[i].coord.empty())
	break;
    }
    if (i==G.size()){
      if (debug_infolevel>1)
	CERR << CLOCK() << " No inter-reduction" << endl;
      return;
    }
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin build M" << endl;
    vector< vector<sparse_element> > M;
    M.reserve(N);
    vector<sparse_element> atrier;
    atrier.reserve(N);
    for (i=0;i<G.size();++i){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vector<sparse_element>(0));
	M[j].reserve(res[G[i]].coord.size());
	makeline(res[G[i]],&jt->u,R,M[j]);
	atrier.push_back(sparse_element(M[j].front().pos,j));
      }
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " end build M" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // CERR << "before sort " << M << endl;
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector< vector<sparse_element> > M1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(M1[i],M[atrier[i].pos]);
    }
    swap(M,M1);
    // sort(M.begin(),M.end(),tri);
    if (debug_infolevel>1)
      CERR << CLOCK() << " M sorted, rows " << M.size() << " columns " << N << " #basis to reduce" << f4buchbergervG.size() << endl;
    // CERR << "after sort " << M << endl;
    // step3 reduce
    unsigned c=N;
    vector<modint> v(N);
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
    for (i=0;i<f4buchbergervG.size();++i){
      if (!f4buchbergerv[f4buchbergervG[i]].coord.empty()){
	makeline(f4buchbergerv[f4buchbergervG[i]],0,R,v);
	// CERR << v << endl;
#ifdef __x86_64__
	/* if (N>=4096)
	  c=giacmin(c,reducef4buchberger(v,M,env));
	  else */
	  c=giacmin(c,reducef4buchberger_64(v,M,env,v128));
#else
	c=giacmin(c,reducef4buchberger(v,M,env));
#endif
	vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4buchbergerv[f4buchbergervG[i]].coord;
	Pcoord.clear();
	unsigned vcount=0;
	vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
	for (;vt!=vtend;++vt){
	  if (*vt)
	    ++vcount;
	}
	Pcoord.reserve(vcount);
	for (vt=v.begin(),it=R.coord.begin();it!=itend;++it){
	  modint coeff=*vt;
	  ++vt;
	  if (coeff!=0)
	    Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
	}
	if (!Pcoord.empty() && Pcoord.front().g!=1){
	  smallmultmod(invmod(Pcoord.front().g,env),f4buchbergerv[f4buchbergervG[i]],env);	
	  Pcoord.front().g=1;
	}
      }
    }
  }

  void rref_f4buchbergermod(vectpolymod & f4buchbergerv,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation,bool split){
    vector<unsigned> f4buchbergervG(f4buchbergerv.size());
    for (unsigned i=0;i<f4buchbergerv.size();++i)
      f4buchbergervG[i]=i;
#if 0
    rref_f4buchbergermod(f4buchbergerv,f4buchbergervG,res,G,excluded,quo,R,env,permutation,true);
#else
    if (//1
	split
	//0
	)
      rref_f4buchbergermodsplit_interreduce(f4buchbergerv,f4buchbergervG,res,G,excluded,quo,R,env,permutation);
    else
      rref_f4buchbergermod_interreduce(f4buchbergerv,f4buchbergervG,res,G,excluded,quo,R,env,permutation);
#endif
  }

  struct info_t {
    vectpolymod quo,quo2;
    polymod R,R2;
    vector<int> permu;
    vector< pair<unsigned,unsigned> > B;
    vector<unsigned> G;
    unsigned nonzero;
  };

  void reducemodf4buchberger(vectpolymod & f4buchbergerv,vectpolymod & res,const vector<unsigned> & G,unsigned excluded, modint env,info_t & info_tmp){
    polymod allf4buchberger(f4buchbergerv.front().order,f4buchbergerv.front().dim),rem(f4buchbergerv.front().order,f4buchbergerv.front().dim);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchberger begin collect monomials on #polys " << f4buchbergerv.size() << endl;
    // collect all terms in f4buchbergerv
    collect(f4buchbergerv,allf4buchberger);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchberger symbolic preprocess" << endl;
    // find all monomials required to reduce all polymod in f4buchberger with res[G[.]]
    symbolic_preprocess(allf4buchberger,res,G,excluded,info_tmp.quo,rem,&info_tmp.R);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchberger end symbolic preprocess" << endl;
    // build a matrix with first lines res[G[.]]*quo[.] in terms of monomials in S
    // and finishing with lines of f4buchbergerv
    // rref (below) the matrix and find the last lines in f4buchbergerv
    rref_f4buchbergermod(f4buchbergerv,res,G,excluded,info_tmp.quo,info_tmp.R,env,info_tmp.permu,true); // do splitting
  }

  // v -= v1, assumes that no overflow can happen (modulo must be 2^30)
  void sub(vector<modint> & v,const vector<modint> & v1,modint env){
    vector<modint>::const_iterator jt=v1.begin();
    vector<modint>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++jt,++it){
      *it -= *jt;
      if (*it>-env && *it<env)
	continue;
      if (*it>env)
	*it -=env;
      else
	*it += env;
    }
#if 0
    // normalize first element to 1
    for (it=v.begin();it!=itend;++it){
      if (*it)
	break;
    }
    if (it!=itend){
      modint c=invmod(*it,env);
      *it=1;
      for (++it;it!=itend;++it){
	if (*it)
	  *it=(longlong(c)*(*it))%env;
      }
    }
#endif
  }

  int f4mod(vectpolymod & res,const vector<unsigned> & G,modint env,vector< pair<unsigned,unsigned> > & B,vectpolymod & f4buchbergerv,bool learning,unsigned & learned_position,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< info_t >* f4buchberger_info,unsigned & f4buchberger_info_position,bool recomputeR){
    if (B.empty())
      return 0;
    vector<tdeg_t> leftshift(B.size());
    vector<tdeg_t> rightshift(B.size());
    leftright(res,B,leftshift,rightshift);
    f4buchbergerv.resize(B.size());
    info_t info_tmp;
    unsigned nonzero=unsigned(B.size());
    info_t * info_ptr=&info_tmp;
    if (!learning && f4buchberger_info && f4buchberger_info_position<f4buchberger_info->size()){
      info_ptr=&(*f4buchberger_info)[f4buchberger_info_position];
      ++f4buchberger_info_position;
      nonzero=info_ptr->nonzero;
    }
    else {
      polymod all(res[B[0].first].order,res[B[0].first].dim),rem;
      if (debug_infolevel>1)
	CERR << CLOCK() << " f4buchberger begin collect monomials on #polys " << f4buchbergerv.size() << endl;
      collect(res,B,all,leftshift,rightshift);
      if (debug_infolevel>1)
	CERR << CLOCK() << " f4buchberger symbolic preprocess" << endl;
      symbolic_preprocess(all,res,G,-1,info_tmp.quo,rem,&info_tmp.R);
      if (debug_infolevel>1)
	CERR << CLOCK() << " end symbolic preprocess, rem size " << rem.coord.size() << endl;
    }
    polymod & R = info_ptr->R;
    vectpolymod & quo = info_ptr->quo;
	unsigned N = unsigned(R.coord.size()), i, j = 0;
    if (N==0){
      if (learning && f4buchberger_info)
	f4buchberger_info->push_back(*info_ptr);
      return 1;
    }
#if GIAC_SHORTSHIFTTYPE==16
    bool useshort=true;
#else
    bool useshort=N<=0xffff;
#endif
    unsigned nrows=0;
    for (i=0;i<G.size();++i){
		nrows += unsigned(quo[i].coord.size());
    }
    unsigned c=N;
    double sknon0=0;
    vector<used_t> used(N,0);
    unsigned usedcount=0,zerolines=0;
    vector< vector<modint> > K(B.size());
    vector<vector<unsigned short> > Mindex;
    vector<vector<unsigned> > Muindex;
    vector< vector<modint> > Mcoeff(G.size());
    vector<coeffindex_t> coeffindex;
    if (useshort)
      Mindex.reserve(nrows);
    else
      Muindex.reserve(nrows);
    coeffindex.reserve(nrows);
    vector<sparse_element> atrier;
    atrier.reserve(nrows);
    for (i=0;i<G.size();++i){
      Mcoeff[i].reserve(res[G[i]].coord.size());
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      if (useshort){
	for (;jt!=jtend;++j,++jt){
	  Mindex.push_back(vector<unsigned short>(0));
#if GIAC_SHORTSHIFTTYPE==16
	  Mindex[j].reserve(int(1.1*res[G[i]].coord.size()));
#else
	  Mindex[j].reserve(res[G[i]].coord.size());
#endif
	}
      }
      else {
	for (;jt!=jtend;++j,++jt){
	  Muindex.push_back(vector<unsigned>(0));
	  Muindex[j].reserve(res[G[i]].coord.size());
	}
      }
    }
    for (i=0,j=0;i<G.size();++i){
      // copy coeffs of res[G[i]] in Mcoeff
      copycoeff(res[G[i]],Mcoeff[i]);
      // for each monomial of quo[i], find indexes and put in Mindex
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	coeffindex.push_back(coeffindex_t(N<=0xffff,i));
	if (useshort){
#if GIAC_SHORTSHIFTTYPE==16
	  makelinesplit(res[G[i]],&jt->u,R,Mindex[j]);
	  if (!coeffindex.back().b)
	    coeffindex.back().b=checkshortshifts(Mindex[j]);
	  atrier.push_back(sparse_element(first_index(Mindex[j]),j));
#else
	  makelinesplits(res[G[i]],&jt->u,R,Mindex[j]);
	  atrier.push_back(sparse_element(Mindex[j].front(),j));
#endif
	}
	else {
	  makelinesplitu(res[G[i]],&jt->u,R,Muindex[j]);
	  atrier.push_back(sparse_element(Muindex[j].front(),j));
	}
      }
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " end build Mindex/Mcoeff f4mod" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // CERR << "before sort " << M << endl;
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector<coeffindex_t> coeffindex1(atrier.size());
    double mem=0; // mem*4=number of bytes allocated for M1
    if (useshort){
      vector< vector<unsigned short> > Mindex1(atrier.size());
      for (i=0;i<atrier.size();++i){
	swap(Mindex1[i],Mindex[atrier[i].pos]);
	mem += Mindex1[i].size();
	swap(coeffindex1[i],coeffindex[atrier[i].pos]);
      }
      swap(Mindex,Mindex1);
	  nrows = unsigned(Mindex.size());
    }
    else {
      vector< vector<unsigned> > Muindex1(atrier.size());
      for (i=0;i<atrier.size();++i){
	swap(Muindex1[i],Muindex[atrier[i].pos]);
	mem += Muindex1[i].size();
	swap(coeffindex1[i],coeffindex[atrier[i].pos]);
      }
      swap(Muindex,Muindex1);
	  nrows = unsigned(Muindex.size());
    }
    swap(coeffindex,coeffindex1);
    vector<unsigned> firstpos(atrier.size());
    for (i=0;i < atrier.size();++i){
      firstpos[i]=atrier[i].val;
    }
    bool freemem=mem>4e7; // should depend on real memory available
    if (debug_infolevel>1)
      CERR << CLOCK() << " Mindex sorted, rows " << nrows << " columns " << N << " terms " << mem << " ratio " << (mem/nrows)/N <<endl;
    // CERR << "after sort " << M << endl;
    // step3 reduce
    vector<modint> v(N);
    vector<modint2> v64(N);
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
    if (N<nrows){
      CERR << "Error " << N << "," << nrows << endl;
      return -1;
    }
    unsigned Kcols=N-nrows;
    vector<unsigned> lebitmap(((N>>5)+1)*B.size());
    unsigned * bitmap=&lebitmap.front();
    for (i=0;i<B.size();++i){
      pair<unsigned,unsigned> bk=B[i];
      if (!learning && pairs_reducing_to_zero && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	if (debug_infolevel>2)
	  CERR << bk << " f4buchberger learned " << learned_position << endl;
	++learned_position;
	unsigned tofill=(N>>5)+1;
	fill(bitmap,bitmap+tofill,0);
	bitmap += tofill;
	continue;
      }
      makeline(res[bk.first],&leftshift[i],R,v,1);
      makelinesub(res[bk.second],&rightshift[i],R,v,1,env);
      // CERR << v << endl << v2 << endl;
      // sub(v,v2,env);
      // CERR << v << endl;
#ifdef __x86_64__
      if (useshort){
	if (env<(1<<24)){
#if GIAC_SHORTSHIFTTYPE==16
	  c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
#else
	  c=giacmin(c,reducef4buchbergersplits(v,Mindex,Mcoeff,coeffindex,env,v64));
#endif
	}
	else {
#if GIAC_SHORTSHIFTTYPE==16
	  c=giacmin(c,reducef4buchbergersplit64(v,Mindex,firstpos,Mcoeff,coeffindex,env,v128));
#else
	  c=giacmin(c,reducef4buchbergersplit64s(v,Mindex,Mcoeff,coeffindex,env,v128));
#endif
	}
      }
      else {
	if (env<(1<<24))
	  c=giacmin(c,reducef4buchbergersplitu(v,Muindex,Mcoeff,coeffindex,env,v64));
	else
	  c=giacmin(c,reducef4buchbergersplit64u(v,Muindex,Mcoeff,coeffindex,env,v128));
      }
#else // __x86_64__
      if (useshort){
#if GIAC_SHORTSHIFTTYPE==16
	c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
#else
	c=giacmin(c,reducef4buchbergersplits(v,Mindex,Mcoeff,coeffindex,env,v64));
#endif
      }
      else 
	c=giacmin(c,reducef4buchbergersplitu(v,Muindex,Mcoeff,coeffindex,env,v64));
#endif // __x86_64__
      // zconvert(v,coeffit,bitmap,used); bitmap += (N>>5)+1;
      K[i].reserve(Kcols);
      zconvert(v,K[i],bitmap,used); bitmap += (N>>5)+1;
      //CERR << v << endl << SK[i] << endl;
    } // end for (i=0;i<B.size();++i)
    Mindex.clear(); Muindex.clear();
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv split reduced " << B.size() << " polynoms over " << N << " monomials, start at " << c << endl;
    for (i=0;i<N;++i)
      usedcount += (used[i]>0);
    if (debug_infolevel>1){
      CERR << CLOCK() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
      if (debug_infolevel>3)
	CERR << " column split used " << used << endl;
    }
    // create dense matrix K 
    bitmap=&lebitmap.front();
    create_matrix(bitmap,(N>>5)+1,used,K);
    // clear memory required for lescoeffs
    //vector<modint> tmp; lescoeffs.swap(tmp); 
    { vector<unsigned> tmp1; lebitmap.swap(tmp1); }
    if (debug_infolevel>1)
      CERR << CLOCK() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> permutation,maxrankcols; longlong idet;
    // CERR << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,int(K.size()),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    //CERR << K << endl;
	unsigned first0 = unsigned(pivots.size());
    if (first0<K.size() && (learning || !f4buchberger_info)){
      vector<modint> & tmpv=K[first0];
      for (i=0;i<tmpv.size();++i){
	if (tmpv[i])
	  break;
      }
      if (i==tmpv.size()){
		  unsigned Ksize = unsigned(K.size());
	K.resize(first0);
	K.resize(Ksize);
      }
    }
    //CERR << permutation << K << endl;
    if (!learning && f4buchberger_info){
      // check that permutation is the same as learned permutation
      for (unsigned j=0;j<permutation.size();++j){
	if (permutation[j]!=info_ptr->permu[j]){
	  CERR << "learning failed"<<endl;
	  return -1;
	}
      }
    }
    if (learning)
      info_ptr->permu=permutation;
    // CERR << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    // vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv interreduced" << endl;
    for (i=0;i<f4buchbergerv.size();++i){
      // CERR << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4buchbergerv[permutation[i]].coord;
      Pcoord.clear();
      vector<modint> & v =K[i];
      if (v.empty()){
	continue;
      }
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      Pcoord.reserve(vcount);
      vector<used_t>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4buchbergerv[permutation[i]],env);	
	Pcoord.front().g=1;
      }
      if (freemem){
	vector<modint> tmp; tmp.swap(v);
      }
    }
    if (learning && f4buchberger_info){
#if 0
      f4buchberger_info->push_back(*info_ptr);
#else
      info_t tmp;
      f4buchberger_info->push_back(tmp);
      info_t & i=f4buchberger_info->back();
      swap(i.quo,info_ptr->quo);
      swap(i.permu,info_ptr->permu);
      swap(i.R.coord,info_ptr->R.coord);
      i.R.order=info_ptr->R.order;
      i.R.dim=info_ptr->R.dim;
#endif
    }
    return 1;
  }

  bool apply(vector<int> permu,vectpolymod & res){
    vectpolymod tmp;
    for (unsigned i=0;i<res.size();++i){
      tmp.push_back(polymod(res.front().order,res.front().dim));
      swap(tmp[i].coord,res[permu[i]].coord);
      tmp[i].sugar=res[permu[i]].sugar;
    }
    swap(tmp,res);
    return true;
  }

  int f4mod(vectpolymod & res,const vector<unsigned> & G,modint env,vector< pair<unsigned,unsigned> > & smallposp,vectpolymod & f4buchbergerv,bool learning,unsigned & learned_position,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,info_t & information,vector< info_t >* f4buchberger_info,unsigned & f4buchberger_info_position,bool recomputeR, polymod & TMP1,polymod & TMP2){
    // Improve: we don't really need to compute the s-polys here
    // it's sufficient to do that at linalg step
    if (debug_infolevel>1)
      CERR << CLOCK() << " Computing s-polys " << smallposp.size() << endl;
    if (f4buchbergerv.size()<smallposp.size())
      f4buchbergerv.clear();
    f4buchbergerv.resize(smallposp.size());
    for (unsigned i=0;i<smallposp.size();++i){
      f4buchbergerv[i].dim=TMP1.dim; f4buchbergerv[i].order=TMP1.order;
      pair<unsigned,unsigned> bk=smallposp[i];
      if (!learning && pairs_reducing_to_zero && f4buchberger_info && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	if (debug_infolevel>2)
	  CERR << bk << " f4buchberger learned " << learned_position << endl;
	++learned_position;
	f4buchbergerv[i].coord.clear();
	continue;
      }
      if (debug_infolevel>2)
	CERR << bk << " f4buchberger not learned " << learned_position << endl;
      if (debug_infolevel>2 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	CERR << CLOCK() << " mod reducing pair with 1 element not in basis " << bk << endl;
      // polymod h(res.front().order,res.front().dim);
      spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
      f4buchbergerv[i].coord.swap(TMP1.coord);
    }
    if (f4buchbergerv.empty())
      return 0;
    // reduce spolys in f4buchbergerv
    if (debug_infolevel>1)
      CERR << CLOCK() << " base size " << G.size() << " reduce f4buchberger begin on " << f4buchbergerv.size() << " pairs" << endl;
    if (!learning && f4buchberger_info && f4buchberger_info_position<f4buchberger_info->size()){
      info_t & info=(*f4buchberger_info)[f4buchberger_info_position];
      // apply(perminv(info.permu),f4buchbergerv);
      if (recomputeR){
	swap(information.permu,info.permu);
	reducemodf4buchberger(f4buchbergerv,res,G,-1,env,info);
	swap(information.permu,info.permu);
      }
      else
	rref_f4buchbergermod(f4buchbergerv,res,G,-1,info.quo,info.R,env,information.permu,false); // don't split
      // apply(info.permu,f4buchbergerv);
      // information.permu should be identity, otherwise the whole learning process failed
      for (unsigned j=0;j<information.permu.size();++j){
	if (information.permu[j]!=info.permu[j]){
	  CERR << "learning failed"<<endl;
	  return -1;
	}
      }
      ++f4buchberger_info_position;
    }
    else {
      reducemodf4buchberger(f4buchbergerv,res,G,-1,env,information);
      if (learning && f4buchberger_info){
#if 0
	f4buchberger_info->push_back(information);
#else
	info_t tmp;
	f4buchberger_info->push_back(tmp);
	info_t & i=f4buchberger_info->back();
	swap(i.quo,information.quo);
	swap(i.permu,information.permu);
	swap(i.R.coord,information.R.coord);
	i.R.order=information.R.order;
	i.R.dim=information.R.dim;
#endif
      }
    }
    return 1;
  }
  
  bool in_gbasisf4buchbergermod(vectpolymod &res,unsigned ressize,vector<unsigned> & G,modint env,bool totdeg,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< info_t > * f4buchberger_info,bool recomputeR){
    unsigned cleared=0;
    unsigned learned_position=0,f4buchberger_info_position=0;
    bool sugar=false,learning=pairs_reducing_to_zero && pairs_reducing_to_zero->empty();
    if (debug_infolevel>1000)
      res.dbgprint(); // instantiate dbgprint()
    polymod TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vector< pair<unsigned,unsigned> > B,BB;
    B.reserve(256); BB.reserve(256);
    vector<unsigned> smallposv;
    smallposv.reserve(256);
    info_t information;
    order_t order=res.front().order;
    if (order.o==_PLEX_ORDER) // if (order!=_REVLEX_ORDER && order!=_TDEG_ORDER)
      totdeg=false;
    vector<unsigned> oldG(G);
    for (unsigned l=0;l<ressize;++l){
#ifdef GIAC_REDUCEMODULO
      reducesmallmod(res[l],res,G,-1,env,TMP2,env);
#endif      
      gbasis_updatemod(G,B,res,l,TMP2,env,true,oldG);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      oldG=G;
      if (debug_infolevel>1)
	CERR << CLOCK() << " begin new iteration mod, " << env << " number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      if (1){
	// mem clear: remove res[i] if i is not in G nor in B
	vector<bool> clean(G.back()+1,true);
	for (unsigned i=0;i<G.size();++i){
	  clean[G[i]]=false;
	}
	for (unsigned i=0;i<B.size();++i){
	  clean[B[i].first]=false;
	  clean[B[i].second]=false;
	}
	for (unsigned i=0;i<clean.size();++i){
	  if (clean[i] && res[i].coord.capacity()>1){
		  cleared += unsigned(res[i].coord.capacity()) - 1;
	    polymod clearer;
	    clearer.coord.push_back(res[i].coord.front());
	    clearer.coord.swap(res[i].coord);
	  }
	}
      }
      // find smallest lcm pair in B
      tdeg_t small0,cur;
      unsigned smallpos,smalltotdeg=0,curtotdeg=0,smallsugar=0,cursugar=0;
      smallposv.clear();
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small0,order);
      smallsugar=res[B[smallpos].first].sugar+(small0-res[B[smallpos].first].coord.front().u).total_degree(order);
      smalltotdeg=small0.total_degree(order);
      smallposv.push_back(smallpos);
      for (unsigned i=smallpos+1;i<B.size();++i){
#ifdef TIMEOUT
	control_c();
#endif
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	bool doswap=false;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree(order);
	curtotdeg=cur.total_degree(order);
	if ( !totdeg || order.o==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small0,cur,order);
	else {
	  if (sugar){
	    if (smallsugar!=cursugar)
	      doswap = smallsugar > cursugar;
	  }
	  else {
	    if (smalltotdeg!=curtotdeg)
	      doswap = smalltotdeg > curtotdeg;	      
	  }
	}
	if (doswap){
	  smallsugar=cursugar;
	  smalltotdeg=curtotdeg;
	  // CERR << "swap mod " << curtotdeg << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small0,cur); // small=cur;
	  smallpos=i;
	  smallposv.clear();
	  smallposv.push_back(i);
	}
	else {
	  if (totdeg && curtotdeg==smalltotdeg && (!sugar || cursugar==smallsugar))
	    smallposv.push_back(i);
	}
      }
      if (smallposv.size()<=GBASIS_F4BUCHBERGER){
	unsigned i=smallposv[0];
	pair<unsigned,unsigned> bk=B[i];
	B.erase(B.begin()+i);
	if (!learning && pairs_reducing_to_zero && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	  if (debug_infolevel>2)
	    CERR << bk << " learned " << learned_position << endl;
	  ++learned_position;
	  continue;
	}
	if (debug_infolevel>2)
	  CERR << bk << " not learned " << learned_position << endl;
	if (debug_infolevel>2 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	  CERR << CLOCK() << " mod reducing pair with 1 element not in basis " << bk << endl;
	// polymod h(res.front().order,res.front().dim);
	spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
	if (debug_infolevel>1){
	  CERR << CLOCK() << " mod reduce begin, pair " << bk << " spoly size " << TMP1.coord.size() << " sugar degree " << TMP1.sugar << " totdeg deg " << TMP1.coord.front().u.total_degree(order) << " degree " << TMP1.coord.front().u << endl;
	}
#if 0 // def GBASIS_HEAP
	heap_reducemod(TMP1,res,G,-1,information.quo,TMP2,env);
	swap(TMP1.coord,TMP2.coord);
#else
	reducemod(TMP1,res,G,-1,TMP1,env);
#endif
	if (debug_infolevel>1){
	  if (debug_infolevel>3){ CERR << TMP1 << endl; }
	  CERR << CLOCK() << " mod reduce end, remainder size " << TMP1.coord.size() << " begin gbasis update" << endl;
	}
	if (!TMP1.coord.empty()){
	  increase(res);
	  if (ressize==res.size())
	    res.push_back(polymod(TMP1.order,TMP1.dim));
	  swap(res[ressize].coord,TMP1.coord);
	  ++ressize;
#if GBASIS_POSTF4BUCHBERGER==0
	  // this does not warrant full interreduced answer
	  // because at the final step we assume that each spoly
	  // is reduced by the previous spolys in res
	  // either by the first reduction or by inter-reduction
	  // here at most GBASIS_F4BUCHBERGER spolys may not be reduced by the previous ones
	  // it happens for example for cyclic8, element no 97
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,false,oldG);
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true,oldG);
#endif
	  if (debug_infolevel>3)
	    CERR << CLOCK() << " mod basis indexes " << G << " pairs indexes " << B << endl;
	}
	else {
	  if (learning && pairs_reducing_to_zero){
	    if (debug_infolevel>2)
	      CERR << "learning " << bk << endl;
	    pairs_reducing_to_zero->push_back(bk);
	  }
	}
	continue;
      }
      vector< pair<unsigned,unsigned> > smallposp;
      if (smallposv.size()==B.size()){
	swap(smallposp,B);
	B.clear();
      }
      else {
	for (unsigned i=0;i<smallposv.size();++i)
	  smallposp.push_back(B[smallposv[i]]);
	// remove pairs
	for (int i=int(smallposv.size())-1;i>=0;--i)
	  B.erase(B.begin()+smallposv[i]);
      }
      vectpolymod f4buchbergerv; // collect all spolys
      int f4res=-1;
      if (1 && env<(1<<30))
	f4res=f4mod(res,G,env,smallposp,f4buchbergerv,learning,learned_position,pairs_reducing_to_zero,f4buchberger_info,f4buchberger_info_position,recomputeR);
      else
	f4res=f4mod(res,G,env,smallposp,f4buchbergerv,learning,learned_position,pairs_reducing_to_zero,information,f4buchberger_info,f4buchberger_info_position,recomputeR,TMP1,TMP2);
      if (f4res==-1)
	return false;
      if (f4res==0)
	continue;
      // update gbasis and learning
      // requires that Gauss pivoting does the same permutation for other primes
      if (learning && pairs_reducing_to_zero){
	for (unsigned i=0;i<f4buchbergerv.size();++i){
	  if (f4buchbergerv[i].coord.empty()){
	    if (debug_infolevel>2)
	      CERR << "learning f4buchberger " << smallposp[i] << endl;
	    pairs_reducing_to_zero->push_back(smallposp[i]);
	  }
	}
      }
      unsigned added=0;
      for (unsigned i=0;i<f4buchbergerv.size();++i){
	if (!f4buchbergerv[i].coord.empty())
	  ++added;
      }
      if (debug_infolevel>1)
	CERR << CLOCK() << " reduce f4buchberger end on " << added << " from " << f4buchbergerv.size() << " pairs, gbasis update begin" << endl;
      for (unsigned i=0;i<f4buchbergerv.size();++i){
	if (!f4buchbergerv[i].coord.empty()){
	  increase(res);
	  if (ressize==res.size())
	    res.push_back(polymod(TMP1.order,TMP1.dim));
	  swap(res[ressize].coord,f4buchbergerv[i].coord);
	  ++ressize;
#ifdef GBASIS_POSTF4BUCHBERGER
#if GBASIS_POSTF4BUCHBERGER==0
	  if (learning || !f4buchberger_info || f4buchberger_info_position-1>=f4buchberger_info->size())
	    gbasis_updatemod(G,B,res,ressize-1,TMP2,env,false,oldG);
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,added<=GBASIS_F4BUCHBERGER,oldG);
#endif
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true,oldG);
#endif
	}
	else {
	  // if (!learning && pairs_reducing_to_zero)  CERR << " error learning "<< endl;
	}
      }
#if GBASIS_POSTF4BUCHBERGER==0
      if (!learning && f4buchberger_info && f4buchberger_info_position-1<f4buchberger_info->size()){
	B=(*f4buchberger_info)[f4buchberger_info_position-1].B;
	G=(*f4buchberger_info)[f4buchberger_info_position-1].G;
	continue;
      }
      if (learning && f4buchberger_info){
	f4buchberger_info->back().B=B;
	f4buchberger_info->back().G=G;
	f4buchberger_info->back().nonzero=added;
      }
#endif
	  unsigned debut = unsigned(G.size()) - added;
#if GBASIS_POSTF4BUCHBERGER>0
      if (added>GBASIS_F4BUCHBERGER){
	// final interreduce 
	vector<unsigned> G1(G.begin(),G.begin()+debut);
	vector<unsigned> G2(G.begin()+debut,G.end());
	vector<int> permu2;
	if (!learning && f4buchberger_info){
	  const info_t & info=(*f4buchberger_info)[f4buchberger_info_position-1];
	  rref_f4buchbergermod_nointerreduce(res,G1,res,G2,-1,info.quo2,info.R2,env,permu2);
	}
	else {
	  information.R2.order=TMP1.order;
	  information.R2.dim=TMP1.dim;
	  TMP1.coord.clear();
	  if (debug_infolevel>1)
	    CERR << CLOCK() << " collect monomials from old basis" << endl;
	  collect(res,G1,TMP1); // collect all monomials in res[G[0..debut-1]]
	  // in_heap_reducemod(TMP1,res,G2,-1,info_tmp.quo2,TMP2,&info_tmp.R2,env);
	  in_heap_reducemod(TMP1,res,G2,-1,information.quo2,TMP2,&information.R2,env);
	  rref_f4buchbergermod_nointerreduce(res,G1,res,G2,-1,information.quo2,information.R2,env,permu2);
	  if (f4buchberger_info){
	    info_t & i=f4buchberger_info->back();
	    swap(i.quo2,information.quo2);
	    swap(i.R2.coord,information.R2.coord);
	    i.R2.order=TMP1.order;
	    i.R2.dim=TMP1.dim;
	  }
	}
      }
#endif
      // CERR << "finish loop G.size "<<G.size() << endl;
      // CERR << added << endl;
    }
#if GBASIS_POSTF4BUCHBERGER==0
    // final interreduce step2
    for (unsigned j=0; j<G.size();++j){
      reducesmallmod(res[G[j]],res,G,j,env,TMP2,true);
    }
#endif
    if (ressize<res.size())
      res.resize(ressize);
    if (debug_infolevel>1){
      unsigned t=0;
      for (unsigned i=0;i<res.size();++i)
		  t += unsigned(res[i].coord.size());
      CERR << CLOCK() << " total number of monomials in res " << t << endl;
      CERR << "Number of monomials cleared " << cleared << endl;
    }
    // sort(res.begin(),res.end(),tripolymod);
    return true;
  }
#endif // GBASIS_F4BUCHBERGER

  bool in_gbasisf4buchbergermod(vectpoly8 & res8,vectpolymod &res,vector<unsigned> & G,modint env,bool totdeg,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< info_t > * f4buchberger_info,bool recomputeR){
    convert(res8,res,env);
	unsigned ressize = unsigned(res8.size());
    bool b=in_gbasisf4buchbergermod(res,ressize,G,env,totdeg,pairs_reducing_to_zero,f4buchberger_info,recomputeR);
    convert(res,res8,env);
    return b;
  }

  // set P mod p*q to be chinese remainder of P mod p and Q mod q
  bool chinrem(poly8 &P,const gen & pmod,poly8 & Q,const gen & qmod,poly8 & tmp){
    gen u,v,d,pqmod(pmod*qmod);
    egcd(pmod,qmod,u,v,d);
    if (u.type==_ZINT && qmod.type==_INT_)
      u=modulo(*u._ZINTptr,qmod.val);
    if (d==-1){ u=-u; v=-v; d=1; }
    if (d!=1)
      return false;
    int qmodval=0,U=0;
    mpz_t tmpz;
    mpz_init(tmpz);
    if (qmod.type==_INT_ && u.type==_INT_ && pmod.type==_ZINT){
      qmodval=qmod.val;
      U=u.val;
    }
    vector< T_unsigned<gen,tdeg_t> >::iterator it=P.coord.begin(),itend=P.coord.end(),jt=Q.coord.begin(),jtend=Q.coord.end();
    if (P.coord.size()==Q.coord.size()){
#ifndef USE_GMP_REPLACEMENTS
      if (qmodval){
	for (;it!=itend;++it,++jt){
	  if (it->u!=jt->u || jt->g.type!=_INT_)
	    break;
	}
	if (it==itend){
	  for (it=P.coord.begin(),jt=Q.coord.begin();it!=itend;++jt,++it){
	    if (it->g.type==_ZINT){
	      mpz_set_si(tmpz,jt->g.val);
	      mpz_sub(tmpz,tmpz,*it->g._ZINTptr);
	      mpz_mul_si(tmpz,*pmod._ZINTptr,(longlong(U)*modulo(tmpz,qmodval))%qmodval);
	      mpz_add(*it->g._ZINTptr,*it->g._ZINTptr,tmpz);
	    }
	    else {
	      mpz_mul_si(tmpz,*pmod._ZINTptr,(U*(longlong(jt->g.val)-it->g.val))%qmodval);
	      if (it->g.val>=0)
		mpz_add_ui(tmpz,tmpz,it->g.val);
	      else
		mpz_sub_ui(tmpz,tmpz,-it->g.val);
	      it->g=tmpz;
	    }
	  }
	  return true;
	}
	else {
	  if (debug_infolevel)
	    CERR << "warning chinrem: exponent mismatch " << it->u << "," << jt->u << endl;
	}
      }
#endif
    }
    else {
      if (debug_infolevel)
	CERR << "warning chinrem: sizes differ " << P.coord.size() << "," << Q.coord.size() << endl;
    }
    tmp.coord.clear(); tmp.dim=P.dim; tmp.order=P.order;
    tmp.coord.reserve(P.coord.size()+3); // allow 3 more terms in Q without realloc
    for (it=P.coord.begin(),jt=Q.coord.begin();it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	gen g;
#ifndef USE_GMP_REPLACEMENTS
	if (qmodval && jt->g.type==_INT_){
	  if (it->g.type==_ZINT){
	    mpz_set_si(tmpz,jt->g.val);
	    mpz_sub(tmpz,tmpz,*it->g._ZINTptr);
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(longlong(U)*modulo(tmpz,qmodval))%qmodval);
	    mpz_add(tmpz,tmpz,*it->g._ZINTptr);
	  }
	  else {
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(U*(longlong(jt->g.val)-it->g.val))%qmodval);
	    if (it->g.val>=0)
	      mpz_add_ui(tmpz,tmpz,it->g.val);
	    else
	      mpz_sub_ui(tmpz,tmpz,-it->g.val);
	  }
	  g=tmpz;
	}
	else
#endif
	  g=it->g+u*(jt->g-it->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
	++it; ++jt;
	continue;
      }
      if (tdeg_t_strictly_greater(it->u,jt->u,P.order)){
	if (debug_infolevel)
	  CERR << "chinrem: exponent mismatch using first " << endl;
	gen g=it->g-u*(it->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
	++it;
      }
      else {
	if (debug_infolevel)
	  CERR << "chinrem: exponent mismatch using second " << endl;
	gen g=u*(jt->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),jt->u));
	++jt;
      }
    }
    for (;it!=itend;++it){
      if (debug_infolevel)
	CERR << "chinrem: exponent mismatch at end using first " << endl;
      gen g=it->g-u*(it->g)*pmod;
      tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
    }
    for (;jt!=jtend;++jt){
      if (debug_infolevel)
	CERR << "chinrem: exponent mismatch at end using second " << endl;
      gen g=u*(jt->g)*pmod;
      tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),jt->u));
    }
    swap(P.coord,tmp.coord);
    mpz_clear(tmpz);
    return true;
  }

  // set P mod p*q to be chinese remainder of P mod p and Q mod q
  // P and Q must have same leading monomials,
  // otherwise returns 0 and leaves P unchanged
  int chinrem(vectpoly8 &P,const gen & pmod,vectpoly8 & Q,const gen & qmod,poly8 & tmp){
    if (P.size()!=Q.size())
      return 0;
    for (unsigned i=0;i<P.size();++i){
      if (P[i].coord.front().u!=Q[i].coord.front().u)
	return 0;
    }
    // LP(P)==LP(Q), proceed to chinese remaindering
    for (unsigned i=0;i<P.size();++i){
      if (!chinrem(P[i],pmod,Q[i],qmod,tmp))
	return -1;
    }
    return 1;
  }


  // set P mod p*q to be chinese remainder of P mod p and Q mod q
  bool chinrem(poly8 &P,const gen & pmod,const polymod & Q,int qmodval,poly8 & tmp){
    gen u,v,d,pqmod(qmodval*pmod);
    egcd(pmod,qmodval,u,v,d);
    if (u.type==_ZINT)
      u=modulo(*u._ZINTptr,qmodval);
    if (d==-1){ u=-u; v=-v; d=1; }
    if (d!=1)
      return false;
    int U=u.val;
    mpz_t tmpz;
    mpz_init(tmpz);
    vector< T_unsigned<gen,tdeg_t> >::iterator it=P.coord.begin(),itend=P.coord.end();
    vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=Q.coord.begin(),jtend=Q.coord.end();
#ifndef USE_GMP_REPLACEMENTS
    if (P.coord.size()==Q.coord.size()){
      for (;it!=itend;++it,++jt){
	if (it->u!=jt->u)
	  break;
      }
      if (it==itend){
	for (it=P.coord.begin(),jt=Q.coord.begin();it!=itend;++jt,++it){
	  if (pmod.type!=_ZINT){
	    it->g=it->g+u*(jt->g-it->g)*pmod;
	    continue;
	  }
	  if (it->g.type==_ZINT){
	    mpz_set_si(tmpz,jt->g);
	    mpz_sub(tmpz,tmpz,*it->g._ZINTptr);
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(longlong(U)*modulo(tmpz,qmodval))%qmodval);
	    mpz_add(*it->g._ZINTptr,*it->g._ZINTptr,tmpz);
	  }
	  else {
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(U*(longlong(jt->g)-it->g.val))%qmodval);
	    if (it->g.val>=0)
	      mpz_add_ui(tmpz,tmpz,it->g.val);
	    else
	      mpz_sub_ui(tmpz,tmpz,-it->g.val);
	    it->g=tmpz;
	  }
	}
	return true;
      }
    }
#endif
    tmp.coord.clear(); tmp.dim=P.dim; tmp.order=P.order;
    tmp.coord.reserve(P.coord.size()+3); // allow 3 more terms in Q without realloc
    for (it=P.coord.begin(),jt=Q.coord.begin();it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	gen g;
#ifndef USE_GMP_REPLACEMENTS
	if (pmod.type==_ZINT){
	  if (it->g.type==_ZINT){
	    mpz_set_si(tmpz,jt->g);
	    mpz_sub(tmpz,tmpz,*it->g._ZINTptr);
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(longlong(U)*modulo(tmpz,qmodval))%qmodval);
	    mpz_add(tmpz,tmpz,*it->g._ZINTptr);
	  }
	  else {
	    mpz_mul_si(tmpz,*pmod._ZINTptr,(U*(longlong(jt->g)-it->g.val))%qmodval);
	    if (it->g.val>=0)
	      mpz_add_ui(tmpz,tmpz,it->g.val);
	    else
	      mpz_sub_ui(tmpz,tmpz,-it->g.val);
	  }
	  g=tmpz;
	}
	else
#endif
	  g=it->g+u*(jt->g-it->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
	++it; ++jt;
	continue;
      }
      if (tdeg_t_strictly_greater(it->u,jt->u,P.order)){
	if (debug_infolevel)
	  CERR << "chinrem: exponent mismatch using first " << endl;
	gen g=it->g-u*(it->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
	++it;
      }
      else {
	if (debug_infolevel)
	  CERR << "chinrem: exponent mismatch using second " << endl;
	gen g=u*((jt->g)*pmod);
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),jt->u));
	++jt;
      }
    }
    for (;it!=itend;++it){
      if (debug_infolevel)
	CERR << "chinrem: exponent mismatch at end using first " << endl;
      gen g=it->g-u*(it->g)*pmod;
      tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
    }
    for (;jt!=jtend;++jt){
      if (debug_infolevel)
	CERR << "chinrem: exponent mismatch at end using second " << endl;
      gen g=u*((jt->g)*pmod);
      tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),jt->u));
    }
    swap(P.coord,tmp.coord);
    mpz_clear(tmpz);
    return true;
  }

  // set P mod p*q to be chinese remainder of P mod p and Q mod q
  // P and Q must have same leading monomials,
  // otherwise returns 0 and leaves P unchanged
  int chinrem(vectpoly8 &P,const gen & pmod,const vectpolymod & Q,int qmod,poly8 & tmp){
    if (P.size()!=Q.size())
      return 0;
    for (unsigned i=0;i<P.size();++i){
      if (P[i].coord.empty() && Q[i].coord.empty())
	continue;
      if (P[i].coord.empty())
	return 0;
      if (Q[i].coord.empty())
	return 0;
      if (P[i].coord.front().u!=Q[i].coord.front().u)
	return 0;
    }
    // LP(P)==LP(Q), proceed to chinese remaindering
    for (unsigned i=0;i<P.size();++i){
      if (!chinrem(P[i],pmod,Q[i],qmod,tmp))
	return -1;
    }
    return 1;
  }

  // a mod b = r/u with n and d<sqrt(b)/2
  // a*u = r mod b -> a*u+b*v=r, Bezout with a and b
  bool fracmod(int a,int b,int & n,int & d){
    if (a<0){
      if (!fracmod(-a,b,n,d))
	return false;
      n=-n;
      return true;
    }
    int r=b,u=0; // v=1
    int r1=a,u1=1,r2,u2,q; // v1=0
    for (;double(2*r1)*r1>b;){
      q=r/r1;
      u2=u-q*u1;
      r2=r-q*r1;
      u=u1;
      u1=u2;
      r=r1;
      r1=r2;
    }
    if (double(2*u1)*u1>b)
      return false;
    if (u1<0){ u1=-u1; r1=-r1; }
    n=r1; d=u1;
    return true;
  }

  // search for d such that d*P mod p has small coefficients
  // call with d set to 1,
  static bool findmultmod(const poly8 & P,int p,int & d){
    int n,s=int(P.coord.size());
    for (int i=0;i<s;++i){
      int a=smod(longlong(P.coord[i].g.val)*d,p);
      if (double(2*a)*a<p)
	continue;
      int d1=1;
      if (!fracmod(a,p,n,d1) || double(2*d1)*d1>p){
	if (debug_infolevel)
	  COUT << "findmultmod failure " << a << " mod " << p << endl;
	return false;
      }
      d=d*d1;
    }
    if (debug_infolevel){
      for (int i=0;i<s;++i){
	int a=smod(longlong(P.coord[i].g.val)*d,p);
	if (double(2*a)*a>=p){
	  COUT << "possible findmultmod failure " << P.coord[i].g.val << " " << d << " " << a << " " << p << endl;
	  //return false;
	}
      }
    }
    return true;
  }

  static bool fracmod(const poly8 &P,const gen & p,
	       mpz_t & d,mpz_t & d1,mpz_t & absd1,mpz_t &u,mpz_t & u1,mpz_t & ur,mpz_t & q,mpz_t & r,mpz_t &sqrtm,mpz_t & tmp,
	       poly8 & Q){
    Q.coord.clear();
    Q.coord.reserve(P.coord.size());
    Q.dim=P.dim;
    Q.order=P.order;
    Q.sugar=P.sugar;
    gen L=1; 
    bool tryL=true;
    for (unsigned i=0;i<P.coord.size();++i){
      gen g=P.coord[i].g,num,den;
      if (g.type==_INT_)
	g.uncoerce();
      if ( (g.type!=_ZINT) || (p.type!=_ZINT) ){
	CERR << "bad type"<<endl;
	return false;
      }
      if (tryL && L.type==_ZINT){
	num=smod(L*g,p);
	if (is_greater(p,4*num*num,context0)){
	  g=fraction(num,L);
	  Q.coord.push_back(T_unsigned<gen,tdeg_t>(g,P.coord[i].u));
	  continue;
	}
      }
      if (!in_fracmod(p,g,d,d1,absd1,u,u1,ur,q,r,sqrtm,tmp,num,den))
	return false;
      if (num.type==_ZINT && mpz_sizeinbase(*num._ZINTptr,2)<=30)
	num=int(mpz_get_si(*num._ZINTptr));
      if (den.type==_ZINT && mpz_sizeinbase(*den._ZINTptr,2)<=30)
	den=int(mpz_get_si(*den._ZINTptr));
      if (!is_positive(den,context0)){ // ok
	den=-den;
	num=-num;
      }
      g=fraction(num,den);
      if (tryL){
	L=lcm(L,den);
	tryL=is_greater(p,L*L,context0);
      }
      Q.coord.push_back(T_unsigned<gen,tdeg_t>(g,P.coord[i].u));
    }
    return true;
  }

  static bool fracmod(const vectpoly8 & P,const gen & p_,
		      mpz_t & d,mpz_t & d1,mpz_t & absd1,mpz_t &u,mpz_t & u1,mpz_t & ur,mpz_t & q,mpz_t & r,mpz_t &sqrtm,mpz_t & tmp,
		      vectpoly8 & Q){
    Q.resize(P.size());
    gen p=p_;
    if (p.type==_INT_)
      p.uncoerce();
    bool ok=true;
    for (unsigned i=0;i<P.size();++i){
      if (!fracmod(P[i],p,d,d1,absd1,u,u1,ur,q,r,sqrtm,tmp,Q[i])){
	ok=false;
	break;
      }
    }
    return ok;
  }

  void cleardeno(poly8 &P){
    gen g=1;
    for (unsigned i=0;i<P.coord.size();++i){
      if (P.coord[i].g.type==_FRAC)
	g=lcm(g,P.coord[i].g._FRACptr->den);
    }
    if (g!=1){
      for (unsigned i=0;i<P.coord.size();++i){
	P.coord[i].g=g*P.coord[i].g;
      }
    }
  }

  void cleardeno(vectpoly8 & P){
    for (unsigned i=0;i<P.size();++i){
      cleardeno(P[i]);
    }
  }


  void collect(const vectpoly8 & f4buchbergerv,polymod & allf4buchberger){
    vectpoly8::const_iterator it=f4buchbergerv.begin(),itend=f4buchbergerv.end();
    vector<heap_tt> H;
    H.reserve(itend-it);
    order_t keyorder={_REVLEX_ORDER,0};
    for (unsigned i=0;it!=itend;++i,++it){
      keyorder=it->order;
      if (!it->coord.empty())
	H.push_back(heap_tt(i,0,it->coord.front().u));
    }
    compare_heap_tt key(keyorder);
    make_heap(H.begin(),H.end(),key);
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end(),key);
      // push root node of the heap in allf4buchberger
      heap_tt & current =H.back();
      if (allf4buchberger.coord.empty() || allf4buchberger.coord.back().u!=current.u)
	allf4buchberger.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      if (current.polymodpos>=f4buchbergerv[current.f4buchbergervpos].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4buchbergerv[current.f4buchbergervpos].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end(),key);
    }
  }

  void makeline(const poly8 & p,const tdeg_t * shiftptr,const polymod & R,vecteur & v){
    v=vecteur(R.coord.size(),0);
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=R.coord.begin(),jtbeg=jt,jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  unsigned firstnonzero(const vecteur & v){
    for (unsigned i=0;i<v.size();++i){
      if (v[i]!=0)
	return i;
    }
	return unsigned(v.size());
  }

  struct sparse_gen {
    gen val;
    unsigned pos;
    sparse_gen(const gen & v,unsigned u):val(v),pos(u){};
    sparse_gen():val(0),pos(-1){};
  };

  void makeline(const poly8 & p,const tdeg_t * shiftptr,const polymod & R,vector<sparse_gen> & v){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=R.coord.begin(),jtend=R.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v.push_back(sparse_gen(it->g,int(jt-R.coord.begin())));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=it->u;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    v.push_back(sparse_gen(it->g,int(jt-R.coord.begin())));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

#ifdef __x86_64__
  bool checkreducef4buchberger_64(vector<modint> &v,vector<modint> & coeff,const vector< vector<sparse_element> > & M,modint env,vector<int128_t> & w){
    w.resize(v.size());
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    vector<int128_t>::iterator wt=w.begin();
    for (;vt!=vtend;++wt,++vt){
      *wt=*vt;
    }
    for (unsigned i=0;i<M.size();++i){
      const vector<sparse_element> & m=M[i];
      const sparse_element * it=&m.front(),*itend=it+m.size(),*it2;
      if (it==itend)
	continue;
      int128_t & ww=w[it->pos];
      if (ww==0){
	coeff[i]=0;
	continue;
      }
      modint c=coeff[i]=(modint2(invmod(it->val,env))*ww)%env;
      // CERR << "multiplier ok line " << i << " value " << c << " " << w << endl;
      if (!c)
	continue;
      ww=0;
      ++it;
      it2=itend-8;
      for (;it<=it2;){
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
	w[it->pos] -= modint2(c)*(it->val);
	++it;
      }
      for (;it!=itend;++it){
	w[it->pos] -= modint2(c)*(it->val);
      }
    }
    for (vt=v.begin(),wt=w.begin();vt!=vtend;++wt,++vt){
      if (*wt && (*wt % env))
	return false;
    }
    return true;
  }

  bool checkreducef4buchbergersplit_64(vector<modint> &v,vector<modint> & coeff,const vector< vector<shifttype> > & M,vector<vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env,vector<int128_t> & w){
    w.resize(v.size());
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    vector<int128_t>::iterator wt=w.begin(),wtend=w.end();
    for (;vt!=vtend;++wt,++vt){
      *wt=*vt;
    }
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end();
      if (jt==jtend)
	continue;
      const vector<shifttype> & mindex=M[i];
      const shifttype * it=&mindex.front();
      unsigned pos=0;
      next_index(pos,it);
      // if (pos>v.size()) CERR << "error" <<endl;
      modint c=coeff[i]=(modint2(invmod(*jt,env))*w[pos])%env;
      w[pos]=0;
      if (!c)
	continue;
      for (++jt;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	next_index(pos,it);
	int128_t &x=w[pos];
	x -= modint2(c)*(*jt);
#else
	w[*it] -= modint2(c)*(*jt);
	++it;
#endif
      }
    }
    for (wt=w.begin();wt!=wtend;++wt){
      if (*wt % env)
	return false;
    }
    return true;
  }

#endif

  // return true if v reduces to 0
  // in addition to reducef4buchberger, compute the coeffs
  bool checkreducef4buchberger(vector<modint> &v,vector<modint> & coeff,const vector< vector<sparse_element> > & M,modint env){
    for (unsigned i=0;i<M.size();++i){
      const vector<sparse_element> & m=M[i];
      vector<sparse_element>::const_iterator it=m.begin(),itend=m.end(),it1=itend-8;
      if (it==itend)
	continue;
      modint c=coeff[i]=v[it->pos];
      if (!c)
	continue;
      c=coeff[i]=(modint2(invmod(it->val,env))*c)%env;
      v[it->pos]=0;
      for (++it;it<it1;){
	modint *x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
	x=&v[it->pos];
	*x=(*x-modint2(c)*(it->val))%env;
	++it;
      }
      for (;it!=itend;++it){
	modint &x=v[it->pos];
	x=(x-modint2(c)*(it->val))%env;
      }
    }
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return false;
    }
    return true;
  }

  // return true if v reduces to 0
  // in addition to reducef4buchberger, compute the coeffs
  bool checkreducef4buchbergersplit(vector<modint> &v,vector<modint> & coeff,const vector< vector<shifttype> > & M,vector<vector<modint> > & coeffs,vector<coeffindex_t> & coeffindex,modint env){
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & mcoeff=coeffs[coeffindex[i].u];
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end();
      if (jt==jtend)
	continue;
      const vector<shifttype> & mindex=M[i];
      const shifttype * it=&mindex.front();
      unsigned pos=0;
      next_index(pos,it);
      // if (pos>v.size()) CERR << "error" <<endl;
      modint c=coeff[i]=(modint2(invmod(*jt,env))*v[pos])%env;
      v[pos]=0;
      if (!c)
	continue;
      for (++jt;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	next_index(pos,it);
	modint &x=v[pos];
#else
	modint &x=v[*it];
	++it;
#endif
	x=(x-modint2(c)*(*jt))%env;
      }
    }
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return false;
    }
    return true;
  }

  // Find x=a mod amod and =b mod bmod
  // We have x=a+A*amod=b+B*Bmod
  // hence A*amod-B*bmod=b-a
  // let u*amod+v*bmod=1
  // then A=(b-a)*u is a solution
  // hence x=a+(b-a)*u*amod mod (amod*bmod) is the solution
  // hence x=a+((b-a)*u mod bmod)*amod
  static bool ichinrem_inplace(matrice & a,const vector< vector<modint> > &b,const gen & amod, int bmod){
    gen U,v,d;
    egcd(amod,bmod,U,v,d);
    if (!is_one(d) || U.type!=_ZINT)
      return false;
    int u=mpz_get_si(*U._ZINTptr);
    longlong q;
    for (unsigned i=0;i<a.size();++i){
      gen * ai = &a[i]._VECTptr->front(), * aiend=ai+a[i]._VECTptr->size();
      const modint * bi = &b[i].front();
      for (;ai!=aiend;++bi,++ai){
	if (*bi==0 && ai->type==_INT_ && ai->val==0)
	  continue;
	q=longlong(*bi)-(ai->type==_INT_?ai->val:modulo(*ai->_ZINTptr,bmod));
	q=(q*u) % bmod;
	if (amod.type==_ZINT && ai->type==_ZINT){
	  if (q>=0)
	    mpz_addmul_ui(*ai->_ZINTptr,*amod._ZINTptr,int(q));
	  else
	    mpz_submul_ui(*ai->_ZINTptr,*amod._ZINTptr,-int(q));
	}
	else
	  *ai += int(q)*amod;
      }
    }
    return true;
  }

  gen linfnorm(const poly8 & p,GIAC_CONTEXT){
    gen B=0;
    for (unsigned i=0;i<p.coord.size();++i){
      gen b=abs(p.coord[i].g,contextptr);
      if (is_strictly_greater(b,B,contextptr))
	B=b;
    }
    return B;
  }

  gen linfnorm(const vectpoly8 & v,GIAC_CONTEXT){
    gen B=0;
    for (unsigned i=0;i<v.size();++i){
      gen b=linfnorm(v[i],contextptr);
      if (is_strictly_greater(b,B,contextptr))
	B=b;
    }
    return B;
  }

  bool chk_equal_mod(const gen & a,longlong p,int m){
    if (a.type==_FRAC){
      int n=a._FRACptr->num.type==_ZINT?modulo(*a._FRACptr->num._ZINTptr,m):a._FRACptr->num.val;
      int d=a._FRACptr->den.type==_ZINT?modulo(*a._FRACptr->den._ZINTptr,m):a._FRACptr->den.val;
      return (n-longlong(p)*d)%m==0;
    }
    if (a.type==_ZINT)
      return (modulo(*a._ZINTptr,m)-p)%m==0;
    if (a.type==_INT_)
      return (a.val-p)%m==0;
    CERR << "Unknow type in reconstruction " << a << endl;
    return false;
  }

  bool chk_equal_mod(const gen & a,const vector<int> & p,int m){
    if (a.type!=_VECT || a._VECTptr->size()!=p.size())
      return false;
    const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
    vector<int>::const_iterator jt=p.begin();
    for (;it!=itend;++jt,++it){
      if (it->type==_INT_ && it->val==*jt) continue;
      if (!chk_equal_mod(*it,*jt,m))
	return false;
    }
    return true;
  }

  bool chk_equal_mod(const vecteur & v,const vector< vector<int> >& p,int m){
    if (v.size()!=p.size())
      return false;
    for (unsigned i=0;i<p.size();++i){
      if (!chk_equal_mod(v[i],p[i],m))
	return false;
    }
    return true;
  }

  bool chk_equal_mod(const poly8 & v,const polymod & p,int m){
    // FIXME: sizes may differ if a coeff of v is 0 mod m
    if (v.coord.size()!=p.coord.size())
      return false;
    if (p.coord.empty())
      return true;
	unsigned s = unsigned(p.coord.size());
    int lc=smod(v.coord[0].g,m).val;
    int lcp=p.coord[0].g;
    if (lcp!=1){
      for (unsigned i=0;i<s;++i){
	if (!chk_equal_mod(lcp*v.coord[i].g,(longlong(lc)*p.coord[i].g)%m,m))
	  return false;
      }
    }
    else {
      for (unsigned i=0;i<s;++i){
	if (!chk_equal_mod(v.coord[i].g,(longlong(lc)*p.coord[i].g)%m,m))
	  return false;
      }
    }
    return true;
  }

  bool chk_equal_mod(const vectpoly8 & v,const vectpolymod & p,const vector<unsigned> & G,int m){
    if (v.size()!=G.size())
      return false;
    for (unsigned i=0;i<G.size();++i){
      if (!chk_equal_mod(v[i],p[G[i]],m))
	return false;
    }
    return true;
  }

  bool chk_equal_mod(const poly8 & v,const poly8 & p,int m){
    if (v.coord.size()!=p.coord.size())
      return false;
    unsigned s=unsigned(p.coord.size());
    int lc=smod(v.coord[0].g,m).val;
    for (unsigned i=0;i<s;++i){
      if (!chk_equal_mod(v.coord[i].g,(longlong(lc)*p.coord[i].g.val)%m,m))
	return false;
    }
    return true;
  }

  bool chk_equal_mod(const vectpoly8 & v,const vectpoly8 & p,const vector<unsigned> & G,int m){
    if (v.size()!=G.size())
      return false;
    for (unsigned i=0;i<G.size();++i){
      if (!chk_equal_mod(v[i],p[G[i]],m))
	return false;
    }
    return true;
  }

  // excluded==-1 and G==identity in calls
  // if eps>0 the check is probabilistic
  bool checkf4buchberger(vectpoly8 & f4buchbergerv,const vectpoly8 & res,vector<unsigned> & G,unsigned excluded,double eps){
    if (f4buchbergerv.empty())
      return true;
    polymod allf4buchberger(f4buchbergerv.front().order,f4buchbergerv.front().dim),rem(allf4buchberger);
    vectpolymod resmod,quo;
    convert(res,resmod,0);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger begin collect monomials on #polys " << f4buchbergerv.size() << endl;
    // collect all terms in f4buchbergerv
    collect(f4buchbergerv,allf4buchberger);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger symbolic preprocess" << endl;
    // find all monomials required to reduce allf4buchberger with res[G[.]]
    polymod R;
    in_heap_reducemod(allf4buchberger,resmod,G,excluded,quo,rem,&R,0);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger end symbolic preprocess" << endl;
    // build a matrix with rows res[G[.]]*quo[.] in terms of monomials in allf4buchberger
    // sort the matrix
    // checking reduction to 0 is equivalent to
    // write a line from f4buchbergerv[]
    // as a linear combination of the lines of this matrix
    // we will do that modulo a list of primes
    // and keep track of the coefficients of the linear combination (the quotients)
    // we reconstruct the quotients in Q by fracmod
    // once they stabilize, we compute the lcm l of the denominators
    // we multiply by l to have an equality on Z
    // we compute bounds on the coefficients of the products res*quo
    // and on l*f4buchbergerv, and we check further the equality modulo additional
    // primes until the equality is proved
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin build M" << endl;
    vector< vector<sparse_gen> > M;
    vector<sparse_element> atrier;
    unsigned N=unsigned(R.coord.size()),i,j=0,nterms=0;
    M.reserve(N); // actual size is at most N (difference is the remainder part size)
    for (i=0;i<res.size();++i){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vector<sparse_gen>(0));
	makeline(res[G[i]],&jt->u,R,M[j]);
	nterms += unsigned(M[j].size());
	atrier.push_back(sparse_element(M[j].front().pos,j));
      }
    }
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector< vector<sparse_gen> > M1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(M1[i],M[atrier[i].pos]);
    }
    swap(M,M1);
    // CERR << M << endl;
    if (debug_infolevel>0)
      CERR << CLOCK() << " rows, columns, terms: " << M.size() << "x" << N << "=" << nterms << endl; 
    // PSEUDO_MOD is not interesting here since there is no inter-reduction
    gen p(int(longlong(1<<31)-1));
    gen pip(1);
    vectpolymod f4buchbergervmod;
    matrice coeffmat;
    vector< vector<modint> > coeffmatmodp(f4buchbergerv.size(),vector<modint>(M.size()));
    gen bres=linfnorm(res,context0);
    gen bf4buchberger=linfnorm(f4buchbergerv,context0);
    matrice prevmatq;
    bool stable=false;
    gen bound=0;
    for (int iter=0;;++iter){
      if (eps>0 && is_greater(eps*pip,1,context0))
	return true;
      p=prevprime(p-1);
      int env=p.val;
      // check that p does not divide a leading monomial in M
      unsigned j;
      for (j=0;j<M.size();++j){
	if (smod(M[j].front().val,p)==0)
	  break;
      }
      if (j<M.size())
	continue;
      // compute M mod p
      vector< vector<sparse_element> > Mp(M.size());
      for (unsigned i=0;i<M.size();++i){
	const vector<sparse_gen> & Mi=M[i];
	vector<sparse_element> Ni;
	Ni.reserve(Mi.size());
	for (unsigned j=0;j<Mi.size();++j){
	  const sparse_gen & Mij=Mi[j];
	  modint tmp= Mij.val.type==_ZINT?modulo(*Mij.val._ZINTptr,env):Mij.val.val%env;
	  Ni.push_back(sparse_element(tmp,Mij.pos));
	}
	swap(Mp[i],Ni);
      }
      // reduce f4buchbergerv and stores coefficients of quotients for f4buchbergerv[i] in coeffmat[i]
      convert(f4buchbergerv,f4buchbergervmod,env);
      if (debug_infolevel>0)
	CERR << CLOCK() << " checking mod " << p << endl;
      vector<modint> v;
      unsigned countres=0;
#ifdef __x86_64__
      vector<int128_t> v128;
#endif
      for (unsigned i=0;i<f4buchbergervmod.size();++i){
	makeline(f4buchbergervmod[i],0,R,v);
#if 0 // def __x86_64__
	if (!checkreducef4buchberger_64(v,coeffmatmodp[i],Mp,env,v128))
	  return false;
#else
	if (!checkreducef4buchberger(v,coeffmatmodp[i],Mp,env))
	  return false;
#endif
	if (iter==0){
	  unsigned countrescur=0;
	  vector<modint> & coeffi=coeffmatmodp[i];
	  for (unsigned j=0;j<coeffi.size();++j){
	    if (coeffi[j])
	      ++countrescur;
	  }
	  if (countrescur>countres)
	    countres=countrescur;
	}
      }
      // if (iter==0) bound=pow(bres,int(countres),context0);
      if (stable){
	if (!chk_equal_mod(prevmatq,coeffmatmodp,env))
	  stable=false;
	// if stable compute bounds and compare with product of primes
	// if 2*bounds < product of primes recheck stabilization and return true
	if (is_strictly_greater(pip,bound,context0)){
	  if (debug_infolevel>0)
	    CERR << CLOCK() << " modular check finished " << endl;
	  return true;
	}
      }
      // combine coeffmat with previous one by chinese remaindering
      if (debug_infolevel>0)
	CERR << CLOCK() << " chinrem mod " << p << endl;
      if (iter)
	ichinrem_inplace(coeffmat,coeffmatmodp,pip,p.val);
      else
	vectvector_int2vecteur(coeffmatmodp,coeffmat);
      pip=pip*p;
      if (is_greater(bound,pip,context0))
	continue;
      if (!stable){
	// check stabilization 
	matrice checkquo;
	checkquo.reserve(coeffmat.size());
	for (unsigned k=0;k<coeffmat.size();++k){
	  if (prevmatq.size()>k && chk_equal_mod(prevmatq[k],coeffmatmodp[k],env))
	    checkquo.push_back(prevmatq[k]);
	  else
	    checkquo.push_back(fracmod(coeffmat[k],pip));
	  if (prevmatq.size()>k && checkquo[k]!=prevmatq[k])
	    break;
	  if (k>(prevmatq.size()*3)/2+2)
	    break;
	}
	if (checkquo!=prevmatq){
	  swap(prevmatq,checkquo);
	  if (debug_infolevel>0)
	    CERR << CLOCK() << " unstable mod " << p << " reconstructed " << prevmatq.size() << endl;
	  continue;
	}
	matrice coeffmatq=*_copy(checkquo,context0)._VECTptr;
	if (debug_infolevel>0)
	  CERR << CLOCK() << " full stable mod " << p << endl;
	stable=true;
	gen lall=1; vecteur l(coeffmatq.size());
	for (unsigned i=0;i<coeffmatq.size();++i){
	  lcmdeno(*coeffmatq[i]._VECTptr,l[i],context0);
	  if (is_strictly_greater(l[i],lall,context0))
	    lall=l[i];
	}
	if (debug_infolevel>0)
	  CERR << CLOCK() << " lcmdeno ok/start bound " << p << endl;
	gen ball=1,bi; // ball is the max bound of all coeff in coeffmatq
	for (unsigned i=0;i<coeffmatq.size();++i){
	  bi=linfnorm(coeffmatq[i],context0);
	  if (is_strictly_greater(bi,ball,context0))
	    ball=bi;
	}
	// bound for res and f4buchbergerv
	bound=bres*ball;
	gen bound2=lall*bf4buchberger;
	// lcm of deno and max of coeff
	if (is_strictly_greater(bound2,bound,context0))
	  bound=bound2;
      }
    }
    return true;
  }


  // excluded==-1 and G==identity in calls
  // if eps>0 the check is probabilistic
  bool checkf4buchbergersplit(vectpoly8 & f4buchbergerv,const vectpoly8 & res,vector<unsigned> & G,unsigned excluded,double eps){
    if (f4buchbergerv.empty())
      return true;
    polymod allf4buchberger(f4buchbergerv.front().order,f4buchbergerv.front().dim),rem(allf4buchberger);
    vectpolymod resmod,quo;
    convert(res,resmod,0);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger split begin collect monomials on #polys " << f4buchbergerv.size() << endl;
    // collect all terms in f4buchbergerv
    collect(f4buchbergerv,allf4buchberger);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger split symbolic preprocess" << endl;
    // find all monomials required to reduce allf4buchberger with res[G[.]]
    polymod R;
    in_heap_reducemod(allf4buchberger,resmod,G,excluded,quo,rem,&R,0);
    if (debug_infolevel>1)
      CERR << CLOCK() << " checkf4buchberger split end symbolic preprocess" << endl;
    // build a matrix with rows res[G[.]]*quo[.] in terms of monomials in allf4buchberger
    // sort the matrix
    // checking reduction to 0 is equivalent to
    // write a line from f4buchbergerv[]
    // as a linear combination of the lines of this matrix
    // we will do that modulo a list of primes
    // and keep track of the coefficients of the linear combination (the quotients)
    // we reconstruct the quotients in Q by fracmod
    // once they stabilize, we compute the lcm l of the denominators
    // we multiply by l to have an equality on Z
    // we compute bounds on the coefficients of the products res*quo
    // and on l*f4buchbergerv, and we check further the equality modulo additional
    // primes until the equality is proved
    if (debug_infolevel>1)
      CERR << CLOCK() << " begin build Mcoeff/Mindex" << endl;
    vector< vector<gen> > Mcoeff(G.size());
    vector<vector<shifttype> > Mindex;
    vector<coeffindex_t> coeffindex;
    vector<sparse_element> atrier;
    unsigned N=unsigned(R.coord.size()),i,j=0,nterms=0;
    Mindex.reserve(N);
    atrier.reserve(N);
    coeffindex.reserve(N);
    for (i=0;i<G.size();++i){
      Mcoeff[i].reserve(res[G[i]].coord.size());
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	Mindex.push_back(vector<shifttype>(0));
#ifdef GIAC_SHORTSHIFTTYPE
	Mindex[j].reserve(1+int(1.1*res[G[i]].coord.size()));
#else
	Mindex[j].reserve(res[G[i]].coord.size());
#endif
      }
    }
    for (i=0,j=0;i<G.size();++i){
      // copy coeffs of res[G[i]] in Mcoeff
      copycoeff(res[G[i]],Mcoeff[i]);
      // for each monomial of quo[i], find indexes and put in Mindex
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	coeffindex.push_back(coeffindex_t(N<0xffff,i));
	makelinesplit(res[G[i]],&jt->u,R,Mindex[j]);
	atrier.push_back(sparse_element(first_index(Mindex[j]),j));
      }
    }
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector< vector<shifttype> > Mindex1(atrier.size());
    vector<coeffindex_t> coeffindex1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(Mindex1[i],Mindex[atrier[i].pos]);
      swap(coeffindex1[i],coeffindex[atrier[i].pos]);
    }
    swap(Mindex,Mindex1);
    swap(coeffindex,coeffindex1);
    // CERR << M << endl;
    if (debug_infolevel>0)
      CERR << CLOCK() << " rows, columns, terms: " << Mindex.size() << "x" << N << "=" << nterms << endl; 
    // PSEUDO_MOD is not interesting here since there is no inter-reduction
    gen p(int(longlong(1<<31)-1));
    gen pip(1);
    vectpolymod f4buchbergervmod;
    matrice coeffmat;
    vector< vector<modint> > coeffmatmodp(f4buchbergerv.size(),vector<modint>(Mindex.size()));
    gen bres=linfnorm(res,context0);
    gen bf4buchberger=linfnorm(f4buchbergerv,context0);
    matrice prevmatq;
    bool stable=false;
    gen bound=0;
    vector< vector<modint> > Mcoeffp(Mcoeff.size());
    for (int iter=0;;++iter){
      if (eps>0 && is_greater(eps*pip,1,context0))
	return true;
      p=prevprime(p-1);
      int env=p.val;
      // check that p does not divide a leading monomial in M
      unsigned j;
      for (j=0;j<Mcoeff.size();++j){
	if (smod(Mcoeff[j].front(),p)==0)
	  break;
      }
      if (j<Mcoeff.size())
	continue;
      // compute Mcoeff mod p
      for (unsigned i=0;i<Mcoeff.size();++i){
	const vector<gen> & Mi=Mcoeff[i];
	vector<modint> & Ni=Mcoeffp[i];
	Ni.clear();
	Ni.reserve(Mi.size());
	for (unsigned j=0;j<Mi.size();++j){
	  const gen & Mij=Mi[j];
	  modint tmp= Mij.type==_ZINT?modulo(*Mij._ZINTptr,env):Mij.val%env;
	  Ni.push_back(tmp);
	}
      }
      // reduce f4buchbergerv and stores coefficients of quotients for f4buchbergerv[i] in coeffmat[i]
      convert(f4buchbergerv,f4buchbergervmod,env);
      if (debug_infolevel>0)
	CERR << CLOCK() << " checking mod " << p << endl;
      vector<modint> v;
      unsigned countres=0;
#ifdef __x86_64__
      vector<int128_t> v128;
#endif
      for (unsigned i=0;i<f4buchbergervmod.size();++i){
	makeline(f4buchbergervmod[i],0,R,v);
#ifdef __x86_64__
	if (!checkreducef4buchbergersplit_64(v,coeffmatmodp[i],Mindex,Mcoeffp,coeffindex,env,v128))
	  return false;
#else
	if (!checkreducef4buchbergersplit(v,coeffmatmodp[i],Mindex,Mcoeffp,coeffindex,env))
	  return false;
#endif
	if (iter==0){
	  unsigned countrescur=0;
	  vector<modint> & coeffi=coeffmatmodp[i];
	  for (unsigned j=0;j<coeffi.size();++j){
	    if (coeffi[j])
	      ++countrescur;
	  }
	  if (countrescur>countres)
	    countres=countrescur;
	}
      }
      // if (iter==0) bound=pow(bres,int(countres),context0);
      if (stable){
	if (!chk_equal_mod(prevmatq,coeffmatmodp,env))
	  stable=false;
	// if stable compute bounds and compare with product of primes
	// if 2*bounds < product of primes recheck stabilization and return true
	if (is_strictly_greater(pip,bound,context0)){
	  if (debug_infolevel>0)
	    CERR << CLOCK() << " modular check finished " << endl;
	  return true;
	}
      }
      // combine coeffmat with previous one by chinese remaindering
      if (debug_infolevel>0)
	CERR << CLOCK() << " chinrem mod " << p << endl;
      if (iter)
	ichinrem_inplace(coeffmat,coeffmatmodp,pip,p.val);
      else
	vectvector_int2vecteur(coeffmatmodp,coeffmat);
      pip=pip*p;
      if (is_greater(bound,pip,context0))
	continue;
      if (!stable){
	// check stabilization 
	matrice checkquo;
	checkquo.reserve(coeffmat.size());
	for (unsigned k=0;k<coeffmat.size();++k){
	  if (prevmatq.size()>k && chk_equal_mod(prevmatq[k],coeffmatmodp[k],env))
	    checkquo.push_back(prevmatq[k]);
	  else
	    checkquo.push_back(fracmod(coeffmat[k],pip));
	  if (prevmatq.size()>k && checkquo[k]!=prevmatq[k])
	    break;
	  if (k>(prevmatq.size()*3)/2+2)
	    break;
	}
	if (checkquo!=prevmatq){
	  swap(prevmatq,checkquo);
	  if (debug_infolevel>0)
	    CERR << CLOCK() << " unstable mod " << p << " reconstructed " << prevmatq.size() << endl;
	  continue;
	}
	matrice coeffmatq=*_copy(checkquo,context0)._VECTptr;
	if (debug_infolevel>0)
	  CERR << CLOCK() << " full stable mod " << p << endl;
	stable=true;
	gen lall=1; vecteur l(coeffmatq.size());
	for (unsigned i=0;i<coeffmatq.size();++i){
	  lcmdeno(*coeffmatq[i]._VECTptr,l[i],context0);
	  if (is_strictly_greater(l[i],lall,context0))
	    lall=l[i];
	}
	if (debug_infolevel>0)
	  CERR << CLOCK() << " lcmdeno ok/start bound " << p << endl;
	gen ball=1,bi; // ball is the max bound of all coeff in coeffmatq
	for (unsigned i=0;i<coeffmatq.size();++i){
	  bi=linfnorm(coeffmatq[i],context0);
	  if (is_strictly_greater(bi,ball,context0))
	    ball=bi;
	}
	// bound for res and f4buchbergerv
	bound=bres*ball;
	gen bound2=lall*bf4buchberger;
	// lcm of deno and max of coeff
	if (is_strictly_greater(bound2,bound,context0))
	  bound=bound2;
      }
    }
    return true;
  }


  bool is_gbasis(const vectpoly8 & res,double eps,bool modularcheck){
    if (res.empty())
      return false;
    if (debug_infolevel>0)
      CERR << "basis size " << res.size() << endl;
    // build possible pairs (i,j) with i<j
    vector< vector<tdeg_t> > lcmpairs(res.size());
    vector<unsigned> G(res.size());
    for (unsigned i=0;i<res.size();++i)
      G[i]=i;
    vectpoly8 vtmp,tocheck;
    vector< pair<unsigned,unsigned> > tocheckpairs;
    if (eps>0 && eps<2e-9)
      modularcheck=true;
    if (modularcheck)
      tocheck.reserve(res.size()*10); // wild guess
    else
      tocheckpairs.reserve(res.size()*10);
    order_t order=res.front().order;
    int dim=res.front().dim;
    poly8 TMP1(order,res.front().dim),TMP2(TMP1),
      spol(TMP1),spolred(TMP1);
    polymod spolmod(order,dim),TMP1mod(order,dim);
    vectpolymod resmod;
    for (unsigned i=0;i<res.size();++i){
      const poly8 & h = res[i];
      const tdeg_t & h0=h.coord.front().u;
      vector<tdeg_t> tmp(res.size());
      for (unsigned j=i+1;j<res.size();++j){
	index_lcm(h0,res[j].coord.front().u,tmp[j],h.order); 
      }
      swap(lcmpairs[i],tmp);
    }
    for (unsigned i=0;i<res.size();++i){    
      if (debug_infolevel>1)
	CERR << "checking pairs for i="<<i<<", j=";
      const poly8 & resi = res[i];
      const tdeg_t & resi0=resi.coord.front().u;
      for (unsigned j=i+1;j<res.size();++j){
	if (disjoint(resi0,res[j].coord.front().u,order,dim))
	  continue;
	// criterion M, F
	unsigned J=0;
	tdeg_t & lcmij=lcmpairs[i][j];
	for (;J<i;++J){
	  if (tdeg_t_all_greater(lcmij,lcmpairs[J][j],order))
	    break;
	}
	if (J<i)
	  continue; 
	for (++J;J<j;++J){
	  tdeg_t & lcmJj=lcmpairs[J][j];
	  if (tdeg_t_all_greater(lcmij,lcmJj,order) && lcmij!=lcmJj)
	    break;
	}
	if (J<j)
	  continue; 
	// last criterion
	unsigned k;
	for (k=j+1;k<res.size();++k){
	  if (lcmpairs[i][k]!=lcmij && lcmpairs[j][k]!=lcmij
	      && tdeg_t_all_greater(lcmij,res[k].coord.front().u,order))
	    break;
	}
	if (k<res.size())
	  continue;
	// compute and reduce s-poly
	if (debug_infolevel>1)
	  CERR <<  j << ",";
	if (modularcheck){
	  spoly(resi,res[j],spol,TMP1,0);
	  tocheck.push_back(poly8(order,dim));
	  swap(tocheck.back(),spol);
	}
	else
	  tocheckpairs.push_back(pair<unsigned,unsigned>(i,j));
      } // end j loop
      if (debug_infolevel>1)
	CERR << endl;
    }
    if (debug_infolevel>0)
      CERR << "Number of critical pairs to check " << (modularcheck?tocheck.size():tocheckpairs.size()) << endl;
    if (modularcheck) // modular check is sometimes slow
      return checkf4buchberger(tocheck,res,G,-1,eps); // split version is slower!
    // integer check or modular check for one modulus (!= from first prime already used)
    modint p=(prevprime((1<<29)-30000000)).val;
    if (eps>0)
      convert(res,resmod,p);
    for (unsigned i=0;i<tocheckpairs.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c){
	CERR << "Check interrupted, assuming Groebner basis. Press Ctrl-C again to interrupt computation" << endl;
	interrupted=ctrl_c=false;
	return true;
      }
      if (eps>0){
	spolymod(resmod[tocheckpairs[i].first],resmod[tocheckpairs[i].second],spolmod,TMP1mod,p);
	reducemod(spolmod,resmod,G,-1,TMP1mod,p);
	// gen den; heap_reduce(spol,res,G,-1,vtmp,spolred,TMP1,den,0);
	if (!TMP1mod.coord.empty())
	  return false;
      }
      else {
	spoly(res[tocheckpairs[i].first],res[tocheckpairs[i].second],spol,TMP1,0);
	reduce(spol,res,G,-1,vtmp,spolred,TMP1,TMP2,0);
	// gen den; heap_reduce(spol,res,G,-1,vtmp,spolred,TMP1,den,0);
	if (!spolred.coord.empty())
	  return false;
      }
      if (debug_infolevel>0){
	CERR << "+";
	if (i%512==511)
	  CERR << tocheckpairs.size()-i << " remaining" << endl; 
      }
    }
    if (debug_infolevel)
      CERR << endl << "Successfull check of " << tocheckpairs.size() << " critical pairs" << endl;
    return true;
  }

  /* ***************
     BEGIN ZPOLYMOD
     ***************  */
#if GIAC_SHORTSHIFTTYPE==16
  // Same algorithms compressing data
  // since all spolys reduced at the same time share the same exponents
  // we will keep the exponents only once in memory
  // sizeof(zmodint)=8 bytes, sizeof(T_unsigned<modint,tdeg_t>)=28 or 36
  typedef T_unsigned<modint,unsigned> zmodint;
  struct zpolymod {
    order_t order;
    short int dim;
    vector<zmodint> coord;
    const vector<tdeg_t> * expo;
    tdeg_t ldeg;
    zpolymod():dim(0),expo(0),ldeg() {order.o=0; order.lex=0; order.dim=0;}
    zpolymod(order_t o,int d): dim(d),expo(0),ldeg() {order=o; order.dim=d;}
    zpolymod(order_t o,int d,const tdeg_t & l): dim(d),expo(0),ldeg(l) {order=o; order.dim=d;}
    zpolymod(order_t o,int d,const vector<tdeg_t> * e,const tdeg_t & l): dim(d),expo(e),ldeg(l) {order=o; order.dim=d;}
    void dbgprint() const;
  };

  struct zinfo_t {
    vector< vector<tdeg_t> > quo;
    vector<tdeg_t> R,rem;
    vector<int> permu;
    vector< pair<unsigned,unsigned> > B;
    vector<unsigned> G;
    unsigned nonzero;
  };

  void zsmallmultmod(modint a,zpolymod & p,modint m){
    if (a==1 || a==1-m)
      return;
    std::vector< zmodint >::iterator pt=p.coord.begin(),ptend=p.coord.end();
    for (;pt!=ptend;++pt){
      pt->g=(longlong(pt->g)*a)%m;
    }
  }

  bool operator == (const zpolymod & p,const zpolymod &q){
    if (p.coord.size()!=q.coord.size() || p.expo!=q.expo)
      return false;
    for (unsigned i=0;i<p.coord.size();++i){
      if (p.coord[i].u!=q.coord[i].u || p.coord[i].g!=q.coord[i].g)
	return false;
    }
    return true;
  }

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os, const zpolymod & p)
#else
  ostream & operator << (ostream & os, const zpolymod & p)
#endif
  {
    if (!p.expo)
      return os << "error, null pointer in expo " ;
    std::vector<zmodint>::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
#ifdef GIAC_64VARS
      if ((*p.expo)[it->u].tdeg%2){
	degtype * i=(degtype *)((*p.expo)[it->u].ui+1);
	for (int j=0;j<(*p.expo)[it->u].order_.dim;++j){
	  t2=i[j];
	  if (t2)
	    os << "*x"<< j << "^" << t2  ;
	}
	++it;
	if (it==itend)
	  break;
	os << " + ";
	continue;
      }
#endif
      short tab[GROEBNER_VARS+1];
      (*p.expo)[it->u].get_tab(tab);
      switch (p.order.o){
      case _PLEX_ORDER:
	for (int i=0;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2)
	    os << "*x"<< i << "^" << t2  ;
	}
	break;
      case _TDEG_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  if (t2)
	    os << "*x"<< i-1 << "^" << t2  ;
	}
	break;
      case _REVLEX_ORDER:
	for (int i=1;i<=GROEBNER_VARS;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;
#if GROEBNER_VARS==15
      case _3VAR_ORDER:
	for (int i=1;i<=3;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 3-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=5;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 4+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _7VAR_ORDER:
	for (int i=1;i<=7;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 7-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=9;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 8+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
      case _11VAR_ORDER:
	for (int i=1;i<=11;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 11-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	for (int i=13;i<=15;++i){
	  t2 = tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< 12+p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;	
#endif
      }
      ++it;
      if (it==itend)
	break;
      os << " + ";
    }
    return os;
  }

  void zpolymod::dbgprint() const { 
    CERR << *this << endl;
  }

  class vectzpolymod:public vector<zpolymod>{
  public:
    void dbgprint() const { CERR << *this << endl; }
  };

  void zleftright(const vectzpolymod & res,vector< pair<unsigned,unsigned> > & B,vector<tdeg_t> & leftshift,vector<tdeg_t> & rightshift){
    for (unsigned i=0;i<B.size();++i){
      const zpolymod & p=res[B[i].first];
      const zpolymod & q=res[B[i].second];
      if (debug_infolevel>2)
	CERR << "zleftright " << p << "," << q << endl;
      tdeg_t l;
      index_lcm(p.ldeg,q.ldeg,l,p.order);
      leftshift[i]=l-p.ldeg;
      rightshift[i]=l-q.ldeg;
    }
  }

  // collect monomials from pairs of res (vector of polymods), shifted by lcm
  // does not collect leading monomial (since they cancel)
  void zcollect(const vectzpolymod & res,vector< pair<unsigned,unsigned> > & B,vector<tdeg_t> & allf4buchberger,vector<tdeg_t> & leftshift,vector<tdeg_t> & rightshift){
    int start=1;
    vector<heap_tt> Ht;
    vector<heap_tt_ptr> H; 
    Ht.reserve(2*B.size());
    H.reserve(2*B.size());
    unsigned s=0;
    order_t keyorder={_REVLEX_ORDER,0};
    for (unsigned i=0;i<B.size();++i){
      const zpolymod & p=res[B[i].first];
      const zpolymod & q=res[B[i].second];
      keyorder=p.order;
      if (int(p.coord.size())>start){
		  s = giacmax(s, unsigned(p.coord.size()));
	Ht.push_back(heap_tt(true,i,start,(*p.expo)[p.coord[start].u]+leftshift[i]));
	H.push_back(heap_tt_ptr(&Ht.back()));
      }
      if (int(q.coord.size())>start){
		  s = giacmax(s, unsigned(q.coord.size()));
	Ht.push_back(heap_tt(false,i,start,(*q.expo)[q.coord[start].u]+rightshift[i]));
	H.push_back(heap_tt_ptr(&Ht.back()));
      }
    }
    allf4buchberger.reserve(s); // int(s*std::log(1+H.size())));
    compare_heap_tt_ptr key(keyorder);
    make_heap(H.begin(),H.end(),key);
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end(),key);
      // push root node of the heap in allf4buchberger
      heap_tt & current = *H.back().ptr;
      if (allf4buchberger.empty() || allf4buchberger.back()!=current.u)
	allf4buchberger.push_back(current.u);
      ++current.polymodpos;
      unsigned vpos;
      if (current.left)
	vpos=B[current.f4buchbergervpos].first;
      else
	vpos=B[current.f4buchbergervpos].second;
      if (current.polymodpos>=res[vpos].coord.size()){
	H.pop_back();
	continue;
      }
      const zpolymod & resvpos=res[vpos];
      if (current.left)
	current.u=(*resvpos.expo)[resvpos.coord[current.polymodpos].u]+leftshift[current.f4buchbergervpos];
      else 
	current.u=(*resvpos.expo)[resvpos.coord[current.polymodpos].u]+rightshift[current.f4buchbergervpos];
      std::push_heap(H.begin(),H.end(),key);
    }
  }

#if 0
  struct zsymb_data {
    unsigned pos;
    unsigned tdeg;
    unsigned terms;
  };
  
  bool operator < (const zsymb_data & z1,const zsymb_data & z2){
    if (z1.tdeg>z2.tdeg && z1.terms<=z2.terms)
      return true;
    if (z1.tdeg<z2.tdeg && z1.terms>=z2.terms)
      return false;
    return z1.pos<z2.pos;
  }
#endif

  void zsymbolic_preprocess(const vector<tdeg_t> & f,const vectzpolymod & g,const vector<unsigned> & G,unsigned excluded,vector< vector<tdeg_t> > & q,vector<tdeg_t> & rem,vector<tdeg_t> & R){
    // divides f by g[G[0]] to g[G[G.size()-1]] except maybe g[G[excluded]]
    // CERR << f << "/" << g << endl;
    // first implementation: use quotient heap for all quotient/divisor
    // do not use heap chain
    // ref Monaghan Pearce if g.size()==1
    // R is the list of all monomials
#if 0
    // First reorder G in order to use the "best" possible reductor
    // This is done using the total degree of g[G[i]] (should be maximal)
    // and the number of terms (should be minimal)
    vector<zsymb_data> GG(G.size());
    for (unsigned i=0;i<G.size();++i){
      zsymb_data zz={i,g[G[i]].ldeg.total_degree(order),g[G[i]].coord.size()};
      GG[i]=zz;
    }
    sort(GG.begin(),GG.end());
#endif
    R.clear();
    rem.clear();
    q.resize(G.size());
    unsigned guess=0;
    for (unsigned i=0;i<G.size();++i){
      q[i].clear();
	  guess += unsigned(g[G[i]].coord.size());
    }
    if (f.empty() || G.empty())
      return ;
    int dim=g[G.front()].dim;
    order_t order=g[G.front()].order;
    vector<heap_t> H_;
    vector<unsigned> H;
    H_.reserve(guess);
    H.reserve(guess);
    heap_t_compare key(H_,order);
    unsigned k=0,i; // k=position in f
    tdeg_t m;
    bool finish=false;
    while (!H.empty() || k<f.size()){
      // is highest remaining degree in f or heap?
      if (k<f.size() && (H.empty() || tdeg_t_greater(f[k],H_[H.front()].u,order)) ){
	// it's in f or both
	m=f[k];
	++k;
      }
      else {
	m=H_[H.front()].u;
      }
      R.push_back(m);
      // extract from heap all terms having m as monomials, substract from c
      while (!H.empty() && H_[H.front()].u==m){
	std::pop_heap(H.begin(),H.end(),key);
	heap_t & current=H_[H.back()]; // was root node of the heap
	const zpolymod & gcurrent = g[G[current.i]];
	if (current.gj<gcurrent.coord.size()-1){
	  ++current.gj;
	  current.u=q[current.i][current.qi]+(*gcurrent.expo)[gcurrent.coord[current.gj].u];
	  std::push_heap(H.begin(),H.end(),key);
	}
	else
	  H.pop_back();
      }
      // divide (c,m) by one of the g if possible, otherwise push in remainder
      if (finish){
	rem.push_back(m); // add to remainder
	continue;
      }
      finish=true;
      unsigned ii;
      for (ii=0;ii<G.size();++ii){
	i=ii; 
	//i=GG[ii].pos; // we can use any permutation of 0..G.size()-1 here
	if (i==excluded)
	  continue;
	const tdeg_t & deg=g[G[i]].ldeg;
	if (tdeg_t_greater(m,deg,order)){
	  finish=false;
	  if (tdeg_t_all_greater(m,deg,order))
	    break;
	}
      }
      if (ii==G.size()){
	rem.push_back(m); // add to remainder
	continue;
      }
      // add m/leading monomial of g[G[i]] to q[i]
      const zpolymod & gGi=g[G[i]];
      tdeg_t monom=m-gGi.ldeg;
      q[i].push_back(monom);
      // CERR << i << " " << q[i] << endl;
      // push in heap
      if (gGi.coord.size()>1){
		  heap_t current = { i, unsigned(q[i].size()) - 1, 1, (*gGi.expo)[gGi.coord[1].u] + monom };
	H.push_back(hashgcd_U(H_.size()));
	H_.push_back(current);
	key.ptr=&H_.front();
	std::push_heap(H.begin(),H.end(),key);
      }
    } // end main heap pseudo-division loop
    // CERR << H_.size() << endl;
  }

  void zcopycoeff(const zpolymod & p,vector<modint> & v){
    std::vector< zmodint >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    v.clear();
    v.reserve(itend-it);
    for (;it!=itend;++it)
      v.push_back(it->g);
  }

  // dichotomic seach for jt->u==u in [jt,jtend[
  bool dicho(std::vector<tdeg_t>::const_iterator & jt,std::vector<tdeg_t>::const_iterator jtend,const tdeg_t & u,order_t order){
    if (*jt==u) return true;
    for (;;){
      std::vector<tdeg_t>::const_iterator j=jt+(jtend-jt)/2;
      if (j==jt)
	return *j==u;
      if (int res=tdeg_t_greater(*j,u,order)){
	jt=j;
	if (res==2)
	  return true;
      }
      else
	jtend=j;
    }
  }

  void zmakelinesplit(const zpolymod & p,const tdeg_t * shiftptr,const vector<tdeg_t> & R,vector<shifttype> & v){
    std::vector<zmodint>::const_iterator it=p.coord.begin(),itend=p.coord.end();
    std::vector<tdeg_t>::const_iterator jt=R.begin(),jtend=R.end();
    double nop1=R.size(); 
    double nop2=4*p.coord.size()*std::log(nop1)/std::log(2.0);
    bool dodicho=nop2<nop1;
    const vector<tdeg_t> & expo=*p.expo;
    unsigned pos=0;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=expo[it->u]+*shiftptr;
#if 1
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  pushsplit(v,pos,unsigned(jt-R.begin()));
	  ++jt;
	  continue;
	}
#endif
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    pushsplit(v,pos,int(jt-R.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=expo[it->u];
#if 1
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  pushsplit(v,pos,unsigned(jt-R.begin()));
	  ++jt;
	  continue;
	}
#endif
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    pushsplit(v,pos,int(jt-R.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }


  void zmakeline(const zpolymod & p,const tdeg_t * shiftptr,const vector<tdeg_t> & R,vector<modint> & v,int start=0){
    v.resize(R.size()); 
    v.assign(R.size(),0);
    std::vector<zmodint>::const_iterator it=p.coord.begin()+start,itend=p.coord.end();
    std::vector<tdeg_t>::const_iterator jt=R.begin(),jtbeg=jt,jtend=R.end();
    double nop1=R.size(); 
    double nop2=4*p.coord.size()*std::log(nop1)/std::log(2.0);
    bool dodicho=nop2<nop1;
    const std::vector<tdeg_t> & expo=*p.expo;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=expo[it->u]+*shiftptr;
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  v[jt-jtbeg]=it->g;
	  ++jt;
	  continue;
	}	
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=expo[it->u];
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  v[jt-jtbeg]=it->g;
	  ++jt;
	  continue;
	}	
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    v[jt-jtbeg]=it->g;
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  // dicho does not work here?
  void zmakelinesub(const zpolymod & p,const tdeg_t * shiftptr,const vector<tdeg_t> & R,vector<modint> & v,int start,modint env){
    std::vector< zmodint >::const_iterator it=p.coord.begin()+start,itend=p.coord.end();
    std::vector<tdeg_t>::const_iterator jt=R.begin(),jtbeg=jt,jtend=R.end();
    const std::vector<tdeg_t> & expo=*p.expo;
    double nop1=R.size(); 
    double nop2=4*p.coord.size()*std::log(nop1)/std::log(2.0);
    bool dodicho=nop2<nop1;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=expo[it->u]+*shiftptr;
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  // v[jt-jtbeg] -= it->g;
	  modint & vv=v[jt-jtbeg];
	  vv = (vv-longlong(it->g))%env;
	  ++jt;
	  continue;
	}
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    // v[jt-jtbeg] -= it->g;
	    modint & vv=v[jt-jtbeg];
	    vv = (vv-longlong(it->g))%env;
	    ++jt;
	    break;
	  }
	}
      }
    }
    else {
      for (;it!=itend;++it){
	const tdeg_t & u=expo[it->u];
	if (dodicho && dicho(jt,jtend,u,p.order)){
	  // v[jt-jtbeg] -= it->g;
	  modint & vv=v[jt-jtbeg];
	  vv = (vv-longlong(it->g))%env;
	  ++jt;
	  continue;
	}
	for (;jt!=jtend;++jt){
	  if (*jt==u){
	    // v[jt-jtbeg]-=it->g;
	    modint & vv=v[jt-jtbeg];
	    vv = (vv-longlong(it->g))%env;
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void Rtorem(const vector<tdeg_t> & R,const vector<tdeg_t> & rem,vector<unsigned> & v){
    v.resize(R.size());
    vector<tdeg_t>::const_iterator it=R.begin(),itend=R.end(),jt=rem.begin(),jt0=jt,jtend=rem.end();
    vector<unsigned>::iterator vt=v.begin();
    for (;jt!=jtend;++jt){
      const tdeg_t & t=*jt;
      for (;it!=itend;++vt,++it){
	if (*it==t)
	  break;
      }
      *vt=hashgcd_U(jt-jt0);
    }
  }

  int zf4mod(vectzpolymod & res,const vector<unsigned> & G,modint env,vector< pair<unsigned,unsigned> > & B,vectzpolymod & f4buchbergerv,bool learning,unsigned & learned_position,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector<zinfo_t> & f4buchberger_info,unsigned & f4buchberger_info_position,bool recomputeR){
    if (B.empty())
      return 0;
    int dim=res.front().dim;
    order_t order=res.front().order;
    vector<tdeg_t> leftshift(B.size());
    vector<tdeg_t> rightshift(B.size());
    zleftright(res,B,leftshift,rightshift);
    f4buchbergerv.resize(B.size());
    zinfo_t info_tmp;
	unsigned nonzero = unsigned(B.size());
    zinfo_t * info_ptr=0;
    if (!learning && f4buchberger_info_position<f4buchberger_info.size()){
      info_ptr=&f4buchberger_info[f4buchberger_info_position];
      ++f4buchberger_info_position;
      nonzero=info_ptr->nonzero;
      if (nonzero==0){
	for (int i=0;i<f4buchbergerv.size();++i){
	  // CERR << v << endl;
	  f4buchbergerv[i].expo=&info_ptr->rem;
	  f4buchbergerv[i].order=order;
	  f4buchbergerv[i].dim=dim;
	  vector< zmodint > & Pcoord=f4buchbergerv[i].coord;
	  Pcoord.clear();
	}
	return 1;
      }
    }
    else {
      vector<tdeg_t> all;
      if (debug_infolevel>1)
	CERR << CLOCK() << " zf4buchberger begin collect monomials on #polys " << f4buchbergerv.size() << endl;
      zcollect(res,B,all,leftshift,rightshift);
      if (debug_infolevel>1)
	CERR << CLOCK() << " zf4buchberger symbolic preprocess" << endl;
      zsymbolic_preprocess(all,res,G,-1,info_tmp.quo,info_tmp.rem,info_tmp.R);
      if (debug_infolevel>1)
	CERR << CLOCK() << " zend symbolic preprocess" << endl;
#if 0
      f4buchberger_info->push_back(*info_ptr);
#else
      zinfo_t tmp;
      f4buchberger_info.push_back(tmp);
      zinfo_t & i=f4buchberger_info.back();
      swap(i.quo,info_tmp.quo);
      swap(i.R,info_tmp.R);
      swap(i.rem,info_tmp.rem);
      info_ptr=&f4buchberger_info.back();
#endif
    }
    const vector<tdeg_t> & R = info_ptr->R;
    vector<unsigned> Rtoremv;
    Rtorem(R,info_ptr->rem,Rtoremv);
    const vector< vector<tdeg_t> > & quo = info_ptr->quo;
    //CERR << quo << endl;
	unsigned N = unsigned(R.size()), i, j = 0;
    if (N==0) return 1;
    unsigned nrows=0;
    for (i=0;i<G.size();++i){
		nrows += unsigned(quo[i].size());
    }
    unsigned c=N;
    double sknon0=0;
    vector<used_t> used(N,0);
    unsigned usedcount=0,zerolines=0;
    vector< vector<modint> > K(B.size());
    vector<vector<unsigned short> > Mindex;
    vector< vector<modint> > Mcoeff(G.size());
    vector<coeffindex_t> coeffindex;
    Mindex.reserve(nrows);
    coeffindex.reserve(nrows);
    vector<sparse_element> atrier;
    atrier.reserve(nrows);
    for (i=0;i<G.size();++i){
      Mcoeff[i].reserve(res[G[i]].coord.size());
      std::vector<tdeg_t>::const_iterator jt=quo[i].begin(),jtend=quo[i].end();
      for (;jt!=jtend;++j,++jt){
	Mindex.push_back(vector<unsigned short>(0));
	Mindex[j].reserve(int(1.1*res[G[i]].coord.size()));
      }
    }
    for (i=0,j=0;i<G.size();++i){
      // copy coeffs of res[G[i]] in Mcoeff
      zcopycoeff(res[G[i]],Mcoeff[i]);
      // for each monomial of quo[i], find indexes and put in Mindex
      std::vector< tdeg_t >::const_iterator jt=quo[i].begin(),jtend=quo[i].end();
      for (;jt!=jtend;++j,++jt){
	coeffindex.push_back(coeffindex_t(N<=0xffff,i));
	zmakelinesplit(res[G[i]],&*jt,R,Mindex[j]);
	if (!coeffindex.back().b)
	  coeffindex.back().b=checkshortshifts(Mindex[j]);
	atrier.push_back(sparse_element(first_index(Mindex[j]),j));
      }
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " end build Mindex/Mcoeff zf4mod" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // CERR << "before sort " << Mindex << endl;
    sort_vector_sparse_element(atrier.begin(),atrier.end()); // sort(atrier.begin(),atrier.end(),tri1); 
    vector<coeffindex_t> coeffindex1(atrier.size());
    double mem=0; // mem*4=number of bytes allocated for M1
    vector< vector<unsigned short> > Mindex1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(Mindex1[i],Mindex[atrier[i].pos]);
      mem += Mindex1[i].size();
      swap(coeffindex1[i],coeffindex[atrier[i].pos]);
    }
    swap(Mindex,Mindex1);
	nrows = unsigned(Mindex.size());
    swap(coeffindex,coeffindex1);
    vector<unsigned> firstpos(atrier.size());
    for (i=0;i < atrier.size();++i){
      firstpos[i]=atrier[i].val;
    }
    bool freemem=mem>4e7; // should depend on real memory available
    if (debug_infolevel>1)
      CERR << CLOCK() << " Mindex sorted, rows " << nrows << " columns " << N << " terms " << mem << " ratio " << (mem/nrows)/N <<endl;
    // CERR << "after sort " << Mindex << endl;
    // step3 reduce
    vector<modint> v(N);
    vector<modint2> v64(N);
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
    if (N<nrows){
      CERR << "Error " << N << "," << nrows << endl;
      return -1;
    }
    unsigned Kcols=N-nrows;
    vector<unsigned> lebitmap(((N>>5)+1)*B.size());
    unsigned * bitmap=&lebitmap.front();
    for (i=0;i<B.size();++i){
      pair<unsigned,unsigned> bk=B[i];
      if (!learning && pairs_reducing_to_zero && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	if (debug_infolevel>2)
	  CERR << bk << " f4buchberger learned " << learned_position << endl;
	++learned_position;
	unsigned tofill=(N>>5)+1;
	fill(bitmap,bitmap+tofill,0);
	bitmap += tofill;
	continue;
      }
      zmakeline(res[bk.first],&leftshift[i],R,v,1);
      zmakelinesub(res[bk.second],&rightshift[i],R,v,1,env);
      // CERR << v << endl << v2 << endl;
      // sub(v,v2,env);
      // CERR << v << endl;
#ifdef __x86_64__
      if (env<(1<<24)){
	c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
      }
      else {
	c=giacmin(c,reducef4buchbergersplit64(v,Mindex,firstpos,Mcoeff,coeffindex,env,v128));
      }
#else // __x86_64__
	c=giacmin(c,reducef4buchbergersplit(v,Mindex,firstpos,Mcoeff,coeffindex,env,v64));
#endif // __x86_64__
      // zconvert(v,coeffit,bitmap,used); bitmap += (N>>5)+1;
      K[i].reserve(Kcols);
      zconvert(v,K[i],bitmap,used); bitmap += (N>>5)+1;
      //CERR << v << endl << SK[i] << endl;
    } // end for (i=0;i<B.size();++i)
    Mindex.clear();
    if (!pairs_reducing_to_zero){
      vector<tdeg_t> clearer;
      info_ptr->R.swap(clearer);
    }
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv split reduced " << B.size() << " polynoms over " << N << " monomials, start at " << c << endl;
    for (i=0;i<N;++i)
      usedcount += (used[i]>0);
    if (debug_infolevel>1){
      CERR << CLOCK() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
      if (debug_infolevel>3)
	CERR << " column split used " << used << endl;
    }
    // create dense matrix K 
    bitmap=&lebitmap.front();
    create_matrix(bitmap,(N>>5)+1,used,K);
    // clear memory required for lescoeffs
    //vector<modint> tmp; lescoeffs.swap(tmp); 
    { vector<unsigned> tmp1; lebitmap.swap(tmp1); }
    if (debug_infolevel>1)
      CERR << CLOCK() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> permutation,maxrankcols; longlong idet;
    //CERR << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,int(K.size()),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    //CERR << K << endl;
	unsigned first0 = unsigned(pivots.size());
    if (first0<K.size() && learning){
      vector<modint> & tmpv=K[first0];
      for (i=0;i<tmpv.size();++i){
	if (tmpv[i])
	  break;
      }
      if (i==tmpv.size()){
		  unsigned Ksize = unsigned(K.size());
	K.resize(first0);
	K.resize(Ksize);
      }
    }
    //CERR << permutation << K << endl;
    if (!learning){
      // check that permutation is the same as learned permutation
      for (unsigned j=0;j<permutation.size();++j){
	if (permutation[j]!=info_ptr->permu[j]){
	  CERR << "learning failed"<<endl;
	  return -1;
	}
      }
    }
    if (learning)
      info_ptr->permu=permutation;
    // CERR << K << "," << permutation << endl;
    // vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      CERR << CLOCK() << " f4buchbergerv interreduced" << endl;
    for (i=0;i<f4buchbergerv.size();++i){
      // CERR << v << endl;
      f4buchbergerv[permutation[i]].expo=&info_ptr->rem;
      f4buchbergerv[permutation[i]].order=order;
      f4buchbergerv[permutation[i]].dim=dim;
      vector< zmodint > & Pcoord=f4buchbergerv[permutation[i]].coord;
      Pcoord.clear();
      vector<modint> & v =K[i];
      if (v.empty()){
	continue;
      }
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      Pcoord.reserve(vcount);
      vector<used_t>::const_iterator ut=used.begin();
      unsigned pos=0;
      for (vt=v.begin();pos<N;++ut,++pos){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(zmodint(coeff,Rtoremv[pos]));
      }
      if (!Pcoord.empty())
	f4buchbergerv[permutation[i]].ldeg=(*f4buchbergerv[permutation[i]].expo)[Pcoord.front().u];
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	zsmallmultmod(invmod(Pcoord.front().g,env),f4buchbergerv[permutation[i]],env);	
	Pcoord.front().g=1;
      }
      if (freemem){
	vector<modint> tmp; tmp.swap(v);
      }
    }
    return 1;
  }

  // oldG is the Gbasis before the first line of f4buchbergerv is added
  // otherwise we might miss some new pairs to be added
  // f:=(1387482169552326*s*t1*t2^2-25694114250969*s*t1*t2+240071563017*s*t1+579168836143704*t1*t2^2-10725348817476*t1*t2+100212766488*t1):;fb:=(-7035747399*s*t1^2*t2^2+118865637*s*t1^2*t2-793881*s*t1^2+118865637*s*t1*t2^2-1167858*s*t1*t2+1944*s*t1-1089126*s*t2^2+1944*s*t2+18*s-2936742966*t1^2*t2^2+49601160*t1^2*t2-328050*t1^2+49601160*t1*t2^2-485514*t1*t2+972*t1-446148*t2^2+972*t2+36):;rmp:=s^2+10*s+4:;gbasis([f,fb,rmp],[s,t1,t2],revlex);
  static void zgbasis_updatemod(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectzpolymod & res,unsigned pos,vector<unsigned> & oldG){
    if (debug_infolevel>2)
      CERR << CLOCK() << " zmod begin gbasis update " << G.size() << endl;
    if (debug_infolevel>3)
      CERR << "G=" << G << "B=" << B << endl;
    const zpolymod & h = res[pos];
    order_t order=h.order;
    short dim=h.dim;
    vector<unsigned> C;
    C.reserve(G.size()+1);
    const tdeg_t & h0=h.ldeg;
    for (unsigned i=0;i<oldG.size();++i){
      if (tdeg_t_all_greater(h0,res[oldG[i]].ldeg,order))
	return;
    }
    tdeg_t tmp1,tmp2;
    // C is used to construct new pairs
    // create pairs with h and elements g of G, then remove
    // -> if g leading monomial is prime with h, remove the pair
    // -> if g leading monomial is not disjoint from h leading monomial
    //    keep it only if lcm of leading monomial is not divisible by another one
#if 1
	unsigned tmpsize = unsigned(G.size());
    vector<tdeg_t> tmp(tmpsize);
    for (unsigned i=0;i<tmpsize;++i){
      index_lcm(h0,res[G[i]].ldeg,tmp[i],order); 
    }
#else
    // this would be faster but it does not work for 
    // gbasis([25*y^2*x^6-10*y^2*x^5+59*y^2*x^4-20*y^2*x^3+43*y^2*x^2-10*y^2*x+9*y^2-80*y*x^6+136*y*x^5+56*y*x^4-240*y*x^3+104*y*x^2+64*x^6-192*x^5+192*x^4-64*x^3,25*y^2*6*x^5-10*y^2*5*x^4+59*y^2*4*x^3-20*y^2*3*x^2+43*y^2*2*x-10*y^2-80*y*6*x^5+136*y*5*x^4+56*y*4*x^3-240*y*3*x^2+104*y*2*x+64*6*x^5-192*5*x^4+192*4*x^3-64*3*x^2,25*2*y*x^6-10*2*y*x^5+59*2*y*x^4-20*2*y*x^3+43*2*y*x^2-10*2*y*x+9*2*y-80*x^6+136*x^5+56*x^4-240*x^3+104*x^2],[x,y],revlex);
    // pair <4,3> is not generated
    unsigned tmpsize=G.empty()?0:G.back()+1;
    vector<tdeg_t> tmp(tmpsize);
    for (unsigned i=0;i<tmpsize;++i){
      index_lcm(h0,res[i].ldeg,tmp[i],order); 
    }
#endif
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (disjoint(h0,res[G[i]].ldeg,order,dim))
	continue;
      // h0 and G[i] leading monomial not prime together
#if 1
      tdeg_t * tmp1=&tmp[i]; 
#else
      tdeg_t * tmp1=&tmp[G[i]]; 
#endif
      tdeg_t * tmp2=&tmp[0],*tmpend=tmp2+tmpsize;
      for (;tmp2!=tmp1;++tmp2){
	if (tmp2->tab[0]==-1)
	  continue;
	if (tdeg_t_all_greater(*tmp1,*tmp2,order))
	  break; // found another pair, keep the smallest, or the first if equal
      }
      if (tmp2!=tmp1)
	continue;
      for (++tmp2;tmp2<tmpend;++tmp2){
	if (tmp2->tab[0]==-1)
	  continue;
	if (tdeg_t_all_greater(*tmp1,*tmp2,order) && *tmp1!=*tmp2){
	  break; 
	}
      }
      if (tmp2==tmpend)
	C.push_back(G[i]);
    }
    vector< pair<unsigned,unsigned> > B1;
    B1.reserve(B.size()+C.size());
    for (unsigned i=0;i<B.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	continue;
      index_lcm(res[B[i].first].ldeg,res[B[i].second].ldeg,tmp1,order);
      if (!tdeg_t_all_greater(tmp1,h0,order)){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].first].ldeg,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
      index_lcm(res[B[i].second].ldeg,h0,tmp2,order);
      if (tmp2==tmp1){
	B1.push_back(B[i]);
	continue;
      }
    }
    // B <- B union pairs(h,g) with g in C
    for (unsigned i=0;i<C.size();++i){
      B1.push_back(pair<unsigned,unsigned>(pos,C[i]));
    }
    swap(B1,B);
    // Update G by removing elements with leading monomial >= leading monomial of h
    if (debug_infolevel>2){
      CERR << CLOCK() << " end, pairs:"<< endl;
      if (debug_infolevel>3)
	CERR << B << endl;
      CERR << "mod begin Groebner interreduce " << endl;
    }
    C.clear();
    C.reserve(G.size()+1);
    // bool pos_pushed=false;
    for (unsigned i=0;i<G.size();++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (interrupted || ctrl_c)
	return;
      if (!res[G[i]].coord.empty() && !tdeg_t_all_greater(res[G[i]].ldeg,h0,order)){
	C.push_back(G[i]);
      }
      // NB: removing all pairs containing i in it does not work
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " mod end Groebner interreduce " << endl;
    C.push_back(pos);
    swap(C,G);
  }

  void convert(const polymod & p,zpolymod & q,const vector<tdeg_t> & R){
    q.order=p.order;
    q.dim=p.dim;
    q.coord.clear();
    q.coord.reserve(p.coord.size());
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    vector<tdeg_t>::const_iterator jt=R.begin(),jt0=jt,jtend=R.end();
    for (;it!=itend;++it){
      const tdeg_t & u=it->u;
      for (;jt!=jtend;++jt){
	if (*jt==u)
	  break;
      }
      if (jt!=jtend){
	q.coord.push_back(zmodint(it->g,int(jt-jt0)));
	++jt;
      }
      else
	COUT << "not found" << endl;
    }
    q.expo=&R;
    if (!q.coord.empty())
      q.ldeg=R[q.coord.front().u];
  }

  void convert(const zpolymod & p,polymod & q){
    q.dim=p.dim;
    q.order=p.order;
    q.coord.clear();
    q.coord.reserve(p.coord.size());
    vector< zmodint >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    const vector<tdeg_t> & expo=*p.expo;
    for (;it!=itend;++it){
      q.coord.push_back(T_unsigned<modint,tdeg_t>(it->g,expo[it->u]));
    }
  }

  void zincrease(vector<zpolymod> &v){
    if (v.size()!=v.capacity())
      return;
    vector<zpolymod> w;
    w.reserve(v.size()*2);
    for (unsigned i=0;i<v.size();++i){
      w.push_back(zpolymod(v[i].order,v[i].dim,v[i].expo,v[i].ldeg));
      w[i].coord.swap(v[i].coord);
    }
    v.swap(w);
  }

  void smod(polymod & resmod,modint env){
    std::vector< T_unsigned<modint,tdeg_t> >::iterator it=resmod.coord.begin(),itend=resmod.coord.end();
    for (;it!=itend;++it){
      modint n=it->g;
      if (n>env/2)
	it->g -= env;
      else {
	if (n<=-env/2)
	  it->g += env;
      }
    }
  }

  void smod(vectpolymod & resmod,modint env){
    for (unsigned i=0;i<resmod.size();++i)
      smod(resmod[i],env);
  }

  bool in_zgbasis(vectpolymod &resmod,unsigned ressize,vector<unsigned> & G,modint env,bool totdeg,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< zinfo_t > & f4buchberger_info,bool recomputeR){
    unsigned cleared=0;
    unsigned learned_position=0,f4buchberger_info_position=0;
    bool learning=f4buchberger_info.empty();
    unsigned capa = unsigned(f4buchberger_info.capacity());
    order_t order=resmod.front().order;
    short dim=resmod.front().dim;
    polymod TMP2(order,dim);
    vector< pair<unsigned,unsigned> > B,BB;
    B.reserve(256); BB.reserve(256);
    vector<unsigned> smallposv;
    smallposv.reserve(256);
    info_t information;
    if (order.o!=_REVLEX_ORDER && order.o!=_TDEG_ORDER)
      totdeg=false;
    vector<unsigned> oldG(G);
    for (unsigned l=0;l<ressize;++l){
#ifdef GIAC_REDUCEMODULO
      reducesmallmod(resmod[l],resmod,G,-1,env,TMP2,env);
#endif
      gbasis_updatemod(G,B,resmod,l,TMP2,env,true,oldG);
    }
    // init zpolymod before main loop
    collect(resmod,TMP2);
    vector<tdeg_t> R0(TMP2.coord.size());
    for (unsigned l=0;l<TMP2.coord.size();++l)
      R0[l]=TMP2.coord[l].u;
    vectzpolymod res;
    res.resize(ressize);
    for (unsigned l=0;l<ressize;++l)
      convert(resmod[l],res[l],R0);
    if (debug_infolevel>1000)
      res.dbgprint(); // instantiate
    for (;!B.empty() && !interrupted && !ctrl_c;){
#ifdef TIMEOUT
      control_c();
#endif
      if (f4buchberger_info_position>=capa-1)
	return false;
      if (debug_infolevel>1)
	CERR << CLOCK() << " begin new iteration zmod, " << env << " number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // mem clear: remove res[i] if i is not in G nor in B
      /* int Gmax=0;
      for (unsigned i=0;i<int(G.size());++i){
	if (G[i]>Gmax) Gmax=G[i];
      }
      for (unsigned i=0;i<int(B.size());++i){
	if (B[i].first>Gmax) Gmax=B[i].first;
	if (B[i].second>Gmax) Gmax=B[i].second;
      }
      vector<bool> clean(Gmax+1,true); */
      vector<bool> clean(G.back()+1,true); 
      vector<tdeg_t> Blcm(B.size());
      for (unsigned i=0;i<G.size();++i){
	clean[G[i]]=false;
      }
      for (unsigned i=0;i<B.size();++i){
	clean[B[i].first]=false;
	clean[B[i].second]=false;
	index_lcm(res[B[i].first].ldeg,res[B[i].second].ldeg,Blcm[i],order);
      }
      for (unsigned i=0;i<clean.size();++i){
	if (clean[i] && res[i].coord.capacity()>1){
	  cleared += int(res[i].coord.capacity())-1;
	  zpolymod clearer;
	  clearer.coord.swap(res[i].coord);
	}
      }
      vector< pair<unsigned,unsigned> > smallposp;
      if (B.size()<=GBASIS_F4BUCHBERGER)
	swap(smallposp,B);      
      else {
#ifdef GIAC_GBASISLEX
	// find smallest lcm pair in B
	unsigned smalltotdeg=RAND_MAX,firstdeg=RAND_MAX-1;
	for (unsigned i=0;i<B.size();++i){
	  if (order.lex){
	    int s=Blcm[i].total_degree(order);
	    if (s>smalltotdeg)
	      continue;
	    if (s<smalltotdeg){
	      firstdeg=Blcm[i].front();
	      smalltotdeg=s;
	      continue;
	    }
	    if (Blcm[i].front()<firstdeg)
	      firstdeg=Blcm[i].front();
	  } 
	  else {
	    if (Blcm[i].total_degree(order)<smalltotdeg)
	      smalltotdeg=Blcm[i].total_degree(order);
	  }
	}
	vector<unsigned> smallposv;
	for (unsigned i=0;i<B.size();++i){
	  if (order.lex){
	    if (Blcm[i].front()==firstdeg && Blcm[i].total_degree(order)==smalltotdeg)
	      smallposv.push_back(i);
	  }
	  else {
	    if (Blcm[i].total_degree(order)==smalltotdeg)
	      smallposv.push_back(i);
	  }
	}
	if (debug_infolevel>1)
	  CERR << CLOCK() << " zpairs degrees " << firstdeg << "," << smalltotdeg << " #" << smallposv.size() << endl;
#else
	// find smallest lcm pair in B
	unsigned smalltotdeg=RAND_MAX,firstdeg=RAND_MAX-1;
	for (unsigned i=0;i<B.size();++i){
	  unsigned f=Blcm[i].total_degree(order);
	  if (f>firstdeg)
	    continue;
	  if (f<firstdeg){
	    firstdeg=f;
	    smalltotdeg=Blcm[i].tdeg;
	    continue;
	  }
	  if (Blcm[i].tdeg<smalltotdeg)
	    smalltotdeg=Blcm[i].tdeg;
	}
	vector<unsigned> smallposv;
	for (unsigned i=0;i<B.size();++i){
	  if (
	      // Blcm[i].tdeg==smalltotdeg && 
	      Blcm[i].total_degree(order)==firstdeg)
	    smallposv.push_back(i);
	}
	if (debug_infolevel>1)
	  CERR << CLOCK() << " zpairs total degrees " << firstdeg << "," << smalltotdeg << " #" << smallposv.size() << endl;
#endif
	if (debug_infolevel>3)
	  CERR << "pairs reduced " << B << " indices " << smallposv << endl;
	if (smallposv.size()==B.size()){
	  swap(smallposp,B);
	  B.clear();
	}
	else {
	  for (unsigned i=0;i<smallposv.size();++i)
	    smallposp.push_back(B[smallposv[i]]);
	  // remove pairs
	  for (int i=int(smallposv.size())-1;i>=0;--i)
	    B.erase(B.begin()+smallposv[i]);
	}
      }
      vectzpolymod f4buchbergerv; // collect all spolys
      int f4res=-1;
      f4res=zf4mod(res,G,env,smallposp,f4buchbergerv,learning,learned_position,pairs_reducing_to_zero,f4buchberger_info,f4buchberger_info_position,recomputeR);
      if (f4res==-1)
	return false;
      if (f4res==0)
	continue;
      // update gbasis and learning
      // requires that Gauss pivoting does the same permutation for other primes
      if (learning && pairs_reducing_to_zero){
	for (unsigned i=0;i<f4buchbergerv.size();++i){
	  if (f4buchbergerv[i].coord.empty()){
	    if (debug_infolevel>2)
	      CERR << "learning f4buchberger " << smallposp[i] << endl;
	    pairs_reducing_to_zero->push_back(smallposp[i]);
	  }
	}
      }
      unsigned added=0;
      for (unsigned i=0;i<f4buchbergerv.size();++i){
	if (!f4buchbergerv[i].coord.empty())
	  ++added;
      }
      if (debug_infolevel>1)
	CERR << CLOCK() << " reduce f4buchberger end on " << added << " from " << f4buchbergerv.size() << " pairs, gbasis update begin" << endl;
      vector<unsigned> oldG(G);
      for (unsigned i=0;i<f4buchbergerv.size();++i){
	if (!f4buchbergerv[i].coord.empty()){
	  zincrease(res);
	  if (ressize==res.size())
	    res.push_back(zpolymod(order,dim,f4buchbergerv[i].ldeg));
	  res[ressize].expo=f4buchbergerv[i].expo;
	  swap(res[ressize].coord,f4buchbergerv[i].coord);
	  ++ressize;
	  if (learning || f4buchberger_info_position-1>=f4buchberger_info.size())
	    zgbasis_updatemod(G,B,res,ressize-1,oldG);
	}
	else {
	  // if (!learning && pairs_reducing_to_zero)  CERR << " error learning "<< endl;
	}
      }
      if (!learning && f4buchberger_info_position-1<f4buchberger_info.size()){
	B=f4buchberger_info[f4buchberger_info_position-1].B;
	G=f4buchberger_info[f4buchberger_info_position-1].G;
	continue;
      }
      if (learning){
	f4buchberger_info.back().B=B;
	f4buchberger_info.back().G=G;
	f4buchberger_info.back().nonzero=added;
      }
      //unsigned debut=G.size()-added;
      // CERR << "finish loop G.size "<<G.size() << endl;
      // CERR << added << endl;
    } // end main loop
    // convert back zpolymod to polymod
    resmod.resize(res.size());
    for (unsigned l=0;l<res.size();++l){
      convert(res[l],resmod[l]);
    }
    res.clear();
    // final interreduce step2
    polymod TMP1(order,dim);
    for (unsigned j=0; j<G.size();++j){
      reducesmallmod(resmod[G[j]],resmod,G,j,env,TMP1,true);
    }
    if (ressize<resmod.size())
      res.resize(ressize);
    if (debug_infolevel>1){
      unsigned t=0;
      for (unsigned i=0;i<res.size();++i)
		  t += unsigned(res[i].coord.size());
      CERR << CLOCK() << " total number of monomials in res " << t << endl;
      CERR << "Number of monomials cleared " << cleared << endl;
    }
    smod(resmod,env);
    // sort(resmod.begin(),resmod.end(),tripolymod);
    return true;
  }

  bool zgbasis(vectpoly8 & res8,vectpolymod &resmod,vector<unsigned> & G,modint env,bool totdeg,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< zinfo_t > & f4buchberger_info,bool recomputeR,bool convertpoly8){
    for (unsigned i=0;i<resmod.size();++i)
      resmod[i].coord.clear();
    convert(res8,resmod,env);
    unsigned ressize = unsigned(res8.size());
    bool b=in_zgbasis(resmod,ressize,G,env,totdeg,pairs_reducing_to_zero,f4buchberger_info,recomputeR);
    if (convertpoly8)
      convert(resmod,res8,env);
    return b;
  }

#endif // GIAC_SHORTSHIFTTYPE==16
  /* *************
     END ZPOLYMOD
     ************* */

  /* *************
     RUR UTILITIES (rational univariate representation for 0 dimension ideals)
     ************* */
  int rur_dim(int dim,order_t order){
    if (order.o==_3VAR_ORDER) return 3;
    if (order.o==_7VAR_ORDER) return 7;
    if (order.o==_11VAR_ORDER) return 11;
    if (order.o==_16VAR_ORDER) return 16;
    if (order.o==_32VAR_ORDER) return 32;
    if (order.o==_64VAR_ORDER) return 64;
    return dim;
  }

  // returns -1 if not 0 dimensional, -RAND_MAX if overflow
  // otherwise returns dimension of quotient and sets lm to the list of 
  // leading monomials generating the quotient ideal
  int rur_quotient_ideal_dimension(const vectpolymod & gbmod,polymod & lm){
    if (gbmod.empty())
      return -1;
    order_t order=gbmod.front().order;
    int dim=gbmod.front().dim;
    lm.order=order; lm.dim=dim; lm.coord.clear();
    polymod gblm(order,dim);
	unsigned S = unsigned(gbmod.size());
    for (unsigned i=0;i<S;++i){
      if (gbmod[i].coord.empty())
	continue;
      gblm.coord.push_back(gbmod[i].coord.front());
    }
    // for 3var, 7var, 11 var search in the first 3 var, 7 var or 11 var
    // for revlex search for all variables
    // we must find a leading monomial in gbmod that contains only this variable
    int d=rur_dim(dim,order);
    vector<int> v(d);
    for (unsigned i=0;i<S;++i){
      index_t l;
      get_index(gblm.coord[i].u,l,order,dim);
      unsigned j,k;
      for (j=0;int(j)<d;++j){
	if (l[j]){
	  for (k=j+1;int(k)<d;++k){
	    if (l[k])
	      break;
	  }
	  if (k==d)
	    v[j]=l[j];
	  break;
	}
      }
    }
    // now all indices of v must be non 0
    double M=1;
    for (unsigned i=0;i<v.size();++i){
      if (v[i]==0)
	return -1;
      M *= v[i];
    }
    if (M>1e6)
      return -RAND_MAX; // overflow
    // the ideal is finite dimension, now we will compute the exact dimension
    // a monomial degree is associated to an integer with
    // [l_0,l_1,...,l_{d-1}] -> ((l_0*v1+l_1)*v2+...+l_{d-1} < v0*v1*...
    // perhaps a sieve would be faster, but it's harder to implement
    // and we won't consider too high order anyway...
    index_t cur(d);
    for (int I=0;I<M;++I){
      int i=I;
      // i-> cur -> tdeg_t
      for (int j=int(v.size())-1;j>=0;--j){
	cur[j]=i%v[j];
	i/=v[j];
      }
      tdeg_t curu(cur,order);
      // then search if > to one of the leading monomials for all indices
      unsigned j;
      if (order.o==_3VAR_ORDER){
	for (j=0;j<S;++j){
	  tdeg_t u=gblm.coord[j].u;
	  if (curu.tab[1]>=u.tab[1] && curu.tab[2]>=u.tab[2] && curu.tab[3]>=u.tab[3])
	    break;
	}
      }
      if (order.o==_7VAR_ORDER){
      }
      if (order.o==_11VAR_ORDER){
      }
      if (order.o==_REVLEX_ORDER){
	for (j=0;j<S;++j){
	  if (tdeg_t_all_greater(curu,gblm.coord[j].u,order))
	    break;
	}
      }
      // if found continue, else add cur to the list of monomials
      if (j==gbmod.size())
	lm.coord.push_back(T_unsigned<modint,tdeg_t>(1,curu));
    }
    sort(lm.coord.begin(),lm.coord.end(),tdeg_t_sort_t(order));
	return unsigned(lm.coord.size());
  }

  // multiply a by b mod p in res
  // b is supposed to have small length
  void rur_mult(const polymod & a,const polymod & b,modint p,polymod & res){
    res.coord.clear();
    polymod tmp(b.order,b.dim);
    for (unsigned i=0;i<b.coord.size();++i){
      smallmultsubmod(res,0,(-b.coord[i].g) % p,a,b.coord[i].u,tmp,p);
      tmp.coord.swap(res.coord);
    }
  }
  
  // coordinates of cur w.r.t. lm
  void rur_coordinates(const polymod & cur,const polymod & lm,vecteur & tmp){
    unsigned k=0,j=0;
    for (;j<lm.coord.size() && k<cur.coord.size();++j){
      if (lm.coord[j].u!=cur.coord[k].u)
	tmp[j]=0;
      else {
	tmp[j]=cur.coord[k].g;
	++k;
      }
    }
    for (;j<lm.coord.size();++j){
      tmp[j]=0;
    }
  }

  // s*coordinates reduced as a linear combination of the lines of M
  bool rur_linsolve(const vectpolymod & gbmod,const polymod & lm,const polymod & s,const matrice & M,modint p,matrice & res){
    int S=int(lm.coord.size()),dim=lm.dim;
    order_t order=lm.order;
    polymod TMP1(order,dim);
    vector<unsigned> G(gbmod.size());
    for (unsigned i=0;i<G.size();++i)
      G[i]=i;
    matrice N(M);
    polymod si(order,dim);
    int d=rur_dim(dim,order);
    vecteur tmp(lm.coord.size());
    for (unsigned i=0;int(i)<d;++i){
      index_t l(dim);
      l[i]=1;
      smallshift(s.coord,tdeg_t(l,order),si.coord);
      reducesmallmod(si,gbmod,G,-1,p,TMP1,false);
      // get coordinates of cur in tmp (make them mod p)
      rur_coordinates(si,lm,tmp);
      N.push_back(tmp);
    }
    N=mtran(N);
    if (debug_infolevel)
      CERR << CLOCK() << " rur rref" << endl;
    gen n=_rref(N,context0);
    if (!ckmatrix(n))
      return false;
    N=mtran(*n._VECTptr);
    // check that first line are idn
    for (unsigned i=0;i<lm.coord.size();++i){
      vecteur & Ni=*N[i]._VECTptr;
      for (unsigned j=0;j<lm.coord.size();++j){
	if (i==j && !is_one(Ni[j]))
	  return false;
	if (i!=j && !is_zero(Ni[j]))
	  return false;
      }
    }
    N=vecteur(N.begin()+lm.coord.size(),N.end());
    for (unsigned i=0;i<N.size();++i){
      vecteur & m =*N[i]._VECTptr;
      for (unsigned j=0;j<m.size();++j){
	if (m[j].type==_MOD)
	  m[j]=*m[j]._MODptr;
      }
      reverse(m.begin(),m.end());
      m=trim(m,0);
    }
    res=N;
    return true;
  }

  // Compute minimal polynomial of s
  bool rur_minpoly(const vectpolymod & gbmod,const polymod & lm,const polymod & s,modint p,vecteur & m,matrice & M){
    int S=int(lm.coord.size()),dim=lm.dim;
    order_t order=lm.order;
    polymod TMP1(order,dim);
    vector<unsigned> G(gbmod.size());
    for (unsigned i=0;i<G.size();++i)
      G[i]=i;
    M.clear();
    // set th i-th row of M with coordinates of s^i reduced/gbmod in terms of lm
    vecteur tmp(S);
    tmp[0]=makemod(0,p);
    tmp[S-1]=1;
    M.push_back(tmp);
    polymod cur(s);
    for (unsigned i=1;i<=lm.coord.size();++i){
      reducesmallmod(cur,gbmod,G,-1,p,TMP1,false);
      // get coordinates of cur in tmp (make them mod p)
      rur_coordinates(cur,lm,tmp);
      M.push_back(tmp);
      // multiply cur and s
      rur_mult(cur,s,p,TMP1);
      cur.coord.swap(TMP1.coord);
    }
    matrice N(M);
    M.pop_back(); // remove the last one (for further computations, assuming max rank)
    N=mtran(N);
    vecteur K;
    if (debug_infolevel)
      CERR << CLOCK() << " begin rur ker" << endl;
    if (!mker(N,K,1,context0) || K.empty() || K.front().type!=_VECT)
      return false;
    if (debug_infolevel)
      CERR << CLOCK() << " end rur ker" << endl;
    m=*K.front()._VECTptr;
    for (unsigned i=0;i<m.size();++i){
      if (m[i].type==_MOD)
	m[i]=*m[i]._MODptr;
    }
    reverse(m.begin(),m.end());
    m=trim(m,0);
    if (debug_infolevel>1)
      CERR << "Minpoly for " << s << ":" << m << endl;
    return true;
  }

  void rur_convert_univariate(const vecteur & v,int varno,polymod & tmp){
    int vs=int(v.size());
    order_t order=tmp.order;
    tmp.coord.clear();
    index_t l(tmp.dim);
    for (unsigned j=0;int(j)<vs;++j){
      l[varno]=vs-1-j;
      if (v[j].val)
	tmp.coord.push_back(T_unsigned<modint,tdeg_t>(v[j].val,tdeg_t(index_m(l),order)));
    }
  }

  // if radical==-1, shrink the ideal to radical part
  // if radical==1, the ideal is already radical
  // if radical==0, also tries with radical ideal
  // find a separating element, given the groebner basis and the list of leading
  // monomials of a basis of the quotient ideal
  // if true, then separating element is s
  // and s has m as minimal polynomial,
  // M is the list of rows coordinates of powers of s in lm
  // This will not work if the ideal is not a radical ideal
  // In that case, if we get a minimal pol of degree M > lm.size()/2 for one coord.
  // we search for each coordinate a relation polynomial1*coordinate-polynomial2=0
  // where degree(polynomial2)<M and degree(polynomial1) <= lm.size()-M
  // then we must consider particular values of t that cancel gcd(polynomial1,minpoly)
  bool rur_separate(vectpolymod & gbmod,polymod & lm,modint p,polymod & s,vecteur & m,matrice & M,int radical){
    order_t order=lm.order;
    int dim=lm.dim,d=rur_dim(dim,order);
    s.order=order; s.dim=dim; 
    // first try coordinates
    vecteur minp(d);
    for (int i=d-1;i>=0;--i){
      s.coord.clear(); m.clear(); M.clear();
      index_t l(dim);
      l[i]=1;
      s.coord.push_back(T_unsigned<modint,tdeg_t>(1,tdeg_t(l,order)));
      if (!rur_minpoly(gbmod,lm,s,p,m,M))
	return false;
      if (m.size()==lm.coord.size()+1)
	return true;
      // keep m in order to shrink to the radical ideal if separation fails
      if (radical<=0)
	minp[i]=m;
    }
    // now try a random small integer linear combination
    if (radical!=-1){
      for (unsigned essai=0;essai<40;++essai){
	s.coord.clear(); m.clear(); M.clear();
	int n=(3+essai/5);
	int r=int(std_rand()*std::pow(double(n),double(d))/RAND_MAX),r1;
	for (unsigned i=0;int(i)<d;++i){
	  index_t l(dim);
	  l[i]=1;
	  r1=(r%n)-n/2;
	  r/=n;
	  if (r1)
	    s.coord.push_back(T_unsigned<modint,tdeg_t>(r1,tdeg_t(l,order)));
	}
	if (!rur_minpoly(gbmod,lm,s,p,m,M))
	  return false;
	if (m.size()==lm.coord.size()+1)
	  return true;      
      }
      if (radical==1)
	return false;
    }
    // shrink ideal and try again
    bool shrinkit=false;
    environment env;
    env.modulo=p;
    env.moduloon=true;
    for (unsigned i=0;int(i)<d;++i){
      if (minp[i].type!=_VECT)
	continue;
      m=*minp[i]._VECTptr;
      if (m.empty())
	continue;
      vecteur m1=derivative(m,&env);
      m1=gcd(m,m1,&env);
      if (m1.size()>1){
	if (debug_infolevel)
	  CERR << "Adding sqrfree part " << m1 << " coordinate " << i << endl;
	m1=operator_div(m,m1,&env); // m1 is the square free part
	polymod m1mod(order,dim);
	rur_convert_univariate(m1,i,m1mod);
	unsigned j;
	for (j=0;j<gbmod.size();++j){
	  if (tdeg_t_greater(m1mod.coord.front().u,gbmod[j].coord.front().u,order))
	    break;
	}
	gbmod.insert(gbmod.begin()+j,m1mod);
	shrinkit=true;
      }
    }
    if (!shrinkit)
      return false;
    vector<unsigned> G;
    if (!in_gbasisf4buchbergermod(gbmod,unsigned(gbmod.size()),G,p,/* totdeg */ true,0,0,true))
      return false;
    vectpolymod newgb;
    for (unsigned i=0;i<G.size();++i)
      newgb.push_back(gbmod[G[i]]);
    newgb.swap(gbmod);
    lm.coord.clear();
    if (rur_quotient_ideal_dimension(gbmod,lm)<0)
      return false;
    if (radical==-1)
      return true;
    return rur_separate(gbmod,lm,p,s,m,M,1);
  }

  bool rur_convert(const vecteur & v,const polymod & lm,polymod & res){
    res.coord.clear();
    res.order=lm.order; res.dim=lm.dim;
    if (v.size()>lm.coord.size())
      return false;
    for (unsigned i=0;i<v.size();++i){
      gen coeff=v[i];
      if (!is_zero(coeff))
	res.coord.push_back(T_unsigned<modint,tdeg_t>(coeff.val,lm.coord[i].u));
    }
    return true;
  }

  // set rur to be the list of s, 
  // m the minimal polynomial of s as a polymod wrt the 1st var
  // and for each coordinate (sqrfree part of m) * coordinate
  // expressed as a polynomial in s (stored in a polymod wrt 1st var)
  bool rur_compute(vectpolymod & gbmod,polymod & lm,polymod & lmmodradical,int p,polymod & s,vectpolymod & rur){
    vecteur m,M,res;
    int dim=lm.dim;
    order_t order=lm.order;
    if (s.coord.empty()){
      // find separating element
      if (!rur_separate(gbmod,lm,p,s,m,M,0))
	return false;
    }
    else {
      // if lm!=lmmodradical, ideal is not radical, recompute radical part
      if (!(lm==lmmodradical)){
	polymod s1(s.order,s.dim);
	if (!rur_separate(gbmod,lm,p,s1,m,M,-1))
	  return false;
      }
      // separating element is already known
      if (!rur_minpoly(gbmod,lm,s,p,m,M) || m.size()!=lm.coord.size()+1)
	return false;
    }
    // find the square-free part of m, express it as a polymod using M
    environment env;
    env.modulo=p;
    env.moduloon=true;
    vecteur m1=derivative(m,&env);
    m1=gcd(m,m1,&env);
    if (debug_infolevel && m1.size()>1)
      CERR << CLOCK() << " sqrfree mod " << p << ":" << m1 << endl;
    m1=operator_div(m,m1,&env); // m1 is the square free part
    vecteur m2=derivative(m1,&env); // m2 is the derivative, prime with m1
    // make the "product" with M (rows of M are powers of t)
    gen m3;
    for (unsigned i=0;i<m2.size();++i){
      gen coeff=m2[m2.size()-1-i];
      m3 += smod(coeff*M[i],p);
    }
    m3=smod(m3,p);
    polymod mprime(order,dim);
    if (m3.type==_VECT && m3._VECTptr->size()<=lm.coord.size())
      rur_convert(*m3._VECTptr,lm,mprime);
    else
      return false;
    if (debug_infolevel)
      CERR << CLOCK() << " rur linsolve" << endl;
    if (!rur_linsolve(gbmod,lm,mprime,M,p,res))
      return false;
    // rur=[separating element,sqrfree part of minpoly,derivative of sqrfree part,
    // derivative of sqrfree part*other coordinates]
    rur.clear();
    rur.push_back(s);
    polymod tmp(order,dim);
    rur_convert_univariate(m1,0,tmp);
    rur.push_back(tmp);
    rur_convert_univariate(m2,0,tmp);
    rur.push_back(tmp);
    // convert res to rur
    for (unsigned i=0;i<res.size();++i){
      index_t l(dim);
      vecteur & v = *res[i]._VECTptr;
      rur_convert_univariate(v,0,tmp);
      rur.push_back(tmp);
    }
    return true;
  }

  // returns -1 if lm1 is not contained in lm2 and lm2 is not contained in lm1
  // returns 0 if lm1==lm2
  // returns 1 if lm1 contains lm2
  // returns 2 if lm2 contains lm1
  int rur_compare(polymod & lm1,polymod & lm2){
    unsigned s1=unsigned(lm1.coord.size()),s2=unsigned(lm2.coord.size());
    if (s1==s2){
      if (lm1==lm2)
	return 0;
      return -1;
    }
    if (s1>s2){
      unsigned i=0;
      for (unsigned j=0;j<s2;++i,++j){
	for (;i<s1;++i){
	  if (lm1.coord[i].u==lm2.coord[j].u)
	    break;
	}
	if (i==s1)
	  return -1;
      }
      return 1;
    }
    unsigned j=0;
    for (unsigned i=0;i<s1;++i,++j){
      for (;j<s2;++j){
	if (lm1.coord[i].u==lm2.coord[j].u)
	  break;
      }
      if (j==s2)
	return -1;
    }
    return 2;
  }

  /* ******************
     END RUR UTILITIES 
     ****************** */

#ifdef HAVE_LIBPTHREAD
  struct thread_gbasis_t {
    vectpoly8 current;
    vectpolymod resmod;
    vector<unsigned> G;
    int p;
    vector< pair<unsigned,unsigned> > * reduceto0;
    vector< info_t > * f4buchberger_info;
    vector< zinfo_t > * zf4buchberger_info;
    bool zdata;
  };
  
  void * thread_gbasis(void * ptr_){
    thread_gbasis_t * ptr=(thread_gbasis_t *) ptr_;
    ptr->G.clear();
    if (ptr->zdata){
      if (!zgbasis(ptr->current,ptr->resmod,ptr->G,ptr->p,true,
		   ptr->reduceto0,*ptr->zf4buchberger_info,false,false))
	return 0;
    }
    else {
      if (!in_gbasisf4buchbergermod(ptr->current,ptr->resmod,ptr->G,ptr->p,true/*totaldeg*/,
				    ptr->reduceto0,ptr->f4buchberger_info,false))
	return 0;
    }
    return ptr_;
  }
#endif

  static bool check_initial_generators(vectpoly8 & res,const vectpoly8 & Wi,vector<unsigned> & G,double eps){
    int initial=int(res.size());
    if (debug_infolevel)
      CERR << CLOCK() << " begin final check, checking " << initial << " generators" << endl;
    poly8 tmp0,tmp1,tmp2;
    vectpoly8 wtmp;
    unsigned j=0,finalchecks=initial;
    if (eps>0)
      finalchecks=giacmin(2*Wi.front().dim,initial);
    G.resize(Wi.size());
    for (j=0;j<Wi.size();++j)
      G[j]=j;
    for (j=0;j<finalchecks;++j){
      if (debug_infolevel)
	CERR << "+";
      sort(res[j].coord.begin(),res[j].coord.end(),tdeg_t_sort_t(res[j].order));
      reduce(res[j],Wi,G,-1,wtmp,tmp0,tmp1,tmp2,0);
      if (!tmp0.coord.empty()){
	break;
      }
      if (debug_infolevel && (j%10==9))
	CERR << j+1 << endl;
    }
    CERR << endl;
    if (j!=finalchecks){
      if (debug_infolevel){
	CERR << CLOCK() << " final check failure, retrying with another prime " << endl;
	CERR << "Non-zero remainder " << tmp0 << endl;
	CERR << "checking res[j], " << j << "<" << initial << endl;
	CERR << "res[j]=" << res[j] << endl;
	CERR << "basis candidate " << Wi << endl;
      }
      return false;
    }
    return true;
  }

  bool mod_gbasis(vectpoly8 & res,bool modularcheck,bool zdata,bool & rur,GIAC_CONTEXT){
    unsigned initial=unsigned(res.size());
    double eps=proba_epsilon(contextptr); int rechecked=0;
    order_t order={0,0};
    for (unsigned i=0;i<res.size();++i){
      const poly8 & P=res[i];
      order=P.order;
      for (unsigned j=0;j<P.coord.size();++j){
	if (!is_integer(P.coord[j].g)) // improve: accept complex numbers
	  return false;
      }
    }
    if (order.o!=_REVLEX_ORDER && order.o!=_3VAR_ORDER && order.o!=_7VAR_ORDER && order.o!=_11VAR_ORDER && order.o!=_16VAR_ORDER && order.o!=_32VAR_ORDER && order.o!=_64VAR_ORDER)
      return false;
    // if (order!=_REVLEX_ORDER) zdata=false;
    vectpoly8 current,vtmp,afewpolys;
    vectpolymod resmod,gbmod;
    poly8 poly8tmp;
#ifdef EMCC
    // make first iteration with a smaller prime to spare memory
    // next iterations will use larger primes
    gen p=(1<<24)-_floor(giac_rand(contextptr)/32e3,contextptr);
#else
    gen p=(1<<29)-_floor(giac_rand(contextptr)/1e3,contextptr);
#endif
    // unless we are unlucky these lists should contain only 1 element
    vector< vectpoly8> V; // list of (chinrem reconstructed) modular groebner basis
    vector< vectpoly8> W; // list of rational reconstructed groebner basis
    vector< vectpoly8> Wlast;
    vecteur P; // list of associate (product of) modulo
    polymod lmmod,lmmodradical,s; vectpolymod rurv; // variables for rational univar. reconstr.
    // environment env;
    // env.moduloon=true;
    vector<unsigned> G;
    vector< pair<unsigned,unsigned> > reduceto0;
    vector< info_t > f4buchberger_info;
    f4buchberger_info.reserve(256);
    vector<zinfo_t> zf4buchberger_info;
    zf4buchberger_info.reserve(256);
    mpz_t zu,zd,zu1,zd1,zabsd1,zsqrtm,zq,zur,zr,ztmp;
    mpz_init(zu);
    mpz_init(zd);
    mpz_init(zu1);
    mpz_init(zd1);
    mpz_init(zabsd1);
    mpz_init(zsqrtm);
    mpz_init(zq);
    mpz_init(zur);
    mpz_init(zr);
    mpz_init(ztmp);
    bool ok=true;
#ifdef HAVE_LIBPTHREAD
    int nthreads=threads_allowed?threads:1,th;
    pthread_t tab[32];
    thread_gbasis_t gbasis_param[32];
#else
    int nthreads=1,th;
#endif
    int pend=p.val,p0;
    for (int count=0;ok;++count){
      p=pend;
      if (count==0 || nthreads==1)
	th=0;
      else
	th=giacmin(nthreads-1,32); // no more than 32 threads
#ifndef EMCC
      if (count==1 && p.val<(1<<24)){
#ifdef PSEUDO_MOD
	p=(1<<29)-1;
#else
	p=(1<<30)+((1<<30)-1);
#endif
      }
#endif
      p=prevprime(p-1); 
      p0=p.val; // 1st prime used by all threads
      // compute gbasis mod p 
      // env.modulo=p;
#ifdef HAVE_LIBPTHREAD
      for (unsigned j=0;j<th;++j){
	gbasis_param[j].current=res;
	gbasis_param[j].p=p.val;
	gbasis_param[j].reduceto0=&reduceto0;
	gbasis_param[j].f4buchberger_info=&f4buchberger_info;
	gbasis_param[j].zf4buchberger_info=&zf4buchberger_info;
	gbasis_param[j].zdata=zdata;
	if (count==1)
	  gbasis_param[j].resmod.reserve(resmod.size());
	bool res=true;
	// CERR << "write " << j << " " << p << endl;
	res=pthread_create(&tab[j],(pthread_attr_t *) NULL,thread_gbasis,(void *) &gbasis_param[j]);
	if (res)
	  thread_gbasis((void *)&gbasis_param[j]);
#if 1
	p=prevprime(p-1); 
#else
	p=nextprime(p+1);
#endif	
      }
#endif // thread
      current=res;
      G.clear();
      if (debug_infolevel)
	CERR << std::setprecision(15) << CLOCK() << " begin computing basis modulo " << p << endl;
      // CERR << "write " << th << " " << p << endl;
#ifdef GBASIS_F4BUCHBERGER 
      if (zdata){
	if (!zgbasis(current,resmod,G,p.val,true,&reduceto0,zf4buchberger_info,false,false)){
	  reduceto0.clear();
	  zf4buchberger_info.clear();
	  zf4buchberger_info.reserve(4*zf4buchberger_info.capacity());
	  if (!zgbasis(current,resmod,G,p.val,true/*totaldeg*/,&reduceto0,zf4buchberger_info,false,false)){
	    ok=false;
	    break;
	  }
	}
      }
      else {
	if (!in_gbasisf4buchbergermod(current,resmod,G,p.val,true/*totaldeg*/,
				      //		  0,0
				      &reduceto0,&f4buchberger_info,
#if 1
				      false /* not useful */
#else
				      (count==1) /* recompute R and quo at 2nd iteration*/
#endif
				      )){
	  // retry 
	  reduceto0.clear();
	  f4buchberger_info.clear();
	  if (!in_gbasisf4buchbergermod(current,resmod,G,p.val,true/*totaldeg*/,&reduceto0,&f4buchberger_info,false)){
	    ok=false;
	    break;
	  }
	}
      }
#else
      if (!in_gbasismod(current,resmod,G,p.val,true,&reduceto0)){
	ok=false;
	break;
      }
      // CERR << "reduceto0 " << reduceto0.size() << endl;
      //if (!in_gbasis(current,G,&env)) return false;
#endif
      pend=p.val; // last prime used
      for (unsigned i=0;i<G.size();++i){
	if (resmod[G[i]].coord.empty()){
	  G.erase(G.begin()+i);
	  --i;
	}
      }
      if (debug_infolevel){
	CERR << CLOCK() << " end, basis size " << G.size() << " prime number " << count+1 << endl;
	if (count==0)
	  CERR << "G=" << G << endl;
      }
      unsigned i=0;
      for (int t=0;t<=th;++t){
	if (t==th){
	  // extract from current
	  if (gbmod.size()<G.size())
	    gbmod.resize(G.size());
	  for (i=0;i<G.size();++i){
	    gbmod[i]=resmod[G[i]];
	  }
	  p=pend;
	  // CERR << "read " << t << " " << p << endl;
	}
#ifdef HAVE_LIBPTHREAD
	else {
	  void * ptr_=(void *)&nthreads; // non-zero initialisation
	  pthread_join(tab[t],&ptr_);
	  if (!ptr_)
	    continue;
	  thread_gbasis_t * ptr = (thread_gbasis_t *) ptr_;
	  // extract from current
	  if (gbmod.size()<ptr->G.size())
	    gbmod.resize(ptr->G.size());
	  for (i=0;i<ptr->G.size();++i)
	    gbmod[i]=ptr->resmod[ptr->G[i]];
	  p=ptr->p;
	  // CERR << "read " << t << " " << p << endl;
	  ++count;
	}
#endif
	if (!ok)
	  continue;
	// remove 0 from gbmod
	for (i=0;i<gbmod.size();){
	  if (gbmod[i].coord.empty())
	    gbmod.erase(gbmod.begin()+i);
	  else
	    ++i;
	}
	// compare gb to existing computed basis
#if 1
	if (rur){
	  gbmod.resize(G.size());
	  polymod lmtmp(lmmodradical.order,lmmodradical.dim);
	  // FIXME rur_quotient_ideal etc. should take care of parameters!
	  if (rur_quotient_ideal_dimension(gbmod,lmtmp)<0){
	    rur=false;
	    continue;
	  }
	  if (debug_infolevel)
	    CERR << CLOCK() << " begin modular rur computation" << endl;
	  if (!rur_compute(gbmod,lmtmp,lmmodradical,p.val,s,rurv)){
	    ok=rur=false;
	    continue;
	  }
	  if (debug_infolevel)
	    CERR << CLOCK() << " end modular rur computation" << endl;
	  if (lmmodradical.coord.empty())
	    lmmodradical=lmtmp;
	  else {
	    int i=rur_compare(lmmodradical,lmtmp);
	    if (i!=0){
	      if (i==1) // lmmodradical!=lmtmp and contains lmtmp, bad prime
		continue;
	      // clear existing reconstruction
	      f4buchberger_info.clear();
	      zf4buchberger_info.clear();
	      reduceto0.clear();
	      V.clear(); W.clear(); Wlast.clear(); P.clear();
	      if (i==-1)
		continue;
	      // restart with this prime
	    }
	  }
	  gbmod.swap(rurv); // reconstruct the rur instead of the gbasis
	} // end if (rur)
	unsigned jpos; gen num,den; 
	if (debug_infolevel>2)
	  CERR << "p=" << p << ":" << gbmod << endl;
	for (i=0;i<V.size();++i){
	  if (W.size()<V.size())
	    W.resize(V.size());
	  if (Wlast.size()<V.size())
	    Wlast.resize(V.size());
	  if (V[i].size()!=gbmod.size())
	    continue;
	  for (jpos=0;jpos<gbmod.size();++jpos){
	    if (V[i][jpos].coord.empty() && gbmod[jpos].coord.empty())
	      continue;
	    if (V[i][jpos].coord.empty())
	      break;
	    if (gbmod[jpos].coord.empty())
	      break;
	    if (V[i][jpos].coord.front().u!=gbmod[jpos].coord.front().u)
	      break;
	  }
	  if (jpos!=gbmod.size()){
	    rechecked=0;
	    continue;
	  }
	  jpos=0;
	  // check existing Wlast
	  for (;jpos<Wlast[i].size();++jpos){
	    if (!chk_equal_mod(Wlast[i][jpos],gbmod[jpos],p.val)){
	      Wlast[i].resize(jpos);
	      rechecked=0;
	      break;
	    }
	  }
	  if (jpos!=Wlast[i].size() || P[i].type==_INT_){
	    // CERR << jpos << endl;
	    if (eps>0 && P[i].type==_INT_){
	      // check for non modular gb with early reconstruction */
	      // first build a candidate in early with V[i]
	      vectpoly8 early(V[i]);
	      int d;
	      for (jpos=0;jpos<early.size();++jpos){
		d=1;
		if (!findmultmod(early[jpos],P[i].val,d)){
		  if (debug_infolevel>1)
		    COUT << "early reconstr. failure pos " << jpos << " P=" << early[jpos] << " d=" << d << " modulo " << P[i].val << endl;
		  break;
		}
		int s=int(early[jpos].coord.size());
		for (int k=0;k<s;++k){
		  early[jpos].coord[k].g=smod(longlong(early[jpos].coord[k].g.val)*d,P[i].val);
		}
	      }
	      // then check
	      if (jpos==early.size()){
		for (jpos=0;jpos<early.size();++jpos){
		  polymod tmp(gbmod[jpos]);
		  smallmultmod(early[jpos].coord.front().g.val,tmp,p.val);
		  if (!chk_equal_mod(early[jpos],tmp,p.val)){
		    if (debug_infolevel>1)
		      COUT << "early recons. failure jpos=" << jpos << " " << early[jpos] << " " << tmp << " modulo " << p.val << endl;
		    break;
		  }
		}
		rechecked=0; 
		if (jpos==early.size() && check_initial_generators(res,early,G,eps)){
		  if (debug_infolevel)
		    CERR << CLOCK() << " end final check " << endl;
		  swap(res,early);
		  mpz_clear(zd);
		  mpz_clear(zu);
		  mpz_clear(zu1);
		  mpz_clear(zd1);
		  mpz_clear(zabsd1);
		  mpz_clear(zsqrtm);
		  mpz_clear(zq);
		  mpz_clear(zur);
		  mpz_clear(zr);
		  mpz_clear(ztmp);
#ifdef HAVE_LIBPTHREAD
		  // finish other threads
		  void * ptr_;
		  for (;t<th;++t)
		    pthread_join(tab[t],&ptr_);
#endif
		  return true;
		}
	      } // end jpos==early.size()
	    }
	    break; // find another prime
	  }
	  for (;jpos<V[i].size();++jpos){
	    unsigned Vijs=unsigned(V[i][jpos].coord.size());
	    if (Vijs!=gbmod[jpos].coord.size()){
	      rechecked=0;
	      if (debug_infolevel>1)
		CERR << jpos << endl;
	      break;
	    }
	    //Vijs=1; 
	    Vijs/=2;
	    if (Vijs && V[i][jpos].coord[Vijs].g.type==_ZINT){
	      if (!in_fracmod(P[i],V[i][jpos].coord[Vijs].g,
			      zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,num,den)){
		rechecked=0;
		if (debug_infolevel>1)
		  CERR << jpos << endl;
		break;
	      }
	      modint gg=gbmod[jpos].coord[Vijs].g;
	      if (!chk_equal_mod(num/den,gg,p.val)){
		rechecked=0;
		if (debug_infolevel>1)
		  CERR << jpos << endl;
		break;
	      }
	    }
	    if (!fracmod(V[i][jpos],P[i],
			 zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,
			 poly8tmp)){
	      rechecked=0;
	      CERR << CLOCK() << " reconstruction failure at position " << jpos << endl;
	      break;
	    }
	    if (rur && !poly8tmp.coord.empty() && !chk_equal_mod(poly8tmp.coord.front().g,gbmod[jpos].coord.front().g,p.val)){
	      rechecked=0;
	      break;
	    }
	    if (!chk_equal_mod(poly8tmp,gbmod[jpos],p.val)){
	      rechecked=0;
	      break;
	    }
	    poly8 tmptmp(poly8tmp.order,poly8tmp.dim);
	    Wlast[i].push_back(tmptmp);
	    Wlast[i].back().coord.swap(poly8tmp.coord);
	  }
	  if (debug_infolevel>0)
	    CERR << CLOCK() << " unstable mod " << p << " from " << V[i].size() << " reconstructed " << Wlast[i].size() << " (#" << i << ")" << endl;
	  break;
	} // end for loop on i
	if (i==V.size()){
	  if (debug_infolevel)
	    CERR << CLOCK() << " creating reconstruction #" << i << endl;
	  // not found
	  V.push_back(vectpoly8());
	  convert(gbmod,V.back(),p.val);
	  W.push_back(vectpoly8()); // no reconstruction yet, wait at least another prime
	  Wlast.push_back(vectpoly8());
	  P.push_back(p);
	  continue; // next prime
	}
	if (jpos<gbmod.size()){
	  if (debug_infolevel)
	    CERR << CLOCK() << " i= " << i << " begin chinese remaindering " << p << endl;
	  int r=chinrem(V[i],P[i],gbmod,p.val,poly8tmp);
	  if (debug_infolevel)
	    CERR << CLOCK() << " end chinese remaindering" << endl;
	  if (r==-1){
	    ok=false;
	    continue;
	  }
	  P[i]=P[i]*p;
	  continue; // next prime
	}
	else { // final check
	  W[i]=Wlast[i];
	  if (!rur){
	    if (debug_infolevel)
	      CERR << CLOCK() << " stable, clearing denominators " << endl;
	    cleardeno(W[i]); // clear denominators
	  }
	  ++rechecked;
	  if (debug_infolevel)
	    CERR << CLOCK() << " end rational reconstruction " << endl;
	  // now check if W[i] is a Groebner basis over Q, if so it's the answer
	  if (rur){ 
	    // a final check could be performed by replacing
	    // res[3..end]/res[2] in the initial gbasis element and check if it's 0 
	    swap(res,W[i]);
	    mpz_clear(zd);
	    mpz_clear(zu);
	    mpz_clear(zu1);
	    mpz_clear(zd1);
	    mpz_clear(zabsd1);
	    mpz_clear(zsqrtm);
	    mpz_clear(zq);
	    mpz_clear(zur);
	    mpz_clear(zr);
	    mpz_clear(ztmp);
#ifdef HAVE_LIBPTHREAD
	    // finish other threads
	    void * ptr_;
	    for (;t<th;++t)
	      pthread_join(tab[t],&ptr_);
#endif
	    return true;
	  }
	  // first verify that the initial generators reduce to 0
	  if (!check_initial_generators(res,W[i],G,eps))
	    continue;
	  if (int(W[i].size())<=GBASIS_DETERMINISTIC)
	    eps=0;
	  double eps2=std::pow(double(p.val),double(rechecked))*eps;
	  // recheck by computing gbasis modulo another prime
	  if (eps2>0 && eps2<1)
	    continue;
	  if (eps>0){
	    double terms=0;
	    int termsmin=RAND_MAX; // estimate of the number of terms of a reduced non-0 spoly
	    for (unsigned k=0;k<W[i].size();++k){
	      terms += W[i][k].coord.size();
	      termsmin = giacmin(termsmin,unsigned(W[i][k].coord.size()));
	    }
	    termsmin = 7*(2*termsmin-1);
	    int epsp=mpz_sizeinbase(*P[i]._ZINTptr,10)-int(std::ceil(2*std::log10(terms)));
	    if (epsp>termsmin)
	      epsp=termsmin;
	    *logptr(contextptr) << gettext("Running a probabilistic check for the reconstructed Groebner basis. If successfull, error probability is less than ") << eps << gettext(" and is estimated to be less than 10^-") << epsp << gettext(". Use proba_epsilon:=0 to certify (this takes more time).") << endl;
	  }
	  G.clear();
	  if (eps2<1 && !is_gbasis(W[i],eps2,modularcheck)){
	    ok=false;
	    continue; // in_gbasis(W[i],G,0,true);
	  }
	  if (debug_infolevel)
	    CERR << CLOCK() << " end final check " << endl;
	  swap(res,W[i]);
	  mpz_clear(zd);
	  mpz_clear(zu);
	  mpz_clear(zu1);
	  mpz_clear(zd1);
	  mpz_clear(zabsd1);
	  mpz_clear(zsqrtm);
	  mpz_clear(zq);
	  mpz_clear(zur);
	  mpz_clear(zr);
	  mpz_clear(ztmp);
#ifdef HAVE_LIBPTHREAD
	  // finish other threads
	  void * ptr_;
	  for (;t<th;++t)
	    pthread_join(tab[t],&ptr_);
#endif
	  return true;
	}
#else
	for (i=0;i<V.size();++i){
	  if (debug_infolevel)
	    CERR << CLOCK() << " i= " << i << " begin chinese remaindering" << endl;
	  int r=chinrem(V[i],P[i],gbmod,p.val,poly8tmp);
	  if (debug_infolevel)
	    CERR << CLOCK() << " end chinese remaindering" << endl;
	  if (r==-1){
	    ok=false;
	    break;
	  }
	  if (r==0){
	    CERR << CLOCK() << " leading terms do not match with reconstruction " << i << " modulo " << p << endl;
	    continue;
	  }
	  // found one! V is already updated, update W
	  if (W.size()<V.size())
	    W.resize(V.size());
	  if (Wlast.size()<V.size())
	    Wlast.resize(V.size());
	  P[i]=P[i]*p;
	  unsigned jpos=0;
	  // afewpolys.clear();
	  for (;jpos<V[i].size();++jpos){
	    if (int(Wlast[i].size())>jpos && chk_equal_mod(Wlast[i][jpos],gb[jpos],p.val)){
	      if (afewpolys.size()<=jpos)
		afewpolys.push_back(Wlast[i][jpos]);
	      else {
		if (!(afewpolys[jpos]==Wlast[i][jpos]))
		  afewpolys[jpos]=Wlast[i][jpos];
	      }
	    }
	    else {
	      if (!fracmod(V[i][jpos],P[i],
			   zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,
			   poly8tmp)){
		CERR << CLOCK() << " reconstruction failure at position " << jpos << endl;
		break;
	      }
	      if (afewpolys.size()<=jpos){
		poly8 tmp(poly8tmp.order,poly8tmp.dim);
		afewpolys.push_back(tmp);
	      }
	      afewpolys[jpos].coord.swap(poly8tmp.coord);
	    }
	    if (Wlast[i].size()>jpos && !(afewpolys[jpos]==Wlast[i][jpos])){
	      if (debug_infolevel){
		unsigned j=0,js=giacmin(afewpolys[jpos].coord.size(),Wlast[i][jpos].coord.size());
		for (;j<js;++j){
		  if (!(afewpolys[jpos].coord[j]==Wlast[i][jpos].coord[j]))
		    break;
		}
		CERR << "Diagnostic: chinrem reconstruction mismatch at positions " << jpos << "," << j << endl;
		if (j<js)
		  CERR << gb[jpos].coord[j].g << "*" << gb[jpos].coord[j].u << endl;
		else
		  CERR << afewpolys[jpos].coord.size() << "," << Wlast[i][jpos].coord.size() << endl;
	      }
	      afewpolys.resize(jpos+1);
	      break;
	    }
	    if (jpos > Wlast[i].size()*1.35+2 )
	      break;
	  }
	  if (afewpolys!=Wlast[i]){
	    swap(afewpolys,Wlast[i]);
	    if (debug_infolevel>0)
	      CERR << CLOCK() << " unstable mod " << p << " from " << V[i].size() << " reconstructed " << Wlast[i].size() << endl;
	    break;
	  }
	  if (debug_infolevel)
	    CERR << CLOCK() << " stable, clearing denominators " << endl;
	  W[i]=Wlast[i];
	  cleardeno(W[i]); // clear denominators
	  if (debug_infolevel)
	    CERR << CLOCK() << " end rational reconstruction " << endl;
	  // now check if W[i] is a Groebner basis over Q, if so it's the answer
	  if (debug_infolevel)
	    CERR << CLOCK() << " begin final check, checking " << initial << " generators " << endl;
	  // first verify that the initial generators reduce to 0
	  poly8 tmp0,tmp1,tmp2;
	  vectpoly8 wtmp;
	  unsigned j=0,finalchecks=initial;
	  if (eps>0)
	    finalchecks=giacmin(2*W[i].front().dim,initial);
	  G.resize(W[i].size());
	  for (j=0;j<W[i].size();++j)
	    G[j]=j;
	  for (j=0;j<finalchecks;++j){
	    if (debug_infolevel)
	      CERR << "+";
	    reduce(res[j],W[i],G,-1,wtmp,tmp0,tmp1,tmp2,0);
	    if (!tmp0.coord.empty()){
	      break;
	    }
	    if (debug_infolevel	&& (j%10==9))
	      CERR << j+1 << endl;
	  }
	  if (j!=finalchecks){
	    if (debug_infolevel){
	      CERR << CLOCK() << " final check failure, retrying with another prime " << endl;
	      CERR << "Non-zero remainder " << tmp0 << endl;
	      CERR << "checking res[j], " << j << "<" << initial << endl;
	      CERR << "res[j]=" << res[j] << endl;
	      CERR << "basis candidate " << W[i] << endl;
	    }
	    break;
	}
	  /* (final check requires that we have reconstructed a Groebner basis,
	     Modular algorithms for computing Groebner bases Elizabeth A. Arnold
	     Journal of Symbolic Computation 35 (2003) 403–419)
	  */
#if 1
	  if (int(W[i].size())<=GBASIS_DETERMINISTIC)
	    eps=0;
	  if (eps>0){
	    double terms=0;
	    int termsmin=RAND_MAX; // estimate of the number of terms of a reduced non-0 spoly
	    for (unsigned k=0;k<W[i].size();++k){
	      terms += W[i][k].coord.size();
	      termsmin = giacmin(termsmin,W[i][k].coord.size());
	    }
	    termsmin = 7*(2*termsmin-1);
	    int epsp=mpz_sizeinbase(*P[i]._ZINTptr,10)-int(std::ceil(2*std::log10(terms)));
	    if (epsp>termsmin)
	      epsp=termsmin;
	    *logptr(contextptr) << gettext("Running a probabilistic check for the reconstructed Groebner basis. If successfull, error probability is less than ") << eps << gettext(" and is estimated to be less than 10^-") << epsp << gettext(". Use proba_epsilon:=0 to certify (this takes more time).") << endl;
	  }
	  G.clear();
	  if (eps<6e-8 && !is_gbasis(W[i],eps*1.677e7,modularcheck)){
	    ok=false;
	    break; // in_gbasis(W[i],G,0,true);
	  }
#endif
	  if (debug_infolevel)
	    CERR << CLOCK() << " end final check " << endl;
	  swap(res,W[i]);
	  mpz_clear(zd);
	  mpz_clear(zu);
	  mpz_clear(zu1);
	  mpz_clear(zd1);
	  mpz_clear(zabsd1);
	  mpz_clear(zsqrtm);
	  mpz_clear(zq);
	  mpz_clear(zur);
	  mpz_clear(zr);
	  mpz_clear(ztmp);
#ifdef HAVE_LIBPTHREAD
	  // finish other threads
	  void * ptr_;
	  for (;t<th;++t)
	    pthread_join(tab[t],&ptr_);
#endif
	  return true;
	} // end for (i<V.size())
	if (i==V.size()){
	  if (debug_infolevel)
	    CERR << CLOCK() << " creating reconstruction #" << i << endl;
	  // not found
	  V.push_back(gb);
	  W.push_back(vectpoly8()); // no reconstruction yet, wait at least another prime
	  Wlast.push_back(vectpoly8());
	  P.push_back(p);
	}
#endif
      } // end loop on threads
    } //end for int count
    mpz_clear(zd);
    mpz_clear(zu);
    mpz_clear(zu1);
    mpz_clear(zd1);
    mpz_clear(zabsd1);
    mpz_clear(zsqrtm);
    mpz_clear(zq);
    mpz_clear(zur);
    mpz_clear(zr);
    mpz_clear(ztmp);
    return false;
  }
  
  bool gbasis8(const vectpoly & v,order_t & order,vectpoly & newres,environment * env,bool modularalgo,bool modularcheck,bool & rur,GIAC_CONTEXT){
    vectpoly8 res;
    vectpolymod resmod;
    vector<unsigned> G;
    vectpoly_2_vectpoly8(v,order,res);
    // Temporary workaround until rur_compute support parametric rur
    if (rur && absint(order.o)!=-_RUR_REVLEX){ 
      rur=false;
      order.o=absint(order.o);
    }
    if (modularalgo && (!env || env->modulo==0 || env->moduloon==false)){
      if (mod_gbasis(res,modularcheck,
		     //order.o==_REVLEX_ORDER /* zdata*/,
		     !rur /* zdata*/,
		     rur,contextptr)){
	newres=vectpoly(res.size(),polynome(v.front().dim,v.front()));
	for (unsigned i=0;i<res.size();++i)
	  res[i].get_polynome(newres[i]);
	return true;
      }
    }
    if (env && env->moduloon && env->modulo.type==_INT_){
#ifdef GBASIS_F4BUCHBERGER
      if (!res.empty() && (res.front().order.o==_REVLEX_ORDER || res.front().order.o==_3VAR_ORDER || res.front().order.o==_7VAR_ORDER || res.front().order.o==_11VAR_ORDER)){
	vector<zinfo_t> f4buchberger_info;
	f4buchberger_info.reserve(256);
	zgbasis(res,resmod,G,env->modulo.val,true/*totaldeg*/,0,f4buchberger_info,false/* recomputeR*/,false /* don't compute res8*/);	
	newres=vectpoly(G.size(),polynome(v.front().dim,v.front()));
	for (unsigned i=0;i<G.size();++i)
	  resmod[G[i]].get_polynome(newres[i]);
	return true;
      }
      else
	in_gbasisf4buchbergermod(res,resmod,G,env->modulo.val,true/*totaldeg*/,0,0,false);
#else
      in_gbasismod(res,resmod,G,env->modulo.val,true,0);
#endif
      if (debug_infolevel)
	CERR << "G=" << G << endl;
    }
    else {
#ifdef GIAC_REDUCEMODULO
      vectpoly w(v);
      reduce(w,env);
      sort_vectpoly(w.begin(),w.end());
      vectpoly_2_vectpoly8(w,order,res);
#endif
      in_gbasis(res,G,env,true);
    }
    newres=vectpoly(G.size(),polynome(v.front().dim,v.front()));
    for (unsigned i=0;i<G.size();++i)
      res[G[i]].get_polynome(newres[i]);
    return true;
  }

  bool greduce8(const vectpoly & v,const vectpoly & gb_,order_t & order,vectpoly & newres,environment * env,GIAC_CONTEXT){
    vectpoly8 red,gb,quo;
    vectpoly_2_vectpoly8(v,order,red);
    vectpoly_2_vectpoly8(gb_,order,gb);
    poly8 rem,TMP1,TMP2;
    vector<unsigned> G(gb_.size());
    for (int i=0;i<int(gb_.size());++i)
      G[i]=i;
    int dim;
    for (int i=0;i<int(v.size());++i){
      quo.clear();
      rem.coord.clear();
      dim=red[i].dim;
      // adjust constant coeff to 1
      gen c_in,c_out;
      index_t idx;
      if (!red[i].coord.empty()){
	get_index(red[i].coord.back().u,idx,order,dim);
	if (is_zero(idx)){
	  c_in=red[i].coord.back().g;
	  red[i].coord.back().g=1;
	}
	else {
	  red[i].coord.push_back(T_unsigned<gen,tdeg_t>(1,tdeg_t(dim,order)));
	}
      }
      if (debug_infolevel>1)
	COUT << CLOCK() << " begin reduce poly no " << i << " #monomials " << red[i].coord.size() << endl;
      reduce(red[i],gb,G,-1,quo,rem,TMP1,TMP2,env);
      if (debug_infolevel>1)
	COUT << CLOCK() << " end reduce poly no " << i << " #monomials " << rem.coord.size() << endl;
      get_index(rem.coord.back().u,idx,order,dim);
      if (is_zero(idx))
	c_out=rem.coord.back().g;
      else 
	return false;
      for (int j=0;j<int(rem.coord.size());++j){
	rem.coord[j].g=rem.coord[j].g/c_out;
      }
      if (is_zero(c_in))
	rem.coord.pop_back();
      else
	rem.coord.back().g=c_in;
      red[i]=rem;
    }
    newres=vectpoly(v.size(),polynome(v.front().dim,v.front()));
    for (unsigned i=0;i<int(v.size());++i)
      red[i].get_polynome(newres[i]);
    return true;
  }

#endif // CAS38_DISABLED

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
