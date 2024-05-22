package space.mori.computer_network.server.dns

// https://stackoverflow.com/a/64408275/11516704
fun getBit(value: Int, position: Int): Int {
    return (value shr position) and 1;
}

data class DNSHeader(
    val tid: Int,
    val Qr: QR,
    val Opcode: OPCODE,
    val TC: Int,
    val RD: Int,
    val RCode: RCODE = RCODE.NO_ERROR,
    val AA: Int = 0,
    val RA: Int = 0,
    val questions: Int = 1,
    val answers: Int = 0,
    val authority: Int = 0,
    val additional: Int = 0,
) {
    enum class QR(val value: Int) {
        QUESTION(0),
        ANSWER(1)
    }

    enum class OPCODE(val value: Int) {
        QUERY(0),
        IQUERY(1),
        STATUS(2),
        NOTIFY(4),
        UPDATE(5),
        DSO(6)
    }

    enum class RCODE(val value: Int) {
        NO_ERROR(0),
        FORMAT_ERROR(1),
        SERVER_ERROR(2),
        NAME_ERROR(3),
        NOT_IMPLEMENTED_ERROR(4),
        REFUSED(5),
        YXDOMAIN(6),
        XRPSET(7),
        NOTAUTH(8),
        NOTZONE(9),
        DSOTYPENI(11),
        BADVERS(16),
        BADKEY(17),
        BADTIME(18),
        BADMODE(19),
        BADNAME(20),
        BADALG(21),
        BADTRUNC(22),
        BADCOOKIE(23)
    }

    fun toByteArray(): ByteArray {
        val arr = ByteArray(12)
        arr[0] = (tid shr 8).toByte()
        arr[1] = tid.toByte()

        val flags = (
            (Qr.value shl 15) or
            (Opcode.value shl 11) or
            (AA shl 10) or
            (TC shl 9) or
            (RD shl 8) or
            (RA shl 7) or
            (RCode.value)
        )

        arr[2] = (flags shr 8).toByte()
        arr[3] = flags.toByte()

        arr[4] = (questions shr 8).toByte()
        arr[5] = questions.toByte()
        arr[6] = (answers shr 8).toByte()
        arr[7] = answers.toByte()
        arr[8] = (authority shr 8).toByte()
        arr[9] = authority.toByte()
        arr[10] = (additional shr 8).toByte()
        arr[11] = additional.toByte()

        return arr
    }

    // by ChatGPT(GPT-4o)
    companion object {
        fun fromByteArray(arr: ByteArray): DNSHeader {
            require(arr.size == 12) { "Invalid array size" }

            val tid = ((arr[0].toInt() and 0xFF) shl 8) or (arr[1].toInt() and 0xFF)
            val flags = ((arr[2].toInt() and 0xFF) shl 8) or (arr[3].toInt() and 0xFF)

            val QR = QR.entries[(flags shr 0) and 1]
            val Opcode = OPCODE.entries[(flags shr 1) and 0xF]
            val AA = (flags shr 5) and 1
            val TC = (flags shr 6) and 1
            val RD = (flags shr 7) and 1
            val RA = (flags shr 8) and 1
            val RCode = RCODE.entries[(flags shr 12) and 0xF]

            val questions = ((arr[4].toInt() and 0xFF) shl 8) or (arr[5].toInt() and 0xFF)
            val answers = ((arr[6].toInt() and 0xFF) shl 8) or (arr[7].toInt() and 0xFF)
            val authority = ((arr[8].toInt() and 0xFF) shl 8) or (arr[9].toInt() and 0xFF)
            val additional = ((arr[10].toInt() and 0xFF) shl 8) or (arr[11].toInt() and 0xFF)


            return DNSHeader(tid, QR, Opcode, TC, RD, RCode, AA, RA, questions, answers, authority, additional)
        }

        fun test(): DNSHeader {
            val header = DNSHeader(129, QR.QUESTION, OPCODE.QUERY, 0, 1)

            header.toByteArray().forEach {
                print(Integer.toHexString(it.toInt()).padStart(2, '0'))
            }

            return header
        }
    }

    operator fun plus(elements: DNSQuestionBody): ByteArray {
        return this.toByteArray() + elements.toByteArray()
    }
}