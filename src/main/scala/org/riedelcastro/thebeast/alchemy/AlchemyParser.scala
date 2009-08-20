package org.riedelcastro.thebeast.alchemy


import collection.mutable.{ArrayBuffer, HashMap}
import env.doubles.DoubleTerm
import env.{Values, Var}
import scala.util.parsing.combinator.RegexParsers
/**
 * @author Sebastian Riedel
 */

object AlchemyParser extends RegexParsers {
  val LowerCaseID = """[a-z]([a-zA-Z0-9]|_[a-zA-Z0-9])*""" r
  val UpperCaseID = """[A-Z]([a-zA-Z0-9]|_[a-zA-Z0-9])*""" r
  val NumDouble = "-?\\d+(\\.\\d+)?" r

  def atom: Parser[Atom] = UpperCaseID ~ "(" ~ termList ~ ")" ^^ {case s ~ "(" ~ terms ~ ")" => Atom(s, terms)}

  def and: Parser[And] = (atom ~ "^" ~ formula) ^^ {case lhs ~ "^" ~ rhs => And(lhs, rhs)}

  def formula: Parser[Formula] = (weightedFormula | binary(minPrec) | atomic)

  def atomic: Parser[Formula] = (parens|atom)

  def weightedFormula: Parser[WeightedFormula] =
    (NumDouble ~ formula) ^^ {case weight ~ formula => WeightedFormula(weight.toDouble, formula)}

  def termList: Parser[List[Term]] = repsep(term, ",") ^^ {case t => t}

  def term: Parser[Term] = (variable | constant | exclType | plusVariable)

  def variable: Parser[VariableOrType] = LowerCaseID ^^ {s => VariableOrType(s)}

  def constant: Parser[Constant] = UpperCaseID ^^ {s => Constant(s)}

  def exclType: Parser[ExclamationType] = "!" ~> LowerCaseID ^^ {s => ExclamationType(s)}

  def plusVariable: Parser[PlusVariable] = "+" ~> LowerCaseID ^^ {s => PlusVariable(s)}

  def parens: Parser[Formula] = "(" ~> formula <~ ")"


  def binaryOp(level: Int): Parser[((Formula, Formula) => Formula)] = {
    level match {
      case 1 =>
        "v" ^^^ {(a: Formula, b: Formula) => Or(a, b)} 
      case 2 =>
        "=>" ^^^ {(a: Formula, b: Formula) => Implies(a, b)} |
                "<=>" ^^^ {(a: Formula, b: Formula) => Equivalence(a, b)}
      case 3 =>
        "^" ^^^ {(a: Formula, b: Formula) => And(a, b)} 
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1
  val maxPrec = 3

  def binary(level: Int): Parser[Formula] =
    if (level > maxPrec) atomic
    else binary(level + 1) * binaryOp(level)


  def test(test: String) = {
    println(parse(weightedFormula, test))
    //println(AlchemyParser.formula(new scala.util.parsing.combinator.lexical.Scanner(test)))    
  }

  trait Expression
  trait Term extends Expression
  case class Constant(value: String) extends Term
  case class VariableOrType(name: String) extends Term
  case class ExclamationType(name: String) extends Term
  case class PlusVariable(name: String) extends Term

  trait Formula extends Expression
  case class WeightedFormula(weight: Double, formula: Formula) extends Formula
  case class Atom(predicate: String, args: List[Term]) extends Formula
  case class And(lhs: Formula, rhs: Formula) extends Formula
  case class Or(lhs: Formula, rhs: Formula) extends Formula
  case class Implies(lhs: Formula, rhs: Formula) extends Formula
  case class Equivalence(lhs: Formula, rhs: Formula) extends Formula

}

object Test extends Application {
  val test = "10.0 Same(+hallo,!po) ^ \n (Popel(du,igel)) => Same(du, nuss)"

  AlchemyParser.test(test)

}

class MLN {
  private val values = new HashMap[String, Values[_]]
  private val predicates = new HashMap[String, Var[_]]
  private val formulae = new ArrayBuffer[DoubleTerm]

}