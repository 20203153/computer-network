package space.mori.computer_network.server.dns

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

fun requestDNS(domain: String, type: DNSQuestionBody.QTYPE, server: String = "1.1.1.1", timeout: Int = 5, silent: Boolean = true, tid: Int = 5000): String {
    val dnsHeader = DNSHeader(tid, DNSHeader.QR.QUESTION, DNSHeader.OPCODE.QUERY, 0, 1)
    val dnsQuestionBody = DNSQuestionBody(domain, type)

    val dnsQueryPacket = (dnsHeader + dnsQuestionBody)

    var result: String = ""

    val dsoc = DatagramSocket()
    val ia = InetAddress.getByName(server)
    val dp = DatagramPacket(dnsQueryPacket, dnsQueryPacket.size, ia, 53)

    dsoc.soTimeout = timeout * 1000 // timeout n secs

    try {
        dsoc.send(dp)
        val buffer = ByteArray(512)
        val response = DatagramPacket(buffer, buffer.size)
        dsoc.receive(response)

        dsoc.close()

        if(!silent) println("\nParsed DNSHeader from response")
        val responseHeader = DNSHeader.fromByteArray(buffer.sliceArray(0 until 12))
        if(!silent) {
            println("Transaction ID: ${responseHeader.tid}")
            println("OPCode: ${responseHeader.Opcode}")
            println("RCode: ${responseHeader.RCode}")
            println("Questions: ${responseHeader.questions}")
            println("Answer RRs: ${responseHeader.answers}")
            println("Authority RRs: ${responseHeader.authority}")
            println("Additional RRs: ${responseHeader.additional}")
        }

        var nextIndex = 12

        if(!silent) println("\nQuestions section:")
        for (i in 0 until responseHeader.questions) {
            val question = DNSQuestionBody.fromByteArray(buffer.sliceArray(nextIndex until buffer.size))
            nextIndex += question.toByteArray().size
            if(!silent) println("$i: Question: ${question.name}")
        }

        if(!silent) println("\nAnswer Section:")
        for (i in 0 until responseHeader.answers) {
            val (responseBody, idx) = DNSResponseBody.fromByteArray(buffer, nextIndex)
            if(!silent) {
                println("Parsed DNSResponseBody $i from response")
                println("Name: ${responseBody.name}")
                println("QType: ${responseBody.QType}")
                println("QClass: ${responseBody.QClass}")
                println("TTL: ${responseBody.TTL}")
                println("Data Length: ${responseBody.dataLength}")
            }

            val data = when (responseBody.QType) {
                DNSQuestionBody.QTYPE.A -> responseBody.data.joinToString(".") {
                    it.toUByte().toString(10).padStart(2, '0')
                }
                DNSQuestionBody.QTYPE.AAAA -> responseBody.data.joinToString(" ") {
                    it.toUByte().toString(16).padStart(2, '0')
                }
                DNSQuestionBody.QTYPE.PTR -> DNSQuestionBody.decodeDomainName(responseBody.data).first

                else -> "";
            }
            if(!silent) println("$i: $data")

            result = result.ifEmpty { data }

            nextIndex = idx
        }
    } catch(e: SocketTimeoutException) {
        throw Exception("Socket Timeout: $domain on SERVER $server")
    } finally {
        dsoc.close()
    }

    return result
}