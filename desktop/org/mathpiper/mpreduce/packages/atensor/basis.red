%----------------------------------------------------------------
%       File:           basis.red
%       Purpose:        Build the triangle form of basis
%       Copyright:      (C) 1990-1996, A.Kryukov, kryukov@theory.npi.msu.su
%       Version:        2.21    Mar. 25, 1996
%----------------------------------------------------------------
%       Revision:       27/11/90        insertv
%                       26/11/90        SieveV
%                       05/03/91        AppS
%                       Nov. 12, 1993     updatev
%                       Mar. 25, 1996   sieved_pv0, reduce_pv0
%----------------------------------------------------------------

lisp <<
  if null getd 'mkunitp then in "perm.red"$
  if null getd 'pv_add then in "pvector.red"$
>>$

module basis$

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


%===================================
% basis ::= (v1 v2 ...)
%===================================
global '(!*basis)$

procedure sieve_pv(v,b)$
  sieve_pv0(v,b,t)$

procedure sieve_pv0(v,b,norm)$
  %---------------------------
  % v - vector.
  % b - basis.
  % norm=t -> normalized vector
  % return sieved vector.
  %---------------------------
  if null v then nil
  else <<
    while b and cdaar b > cdar v do b:=cdr b$
    while v and b do <<          % reduce v.
      v:=reduce_pv0(v,car b,norm)$
      b:=cdr b$
    >>$
    v
  >>$

procedure reduce_pv(v,q)$
  reduce_pv0(v,q,t)$

global '(pv_den)$

procedure reduce_pv0(v,q,norm)$
  %---------------------------
  % v is reduced by q.
  % norm=t -> normalized vector
  % return reduced v.
  %---------------------------
  if null q then v
  else if null v then nil
  else begin scalar w,k$
      w:=v$
      while w and q and (cdar w > cdar q)
         do w := cdr w$  % find needed component.
      if w and q and (cdar q = cdar w) then <<
        k:=lcm(caar w,caar q)$                % Least Common Multiplier.
        v:=pv_add(pv_multc(v,k/caar w),pv_multc(q,-k/caar q))$
%        if v then v:=pv_renorm v$
        if null norm then pv_den:=pv_den*k/caar w       % +AK 26/03/96
        else pv_den:=1$                                 % +AK 28/03/96
      >>$
      return v$
 end$

%------------------- Insert new vector ----------------

symbolic procedure insert_pv(pv,bl)$
  % pv - pvector
  % bl - original basis list
  % (r.v.) - new basis list
  (if null x then bl
   else insert_pv1(pv_renorm x,bl,nil)
  ) where x=sieve_pv(pv,bl)$

symbolic procedure insert_pv1(pv,bl,bl1)$
  % pv - pvector
  % bl,bl1(r.v.) - basis list
  if null bl then if null pv then reversip bl1
                  else reversip(pv . bl1)
  else if null pv then insert_pv1(nil,cdr bl,car bl . bl1)
  else if cdaar bl > cdar pv
    then insert_pv1(pv,cdr bl,pv_renorm reduce_pv(car bl,pv) . bl1)
  else insert_pv1(nil,bl,pv . bl1)$


procedure insert_pv_(v,b)$
  % v - vector.
  % b - basis (midified.).
  % return updatev basis.
  if null v then b
  else if null b then list v
  % bug: if .. then .. <missing else> if .. then .. else ..
  else begin scalar b1,w$
    v:=pv_renorm sieve_pv(v,b);
    if null v then return b$
    b1:=b$
    while cdr b1 and cdaar b1 > cdar v do <<          % reduce car b1.
      rplacA(b1,pv_renorm reduce_pv(car b1,v))$
      b1:=cdr b1$
    >>$
    if cdaar b1 > cdar v then <<
      rplacA(b1,pv_renorm reduce_pv(car b1,v))$
      rplacD(b1,v . cdr b1)$                             % insert after.
    >> else <<                                                    % insert before.
      w:=car b1 . cdr b1;
      rplacD(rplacA(b1,v),w)$
    >>$
    return b$
 end$

remprop('basis,'stat)$

symbolic procedure update_pv(v,b)$
  % v - vector (modified)$
  % b - basis (modified)$
  % return updatevd vector v.
  if null v then nil
  else begin scalar r,w$
    if null(car b eq '!*basis)
      then rederr list('updatev,": 2-nd arg. is not a basis.")$
    r:=v$
    while v do <<
      w:=member(cdar v,cdr b)$
      if w then rplacD(car v,car w)
      else rplacD(b,cdar v . cdr b)$
      v:=cdr v$
    >>$
    return r$
  end$

endmodule;

end;
