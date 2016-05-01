package piasy.github.com.dialogfragmentanywhere.example;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.OnClick;
import piasy.github.com.dialogfragmentanywhere.BaseDialogFragment;

/**
 * Created by Piasy on 16/4/28.
 */
public class WPDialog extends BaseDialogFragment {

    public static void showAt(FragmentManager fragmentManager, View anchor, @Locate int locate,
            int offsetX, int offsetY) {
        WPDialog dialog = new WPDialog();
        dialog.setArguments(anchorTo(anchor, locate, offsetX, offsetY));
        dialog.show(fragmentManager, "WPDialog");
    }

    @OnClick({ R.id.mOp1, R.id.mOp2, R.id.mOp3, R.id.mOp4, R.id.mOp5 })
    public void onOpsClicked(View op) {
        switch (op.getId()) {
            case R.id.mOp1:
                Toast.makeText(getContext(), "clicked op1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mOp2:
                Toast.makeText(getContext(), "clicked op2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mOp3:
                Toast.makeText(getContext(), "clicked op3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mOp4:
                Toast.makeText(getContext(), "clicked op4", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mOp5:
                Toast.makeText(getContext(), "clicked op5", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog;
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected boolean autoBindViews() {
        return true;
    }
}
