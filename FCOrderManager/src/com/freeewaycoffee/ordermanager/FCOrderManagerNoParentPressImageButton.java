package com.freeewaycoffee.ordermanager;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.util.AttributeSet;


public class FCOrderManagerNoParentPressImageButton extends ImageButton
{
	public FCOrderManagerNoParentPressImageButton(Context context)
	{
		super(context,null);
	}
	
	public FCOrderManagerNoParentPressImageButton(Context context, AttributeSet attrs)
	{
		super(context,attrs);
	}
	
	@Override
    public void setPressed(boolean pressed) 
	{
        // If the parent is pressed, do not set to pressed.
        if (pressed && ((View) getParent()).isPressed()) 
        {
            return;
        }
        super.setPressed(pressed);
    }

}
