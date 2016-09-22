package com.wenruisong.basestationmap.offlinemap;

/**
 * Created by yinjianhua on 15-6-12.
 */
public interface OfflineMapDownloadView {
    public void updateList();

    public void dismissRecommendView();

    public void showDialog(final String tip, final boolean isCancelable, int delayTime);

    public void hideDialog();

    public void doWorkBackground(AynscWorkListener listener);
}
