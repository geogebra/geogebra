% ----------------------------------------------------------------------
% $Id: ofsftfc.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2003-2009 Andreas Dolzmann and Lorenz Gilch
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(ofsf_tfc_rcsid!* ofsf_tfc_copyright!*);
   ofsf_tfc_rcsid!* :=
      "$Id: ofsftfc.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   ofsf_tfc_copyright!* := "Copyright (c) 2003-2009 A. Dolzmann and L. Gilch"
>>;

module ofsftfc;
% Type Formula Construction.

procedure ofsf_tfcmain(l,y);
   % Type formula construction. [l] is a list of SQ's, [y] is an
   % identi1fier. Returns a type formula, i.e. a formula that is true
   % if the product of the input polynomials has type 0 wrt. the main
   % variable.
   begin scalar elem,d;
      elem := reorder numr car l;
      d := if domainp elem or mvar elem neq y then
	 0
      else
	 ldeg elem;
      return ofsf_tfcmain1(l,d,y);
   end;

procedure ofsf_tfcmain1(l,d,y);
   % Subroutine of ofsf_tfcmain. [l] is a list of SQ's, [d] is a positive
   % integer, [y] is an identifier. Returns a formula.
   begin scalar cl,res;
      if null cdr l or (length l)*d < 8 or not !*rlhqetfcsplit then <<
	 cl := reversip ofsf_coefflist(ofsf_multfl l,y);
	 res := ofsf_tfc cl
      >> else if !*rlhqetfcfullsplit then
	 res := ofsf_splittypes(l,d,y,0)
      else
      	 res := ofsf_split(l,d,y);
      return res;
   end;

procedure ofsf_split(l,d,y);
   % Split Type formula computation. [l] is a list of SQ's, [d] is a positive
   % integer, [y] is an identifier. Returns a formula.
   begin scalar l1,l2, res,res2;
      l2 := ofsf_splitlist l;
      l1 := car l2;
      l2 := cadr l2;
      %oldorder := setkorder {y};
      res := for i:=1:d collect
	 rl_smkn('and,{ofsf_tfk(reversip ofsf_coefflist(ofsf_multfl l1,y),i),
	    ofsf_tfk(reversip ofsf_coefflist(ofsf_multfl l2,y),-i)});
      %setkorder oldorder;
      res2 := rl_smkn('and,{ofsf_tfcmain1(l1,d,y),ofsf_tfcmain1(l2,d,y)});
      return rl_smkn('or,res2 . res)
   end;

procedure ofsf_multfl(l);
   % Computatipn of product of the elements of a list. [l] is a list of SQ's.
   % Returns a SQ:
   if null l then
      simp 1
   else
      multsq(car l, ofsf_multfl(cdr l));

procedure ofsf_splitlist(l);
   % Splittling a list into two lists with same cardinality. [l] is a list.
   % Returns a list of two lists.
   begin scalar l1,l2,ll;
      ll := l;
      while not null ll do <<
	 l1 := (car ll) . l1;
	 l2 := (cadr ll) . l2;
	 ll := cddr ll
      >>;
      return {l1,l2}
   end;

procedure ofsf_tfc(coeffl);
   % Type Formula Construction. [coeffl] is a list of SQ's.
   % Returns a quantifier-free formula.
   ofsf_tfc1(length coeffl - 1,coeffl);

procedure ofsf_tfc1(n,coeffl);
   % Type Formula Construction. [n] is an integer, [coeffl] is a list of SQ's.
   % Returns a quantifier-free formula.
   if n = 1 then
      ofsf_0mk2('equal, numr car coeffl)
   else if n = 2 then
      rl_smkn('or,{ofsf_0mk2('equal, numr cadr coeffl),
	 ofsf_0mk2('lessp, ofsf_norm car coeffl)})
   else if evenp n then
      ofsf_tfceven(n,coeffl)
   else
      ofsf_tfcodd(n,coeffl);

procedure ofsf_norm(s);
   % Normalize left hand side of a < or >-relation. [s] is a SQ. Returns a
   % Lisp Prefix form.
   if domainp denr s then
      numr s
   else
      multf(numr s, denr s);

procedure ofsf_tfcodd(n,coeffl);
   % Type Formula Construction in case of odd dimension. [n] is an odd integer,
   % [coeffl] is a list of SQ's. Returns a formula.
   if domainp numr car coeffl and not null numr car coeffl then
      'false
   else
      rl_smkn('and,{ofsf_0mk2('equal, numr car coeffl),
	 ofsf_tfc1(n-1,cdr coeffl)});

procedure ofsf_tfceven(n,coeffl);
   % Type Formula Construction in case of even dimension. [n] is an even
   % integer, [coeffl] is a list of SQ's. Returns a formula.
   if domainp numr car coeffl then
      if null numr car coeffl then
	 ofsf_tfc1(n-1,cdr coeffl)
      else
	 ofsf_stfc(n,reverse coeffl)
   else
      rl_smkn('or, {rl_smkn('and, {ofsf_0mk2('equal, numr car coeffl),
      	 ofsf_tfc1(n-1,cdr coeffl)}),
      	 rl_smkn('and,{ofsf_0mk2('neq, numr car coeffl),
	    ofsf_stfc(n,reverse coeffl)})});


% -----------------------------------------------------------------------------
% Strict Type Formulas:
% -----------------------------------------------------------------------------

procedure ofsf_stfc(n,coeffl);
   % Strict Type Formula Construction. [n] is an even integer, [coeffl] is a
   % list of SQ's. Returns a quantifier-free formula.
   % [coeffl] has form $ (c_{n} c_{n-1} ... c_{0}) $.
   begin scalar vv, res, res1, m;
      if !*rlhqetfcfast then <<
	 vv := ofsf_signs(coeffl);
	 m := ofsf_olsfast(vv,n)
      >> else
	 m := ofsf_ols n;
      while not null m do <<
	 res1 := ofsf_buildconj(coeffl, car m);
	 if res1 eq 'true then <<
	    m := nil;
	    res := 'true
	 >> else if res1 eq 'false then
	    m := cdr m
	 else <<
	    res := res1 . res;
	    m := cdr m
	 >>
      >>;
      if res eq 'true then
	 return res
      else
      	 return ofsf_tfcmkor res
   end;

procedure ofsf_buildconj(coeffl,l);
   % Construction of one conjunction member. [coeffl] is a list of SQ's,
   % [l] is a list. Returns a formula.
   begin scalar newcoeffl, newl, res, af;
      newcoeffl := cdr coeffl;
      newl := cdr l;
      while not null newl do <<
	 af := ofsf_af(car newcoeffl, car newl);
	 if not null af then
	    res := ofsf_af(car newcoeffl, car newl) . res;
	 newcoeffl := cdr newcoeffl;
	 newl := cdr newl
      >>;
      return ofsf_tfcmkand res
   end;

procedure ofsf_af(c,sign);
   % Construction of atomic formular. [c] is a SQ, [sign] is an integer.
   % Returns an atomic formular.
   if sign = 0 then
      nil
   else if domainp denr c then
      ofsf_af1(numr c,sign)
   else
      ofsf_af1(multf(numr c,denr c),sign);

procedure ofsf_af1(c,sign);
   % Subroutine of ofsf_af. [c] is a SF, [sign] is an integer. Returns a
   % formula.
   if sign = 1 then
      ofsf_0mk2('greaterp, c)
   else
      ofsf_0mk2('lessp, c);


procedure ofsf_tfcmkand(l);
   % Build conjunction. [l] is a list of atomic formulas. Returns a formula.
   cl_simpl(rl_smkn('and,l),nil,-1);

procedure ofsf_tfcmkor(l);
   % Build disjunction. [l] is a list of atomic formulas. Returns a formula.
   rl_smkn('or,l);

procedure ofsf_signs(cl);
   % Construction of a signs vector. [cl] is a list of SQ's. Returns a list of
   % integers.
   if null cl then
      nil
   else if domainp numr car cl  and not null numr car cl then
      if numr car cl > 0 then
	 1 . ofsf_signs(cdr cl)
      else
	 (-1) . ofsf_signs(cdr cl)
   else
      0 . ofsf_signs(cdr cl);


% -----------------------------------------------------------------------------
% Type formulas of type k
% -----------------------------------------------------------------------------

procedure ofsf_tfk(cl,k);
   % Type formula construction for type k. [cl] is a list of SQ's, [k] is
   % a positive integer. Returns a formula.
   if length cl - 1 = abs(k) then
      if k=1 then
	 ofsf_0mk2('lessp, numr car cl)
      else if k=-1 then
	 ofsf_0mk2('greaterp, numr car cl)
      else
      	 ofsf_stfk(k,reverse cl,abs(k))
   else if remainder(length cl -1-abs(k),2)=1 then
      if domainp numr car cl and not null numr car cl then
	 'false
      else
      	 rl_smkn('and,{ofsf_0mk2('equal, numr car cl),
	    ofsf_tfk(cdr cl,k)})
   else if domainp numr car cl then
      if null numr car cl then
	 ofsf_tfk(cdr cl,k)
      else
	 ofsf_stfk(k,reverse cl,length cl -1)
   else
      rl_smkn('or, {rl_smkn('and, {ofsf_0mk2('equal, numr car cl),
      	 ofsf_tfk(cdr cl,k)}),rl_smkn('and,{ofsf_0mk2('neq, numr car cl),
	    ofsf_stfk(k,reverse cl,length cl -1)})});

procedure ofsf_stfk(k,coeffl,d);
   % Strict Type Formula Construction. [k] and [d] are integers, [coeffl] is a
   % list of SQ's. Returns a quantifier-free formula.
   % [coeffl] has form $ (c_{d} c_{d-1} ... c_{0}) $.
   begin scalar vv,res, res1, m;
      if !*rlhqetfcfast then <<
	 vv := ofsf_signs(coeffl);
	 m := ofsf_kolsfast(vv,d,k)
      >> else
	 m := ofsf_kols(d,k);
      while not null m do <<
	 res1 := ofsf_buildconj(coeffl, car m);
	 if res1 eq 'true then <<
	    m := nil;
	    res := 'true
	 >> else if res1 eq 'false then
	    m := cdr m
	 else <<
	    res := res1 . res;
	    m := cdr m
	 >>
      >>;
      if res eq 'true then
	 return res
      else
      	 return ofsf_tfcmkor res
   end;

procedure ofsf_kols(d,k);
   % Good tuples of type $k$ to degree $d$. [d] is an INTEGER, the degree of
   % the polynomials, [k] is an INTEGER. Returns a list.
   lto_nconcn {
      ofsf_kextend({-1,1},1,-1,d,d-2,1,0,k),
      ofsf_kextend({ 0,1},1,0,d,d-2,0,0,k),
      ofsf_kextend({ 1,1},1,1,d,d-2,0,1,k)};


procedure ofsf_kextend(l,l0,l1,d,c,p,n,k);
   % Type formula extend. [l] is a list; [d], [c], [p], and [n] are
   % INTEGERS. Returns a list of lists. [l] is the current coefficient
   % tuple that is to be extended; [d] is the target degree; [c] is
   % the current degeree; [p] is the current number of positive
   % zeroes; [n] is the current number of negative zeroes.
   begin;
      if c=0 then
         return ofsf_kextend0(l,l0,l1,d,p,n,k);
      if l1=0 then
         return ofsf_kextend((-l0) . l,l1,-(l0),d,c-1,p+1,n+1,k);
      if l0=0 then  % implies l1 neq 0
         return lto_nconcn {
            ofsf_kextend((-1) . l,l1,-1,d,c-1,p+ofsf_knpz(l1,-1),
	       n+ofsf_knnz(l1,-1),k),
            ofsf_kextend(0 . l,l1, 0,d,c-1,p,n,k),
            ofsf_kextend(1 . l,l1, 1,d,c-1,p+ofsf_knpz(l1, 1),
	       n+ofsf_knnz(l1, 1),k)};
      return lto_nconcn {
         ofsf_kextend(0  . l,l1, 0,d,c-1,p,n,k),
         ofsf_kextend(l0 . l,l1,l0,d,c-1,
	    p+ofsf_knpz(l1,l0),n+ofsf_knnz(l1,l0),k)}
   end;

procedure ofsf_kolsfast(vv,d,k);
   % [d] is a even INTEGER, the degree of the polynomials.
   if car vv = 1 then
      if cadr vv=1 then
	 lto_nconcn {
      	    ofsf_kextendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1,k),
	    ofsf_kextendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0,k)}
      else if cadr vv=-1 then
	 lto_nconcn {
	    ofsf_kextendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0,k),
	    ofsf_kextendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0,k)}
      else
	 lto_nconcn {
      	    ofsf_kextendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0,k),
      	    ofsf_kextendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0,k),
      	    ofsf_kextendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1,k)}
   else
      lto_nconcn {
      	 ofsf_kextendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0,k),
      	 ofsf_kextendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0,k),
      	 ofsf_kextendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1,k)};


procedure ofsf_kextendfast(vv,l,l0,l1,d,c,p,n,k);
   % Type formula extend. [l] is a list; [d], [c], [p], and [n] are
   % INTEGERS. Returns a list of lists. [l] is the current coefficient
   % tuple that is to be extended; [d] is the target degree; [c] is
   % the current degeree; [p] is the current number of positive
   % zeroes; [n] is the current number of negative zeroes; [vv] is a list of
   % integers.
   begin;
      if c=0 then
         return ofsf_kextend0(l,l0,l1,d,p,n,k);
      if l1=0 then
	 if car vv member {-l0,0} then
            return ofsf_kextendfast(cdr vv,(-l0) . l,l1,-(l0),d,c-1,p+1,n+1,k)
	 else
	    return nil;
      if l0=0 then  % implies l1 neq 0
	 if car vv=1 then
	    return lto_nconcn {
	       ofsf_kextendfast(cdr vv, 1 . l,l1, 1,d,c-1,p+ofsf_knpz(l1, 1),
	       	  n+ofsf_knnz(l1, 1),k),
	       ofsf_kextendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n,k)}
	 else if car vv=-1 then
	    return lto_nconcn {
	       ofsf_kextendfast(cdr vv, (-1) . l,l1,-1,d,c-1,
	       	  p+ofsf_knpz(l1,-1),n+ofsf_knnz(l1,-1),k),
	       ofsf_kextendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n,k)}
	 else
            return lto_nconcn {
               ofsf_kextendfast(cdr vv, (-1) . l,l1,-1,d,c-1,
		  p+ofsf_knpz(l1,-1),n+ofsf_knnz(l1,-1),k),
               ofsf_kextendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n,k),
               ofsf_kextendfast(cdr vv, 1 . l,l1, 1,d,c-1,p+ofsf_knpz(l1,1),
	       	  n+ofsf_knnz(l1, 1),k)};
      if not (car vv member {l0,0}) then
	 return ofsf_kextendfast(cdr vv,0 . l,l1,0,d,c-1,p,n,k)
      else return lto_nconcn {
            ofsf_kextendfast(cdr vv, 0  . l,l1, 0,d,c-1,p,n,k),
            ofsf_kextendfast(cdr vv, l0 . l,l1,l0,d,c-1,p+ofsf_knpz(l1,l0),
	       n+ofsf_knnz(l1,l0),k)}
   end;

procedure ofsf_kextend0(l,l0,l1,d,p,n,k);
   begin scalar w;
      w := if remainder(k,2)=0 and remainder(d,2)=0 then
               if remainder((d-k)/2,2) = 0 then 1 else -1
           else if remainder(k,2)=0 or remainder(d,2)=0 then nil
           else if k>=0 then
                if  remainder((d-k)/2,2)=0 then -1 else 1
           else if remainder((d+k)/2,2)=0 then 1 else -1;
      if null w then return nil;
      % w := if remainder(k,2) = 1 then -w else w;
      if (l0*w=-1) and (l1 neq 0) then return nil;
      return ofsf_kextend1(w . l,p+ofsf_knpz(l1,w),n+ofsf_knnz(l1,w),k)
   end;

procedure ofsf_kextend1(l,p,n,k);
%   if p=n then {reverse l} else << prin2t reverse l; nil>> ;
   if p-n=k then {reverse l} else nil;

procedure ofsf_knnz(a,b);
   % new n zeroes
   if a*b=1 then 1 else 0;

procedure ofsf_knpz(a,b);
   % new p zeroes
   if a*b=-1 then 1 else 0;


% Sum of all types

procedure ofsf_splittypes(l,d,y,i);
   % Computes formula, so that the sum of the types of the elements of a list
   % is zero. [l] is a list of SQ's, [d] and [i] are positive integers, [y] is
   % an identifier. Returns a formula.
   begin scalar res,f1,f2fn,lenl;
      % ioto_prin2t {length l,d,i};
      if null cdr l or (length l)*d < 8 or d*(length l)<2*abs(i) then
	 if abs(i)<(length l)*d+1 then
	    if i=0 then
	       return ofsf_tfc reversip ofsf_coefflist(ofsf_multfl l,y)
	    else
      	       return ofsf_tfk(reversip ofsf_coefflist(ofsf_multfl l,y),i)
	 else
	    return 'false;
      lenl := length l;
      for j:=-d:d do
	 if (lenl - 1)*d+1 > abs(i-j) then <<
	    if j=0 then
	       f1 := ofsf_tfc reversip ofsf_coefflist(car l,y)
	    else
	       f1 := ofsf_tfk(reversip ofsf_coefflist(car l,y),j);
	    f2fn := ofsf_splittypes(cdr l,d,y,i-j);
	    res := rl_smkn('and,{f1, f2fn}) . res
      	 >>;
      return rl_smkn('or,res)
   end;


% -----------------------------------------------------------------------------
% Good tuples for type 0
% -----------------------------------------------------------------------------
procedure ofsf_ols(d);
   % [d] is a even INTEGER, the degree of the polynomials.
   lto_nconcn {
      ofsf_extend({-1,1},1,-1,d,d-2,1,0),
      ofsf_extend({ 0,1},1,0,d,d-2,0,0),
      ofsf_extend({ 1,1},1,1,d,d-2,0,1)};

procedure ofsf_extend(l,l0,l1,d,c,p,n);
   % Type formula extend. [l] is a list; [d], [c], [p], and [n] are
   % INTEGERS. Returns a list of lists. [l] is the current coefficient
   % tuple that is to be extended; [d] is the target degree; [c] is
   % the current degeree; [p] is the current number of positive
   % zeroes; [n] is the current number of negative zeroes.
   begin;
      if c=0 then
         return ofsf_extend0(l,l0,l1,d,p,n);
      if l1=0 then
         return ofsf_extend((-l0) . l,l1,-(l0),d,c-1,p+1,n+1);
      if l0=0 then  % implies l1 neq 0
         return lto_nconcn {
            ofsf_extend((-1) . l,l1,-1,d,c-1,
	       p+ofsf_npz(l1,-1),n+ofsf_nnz(l1,-1)),
            ofsf_extend( 0 . l,l1, 0,d,c-1,p,n),
            ofsf_extend( 1 . l,l1, 1,d,c-1,
	       p+ofsf_npz(l1, 1),n+ofsf_nnz(l1, 1))};
      return lto_nconcn {
         ofsf_extend( 0  . l,l1, 0,d,c-1,p,n),
         ofsf_extend( l0 . l,l1,l0,d,c-1,p+ofsf_npz(l1,l0),n+ofsf_nnz(l1,l0))}
   end;

procedure ofsf_olsfast(vv,d);
   % [d] is a even INTEGER, the degree of the polynomials.
   if car vv = 1 then
      if cadr vv=1 then
	 lto_nconcn {
      	    ofsf_extendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1),
	    ofsf_extendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0)}
      else if cadr vv=-1 then
	 lto_nconcn {
	    ofsf_extendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0),
	    ofsf_extendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0)}
      else
	 lto_nconcn {
      	    ofsf_extendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0),
      	    ofsf_extendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0),
      	    ofsf_extendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1)}
   else
      lto_nconcn {
      	 ofsf_extendfast(cddr vv,{-1,1},1,-1,d,d-2,1,0),
      	 ofsf_extendfast(cddr vv,{ 0,1},1,0,d,d-2,0,0),
      	 ofsf_extendfast(cddr vv,{ 1,1},1,1,d,d-2,0,1)};

procedure ofsf_extendfast(vv,l,l0,l1,d,c,p,n);
   % Type formula extend. [l] is a list; [d], [c], [p], and [n] are
   % INTEGERS. Returns a list of lists. [l] is the current coefficient
   % tuple that is to be extended; [d] is the target degree; [c] is
   % the current degeree; [p] is the current number of positive
   % zeroes; [n] is the current number of negative zeroes.
   begin;
      if c=0 then
         return ofsf_extend0(l,l0,l1,d,p,n);
      if l1=0 then
	 if car vv member {-l0,0} then
            return ofsf_extendfast(cdr vv, (-l0) . l,l1,-(l0),d,c-1,p+1,n+1)
	 else
	    return nil;
      if l0=0 then % implies l1 neq 0
 	 if car vv = -1 then
	    return lto_nconcn {
	       ofsf_extendfast(cdr vv, (-1) . l,l1,-1,d,c-1,p+ofsf_npz(l1,-1),
	       	  n+ofsf_nnz(l1,-1)),
	       ofsf_extendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n)}
	 else if car vv=1 then
	    return lto_nconcn {
	       ofsf_extendfast(cdr vv, 1 . l,l1, 1,d,c-1,p+ofsf_npz(l1, 1),
	       	  n+ofsf_nnz(l1, 1)),
	       ofsf_extendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n)}
	 else
            return lto_nconcn {
               ofsf_extendfast(cdr vv, (-1) . l,l1,-1,d,c-1,p+ofsf_npz(l1,-1),
	       	  n+ofsf_nnz(l1,-1)),
               ofsf_extendfast(cdr vv, 0 . l,l1, 0,d,c-1,p,n),
               ofsf_extendfast(cdr vv, 1 . l,l1, 1,d,c-1,p+ofsf_npz(l1, 1),
	       	  n+ofsf_nnz(l1, 1))};
      if not(car vv member {l0,0}) then
	 return ofsf_extendfast(cdr vv, 0 . l,l1,0,d,c-1,p,n)
      else return lto_nconcn {
            ofsf_extendfast(cdr vv, 0  . l,l1, 0,d,c-1,p,n),
            ofsf_extendfast(cdr vv, l0 . l,l1,l0,d,c-1,p+ofsf_npz(l1,l0),
	       n+ofsf_nnz(l1,l0))}
   end;

procedure ofsf_extend0(l,l0,l1,d,p,n);
   begin scalar w;
      w := if remainder(d,4) = 2 then -1 else 1;
      if (l0*w=-1) and (l1 neq 0) then return nil;
      return ofsf_extend1(w . l,p+ofsf_npz(l1,w),n+ofsf_nnz(l1,w))
   end;

procedure ofsf_extend1(l,p,n);
   if p=n then {reverse l} else nil;

procedure ofsf_nnz(a,b);
   % new n zeroes
   if a*b=1 then 1 else 0;

procedure ofsf_npz(a,b);
   % new p zeroes
   if a*b=-1 then 1 else 0;

endmodule;  % [ofsftfc]

end;  % of file
