	module switch;  % Support for switches and ON and OFF statements.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation.  All rights reserved.

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


global '(!*switchcheck switchlist!*);

% No references to RPLAC-based functions in this module.

symbolic procedure on u; for each j in u do on1 j;

symbolic procedure off u; for each j in u do off1 j;

symbolic procedure off1 u; onoff(u,nil);

symbolic procedure on1 u; onoff(u,t);

symbolic procedure onoff(u,bool);
   begin scalar x,y;
      if not idp u then typerr(u,"switch")
       else if not flagp(u,'switch)
%       then if !*switchcheck
               then rerror(rlisp,25,list(u,"not defined as switch"));
%             else lpriw("*****",list(u,"not defined as switch"));
      x := intern compress append(explode '!*,explode u);
      if !*switchcheck and lispeval x eq bool then return nil
       else if y := atsoc(bool,get(u,'simpfg))
        then lispeval('progn . append(cdr y,list nil));
      if bool and x eq '!*!r!a!i!s!e then x := '!*raise; % Special case.
       set(x,bool)
   end;

symbolic procedure onoff!-reset u;
   % set switch to its default value
   % set !*switchcheck to t so that simpfg code is executed only 
   %  if the switch is actually toggled.
   <<!*switchcheck := t;
     onoff(u,if get(u,'switchdefault) then t else nil);
     !*switchcheck := swcksave>> where swcksave:=!*switchcheck;

symbolic procedure switch u;
   % Declare list u as switches.
   for each x in u do
      begin scalar y,dflt;
         if eqcar(x,'equal) and not atom cdr x and idp cadr x
             and not null cddr x and null cdddr x
           then if caddr x memq '(on off t nil)
                  then <<dflt := list if caddr x eq 'on then t
                                       else if caddr x eq 'off then nil
                                       else caddr x;
                         x := cadr x>>
                 else typerr(caddr x,"switch default value");
         if not idp x then typerr(x,"switch");
         if not(x memq switchlist!*)
           then switchlist!* := x . switchlist!*;
         flag(list x,'switch);
         y := intern compress append(explode '!*,explode x);
         if not fluidp y and not globalp y then fluid list y;
         if not null dflt then <<put(x,'switchdefault,car dflt); set(y,car dflt)>>
      end;

deflist('((switch rlis)),'stat);   % we use deflist since it's flagged
                                   % eval
flag('(switch),'eval);

put('off,'stat,'rlis);

put('on,'stat,'rlis);

flag ('(off on),'ignore);

% Symbolic mode switches:

switch backtrace,comp,defn,demo,echo,errcont,fastfor,   % eoldelimp
       int,lessspace,msg,output,pret,quotenewnam,raise,time,
       strict_argcount, report_colons;

!*report_colons := t;

put('eoldelimp,'simpfg,'((t (flag (list !$eol!$) 'delchar))
                         (nil (remflag (list !$eol!$) 'delchar))));

% Support for REDUCE 4.

switch reduce4;

put('reduce4,'simpfg,'((t (load!-package 'reduce4) (!%reduce4))));

endmodule;

end;
