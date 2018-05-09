package png.manager.entity;

import java.util.ArrayList;

import png.manager.miscellaneous.PNGConstants;
import png.manager.miscellaneous.Utility;

/**
 * Modella un'immagine in formato PNG e ne memorizza tutte le caratteristiche.
 */
public class PNGImage {

    /**
     * Elenco dei chunk dell'immagine.
     */
    private final ArrayList<Chunk> chunks;
    /**
     * Larghezza dell'immagine.
     */
    private int width;
    /**
     * Altezza dell'immagine.
     */
    private int height;
    /**
     * Profondità in bit dell'immagine.
     */
    private int bitDepth;
    /**
     * Tipo di colore dell'immagine.
     */
    private int colorType;
    /**
     * Metodo di compressione dell'immagine.
     */
    private int compressionMethod;
    /**
     * Metodo di filtraggio dell'immagine.
     */
    private int filteringMethod;
    /**
     * Metodo di interlacciamento dell'immagine.
     */
    private int interlacingMethod;

    /**
     * Costruttore dell'oggetto.
     *
     * @param chunks lista di chunk dell'immagine
     */
    public PNGImage(ArrayList<Chunk> chunks) {
        this.chunks = chunks;
        Chunk c = getChunk(PNGConstants.MAIN_CHUNK_NAME);
        retrieveData(c);
    }

    /**
     * Estrae tutte le informazione dell'immagine, a partire dal chunk IHDR.
     *
     * @param mainChunk chunk IHDR
     */
    private void retrieveData(Chunk mainChunk) {
        int currIndex = 0;
        byte[] data = mainChunk.getData();
        
        //Vedere le specifiche del chunk IHDR per comprendere l'interpretazione dei dati del chunk
        
        width = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_WIDTH_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_WIDTH_LENGTH_BYTE;
        height = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_HEIGHT_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_HEIGHT_LENGTH_BYTE;
        bitDepth = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_BIT_DEPTH_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_BIT_DEPTH_LENGTH_BYTE;
        colorType = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_COLOR_TYPE_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_COLOR_TYPE_LENGTH_BYTE;
        compressionMethod = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_COMPRESSION_METHOD_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_COMPRESSION_METHOD_LENGTH_BYTE;
        filteringMethod = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_FILTER_METHOD_LENGTH_BYTE, currIndex)));
        currIndex += PNGConstants.IHDR_FILTER_METHOD_LENGTH_BYTE;
        interlacingMethod = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(data, PNGConstants.IHDR_INTERLACE_METHOD_LENGTH_BYTE, currIndex)));
    }

    /**
     * Ritorna l'elenco dei chunk dell'immagine.
     *
     * @return elenco dei chunk
     */
    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    /**
     * Ritorna la larghezza dell'immagine.
     *
     * @return larghezza dell'immagine
     */
    public int getWidth() {
        return width;
    }

    /**
     * Ritorna l'altezza dell'immagine.
     *
     * @return altezza dell'immagine
     */
    public int getHeight() {
        return height;
    }

    /**
     * Ritorna la profondità di bit dell'immagine.
     *
     * @return profondità di bit dell'immagine
     */
    public int getBitDepth() {
        return bitDepth;
    }

    /**
     * Ritorna il tipo di colore dell'immagine.
     *
     * @return tipo di colore dell'immagine
     */
    public int getColorType() {
        return colorType;
    }

    /**
     * Ritorna il metodo di compressione dell'immagine.
     *
     * @return metodo di compressione dell'immagine
     */
    public int getCompressionMethod() {
        return compressionMethod;
    }

    /**
     * Ritorna il metodo di filtraggio dell'immagine.
     *
     * @return metodo di filtraggio dell'immagine
     */
    public int getFilteringMethod() {
        return filteringMethod;
    }

    /**
     * Ritorna il metodo di interlacciamento dell'immagine.
     *
     * @return metodo di interlacciamento dell'immagine
     */
    public int getInterlacingMethod() {
        return interlacingMethod;
    }

    /**
     * Verifica se l'immagine contiene un particolare chunk.
     *
     * @param type tipo del chunk da controllare
     * @return <code>true</code> se l'immagine contiene almeno un chunk col nome
     * in input, <code>false</code> altrimenti
     */
    public boolean containsChunk(String type) {
        for (Chunk c : chunks) {
            if (c.getTypeAsString().equals(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Ritorna il primo chunk con tipo corrispondente alla stringa input; null
     * se non ve ne sono.
     *
     * @param type tipo del chunk da ricercare
     * @return chunk trovato
     */
    public final Chunk getChunk(String type) {
        for (Chunk c : chunks) {
            if (c.getTypeAsString().equals(type)) {
                return c; //Ritorna la prima occorrenza
            }
        }
        return null;
    }

    /**
     * Recupera e restituisce tutti i dati compressi dell'immagine.
     * <p>
     * Se sono presenti più di un chunk IDAT, il metodo concatena tutti i dati.
     *
     * @return dati dell'immagine
     */
    public byte[] getCompressedData() {
        byte[] ris = new byte[0];
        for (Chunk c : chunks) {
            if (c.getTypeAsString().equals(PNGConstants.IMAGE_DATA_CHUNK_NAME)) {
                ris = Utility.concatArray(ris, c.getData());
            }
        }

        return ris;
    }

    /**
     * Rappresentazione testuale dei chunk dell'immagine.
     *
     * @return elenco dei chunk presenti nell'immagine
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Chunk c : getChunks()) {
            sb.append(c).append("\n");
        }
        return sb.toString();
    }

    /**
     * Riepilogo delle caratteristiche dell'immagine.
     *
     * @return prospetto dell'immagine
     */
    public String getInfo() {
        return "Chunks: " + getChunks().size() + "; width: " + getWidth() + "px; height: " + getHeight() + "px; colorType: " + getColorType() + "; bitDepth: " + getBitDepth() + "; interlaceMethod: " + getInterlacingMethod() + "; compression: " + getCompressionMethod() + ".";
    }
}
