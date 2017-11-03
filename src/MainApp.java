import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;


import logic.Logic;
import logic.LogicManager;
import model.Model;
import model.ModelManager;
import samples.testbed.ewrapper.EWrapperImpl;

public class MainApp {
    protected EWrapperImpl eWrapper; // Mechanism through which TWS delivers information to client app
    protected EClientSocket eClientSocket; // Mechanism through which client app delivers information to TWS
    protected EReaderSignal eReaderSignal;
    protected EReader eReader;

    protected Logic logic;
    protected Model model;

    public void init() throws Exception {
        model = new ModelManager();
        logic = new LogicManager(model);

        eWrapper = new EWrapperImpl(model);
        eClientSocket = eWrapper.getClient();
        eReaderSignal = eWrapper.getSignal();

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
        mainApp.logic.getRealTimeBars(mainApp.eClientSocket);
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
