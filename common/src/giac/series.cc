// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c series.cc  -DIN_GIAC -DHAVE_CONFIG_H " -*-
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
#include "subst.h"
#include "series.h"
#include "symbolic.h"
#include "unary.h"
#include "usual.h"
#include "poly.h"
#include "sym2poly.h" 
#include "tex.h"
#include "prog.h"
#include "misc.h"
#include "intg.h"
#include "maple.h"
#include "lin.h"
#include "plot.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  static int mrv_begin_order=2;

  bool taylor(const gen & f_x,const gen & x,const gen & lim_point,int ordre,vecteur & v,GIAC_CONTEXT){
    gen current_derf(f_x),value,factorielle(1);
    for (int i=0;;++i){
      value=subst(current_derf,x,lim_point,false,contextptr);
      if (is_undef(value))
	return false;
      v.push_back(ratnormal(rdiv(value,factorielle,contextptr)));
      if (i==ordre){
	v.push_back(undef);
	return true;
      }
      factorielle = factorielle * gen(i+1);
      current_derf=ratnormal(derive(current_derf,x,contextptr));
      if (is_undef(current_derf))
	return false;
    }
    v.dbgprint();
    return false;
  }

  // direction is always ignored for taylor, but might not 
  // for generic series_expansion
  // shift coeff =0 for taylor
  gen taylor(const gen & lim_point,int ordre,const unary_function_ptr & f,int direction,gen & shift_coeff,GIAC_CONTEXT){
    // Special handling for sin/cos expansion inside limit
    if ( is_inf(lim_point) && ( (f==at_cos)  || (f==at_sin) ) ){
      gen g=bounded_function(contextptr);
      /*
      int i=sincosinf.size();
      sincosinf.push_back(gen(" sincosinf"+print_INT_(i)));
      gen g=sincosinf.back();
      if (!g._IDNTptr->value){
	vecteur minusone_one(2);
	minusone_one[0]=minus_one;
	minusone_one[1]=plus_one;
	gen v(vecteur(1,gen(minusone_one,_LINE__VECT)));
	gen d(_DOUBLE_);
	d.subtype=_INT_TYPE;
	gen aa(makevecteur(d,v,vecteur(0)),_ASSUME__VECT);
	g._IDNTptr->value=new gen(aa);
      }
    */
      return vecteur(1,g);
    }
    // if preprocessing is needed for f, series_expansion for ordre==-1 should 
    // push back in a global vector f and it's substitution
    if (ordre<0) 
      return 0;
    shift_coeff=0;
    if (is_undef(lim_point) || is_inf(lim_point)){
      invalidserieserr(gettext("non tractable function ")+(f.ptr()->print(contextptr)+(" at "+lim_point.print(contextptr))));
      return undef;
    }
    identificateur x(" ");
    vecteur v;
    if (taylor(f(x,contextptr),x,lim_point,ordre,v,contextptr)) 
      return v;
    else
      return undef;
  }

  gen porder(const sparse_poly1 & a){
    if (a.empty())
      return plus_inf;
    sparse_poly1::const_iterator a_end=a.end()-1;
    if (is_undef(a_end->coeff))
      return a_end->exponent;
    else
      return plus_inf;
  }

  static void vecteur2sparse_poly1(const vecteur & v,sparse_poly1 & p){
    p.clear();
    vecteur::const_iterator it=v.begin(),itend=v.end();
    p.reserve(itend-it);
    for (int i=0;it!=itend;++i,++it){
      if (!is_zero(*it))
	p.push_back(monome(*it,i));
    }
  }

  sparse_poly1 vecteur2sparse_poly1(const vecteur & v){
    sparse_poly1 p;
    vecteur2sparse_poly1(v,p);
    return p;
  }

  static gen sparse_poly12gen(const sparse_poly1 & p,const gen & x,gen & remains,bool with_order_size);

  static gen spol12gen(const gen & coeff,const gen & x){
    if (coeff.type==_VECT){
      vecteur v=*coeff._VECTptr;
      int s=v.size();
      for (int i=0;i<s;++i){
	v[i]=spol12gen(v[i],x);
      }
      return gen(v,coeff.subtype);
    }
    if (coeff.type==_SPOL1){
      gen remains=0;
      return sparse_poly12gen(*coeff._SPOL1ptr,x,remains,true)+remains;
    }
    if (coeff.type!=_SYMB)
      return coeff;
    return symbolic(coeff._SYMBptr->sommet,spol12gen(coeff._SYMBptr->feuille,x));
  }

  static gen sparse_poly12gen(const sparse_poly1 & p,const gen & x,gen & remains,bool with_order_size){
    gen res;
    remains=0;
    sparse_poly1::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      gen coeff=it->coeff;
      if (is_undef(coeff)){
	remains=pow(x,it->exponent,context0); // ok
	if (with_order_size)
	  return res+remains*order_size(x,context0);
	else
	  return res;
      }
      coeff=spol12gen(coeff,x);
      res = res + coeff * pow(x,it->exponent,context0); // ok
    }
    return res;
  }


  bool ptruncate(sparse_poly1 & p,const gen & ordre,GIAC_CONTEXT){
    if ( (series_flags(contextptr) & 0x2) || p.empty() ){
      sparse_poly1::iterator it=p.begin(),itend=p.end();
      gen first=it->exponent;
      for (;it!=itend;++it){
	if (is_undef(it->coeff))
	  return true;
	if (ck_is_strictly_greater(it->exponent-first,ordre,contextptr)){
	  it->coeff=undef;
	  p.erase(it+1,itend);
	  return true;
	}
      }
    }
    return true;
  }

  void poly_truncate(sparse_poly1 & p,int ordre,GIAC_CONTEXT){
    if ( (series_flags(contextptr) & 0x2) || p.empty() ){
      sparse_poly1::iterator it=p.begin(),itend=p.end();
      for (;it!=itend;++it){
	if (is_undef(it->coeff))
	  return ;
	if (ck_is_strictly_greater(it->exponent,ordre,contextptr)){
	  it->coeff=undef;
	  p.erase(it+1,itend);
	  return ;
	}
      }
    }
    return ;
  }

  static gen remove_lnexp(const gen & e,GIAC_CONTEXT);

  bool padd(const sparse_poly1 & a,const sparse_poly1 &b, sparse_poly1 & res,GIAC_CONTEXT){
    // Series addition
    if (a.empty()){
      if (&b!=&res)
	res=b;
      return true;
    }
    if (b.empty()){
      if (&a!=&res)
	res=a;
      return true;
    }
    sparse_poly1::const_iterator a_cur,a_end,b_cur,b_end;
    sparse_poly1 temp_a,temp_b;
    if (&res==&a){ // must make a copy of a
      temp_a=a;
      a_cur=temp_a.begin();
      a_end=temp_a.end();
    }
    else {
      a_cur=a.begin();
      a_end=a.end();
    }
    if (&res==&b){ // must make a copy of b
      temp_b=b;
      b_cur=temp_b.begin();
      b_end=temp_b.end();
    }
    else {
      b_cur=b.begin();
      b_end=b.end();
    }
    res.clear();
    res.reserve((a_end-a_cur)+(b_end-b_cur));
    for (;(a_cur!=a_end) && (b_cur!=b_end) ;) {
      gen a_pow=a_cur->exponent;
      gen b_pow=b_cur->exponent;
      // a and b are non-empty, compare powers
      if (ck_is_strictly_greater(b_pow,a_pow,contextptr)) {
	// get coefficient from a
	res.push_back(*a_cur);
	if (is_undef(a_cur->coeff)){
	  return true;
	}
	a_cur++;
	continue;
      }
      if (ck_is_strictly_greater(a_pow,b_pow,contextptr)) {
	// get coefficient from b
	res.push_back(*b_cur);
	if (is_undef(b_cur->coeff)){
	  return true;
	}
	b_cur++;
	continue;
      }
      // Add coefficient of a and b
      gen sum=a_cur->coeff+b_cur->coeff;
      if (res.empty() || (series_flags(contextptr) & 0x1) ){
	//cerr << sum << " ";
	sum=recursive_normal(remove_lnexp(sum,contextptr),contextptr);
	//cerr << sum << endl;
      }
      // gen sum=(a_cur->coeff+b_cur->coeff);
      if (!is_zero(sum))
	res.push_back(monome(sum,a_pow));
      if (is_undef(sum)){
	return true;
      }
      a_cur++;
      b_cur++;
    }
    for (;a_cur!=a_end;++a_cur)
      res.push_back(*a_cur);
    for (;b_cur!=b_end;++b_cur)
      res.push_back(*b_cur);
    return true;
  }

  sparse_poly1 spadd(const sparse_poly1 & a,const sparse_poly1 &b,GIAC_CONTEXT){
    sparse_poly1 res;
    padd(a,b,res,contextptr);
    return res;
  }

  sparse_poly1 spsub(const sparse_poly1 & a,const sparse_poly1 &b,GIAC_CONTEXT){
    sparse_poly1 res(b);
    pneg(b,res,contextptr);
    padd(a,res,res,contextptr);
    return res;
  }


  bool pmul(const sparse_poly1 & a,const gen & b_orig, sparse_poly1 & res,GIAC_CONTEXT){
    gen b(b_orig);
    if (&a==&res){
      sparse_poly1::iterator it=res.begin(),itend=res.end();
      for (;it!=itend;++it)
	it->coeff = ratnormal(it->coeff * b) ;
      return true;
    }
    sparse_poly1::const_iterator it=a.begin(),itend=a.end();
    res.clear();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(monome(ratnormal(it->coeff * b), it->exponent));
    return true;
  }

  bool pmul(const gen & b, const sparse_poly1 & a,sparse_poly1 & res,GIAC_CONTEXT){
    return pmul(a,b,res,contextptr);
  }

  sparse_poly1 spmul(const sparse_poly1 & a,const gen &b,GIAC_CONTEXT){
    sparse_poly1 res;
    if (!pmul(a,b,res,contextptr))
      res=sparse_poly1(1,monome(1,undef));
    return res;
  }

  sparse_poly1 spmul(const gen & a,const sparse_poly1 &b,GIAC_CONTEXT){
    sparse_poly1 res;
    if (!pmul(a,b,res,contextptr))
      res=sparse_poly1(1,monome(1,undef));
    return res;
  }

  static bool monome_less(const monome & a,const monome & b){
    return ck_is_strictly_greater(b.exponent,a.exponent,context0);
  }

  bool pmul(const sparse_poly1 & celuici,const sparse_poly1 &other, sparse_poly1 & final_seq,bool n_truncate,const gen & n_valuation,GIAC_CONTEXT){
    int asize=celuici.size();
    int bsize=other.size();
    if ( (!asize) || (!bsize) ) {
      final_seq.clear();
      return true;
    }
    if (asize==1){
      gen temp(celuici.front().coeff);
      pshift(other,celuici.front().exponent,final_seq,contextptr);
      // cout << other << "Shifted" << final_seq << endl;
      return pmul(final_seq,temp,final_seq,contextptr);
      // cout << other << "Multiplied" << final_seq << endl;
    }
    if (bsize==1){
      gen temp(other.front().coeff);
      pshift(celuici,other.front().exponent,final_seq,contextptr);
      return pmul(final_seq,temp,final_seq,contextptr);
    }
    sparse_poly1 new_seq;
    new_seq.reserve(asize*bsize);
    // General sparse series multiplication: complexity is N*M*ln(N*M)
    // Storage capacity 2*N*M expair
    // That's much more than O(N+M) for dense poly *but*
    // it works for non integer powers
    // cout << celuici << "pmul" << other << endl;
    // First find the order product
    gen a_max = porder(celuici);
    gen b_max = porder(other);
    gen a_min = celuici.front().exponent;
    gen b_min = other.front().exponent;
    gen c_min = normal(a_min + b_min,contextptr);
    gen c_max = min(normal(a_min + b_max,contextptr),normal(b_min + a_max,contextptr),contextptr);
    if (c_max.type==_SYMB && c_max._SYMBptr->sommet==at_max)
      return false; // setsizeerr(gettext("series.cc/pmul"));
    // compute all products term by term, with optimization for dense poly
    // (coeff are sorted for dense poly)
    sparse_poly1::const_iterator itb = other.begin(),itbend = other.end();
    sparse_poly1::const_iterator ita = celuici.begin(),ita_end=celuici.end();
    sparse_poly1::const_iterator itabegin = ita;
    gen old_pow=normal(ita->exponent+itb->exponent,contextptr);
    gen res(0);
    for ( ; ita!=ita_end; ++ita ){
      sparse_poly1::const_iterator itacur=ita;
      sparse_poly1::const_iterator itbcur=itb;
      for (;;) {
	gen cur_pow=normal(itacur->exponent+itbcur->exponent,contextptr);
	if ((n_truncate && ck_is_strictly_greater(n_valuation,cur_pow,contextptr)) || ck_is_greater(c_max,cur_pow,contextptr)){
	  if (cur_pow!=old_pow){
	    new_seq.push_back( monome(res,old_pow ));
	    res=itacur->coeff * itbcur->coeff;
	    old_pow=cur_pow;
	  }
	  else
	    res=res+ itacur->coeff  * itbcur->coeff;
	}
	if (itacur==itabegin)
	  break;
	--itacur;
	++itbcur;
	if (itbcur==itbend)
	  break;
      }
    }
    --ita;
    ++itb;
    for ( ; itb!=itbend;++itb){
      sparse_poly1::const_iterator itacur=ita;
      sparse_poly1::const_iterator itbcur=itb;
      for (;;) {
	gen cur_pow=normal(itacur->exponent + itbcur->exponent,contextptr);
	if ((n_truncate && ck_is_strictly_greater(n_valuation,cur_pow,contextptr)) || ck_is_greater(c_max,cur_pow,contextptr)){
	  if (cur_pow!=old_pow){
	    new_seq.push_back( monome(res ,old_pow ));
	    res= itacur->coeff  * itbcur->coeff ;
	    old_pow=cur_pow;
	  }
	  else
	    res=res+ itacur->coeff  * itbcur->coeff ;
	}
	if (itacur==itabegin)
	  break;
	--itacur;
	++itbcur;
	if (itbcur==itbend)
	  break;
      }
    }
    new_seq.push_back( monome(res ,old_pow ));
    // cout << new_seq << endl;
    // sort by asc. power
    sort( new_seq.begin(),new_seq.end(),monome_less);
    // cout << "Sorted" << new_seq << endl;
    // add terms with same power
    sparse_poly1::const_iterator it=new_seq.begin();
    sparse_poly1::const_iterator itend=new_seq.end();
    final_seq.clear();
    final_seq.reserve(itend-it);
    while (it!=itend){
      gen res=it->coeff;
      gen pow=it->exponent;
      if (is_undef(res)){
	final_seq.push_back(*it);
	return true;
      }
      ++it;
      while ( (it!=itend) && (it->exponent==pow)){
	if (is_undef(res)){
	  final_seq.push_back(*it);
	  return true;
	}
	res=res+it->coeff;
	++it;
      }
      if (series_flags(contextptr) & 0x1)
	res=recursive_normal(res,contextptr);
      if (!is_zero(res))
	final_seq.push_back(monome(res, pow));
    }
    if (c_max!=plus_inf)
      final_seq.push_back(monome(undef, c_max));
    return true;
    cout << final_seq.back().coeff << endl;
    return true;
  }

  sparse_poly1 spmul(const sparse_poly1 & a,const sparse_poly1 &b,GIAC_CONTEXT){
    sparse_poly1 res;
    if (!pmul(a,b,res,false,0,contextptr))
      res=sparse_poly1(1,monome(1,undef));
    return res;
  }

  bool pneg(const sparse_poly1 & a,sparse_poly1 & res,GIAC_CONTEXT){
    if (&a==&res){
      sparse_poly1::iterator it=res.begin(),itend=res.end();
      for (;it!=itend;++it)
	it->coeff=-it->coeff;
      return true;
    }
    sparse_poly1::const_iterator it=a.begin(),itend=a.end();
    res.clear();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(monome(-it->coeff, it->exponent));
    return true;
  }

  sparse_poly1 spneg(const sparse_poly1 & a,GIAC_CONTEXT){
    sparse_poly1 res;
    pneg(a,res,contextptr);
    return res;
  }
  
  bool pshift(const sparse_poly1 & a,const gen & b_orig, sparse_poly1 & res,GIAC_CONTEXT){
    if (is_zero(b_orig)){
      if (&a!=&res)
	res=a;
      return true;
    }
    gen b(b_orig);
    if (&a==&res){
      sparse_poly1::iterator it=res.begin(),itend=res.end();
      for (;it!=itend;++it)
	it->exponent = normal(it->exponent + b,contextptr);
      return true;
    }
    sparse_poly1::const_iterator it=a.begin(),itend=a.end();
    res.clear();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(monome(it->coeff , normal(it->exponent +b,contextptr)));
    return true;
  }

  // ascending order division
  bool pdiv(const sparse_poly1 & a,const sparse_poly1 &b_orig, sparse_poly1 & res,int ordre_orig,GIAC_CONTEXT){
    sparse_poly1 b(b_orig);
    ptruncate(b,ordre_orig,contextptr);
    if (b.empty()){
      // divisionby0err(a);
      return false;
    }
    gen b0=b.front().coeff;
    if (is_undef(b0)){
      if (&b!=&res)
	res=b;
      return true;
    }
    if (b.size()==1){
      pshift(a,-b.front().exponent,res,contextptr);
      return pdiv(res,b0,res,contextptr);
    }
    // cout << a << "/" << b << endl;
    if (&res==&b){
      // setsizeerr(gettext("series.cc/pdiv"));
      return false;
    }
    gen e0=b.front().exponent;
    gen ordre=min(min(porder(a),porder(b)-e0,contextptr),ordre_orig,contextptr);
    if (ordre==plus_inf)
      ordre=series_default_order;
    // cout << ordre << endl;
    if (ordre.type==_SYMB && ordre._SYMBptr->sommet==at_max)
      return false; // setsizeerr(gettext("series.cc/pdiv"));
    sparse_poly1 rem(a);
    res.clear();
    sparse_poly1 bshift;
    gen q_cur,e_cur; // current quotient, current exponent
    for (;;){
      if (is_undef(rem.front().coeff)){
	res.push_back(monome(undef,rem.front().exponent-e0));
	// cout << "=" << res << endl;
	return true;
      }
      q_cur=rdiv(rem.front().coeff,b0,contextptr);
      e_cur=rem.front().exponent-e0;
      res.push_back(monome(q_cur,e_cur));
      pshift(b,e_cur,bshift,contextptr);
      if (!pmul(-q_cur,bshift,bshift,contextptr))
	return false;
      padd(rem,bshift,rem,contextptr);
      // cout << rem.front().exponent << " " << e0+ordre << endl;
      if (ck_is_strictly_greater(rem.front().exponent,e0+ordre,contextptr)){
	res.push_back(monome(undef,ordre+1));
	return true;
      }
    }
    return true;
  }

  sparse_poly1 spdiv(const sparse_poly1 & a,const sparse_poly1 &b,GIAC_CONTEXT){
    sparse_poly1 res;
    if (!pdiv(a,b,res,series_default_order,contextptr))
      res=sparse_poly1(1,monome(1,undef));
    return res;
  }

  bool pdiv(const sparse_poly1 & a,const gen & b_orig, sparse_poly1 & res,GIAC_CONTEXT){
    if (is_zero(b_orig))
      return false; // divisionby0err(a);
    if (is_one(b_orig)){
      if (&a!=&res)
	res=a;
      return true;
    }
    gen b(b_orig);
    if (&a==&res){
      sparse_poly1::iterator it=res.begin(),itend=res.end();
      for (;it!=itend;++it){
	it->coeff=rdiv(it->coeff, b,contextptr);
	if (series_flags(contextptr) & 0x1)
	  it->coeff=normal(it->coeff,contextptr);
      }
      // it->coeff=rdiv(it->coeff, b,contextptr);
      return true;
    }
    sparse_poly1::const_iterator it=a.begin(),itend=a.end();
    res.clear();
    res.reserve(itend-it);
    gen tmp;
    for (;it!=itend;++it){
      tmp=rdiv(it->coeff,b,contextptr);
      if (series_flags(contextptr) & 0x1)
	tmp=normal(tmp,contextptr);
      res.push_back(monome(tmp , it->exponent));
    }
    // res.push_back(monome(rdiv(it->coeff,b,contextptr) , it->exponent));
    return true;
  }

  sparse_poly1 spdiv(const sparse_poly1 & a,const gen &b,GIAC_CONTEXT){
    sparse_poly1 res;
    if (!pdiv(a,b,res,contextptr))
      res=sparse_poly1(1,undef);
    return res;
  }
  
  // v is replaced by e*v where e*v has no denominator 
  void lcmdeno(vecteur &v,gen & e,GIAC_CONTEXT){
    if (v.empty()){
      e=1;
      return;
    }
    if (is_undef(v.front())){
      v.erase(v.begin());
      lcmdeno(v,e,contextptr);
      v.insert(v.begin(),undef);
      return;
    }
    vecteur l;
    lvar(v,l);
    int l_size(l.size());
    vecteur w;
    w.reserve(2*l_size);
    gen common=1,f,num,den;
    // compute lcm of denominators in common
    vecteur::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      f=e2r(*it,l,contextptr);
      fxnd(f,num,den);
      w.push_back(num);
      w.push_back(den);
      // replace common by lcm of common and den
#ifndef USE_GMP_REPLACEMENTS
      if (common.type==_ZINT && common.ref_count()==1 && is_integer(den)){
	if (den.type==_ZINT)
	  mpz_lcm(*common._ZINTptr,*common._ZINTptr,*den._ZINTptr);
	else
	  mpz_lcm_ui(*common._ZINTptr,*common._ZINTptr,absint(den.val));
      }
      else
	common = lcm(common,den);
#else
      common = lcm(common,den);
#endif
    }
    // compute e and recompute v
    e=r2sym(common,l,contextptr);
    it=v.begin();
    for (int i=0;it!=itend;++it,i=i+2){
      *it=r2sym(w[i]*rdiv(common,w[i+1],contextptr),l,contextptr);
    }
  }

  void lcmdeno(sparse_poly1 &v,gen & e,GIAC_CONTEXT){
    if (v.empty()){
      e=1;
      return;
    }
    if (is_undef(v.back().coeff)){
      monome last=v.back();
      v.pop_back();
      lcmdeno(v,e,contextptr);
      v.push_back(last);
      return;
    }
    vecteur l;
    lvar(v,l);
    int l_size(l.size());
    vector<gen> w;
    w.reserve(2*l_size);
    gen common=1,num,den,f;
    // compute lcm of denominators in common
    sparse_poly1::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      f=e2r(it->coeff,l,contextptr);
      fxnd(f,num,den);
      w.push_back(num);
      w.push_back(den);
      common=lcm(common,den);
    }
    // compute e and recompute v
    e=r2sym(common,l,contextptr);
    it=v.begin();
    for (int i=0;it!=itend;++it,i=i+2){
      it->coeff=r2sym(w[i]*rdiv(common,w[i+1],contextptr),l,contextptr);
    }
  }

  bool pcompose(const vecteur & v,const sparse_poly1 & p, sparse_poly1 & res,GIAC_CONTEXT){
    if (v.empty()){
      res.clear();
      return true;
    }
    if ( p.empty() ){
      res.clear();
      if (!is_zero(v.front())) 
	res.push_back(monome(v.front(),0));
      return true;
    }
    // Conversion of p and v to "internal" polynomial form
    vecteur l; // will contain the list of variables common to v and p
    alg_lvar(v,l);
    alg_lvar(p,l);
    // int l_size(l.size());
    gen plcm=plus_one,vlcm=plus_one,f,num,den;
    // compute lcm of denominators of p in plcm
    sparse_poly1::const_iterator its=p.begin(),itsend=p.end();
    vecteur ptemp;
    ptemp.reserve(2*(itsend-its));
    for (;its!=itsend;++its){
      f=e2r(its->coeff,l,contextptr);
      fxnd(f,num,den);
      ptemp.push_back(num);
      ptemp.push_back(den);
      plcm=lcm(den,plcm);
    }
    // compute pcopy such that pcopy/plcm=p
    its=p.begin();
    sparse_poly1 pcopy;
    pcopy.reserve(itsend-its);
    for (int i=0;its!=itsend;++its,i=i+2){
      num=ptemp[i]*rdiv(plcm,ptemp[i+1],contextptr);
      pcopy.push_back(monome(num,its->exponent));
    }
    // do the same thing on v
    vecteur w;
    // compute lcm of denominators in common
    vecteur::const_iterator it=v.begin(),itend=v.end();
    w.reserve(2*(itend-it));
    for (;it!=itend;++it){
      f=e2r(*it,l,contextptr);
      fxnd(f,num,den);
      w.push_back(num);
      w.push_back(den);
      vlcm=lcm(vlcm,den);
    }
    // compute vcopy
    it=v.begin();
    vecteur vcopy;
    vcopy.reserve(itend-it);
    for (int i=0;it!=itend;++it,i=i+2)
      vcopy.push_back(w[i]*rdiv(vlcm,w[i+1],contextptr));
    reverse(vcopy.begin(),vcopy.end());
    if (vcopy.empty()  ){
      res=sparse_poly1(1,monome(undef,minus_inf));
      return true;
    }
    // cout << "compose " << vcopy << " with " << pcopy << endl;
    it=vcopy.begin(),itend=vcopy.end();
    int n=itend-it-1;
    bool n_truncate=false; 
    gen n_valuation;
    if (is_undef(*it)){
      ++it;
      n_truncate=true;
      n_valuation=gen(n)*p.front().exponent;
      // add undef order term
      gen cur_ordre=porder(p);
      // compare cur_ordre with n*valuation(pcopy)
      if ( (cur_ordre==plus_inf) || (ck_is_strictly_greater(cur_ordre,n_valuation,contextptr)) ){
	// remove greater order terms from pcopy
	for (;!pcopy.empty();){
	  if (ck_is_strictly_greater(pcopy.back().exponent,n_valuation,contextptr))
	    pcopy.pop_back();
	  else
	    break;
	}
	// insert undef
	if (pcopy.empty() || (!is_undef(pcopy.back().coeff)) )
	  pcopy.push_back(monome(undef,n_valuation));
      }
    }
    // Skip 0 coeffs in the reverse list vcopy
    for (;it!=itend;++it){
      if (!is_zero(*it))
	break;
    }
    if (it==itend){
      res=sparse_poly1(1,monome(undef));
      return true;
    }
    res=sparse_poly1(1,monome(*it));
    // cout << res << endl;
    ++it;
    if (it==itend && is_undef(pcopy.back().coeff))
      res.push_back(monome(undef,min(n_valuation,pcopy.back().exponent,contextptr)));
    gen plcmn=plus_one;
    for (;it!=itend;++it){
      plcmn=plcmn*plcm;
      // cout << res << "*" << pcopy << endl ;
      if (!pmul(res,pcopy,res,n_truncate,n_valuation,contextptr))
	return false;
      if (n_truncate){ // Remove all terms of order > n_valuation
	sparse_poly1::iterator sit=res.begin(),sitend=res.end();
	for (;sit!=sitend;++sit){
	  if (ck_is_greater(sit->exponent,n_valuation,contextptr)){
	    res.erase(sit,sitend);
	    res.push_back(monome(undef,n_valuation));
	    break;
	  }
	}
      }
      // cout << res << endl;
      if (!is_zero(*it))
	padd(res,sparse_poly1(1,monome(*it*plcmn)),res,contextptr);
      // cout << res << endl;
    }
    den=vlcm*plcmn;
    // back conversion from res to symbolic form
    sparse_poly1::iterator sit=res.begin(),sitend=res.end();
    for (;sit!=sitend;++sit){
      num=den;
      sit->coeff=r2sym(fraction(sit->coeff,num).normal(),l,contextptr);
    }
    return true;
  }

  bool ppow(const sparse_poly1 & base,int m,int ordre,sparse_poly1 & res,GIAC_CONTEXT){
    if (m==0){
      res.clear();
      return true;
    }
    if (m==1){
      if (&base!=&res)
	res=base;
      return true;
    }
    sparse_poly1 temp;
    if (!pmul(base,base,temp,true,ordre,contextptr))
      return false;
    ptruncate(temp,ordre,contextptr);
    if (m%2){
      if (!ppow(temp,m/2,ordre,temp,contextptr) ||
	  !pmul(temp,base,res,true,ordre,contextptr))
	return false;
    }
    else {
      if (!ppow(temp,m/2,ordre,res,contextptr))
	return false;
    }
    return true;
  }
  
  // constant power, otherwise use exp(ln)
  bool ppow(const sparse_poly1 & base,const gen & e,int ordre,int direction,sparse_poly1 & res,GIAC_CONTEXT){
    if (base.size()==1){
      if (&base==&res){
	res.front().coeff=pow(res.front().coeff,e,contextptr);
	res.front().exponent=res.front().exponent*e;	
      }
      else 
	res=sparse_poly1(1,monome(pow(base.front().coeff,e,contextptr),base.front().exponent*e));
      return true;
    }
    gen n=porder(base);
    if ((n==plus_inf) && (e.type==_INT_) && (e.val>=0) ){ // exact power
      int m=e.val;
      return ppow(base,m,ordre,res,contextptr);
    }
    if (base.empty()){
      if (ck_is_positive(e,contextptr))
	res.clear();
      else
	return false; // divisionby0err(base);
      return true;
    }
    // series expansion to a constant power
    monome first(base.front());
    sparse_poly1 basecopy(base);
    basecopy.erase(basecopy.begin());
    pshift(basecopy,-first.exponent,basecopy,contextptr);
    if (!pdiv(basecopy,first.coeff,basecopy,contextptr))
      return false;
    if (n==plus_inf && !basecopy.empty()){ // add an O() error term
      monome last(undef,ordre+1);
      basecopy.push_back(last);
    }
    // If first.exponent!=0 and direction==0 we can not find 
    // first.exponent^e consistently around 0
    if (!direction && !is_integer(e) && !is_zero(first.exponent) ){
      *logptr(contextptr) << gettext("Warning: vanishing non integral power expansion") << endl;
      /*
      res.clear();
      first.coeff=pow(first.coeff,e,contextptr);
      first.exponent = first.exponent*e;
      res.push_back(first);
      first.coeff=undef;
      first.exponent += basecopy[0].exponent;
      res.push_back(first);      
      return;
      */
    }
    // answer=first.coeff^e*x^(first.exponent*e)*(1+base)^e
    // first (1+base)^e -> compose( [1,e,e(e-1)/2,...], base)
    vecteur v(1,plus_one);
    gen produit(e),factorielle(1);
    for (int i=1;i<=ordre;++i){
      v.push_back(rdiv(produit,factorielle,contextptr));
      produit=produit*(e-gen(i));
      factorielle=factorielle*gen(i+1);
    }
    if (e.type!=_INT_ || e.val>ordre)
      v.push_back(undef);
    // cout << v << endl;
    if (!pcompose(v,basecopy,res,contextptr))
      return false;
    // cout << res << endl;
    // final multiplication ans shift
    pshift(res,first.exponent*e,res,contextptr);
    return pmul(res,normalize_sqrt(pow(first.coeff,e,contextptr),contextptr),res,contextptr);
  }

  bool pintegrate(sparse_poly1 & p,const gen & t,GIAC_CONTEXT){
    sparse_poly1::iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      it->coeff=integrate_gen(it->coeff,t,contextptr);
    }
    return true;
  }

  // find q such that pcompose(p,q)=x
  // does not take care of cst coeff of p
  bool prevert(const sparse_poly1 & p_orig,sparse_poly1 & q,GIAC_CONTEXT){
    sparse_poly1 p(p_orig);
    if (p.empty())
      return false; // setsizeerr(gettext("prevert"));
    if (p.front().exponent==0)
      p.erase(p.begin());
    gen ak,k,invk,b1;
    if (p.empty() || is_undef( (ak=p.front().coeff) ) || ck_is_positive(- (k=p.front().exponent) ,contextptr) || k.type!=_INT_ )
      return false; // setsizeerr(gettext("prevert"));
    invk=gen(1)/k;
    b1=pow(ak,invk,contextptr);
    vecteur pv(1);
    sparse_poly1::const_iterator it=p.begin(),itend=p.end();
    int N=0;
    for (;it!=itend;++it){
      gen Ng=it->exponent;
      if (Ng.type!=_INT_)
	return false; // setsizeerr();
      N=Ng.val;
      if (is_undef(it->coeff))
	break;
      for (int n=pv.size();n<N;++n){
	pv.push_back(0);
      }
      pv.push_back(it->coeff);
    }
    if (it==itend)
      N++;
    N=k.val*N;
    q.clear();
    q.push_back(monome(gen(1)/b1,invk));
    for (int n=2;n<N;++n){
      sparse_poly1 qtemp(q),res;
      qtemp.push_back(monome(undef,(n+1)*invk));
      if (!pcompose(pv,qtemp,res,contextptr))
	return false;
      // find coeff of order (n+k-1)/k
      sparse_poly1::const_iterator jt=res.begin(),jtend=res.end();
      for (;jt!=jtend;++jt){
	if (jt->exponent==(n+k-1)/k)
	  break;
      }
      if (jt!=jtend)
	q.push_back(monome(-jt->coeff*invk/b1,gen(n)/k));
    }
    q.push_back(monome(undef,gen(N)/k));
    return true;
  }

  static bool in_series__SPOL1(const gen & e,const identificateur & x,const vecteur & lvx, const vecteur & lvx_s,int ordre,int direction,sparse_poly1 & s,GIAC_CONTEXT){
    s.clear();
    int pos=equalposcomp(lvx,e);
    if (pos){
      gen f=lvx_s[pos-1]; // since vectors begin at position 0
      if (is_zero(f)){
	return true;
      }
      if (f.type==_SPOL1){
	s=*(f._SPOL1ptr);
	return true;
      }
      if (f.type!=_VECT)
	return false; // settypeerr();
      vecteur2sparse_poly1(*f._VECTptr,s);
      return true;
    }
    if ( (e.type!=_SYMB) || !contains(e,x) ){
      gen en=normal(e,contextptr);
      if (!is_zero(en))
	s.push_back(monome(en,0));
      return true;
    }
    // do rational operations
    if (e._SYMBptr->sommet==at_plus){
      if (e._SYMBptr->feuille.type!=_VECT){
	return in_series__SPOL1(e._SYMBptr->feuille,x,lvx,lvx_s,ordre,direction,s,contextptr);
      }
      const_iterateur it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      sparse_poly1 temp;
      for (;it!=itend;++it){
	if (!in_series__SPOL1(*it,x,lvx,lvx_s,ordre,direction,temp,contextptr))
	  return false;
	padd(s,temp,s,contextptr);
      }
      return true;
    }
    if (e._SYMBptr->sommet==at_neg){
      if (e._SYMBptr->feuille.type!=_VECT){
	if (!in_series__SPOL1(e._SYMBptr->feuille,x,lvx,lvx_s,ordre,direction,s,contextptr))
	  return false;
	pneg(s,s,contextptr);
	return true;
      }
      const_iterateur it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      sparse_poly1 temp;
      for (;it!=itend;++it){
	if (!in_series__SPOL1(*it,x,lvx,lvx_s,ordre,direction,temp,contextptr))
	  return false;
	pneg(temp,temp,contextptr);
	padd(s,temp,s,contextptr);
      }
      return true;
    }
    if (e._SYMBptr->sommet==at_prod){
      if (e._SYMBptr->feuille.type!=_VECT){
	if (!in_series__SPOL1(e._SYMBptr->feuille,x,lvx,lvx_s,ordre,direction,s,contextptr))
	  return false;
	return true;
      }
      const_iterateur it=e._SYMBptr->feuille._VECTptr->begin(),itend=e._SYMBptr->feuille._VECTptr->end();
      sparse_poly1 temp;
      s=sparse_poly1(1,monome(1,0));
      for (;it!=itend;++it){
	if (!in_series__SPOL1(*it,x,lvx,lvx_s,ordre,direction,temp,contextptr) ||
	    !pmul(s,temp,s,true,ordre,contextptr))
	  return false;
      }
      return true;
    }
    if (e._SYMBptr->sommet==at_inv){
      if (e._SYMBptr->feuille.type==_VECT)
	return false; // setsizeerr(gettext("series.cc/in_series__SPOL1"));
      sparse_poly1 temp;
      if (!in_series__SPOL1(e._SYMBptr->feuille,x,lvx,lvx_s,ordre,direction,temp,contextptr))
	return false;
      return pdiv(sparse_poly1(1,monome(1,0)),temp,s,ordre,contextptr);
    }
    if (e._SYMBptr->sommet==at_pow){
      // the power is independent on x
      gen base=(*(e._SYMBptr->feuille._VECTptr))[0];
      gen exponent=(*(e._SYMBptr->feuille._VECTptr))[1];
      if (!in_series__SPOL1(base,x,lvx,lvx_s,ordre,direction,s,contextptr))
	return false;
      return ppow(s,exponent,ordre,direction,s,contextptr);
    }
    // unknown rational operator
    invalidserieserr(gettext("unknown rational operator"));
    return false; // 
  }

  static int find_direction(const sparse_poly1 & s,int direction,GIAC_CONTEXT){
    int image_of_direction=0;
    if (!s.empty() && fastsign(s.front().coeff,0)){
      if (direction)
	image_of_direction=1;
      else {
	if (s.front().exponent.type==_INT_) {
	  if (s.front().exponent.val %2)
	    image_of_direction=direction;
	  else
	    image_of_direction=1;
	}
      }
      image_of_direction=image_of_direction*fastsign(s.front().coeff,0);
      return image_of_direction;
    }
    return 0;
  }

  static int ck_is_greater(const sparse_poly1 & s1, sparse_poly1 & s2,int direction,GIAC_CONTEXT){
    sparse_poly1 s(s2);
    pneg(s,s,contextptr);
    padd(s1,s,s,contextptr);
    int image_of_direction=find_direction(s,direction,contextptr);
    if (!image_of_direction){
      cksignerr(s);
      return -1;
    }
    return image_of_direction==1;
  }

  static gen in_limit(const gen & e,const identificateur & x,const gen & lim_point,int direction,GIAC_CONTEXT);

  static bool mrv_lead_term(const gen & e,const identificateur & x,gen & coeff, gen & mrv_var, gen & exponent,sparse_poly1 & q,int begin_ordre,GIAC_CONTEXT,bool series);

  bool series__SPOL1(const gen & e_orig,const identificateur & x,const gen & lim_point,int ordre,int direction,sparse_poly1 & s,GIAC_CONTEXT){
    gen e(e_orig);
    // fast check first
    if (!contains(e,x)){
      s.push_back(monome(e,0));
      return true;
    }
    if (e.type==_IDNT){
      if (!is_zero(lim_point))
	s.push_back(monome(lim_point));
      if (ordre)
	s.push_back(monome(1,1));
      else
	s.push_back(monome(undef,1));
      return true;
    }
    if (e.type!=_SYMB)
      return false; // settypeerr(); // comp not allowed
    // rewrite cos/sin/tan constants using rootof
    vecteur lv1(lvar(e)),lva,lvb;
    gen tmp;
    const_iterateur lv1_it=lv1.begin(),lv1_itend=lv1.end();
    for (;lv1_it!=lv1_itend;++lv1_it){
      if (lv1_it->type==_SYMB){
	unary_function_ptr & u =lv1_it->_SYMBptr->sommet;
	if ( (u==at_cos || u==at_sin || u==at_tan) && has_evalf(*lv1_it,tmp,1,contextptr)){
	  tmp=normal(trig2exp(*lv1_it,contextptr),contextptr);
	  if (lop(tmp,at_exp).empty()){
	    lva.push_back(*lv1_it);
	    lvb.push_back(tmp);
	  }
	}
      }
    }
    if (!lva.empty())
      e=subst(e,lva,lvb,false,contextptr);
    // find list of vars depending on x
    vecteur lvx(rlvarx(e,x));
    iterateur lvx_it=lvx.begin(),lvx_end=lvx.end();
    // find asymptotic series expansion of vars in lvx
    vecteur lvx_s;
    lvx_s.reserve(lvx_end-lvx_it);
    for (;lvx_it!=lvx_end;++lvx_it){
      if (lvx_it->type==_IDNT){
	sparse_poly1 tmp;
	if (!is_zero(lim_point))
	  tmp.push_back(monome(lim_point));
	if (ordre)
	  tmp.push_back(monome(1,1));
	else
	  tmp.push_back(monome(undef,1));
	lvx_s.push_back(tmp);
	continue;
      }
      if (lvx_it->type!=_SYMB) // just in case...
	return false; // settypeerr();
      // test for a^b
      symbolic temp__SYMB=*lvx_it->_SYMBptr;
      if ( (temp__SYMB.sommet==at_pow) && (!contains((*temp__SYMB.feuille._VECTptr)[1],x) ) ){
	if (!in_series__SPOL1((*temp__SYMB.feuille._VECTptr)[0],x,lvx,lvx_s,ordre,direction,s,contextptr)||
	    !ppow(s,(*temp__SYMB.feuille._VECTptr)[1],ordre,direction,s,contextptr))
	  return false;
	lvx_s.push_back(s);
	continue;
      }
      if (temp__SYMB.sommet==at_pow)
	temp__SYMB=symbolic(at_exp,(*temp__SYMB.feuille._VECTptr)[1]*ln((*temp__SYMB.feuille._VECTptr)[0],contextptr));
      // Check here for logarithms if image_of_lim_point=+/-inf
      // In such case we must factor x^s.begin().exponent and add
      // it to the exponent 0 term of the series expansion of the ln
      if (temp__SYMB.sommet==at_ln){
	// recursive call, works since lvx is sorted by increasing size
	if (!in_series__SPOL1(temp__SYMB.feuille,x,lvx,lvx_s,ordre,direction,s,contextptr))
	  return false;
	gen exponent=s.front().exponent;
	gen c=s.front().coeff;
	s.erase(s.begin());
	bool adjust=false;
	if (is_positive(-c,contextptr)){ 
	  // im(ln(c)) is i*pi, but im(ln(c*x^exposant+...)) might be -i*pi
	  // check sign of imaginary part of expansion
	  sparse_poly1::iterator it=s.begin(),itend=s.end();
	  for (;it!=itend;++it){
	    if (is_undef(it->coeff))
	      break;
	    gen tmp=im(it->coeff,contextptr);
	    if (!is_zero(tmp)){
	      if (is_positive(-tmp,contextptr))
		adjust=true;
	      break;
	    }
	  }
	}
	if (!s.empty()){
	  pshift(s,-exponent,s,contextptr);
	  if (!pdiv(s,c,s,contextptr))
	    return false;
	  vecteur expansion(1,zero);
	  expansion.reserve(ordre);
	  for (int i=1;i<=ordre;i++){
	    if (i%2)
	      expansion.push_back(inv(gen(i),contextptr));
	    else
	      expansion.push_back(-inv(gen(i),contextptr));
	  }
	  expansion.push_back(undef);
	  if (!pcompose(expansion,s,s,contextptr))
	    return false;
	}
	if (!is_zero(exponent) || !is_one(c)){
	  c=ln(c,contextptr);
	  if (adjust)
	    c-=cst_two_pi*cst_i;
	  s.insert(s.begin(),monome(exponent*ln(x,contextptr)+c));
	}
	lvx_s.push_back(s);
	continue;
	cout << s.back() << endl;
      }
      // test for the special case var=f(x)
      if ((temp__SYMB.feuille.type==_IDNT) && (temp__SYMB.sommet!=at_abs)){ 
	// Since e contains x feuille of e must be x
	if (!temp__SYMB.sommet.ptr()->series_expansion){
	  *logptr(contextptr) << gettext("no taylor method for ") << temp__SYMB.sommet.ptr()->print(contextptr) << endl;
	  return false; 
	}
	gen shift_coeff;
	gen res=temp__SYMB.sommet.ptr()->series_expansion(lim_point,ordre,temp__SYMB.sommet,direction,shift_coeff,contextptr); 
	if (res.type==_SPOL1)
	  lvx_s.push_back(*res._SPOL1ptr);
	else {// res must be a vecteur
	  if (res.type!=_VECT)
	    return false; // settypeerr(gettext("series.cc 1066"));
	  sparse_poly1 temp(vecteur2sparse_poly1(*res._VECTptr));
	  if (!is_zero(shift_coeff)){
	    pshift(temp,shift_coeff,temp,contextptr);
	    if (is_positive(shift_coeff,contextptr))
	      temp.insert(temp.begin(),monome(temp__SYMB.sommet(lim_point,contextptr))); 
	  }
	  lvx_s.push_back(temp);
	}
	continue;
      }
      // Taylor not successfull: find series_expansion of arg, 
      // compose with sommet expansion
      // fixme: multiargs disabled, should return a vecteur of series_exp
      if (temp__SYMB.sommet != at_of && temp__SYMB.feuille.type==_VECT){
	int nargs=temp__SYMB.feuille._VECTptr->size();
	if (temp__SYMB.sommet==at_euler_mac_laurin){
	  if (nargs!=5){
	    invalidserieserr(gettext("Integral must be definite"));
	    return false;
	  }
	  vecteur & tempfv=*temp__SYMB.feuille._VECTptr;
	  gen k=tempfv[2];
	  if (contains(k,x)){
	    invalidserieserr(gettext("Summation variable must be != from series expansion variable"));
	    return false;
	  }
	  if (k.type!=_IDNT)
	    return false; // setsizeerr();
	  // find upper and lower bound limit
	  gen lower=tempfv[3],upper=tempfv[4];
	  gen limlo=limit(lower,x,0,1,contextptr);
	  gen limup=limit(upper,x,0,1,contextptr);
	  gen f=tempfv[0],F=tempfv[1];
	  gen flim=limit(f,x,0,1,contextptr);
	  gen Flim=limit(F,x,0,1,contextptr);
	  gen eff=sum(flim,x,limlo,limup,contextptr);
	  if (is_inf(eff)){
	    *logptr(contextptr) << gettext("Limit of divergent sum not implemented yet") << endl;
	    return false;
	  }
	  // euler 1st part sum(f(n,k),k,lim(lower),lower-1)
	  // euler 2nd part sum(f(n,k),k,upper+1,lim(upper)
	  // euler 3rd part sum(f(n.k)-flim(k),lim(lower),lim(upper))
	  // first add integral part
	  if (is_zero(flim))
	    eff += preval(F,k,lower,upper,contextptr);
	  else {
	    if (lower!=limlo)
	      eff += -preval(F,k,limlo,lower-1,contextptr);
	    if (upper!=limup)
	      eff += -preval(F,k,upper+1,limup,contextptr);
	    eff += preval(F-Flim,k,limlo,limup,contextptr);
	  }
	  // then f part
	  if (is_zero(flim))
	    eff += (limit(f,*k._IDNTptr,limlo,0,contextptr)+limit(f,*k._IDNTptr,limup,0,contextptr) )/2 ;
	  else {
	    if (lower!=limlo)
	      eff += (-limit(f,*k._IDNTptr,limlo,0,contextptr)-limit(f,*k._IDNTptr,lower-1,0,contextptr))/2;
	    if (upper!=limup)
	      eff += (-limit(f,*k._IDNTptr,upper+1,0,contextptr)-limit(f,*k._IDNTptr,limup,0,contextptr))/2;
	    eff += (limit(f-flim,*k._IDNTptr,limlo,0,contextptr)+limit(f-flim,*k._IDNTptr,limup,0,contextptr))/2;
	  }
	  // then Bernoulli part
	  gen fdiff=derive(f,k,contextptr);
	  gen flimdiff=derive(f,k,contextptr);
	  if (is_undef(fdiff) || is_undef(flimdiff))
	    return false;
	  for (int i=1;i<ordre;i++){
	    gen add;
	    if (is_zero(flim))
	      add = -limit(fdiff,*k._IDNTptr,limlo,0,contextptr)+limit(fdiff,*k._IDNTptr,limup,0,contextptr);
	    else {
	      if (lower!=limlo)
		add += limit(fdiff,*k._IDNTptr,limlo,0,contextptr)-limit(fdiff,*k._IDNTptr,lower-1,0,contextptr);
	      if (upper!=limup)
		add += limit(fdiff,*k._IDNTptr,upper+1,0,contextptr)-limit(fdiff,*k._IDNTptr,limup,0,contextptr);
	      add += -limit(fdiff-flimdiff,*k._IDNTptr,limlo,0,contextptr)+limit(fdiff-flimdiff,*k._IDNTptr,limup,0,contextptr);
	    }
	    add=add*bernoulli(2*i)/factorial(2*i);
	    eff += add; // fdiff flimdiff 2 fois
	    fdiff=derive(fdiff,k,contextptr);
	    fdiff=ratnormal(derive(fdiff,k,contextptr));
	    flimdiff=derive(flimdiff,k,contextptr);
	    flimdiff=ratnormal(derive(flimdiff,k,contextptr));
	    if (is_undef(fdiff) || is_undef(flimdiff))
	      return false;
	  }
	  // must do a recursive call since eff may contain new functions
	  gen coeff,mrv_var,exponent; 
	  eff =subst(eff,x,inv(x,contextptr),true,contextptr);
	  if (!mrv_lead_term(eff,x,coeff,mrv_var,exponent,s,ordre,contextptr,true))
	    return false;
	  lvx_s.push_back(s);
	  continue;
	  // never reached setsizeerr();
	}
	if (temp__SYMB.sommet==at_integrate){
	  if (nargs!=4){
	    invalidserieserr(gettext("Integral must be definite"));
	    return false;
	  }
	  vecteur & tempfv=*temp__SYMB.feuille._VECTptr;
	  gen t=tempfv[1];
	  if (contains(t,x)){
	    invalidserieserr(gettext("Integration variable must be != from series expansion variable"));
	    return false;
	  }
	  if (!in_series__SPOL1(tempfv[0],x,lvx,lvx_s,ordre,direction,s,contextptr))
	    return false;
	  // FIXME if tempfv[3] and tempfv[2] tends to the same limit l
	  // we may expand tempfv[0] w.r.t. t at l before integration
	  
	  // integrate s term by term wrt t
	  if (!pintegrate(s,t,contextptr))
	    return false;
	  gen remains,primit=sparse_poly12gen(s,x,remains,false);
	  // then compose primit at bounds and substract
	  primit=subst(primit,t,tempfv[3],false,contextptr)-subst(primit,t,tempfv[2],false,contextptr);
	  if (!series__SPOL1(primit,x,lim_point,ordre,direction,s,contextptr))
	    return false;
	  // add remains to s: 
	  // int(remains,t,tempfv[2],tempfv[3]) = 
	  // (tempfv[3]-tempfv[2])*remains(theta) with theta in interval
	  remains=remains*(tempfv[3]-tempfv[2]);
	  sparse_poly1 p; 
	  if (!series__SPOL1(remains,x,lim_point,ordre,direction,p,contextptr))
	    return false;
	  if (p.empty()){
	    invalidserieserr(gettext("Can not expand remainder of integrand"));
	    return false;
	  }
	  p=sparse_poly1(p.begin(),p.begin()+1);
	  p.front().coeff=undef;
	  s=spadd(p,s,contextptr);
	  lvx_s.push_back(s);
	  continue;
	}
	const_iterateur fit=temp__SYMB.feuille._VECTptr->begin(),fitend=temp__SYMB.feuille._VECTptr->end();
	if (temp__SYMB.sommet==at_Psi || temp__SYMB.sommet==at_Eta || temp__SYMB.sommet==at_Zeta){
	  if (!in_series__SPOL1(*fit,x,lvx,lvx_s,ordre,direction,s,contextptr))
	    return false;
	}
	else {
	  bool ok=false;
	  dbgprint_vector <sparse_poly1> vs;
	  vs.reserve(fitend-fit);
	  for (;fit!=fitend;++fit){
	    if (!in_series__SPOL1(*fit,x,lvx,lvx_s,ordre,direction,s,contextptr))
	      return false;
	    vs.push_back(s);
	  }
	  if (temp__SYMB.sommet==at_max){
	    ok=true;
	    int testck=ck_is_greater(vs.front(),vs.back(),direction,contextptr);
	    if (testck==-1)
	      return false;
	    if (testck)
	      s=vs.front();
	    else
	      s=vs.back();
	  }
	  if (temp__SYMB.sommet==at_min){
	    ok=true;
	    int testck=ck_is_greater(vs.front(),vs.back(),direction,contextptr);
	    if (testck==-1)
	      return false;
	    if (testck)
	      s=vs.back();
	    else
	      s=vs.front();
	  }
	  if (!ok){
	    invalidserieserr(gettext(" multiargs not implemented"));
	    return false;
	  }
	  lvx_s.push_back(s);
	  continue;
	}
      }
      else { // 1-arg function
	if (temp__SYMB.sommet==at_of){
	  gen & tf=temp__SYMB.feuille;
	  if (tf.type==_VECT && tf._VECTptr->size()==2){
	    gen tff=tf._VECTptr->front();
	    gen tfx=tf._VECTptr->back();
	    if (!in_series__SPOL1(tfx,x,lvx,lvx_s,ordre,direction,s,contextptr)) return false; // s<-arg
	  }
	}
	else {
	  if (!in_series__SPOL1(temp__SYMB.feuille,x,lvx,lvx_s,ordre,direction,s,contextptr)) return false; // s<-arg
	}
      } // end 1-arg function
      gen image_of_lim_point; 
      int image_of_direction=0;
      if (!s.empty()){
	if (s.begin()->exponent==0){
	  image_of_lim_point=s.begin()->coeff;
	  // ?? FIXME ???
	  if (temp__SYMB.sommet!=at_abs){
	    s.erase(s.begin()); // remove cst coeff from s
	    for (;!s.empty();){
	      s.front().coeff=normal(s.front().coeff,contextptr);
	      if (!is_zero(s.front().coeff))
		break;
	      s.erase(s.begin());
	    }
	  }
	}
	else {
	  if (ck_is_strictly_positive(s.begin()->exponent,contextptr))
	    image_of_lim_point=0;
	  else {
	    image_of_lim_point=unsigned_inf;
	    if ( (s.begin()->exponent.type==_INT_) && !(s.begin()->exponent.val%2) ){ // odd negative exponent
	      if (is_strictly_positive(s.begin()->coeff,contextptr))
		image_of_lim_point=plus_inf;
	      if (is_strictly_positive(-s.begin()->coeff,contextptr))
		image_of_lim_point=minus_inf;
	    }
	    else { // other negative exponent
	      if (direction){
		if (is_strictly_positive(s.begin()->coeff,contextptr))
		  image_of_lim_point=plus_inf;
		if (is_strictly_positive(-s.begin()->coeff,contextptr))
		  image_of_lim_point=minus_inf;
		if (direction<0){
		  if (s.begin()->exponent.type==_INT_)
		    image_of_lim_point=-image_of_lim_point;
		  else
		    image_of_lim_point=unsigned_inf;
		}
	      }
	    }
	  } // end negative leading exponent
	} // end non-zero leading exponent
      } // end non-empty series
      image_of_direction = find_direction(s,direction,contextptr);
      // Symbolic series expansion f(x), f is assumed to be analytic
      if (temp__SYMB.sommet==at_of){
	gen & tf=temp__SYMB.feuille;
	if (tf.type==_VECT && tf._VECTptr->size()==2){
	  gen tff=tf._VECTptr->front();
	  gen tfx=tf._VECTptr->back();
	  // Symbolic Taylor expansion of tff
	  vecteur expansion(1,symbolic(at_of,makesequence(tff,image_of_lim_point))); 
	  expansion.reserve(ordre);
	  for (int i=1;i<=ordre;i++){
	    gen fn;
	    if (i==1)
	      fn=symbolic(at_of,makesequence(symbolic(at_function_diff,tff),image_of_lim_point));
	    else
	      fn=symbolic(at_of,makesequence(symbolic(at_of,makesequence(symbolic(at_composepow,makesequence(at_function_diff,i)),tff)),image_of_lim_point));
	    expansion.push_back(fn/factorial(i));
	  }
	  expansion.push_back(undef);
	  if (!pcompose(expansion,s,s,contextptr))
	    return false;
	  lvx_s.push_back(s);
	  continue;
	}
      }
      if (temp__SYMB.sommet==at_abs){
	if (!image_of_direction){
	  *logptr(contextptr) << gettext("Sign error ") << s << endl;
	  return false; // cksignerr(s);
	}
	if (image_of_direction==-1)
	  pneg(s,s,contextptr);
	lvx_s.push_back(s);
	continue;
      }
      if (is_inf(image_of_lim_point)){ 
	// check for sin/cos
	if (temp__SYMB.sommet==at_cos || temp__SYMB.sommet==at_sin){
	  // split the series expansion in two parts, one tending -> 0
	  sparse_poly1::iterator it=s.begin(),itend=s.end();
	  for (;it!=itend;++it){
	    if (ck_is_strictly_greater(it->exponent,zero,contextptr))
	      break;
	  }
	  sparse_poly1 s0(it,s.end()),s1(s.begin(),it);
	  // expansion is done at s0
	  image_of_lim_point=s1;
	  s=s0;
	}
	else {
	  // the function is assumed to have an expansion at infinity
	  // invert series expansion
	  sparse_poly1 stmp;
	  if (!pdiv(sparse_poly1(1,monome(1,0)),s,stmp,ordre,contextptr))
	    return false;
	  s=stmp;
	}
      }
      gen shift_coeff;
      if (!temp__SYMB.sommet.ptr()->series_expansion){
	*logptr(contextptr) << string(gettext("Not expandable "))+temp__SYMB.sommet.ptr()->s << endl;
	return false;
      }
      int addorder=0;
      gen expansion;
      if ( (temp__SYMB.sommet==at_Psi ||temp__SYMB.sommet==at_Eta ||temp__SYMB.sommet==at_Zeta) && temp__SYMB.feuille.type==_VECT){
	if (temp__SYMB.feuille._VECTptr->size()!=2 )
	  return false; // setsizeerr();
	addorder=temp__SYMB.feuille._VECTptr->back().val;
	if (addorder<=0){
	  *logptr(contextptr) << gettext("Psi/Zeta/Eta: bad second argument") << endl;
	  return false;
	}
	if (temp__SYMB.sommet==at_Psi)
	  expansion=at_Psi_minus_ln->ptr()->series_expansion(image_of_lim_point,ordre+addorder,temp__SYMB.sommet,image_of_direction,shift_coeff,contextptr); 
      }
      if (expansion==0)
	expansion=temp__SYMB.sommet.ptr()->series_expansion(image_of_lim_point,ordre+addorder,temp__SYMB.sommet,image_of_direction,shift_coeff,contextptr); 
      if (expansion.type==_VECT){
	if (addorder){
	  // derive expansion
	  vecteur & v =*expansion._VECTptr;
	  for (int i=0;i<addorder;++i){
	    int vs=v.size();
	    if (is_zero(shift_coeff)){
	      vecteur w(vs-1);
	      for (int j=1;j<vs;++j){
		w[j-1]=j*v[j];
	      }
	      v=w;
	    }
	    else {
	      for (int j=0;j<vs;++j){
		v[j]=-v[j]*(j+shift_coeff);
	      }
	      shift_coeff += 1;
	    }
	  }
	  // final correction for Psi
	  if (temp__SYMB.sommet==at_Psi){
	    v.insert(v.begin(),((addorder%2)?1:-1)/factorial(addorder));
	    shift_coeff -= 1;
	  }
	}
	if (is_zero(shift_coeff)){
	  if (!pcompose(*expansion._VECTptr,s,s,contextptr))
	    return false;
	}
	else {
	  sparse_poly1 temp;
	  if (!ppow(s,shift_coeff,ordre,direction,temp,contextptr) ||
	      !pcompose(*expansion._VECTptr,s,s,contextptr) ||
	      !pmul(s,temp,s,true,ordre,contextptr))
	    return false;
	  if (is_positive(shift_coeff,contextptr)){
	    gen imtemp;
	    if (addorder>0)
	      imtemp=temp__SYMB.sommet(makesequence(image_of_lim_point,addorder),contextptr);
	    else
	      imtemp=temp__SYMB.sommet(image_of_lim_point,contextptr);
	    if (!is_zero(imtemp))
	      s.insert(s.begin(),monome(imtemp));
	  }
	}
      }
      else {
	s.clear();
	s.push_back(monome(undef,minus_inf));
	return true;
      }
      lvx_s.push_back(s);
      // fixme: add support for sparse_poly1 composition
    } // end loop lvx_it!=lvx_end
    return in_series__SPOL1(e,x,lvx,lvx_s,ordre,direction,s,contextptr);
  }

  sparse_poly1 series__SPOL1(const gen & e,const identificateur & x,const gen & lim_point,int ordre,int direction,GIAC_CONTEXT){
    sparse_poly1 s;
    if (!series__SPOL1(e,x,lim_point,ordre,direction,s,contextptr))
      s=sparse_poly1(1,monome(1,undef));
    return s;
  }

  static sparse_poly1 ck_series__SPOL1(const gen & e,const identificateur & x,const gen & lim_point,int ordre,int direction,GIAC_CONTEXT){
    sparse_poly1 s;
    if (!series__SPOL1(e,x,lim_point,ordre,direction,s,contextptr)){
      s=sparse_poly1(1,monome(1,undef));
      return s;
    }
    // if s is not at order ordre, ask again with a modified ordre
    gen true_order=porder(s);
    if (true_order.type==_INT_ && true_order.val<=ordre){
      if (!series__SPOL1(e,x,lim_point,ordre+1+ordre-true_order.val,direction,s,contextptr))
      s=sparse_poly1(1,monome(1,undef));
    }
    return s;
  }

#ifdef DEBUG_SUPPORT
  static void inutile(sparse_poly1 & s){
    s.dbgprint();
  }
#endif

  // ***********************
  // LIMITS
  // ***********************

  bool contains(const vecteur & v,const gen & elem){
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      if (contains(*it,elem))
	return true;
    return false;
  }

  bool contains(const gen & e,const gen & elem){
    if (e==elem)
      return true;
    if (e.type==_VECT){
      return contains(*e._VECTptr,elem);
    }
    if (e.type==_SYMB){
      return contains(e._SYMBptr->feuille,elem);
    }
    if (e.type==_FRAC)
      return contains(e._FRACptr->num,elem) || contains(e._FRACptr->den,elem);
    return false;
  }
  
  vecteur lvarx(const gen &e,const gen & x,bool test){
    vecteur v(lvar(e));
    vecteur res;
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      // remove ^ if exponent does not depend on x
      if ( (it->type==_SYMB) 
	   && (it->_SYMBptr->sommet==at_pow) 
	   && !contains((*(it->_SYMBptr->feuille._VECTptr))[1],x)
	   ){
	vecteur tmp(lvarx((*(it->_SYMBptr->feuille._VECTptr))[0],x));
	const_iterateur it=tmp.begin(),itend=tmp.end();
	for (;it!=itend;++it){
	  if (!equalposcomp(res,*it))
	    res.push_back(*it);
	}
      }
      else {
	if ( (!test || res.empty() || *it!=x ) && contains(*it,x))
	  res.push_back(*it);
      }
    }
    return res;
  }

  void rlvarx(const gen &e,const gen & xgen,vecteur & res){
    const vecteur & v=lvar(e);
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!contains(*it,xgen) || equalposcomp(res,*it))
	continue;
      // recursive call
      res.push_back(*it);
      if (it->type==_SYMB) {
	rlvarx(it->_SYMBptr->feuille,xgen,res);
	if ( (it->_SYMBptr->sommet==at_pow) 
	     && contains((*(it->_SYMBptr->feuille._VECTptr))[1],xgen) )
	  rlvarx(symbolic(at_ln,(*(it->_SYMBptr->feuille._VECTptr))[0]),xgen,res);
      }
    }
  }

  vecteur rlvarx(const gen &e,const gen & x){
    vecteur res;
    rlvarx(e,x,res);
    sort(res.begin(),res.end(),symb_size_less);
    return res;
  }

  static void upscale(gen & e,const identificateur & x,GIAC_CONTEXT){
    vecteur a_remplacer,remplacer_par;
    a_remplacer.push_back(ln(x,contextptr));
    remplacer_par.push_back(x);
    a_remplacer.push_back(x);
    remplacer_par.push_back(exp(x,contextptr));
    e=subst(e,a_remplacer,remplacer_par,false,contextptr);
  }

  static void downscale(gen & e,const identificateur & x,GIAC_CONTEXT){
    vecteur a_remplacer,remplacer_par;
    a_remplacer.push_back(exp(x,contextptr));
    remplacer_par.push_back(x);
    a_remplacer.push_back(x);
    remplacer_par.push_back(ln(x,contextptr));
    e=subst(e,a_remplacer,remplacer_par,false,contextptr);
  }
  
  /*
  gen pow2exp(const gen & e,const identificateur & x){
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it)
	v.push_back(pow2exp(*it,x));
      return v;
    }
    if (e.type!=_SYMB)
      return e;
    if ( e._SYMBptr->sommet==at_pow && contains((*(e._SYMBptr->feuille._VECTptr))[1],x))
      return exp(pow2exp((*(e._SYMBptr->feuille._VECTptr))[1],x)*pow2exp(ln((*(e._SYMBptr->feuille._VECTptr))[0]),x));
    if ( e._SYMBptr->sommet==at_tan && contains (e._SYMBptr->feuille,x))
      return symbolic(at_sin,pow2exp(e._SYMBptr->feuille,x))/symbolic(at_cos,pow2exp(e._SYMBptr->feuille,x));
    return e._SYMBptr->sommet(pow2exp(e._SYMBptr->feuille,x),contextptr); 
  }
  */

  static bool check_bounded(const gen & g){
    vecteur v=loptab(g,sincostan_tab);
    int vs=v.size();
    for (int i=0;i<vs;++i){
      if (v[i].type==_SYMB && v[i]._SYMBptr->feuille.type==_SPOL1)
	return true;
    }
    return false;
  }

  // specialization 
  static int equalposcomp(const std::vector<const unary_function_ptr *> & v,unary_function_ptr * w){
    int n=1;
    for (std::vector<const unary_function_ptr *>::const_iterator it=v.begin();it!=v.end();++it){
      if (*(*it)==*w)
	return n;
      else
	n++;
    }
    return 0;
  }

  static gen ln_expand0_(const gen & e,GIAC_CONTEXT){
    if (e.type!=_SYMB)
      return ln(e,contextptr);
    if (e._SYMBptr->sommet==at_exp)
      return e._SYMBptr->feuille;
    if (e._SYMBptr->sommet==at_prod)
      return symbolic(at_plus,apply(e._SYMBptr->feuille,ln_expand0_,contextptr));
    if (e._SYMBptr->sommet==at_inv)
      return -ln_expand0_(e._SYMBptr->feuille,contextptr);
    if (e._SYMBptr->sommet==at_pow){
      gen & tmp=e._SYMBptr->feuille;
      if (tmp.type==_VECT && tmp._VECTptr->size()==2)
	return tmp._VECTptr->back()*ln_expand0_(tmp._VECTptr->front(),contextptr);
    }
    return ln(e,contextptr);
  }

  static gen ln_expand_(const gen & e0,GIAC_CONTEXT){
    gen e(factor(e0,false,contextptr));
    return ln_expand0_(e,contextptr);
  }

  static gen remove_lnexp(const gen & e,GIAC_CONTEXT){
    vector<const unary_function_ptr *> v(1,at_ln);
    vector< gen_op_context > w(1,&ln_expand_);
    return subst(e,v,w,false,contextptr);
  }

  gen limit_symbolic_preprocess(const gen & e0,const identificateur & x,const gen & lim_point,int direction,GIAC_CONTEXT){
    // FIXME: add support for int and sum
    gen e=factorial2gamma(e0,contextptr);
    // Find functions depending of x in e which are in the list
    // If their argument tends to +/-infinity, replace these functions
    vecteur v=rlvarx(e,x);
    int vs=v.size(),pos1,pos2;
    vecteur v1,v2;
    for (int i=0;i<vs;++i){
      if (v[i].type==_SYMB){
	if (v[i].is_symb_of_sommet(at_ln)){
	  gen g=limit(v[i]._SYMBptr->feuille,x,lim_point,direction,contextptr);
	  if (is_inf(g) && g!=plus_inf)
	    return gensizeerr(gettext("ln of unsigned or minus infinity"));
	}
	if (v[i].is_symb_of_sommet(at_sum)){
	  gen tmp;
	  if (!convert_to_euler_mac_laurin(v[i],tmp,contextptr))
	    return gensizeerr(gettext("Unable to convert sum to Euler Mac-Laurin")+v[i].print(contextptr));
	  v1.push_back(v[i]);
	  v2.push_back(tmp);
	}
	if ( ( (pos1=equalposcomp(limit_tab,v[i]._SYMBptr->sommet)) || (pos2=equalposcomp(limit_tractable_functions(),&v[i]._SYMBptr->sommet)) ) ){
	  gen g=limit(v[i]._SYMBptr->feuille,x,lim_point,direction,contextptr);
	  if (is_inf(g)){
	    v1.push_back(v[i]);
	    v2.push_back(pos1?limit_replace[pos1-1](v[i]._SYMBptr->feuille,contextptr):limit_tractable_replace()[pos2-1](v[i]._SYMBptr->feuille,contextptr));
	  }
	  if (is_zero(g)){
	    if (v[i]._SYMBptr->sommet==at_Ci){
	      v1.push_back(v[i]);
	      v2.push_back(Ci_replace0(v[i]._SYMBptr->feuille,contextptr));
	    }
	    if (v[i]._SYMBptr->sommet==at_Ei){
	      v1.push_back(v[i]);
	      v2.push_back(Ei_replace0(v[i]._SYMBptr->feuille,contextptr));
	    }
	  }
	}
      }
    }
    if (!v1.empty())
      e=remove_lnexp(subst(e,v1,v2,false,contextptr),contextptr);
    v=rlvarx(e,x);
    vs=v.size();
    v1.clear(); v2.clear();
    for (int i=0;i<vs;++i){
      if (v[i].is_symb_of_sommet(at_sign) && v[i]!=e){
	gen g;
#ifndef NO_STDEXCEPT
	try {
#endif
	  g=limit(v[i],x,lim_point,direction,contextptr);
	  v1.push_back(v[i]);
	  v2.push_back(g);
#ifndef NO_STDEXCEPT
	} catch (std::runtime_error & ) {
	}
#endif
      }
    }
    e=subst(e,v1,v2,false,contextptr);
    return e;
  }

  static gen in_limit(const gen & e0,const identificateur & x,const gen & lim_point,int direction,GIAC_CONTEXT){
    if (direction==-2)
      return gensizeerr(contextptr);
    if (e0.type==_VECT){
      const_iterateur it=e0._VECTptr->begin(),itend=e0._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it){
	res.push_back(in_limit(*it,x,lim_point,direction,contextptr));
      }
      return gen(res,e0.subtype);
    }
    if (_about(x,contextptr)!=x){
      identificateur xprime(" "+print_INT_(giac_rand(contextptr)));
      return in_limit(quotesubst(e0,x,xprime,contextptr),xprime,lim_point,direction,contextptr);
    }
    gen e=Heavisidetosign(when2sign(piecewise2when(e0,contextptr),contextptr),contextptr);
    // Adjust direction for +/- inf limits
    if (lim_point==plus_inf)
      direction=1;
    if (lim_point==minus_inf)
      direction=-1;    
    // First try substitution
    if (has_i(lop(e,at_ln)))
      e=recursive_normal(expln2trig(e,contextptr),contextptr);
    if (loptab(e,sign_floor_ceil_round_tab).empty()){
      gen first_try;
      if (lim_point==unsigned_inf){
	*logptr(contextptr) << gettext("Warning, infinity is unsigned, perhaps you meant +infinity")<< endl;
	first_try = subst(partfrac(e,false,contextptr),x,lim_point,false,contextptr);
	// first_try = subst(ratnormal(e),x,lim_point,false,contextptr);
      }
      else {
	first_try = quotesubst(partfrac(e,false,contextptr),x,lim_point,contextptr);
	// first_try = quotesubst(ratnormal(e),x,lim_point,contextptr);
      }
      first_try = recursive_normal(eval(first_try,eval_level(contextptr),contextptr),contextptr);
      if (!is_undef(first_try)){
	if (!direction) 
	  return first_try;
	if (first_try!=unsigned_inf)
	  return first_try;
      }
    }
    e=limit_symbolic_preprocess(e,x,lim_point,direction,contextptr);
    if (is_undef(e)) return e;
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode))
      return errcode;
    if (e.type!=_SYMB) // e might be an _IDNT equal to x (limit(x*sign(x),x,0,1))
      return subst(e,x,lim_point,false,contextptr);
    if (e._SYMBptr->sommet==at_exp)
      return exp(in_limit(e._SYMBptr->feuille,x,lim_point,direction,contextptr),contextptr);
    if (e._SYMBptr->sommet==at_ln){
      gen tmp=in_limit(e._SYMBptr->feuille,x,lim_point,direction,contextptr);
      if (is_undef(tmp)) return tmp;
      if (!is_positive(-tmp,contextptr))
	return ln(tmp,contextptr);
    }
    gen e_copy;
    // Rewrite non rational ^ and tan 
    e_copy=_pow2exp(tan2sincos(e,contextptr),contextptr);
    // FIXME: this translate exp(i*...) to sin/cos without bugging for
    // exp(exp(exp(x)/(1-1/x)))-exp(exp(exp(x)/(1-1/x-exp((-(ln(x)))*ln(ln(x))))))
    if (has_i(e_copy)) {
      e_copy=subst(e_copy,tan_tab,tan2sincos_tab,true,contextptr);
      e_copy=subst(e_copy,exp_tab,exp2sincos_tab,true,contextptr);
    }
    if (!direction) { // supposed to be analytic, series expansion only
      sparse_poly1 p;
      p.push_back(monome(undef,0));
      double ordre=mrv_begin_order;
      for ( ; !p.empty() && is_undef(p.front().coeff) && (ordre<max_series_expansion_order);ordre=1.5*ordre+1) 
	p=series__SPOL1(e_copy,x,lim_point,int(ordre),0,contextptr);
      // cout << p << endl;
      if (ordre>=max_series_expansion_order)
	return genmaxordererr();
      if (p.empty() || ck_is_strictly_positive(p.front().exponent,contextptr))
	return 0;
      if (ck_is_strictly_positive(-p.front().exponent,contextptr))
	return unsigned_inf;
      if (is_zero(p.front().exponent)){
	if (contains(p.front().coeff,x)){
	  return gensizeerr(gettext("Try unidirectional series"));
	}
	return check_bounded(p.front().coeff)?bounded_function(contextptr):p.front().coeff;
      }
      return gensizeerr(gettext("Series internal bug"));
    }
    // Unidirectional limit, rewrite first (if needed)
    if (is_inf(lim_point)){
      if (lim_point==minus_inf)
	e_copy=subst(e_copy,x,-x,false,contextptr);
    }
    else {
      if (direction>0)
	e_copy=subst(e_copy,x,lim_point+inv(x,contextptr),false,contextptr);
      else
	e_copy=subst(e_copy,x,lim_point-inv(x,contextptr),false,contextptr);
    }
    gen coeff,mrv_var,exponent;
    sparse_poly1 p;
    if (!mrv_lead_term(e_copy,x,coeff,mrv_var,exponent,p,mrv_begin_order,contextptr,false))
      return gensizeerr(contextptr);
    if (ck_is_strictly_positive(exponent,contextptr))
      return 0;
    if (is_zero(exponent))
      return check_bounded(coeff)?bounded_function(contextptr):coeff;
    // check sign of coeff, if coeff depends on x first find equivalent
    gen essai=subst(coeff,x,plus_inf,false,contextptr);
    if (is_undef(essai) || is_zero(essai) || (essai==unsigned_inf)){
      while (contains(coeff,x)){
	e_copy=coeff;
	if (!mrv_lead_term(e_copy,x,coeff,mrv_var,exponent,p,mrv_begin_order,contextptr,false))
	  return gensizeerr(contextptr);
      }
      essai=coeff;
    }
    gen s=0;
    if (calc_mode(contextptr)!=1 || !has_i(p)) // should do it only up to order 0 terms
      s=sign(essai,contextptr); 
    if (s==plus_one)
      return plus_inf;
    if (s==minus_one)
      return minus_inf;
    // ? FIXME: if essai=cos(<pi,-1>)+i*sin(<pi,-1>) unsigned_inf better
    // limit((-2)^n,n,inf)
    return check_bounded(essai)?undef:unsigned_inf;
    /* 
    essai=eval(subst(essai,sincosinf,vecteur(sincosinf.size(),undef)));
    if (is_undef(essai))
      return undef;
    else
      return unsigned_inf;
    */
  }
  
  // return plus_inf if a > b (at x=+infinity), !0 if a#b, 0 if a < b
  static gen mrv_compare(const gen & a,const gen & b,const identificateur & x,GIAC_CONTEXT){
    if ((a.type!=_SYMB) && (b.type!=_SYMB))
      return 1;
    gen lna,lnb,l;
    if ((a.type==_SYMB) && (a._SYMBptr->sommet==at_exp))
      lna=a._SYMBptr->feuille;
    else
      lna=ln(a,contextptr);
    if ((b.type==_SYMB) && (b._SYMBptr->sommet==at_exp))
      lnb=b._SYMBptr->feuille;
    else
      lnb=ln(b,contextptr);
    gen coeff,mrv_var,exponent;
    sparse_poly1 p;
    if (!mrv_lead_term(rdiv(lna,lnb,contextptr),x,coeff,mrv_var,exponent,p,mrv_begin_order,contextptr,false))
      return gensizeerr(contextptr);
    if (ck_is_strictly_positive(exponent,contextptr))
      return 0;
    if (is_zero(exponent))
      return coeff;
    return plus_inf;
  }

  static bool mrv_max(const vecteur & a_faster_var, const vecteur & a_coeff_ln, const vecteur & a_slower_var, const vecteur & b_faster_var,const vecteur & b_coeff_ln, const vecteur & b_slower_var,const identificateur & x, vecteur & faster_var, vecteur & coeff_ln, vecteur & slower_var,GIAC_CONTEXT){
    int pos_a,pos_b;
    gen s;
    if (intersect(a_faster_var,b_faster_var,pos_a,pos_b))
      s=normal(rdiv(a_faster_var[pos_a],b_faster_var[pos_b],contextptr),contextptr);
    else {
      if (a_faster_var.empty() || intersect(a_faster_var,b_slower_var,pos_a,pos_b))
	s=0;
      else {
	if (b_faster_var.empty() || intersect(b_faster_var,a_slower_var,pos_a,pos_b) )
	  s=plus_inf;
	else
	  s=mrv_compare(a_faster_var.front(),b_faster_var.front(),x,contextptr);
      }
      if (is_undef(s))
	return false;
      if (s==plus_inf){
	slower_var=mergevecteur(a_slower_var,b_slower_var);
	slower_var=mergevecteur(b_faster_var,slower_var);      
	faster_var=a_faster_var;
	coeff_ln=a_coeff_ln;
	return true;
      }
      if (is_zero(s)){
	slower_var=mergevecteur(a_slower_var,b_slower_var);
	slower_var=mergevecteur(a_faster_var,slower_var);
	faster_var=b_faster_var;
	coeff_ln=b_coeff_ln;
	return true;
      }
    }
    // s!=0 && s!=plus_inf
    // size test used to get w or inv(w) at the front()
    if (a_faster_var.front().symb_size()>b_faster_var.front().symb_size()){
      coeff_ln=mergevecteur(b_coeff_ln,multvecteur(s,a_coeff_ln));
      faster_var=mergevecteur(b_faster_var,a_faster_var);
      slower_var=mergevecteur(b_slower_var,a_slower_var);
    }
    else {
      coeff_ln=mergevecteur(a_coeff_ln,multvecteur(inv(s,contextptr),b_coeff_ln));
      faster_var=mergevecteur(a_faster_var,b_faster_var);
      slower_var=mergevecteur(a_slower_var,b_slower_var);
    }
    return true;
  }
  
  // Find most rapidly varying subexpression of e and res
  static bool mrv(const gen & e,const identificateur & x,vecteur & faster_var,vecteur & coeff_ln, vecteur & slower_var,GIAC_CONTEXT){
    // Find all var of e depending on x
    vecteur v0(lvarx(e,x));
    // Find mrv of these vars
    vecteur::const_iterator it=v0.begin(),itend=v0.end();
    for (;it!=itend;++it){
      if (equalposcomp(faster_var,*it) || equalposcomp(slower_var,*it))
	continue;
      if (it->type!=_SYMB){
	if (!mrv_max(faster_var,coeff_ln,slower_var,
		     vecteur(1,*it),vecteur(1,plus_one),vecteur(0),
		     x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      gen temp=*it;
      if (temp._SYMBptr->sommet==at_ln){
	if (!mrv(temp._SYMBptr->feuille,x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      if (temp._SYMBptr->sommet==at_Psi && temp._SYMBptr->feuille.type==_VECT){
	if (!mrv(temp._SYMBptr->feuille[0],x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      if (temp._SYMBptr->sommet==at_pow)
	temp=symbolic(at_exp,(*(temp._SYMBptr->feuille._VECTptr))[1]*ln((*(temp._SYMBptr->feuille._VECTptr))[0],contextptr));
      if (temp._SYMBptr->sommet==at_euler_mac_laurin){
	gen & f = temp._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==5){
	  if (!mrv(f[0],x,faster_var,coeff_ln,slower_var,contextptr) ||
	      !mrv(f[3],x,faster_var,coeff_ln,slower_var,contextptr) ||
	      !mrv(f[4],x,faster_var,coeff_ln,slower_var,contextptr))
	    return false;
	  continue;
	}
      }
      if (temp._SYMBptr->feuille.type==_VECT){
	*logptr(contextptr) << gettext("Unable to handle ")+temp.print(contextptr) << endl;
	return false;
      }
      gen l=in_limit(temp._SYMBptr->feuille,x,plus_inf,0,contextptr);
      if (is_undef(l) || (l==unsigned_inf && temp._SYMBptr->sommet!=at_cos && temp._SYMBptr->sommet!=at_sin)){
	*logptr(contextptr) << gettext("Undef/Unsigned Inf encountered in limit") << endl;
	return false;
      }
      if (!is_inf(l)){
	if (!mrv(temp._SYMBptr->feuille,x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      if (temp._SYMBptr->sommet==at_exp){
	if (!mrv(temp._SYMBptr->feuille,x,faster_var,coeff_ln,slower_var,contextptr) ||
	    !mrv_max(faster_var,coeff_ln,slower_var,
		    vecteur(1,temp),vecteur(1,plus_one),vecteur(0),
		     x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      // (semi-)tractable functions?
      gen shift_coeff;
      if (!temp._SYMBptr->sommet.ptr()->series_expansion){
	invalidserieserr(string(gettext("no taylor method for "))+temp._SYMBptr->sommet.ptr()->print(contextptr));
	return false;
      }
      gen test=temp._SYMBptr->sommet.ptr()->series_expansion(l,0,temp._SYMBptr->sommet,0,shift_coeff,contextptr); // fixme: 0 should be image_of_direction 
      if (!is_undef(test)){
	if (!mrv(temp._SYMBptr->feuille,x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
	continue;
      }
      // fixme: add support for integrals and sums
      if (temp._SYMBptr->sommet!=at_exp)
	return false; // setsizeerr(gettext("series.cc/mrv"));
    }
    return true;
  }

  bool pnormal(sparse_poly1 & v,GIAC_CONTEXT){
    sparse_poly1::const_iterator it=v.begin(),itend=v.end();
    sparse_poly1 p;
    gen e;
    for (;it!=itend;++it){
      e=recursive_normal(it->coeff,contextptr);
      if (!is_zero(e))
	p.push_back(monome(e,it->exponent));
    }
    swap(p,v);
    return true;
  }

  // find asymptotic equivalent of e in terms of the mrv var of e
  static bool mrv_lead_term(const gen & e,const identificateur & x,gen & coeff, gen & mrv_var, gen & exponent,sparse_poly1 & q,int begin_ordre,GIAC_CONTEXT,bool series){
    if (!contains(e,x)){
      coeff=ratnormal(e);
      mrv_var=x;
      exponent=0;
      q.clear();
      q.push_back(monome(coeff,0));
      return true;
    }
    vecteur faster_var,coeff_ln,slower_var;
    gen ecopy(e);
    if (!mrv(ecopy,x,faster_var,coeff_ln,slower_var,contextptr))
      return false;
    int rescaling=0; // number of ln(x) -> x substitution
    // if the mrv contains x, we must change scale ln(x) -> x and x -> e^x
    for (;equalposcomp(faster_var,x);++rescaling){
      upscale(ecopy,x,contextptr);
      // next test needed to avoid infinite recursion
      // if there is an e^x inside the new mrv is m rescaled
      if (contains(ecopy,exp(x,contextptr))){
	gen temp(faster_var);
	upscale(temp,x,contextptr);
	faster_var=*temp._VECTptr;
      }
      else {
	faster_var.clear();
	slower_var.clear();
	coeff_ln.clear();
	if (!mrv(ecopy,x,faster_var,coeff_ln,slower_var,contextptr))
	  return false;
      }
    }
    if (faster_var.empty()){
      coeff=ratnormal(ecopy);
      mrv_var=x;
      exponent=0;
      q.clear();
      q.push_back(monome(coeff,0));
      return true;
    }
    // now find w, the mrv element -> 0 and express other elements of the mrv
    // algo: sort faster_var by symb_size
    // w is the shortest one, set g=ln(w)
    // replace the next shortest at position pos
    // by w^coeff_ln[pos]* exp(ln(faster_var[pos])-coeff_ln[pos]*g)
    // then replace faster_var[0] by w above
    // go on, the replace operation should be done with 
    // previous ordered_faster by their expression in terms of w
    // At the end replace w by 1/w if w -> plus_inf
    bool dont_invert=is_zero(in_limit(faster_var.front(),x,plus_inf,0,contextptr));
    vecteur faster_var_tmp(faster_var);
    stable_sort(faster_var.begin(),faster_var.end(),symb_size_less);
    identificateur w(" w");
    vecteur faster_var_subst(1,w);
    gen g=faster_var.front()._SYMBptr->feuille;
    iterateur it=faster_var.begin()+1,itend=faster_var.end();
    gen c,f;
    for (;it!=itend;++it){
      int p=equalposcomp(faster_var_tmp,*it);
      // assert(p);
      c=coeff_ln[p-1];
      f=subst(it->_SYMBptr->feuille,faster_var,faster_var_subst,false,contextptr);
      faster_var_subst.push_back(pow(w,c,contextptr)*exp(normal(f-c*g,contextptr),contextptr));
    }
    // subst in original expression and make the asymptotic expansion
    double ordre=begin_ordre;
    f=subst(ecopy,faster_var,faster_var_subst,false,contextptr);
    if (!dont_invert)
      f=subst(f,w,inv(w,contextptr),false,contextptr);
    if (faster_var.front().is_symb_of_sommet(at_exp)){
      // replace ln(exp(g)^k*...) by k*g+ln(...)
      vecteur lf(lop(f,at_ln)),lf1,lf2;
      iterateur it=lf.begin(),itend=lf.end();
      for (;it!=itend;++it){
	gen argln=it->_SYMBptr->feuille;
	sparse_poly1 p=series__SPOL1(argln,w,0,int(ordre),1,contextptr);
	if (!p.empty() && !is_undef(p.front().coeff) ){
	  if (!is_zero(p.front().exponent))
	    argln=argln*symbolic(at_pow,gen(makevecteur(w,-p.front().exponent),_SEQ__VECT));
	  lf1.push_back(*it);
	  lf2.push_back(p.front().exponent*(dont_invert?g:-g)+symbolic(at_ln,argln));
	}
      }
      if (!lf1.empty())
	f=subst(f,lf1,lf2,false,contextptr);
    }
    sparse_poly1 p;
    p.push_back(monome(undef,0));
    // FIXME: if ordre>max_series it might return a wrong answer here
    for (; (ordre<max_series_expansion_order) && !p.empty() && 
	   (p.size()>=1) && is_undef(p.front().coeff) ;ordre=ordre*1.5+1){
      bool inv=false;
      p=series__SPOL1(f,w,0,int(ordre),1,contextptr);
      if (!p.empty() && !is_undef(p.front().coeff) ){
	// substitution of ln(w) by +-g should not be useful anymore
	gen tmp=ratnormal(subst(p.front().coeff,ln(w,contextptr),(dont_invert?g:-g),false,contextptr));
	if (is_undef(tmp) ){
	  inv=true;
	  p=spdiv(sparse_poly1(1,monome(1,0)),p,contextptr);
	  if (is_undef(p))
	    return false;
	  pnormal(p,contextptr);
	}
      }
      // cerr << p << endl;
      // replace ln( w) in coeff by g or -g
      if (dont_invert)
	p=subst(p,ln(w,contextptr),g,false,contextptr);
      else
	p=subst(p,ln(w,contextptr),-g,false,contextptr);
      if (inv){
	p=spdiv(sparse_poly1(1,monome(1,0)),p,contextptr);
	if (is_undef(p))
	  return false;
	pnormal(p,contextptr);
      }
    }
    if (!p.empty())
      p.front().exponent=simplify(p.front().exponent,contextptr);
    q=p;
    if (!p.empty()){
      bool done=false;
      // check for exponent 0 at front()
      if (is_zero(p.front().exponent) && contains(p.front().coeff,x) && !is_zero(derive(p.front().coeff,x,contextptr))){
	// if p is uniquely composed of this coeff, expand
	if (!mrv_lead_term(p.front().coeff,x,coeff,mrv_var,exponent,p,mrv_begin_order,contextptr,false))
	  return false;
	if (p.empty()
	    // test below allow expanding series(ln(x+1),x=inf)
	    // the boolean series was added 
	    // otherwise testlimit would not work correctly
	    || (series && p.size()==1 && q.size()>1 &&!is_undef(p.front().coeff)) 
	    ){
	  done=false;
	  p=q;
	}
	else {
	  if (q.size()>1 && !is_undef(p.back().coeff))
	    p.push_back(monome(undef,p.back().exponent+begin_ordre));
	  q=p;
	  done=true;
	}
      }
      if (!done) {
	if (dont_invert)
	  mrv_var=faster_var.front();
	else
	  mrv_var=inv(faster_var.front(),contextptr);
	coeff = p.front().coeff;
	exponent = p.front().exponent;
      }
    }
    // mrv_var is w as function of x, rescaled using the int variable rescaling
    // coeff must be rescaled as well
    sparse_poly1::iterator it0=q.begin(),it1=q.end();
    for (;rescaling;--rescaling){
      downscale(mrv_var,x,contextptr);
      downscale(coeff,x,contextptr);
      for (sparse_poly1::iterator it=it0;it!=it1;++it)
	downscale(it->coeff,x,contextptr);
    }
    return true;
  }

  bool intersect(const vecteur & a,const vecteur &b,int & pos_a,int & pos_b){
    vecteur res;
    if (a.empty() || b.empty())
      return false;
    vecteur::const_iterator it=a.begin(),itend=a.end();
    for (;it!=itend;++it)
      pos_b=equalposcomp(b,*it);
      if (pos_b){
	--pos_b;
	pos_a=it-a.begin();
	return true;
      }
    return false;
  }

  static int convert_to_direction(const gen & l){
    if (is_one(l))
      return 1;
    if (is_minus_one(l))
      return -1;
    if (is_zero(l))
      return 0;
    return -2;
  }

  // Main limit entry point
  gen limit(const gen & e,const identificateur & x,const gen & lim_point,int direction,GIAC_CONTEXT){
    // Insert here code for cleaning limit remember
    // int save_inside_limit=inside_limit(contextptr);
    // inside_limit(1,contextptr);
    // sincosinf.clear();
    gen l=in_limit(e,x,lim_point,direction,contextptr);
    // inside_limit(save_inside_limit,contextptr);
    // vecteur sincosinfsub(sincosinf.size(),undef);
    // l=eval(subst(l,sincosinf,sincosinfsub));
    return l;
  }

  // "unary" version
  static const char _limit_s []="limit";
  gen _limit(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return limit(args,*vx_var._IDNTptr,0,0,contextptr);
    int s=args._VECTptr->size();
    if (!s)
      toofewargs(_limit_s);
    gen G=(*(args._VECTptr))[0];
    if (s==1)
      return limit( G,*vx_var._IDNTptr,0,0,contextptr);
    gen e=(*(args._VECTptr))[1];
    if (s==2){
      if (calc_mode(contextptr)==1)
	return _limit(makesequence(G,ggb_var(G),e),contextptr);
      if (e.type==_IDNT)
	return limit(G,*e._IDNTptr,0,0,contextptr);
      if (e.type!=_SYMB)
	return gentypeerr(contextptr);
      if (e._SYMBptr->sommet!=at_equal)
	return gensizeerr(contextptr);
      gen x=(*(e._SYMBptr->feuille._VECTptr))[0];
      if (x.type!=_IDNT)
	return gensizeerr(contextptr);
      return limit(G,*x._IDNTptr,(*(e._SYMBptr->feuille._VECTptr))[1],0,contextptr);
    }
    if (s==3){
      gen arg3=(*(args._VECTptr))[2];
      if (e.type==_IDNT)
	return limit(G,*e._IDNTptr,arg3,0,contextptr);
      if (e.type!=_SYMB){
	if (is_one(arg3)||is_minus_one(arg3))
	  return limit(G,*ggb_var(G)._IDNTptr,e,int(evalf_double(arg3,1,contextptr)._DOUBLE_val),contextptr);
	return gentypeerr(contextptr);
      }
      if (e._SYMBptr->sommet!=at_equal){
	if (is_one(arg3)||is_minus_one(arg3))
	  return limit(G,*ggb_var(G)._IDNTptr,e,int(evalf_double(arg3,1,contextptr)._DOUBLE_val),contextptr);
	return gensizeerr(contextptr);
      }
      gen x=(*(e._SYMBptr->feuille._VECTptr))[0];
      if (x.type!=_IDNT)
	return gensizeerr(contextptr);
      return limit(G,*x._IDNTptr,(*(e._SYMBptr->feuille._VECTptr))[1],convert_to_direction((*(args._VECTptr))[2]),contextptr);
    }
    if (s>4)
      return gentoomanyargs(_limit_s);
    if (e.type!=_IDNT)
      return gentypeerr(contextptr);
    return limit(G,*e._IDNTptr,(*(args._VECTptr))[2],convert_to_direction((*(args._VECTptr))[3]),contextptr);
  }
  static string texprintaslimit(const gen & g,const char * orig_s,GIAC_CONTEXT){
    string s("\\lim ");
    if (g.type!=_VECT)
      return s+gen2tex(g,contextptr);
    vecteur v(*g._VECTptr);
    int l(v.size());
    if (!l)
      return s;
    if (l==1)
      return s+gen2tex(v[0],contextptr);
    if (l==2)
      return s+"_{"+gen2tex(v[1],contextptr)+"}"+gen2tex(v[0],contextptr);
    // directional limit
    if (l==3){
      if (is_one(v[2]))
	return s+"_{"+gen2tex(v[1],contextptr)+"^+}"+gen2tex(v[0],contextptr);
      if (is_minus_one(v[2]))
	return s+"_{"+gen2tex(v[1],contextptr)+"^-}"+gen2tex(v[0],contextptr);
      else return s+"_{"+gen2tex(v[1],contextptr)+"}"+gen2tex(v[0],contextptr);
    }
    return s;
  }
  static define_unary_function_eval4 (__limit,&_limit,_limit_s,0,&texprintaslimit);
  define_unary_function_ptr5( at_limit ,alias_at_limit,&__limit,0,true);

  // like sparse_poly12gen, but if there is only 1 term and no remainder
  // expand it, l.1976
  static gen sparse_poly12gen_expand(const sparse_poly1 & s,const identificateur & x,const gen & mrv_var,int ordre,gen & remains,bool with_order_size,GIAC_CONTEXT){
    if (s.size()!=1 || !contains(s.front().coeff,x) )
      return sparse_poly12gen(s,mrv_var,remains,with_order_size);
    gen afaire=s.front().coeff,mrv_fait(mrv_var),a,b,c,exponent(s.front().exponent);
    if (mrv_fait.is_symb_of_sommet(at_inv) && mrv_fait._SYMBptr->feuille.is_symb_of_sommet(at_exp))
      mrv_fait=symbolic(at_exp,symbolic(at_neg,mrv_fait._SYMBptr->feuille._SYMBptr->feuille));
    if (mrv_fait.is_symb_of_sommet(at_exp)){
      // search embedded ln if mrv_var is an exp var
      mrv_fait=mrv_fait._SYMBptr->feuille;
      vecteur l(lop(mrv_fait,at_ln));
      int ls=l.size();
      for (int i=0;i<ls;++i){
	identificateur tx(" x");
	gen tmpx(tx);
	gen mrv_temp=subst(mrv_fait,l[i],tmpx,true,contextptr);
	if (is_linear_wrt(mrv_temp,tmpx,a,b,contextptr)){
	  // extract constant part of a in c using decompose_plus
	  if (a.is_symb_of_sommet(at_plus) && a._SYMBptr->feuille.type==_VECT){
	    c=0;
	    vecteur non_constant;
	    decompose_plus(*a._SYMBptr->feuille._VECTptr,x,non_constant,c,contextptr);
	    a=_plus(non_constant,contextptr);
	    afaire=afaire*pow(l[i]._SYMBptr->feuille,c*exponent,contextptr);
	    mrv_fait=a*l[i]+b;
	  }
	}
      }
      mrv_fait=symbolic(at_exp,mrv_fait);
    }
    gen coeff2,mrv_var2,exponent2;
    sparse_poly1 s2;
    if (!mrv_lead_term(afaire,x,coeff2,mrv_var2,exponent2,s2,ordre,contextptr,true))
      return false;
    return sparse_poly12gen_expand(s2,x,mrv_var2,ordre,remains,with_order_size,contextptr)*pow(mrv_fait,exponent,contextptr);
  }

  static gen in_series(const gen & e0,const identificateur & x,const gen & lim_point,int ordre,int direction,GIAC_CONTEXT){
    gen e=limit_symbolic_preprocess(e0,x,lim_point,direction,contextptr);
    if (is_undef(e)) return e;
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;    
    if (lim_point==plus_inf){
      gen coeff,mrv_var,exponent,remains;
      sparse_poly1 s;
      if (!mrv_lead_term(e,x,coeff,mrv_var,exponent,s,ordre,contextptr,true))
	return gensizeerr(contextptr);
      return sparse_poly12gen_expand(s,x,mrv_var,ordre,remains,true,contextptr);
    }
    if (lim_point==minus_inf){
      gen coeff,mrv_var,exponent,remains;
      sparse_poly1 s;
      if (!mrv_lead_term(subst(e,x,-x,false,contextptr),x,coeff,mrv_var,exponent,s,ordre,contextptr,true))
	return gensizeerr(contextptr);
      return subst(sparse_poly12gen_expand(s,x,mrv_var,ordre,remains,true,contextptr),x,-x,false,contextptr);
    }
    if (direction==1){
      gen ecopy=subst(e,x,lim_point+inv(x,contextptr),false,contextptr);
      gen coeff,mrv_var,exponent,remains;
      sparse_poly1 s;
      if (!mrv_lead_term(ecopy,x,coeff,mrv_var,exponent,s,ordre,contextptr,true))
	return gensizeerr(contextptr);
      return subst(sparse_poly12gen_expand(s,x,mrv_var,ordre,remains,true,contextptr),x,inv(x-lim_point,contextptr),false,contextptr);
    }
    if (direction==-1){
      gen ecopy=subst(e,x,lim_point-inv(x,contextptr),false,contextptr);
      gen coeff,mrv_var,exponent,remains;
      sparse_poly1 s;
      if (!mrv_lead_term(ecopy,x,coeff,mrv_var,exponent,s,ordre,contextptr,true))
	return gensizeerr(contextptr);
      return subst(sparse_poly12gen_expand(s,x,mrv_var,ordre,remains,true,contextptr),x,inv(lim_point-x,contextptr),false,contextptr);
    }
    gen remains;
    switch (e.type){
    case _INT_: case _ZINT: case _DOUBLE_: case _CPLX:
      return e;
    case _IDNT:
      if ( !ordre && (*e._IDNTptr==x) )
	return lim_point+(x-lim_point)*order_size(x-lim_point,contextptr);
      else
	return e;
    case _SYMB:
      return sparse_poly12gen(ck_series__SPOL1(e,x,lim_point,ordre,direction,contextptr),x-lim_point,remains,true);
    default:
      return symbolic(at_series,makesequence(e,x,lim_point,ordre));
    }
  }

  // Main series entry point
  gen series(const gen & e,const identificateur & x,const gen & lim_point,int ordre,int direction,GIAC_CONTEXT){
    if (e.type==_VECT){
      vecteur res = *e._VECTptr;
      int l=res.size();
      for (int i=0;i<l;++i){
	res[i]=in_series(_pow2exp(tan2sincos(res[i],contextptr),contextptr),x,lim_point,ordre,direction,contextptr);
      }
      return res;
    }
    gen res=in_series(_pow2exp(tan2sincos(e,contextptr),contextptr),x,lim_point,ordre,direction,contextptr);
    return res;
  }

  gen series(const gen & e,const gen & vars,const gen & lim_point,int ordre,int direction,GIAC_CONTEXT){
    gen x,l;
    if (vars.is_symb_of_sommet(at_equal)){ 
      // vars= x==lim_point, overwrites lim_point definition
      // direction is given by lim_point (for interactive input)
      x = (*(vars._SYMBptr->feuille._VECTptr)) [0];
      l = (*(vars._SYMBptr->feuille._VECTptr)) [1];
      if (lim_point.type!=_INT_)
	return gensizeerr(contextptr);
      if (absint(lim_point.val)>0){
	if (!direction && absint(ordre)<2)
	  direction=ordre;
	ordre=absint(lim_point.val);
      }
      else
	direction = lim_point.val;
    }
    else {
      x=vars;
      l=lim_point;
    }
    if (x.type==_VECT && l.type==_VECT){
      vecteur &v=*x._VECTptr;
      gen h(identificateur(" h"));
      vecteur w=addvecteur(*l._VECTptr,multvecteur(h,subvecteur(v,*l._VECTptr)));
      gen newe=subst(e,v,w,false,contextptr);
      sparse_poly1 res=series__SPOL1(newe,*h._IDNTptr,zero,ordre,direction,contextptr);
      poly_truncate(res,ordre,contextptr);
      if (!res.empty() && is_undef(res.back().coeff))
	res.pop_back();
      // order term has been removed
      gen remains;
      return sparse_poly12gen(res,1,remains,false);
    }
    if (x.type!=_IDNT){
      identificateur xx("x");
      gen res=series(subst(e,x,xx,false,contextptr),xx,l,ordre,direction,contextptr);
      return subst(res,xx,x,false,contextptr);
    }
    return series(e,*x._IDNTptr,l,ordre,direction,contextptr);
  }

  gen series(const gen & e,const gen & vars,const gen & lim_point,const gen & ordre0,GIAC_CONTEXT){
    gen ordre(ordre0);
    if (!is_integral(ordre))
      return gensizeerr(contextptr);
    return series(e,vars,lim_point,ordre.val,0,contextptr); // it's the direction
  }

  // "unary" version
  static const char _series_s []="series";
  gen _series(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return series(args,vx_var,0,5,0,contextptr);
    vecteur v=*args._VECTptr;
    v[0]=Heavisidetosign(when2sign(piecewise2when(v[0],contextptr),contextptr),contextptr);
    int s=v.size();
    if (!s)
      toofewargs(_series_s);
    if (s==1)
      return series( v[0],vx_var,0,5,0,contextptr);
    if (s==2)
      return series( v[0],v[1],0,5,contextptr);
    if (s==3){
      if ( (v[1].type==_VECT && v[2].type==_VECT) ||
	   ( v[1].type==_IDNT || ( v[1].type==_SYMB && (v[1]._SYMBptr->sommet==at_equal || v[1]._SYMBptr->sommet==at_at ) ) )
	   )
	return series( v[0],v[1],v[2],5,contextptr);
      return series( v[0],symbolic(at_equal,makesequence(vx_var,v[1])),v[2],5,contextptr);
    }
    if (s==4)
      return series( v[0],v[1],v[2],v[3],contextptr);    
    if (s>5 || v[3].type!=_INT_ || v[4].type!=_INT_)
      return gentoomanyargs(_series_s);
    return series(v[0],v[1],v[2],v[3].val,v[4].val,contextptr);
  }
  static define_unary_function_eval (__series,&_series,_series_s);
  define_unary_function_ptr5( at_series ,alias_at_series,&__series,0,true);

  // "unary" version
  static const char _revert_s []="revert";
  gen _revert(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=gen2vecteur(args);
    if (v.empty())
      return gensizeerr(contextptr);
    gen g=v[0],x;
    if (v.size()==1)
      x=vx_var;
    else
      x=v[1];
    if (x.type!=_IDNT){
      identificateur idx(" trevert");
      return _revert(subst(args,x,idx,false,contextptr),contextptr);
    }
    // find ordre
    int ordre=series_default_order;
    if (v.size()>2 && v[2].type==_INT_)
      ordre=v[2].val;
    vecteur w=lop(g,at_order_size);
    if (w.size()==1){
      gen xn=derive(g,w.front(),contextptr);
      if (is_undef(xn)) return xn;
      if (xn.is_symb_of_sommet(at_pow)){
	gen & f=xn._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->back().type==_INT_){
	  ordre=f._VECTptr->back().val;
	  w.clear();
	  g=subst(g,w.front(),0,false,contextptr);
	} 
      }
    }
    if (!w.empty())
      return gensizeerr(contextptr);
    sparse_poly1 p=series__SPOL1(g,*x._IDNTptr,zero,ordre,0,contextptr),res;
    if (!prevert(p,res,contextptr))
      return gensizeerr(contextptr);
    gen remains;
    return sparse_poly12gen(res,x,remains,false);
  }
  static define_unary_function_eval (__revert,&_revert,_revert_s);
  define_unary_function_ptr5( at_revert ,alias_at_revert,&__revert,0,true);

  static const char _bounded_function_s []="bounded_function";
  gen _bounded_function(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_bounded_function,args);
  }
  gen bounded_function(GIAC_CONTEXT){
    int i=bounded_function_no(contextptr);
    ++i;
    bounded_function_no(i,contextptr);
    return symbolic(at_bounded_function,i);
  }
  static define_unary_function_eval (__bounded_function,&_bounded_function,_bounded_function_s);
  define_unary_function_ptr5( at_bounded_function ,alias_at_bounded_function,&__bounded_function,0,true);

  // internal function, used to replace sum for limit/series
  // args = expression, antiderivative, variable, lower_bound, upper_bound
  static const char _euler_mac_laurin_s []="euler_mac_laurin";
  gen _euler_mac_laurin(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_euler_mac_laurin,args);
  }
  static define_unary_function_eval (__euler_mac_laurin,&_euler_mac_laurin,_euler_mac_laurin_s);
  define_unary_function_ptr5( at_euler_mac_laurin ,alias_at_euler_mac_laurin,&__euler_mac_laurin,0,true);

  bool convert_to_euler_mac_laurin(const gen & g,gen & res,GIAC_CONTEXT){
    if (g.is_symb_of_sommet(at_sum)){
      gen & f = g._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=4)
	return false;
      // check that the expression to be summed has a derivative
      // which is a o(expression) as x -> inf
      gen & f0=f._VECTptr->front();
      gen x =f[1];
      if (x.type!=_IDNT){
	*logptr(contextptr) << gettext("Unable to convert to euler mac laurin");
	return false;
      }
      gen f0prime=derive(f0,x,contextptr);
      if (is_undef(f0prime)) return false;
      gen l=in_limit(f0prime/f0,*x._IDNTptr,plus_inf,1,contextptr);
      if (!is_zero(l))
	return false;
      gen remains;
      gen F0=integrate_gen_rem(f0,x,remains,contextptr);
      if (!is_zero(remains) || is_undef(F0))
	return false;
      res=symbolic(at_euler_mac_laurin,gen(makevecteur(f0,F0,x,f[2],f[3]),_SEQ__VECT));
      return true;
    }
    vecteur v=lop(g,at_sum);
    vecteur w=v;
    int s=v.size();
    for (int i=0;i<s;++i){
      if (!convert_to_euler_mac_laurin(v[i],w[i],contextptr))
	return false;
    }
    res=subst(g,v,w,false,contextptr);
    return true;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

