package Handlers;

import Blockchain.Transaction.Transaction;
import Main.Main;
import TcpServer.BaseRequest;
import TcpServer.BaseServerHandler;
import TcpServer.Response.BaseResponse;
import TcpServer.Response.JsonResponse;
import com.alibaba.fastjson.JSONArray;
import utils.Serializer.Serializer;

import java.util.ArrayList;

public class TransactionHandle extends BaseServerHandler {

    /**
     * 获取当前交易，还未并入到链中
     */
    public BaseResponse get(BaseRequest request) {
        ArrayList<Transaction> txns = Main.blockchain.txns;
        Serializer ser = new Serializer(txns);
        JSONArray data = ser.serializeToArray();
        return new JsonResponse(request.exchange, data);
    }

}
