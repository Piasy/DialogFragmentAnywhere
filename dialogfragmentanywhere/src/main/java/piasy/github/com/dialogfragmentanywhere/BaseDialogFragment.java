/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package piasy.github.com.dialogfragmentanywhere;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.github.piasy.safelyandroid.activity.StartActivityDelegate;
import com.github.piasy.safelyandroid.dialogfragment.SupportDialogFragmentDismissDelegate;
import com.github.piasy.safelyandroid.fragment.SupportFragmentTransactionDelegate;
import com.github.piasy.safelyandroid.fragment.TransactionCommitter;

/**
 * Created by Piasy on 16/4/28.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class BaseDialogFragment extends DialogFragment implements TransactionCommitter {

    protected static final float DEFAULT_DIM_AMOUNT = 0.2F;
    protected static final String ANCHOR_VIEW_X = "ANCHOR_VIEW_X";
    protected static final String ANCHOR_VIEW_Y = "ANCHOR_VIEW_Y";
    protected static final String ANCHOR_VIEW_WIDTH = "ANCHOR_VIEW_WIDTH";
    protected static final String ANCHOR_VIEW_HEIGHT = "ANCHOR_VIEW_HEIGHT";
    protected static final String LOCATE_TO_ANCHOR = "LOCATE_TO_ANCHOR";
    protected static final String OFFSET_X = "OFFSET_X";
    protected static final String OFFSET_Y = "OFFSET_Y";

    public static final int LOCATE_LEFT = 1;
    public static final int LOCATE_ABOVE = 2;
    public static final int LOCATE_RIGHT = 3;
    public static final int LOCATE_BELOW = 4;

    private final SupportDialogFragmentDismissDelegate mSupportDialogFragmentDismissDelegate =
            new SupportDialogFragmentDismissDelegate();

    private final SupportFragmentTransactionDelegate mSupportFragmentTransactionDelegate =
            new SupportFragmentTransactionDelegate();

    @IntDef({ LOCATE_LEFT, LOCATE_ABOVE, LOCATE_RIGHT, LOCATE_BELOW })
    public @interface Locate {}

    public static Bundle anchorTo(View anchor, @Locate int locate, int offsetX, int offsetY) {
        Bundle args = new Bundle();
        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc);
        args.putInt(ANCHOR_VIEW_X, loc[0]);
        args.putInt(ANCHOR_VIEW_Y, loc[1]);
        args.putInt(ANCHOR_VIEW_WIDTH, anchor.getWidth());
        args.putInt(ANCHOR_VIEW_HEIGHT, anchor.getHeight());
        args.putInt(LOCATE_TO_ANCHOR, locate);
        args.putInt(OFFSET_X, offsetX);
        args.putInt(OFFSET_Y, offsetY);
        return args;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        return inflater.inflate(getLayoutRes(), container, false);
    }

    /**
     * CONTRACT: the new life cycle method {@link #initFields()}, {@link #bindView(View)}
     * and {@link #startBusiness()} might use other infrastructure initialised in subclass's
     * onViewCreated, e.g. DI, MVP, so those subclass should do those
     * infrastructure init job before this method is invoked.
     */
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(new Runnable() {
            @Override
            public void run() {
                anchorDialog();
            }
        });
        initFields();
        bindView(view);
        startBusiness();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Less dimmed background; see http://stackoverflow.com/q/13822842/56285
        final Window window = getDialog().getWindow();
        final WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = getDimAmount(); // dim only a little bit
        window.setAttributes(params);

        window.setLayout(getWidth(), getHeight());
        window.setGravity(Gravity.LEFT | Gravity.TOP);

        // Transparent background; see http://stackoverflow.com/q/15007272/56285
        // (Needed to make dialog's alpha shadow look good)
        window.setBackgroundDrawableResource(android.R.color.transparent);

        final Resources res = getResources();
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        if (titleDividerId > 0) {
            final View titleDivider = getDialog().findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(res.getColor(android.R.color.transparent));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSupportDialogFragmentDismissDelegate.onResumed(this);
        mSupportFragmentTransactionDelegate.onResumed();
    }

    public void onDestroyView() {
        super.onDestroyView();
        unbindView();
    }

    protected final boolean startActivitySafely(final Intent intent) {
        return StartActivityDelegate.startActivitySafely(this, intent);
    }

    protected boolean safeCommit(@NonNull final FragmentTransaction transaction) {
        return mSupportFragmentTransactionDelegate.safeCommit(this, transaction);
    }

    public boolean safeDismiss() {
        return mSupportDialogFragmentDismissDelegate.safeDismiss(this);
    }

    @Override
    public boolean isCommitterResumed() {
        return isResumed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (isCanceledOnBackPressed()) {
                    super.onBackPressed();
                }
            }
        };
    }

    void anchorDialog() {
        final Bundle args = getArguments();
        if (args == null) {
            return;
        }

        final Window window = getDialog().getWindow();
        final WindowManager.LayoutParams params = window.getAttributes();

        int anchorX = args.getInt(ANCHOR_VIEW_X);
        int anchorY = args.getInt(ANCHOR_VIEW_Y);
        int anchorWidth = args.getInt(ANCHOR_VIEW_WIDTH);
        int anchorHeight = args.getInt(ANCHOR_VIEW_HEIGHT);
        int dialogWidth = window.getDecorView().getWidth();
        int dialogHeight = window.getDecorView().getHeight();
        int locate = args.getInt(LOCATE_TO_ANCHOR);
        int offsetX = args.getInt(OFFSET_X);
        int offsetY = args.getInt(OFFSET_Y);

        switch (locate) {
            case LOCATE_LEFT:
                params.x = anchorX - dialogWidth + offsetX;
                params.y =
                        anchorY - Math.abs(dialogHeight - anchorHeight) / 2 - getStatusBarHeight() +
                                offsetY;
                break;
            case LOCATE_RIGHT:
                params.x = anchorX + anchorWidth + offsetX;
                params.y =
                        anchorY - Math.abs(dialogHeight - anchorHeight) / 2 - getStatusBarHeight() +
                                offsetY;
                break;
            case LOCATE_BELOW:
                params.x = anchorX - Math.abs(dialogWidth - anchorWidth) / 2 + offsetX;
                params.y = anchorY + anchorHeight - getStatusBarHeight() + offsetY;
                break;
            case LOCATE_ABOVE:
                params.x = anchorX - Math.abs(dialogWidth - anchorWidth) / 2 + offsetX;
                params.y = anchorY - dialogHeight - getStatusBarHeight() + offsetY;
            default:
                break;
        }
        window.setAttributes(params);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected float getDimAmount() {
        return DEFAULT_DIM_AMOUNT;
    }

    protected abstract int getWidth();

    protected abstract int getHeight();

    protected boolean isCanceledOnTouchOutside() {
        return true;
    }

    protected boolean isCanceledOnBackPressed() {
        return true;
    }

    /**
     * init necessary fields.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void initFields() {

    }

    /**
     * bind views, should override this method when bind view manually.
     */
    protected void bindView(final View rootView) {

    }

    /**
     * start specific business logic.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void startBusiness() {

    }

    /**
     * unbind views, should override this method when unbind view manually.
     */
    protected void unbindView() {

    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
