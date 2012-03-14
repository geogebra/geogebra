module prod;  % Module for production of finite terms.

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


%       Author: F.Kako                      Hiroshima Univ.
%                               Mon Sep. 7, 1987.
%
%       usage:
%               prod(expression,variable[,lower[,upper]]);
%               lower and upper are optional.

fluid '(!*trsum prod_last_attempt_rules!*);

symbolic procedure simp!-prod u;
%ARGUMENT  CAR U: expression of prefix form;
%         CADR U: kernel;
%        CADDR U: lower bound;
%       CADDDR U: upper bound;
%value            : expression of sq form;
   begin scalar v,y,upper,lower,lower1,dif;
      y := cdr u;
      u := simp!* car u;
      if null numr u then return (1 ./ 1)
       else if atom y then return u;
      if not atom cdr y then <<
          lower := cadr y;
          lower1 := if numberp lower then lower - 1
                     else list('plus,lower,-1);
          upper := if not atom cddr y then caddr y else car y;
          dif := addsq(simp!* upper, negsq simp!* lower);
          if denr dif = 1 then
              if null numr dif
                then return subsq(u,list(!*a2k car y . upper))
                else if fixp numr dif then dif := numr dif
                else dif := nil
           else dif := nil;
          if dif and dif <= 0 then return 1 ./ 1;
          if atom cddr y then upper := nil>>;
      v := !*a2k car y;
      return simp!-prod1(u,v,y,upper,lower,lower1,dif)
   end;


symbolic procedure simp!-prod1(u,v,y,upper,lower,lower1,dif);
   begin scalar w,lst,x,z,flg;
      lst := prod!-split!-exp(u,v);
      w := car lst;
      lst := cdr lst;
      u := 1 ./ 1;
    a:
      if null w then go to b;
      x := simp!-sum1(cdar w,v,y,upper,lower,lower1,dif);
      u := multsq(u,simpexpt list(caar w, prepsq x));
      w := cdr w;
      go to a;
    b:
       if null lst then return u;
            flg := nil;
            z := car lst;
            if !*trsum then <<
               prin2!* "Product ";sqprint z;prin2!*  " w.r.t ";
               xprinf(!*k2f v,nil,nil);terpri!* t >>;
%           z := reorder numr z ./ reorder denr z;
            w := prod!-sq(z,v);
            if w = 'failed then <<
                if !*trsum then <<
                     prin2!* "PROD-SQ failed.";
                     terpri!* t>>;
                w := prod!-unknown(z,v,y,lower,dif);
                flg := car w;
                w := cdr w>>;
            if !*trsum then <<
                 prin2!* "Result  = "; sqprint w;terpri!* t >>;
            if flg then goto c;
            if upper then
                w := multsq(subsq(w,list(v . upper)),
                           invsq subsq(w,list(v . lower1)))
             else if lower then
                w := multsq(w , invsq subsq(w, list(v . lower1)));
   c:
            u := multsq(u,w);
            lst := cdr lst;
            goto b
   end;


put('prod,'simpfn,'simp!-prod);

%*********************************************************************
%       Case of other functions
%********************************************************************;

symbolic procedure prod!-unknown(u,v,y,lower,dif);
   begin scalar z,w,uu;
     if null dif then <<
          z := 'prod . (prepsq u . list car y);
          if w := opmtch z then return (nil . simp w)
           else if null cdr y then return (t . mksq(z,1));

          load_package 'factor; % try to find factors
          uu := old_factorize prepf numr u;
          if length uu > 2 then <<
                z := 'times . foreach uuu in cdr uu collect
                 ('prod . ( prepsq
                        multsq(if pairp uuu and eq(car uuu,'!*sq)
                                then cadr uuu
                                else simp uuu,1 ./ denr u)) . y);
                return (t . simp z) >>;

          z := 'prod . (prepsq u . y); % try to apply rules
          let prod_last_attempt_rules!*;
          w:= opmtch z;
          rule!-list (list prod_last_attempt_rules!*,nil);
          return (t . if w then simp w else mksq(z,1))>>;
%         return (t . if w := opmtch z then simp w else mksq(z,1))>>;
     z := 1 ./ 1;
  a:
     if dif < 0 then return (t . z);
     z := multsq(z,subsq(u,list(v . list('plus,lower,dif))));
     dif := dif - 1;
     goto a
   end;

   prod_last_attempt_rules!* :=
    algebraic  <<
    { prod(~f * ~g,~n,~anf,~ende) => prod(f,n,anf,ende) *
                                       prod(g,n,anf,ende)
                when g neq 1 and
                         or(numberp prod(f,n,anf,ende),
                         part(prod(f,n,anf,ende),0) neq prod,
                         part(prod(g,n,anf,ende),0) neq prod),
        prod(~f / ~g,~n,~anf,~ende) => prod(f,n,anf,ende) /
                                       prod(g,n,anf,ende)
                when g neq 1 and
                        or(numberp prod(f,n,anf,ende), % 1?
                        part(prod(f,n,anf,ende),0) neq prod,
                        part(prod(g,n,anf,ende),0) neq prod),
        prod(expt(~f,~k),~n,~anf,~ende) =>
                (for ii:=1:k product prod(f,n,anf,ende))
                when neq(part(prod(f,n,anf,ende),0),prod)} >>;

%*********************************************************************
%       Product of rational function
%********************************************************************;

symbolic procedure prod!-sq(u,v);
%ARGUMENT U : expression of s-q;
%         V : kernel;
%value        : expression of sq (result of product.);
   begin scalar gn,p1n,p2n,rn,qn,z,k,x,y;
      if null numr u then return 1 ./ 1;
      x := setkorder list v;
      qn := reorder numr u;
      rn := reorder denr u;
      if !*trsum then <<
            prin2t " *** Product of A(n) = qn/rn with ***";
            prin2!* "QN = ";xprinf(qn,nil,nil);terpri!* t;
            prin2!* "RN = ";xprinf(rn,nil,nil);terpri!* t>>;
      if nonpolyp(qn,v) or nonpolyp(rn,v) then go to fail;
      k := compress explode '!+j;
      z := integer!-root2(resultant(qn,nsubsf(rn,v,k),v),k);
      if !*trsum then <<
         prin2 "Root of resultant(q(n),r(n+j)) are "; prin2t z >>;
      p2n := p1n := 1;
      while z do <<
            k := car z;
            gn := gcdf!*(qn,nsubsf(rn,v,k));
            qn := quotf!*(qn,gn);
            rn := quotf!*(rn,nsubsf(gn,v, -k));
            if k > 0 then
                while (k := k - 1)>=0 do <<
                   p1n := multf(p1n,nsubsf(gn,v, -k));
                   if y := prod!-nsubsf(gn,v,-k)
                     then p2n := multf(p2n,y)>>
             else if k < 0 then
                while k < 0 do <<
                   p2n := multf(p2n,nsubsf(gn,v, -k));
                   if y := prod!-nsubsf(gn,v,-k)
                     then p1n := multf(p1n,y);
                   k := k + 1>>;
            z := cdr z>>;
      if depend!-f(qn,v) or depend!-f(rn,v) then go to fail;
      u := multsq(p1n ./ p2n, simpexpt list(prepsq (qn ./ rn), v));
      if !*trsum then <<
            prin2t " *** Product of rational function calculated ***";
            prin2!* "    P(n) = "; sqprint u;terpri!* t;
            terpri!* t>>;
      setkorder x;
      return (reorder numr u ./ reorder denr u);
      return u;
  fail:
      if !*trsum then <<
            prin2t " *** Product of rational function failed ***";
            terpri!* t>>;
      setkorder x;
      return 'failed
   end;

symbolic procedure prod!-nsubsf(u,kern,i);
% ARGUMENT U : expression of sf;
%          KERN : kernel;
%          I : integer;
% value        : expression of sf;
   begin scalar x,z,n;
      x := setkorder list kern;
      u := reorder u;
      z := nil;
   a:
      if domainp u or not(mvar u eq kern) then goto b;
      z := addf(z,lc u);
      n := degr(u,kern) - degr(red u,kern);
      u := red u;
      if i = 0 then if n = 0 then nil else z := nil
       else z := multf(z,expt(i,n));
      go to a;
   b:
      z := addf(z,u);
      setkorder x;
      return reorder z
   end;

%*********************************************************************
%       integer (positive and negative) root isolation
%********************************************************************;

symbolic procedure integer!-root2(u,v);
% Produce a list of all integer root of U;
   begin scalar x,root,n,w;
        x := setkorder list v;
        u := reorder u;
        if domainp u or not(mvar u eq v) then go to a;
        u := numr cancel(u ./ lc u);
        w := u;                                 % get trailing term;
        while not domainp w and mvar w eq v and cdr w do
            w := cdr w;
        if (n := degr(w,v)) > 0 then <<
            w := lc w;
            while n > 0 do <<
               root := 0 . root;
               n := n - 1>>>>;
        n := dfactors lowcoef w;                % factor tail coeff.;
        w := (v . 1) . 1;
        while n do <<
            if not testdivide(u,v,car n) then <<
                root := car n . root;
                u := quotf!*(u, (w . - car n))>>
             else if not testdivide(u,v,- car n) then <<
                root := (- car n) . root;
                u := quotf!*(u, (w . car n))>>
             else n := cdr n>>;
  a:
        setkorder x;
        return root
   end;

endmodule;

end;
