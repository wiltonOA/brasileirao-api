package br.com.visaosoftware.brasileiraoapi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.visaosoftware.brasileiraoapi.dto.PartidaGoogleDTO;


public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	
	private static final String BASE_URL_GOOGLE = "https://www.google.com.br/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE+"palmeiras+x+corinthians+08/08/2020"+COMPLEMENTO_URL_GOOGLE;
		
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
			
		}catch(Exception ex) {
			LOGGER.error("ERRO AO CONECTAR NA PAGINA DO GOOGLE COM JSOUP ->{}", ex.getMessage());
		}
		
		return partida;
	}

}
