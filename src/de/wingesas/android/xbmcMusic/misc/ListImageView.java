package de.wingesas.android.xbmcMusic.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ListImageView extends ImageView {

	public ListImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
    public ListImageView(Context context, AttributeSet attrs) {
        super(context, attrs);        
    }
    
    public ListImageView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
	
	

	@Override
	public void setPressed(boolean pressed) {

		if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
			return;
		}

		// TODO Auto-generated method stub
		super.setPressed(pressed);
	}

}
