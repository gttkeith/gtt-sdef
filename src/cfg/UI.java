package cfg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import game.*;

/* singleton UI manager class */
public class UI {
    // file paths and font sizes
    private static final String BUY_PANEL_PATH = "res/images/buypanel.png";
    private static final String STATUS_PANEL_PATH = "res/images/statuspanel.png";
    private static final String FONT_PATH = "res/fonts/DejaVuSans-Bold.ttf";
    private static final int BIG_FONT_SIZE = 48;
    private static final int SMALL_FONT_SIZE = 18;
    private static final int KEYBIND_FONT_SIZE = 15;

    // font and panel initialisation
    private final Font keybindFont = new Font(FONT_PATH, KEYBIND_FONT_SIZE);
    private final Font smallFont = new Font(FONT_PATH, SMALL_FONT_SIZE);
    private final Font bigFont = new Font(FONT_PATH, BIG_FONT_SIZE);
    private final Sprite buyPanel = new Sprite(BUY_PANEL_PATH, new Point(0,0),true);
    private final Sprite statusPanel = new Sprite(STATUS_PANEL_PATH, new Point(0, Window.getHeight()-new Image(STATUS_PANEL_PATH).getHeight()), true);
    private final List<Sprite> panels = new ArrayList<Sprite>(List.of(buyPanel, statusPanel));

    // buy panel layout configuration
    private Point towerIconsPosition = new Point(64, buyPanel.getHeight()/2-10);
    private double towerPriceBottomPadding = 12;
    private Colour towerPriceAffordable = new Colour(0,1,0);
    private Colour towerPriceUnaffordable = new Colour(1,0,0);
    private Colour towerActiveSelected = new Colour(0,1,0,0.5);
    private double towerIconPadding = 120;
    private Point keybindsPosition = new Point(Window.getWidth()-400, 22);
    private String keybindsText = "Key binds:\n\nS - Start Wave\nL - Increase Timescale\nK - Decrease Timescale";
    private Point moneyPosition = new Point(Window.getWidth()-200, 65);
    private String dollar = "$";
    
    // status panel layout configuration
    private double statusLeftPadding = 10;
    private double statusItemPadding = 250;
    private String waveTitle = "Wave";
    private String timescaleTitle = "Time Scale";
    private Colour speedupColour = new Colour(0,1,0);
    private String statusTitle = "Status";
    private String livesTitle = "Lives";
    
    // singleton initialisation
    private static UI instance = null; 
    private UI() {}
    public static UI getInstance() {
        if (instance == null) instance = new UI();
        return instance;
    }

    private List<TowerIcon> towerIcons = TowerIcon.populateTowerIcons(Tower.DATA, towerIconsPosition, towerIconPadding);

    /* mouse events */
    private TowerIcon selectedTowerIcon = towerIcons.get(0);
    private boolean selectionActive = false;
    private boolean mousePressed = false;

    // selection state getter
    public boolean getSelectionActive() {return selectionActive;}
    
    /**
     * manages mouse events and purchasing of towers
     *
     * @param input Bagel Input object.
     * @param lane the lane on which towers cannot be built on.
     */
    public void updateMouse (Input input, List<Point> lane) {
        if (Player.getInstance().isSuspended()) return;
        // if selection is active, check for left click events and enable selection if valid
        if (!selectionActive) {
            for (TowerIcon t : towerIcons) {
                if (input.isDown(MouseButtons.LEFT) & !mousePressed & t.isInClickArea(input.getMousePosition())) {
                    selectedTowerIcon = t;
                    selectionActive = Player.getInstance().canAfford(t.getData().cost);
                    break;
                }
            }
        // if selection is inactive, check tower placement and place if requested and valid
        } else {
            Sprite preview = new Sprite(selectedTowerIcon.getData().spritePath, input.getMousePosition());
            if (Tower.getValidPlacement(preview, panels, lane, selectedTowerIcon.getData().isAirSupport)) {
                if (input.isDown(MouseButtons.LEFT) & !mousePressed) {
                    if (Player.getInstance().spendIfEnough(selectedTowerIcon.getData().cost)) {
                        Tower.addTower(selectedTowerIcon.towerKey, input.getMousePosition());
                        selectionActive = false;
                    }
                } else {
                    if (selectedTowerIcon.getData().isAirSupport) {
                        if (Tower.getPlaneWillBeHorizontal()) preview.setRotation(Math.PI/2);
                        else preview.setRotation(Math.PI);
                    }
                    preview.draw();
                }
            }
            if (input.isDown(MouseButtons.RIGHT) & !mousePressed) selectionActive = false;
        }
        selectionActive = selectionActive & Player.getInstance().canAfford(selectedTowerIcon.getData().cost);
        mousePressed = (input.isDown(MouseButtons.LEFT) | input.isDown(MouseButtons.RIGHT));
    }
    
    public double getBuyPanelHeight() {return buyPanel.getHeight();}
    /**
     * draws the buy panel and all elements on it with provided info.
     *
     * @param money current player money.
     */
    public void drawBuy (int money) {
        buyPanel.draw();
        // keybinds
        keybindFont.drawString(keybindsText, keybindsPosition.x, keybindsPosition.y);
        // money
        bigFont.drawString(dollar+Integer.toString(money), moneyPosition.x, moneyPosition.y);
        // tower icons
        for (TowerIcon t : towerIcons)  {
            if (selectionActive & selectedTowerIcon.towerKey.equals(t.towerKey)) {
                t.icon.setBlend(towerActiveSelected);
                t.icon.setUseBlend(true);
            } else t.icon.setUseBlend(false);
            t.icon.draw();
            DrawOptions d = new DrawOptions();
            d.setBlendColour(money<t.getData().cost ? towerPriceUnaffordable : towerPriceAffordable);
            smallFont.drawString(dollar+Integer.toString(t.getData().cost), t.icon.getBoundingBoxAtPosition().left(), buyPanel.getHeight()-towerPriceBottomPadding, d);
        }
    }

    /* status panel */
    private double statusDrawCount;
    private double statusYStart = Window.getHeight() - (statusPanel.getHeight()-SMALL_FONT_SIZE);
    // use before each draw sequence
    private void statusDrawInit() {statusDrawCount=statusLeftPadding;}
    // draw without DrawOptions
    private void statusDrawColumn (String param, String s) {
        smallFont.drawString(param+": "+s, statusDrawCount, statusYStart);
        statusDrawCount+=statusItemPadding;
    }
    // draw with DrawOptions
    private void statusDrawColumn (String param, String s, DrawOptions d) {
        smallFont.drawString(param+": "+s, statusDrawCount, statusYStart, d);
        statusDrawCount+=statusItemPadding;
    }

    /**
     * draws the status panel and all elements on it with provided info.
     *
     * @param waveNum current wave number.
     * @param timescale the timescale.
     * @param status a String containing the status to be rendered.
     * @param lives current player lives.
     */
    public void drawStatus (int waveNum, double timescale, String status, int lives) {
        statusDrawInit();
        statusPanel.draw();
        statusDrawColumn(waveTitle, Integer.toString(waveNum));
        DrawOptions d = new DrawOptions();
        if (timescale>1.0) d.setBlendColour(speedupColour);
        statusDrawColumn(timescaleTitle, String.format("%.1f", timescale), d);
        statusDrawColumn(statusTitle, status);
        statusDrawColumn(livesTitle, Integer.toString(lives));
    }
}

/* represents a singular tower icon drawn on the buy panel.
   click detection logic included */
class TowerIcon {
    /**
     * generates a list of TowerIcon objects given a tower data map
     * icon positions are dynamically generated
     *
     * @param towerDataMap the tower data map to be parsed
     * @param firstIconPos the position of first icon
     * @param padding the horizontal space between each icon
     * @return a List of TowerIcon objects, parsed from the Map
     */
    static List<TowerIcon> populateTowerIcons (Map<String,TowerData> towerDataMap, Point firstIconPos, double padding) {
        Point currentIconPos = firstIconPos;
        List<TowerIcon> result = new LinkedList<TowerIcon>();
        for (Map.Entry<String,TowerData> tt : Tower.DATA.entrySet()) {
            Sprite nicon = new Sprite(tt.getValue().spritePath, currentIconPos);
            result.add(new TowerIcon(tt.getKey(), nicon));
            currentIconPos = new Point(currentIconPos.x+padding, currentIconPos.y);
        } return result;
    }

    final String towerKey;
    final Sprite icon;

    // constructor
    TowerIcon (String ntowerKey, Sprite nicon) {
        towerKey = ntowerKey;
        icon = nicon;
    }

    /**
     * is the Point in a specific TowerIcon's click area?
     *
     * @param p the Point in question.
     * @return whether the Point is in the TowerIcon's clickarea
     */
    boolean isInClickArea (Point p) {
        double xLow = icon.getBoundingBoxAtPosition().left();
        double xHi = icon.getBoundingBoxAtPosition().right();
        double yLow = icon.getBoundingBoxAtPosition().top();
        double yHi = icon.getBoundingBoxAtPosition().bottom();
        return (xLow<p.x & p.x<xHi & yLow<p.y & p.y<yHi);
    }

    TowerData getData() {return Tower.DATA.get(towerKey);}
}