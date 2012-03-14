module gint;  % Support for gaussian integers (complex numbers).

% Author: Eberhard Schruefer.

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


global '(domainlist!*);

fluid '(!*complex !*gcd);

switch complex;

domainlist!* := union('(!:gi!:),domainlist!*);

symbolic procedure setcmpxmode(u,bool);
   % Sets polynomial domain mode in complex case.
   begin scalar x,y;
      x := get(u,'tag);
      if u eq 'complex
        then if null dmode!*
               then return if null bool then nil
                else <<put('i,'idvalfn,'mkdgi);
                       setdmode1('complex,bool)>>
         else if null bool
                then return if null !*complex then nil
                else if get(dmode!*,'dname) eq 'complex
                      then <<remprop('i,'idvalfn);
                             setdmode1('complex,nil)>>
                     else <<remprop('i,'idvalfn);
                           setdmode1(get(get(dmode!*,'realtype),'dname),
                                       t)>>
         else if dmode!* eq '!:gi!: or get(dmode!*,'realtype)
            then return nil
         else if not (y := get(dmode!*,'cmpxtype))
               then dmoderr(dmode!*,x)
         else <<put('i,'idvalfn,get(y,'ivalue));
                     return setdmode1(get(y,'dname),bool)>>
      else if null bool then
         if u eq (y := get(get(dmode!*,'realtype),'dname)) then
            <<put('i,'idvalfn,'mkdgi); return setdmode1('complex,t)>>
            else if null y then return nil
            else offmoderr(u,y)
      else <<u := get(u,'tag);
             y := get(u,'cmpxtype);
             if null y then dmoderr(u,'!:gi!:);
             put('i,'idvalfn,get(y,'ivalue));
             return setdmode1(get(y,'dname),bool)>>
   end;

% Used by gcdk.

symbolic procedure intgcdd(u,v);
   if null u then v
   else if atom u then
      if atom v then gcdn(u,v) else gcdn(cadr v,gcdn(cddr v,u))
   else if atom v then intgcdd(v,u)
      else intgcdd(cadr u,intgcdd(cddr u,v));

put('complex,'tag,'!:gi!:);
put('!:gi!:,'dname,'complex);
put('!:gi!:,'i2d,'!*i2gi);
put('!:gi!:,'minusp,'giminusp!:);
put('!:gi!:,'zerop,'gizerop!:);
put('!:gi!:,'onep,'gionep!:);
put('!:gi!:,'plus,'giplus!:);
put('!:gi!:,'difference,'gidifference!:);
put('!:gi!:,'times,'gitimes!:);
put('!:gi!:,'quotient,'giquotient!:);
put('!:gi!:,'divide,'gidivide!:);
put('!:gi!:,'gcd,'gigcd!:);
put('!:gi!:,'factorfn,'gifactor!:);
% put('!:gi!:,'rationalizefn,'girationalize!:);
put('!:gi!:,'prepfn,'giprep!:);
put('!:gi!:,'intequivfn,'gintequiv!:);
put('!:gi!:,'specprn,'giprn!:);
put('!:gi!:,'prifn,'giprn!:);
put('!:gi!:,'cmpxfn,'mkgi);

put('!:gi!:,'unitsfn,'!:gi!:unitconv);

symbolic procedure !:gi!:unitconv(u,v);
   unitconv(u,v,get('!:gi!:,'units));

put('!:gi!:,'units,'(((!:gi!: 0 . 1) . (!:gi!: 0 . -1))
             ((!:gi!: 0 . -1) . (!:gi!: 0 . 1))));

symbolic procedure unitconv(u,v,w);
   begin scalar z;
   a: if null w then return u;
      z := quotf1(v,caar w);
      if null z or not fixp z then <<w := cdr w; go to a>>;
      z := multf(denr u,cdar w);
      w := multf(numr u,cdar w);
      if minusf z then <<w := negf w; z := negf z>>;
      return w ./ z
   end;

symbolic procedure !*i2gi u; '!:gi!: . (u . 0);

symbolic procedure giminusp!: u;
   %*** this is rather a test for u being in a canonical form! ***;
   if cadr u = 0 then minusp cddr u else minusp cadr u;

symbolic procedure gizerop!: u;
   cadr u = 0 and cddr u = 0;

symbolic procedure gionep!: u;
   cadr u=1 and cddr u=0;

symbolic procedure gintequiv!: u;
   if cddr u=0 then cadr u else nil;

symbolic procedure mkdgi u;
   ('!:gi!: . (0 . 1)) ./ 1;

symbolic procedure mkgi(re,im);
   '!:gi!: . (re . im);

symbolic procedure giplus!:(u,v);
   mkgi(cadr u+cadr v,cddr u+cddr v);

symbolic procedure gidifference!:(u,v);
   mkgi(cadr u-cadr v,cddr u-cddr v);

symbolic procedure gitimes!:(u,v);
   (lambda r1,i1,r2,i2;
       mkgi(r1*r2-i1*i2,r1*i2+r2*i1))
    (cadr u,cddr u,cadr v,cddr v);

symbolic procedure giquotient!:(u,v);
% Quotient when the quotient is exact, otherwise zero.
   begin integer r1,i1,r2,i2,d; scalar rr,ii;
     r1 := cadr u; i1 := cddr u;
     r2 := cadr v; i2 := cddr v;
     d := r2*r2+i2*i2;
     rr := divide(r1*r2+i1*i2,d);
     ii := divide(i1*r2-i2*r1,d);
     return if cdr ii=0 and cdr rr=0 then mkgi(car rr,car ii)
             else '!:gi!: . (0 . 0)
   end;

symbolic procedure gidivide!:(u,v);
% Rounded quotient and corresponding remainder. This rounds to the
% NEAREST quotient. In some cases there can be several such (eg when
% dividing by 2) and in that case use resolve the ambiguity by selecting
% the result closes to zero in real and imaginary parts.
   begin integer r1,i1,r2,i2,d,rr,ir,rq,iq;
     r1 := cadr u; i1 := cddr u;
     r2 := cadr v; i2 := cddr v;
     d := r2*r2+i2*i2;
     rq := r1*r2+i1*i2;
     iq := i1*r2-i2*r1;
     rq := car divide(2*rq+if rq<0 then -d+1 else d-1,2*d);
     iq := car divide(2*iq+if iq<0 then -d+1 else d-1,2*d);
     rr := r1-(rq*r2-iq*i2);
     ir := i1-(iq*r2+rq*i2);
     return mkgi(rq,iq) . mkgi(rr,ir)
   end;

symbolic procedure giremainder(u,v);
% Remainder as from gidivide:
   begin integer r1,i1,r2,i2,d,rr,ir,rq,iq;
     r1 := cadr u; i1 := cddr u;
     r2 := cadr v; i2 := cddr v;
     d := r2*r2+i2*i2;
     rq := r1*r2+i1*i2;
     iq := i1*r2-i2*r1;
     rq := car divide(2*rq+if rq<0 then -d+1 else d-1,2*d);
     iq := car divide(2*iq+if iq<0 then -d+1 else d-1,2*d);
     rr := r1-(rq*r2-iq*i2);
     ir := i1-(iq*r2+rq*i2);
     return '!:gi!: . (rr . ir)
   end;

symbolic procedure gigcd!:(u,v);
   % Straightforward Euclidean algorithm.
   if gizerop!: v then fqa u else gigcd!:(v,giremainder(u,v));

symbolic procedure fqa u;
   %calculates the unique first-quadrant associate of u;
  if cddr u=0 then abs cadr u
   else if cadr u=0 then '!:gi!: . (abs cddr u . 0)
   else if (cadr u*cddr u)>0 then
                  '!:gi!: . (abs cadr u . abs cddr u)
   else '!:gi!: . (abs cddr u . abs cadr u);

symbolic procedure gifactor!: u;
   % Trager's modified version of Van der Waerdens algorithm.
   begin scalar x,y,norm,aftrs,ftrs,mvu,dmode!*,!*exp,w,z,l,bool;
         integer s;
     !*exp := t;
     if realp u
        then u := cdr factorf u
      else u := list(u . 1);
     w := 1;
     for each f in u do begin
     u := car f;
     dmode!* := '!:gi!:;
     mvu := power!-sort powers u;
     bool := contains!-oddpower mvu;
     if realp u and bool
        then <<u := normalize!-lcgi u;
               w := multd(car u,w);
               aftrs := (cdr u . 1) . aftrs;
               return>>;
     mvu := caar mvu;
     y := u;
     go to b;
     a: l := list(mvu . prepf addf(!*k2f mvu,multd(s,!*k2f 'i)));
        u := numr subf1(y,l);
     b: if realp u
           then if bool
                   then <<y := normalize!-lcgi y;
                          w := multd(car y,w);
                          aftrs := (cdr y . 1) . aftrs;
                          return>>
                 else <<s := s-1; go to a>>;
        norm := multf(u,conjgd u);
        if not sqfrp norm then <<s := s-1; go to a>>;
     dmode!* := nil;
     ftrs := factorf norm;
     dmode!* := '!:gi!:;
     if null cddr ftrs then <<y := normalize!-lcgi y;
                              w := multd(car y,w);
                              aftrs := (cdr y . 1) . aftrs;
                              return>>;
     w := car ftrs;
     l := if s=0 then nil
           else list(mvu . prepf addf(!*k2f mvu,
                                      negf multd(s,!*k2f 'i)));
     for each j in cdr ftrs do
         <<x := gcdf!*(car j,u);
           u := quotf!*(u,x);
           z := if l then numr subf1(x,l) else x;
           z := normalize!-lcgi z;
           w := multd(car z,w);
           aftrs := (cdr z . 1) . aftrs>>;
     w := multf(u,w) end;
     return w . aftrs
   end;

symbolic procedure normalize!-lcgi u;
   % Normalize lnc by using units as canonsq would do it.
   begin scalar l,x,y;
     l := lnc u;
     if numberp l then return if minusp l then (-1) . negf u
                               else 1 . u;
     x := get('!:gi!:,'units);
     a: if null x then return 1 . u;
        y := quotf1(l,caar x);
        if null y or null fixp y then <<x := cdr x; go to a>>;
        u := multd(cdar x,u);
        return if minusf u then negf caar x . negf u
                else caar x . u
   end;

symbolic procedure contains!-oddpower u;
   if null u then nil
    else if evenp cdar u then contains!-oddpower cdr u
    else t;

symbolic procedure power!-sort u;
   begin scalar x,y;
     while u do
       <<x := maxdeg(cdr u,car u);
         u := delallasc(car x,u);
         y := x . y>>;
     return y
   end;

symbolic procedure sqfrp u;
   % Square free test for poly u over the integers.
   % It works best with ezgcd on.
   begin scalar !*ezgcd, dmode!*;
     % Make sure ezgcd loaded.
     if null getd 'ezgcdf1 then load_package ezgcd;
     !*ezgcd := t;
     return domainp gcdf!*(u,diff(u,mvar u))
   end;

symbolic procedure realp u;
   if domainp u
     then atom u
        or not get(car u,'cmpxfn)
        or cddr u = cddr apply1(get(car u,'i2d),1)
    else realp lc u and realp red u;

symbolic procedure fd2f u;
   if atom u then u
    else if car u eq '!:gi!:
        then addf(!*n2f cadr u,multf(!*k2f 'i,!*n2f cddr u))
    else addf(multf(!*p2f lpow u,fd2f lc u),fd2f red u);


% symbolic procedure giprep!: u;  %giprep1 cdr u;
%   prepsq!* addsq(!*n2f cadr u ./ 1,
%                  multsq(!*n2f cddr u ./ 1, !*k2q 'i));

% symbolic procedure giprep1 u;  %not used now;
%    if cdr u=0 then car u
%     else if car u=0 then retimes list(cdr u,'i)
%     else begin scalar gn;
%            gn := gcdn(car u,cdr u);
%            return retimes list(gn,
%                       replus list(car u/gn,retimes list(cdr u/gn,'i)))
%          end;

symbolic procedure giprep!: u;
   <<if im=0 then rl else if rl=0 then giprim im
     else if im<0 then list('difference,rl,giprim(minus im))
     else list('plus,rl,giprim im)>>
   where rl=cadr u,im=cddr u;

symbolic procedure giprim im; if im=1 then 'i else list('times,im,'i);

symbolic procedure giprn!: v;
   (lambda u;
    if atom u or (car u eq 'times) then maprin u
     else <<prin2!* "("; maprin u; prin2!* ")" >>) giprep!: v;

% symbolic procedure girationalize!: u;
%    %Rationalizes standard quotient u over the gaussian integers.
%    begin scalar x,y,z;
%       y := denr u;
%       z := conjgd y;
%       if y=z then return u;
%       x := multf(numr u,z);
%       y := multf(y,z);
%       return x ./ y
%    end;

symbolic procedure girationalize!: u;
   % Rationalizes standard quotient u over the gaussian integers.
   begin scalar y,z,!*gcd;
      !*gcd := t;
      if y=(z := conjgd(y := denr u)) then return u;
      % Remove from z any real polynomial factors of y and z.
      z := quotf(z,quotf(
         gcdf(addf(y,z),multf(addf(z,negf y),'!:gi!: . (0 . 1))),2));
      % The following subs2 can undo the above if !*match is non-NIL.
%     return subs2 gigcdsq(multf(numr u,z),multf(y,z))
      return gigcdsq(multf(numr u,z),multf(y,z))
   end;

symbolic procedure gigcdsq(x,y); % remove integer common factor.
   <<if x then
        (<<if d neq 1 and (d := giintgcd(x,d)) neq 1 then
              <<x := quotf(x,d); y := quotf(y,d)>> >>
         where d=giintgcd(y,0)); x ./ y >>;

symbolic procedure giintgcd(u,d);
   if d=1 then 1 else if null u then d else if atom u then gcdn(u,d)
   else if eqcar(u,'!:gi!:) then gcdn(cadr u,gcdn(cddr u,d))
   else giintgcd(lc u,giintgcd(red u,d));

symbolic procedure conjgd u;
   begin scalar x;
      return if atom u then u
              else if domainp u and (x := get(car u,'cmpxfn))
               then
                  apply2(x,cadr u,
                   if numberp cddr u then -cddr u
                       % Allow for tagged parts of complex object.
                       else if domainp cddr u and not numberp caddr u
                          then !:minus cddr u
                       else cdr !:minus (get(car u,'realtype).cddr u))
              else if domainp u then u   % Should be a real number now.
              else addf(multpf(lpow u,conjgd lc u),conjgd red u)
   end;

initdmode 'complex;

endmodule;

end;
