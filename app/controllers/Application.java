package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import play.cache.Cache;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.Util;

import com.google.gson.Gson;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.FileInputStream;

import static utils.Util.*;

public class Application extends Controller {

	public static final Zemberek z = new Zemberek(new TurkiyeTurkcesi());

	public static void index() {
		Cache.delete(session.getId());
		render();
	}

	public static void kullanicidanAl(@Required String text) {

		if (validation.hasErrors()) {
			flash.error("Text girmeniz gerekiyor!");
			index();
		}

		Cache.set(session.getId(), text, "3h");
		sayiBul();
	}

	@Util
	public static String getText() {
		String text = Cache.get(session.getId(), String.class);
		if (controlText(text))
			return text;
		return null;
	}

	@Util
	public static boolean controlText(String text) {
		if (text == null) {
			flash.error("Text girmeniz gerekiyor!");
			index();
			return false;
		} else
			return true;
	}

	public static void dosyadanOku(@Required File dosya) throws Exception {

		if (validation.hasErrors()) {
			flash.error("Dosya upload etmediniz!");
			index();
		}

		InputStream inputStream = new FileInputStream(dosya);
		Parser parser = new AutoDetectParser();
		ContentHandler contenthandler = new BodyContentHandler(
				Integer.MAX_VALUE);
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		parser.parse(inputStream, contenthandler, metadata, context);
		String text = contenthandler.toString();

		Cache.set(session.getId(), text, "3h");

		sayiBul();
	}

	public static void heceler() {

		String k = getText();
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

	public static void kelimeAyristir() {

		String k = getText();
		k = utils.Util.duzenle(k);
		String[] dizi = k.split(" ");
		List<List<String[]>> ayrisimlar = new ArrayList<List<String[]>>();
		for (String kelime : dizi) {
			ayrisimlar.add(z.kelimeAyristir(kelime));
		}
		render("Application/kelimeAyristir.html", ayrisimlar);
	}

	public static void kelimeCozumle() {

		String k = getText();
		k = utils.Util.duzenle(k);
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

	public static void asciDonustur() {

		String k = getText();
		String l = z.asciiyeDonustur(k);
		render("Application/asciDonustur.html", l);
	}

	public static void oneriler() {

		String k = getText();
		k = utils.Util.duzenle(k);
		String[] dizi = k.split(" ");
		List<String[]> gecis = new ArrayList<String[]>();

		for (String oneri : dizi) {
			gecis.add(z.oner(oneri));
		}
		render("Application/oneriler.html", gecis);
	}

	public static void kelimeDenetle() {

		String k = getText();
		k = utils.Util.duzenle(k);
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

	public static void kokBul() {

		String k = getText();
		k = utils.Util.duzenle(k);
		String[] dizi = k.split(" ");
		KokBulucu kok = z.kokBulucu();
		List<String[]> gecis = new ArrayList<String[]>();

		for (String kokk : dizi) {
			gecis.add(kok.stringKokBul(kokk));
		}
		render("Application/kokBul.html", gecis);
	}

	public static void sayiBul() {

		String k = getText();
		k = utils.Util.duzenle(k);
		String[] dizi = k.split(" ");
		KokBulucu kok = z.kokBulucu();
		String[] kokler = null;
		List<String> gecis = new ArrayList<String>();

		for (int i = 0; i < dizi.length; i++) {

			kokler = kok.stringKokBul(dizi[i]);
			if (kokler.length != 0) {
				try {
					if (!gozArdiEt(kokler[0]))
						gecis.add(kokler[0]);

				} catch (Exception e) {
					Logger.error(e, "bir hata oluştu");
				}
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
