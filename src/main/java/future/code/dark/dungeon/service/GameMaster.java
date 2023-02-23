package future.code.dark.dungeon.service;

import future.code.dark.dungeon.config.Configuration;
import future.code.dark.dungeon.domen.Coin;
import future.code.dark.dungeon.domen.DynamicObject;
import future.code.dark.dungeon.domen.Enemy;
import future.code.dark.dungeon.domen.Exit;
import future.code.dark.dungeon.domen.GameObject;
import future.code.dark.dungeon.domen.Map;
import future.code.dark.dungeon.domen.Player;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static future.code.dark.dungeon.config.Configuration.*;

public class GameMaster {
    public boolean exitEnable = false;
    private Image victori = new ImageIcon(WINER_SPRITE).getImage();

    private static GameMaster instance;
    public long ostatok;
    private final Map map;
    private final List<GameObject> gameObjects;
    public  int shet;
    public static synchronized GameMaster getInstance() {
        if (instance == null) {
            instance = new GameMaster();
        }
        return instance;
    }

    private GameMaster() {
        try {
            this.map = new Map(Configuration.MAP_FILE_PATH);
            this.gameObjects = initGameObjects(map.getMap());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GameObject> initGameObjects(char[][] map) {
        List<GameObject> gameObjects = new ArrayList<>();
        Consumer<GameObject> addGameObject = gameObjects::add;
        Consumer<Enemy> addEnemy = enemy -> {if (ENEMIES_ACTIVE) gameObjects.add(enemy);};

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                switch (map[i][j]) {
                    case EXIT_CHARACTER -> addGameObject.accept(new Exit(j, i));
                    case COIN_CHARACTER -> addGameObject.accept(new Coin(j, i));
                    case ENEMY_CHARACTER -> addEnemy.accept(new Enemy(j, i));
                    case PLAYER_CHARACTER -> addGameObject.accept(new Player(j, i));
                }
            }
        }

        return gameObjects;
    }

    public void renderFrame(Graphics graphics) {

           getMap().render(graphics);
           getStaticObjects().forEach(gameObject -> gameObject.render(graphics));
           getEnemies().forEach(gameObject -> gameObject.render(graphics));
           getPlayer().render(graphics);
           graphics.setColor(Color.WHITE);
           graphics.drawString(getPlayer().toString(), 10, 20);
           graphics.drawString("Cобраные Coin: " + shet, 100, 20);
           graphics.drawString("Оставшися Coin: " + ostatok, 240, 20);
           if(getPlayer().getXPosition() == getExit().getXPosition() && getPlayer().getYPosition() == getExit().getYPosition() ){
               graphics.drawImage(victori, 0 ,0 , null);
           }
    }

    public Player getPlayer() {
        return (Player) gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Player)
                .findFirst()
                .orElseThrow();
    }
    public Exit getExit() {
        return (Exit) gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Exit)
                .findFirst()
                .orElseThrow();
    }

    private List<GameObject> getStaticObjects() {
        return gameObjects.stream()
                .filter(gameObject -> !(gameObject instanceof DynamicObject))
                .collect(Collectors.toList());
    }

    private List<Enemy> getEnemies() {
        return gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Enemy)
                .map(gameObject -> (Enemy) gameObject)
                .collect(Collectors.toList());
    }
    public List<Coin> getCoin() {
        return gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Coin)
                .map(gameObject -> (Coin) gameObject)
                .collect(Collectors.toList());
    }

    public Map getMap() {
        return map;
    }

    public void deleteCoin(int x,int y){
      if( this.gameObjects.removeIf(coin -> coin instanceof  Coin && coin.getXPosition() == x && coin.getYPosition() == y)){
          shet++;
      }

    }
    public void getKolwo(){
       ostatok= getCoin().size();
    }
    public boolean enabelExit(int x,int y){
        if(!(GameMaster.getInstance().getExit().getXPosition() == x && GameMaster.getInstance().getExit().getYPosition() == y)){
            return true;
        }
        if(ostatok == 0){

            return true;
        }
        return false;
    }
}
