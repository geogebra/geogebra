/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c -Wall cocoa.cc" -*- */
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
  // special code for polynomial up to 10 or 11 variables (max deg<32768) 
#define GROEBNER_VARS 11

  struct tdeg_t {
    short tab[GROEBNER_VARS+1];
    tdeg_t() { 
      longlong * ptr = (longlong *) tab;
      ptr[2]=ptr[1]=ptr[0]=0;
    }
    tdeg_t(int i){
      longlong * ptr = (longlong *) tab;
      ptr[2]=ptr[1]=ptr[0]=0;
    }
    tdeg_t(const index_m & lm,short order){ 
      longlong * ptr_ = (longlong *) tab;
      ptr_[2]=ptr_[1]=ptr_[0]=0;
      // tab[GROEBNER_VARS]=order;
      short * ptr=tab;
      if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
	*ptr=sum_degree(lm);
	++ptr;
      }
      vector<deg_t>::const_iterator it=lm.begin(),itend=lm.end();
      if (order==_REVLEX_ORDER){
	for (--itend,--it;it!=itend;++ptr,--itend)
	  *ptr=-*itend;
      }
      else {
	for (;it!=itend;++ptr,++it)
	  *ptr=*it;
      }
    }
  };
  inline tdeg_t operator + (const tdeg_t & x,const tdeg_t & y){
    tdeg_t res;
    for (unsigned i=0;i<GROEBNER_VARS;++i)
      res.tab[i]=x.tab[i]+y.tab[i];
    return res;
  }
  inline tdeg_t & operator += (tdeg_t & x,const tdeg_t & y){ 
    for (unsigned i=0;i<GROEBNER_VARS;++i)
      x.tab[i]+=y.tab[i];
    return x;  
  }
  inline tdeg_t operator - (const tdeg_t & x,const tdeg_t & y){ 
    tdeg_t res;
    for (unsigned i=0;i<GROEBNER_VARS;++i)
      res.tab[i]=x.tab[i]-y.tab[i];
    return res;
  }
  inline bool operator == (const tdeg_t & x,const tdeg_t & y){ 
    return  ((longlong *) x.tab)[0] == ((longlong *) y.tab)[0] && 
      ((longlong *) x.tab)[1] == ((longlong *) y.tab)[1] &&
      ((longlong *) x.tab)[2] == ((longlong *) y.tab)[2] ;
  }
  inline bool operator != (const tdeg_t & x,const tdeg_t & y){ 
    return !(x==y);
  }
  bool operator >= (const tdeg_t & x,const tdeg_t & y){
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
    if (x.tab[8]!=y.tab[8])
      return x.tab[8]>y.tab[8];
    if (x.tab[9]!=y.tab[9])
      return x.tab[9]>y.tab[9];
    if (x.tab[10]!=y.tab[10])
      return x.tab[10]>y.tab[10];
    return x.tab[11]>y.tab[11];
  }
  inline bool operator >  (const tdeg_t & x,const tdeg_t & y){ return !(y>=x); }
  inline bool tdeg_t_strictly_greater (const tdeg_t & x,const tdeg_t & y){
    return x>=y;
  }
  inline bool tdeg_t_all_greater(const tdeg_t & x,const tdeg_t & y,short order){
    if (order==_REVLEX_ORDER){
      for (unsigned i=1;i<GROEBNER_VARS;++i){
	if (x.tab[i]>y.tab[i])
	  return false;
      }
      return true;
    }
    for (unsigned i=0;i<GROEBNER_VARS;++i){
      if (x.tab[i]<y.tab[i])
	return false;
    }
    return true;
  }

  void index_lcm(const tdeg_t & x,const tdeg_t & y,tdeg_t & z,short order){
    // short order=x.tab[GROEBNER_VARS];
    int t=0;
    if (order==_REVLEX_ORDER){
      for (unsigned i=1;i<GROEBNER_VARS;++i){
	t -= (z.tab[i]=(x.tab[i]<y.tab[i])?x.tab[i]:y.tab[i]);
      }
    }
    else {
      for (unsigned i=1;i<GROEBNER_VARS;++i){
	t += (z.tab[i]=(x.tab[i]>y.tab[i])?x.tab[i]:y.tab[i]);
      }
    }
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      z.tab[0] = t;
    else 
      z.tab[0]=(x.tab[0]>y.tab[0])?x.tab[0]:y.tab[0];
  }
  // polynomial are vector< T_unsigned<gen,tdeg_t> >

  void get_index(const tdeg_t & x,index_t & idx,int order,int dim){
    idx.resize(dim);
    const short * ptr=x.tab;
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER)
      ++ptr;
    if (order==_REVLEX_ORDER){
      for (int i=1;i<=dim;++ptr,++i)
	idx[dim-i]=-*ptr;
    }
    else {
      for (int i=0;i<dim;++ptr,++i)
	idx[i]=*ptr;
    }
  }
  
  struct poly8 {
    std::vector< T_unsigned<gen,tdeg_t> > coord;
    // lex order is implemented using tdeg_t as a list of degrees
    // tdeg uses total degree 1st then partial degree in lex order, max 7 vars
    // revlex uses total degree 1st then opposite of partial degree in reverse ordre, max 7 vars
    short int order; // _PLEX_ORDER, _REVLEX_ORDER or _TDEG_ORDER
    short int dim;
    void dbgprint() const;
    poly8():order(_PLEX_ORDER),dim(0) {}
    poly8(int o_,int dim_): order(o_),dim(dim_) {}
    poly8(const polynome & p){
      order=-1;
      dim=p.dim;
      if (p.is_strictly_greater==i_lex_is_strictly_greater)
	order=_PLEX_ORDER;
      if (p.is_strictly_greater==i_total_revlex_is_strictly_greater)
	order=_REVLEX_ORDER;
      if (p.is_strictly_greater==i_total_lex_is_strictly_greater)
	order=_TDEG_ORDER;
      if (p.dim>GROEBNER_VARS-(order==_REVLEX_ORDER || order==_TDEG_ORDER)) 
	cerr << "Dimension error";
      else {
	coord.reserve(p.coord.size());
	for (unsigned i=0;i<p.coord.size();++i){
	  coord.push_back(T_unsigned<gen,tdeg_t>(p.coord[i].value,tdeg_t(p.coord[i].index,order)));
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
  // in threaded.h:
  // addition: smalladd(p1.coord,p2.coord,res.coord);
  // addition + reduction mod an integer smalladd(p1.coord,p2.coord,res.coord,reduce);
  // substraction: smallsub instead of smalladd, accepts reduce of type int or reduce type
  // multiply by a constant: smallmult(g,p.coord,res.coord);
  // smallshift(p.coord,shift,res.coord): accepts &p=&coord (recommended if possible)
  // todo: index_lcm, spoly, gbasis_update, gbasis8

  ostream & operator << (ostream & os, const poly8 & p){
    std::vector< T_unsigned<gen,tdeg_t> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int t2;
    for (;it!=itend;++it){
      os << it->g << "*" ;
      switch (p.order){
      case _PLEX_ORDER:
	for (int i=0;i<GROEBNER_VARS;++i){
	  t2 = it->u.tab[i];
	  if (t2)
	    os << "x"<< i << "^" << t2 << "*" ;
	}
	break;
      case _TDEG_ORDER:
	for (int i=1;i<GROEBNER_VARS;++i){
	  t2 = it->u.tab[i];
	  if (t2)
	    os << "x"<< i-1 << "^" << t2 << "*" ;
	}
	break;
      case _REVLEX_ORDER:
	for (int i=1;i<GROEBNER_VARS;++i){
	  t2 = -it->u.tab[i];
	  if (t2==0)
	    continue;
	  os << "x"<< p.dim-i;
	  if (t2!=1)
	    os << "^" << t2;
	}
	break;
      }
      os << " + ";
    }
    return os;
  }

  void poly8::dbgprint() const { 
    std::cerr << *this << endl;
  }

  typedef vector<poly8> vectpoly8;

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

  // Groebner basis code begin here

  gen inplace_ppz(poly8 & p,bool divide=true){
    vector< T_unsigned<gen,tdeg_t> >::iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend)
      return 1;
    gen res=(itend-1)->g;
    for (it=p.coord.begin();it!=itend-1;++it){
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
  
  void reduce(const poly8 & p,const vectpoly8 & res,const vector<unsigned> & G,unsigned excluded,poly8 & rem,poly8 & TMP1, poly8 & TMP2,environment * env){
    if (&p!=&rem)
      rem=p;
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
	++rempos;
	continue;
      }
      else rempos=0;
      gen a(pt->g),b(res[G[i]].coord.front().g);
      TMP1.coord.clear();
      TMP2.coord.clear();
      smallshift(res[G[i]].coord,pt->u-res[G[i]].coord.front().u,TMP1.coord);
      if (env && env->moduloon){
	smallmult(a*invmod(b,env->modulo),TMP1.coord,TMP1.coord); // ?env->modulo
	smallsub(rem.coord,TMP1.coord,TMP2.coord,env->modulo);
      }
      else {
	simplify(a,b);
	if (b==-1){
	  b=-b;
	  a=-a;
	}
	if (!is_one(a))
	  inplace_mult(a,TMP1.coord);
	if (!is_one(b)){
	  inplace_mult(-b,rem.coord);
	  smalladd(rem.coord,TMP1.coord,TMP2.coord);
	}
	else
	  smallsub(rem.coord,TMP1.coord,TMP2.coord);
	// if (count %50==49) inplace_ppz(rem);
      }
      swap(rem.coord,TMP2.coord);
    }
    gen g=inplace_ppz(rem);
    if (debug_infolevel>1)
      cerr << "ppz was " << g << endl;
  }

  // reduce with respect to itself the elements of res with index in G
  void reduce(vectpoly8 & res,vector<unsigned> G,environment * env){
    if (res.empty() || G.empty())
      return;
    poly8 pred(res.front().order,res.front().dim),
      TMP1(res.front().order,res.front().dim),
      TMP2(res.front().order,res.front().dim);
    // reduce res
    for (unsigned i=0;i<G.size();++i){
      poly8 & p=res[i];
      reduce(p,res,G,i,pred,TMP1,TMP2,env);
      swap(res[i].coord,pred.coord);
    }
  }

  bool disjoint(const tdeg_t & a,const tdeg_t & b,short order,short dim){
    const short * it=a.tab, * jt=b.tab;
    if (order==_REVLEX_ORDER || order==_TDEG_ORDER){
      ++it; ++jt;
    }
    const short * itend=it+dim;
    for (;it!=itend;++jt,++it){
      if (*it && *jt)
	return false;
    }
    return true;
  }

  void spoly(const poly8 & p,const poly8 & q,poly8 & res,environment * env){
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
    poly8 tmp1(p),tmp2(q);
    smallshift(tmp1.coord,lcm-pi,tmp1.coord);
    smallmult(q.coord.front().g,tmp1.coord,tmp1.coord);
    smallshift(tmp2.coord,lcm-qi,tmp2.coord);
    smallmult(p.coord.front().g,tmp2.coord,tmp2.coord);
    if (env && env->moduloon)
      smallsub(tmp1.coord,tmp2.coord,res.coord,env->modulo);
    else
      smallsub(tmp1.coord,tmp2.coord,res.coord);
  }

  static void gbasis_update(vector<unsigned> & G,vector< pair<unsigned,unsigned> > & B,vectpoly8 & res,unsigned pos,poly8 & TMP1,poly8 & TMP2,environment * env){
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
    vector< pair<unsigned,unsigned> > B1;
    B1.reserve(B.size()+C.size());
    for (unsigned i=0;i<B.size();++i){
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
    C.clear();
    C.reserve(G.size());
    vector<unsigned> hG(1,pos);
    for (unsigned i=0;i<G.size();++i){
      if (!res[G[i]].coord.empty() && !tdeg_t_all_greater(res[G[i]].coord.front().u,h0,order)){
	// reduce res[G[i]] with respect to h
	reduce(res[G[i]],res,hG,-1,res[G[i]],TMP1,TMP2,env);
	C.push_back(G[i]);
      }
      // NB: removing all pairs containing i in it does not work
    }
    C.push_back(pos);
    swap(C,G);
  }

  vectpoly gbasis8(const vectpoly & v,environment * env){
    vectpoly8 res;
    vectpoly_2_vectpoly8(v,res);
    poly8 TMP1(res.front().order,res.front().dim),TMP2(res.front().order,res.front().dim);
    vector<unsigned> G;
    vector< pair<unsigned,unsigned> > B;
    short order=res.front().order;
    for (unsigned l=0;l<res.size();++l){
      gbasis_update(G,B,res,l,TMP1,TMP2,env);
    }
    for (;!B.empty();){
      if (debug_infolevel>1)
	cerr << clock() << " number of pairs: " << B.size() << ", base size: " << G.size() << endl;
      // find smallest lcm pair in B
      tdeg_t small,cur;
      unsigned smallpos;
      for (smallpos=0;smallpos<B.size();++smallpos){
	if (!res[B[smallpos].first].coord.empty() && !res[B[smallpos].second].coord.empty())
	  break;
      }
      index_lcm(res[B[smallpos].first].coord.front().u,res[B[smallpos].second].coord.front().u,small,order);
      for (unsigned i=smallpos+1;i<B.size();++i){
	if (res[B[i].first].coord.empty() || res[B[i].second].coord.empty())
	  continue;
	index_lcm(res[B[i].first].coord.front().u,res[B[i].second].coord.front().u,cur,order);
	if (tdeg_t_strictly_greater(small,cur)){
	  swap(small,cur); // small=cur;
	  smallpos=i;
	}
      }
      pair<unsigned,unsigned> bk=B[smallpos];
      if (debug_infolevel>1 && (equalposcomp(G,bk.first)==0 || equalposcomp(G,bk.second)==0))
	cerr << clock() << " reducing pair with 1 element not in basis " << bk << endl;
      B.erase(B.begin()+smallpos);
      poly8 h(res.front().order,res.front().dim);
      spoly(res[bk.first],res[bk.second],h,env);
      if (debug_infolevel>1)
	cerr << clock() << " reduce begin, pair " << bk << " remainder size " << h.coord.size() << endl;
      reduce(h,res,G,-1,h,TMP1,TMP2,env);
      if (debug_infolevel>1){
	if (debug_infolevel>2){ cerr << h << endl; }
	cerr << clock() << " reduce end, remainder size " << h.coord.size() << endl;
      }
      if (!h.coord.empty()){
	res.push_back(h);
	gbasis_update(G,B,res,res.size()-1,TMP1,TMP2,env);
	if (debug_infolevel>2)
	  cerr << clock() << " basis indexes " << G << " pairs indexes " << B << endl;
      }
    }
    vectpoly newres(G.size(),polynome(v.front().dim,v.front()));
    for (unsigned i=0;i<G.size();++i)
      res[G[i]].get_polynome(newres[i]);
    return newres;
  }

#endif // CAS38_DISABLED

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
