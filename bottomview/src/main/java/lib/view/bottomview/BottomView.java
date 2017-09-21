package lib.view.bottomview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class BottomView extends FrameLayout {

    BottomViewBehavior bottomViewBehavior;
    // Variables
    private Context context;
    private Resources resources;
    private boolean translucentViewEnabled;
    private boolean isBehaviorTranslationSet = false;
    private boolean behaviorTranslationEnabled = true;
    private boolean needHideBottomView = false;
    private boolean hideBottomViewWithAnimation = false;
    private boolean soundEffectsEnabled = true;
    // Variables (Styles)  
    private int bottomViewHeight, ViewBarHeight = 0;

    /**
     * Constructors
     */
    public BottomView(Context context) {
        super(context);
        init(context, null);
    }

    public BottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setSoundEffectsEnabled(final boolean soundEffectsEnabled) {
        super.setSoundEffectsEnabled(soundEffectsEnabled);
        this.soundEffectsEnabled = soundEffectsEnabled;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isBehaviorTranslationSet) {
            //The translation behavior has to be set up after the super.onMeasure has been called.
            setBehaviorTranslationEnabled(behaviorTranslationEnabled);
            isBehaviorTranslationSet = true;
        }
    }

    /**
     * Init
     *
     * @param context
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        resources = this.context.getResources();

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomViewBehavior_Params, 0, 0);
            try {
                translucentViewEnabled = ta.getBoolean(R.styleable.BottomViewBehavior_Params_translucentViewEnabled, false);

            } finally {
                ta.recycle();
            }
        }

        bottomViewHeight = (int) resources.getDimension(R.dimen.bottom_view_height);

        ViewCompat.setElevation(this, resources.getDimension(R.dimen.bottom_view_elevation));
        setClipToPadding(false);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, bottomViewHeight);
        setLayoutParams(params);
    }

    /////////////
    // PRIVATE //
    /////////////

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private int calculateHeight(int layoutHeight) {
        if (!translucentViewEnabled) return layoutHeight;

        int resourceId = getResources().getIdentifier("View_bar_height", "dimen", "android");
        if (resourceId > 0) {
            ViewBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        int[] attrs = {android.R.attr.fitsSystemWindows, android.R.attr.windowTranslucentNavigation};
        TypedArray typedValue = getContext().getTheme().obtainStyledAttributes(attrs);

        @SuppressWarnings("ResourceType")
        boolean fitWindow = typedValue.getBoolean(0, false);

        @SuppressWarnings("ResourceType")
        boolean translucentView = typedValue.getBoolean(1, true);

        if (hasImmersive() /*&& !fitWindow*/ && translucentView) {
            layoutHeight += ViewBarHeight;
        }

        typedValue.recycle();

        return layoutHeight;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean hasImmersive() {
        Display d = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth > displayWidth) || (realHeight > displayHeight);
    }

    /**
     * Return if the behavior translation is enabled
     *
     * @return a boolean value
     */
    public boolean isBehaviorTranslationEnabled() {
        return behaviorTranslationEnabled;
    }

    /**
     * Set the behavior translation value
     *
     * @param behaviorTranslationEnabled boolean for the state
     */
    public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
        this.behaviorTranslationEnabled = behaviorTranslationEnabled;
        if (getParent() instanceof CoordinatorLayout) {
            ViewGroup.LayoutParams params = getLayoutParams();
            if (bottomViewBehavior == null) {
                bottomViewBehavior = new BottomViewBehavior<>(behaviorTranslationEnabled, ViewBarHeight);
            } else {
                bottomViewBehavior.setBehaviorTranslationEnabled(behaviorTranslationEnabled, ViewBarHeight);
            }
            ((CoordinatorLayout.LayoutParams) params).setBehavior(bottomViewBehavior);
            if (needHideBottomView) {
                needHideBottomView = false;
                bottomViewBehavior.hideView(this, bottomViewHeight, hideBottomViewWithAnimation);
            }
        }
    }

    /**
     * Hide Bottom View with animation
     */
    public void hideBottomView() {
        hideBottomView(true);
    }

    /**
     * Hide Bottom View with or without animation
     *
     * @param withAnimation Boolean
     */
    public void hideBottomView(boolean withAnimation) {
        if (bottomViewBehavior != null) {
            bottomViewBehavior.hideView(this, bottomViewHeight, withAnimation);
        } else if (getParent() instanceof CoordinatorLayout) {
            needHideBottomView = true;
            hideBottomViewWithAnimation = withAnimation;
        } else {
            // Hide bottom View
            ViewCompat.animate(this)
                    .translationY(bottomViewHeight)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .setDuration(withAnimation ? 300 : 0)
                    .start();
        }
    }

    /**
     * Restore Bottom View with animation
     */
    public void restoreBottomView() {
        restoreBottomView(true);
    }

    /**
     * Restore Bottom View with or without animation
     *
     * @param withAnimation Boolean
     */
    public void restoreBottomView(boolean withAnimation) {
        if (bottomViewBehavior != null) {
            bottomViewBehavior.resetOffset(this, withAnimation);
        } else {
            // Show bottom View
            ViewCompat.animate(this)
                    .translationY(0)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .setDuration(withAnimation ? 300 : 0)
                    .start();
        }
    }

    /**
     * Return if the Bottom View is hidden or not
     */
    public boolean isHidden() {
        if (bottomViewBehavior != null) {
            return bottomViewBehavior.isHidden();
        }
        return false;
    }

}