// -*- mode:C++ ; compile-command: "g++ -I. -I.. -I../include -g -c sparse.cc -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DHAVE_CONFIG_H -DIN_GIAC" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,14 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include <cmath>
#include <stdexcept>
#include <map>
#include <iostream>
#include "gen.h"
#include "sparse.h"
#include "vecteur.h"
#include "modpoly.h"
#include "unary.h"
#include "symbolic.h"
#include "usual.h"
#include "sym2poly.h"
#include "solve.h"
#include "prog.h"
#include "subst.h"
#include "permu.h"
#include "plot.h"
#include "misc.h"
#include "ti89.h"
#include "csturm.h"
#include "giacintl.h"
#ifdef HAVE_LIBGSL
#include <gsl/gsl_linalg.h>
#include <gsl/gsl_eigen.h>
#include <gsl/gsl_poly.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  void sparse_add(const gen_map & a,const gen_map & b,gen_map & c){
    c.clear();
    comparegen key;
    gen_map::const_iterator it=a.begin(),itend=a.end(),jt=b.begin(),jtend=b.end();
    for (;it!=itend && jt!=jtend;){
      if (it->first==jt->first){
	gen tmp=it->second+jt->second;
	if (!is_zero(tmp)){
	  c[it->first]=tmp;
	}
	++it;
	++jt;
	continue;
      }
      if (key(it->first,jt->first)){
	c[it->first]=it->second;
	++it;
	continue;
      }
      c[jt->first]=jt->second;
      ++jt;
    }
    for (;it!=itend;++it){
      c[it->first]=it->second;
    }
    for (;jt!=jtend;++jt){
      c[jt->first]=jt->second;
    }
  }

  void sparse_sub(const gen_map & a,const gen_map & b,gen_map & c){
    c.clear();
    comparegen key;
    gen_map::const_iterator it=a.begin(),itend=a.end(),jt=b.begin(),jtend=b.end();
    for (;it!=itend && jt!=jtend;){
      if (it->first==jt->first){
	gen tmp=it->second-jt->second;
	if (!is_zero(tmp)){
	  c[it->first]=tmp;
	}
	++it;
	++jt;
	continue;
      }
      if (key(it->first,jt->first)){
	c[it->first]=it->second;
	++it;
	continue;
      }
      c[jt->first]=-jt->second;
      ++jt;
    }
    for (;it!=itend;++it){
      c[it->first]=it->second;
    }
    for (;jt!=jtend;++jt){
      c[jt->first]=-jt->second;
    }
  }

  void sparse_mult(const gen & x,gen_map & c){
    if (is_zero(x)){
      c.clear();
      return;
    }
    gen_map::iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      it->second = x*it->second;
    }
  }

  void sparse_div(gen_map & c,const gen & x){
    if (is_inf(x)){
      c.clear();
      return;
    }
    gen_map::iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      it->second = it->second/x;
    }
  }

  void sparse_neg(gen_map & c){
    gen_map::iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      it->second = -it->second;
    }
  }

  struct sparse_line_begin_t {
    longlong i;
    gen_map::const_iterator begin,end;
    sparse_line_begin_t(longlong i_,gen_map::const_iterator it_,gen_map::const_iterator jt_):i(i_),begin(it_),end(jt_){};
    sparse_line_begin_t(){};
  };

  bool operator < (const sparse_line_begin_t & a,const sparse_line_begin_t & b){
    return a.i<b.i;
  }

  bool dicho(const std::vector<sparse_line_begin_t> & B,longlong pos,gen_map::const_iterator & begin,gen_map::const_iterator & end){
    if (B.empty())
      return false;
    longlong a=0,b=B.size()-1,c; // we are between a and b
    if (pos<B[0].i || pos>B[b].i)
      return false;
    for (;a<b-1;){
      c=(a+b)/2;
      if (pos<B[c].i)
	b=c;
      else
	a=c;
    }
    // a>=b-1 hence a==b-1 or a==b
    if (pos==B[a].i){
      begin=B[a].begin;
      end=B[a].end;
      for (;begin!=end;++begin){
	if (begin->second!=0)
	  break;
      }
      return true;
    }
    if (pos==B[b].i){
      begin=B[b].begin;
      end=B[b].end;
      for (;begin!=end;++begin){
	if (begin->second!=0)
	  break;
      }
      return true;
    }
    return false;
  }

  void sparse_trim(const gen_map & d,gen_map &c){
    gen_map::const_iterator it=d.begin(),itend=d.end();
    for (;it!=itend;++it){
      if (!is_zero(it->second))
	c[it->first]=it->second;
    }
  }

  bool need_sparse_trim(const gen_map & d){
    gen_map::const_iterator it=d.begin(),itend=d.end();
    for (;it!=itend;++it){
      if (is_zero(it->second))
	return true;
    }
    return false;
  }

  void find_line_begin(gen_map::const_iterator jt,gen_map::const_iterator jtend,std::vector<sparse_line_begin_t> & B){
    B.clear();
    gen_map::const_iterator begin;
    longlong prev=-1;
    for (;jt!=jtend;++jt){
      longlong cur=jt->first._VECTptr->front().val;
      if (cur==prev)
	continue;
      if (prev!=-1 && begin!=jtend)
	B.push_back(sparse_line_begin_t(prev,begin,jt));
      prev=cur;
      begin=jt;
    }
    if (begin!=jtend)
      B.push_back(sparse_line_begin_t(prev,begin,jt));
  }

  bool sparse_mult(const gen_map & a,const gen_map & b,gen_map & c){
    c.clear();
    if (a.empty() || b.empty())
      return true;
    gen_map::const_iterator it=a.begin(),itend=a.end(),jt=b.begin(),jtend=b.end(),begin,end;
    if (jt->first.type==_INT_){
      gen_map d;
      // matrix * std::vector
      for (;it!=itend;++it){
	jt=b.find(it->first._VECTptr->back());
	if (jt!=jtend)
	  d[it->first._VECTptr->front()] += it->second*jt->second;
      }
      sparse_trim(d,c);
      return true;
    }
    if (it->first.type==_INT_){
      gen_map d;
      std::vector<sparse_line_begin_t> B;
      // compute positions of line begin of b in B
      find_line_begin(jt,jtend,B);
      for (;it!=itend;++it){
	if (!dicho(B,it->first.val,begin,end))
	  continue;
	gen coeffa=it->second;
	for (;begin!=end;++begin){
	  d[begin->first._VECTptr->back()] += coeffa*begin->second;
	}
      }
      sparse_trim(d,c);
      return true;
    }
    if (1){
      smatrix B;
      if (!convert(b,B))
	return false;
      int C=B.ncols();
      int previ=-1; 
      vecteur cur(C);
      for (;;){
	int i=-2,k=-1; 
	gen coeffa;
	if (it!=itend){
	  i=it->first._VECTptr->front().val;
	  k=it->first._VECTptr->back().val;
	  coeffa=it->second;
	  ++it;
	}
	if (i!=previ){
	  if (previ>=0){
	    // copy cur in c
	    vecteur pos(2,previ);
	    for (int j=0;j<C;++j){
	      gen & cj=cur[j];
	      if (is_zero(cj))
		continue;
	      pos[1]=j;
	      c[gen(pos,_SEQ__VECT)]=cj;
	      cj=0;
	    }
	  }
	  if (i==-2 && it==itend) break;
	  previ=i;
	}
	if (k>=B.m.size())
	  continue;
	// a_[i,k]*B[k,j]
	const_iterateur bt=B.m[k]._VECTptr->begin(),btend=B.m[k]._VECTptr->end();
	std::vector<int>::const_iterator bpos=B.pos[k].begin();
	for (;bt!=btend;++bpos,++bt){
	  cur[*bpos] += coeffa*(*bt);
	}
      } // end for
    }
    else {
      gen_map d;
      std::vector<sparse_line_begin_t> B;
      // compute positions of line begin of b in B
      find_line_begin(jt,jtend,B);
      // sort(B.begin(),B.end());
      vecteur pos(2);
      for (;it!=itend;++it){
	pos[0]=it->first._VECTptr->front();
	longlong cur=it->first._VECTptr->back().val;
	gen coeffa=it->second;
	// dichotomic search in B 
	if (!dicho(B,cur,begin,end))
	  continue;
	for (;begin!=end;++begin){
	  pos[1]=begin->first._VECTptr->back();
	  d[gen(pos,_SEQ__VECT)] += coeffa*begin->second;
	}
      }
      sparse_trim(d,c);
    }
    return true;
  }

  bool sparse_mult(const gen_map & a,const vecteur & b,gen_map & c){
    c.clear();
    if (a.empty() || b.empty())
      return true;
    gen_map d;
    gen_map::const_iterator it=a.begin(),itend=a.end();
    vecteur pos(2);
    for (;it!=itend;++it){
      gen i=it->first._VECTptr->front();
      int j=it->first._VECTptr->back().val;
      gen coeffa=it->second;
      if (j>=b.size())
	return false;
      gen bj=b[j];
      if (bj.type!=_VECT)
	d[i] += coeffa*bj;
      else {
	pos[0]=i;
	const_iterateur jt=bj._VECTptr->begin(),jtend=bj._VECTptr->end();
	for (int k=0;jt!=jtend;++jt,++k){
	  pos[1]=k;
	  d[gen(pos,_SEQ__VECT)] += coeffa*(*jt);
	}
      }
    }    
    sparse_trim(d,c);
    return true;
  }

  void sparse_mult(const smatrix & a,const vecteur & b,vecteur & c){
    c.clear();
    int l=a.size();
    c.reserve(l);
    for (int i=0;i<l;++i){
      gen tmp=0;
      const_iterateur at=a.m[i]._VECTptr->begin(),atend=a.m[i]._VECTptr->end();
      std::vector<int>::const_iterator apos=a.pos[i].begin();
      for (;at!=atend;++apos,++at){
	tmp += (*at)*b[*apos];
      }
      c.push_back(tmp);
    }
  }

  void sparse_mult(const fmatrix & a,const std::vector<giac_double> & b,std::vector<giac_double> & c){
    c.clear();
    int l=a.size();
    c.reserve(l);
    for (int i=0;i<l;++i){
      double tmp=0;
      std::vector<double>::const_iterator at=a.m[i].begin(),atend=a.m[i].end();
      std::vector<int>::const_iterator apos=a.pos[i].begin();
      for (;at!=atend;++apos,++at){
	tmp += (*at)*b[*apos];
      }
      c.push_back(tmp);
    }
  }

  void sparse_mult(const vecteur & v,const smatrix & m,vecteur & c){
    c.clear();
    int l=m.size();
    c.resize(l);
    for (int i=0;i<l;++i){
      gen coeff=v[i];
      const_iterateur mt=m.m[i]._VECTptr->begin(),mtend=m.m[i]._VECTptr->end();
      std::vector<int>::const_iterator mpos=m.pos[i].begin();
      for (;mt!=mtend;++mpos,++mt){
	c[*mpos] += coeff*(*mt);
      }
    }
  }

  void sparse_mult(const std::vector<double> & v,const fmatrix & m,std::vector<double> & c){
    c.clear();
    int l=m.size();
    c.resize(l);
    for (int i=0;i<l;++i){
      double coeff=v[i];
      std::vector<double>::const_iterator mt=m.m[i].begin(),mtend=m.m[i].end();
      std::vector<int>::const_iterator mpos=m.pos[i].begin();
      for (;mt!=mtend;++mpos,++mt){
	c[*mpos] += coeff*(*mt);
      }
    }
  }

  bool sparse_mult(const vecteur & a,const gen_map & b,gen_map & c){
    c.clear();
    if (a.empty() || b.empty())
      return true;
    bool mat=ckmatrix(a); vecteur a_;
    if (mat)
      a_=mtran(a);
    gen_map d;
    gen_map::const_iterator it=b.begin(),itend=b.end();
    vecteur pos(2);
    for (;it!=itend;++it){
      int j=it->first._VECTptr->front().val;
      gen k=it->first._VECTptr->back();
      gen coeffa=it->second;
      if (!mat){
	if (j>=a.size())
	  return false;
	d[k] += a[j]*coeffa;
      }
      else {
	if (j>=a_.size())
	  return false;
	pos[1]=k;
	const_iterateur jt=a_[j]._VECTptr->begin(),jtend=a_[j]._VECTptr->end();
	for (int i=0;jt!=jtend;++jt,++i){
	  pos[0]=i;
	  d[gen(pos,_SEQ__VECT)] += (*jt)*coeffa;
	}
      }
    }    
    sparse_trim(d,c);
    return true;
  }

  // transpose or transconjugate
  void sparse_trn(const gen_map & c,gen_map & t,bool trn,GIAC_CONTEXT){
    t.clear();
    gen_map::const_iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      gen i=it->first;
      if (i.type==_INT_)
	i=makesequence(0,i);
      else
	i=makesequence(i._VECTptr->back(),i._VECTptr->front());
      t[i]= trn?conj(it->second,contextptr):it->second;
    }    
  }

  void map_apply(const gen_map & c,gen_map & t,GIAC_CONTEXT,gen (* f) (const gen &,GIAC_CONTEXT) ){
    t.clear();
    gen_map::const_iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      gen g=f(it->second,contextptr);
      if (!is_zero(g))
	t[it->first]=g;
    }
  }

  void map_apply(const gen_map & c,const unary_function_ptr & f,gen_map & t,GIAC_CONTEXT){
    t.clear();
    gen_map::const_iterator it=c.begin(),itend=c.end();
    for (;it!=itend;++it){
      gen g=f(it->second,contextptr);
      if (!is_zero(g))
	t[it->first]=g;
    }
  }

  bool sparse_swaprows(gen_map & u,std::vector<sparse_line_begin_t>& B,int r1,int r2,int c=-1){
    if (r1>r2)
      return sparse_swaprows(u,B,r2,r1,c);
    if (r1==r2)
      return true;
    gen_map::const_iterator r1b0,r1b,r1e,r2b0,r2b,r2e;
    if (!dicho(B,r1,r1b0,r1e) || !dicho(B,r2,r2b0,r2e))
      return false;
    vecteur c1;
    for (r1b=r1b0;r1b!=r1e;++r1b){
      gen & C=r1b->first._VECTptr->back();
      if (c>=0 && C.val>c){
	r1e=r1b;
	break;
      }
      c1.push_back(C);
      c1.push_back(r1b->second);
    }
    vecteur c2;
    for (r2b=r2b0;r2b!=r2e;++r2b){
      gen & C=r2b->first._VECTptr->back();
      if (c>=0 && C.val>c){
	r2e=r2b;
	break;
      }
      c2.push_back(C);
      c2.push_back(r2b->second);
    }
    u.erase(*(gen_map::iterator *) &r1b0, *(gen_map::iterator *) &r1e);
    u.erase(*(gen_map::iterator *) &r2b0, *(gen_map::iterator *) &r2e);
    for (int i=0;i<int(c2.size());i+=2){
      u[makesequence(r1,c2[i])]=c2[i+1];
    }
    for (int i=0;i<int(c1.size());i+=2){
      u[makesequence(r2,c1[i])]=c1[i+1];
    }
    return true;
  }

  void sparse_rowadd(gen_map & u,int r,int col,gen_map::const_iterator it,gen_map::const_iterator itend,const gen & coeff,gen_map::const_iterator jt,gen_map::const_iterator jtend){
    vecteur c;
    gen_map::const_iterator itsave=it;
    for (;it!=itend && jt!=jtend;){
      int ic=it->first._VECTptr->back().val,jc=jt->first._VECTptr->back().val;
      if (ic<col){
	++it;
	continue;
      }
      if (jc<col){
	++it;
	continue;
      }
      if (ic==jc){
	gen tmp=it->second+coeff*jt->second;
	c.push_back(ic);
	c.push_back(tmp);
	++it;
	++jt;
	continue;
      }
      if (ic<jc){
	c.push_back(ic);
	c.push_back(it->second);
	++it;
	continue;
      }
      c.push_back(jc);
      c.push_back(coeff*jt->second);
      ++jt;
    }
    for (;it!=itend;++it){
      int ic=it->first._VECTptr->back().val;
      if (ic<col)
	continue;
      c.push_back(ic);
      c.push_back(it->second);
    }
    for (;jt!=jtend;++jt){
      int jc=jt->first._VECTptr->back().val;
      if (jc<col)
	continue;
      c.push_back(jc);
      c.push_back(coeff*jt->second);
    }
    // copy c in the map
    // should erase 0
    for (unsigned i=0;i<c.size();i+=2){
      u[makesequence(r,c[i])]=c[i+1];
    }
  }

  bool sparse_lu(const gen_map & a,std::vector<int> & p,gen_map & l,gen_map & u_){
    int L,C,n;
    if (!is_sparse_matrix(a,L,C,n))
      return false;
    l.clear(); 
    gen_map u(a);
    p=std::vector<int>(L);
    for (int i=0;i<L;++i)
      p[i]=i;
    int r=0,c=0;
    for (;r<L && c<C;){
      std::vector<sparse_line_begin_t> B;
      find_line_begin(u.begin(),u.end(),B);
      // search pivot in column c starting at line l
      gen_map::const_iterator begin,end;
      int lp=r,pivotline=-1;
      gen pivot=0;
      for (;lp<L;++lp){
	if (!dicho(B,lp,begin,end))
	  continue;
	for (;begin!=end;++begin){
	  if (begin->first._VECTptr->back()==c){
	    gen temp=begin->second;
	    if (temp.islesscomplexthan(pivot)){
	      pivot=temp;
	      pivotline=lp;
	    }
	    break;
	  }
	  if (begin->first._VECTptr->back()>c)
	    break;
	}
      }
      if (pivotline==-1){
	++c;
	continue;
      }
      // exchange rows
      if (pivotline!=r){
	sparse_swaprows(u,B,r,pivotline);
	find_line_begin(l.begin(),l.end(),B);
	sparse_swaprows(l,B,r,pivotline,c);
	find_line_begin(u.begin(),u.end(),B);
	swapint(p[r],p[pivotline]);
      }
      // reduce rows and fill l
      l[makesequence(r,c)]=1;
      gen_map::const_iterator begin2,end2;
      if (!dicho(B,r,begin2,end2))
	return false;
      for (int R=r+1;R<L;++R){
	if (!dicho(B,R,begin,end))
	  continue;
	if (begin->first._VECTptr->back()!=c)
	  continue;
	gen coeff=begin->second/pivot;
	l[makesequence(R,c)]=coeff;
	sparse_rowadd(u,R,c,begin,end,-coeff,begin2,end2);
      }
      ++r; ++c;
    } // end r<L && c<C
    sparse_trim(u,u_);
    return true;
  }

  // solve triangular lower inf system l*y=b
  bool sparse_linsolve_l(const gen_map & l,const vecteur & b,vecteur & y){
    int n=int(b.size());
    y.resize(n);
    std::vector<sparse_line_begin_t> L;
    find_line_begin(l.begin(),l.end(),L);
    gen_map::const_iterator begin,end;
    for (int i=0;i<n;++i){
      if (!dicho(L,i,begin,end))
	return false;
      gen res=b[i]; bool ok=false;
      for (;begin!=end;++begin){
	int j=begin->first._VECTptr->back().val;
	if (j>i)
	  return false;
	if (j==i){
	  res=res/begin->second;
	  ok=true;
	}
	else 
	  res -= y[j]*begin->second;
      }
      if (!ok)
	return false;
      y[i]=res;
    }
    return true;
  }

  bool sparse_linsolve_l(const fmatrix & l,const std::vector<giac_double> & b,std::vector<giac_double> & y){
    int n=int(b.size());
    y.resize(n);
    for (int i=0;i<n;++i){
      const std::vector<int> & posi=l.pos[i];
      const std::vector<giac_double> & li = l.m[i];
      double res=b[i]; int s=int(posi.size()); bool ok=false;
      for (int j=0;j<s;++j){
	int pos=posi[j];
	if (pos>i)
	  return false;
	if (pos==i){
	  res /= li[j];
	  ok=true;
	}
	else
	  res -= y[pos]*li[j];
      }
      if (!ok)
	return false;
      y[i]=res;
    }
    return true;
  }

  // solve triangular upper system u*x=b
  bool sparse_linsolve_u(const gen_map & u,const vecteur & b,vecteur & x){
    int n=int(b.size());
    x.resize(n);
    std::vector<sparse_line_begin_t> U;
    find_line_begin(u.begin(),u.end(),U);
    gen_map::const_iterator begin,end;
    for (int i=n-1;i>=0;--i){
      if (!dicho(U,i,begin,end))
	return false;
      gen res=b[i],coeff; bool ok=false;
      for (;begin!=end;++begin){
	int j=begin->first._VECTptr->back().val;
	if (j<i)
	  return false;
	if (j==i){
	  coeff=begin->second;
	  ok=true;
	}
	else 
	  res -= x[j]*begin->second;
      }
      if (!ok)
	return false;
      x[i]=res/coeff;
    }
    return true;
  }

  void smatrix::dbgprint() const  { 
    gen_map d; convert(*this,d); 
    CERR << d << endl; 
  }

  void fmatrix::dbgprint() const  { 
    for (int i=0;i<int(pos.size());++i){
      const std::vector<int> & posi=pos[i];
      const std::vector<double> & mi=m[i];
      CERR << "line " << i << ": ";
      for (int j=0;j<posi.size();++j){
	CERR << posi[j]<<"="<<mi[j] <<", ";
      }
      CERR << endl;
    }
  }

  bool convert(const gen_map & d,smatrix & s){
    int nrows,ncols,n;
    if (!is_sparse_matrix(d,nrows,ncols,n))
      return false;
    s.pos=std::vector< std::vector<int> >(nrows);
    s.m=vecteur(nrows);
    for (int i=0;i<nrows;++i)
      s.m[i]=vecteur(0);
    int prev=-1;
    gen_map::const_iterator it=d.begin(),itend=d.end();
    for (;it!=itend;++it){
      gen p=it->first;
      if (p.type!=_VECT || p._VECTptr->size()!=2)
	return false;
      int l=p._VECTptr->front().val,c=p._VECTptr->back().val;
      s.pos[l].push_back(c);
      s.m[l]._VECTptr->push_back(it->second);
    }
    return true;
  }

  bool convert(const gen_map & d,fmatrix & s){
    int nrows,ncols,n;
    if (!is_sparse_matrix(d,nrows,ncols,n))
      return false;
    s.pos=std::vector< std::vector<int> >(nrows);
    s.m=std::vector< std::vector<giac_double> >(nrows);
    int prev=-1;
    gen_map::const_iterator it=d.begin(),itend=d.end();
    for (;it!=itend;++it){
      gen p=it->first;
      if (p.type!=_VECT || p._VECTptr->size()!=2)
	return false;
      int l=p._VECTptr->front().val,c=p._VECTptr->back().val;
      s.pos[l].push_back(c);
      gen tmp=evalf_double(it->second,1,context0);
      if (tmp.type!=_DOUBLE_)
	return false;
      s.m[l].push_back(tmp._DOUBLE_val);
    }
    return true;
  }

  bool convert(const smatrix & s,gen_map & d){
    d.clear();
    int ss=s.size();
    for (int i=0;i<ss;++i){
      const vecteur & v =*s.m[i]._VECTptr;
      const std::vector<int> & p=s.pos[i];
      if (v.size()!=p.size())
	return false;
      const_iterateur it=v.begin(),itend=v.end();
      std::vector<int>::const_iterator jt=p.begin();
      for (;it!=itend;++jt,++it){
	d[makesequence(i,*jt)]=*it;
      }
    }
    return true;
  }
  
  bool convert(const fmatrix & s,gen_map & d){
    d.clear();
    int ss=s.size();
    for (int i=0;i<ss;++i){
      const std::vector<double> & v =s.m[i];
      const std::vector<int> & p=s.pos[i];
      if (v.size()!=p.size())
	return false;
      std::vector<double>::const_iterator it=v.begin(),itend=v.end();
      std::vector<int>::const_iterator jt=p.begin();
      for (;it!=itend;++jt,++it){
	d[makesequence(i,*jt)]=*it;
      }
    }
    return true;
  }

  vecteur convert(const std::vector<giac_double> & v){
    vecteur res;
    res.reserve(v.size());
    for (int i=0;i<v.size();++i)
      res.push_back(v[i]);
    return res;
  }

  bool convert(const vecteur & source,std::vector<giac_double> & target){
    const_iterateur it=source.begin(),itend=source.end();
    target.clear();
    target.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=evalf_double(*it,1,context0);
      if (tmp.type!=_DOUBLE_)
	return false;
      target.push_back(tmp._DOUBLE_val);
    }
    return true;
  }

  bool convert(const vecteur & m,gen_map & res){
    if (ckmatrix(m)){
      for (int i=0;i<int(m.size());++i){
	const vecteur & v = *m[i]._VECTptr;
	for (int j=0;j<int(v.size());++j){
	  if (!is_zero(v[j]))
	    res[makesequence(i,j)]=v[j];
	}
      }
    }
    else {
      for (int i=0;i<int(m.size());++i){
	if (!is_zero(m[i]))
	  res[i]=m[i];
      }
    }
    return true;
  }

  bool convert(const gen_map & g,vecteur & res){
    int n,nrows,ncols;
    if (!is_sparse_matrix(g,nrows,ncols,n)){
      if (!is_sparse_vector(g,nrows,n))
	return false;
      res=vecteur(nrows);
      gen_map::const_iterator it=g.begin(),itend=g.end();
      for (;it!=itend;++it){
	gen l=it->first;
	is_integral(l); 
	res[l.val]=it->second;
      }
      return true;
    }
    res=vecteur(nrows);
    for (int i=0;i<nrows;++i){
      vecteur l(ncols);
      res[i]=l;
    }
    gen_map::const_iterator it=g.begin(),itend=g.end();
    for (;it!=itend;++it){
      gen G=it->first;
      gen l=G._VECTptr->front();
      gen c=G._VECTptr->back();
      is_integral(l); is_integral(c);
      (*res[l.val]._VECTptr)[c.val]=it->second;
    }
    return true;
  }
    
  bool is_sparse_matrix(const gen & g,int & nrows,int & ncols,int & n){
    if (g.type!=_MAP)
      return false;
    gen_map & m=*g._MAPptr;
    return is_sparse_matrix(*g._MAPptr,nrows,ncols,n);
  }
  bool is_sparse_matrix(const gen_map & m,int & nrows,int & ncols,int & n){
    nrows=0;ncols=0;n=0;
    gen_map::const_iterator it=m.begin(),itend=m.end();
    for (;it!=itend;++n,++it){
      gen g=it->first;
      if (g.type!=_VECT || g._VECTptr->size()!=2)
	return false;
      gen l=g._VECTptr->front();
      gen c=g._VECTptr->back();
      if (!is_integral(l) || !is_integral(c) || l.val<0 || c.val<0)
	return false;
      if (nrows<=l.val)
	nrows=l.val+1;
      if (ncols<=c.val)
	ncols=c.val+1;
    }
    return true;
  }

  bool is_sparse_vector(const gen & g,int & nrows,int & n){
    if (g.type!=_MAP)
      return false;
    return is_sparse_vector(*g._MAPptr,nrows,n);
  }
  bool is_sparse_vector(const gen_map & m,int & nrows,int & n){
    nrows=0;n=0;
    gen_map::const_iterator it=m.begin(),itend=m.end();
    for (;it!=itend;++n,++it){
      gen l=it->first;
      if (!is_integral(l) || l.val<0)
	return false;
      if (nrows<=l.val)
	nrows=l.val+1;
    }
    return true;
  }

  static int ncols_(const std::vector< std::vector<int> > & pos){
    int res=-1;
    for (unsigned i=0;i<pos.size();++i){
      const std::vector<int> & pi=pos[i];
      if (!pi.empty())
	res=giacmax(res,pi.back());
    }
    return res+1;
  }

  int smatrix::ncols() const { 
    return ncols_(pos);
  }

  int fmatrix::ncols() const {
    return ncols_(pos);
  }

  gen sparse_conjugate_gradient(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(b_orig.size());
    vecteur tmp(n);
    sparse_mult(A,x0,tmp);
    vecteur b=subvecteur(b_orig,tmp);
    vecteur xk(x0);
    vecteur rk(b),pk(b);
    gen rk2=scalarproduct(rk,rk,contextptr);
    vecteur Apk(n);
    for (int k=1;k<=maxiter;++k){
      sparse_mult(A,pk,Apk);
      gen alphak=rk2/scalarproduct(pk,Apk,contextptr);
      multvecteur(alphak,pk,tmp);
      addvecteur(xk,tmp,xk);
      multvecteur(alphak,Apk,tmp);
      subvecteur(rk,tmp,rk);
      gen newrk2=scalarproduct(rk,rk,contextptr);
      if (is_greater(eps*eps,newrk2,contextptr))
	return xk;
      multvecteur(newrk2/rk2,pk,tmp);
      addvecteur(rk,tmp,pk);
      rk2=newrk2;
    }
    *logptr(contextptr) << gettext("Warning! Leaving conjugate gradient algorithm after dimension of matrix iterations. Check that your matrix is hermitian/symmetric definite.") << endl;
    return xk;
  }

  std::vector<giac_double> sparse_conjugate_gradient(const fmatrix & A,const std::vector<giac_double> & b_orig,const std::vector<giac_double> & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(b_orig.size());
    std::vector<giac_double> tmp(n);
    sparse_mult(A,x0,tmp);
    std::vector<giac_double> b;
    subvecteur(b_orig,tmp,b);
    std::vector<giac_double> xk(x0);
    std::vector<giac_double> rk(b),pk(b);
    giac_double rk2=dotvecteur(rk,rk);
    std::vector<giac_double> Apk(n);
    for (int k=1;k<=maxiter;++k){
      sparse_mult(A,pk,Apk);
      giac_double alphak=rk2/dotvecteur(pk,Apk);
      multvecteur(alphak,pk,tmp);
      addvecteur(xk,tmp,xk);
      multvecteur(alphak,Apk,tmp);
      subvecteur(rk,tmp,rk);
      giac_double newrk2=dotvecteur(rk,rk);
      if (eps*eps>=newrk2)
	return xk;
      multvecteur(newrk2/rk2,pk,tmp);
      addvecteur(rk,tmp,pk);
      rk2=newrk2;
    }
    *logptr(contextptr) << gettext("Warning! Leaving conjugate gradient algorithm after dimension of matrix iterations. Check that your matrix is hermitian/symmetric definite.") << endl;
    return xk;
  }

  gen sparse_conjugate_gradient(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    if (has_num_coeff(b_orig)){
      fmatrix As; std::vector<giac_double> B_orig,X0;
      if (convert(A,As) && convert(b_orig,B_orig) && convert(x0,X0)){
	std::vector<giac_double> res=sparse_conjugate_gradient(As,B_orig,X0,eps,maxiter,contextptr);
	return convert(res);
      }
    }
    smatrix As;
    if (!convert(A,As))
      return gendimerr(contextptr);
    return sparse_conjugate_gradient(As,b_orig,x0,eps,maxiter,contextptr);
  }

  // Ax=b where A=D+B, Dx_{n+1}=b-B*x_n
  gen sparse_jacobi_linsolve(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.m.size());
    smatrix B;
    vecteur D(n);
    vecteur b=*evalf_double(b_orig,1,contextptr)._VECTptr;
    for (int i=0;i<n;++i){
      const vecteur & Ai=*A.m[i]._VECTptr;
      const std::vector<int> & posi=A.pos[i];
      vecteur Bi; std::vector<int> posB;
      Bi.reserve(Ai.size()); posB.reserve(posi.size());
      for (int j=0;j<int(posi.size());++j){
	if (posi[j]==i){
	  D[i]=evalf_double(Ai[j],1,contextptr);
	}
	else {
	  posB.push_back(posi[j]);
	  Bi.push_back(evalf_double(Ai[j],1,contextptr));
	}
      }
      B.m.push_back(Bi);
      B.pos.push_back(posB);
    }
    vecteur tmp(n),xn(x0),prev(n);
    gen bn=l2norm(b,contextptr);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      sparse_mult(B,xn,tmp);
      subvecteur(b,tmp,xn);
      iterateur jt=xn.begin(),jtend=xn.end(),dt=D.begin();
      for (;jt!=jtend;++jt){
	*jt=*jt / *dt;
      }
      gen g=l2norm(xn-prev,contextptr)/bn;
      if (is_greater(eps,g,contextptr))
	return xn;
    }
    *logptr(contextptr) << gettext("Warning! Leaving Jacobi iterative algorithm after maximal number of iterations. Check that your matrix is diagonal dominant.") << endl;
    return xn;    
  }

  double l2norm(const std::vector<giac_double> & v){
    double res=0;
    std::vector<giac_double>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      register double tmp=*it;
      res+=tmp*tmp;
    }
    return std::sqrt(res);
  }

  void subvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b,std::vector<giac_double> & c){
    if (&b==&c){
      std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
      std::vector<giac_double>::const_iterator at=a.begin();
      for (;ct!=ctend;++ct,++at){
	*ct=*at-*ct;
      }
      return;
    }
    if (&a==&c){
      std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
      std::vector<giac_double>::const_iterator bt=b.begin();
      for (;ct!=ctend;++ct,++bt){
	*ct -= *bt;
      }
      return;
    }
    c.resize(a.size());
    std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
    std::vector<giac_double>::const_iterator at=a.begin(),bt=b.begin();
    for (;ct!=ctend;++at,++bt,++ct){
      *ct = *at-*bt;
    }
  }
  
  std::vector<giac_double> subvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b){
    std::vector<giac_double> c;
    subvecteur(a,b,c);
    return c;
  }

  void addvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b,std::vector<giac_double> & c){
    if (&b==&c){
      std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
      std::vector<giac_double>::const_iterator at=a.begin();
      for (;ct!=ctend;++ct,++at){
	*ct=*at+*ct;
      }
      return;
    }
    if (&a==&c){
      std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
      std::vector<giac_double>::const_iterator bt=b.begin();
      for (;ct!=ctend;++ct,++bt){
	*ct += *bt;
      }
      return;
    }
    c.resize(a.size());
    std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
    std::vector<giac_double>::const_iterator at=a.begin(),bt=b.begin();
    for (;ct!=ctend;++at,++bt,++ct){
      *ct = *at+*bt;
    }
  }

  std::vector<giac_double> addvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b){
    std::vector<giac_double> c;
    addvecteur(a,b,c);
    return c;
  }

  void multvecteur(double x,const std::vector<giac_double> & a,std::vector<giac_double> & c){
    if (&a==&c){
      std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
      for (;ct!=ctend;++ct){
	*ct *= x;
      }
      return;
    }
    c.resize(a.size());
    std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
    std::vector<giac_double>::const_iterator at=a.begin();
    for (;ct!=ctend;++at,++ct){
      *ct = x*(*at);
    }
  }

  void multvecteur(double x,std::vector<giac_double> & c){
    std::vector<giac_double>::iterator ct=c.begin(),ctend=c.end();
    for (;ct!=ctend;++ct){
      *ct *= x;
    }
  }
  
  std::vector<giac_double> multvecteur(double x,const std::vector<giac_double> & b){
    std::vector<giac_double> c(b);
    multvecteur(x,c);
    return c;
  }

  // Ax=b where A=D+B, Dx_{n+1}=b-B*x_n
  std::vector<giac_double> sparse_jacobi_linsolve(const fmatrix & A,const std::vector<giac_double> & b_orig,const std::vector<giac_double> & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.m.size());
    fmatrix B;
    std::vector<giac_double> D(n);
    std::vector<giac_double> b=b_orig;
    for (int i=0;i<n;++i){
      const std::vector<double> & Ai=A.m[i];
      const std::vector<int> & posi=A.pos[i];
      std::vector<giac_double> Bi; std::vector<int> posB;
      Bi.reserve(Ai.size()); posB.reserve(posi.size());
      for (int j=0;j<int(posi.size());++j){
	if (posi[j]==i){
	  D[i]=Ai[j];
	}
	else {
	  posB.push_back(posi[j]);
	  Bi.push_back(Ai[j]);
	}
      }
      B.m.push_back(Bi);
      B.pos.push_back(posB);
    }
    std::vector<giac_double> tmp(n),xn(x0),prev(n);
    double bn=l2norm(b);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      sparse_mult(B,xn,tmp);
      subvecteur(b,tmp,xn);
      std::vector<giac_double>::iterator jt=xn.begin(),jtend=xn.end(),dt=D.begin();
      for (;jt!=jtend;++jt){
	*jt=*jt / *dt;
      }
      subvecteur(xn,prev,tmp);
      double g=l2norm(tmp)/bn;
      if (eps>g)
	return xn;
    }
    *logptr(contextptr) << gettext("Warning! Leaving Jacobi iterative algorithm after maximal number of iterations. Check that your matrix is diagonal dominant.") << endl;
    return xn;    
  }
  
  gen sparse_jacobi_linsolve(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    fmatrix Asf;
    std::vector<giac_double> B_orig,X0;
    if (convert(A,Asf) && convert(b_orig,B_orig) && convert(x0,X0)){
      std::vector<giac_double> res=sparse_jacobi_linsolve(Asf,B_orig,X0,eps,maxiter,contextptr);
      return convert(res);
    }
    smatrix As;
    if (!convert(A,As))
      return gendimerr(contextptr);
    return sparse_jacobi_linsolve(As,b_orig,x0,eps,maxiter,contextptr);
  }

  // Ax=b where A=L+D+U, (D+L)x_{n+1}=b-U*x_n (Gauss-Seidel for omega==1)
  // or (L+D/omega)*x_{n+1}=b-(U+D*(1-1/omega))*x_n
  // or (-omega*E+D)*x_{n+1}=omega*b+(omega*F+D*(1-omega))*x_n
  // or (I-omega*D^-1*E)x_{n+1}=omega*D^-1*b+((1-omega)*I+omega*D^-1*F)*x_n
  gen sparse_gauss_seidel_linsolve(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double omega,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.m.size());
    double invomega=1/omega;
    smatrix L,U;
    vecteur b=*evalf_double(b_orig,1,contextptr)._VECTptr;
    for (int i=0;i<n;++i){
      const vecteur & Ai=*A.m[i]._VECTptr;
      const std::vector<int> & posi=A.pos[i];
      vecteur Li,Ui; std::vector<int> posL,posU;
      for (int j=0;j<int(posi.size());++j){
	if (posi[j]==i) {
	  posL.push_back(posi[j]);
	  Li.push_back(invomega*evalf_double(Ai[j],1,contextptr));
	  if (invomega!=1){
	    posU.push_back(posi[j]);
	    Ui.push_back((1-invomega)*evalf_double(Ai[j],1,contextptr));	  
	  }
	  continue;
	}
	if (posi[j]<i){
	  posL.push_back(posi[j]);
	  Li.push_back(evalf_double(Ai[j],1,contextptr));	  
	  continue;
	}
	posU.push_back(posi[j]);
	Ui.push_back(evalf_double(Ai[j],1,contextptr));	  
      }
      L.m.push_back(Li);
      L.pos.push_back(posL);
      U.m.push_back(Ui);
      U.pos.push_back(posU);
    }
    vecteur tmp(n),xn(x0),prev(n);
    gen bn=l2norm(b,contextptr);
    gen_map Lfixme; convert(L,Lfixme);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      sparse_mult(U,xn,tmp);
      subvecteur(b,tmp,tmp);
      if (!sparse_linsolve_l(Lfixme,tmp,xn))
	return gensizeerr(contextptr);
      gen g=l2norm(xn-prev,contextptr)/bn;
      if (is_greater(eps,g,contextptr))
	return xn;
    }
    *logptr(contextptr) << gettext("Warning! Leaving Gauss-Seidel iterative algorithm after maximal number of iterations. Check that your matrix is diagonal dominant.") << endl;
    return xn;    
  }
  
  // Ax=b where A=L+D+U, (D+L)x_{n+1}=b-U*x_n (Gauss-Seidel for omega==1)
  // or (L+D/omega)*x_{n+1}=b-(U+D*(1-1/omega))*x_n
  std::vector<giac_double> sparse_gauss_seidel_linsolve(const fmatrix & A,const std::vector<giac_double> & b_orig,const std::vector<giac_double> & x0,double omega,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.m.size());
    double bn=l2norm(b_orig);
#if 1
    // Wikipedia notations
    std::vector<giac_double> tmp(n),phiknew(n),phik(x0);
    for (int iter=0;iter<maxiter;++iter){
      for (int i=0;i<n;++i){
	giac_double sigma=0,aii=0;
	// const std::vector<giac_double> Ai=A.m[i];
	const std::vector<int> & posi=A.pos[i];
	bool ok=false;
	std::vector<int>::const_iterator post=posi.begin(),postend=posi.end();
	std::vector<giac_double>::const_iterator at=A.m[i].begin();
	for (;post!=postend;++at,++post){
	  int posij=*post;
	  if (posij==i) 
	    aii=*at;
	  else 
	    sigma += *at*(posij<i?phiknew[posij]:phik[posij]);
	}
	if (aii==0)
	  return std::vector<giac_double>(0);
	phiknew[i]=(1-omega)*phik[i]+omega/aii*(b_orig[i]-sigma);
      }
      subvecteur(phik,phiknew,tmp);
      if (l2norm(tmp)<eps*bn){
	if (debug_infolevel)
	  *logptr(contextptr) << "Convergence criterium reached after " << iter << " iterations, omega=" << omega << endl;
	return phiknew;
      }
      phik.swap(phiknew);
    }
    *logptr(contextptr) << gettext("Warning! Leaving Gauss-Seidel iterative algorithm after maximal number of iterations. Check that your matrix is symetric definite.") << endl;
    return phik;   
#else
    std::vector<giac_double> b=b_orig;
    double invomega=1/omega;
    fmatrix L,U;
    for (int i=0;i<n;++i){
      const std::vector<giac_double> Ai=A.m[i];
      const std::vector<int> & posi=A.pos[i];
      std::vector<giac_double> Li,Ui; std::vector<int> posL,posU;
      for (int j=0;j<int(posi.size());++j){
	if (posi[j]==i) {
	  posL.push_back(posi[j]);
	  Li.push_back(invomega*Ai[j]);
	  if (invomega!=1){
	    posU.push_back(posi[j]);
	    Ui.push_back((1-invomega)*Ai[j]);	  
	  }
	  continue;
	}
	if (posi[j]<i){
	  posL.push_back(posi[j]);
	  Li.push_back(Ai[j]);	  
	  continue;
	}
	posU.push_back(posi[j]);
	Ui.push_back(Ai[j]);	  
      }
      L.m.push_back(Li);
      L.pos.push_back(posL);
      U.m.push_back(Ui);
      U.pos.push_back(posU);
    }
    std::vector<giac_double> tmp(n),xn(x0),prev(n);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      sparse_mult(U,xn,tmp);
      subvecteur(b,tmp,tmp);
      if (!sparse_linsolve_l(L,tmp,xn))
	return std::vector<giac_double>(0);
      // CERR << xn << endl;
      subvecteur(xn,prev,tmp);
      double g=l2norm(tmp)/bn;
      if (eps>=g){
	if (debug_infolevel)
	  *logptr(contextptr) << "Convergence criterium reached after " << i << " iterations, omega=" << omega << endl;
	return xn;
      }
    }
    *logptr(contextptr) << gettext("Warning! Leaving Gauss-Seidel iterative algorithm after maximal number of iterations. Check that your matrix is symetric definite.") << endl;
    return xn;   
#endif 
  }
  
  gen sparse_gauss_seidel_linsolve(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double omega,double eps,int maxiter,GIAC_CONTEXT){
    fmatrix Asf;
    std::vector<giac_double> B_orig,X0;
    if (convert(A,Asf) && convert(b_orig,B_orig) && convert(x0,X0)){
      std::vector<giac_double> res=sparse_gauss_seidel_linsolve(Asf,B_orig,X0,omega,eps,maxiter,contextptr);
      return convert(res);
    }
    smatrix As;
    if (!convert(A,As))
      return gendimerr(contextptr);
    return sparse_gauss_seidel_linsolve(As,b_orig,x0,omega,eps,maxiter,contextptr);
  }

#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC
