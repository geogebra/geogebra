#ifndef GIAC_VECTOR_H
#define GIAC_VECTOR_H
// Simple vector implementation for giac
// Incomplete (allocation is handled by new/delete)
// compatible with alias_ref_vecteur and without all VISUALC++ checks
#include <string.h>
// #include <iostream>
// define IMMEDIATE_VECTOR if you want to use imvector
#define immvector_max 1 << 30 

namespace std {

  // inline void swapptr(void * & a,void * & b){ register void * c=a; a=b; b=c; }
  inline unsigned _abs(int i){ return i>=0?(i==immvector_max?0:i):-i;}
  inline int nextpow2(int n){
    if (n>=16){
      return n>=64?n:(n>32?64:32);
    }
    else {
      return n>8?16:(n>4?8:4);
    }
  }

  template<class _Tp> class giac_reverse_pointer {
    _Tp * ptr;
  public:
  giac_reverse_pointer(): ptr(0) {}
  giac_reverse_pointer(_Tp *p): ptr(p) {}
  giac_reverse_pointer(const giac_reverse_pointer & p): ptr(p.ptr) {}
    giac_reverse_pointer operator +(int n){ return ptr-n; }
    giac_reverse_pointer operator -(int n){ return ptr+n; }
    giac_reverse_pointer & operator ++(){ --ptr; return *this; }
    giac_reverse_pointer & operator --(){ ++ptr; return *this; }
    _Tp & operator *() { return *ptr; }
    const _Tp operator *() const { return *ptr; }
    bool operator ==(const giac_reverse_pointer & other) const { return ptr==other.ptr; }
    bool operator !=(const giac_reverse_pointer & other) const { return ptr!=other.ptr; }
    int operator -(const giac_reverse_pointer & other) const { return other.ptr-ptr; }
  };

  // define IMMEDIATE_VECTOR to an integer>=1 for non-dynamical small vectors
#if defined(IMMEDIATE_VECTOR) 

#define _begin_immediate_vect _ptr[0]
#define _endalloc_immediate_vect _ptr[1]
  template<typename _Tp> class imvector{
    // private members
    int _taille; // <=0 for immediate, >0 for allocated, immvector_max for empty allocated
    union {
      _Tp *_ptr[2]; // ptr[0]==begin_immediate_vect, ptr[1]==endalloc_immediate_vect
      int _tab[IMMEDIATE_VECTOR];
    };
    // private allocation methods
    void _zero_tab(){
      for (unsigned i=0;i<IMMEDIATE_VECTOR;i++) 
	_tab[i]=0; 
    }
    void _free_tab(){
      for (unsigned i=0;i<(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp);++i){
	_Tp * Tptr = (_Tp *) _tab;
	*(Tptr+i)=_Tp();
      }
    }
    void _destroy(){
      if (_taille>0){ 
	if (_begin_immediate_vect) {
	  // std::cerr << "delete " << _taille << endl;
	  delete [] _begin_immediate_vect; 
	}
      }
      else
	_free_tab();
    }
    void _realloc(unsigned n){
      if (n<=(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp)){
	if (_taille!=immvector_max){
	  for (int i=n;i<_taille;i++){
	    *(_begin_immediate_vect+i)=_Tp();
	  }
	}
	return;
      }
      if (_taille<=0){ // dyn alloc the vector
	_taille=(_taille?-_taille:immvector_max);
	n=nextpow2(n);
	_Tp * _newbegin = new _Tp[n];
	if (_taille<immvector_max){
	  for (int i=0;i<_taille;++i){
	    _newbegin[i] = ((_Tp *)_tab)[i];
	  }
	}
	_free_tab();
	_begin_immediate_vect=_newbegin;
	_endalloc_immediate_vect=_begin_immediate_vect+n;
	return;
      }
      if ( _endalloc_immediate_vect-_begin_immediate_vect>=int(n) )
	return;
      n=nextpow2(n);
      _Tp * _newbegin = new _Tp[n];
      _Tp * _end_immediate_vect = _begin_immediate_vect+(_taille==immvector_max?0:_taille);
      for (_Tp * ptr=_begin_immediate_vect;ptr!=_end_immediate_vect;++ptr,++_newbegin){
	*_newbegin = *ptr;
      }
      _newbegin -= (_taille==immvector_max?0:_taille);
      if (_begin_immediate_vect)
	delete [] _begin_immediate_vect;
      _begin_immediate_vect=_newbegin;
      _endalloc_immediate_vect=_begin_immediate_vect+n;
    }
    void _alloc(unsigned n){
      _zero_tab();
      if (n<=(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp)){
	_taille=-int(n);
      }
      else {
	_taille=(n?n:immvector_max);
	n=nextpow2(n);
	_begin_immediate_vect = new _Tp[n];
	_endalloc_immediate_vect=_begin_immediate_vect+n;
      }
    }
    void _alloc_fill(const _Tp * b,const _Tp * e){
      unsigned n=e-b;
      if (n<=(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp)){
	_zero_tab();
	_taille = -int(n);
	for (unsigned i=0;i<n;++b,++i){
	  ((_Tp *) _tab)[i]=*b;
	}
      }
      else {
	_alloc(n); 
	unsigned i=0;
	for (_Tp * ptr=_begin_immediate_vect;i<n;++ptr,++b,++i){
	  *ptr = *b;
	}
      }
    }
  public:
    typedef _Tp value_type;
    typedef _Tp * pointer;
    typedef const _Tp * const_iterator;
    typedef pointer iterator;
    typedef giac_reverse_pointer<_Tp> reverse_iterator;
    typedef giac_reverse_pointer<const _Tp> const_reverse_iterator;
    
    imvector():_taille(0) { 
      _zero_tab();
    }
    ~imvector() { 
      _destroy();
    }
    imvector(unsigned n,const _Tp & value=_Tp()){
      _alloc(n); 
      _Tp * _end_immediate_vect=_taille>0?_begin_immediate_vect:(_Tp *)_tab;
      for (unsigned i=0;i<n;++_end_immediate_vect,++i){
	*_end_immediate_vect =value;
      }
    }
    imvector(const const_iterator & b,const const_iterator & e){
      _alloc_fill(b,e);
    }
    imvector(const imvector<_Tp> & w){
      const _Tp * ptr=w.begin();
      _alloc_fill(ptr,ptr+_abs(w._taille));
    }
    imvector<_Tp> & operator = (const imvector<_Tp> & w){
      if (this==&w)
	return *this;
      _Tp copie[IMMEDIATE_VECTOR];
      _Tp * wbeg;
      unsigned n=_abs(w._taille),i=0;
#ifdef NO_WORKAROUND
      if (w._taille<0){
	wbeg=copie;
	for (unsigned j=0;j<n;++j)
	  copie[j]=((_Tp *)w._tab)[j];
      }
      else {
	wbeg=w._begin_immediate_vect;
      }
#else
      // the loop should be inside the test below
      // we keep it outside as a workaround to keep a copy of the source when doing
      // something like v=*v.front()._VECTptr; where v=imvector<gen>
      for (unsigned j=0;j<n && j<IMMEDIATE_VECTOR;++j)
	copie[j]=w[j];
      if (w._taille<0){
	wbeg=copie;
      }
      else {
	wbeg=w._begin_immediate_vect;
      }
#endif
      _realloc(n);
      _taille=_taille<=0?-int(n):(n?n:immvector_max);
      _Tp * _end_immediate_vect=begin();
      for (const _Tp * ptr=wbeg;i<n;++ptr,++_end_immediate_vect,++i){
	*_end_immediate_vect = *ptr;
      }
      return *this;
    }
    iterator begin(){ return _taille>0?_begin_immediate_vect:((_Tp *) _tab); }
    iterator end(){ return _taille>0?_begin_immediate_vect+_abs(_taille):((_Tp *) _tab)-_taille;}
    const_iterator begin() const { return _taille>0?_begin_immediate_vect:((_Tp *) _tab); }
    const_iterator end() const { return _taille>0?_begin_immediate_vect+_abs(_taille):((_Tp *) _tab)-_taille;}
    reverse_iterator rbegin(){ return _taille>0?_begin_immediate_vect+_abs(_taille)-1:((_Tp *) _tab)-_taille-1; }
    reverse_iterator rend(){ return _taille>0?_begin_immediate_vect-1:((_Tp *) _tab)-1;}
    const_reverse_iterator rbegin() const { return _taille>0?_begin_immediate_vect+_abs(_taille)-1:((_Tp *) _tab)-_taille-1; }
    const_reverse_iterator rend() const { return _taille>0?_begin_immediate_vect-1:((_Tp *) _tab)-1;}
    unsigned size() const { return _abs(_taille); }
    unsigned capacity() const { return _taille<0?(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp):_endalloc_immediate_vect-_begin_immediate_vect;}
    _Tp & front() { return *begin(); }
    _Tp & back() { return *rbegin(); }
    _Tp & operator [](unsigned i) { return *(begin()+i); }
    const _Tp & front() const { return *begin(); }
    const _Tp & back() const { return *rbegin(); }
    const _Tp & operator [](unsigned i) const { return *(begin()+i); }
    void push_back(const _Tp & p0){ 
      _Tp p(p0); 
      // create a copy since p0 may be scratched 
      // if p0 is a vector element and the vector is realloced
      if (_taille<=0){ 
	if (unsigned(-_taille)<(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp)){
	  ((_Tp *) _tab)[-_taille]=p;
	  --_taille;
	  return;
	}
	_realloc(_taille?2*_abs(_taille):1);
      }
      if (_endalloc_immediate_vect==_begin_immediate_vect+_abs(_taille))
	_realloc(_abs(_taille)?2*_taille:1);
      *(_begin_immediate_vect+_abs(_taille))=p;
      if (_taille==immvector_max) _taille=1; else ++_taille;
    }
    _Tp pop_back(){ 
      if (_taille<=0){
	if (_taille) ++_taille;
	_Tp res=*( ((_Tp *) _tab) -_taille);
	*(((_Tp *) _tab)-_taille)=_Tp();
	return res;
      }
      --_taille; 
      if (_taille){
	_Tp res=*(_begin_immediate_vect+_taille); 
	*(_begin_immediate_vect+_taille)=_Tp();
	return res;
      }
      _Tp res=*_begin_immediate_vect;
      delete [] _begin_immediate_vect;
      _zero_tab();
      return res;
    }
    void clear(){ 
      if (_taille>0 &&_begin_immediate_vect){
	delete [] _begin_immediate_vect; 
	_zero_tab();
      }
      else {
	if (_taille<0)
	  _free_tab();
      }
      _taille=0;
    }
    bool empty() const { return _taille==0 || _taille==immvector_max; }
    void reserve(unsigned n){ if (_abs(_taille)<n) _realloc(n); }
    void resize(unsigned n,const _Tp &value=_Tp()){ 
      if (_taille!=immvector_max && _abs(_taille)>=n) {
	// clear elements from _begin()+n to _end()
	_Tp * ptr = begin()+n;
	for (;ptr!=end();++ptr)
	  *ptr=value;
	_taille=_taille>0?(n?n:immvector_max):-int(n);
      }
      else {
	unsigned prev=_taille==immvector_max?0:_abs(_taille);
	_realloc(n);
	_Tp * ptr=begin()+prev;
	for (unsigned i=prev;i<n;++ptr,++i){
	  *ptr=value;
	}
	if (_taille<=0) _taille=-int(n); else _taille=n?n:immvector_max;
      }
    }
    void erase(_Tp * b,_Tp * e){
      unsigned decal=e-b;
      if (!decal || _taille==0 || _taille==immvector_max)
	return;
      if (decal>=_abs(_taille)){
	clear();
	return;
      }
      _Tp * _end_immediate_vect=end();
      for (_Tp * ptr=e;ptr!=_end_immediate_vect;++ptr){
	*(ptr-decal)=*ptr;
	*ptr = _Tp();
      }
      if (_taille<0) { 
	_taille += decal; 
      }
      else {
	_taille -= decal;
	if (!_taille) _taille=immvector_max;
      }
    }
    void erase(_Tp * b){
      erase(b,b+1);
    }
    _Tp * insert(_Tp * b, const _Tp& x ){
      if (_taille==0){
	push_back(x);
	return begin();
      }
      if (_taille<=0){
	if (unsigned(-_taille)<(sizeof(int)*IMMEDIATE_VECTOR)/sizeof(_Tp)){
	  --_taille;
	  for (_Tp * ptr=((_Tp *) _tab)-_taille-1;ptr!=b;--ptr){
	    *ptr=*(ptr-1);
	  }
	  *b=x;
	  return b;
	}
	int pos=b-((_Tp *) _tab);
	_realloc(_abs(_taille)?2*(-_taille):1);
	b=_begin_immediate_vect+pos;
      }
      if (int(_abs(_taille))==_endalloc_immediate_vect-_begin_immediate_vect){
	int pos=b-_begin_immediate_vect;
	_realloc(_abs(_taille)?2*_taille:1);
	b=_begin_immediate_vect+pos;
      }
      if (_taille==immvector_max) _taille=1; else ++_taille;
      for (_Tp * ptr=_begin_immediate_vect+_abs(_taille)-1;ptr!=b;--ptr){
	*ptr=*(ptr-1);
      }
      *b=x;
      return b;
    }
    void insert(_Tp * b, unsigned k,const _Tp& x ){
      if (_taille<=0){
	int pos=b-((_Tp *) _tab);
	_realloc(k+IMMEDIATE_VECTOR);
	b=_begin_immediate_vect+pos;
      }
      if (_endalloc_immediate_vect-_begin_immediate_vect<int(_abs(_taille)+k)){
	unsigned pos=b-_begin_immediate_vect;
	_realloc(_abs(_taille)?2*_taille:1);
	b=_begin_immediate_vect+pos;
      }
      if (_taille==immvector_max) _taille=k; else _taille+=k;
      for (_Tp * ptr=_begin_immediate_vect+_abs(_taille)-k;ptr!=b;){
	--ptr;
	*(ptr+k)=*ptr;
      }
      for (unsigned j=0;j<k;++b,++j){
	*b=x;
      }
    }
    void swap(imvector<_Tp> & w){
      char ch[sizeof(imvector<_Tp>)];
      memcpy((void *)ch,(void *)&w,sizeof(imvector<_Tp>));
      memcpy(&w,this,sizeof(imvector<_Tp>));
      memcpy(this,(void *)ch,sizeof(imvector<_Tp>));
    }
    void assign(unsigned n,const _Tp & value=_Tp()){
      _realloc(n);
      _taille=_taille>0?(n?n:immvector_max):-n;
      unsigned i=0;
      for (_Tp * ptr=_begin_immediate_vect;i<n;++ptr,++i){
	*ptr=value;
      }
    }
    void assign(const _Tp * b,const _Tp * e){
      unsigned n=e-b;
      _realloc(n);
      _taille=_taille>0?(n?n:immvector_max):-n;
      for (_Tp * ptr=_begin_immediate_vect;b!=e;++b,++ptr){
	*ptr=*b;
      }
    }
    unsigned max_size() const {
      return (1 << 30) -1;
    }
    _Tp & at(unsigned n){
      if (n>_abs(_taille))
	return _Tp(); // should be defined somewhere else
      return *(begin()+n);
    }
    const _Tp at(unsigned n) const {
      if (n>_abs(_taille))
	return _Tp(); // should be defined somewhere else
      return *(begin()+n);
    }
  };

  template<typename _Tp>
    inline bool operator==(const imvector<_Tp>& __x, const imvector<_Tp>& __y){ 
    if (__x.size() != __y.size())
      return false;
    const _Tp * xend=__x.end();
    for (const _Tp * xptr=__x.begin(), * yptr=__y.begin();xptr!=xend;++yptr,++xptr){
      if (*xptr!=*yptr)
	return false;
    }
    return true;
  }

  template<typename _Tp>
    inline bool operator < (const imvector<_Tp>& __x, const imvector<_Tp>& __y){ 
    if (__x.size() != __y.size())
      return __x.size()<__y.size();
    const _Tp * xend=__x.end();
    for (const _Tp * xptr=__x.begin(), * yptr=__y.begin();xptr!=xend;++yptr,++xptr){
      if (*xptr!=*yptr)
	return *xptr<*yptr;
    }
    return false;
  }

  template<typename _Tp>
    void swap(imvector<_Tp>& __x,imvector<_Tp>& __y){
    __x.swap(__y);
  }
#else // IMMEDIATE_VECTOR

#define imvector vector

#endif // IMMEDIATE_VECTOR
} // namespace std

#ifndef GIAC_VECTOR
#include <vector>
#else

namespace std {
  template<typename _Tp> class vector{
    // private members
    _Tp * _begin,*_end,*_endalloc;
    // private allocation methods
    void _realloc(unsigned n){
      if (_endalloc-_begin>=int(n))
	return;
      unsigned old=_end-_begin;
      _Tp * _newbegin = new _Tp[n];
      for (_Tp * ptr=_begin;ptr!=_end;++ptr,++_newbegin){
	*_newbegin = *ptr;
      }
      _newbegin -= old;
      if (_begin)
	delete [] _begin;
      _begin=_newbegin;
      _end=_begin+old;
      _endalloc=_begin+n;
    }
    void _alloc(unsigned n){
      _end=_begin = new _Tp[n];
      _endalloc=_begin+n;
    }
    void _alloc_fill(const _Tp * b,const _Tp * e){
      unsigned n=e-b;
      _alloc(n); 
      for (_Tp * ptr=_begin;ptr!=_endalloc;++ptr,++b){
	*ptr = *b;
      }
      _end = _begin+ n;
    }
  public:
    typedef _Tp value_type;
    typedef _Tp * pointer;
    typedef const _Tp * const_iterator;
    typedef pointer iterator;
    typedef giac_reverse_pointer<_Tp> reverse_iterator;
    typedef giac_reverse_pointer<const _Tp> const_reverse_iterator;
    
  vector():_begin(0),_end(0),_endalloc(0) {}
    ~vector() { if (_begin) delete [] _begin; }
    vector(unsigned n,const _Tp & value=_Tp()){
      _alloc(n); 
      for (;_end!=_endalloc;++_end){
	*_end =value;
      }
    }
    vector(const const_iterator & b,const const_iterator & e){
      _alloc_fill(b,e);
    }
    vector(const vector<_Tp> & w){
      _alloc_fill(w._begin,w._end);
    }
    vector<_Tp> & operator = (const vector<_Tp> & w){
      if (this==&w)
	return *this;
      unsigned n=w._end-w._begin;
      _realloc(n);
      _end=_begin;
      for (_Tp * ptr=w._begin;ptr!=w._end;++_end,++ptr){
	*_end = *ptr;
      }
      return *this;
    }
    iterator begin(){ return _begin; }
    iterator end(){ return _end;}
    const_iterator begin() const { return _begin; }
    const_iterator end() const { return _end;}
    reverse_iterator rbegin(){ return _end-1; }
    reverse_iterator rend(){ return _begin-1;}
    const_reverse_iterator rbegin() const { return _end-1; }
    const_reverse_iterator rend() const { return _begin-1;}
    unsigned size() const { return _end-_begin; }
    unsigned capacity() const { return _endalloc-_begin;}
    _Tp & front() { return *_begin; }
    _Tp & back() { return *(_end-1); }
    _Tp & operator [](unsigned i) { return *(_begin+i); }
    const _Tp & front() const { return *_begin; }
    const _Tp & back() const { return *(_end-1); }
    const _Tp & operator [](unsigned i) const { return *(_begin+i); }
    void push_back(const _Tp & p){ 
      if (_endalloc==_end){
	unsigned n = _end-_begin;
	_realloc(n?2*n:2);
      } 
      *_end=p; 
      ++_end; 
    }
    _Tp pop_back(){ --_end; return *_end; }
    void clear(){ _end=_begin;}
    bool empty() const { return _end==_begin; }
    void reserve(unsigned n){ if (_endalloc-_begin<int(n)) _realloc(n); }
    void resize(unsigned n,const _Tp &value=_Tp()){ 
      if (_end-_begin>=(int)n) _end=_begin+n;
      else {
	_realloc(n);
	for (;_end!=_endalloc;++_end){
	  *_end=value;
	}
      }
    }
    void erase(_Tp * b,_Tp * e){
      unsigned decal=e-b;
      if (!decal)
	return;
      for (_Tp * ptr=e;ptr!=_end;++ptr){
	*(ptr-decal)=*ptr;
      }
      _end -= decal;
    }
    void erase(_Tp * b){
      erase(b,b+1);
    }
    _Tp * insert(_Tp * b, const _Tp& x ){
      if (_endalloc==_end){
	unsigned pos=b-_begin;
	unsigned n = _end-_begin;
	_realloc(n?2*n:2);
	b=_begin+pos;
      }
      ++_end;
      for (_Tp * ptr=_end-1;ptr!=b;--ptr){
	*ptr=*(ptr-1);
      }
      *b=x;
      return b;
    }
    void insert(_Tp * b, unsigned k,const _Tp& x ){
      if (_endalloc<_end+k){
	unsigned pos=b-_begin;
	unsigned n = _end-_begin;
	_realloc(n>k?2*n:n+k);
	b=_begin+pos;
      }
      _end += k ;
      for (_Tp * ptr=_end-k;ptr!=b;){
	--ptr;
	*(ptr+k)=*ptr;
      }
      for (unsigned j=0;j<k;++b,++j){
	*b=x;
      }
    }
    void swap(vector<_Tp> & w){
      _Tp * tmp=_begin; _begin=w._begin; w._begin=tmp;
      tmp=_end; _end=w._end; w._end=tmp;
      tmp=_endalloc; _endalloc=w._endalloc; w._endalloc=tmp;
    }
    void assign(unsigned n,const _Tp & value=_Tp()){
      _realloc(n);
      _end = _begin +n;
      for (_Tp * ptr=_begin;ptr!=_end;++ptr){
	*ptr=value;
      }
    }
    void assign(const _Tp * b,const _Tp * e){
      unsigned n=e-b;
      _realloc(n);
      _end = _begin +n;
	for (_Tp * ptr=_begin;b!=e;++b,++ptr){
	*ptr=*b;
      }
    }
    unsigned max_size() const {
      return 1 << 30;
    }
    _Tp & at(unsigned n){
      if (n>_end-_begin)
	return _Tp(); // should be defined somewhere else
      return *(_begin+n);
    }
    const _Tp at(unsigned n) const {
      if (n>_end-_begin)
	return _Tp(); // should be defined somewhere else
      return *(_begin+n);
    }
  };

  template<typename _Tp>
    inline bool operator==(const vector<_Tp>& __x, const vector<_Tp>& __y){ 
    if (__x.size() != __y.size())
      return false;
    for (const _Tp * xptr=__x.begin(), * yptr=__y.begin();xptr!=__x.end();++yptr,++xptr){
      if (*xptr!=*yptr)
	return false;
    }
    return true;
  }

  template<typename _Tp>
    inline bool operator < (const vector<_Tp>& __x, const vector<_Tp>& __y){ 
    if (__x.size() != __y.size())
      return __x.size()<__y.size();
    for (const _Tp * xptr=__x.begin(), * yptr=__y.begin();xptr!=__x.end();++yptr,++xptr){
      if (*xptr!=*yptr)
	return *xptr<*yptr;
    }
    return false;
  }

  template<typename _Tp>
    inline bool operator!=(const _Tp & __x, const _Tp & __y){
    return !(__x==__y);
  }

  template<typename _Tp>
    inline bool operator > (const _Tp & __x, const _Tp & __y){
    return __y<__x;
  }

  template<typename _Tp>
    inline bool operator >= (const _Tp & __x, const _Tp & __y){
    return !(__x<__y);
  }

  template<typename _Tp>
    inline bool operator <= (const _Tp & __x, const _Tp & __y){
    return !(__y<__x);
  }

}

#endif

#endif // GIAC_VECTOR_H
