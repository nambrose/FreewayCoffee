package com.freeewaycoffee.ordermanager2;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class FCOrderManagerNoParentPressImageView extends ImageView 
{
	public FCOrderManagerNoParentPressImageView(Context context) {
		super(context, null);
	}

	public FCOrderManagerNoParentPressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setPressed(boolean pressed) {
		// If the parent is pressed, do not set to pressed.
		if (pressed && ((View) getParent()).isPressed()) {
			return;
		}
		super.setPressed(pressed);
	}


}
