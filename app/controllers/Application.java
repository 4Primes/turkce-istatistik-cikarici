package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.zemberek.erisim.Zemberek;
import net.zemberek.islemler.KokBulucu;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Controller;

import com.google.gson.Gson;

public class Application extends Controller {

	public static final Zemberek z = new Zemberek(new TurkiyeTurkcesi());

	public static void index() {

		render();
	}

	public static void kullanicidanAl(@Required String text) {

		if (validation.hasErrors()) {
			flash.error("Text girmeniz gerekiyor!");
			index();
		}
		render("Application/kullanicidanAl.html", text);
	}

	public static void dosyadanOku(@Required File dosyaadı) {

		if (validation.hasErrors()) {
			flash.error("Dosya upload etmediniz!");
			index();
		}
		String a = "";
		try {
			BufferedReader oku = new BufferedReader(new FileReader(dosyaadı));

			while (oku.ready()) {
				a += oku.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("Application/dosyadanOku.html", a);
	}

	public static String duzenle(String k) {

		k = k.replaceAll("[^a-zA-ZğşıçüöĞÖÇŞÜİ]", " ").replaceAll("\\s+", " ");
		;
		return k;
	}

	public static void heceler(String k) {

		k = duzenle(k);
		String dizi[] = k.split(" ");
		List<String[]> hecelenmis = new ArrayList<String[]>();
		for (String kelime : dizi) {
			try {
				if (z.hecele(kelime) != null)
					hecelenmis.add(z.hecele(kelime));
			} catch (Exception e) {
				Logger.error(e, "bir hata oluştu");
			}
		}
		render("Application/heceler.html", hecelenmis);
	}

	public static void kelimeAyristir(String k) {

		k = duzenle(k);
		String[] dizi = k.split(" ");
		List<List<String[]>> ayrisimlar = new ArrayList<List<String[]>>();
		for (String kelime : dizi) {
			ayrisimlar.add(z.kelimeAyristir(kelime));
		}
		render("Application/kelimeAyristir.html", ayrisimlar);
	}

	public static void kelimeCozumle(String k) {

		k = duzenle(k);
		String dizi[] = k.split(" ");
		List<List<Kelime>> gecis = new ArrayList<List<Kelime>>();
		Kelime[] cozumler = null;
		for (String kel : dizi) {
			cozumler = z.kelimeCozumle(kel);
			List<Kelime> cozum = Arrays.asList(cozumler);
			gecis.add(cozum);
		}
		render("Application/kelimeCozumle.html", gecis);
	}

	public static void asciDonustur(String k) {

		String l = z.asciiyeDonustur(k);
		render("Application/asciDonustur.html", l);
	}

	public static void oneriler(String k) {

		k = duzenle(k);
		String[] dizi = k.split(" ");
		List<String[]> gecis = new ArrayList<String[]>();

		for (String oneri : dizi) {
			gecis.add(z.oner(oneri));
		}
		render("Application/oneriler.html", gecis);
	}

	public static void kelimeDenetle(String k) {

		k = duzenle(k);
		String dizi[] = null;
		dizi = k.split(" ");
		int a = 0;
		String l = "";
		String denetle[] = new String[dizi.length];
		while (a < dizi.length) {
			if (z.kelimeDenetle(dizi[a]))
				l = "Kelime doğru yazilmis";
			else
				l = "Kelime yanlış yazilmis";
			denetle[a] = l;
			a++;
		}

		render("Application/kelimeDenetle.html", denetle);
	}

	public static void kokBul(String k) {

		k = duzenle(k);
		String[] dizi = k.split(" ");
		KokBulucu kok = z.kokBulucu();
		List<String[]> gecis = new ArrayList<String[]>();

		for (String kokk : dizi) {
			gecis.add(kok.stringKokBul(kokk));
		}
		render("Application/kokBul.html", gecis);
	}

	public static void sayiBul(String k) {

		k = duzenle(k);
		String[] dizi = k.split(" ");
		KokBulucu kok = z.kokBulucu();
		String[] kokler = null;
		List<String> gecis = new ArrayList();

		for (int i = 0; i < dizi.length; i++) {
			kokler = kok.stringKokBul(dizi[i]);
			try {
				if (!"".equals(kokler[0]))
					gecis.add(kokler[0]);
			} catch (Exception e) {
				Logger.error(e, "bir hata oluştu");
			}
		}
		Map<String, Integer> kelimeSayi = new HashMap<String, Integer>();
		for (String kelime : gecis) {

			if (kelimeSayi.get(kelime) == null) {
				kelimeSayi.put(kelime, 1);
			} else {
				kelimeSayi.put(kelime, kelimeSayi.get(kelime) + 1);
			}

		}

		TreeMap treeMap = new TreeMap(new MyComparator(kelimeSayi));
		treeMap.putAll(kelimeSayi);
		kelimeSayi = treeMap;

		Set<String> keys = kelimeSayi.keySet();
		Collection<Integer> values = kelimeSayi.values();
		String kelimeDizisi = new Gson().toJson(keys);
		String sayiDizisi = new Gson().toJson(values);

		render("Application/sayiBul.html", kelimeDizisi, sayiDizisi);

	}
}
