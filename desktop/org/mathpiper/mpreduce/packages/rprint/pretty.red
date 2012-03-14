module pretty;  % Print list structures in an indented format.

% Author: A. C. Norman, July 1978.

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


create!-package('(pretty),'(util));

fluid '(bn
        bufferi
        buffero
        indblanks
        indentlevel
        initialblanks
        lmar
        pendingrpars
        rmar
        rparcount
        stack);

global '(!*quotes !*pretty!-symmetric thin!*);

!*pretty!-symmetric := t;
!*quotes := t;
thin!* := 5;

% This package prints list structures in an indented format that
% is intended to make them legible. There are a number of special
% cases recognized, but in general the intent of the algorithm
% is that given a list (R1 R2 R3 ...), SUPERPRINT checks if
% the list will fit directly on the current line and if so
% prints it as:
%        (R1 R2 R3 ...)
% if not it prints it as:
%        (R1
%           R2
%           R3
%           ... )
% where each sublist is similarly treated.
%


% Functions:
%   SUPERPRINTM(X,M)   print expression X with left margin M
%   PRETTYPRINT(X)     = <<superprintm(x,posn()); terpri(); terpri()>>;
%
% Flag:
%   !*SYMMETRIC        If TRUE, print with escape characters,
%                      otherwise do not (as PRIN1/PRIN2
%                      distinction). defaults to TRUE;
%   !*QUOTES           If TRUE, (QUOTE x) gets displayed as 'x.
%                      default is TRUE;
%
% Variable:
%   THIN!*             if THIN!* expressions can be fitted onto
%                      a single line they will be printed that way.
%                      this is a parameter used to control the
%                      formatting of long thin lists. default
%                      value is 5;


symbolic procedure prettyprint x;
 << superprinm(x,posn()); %WHAT REDUCE DOES NOW;
    terpri();
    nil>>;

symbolic procedure superprintm(x,lmar);
  << superprinm(x,lmar); terpri(); x >>;


% From here down the functions are not intended for direct use.

% The following functions are defined here in case this package
% is called from LISP rather than REDUCE.

symbolic procedure eqcar(a,b);
    pairp a and car a eq b;

symbolic procedure spaces n;
    for i:=1:n do prin2 '! ;

% End of compatibility section.

symbolic procedure superprinm(x,lmar);
  begin
    scalar stack,bufferi,buffero,bn,initialblanks,rmar,
           pendingrpars,indentlevel,indblanks,rparcount,w;
    bufferi:=buffero:=list nil; %fifo buffer.
    initialblanks:=0;
    rparcount:=0;
    indblanks:=0;
    rmar:=linelength(nil) - 3; %right margin.
    if rmar<25 then error(0,list(rmar+3,
        "Linelength too short for superprinting"));
    bn:=0; %characters in buffer.
    indentlevel:=0; %no indentation needed, yet.
    if lmar+20>=rmar then lmar:=rmar - 21; %no room for specified margin
    w:=posn();
    if w>lmar then << terpri(); w:=0 >>;
    if w<lmar then initialblanks:=lmar - w;
    prindent(x,lmar+3); %main recursive print routine.
% traverse routine finished - now tidy up buffers.
    overflow 'none; %flush out the buffer.
    return x
  end;


% Access functions for a stack entry.


smacro procedure top; car stack;
smacro procedure depth frm; car frm;
smacro procedure indenting frm; cadr frm;
smacro procedure blankcount frm; caddr frm;
smacro procedure blanklist frm; cdddr frm;
smacro procedure setindenting(frm,val); rplaca(cdr frm,val);
smacro procedure setblankcount(frm,val); rplaca(cddr frm,val);
smacro procedure setblanklist(frm,val); rplacd(cddr frm,val);
smacro procedure newframe n; list(n,nil,0);
smacro procedure blankp char; numberp car char;





symbolic procedure prindent(x,n);
% Print list x with indentation level n.
    if atom x then if vectorp x then prvector(x,n)
        else for each c in
          (if !*pretty!-symmetric
             then if stringp x then explodes x else explode x
            else explode2 x) do putch c
    else if quotep x then <<
        putch '!';
        prindent(cadr x,n+1) >>
    else begin
        scalar cx;
        if 4*n>3*rmar then << %list is too deep for sanity.
            overflow 'all;
            n:=n/8;
            if initialblanks>n then <<
                lmar:=lmar - initialblanks+n;
                initialblanks:=n >> >>;
        stack := (newframe n) . stack;
        putch ('lpar . top());
        cx:=car x;
        prindent(cx,n+1);
        if idp cx and not atom cdr x then
            cx:=get(cx,'ppformat) else cx:=nil;
        if cx=2 and atom cddr x then cx:=nil;
        if cx='prog then <<
            putch '! ;
            prindent(car (x:=cdr x),n+3) >>;
% CX now controls the formatting of what follows:
%    nil      default action
%    <number> first few blanks are non-indenting
%    prog     display atoms as labels.
         x:=cdr x;

   scan: if atom x then go to outt;
         finishpending(); %about to print a blank.
         if cx='prog then <<
             putblank();
             overflow bufferi; %force format for prog.
             if atom car x then << % a label.
                 lmar:=initialblanks:=max(lmar - 6,0);
                 prindent(car x,n - 3); % print the label.
                 x:=cdr x;
                 if not atom x and atom car x then go to scan;
                 if lmar+bn>n then putblank()
                 else for i:=lmar+bn:n - 1 do putch '! ;
                 if atom x then go to outt>> >>
         else if numberp cx then <<
             cx:=cx - 1;
             if cx=0 then cx:=nil;
             putch '!  >>
         else putblank();
         prindent(car x,n+3);
         x:=cdr x;
         go to scan;

   outt: if not null x then <<
            finishpending();
            putblank();
            putch '!.;
            putch '! ;
            prindent(x,n+5) >>;
        putch ('rpar . (n - 3));
        if indenting top()='indent and not null blanklist top() then
               overflow car blanklist top()
        else endlist top();
        stack:=cdr stack
      end;

symbolic procedure explodes x;
   %dummy function just in case another format is needed.
   explode x;

symbolic procedure prvector(x,n);
  begin
    scalar bound;
    bound:=upbv x; % length of the vector.
    stack:=(newframe n) . stack;
    putch ('lsquare . top());
    prindent(getv(x,0),n+3);
    for i:=1:bound do <<
        putch '!,;
        putblank();
        prindent(getv(x,i),n+3) >>;
    putch('rsquare . (n - 3));
    endlist top();
    stack:=cdr stack
  end;

symbolic procedure putblank();
  begin
    putch top(); %represents a blank character.
    setblankcount(top(),blankcount top()+1);
    setblanklist(top(),bufferi . blanklist top());
         %remember where I was.
    indblanks:=indblanks+1
  end;




symbolic procedure endlist l;
%Fix up the blanks in a complete list so that they
%will not be turned into indentations.
     pendingrpars:=l . pendingrpars;

% When I have printed a ')' I want to mark all of the blanks
% within the parentheses as being unindented, ordinary blank
% characters. It is however possible that I may get a buffer
% overflow while printing a string of )))))))))), and so this
% marking should be delayed until I get round to printing
% a further blank (which will be a candidate for a place to
% split lines). This delay is dealt with by the list
% pendingrpars which holds a list of levels that, when
% convenient, can be tidied up and closed out.

symbolic procedure finishpending();
 << for each stackframe in pendingrpars do <<
        if indenting stackframe neq 'indent then
            for each b in blanklist stackframe do
              << rplaca(b,'! ); indblanks:=indblanks - 1>>;
% blanklist of stackframe must be non-nil so that overflow
% will not treat the '(' specially.
        setblanklist(stackframe,t) >>;
    pendingrpars:=nil >>;



symbolic procedure quotep x;
    !*quotes and
    not atom x and
    car x='quote and
    not atom cdr x and
    null cddr x;






% property ppformat drives the prettyprinter -
% prog     : special for prog only
% 1        :    (fn a1
%                  a2
%                  ... )
% 2        :    (fn a1 a2
%                  a3
%                  ... )     ;

put('prog,'ppformat,'prog);
put('lambda,'ppformat,1);
put('lambdaq,'ppformat,1);
put('setq,'ppformat,1);
put('set,'ppformat,1);
put('while,'ppformat,1);
put('t,'ppformat,1);
put('de,'ppformat,2);
put('df,'ppformat,2);
put('dm,'ppformat,2);
put('foreach,'ppformat,4); % (foreach x in y do ...) etc.


% Now for the routines that buffer things on a character by character
% basis, and deal with buffer overflow.


symbolic procedure putch c;
  begin
    if atom c then rparcount:=0
    else if blankp c then << rparcount:=0; go to nocheck >>
    else if car c='rpar then <<
        rparcount:=rparcount+1;
% format for a long string of rpars is:
%    )))) ))) ))) ))) )))   ;
        if rparcount>4 then << putch '! ; rparcount:=2 >> >>
    else rparcount:=0;
    while lmar+bn>=rmar do overflow 'more;
nocheck:
    bufferi:=cdr rplacd(bufferi,list c);
    bn:=bn+1
  end;

symbolic procedure overflow flg;
  begin
    scalar c,blankstoskip;
%the current buffer holds so much information that it will
%not all fit on a line. try to do something about it.
% flg is one of:
%  'none       do not force more indentation
%  'more       force one level more indentation
% <a pointer into the buffer>
%               prints up to and including that character, which
%               should be a blank.
    if indblanks=0 and initialblanks>3 and flg='more then <<
        initialblanks:=initialblanks - 3;
        lmar:=lmar - 3;
        return 'moved!-left >>;
fblank:
    if bn=0 then <<
% No blank found - can do no more for now.
% If flg='more I am in trouble and so have to print
% a continuation mark. in the other cases I can just exit.
        if not(flg = 'more) then return 'empty;
        if atom car buffero then
% continuation mark not needed if last char printed was
% special (e.g. lpar or rpar).
            prin2 "%+"; %continuation marker.
        terpri();
        lmar:=0;
        return 'continued >>
    else <<
        spaces initialblanks;
        initialblanks:=0 >>;
    buffero:=cdr buffero;
    bn:=bn - 1;
    lmar:=lmar+1;
    c:=car buffero;
    if atom c then << prin2 c; go to fblank >>
    else if blankp c then if not atom blankstoskip then <<
        prin2 '! ;
        indblanks:=indblanks - 1;
% blankstoskip = (stack-frame . skip-count).
        if c eq car blankstoskip then <<
            rplacd(blankstoskip,cdr blankstoskip - 1);
            if cdr blankstoskip=0 then blankstoskip:=t >>;
        go to fblank >>
      else go to blankfound
    else if car c='lpar or car c='lsquare then <<
        prin2 get(car c,'ppchar);
        if flg='none then go to fblank;
% now I want to flag this level for indentation.
        c:=cdr c; %the stack frame.
        if not null blanklist c then go to fblank;
        if depth c>indentlevel then << %new indentation.
% this level has not emitted any blanks yet.
            indentlevel:=depth c;
            setindenting(c,'indent) >>;
        go to fblank >>
    else if car c='rpar or car c='rsquare then <<
        if cdr c<indentlevel then indentlevel:=cdr c;
        prin2 get(car c,'ppchar);
        go to fblank >>
    else error(0,list(c,"UNKNOWN TAG IN OVERFLOW"));
blankfound:
    if eqcar(blanklist c,buffero) then
        setblanklist(c,nil);
% at least one entry on blanklist ought to be valid, so if I
% print the last blank I must kill blanklist totally.
    indblanks:=indblanks - 1;
% check if next level represents new indentation.
    if depth c>indentlevel then <<
        if flg='none then << %just print an ordinary blank.
            prin2 '! ;
            go to fblank >>;
% here I increase the indentation level by one.
        if blankstoskip then blankstoskip:=nil
        else <<
            indentlevel:=depth c;
            setindenting(c,'indent) >> >>;
%otherwise I was indenting at that level anyway.
    if blankcount c>(thin!* - 1) then << %long thin list fix-up here.
        blankstoskip:=c . ((blankcount c) - 2);
        setindenting(c,'thin);
        setblankcount(c,1);
        indentlevel:=(depth c) - 1;
        prin2 '! ;
        go to fblank >>;
    setblankcount(c,(blankcount c) - 1);
    terpri();
    lmar:=initialblanks:=depth c;
    if buffero eq flg then return 'to!-flg;
    if blankstoskip or not (flg='more) then go to fblank;
% keep going unless call was of type 'more'.
    return 'more; %try some more.
  end;

put('lpar,'ppchar,'!();
put('lsquare,'ppchar,'![);
put('rpar,'ppchar,'!));
put('rsquare,'ppchar,'!]);

endmodule;

end;
