package space.mori.computer_network.server.dns

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DNSResponseBody(
    val name: String,
    val QType: DNSQuestionBody.QTYPE,
    val QClass: DNSQuestionBody.QCLASS,
    val TTL: Int,
    val dataLength: UInt,
    val data: ByteArray
) {

    companion object {
        fun fromByteArray(arr: ByteArray, startIndex: Int): Pair<DNSResponseBody, Int> {
            var index = startIndex

            val (name, nameEndIndex) = DNSQuestionBody.decodeDomainName(arr, index)
            index = nameEndIndex

            val QType = ((arr[index].toInt() and 0xFF) shl 8) or (arr[index+1].toInt() and 0xFF)
            index += 2;
            val QClass = ((arr[index].toInt() and 0xFF) shl 8) or (arr[index+1].toInt() and 0xFF)
            index += 2;
            val TTL = ByteBuffer.wrap(arr, index, 4).order(ByteOrder.BIG_ENDIAN).int
            index += 4
            val dataLength = ((arr[index].toInt() and 0xFF) shl 8) or (arr[index+1].toInt() and 0xFF)
            index += 2

            val data = arr.copyOfRange(index, index + dataLength.toInt())
            index += dataLength.toInt()

            return Pair(
                DNSResponseBody(
                    name,
                    DNSQuestionBody.QTYPE.fromInt(QType),
                    DNSQuestionBody.QCLASS.fromInt(QClass),
                    TTL,
                    dataLength.toUInt(),
                    data
                ),
            index)
        }
    }
}