# computer network

### Implements local DNS client and server

- [x] Make the local DNS client server program.
- [x] The server has DNS Query logics
- [ ] The server has a short table made of two columns, domain nad ip address
    - The normal request is a string in the format "N : domain name"
    - The reverse request is in the format "R: ip address(ipv4 only)"
    - The server responds with ether the IP address or the message "Not found."
- Any programming language is OK (implements with Kotlin


## Function block desription

### Server

```kotlin
    var _in: BufferedReader? = null // Application-layer Input buffer
    var _out: BufferedWriter? = null // Application-layer Output buffer
    var listener: ServerSocket? = null // SocketServer
    var socket: Socket? = null // Socket objects
    
    var tid = 1
    
    val scanner = Scanner(System.`in`) // Java Scanner object. handle keyboard inputs
    try {
        ServerSocket(40000).also { listener = it } // make SocketServer with port 40000
        println("Waiting connection...")
        listener?.accept().also { socket = it } // SocketServer makes client connections

        _in = BufferedReader(InputStreamReader(socket!!.getInputStream())) // Initialize input buffer
        _out = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())) // Initialize output buffer

        while (true) {
            val line = _in.readLine() ?: continue // if input buffer has line? read. else continue

            if (line.startsWith("bye", ignoreCase = true)) { // if client sent "bye" connection close
                println("Client sent bye, connection closed.")
                break
            }

            println("Client: $line")

            val domain = if (line.startsWith("N : ")) { // if client sent "N Format"
                println("Normal Query with ${line.drop(4)}")
                requestDNS(line.drop(4), DNSQuestionBody.QTYPE.A, tid=tid++) // DNS Query with A record
            } else if (line.startsWith("R : ")) { // if client sent "R Format
                println("Reverse Query with ${line.drop(4)}")
                requestDNS(DNSQuestionBody.ipv4ToReverse(line.drop(4)), DNSQuestionBody.QTYPE.PTR, tid=tid++) // DNS Query with PTR record
            } else {
                "Wrong format" // else print Wrong format
            } ?: "Not found"

            _out.write("$domain\n") // add message with output buffer
            _out.flush() // send output buffer to client
        }
    } catch (e: IOException) {
        println("Error: ${e.message}")
    } catch (e: SocketTimeoutException) {
        println("Error: ${e.message}")
    } finally {
        try { // if server is closed
            scanner.close() // scanner destruct
            socket?.close() // socket destruct
            listener?.close() // SocketServer destruct
        } catch (e: IOException) {
            println("Error: ${e.message}")
        }
    }
```

### Client
```kotlin
    var _in: BufferedReader? = null // Application-layer Input buffer
    var _out: BufferedWriter? = null // Application-layer Output buffer
    var socket: Socket? = null // Socket objects

    val scanner = Scanner(System.`in`) // Java Scanner object. handle keyboard inputs

    try {
        Socket("localhost", 40000).also { socket = it } // connect with SocketServer
        _in = BufferedReader(InputStreamReader(socket!!.getInputStream())) // initialize input buffer
        _out = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())) // initialize output buffer

        socket?.soTimeout = 3000 // socket timeout. if socket has no activity, close socket

        while(true) {
            print("Send >>")
            val input = scanner.nextLine() // keybard input handle.

            if(input.equals("bye", true)) { // if "bye" inputed, exit
                _out.write("$input\n") // output buffer write
                _out.flush() // socket write
                break;
            }

            _out.write("$input\n") // output buffer write
            _out.flush() // socket write

            val inputMessage = _in.readLine() // get input messages
            println("Server: $inputMessage")
        }
    } catch(e: IOException) {
        println("Error: ${e.message}")
    } finally {
        try {
            scanner.close() // destruct scanner
            socket?.close() // destruct socket.
        } catch (e: IOException) {
            println("Error: ${e.message}")
        }
    }
```

## How to run?

1. `java -jar server.jar`
2. `java -jar client.jar`
3. on client, Input N or R format with IPv4 (this program can handle only IPv4)

## Files
1. LocalDNS_Server_최진우_20203153.jar
2. LocalDNS_Client_최진우_20203153.jar
3. HowToRun-LocalDNS.pdf

**LocalDNS.txt isn't required. It can handle any domain, IPs.**

## Github repository

[https://github.com/20203153/computer-network](https://github.com/20203153/computer-network)

END.