module homog; % Procedures for factorization of homogeneous polynomials.

% Authors: Shuichi Moritsugu <y31046@tansei.cc.u-tokyo.ac.jp>
%          and Eiichi Goto.

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



symbolic procedure homogp s;
   % Confirmation of homogeneous polynomials.
      if domainp s or domainp car s then nil
        else if null cdadr s then nil
        else if domainp cdr lastnondomain cadr s then nil
        else if listsum caaadr s=listsum caar lastnondomain cadr s
         then t
        else nil;

symbolic procedure subs0 nm;
   %Substitution of 0 into exponent list.
      if null nm then nil
        else ((0 . cdaar nm) . cdar nm) . subs0 cdr nm;

symbolic procedure varss(v,d);
   % Ss of single variable.
      ((v . nil) . 1) . ((((d . nil) . 1) . nil) . 1);

symbolic procedure rconstnm(nm,nv,td);
   % Reconstruction of numerator.
      if null nm then nil
        else if domainp nm then ((td . mkzl(nv+1)) . nm) . nil
        else (((td-listsum caar nm) . caar nm) . cdar nm)
             . rconstnm(cdr nm,nv,td);

symbolic procedure rconst1(s,v,td);
   % Reconstruction of one factor.
      if homogp s then s
        else ((v . caar s) . (cdar s+1))
             . (reverse rconstnm(cadr s,cdar s,td) . cddr s);

symbolic procedure rconst(p,fctrlis);
   % Reconstruction of factors.
   begin scalar v,d,td,fs,fcf,ffl,x;
      v := car p; d := cdr p; fcf := car fctrlis;
      for i:=2:length fctrlis do
          <<x := nth(fctrlis,i);
            fs := sf2ss car x; td := listsum caaadr fs;
            fs := rconst1(fs,v,td);
            d := d-cdr x*td;
            ffl := aconc(ffl,ss2sf fs . cdr x)>>;
      ffl := fcf . ffl;
      if d>0 then ffl := aconc(ffl,ss2sf varss(v,1) . d);
      return ffl;
   end;

endmodule;

end;
