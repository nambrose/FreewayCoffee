package com.freewaycoffee.client;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.util.AttributeSet;


public class FreewayCoffeeNoParentPressButtonView extends Button
{
	public FreewayCoffeeNoParentPressButtonView(Context context)
	{
		super(context,null);
	}
	
	public FreewayCoffeeNoParentPressButtonView(Context context, AttributeSet attrs)
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
