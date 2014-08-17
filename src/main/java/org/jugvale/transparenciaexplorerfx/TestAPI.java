/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jugvale.transparenciaexplorerfx;

import com.sensedia.transparencia.client.core.TransparenciaClient;
import com.sensedia.transparencia.client.ex.RestException;

/**
 *
 * @author william
 */
public class TestAPI {

    // testes para rodar e pegar metadados...
    public static void main(String args[]) throws RestException {
        TransparenciaClient cli = new TransparenciaClient("lGkuSLiphXo7");

        cli.getEstados().forEach(e -> System.out.println(e.getNome()));
        cli.getCargos().forEach(c -> System.out.println(c.getCargoId() + " - " + c.getNome()));
    }

}
