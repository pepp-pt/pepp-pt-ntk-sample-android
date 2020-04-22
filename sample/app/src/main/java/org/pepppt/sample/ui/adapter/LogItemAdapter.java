package org.pepppt.sample.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.LogActivity;
import org.pepppt.sample.ui.utils.DisplayLogger;

import java.util.List;

public class LogItemAdapter extends RecyclerView.Adapter<LogItemAdapter.ViewHolder> {

    private List<LogItem> loglist;

    public LogItemAdapter(List<LogItem> list) {
        loglist = list;
    }

    public void addScanItem(LogItem newitem) {
        DisplayLogger.loglist.add(0, newitem);
        notifyItemInserted(0);
        if (LogActivity.getInstance().syncEnabled())
            LogActivity.getInstance().getLogView().smoothScrollToPosition(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View scandataview = inflater.inflate(R.layout.item_log, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(scandataview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogItem item = loglist.get(position);

        holder.tvLogLine.setText(item.line);
    }

    @Override
    public int getItemCount() {
        return loglist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvLogLine;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvLogLine = (TextView) itemView.findViewById(R.id.tvLogLine);
        }
    }

}
