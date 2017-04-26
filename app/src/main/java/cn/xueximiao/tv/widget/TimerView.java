package cn.xueximiao.tv.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/14/014.
 */

public class TimerView extends AppCompatTextView {
    private MyHandler handler ;
    public TimerView(Context context) {
        super(context);
        init();
    }

    public TimerView(Context context, AttributeSet attr){
        super(context,attr);
        init();
    }

    public void init(){
        handler = new MyHandler(this);
        handler.sendEmptyMessage(0);
    }

    private class MyHandler extends Handler {
        WeakReference<TimerView> ref = null;

        public MyHandler(TimerView timerView) {
            ref = new WeakReference<TimerView>(timerView);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                String formatDate = null;
                Date date = new Date();
                DateFormat sdf = new SimpleDateFormat("HH:mm");
                formatDate = sdf.format(date);
                setText(formatDate);
                sendMessageDelayed(Message.obtain(),10000);
            }
        }
    }

}
