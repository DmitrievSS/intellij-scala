package org.jetbrains.plugins.scala
package console

import com.intellij.openapi.actionSystem.{PlatformDataKeys, AnActionEvent, AnAction}
import com.intellij.openapi.util.TextRange
import java.io.{IOException, OutputStream}
import com.intellij.openapi.editor.ex.EditorEx

/**
 * @author Ksenia.Sautina
 * @since 9/18/12
 */
class ScalaConsoleExecuteAction extends AnAction {
  override def update(e: AnActionEvent) {
    val project = e.getData(PlatformDataKeys.PROJECT)
    if (project == null) {
      e.getPresentation.setEnabled(false)
      return
    }
    val console = ScalaConsoleInfo.getConsole(project)
    if (console == null)  {
      e.getPresentation.setEnabled(false)
      return
    }
    val editor: EditorEx = console.getConsoleEditor
    e.getPresentation.setEnabled(!editor.isRendererMode && !ScalaConsoleInfo.getProcessHandler(project).isProcessTerminated)
  }

  def actionPerformed(e: AnActionEvent) {
    val project = e.getData(PlatformDataKeys.PROJECT)
    if (project == null) {
      return
    }
    val console = ScalaConsoleInfo.getConsole(project)
    val processHandler = ScalaConsoleInfo.getProcessHandler(project)
    val model = ScalaConsoleInfo.getModel(project)
    val editor = PlatformDataKeys.EDITOR.getData(e.getDataContext)
    if (editor != null && console != null && processHandler != null && model != null) {
      val document = console.getEditorDocument
      val text = document.getText

      // Process input and add to history
      extensions.inWriteAction {
        val range: TextRange = new TextRange(0, document.getTextLength)
        editor.getSelectionModel.setSelection(range.getStartOffset, range.getEndOffset)
        console.addCurrentToHistory(range, false, true)
        model.addToHistory(text)

        editor.getCaretModel.moveToOffset(0)
        editor.getDocument.setText("")
      }

      text.split('\n').foreach(line => {
        if (line != "") {
          val outputStream: OutputStream = processHandler.getProcessInput
          try {
            val bytes: Array[Byte] = (line + "\n").getBytes
            outputStream.write(bytes)
            outputStream.flush()
          }
          catch {
            case e: IOException => //ignore
          }
        }
        console.textSent(line + "\n")
      })
    }
  }
}
