module ztrans; % Calculation of Z transformation and inverse.

% Authors: Wolfram Koepf, Lisa Temme.
% Version 1.0, April 1995.

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


% ZTRANS: Z transformation, see
% Bronstein, Semendjajew: Taschenbuch der Mathematik, 4.4.4

create!-package('(ztrans ztrrules),'(contrib misc));

flag('(ztrrules),'lap);

fluid '(!*precise);

!*precise := nil;   % Needed for this module at the moment.

% auxiliary functions


symbolic procedure newrederr(u);
   <<terpri!* t;
     prin2!* "***** ";
     if eqcar(u,'list) then foreach xx in cdr u do newrederr1(xx)
       else  newrederr1 u;
     terpri!* nil; erfg!* := t; error1()>>;

symbolic procedure newrederr1(u);
     if not atom u and atom car u and cdr u and atom cadr u
        and null cddr u
       then <<prin2!* car u; prin2!* " "; prin2!* cadr u>>
      else maprin u;

flag('(newrederr),'opfn);


%********************************************************************

%Ztrans procedure

algebraic operator ztrans_aux;
algebraic operator !~f,!~g,!~summ,binomial;

algebraic procedure ztrans(f,n,z);
 begin
 scalar tmp,!*factor,!*exp;
   off factor;
   tmp := ztrans_aux(f,n,z);
   if part(tmp,0)=ztrans_aux then
     << on factor;
        tmp := ztrans_aux(f,n,z);
        off factor;
     >>;
   if part(tmp,0)=ztrans_aux then
       return lisp mk!*sq((list((car fkern list('ztrans,f,n,z) . 1)
                                                   . 1)) . 1)
     else return tmp;
 end$

endmodule;

end;
