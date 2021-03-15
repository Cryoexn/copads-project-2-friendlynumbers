import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Tuple;
import edu.rit.pj2.Vbl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapVbl<K,V> extends Tuple implements Vbl {

    public Map<K,V> item;

    public MapVbl() {
        item = new LinkedHashMap<K,V>();
    }

    public MapVbl(Map<K,V> inItem) {
        item = inItem;
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        MapVbl<K,V> vbl = (MapVbl<K,V>) super.clone();
        vbl.set(this);
        return vbl;
    }

    @Override
    public void writeOut(OutStream outStream) throws IOException {

    }

    @Override
    public void readIn(InStream inStream) throws IOException {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(Vbl vbl) {

        Map<K,V> copy = new LinkedHashMap<K,V>();
        for(Map.Entry<K,V> entry : ((MapVbl<K,V>)vbl).item.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        this.item = copy;
    }

    public void reduce(Map<K,V> map) {
        int len = Math.min(item.size(), map.size());
        for(int i = 0; i < len; i++) {
            reduce(i, map);
        }
    }

    public void reduce(int i, Map<K,V> map) {
        ArrayList<K> keys = new ArrayList<K>(map.keySet());
        ArrayList<V> values = new ArrayList<V>(map.values());

        if(this.item.containsKey(keys.get(i))) {
            this.item.put(keys.get(i), concatArrays(this.item.get(keys.get(i)), values.get(i)));
        } else {
            this.item.put(keys.get(i), values.get(i));
        }
    }

    private V concatArrays(V a1, V a2) {
        int [] val = new int[((int[])a1).length + ((int[])a2).length];

        System.arraycopy(a1, 0, val, 0, ((int[])a1).length);
        System.arraycopy(a2, 0, val, ((int[])a1).length, ((int[])a2).length);

        return (V)val;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reduce(Vbl vbl) {
        reduce(((MapVbl<K,V>)vbl).item);
    }
}
