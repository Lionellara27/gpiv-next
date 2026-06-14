package com.unrn.gpiv.messaging.service;

import com.unrn.gpiv.messaging.model.Mensaje;
import com.unrn.gpiv.messaging.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Mensaje enviarMensajeNuevo(Mensaje mensaje) {
        // 1. Guardar en Postgres
        Mensaje guardado = mensajeRepository.save(mensaje);

        // 2. Avisar por Gmail (Si falla el mail, el mensaje igual se guarda)
        try {
            emailService.enviarNotificacion(
                    mensaje.getReceptor().getEmail(),
                    mensaje.getEmisor().getUsername(),
                    mensaje.getAsunto()
            );
        } catch (Exception e) {
            System.err.println("No se pudo enviar el aviso por mail: " + e.getMessage());
        }

        return guardado;
    }
}