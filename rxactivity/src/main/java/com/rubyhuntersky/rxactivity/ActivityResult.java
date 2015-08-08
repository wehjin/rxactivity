package com.rubyhuntersky.rxactivity;

import android.content.Intent;

/**
 * @author wehjin
 * @since 2/27/15
 */
public class ActivityResult {

    public int requestCode;
    public int resultCode;
    public Intent data;

    public ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}
