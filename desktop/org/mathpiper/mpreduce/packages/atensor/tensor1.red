%======================================================
%       Name:           tensor1.red - tensor continuation
%       Author:         A.Kryukov (kryukov@theory.npi.msu.su)
%       Copyright:      (C), 1993-1996, A.Kryukov
%       Version:        2.22 Apr. 02, 1996
%------------------------------------------------------
%       Release:        Dec. 15, 1993
%                       Mar. 25, 1996     sieve_t2
%                       Apr. 02, 1996     t_add2
%======================================================

module tensor1$

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
global '(pv_den)$

symbolic procedure th2pe(th,v)$
  % th - tensor header
  % th::=(tname ...) . (i ...) . (d ...)
  % v  - vector
  % return prefix expression
  begin scalar pe,r,i,il,tt,tt1$
    % tt,tt1 - tensor term
    while v do <<
      il:=pappl(cdar v,di_restore cadr th)$
      tt1:=nil$
      for each x in car th do <<
        r:=get(x,'!:tensor)$
        tt:=list x$
        for i:=1:r do << tt:=car il . tt$ il:=cdr il >>$
        tt1:=reversip tt . tt1$
      >>$ % for each
      tt1:=reversip tt1$
      if null(caar v = 1) then tt1:=caar v . tt1$
      if tt1 and cdr tt1 then tt1:='times . tt1$
      if tt1 and null cdr tt1 then tt1:=car tt1$
      pe:=tt1 . pe$
      v:=cdr v$
    >>$ % while v
    pe:=reversip pe$
    if pe and cdr pe then pe:='plus . pe
    else if pe then pe:=car pe$
    return pe$
  end$

symbolic procedure t_pri1(tt,sw)$
  % tt - tensor expression
  % tt::=!:tensor . ((th . v) ...)
  begin scalar pe,den$                                 %mod AK 28/03/96
    tt:=cdr tt$
    den:=cddr caar tt$                                 %+   AK 28/03/96
    while tt do <<
      pe:=th2pe(caar tt,cdar tt) . pe$
      tt:=cdr tt$
    >>$
    if pe and cdr pe then pe:='plus . reversip pe
    else if pe then pe:=car pe$
    if not(den = 1) then pe:='quotient . pe . list den$%+   AK 28/03/96
%    terpri()$ print list(">>>>>> t_pri1: pe=",pe)$ terpri()$
    assgnpri(pe,nil,sw)$                               % WN 10.4.96
  end$

symbolic procedure pappl_t(p,tt)$
  for each x in tt collect
    (caar x . pappl(p,cadar x) . cddar x) . pappl_pv(p,cdr x)$

symbolic procedure t_add(t1,t2)$
  if null cdr t1 then t2
  else if null cdr t2 then t1
  else if th_match(cadr t1,cadr t2)
       then sieve_t(t_add2(t1,t2),!*basis)
  else t_addf(t1,t2)$

symbolic procedure sieve_t(tt,bs)$
  % tt:=(!:tensor . (ten1 ten2 ...))
  car tt . sieve_t0(cdr tt,nil,bs)$                    % -AK 250396
%  ((car tt . car x) . cdr x)                            % +AK 250396
%  where x=sieve_t0(cdr tt,nil,bs)$                      % +AK 250396

symbolic procedure sieve_t0(u,v,bs)$      % July 13, 1994
  % u::=(ten1 ten2 ...)
  % v - sieved tensor (r.v.)
  if null u then reversip v
  else sieve_t0(cdr u
               ,((if cdr x then x . v else v)           % -AK 250396
%               ,((if cdr x then (x.pv_den) . v else v)   % +AK 250396
                 where x=sieve_t2(car u,bs)
                )
               ,bs
               )$

symbolic procedure sieve_t1(tt,bs)$
  % tt::=(th . pv)
  begin scalar bs$
    bs:=!*basis$
    while bs and null th_match(car tt,caar bs) do bs:=cdr bs$
    if bs then return car tt . sieve_pv(cdr tt,cdar bs)$
    if dl_get(cadar tt) then <<
      !*basis:=append(adddummy('!:tensor . list tt),!*basis)$
      bs:=!*basis$
      while bs and null th_match(car tt,caar bs) do bs:=cdr bs$
      if bs then return car tt . sieve_pv(cdr tt,cdar bs)$
    >>$
    return tt$
  end$

%symbolic procedure sieve_t2(tt,bs1)$     % Jul 13, 1994
%  % tt::=(th . pv)
%  begin scalar bs$
%    bs:=bs1$
%    if dl_get(cadar tt) then bs:=append(adddummy0(list tt,bs),bs)$
%    while bs and null th_match(car tt,caar bs) do bs:=cdr bs$
%    if bs then tt := car tt . sieve_pv(cdr tt,cdar bs)$
%    return tt$
%  end$

symbolic procedure sieve_t2(tt,bs1)$      % Mar. 25, 1996
  % tt::=(th . pv)
  begin scalar bs,tt1$
    bs:=bs1$
    if dl_get(cadar tt) then bs:=append(adddummy0(list tt,bs),bs)$
    while bs and null th_match(car tt,caar bs) do bs:=cdr bs$
    tt1:=tt$
    pv_den:=1$
    if bs then tt := car tt . sieve_pv0(cdr tt,cdar bs,nil)$
    rplacd(cdar tt,cddar tt * pv_den)$              % + AK 28/03/96
    if !*debug then
      << terpri()$
         write " DEBUG: sieve_t2"$
         terpri()$
         t_pri1('!:tensor.list tt1,t);
         if bs then
           for each z in cdar bs
           do t_pri1('!:tensor.list(caar bs.z),t);
         terpri()$
         t_pri1('!:tensor.list tt,t);
         terpri()$
      >>$
    return tt$
  end$

symbolic procedure t_addf(t1,t2)$
  if ordp(cadr t1,cadr t2)
%    then ( t1 .+ (t2 .+ nil) )
    then ( ((t1 .** 1) .* 1) .+ ( ((t2 .** 1 ) .* 1) .+ nil) )
  else t_addf(t2,t1)$

symbolic procedure t_add2(tx1,tx2)$
  begin scalar w$
    w:=il_update(cadar tx2,dl_get cadar tx1)$
    w:=pfind(w,cadar tx1)$
%    w:=for each x in cdr tx2 collect car x . pappl0(w,cdr x)$
        % - AK 02/04/96
    w:=for each x in cdr tx2 collect car x . pappl0(cdr x,w)$
        % + AK 02/04/96
    return car tx1 . pv_add(cdr tx1,w)$
  end$

symbolic procedure t_match(t1,t2)$ th_match(car t1,car t2)$

symbolic procedure th_match(th1,th2)$
  th_match0(th1,th2) and
  (length dl_get cadr th1 = length dl_get cadr th2)$

symbolic procedure th_match0(th1,th2)$
  (car th1 = car th2) and (length cadr th1 = length cadr th2)$

symbolic procedure th_match_(th1,th2)$
  if car th1 = car th2 and th_match1(cadr th1,cadr th2)
    then pfind(cadr th1,cadr th2)
  else nil$

symbolic procedure th_match1(il1,il2)$
  if null il1 then null il2
  else if null(il2 = (il2:=delete(car il1,il2)))
         then th_match1(cdr il1,il2)
  else nil$

symbolic procedure t_neg te$
  if numberp car te then list(-car te)
  else for each x in te collect car x . pv_neg cdr x$

symbolic procedure t_mult(te1,te2)$
  if null te1 then te2
  else if numberp car te1 then c_mult(car te1,te2)
  else if numberp car te2 then c_mult(car te2,te1)
  else t_mult(cdr te1,t_mult1(car te1,te2))$

symbolic procedure t_mult1(te1,te)$
  for each x in te collect t_mult2(te1,x)$

symbolic procedure t_mult2(tt1,tt2)$
  begin scalar tt$
    if cddr tt1 or cddr tt2
      then  rederr list('t_mult2," *** Must be tterms: ",tt1,tt2)$
    tt:=tt1$
    tt1:=t_upright(tt1,car tt2)$
    tt2:=t_upleft(tt2,car tt)$
    return (car tt1 . pv_multc(caadr tt1,cdr tt2))$
  end$

symbolic procedure c_mult(c,te)$
  if null te then nil
  else if numberp car te then list(c*car te)
  else for each x in te collect car x . pv_multc(c,cdr x)$

symbolic procedure t_upright(tt,th)$
  begin scalar th1,tt1$
    th1:=car tt$
    th1:=append(car th1,car th) . append(cadr th1,cadr th)
       . append(cddr th1,cddr th)$
    return (th1 . pv_upright(cdr tt,length cadr th))$
  end$

symbolic procedure t_upleft(tt,th)$
  begin scalar th1,tt1$
    th1:=car tt$
    th1:=append(car th,car th1) . append(cadr th,cadr th1)
       . append(cddr th,cddr th1)$
    return (th1 . pv_upleft(cdr tt,length cadr th))$
  end$

global '(!*debug_times)$
switch debug_times$

symbolic procedure b_expand(u,v)$
  (if !*debug_times then !*basis else !*basis := x
  ) where x = b_expand1(cadr u,cadr v,!*basis,!*basis)$

symbolic procedure b_expand1(t1,t2,bs,bs1)$       % Jul 13, 1994
  % t1,t2 - (th . pv)
  % bs,bs1(r.v.) - (b1 b2 ...) where b::=(th . (pv1 pv2 ...))
  if null bs then reversip bs1
  else if th_match0(car t1,caar bs)
    then b_expand1(t1,t2,cdr bs,b_expand2(car bs,t2,bs1))
  else if th_match0(car t2,caar bs)
    then b_expand1(t1,t2,cdr bs,b_expand2(car bs,t1,bs1))
  else b_expand1(t1,t2,cdr bs,bs1)$

symbolic procedure b_expand2(b,t1,bs)$
  % t1 - (th . pv)
  % b - (th . (pv1 pv2 ...))
  % bs(r.v.) - (b1 b2 ...)
%  b_expand2a(car b,cdr b,t1,nil,bs)$
  b_expand2b(car b,cdr b,t1,bs)$

symbolic procedure b_expand2b(th,b,t1,bs)$
  % t1 - (th . pv)
  % b - (th . (pv1 pv2 ...))
  % bs(r.v.) - (b1 b2 ...)
  if null b then bs
  else b_expand2b(th
         ,cdr b
         ,t1
         ,tsym2(list t_prod(th . car b,t1),bs,nil)
                 )$

symbolic procedure b_expand2a(th,b,t1,b1,bs)$
  % t1 - (th . pv)
  % b - (th . (pv1 pv2 ...))
  % bs(r.v.) - (b1 b2 ...)
  if null b then b_join(caar b1 . b_expand3(b1,nil),bs)
  else b_expand2a(th,cdr b,t1,t_prod(th . car b,t1) . b1,bs)$

symbolic procedure b_expand3(b,b1)$
  if null b then b1
  else b_expand3(cdr b,cdar b . b1)$

symbolic procedure b_join(b,bs)$ b_join1(b,bs,nil)$

symbolic procedure b_join1(b,bs,bs1)$
  if null bs then reversip(if b then b . bs1 else bs1)
  else if b and th_match(car b,caar bs)
    then b_join1(nil,cdr bs,(car b . b_join2(cdr b,cdar bs)) . bs1)
  else b_join1(b,cdr bs,car bs . bs1)$

symbolic procedure b_join2(b1,b2)$
  if null b1 then b2
  else b_join2(cdr b1,insert_pv(car b1,b2))$

symbolic procedure t_prod(t1,t2)$
  % t1,t2 - tensors::=(th . pv)
  % r.v.  - direct product of t1 and t2
  if null ordp(caar t1,caar t2) then t_prod(t2,t1)
  else (append(caar t1,caar t2)
       . il_join(cadar t1,cadar t2)
       . append(cddar t1,cddar t2)
       ) . cdr pv_times('!:pv . cdr t1,'!:pv . cdr t2)$

symbolic procedure il_join(l1,l2)$
  if null l1 then l2
  else if memq(car l1,l2) then wi_new(car l1) . il_join(cdr l1,l2)
  else car l1 . il_join(cdr l1,l2)$

global '(wi_number)$
wi_number:=0$

symbolic procedure wi_new(x)$
  begin scalar z$
    wi_number := wi_number + 1$
    z := intern mkid('!:,wi_number)$   %++++++ intern ?!
    put(z,'windex,list x)$
    return z$
  end$

endmodule;

end;
