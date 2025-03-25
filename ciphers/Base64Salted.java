package ciphers;
import java.nio.charset.StandardCharsets;

/*
The entire code logic for encode and decode is same as that of normal Base64 except the value at each step is manipulated.
Lets say your key is 1 and the 6-bit chosen in some step was 010000, normally we would map this with whatever character is at that
index, but with key 1, the 6-bit chosen becomes 010001 that is key is added into it. While decoding, we just subtract the key from
value. Since this is a simple change, it is possible to have multiple keys that could decipher an encoded text. However, the
purpose of salting is just to make life of decoders a bit harder.

*/

public class Base64Salted extends Base64 implements Cipher {
    private int key;

    public Base64Salted(String text, int key) {
        super(text);
        this.key = key;
    }

    @Override
    public void encode() {
        System.out.printf("Encode in Salted for %s with key %d is running... \n", super.text, key);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        int buffer = 0, bitsInBuffer = 0, i = 0;
        StringBuilder encoded = new StringBuilder();

        while (i < bytes.length) {
            buffer = (buffer << 8) + (bytes[i] & 0xFF);
            bitsInBuffer += 8;
            i++;

            while (bitsInBuffer >= 6) {
                bitsInBuffer -= 6;
                int value = (buffer >> bitsInBuffer) & 0x3F;
                value = (value + key) % 64;
                encoded.append(BASE64chars.charAt(value));
            }
        }

        if (bitsInBuffer > 0) {
            int value = (buffer << (6 - bitsInBuffer)) & 0x3F;
            //key is added to value also % 64 is used to keep value within value range
            value = (value + key) % 64;
            encoded.append(BASE64chars.charAt(value));

            while (encoded.length() % 4 != 0) {
                encoded.append('=');
            }
        }
        System.out.println("Encoded string is: "+ encoded);
    }

    @Override
    public void decode() {
        System.out.printf("Decode in Salted for %s with key %d is running...\n", super.text, key);
        int buffer = 0, bitsInBuffer = 0, i = 0;
        byte[] bytes = new byte[text.length() * 6 / 8];

        for (char c : text.toCharArray()) {
            int value = BASE64chars.indexOf(c);
            if (value >= 0) {
                //key is being subtracted, also % 64 is put for value to remain in range
                value = (value - key + 64) % 64;
                buffer = (buffer << 6) + value;
                bitsInBuffer += 6;
            }
            while (bitsInBuffer >= 8) {
                bitsInBuffer -= 8;
                bytes[i++] = (byte) ((buffer >> bitsInBuffer) & 0xFF);
            }
        }
        String decoded = new String(bytes, 0, i, StandardCharsets.UTF_8);
        System.out.println("Decoded string is: "+ decoded);
        System.out.println("Tip: If the output doesn't make sense to you, try to enter different key !!");
    }
}
