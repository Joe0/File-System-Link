package info.javacoding.fsl.server.net

import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory
import org.jboss.netty.bootstrap.ConnectionlessBootstrap
import org.jboss.netty.handler.codec.string.StringEncoder
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.handler.codec.string.StringDecoder
import org.jboss.netty.channel._
import grizzled.slf4j.Logging
import info.javacoding.fsl.server.Server
import java.net.InetSocketAddress

/**
 * The core of the networking.<br>
 * Binds the port and listens for connections/messages...
 * 
 * @author Joe Pritzel
 */
class Networking extends Logging {
  val factory = new NioDatagramChannelFactory(Server.threadPool)
  val bootstrap = new ConnectionlessBootstrap(factory)

  MessageManager.init

  bootstrap.setPipelineFactory(new ChannelPipelineFactory() with Logging {
    override def getPipeline() = {
      Channels.pipeline(new StringEncoder(
        CharsetUtil.ISO_8859_1), new StringDecoder(
        CharsetUtil.ISO_8859_1), new SimpleChannelUpstreamHandler {
        @throws(classOf[Exception])
        override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
          val msg = { e.getMessage }.asInstanceOf[String]
          info("Recieved - " + msg)
          MessageManager.handle(e, msg.split(" "))
        }
      })
    }
  })

  bootstrap.setOption("broadcast", "true")
  bootstrap.setOption("receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory(1024))
  bootstrap.bind(new InetSocketAddress(48620))
  info("Port is bound")
}