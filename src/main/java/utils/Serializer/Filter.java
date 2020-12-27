package utils.Serializer;

import com.alibaba.fastjson.serializer.PropertyFilter;

import java.util.ArrayList;

public class Filter implements PropertyFilter {

    private final ArrayList<String> includeFields;

    public Filter(ArrayList<String> includeFields) {
        this.includeFields = includeFields;
    }

    @Override
    public boolean apply(Object object, String name, Object value) {
        return this.includeFields.contains(name);
    }
}
