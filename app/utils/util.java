package utils;

public class util {
	
	public static String duzenle(String k) {

		k = k.replaceAll("[^a-zA-ZğşıçüöĞÖÇŞÜİ]", " ").replaceAll("\\s+", " ");
		return k;
	}

}
