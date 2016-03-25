package com.trx.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.trx.mobilesafe.R;
import com.trx.mobilesafe.R.id;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.dao.AddressDao;
import com.trx.mobilesafe.dao.BlackNumberDao;
import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.utils.PrefUtils;
import com.trx.mobilesafe.utils.StreamUtils;
import com.trx.mobilesafe.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;

/**
 * Created by trx08 on 2016/3/12.
 */
public class SplashActivity extends BaseActivity {
	private static final int CODE_UPDATE_DIALOG = 1;
	private static final int CODE_ENTER_HOME = 2;
	private static final int CODE_URL_ERROR = 3;
	private static final int CODE_NETWORK_ERROR = 4;
	private static final int CODE_JSON_ERROR = 5;

	private TextView tvSplashVersion;
	private ProgressBar pbSplash;
	private String mVersionName;
	private String mDesc;
	private String mUrl;
	private int mVersionCode;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			case CODE_JSON_ERROR:
				ToastUtils.showToast(SplashActivity.this, "数据解析异常");
				enterHome();
				break;
			case CODE_NETWORK_ERROR:
				ToastUtils.showToast(SplashActivity.this, "网络异常");
				enterHome();
				break;
			case CODE_URL_ERROR:
				ToastUtils.showToast(SplashActivity.this, "网络链接异常");
				enterHome();
				break;
			}
		}
	};
	private TextView tvSplashProgress;
	private RelativeLayout rlSplash;

	@Override
	public void initView() {
		setContentView(R.layout.activity_splash);

		tvSplashVersion = (TextView) findViewById(id.tv_splash_version);
		pbSplash = (ProgressBar) findViewById(id.pb_splash);
		tvSplashProgress = (TextView) findViewById(id.tv_splash_progress);
		rlSplash = (RelativeLayout) findViewById(id.rl_Splash);

	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {
		tvSplashVersion.setText("版本号：" + getVersionName());

		AlphaAnimation animation = new AlphaAnimation(0.2f, 1);
		animation.setDuration(2000);
		rlSplash.startAnimation(animation);

		boolean auto_update = PrefUtils.getBoolean("auto_update", false, this);
		if (auto_update) {
			checkVersion();
		} else {
			handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}

		copyDb("address.db");
		copyDb("commonnum.db");
		copyDb("antivirus.db");
		copyDb("myblacknumber.db");
		copyBlackNumberData();

		installShortcut();
	}

	@Override
	public void processClick(View v) {

	}

	public void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SplashActivity.this);

		builder.setTitle("发现新版本:" + mVersionName);
		builder.setMessage(mDesc);
		// builder.setCancelable(false);//不可取消,点返回键弹窗不消失, 尽量不要用,用户体验不好
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadApk();
					}
				});
		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						enterHome();
					}
				});

		// 用户取消弹窗的监听,比如点返回键
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});

		builder.show();
	}

	public void downloadApk() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mobilesafe.apk";
		tvSplashProgress.setVisibility(View.VISIBLE);

		HttpUtils utils = new HttpUtils();
		utils.download(mUrl, path, new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				// 下载成功
				String p = responseInfo.result.getAbsolutePath();

				// 跳转系统安装页面
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setDataAndType(Uri.fromFile(responseInfo.result),
						"application/vnd.android.package-archive");
				startActivityForResult(intent, 0);
			}

			@Override
			public void onFailure(HttpException e, String s) {
				e.printStackTrace();
				ToastUtils.showToast(SplashActivity.this, s);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				long percent = current * 100 / total;
				tvSplashProgress.setText("当前进度：" + percent + "%");
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	public void checkVersion() {

		new Thread() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				long startMill = System.currentTimeMillis();
				Message msg = handler.obtainMessage();
				try {
					URL url = new URL("http://192.168.0.101:8080/update.json");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(2000);
					connection.setReadTimeout(2000);
					connection.connect();

					if (200 == connection.getResponseCode()) {
						InputStream inputStream = connection.getInputStream();

						String data = StreamUtils.stream2String(inputStream);

						JSONObject jo = new JSONObject(data);
						mVersionCode = jo.getInt("versionCode");
						mVersionName = jo.getString("versionName");
						mDesc = jo.getString("desc");
						mUrl = jo.getString("url");

						if (mVersionCode > getmVersionCode()) {
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = CODE_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = CODE_NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = CODE_JSON_ERROR;
				} finally {
					if (null != connection) {
						connection.disconnect();
					}

					/* 至少展示splash界面2秒钟 */
					long stopMill = System.currentTimeMillis();
					long durMill = startMill - startMill;
					if (durMill < 2000) {
						try {
							Thread.sleep(2000 - durMill);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					handler.sendMessage(msg);
				}

			}
		}.start();
	}

	public void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	public String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	public int getmVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public void copyBlackNumberData() {
		File dbDir = new File("/data/data/" + getPackageName() + "/databases");
		File dbFile = new File(dbDir, "blacknumber.db");

		BlackNumberDao mDao = BlackNumberDao.getInstance(this);
		if (dbFile.exists()) {
			return;
		} else {
			String path = getFilesDir().getAbsolutePath() + "/myblacknumber.db";
			LogUtils.d(this, "copyBlackNumberData "+path);

			SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.query("blacknumber", new String[] { "number",
					"mode" }, null, null, null, null, null);
			while(cursor.moveToNext()){
				String number = cursor.getString(cursor.getColumnIndex("number"));
				int mode = cursor.getInt(cursor.getColumnIndex("mode"));
				
				mDao.add(number, mode);
			}
			cursor.close();
			db.close();
		}

	}

	public void copyDb2Database(String dbName) {
		File dbDir = new File("/data/data/" + getPackageName() + "/databases");
		File dbFile = new File(dbDir, dbName);

		if (dbFile.exists()) {
			LogUtils.d(this, dbName + "已经存在");
			return;
		} else {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream in = null;
		FileOutputStream fo = null;

		AssetManager assetManager = getAssets();
		try {
			in = assetManager.open(dbName);
			fo = new FileOutputStream(dbFile, true);

			byte[] buffer = new byte[1024];
			int len = 0;

			while (-1 != (len = in.read(buffer))) {
				fo.write(buffer, 0, len);
			}
		} catch (IOException e) {
			LogUtils.d(this, "io exception");
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != fo) {
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		LogUtils.d(this, "拷贝数据库" + dbName + "成功!");

	}

	public void copyDb(String dbName) {

		File filesDir = getFilesDir();
		File dbFile = new File(filesDir, dbName);

		if (dbFile.exists()) {
			LogUtils.d(this, dbName + "已经存在");
			return;
		} else {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream in = null;
		FileOutputStream fo = null;

		AssetManager assetManager = getAssets();
		try {
			in = assetManager.open(dbName);
			fo = new FileOutputStream(dbFile, true);

			byte[] buffer = new byte[1024];
			int len = 0;

			while (-1 != (len = in.read(buffer))) {
				LogUtils.d(this, "len=" + len);
				fo.write(buffer, 0, len);
			}
		} catch (IOException e) {
			LogUtils.d(this, "io exception");
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != fo) {
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		LogUtils.d(this, "拷贝数据库" + dbName + "成功!");
	}

	public void installShortcut() {
		boolean isCreate = PrefUtils.getBoolean("is_short_cut", false, this);

		if (!isCreate) {
			Intent intent = new Intent();
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "我的手机卫士");
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
					.decodeResource(getResources(), R.drawable.home_apps));

			Intent actionIntent = new Intent();
			actionIntent.setAction("com.trx.mobilesafe.HOME");

			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);

			sendBroadcast(intent);

			System.out.println("enter shortcut");

			PrefUtils.putBoolean("is_short_cut", true, this);
		}
	}
}
