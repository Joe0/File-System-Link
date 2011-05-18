package info.javacoding.fsl.client.net
import java.net.InetSocketAddress
import java.net.InetAddress
import java.net.DatagramSocket
import java.net.DatagramPacket

/**
 * This class communicates via UDP
 *
 * @author Joe Pritzel
 */
class UDPCommunication(ip: String, port: Int) {

  val address = new InetSocketAddress(InetAddress.getByName(ip), port)

  def sendMessageWResp(msg: String): String = {
    val socket = new DatagramSocket()
    socket.send(new DatagramPacket(msg.getBytes, msg.length, address))
    val message = new Array[Byte](1024)
    val packet = new DatagramPacket(message, message.length)
    socket.setSoTimeout(3000)
    socket.receive(packet)
    socket.close
    new String(packet.getData)
  }

  def sendMessage(msg: String) = {
    val socket = new DatagramSocket()
    socket.send(new DatagramPacket(msg.getBytes, msg.length, address))
  }
}