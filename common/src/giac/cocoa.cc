/* -*- mode:C++ ; compile-command: "g++ -I.. -I../include -I.. -g -c -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DHAVE_CONFIG_H -DIN_GIAC -Wall cocoa.cc" -*- */
#include "giacPCH.h"

#ifndef WIN32
#define COCOA9950
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
 *  Copyright (C) 2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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

#include <iostream>
#include "cocoa.h"
#include "gausspol.h"
#include "identificateur.h"
#include "giacintl.h"
#include "index.h"

#if defined(USE_GMP_REPLACEMENTS) || defined(GIAC_VECTOR)
#undef HAVE_LIBCOCOA
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

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
  // #define GBASIS_XOR
#define GBASIS_SWAP 
  // minimal numbers of pair to reduce simultaneously with f4
#define GBASIS_F4 5
#define GBASIS_POSTF4 0 // 0 means final simplification at the end, 1 at each loop
  // if GIAC_SHORTSHIFTTYPE is defined, sparse matrix is using shift index
  // coded on 1 or 2 bytes
  //#define GIAC_SHORTSHIFTTYPE 8
  //#define GIAC_SHORTSHIFTTYPE 16

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

  struct tdeg_t {
    short tab[GROEBNER_VARS+1];
    unsigned total_degree() const {
      // works only for revlex and tdeg
#ifdef GBASIS_XOR
      return tab[GROEBNER_VARS];
#else
      return tab[0];
#endif
    }
    void set_total_degree(unsigned d) {
      // works only for revlex and tdeg
#ifdef GBASIS_XOR
      tab[GROEBNER_VARS]=d;
#else
      tab[0]=d;
#endif
    }
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
      for (unsigned i=0;i<=GROEBNER_VARS;++i)
	ptr[i]=tab[i];
#ifdef GBASIS_SWAP
      swap_indices(ptr);
#endif
    }
#ifdef GBASIS_XOR // put total degree at the end and partial degree from the end
    tdeg_t(const index_m & lm,short order){ 
      longlong * ptr_ = (longlong *) tab;
      ptr_[2]=ptr_[1]=ptr_[0]=0;
      short * ptr=tab+GROEBNER_VARS;
      if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
	*ptr=sum_degree(lm);
	--ptr;
      }
      vector<deg_t>::const_iterator it=lm.begin(),itend=lm.end();
      if (order==_REVLEX_ORDER){
	for (--itend,--it;it!=itend;--ptr,--itend)
	  *ptr=-*itend;
      }
      else {
	for (;it!=itend;--ptr,++it)
	  *ptr=*it;
      }
    }
#else
    tdeg_t(const index_m & lm,short order){ 
      longlong * ptr_ = (longlong *) tab;
      ptr_[2]=ptr_[1]=ptr_[0]=0;
#if GROEBNER_VARS>11
      ptr_[3]=0;
#endif
      // tab[GROEBNER_VARS]=order;
      short * ptr=tab;
      if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
	*ptr=sum_degree(lm);
	++ptr;
      }
      vector<deg_t>::const_iterator it=lm.begin(),itend=lm.end();
      if (order==_REVLEX_ORDER){
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
    }
#endif
  };

  typedef map<tdeg_t,unsigned> annuaire;

  ostream & operator << (ostream & os,const tdeg_t & x){
    os << "[";
    for (unsigned i=0; i<=GROEBNER_VARS;++i){
      os << x.tab[i] << ",";
    }
    return os << "]";
  }
  inline void add(const tdeg_t & x,const tdeg_t & y,tdeg_t & res,int dim){
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
  inline tdeg_t operator + (const tdeg_t & x,const tdeg_t & y){
    tdeg_t res;
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
  inline tdeg_t & operator += (tdeg_t & x,const tdeg_t & y){ 
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
  inline tdeg_t operator - (const tdeg_t & x,const tdeg_t & y){ 
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
    return  ((longlong *) x.tab)[0] == ((longlong *) y.tab)[0] && 
      ((longlong *) x.tab)[1] == ((longlong *) y.tab)[1] &&
      ((longlong *) x.tab)[2] == ((longlong *) y.tab)[2] 
#if GROEBNER_VARS>11
      &&  ((longlong *) x.tab)[3] == ((longlong *) y.tab)[3]
#endif
    ;
  }
  inline bool operator != (const tdeg_t & x,const tdeg_t & y){ 
    return !(x==y);
  }

#ifdef GBASIS_XOR
  bool operator >= (const tdeg_t & x,const tdeg_t & y){
    if (((ulonglong *) x.tab)[2] != ((ulonglong *) y.tab)[2])
      return (((ulonglong *) x.tab)[2]^0x8000800080008000ULL)>=(((ulonglong *) y.tab)[2]^0x8000800080008000ULL);
    if (((ulonglong *) x.tab)[1] != ((ulonglong *) y.tab)[1])
      return (((ulonglong *) x.tab)[1]^0x8000800080008000ULL)>=(((ulonglong *) y.tab)[1]^0x8000800080008000ULL);
    return (((ulonglong *) x.tab)[0]^0x8000800080008000ULL)>=(((ulonglong *) y.tab)[0]^0x8000800080008000ULL);    
  }
#else
  bool operator >= (const tdeg_t & x,const tdeg_t & y){
#ifdef GBASIS_SWAP
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    ulonglong X=*xtab, Y=*ytab;
    if (X!=Y){
      if ( (X & 0xffff) != (Y &0xffff))
	return (X&0xffff)>=(Y&0xffff);
      return X<=Y;
    }
    if (xtab[1]!=ytab[1])
      return xtab[1]<=ytab[1];
    if (xtab[2]!=ytab[2])
      return xtab[2]<=ytab[2];
#if GROEBNER_VARS>11
    return xtab[3]<=ytab[3];
#endif
    return true;
#else
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>=y.tab[0];
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]<=y.tab[1];
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]<=y.tab[2];
      return x.tab[3]<=y.tab[3];
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]<=y.tab[4];
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]<=y.tab[5];
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]<=y.tab[6];
      return x.tab[7]<=y.tab[7];
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]<=y.tab[8];
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]<=y.tab[9];
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]<=y.tab[10];
      return x.tab[11]<=y.tab[11];
    }
#if GROEBNER_VARS>11
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]<=y.tab[12];
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]<=y.tab[13];
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]<=y.tab[14];
      return x.tab[15]<=y.tab[15];
    }
#endif
    return true;
#endif
  }

  inline bool operator >  (const tdeg_t & x,const tdeg_t & y){ return !(y>=x); }
  inline bool operator <  (const tdeg_t & x,const tdeg_t & y){ return !(x>=y); }
#endif // GBASIS_XOR

  inline bool tdeg_t_lex_greater (const tdeg_t & x,const tdeg_t & y){
#ifdef GBASIS_SWAP
    ulonglong *xtab=(ulonglong *)&x,*ytab=(ulonglong *)&y;
    ulonglong X=*xtab, Y=*ytab;
    if (X!=Y){
      if ( (X & 0xffff) != (Y &0xffff))
	return (X&0xffff)>=(Y&0xffff);
      return X>=Y;
    }
    if (xtab[1]!=ytab[1])
      return xtab[1]>=ytab[1];
    if (xtab[2]!=ytab[2])
      return xtab[2]>=ytab[2];
#if GROEBNER_VARS>11
    return xtab[3]>=ytab[3];
#endif
    return true;
#else
    if (((longlong *) x.tab)[0] != ((longlong *) y.tab)[0]){
      if (x.tab[0]!=y.tab[0])
	return x.tab[0]>y.tab[0];
      if (x.tab[1]!=y.tab[1])
	return x.tab[1]>y.tab[1];
      if (x.tab[2]!=y.tab[2])
	return x.tab[2]>y.tab[2];
      return x.tab[3]>y.tab[3];
    }
    if (((longlong *) x.tab)[1] != ((longlong *) y.tab)[1]){
      if (x.tab[4]!=y.tab[4])
	return x.tab[4]>y.tab[4];
      if (x.tab[5]!=y.tab[5])
	return x.tab[5]>y.tab[5];
      if (x.tab[6]!=y.tab[6])
	return x.tab[6]>y.tab[6];
      return x.tab[7]>y.tab[7];
    }
    if (((longlong *) x.tab)[2] != ((longlong *) y.tab)[2]){
      if (x.tab[8]!=y.tab[8])
	return x.tab[8]>y.tab[8];
      if (x.tab[9]!=y.tab[9])
	return x.tab[9]>y.tab[9];
      if (x.tab[10]!=y.tab[10])
	return x.tab[10]>y.tab[10];
      return x.tab[11]>=y.tab[11];
    }
#if GROEBNER_VARS>11
    if (((longlong *) x.tab)[3] != ((longlong *) y.tab)[3]){
      if (x.tab[12]!=y.tab[12])
	return x.tab[12]>y.tab[12];
      if (x.tab[13]!=y.tab[13])
	return x.tab[13]>y.tab[13];
      if (x.tab[14]!=y.tab[14])
	return x.tab[14]>y.tab[14];
      return x.tab[15]>=y.tab[15];
    }
#endif
    return true;
#endif
  }

  inline bool tdeg_t_greater (const tdeg_t & x,const tdeg_t & y,short order){
    if (order==_REVLEX_ORDER)
      return x>=y;
    else
      return tdeg_t_lex_greater(x,y);
  }
  inline bool tdeg_t_strictly_greater (const tdeg_t & x,const tdeg_t & y,short order){
    return !tdeg_t_greater(y,x,order); // total order
  }
#ifdef GBASIS_XOR
  inline bool tdeg_t_all_greater(const tdeg_t & x,const tdeg_t & y,short order){
    if (order==_REVLEX_ORDER){
      for (int i=GROEBNER_VARS-1;i>=0;--i){
	if (x.tab[i]>y.tab[i])
	  return false;
      }
      return true;
    }
    for (int i=GROEBNER_VARS;i>=0;--i){
      if (x.tab[i]<y.tab[i])
	return false;
    }
    return true;
  }
#else
  inline bool tdeg_t_all_greater(const tdeg_t & x,const tdeg_t & y,short order){
#if 1
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
#else
    for (unsigned i=0;i<=GROEBNER_VARS;++i){
      if (x.tab[i]<y.tab[i])
	return false;
    }
#endif
    return true;
  }
#endif

#ifdef GBASIS_XOR
  void index_lcm(const tdeg_t & x,const tdeg_t & y,tdeg_t & z,short order){
    // short order=x.tab[GROEBNER_VARS];
    int t=0;
    if (order==_REVLEX_ORDER){
      for (unsigned i=0;i<GROEBNER_VARS;++i){
	t -= (z.tab[i]=(x.tab[i]<y.tab[i])?x.tab[i]:y.tab[i]);
      }
    }
    else {
      for (unsigned i=0;i<GROEBNER_VARS;++i){
	t += (z.tab[i]=(x.tab[i]>y.tab[i])?x.tab[i]:y.tab[i]);
      }
    }
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      z.tab[GROEBNER_VARS] = t;
    else 
      z.tab[GROEBNER_VARS]=(x.tab[GROEBNER_VARS]>y.tab[GROEBNER_VARS])?x.tab[GROEBNER_VARS]:y.tab[GROEBNER_VARS];
  }

  void get_index(const tdeg_t & x,index_t & idx,int order,int dim){
    idx.resize(dim);
    const short * ptr=x.tab+GROEBNER_VARS;
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      --ptr;
    if (order==_REVLEX_ORDER){
      for (int i=0;i<dim;--ptr,++i)
	idx[i]=-*ptr;
    }
    else {
      for (int i=1;i<=dim;--ptr,++i)
	idx[dim-i]=*ptr;
    }
  }
  bool disjoint(const tdeg_t & a,const tdeg_t & b,short order,short dim){
    const short * it=a.tab+GROEBNER_VARS, * jt=b.tab+GROEBNER_VARS;
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
      --it; --jt;
    }
    const short * itend=it-dim;
    for (;it!=itend;--jt,--it){
      if (*it && *jt)
	return false;
    }
    return true;
  }
  
#else
  void index_lcm(const tdeg_t & x,const tdeg_t & y,tdeg_t & z,short order){
#if 1
    int t=0;
    const short * xtab=&x.tab[1],*ytab=&y.tab[1];
    short *ztab=&z.tab[1];
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
#if GROEBNER_VARS>11
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
    ++xtab; ++ytab; ++ztab;
    t += (*ztab=(*xtab>*ytab)?*xtab:*ytab);
#endif
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      z.tab[0]=t;
    else
      t += (z.tab[0]=(x.tab[0]>y.tab[0])?x.tab[0]:y.tab[0]);
#else
    // short order=x.tab[GROEBNER_VARS];
    int t=0;
    for (unsigned i=0;i<=GROEBNER_VARS;++i){
      t += (z.tab[i]=(x.tab[i]>y.tab[i])?x.tab[i]:y.tab[i]);
    }
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
      z.tab[0] = t-z.tab[0];
    }
#endif
  }

  void get_index(const tdeg_t & x_,index_t & idx,int order,int dim){
    idx.resize(dim);
#ifdef GBASIS_SWAP    
    tdeg_t x(x_);
    swap_indices(x.tab);
#else
    const tdeg_t & x= x_;
#endif
    const short * ptr=x.tab;
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      ++ptr;
    if (order==_REVLEX_ORDER){
      for (int i=1;i<=dim;++ptr,++i)
	idx[dim-i]=*ptr;
    }
    else {
      for (int i=0;i<dim;++ptr,++i)
	idx[i]=*ptr;
    }
  }
  
  bool disjoint(const tdeg_t & a,const tdeg_t & b,short order,short dim){
    const short * it=a.tab, * jt=b.tab;
#ifdef GBASIS_SWAP
    const short * itend=it+GROEBNER_VARS+1;
#endif
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
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
#endif // GBASIS_XOR

  // polynomial are vector< T_unsigned<gen,tdeg_t> >

  struct poly8 {
    std::vector< T_unsigned<gen,tdeg_t> > coord;
    // lex order is implemented using tdeg_t as a list of degrees
    // tdeg uses total degree 1st then partial degree in lex order, max 7 vars
    // revlex uses total degree 1st then opposite of partial degree in reverse ordre, max 7 vars
    short int order; // _PLEX_ORDER, _REVLEX_ORDER or _TDEG_ORDER
    short int dim;
    unsigned sugar;
    void dbgprint() const;
    poly8():order(_PLEX_ORDER),dim(0),sugar(0) {}
    poly8(int o_,int dim_): order(o_),dim(dim_),sugar(0) {}
    poly8(const polynome & p){
      order=-1;
      dim=p.dim;
      if (p.is_strictly_greater==i_lex_is_strictly_greater)
	order=_PLEX_ORDER;
      if (p.is_strictly_greater==i_total_revlex_is_strictly_greater)
	order=_REVLEX_ORDER;
      if (p.is_strictly_greater==i_total_lex_is_strictly_greater)
	order=_TDEG_ORDER;
      if (p.dim>GROEBNER_VARS)
	cerr << "Number of variables is too large to be handled by giac";
      else {
	coord.reserve(p.coord.size());
	for (unsigned i=0;i<p.coord.size();++i){
	  coord.push_back(T_unsigned<gen,tdeg_t>(p.coord[i].value,tdeg_t(p.coord[i].index,order)));
	}
      }
      if (coord.empty())
	sugar=0;
      else
	sugar=coord.front().u.total_degree();
    }
    void get_polynome(polynome & p) const {
      p.dim=dim;
      switch (order){
      case _PLEX_ORDER:
	p.is_strictly_greater=i_lex_is_strictly_greater;
	break;
      case _REVLEX_ORDER:
	p.is_strictly_greater=i_total_revlex_is_strictly_greater;
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

#ifdef GBASIS_XOR
  ostream & operator << (ostream & os, const poly8 & p){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
      switch (p.order){
      case _PLEX_ORDER:
	for (int i=GROEBNER_VARS;i>=0;--i){
	  t2 = it->u.tab[i];
	  if (t2)
	    os << "*x"<< GROEBNER_VARS-i << "^" << t2  ;
	}
	break;
      case _TDEG_ORDER:
	for (int i=GROEBNER_VARS-1;i>=0;--i){
	  t2 = it->u.tab[i];
	  if (t2)
	    os << "*x"<< GROEBNER_VARS-1-i << "^" << t2  ;
	}
	break;
      case _REVLEX_ORDER:
	for (int i=GROEBNER_VARS-1;i>=0;--i){
	  t2 = -it->u.tab[i];
	  if (t2==0)
	    continue;
	  os << "*x"<< p.dim-(GROEBNER_VARS-i);
	  if (t2!=1)
	    os << "^" << t2;
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
#else
  ostream & operator << (ostream & os, const poly8 & p){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
#ifdef GBASIS_SMALL
      signed char tab[GROEBNER_VARS+1];
#else
      short tab[GROEBNER_VARS+1];
#endif
      it->u.get_tab(tab);
      switch (p.order){
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

#endif

  void poly8::dbgprint() const { 
    std::cerr << *this << endl;
  }

  class vectpoly8:public vector<poly8>{
  public:
    void dbgprint() const { cerr << *this << endl; }
  };

  void vectpoly_2_vectpoly8(const vectpoly & v,vectpoly8 & v8){
    v8.clear();
    v8.reserve(v.size());
    for (unsigned i=0;i<v.size();++i){
      v8.push_back(v[i]);
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
      type_operator_times(g,it1->g,it1->g);
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

  bool operator > (const heap_t & a,const heap_t & b){
    return a.u>b.u;
  }

  bool operator < (const heap_t & a,const heap_t & b){
    return b>a;
  }

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
      guess += g[G[i]].coord.size();
    }
    vector<heap_t> H;
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
    bool small=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
    int p=env?env->modulo.val:0;
    while (!H.empty() || k<f.coord.size()){
      // is highest remaining degree in f or heap?
      if (k<f.coord.size() && (H.empty() || tdeg_t_greater(f.coord[k].u,H.front().u,f.order)) ){
	// it's in f or both
	m=f.coord[k].u;
	if (small)
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
	std::pop_heap(H.begin(),H.end());
	heap_t & current=H.back(); // was root node of the heap
	const poly8 & gcurrent = g[G[current.i]];
	if (small)
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
	  push_heap(H.begin(),H.end());
	}
	else
	  H.pop_back();
      }
      if (small){
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
	i=G.size();
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
	heap_t current={i,q[i].coord.size()-1,1,g[G[i]].coord[1].u+monom};
	H.push_back(current);
	push_heap(H.begin(),H.end());
      }
    } // end main heap pseudo-division loop
  }


  void heap_reduce(const poly8 & f,const vectpoly8 & g,const vector<unsigned> & G,unsigned excluded,vectpoly8 & q,poly8 & rem,poly8& TMP1,environment * env){
    gen s;
    if (debug_infolevel>2)
      cerr << f << " = " << endl;
    heap_reduce(f,g,G,excluded,q,rem,TMP1,s,env);
    // end up by multiplying rem by s (so that everything is integer)
    if (debug_infolevel>2){
      for (unsigned i=0;i<G.size();++i)
	cerr << "(" << g[G[i]]<< ")*(" << q[i] << ")+ ";
      cerr << rem << endl;
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
      cerr << "ppz was " << tmp << endl;
  }

  void fake_smallsub(const poly8 &v1,unsigned rempos,const poly8 & v2,poly8 & v,annuaire & S){
    // like smallsub but does not do any arithmetic operation
    // except cancelling term rempos of v1 and leading term of v2
    // also updates S with all momonials
    vector< T_unsigned<gen,tdeg_t> >::const_iterator it1=v1.coord.begin(),it1pos=it1+rempos,it1end=v1.coord.end(),it2=v2.coord.begin(),it2end=v2.coord.end();
    v.coord.clear();
    v.coord.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1pos;++it1){
      v.coord.push_back(*it1);
    }
    S[it1->u]=0;
    ++it1; ++it2;
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	S[it1->u]=0;
	v.coord.push_back(*it1);
	++it1;
	++it2;
      }
      else {
	if (tdeg_t_strictly_greater(it1->u,it2->u,v1.order)){
	  S[it1->u]=0;
	  v.coord.push_back(*it1);
	  ++it1;
	}
	else {
	  S[it2->u]=0;
	  v.coord.push_back(*it2);
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1){
      S[it1->u]=0;
      v.coord.push_back(*it1);
    }
    for (;it2!=it2end;++it2){
      S[it2->u]=0;
      v.coord.push_back(*it2);
    }
  }

  void fake_reduce(const poly8 & p,const vectpoly8 & res,const vector<unsigned> & G,unsigned excluded,vectpoly8 & quo,poly8 & rem,poly8 & TMP1, poly8 & TMP2,annuaire & S){
    // like reduce but does not do any arithmetic operation
    if (&p!=&rem)
      rem=p;
    if (quo.size()<G.size())
      quo.resize(G.size());
    for (unsigned i=0;i<G.size();++i){
      quo[i].coord.clear();
      quo[i].order=p.order;
      quo[i].dim=p.dim;
    }
    S.clear();
    if (p.coord.empty())
      return ;
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator pt,ptend;
    unsigned i,rempos=0;
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
	S[pt->u]=0;
	++rempos;
	continue;
      }
      tdeg_t m=pt->u-res[G[i]].coord.front().u;
      quo[i].coord.push_back(T_unsigned<gen,tdeg_t>(1,m));
      TMP1.coord.clear();
      TMP2.coord.clear();
      smallshift(res[G[i]].coord,m,TMP1.coord);
      fake_smallsub(rem,rempos,TMP1,TMP2,S);
      swap(rem.coord,TMP2.coord);
    }
  }

  void makeline(const poly8 & p,const tdeg_t * shiftptr,annuaire & S,unsigned N,vecteur & v){
    v.resize(N); // size of S
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	v[S[it->u+*shiftptr]]=it->g;
      }
    }
    else {
      for (;it!=itend;++it){
	v[S[it->u]]=it->g;
      }
    }
  }

  // build a linear system to solve p = sum_{i,i!=excluded} res[G[i]]*quo[i] + rem
  // at startup: quo and rem must contain the monomials required (see fake_reduce), 
  // coefficients are undefined
  // at end: quo and rem contain the coefficients
  void solve_linsyst(const poly8 & p,const vectpoly8 & res,const vector<unsigned> & G,unsigned excluded,vectpoly8 & quo,int quosize,poly8 & rem,poly8 & TMP1,annuaire & S){
    // step1: stores right indices in S
    annuaire::iterator it=S.begin(),itend=S.end();
    unsigned N=0,i=0,j=0; // size of S, index
    for (;it!=itend;++N,++it){
      it->second=N;
    }
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // for each monomial of rem (will bring a line of 0 except one 1)
    // set coefficient in a line of a matrix M, columns are S monomials indices
    matrice M;
    M.reserve(N);
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator jt,jtend;
    for (i=0;i<G.size();++i){
      jt=quo[i].coord.begin();jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vecteur(N));
	makeline(res[G[i]],&jt->u,S,N,*M[j]._VECTptr);
      }
    }
    jt=rem.coord.begin();jtend=rem.coord.end();
    for (;jt!=jtend;++j,++jt){
      M.push_back(vecteur(N));
      (*M[j]._VECTptr)[S[jt->u]]=1;
    }
    // step3 transpose matrix, and call linsolve
    M=mtran(M);
    vecteur v(N);
    makeline(p,0,S,N,v);
    vecteur sol=linsolve(M,v,context0);
    // step4 pull back solution in rem (we don't need quo)
    gen lcmdeno=1;
    for (i=0;i<rem.coord.size();++i){
      gen g=sol[i+quosize];
      rem.coord[i].g=g;
      if (g.type==_FRAC)
	lcmdeno=lcm(lcmdeno,g._FRACptr->den);
    }
    // cleanup
    TMP1.coord.clear();
    for (i=0;i<rem.coord.size();++i){
      if (!is_zero(rem.coord[i].g))
	TMP1.coord.push_back(T_unsigned<gen,tdeg_t>(lcmdeno*rem.coord[i].g,rem.coord[i].u));
    }
    swap(rem.coord,TMP1.coord);
    inplace_ppz(rem);
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
#if 0 // much slower!
    if (debug_infolevel>1 && (!env || !env->moduloon)){
      annuaire S; 
      poly8 R;
      fake_reduce(p,res,G,excluded,quo,R,TMP1,TMP2,S);
      unsigned quosize=0;
      for (unsigned i=0;i<G.size();++i){
	quosize += quo[i].coord.size();
      }
      if (!quosize){
	swap(rem.coord,R.coord);
	return;
      }
      if (debug_infolevel>1){
	cerr << clock() << " reducing (#size) " << p.coord.size() << " wrt (#elements) " << G.size() << " system size " << S.size() << " quotient size " << quosize << endl;
      }
      solve_linsyst(p,res,G,excluded,quo,quosize,R,TMP1,S);
      swap(rem.coord,R.coord);
      return;
    }
#endif
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
    bool small=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
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
	if (tdeg_t_all_greater(pt->u,res[G[i]].coord.front().u,p.order))
	  break;
      }
      if (i==G.size()){ // no leading coeff of G is smaller than the current coeff of rem
	++rempos;
	// if (small) TMP1.coord.push_back(*pt);
	continue;
      }
      gen a(pt->g),b(res[G[i]].coord.front().g);
      if (small){
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
      // if (small) swap(rem.coord,TMP1.coord);
      if (!rem.coord.empty() && rem.coord.front().g!=1)
	smallmult(invmod(rem.coord.front().g,env->modulo),rem.coord,rem.coord,env->modulo.val);
      return;
    }    
    gen g=inplace_ppz(rem);
    if (debug_infolevel>2){
      if (rem.coord.empty())
	cerr << "0 reduction" << endl;
      if (g.type==_ZINT && mpz_sizeinbase(*g._ZINTptr,2)>16)
	cerr << "ppz size was " << mpz_sizeinbase(*g._ZINTptr,2) << endl;
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
    // if (small) swap(p.coord,TMP1.coord);
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
    unsigned sugarshift=pshift.total_degree();
    // adjust sugar for res
    res.sugar=p.sugar+sugarshift;
    // cerr << "spoly " << res.sugar << " " << pi << qi << endl;
    gen a=p.coord.front().g,b=q.coord.front().g;
    simplify3(a,b);
    if (debug_infolevel>2)
      cerr << "spoly " << a << " " << b << endl;
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
      cerr << "spoly ppz " << a << endl;
  }

  static void gbasis_update(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpoly8 & res,unsigned pos,poly8 & TMP1,poly8 & TMP2,vectpoly8 & vtmp,environment * env){
    if (debug_infolevel>1)
      cerr << clock() << " begin gbasis update " << endl;
    const poly8 & h = res[pos];
    short order=h.order;
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
      if (interrupted || ctrl_c)
	return;
      if (res[G[i]].coord.empty() || disjoint(h0,res[G[i]].coord.front().u,res.front().order,res.front().dim))
	continue;
      index_lcm(h0,res[G[i]].coord.front().u,tmp1,order); // h0 and G[i] leading monomial not prime together
      unsigned j;
      for (j=0;j<G.size();++j){
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
      cerr << clock() << " begin Groebner interreduce " << endl;
    C.clear();
    C.reserve(G.size());
    vector<unsigned> hG(1,pos);
    bool small=env && env->moduloon && env->modulo.type==_INT_ && env->modulo.val;
    for (unsigned i=0;i<G.size();++i){
      if (interrupted || ctrl_c)
	return;
      if (!res[G[i]].coord.empty() && !tdeg_t_all_greater(res[G[i]].coord.front().u,h0,order)){
	// reduce res[G[i]] with respect to h
	if (small)
	  reduce1small(res[G[i]],h,TMP1,TMP2,env);
	else
	  reduce(res[G[i]],res,hG,-1,vtmp,res[G[i]],TMP1,TMP2,env);
	C.push_back(G[i]);
      }
      // NB: removing all pairs containing i in it does not work
    }
    if (debug_infolevel>1)
      cerr << clock() << " end Groebner interreduce " << endl;
    C.push_back(pos);
    swap(C,G);
  }

  bool in_gbasis(vectpoly8 & res,vector<unsigned> & G,environment * env,bool sugar){
    poly8 TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vectpoly8 vtmp;
    vector< pair<unsigned,unsigned> > B;
    short order=res.front().order;
    //if (order==_PLEX_ORDER)
      sugar=false; // otherwise cyclic6 fails (bus error), don't know why
    for (unsigned l=0;l<res.size();++l){
      gbasis_update(G,B,res,l,TMP1,TMP2,vtmp,env);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      if (debug_infolevel>1)
	cerr << clock() << " number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small,cur;
      unsigned smallpos,smallsugar=0,cursugar=0;
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small,order);
      if (sugar)
	smallsugar=res[B[smallpos].first].sugar+(small-res[B[smallpos].first].coord.front().u).total_degree();
      for (unsigned i=smallpos+1;i<B.size();++i){
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	if (sugar)
	  cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree();
	bool doswap;
	if (order==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small,cur,order);
	else {
	  if (cursugar!=smallsugar)
	    doswap = smallsugar > cursugar;
	  else
	    doswap=tdeg_t_strictly_greater(small,cur,order);
	}
	if (doswap){
	  // cerr << "swap " << cursugar << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small,cur); // small=cur;
	  swap(smallsugar,cursugar);
	  smallpos=i;
	}
      }
      pair<unsigned,unsigned> bk=B[smallpos];
      if (debug_infolevel>1 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	cerr << clock() << " reducing pair with 1 element not in basis " << bk << endl;
      B.erase(B.begin()+smallpos);
      poly8 h(res.front().order,res.front().dim);
      spoly(res[bk.first],res[bk.second],h,TMP1,env);
      if (debug_infolevel>1)
	cerr << clock() << " reduce begin, pair " << bk << " remainder size " << h.coord.size() << endl;
      reduce(h,res,G,-1,vtmp,h,TMP1,TMP2,env);
      if (debug_infolevel>1){
	if (debug_infolevel>3){ cerr << h << endl; }
	cerr << clock() << " reduce end, remainder size " << h.coord.size() << endl;
      }
      if (!h.coord.empty()){
	res.push_back(h);
	gbasis_update(G,B,res,res.size()-1,TMP1,TMP2,vtmp,env);
	if (debug_infolevel>2)
	  cerr << clock() << " basis indexes " << G << " pairs indexes " << B << endl;
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
    short int order; // _PLEX_ORDER, _REVLEX_ORDER or _TDEG_ORDER
    short int dim;
    unsigned sugar;
    void dbgprint() const;
    polymod():order(_PLEX_ORDER),dim(0) {}
    polymod(int o_,int dim_): order(o_),dim(dim_) {}
    polymod(const polynome & p,modint m){
      order=-1;
      dim=p.dim;
      if (p.is_strictly_greater==i_lex_is_strictly_greater)
	order=_PLEX_ORDER;
      if (p.is_strictly_greater==i_total_revlex_is_strictly_greater)
	order=_REVLEX_ORDER;
      if (p.is_strictly_greater==i_total_lex_is_strictly_greater)
	order=_TDEG_ORDER;
      if (p.dim>GROEBNER_VARS-(order==_REVLEX_ORDER || order==_TDEG_ORDER)) 
	cerr << "Number of variables is too large to be handled by giac";
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
	  sugar=coord.front().u.total_degree();
	}
      }
    }
    void get_polynome(polynome & p) const {
      p.dim=dim;
      switch (order){
      case _PLEX_ORDER:
	p.is_strictly_greater=i_lex_is_strictly_greater;
	break;
      case _REVLEX_ORDER:
	p.is_strictly_greater=i_total_revlex_is_strictly_greater;
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
    }
  };

  bool tripolymod (const polymod & p,const polymod & q){
    if (p.coord.size()!=q.coord.size())
      return p.coord.size()<q.coord.size();
    if (p.coord.empty())
      return false;
    return p.coord.front().u<q.coord.front().u; 
    // this should be enough to sort groebner basis
  }

  void smallmultmod(modint a,polymod & p,modint m){
    if (a==1 || a==1-m)
      return;
    std::vector< T_unsigned<modint,tdeg_t> >::iterator pt=p.coord.begin(),ptend=p.coord.end();
    for (;pt!=ptend;++pt){
      pt->g=(longlong(pt->g)*a)%m;
    }
  }

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
      q.sugar=q.coord.front().u.total_degree();
      if (q.coord.front().g!=1)
	smallmultmod(invmod(q.coord.front().g,env),q,env);
      q.coord.front().g=1;
    }
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
      q.sugar=q.coord.front().u.total_degree();
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

  ostream & operator << (ostream & os, const polymod & p){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    if (it==itend)
      return os << 0 ;
    for (;it!=itend;){
      os << it->g  ;
#ifdef GBASIS_SMALL
      signed char tab[GROEBNER_VARS+1];
#else
      short tab[GROEBNER_VARS+1];
#endif
      it->u.get_tab(tab);
      switch (p.order){
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
      }
      ++it;
      if (it==itend)
	break;
      os << " + ";
    }
    return os;
  }

  void polymod::dbgprint() const { 
    std::cerr << *this << endl;
  }

  class vectpolymod:public vector<polymod>{
  public:
    void dbgprint() const { cerr << *this << endl; }
  };

  void vectpoly_2_vectpolymod(const vectpoly & v,vectpolymod & v8,modint m){
    v8.clear();
    v8.reserve(v.size());
    for (unsigned i=0;i<v.size();++i){
      v8.push_back(polymod(v[i],m));
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
      guess += g[G[i]].coord.size();
    }
    vector<heap_t> H;
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
	std::pop_heap(H.begin(),H.end());
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
	  push_heap(H.begin(),H.end());
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
	heap_t current={i,q[i].coord.size()-1,1,g[G[i]].coord[1].u+monom};
	H.push_back(current);
	push_heap(H.begin(),H.end());
      }
    } // end main heap pseudo-division loop
  }

  void heap_reducemod(const polymod & f,const vectpolymod & g,const vector<unsigned> & G,unsigned excluded,vectpolymod & q,polymod & rem,modint env){
    in_heap_reducemod(f,g,G,excluded,q,rem,0,env);
    // end up by multiplying rem by s (so that everything is integer)
    if (debug_infolevel>2){
      for (unsigned i=0;i<G.size();++i)
	cerr << "(" << g[G[i]]<< ")*(" << q[i] << ")+ ";
      cerr << rem << endl;
    }
    if (!rem.coord.empty() && rem.coord.front().g!=1){
      smallmult(invmod(rem.coord.front().g,env),rem.coord,rem.coord,env);
      rem.coord.front().g=1;
    }
  }

#endif

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
	// if (small) TMP1.coord.push_back(*pt);
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
    unsigned sugarshift=pshift.total_degree();
    // adjust sugar for res
    res.sugar=p.sugar+sugarshift;
    // cerr << "spoly mod " << res.sugar << " " << pi << qi << endl;
    if (p.order==_PLEX_ORDER || sugarshift!=0)
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
      // rempos=0; // since we have removed the beginning of rem (copied in TMP1)
      swap(p.coord,TMP2.coord);
    }
    // if (small) swap(p.coord,TMP1.coord);
    if (!p.coord.empty() && p.coord.front().g!=1){
      smallmultmod(invmod(p.coord.front().g,env),p,env);
      p.coord.front().g=1;
    }
  }

  static void gbasis_updatemod(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpolymod & res,unsigned pos,polymod & TMP2,modint env,bool reduce){
    if (debug_infolevel>2)
      cerr << clock() << " mod begin gbasis update " << G.size() << endl;
    if (debug_infolevel>3)
      cerr << G << endl;
    const polymod & h = res[pos];
    short order=h.order;
    vector<unsigned> C;
    C.reserve(G.size()+1);
    const tdeg_t & h0=h.coord.front().u;
    tdeg_t tmp1,tmp2;
    // C is used to construct new pairs
    // create pairs with h and elements g of G, then remove
    // -> if g leading monomial is prime with h, remove the pair
    // -> if g leading monomial is not disjoint from h leading monomial
    //    keep it only if lcm of leading monomial is not divisible by another one
    unsigned tmpsize=G.empty()?0:G.back()+1;
    vector<tdeg_t> tmp(tmpsize);
    for (unsigned i=0;i<tmpsize;++i){
      if (res[i].coord.empty()){
	tmp[i].tab[0]=-1;
      }
      else
	index_lcm(h0,res[i].coord.front().u,tmp[i],order); 
    }
    for (unsigned i=0;i<G.size();++i){
      if (interrupted || ctrl_c)
	return;
      if (res[G[i]].coord.empty() || disjoint(h0,res[G[i]].coord.front().u,res.front().order,res.front().dim))
	continue;
      tdeg_t * tmp1=&tmp[G[i]]; // h0 and G[i] leading monomial not prime together
      tdeg_t * tmp2=&tmp[0],*tmpend=tmp2+tmpsize;
      for (;tmp2!=tmp1;++tmp2){
	if (tmp2[0]==-1)
	  continue;
	if (tdeg_t_all_greater(*tmp1,*tmp2,order))
	  break; // found another pair, keep the smallest, or the first if equal
      }
      if (tmp2!=tmp1)
	continue;
      for (++tmp2;tmp2!=tmpend;++tmp2){
	if (tmp2[0]==-1)
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
      cerr << clock() << " end, pairs:"<< endl;
      if (debug_infolevel>3)
	cerr << B << endl;
      cerr << "mod begin Groebner interreduce " << endl;
    }
    C.clear();
    C.reserve(G.size()+1);
    // bool pos_pushed=false;
    for (unsigned i=0;i<G.size();++i){
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
      cerr << clock() << " mod end Groebner interreduce " << endl;
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
      cerr << clock() << " mod begin gbasis update " << G.size() << "+" << add.size() << endl;
    if (debug_infolevel>3)
      cerr << G << endl;
    vector<unsigned> C;
    // C is used to construct new pairs
    tdeg_t tmp1,tmp2;
    short order;
    for (unsigned pos=debut;pos<G.size();++pos){
      const polymod & h = res[pos];
      const tdeg_t & h0=h.coord.front().u;
      // create pairs with h and elements g of G, then remove
      // -> if g leading monomial is prime with h, remove the pair
      // -> if g leading monomial is not disjoint from h leading monomial
      //    keep it only if lcm of leading monomial is not divisible by another one
      for (unsigned i=0;i<pos;++i){
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
      cerr << clock() << " end, pairs:"<< endl;
      if (debug_infolevel>3)
	cerr << B << endl;
      cerr << "mod begin Groebner interreduce " << endl;
    }
    vector<unsigned> C;
    C.reserve(G.size());
    for (unsigned i=0;i<G.size();++i){
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
      cerr << clock() << " mod end Groebner interreduce " << endl;
    swap(C,G);
  }
#endif

  bool in_gbasismod(vectpoly8 & res8,vectpolymod &res,vector<unsigned> & G,modint env,bool sugar,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero){
    convert(res8,res,env);
    unsigned ressize=res8.size();
    unsigned learned_position=0;
    bool learning=pairs_reducing_to_zero && pairs_reducing_to_zero->empty();
    if (debug_infolevel>1000)
      res.dbgprint(); // instantiate dbgprint()
    polymod TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vector< pair<unsigned,unsigned> > B;
    short order=res.front().order;
    if (order==_PLEX_ORDER)
      sugar=false;
    for (unsigned l=0;l<ressize;++l){
      gbasis_updatemod(G,B,res,l,TMP2,env,true);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      if (debug_infolevel>1)
	cerr << clock() << " mod number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small,cur;
      unsigned smallpos,smallsugar=0,cursugar=0;
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small,order);
      if (sugar)
	smallsugar=res[B[smallpos].first].sugar+(small-res[B[smallpos].first].coord.front().u).total_degree();
      for (unsigned i=smallpos+1;i<B.size();++i){
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	bool doswap=false;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	if (sugar)
	  cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree();
	if (order==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small,cur,order);
	else {
	  if (cursugar!=smallsugar)
	    doswap = smallsugar > cursugar;
	  else
	    doswap=tdeg_t_strictly_greater(small,cur,order);
	}
	if (doswap){
	  // cerr << "swap mod " << cursugar << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small,cur); // small=cur;
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
	cerr << clock() << " mod reducing pair with 1 element not in basis " << bk << endl;
      // polymod h(res.front().order,res.front().dim);
      spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
      if (debug_infolevel>1){
	cerr << clock() << " mod reduce begin, pair " << bk << " spoly size " << TMP1.coord.size() << " sugar deg " << TMP1.sugar << " degree " << TMP1.coord.front().u << endl;
      }
      reducemod(TMP1,res,G,-1,TMP1,env);
      if (debug_infolevel>1){
	if (debug_infolevel>2){ cerr << TMP1 << endl; }
	cerr << clock() << " mod reduce end, remainder size " << TMP1.coord.size() << endl;
      }
      if (!TMP1.coord.empty()){
	if (ressize==res.size())
	  res.push_back(polymod(TMP1.order,TMP1.dim));
	swap(res[ressize],TMP1);
	++ressize;
	gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true);
	if (debug_infolevel>2)
	  cerr << clock() << " mod basis indexes " << G << " pairs indexes " << B << endl;
      }
      else {
	if (learning && pairs_reducing_to_zero)
	  pairs_reducing_to_zero->push_back(bk);
      }
    }
    if (ressize<res.size())
      res.resize(ressize);
    // sort(res.begin(),res.end(),tripolymod);
    convert(res,res8,env);
    return true;
  }

  // F4 algorithm
  struct heap_tt {
    unsigned f4vpos;
    unsigned polymodpos;
    tdeg_t u;
    heap_tt(unsigned a,unsigned b,tdeg_t t):f4vpos(a),polymodpos(b),u(t){};
    heap_tt():f4vpos(0),polymodpos(0),u(0){};
  };
  bool operator > (const heap_tt & a,const heap_tt & b){
    return a.u>b.u;
  }

  bool operator < (const heap_tt & a,const heap_tt & b){
    return b>a;
  }

  void collect(const vectpolymod & f4v,polymod & allf4,int start=0){
    vectpolymod::const_iterator it=f4v.begin(),itend=f4v.end();
    vector<heap_tt> H;
    H.reserve(itend-it);
    for (unsigned i=0;it!=itend;++i,++it){
      if (int(it->coord.size())>start)
	H.push_back(heap_tt(i,start,it->coord[start].u));
    }
    make_heap(H.begin(),H.end());
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end());
      // push root node of the heap in allf4
      heap_tt & current =H.back();
      if (allf4.coord.empty() || allf4.coord.back().u!=current.u)
	allf4.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
#if 1
      if (current.polymodpos>=f4v[current.f4vpos].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4v[current.f4vpos].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end());
#else
      const vector< T_unsigned<modint,tdeg_t> > & f4curcoord = f4v[current.f4vpos].coord;
      if (current.polymodpos>=f4curcoord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4curcoord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end());
#endif
    }
  }

  void collect(const vectpolymod & f4v,const vector<unsigned> & G,polymod & allf4,unsigned start=0){
    vector<heap_tt> H;
    unsigned Gsize=G.size();
    H.reserve(Gsize);
    for (unsigned i=0;i<Gsize;++i){
      if (f4v[G[i]].coord.size()>start)
	H.push_back(heap_tt(i,start,f4v[G[i]].coord[start].u));
    }
    make_heap(H.begin(),H.end());
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end());
      // push root node of the heap in allf4
      heap_tt & current =H.back();
      if (allf4.coord.empty() || allf4.coord.back().u!=current.u)
	allf4.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      if (current.polymodpos>=f4v[G[current.f4vpos]].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4v[G[current.f4vpos]].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end());
    }
  }

  struct sparse_element {
    modint val;
    unsigned pos;
    sparse_element(modint v,unsigned u):val(v),pos(u){};
    sparse_element():val(0),pos(-1){};
  };

  ostream & operator << (ostream & os,const sparse_element & s){
    return os << '{' << s.val<<',' << s.pos << '}' ;
  }

#ifdef GBASIS_F4
  bool reducef4pos(vector<modint> &v,const vector< vector<modint> > & M,vector<int> pivotpos,modint env){
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
  unsigned reducef4_64(vector<modint> &v,const vector< vector<sparse_element> > & M,modint env,vector<int128_t> & w){
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
      // cerr << "multiplier ok line " << i << " value " << c << " " << w << endl;
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

  ostream & operator << (ostream & os,const int128_t & i){
    return os << longlong(i) ;
    // return os << "(" << longlong(i>>64) <<","<< longlong(i) <<")" ;
  }

#endif
  // sparse element if prime is < 2^24
  // if shift == 0 the position is absolute in the next sparse32 of the vector
  struct sparse32 {
    modint val:25;
    unsigned shift:7;
    sparse32(modint v,unsigned s):val(v),shift(s){};
    sparse32():val(0),shift(0){};
  };

  ostream & operator << (ostream & os,const sparse32 & s){
    return os << "(" << s.val << "," << s.shift << ")" ;
  }

  unsigned reducef4_32(vector<modint> &v,const vector< vector<sparse32> > & M,modint env,vector<modint2> & w){
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
	return vt-v.begin();
    }
    return v.size();
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

  unsigned reducef4(vector<modint> &v,const vector< vector<sparse_element> > & M,modint env){
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
	return vt-v.begin();
    }
    return v.size();
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

  unsigned first_index(const vector<shifttype> & v){
    if (v.front())
      return v.front();
    return (v[2]<<16)+v[1];
  }

  inline void pushsplit(vector<shifttype> & v,unsigned & pos,unsigned newpos){
    unsigned shift=newpos-pos;
    if ( shift && (shift < (1<<16)) )
      v.push_back(shift);
    else {
      v.push_back(0);
      v.push_back(shift);
      v.push_back(shift >> 16 );
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

#ifdef __x86_64__
  unsigned reducef4split64(vector<modint> &v,const vector< vector<shifttype> > & M,vector< vector<modint> > & coeffs,vector<unsigned> & coeffindex,modint env,vector<int128_t> & v128){
    vector<modint>::iterator vt=v.begin(),vtend=v.end();
    v128.resize(v.size());
    vector<int128_t>::iterator wt=v128.begin();
    for (;vt!=vtend;++wt,++vt)
      *wt=*vt;
    for (unsigned i=0;i<M.size();++i){
      const vector<modint> & mcoeff=coeffs[coeffindex[i]];
      vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end(),jt_=jtend-8;
      if (jt==jtend)
	continue;
      const vector<shifttype> & mindex=M[i];
      const shifttype * it=&mindex.front();
      unsigned pos=0;
      next_index(pos,it);
      // if (pos>v.size()) cerr << "error" <<endl;
      modint c=(invmod(*jt,env)*v128[pos])%env;
      v128[pos]=0;
      if (!c)
	continue;
      for (++jt;jt<jt_;){
#ifdef GIAC_SHORTSHIFTTYPE
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;	
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
	++jt;	
#else
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
#endif
      }
      for (;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	next_index(pos,it);
	v128[pos]-=modint2(c)*(*jt);
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
#endif

  // reducef4 matrix M has band structure, to spare memory
  // we split coeffs/index in :
  // - M each line is a list of shift index, 
  // - coeffindex, relative to coeffs, M[i][j] corresponds to coeffs[coeffinde[i]][j]
  // - coeffs is the list of coefficients
  unsigned reducef4split(vector<modint> &v,const vector< vector<shifttype> > & M,vector< vector<modint> > & coeffs,vector<unsigned> & coeffindex,modint env,vector<modint2> & v64){
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
	const vector<modint> & mcoeff=coeffs[coeffindex[i]];
	if (mcoeff.empty())
	  continue;
	const modint * jt=&mcoeff.front(),*jtend=jt+mcoeff.size(),*jt_=jtend-8;
	const vector<shifttype> & mindex=M[i];
	const shifttype * it=&mindex.front();
	unsigned pos=0;
	next_index(pos,it);
	// if (pos>v.size()) cerr << "error" <<endl;
	modint c=(modint2(invmod(*jt,env))*v64[pos])%env;
	v64[pos]=0;
	if (!c)
	  continue;
	++jt;
#ifdef GIAC_SHORTSHIFTTYPE
#if GIAC_SHORTSHIFTTYPE==16
	next_index(pos,it);
	v64[pos]-=modint2(c)*(*jt);
	++jt;
	if ((unsigned long)(it)%4==0){
	  for (;jt<jt_;){
	    unsigned shift=*(unsigned*) it;
	    if (!(shift & 0xffff) || !(shift>>16))
	      break;
	    pos += (shift & 0xffff);
	    v64[pos] -= modint2(c)*(*jt);
	    ++jt;
	    pos += (shift>>16);
	    v64[pos] -= modint2(c)*(*jt);	    
	    ++jt;
	    it += 2;
	  }
	}
#endif
	for (;jt!=jtend;++jt){
	  next_index(pos,it);
	  v64[pos]-=modint2(c)*(*jt);
	}
#else // def GIAC_SHORTSHIFTTYPE
#if 1
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
#else
	for (;jt<jt_;it+=8,jt+=8){
	  v64[it[0]]-=modint2(c)*jt[0];
	  v64[it[1]]-=modint2(c)*jt[1];
	  v64[it[2]]-=modint2(c)*jt[2];
	  v64[it[3]]-=modint2(c)*jt[3];
	  v64[it[4]]-=modint2(c)*jt[4];
	  v64[it[5]]-=modint2(c)*jt[5];
	  v64[it[6]]-=modint2(c)*jt[6];
	  v64[it[7]]-=modint2(c)*jt[7];
	}
#endif
	for (;jt!=jtend;++jt){
	  next_index(pos,it);
	  v64[pos]-=modint2(c)*(*jt);
	}
#endif
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
	const vector<modint> & mcoeff=coeffs[coeffindex[i]];
	vector<modint>::const_iterator jt=mcoeff.begin(),jtend=mcoeff.end();
	if (jt==jtend)
	  continue;
	const vector<shifttype> & mindex=M[i];
	const shifttype * it=&mindex.front();
	unsigned pos=0;
	next_index(pos,it);
	// if (pos>v.size()) cerr << "error" <<endl;
	modint c=(modint2(invmod(*jt,env))*v[pos])%env;
	v[pos]=0;
	if (!c)
	  continue;
#ifdef PSEUDO_MOD
	if (env<(1<<29)){
	  c=-c;
	  for (++jt;jt!=jtend;++jt){
#ifdef GIAC_SHORTSHIFTTYPE
	    next_index(pos,it);
	    // if (pos>v.size()) cerr << "error" <<endl;
	    pseudo_mod(v[pos],c,*jt,env,invmodulo,nbits);
#else
	    // if (pos>v.size()) cerr << "error" <<endl;
	    pseudo_mod(v[*it],c,*jt,env,invmodulo,nbits);
	    ++it;
#endif
	  }
	  continue;
	}
#endif
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
#ifdef PSEUDO_MOD
      for (vt=v.begin();vt!=vtend;++vt){
	if (*vt)
	  *vt %= env;
      }
#endif
    } // end else based on modulo size
    for (vt=v.begin();vt!=vtend;++vt){
      if (*vt)
	return vt-v.begin();
    }
    return v.size();
  }

  bool tri(const vector<sparse_element> & v1,const vector<sparse_element> & v2){
    return v1.front().pos<v2.front().pos;
  }

  bool tri1(const sparse_element & v1,const sparse_element & v2){
    return v1.val<v2.val;
  }

#ifdef GBASIS_HEAP
  void makeline(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<modint> & v){
    v.resize(R.coord.size()); 
    v.assign(R.coord.size(),0);
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtbeg=jt,jtend=R.coord.end();
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
	if (u>=jt->u){
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

  void convert(const vector<modint> & v,vector<sparse_element> & w,vector<char> & used){
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
	    push32(v,it->g,pos,jt-R.coord.begin());
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
	    push32(v,it->g,pos,jt-R.coord.begin());
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void convert32(const vector<modint> & v,vector<sparse32> & w,vector<char> & used){
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
	push32(w,*it,pos,it-v.begin());
    }
  }

  void rref_f4mod_interreduce(vectpolymod & f4v,const vector<unsigned> & f4vG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
    unsigned N=R.coord.size(),i,j=0;
    unsigned c=N;
    double sknon0=0;
    vector<char> used(N,0);
    unsigned usedcount=0,zerolines=0,Msize=0;
    vector< vector<modint> > K(f4vG.size());
    for (i=0;i<G.size();++i){
      Msize += quo[i].coord.size();
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
	  // cerr << M[j] << endl;
	  if (M[j].front().shift)
	    atrier.push_back(sparse_element(M[j].front().shift,j));
	  else
	    atrier.push_back(sparse_element(*(unsigned *) &M[j][1],j));
	}
      }
      if (debug_infolevel>1)
	cerr << clock() << " end build M32" << endl;
      // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
      // cerr << "before sort " << M << endl;
      sort(atrier.begin(),atrier.end(),tri1);
      vector< vector<sparse32> > M1(atrier.size());
      double mem=0; // mem*4=number of bytes allocated for M1
      for (i=0;i<atrier.size();++i){
	swap(M1[i],M[atrier[i].pos]);
	mem += M1[i].size();
      }
      swap(M,M1);
      bool freemem=mem>4e7; // should depend on real memory available
      if (debug_infolevel>1)
	cerr << clock() << " M32 sorted, rows " << M.size() << " columns " << N << " terms " << mem << " ratio " << (mem/M.size())/N <<endl;
      // cerr << "after sort " << M << endl;
      // step3 reduce
      vector<modint> v(N); vector<modint2> w(N);
      vector< vector<sparse32> > SK(f4v.size());
      for (i=0;i<f4vG.size();++i){
	if (!f4v[f4vG[i]].coord.empty()){
	  makeline(f4v[f4vG[i]],0,R,v);
	  if (freemem){ 
	    polymod clearer; swap(f4v[f4vG[i]].coord,clearer.coord); 
	  }
	  c=giacmin(c,reducef4_32(v,M,env,w));
	  // convert v to a sparse vector in SK and update used
	  convert32(v,SK[i],used);
	  //cerr << v << endl << SK[i] << endl;
	}
      }
      M.clear();
      if (debug_infolevel>1)
	cerr << clock() << " f4v reduced " << f4vG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
      for (i=0;i<N;++i)
	usedcount += (used[i]>0);
      if (debug_infolevel>1){
	cerr << clock() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
	if (debug_infolevel>2)
	  cerr << " column32 used " << used << endl;
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
	vector<char>::const_iterator ut=used.begin(),ut0=ut;
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
	cerr << clock() << " end build M" << endl;
      // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
      // cerr << "before sort " << M << endl;
      sort(atrier.begin(),atrier.end(),tri1);
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
	cerr << clock() << " M sorted, rows " << M.size() << " columns " << N << "[" << firstpart << "] terms " << mem << " ratio " << (mem/N)/M.size() << endl;
      // cerr << "after sort " << M << endl;
      // step3 reduce
      vector<modint> v(N);
      vector< vector<sparse_element> > SK(f4v.size());
#ifdef __x86_64__
      vector<int128_t> v128(N); 
      vector<modint> multiplier(M.size()); vector<unsigned> pos(M.size());
#endif
      for (i=0;i<f4vG.size();++i){
	if (!f4v[f4vG[i]].coord.empty()){
	  makeline(f4v[f4vG[i]],0,R,v);
	  if (freemem){ 
	    polymod clearer; swap(f4v[f4vG[i]].coord,clearer.coord); 
	  }
#ifdef __x86_64__
	  /* vector<modint> w(v);
	  // cerr << "reduce " << v << endl << M << endl;
	  c=giacmin(c,reducef4slice(w,M,env,v128,multiplier,pos));
	  c=giacmin(c,reducef4_64(v,M,env,v128));
	  if (w!=v) cerr << w << endl << v << endl; else cerr << "ok" << endl;
	  */
	  // c=giacmin(c,reducef4slice(v,M,env,v128,multiplier,pos));
	  if (0 && env<(1<<29) && N>10000) // it's slower despite v128 not in cache
	    c=giacmin(c,reducef4(v,M,env));
	  else
	    c=giacmin(c,reducef4_64(v,M,env,v128));
#else // __x86_64__
	  c=giacmin(c,reducef4(v,M,env));
#endif // __x86_64__
	  // convert v to a sparse vector in SK and update used
	  convert(v,SK[i],used);
	  // cerr << v << endl << SK[i] << endl;
	}
      }
      M.clear();
      if (debug_infolevel>1)
	cerr << clock() << " f4v reduced " << f4vG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
      for (i=0;i<N;++i)
	usedcount += (used[i]>0);
      if (debug_infolevel>1){
	cerr << clock() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
	// if (debug_infolevel>2) cerr << " column use " << used << endl;
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
	vector<char>::const_iterator ut=used.begin(),ut0=ut;
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
	// cerr << used << endl << SK[i] << endl << K[i] << endl;
      }
    }
    if (debug_infolevel>1)
      cerr << clock() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> maxrankcols; longlong idet;
    // cerr << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,K.size(),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    // cerr << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      cerr << clock() << " f4v interreduced" << endl;
    for (i=0;i<f4vG.size();++i){
#if 0 // spare memory, keep exactly the right number of monomials in f4v[]
      polymod tmpP(f4v[f4vG[i]].order,f4v[f4vG[i]].dim);
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
      vector<char>::const_iterator ut=used.begin();
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
      swap(tmpP.coord,f4v[f4vG[i]].coord);
#else
      // cerr << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4v[f4vG[i]].coord;
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
      vector<char>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4v[f4vG[i]],env);	
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

  void makelinesplit(const polymod & p,const tdeg_t * shiftptr,const polymod & R,vector<shifttype> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),jt=R.coord.begin(),jtend=R.coord.end();
    unsigned pos=0;
    if (shiftptr){
      for (;it!=itend;++it){
	tdeg_t u=it->u+*shiftptr;
	for (;jt!=jtend;++jt){
	  if (jt->u==u){
	    pushsplit(v,pos,jt-R.coord.begin());
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
	    pushsplit(v,pos,jt-R.coord.begin());
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

  void rref_f4modsplit_interreduce(vectpolymod & f4v,const vector<unsigned> & f4vG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
    unsigned N=R.coord.size(),i,j=0;
    unsigned c=N;
    double sknon0=0;
    vector<char> used(N,0);
    unsigned usedcount=0,zerolines=0;
    vector< vector<modint> > K(f4vG.size());
    vector<vector<shifttype> > Mindex;
    vector< vector<modint> > Mcoeff(G.size());
    vector<unsigned> coeffindex;
    Mindex.reserve(N);
    coeffindex.reserve(N);
    vector<sparse_element> atrier;
    atrier.reserve(N);
    for (i=0;i<G.size();++i){
      // copy coeffs of res[G[i]] in Mcoeff
      copycoeff(res[G[i]],Mcoeff[i]);
      // for each monomial of quo[i], find indexes and put in Mindex
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	coeffindex.push_back(i);
	Mindex.push_back(vector<shifttype>(0));
#ifdef GIAC_SHORTSHIFTTYPE
	Mindex[j].reserve(1+int(1.1*res[G[i]].coord.size()));
#else
	Mindex[j].reserve(res[G[i]].coord.size());
#endif
	makelinesplit(res[G[i]],&jt->u,R,Mindex[j]);
	atrier.push_back(sparse_element(first_index(Mindex[j]),j));
      }
    }
    if (debug_infolevel>1)
      cerr << clock() << " end build Mindex/Mcoeff" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // cerr << "before sort " << M << endl;
    sort(atrier.begin(),atrier.end(),tri1);
    vector< vector<shifttype> > Mindex1(atrier.size());
    vector<unsigned> coeffindex1(atrier.size());
    double mem=0; // mem*4=number of bytes allocated for M1
    for (i=0;i<atrier.size();++i){
      swap(Mindex1[i],Mindex[atrier[i].pos]);
      swap(coeffindex1[i],coeffindex[atrier[i].pos]);
      mem += Mindex1[i].size();
    }
    swap(Mindex,Mindex1);
    swap(coeffindex,coeffindex1);
    bool freemem=mem>4e7; // should depend on real memory available
    if (debug_infolevel>1)
      cerr << clock() << " Mindex sorted, rows " << Mindex.size() << " columns " << N << " terms " << mem << " ratio " << (mem/Mindex.size())/N <<endl;
    // cerr << "after sort " << M << endl;
    // step3 reduce
    vector<modint> v(N); 
    vector<modint2> v64(N);
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
    vector< vector<sparse_element> > SK(f4v.size());
    for (i=0;i<f4vG.size();++i){
      if (!f4v[f4vG[i]].coord.empty()){
	makeline(f4v[f4vG[i]],0,R,v);
	if (freemem){ 
	  polymod clearer; swap(f4v[f4vG[i]].coord,clearer.coord); 
	}
#ifdef __x86_64__
	if (env<(1<<24))
	  c=giacmin(c,reducef4split(v,Mindex,Mcoeff,coeffindex,env,v64));
	else
	  c=giacmin(c,reducef4split64(v,Mindex,Mcoeff,coeffindex,env,v128));
#else
	c=giacmin(c,reducef4split(v,Mindex,Mcoeff,coeffindex,env,v64));
#endif
	// convert v to a sparse vector in SK and update used
	convert(v,SK[i],used);
	//cerr << v << endl << SK[i] << endl;
      }
    }
    Mindex.clear();
    if (debug_infolevel>1)
      cerr << clock() << " f4v split reduced " << f4vG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
    for (i=0;i<N;++i)
      usedcount += (used[i]>0);
    if (debug_infolevel>1){
      cerr << clock() << " number of non-zero columns " << usedcount << " over " << N << endl; // usedcount should be approx N-M.size()=number of cols of M-number of rows
      if (debug_infolevel>2)
	cerr << " column split used " << used << endl;
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
      vector<char>::const_iterator ut=used.begin(),ut0=ut;
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
      // cerr << used << endl << SK[i] << endl << K[i] << endl;
    } // end create dense matrix K
    if (debug_infolevel>1)
      cerr << clock() << " rref " << K.size() << "x" << usedcount << " non0 " << sknon0 << " ratio " << (sknon0/K.size())/usedcount << " nulllines " << zerolines << endl;
    vecteur pivots; vector<int> maxrankcols; longlong idet;
    // cerr << K << endl;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,K.size(),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    // cerr << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    vector<int> permu=perminv(permutation);
    if (debug_infolevel>1)
      cerr << clock() << " f4v interreduced" << endl;
    for (i=0;i<f4vG.size();++i){
#if 0 // spare memory, keep exactly the right number of monomials in f4v[]
      polymod tmpP(f4v[f4vG[i]].order,f4v[f4vG[i]].dim);
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
      vector<char>::const_iterator ut=used.begin();
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
      swap(tmpP.coord,f4v[f4vG[i]].coord);
#else
      // cerr << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4v[f4vG[i]].coord;
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
      vector<char>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4v[f4vG[i]],env);	
	Pcoord.front().g=1;
      }
#endif
    }
  }

  void rref_f4mod_nointerreduce(vectpolymod & f4v,const vector<unsigned> & f4vG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation){
    unsigned N=R.coord.size(),i,j=0;
    for (i=0;i<G.size();++i){
      if (!quo[i].coord.empty())
	break;
    }
    if (i==G.size()){
      if (debug_infolevel>1)
	cerr << clock() << " No inter-reduction" << endl;
      return;
    }
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
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
      cerr << clock() << " end build M" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // cerr << "before sort " << M << endl;
    sort(atrier.begin(),atrier.end(),tri1);
    vector< vector<sparse_element> > M1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(M1[i],M[atrier[i].pos]);
    }
    swap(M,M1);
    // sort(M.begin(),M.end(),tri);
    if (debug_infolevel>1)
      cerr << clock() << " M sorted, rows " << M.size() << " columns " << N << " #basis to reduce" << f4vG.size() << endl;
    // cerr << "after sort " << M << endl;
    // step3 reduce
    unsigned c=N;
    vector<modint> v(N);
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
#ifdef __x86_64__
    vector<int128_t> v128(N);
#endif
    for (i=0;i<f4vG.size();++i){
      if (!f4v[f4vG[i]].coord.empty()){
	makeline(f4v[f4vG[i]],0,R,v);
	// cerr << v << endl;
#ifdef __x86_64__
	/* if (N>=4096)
	  c=giacmin(c,reducef4(v,M,env));
	  else */
	  c=giacmin(c,reducef4_64(v,M,env,v128));
#else
	c=giacmin(c,reducef4(v,M,env));
#endif
	vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4v[f4vG[i]].coord;
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
	  smallmultmod(invmod(Pcoord.front().g,env),f4v[f4vG[i]],env);	
	  Pcoord.front().g=1;
	}
      }
    }
  }

#if 0
  void rref_f4mod(vectpolymod & f4v,const vector<unsigned> & f4vG,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation,bool interreduce){
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are R monomials indices
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
    vector< vector<sparse_element> > M;
    unsigned N=R.coord.size(),i,j=0;
    M.reserve(N);
    for (i=0;i<G.size();++i){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vector<sparse_element>(0));
	makeline(res[G[i]],&jt->u,R,M[j]);
      }
    }
    if (debug_infolevel>1)
      cerr << clock() << " end build M" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // cerr << "before sort " << M << endl;
    sort(M.begin(),M.end(),tri);
    if (debug_infolevel>1)
      cerr << clock() << " M sorted, rows " << M.size() << " columns " << N << endl;
    // cerr << "after sort " << M << endl;
    // step3 reduce
    // perhaps better to do simultaneous reduction?
    vector< vector<modint> > K(f4vG.size());
    unsigned c=N;
    for (i=0;i<f4vG.size();++i){
      vector<modint> &v=K[i];
      v.resize(N);
      if (!f4v[f4vG[i]].coord.empty()){
	makeline(f4v[f4vG[i]],0,R,v);
	// cerr << v << endl;
	c=giacmin(c,reducef4(v,M,env));
      }
    }
    if (debug_infolevel>1)
      cerr << clock() << " f4v reduced " << f4vG.size() << " polynoms over " << N << " monomials, start at " << c << endl;
    vector<char> used;
    if (interreduce){
      used=vector<char>(N);
      for (i=0; i<K.size(); ++i){
	vector<modint>::const_iterator vt=K[i].begin(),vtend=K[i].end();
	vector<char>::iterator ut=used.begin();
	for (;vt!=vtend;++ut,++vt){
	  if (*vt)
	    *ut=1;
	}
      }
      unsigned usedcount=0;
      for (i=0;i<N;++i)
	usedcount += used[i];
      if (debug_infolevel>1)
	cerr << clock() << " number of non-zero columns " << usedcount << " over " << N << endl;
      // compress matrix K by removing 0 columns
      for (i=0; i<K.size(); ++i){
	vector <modint> tmpused(usedcount);
	vector<modint>::iterator vt=K[i].begin(),vtend=K[i].end(),tmpt=tmpused.begin();
	vector<char>::const_iterator ut=used.begin();
	for (;vt!=vtend;++ut,++vt){
	  if (!*ut)
	    continue;
	  *tmpt=*vt;
	  ++tmpt;
	}
	swap(K[i],tmpused); // this will destroy K[i] allocated mem area
      }
      vecteur pivots; vector<int> maxrankcols; longlong idet;
      // cerr << K << endl;
      smallmodrref(K,pivots,permutation,maxrankcols,idet,0,K.size(),0,usedcount,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);
    }
    else { // else interreduce
      used=vector<char>(N,1);
    }
    // cerr << K << "," << permutation << endl;
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it=R.coord.begin(),itend=R.coord.end();
    vector<int> permu;
    if (interreduce)
      permu=perminv(permutation);
    if (debug_infolevel>1)
      cerr << clock() << " f4v interreduced" << endl;
    for (i=0;i<f4vG.size();++i){
      // cerr << v << endl;
      vector< T_unsigned<modint,tdeg_t> > & Pcoord=f4v[f4vG[i]].coord;
      Pcoord.clear();
      vector<modint> & v =K[interreduce?permu[i]:i];
      unsigned vcount=0;
      vector<modint>::const_iterator vt=v.begin(),vtend=v.end();
      for (;vt!=vtend;++vt){
	if (*vt)
	  ++vcount;
      }
      Pcoord.reserve(vcount);
      vector<char>::const_iterator ut=used.begin();
      for (vt=v.begin(),it=R.coord.begin();it!=itend;++ut,++it){
	if (!*ut)
	  continue;
	modint coeff=*vt;
	++vt;
	if (coeff!=0)
	  Pcoord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->u));
      }
      if (!Pcoord.empty() && Pcoord.front().g!=1){
	smallmultmod(invmod(Pcoord.front().g,env),f4v[f4vG[i]],env);	
	Pcoord.front().g=1;
      }
    }
  }
#endif

  void rref_f4mod(vectpolymod & f4v,vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,const polymod & R,modint env,vector<int> & permutation,bool split){
    vector<unsigned> f4vG(f4v.size());
    for (unsigned i=0;i<f4v.size();++i)
      f4vG[i]=i;
#if 0
    rref_f4mod(f4v,f4vG,res,G,excluded,quo,R,env,permutation,true);
#else
    if (//1
	split
	//0
	)
      rref_f4modsplit_interreduce(f4v,f4vG,res,G,excluded,quo,R,env,permutation);
    else
      rref_f4mod_interreduce(f4v,f4vG,res,G,excluded,quo,R,env,permutation);
#endif
  }

  struct info_t {
    vectpolymod quo,quo2;
    polymod R,R2;
    vector<int> permu;
    vector< pair<unsigned,unsigned> > B;
    vector<unsigned> G;
  };

  void reducemodf4(vectpolymod & f4v,vectpolymod & res,vector<unsigned> & G,unsigned excluded, modint env,info_t & info_tmp){
    polymod allf4(f4v.front().order,f4v.front().dim),rem(f4v.front().order,f4v.front().dim);
    if (debug_infolevel>1)
      cerr << clock() << " f4 begin collect monomials" << f4v.size() << endl;
    // collect all terms in f4v
    collect(f4v,allf4);
    if (debug_infolevel>1)
      cerr << clock() << " f4 symbolic preprocess" << endl;
    // find all monomials required to reduce all polymod in f4 with res[G[.]]
    in_heap_reducemod(allf4,res,G,excluded,info_tmp.quo,rem,&info_tmp.R,env);
    if (debug_infolevel>1)
      cerr << clock() << " f4 end symbolic preprocess" << endl;
    // build a matrix with first lines res[G[.]]*quo[.] in terms of monomials in S
    // and finishing with lines of f4v
    // rref (below) the matrix and find the last lines in f4v
    rref_f4mod(f4v,res,G,excluded,info_tmp.quo,info_tmp.R,env,info_tmp.permu,true); // do splitting
  }

#else

  void fake_smallsub(const polymod &v1,const polymod & v2,bool cancel,polymod & v,annuaire & S){
    // like smallsub but does not do any arithmetic operation
    // except cancelling term leading terms of v1 and v2 if cancel is true
    // also updates S with all momonials
    vector< T_unsigned<modint,tdeg_t> >::const_iterator it1=v1.coord.begin(),it1end=v1.coord.end(),it2=v2.coord.begin(),it2end=v2.coord.end();
    v.coord.clear();
    v.coord.reserve((it1end-it1)+(it2end-it2)); // worst case
    if (cancel){
      S[it1->u]=0;
      ++it1; ++it2;
    }
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	S[it1->u]=0;
	v.coord.push_back(*it1);
	++it1;
	++it2;
      }
      else {
	if (tdeg_t_strictly_greater(it1->u,it2->u,v1.order)){
	  S[it1->u]=0;
	  v.coord.push_back(*it1);
	  ++it1;
	}
	else {
	  S[it2->u]=0;
	  v.coord.push_back(*it2);
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1){
      S[it1->u]=0;
      v.coord.push_back(*it1);
    }
    for (;it2!=it2end;++it2){
      S[it2->u]=0;
      v.coord.push_back(*it2);
    }
  }

  void fake_reduce(const polymod & p,const vectpolymod & res,const vector<unsigned> & G,unsigned excluded,vectpolymod & quo,polymod & rem,polymod & TMP1, polymod & TMP2,annuaire & S){
    // like reduce but does not do any arithmetic operation
    if (&p!=&rem)
      rem=p;
    if (quo.size()<G.size())
      quo.resize(G.size());
    for (unsigned i=0;i<G.size();++i){
      quo[i].coord.clear();
      quo[i].order=p.order;
      quo[i].dim=p.dim;
    }
    S.clear();
    if (p.coord.empty())
      return ;
    unsigned i,rempos=0;
    for (unsigned count=0;;++count){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator pt=rem.coord.begin()+rempos,ptend=rem.coord.end();
      // this branch search first in all leading coeff of G for a monomial 
      // <= to the current rem monomial
      if (pt>=ptend)
	break;
      for (i=0;i<G.size();++i){
	if (i==excluded || res[G[i]].coord.empty())
	  continue;
	if (tdeg_t_all_greater(pt->u,res[G[i]].coord.front().u,p.order))
	  break;
      }
      if (i==G.size()){ // no leading coeff of G is smaller than the current coeff of rem
	S[pt->u]=0;
	++rempos;
	continue;
      }
      tdeg_t m=pt->u-res[G[i]].coord.front().u;
      quo[i].coord.push_back(T_unsigned<modint,tdeg_t>(1,m));
      TMP1.coord=res[G[i]].coord;
      if (pt->u!=res[G[i]].coord.front().u)
	smallshift(TMP1.coord,m,TMP1.coord);
      TMP2.coord.clear();
      fake_smallsub(rem,TMP1,true,TMP2,S);
      swap(rem.coord,TMP2.coord);
    }
  }

  void makeline(const polymod & p,const tdeg_t * shiftptr,annuaire & S,unsigned N,vector<modint> & v){
    v.resize(N); // size of S
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	v[S[it->u+*shiftptr]]=it->g;
      }
    }
    else {
      for (;it!=itend;++it){
	v[S[it->u]]=it->g;
      }
    }
  }

  void makeline(const polymod & p,const tdeg_t * shiftptr,annuaire & S,vector<sparse_element> & v){
    std::vector< T_unsigned<modint,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (shiftptr){
      for (;it!=itend;++it){
	v.push_back(sparse_element(it->g,S[it->u+*shiftptr]));
      }
    }
    else {
      for (;it!=itend;++it){
	v.push_back(sparse_element(it->g,S[it->u]));
      }
    }
  }

  void rref_f4mod(vectpolymod & f4v,const vectpolymod & res,const vector<unsigned> & G,unsigned excluded,const vectpolymod & quo,annuaire & S,modint env,vector<int> & permutation){
    // step1: stores right indices in S
    annuaire::iterator it=S.begin(),itend=S.end();
    unsigned N=S.size(),i=0,j=0; // size of S, index
    for (;it!=itend;++i,++it){
      //cerr << it->first << ",";
      it->second=N-1-i;
    }
    // step2: for each monomials of quo[i], shift res[G[i]] by monomial
    // set coefficient in a line of a matrix M, columns are S monomials indices
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
    vector< vector<sparse_element> > M;
    M.reserve(N);
    for (i=0;i<quo.size();++i){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vector<sparse_element>(0));
	makeline(res[G[i]],&jt->u,S,M[j]);
      }
    }
    if (debug_infolevel>1)
      cerr << clock() << " end build M" << endl;
    // should not sort but compare res[G[i]]*quo[i] monomials to build M already sorted
    // cerr << "before sort " << M << endl;
    sort(M.begin(),M.end(),tri);
    if (debug_infolevel>1)
      cerr << clock() << " M sorted" << endl;
    // cerr << "after sort " << M << endl;
    // step3 reduce
    // perhaps better to do simultaneous reduction?
    vector< vector<modint> > K(f4v.size());
    for (i=0;i<f4v.size();++i){
      vector<modint> &v=K[i];
      v.resize(N);
      makeline(f4v[i],0,S,N,v);
      // cerr << v << endl;
      reducef4(v,M,env);
    }
    if (debug_infolevel>1)
      cerr << clock() << " f4v reduced" << f4v.size() << endl;
    vecteur pivots; vector<int> maxrankcols; longlong idet;
    smallmodrref(K,pivots,permutation,maxrankcols,idet,0,K.size(),0,N,1/* fullreduction*/,0/*dontswapbelow*/,env,0/* rrefordetorlu*/);    
    if (debug_infolevel>1)
      cerr << clock() << " f4v interreduced" << endl;
    for (i=0;i<f4v.size();++i){
      // cerr << v << endl;
      f4v[i].coord.clear();
      vector<modint> & v =K[i];
      for (j=0,it=S.begin();it!=itend;++it,++j){
	modint coeff=v[N-1-j];
	if (coeff!=0)
	  f4v[i].coord.push_back(T_unsigned<modint,tdeg_t>(coeff,it->first));
      }
      reverse(f4v[i].coord.begin(),f4v[i].coord.end());
      if (!f4v[i].coord.empty() && f4v[i].coord.front().g!=1){
	smallmultmod(invmod(f4v[i].coord.front().g,env),f4v[i],env);	
	f4v[i].coord.front().g=1;
      }
    }
  }

  void reducemodf4(vectpolymod & f4v,vectpolymod & res,vector<unsigned> & G,unsigned excluded, modint env,info_t & info_tmp){
    annuaire S; // revlex ordering
#if 1
    polymod allf4(f4v.front().order,f4v.front().dim),TMP1(allf4.order,allf4.dim),TMP2(allf4.order,allf4.dim);
    if (debug_infolevel>1)
      cerr << clock() << " f4 begin collect monomials" << f4v.size() << endl;
    collect(f4v,allf4);
#else
    polymod allf4(f4v.front()),R(f4v.front().order,f4v.front().dim),TMP1(allf4.order,allf4.dim),TMP2(allf4.order,allf4.dim);
    if (debug_infolevel>1)
      cerr << clock() << " f4 begin collect monomials" << f4v.size() << endl;
    // collect all terms in f4v
    for (unsigned i=1;i<f4v.size();++i){
      fake_smallsub(f4v[i],allf4,false /* do not cancel leading terms*/,R,S); 
      swap(allf4.coord,R.coord);
    }
#endif
    if (debug_infolevel>1)
      cerr << clock() << " f4 symbolic preprocess" << endl;
    // find all monomials required to reduce all polymod in f4 with res[G[.]]
    fake_reduce(allf4,res,G,excluded,quo,R,TMP1,TMP2,S);
    if (debug_infolevel>1)
      cerr << clock() << " f4 end symbolic preprocess" << endl;
    // build a matrix with first lines res[G[.]]*quo[.] in terms of monomials in S
    // and finishing with lines of f4v
    // rref (below) the matrix and find the last lines in f4v
    rref_f4mod(f4v,res,G,excluded,quo,S,env,info_tmp.permutation);
  }

#endif

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

  bool in_gbasisf4mod(vectpoly8 & res8,vectpolymod &res,vector<unsigned> & G,modint env,bool totdeg,vector< pair<unsigned,unsigned> > * pairs_reducing_to_zero,vector< info_t > * f4_info,bool recomputeR){
    convert(res8,res,env);
    unsigned ressize=res8.size();
    unsigned learned_position=0,f4_info_position=0;
    bool sugar=false,learning=pairs_reducing_to_zero && pairs_reducing_to_zero->empty();
    if (debug_infolevel>1000)
      res.dbgprint(); // instantiate dbgprint()
    polymod TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vector< pair<unsigned,unsigned> > B,BB;
    B.reserve(256); BB.reserve(256);
    vector<unsigned> smallposv;
    smallposv.reserve(256);
    info_t information;
    short order=res.front().order;
    if (order==_PLEX_ORDER)
      totdeg=false;
    for (unsigned l=0;l<ressize;++l){
      gbasis_updatemod(G,B,res,l,TMP2,env,true);
    }
    for (;!B.empty() && !interrupted && !ctrl_c;){
      if (debug_infolevel>1)
	cerr << clock() << " begin new iteration mod, number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small,cur;
      unsigned smallpos,smalltotdeg=0,curtotdeg=0,smallsugar=0,cursugar=0;
      smallposv.clear();
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
	if (interrupted || ctrl_c)
	  return false;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small,order);
      smallsugar=res[B[smallpos].first].sugar+(small-res[B[smallpos].first].coord.front().u).total_degree();
      smalltotdeg=small.total_degree();
      smallposv.push_back(smallpos);
      for (unsigned i=smallpos+1;i<B.size();++i){
	if (interrupted || ctrl_c)
	  return false;
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	bool doswap=false;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	cursugar=res[B[smallpos].first].sugar+(cur-res[B[smallpos].first].coord.front().u).total_degree();
	curtotdeg=cur.total_degree();
	if ( !totdeg || order==_PLEX_ORDER)
	  doswap=tdeg_t_strictly_greater(small,cur,order);
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
	  // cerr << "swap mod " << curtotdeg << " " << res[B[i].first].coord.front().u << " " << res[B[i].second].coord.front().u << endl;
	  swap(small,cur); // small=cur;
	  smallpos=i;
	  smallposv.clear();
	  smallposv.push_back(i);
	}
	else {
	  if (totdeg && curtotdeg==smalltotdeg && (!sugar || cursugar==smallsugar))
	    smallposv.push_back(i);
	}
      }
      if (smallposv.size()<=GBASIS_F4){
	unsigned i=smallposv[0];
	pair<unsigned,unsigned> bk=B[i];
	B.erase(B.begin()+i);
	if (!learning && pairs_reducing_to_zero && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	  if (debug_infolevel>2)
	    cerr << bk << " learned " << learned_position << endl;
	  ++learned_position;
	  continue;
	}
	if (debug_infolevel>2)
	  cerr << bk << " not learned " << learned_position << endl;
	if (debug_infolevel>2 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	  cerr << clock() << " mod reducing pair with 1 element not in basis " << bk << endl;
	// polymod h(res.front().order,res.front().dim);
	spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
	if (debug_infolevel>1){
	  cerr << clock() << " mod reduce begin, pair " << bk << " spoly size " << TMP1.coord.size() << " sugar degree " << TMP1.sugar << " totdeg deg " << TMP1.coord.front().u.total_degree() << " degree " << TMP1.coord.front().u << endl;
	}
#if 0 // def GBASIS_HEAP
	heap_reducemod(TMP1,res,G,-1,information.quo,TMP2,env);
	swap(TMP1.coord,TMP2.coord);
#else
	reducemod(TMP1,res,G,-1,TMP1,env);
#endif
	if (debug_infolevel>1){
	  if (debug_infolevel>3){ cerr << TMP1 << endl; }
	  cerr << clock() << " mod reduce end, remainder size " << TMP1.coord.size() << " begin gbasis update" << endl;
	}
	if (!TMP1.coord.empty()){
	  if (ressize==res.size())
	    res.push_back(polymod(TMP1.order,TMP1.dim));
	  swap(res[ressize].coord,TMP1.coord);
	  ++ressize;
#if GBASIS_POSTF4==0
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,false);
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true);
#endif
	  if (debug_infolevel>3)
	    cerr << clock() << " mod basis indexes " << G << " pairs indexes " << B << endl;
	}
	else {
	  if (learning && pairs_reducing_to_zero){
	    if (debug_infolevel>2)
	      cerr << "learning " << bk << endl;
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
	for (int i=smallposv.size()-1;i>=0;--i)
	  B.erase(B.begin()+smallposv[i]);
      }
      if (debug_infolevel>1)
	cerr << clock() << " Computing s-polys " << smallposv.size() << endl;
      vectpolymod f4v; // collect all spolys 
      for (unsigned i=0;i<smallposp.size();++i){
	pair<unsigned,unsigned> bk=smallposp[i];
	if (!learning && pairs_reducing_to_zero && f4_info && learned_position<pairs_reducing_to_zero->size() && bk==(*pairs_reducing_to_zero)[learned_position]){
	  if (debug_infolevel>2)
	    cerr << bk << " f4 learned " << learned_position << endl;
	  ++learned_position;
	  TMP1.coord.clear();
	  f4v.push_back(TMP1);
	  continue;
	}
	if (debug_infolevel>2)
	  cerr << bk << " f4 not learned " << learned_position << endl;
	if (debug_infolevel>2 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	  cerr << clock() << " mod reducing pair with 1 element not in basis " << bk << endl;
	// polymod h(res.front().order,res.front().dim);
	spolymod(res[bk.first],res[bk.second],TMP1,TMP2,env);
	f4v.push_back(TMP1);
      }
      if (f4v.empty())
	continue;
      // reduce spolys in f4v
      if (debug_infolevel>1)
	cerr << clock() << " base size " << G.size() << " reduce f4 begin on " << f4v.size() << " pairs" << endl;
      if (!learning && f4_info && f4_info_position<f4_info->size()){
	info_t & info=(*f4_info)[f4_info_position];
	// apply(perminv(info.permu),f4v);
	if (recomputeR){
	  swap(information.permu,info.permu);
	  reducemodf4(f4v,res,G,-1,env,info);
	  swap(information.permu,info.permu);
	}
	else
	  rref_f4mod(f4v,res,G,-1,info.quo,info.R,env,information.permu,false); // don't split
	// apply(info.permu,f4v);
	// information.permu should be identity, otherwise the whole learning process failed
	for (unsigned j=0;j<information.permu.size();++j){
	  if (information.permu[j]!=info.permu[j]){
	    cerr << "learning failed"<<endl;
	    return false;
	  }
	}
	++f4_info_position;
      }
      else {
	reducemodf4(f4v,res,G,-1,env,information);
	if (learning && f4_info){
#if 0
	  f4_info->push_back(information);
#else
	  info_t tmp;
	  f4_info->push_back(tmp);
	  info_t & i=f4_info->back();
	  swap(i.quo,information.quo);
	  swap(i.permu,information.permu);
	  swap(i.R.coord,information.R.coord);
	  i.R.order=information.R.order;
	  i.R.dim=information.R.dim;
#endif
	}
      }
      // update gbasis and learning
      // requires that Gauss pivoting does the same permutation for other primes
      if (learning && pairs_reducing_to_zero){
	for (unsigned i=0;i<f4v.size();++i){
	  if (f4v[i].coord.empty()){
	    if (debug_infolevel>2)
	      cerr << "learning f4 " << smallposp[i] << endl;
	    pairs_reducing_to_zero->push_back(smallposp[i]);
	  }
	}
      }
      unsigned added=0;
      for (unsigned i=0;i<f4v.size();++i){
	if (!f4v[i].coord.empty())
	  ++added;
      }
      if (debug_infolevel>1)
	cerr << clock() << " reduce f4 end on " << added << " from " << f4v.size() << " pairs, gbasis update begin" << endl;
      for (unsigned i=0;i<f4v.size();++i){
	if (!f4v[i].coord.empty()){
	  if (ressize==res.size())
	    res.push_back(polymod(TMP1.order,TMP1.dim));
	  swap(res[ressize].coord,f4v[i].coord);
	  ++ressize;
#ifdef GBASIS_POSTF4
#if GBASIS_POSTF4==0
	  if (learning || !f4_info || f4_info_position-1>=f4_info->size())
	    gbasis_updatemod(G,B,res,ressize-1,TMP2,env,false);
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,added<=GBASIS_F4);
#endif
#else
	  gbasis_updatemod(G,B,res,ressize-1,TMP2,env,true);
#endif
	}
	else {
	  // if (!learning && pairs_reducing_to_zero)  cerr << " error learning "<< endl;
	}
      }
#if GBASIS_POSTF4==0
      if (!learning && f4_info && f4_info_position-1<f4_info->size()){
	B=(*f4_info)[f4_info_position-1].B;
	G=(*f4_info)[f4_info_position-1].G;
	continue;
      }
      if (learning && f4_info){
	f4_info->back().B=B;
	f4_info->back().G=G;
      }
#endif
      unsigned debut=G.size()-added;
#if GBASIS_POSTF4>0
      if (added>GBASIS_F4){
	// final interreduce 
	vector<unsigned> G1(G.begin(),G.begin()+debut);
	vector<unsigned> G2(G.begin()+debut,G.end());
	vector<int> permu2;
	if (!learning && f4_info){
	  const info_t & info=(*f4_info)[f4_info_position-1];
	  rref_f4mod_nointerreduce(res,G1,res,G2,-1,info.quo2,info.R2,env,permu2);
	}
	else {
	  information.R2.order=TMP1.order;
	  information.R2.dim=TMP1.dim;
	  TMP1.coord.clear();
	  if (debug_infolevel>1)
	    cerr << clock() << " collect monomials from old basis" << endl;
	  collect(res,G1,TMP1); // collect all monomials in res[G[0..debut-1]]
	  // in_heap_reducemod(TMP1,res,G2,-1,info_tmp.quo2,TMP2,&info_tmp.R2,env);
	  in_heap_reducemod(TMP1,res,G2,-1,information.quo2,TMP2,&information.R2,env);
	  rref_f4mod_nointerreduce(res,G1,res,G2,-1,information.quo2,information.R2,env,permu2);
	  if (f4_info){
	    info_t & i=f4_info->back();
	    swap(i.quo2,information.quo2);
	    swap(i.R2.coord,information.R2.coord);
	    i.R2.order=TMP1.order;
	    i.R2.dim=TMP1.dim;
	  }
	}
      }
#endif
      // cerr << "finish loop G.size "<<G.size() << endl;
      // cerr << added << endl;
    }
#if GBASIS_POSTF4==0
    // final interreduce step2
    for (unsigned j=1; j<G.size();++j){
      polymod & h=res[G[j]];
      if (h.coord.empty())
	continue;
      for (unsigned i=0;i<j;++i){
	if (!res[G[i]].coord.empty())
	  // reduce res[G[i]] with respect to h
	  reduce1smallmod(res[G[i]],h,TMP2,env);
      }
    }
#endif
    if (ressize<res.size())
      res.resize(ressize);
    // sort(res.begin(),res.end(),tripolymod);
    convert(res,res8,env);
    return true;
  }
#endif // GBASIS_F4

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
      }
#endif
    }
    else {
      if (debug_infolevel)
	cerr << "chinrem: sizes differ " << P.coord.size() << "," << Q.coord.size() << endl;
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
	  cerr << "chinrem: exponent mismatch using first " << endl;
	gen g=it->g-u*(it->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
	++it;
      }
      else {
	if (debug_infolevel)
	  cerr << "chinrem: exponent mismatch using second " << endl;
	gen g=u*(jt->g)*pmod;
	tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),jt->u));
	++jt;
      }
    }
    for (;it!=itend;++it){
      if (debug_infolevel)
	cerr << "chinrem: exponent mismatch at end using first " << endl;
      gen g=it->g-u*(it->g)*pmod;
      tmp.coord.push_back(T_unsigned<gen,tdeg_t>(smod(g,pqmod),it->u));
    }
    for (;jt!=jtend;++jt){
      if (debug_infolevel)
	cerr << "chinrem: exponent mismatch at end using second " << endl;
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

  static bool fracmod(const poly8 &P,const gen & p,
	       mpz_t & d,mpz_t & d1,mpz_t & absd1,mpz_t &u,mpz_t & u1,mpz_t & ur,mpz_t & q,mpz_t & r,mpz_t &sqrtm,mpz_t & tmp,
	       poly8 & Q){
    Q.coord.clear();
    Q.coord.reserve(P.coord.size());
    Q.dim=P.dim;
    Q.order=P.order;
    Q.sugar=P.sugar;
    for (unsigned i=0;i<P.coord.size();++i){
      gen g=P.coord[i].g,num,den;
      if (g.type==_INT_)
	g.uncoerce();
      if ( (g.type!=_ZINT) || (p.type!=_ZINT) ){
	cerr << "bad type"<<endl;
	return false;
      }
      if (!in_fracmod(p,g,d,d1,absd1,u,u1,ur,q,r,sqrtm,tmp,num,den))
	return false;
      if (num.type==_ZINT && mpz_sizeinbase(*num._ZINTptr,2)<=30)
	num=int(mpz_get_si(*num._ZINTptr));
      if (den.type==_ZINT && mpz_sizeinbase(*den._ZINTptr,2)<=30)
	den=int(mpz_get_si(*den._ZINTptr));
      if (is_positive(den,context0)) // ok
	g=fraction(num,den);
      else
	g=fraction(-num,-den);
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


  void collect(const vectpoly8 & f4v,polymod & allf4){
    vectpoly8::const_iterator it=f4v.begin(),itend=f4v.end();
    vector<heap_tt> H;
    H.reserve(itend-it);
    for (unsigned i=0;it!=itend;++i,++it){
      if (!it->coord.empty())
	H.push_back(heap_tt(i,0,it->coord.front().u));
    }
    make_heap(H.begin(),H.end());
    while (!H.empty()){
      std::pop_heap(H.begin(),H.end());
      // push root node of the heap in allf4
      heap_tt & current =H.back();
      if (allf4.coord.empty() || allf4.coord.back().u!=current.u)
	allf4.coord.push_back(T_unsigned<modint,tdeg_t>(1,current.u));
      ++current.polymodpos;
      if (current.polymodpos>=f4v[current.f4vpos].coord.size()){
	H.pop_back();
	continue;
      }
      current.u=f4v[current.f4vpos].coord[current.polymodpos].u;
      std::push_heap(H.begin(),H.end());
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
    return v.size();
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
	    v.push_back(sparse_gen(it->g,jt-R.coord.begin()));
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
	    v.push_back(sparse_gen(it->g,jt-R.coord.begin()));
	    ++jt;
	    break;
	  }
	}
      }
    }
  }

#ifdef __x86_64__
  bool checkreducef4_64(vector<modint> &v,vector<modint> & coeff,const vector< vector<sparse_element> > & M,modint env,vector<int128_t> & w){
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
      // cerr << "multiplier ok line " << i << " value " << c << " " << w << endl;
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
#endif

  // return true if v reduces to 0
  // in addition to reducef4, compute the coeffs
  bool checkreducef4(vector<modint> &v,vector<modint> & coeff,const vector< vector<sparse_element> > & M,modint env){
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
    cerr << "Unknow type in reconstruction " << a << endl;
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
    unsigned s=p.coord.size();
    int lc=smod(v.coord[0].g,m).val;
    for (unsigned i=0;i<s;++i){
      if (!chk_equal_mod(v.coord[i].g,(longlong(lc)*p.coord[i].g)%m,m))
	return false;
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
    unsigned s=p.coord.size();
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
  bool checkf4(vectpoly8 & f4v,const vectpoly8 & res,vector<unsigned> & G,unsigned excluded,double eps){
    if (f4v.empty())
      return true;
    polymod allf4(f4v.front().order,f4v.front().dim),rem(allf4);
    vectpolymod resmod,quo;
    convert(res,resmod,0);
    if (debug_infolevel>1)
      cerr << clock() << " checkf4 begin collect monomials" << f4v.size() << endl;
    // collect all terms in f4v
    collect(f4v,allf4);
    if (debug_infolevel>1)
      cerr << clock() << " checkf4 symbolic preprocess" << endl;
    // find all monomials required to reduce allf4 with res[G[.]]
    polymod R;
    in_heap_reducemod(allf4,resmod,G,excluded,quo,rem,&R,0);
    if (debug_infolevel>1)
      cerr << clock() << " checkf4 end symbolic preprocess" << endl;
    // build a matrix with rows res[G[.]]*quo[.] in terms of monomials in allf4
    // sort the matrix
    // checking reduction to 0 is equivalent to
    // write a line from f4v[]
    // as a linear combination of the lines of this matrix
    // we will do that modulo a list of primes
    // and keep track of the coefficients of the linear combination (the quotients)
    // we reconstruct the quotients in Q by fracmod
    // once they stabilize, we compute the lcm l of the denominators
    // we multiply by l to have an equality on Z
    // we compute bounds on the coefficients of the products res*quo
    // and on l*f4v, and we check further the equality modulo additional
    // primes until the equality is proved
    if (debug_infolevel>1)
      cerr << clock() << " begin build M" << endl;
    vector< vector<sparse_gen> > M;
    vector<sparse_element> atrier;
    unsigned N=R.coord.size(),i,j=0,nterms=0;
    M.reserve(N); // actual size is at most N (difference is the remainder part size)
    for (i=0;i<res.size();++i){
      std::vector< T_unsigned<modint,tdeg_t> >::const_iterator jt=quo[i].coord.begin(),jtend=quo[i].coord.end();
      for (;jt!=jtend;++j,++jt){
	M.push_back(vector<sparse_gen>(0));
	makeline(res[G[i]],&jt->u,R,M[j]);
	nterms += M[j].size();
	atrier.push_back(sparse_element(M[j].front().pos,j));
      }
    }
    sort(atrier.begin(),atrier.end(),tri1);
    vector< vector<sparse_gen> > M1(atrier.size());
    for (i=0;i<atrier.size();++i){
      swap(M1[i],M[atrier[i].pos]);
    }
    swap(M,M1);
    // cerr << M << endl;
    if (debug_infolevel>0)
      cerr << clock() << " rows, columns, terms: " << M.size() << "x" << N << "=" << nterms << endl; 
    // PSEUDO_MOD is not interesting here since there is no inter-reduction
    gen p(int(longlong(1<<31)-1));
    gen pip(1);
    vectpolymod f4vmod;
    matrice coeffmat;
    vector< vector<modint> > coeffmatmodp(f4v.size(),vector<modint>(M.size()));
    gen bres=linfnorm(res,context0);
    gen bf4=linfnorm(f4v,context0);
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
      // reduce f4v and stores coefficients of quotients for f4v[i] in coeffmat[i]
      convert(f4v,f4vmod,env);
      if (debug_infolevel>0)
	cerr << clock() << " checking mod " << p << endl;
      vector<modint> v;
      unsigned countres=0;
#ifdef __x86__64__
      vector<int128_t> v128;
#endif
      for (unsigned i=0;i<f4vmod.size();++i){
	makeline(f4vmod[i],0,R,v);
#ifdef __x86__64__
	if (!checkreducef4_64(v,coeffmatmodp[i],Mp,env,v128))
	  return false;
#else
	if (!checkreducef4(v,coeffmatmodp[i],Mp,env))
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
	    cerr << clock() << " modular check finished " << endl;
	  return true;
	}
      }
      // combine coeffmat with previous one by chinese remaindering
      if (debug_infolevel>0)
	cerr << clock() << " chinrem mod " << p << endl;
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
	    cerr << clock() << " unstable mod " << p << " reconstructed " << prevmatq.size() << endl;
	  continue;
	}
	matrice coeffmatq=*_copy(checkquo,context0)._VECTptr;
	if (debug_infolevel>0)
	  cerr << clock() << " full stable mod " << p << endl;
	stable=true;
	gen lall=1; vecteur l(coeffmatq.size());
	for (unsigned i=0;i<coeffmatq.size();++i){
	  lcmdeno(*coeffmatq[i]._VECTptr,l[i],context0);
	  if (is_strictly_greater(l[i],lall,context0))
	    lall=l[i];
	}
	if (debug_infolevel>0)
	  cerr << clock() << " lcmdeno ok/start bound " << p << endl;
	gen ball=1,bi; // ball is the max bound of all coeff in coeffmatq
	for (unsigned i=0;i<coeffmatq.size();++i){
	  bi=linfnorm(coeffmatq[i],context0);
	  if (is_strictly_greater(bi,ball,context0))
	    ball=bi;
	}
	// bound for res and f4v
	bound=bres*ball;
	gen bound2=lall*bf4;
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
      cerr << "basis size " << res.size() << endl;
    // build possible pairs (i,j) with i<j
    vector< vector<tdeg_t> > lcmpairs(res.size());
    vector<unsigned> G(res.size());
    for (unsigned i=0;i<res.size();++i)
      G[i]=i;
    vectpoly8 vtmp,tocheck;
    tocheck.reserve(res.size()*10); // wild guess
    int order=res.front().order,dim=res.front().dim;
    poly8 TMP1(order,res.front().dim),TMP2(TMP1),
      spol(TMP1),spolred(TMP1);
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
	cerr << "checking pairs for i="<<i<<", j=";
      const poly8 & resi = res[i];
      const tdeg_t & resi0=resi.coord.front().u;
      for (unsigned j=i+1;j<res.size();++j){
	if (interrupted || ctrl_c){
	  cerr << "Check interrupted, assuming Groebner basis. Press Ctrl-C again to interrupt computation" << endl;
	  interrupted=ctrl_c=false;
	  return true;
	}
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
	  cerr <<  j << ",";
	spoly(resi,res[j],spol,TMP1,0);
	tocheck.push_back(poly8(order,dim));
	swap(tocheck.back(),spol);
      } // end j loop
      if (debug_infolevel>1)
	cerr << endl;
    }
    if (debug_infolevel>0)
      cerr << "Number of critical pairs to check " << tocheck.size() << endl;
    if (eps>0 || modularcheck) // modular check is sometimes slow
      return checkf4(tocheck,res,G,-1,eps); 
    // integer check
    for (unsigned i=0;i<tocheck.size();++i){
      reduce(tocheck[i],res,G,-1,vtmp,spolred,TMP1,TMP2,0);
      // gen den; heap_reduce(spol,res,G,-1,vtmp,spolred,TMP1,den,0);
      if (!spolred.coord.empty())
	return false;
      if (debug_infolevel>0)
	cerr << "+";
    }
    if (debug_infolevel)
      cerr << endl << "Successfull check of " << tocheck.size() << " critical pairs" << endl;
    return true;
  }

  bool mod_gbasis(vectpoly8 & res,bool modularcheck,GIAC_CONTEXT){
    unsigned initial=res.size();
    double eps=proba_epsilon(contextptr);
    for (unsigned i=0;i<res.size();++i){
      const poly8 & P=res[i];
      for (unsigned j=0;j<P.coord.size();++j){
	if (!is_integer(P.coord[j].g)) // improve: accept complex numbers
	  return false;
      }
    }
    vectpoly8 current,gb,vtmp,afewpolys;
    vectpolymod resmod;
    poly8 poly8tmp;
#if 0
    // make first iteration with a smaller prime to spare memory
    // next iterations will use larger primes
    gen p=1<<24;
#else
    gen p=(1<<29)-_floor(giac_rand(contextptr)/1e3,contextptr);
#endif
    vector< vectpoly8> V; // list of (chinrem reconstructed) modular groebner basis
    vector< vectpoly8> W; // list of rational reconstructed groebner basis
    vector< vectpoly8> Wlast;
    vecteur P; // list of associate (product of) modulo
    environment env;
    vector<unsigned> G;
    vector< pair<unsigned,unsigned> > reduceto0;
    vector< info_t > f4_info;
    f4_info.reserve(100);
    env.moduloon=true;
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
    // unless we are unlucky these lists should contain only 1 element
    for (int count=0;ok;++count){
#if 1 
      if (count==1 && p.val<(1<<24)){
#ifdef PSEUDO_MOD
	p=(1<<29)-1;
#else
	p=(1<<30)+((1<<30)-1);
#endif
      }
      p=prevprime(p-1); 
#else
      p=nextprime(p+1);
#endif
      // compute gbasis mod p (this could be parallelized)
      env.modulo=p;
      current=res;
      G.clear();
      if (debug_infolevel)
	cerr << clock() << " begin computing basis modulo " << p << endl;
#ifdef GBASIS_F4 
      if (!in_gbasisf4mod(current,resmod,G,p.val,true/*totaldeg*/,
			  //		  0,0
			  &reduceto0,&f4_info,
#if 1
			  false /* not useful */
#else
			  (count==1) /* recompute R and quo at 2nd iteration*/
#endif
			  )){
	// retry 
	reduceto0.clear();
	f4_info.clear();
	if (!in_gbasisf4mod(current,resmod,G,p.val,true/*totaldeg*/,&reduceto0,&f4_info,false)){
	  ok=false;
	  break;
	}
      }
#else
      if (!in_gbasismod(current,resmod,G,p.val,true,&reduceto0)){
	ok=false;
	break;
      }
      // cerr << "reduceto0 " << reduceto0.size() << endl;
      //if (!in_gbasis(current,G,&env)) return false;
#endif
      if (debug_infolevel){
	cerr << clock() << " end, basis size " << G.size() << " prime number " << count+1 << endl;
	if (count==0)
	  cerr << "G=" << G << endl;
      }
      // extract from current
      if (gb.size()<G.size())
	gb.resize(G.size());
      unsigned i=0;
      for (;i<G.size();++i){
	gb[i]=current[G[i]];
      }
      // compare gb to existing computed basis
      for (i=0;i<V.size();++i){
	if (debug_infolevel)
	  cerr << clock() << " i= " << i << " begin chinese remaindering" << endl;
	int r=chinrem(V[i],P[i],gb,p,poly8tmp);
	if (debug_infolevel)
	  cerr << clock() << " end chinese remaindering" << endl;
	if (r==-1){
	  ok=false;
	  break;
	}
	if (r==0){
	  cerr << clock() << " leading terms do not match with reconstruction " << i << " modulo " << p << endl;
	  continue;
	}
	// found one! V is already updated, update W
	P[i]=P[i]*p;
	if (W.size()<V.size())
	  W.resize(V.size());
	if (Wlast.size()<V.size())
	  Wlast.resize(V.size());
#if 1
	if (W[i].empty()){
	  // rational reconstruction of a few elements only, once it is stable
	  // we reconstruct the whole W
	  afewpolys.clear();
	  int jpos=0;
	  for (int j=V[i].size()-1;j>=0;++jpos,j-=20){
	    if (int(Wlast[i].size())>jpos && chk_equal_mod(Wlast[i][jpos],gb[j],p.val))
	      afewpolys.push_back(Wlast[i][jpos]);
	    else {
	      if (!fracmod(V[i][j],P[i],
			   zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,
			   poly8tmp)){
		cerr << clock() << " reconstruction failure at position " << j << endl;
		// ok=false;
		break;
	      }
	      afewpolys.push_back(poly8tmp);
	    }
	  }
	  for (jpos=0;jpos<afewpolys.size() && jpos<Wlast[i].size();++jpos){
	    if (!(afewpolys[jpos]==Wlast[i][jpos]))
	      break;
	  }
	  if (jpos!=afewpolys.size()){
	    if (debug_infolevel)
	      cerr << clock() << " reconstructed " << jpos << " of " << V[i].size()/20 << endl;
	    swap(afewpolys,Wlast[i]);
	    break;
	  }
	  else {
	    if (debug_infolevel)
	      cerr << clock() << " last component same " << endl;
	  }
	}
	if (W[i].empty()){
	  if (debug_infolevel)
	    cerr << clock() << " begin rational reconstruction mod " << P[i] << endl;
	  if (!fracmod(V[i],P[i],
		       zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,
		       vtmp)){
	    cerr << clock() << " reconstruction failure" << endl;
	    // ok=false; //?
	    break; // no luck reconstructing
	  }
	  if (debug_infolevel)
	    cerr << clock() << " clearing denominators " << endl;
	  cleardeno(vtmp); // clear denominators
	  if (debug_infolevel)
	    cerr << clock() << " end rational reconstruction " << endl;
	  swap(vtmp,W[i]);
	  break; // not stabilized, find another prime
	}
	else {
	  if (debug_infolevel)
	    cerr << clock() << " checking reconstructed basis mod " << p << endl;
	  if (!chk_equal_mod(W[i],resmod,G,p.val)){
	    W[i].clear();
	    cerr << clock() << " checking failure " << endl;
	    break;
	  }
	  if (debug_infolevel)
	    cerr << clock() << " check successful " << endl;
	}
	// now check if W[i] is a Groebner basis over Q, if so it's the answer
#else
	unsigned jpos=0;
	afewpolys.clear();
	for (;jpos<V[i].size();++jpos){
	  if (int(Wlast[i].size())>jpos && chk_equal_mod(Wlast[i][jpos],gb[jpos],p.val))
	    afewpolys.push_back(Wlast[i][jpos]);
	  else {
	      if (!fracmod(V[i][jpos],P[i],
			   zd,zd1,zabsd1,zu,zu1,zur,zq,zr,zsqrtm,ztmp,
			   poly8tmp)){
		cerr << clock() << " reconstruction failure at position " << jpos << endl;
		break;
	      }
	      afewpolys.push_back(poly8tmp);
	  }
	  if (Wlast[i].size()>jpos && !(afewpolys[jpos]==Wlast[i][jpos])){
	    if (debug_infolevel){
	      unsigned j=0,js=giacmin(afewpolys[jpos].coord.size(),Wlast[i][jpos].coord.size());
	      for (;j<js;++j){
		if (!(afewpolys[jpos].coord[j]==Wlast[i][jpos].coord[j]))
		  break;
	      }
	      cerr << "Diagnostic: chinrem reconstruction mismatch at positions " << jpos << "," << j << endl;
	      if (j<js)
		cerr << gb[jpos].coord[j].g << "*" << gb[jpos].coord[j].u << endl;
	      else
		cerr << afewpolys[jpos].coord.size() << "," << Wlast[i][jpos].coord.size() << endl;
	    }
	    break;
	  }
	  if ( jpos > (Wlast[i].size()*3)/2+2 )
	    break;
	}
	if (afewpolys!=Wlast[i]){
	  swap(afewpolys,Wlast[i]);
	  if (debug_infolevel>0)
	    cerr << clock() << " unstable mod " << p << " from " << V[i].size() << " reconstructed " << Wlast[i].size() << endl;
	  break;
	}
	if (debug_infolevel)
	  cerr << clock() << " stable, clearing denominators " << endl;
	W[i]=Wlast[i];
	cleardeno(W[i]); // clear denominators
	if (debug_infolevel)
	  cerr << clock() << " end rational reconstruction " << endl;
	// now check if W[i] is a Groebner basis over Q, if so it's the answer
#endif
	if (debug_infolevel)
	  cerr << clock() << " begin final check" << endl;
	// first verify that the initial generators reduce to 0
	poly8 tmp0,tmp1,tmp2;
	vectpoly8 wtmp;
	unsigned j=0;
	G.resize(W[i].size());
	for (j=0;j<W[i].size();++j)
	  G[j]=j;
	for (j=0;j<initial;++j){
	  reduce(res[j],W[i],G,-1,wtmp,tmp0,tmp1,tmp2,0);
	  if (!tmp0.coord.empty()){
	    break;
	  }
	}
	if (j!=initial){
	  if (debug_infolevel)
	    cerr << clock() << " final check failure, retrying with another prime " << endl;
	  continue;
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
	  cerr << clock() << " end final check " << endl;
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
	return true;
      }
      if (i==V.size()){
	if (debug_infolevel)
	  cerr << clock() << " creating reconstruction #" << i << endl;
	// not found
	V.push_back(gb);
	W.push_back(vectpoly8()); // no reconstruction yet, wait at least another prime
	Wlast.push_back(vectpoly8());
	P.push_back(p);
      }
    }
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

  vectpoly gbasis8(const vectpoly & v,environment * env,bool modularcheck,GIAC_CONTEXT){
    vectpoly8 res; vectpolymod resmod;
    vector<unsigned> G;
    vectpoly_2_vectpoly8(v,res);
    if (!env || env->modulo==0){
      if (mod_gbasis(res,modularcheck,contextptr)){
	vectpoly newres(res.size(),polynome(v.front().dim,v.front()));
	for (unsigned i=0;i<res.size();++i)
	  res[i].get_polynome(newres[i]);
	return newres;
      }
    }
    if (env && env->moduloon && env->modulo.type==_INT_){
#ifdef GBASIS_F4
      in_gbasisf4mod(res,resmod,G,env->modulo.val,true/*totaldeg*/,0,0,false);
#else
      in_gbasismod(res,resmod,G,env->modulo.val,true,0);
#endif
      if (debug_infolevel)
	cerr << "G=" << G << endl;
    }
    else
      in_gbasis(res,G,env,true);
    vectpoly newres(G.size(),polynome(v.front().dim,v.front()));
    for (unsigned i=0;i<G.size();++i)
      res[G[i]].get_polynome(newres[i]);
    return newres;
  }

#endif // CAS38_DISABLED

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
