package minecraft.layer;

//import com.google.common.collect.Lists;
//import java.util.List;
//
//public class IntCache
//{
//    private static int intCacheSize = 256;
//
//    /**
//     * A list of pre-allocated int[256] arrays that are currently unused and can be returned by getIntCache()
//     */
//    private static List<int[]> freeSmallArrays = Lists.newArrayList();
//
//    /**
//     * A list of pre-allocated int[256] arrays that were previously returned by getIntCache() and which will not be re-
//     * used again until resetIntCache() is called.
//     */
//    private static List<int[]> inUseSmallArrays = Lists.newArrayList();
//
//    /**
//     * A list of pre-allocated int[cacheSize] arrays that are currently unused and can be returned by getIntCache()
//     */
//    private static List<int[]> freeLargeArrays = Lists.newArrayList();
//
//    /**
//     * A list of pre-allocated int[cacheSize] arrays that were previously returned by getIntCache() and which will not
//     * be re-used again until resetIntCache() is called.
//     */
//    private static List<int[]> inUseLargeArrays = Lists.newArrayList();
//
//    public static synchronized int[] getIntCache(int p_76445_0_)
//    {
//        int[] var1;
//
//        if (p_76445_0_ <= 256)
//        {
//            if (freeSmallArrays.isEmpty())
//            {
//                var1 = new int[256];
//                inUseSmallArrays.add(var1);
//                return var1;
//            }
//            else
//            {
//                var1 = (int[])freeSmallArrays.remove(freeSmallArrays.size() - 1);
//                inUseSmallArrays.add(var1);
//                return var1;
//            }
//        }
//        else if (p_76445_0_ > intCacheSize)
//        {
//            intCacheSize = p_76445_0_;
//            freeLargeArrays.clear();
//            inUseLargeArrays.clear();
//            var1 = new int[intCacheSize];
//            inUseLargeArrays.add(var1);
//            return var1;
//        }
//        else if (freeLargeArrays.isEmpty())
//        {
//            var1 = new int[intCacheSize];
//            inUseLargeArrays.add(var1);
//            return var1;
//        }
//        else
//        {
//            var1 = (int[])freeLargeArrays.remove(freeLargeArrays.size() - 1);
//            inUseLargeArrays.add(var1);
//            return var1;
//        }
//    }
//
//    /**
//     * Mark all pre-allocated arrays as available for re-use by moving them to the appropriate free lists.
//     */
//    public static synchronized void resetIntCache()
//    {
//        if (!freeLargeArrays.isEmpty())
//        {
//            freeLargeArrays.remove(freeLargeArrays.size() - 1);
//        }
//
//        if (!freeSmallArrays.isEmpty())
//        {
//            freeSmallArrays.remove(freeSmallArrays.size() - 1);
//        }
//
//        freeLargeArrays.addAll(inUseLargeArrays);
//        freeSmallArrays.addAll(inUseSmallArrays);
//        inUseLargeArrays.clear();
//        inUseSmallArrays.clear();
//    }
//
//    /**
//     * Gets a human-readable string that indicates the sizes of all the cache fields.  Basically a synchronized static
//     * toString.
//     */
//    public static synchronized String getCacheSizes()
//    {
//        return "cache: " + freeLargeArrays.size() + ", tcache: " + freeSmallArrays.size() + ", allocated: " + inUseLargeArrays.size() + ", tallocated: " + inUseSmallArrays.size();
//    }
//}
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntCache{
	private int intCacheSize = 256;
	private List freeSmallArrays = new ArrayList();
	private List inUseSmallArrays = new ArrayList();
	private List freeLargeArrays = new ArrayList();
	private List inUseLargeArrays = new ArrayList();
	
	private int[] getIntCache_orig(int par0){
        int[] var1;

        if (par0 <= 256){
            if (freeSmallArrays.isEmpty()){
                var1 = new int[256];
                inUseSmallArrays.add(var1);
                return var1;
            }else{
                var1 = (int[])freeSmallArrays.remove(freeSmallArrays.size() - 1);
                inUseSmallArrays.add(var1);
                return var1;
            }
        }else if (par0 > intCacheSize){
            intCacheSize = par0;
            freeLargeArrays.clear();
            inUseLargeArrays.clear();
            var1 = new int[intCacheSize];
            inUseLargeArrays.add(var1);
            return var1;
        }else if (freeLargeArrays.isEmpty()){
            var1 = new int[intCacheSize];
            inUseLargeArrays.add(var1);
            return var1;
        }else{
            var1 = (int[])freeLargeArrays.remove(freeLargeArrays.size() - 1);
            inUseLargeArrays.add(var1);
            return var1;
        }
    }

    private void resetIntCache_orig(){
        if (!freeLargeArrays.isEmpty()) freeLargeArrays.remove(freeLargeArrays.size() - 1);
        if (!freeSmallArrays.isEmpty()) freeSmallArrays.remove(freeSmallArrays.size() - 1);

        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }
    
    
    private static HashMap<Long, IntCache> threadCache = new HashMap<Long, IntCache>();
    
    public static int[] getIntCache(int par0){
    	IntCache cache;
    	synchronized(threadCache){
	    	cache = threadCache.get(Thread.currentThread().getId());
	    	if(cache == null) threadCache.put(Thread.currentThread().getId(), cache = new IntCache());
    	}
    	return cache.getIntCache_orig(par0);
    }
    
    public static void resetIntCache(){
    	IntCache cache;
    	synchronized(threadCache){
			cache = threadCache.get(Thread.currentThread().getId());
			if(cache == null) threadCache.put(Thread.currentThread().getId(), cache = new IntCache());
		}
    	cache.resetIntCache_orig();
    }
}

