package se.kry.codetest;

import org.apache.commons.validator.routines.UrlValidator;

public class Util {
    public static String getQueryBuilder(String url, String name, String status){
        return "insert into service(url, status, name) values('" + url + "', '" + status + "', '" + name + "');";
    }
    public static String deleteQueryBuilder(String id){
        return "delete from service where rowid = '" + id + "';";
    }

    public static String updateQueryBuilder(String url, String name, String status){
        return "update service set status = '" + status + "' where url = '" + url + "' and name = '" + name + "';";
    }

    public static boolean isValidUrl(String url){
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(url)) {
            System.out.println("URL is valid");
            return true;
        } else {
            System.out.println("URL is invalid");
            return false;
        }
    }
}
