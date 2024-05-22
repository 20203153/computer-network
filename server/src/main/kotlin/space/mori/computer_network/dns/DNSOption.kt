package space.mori.computer_network.dns

import java.nio.ByteBuffer

class DNSOption() {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(11)
        buffer.put(0.toByte()) // Name field (root)
        buffer.put(0x00.toByte()) // OPT record type (lower 8 bits)
        buffer.put(0x29.toByte()) // OPT record type (upper 8 bits)
        buffer.put(0x10.toByte()) // UDP payload size (lower 8 bits)
        buffer.put(0x00.toByte()) // UDP payload size (upper 8 bits)
        buffer.put(0x00.toByte()) // Extended RCODE and flags (upper 8 bits)
        buffer.put(0x80.toByte()) // DO bit (lower 8 bits) (0x80 = 10000000)
        buffer.put(0x00.toByte()) // EDNS version
        buffer.put(0x00.toByte()) // Z (extended flags)
        buffer.put(0x00.toByte()) // Z (extended flags)
        buffer.put(0x00.toByte()) // Data length

        return buffer.array()
    }
}
