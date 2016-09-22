package com.wenruisong.basestationmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;

import java.util.ArrayList;

public class ImportedCellDetailActivity extends AppCompatActivity {
    private ListView cellList;
    private TextView title;
    private String mCity;
    private String mNetType;
    private int mCount;
    private CellAdapter mCellAdapter;
    private ArrayList<Cell> mCells;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCity = getIntent().getStringExtra("CITY");
        mNetType = getIntent().getStringExtra("TYPE");
        mCount  = getIntent().getIntExtra("COUNT",0);
        setContentView(R.layout.activity_imported_cell_detail);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cellList = (ListView) findViewById(R.id.cell_list);
        title = (TextView)findViewById(R.id.title);
        title.setText(mCity+mNetType+"小区列表（"+ mCount +"）");

        mCellAdapter = new CellAdapter(this,mCells);
        cellList.setAdapter(mCellAdapter);
        cellList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ImportedCellDetailActivity.this,CellDetailActivity.class);
                intent.putExtra("CELL",mCells.get(position));
                startActivity(intent);
            }
        });
        LoadCellsTask loadCellsTask = new LoadCellsTask();
        loadCellsTask.execute();
    }

    class LoadCellsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            if(mNetType.equals("GSM")) {
                mCells = BasestationManager.searchGSMCellsByCity(mCity);
            } else if (mNetType.equals("LTE")){
                mCells = BasestationManager.searchLTECellsByCity(mCity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCellAdapter.setDate(mCells);
        }
    }
    class CellAdapter extends BaseAdapter {
        ArrayList<Cell> mList;
        private Context mContext;

        public CellAdapter(Context context, ArrayList<Cell> cells) {
            mContext = context;
            mList = cells;
        }

       public void setDate(ArrayList<Cell> cells){
           mList = cells;
           notifyDataSetChanged();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_cell, null);
                holder = new ViewHolder();
                holder.index = (TextView) convertView.findViewById(R.id.tv_index);
                holder.cellName = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }

            Cell cell = mList.get(position);
            holder.index.setText(""+cell.index);
            holder.cellName.setText(cell.cellName);

            return convertView;
        }

        class ViewHolder {
            TextView index;
            TextView cellName;
        }
    }
}
