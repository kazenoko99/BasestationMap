package com.wenruisong.basestationmap.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.CsvFileAdapter;
import com.wenruisong.basestationmap.adapter.ImportedCellAdapter;
import com.wenruisong.basestationmap.common.PathCursorLoader;
import com.wenruisong.basestationmap.eventbus.FileExplorerEvents;

import java.io.File;

/**
 * Created by wen on 2016/1/24.
 */
public class BasestationImportFragment extends BackPressHandledFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView importedcellListView;
    private ImportedCellAdapter importedCellAdapter;
    private CsvFileAdapter csvFileAdapter;
    private TextView getImportCSV;
    private TextView startImport;
    private static final String ARG_PATH = "path";
    private static final String INDEX = "index";
    private String mPath;
    private int mIndex;
    public static BasestationImportFragment newInstance(String path,int index) {
        BasestationImportFragment f = new BasestationImportFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putInt(INDEX, index);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_basestation_import, container, false);
        importedcellListView = (ListView) viewGroup.findViewById(R.id.cell_list);
        getImportCSV = (TextView) viewGroup.findViewById(R.id.importbtn);
        startImport = (TextView) viewGroup.findViewById(R.id.start_import);
        Activity activity = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPath = bundle.getString(ARG_PATH);
            mPath = new File(mPath).getAbsolutePath();
            mIndex = bundle.getInt(INDEX);
            getImportCSV.setText(mPath);
        }

        csvFileAdapter = new CsvFileAdapter(activity);
        importedcellListView.setAdapter(csvFileAdapter);
        importedcellListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                String path = csvFileAdapter.getFilePath(position);
                if (TextUtils.isEmpty(path))
                    return;
                FileExplorerEvents.getBus().post(new FileExplorerEvents.OnClickFile(path,mIndex));
            }
        });
        getLoaderManager().initLoader(1, null, this);
        return viewGroup;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (TextUtils.isEmpty(mPath))
            return null;
        return new PathCursorLoader(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        csvFileAdapter.swapCursor(data);
        csvFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
