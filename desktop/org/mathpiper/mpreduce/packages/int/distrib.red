module distrib;  % Routines for manipulating distributed forms.

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


fluid '(indexlist sqrtlist zlist);

exports dfprintform,multbyarbpowers,negdf,quotdfconst,sub1ind, % var2df,
   vp1,vp2,plusdf,multdf,multdfconst,orddf;

imports interr,addsq,negsq,exptsq,simp,domainp,mk!*sq,addf,
   multsq,invsq,minusp,mksp,sub1;

%***********************************************************************
% NOTE:     The expressions lt,red,lc,lpow have been used on distributed
%           forms as the latter's structure is sufficiently similar to
%           s.f.'s.  However lc df is a s.q. not a s.f. and lpow df is a
%           list of the exponents of the variables.  This also makes
%           lt df different.  Red df is d.f. as expected.
%***********************************************************************

symbolic procedure plusdf(u,v);
% U and V are D.F.'s. Value is D.F. for U+V.
    if null u then v
        else if null v then u
        else if lpow u=lpow v then
            (lambda(x,y); if null numr x then y else (lpow u .* x) .+ y)
            (!*addsq(lc u,lc v),plusdf(red u,red v))
        else if orddf(lpow u,lpow v) then lt u .+ plusdf(red u,v)
        else (lt v) .+ plusdf(u,red v);

symbolic procedure orddf(u,v);
% U and V are the LPOW of a D.F. - i.e. the list of exponents.
% Value is true if LPOW U '>' LPOW V and false otherwise.
    if null u then if null v then interr "Orddf = case"
        else interr "Orddf v longer than u"
        else if null v then interr "Orddf u longer than v"
        else if exptcompare(car u,car v) then t
        else if exptcompare(car v,car u) then nil
        else orddf(cdr u,cdr v);

symbolic procedure exptcompare(x,y);
    if atom x then if atom y then x>y else nil
        else if atom y then t
        else car x > car y;

symbolic procedure negdf u;
    if null u then nil
        else (lpow u .* negsq lc u) .+ negdf red u;

symbolic procedure multdf(u,v);
% U and V are D.F.'s. Value is D.F. for U*V.
% Reduces squares of square-roots as it goes.
    if null u or null v then nil
    else begin scalar y;
        % use (a+b)*(c+d) = (a*c) + a*(c+d) + b*(c+d).
        y:=multerm(lt u,lt v); %leading terms;
        y:=plusdf(y,multdf(red u,v));
        y:=plusdf(y,multdf((lt u) .+ nil,red v));
        return y
    end;

symbolic procedure multerm(u,v);
% Multiply two terms to give a D.F.
    begin scalar coef;
       coef:=!*multsq(cdr u,cdr v); % coefficient part.
       return multdfconst(coef,mulpower(car u,car v))
    end;

symbolic procedure mulpower(u,v);
% U and v are exponent lists. multiply corresponding forms.
    begin scalar r,s;
       r:=addexptsdf(u,v);
       if not null sqrtlist then s:=reduceroots(r,zlist);
       r:=(r .* (1 ./ 1)) .+ nil;
       if not (s=nil) then r:=multdf(r,s);
       return r
    end;

symbolic procedure reduceroots(r,zl);
    begin scalar s;
       while not null r do <<
          if eqcar(car zl,'sqrt) then
              s:=tryreduction(r,car zl,s);
          r:=cdr r; zl:=cdr zl >>;
       return s
    end;

symbolic procedure tryreduction(r,var,s);
   begin scalar x;
      x:=car r; % current exponent.
      if not atom x then << r:=x; x:=car r >>; % numeric part.
      if (x=0) or (x=1) then return s; % no reduction possible.
      x:=divide(x,2);
      rplaca(r,cdr x); % reduce exponent as redorded.
      x:=car x;
      var:=simp cadr var; % sqrt arg as a s q.
      var:=!*exptsq(var,x);
      x:=multdfconst(1 ./ denr var,f2df numr var); % distribute.
      if s=nil then s:=x
      else s:=multdf(s,x);
      return s
   end;



symbolic procedure addexptsdf(x,y);
% X and Y are LPOW's of D.F. Value is list of sum of exponents.
    if null x then if null y then nil else interr "X too long"
        else if null y then interr "Y too long"
        else exptplus(car x,car y).addexptsdf(cdr x,cdr y);

symbolic procedure exptplus(x,y);
    if atom x then if atom y then x+y else list (x+car y)
        else if atom y then list (car x +y)
        else interr "Bad exponent sum";

symbolic procedure multdfconst(x,u);
   % X is S.Q. not involving Z variables of DF U. Value is DF for X*U.
   if null u or null numr x then nil
      % else lpow u .* !*multsq(x,lc u) .+ multdfconst(x,red u);
      % FJW: Does not handle i^2 correctly, so ...
      % (cf. solve!-for!-u in module isolve)
    else lpow u .* subs2q multsq(x,lc u) .+ multdfconst(x,red u);

%symbolic procedure quotdfconst(x,u);
%    multdfconst(!*invsq x,u);

symbolic procedure f2df p;
% P is standard form. Value is P in D.F.
    if domainp p then dfconst(p ./ 1)
        else if mvar p member zlist then
             plusdf(multdf(vp2df(mvar p,tdeg lt p,zlist),f2df lc p),
                    f2df red p)
        else plusdf(multdfconst(((lpow p .* 1) .+ nil) ./ 1,f2df lc p),
                    f2df red p);

% SYMBOLIC PROCEDURE VAR2DF(VAR,N,ZLIST);
%    ((VP1(VAR,N,ZLIST) .* (1 ./ 1)) .+ NIL);

symbolic procedure vp1(var,degg,z);
% Takes VAR and finds it in Z (=list), raises it to power DEGG and puts
% the result in exponent list form for use in a distributed form.
    if null z then interr "Var not in z-list after all"
        else if var=car z then degg.vp2 cdr z
        else 0 . vp1(var,degg,cdr z);

symbolic procedure vp2 z;
% Makes exponent list of zeroes.
    if null z then nil
        else 0 . vp2 cdr z;

symbolic procedure vp2df(var,exprn,z);
% Makes VAR**EXPRN into exponent list and then converts the resulting
% power into a distributed form.  Special care needed with square-roots.
    if eqcar(var,'sqrt) and (exprn>1) then
        mulpower(vp1(var,exprn,z),vp2 z)
    else (vp1(var,exprn,z) .* (1 ./ 1)) .+ nil;

symbolic procedure dfconst q;
% Makes a distributed form from standard quotient constant Q.
    if numr q=nil then nil
        else ((vp2 zlist) .* q) .+ nil;

% Df2q moved to a section of its own.

symbolic procedure df2printform p;
% Convert to a standard form good enough for printing.
    if null p then nil
    else begin
        scalar mv,co;
        mv:=xl2q(lpow p,zlist,indexlist);
        if mv=(1 ./ 1) then <<
            co:=lc p;
            if denr co=1 then return addf(numr co,
                df2printform red p);
            co:=mksp(mk!*sq co,1);
            return (co .* 1) .+ df2printform red p >>;
        co:=lc p;
        if not (denr co=1) then mv:=!*multsq(mv,1 ./ denr co);
        mv:=mksp(mk!*sq mv,1) .* numr co;
        return mv .+ df2printform red p
    end;


symbolic procedure xl2q(l,z,il);
% L is an exponent list from a D.F.,Z is the Z-list, IL is the list of
% indices.  Value is L converted to standard quotient.
    if null z then 1 ./ 1
        else if car l=0 then xl2q(cdr l,cdr z,cdr il)
        else if not atom car l then
            begin    scalar temp;
                if caar l=0 then temp:= car il
                else temp:=list('plus,car il,caar l);
                temp:=mksp(list('expt,car z,temp),1);
                return !*multsq(((temp .* 1) .+ nil) ./ 1,
                               xl2q(cdr l,cdr z,cdr il))
            end
        else if minusp car l then
             !*multsq(!*invsq(((mksp(car z,-car l) .* 1) .+ nil) ./ 1),
                         xl2q(cdr l,cdr z,cdr il))
        else !*multsq(((mksp(car z,car l) .* 1) .+ nil) ./ 1,
                    xl2q(cdr l,cdr z,cdr il));


%symbolic procedure sub1ind power;
%     if atom power then power-1 else list sub1 car power;

symbolic procedure multbyarbpowers u;
% Multiplies the ordinary D.F., U, by arbitrary powers
% of the z-variables,
%       i-1  j-1  k-1
% i.e. x    z    z    ... so result is D.F. with the exponent list
%            1    2
%appropriately altered to contain list elements instead of numeric ones.
   if null u then nil
   else ((addarbexptsdf lpow u) .* lc u) .+ multbyarbpowers red u;

symbolic procedure addarbexptsdf x;
% Adds the arbitrary powers to powers in exponent list, X, to produce
% new exponent list.  e.g. 3 -> (2) to represent x**3 now becoming :
%          3    i-1    i+2
%         x  * x    = x      .
   if null x then nil
    else list exptplus(car x,-1) . addarbexptsdf cdr x;

endmodule;

end;
