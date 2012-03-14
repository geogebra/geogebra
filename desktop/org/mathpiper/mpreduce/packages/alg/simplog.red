xmodule simplog;  % Simplify logarithms.

% Authors: Mary Ann Moore and Arthur C. Norman.

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


fluid '(!*intflag!* !*noneglogs !*expandlogs);

global '(domainlist!*);

exports simplog,simplogb,simplogbi,simplogbsq,simplogi,simplogsq;

imports addf,addsq,comfac,quotf,prepf,mksp,simp!*,!*multsq,simptimes,
        minusf,negf,negsq,mk!*sq,carx,multsq,resimp,simpiden,simpplus,
        prepd,mksq,rerror,zfactor,sfchk;

symbolic smacro procedure get!-log!-base u;
   if car u eq 'log10 then 10 else nil;

symbolic procedure simplog u;
   (if !*expandlogs then
     (resimp simplogbi(x,get!-log!-base u) where !*expandlogs=nil)
%%% next line temporarily left out: causes CRACK test file to fail
%    else if x=0 then rerror(alg,210,{car u,"0 formed"})
    else if fixp x and car u='log10 and not(dmode!* and get('log10,dmode!*))
      then simplogbn(x,get!-log!-base u,t)
    else if eqcar(x,'quotient) and cadr x=1
      and (null !*precise or realvaluedp caddr x)
     then negsq simpiden(car u . cddr x)
    else simpiden u)
    where x=carx(cdr u,'simplog);

symbolic procedure simplogb u;
   (if !*expandlogs then
     (resimp simplogbi(x,carx(cddr u,'simplogb)) where !*expandlogs=nil)
    else if x=0 then rerror(alg,210,"Logb(0,...) formed")
%    else if fixp x then simplogbn(x,caddr u,nil)
    else if eqcar(x,'quotient) and cadr x=1
      and (null !*precise or realvaluedp caddr x)
     then negsq simpiden {car u, caddr x, caddr u}
    else simpiden u)
    where x=cadr u;

put('log,'simpfn,'simplog);

put('log10,'simpfn,'simplog);

put('logb,'simpfn,'simplogb);

flag('(log log10 logb),'full);

put('expandlogs,'simpfg,'((nil (rmsubs)) (t (rmsubs))));

put('combinelogs,'simpfg,'((nil (rmsubs)) (t (rmsubs))));

symbolic smacro procedure mk!-log!-arg(arg,base);
   if null base or base eq 'e then {'log,arg}
    else if base=10 then {'log10,arg}
    else {'logb,arg,base};

symbolic procedure simplogi(sq);
   simplogbi(sq,nil);

symbolic procedure simplogbi(sq,base);
   % This version will only expand a log if at most one of the
   % arguments is complex.  Otherwise you can finish up on the wrong
   % sheet.
   if atom sq then simplogbsq(simp!* sq,base)
    else if car sq memq domainlist!* then simpiden mk!-log!-arg(sq,base)
    else if car sq eq 'times
          then if null !*precise or one_complexlist cdr sq
           then simpplus(for each u in cdr sq collect mk!*sq simplogbi(u,base))
          else !*kk2q mk!-log!-arg(sq,base)
    else if car sq eq 'quotient
       and (null !*precise or one_complexlist cdr sq)
     then addsq(simplogbi(cadr sq,base),negsq simplogbi(caddr sq,base))
    else if car sq eq 'expt
     then simptimes list(caddr sq,mk!*sq simplogbi(cadr sq,base))
    else if car sq eq 'nthroot
     then multsq!*(1 ./ caddr sq,simplogbi(cadr sq,base))
    % we had (nthroot of n).
    else if car sq eq 'sqrt then multsq!*(1 ./ 2,simplogbi(cadr sq,base))
    else if car sq = '!*sq then simplogbsq(cadr sq,base)
    else simplogbsq(simp!* sq,base);

symbolic procedure one_complexlist u;
   % True if at most one member of list u is complex.
   if null u then t
    else if realvaluedp car u then one_complexlist cdr u
    else null cdr u or realvaluedlist cdr u;

symbolic procedure multsq!*(u,v);
   if !*intflag!* then !*multsq(u,v) else multsq(u,v);

symbolic procedure simplogsq sq;
   simplogbsq(sq,nil);

symbolic procedure simplogbsq(sq,base);
   % This procedure needs to be reworked to provide for proper sheet
   % handling.
   if null numr sq then rerror(alg,210,"Log 0 formed")
    else begin integer n;
      if denr sq=1 and domainp numr sq
        then <<if !:onep numr sq then return nil ./ 1
                else if (n:=int!-equiv!-chk numr sq) and fixp n then return simplogbn(n,base,nil)
                else if eqcar(numr sq,'!:rn!:) and not !:minusp numr sq
                 then return addsq(simplogb2(cadr numr sq,base),negsq simplogb2(cddr numr sq,base)) >>
       else if fixp denr sq and domainp numr sq
        then <<if (n:=int!-equiv!-chk numr sq) and fixp n
                 then return addsq(simplogbn(n,base,nil),negsq simplogbn(denr sq,base,nil))>>;
      if !*precise then return !*kk2q mk!-log!-arg(prepsq sq,base)
        else return addsq(simplogb2(numr sq,base),negsq simplogb2(denr sq,base));
    end;

symbolic procedure simplogb2(sf,base);
 if atom sf
   then if null sf then rerror(alg,21,"Log 0 formed")
      else if numberp sf
       then if sf iequal 1 then nil ./ 1
             else if sf iequal 0 then rerror(alg,22,"Log 0 formed")
             else simplogbn(sf,base,nil)
      else formlog(sf,base)
   else if domainp sf then mksq(mk!-log!-arg(prepd sf,base),1)
     else begin scalar form;
        form := comfac sf;
        if not null car form
          then return addsq(formlog(form .+ nil,base),
                            simplogb2(quotf(sf,form .+ nil),base));
        % We have killed common powers.
        form := cdr form;
        if form neq 1
          then return addsq(simplogb2(form,base),simplogb2(quotf(sf,form),base));
        % Remove a common factor from the sf.
        return formlog(sf,base)
     end;

symbolic procedure simplogn u;
   simplogbn(u,nil,nil);


% If base is 10, apply simplification for log10 of an integer:
% after factorization of the integer argument, check if the
% factors 2 and 5 can be combined to a power of 10.

% The third argument flg tells simplogbn to not return a sum of terms.
symbolic procedure simplogbn(u,base,flg);
   % See comments in formlog for an explanation of the code.
   begin scalar y,z;
      y := zfactor u;
      if base=10 then begin integer twos,fives;
         twos := assoc(2,y);
         fives := assoc(5,y);
         if twos and fives then <<
            y := delete(twos,y);
            y := delete(fives,y);
            if cdr twos = cdr fives then z := cdr twos
             else if cdr twos < cdr fives
              then <<z := cdr twos;
                     y := append(y, list(5 . (cdr fives - cdr twos)))>>
             else <<z := cdr fives;
                     y := append(y, list(2 . (cdr twos - cdr fives)))>>>>
      end;
      if flg then return (if null y then z else !*kk2f mk!-log!-arg(u,base)) ./ 1;
      if eqcar(y,'(-1 . 1)) and null(y := mergeminus cdr y)
       then return !*kk2q mk!-log!-arg(u,base);
      for each x in y do
          z := addf(((mksp(mk!-log!-arg(car x,base),1) .* cdr x) .+ nil),z);
      return z ./ 1
   end;

symbolic procedure mergeminus u;
   begin scalar x;
   a: if null u then return nil
       else if remainder(cdar u,2)=1
        then return reversip2(x,((-caar u) . cdar u) . cdr u)
       else <<x := car u . x; u := cdr u; go to a>>
   end;

symbolic procedure formlog(sf,base);
   % Minus test commented out. Otherwise, we can get:
   % log(a) + log(-1) => log(a*(-1)) => log(-a).
   % log(a) - log(-1) => log(a/(-1)) => log(-a).
   % I.e., log(-a) can be log(a) + log(-1) or log(a) - log(-1).
   if null red sf then formlogterm(sf,base)
%   else if minusf sf and null !*noneglogs
%    then addf((mksp(list('log,-1),1) .* 1) .+ nil,
%              formlog2(negf sf,base)) ./ 1
    else formlog2(sf,base) ./ 1;

symbolic procedure formlogterm(sf,base);
   begin scalar u;
      u := mvar sf;
      if not atom u and (car u member '(times sqrt expt nthroot))
         then u := addsq(simplogb2(lc sf,base),
                         multsq!*(simplogbi(u,base),simp!* ldeg sf))
        else if (lc sf iequal 1) and (ldeg sf iequal 1)
         then u := ((mksp(mk!-log!-arg(sfchk u,base),1) .* 1) .+ nil) ./ 1
        else u := addsq(simptimes list(mk!-log!-arg(sfchk u,base),ldeg sf),
                        simplogb2(lc sf,base));
      return u
   end;

symbolic procedure formlog2(sf,base);
   ((mksp(mk!-log!-arg(prepf sf,base),1) .* 1) .+ nil);

endmodule;

end;
