package org.jetbrains.plugins.scala.codeInspection.dataFlow

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.codeInspection.AbstractInspection
import org.jetbrains.plugins.scala.lang.psi.api.ScalaRecursiveElementVisitor
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScParameter
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScFunctionDefinition, ScFunction}
import org.jetbrains.plugins.scala.lang.psi.impl.statements.params.ScParameterImpl

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
  * Created by ssdmitriev on 04.02.16.
  */
class UnusedFunctionParameterInspection
  extends AbstractInspection("UnusedFunctionParameter", "function parameter not used") {
  override def actionFor(holder: ProblemsHolder): PartialFunction[PsiElement, Any] = {
    case expr: ScFunctionDefinition =>
      val message = "function parameter not used"
      findUnusedParameter(expr).foreach(parameter => holder.registerProblem(parameter, message))
  }

  def findUnusedParameter(f: ScFunctionDefinition): mutable.Buffer[ScParameter] = {
    val unusedParameters = f.parameters.toBuffer
    val visitor = new ScalaRecursiveElementVisitor() {
      override def visitReferenceExpression(ref: ScReferenceExpression): Unit = {
        if (unusedParameters.map(el=>el.getText.substring(0, el.getText.indexOf(':'))).contains(ref.getText)) {
          ref.resolve() match {
            case p: ScParameterImpl =>
              unusedParameters-=(p)
            case _ =>
          }
        }
      }
    }
    f.body.foreach(_.accept(visitor))
    unusedParameters
  }
}
