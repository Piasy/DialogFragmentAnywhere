package com.github.piasy.dialogfragmentanywhere.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.piasy.dialogfragmentanywhere.BaseDialogFragment;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BindViews({ R.id.mEtOffSetX, R.id.mEtOffSetY })
    List<EditText> mOffsets;
    @BindView(R.id.mLocate)
    Spinner mLocates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.mBtnCenter })
    public void showDialog(View v) {
        BubbleDialog.showAt(getSupportFragmentManager(), v, getLocate(), getOffsetX(),
                getOffsetY());
    }

    private static final int BUBBLE_OFFSET_X_DP = 53;
    private static final int BUBBLE_OFFSET_Y_DP = 7;

    @OnClick({ R.id.mBtnAdd })
    public void showAddDialog(View v) {
        BubbleDialog.showAt(getSupportFragmentManager(), v, BaseDialogFragment.LOCATE_BELOW,
                (int) -(getResources().getDisplayMetrics().density * BUBBLE_OFFSET_X_DP),
                (int) (getResources().getDisplayMetrics().density * BUBBLE_OFFSET_Y_DP));
    }

    private int getOffsetX() {
        String offsetX = mOffsets.get(0).getText().toString();
        return TextUtils.isEmpty(offsetX) ? 0 : Integer.parseInt(offsetX);
    }

    private int getOffsetY() {
        String offsetY = mOffsets.get(1).getText().toString();
        return TextUtils.isEmpty(offsetY) ? 0 : Integer.parseInt(offsetY);
    }

    @BaseDialogFragment.Locate
    int getLocate() {
        switch (mLocates.getSelectedItemPosition()) {
            case 0:
                return BaseDialogFragment.LOCATE_LEFT;
            case 2:
                return BaseDialogFragment.LOCATE_RIGHT;
            case 3:
                return BaseDialogFragment.LOCATE_BELOW;
            case 1:
            default:
                return BaseDialogFragment.LOCATE_ABOVE;
        }
    }
}
