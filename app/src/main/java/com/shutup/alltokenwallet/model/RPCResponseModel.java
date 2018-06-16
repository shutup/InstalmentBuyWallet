package com.shutup.alltokenwallet.model;

public class RPCResponseModel {

    String jsonrpc;
    String result;
    int id;

    public RPCResponseModel(String jsonrpc, String result, int id) {
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
