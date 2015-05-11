package da.se.golist.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import da.se.golist.R;

public class LogoView extends View {

	private Bitmap bitmap, bitmapBackground;
	private Paint paint;
	private static Animation pulseAnimation, roateAnimation;

	public LogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logorand);
		paint = new Paint();		

		pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse);
		roateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
		            paint.setColorFilter(new LightingColorFilter(0x6D6D6D, 0x000000));
		            invalidate();
		            startRotationAnimation();
	                break;
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
	}
	
	public void showLogoBackground(){
		if(getWidth() <= 0 || getHeight() <= 0){
			bitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.logomitte);
			return;
		}
		bitmapBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logomitte), getWidth(), getHeight(), false);
	}

	public void startRotationAnimation() {
		if(getAnimation() == null){
			startAnimation(roateAnimation);
		}
	}
	
	public void startPulseAnimation(){
		startAnimation(pulseAnimation);
	}
	
	public void stopAnimation(){
		clearAnimation();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bitmapBackground != null){
			canvas.drawBitmap(bitmapBackground, 0, 0, paint);
		}
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}
}
