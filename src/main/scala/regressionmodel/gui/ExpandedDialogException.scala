package regressionmodel.gui

case class ExpandedDialogException(title: String, header: String, text: String, expanded: String) extends Exception(header)
