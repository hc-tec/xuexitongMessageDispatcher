package Handlers;

import com.alibaba.fastjson.JSONObject;

import Main.Main;
import Blockchain.Blockchain;
import TcpServer.BaseRequest;
import TcpServer.BaseServerHandler;
import TcpServer.Response.BaseResponse;
import TcpServer.Response.JsonResponse;
import utils.Serializer.Serializer;


public class BlockchainHandle extends BaseServerHandler {
    /**
     * 区块链信息 API
     * @return 当前区块链信息
     * e.g:
     * {
     *     "chain": [
     *         {
     *             "index": 1,
     *             "prev_hash": "0",
     *             "proof": 0,
     *             "hash": "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
     *             "timestamp": "1609083612394",
     *             "txns": []
     *         }
     *     ]
     * }
     */
    public BaseResponse post(BaseRequest request) {
        Blockchain chain = Main.blockchain;
        Serializer ser = new Serializer(chain);
        String[] includeFields = {"chain"};
        JSONObject data = ser.serializeToObject(includeFields);
        return new JsonResponse(request.exchange, data);
    }

}
