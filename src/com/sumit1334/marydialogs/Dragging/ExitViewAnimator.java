package com.sumit1334.marydialogs.Dragging;


import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.core.view.ViewPropertyAnimatorUpdateListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExitViewAnimator<D extends DraggableView> extends ReturnOriginViewAnimator<D> {

    @Override
    public boolean animateExit(@NonNull final D draggableView, final DraggableView.Direction direction, int duration) {
        draggableView.setDraggable(false);
        draggableView.setAnimating(true);

        int translation = 0;
        switch (direction) {
            case LEFT:
                translation = (int) -(draggableView.getParentWidth());
                break;
            case RIGHT:
                translation = (int) (draggableView.getParentWidth());
                break;
            case TOP:
                translation = (int) -draggableView.getHeight() * 3;
                break;
            case BOTTOM:
                translation = (int) draggableView.getHeight() * 3;
                break;
        }

        ViewPropertyAnimatorCompat animator = null;

        switch (direction) {
            case LEFT:
            case RIGHT:
                animator = ViewCompat.animate(draggableView).withLayer().translationX(translation);
                break;
            case TOP:
            case BOTTOM:
                animator = ViewCompat.animate(draggableView).withLayer().translationY(translation);
                break;
        }

        final AtomicBoolean willUpdate = new AtomicBoolean(true);

        animator
                .setDuration(duration)
                .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(View view) {
                        if(willUpdate.get()) {
                            notifyDraggableViewUpdated(draggableView);
                        }
                    }
                })
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        willUpdate.set(false);

                        DraggableView.DraggableViewListener dragListener = draggableView.getDragListener();
                        if (dragListener != null) {
                            dragListener.onDraggedEnded(draggableView, direction);
                        }

                        draggableView.setAnimating(false);
                    }
                });

        return true;
    }
}
