//@@author zenghou
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;

import logic.DateBuilder;
import logic.EWrapperImplementation;
import logic.Logic;
import logic.LogicManager;
import model.Model;
import model.ModelManager;
import model.UniqueContractList;

public class MainApp {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private EWrapperImplementation eWrapper; // Mechanism through which TWS delivers information to client app
    private EClientSocket eClientSocket; // Mechanism through which client app delivers information to TWS
    private EReaderSignal eReaderSignal;
    private EReader eReader;
    private Timer timer;

    protected Logic logic;
    protected Model model;

    /**
     * Initialize the parts required for all the processes.
     */
    private void init() throws Exception {
        LOGGER.log(Level.INFO, "=============================[ Initializing IB Automatic Trading ]===========================");

        model = new ModelManager();

        eWrapper = new EWrapperImplementation(model);
        eClientSocket = eWrapper.getClient();
        eReaderSignal = eWrapper.getSignal();

        logic = new LogicManager(model, eClientSocket, eWrapper);

        for (UniqueContractList eachList : model.getUniqueContractLists().getArrayList()) {
            eachList.addObserver(logic);
        }

        timer = new Timer();

        // Connect to server
        eClientSocket.eConnect("127.0.0.1", 7496, 0);

        eReader = new EReader(eClientSocket, eReaderSignal);
        eReader.start();

        //An additional thread is created in this program design to empty the messaging queue
        new Thread(() -> {
            while (eClientSocket.isConnected()) {
                eReaderSignal.waitForSignal();
                try {
                    eReader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }).start();

        // A pause to give the application time to establish the connection
        // In a production application, it would be best to wait for callbacks to confirm the connection is complete
        Thread.sleep(1000);
    }

    /**
     * Starts the sell limit order process.
     */
    private void initiateSellLimitOrders() throws Exception {
        LOGGER.log(Level.INFO, "=============================[ Initiate Sell Limit Orders ]===========================");
        // retrieving a list of symbols from previous trading day
        logic.requestAccountUpdates();

        // pause thread for 1.5 seconds to allow updates to be completed
        Thread.sleep(1500);

        // Terminate subscription of account updates
        logic.cancelAccountUpdates();

        // To log the correct stocks that are to be closed
        model.printAllStocksInUniqueContractToCloseList();

        // submit sell limit orders to IB
        logic.setLimitSellOrdersForAllExistingPositions(50);
    }

    /**
     * Initiates the scheduled tasks of cancelling unfilled limit sell orders and executing MOC orders for the respective
     * stocks at a fixed time {@see DateBuilder}.
     */
    private void initiateScheduledCancellationAndMarketClose() {
        LOGGER.log(Level.INFO, "=============================[ Initiate Scheduled Tasks ]===========================");

        timer.schedule(logic.getScheduledCancelUnfilledOrdersTask(), DateBuilder.getCancellationTime());
        timer.schedule(logic.getScheduledMarketOnCloseTask(), DateBuilder.getMarketOnCloseTime());
    }

    /**
     * Starts the buying process
     */
    private void initiateBuying() throws Exception {
        LOGGER.log(Level.INFO, "=============================[ Initiate Buying Process ]===========================");
        // submitting of valid orders for today's session
        logic.getRealTimeMarketData();

        // read CSV file every 30 seconds
        timer.schedule(logic.getParser(), 0, 30000);
    }

    /**
     * Sets up the logger and its handlers for console and file output.
     */
    public static void initLogger() throws Exception {
        LogManager.getLogManager().reset();
        LOGGER.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        LOGGER.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("IBAutomaticTradingLog");
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.INFO);
        LOGGER.addHandler(fileHandler);
    }

    /**
     * Disconnects the client app
     */
    public void stop() {
        LOGGER.log(Level.INFO, "=============================[ Terminating Session ]===========================");

        eClientSocket.eDisconnect();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) throws Exception {
        MainApp.initLogger();

        MainApp mainApp = new MainApp();
        mainApp.init();

         mainApp.initiateSellLimitOrders();

         mainApp.initiateScheduledCancellationAndMarketClose();

        mainApp.initiateBuying();
    }
}
