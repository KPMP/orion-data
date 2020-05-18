package org.kpmp.cache;

import java.util.Collection;

public class CacheResponse {

        private String message;
        private Collection<String> cacheNames;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Collection<String> getCacheNames() {
            return cacheNames;
        }

        public void setCacheNames(Collection<String> cacheNames) {
            this.cacheNames = cacheNames;
        }
}
