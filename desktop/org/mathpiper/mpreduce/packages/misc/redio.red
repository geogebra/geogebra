module redio; % General Purpose I/O package, sorting and positioning.

% Author: Martin L. Griss.

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


% Modified by: Anthony C. Hearn.

fluid '(orig!*);

global '(!*formfeed lnnum!* maxln!* pgnum!* title!*);

% This module is functionally equivalent to the PSL file PSL-CREFIO.RED.

% FORMFEED (ON)  controls ^L or spacer of ====;

symbolic procedure initio();
% Set-up common defaults;
   begin
        !*formfeed:=t;
        orig!*:=0;
        lnnum!*:=0;
        linelength(75);
        maxln!*:=55;
        title!*:=nil;
        pgnum!*:=1;
   end;

% symbolic procedure lposn(); lnnum!*; % Actually part of Standard LISP.

initio();

symbolic procedure setpgln(p,l);
  begin if p then maxln!*:=p;
        if l then linelength(l);
  end;

% We use EXPLODE to produce a list of chars from atomname,
% and TERPRI() to terminate a buffer..all else
% done in package..spaces,tabs,etc. ;

Comment Character lists are (length . chars), for FITS;


symbolic  procedure getes u;
   % Returns for U , eee=(Length . List of char);
   begin scalar eee;
      if not idp u then return <<eee:=explode u; length(eee) . eee>>;
      if not(eee:=get(u,'rccnam))
        then <<eee:=explode(u);
               eee:=length(eee) . eee;
               put(u,'rccnam,eee)>>;
        return eee
   end;

% symbolic smacro procedure prtwrd u;
%   if numberp u then prtnum u else prtatm u;

symbolic procedure prtatm u;
        prin2 u;        % For a nice print;

symbolic procedure prtlst u;
 if atom u then prin2 u else for each x in u do prin2 x;

symbolic procedure prtnum n;
   % We use this kludge to defeat the new line that several LISPs
   % including PSL like to insert when printing a number near the line
   % boundary.
   for each x in explode2 n do prin2 x;

symbolic procedure princn eee;
% output a list of chars, update POSN();
         while (eee:=cdr eee) do prin2 car eee;

symbolic procedure spaces n; for i:=1:n do prin2 '!  ;

symbolic procedure spaces!-to n;
   begin scalar x;
        x := n - posn();
        if x<1 then newline n
         else spaces x;
   end;

symbolic procedure setpage(title,page);
% Initialise current page and title;
   begin
        title!*:= title ;
        pgnum!*:=page;
   end;

symbolic procedure newline n;
% Begins a fresh line at posn N;
   begin
        lnnum!*:=lnnum!*+1;
        if lnnum!*>=maxln!* then newpage()
         else terpri();
        spaces(orig!*+n);
   end;

symbolic procedure newpage();
% Start a fresh page, with PGNUM and TITLE, if needed;
   begin scalar a;
%       a:=lposn();
        a := lnnum!*;
        lnnum!*:=0;
        if posn() neq 0 then newline 0;
        if a neq 0 then formfeed();
        if title!* then
          <<spaces!-to 5; prtlst title!*>>;
        spaces!-to (linelength(nil)-4);
        if pgnum!* then <<prtnum pgnum!*; pgnum!*:=pgnum!*+1>>
         else pgnum!*:=2;
        newline 10;
        newline 0;
   end;

symbolic procedure underline2 n;
        if n>=linelength(nil) then
          <<n:=linelength(nil)-posn();
            for i:=0:n do prin2 '!- ;
            newline(0)>>
         else begin scalar j;
                j:=n-posn();
                for i:=0:j do prin2 '!-;
              end;

symbolic procedure lprint(u,n);
% prints a list of atoms within block LINELENGTH(NIL)-n;
   begin scalar eee; integer l,m;
        spaces!-to n;
        l := linelength nil-posn();
        if l<=0 then error(13,"WINDOW TOO SMALL FOR LPRINT");
        while u do
           <<eee:=getes car u; u:=cdr u;
            if linelength nil<posn() then newline n;
             if car eee<(m := linelength nil-posn()) then princn eee
              else if car eee<l then <<newline n; princn eee>>
              else begin
                 eee := cdr eee;
              a: for i := 1:m do <<prin2 car eee; eee := cdr eee>>;
                 newline n;
                 if null eee then nil
                  else if length eee<(m := l) then princn(nil . eee)
                  else go to a
                end;
             if posn()<linelength nil then prin2 '! >>
   end;

symbolic procedure rempropss(atmlst,lst);
   for each x in atmlst do
      for each y in lst do remprop(x,y);


symbolic procedure remflagss(atmlst,lst);
   for each x in lst do remflag(atmlst,x);

symbolic procedure formfeed;
        if !*formfeed then eject()
         else <<terpri();
                prin2 " ========================================= ";
                terpri()>>;

endmodule;

end;
