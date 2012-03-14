module excalc; % header for EXCALC, a differential geometry package.

% Author: Eberhard Schruefer

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



%*********************************************************************;
%*********************************************************************;
%                   Differential Geometry Package                     ;
%*********************************************************************;
% This version runs in REDUCE 3.6                                     ;
%*********************************************************************;
% Version: 2.5                                                        ;
% E. Schruefer 09/20/93, 05/29/95                                     ;
%*********************************************************************;
% Last version 2 release.                                             ;
%*********************************************************************;
% Eberhard Schruefer                                                  ;
% German National Research Center for Information Technology (GMD)    ;
% Institut SCAI.ALG                                                   ;
% Schloss Birlinghoven                                                ;
% 53754 St. Augustin                                                  ;
% Germany                                                             ;
%*********************************************************************;
% E-mail: schruefer@gmd.de         FAX: +49 2241 14 2618              ;
%*********************************************************************;


create!-package('(excalc exintro exaux degform exdf forder frames hodge
                  idexf indices indsymm indxprin innerprd liedf
                  lievalfm partdf partitsf vardf vecanlys exlists
                  wedge),
                '(contrib excalc));

%************ patches ***************;

% Meaning of ^ and # changed.  !!!! BE AWARE OF THIS "!!!

remprop('!^,'newnam);

% plus and difference changed because we are dealing with non-
% homogenous terms

deflist('
  ((difference getrtypeor)
   (plus getrtypeor)
 ),'rtypefn);

fluid '(depl!*);    % !*ignoreeol

global '(bndeq!* detm!*);

share bndeq!*,detm!*;

global '(lftshft!*);

% !*ignoreeol := t;    % To allow for Excalc's special constructs.


% Smacros used by more than one EXCALC module:

smacro procedure ldpf u;
   %selector for leading standard form in patitioned sf;
   caar u;

smacro procedure tpsf u;
   %selector for leading term in partitioned sf;
   car u;

smacro procedure !*k2pf u;
   u .* (1 ./ 1) .+ nil;

smacro procedure negpf u;
   multpfsq(u,(-1) ./ 1);

smacro procedure lowerind u;
   list('minus,u);

smacro procedure lwf u;
   %selector for leading factor in wedge.
   car u;

smacro procedure rwf u;
   %selector for the rest of factors in wedge.
   cdr u;

smacro procedure lftshftp u;
   smemqlp(lftshft!*,u);

smacro procedure get!-impfun!-args u;
   % Get dependencies of id u.
   cdr assoc(u,depl!*);

smacro procedure get!*fdeg u;
   (if x then car x else nil) where x = get(u,'fdegree);

smacro procedure get!*ifdeg u;
   (if x then cdr x else nil)
    where x = assoc(length cdr u,get(car u,'ifdegree));


%%% This macro from fmprint.red needed for independent compilation.

symbolic macro procedure fancy!-level u;
 % unwind-protect for special output functions.
  {'prog,'(pos fl w),
      '(setq pos fancy!-pos!*),
      '(setq fl fancy!-line!*),
      {'setq,'w,cadr u},
      '(cond ((eq w 'failed)
              (setq fancy!-line!* fl)
              (setq fancy!-pos!* pos))),
       '(return w)};

endmodule;

end;
