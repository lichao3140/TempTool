package com.example.temptool;

/**
 * 串口通信工具类
 */

public class SerialUtil {

    /**
     * 十六进制字符串转字节
     * @param hexString
     * @return
     */
    public static byte[] toByteArray(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     *  字节长度截取
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 字节数组转转hex字符串
     * @param inBytArr
     * @return
     */
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder=new StringBuilder();
        int j=inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    /**
     * 1字节转2个Hex字符
     * @param inByte
     * @return
     */
    private static String Byte2Hex(Byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }

    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     * @param bytearray
     * @return
     */
    public static String bytetoString(byte[] bytearray) {
        String result = "";
        char temp;

        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }
}
