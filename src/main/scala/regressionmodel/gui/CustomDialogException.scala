package regressionmodel.gui

case class CustomDialogException(title: String, header: String, text: String) extends Exception(header)
