package space.mori.computer_network.server

import space.mori.computer_network.server.dns.DNSQuestionBody
import space.mori.computer_network.server.dns.requestDNS
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*

fun main() {
    var _in: BufferedReader? = null
    var _out: BufferedWriter? = null
    var listener: ServerSocket? = null
    var socket: Socket? = null

    var tid = 1

    val scanner = Scanner(System.`in`)

    try {
        ServerSocket(40000).also { listener = it }
        println("Waiting connection...")
        listener?.accept().also { socket = it }

        _in = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        _out = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))

        while (true) {
            val line = _in.readLine() ?: continue

            if (line.startsWith("bye", ignoreCase = true)) {
                println("Client sent bye, connection closed.")
                break
            }

            println("Client: $line")

            val domain = if (line.startsWith("N : ")) {
                println("Normal Query with ${line.drop(4)}")
                requestDNS(line.drop(4), DNSQuestionBody.QTYPE.A, tid=tid++)
            } else if (line.startsWith("R : ")) {
                println("Reverse Query with ${line.drop(4)}")
                requestDNS(DNSQuestionBody.ipv4ToReverse(line.drop(4)), DNSQuestionBody.QTYPE.PTR, tid=tid++)
            } else {
                "Wrong format"
            } ?: "Not found"

            _out.write("$domain\n")
            _out.flush()
        }
    } catch (e: IOException) {
        println("Error: ${e.message}")
    } catch (e: SocketTimeoutException) {
        println("Error: ${e.message}")
    } finally {
        try {
            scanner.close()
            socket?.close()
            listener?.close()
        } catch (e: IOException) {
            println("Error: ${e.message}")
        }
    }
}