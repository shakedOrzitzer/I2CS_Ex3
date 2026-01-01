package assignments.Ex3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D {
	private int[][] _map;
	private boolean _cyclicFlag = true;
    private final int BARRIER=-1;
	
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w,h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("map width and height must be positive");
        }
        this._map = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this._map[i][j] = v;
            }
        }
	}
	@Override
	public void init(int[][] arr) {
        if (arr == null) {
            throw new RuntimeException("map array is empty");
        }
        boolean t = true;
        for (int s = 0; s < arr.length - 1; s++) {
            if (arr[s].length != arr[s + 1].length) {
                t = false;
                break;
            }
        }
        if (t == false) {
            throw new RuntimeException("map can't be made of ragged 2D array");
        }
        this._map = new int[arr.length][arr[0].length];
        for (int i = 0; i < _map.length; i++) {
            for (int j = 0; j < _map[1].length; j++) {
                this._map[i][j] = arr[i][j];
            }
        }
	}

    @Override
    public int[][] getMap() {
        return this._map;
    }

    @Override
    public int getWidth() {
        return this._map.length;
    }

    @Override
    public int getHeight() {
        return this._map[0].length;
    }

    @Override
    public int getPixel(int x, int y) {
        int ans= -5;
        Pixel2D p=new Index2D(x,y);
        if(isInside(p)) ans = getPixel(p);
        return ans;
    }
    // return the int value of pixel in the map, -5 if not in map
    @Override
    public int getPixel(Pixel2D p) {
        int ans = -5;
        if(isInside(p)) ans = _map[p.getX()][p.getY()];
        return ans;
    }    @Override
    public void setPixel(int x, int y, int v) {
        Pixel2D p= new Index2D(x,y);
        if (isInside(p) ) _map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        if(isInside(p)) _map[p.getX()][p.getY()] = v;
    }

	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v) {
        boolean[][] hits= new boolean[_map.length][_map[0].length];
        Queue<Pixel2D> toFill = new LinkedList<>();
        toFill.add(xy);
        int srcCol = getPixel(xy);
        int count = 0;
        Pixel2D p;
        while (!toFill.isEmpty()) {
            p=toFill.remove();
            int x = p.getX();
            int y = p.getY();
            if(_cyclicFlag){
                if (x==-1) x=getWidth()-1;
                if(x==getWidth()) x=0;
                if (y==-1) y=getHeight()-1;
                if(y==getHeight()) y=0;
            }
            if(_cyclicFlag && (floodFillDo(_map,hits,x,y,srcCol,new_v))){
                toFill.add(new Index2D(x,y - 1));
                toFill.add(new Index2D(x,y + 1));
                toFill.add(new Index2D(x - 1,y));
                toFill.add(new Index2D(x+ 1,y));
                count++;
            }
            else if (!_cyclicFlag && floodFillDo(_map,hits,x,y,srcCol,new_v)){
                if (isValid(x,y-1)&& _map[x][y-1]!=BARRIER) toFill.add(new Index2D(x,y-1));
                if (isValid(x,y+1)&& _map[x][y+1]!=BARRIER) toFill.add(new Index2D(x,y+1));
                if (isValid(x-1,y)&& _map[x-1][y]!=BARRIER) toFill.add(new Index2D(x-1,y));
                if (isValid(x+1,y)&& _map[x+1][y]!=BARRIER) toFill.add(new Index2D(x+1,y));
                count++;
            }
        }
        return count;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
        if(p1.equals(p2)){
            return new Pixel2D[]{p1};
        }
        if(!isInside(p1) || !isInside(p2)){
            Pixel2D[] res= new Index2D[]{new Index2D(-1, -1)};
            return res;
        }
        int[][] dists = new int[getWidth()][getHeight()];
        for(int i = 0; i< dists.length; i++){
            for (int j = 0; j< dists[0].length; j++){
                if (getPixel(i,j) == obsColor)
                    dists[i][j]=BARRIER;
                else
                    dists[i][j]=-2;
            }
        }
        dists[p1.getX()][p1.getY()]=0;
        Queue<Pixel2D> neighbors = new LinkedList<>();
        neighbors.add(p1);
        Pixel2D p;
        boolean found = false;
        boolean[][] visited =new  boolean[getWidth()][getHeight()];
        while(!neighbors.isEmpty()){
            p=neighbors.poll();
            if (p.equals(p2)) {
                found = true;
                break;
            }
            for(Pixel2D neighbor: getNeighbors(p,_cyclicFlag)){
                if( getPixel(neighbor)!=obsColor && dists[neighbor.getX()][neighbor.getY()] == -2){
                    dists[neighbor.getX()][neighbor.getY()] = dists[p.getX()][p.getY()] + 1;
                    neighbors.add(neighbor);
                }
            }
        }
        if(!found){
            return new Pixel2D[]{new Index2D(-1, -1)};
        }
        int pathLen = dists[p2.getX()][p2.getY()] + 1;
        Pixel2D[] path = new Pixel2D[pathLen];
        Pixel2D temp = p2;

        for (int i = pathLen - 1; i >= 0; i--) {
            path[i] = temp;
            temp = findPreviousNeighbor(temp, dists,_cyclicFlag);
        }
        return path;

    }
	@Override
	public boolean isInside(Pixel2D p) {
        boolean ans = true;
        if (!(isValid(p.getX(), p.getY())) || (p==null)) {
            return false;
        }
        return ans;
	}

	@Override
	/////// add your code below ///////
	public boolean isCyclic() {
        if(_cyclicFlag){
            return true;
        }
		return false;
	}
	@Override
	public void setCyclic(boolean cy) {
        _cyclicFlag = cy;}

	@Override
	public Map2D allDistance(Pixel2D start, int obsColor) {
        Map2D ans = new Map(getWidth(),getHeight(),-1);
        if(start==null){return ans;}
        Queue<Pixel2D> toCheck = new LinkedList<>();
        toCheck.add(start);
        ans.setPixel(start,0);
        while(!toCheck.isEmpty()){
            Pixel2D p = toCheck.poll();
            int distance= ans.getPixel(p)+1;
            for (Pixel2D neighbor:getNeighbors(p,_cyclicFlag)) {
                if(getPixel(neighbor)!=obsColor && ans.getPixel(neighbor)==-1){
                    ans.setPixel(neighbor,distance);
                    toCheck.add(neighbor);
                }
            }
        }
        return ans;
	}


/////////////private functions////////////////
    /**
     * checks if map size>0, width and height have to be positive and within map
     *
     * @param x
     * @param y
     * @return true if (x,y) in map
     */
    private boolean isValid(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }
        return true;
    }

    /**
     * help function for floodFill
     * @param _map the map to be filled
     * @param hits boolean array which contains value true if a pixel was filled
     * @param x x of pixel
     * @param y y of pixel
     * @param srcColor original color to be filled
     * @param tgtColor the color swiched to, the output color
     * @return true if pixel was filled, false if not
     */
    private boolean floodFillDo(int[][] _map, boolean[][] hits,int x, int y,int srcColor, int tgtColor)
    {
        if (y < 0) return false;
        if (x < 0) return false;
        if (y > _map.length-1) return false;
        if (x > _map[0].length-1) return false;
        if (hits[x][y]) return false;
        if (_map[x][y]!=srcColor)
            return false;
        if (_map[x][y]==tgtColor) return false;
        this.setPixel (x, y, tgtColor);
        hits[x][y] = true;
        return true;
    }

    /**
     * function computs all the neighbors of a given pixel within the map
     * @param p given pixel
     * @param cyclic is cyclic
     * @return       an arrayList contains all 'next door neighbors' of p, north, south, east, west,
     *  who are within the map.
     */
    private ArrayList<Pixel2D> getNeighbors(Pixel2D p, boolean cyclic){
        ArrayList<Pixel2D> neighbors = new ArrayList<>();
        if (!isInside(p)) return neighbors;
        int x = p.getX();
        int y = p.getY();
        int w = getWidth();
        int h = getHeight();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];
            if (cyclic) {
                nextX = (nextX + w) % w;
                nextY = (nextY + h) % h;
                neighbors.add(new Index2D(nextX, nextY));
            } else {
                Pixel2D nextP= new Index2D(nextX,nextY);
                if (isInside(nextP)) {
                    neighbors.add(nextP);
                }
            }
        }
        return neighbors;}

    /**
     * Finds a neighboring pixel that is one step closer to the starting point.
     * This helper method searches the immediate neighbors of the current pixel
     * to find one whose recorded distance is exactly one less than the current
     * pixel's distance. This is used to backtrack from a destination to the
     * source to reconstruct the shortest path.
     *
     * @param current The pixel from which to look backward.
     * @param dists   A 2D array of calculated distances from the source point.
     * @param cyclic  If true, searches for neighbors across map boundaries.
     * @return        A Pixel2D neighbor with a distance of (current distance - 1),
     *  * or null if no such neighbor exists.
     */
    private Pixel2D findPreviousNeighbor(Pixel2D current, int[][] dists,boolean cyclic) {
        int targetDist = dists[current.getX()][current.getY()] - 1;
        for (Pixel2D neighbor : getNeighbors(current, cyclic)) {
            if (dists[neighbor.getX()][neighbor.getY()] == targetDist) {
                return neighbor;
            }
        }
        return null;
    }

}
