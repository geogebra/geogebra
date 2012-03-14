module torder; % Term order modes for distributive polynomials.
               % H. Melenk, ZIB Berlin.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


% The routines of this module should be coded as efficiently as
% possible.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%   switching between term order modes: TORDER statement.
%

global!-dipvars!*:='(list);

symbolic procedure torder u;
  begin scalar oldmode,oldex,oldvars,w;
   oldmode:=vdpsortmode!*; oldex:=vdpsortextension!*;
   oldvars:=global!-dipvars!*;
   global!-dipvars!*:='(list);
 a:
   w:=reval car u; u:=cdr u;
   if eqcar(w,'list) and null u then<<u:=cdr w; goto a>>;
   if eqcar(w,'list) then
   <<global!-dipvars!*:=w; w:=reval car u; u:=cdr u>>;
   vdpsortmode!*:=w;
%  dipsortevcomp!*:=get(w, 'evcomp);
   vdpsortextension!*:=for each x in u join
     (if eqcar(x:=reval x,'list) then cdr x else {x});
   if flagp(vdpsortmode!*,'dipsortextension) and null vdpsortextension!*
     then rederr "term order needs additional parameter(s)";
   return 'list . oldvars . oldmode . oldex end ;

remprop('torder,'number!-of!-args);

put('torder,'psopfn,'torder);

symbolic procedure dipsortingmode u;
%   Sets the exponent vector sorting mode. Returns the previous mode
    begin scalar x,z;
       if not idp u or not flagp(u,'dipsortmode)
            then return typerr(u,"term ordering mode");
       x:=dipsortmode!*; dipsortmode!*:=u;
           % saves thousands of calls to GET;
       dipsortevcomp!*:=get(dipsortmode!*,'evcomp);
       if not getd dipsortevcomp!* then
          rerror(dipoly,2,
                 "No compare routine for term order mode found");
       if (z:=get(dipsortmode!*,'evcompinit)) then apply(z,nil);
       if (z:=get(dipsortmode!*,'evlength)) and z neq length dipvars!*
          then rederr
                  "wrong variable number for fixed length term order";
       vdplastvar!*:=length dipvars!* ;
       return x end ;

flag('(lex gradlex revgradlex),'dipsortmode);

put('lex,'evcomp,'evlexcomp);

put('gradlex,'evcomp,'evgradlexcomp);

put('revgradlex,'evcomp,'evrevgradlexcomp);

symbolic procedure evcompless!?(e1,e2);
%    Exponent vector compare less. e1, e2 are exponent vectors
%    in some order. Evcompless? is a boolean function which returns
%    true if e1 is ordered less than e2 .
%    Mapped to evcomp .
    1=evcomp(e2,e1) ;

symbolic procedure evcomp (e1,e2);
%   Exponent vector compare. e1, e2 are exponent vectors in some
%    order.  Evcomp(e1,e2) returns the digit 0 if exponent vector e1 is
%    equal exponent vector e2, the digit 1 if e1 is greater than e2,
%    else the digit -1. This function is assigned a value by the
%    ordering mechanism, so is dummy for now .
% IDapply would be better here, but is not within standard LISP!
   apply(dipsortevcomp!*,list(e1,e2)) ;

symbolic procedure evlexcomp (e1,e2);
%    Exponent vector lexicographical compare. The
%    exponent vectors e1 and e2 are in lexicographical
%    ordering. evLexComp(e1,e2) returns the digit 0 if exponent
%    vector e1 is equal exponent vector e2, the digit 1 if e1 is
%    greater than e2, else the digit -1.
   if null e1 then 0
    else if null e2 then evlexcomp(e1,'(0))
    else if car e1 #= car e2 then evlexcomp(cdr e1,cdr e2)
    else if car e1 #> car e2 then 1
    else -1 ;

symbolic procedure evinvlexcomp (e1,e2);
%   Exponent vector inverse lexicographical compare .
   if null e1 then
      if null e2 then 0 else evinvlexcomp('(0),e2)
    else if null e2 then evlexcomp(e1,'(0))
    else if car e1 #= car e2 then evinvlexcomp(cdr e1,cdr e2)
    else (if not(n#=0) then n
%         else if car e2 #= car e1 then 0
          else if car e2 #> car e1 then 1
          else -1) where n=evinvlexcomp(cdr e1,cdr e2);

symbolic procedure evgradlexcomp (e1,e2);
%    Exponent vector graduated lex compare.
%    The exponent vectors e1 and e2 are in graduated lex
%    ordering. evGradLexComp(e1,e2) returns the digit 0 if exponent
%    vector e1 is equal exponent vector e2, the digit 1 if e1 is
%    greater than e2, else the digit -1.
   if null e1 then 0
    else if null e2 then evgradlexcomp(e1,'(0))
    else if car e1 #= car e2 then evgradlexcomp(cdr e1, cdr e2)
    else (if te1#=te2 then if car e1 #> car e2 then 1 else -1
          else if te1 #> te2 then 1 else -1)
          where te1=evtdeg e1, te2=evtdeg e2;

symbolic procedure evrevgradlexcomp (e1,e2);
%    Exponent vector reverse graduated lex compare.
%    The exponent vectors e1 and e2 are in reverse graduated lex
%    ordering. evRevGradLexcomp(e1,e2) returns the digit 0 if exponent
%    vector e1 is equal exponent vector e2, the digit 1 if e1 is
%    greater than e2, else the digit -1.
   if null e1 then 0
    else if null e2 then evrevgradlexcomp(e1,'(0))
    else if car e1 #= car e2 then evrevgradlexcomp(cdr e1, cdr e2)
    else (if te1 #= te2 then evinvlexcomp(e1,e2)
          else if te1 #> te2 then 1 else -1)
          where te1=evtdeg e1, te2=evtdeg e2;

symbolic procedure evtdeg e1;
%    Exponent vector total degree. e1 is an exponent vector.
%    evtdeg(e1) calculates the total degree of the exponent
%    e1 and returns an integer.
   (<<while e1 do <<x:=car e1 #+ x; e1:=cdr e1>>; x>>) where x=0;

% The following section contains additional term order modes.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  gradlexgradlex
%
%  this order can have several steps
%      torder gradlexgradlex,3,2,4;
%

flag ('(gradlexgradlex),'dipsortmode);
flag ('(gradlexgradlex),'dipsortextension);
put('gradlexgradlex,'evcomp,'evgradgradcomp);

symbolic procedure evgradgradcomp (e1,e2);
      evgradgradcomp1 (e1,e2,car vdpsortextension!*,
                             cdr vdpsortextension!*);

symbolic procedure evgradgradcomp1 (e1,e2,n,nl);
   if null e1 then 0
    else if null e2 then evgradgradcomp1(e1,'(0),n,nl)
    else if n#=0 then if null nl then evgradlexcomp(e1,e2)
                   else evgradgradcomp1 (e1,e2,car nl,cdr nl)
    else if car e1 #= car e2 then
              evgradgradcomp1(cdr e1,cdr e2,n#-1,nl)
    else (if te1 #= te2 then if car e1 #> car e2 then 1 else -1
           else if te1 #> te2 then 1 else -1)
          where te1=evpartdeg(e1,n), te2=evpartdeg(e2,n);

symbolic procedure evpartdeg(e1,n); evpartdeg1(e1,n,0);

symbolic procedure evpartdeg1(e1,n,sum);
     if n #= 0 or null e1 then sum
      else evpartdeg1(cdr e1,n #-1, car e1 #+ sum);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  gradlexrevgradlex
%
%

flag ('(gradlexrevgradlex),'dipsortmode);
flag ('(gradlexrevgradlex),'dipsortextension);
put('gradlexrevgradlex,'evcomp,'evgradrevgradcomp);

symbolic procedure evgradrevgradcomp (e1,e2);
      evgradrevgradcomp1 (e1,e2,car vdpsortextension!*);

symbolic procedure evgradrevgradcomp1 (e1,e2,n);
   if null e1 then 0
    else if null e2 then evgradrevgradcomp1(e1,'(0),n)
    else if n#=0 then evrevgradlexcomp(e1,e2)
    else if car e1 #= car e2 then evgradrevgradcomp1(cdr e1,cdr e2,n#-1)
    else (if te1 #= te2 then if car e1 #< car e2 then 1 else -1
           else if te1 #> te2 then 1 else -1)
          where te1=evpartdeg(e1,n), te2=evpartdeg(e2,n);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  LEXGRADLEX
%
%

flag ('(lexgradlex),'dipsortmode);
flag ('(lexgradlex),'dipsortextension);
put('lexgradlex,'evcomp,'evlexgradlexcomp);

symbolic procedure evlexgradlexcomp (e1,e2);
      evlexgradlexcomp1 (e1,e2,car vdpsortextension!*);

symbolic procedure evlexgradlexcomp1 (e1,e2,n);
   if null e1 then (if evzero!? e2 then 0 else -1)
    else if null e2 then evlexgradlexcomp1(e1,'(0),n)
    else if n#=0 then evgradlexcomp(e1,e2)
    else if car e1 #= car e2 then evlexgradlexcomp1(cdr e1,cdr e2,n#-1)
    else if car e1 #> car e2 then 1 else -1;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  LEXREVGRADLEX
%
%

flag ('(lexrevgradlex),'dipsortmode);
flag ('(lexrevgradlex),'dipsortextension);
put('lexrevgradlex,'evcomp,'evlexrevgradlexcomp);

symbolic procedure evlexrevgradlexcomp (e1,e2);
      evlexrevgradlexcomp1 (e1,e2,car vdpsortextension!*);

symbolic procedure evlexrevgradlexcomp1 (e1,e2,n);
   if null e1 then (if evzero!? e2 then 0 else -1)
    else if null e2 then evlexrevgradlexcomp1(e1,'(0),n)
    else if n#=0 then evrevgradlexcomp(e1,e2)
    else if car e1 #= car e2 then
                   evlexrevgradlexcomp1(cdr e1,cdr e2,n#-1)
    else if car e1 #> car e2 then 1 else -1;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  WEIGHTED
%
%

flag ('(weighted),'dipsortmode);
flag ('(weighted),'dipsortextension);
put('weighted,'evcomp,'evweightedcomp);

symbolic procedure evweightedcomp (e1,e2);
     (if dg1 #= dg2 then evlexcomp(e1,e2) else
      if dg1 #> dg2 then 1 else -1
       ) where dg1=evweightedcomp2(0,e1,vdpsortextension!*),
               dg2=evweightedcomp2(0,e2,vdpsortextension!*);

symbolic procedure evweightedcomp1 (e,w); evweightedcomp2(0, e, w);

symbolic procedure evweightedcomp2 (n,e,w);
  % scalar product of exponent and weight vector
   if null e then n else
   if null w then evweightedcomp2(n, e, '(1 1 1 1 1)) else
   if car w=0 then evweightedcomp2(n, cdr e, cdr w) else
   if car w=1 then evweightedcomp2(n #+ car e, cdr e, cdr w) else
   evweightedcomp2(car e #* car w #+ n, cdr e, cdr w);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  GRADED term order
%     cascading a graded sorting with another term order.
%
%  The grade of a term is defined as a scalar product of the exponent
%  vector and a grade vector which contains non-negative integers.
%  In contrast to a weight vector the grade vector may contain also
%  zeros. A vector of ones is used if no vector is given explicitly.
%

fluid '(gradedrec!*);

flag ('(graded),'dipsortmode);
flag ('(graded),'dipsortextension);
put('graded,'evcomp,'evgradedcomp);
put('graded,'evcompinit,'evgradedinit);

symbolic procedure evgradedinit();
  begin scalar w,gvect,vse;
    vse:=vdpsortextension!*;
    while pairp vdpsortextension!* and numberp car vdpsortextension!*
      do <<gvect:=car vdpsortextension!* . gvect;
           vdpsortextension!*:=cdr vdpsortextension!*>>;
    if vdpsortextension!* then
       <<w:=car vdpsortextension!*;
         vdpsortextension!*:=cdr vdpsortextension!*>>
      else w:='lex;
    dipsortingmode w;
    gradedrec!*:={reversip gvect,dipsortevcomp!*,vdpsortextension!*};
    dipsortevcomp!*:='evgradedcomp;
    dipsortmode!*:='graded;
    vdpsortextension!*:=vse end;

symbolic procedure evgradedcomp (e1,e2);
     (if dg1 #= dg2 then
           apply(cadr gradedrec!*,{e1,e2})
              where vdpsortextension!*=caddr gradedrec!*
        else
      if dg1 #> dg2 then 1 else -1
       ) where dg1=ev!-gamma e1,  dg2=ev!-gamma e2;

symbolic procedure ev!-gamma(ev);
  % compute the grade of an exponent vector;
    evweightedcomp1 (ev,
         if dipsortmode!*='graded then car gradedrec!* else
         if dipsortmode!*='weighted then vdpsortextension!* else nil);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% MATRIX
%
%

% In the following routines I assume that 99 percent of the matrix
% entries will be 0 or 1 such that the special branches for these
% numbers makes sense. We save lots of memory read and
% multiplication is needed only entries other than 0 and 1.
%
% I could do the same optimization step for -1, but I don't
% expect that many people will use term orders with negative
% numbers.
%
% This package includes a compilation mode for matrix term orders
% for fixed length variable lists. Compilation is done implicilty
% when *comp is on, or explicitly by callint torder_compile.

flag ('(matrix),'dipsortmode);
flag ('(matrix),'dipsortextension);
put('matrix,'evcomp,'evmatrixcomp);
put('matrix,'evcompinit,'evmatrixinit);

symbolic procedure evmatrixcomp(e1,e2); evmatrixcomp1(e1,e2,vdpmatrix!*);

symbolic procedure evmatrixcomp1(e1,e2,m);
   if null m then 0 else
   (if w1 #= w2 then evmatrixcomp1(e1,e2,cdr m) else % #=
    if w1 #> w2 then 1 else -1)
  where
    w1= evmatrixcomp2 (e1,car m,0),
    w2= evmatrixcomp2 (e2,car m,0);

symbolic procedure evmatrixcomp2(e,l,w);
   if null e or null l then w else
  (if l1 #= 0 then
      evmatrixcomp2(cdr e,cdr l,w) else
   if l1 #= 1 then
      evmatrixcomp2(cdr e,cdr l,w #+ car e)
   else evmatrixcomp3(e,l1,l,w)) where l1=car l;

symbolic procedure evmatrixcomp3(e,l1,l,w);
   evmatrixcomp2(cdr e,cdr l,w #+ car e #* l1);

symbolic procedure evmatrixinit1(w,mode);
  begin scalar m,mm;
   if not eqcar(w,'mat) or
      mode and length cadr w neq length dipvars!* then
           typerr(w,"term order matrix for". dipvars!*);
   for each row in cdr w do
    <<row:=for each c in row collect ieval c;
      mm:=row . mm;
      row:=reverse row;
      while eqcar(row,0) do row:=cdr row;
      m:=reversip row . m>>;
   m:=reversip m; mm:=reversip mm;
   if m neq vdpmatrix!* then
    <<if length cadr w > length cdr w then
           lprim "Warning: non-square matrix used in torder"
      else if 0=reval{'det,w} then
           typerr(w,"term order (singular matrix)");
      if not evmatrixcheck mm then
           typerr(w,"term order (non admissible)")
     >>;
   return m end;

symbolic procedure evmatrixinit();
  begin scalar c,m,w;
   w:=reval car vdpsortextension!*;
   m:=evmatrixinit1(w,t);
   if (c:=assoc(m,compiled!-orders!*)) then
      dipsortevcomp!*:=cdr c else
    if !*comp then dipsortevcomp!*:=evmatrixcompile m;
   vdpmatrix!*:=m end;

symbolic procedure evmatrixcheck m;
 % Check the usability of the term order matrix: the
 % top elements of each column must be positive. This
 % approach goes back to a recommendation of J. Apel.
  begin scalar bad,c,w;
    integer i,j,r;
      r:=length m;
      for i:=1:length car m do
      <<c:=nil;
        for j:=1:r do
          if (w:=nth(nth(m,j),i)) neq 0 and null c then
           <<c:=t; bad:=w < 0>>
       >>;
      return not bad end;

symbolic procedure evmatrixcompile m;
  begin scalar w;
    w:= evmatrixcompile1 m;
    putd(car w,'expr,caddr w);
    compiled!-orders!*:=(m.car w).compiled!-orders!*;
    return car w end;

symbolic procedure evmatrixcompile1 m;
  begin scalar c,n,x,w,lvars,code;
     integer ld,p,k;
    for each row in m do k:=max(k,length row);
    lvars:=for i:=1:k collect gensym();
    code:={{'setq,car lvars,
                '(idifference (car e1) (car e2))}};
    ld:=1;
    for each row in m do
    <<p:=0; w:=nil;
      while row do
      <<c:=car row; row:=cdr row; p:=p+1;
        if c neq 0 then
        << % load the differences up to the current point.
          for i:=ld+1:p do
          <<  code:= append(code,'((setq e1(cdr e1))(setq e2(cdr e2))));
              code:=append(code,
                 {{'setq,nth(lvars,i),
                      '(idifference (car e1) (car e2))}});
              ld:=i; >>;
          % collect the terms of the row sum
        x:=nth(lvars,p);
        if c=-1 then x:={'iminus,x} else
        if c neq 1 then x:={'itimes,c,x};
        w:=if w then {'iplus2,x,w} else x >>;
      >>;
      if not atom w then
      <<code:=append(code,{{'setq,'w,w}});w:='w>>;
      code:=append(code,
       {{'cond,{{'iequal,w,0},t},
               {{'igreaterp,w,0},'(return 1)},
               '(t (return -1))}}); >>;
     % common trailor
    code:=append(code,'((return 0)));
    n:=gensym();
    return {n,k,evform {'lambda,'(e1 e2), 'prog.('w.lvars). code}} end;

symbolic procedure evform(u);
  % Let form play on the generated code.
    form1(u,nil,'symbolic);

symbolic procedure torder_compile_form(w,c,m);
  begin scalar n;
     if length w < 3 then rederr "illegal arguments";
     m:=evmatrixinit1(eval form caddr w,nil);
     c:=evmatrixcompile1 m;
     n:=eval form cadr w;
     return
      {'progn,
        {'putd,mkquote n,mkquote 'expr,mkquote caddr c},
        {'setq,'compiled!-orders!*,
             {'cons,{'cons,mkquote m,mkquote n}, 'compiled!-orders!*}},
        {'put,mkquote n,''evcomp,mkquote n},
        {'put,mkquote n,''evlength,cadr c},
        {'flag,mkquote{n},''dipsortmode}, mkquote n} end;

put('torder_compile,'formfn,'torder_compile_form);

endmodule;;end;
