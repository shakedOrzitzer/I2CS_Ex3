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
public class Ex3Algo implements PacManAlgo{
	private int _count;
	public Ex3Algo() {_count=0;}
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
		if(_count==0 || _count==300) {
			int code = 0;
			int[][] board = game.getGame(0);
			printBoard(board);
			int blue = Game.getIntColor(Color.BLUE, code);
			int pink = Game.getIntColor(Color.PINK, code);
			int black = Game.getIntColor(Color.BLACK, code);
			int green = Game.getIntColor(Color.GREEN, code);
			System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
			String pos = game.getPos(code).toString();
			System.out.println("Pacman coordinate: "+pos);
			GhostCL[] ghosts = game.getGhosts(code);
			printGhosts(ghosts);
			int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
		}
        int blue = Game.getIntColor(Color.BLUE, 0);
        int[][] board = game.getGame(0);
        String pos = game.getPos(0).toString();
        String[] s= pos.split(",");
        int x= Integer.parseInt(s[0]);
        int y= Integer.parseInt(s[1]);
        Map2D map= new Map (board);
        Pixel2D xy=new Index2D(x,y);
        Map2D dists= map.allDistance(xy,blue);
        Pixel2D closestPink = closestPink(map,dists);
		_count++;
		int dir = moveTo(xy,closestPink,map);
//        int dir=randomDir();
		return dir;
	}
    public static Pixel2D closestPink(Map2D map, Map2D dists) {
        Pixel2D ans=null;
        int pink=Game.getIntColor(Color.PINK, 0);
        int minDist=-20;
        for (int x=0;x<map.getWidth();x++){
            for (int y=0;y<map.getHeight();y++){
                if(map.getPixel(x,y)==pink){
                    if (minDist==-20 || minDist>dists.getPixel(x,y)){
                        minDist=dists.getPixel(x,y);
                        ans= new Index2D(x,y);
                    }
                }
            }
        }
        return ans;
    }

    public static Pixel2D closestGreen(Map2D map, Map2D dists) {
        Pixel2D ans=null;
        int green = Game.getIntColor(Color.GREEN, 0);
        int minDist= -20;
        for (int x=0;x<map.getWidth();x++){
            for (int y=0;y<map.getHeight();y++){
                if(map.getPixel(x,y)==green){
                    if (minDist==-20 || minDist>dists.getPixel(x,y)){
                        minDist=dists.getPixel(x,y);
                        ans= new Index2D(x,y);
                    }
                }
            }
        }
        return ans;
    }

	private static void printBoard(int[][] b) {
		for(int y =0;y<b[0].length;y++){
			for(int x =0;x<b.length;x++){
				int v = b[x][y];
				System.out.print(v+"\t");
			}
			System.out.println();
		}
	}
	private static void printGhosts(GhostCL[] gs) {
		for(int i=0;i<gs.length;i++){
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
	}
	private static int randomDir() {
		int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
		int ind = (int)(Math.random()*dirs.length);
		return dirs[ind];
	}

    private static int moveTo (Pixel2D pos, Pixel2D target,Map2D map){
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int blue = Game.getIntColor(Color.BLUE, 0);
        Pixel2D[] arr= map.shortestPath(pos,target,blue);
        Pixel2D next=arr[1];
        if (map.isCyclic()) {
            if(pos.getY()+1==target.getY()){return dirs[0];}
            if(pos.getX()+1==target.getX()){return dirs[1];}
            if(pos.getY()-1==target.getY()){return dirs[2];}
            else return dirs[3];
        }
        if((next.getY()+1)%map.getHeight() ==pos.getY())
            return dirs[0];
        if((next.getX()+1)%map.getWidth() ==pos.getX())
            return dirs[1];
        if((next.getY()+1)%map.getHeight() ==pos.getY())
            return dirs[2];
//        if((next.getX()+1)%map.getWidth() ==pos.getX())
        else return dirs[3];
//        return dirs[3];
//        return randomDir();
    }
}