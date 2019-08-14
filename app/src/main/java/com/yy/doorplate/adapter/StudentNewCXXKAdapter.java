package com.yy.doorplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.doorplate.R;
import com.yy.doorplate.model.StudentInfoModel;

import java.util.List;


public class StudentNewCXXKAdapter extends BaseAdapter{

    private String TAG="ShowAdapter";
    private List<StudentInfoModel> studentInfoModelList;

    private Context mContext;

    public StudentNewCXXKAdapter(Context context, List<StudentInfoModel> studentInfoModelList){
        mContext=context;
        this.studentInfoModelList=studentInfoModelList;

    }

    public void setDate(List<StudentInfoModel> studentInfoModelList){
        this.studentInfoModelList=studentInfoModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return studentInfoModelList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.new_cx_item_attend_student, null);
            vh = new ViewHolder();
            vh.txt_item_attend = (TextView) view.findViewById(R.id.txt_item_attend);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        StudentInfoModel studentInfoModel=studentInfoModelList.get(pos);
        vh.txt_item_attend.setText(studentInfoModel.xm);

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return studentInfoModelList.size() ;
    }


    class ViewHolder {
        TextView txt_item_attend;
    }

}
