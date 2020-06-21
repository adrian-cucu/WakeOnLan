package com.wakeonlan;

public interface AdapterItemActionListener {

    void onClick(int position);

    void onDelete(int position);

    void onEdit(int position);
}
