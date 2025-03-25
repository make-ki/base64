import ciphers.*;
import java.util.Scanner;
import java.util.Random;

/*
This is Base64 encoder/decoder which has two modes, normal and salted. I have tried using validate() static methods(overloaded) to deal with
invalid inputs, if any invalid input is made it asks again for the prompt. I have created two classes named Base64 and Base64Salted
the later is extending previous. Also I have implemented a Cipher interface so that I can add methods in every cipher and keep
track of the methods they are implementing. The logic of ciphers is discussed in detail in their respective files. I have also tried
to name identifiers as self explanatory as I could. There were many print statements for debugging purposes which I have removed.
*/



public class Main {
    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String retry;
        do{
            String choice;
            do {
                System.out.printf("Do you wish to (e)ncrypt or (d)ecrypt?: ");
                choice = scanner.next();
            } while (!validate(choice));

            int mode;
            do {
                System.out.printf("Choose the mode:\n");
                System.out.printf("1: Base64 (normal)\n");
                System.out.printf("2: Base64 (salted)\n");
                System.out.printf("Enter the serial number of the mode you like: ");
                //I was trying to do a try catch here but I didn't want program to stop so I did this instead.
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number (1 or 2). Try again.");
                    scanner.next();
                }
                mode = scanner.nextInt();
            } while (!validate(mode));

            if (choice.equalsIgnoreCase("e")) {
                String plaintext;
                do {
                    System.out.printf("Enter text to encrypt: ");
                    plaintext = scanner.next();
                } while (!validate(plaintext, 'e'));

                switch (mode) {
                    case 1:
                        Base64 text = new Base64(plaintext);
                        text.encode();
                        break;
                    case 2:
                        //during encryption, the plaintext will get encrypted using a random key which will be displayed too.
                        Random random = new Random();
                        int rand_key = random.nextInt(63);
                        Base64Salted stext = new Base64Salted(plaintext, rand_key);
                        stext.encode();
                        break;
                }
            } else {
                String ciphertext;
                do {
                    System.out.printf("Enter cipher to decrypt: ");
                    ciphertext = scanner.next();
                } while (!validate(ciphertext, 'd'));

                switch (mode) {
                    case 1:
                        Base64 text = new Base64(ciphertext);
                        text.decode();
                        break;
                    case 2:
                        int key;
                        do {
                            System.out.printf("Enter the key to decode: ");
                            while (!scanner.hasNextInt()) {
                                System.out.println("The key must be an integer. Try again.");
                                scanner.next();
                            }
                            key = scanner.nextInt();
                        } while (!validate(key, 'd'));

                        Base64Salted stext = new Base64Salted(ciphertext, key);
                        stext.decode();
                        break;
                }
            }
            System.out.printf("\nDo you want to try again? (y/n): ");
            retry = scanner.next();

        }while (retry.equalsIgnoreCase("y"));

        System.out.println("Exiting... Goodbye!");
        scanner.close();
    }


    static boolean validate(String choice) {
        if (choice == null || choice.isEmpty()) {
            System.out.println("Input cannot be empty. Try again.");
            return false;
        }
        if (!choice.equalsIgnoreCase("e") && !choice.equalsIgnoreCase("d")) {
            System.out.println("Invalid choice. Enter 'e' for encrypt or 'd' for decrypt.");
            return false;
        }
        return true;
    }

    static boolean validate(int mode) {
        if (mode != 1 && mode != 2) {
            System.out.println("Invalid mode. Choose either 1 or 2.");
            return false;
        }
        return true;
    }

    static boolean validate(String str, char choice) {
        if (str == null || str.isEmpty()) {
            System.out.println("Input cannot be empty. Try again.");
            return false;
        }
        //I had to look up how to implement this matches() method i didn't know abt it nor the regex like expression
        if (choice == 'd') {
            if (!str.matches("[A-Za-z0-9+/=]+")) {
                System.out.println("Invalid Base64 string. Contains non-Base64 characters.");
                return false;
            }
        }
        return true;
    }

    static boolean validate(int key, char choice) {
        if (key <= 0) {
            System.out.println("Key must be a positive integer. Try again.");
            return false;
        }
        return true;
    }
}
