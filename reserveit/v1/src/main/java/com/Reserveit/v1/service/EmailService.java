package com.Reserveit.v1.service;

import com.Reserveit.v1.entity.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public boolean sendAppointmentConfirmation(Appointment appointment) {
        return sendAppointmentMail(
                appointment,
                "ReserveIt - Appointment Confirmed",
                "Your appointment has been successfully booked."
        );
    }

    public boolean sendAppointmentReminder(Appointment appointment) {
        return sendAppointmentMail(
                appointment,
                "ReserveIt - Appointment Reminder",
                "This is a reminder for your upcoming appointment."
        );
    }

    private boolean sendAppointmentMail(Appointment appointment, String subject, String intro) {
        String recipient = appointment.getPatient().getUser().getEmail();

        StringBuilder body = new StringBuilder();
        body.append("Hello ")
                .append(appointment.getPatient().getUser().getFullName())
                .append(",\n\n")
                .append(intro)
                .append("\n\n")
                .append("Appointment Details\n")
                .append("-------------------\n")
                .append("Appointment ID: ").append(appointment.getId()).append("\n")
                .append("Patient: ").append(appointment.getPatient().getUser().getFullName()).append("\n")
                .append("Doctor: ").append(appointment.getDoctor().getUser().getFullName()).append("\n")
                .append("Clinic: ").append(appointment.getClinic().getName()).append("\n")
                .append("Date: ").append(appointment.getAppointmentDate()).append("\n")
                .append("Time: ").append(appointment.getStartTime())
                .append(" - ").append(appointment.getEndTime()).append("\n")
                .append("Status: ").append(appointment.getStatus()).append("\n");

        if (appointment.getService() != null) {
            body.append("Service: ").append(appointment.getService().getName()).append("\n");
        }

        if (appointment.getNotes() != null && !appointment.getNotes().isBlank()) {
            body.append("Notes: ").append(appointment.getNotes()).append("\n");
        }

        body.append("\nPlease keep this email for your records.\n\n")
                .append("Regards,\nReserveIt Team");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body.toString());

        try {
            mailSender.send(message);
            log.info("Appointment email sent to {} for appointment {}", recipient, appointment.getId());
            return true;
        } catch (Exception ex) {
            // Do not undo a successful appointment booking just because SMTP is temporarily unavailable.
            log.error("Could not send appointment email for appointment {} to {}", appointment.getId(), recipient, ex);
            return false;
        }
    }
}
