package com.unrn.gpiv.service;

import com.unrn.gpiv.model.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarNotificacion(String destinatarioEmail, String emisorNombre, String asunto) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatarioEmail);
        email.setSubject("Nuevo mensaje en SGPIV: " + asunto);
        email.setText("Hola,\n\nHas recibido un mensaje oficial de: " + emisorNombre +
                "\n\nPor favor, ingresá al sistema del Parque Industrial para leerlo.\n" +
                "Este es un aviso automático, no respondas a este mail.");

        mailSender.send(email);
    }

    public void notificarNuevaSolicitud(Empresa empresa) {
        // Por ahora, un log o un sistema interno de mensajes
        System.out.println("NOTIFICACIÓN: Nueva solicitud recibida de: " + empresa.getRazonSocial());
        // implementar invio de email aca
    }

}