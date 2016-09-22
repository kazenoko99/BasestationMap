package com.wenruisong.basestationmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.BasestationImportActivity;
import com.wenruisong.basestationmap.activity.ImportedCellDetailActivity;
import com.wenruisong.basestationmap.database.ImportedCellsSqliteHelper;
import com.wenruisong.basestationmap.model.ImportedCity;
import com.wenruisong.basestationmap.utils.DisplayUtil;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.view.SlidingButtonView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/1/23.
 */
public class BasestationSettingFragment extends BackPressHandledFragment {
    private RecyclerView importedcellListView;
     private ImportedCityAdapter mImportedCityAdapter;
    private ImportedCellsSqliteHelper mImportedCellsSqliteHelper;
    private ArrayList<ImportedCity> mImportedCities;
    private FloatingActionButton mFloatingActionButton;
    public static BasestationSettingFragment getInstance(Bundle bundle) {
        BasestationSettingFragment backPressHandledFragment = new BasestationSettingFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }



    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logs.d("OfflineMapModule", "OfflineMapFragment onCreateView");
        View root = initView(inflater);
        return root;
    }

    private View initView(LayoutInflater inflater) {
        Logs.d("OfflineMapModule", "OfflineMapFragment initView");
        View root = inflater.inflate(R.layout.fragment_basestation_setting, null);
        View drawerIcon = root.findViewById(R.id.drawer_icon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openOrCloseDrawers();
            }
        });
        mFloatingActionButton = (FloatingActionButton) root.findViewById(R.id.btnFloatingAction);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BasestationImportActivity.class);
                startActivity(intent);
            }
        });
        mImportedCellsSqliteHelper = new ImportedCellsSqliteHelper(getContext());
        mImportedCities = mImportedCellsSqliteHelper.queryImportedCells();
        importedcellListView = (RecyclerView) root.findViewById(R.id.cell_list);
        importedcellListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mImportedCityAdapter = new ImportedCityAdapter(getContext(),mImportedCities);
        importedcellListView.setAdapter(mImportedCityAdapter);
          return root;
    }


    public class ImportedCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements SlidingButtonView.IonSlidingButtonListener  {

        protected Context mContext;
        protected List<ImportedCity> mList;
        protected LayoutInflater mInflater;    // 视图容器
        private SlidingButtonView mMenu = null;

        public ImportedCityAdapter(Context context, List<ImportedCity> importedCities) {
            mContext = context;
            mList = importedCities;
            mInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



            ViewHolder holder;
            View  root = mInflater.inflate(R.layout.item_imported_city, parent, false);
            holder = new ViewHolder(root);
            holder.delete = (TextView) root.findViewById(R.id.tv_delete);
            holder.reimport = (TextView) root.findViewById(R.id.tv_edit);
            holder.city = (TextView) root.findViewById(R.id.tv_imported_city);
            holder.nettype = (TextView) root.findViewById(R.id.tv_imported_nettype);
            holder.count = (TextView) root.findViewById(R.id.tv_imported_count);
         //   holder.devideLine =  root.findViewById(R.id.devide_line);
            holder.layout_content =  (ViewGroup) root.findViewById(R.id.layout_content);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.layout_content.getLayoutParams().width = DisplayUtil.getScreenWidth(mContext);

            ImportedCity importedCell = mList.get(position);
            viewHolder.city.setText(importedCell.city);
            viewHolder.nettype.setText(importedCell.netType);
            viewHolder.count.setText("" + importedCell.cellCount);
            viewHolder.layout_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    closeMenu();
                    viewHolder.slidingButtonView.openMenu();
                    return true;
                }
            });

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            viewHolder.reimport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            viewHolder.layout_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(menuIsOpen()){
                        closeMenu();
                    } else {
                        Intent intent = new Intent(mContext, ImportedCellDetailActivity.class);
                        intent.putExtra("CITY", mList.get(position).city);
                        intent.putExtra("TYPE", mList.get(position).netType);
                        intent.putExtra("COUNT", mList.get(position).cellCount);
                        mContext.startActivity(intent);
                    }
                }
            });
//            if(position ==getItemCount()-1){
//                viewHolder.devideLine.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onMenuIsOpen(View view) {
            mMenu = (SlidingButtonView) view;
        }


        @Override
        public void onDownOrMove(SlidingButtonView slidingButtonView) {
            if(menuIsOpen()){
                if(mMenu != slidingButtonView){
                    closeMenu();
                }
            }
        }


        public void closeMenu() {
            if(mMenu != null)
                mMenu.closeMenu();
            mMenu = null;

        }

        public Boolean menuIsOpen() {
            if(mMenu != null){
                return true;
            }
            return false;
        }


        //定义ViewHolder，包括两个控件
        private class ViewHolder extends RecyclerView.ViewHolder {

            public TextView city;
            public TextView nettype;
            public TextView count;
            public TextView delete;
            public TextView reimport;
            public ViewGroup layout_content;
            public SlidingButtonView slidingButtonView;
            public ViewHolder(final View root) {
                super(root);
                ((SlidingButtonView) root).setSlidingButtonListener(ImportedCityAdapter.this);
                slidingButtonView =  ((SlidingButtonView) root);

            }
        }

        @Override
        public int getItemCount() {
            if ((mList == null) || (mList.size() == 0)) {
                return 0;
            }

            return (mList.size());
        }

        public void removeData(int position){
            mList.remove(position);
            notifyItemRemoved(position);

        }

    }

}
