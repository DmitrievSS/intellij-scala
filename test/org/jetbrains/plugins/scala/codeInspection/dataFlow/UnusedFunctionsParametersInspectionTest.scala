package org.jetbrains.plugins.scala.codeInspection.dataFlow

import com.intellij.codeInspection.LocalInspectionTool
import org.jetbrains.plugins.scala.UnusedFunctionParameterInspection
import org.jetbrains.plugins.scala.codeInspection.ScalaLightInspectionFixtureTestAdapter

/**
  * Created by ssdmitriev on 11.02.16.
  */
class UnusedFunctionsParametersInspectionTest extends ScalaLightInspectionFixtureTestAdapter {
  override protected def classOfInspection: Class[_ <: LocalInspectionTool] = classOf[UnusedFunctionParameterInspection]

  override protected def annotation: String = UnusedFunctionParameterInspection.inspectionName

  def testUsedParametersInspection() = {
    val text =
      s"""def foo(x: Int) = x"""
    checkTextHasNoErrors(text)
  }

  def testUnusedParametersInspection() = {
    val text =
      s"""def foo(x: Int) = {}"""
    checkTextHasError(text)
  }

  def testUnusedParametersInspectionWithInner() = {
    val text =
      s"""def foo(x: Int) = {def inner(x: Int) {
          |      x
          |    }}"""
    checkTextHasError(text)
  }

}
