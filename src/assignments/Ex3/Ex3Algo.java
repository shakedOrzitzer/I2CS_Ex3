package assignments.Ex3;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    private int _count;
    //state machine: pink-go to the nearest pink. black-run away from ghosts. white-chase ghosts. green-go to nearest green
    private static final int PINK = 0, BLACK = -1, WHITE = -2, GREEN = -3;
    public Ex3Algo() {
        _count = 0;
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        return null;
    }

    @Override
    /**
     * This ia the main method - that you should design, implement and test.
     */
    public int move(PacmanGame game) {
        GhostCL[] ghosts = game.getGhosts(0);
        if (_count == 0 || _count == 300) {
            int code = 0;
            int[][] board = game.getGame(0);
            printBoard(board);
            int blue = Game.getIntColor(Color.BLUE, code);
            int pink = Game.getIntColor(Color.PINK, code);
            int black = Game.getIntColor(Color.BLACK, code);
            int green = Game.getIntColor(Color.GREEN, code);
            System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
            String pos = game.getPos(code).toString();
            System.out.println("Pacman coordinate: " + pos);
            ghosts = game.getGhosts(code);
            printGhosts(ghosts);
            int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
        }

        int blue = Game.getIntColor(Color.BLUE, 0);
        int pink = Game.getIntColor(Color.PINK, 0);
        int black = Game.getIntColor(Color.BLACK, 0);
        int green = Game.getIntColor(Color.GREEN, 0);
        int[][] board = game.getGame(0);

        Map2D map = new Map(board);
        Pixel2D pacmanPos = getPacmanPos(game);
        Map2D dists = map.allDistance(pacmanPos, blue);
        int minDistInd = Integer.MAX_VALUE;
        for (int i = ghosts.length - 1; i >= 0; i--) {
            String[] t = ghosts[i].getPos(i).split(",");
            int gX = Integer.parseInt(t[0]);
            int gY = Integer.parseInt(t[1]);
            Pixel2D gInd = new Index2D(gX, gY);
            if (map.shortestPath(pacmanPos, gInd, blue).length <= minDistInd) {
                minDistInd = i;
            }
        }
        Pixel2D closestGreen = closestGreen(map, dists);
        Pixel2D closestPink = closestPink(map, dists);
        _count++;
        int state = getState(map, ghosts, pacmanPos);
        if (state != PINK)
            System.out.println("State: " + state);
        if (state == PINK)
            return moveTo(pacmanPos, closestPink, map);
        if (state == BLACK)
            return runAway(pacmanPos, map, ghosts);
        if (state == WHITE) {
            return chaseGhost(pacmanPos, ghosts, map);
        }
        if (state == GREEN)
            return moveTo(pacmanPos, closestGreen, map);
        else return randomDir();
    }
    public static Index2D getPacmanPos (PacmanGame game){
        String pos = game.getPos(0).toString();
        String [] p = pos.split(",");
        int pacmanX = Integer.parseInt(p[0]);
        int pacmanY = Integer.parseInt(p[1]);

        return new Index2D(pacmanX, pacmanY);
    }

    public static Pixel2D closestPink(Map2D map, Map2D dists) {
        Pixel2D ans = null;
        int pink = Game.getIntColor(Color.PINK, 0);
        int minDist = Integer.MAX_VALUE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == pink) {
                    if (minDist != -1 && minDist > dists.getPixel(x, y)) {
                        minDist = dists.getPixel(x, y);
                        ans = new Index2D(x, y);
                    }
                }
            }
        }
        return ans;
    }

    public static Pixel2D closestGreen(Map2D map, Map2D dists) {
        Pixel2D ans = null;
        int green = Game.getIntColor(Color.GREEN, 0);
        int minDist = Integer.MAX_VALUE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == green) {
                    if (minDist != -1 && minDist > dists.getPixel(x, y)) {
                        minDist = dists.getPixel(x, y);
                        ans = new Index2D(x, y);
                    }
                }
            }
        }
        return ans;
    }

    private static void printBoard(int[][] b) {
        for (int y = 0; y < b[0].length; y++) {
            for (int x = 0; x < b.length; x++) {
                int v = b[x][y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }
    }

    private static void printGhosts(GhostCL[] gs) {
        for (int i = 0; i < gs.length; i++) {
            GhostCL g = gs[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private static int randomDir() {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int ind = (int) (Math.random() * dirs.length);
        return dirs[ind];
    }

    /**
     * move toward a chosen pixel
     * @param pos - pacman position
     * @param target - target pixel
     * @param map - game map
     * @return - the direction pacman has to move to
     */
    private static int moveTo(Pixel2D pos, Pixel2D target, Map2D map) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        Pixel2D[] path = map.shortestPath(pos, target, blue);

        if (path == null || path.length < 2) return randomDir();

        Pixel2D next = path[1];
        int x1 = pos.getX(), y1 = pos.getY();
        int x2 = next.getX(), y2 = next.getY();

        // בדיקת ציר X (כולל מעבר ציקלי)
        if (x2 != x1) {
            if (x2 > x1) {
                // אם המרחק גדול מ-1, זה אומר שדילגנו מהקצה השמאלי לימני דרך המנהרה
                return (x2 - x1 > 1) ? Game.LEFT : Game.RIGHT;
            } else {
                // אם המרחק גדול מ-1, זה אומר שדילגנו מהקצה הימני לשמאלי
                return (x1 - x2 > 1) ? Game.RIGHT : Game.LEFT;
            }
        }

        // בדיקת ציר Y (כולל מעבר ציקלי)
        if (y2 != y1) {
            if (y2 > y1) {
                // אם ה-Y גדל ביותר מ-1, עברנו מלמטה למעלה (או הפוך, תלוי במערכת הצירים)
                return (y2 - y1 > 1) ? Game.DOWN : Game.UP;
            } else {
                return (y1 - y2 > 1) ? Game.UP : Game.DOWN;
            }
        }

        return randomDir();
    }

    /**
     * gets pacMan pos and array of ghosts
     * goes to the closest pixel furthest from the closest ghost
     * @param pPos
     * @return
     */
    private static int runAway(Pixel2D pPos, Map2D map, GhostCL[] ghosts) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int pX = pPos.getX(), pY = pPos.getY();
        int w = map.getWidth(), h = map.getHeight();

        int closestIdx = getClosestGhostIndex(ghosts, pPos, map);
        GhostCL cGhost = ghosts[closestIdx];
        Pixel2D gPos = getGhostPos(cGhost);
        if (gPos.getX() == 11 && gPos.getY() == 11) { // so pacman doesn't get stuck
            return -1;
        }
        Map2D distsFromGhost = map.allDistance(gPos, blue);
        int bestDir = -1;
        int maxDist = -1;
        int[] dirCodes = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int[] dx = {0, -1, 0, 1};
        int[] dy = {1, 0, -1, 0};

        for (int i = 0; i < 4; i++) {
            int nextX = (pX + dx[i] + w) % w;
            int nextY = (pY + dy[i] + h) % h;
            if (map.getPixel(nextX, nextY) == blue) continue;
            int dist = distsFromGhost.getPixel(nextX, nextY);
            if (dist > maxDist) {
                maxDist = dist;
                bestDir = dirCodes[i];
            }
        }
        return (bestDir != -1) ? bestDir : randomDir();
    }

    /**
     * computes how many steps is the closest ghost to pacMan
     *
     * @param gs
     * @param pPos
     * @param map
     * @return
     */
    private static int getClosestGhostDist(GhostCL[] gs, Pixel2D pPos, Map2D map) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int ind = getClosestGhostIndex(gs, pPos, map);
        String s = gs[ind].getPos(0);
        int gX = Integer.parseInt(s.split(",")[0]);
        int gY = Integer.parseInt(s.split(",")[1]);
        Pixel2D gP = new Index2D(gX, gY);
        int minDist = map.shortestPath(pPos, gP, blue).length ;
        return minDist;
    }

    /**
     * @param gs - array of ghosts
     * @param pPos - pacman position
     * @param map - map of game
     * @return the index in the array, of the closest ghost
     */
    private static int getClosestGhostIndex(GhostCL[] gs, Pixel2D pPos, Map2D map) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int index = Integer.MAX_VALUE;
        GhostCL closeGhost = getClosestGhostAsGhost(gs, pPos, map);
        for (int i = 0; i < gs.length; i++) {
            if (gs[i].equals(closeGhost)){
                return i;
            }
        }
        return index;
    }

    /**
     * @param ghosts - array of ghosts
     * @param pacmanPos - pacman position
     * @param gameMap - map of game
     * @return - the GhostCL ghost closest
     */
    public static GhostCL getClosestGhostAsGhost(GhostCL[] ghosts, Pixel2D pacmanPos,Map2D gameMap){
        GhostCL finalGhostPos = ghosts[0];
        int blue = Game.getIntColor(Color.BLUE, 0);
        int dis = -1;
        for(GhostCL g: ghosts){
            Pixel2D currentGhost = getGhostPos(g);
            int currentDis = gameMap.shortestPath(pacmanPos, currentGhost, blue).length;

            if (dis == -1 || currentDis < dis){
                finalGhostPos = g;
                dis = currentDis;
            }
        }
        return finalGhostPos;
    }

    /**
     *
     * @param ghost - ghost as GhostCL
     * @return the pixel position in the map of the ghost
     */
    public static Pixel2D getGhostPos(GhostCL ghost){
        String pos = ghost.getPos(0).toString();
        String [] p = pos.split(",");
        int ghostY = Integer.parseInt(p[1]);
        int ghostX = Integer.parseInt(p[0]);

        return new Index2D(ghostX, ghostY);
    }

    /**
     * @param pPos - pacman position
     * @param ghosts - array of ghosts
     * @param map - game map
     * @return the func moveTo, towards the ghost
     */
    private int chaseGhost(Pixel2D pPos, GhostCL[] ghosts, Map2D map) {
        int indx = getClosestGhostIndex(ghosts, pPos, map);
        return moveTo(pPos, getGhostPos(ghosts[indx]), map);
    }

    /** if a ghost is closer than 3 steps-run away and time eatable<=0: BLACK
     * if a ghost's time as eatable >0.5-chase ghost: WHITE
     * if green is 2 or less steps from me AND closest ghost is >2 steps away-go to green: GREEN
     * else; PINK
     */
    private static int getState(Map2D map, GhostCL[] ghosts, Pixel2D pPos) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int pink = Game.getIntColor(Color.PINK, 0);
        int black = Game.getIntColor(Color.BLACK, 0);
        int green = Game.getIntColor(Color.GREEN, 0);

        Map2D dists = map.allDistance(pPos, blue);
        int closestGhostIndex = getClosestGhostIndex(ghosts, pPos, map);
        System.out.println(ghosts[closestGhostIndex].getPos(0));
        System.out.println(pPos.toString());

        if ((getClosestGhostDist(ghosts, pPos, map) <= 8 && ghosts[closestGhostIndex].remainTimeAsEatable(0) <= 0)) {
            if (runAway(pPos, map, ghosts) != -1) return BLACK;}

        Pixel2D closestGreen = closestGreen(map, dists);
        if (dists.getPixel(closestGreen) <= 2 && closestGreen!=null) {
            return GREEN;
        }
        if (getClosestGhostDist(ghosts,pPos,map) <= 6 && ghosts[closestGhostIndex].remainTimeAsEatable(0) > 0.5) {
            return WHITE;
        }

        return PINK;
    }
}