package com.HesperusMarketing.channelbridgeaddapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.HesperusMarketing.channelbridge.InvoiceGen1Alternate;
import com.HesperusMarketing.channelbridge.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;

/**
 * Created by Himanshu on 3/28/2016.
 */
public class ProductImagesAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;

    ArrayList<ProductImages> item;
    TextView title;
    LinearLayout layoutBack;
    ArrayList<String[]> itemCodeDetailList;
    EditText shelf;
    ImageView coverimage;
    String mypath;


    public ProductImagesAdapter(Context context, ArrayList<String[]> itemcodedetailList, ArrayList<ProductImages> AReworditem) {
        mContext = context;
        item = AReworditem;
        itemCodeDetailList = itemcodedetailList;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int i) {
        return item.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ProductImages product = (ProductImages) getItem(i);
        ProductViewHolder holder = null;

        // if(view==null) {
        holder = new ProductViewHolder();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_dialog_product_image, null);

        holder.img = (ImageView) view.findViewById(R.id.image_gallery);
        holder.title = (TextView) view.findViewById(R.id.text_gallery_title);
        holder.layoutBack = (LinearLayout) view.findViewById(R.id.layout_backgroun);

        view.setTag(holder);
        //  }else {
        //      holder = (ProductViewHolder) view.getTag();
        //  }

        holder.populate(product);

        view.setOnClickListener(new OnItemClickListener(i));
        return view;
    }


    class ProductViewHolder {
        public ImageView img;
        public TextView title;
        public LinearLayout layoutBack;

        void populate(ProductImages p) {
            title.setText(p.itemCode);
            //  new LoadImage(img, p.itemImage).execute();
            try {
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeFile(p.itemImage);
                img.setImageBitmap(bitmap);
            } catch (OutOfMemoryError a) {

            }


            if (Integer.parseInt(p.order) > 0) {
                layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.myBlue));
            } else {

            }

        }

    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;
        private String order;

        public LoadImage(ImageView imv, String path) {
            this.imv = imv;
            this.path = path;

        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(path);


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null && imv != null) {
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            } else {
                imv.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onClick(View view) {

    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            ((InvoiceGen1Alternate) mContext).lodeSelectCode(item.get(mPosition).itemCode, item.get(mPosition).batch, item.get(mPosition).shelf, item.get(mPosition).request, item.get(mPosition).order, item.get(mPosition).free, item.get(mPosition).stock);
           // arg0.setBackgroundColor(mContext.getResources().getColor(R.color.myRed));

        }

    }

}
