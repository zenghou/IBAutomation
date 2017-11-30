//@@author zenghou
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;


import logic.EWrapperImplementation;
import logic.Logic;
import logic.LogicManager;
import model.Model;
import model.ModelManager;

public class MainApp {
    private EWrapperImplementation eWrapper; // Mechanism through which TWS delivers information to client app
    private EClientSocket eClientSocket; // Mechanism through which client app delivers information to TWS
    private EReaderSignal eReaderSignal;
    private EReader eReader;

    protected Logic logic;
    protected Model model;

    public void init() throws Exception {
        model = new ModelManager();

        eWrapper = new EWrapperImplementation(model);
        eClientSocket = eWrapper.getClient();
        eReaderSignal = eWrapper.getSignal();

        logic = new LogicManager(model, eClientSocket);

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

    public static void main(String[] args) throws Exception {
        MainApp mainApp = new MainApp();
        mainApp.init();
        mainApp.logic.getRealTimeBars();
        // mainApp.logic.cancelRealTimeBars(mainApp.eClientSocket);
        // mainApp.stop();
    }

    /**
     * Disconnects the client app
     */
    public void stop() {
        System.out.println("User has requested to terminate this session");
        eClientSocket.eDisconnect();
    }
}
