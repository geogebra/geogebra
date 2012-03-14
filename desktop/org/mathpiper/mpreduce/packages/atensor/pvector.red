%===============================================================
%       File:           pvector.red
%       Purpose:        Vector arithmetic.
%       Version:        3.01    Nov. 14, 1993
%---------------------------------------------------------------
%       Revision        26/11/90        PermGT
%                       05/03/91        UpDate
%                       Nov. 01, 1993     General revisions.
%                       Nov. 14, 1993     Domain introduction
%===============================================================

lisp <<
  if null getd 'mkunitp then in "perm.red"$
>>$


module pvector$

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


%       p-vector is a list of b-vectors.
%       b-vector is a <coeff> . <permutation>.
%       coeff - integer.

%---------------------- Main procedures -------------------

symbolic procedure pv_simp v$
  (('!:pv . list(1 . car v)) ./ 1)$

put('pv,'simpfn,'pv_simp)$

global '(domainlist!*)$

switch pvector$

domainlist!*:=union('(!:pv),domainlist!*)$
put('pvector,'tag,'!:pv)$
put('!:pv,'dname,'pvector)$
%flag('(!:pv),'field)$          % !:pv is not a field!
put('!:pv,'minus,'pv_minus)$
put('!:pv,'minusp,'pv_minusp)$
put('!:pv,'plus,'pv_plus)$
put('!:pv,'times,'pv_times)$    % v*c
put('!:pv,'difference,'pv_difference)$
put('!:pv,'zerop,'pv_zerop)$
put('!:pv,'onep,'pv_onep)$
put('!:pv,'prepfn,'pv_prep)$
put('!:pv,'prifn,'pv_pri)$
put('!:pv,'intequivfn,'pv_intequiv)$
put('!:pv,'i2d,'i2pvector)$
put('!:pv,'expt,'pv_expt)$
put('!:pv,'quotient,'pv_quotient)$
put('!:pv,'divide,'pv_divide)$
put('!:pv,'gcd,'pv_gcd)$

flag('(!:pv),'pvmode)$

symbolic procedure pv_minus u$
  car u . pv_neg cdr u$

symbolic procedure pv_minusp u$ nil$

symbolic procedure pv_plus(u,v)$
%  if abs(cdadr u - cdadr v)>100       % incorrect test!
%    then rederr list('pv_plus,"*** Differ order of permutations:",u,v)
%  else
   if atom cdr u and atom cdr v then car u . (cdr u + cdr v)
   else if atom cdr u
    then rederr list('pv_plus,"*** pvector can't be added to:",cdr u)
   else if atom cdr v then pv_plus(v,u)
   else car u . pv_add(cdr u,cdr v)$

symbolic procedure pv_times(u,v)$
  % u,v - (!:pv . pvlist)
  if pv_intequiv u then pv_times(v,u)
  else if atom cdr v then car u . pv_multc(cdr u,cdr v)
  else car u . pv_times1(cdr u,cdr v,nil)$
%  else rederr {'pv_times,"*** pvector can't be multiplied by: ",cdr v}$

symbolic procedure pv_times1(u,v,w)$
  % u,v,w - pvlist::=((c1 . p1) ...)
  if null u then w
  else pv_times1(cdr u,v,pv_times2(car u,v,w))$

symbolic procedure pv_times2(x,v,w)$
  % x - (c . p)
  % v,w - pvlist::=((c1 . p1) ...)
  if null v then w
  else pv_times2(x,cdr v
                ,pv_add(list pv_times3(x,car v),w)
                )$

symbolic procedure pv_times3(x,y)$
  % x,y - (c . p)
  (car x * car y) . pappend(cdr x,cdr y)$

symbolic procedure pv_difference(u,v)$
  pv_plus(u,pv_minus v)$

symbolic procedure pv_zerop(u)$
  null cdr u$

symbolic procedure pv_onep u$ nil$

symbolic procedure pv_prep u$ u$

symbolic procedure pv_pri(u)$
  begin scalar notfirst$
    for each x in cdr u do <<
      if notfirst and car x > 0 then prin2!* " + "
      else notfirst:=t$
      if null(car x = 1) then << prin2!* car x$ prin2!* "*" >>$
      prin2!* 'pv$ prin2!* '!($ prin2!* cdr x$ prin2!* '!)$
    >>$
  end$

symbolic procedure pv_intequiv u$
  if atom cdr u then cdr u else nil$

symbolic procedure i2pvector n$
  '!:pv . n$

symbolic procedure pv_expt(u,n)$
  if n=1 then u
  else rederr list('pv_expt,"*** Can't powered pvector")$

symbolic procedure pv_quotient(u,c)$
  if pv_intequiv c and cdr c = 1 then u
  else rederr list('pv_quotient,"*** pvector can't be divided by: ",c)$

symbolic procedure pv_divide(u,v)$
  rederr list('pv_divide,"*** Can't divide pvector by pvector")$

symbolic procedure pv_gcd(u,v)$ car u . 1$

%-------------------------------------------------------

initdmode 'pvector$

symbolic procedure pv_add(v1,v2)$
 %       v1,v2   - pvectors.
 %       Return v1+v2.
 if null v1 then v2
 else if null v2 then v1
 else begin scalar r,h$
     while v1 or v2 do
       if v1 and v2 and cdar v1 = cdar v2 then <<
           h:=caar v1 + caar v2$
           if null(h = 0) then r:=(h . cdar v1) . r$
           v1:=cdr v1$
           v2:=cdr v2$
         >>
       else if (v1 and null v2) or (v1 and v2 and cdar v1 > cdar v2)
              then << r:=(car v1 . r)$ v1:=cdr v1 >>
       else << r:=(car v2 . r)$ v2:=cdr v2 >>$
     return reversip r$
 end$

symbolic procedure pv_neg v1$
   %       v1     - pvector$
   %       Return - v1.
   begin scalar r$
     while v1 do <<
       r:= ((-caar v1) . cdar v1) . r$
       v1:=cdr v1$
     >>$
     return reversip r$
 end$

symbolic procedure pv_multc(v,c)$
 if c=0 or null v then nil
 else if c=1 then v
 else begin scalar r$
     while v do <<
       if null(caar v = 0) then r:=((c*caar v) . cdar v) . r$
       v:=cdr v$
     >>$
     return reversip r$
 end$

%-------------------- Sorting ... -----------------------

symbolic procedure pv_sort v$
  if null v then nil
  else pv_sort1(cdr v,list car v)$

symbolic procedure pv_sort1(v,v1)$
 if null v then reversip v1
 else if cdar v < cdar v1 then pv_sort1(cdr v,car v . v1)
 else pv_sort1(cdr v,pv_sort2(car v,v1))$

symbolic procedure pv_sort2(x,v1)$
  << pv_sort2a(x,v1); v1 >>$

symbolic procedure pv_sort2a(x,v1)$
  if null cdr v1
    then if cdr x > cdar v1 then rplacd(v1,list x)
         else (lambda w; rplacd(rplaca(v1,x),w)) (car v1 . cdr v1)
  else if cdr x > cdar v1 then pv_sort2a(x,cdr v1)
  else (lambda w; rplacd(rplaca(v1,x),w)) (car v1 . cdr v1)$


%------------------- pv_renorm -------------------------------

symbolic procedure pv_compress v$
  begin scalar u$
    while v do <<
      if null(caar v = 0) then u:=car v . u$
      v:=cdr v$
    >>$
    return reversip u$
  end$

symbolic procedure pv_renorm v$    % not v modified.
 if null v then nil
 else begin scalar r,k$
    while v and caar v = 0 do v:=cdr v$
    if null v then return nil$
    if caar v < 0 then v:=pv_neg v$
    k:=caar v$
    r:=cdr v$
    while r and k neq 1 do <<
      k:=gcdf!*(k,caar r)$
      r:=cdr r$
    >>$
    r:=nil$
    for each x in v do
      if null(car x = 0)
        then r:=(if k=1 then x else ((car x/k) . cdr x)) . r$
    return reversip r$
 end$

 %---------------------------------------------------------------

symbolic procedure pappl_pv(p,v)$
  pv_sort for each x in v collect (car x . pappl0(p,cdr x))$

symbolic procedure pv_applp(v,p)$
  pv_sort for each x in v collect (car x . pappl0(cdr x,p))$

symbolic procedure pv_upright(v,d)$
  for each x in v collect (car x . pupright(cdr x,d))$

symbolic procedure vupleft(v,d)$
  for each x in v collect (car x . pupleft(cdr x,d))$

endmodule;

end;
