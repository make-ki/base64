package ciphers;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.lang.StringBuilder;

/*
This is the normal Base64 mode.
How base64 works:
        Lets say you want to encode : AB
        AB in bits is: 01000001 01000010
    This is called base 2. What base64 then means is that 6-bits are clubbed together.
    So here,
        010000 010100 0010
    The last 6-bit batch isn't complete so we add need to add 0s at the end(padding) which is signified by '=' character
    Hence we get,
        010000 010100 001000
          16     20      8
    These numbers are mapped to Characters from A-Z then a-z then 0-9 then + / symbols.
    The string of those characters is the encoded base64 output.
    To decode we find the mapped value of each encoded character, then push it into buffer. If the buffer has >= 8 bits we
    push it out and store it in an array of bytes. Padding is ignored.

*/

public class Base64 implements Cipher{
    protected String text;
    protected static final String BASE64chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public Base64(String text){
        this.text = text;
    }

    public void encode(){
        System.out.printf("Encode in normal for %s is running...\n", text);
        //what we want to do is create a byte array of the string
        //stackoverflow suggested that I use specify what characterset I am using when making a byte[] so I did
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        int buffer = 0;
        int bitsInBuffer = 0;
        int i = 0;
        StringBuilder encoded = new StringBuilder();
        while( i < bytes.length){
            // here we are loading the 8 bits from bytes array into int buffer
            // & 0xFF is used to let buffer be unsigned.
            buffer = (buffer << 8) + (bytes[i] & 0xFF);
            bitsInBuffer += 8;
            i++;

            while(bitsInBuffer >= 6){
                bitsInBuffer -= 6;
                //We shift the buffer here, lets say first byte was 01000001, we shift it right two times and get the first 6-bit
                //We are doing & 0x3f (63) for value to be unsigned.
                int value = (buffer >> bitsInBuffer) & 0x3f;
                //finding what character is at index of value
                encoded.append(BASE64chars.charAt(value));
            }
        }
        //if any remaing bits are remaining, we need to add padding to them
        if(bitsInBuffer > 0){
            int value = (buffer << (6 - bitsInBuffer) & 0x3f);
            encoded.append(BASE64chars.charAt(value));

            //since 3 bytes(24bits is div by 6) of plaintext is equal to 4 characters in base64, we pad till encoded length becomes multiple of four
            while(encoded.length() % 4 != 0){
                encoded.append('=');
            }
        }
        System.out.println("Encoded string is: " + encoded);

    }
    public void decode(){
        System.out.printf("Decode in normal for %s is running...\n", text);
        int buffer = 0;
        int i = 0;
        int bitsInBuffer = 0;
        //length * 6 bits divided by 8 (one byte)
        byte[] bytes = new byte[text.length()*6 / 8];

        //converted the input string into array of character and performed operations individually.
        for(char c : text.toCharArray()){
            int value = BASE64chars.indexOf(c);
            if (value >= 0){
                //loads value into buffer
                buffer = (buffer << 6) + value;
                bitsInBuffer += 6;
            }
            while(bitsInBuffer >= 8){
                bitsInBuffer -= 8;
                //puts in the 8 bits back as bytes
                bytes[i++] = (byte) ((buffer >> bitsInBuffer) & 0xff);
            }
        }
        //forms a string from those array of bytes
        String decoded = new String(bytes, 0, i);
        System.out.println("Decoded string is: "+ decoded);
    }
}
