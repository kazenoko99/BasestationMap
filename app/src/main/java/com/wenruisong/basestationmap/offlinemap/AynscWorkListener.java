package com.wenruisong.basestationmap.offlinemap;

/**
 * 非主线程调用，关心结果, #onPostResult有结果返回的主线程执行，#onPostExcuted无结果返回的主线程执行
 * Created by wuxuexiang on 15-5-26.
 */
public interface AynscWorkListener {
    public void onPreExcuted();
    public Object doInBackground();
    public void onPostResult(Object obj);
    public void onPostExcuted();
}
