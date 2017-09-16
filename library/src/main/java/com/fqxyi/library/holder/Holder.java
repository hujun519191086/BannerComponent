package com.fqxyi.library.holder;

import android.content.Context;
import android.view.View;

/**
 * @param <T> 任何你指定的对象
 */
public interface Holder<T> {

    View createView(Context context);

    void updateUI(Context context, View view, int position, T data);

}