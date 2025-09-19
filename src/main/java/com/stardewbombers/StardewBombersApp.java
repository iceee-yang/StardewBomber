package com.stardewbombers;

import com.stardewbombers.client.MainMenuView;
import com.stardewbombers.ui.LoginRegisterView;
import com.stardewbombers.model.Player;
import javafx.application.Application;
import javafx.stage.Stage;

public class StardewBombersApp extends Application {
	
	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		// 先显示登录注册界面
		LoginRegisterView loginView = new LoginRegisterView(this::onLoginSuccess);
		loginView.show(stage);
	}

	private void onLoginSuccess(Player player) {
		// 登录成功后显示主菜单，传递用户信息
		System.out.println("登录成功，用户信息: " + (player != null ? player.getNickname() : "null"));
		MainMenuView mainMenu = new MainMenuView(primaryStage, player);
		mainMenu.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}