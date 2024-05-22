package space.mori.computer_network

import space.mori.computer_network.dns.DNSQuestionBody
import space.mori.computer_network.dns.requestDNS

fun main() {
    val dns = requestDNS("mori.space", DNSQuestionBody.QTYPE.A)
    println(dns)
}