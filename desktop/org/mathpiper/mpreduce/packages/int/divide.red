module divide;  % Exact division of standard forms to give a S Q.

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


fluid '(!*trdiv !*trint resid sqrtlist zlist);

exports fquotf,testdivdf,dfquotdf;

imports df2q,f2df,gcdf,interr,multdf,negdf,plusdf,printdf,printsf,
   quotf,multsq,invsq,negsq;

% Intended for dividing out known factors as produced by the
% integration program. Horrible and slow, I expect!!

symbolic procedure dfquotdf(a,b);
    begin       scalar resid;
        if (!*trint or !*trdiv) then <<
            printc "Dividing out a simple factor of "; printdf b >>;
        a:=dfquotdf1(a,b);
        if (!*trint or !*trdiv) then <<
            printc "Remaining term to be factorised is ";
            printdf a >>;
        if not null resid then begin
            scalar gres,w;
            % Make one more check for a zero residue.
            if null numr df2q resid then return nil;
            if !*trint or !*trdiv then <<
            printc "Failure in factorisation:";
            printdf resid;
            printc "Which should be zero";
            w:=resid;
            gres:=numr lc w; w:=red w;
            while not null w do <<
                gres:=gcdf(gres,numr lc w);
                w:=red w >>;
            printc "I.e. the following vanishes";
            printsf gres>>;
            interr "Non-exact division due to a log term"
            end;
        return a
   end;

symbolic procedure fquotf(a,b);
% Input: a and b standard quotients with (a/b) an exact
% division with respect to the variables in zlist,
% but not necessarily obviously so. the 'non-obvious' problems
% will be because of (e.g.) square-root symbols in b
% output: standard quotient for (a/b)
% (prints message if remainder is not 'clearly' zero.
% A must not be zero.
    begin     scalar t1;
        if null a then interr "a=0 in fquotf";
        t1:=quotf(a,b); %try it the easy way
        if not null t1 then return t1 ./ 1; %ok
        return df2q dfquotdf(f2df a,f2df b)
    end;

symbolic procedure dfquotdf1(a,b);
    begin   scalar q;
        if null b then interr "Attempt to divide by zero";
        q:=sqrtlist; %remove sqrts from denominator, maybe.
        while not null q do begin
            scalar conj;
            conj:=conjsqrt(b,car q); %conjugate wrt given sqrt
            if not (b=conj) then <<
                a:=multdf(a,conj);
                b:=multdf(b,conj) >>;
            q:=cdr q end;
        q:=dfquotdf2(a,b);
        resid:=reversip resid;
        return q
    end;

symbolic procedure dfquotdf2(a,b);
% As above but a and b are distributed forms, as is the result.
    if null a then nil
    else begin scalar xd,lcd;
        xd:=xpdiff(lpow a,lpow b);
        if xd='failed then <<
            xd:=lt a; a:=red a;
            resid:=xd .+ resid;
            return dfquotdf2(a,b) >>;
        lcd:= !*multsq(lc a,!*invsq lc b);
        if null numr lcd then return dfquotdf2(red a,b);
           % Should not be necessary;
        lcd := xd .* lcd;
        xd:=plusdf(a,multdf(negdf (lcd .+ nil),b));
        if xd and (lpow xd = lpow a % Again, should not be necessary;
                   or xpdiff(lpow xd,lpow b) = 'failed)
          then <<if !*trint or !*trdiv
                   then <<printc "Problems in dividing:"; printdf xd>>;
                 xd := rootextractdf xd;
                 if !*trint or !*trdiv then printdf xd>>;
        return lcd .+ dfquotdf2(xd,b)
    end;

symbolic procedure rootextractdf u;
   if null u then nil
    else begin scalar v;
      v := resimp rootextractsq lc u;
      return if null numr v then rootextractdf red u
              else (lpow u .* v) .+ rootextractdf red u
    end;

symbolic procedure rootextractsq u;
   if null numr u then u
%   else rootextractf numr u ./ rootextractf denr u;
    else (rootextractf numr x ./ rootextractf denr x)
       where x=subs2q u;

symbolic procedure rootextractf v;
   if domainp v then v
    else begin scalar u,r,c,x,p;
      u := mvar v;  p := ldeg v;
      r := rootextractf red v;
      c := rootextractf lc v;
      if null c then return r
       else if atom u then return (lpow v .* c) .+ r
       else if car u eq 'sqrt
        or car u eq 'expt and eqcar(caddr u,'quotient)
           and car cdaddr u = 1 and numberp cadr cdaddr u
        then <<p := divide(p,if car u eq 'sqrt then 2
                              else cadr cdaddr u);
      if car p = 0
        then return if null c then r else (lpow v .* c) .+ r
       else if numberp cadr u
        then <<c := multd(cadr u ** car p,c); p := cdr p>>
       else <<x := simpexpt list(cadr u,car p);
              if denr x = 1 then <<c := multf(numr x,c); p := cdr p>>
               else p := ldeg v>>>>;
      % D. Dahm suggested an additional call of rootextractf on the
      % result here.  This does cause some expressions to simplify
      % sooner, but also leads to infinite loops with expressions
      % like (a*x+b)**p.
      return if p=0 then addf(c,r)
              else if null c then r
              else ((u to p) .* c) .+ r
   end;

% The following hack makes sure that the results of differentiation
% gets passed through ROOTEXTRACT
% a) This should not be done this way, since the effect is global
% b) Should this be done via TIDYSQRT?

put('df,'simpfn,'simpdf!*);

symbolic procedure simpdf!* u;
  begin scalar v,v1;
        v:=simpdf u;
        v1:=rootextractsq v;
        if not(v1=v) then return resimp v1
        else return v
end;

symbolic procedure xpdiff(a,b);
%Result is list a-b, or 'failed' if a member of this would be negative.
    if null a then if null b then nil
        else interr "B too long in xpdiff"
    else if null b then interr "A too long in xpdiff"
    else if car b>car a then 'failed
    else (lambda r;
        if r='failed then 'failed
        else (car a-car b) . r) (xpdiff(cdr a,cdr b));


symbolic procedure conjsqrt(b,var);
% Subst(var=-var,b).
    if null b then nil
    else conjterm(lpow b,lc b,var) .+ conjsqrt(red b,var);

symbolic procedure conjterm(xl,coef,var);
% Ditto but working on a term.
    if involvesp(xl,var,zlist) then xl .* negsq coef
    else xl .* coef;

symbolic procedure involvesp(xl,var,zl);
% Check if exponent list has non-zero power for variable.
    if null xl then interr "Var not found in involvesp"
    else if car zl=var then car xl neq 0
    else involvesp(cdr xl,var,cdr zl);

endmodule;

end;
