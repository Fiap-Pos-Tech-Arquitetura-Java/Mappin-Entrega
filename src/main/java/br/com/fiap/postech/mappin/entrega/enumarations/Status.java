package br.com.fiap.postech.mappin.entrega.enumarations;

import java.util.Arrays;

public enum Status {
    AGUARDANDO_ENTREGA, PREPARANDO_ENVIO, ENTREGUE;

    public static Boolean contains(String status) {
        return Arrays.stream(Status.values()).anyMatch(s -> {
                return s.name().equals(status);
            }
        );
    }
}
