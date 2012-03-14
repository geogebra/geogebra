%======================================================
%       Name:           dummy2.red - dummy indices package
%       Author:         A.Kryukov (kryukov@npi.msu.su)
%       Copyright:      (C), 1993, A.Kryukov
%------------------------------------------------------
%       Version:        2.34
%       Release:        Dec. 15, 1993
%                       Mar. 24, 1996 mk_ddsym1
%======================================================

module dummy2$

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


global '(!*basis); fluid '(!*debug)$

symbolic procedure adddummy(tt)$
  % tt - tensor::=(!:tensor . ((th1 . pv1) ...)))
  % (r.v.) - basis::=((th1 . (pv1 pv2 ...)) ...)
  adddummy0(cdr tt,!*basis)$

symbolic procedure adddummy0(tt,b)$
  % tt - ((th1 . pv1) ...)
  % b(r.v.) - basis::=((th1 . (pv1 pv2 ...)) ...)
  if null tt then reversip b
  else adddummy0(cdr tt,adddummy0b(mk_dsym0 car tt,b))$

symbolic procedure adddummy0b(u,b)$
  % u - (th . (pv1 pv2 ...))
  %b,b1(r.v.) - basis
  if null cdr u then b
  else adddummy0b(car u . cddr u,adddummy0a(car u . cadr u,b,nil))$

symbolic procedure adddummy0a(t1,b,b1)$
  % t1 - (th . pv)
  % b,b1(r.v.) - basis::=((th1 . (pv1 pv2 ...)) ...)
  if null b then if null t1 then reversip b1
                 else reversip(adddummy1(t1
                                 ,gperm length cadar t1
                                 ,nil
                               ) . b1
                      )
  else if null t1 then adddummy0a(nil,cdr b,car b . b1)
%  else if th_match(car t1,caar b) then adddummy0a(nil,b,b1)
  else if th_match0(car t1,caar b)
    then adddummy0a(nil,cdr b,adddummy1(t1
                                ,gperm length cadar t1
                                ,car t1 . cdar b
                              ) . b1
                   )
  else adddummy0a(t1,cdr b,car b . b1)$

symbolic procedure adddummy1(t1,plist,b)$
  << if !*debug
        then << terpri()$
                write " DEBUG: adddummy1"$
                terpri()$
                t_pri1('!:tensor . list(t1),t)$
                terpri()$
                for each z in cdr x
                do t_pri1('!:tensor . list(car x . z),t)$
                write " DEBUG=",length cdr x$ terpri()$
                         >>$
     x
  >>  where x=adddummy1a(t1,plist,b)$

symbolic procedure adddummy1a(t1,plist,b)$
  % t1 - (th . pv)
  % plist - (p1 p2 ...)
  % b,w(r.v.) - (th1 . (pv1 pv2 ...))
  if null plist then b
  else adddummy1a(t1
         ,cdr plist
         ,(if null b then car t1 else car b)
          . insert_pv(pappl_pv(car plist,cdr t1)
              ,if null b then b else cdr b
            )
       )$

symbolic procedure mk_dsym0 t1$
  car t1 . append(cdr mk_dsym t1,cdr mk_ddsym t1)$

symbolic procedure mk_dsym(t1)$
  % t1 - (th . pv)
  car t1 . mk_dsym1(cdr t1
             ,nil
             ,mk_flips(cadar t1,dl_get cadar t1,nil)
           )$

symbolic procedure mk_dsym1(pv1,pv2,fs)$
  % pv1,pv2(r.v.) - pvector
  % fs - permutation list
  if null fs then pv2
  else mk_dsym1(pv1
         ,pv_add(pv1,pv_neg pv_applp(pv1,car fs)) . pv2
%         ,pv_add(pv1,pv_neg pappl_pv(car fs,pv1)) . pv2
         ,cdr fs
       )$

symbolic procedure dl_get(il)$ dl_get2(il,nil)$

symbolic procedure dl_get2(il,d_alst)$
  if null il then d_alst
  else if get(car il,'dummy)
    then dl_get2(cdr il,di_insert(car il,d_alst,nil))
  else dl_get2(cdr il,d_alst)$

symbolic procedure eqdummy(x,y)$
  x and car get(x,'dummy) eq car get(y,'dummy)$

symbolic procedure di_insert(di,d_alst1,d_alst2)$
  if null d_alst1 then if di then ((di . nil) . d_alst2)
                       else d_alst2
  else if eqdummy(di,caar d_alst1)
    then di_insert(nil,cdr d_alst1,(caar d_alst1 . di) . d_alst2)
  else di_insert(di,cdr d_alst1,car d_alst1 . d_alst2)$

symbolic procedure il_update(il,d_alst)$
  il_update1(il,d_alst,nil)$

symbolic procedure il_update1(il,d_alst,il1)$
  if null il then reversip il1
  else ((if null y then il_update1(cdr il,d_alst,car il . il1)
          else ((if x
                   then il_update1(cdr il,delete(x,d_alst),cdr x . il1)
                 else begin scalar z,u$
                        z:=di_next(d_alst)$
                        u:=car z$
                        rplaca(z,y)$
                        return il_update1(cdr il,d_alst,u . il1
                               )$
                      end
                ) where x=assoc(y,d_alst)
               )
        ) where y=get(car il,'dummy)
       )$

symbolic procedure di_next(dl)$
  if null dl then rederr list('di_next,"+++ Can't find next dummy")
  else if get(caar dl,'dummy) then car dl
  else di_next(cdr dl)$

symbolic procedure mk_flips(il,dl,fs)$
  if null dl then reversip fs
  else mk_flips(il,cdr dl,mk_flip(il,car dl) . fs)$

symbolic procedure mk_flip(il,x)$
  pfind(il,mk_flip1(il,x,nil))$

symbolic procedure mk_flip1(il,x,w)$
  if null il then reverse w
  else if car x eq car il
    then mk_flip1(cdr il,(cdr x . car x),cdr x . w)
  else mk_flip1(cdr il,x,car il . w)$

symbolic procedure mk_flip_(il,di)$
  begin scalar il1,il2,w,w1,ok,x$
    w:=il$
    while w and null ok do if null car w eq caar di
                 then << il1:=car w . il1$ w:=cdr w >>
               else ok:=t$
    if null w then rederr 1;
    il1:=car w . il1$
    il2:=il1$
    w:=cdr w$
    ok:=nil$
    while w do if null car w eq cdar di
                 then << il2:=car w . il2$ w:=cdr w >>
               else ok:=t$
    if null w then rederr 2;
    il2:=car w . il2$
    w:=cdr w$
    w1:=il2$
    while w do << w1:=car w . w1$ w:=cdr w >>$
    x:=car il1$
    rplaca(il1,car il2)$
    rplaca(il2,x)$
    return pfind(il,reversip w)$
  end$

%++++++++++++++++++++++++++++++++++

symbolic procedure mk_ddsym(t1)$
  % t1 - (th . pv)
  % r.v. - (th . (pv1 pv2 ...))
  car t1 . mk_ddsym1(cdr t1
             ,nil
             ,mk_fflips(cadar t1,dl_get cadar t1,nil)
           )$

symbolic procedure mk_ddsym1(pv,pvs,fs)$
  if null fs then pvs
  else mk_ddsym1(pv
%         ,pv_add(pv,pv_neg pappl_pv(car fs,pv)) . pvs % -A.K. 24.03.96
         ,pv_add(pv,pv_neg pv_applp(pv,car fs)) . pvs  % +A.K. 24.03.96
         ,cdr fs
       )$

symbolic procedure mk_fflips(il,dl,fs)$
  if null dl then fs
  else mk_fflips(il,cdr dl,mk_fflips1(il,car dl,cdr dl,fs))$

symbolic procedure mk_fflips1(il,dp,dl,fs)$
  if null dl then fs
  else mk_fflips1(il,dp,cdr dl,mk_fflip1(il,dp,car dl) . fs)$

symbolic procedure mk_fflip1(il,dp1,dp2)$
  pfind(il,mk_fflip2(il,dp1,dp2,nil))$

symbolic procedure mk_fflip2(il,dp1,dp2,il1)$
  % dp1,dp2 - (di1 . di2) - contracted indecies
  if null il then reverse il1
  else ((if null(x=get(car dp1,'dummy)) and null(x=get(car dp2,'dummy))
           then mk_fflip2(cdr il,dp1,dp2,car il . il1)
         else if x=get(car dp2,'dummy)
           then mk_fflip2(il,dp2,dp1,il1)
         else mk_fflip2(cdr il,dp1,cdr dp2 . car dp2,car dp2 . il1)
        ) where x=get(car il,'dummy)
       )$

endmodule;

end;
