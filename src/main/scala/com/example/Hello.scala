package com.example

import net.schmizz.sshj.SSHClient
import java.util.concurrent.TimeUnit
import java.io.{InputStreamReader, BufferedReader}
import scala.annotation.tailrec

trait Auth
case class PasswdAuth(user: String, pass: String) extends Auth
case class PubKeyAuth(userName: String) extends Auth

case class Connect(host: String, auth: Auth)

object Hello {
  
  def connect(con: Connect) {
    con match {
      case Connect(host, auth) => auth match {
        case PasswdAuth(user, pass) =>
          
        case PubKeyAuth(userName) =>
      }
    }
  }
  def main(args: Array[String]): Unit = {
    val client = new SSHClient()
    client.loadKnownHosts()
    client.connect(args(0))

    try {
      client.authPassword(args(1), args(2));
      val session = client.startSession();
      try {
        val cmd = session.exec(args(3));
        //cmd.join(1, TimeUnit.SECONDS);
        val reader = new BufferedReader(new InputStreamReader(cmd.getInputStream, "UTF-8"))
        
        readLine(reader) { line =>
          println(line)
        }
        
      } finally {
        session.close();
      }
    } finally {
      client.disconnect();
    }

    println("Hello, world!")
  }

  @tailrec
  def readLine(br: BufferedReader)(f: String => Unit) {
    Option(br.readLine) match {
      case Some(line) =>
        f(line)
        readLine(br)(f)
      case _ =>
    }
  }
}
