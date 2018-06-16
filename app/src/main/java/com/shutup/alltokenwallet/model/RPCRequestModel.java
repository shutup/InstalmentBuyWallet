package com.shutup.alltokenwallet.model;

import java.util.ArrayList;
import java.util.List;

public class RPCRequestModel {
    String jsonrpc;
    String method;
    List<String> params;
    int id;

    public RPCRequestModel(String method, String... params) {
        List<String> paramsList = new ArrayList<>();
        for (String param :
                params) {
            paramsList.add(param);
        }

        this.jsonrpc = "2.0";
        this.method = method;
        this.params = paramsList;
        this.id = 1;
    }

    public RPCRequestModel(String jsonrpc, String method, List<String> params, int id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
