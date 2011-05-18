package info.javacoding.fsl.server.net
import scala.collection.immutable.HashMap
import scala.io.Source
import grizzled.slf4j.Logging
import org.jboss.netty.channel.MessageEvent
import info.javacoding.fsl.server.Server
import java.util.Arrays

/**
 * A message...
 *
 * @author Joe Pritzel
 */
trait Message {
  def getOpcode: String
  def handle(params: Array[String]): String
}

/**
 * Manages messages; specifically has a map of the message names to the class.<br>
 * Will send a response to messages if given one.
 *
 * @author Joe Pritzel
 */
object MessageManager extends Logging {
  var msgHandlers = new HashMap[String, Message]()

  def init {
    for (line <- Source.fromFile("Messages.conf").getLines) {
      val handlerClass = Class.forName(line)
      val handlerInstance = handlerClass.newInstance().asInstanceOf[Message]
      msgHandlers = msgHandlers + ((handlerInstance.getOpcode, handlerInstance))
      info("Loaded messages: " + handlerInstance.getOpcode)
    }
  }

  private val ls = System.getProperty("line.separator")

  def handle(e: MessageEvent, args: Array[String]) {
    Server.threadPool.execute(new Runnable() {

      def run() {
        val opcode = args(0).toUpperCase()
        val handler = msgHandlers.get(opcode).get;
        if (handler == null) {
          return ;
        }

        val params = Arrays.copyOfRange(args, 1, args.length);
        val response = handler.handle(params);
        info("response - " + response)
        if (response != null && response.length > 0) {
          e.getChannel().write(response + ls, e.getRemoteAddress);
        }
      }

    });
  }
}