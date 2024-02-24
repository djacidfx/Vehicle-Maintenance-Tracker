package com.demo.carservicetracker2.backupRestore;

import java.util.ArrayList;


public interface OnBackupRestore {
    void getList(ArrayList<RestoreRowModel> list);

    void onSuccess(boolean isSuccess);
}
