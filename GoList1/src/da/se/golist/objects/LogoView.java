package da.se.golist.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import da.se.golist.R;

public class LogoView extends View {

	Bitmap bitmap, bitmapBackground;
	Float mRotate = 0f;
	Handler h;
	private Paint paint;
	// State variables
	final int STATE_PAUSE = 2;
	final int STATE_ROTATE = 3;
	int STATE_CURRENT;
	private ScaleAnimation blinkanimation;

	public LogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		h = new Handler();
		bitmapBackground = null;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logorand);
		STATE_CURRENT = STATE_PAUSE;
		paint = new Paint();
		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                // 0x6D6D6D sets how much to darken - tweak as desired
	            	LightingColorFilter darken = new LightingColorFilter(0x6D6D6D, 0x000000);
		            paint.setColorFilter(darken);
		            invalidate();
		            startAnimation(blinkanimation);
	                break;
	            // remove the filter when moving off the button
	            // the same way a selector implementation would 
	            case MotionEvent.ACTION_MOVE:
	                Rect r = new Rect();
	                v.getLocalVisibleRect(r);
	                if (!r.contains((int) event.getX(), (int) event.getY())) {
	                	paint.setColorFilter(null);
	                }
		            invalidate();
	                break;
	            case MotionEvent.ACTION_OUTSIDE:
	            case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_UP:
                	paint.setColorFilter(null);
		            invalidate();
		            performClick();
	                break;
	        }
	        return true;
			}
		});
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(bitmapBackground != null){
			bitmapBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logomitte), w, h, false);
		}
		bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logorand), w, h, false);
		
		blinkanimation= new ScaleAnimation(getScaleX(), getScaleX()/2, getScaleY(), getScaleY()/2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // Change alpha from fully visible to invisible
		blinkanimation.setDuration(150); // duration - half a second
		blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkanimation.setRepeatCount(1); // Repeat animation infinitely
		blinkanimation.setRepeatMode(Animation.REVERSE);	
	}

	Runnable move = new Runnable() {
		@Override
		public void run() {
			switch (STATE_CURRENT) {
			case STATE_ROTATE:
				mRotate += 3;
				invalidate();				
				h.postDelayed(move, 20);
				break;
			}
		}
	};
	
	public void showLogoBackground(){
		if(getWidth() <= 0 || getHeight() <= 0){
			bitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.logomitte);
			return;
		}
		bitmapBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logomitte), getWidth(), getHeight(), false);
	}

	public void startDrawing() {
		if (STATE_CURRENT == STATE_PAUSE) {
			STATE_CURRENT = STATE_ROTATE;
			h.postDelayed(move, 20);
		}
	}
	
	public void stopDrawing() {
		STATE_CURRENT = STATE_PAUSE;
	}
	
	public int getCurrentState(){
		return STATE_CURRENT;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bitmapBackground != null){
			canvas.drawBitmap(bitmapBackground, 0, 0, null);
		}
		canvas.rotate(mRotate, getWidth() / 2, getHeight() / 2);
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}
}
