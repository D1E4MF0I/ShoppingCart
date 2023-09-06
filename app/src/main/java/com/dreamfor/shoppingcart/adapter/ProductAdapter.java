package com.dreamfor.shoppingcart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dreamfor.shoppingcart.R;
import com.dreamfor.shoppingcart.domain.ProductItem;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<ProductItem> productItemList;

    public ProductAdapter(Context context, List<ProductItem> productList) {
        this.context = context;
        this.productItemList = productList;
    }

    @Override
    public int getCount() {
        return productItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return productItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.products_item, parent, false);

            holder = new ViewHolder();
            holder.pNameTextView = convertView.findViewById(R.id.pitem_pName);
            holder.pTextView = convertView.findViewById(R.id.pitem_ptext);
            holder.pPriceTextView = convertView.findViewById(R.id.pitem_pprice);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductItem productItem = productItemList.get(position);
        holder.pNameTextView.setText(productItem.getProduct_name());
        holder.pTextView.setText(productItem.getProduct_text());
        holder.pPriceTextView.setText(String.valueOf(productItem.getPrice()));

        return convertView;
    }

    private static class ViewHolder {
        TextView pNameTextView;
        TextView pTextView;
        TextView pPriceTextView;
    }
}
