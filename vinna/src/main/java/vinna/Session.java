package vinna;

import vinna.exception.VuntimeException;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Session implements Serializable {
    public boolean exists() {
        return VinnaContext.get().request.getSession(false) != null;
    }

    public String getId() {
        HttpSession s = VinnaContext.get().request.getSession(false);
        if (s != null) {
            return s.getId();
        } else {
            throw new VuntimeException("No session found");
        }
    }

    public void delete() {
        HttpSession s = VinnaContext.get().request.getSession(false);
        if (s != null) {
            s.invalidate();
        }
    }

    public void create() {
        VinnaContext.get().request.getSession(true);
    }

    public void renew() {
        if (!exists()) {
            throw new VuntimeException("Trying to renew an non-existing session");
        } else {
            Map<String, Object> backup = new HashMap<>();
            HttpSession s = VinnaContext.get().request.getSession(false);
            Enumeration names = s.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                backup.put(name, s.getAttribute(name));
            }
            s.invalidate();
            s = VinnaContext.get().request.getSession(true);

            //think: do we need to filter out vinna.session item ?
            for (Map.Entry<String, Object> entry : backup.entrySet()) {
                s.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    public Serializable get(String id) {
        HttpSession s = VinnaContext.get().request.getSession(false);
        if (s != null) {
            return (Serializable) s.getAttribute(id);
        } else {
            return null;
        }
    }

    public void put(String id, Serializable value) {
        HttpSession s = VinnaContext.get().request.getSession(false);
        if (s != null) {
            s.setAttribute(id, value);
        } else {
            throw new VuntimeException("No session found. You should call create() before put()");
        }
    }
}
