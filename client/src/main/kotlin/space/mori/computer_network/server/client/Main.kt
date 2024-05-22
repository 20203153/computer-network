package space.mori.computer_network.server.client

import java.io.*
import java.net.Socket
import java.util.*

fun main() {
    var _in: BufferedReader? = null
    var _out: BufferedWriter? = null
    var socket: Socket? = null

    val scanner = Scanner(System.`in`)

    try {
        Socket("localhost", 40000).also { socket = it }
        _in = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        _out = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))

        socket?.soTimeout = 3000

        while(true) {
            print("Send >>")
            val input = scanner.nextLine()

            if(input.equals("bye", true)) {
                _out.write("$input\n")
                _out.flush()
                break;
            }

            _out.write("$input\n")
            _out.flush()

            val inputMessage = _in.readLine()
            println("Server: $inputMessage")
        }
    } catch(e: IOException) {
        println("Error: ${e.message}")
    } finally {
        try {
            scanner.close()
            socket?.close()
        } catch (e: IOException) {
            println("Error: ${e.message}")
        }
    }
}