package space.mori.computer_network.server.dns

class DNSQuestionBody(
    val name: String,
    val QType: QTYPE,
    val QClass: QCLASS = QCLASS.INTERNET
) {
    enum class QTYPE(val value: Short) {
        A(1),
        NS(2),
        CNAME(5),
        SOA(6),
        WKS(11),
        PTR(12),
        MX(15),
        TXT(16),
        SIG(24),
        KEY(25),
        AAAA(28),
        SRV(33),
        RRSIG(46),
        NSEC(47),
        DNSKEY(48),
        ANY(255);

        companion object {
            fun fromInt(value: Int) = entries.first { it.value == value.toShort() }
        }
    }

    enum class QCLASS(val value: Short) {
        INTERNET(1),
        CHAOS(3),
        HESIOD(4),
        QCLASS_NONE(254),
        QCLASS_ANY(255);

        companion object {
            fun fromInt(value: Int) = entries.first { it.value == value.toShort() }
        }
    }

    fun toByteArray(): ByteArray {
        val domain = encodeDomainName(name)
        val arr = domain.copyOf(domain.size + 4)
        arr[arr.size - 4] = (QType.value.toInt() shl 8).toByte()
        arr[arr.size - 3] = QType.value.toByte()
        arr[arr.size - 2] = (QClass.value.toInt() shr 8).toByte()
        arr[arr.size - 1] = QClass.value.toByte()

        return arr
    }

    // by ChatGPT(GPT-4o)
    companion object {
        private fun encodeDomainName(domain: String): ByteArray {
            val parts = domain.split(".")
            val result = ByteArray(parts.sumOf { it.length + 1 } + 1) // 각 파트 길이 바이트 + 파트 길이 + null 바이트
            var index = 0

            for (part in parts) {
                result[index++] = part.length.toByte() // 길이 바이트
                part.toByteArray().forEach {
                    result[index++] = it.toByte() // 라벨 문자열
                }
            }

            result[index] = 0x00.toByte() // null 바이트로 끝남
            return result
        }

        fun decodeDomainName(arr: ByteArray, startIndex: Int = 0): Pair<String, Int> {
            val domainParts = mutableListOf<String>()
            var index = startIndex

            while (arr[index].toInt() != 0) {
                val length = arr[index].toInt()
                if ((length and 0xC0) == 0xC0) { // 압축된 도메인 이름
                    val pointer = ((length and 0x3F) shl 8) or arr[index + 1].toInt()
                    val (pointerName, _) = decodeDomainName(arr, pointer)
                    domainParts.add(pointerName)
                    index += 2
                    return Pair(domainParts.joinToString("."), index)
                } else {
                    index++
                    val part = arr.copyOfRange(index, index + length)
                    domainParts.add(String(part))
                    index += length
                }
            }

            return Pair(domainParts.joinToString("."), index + 1)
        }

        fun fromByteArray(arr: ByteArray): DNSQuestionBody {
            val (domain, index) = decodeDomainName(arr)
            var currentIndex = index

            val QType = (arr[currentIndex].toUByte().toInt() shl 8) or arr[currentIndex + 1].toUByte().toInt()
            currentIndex += 2

            val QClass = (arr[currentIndex].toUByte().toInt() shl 8) or arr[currentIndex + 1].toUByte().toInt()

            return DNSQuestionBody(domain, QTYPE.fromInt(QType), QCLASS.fromInt(QClass))
        }

        fun ipv4ToReverse(ip: String): String {
            return ip.split(".").reversed().joinToString(".") + ".in-addr.arpa"
        }

        fun ipv6ToReverse(ip: String): String {
            return ip.split(":")
                .joinToString("") { it.padStart(4, '0') }
                .reversed()
                .map { it.toString() }
                .joinToString(".") + ".ip6.arpa"
        }

        fun test(): DNSQuestionBody {
            val body = DNSQuestionBody("www.google.com", QTYPE.A)
            body.toByteArray().forEach {
                print(Integer.toHexString(it.toInt()).padStart(2, '0'))
            }

            return body
        }
    }
}