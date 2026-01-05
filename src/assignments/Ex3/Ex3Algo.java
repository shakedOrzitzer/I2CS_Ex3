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
        GhostCL[] ghosts = game.getGhosts(0);
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
			ghosts = game.getGhosts(code);
			printGhosts(ghosts);
			int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
		}

        int blue = Game.getIntColor(Color.BLUE, 0);
        int pink = Game.getIntColor(Color.PINK, 0);
        int black = Game.getIntColor(Color.BLACK, 0);
        int green = Game.getIntColor(Color.GREEN, 0);
        int[][] board = game.getGame(0);
        String pos = game.getPos(0).toString();
        String[] s= pos.split(",");
        int x= Integer.parseInt(s[0]);
        int y= Integer.parseInt(s[1]);


        Map2D map= new Map (board);
        Pixel2D xy=new Index2D(x,y);
        Map2D dists= map.allDistance(xy,blue);
        int minDistInd= Integer.MAX_VALUE;
        for (int i=ghosts.length-1;i>=0;i--) {
            String[] t=ghosts[i].getPos(i).split(",");
            int gX=Integer.parseInt(t[0]);
            int gY=Integer.parseInt(t[1]);
            Pixel2D gInd= new Index2D(gX,gY);
            if(map.shortestPath(xy,gInd,blue).length<=minDistInd) {
                minDistInd=i;
            }
        }
        int closestGhostIndex=getClosestGhostIndex(ghosts,xy,map);
        if((getClosestGhostDist(ghosts,xy,map)<=6 && ghosts[closestGhostIndex].remainTimeAsEatable(0)<=0))
            {return runAway(xy,map,ghosts);}
        Pixel2D closestGreen = closestGreen(map,dists);
        if (dists.getPixel(closestGreen)<=2) {return moveTo(xy,closestGreen,map);}
//        if( closestGhostIndex<=3 && ghosts[closestGhostIndex].remainTimeAsEatable(0)>0 ) {
//            int gX= Integer.parseInt( ghosts[closestGhostIndex].getPos(0).split(",")[0]);
//            int gY= Integer.parseInt( ghosts[closestGhostIndex].getPos(0).split(",")[1]);
//            Pixel2D gPos= new Index2D(gX,gY);
//            return chaseGhost(xy, gPos,map);}
        Pixel2D closestPink = closestPink(map,dists);
        _count++;
		return moveTo(xy,closestPink,map);
	}
    public static Pixel2D closestPink(Map2D map, Map2D dists) {
        Pixel2D ans=null;
        int pink=Game.getIntColor(Color.PINK, 0);
        int minDist=Integer.MAX_VALUE;
        for (int x=0;x<map.getWidth();x++){
            for (int y=0;y<map.getHeight();y++){
                if(map.getPixel(x,y)==pink){
                    if (minDist!=-1 && minDist>dists.getPixel(x,y)){
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
        int green = Game.getIntColor(Color.GREEN,0);
        int minDist=Integer.MAX_VALUE;
        for (int x=0;x<map.getWidth();x++){
            for (int y=0;y<map.getHeight();y++){
                if(map.getPixel(x,y)==green){
                    if (minDist!=-1 && minDist>dists.getPixel(x,y)){
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
        Pixel2D next;
        if(arr!=null&&arr.length>1){next=arr[1];}
        else return randomDir();

        int x=pos.getX();
        int y=pos.getY();
        int w=map.getWidth();
        int h=map.getHeight();
        //up
        if((y+1)%h==next.getY()){return dirs[0];}
        //left
        if((x-1+w)%w==next.getX()){return dirs[1];}
        //down
        if((y-1+h)%h==next.getY()){return dirs[2];}
        //right
        if((x+1+w)%w==next.getX()){return dirs[3];}

        return randomDir();
    }

    /**
     * gets pacMan pos and dists map of distances from the closest ghost
     * @param pPos
     * @return
     */
    private static int runAway(Pixel2D pPos, Map2D map, GhostCL[] ghosts){
        int blue = Game.getIntColor(Color.BLUE, 0);
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int pX=pPos.getX(), pY=pPos.getY(),w=map.getWidth(),h=map.getHeight();
        int distFromG = getClosestGhostDist(ghosts,pPos,map);
        GhostCL cGhost= ghosts[getClosestGhostIndex(ghosts,pPos,map)];
        int gX= Integer.parseInt( cGhost.getPos(0).split(",")[0]);
        int gY= Integer.parseInt( cGhost.getPos(0).split(",")[1]);
        if (gX==11 && gY==11){
            Pixel2D p= closestPink(map,map.allDistance(pPos,blue));
            return moveTo(pPos,p,map);}
        Pixel2D cInd= new Index2D(gX,gY);
        Map2D distsFromG =map.allDistance(cInd,blue);
        //up
        if (distsFromG.getPixel(pPos)<=((pY+1)%h)+1){return dirs[0]; }
        //left
        if (distsFromG.getPixel(pPos)<=((pX-1+w)%w)+1){return dirs[1];}
        //down
        if (distsFromG.getPixel(pPos)<=((pY-1+h)%h)+1){return dirs[2];}
        //right
        if(distsFromG.getPixel(pPos)<=((pX+1+w)%w)+1){return dirs[3];}
        return randomDir();
    }

    /**
     * computes how many steps is the closest ghost to pacMan
     * @param gs
     * @param pPos
     * @param map
     * @return
     */
    private static int getClosestGhostDist(GhostCL[] gs, Pixel2D pPos, Map2D map) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int minDist=Integer.MAX_VALUE;
        int ind= getClosestGhostIndex(gs,pPos,map);
        String s=gs[ind].getPos(0);
        int gX= Integer.parseInt( s.split(",")[0]);
        int gY= Integer.parseInt( s.split(",")[1]);
        Pixel2D gP=new Index2D(gX,gY);
        //change
        minDist=map.shortestPath(pPos,gP,blue).length-1;
        return minDist;
    }

    private static int getClosestGhostIndex(GhostCL[] gs, Pixel2D pPos, Map2D map) {
        int blue = Game.getIntColor(Color.BLUE, 0);
        int index=Integer.MAX_VALUE;
        for (int i=0;i<gs.length-2;i++) {
            String s=gs[i].getPos(0);
            int gX= Integer.parseInt( s.split(",")[0]);
            int gY= Integer.parseInt( s.split(",")[1]);
            Pixel2D p=new Index2D(gX,gY);
            Map2D gMap1 = map.allDistance(p, blue);
            int dist = gMap1.shortestPath(p, pPos, blue).length;
            Map2D gMap2 = map.allDistance(p, blue);
            int nextDist =  gMap2.shortestPath(p, pPos, blue).length;
            if (dist <= nextDist) {index = i;};
        }
    return index;
    }

//    private static int chaseGhost(Pixel2D pPos, Pixel2D gPos,Map2D map){
//        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
//        int blue = Game.getIntColor(Color.BLUE, 0);
//        Map2D gDists= map.allDistance(gPos,blue);
//        int x=pPos.getX();
//        int y=pPos.getY();
//        int w=map.getWidth();
//        int h=map.getHeight();
//        //up
//        if ( gDists.getPixel(pPos)>gDists.getPixel(x,(y+1+h)%h) ){return dirs[0];}
//        //left
//        if(gDists.getPixel(pPos)>gDists.getPixel((x-1)%w,y)){return dirs[1];}
//        //down
//        if(gDists.getPixel(pPos)>gDists.getPixel(x,(y-1)%h)){return dirs[2];}
//        //right
//        if(gDists.getPixel(pPos)>gDists.getPixel((x+1+w)%w,y)){return dirs[3];}
//        return randomDir();
//    }
}