package net.backtothefront;


import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

// courtesy of: http://backtothefront.net/2011/storing-sets-keyvalue-pairs-single-db-column-hibernate-postgresql-hstore-type/
public class HstoreHelper {
    
    private static final String K_V_SEPARATOR = "=>";
    private static final String ATTR_SEPARATOR = ", ";
    private static final String COMMA_ESCAPE = "&com;";
    
    public static String toString(Map<String, String> m) {
        if (m.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int n = m.size();
        for (String key : m.keySet()) {
            //replace the ATTR_SEPARATOR with a COMMA_ESCAPE and also escape any special chars with a \ so that sql with be happy
            sb.append("\"").append(key).append("\"").append(K_V_SEPARATOR).append("\"").append(m.get(key).replaceAll(ATTR_SEPARATOR, COMMA_ESCAPE).replaceAll("(?=[],\\[+&|!(){}^\"~*?:\\\\-])", "\\\\")).append("\"");
            if (n > 1) {
                sb.append(ATTR_SEPARATOR);
                n--;
            }
        }
        return sb.toString();
    }
    
    public static Map<String, String> toMap(String s) {
        Map<String, String> m = new HashMap<>();
        if (! StringUtils.hasText(s)) {
            return m;
        }
        String[] tokens = s.split(ATTR_SEPARATOR);
        for (String token : tokens) {
            String[] kv = token.split(K_V_SEPARATOR);
            String k = kv[0];
            String v = kv[1].replace(COMMA_ESCAPE, ATTR_SEPARATOR).trim();
            
            if(k.length() == 0){
                break;
            }
            
            k = k.trim().substring(1, k.length() - 1);
            if(!v.toLowerCase().equals("null") && v.length() > 1){
                v = v.substring(1, v.length() - 1);
            }else{
                v = null;
            }
            m.put(k, v);
        }
        return m;
    }
}