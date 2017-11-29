//@@author zenghou
package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.Model;

/**
 * Parses a CSV file of ticker symbols and add
 */
public class Parser {
    private Model model;
    private Scanner scanner;

    public Parser(String filepath, Model model) {
        setModel(model);
        try {
            scanner = new Scanner(new File(filepath));
            scanner.useDelimiter(",");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    /** Called by constructor to set Model of this parser */
    private void setModel(Model model) {
        this.model = model;
    }

    /**
     * Reads CSV file from filepath with Scanner and adds each symbol into Model's {@code listOfSymbols}
     */
    public void readDataUpdateModel() {
        while (scanner.hasNext()) {
            model.getListOfSymbolsArray().add(scanner.next());
        }
    }
}
