package com.wenruisong.basestationmap.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wenruisong.basestationmap.group.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserProxy {

	public static final String TAG = "UserProxy";

	private Context mContext;

	public UserProxy(Context context){
		this.mContext = context;
	}

	public void signUp(final String email, String password){
		final User user = new User();
		String userNameSplit[] = email.split("@");
		user.setUsername(userNameSplit[0]);
		user.setPassword(password);
		user.setEmail(email);
		user.signUp(mContext, new SaveListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(signUpLister != null){
							user.requestEmailVerify(mContext, email, new EmailVerifyListener() {
								@Override
								public void onSuccess() {
									signUpLister.onSignUpSuccess();
							Toast.makeText(mContext.getApplicationContext(), "注册成功，请登录注册邮箱完成验证", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onFailure(int i, String s) {
							Log.d(TAG,"requestEmailVerify failed!");
						}
					});

				}else{
					Log.d(TAG,"signup listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if(signUpLister != null){
					signUpLister.onSignUpFailure(msg);
				}else{
					Log.d(TAG,"signup listener is null,you must set one!");
				}
			}
		});
	}

	public interface ISignUpListener{
		void onSignUpSuccess();
		void onSignUpFailure(String msg);
	}
	private ISignUpListener signUpLister;
	public void setOnSignUpListener(ISignUpListener signUpLister){
		this.signUpLister = signUpLister;
	}


	public User getCurrentUser(){
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if(user != null){
			Log.d("T^T", user.getObjectId() + "-"
							+ user.getUsername() + "-"
							+ user.getSessionToken() + "-"
							+ user.getCreatedAt() + "-"
							+ user.getUpdatedAt() + "-"
			);
			return user;
		}else{
			Log.d(TAG,"错误");
		}
		return null;
	}

	public void login(String userName,String password){
		final BmobUser user = new BmobUser();
		user.setUsername(userName);
		user.setPassword(password);
		user.login(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(loginListener != null){
					loginListener.onLoginSuccess();
				}else{
					Log.i(TAG, "login listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if(loginListener != null){
					loginListener.onLoginFailure(msg);
				}else{
					Log.i(TAG,"login listener is null,you must set one!");
				}
			}
		});
	}

	public interface ILoginListener{
		void onLoginSuccess();
		void onLoginFailure(String msg);
	}
	private ILoginListener loginListener;
	public void setOnLoginListener(ILoginListener loginListener){
		this.loginListener  = loginListener;
	}

	public void logout(){
		BmobUser.logOut(mContext);
		Log.i(TAG, "logout result:"+(null == getCurrentUser()));
	}

	public void update(String... args){
		User user = getCurrentUser();
		user.setUsername(args[0]);
		user.setEmail(args[1]);
		user.setPassword(args[2]);
		//...
		user.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(updateListener != null){
					updateListener.onUpdateSuccess();
				}else{
					Log.i(TAG,"update listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if(updateListener != null){
					updateListener.onUpdateFailure(msg);
				}else{
					Log.i(TAG,"update listener is null,you must set one!");
				}
			}
		});
	}

	public interface IUpdateListener{
		void onUpdateSuccess();
		void onUpdateFailure(String msg);
	}
	private IUpdateListener updateListener;
	public void setOnUpdateListener(IUpdateListener updateListener){
		this.updateListener = updateListener;
	}

	public void resetPassword(String email){
		BmobUser.resetPasswordByEmail(mContext, email, new ResetPasswordByEmailListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(resetPasswordListener != null){
					resetPasswordListener.onResetSuccess();
				}else{
					Log.i(TAG,"reset listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if(resetPasswordListener != null){
					resetPasswordListener.onResetFailure(msg);
				}else{
					Log.i(TAG,"reset listener is null,you must set one!");
				}
			}
		});
	}
	public interface IResetPasswordListener{
		void onResetSuccess();
		void onResetFailure(String msg);
	}
	private IResetPasswordListener resetPasswordListener;
	public void setOnResetPasswordListener(IResetPasswordListener resetPasswordListener){
		this.resetPasswordListener = resetPasswordListener;
	}

}
