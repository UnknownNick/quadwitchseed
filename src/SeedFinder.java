

import java.util.Random;

import com.scicraft.seedfinder.*;

public class SeedFinder extends Thread {
	public final static int TOPRIGHT = 0;
	public final static int BOTTOMRIGHT = 1;
	public final static int BOTTOMLEFT = 2;
	public final static int TOPLEFT = 3;
        public final static int THREADCOUNT = 10;
        public final static long BATCHSIZE = 1000000000L;
	public Random rnd = new Random();
	public int[] xpos = new int[4];
	public int[] zpos = new int[4];
        public int[] ors = new int[4];
        public static long[] startSeeds;
        public static long endSeed;
        public static SeedFinder[] threads;
	//public static int xmon, zmon;
	public structureHut hut;
	public bitIterator bitIt;
        public static int least = 505; //spawning spaces
        public String s;
        public int index = 0;
	
        public String or(int index) {
            return (ors[index]==1)?"EW":"NS";
        }
        
	
	public static boolean allSwamp(int[] x, int[] z, biomeGenerator generate)
	{
		for(int i = 0; i < 4; i++)
		{
			if(generate.getBiomeAt(x[i] * 16 , z[i] * 16 ) != 6)
				return false;
		}
		return true;
	}
        
        public static boolean quadInValidBiomes(int[] x, int[] z, biomeGenerator generate) {
            for(int i = 0; i < 4; i++)
		{
                    int b = generate.getBiomeAt(x[i] * 16 , z[i] * 16 );
			if(b != 6 && b != 2 && b != 21)
				return false;
		}
		return true;
        }
        
        public static boolean allDesert(int[] x, int[] z, biomeGenerator generate)
	{
		for(int i = 0; i < 4; i++)
		{
			if(generate.getBiomeAt(x[i] * 16 + 8, z[i] * 16 + 8) != 2)
				return false;
		}
		return true;
	}
	
	protected boolean checkForStructureBR(int x, int z, long seed) {
		xzPair coords = hut.structurePosInRegion(x, z, seed);		
		int xrand = coords.getX();
		int zrand = coords.getZ();
		xpos[TOPLEFT] = x  * 32 + xrand;
		zpos[TOPLEFT] = z  * 32 + zrand;
                ors[TOPLEFT] = hut.lastCall.getOrientation();
		
		
		return xrand >= 22 && zrand >= 22;
	}

	protected boolean checkForStructureBL(int x, int z, long seed) {
		xzPair coords = hut.structurePosInRegion(x, z, seed);		
		int xrand = coords.getX();
		int zrand = coords.getZ();
		xpos[TOPRIGHT] = x  * 32 + xrand;
		zpos[TOPRIGHT] = z  * 32 + zrand;
                ors[TOPRIGHT] = hut.lastCall.getOrientation();
	
		return xrand <=1 && zrand >= 22;
	}
	
	protected boolean checkForStructureTR(int x, int z, long seed) {
		xzPair coords = hut.structurePosInRegion(x, z, seed);		
		int xrand = coords.getX();
		int zrand = coords.getZ();
		xpos[BOTTOMLEFT] = x  * 32 + xrand;
		zpos[BOTTOMLEFT] = z  * 32 + zrand;
                ors[BOTTOMLEFT] = hut.lastCall.getOrientation();
		
		return xrand >=22 && zrand <= 1;
	}

	protected boolean checkForStructureTL(int x, int z, long seed) {
		xzPair coords = hut.structurePosInRegion(x, z, seed);		
		int xrand = coords.getX();
		int zrand = coords.getZ();
		xpos[BOTTOMRIGHT] = x  * 32 + xrand;
		zpos[BOTTOMRIGHT] = z  * 32 + zrand;
                ors[BOTTOMRIGHT] = hut.lastCall.getOrientation();
		
		return xrand <=1 && zrand <= 1;
	}
	
	
	public void checkBits(long seed) {	
		long seedBit = seed & 281474976710655L;	//magic number
		bitIt = new bitIterator(seedBit);
		Candidate candidate = new Candidate(seed,xpos,zpos,ors);
		
		System.out.println("checking bits of base " + seedBit + "\n" +
                    (xpos[0] * 16) + " " + (zpos[0] * 16) + " " + or(0) + "\n" +
                    (xpos[1] * 16) + " " + (zpos[1] * 16) + " " + or(1) + "\n" +
                    (xpos[2] * 16) + " " + (zpos[2] * 16) + " " + or(2) + "\n" +
                    (xpos[3] * 16) + " " + (zpos[3] * 16) + " " + or(3));
		
		while(bitIt.hasNext()){
			long seedFull = bitIt.next();
			biomeGenerator generate = new biomeGenerator(seedFull, 2);
                        candidate.setSeed(seedFull);
                        candidate.initializeBiomeValues();
			if(candidate.isQuadInAValidBiome()){
                            int a = candidate.calculateWitchSpawnableArea();
                            if(a>=least){
                                s = seedFull + " " + candidate.calculateMaxDistance() + " " + a;
                                System.out.println(s);
                            }
                        }
		}
		
	}
	
	
        @SuppressWarnings("empty-statement")
	public static void main(String[] args) {
            Random r = new Random();
            endSeed = 281474976710656L; //higher than 2^48 will be useless
            long startSeed = 278827814000L;
            while((startSeed = r.nextLong()) > (endSeed - THREADCOUNT*BATCHSIZE)); //Long.parseLong(args[0]);
            endSeed = startSeed + THREADCOUNT*BATCHSIZE;
            System.out.println(startSeed);
            startSeeds = new long[THREADCOUNT];
            threads = new SeedFinder[THREADCOUNT];
            
            for(int t = 0; t < THREADCOUNT ; t++) {
                startSeeds[t] = startSeed + t;
                threads[t] = new SeedFinder() {
                    
                    @Override
                    public void run(){
                        
                        int radius = 4;
                        long currentSeed;
                        int xr, zr;
                        this.hut = new structureHut();
                        for(currentSeed = startSeeds[index]; currentSeed <= endSeed; currentSeed += THREADCOUNT){			

                                for(int x=-radius; x<radius - 1; x+=2) {	

                                        long xPart = this.hut.xPart(x);

                                        for(int z=-radius; z<radius - 1; z+=2) {

                                                long zPart = hut.zPart(z);
                                                xzPair coords = hut.structurePosInRegionFast(xPart, zPart, currentSeed, 1, 22);

                                                if(coords != null){
                                                        xr = coords.getX();
                                                        zr = coords.getZ();

                                                        ors[BOTTOMRIGHT] = hut.lastCall.getOrientation();
                                                        ors[TOPRIGHT] = hut.lastCall.getOrientation();
                                                        ors[BOTTOMLEFT] = hut.lastCall.getOrientation();
                                                        ors[TOPLEFT] = hut.lastCall.getOrientation();


                                                        if (xr <= 1) {

                                                                if( zr <= 1 ) {
                                                                        // candidate witch hut, is in the top left of the 32x32 chunk array
                                                                        // this means that to be in a quad it would be in bottom right of the quad

                                                                        // check the 32x32 chunk area neighbors to the left and above
                                                                        if ( checkForStructureTR(x-1, z, currentSeed) && 
                                                                                checkForStructureBR(x-1, z-1, currentSeed) &&
                                                                                checkForStructureBL(x, z-1, currentSeed)) {	
                                                                                        xpos[BOTTOMRIGHT] =  x * 32 + xr;
                                                                                        zpos[BOTTOMRIGHT] =  z * 32 + zr;
                                                                                        checkBits(currentSeed);			
                                                                        }

                                                                }
                                                                else if( zr >= 22 ){
                                                                        // candidate witch hut, is in the bottom left of the 32x32 chunk array
                                                                        // this means that to be in a quad it would be in top right of the quad

                                                                        // check the 32x32 chunk area neighbors to the left and below
                                                                        if ( checkForStructureTL(x, z+1, currentSeed) && 
                                                                                checkForStructureTR(x-1, z+1, currentSeed) &&
                                                                                checkForStructureBR(x-1, z, currentSeed)) {
                                                                                        xpos[TOPRIGHT] =  x  * 32 + xr;
                                                                                        zpos[TOPRIGHT] =  z  * 32 + zr;
                                                                                        checkBits(currentSeed);
                                                                        }
                                                                }

                                                        } else{							
                                                                if( zr <= 1 ) {
                                                                        // candidate witch hut, is in the top right of the 32x32 chunk array
                                                                        // this means that to be in a quad it would be in bottom left of the quad

                                                                        // check the 32x32 chunk area neighbors to the right and above
                                                                        if ( checkForStructureBR(x, z-1, currentSeed) && 
                                                                                checkForStructureBL(x+1, z-1, currentSeed) && 
                                                                                checkForStructureTL(x+1, z, currentSeed)) {
                                                                                        xpos[BOTTOMLEFT] =  x  * 32 + xr;
                                                                                        zpos[BOTTOMLEFT] =  z  * 32 + zr;
                                                                                        checkBits(currentSeed);

                                                                        }
                                                                }
                                                                else if( zr >= 22 ){						
                                                                        // candidate witch hut, is in the bottom right of the 32x32 chunk array
                                                                        // this means that to be in a quad it would be in top left of the quad

                                                                        // check the 32x32 chunk area neighbors to the right and below
                                                                        if ( checkForStructureBL(x+1, z, currentSeed) && 
                                                                                checkForStructureTL(x+1, z+1, currentSeed) && 
                                                                                checkForStructureTR(x, z+1, currentSeed)) {
                                                                                        xpos[TOPLEFT] =  x  * 32 + xr;
                                                                                        zpos[TOPLEFT] =  z  * 32 + zr;
                                                                                        checkBits(currentSeed);									
                                                                        }	
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                    }
                };
                threads[t].index = t;
                threads[t].start();
            }
		
	}	
}