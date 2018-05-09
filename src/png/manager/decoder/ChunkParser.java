package png.manager.decoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import png.manager.entity.Chunk;
import png.manager.exception.ChunkParserException;
import png.manager.miscellaneous.PNGConstants;
import png.manager.miscellaneous.Utility;

/**
 * Fornisce un parser di chunk per le immagini PNG.
 */
class ChunkParser {

    /**
     * Costruttore privato della classe.
     */
    private ChunkParser() {
    }

    /**
     * Effettua il parsing dell'immagine.
     *
     * @param filename percorso del file su cui effettuare il parsing
     * @return lista dei chunk estratti
     * @throws ChunkParserException - se l'immagine non rispetta gli standard
     * del formato
     */
    public static ArrayList<Chunk> parseImage(String filename) throws ChunkParserException {
        Path path = Paths.get(filename);
        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new ChunkParserException("Impossibile aprire il file.");
        }

        //Controlla l'intestazione dell'immagine
        for (int i = 0; i < PNGConstants.HEADER_LENGTH; i++) {
            if (fileData[i] != PNGConstants.FORMAT_SIGNATURE[i]) {
                throw new ChunkParserException("Intestazione del file errata: il file non e' un'immagine PNG valida.");
            }
        }

        int currIndex = PNGConstants.HEADER_LENGTH;
        ArrayList<Chunk> chunksList = new ArrayList<>();

        try {
            while (currIndex != fileData.length) {
                //Ricava i 4 campi del chunk
                byte[] length = Utility.getBytesFromArray(fileData, PNGConstants.CHUNK_LENGTH_FIELD_SIZE, currIndex);
                currIndex += PNGConstants.CHUNK_LENGTH_FIELD_SIZE;
                byte[] type = Utility.getBytesFromArray(fileData, PNGConstants.CHUNK_NAME_FIELD_SIZE, currIndex);
                currIndex += PNGConstants.CHUNK_NAME_FIELD_SIZE;
                int dataLength = Utility.hexStringToInt(Utility.bytesToHexString(length));
                byte[] data = Utility.getBytesFromArray(fileData, dataLength, currIndex);
                currIndex += dataLength;
                byte[] CRC = Utility.getBytesFromArray(fileData, PNGConstants.CHUNK_CRC_FIELD_SIZE, currIndex);
                currIndex += PNGConstants.CHUNK_CRC_FIELD_SIZE;

                chunksList.add(new Chunk(length, type, data, CRC));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new ChunkParserException("Errore nel parsing dei chunk.\nIl file non è strutturato secondo le specifiche del formato.");
        }

        return chunksList;
    }
}
