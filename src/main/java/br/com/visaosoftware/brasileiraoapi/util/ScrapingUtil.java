package br.com.visaosoftware.brasileiraoapi.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.visaosoftware.brasileiraoapi.dto.PartidaGoogleDTO;

@Service
public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	private static final String DIV_PARTIDA_ANDAMENTO = "div[class=imso_mh__lv-m-stts-cont]";
	private static final String DIV_PARTIDA_ENCERRADA = "span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]";
	
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
	private static final String PENALTIS = "Pênaltis";
	
	private static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]";
	
	private static final String ITEM_GOL = "div[class=imso_gs__gs-r]";
	private static final String DIV_GOLS_EQUIPE_CASA = "div[class=imso_gs__tgs imso_gs__left-team]";
	private static final String DIV_GOLS_EQUIPE_VISITANTE = "div[class=imso_gs__tgs imso_gs__right-team]";
	
	private static final String DIV_PLACAR_EQUIPE_CASA= "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
	private static final String DIV_PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]";
	
	private static final String ITEM_LOGO = "img[class=imso_btl__mh-logo]";
	private static final String DIV_DADOS_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String DIV_DADOS_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";


	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();

		Document document = null;

		try {
			document = Jsoup.connect(url).get();

			String titulo = document.title();
			LOGGER.info("Titulo da pagina ->{}", titulo);

			StatusPartida statusPartida = obtemStatusDaPartida(document);
			partida.setStatusPartida(statusPartida.toString());
			
			LOGGER.info("Status da partida ->{}", statusPartida.toString());

			if (statusPartida != StatusPartida.PARTINA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				partida.setTempoPartida(tempoPartida);
				
				LOGGER.info("Tempo da partida ->{}", tempoPartida);
				
				Integer placarEquipeCasa = recuperaPlacarEquipe(document, DIV_PLACAR_EQUIPE_CASA);
				partida.setPlacarEquipeCasa(placarEquipeCasa);
				LOGGER.info("Placar equipe casa ->{}", placarEquipeCasa);
				
				Integer placarEquipeVisitante = recuperaPlacarEquipe(document, DIV_PLACAR_EQUIPE_VISITANTE);
				partida.setPlacarEquipeVisitante(placarEquipeVisitante);
				LOGGER.info("Placar equipe visitante ->{}", placarEquipeVisitante);
				
				
				String golsEquipeCasa = recuperaGolsEquipe(document, DIV_GOLS_EQUIPE_CASA);
				partida.setGolsEquipeCasa(golsEquipeCasa);
				LOGGER.info("Gols equipe casa ->{}", golsEquipeCasa);//
				
				String golsEquipeVisitante = recuperaGolsEquipe(document, DIV_GOLS_EQUIPE_VISITANTE);
				partida.setGolsEquipeVisitante(golsEquipeVisitante);
				LOGGER.info("Gols equipe Visitante ->{}", golsEquipeVisitante);
			}

			String nomeEquipeCasa = recuperaNomeEquipe(document, DIV_DADOS_EQUIPE_CASA);
			partida.setNomeEquipeCasa(nomeEquipeCasa);
			LOGGER.info("Equipe casa ->{}", nomeEquipeCasa);

			String nomeEquipeVisitante = recuperaNomeEquipe(document, DIV_DADOS_EQUIPE_VISITANTE);
			partida.setNomeEquipeVisitante(nomeEquipeVisitante);
			LOGGER.info("Equipe visitante ->{}", nomeEquipeVisitante);

			String urlLogoEquipeCasa = recuperaLogoEquipe(document, DIV_DADOS_EQUIPE_CASA);
			partida.setUrlLogoEquipeCasa(urlLogoEquipeCasa);
			LOGGER.info("URL logo equipe casa ->{}", urlLogoEquipeCasa);
			
			String urlLogoEquipeVisitante = recuperaLogoEquipe(document, DIV_DADOS_EQUIPE_VISITANTE);
			partida.setUrlLogoEquipeVisitante(urlLogoEquipeVisitante);
			LOGGER.info("URL logo equipe visitante ->{}", urlLogoEquipeVisitante);
			
			Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
			partida.setPlacarEstendidoEquipeCasa(placarEstendidoEquipeCasa);
			LOGGER.info("Placar estendido equipe CASA ->{}", placarEstendidoEquipeCasa);
			
			Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
			partida.setPlacarEstendidoEquipeVisitante(placarEstendidoEquipeVisitante);
			LOGGER.info("Placar estendido equipe VISITANTE ->{}", placarEstendidoEquipeVisitante);

		} catch (Exception ex) {
			LOGGER.error("ERRO AO CONECTAR NA PAGINA DO GOOGLE COM JSOUP ->{}", ex.getMessage());
		}
		return partida;
	}

	// Metodo que verifica e retorna o status da partida
	public StatusPartida obtemStatusDaPartida(Document document) {
		StatusPartida statusPartida = StatusPartida.PARTINA_NAO_INICIADA;

		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		if (!isTempoPartida) {
			String tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains(PENALTIS)) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
		}

		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
		if (!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}

		return statusPartida;
	}

	// Metodo que retorna o tempo da partida
	public String obtemTempoPartida(Document document) {
		String tempoPartida = "";
		// Jogo em andamento ou intervalo ou em penalidades
		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
		}
		// Jogo encerrado
		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ENCERRADA).first()
					.text();
		}
		// Formatando o temo
		if (tempoPartida.contains("'")) {
			tempoPartida = tempoPartida.replace("'", " min");
		}
		return tempoPartida;
	}

	public String recuperaNomeEquipe(Document document, String itemHtml) {
		String nomeEqupe = "";
		Element elemento = document.selectFirst(itemHtml);

		nomeEqupe = elemento.select("span").text();

		return nomeEqupe;
	}

	public String recuperaLogoEquipe(Document document, String itemHtml) {

		Element elemento = document.selectFirst(itemHtml);
		String urlLogoEquipe = "https://" + elemento.select(ITEM_LOGO).attr("src");

		return urlLogoEquipe;
	}
	
	public Integer recuperaPlacarEquipe(Document document, String itemHtml) {

		String placarEquipe = document.selectFirst(itemHtml).text();
		return formataPlacarStringInteger(placarEquipe);
	}
		
	public String recuperaGolsEquipe(Document document, String itemHtml) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select(itemHtml).select(ITEM_GOL);
		
		elementos.forEach(item -> {
			String infoGol = item.select(ITEM_GOL).text();
			golsEquipe.add(infoGol);
		});
		
		return String.join(", ", golsEquipe);
	}
	
	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select(DIV_PENALIDADES).isEmpty();
		
		if(!isPenalidades) {
			String penalidades = document.select(DIV_PENALIDADES).text();
			
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
	
	public String montaUrlGoogle(String equipeCasa, String equipeVisitante) {
		
		try {
			String nomeEquipecasa = equipeCasa.replace(" ", "+").replace("-", "+");
			String nomeEquipeVisitante = equipeVisitante.replace(" ", "+").replace("-", "+");
			return BASE_URL_GOOGLE + nomeEquipecasa+"+x+"+nomeEquipeVisitante+COMPLEMENTO_URL_GOOGLE;
		}catch (Exception e) {
			LOGGER.error("ERRO: {}", e.getMessage());
		}
		
		return null;
	}
}
