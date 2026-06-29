package kg.biamino.projects.utils;



public class ValidationInput {

    public static  <T> void nullChecker(T trainer, String message) {
        if(trainer == null) {
            throw new IllegalArgumentException("Entity named does not exist"+ message);
        }
    }

    public static void integerChecker(long integer, String message) {
        if(integer < 1) {
            throw new IllegalArgumentException("Entity's value should be greater than 1"+ message);
        }
    }
    public static void stringChecker(String string, String message) {
        if(string == null || string.isBlank()) {
            throw new IllegalArgumentException("The field cant be blank "+ message);
        }
    }
}
