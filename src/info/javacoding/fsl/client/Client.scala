package info.javacoding.fsl.client
import info.javacoding.fsl.client.net.UDPCommunication
import java.net.{ InetSocketAddress, InetAddress }

/**
 * The base of the client...
 *
 * @author Joe Pritzel
 */
object Client {

  var udp: UDPCommunication = null

  def main(args: Array[String]): Unit = {
    println("Enter the I.P. Address")
    val ip = readLine
    println("Enter the port")
    val port = Integer.parseInt(readLine)
    udp = new UDPCommunication(ip, port)
    for (ln <- io.Source.stdin.getLines) parseLine(ln)
  }

  def parseLine(msg: String) {
    msg.toUpperCase match {
      case "SHUTDOWN" => {
        udp.sendMessage(msg)
        println("Sent shutdown request")
      }

      case _ => println(udp.sendMessageWResp(msg))
    }
  }
}
