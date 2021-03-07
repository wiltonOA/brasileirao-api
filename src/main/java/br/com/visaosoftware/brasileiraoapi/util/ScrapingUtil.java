package br.com.visaosoftware.brasileiraoapi.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.visaosoftware.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";

	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "palmeiras+x+corinnthians+08/08/2020" + COMPLEMENTO_URL_GOOGLE;

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
			LOGGER.info("Status da partida ->{}", statusPartida.toString());

			if (statusPartida != StatusPartida.PARTINA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				LOGGER.info("Tempo da partida ->{}", tempoPartida);
				
				Integer placarEquipeCasa = recuperaPlacarEquipeCasa(document);
				LOGGER.info("Placar equipe casa ->{}", placarEquipeCasa);
				
				Integer placarEquipeVisitante = recuperaPlacarEquipeVisitante(document);
				LOGGER.info("Placar equipe visitante ->{}", placarEquipeVisitante);
				//recuperaGolsEquipeCasa
				String golsEquipeCasa = recuperaGolsEquipeCasa(document);
				LOGGER.info("Gols equipe casa ->{}", golsEquipeCasa);//
				
				String golsEquipeVisitante = recuperaGolsEquipeVisitante(document);
				LOGGER.info("Gols equipe Visitante ->{}", golsEquipeVisitante);
			}

			String nomeEquipeCasa = recuperaNomeEquipeCasa(document);
			LOGGER.info("Equipe casa ->{}", nomeEquipeCasa);

			String nomeEquipeVisitante = recuperaNomeEquipeVisitante(document);
			LOGGER.info("Equipe casa ->{}", nomeEquipeVisitante);

			String urlLogoEquipeCasa = recuperaLogoEquipeCasa(document);
			LOGGER.info("URL logo equipe casa ->{}", urlLogoEquipeCasa);
			
			String urlLogoEquipeVisitante = recuperaLogoEquipeVisitante(document);
			LOGGER.info("URL logo equipe visitante ->{}", urlLogoEquipeVisitante);
			
			Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
			LOGGER.info("Placar estendido equipe CASA ->{}", placarEstendidoEquipeCasa);
			
			Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
			LOGGER.info("Placar estendido equipe VISITANTE ->{}", placarEstendidoEquipeVisitante);

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
		// Jogo em andamento ou intervalo ou em penalidades
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}
		// Jogo encerrado
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first()
					.text();
		}
		// Formatando o temo
		if (tempoPartida.contains("'")) {
			tempoPartida = tempoPartida.replace("'", " min");
		}
		return tempoPartida;
	}

	public String recuperaNomeEquipeCasa(Document document) {
		String nomeEqupeCasa = "";
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");

		nomeEqupeCasa = elemento.select("span").text();

		return nomeEqupeCasa;
	}

	public String recuperaNomeEquipeVisitante(Document document) {
		String nomeEqupeVisitante = "";
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");

		nomeEqupeVisitante = elemento.select("span").text();

		return nomeEqupeVisitante;
	}

	public String recuperaLogoEquipeCasa(Document document) {

		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogoEquipeCasa = "https://" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");

		return urlLogoEquipeCasa;
	}

	public String recuperaLogoEquipeVisitante(Document document) {

		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogoEquipeVisitante = "https://" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");

		return urlLogoEquipeVisitante;
	}//
	
	public Integer recuperaPlacarEquipeCasa(Document document) {

		String placarEquipeCasa = document.selectFirst("div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarStringInteger(placarEquipeCasa);
	}
	
	public Integer recuperaPlacarEquipeVisitante(Document document) {

		String placarEquipeVisitante = document.selectFirst("div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarStringInteger(placarEquipeVisitante);
	}
	
	public String recuperaGolsEquipeCasa(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__left-team]").select("div[class=imso_gs__gs-r]");
		
		for(Element e : elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}

		return String.join(", ", golsEquipe);
	}//
	
	public String recuperaGolsEquipeVisitante(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__right-team]").select("div[class=imso_gs__gs-r]");
		
		elementos.forEach(item -> {
			String infoGol = item.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		});
		
		return String.join(", ", golsEquipe);
	}
	
	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select("div[class=imso_mh_s__psn-sc]").isEmpty();
		
		if(!isPenalidades) {
			String penalidades = document.select("div[class=imso_mh_s__psn-sc]").text();
			
			String penalidadesCompleta = penalidades.substring(0, 5).replace(" ", "");
			String[] divisao = penalidadesCompleta.split("-");
			
			return tipoEquipe.equals(CASA) ? formataPlacarStringInteger(divisao[0]) : formataPlacarStringInteger(divisao[1]);
			
		}
		return null;
	}
	
	public Integer formataPlacarStringInteger(String placar) {
		Integer valor;
		try {
			valor = Integer.parseInt(placar);
		}catch (Exception e) {
			return valor = 0;
		}
		return valor;
	}

}
