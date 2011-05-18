package info.javacoding.fsl.server.net.msg
import info.javacoding.fsl.server.net.Message

/**
 * Shuts down the FSL
 * 
 * @author Joe Pritzel
 */
class ShutdownMsg extends Message {
  def getOpcode = "SHUTDOWN"
  def handle(params: Array[String]): String = {
	  System.exit(0)
	  ""
  }
}