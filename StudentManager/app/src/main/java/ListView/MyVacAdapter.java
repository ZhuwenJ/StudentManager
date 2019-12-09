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
import model.Vacation;

public class MyVacAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Vacation> list;
    //MyDepAdapter的构造函数，一个Context类型的参数，也就是哪一个Activity
    //这里传进去的是 ListViewActivity,ListView所在的Activity
    public MyVacAdapter(Context context, ArrayList<Vacation> list){
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
        if(list.size()<=position)
            return converView;
        String id="审批号: "+list.get(position).getJudgeid();
        String state;
        if(list.get(position).getIsjudge()==null)
            state="未审批";
        else if(list.get(position).getIsjudge().equals("null"))
            state="还没审批";
        else
            state=list.get(position).getIsjudge();
        holder.vacateperson.setText(id);
        holder.introduction.setText(state);
        Glide.with(mContext).load(R.drawable.ic_leave).into(holder.imageView);
        return converView;
    }
}
