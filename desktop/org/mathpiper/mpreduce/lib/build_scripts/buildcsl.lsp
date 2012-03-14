
%
% Create fasl files for compiler, ccomp and extras and build a CSL
% checkpoint image for a system that includes them.
%
% Run this using
%           csl -z build0.lsp -l build0.log
%

%
% This code may be used and modified, and redistributed in binary
% or source form, subject to the "CCL Public License", which should
% accompany it. This license is a variant on the BSD license, and thus
% permits use of code derived from this in either open and commercial
% projects: but it does require that updates to this code be made
% available back to the originators of the package. Note that as with
% any BSD-style licenses the terms here are not compatible with the GNU
% public license, and so GPL code should not be combined with the material
% here in any way.
%


(verbos 3)

(rdf "$cslbase/compat.lsp")
(rdf "$cslbase/extras.lsp")
(rdf "$cslbase/compiler.lsp")

% First I compile those things that appear to be most heavily used in the
% compiler. This should speed up the whole bootstrap compilation process

(compile '(
    s!:improve s!:literal_order s!:comval s!:outopcode0
    s!:plant_basic_block s!:remlose s!:islocal
    s!:is_lose_and_exit s!:comatom s!:destination_label
    s!:record_literal s!:resolve_labels s!:expand_jump
    s!:outopcode1lit stable!-sortip s!:iseasy s!:outjump
    s!:add_pending s!:comcall s!:resolve_literals))

(compile!-all)

% Build fasl files for the compatibility code and the two
% versions of the compiler.

% I choose to turn the utility functions into native code in
% case performance matters for some of them, but I will leave the
% compiler byte-coded since it generally seems to me to be fast enough
% for most reasonable needs. This is a rather frivolous decision since
% the performance gains are not liable to be huge, but is done mainly
% to illustrate the native compilation scheme and keep some regression
% testing on it.
%
% Well maybe I will not, since that is a way of making things more
% delicate!
%
% (setq !*native_code t)

(faslout 'compat)
(rdf "$cslbase/compat.lsp")
(rdf "$cslbase/extras.lsp")
(faslend)

(setq !*native_code nil)

(faslout 'compiler)
(rdf "$cslbase/compiler.lsp")
(faslend)

(bytecounts)

%
% (restart-csl nil) is a fairly dramatic, and maybe dangerous function.  It
% abandons all existing Lisp data and restarts the system from scratch,
% with only minimal built-in functionality.
%

(restart!-csl nil)

(load!-module 'compat)

(fluid '(!*comp !*native_code))

(setq !*comp nil)

(set!-autoload 'compile      'compiler)
(set!-autoload 'compile!-all 'compiler)
(set!-autoload 'faslout      'compiler)
(set!-autoload 'c_out        'compiler)


% My choice for a Lisp system is to enable full compilation (but NOT
% into DLL form) by default.

(setq !*comp t)
(setq !*native_code nil)

(setq !@cslbase (setq !@srcdir (symbol!-value (gensym))))

(preserve)

