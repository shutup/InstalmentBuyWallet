package com.shutup.alltokenwallet.network;

import com.shutup.alltokenwallet.model.RPCRequestModel;
import com.shutup.alltokenwallet.model.RPCResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WalletService {
    @POST("/wallet/getBalance")
    Call<RPCResponseModel> getBalance(@Body RPCRequestModel rpcRequestModel);

    @POST("/wallet/getTransactionCount")
    Call<RPCResponseModel> getTransactionCount(@Body RPCRequestModel rpcRequestModel);

    @POST("/wallet/sendRawTransaction")
    Call<RPCResponseModel> sendRawTransaction(@Body RPCRequestModel rpcRequestModel);

}
