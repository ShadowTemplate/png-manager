package png.manager.exception;

/**
 * Eccezione sollevata quando, dopo aver effettuato il parsing dell'immagine,
 * essa non rispetta una delle specifiche richieste dal formato PNG.
 */
public class PNGStructureException extends Exception {

    /**
     * Costruttore dell'oggetto.
     */
    public PNGStructureException() {
        super();
    }

    /**
     * Costruisce l'oggetto memorizzando un messaggio legato all'eccezione.
     *
     * @param msg messaggio dell'eccezione
     */
    public PNGStructureException(String msg) {
        super(msg);
    }
}
