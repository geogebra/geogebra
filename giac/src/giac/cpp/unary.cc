// -*- mode:C++ ; compile-command: "g++ -I.. -g -c unary.cc -DIN_GIAC -DHAVE_CONFIG_H" -*-
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
#include "unary.h"
#include "gen.h"
#include "usual.h"
#include "rpn.h"
#include "tex.h"
#include "input_lexer.h"
#include "symbolic.h"
#include "input_lexer.h"
#include "rpn.h"
#include "sparse.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  // unary_function_ptr

  gen unary_function_ptr::operator () (const gen & arg,const context * context_ptr) const{
    return (*ptr())(arg,context_ptr);
    // return (*ptr)(arg,context_ptr);
    this->dbgprint();
  }

  const char * unary_function_ptr::dbgprint() const {
#ifndef NSPIRE
    CERR << ptr()->s << endl; 
#endif
    return ptr()->s;
  }

#if 0 // def x86_64
  bool unary_function_ptr::quoted() const { return (ptr()->index_quoted_function & 0x1) || ( ((longlong) _ptr) & 0x1); }
#else
  bool unary_function_ptr::quoted() const { return (ptr()->index_quoted_function & 0x1) || ( ((size_t) _ptr) & 0x1); }
#endif

  /*
  unary_function_ptr::unary_function_ptr(const unary_function_ptr & myptr):ptr(myptr.ptr),ref_count(myptr.ref_count),quoted(myptr.quoted){
    if (ref_count)
      ++(*ref_count);
  }

  // dynamic unary_function_abstract
  unary_function_ptr::unary_function_ptr(const unary_function_abstract & myptr): quoted(0) {
    ref_count = new int(1);
    ptr = myptr.recopie();
  }

  unary_function_ptr::unary_function_ptr(const unary_function_abstract & myptr,int myquoted,int parser_token): quoted(myquoted) {
    ptr = myptr.recopie();
    if (parser_token)
      if (!lexer_functions_register(*this,ptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+ptr->s);
  }
  */

  // global unary_function_abstract pointer, no reference count here

#ifdef NO_UNARY_FUNCTION_COMPOSE
  unary_function_ptr::unary_function_ptr(const unary_function_eval * myptr,int myquoted,int parser_token): _ptr((size_t)myptr){
    if (myquoted)
      *((size_t *) &_ptr) |= 0x1;
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  unary_function_ptr::unary_function_ptr(const alias_unary_function_eval * myptr,int myquoted,int parser_token):_ptr((size_t)myptr) {     
    if (myquoted)
      *((size_t *) &_ptr) |= 0x1;
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  /*
  unary_function_ptr::unary_function_ptr(const unary_function_eval * myptr,int parser_token): _ptr(myptr){
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }
  unary_function_ptr::unary_function_ptr(const alias_unary_function_eval * myptr,int parser_token):_ptr((const unary_function_eval *)myptr) {     
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  */
  const char * unary_function_eval::print(GIAC_CONTEXT) const { 
    if (abs_calc_mode(contextptr)==38){ 
      if (calc_mode(contextptr)==38){
	const char * maj = hp38_display_in_maj(s);
	return maj?maj:s;
      }
      else
	return s;
    }
    int lang=language(contextptr);
    multimap<string,localized_string>::iterator it=back_lexer_localization_map().find(s),backend=back_lexer_localization_map().end(),itend=back_lexer_localization_map().upper_bound(s);
    if (it!=backend){
      for (;it!=itend;++it){
	if (it->second.language==lang)
	  return it->second.chaine.c_str();
      }
    }
    return s;
  }

#else //  NO_UNARY_FUNCTION_COMPOSE
  unary_function_ptr::unary_function_ptr(const unary_function_abstract * myptr,int myquoted,int parser_token): _ptr(myptr){
    if (myquoted)
      *((size_t *) &_ptr) |= 0x1;
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  unary_function_ptr::unary_function_ptr(const alias_unary_function_eval * myptr,int myquoted,int parser_token):_ptr((const unary_function_abstract *)myptr) {     
    if (myquoted)
      *((size_t *) &_ptr) |= 0x1;
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  /*
  unary_function_ptr::unary_function_ptr(const unary_function_abstract * myptr,int parser_token): _ptr(myptr){
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }

  unary_function_ptr::unary_function_ptr(const alias_unary_function_eval * myptr,int parser_token):_ptr((const unary_function_abstract *)myptr) {     
    if (parser_token)
      if (!lexer_functions_register(*this,myptr->s,parser_token))
	setsizeerr(gettext("Unable to register ")+string(myptr->s));
  }
  */

  const char * unary_function_abstract::print(GIAC_CONTEXT) const { 
    if (abs_calc_mode(contextptr)==38){ 
      if (calc_mode(contextptr)==38){
	const char * maj = hp38_display_in_maj(s);
	return maj?maj:s;
      }
      return s;
    }
    int lang=language(contextptr);
    multimap<string,localized_string>::iterator it=back_lexer_localization_map().find(s),backend=back_lexer_localization_map().end(),itend=back_lexer_localization_map().upper_bound(s);
    if (it!=backend){
      for (;it!=itend;++it){
	if (it->second.language==lang)
	  return it->second.chaine.c_str();
      }
    }
    return s; 
  }
#endif //  NO_UNARY_FUNCTION_COMPOSE

  /*
  // copy constructor
  unary_function_ptr & unary_function_ptr::operator = (const unary_function_ptr & acopier){
    if (ref_count){
      if (!((*ref_count)--)){
	delete ptr;
	delete ref_count;
      }
    }
    ptr = acopier.ptr;
    ref_count=acopier.ref_count;
    quoted = acopier.quoted;
    if (ref_count)
      ++(*ref_count);
    return *this;
  }

  unary_function_ptr::~unary_function_ptr(){
    if (ref_count){
      if (!((*ref_count)--)){
	delete ptr;
	delete ref_count;
      }
    }
  }
  */

  gen apply(const gen & e,const unary_function_ptr & f,GIAC_CONTEXT){
    if (e.type==_MAP){
      gen_map res;
      gen g(res);
      map_apply(*e._MAPptr,f,*g._MAPptr,contextptr);
      return g;
    }
    if (e.type!=_VECT)
      return f(e,contextptr);
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    //    if (itend==it && abs_calc_mode(contextptr)==38) return gensizeerr();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e.subtype);
  }

  gen apply(const gen & e,const unary_function_ptr * f,GIAC_CONTEXT){
    if (e.type!=_VECT)
      return (*f)(e,contextptr);
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    // if (itend==it && abs_calc_mode(contextptr)==38) return gensizeerr();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=(*f)(*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e.subtype);
  }

  gen apply(const gen & e, gen (* f) (const gen &) ){
    if (e.type!=_VECT)
      return f(e);
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e.subtype);
  }

  gen apply(const gen & e, gen (* f) (const gen &,const context *),GIAC_CONTEXT ){
    if (e.type==_MAP){
      gen_map res;
      gen g(res);
      map_apply(*e._MAPptr,*g._MAPptr,contextptr,f);
      return g;
    }
    if (e.type!=_VECT)
      return f(e,contextptr);
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    // if (itend==it && abs_calc_mode(contextptr)==38) return gensizeerr();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e.subtype);
  }

  gen apply(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) ){
    if ((e1.type!=_VECT) && (e2.type!=_VECT))
      return f(e1,e2);
    if (e1.type!=_VECT){
      const_iterateur it=e2._VECTptr->begin(),itend=e2._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it){
	gen tmp=f(e1,*it);
	if (is_undef(tmp))
	  return gen2vecteur(tmp);
	v.push_back(tmp);
      }
      return gen(v,e2.subtype);
    }
    if (e2.type!=_VECT){
      const_iterateur it=e1._VECTptr->begin(),itend=e1._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it){
	gen tmp=f(*it,e2);
	if (is_undef(tmp))
	  return gen2vecteur(tmp);
	v.push_back(tmp);
      }
      return gen(v,e1.subtype);
    }
    const_iterateur it1=e1._VECTptr->begin(),it1end=e1._VECTptr->end();
    const_iterateur it2=e2._VECTptr->begin(),it2end=e2._VECTptr->end();
    // if (it2end-it2!=it1end-it1) return gendimerr();
    vecteur v;
    v.reserve(it1end-it1);
    for (;it1!=it1end && it2!=it2end;++it1,++it2){
      gen tmp=f(*it1,*it2);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e1.subtype);
  }

  gen apply(const gen & e, const context * contextptr,const gen_op_context & f ){
    if (e.type==_MAP){
      gen_map res;
      gen g(res);
      map_apply(*e._MAPptr,*g._MAPptr,contextptr,f);
      return g;
    }
    if (e.type!=_VECT)
      return f(e,contextptr);
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    // if (itend==it && abs_calc_mode(contextptr)==38) return gensizeerr();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e.subtype);
  }

  gen apply(const gen & e1, const gen & e2,const context * contextptr,gen (* f) (const gen &, const gen &,const context *) ){
    if ((e1.type!=_VECT) && (e2.type!=_VECT))
      return f(e1,e2,contextptr);
    if (e1.type!=_VECT){
      const_iterateur it=e2._VECTptr->begin(),itend=e2._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it){
	gen tmp=f(e1,*it,contextptr);
	if (is_undef(tmp))
	  return gen2vecteur(tmp);
	v.push_back(tmp);
      }
      return gen(v,e2.subtype);
    }
    if (e2.type!=_VECT){
      const_iterateur it=e1._VECTptr->begin(),itend=e1._VECTptr->end();
      vecteur v;
      v.reserve(itend-it);
      for (;it!=itend;++it){
	gen tmp=f(*it,e2,contextptr);
	if (is_undef(tmp))
	  return gen2vecteur(tmp);
	v.push_back(tmp);
      }
      return gen(v,e1.subtype);
    }
    const_iterateur it1=e1._VECTptr->begin(),it1end=e1._VECTptr->end();
    const_iterateur it2=e2._VECTptr->begin(),it2end=e2._VECTptr->end();
    // if (it2end-it2!=it1end-it1) return gendimerr();
    vecteur v;
    v.reserve(it1end-it1);
    for (;it1!=it1end && it2!=it2end;++it1,++it2){
      gen tmp=f(*it1,*it2,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e1.subtype);
  }

  gen apply1st(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) ){
    if (e1.type!=_VECT)
      return f(e1,e2);
    const_iterateur it=e1._VECTptr->begin(),itend=e1._VECTptr->end();
    gen res=new ref_vecteur;
    res.subtype=e1.subtype;
    vecteur &v=*res._VECTptr;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it,e2);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return res;//gen(v,e1.subtype);
  }

  gen apply1st(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) ){
    if (e1.type!=_VECT)
      return f(e1,e2,contextptr);
    const_iterateur it=e1._VECTptr->begin(),itend=e1._VECTptr->end();
    gen res=new ref_vecteur;
    res.subtype=e1.subtype;
    vecteur &v=*res._VECTptr;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(*it,e2,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return res;//gen(v,e1.subtype);
  }

  gen apply2nd(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) ){
    if (e2.type!=_VECT)
      return f(e1,e2);
    const_iterateur it=e2._VECTptr->begin(),itend=e2._VECTptr->end();
    gen res=new ref_vecteur;
    res.subtype=e2.subtype;
    vecteur &v=*res._VECTptr;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(e1,*it);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return res; // gen(v,e2.subtype);
  }

  gen apply2nd(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) ){
    if (e2.type!=_VECT)
      return f(e1,e2,contextptr);
    const_iterateur it=e2._VECTptr->begin(),itend=e2._VECTptr->end();
    gen res=new ref_vecteur;
    res.subtype=e2.subtype;
    vecteur &v=*res._VECTptr; //   vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(e1,*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return res;//gen(v,e2.subtype);
  }

#ifdef NO_UNARY_FUNCTION_COMPOSE
#ifdef NSPIRE
  template<class T> nio::ios_base<T> & operator << (nio::ios_base<T> & os,const unary_function_eval & o) { return os << o.s ; }
#else
  ostream & operator << (ostream & os,const unary_function_eval & o) { return os << o.s ; }
#endif
#else
  // unary_function_abstract
  unary_function_abstract * unary_function_abstract::recopie() const{
    unary_function_abstract * ptr=new unary_function_abstract(index_quoted_function,s);
    ptr->D = D;
    return ptr;
  }

  unary_function_unary * unary_function_unary::recopie() const{
    unary_function_unary * ptr=new unary_function_unary(index_quoted_function,op,s);
    ptr->D = D;
    return ptr;
  }

  unary_function_eval * unary_function_eval::recopie() const{
    unary_function_eval * ptr=new unary_function_eval(index_quoted_function,op,s);
    ptr->D = D;
    return ptr;
  }

  // I/O
  ostream & operator << (ostream & os,const unary_function_abstract & o){ return os << o.s; }
  ostream & operator << (ostream & os,const unary_function_unary & o) { return os << o.s ; }
  ostream & operator << (ostream & os,const unary_function_eval & o) { return os << o.s ; }

  unary_function_compose * unary_function_compose::recopie() const{
    unary_function_compose * ptr=new unary_function_compose(index_quoted_function,op_v);
    ptr->D = D;
    return ptr;
  }

  unary_function_list * unary_function_list::recopie() const{
    unary_function_list * ptr=new unary_function_list(index_quoted_function,op_l);
    ptr->D = D;
    return ptr;
  }

  unary_function_constant * unary_function_constant::recopie() const{
    unary_function_constant * ptr=new unary_function_constant(index_quoted_function,constant);
    ptr->D = D;
    return ptr;
  }

  unary_function_innerprod * unary_function_innerprod::recopie() const{
    unary_function_innerprod * ptr=new unary_function_innerprod(index_quoted_function,i);
    ptr->D = D;
    return ptr;
  }
  
  unary_function_user * unary_function_user::recopie() const{
    unary_function_user * ptr=new unary_function_user(index_quoted_function,f,s,printsommet,texprint,cprint);
    ptr->D = D;
    return ptr;
  }
  

  // unary_function_compose related
  gen unary_function_compose::operator () (const gen & arg,const context * context_ptr) const{
    vector<unary_function_ptr>::const_iterator it=op_v.begin(),itend=op_v.end();
    gen res(arg);
    for (;it!=itend;++it)
      res=(*it)(res,context_ptr);
    return res;
  }

  unary_function_compose::unary_function_compose(unsigned u,const vector<unary_function_ptr> & myop_v) : unary_function_abstract(u,0) {
    string * sptr=new string(":: ");
    vector<unary_function_ptr>::const_iterator it=myop_v.begin(),itend=myop_v.end();
    for (;it!=itend;++it){
      op_v.push_back( (*it) );
      *sptr += it->ptr()->s;
      *sptr +=" ";
    }
    *sptr += string(";");
    s=sptr->c_str();
  }

  gen unary_function_list::operator () (const gen & arg,const context * context_ptr) const{
    vector<unary_function_ptr>::const_iterator it=op_l.begin(),itend=op_l.end();
    vecteur res;
    for (;it!=itend;++it)
      res.push_back( (*it)(arg,context_ptr));
    return res;
  }

  unary_function_list::unary_function_list(unsigned u,const vector<unary_function_ptr> & myop_v) : unary_function_abstract(u,0) {
    string * sptr=new string("{ ");
    vector<unary_function_ptr>::const_iterator it=myop_v.begin(),itend=myop_v.end();
    for (;it!=itend;++it){
      op_l.push_back( (*it) );
      *sptr += it->ptr()->s;
      *sptr +=" ";
    }
    *sptr += string("}");
    s=sptr->c_str();
  }


  gen unary_function_innerprod::operator () (const gen & arg,const context * contextptr) const{
    if (arg.type!=_VECT)
      setsizeerr(arg.print(contextptr)+ gettext(" should be of type _VECT (unary.cc)"));
    vecteur res;
    // remove i indices from arg
    vecteur::const_iterator jt=arg._VECTptr->begin(),jtend=arg._VECTptr->end();
    vector<int>::const_iterator it=i.begin(), itend=i.end();
    for (int j=0;jt!=jtend;++jt,++j){
      if (it==itend)
	break;
      else {
	if (j!=*it)
	  res.push_back(*jt);
	else
	  ++it;
      }
    }
    for (;jt!=jtend;++jt)
      res.push_back(*jt);
    return res;
  }

  ostream & operator << (ostream & os,const unary_function_compose & p){ return os << p.s;} 
  ostream & operator << (ostream & os,const unary_function_list & p){ return os<< p.s; }
  ostream & operator << (ostream & os,const unary_function_constant & c){ return os<< c.s; }
  ostream & operator << (ostream & os,const unary_function_innerprod & i){ return os<< i.s; }

#endif // NO_UNARY_FUNCTION_COMPOSE

  string printsommetasoperator(const gen & feuille,const char * sommetstr_orig,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return feuille.print(contextptr);
    string sommetstr(sommetstr_orig);
    if ( (sommetstr[0]>32 && isalpha(sommetstr[0])) || sommetstr[0]=='%' || sommetstr[0]=='.')
      sommetstr=' '+sommetstr+' ';
    vecteur::const_iterator itb=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    if (itb==itend)
      return "";
    string s;
    if (itb->type==_FRAC && sommetstr!="=")
      s='('+itb->print(contextptr)+')';
    else {
      if ( sommetstr=="=" || itb->type==_IDNT || (itb->type<=_CPLX && is_positive(*itb,contextptr)) )
	s=itb->print(contextptr);
      else
	s='('+itb->print(contextptr)+')';
    }
    ++itb;
    for (;;){
      if (itb==itend)
	return s;
      if ( (itb->type==_SYMB || itb->type==_FRAC || itb->type==_CPLX || (itb->type==_VECT && itb->subtype==_SEQ__VECT) ) && sommetstr!="." )
	s += sommetstr + '('+itb->print(contextptr)+")";
      else
	s += sommetstr + itb->print(contextptr);
      ++itb;
    }
  }
    
  string texprintsommetasoperator(const gen & feuille,const char * sommetstr_orig,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return feuille.print(contextptr);
    string sommetstr(sommetstr_orig);
    vecteur::const_iterator itb=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    if (itb==itend)
      return "";
    string s;
    if (itb->type==_FRAC)
      s="("+gen2tex(*itb,contextptr)+")";
    else {
      if ( sommetstr=="=" || itb->type==_IDNT || (itb->type<=_CPLX && is_positive(*itb,contextptr)) )
	s=gen2tex(*itb,contextptr);
      else
	s="("+gen2tex(*itb,contextptr)+")";
    }
    ++itb;
    for (;;){
      if (itb==itend)
	return s;
      if ( itb->type==_SYMB || itb->type==_FRAC || itb->type==_CPLX || (itb->type==_VECT && itb->subtype==_SEQ__VECT) )
	s += sommetstr + '('+gen2tex(*itb,contextptr)+")";
      else
	s += sommetstr + gen2tex(*itb,contextptr);
      ++itb;
    }
  }

#ifdef NO_UNARY_FUNCTION_COMPOSE

  partial_derivative::partial_derivative(gen (* mydf) (const gen & argss,const context * contextptr) ) :     df(unary_function_ptr(new unary_function_eval(0,mydf,""))) {}

#else
  partial_derivative_onearg::partial_derivative_onearg(gen (* mydf) (const gen & args) ) :     df(unary_function_ptr(new unary_function_unary(0,mydf,""))) {}
  partial_derivative_onearg::partial_derivative_onearg(gen (* mydf) (const gen & args,const context * contextptr) ) :     df(unary_function_ptr(new unary_function_eval(0,mydf,""))) {}
#endif


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
