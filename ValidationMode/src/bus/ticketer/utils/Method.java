package bus.ticketer.utils;

public enum Method {
    POST("POST"),
    GET("GET"),
    PUT("PUT");

    private final String name;       

    private Method(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
       return name;
    }

}