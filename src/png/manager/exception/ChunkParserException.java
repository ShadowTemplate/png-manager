package png.manager.exception;

/**
 * Eccezione sollevata quando il parser incontra problemi nel suddividere il PNG
 * datastream in chunk.
 */
public class ChunkParserException extends Exception {

    /**
     * Costruttore dell'oggetto.
     */
    public ChunkParserException() {
        super();
    }

    /**
     * Costruisce l'oggetto memorizzando un messaggio legato all'eccezione.
     * 
     * @param msg messaggio dell'eccezione
     */
    public ChunkParserException(String msg) {
        super(msg);
    }
}
