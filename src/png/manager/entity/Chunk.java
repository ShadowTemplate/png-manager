package png.manager.entity;

import png.manager.miscellaneous.Utility;

/**
 * Modella l'entità chunk e ne memorizza tutte le caratteristiche.
 */
public class Chunk {

    /**
     * Campo lunghezza del chunk.
     */
    private final byte[] length;
    /**
     * Campo tipo del chunk.
     */
    private final byte[] type;
    /**
     * Campo dati del chunk.
     */
    private final byte[] data;
    /**
     * Campo CRC del chunk.
     */
    private final byte[] CRC;
    /**
     * Flag che indica se il chunk è ausiliario.
     */
    private final boolean ancillary;
    /**
     * Flag che indica se il chunk è privato.
     */
    private final boolean privateChunck;
    /**
     * Flag che indica se il chunk è riservato.
     */
    private final boolean reserved;
    /**
     * Flag che indica se il chunk è sicuro da copiare.
     */
    private final boolean safeToCopy;
    /**
     * Intero rappresentante la lunghezza del chunk.
     */
    private final int lengthAsInt;
    /**
     * Rappresentazione testuale del tipo del chunk.
     */
    private final String typeAsString;
    /**
     * Rappresentazione testuale del CRC del chunk.
     */
    private final String CRCAsString;

    /**
     * Costruttore del chunk.
     *
     * @param length lunghezza del chunk
     * @param type nome del chunk
     * @param data dati del chunk
     * @param CRC codice CRC del chunk
     */
    public Chunk(byte[] length, byte[] type, byte[] data, byte[] CRC) {
        this.length = length;
        this.type = type;
        this.data = data;
        this.CRC = CRC;

        //Determina le proprietà del chunk verificando se la lettera è maiuscola, in accordo alle specifiche del formato
        this.ancillary = Character.isLowerCase(Utility.hexStringToASCIIString(Utility.bytesToHexString(type)).charAt(0));
        this.privateChunck = Character.isLowerCase(Utility.hexStringToASCIIString(Utility.bytesToHexString(type)).charAt(1));
        this.reserved = Character.isLowerCase(Utility.hexStringToASCIIString(Utility.bytesToHexString(type)).charAt(2));
        this.safeToCopy = Character.isLowerCase(Utility.hexStringToASCIIString(Utility.bytesToHexString(type)).charAt(3));

        this.lengthAsInt = Utility.hexStringToInt(Utility.bytesToHexString(length));
        this.typeAsString = Utility.hexStringToASCIIString(Utility.bytesToHexString(type));
        this.CRCAsString = Utility.bytesToHexString(CRC);
    }

    /**
     * Ritorna la lunghezza del chunk.
     *
     * @return lunghezza del chunk
     */
    public byte[] getLength() {
        return length;
    }

    /**
     * Ritorna il nome del chunk.
     *
     * @return nome del chunk
     */
    public byte[] getType() {
        return type;
    }

    /**
     * Ritorna i dati del chunk.
     *
     * @return dati del chunk
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Ritorna il CRC del chunk.
     *
     * @return CRC del chunk
     */
    public byte[] getCRC() {
        return CRC;
    }

    /**
     * Definisce se il chunk è ausiliario.
     *
     * @return <code>true</code> se il chunk è ausiliario, <code>false</code>
     * altrimenti.
     */
    public boolean isAncillary() {
        return ancillary;
    }

    /**
     * Definisce se il chunk è privato.
     *
     * @return <code>true</code> se il chunk è privato, <code>false</code>
     * altrimenti.
     */
    public boolean isPrivate() {
        return privateChunck;
    }

    /**
     * Definisce se il chunk è riservato.
     *
     * @return <code>true</code> se il chunk è riservato, <code>false</code>
     * altrimenti.
     */
    public boolean isReserved() {
        return reserved;
    }

    /**
     * Definisce se il chunk è sicuro da copiare.
     *
     * @return <code>true</code> se il chunk è sicuro da * copiare,
     * <code>false</code> altrimenti.
     */
    public boolean isSafeToCopy() {
        return safeToCopy;
    }

    /**
     * Ritorna un intero rappresentante la lunghezza del chunk.
     *
     * @return lunghezza del chunk
     */
    public int getLengthAsInt() {
        return lengthAsInt;
    }

    /**
     * Ritorna la rappresentazione testuale del nome del chunk.
     *
     * @return nome del chunk
     */
    public String getTypeAsString() {
        return typeAsString;
    }

    /**
     * Ritorna la rappresentazione testuale del nom del chunk.
     *
     * @return il CRC del chunk
     */
    public String getCRCAsString() {
        return CRCAsString;
    }

    /**
     * Rappresentazione testuale del chunk.
     *
     * @return rappresentazione del chunk
     */
    @Override
    public String toString() {
        return "[" + lengthAsInt + "] [" + typeAsString + "] [--data--] [" + CRCAsString + "]";

        /*Con dati:
         return "[" + lengthAsInt + "] [" + typeAsString + "] [" + Utility.bytesToSplittedHexString(data) + "] [" + CRCAsString + "]";    
         */
    }

    /**
     * Rappresentazione sintentica delle caratteristiche del chunk.
     *
     * @return prospetto riassuntivo del chunk
     */
    public String getInfo() {
        return "[" + Utility.bytesToSplittedHexString(length) + "] [" + Utility.bytesToSplittedHexString(type) + "] [" + Utility.bytesToSplittedHexString(data) + "] [" + Utility.bytesToSplittedHexString(CRC) + "]";
    }
}
