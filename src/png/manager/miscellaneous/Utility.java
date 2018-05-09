package png.manager.miscellaneous;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

import png.manager.entity.Chunk;
import png.manager.entity.PNGImage;

/**
 * Fornisce diverse funzionalità necessarie al programma.
 */
public class Utility {

    /**
     * Elenco dei caratteri presenti nel sistema numerico esadecimale.
     */
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Dimensioni dello schermo sul quale sta venendo visualizzato il programma.
     */
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Costruttore privato dell'oggetto.
     */
    private Utility() {
    }

    /**
     * Converte un array di byte in un array di caratteri.
     *
     * @param bytes array di byte da convertire
     * @return array di caratteri convertiti
     */
    public static char[] bytesToHexChar(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return hexChars;
    }

    /**
     * Converte un array di byte in una stringa in alfabeto esadecimale.
     *
     * @param bytes array di byte da convertire
     * @return stringa convertita
     */
    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = bytesToHexChar(bytes);
        return new String(hexChars);
    }

    /**
     * Converte un array di byte in una stringa in alfabeto esadecimale,
     * inserendo uno spazio dopo ogni coppia di caratteri.
     *
     * @param bytes array di byte da convertire
     * @return stringa convertita
     */
    public static String bytesToSplittedHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        char[] hexChars = bytesToHexChar(bytes);
        for (int i = 0; i < hexChars.length; i++) {
            sb.append(hexChars[i]);
            //Controllo se ho scandito una coppia
            if (i % 2 == 1 && i != hexChars.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Converte una stringa in alfabeto esadecimale in una stringa in alfabeto
     * decimale.
     *
     * @param hex stringa in alfabeto esadecimale da convertire
     * @return stringa in alfabeto decimale convertita
     */
    public static String hexStringToASCIIString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) { 
            //Prendo i caratteri a coppie
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            tmp.append(decimal);
        } 

        return sb.toString();
    }

    /**
     * Converte una stringa in alfabeto esadecimale in un intero.
     *
     * @param hex stringa in alfabeto esadecimale da convertire
     * @return intero convertito
     */
    public static int hexStringToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * Converte un byte in un intero senza segno.
     *
     * @param b byte da convertire
     * @return intero senza segno convertito
     */
    public static int byteToUnsignedInt(byte b) {
        return ((int) b & 0x000000FF);
    }

    /**
     * Converte una coppia di byte in un intero senza segno.
     *
     * @param b1 primo byte da convertire
     * @param b2 secondo byte ad convertire
     * @return intero senza segno convertito
     */
    public static int bytes2ToUnsignedInt(byte b1, byte b2) {
        return (b1 & 0x000000FF) | (b2 & 0x000000FF) << 8;
    }

    /**
     * Ritorna un sub-array della lunghezza specificata in input a partire da un
     * array passato in input.
     *
     * @param array array d'origine
     * @param len lunghezza del sub-array
     * @param offset elemento da cui cominciare la copia
     * @return sub-array
     */
    public static byte[] getBytesFromArray(byte[] array, int len, int offset) {
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = array[i + offset];
        }
        return data;
    }

    /**
     * Concatena due array di byte.
     *
     * @param A primo array da concatenare
     * @param B secondo array da concatenare
     * @return concatenazione dei due array
     */
    public static byte[] concatArray(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    /**
     * Effettua una copia dell'immagine in input. I due oggetti sono
     * indipendenti.
     *
     * @param image immagine da copiare
     * @return immagine clonata
     */
    public static BufferedImage copyImage(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Scrive su un file di testo un report delle caratteristiche di una
     * immagine PNG.
     *
     * @param path percorso del file di testo da scrivere
     * @param content caratteristiche dell'immagine
     * @return <code>true</code> se l'operazione è terminata con successo,
     * <code>false</code> altrimenti.
     */
    public static boolean createReport(String path, String content) {
        PrintWriter out = null;
        boolean success = false;
        try {
            //Sostituisce i ritorni a capo
            content = content.replace("<br>", System.getProperty("line.separator"));
            //Rimuove la tabulazione
            content = content.replace("&#9;", "   ");
            //Rimuove il codice html
            content = content.replaceAll("\\<.*?>", "");
            //Scrive il report sul file di testo
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path)), true);
            out.println("Report generato da PNG Manager:");
            out.println(content);
            success = true;
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return success;
    }

    /**
     * Salva l'immagine in input in un nuovo file, senza scrivere chunk
     * ausiliari.
     *
     * @param path percorso della nuova immagine
     * @param image immagine da scrivere
     * @return <code>true</code> se l'operazione è terminata con successo,
     * <code>false</code> altrimenti.
     */
    public static boolean exportImage(String path, PNGImage image) {
        FileOutputStream fos = null;
        boolean success = false;
        try {
            fos = new FileOutputStream(path);
            fos.write(PNGConstants.FORMAT_SIGNATURE);

            for (Chunk c : image.getChunks()) {
                if (!c.isAncillary()) {
                    //Scrivo i 4 campi del chunk
                    fos.write(c.getLength());
                    fos.write(c.getType());
                    fos.write(c.getData());
                    fos.write(c.getCRC());
                }
            }

            success = true;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        return success;
    }
    
    /**
     * Salva l'immagine in input in un file in formato jpeg.
     * 
     * @param path percorso del file da scrivere
     * @param image immagine da scrivere
     * @return <code>true</code> se l'operazione è terminata con successo,
     * <code>false</code> altrimenti.
     */
    public static boolean convertImage(String path, BufferedImage image) {
        boolean success = false;
        try{
            ImageIO.write(image, "jpg", new File(path));
            success = true;
        } catch (IOException e) {
            System.err.println(e.getMessage());        
        }
        
        return success;
    }

    /**
     * Ritorna la larghezza dello schermo su cui è visualizzata l'interfaccia
     * grafica.
     *
     * @return dimensione in pixel
     */
    public static int getScreenWidth() {
        return (int) screenSize.getWidth();
    }

    /**
     * Ritorna l'altezza dello schermo su cui è visualizzata l'interfaccia
     * grafica.
     *
     * @return dimensione in pixel
     */
    public static int getScreenHeight() {
        return (int) screenSize.getHeight();
    }
}
