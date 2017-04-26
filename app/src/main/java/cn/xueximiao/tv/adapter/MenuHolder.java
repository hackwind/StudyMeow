package adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import cn.tv.tv.R;

public class MenuHolder extends OpenPresenter.ViewHolder {

	public ImageView iv;
	public TextView tv;

	public MenuHolder(View itemView) {
		super(itemView);
		iv = (ImageView)itemView.findViewById(R.id.icon);
		tv = (TextView)itemView.findViewById(R.id.title_tv);
	}

}
