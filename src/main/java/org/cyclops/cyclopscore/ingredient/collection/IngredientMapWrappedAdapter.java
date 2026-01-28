package org.cyclops.cyclopscore.ingredient.collection;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientInstanceWrapper;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * An abstract ingredient map adapter.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @param <V> The type of mapped values.
 */
public abstract class IngredientMapWrappedAdapter<T, M, V, C extends Map<IngredientInstanceWrapper<T, M>, V>>
        extends IngredientMapAdapter<T, M, V> {

    private final C collection;
    
    private IngredientSet<T, M> cachedKeySet = null;
    private int cachedSize = -1;

    protected IngredientMapWrappedAdapter(IngredientComponent<T, M> component, C collection) {
        super(component);
        this.collection = collection;
    }

    protected C getCollection() {
        return this.collection;
    }

    protected IngredientInstanceWrapper<T, M> wrap(T instance) {
        return getComponent().wrap(instance);
    }

    @Override
    public void clear() {
        this.collection.clear();
        invalidateCache();
    }

    @Nullable
    @Override
    public V put(T key, V value) {  
        V result = this.collection.put(wrap(key), value);  
        
        if (result == null) {  
            invalidateCache();  
        }  
        return result;  
    }

    @Nullable
    @Override
    public V remove(T key)  {  
        V result = this.collection.remove(wrap(key));  
        
        if (result != null) {  
            invalidateCache();  
        }  
        return result;  
    }

    @Override
    public int size() {
        return this.collection.size();
    }

    @Override
    public boolean containsValue(V value) {
        return this.collection.containsValue(value);
    }

    @Nullable
    @Override
    public V get(T key) {
        return this.collection.get(wrap(key));
    }

    @Override
    public IngredientSet<T, M> keySet()  {  
        int currentSize = this.collection.size();  
        if (cachedKeySet != null && cachedSize == currentSize) {  
            return cachedKeySet;  
        }  
          
        cachedKeySet = new IngredientHashSet<>(this.getComponent(), Sets.newHashSet(this.collection.keySet()));  
        cachedSize = currentSize;  
        return cachedKeySet;  
    }

    @Override
    public Collection<V> values() {
        return this.collection.values();
    }

    @Override
    public Iterator<Map.Entry<T, V>> iterator() {
        return Iterators.transform(this.collection.keySet().iterator(),
                key -> new AbstractMap.SimpleEntry<>(key.getInstance(), this.collection.get(key)));
    }
    
    private void invalidateCache() {  
        cachedKeySet = null;  
        cachedSize = -1;  
    }

}
