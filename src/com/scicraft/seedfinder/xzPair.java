package com.scicraft.seedfinder;

public class xzPair {
	private int x, z,or;
        public static final int EW = 1, NS = 0;
	public xzPair(int x, int z, int or){
		this.x = x;
		this.z = z;
                this.or = or;
	}
        
        public xzPair(int x, int z){
		this.x = x;
		this.z = z;
	}
	
	public int getX(){
		return x;
	}
	
	public int getZ(){
		return z;
	}
        
        public int getOrientation(){
            return or;
        }
}
