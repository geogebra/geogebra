module codgen;

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, The Netherlands.;
% Author:     J.A. van Hulzen.                                        ;
% ------------------------------------------------------------------- ;

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


lisp$
global '(!*for!* !*do!*)$ % Gentran-globals used in makedecs.
global '(!*currout!*)$ % Gentran global used in redefinition
                       % of symbolic procedure gentran.
fluid '(!*gentranseg)$ % Gentran fluid introduced.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Patch 8 november 94 HvH.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

put('c,'preproc,'preproc)$
put('ratfor,'preproc,'preproc)$
put('fortran,'preproc,'preproc)$
put('pascal,'preproc,'preproc)$
put('c,'parser,'gentranparse)$
put('ratfor,'parser,'gentranparse)$
put('fortran,'parser,'gentranparse)$
put('pascal,'parser,'gentranparse)$
put('c,'lispcode,'lispcode)$
put('ratfor,'lispcode,'lispcode)$
put('fortran,'lispcode,'lispcode)$
put('pascal,'lispcode,'lispcode)$

global '(!*wrappers!*)$
!*wrappers!*:='(optimization segmentation)$

symbolic procedure optimization forms;
 if !*gentranopt then opt forms else forms$

symbolic procedure segmentation forms;
 if !*gentranseg then seg forms else forms$

symbolic procedure gentran!-wrappers!* forms;
begin
  if !*wrappers!* then
   foreach proc_name in !*wrappers!* do
              forms:=apply1(proc_name,forms);
  return forms
end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%
%%%% Herbert's facility can now be added:
%%%%
%%%% !*wrappers!*:=append(list('differentiate),!*wrappers!*)$
%%%% symbolic procedure differentiate forms;
%%%%  << load!-package adiff; adiff!-eval forms>>$
%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure gentran(forms, flist);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%      Redefinition of the main gentran procedure          %%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
begin scalar !:print!-prec!: ; % Gentran ignores print_precision
if flist then
    lispeval list('gentranoutpush, list('quote, flist));
forms:=
 apply1(get(gentranlang!*,'preproc) or get('fortran,'preproc),
       list forms);
apply1(get(gentranlang!*,'parser) or get('fortran,'parser),forms);
forms:=
 apply1(get(gentranlang!*,'lispcode) or get('fortran,'lispcode),forms);
forms:=gentran!-wrappers!* forms;
apply1(get(gentranlang!*,'formatter) or get('fortran,'formatter),
       apply1(get(gentranlang!*,'codegen) or get('fortran,'codegen),
              forms));
if flist then
<<
    flist := car !*currout!* or ('list . cdr !*currout!*);
    lispeval '(gentranpop '(nil));
    return flist
>>
else
    return car !*currout!* or ('list . cdr !*currout!*)
end$

%=================================================================
%=== The codgen.red module itself!!!
%=================================================================

symbolic procedure interchange_defs(def1,def2);
begin scalar temp1,temp2;
temp1:=getd def1; remd def1;
temp2:=getd def2; remd def2;
putd(def1,car temp2,cdr temp2);
putd(def2,car temp1,cdr temp1);
end$

symbolic procedure strip_progn(lst);
if pairp lst
   then if pairp(car lst) and caar(lst)='progn
           then cdar(lst)
           else if pairp(car lst) and
                   caar(lst)='prog and
                   cadar(lst)='nil
                   then cddar(lst)
                   else lst;

symbolic procedure add_progn(lst);
if pairp lst then append(list('progn),lst) else lst;

switch gentranopt$
!*gentranopt:=nil$
fluid '(delaylist!* delayoptlist!* delaydecs!* !*gendecs !*period!*)$

symbolic procedure delaydecs;
% ------------------------------------------------------------------- ;
% Effect: Redefinition of codegeneration functions.                   ;
% ------------------------------------------------------------------- ;
begin
 !*period!*:=!*period; !*period:=nil;
 delaydecs!*:=t; delaylist!*:=nil;
 symtabrem('!*main!*,'!*decs!*);
 symtabrem('!*main!*,'!*params!*);
 symtabrem('!*main!*,'!*type!*);
 !*wrappers!*:=
              delete('optimization,delete('segmentation,!*wrappers!*));
 interchange_defs('gentran,'gentran_delaydecs);
end;

put('delaydecs,'stat,'endstat)$

symbolic procedure gentran_delaydecs(forms,flist);
% ------------------------------------------------------------------- ;
% This procedure replaces the gentran-evaluator when production of    ;
% delcarations has to be delayed. The results of all gentran eval.s   ;
% are collected in the list delaylist!* and processed together  by    ;
% activating thre function make decs.                                 ;
% ------------------------------------------------------------------- ;
begin
forms:= apply1(get(gentranlang!*,'preproc) or
                 get('fortran,'preproc),
               list forms);
apply1(get(gentranlang!*,'parser) or get('fortran,'parser),forms);
forms:= apply1(get(gentranlang!*,'lispcode) or
                 get('fortran,'lispcode),
               forms);
forms:=gentran!-wrappers!* forms;
 if !*gentranopt then forms:=opt strip_progn forms;
 if !*gentranseg then forms:=seg forms;
 forms:=strip_progn forms;
 if delaylist!*
  then delaylist!*:=append(delaylist!*,forms)
  else delaylist!*:=forms
end;

symbolic procedure makedecs;
% ------------------------------------------------------------------- ;
% Effect: Original situation restored. Template processing performed. ;
% Symboltable cleaned up.                                             ;
% ------------------------------------------------------------------- ;
begin scalar gentranopt,gentranseg;
 if delayoptlist!*
  then gentranerr(nil,nil,"DELAYOPT ACTIVE",nil)
  else
   << !*period:=!*period!*;
      !*gendecs:=t; delaydecs!*:=nil;
      gentranopt:=!*gentranopt;!*gentranopt:=nil;
      gentranseg:=!*gentranseg;!*gentranseg:=nil;
      interchange_defs('gentran,'gentran_delaydecs);
        delaylist!* := subst('for,!*for!*, delaylist!*);  % JB 9/3/94
        delaylist!* := subst('do, !*do!*,  delaylist!*);  % JB 9/3/94
      apply('gentran,list(add_progn delaylist!*,nil));
      delaylist!*:=nil;
      !*wrappers!*:=
              append(!*wrappers!*,list('optimization,'segmentation));
      !*gentranopt:=gentranopt;!*gentranseg:=gentranseg;
   >>
end;

put('makedecs,'stat,'endstat)$

symbolic procedure delayopts;
% ------------------------------------------------------------------- ;
% This procedure allows to avoid optimization until further notice,   ;
% i.e. until the command makeopts is executed.                        ;
% All gentran evaluations are collected in the list delayoptlist!*.   ;
% Through makeopts this colection is processed in one run.            ;
% ------------------------------------------------------------------- ;
begin
 if not delaydecs!*
  then !*wrappers!*:=
              delete('optimization,delete('segmentation,!*wrappers!*));
 interchange_defs('gentran,'gentran_delayopt);
 delayoptlist!*:=nil
end;

put('delayopts,'stat,'endstat)$

symbolic procedure gentran_delayopt(forms,flist);
% ------------------------------------------------------------------- ;
% This procedure replaces the current gentran evaluator when produc-  ;
% tion of optimizwd code has to be delayed. We informally introduce a ;
% two-pass evaluation mechanism by doing so: one for gentran treatable;
% prefix statements and a second for optimization of this set of sta- ;
% tements.                                                            ;
% ------------------------------------------------------------------- ;
begin
forms:= apply1(get(gentranlang!*,'preproc) or
               get('fortran,'preproc),
               list forms);
apply1(get(gentranlang!*,'parser) or get('fortran,'parser),forms);
 if delayoptlist!*
  then delayoptlist!*:=
       append(delayoptlist!*,
              strip_progn(gentran!-wrappers!* lispcode forms))
  else delayoptlist!*:=strip_progn(gentran!-wrappers!* lispcode forms);
end;

symbolic procedure makeopts;
% ------------------------------------------------------------------- ;
% The previous gentran environment is restored and the list of state- ;
% ments delayoptlist!* is treated in this environment.                ;
% ------------------------------------------------------------------- ;
begin scalar gendecs,gentranopt;
 interchange_defs('gentran,'gentran_delayopt);
 gentranopt:=!*gentranopt;!*gentranopt:=t;
 gendecs:=!*gendecs; !*gendecs:=nil;
 if delaydecs!*
  then
   if delaylist!*
    then delaylist!*:=
          append(delaylist!*,strip_progn opt delayoptlist!*)
    else delaylist!*:=strip_progn opt delayoptlist!*
  else << !*wrappers!*:=
               append(!*wrappers!*,list('optimization,'segmentation));
          apply('gentran,list(add_progn delayoptlist!*,nil))
       >>;
 delayoptlist!*:=nil; !*gentranopt:=gentranopt ; !*gendecs:=gendecs;
end;

put('makeopts,'stat,'endstat)$

endmodule;
end;
