package png.manager.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

import png.manager.miscellaneous.PNGConstants;

/**
 * Fornisce un metodo per filtrare i file supportati dal programma nelle
 * finestre di dialogo per la selezione di file.
 */
public class ExtensionFilter extends FileFilter {

    /**
     * Lista delle estensioni supportate.
     */
    private final ArrayList<String> supportedExt;

    /**
     * Costruisce l'oggetto inizializzando le estensioni supportate.
     */
    public ExtensionFilter() {
        supportedExt = new ArrayList<>();
        supportedExt.add(PNGConstants.LOWERCASE_EXTENSION);
        supportedExt.add(PNGConstants.UPPERCASE_EXTENSION);
    }

    /**
     * Controlla se il file specificato è una cartella o se ha una estensione
     * presente tra la lista di quelle supportate.
     *
     * @param f il file che deve essere controllato
     * @return <code>true</code> se il file è una cartella o ha un'estensione
     * valida, <code>false</code> altrimenti
     */
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || checkExtension(f.getName());
    }

    /**
     * Controlla se il file specificato ha un'estensione valida.
     *
     * @param fileName il nome del file da controllare
     * @return <code>true</code> se il file è valido, <code>false</code>
     * altrimenti
     */
    private boolean checkExtension(String fileName) {
        if (!fileName.contains(".")) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        return supportedExt.indexOf(extension) != -1;
    }

    /**
     * Ritorna una descrizione del filtro.
     *
     * @return la descrizione del filtro
     */
    @Override
    public String getDescription() {
        return "Immagine PNG";
    }
}
