package com.project.lb3labs.ecashier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class ShoppingAdapter extends ArrayAdapter<Shops> {
    private int total;
    private final Context mContext;
    private final List<Shops> shoppingList;

    public ShoppingAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Shops> list) {
        super(context, 0 , list);
        mContext = context;
        shoppingList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.listview_display,parent,false);

        final Shops currentShops = shoppingList.get(position);

        TextView name = listItem.findViewById(R.id.nama);
        name.setText(currentShops.getTitle());

        TextView quantity = listItem.findViewById(R.id.jumlah_dan_harga);
        quantity.setText(currentShops.getQty() + currentShops.getUnit() + " x " + currentShops.getPriceOne());

        TextView price = listItem.findViewById(R.id.harga);
        price.setText(NewMainActivity.currencyFormat(currentShops.getTotalPriceOne()));

        // Set the variable first
        setTotal(total);

        // Passing the variable
        NewMainActivity.getTotal(total);

        // inflate other items here :
        Button deleteButton = listItem.findViewById(R.id.remove);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Passing variable
                        NewMainActivity.getTotal(total + currentShops.getTotalPriceOne());

                        Integer index = (Integer) v.getTag();
                        shoppingList.remove(index.intValue());
                        notifyDataSetChanged();
                    }
                }
        );

        return listItem;
    }

    private void setTotal(int total) {
        this.total = total;
    }

}
