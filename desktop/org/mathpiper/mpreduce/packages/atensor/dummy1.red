%======================================================
%       Name:           dummy.red - dummy indecies package
%       Author:         A.Kryukov (kryukov@npi.msu.su)
%       Copyright:      (C), 1993, A.Kryukov
%       Version:        2.10
%       Release:        Nov. 17, 1993
%======================================================

module dummy1$

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


global '(!*basis)$

symbolic procedure cross(s1,s2)$ cross1(s1,s2,nil)$

symbolic procedure cross1(s1,s2,w)$
  if null s1 then w
  else if car s1 memq s2
    then cross1(cdr s1,delete(car s1,s2),car s1 . w)
  else cross1(cdr s1,s2,w)$

symbolic procedure suppl(s1,s2)$ suppl1(s1,s2,nil)$

symbolic procedure suppl1(s1,s2,w)$
  if null s1 then w
  else if null(car s1 memq s2) then suppl1(cdr s1,s2,car s1 .w)
  else suppl1(cdr s1,delete(car s1,s2),w)$

symbolic procedure suppl2(s1,s2,w)$
  if null s1 then (s2 . w)
  else if null(car s1 memq s2) then suppl1(cdr s1,s2,car s1 .w)
  else suppl1(cdr s1,delete(car s1,s2),w)$

symbolic procedure tn_equal(tn1,tn2)$
  % tn1,tn2 - tname::=(id1 id2 ...)
  (car x and cdr x) where x=suppl2(tn1,tn2,nil)$

symbolic procedure th_equal(th1,th2)$
  % th1,th2 - theader::=(tname . ilist . dlist)
  if tn_equal(car th1,car th2) then il_equal(cadr th1,cadr th2)
  else nil$

symbolic procedure il_equal(il1,il2)$
  il_equal1(il2,suppl(il1,il2),nil)$

symbolic procedure il_equal1(il,dl,w)$
  % il,w - ilist
  % dl - dlist
  if null il then reversip w
  else if null get(car il,'dummy) then il_equal1(cdr il,dl,car il . w)
  else ((if null cdr x
           then (il_equal1(cdr il,cdr dl,car dl . w)
                where z=rplacd(rplaca(x,car get(car dl,'dummy)),t)
                )
         else (il_equal1(cdr il,delete(z,dl),z . w)
              where z=dfind(car x,dl)
              )
        ) where x=get(car il,'dummy)
       )$

symbolic procedure dfind(di,dl)$
  if null dl then nil
  else if di eq get(car dl,'dummy) then car dl
  else dfind(di,cdr dl)$

symbolic procedure il_simp(il)$ il_simp1(il,nil)$

symbolic procedure il_simp1(il,w)$
  if null il then reversip w
  else if car il memq cdr il
    then il_simp1(di_subst(car il . di_new car il,cdr il)
                 ,di_new car il . w
                 )
  else il_simp1(cdr il, car il . w)$

symbolic procedure di_subst(x,il)$ di_subst1(x,il,nil)$

symbolic procedure di_subst1(x,il,w)$
  if null il then reversip w
  else if car x eq car il then di_subst1(x,cdr il,cdr x . w)
  else di_subst1(x,cdr il,car il . w)$

global '(d_number)$
if null d_number then d_number:=0$

symbolic procedure di_new(x)$
  begin scalar z$
    d_number:=d_number + 1$
    z:=mkid('!_,d_number)$
    put(z,'dummy,list x)$
    return z$
  end$

global '(!*dummypri !*windexpri)$
switch dummypri,windexpri$

symbolic procedure di_restore il$ di_restore1(il,nil)$

symbolic procedure di_restore1(il,w)$
  if null il then reversip w
  else ((if null x
           then ((if null y then di_restore1(cdr il,car il . w)
                  else di_restore1(cdr il
                         ,(if !*windexpri then mkid(car y,car il)
                           else car y
                          ) . w
                                  )
                 ) where y = get(car il,'windex)
                )
         else di_restore1(cdr il
                ,(if !*dummypri then mkid(car x,car il) else car x) . w
                         )
        ) where x=get(car il,'dummy)
       )$

endmodule;

end;
