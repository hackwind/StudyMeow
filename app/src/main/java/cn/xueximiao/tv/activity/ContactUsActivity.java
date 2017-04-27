package cn.xueximiao.tv.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.entity.GetQRCodeEntity;
import cn.xueximiao.tv.http.HttpImageAsync;
import cn.xueximiao.tv.http.HttpRequest;
import cn.xueximiao.tv.http.HttpAddress;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ContactUsActivity extends BaseActivity {
    private final static int REPEAT_INTERVAL = 2000;
    private int type;//1:问题反馈;2.联系我们
    private ImageView wxImage;
    private TextView setType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contactus);
        wxImage = (ImageView)findViewById(R.id.wechat_image);
        setType = (TextView)findViewById(R.id.set_type);
        type = getIntent().getIntExtra("type",0);
        if(type == 1) {
            setType.setText(R.string.feed_back);
        } else if(type == 2) {
            setType.setText(R.string.contact_us);
        }
        getQRCode(type);
    }


    private void getQRCode(int type) {
        HttpRequest.get(HttpAddress.getOtherQRCode(type),null,ContactUsActivity.this,"getQRCodeBack",null,this, GetQRCodeEntity.class);
    }


    public void getQRCodeBack(GetQRCodeEntity entity,String totalResult) {
        if(entity == null || entity.data == null || entity.data.erweima == null) {
            return;
        }
        HttpImageAsync.loadingImage(wxImage,entity.data.erweima);


    }

}
