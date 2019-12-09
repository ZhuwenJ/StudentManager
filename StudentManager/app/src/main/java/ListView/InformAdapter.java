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

import org.w3c.dom.Text;

import java.util.ArrayList;

import model.Inform;

public class InformAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Inform> list;
    //InformAdapter的构造函数，一个Context类型的参数，也就是哪一个Activity
    //这里传进去的是 ListViewActivity,ListView所在的Activity

    public InformAdapter(Context context,ArrayList<Inform> list){
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount(){
        return  list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //编写一个类，用于转移layout_list_item的控件
    static  class ViewHolder{

        public ImageView imageView;
        public TextView depuid,informerid; //depuid是发送者,informerid是通知内容
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //实例化ViewHolder
        ViewHolder holder = null;
        //如果视图为空
        if(convertView == null){
            //此处需要导入包，填写ListView的图标和标题等控件的来源，来自于inform_item这个布局文件
            convertView = mLayoutInflater.inflate(R.layout.inform_item,null);
            //生成ViewHolder
            holder = new ViewHolder();
            //转移item布局的对象,以便修改和赋值
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.depuid = (TextView) convertView.findViewById(R.id.name);
            holder.informerid = (TextView) convertView.findViewById(R.id.inform_content);

            //传递holder
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //给控件赋值

        if(list.size()<=position)
            return  convertView;
        holder.depuid.setText(list.get(position).getDepuid());
        holder.informerid.setText(list.get(position).getInformerid());
        Glide.with(mContext).load(R.mipmap.notice).into(holder.imageView);
        return convertView;
    }
}
