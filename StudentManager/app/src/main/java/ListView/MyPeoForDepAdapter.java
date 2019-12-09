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

import model.Department;
import model.PepForDep;
import model.Vacation;

public class MyPeoForDepAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<PepForDep> list;
    //MyDepAdapter的构造函数，一个Context类型的参数，也就是哪一个Activity
    //这里传进去的是 ListViewActivity,ListView所在的Activity
    public MyPeoForDepAdapter(Context context, ArrayList<PepForDep> list){
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
    public long getItemId(int i) {
        return 0;
    }

    //写一个静态的class,把layout_list_item的控件转移过来使用
    static class ViewHolder{
        //声明引用
        public ImageView imageView;
        public TextView vacateperson,introduction;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        //实例化ViewHolder
        ViewHolder holder = null;

        if(converView == null){

            converView = mLayoutInflater.inflate(R.layout.vacate_item,null);
            //生成一个ViewHolder的对象
            holder = new ViewHolder();
            //把layout_list_item对象转移过来
            holder.imageView = (ImageView) converView.findViewById(R.id.image);
            holder.vacateperson = (TextView) converView.findViewById(R.id.judgeid);
            holder.introduction = (TextView) converView.findViewById(R.id.state);
            //传递holder
            converView.setTag(holder);
        }else{
            holder = (ViewHolder) converView.getTag();
        }
        //给控件赋值
        holder.vacateperson.setText(list.get(position).getApplyuser());
        holder.introduction.setText(list.get(position).getApplyreson());
//        Glide.with(mContext).load().into(holder.imageView);
        return converView;
    }
}
