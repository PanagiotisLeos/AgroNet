package com.example.agronet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FarmInfoView extends LinearLayout {
    private TextView farmName;
    private TextView farmLocation;
    private TextView farmType;
    private TextView farmProducts;

    public FarmInfoView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FarmInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FarmInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate the layout for this view
        LayoutInflater.from(context).inflate(R.layout.farm_info_view, this, true);
        farmName = findViewById(R.id.farm_name);
        farmLocation = findViewById(R.id.farm_location);
        farmType = findViewById(R.id.farm_type);
        farmProducts = findViewById(R.id.farm_products);
    }

    public void setFarmInfo(String name, String location, String type, String products) {
        farmName.setText(name);
        farmLocation.setText(location);
        farmType.setText(type);
        farmProducts.setText(products);
    }

}
