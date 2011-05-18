package info.javacoding.fsl.server
import java.util.concurrent.Executors
import grizzled.slf4j.Logging
import info.javacoding.fsl.server.net.Networking
/**
 * Creates a basic server...
 * 
 * @author Joe Pritzel
 */
object Server extends Logging {
  val threadPool = Executors.newCachedThreadPool
  def main(args: Array[String]) {
    new Networking()
  }
}