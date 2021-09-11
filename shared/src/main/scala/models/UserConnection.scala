package com.github.gwyddie.chat.shared.models

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner

case class UserConnection(username: String, socket: Socket) {
  protected var closed = true
  protected var _in: Scanner = getIn
  protected var _out: PrintWriter = getOut

  def close(): Unit = {
    if (!closed) {
      in.close()
      out.close()
      socket.close()
    }
    closed = true
  }

  def in: Scanner = _in

  def out: PrintWriter = _out

  private def getIn: Scanner = new Scanner(socket.getInputStream)

  private def getOut: PrintWriter = new PrintWriter(socket.getOutputStream, true)
}
