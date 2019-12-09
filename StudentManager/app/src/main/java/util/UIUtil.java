package util;

import android.text.SpannableString;

public class UIUtil {
    /**
     * Description:这个是用来讲string变成在EditText中可以设置的Hint
     * @author zwj
     * @Time 2019-12-3 20:50:00
     * @param text:待处理的String
     * @return  SpannableString:返回是处理好的用来设置Hint的String
     */
    public static SpannableString getHint(String text){
        return  new SpannableString(text);//这里输入自己想要的提示文字
    }
}
