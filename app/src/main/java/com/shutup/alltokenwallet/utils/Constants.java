package com.shutup.alltokenwallet.utils;

public interface Constants {

    int REQUEST_CODE_SCAN = 1;
    int REQUEST_CODE_USE_CAMERA = 2;
    int REQUEST_CODE_USE_SDCARD = 3;

    String ACCOUNT_INFO_KEY = "ACCOUNT_INFO_KEY";

    String RPC_METHOD_GET_BALANCE = "eth_getBalance";
    String RPC_METHOD_GET_TRANSACTION_COUNT = "eth_getTransactionCount";
    String RPC_METHOD_SEND_RAW_TRANACTION = "eth_sendRawTransaction";

    String HTTP_NODE_URL = "https://kovan.infura.io/vknyxyJN14EVybheGKVV";
    String Token_Address = "0x59d81c8024fdb7e518eb7a11fee72e5ed0073cff";

}
