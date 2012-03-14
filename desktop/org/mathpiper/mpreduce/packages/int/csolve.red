module csolve;   % routines to do with the C constants.

% Author: John P. Fitch.

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


fluid '(!*trint ccount cmap cmatrix cval loglist neweqn);

exports backsubst4cs,createcmap,findpivot,printvecsq, % printspreadc
   spreadc,subst4eliminateds;

imports nth,interr,!*multf,printsf,printsq,quotf,putv,negf,invsq,
   negsq,addsq,multsq,mksp,addf,domainp,pnth;

symbolic procedure findpivot cvec;
% Finds first non-zero element in CVEC and returns its cell number.
% If no such element exists, result is nil.
   begin         scalar i,x;
      i:=1;
      x:=getv(cvec,i);
      while i<ccount and null x do
      << i:=i+1;
         x:=getv(cvec,i) >>;
      if null x then return nil;
      return i
   end;

symbolic procedure subst4eliminatedcs(neweqn,substorder,ceqns);
% Substitutes into NEWEQN for all the C's that have been eliminated so
% far. These are given by CEQNS. SUBSTORDER gives the order of
% substitution as well as the constant multipliers. Result is the
% transformed NEWEQN.
   if null substorder then neweqn
   else begin    scalar nxt,row,cvar,temp;
      row:=car ceqns;
      nxt:=car substorder;
      if null (cvar:=getv(neweqn,nxt)) then
         return subst4eliminatedcs(neweqn,cdr substorder,cdr ceqns);
      nxt:=getv(row,nxt);
      for i:=0 : ccount do
      << temp:=!*multf(nxt,getv(neweqn,i));
         temp:=addf(temp,negf !*multf(cvar,getv(row,i)));
         putv(neweqn,i,temp) >>;
      return subst4eliminatedcs(neweqn,cdr substorder,cdr ceqns)
   end;


symbolic procedure backsubst4cs(cs2subst,cs2solve,cmatrix);
% Solves the C-eqns and sets vector CVAL to the C-constant values
% CMATRIX is a list of matrix rows for C-eqns after Gaussian
% elimination has been performed. CS2SOLVE is a list of the remaining
% C's to evaluate and CS2SUBST are the C's we have evaluated already.
   if null cmatrix then nil
   else begin    scalar eqnn,cvar,already,substlist,temp,temp2;
      eqnn:=car cmatrix;
      cvar:=car cs2solve;
      already:=nil ./ 1; % The S.Q. nil.
      substlist:=cs2subst;
% Now substitute for previously evaluated c's:
      while not null substlist do
      << temp:=car substlist;
         if not null getv(eqnn,temp) then
            already:=addsq(already,multsq(getv(eqnn,temp) ./ 1,
                                 getv(cval,temp)));
         substlist:=cdr substlist >>;
% Now solve for the c given by cvar (any remaining c's assumed zero).
      temp:=negsq addsq(getv(eqnn,0) ./ 1,already);
      if not null (temp2:=quotf(numr temp,getv(eqnn,cvar))) then
                                       temp:=temp2 ./ denr temp
      else temp:=multsq(temp,invsq(getv(eqnn,cvar) ./ 1));
      if not null numr temp then putv(cval,cvar,
                resimp rootextractsq subs2q temp);
      backsubst4cs(reversip(cvar . reversip cs2subst),
            cdr cs2solve,cdr cmatrix)
   end;

%**********************************************************************
% Routines to deal with linear equations for the constants C.
%**********************************************************************

symbolic procedure createcmap;
%Sets LOGLIST to list of things of form (LOG C-constant f), where f is
% function linear in one of the z-variables and C-constant is in S.F.
% When creating these C-constant names, the CMAP is also set up and
% returned as the result.
   begin         scalar i,l,c;
      l:=loglist;
      i:=1;
      while not null l do <<
         c:=(int!-gensym1('c) . i) . c;
         i:=i+1;
         rplacd(car l,((mksp(caar c,1) .* 1) .+ nil) . cdar l);
         l:=cdr l >>;
      if !*trint
        then printc ("Constants Created for log and tan terms:" . c);
      return c
   end;


symbolic procedure spreadc(eqnn,cvec1,w);
% Sets a vector 'cvec1' to coefficients of c<i> in eqnn.
    if domainp eqnn then putv(cvec1,0,addf(getv(cvec1,0),
                                !*multf(eqnn,w)))
    else begin    scalar mv,t1,t2;
        spreadc(red eqnn,cvec1,w);
        mv:=mvar eqnn;
        t1:=assoc(mv,cmap); %tests if it is a c var.
        if not null t1 then return <<
            t1:=cdr t1; %loc in vector for this c.
            if not (tdeg lt eqnn=1) then interr "Not linear in c eqn";
            t2:=addf(getv(cvec1,t1),!*multf(w,lc eqnn));
            putv(cvec1,t1,t2) >>;
        t1:=((lpow eqnn) .* 1) .+ nil; %this main var as sf.
        spreadc(lc eqnn,cvec1,!*multf(w,t1))
    end;

% symbolic procedure printspreadc cvec1;
%     begin
%         for i:=0 : ccount do <<
%            prin2 i;
%            printc ":";
%            printsf(getv(cvec1,i)) >>;
%         printc "End of printspreadc output"
%     end;

% symbolic procedure printvecsq cvec;
% % Print contents of cvec which contains s.q.'s (not s.f.'s).
% % Starts from cell 1 not 0 as above routine (printspreadc).
%    begin
%       for i:=1 : ccount do <<
%         prin2 i;
%         printc ":";
%         if null getv(cvec,i) then printc "0"
%         else printsq(getv(cvec,i)) >>;
%       printc "End of printvecsq output"
%    end;

endmodule;

end;
