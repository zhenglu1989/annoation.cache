package com.luffy.util;

import java.io.IOException;
import java.io.InputStream;

/** This class provides methods for computing 63 bit fingerprints of strings,
byte arrays, and input streams. A 63 bit fingerprint is represented as a
<code>long</code> value. The static methods of <code>FP63</code> are used
to initialize 63 bit fingerprints and to extend them.

This implementation matches the one used by Arachne. 

@author mattias.larsson@av.com
*/

public class FP63 {
    /** Return the fingerprint of the empty string. */
    /**
     * @return IrredPoly
     */
    public static long newFP63() {
        return IRRED_POLY;
    }

    /** Return the fingerprint of <code>s</code>. */
    //@ requires s != null

    /**
     * @param s
     * @return Extend(IrredPoly, s) >> 1L
     */
    public static long newFP63(String s) {
    	if(s == null) {
    		return 0;
    	}
        return extendFP63(IRRED_POLY, s) >> 1L;
    }

    /** Return the fingerprint of the characters in the array <code>c</code>. */
    //@ requires c != null
    /**
     * @param c
     * @return Extend(IrredPoly, c, 0, c.length) >> 1L;
     */
    public static long newFP63(char[] c) {
        return extendFP63(IRRED_POLY, c, 0, c.length) >> 1L;
    }

    /** Return the fingerprint of the characters in the array <code>c</code>. */
    //@ requires c != null && off >= 0 && off + len <= c.length
    /**
     * @param c
     * @param off
     * @param len
     * @return Extend(IrredPoly, c, off, len) >> 1L;
     */
    public static long newFP63(char[] c, int off, int len) {
        return extendFP63(IRRED_POLY, c, off, len) >> 1L;
    }

    /** Return the fingerprint of the bytes in the array <code>bytes</code>. */
    //@ requires bytes != null
    /**
     * @param bytes
     * @return Extend(IrredPoly, bytes, 0, bytes.length) >> 1L;
     */
    public static long newFP63(byte[] bytes) {
        return extendFP63(IRRED_POLY, bytes, 0, bytes.length) >> 1L;
    }

    /** Return the fingerprint of the contents of the
    stream <code>is</code>. <code>IOException</code>
    is thrown in the event of an error reading the
    stream. */
    //@ requires is != null
    /**
     * @param is
     * @return IOException
     * @throws java.io.IOException IOException
     */
    public static long newFP63(InputStream is) throws IOException {
        return extendFP63(IRRED_POLY, is) >> 1L;
    }

    /** Extend the fingerprint <code>fp</code> by the characters of
    <code>s</code>. */
    //@ requires s != null
    /**
     * @param fp
     * @param s
     * @return fp
     */
    public static long extendFP63(long fp, String s) {
        final long[] mod = byteModeTable7;
        final int mask = 0xFF;
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            fp = ((fp >>> 8) ^ (mod[(((int)c) ^ ((int)fp)) & mask]));
            if(c > 255) {
                // char c is not ascii, calculate next byte
                fp = ((fp >>> 8) ^ (mod[(((int)c >> 8) ^ ((int)fp)) & mask]));
            }
        }
        if ((fp & X63)!=0)
        {
            fp ^= 0xCF517E46C7CE691FL;
        }
        return fp;
    }


    /** Extend the fingerprint <code>fp</code> by the characters of
    <code>chars</code>. */
    //@ requires chars != null && start >= 0 && start+len <= chars.length
    /**
     * @param fp
     * @param chars
     * @param start
     * @param len
     * @return fp
     */
    public static long extendFP63(long fp, char[] chars, int start, int len) {
        final long[] mod = byteModeTable7;
        int end = start + len;
        for (int i = start; i < end; i++) {
            fp = ((fp >>> 8) ^ (mod[(((int)chars[i]) ^ ((int)fp)) & 0xFF]));
        }
        if ((fp & X63)!=0)
        {
            fp ^= 0xCF517E46C7CE691FL;
        }
        return fp;
    }

    /** Extend the fingerprint <code>fp</code> by the
    bytes in the array <code>bytes</code>. */
    //@ requires bytes != null && start >= 0 && start + len <= bytes.length
    /**
     * @param fp
     * @param bytes
     * @param start
     * @param len
     * @return fp
     */
    public static long extendFP63(long fp, byte[] bytes, int start, int len) {
        final long[] mod = byteModeTable7;
        int end = start + len;
        for (int i = start; i < end; i++) {
            fp = (fp >>> 8) ^ mod[(bytes[i] ^ (int)fp) & 0xFF];
        }
        if ((fp & X63)!=0)
        {
            fp ^= 0xCF517E46C7CE691FL;
        }
        return fp;
    }

    /** Extend the fingerprint <code>fp</code> by the bytes
    of the stream <code>is</code>. <code>IOException</code>
    is thrown in the event of an error reading the stream. */
    //@ requires is != null
    /**
     * @param fp
     * @param is
     * @return fp
     * @throws java.io.IOException IOException
     */
    public static long extendFP63(long fp, InputStream is) throws IOException {
        final long[] mod = byteModeTable7;
        final int mask = 0xFF;
        int i;
        while ((i = is.read()) != -1) {
            fp = ((fp >>> 8) ^ (mod[(i ^ ((int)fp)) & mask]));
        }
        if ((fp & X63)!=0)
        {
            fp ^= 0xCF517E46C7CE691FL;
        }
        return fp;
    }

    /** Return a hash value of the fingerprint <code>fp</code>. */
    /**
     * @param fp
     * @return (int)fp;
     */
    public static int hashFP63(long fp) {
        return (int)fp;
    }

    /** Unlikely fingerprint? */
    public static final long ZEOR_FP63 = 0L;

    // implementation constants
  //  private static final long ONE = 0x8000000000000000L;
    private static final long IRRED_POLY = 0xa8f9c165a4295d90L;
    private static final long X63 = 0x1L;

    /* This is the table used for computing fingerprints.  The
    ByteModTable could be hardwired.  Note that since we just
    extend a byte at a time, we need just "ByteModeTable[7]". */
    private static /*@ non_null */ long[] byteModeTable7;

    // Initialization code
    static {

    long[] powerTable = { 0x8000000000000000L, 0x4000000000000000L, 0x2000000000000000L, 
            0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 
            0x100000000000000L, 0x80000000000000L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L,
            0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L, 0x800000000000L,
            0x400000000000L,0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L,
            0x20000000000L, 0x10000000000L, 0x8000000000L,0x4000000000L, 0x2000000000L, 0x1000000000L,
            0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L, 0x80000000L,
            0x40000000L, 0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L, 0x800000L, 0x400000L,
            0x200000L, 0x100000L, 0x80000L, 0x40000L, 0x20000L, 0x10000L, 0x8000L, 0x4000L, 0x2000L, 0x1000L, 0x800L, 
            0x400L, 0x200L, 0x100L, 0x80L, 0x40L, 0x20L, 0x10L, 0x8L, 0x4L, 0x2L, 0x1L, 0x67a8bf2363e7348fL, 
            0x547ce0b2d214aec8L, 0x2a3e7059690a5764L, 0x151f382cb4852bb2L, 0xa8f9c165a4295d9L, 0x62ef71284ec67e63L, 
            0x56df07b744840bbeL, 0x2b6f83dba24205dfL, 0x721f7eceb2c63660L, 0x390fbf6759631b30L, 0x1c87dfb3acb18d98L,
            0xe43efd9d658c6ccL, 0x721f7eceb2c6366L, 0x390fbf6759631b3L, 0x6660c2d8592c2c56L, 0x3330616c2c96162bL,
            0x7e308f9575ac3f9aL, 0x3f1847cabad61fcdL, 0x78249cc63e8c3b69L, 0x5bbaf1407ca1293bL, 0x4a75c7835db7a012L,
            0x253ae3c1aedbd009L, 0x7535cec3b48adc8bL, 0x5d325842b9a25acaL, 0x2e992c215cd12d65L, 0x70e42933cd8fa23dL,
            0x5fdaabba8520e591L, 0x4845eafe21774647L, 0x438a4a5c735c97acL, 0x21c5252e39ae4bd6L, 0x10e292971cd725ebL,
            0x6fd9f668ed8ca67aL, 0x37ecfb3476c6533dL, 0x7c5ec2b958841d11L, 0x5987de7fcfa53a07L, 0x4b6b501c8435a98cL,
            0x25b5a80e421ad4c6L, 0x12dad407210d6a63L, 0x6ec5d520f36181beL, 0x3762ea9079b0c0dfL, 0x7c19ca6b5f3f54e0L, 
            0x3e0ce535af9faa70L, 0x1f06729ad7cfd538L, 0xf83394d6be7ea9cL, 0x7c19ca6b5f3f54eL, 0x3e0ce535af9faa7L, 
            0x6658d80ace9bc9dcL, 0x332c6c05674de4eeL, 0x19963602b3a6f277L, 0x6b63a4223a344db4L, 0x35b1d2111d1a26daL,
            0x1ad8e9088e8d136dL, 0x6ac4cba724a1bd39L, 0x52cadaf0f1b7ea13L, 0x4ecdd25b1b3cc186L, 0x2766e92d8d9e60c3L,
            0x741bcbb5a52804eeL, 0x3a0de5dad2940277L, 0x7aae4dce0aad35b4L, 0x3d5726e705569adaL, 0x1eab937382ab4d6dL,
            0x68fd769aa2b29239L, 0x53d6046e32be7d93L, 0x4e43bd147ab80a46L };

        // Just need the 7th iteration of the ByteModTable initialization code
        byteModeTable7 = new long[256];
        for (int j = 0; j <= 255; j++) {
            long v = ZEOR_FP63;
            for (int k = 0; k <= 7; k++) {
                if ((j & (1L << k)) != 0) {
                    v ^= powerTable[127-(7*8)-k];
                }
            }
            byteModeTable7[j] = v;
            //System.out.println("mod7[" + j + "] = " + Long.toHexString(v));
        }
    }
}
