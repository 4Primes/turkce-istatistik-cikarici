package utils;

public class Util {

    private static final String[] GOZ_ARDI_LISTESI = { "sayın", "bay", "bayan", "bir", "iki", "ile", "veya", "gibi", "daha", "al", "ver", "gel",
            "git", "için", "ama", "fakat", "ise", "ancak", "her", "hiç", "hep", "var", "yok" };

    public static String duzenle(String k) {

        k = k.replaceAll("[^a-zA-ZğşıçüöĞÖÇŞÜİ]", " ").replaceAll("\\s+", " ");
        return k;
    }

    public static boolean gozArdiEt(String kelime) {

        if (kelime == null || kelime.length() < 3)
            return true;
        for (String eleman : GOZ_ARDI_LISTESI) {
            if (eleman.equals(kelime))
                return true;
        }

        return false;
    }

}
