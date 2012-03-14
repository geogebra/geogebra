module fortpri; % FORTRAN output package for expressions.

% Author: Anthony C. Hearn.

% Modified by: James Davenport after Francoise Richard, April 1988.
%              Herbert Melenk (introducing C output style), October 1994

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid  '(!*fort
         !*fortupper
         !*period
         scountr
         explis
         fbrkt
         fvar
         nchars
         svar
         posn!*
         fortlang!*);

fluid '(previous!-fancy!-state !*fancy);

put('fort, 'simpfg, '((t (save!-fancy)) (nil (restore!-fancy))));

symbolic procedure save!-fancy();
  begin
    previous!-fancy!-state := !*fancy;
    if !*fancy then off fancy;
  end;

symbolic procedure restore!-fancy();
  if previous!-fancy!-state then on fancy;

switch fortupper;

global '(card_no
         charassoc!*
         fort_width
         fort_lang
         spare!*
         varnam!*);

%   The global fort_exponent is defined in the module arith/smlbflot.

% Global variables initialized in this section.

% SPARE!* should be set in the system dependent code module.

card_no:=20;
charassoc!* :=
         '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e) (!F . !f)
           (!G . !g) (!H . !h) (!I . !i) (!J . !j) (!K . !k) (!L . !l)
           (!M . !m) (!N . !n) (!O . !o) (!P . !p) (!Q . !q) (!R . !r)
           (!S . !s) (!T . !t) (!U . !u) (!V . !v) (!W . !w) (!X . !x)
           (!Y . !y) (!Z . !z));
fort_width := 70;
posn!* := 0;
varnam!* := 'ans;
fort_lang := 'fort;

flag ('(card_no fort_width fort_lang),'share);

put('fort_array,'stat,'rlis);

flag('(fort_array),'flagop);

symbolic procedure varname u;
   % Sets the default variable assignment name.
   if not idp car u then typerr(car u,"identifier")
    else varnam!* := car u;

rlistat '(varname);

symbolic procedure flength(u,chars);
   if chars<0 then chars
    else if atom u
     then chars-if numberp u then if fixp u then flatsizec u+1
                                   else flatsizec u
                 else flatsizec((lambda x; if x then x else u)
                                   get(u,'prtch))
    else flength(car u,flenlis(cdr u,chars)-2);

symbolic procedure flenlis(u,chars);
   if null u then chars
    else if chars<0 then chars
    else if atom u then flength(u,chars)
    else flenlis(cdr u,flength(car u,chars));

symbolic procedure fmprint(l,p);
   begin scalar x,w;
        if null l then return nil
         else if atom l then <<
           if l eq 'e then return
%            if fortlang!*='c then "exp(1.0)" else "EXP(1.0)";
             fprin2!* "EXP(1.0)";
           if fixp l and !*period then return fmprint(i2rd!* l,p);
           if not numberp l or
              not(l<0) then return fprin2!* l;
           fprin2!* "(";
           fbrkt := nil . fbrkt;
           fprin2!* l;
           fprin2!* ")";
           return fbrkt := cdr fbrkt >>
         else if stringp l then return fprin2!* l
         else if not atom car l then fmprint(car l,p)
         else if x := get(car l,'fort)
          then return apply2(x,l,p)
         else if ((x := get(car l,'pprifn))
             and not((x := apply2(x,l,p)) eq 'failed)) or
                 ((x := get(car l,'prifn))
             and not((x := apply1(x,l)) eq 'failed))
          then return x
         else if x := get(car l,'infix) then <<
            p := not(x>p);
            if p then <<fprin2!* "("; fbrkt := nil . fbrkt>>;
            fnprint(car l,x,cdr l);
            if p then <<fprin2!* ")"; fbrkt := cdr fbrkt>>;
            return >>
         else fprin2!* car l;
        w:= fortlang!* = 'c and flagp(car l,'fort_array);
        fprin2!* if w then "[" else "(";
        fbrkt := nil . fbrkt;
        x := !*period;
        % Assume no period printing for non-operators (e.g., matrices).
        if gettype car l neq 'operator or flagp(car l,'fort_array)
                   then !*period := nil;
        if cdr l then fnprint(if w then "][" else '!*comma!*,0,cdr l);
        !*period := x;
        fprin2!* if w then "]" else ")";
        return fbrkt := cdr fbrkt
   end;

symbolic procedure fnprint(op,p,l);
   begin
        if op eq 'expt then return fexppri(p,l)
         else if not get(op,'alt) then <<
           fmprint(car l,p);
           l := cdr l >>;
        for each v in l do <<
          if atom v or not (op eq get(car v,'alt)) then foprin op;
          fmprint(v,p) >>
   end;

symbolic procedure fexppri(p,l);
   % Next line added by James Davenport after Francoise Richard.
   if car l eq 'e then fmprint('exp . cdr l,p)
   % C entry by Herbert Melenk.
    else if fortlang!*='c then
       if fixp cadr l and cadr l >0 and cadr l<4 then
           fmprint('times . for i:=1:cadr l collect car l,p)
       else fmprint('pow.l,p)
    else begin scalar pperiod;
      fmprint(car l,p);
      foprin 'expt;
      pperiod := !*period;
      if numberp cadr l then !*period := nil else !*period := t;
      fmprint(cadr l,p);
      !*period := pperiod
   end;

put('pow,'simpfn,'simpiden);

symbolic procedure foprin op;
   (if null x then fprin2!* op else fprin2!* x) where x=get(op,'prtch);

symbolic procedure fvarpri(u,v,w);
   %prints an assignment in FORTRAN notation;
   begin integer scountr,llength,nchars; scalar explis,fvar,svar;
        fortlang!* := reval fort_lang;
        if not(fortlang!* memq '(fort c)) then
           typerr(fortlang!*,"target language");
        if not posintegerp card_no
          then typerr(card_no,"FORTRAN card number");
        if not posintegerp fort_width
          then typerr(fort_width,"FORTRAN line width");
        llength := linelength fort_width;
        if stringp u
          then return <<fprin2!* u;
                        if w eq 'only then fterpri(t);
                        linelength llength>>;
        if eqcar(u,'!*sq) then u := prepsq!* sqhorner!* cadr u;
        scountr := 0;
        nchars := if fortlang!* = 'c then 999999
            else ((linelength nil-spare!*)-12)*card_no;
           %12 is to allow for indentation and end of line effects;
        svar := varnam!*;
        fvar := if null v then (if fortlang!*='fort then svar else nil)
                 else car v;
        if posn!*=0 and w then fortpri(fvar,u,w)
         else fortpri(nil,u,w);
                % should mean expression preceded by a string.
        linelength llength
   end;

symbolic procedure fortpri(fvar,xexp,w);
   begin scalar fbrkt;
      if eqcar(xexp,'list)
        then <<posn!* := 0;
               fprin2!* "C ***** INVALID FORTRAN CONSTRUCT (";
               fprin2!* car xexp;
               return fprin2!* ") NOT PRINTED">>;
        if flength(xexp,nchars)<0
          then xexp := car xexp . fout(cdr xexp,car xexp,w);
        if fvar
          then <<posn!* := 0;
                 fprin2!* "      ";
                 fmprint(fvar,0);
                 fprin2!* "=">>;
        fmprint(xexp,0);
        if fortlang!*='fort and w or w='last then fterpri(w)
   end;

symbolic procedure fout(args,op,w);
   begin integer ncharsl; scalar distop,x,z;
        ncharsl := nchars;
        if op memq '(plus times) then distop := op;
        while args do
         <<x := car args;
           if atom x and (ncharsl := flength(x,ncharsl))
              or (null cdr args or distop)
                and (ncharsl := flength(x,ncharsl))>0
             then z := x . z
            else if distop and flength(x,nchars)>0
             then <<z := fout1(distop . args,w) . z;
                    args := list nil>>
            else <<z := fout1(x,w) . z;
                   ncharsl := flength(op,ncharsl)>>;
           ncharsl := flength(op,ncharsl);
           args := cdr args>>;
        return reversip!* z
   end;

symbolic procedure fout1(xexp,w);
   begin scalar fvar;
      fvar := genvar();
      explis := (xexp . fvar) . explis;
      fortpri(fvar,xexp,w);
      return fvar
   end;

% If we are in a comment, we want to continue to stay in one,
% Even if there's a formula. That's the purpose of this flag
% Added by James Davenport after Francoise Richard.

global '(comment!*);

symbolic procedure fprin2!* u;
   % FORTRAN output of U.
   begin integer m,n;
        if posn!*=0 then comment!* :=
                stringp u and cadr(explode u) eq 'C;
        n := flatsizec u;
        m := posn!*+n;
        if fixp u and !*period then m := m+1;
        if m<(linelength nil-spare!*) then posn!* := m
          else <<terpri();
                if comment!* then << fprin2 "C"; spaces 4 >>
                             else spaces 5;
                prin2 if fortlang!*='c then "  " else ". ";
                posn!* := n+7>>;
        fprin2 u;
        if fixp u and !*period then prin2 "."
   end;

symbolic procedure prin2!-downcase u;
   for each c in explode2 u do
      if liter c then prin2 red!-char!-downcase c else prin2 c;

symbolic procedure prin2!-upcase u;
   for each c in explode2 u do
      if liter c then prin2 red!-char!-upcase c else prin2 c;

symbolic procedure fprin2 u;
   % Prints id or string u so that case of all characters depends on
   % !*fortupper. Note !*lower setting only relevant here for PSL.
   (if !*fortupper then prin2!-upcase u else prin2!-downcase u)
    where !*lower = nil;

symbolic procedure red!-char!-downcase u;
   (if x then cdr x else u) where x = atsoc(u,charassoc!*);

symbolic procedure red!-char!-upcase u;
   (if x then car x else u) where x = rassoc(u,charassoc!*);

symbolic procedure fterpri(u);
   <<if not(posn!*=0) and u then terpri();
     posn!* := 0>>;

symbolic procedure genvar;
   intern compress append(explode svar,explode(scountr := scountr + 1));

mkop 'no_period;  % for printing of expressions with period locally off.

put('no_period,'fort,'fo_no_period);

symbolic procedure fo_no_period(u,p);
   begin scalar !*period; fmprint(cadr u,p) end;

endmodule;

end;
