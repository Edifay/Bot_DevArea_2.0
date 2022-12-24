package devarea.fr.discord.cache.cached_entity;

import discord4j.core.object.entity.Entity;

import java.util.Objects;

public abstract class CachedObject<T extends Entity> {

    /*
        600000L -> 10min
     */
    private final static long CACHED_TIME = 600000L;

    protected String object_id;
    protected T object_cached;
    protected long last_fetch;

    public CachedObject(final T object_cached, final long last_fetch) {
        this.object_cached = object_cached;
        this.object_id = object_cached.getId().asString();
        this.last_fetch = last_fetch;
    }

    public CachedObject(final String object_id) {
        this.object_id = object_id;
        this.object_cached = null;
        this.last_fetch = 0;
    }

    /*
        Get the value, fetch if needToBeFetch() and use cache if not.
     */
    public T get() {
        if (this.object_cached == null || needToBeFetch())
            return fetch();
        return this.object_cached;
    }

    /*
        Return if the object cached is too old, defined by CACHED_TIME
     */
    protected boolean needToBeFetch() {
        return (System.currentTimeMillis() - this.last_fetch) > CACHED_TIME;
    }

    /*
        Bypass cache and force to fetch.
        Need to be implemented, to fetch your own object !
     */
    public abstract T fetch();

    /*
        Bypass needToBeFetch() and fetch if the object isn't null.
     */
    public T watch() {
        if (this.object_cached == null)
            this.fetch();
        return this.object_cached;
    }

    /*
        Simulate a new fetch from an object
     */
    public void use(final T object_cached) throws Exception {
        if (this.object_id.equals(object_cached.getId().asString())) {
            this.object_cached = object_cached;
            this.last_fetch = System.currentTimeMillis();
        } else
            throw new Exception("Wrong member usage !");
    }

    /*
        Reset
     */
    public void reset() {
        this.last_fetch = 0;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(object_id, o) || Objects.equals(o, object_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object_id, object_cached, last_fetch);
    }
}
