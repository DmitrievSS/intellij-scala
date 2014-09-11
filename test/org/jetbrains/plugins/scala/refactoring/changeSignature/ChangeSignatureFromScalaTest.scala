package org.jetbrains.plugins.scala
package refactoring.changeSignature

import com.intellij.refactoring.changeSignature.{ChangeSignatureProcessorBase, ParameterInfo}
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory
import org.jetbrains.plugins.scala.lang.psi.types
import org.jetbrains.plugins.scala.lang.psi.types.ScType
import org.jetbrains.plugins.scala.lang.refactoring.changeSignature.ScalaParameterInfo

/**
 * Nikolay.Tropin
 * 2014-09-05
 */
class ChangeSignatureFromScalaTest extends ChangeSignatureTestBase {
  override def folderPath: String = baseRootPath() + "changeSignature/fromScala/"

  private var isAddDefaultValue = false

  override def mainFileName(testName: String) = testName + ".scala"
  override def secondFileName(testName: String) = testName + ".java"
  override def mainFileAfterName(testName: String) = testName + "_after.scala"
  override def secondFileAfterName(testName: String) = testName + "_after.java"

  override def processor(newVisibility: String,
                         newName: String,
                         newReturnType: String,
                         newParams: => Seq[Seq[ParameterInfo]]): ChangeSignatureProcessorBase = {
    scalaProcessor(newVisibility, newName, newReturnType, newParams, isAddDefaultValue)
  }

  private def parameterInfo(name: String, oldIdx: Int, tpe: ScType, defVal: String = "", isRep: Boolean = false, isByName: Boolean = false) = {
    new ScalaParameterInfo(name, oldIdx, tpe, getProjectAdapter, isRep, isByName, defVal)
  }

  def testSimpleMethod() = {
    isAddDefaultValue = false
    val params = Seq(parameterInfo("ii", 0, types.Int), parameterInfo("b", 2, types.Boolean))
    doTest(null, "bar", null, Seq(params))
  }

  def testSimpleMethodAdd() = {
    isAddDefaultValue = false
    val params = Seq(parameterInfo("i", 0, types.Int), parameterInfo("s", -1, types.AnyRef, "\"hi\""), parameterInfo("b", 1, types.Boolean))
    doTest(null, "foo", null, Seq(params))
  }

  def testAddWithDefault() = {
    isAddDefaultValue = true
    val params = Seq(parameterInfo("i", 0, types.Int), parameterInfo("s", -1, types.AnyRef, "\"hi\""), parameterInfo("b", 1, types.Boolean))
    doTest(null, "foo", null, Seq(params))
  }

  def testParameterless() = {
    isAddDefaultValue = true
    val params = Seq(parameterInfo("i", -1, types.Int, "1"))
    doTest(null, "bar", null, Seq(params))
  }

  def testAddByName() = {
    val params = Seq(parameterInfo("x", 0, types.Int), parameterInfo("s", 1, types.AnyRef, isByName = true))
    doTest(null, "foo", null, Seq(params))
  }

  def testReturnTypeChange() = {
    val params = Seq(Seq.empty)
    doTest(null, "foo", "Unit", params)
  }

  def testGenerics() = {
    def tpe = ScalaPsiElementFactory.createTypeFromText("T", targetMethod, targetMethod)
    doTest(null, "foo", "T", Seq(Seq(parameterInfo("t", 0, tpe))))
  }
}