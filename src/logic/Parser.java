//@@author zenghou
package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimerTask;

import model.ContractBuilder;
import model.Model;

/**
 * Parses a CSV file of ticker symbols and adds Contracts to the {@see UniqueContractList} periodically (estimated to be
 * every 30 seconds)
 */
public class Parser extends TimerTask {
    private Model model;
    private Scanner scanner;
    private String filepath;

    public Parser(String filepath, Model model) {
        this.filepath = filepath;
        setModel(model);

        try {
            scanner = new Scanner(new File(filepath));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    /** Called by constructor to set Model of this parser */
    private void setModel(Model model) {
        this.model = model;
    }

    /**
     * Reads CSV file from filepath with Scanner and adds each symbol into Model's {@code tickerPriceHashMap}
     * for the first time
     */
    public void readDataUpdateModel() {
        while (scanner.hasNextLine()) {
            // separate each line with a space
            String eachLine = scanner.nextLine();
            String[] tickerPriceList = eachLine.split(" ");
            String ticker = tickerPriceList[0];
            Double price = Double.parseDouble(tickerPriceList[1]);

            HashMap<String, Double> tickerPriceHashMap = model.getTickerPriceHashMap();
            tickerPriceHashMap.put(ticker, price);
        }
    }

    /**
     * Continually opens and checks a CSV file for updated ticker symbols and adds them to the model's
     * {@see UniqueContractList}
     * @throws FileNotFoundException
     */
    private void checkForUpdates() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));

        while (scanner.hasNextLine()) {
            String eachLine = scanner.nextLine();
            String[] tickerPriceList = eachLine.split(" ");
            String ticker = tickerPriceList[0];
            Double price = Double.parseDouble(tickerPriceList[1]);

            HashMap<String, Double> tickerPriceHashMap = model.getTickerPriceHashMap();

            // only add to tickerPriceHashMap if ticker is not already inside
            if (!tickerPriceHashMap.containsKey(ticker)) {
                tickerPriceHashMap.put(ticker, price);

                model.updateUniqueContractList(ContractBuilder.buildContractWithPriceDetail(ticker, price));
            }

        }
    }

    // To be called by Timer object
    @Override
    public void run() {
        try {
            checkForUpdates();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
