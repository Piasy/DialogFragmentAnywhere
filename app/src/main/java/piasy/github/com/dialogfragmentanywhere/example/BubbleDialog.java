package piasy.github.com.dialogfragmentanywhere.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import piasy.github.com.dialogfragmentanywhere.BaseDialogFragment;

/**
 * Created by Piasy on 16/4/28.
 */
public class BubbleDialog extends BaseDialogFragment {

    private static final int WIDTH_DP = 144;

    public interface Action {
        void createGroup();
        void addFriend();
    }

    public static void showAt(FragmentManager fragmentManager, View anchor, @Locate int locate,
            int offsetX, int offsetY) {
        BubbleDialog dialog = new BubbleDialog();
        dialog.setArguments(anchorTo(anchor, locate, offsetX, offsetY));
        dialog.show(fragmentManager, "BubbleDialog");
    }

    private Action mAction;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getTargetFragment() instanceof Action) {
            mAction = (Action) getTargetFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAction = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.mTvCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    mAction.createGroup();
                }
                safeDismiss();
            }
        });
        view.findViewById(R.id.mTvAddFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    mAction.addFriend();
                }
                safeDismiss();
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.bubble_dialog;
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    @Override
    protected int getWidth() {
        return (int) (getResources().getDisplayMetrics().density * WIDTH_DP);
    }

    @Override
    protected int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }
}
