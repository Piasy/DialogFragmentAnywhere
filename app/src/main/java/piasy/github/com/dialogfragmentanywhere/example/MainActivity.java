package piasy.github.com.dialogfragmentanywhere.example;

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
import java.util.List;
import piasy.github.com.dialogfragmentanywhere.BaseDialogFragment;

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
        WPDialog.showAt(getSupportFragmentManager(), v, getLocate(), getOffsetX(), getOffsetY());
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
