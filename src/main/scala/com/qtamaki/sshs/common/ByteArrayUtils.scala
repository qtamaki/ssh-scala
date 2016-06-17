package com.qtamaki.sshs.common


/** Utility functions for byte arrays. */
object ByteArrayUtils {

    val digits:Array[Char] = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * Check whether some part or whole of two byte arrays is equal, for <code>length</code> bytes starting at some
     * offset.
     *
     * @param a1
     * @param a1Offset
     * @param a2
     * @param a2Offset
     * @param length
     *
     * @return <code>true</code> or <code>false</code>
     */
    def equals(a1:Array[Byte], a1Offset:Int, a2:Array[Byte], a2Offset:Int, length:Int):Boolean = {
        if (a1.length < a1Offset + length || a2.length < a2Offset + length)
            return false;
        var l = length
        var i = 0
        while (l > 0) {
          l -= 1
          if (a1(a1Offset+i) != a2(a2Offset+i))
            return false;
          i += 1
        }
        return true;
    }

    /**
     * Get a hexadecimal representation of a byte array starting at <code>offset</code> index for <code>len</code>
     * bytes, with each octet separated by a space.
     *
     * @param array
     * @param offset
     * @param len
     *
     * @return hex string, each octet delimited by a space
     */
    def printHex(array:Array[Byte], offset:Int, len:Int):String = {
        val sb = new StringBuilder();
        
        (0 until len).foreach { i => 
            val b = array(offset + i)
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(digits(b >> 4 & 0x0F));
            sb.append(digits(b & 0x0F));
        }
        return sb.toString();
    }

    /**
     * Get the hexadecimal representation of a byte array.
     *
     * @param array
     *
     * @return hex string
     */
    def toHex(array:Array[Byte]):String = {
        return toHex(array, 0, array.length);
    }

    /**
     * Get the hexadecimal representation of a byte array starting at <code>offset</code> index for <code>len</code>
     * bytes.
     *
     * @param array
     * @param offset
     * @param len
     *
     * @return hex string
     */
    def toHex(array:Array[Byte], offset:Int, len:Int):String = {
        val sb = new StringBuilder();
        (0 until len).foreach { i => 
            val b = array(offset + i);
            sb.append(digits(b >> 4 & 0x0F));
            sb.append(digits(b & 0x0F));
        }
        return sb.toString();
    }

}
