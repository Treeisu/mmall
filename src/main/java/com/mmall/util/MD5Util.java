package com.mmall.util;

import java.security.MessageDigest;

public class MD5Util {
	//工具类内部使用的加密算法1
	private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }
	//工具类内部使用的加密算法2
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    /**
     * 
     * @Title: MD5Encode
     * @Description: TODO 调用此方法，返回大写的MD5【这里设置成了私有方法不开放给外部使用】
     * @param @param origin 需要加密的原字符串
     * @param @param charsetname  加密算法使用的字符集
     * @param @return    
     * @return String    
     * @throws
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }
    /**
     * 
     * @Title: MD5EncodeUtf8
     * @Description: TODO  调用加密方法，并强制使用utf-8，提供给外部调用
     * @param @param origin
     * @param @return    
     * @return String    
     * @throws
     */
    public static String MD5EncodeUtf8(String origin) {        
        return MD5Util.MD5Encode(origin, "utf-8");
    }
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
