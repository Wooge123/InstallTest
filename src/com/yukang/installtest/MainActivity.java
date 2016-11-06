package com.yukang.installtest;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView apkPathText;
	String apkPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		apkPathText = (TextView) findViewById(R.id.apkPathText);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			apkPath = data.getStringExtra("apk_path");
			apkPathText.setText(apkPath);
		}
	}

	public void onChooseApkFile(View view) {
		Intent intent = new Intent(this, FileExplorerActivity.class);
		startActivityForResult(intent, 0);
	}

	public void onSilentInstall(View view) {
		if (!isRoot()) {
			Toast.makeText(this, "没有ROOT权限，不能使用静默安装", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(apkPath)) {
			Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
			return;
		}
		final Button button = (Button) view;
		button.setText("安装中");
		new Thread(new Runnable() {

			@Override
			public void run() {
				SilentInstall installHelper = new SilentInstall();
				final boolean result = installHelper.install(apkPath);
				runOnUiThread(new Runnable() {
					public void run() {
						if (result) {
							Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
						}
						button.setText("静默安装");
					}
				});
			}
		}).start();
	}

	public void onForwardToAccessibility(View view) {
		Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivity(intent);
	}

	public void onSmartInstall(View view) {
		if (TextUtils.isEmpty(apkPath)) {
			Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.fromFile(new File(apkPath));
		Intent localIntent = new Intent(Intent.ACTION_VIEW);
		localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(localIntent);
	}

	private boolean isRoot() {
		boolean bool = false;
		try {
			bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}
}
