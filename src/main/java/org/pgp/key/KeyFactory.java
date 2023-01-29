package org.pgp.key;

import org.pgp.key.keys.ASymmetricKey;
import org.pgp.key.keys.SymmetricKey;

public class KeyFactory {
    public static SymmetricKey createSymmetricKey(){
        return new SymmetricKey();
    }
    public static ASymmetricKey createASymmetricKey(){
        return new ASymmetricKey();
    }
}
