package com.demo.carservicetracker2.utils;

import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;


public class BetterActivityResult<Input, Result> {
    private final ActivityResultLauncher<Input> launcher;
    private OnActivityResult<Result> onActivityResult;

    
    public interface OnActivityResult<O> {
        void onActivityResult(O result);
    }

    private BetterActivityResult(ActivityResultCaller caller, ActivityResultContract<Input, Result> contract, OnActivityResult<Result> onActivityResult) {
        this.onActivityResult = onActivityResult;
        this.launcher = caller.registerForActivityResult(contract, new ActivityResultCallback() { 
            @Override 
            public final void onActivityResult(Object obj) {
                BetterActivityResult.this.callOnActivityResult((Result) obj);
            }
        });
    }

    public static <Input, Result> BetterActivityResult<Input, Result> registerForActivityResult(ActivityResultCaller caller, ActivityResultContract<Input, Result> contract, OnActivityResult<Result> onActivityResult) {
        return new BetterActivityResult<>(caller, contract, onActivityResult);
    }

    public static <Input, Result> BetterActivityResult<Input, Result> registerForActivityResult(ActivityResultCaller caller, ActivityResultContract<Input, Result> contract) {
        return registerForActivityResult(caller, contract, null);
    }

    public static BetterActivityResult<Intent, ActivityResult> registerActivityForResult(ActivityResultCaller caller) {
        return registerForActivityResult(caller, new ActivityResultContracts.StartActivityForResult());
    }

    public void setOnActivityResult(OnActivityResult<Result> onActivityResult) {
        this.onActivityResult = onActivityResult;
    }

    public void launch(Input input, OnActivityResult<Result> onActivityResult) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult;
        }
        this.launcher.launch(input);
    }

    public void launch(Input input) {
        launch(input, this.onActivityResult);
    }

    
    public void callOnActivityResult(Result result) {
        OnActivityResult<Result> onActivityResult = this.onActivityResult;
        if (onActivityResult != null) {
            onActivityResult.onActivityResult(result);
        }
    }
}
