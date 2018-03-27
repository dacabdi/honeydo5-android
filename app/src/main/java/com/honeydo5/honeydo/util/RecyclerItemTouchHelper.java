package com.honeydo5.honeydo.util;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirection, int swipeDirection, RecyclerItemTouchHelperListener listener) {
        super(dragDirection, swipeDirection);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int state) {
        if(viewHolder != null) {
            View foregroundView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutForeground;
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dx, float dy,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutForeground;

        if(dx > 0)
        {
            View backgroundDeleteView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutBackgroundDelete;
            backgroundDeleteView.setVisibility(View.INVISIBLE);
        } else {
            View backgroundEditView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutBackgroundEdit;
        }

        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dx, dy, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View foregroundView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutForeground;

        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dx, float dy, int state, boolean currentlyActive) {
        View foregroundView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutForeground;

        if(dx > 0)
        {
            View backgroundDeleteView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutBackgroundDelete;
            backgroundDeleteView.setVisibility(View.INVISIBLE);
        } else {
            View backgroundEditView = ((TaskAdapter.TaskViewHolder) viewHolder).layoutBackgroundEdit;
        }

        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dx, dy, state, currentlyActive);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
