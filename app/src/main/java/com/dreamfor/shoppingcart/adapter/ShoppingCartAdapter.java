package com.dreamfor.shoppingcart.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dreamfor.shoppingcart.R;
import com.dreamfor.shoppingcart.domain.ProductItem;
import com.dreamfor.shoppingcart.service.ProductService;

import java.util.List;
import java.util.Locale;

public class ShoppingCartAdapter extends BaseAdapter {
    private List<ProductItem> productItemList;
    private LayoutInflater layoutInflater;
    private ProductService productService;

    public ShoppingCartAdapter(Context context, List<ProductItem> productItemList, ProductService productService) {
        this.productItemList = productItemList;
        this.layoutInflater = LayoutInflater.from(context);
        this.productService = productService;
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
            convertView = layoutInflater.inflate(R.layout.shoppingcart_item_product, parent, false);
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

        ProductItem productItem = productItemList.get(position);

        holder.nameTv.setText(productItem.getProduct_name());
        holder.textTv.setText(productItem.getProduct_text());
        holder.priceTv.setText(String.format(Locale.getDefault(), "%.2f", productItem.getAllPrice()));
        holder.quantityEt.setText(String.valueOf(productItem.getQuantity()));

        holder.quantityEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = holder.quantityEt.getText().toString();

                // 检查输入是否为有效数字
                if (!TextUtils.isEmpty(input)) {
                    try {
                        int quantity = Integer.parseInt(input);
                        productItem.setQuantity(quantity);
                        productItem.setAllPrice(productItem.getPrice() * productItem.getQuantity());

                        holder.priceTv.setText(String.format(Locale.getDefault(), "%.2f", productItem.getAllPrice()));

                        if(productItem.getQuantity() == 0){
                            productItemList.remove(productItem);
                        }
                        productService.setProductAndSyncUser(productItem.getUser_Id(), productItem.getProduct_id(), productItem.getQuantity());
                        notifyDataSetChanged();
                    } catch (NumberFormatException e) {
                        holder.quantityEt.setText("0");
                        productItem.setQuantity(0);
                        productItem.setAllPrice(productItem.getPrice() * productItem.getQuantity());
                        holder.priceTv.setText(String.format(Locale.getDefault(), "%.2f", productItem.getAllPrice()));

                        if(productItem.getQuantity() == 0){
                            productItemList.remove(productItem);
                        }
                        productService.setProductAndSyncUser(productItem.getUser_Id(), productItem.getProduct_id(), productItem.getQuantity());
                        notifyDataSetChanged();
                    }
                }
                return true;
            }
        });

        holder.addIb.setOnClickListener(v -> {
            // 计算数量
            int quantity = productItem.getQuantity();
            productItem.setQuantity(quantity + 1);
            // 总金额
            productItem.setAllPrice(productItem.getAllPrice() + productItem.getPrice());

            // 删除数据库记录
            productService.setProductAndSyncUser(productItem.getUser_Id(), productItem.getProduct_id(), productItem.getQuantity());
            notifyDataSetChanged();
        });

        holder.subIb.setOnClickListener(v -> {
            // 计算数量
            int quantity = productItem.getQuantity();
            // 计算总金额
            if (quantity > 1) {
                productItem.setQuantity(quantity - 1);
                productItem.setAllPrice(productItem.getAllPrice() - productItem.getPrice());
            } else{
                productItem.setQuantity(0);
                productItem.setAllPrice(productItem.getAllPrice() - productItem.getPrice());
                productItemList.remove(productItem);
            }

            // 删除数据库记录
            productService.setProductAndSyncUser(productItem.getUser_Id(), productItem.getProduct_id(), productItem.getQuantity());
            notifyDataSetChanged();
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

