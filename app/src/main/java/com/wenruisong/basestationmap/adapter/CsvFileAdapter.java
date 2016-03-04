package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.PathCursor;

/**
 * Created by wen on 2016/1/24.
 */

public class CsvFileAdapter extends SimpleCursorAdapter {
    final class ViewHolder {
        public ImageView iconImageView;
        public TextView nameTextView;
    }

    public CsvFileAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, null,
                new String[]{PathCursor.CN_FILE_NAME, PathCursor.CN_FILE_PATH},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.fragment_file_list_item, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) view.findViewById(R.id.icon);
            viewHolder.nameTextView = (TextView) view.findViewById(R.id.name);
        }

        if (isDirectory(position)) {
            viewHolder.iconImageView.setImageResource(R.drawable.ic_theme_folder);
        } else if (isCSV(position)) {
            viewHolder.iconImageView.setImageResource(R.drawable.ic_theme_description);
        }
        viewHolder.nameTextView.setText(getFileName(position));

        return view;
    }

    @Override
    public long getItemId(int position) {
        final Cursor cursor = moveToPosition(position);
        if (cursor == null)
            return 0;

        return cursor.getLong(PathCursor.CI_ID);
    }

    Cursor moveToPosition(int position) {
        final Cursor cursor = getCursor();
        if (cursor.getCount() == 0 || position >= cursor.getCount()) {
            return null;
        }
        cursor.moveToPosition(position);
        return cursor;
    }

    public boolean isDirectory(int position) {
        final Cursor cursor = moveToPosition(position);
        if (cursor == null)
            return true;

        return cursor.getInt(PathCursor.CI_IS_DIRECTORY) != 0;
    }

    public boolean isCSV(int position) {
        final Cursor cursor = moveToPosition(position);
        if (cursor == null)
            return true;

        return cursor.getInt(PathCursor.CI_IS_CSV) != 0;
    }

    public String getFileName(int position) {
        final Cursor cursor = moveToPosition(position);
        if (cursor == null)
            return "";

        return cursor.getString(PathCursor.CI_FILE_NAME);
    }

    public String getFilePath(int position) {
        final Cursor cursor = moveToPosition(position);
        if (cursor == null)
            return "";

        return cursor.getString(PathCursor.CI_FILE_PATH);
    }
}