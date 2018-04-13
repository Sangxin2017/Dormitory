package com.jxaummd.light.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jxaummd.light.MyApplication;
import com.jxaummd.light.R;
import com.jxaummd.light.base.net.BaseNetGetRequest;
import com.jxaummd.light.hardware.BleOperator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sangx on 2018/2/23.
 */

public class MySelfAdapter extends RecyclerView.Adapter<MySelfAdapter.MyViewHolder> {
    private List<SetItem> list=new ArrayList<>();
    private  BaseNetGetRequest request= new BaseNetGetRequest();

    public  void addItem(int image,String title){
        list.add(new SetItem(image,title));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myself_set_list,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
                holder.title.setText(list.get(position).getTitle());
                holder.image.setImageResource(list.get(position).getImage_id());
                if (position==3){
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        request.ResquestUrlResone("http://jxaummd.com/lightnew/control?op=tq&d=1889", new BaseNetGetRequest.RequestresultCallback() {
                                    @Override
                                    public void success(String result) throws JSONException {
                                        EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_SENDDATA,result));
                                        Log.d("time",result);
                                    }

                                    @Override
                                    public void fail() {

                                    }
                                }
                        );
                    }
                     });
                MyApplication.MyToast("天气已经更新！");
                }

                if (position==0){
                    holder.fuzhuline.setVisibility(View.GONE);
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         //   request.ResquestUrlResone("");
                        }
                    });



                }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        TextView fuzhuline;
        View  view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            title=itemView.findViewById(R.id.set_item_text);
            image=itemView.findViewById(R.id.set_item_image);
            fuzhuline=itemView.findViewById(R.id.fuzhu_line);
        }
    }



    public class  SetItem {
        private int image_id;
        private String title;

        public SetItem(int image_id, String title) {
            this.image_id = image_id;
            this.title = title;
        }


        public int getImage_id() {
            return image_id;
        }

        public void setImage_id(int image_id) {
            this.image_id = image_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}

