package Handlers;

import Blockchain.Block;
import TcpServer.*;
import Main.Main;
import TcpServer.Response.BaseResponse;
import TcpServer.Response.JsonResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import utils.Serializer.Serializer;

public class LastBlockHandle extends BaseServerHandler {

    /**
     * 最后一个区块信息
     * @return e.g
     * {
     *     "index": 1,
     *     "prev_hash": "0",
     *     "proof": 0,
     *     "hash": "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
     *     "timestamp": "1609083612394",
     *     "txns": []
     * }
     */
    public BaseResponse get(BaseRequest request) {
        Block lastBlock = Main.blockchain.last_block();
        Serializer ser = new Serializer(lastBlock);
        JSONObject blockJsonObj = ser.serializeToObject();
        return new JsonResponse(request.exchange, blockJsonObj);
    }

}
