(*
 * Copyright 2014, INRIA
 * Julia Lawall
 * This file is part of Cgen.  Much of it comes from Coccinelle, which is
 * also available under the GPLv2 license
 *
 * Cgen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, according to version 2 of the License.
 *
 * Cgen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cgen.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The authors reserve the right to distribute this or future versions of
 * Cgen under other licenses.
 *)


# 0 "./pretty_print_c.mli"
type type_with_ident =
    (string * Ast_c.info) option ->
    (Ast_c.storage * Ast_c.il) option ->
    Ast_c.fullType ->
    Ast_c.attribute list -> unit

type 'a printer = 'a -> unit

type pretty_printers = {
  expression      : Ast_c.expression printer;
  arg_list        : (Ast_c.argument Ast_c.wrap2 list) printer;
  arg             : Ast_c.argument printer;
  statement       : Ast_c.statement printer;
  decl            : Ast_c.declaration printer;
  field           : Ast_c.field printer;
  field_list      : Ast_c.field list printer;
  init            : Ast_c.initialiser printer;
  init_list       : (Ast_c.initialiser Ast_c.wrap2 list) printer;
  param           : Ast_c.parameterType printer;
  paramlist       : (Ast_c.parameterType Ast_c.wrap2 list) printer;
  ty              : Ast_c.fullType printer;
  type_with_ident : type_with_ident;
  toplevel        : Ast_c.toplevel printer;
  fragment        : Ast_c.string_fragment printer;
  fragment_list   : (Ast_c.string_fragment list) printer;
  format          : Ast_c.string_format printer;
}

val mk_pretty_printers :
  pr_elem:Ast_c.info printer ->
  pr_space:unit printer ->
  pr_nl: unit printer ->
  pr_indent: unit printer ->
  pr_outdent: unit printer ->
  pr_unindent: unit printer ->
  pretty_printers

(* used in pycocci mostly *)
val pp_expression_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.expression printer
val pp_arg_list_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  (Ast_c.argument Ast_c.wrap2 list) printer
val pp_arg_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.argument printer
val pp_decl_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.declaration printer
val pp_field_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.field printer
val pp_field_list_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.field list printer
val pp_statement_gen: pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.statement printer
val pp_param_gen:  pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  Ast_c.parameterType printer
val pp_param_list_gen:  pr_elem:Ast_c.info printer -> pr_space: unit printer ->
  (Ast_c.parameterType Ast_c.wrap2 list) printer
val pp_type_gen:  pr_elem:Ast_c.info printer -> pr_space:unit printer ->
  Ast_c.fullType printer
val pp_init_gen:  pr_elem:Ast_c.info printer -> pr_space:unit printer ->
  Ast_c.initialiser printer
val pp_init_list_gen:  pr_elem:Ast_c.info printer -> pr_space:unit printer ->
  (Ast_c.initialiser Ast_c.wrap2 list) printer
val pp_string_fragment_list_gen:
    pr_elem:Ast_c.info printer -> pr_space:unit printer ->
      Ast_c.string_fragment list printer
val pp_string_format_gen:
    pr_elem:Ast_c.info printer -> pr_space:unit printer ->
      Ast_c.string_format printer
val pp_program_gen : pr_elem:Ast_c.info printer -> pr_space:unit printer ->
  Ast_c.toplevel printer


(* used in pretty_print_engine.ml mostly *)
val pp_expression_simple: Ast_c.expression printer
val pp_init_simple:       Ast_c.initialiser printer
val pp_type_simple:       Ast_c.fullType printer
val pp_decl_simple:       Ast_c.declaration printer
val pp_field_simple:      Ast_c.field printer
val pp_statement_simple:  Ast_c.statement printer
val pp_toplevel_simple:   Ast_c.toplevel printer
val pp_string_fragment_simple:   Ast_c.string_fragment printer
val pp_string_format_simple:     Ast_c.string_format printer

val string_of_expression: Ast_c.expression -> string
val string_of_type: Ast_c.fullType -> string
val string_of_toplevel: Ast_c.toplevel -> string
