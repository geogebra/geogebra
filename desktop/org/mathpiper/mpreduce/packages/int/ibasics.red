module ibasics;   % Some basic support routines for integrator.

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


fluid '(!*backtrace !*gcd !*sqfree !*trint indexlist sqrtflag sqrtlist
        varlist zlist);

exports partialdiff,printdf,interr;

imports df2printform,printsf,varsinsf,addsq,multsq,multd,mksp;

symbolic procedure printdf u;
% Print distributed form via cheap conversion to reduce structure.
    begin scalar !*gcd;
       printsf df2printform u;
    end;

% symbolic procedure indx(n);
%    if n<2 then (list 1) else(n . indx(isub1 n));

symbolic procedure interr mess;
   <<if !*trint or !*backtrace
       then <<prin2 "***** INTEGRATION PACKAGE ERROR:  "; printc mess>>;
     error1()>>;

symbolic procedure partialdiff(p,v);
% Partial differentiation of p wrt v - p is s.f. as is result.
    if domainp p then nil
    else
        if v=mvar p then
            (lambda x; if x=1 then lc p
             else ((mksp(v,x-1) .* multd(x,lc p))
                         .+ partialdiff(red p,v)))
            (tdeg lt p)
        else
            (lambda x; if null x then partialdiff(red p,v)
             else ((lpow p .* x) .+ partialdiff(red p,v)))
            (partialdiff(lc p,v));

put('pdiff,'simpfn,'simppdiff);

symbolic procedure mkilist(old,term);
   if null old then nil
    else term.mkilist(cdr old,term);

% symbolic procedure addin(lista,first,listb);
% if null lista
%  then nil
%  else ((first.car listb).car lista).addin(cdr lista,first,cdr listb);

symbolic procedure removeduplicates(u);
  % Purges duplicates from the list passed to it.
if null u then nil
  else if (atom u) then u.nil
    else if member(car u,cdr u)
      then removeduplicates cdr u
      else (car u).removeduplicates cdr u;

symbolic procedure jsqfree(sf,var);
begin
  varlist:=getvariables(sf ./ 1);
  zlist:=findzvars(varlist,list var,var,nil);
  sqrtlist:=findsqrts varlist; % before the purge
  sqrtflag:=not null sqrtlist;
  varlist := setdiff(varlist,zlist);
  return if sf eq !*sqfree then list list sf else sqfree(sf,zlist)
  end;

symbolic procedure stt(u,x);
  if domainp u
    then if u eq nil
      then ((-1) . nil)
      else (0 . u)
    else if mvar u eq x
      then ldeg  u . lc u
      else if ordop(x,mvar u)
        then (0 . u)
        else begin
          scalar ltlc,ltrest;
          ltlc:=stt(lc u,x);
          ltrest:= stt(red u,x);
          if car ltlc = car ltrest then go to merge;
          if car ltlc > car ltrest
            then return car ltlc .
                             !*multf(cdr ltlc,(lpow u .* 1) .+ nil)
            else return ltrest;
        merge:
          return car ltlc.addf(cdr ltrest,
                               !*multf(cdr ltlc,(lpow u .* 1) .+ nil))
          end;

symbolic procedure mapply(funct,l);
   if null l then rerror(int,6,"Empty list to mapply")
    else if null cdr l then car l
    else apply2(funct,car l,mapply(funct,cdr l));

% symbolic procedure intersect(x,y);
%    if null x then nil else if member(car x,y) then
%     car(x) . intersect(cdr x,y) else
%          intersect(cdr x,y);

symbolic procedure mapvec(v,f);
   begin
      scalar n;
      n:=upbv v;
      for i:=0:n do apply1(f,getv(v,i))
   end;

endmodule;

end;
