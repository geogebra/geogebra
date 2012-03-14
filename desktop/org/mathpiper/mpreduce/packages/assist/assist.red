module assist; % Header Module valid for REDUCE versions from 3.5 to 3.7.

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


create!-package('(assist sl2psl switchxt baglist hcvctors genpurfn
                  control polyexns transfns vectorop grassman matrext
                  helpasst),
                '(contrib assist));

% % *****************************************************************
%
%                Author: H. Caprasse <hubert.caprasse@ulg.ac.be>.
%
% Version and Date:  Version 2.31, 15 January 1999.
%
% Revision history to versions 2.0 to 2.3 and 2.31:
%
% 1 Mai 1993    : Correction to SPLITTERMS and RCONS
%               : New functions CYCLICPERMLIST, APPENDN,
%               : INSERT_KEEP_ORDER
%               : CHECKPROPLIST, EXTRACTLIST, SORTNUMLIST.
% 15 Mai 1993   : LIST_TO_IDS replaces MKIDN.
%               : SORTLIST generalises SORTNUMLIST to sort list of ids
%               : ALG_TO_SYMB  et SYMB_TO_ALG created.
%               : MERGE_LIST complementary funct. to INSERT_KEEP_ORDER
% 17 May 1993   : Creation of SYMMETRIZE
% 27 May 1993   : Various modifications and improvements,
% 12 June 1993  : Corrections to UNION, SYMB_TO_ALG and SYMMETRIZE
% 20 June 1993  : Addition of several functions
%               : in the module POLYEXNS.
%               : GCDNL, NORM_MON, NORM_POL, LIST_COEFF_POL
%               : Addition of PERM_TO_NUM and NUM_TO_PERM in module
%               : 'genpurfn'
% 25 June 1993  : Various modifications and corrections.
%               : Functions involved: DEPTH, ADDFD, REMVAR!: .
% 12 Dec. 1993  : Functions CONCSUMLOG and PLUSLOG eliminated.
%               : Function ALGSORT created. It will probably replace
%               : SORTLIST and SORTNUMLIST very soon.
%               : Module 'hcvectors' added. It provides functions to
%               : manipulate symbolic vector as list and for coercion
%               : of list to vector and vice-versa. Needed to run
%               : the package 'DUMMY.RED'.
%               : Module 'SL2PSL' added. By default it is entirely
%               : commented because all psl versions of REDUCE do not
%               : need it. For the other versions some of the included
%               : functions may be needed. It suffices to remove the
%               : comment characters where needed and recompile the
%               : package.
% 20 Dec. 1993  : Function ALGNLIST introduced. Module 'hcvectors'
%               : extended to allow to make coercions from lists
%               : to arrays.
%               : LIST_to_ARRAY, ARRAY_TO_LIST available.
%               : Function MKRANDTABL created.
% 21 Dec. 1993  : Module 'HELPASST' added.
% 21 Jan. 1994  : Corrections for a proper use of number|-of|-args.
%               : Corrections to EXTRACTLIST and LOWESTDEG
% 28 Jan. 1994  : Modification of LIST2VECT. Corrections to
%               : CLEARFUNCTIONS.
% 20 Aug. 1996  : RPOSITION modified to work properly in OFF EXP.
%               : RCONS has been modified to make it compatible
%               : with the dot product of the HEPHYS package.
%               : Function MKDEPTH_ONE added.
%               : The functions PERM_TO_NUM and NUM_TO_PERM are
%               : commented.
%               : The function REMNONCOM to remove the NONCOM property
%               : has been introduced.
%               : Utility functions for the HEPHYS package are
%               : included in the module control. They are
%               : REMINDEX, REMVECTOR, MKGAM, GETMAS.
%               : Module 'HELPASST' modified. HELPASSIST ==> ASSISTHELP.
%               : Additional function ASSIST introduced.
%               : Numerous revised or corrected comments.
% 1 Jan. 1999   : MKIDM modified
% ===============================================================

 fluid '(!*ncmp);
!*ncmp:=t;

endmodule;
end;
