package com.dreamfor.shoppingcart.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dreamfor.shoppingcart.R;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.service.ProductService;

import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    private List<ProductQuantity> productQuantityList;
    private LayoutInflater layoutInflater;

    private ProductService productService;

    public ListViewAdapter(Context context, List<ProductQuantity> productQuantityList, ProductService productService) {
        this.productQuantityList = productQuantityList;
        this.layoutInflater = LayoutInflater.from(context);
        this.productService = productService;
    }

    @Override
    public int getCount() {
        return productQuantityList.size();
    }

    @Override
    public Object getItem(int position) {
        return productQuantityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.nameTv = convertView.findViewById(R.id.item_name_tv);
            holder.textTv = convertView.findViewById(R.id.item_product_text_tv);
            holder.priceTv = convertView.findViewById(R.id.item_product_price_tv);
            holder.quantityEt = convertView.findViewById(R.id.item_quantity_et);
            holder.addIb = convertView.findViewById(R.id.item_add_ib);
            holder.subIb = convertView.findViewById(R.id.item_sub_ib);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // TODO:完善Adapter问题
        ProductQuantity productQuantity = productQuantityList.get(position);

        Product product = productService.getProduct(productQuantity.getProductId());

        holder.nameTv.setText(product.getProduct_name());
        holder.textTv.setText(product.getProduct_text());
        holder.priceTv.setText(String.format(Locale.getDefault(), "%.2f", product.getPrice()));
        holder.quantityEt.setText(String.valueOf(productQuantity.getQuantity()));

        holder.addIb.setOnClickListener(v -> {
            int quantity = productQuantity.getQuantity();
            productQuantity.setQuantity(quantity + 1);
            notifyDataSetChanged();
        });

        holder.subIb.setOnClickListener(v -> {
            int quantity = productQuantity.getQuantity();
            if (quantity > 1) {
                productQuantity.setQuantity(quantity - 1);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder{
        TextView nameTv;
        TextView textTv;
        TextView priceTv;
        EditText quantityEt;
        ImageButton addIb;
        ImageButton subIb;
    }
}

