module plot; % device and driver independent plot services.

% Author: Herbert Melenk.
% Adjusted by A C Norman to be compatible with CSL - the original
% was written to be fairly PSL-specific.

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


% Minor corrections by Winfried Neun (October 1995)

create!-package('(plot plotsynt plotexp2 pltpara plotexp3 plotimp2
                  plotimp3 plotnum parray xvect),
                nil);

global '(

      plotdriver!*         % modulename of the actual driver.

      plotmax!*            % maximal floating point number which
                           % gnuplot supports on the machine
                           % (mostly IEEE double or single precision).

      plotmin!*            % lower bound (=1/plotmax!*)

      variation!*          %  definition of y-bigstep for smooth

      plotoptions!*        % list for collecting the options.

);

fluid '(

      plotderiv!*          %  derivative for 2d plot

);

!#if(or (errorp (errorset '!*writingfaslfile nil nil))
        (not !*writingfaslfile)
        (errorp (errorset '(load fcomp) nil nil)))
%  prin2t "*** No support for fast float!";
   symbolic macro procedure fdeclare u; nil;
   symbolic macro procedure thefloat u; cadr u;
!#endif

% Create .. as infix operator.

newtok '((!. !.) !*interval!*);

put('!*interval!*,'prtch,'! !.!.! );

if null get('!*interval!*,'simpfn)
  then <<precedence .., or; algebraic operator ..>>;

 % Reestablished these routines in order to support singularity handling
 % (which was better in some respects in Reduce 3.5) %WN

symbolic procedure ploteval3xysingular(ff,f,x,xx,dx,y,yy,dy,zhi,zlo);
 % set up an iteration approaching a critical point.
   <<dx:=dx/4; dy:=dy/4;
     ploteval3xysingular1(ff,f,x,xx,dx,y,yy,dy,zhi,zlo,
       plotevalform(ff,f,{x . (xx+dx), y . (yy+dy)}),0)
    >>;

symbolic procedure ploteval3xysingular1(ff,f,x,xx,dx,y,yy,dy,zhi,zlo,w,c);
  if null w then nil else if c>8 then nil else
  if w>zhi then zhi else
  if w<zlo then zlo else
  begin scalar wnew;
    dx:=dx/2; dy:=dy/2;
    wnew := plotevalform(ff,f,{x . (xx+dx), y . (yy+dy)});
    return
     if null wnew then nil else
     if abs(wnew-w) <abs wnew/20 then wnew else
       ploteval3xysingular1(ff,f,x,xx,dx,y,yy,dy,zhi,zlo,wnew,c+1);
  end;


% I need the following definition only at compile time.
macro procedure plotdriver u;
  {'apply,{'get,'plotdriver!*,mkquote cadr u},'list . cddr u};


endmodule;

end;

