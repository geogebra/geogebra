module tmprint; % Output module for TeXmacs interface
                % this is fmprint with minor modifications
                % Note that fmprint will not have been updated
                % to track changes made here.


% Fancy output package for symbolic expressions.
% using TEX as intermediate language.

% Author: Herbert Melenk, using ideas of maprin.red (A.C.H, A.C.N).

% Copyright (c) 1993 RAND, Konrad-Zuse-Zentrum.  All rights reserved.





% ----------------------------------------------------------------------
% $Id: tmprint.red,v 1.15 2006/06/29 20:04:29 sturm Exp $
% ----------------------------------------------------------------------
% Copyright (c) 1993-1994, 1999, 2003-2005 A. Dolzmann, T. Hearn, A.
% Grozin, H. Melenk, W. Neun, A. Norman, A. Seidl, and T. Sturm
%
% Permission is hereby granted, free of charge, to any person
% obtaining a copy of this software and associated documentation files
% (the "Software"), to deal in the Software without restriction,
% including without limitation the rights to use, copy, modify, merge,
% publish, distribute, sublicense, and/or sell copies of the Software,
% and to permit persons to whom the Software is furnished to do so,
% subject to the following conditions:
%
% The above copyright notice and this permission notice shall be
% included in all copies or substantial portions of the Software.
%
% THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
% EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
% MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
% NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
% BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
% ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
% CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
% SOFTWARE.
% ----------------------------------------------------------------------


% $Log: tmprint.red,v $
%
% The set of explanations about revisons here are now rather out of date.
% Recent history can be found by browsing the subversion repository at
% sourceforge. However as of Easter 2011, Arthur Norman is having a go at
% cleaning things up a little.
%
% Revision 1.15  2006/06/29 20:04:29  sturm
% There are now two different linelength patches for the two Lisps. Arthur
% hat pointed out that he does not want any at all. We have to understand
% that, and find a clean solution!
%
% Revision 1.14  2006/06/27 16:49:39  sturm
% The linelength hack did not work the way it was. PSL did not really extend
% the line length.
% Redesigned prompt.
%
% Revision 1.13  2005/10/15 11:21:32  seidl
% Released under a MIT-style licence, as all authors have agreed.
%
% Revision 1.12  2005/08/25 17:06:02  sturm
% Declare lispsystem!* fluid instead of global. In PSL it is in fact fluid, and
% according to Arthur this is compatible with CSL.
%
% Revision 1.11  2005/08/24 15:29:24  seidl
% Changes of fancy!-intpri as suggested by Winfried Neun: bounds of the
% intregral are printed now.
%
% Revision 1.10  2004/11/20 20:50:14  seidl
% Linelength hack established again, only if Texmacs runs. Removed
% centering and curly brackets from fancy-out-header and -trailer.
% New switch promptnumbers, turned off only if Texmacs is running.
%
% Revision 1.9  2004/11/19 00:52:26  seidl
% fancy!-out!-header and fancy!-out!-trailer were split up between the
% different lisps although they are the same. Now there is one version
% again. Fixed the unbalanced parentheses problem around \displaystyle.
%
% Revision 1.8  2004/11/18 20:44:16  seidl
% Further changes by ACN to help with CSL-based systems:
% (*) use of \mathit{A} for \Alpha and related changes to avoid direct
% reference to character codes
% (*) added texsymbol(), texbox(), texfbox() and texstring() formatting
% at least for the benefit of those debugging.
% (*) new fluids !*standard!-output!* !*math!-output!* !*spool!-output!*
% (*) changes to fancy!-output
% (*) splitted fancy!-flush between csl and psl
% (*) changes to fancy!-binomial
%
% Revision 1.7  2004/11/09 01:11:17  seidl
% Changes by ACN to help with CSL-based systems
% (*) switch redfront_mode made present in the CSL build to control
% whether prompt colouring is generated via escapes and an external
% package or by the underlying Lisp system directly. I think this MAY be
% useful for PSL too but did not enable it there to avoid introducing
% inconsistencies with current behaviour.
% (*) elaborate scheme to try to arrange that screen display and log-file
% stuff both end up in good style in the CSL case.
%
% Revision 1.6  2004/09/24 10:42:41  seidl
% Taken over changes by A C Norman related to CSL, except of whitespace
% changes:
%
% This version is being hacked by ACN to make it CSL-friendly. Perhaps
% the main part of that will be re-working string processing operations
% so that they are less sensitive to the exact behaviours of intern and
% compress and looking carefully at the way that things with control
% characters in get printed.
% So that I can work on this comfortably I will expand tabs to blanks
% and mend places where this results in very obviously damaged layout.
%
% Remove fancy!-mode!* since Reduce 3.5 is now rather ancient history.
%
% Revision 1.5  2004/08/12 13:04:23  seidl
% Version for the Reduce development system for inclusion into Reduce 3.8.
%
% Revision 1.4  2003/11/20 13:10:44  sturm
% I think the protocol to Texmacs was not clean. One int2id 5 deleted.
%
% Revision 1.3  2003/11/20 12:23:01  sturm
% Temporary remflag lose on break_prompt for reloading.
% Removed linelength hack.
% Removed overflowed!* test in fancy!-maprin0 instead of linelength hack.
% Do not explode atoms starting with "\" in fancy!-prin2!*.
% Dirty, but apparently working.
%
% Revision 1.2  2003/11/20 11:06:12  sturm
% Texmacs now basically runs.
% Linelength workaround does not function anymore due to missing filter.
%
% Revision 1.1  2003/11/11 11:08:57  sturm
% Inital check-in.
% This is the original version by Andrey Grozin as obtained from fmprint.red
% via patching.
%
%   8-Sep-94
%               introduced data driven formatting (print-format)

%  12-Apr-94
%               removed print function for dfp
%               removed some unused local variables
%               corrected output for conditional expressions and
%                 aeval/aeval* forms

%  17_Mar-94    corrected line breaks in Taylor expressions
%               rational exponents use /
%               vertical bar for SUB expressions
%               explicit * for product of two quotients (Taylor)

%  94-Jan-26 - Output for Taylor series repaired.
%  94-Jan-17 - printing of index for Bessel function repaired.
%            - New functions for local encapsulation of printing
%              independent of smacro fancy!-level.
%            - Allow printing of upper case symbols locally
%              controlled by *fancy-lower

%  93-Dec-22 Vectors printed with square brackets.


% ----------------------------------------------------------------------


% switches
%
%    ON FANCY          enable algebraic output processing by this module
%
%    ON FANCY_TEX      under ON FANCY: display TEX equivalent
%

% properties used in this module:
%
%     fancy-prifn      print function for an operator
%
%     fancy-pprifn     print function for an oeprator including current
%                      operator precedence for infix printing
%
%     fancy!-flatprifn print function for objects which require
%                      special printing if prefix operator form
%                      would have been used, e.g. matrix, list
%
%     fancy-prtch      string for infix printing of an operator
%
%     fancy-special-symbol
%                      print expression for a non-indexed item
%                      string with TEX expression  "\alpha"
%                         or
%                      number referring ASCII symbol code
%
%     fancy-infix-symbol    special-symbol for infix operators
%
%     fancy-prefix-symbol   special symbol for prefix operators
%
%     fancy!-symbol!-length  the number of horizontal units needed for
%                      the symbol.  A standard character has 2 units.


create!-package('(tmprint),nil);

fluid  '(
         !*list
         !*nat
         !*nosplit
         !*ratpri
         !*revpri
         overflowed!*
         p!*!*
         testing!-width!*
         tablevel!*
         sumlevel!*
         outputhandler!*
         outputhandler!-stack!*
         posn!*
         long!*
         obrkp!*    % outside-brackets-p
            );

global '(!*eraise charassoc!* initl!* nat!*!* spare!* ofl!*);

%
% The interaction between the code here and avariety of other Reduce flags
% that set output options is possibly delicate and probably often broken.
% As well as "list" the code here needs review with regard to options
% such as "fort" for generating other formats of output.
%

switch list,ratpri,revpri,nosplit;

% Temp experiment while investigating a possible for for an interaction with
% "on list". Well in fact "on/off acn" can provide a general guard for
% some incremental changes being made here.   But evenually this switch
% will be retired.                 ACN March 2011
switch acn;

% Global variables initialized in this section.

fluid '(
      fancy!-switch!-on!*
      fancy!-switch!-off!*
      !*fancy!-mode
      fancy!-pos!*
      fancy!-line!*
      fancy!-page!*
      fancy!-bstack!*
      !*fancy_tex
      !*fancy!-lower    % control of conversion to lower case
      );

fluid '(fancy!-texwidth fancy!-texpos tex!-pointsize);

switch fancy_tex; % output TEX equivalent.

fancy!-switch!-on!* := int2id 16$
fancy!-switch!-off!* := int2id 17$
!*fancy!-lower := nil;

global '(fancy_lower_digits fancy_print_df);

share fancy_lower_digits; % T, NIL or ALL.

if null fancy_lower_digits then fancy_lower_digits:=t;

share fancy_print_df;     % PARTIAL, TOTAL, INDEXED.

if null fancy_print_df then  fancy_print_df := 'partial;
switch fancy;

put('fancy,'simpfg,
  '((t (fmp!-switch t))
    (nil (fmp!-switch nil)) ));


symbolic procedure fmp!-switch mode;
      if mode then
        <<if outputhandler!* neq 'fancy!-output then
          <<outputhandler!-stack!* :=
                outputhandler!* . outputhandler!-stack!*;
           outputhandler!* := 'fancy!-output;
          >>
        >>
      else
        <<if outputhandler!* = 'fancy!-output then
          <<outputhandler!* := car outputhandler!-stack!*;
            outputhandler!-stack!* := cdr outputhandler!-stack!*;
          >>
          else
             rederr "FANCY is not current output handler"
% ACN feels that raising an error on an attempt to switch off an option
% in the case that the option is already disabled is a bit harsh.
        >>;

fluid '(lispsystem!*);

procedure texmacsp;
   % Texmacs predicate. Returns [t] iff Texmacs is running.
   if getenv("TEXMACS_REDUCE_PATH") then t;

copyd('linelength!-orig,'linelength);
remd('linelength);

!#if (memq 'psl lispsystem!*)

procedure linelength(a);
   if texmacsp() then linelength!-orig(30000) else linelength!-orig(a);

!#else

% In CSL you can look for memq('texmacs,lispsystem!*) to see if the command-
% line option "--texmacs" was given when Reduce was started.
procedure linelength(a);
   if texmacsp() then 30000 else linelength!-orig(a);

!#endif

% The next two functions provide abstraction for conversion between
% strings and lists of character objects.

!#if (memq 'csl lispsystem!*)

% Under CSL the eventual state will be that IF output is going directly
% to a window that can support maths display then I will send stuff there
% so it gets displayed using the CSL embedded code. If on the other hand
% output is going to a pipe or a file or basically anything other than
% directly to the screen I will issue the codes that texmacs likes to see.
%

% Convert a list of character objects into a string.
% (The function list!-to!-string already exists...)

% Convert a string into a list of character objects.
smacro procedure string!-to!-list a;
    explode2 a;

% Print a string without ANY conversion or adjustment, so if the string
% has control characters etc in it they get transmitted unchanged. Well
% let me express some reservations about what might happen if the string
% contains tabs and newlines - the lower level system IO code might
% interpret same...
smacro procedure raw!-print!-string s;
    prin2 s;

% Print the character whose code is n.
smacro procedure writechar n;
    tyo n;    % Like "prin2 int2id n"

% Convert a symbol or string to characters but ensure that all
% output characters are folded to lower case.
% CSL already has explode2lc;

!#else

smacro procedure list!-to!-string a;
    compress ('!" . append(a, '(!")));

smacro procedure string!-to!-list a;
    explode2 a;

% I do not know if this has to be like this in PSL, but it reflects
% what was in the code.
symbolic procedure raw!-print!-string s;
     for each x in string!-to!-list s do prin2 x;
% writechar already exists in PSL.

symbolic procedure explode2lc s;
    explode2 s where !*lower = t;

!#endif

symbolic procedure fancy!-tex s;
  % test output: print tex string.
   <<prin2 fancy!-switch!-on!*;
      raw!-print!-string s;
     prin2t fancy!-switch!-off!*;
   >>;

symbolic procedure fancy!-out!-item(it);
  if atom it then prin2 it else
  if eqcar(it,'ascii) then writechar(cadr it) else
  if eqcar(it,'tab) then
      for i:=1:cdr it do prin2 " "
    else
  if eqcar(it,'bkt) then
     begin scalar m,b,l; integer n;
      m:=cadr it; b:=caddr it; n:=cadddr it;
      l := b member '( !( !{ );
   %  if m then prin2 if l then "\left" else "\right"
   % else
%      if n> 0 then
%      <<prin2 if n=1 then "\big" else if n=2 then "\Big" else
%            if n=3 then "\bigg" else "\Bigg";
%       prin2 if l then "l" else "r";
%      >>;
      if l then prin2 "\left" else prin2 "\right";
      if b member '(!{ !}) then prin2 "\";
      prin2 b;
    end
    else
      rederr "unknown print item";

symbolic procedure set!-fancymode bool;
  if bool neq !*fancy!-mode then
    <<!*fancy!-mode:=bool;
      fancy!-pos!* :=0;
      fancy!-texpos:=0;
      fancy!-page!*:=nil;
      fancy!-line!*:=nil;
      overflowed!* := nil;
        % new: with tab
      fancy!-line!*:= '((tab . 1));
      fancy!-pos!* := 10;
      sumlevel!* := tablevel!* := 1;
   >>;

!#if (memq 'csl lispsystem!*)
fluid '(!*standard!-output!* !*math!-output!* !*spool!-output!*);
!#endif

symbolic procedure fancy!-output(mode,l);
% Interface routine.

% ACN does not understand the "posn!*>2" filter here. To avoid some
% bad consequences it was having for my new screen/log-file stuff it now only
% applies in maprin mode not terpri mode, but it would be nice if somebody
% could explain to me just why it was needed in the first case at all.  I can
% imagine that if "on fancy" is acticated when there is still some partly-
% printed expression (in non-fancy mode) buffered up the terpri!* to flush it
% may need special care. But if that is what it is about I would suggest that
% treatment be applied in fmp!-switch not here...

   if ofl!* or (mode='maprin and posn!*>2) or not !*nat then <<
% not terminal handler or current output line non-empty.
      if mode = 'maprin then maprin l
      else terpri!*(l) >> where outputhandler!* = nil
      else
% I want to do some more magic for CSL here. In CSL the system can be launched
% or run-time configured so that a transcript of screen output goes to a
% file, the "log file". In the CSL sources the handle for this file is known
% as "spool_file". It does not look sensible to me that TeX-ified maths
% should go there even if that is what best goes to the screen. Thus I think I
% want fancy mode in CSL with a spool_file enabled to do something rather like
%
%    wrs math-output-destination;
%    fancy!-maprin0 expression;
%    wrs spool_file;
%    maprin0 expression;
%    wrs undivided standard output;
%
% Rather than using "wrs" here I will re-bind the CSL variable
% *standard-output*. This achieves a similar effect but guarantees that
% the regular situation is restored if there is ANY sort of exit from the
% maths display code - eg a user-generated interrupt. It I had used wrs then
% I could perhaps have restored things using errorset, but this feels easier.
% Also this little section of code is pretty CSL-specific since it is
% working with the CSL-embedded display code, so I do not feel bad about
% going beyond Standard Lisp.
%
% A further wrinkle on this wants to be that garbage collector and diagnostic
% output always goes to the undivided standard output in the normal way, and
% this output to the "math-output" stream can never be interrupted by any
% such. If a section of maths display is not completed then the maths output
% will find that it has a fancy_header but no fancy_trailer, and any request
% for user input or any error exit will force terminate it leaving a visibly
% incomplete fragment (which the display code can detect and ignore).
%
% Note that the risk of error or garbage collection during maths display is
% not actually terribly high since  all that is done between the generation
% of header & trailer is a load of calls to fancy!-out!-item, ie ready
% prepared sequences of items get printed. Also the normal maprin just buffers
% things up and only displays them when terpri!* is called. So I can afford to
% use both fancy!-maprin0 and maprin and then fuss about destinations a bit
% more at terpri!* time.  In this regard observe that because I have got here
% I know I on in "on nat" mode. In that case setting pline!* to nil has the
% effect of discarding any built-up layout.
   <<set!-fancymode t;
      if mode = 'maprin then <<
!#if (memq 'csl lispsystem!*)
% math!-display 1 will not do anything, but returns true if a spool_file
% is active.
         if getd 'math!-display and
            math!-display 0 and
            math!-display 1 then <<
            maprin l where outputhandler!* = nil >>;
!#endif
         fancy!-maprin0 l >>
      else <<
!#if (memq 'csl lispsystem!*)
         if getd 'math!-display and
            math!-display 0 and
            math!-display 1 then <<
            terpri!* l where outputhandler!* = nil
                       where !*standard!-output!* = !*spool!-output!* >>;
!#endif
         fancy!-flush() >> >>;

symbolic procedure fancy!-out!-header();
    <<
       if posn()>0 then terpri();
       prin2 int2id 2;
       prin2 "latex:\black$\displaystyle "
    >>;

symbolic procedure fancy!-out!-trailer();
    <<
       prin2 "$";
       prin2 int2id 5
    >>;

!#if (memq 'csl lispsystem!*)

symbolic procedure fancy!-flush();
  begin
    fancy!-terpri!* t;
    if getd 'math!-display and math!-display 0 then <<
      math!-display 2; % clear out any previous junk
      for each line in reverse fancy!-page!* do
        if line and not eqcar(car line,'tab) then <<
          if 'wx memq lispsystem!* then fancy!-out!-item "\[";
          for each it in reverse line do fancy!-out!-item it;
          if 'wx memq lispsystem!* then fancy!-out!-item "\]";
          terpri() >>;
      math!-display 3 >> where !*standard!-output!*=!*math!-output!*
    else for each line in reverse fancy!-page!* do
      if line and not eqcar(car line,'tab) then <<
         fancy!-out!-header();
         for each it in reverse line do fancy!-out!-item it;
         fancy!-out!-trailer() >>;
    set!-fancymode nil
  end;

!#else

symbolic procedure fancy!-flush();
  begin
    scalar !*lower;  % Rebinding *lower is needed for PSL here
    fancy!-terpri!* t;
        for each line in reverse fancy!-page!* do
      if line and not eqcar(car line,'tab) then <<
         fancy!-out!-header();
          for each it in reverse line do fancy!-out!-item it;
         fancy!-out!-trailer() >>;
     set!-fancymode nil
  end;

!#endif

%---------------- primitives -----------------------------------

symbolic procedure fancy!-special!-symbol(u,n);
   if numberp u then
     <<fancy!-prin2!*("\symb{",n);
       fancy!-prin2!*(u,0);
       fancy!-prin2!*("}",0);
     >>
    else fancy!-prin2!*(u,n);

symbolic procedure fancy!-prin2 u;
    fancy!-prin2!*(u,nil);

% fancy-prin2!* maintains a variable fancy!-pos!* which is compared
% against (multiples of) linelength. This is not incremented when a
% TeX keyword is inserted. That is probably reasonable for some
% words such as "\mathrm", but seems odd for "\alpha".
% It is incremented for "{" and "}" and also for "^" and "_". That also
% seems deeply wrong. And to the extent that it is used to estimate the
% width of the current part-line it is certainly oblivious to the
% different metrics that "\,", "i", "m", and "\ldots" might have, where
% those are rather more than minor.
%
% So even if one assumes that the units in which linelength() returns
% its value are relevant in TeX output (they probably are at least
% roughly, except that the idea of users altering linelength and getting
% different behaviour seems pretty scary to me, and the potential confusion
% between desired width of mathematical display and the number of character
% positions that the TeX material should fit with seems messy) the calculation
% done here is a bit of a mess.

% I think that what I wish to do is to assume that reasonable output width
% for TeX is (say) 500pts and so replace all calls "linelenth nil" with
% reference to tex!-width!-points, a global variable initialised to 500.
% Then I would want to re-work fancy!-prin2 to provide at least a rough
% estimate of the width of each character based on expecting the width of
% an average letter or digit to be around 6.25pts. Well it will be rather
% nicer if even these crude estimates are make in units of millipoints, since
% otherwise I will have enough issues of rounding to corrupt even rather
% coarse calculations.

% If I suppose that letters and digits are generally going to be set in
% cmr or cmmi fonts in main, script or scriptscript size can have at least
% approximate tabulation rather easily. To exploit this fancy!-prin2!* will
% need to be aware when it is setting full-sized text and when it is processing
% a sub- or superscript. It may be acceptable here to keep just one table of
% widths and cope with scripts by simple scaling.

% Greek letters and mathematic symbols (such as "\infty" and "\forall")
% will need a width attribute. And any parts of tmprint.red that generate
% stacked structures (such as displayed fractions, matrices etc) may need
% to be reviewed.

% For the purposes of a width estimate sufficient to guide line breaking
% I feel that ligatures and kerning can be ignored. Life is messy and hard
% enough as it is!


% The unit for texwidth will be "points". That perhaps really means
% something on paper, with 72 points per inch (or thereabouts, depending
% on how pedantic you are). On screen it is much less certain. However
% 500 points is roughtly a sensible width to use as the printable area
% across an A4 or letter sheet of paper, so for now I make it my default.

fancy!-texwidth := 500;

% texwidth will let you reset the width that will be used, but as a matter
% of sanity I will never let it set a width of less than 100 points (ie
% about 1.4 inches, 3.5cm). The previously set width is returned.

symbolic procedure texwidth n;
  begin
    scalar old;
    old := fancy!-texwidth;
    if fixp n and n >= 100 then fancy!-texwidth := n;
    return old
  end;

flag('(texwidth), 'opfn);


% You may generate the TeX either as a (default) 10pt document or
% with a 12-point basic size. This only gives a rather small change
% but it may be useful in terms of control over readibility.

tex!-pointsize := 10;


symbolic procedure texpointsize n;
  begin
    scalar old;
    old := tex!-pointsize;
    if n = 10 or n = 12 then tex!-pointsize := n;
    return old
  end;

flag('(texpointsize), 'opfn);


% Widths for characters in Computer Modern Fonts

% extracted from wxfonts/tfm

% Widths here are given in millipoints, and I include (at present)
% four key mathematical fonts, all at nominal size 10pt. Since I am
% only using this to give me and ESTIMATE of the width of the TeX
% output so I have a reasonable idea of where to split lines I will
% assume that other sizes can have their metrics deduced by scaling.
% But when I do a bit of research I find different sizes quoted even
% for basic cases, so the line I will follow will be based on the
% following sequences for 10 and 12-point main text.
%
%   tiny       5     6
%   script     7     8
%   footnote   8     10
%   small      9     11
%   normal     10    12   <<<<< main default size
%   large      12    14
%   Large      14    17
%   LARGE      17    20
%   huge       20    25
%   Huge       25    25

fluid '(cm!-widths!*);

!#if (memq 'psl lispsystem!*)
symbolic procedure list!-to!-vector a; list2vector a;
!#endif

cm!-widths!* := list(
    % name checksum design-size (millipoints)
    list("cmex10", -89033454, 10000, list!-to!-vector '(
       4583    4583    4166    4166    4722    4722    4722    4722 
       5833    5833    4722    4722    3333    5555    5777    5777 
       5972    5972    7361    7361    5277    5277    5833    5833 
       5833    5833    7500    7500    7500    7500   10444   10444 
       7916    7916    5833    5833    6388    6388    6388    6388 
       8055    8055    8055    8055   12777   12777    8111    8111 
       8750    8750    6666    6666    6666    6666    6666    6666 
       8888    8888    8888    8888    8888    8888    8888    6666 
       8750    8750    8750    8750    6111    6111    8333   11111 
       4722    5555   11111   15111   11111   15111   11111   15111 
      10555    9444    4722    8333    8333    8333    8333    8333 
      14444   12777    5555   11111   11111   11111   11111   11111 
       9444   12777    5555   10000   14444    5555   10000   14444 
       4722    4722    5277    5277    5277    5277    6666    6666 
      10000   10000   10000   10000   10555   10555   10555    7777 
       6666    6666    4500    4500    4500    4500    7777    7777)),
    % name checksum design-size (millipoints)
    list("cmmi10", -1725937524, 10000, list!-to!-vector '(
       6152    8333    7627    6944    7423    8312    7798    5833 
       6666    6122    7723    6397    5656    5177    4444    4059 
       4375    4965    4694    3539    5761    5833    6025    4939 
       4375    5700    5170    5714    4371    5402    5958    6256 
       6513    6224    4663    5914    8281    5170    3628    6541 
      10000   10000   10000   10000    2777    2777    5000    5000 
       5000    5000    5000    5000    5000    5000    5000    5000 
       5000    5000    2777    2777    7777    5000    7777    5000 
       5309    7500    7585    7147    8279    7381    6430    7862 
       8312    4395    5545    8493    6805    9701    8034    7627 
       6420    7905    7592    6131    5843    6827    5833    9444 
       8284    5805    6826    3888    3888    3888   10000   10000 
       4166    5285    4291    4327    5204    4656    4895    4769 
       5761    3445    4118    5206    2983    8780    6002    4847 
       5031    4464    4511    4687    3611    5724    4847    7159 
       5715    4902    4650    3224    3840    6364    5000    2777)),
    % name checksum design-size (millipoints)
    list("cmr10", 1274110073, 10000, list!-to!-vector '(
       6250    8333    7777    6944    6666    7500    7222    7777 
       7222    7777    7222    5833    5555    5555    8333    8333 
       2777    3055    5000    5000    5000    5000    5000    7500 
       4444    5000    7222    7777    5000    9027   10138    7777 
       2777    2777    5000    8333    5000    8333    7777    2777 
       3888    3888    5000    7777    2777    3333    2777    5000 
       5000    5000    5000    5000    5000    5000    5000    5000 
       5000    5000    2777    2777    2777    7777    4722    4722 
       7777    7500    7083    7222    7638    6805    6527    7847 
       7500    3611    5138    7777    6250    9166    7500    7777 
       6805    7777    7361    5555    7222    7500    7500   10277 
       7500    7500    6111    2777    5000    2777    5000    2777 
       2777    5000    5555    4444    5555    4444    3055    5000 
       5555    2777    3055    5277    2777    8333    5555    5000 
       5555    5277    3916    3944    3888    5555    5277    7222 
       5277    5277    4444    5000   10000    5000    5000    5000)),
    % name checksum design-size (millipoints)
    list("cmsy10", 555887770, 10000, list!-to!-vector '(
       7777    2777    7777    5000    7777    5000    7777    7777 
       7777    7777    7777    7777    7777   10000    5000    5000 
       7777    7777    7777    7777    7777    7777    7777    7777 
       7777    7777    7777    7777   10000   10000    7777    7777 
      10000   10000    5000    5000   10000   10000   10000    7777 
      10000   10000    6111    6111   10000   10000   10000    7777 
       2749   10000    6666    6666    8888    8888       0       0 
       5555    5555    6666    5000    7222    7222    7777    7777 
       6111    7984    6568    5265    7713    5277    7187    5948 
       8445    5445    6777    7619    6897   12009    8204    7961 
       6955    8166    8475    6055    5446    6258    6127    9877 
       7132    6683    7247    6666    6666    6666    6666    6666 
       6111    6111    4444    4444    4444    4444    5000    5000 
       3888    3888    2777    5000    5000    6111    5000    2777 
       8333    7500    8333    4166    6666    6666    7777    7777 
       4444    4444    4444    6111    7777    7777    7777    7777))
    );



symbolic procedure fancy!-prin2!*(u,n);
   if atom u and eqcar(explode2 u,'!\) then
      fancy!-line!* := u . fancy!-line!*
   else if numberp u and not testing!-width!* then fancy!-prin2number u
     else
  (begin scalar str,id; integer l;
    str := stringp u; id := idp u and not digit u; long!*:=nil;
     u:= if atom u then <<
             if !*fancy!-lower then explode2lc u
             else explode2 u >>
        else {u};
    if cdr u then long!*:=t;
    if car u = '!\ then long!*:=nil;
    l := if numberp n then n else 2*length u;
    if id and not numberp n then
       u:=fancy!-lower!-digits(fancy!-esc u);
    if long!* then
       %% fancy!-line!* := '!{ . '!m . '!r . '!h . '!t . '!a . '!m . '!\ . fancy!-line!*;
       fancy!-line!* := '!\mathrm!{ . fancy!-line!*;
    for each x in u do
    <<if str and (x='!    or x='!_)
         then fancy!-line!* := '!\ . fancy!-line!*;
      fancy!-line!* :=
        (if id and !*fancy!-lower
          then red!-char!-downcase x else x) . fancy!-line!*;
    >>;
    if long!* then fancy!-line!* := '!} . fancy!-line!*;
    fancy!-pos!* := fancy!-pos!* + l;
    if fancy!-pos!* > 2 * (linelength nil +1 ) then overflowed!*:=t;
  end) where !*lower = !*lower;

symbolic procedure fancy!-last!-symbol();
   if fancy!-line!* then car fancy!-line!*;

charassoc!* :=
         '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e) (!F . !f)
           (!G . !g) (!H . !h) (!I . !i) (!J . !j) (!K . !k) (!L . !l)
           (!M . !m) (!N . !n) (!O . !o) (!P . !p) (!Q . !q) (!R . !r)
           (!S . !s) (!T . !t) (!U . !u) (!V . !v) (!W . !w) (!X . !x)
           (!Y . !y) (!Z . !z));

symbolic procedure red!-char!-downcase u;
   (if x then cdr x else u) where x = atsoc(u,charassoc!*);

symbolic procedure fancy!-prin2number u;
  % we print a number eventually causing a line break
  % for very big numbers.
  if testing!-width!* then  fancy!-prin2!*(u,t) else
     fancy!-prin2number1 (if atom u then explode2 u else u);

symbolic procedure fancy!-prin2number1 u;
  begin integer c,ll;
   ll := 2 * (linelength nil +1 );
   while u do
   <<c:=c+1;
     if c>10 and fancy!-pos!* > ll then fancy!-terpri!*(t);
     fancy!-prin2!*(car u,2); u:=cdr u;
   >>;
  end;

symbolic procedure fancy!-esc u;
   if not('!_ memq u) then u else
   (if car u eq '!_ then '!\ . w else w)
      where w = car u . fancy!-esc cdr u;

symbolic procedure fancy!-lower!-digits u;
    (if null m then u else if m = 'all or
        fancy!-lower!-digitstrail(u,nil) then
           fancy!-lower!-digits1(u,nil)
     else u
     ) where m=fancy!-mode 'fancy_lower_digits;

symbolic procedure fancy!-lower!-digits1(u,s);
  begin scalar c,q,r,w,x;
 loop:
    if u then <<c:=car u; u:=cdr u>> else c:=nil;
    if null s then
      if not digit c and c then w:=c.w else
      << % need to close the symbol w;
         w:=reversip w;
         q:=intern compress w;
	 % The following test "explode q = w" is a hack to avoid the
	 % problem that in CSL compress '(a l p h a !\ !_) is just
	 % alpha. In PSL it is !_, which is not correct either but
	 % this does not cause problems here:
         if explode q = w and stringp (x:=get(q,'fancy!-special!-symbol))
            then w:=explode2 x;
         if cdr w then
            if car w = '!\ then long!*:=nil else long!*:=t
         else long!*:=nil;
         r:=nconc(r,w);
         if digit c then <<s:=t; w:={c}>> else w:=nil;
      >>
    else
      if digit c then w:=c.w else
      << % need to close the number w.
        w:='!_ . '!{ . reversip('!} . w);
        r:=nconc(r,w);
        if c then <<s:=nil; w:={c}>> else w:=nil;
      >>;
    if w then goto loop;
    return r;
  end;




symbolic procedure fancy!-lower!-digitstrail(u,s);
   if null u then s else
   if not s and digit car u then
          fancy!-lower!-digitstrail(cdr u,t) else
   if s and not digit car u then nil
   else fancy!-lower!-digitstrail(cdr u,s);

symbolic procedure fancy!-terpri!* u;
   <<
     if fancy!-line!* then
         fancy!-page!* := fancy!-line!* . fancy!-page!*;
     fancy!-pos!* :=tablevel!* * 10;
     fancy!-texpos := tablevel!* * 30000; % Roughtly 1 cm
     fancy!-line!*:= {'tab . tablevel!*};
     overflowed!* := nil
   >>;

symbolic macro procedure fancy!-level u;
 % unwind-protect for special output functions.
  {'prog,'(pos tpos fl w),
      '(setq pos fancy!-pos!*),
      '(setq tpos fancy!-texpos),
      '(setq fl fancy!-line!*),
      {'setq,'w,cadr u},
      '(cond ((eq w 'failed)
              (setq fancy!-line!* fl)
              (setq fancy!-texpos tpos)
              (setq fancy!-pos!* pos))),
       '(return w)};

symbolic procedure fancy!-begin();
  % collect current status of fancy output. Return as a list
  % for later recovery.
  {fancy!-pos!*,fancy!-line!*,fancy!-texpos};

symbolic procedure fancy!-end(r,s);
  % terminates a fancy print sequence. Eventually resets
  % the output status from status record <s> if the result <r>
  % signals an overflow.
  <<if r='failed then
     <<fancy!-line!*:=car s; fancy!-pos!*:=cadr s;fancy!-texpos:=caddr s>>;
     r>>;

symbolic procedure fancy!-mode u;
  begin scalar m;
     m:= lispeval u;
     if eqcar(m,'!*sq) then m:=reval m;
     return m;
  end;

%---------------- central formula converter --------------------

symbolic procedure fancy!-maprin0 u;
%%   if not overflowed!* then
   fancy!-maprint(u,0) where !*lower=nil;

symbolic procedure fancy!-maprint(l,p!*!*);
   % Print expression l at bracket level p!*!* without terminating
   % print line.  Special cases are handled by:
   %    pprifn: a print function that includes bracket level as 2nd arg.
   %     prifn: a print function with one argument.
  (begin scalar p,x,w,pos,tpos, fl;
        p := p!*!*;     % p!*!* needed for (expt a (quotient ...)) case.
        if null l then return nil;
        if atom l then return fancy!-maprint!-atom(l,p);
        pos := fancy!-pos!*; tpos := fancy!-texpos; fl := fancy!-line!*;

        if not atom car l then return fancy!-maprint(car l,p);

        l := fancy!-convert(l,nil);

        if (x:=get(car l,'fancy!-reform)) then
          return fancy!-maprint(apply1(x,l),p);
        if ((x := get(car l,'fancy!-pprifn)) and
                   not(apply2(x,l,p) eq 'failed))
          or ((x := get(car l,'fancy!-prifn)) and
                   not(apply1(x,l) eq 'failed))
          or (get(car l,'print!-format)
                 and fancy!-print!-format(l,p) neq 'failed)
          then return nil;

        if testing!-width!* and overflowed!*
           or w='failed then return fancy!-fail(pos,tpos,fl);

        % eventually convert expression to a different form
        % for printing.

        l := fancy!-convert(l,'infix);

        % printing operators with integer argument in index form.
        if flagp(car l,'print!-indexed) then
        << fancy!-prefix!-operator(car l);
           w :=fancy!-print!-indexlist cdr l
        >>

        else if x := get(car l,'infix) then
        << p := not(x>p);
          w:= if p then fancy!-in!-brackets(
            {'fancy!-inprint,mkquote car l,x,mkquote cdr l},
               '!(,'!))
              else
            fancy!-inprint(car l,x,cdr l);
        >>
        else if x:= get(car l,'fancy!-flatprifn) then
            w:=apply(x,{l})
        else
        <<
           w:=fancy!-prefix!-operator(car l);
           obrkp!* := nil;
           if w neq 'failed then
             w:=fancy!-print!-function!-arguments cdr l;
        >>;

        return if testing!-width!* and overflowed!*
              or w='failed then fancy!-fail(pos,tpos,fl) else nil;
    end ) where obrkp!*=obrkp!*;

symbolic procedure fancy!-convert(l,m);
  % special converters.
  if eqcar(l,'expt) and cadr l= 'e and
     ( m='infix or treesizep(l,20) )
        then {'exp,caddr l}
    else l;

symbolic procedure fancy!-print!-function!-arguments u;
  % u is a parameter list for a function.
    fancy!-in!-brackets(
       u and {'fancy!-inprint, mkquote '!*comma!*,0,mkquote u},
            '!(,'!));

symbolic procedure fancy!-maprint!-atom(l,p);
% This should be where any atomic entity provided by the user gets
% treated. The "ordinarily special" cases are
%   (a) Things like the names "alpha", "beta", "geq", "partial-df" and
%       a whole bunch more that have a fancy!-special!-symbol property
%       indicating that they stand for some special character.
%   (b) vectors, which get displayed as eg [1,2,3,4]
%   (c) negative numbers in cases where they should be rendered in
%       parentheses to avoid ambiguity in the output.
% In the original code here all other cases where merely delegated to
% fancy!-prin2!*.
%
% There are however some "less ordinary" special cases that arise when
% material from the user clashes with TeX. I am at present aware of
% five cases of oddity:
%   (1) Strings: If the user puts a string in the input it ought to end
%                up rendered literally come what may. At present it tends
%                to get transcrioned to the TeX stream unaltered, and if the
%                string has TeX special characters in it the result can be
%                odd!
%   (2) Names with special characters within. For instance "abc!%def" leads
%                to TeX that says "\mathrm{abc%def}" and the "%" there is
%                treated as a comment marker, leading to disaster.
%   (3) Names that alias a TeX directive. Eg "on revpri; (1+!\big)^3;". This
%                case can include explicit cases that could be held to
%                be deliberate such as !\alpha, but the fancy!-special!-symbol
%                scheme ought to make that unnecessary.
%   (4) Names (or strings) containing characters outside the LaTeX fonts that
%                are used by default. Mostly these will be special LaTeX
%                control characters, but e.g. if a user could get a "pounds
%                sterling" character into a name...
%   (5) All the follow-on joys that go beyond just (4) and correspond to
%       "Internationalisation"!
% I view all of these as illustrating the fact that interfacing between the
% core of Reduce and its front-end using a textual interface like this is
% unsatisfactory, even though it has been a good place-holder and a path of
% least resistance. The problems noted here only escalate if you imagine
% delevloping the graphical front-end to support cut and (particularly)
% paste operations where the same sorts of textual conversion would need to
% be done but consistently and in the other direction. It also makes the
% issue about who takes responsibility for line breaks a muddled one.
%
% Going via LaTeX is not automatically or comfortably 1:1, it loses structural
% information and it adds the inefficiency of the conversion done here which
% feeds instantly into a TeX parser that tries to reconstruct a box-structure
% that could be closely related to a Lisp prefix form.
%
% So in the long term I would really like to discard this and go directly
% from the Reduce internal form to a box-structure that can be used for
% layout and rendering.
%
% If an identifier contains one of the TeX special characters (other than
% underscore) I will just display it as in \mathrm{} context. Doing so will
% override any detection of trailing digits that could otherwise end up
% displayed as subscripts.
%
% I suspect that I really want to render strings in the cmtt fixed-pitch font,
% but at present I am not confident that Reduce always makes a careful enough
% distinction about what it provides as string and what as symbol data here.
 fancy!-level
  begin scalar x;
     if (x:=get(l,'fancy!-special!-symbol)) then
         fancy!-special!-symbol(x,
                get(l,'fancy!-special!-symbol!-size) or 2)
     else if vectorp l then <<
         fancy!-prin2!*("[",0);
         l:=for i:=0:upbv l collect getv(l,i);
         x:=fancy!-inprint(",",0,l);
         fancy!-prin2!*("]",0);
         return x >>
     else if stringp l or (idp l and contains!-tex!-special l) then <<
         fancy!-line!* := '!\mathrm!{ . fancy!-line!*;
         for each c in explodec l do fancy!-tex!-character c;
         fancy!-line!* := '!} . fancy!-line!* >>
     else if not numberp l or (not (l<0) or p<=get('minus,'infix))
         then fancy!-prin2!*(l,'index)
     else fancy!-in!-brackets({'fancy!-prin2!*,mkquote l,t}, '!(,'!));
     return (if testing!-width!* and overflowed!* then 'failed else nil);
  end;

symbolic procedure contains!-tex!-special x;
% Checks if an identifier contains any character that could "upset" TeX
% in its name. Note that as a special case I do NOT count underscore as
% special here!
  begin
    scalar u;
    u:= (if !*fancy!-lower then explode2lc x
         else explode2 x);
top:if null u then return nil
    else if memq(car u, '(!# !$ !% !& !{ !} !~ !^ !\)) or
            car u eq blank or car u eq tab or car u eq !$eol!$ then return t;
    u := cdr u;
    go to top
  end;


symbolic procedure fancy!-tex!-character c;
% This arranges to print something even if it is a funny character as
% far as TeX is concerned. I display a tab as two spaces, and a newline
% as $eol$ and rather hope that neither ever arises. I also need to check
% that my TeX parser can handle all these...
  if c = '!# or
     c = '!$ or
     c = '!% or
     c = '!& or
     c = '!_ or
     c = '!{ or
     c = '!} then fancy!-line!* := c . '!\ . fancy!-line!*
  else if c = '!~ then fancy!-line!* := '!{!\textasciitilde!} . fancy!-line!*
  else if c = '!^ then fancy!-line!* := '!{!\textasciicircum!} . fancy!-line!*
  else if c = '!\ then fancy!-line!* := '!{!\textbackslash!} . fancy!-line!*
  else if c = blank   then fancy!-line!* := '!~ . fancy!-line!*
  else if c = tab     then fancy!-line!* := '!~ . '!~ . fancy!-line!*
  else if c = !$eol!$ then fancy!-line!* := '!\!$eol!\!$ . fancy!-line!*
  else fancy!-line!* := c . fancy!-line!*;

put('print_indexed,'psopfn,'(lambda(u)(flag u 'print!-indexed)));

symbolic procedure fancy!-print!-indexlist l;
   fancy!-print!-indexlist1(l,'!_,nil);

symbolic procedure fancy!-print!-indexlist1(l,op,sep);
  % print index or exponent lists, with or without separator.
 fancy!-level
  begin scalar w,testing!-width!*,obrkp!*;
    testing!-width!* :=t;
    fancy!-prin2!*(op,0);
    fancy!-prin2!*('!{,0);
    w:=fancy!-inprint(sep or 'times,0,l);
    fancy!-prin2!*("}",0);
    return w;
  end;

symbolic procedure fancy!-print!-one!-index i;
 fancy!-level
  begin scalar w,testing!-width!*,obrkp!*;
    testing!-width!* :=t;
    fancy!-prin2!*('!_,0);
    fancy!-prin2!*('!{,0);
    w:=fancy!-inprint('times,0,{i});
    fancy!-prin2!*("}",0);
    return w;
  end;

symbolic procedure fancy!-in!-brackets(u,l,r);
  % put form into brackets (round, curly,...).
  % u: form to be evaluated,
  % l,r: left and right brackets to be inserted.
  fancy!-level
   (begin scalar fp,w,r1,r2,rec;
     rec := {0};
     fancy!-bstack!* := rec . fancy!-bstack!*;
     fancy!-adjust!-bkt!-levels fancy!-bstack!*;
     fp := length fancy!-page!*;
     fancy!-prin2!* (r1:='bkt.nil.l.rec, 2);
     w := eval u;
     fancy!-prin2!* (r2:='bkt.nil.r.rec, 2);
       % no line break: use \left( .. \right) pair.
     if fp = length fancy!-page!* then
     <<car cdr r1:= t; car cdr r2:= t>>;
     return w;
   end)
    where fancy!-bstack!* = fancy!-bstack!*;


symbolic procedure fancy!-adjust!-bkt!-levels u;
   if null u or null cdr u then nil
   else if caar u >= caadr u then
    <<car cadr u := car cadr u +1;
      fancy!-adjust!-bkt!-levels cdr u; >>;

symbolic procedure fancy!-exptpri(l,p);
% Prints expression in an exponent notation.
   (begin scalar !*list,pp,q,w,w1,w2,pos,tpos,fl;
      pos:=fancy!-pos!*; tpos:=fancy!-texpos; fl:=fancy!-line!*;
      w1 := cadr l; w2 := caddr l;
      pp := eqcar(w1, 'quotient) or
            eqcar(w1, 'expt) or
            (eqcar(w1, '!*hold) and not atom cadr w1);
      testing!-width!* := t;
      if eqcar(w2,'quotient) and cadr w2 = 1
          and (fixp caddr w2 or liter caddr w2) then
         return fancy!-sqrtpri!*(w1,caddr w2);
      if eqcar(w2,'quotient) and eqcar(cadr w2,'minus)
          then w2 := list('minus,list(car w2,cadadr w2,caddr w2))
          else w2 := negnumberchk w2;
      if pp then <<
          if fancy!-in!-brackets({'fancy!-maprint, mkquote w1,mkquote q},
                                 '!(, '!))='failed
            then return fancy!-fail(pos,tpos,fl) >>
      else if fancy!-maprint(w1,get('expt,'infix))='failed
            then return fancy!-fail(pos,tpos,fl);
     fancy!-prin2!*("^",0);
     if eqcar(w2,'quotient) and fixp cadr w2 and fixp caddr w2 then
      <<fancy!-prin2!*("{",0); w:=fancy!-inprint('!/,0,cdr w2);
                 fancy!-prin2!*("}",0)>>
           else w:=fancy!-maprint!-tex!-bkt(w2,0,nil);
     if w='failed then return fancy!-fail(pos,tpos,fl) ;
    end) where !*ratpri=!*ratpri,
           testing!-width!*=testing!-width!*;

put('expt,'fancy!-pprifn,'fancy!-exptpri);

symbolic procedure fancy!-inprint(op,p,l);
  (begin scalar x,y,w, pos,tpos,fl;
     pos:=fancy!-pos!*;
     tpos:= fancy!-texpos;
     fl:=fancy!-line!*;
      % print product of quotients using *.
     if op = 'times and eqcar(car l,'quotient) and
       cdr l and eqcar(cadr l,'quotient) then
        op:='!*;
     if op eq 'plus and !*revpri then l := reverse l;
     if not get(op,'alt) then
     <<
        if op eq 'not then
         << fancy!-oprin op;
            return  fancy!-maprint(car l,get('not,'infix));
         >>;
        if op eq 'setq and not atom (x := car reverse l)
             and idp car x and (y := getrtype x)
             and (y := get(get(y,'tag),'fancy!-setprifn))
            then return apply2(y,car l,x);
        if not atom car l and idp caar l
              and
           ((x := get(caar l,'fancy!-prifn))
                   or (x := get(caar l,'fancy!-pprifn)))
              and (get(x,op) eq 'inbrackets)
            % to avoid mix up of indices and exponents.
          then<<
               fancy!-in!-brackets(
                {'fancy!-maprint,mkquote car l,p}, '!(,'!));
              >>
           else if !*nosplit and not testing!-width!* then
                fancy!-prinfit(car l, p, nil)
           else w:=fancy!-maprint(car l, p);
          l := cdr l
      >>;
     if testing!-width!* and (overflowed!* or w='failed)
            then return fancy!-fail(pos,tpos,fl);
     if !*list and obrkp!* and memq(op,'(plus minus)) then
        <<sumlevel!*:=sumlevel!*+1;
          tablevel!* := tablevel!* + 1>>;
     if !*nosplit and not testing!-width!* then
          % main line:
         fancy!-inprint1(op,p,l)
     else w:=fancy!-inprint2(op,p,l);
     if testing!-width!* and w='failed then return fancy!-fail(pos,tpos,fl);
   end
   ) where tablevel!*=tablevel!*, sumlevel!*=sumlevel!*;


symbolic procedure fancy!-inprint1(op,p,l);
   % main line (top level) infix printing, allow line break;
  begin scalar lop;
   for each v in l do
   <<lop := op;
     if op='plus and eqcar(v,'minus) then
       <<lop := 'minus; v:= cadr v>>;
     if 'failed = fancy!-oprin lop then
      <<fancy!-terpri!* nil; fancy!-oprin lop>>;
     fancy!-prinfit(negnumberchk v, p, nil)
   >>;
  end;

symbolic procedure fancy!-inprint2(op,p,l);
   % second line
  begin scalar lop,w;
   for each v in l do
    if not testing!-width!* or w neq 'failed then
     <<lop:=op;
       if op='plus and eqcar(v,'minus) then
              <<lop := 'minus; v:= cadr v>>;
       fancy!-oprin lop;
       if w neq 'failed then w:=fancy!-maprint(negnumberchk v,p)
     >>;
   return w;
  end;

symbolic procedure fancy!-inprintlist(op,p,l);
   % inside algebraic list
fancy!-level
 begin scalar fst,w,v;
  loop:
   if null l then return w;
   v := car l; l:= cdr l;
   if fst then
       << fancy!-prin2!*("\,",1);
          w:=fancy!-oprin op;
          fancy!-prin2!*("\,",1);
       >>;
   if w eq 'failed  and testing!-width!* then return w;
   w:= if w eq 'failed then fancy!-prinfit(v,0,op)
                    else fancy!-prinfit(v,0,nil);
   if w eq 'failed  and testing!-width!* then return w;
   fst := t;
   goto loop;
  end;

put('times,'fancy!-prtch,"\*");

symbolic procedure fancy!-oprin op;
 fancy!-level
  begin scalar x;
    if (x:=get(op,'fancy!-prtch)) then fancy!-prin2!*(x,1)
      else
    if (x:=get(op,'fancy!-infix!-symbol))
           then fancy!-special!-symbol(x,get(op,'fancy!-symbol!-length)
                                            or 4)
      else
    if null(x:=get(op,'prtch)) then fancy!-prin2!*(op,t)
      else
    << if !*list and obrkp!* and op memq '(plus minus)
        and sumlevel!*=2
       then
        if testing!-width!* and not (!*acn and !*list) then return 'failed
            else fancy!-terpri!* t;
       fancy!-prin2!*(x,t);
    >>;
    if overflowed!* then return 'failed
   end;

put('alpha,'fancy!-special!-symbol,"\alpha");
put('beta,'fancy!-special!-symbol,"\beta");
put('gamma,'fancy!-special!-symbol,"\gamma");
put('delta,'fancy!-special!-symbol,"\delta");
put('epsilon,'fancy!-special!-symbol,"\varepsilon");
put('zeta,'fancy!-special!-symbol,"\zeta");
put('eta,'fancy!-special!-symbol,"\eta");
put('theta,'fancy!-special!-symbol,"\theta");
put('iota,'fancy!-special!-symbol,"\iota");
put('kappa,'fancy!-special!-symbol,"\varkappa");
put('lambda,'fancy!-special!-symbol,"\lambda");
put('mu,'fancy!-special!-symbol,"\mu");
put('nu,'fancy!-special!-symbol,"\nu");
put('xi,'fancy!-special!-symbol,"\xi");
put('pi,'fancy!-special!-symbol,"\pi");
put('rho,'fancy!-special!-symbol,"\rho");
put('sigma,'fancy!-special!-symbol,"\sigma");
put('tau,'fancy!-special!-symbol,"\tau");
put('upsilon,'fancy!-special!-symbol,"\upsilon");
put('phi,'fancy!-special!-symbol,"\phi");
put('chi,'fancy!-special!-symbol,"\chi");
put('psi,'fancy!-special!-symbol,"\psi");
put('omega,'fancy!-special!-symbol,"\omega");

!#if (memq 'csl lispsystem!*)

deflist('(
% Many of these are just the same glyphs as ordinary upper case letters,
% and so for compatibility with external viewers I map those ones onto
% letters with the "\mathit" qualifier to force the font.
     (!Alpha "\mathit{A}") (!Beta "\mathit{B}") (!Chi "\Chi ")
     (!Delta "\Delta ") (!Epsilon "\mathit{E}") (!Phi "\Phi ")
     (!Gamma "\Gamma ") (!Eta "\mathit{H}") (!Iota "\mathit{I}")
     (!vartheta "\vartheta") (!Kappa "\Kappa ") (!Lambda "\Lambda ")
     (!Mu "\mathit{M}") (!Nu "\mathit{N}") (!O "\mathit{O}")
     (!Pi "\Pi ") (!Theta "\Theta ") (!Rho "\mathit{R}")
     (!Sigma "\Sigma ") (!Tau "\Tau ") (!Upsilon "\Upsilon ")
     (!Omega "\Omega ") (!Xi "\Xi ") (!Psi "\Psi ")
     (!Zeta "\mathit{Z}") (!varphi "\varphi ")
        ),'fancy!-special!-symbol);

!#else

if 'a neq '!A then deflist('(
    (!Alpha 65) (!Beta 66) (!Chi 67) (!Delta 68)
    (!Epsilon 69)(!Phi 70) (!Gamma 71)(!Eta 72)
    (!Iota 73) (!vartheta 74)(!Kappa 75)(!Lambda 76)
    (!Mu 77)(!Nu 78)(!O 79)(!Pi 80)(!Theta 81)
    (!Rho 82)(!Sigma 83)(!Tau 84)(!Upsilon 85)
    (!Omega 87) (!Xi 88)(!Psi 89)(!Zeta 90)
    (!varphi 106)
       ),'fancy!-special!-symbol);

!#endif

put('infinity,'fancy!-special!-symbol,"\infty ");
put('partial!-df,'fancy!-special!-symbol,"\partial ");
%put('partial!-df,'fancy!-symbol!-length,8);
put('empty!-set,'fancy!-special!-symbol,"\emptyset ");
put('not,'fancy!-special!-symbol,"\neg ");
put('not,'fancy!-infix!-symbol,"\neg ");
put('leq,'fancy!-infix!-symbol,"\leq ");
put('geq,'fancy!-infix!-symbol,"\geq ");
put('neq,'fancy!-infix!-symbol,"\neq ");
put('intersection,'fancy!-infix!-symbol,"\cap ");
put('union,'fancy!-infix!-symbol,"\cup ");
put('member,'fancy!-infix!-symbol,"\in ");
put('and,'fancy!-infix!-symbol,"\wedge ");
put('or,'fancy!-infix!-symbol,"\vee ");
put('when,'fancy!-infix!-symbol,"|");
put('!*wcomma!*,'fancy!-infix!-symbol,",\,");
put('replaceby,'fancy!-infix!-symbol,"\Rightarrow ");
%put('replaceby,'fancy!-symbol!-length,8);
%put('gamma,'fancy!-functionsymbol,71);  % big Gamma
put('!~,'fancy!-functionsymbol,"\forall ");     % forall
%put('!~,'fancy!-symbol!-length,8);

% arbint, arbcomplex.
%put('arbcomplex,'fancy!-functionsymbol,227);
%put('arbint,'fancy!-functionsymbol,226);
%flag('(arbcomplex arbint),'print!-indexed);

% flag('(delta),'print!-indexed);         % Dirac delta symbol.
% David Hartley voted against..

% The following definitions allow for more natural printing of
% conditional expressions within rule lists.

symbolic procedure fancy!-condpri0 u;
   fancy!-condpri(u,0);

symbolic procedure fancy!-condpri(u,p);
 fancy!-level
  begin scalar w;
    if p>0 then fancy!-prin2 "\left(";
    while (u := cdr u) and w neq 'failed do
      <<if not(caar u eq 't)
            then <<fancy!-prin2 'if; fancy!-prin2 " ";
                   w:=fancy!-maprin0 caar u;
                   fancy!-prin2 "\,"; fancy!-prin2 'then;
                   fancy!-prin2 "\,">>;
          if w neq 'failed then w := fancy!-maprin0 cadar u;
          if cdr u then <<fancy!-prin2 "\,";
                       fancy!-prin2 'else; fancy!-prin2 "\,">>>>;
     if p>0 then fancy!-prin2 "\right)";
     if overflowed!* or w='failed then return 'failed;
   end;

put('cond,'fancy!-pprifn,'fancy!-condpri);
put('cond,'fancy!-flatprifn,'fancy!-condpri0);

symbolic procedure fancy!-revalpri u;
   fancy!-maprin0 fancy!-unquote cadr u;

symbolic procedure fancy!-unquote u;
  if eqcar(u,'list) then for each x in cdr u collect
      fancy!-unquote x
  else if eqcar(u,'quote) then cadr u else u;

put('aeval,'fancy!-prifn,'fancy!-revalpri);
put('aeval!*,'fancy!-prifn,'fancy!-revalpri);
put('reval,'fancy!-prifn,'fancy!-revalpri);
put('reval!*,'fancy!-prifn,'fancy!-revalpri);

put('aminusp!:,'fancy!-prifn,'fancy!-patpri);
put('aminusp!:,'fancy!-pat,'(lessp !&1 0));

symbolic procedure fancy!-holdpri u;
   if atom cadr u then fancy!-maprin0 cadr u
   else fancy!-in!-brackets({'fancy!-maprin0, mkquote cadr u}, '!(, '!));

put('!*hold, 'fancy!-prifn, 'fancy!-holdpri);

symbolic procedure fancy!-patpri u;
  begin scalar p;
    p:=subst(fancy!-unquote  cadr u,'!&1,
             get(car u,'fancy!-pat));
    return fancy!-maprin0 p;
  end;

symbolic procedure fancy!-boolvalpri u;
   fancy!-maprin0 cadr u;

put('boolvalue!*,'fancy!-prifn,'fancy!-boolvalpri);

symbolic procedure fancy!-quotpri u;
   begin scalar n1,n2,n1t,n2t,fl,w,pos,tpos,testing!-width!*;
     if overflowed!* or (!*acn and !*list) then return 'failed;
     testing!-width!*:=t;
     pos:=fancy!-pos!*;
     tpos:=fancy!-texpos;
     fl:=fancy!-line!*; 
     fancy!-prin2!*("\frac",0);
     w:=fancy!-maprint!-tex!-bkt(cadr u,0,t);
     n1 := fancy!-pos!*;
     n1t := fancy!-texpos;
     if w='failed
       then return fancy!-fail(pos,tpos,fl);
     fancy!-pos!* := pos;
     fancy!-texpos := tpos;
     w := fancy!-maprint!-tex!-bkt(caddr u,0,nil);
     n2 := fancy!-pos!*;
     n2t := fancy!-texpos;
     if w='failed
       then return fancy!-fail(pos,tpos,fl);
     fancy!-pos!* := max(n1,n2);
     fancy!-texpos := max(n1t,n2t);
     return t;
  end;

symbolic procedure fancy!-maprint!-tex!-bkt(u,p,m);
  % Produce expression with tex brackets {...} if
  % necessary. Ensure that {} unit is in same formula.
  % If m=t brackets will be inserted in any case.
  begin scalar w,pos,tpos,fl,testing!-width!*;
    testing!-width!*:=t;
    pos:=fancy!-pos!*;
    tpos:=fancy!-texpos;
    fl:=fancy!-line!*;
   if not m and (numberp u and 0<=u and u <=9 or liter u) then
   << fancy!-prin2!*(u,t);
      return if overflowed!* then fancy!-fail(pos,tpos,fl);
   >>;
   fancy!-prin2!*("{",0);
   w := fancy!-maprint(u,p);
   fancy!-prin2!*("}",0);
   if w='failed then return fancy!-fail(pos,tpos,fl);
  end;

symbolic procedure fancy!-fail(pos,tpos,fl);
 <<
     overflowed!* := nil;
     fancy!-pos!* := pos;
     fancy!-texpos := tpos;
     fancy!-line!* := fl;
     'failed
 >>;

put('quotient,'fancy!-prifn,'fancy!-quotpri);

symbolic procedure fancy!-prinfit(u, p, op);
% Display u (as with maprint) with op in front of it, but starting
% a new line before it if there would be overflow otherwise.
   begin scalar pos,tpos,fl,w,ll,f;
     if pairp u and (f:=get(car u,'fancy!-prinfit)) then
        return apply(f,{u,p,op});
     pos:=fancy!-pos!*;
     tpos:=fancy!-texpos;
     fl:=fancy!-line!*;
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       if w neq 'failed then w := fancy!-maprint(u,p);
     end;
     if w neq 'failed then return t;
     fancy!-line!*:=fl; fancy!-pos!*:=pos; fancy!-texpos:=tpos;
     if testing!-width!* and w eq 'failed then return w;

     if op='plus and eqcar(u,'minus) then <<op := 'minus; u:=cadr u>>;
     w:=if op then fancy!-oprin op;
       % if the operator causes the overflow, we break the line now.
     if w eq 'failed then
     <<fancy!-terpri!* nil;
       if op then fancy!-oprin op;
       return fancy!-maprint(u, p);>>;
       % if at least half the line is still free and the
       % object causing the overflow has been a number,
       % let it break.
     if fancy!-pos!* < (ll:=linelength(nil)) then
             if numberp u then return fancy!-prin2number u else
         if eqcar(u,'!:rd!:) then return fancy!-rdprin u;
       % generate a line break if we are not just behind an
       % opening bracket at the beginning of a line.
     if fancy!-pos!* > linelength nil / 2 or
          not eqcar(fancy!-last!-symbol(),'bkt) then
           fancy!-terpri!* nil;
     return fancy!-maprint(u, p);
   end;

%-----------------------------------------------------------
%
%   support for print format property
%
%-----------------------------------------------------------

symbolic procedure print_format(f,pat);
  % Assign a print pattern p to the operator form f.
put(car f, 'print!-format, (cdr f . pat) . get(car f, 'print!-format));

symbolic operator print_format;

symbolic procedure fancy!-print!-format(u,p);
 fancy!-level
  begin scalar fmt,fmtl,a;
   fmtl:=get(car u,'print!-format);
 l:
   if null fmtl then return 'failed;
   fmt := car fmtl; fmtl := cdr fmtl;
   if length(car fmt) neq length cdr u then goto l;
   a:=pair(car fmt,cdr u);
   return fancy!-print!-format1(cdr fmt,p,a);
  end;

symbolic procedure fancy!-print!-format1(u,p,a);
  begin scalar w,x,y,pl,bkt,obkt,q;
   if eqcar(u,'list) then u:= cdr u;
   while u and w neq 'failed do
   <<x:=car u; u:=cdr u;
     if eqcar(x,'list) then x:=cdr x;
     obkt := bkt; bkt:=nil;
     if obkt then fancy!-prin2!*('!{,0);
     w:=if pairp x then fancy!-print!-format1(x,p,a) else
        if memq(x,'(!( !) !, !. !|)) then
         <<if x eq '!( then <<pl:=p.pl; p:=0>> else
           if x eq '!) then <<p:=car pl; pl:=cdr pl>>;
           fancy!-prin2!*(x,1)>> else
        if x eq '!_ or x eq '!^ then <<bkt:=t;fancy!-prin2!*(x,0)>> else
        if q:=assoc(x,a) then fancy!-maprint(cdr q,p) else
        fancy!-maprint(x,p);
     if obkt then fancy!-prin2!*('!},0);
    >>;
    return w;
  end;


%-----------------------------------------------------------
%
%   some operator specific print functions
%
%-----------------------------------------------------------

symbolic procedure fancy!-prefix!-operator(u);
 % Print as function, but with a special character.
   begin scalar sy;
     sy :=
       get(u,'fancy!-functionsymbol) or get(u,'fancy!-special!-symbol);
     if sy
      then fancy!-special!-symbol(sy,get(u,'fancy!-symbol!-length) or 2)
      else fancy!-prin2!*(u,t);
   end;

put('sqrt,'fancy!-prifn,'fancy!-sqrtpri);

symbolic procedure fancy!-sqrtpri(u);
    fancy!-sqrtpri!*(cadr u,2);

symbolic procedure fancy!-sqrtpri!*(u,n);
  fancy!-level
   begin
     if not numberp n and not liter n then return 'failed;
     fancy!-prin2!*("\sqrt",0);
     if n neq 2 then
     <<fancy!-prin2!*("[",0);
       fancy!-prin2!*("\,",1);
       fancy!-prin2!*(n,t);
       fancy!-prin2!*("]",0);
     >>;
     return fancy!-maprint!-tex!-bkt(u,0,t);
   end;


symbolic procedure fancy!-sub(l,p);
% Prints expression in an exponent notation.
  if get('expt,'infix)<=p then
      fancy!-in!-brackets({'fancy!-sub,mkquote l,0},'!(,'!))
    else
   fancy!-level
    begin scalar eqs,w;
      l:=cdr l;
      while cdr l do <<eqs:=append(eqs,{car l}); l:=cdr l>>;
      l:=car l;
      testing!-width!* := t;
      w := fancy!-maprint(l,get('expt,'infix));
      if w='failed then return w;
%      fancy!-prin2!*("\bigl",0);
      fancy!-prin2!*("|",1);
      fancy!-prin2!*('!_,0);
      fancy!-prin2!*("{",0);
      w:=fancy!-inprint('!*comma!*,0,eqs);
      fancy!-prin2!*("}",0);
      return w;
   end;

put('sub,'fancy!-pprifn,'fancy!-sub);


put('factorial,'fancy!-pprifn,'fancy!-factorial);

symbolic procedure fancy!-factorial(u,n);
  fancy!-level
   begin scalar w;
     w := (if atom cadr u then fancy!-maprint(cadr u,9999)
              else
           fancy!-in!-brackets({'fancy!-maprint,mkquote cadr u,0},
                               '!(,'!))
          );
     fancy!-prin2!*("!",2);
     return w;
   end;

put('binomial,'fancy!-prifn,'fancy!-binomial);

symbolic procedure fancy!-binomial u;
  fancy!-level
   begin scalar w1,w2;
     fancy!-prin2!*("\left(\begin{matrix}",2);
     w1 := fancy!-maprint(cadr u,0);
     fancy!-prin2!*("\\",0);
     w2 := fancy!-maprint(caddr u,0);
     fancy!-prin2!*("\end{matrix}\right)",2);
     if w1='failed or w2='failed then return 'failed;
   end;

symbolic procedure fancy!-intpri(u,p);
% Fancy integral print.
   if p>get('times,'infix) then
      fancy!-in!-brackets({'fancy!-intpri,mkquote u,0},'!(,'!))
   else
      fancy!-level
         begin scalar w0,w1,w2,hi,lo;
            if cdddr u then lo:=cadddr u;
            if lo and cddddr u then hi := car cddddr u;
            if fancy!-height(cadr u,1.0) > 3 then
               fancy!-prin2!*("\int ",0) % big integral wanted
            else
               fancy!-prin2!*("\int ",0);
            if lo then <<
               fancy!-prin2!*('!_,0);
               fancy!-prin2!*('!{,0);
               w0 := fancy!-maprint(lo,0);
               fancy!-prin2!*('!},0);
            >>;
            if hi then <<
               fancy!-prin2!*('!^,0);
               fancy!-maprint!-tex!-bkt(hi,0,nil);
            >>;
            w1:=fancy!-maprint(cadr u,0);
            fancy!-prin2!*("\,d\,",2);
            w2:=fancy!-maprint(caddr u,0);
            if w1='failed or w2='failed or w0='failed then return 'failed;
         end;

symbolic procedure fancy!-height(u,h);
  % Fancy height. Estimate the height of an expression, this is a
  % subroutine of fancy!-intpri.
    if atom u then h
    else if car u = 'minus then fancy!-height(cadr u,h)
    else if car u = 'plus or car u = 'times then
      eval('max. for each w in cdr u collect fancy!-height(w,h))
    else if car u = 'expt then
         fancy!-height(cadr u,h) + fancy!-height(caddr u,h*0.8)
    else if car u = 'quotient then
         fancy!-height(cadr u,h) + fancy!-height(caddr u,h)
    else if get(car u,'simpfn) then fancy!-height(cadr u,h)
    else h;

put('int,'fancy!-pprifn,'fancy!-intpri);

symbolic procedure fancy!-sumpri!*(u,p,mode);
  if p>get('minus,'infix) then
    fancy!-in!-brackets({'fancy!-sumpri!*,mkquote u,0,mkquote mode},
                         '!(,'!))
   else
  fancy!-level
   begin scalar w,w0,w1,lo,hi,var;
     var := caddr u;
     if cdddr u then lo:=cadddr u;
     if lo and cddddr u then hi := car cddddr u;
     w:=if lo then {'equal,var,lo} else var;
     if mode = 'sum then
        fancy!-prin2!*("\sum",0) % big SIGMA
     else if mode = 'prod then
        fancy!-prin2!*("\prod",0); % big PI
     fancy!-prin2!*('!_,0);
     fancy!-prin2!*('!{,0);
     if w then w0:=fancy!-maprint(w,0);
     fancy!-prin2!*('!},0);
     if hi then <<fancy!-prin2!*('!^,0);
                  fancy!-maprint!-tex!-bkt(hi,0,nil);
                 >>;
     fancy!-prin2!*('!\!, ,1);
     w1:=fancy!-maprint(cadr u,0);
     if w0='failed or w1='failed then return 'failed;
   end;

symbolic procedure fancy!-sumpri(u,p); fancy!-sumpri!*(u,p,'sum);

put('sum,'fancy!-pprifn,'fancy!-sumpri);
put('infsum,'fancy!-pprifn,'fancy!-sumpri);

symbolic procedure fancy!-prodpri(u,p); fancy!-sumpri!*(u,p,'prod);

put('prod,'fancy!-pprifn,'fancy!-prodpri);

symbolic procedure fancy!-limpri(u,p);
  if p>get('minus,'infix) then
    fancy!-in!-brackets({'fancy!-sumpri,mkquote u,0},'!(,'!))
   else
  fancy!-level
   begin scalar w,lo,var;
     var := caddr u;
     if cdddr u then lo:=cadddr u;
     fancy!-prin2!*("\lim",6);
     fancy!-prin2!*('!_,0);
     fancy!-prin2!*('!{,0);
     fancy!-maprint(var,0);
     fancy!-prin2!*("\rightarrow",0);
     fancy!-maprint(lo,0);
     fancy!-prin2!*('!},0);
     w:=fancy!-maprint(cadr u,0);
     return w;
   end;

put('limit,'fancy!-pprifn,'fancy!-limpri);

symbolic procedure fancy!-listpri(u);
 fancy!-level
 (if null cdr u then fancy!-maprint('empty!-set,0)
   else
  fancy!-in!-brackets(
   {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote cdr u},
               '!{,'!})
  );

put('list,'fancy!-prifn,'fancy!-listpri);
put('list,'fancy!-flatprifn,'fancy!-listpri);

put('!*sq,'fancy!-reform,'fancy!-sqreform);

symbolic procedure fancy!-sqreform u;
    prepsq!* sqhorner!* cadr u;

put('df,'fancy!-pprifn,'fancy!-dfpri);

% 9-Dec-93: 'total repaired

symbolic procedure fancy!-dfpri(u,l);
  (if flagp(cadr u,'print!-indexed) or
      pairp cadr u and flagp(caadr u,'print!-indexed)
    then fancy!-dfpriindexed(u,l)
   else if m = 'partial then fancy!-dfpri0(u,l,'partial!-df)
   else if m = 'total then fancy!-dfpri0(u,l,'!d)
   else if m = 'indexed then fancy!-dfpriindexed(u,l)
   else rederr "unknown print mode for DF")
        where m=fancy!-mode('fancy_print_df);

symbolic procedure fancy!-partialdfpri(u,l);
     fancy!-dfpri0(u,l,'partial!-df);

symbolic procedure fancy!-dfpri0(u,l,symb);
 if null cddr u then fancy!-maprin0{'times,symb,cadr u} else
 if l >= get('expt,'infix) then % brackets if exponented
  fancy!-in!-brackets({'fancy!-dfpri0,mkquote u,0,mkquote symb},
                      '!(,'!))
   else
 fancy!-level
  begin scalar x,d,q; integer n,m;
    u:=cdr u;
    q:=car u;
    u:=cdr u;
    while u do
    <<x:=car u; u:=cdr u;
      if u and numberp car u then
      <<m:=car u; u := cdr u>> else m:=1;
      n:=n+m;
      d:= append(d,{symb,if m=1 then x else {'expt,x,m}});
    >>;
    return fancy!-maprin0
    {'quotient, {'times,if n=1 then symb else
                                    {'expt,symb,n},q},
       'times. d};
  end;

symbolic procedure fancy!-dfpriindexed(u,l);
   if null cddr u then fancy!-maprin0{'times,'partial!-df,cadr u} else
   begin scalar w;
      w:=fancy!-maprin0 cadr u;
      if testing!-width!* and w='failed then return w;
      w :=fancy!-print!-indexlist fancy!-dfpriindexedx(cddr u,nil);
      return w;
   end;

symbolic procedure fancy!-dfpriindexedx(u,p);
  if null u then nil else
  if numberp car u then
   append(for i:=2:car u collect p,fancy!-dfpriindexedx(cdr u,p))
     else
  car u . fancy!-dfpriindexedx(cdr u,car u);

put('!:rd!:,'fancy!-prifn,'fancy!-rdprin);
put('!:rd!:,'fancy!-flatprifn,'fancy!-rdprin);

symbolic procedure fancy!-rdprin u;
 fancy!-level
  begin scalar digits; integer dotpos,xp;
   u:=rd!:explode u;
   digits := car u; xp := cadr u; dotpos := caddr u;
   return fancy!-rdprin1(digits,xp,dotpos);
  end;

symbolic procedure fancy!-rdprin1(digits,xp,dotpos);
  begin scalar str;
   if xp>0 and dotpos+xp<length digits-1 then
      <<dotpos := dotpos+xp; xp:=0>>;
    % build character string from number.
   for i:=1:dotpos do
   <<str := car digits . str;
     digits := cdr digits; if null digits then digits:='(!0);
   >>;
   str := '!. . str;
   for each c in digits do str :=c.str;
   if not(xp=0) then
   <<str:='!e.str;
     for each c in explode2 xp do str:=c.str>>;
   if testing!-width!* and
      fancy!-pos!* + 2*length str > 2 * linelength nil then
        return 'failed;
   fancy!-prin2number1 reversip str;
  end;

put('!:cr!:,'fancy!-pprifn,'fancy!-cmpxprin);
put('!:cr!:,'fancy!-pprifn,'fancy!-cmpxprin);

symbolic procedure fancy!-cmpxprin(u,l);
   begin scalar rp,ip;
     rp:=reval {'repart,u}; ip:=reval {'impart,u};
     return fancy!-maprint(
       if ip=0 then rp else
       if rp=0 then {'times,ip,'!i} else
        {'plus,rp,{'times,ip,'!i}},l);
   end;

symbolic procedure fancy!-dn!:prin u;
 begin scalar lst; integer dotpos,ex;
  lst := bfexplode0x (cadr u, cddr u);
  ex := cadr lst;
  dotpos := caddr lst;
  lst := car lst;
  return fancy!-rdprin1 (lst,ex,dotpos)
 end;

put ('!:dn!:, 'fancy!-prifn, 'fancy!-dn!:prin);

fmp!-switch t;

endmodule;


%-------------------------------------------------------

module f;   % Matrix printing routines.


fluid '(!*nat);

fluid '(obrkp!*);

symbolic procedure fancy!-setmatpri(u,v);
   fancy!-matpri1(cdr v,u);

put('mat,'fancy!-setprifn,'fancy!-setmatpri);

symbolic procedure fancy!-matpri u;
   fancy!-matpri1(cdr u,nil);


put('mat,'fancy!-prifn,'fancy!-matpri);

symbolic procedure fancy!-matpri1(u,x);
   % Prints a matrix canonical form U with name X.
   % Tries to do fancy display if nat flag is on.
  begin scalar w;
     w := fancy!-matpri2(u,x,nil);
     if w neq 'failed or testing!-width!* then return w;
     fancy!-matpri3(u,x);
  end;

symbolic procedure fancy!-matpri2(u,x,bkt);
  % Tries to print matrix as compact block.
  fancy!-level
    begin scalar w,testing!-width!*,fl,fp,fmat,row,elt,fail;
      integer cols,rows,rw,maxpos;
      testing!-width!*:=t;
      rows := length u;
      cols := length car u;
      if cols*rows>400 then return 'failed;

      if x then
      << fancy!-maprint(x,0); fancy!-prin2!*(":=",4) >>;
      fl := fancy!-line!*; fp := fancy!-pos!*;
         %  remaining room for the columns.
      rw := linelength(nil)-2 -(fancy!-pos!*+2);
      rw := rw/cols;
      fmat := for each row in u collect
        for each elt in row collect
          if not fail then
          <<fancy!-line!*:=nil; fancy!-pos!*:=0;
            w:=fancy!-maprint(elt,0);
            if fancy!-pos!*>maxpos then maxpos:=fancy!-pos!*;
            if w='failed or fancy!-pos!*>rw
              then fail:=t else
               (fancy!-line!*.fancy!-pos!*)
          >>;
     if fail then return 'failed;
     testing!-width!* := nil;
       % restore output line.
     fancy!-pos!* := fp; fancy!-line!* := fl;
       % TEX header
     fancy!-prin2!*(bldmsg("\left%w\begin{matrix}",
                        if bkt then car bkt else "("),0);
       % join elements.
     while fmat do
     <<row := car fmat; fmat:=cdr fmat;
       while row do
       <<elt:=car row; row:=cdr row;
         fancy!-line!* := append(car elt,fancy!-line!*);
         if row then fancy!-line!* :='!& . fancy!-line!*
          else if fmat then
             fancy!-line!* := "\\". fancy!-line!*;
       >>;
     >>;
     fancy!-prin2!*(bldmsg("\end{matrix}\right%w",
                        if bkt then cdr bkt else ")"),0);
      % compute total horizontal extent of matrix
     fancy!-pos!* := fp + maxpos*(cols+1);
    return t;
    end;


symbolic procedure fancy!-matpri3(u,x);
  if null x then fancy!-matpriflat('mat.u) else
   begin scalar obrkp!*,!*list;
      integer r,c;
      obrkp!* := nil;
      if null x then x:='mat;
      fancy!-terpri!*;
      for each row in u do
      <<r:=r+1; c:=0;
        for each elt in row do
        << c:=c+1;
           if not !*nero then
           << fancy!-prin2!*(x,t);
              fancy!-print!-indexlist {r,c};
              fancy!-prin2!*(":=",t);
              fancy!-maprint(elt,0);
              fancy!-terpri!* t;
           >>;
        >>;
      >>;
   end;

symbolic procedure fancy!-matpriflat(u);
 begin
  fancy!-oprin 'mat;
  fancy!-in!-brackets(
   {'fancy!-matpriflat1,mkquote '!*wcomma!*,0,mkquote cdr u},
               '!(,'!));
 end;

symbolic procedure fancy!-matpriflat1(op,p,l);
   % inside algebraic list
 begin scalar fst,w;
   for each v in l do
     <<if fst then
       << fancy!-prin2!*("\,",1);
          fancy!-oprin op;
          fancy!-prin2!*("\,",1);
       >>;
  % if the next row does not fit on the current print line
  % we move it completely to a new line.
       if fst then
        w:= fancy!-level
         fancy!-in!-brackets(
          {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote v},
            '!(,'!)) where testing!-width!*=t;
       if w eq 'failed then fancy!-terpri!* t;
       if not fst or w eq 'failed then
         fancy!-in!-brackets(
          {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote v},
            '!(,'!));
       fst := t;
     >>;
  end;

put('mat,'fancy!-flatprifn,'fancy!-matpriflat);

symbolic procedure fancy!-matfit(u,p,op);
% Prinfit routine for matrix.
% a new line before it if there would be overflow otherwise.
 fancy!-level
   begin scalar pos,tpos,fl,fp,w,ll;
     pos:=fancy!-pos!*;
     tpos:=fancy!-texpos;
     fl:=fancy!-line!*;
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       if w neq 'failed then w := fancy!-matpri(u);
     end;
     if w neq 'failed or
       (w eq 'failed and testing!-width!*) then return w;
     fancy!-line!*:=fl; fancy!-pos!*:=pos; fancy!-texpos:=tpos; w:=nil;
     fp := fancy!-page!*;
% matrix: give us a second chance with a fresh line
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       fancy!-terpri!* nil;
       if w neq 'failed then w := fancy!-matpri u;
     end;
     if w neq 'failed then return t;
     fancy!-line!*:=fl; fancy!-pos!*:=pos; fancy!-texpos:=tpos; fancy!-page!*:=fp;

     ll:=linelength nil;
     if op then fancy!-oprin op;
     if atom u or fancy!-pos!* > ll / 2 then fancy!-terpri!* nil;
     return fancy!-matpriflat(u);
   end;

put('mat,'fancy!-prinfit,'fancy!-matfit);

put('taylor!*,'fancy!-reform,'Taylor!*print1);

endmodule;

module fancy_specfn;

put('sin,'fancy!-prifn,'fancy!-sin);
put('cos,'fancy!-prifn,'fancy!-cos);
put('tan,'fancy!-prifn,'fancy!-tan);
put('cot,'fancy!-prifn,'fancy!-cot);
put('sec,'fancy!-prifn,'fancy!-sec);
put('csc,'fancy!-prifn,'fancy!-csc);
put('asin,'fancy!-prifn,'fancy!-asin);
put('acos,'fancy!-prifn,'fancy!-acos);
put('atan,'fancy!-prifn,'fancy!-atan);
put('sinh,'fancy!-prifn,'fancy!-sinh);
put('cosh,'fancy!-prifn,'fancy!-cosh);
put('tanh,'fancy!-prifn,'fancy!-tanh);
put('coth,'fancy!-prifn,'fancy!-coth);
put('exp,'fancy!-prifn,'fancy!-exp);
put('log,'fancy!-prifn,'fancy!-log);
put('ln,'fancy!-prifn,'fancy!-ln);
put('max,'fancy!-prifn,'fancy!-max);
put('min,'fancy!-prifn,'fancy!-min);
%put('repart,'fancy!-prifn,'fancy!-repart);
%put('impart,'fancy!-prifn,'fancy!-impart);

symbolic procedure fancy!-sin(u);
 fancy!-level
  begin
   fancy!-prin2!*("\sin",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-cos(u);
 fancy!-level
  begin
   fancy!-prin2!*("\cos",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-tan(u);
 fancy!-level
  begin
   fancy!-prin2!*("\tan",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-cot(u);
 fancy!-level
  begin
   fancy!-prin2!*("\cot",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-sec(u);
 fancy!-level
  begin
   fancy!-prin2!*("\sec",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-csc(u);
 fancy!-level
  begin
   fancy!-prin2!*("\csc",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-asin(u);
 fancy!-level
  begin
   fancy!-prin2!*("\arcsin",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-acos(u);
 fancy!-level
  begin
   fancy!-prin2!*("\arccos",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-atan(u);
 fancy!-level
  begin
   fancy!-prin2!*("\arctan",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-sinh(u);
 fancy!-level
  begin
   fancy!-prin2!*("\sinh",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-cosh(u);
 fancy!-level
  begin
   fancy!-prin2!*("\cosh",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-tanh(u);
 fancy!-level
  begin
   fancy!-prin2!*("\tanh",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-coth(u);
 fancy!-level
  begin
   fancy!-prin2!*("\coth",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-exp(u);
 fancy!-level
  begin
   fancy!-prin2!*("\exp",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-log(u);
 fancy!-level
  begin
   fancy!-prin2!*("\log",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-ln(u);
 fancy!-level
  begin
   fancy!-prin2!*("\ln",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-max(u);
 fancy!-level
  begin
   fancy!-prin2!*("\max",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-min(u);
 fancy!-level
  begin
   fancy!-prin2!*("\min",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-repart(u);
 fancy!-level
  begin
   fancy!-prin2!*("\Re",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

symbolic procedure fancy!-impart(u);
 fancy!-level
  begin
   fancy!-prin2!*("\Im",0);
   return fancy!-print!-function!-arguments cdr u;
  end;

put('besseli,'fancy!-prifn,'fancy!-bessel);
put('besselj,'fancy!-prifn,'fancy!-bessel);
put('bessely,'fancy!-prifn,'fancy!-bessel);
put('besselk,'fancy!-prifn,'fancy!-bessel);
put('besseli,'fancy!-functionsymbol,'(ascii 73));
put('besselj,'fancy!-functionsymbol,'(ascii 74));
put('bessely,'fancy!-functionsymbol,'(ascii 89));
put('besselk,'fancy!-functionsymbol,'(ascii 75));

symbolic procedure fancy!-bessel(u);
 fancy!-level
  begin scalar w;
   fancy!-prefix!-operator car u;
   w:=fancy!-print!-one!-index cadr u;
   if testing!-width!* and w eq 'failed then return w;
   return fancy!-print!-function!-arguments cddr u;
  end;

% Hypergeometric functions.

put('empty!*,'fancy!-special!-symbol,32);  % no longer used?

put('hypergeometric,'fancy!-prifn,'fancy!-hypergeometric);

symbolic procedure fancy!-hypergeometric u;
 fancy!-level
  begin scalar w,a1,a2,a3;
   a1 :=cdr cadr u;
   a2 := cdr caddr u;
   a3 := cadddr u;
   %fancy!-special!-symbol(get('empty!*,'fancy!-special!-symbol),nil);
   fancy!-prin2!*("{}",0);
   w:=fancy!-print!-one!-index length a1;
   if testing!-width!* and w eq 'failed then return w;
   fancy!-prin2!*("F",nil);
   w:=fancy!-print!-one!-index length a2;
   if testing!-width!* and w eq 'failed then return w;
   fancy!-prin2!*("\left(\left.",nil);
   w := w eq 'failed or fancy!-print!-indexlist1(a1,'!^,'!*comma!*);
   w := w eq 'failed or fancy!-print!-indexlist1(a2,'!_,'!*comma!*);
   fancy!-prin2!*("\,",1);
   %w := w eq 'failed or fancy!-special!-symbol(124,1);    % vertical bar
   fancy!-prin2!*("\right|\,",1);
   w := w eq 'failed or fancy!-prinfit(a3,0,nil);
   fancy!-prin2!*("\right)",nil);
   return w;
  end;

% hypergeometric({1,2,u/w,v},{5,6},sqrt x);

put('meijerg,'fancy!-prifn,'fancy!-meijerG);

symbolic procedure fancy!-meijerG u;
 fancy!-level
  begin scalar w,a1,a2,a3;
   integer n,m,p,q;
   a1 :=cdr cadr u;
   a2 := cdr caddr u;
   a3 := cadddr u;
   m:=length cdar a2;
   n:=length cdar a1;
   a1 := append(cdar a1 , cdr a1);
   a2 := append(cdar a2 , cdr a2);
   p:=length a1; q:=length a2;
   fancy!-prin2!*("G",nil);
   w := w eq 'failed or
        fancy!-print!-indexlist1({m,n},'!^,nil);
   w := w eq 'failed or
        fancy!-print!-indexlist1({p,q},'!_,nil);
   fancy!-prin2!*("\left(",nil);
   w := w eq 'failed or fancy!-prinfit(a3,0,nil);
   %w := w eq 'failed or fancy!-special!-symbol(124,1);    % vertical bar
   fancy!-prin2!*("\left|",1);
   w := w eq 'failed or fancy!-print!-indexlist1(a1,'!^,'!*comma!*);
   w := w eq 'failed or fancy!-print!-indexlist1(a2,'!_,'!*comma!*);
   fancy!-prin2!*("\right.\right)",nil);
   return w;
  end;

% meijerg({{},1},{{0}},x);

% Now a few things that can be useful for testing this code...

symbolic <<
% Arrange that if this file is loaded twice you do not get silly messages
% to do with redefinition of these.
  if not get('texsym, 'simpfn) then
    algebraic operator texsym, texbox, texfbox, texstring >>;

% texsym(!Longleftarrow) should generate \Longleftarrow (etc). This
% might plausibly be useful while checking that the interface can render
% all TeX built-in keywords properly. Furthermore I allow extra args, so
% that eg texsym(stackrel,f,texsym(longrightarrow)) turns into
%   \stackrel{f}{\longrightarrow}

put('texsym,'fancy!-prifn,'fancy!-texsym);

symbolic procedure fancy!-texsym u;
   fancy!-level
    begin
      if null u then return;
      fancy!-prin2 list!-to!-string ('!\ . explode2 cadr u);
      u := cddr u;
      while u do <<
         fancy!-line!* := "{" . fancy!-line!*;
         fancy!-maprint(car u, 0);
         fancy!-line!* := "}" . fancy!-line!*;
         u := cdr u >>
    end;

% texstring("arbitrary tex stuff",...)
% where atoms (eg strings and words) are just passed to tex but
% more complicated items go through fancy!-maprint.

put('texstring,'fancy!-prifn,'fancy!-texstring);

symbolic procedure fancy!-texstring u;
   fancy!-level
    for each s in cdr u do <<
      if not atom s then fancy!-maprint(s, 0)
      else <<
         if not stringp s then s := list!-to!-string explode2 s;
         fancy!-line!* := s . fancy!-line!* >> >>;

% texbox(h) is a box of given height (in points)
% texbox(h, d) is a box of given height and depth
%              height is amount above the reference line, depth is amount
%              below.
% textbox(h, d, c) is a box of given size with some specified content

% All these draw a frame around the space used so you can see what is
% goin on.

% The idea that this may be useful when checking how layouts cope with
% various sizes of content, eg big delimiters, square root signs etc. So I
% can test with "for i := 10:40 do write sqrt(texbox(i))" etc.
% to test sqrt with arguments of height 10, 11, ... to 40 points. Note that
% certainly with the CSL version the concept of a "point" is a bit vauge!
% However if I were to imagine that my screen was at 75 pixels per inch I
% could with SOME reason interpret point as meaning pixel, and that is
% what I will do. At present what I might do about hard-copy output is
% pretty uncertain. If height and depth are given as 0 and there is a
% content them the content will define the box size.

put('texbox,'fancy!-prifn,'fancy!-texbox);

symbolic procedure fancy!-texbox u;
   fancy!-level
    begin
      scalar height, depth, contents;
      contents := nil;
      u := cdr u;
      height := car u;
      u := cdr u;
      if u then <<
         depth := car u;
         u := cdr u;
         if u then contents := car u >>;
      if not numberp height then height:=0;
      if not numberp depth then depth:=0;
      if height=0 and depth=0 and null contents then height:=10;
      fancy!-prin2 "\fbox{";
      if height neq 0 or depth neq 0 then << % insert a rule
         fancy!-line!* := "\rule" . fancy!-line!*;
         if depth neq 0 then <<
            fancy!-line!* := "[-" . fancy!-line!*;
            fancy!-line!* := depth . fancy!-line!*;
            fancy!-line!* := "pt]" . fancy!-line!* >>;
         fancy!-line!* := "{0pt}{" . fancy!-line!*;
         fancy!-line!* := (height+depth) . fancy!-line!*;
         fancy!-line!* := "pt}" . fancy!-line!* >>;
      if contents then contents := fancy!-maprint(contents, 0)
      else fancy!-line!* := "\rule{10pt}{0pt}" . fancy!-line!*;
      fancy!-prin2 "}";
      return contents
    end;

% texfbox is a simplified version of texbox, and just draws a box around the
% expression it is given.

put('texfbox,'fancy!-prifn,'fancy!-texfbox);

symbolic procedure fancy!-texfbox u;
   fancy!-level
    begin
      fancy!-prin2 "\fbox{";
      u := fancy!-maprint(cadr u, 0);
      fancy!-prin2 "}";
      return u
    end;



endmodule;

module promptcolor;
% Adapted from Prompt coloring for redfront.

fluid '(lispsystem!*);

fluid '(promptstring!* tm_switches!* tm_switches!-this!-sl!* lessspace!*);

fluid '(!*promptnumbers);
switch promptnumbers;
if texmacsp () then % We don't want prompt numbers in a Texmacs worksheet
   off1 'promptnumbers
else
   on1 'promptnumbers;

tm_switches!* := {!*msg,!*output};

off1 'msg;
off1 'output;

procedure tm_bprompt();
   % Begin of prompt.
   {int2id 2,'c,'h,'a,'n,'n,'e,'l,'!:,'p,'r,'o,'m,'p,'t,int2id 5,
      int2id 2,'l,'a,'t,'e,'x,'!:,'!R,'!E,'!D,'!U,'!C,'!E};

procedure tm_eprompt();
   % End of prompt
   {'!],'!\,'! ,int2id 5};

% This always gets a list of the characters that make up the prompt...
procedure tm_coloredp(ec);
      eqcar(ec, car tm_bprompt());

procedure tm_nconcn(l);
   % Taken from rltools.
   if cdr l then nconc(car l,tm_nconcn cdr l) else car l;

symbolic procedure tm_prunelhead(l, l1);
    if null l or null l1 then l else tm_prunelhead(cdr l, cdr l1);

procedure tm_pruneltail(l,l1);
   reversip tm_prunelhead(reversip l,l1);

procedure tm_pslp();
   'psl memq lispsystem!*;

if tm_pslp() then <<
   tm_switches!-this!-sl!* := {!*usermode};
   off1 'usermode
>>;

procedure tm_color(c);
   % Color prompt. This will handle EITHER an identifier OR a string, and
   % it returns the same sort of object. It wraps tm_bprompt() and
   % tm_eprompt() around the text it is passed.
   begin scalar ec, sf;
      if stringp c then <<
               ec := string!-to!-list c;
               sf := t >>
      else ec := explode2 c;   % Original code has explode not explode2 here.
      ec := '!  . ec; % add space
      if not !*promptnumbers then << % strip numbers from prompt
               while ec and memq(car ec,'(!  !0 !1 !2 !3 !4 !5 !6 !7 !8 !9)) do
            ec := cdr ec;
         ec := if ec and eqcar(ec,'!*) then {'!*};
      >>;
      ec := append(tm_bprompt(), append(ec, tm_eprompt()));
      ec := list!-to!-string ec;
      if sf then return ec
      else return intern ec
   end;

procedure tm_uncolor(c);
   % Uncolor prompt.
   begin scalar ec, sf;
      if stringp c then <<
           ec := string!-to!-list c;
           sf := t >>
       else ec := explode2 c;  % cf explode?
       if not tm_coloredp ec then return c;
       ec := tm_prunelhead(ec, tm_bprompt());
       if car ec eq '!  then ec := cdr ec; % strip space
       ec := tm_pruneltail(ec, tm_eprompt());
       ec := list!-to!-string ec;
       if sf then return ec
       else return intern ec
   end;

procedure tm_setpchar!-psl(c);
   begin scalar w;
      w := tm_setpchar!-orig c;
      promptstring!* := tm_color promptstring!*;
      return tm_uncolor w
   end;

!#if (memq 'csl lispsystem!*)

switch redfront_mode;

% I do not think there is any merit in even definning this if I am not
% using CSL.

procedure tm_setpchar!-csl(c);
% With CSL in many cases the system does prompt colouring at a lower level
% in the code, so the stuff here is not necessary. However if CSL is used
% with an external redfront of texmacs interface I will want to activate
% this special stuff. So I provide a switch redfront_mode that controls
% what I do. I expect to run with this module loaded almost all of the time
% which is why I want a control via switch rather than through just
% "load tmprint". I note that if CSL is loaded from a script that attaches it
% to redfront of som eother interface that the invocation can use
%      -D*redfront_mode
% to preset the switch, which ought to be a small enough burden to be
% tolerable!
   if !*redfront_mode then tm_uncolor tm_setpchar!-orig tm_color c
   else tm_setpchar!-orig c;

!#endif

if not getd 'tm_setpchar!-orig then copyd('tm_setpchar!-orig,'setpchar);

if tm_pslp() then
   copyd('setpchar,'tm_setpchar!-psl)
else
   copyd('setpchar,'tm_setpchar!-csl);

procedure tm_yesp!-psl(u);
   begin scalar ifl,ofl,x,y;
      if ifl!* then <<
         ifl := ifl!* := {car ifl!*,cadr ifl!*,curline!*};
         rds nil
      >>;
      if ofl!* then <<
         ofl:= ofl!*;
          wrs nil
      >>;
      if null !*lessspace then
          terpri();
      if atom u then
          prin2 u
      else
          lpri u;
      if null !*lessspace then
          terpri();
      y := setpchar "?";
      x := yesp1();
      setpchar y;
      if ofl then wrs cdr ofl;
      if ifl then rds cadr ifl;
      cursym!* := '!*semicol!*;
      return x
   end;

if tm_pslp() then <<
   remflag('(yesp),'lose);
   copyd('tm_yesp!-orig,'yesp);
   copyd('yesp,'tm_yesp!-psl);
   flag('(yesp),'lose)
>>;

% Color PSL prompts, in case user falls through:

procedure tm_compute!-prompt!-string(count,level);
   tm_color tm_compute!-prompt!-string!-orig(count,level);

if tm_pslp() then <<
   copyd('tm_compute!-prompt!-string!-orig,'compute!-prompt!-string);
   copyd('compute!-prompt!-string,'tm_compute!-prompt!-string)
>>;

procedure tm_break_prompt();
   <<
      prin2 "break["; prin2 breaklevel!*; prin2 "]";
      promptstring!* := tm_color promptstring!*
   >>;

if tm_pslp() then <<
   remflag('(break_prompt),'lose);
   copyd('break_prompt,'tm_break_prompt);
   flag('(break_prompt),'lose);
>>;

if tm_pslp() then
   onoff('usermode,car tm_switches!-this!-sl!*);

onoff('msg,car tm_switches!*);
onoff('output,cadr tm_switches!*);

crbuf!* := nil;
inputbuflis!* := nil;
lessspace!* := t;
statcounter := 0;

endmodule;

end;
