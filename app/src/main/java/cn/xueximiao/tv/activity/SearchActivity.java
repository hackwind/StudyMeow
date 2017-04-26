package cn.xueximiao.tv.activity;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.keyboard.SkbContainer;
import com.open.androidtvwidget.keyboard.SoftKey;
import com.open.androidtvwidget.keyboard.SoftKeyBoardListener;
import com.orhanobut.logger.Logger;
import cn.xueximiao.tv.R;

/**
 * Created by Administrator on 2017/1/12.
 */
public class SearchActivity extends Activity{

    private TextView input_tv;
    //软键盘主容器
    private SkbContainer skbContainer;
    //按键的键值
    private SoftKey mOldSoftKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView(){
        input_tv= (TextView) findViewById(R.id.input_tv);
        skbContainer= (SkbContainer) findViewById(R.id.skbContainer);
        skbContainer.setSkbLayout(R.xml.sbd_qwerty);
        skbContainer.setFocusable(true);
        skbContainer.setFocusableInTouchMode(true);
        // 设置属性(默认是不移动的选中边框)
        setSkbContainerMove();

        skbContainer.setSelectSofkKeyFront(true); // 设置选中边框最前面.

        // 监听键盘事件.
        skbContainer.setOnSoftKeyBoardListener(new SoftKeyBoardListener() {
            @Override
            public void onCommitText(SoftKey softKey) {
                if ((skbContainer.getSkbLayoutId() == R.xml.skb_t9_keys)) {
                    onCommitT9Text(softKey);
                } else {
                    int keyCode = softKey.getKeyCode();
                    String keyLabel = softKey.getKeyLabel();
                    if (!TextUtils.isEmpty(keyLabel)) { // 输入文字.
                        input_tv.setText(input_tv.getText() + softKey.getKeyLabel());
                    } else { // 自定义按键，这些都是你自己在XML中设置的keycode.
                        keyCode = softKey.getKeyCode();
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            String text = input_tv.getText().toString();
                            if (TextUtils.isEmpty(text)) {
                                Toast.makeText(getApplicationContext(), "文本已空", Toast.LENGTH_LONG).show();
                            } else {
                                input_tv.setText(text.substring(0, text.length() - 1));
                            }
                        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finish();
                        } else if (keyCode == 66) {
                            Toast.makeText(getApplicationContext(), "回车", Toast.LENGTH_LONG).show();
                        } else if (keyCode == 250) { //切换键盘
                            // 这里只是测试，你可以写自己其它的数字键盘或者其它键盘
                            setSkbContainerOther();
                            skbContainer.setSkbLayout(R.xml.sbd_number);
                        }
                    }
                }
            }

            @Override
            public void onBack(SoftKey key) {
                finish();
            }

            @Override
            public void onDelete(SoftKey key) {
                String text = input_tv.getText().toString();
                input_tv.setText(text.substring(0, text.length() - 1));
            }

        });

        // DEMO（测试键盘失去焦点和获取焦点)
        skbContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Logger.d("hasFocus:"+hasFocus);
                if (hasFocus) {
                    if (mOldSoftKey != null)
                        skbContainer.setKeySelected(mOldSoftKey);
                    else
                        skbContainer.setDefualtSelectKey(0, 0);
                } else {
                    mOldSoftKey = skbContainer.getSelectKey();
                    skbContainer.setKeySelected(null);
                }
            }
        });

    }

    private void setSkbContainerMove() {
        mOldSoftKey = null;
        skbContainer.setMoveSoftKey(true); // 设置是否移动按键边框.
        RectF rectf = new RectF((int)getResources().getDimension(R.dimen.
                px25), (int)getResources().getDimension(R.dimen.
                px25), (int)getResources().getDimension(R.dimen.
                px25), (int)getResources().getDimension(R.dimen.
                px25));
        skbContainer.setSoftKeySelectPadding(rectf); // 设置移动边框相差的间距.
        skbContainer.setMoveDuration(200); // 设置移动边框的时间(默认:300)
        skbContainer.setSelectSofkKeyFront(true); // 设置选中边框在最前面.
    }

    /**
     * 处理T9键盘的按键.
     * @param softKey
     */
    private void onCommitT9Text(SoftKey softKey) {
        Toast.makeText(SearchActivity.this, "keycode:" + softKey.getKeyCode(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 切换布局测试.
     * 因为布局不相同，所以属性不一样，
     * 需要重新设置(不用参考我的,只是DEMO)
     */
    private void setSkbContainerOther(){
        mOldSoftKey = null;
        skbContainer.setMoveSoftKey(false);
        skbContainer.setSoftKeySelectPadding(0);
        skbContainer.setSelectSofkKeyFront(false);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (skbContainer.onSoftKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (skbContainer.onSoftKeyUp(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
