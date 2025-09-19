package com.stardewbombers.client.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import com.stardewbombers.component.PlayerComponent;

public class PlayerController {
	private final PlayerComponent playerComponent;
	private final Set<KeyCode> pressed = new HashSet<>();

	public PlayerController(PlayerComponent playerComponent) {
		this.playerComponent = playerComponent;
	}

	public void bindScene(Scene scene) {
		EventHandler<KeyEvent> keyPressed = e -> pressed.add(e.getCode());
		EventHandler<KeyEvent> keyReleased = e -> pressed.remove(e.getCode());
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, keyReleased);
	}

	public void update(long nowMs) {
		if (pressed.contains(KeyCode.W) || pressed.contains(KeyCode.UP)) playerComponent.moveUp();
		if (pressed.contains(KeyCode.S) || pressed.contains(KeyCode.DOWN)) playerComponent.moveDown();
		if (pressed.contains(KeyCode.A) || pressed.contains(KeyCode.LEFT)) playerComponent.moveLeft();
		if (pressed.contains(KeyCode.D) || pressed.contains(KeyCode.RIGHT)) playerComponent.moveRight();
		if (pressed.contains(KeyCode.SPACE)) playerComponent.placeBomb(nowMs);
		playerComponent.tick(nowMs);
	}
}
