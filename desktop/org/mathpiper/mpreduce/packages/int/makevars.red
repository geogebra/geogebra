module makevars; % Make dummy variables for integration process.

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


fluid '(!*gensymlist!* !*purerisch);

% exports getvariables,varsinlist,varsinsf,findzvars,  % varsinsq
%         createindices,mergein;

% imports dependsp,union;

% Note that 'i' is already maybe committed for sqrt(-1),
% also 'l' and 'o' are not used as they print badly on certain
% terminals etc and may lead to confusion.

!*gensymlist!* := '(! j ! k ! m ! n ! p ! q ! r ! s ! t ! u ! v ! w ! x
                    ! y ! z);

%mapc(!*gensymlist!*,function remob); %REMOB protection;


symbolic procedure varsinlist(l,vl);
% L is a list of s.q. - find all variables mentioned,
% given thal vl is a list already known about.
    begin       while not null l do <<
            vl:=varsinsf(numr car l,varsinsf(denr car l,vl));
            l:=cdr l >>;
        return vl
    end;

symbolic procedure getvariables sq;
    varsinsf(numr sq,varsinsf(denr sq,nil));

symbolic procedure varsinsf(form,l);
   if domainp form then l
   else begin
     while not domainp form do <<
        l:=varsinsf(lc form,union(l,list mvar form));
        form:=red form >>;
     return l
   end;

symbolic procedure findzvars(vl,zl,var,flg);
    begin scalar v;
   % VL is the crude list of variables found in the original integrand.
   % ZL must have merged into it all EXP, LOG etc terms from this.
   % If FLG is true then ignore DF as a function.
scan: if null vl then return zl;
         v:=car vl; % next variable.
         vl:=cdr vl;
   % At present items get put onto ZL if they are non-atomic
   % and they depend on the main variable. The arguments of
   % functions are decomposed by recursive calls to findzvar.
   % Give up if V has been declared dependent on other things.
        if atom v and v neq var and depends(v,var) then
%          rerror(int,7,
%                 "Can't integrate in the presence of side-relations")
          zl := union(list v, zl)
         else if not atom v and not(v member zl) and dependsp(v,var)
         then if car v='!*sq then zl:=findzvarssq(cadr v,zl,var)
         else if car v memq '(times quotient plus minus difference)
                 or (((car v) eq 'expt) and fixp caddr v)
             then
                 zl:=findzvars(cdr v,zl,var,flg)
         else if flg and car v eq 'df
          then <<!*purerisch := t;  % printc "Pure set";
                 return zl>> % try and stop it
             else zl:=v . findzvars(cdr v,zl,var,flg);
                 % scan arguments of fn.
             %ACH: old code used to look only at CADR if a DF involved.
        go to scan
   end;

symbolic procedure findzvarssq(sq,zl,var);
    findzvarsf(numr sq,findzvarsf(denr sq,zl,var),var);

symbolic procedure findzvarsf(sf,zl,var);
    if domainp sf then zl
    else findzvarsf(lc sf,
                    findzvarsf(red sf,
                               findzvars(list mvar sf,zl,var,nil),
                               var),
                  var);

symbolic procedure createindices zl;
% Produces a list of unique indices, each associated with a ;
% different Z-variable;
     reversip crindex1(zl,!*gensymlist!*);

symbolic procedure crindex1(zl,gl);
 begin if null zl then return nil;
    if null gl then << gl:=list int!-gensym1 'i; %new symbol needed;
        nconc(!*gensymlist!*,gl) >>;
    return (car gl) . crindex1(cdr zl,cdr gl) end;

symbolic procedure cdrmember(a,b);
    if null b then nil
    else if a=cdar b then car b
    else cdrmember(a,cdr b);

symbolic procedure mergein(dl,ll);
    % Adjoin logs of things in dl to existing list ll.
    if null dl then ll
    else if cdrmember(car dl,ll) then mergein(cdr dl,ll)
    else mergein(cdr dl,('log . car dl) . ll);

endmodule;

end;
