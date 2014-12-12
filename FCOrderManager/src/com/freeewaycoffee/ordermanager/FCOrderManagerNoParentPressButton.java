package com.freeewaycoffee.ordermanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class FCOrderManagerNoParentPressButton extends Button 
{
	public FCOrderManagerNoParentPressButton(Context context)
	{
		super(context,null);
	}
	
	public FCOrderManagerNoParentPressButton(Context context, AttributeSet attrs)
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
