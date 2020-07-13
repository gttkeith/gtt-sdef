# gtt-sdefense
Simple tower defense game, created using the BAGEL graphics manipulation library

### Dependencies

- [LightWeight Java Game Library](https://www.lwjgl.org/)
- Eleanor McMurtry's BAGEL (Basic Academic Graphical Engine Library)

### Configuration

Configuration information and resources are stored in `/res`:

```
>	res
|	>	fonts
|	|		master_font.ttf
|	>	images
|	|		buypanel.png
|	|		statuspanel.png
|	>	levels
|	|		1.png
|	|		1.tmx
|	|		waves1.txt
|		enemies.csv
|		levels.csv
|		towers.csv
```

Additional resources can also be placed in the relevant folders.

The schema for each config CSV is found in `/src/cfg` and templates are also available in this repo. Towers, enemies and levels with custom art, attributes and events can be designed solely through these configuration files.

### Description

A simple tower defense game that supports multiple levels, each level having its own map, path (in .tmx format), and enemy wave events.

The player starts with a preset amount of money, and receives a reward for completing each wave. Completing all waves in a level advances the level. If the player completes all wave events in all levels, the game is won.

**Towers**

Towers are deployable by the player. They cost money and come in two types:

- **Standard** - static tower, target-locked projectile attack with fixed/variable damage and speed
- **Airplane** - temporary tower that moves along an axis, causing damage at fixed/variable intervals along a flight path

At the end of each level, all towers are removed from the map and money is reset to the predefined initial amount.

**Enemies**

Enemies spawn, follow a set path, and are capable of spawning children when they die.

If an enemy reaches the end of the path, a number of lives (according to the recorded stats of the monster) are subtracted from the player; if lives reach zero, the player loses.
