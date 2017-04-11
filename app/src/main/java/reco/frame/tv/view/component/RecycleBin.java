package reco.frame.tv.view.component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.renderscript.Int2;
import android.support.v4.util.LruCache;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RecycleBin {
	private final static String TAG = "RecycleBin";
	public final static int STATE_ACTIVE = 0, STATE_SCRAP = 1;

	public static LruCache<String, Bitmap> mLruCache;

	private String cachedDir;

	private static ExecutorService sExecutorService;

	private Handler handler;

	// private static SparseIntArray recycleIds;

	private static SparseIntArray itemStates;

	private Map<Integer, HashSet<Integer>> recycleIds;

	private final static int STATE_RECYCLING = 1, STATE_RELOADING = 2;

	public RecycleBin(String cachedDir) {
		this.cachedDir = cachedDir;
		handler = new Handler();
		// recycleIds = new SparseIntArray();
		itemStates = new SparseIntArray();
		initLruCache();
		startThreadPoolIfNecessary();

		recycleIds = new HashMap<Integer, HashSet<Integer>>();

	}

	private static void initLruCache() {
		if (mLruCache == null) {

			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

			int cacheSize = maxMemory / 6;
			mLruCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {

					return bitmap.getByteCount() / 1024;
				}
			};
		}

	}


	public static void startThreadPoolIfNecessary() {
		if (sExecutorService == null || sExecutorService.isShutdown()
				|| sExecutorService.isTerminated()) {
			sExecutorService = Executors.newFixedThreadPool(3);
		}
	}

	public void recycleChild(HashSet<Integer> idSet,
			final View scrapView, int itemId) {


		if (scrapView instanceof ImageView) {
			((ImageView) scrapView).setImageResource(0);
			// if (front != null && front instanceof BitmapDrawable) {
			// BitmapDrawable bitmapDrawable = (BitmapDrawable) front;
			// Bitmap bitmap = bitmapDrawable.getBitmap();
			// if (bitmap != null && !bitmap.isRecycled()) {
			// recycleBitmap(bitmap);
			// }
			// }

		}

		Drawable background = scrapView.getBackground();
		if (background != null) {

			BitmapDrawable bitmapDrawable = null;

			if (background instanceof TransitionDrawable) {
				background = ((TransitionDrawable) background).getDrawable(1);
			}
			bitmapDrawable = (BitmapDrawable) background;
			final Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {

				if (saveBitmap(itemId, scrapView.getId(), bitmap)) {
					idSet.add(scrapView.getId());
					handler.post(new Runnable() {

						@Override
						public void run() {
							scrapView.setBackgroundResource(0);

						}
					});
				}
			}
			

		}

	}

	public void recycleView(final View item) {

		sExecutorService.submit(new Runnable() {

			@Override
			public void run() {
				if (itemStates.get(item.getId(), -1) != -1) {
					return;
				}

				itemStates.put(item.getId(), STATE_RECYCLING);
				HashSet<Integer> set = new HashSet<Integer>();
				if (item instanceof ViewGroup) {
					ViewGroup container = (ViewGroup) item;

					for (int i = 0; i < container.getChildCount(); i++) {
						View child = container.getChildAt(i);
						recycleChild(set, child, item.getId());
					}
					if (!set.isEmpty()) {
						recycleIds.put(item.getId(), set);
					}
				}

			}
		});

	}

	public void reloadView(final View item) {

		if (item == null) {
			return;
		}
		final HashSet<Integer> childIds = (HashSet<Integer>) recycleIds
				.get(item.getId());

		if (childIds == null) {
			return;
		}

		int state = itemStates.get(item.getId()) + 0;
		if (state != STATE_RECYCLING) {
			return;
		}

		itemStates.delete(item.getId());
		itemStates.put(item.getId(), STATE_RELOADING);

		// �Ի����ȡͼƬ

		Object[] childIdArray = childIds.toArray();
		for (int i = 0; i < childIdArray.length; i++) {
			final int childId = (Integer) childIdArray[i];

			final Bitmap bmp = getBitmapFromMemory(childId + "");
			if (bmp != null) {

				if (childId == item.getId()) {


				} else {

					Log.d(TAG, "�ɹ��Ի����ȡͼƬ" + childId + "--" + item.getId());
					View child = item.findViewById(childId);

					child.setBackgroundDrawable(new BitmapDrawable(bmp));
				}
				childIds.remove(childId);
			} else {
				sExecutorService.submit(new Runnable() {
					@Override
					public void run() {

						final Bitmap bmp = getBitmapFromDisk(item.getId());

						if (bmp == null)
							return;

						setBitmapToMemory(item.getId() + "", bmp);

						handler.post(new Runnable() {
							@Override
							public void run() {

								if (childId == item.getId()) {
									// item��ͼ

								} else {
									// ������ͼ

									View child = item.findViewById(childId);
									child.setBackgroundDrawable(new BitmapDrawable(
											bmp));
								}

								childIds.remove(childId);
							}
						});
					}
				});
			}
		}

		recycleIds.remove(item.getId());
		itemStates.delete(item.getId());

	}

	public static Bitmap getBitmapFromMemory(String key) {
		return mLruCache.get(TvUtil.md5(key));
	}


	private Bitmap getBitmapFromDisk(int viewId) {
		Bitmap bitmap = null;
		String fileName = TvUtil.md5(viewId + "");

		String filePath = cachedDir + "/" + fileName;

		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	public static void setBitmapToMemory(String key, Bitmap bitmap) {
		String md5 = TvUtil.md5(key);
		if (mLruCache.get(md5) == null) {
			mLruCache.put(md5, bitmap);
		}
	}

	public boolean saveBitmap(int itemId, int childId, Bitmap bmp) {

		String key = TvUtil.md5(itemId + "");

		setBitmapToMemory(key, bmp);

		try {

			String filePath = this.cachedDir + "/" + key;
			FileOutputStream fos = new FileOutputStream(filePath);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();

			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}


	public interface DrawableCallback {

		public void onDrawableLoaded(Drawable draw);

	}

}