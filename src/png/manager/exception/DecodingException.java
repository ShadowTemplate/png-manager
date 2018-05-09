package png.manager.exception;

/**
 * Eccezione sollevata quando il decoder incontra problemi nel decodificare
 * l'immagine prima di visualizzarla.
 */
public class DecodingException extends Exception {

    /**
     * Costruttore dell'oggetto.
     */
    public DecodingException() {
        super();
    }

    /**
     * Costruisce l'oggetto memorizzando un messaggio legato all'eccezione.
     *
     * @param msg messaggio dell'eccezione
     */
    public DecodingException(String msg) {
        super(msg);
    }
}
