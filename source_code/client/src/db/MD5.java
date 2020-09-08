/*
MIT License

Copyright (c) 2020 Who Write Code

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class MD5 {
    
    // Define standard matrix 4x4
    static final int S11 = 7, S12 = 12, S13 = 17, S14 = 22;
    static final int S21 = 5, S22 = 9, S23 = 14, S24 = 20;
    static final int S31 = 4, S32 = 11, S33 = 16, S34 = 23;
    static final int S41 = 6, S42 = 10, S43 = 15, S44 = 21;

    // Determined PADDING by final byte array RFC1321 standard
    static final byte[] PADDING =
    {
        -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private long[] state = new long[4];
    private byte[] buffer = new byte[64];
    private long[] count = new long[2];
    
    public String resultStr;
    public String resultTimeMD5Str;
    
    public byte[] digest = new byte[16];
    public char bigger;
    
    public String getMD5ofStr(String in){
        md5Init();
        md5Update(in.getBytes(), in.length());
        md5Final();
        for (int i = 0; i < 16; i++){
            resultStr += byteToHEX(digest[i]);
            // System.out.println(resultStr);
        }
        return resultStr;
    }
    
    public String MD5Digest(String in){
        md5Init();
        md5Update(in.getBytes(), in.length());
        md5Final();
        for (int i = 0; i < 16; i++){
            resultStr += byteToHEX(digest[i]);
            // System.out.println(resultStr);
        }
        StringBuilder stringbuilder = new StringBuilder(resultStr);
        StringBuilder StrBuilder = getResult(stringbuilder,  0,  31);
        return StrBuilder.toString();
    }
    
    public String getMD5WithTimeofStr(String in){
        md5Init();
        md5Update(in.getBytes(), in.length());
        md5Final();
        for (int i = 0; i < 16; i++){
            resultStr += byteToHEX(digest[i]);
        }
        StringBuilder stringbuilder = new StringBuilder(resultStr);
        StringBuilder StrBuilder = getResult(stringbuilder,  0,  31);
        // StringBuilder StrBuilder = new StringBuilder(resultStr);
        return InsertTime(StrBuilder);
    }
    
    public StringBuilder getResult(StringBuilder stringbuilder,  int start,  int end){
        int middle = (start + end)/2;
    	if((end - start) >7){
    		if((end - start) == 31){
//    		System.out.println("31");
            	if(stringbuilder.charAt(start) > stringbuilder.charAt(end))
            		bigger = stringbuilder.charAt(start);
            	else
            		bigger = stringbuilder.charAt(end);
    		}
//        	System.out.println("middle:" + middle);
        	if(stringbuilder.charAt(start) > stringbuilder.charAt(end))
        		stringbuilder.replace(start, start+1, stringbuilder.substring(end,end+1)  );
        	else
        		stringbuilder.replace(end, end+1, stringbuilder.substring(start,start+1)  );
    		getResult(stringbuilder, start, middle);
    		getResult(stringbuilder, start, middle);
    	}
    	return stringbuilder;        
    }
    
    public String InsertTime(StringBuilder StrBuilder){
    	
    	String current_time = new Date().toString();
    	
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] byteArray = current_time.getBytes();
			md.update(byteArray);
			byte [] tmp = md.digest();
	        StringBuilder stringbuilder = new StringBuilder();
	        for (byte b:tmp) {
	        	stringbuilder.append(Integer.toHexString(b&0xff));
	        }
	        resultTimeMD5Str = stringbuilder.toString();
//	        System.out.println(stringbuilder);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
		}
    	
    	int distance = bigger - '0';
//    	System.out.println("time of md5:"+resultTimeMD5Str+"...");
//    	return resultTimeMD5Str;
//    	String timeBase64 = new BASE64Encoder().encode(current_time.getBytes());
//    	System.out.println("lenth:"+timeBase64.length());
//    	System.out.println(bigger);
    	System.out.print(StrBuilder.substring(0 , distance)+"*");
    	System.out.println(StrBuilder.substring(distance,31));
    	return StrBuilder.substring(0 , distance)+resultTimeMD5Str+StrBuilder.substring(distance,32);
    }
    
    public MD5(){
        md5Init();
    }
    
    private void md5Init(){
        state[0] = 0x67452301L;
        state[1] = 0xefcdab89L;
        state[2] = 0x98badcfeL;
        state[3] = 0x10325476L;
        count[0] = count[1] = 0L;
        resultStr = "";
        resultTimeMD5Str = "";
        for (int i = 0; i < 16; i++){
            digest[i] = 0;
        }
    }
    
    private long F(long x, long y, long z){
        return (x & y) | ((~x) & z);
    }

    private long G(long x, long y, long z){
        return (x & z) | (y & (~z));
    }

    private long H(long x, long y, long z){
        return x ^ y ^ z;
    }

    private long I(long x, long y, long z){
        return y ^ (x | (~z));
    }

    private long FF(long a, long b, long c, long d, long x, long s, long ac){
        a += F(b, c, d) + x + ac;
        a = ((int) a << s) | ((int) a >>> (32 - s)); 
        a += b;
        return a;
    }

    private long GG(long a, long b, long c, long d, long x, long s, long ac){
        a += G(b, c, d) + x + ac;
        a = ((int) a << s) | ((int) a >>> (32 - s)); 
        a += b;
        return a;
    }

    private long HH(long a, long b, long c, long d, long x, long s, long ac){
        a += H(b, c, d) + x + ac;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }

    private long II(long a, long b, long c, long d, long x, long s, long ac){
        a += I(b, c, d) + x + ac;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private void md5Update(byte[] input, int inputLen){
        int i, index, partLen;
        byte[] block = new byte[64];
        index = (int)(count[0] >>> 3) & 0x3F;
        if ((count[0] += (inputLen << 3)) < (inputLen << 3)){
            count[1]++;
        }
        count[1] += (inputLen >>> 29);
        partLen = 64 - index;
        if (inputLen >= partLen){
            md5Memcpy(buffer, input,index, 0, partLen);
            md5Transform(buffer);
            for (i = partLen; i + 63 < inputLen; i += 64)
            {
                md5Memcpy(block, input, 0, i, 64);
                md5Transform(block);
            }
            index = 0;
        }else {
            i = 0;
        }
        md5Memcpy(buffer, input, index, i, inputLen - i);
    }
    
    private void md5Final(){
        byte[] bits = new byte[8];
        int index, padLen;
        Encode(bits, count, 8);
        index = (int) (count[0] >>> 3) & 0x3f;
        padLen = (index < 56) ? (56 - index) :(120 - index);
        md5Update(PADDING, padLen);
        md5Update(bits, 8);
        Encode(digest, state, 16);        
    }
     
    private void md5Memcpy(byte[] output, byte[] input, int outpos, int inpos, int partLen){
        int i;
        for (i = 0; i < partLen; i++)
        {
            output[outpos + i] = input[inpos + i];
        }
    }

    private void md5Transform(byte block[])
    {
        long a = state[0], b = state[1], c = state[2], d = state[3];
        long[] x = new long[16];
        Decode(x, block, 64);

        
        a = FF(a, b, c, d, x[0], S11, 0xd76aa478L); /* 1 */
        d = FF(d, a, b, c, x[1], S12, 0xe8c7b756L); /* 2 */
        c = FF(c, d, a, b, x[2], S13, 0x242070dbL); /* 3 */
        b = FF(b, c, d, a, x[3], S14, 0xc1bdceeeL); /* 4 */
        a = FF(a, b, c, d, x[4], S11, 0xf57c0fafL); /* 5 */
        d = FF(d, a, b, c, x[5], S12, 0x4787c62aL); /* 6 */
        c = FF(c, d, a, b, x[6], S13, 0xa8304613L); /* 7 */
        b = FF(b, c, d, a, x[7], S14, 0xfd469501L); /* 8 */
        a = FF(a, b, c, d, x[8], S11, 0x698098d8L); /* 9 */
        d = FF(d, a, b, c, x[9], S12, 0x8b44f7afL); /* 10 */
        c = FF(c, d, a, b, x[10], S13, 0xffff5bb1L); /* 11 */
        b = FF(b, c, d, a, x[11], S14, 0x895cd7beL); /* 12 */
        a = FF(a, b, c, d, x[12], S11, 0x6b901122L); /* 13 */
        d = FF(d, a, b, c, x[13], S12, 0xfd987193L); /* 14 */
        c = FF(c, d, a, b, x[14], S13, 0xa679438eL); /* 15 */
        b = FF(b, c, d, a, x[15], S14, 0x49b40821L); /* 16 */


        a = GG(a, b, c, d, x[1],  S21, 0xf61e2562L); /* 17 */
        d = GG(d, a, b, c, x[6],  S22, 0xc040b340L); /* 18 */
        c = GG(c, d, a, b, x[11], S23, 0x265e5a51L); /* 19 */
        b = GG(b, c, d, a, x[0],  S24, 0xe9b6c7aaL); /* 20 */
        a = GG(a, b, c, d, x[5],  S21, 0xd62f105dL); /* 21 */
        d = GG(d, a, b, c, x[10], S22, 0x2441453L); /*22 */
        c = GG(c, d, a, b, x[15], S23, 0xd8a1e681); /* 23 */
        b = GG(b, c, d, a, x[4],  S24, 0xe7d3fbc8L); /* 24 */
        a = GG(a, b, c, d, x[9],  S21, 0x21e1cde6L); /* 25 */
        d = GG(d, a, b, c, x[14], S22, 0xc33707d6L); /* 26 */
        c = GG(c, d, a, b, x[3],  S23, 0xf4d50d87L); /* 27 */
        b = GG(b, c, d, a, x[8],  S24, 0x455a14edL); /* 28 */
        a = GG(a, b, c, d, x[13], S21, 0xa9e3e905L); /* 29 */
        d = GG(d, a, b, c, x[2],  S22, 0xfcefa3f8L); /* 30 */
        c = GG(c, d, a, b, x[7],  S23, 0x676f02d9L); /* 31 */
        b = GG(b, c, d, a, x[12], S24, 0x8d2a4c8aL); /* 32 */


        a = HH(a, b, c, d, x[5],    S31, 0xfffa3942L); /* 33 */
        d = HH(d, a, b, c, x[8],    S32, 0x8771f681L); /* 34 */
        c = HH(c, d, a, b, x[11],   S33, 0x6d9d6122L); /* 35 */
        b = HH(b, c, d, a, x[14],   S34, 0xfde5380cL); /* 36 */
        a = HH(a, b, c, d, x[1],    S31, 0xa4beea44L); /* 37 */
        d = HH(d, a, b, c, x[4],    S32, 0x4bdecfa9L); /* 38 */
        c = HH(c, d, a, b, x[7],    S33, 0xf6bb4b60L); /* 39 */
        b = HH(b, c, d, a, x[10],   S34, 0xbebfbc70L); /* 40 */
        a = HH(a, b, c, d, x[13],   S31, 0x289b7ec6L); /* 41 */
        d = HH(d, a, b, c, x[0],    S32, 0xeaa127faL); /* 42 */
        c = HH(c, d, a, b, x[3],    S33, 0xd4ef3085L); /* 43 */
        b = HH(b, c, d, a, x[6],    S34, 0x4881d05L); /* 44 */
        a = HH(a, b, c, d, x[9],    S31, 0xd9d4d039L); /* 45 */
        d = HH(d, a, b, c, x[12],   S32, 0xe6db99e5L); /* 46 */
        c = HH(c, d, a, b, x[15],   S33, 0x1fa27cf8L); /* 47 */
        b = HH(b, c, d, a, x[2],    S34, 0xc4ac5665L); /* 48 */


        a = II(a, b, c, d, x[0],    S41, 0xf4292244L); /* 49 */
        d = II(d, a, b, c, x[7],    S42, 0x432aff97L); /* 50 */
        c = II(c, d, a, b, x[14],   S43, 0xab9423a7L); /* 51 */
        b = II(b, c, d, a, x[5],    S44, 0xfc93a039L); /* 52 */
        a = II(a, b, c, d, x[12],   S41, 0x655b59c3L); /* 53 */
        d = II(d, a, b, c, x[3],    S42, 0x8f0ccc92L); /* 54 */
        c = II(c, d, a, b, x[10],   S43, 0xffeff47dL); /* 55 */
        b = II(b, c, d, a, x[1],    S44, 0x85845dd1L); /* 56 */
        a = II(a, b, c, d, x[8],    S41, 0x6fa87e4fL); /* 57 */
        d = II(d, a, b, c, x[15],   S42, 0xfe2ce6e0L); /* 58 */
        c = II(c, d, a, b, x[6],    S43, 0xa3014314L); /* 59 */
        b = II(b, c, d, a, x[13],   S44, 0x4e0811a1L); /* 60 */
        a = II(a, b, c, d, x[4],    S41, 0xf7537e82L); /* 61 */
        d = II(d, a, b, c, x[11],   S42, 0xbd3af235L); /* 62 */
        c = II(c, d, a, b, x[2],    S43, 0x2ad7d2bbL); /* 63 */
        b = II(b, c, d, a, x[9],    S44, 0xeb86d391L); /* 64 */

        //state[0]，state[1]，state[2]，state[3]
        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
    }

    private static long byteToul(byte b){
        return b > 0 ? b : (b & 0x7F + 128);
    }
    
    private static String byteToHEX(byte in){
        char[] DigitStr =
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'
        };
        char[] out = new char[2];
        out[0] = DigitStr[(in >> 4) & 0x0F];
        out[1] = DigitStr[in & 0x0F];        
        String s = new String(out);
        return s;
    }
    
    private void Encode(byte[] output, long[] input, int len){
        int i, j;
        for (i = 0, j = 0; j < len; i++, j += 4)
        {
            output[j] = (byte) (input[i] & 0xffL);
            output[j + 1] = (byte) ((input[i] >>> 8) & 0xffL);
            output[j + 2] = (byte) ((input[i] >>> 16) & 0xffL);
            output[j + 3] = (byte) ((input[i] >>> 24) & 0xffL);
        }
    }
    
    private void Decode(long[] output, byte[] input, int len){
        int i, j;
        for (i = 0, j = 0; j < len; i++, j += 4)
        {
            output[i] = byteToul(input[j])
                    | (byteToul(input[j + 1]) << 8)
                    | (byteToul(input[j + 2]) << 16)
                    | (byteToul(input[j + 3]) << 24);
        }
    }
}
