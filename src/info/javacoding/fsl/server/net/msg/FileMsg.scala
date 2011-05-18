package info.javacoding.fsl.server.net.msg
import info.javacoding.fsl.server.net.Message
import java.io.{ File, FileInputStream, FileOutputStream }

/**
 * Moves a file...
 *
 * @author Joe Pritzel
 */
class MoveMsg extends Message {
  def getOpcode = "MOVE"
  def handle(params: Array[String]): String = {
    if (params.length != 2) {
      return "Move failed - invalid params"
    }
    val fileName = params(0) replaceAll("\\(home\\)", System.getProperty("user.home")) replaceAll("\\(sep\\)", File.separator)
    val file = new File(fileName)
    val dir = new File(params(1) replaceAll("\\(home\\)", System.getProperty("user.home")) replaceAll("\\(sep\\)", File.separator))
    val success = file.renameTo(new File(dir, file.getName()));
    if (!success) {
      return "Move failed"
    } else {
      return "Moved - " + fileName + " to " + params(1)
    }
  }
}

/**
 * Copies a file...
 *
 * @author Joe Pritzel
 */
class CopyMsg extends Message {
  def getOpcode = "COPY"
  def handle(params: Array[String]): String = {
    if (params.length != 2) {
      return "Copy failed - invalid params"
    }
    val src = new File(params(0) replaceAll("\\(home\\)", System.getProperty("user.home")) replaceAll("\\(sep\\)", File.separator))
    val dest = new File(params(1) replaceAll("\\(home\\)", System.getProperty("user.home")) replaceAll("\\(sep\\)", File.separator))
    new FileOutputStream(dest) getChannel() transferFrom(new FileInputStream(src) getChannel, 0, Long.MaxValue)
    return "Copied - " + params(0) + " to " + params(1)
  }
}

/**
 * The equivilent of ls -a on a Linux machine.
 *
 * @author Joe Pritzel
 */
class ListMsg extends Message {
  def getOpcode = "LIST"
  def handle(params: Array[String]): String = {
    if (params.length != 1) {
      return "List failed - invalid params"
    }
    val msg = params(0) replaceAll("\\(home\\)", System.getProperty("user.home")) replaceAll("\\(sep\\)", File.separator)
    val f = new File(msg)
    if (!f.exists()) {
      return "No such directory"
    }
    if (!f.isDirectory()) {
      return "That is not a directory"
    }
    val fl = f.listFiles
    val sb = new StringBuilder()
    fl.foreach(ft => sb.append(ft.getName()).append(" - "))
    sb.delete(sb.length() - 3, sb.length() - 1);
    return sb.toString
  }
}