module write;  % Miscellaneous statement definitions.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


% ***** DEFINE STATEMENT *****

remprop('define,'stat);

symbolic procedure define u;
   for each x in u do
      if not eqcar(x,'equal) or not idp cadr x
        then typerr(x,"DEFINE declaration")
       else put(cadr x,'newnam,caddr x);

deflist('((define rlis)),'stat);

flag('(define),'eval);

% ***** WRITE STATEMENT *****

symbolic procedure formwrite(u,vars,mode);
   begin scalar bool1,bool2,x,y,z;
      u := cdr u;
      bool1 := mode eq 'symbolic;
      while u do
        <<x := formc(car u,vars,mode);
          y := getsetvars x;
          z := (if bool1 then list('prin2,x)
                 else list('assgnpri,x,if y then 'list . y else nil,
          if not cdr u then if not bool2 then ''only else ''last
           else if not bool2 then ''first else nil)) .
                             z;
          bool2 := t;
          u := cdr u>>;
        if bool1 then z := nil . z;  % Since PRIN2 returns its value.
        return if null z then nil
                else if null cdr z then car z
                else 'progn . reversip!* z
   end;

put('write,'stat,'rlis);

put('write,'formfn,'formwrite);


% ECHOPR is similar to WRITE but, if switch TESTECHO is on, it echos an
% offline print onto the screen, in either algebraic or symbolic mode.

% Switch is not yet defined.

flag('(testecho),'switch);

put('echopr,'stat,'rlis);

put('echopr,'formfn,'formechopr);

symbolic procedure formechopr(u,vars,mode);
   (lambda x; list ('progn,x,
      list ('cond,list ('(and !*testecho ofl!*),
         list (list ('lambda,'(n),
            list ('progn,x,'(wrs n),nil)),'(wrs nil) ))) ))
       formwrite(u,vars,mode);

endmodule;

end;
