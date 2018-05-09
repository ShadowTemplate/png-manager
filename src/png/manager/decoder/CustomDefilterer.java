package png.manager.decoder;

import java.util.Arrays;

import png.manager.entity.PNGImage;
import png.manager.exception.DecodingException;
import png.manager.miscellaneous.PNGConstants;
import png.manager.miscellaneous.Utility;

/**
 * Defilterer personale per la decodifica delle immagini.
 */
class CustomDefilterer {

    /**
     * Costruttore privato della classe.
     */
    private CustomDefilterer() {
    }

    /**
     * Applica l'algoritmo di defiltering sui dati in input dell'immagine.
     *
     * @param image immagine risultato del parsing
     * @param filteredData dati filtrati
     * @return dati defiltrati
     * @throws DecodingException - se viene incontrato un filtro sconosciuto
     */
    public static byte[] defilterImage(PNGImage image, byte[] filteredData) throws DecodingException {
        int pixelWidth = getPixelWidth(image.getBitDepth(), image.getColorType());
        int scanlineSize = getScanlineSize(image.getColorType(), image.getWidth(), pixelWidth, image.getBitDepth());
        byte[] defilteredData = new byte[filteredData.length - image.getHeight()], previousScanline = null;

        for (int i = 0; i < image.getHeight(); i++) {
            byte[] currScanline = Arrays.copyOfRange(filteredData, i * scanlineSize, (i + 1) * scanlineSize);
            byte filterType = currScanline[0];
            switch (filterType) {
                case PNGConstants.NO_FILTER:
                    break;
                case PNGConstants.PREVIOUS_FILTER:
                    currScanline = previousFilter(currScanline, scanlineSize, pixelWidth);
                    break;
                case PNGConstants.UP_FILTER:
                    currScanline = upFilter(currScanline, previousScanline, scanlineSize, (i <= 0));
                    break;
                case PNGConstants.AVERAGE_FILTER:
                    currScanline = averageFilter(currScanline, previousScanline, scanlineSize, pixelWidth, (i <= 0));
                    break;
                case PNGConstants.PAETH_FILTER:
                    currScanline = paethFilter(currScanline, previousScanline, scanlineSize, pixelWidth, (i <= 0));
                    break;
                default:
                    throw new DecodingException("Filtro sconosciuto incontrato.");
            }

            System.arraycopy(currScanline, 1, defilteredData, i * (scanlineSize - 1), currScanline.length - 1);
            previousScanline = currScanline;
        }

        return defilteredData;
    }

    /**
     * Applica lo specifico algoritmo di defiltering, indicato dal tipo di
     * filtro.
     *
     * @param scanlineData scanline filtrata
     * @param scanlineSize dimensione della scanline
     * @param pixelWidth ampiezza del pixel
     * @return scanline defiltrata
     */
    private static byte[] previousFilter(byte[] scanlineData, int scanlineSize, int pixelWidth) {
        for (int i = pixelWidth + 1; i < scanlineSize; i++) {
            scanlineData[i] = (byte) (scanlineData[i] + scanlineData[i - pixelWidth]);
        }

        return scanlineData;
    }

    /**
     * Applica lo specifico algoritmo di defiltering, indicato dal tipo di
     * filtro.
     *
     * @param scanlineData scanline filtrata
     * @param previousScanline scanline precedente
     * @param scanlineSize dimensione della scanline
     * @param firstScanline  <code>true</code> se la scanline è la prima
     * dell'immagine
     * @return scanline defiltrata
     */
    private static byte[] upFilter(byte[] scanlineData, byte[] previousScanline, int scanlineSize, boolean firstScanline) {
        if (!firstScanline) {
            for (int j = 1; j < scanlineSize; j++) {
                scanlineData[j] = (byte) (scanlineData[j] + previousScanline[j]);
            }
        }

        return scanlineData;
    }

    /**
     * Applica lo specifico algoritmo di defiltering, indicato dal tipo di
     * filtro.
     *
     * @param scanlineData scanline filtrata
     * @param previousScanline scanline precedente
     * @param scanlineSize dimensione della scanline
     * @param pixelWidth ampiezza del pixel
     * @param firstScanline  <code>true</code> se la scanline è la prima
     * dell'immagine
     * @return scanline defiltrata
     */
    private static byte[] averageFilter(byte[] scanlineData, byte[] previousScanline, int scanlineSize, int pixelWidth, boolean firstScanline) {
        int rscan, pscan;
        for (int j = 1; j < scanlineSize; j++) {
            rscan = (!firstScanline) ? (Utility.byteToUnsignedInt(previousScanline[j])) : 0;
            pscan = (j > pixelWidth) ? (Utility.byteToUnsignedInt(scanlineData[j - pixelWidth])) : 0;
            scanlineData[j] = (byte) (scanlineData[j] + Math.floor((rscan + pscan) / 2));
        }
        return scanlineData;
    }

    /**
     * Applica lo specifico algoritmo di defiltering, indicato dal tipo di
     * filtro.
     *
     * @param scanlineData scanline filtrata
     * @param previousScanline scanline precedente
     * @param scanlineSize dimensione della scanline
     * @param pixelWidth ampiezza del pixel
     * @param firstScanline  <code>true</code> se la scanline è la prima
     * dell'immagine
     * @return scanline defiltrata
     */
    private static byte[] paethFilter(byte[] scanlineData, byte[] previousScanline, int scanlineSize, int pixelWidth, boolean firstScanline) {
        byte rscan, p1scan, p2scan;
        for (int j = 1; j < scanlineSize; j++) {
            rscan = (!firstScanline) ? previousScanline[j] : 0;
            p1scan = (j > pixelWidth) ? scanlineData[j - pixelWidth] : 0;
            p2scan = (!firstScanline && j > pixelWidth) ? previousScanline[j - pixelWidth] : 0;
            scanlineData[j] = (byte) (scanlineData[j] + paethPredictor(p1scan, rscan, p2scan));
        }

        return scanlineData;
    }

    /**
     * Calcola il valore del pixel, basandosi sui tre pixel adiacenti a quello
     * in esame.
     *
     * @param a pixel a sinistra
     * @param b pixel superiore
     * @param c pixel in alto a sinistra
     * @return valore predetto secondo l'algoritmo di Paeth
     */
    private static byte paethPredictor(byte a, byte b, byte c) {
        int ua = Utility.byteToUnsignedInt(a), ub = Utility.byteToUnsignedInt(b), uc = Utility.byteToUnsignedInt(c);
        int p = ua + ub - uc, pa = Math.abs(p - ua), pb = Math.abs(p - ub), pc = Math.abs(p - uc);

        if (pa <= pb && pa <= pc) {
            return a;
        }

        if (pb <= pc) {
            return b;
        }

        return c;
    }

    /**
     * Calcola la dimensione della scanline in base al tipo di immagine.
     *
     * @param colorType il tipo di colore dell'immagine
     * @param imageWidth larghezza dell'immagine
     * @param pixelWidth ampiezza del pixel
     * @param bitDepth profondità di bit
     * @return dimensione della scanline
     */
    private static int getScanlineSize(int colorType, int imageWidth, int pixelWidth, int bitDepth) {
        if (colorType == 0 && bitDepth == 1) {
            int bytes = (int) (imageWidth / 8);
            if ((imageWidth % 8) != 0) {
                bytes++;
            }

            return bytes + 1;
        }

        return (colorType == 0 || colorType == 3) ? (int) (1 + Math.ceil(imageWidth * pixelWidth)) : 1 + imageWidth * pixelWidth;
    }

    /**
     * Calcola la larghezza di un pixel.
     *
     * @param bitDepth profondità di bit dell'immagine
     * @param colorType tipo di colore dell'immagine
     * @return larghezza di un pixel
     */
    private static int getPixelWidth(int bitDepth, int colorType) {
        int bpp = 0;
        switch (colorType) {
            case 0:
                bpp = Math.max(1, (bitDepth * PNGConstants.COLOR_TYPE_0_SAMPLES) >> 3);
                break;
            case 2:
                bpp = Math.max(1, (bitDepth * PNGConstants.COLOR_TYPE_2_SAMPLES) >> 3);
                break;
            case 4:
                bpp = Math.max(1, (bitDepth * PNGConstants.COLOR_TYPE_4_SAMPLES) >> 3);
                break;
            case 6:
                bpp = Math.max(1, (bitDepth * PNGConstants.COLOR_TYPE_6_SAMPLES) >> 3);
                break;
        }
        return bpp;
    }
}
