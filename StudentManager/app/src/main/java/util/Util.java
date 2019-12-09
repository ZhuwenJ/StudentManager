package util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Util {
    public static String TimestampToString(Timestamp timestamp){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
    }
}
