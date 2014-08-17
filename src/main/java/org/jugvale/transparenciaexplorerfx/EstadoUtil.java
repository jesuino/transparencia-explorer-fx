/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jugvale.transparenciaexplorerfx;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilidades para lidar com o estado
 *
 * @author william
 */
public class EstadoUtil {

    // TODO: Mapa sigla/nome
    private static final String IMG_BASE_URL = "/imagens/estados/%s.gif";
    private static final Map<String, String> estados;

    static {
        estados = new HashMap<>();
        estados.put("AC", "Acre");
        estados.put("AL", "Alagoas");
        estados.put("AP", "Amapá");
        estados.put("AM", "Amazonas");
        estados.put("BA", "Bahia ");
        estados.put("CE", "Ceará");
        estados.put("DF", "Distrito Federal ");
        estados.put("ES", "Espírito Santo");
        estados.put("GO", "Goiás");
        estados.put("MA", "Maranhão");
        estados.put("MT", "Mato Grosso");
        estados.put("MS", "Mato Grosso do Sul");
        estados.put("MG", "Minas Gerais");
        estados.put("PA", "Pará");
        estados.put("PB", "Paraíba");
        estados.put("PR", "Paraná");
        estados.put("PE", "Pernambuco");
        estados.put("PI", "Piauí");
        estados.put("RJ", "Rio de Janeiro");
        estados.put("RN", "Rio Grande do Norte");
        estados.put("RS", "Rio Grande do Sul");
        estados.put("RO", "Rondônia");
        estados.put("RR", "Roraima");
        estados.put("SC", "Santa Catarina");
        estados.put("SP", "São Paulo");
        estados.put("SE", "Sergipe");
        estados.put("TO", "Tocantins");
    }

    public static String urlBandeira(String sigla) {
        return String.format(IMG_BASE_URL, sigla.toLowerCase());
    }

    public static String nome(String sigla) {
        return estados.get(sigla.toUpperCase());        
    }
    
}