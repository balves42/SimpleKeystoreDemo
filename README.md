# SimpleKeystoreDemo
Simple Android Keystore implementation working with min Api 19. 

# Android Keystore
The Android Keystore makes it more difficult to extract cryptographic keys from the device, since it works as a special container that mitigates unauthorised use of key material by other application processes or from the Android device as a whole.

# Implementation
A java class named KeystoreUtil was developed and can be tested using the sample provided. Multiple aliases can be created and accessed.

# Demo
<img src="https://i.imgur.com/AmPXAwL.png" title="Keystore demo" /></a>

1. Create and delete aliases for ciphering / deciphering content
2. Cipher text with a chosen alias
3. Check the ciphered content
4. Decipher with a chosen alias and check the text

# Limitations
Since the choosen min Api is 19, the only cipher with padding available is RSA/ECB/PKSC1Padding. The padding ensures that when the plaintext to be encrypted is not an exact multiple, no extra information is required from the user to “fill the blanks”.

On versions prior to Android 5.0, the Keystore is deleted when changing the lockscreen pin / password (requires confirmation)
