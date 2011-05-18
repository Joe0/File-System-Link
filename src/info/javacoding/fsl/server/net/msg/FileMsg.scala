package info.javacoding.fsl.server.net.msg
import info.javacoding.fsl.server.net.Message
import java.io.{ File, FileInputStream, FileOutputStream }
import info.javacoding.fsl.server.auth.Authentication

/**
 * Moves a file/directory...
 *
 * @author Joe Pritzel
 */
class MoveMsg extends Message {
  def getOpcode = "MOVE"
  def handle(params: Array[String]): String = {
    if (!Authentication.canWrite()) {
      return "You do not have permission to use this command"
    }
    if (params.length != 2) {
      return "Move failed - invalid params"
    }
    val fileName = Helper.parse(params(0))
    val file = new File(fileName)
    val dir = new File(Helper.parse(params(1)))
    if (file.isDirectory) {
      Helper.copy(file, dir)
      Helper.deleteDir(file)
      return "Moved - " + fileName + " to " + params(1)
    } else {
      val success = file.renameTo(new File(dir, file.getName()))
      if (!success) {
        return "Move failed"
      } else {
        return "Moved - " + fileName + " to " + params(1)
      }
    }
  }
}

/**
 * Copies a file or folder...
 *
 * @author Joe Pritzel
 */
class CopyMsg extends Message {
  def getOpcode = "COPY"
  def handle(params: Array[String]): String = {
    if (!Authentication.canWrite()) {
      return "You do not have permission to use this command"
    }
    if (params.length != 2) {
      return "Copy failed - invalid params"
    }
    val src = new File(Helper.parse(params(0)))
    val dest = new File(Helper.parse(params(1)))
    Helper.copy(src, dest)
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
    if (!Authentication.canRead()) {
      return "You do not have permission to use this command"
    }
    if (params.length != 1) {
      return "List failed - invalid params"
    }
    val msg = Helper.parse(params(0))
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
    sb.delete(sb.length() - 3, sb.length() - 1)
    return sb.toString
  }
}

/**
 * Deletes a file/directory...
 *
 * @author Joe Pritzel
 */
class RemoveMsg extends Message {
  def getOpcode = "REMOVE"
  def handle(params: Array[String]): String = {
    if (!Authentication.canWrite()) {
      return "You do not have permission to use this command"
    }
    if (params.length != 1) {
      return "Remove failed - invalid params"
    }
    val f = new File(Helper.parse(params(0)))
    Helper.deleteDir(f)
    return "Removed - " + params(0)
  }
}

/**
 * A class that has methods/functions that are common between FileMsgs
 *
 * @author Joe Pritzel
 */
private object Helper {
  def copy(src: File, dest: File) {
    if (src.isDirectory()) {
      copyDir(src, dest)
    } else {
      copyFile(src, dest)
    }
    def copyFile(src: File, dest: File) {
      val fos = new FileOutputStream(dest)
      fos getChannel () transferFrom (new FileInputStream(src) getChannel, 0, Long.MaxValue)
      fos close
    }
    def copyDir(src: File, dest: File) {
      if (!dest.exists()) {
        dest.mkdir();
      }

      val children = src.list;
      for (i <- 0 until children.length) {
        copy(new File(src, children(i)),
          new File(dest, children(i)))
      }
    }
  }

  def deleteDir(dir: File) {
    if (dir.isDirectory()) {
      val children = dir.list()
      for (i <- 0 until children.length) {
        deleteDir(new File(dir, children(i)));
      }
    }
    dir.delete();
  }

  def parse(msg: String) = {
    msg.replaceAll("\\(home\\)", System.getProperty("user.home")).replaceAll("\\(sep\\)", File.separator)
  }

}