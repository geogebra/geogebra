%======================================================
%       Name:           PERM1 - permutation package
%       Author:         A.Kryukov (kryukov@theory.npi.msu.su)
%       Copyright:      (C), 1993-1996, A.Kryukov
%       Version:        2.32
%       Release:        Nov. 12, 1993
%                       Mar. 28, 1996     PFIND: add error msg.
%======================================================

module perm1$

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


global '(!*ppacked)$
!*ppacked:=t$

%-------------------------------------------------------
%       Generator of permutations.
%       Version 1.2.1   Nov. 18, 1994
%
%-------------------------------------------------------

procedure GPerm n$        % order of symmetric group.
  %       Return all pertmutation of S(n).
  begin scalar l$
%    if n>9 then rederr list('GPerm,": ",n," is too high order (<=9).")$
    while n>0 do << l:=n . l$ n:=n-1 >>$
    return for each x in GPerm0 l collect pkp x$
end$

 procedure GPerm0(OLst)$
   %       OLst    - list of objects.
   %       Return  - list of permutation of these objects.
   if null OLst then nil
   else GPerm3(cdr OLst,list list car OLst)$

 procedure GPerm3(OList,Res)$
   %       OList   - list of objects,
   %       Res     - list of perm. of objects.
   if null OList then Res
   else GPerm3(cdr OList,GPerm2(Res,car OList,nil))$

 procedure GPerm2(PLst,Obj,Res)$
   %       Obj     - object,
   %       PLst    - permutation list,
   %       Res     - list of perm. included Obj.
   if null PLst then Res
   else GPerm2(cdr PLst,Obj,GPerm1(Rev(car PLst,nil),Obj,nil,Res))$

 procedure GPerm1(L,Obj,R,Res)$
   %       Obj     - object,
   %    L,R     - left(reverse form) and right(direct form) part of
   %                 permutation.
   %       Res     - list of permutation.
   if null L then (Obj . R) . Res
   else GPerm1(cdr L,Obj,car L . R,Rev(L,Obj . R) . Res)$

 procedure Rev(Lst,RLst)$
   if null Lst then RLst
   else Rev(cdr Lst, car Lst . RLst)$

%-------------------------------------------------------

symbolic procedure mkunitp k$
  begin scalar p$
    for i:=1:k do p:=i . p$
    return pkp reversip p$
  end$

symbolic procedure pfind(l1,l2)$
  % l1,l2 - (paked) lists of indices.
  begin scalar p,z$
        integer m$
    l1:=unpkp l1$
    l2:=unpkp l2$
    m:=length l2 + 1$
    l2:=for each x in l2 collect x$
    for each x in l1 do <<
      z:=member(x,l2)$
      if null z
        then rederr list("PFIND: No index",x,"in",l2)$ %+ AK 28/03/96
      p:=(m - length z) . p$
      rplaca(z,'nil!*)$
    >>$
    return pkp reversip p$
  end$

symbolic procedure prev(f)$
  begin scalar p,w$
        integer i,j,l$
    f:=unpkp f$
    l:=length f$
    for i:=1:l do <<
      w:=f$
      j:=1$
      while not(car w = i) do << j:=j+1$ w:=cdr w >>$
      p:=j . p$
    >>$
    return pkp reversip p$
  end$

symbolic procedure psign(f)$
  begin integer s,i,j,n,k$
    scalar new0,new,wnew,f0,wf$
    s:=1$
    f:=unpkp f$
    n:=length f$
    f0:=f$
    new0:=for each x in f collect t$
    new:=new0$
    for i:=1:n do <<
      if car new then                  % find cycle contained i
        << j:=car f$
           while not(j = i) do <<
             wnew:=new0$
             wf:=f0$
             for k:=1:j-1 do << wnew:=cdr wnew$ wf:=cdr wf >>$
             rplaca(wnew,nil)$
             s:=-s$
             j:=car wf$
           >>$
        >>$
      new:=cdr new$
      f:=cdr f$
    >>$ % for i
    return s$
  end$

symbolic procedure pmult(f,g)$
  begin scalar p,w,ok$
        integer i$
    f:=unpkp f$
    g:=unpkp g$
    while g do <<
      w:=f$
      for i:=1:(car g - 1) do w:=cdr w$
      p:=car w . p$
      g:=cdr g$
    >>$
    return pkp reversip p$
  end$

symbolic procedure pappl(p,l)$
  begin scalar l1,w$
        integer i$
    p:=unpkp p$
    while p do <<
      w:=l$
      for i:=1:(car p - 1) do w:=cdr w$
      l1:=car w . l1$
      p:=cdr p$
    >>$
    return reversip l1$
  end$

symbolic procedure pappl0(p1,p2)$
  pkp pappl(p1,unpkp p2)$

symbolic procedure pupright(p,d)$
  begin scalar w,i,k$
     p:=unpkp p$
     k:=(length p + 1)$
     d:=k+d-1$
     for i:=k:d do w:=i . w$
     return pkp append(p,reversip w)$
  end$

symbolic procedure pupleft(p,d)$
  begin scalar w,i$
     p:=unpkp p$
     p:=for each x in p collect (x+d)$
     for i:=1:d do w:=i . w$
     return pkp append(reversip w,p)$
  end$

symbolic procedure pappend(p1,p2)$
  begin scalar l;
    p1:=unpkp p1;
    l:=length p1;
    p2:=unpkp p2;
    p2:=for each x in p2 collect (x + l)$
    return pkp append(p1,p2)$
  end$

%--------------------------------------------------------

global '(diglist!*)$
diglist!*:='((!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4) (!5 . 5)
             (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9) (!0 . 0))$

symbolic procedure dssoc(x,u)$
  if null u then nil
  else if x=cdar u then car u
  else dssoc(x,cdr u)$

%symbolic procedure hugerank()$ 3$

symbolic procedure pkp p$
  begin scalar w,huge,z$
    if atom p or null !*ppacked then return p$
    huge:=(length p >= 10)$
    for each x in p do
      if huge then <<
        if x<10 then w := car dssoc(x,diglist!*) . '!0 . w
        else << z:=divide(x,10)$
                w := car dssoc(car z,diglist!*) . w$
                w := car dssoc(cdr z,diglist!*) . w$
              >>$
      >>
      else w:=car dssoc(x,diglist!*) . w$

    return compress reversip w$
  end$

symbolic procedure unpkp p$
  begin scalar w,huge,z$
    if null atom p then return p$
    p:=explode p$
    huge:=(length p >=10)$
    if huge and null evenp length p then p := '!0 . p$
    while p do <<
      if huge then <<
          z:=cdr assoc(car p,diglist!*)$
          p:=cdr p$
          w:= (z*10+cdr assoc(car p,diglist!*)) . w$
        >>
      else w:=cdr assoc(car p,diglist!*) . w$
      p:=cdr p$
    >>$
    return reversip w$
  end$

symbolic procedure porder p $
  length unpkp p$

symbolic procedure hugep p$
  <<
     p:=unpkp p$
     if length p >= 10 then list p else nil
  >>$

endmodule;

end;
