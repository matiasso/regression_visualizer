package regressionmodel.filehandler

case class InvalidDataFormat(description: String, origText: String) extends java.lang.Exception(description)
