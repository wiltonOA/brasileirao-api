package br.com.visaosoftware.brasileiraoapi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.visaosoftware.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";

	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "guadalajara+x+pumas+01/03/2021" + COMPLEMENTO_URL_GOOGLE;

		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obtemInformacoesPartida(url);

	}

	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();

		Document document = null;

		try {
			document = Jsoup.connect(url).get();

			String titulo = document.title();
			LOGGER.info("Titulo da pagina ->{}", titulo);

			StatusPartida statusPartida = obtemStatusDaPartida(document);
			LOGGER.info("Status da partida ->{}",statusPartida.toString());
			
			String tempoPartida = obtemTempoPartida(document);
			LOGGER.info("Tempo da partida ->{}",tempoPartida);

		} catch (Exception ex) {
			LOGGER.error("ERRO AO CONECTAR NA PAGINA DO GOOGLE COM JSOUP ->{}", ex.getMessage());
		}
		return partida;
	}

	// Metodo que verifica e retorna o status da partida
	public StatusPartida obtemStatusDaPartida(Document document) {
		StatusPartida statusPartida = StatusPartida.PARTINA_NAO_INICIADA;

		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if (!isTempoPartida) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
		}

		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if (!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		

		return statusPartida;
	}

	// Metodo que retorna o tempo da partida
	public String obtemTempoPartida(Document document) {
		String tempoPartida = "";
		//Jogo em andamento ou intervalo ou em penalidades
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}
		//Jogo encerrado
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}
		//Formatando o temo
		if(tempoPartida.contains("'")) {
			tempoPartida = tempoPartida.replace("'", " min");
		}
		return tempoPartida;
	}

}
