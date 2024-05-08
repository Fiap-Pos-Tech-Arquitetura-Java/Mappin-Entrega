package br.com.fiap.postech.mappin.entrega;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

@SpringBootApplication
@EnableFeignClients
@Generated
public class MappinEntregaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MappinEntregaApplication.class, args);
    }

}
