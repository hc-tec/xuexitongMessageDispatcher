package utils.Serializer;

import Store.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.ArrayList;

public class Serializer {

    private final Object instance;

    public Serializer(Object instance) {
        this.instance = instance;
    }

    public JSONObject serializeToObject() {
        return this.serializeToObject(this.instance);
    }

    public JSONObject serializeToObject(String[] includeFields) {
        return this.serializeToObject(this.instance, includeFields);
    }

    public JSONObject serializeToObject(Object instance) {
        String jsonStr = this.getJsonStr(instance);
        return JSON.parseObject(jsonStr);
    }

    public JSONObject serializeToObject(Object instance, String[] includeFields) {
        String jsonStr = this.getJsonStr(instance, includeFields);
        return JSON.parseObject(jsonStr);
    }

    public JSONArray serializeToArray() {
        JSONArray serializeList = new JSONArray();
        for (Object instance : (ArrayList) this.instance) {
            Object serializeItem = this.serializeToObject(instance);
            serializeList.add(serializeItem);
        }
        return serializeList;
    }

    public JSONArray serializeToArray(String[] includeFields) {
        JSONArray serializeList = new JSONArray();
        for (Object instance : (ArrayList) this.instance) {
            Object serializeItem = this.serializeToObject(instance, includeFields);
            serializeList.add(serializeItem);
        }
        return serializeList;
    }

    private String getJsonStr(Object instance) {
        return JSON.toJSONString(instance);
    }

    private String getJsonStr(Object instance, String[] includeFields) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(instance.getClass(), includeFields);
        return JSON.toJSONString(instance, filter);
    }
}
