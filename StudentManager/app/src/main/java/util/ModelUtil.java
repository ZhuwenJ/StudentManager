package util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ModelUtil {
    /**
     * Description:这个是用来讲对象转化为map,便于post所用的表单信息
     * @author zwj
     * @Time 2019-12-3 21:10:00
     * @param obj:待处理的object对象
     * @return  Map<String, String>:返回是对象所有域的HashMap，每个属性和对应String值存在里面
     */
    public static Map<String, String> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }

        Map<String, String> map = new HashMap<String, String>();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                //如果所获得的属性值Class不是string，转化为String
                if(!field.get(obj).getClass().equals("s".getClass()))
                    map.put(field.getName(), field.get(obj).toString());
                else
                    map.put(field.getName(), field.get(obj).toString());
            }
        } catch (Exception e) {
        }
        return map;
    }
}
