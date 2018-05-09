package png.manager.checker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import png.manager.entity.PNGImage;

/**
 * Fornisce alcune funzionalità per il controllo dell'integrità delle immagini
 * PNG.
 */
public class Checker {

    /**
     * Contenitore delle combinazioni valide di tipo di colore e profondità di
     * bit, secondo le specifiche del formato.
     */
    private static final HashMap<Integer, HashSet<Integer>> colorTypes_bitDepths_combinations;
    
    //Codice eseguito all'atto del caricamento della classe
    static {
        colorTypes_bitDepths_combinations = new HashMap<>();

        //Ogni pixel è un campione di grayscale
        colorTypes_bitDepths_combinations.put(0, new HashSet<>(Arrays.asList(1, 2, 4, 8, 16)));

        //Ogni pixel è una tripletta RGB
        colorTypes_bitDepths_combinations.put(2, new HashSet<>(Arrays.asList(8, 16)));

        //Ogni pixel è un indice ad una tavolozza; deve apparire il chunk PLTE
        colorTypes_bitDepths_combinations.put(3, new HashSet<>(Arrays.asList(1, 2, 4, 8)));

        //Ogni pixel è un campione di grayscale, seguito da un campione alpha
        colorTypes_bitDepths_combinations.put(4, new HashSet<>(Arrays.asList(8, 16)));

        //Ogni pixel è una tripletta RGB, seguita da un campione alpha.
        colorTypes_bitDepths_combinations.put(6, new HashSet<>(Arrays.asList(8, 16)));
    }

    /**
     * Costruttore privato della classe.
     */
    private Checker() {
    }

    /**
     * Controlla se la combinazione di tipo di colore e profondità dei bit
     * dell'immagine è valida.
     *
     * @param image immagine su cui eseguire il controllo
     * @return <code>true</code> se la combinazione è valida, <code>false</code>
     * altrimenti
     */
    public static boolean checkColorTypeBitDepthCombination(PNGImage image) {
        return colorTypes_bitDepths_combinations.get(image.getColorType()).contains(image.getBitDepth());
    }

    /**
     * Controlla se le dimensioni dell'immagine sono non nulle.
     *
     * @param image immagine su cui eseguire il controllo
     * @return <code>true</code> se le dimensioni sono valide,
     * <code>false</code> altrimenti
     */
    public static boolean checkDimension(PNGImage image) {
        return (image.getWidth() > 0 && image.getHeight() > 0);
    }
}
