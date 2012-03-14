% make-smacros.red

%**************************************************************************
%* Copyright (C) 2010, Codemist Ltd.                     A C Norman       *
%*                                                                        *
%* Redistribution and use in source and binary forms, with or without     *
%* modification, are permitted provided that the following conditions are *
%* met:                                                                   *
%*                                                                        *
%*     * Redistributions of source code must retain the relevant          *
%*       copyright notice, this list of conditions and the following      *
%*       disclaimer.                                                      *
%*     * Redistributions in binary form must reproduce the above          *
%*       copyright notice, this list of conditions and the following      *
%*       disclaimer in the documentation and/or other materials provided  *
%*       with the distribution.                                           *
%*                                                                        *
%* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
%* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
%* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
%* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
%* COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
%* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
%* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
%* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
%* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
%* TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
%* THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
%* DAMAGE.                                                                *
%*************************************************************************/




% Run this file using bootstrapreduce to create a file smacros.red that
% can perhaps be copied to packages/support.


% Unexpectedly there are some things where a conversion to an SMACRO is
% fatal. Eg some parts of the code explicitly look to see if code has a call
% to AEVAL at its head as a signature of algebraic mode stuff. There may be
% further such cases that I have not spotted yet. Nut I keep a list "omit"
% here of cases that must not be mapped this way.

% I leave the non-smacro definition in place because sometimes it is needed
% because the function is put as a 'SIMPFN or other property.

% I further support a how_many option that can be used to limit how many
% functions get adjusted: this is amazingly useful when debugging in that
% one can go binary-chop on how_many to identify exactly which function
% causes trouble.

% how_big controls the size of function I will be willing to expand
% in-line

% The exact set of smacros that this code picks out seems to be pretty
% sensitive - eg to what happens to have been defined as an smacro in the
% image file used to run it. I probably suggest making a version of
% bootstrapreduce based on smacros0.red (ie no EXTRA smacros) and using
% that when running this script - at last that should give consistent
% results. The oddities may arise in part because when there are extra
% smacros that causes some functions to expand, and hence cease to be
% candidates for in-lining.

symbolic;

if not boundp 'how_many then how_many := 10000
else << how_many := compress explodec how_many;
        if not numberp how_many then how_many := 10000 >>;

if not boundp 'how_big then how_big := 20
else << how_big := compress explodec how_big;
        if not numberp how_big then how_big := 20 >>;

omit := '(
  aeval       % used as a marker for symbolic/algebraic mode
  aeval!*     % ditto
  reval       % ditto
  revalx      % ditto
  linelength  % redefined in terms of itself!
  module      % delicate re bootstrapping?
  endmodule   % just to be on the safe side!
  prepsqxx    % I do not understand this one
  setpchar    % evil redefinition in terms of itself!
  simpminus   % I do not understand this one
  rplacw      % I suspect performance may not be helped, rplac is dangerous.
  setcar      % Disaster when expanded textually ...
  setcdr      % ... eg setcdr(x, cddr x) -> <<rplacd(x,cddr x); cddr x>> oops.
  cr!:minus   % ???

% The next things listed are functions where there seem to be at least
% two defintions. This does not include anything that has its definition
% overwritten using copyd or putd.

  add2resultbuf
  altitude
  ashift
  circle
  deg
  depend
  depends
  diffp
  domainp
  exptpri
  fancy!-binomial
  fancy!-condpri
  fancy!-flush
  fancy!-hypergeometric
  fancy!-inprint1
  fancy!-inprint2
  fancy!-intpri
  fancy!-limpri
  fancy!-lower!-digits1
  fancy!-maprin0
  fancy!-maprint!-atom
  fancy!-matpri2
  fancy!-meijerg
  fancy!-out!-header
  fancy!-out!-item
  fancy!-out!-trailer
  fancy!-output
  fancy!-prin2!*
  fancy!-sub
  fancy!-tex
  flatten
  getrtype
  getrtype1
  getrtype2
  getrtypecadr
  limit
  line
  lineint
  lshift
  maprint
  maxi
  median
  midpoint
  multf
  multfnc
  newton
  nodepend
  noncom
  normbf
  onoff
  ordop
  ordp
  ordpa
  other_cc_point
  other_cl_point
  p3_angle
  pedalpoint
  polynomqqq
  prin2!*
  rcons
  reordop
  scprint
  select!-eval
  subs3f1
  subsetp
  superprinm
  superprintm
  symbol
  texlet
  ttttype_ratpoly
  type_ratpoly
  varpoint
  vectoradd
  vectorplus

% Some more things that give trouble...
  packages_to_load   % use as smacro messes up 'eval property, or lack thereof

% Functions that must not be compiled into C or turned into SMACROs
% because their definitions are moved or changed dynamically.

  gentran                  % scope/codgen.red
  gentran_delaydecs        % scope/codgen.red

% any autoload entrypoint  % support/entries.red
                           % Body is (progn (load!-package ..) ..)

  oldreclaim               % crack/crutil.red
  !%reclaim
  reclaim

  quotientx                % dipoly/bcoeff.red

  excalcputform            % eds/edspatch.red

  groebspolynom            % groebner/hggroeb.red
  true!-groebspolynom

  lr_add2resultbuff        % libreduce/libreduce.red
  add2resultbuff
  setpchar
  setpchar!-orig
  setpchar!-csl
  setpchar!-csl
  lr_compute!-prompt!-string
  compute!-prompt!-string
  yesp
  lr_yesp!-orig
  lr_yesp!-psl
  break_prompt
  lr_break_prompt

  ODESolve!-old!-subsublis % odesolve/odenon1.red
  subsublis
  ODESolve!-subsublis


  plot!-exec               % plot/gnuintfc.red
  explodec                 % plot/gnupldrv.red

  simpexpt                 % qsum/qsum.red
  original_simpexpt
  new_simpexpt

  olderfaslp_orig          % rd/rd.red
  olderfaslp

  redfront_setpchar!-orig  %redfront/redfront.red
  setpchar
  redfront_setpchar!-csl
  redfront_setpchar!-psl
  yesp
  redfront_yesp!-psl
  redfront_compute!-prompt!-string
  compute!-prompt!-string
  redfront_break_prompt
  break_prompt

  for                      % rlisp88/rlisp88.red
  oldrepeat*
  repeat
  oldwhile!*
  while

  linelength               % tmprint/tmprint.red
  linelength!-orig
  tm_setpchar!-orig
  setpchar
  tm_setpchar!-csl
  tm_setpchar!-psl
  yesp
  tm_yesp!-orig
  tm_compute!-prompt!-string!-orig
  compute!-prompt!-string
  break_prompt
  tm_break_prompt

  prin2!*_orig             % utf8/utf8.red
  prin2!*
  scprint_orig
  scprint
  exptpri_orig
  exptpri
  intprint

  scprint                 % redlog/mma/mma.red
  mma_scprint!-orig


  );

<< terpri();
   princ "how_many = "; print how_many;
   princ "how_big  = "; print how_big;
   nil >>;

!*load!-source := t;

% I will load all the packages that are in the core. There is a worry
% if any of these provide multiple definitions of some name!

<< load!-source 'rlisp;
   load!-source 'alg;
   load!-source 'poly;
   load!-source 'polydiv;
   load!-source 'arith;
   load!-source 'mathpr;
   load!-source 'ezgcd;
   load!-source 'factor;
   load!-source 'hephys;
   load!-source 'int;
   load!-source 'matrix;
   load!-source 'rlisp88;
   load!-source 'rprint;
   load!-source 'fmprint;
   load!-source 'pretty;
   load!-source 'solve;
   load!-source 'desir;
   load!-source 'ineq;
   load!-source 'modsr;
   load!-source 'rsolve;
   load!-source 'algint;
   load!-source 'arnum;
   load!-source 'assist;
   load!-source 'dummy;
   load!-source 'cantens;
   load!-source 'atensor;
   load!-source 'avector;
   load!-source 'invbase;
   load!-source 'misc;
   load!-source 'boolean;
   load!-source 'cedit;
   load!-source 'rcref;
   load!-source 'reset;
   load!-source 'cali;
   load!-source 'camal;
   load!-source 'changevr;
   load!-source 'compact;
   load!-source 'dfpart;
   load!-source 'lie >>;

on comp;

load!-module 'rprint;

% The normal version of rprint has some treatment that is there so it
% can display algebraic mode code in a tidy manner. For instance it
% will turn a use of "evalgreaterp(a,b)" into "a>b". That is a real
% disaster in the sort of use that is being made here, so I will now
% provide an alternative version that does not have such clever
% extras! Actually I can use EXACTLY the same CODE but just set the tables
% up differently.

fluid '(!*lower !*n buffp combuff!* curmark curpos orig pretop
        pretoprinf rmar rprifn!* rterfn!*);

pretop := 'csl!-op; pretoprinf := 'csl!-oprinf;

put('cond,pretoprinf,'condox);

put('if,pretoprinf,'ifox);

put('return,pretoprinf,'retox);

put('rblock,pretoprinf,'blockox);

put('prog,pretoprinf,'progox);

put('go,pretoprinf,'gox);

put('!*label,pretoprinf,'labox);

put('quote,pretoprinf,'quotox);

put('prog2,pretoprinf,'prognox);

put('progn,pretoprinf,'prognox);

put('list,pretoprinf,'listox);

put('repeat,pretoprinf,'repeatox);

put('while,pretoprinf,'whileox);

put('proc,pretoprinf,'procox);

put('procedure,pretoprinf,'proceox);

put('de,pretoprinf,'deox);

put('ds,pretoprinf,'dsox);

put('string,pretoprinf,'stringox);

put('lambda,pretoprinf,'lambdox);

put('foreach,pretoprinf,'eachox);

put('for,pretoprinf,'forox);

put('forall,pretoprinf,'forallox);

if null get('!*semicol!*,pretop)
  then <<put('!*semicol!*,pretop,'((-1 0)));
         put('!*comma!*,pretop,'((5 6)))>>;


% End of rprint customisation

symbolic procedure listsize x;
  if null x then 0
  else if atom x then 1
  else listsize car x + listsize cdr x;

symbolic procedure does_setq(x, v);
  if atom x or eqcar(x, 'quote) then nil
  else if eqcar(x, 'setq) then
     eqcar(cdr x, v) or
     (not atom cdr x and does_setq(cddr x, v))
  else does_setq(car x, v) or does_setq(cdr x, v);

symbolic procedure does_any_setq(x, l);
  if atom l then nil
  else if does_setq(x, car l) then t
  else does_any_setq(x, cdr l);

fns := nil;

count := 0;

for each x in oblist() do
   if how_many > 0 and
      (d := get(x, '!*savedef)) and
      listsize d < how_big then <<
         how_many := how_many - 1;
         prin (count := count + 1);
         princ ": ";
         print x;
         fns := x . fns >>;

fns := reverse fns$

<<

ofil := wrs open("smacros.red", 'output);

printc "% smacros.red - automatically generated from other source files";
terpri();
printc "% Redistribution and use in source and binary forms, with or without";
printc "% modification, are permitted provided that the following conditions are met:";
printc "%";
printc "%    * Redistributions of source code must retain the relevant copyright";
printc "%      notice, this list of conditions and the following disclaimer.";
printc "%    * Redistributions in binary form must reproduce the above copyright";
printc "%      notice, this list of conditions and the following disclaimer in the";
printc "%      documentation and/or other materials provided with the distribution.";
printc "%";
printc "% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ""AS IS""";
printc "% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,";
printc "% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR";
printc "% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR";
printc "% CONTRIBUTORS";
printc "% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR";
printc "% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF";
printc "% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS";
printc "% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN";
printc "% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)";
printc "% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE";
printc "% POSSIBILITY OF SUCH DAMAGE.";
printc "%";
terpri();

count := 0;

while fns do <<

fns1 := fns;
fns := nil;

for each xx on fns1 do <<
   x := car xx;
   d := get(x, '!*savedef);
   if null d then d := get(x, 'smacro);
   if eqcar(d, 'lambda) and      % must be a real function
      not atom cdr d and         % ... properly formatted
      not memq(x, omit) and      % Not on list of explicit exclusions
      not smemq('declare, d) and % No DECLARE of fluids
      not smemq('aeval, d) and   % No algebraic mode
      not smemq('forall, d) and  % Some confusion with rprint?
      not smemq('load!-package, d) and
                                 % Not an autoload stub
      not (not atom cdr d and does_any_setq(cddr d, cadr d)) and
                                 % Assigns to its argument
      not smemq(x, d) then <<    % Calls itself
% Really I need a topological sort here so that if one small function
% depends on another the order in which smacros are introduced makes
% sense. OK so I do that! And if I find a pair of smacros so that each
% calls the other then I will discard one of them to break the loop.
% The issue of what happens if there is a chain of three or more functions
% which call each other is one I will think about when and if it arises!
      defer := nil;
      discard := nil;
      for each n in xx do
         if smemq(n, d) then <<
            if smemq(x, get(n, 'smacro)) then discard := t;
            defer := t >>;
      if discard then nil
      else if defer then fns := x . fns
      else <<
         terpri();
         rprint ('ds . x . cdr d);
         count := count+1;
         terpri() >> >> >>;

fns := reverse fns;

>>;

terpri();
printc "end;";
terpri();

close wrs ofil;

prin count; printc " smacros created";

window!-heading
  list!-to!-string
    append(explodec count, explodec " smacros created");

>>;

end;
