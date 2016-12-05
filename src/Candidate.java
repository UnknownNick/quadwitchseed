
import com.scicraft.seedfinder.*;
import minecraft.layer.GenLayer;
import minecraft.layer.IntCache;


public class Candidate {
	public final static int TOPRIGHT = 0;
	public final static int BOTTOMRIGHT = 1;
	public final static int BOTTOMLEFT = 2;
	public final static int TOPLEFT = 3;
	
	public final static int NONE = 0;
	public final static int HUT = 1;
	public final static int DESERTTEMPLE = 2;
	public final static int JUNGLETEMPLE = 3;

	public final static int NORTHSOUTHORIENTATION = 0;
	public final static int EASTWESTORIENTATION = 1;
	
	public final static String[] TYPESTRING = new String[] { "NONE", "Witch Hut", "Desert Temple", "Jungle Temple" };
	public final static String[] ORIENTATIONSTRING = new String[] { "N-S Orientation", "E-W Orientation" };
	
	protected long seed;
        protected biomeGenerator biomeGen;
	
	protected int[] xpos;
	protected int[] zpos;
	protected int[] xrand;
	protected int[] zrand;
	
	protected int[] structureOrientations;
	
	protected int xcenter;
	protected int zcenter;
	
	protected double[] distanceToCenter;
	protected double maxDistanceToCenter;
	
	protected int[][] biomeInts;
	protected int[] biomeIds;
	protected boolean[] validBiomes;
	
	protected int[] type;
	protected int hutCount;
	
	protected int[] witchSpawningArea;
	protected int totalWitchSpawningArea;
	
	public Candidate(long seed, int[] x, int[] z, int[] xrand, int[] zrand, int[] structureOrientations) {
		this.seed = seed;
		this.xrand = xrand;
		this.zrand = zrand;
		this.structureOrientations = structureOrientations;
                
                biomeGen = new biomeGenerator(this.seed,2);
		
		xpos = new int[4];
		zpos = new int[4];
		
		xpos[TOPRIGHT] = 512 * x[TOPRIGHT] + 16 * xrand[TOPRIGHT];
		zpos[TOPRIGHT] = 512 * z[TOPRIGHT] + 16 * zrand[TOPRIGHT];
		xpos[BOTTOMRIGHT] = 512 * x[BOTTOMRIGHT] + 16 * xrand[BOTTOMRIGHT];
		zpos[BOTTOMRIGHT] = 512 * z[BOTTOMRIGHT] + 16 * zrand[BOTTOMRIGHT];
		xpos[BOTTOMLEFT] = 512 * x[BOTTOMLEFT] + 16 * xrand[BOTTOMLEFT];
		zpos[BOTTOMLEFT] = 512 * z[BOTTOMLEFT] + 16 * zrand[BOTTOMLEFT];
		xpos[TOPLEFT] = 512 * x[TOPLEFT] + 16 * xrand[TOPLEFT];
		zpos[TOPLEFT] = 512 * z[TOPLEFT] + 16 * zrand[TOPLEFT];
	}
        
        public Candidate(long seed, int[] x, int[] z, int[] structureOrientations) {
		this.seed = seed;
		this.structureOrientations = structureOrientations;
                
                biomeGen = new biomeGenerator(this.seed,2);
		
		xpos = new int[4];
		zpos = new int[4];
		
		xpos[TOPRIGHT] = 16*x[TOPRIGHT];
		zpos[TOPRIGHT] = 16*z[TOPRIGHT];
		xpos[BOTTOMRIGHT] = 16*x[BOTTOMRIGHT];
		zpos[BOTTOMRIGHT] = 16*z[BOTTOMRIGHT];
		xpos[BOTTOMLEFT] = 16*x[BOTTOMLEFT];
		zpos[BOTTOMLEFT] = 16*z[BOTTOMLEFT];
		xpos[TOPLEFT] = 16*x[TOPLEFT];
		zpos[TOPLEFT] = 16*z[TOPLEFT];
	}
        
	public void initializeBiomeValues() {
            biomeGen = new biomeGenerator(this.seed,2);
		GenLayer biomeIndexLayer = biomeGen.biomeIndexLayer;
		
		//calls a modified version of Minecraft's IntCache that was changed for using it with multiple threads
		IntCache.resetIntCache();		
		
		/* 
		 * Determine the biome of the structure
		 */
		biomeInts = new int[4][];
		
		// get ints, use a 24 x24 array so that it is large enough to hold a 21x21 desert temple
		biomeInts[TOPRIGHT] = biomeIndexLayer.getInts(xpos[TOPRIGHT],zpos[TOPRIGHT],24,24);
		biomeInts[BOTTOMRIGHT] = biomeIndexLayer.getInts(xpos[BOTTOMRIGHT],zpos[BOTTOMRIGHT],24,24);
		biomeInts[BOTTOMLEFT] = biomeIndexLayer.getInts(xpos[BOTTOMLEFT],zpos[BOTTOMLEFT],24,24);
		biomeInts[TOPLEFT] = biomeIndexLayer.getInts(xpos[TOPLEFT],zpos[TOPLEFT],24,24);
		
		biomeIds = new int[4];
		
		/*
		 * There is some wierdness in the minecraft code here, as it seems like it looks
		 * at the biomeInts code at xpos = 6, zpos = 8 or 8,6 depending on orientation
		 * to determine the type of structure.
		 * 
		 * This makes sense for witch huts as they are 7x9 but temples are bigger, and
		 * yet it appears they are determined by the same location.
		 *
		 * use 
		 * 		198 here as we have gotten a 24x24 array, so 8 * 24 + 6 = 198
		 * or 
		 * 		152 here as we have gotten a 24x24 array, so 6 * 24 + 8 = 152
		 */
		biomeIds[TOPRIGHT] = structureOrientations[TOPRIGHT] == NORTHSOUTHORIENTATION ?
			biomeInts[TOPRIGHT][198] : biomeInts[TOPRIGHT][152];
		biomeIds[BOTTOMRIGHT] = structureOrientations[BOTTOMRIGHT] == NORTHSOUTHORIENTATION ?
			biomeInts[BOTTOMRIGHT][198] : biomeInts[BOTTOMRIGHT][152];
		biomeIds[BOTTOMLEFT] = structureOrientations[BOTTOMLEFT] == NORTHSOUTHORIENTATION ?
			biomeInts[BOTTOMLEFT][198] : biomeInts[BOTTOMLEFT][152];
		biomeIds[TOPLEFT] = structureOrientations[TOPLEFT] == NORTHSOUTHORIENTATION ?
			biomeInts[TOPLEFT][198] : biomeInts[TOPLEFT][152];

		validBiomes = new boolean[4];
		type = new int[4];
		
		switch(biomeIds[TOPRIGHT]) {
		case 2:
			validBiomes[TOPRIGHT] = true;
			type[TOPRIGHT] = DESERTTEMPLE;
			break;
		case 21:
			validBiomes[TOPRIGHT] = true;
			type[TOPRIGHT] = JUNGLETEMPLE;
			break;
		case 6:
			validBiomes[TOPRIGHT] = true;
			type[TOPRIGHT] = HUT;
			hutCount++;
			break;
		}
		
		switch(biomeIds[BOTTOMRIGHT]) {
		case 2:
			validBiomes[BOTTOMRIGHT] = true;
			type[BOTTOMRIGHT] = DESERTTEMPLE;
			break;
		case 21:
			validBiomes[BOTTOMRIGHT] = true;
			type[BOTTOMRIGHT] = JUNGLETEMPLE;
			break;
		case 6:
			validBiomes[BOTTOMRIGHT] = true;
			type[BOTTOMRIGHT] = HUT;
			hutCount++;
			break;
		}

		switch(biomeIds[BOTTOMLEFT]) {
		case 2:
			validBiomes[BOTTOMLEFT] = true;
			type[BOTTOMLEFT] = DESERTTEMPLE;
			break;
		case 21:
			validBiomes[BOTTOMLEFT] = true;
			type[BOTTOMLEFT] = JUNGLETEMPLE;
			break;
		case 6:
			validBiomes[BOTTOMLEFT] = true;
			type[BOTTOMLEFT] = HUT;
			hutCount++;
			break;
		}

		switch(biomeIds[TOPLEFT]) {
		case 2:
			validBiomes[TOPLEFT] = true;
			type[TOPLEFT] = DESERTTEMPLE;
			break;
		case 21:
			validBiomes[TOPLEFT] = true;
			type[TOPLEFT] = JUNGLETEMPLE;
			break;
		case 6:
			validBiomes[TOPLEFT] = true;
			type[TOPLEFT] = HUT;
			hutCount++;
			break;
		}

	}
	
    /*
     * check whether the biome for all 4 structures is a valid biome for a structure
     */
	public boolean isQuadInAValidBiome(){		
		return (validBiomes[TOPRIGHT] && validBiomes[BOTTOMRIGHT] && validBiomes[BOTTOMLEFT]&& validBiomes[TOPLEFT]);
	}

	public double calculateMaxDistance() {
		int minx = xpos[TOPRIGHT];
		if ( xpos[BOTTOMRIGHT] < minx )
			minx = xpos[BOTTOMRIGHT];
		if ( xpos[BOTTOMLEFT] < minx )
			minx = xpos[BOTTOMLEFT];
		if ( xpos[TOPLEFT] < minx )
			minx = xpos[TOPLEFT];
		
		int maxx = xpos[TOPRIGHT];
		if ( xpos[BOTTOMRIGHT] > maxx )
			maxx = xpos[BOTTOMRIGHT];
		if ( xpos[BOTTOMLEFT] > maxx )
			maxx = xpos[BOTTOMLEFT];
		if ( xpos[TOPLEFT] > maxx )
			maxx = xpos[TOPLEFT];
		
		int minz = zpos[TOPRIGHT];
		if ( zpos[BOTTOMRIGHT] < minz )
			minz = zpos[BOTTOMRIGHT];
		if ( zpos[BOTTOMLEFT] < minz )
			minz = zpos[BOTTOMLEFT];
		if ( zpos[TOPLEFT] < minz )
			minz = zpos[TOPLEFT];
		
		int maxz = zpos[TOPRIGHT];
		if ( zpos[BOTTOMRIGHT] > maxz )
			maxz = zpos[BOTTOMRIGHT];
		if ( zpos[BOTTOMLEFT] > maxz )
			maxz = zpos[BOTTOMLEFT];
		if ( zpos[TOPLEFT] > maxz )
			maxz = zpos[TOPLEFT];
		
		xcenter = minx + (maxx - minx) /2;
		zcenter = minz + (maxz - minz) /2;
		
		distanceToCenter = new double[4];
		
		int dx=xcenter - xpos[TOPRIGHT];
		int dz=zpos[TOPRIGHT] - zcenter;
		distanceToCenter[TOPRIGHT] = Math.sqrt(dx*dx + dz*dz);
		dx=xcenter - xpos[BOTTOMRIGHT];
		dz=zcenter - zpos[BOTTOMRIGHT];
		distanceToCenter[BOTTOMRIGHT] = Math.sqrt(dx*dx + dz*dz);
		dx=xpos[BOTTOMLEFT] - xcenter;
		dz=zcenter - zpos[BOTTOMLEFT];
		distanceToCenter[BOTTOMLEFT] = Math.sqrt(dx*dx + dz*dz);
		dx=xpos[TOPLEFT] - xcenter;
		dz=zpos[TOPLEFT] - zcenter;
		distanceToCenter[TOPLEFT] = Math.sqrt(dx*dx + dz*dz);
		
		maxDistanceToCenter = distanceToCenter[TOPRIGHT];
		if ( distanceToCenter[BOTTOMRIGHT] > maxDistanceToCenter )
			maxDistanceToCenter = distanceToCenter[BOTTOMRIGHT];
		if ( distanceToCenter[BOTTOMLEFT] > maxDistanceToCenter )
			maxDistanceToCenter = distanceToCenter[BOTTOMLEFT];
		if ( distanceToCenter[TOPLEFT] > maxDistanceToCenter )
			maxDistanceToCenter = distanceToCenter[TOPLEFT];
		
		return maxDistanceToCenter;
	}
	
	public int calculateWitchSpawnableArea() {
		witchSpawningArea = new int[4];
		
		witchSpawningArea[TOPRIGHT] = calculateWitchSpawnableArea2(xpos[TOPRIGHT], zpos[TOPRIGHT], 
			type[TOPRIGHT], structureOrientations[TOPRIGHT], biomeInts[TOPRIGHT]);
		witchSpawningArea[BOTTOMRIGHT] = calculateWitchSpawnableArea2(xpos[BOTTOMRIGHT], zpos[BOTTOMRIGHT], 
				type[BOTTOMRIGHT], structureOrientations[BOTTOMRIGHT], biomeInts[BOTTOMRIGHT]);
		witchSpawningArea[BOTTOMLEFT] = calculateWitchSpawnableArea2(xpos[BOTTOMLEFT], zpos[BOTTOMLEFT], 
				type[BOTTOMLEFT], structureOrientations[BOTTOMLEFT], biomeInts[BOTTOMLEFT]);
		witchSpawningArea[TOPLEFT] = calculateWitchSpawnableArea2(xpos[TOPLEFT], zpos[TOPLEFT], 
				type[TOPLEFT], structureOrientations[TOPLEFT], biomeInts[TOPLEFT]);
		
		totalWitchSpawningArea = witchSpawningArea[TOPRIGHT] + witchSpawningArea[BOTTOMRIGHT] + 
			witchSpawningArea[BOTTOMLEFT] + witchSpawningArea[TOPLEFT];
		
		return totalWitchSpawningArea; 
	}
	
	private int calculateWitchSpawnableArea2(int x, int z, int type, int structureOrientation, int[] biomeInts) {
		int count = 0;
		switch ( type ){
		case NONE:
			break;
		case HUT:
			if ( structureOrientation == NORTHSOUTHORIENTATION ) {
				for ( int xx = 0; xx < 7; xx++)
					for ( int zz = 0; zz < 9; zz++) {
						if (isSwamp(xx, zz, biomeInts))
							count+=2;
					}
			}
			else {
				// otherwise EW orientation
				for ( int xx = 0; xx < 9; xx++)
					for ( int zz = 0; zz < 7; zz++) {
						if (isSwamp(xx, zz, biomeInts))
							count+=2;
					}
				
			}
			break;
		case DESERTTEMPLE:
			for ( int xx = 0; xx < 21; xx++)
				for ( int zz = 0; zz < 21; zz++) {
					if (isSwamp(xx, zz, biomeInts))
						count+=5;
				}
			break;
		case JUNGLETEMPLE:			
			if ( structureOrientation == NORTHSOUTHORIENTATION ) {
				for ( int xx = 0; xx < 12; xx++)
					for ( int zz = 0; zz < 15; zz++) {
						if (isSwamp(xx, zz, biomeInts))
							count+=4;
					}
			}
			else {
				// otherwise EW orientation
				for ( int xx = 0; xx < 12; xx++)
					for ( int zz = 0; zz < 15; zz++) {
						if (isSwamp(xx, zz, biomeInts))
							count+=4;
					}
				
			}
		}
		
		return count;
	}

	private boolean isSwamp(int x, int z, int[] biomeInts) {
                
		return biomeInts[24 * z + x] == 6;
	}
        
        public void setSeed(long seed){
            this.seed = seed;
        }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{ center (x, z) : (").append(xcenter).append(", ").append(zcenter);
		sb.append(",  maximum distance from Center : ").append(maxDistanceToCenter);
		sb.append(",  seed : ").append(seed);
		sb.append(", # of huts : ").append(hutCount);
		sb.append(", estimated total spawning area : ").append(totalWitchSpawningArea);
		sb.append(", \n  Top Right : { type : ").append(TYPESTRING[type[TOPRIGHT]]);
		sb.append(", coord (x,z) : (").append(xpos[TOPRIGHT]).append(",").append(zpos[TOPRIGHT]);
		sb.append(", spawning area : ").append(witchSpawningArea[TOPRIGHT]);
		sb.append(", orientation : ").append(ORIENTATIONSTRING[structureOrientations[TOPRIGHT]]);
		sb.append(") }, \n  Bottom Right : { type : ").append(TYPESTRING[type[BOTTOMRIGHT]]);
		sb.append(", coord (x,z) : (").append(xpos[BOTTOMRIGHT]).append(",").append(zpos[BOTTOMRIGHT]);
		sb.append(", spawning area : ").append(witchSpawningArea[BOTTOMRIGHT]);
		sb.append(", orientation : ").append(ORIENTATIONSTRING[structureOrientations[BOTTOMRIGHT]]);
		sb.append(") }, \n  Bottom Left : { type : ").append(TYPESTRING[type[BOTTOMLEFT]]);
		sb.append(", coord (x,z) : (").append(xpos[BOTTOMLEFT]).append(",").append(zpos[BOTTOMLEFT]);
		sb.append(", spawning area : ").append(witchSpawningArea[BOTTOMLEFT]);
		sb.append(", orientation : ").append(ORIENTATIONSTRING[structureOrientations[BOTTOMLEFT]]);
		sb.append(") }, \n  Top Left : { type : ").append(TYPESTRING[type[TOPLEFT]]);
		sb.append(", coord (x,z) : (").append(xpos[TOPLEFT]).append(",").append(zpos[TOPLEFT]);
		sb.append(", spawning area : ").append(witchSpawningArea[TOPLEFT]);
		sb.append(", orientation : ").append(ORIENTATIONSTRING[structureOrientations[TOPLEFT]]).append(") }}\n");
		
		return sb.toString();
	}
	
}
