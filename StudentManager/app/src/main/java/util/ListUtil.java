package util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.baoyz.swipemenulistview.SwipeMenuItem;

public class ListUtil {
    /**
     * Description:这个是用来讲dp为计量单位改为px
     * @author zwj
     * @Time 2019-12-3 21:15:00
     * @param context:函数的上下文
     * @param dpValue:dp的值
     * @return  int:返回是转化好后的数值
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Description:这个是用来生成左滑菜单里面的元素，只有文字的元素
     * @author zwj
     * @Time 2019-12-3 21:20:00
     * @param context:函数的上下文
     * @param title:对应元素里面的文本
     * @param color:元素的背景色
     * @return  SwipeMenuItem:返回是菜单的元素
     */
    public static SwipeMenuItem SwipeMenuItemTitle(Context context, String title,ColorDrawable color){
        SwipeMenuItem item1 = new SwipeMenuItem(context);
        // set item background
        item1.setBackground(color);
        // set item width
        item1.setWidth(dp2px(context,80));
        // set item title
        item1.setTitle(title);
        // set item title fontsize
        item1.setTitleSize(18);

        // set item title font color
        item1.setTitleColor(Color.WHITE);
        return item1;
    }

    /**
     * Description:这个是用来生成左滑菜单里面的元素，以图片主打的元素
     * @author zwj
     * @Time 2019-12-3 21:21:00
     * @param context:函数的上下文
     * @param id:对应元素的图片的id
     * @param color:元素的背景色
     * @return  SwipeMenuItem:返回是菜单的元素
     */
    public static SwipeMenuItem SwipeMenuItemIcon(Context context, int id,ColorDrawable color){
        SwipeMenuItem Item = new SwipeMenuItem(context);
        // set item background
        Item.setBackground(color);
        // set item width
        Item.setWidth(dp2px(context,80));
        // set a icon
        Item.setIcon(id);
        return Item;
    }
}
