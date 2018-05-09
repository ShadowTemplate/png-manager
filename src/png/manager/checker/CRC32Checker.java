package png.manager.checker;

import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import png.manager.entity.Chunk;
import png.manager.miscellaneous.PNGConstants;
import png.manager.miscellaneous.Utility;

/**
 * Fornisce le funzionalità per il controllo del CRC dei chunk.
 */
public class CRC32Checker {

    /**
     * Costruttore privato dell'oggetto.
     */
    private CRC32Checker() {
    }

    /**
     * Controlla se il CRC di ciascun chunk in input ha CRC valido.
     *
     * @param chunks elenco dei chunk da controllare
     * @return <code>true</code> se tutti i chunk hanno CRC valido,
     * <code>false</code> altrimenti
     */
    public static boolean checkChunksCRC(ArrayList<Chunk> chunks) {
        int size = chunks.size();
        for (int i = 0; i < size; i++) {
            if (!checkChunkCRC32(chunks.get(i))) {
                System.err.println("Errore trovato nel CRC al chunk " + (i + 1) + ": " + chunks.get(i));
                return false;
            }
        }
        return true;
    }

    /**
     * Controlla se il CRC del chunk in input è valido.
     *
     * @param c chunk da controllare
     * @return <code>true</code> se il chunk ha CRC valido, <code>false</code>
     * altrimenti
     */
    private static boolean checkChunkCRC32(Chunk c) {
        Checksum checksum = new CRC32();
        //Il CRC deve essere calcolato sulla concatenazione di nome e dati
        byte[] data = Utility.concatArray(c.getType(), c.getData());
        checksum.update(data, 0, data.length);
        long CRC = checksum.getValue();
        String hexCRC = Long.toHexString(CRC).toUpperCase();
        //Zerofill d'avanti per arrivare a 8 cifre
        while (hexCRC.length() != PNGConstants.CRC_HEX_LENGTH) {
            hexCRC = "0" + hexCRC;
        }
        return hexCRC.equals(c.getCRCAsString());
    }
}
