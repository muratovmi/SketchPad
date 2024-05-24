package com.itschoolsamsung.sketchpad.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itschoolsamsung.sketchpad.R;

public class OperationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String[] mOperationList;
    private final Context mContext;
    private final LayoutInflater mInflater;
    // Объект, который нужен для перехода от события onClick при просмотре к основному действию.
    private final OperationViewHolder.OperationCommunicator mCallback;

    public OperationRecyclerViewAdapter(Context context, OperationViewHolder.OperationCommunicator callback) {
        mContext = context;
        mCallback = callback;
        mOperationList = mContext.getResources().getStringArray(R.array.operation_icon);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.operation_recycler_view_single_row, viewGroup, false);
        return new OperationViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        OperationViewHolder holder = (OperationViewHolder) viewHolder;
        holder.bindData(mOperationList[i], mContext);
    }

    @Override
    public int getItemCount() {
        return mOperationList.length;
    }

    public static class OperationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView operationImageHolder;
        private final OperationCommunicator obj;

        public OperationViewHolder(View itemView, OperationCommunicator callback) {
            super(itemView);
            obj = callback;
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            operationImageHolder = itemView.findViewById(R.id.operation_imageview);
        }

        @SuppressLint("DiscouragedApi")
        public void bindData(String val, Context context) {
            operationImageHolder.setImageResource(context.getResources().getIdentifier(val, "drawable", context.getPackageName()));
        }

        // Переопределённый метод onClick, который вызывается при нажатии на инструмент из набора инструментов.
        @Override
        public void onClick(View v) {
            // Метод, переопределённый в главной активити.
            obj.getPosition(getAdapterPosition());
        }

        // Интерфейс для коммуникации
        public interface OperationCommunicator {
            void getPosition(int position);
        }
    }
}