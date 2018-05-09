package png.manager.miscellaneous;

/**
 * Raccoglie alcune costanti del formato PNG.
 */
public class PNGConstants {

    /**
     * Estensione dei file minuscola.
     */
    public static final String LOWERCASE_EXTENSION = "png";
    /**
     * Estensione dei file maiuscola.
     */
    public static final String UPPERCASE_EXTENSION = "PNG";

    /**
     * Header dei file.
     */
    public static final byte[] FORMAT_SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    /**
     * Lunghezza dell'header dei file.
     */
    public static final int HEADER_LENGTH = 8;

    /**
     * Lunghezze, in byte, del campo lunghezza del chunk.
     */
    public static final int CHUNK_LENGTH_FIELD_SIZE = 4;
    /**
     * Lunghezze, in byte, del campo nome del chunk.
     */
    public static final int CHUNK_NAME_FIELD_SIZE = 4;
    /**
     * Lunghezze, in byte, del campo CRC del chunk.
     */
    public static final int CHUNK_CRC_FIELD_SIZE = 4;

    /**
     * Lunghezze, in byte, del componente larghezza del chunk IHDR.
     */
    public final static int IHDR_WIDTH_LENGTH_BYTE = 4;
    /**
     * Lunghezze, in byte, del componente altezza del chunk IHDR.
     */
    public final static int IHDR_HEIGHT_LENGTH_BYTE = 4;
    /**
     * Lunghezze, in byte, del componente profondità di bit del chunk IHDR.
     */
    public final static int IHDR_BIT_DEPTH_LENGTH_BYTE = 1;
    /**
     * Lunghezze, in byte, del componente tipo di colore del chunk IHDR.
     */
    public final static int IHDR_COLOR_TYPE_LENGTH_BYTE = 1;
    /**
     * Lunghezze, in byte, del componente metodo di compressione del chunk IHDR.
     */
    public final static int IHDR_COMPRESSION_METHOD_LENGTH_BYTE = 1;
    /**
     * Lunghezze, in byte, del componente metodo di filtraggio del chunk IHDR.
     */
    public final static int IHDR_FILTER_METHOD_LENGTH_BYTE = 1;
    /**
     * Lunghezze, in byte, del componente metodo di interlacciamento del chunk
     * IHDR.
     */
    public final static int IHDR_INTERLACE_METHOD_LENGTH_BYTE = 1;

    /**
     * Nome del chunk IHDR.
     */
    public final static String MAIN_CHUNK_NAME = "IHDR";
    /**
     * Nome del chunk IDAT.
     */
    public final static String IMAGE_DATA_CHUNK_NAME = "IDAT";
    /**
     * Nome del chunk PLTE.
     */
    public final static String PALETTE_CHUNK_NAME = "PLTE";
    /**
     * Nome del chunk gAMA.
     */
    public final static String GAMMA_CHUNK_NAME = "gAMA";
    /**
     * Nome del chunk pHYs.
     */
    public final static String PHYSICAL_PIXEL_DIMENSION_CHUNK_NAME = "pHYs";
    /**
     * Nome del chunk tRNS.
     */
    public final static String TRANSPARENCY_CHUNK_NAME = "tRNS";
    /**
     * Nome del chunk bKGD.
     */
    public final static String BACKGROUND_CHUNK_NAME = "bKGD";
    /**
     * Nome del chunk cHRM.
     */
    public final static String CHROMACITIES_CHUNK_NAME = "cHRM";
    /**
     * Nome del chunk tEXt.
     */
    public final static String TEXT_CHUNK_NAME = "tEXt";
    /**
     * Nome del chunk sRGB.
     */
    public final static String STANDARD_RGB_COLOR_SPACE_CHUNK_NAME = "sRGB";

    /**
     * Lunghezza della stringa in alfabeto esadecimale contenente il CRC di un
     * chunk.
     */
    public static final int CRC_HEX_LENGTH = 8;

    /**
     * Identificativo del tipo di colore grayscale.
     */
    public static final int COLOR_TYPE_GRAYSCALE = 0;
    /**
     * Identificativo del tipo di colore RGB.
     */
    public static final int COLOR_TYPE_RGB = 2;
    /**
     * Identificativo del tipo di colore tavolozza.
     */
    public static final int COLOR_TYPE_PALETTE = 3;
    /**
     * Identificativo del tipo di colore grayscale con alpha.
     */
    public static final int COLOR_TYPE_GRAYSCALE_ALPHA = 4;
    /**
     * Identificativo del tipo di colore RGB con alpha.
     */
    public static final int COLOR_TYPE_RGB_ALPHA = 6;

    /**
     * Identificativo per immagini senza interlacciamento.
     */
    public static final int NO_INTERLACE = 0;
    /**
     * Identificativo per immagini con interlacciamento Adam7.
     */
    public static final int ADAM7_INTERLACE = 1;

    /**
     * Numero di campioni per il tipo di colore grayscale.
     */
    public static final int COLOR_TYPE_0_SAMPLES = 1;
    /**
     * Numero di campioni per il tipo di colore RGB.
     */
    public static final int COLOR_TYPE_2_SAMPLES = 3;
    /**
     * Numero di campioni per il tipo di colore grayscale con alpha.
     */
    public static final int COLOR_TYPE_4_SAMPLES = 2;
    /**
     * Numero di campioni per il tipo di colore RGB con alpha.
     */
    public static final int COLOR_TYPE_6_SAMPLES = 4;

    /**
     * Identificativo per scanline senza filtro.
     */
    public static final int NO_FILTER = 0;
    /**
     * Identificativo per scanline con filtro basato sul pixel precedente.
     */
    public static final int PREVIOUS_FILTER = 1;
    /**
     * Identificativo per scanline con filtro basato sul pixel superiore.
     */
    public static final int UP_FILTER = 2;
    /**
     * Identificativo per scanline con filtro basato sulla media degli
     * adiacenti.
     */
    public static final int AVERAGE_FILTER = 3;
    /**
     * Identificativo per scanline con filtro Paeth.
     */
    public static final int PAETH_FILTER = 4;

    /**
     * Costruttore privato dell'oggetto.
     */
    private PNGConstants() {
    }

}
