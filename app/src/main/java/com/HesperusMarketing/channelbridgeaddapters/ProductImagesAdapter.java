package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HesperusMarketing.Entity.Product;
import com.HesperusMarketing.Entity.TempInvoiceStock;
import com.HesperusMarketing.channelbridge.InvoiceGen1Alternate;
import com.HesperusMarketing.channelbridge.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;

/**
 * Created by Himanshu on 3/28/2016.
 */
public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder> {


    private Context mContext;
    private ArrayList<TempInvoiceStock> albumList;


    public ProductImagesAdapter(Context mContext, ArrayList<TempInvoiceStock> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title;
        public RelativeLayout layoutBack, layoutSelectColor;

        public MyViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.image_gallery);
            title = (TextView) view.findViewById(R.id.text_gallery_title);
            layoutBack = (RelativeLayout) view.findViewById(R.id.layout_backgroun);
            layoutSelectColor = (RelativeLayout) view.findViewById(R.id.layout_select_color);
        }


    }


    @Override
    public ProductImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dialog_product_image, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProductImagesAdapter.MyViewHolder holder, final int position) {

        holder.title.setText(albumList.get(position).getProductCode());


        if ((Integer.parseInt(albumList.get(position).getNormalQuantity()) > 0) && (Integer.parseInt(albumList.get(position).getFreeQuantity()) > 0)) {
            holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myGreen));
        } else {
            if (Integer.parseInt(albumList.get(position).getNormalQuantity()) > 0) {
                holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myBlue));
            } else {
                if (Integer.parseInt(albumList.get(position).getFreeQuantity()) > 0) {
                    holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myRed));
                } else {

                }
            }
        }


       /* if((Integer.parseInt(albumList.get(position).getNormalQuantity()) > 0)&&(Integer.parseInt(albumList.get(position).getFreeQuantity()) > 0)){
            holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myGreen));
        }else if(Integer.parseInt(albumList.get(position).getNormalQuantity()) > 0) {
            holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myBlue));
        }else if(Integer.parseInt(albumList.get(position).getFreeQuantity()) > 0){
            holder.layoutSelectColor.setBackgroundColor(mContext.getResources().getColor(R.color.myRed));

        }*/


        //   holder.layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.myBlue));

        try {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(albumList.get(position).getProImage());
            holder.img.setImageBitmap(bitmap);
        } catch (OutOfMemoryError a) {

        }



      /*  System.out.println("ooooooo :"+albumList.get(position).getNormalQuantity());
        if (Integer.parseInt(albumList.get(position).getNormalQuantity()) > 0) {
            holder.layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.myBlue));

        } else {

        }*/

        holder.layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InvoiceGen1Alternate) mContext).lodeSelectCode(albumList.get(position).getProductCode(), albumList.get(position).getBatchCode(),
                        String.valueOf(albumList.get(position).getShelfQuantity()), String.valueOf(albumList.get(position).getRequestQuantity()),
                        String.valueOf(albumList.get(position).getNormalQuantity()), String.valueOf(albumList.get(position).getFreeQuantity()),
                        String.valueOf(albumList.get(position).getStock()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
