package model;

import java.util.ArrayList;

/** Keeps track of {@see OpenOrderDetails} created when {@see EWrapperImplementation#OpenOrder} is called */
public class OpenOrderDetailList {
    private ArrayList<OpenOrderDetail> openOrderDetailArrayList;

    public OpenOrderDetailList() {
        openOrderDetailArrayList = new ArrayList<>();
    }

    public String retrieveSymbolFromOrderId(int orderId) {
        for (OpenOrderDetail openOrderDetail : openOrderDetailArrayList) {
            if (openOrderDetail.matchesOrderId(orderId)) {

                try {
                    return openOrderDetail.getSymbol();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return "";
    }

    public void add(OpenOrderDetail openOrderDetail) throws Exception {
        if (openOrderDetailArrayList.contains(openOrderDetail)) {
            throw new Exception("Duplicate openOrderDetail exception! Symbol: " + openOrderDetail.getSymbol());
        }
        openOrderDetailArrayList.add(openOrderDetail);
    }
}
