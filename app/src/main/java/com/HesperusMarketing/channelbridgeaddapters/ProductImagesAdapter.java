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
public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder>  {


    private Context mContext;
    private ArrayList<TempInvoiceStock> albumList;



    public ProductImagesAdapter(Context mContext, ArrayList<TempInvoiceStock> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title;
        public LinearLayout layoutBack;

        public MyViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.image_gallery);
            title = (TextView) view.findViewById(R.id.text_gallery_title);
            layoutBack = (LinearLayout) view.findViewById(R.id.layout_backgroun);

        }


    }


    @Override
    public ProductImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dialog_product_image, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProductImagesAdapter.MyViewHolder holder, int position) {

        holder.title.setText(albumList.get(position).getProductCode());

        try {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(albumList.get(position).getProImage());
            holder.img.setImageBitmap(bitmap);
        } catch (OutOfMemoryError a) {

        }

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
