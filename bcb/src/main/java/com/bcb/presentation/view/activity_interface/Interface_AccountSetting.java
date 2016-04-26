package com.bcb.presentation.view.activity_interface;

/**
 * Created by cain on 16/3/28.
 */
public interface Interface_AccountSetting extends Interface_Base {
    void onRequestResult(boolean hasCert,
                         boolean hasTradePassword,
                         String userName,
                         String IDCard,
                         String cardNumber,
                         String localPhone,
                         String companyMessage);
}
