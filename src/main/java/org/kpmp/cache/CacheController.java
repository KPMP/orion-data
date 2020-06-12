package org.kpmp.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "/v1/clearCache", method = RequestMethod.GET)
    public @ResponseBody CacheResponse clearCache(){
        Collection<String> cacheNames = cacheManager.getCacheNames();
        CacheResponse clearCacheResponse = new CacheResponse();
        clearCacheResponse.setMessage("Caches successfully cleared!");
        for(String name:cacheNames){
            if (cacheManager.getCache(name) != null) {
                cacheManager.getCache(name).clear();
            } else {
                clearCacheResponse.setMessage("There was a problem getting the " + name + " cache.");
                break;
            }
        }
        clearCacheResponse.setCacheNames(cacheNames);
        return clearCacheResponse;
    }
}
