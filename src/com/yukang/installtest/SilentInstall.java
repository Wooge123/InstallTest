package com.yukang.installtest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.util.Log;

public class SilentInstall {

	/**
	 * 执行具体的静默安装逻辑，需要手机ROOT
	 */
	public boolean install(String apkPath) {
		boolean result = false;
		DataOutputStream dataOutputStream = null;
		BufferedReader reader = null;
		try {
			// 申请su权限
			Process process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			// 执行pm install命令
			String command = "pm install -r " + apkPath + "\n";
			dataOutputStream.write(command.getBytes(Charset.forName("utf8")));
			dataOutputStream.flush();
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String msg = "";
			String line;
			// 读取命令的执行结果
			while ((line = reader.readLine()) != null) {
				msg += line;
			}
			Log.d("TAG", "install msg is " + msg);
			// 如果执行结果中包含Failure字样就认为是安装失败，否则认为安装成功
			if (!msg.contains("Failure")) {
				result = true;
			}
		} catch (Exception e) {
			Log.e("TAG", e.getMessage(), e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
			} catch (IOException e) {
				Log.e("TAG", e.getMessage(), e);
			}
		}
		return result;
	}
}
