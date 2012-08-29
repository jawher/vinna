package vinna;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Model extends HashMap<String, Object> {
    public Model add(String k, Object v) {
        put(k, v);
        return this;
    }

    public Model merge(Map<String, Object> m) {
        putAll(m);
        return this;
    }

    public Model addReqParams() {
        Map<String, Collection<String>> params = VinnaContext.get().request.getParameters();
        for (Map.Entry<String, Collection<String>> entry : params.entrySet()) {
            if(entry.getValue().size()==1) {
                put(entry.getKey(), entry.getValue().iterator().next());
            } else {
                put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
}
