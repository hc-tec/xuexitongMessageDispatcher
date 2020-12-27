package TcpServer.Response;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;

public class ErrorResponse extends JsonResponse {
    public ErrorResponse(HttpExchange exchange, String errCode, String errMsg) {
        super(exchange, new JSONObject());
        JSONObject ret = new JSONObject();
        ret.put("code", errCode);
        ret.put("msg", errMsg);
        super.responseString = ret.toJSONString();
    }
}
