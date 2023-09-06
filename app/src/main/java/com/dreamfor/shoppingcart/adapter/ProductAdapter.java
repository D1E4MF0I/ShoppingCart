package com.dreamfor.shoppingcart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dreamfor.shoppingcart.ProductInfoActivity;
import com.dreamfor.shoppingcart.R;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductItem;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {
    private List<Product> productList;
    private LayoutInflater inflater;


    public ProductAdapter(Context context, List<Product> productList) {
        this.inflater = LayoutInflater.from(context);
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.products_item, parent, false);

            holder = new ViewHolder();
            holder.pNameTextView = convertView.findViewById(R.id.pitem_pName);
            holder.pTextView = convertView.findViewById(R.id.pitem_ptext);
            holder.pPriceTextView = convertView.findViewById(R.id.pitem_pprice);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        holder.pNameTextView.setText(product.getProduct_name());
        holder.pTextView.setText(product.getProduct_text());
        holder.pPriceTextView.setText(String.format(Locale.getDefault(), "%.2f", product.getPrice()));

        convertView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ProductInfoActivity.class);
            intent.putExtra("product_name", product.getProduct_name());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_text", product.getProduct_text());
            intent.putExtra("product_id", product.getProduct_id());
            context.startActivity(intent);
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView pNameTextView;
        TextView pTextView;
        TextView pPriceTextView;
    }
}
