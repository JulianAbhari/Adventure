package com.Julian.game.entities;

import java.util.Random;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;
import com.Julian.game.level.OpenSimplexNoise;

public class Raptor extends Mob {

	private int color = Colors.get(-1, 134, 444, 555);
	private int scale = 1;
	protected boolean isSwimming = false;
	private int tickCount = 0;
	public int xRaptor;
	public int yRaptor;
	public boolean isInRange = false;

	Random ran = new Random();
	OpenSimplexNoise noise = new OpenSimplexNoise();

	public Raptor(Level level, String name, int x, int y, int speed) {
		super(level, "Raptor", x, y, 1);
		xRaptor = x;
		yRaptor = y;
	}

	double xPerlinValue;
	double yPerlinValue;

	@Override
	public void tick() {
		int xa = 0;
		int ya = 0;
		int range = 32;
		xRaptor = this.x;
		yRaptor = this.y;

		// Is In Range method
		if (!(tickCount % 60 < 15)) {
			if ((Math.abs(Player.staticXPos - xRaptor)) <= range && (Math.abs(Player.staticYPos - yRaptor)) <= range) {
				isInRange = true;
				followThePlayer(xa, ya);
			} else {
				isInRange = false;
			}
		}

		// If the raptor is not in range with the Player, then perlinNoise is activiated
		if (!isInRange) {
			xPerlinValue = noise.eval(this.tickCount / 24, this.tickCount / 24);

			if (xPerlinValue >= 0 && xPerlinValue < 0.2675) {
				xa -= 1;
			}

			if (xPerlinValue >= 0.2675) {
				xa += 1;
			}

			yPerlinValue = noise.eval(this.tickCount / 21, this.tickCount / 21);

			if (yPerlinValue >= 0 && yPerlinValue < 0.2675) {
				ya -= 1;
			}

			if (yPerlinValue >= 0.2675) {
				ya += 1;
			}
		}

		if (xa != 0 || ya != 0) {
			move(xa, ya);
			isMoving = true;

		} else {
			isMoving = false;
		}
		if (level.getTile(this.x >> 3, this.y >> 3).getId() == 4) {
			isSwimming = true;
		}
		if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 4) {
			isSwimming = false;
		}

		tickCount++;
	}

	@Override
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 23;
		// int walkingSpeed = 4;
		int modifier = 8 * scale;
		int xOffset = x - modifier / 2;
		int yOffset = y - modifier / 2 - 4;

		if (isSwimming) {
			int waterColor = 0; // or waterSplashColor
			yOffset += 4;

			if (tickCount % 60 < 15) {
				waterColor = Colors.get(-1, -1, 225, -1);
			} else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
				waterColor = Colors.get(-1, 225, 115, -1);
				yOffset -= 1;
			} else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
				waterColor = Colors.get(-1, 115, -1, 225);
			} else {
				waterColor = Colors.get(-1, 225, 115, -1);
				yOffset -= 1;
			}
			// water splash render
			screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColor, false, false, 1);
			screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColor, true, false, 1);

			// top of raptor
			screen.render(xOffset - modifier, yOffset, xTile + yTile * 32, color, false, false, scale);
			screen.render(xOffset, yOffset, (xTile + 1) + yTile * 32, color, false, false, scale);
			screen.render(xOffset + modifier, yOffset, (xTile + 2) + yTile * 32, color, false, false, scale);
		}

		if (!isSwimming) {
			screen.render(xOffset - modifier, yOffset, xTile + yTile * 32, color, false, false, scale);
			screen.render(xOffset, yOffset, (xTile + 1) + yTile * 32, color, false, false, scale);
			screen.render(xOffset + modifier, yOffset, (xTile + 2) + yTile * 32, color, false, false, scale);

			screen.render(xOffset - modifier, yOffset + modifier, xTile + (yTile + 1) * 32, color, false, false, scale);
			screen.render(xOffset, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, color, false, false, scale);
			screen.render(xOffset + modifier, yOffset + modifier, (xTile + 2) + (yTile + 1) * 32, color, false, false,
					scale);
		}
	}

	public void followThePlayer(int xa, int ya) {
		if (Player.staticXPos > xRaptor) {
			xa += 1;
			move(xa, 0);
		}
		if (Player.staticYPos > yRaptor) {
			ya += 1;
			move(0, ya);
		}
		if (Player.staticXPos < xRaptor) {
			xa -= 1;
			move(xa, 0);
		}
		if (Player.staticYPos < yRaptor) {
			ya -= 1;
			move(0, ya);
		}
	}

	@Override
	public boolean hasCollided(int xa, int ya) {
		int xMin = 1;
		int xMax = 7;
		int yMin = 3;
		int yMax = 7;
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				return true;
			}
		}
		return false;
	}

}
