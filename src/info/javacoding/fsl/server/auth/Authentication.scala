package info.javacoding.fsl.server.auth

import info.javacoding.fsl.server.net.Message

/**
 * Stores if anyone has read/write access.
 *
 * @author Joe Pritzel
 */
object Authentication {
  val canRead = new Property[Boolean](false)
  val canWrite = new Property[Boolean](false)
}

/**
 * Checks credentials.xml for passwords to determine what access should be given.
 *
 * @author Joe Pritzel
 */
class AuthMsg extends Message {
  import scala.xml._
  def getOpcode = "AUTH"
  def handle(params: Array[String]): String = {
    if (params.length != 1) {
      return "Auth failed - Invalid params"
    }
    val someXML = XML.load("credentials.xml")

    Authentication.canRead := false
    for (pass <- (someXML \ "read" \ "pass"); if (pass.text == params(0)))
      Authentication.canRead := true

    Authentication.canWrite := false
    for (pass <- (someXML \ "write" \ "pass"); if (pass.text == params(0)))
      Authentication.canWrite := true

    return "Read access: " + Authentication.canRead() + "\nWrite access: " + Authentication.canWrite()
  }
}