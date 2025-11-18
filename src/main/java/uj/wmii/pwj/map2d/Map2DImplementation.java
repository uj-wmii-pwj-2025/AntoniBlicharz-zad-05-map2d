package uj.wmii.pwj.map2d;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class Map2DImplementation<R,C,V> implements Map2D<R, C, V> {

    Map<R, Map<C, V>> map = new HashMap<>();

    <K1, K2, V> Map<K1, Map<K2, V>> reverseKeys(Map<K2, Map<K1, V>> map) {

        Map<K1, Map<K2, V>> reversed = new HashMap<>();
        for (K2 rowKey : map.keySet())
            for (K1 columnKey : map.get(rowKey).keySet()){
                V value = map.get(rowKey).get(columnKey);
                if (!reversed.containsKey(columnKey))
                    reversed.put(columnKey,new HashMap<>());

                reversed.get(columnKey).put(rowKey, value);
            }

        return reversed;
    }


    public V put (R rowKey, C columnKey, V value){
        if (rowKey == null || columnKey == null){
            throw new NullPointerException("rowKey and columnKey can't be null");
        }

        V prevValue = null;
        if (map.containsKey(rowKey)){
            if(map.get(rowKey).containsKey(columnKey))
                prevValue = map.get(rowKey).get(columnKey);

            map.get(rowKey).put(columnKey, value);
            }
        else{
            map.put(rowKey, new HashMap<>());
            map.get(rowKey).put(columnKey, value);}
        
        
        return prevValue;
    }

    public V get (R rowKey, C columnKey){

        if (map.containsKey(rowKey) && map.get(rowKey).containsKey(columnKey))
            return map.get(rowKey).get(columnKey);
        else
            return null;
    }

    public V getOrDefault(R rowKey, C columnKey, V defaultValue){
        if (map.get(rowKey) == null || map.get(rowKey).get(columnKey) == null)
            return defaultValue;
        else
            return map.get(rowKey).get(columnKey);
    }

    public V remove(R rowKey, C columnKey){

        V prevValue = null;
        if (map.containsKey(rowKey)){
            if(map.get(rowKey).containsKey(columnKey)){
                prevValue = map.get(rowKey).get(columnKey);

                map.get(rowKey).remove(columnKey);
                if (map.get(rowKey).isEmpty())
                    map.remove(rowKey);}}

        return prevValue;
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public boolean nonEmpty(){
        return !isEmpty();
    }

    public int size(){
        int size = 0;

        for (R rowKey : map.keySet()){
            size += map.get(rowKey).size();
        }

        return size;
    }

    public void clear() {
        map.clear();
    }

    public Map<C, V> rowView(R rowKey){
        return map.get(rowKey);
    }

    public Map<R, V> columnView(C columnKey){
        return reverseKeys(map).get(columnKey);
    }

    public boolean containsValue(V value) {
        for (R rowKey : map.keySet())
            for (C columnKey : map.get(rowKey).keySet())
                if (map.get(rowKey).get(columnKey).equals(value))
                    return true;
        return false;
    }

    public boolean containsKey(R rowKey, C columnKey){
        if (map.containsKey(rowKey) && map.get(rowKey).containsKey(columnKey))
            return map.get(rowKey).get(columnKey) != null;
        else
            return false;
    }
    
    public boolean containsRow(R rowKey){
        return map.containsKey(rowKey);
    }
    
    public boolean containsColumn(C columnKey){
        return reverseKeys(map).containsKey(columnKey);
    }

    public Map<R, Map<C,V>> rowMapView(){
        Map<R, Map<C, V>> copy = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : map.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    public Map<C, Map<R,V>> columnMapView(){
        return reverseKeys(map);
    }

    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey){
        if (map.containsKey(rowKey))
            target.putAll(map.get(rowKey));
        return this;
    }

    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey){
        if (reverseKeys(map).containsKey(columnKey))
            target.putAll(reverseKeys(map).get(columnKey));
        return this;
    }

    public Map2D<R, C, V>  putAll(Map2D<? extends R, ? extends C, ? extends V> source){
        Map<? extends R, ? extends Map<? extends C, ? extends V>> helperMap = source.rowMapView();
        for (R rowKey : helperMap.keySet())
            for (C columnKey : helperMap.get(rowKey).keySet()){
                V value = helperMap.get(rowKey).get(columnKey);
                this.put(rowKey, columnKey, value);}

        return this;
    }

    public Map2D<R, C, V>  putAllToRow(Map<? extends C, ? extends V> source, R rowKey){
        if (!map.containsKey(rowKey))
            map.put(rowKey, new HashMap<>());

        map.get(rowKey).putAll(source);

        return this;
    }

    public Map2D<R, C, V>  putAllToColumn(Map<? extends R, ? extends V> source, C columnKey){
        Map<C, Map<R, V>> reversed = reverseKeys(map);
        if (!reversed.containsKey(columnKey))
            reversed.put(columnKey, new HashMap<>());

        reversed.get(columnKey).putAll(source);
        map = reverseKeys(reversed);
        return this;
    }

    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
            Function<? super R, ? extends R2> rowFunction,
            Function<? super C, ? extends C2> columnFunction,
            Function<? super V, ? extends V2> valueFunction){

        Map2D<R2,C2,V2> convertedMap = new Map2DImplementation<>();
        for (R rowKey : map.keySet()) {
            R2 convertedRow = rowFunction.apply(rowKey);
            for (C columnKey : map.get(rowKey).keySet()){
                V value = map.get(rowKey).get(columnKey);
                C2 convertedColumn = columnFunction.apply(columnKey);
                V2 convertedValue = valueFunction.apply(value);
            convertedMap.put(convertedRow, convertedColumn, convertedValue);
        }}

    return convertedMap;
    }


}
