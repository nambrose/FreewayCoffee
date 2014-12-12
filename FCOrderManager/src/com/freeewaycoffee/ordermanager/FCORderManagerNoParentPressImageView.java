package com.freeewaycoffee.ordermanager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class FCORderManagerNoParentPressImageView extends ImageView 
{
	public FCORderManagerNoParentPressImageView(Context context) {
		super(context, null);
	}

	public FCORderManagerNoParentPressImageView(Context context, AttributeSet attrs) {
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
