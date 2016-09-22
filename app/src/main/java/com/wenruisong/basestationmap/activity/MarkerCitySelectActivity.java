package com.wenruisong.basestationmap.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.database.ImportedCellsSqliteHelper;
import com.wenruisong.basestationmap.model.ImportedCity;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;

public class MarkerCitySelectActivity extends AppCompatActivity {
  private ListView cityList;
    ArrayList<ImportedCity> mImportedCities;
    CityAdapter cityAdapter;
    private ImportedCellsSqliteHelper mImportedCellsSqliteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_city_select);
        cityList = (ListView)findViewById(R.id.city_list);
        mImportedCellsSqliteHelper = new ImportedCellsSqliteHelper(this);
        mImportedCities = mImportedCellsSqliteHelper.queryImportedCitys();
        cityAdapter = new CityAdapter(this, mImportedCities);
        cityList.setAdapter(cityAdapter);
    }

    class CityAdapter extends BaseAdapter {
        ArrayList<ImportedCity> mList;
        private Context mContext;

        public CityAdapter(Context context, ArrayList<ImportedCity> cells) {
            mContext = context;
            mList = cells;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList == null ? 0 : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_marker_city, null);
            holder = new ViewHolder();
            holder.city = (TextView) view.findViewById(R.id.tv_city);
            holder.state = (TextView) view.findViewById(R.id.tv_state);

            final ImportedCity importedCity = mList.get(position);
            holder.city.setText(importedCity.city);
            if(importedCity.city.equals(BasestationManager.getCurrentShowCity())){
                holder.state.setText("已选择");
                holder.state.setTextColor(ResourcesUtil.getColor(R.color.gray));
            } else {
                holder.state.setText("选择");
                holder.state.setTextColor(ResourcesUtil.getColor(R.color.text_color_highlight));
                holder.state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BasestationManager.setCurrentShowCity(importedCity.city);
                        BasestationManager.getInstance().initCity();
                        notifyDataSetChanged();
                    }
                });
            }


            return view;
        }

        class ViewHolder {
            TextView city;
            TextView state;
        }
    }
}
