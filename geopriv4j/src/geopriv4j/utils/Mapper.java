package geopriv4j.utils;

/*
 * This is used to store the open Street Map data 
 */

public class Mapper {
    public String id;
    public LatLng loc;

    public Mapper(String id, LatLng loc) {
        super();
        this.id = id;
        this.loc = loc;
    }
    public Mapper() {

    }
    @Override
    public String toString() {
        return "map [id=" + id + ", loc=" + loc + "]";
    }
}

