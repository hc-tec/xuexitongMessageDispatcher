package TcpServer.Response;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;


public class JsonResponse extends BaseResponse {

    public JsonResponse(HttpExchange exchange, JSON responseObj) {
        super(exchange, "");
        Headers headers = super.exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept");

        String responseString = responseObj.toJSONString();
        super.responseString = responseString;
    }

}
