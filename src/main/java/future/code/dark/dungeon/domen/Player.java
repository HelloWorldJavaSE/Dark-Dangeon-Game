package future.code.dark.dungeon.domen;

import future.code.dark.dungeon.GameFrame;
import future.code.dark.dungeon.config.Configuration;
import future.code.dark.dungeon.service.GameMaster;

public class Player extends DynamicObject {
    private static final int stepSize = 1;

    public Player(int xPosition, int yPosition) {
        super(xPosition, yPosition, Configuration.PLAYER_SPRITE);
    }

    public void move(Direction direction) {
        super.move(direction, stepSize);
        GameMaster.getInstance().getKolwo();
        if( GameMaster.getInstance().getCoin().stream().anyMatch(coin -> this.collision(coin))){
           GameMaster.getInstance().deleteCoin(getXPosition(),getYPosition());
           GameMaster.getInstance().ostatok--;
        }
    }

    @Override
    public String toString() {
        return "Player{[" + xPosition + ":" + yPosition + "]}";
    }
}
