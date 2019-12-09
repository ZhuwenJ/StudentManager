package ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zwj.studentmanager.R;

import java.util.ArrayList;

import model.Department;

public class MyDepAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Department> list;
    //MyDepAdapter的构造函数，一个Context类型的参数，也就是哪一个Activity
    //这里传进去的是 ListViewActivity,ListView所在的Activity
    public MyDepAdapter(Context context, ArrayList<Department> list){
        this.mContext=context;
        mLayoutInflater =LayoutInflater.from(context);
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //写一个静态的class,把layout_list_item的控件转移过来使用
    static class ViewHolder{
        //声明引用
        public ImageView imageView;
        public TextView departmentname,introduction;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //实例化ViewHolder
        ViewHolder holder = null;
        //如果视图为空
        if (convertView == null){
            //此处需要导入包，填写ListView的图标和标题等控件的来源，来自于layout_list_item这个布局文件
            convertView = mLayoutInflater.inflate(R.layout.department_item,null);
            //生成一个ViewHolder的对象
            holder = new ViewHolder();
            //把layout_list_item对象转移过来，以便修改和赋值
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.departmentname= (TextView) convertView.findViewById(R.id.name);
            holder.introduction = (TextView) convertView.findViewById(R.id.introduction);
            //把这个holder传递进去
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //给控件赋值
        if(list.size()<=position)
            return convertView;
        holder.departmentname.setText(list.get(position).getDepartmentname());
        holder.introduction.setText(list.get(position).getIntroduction());
        Glide.with(mContext).load(R.mipmap.department).into(holder.imageView);
        return convertView;
    }
}
